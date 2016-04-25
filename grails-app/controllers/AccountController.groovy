//import org.codehaus.groovy.grails.commons.grailsApplication
import grails.converters.JSON
class AccountController {
  def requestService
  def imageService
  def mailerService
  def billingService
  def smsService
  def usersService
  def pdfRenderingService
  def iRubId=857 //rub valuta_id for price conversion 
  def static final DATE_FORMAT='yyyy-MM-dd'
  
  def checkUser(hsRes) {
    if(!hsRes?.user){
      response.sendError(401)
      return false;
    }
	def oTemp_notification=Temp_notification.findWhere(id:1,status:1)	  
	session.attention_message=oTemp_notification?oTemp_notification.text:null
    return true
  }

  def checkUserAJAX(hsRes) {
    if(!hsRes?.user){
      render(contentType:"application/json"){[error:true]}
      return false
    }
    return true
  }

  def init(hsRes){
    def hsTmp=findClientId(hsRes)
    hsTmp.imageurl = grailsApplication.config.urlphoto + hsTmp.client_id.toString()+'/'
    hsTmp.billingsettings = Tools.getIntVal(grailsApplication.config.billingsettings.active,1)
    session.attention_message_once=null
    def oClient=Client.get(hsTmp?.client_id?:0)    
    if(oClient&&!(oClient?.is_notification?:0)){
      def oTemp_notification=Temp_notification.findWhere(id:4,status:1)	  
	    session.attention_message_once=oTemp_notification?oTemp_notification.text:null
      oClient.is_notification=1
      try{  
        if( !oClient.save(flush:true)) {
          log.debug(" Error on save client in personal init():")    
          oClient.errors.each{log.debug(it)}
        }
      }catch(Exception e){
        log.debug(" Error on save client in personal init(): \n"+e.toString())
      }
    }
    hsTmp.accountmenu=Infotext.findAllByItemplate_id(7,[sort:'npage',order:'asc'])
    
    hsTmp.user = User.read(hsRes.user?.id)
    
    if(hsRes.context.lang){      
      hsTmp.user = hsTmp.user.csiSetEnUser()     
    }
    
    return hsTmp
  }  
  def findClientId(hsRes){    
    def hsTmp=[:]
    hsTmp.client_id=0.toLong()	
    if(hsRes?.user?.client_id)
      hsTmp.client_id=hsRes?.user?.client_id	
    return hsTmp
  }
  
  def index = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    //hsRes.user = User.get(hsRes.user?.id)

    hsRes.notegroup = Notegroup.findAllByDb_nameNotEqual('')

    requestService.setStatistic('pcservices',38)
    return hsRes
  }

  def savenotesetting = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.user = User.get(hsRes.user?.id)

    hsRes+=requestService.getParams(['is_news','is_review','is_zayvka','is_emailzayvka','is_improve','is_postreview','is_ratingnote','is_noticeSMS','is_subscribe'])

    hsRes.user.is_news = hsRes.inrequest?.is_news?:0
    hsRes.user.is_review = hsRes.inrequest?.is_review?:0
    hsRes.user.is_zayvka = hsRes.inrequest?.is_zayvka?:0
    hsRes.user.is_emailzayvka = hsRes.inrequest?.is_zayvka?(hsRes.inrequest?.is_emailzayvka?:0):0
    hsRes.user.is_improve = hsRes.inrequest?.is_improve?:0
    hsRes.user.is_postreview = hsRes.inrequest?.is_postreview?:0
    hsRes.user.is_ratingnote = hsRes.inrequest?.is_ratingnote?:0
    hsRes.user.is_noticeSMS = hsRes.inrequest?.is_noticeSMS?:0
    hsRes.user.is_subscribe = hsRes.inrequest?.is_subscribe?:0
    

    if(!hsRes.user.save(flush:true)) {
      log.debug(" Error on save user. in Account:savenotesetting:")
      hsRes.user.errors.each{log.debug(it)}
    }

    redirect(action:'index', base:hsRes.context.mainserverURL_lang)
    return
  }
  
  def payout = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return

    hsRes+=init(hsRes)
    //hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    hsRes.country = Country.findAllByIs_reserve(1)
    hsRes.valuta = Valuta.findAllByModstatus(1)
    hsRes.paycountry = Paycountry.findAllByCountry_id(hsRes.client?.country_id?:hsRes.country[0]?.id?:0)
    hsRes.reserve = Reserve.findAllByModstatusAndIs_mainInList(1,hsRes.client?.type_id!=2?[1]:[0,1])

    return hsRes
  }

  def offerprint = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    def iId=requestService.getStr('offer_id')
    hsRes.text=Infotext.findByControllerAndAction('account','payout')['itext'+iId+hsRes.context?.lang]

    return hsRes
  }

  def paydescription = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)

    hsRes+=requestService.getParams(['countryId','payaccount'])
    def sDescription = Paycountry.findByCountry_idAndPayaccount_id(hsRes.inrequest?.countryId?:0,hsRes.inrequest?.payaccount?:0)

    render sDescription.description?:''
  }

  def payaccounts = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)

    hsRes+=requestService.getParams(['countryId'])
    hsRes.paycountry = Paycountry.findAllByCountry_id(hsRes.inrequest?.countryId?:0)

    return hsRes
  }

  def payscheme = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)

    hsRes.reserve = Reserve.findAllByModstatusAndIs_mainInList(1,requestService.getIntDef('id',0)!=2?[1]:[0,1])
    return hsRes
  }

  def sendVerifyTel={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!checkUser(hsRes))
      return

    hsRes.user = User.get(hsRes.user.id)
    def isSMSsend = Sms.isSMSsend(hsRes.user?.tel?:'')
    def readyToSms = false
    if (!isSMSsend) {
      readyToSms = hsRes.user.validateTelNumber()//генерация кода + TODO??:проверка допустимых стран и телефонов.
    }


    if (readyToSms && !isSMSsend)
      render(contentType:"application/json"){[error:(smsService.sendVerifySms(hsRes.user) as boolean)]}
    else
      render(contentType:"application/json"){[error:true]}
    return
  }

  def findbank = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!checkUserAJAX(hsRes)) return

    hsRes+=requestService.getParams(null,null,['bik','bankname'])

    hsRes.result = Bik.findAll {
      bik =~ ('%'+(hsRes.inrequest.bik?:'')+'%') &&
      bankname =~ ('%'+(hsRes.inrequest.bankname?:'')+'%')
    }

    return hsRes
  }

  def savepayoutdetails = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!checkUserAJAX(hsRes)) return
    hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0)
    if (!hsRes.client) render(contentType:"application/json"){[error:true]}

    hsRes+=requestService.getParams(['payaccount_id','reserve_id','country_id','nds','type_id','confirmdogovor','smscode','is_presave'],null,
                                    ['inn','bankname','kpp','bik','corraccount','payee','settlaccount','paycomment',
                                      'webmoney','yandex','yandex2','webmoney2','pcard','settlprocedure'])
    def lsDirectory = requestService.getParams(['reserve_id','country_id','type_id']).inrequest

    hsRes.error = [:]
    hsRes.error.errorsList = []
    if(lsDirectory?.size()<3)
      hsRes.error.errorsList<<8
    if (!hsRes.inrequest.confirmdogovor) {
      hsRes.error.errorsList<<9
    }
    if (!hsRes.inrequest.is_presave) {
      if (hsRes.user.smscode != hsRes.inrequest.smscode.toString()) {
        hsRes.error.errorsList<<11
      }
    }
    if(!(hsRes.inrequest?.reserve_id in 3..4)){
      if(!hsRes.inrequest.payaccount_id)
        hsRes.error.errorsList << 13
      if (hsRes.inrequest.payaccount_id==1) {
        if(!hsRes.inrequest?.payee)
          hsRes.error.errorsList<<1
        if(hsRes.inrequest.type_id!=1&&!hsRes.inrequest.inn)
          hsRes.error.errorsList<<2
        if(hsRes.inrequest.inn&&!hsRes.inrequest?.inn?.matches('\\d{10}|\\d{12}'))
          hsRes.error.errorsList<<12
        if(hsRes.inrequest?.kpp&&!hsRes.inrequest.kpp.matches('\\d{9}')&&(hsRes.inrequest.type_id?:0)==2)
          hsRes.error.errorsList<<3
        if(!hsRes.inrequest?.bankname)
          hsRes.error.errorsList<<4
        if(!hsRes.inrequest?.bik?.matches('04\\d{7}'))
          hsRes.error.errorsList<<5
        if(!hsRes.inrequest?.corraccount?.matches('\\d{20,25}'))
          hsRes.error.errorsList<<6
        if(!hsRes.inrequest?.settlaccount?.matches('\\d{20,25}'))
          hsRes.error.errorsList<<7
      }
      if (hsRes.inrequest.payaccount_id==2) {
        if (hsRes.client.yandex!=hsRes.inrequest.yandex&&hsRes.inrequest.yandex!=hsRes.inrequest.yandex2) {
          hsRes.error.errorsList<<10
        }
      }
      if (hsRes.inrequest.payaccount_id==3) {
        if ((hsRes.client.webmoney!=hsRes.inrequest.webmoney&&hsRes.inrequest.webmoney!=hsRes.inrequest.webmoney2)||!hsRes.inrequest.webmoney) {
          hsRes.error.errorsList<<10
        }
      }
    }
    if (!hsRes.error.errorsList) {
      try {
        if (!hsRes.inrequest.is_presave) {
          hsRes.client.payoutDetailsUpdate(hsRes.inrequest,request.remoteAddr)
          if (hsRes.inrequest.reserve_id in 3..4)
            hsRes.client.cleanDetails()
          else if (hsRes.inrequest.payaccount_id==1)
            hsRes.client.billingDetailsUpdate(hsRes.inrequest)
          else if (hsRes.inrequest.payaccount_id==2)
            hsRes.client.yandexDetailsUpdate(hsRes.inrequest)
          else if (hsRes.inrequest.payaccount_id==3)
            hsRes.client.webmoneyDetailsUpdate(hsRes.inrequest)

          def oClienthistory=new Clienthistory()
          oClienthistory.setData(hsRes.client,1)

          if(hsRes.inrequest.reserve_id in 3..4) mailerService.sendConfirmPayoutMail(hsRes.client)
          else mailerService.sendChangePayoutSchemeMails(hsRes.client,hsRes.user)
        } else {
          new Clienthistory().preSaveData(hsRes.client,hsRes.inrequest,request.remoteAddr)
        }
      } catch(Exception e) {
        log.debug("Cannot update payout details for client: "+hsRes.client.id+"\n"+e.toString()+' in account/savepayoutdetails')
        hsRes.error.errorsList<<101
      }
    }
    if (hsRes.error.errorsList||hsRes.inrequest.is_presave) {
      render hsRes.error as JSON
    }

    render(contentType:"application/json"){[error:false]}
  }
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////payorder history>>>/////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
  def history = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    //hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0l)
    hsRes.account = Account.findByClient_id(hsRes.client?.id?:0l)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

    return hsRes
  }

  def payorderbronhistory = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    //hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0l)
    hsRes.payorderlist = Payorder.findAllByClient_idAndModstatusGreaterThan(hsRes.client?.id?:0l,0,[offset:requestService.getOffset(),max:10,order:'desc',sort:'id'])
    hsRes.payorderlistcount = Payorder.countByClient_idAndModstatusGreaterThan(hsRes.client?.id?:0l,0)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

    return hsRes
  }

  def payorderdetail = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    //hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0l)
    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.payorder = Payorder.find { modstatus > 0 && client_id == (hsRes.client?.id?:0l) && norder == (hsRes.inrequest?.id?:'') }
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.paytrans = Paytrans.findAllByPayorder_id(hsRes.payorder.id,[sort:'id',order:'desc'])
    hsRes.home  = Home.get(hsRes.payorder.home_id)
    hsRes.guest  = User.get(hsRes.payorder.user_id)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

    return hsRes
  }

  def payorderacthistory = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    //hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0l)
    hsRes.payactlist = Payact.findAllByClient_id(hsRes.client?.id?:0,[offset:requestService.getOffset(),max:10,order:'desc',sort:'id'])
    hsRes.payactlistcount = Payact.countByClient_id(hsRes.client?.id?:0)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

    return hsRes
  }

////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////<<<payorder history/////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////trip payment history>>>/////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
  def trippayment = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    //hsRes.user = User.get(hsRes.user?.id)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

    return hsRes
  }

  def trippaymenthistorylist = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    //hsRes.user = User.get(hsRes.user?.id)
    hsRes.payorderlist = Payorder.findAllByUser_idAndModstatusGreaterThan(hsRes.user?.id?:0l,0,[offset:requestService.getOffset(),max:10,order:'desc',sort:'id'])
    hsRes.payorderlistcount = Payorder.countByUser_idAndModstatusGreaterThan(hsRes.user?.id?:0l,0)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

    return hsRes
  }

////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////<<<trip payment history/////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
  def refferals = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)  
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)	
    //hsRes.user = User.get(hsRes.user?.id)
    
    return hsRes	  
  }

  def settings = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    //hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0)

    return hsRes
  }

  def savebillingdetails = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!checkUserAJAX(hsRes)) return
    hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0)
    if (!hsRes.client) render(contentType:"application/json"){[error:true]}

    hsRes+=requestService.getParams(['nds'],null,['inn','bankname','kpp','bik','corraccount','payee','margin','settlaccount','paymessage','paycomment'])
    hsRes.error = [:]
    hsRes.error.errorsList = []
    if(!hsRes.inrequest?.payee)
      hsRes.error.errorsList<<1
    if(!hsRes.inrequest.inn)
      hsRes.error.errorsList<<2
    if(hsRes.inrequest?.kpp&&!hsRes.inrequest.kpp.matches('\\d{9}'))
      hsRes.error.errorsList<<3
    if(!hsRes.inrequest?.bankname)
      hsRes.error.errorsList<<4
    if(!hsRes.inrequest?.bik?.matches('\\d{9}'))
      hsRes.error.errorsList<<5
    if(!hsRes.inrequest?.corraccount?.matches('\\d{20}'))
      hsRes.error.errorsList<<6
    if(!hsRes.inrequest?.settlaccount?.matches('\\d{20}'))
      hsRes.error.errorsList<<7

    if (!hsRes.error.errorsList) {
      try {
        hsRes.client.billingDetailsUpdate(hsRes.inrequest)
      } catch(Exception e) {
        log.debug("Cannot update Billing details for client: "+hsRes.client.id+"\n"+e.toString()+' in account/savebillingdetails')
        hsRes.error.errorsList<<101
      }
    }
    if (hsRes.error.errorsList) {
      render hsRes.error as JSON
    }

    render(contentType:"application/json"){[error:false]}
  }
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////partner program/////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
  def partner = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return

    hsRes+=init(hsRes)

    hsRes.client = Client.get(hsRes.user.client_id?:0)
    hsRes.partnerway = Partnerway.findAllByModstatus(1)

    return hsRes
  }

  def partnerofferprint = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return

    hsRes.text=Infotext.findByControllerAndAction('account','partner')['itext'+hsRes.context?.lang]

    render(view: "offerprint", model: hsRes)
    return
  }

  def partnerconfirmation = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!checkUserAJAX(hsRes)) return
    hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0)
    if (!hsRes.client||hsRes.client.partnerstatus==2) render(contentType:"application/json"){[error:true]}

    hsRes+=requestService.getParams(['confirmdogovor','partnerway_id'],null,['prequisite'])

    hsRes.result = [:]
    hsRes.result.errorsList = []
    if (!hsRes.inrequest.confirmdogovor)
      hsRes.result.errorsList<<1
    if (!hsRes.inrequest.partnerway_id)
      hsRes.result.errorsList<<2
    else if (hsRes.inrequest.partnerway_id!=1&&!hsRes.inrequest.prequisite)
      hsRes.result.errorsList<<3

    if (!hsRes.result.errorsList) {
      try {
        if(hsRes.client.partnerstatus==0) mailerService.sendStartPartnerMail(hsRes.client,hsRes.inrequest.partnerway_id)
        hsRes.client.csiSetPatnerway(hsRes.inrequest.partnerway_id).csiSetPrequisite(hsRes.inrequest.prequisite).csiSetPatnerstatus(1).save(failOnError:true)
        hsRes.result.where = hsRes.client.partnerway_id==1?createLink(controller:'account',action: 'paypal',id:(billingService.createPartnerConfirmationOrder(userip:request.remoteAddr,user:hsRes.user,client:hsRes.client,payway:7)?.norder?:''),absolute:true):''
      } catch(Exception e) {
        log.debug("Cannot update partner status for client: "+hsRes.client.id+"\n"+e.toString()+' in account/partnerconfirmation')
        hsRes.result.errorsList<<101
      }
    }

    render hsRes.result as JSON
    return
  }

  def recallpartner = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!checkUserAJAX(hsRes)) return
    hsRes.user = User.get(hsRes.user?.id)
    hsRes.client = Client.get(hsRes.user.client_id?:0)
    if (!hsRes.client||hsRes.client.partnerstatus==0) render(contentType:"application/json"){[error:true]}

    try {
      hsRes.client.csiSetPatnerstatus(0).clearPartnerData().save(failOnError:true)
    } catch(Exception e) {
      log.debug("Cannot update partner status for client: "+hsRes.client.id+"\n"+e.toString()+' in account/recallpartner')
    }

    render(contentType:"application/json"){[error:false]}
  }
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////payorder documents//////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
  def paydocuments = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return    

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,hsRes.inrequest?.id?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    return hsRes
  }

  def payorderdocprint = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return    

    hsRes+=requestService.getParams(['type'],null,['payer','payeraddress','payerinn','payeraccount','payprice','order_id','payerokpo','payerbik','payerbank','payercorchet'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,hsRes.inrequest?.order_id?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    billingService.updatePayorderPlatInfo(hsRes.payorder,hsRes.inrequest)
    hsRes.client = Accountdata.get(1)
    hsRes.priceASstring = Tools.num2str(hsRes.payorder.summa,false)
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)

    if (hsRes.inrequest?.type) {
      render file: pdfRenderingService.render(template: "payorderdocprint", model: hsRes, controller:this).toByteArray(), filename: "platStaytoday", contentType: "application/pdf"
      return
    } else {
      return hsRes
    }
  }

////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////agr pages///////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////

  def liqpay = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return    

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,hsRes.inrequest?.id?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.configParams = [
      password:grailsApplication.config.liqpay.password?grailsApplication.config.liqpay.password.trim():'i35361y272ywp5l',
      merid:grailsApplication.config.liqpay.merid?grailsApplication.config.liqpay.merid.trim():'I0MO0A98',
      acqid:grailsApplication.config.liqpay.acqid?grailsApplication.config.liqpay.acqid.trim():'469584'
    ]
    hsRes.accdata = Accountdata.get(1)
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.orderdescription = hsRes.accdata.paycomment.replace('[@NORDER]',hsRes.payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_end))

    hsRes.purchaseamt = hsRes.context.is_dev?'000000000100':Tools.generatePriceForliqpay(hsRes.payorder.summa)
    hsRes.signature = (hsRes.configParams.password+hsRes.configParams.merid+hsRes.configParams.acqid+hsRes.payorder.norder+hsRes.purchaseamt+'643'+hsRes.orderdescription).encodeAsSHA1Bytes().encodeAsBase64()
    return hsRes
  }

  def liqpayresult = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    hsRes+=requestService.getParams(['responsecode'],null,['reasoncode','orderid','signature','reasoncodedesc'])
    hsRes.configParams = [
      password:grailsApplication.config.liqpay.password?grailsApplication.config.liqpay.password.trim():'i35361y272ywp5l',
      merid:grailsApplication.config.liqpay.merid?grailsApplication.config.liqpay.merid.trim():'I0MO0A98',
      acqid:grailsApplication.config.liqpay.acqid?grailsApplication.config.liqpay.acqid.trim():'469584'
    ]
    hsRes.signature = (hsRes.configParams.password+hsRes.configParams.merid+hsRes.configParams.acqid+hsRes.inrequest.orderid+hsRes.inrequest.responsecode+hsRes.inrequest.reasoncode+hsRes.inrequest.reasoncodedesc).encodeAsSHA1Bytes().encodeAsBase64()
    hsRes.payorder = Payorder.findByNorder(hsRes.inrequest?.orderid?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    try {
      if(hsRes.inrequest.signature==hsRes.signature&&hsRes.inrequest.responsecode==1&&hsRes.inrequest.reasoncode=='1') {
        billingService.doTransaction( billingService.createPaytrans(hsRes.payorder,hsRes.inrequest.reasoncodedesc,2) )
        hsRes.is_succes = 1
      } else if(hsRes.inrequest.signature==hsRes.signature){
        billingService.createPaytrans([payorder:hsRes.payorder,retcode:hsRes.inrequest.reasoncode],hsRes.inrequest.reasoncodedesc,20)
        hsRes.errortext = Payerrorcode.findByAgr_idAndReasoncode(3,hsRes.inrequest.reasoncode?:'36')?.clienttext
        hsRes.is_succes = 0
      } else {
        hsRes.is_succes = -1
      }
    } catch(Exception e) {
      log.error("AccountController:liqpayresult:\n"+e.toString())
      hsRes.is_succes = 2
    }
    return hsRes
  }

  def payU = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return    

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,hsRes.inrequest?.id?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.configParams = [
      secretKey:grailsApplication.config.payu.secretKey?grailsApplication.config.payu.secretKey.trim():'!F6[bz*5a6b2++Q3EA7@',
      merchant:grailsApplication.config.payu.merchant?grailsApplication.config.payu.merchant.trim():'staytodq'
    ]
    hsRes.accdata = Accountdata.get(1)
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.payway = Payway.get(hsRes.payorder.payway_id)
    hsRes.orderdescription = hsRes.accdata.paycomment.replace('[@NORDER]',hsRes.payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_end))
    hsRes.orderdate = String.format('%tF %<tT', new Date())
    def str = ''
    str += hsRes.configParams.merchant.getBytes("UTF-8").length+hsRes.configParams.merchant
    str += hsRes.payorder.norder.getBytes("UTF-8").length+hsRes.payorder.norder
    str += hsRes.orderdate.getBytes("UTF-8").length+hsRes.orderdate
    str += hsRes.orderdescription.getBytes("UTF-8").length+hsRes.orderdescription
    str += hsRes.payorder.norder.getBytes("UTF-8").length+hsRes.payorder.norder
    str += hsRes.payorder.summa.toString().getBytes("UTF-8").length+hsRes.payorder.summa.toString()
    str += '1'.getBytes("UTF-8").length+'1'
    str += '0'.getBytes("UTF-8").length+'0'
    str += '0'.getBytes("UTF-8").length+'0'
    str += 'RUB'.getBytes("UTF-8").length+'RUB'
    str += (hsRes.payway?.payumethod?:'CCVISAMC').getBytes("UTF-8").length+(hsRes.payway?.payumethod?:'CCVISAMC')
    str += 'NET'.getBytes("UTF-8").length+'NET'
    hsRes.signature = Tools.generateHmacMD5(str,hsRes.configParams.secretKey).encodeAsHex()

    return hsRes
  }

  def payUresult = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    hsRes+=requestService.getParams(null,null,['ctrl','payrefno'])
    hsRes.result = requestService.getIntDef('result',0)
    hsRes.configParams = [
      secretKey:grailsApplication.config.payu.secretKey?grailsApplication.config.payu.secretKey.trim():'!F6[bz*5a6b2++Q3EA7@',
      merchant:grailsApplication.config.payu.merchant?grailsApplication.config.payu.merchant.trim():'staytodq'
    ]
    def curUrl  = request.requestURL.toString()-'.dispatch'-'/grails'+'?'+request.getQueryString().toString()-('&ctrl='+hsRes.inrequest.ctrl)
    if(!(Tools.generateHmacMD5(curUrl.getBytes("UTF-8").length+curUrl,hsRes.configParams.secretKey).encodeAsHex()==hsRes.inrequest.ctrl)){
      response.sendError(404)
      return
    }

    return hsRes
  }

  def payuipn = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)

    hsRes+=requestService.getParams(null,null,['REFNO','REFNOEXT','ORDERSTATUS','CURRENCY','IPN_PID[]','IPN_PNAME[]','IPN_TOTALGENERAL','IPN_DATE','HASH'])

    hsRes.configParams = [
      secretKey:grailsApplication.config.payu.secretKey?grailsApplication.config.payu.secretKey.trim():'!F6[bz*5a6b2++Q3EA7@',
      merchant:grailsApplication.config.payu.merchant?grailsApplication.config.payu.merchant.trim():'staytodq'
    ]
    def curtime = String.format('%tY%<tm%<td%<tH%<tM%<tS',new Date())
    def reqstr = ''
    reqstr += (hsRes.inrequest.'REFNO'?:'test').getBytes("UTF-8").length+(hsRes.inrequest.'REFNO'?:'test')
    reqstr += (hsRes.inrequest.'REFNOEXT'?:'test').getBytes("UTF-8").length+(hsRes.inrequest.'REFNOEXT'?:'test')
    reqstr += (hsRes.inrequest.'ORDERSTATUS'?:'test').getBytes("UTF-8").length+(hsRes.inrequest.'ORDERSTATUS'?:'test')
    reqstr += (hsRes.inrequest.'CURRENCY'?:'test').getBytes("UTF-8").length+(hsRes.inrequest.'CURRENCY'?:'test')
    reqstr += (hsRes.inrequest.'IPN_PID[]'?:'test').getBytes("UTF-8").length+(hsRes.inrequest.'IPN_PID[]'?:'test')
    reqstr += (hsRes.inrequest.'IPN_PNAME[]'?:'test').getBytes("UTF-8").length+(hsRes.inrequest.'IPN_PNAME[]'?:'test')
    reqstr += (hsRes.inrequest.'IPN_TOTALGENERAL'?:'test').getBytes("UTF-8").length+(hsRes.inrequest.'IPN_TOTALGENERAL'?:'test')
    reqstr += (hsRes.inrequest.'IPN_DATE'?:curtime).getBytes("UTF-8").length+(hsRes.inrequest.'IPN_DATE'?:curtime)
    hsRes.reqsignature = Tools.generateHmacMD5(reqstr,hsRes.configParams.secretKey).encodeAsHex()
    if (hsRes.reqsignature==hsRes.inrequest.HASH) {
      hsRes.payorder = Payorder.findByNorder(hsRes.inrequest?.'REFNOEXT'?:'')
      if (hsRes.payorder) {
        try {
          billingService.doTransaction( billingService.createPaytrans(hsRes.payorder,hsRes.inrequest.'REFNO'?:'',2) )
        } catch(Exception e) {
          log.error("AccountController:payuipn:\n"+e.toString())
        }
      }
      log.debug('Valid hash')
    } else {
      log.debug('Bad hash: '+hsRes.reqsignature+' != '+hsRes.inrequest.HASH)
    }

    def respstr = ''
    respstr += (hsRes.inrequest.'IPN_PID[]'?:'test').getBytes("UTF-8").length+(hsRes.inrequest.'IPN_PID[]'?:'test')
    respstr += (hsRes.inrequest.'IPN_PNAME[]'?:'test').getBytes("UTF-8").length+(hsRes.inrequest.'IPN_PNAME[]'?:'test')
    respstr += (hsRes.inrequest.'IPN_DATE'?:curtime).getBytes("UTF-8").length+(hsRes.inrequest.'IPN_DATE'?:curtime)
    respstr += curtime.getBytes("UTF-8").length+curtime
    hsRes.signature = Tools.generateHmacMD5(respstr,hsRes.configParams.secretKey).encodeAsHex()
    hsRes.result = '<EPAYMENT>'+curtime+'|'+hsRes.signature+'</EPAYMENT>'

    render hsRes.result
  }

  def wmoney = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,hsRes.inrequest?.id?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.configParams = [
      secretKey:grailsApplication.config.wmoney.secretKey?grailsApplication.config.wmoney.secretKey.trim():'8D38F03EE4415A6E0199AB6D66051F64',
      merchant:grailsApplication.config.wmoney.merchant?grailsApplication.config.wmoney.merchant.trim():'R189136258947',
      testmode:Tools.getIntVal(grailsApplication.config.wmoney.testmode,1)
    ]
    hsRes.accdata = Accountdata.get(1)
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.purchaseamt = hsRes.configParams.testmode?1f:(hsRes.payorder.summa as float)
    hsRes.orderdescription = hsRes.accdata.paycomment.replace('[@NORDER]',hsRes.payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_end)).getBytes("UTF-8").encodeAsBase64()
    return hsRes
  }

  def wmfail = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    hsRes+=requestService.getParams(null,null,['LMI_PAYMENT_NO'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,(hsRes.inrequest?.'LMI_PAYMENT_NO'?'st'+hsRes.inrequest?.'LMI_PAYMENT_NO':''))
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    return hsRes
  }

  def wmsuccess = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    hsRes+=requestService.getParams(null,null,['LMI_PAYMENT_NO'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,(hsRes.inrequest?.'LMI_PAYMENT_NO'?'st'+hsRes.inrequest?.'LMI_PAYMENT_NO':''))
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    return hsRes
  }

  def wmresult = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes+=requestService.getParams(null,null,['LMI_PAYMENT_NO','LMI_PAYEE_PURSE','LMI_PAYMENT_AMOUNT','LMI_MODE',
                                      'LMI_SYS_INVS_NO','LMI_SYS_TRANS_NO','LMI_SYS_TRANS_DATE','LMI_PAYER_PURSE',
                                      'LMI_PAYER_WM','LMI_HASH'])
    if (!hsRes.inrequest?.'LMI_PAYMENT_NO') {
      render ""
      return
    }
    hsRes.payorder = Payorder.findByNorder((hsRes.inrequest?.'LMI_PAYMENT_NO'?'st'+hsRes.inrequest?.'LMI_PAYMENT_NO':''))
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.configParams = [
      secretKey:grailsApplication.config.wmoney.secretKey?grailsApplication.config.wmoney.secretKey.trim():'8D38F03EE4415A6E0199AB6D66051F64',
      merchant:grailsApplication.config.wmoney.merchant?grailsApplication.config.wmoney.merchant.trim():'R189136258947',
      testmode:Tools.getIntVal(grailsApplication.config.wmoney.testmode,1)
    ]
    hsRes.purchaseamt = hsRes.configParams.testmode?1f:(hsRes.payorder.summa as float)
    def requestStr = ''
    requestStr += hsRes.inrequest.LMI_PAYEE_PURSE?:''
    requestStr += hsRes.inrequest.LMI_PAYMENT_AMOUNT?:''
    requestStr += hsRes.inrequest.LMI_PAYMENT_NO?:''
    requestStr += hsRes.inrequest.LMI_MODE?:''
    requestStr += hsRes.inrequest.LMI_SYS_INVS_NO?:''
    requestStr += hsRes.inrequest.LMI_SYS_TRANS_NO?:''
    requestStr += hsRes.inrequest.LMI_SYS_TRANS_DATE?:''
    requestStr += hsRes.configParams.secretKey
    requestStr += hsRes.inrequest.LMI_PAYER_PURSE?:''
    requestStr += hsRes.inrequest.LMI_PAYER_WM?:''

    if (requestStr.encodeAsMD5().toUpperCase()==hsRes.inrequest.LMI_HASH
        &&hsRes.inrequest.LMI_MODE==hsRes.configParams.testmode.toString()
        &&hsRes.inrequest.LMI_PAYEE_PURSE==hsRes.configParams.merchant
        &&(hsRes.inrequest.LMI_PAYMENT_AMOUNT as float)==hsRes.purchaseamt) {
      billingService.doTransaction( billingService.createPaytrans(hsRes.payorder,hsRes.inrequest.LMI_SYS_TRANS_NO?:'',2) )
      log.debug('Wm payment is complete')
    } else {
      log.debug('InCorrect Wm payment')
      response.sendError(404)
      return
    }

    render "YES"
    return
  }

  def paypal = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,hsRes.inrequest?.id?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.configParams = [
      user:grailsApplication.config.paypal.user?grailsApplication.config.paypal.user.trim():'info-facilitator_api1.staytoday.ru',
      pwd:grailsApplication.config.paypal.pwd?grailsApplication.config.paypal.pwd.trim():'KJRYE3PSUR4H856N',
      signature:grailsApplication.config.paypal.signature?grailsApplication.config.paypal.signature.trim():'AiCy60LSzMTxiUQ7My4hwo61JyRxAjfML0HRrNVYvJw.AubVWr2-L6-D',
      testmode:Tools.getIntVal(grailsApplication.config.paypal.testmode,1),
      paymenturl:Tools.getIntVal(grailsApplication.config.paypal.testmode,1)?'https://www.sandbox.paypal.com/cgi-bin/webscr':'https://www.paypal.com/cgi-bin/webscr',
      retURL:g.createLink(controller:'account',action:'ppsuccess',base:hsRes.context.sequreServerURL),
      cancURL:g.createLink(controller:'account',action:'ppfail',base:hsRes.context.sequreServerURL)
    ]
    hsRes.accdata = Accountdata.get(1)
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.purchaseamt = hsRes.configParams.testmode?1f:(hsRes.payorder.summa as float)
    hsRes.orderdescription = hsRes.payorder.norder[3]=='2'?'Подтверждающий платеж':hsRes.accdata.paycomment.replace('[@NORDER]',hsRes.payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_end))

    hsRes.ppresponse = smsService.paypal_SetExpressCheckout(hsRes)

    return hsRes
  }

  def ppsuccess = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes+=requestService.getParams(null,null,['PayerID','token'])
    if (!hsRes.inrequest.token) {
      response.sendError(404)
      return
    }

    hsRes.configParams = [
      user:grailsApplication.config.paypal.user?grailsApplication.config.paypal.user.trim():'info-facilitator_api1.staytoday.ru',
      pwd:grailsApplication.config.paypal.pwd?grailsApplication.config.paypal.pwd.trim():'KJRYE3PSUR4H856N',
      signature:grailsApplication.config.paypal.signature?grailsApplication.config.paypal.signature.trim():'AiCy60LSzMTxiUQ7My4hwo61JyRxAjfML0HRrNVYvJw.AubVWr2-L6-D',
      testmode:Tools.getIntVal(grailsApplication.config.paypal.testmode,1),
      paymenturl:Tools.getIntVal(grailsApplication.config.paypal.testmode,1)?'https://www.sandbox.paypal.com/cgi-bin/webscr':'https://www.paypal.com/cgi-bin/webscr'
    ]

    hsRes.paymentdetails = smsService.paypal_GetExpressCheckoutDetails(hsRes)

    hsRes.payorder = Payorder.findByNorder(hsRes.paymentdetails?.INVNUM?'st'+hsRes.paymentdetails.INVNUM:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }

    hsRes.paymenttype = hsRes.payorder.norder[3]=='2'?2:1
    def doResponse = smsService.paypal_DoExpressCheckoutPayment(hsRes)
    if (doResponse?.ACK=="Success") {
      billingService.doTransaction( billingService.createPaytrans(hsRes.payorder,doResponse.PAYMENTINFO_0_TRANSACTIONID?:'',hsRes.paymenttype==1?2:24) )
      if (hsRes.paymenttype==2){
        Client.get(User.get(hsRes.payorder.user_id)?.client_id?:0)?.updatePPdata(hsRes.paymentdetails.EMAIL,hsRes.paymentdetails.PAYERID)?.save(flush:true)
        mailerService.sendConfirmPartnerMail(User.get(hsRes.payorder.user_id))
      }
      log.debug('Paypal payment is complete')
    } else {
      log.debug('InCorrect Paypal payment')
      response.sendError(404)
      return
    }

    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    return hsRes
  }

  def ppfail = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    hsRes+=requestService.getParams(null,null,['PayerID','token'])
    if (!hsRes.inrequest.token) {
      response.sendError(404)
      return
    }

    hsRes.configParams = [
      user:grailsApplication.config.paypal.user?grailsApplication.config.paypal.user.trim():'info-facilitator_api1.staytoday.ru',
      pwd:grailsApplication.config.paypal.pwd?grailsApplication.config.paypal.pwd.trim():'KJRYE3PSUR4H856N',
      signature:grailsApplication.config.paypal.signature?grailsApplication.config.paypal.signature.trim():'AiCy60LSzMTxiUQ7My4hwo61JyRxAjfML0HRrNVYvJw.AubVWr2-L6-D',
      testmode:Tools.getIntVal(grailsApplication.config.paypal.testmode,1),
      paymenturl:Tools.getIntVal(grailsApplication.config.paypal.testmode,1)?'https://www.sandbox.paypal.com/cgi-bin/webscr':'https://www.paypal.com/cgi-bin/webscr'
    ]

    hsRes.paymentdetails = smsService.paypal_GetExpressCheckoutDetails(hsRes)

    hsRes.payorder = Payorder.findByNorder(hsRes.paymentdetails?.INVNUM?'st'+hsRes.paymentdetails.INVNUM:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }

    hsRes.paymenttype = hsRes.payorder.norder[3]=='2'?2:1
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    return hsRes
  }
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////external payment////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////

  def payment = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes+=init(hsRes)

    def lId=requestService.getLongDef('id',0)
    def sCode = requestService.getStr('hash')

    Long lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id?:0

    hsRes.mboxRec = Mboxrec.get(lId)
    hsRes.mbox = Mbox.get(hsRes.mboxRec?.mbox_id)
    if (!hsRes.mboxRec||!hsRes.mbox||sCode!=Tools.generateModeParam(hsRes.mboxRec?.id,hsRes.mbox?.id)) {
      response.sendError(404)
      return
    }

    if (hsRes.mboxRec.answertype_id in 1..2&&hsRes.mbox.modstatus==3&&!hsRes.user&&usersService.loginInternalUser('','',requestService,1,hsRes.mbox.user_id)){
      hsRes.user = User.get(hsRes.mbox.user_id)
    }

    hsRes.homeperson=Homeperson.get(hsRes.mboxRec?.homeperson_id)
    hsRes.home=Home.read(hsRes.mboxRec?.home_id)
    hsRes.ownerUser = User.findWhere(client_id:hsRes.home.client_id)
    hsRes.date_start=String.format('%td/%<tm/%<tY',hsRes.mboxRec?.date_start)
    hsRes.date_end=String.format('%td/%<tm/%<tY',hsRes.mboxRec?.date_end)
    hsRes.moddate=String.format('%td/%<tm/%<tY %<tH:%<tM',hsRes.mbox?.moddate)
    hsRes.cancellation=Rule_cancellation.get(hsRes.home?.rule_cancellation_id)
    hsRes.timein=Rule_timein.get(hsRes.home?.rule_timein_id?:0)['name'+hsRes.context?.lang]
    hsRes.timeout=Rule_timeout.get(hsRes.home?.rule_timeout_id?:0)['name'+hsRes.context?.lang]
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    hsRes.ownerClient = Client.get(hsRes.mbox.homeowner_cl_id)
    hsRes.resstatModifier = 1.0
    hsRes.ispaypossible = (hsRes.ownerClient?.resstatus==1)
    hsRes.invoicelife = Tools.getIntVal(grailsApplication.config.payorder.invoicelife.days,5)
    if (hsRes.ispaypossible) {
      hsRes.resstatModifier = hsRes.resstatModifier + (Tools.getIntVal(grailsApplication.config.clientPrice.modifier,4) / 100)
    }
    hsRes.displayPrice = Math.round(hsRes.mboxRec.price_rub / hsRes.valutaRates * hsRes.resstatModifier)
    hsRes.payway = Payway.findAllByModstatus(1)
    hsRes.reserve = Reserve.get(hsRes.ownerClient?.reserve_id?:0)
    if(hsRes.ispaypossible&&hsRes.reserve)
      hsRes.totalPrice = Math.round(billingService.getBronPrice(hsRes.mbox) / hsRes.valutaRates)

    if(hsRes.context.lang){
      hsRes.home=hsRes.home.csiSetEnHome()
      hsRes.ownerUser=hsRes.ownerUser.csiSetEnUser()
    }
    hsRes.urlphoto = grailsApplication.config.urlphoto

    return hsRes
  }

}