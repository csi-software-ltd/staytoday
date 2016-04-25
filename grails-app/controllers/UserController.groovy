//import org.codehaus.groovy.grails.commons.grailsApplication
//import org.openid4java.consumer.ConsumerManager
import org.openid4java.discovery.*
import org.openid4java.consumer.*
import org.openid4java.message.*
import org.openid4java.server.*
import grails.converters.JSON

class UserController {
  static ConsumerManager s_cmManager = new ConsumerManager();
  def usersService
  def requestService
  def jcaptchaService
  def facebookConnectService
  def mailerService
  def homeService

  def checkAccess() { 
    if(Tools.getIntVal(grailsApplication.config.userregservice,1)==0){  
      redirect(controller:'index', action:'notactive', base:hsRes.context.mainserverURL_lang)
      return false;    
    }
  }
    ///////////////////////////////////////////////////////////////////////////////////////
  def index = {
    //error: 1  set user and provider
    //       2  "Failed to find valid openid URI at specified address"
    //       3  "Ошибка протокола авторизации, попробуйте позже"
    //       4  "OpenID authentication failed"
    //       5  "User banned"
    //       99 "Cannot login. Try late."
    requestService.init(this)

    def hsRes=requestService.getContextAndDictionary(false,true)
    if(hsRes.spy_protection){	  
      redirect(controller:'index',action:'captcha')
      return
    }
    hsRes.inrequest = requestService.getParams(['is_ajax','service'],['id'],['control','act','what','user']).inrequest
    if(!(hsRes.inrequest?.is_ajax
        || hsRes.inrequest?.act)){
        redirect(controller:'index', action:'index', base:hsRes.context.mainserverURL_lang)
        return
    }
    hsRes.inrequest.user_max_enter_fail=Tools.getIntVal(grailsApplication.config.user_max_enter_fail,10)
    if(flash.error==51 || flash.error==1 || flash.error==3)
      if(session.user_enter_fail)
        session.user_enter_fail++
      else
        session.user_enter_fail=1

    hsRes.twitter_api_key=grailsApplication.config.twitter.APIKey
    hsRes.vk_api_key=grailsApplication.config.vk.APIKey	

    if (hsRes.inrequest.is_ajax==1){//AJAX
      if(hsRes.user!=null){
        render hsRes.inrequest as JSON
        return
      }else{
        hsRes.inrequestAjax = [:]
        hsRes.inrequestAjax.errorprop=[]
        hsRes.inrequestAjax.error=1
        hsRes.inrequestAjax.user_enter_fail=session.user_enter_fail?:0
        hsRes.inrequestAjax.user_max_enter_fail=hsRes.inrequest.user_max_enter_fail
        hsRes.inrequestAjax.captcha=jcaptcha.jpeg(name:'image').toString()
        if(flash.error==99){
          hsRes.inrequestAjax.captchaError = 1
          hsRes.inrequestAjax.errorprop<<2
        } else if (flash.error==3) {
          hsRes.inrequestAjax.errorprop<<3
        } else
          hsRes.inrequestAjax.errorprop<<1
        render hsRes.inrequestAjax as JSON
        return
      }
    }
	//<<
    if((hsRes.inrequest?.control?:'')=='user' && (hsRes.inrequest?.act?:'')=='addnew'){
      hsRes.inrequest+=requestService.getParams([],['home_id'],['code']).inrequest
      if(hsRes.user!=null){
        def oClient=Client.findWhere(code:hsRes.inrequest.code)
        if(hsRes.user?.client_id){
          if(homeService.setHomeModstatus(2,hsRes.inrequest?.home_id,oClient.id,hsRes.user?.client_id)){
            if(Home.get(hsRes.inrequest?.home_id))
              if(!homeService.setHomePropModstatus(2,hsRes.inrequest?.home_id))
                flash.error=101
          } else { 
            flash.error=101 
          }
          if(!flash.error)
            oClient.delete(flush:true)
        } else {
          oClient.name=hsRes.user?.name
          def oUser = User.get(hsRes.user.id)
          if (oUser.modstatus==0)
            oUser.modstatus=2
          oUser.client_id=oClient.id
          oUser.is_am = 1
          oClient.modstatus=oUser.modstatus
          if(!oUser.save(flush:true)) {
            log.debug(" Error on save user:")
            oUser.errors.each{log.debug(it)}
            flash.error=101
          }
          if(!oClient.save(flush:true)) {
            log.debug(" Error on save client:")
            oClient.errors.each{log.debug(it)}
            flash.error=101		   
          }else if(homeService.setHomeModstatus(2,hsRes.inrequest?.home_id,oClient.id)){
            if(Home.get(hsRes.inrequest?.home_id))
              if(!homeService.setHomePropModstatus(2,hsRes.inrequest?.home_id))
                flash.error=101
          }else{
            flash.error=101
          }
        }
        redirect(controller:'personal', action:'index', base:hsRes.context.mainserverURL_lang)
        return
      }
    }
	//>>
    if((hsRes.inrequest?.control?:'')=='home' && (hsRes.inrequest?.act?:'')=='view'){//owner Dmitry>>
      hsRes.inrequest+=requestService.getParams(['service'],['home_id']).inrequest
      if(hsRes.user!=null){
        redirect(controller:hsRes.inrequest.control,action:hsRes.inrequest.act, params:[id:hsRes.inrequest.home_id?:0, service:hsRes.inrequest.service?:0], base:hsRes.context.mainserverURL_lang)
        return
	   }
    }//<<
    if((hsRes.inrequest?.control?:'')=='user' && (hsRes.inrequest?.act?:'')=='confirm'){
      if(hsRes.user!=null){
        redirect(controller:hsRes.inrequest.control,action:hsRes.inrequest.act, params:[confirm:1], base:hsRes.context.mainserverURL_lang)
        return
      }
    }
    if(hsRes.user!=null){
      if (hsRes.inrequest.control&&hsRes.inrequest.act){
        redirect(controller:hsRes.inrequest.control,action:hsRes.inrequest.act,params:[id:hsRes.inrequest.id,what:hsRes.inrequest.what,where:hsRes.inrequest.where], base:hsRes.context.mainserverURL_lang)
        return
      }
    }
    if(hsRes?.user){		
      redirect(controller:'personal', action:'index', base:hsRes.context.mainserverURL_lang)
    }
    return hsRes        
  }  
///////////////////////////////////////////////////////////////////////////////////////
  def facebook={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    requestService.setCookie('user','parararam',10000)
    def hsInrequest = requestService.getParams(['is_ajax','service'],['id','m_fb_id','home_id','fb_id'],['fb_name','fb_pic','fb_photo','fb_email','control','act','what','where']).inrequest
    def base = 'http://'+request.serverName+(hsRes.context.is_dev?':8080/Arenda':'')
    if (hsInrequest.fb_id?:0){
      if(!usersService.loginUser('f_'+hsInrequest.fb_id.toString(),'','facebook',requestService,'')){
        // if user banned
        flash.error=5
      }
    } else if(hsInrequest.m_fb_id?:0) {
      try {
        def bNewUser = true
        if (User.findWhere(openid:'f_'+hsInrequest.m_fb_id.toString())?.ref_id?:0)
          bNewUser = false
        if(!usersService.loginUser('f_'+hsInrequest.m_fb_id,hsInrequest.fb_name,'facebook',requestService,hsInrequest.fb_photo.replace('http://','https://'),flash?.ref_id?:0)){
          // if user banned
          flash.error=5          
        }else{
          def oUser=User.findWhere(openid:'f_'+hsInrequest.m_fb_id.toString())
          if(oUser!=null){
            oUser.lastdate=new Date()
            oUser.smallpicture=hsInrequest.fb_pic.replace('http://','https://')
            oUser.picture=hsInrequest.fb_photo.replace('http://','https://')
            oUser.email=oUser.email?:hsInrequest.fb_email
            if ((flash?.ref_id?:0) && bNewUser){
              oUser.ref_id=flash.ref_id
              oUser.code=''
              def oRefUser=User.get(flash.ref_id)
              oRefUser.nickname = oUser.nickname
              oRefUser.description = oUser.description
              oRefUser.banned = oUser.banned
              oRefUser.is_external = oUser.is_external
              oRefUser.tel = oUser.tel
              oRefUser.tel1 = oUser.tel1
              oRefUser.companylist = oUser.companylist
              oRefUser.inputdate = oUser.inputdate
              oRefUser.lastdate = oUser.lastdate
              oRefUser.ncomment = oUser.ncomment
              oRefUser.picture = oUser.picture
              oRefUser.smallpicture = oUser.smallpicture
              oRefUser.modstatus = oUser.modstatus
              oRefUser.is_telcheck = oUser.is_telcheck
              oRefUser.is_news = oUser.is_news
              oRefUser.is_review = oUser.is_review
              oRefUser.is_zayvka = oUser.is_zayvka
              oRefUser.is_emailzayvka = oUser.is_emailzayvka
              oRefUser.is_improve = oUser.is_improve
              if(!oRefUser.save(flush:true)) {
                log.debug(" Error on save User:")
                oRefUser.errors.each{log.debug(it)}
              }
            }
            if(!oUser.save(flush:true)) {
              log.debug(" Error on save User:")
              oUser.errors.each{log.debug(it)}	 
            }
            if (oUser.ref_id){
              def oRefUser=User.get(oUser.ref_id)
              oRefUser.nickname = oUser.nickname
              oRefUser.picture = oUser.picture
              oRefUser.smallpicture = oUser.smallpicture
              oRefUser.lastdate = oUser.lastdate
              if(!oRefUser.save(flush:true)) {
                log.debug(" Error on save User:")
                oRefUser.errors.each{log.debug(it)}
              }
            }
            if(flash?.code){//>>from saveuser
              def oClient=Client.findWhere(code:flash.code?:0)
              if(oClient){
                def iClientId=User.get(oUser.ref_id)?.client_id?:0
                homeService.setHomeModstatus(2,flash?.home_id,oClient?.id,iClientId)
                if(Home.get(flash?.home_id))
                  homeService.setHomePropModstatus(2,flash?.home_id?:0)
              }
            }//<<from saveuser
          }
        }
      } catch (Exception e) {        
        log.debug('Facebook parse error :'+e.toString())        
        flash.error=3
      }
    }else{
      flash.error=6
    }
    if(!flash.error && flash.home_id)
      redirect(controller:'personal', action:'adsoverview', id:flash.home_id, absolute:true, base:hsRes.context.mainserverURL_lang)
    else
      redirect(action:'index',params:hsInrequest, base:hsRes.context.mainserverURL_lang/*base*/)
    return	
  }
  def vk={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    requestService.setCookie('user','parararam',10000)
    def hsInrequest = requestService.getParams(['is_ajax','service'],['id','vk_id','home_id'],['vk_pic','vk_photo','vk_name','vk_hash','control','act','what','where']).inrequest

    def sStr=grailsApplication.config.vk.APIKey+hsInrequest.vk_id.toString()+grailsApplication.config.vk.SecretKey
    def base = 'http://'+request.serverName+(hsRes.context.is_dev?':8080/Arenda':'')

    def bNewUser = true
    if (User.findWhere(openid:'vk_'+hsInrequest.vk_id.toString())?.ref_id?:0)
      bNewUser = false
    if(!usersService.loginUser('vk_'+hsInrequest.vk_id.toString(),hsInrequest.vk_name,'vkontakte',requestService,Tools.transformToSecurePhotourl(hsInrequest.vk_pic),flash?.ref_id?:0)){
      // if user banned
      flash.error=5
    }else{
      def oUser=User.findWhere(openid:'vk_'+hsInrequest.vk_id.toString())
      if(oUser!=null){
        oUser.lastdate=new Date()
        oUser.smallpicture=Tools.transformToSecurePhotourl(hsInrequest.vk_pic)
        oUser.picture=Tools.transformToSecurePhotourl(hsInrequest.vk_photo)
        if ((flash?.ref_id?:0) && bNewUser){
          oUser.ref_id=flash.ref_id
          oUser.code=''
          def oRefUser=User.get(flash.ref_id)
          oRefUser.nickname = oUser.nickname
          oRefUser.description = oUser.description
          oRefUser.banned = oUser.banned
          oRefUser.is_external = oUser.is_external
          oRefUser.tel = oUser.tel
          oRefUser.tel1 = oUser.tel1
          oRefUser.companylist = oUser.companylist
          oRefUser.inputdate = oUser.inputdate
          oRefUser.lastdate = oUser.lastdate
          oRefUser.ncomment = oUser.ncomment
          oRefUser.picture = oUser.picture
          oRefUser.smallpicture = oUser.smallpicture
          oRefUser.modstatus = oUser.modstatus
          oRefUser.is_telcheck = oUser.is_telcheck
          oRefUser.is_news = oUser.is_news
          oRefUser.is_review = oUser.is_review
          oRefUser.is_zayvka = oUser.is_zayvka
          oRefUser.is_emailzayvka = oUser.is_emailzayvka
          oRefUser.is_improve = oUser.is_improve
          if(!oRefUser.save(flush:true)) {
            log.debug(" Error on save User:")
            oRefUser.errors.each{log.debug(it)}
          }
        }
        if(!oUser.save(flush:true)) {
          log.debug(" Error on save User:")
          oUser.errors.each{log.debug(it)}
        }
        if (oUser.ref_id){
          def oRefUser=User.get(oUser.ref_id)
          oRefUser.nickname = oUser.nickname
          oRefUser.picture = oUser.picture
          oRefUser.smallpicture = oUser.smallpicture
          oRefUser.lastdate = oUser.lastdate
          if(!oRefUser.save(flush:true)) {
            log.debug(" Error on save User:")
            oRefUser.errors.each{log.debug(it)}
          }
        }
      }
    }
    if(flash?.code){//>>from saveuser
      def oClient=Client.findWhere(code:flash.code?:0)
      if(oClient){
        def oUser=User.findWhere(openid:'vk_'+hsInrequest.vk_id.toString())
        def iClientId=User.get(oUser.ref_id)?.client_id?:0
        homeService.setHomeModstatus(2,flash?.home_id,oClient?.id,iClientId)
        if(Home.get(flash?.home_id))
          homeService.setHomePropModstatus(2,flash?.home_id?:0)
      }
    }//<<from saveuser
    if(!flash.error && flash.home_id)
      redirect(controller:'personal', action:'adsoverview', id:flash.home_id, absolute:true, base:hsRes.context.mainserverURL_lang)
    else
      redirect(action:'index', params:hsInrequest, base:hsRes.context.mainserverURL_lang/*base:base*/)
    return
  }
  ///////////////////////////////////////////////////////////////////////////////////////
  def logout = {
    requestService.init(this)
    usersService.logoutUser(requestService)
    session.user_small_pic=null
    def hsRes=requestService.getContextAndDictionary(true)
    def sTmp=request.getHeader('referer')
    def sId=requestService.getStr('id')
    if(sId in ['personal','account','inbox','profile','trip'] || sTmp==null){
      redirect(mapping:'mainpage'+hsRes.context?.lang)
      return
    }
    else
      redirect(url:sTmp)
  }  
  ///////////////////////////////////////////////////////////////////////////////////////
  def login = {  
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    requestService.setCookie('user','parararam',10000)
    def hsInrequest = requestService.getParams(['is_ajax','service'],['user_index','id'],['control','act','what','where']).inrequest
    hsInrequest+=requestService.getParams([],['home_id'],['code']).inrequest
    def sUser=requestService.getStr('user')
    def sProvider=requestService.getStr('provider')
    def sPassword=requestService.getStr('password')
    def sMoveTo=request.getHeader('referer')
    def iRemember=requestService.getIntDef('remember',0)
    def base = 'http://'+request.serverName+(hsRes.context.is_dev?':8080/Arenda':'')
    if(!flash.user_id){//>>not from confirm
      if((hsInrequest?.user_index?:0) && session.user_enter_fail>Tools.getIntVal(grailsApplication.config.user_max_enter_fail,10)){
        try{
          if (! jcaptchaService.validateResponse("image", session.id, params.captcha)){
            flash.error=99 //error in captha
            redirect(action:'index',params:hsInrequest, base:hsRes.context.mainserverURL_lang/*base:base*/)
            return
          } else {
            session.user_enter_fail=null
          }
        }catch(Exception e){
          flash.error=99 //error in captha
          redirect(action:'index',params:hsInrequest, base:hsRes.context.mainserverURL_lang/*base:base*/)
          return
        }
      }
      if((sUser=='')||(sProvider=='')||(sUser=='логин')){
        flash.error = 1 // set user and provider
        if(requestService.getLongDef('openid',0))
          redirect(action:'openid',params:hsInrequest)
        else
          redirect(action:'index',params:hsInrequest, base:hsRes.context.mainserverURL_lang/*base:base*/)
        return
      }
      if (!User.findWhere(name:sUser)){
        hsInrequest.user = sUser
        flash.error = 3 // new user. need registration
        redirect(action:'index',params:hsInrequest, base:hsRes.context.mainserverURL_lang/*base:base*/)
        return
      }
      if (!sPassword){
        hsInrequest.user = sUser
        flash.error = 2 // empty password is not allowed
        redirect(action:'index',params:hsInrequest, base:hsRes.context.mainserverURL_lang/*base:base*/)
        return
      }
    }//<<not from confirm
    if(sProvider.toLowerCase()==usersService.INTERNALPROVIDER){
      if(!usersService.loginInternalUser(sUser,sPassword,requestService,iRemember,flash?.user_id?:0)){
        //sMoveTo='index'
        flash.error=51 // Wrong password or user does not exists
      }else {
        def oUser=User.findWhere(name:sUser)
        if(oUser!=null){
          oUser.lastdate=new Date()
          if(!oUser.save(flush:true)) {
            log.debug(" Error on save User:")
            oUser.errors.each{log.debug(it)}
          }
        }
        //requestService.setStatistic('userenter',5) //Statistic
      }
      hsInrequest.user=sUser
      redirect(action:'index',params:hsInrequest, base:hsRes.context.mainserverURL_lang/*base:base*/)
      return
    }
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def addnew={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if(hsRes.spy_protection){	  
      redirect(controller:'index',action:'captcha')
      return
    }
    hsRes+=requestService.getParams([],['home_id'],['code','email','nickname','firstname','lastname',])	
    if(!hsRes.inrequest?.home_id){
      redirect(controller:'index', action:'index', base:hsRes.context.mainserverURL_lang)
      return
    }
    hsRes.twitter_api_key=grailsApplication.config.twitter.APIKey
    hsRes.vk_api_key=grailsApplication.config.vk.APIKey	
    hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
	
    requestService.setStatistic('useraddnew')
    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveuser={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if(hsRes.spy_protection){	  
      redirect(controller:'index',action:'captcha')
      return
    }
    if(hsRes.user!=null){
      redirect(action:'index', base:hsRes.context.mainserverURL_lang)
      return
    }	
    hsRes+=requestService.getParams(['is_ajax','service'],['home_id'],['code','email','nickname','firstname','lastname','password1','password2'])	
    hsRes.hinrequest=requestService.getParams([],['vk_id','m_fb_id'],['fb_name','fb_pic','fb_photo','vk_pic','vk_photo','vk_name','vk_hash']).inrequest	
    def iFb_auth=requestService.getIntDef('fb_auth',0)

    flash.error=[]
    def iRefId=0
    def lId=0
    def needUser = true

    if((hsRes.hinrequest?.vk_id || iFb_auth) &&  hsRes.inrequest?.code){	     		      		       
      iRefId=User.findWhere(openid:'vk_'+hsRes.hinrequest?.vk_id)?.ref_id?:User.findWhere(openid:'f_'+hsRes.hinrequest?.m_fb_id)?.ref_id?:0
      if(iRefId)  	    
        hsRes.inrequest?.email=User.get(iRefId)?.email?:''//email allways exists on staytoday provider	
      else
        hsRes.inrequest?.email=Client.findWhere(code:hsRes.inrequest?.code)?.name?:''//email allways exists on staytoday provider	
    }
    if ((hsRes.hinrequest?.vk_id || iFb_auth) && (!hsRes.inrequest?.code)){
      needUser = false
    }

    if(!hsRes.hinrequest?.vk_id && !iFb_auth){
      if(!(hsRes.inrequest?.email?:''))
        flash.error<<1
      else if (!Tools.checkEmailString(hsRes.inrequest.email))
        flash.error<<2
      if(!(hsRes.inrequest?.nickname?:''))
        flash.error<<3	
      if((hsRes.inrequest?.password1?:'')!=(hsRes.inrequest?.password2?:''))
        flash.error<<4
      if((hsRes.inrequest?.password2?:'').size()<Tools.getIntVal(grailsApplication.config.user.passwordlength?:5))
        flash.error<<5	
    }
	
    if(!(flash.error?:[]).size()){  
      def oClient=null
      def iClientId=0
     //>>client	 
      if((hsRes.inrequest?.code?:'') && hsRes.inrequest?.home_id && !iRefId){	   
        oClient=Client.findWhere(code:hsRes.inrequest?.code)        		
        if(oClient){//>>client exist		  		  
          if(oClient.name!=hsRes.inrequest?.email){//>>another email		  
            if(User.findWhere(email:hsRes.inrequest.email?:'bad_email'))
              if(User.findWhere(email:hsRes.inrequest.email?:'bad_email').is_external)
                flash.error<<10
              else
                flash.error<<6
            else if(Client.findWhere(name:hsRes.inrequest.email)){
              def delClient = Client.findWhere(name:hsRes.inrequest.email)
              def lsHome=Home.findAllWhere(client_id:delClient.id)
              for(home in lsHome){
                def oHomeProp=Homeprop.findWhere(home_id:home.id)
                oHomeProp.delete(flush:true)
                home.delete(flush:true)
              }
              delClient.delete(flush:true)
            }				
          }//<<another email			
          if(!(flash.error?:[]).size()){		  
            iClientId=oClient.id		
            oClient.modstatus=2
            if(oClient.name!=hsRes.inrequest?.email)
              oClient.name=hsRes.inrequest?.email		        
            if(!oClient.save(flush:true)) {
              log.debug(" Error on save client:")
              oClient.errors.each{log.debug(it)}            
              flash.error<<101
            }
          }	
          if(!(flash.error?:[]).size()){
            if(!homeService.setHomeModstatus(2,hsRes.inrequest?.home_id,iClientId))
              flash.error<<101
            else if(Home.get(hsRes.inrequest?.home_id))
              if (!homeService.setHomePropModstatus(2,hsRes.inrequest?.home_id))
                flash.error<<101
          }
        }//<<client exist		
      }
      //<<client		  
      if(!(flash.error?:[]).size() && !iRefId&&needUser){
        def oNewUser
        def oUser=User.findWhere(name:hsRes.inrequest.email)//авторизация не только после подачи объявления		
        if(!oUser){
          oNewUser=new User()
          def sCode=java.util.UUID.randomUUID().toString()		  
          iRefId=oNewUser.csiInsertInternal([email:hsRes.inrequest?.email,password:Tools.hidePsw((hsRes.inrequest?.password2?:'')),firstname:(hsRes.inrequest?.firstname?:''),lastname:(hsRes.inrequest?.lastname?:''),nickname:(hsRes.inrequest?.nickname?:''),client_id:iClientId,code:sCode])
          hsRes.inrequest.error=1 //регистрация завершена
          if(iClientId) {//owner Dmitry>>
            oUser = User.get(iRefId)
            oUser.is_am = 1
            if(!oUser.save(flush:true)) {
              log.debug(" Error on save user:")
              oUser.errors.each{log.debug(it)}            
            }
          }//<<owner Dmitry
          //note>>
          def oNote = Note.findByUser_idAndNotetype_id(iRefId,1)
          if (!oNote)
            oNote = new Note(hsRes.inrequest.home_id?:0, iRefId, Notetype.get(1))
          try{
            oNote.save(flush:true)
          } catch (Exception e){
            log.debug("Error on save Note in User:saveuser\n"+e.toString())
          }
          //<<note
          //<<Email		
          def lsText=Email_template.findWhere(action:'#activation')
          def sText='[@EMAIL], for activation of your account use follow link [@URL]'
          def sHeader="Registration at StayToday" 
          if(lsText){
            sText=lsText.itext
            sHeader=lsText.title
          }
          sText=sText.replace(
          '[@NICKNAME]',hsRes.inrequest.nickname?:'').replace(
          '[@EMAIL]',hsRes.inrequest.email).replace(
          '[@URL]',(grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/user/confirm/'+sCode))
          sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
          sHeader=sHeader.replace(
          '[@EMAIL]',hsRes.inrequest.email).replace(
          '[@URL]',(grailsApplication.config.grails.mailServerURL+'/user/confirm/'+sCode))
      
          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              mailerService.sendMailGAE(sText,grailsApplication.config.grails.mail.from1,grailsApplication.config.grails.mail.username,hsRes.inrequest.email,sHeader,1)          
            else{
              sendMail{
                to hsRes.inrequest.email        
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])              
              }          
            }
          }catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString())
            flash.error<<-100 
          }		  
        //>>Email      
        //redirect(action:'addnew',params:hsRes.inrequest)		  
        //return
        }else{
          flash.error<<9
        }
      }	  
	 
      if(!hsRes.hinrequest?.vk_id && !iFb_auth && (hsRes.inrequest.error==1)){
        redirect(action:'login', params:[user:hsRes.inrequest.email,password:requestService.getStr('password1'),provider:'staytoday',is_ajax:hsRes.inrequest.is_ajax,service:hsRes.inrequest.service], base:hsRes.context.mainserverURL_lang)
        return
      }   
      if(hsRes.hinrequest?.vk_id){
        flash.ref_id=iRefId
        if((hsRes.inrequest.error!=1) && hsRes.inrequest.code){
          flash.home_id=hsRes.inrequest.home_id
          flash.code=hsRes.inrequest.code		
        } else if ((hsRes.inrequest.error==1) && hsRes.inrequest.code){
          flash.home_id=hsRes.inrequest.home_id
        }
        redirect(action:'vk',params:hsRes.hinrequest,base:hsRes.context.mainserverURL_lang)  
        return     
      }
      if(iFb_auth){
        flash.ref_id=iRefId
        if((hsRes.inrequest.error!=1) && hsRes.inrequest.code){
          flash.home_id=hsRes.inrequest.home_id
          flash.code=hsRes.inrequest.code		
        } else if ((hsRes.inrequest.error==1) && hsRes.inrequest.code){
          flash.home_id=hsRes.inrequest.home_id
        }
        redirect(action:'facebook',params:hsRes.hinrequest,base:hsRes.context.mainserverURL_lang)
        return
      }
    }
    if (hsRes.inrequest.is_ajax==1){//AJAX
      hsRes.inrequestAjax = [:]
      hsRes.inrequestAjax.error=((flash.error?:[]).size())?1:0//boolean to int
      hsRes.inrequestAjax.errorprop=flash.error
      render hsRes.inrequestAjax as JSON
      return
    }
    redirect(action:'addnew',params:hsRes.inrequest,base:hsRes.context.mainserverURL_lang)  
    return        
  }   
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def confirm={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if(hsRes.spy_protection){	  
      redirect(controller:'index',action:'captcha')
      return
    }
    def sCode=requestService.getStr('id')
    def iConf=requestService.getIntDef('confirm',0)
    if(sCode==''){
      if (!iConf)
        flash.error=1
    }else{
      def oUser=User.findWhere(code:sCode)
      def lRUid = 0
      if(oUser){
        def lsText=''
        oUser.modstatus=1
        oUser.code=''

        if(!oUser.save(flush:true)) {
          log.debug(" Error on save User")
          oUser.errors.each { log.debug(it)}
        }else{
          lsText=Email_template.findWhere(action:'#greeting_user')
          if(oUser.client_id){
            def oClient=Client.get(oUser.client_id)
            oClient.modstatus=1
            if( !oClient.save(flush:true)) {
              log.debug(" Error on save client")
              oClient.errors.each { log.debug(it)}
            }else
              lsText=Email_template.findWhere(action:'#greeting')
          }
        }
        if(oUser.is_external && oUser.provider=='staytoday'){
          def oRefUser = User.findByRef_id(oUser.id)
          if (oRefUser){
            lRUid = oRefUser.id
            oRefUser.modstatus=1
            if(!oRefUser.save(flush:true)) {
              log.debug(" Error on save User")
              oRefUser.errors.each { log.debug(it)}
            }
          }
        }
        def oNote = Note.findByUser_idAndNotetype_id(oUser.id,1)
        if (oNote){
          oNote.modstatus=0
          if(!oNote.save(flush:true)) {
            log.debug(" Error on save Note(user/confirm/) id:"+oNote.id)
            oNote.errors.each { log.debug(it)}
          }
        }
        def aZayavka = Zayavka.findAll('FROM Zayavka WHERE (user_id=:uId OR user_id=:ruId) and modstatus=-1',[uId:oUser.id,ruId:lRUid.toLong()])
        for (oZayavka in aZayavka){
          oZayavka.modstatus=0
          if(!oZayavka.save(flush:true)) {
            log.debug(" Error on save Zayavka(user/confirm/) id:"+oZayavka.id)
            oZayavka.errors.each { log.debug(it)}
          }
          mailerService.sendAdminZayavkaMail(oZayavka.id)
        }
        //<<Email
        if(lsText){
          def sText='[@EMAIL] Registration at StayToday'
          def sHeader="Registration at StayToday"
          if(lsText){
            sText=lsText.itext
            sHeader=lsText.title
          }
          sText=sText.replace('[@NICKNAME]',oUser.nickname).replace('[@EMAIL]',oUser.email)
          sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
          sHeader=sHeader.replace('[@EMAIL]',oUser.email)
          try{
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
              mailerService.sendMailGAE(sText,grailsApplication.config.grails.mail.from1,grailsApplication.config.grails.mail.username,oUser.email,sHeader,1)
            else{
              sendMail{
                to oUser.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          }catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString())
            hsRes.hinrequest.error=-100
          }
        //>>Email
        }
        if(!hsRes?.user){
          if (oUser.provider=='vkontakte'){
            def aVk_id = oUser.openid.split('_')
            def sStr=grailsApplication.config.vk.APIKey+aVk_id[1].toString()+grailsApplication.config.vk.SecretKey
            redirect(action:'vk',params:[vk_id:aVk_id[1],vk_hash:(sStr).encodeAsMD5(),act:'confirm',control:'user'], base:hsRes.context.mainserverURL_lang)
            return
          } else if (oUser.provider=='facebook') {
            def aFb_id = oUser.openid.split('_')
            redirect(controller:'user', action:'facebook',params:[fb_id:aFb_id[1],act:'confirm',control:'user'], base:hsRes.context.mainserverURL_lang)
            return
          } else {
            flash.user_id=oUser.id
            redirect(action:'login',params:[provider:'staytoday',act:'confirm',control:'user'], base:hsRes.context.mainserverURL_lang)
            return
          }
        }
      }else{
        flash.error=1
      }
    }
    return hsRes
  }
//////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////          
  def passwconfirm={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
	  def hsResult=[inrequest:[:]]
    def sCode=requestService.getStr('id')
    if(sCode=='')
      hsResult.inrequest.error=2
    else{
      def oUser=User.findWhere(code:sCode)
      if(!oUser)
        hsResult.inrequest.error=2
      else{
        session.regusercode=sCode
        session.startchange=true
        redirect(action:'passwsetup', base:hsRes.context.mainserverURL_lang)
        return
      }
    }
    render(view:'confirm',model:hsResult)
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////// 
   def passwsetup={
    requestService.init(this)
    def hsResult=requestService.getContextAndDictionary(false,true)
    if(hsResult.spy_protection){	  
      redirect(controller:'index',action:'captcha')
      return
    }
    hsResult.inrequest=[error:0]
    /*if(hsResult.user!=null){
      redirect(action:'index')
      return
    }*/
    def sCode=session.regusercode?:''
    if(sCode==''){
      redirect(action:'restore', base:hsResult.context.mainserverURL_lang)
      return
    } else {
      def oUser = User.findWhere(code:sCode)
      hsResult.inrequest.name = oUser.name
    }
    if(session.startchange?:false){
      session.startchange=false
      hsResult.inrequest.error=0
    }else{
      def sPassword1=requestService.getStr('password1')
      def sPassword2=requestService.getStr('password2')
      
      if(sPassword2!=sPassword1)
        hsResult.inrequest.error=1
      else if(sPassword2.size()<Tools.getIntVal(grailsApplication.config.user.passwordlength,5))
        hsResult.inrequest.error=2      
      else{
        def oNewUser
        def lsUsers=User.findAll('FROM User WHERE name=:email',[email:hsResult.inrequest.name])
        if((lsUsers?:[]).size()!=0){
          oNewUser=User.get(lsUsers[0].id)
          oNewUser.password=Tools.hidePsw(sPassword2)
          oNewUser.modstatus = oNewUser.modstatus?:1
          try{
            if( !oNewUser.save(flush:true)) {
              log.debug(" Error on save user")    
              oNewUser.errors.each { log.debug(it) }
            }else
              hsResult.inrequest.error=-1 //пароль изменен
            
          }catch(Exception e) {
            log.debug("Cannot save user \n"+e.toString())
            hsResult.inrequest.error=3 // general error
          }        
        }
        session.regusercode=null       
      }
    }
    
    if(hsResult.inrequest.error==-1){
      redirect(action:'login', params:[user:hsResult.inrequest.name,password:requestService.getStr('password1'),provider:'staytoday'], base:hsResult.context.mainserverURL_lang)
    }

    return hsResult     
  }    
  //////////////////////////////////////////////////////////////////////////////////////////////////  
  def restore={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if(hsRes.spy_protection){	  
      redirect(controller:'index',action:'captcha')
      return
    }
    hsRes.inrequest=[error:requestService.getIntDef('error',0),name:requestService.getStr('name')]
    if(hsRes.user!=null){
      redirect(action:'index', base:hsRes.context.mainserverURL_lang)
      return
    }

    return hsRes 
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////  
  def rest={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.inrequest=[:]
    if(hsRes.user!=null){
      redirect(action:'index', base:hsRes.context.mainserverURL_lang)
      return
    }
    
    hsRes.inrequest.name=requestService.getStr('name')
    hsRes.inrequest.error=0
    
    withForm{
      def oUser=User.findWhere(name:hsRes.inrequest.name)
      if(!oUser){       
        hsRes.inrequest.error=1 //USER NOT EXISTS
        redirect(action:"restore",params:hsRes.inrequest, base:hsRes.context.mainserverURL_lang)
        return
      }
      if(!Tools.checkEmailString(hsRes.inrequest.name)){
        hsRes.inrequest.error=2 //ERROR IN EMAIL
        redirect(action:"restore",params:hsRes.inrequest, base:hsRes.context.mainserverURL_lang)
        return
      }        
      try{
        if (! jcaptchaService.validateResponse("image", session.id, params.captcha)){
          hsRes.inrequest.error=3 //error in captha
          redirect(action:"restore",params:hsRes.inrequest, base:hsRes.context.mainserverURL_lang)
          return
        }
      }catch(Exception e){
        hsRes.inrequest.error=3 //error in captha
        redirect(action:"restore",params:hsRes.inrequest, base:hsRes.context.mainserverURL_lang)
        return
      } 
      if(oUser.is_external){
        hsRes.inrequest.error=4 //External USER
        redirect(action:"restore",params:hsRes.inrequest, base:hsRes.context.mainserverURL_lang)
        return
      }
	  if (!oUser.code) {
		oUser.code=java.util.UUID.randomUUID().toString()
		if(!oUser.save(flush:true)) {
			log.debug(" Error on save User:")
			oUser.errors.each{log.debug(it)}
		}
	  }
      def sCode=oUser.code     
      if((hsRes.inrequest.name?:'').size()>0){
        //<<Email
        def lsText=Email_template.findAllWhere(action:'#restore')
        def sText='[@EMAIL], for restore of your password use follow link [@URL]'
        def sHeader="Restore password" 
        if((lsText?:[]).size()>0){
          sText=lsText[0].itext
          sHeader=lsText[0].title
        }
        sText=sText.replace(
		'[@NICKNAME]',oUser.nickname).replace(
        '[@EMAIL]',hsRes.inrequest.name).replace(
        '[@URL]',(grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/user/passwconfirm/'+sCode))
        sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
        sHeader=sHeader.replace(
        '[@EMAIL]',hsRes.inrequest.name).replace(
        '[@URL]',(grailsApplication.config.grails.mailServerURL+'/user/passwconfirm/'+sCode))

        try{
          if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
            mailerService.sendMailGAE(sText,grailsApplication.config.grails.mail.from1,grailsApplication.config.grails.mail.username,hsRes.inrequest.name,sHeader,1)        
          else{
          sendMail{
            to hsRes.inrequest.name        
            subject sHeader
            body( view:"/_mail",
            model:[mail_body:sText])           
            }          
          }
        }catch(Exception e) {
          log.debug("Cannot sent email \n"+e.toString())          
          hsRes.inrequest.error=-100
          redirect(action:"restore",params:hsRes.inrequest, base:hsRes.context.mainserverURL_lang)
          return		  
        }
		//>>Email
      }
    }.invalidToken {
      hsRes.inrequest.error=5
      redirect(action:"restore",params:hsRes.inrequest, base:hsRes.context.mainserverURL_lang)
      return
    }
    return hsRes
  }
}
