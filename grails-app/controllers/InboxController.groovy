import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.converters.JSON
class InboxController {  
  def requestService
  def imageService
  def mailerService
  def billingService
  def static final DATE_FORMAT='yyyy-MM-dd' 
  def androidGcmService  
  
  def checkUser(hsRes) {   
    if(!hsRes?.user){	  	                       
      response.sendError(401)
      return false;
    }
	def oTemp_notification=Temp_notification.findWhere(id:1,status:1)	  
	session.attention_message=oTemp_notification?oTemp_notification.text:null
    return true
  } 
  def checkAccess(hsRes) {
    Long lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id?:0
    if (!(hsRes.msg && (hsRes.msg.user_id==hsRes.user?.id || hsRes.msg.user_id==lSocId || hsRes.client_id == Home.get(hsRes.msg.home_id)?.client_id))){
      redirect(controller:'personal', action:'index', base:hsRes.context.mainserverURL_lang)
      return false
    }
    return true
  }
  def init(hsRes){   
    def hsTmp=findClientId(hsRes)
    hsTmp.imageurl = ConfigurationHolder.config.urlphoto + hsTmp.client_id.toString()+'/'
    hsTmp.textlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
    hsTmp.stringlimit = Tools.getIntVal(ConfigurationHolder.config.smalltext.limit,220)

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
    else if(hsRes?.user?.ref_id)	
      hsTmp.client_id=User.get(hsRes?.user?.ref_id)?.client_id?:0	        	
    return hsTmp	
  }  
  ////////////////////////////////////////////////////////////////////////////////
  def index ={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if(request.getHeader("User-Agent")?.contains('Mobile'))
      redirect(uri:'/inbox',base:hsRes.context?.mobileURL_lang,permanent:true)    
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    session.attention_message_once=null
    def oClient=Client.get(hsRes?.client_id?:0)
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

    hsRes+=requestService.getParams([],['client'])

    //hsRes.user = User.get(hsRes.user.id)
    def oMbox=new MboxSearch()
    hsRes.count=oMbox.csiGetMbox(hsRes.user?.id,hsRes.inrequest?.client?:0,-1,0,0).count
    hsRes.count_fav=oMbox.csiGetMbox(hsRes.user?.id,hsRes.inrequest?.client?:0,-2,0,0).count
    hsRes.count_del=oMbox.csiGetMbox(hsRes.user?.id,hsRes.inrequest?.client?:0,0,0,0).count

    hsRes.modstatus = Mboxmodstatus.findAllByModstatusGreaterThan(0)
    hsRes.status_count = []
    for(status in hsRes.modstatus)
      hsRes.status_count << oMbox.csiGetMbox(hsRes.user?.id?:0,hsRes.inrequest?.client?:0,status.modstatus,0,0).count

    if(hsRes.user?.client_id)
      requestService.setStatistic('messageslistview',29)
    else
      requestService.setStatistic('messageslistview',30)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////
  def inbox_list={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    //hsRes.user = User.get(hsRes.user.id)
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
    hsRes+=requestService.getParams(['modstatus','ord'],['client'])
    hsRes.data=[records:[],count:0]
    hsRes.reminder=[:]  
    if(hsRes.user){
      def oMbox=new MboxSearch()
      hsRes.data=oMbox.csiGetMbox(hsRes.user?.id?:0,hsRes.inrequest?.client,hsRes.inrequest?.modstatus?:0,20,requestService.getOffset(),0,hsRes.int?.ord?:0)                  
      
      def today = new Date()
      for(def i=0; i<hsRes.data.records.size();i++){
        use(groovy.time.TimeCategory){
          def duration = hsRes.data.records[i].moddate + Tools.getIntVal(ConfigurationHolder.config.mbox.answertime.hours,4).hours - today
          def days = duration.days
          def hours = duration.hours
          def minutes = duration.minutes
          def seconds = duration.seconds
          if(days==0 && hours>=0 && minutes>=0 && seconds>=0)
            hsRes.reminder[i] = ((hours<10)?'0':'')+hours+':'+((minutes<10)?'0':'')+minutes+':'+((seconds<10)?'0':'')+seconds
          else
            hsRes.reminder[i] = 0
        }
      }
      
      if(hsRes.context?.lang){
        def lsData=[]
        for(record in hsRes.data.records){
          def oMboxTmp=new MboxSearch()
          
          record.properties.each { prop, val ->
            if(["metaClass","class"].find {it == prop}) return
            if(oMboxTmp.hasProperty(prop)) oMboxTmp[prop] = val
          }
          oMboxTmp.id=record.id
          oMboxTmp.client_nickname=Tools.transliterate(oMboxTmp.client_nickname,0)
          oMboxTmp.user_nickname=Tools.transliterate(oMboxTmp.user_nickname,0)
          oMboxTmp.home_name=Tools.transliterate(oMboxTmp.home_name,0)
          oMboxTmp.home_address=Tools.transliterate(oMboxTmp.home_address,0)
          oMboxTmp.home_city=Tools.transliterate(oMboxTmp.home_city,0)
          oMboxTmp.shortaddress=Tools.transliterate(oMboxTmp.shortaddress,0) 

          lsData<<oMboxTmp          
        }
        hsRes.data.records=lsData
      }

      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      hsRes.homeperson=Homeperson.list()
      hsRes.answertype=Answertype.list()
    }
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////
  def selectmbox = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)    
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)  
    
    def lId = requestService.getLongDef('id',0)
    def iOwner=requestService.getLongDef('owner',0)
    def iAction=requestService.getLongDef('act',0)    
    hsRes.msg = Mbox.get(lId)
    if (!checkAccess(hsRes)) return

    if(iOwner==1)
      hsRes.msg.is_owfav=(iAction==1)?1:0
    else
      hsRes.msg.is_clfav=(iAction==1)?1:0      
    if(!hsRes.msg.save(flush:true)) {
      log.debug(" Error on save Mbox:")
      oUser.errors.each{log.debug(it)}
    }
    render(contentType:"application/json"){[error:false]}
  }
  ////////////////////////////////////////////////////////////////////////////////
  def hidembox = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)    
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)  
    def lId = requestService.getLongDef('id',0)
    def iAction=requestService.getLongDef('act',0)

    hsRes.msg = Mbox.get(lId)
    if (!checkAccess(hsRes)) return

    if(hsRes.user.id==hsRes.msg.user_id){
      hsRes.msg.is_clfav=(iAction==1)?-1:0
      if (hsRes.msg.modstatus>0&&iAction==1) {
        hsRes.msg.modstatus = -100
      } else if (hsRes.msg.modstatus==-100&&iAction!=1) {
        hsRes.msg.modstatus = 1
      }
    } else {
      hsRes.msg.is_owfav=(iAction==1)?-1:0
      if (hsRes.msg.modstatus>0&&iAction==1) {
        hsRes.msg.modstatus = -101
        mailerService.sendCloseMboxMail(hsRes.msg,hsRes.context)
      } else if (hsRes.msg.modstatus==-101&&iAction!=1) {
        hsRes.msg.modstatus = 1
      }
    }
    hsRes.msg.controlstatus = (iAction==1)?-2:0

    if(!hsRes.msg.save(flush:true)) {
      log.debug(" Error on save Mbox:")
      hsRes.msg.errors.each{log.debug(it)}
    }
    render(contentType:"application/json"){[error:false]}
  }
  //////////////////////////////////////////////////////////////////////////////// 
  def view={     
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true)      
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    hsRes.msg=hsRes.sender=hsRes.recipient=hsRes.home=[:]     
    def lId=requestService.getLongDef('id',0)	
    hsRes.msg = Mbox.get(lId)

    if(request.getHeader("User-Agent")?.contains('Mobile'))
      redirect(uri:'/mbox?id='+hsRes.msg?.id,base:hsRes.context?.mobileURL_lang,permanent:true)

    //hsRes.user = User.get(hsRes.user.id)
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
    hsRes.howto=Howto.findAllByPageAndType_id('inbox',1,[sort:'number',order:'asc'])
    if (!checkAccess(hsRes)) return
    
    if(hsRes.msg && hsRes.user){
      if(hsRes.msg.user_id==hsRes.user.id || hsRes.msg.user_id==User.findByRef_id(hsRes.user.id)?.id?:0) hsRes.msg.is_readclient = 1
      else if(hsRes.client_id==hsRes.msg.homeowner_cl_id) hsRes.msg.is_readowner = 1
      if(hsRes.msg.is_answer){
        if(hsRes.msg.user_id==hsRes.user.id || hsRes.msg.user_id==User.findByRef_id(hsRes.user.id)?.id?:0)
          hsRes.msg.is_read=1
      }else if(hsRes.client_id==Home.get(hsRes.msg.home_id?:0)?.client_id?:0)
        hsRes.msg.is_read=1		
      if (!hsRes.msg.save(flush:true)){
        log.debug('error on save Mbox in InboxController:addmbox')
        hsRes.msg.errors.each{log.debug(it)}		    
      }

      def lClientId = Home.get(hsRes.msg.home_id).client_id
      hsRes.sender = User.findWhere(client_id:lClientId)
      hsRes.ownerClient = Client.get(lClientId)
      hsRes.recipient = User.read(User.get(hsRes.msg?.user_id)?.ref_id?:hsRes.msg?.user_id)
      hsRes.home = Home.read(hsRes.msg.home_id)
      
      if(hsRes.context?.lang){        
        hsRes.home=hsRes.home.csiSetEnHome()                                                               
        hsRes.recipient = hsRes.recipient.csiSetEnUser()               
        hsRes.sender=hsRes.sender.csiSetEnUser()   
      }            

      hsRes.userHomes=Home.findAll('FROM Home WHERE client_id=:client_id and modstatus=1 ORDER BY name',[client_id:lClientId])
      hsRes.hometype=Hometype.list()
      hsRes.homeperson=Homeperson.list()
      hsRes.minday=Rule_minday.list()
      hsRes.maxday=Rule_maxday.list()
      hsRes.answergroup=Answergroup.list()
      hsRes.answertype=Answertype.list()
      hsRes.modstatus=Mboxmodstatus.list()
      hsRes.isHideContact=Tools.getIntVal(ConfigurationHolder.config.mbox.hideContactMode,1)?true:false
      hsRes.payway=Payway.findAllByModstatusAndIs_invoice(1,0)
      hsRes.iscanoffer = Mboxrec.findAllByMbox_idAndIs_answerAndAnswertype_idInList(hsRes.msg?.id?:0,1,[1,2])?false:true
      hsRes.iscandecline = Mboxrec.findAllByMbox_idAndIs_answerAndAnswertype_idGreaterThanAndAdmin_id(hsRes.msg?.id?:0,1,0,0)?false:true

      def today = new Date()
      use(groovy.time.TimeCategory){
        def duration = hsRes.msg.moddate + Tools.getIntVal(ConfigurationHolder.config.mbox.answertime.hours,4).hours - today
        def days = duration.days
        def hours = duration.hours
        def minutes = duration.minutes
        def seconds = duration.seconds
        if(days==0 && hours>=0 && minutes>=0 && seconds>=0)
          hsRes.reminder = ((hours<10)?'0':'')+hours+':'+((minutes<10)?'0':'')+minutes+':'+((seconds<10)?'0':'')+seconds
        else
          hsRes.reminder = 0
      }      
      
      hsRes+=requestService.getParams(['home_id','homeperson_id','rule_minday_id','rule_maxday_id','answertype_id'],['message'],['date_start','date_end'])
      if(hsRes.inrequest?.date_start?:'')
        hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
      if(hsRes.inrequest?.date_end?:'')
        hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)

      if(hsRes.msg?.date_end && hsRes.msg?.date_start)
        use(groovy.time.TimeCategory) {
          def duration = hsRes.msg?.date_end - hsRes.msg?.date_start
          hsRes.days_between=duration.days
        }

      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.cpecofferRates = oValutarate.csiGetRate(hsRes.home?.valuta_id?:857)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      hsRes.reserve = Reserve.get(hsRes.ownerClient?.reserve_id?:0)
      hsRes.ispaypossible = (Client.get(hsRes.msg.homeowner_cl_id)?.resstatus==1)
      hsRes.resstatModifier = 1.0
      if (hsRes.ispaypossible) {
        hsRes.resstatModifier = hsRes.resstatModifier + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100)
      }
      hsRes.displayPrice = Math.round(hsRes.msg?.price_rub * hsRes.resstatModifier / hsRes.valutaRates)      
      hsRes.reserve = Reserve.get(hsRes.ownerClient?.reserve_id?:0)
      if(hsRes.ispaypossible && hsRes.reserve)
        hsRes.totalPrice = Math.round(billingService.getBronPrice(hsRes.msg) / hsRes.valutaRates)      
   	} else {
      redirect(controller:'user', action:'index', base:hsRes.context.mainserverURL_lang)
      return
    }
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////// 
  def msg_list={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.sender=hsRes.recipient=[]

    //hsRes.user = User.get(hsRes.user.id)
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto

    def lId=requestService.getLongDef('id',0)
    hsRes.msg = Mbox.get(lId)
    if (!checkAccess(hsRes)) return

    if(hsRes.msg?.date_end && hsRes.msg?.date_start)
      use(groovy.time.TimeCategory) {
        def duration = hsRes.msg?.date_end - hsRes.msg?.date_start
        hsRes.days_between = duration.days
      }

    if(hsRes.msg && hsRes.user){
      def lClientId = Home.get(hsRes.msg.home_id).client_id
      hsRes.ownerClient = Client.get(lClientId)
      hsRes.sender = User.findWhere(client_id:lClientId)
      hsRes.recipient = User.read(hsRes.msg?.user_id)      

      hsRes.data=[records:[],count:0]
      def oMbox=new MboxrecSearch()
      hsRes.data=oMbox.csiGetList(hsRes.msg?.id,hsRes.msg?.user_id,hsRes.sender?.id,hsRes.user.id==hsRes.msg.user_id?0:1,20,requestService.getOffset())
      
      if(hsRes.context?.lang){
        hsRes.recipient = hsRes.recipient.csiSetEnUser()                                               
        hsRes.sender=hsRes.sender.csiSetEnUser()  
        
        def lsData=[]
        for(record in hsRes.data.records){
          def oMboxTmp=new MboxrecSearch()
          
          record.properties.each { prop, val ->
            if(["metaClass","class"].find {it == prop}) return
            if(oMboxTmp.hasProperty(prop)) oMboxTmp[prop] = val
          }
          oMboxTmp.id=record.id
          oMboxTmp.home_city=Tools.transliterate(oMboxTmp.home_city,0)                 
        
          lsData<<oMboxTmp           
        }
        hsRes.data.records=lsData
      } 
      
      hsRes.homeNames=[]
      for(record in hsRes.data.records){
        def sName=Home.get(record?.rechome_id)?.name?:''
        if(hsRes.context.lang)
          sName=Tools.transliterate(sName,0)
        hsRes.homeNames<<sName          
      }
      
      hsRes.homeperson=Homeperson.list()
      hsRes.rule_minday=Rule_minday.list()
      hsRes.rule_maxday=Rule_maxday.list()

      hsRes.home = Home.get(hsRes.msg.home_id)
      hsRes.alikeWhere = hsRes.home.csiGetWhere()

      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      
      hsRes.ispaypossible = (Client.get(hsRes.msg.homeowner_cl_id)?.resstatus==1)
      hsRes.resstatModifier = 1.0
      if (hsRes.ispaypossible) {
        hsRes.resstatModifier = hsRes.resstatModifier + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100)
      }      
      hsRes.reserve = Reserve.get(hsRes.ownerClient?.reserve_id?:0)
      if(hsRes.ispaypossible && hsRes.reserve)
        hsRes.totalPrice = Math.round(billingService.getBronPrice(hsRes.msg) / hsRes.valutaRates)
    }
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////  
  def deletembox = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    def lId = requestService.getLongDef('id',0)
    hsRes.record=[:]

    if(lId){
      def oMboxrecDel=Mboxrec.get(lId)
      if(oMboxrecDel){
        def oMbox=Mbox.get(oMboxrecDel.mbox_id)
        if(oMbox?.modstatus<4){
          oMboxrecDel.delete(flush:true)
          hsRes.record = Mboxrec.findAllByMbox_idAndIs_approvedAndAdmin_id(oMbox.id,1,0,[sort:'inputdate',order:'desc'])[0]
          if(hsRes.record){
            oMbox.date_start = hsRes.record.date_start
            oMbox.date_end = hsRes.record.date_end
            oMbox.home_id = hsRes.record.home_id
            oMbox.homeperson_id = hsRes.record.homeperson_id
            oMbox.price = hsRes.record.price
            oMbox.valuta_id = hsRes.record.valuta_id
            oMbox.price_rub = hsRes.record.price_rub
            oMbox.answertype_id = hsRes.record.answertype_id
            oMbox.is_answer = hsRes.record.is_answer
            oMbox.is_read = 0
            oMbox.moddate = hsRes.record.inputdate
            oMbox.mtext = hsRes.record.rectext
            oMbox.mtextowner = ''
            oMbox.modstatus = 1
            oMbox.controlstatus = 0

            if(!oMbox.save(flush:true)){
              log.debug('error on save Mbox in InboxController:deletembox')
              oMbox.errors.each{log.debug(it)}
              flash.error=2
            }
          }
          Payorder.findAllByMbox_idAndModstatus(oMbox.id,0).each{
            it.cancelBronOffer()
          }
          //if(oMboxrecDel.is_approved) mailerService.mbox_cancel_bron_offer(oMbox)
        }
      }
    }
    render(contentType:"application/json"){[error:false]}
  }
  ////////////////////////////////////////////////////////////////////////////////  
  def addanswer={   
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return	
    hsRes+=init(hsRes)							

    hsRes+=requestService.getParams(['homeperson_id','rule_minday_id','rule_maxday_id','answertype_id','confirmdogovor_1','confirmdogovor_2','pricing_unit'],
                                    ['id','price','home_id','home_id_spec'],null,['date_start','date_end'])

    if(hsRes.inrequest?.id?:0){
      def oMbox=Mbox.get(hsRes.inrequest?.id)
      def ownerClient = Client.get(oMbox?.homeowner_cl_id?:0)
      def iscanoffer = Mboxrec.findAllByMbox_idAndIs_answerAndAnswertype_idInList(oMbox?.id?:0,1,[1,2])?false:true
      if (!oMbox||oMbox?.modstatus<0||((oMbox.modstatus==3||!iscanoffer)&&hsRes.inrequest.answertype_id in [1,2])||(!(oMbox.modstatus in 3..4)&&iscanoffer&&hsRes.inrequest.answertype_id==6)||!ownerClient) {
        //action not occurs when no Mbox, mbox was deleted, or adding new special offer when previous is actually, or adding info request before special offer was made
        render(contentType:"application/json"){[error:true]}
        return
      }
      //validation>>
      def date_start1
      def date_end1

      if(hsRes.inrequest?.date_start?:'')
        date_start1=Date.parse('yyyy-MM-dd', hsRes.inrequest?.date_start)
      else
        date_start1=oMbox.date_start
      if(hsRes.inrequest?.date_end?:'')
        date_end1=Date.parse('yyyy-MM-dd', hsRes.inrequest?.date_end)
      else
        date_end1=oMbox.date_end
      if(date_start1 && date_start1>=date_end1) {
        hsRes.error = true
        hsRes.errorprop = "home.calculateHomePrice.badDate_end.errorprop"
      }
      if(date_start1){
        def dateStart=new Date()
        def date1= new GregorianCalendar()
        date1.setTime(dateStart)
        date1.set(Calendar.HOUR_OF_DAY ,0)
        date1.set(Calendar.MINUTE ,0)
        date1.set(Calendar.SECOND,0)
        date1.set(Calendar.MILLISECOND,0)
        if(date_start1<date1.getTime()){
          hsRes.error = true
          hsRes.errorprop = "home.calculateHomePrice.badDate_start.errorprop"
        }
      }
      if (!hsRes.error&&hsRes.inrequest.answertype_id==2) {
        hsRes.result = 0
        hsRes+=Home.calculateHomePrice(hsRes,hsRes.inrequest.home_id_spec,false)
      }
      if(!ownerClient.is_offeradmit&&hsRes.inrequest.answertype_id in 1..2&&!hsRes.inrequest."confirmdogovor_$hsRes.inrequest.answertype_id"){
        hsRes.error = true
        hsRes.errorprop = "inbox.bron.error.nooferta"
      }
      def hsOut=[:]
      hsOut.error=hsRes.error
      if (hsRes.errorprop){
        hsOut.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)   
        render hsOut as JSON
        return
      }
      //validation<<

      def lsMessage=requestService.getStringList('message')
      for(message in lsMessage)
        if(message){
          hsRes.inrequest.message=message
          if (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false) {
            if (oMbox.modstatus<=3&&(hsRes.inrequest.message.replace('(','').replace(')','').replace('-','').replaceAll("\\s","").matches('.*[0-9]{7,}.*')||hsRes.inrequest.message.matches('.*\\S+@\\S*.*')||hsRes.inrequest.message.matches('.*\\S*@\\S+.*'))) {
              hsRes.inrequest.message = (hsRes.inrequest.message?:'').replaceAll('[0-9( )-]{7,}',' [номер] ').replaceAll('\\S+@\\S*','[email]').replaceAll('\\S*@\\S+','[email]').trim()
            }
          }
        }
      if ((hsRes.inrequest?.message?:'').size()>hsRes.textlimit)
        hsRes.inrequest?.message = hsRes.inrequest?.message.substring(0, hsRes.textlimit)

      Long lSocId = 0
      if (hsRes.user?.is_external)
        lSocId = User.findByRef_id(hsRes.user?.id)?.id?:0

      if(oMbox.user_id==hsRes.user.id || oMbox.user_id==lSocId){
        oMbox.mtext = hsRes.inrequest.message?:''
        if (oMbox.modstatus==4){
          oMbox.is_answer = 0
          oMbox.is_read = 0
          oMbox.answertype_id = 0
        }
      }else if(hsRes.client_id==oMbox.homeowner_cl_id){
        oMbox.mtextowner = hsRes.inrequest.message?:(hsRes.inrequest.answertype_id in 1..2)?'Можно бронировать':''
        if (oMbox.modstatus==4){
          oMbox.is_answer = 1
          oMbox.answertype_id = hsRes.inrequest.answertype_id
          oMbox.is_read = 0
        }
        oMbox.controlstatus = (oMbox.controlstatus>0)?oMbox.controlstatus:((hsRes.inrequest.answertype_id in [3,4,5])?2:1)
        if (oMbox.inputdate>new Date(112,11,05))//release date this feature. Mboxes older this date have not an adequate statistic.
          oMbox.responsetime?:(oMbox.responsetime=(new Date().getTime()-oMbox.inputdate.getTime())/1000)
      }else
        flash.error = 1

      oMbox.home_id = hsRes.inrequest.home_id?:oMbox.home_id
      oMbox.is_adminread = 0
      oMbox.lastusermessagedate = new Date()

      if(hsRes.inrequest.answertype_id==2){
        def date_start=requestService.getDate('date_start')
        def date_end=requestService.getDate('date_end')

        def oValutarate = new Valutarate()
        def valutaRates = oValutarate.csiGetRate(Home.get(hsRes.inrequest.home_id_spec)?.valuta_id?:857)

        oMbox.date_start = date_start
        oMbox.date_end = date_end
        oMbox.homeperson_id = hsRes.inrequest.homeperson_id
        oMbox.home_id = hsRes.inrequest.home_id_spec
        oMbox.price = hsRes.inrequest.pricing_unit?hsRes.inrequest.price*oMbox.getTripLength():hsRes.inrequest.price?:0
        oMbox.price_rub = (oMbox.price*valutaRates).toLong()
        oMbox.valuta_id = Home.get(hsRes.inrequest.home_id_spec)?.valuta_id?:857
      }
      if(hsRes.inrequest.answertype_id==3){
        def start
        def end
        if(hsRes.inrequest?.date_start?:'')
          start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
        if(hsRes.inrequest?.date_end?:'')
          end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
        def hmp = new Homeprop()
        hmp.addHomepropUnavailability(hsRes.inrequest?.home_id,start,end)
      }
      if(!flash.error)
        if(!oMbox.save(flush:true)){
          log.debug('error on save Mbox in InboxController:addanswer')
          oMbox.errors.each{log.debug(it)}
          flash.error=2
        }
      def oMboxrec=new Mboxrec()
      if(!flash.error){
        oMboxrec.mbox_id = oMbox.id
        oMboxrec.answertype_id = hsRes.inrequest?.answertype_id?:0
        oMboxrec.rectext = hsRes.inrequest.message?:(oMboxrec.answertype_id in 1..2)?'Можно бронировать':''
        if(oMbox.user_id==hsRes.user.id || oMbox.user_id==lSocId)
          oMboxrec.is_answer=0
        else if(hsRes.client_id==oMbox.homeowner_cl_id){
          oMboxrec.is_answer=1
        }
        oMboxrec.inputdate = new Date()
        oMboxrec.is_approved = oMbox.modstatus==4?1:0

        oMboxrec.price = oMbox.price
        oMboxrec.price_rub = oMbox.price_rub
        oMboxrec.valuta_id = oMbox.valuta_id
        oMboxrec.date_start = oMbox.date_start
        oMboxrec.date_end = oMbox.date_end
        oMboxrec.homeperson_id = oMbox.homeperson_id
        oMboxrec.home_id = oMbox.home_id

        if(hsRes.inrequest.answertype_id==4){
          oMboxrec.rule_minday_id = hsRes.inrequest.rule_minday_id
          oMboxrec.rule_maxday_id = hsRes.inrequest.rule_maxday_id
        }
        if (!oMboxrec.save(flush:true)){
          log.debug('error on save Mboxrec in InboxController:addmbox')
          oMboxrec.errors.each{log.debug(it)}
          flash.error=2
        }
        if(!flash.error&&oMboxrec.answertype_id in 1..2&&!ownerClient.is_offeradmit){
          ownerClient.csiSetAdmit().ip = request.remoteAddr
          if (!ownerClient.save(flush:true)){
            log.debug('error on save Client in InboxController:addmbox')
            oMboxrec.errors.each{log.debug(it)}
            flash.error=2
          }
        }
      }
      if (oMbox?.modstatus==4&&!flash.error){
        mailerService.addanswermail(oMbox,oMboxrec,hsRes.context)
      }
    }

    hsRes.hInrequest=[:]
    hsRes.hInrequest.answertype_id=hsRes.inrequest.answertype_id

    render hsRes.hInrequest as JSON
  }

  def bron={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lMboxId=requestService.getLongDef('mbox_id',0)
    def lId=requestService.getLongDef('id',0)

    Long lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id?:0

    hsRes.mbox=Mbox.findByIdAndUser_idInList(lMboxId,[lSocId,hsRes.user.id])
    hsRes.mboxRec=Mboxrec.findWhere(id:lId,mbox_id:hsRes.mbox?.id)
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
    if(hsRes.mboxRec){
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
      hsRes.invoicelife = Tools.getIntVal(ConfigurationHolder.config.payorder.invoicelife.days,5)
      if (hsRes.ispaypossible) {
        hsRes.resstatModifier = hsRes.resstatModifier + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100)
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
    }
    return hsRes
  }

  def setMboxBron={
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes) 
    def lId=requestService.getLongDef('id',0)
    def lMboxrecId=requestService.getLongDef('mboxrec_id',0)
    def iClose=requestService.getLongDef('close',0)
    hsRes+=requestService.getParams(['payway','oferta'])

    Long lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id?:0

    def oMbox=Mbox.findByIdAndUser_idInList(lId,[lSocId,hsRes.user.id])
    def oMboxrec1=Mboxrec.findWhere(id:lMboxrecId,mbox_id:oMbox?.id)
    if(oMbox&&oMboxrec1){
      hsRes.ispaypossible = (Client.get(oMbox.homeowner_cl_id)?.resstatus==1)
      if(Homeprop.findAll("FROM Homeprop WHERE (date_start<:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND (modstatus=3 OR modstatus=5) ORDER BY date_start",
          [date_start_query:oMbox.date_start,date_end_query:oMbox.date_end,home_id:oMbox.home_id])){
        hsRes.error = 3
      }
      if (!hsRes.ispaypossible) {
        if(!hsRes.error){
          try {
            billingService.createTripFromMbox(oMbox,oMboxrec1,hsRes.context,hsRes.user.id,iClose)
            hsRes.where = ''
          } catch(Exception e) {
            log.debug("InboxController:setMboxBron:\n"+e.toString())
            hsRes.error = 1
          }
        }
      } else {
        if (!hsRes.inrequest?.payway) {
          hsRes.error = 6
        }
        if (!hsRes.inrequest?.oferta) {
          hsRes.error = 2
        }
        def oPayway = Payway.get(hsRes.inrequest.payway)
        if (oPayway?.is_invoice&&(oMbox.date_start - new Date() < Tools.getIntVal(ConfigurationHolder.config.payorder.invoicelife.days,5))) {
          hsRes.error = 4
        }
        /*if (oPayway?.is_invoice&&(Client.get(oMbox.homeowner_cl_id)?.reserve_id in 3..4)) {
          hsRes.error = 7
        }*/
        if (billingService.getBronPrice(oMbox)>(oPayway?.limit?:Long.MAX_VALUE)) {
          hsRes.error = 5
        }
        if(!hsRes.error){
          try {
            if (oPayway?.is_invoice) {
              hsRes.where = createLink(controller:'account',action: 'paydocuments',id:(billingService.createOrderFromMbox(oMbox,hsRes.inrequest+[userip:request.remoteAddr])?.norder?:''),absolute:true)
            } else if(oPayway.agr_id==3) {
              hsRes.where = createLink(controller:'account',action: 'liqpay',id:(billingService.createOrderFromMbox(oMbox,hsRes.inrequest+[userip:request.remoteAddr])?.norder?:''),absolute:true)
            } else if(oPayway.agr_id==4) {
              hsRes.where = createLink(controller:'account',action: 'payU',id:(billingService.createOrderFromMbox(oMbox,hsRes.inrequest+[userip:request.remoteAddr])?.norder?:''),absolute:true)
            } else if(oPayway.agr_id==5) {
              hsRes.where = createLink(controller:'account',action: 'wmoney',id:(billingService.createOrderFromMbox(oMbox,hsRes.inrequest+[userip:request.remoteAddr])?.norder?:''),absolute:true)
            } else {
              billingService.createOrderFromMbox(oMbox,hsRes.inrequest)
              hsRes.where = createLink(controller:'account',action: 'index',absolute:true)
            }
            if (iClose) {
              billingService.closeAnotherMboxes(oMbox,hsRes.user.id?:0)
            }
          } catch(Exception e) {
            hsRes.error = 1
          }
        }
      }
      if(hsRes.error)
        render(contentType:"application/json"){[error:(hsRes.error)]}
      else
        render(contentType:"application/json"){[error:false,where:hsRes.where]}
    }else
      render(contentType:"application/json"){[error:true]}
  }

  def cancelMboxBron={
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes) 
    def lId=requestService.getLongDef('id',0)
    def sComments=requestService.getStr('comments')
    def iType=requestService.getLongDef('type',0)
    def oTrip = Trip.get(lId?:0)

    if (!iType){
      Long lSocId = 0
      if (hsRes.user?.is_external)
        lSocId = User.findByRef_id(hsRes.user?.id)?.id
      if (!(oTrip && (oTrip.user_id==hsRes.user?.id || oTrip.user_id==lSocId))){
        redirect(controller:'trip', action:'next', base:hsRes.context.mainserverURL_lang)
        return
      }
    } else {
      if (!(oTrip && (hsRes.client_id == Home.get(oTrip.home_id)?.client_id))){
        render(contentType:"application/json"){[error:true]}
        return
      }
    }
    if(!sComments){
      if (!iType){
        flash.error = 1
        redirect(controller:'trip', action:'cancellation', id:lId, base:hsRes.context.mainserverURL_lang)
        return
      } else {
        hsRes.error = true
        hsRes.message = message(code:'inbox.bron.cancelBron.error',args:[], default:'').toString()
        render hsRes as JSON
        return
      }
    }
    def stringlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
    if (sComments.size()>stringlimit) sComments = sComments.substring(0, stringlimit)

    if(oTrip.payorder_id) {
      try {
        billingService.doTransaction( billingService.createPaytrans(oTrip,sComments,iType?17:16) )
      } catch(Exception e) {
        if (!iType){
          flash.error = 2
          redirect(controller:'trip', action:'cancellation', id:lId, base:hsRes.context.mainserverURL_lang)
          return
        } else {
          hsRes.error = true
          hsRes.message = message(code:'inbox.bron.cancelBron.bderror',args:[], default:'').toString()
          render hsRes as JSON
          return
        }
      }
    } else {
       flash.error = billingService.cancelMboxBron(oTrip,iType,sComments,hsRes.context)
    }

    if (!iType){
      redirect(controller:'inbox', action:'view', id:oTrip.mbox_id, base:hsRes.context.mainserverURL_lang)
      return
    } else {
      hsRes.error = false
      hsRes.lId = oTrip.mbox_id
      render hsRes as JSON
      return
    }
  }

  def removeBron={
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes) 
    def lId=requestService.getLongDef('id',0)
    def oMbox=Mbox.get(lId?:0)
    if(oMbox && (hsRes.client_id == Home.get(oMbox.home_id)?.client_id)){
      def start
      if(oMbox.date_start?:'')
        start= new Date(oMbox.date_start.getTime())
      def hmp = new Homeprop()
      hmp.removeHomepropUnavailability(oMbox.home_id,start,oMbox.id)
    }
    render(contentType:"application/json"){[error:false]}
  }  
  def zayavkaresponse={
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes) 

    def lId=requestService.getLongDef('id',0)
    def lZ2C_id=requestService.getLongDef('z2C_id',0)
    def sComments=requestService.getStr('comments')
    def sPrice=requestService.getStr('price')
    hsRes+=requestService.getParams(['homeperson_id'],['home_id'],null,['date_start','date_end'])
    hsRes.result = 0

    if(!sComments){
      hsRes.error = true
      hsRes.message = message(code:'inbox.zayavkaresponse.notext.error',args:[], default:'').toString()
      render hsRes as JSON
      return
    }
    if(!sPrice){
      hsRes.error = true
      hsRes.message = message(code:'inbox.zayavkaresponse.noprice.error',args:[], default:'').toString()
      render hsRes as JSON
      return
    } else if(!sPrice.replace(',','.').matches("[0-9]+[\\u002E]?[0-9]+")) {
      hsRes.error = true
      hsRes.message = message(code:'inbox.zayavkaresponse.badprice.error',args:[], default:'').toString()
      render hsRes as JSON
      return
    }
    hsRes+=Home.calculateHomePrice(hsRes,hsRes.inrequest.home_id)
    if(hsRes.error){
      hsRes.message = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)
      render hsRes as JSON
      return
    }
    def oZayavka = Zayavka.get(lId)
    if(!oZayavka){
      hsRes.error = true
      hsRes.message = message(code:'inbox.zayavkaresponse.saveerror.error',args:[], default:'').toString()
      render hsRes as JSON
      return
    }
    def oZayvka2client = Zayvka2client.get(lZ2C_id)
    if(!oZayvka2client){
      hsRes.error = true
      hsRes.message = message(code:'inbox.zayavkaresponse.saveerror.error',args:[], default:'').toString()
      render hsRes as JSON
      return
    }
	
    def date_start=requestService.getDate('date_start')
    def date_end=requestService.getDate('date_end')
    flash.error=[]
    def oMboxrec
    def oMbox = new Mbox()
    oMbox.inputdate=new Date()

    def oValutarate = new Valutarate()
    def valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    
    def stringlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
    if (sComments.size()>stringlimit) 
      sComments = sComments.substring(0, stringlimit)
    
    oMbox.price_rub=Math.rint((sPrice.replace(',','.') as double) * valutaRates)
    oMbox.price=Math.rint(sPrice.replace(',','.') as double)
    oMbox.valuta_id=hsRes.context?.shown_valuta.id?(hsRes.context?.shown_valuta?.id.toInteger()):null
    oMbox.user_id=oZayavka.user_id
    oMbox.moddate=new Date()
    oMbox.date_start=date_start
    oMbox.date_end=date_end
    oMbox.homeperson_id=hsRes.inrequest.homeperson_id
    oMbox.mtext=''
    oMbox.mtextowner=sComments
    oMbox.is_telperm=0
    oMbox.timezone=16
    oMbox.home_id=hsRes.inrequest.home_id
    oMbox.homeowner_cl_id=Home.get(hsRes.inrequest.home_id).client_id?:0
    oMbox.answertype_id=0
    oMbox.modstatus=1
    oMbox.is_answer=0
    oMbox.is_approved=1
    oMbox.is_read=1
    oMbox.zayvka_id=oZayavka.id
    oMbox.controlstatus=1
    oMbox.responsetime=(new Date().getTime()-oZayvka2client.inputdate.getTime())/1000
    
    if (!oMbox.save(flush:true)){
      log.debug('error on save Mbox in InboxController:zayavkaresponse')
      oMbox.errors.each{log.debug(it)}
      flash.error<<1
    }
    if(!flash.error){
      oMboxrec=new Mboxrec()
      oMboxrec.mbox_id =oMbox.id
      oMboxrec.answertype_id=0
      oMboxrec.rectext=oZayavka.ztext
      oMboxrec.is_answer=0
      oMboxrec.inputdate=new Date()
      oMboxrec.home_id=oMbox.home_id
      oMboxrec.homeperson_id=oMbox.homeperson_id
      oMboxrec.date_start=oMbox.date_start
      oMboxrec.date_end=oMbox.date_end
      oMboxrec.price_rub=oMbox.price_rub
      oMboxrec.price=oMbox.price
      oMboxrec.valuta_id=oMbox.valuta_id
      oMboxrec.is_approved=1
    
      if (!oMboxrec.save(flush:true)){
        log.debug('error on save Mboxrec in InboxController:zayavkaresponse')
        oMboxrec.errors.each{log.debug(it)}
        flash.error<<1
      }
      if(!flash.error){
        oMboxrec=new Mboxrec()
        oMboxrec.mbox_id =oMbox.id
        oMboxrec.answertype_id=2
        oMboxrec.rectext=oMbox.mtextowner
        oMboxrec.is_answer=1
        oMboxrec.inputdate=new Date()
        oMboxrec.home_id=oMbox.home_id
        oMboxrec.homeperson_id=oMbox.homeperson_id
        oMboxrec.date_start=oMbox.date_start
        oMboxrec.date_end=oMbox.date_end
        oMboxrec.price_rub=oMbox.price_rub
        oMboxrec.price=oMbox.price
        oMboxrec.valuta_id=oMbox.valuta_id
        oMboxrec.is_approved=0
    
        if (!oMboxrec.save(flush:true)){
          log.debug('error on save Mboxrec in InboxController:zayavkaresponse')
          oMboxrec.errors.each{log.debug(it)}
          flash.error<<1
        }
      }
    }
    if(!flash.error){
      oZayvka2client.modstatus = 1
      if (!oZayvka2client.save(flush:true)){
        log.debug('error on save Zayvka2client in InboxController:zayavkaresponse id:'+oZayvka2client.id)
        oZayvka2client.errors.each{log.debug(it)}
        flash.error<<1
      }
      //mailerService.sendZayavkaResponseMail(oMbox,oMboxrec,hsRes.context)
    }
    
    if(flash.error){
      hsRes.error = true
      hsRes.message = message(code:'inbox.zayavkaresponse.saveerror.error',args:[], default:'').toString()
      render hsRes as JSON
      return
    }
    
    render hsRes as JSON
    return
  }
}
