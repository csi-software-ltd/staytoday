//import org.codehaus.groovy.grails.commons.grailsApplication
import org.springframework.context.i18n.LocaleContextHolder as LCH
import javax.servlet.http.Cookie

class RequestService {
  boolean transactional = false
  static scope = "request" //!!!!
  static proxy = true
  def transient m_oController=null
  def transient usersService
  def static final DATE_FORMAT='yyyy-MM-dd'
  def grailsApplication
  //def transient bannersService
  
  def init(oController){
    m_oController=oController
  }
  ////////////////////////////////////////////////////////////////////////////////////
  private checkInit(){
    if(m_oController==null)
      log.debug("Does not set controller object in Request Service. Call requestService.init(this)")
    return (m_oController==null)
  }  
  ////////////////////////////////////////////////////////////////////////////////////
  def getContextAndDictionary(bContextOnly=false,bGetInfotext=false,bWallet=false,bGetBanners=false,bNotice=false) {
    //def sServer=m_oRequest.getServerName().toLowerCase()
    if(checkInit()) return [:]
    
    def sServer=m_oController.request.getServerName().toLowerCase()	    
    def iPort=m_oController.request.getServerPort()     
    def sForwardURI=m_oController.request.forwardURI
    
    def hsRes=[
          context:[
            lang:(LCH.getLocale().language.toString()=='en')?'_en':'',
	          is_dev:(Tools.getIntVal(grailsApplication.config.isdev,0)==1),
            addport:(iPort!=80 && iPort!=443)?(':'+iPort):'',//443 is https port
            serverURL:grailsApplication.config.grails.mailServerURL?:grailsApplication.config.grails.serverURL,                        
            mainserverURL:grailsApplication.config.grails.serverURL,
            sequreServerURL:Tools.getIntVal(Dynconfig.findByName('global.https.enable')?.value,0)?(grailsApplication.config.grails.secureServerURL?:grailsApplication.config.grails.serverURL):grailsApplication.config.grails.serverURL,
            appname:grailsApplication.config.grails.serverApp,
	          mapkey:grailsApplication.config.yandex.mapkey,
            curl_ru:'http://'+m_oController.request.serverName+sForwardURI.replace('/en',''),                             
            cquery:(m_oController.request.getQueryString()?:'')-'&lang=en'-'&lang=ru'-'lang=en'-'lang=ru',
            mobileURL:grailsApplication.config.grails.mobileURL            
          ]
        ]       
    if(sForwardURI.contains('/en/') || (sForwardURI.size() == 3 && sForwardURI.substring(sForwardURI.size() - 3)=='/en'))
      hsRes.context.curl_en='http://'+m_oController.request.serverName+sForwardURI
    else{      
      if(!sForwardURI)
        sForwardURI='/'
      hsRes.context.curl_en='http://'+m_oController.request.serverName+(hsRes?.context?.is_dev?('/'+hsRes?.context?.appname):'')+'/en'+(hsRes?.context?.is_dev?sForwardURI.replace('/'+hsRes?.context?.appname,''):sForwardURI)
    }
    hsRes.context.curl=hsRes.context?.lang?hsRes.context.curl_en:hsRes.context.curl_ru
    hsRes.context.serverURL=grailsApplication.config.grails.mailServerURL?:(hsRes?.context?.lang?(grailsApplication.config.grails.serverURL+'/en'):grailsApplication.config.grails.serverURL)                       
     
//lang url>>    
    def sUrl=(m_oController.request.isSecure()?'https':'http')+'://'+sServer+hsRes?.context?.addport+(hsRes?.context?.is_dev?('/'+hsRes?.context?.appname):'')
    hsRes.context.mainserverURL_lang=hsRes?.context?.lang?(sUrl+'/en'):sUrl
    hsRes.context.sequreServerURL=hsRes?.context?.lang?(hsRes.context.sequreServerURL+'/en'):hsRes.context.sequreServerURL
    hsRes.context.mobileURL=hsRes?.context?.mobileURL+hsRes?.context?.addport+(hsRes?.context?.is_dev?('/'+hsRes?.context?.appname):'')
    hsRes.context.mobileURL_lang=hsRes?.context?.mobileURL+(hsRes.context.lang?'/en':'')
    hsRes.context.absolute_lang=grailsApplication.config.grails.serverURL.trim()+(hsRes.context.lang?'/en':'')    
//<<    
	//spy protection
    /*hsRes.spy_protection=false
	if(Temp_ipblock.findWhere(userip:m_oController.request.remoteAddr,status:2)){		
	  hsRes.spy_protection=true	  
	}*/
	//capcha fail
    hsRes.capcha_fail=false
    if(Temp_ipblock.findWhere(userip:m_oController.request.remoteAddr,status:1)){		
      hsRes.capcha_fail=true	  
    }
	
	//valuta ------------------------------//owner Dmitry
    hsRes.change_valuta_menu=Valuta.findAll('FROM Valuta WHERE modstatus=1 ORDER BY regorder')
    hsRes.context.shown_valuta=Valuta.get(getCookie('SHOWN_VALUTA')?:Tools.getIntVal(grailsApplication.config.valuta.shown_valuta_id_default,857).toInteger())    
    hsRes.vk_api_key=grailsApplication.config.vk.APIKey
    hsRes.fb_api_key=grailsApplication.config.facebook.APIKey

    hsRes.user_max_enter_fail=Tools.getIntVal(grailsApplication.config.user_max_enter_fail,10)
					  
    if(bContextOnly)
      return hsRes          
           
	  //htmlPageTextCorrection
//	  hsRes.context.gberrortype=Gberrortype.findAll()

    if(bGetInfotext){
    //infotext ---------------------------------		      	  	      	            
      hsRes?.infotext=Infotext.findWhere(controller:m_oController.controllerName,action:m_oController.actionName)				   
      hsRes?.topmenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=1 ORDER BY npage ASC')				   //owner Marina
      hsRes?.helpmenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=4 ORDER BY npage ASC')			     //owner Marina
      hsRes?.bottommenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=10 ORDER BY npage ASC')			 //owner Marina
      
      hsRes?.personalmenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=2 ORDER BY npage ASC')			 //owner Marina    
      hsRes?.profilemenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=3 ORDER BY npage ASC')			 //owner Marina      
      
    }

    //get current logged in user>>
    hsRes+=getCurUser()
    if(hsRes.context.lang&&hsRes.user){
      hsRes.user.name=Tools.transliterate(hsRes.user.name,0)
      hsRes.user.nickname=Tools.transliterate(hsRes.user.nickname,0)    
    }  
    //<<get current logged in user

    if(bWallet){
      def oUser = User.get(hsRes.user?.id)
      if (oUser) {
        def lsCompanylist = []
        try{
          oUser.companylist.tokenize(', ').each{lsCompanylist << it.toLong()}
        }catch(Exception e){}
	      hsRes.wallet=lsCompanylist
      }
    }

    if (hsRes.user?.id) {

      Long lClientId = 0
      if(hsRes?.user?.client_id)
        lClientId=hsRes?.user?.client_id
      else if(hsRes?.user?.ref_id)
        lClientId=User.get(hsRes?.user?.ref_id)?.client_id?:0

      hsRes.msg_unread_count = Mbox.executeQuery('select count(*) from Mbox where (((homeowner_cl_id=:cl_id and is_readowner=0)or(user_id=:u_id and is_readclient=0)) OR (is_read=0 and ((homeowner_cl_id=:cl_id and is_answer=0 and is_approved=1) or (user_id=:u_id and is_answer=1)))) and not(modstatus=6 or ((homeowner_cl_id=:cl_id and IFNULL(is_owfav,0)=-1)or(user_id=:u_id and IFNULL(is_clfav,0)=-1)))',[cl_id:lClientId,u_id:hsRes.user.id])[0]
      if(hsRes.msg_unread_count==1) hsRes.msg_unread_id = Mbox.executeQuery('select id from Mbox where (((homeowner_cl_id=:cl_id and is_readowner=0)or(user_id=:u_id and is_readclient=0)) OR (is_read=0 and ((homeowner_cl_id=:cl_id and is_answer=0 and is_approved=1) or (user_id=:u_id and is_answer=1)))) and not(modstatus=6 or ((homeowner_cl_id=:cl_id and IFNULL(is_owfav,0)=-1)or(user_id=:u_id and IFNULL(is_clfav,0)=-1)))',[cl_id:lClientId,u_id:hsRes.user.id])[0]
      if(lClientId){
        hsRes.waiting_unread_count=Zayvka2client.countByClient_idAndModstatus(lClientId,0)
        if (Client.get(lClientId)?.resstatus>0) {
          hsRes.reserveStatus = [booking_unread_count:Trip.executeQuery('select count(*) from Trip t, Home h where t.modstatus=0 and h.client_id=:cl_id and t.home_id=h.id',[cl_id:lClientId])[0],payAttached:true]
        }
      }
    }
   
    if(bNotice){
      hsRes.serverUrl=grailsApplication.config.grails.serverURL?:''
      hsRes.notice=[]
      def oInfotext=Infotext.findWhere(controller:m_oController.controllerName,action:m_oController.actionName,is_anons:1)
      def i=0      
      for(notice in Notice.findAll("FROM Notice WHERE modstatus=1 ORDER BY rand()")){               
        if((notice?.page?.tokenize(',')?:[]).contains(oInfotext?.id.toString())){
          if(i<2){
            hsRes.notice<<notice
            notice.ncount++
            if(!notice.save(flush:true)) {
              log.debug(" Error on save Notice:")    
              notice.errors.each{log.debug(it)}
            }            
          }
          i++  
        }
      }     
    }   
    return hsRes
  }

  ////////////////////////////////////////////////////////////////////////////////////
  def setStatistic(page,type=0,home_id=0,reccount=0,keyword='',prop='',reclist=[],isMobile=false) {
    def sServer=m_oController.request.getServerName().toLowerCase()

    def uri=m_oController.request.forwardURI
   // println(uri)
    def sLang = LCH.getLocale().language.toString()
    def sWeb=isMobile?'mobile':'web' //TODO
    
    def user=usersService.getCurrentUser(this)   
    
    def referer=m_oController.request.getHeader("Referer")
    def aReferer
    def sReferer=''
    if(referer!=null){
      aReferer=referer.split('//')
	  if((aReferer?:[]).size()>1){
        def index=aReferer[1].indexOf('/')
        if(index>0)	  
          aReferer[1]=aReferer[1].substring(0,index)
        sReferer=aReferer[1]       
	  }
    }
    def iRefId
    def lsStatref = Statref.findAll('FROM Statref')
    lsStatref.each{
      if ((it.domain)
	    &&(sReferer.contains(it.domain)))
		iRefId = it.id
    }
    if (!iRefId) iRefId = 6
    try{
      def oOnlinelog = new Onlinelog() 
      oOnlinelog.userip = m_oController.request.remoteAddr
      oOnlinelog.reference = sReferer
      oOnlinelog.ref_id = iRefId
      oOnlinelog.page=page
      oOnlinelog.type=type
      oOnlinelog.reccount=reccount
      oOnlinelog.reclist=Tools.arrayToString(reclist,', ')    
      oOnlinelog.home_id=home_id
      oOnlinelog.keyword=keyword?:''
      oOnlinelog.lang=sLang
      oOnlinelog.users_id=(user!=null)?user.id:0
      oOnlinelog.site=sWeb
      oOnlinelog.prop=prop
	
	def sHeader=m_oController.request.getHeader("User-Agent")	
	
	if((sHeader?:'').size()>255){	  
	  sHeader=sHeader.substring(0,255)
	}
	oOnlinelog.useragent=sHeader?sHeader:''
    
      if(!oOnlinelog.save(flush:true)) {
        log.debug(" Error on save Onlinelog:")    
        oOnlinelog.errors.each{log.debug(it)}
      }   	
    }catch(Exception e){
	  log.debug(" Error on save Onlinelog:"+e.toString())    
    }	
  }  
  ///////////////////////////////////////////////////////////////////////////////////
  def getRaw(sName){
    if(checkInit()) return null
    
    return m_oController.params[sName]
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getIds(sName){
    if(checkInit()) return null
    if(m_oController.params[sName]==null||m_oController.params[sName]=='')
      return null
    
    def lsRes=[]
    try{
      if(m_oController.params[sName] instanceof String){	  
        lsRes << m_oController.params[sName].toLong()
	  }
      else
        lsRes=m_oController.params[sName].collect{it.toLong()}
    }catch(Exception e){
      log.debug("getIds wrong Long \n"+e.toString())
      lsRes=null
    }
    return lsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getStringList(sName){
    if(checkInit()) return []
    if(m_oController.params[sName]==null)
    return []
    
    def lsRes=[]
    try{
      if(m_oController.params[sName] instanceof String)
      lsRes << m_oController.params[sName].trim()
      else
      lsRes=m_oController.params[sName].collect{it.trim()}
    }catch(Exception e){
      log.debug("Error in list param ${sName}\n"+e.toString())
      lsRes=null
    }
    return lsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getLongDef(sName,iDef){
    try{
	  iDef=iDef.toLong()
	}catch(Exception e){
      //exception here usual on convertion. Pass it.
    }
    if(checkInit()) return iDef
    
    if (m_oController.params==null) return iDef
    if (m_oController.params[sName]==null) return iDef
    if (m_oController.params[sName]=='') return iDef
    try{	
      return m_oController.params[sName].toLong()
    }catch(Exception e){
      //exception here usual on convertion. Pass it.
    }
    return iDef
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getFloatDef(sName,iDef){
    try{
	  iDef=iDef.toFloat()
	}catch(Exception e){
      //exception here usual on convertion. Pass it.
    }
    if(checkInit()) return iDef
    
    if (m_oController.params==null) return iDef
    if (m_oController.params[sName]==null) return iDef
    if (m_oController.params[sName]=='') return iDef
    try{	
      return m_oController.params[sName].toFloat()
    }catch(Exception e){
      //exception here usual on convertion. Pass it.
    }
    return iDef
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getStr(sName){
    if(checkInit()) return ''
    
    try{
      if (m_oController.params==null)
        return ""
      if (m_oController.params[sName]==null)
        return ""
      return m_oController.params[sName].trim()
    }catch(Exception e){
      log.debug("Error in string param ${sName}\n"+e.toString())
    }
    return ""
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getPhoneCode(sName){
    def sPhone = getPreparedStr(sName)
    try{
      sPhone=sPhone.replace('(','').replace(')','').replace('+','').replace('-','').replace(' ','').replace('.','')
      def iTest=sPhone.toLong()
      return sPhone
    }catch(Exception e){
      return ''
    }
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getPhone(sName){
    def sPhone=getPhoneCode(sName)
    if(sPhone.size()>3)
      return sPhone[0..2]+'-'+sPhone[3..-1]
    else
      return sPhone
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getPreparedStrNull(sName){
    sName=getPreparedStr(sName)
    return (sName=='')?null:sName
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getPreparedStr(sName){
    return Tools.prepareSearchString(getStr(sName))
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getMax(){
    def iCfg=Tools.getIntVal(grailsApplication.config.request.max)
    def iMax=getLongDef('max',iCfg).toInteger()
    return Math.min( iMax,iCfg)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getOffset(){
    def iOffset=getLongDef('offset',0).toInteger()
    return Math.max(iOffset,0)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getIntFromFloatDef(sName,iDef){
    if(checkInit()) return iDef
    
    if (m_oController.params==null) return iDef
    def sIn=m_oController.params[sName]
    if (sIn==null) return iDef
    if (sIn=='') return iDef
    sIn=sIn.replace(',','.')
    if (sIn=='.') return iDef
    def iPos=sIn.indexOf('.')
    if(iPos>=0)
      sIn=sIn.substring(0,iPos)
    if (sIn=='') return (iPos>=0)?0:iDef
    try{
      return sIn.toInteger()
    }catch(Exception e){
      //pass exception
    }
    return iDef
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getIntDef(sName,iDef){
    if(checkInit()) return iDef
    
    if (m_oController.params[sName]==null) return iDef
    if (m_oController.params[sName]=='') return iDef
    try{
      return m_oController.params[sName].toInteger()
    }catch(Exception e){;
    }
    return iDef
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def setSessionIds(sSessionName,sParamName){
    if(checkInit()) return 
    def sId=getLongDef(sParamName,0).toString()
    if(sId!='0'){
      if(m_oController.session[sSessionName]==null)
        m_oController.session[sSessionName]=[]
      if(sId in m_oController.session[sSessionName]){
        while (sId in m_oController.session[sSessionName])
          m_oController.session[sSessionName].remove(sId)
      }else{
        m_oController.session[sSessionName] << sId
      }
    }
  }
  
  ///////////////////////////////////////////////////////////////////////////////////
  def setSessionParamIds(sSessionName,sId){
    if(checkInit()) return
        if(sId!='0'){
          if(m_oController.session[sSessionName]==null)
            m_oController.session[sSessionName]=[]
          if(sId in m_oController.session[sSessionName]){           
          }else{
            m_oController.session[sSessionName] << sId
          }
        }     
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def setCookie(sName,sValue,iTimeout,bDomain = true){
    if(checkInit()) return

    if((m_oController.response==null)||(m_oController.request==null)){
      log.debug("ERROR: Call Request method setCookie without nessesary params")
      return
    }
    def oCookie = new Cookie(sName, sValue)

    def sServerURL = m_oController.request.serverName

    if(sServerURL.find(/localhost/)==null&&!sServerURL.matches('.*/([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\u002E([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\u002E([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\u002E([01]?\\d\\d?|2[0-4]\\d|25[0-5]).*')){
      if (bDomain){
        def lsServerURL = sServerURL.tokenize(':')
        if(sServerURL.tokenize('.').size()>2){
          oCookie.domain = sServerURL - sServerURL.tokenize('.')[0]-(lsServerURL.size()>2?':'+lsServerURL[2]:'')
        }else{
          oCookie.domain = sServerURL-'http://'-(lsServerURL.size()>2?':'+lsServerURL[2]:'')
        }
      }
    }
    oCookie.path = '/'
    oCookie.maxAge = iTimeout //108000// 30 days
    m_oController.response.addCookie(oCookie)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getCookie(sName){
    if(checkInit()) return ''
    
    if((m_oController.response==null)||(m_oController.request==null)){
      log.debug("ERROR: Call Request method getCookie without nessesary params")
      return null
    }	
    def hsCookie=m_oController.request.cookies.find{it.name == sName}
	
    if(hsCookie)
      return hsCookie.value
    return null
  }    
  ////////////////////////////////////////////////////////////////////////////////////
  def getDate(sName){
    def iYear=getIntDef(sName+'_year',-1)
    def iMonth=getIntDef(sName+'_month',-1)
    def iDay=getIntDef(sName+'_day',-1)
    if((iYear<=0)||(iMonth<=0)||(iDay<=0))
      return null
    try{
      return  (new GregorianCalendar(iYear,iMonth-1, iDay)).time
    }catch(Exception e){
      return null
    }
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def getParams(intParams,liParams=[],lsParams=[],ldParams=[]){
    def hRet=[long:[:],int:[:],string:[:],inrequest:[:]]
    for (elem in lsParams){
      hRet.string[elem]=getStr(elem)
      if(hRet.string[elem]!='')
      hRet.inrequest[elem]=hRet.string[elem]
    }
	for (elem in liParams){
      hRet.long[elem]=getLongDef(elem,0)
      if(hRet.long[elem]!=0)
      hRet.inrequest[elem]=hRet.long[elem]
    }
    for (elem in intParams){
      hRet.int[elem]=getIntDef(elem,0)
      if(hRet.int[elem]!=0)
      hRet.inrequest[elem]=hRet.int[elem]
    }
    def datCurrent
    for (elem in ldParams){ //dates	
      datCurrent=getDate(elem)	  
      hRet.string[elem]=((datCurrent!=null)? datCurrent.format(DATE_FORMAT)
      : getStr(elem))
      if(hRet.string[elem]!=''&&datCurrent!=null)
      hRet.inrequest[elem]=hRet.string[elem]
    }
    return hRet
  }
  //////////////////////////////////////////////////////////////////////////////////////
  def sendError(iStatusCode,sMessage = null){
    if(checkInit()) return
    
    if((m_oController.response==null)||(m_oController.request==null)){
      log.debug("ERROR: Call Request method sendError without nessesary params")
      return
    }
    try{
      if (sMessage) 
        m_oController.response.sendError(iStatusCode,sMessage)
      else
        m_oController.response.sendError(iStatusCode)
    }catch(Exception e){
      log.debug('Error in Request method sendError\n'+e.toString())
    }
  }
  //////////////////////////////////////////////////////////////////////////////////////
  def setStatus(iStatusCode){
    if(checkInit()) return
    
    if((m_oController.response==null)||(m_oController.request==null)){
      log.debug("ERROR: Call Request method sendError without nessesary params")
      return
    }
    try{
      m_oController.response.status = iStatusCode
    }catch(Exception e){
      log.debug('Error in Request method setStatus\n'+e.toString())
    }
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getList(sName,bZero=false){
    if(checkInit()) return null
    if(m_oController.params[sName]==null)
    return null
    
    def lsRes=[]
    
    lsRes=m_oController.params[sName].split(',')

    if(bZero){     
      if(lsRes[0]==0 || lsRes[0]=='0' || lsRes[0]=='')
        lsRes=[]
    }
    return lsRes
  }

  def getBodyXML(){
    if(checkInit()) return null

    m_oController.request.XML
  }

  def getCurUser(){
    if(checkInit()) return [:]

    def hsTmp = [:]
    def tmp
    if (!((tmp=Temp_notification.get(Tools.getIntVal(grailsApplication.config.loginDenied.temp_notification_id,3)))?.status)) {
      hsTmp.user=usersService.getCurrentUser(this)
    } else {
      hsTmp.isLoginDenied=true
      hsTmp.loginDeniedText=tmp?.text?:'Сервис временно недоступен'
    }
    return hsTmp
  }
}
