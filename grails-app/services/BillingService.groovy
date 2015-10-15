import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.context.i18n.LocaleContextHolder as LCH
import org.apache.poi.ss.usermodel.*
import org.cyberneko.html.parsers.*
class BillingService {
	def messageSource
  def mailerService
  def smsService

  //static datasource = 'admin'
	void createTripFromMbox(oMbox,oMboxrec1,oContext,lUId=0l,iClose=0,lPayorderId=0,iPaymentStatus=0){

    setBookAndPaidStatus(oMbox,oMboxrec1,lPayorderId?true:false)

    if (!lPayorderId) {
      mailerService.mbox_bron_client(lUId?:0.toLong(),oMbox.home_id,oMbox.id,oMboxrec.id,oContext)
    } else {
      mailerService.mbox_bron_client_with_payment(lUId,oMbox) //клиенту
      mailerService.mbox_bron_owner_with_payment(oMbox.homeowner_cl_id,oMbox) //владельцу
      smsService.sendPaymentSMS(User.findByClient_id(oMbox.homeowner_cl_id),Home.get(oMbox.home_id)?.region_id) //владельцу
    }

    def oTrip = new Trip(oMbox,lPayorderId,iPaymentStatus)
    oTrip.save(flush:true)
    if (iClose) {
      closeAnotherMboxes(oMbox,lUId?:0)
    }
	}
  void closeAnotherMboxes(oMbox,lUId){
    for(mbox in Mbox.findAll("from Mbox where modstatus>0 and date_start=:d_st and date_end=:d_end and user_id=:u_id and id!=:mbox_id",
        [d_st:oMbox.date_start,d_end:oMbox.date_end,u_id:(lUId?:0),mbox_id:oMbox.id])){
      mbox.modstatus = -100
      mbox.controlstatus = -2
      mbox.save(flush:true)
    }
  }

  def createOrderFromOrder(oPayorder,oRequest){
    def oClient = Client.get(Home.get(oRequest?.home_id)?.client_id)
    if (!(oPayorder&&oClient)) {
      throw new Exception ('Not all data set')
    }
    def result = null
    Payorder.withTransaction { status ->
      try {
        synchronized(this) {
          if((result = Payorder.createOrder(oPayorder,oRequest,oClient,messageSource))){
            if(!Account.getInstanceForOwner(oClient.id))
              throw new Exception ('save error')
            status.flush()
          }
        }
      } catch(Exception e) {
        log.debug("BillingService:createOrderFromOrder:\n"+e.toString())
        status.setRollbackOnly()
        throw e
      }
    }
    return result
  }

	def createOrderFromMbox(oMbox,oRequest){
		def oClient = Client.get(oMbox?.homeowner_cl_id)
		def oReserve = Reserve.get(oClient?.reserve_id)
		if (!(oMbox&&oClient&&oReserve&&oRequest?.payway&&oRequest?.oferta)) {
			throw new Exception ('Not all data set')
		}
    def result = null
		Payorder.withTransaction { status ->
			try {
				def lPrice = getBronPrice(oMbox,oClient)
        synchronized(this) {
				  if((result = Payorder.createOrder(oMbox,oReserve,oRequest,lPrice))){
            if(!Account.getInstanceForOwner(oMbox.homeowner_cl_id))
              throw new Exception ('save error')
            status.flush()
          }
        }
			} catch(Exception e) {
				log.debug("InboxController:setMboxBron:\n"+e.toString())
				status.setRollbackOnly()
				throw e
			}
		}
    return result
	}

	Long calculateOrderDayPrice(oMbox,iType){
		def iDays = oMbox.date_end - oMbox.date_start
		if (Home.get(oMbox.home_id)?.is_pricebyday) {
			iDays++
		}
		def result = Math.round((oMbox.price_rub/(iDays?:1)) + oMbox.price_rub * (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100))
    def comission = computeOurComissionSumma(oMbox.price_rub + oMbox.price_rub * (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100),iType)
		return (result>comission?result:comission)
	}

  Long getBronPrice(oMbox){
    return getBronPrice(oMbox,Client.get(oMbox?.homeowner_cl_id))
  }
  Long getBronPrice(oMbox,oClient){
    def result = 0l
    switch(oClient?.reserve_id) {
      case 1:
        result = calculateOrderDayPrice(oMbox,1)
        break
      case 3:
        result = Math.round((oMbox.price_rub + oMbox.price_rub * (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100))*(Tools.getIntVal(ConfigurationHolder.config.clientPric111e.modifier,10) / 100))
        break
      default:
        result = Math.round(oMbox.price_rub + oMbox.price_rub * (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100))
        break
    }
    return result
  }

  def updatePayorderPlatInfo(oPayorder,oRequest){
    oPayorder.plat_name = oRequest.payer?:''
    oPayorder.plat_address = oRequest.payeraddress?:''
    oPayorder.plat_inn = oRequest.payerinn?:''
    oPayorder.plat_okpo = oRequest.payerokpo?:''
    oPayorder.plat_bik = oRequest.payerbik?:''
    oPayorder.plat_bank = oRequest.payerbank?:''
    oPayorder.plat_corchet = oRequest.payercorchet?:''
    oPayorder.plat_schet = oRequest.payeraccount?:''
    return oPayorder.save(flush:true)
  }

  void doTransaction (lId){
    Paytrans.withTransaction { status ->
      def oPaytrans = Paytrans.lock(lId)
      try {
        if (!oPaytrans)
          throw new Exception ('Transaction '+lId+' not exist')
        if (oPaytrans.modstatus == 1)
          throw new Exception ('Transaction already done')
        switch(oPaytrans.paytype_id) {
          case 1:
            transactionType1Handler(oPaytrans)
            break
          case 2:
            transactionType2Handler(oPaytrans)
            break
          case 8:
            transactionType8Handler(oPaytrans)
            break
          case 10:
            transactionType10Handler(oPaytrans)
            break
          case 11:
            transactionType11Handler(oPaytrans)
            break
          case 14:
            transactionType14Handler(oPaytrans)
            break
          case 15:
            transactionType15Handler(oPaytrans)
            break
          case 16:
            transactionType16Handler(oPaytrans)
            break
          case 17:
            transactionType17Handler(oPaytrans)
            break
          case 19:
            transactionType19Handler(oPaytrans)
            break
          case 20:
            transactionType20Handler(oPaytrans)
            break
          case 21:
            transactionType21Handler(oPaytrans)
            break
          case 22:
            transactionType22Handler(oPaytrans)
            break
          case 23:
            transactionType23Handler(oPaytrans)
            break
          default:
            throw new Exception ('Where is no handler for transaction type '+oPaytrans.paytype_id)
            break
        }
        oPaytrans.modstatus = 1
        oPaytrans.save()
        status.flush()
      } catch(Exception e) {
        log.debug("BillingService:doTransaction:\n"+e.toString())
        status.setRollbackOnly()
        throw e
      }
    }
  }

  private void transactionType17Handler(_paytrans){
    def oAccount = Account.get(_paytrans.account_id)
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (oPayorder?.agrstatus==1){
      Paytask.createTask(_paytrans,(oPayorder.dealstatus==2?5:2),_paytrans.summa,1)
      oAccount?.moneyReturnExternAccountUpdate(_paytrans.summa)
      oPayorder?.moneyReturnExternOrderUpdate(_paytrans.summa,3)
    } else if(oPayorder?.transtatus==1) {
      Paytask.createTask(_paytrans,(oPayorder.dealstatus==2?5:2),_paytrans.summa,0)
      oAccount?.moneyReturnAccountUpdate(_paytrans.summa)
      oPayorder?.moneyReturnOrderUpdate(_paytrans.summa,3)
    } else {
      throw new Exception ('Unable to determine refund sheme in transaction: '+_paytrans.id)
    }
    cancelMboxBron(Trip.findByPayorder_id(_paytrans.payorder_id),1,_paytrans.comment,collectContext())
    //admin notice! >>
    def oAdmin = Admin.get(Tools.getIntVal(ConfigurationHolder.config.notifyAdmin.id,2))
    if (oAdmin?.email)
      (new Zayavkabyemail(oAdmin.email,oAdmin.name,_paytrans.payorder_id,'владельцем',new Date(),new Date(),'#cancelBronAdminNotice')).save()
  }
  private void transactionType16Handler(_paytrans){
    def oAccount = Account.get(_paytrans.account_id)
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (oPayorder?.agrstatus==1){
      if (_paytrans.summa>0) {
        Paytask.createTask(_paytrans,3,_paytrans.summa,1)//клиенту
      }
      def iCommSumma = 0
      if(oPayorder.reserve_id==3){
        iCommSumma = computeSiteComissionSumma(oPayorder)
        createPaytrans([payorder_id:_paytrans.payorder_id,account_id:_paytrans.account_id,summa:iCommSumma],'',11)
      }
      if (Client.get(oPayorder.client_id)?.is_transferauto&&oPayorder.summa-_paytrans.summa-iCommSumma>0) {
        createPaytrans([payorder_id:_paytrans.payorder_id,account_id:_paytrans.account_id,summa:oPayorder.summa-_paytrans.summa-iCommSumma],'',21)
        if (oPayorder.summa-_paytrans.summa-iCommSumma>0) {
          Paytask.createTask(_paytrans,4,oPayorder.summa-_paytrans.summa,1)//владельцу
        }
      }
      oAccount?.moneyReturnExternAccountUpdate(_paytrans.summa)
      oPayorder?.moneyReturnExternOrderUpdate(_paytrans.summa,2)
    } else if(oPayorder?.transtatus==1) {
      if (_paytrans.summa>0) {
        Paytask.createTask(_paytrans,3,_paytrans.summa,0)//клиенту
      }
      def iCommSumma = 0
      if(oPayorder.reserve_id==3){
        iCommSumma = computeSiteComissionSumma(oPayorder)
        createPaytrans([payorder_id:_paytrans.payorder_id,account_id:_paytrans.account_id,summa:iCommSumma],'',11)
      }
      if (Client.get(oPayorder.client_id)?.is_transferauto&&oPayorder.summa-_paytrans.summa-iCommSumma>0) {
        createPaytrans([payorder_id:_paytrans.payorder_id,account_id:_paytrans.account_id,summa:oPayorder.summa-_paytrans.summa-iCommSumma],'',21)
        if (oPayorder.summa-_paytrans.summa-iCommSumma>0) {
          Paytask.createTask(_paytrans,4,oPayorder.summa-_paytrans.summa,0)//владельцу
        }
      }
      oAccount?.moneyReturnAccountUpdate(_paytrans.summa)
      oPayorder?.moneyReturnOrderUpdate(_paytrans.summa,2)
    } else {
      throw new Exception ('Unable to determine refund sheme in transaction: '+_paytrans.id)
    }
    cancelMboxBron(Trip.findByPayorder_id(_paytrans.payorder_id),0,_paytrans.comment,collectContext())
    //admin notice! >>
    def oAdmin = Admin.get(Tools.getIntVal(ConfigurationHolder.config.notifyAdmin.id,2))
    if (oAdmin?.email)
      (new Zayavkabyemail(oAdmin.email,oAdmin.name,_paytrans.payorder_id,'гостем',new Date(),new Date(),'#cancelBronAdminNotice')).save()
  }
  private void transactionType14Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    confirmTrip(Trip.findByPayorder_id(_paytrans.payorder_id))
    oPayorder.confirmBron()
    if (oPayorder.agr_id==4) {//если аггрегатор payU
      if (smsService.payuIdn(oPayorder))//если ошибка в автоматическом подтверждении
        Paytask.createTask(_paytrans,8,_paytrans.summa,1,oPayorder.tranagr_id)//задание на подтверждение
    }
  }
  private void transactionType20Handler(_paytrans){
    //add some action here
  }
  private void transactionType8Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    oPayorder.updateTransferOutDetail(_paytrans.agrdate)
  }
  private void transactionType1Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    if (oPayorder.modstatus==1&&oPayorder.transtatus!=1&&(oPayorder.agrstatus==1||oPayorder.agrstatus==2)) {//from extern to intern account
      //TODO: считать корректную сумму комиссии аггрегатора
      createPaytrans([payorder_id:oPayorder.id,account_id:_paytrans.account_id,summa:0,agrcode:_paytrans.agrcode,agrdate:_paytrans.agrdate],'',10)
    } else if (oPayorder.modstatus<1) {
      def iPayorderStatus = 1
      if (oPayorder.modstatus==-1||_paytrans.summa!=oPayorder.summa)
        iPayorderStatus = -1
      createTripFromMbox(Mbox.get(oPayorder.mbox_id),Mboxrec.findByMbox_id(oPayorder.mbox_id,[sort:'id',order:'desc']),collectContext(),oPayorder.user_id,0,oPayorder.id,iPayorderStatus)
      Account.get(_paytrans.account_id)?.moneyEarningsAccountUpdate(_paytrans.summa)
      oPayorder?.moneyEarningsOrderUpdate(_paytrans.summa)
      if (iPayorderStatus==-1){
        def oAdmin = Admin.get(Tools.getIntVal(ConfigurationHolder.config.notifyAdmin.id,2))
        def sEmailTemplate = (_paytrans.summa!=oPayorder.summa?'#badPayorderMoney3':(oPayorder.modstatus==-1&&oPayorder.confstatus==2)?'#badPayorderMoney':'#badPayorderMoney2')
        if (oAdmin?.email)
          (new Zayavkabyemail(oAdmin.email,oAdmin.name,oPayorder.id,'',new Date(),new Date(),sEmailTemplate)).save()
      }
    } else {//money has come twice - error!
      throw new Exception ('Error: money has come twice for payorder: '+oPayorder.id)
    }
  }
  private void transactionType2Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    def iPayorderStatus = 1
    if (oPayorder.modstatus==-1||_paytrans.summa!=oPayorder.summa)
      iPayorderStatus = -1
    createTripFromMbox(Mbox.get(oPayorder.mbox_id),Mboxrec.findByMbox_id(oPayorder.mbox_id,[sort:'id',order:'desc']),collectContext(),oPayorder.user_id,0,oPayorder.id,iPayorderStatus)
    Account.get(_paytrans.account_id)?.moneyEarningsExternAccountUpdate(_paytrans.summa)
    oPayorder?.moneyEarningsExternOrderUpdate(_paytrans.summa,_paytrans.comment)
    if (iPayorderStatus==-1){
      def oAdmin = Admin.get(Tools.getIntVal(ConfigurationHolder.config.notifyAdmin.id,2))
      if (oAdmin?.email)
        (new Zayavkabyemail(oAdmin.email,oAdmin.name,oPayorder.id,'',new Date(),new Date(),'#badPayorderMoney')).save()
    }
  }
  private void transactionType10Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    if (oPayorder.modstatus==1&&oPayorder.transtatus!=1&&(oPayorder.agrstatus==1||oPayorder.agrstatus==2)) {//from extern to intern account
      Account.get(_paytrans.account_id)?.moneyExternToInternAccountUpdate(oPayorder.summa_ext - _paytrans.summa,oPayorder.summa_ext)
      oPayorder?.moneyExternToInternOrderUpdate(_paytrans.summa,oPayorder.agrstatus).updateTransferDetail(_paytrans.agrcode,_paytrans.agrdate)
    } else if (oPayorder.modstatus==2&&oPayorder.transtatus!=1&&(oPayorder.agrstatus==1||oPayorder.agrstatus==2)) {//from extern to intern account after deal confirm
      Account.get(_paytrans.account_id)?.moneyExternToInternAfterFinalizationAccountUpdate(oPayorder.summa_ext)
      oPayorder?.moneyExternToInternAfterFinalizationOrderUpdate(_paytrans.summa).updateTransferDetail(_paytrans.agrcode,_paytrans.agrdate)
    } else {//money has come twice - error!
      throw new Exception ('Error: money has come twice for payorder: '+oPayorder.id)
    }
  }
  private void transactionType15Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    if (oPayorder.dealstatus==0) {
      oPayorder.confirmOrderFinallyUpdate()
      def iCommSumma = computeSiteComissionSumma(oPayorder)
      createPaytrans([payorder_id:_paytrans.payorder_id,account_id:_paytrans.account_id,summa:iCommSumma],'',11)
      if(Client.get(oPayorder.client_id)?.is_transferauto){
        if (oPayorder.agrstatus==1||oPayorder.agrstatus==2) {
          createPaytrans([payorder_id:_paytrans.payorder_id,account_id:_paytrans.account_id,summa:oPayorder.summa_ext - iCommSumma],'',21)
        } else if (oPayorder.transtatus>0){
          createPaytrans([payorder_id:_paytrans.payorder_id,account_id:_paytrans.account_id,summa:oPayorder.summa_int - iCommSumma],'',21)
        } else {
          throw new Exception ('Unable to determine out sheme in transaction: '+_paytrans.id)
        }
      }
    } else {
      throw new Exception ('Error: payorder already finalized for transaction: '+_paytrans.id)
    }
  }
  private void transactionType11Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    if (oPayorder.agrstatus==1||oPayorder.agrstatus==2) {
      Account.get(_paytrans.account_id)?.moneySiteComissionExternAccountUpdate(_paytrans.summa)
      oPayorder?.moneySiteComissionExternOrderUpdate(_paytrans.summa)
    } else if (oPayorder.transtatus>0){
      Account.get(_paytrans.account_id)?.moneySiteComissionInternAccountUpdate(_paytrans.summa)
      oPayorder?.moneySiteComissionInternOrderUpdate(_paytrans.summa)
    } else {
      throw new Exception ('Unable to determine commision sheme in transaction: '+_paytrans.id)
    }
  }
  private void transactionType21Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    if (oPayorder.agrstatus==1||oPayorder.agrstatus==2) {
      Account.get(_paytrans.account_id)?.moneyOutExternAccountUpdate(_paytrans.summa)
      oPayorder?.moneyOutExternOrderUpdate(_paytrans.summa)
      if (_paytrans.summa>0&&oPayorder.retstatus==0) {
        Paytask.createTask(_paytrans,1,_paytrans.summa,1)//владельцу
      }
    } else if (oPayorder.transtatus>0){
      Account.get(_paytrans.account_id)?.moneyOutInternAccountUpdate(_paytrans.summa)
      oPayorder?.moneyOutInternOrderUpdate(_paytrans.summa)
      if (_paytrans.summa>0&&oPayorder.retstatus==0) {
        Paytask.createTask(_paytrans,1,_paytrans.summa,0)//владельцу
      }
    } else {
      throw new Exception ('Unable to determine out sheme in transaction: '+_paytrans.id)
    }
    if (oPayorder.retstatus>0)
      oPayorder.finalizeOrderAfterReturn()
  }
  private void transactionType19Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    if (oPayorder.dealstatus==0) {
      oPayorder.declineDealOrderUpdate()
      declineTrip(Trip.findByPayorder_id(_paytrans.payorder_id))
      def oAdmin = Admin.get(Tools.getIntVal(ConfigurationHolder.config.notifyAdmin.id,2))
      if (oAdmin?.email)
        (new Zayavkabyemail(oAdmin.email,oAdmin.name,oPayorder.id,'',new Date(),new Date(),'#declineDealClient')).save()
    } else {
      throw new Exception ('Error: payorder already finalized for transaction: '+_paytrans.id)
    }
  }
  private void transactionType22Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    if (oPayorder.agrstatus==1||oPayorder.agrstatus==2) {
      Account.get(_paytrans.account_id)?.changeHomeOldExternAccountUpdate(_paytrans.summa)
      oPayorder?.changeHomeOldExternOrderUpdate(_paytrans.summa)
    } else if (oPayorder.transtatus>0){
      Account.get(_paytrans.account_id)?.changeHomeOldInternAccountUpdate(_paytrans.summa)
      oPayorder?.changeHomeOldInternOrderUpdate(_paytrans.summa)
    } else {
      throw new Exception ('Unable to determine change sheme in transaction: '+_paytrans.id)
    }
  }
  private void transactionType23Handler(_paytrans){
    def oPayorder = Payorder.get(_paytrans.payorder_id)
    if (!oPayorder)
      throw new Exception ('Where are no related payorder for transaction '+_paytrans.id)
    if (oPayorder.agrstatus==1||oPayorder.agrstatus==2) {
      Account.get(_paytrans.account_id)?.changeHomeNewExternAccountUpdate(_paytrans.summa)
    } else if (oPayorder.transtatus>0){
      Account.get(_paytrans.account_id)?.changeHomeNewInternAccountUpdate(_paytrans.summa)
    } else {
      throw new Exception ('Unable to determine change sheme in transaction: '+_paytrans.id)
    }
  }

  void confirmTrip(oTrip){
    def start
    def end
    if(oTrip.fromdate?:'')
      start= new Date(oTrip.fromdate.getTime())
    if(oTrip.todate?:'')
      end=new Date(oTrip.todate.getTime())
    def hmp = new Homeprop()
    def oUser = User.get(oTrip.user_id)
    def sName = oUser?.nickname?:''
    mailerService.trip_bron_confirm_notice(oUser,oTrip)
    hmp.addHomepropUnavailability(oTrip.home_id,start,end,messageSource.getMessage('inbox.bron.calendar.remark', [(sName?:'')].toArray(), new Locale("ru","RU")),5,oTrip.mbox_id)
    oTrip.modstatus = 1
    oTrip.controlstatus = -1
    oTrip.paystatus = (oTrip.paystatus==-1?1:oTrip.paystatus)
    oTrip.save()
  }

  void declineTrip(oTrip){
    oTrip?.paystatus = 3
    oTrip?.is_read = 0
    oTrip?.save()
  }

  def cancelMboxBron(oTrip,iType,sComments,oContext){
    def oMbox=Mbox.get(oTrip?.mbox_id)
    if(oMbox){
      oMbox.modstatus=5
      oMbox.answertype_id=iType?9:8
      oMbox.moddate=new Date()
      oMbox.mtext=sComments
      oMbox.mtextowner=sComments
      oMbox.is_answer=iType?:0
      oMbox.is_read=0

      if (!oMbox.save()){
        log.debug('error on save Mbox in BillingService:cancelMboxBron')
        oMbox.errors.each{log.debug(it)}
        return 2
      } else {
        def oMboxrec=new Mboxrec()
        oMboxrec.mbox_id =oMbox.id
        oMboxrec.answertype_id=iType?9:8
        oMboxrec.rectext=sComments
        oMboxrec.is_answer=iType?:0
        oMboxrec.inputdate=new Date()

        oMboxrec.price_rub=oMbox.price_rub
        oMboxrec.price=oMbox.price
        oMboxrec.valuta_id=oMbox.valuta_id
        oMboxrec.date_start=oMbox.date_start
        oMboxrec.date_end=oMbox.date_end
        oMboxrec.homeperson_id=oMbox.homeperson_id
        oMboxrec.home_id=oMbox.home_id

        if (!oMboxrec.save()){
          log.debug('error on save Mboxrec in BillingService:cancelMboxBron')
          oMboxrec.errors.each{log.debug(it)}
          return 2
        } else {
          def oUser=User.get(oTrip.user_id?:0)
          if (iType) {
            mailerService.sendOwnerCancelBronMail(oMbox,oUser,oMboxrec.id,oContext)
          } else {
            mailerService.sendCancelBronMail(oMbox,oUser,oMboxrec.id,oContext)
            smsService.sendCancelBronSMS(User.findByClient_id(oMbox.homeowner_cl_id),Home.get(oMbox.home_id)?.region_id)
          }
          oTrip.modstatus = -1
          oTrip.controlstatus = -1
          oTrip.is_read = 0
          oTrip.save()
        }
      }
      return 0
    }
    return 3
  }

  Long createPaytrans(oObject,sComment,iType){
    try {
      switch(iType) {
        case 1:
        case 8:
          return new Paytrans(payorder_id:oObject.payorder_id,account_id:Account.findByClient_id(Payorder.get(oObject.payorder_id)?.client_id)?.id,summa:Math.round(oObject.summa).toInteger(),summa_val:Math.round(oObject.summa).toInteger(),paytype_id:iType,comment:sComment,agrcode:oObject.paynomer,agrdate:oObject.paydate).save(failOnError:true)?.id?:0l
          break
        case 2:
        case 23:
          return new Paytrans(payorder_id:oObject.id,account_id:Account.findByClient_id(oObject.client_id)?.id,summa:oObject.summa,summa_val:oObject.summa,paytype_id:iType,comment:sComment).save(failOnError:true)?.id?:0l
          break
        case 10:
        case 11:
        case 21:
          return new Paytrans(payorder_id:oObject.payorder_id,account_id:oObject.account_id,summa:oObject.summa,summa_val:oObject.summa,paytype_id:iType,comment:sComment,agrcode:oObject.agrcode?:'',agrdate:oObject.agrdate?:'').save(failOnError:true)?.id?:0l
          break
        case 14:
        case 17:
          def oPayorder = Payorder.get(oObject.payorder_id)
          return new Paytrans(payorder_id:oObject.payorder_id,account_id:Account.findByClient_id(oPayorder?.client_id)?.id,paytype_id:iType,comment:sComment,summa:oPayorder.summa,summa_val:oPayorder.summa).save(flush:true,failOnError:true)?.id?:0l
          break
        case 15:
        case 19:
          return new Paytrans(payorder_id:oObject.payorder_id,account_id:Account.findByClient_id(Payorder.get(oObject.payorder_id)?.client_id)?.id,summa:0,summa_val:0,paytype_id:iType,comment:sComment).save(failOnError:true)?.id?:0l
          break
        case 16:
          def oPayorder = Payorder.get(oObject.payorder_id)
          def retsumma = computeReturnSumma(oPayorder)
          return new Paytrans(payorder_id:oObject.payorder_id,account_id:Account.findByClient_id(oPayorder?.client_id)?.id,paytype_id:iType,comment:sComment,summa:retsumma,summa_val:retsumma).save(flush:true,failOnError:true)?.id?:0l
          break
        case 20:
          return new Paytrans(payorder_id:oObject.payorder.id,account_id:Account.findByClient_id(oObject.payorder.client_id)?.id,summa:oObject.payorder.summa,summa_val:oObject.payorder.summa,paytype_id:iType,comment:sComment,returncode:oObject.retcode).save(failOnError:true)?.id?:0l
          break
        case 22:
          def iSumma = 0
          if (oObject.retstatus==0) {
            iSumma = oObject.summa_ext?:oObject.summa_int
          }
          return new Paytrans(payorder_id:oObject.id,account_id:Account.findByClient_id(oObject.client_id)?.id,summa:iSumma,summa_val:iSumma,paytype_id:iType,comment:sComment).save(failOnError:true)?.id?:0l
          break
        default:
          return 0l
          break
      }
    } catch(Exception e) {
      log.debug('error on save Paytrans in BillingService:createPaytrans\n'+e.toString())
      return 0l
    }
  }

  def importPaytransferFromWMFakeXLS(_fakexls){
    if (!_fakexls)
      throw new Exception ('No file')
    /*if (_fakexls.getContentType() != "application/vnd.ms-excel")
      throw new Exception ('Not supported file type')*/
    InputStreamReader isr = new InputStreamReader(_fakexls.getInputStream(), "windows-1251")
    def records = new XmlSlurper(new org.cyberneko.html.parsers.SAXParser()).parse(isr)
    def lsPreparedLines = []
    records.BODY.TABLE[0].TR.eachWithIndex{ it, i ->
      if (it.TD[2].text()&&i)
        lsPreparedLines << it.TD.collect{td->td.text()}
    }
    def result = [complete:0,total:lsPreparedLines.size()?:0,notimport:[]]
    Paytrans.withTransaction { status ->
      lsPreparedLines.eachWithIndex{ it, idx ->
        def oSavepoint = status.createSavepoint()
        try {
          def oPaytransfer = null
          if ((oPaytransfer = Paytransfer.createWMExpensesFromHtmlLine(it))){
            if (oPaytransfer.payorder_id&&oPaytransfer.paytype>0) {
              if(!createPaytrans(oPaytransfer,oPaytransfer.paycomment,(oPaytransfer.paytype==1)?1:8))
                throw new Exception ('adding Paytrans error')
              oPaytransfer.updateModstatus(1)
            } else if (oPaytransfer.paylist&&oPaytransfer.paytype>0){
              oPaytransfer.paylist.split(',').each{
                if(!createPaytrans([payorder_id:it as Long,summa:oPaytransfer.summa,paydate:oPaytransfer.paydate,paynomer:oPaytransfer.paynomer],oPaytransfer.paycomment,(oPaytransfer.paytype==1)?1:8))
                  throw new Exception ('adding Paytrans error')
              }
              oPaytransfer.updateModstatus(1)
            }
            result.complete++
            status.releaseSavepoint(oSavepoint)
          }
        } catch(Exception e) {
          log.debug('error on create Paytransfer in BillingService:importPaytransferFromCSV '+idx+'\n'+e.toString())
          result.notimport << idx+1
          status.rollbackToSavepoint(oSavepoint)
        }
      }
    }
    return result.complete.toString() + ' of ' + result.total + ' was imported. ' + (result.notimport?('Not imported lines are: '+result.notimport.toString()):'')
  }

  def importPaytransferFromCSV(_csv){
    if (!_csv)
      throw new Exception ('No file')
    /*if (_csv.getContentType() != "text/comma-separated-values")
      throw new Exception ('Not supported file type')*/
    def result = [complete:0,total:0,notimport:[]]
    def lsLines = _csv.getInputStream().readLines('windows-1251')
    def lsTitleInd = messageSource.getMessage('csv.fields', null, LCH.getLocale()).split(',').collect{(lsLines[5]+' '+lsLines[6]).replace('"','').split(';').toList().indexOf(it)}
    def lsPreparedLines = Tools.prepareCSVLines(lsLines[6..lsLines.size()-1],(lsLines[5]+' '+lsLines[6]).replace('"','').split(';',-1).size())
    result.total = lsPreparedLines.size()?:0
    Paytrans.withTransaction { status ->
      lsPreparedLines.eachWithIndex{ it, idx ->
        def oSavepoint = status.createSavepoint()
        try {
          def oPaytransfer = null
          if ((oPaytransfer = Paytransfer.createFromCsvLine(it.split(';',-1),lsTitleInd))){
            if (oPaytransfer.payorder_id&&oPaytransfer.paytype>0) {
              if(!createPaytrans(oPaytransfer,oPaytransfer.paycomment,(oPaytransfer.paytype==1)?1:8))
                throw new Exception ('adding Paytrans error')
              oPaytransfer.updateModstatus(1)
            } else if (oPaytransfer.paylist&&oPaytransfer.paytype>0){
              oPaytransfer.paylist.split(',').each{
                if(!createPaytrans([payorder_id:it as Long,summa:oPaytransfer.summa,paydate:oPaytransfer.paydate,paynomer:oPaytransfer.paynomer],oPaytransfer.paycomment,(oPaytransfer.paytype==1)?1:8))
                  throw new Exception ('adding Paytrans error')
              }
              oPaytransfer.updateModstatus(1)
            }
            result.complete++
            status.releaseSavepoint(oSavepoint)
          }
        } catch(Exception e) {
          log.debug('error on create Paytransfer in BillingService:importPaytransferFromCSV '+idx+'\n'+e.toString())
          result.notimport << idx+1
          status.rollbackToSavepoint(oSavepoint)
        }
      }
    }
    return result.complete.toString() + ' of ' + result.total + ' was imported. ' + (result.notimport?('Not imported lines are: '+result.notimport.toString()):'')
  }

  void updatePaytransferDetails(_oRequest,_oPaytransfer){
    if (!_oPaytransfer)
      throw new Exception ('No paytransfer')
    _oPaytransfer.paycomment = _oRequest.paycomment?:''
    _oPaytransfer.admin_id = _oRequest.admin_id?:0
    if (_oPaytransfer.modstatus!=1) {
      _oPaytransfer.paytype = _oRequest.paytype?:0
      if (_oRequest.payorder?.isLong()) {
        _oPaytransfer.payorder_id = _oRequest.payorder?.toLong()
        _oPaytransfer.paylist = ''
      } else {
        _oPaytransfer.payorder_id = 0l
        _oPaytransfer.paylist = _oRequest.payorder?:''
      }
      if(_oPaytransfer.paytype&&(_oPaytransfer.payorder_id||_oPaytransfer.paylist)) {
        _oPaytransfer.modstatus = 0
      } else {
        _oPaytransfer.modstatus = 2
      }
    }
    _oPaytransfer.save(flush:true)
  }

  void updatePayorderAdmincomment(_oPayorder,_sComment){
    if (!_oPayorder)
      throw new Exception ('No payorder')
    _oPayorder.admincomment = _sComment?:''
    _oPayorder.save(flush:true,failOnError:true)
  }

  void processPaytransfer(_oPaytransfer){
    if (!_oPaytransfer)
      throw new Exception ('No paytransfer')
    if(_oPaytransfer.modstatus==0){
      Paytrans.withTransaction { status ->
        try {
          if (_oPaytransfer.payorder_id&&_oPaytransfer.paytype>0) {
            if(!createPaytrans(_oPaytransfer,_oPaytransfer.paycomment,(_oPaytransfer.paytype==3)?8:1))
              throw new Exception ('adding Paytrans error')
            _oPaytransfer.updateModstatus(1)
          } else if (_oPaytransfer.paylist&&_oPaytransfer.paytype>0){
            _oPaytransfer.paylist.split(',').each{
              if(!createPaytrans([payorder_id:it as Long,summa:_oPaytransfer.summa],_oPaytransfer.paycomment,(_oPaytransfer.paytype==3)?8:1))
                throw new Exception ('adding Paytrans error')
            }
            _oPaytransfer.updateModstatus(1)
          }
        } catch(Exception e) {
          log.debug("BillingService:processPaytransfer:\n"+e.toString())
          status.setRollbackOnly()
          throw e
        }
      }
    }
  }
  private def collectContext() {
    [
      is_dev:(Tools.getIntVal(ConfigurationHolder.config.isdev,0)==1),
      serverURL:(ConfigurationHolder.config.grails.mailServerURL?:ConfigurationHolder.config.grails.serverURL),
      appname:ConfigurationHolder.config.grails.serverApp,
      lang:''
    ]
  }
  private Integer computeReturnSumma(oPayorder) {
    if (!oPayorder)
      throw new Exception ('Payorder can not be null in computeReturnSumma')
    def oTrip = Trip.findByPayorder_id(oPayorder?.id)
    if (!oTrip)
      throw new Exception ('Where are no related trip for payorder '+oPayorder?.id+' in computeReturnSumma')
    if (oPayorder.reserve_id==3) {
      return 0
    }
    if (oTrip.modstatus==0) {
      return oPayorder.summa
    }
    def now = Calendar.getInstance()
    def fromdate = Calendar.getInstance()
    fromdate.setTime(oTrip.fromdate)
    fromdate.add(Calendar.HOUR_OF_DAY,Rule_timein.get(Home.get(oTrip.home_id)?.rule_timein_id?:1)?.kol?:15)
    if(oTrip.rule_cancellation_id==1) {//гибкая схема отмены
      if (fromdate.getTime()-1<now.getTime()) {
        if (oPayorder.reserve_id==1) {//бронь на сутки
          return 0
        } else {//другие схемы брони
          return oPayorder.summa - calculateReturnDayPrice(oTrip,oPayorder.summa_deal)
        }
      } else {
        return oPayorder.summa
      }
    } else if (oTrip.rule_cancellation_id==2) {//умеренная схема отмены
      if (fromdate.getTime()-5<now.getTime()) {
        if (oPayorder.reserve_id==1) {//бронь на сутки
          return 0
        } else {//другие схемы брони
          return Math.round((oPayorder.summa - calculateReturnDayPrice(oTrip,oPayorder.summa_deal))*0.5) as Integer
        }
      } else {
        return oPayorder.summa
      }
    } else {//все остальные обрабатываются как строгая схема
      if (fromdate.getTime()-7<now.getTime()) {
        return 0
      } else {
        return Math.round(oPayorder.summa*0.5) as Integer
      }
    }
  }

  private Integer calculateReturnDayPrice(oTrip,iSumma){
    def iDays = oTrip.todate - oTrip.fromdate
    if (Home.get(oTrip.home_id)?.is_pricebyday) {
      iDays++
    }
    if ((iDays?:1)==1) {
      return iSumma
    } else {
      return Math.round((iSumma/(iDays?:1))) as Integer
    }
  }

  private Integer computeSiteComissionSumma(oPayorder) {
    if (oPayorder.paycomtype==1) {
      def ourComm = computeOurComissionSumma(oPayorder.summa_deal,oPayorder.reserve_id)
      return (ourComm>oPayorder.summa?oPayorder.summa:ourComm) - oPayorder.summa_agr
    } else if (oPayorder.paycomtype==2){
      return computeOurComissionSumma(oPayorder.summa,oPayorder.reserve_id) - oPayorder.summa_agr
    } else {
      return 0
    }
  }
  private Integer computeOurComissionSumma(_summa,_type) {
    return (_type==3?_summa:Math.round(_summa * (Tools.getIntVal(ConfigurationHolder.config.commision.our.percent,10) / 100)) as Integer)
  }

  void processGenerateAct(){
    Payorder.findAll {
      modstatus == 2 &&
      dealstatus == 1 &&
      year(findate) == (new Date()-1).getYear()+1900 &&
      month(findate) == (new Date()-1).getMonth()+1
    }.collect{it.client_id}.unique().each{ clId ->
      def ndeal = 0
      def summafull = 0
      def summa = 0
      Payorder.findAll {
          modstatus == 2 &&
          dealstatus == 1 &&
          year(findate) == (new Date()-1).getYear()+1900 &&
          month(findate) == (new Date()-1).getMonth()+1 &&
          client_id == clId
      }.each{
          ndeal++
          summafull += it.summa
          summa += it.summa_com + it.summa_agr
      }
      try {
        String [] args = [messageSource.getMessage('calendar.monthName', null, LCH.getLocale()).split(',')[(new Date()-1).getMonth()]]
        new Payact(client_id:clId,type_id:1,ndeal:ndeal,summafull:summafull,summa:summa,actdate:new Date(),description:messageSource.getMessage('account.payact.description.template', args, LCH.getLocale())).save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("BillingService:processGenerateAct:error for client id:"+clId+"\n"+e.toString())
      }
    }
  }

  void computeClientSaldo(){
    Payorder.findAll {
      modstatus > 0 &&
      year(plat_date) == (new Date()-1).getYear()+1900 &&
      month(plat_date) == (new Date()-1).getMonth()+1
    }.collect{it.client_id}.unique().each{ clId ->
      def saldo = 0
      Payorder.findAll {
        modstatus == 1 &&
        year(plat_date) == (new Date()-1).getYear()+1900 &&
        month(plat_date) == (new Date()-1).getMonth()+1 &&
        client_id == clId
      }.each{
        if (it.summa_own==0)
          saldo += (it.summa - computeSiteComissionSumma(it))
      }
      try {
        new Clientsaldo(client_id:clId,mdate:new Date()-1,saldo:saldo).save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("BillingService:computeClientSaldo:error for client id:"+clId+"\n"+e.toString())
      }
    }
  }

  void changeTripHome(oTrip,oRequest,lPayorderId){
    if (!oTrip)
      throw new Exception ('Trip can not be null')
    oTrip.home_id = oRequest.home_id
    oTrip.mbox_id = oRequest.mbox_id?:0
    oTrip.payorder_id = lPayorderId
    oTrip.fromdate = oRequest.order_date_start
    oTrip.todate = oRequest.order_date_end
    oTrip.price = oRequest.summa_val
    oTrip.valuta_id = oRequest.valuta_id
    oTrip.price_rub = oRequest.summa_deal
    oTrip.paystatus = 1
    oTrip.modstatus = 1
    oTrip.save(failOnError:true)
    if (Mbox.get(oRequest.mbox_id?:0)) setBookAndPaidStatus(Mbox.get(oRequest.mbox_id),Mboxrec.findByMbox_id(oRequest.mbox_id,[sort:'id',order:'desc']),true)
  }

  private void setBookAndPaidStatus(oMbox,oMboxrec1,isPaid=false){
    oMbox.modstatus = 4
    oMbox.answertype_id = 7
    oMbox.moddate = new Date()
    oMbox.mtext = (isPaid?messageSource.getMessage('inbox.bron.message.pay', null, new Locale("ru","RU")):messageSource.getMessage('inbox.bron.message.notpay', null, new Locale("ru","RU")))
    oMbox.mtextowner = oMbox.mtext
    oMbox.is_answer = 0
    oMbox.is_read = 0

    if (!oMbox.save(flush:true)){
      log.debug('error on save Mbox in BillingService:createTripFromMbox')
      oMbox.errors.each{log.debug(it)}
      throw new Exception ('Save Error')
    }

    def oMboxrec=new Mboxrec()
    oMboxrec.mbox_id = oMbox.id
    oMboxrec.answertype_id = oMbox.answertype_id
    oMboxrec.rectext = oMbox.mtext
    oMboxrec.is_answer = 0
    oMboxrec.is_system = 1
    oMboxrec.inputdate = new Date()

    oMboxrec.price_rub = oMboxrec1.price_rub
    oMboxrec.price = oMboxrec1.price
    oMboxrec.valuta_id = oMboxrec1.valuta_id
    oMboxrec.date_start = oMboxrec1.date_start
    oMboxrec.date_end = oMboxrec1.date_end
    oMboxrec.homeperson_id = oMboxrec1.homeperson_id
    oMboxrec.home_id = oMboxrec1.home_id

    if (!oMboxrec.save(flush:true)){
      log.debug('error on save Mboxrec in BillingService:createTripFromMbox')
      oMboxrec.errors.each{log.debug(it)}
      throw new Exception ('Save Error')
    }
  }

}