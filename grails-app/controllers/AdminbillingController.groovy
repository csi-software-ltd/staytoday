import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter

class AdminbillingController {
  
  def requestService
  def billingService

  def beforeInterceptor = [action:this.&checkAdmin]
  def static final DATE_FORMAT='yyyy-MM-dd'
  
  def checkAdmin() {
    if(session?.admin?.id!=null){
      def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
      session.attention_message=oTemp_notification?oTemp_notification.text:null
      if (session.admin?.menu?.find{it.id==16}) {
        session.admin.mbox_notmoderate_count = Mbox.countByIs_approvedOrIs_adminread(0,0)
        if (session.admin.mbox_notmoderate_count==1) {
          session.admin.mbox_notmoderate_id = Mbox.findByIs_approvedOrIs_adminread(0,0)?.id?:0
        }
      }
      if (session.admin?.menu?.find{it.id==28}) {
        session.admin.trip_notread_count = Trip.countByIs_read(0)
      }
      if (session.admin?.menu?.find{it.id==25}) {
        session.admin.paytask_notcomplete_count = Paytask.countByModstatus(0)
      }
    }else{
      redirect(controller:'administrators', action:'index',params:[redir:1], base:(Tools.getIntVal(Dynconfig.findByName('global.https.enable')?.value,0)?(ConfigurationHolder.config.grails.secureServerURL?:ConfigurationHolder.config.grails.serverURL):ConfigurationHolder.config.grails.serverURL))
      return false;
    }
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def checkAccess(iActionId){
    def bDenied = true
    session.admin.menu.each{
      if (iActionId==it.id) bDenied = false
    }
    if (bDenied) {
      redirect(controller:'administrators', action:'profile')
      return
    }
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytransfer >>>/////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def paytransfer = {
    checkAccess(19)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=19
    hsRes.admin = session.admin 

    def fromDetail = requestService.getIntDef('fromDetail',0)
    if (fromDetail){
      session.lastRequest.fromDetail = fromDetail
      hsRes.inrequest = session.lastRequest
    }
    return hsRes
  }

  def paytransferlist = {
    checkAccess(19)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=19
    hsRes.admin = session.admin
    if (session.lastRequest?.fromDetail?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetail = 0
    } else {
      hsRes+=requestService.getParams(null,['transfer_id','payorder_id'],null,['pay_date_from','pay_date_to','modify_date_from','modify_date_to'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.paytype = requestService.getIntDef('paytype',0)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    def oPaytransfer = new Paytransfer()
    hsRes+=oPaytransfer.csiSelectPaytransfer(hsRes.inrequest.transfer_id?:0,hsRes.inrequest.modstatus,
        hsRes.inrequest.paytype,hsRes.inrequest.payorder_id?:0l,hsRes.inrequest.pay_date_from?:'',
        hsRes.inrequest.pay_date_to?:'',hsRes.inrequest.modify_date_from?:'',hsRes.inrequest.modify_date_to?:'',
        20,requestService.getOffset())

    return hsRes
  }

  def paytransferdetail = {
    checkAccess(19)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=19
    hsRes.admin = session.admin 

    def lId=requestService.getLongDef('id',0)
    hsRes.paytransfer = Paytransfer.get(lId)
    if (!hsRes.paytransfer) {
      redirect(controller:'adminbilling', action:'paytransfer')
      return
    }
    return hsRes
  }

  def paytransferimportCSV = {
    checkAccess(19)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=19
    hsRes.admin = session.admin
    
    try {
      hsRes.result = billingService.importPaytransferFromCSV(request.getFile('file'))
    } catch(Exception e) {
      log.debug('Error on import Paytransfer from CSV: '+e.toString())
      hsRes.result = e.toString()
    }
    return hsRes
  }

  def paytransferimportWM = {
    checkAccess(19)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=19
    hsRes.admin = session.admin

    try {
      hsRes.result = billingService.importPaytransferFromWMFakeXLS(request.getFile('file'))
    } catch(Exception e) {
      log.debug('Error on import Paytransfer from WM: '+e.toString())
      hsRes.result = e.toString()
    }
    render(view:'paytransferimportCSV',model:hsRes)
  }

  def savePaytransferDetails = {
    checkAccess(19)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=19
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(['paytype','admin_id'],['id'],['payorder','paycomment'])
    flash.save_error = []
    hsRes.paytransfer = Paytransfer.get(hsRes.inrequest.id)
    if (hsRes.paytransfer) {
      try {
        billingService.updatePaytransferDetails(hsRes.inrequest,hsRes.paytransfer)
      } catch(Exception e) {
        log.debug('error on save Mboxrec in BillingService:savePaytransferDetails')
        flash.save_error << 101
      }
    }

    redirect(action:'paytransferdetail',params:hsRes.inrequest)
    return
  }

  def processPaytransfer = {
    checkAccess(19)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=19
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(null,['id'])
    flash.save_error = []
    hsRes.paytransfer = Paytransfer.get(hsRes.inrequest.id)
    if (hsRes.paytransfer) {
      try {
        billingService.processPaytransfer(hsRes.paytransfer)
      } catch(Exception e) {
        log.debug('error on save Mboxrec in BillingService:processPaytransfer')
        flash.save_error << 101
      }
    }

    redirect(action:'paytransferdetail',params:hsRes.inrequest)
    return
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytransfer <<</////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Payorder >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def payorder = {
    checkAccess(21)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=21
    hsRes.admin = session.admin 

    def fromDetail = requestService.getIntDef('fromDetail',0)
    if (fromDetail){
      session.lastRequest.fromDetail = fromDetail
      hsRes.inrequest = session.lastRequest
    }
    hsRes.agr = Agr.list()
    hsRes.payway = Payway.list()
    return hsRes
  }

  def payorderlist = {
    checkAccess(21)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=21
    hsRes.admin = session.admin
    if (session.lastRequest?.fromDetail?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetail = 0
    } else {
      hsRes+=requestService.getParams(['payway_id','agr_id'],['client_id','home_id'],['plat_name','norder'],['inputdate_from','inputdate_to'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    def oPayorder = new PayorderSearch()
    hsRes+=oPayorder.csiSelectPayorder(hsRes.inrequest.norder?:'',hsRes.inrequest.modstatus,
        hsRes.inrequest.payway_id?:0,hsRes.inrequest.agr_id?:0,hsRes.inrequest.client_id?:0l,
        hsRes.inrequest.home_id?:0l,hsRes.inrequest.plat_name?:'',hsRes.inrequest.inputdate_from?:'',
        hsRes.inrequest.inputdate_to?:'',20,requestService.getOffset())

    return hsRes
  }

  def payorderdetail = {
    checkAccess(21)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=21
    hsRes.admin = session.admin 

    def lId=requestService.getLongDef('id',0)
    hsRes.payorder = Payorder.get(lId)
    if (!hsRes.payorder) {
      redirect(controller:'adminbilling', action:'payorder')
      return
    }
    hsRes.client = Client.get(hsRes.payorder.client_id)
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.trip = Trip.findByPayorder_id(hsRes.payorder.id)
    hsRes.paytrans = Paytrans.findAllByPayorder_id(hsRes.payorder.id,[sort:'id',order:'desc'])


    return hsRes
  }

  def payorderAddComment = {
    checkAccess(21)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=21
    hsRes.admin = session.admin 

    def lId=requestService.getLongDef('id',0)
    def sComment=requestService.getStr('admincomment')
    hsRes.payorder = Payorder.get(lId)
    if (!hsRes.payorder) {
      redirect(controller:'adminbilling', action:'payorder')
      return
    }
    try {
      billingService.updatePayorderAdmincomment(hsRes.payorder,sComment)
    } catch(Exception e) {
      log.debug('Error on add comment to payorder: '+e.toString())
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def payorderMoneyReturn = {
    checkAccess(21)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=21
    hsRes.admin = session.admin 

    def lId=requestService.getLongDef('id',0)
    hsRes.payorder = Payorder.get(lId)
    if (!hsRes.payorder) {
      redirect(controller:'adminbilling', action:'payorder')
      return
    }
    if (hsRes.payorder.retstatus){
      render(contentType:"application/json"){[error:true]}
      return
    }
    try {
      billingService.doTransaction( billingService.createPaytrans(Trip.findByPayorder_id(hsRes.payorder.id),message(code:'admin.payorder.moneyreturn.message'),17) )
    } catch(Exception e) {
      log.debug('Error on return money to payorder: '+e.toString())
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def payorderDeclainDeal = {
    checkAccess(21)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=21
    hsRes.admin = session.admin 

    def lId=requestService.getLongDef('id',0)
    hsRes.payorder = Payorder.get(lId)
    if (!hsRes.payorder) {
      redirect(controller:'adminbilling', action:'payorder')
      return
    }
    if (hsRes.payorder.dealstatus!=0||hsRes.payorder.confstatus!=1){
      render(contentType:"application/json"){[error:true]}
      return
    }
    try {
      billingService.doTransaction( billingService.createPaytrans([payorder_id:hsRes.payorder.id],'',19) )
    } catch(Exception e) {
      log.debug('Error on declain deal to payorder: '+e.toString())
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def changepayorderhome = {
    checkAccess(21)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=21
    hsRes.admin = session.admin 

    def lId=requestService.getLongDef('id',0)
    hsRes.payorder = Payorder.get(lId)
    if (!hsRes.payorder) {
      redirect(controller:'adminbilling', action:'payorder')
      return
    }
    if (hsRes.payorder.modstatus!=1){
      render(contentType:"application/json"){[error:true,errortext:message(code:'admin.changepayorderhome.incorrectstatus.errortext',args:[], default:'')]}
      return
    }
    hsRes+=requestService.getParams(['summa_deal','summa_val','valuta_id'],['home_id'])
    hsRes.inrequest.order_date_start = requestService.getDate('order_date_start')
    hsRes.inrequest.order_date_end = requestService.getDate('order_date_end')
    hsRes.inrequest.mbox_id = requestService.getLongDef('mbox_id',0)
    if ((hsRes.inrequest?.size()?:0)!=7) {
      render(contentType:"application/json"){[error:true,errortext:message(code:'admin.changepayorderhome.notalldataset.errortext',args:[], default:'')]}
      return
    }
    try {
      hsRes.neworder = billingService.createOrderFromOrder(hsRes.payorder,hsRes.inrequest)
      billingService.doTransaction( billingService.createPaytrans(hsRes.neworder,'',23) )
      billingService.doTransaction( billingService.createPaytrans(hsRes.payorder,'',22) )
      billingService.changeTripHome(Trip.findByPayorder_id(hsRes.payorder.id),hsRes.inrequest,hsRes.neworder?.id?:0l)
    } catch(Exception e) {
      log.debug('Error on change home to payorder: '+e.toString())
      render(contentType:"application/json"){[error:true,errortext:message(code:'admin.changepayorderhome.bderror.errortext',args:[], default:'')]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Payorder <<<////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Account >>>/////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def account = {
    checkAccess(22)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=22
    hsRes.admin = session.admin

    def fromDetail = requestService.getIntDef('fromDetail',0)
    if (fromDetail){
      session.lastRequest.fromDetail = fromDetail
      hsRes.inrequest = session.lastRequest
    }
    return hsRes
  }

  def accountlist = {
    checkAccess(22)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=22
    hsRes.admin = session.admin
    if (session.lastRequest?.fromDetail?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetail = 0
    } else {
      hsRes+=requestService.getParams(null,['client_id'],['code'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    def oAccount = new Account()
    hsRes+=oAccount.csiSelectAccount(hsRes.inrequest.code?:'',hsRes.inrequest.modstatus,
        hsRes.inrequest.client_id?:0l,20,requestService.getOffset())

    return hsRes
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Account <<</////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytrans >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def paytrans = {
    checkAccess(23)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=23
    hsRes.admin = session.admin 

    def fromDetail = requestService.getIntDef('fromDetail',0)
    if (fromDetail){
      session.lastRequest.fromDetail = fromDetail
      hsRes.inrequest = session.lastRequest
    }
    hsRes.paytype = Paytype.list()
    return hsRes
  }

  def paytranslist = {
    checkAccess(23)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=23
    hsRes.admin = session.admin
    if (session.lastRequest?.fromDetail?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetail = 0
    } else {
      hsRes+=requestService.getParams(['paytype_id'],['payorder_id','account_id','client_id'],null,['moddate_from','moddate_to'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    def oPaytrans = new PaytransSearch()
    hsRes+=oPaytrans.csiSelectPaytrans(hsRes.inrequest.payorder_id?:0l,hsRes.inrequest.modstatus,
        hsRes.inrequest.account_id?:0l,hsRes.inrequest.paytype_id?:0,hsRes.inrequest.client_id?:0l,
        hsRes.inrequest.moddate_from?:'',hsRes.inrequest.moddate_to?:'',20,requestService.getOffset())

    return hsRes
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytrans <<<////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytask >>>/////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def paytask = {
    checkAccess(25)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=25
    hsRes.admin = session.admin 

    def fromDetail = requestService.getIntDef('fromDetail',0)
    if (fromDetail){
      session.lastRequest.fromDetail = fromDetail
      hsRes.inrequest = session.lastRequest
    }
    hsRes.paytasktype = Paytasktype.list()
    return hsRes
  }

  def paytasklist = {
    checkAccess(25)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=25
    hsRes.admin = session.admin
    if (session.lastRequest?.fromDetail?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetail = 0
    } else {
      hsRes+=requestService.getParams(['paytasktype_id','paytask_id'],['paytrans_id'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    def oPaytask = new PaytaskSearch()
    hsRes+=oPaytask.csiSelectPaytask(hsRes.inrequest.paytask_id?:0,hsRes.inrequest.modstatus,
        hsRes.inrequest.paytasktype_id?:0,hsRes.inrequest.paytrans_id?:0l,20,requestService.getOffset())

    return hsRes
  }

  def paytaskcomplete = {
    checkAccess(25)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=25
    hsRes.admin = session.admin 

    def iId = requestService.getIntDef('id',0)
    def oPaytask = Paytask.get(iId)

    if(oPaytask){
      try {
        oPaytask.setcomplete()
      } catch(Exception e) {
        log.debug('Error update paytask №: '+iId+'\n'+e.toString())
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytask <<</////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Payreport >>>///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def payreport = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    return hsRes
  }

  def payreportordermoney = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    hsRes.reportdate_from = requestService.getDate('reportdate_from')
    hsRes.reportdate_to = requestService.getDate('reportdate_to')

    hsRes.payreport = Paytrans.findAll(sort:"payorder_id"){
      (paytype_id == 1 || paytype_id == 8) &&
      moddate > (hsRes.reportdate_from?:new Date()) &&
      moddate < (hsRes.reportdate_to?hsRes.reportdate_to+1:new Date()+1)
    }

    renderPdf(template: 'payreportordermoney', model: hsRes, filename: "reportordermoney")
    return
  }

  def payreportagentreward = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    hsRes.agentreward_date = requestService.getRaw('agentreward_date')
    hsRes.reportStart = new GregorianCalendar()
    hsRes.reportStart.setTime(hsRes.agentreward_date)
    hsRes.reportEnd = hsRes.reportStart.clone()
    hsRes.reportEnd.add(Calendar.MONTH,1)
    hsRes.reportEnd.add(Calendar.DATE,-1)
    if (hsRes.reportEnd.getTime() > new Date())
      hsRes.reportEnd.setTime(new Date())
    def lClientId = requestService.getLongDef('client_id',0)
    if (lClientId){
      hsRes.clIds = []
      hsRes.clIds << lClientId
    } else {
      hsRes.clIds = hsRes.payreport = Payorder.findAll(sort:"id"){
        modstatus > 0 &&
        plat_date >= hsRes.reportStart.getTime() &&
        plat_date < hsRes.reportEnd.getTime()+1
      }.collect{it.client_id}.unique()
    }
    hsRes.payreports = []
    hsRes.clIds.each { clId -> 
      def orders = Payorder.findAll(sort:"id"){
        client_id == clId &&
        modstatus > 0 &&
        plat_date >= hsRes.reportStart.getTime() &&
        plat_date < hsRes.reportEnd.getTime()+1
      }
      def comsumma = 0
      def summatotal = 0
      def summadealtotal = 0
      def summafin = 0
      orders.each{
        comsumma += it.summa_com
        summatotal += it.summa
        summadealtotal += it.summa_deal
        summafin += it.summa_own
      }
      if (orders)
        hsRes.payreports << [orders:orders,comsumma:comsumma,summatotal:summatotal,summafin:summafin,summadealtotal:summadealtotal,summafinWords:Tools.num2str(summafin,true),comsummaWords:Tools.num2str(comsumma,true)]
    }
    hsRes.reportMonth = message(code:'calendar.monthNameP').split(',')[requestService.getIntDef('agentreward_date_month',1)]
    hsRes.reportYear = requestService.getIntDef('agentreward_date_year',2013)
    hsRes.accountData = Accountdata.findByModstatus(1)
    hsRes.commissionPersent = Tools.getIntVal(ConfigurationHolder.config.commision.our.percent,10)

    renderPdf(template: 'payreportagentreward', model: hsRes, filename: "reportagentreward")
    return
  }

  def payreportagent = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    hsRes.agent_date = requestService.getRaw('agent_date')
    hsRes.reportStart = new GregorianCalendar()
    hsRes.reportStart.setTime(hsRes.agent_date)
    hsRes.reportEnd = hsRes.reportStart.clone()
    hsRes.reportEnd.add(Calendar.MONTH,1)
    hsRes.reportEnd.add(Calendar.DATE,-1)
    if (hsRes.reportEnd.getTime() > new Date())
      hsRes.reportEnd.setTime(new Date())
    def lClientId = requestService.getLongDef('client_id',0)
    if (lClientId){
      hsRes.clIds = []
      hsRes.clIds << lClientId
    } else {
      hsRes.clIds = hsRes.payreport = Payorder.findAll(sort:"id"){
        modstatus > 0 &&
        plat_date >= hsRes.reportStart.getTime() &&
        plat_date < hsRes.reportEnd.getTime()+1
      }.collect{it.client_id}.unique()
    }
    hsRes.payreports = []
    hsRes.clIds.each { clId -> 
      def orders = Payorder.findAll(sort:"id"){
        client_id == clId &&
        modstatus > 0 &&
        plat_date >= hsRes.reportStart.getTime() &&
        plat_date < hsRes.reportEnd.getTime()+1
      }
      def comsumma = 0
      def summatotal = 0
      def summadealtotal = 0
      def summafin = 0
      orders.each{
        comsumma += it.summa_com
        summatotal += it.summa
        summadealtotal += it.summa_deal
        summafin += it.summa_own
      }
      if (orders)
        hsRes.payreports << [orders:orders,comsumma:comsumma,summatotal:summatotal,summafin:summafin,summadealtotal:summadealtotal,saldo:Clientsaldo.findByClient_idAndMdate(clId,hsRes.reportEnd.getTime())?.saldo?:0]
    }
    hsRes.reportMonth = message(code:'calendar.monthNameP').split(',')[requestService.getIntDef('agent_date_month',1)]
    hsRes.reportYear = requestService.getIntDef('agent_date_year',2013)
    hsRes.accountData = Accountdata.findByModstatus(1)
    hsRes.commissionPersent = Tools.getIntVal(ConfigurationHolder.config.commision.our.percent,10)

    renderPdf(template: 'payreportagent', model: hsRes, filename: "reportagent")
    return
  }

  def payreportagentXLS = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    hsRes.agent_date = requestService.getRaw('agent_date')
    hsRes.reportStart = new GregorianCalendar()
    hsRes.reportStart.setTime(hsRes.agent_date)
    hsRes.reportEnd = hsRes.reportStart.clone()
    hsRes.reportEnd.add(Calendar.MONTH,1)
    hsRes.reportEnd.add(Calendar.DATE,-1)
    if (hsRes.reportEnd.getTime() > new Date())
      hsRes.reportEnd.setTime(new Date())
    def lClientId = requestService.getLongDef('client_id',0)
    if (lClientId){
      hsRes.clIds = []
      hsRes.clIds << lClientId
    } else {
      hsRes.clIds = hsRes.payreport = Payorder.findAll(sort:"id"){
        modstatus > 0 &&
        plat_date >= hsRes.reportStart.getTime() &&
        plat_date < hsRes.reportEnd.getTime()+1
      }.collect{it.client_id}.unique()
    }
    hsRes.payreports = []
    hsRes.clIds.each { clId -> 
      def orders = Payorder.findAll(sort:"id"){
        client_id == clId &&
        modstatus > 0 &&
        plat_date >= hsRes.reportStart.getTime() &&
        plat_date < hsRes.reportEnd.getTime()+1
      }
      def comsumma = 0
      def summatotal = 0
      def summadealtotal = 0
      def summafin = 0
      orders.each{
        comsumma += it.summa_com
        summatotal += it.summa
        summadealtotal += it.summa_deal
        summafin += it.summa_own
      }
      if (orders)
        hsRes.payreports << [orders:orders,comsumma:comsumma,summatotal:summatotal,summafin:summafin,summadealtotal:summadealtotal,saldo:Clientsaldo.findByClient_idAndMdate(clId,hsRes.reportEnd.getTime())?.saldo?:0]
    }
    if (hsRes.payreports.size()==0) {
      def emptytemplatepath = (ConfigurationHolder.config.xlstemplate.emptyReport.path)?ConfigurationHolder.config.xlstemplate.emptyReport.path.trim():"d:/project/AR_ARENDA_2.1.2/web-app/xlstemplates/templateEmptyReport.xlsx"
      new WebXlsxExporter(emptytemplatepath).with {
        setResponseHeaders(response)
        save(response.outputStream)
      }
    } else {
      def templatepath = (ConfigurationHolder.config.xlstemplate.reportAgent.path)?ConfigurationHolder.config.xlstemplate.reportAgent.path.trim():"d:/project/AR_ARENDA_2.1.2/web-app/xlstemplates/templateReportAgent.xlsx"
      hsRes.reportMonth = message(code:'calendar.monthNameP').split(',')[requestService.getIntDef('agent_date_month',1)]
      hsRes.reportYear = requestService.getIntDef('agent_date_year',2013)
      hsRes.accountData = Accountdata.findByModstatus(1)
      hsRes.commissionPersent = Tools.getIntVal(ConfigurationHolder.config.commision.our.percent,10)
      def reportsize = hsRes.payreports[0].orders.size()
      new WebXlsxExporter(templatepath).with {
        setResponseHeaders(response)
        putCellValue(0, 12, "Приложение №3 к Публичной оферте (Агентскому договору) учетный № принципала - ${User.findByClient_id(hsRes.payreports[0].orders[0].client_id)?.id}")
        putCellValue(4, 3, "за период с \"${hsRes.reportStart.get(Calendar.DATE)}\" ${hsRes.reportMonth} ${hsRes.reportYear} г. по \"${hsRes.reportEnd.get(Calendar.DATE)}\" ${hsRes.reportMonth} ${hsRes.reportYear} г.")
        putCellValue(5, 15, "${hsRes.reportEnd.get(Calendar.DATE)} ${hsRes.reportMonth} ${hsRes.reportYear} г.")
        putCellValue(7, 0, "Мы, нижеподписавшиеся, \"Принципал\" ${Client.get(hsRes.payreports[0].orders[0].client_id)?.payee?:User.findByClient_id(hsRes.payreports[0].orders[0].client_id)?.nickname} и \"Агент\" ${hsRes.accountData.payee} в лице генерального директора ${hsRes.accountData.chief}, составили настоящий Отчет в подтверждение того что за отчетный период согласно Публичной оферте (Агентскому  договору) (далее - \"договор\") Агентом выполнены, а Принципалом приняты услуги по организации Агентом сдачи внаем объектов недвижимого имущества, принятию денежных средств от Нанимателей:")
        (14..<(reportsize+14)).eachWithIndex{ rowNumber, idx ->
          fillRow([idx+1, hsRes.payreports[0].orders[idx].plat_name?:User.get(hsRes.payreports[0].orders[0].user_id)?.nickname,
            hsRes.payreports[0].orders[idx].user_id.toString(), hsRes.payreports[0].orders[idx].norder, 
            hsRes.payreports[0].orders[idx].home_id.toString(), 
            String.format('%tm/%<td/%<tY',Trip.findByPayorder_id(hsRes.payreports[0].orders[idx].id)?.fromdate),
            String.format('%tm/%<td/%<tY',Trip.findByPayorder_id(hsRes.payreports[0].orders[idx].id)?.todate),'',
            hsRes.payreports[0].orders[idx].summa_deal, String.format('%tm/%<td/%<tY',hsRes.payreports[0].orders[idx].plat_date),
            hsRes.payreports[0].orders[idx].in_paydate?String.format('%tm/%<td/%<tY',hsRes.payreports[0].orders[idx].in_paydate):'нет',
            hsRes.payreports[0].orders[idx].in_paynumber?:'нет', hsRes.payreports[0].orders[idx].summa,
            hsRes.commissionPersent/100, hsRes.payreports[0].orders[idx].summa_com,
            hsRes.payreports[0].orders[idx].out_paydate?String.format('%tm/%<td/%<tY',hsRes.payreports[0].orders[idx].out_paydate):'нет',
            hsRes.payreports[0].orders[idx].summa_own], rowNumber, rowNumber!=(hsRes.payreports[0].orders.size()+13))
        }
        putCellValue((reportsize+14), 8, hsRes.payreports[0].summadealtotal.toString())
        putCellValue((reportsize+14), 12, hsRes.payreports[0].summatotal.toString())
        putCellValue((reportsize+14), 14, hsRes.payreports[0].comsumma.toString())
        putCellValue((reportsize+14), 16, hsRes.payreports[0].summafin.toString())
        putCellValue((reportsize+14)+2, 12, hsRes.payreports[0].summatotal.toString())
        putCellValue((reportsize+14)+4, 14, hsRes.payreports[0].comsumma.toString())
        putCellValue((reportsize+14)+6, 16, hsRes.payreports[0].saldo.toString())
        putCellValue((reportsize+14)+12, 0, "Принципал: __________________  /${Client.get(hsRes.payreports[0].orders[0].client_id)?.payee?:User.findByClient_id(hsRes.payreports[0].orders[0].client_id)?.nickname}/")
        putCellValue((reportsize+14)+12, 6, "Агент: __________________/${hsRes.accountData.chief}/")
        save(response.outputStream)
      }
    }
  }

  def payreportagentrewardXLS = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    hsRes.agentreward_date = requestService.getRaw('agentreward_date')
    hsRes.reportStart = new GregorianCalendar()
    hsRes.reportStart.setTime(hsRes.agentreward_date)
    hsRes.reportEnd = hsRes.reportStart.clone()
    hsRes.reportEnd.add(Calendar.MONTH,1)
    hsRes.reportEnd.add(Calendar.DATE,-1)
    if (hsRes.reportEnd.getTime() > new Date())
      hsRes.reportEnd.setTime(new Date())
    def lClientId = requestService.getLongDef('client_id',0)
    if (lClientId){
      hsRes.clIds = []
      hsRes.clIds << lClientId
    } else {
      hsRes.clIds = hsRes.payreport = Payorder.findAll(sort:"id"){
        modstatus > 0 &&
        plat_date >= hsRes.reportStart.getTime() &&
        plat_date < hsRes.reportEnd.getTime()+1
      }.collect{it.client_id}.unique()
    }
    hsRes.payreports = []
    hsRes.clIds.each { clId -> 
      def orders = Payorder.findAll(sort:"id"){
        client_id == clId &&
        modstatus > 0 &&
        plat_date >= hsRes.reportStart.getTime() &&
        plat_date < hsRes.reportEnd.getTime()+1
      }
      def comsumma = 0
      def summatotal = 0
      def summadealtotal = 0
      def summafin = 0
      orders.each{
        comsumma += it.summa_com
        summatotal += it.summa
        summadealtotal += it.summa_deal
        summafin += it.summa_own
      }
      if (orders)
        hsRes.payreports << [orders:orders,comsumma:comsumma,summatotal:summatotal,summafin:summafin,summadealtotal:summadealtotal,summafinWords:Tools.num2str(summafin,true),comsummaWords:Tools.num2str(comsumma,true)]
    }
    hsRes.reportMonth = message(code:'calendar.monthNameP').split(',')[requestService.getIntDef('agentreward_date_month',1)]
    hsRes.reportYear = requestService.getIntDef('agentreward_date_year',2013)
    hsRes.accountData = Accountdata.findByModstatus(1)
    hsRes.commissionPersent = Tools.getIntVal(ConfigurationHolder.config.commision.our.percent,10)

    if (hsRes.payreports.size()==0) {
      def emptytemplatepath = (ConfigurationHolder.config.xlstemplate.emptyReport.path)?ConfigurationHolder.config.xlstemplate.emptyReport.path.trim():"d:/project/AR_ARENDA_2.1.2/web-app/xlstemplates/templateEmptyReport.xlsx"
      new WebXlsxExporter(emptytemplatepath).with {
        setResponseHeaders(response)
        save(response.outputStream)
      }
    } else {
      def templatepath = (ConfigurationHolder.config.xlstemplate.reportAgentReward.path)?ConfigurationHolder.config.xlstemplate.reportAgentReward.path.trim():"d:/project/AR_ARENDA_2.1.2/web-app/xlstemplates/templateReportAgent.xlsx"
      hsRes.reportMonth = message(code:'calendar.monthNameP').split(',')[requestService.getIntDef('agentreward_date_month',1)]
      hsRes.reportYear = requestService.getIntDef('agentreward_date_year',2013)
      hsRes.accountData = Accountdata.findByModstatus(1)
      hsRes.commissionPersent = Tools.getIntVal(ConfigurationHolder.config.commision.our.percent,10)
      def reportsize = hsRes.payreports[0].orders.size()
      new WebXlsxExporter(templatepath).with {
        setResponseHeaders(response)
        putCellValue(0, 12, "Приложение №2 к Публичной оферте (Агентскому договору) учетный № принципала - ${User.findByClient_id(hsRes.payreports[0].orders[0].client_id)?.id}")
        putCellValue(4, 3, "за период с \"${hsRes.reportStart.get(Calendar.DATE)}\" ${hsRes.reportMonth} ${hsRes.reportYear} г. по \"${hsRes.reportEnd.get(Calendar.DATE)}\" ${hsRes.reportMonth} ${hsRes.reportYear} г.")
        putCellValue(5, 13, "${hsRes.reportEnd.get(Calendar.DATE)} ${hsRes.reportMonth} ${hsRes.reportYear} г.")
        putCellValue(7, 0, "Настоящий Акт составлен в подтверждение того, что \"Агентом\" ${hsRes.accountData.payee} в лице Генерального директора ${hsRes.accountData.chief} оказаны, а \"Принципалом\" ${Client.get(hsRes.payreports[0].orders[0].client_id)?.payee?:User.findByClient_id(hsRes.payreports[0].orders[0].client_id)?.nickname} приняты услуги по организации и сдачи внаем объектов недвижимого имущества  и принятию денежных средств от Нанимателей за отчетный период:")
        (14..<(reportsize+14)).eachWithIndex{ rowNumber, idx ->
          fillRow([idx+1, hsRes.payreports[0].orders[idx].plat_name?:User.get(hsRes.payreports[0].orders[0].user_id)?.nickname,
            hsRes.payreports[0].orders[idx].user_id.toString(), hsRes.payreports[0].orders[idx].norder, 
            hsRes.payreports[0].orders[idx].home_id.toString(), 
            String.format('%tm/%<td/%<tY',Trip.findByPayorder_id(hsRes.payreports[0].orders[idx].id)?.fromdate),
            String.format('%tm/%<td/%<tY',Trip.findByPayorder_id(hsRes.payreports[0].orders[idx].id)?.todate),'',
            hsRes.payreports[0].orders[idx].summa_deal, String.format('%tm/%<td/%<tY',hsRes.payreports[0].orders[idx].plat_date),
            hsRes.payreports[0].orders[idx].in_paydate?String.format('%tm/%<td/%<tY',hsRes.payreports[0].orders[idx].in_paydate):'нет',
            hsRes.payreports[0].orders[idx].in_paynumber?:'нет', hsRes.payreports[0].orders[idx].summa,
            hsRes.commissionPersent/100, hsRes.payreports[0].orders[idx].summa_com], rowNumber, rowNumber!=(hsRes.payreports[0].orders.size()+13))
        }
        putCellValue((reportsize+14), 8, hsRes.payreports[0].summadealtotal.toString())
        putCellValue((reportsize+14), 12, hsRes.payreports[0].summatotal.toString())
        putCellValue((reportsize+14)+2, 12, hsRes.payreports[0].summatotal.toString())
        putCellValue((reportsize+14)+4, 14, hsRes.payreports[0].comsumma.toString())
        putCellValue((reportsize+14)+6, 0, "Всего оказанно услуг ${hsRes.payreports[0].orders.size()} на сумму ${hsRes.payreports[0].comsumma} рублей. Вышеперечисленные услуги оказаны Агентом полностью и в срок. Агентом перечислено Принципалу сумма денежных средств в размере ${hsRes.payreports[0].summafin} руб. (${hsRes.payreports[0].summafinWords}). Агентом удержано агентское вознаграждение в размере ${hsRes.payreports[0].comsumma} руб. (${hsRes.payreports[0].comsummaWords}). Принципал претензий по объему, качеству и срокам оказания услуг претензий не имеет.")
        putCellValue((reportsize+14)+10, 0, "Принципал: __________________  /${Client.get(hsRes.payreports[0].orders[0].client_id)?.payee?:User.findByClient_id(hsRes.payreports[0].orders[0].client_id)?.nickname}/")
        putCellValue((reportsize+14)+10, 6, "Агент: __________________/${hsRes.accountData.chief}/")
        save(response.outputStream)
      }
    }
  }

  def payreporttransaction = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(null,null,null,['reportdate_start','reportdate_end'])

    def oObject = new ReportTransSearch()
    hsRes.payreportWM = oObject.csigetReport(true,hsRes.inrequest.reportdate_start?:'',hsRes.inrequest.reportdate_end?:'')
    hsRes.wmtotal = hsRes.payreportWM.sum() { it.summa }
    hsRes.wmcomtotal = hsRes.payreportWM.sum() { Math.round(it.summa*it.persent)/100 }
    hsRes.payreport = oObject.csigetReport(false,hsRes.inrequest.reportdate_start?:'',hsRes.inrequest.reportdate_end?:'')
    hsRes.total = hsRes.payreport.sum() { it.summa }
    hsRes.comtotal = hsRes.payreport.sum() { Math.round(it.summa*it.persent)/100 }
    hsRes.accountData = Accountdata.findByModstatus(1)

    renderPdf(template: 'payreporttransaction', model: hsRes, filename: "reporttransaction")
    return
  }

  def payreporttransactionXLS = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(null,null,null,['reportdate_start','reportdate_end'])

    def oObject = new ReportTransSearch()
    hsRes.payreportWM = oObject.csigetReport(true,hsRes.inrequest.reportdate_start?:'',hsRes.inrequest.reportdate_end?:'')
    hsRes.wmtotal = hsRes.payreportWM.sum() { it.summa }
    hsRes.wmcomtotal = hsRes.payreportWM.sum() { Math.round(it.summa*it.persent)/100 }
    hsRes.payreport = oObject.csigetReport(false,hsRes.inrequest.reportdate_start?:'',hsRes.inrequest.reportdate_end?:'')
    hsRes.total = hsRes.payreport.sum() { it.summa }
    hsRes.comtotal = hsRes.payreport.sum() { Math.round(it.summa*it.persent)/100 }
    hsRes.accountData = Accountdata.findByModstatus(1)

    def payreportWMsize = hsRes.payreportWM.size()
    def payreportsize = hsRes.payreport.size()
    if (payreportsize==0&&payreportWMsize==0) {
      def emptytemplatepath = (ConfigurationHolder.config.xlstemplate.emptyReport.path)?ConfigurationHolder.config.xlstemplate.emptyReport.path.trim():"d:/project/AR_ARENDA_2.1.2/web-app/xlstemplates/templateEmptyReport.xlsx"
      new WebXlsxExporter(emptytemplatepath).with {
        setResponseHeaders(response)
        save(response.outputStream)
      }
    } else {
      def templatepath = (ConfigurationHolder.config.xlstemplate.reportTransaction.path)?ConfigurationHolder.config.xlstemplate.reportTransaction.path.trim():"d:/project/AR_ARENDA_2.1.2/web-app/xlstemplates/templateReportTransaction.xlsx"
      new WebXlsxExporter(templatepath).with {
        setResponseHeaders(response)
        putCellValue(1, 0, hsRes.accountData.payee)
        (6..<(payreportWMsize+6)).eachWithIndex{ rowNumber, idx ->
          fillRow([String.format('%td/%<tm/%<tY',hsRes.payreportWM[idx].plat_date),
            hsRes.payreportWM[idx].plat_name?:User.get(hsRes.payreportWM[idx].user_id)?.nickname, hsRes.payreportWM[idx].persent, 
            hsRes.payreportWM[idx].user_id, hsRes.payreportWM[idx].norder, hsRes.payreportWM[idx].home_id, 
            String.format('%td/%<tm/%<tY',hsRes.payreportWM[idx].fromdate), String.format('%td/%<tm/%<tY',hsRes.payreportWM[idx].todate),
            hsRes.payreportWM[idx].summa, Math.round(hsRes.payreportWM[idx].summa*hsRes.payreportWM[idx].persent)/100],
            rowNumber, rowNumber!=(payreportWMsize+5))
        }
        putCellValue((payreportWMsize+6)+1, 8, hsRes.wmtotal.toString())
        putCellValue((payreportWMsize+6)+1, 9, hsRes.wmcomtotal.toString())
        ((payreportWMsize+10)..<(payreportWMsize+10+payreportsize)).eachWithIndex{ rowNumber, idx ->
          fillRow([String.format('%td/%<tm/%<tY',hsRes.payreport[idx].plat_date),
            hsRes.payreport[idx].plat_name?:User.get(hsRes.payreport[idx].user_id)?.nickname, hsRes.payreport[idx].persent/100, 
            hsRes.payreport[idx].user_id, hsRes.payreport[idx].norder, hsRes.payreport[idx].home_id, 
            String.format('%td/%<tm/%<tY',hsRes.payreport[idx].fromdate), String.format('%td/%<tm/%<tY',hsRes.payreport[idx].todate),
            hsRes.payreport[idx].summa, Math.round(hsRes.payreport[idx].summa*hsRes.payreport[idx].persent)/100],
            rowNumber, rowNumber!=(payreportWMsize+9+payreportsize))
        }
        putCellValue((payreportWMsize+10+payreportsize)+1, 8, hsRes.total.toString())
        putCellValue((payreportWMsize+10+payreportsize)+1, 9, hsRes.comtotal.toString())
        save(response.outputStream)
      }
    }
  }

  def payreportdeal = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(null,null,null,['reportdealdate_start','reportdealdate_end'])

    def oObject = new ReportTransSearch()
    hsRes.payreport = oObject.csigetDealReport(hsRes.inrequest.reportdealdate_start?:'',hsRes.inrequest.reportdealdate_end?:'')
    hsRes.total = hsRes.payreport.sum() { it.summa }
    hsRes.accountData = Accountdata.findByModstatus(1)

    renderPdf(template: 'payreportdeal', model: hsRes, filename: "reportdeal")
    return
  }

  def payreportdealXLS = {
    checkAccess(27)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=27
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(null,null,null,['reportdealdate_start','reportdealdate_end'])

    def oObject = new ReportTransSearch()
    hsRes.payreport = oObject.csigetDealReport(hsRes.inrequest.reportdealdate_start?:'',hsRes.inrequest.reportdealdate_end?:'')
    hsRes.total = hsRes.payreport.sum() { it.summa }
    hsRes.accountData = Accountdata.findByModstatus(1)

    def payreportsize = hsRes.payreport.size()
    if (payreportsize==0) {
      def emptytemplatepath = (ConfigurationHolder.config.xlstemplate.emptyReport.path)?ConfigurationHolder.config.xlstemplate.emptyReport.path.trim():"d:/project/AR_ARENDA_2.1.2/web-app/xlstemplates/templateEmptyReport.xlsx"
      new WebXlsxExporter(emptytemplatepath).with {
        setResponseHeaders(response)
        save(response.outputStream)
      }
    } else {
      def templatepath = (ConfigurationHolder.config.xlstemplate.reportDeal.path)?ConfigurationHolder.config.xlstemplate.reportDeal.path.trim():"d:/project/AR_ARENDA_2.1.2/web-app/xlstemplates/templateReportDeal.xlsx"
      new WebXlsxExporter(templatepath).with {
        setResponseHeaders(response)
        putCellValue(1, 1, hsRes.accountData.payee)
        (6..<(payreportsize+6)).eachWithIndex{ rowNumber, idx ->
          fillRow([String.format('%td/%<tm/%<tY',hsRes.payreport[idx].fromdate),String.format('%td/%<tm/%<tY',hsRes.payreport[idx].todate),
            hsRes.payreport[idx].plat_name?:User.get(hsRes.payreport[idx].user_id)?.nickname, hsRes.payreport[idx].user_id,
            Client.get(hsRes.payreport[idx].client_id)?.payee?:User.findByClient_id(hsRes.payreport[idx].client_id)?.nickname,
            User.findByClient_id(hsRes.payreport[idx].client_id)?.id, hsRes.payreport[idx].norder, hsRes.payreport[idx].home_id,
            hsRes.payreport[idx].summa], rowNumber, rowNumber!=(payreportsize+5))
        }
        putCellValue((payreportsize+6)+1, 8, hsRes.total.toString())
        putCellValue((payreportsize+6)+10, 8, hsRes.accountData.accountant)
        save(response.outputStream)
      }
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Payreport <<<///////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Trip>>>////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def trip={
    checkAccess(28)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 28
    hsRes.admin = session.admin

    return hsRes
  }

  def triplist = {
    checkAccess(28)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 28
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(['paystatus','modstatus','order'],['owner_id','user_id','home_id'])

    hsRes+=new TripAdminSearch().csiSelectTrip(hsRes.inrequest.owner_id?:0,hsRes.inrequest.user_id?:0,hsRes.inrequest.home_id?:0,
                                              hsRes.inrequest.paystatus?:0,hsRes.inrequest.modstatus?:0,hsRes.inrequest.order?:0,
                                              20,requestService.getOffset())
    return hsRes
  }

  def readtripevent={
    checkAccess(28)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 28
    hsRes.admin = session.admin

    Trip.get(requestService.getIntDef('id',0))?.readevent().save(flush:true)

    render(contentType:"application/json"){[error:false]}
  }

  def accepttrip={
    checkAccess(28)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 28
    hsRes.admin = session.admin

    def oTrip = Trip.get(requestService.getIntDef('id',0))
    if(oTrip&&oTrip?.modstatus==0){
      if(oTrip.payorder_id) {
        try {
          billingService.doTransaction( billingService.createPaytrans(oTrip,'',14) )
        } catch(Exception e) {
          log.debug('Error confirm bron for Trip #: '+oTrip.id+'\n'+e.toString())
        }
      } else {
        billingService.confirmTrip(oTrip)
      }
    }

    render(contentType:"application/json"){[error:false]}
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Trip>>>////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////

}