//import org.codehaus.groovy.grails.commons.grailsApplication
import grails.converters.JSON

class ErrorController {
  def requestService
  def mailerService
  def smsService
  def usersService
  def jcaptchaService
  def homeService

  def page_404 = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    if(Tools.getIntVal(grailsApplication.config.loginDenied.isLoginDenied,0)) {
      hsRes.isLoginDenied=true
      hsRes.loginDeniedText=grailsApplication.config.loginDenied.text?:'Сервис временно недоступен'
    }
    hsRes.hometype=Hometype.list([sort:'regorder',order:'asc'])
    hsRes.homeperson=Homeperson.list()
    hsRes.popdirection=Popdirection.findAllByModstatusAndIs_main(1,1,[sort:'rating',order:'desc'])    
    hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
    
    hsRes.countryIds = hsRes.popdirection.collect{it.country_id}
    hsRes.countryIds.unique()
    hsRes.countries = Country.list()

    def oHomeSearch=new HomeSearch()
    hsRes.specoffer_records=oHomeSearch.csiFindSpecoffer()
    //hsRes.specoffer_records.each{requestService.setStatistic('specoffer',0,it.id)}
    hsRes.urlphoto = grailsApplication.config.urlphoto        
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    
    //transliterate////////////////////////////////////////////   
    if(hsRes.context.lang){      
      def lsSpecoffer_records=[]
      for(record in hsRes.specoffer_records){                                      
        lsSpecoffer_records<<record.csiSetEnHomeSearch()             
      }  
      hsRes.specoffer_records=lsSpecoffer_records            
    }
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def page_500 = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)

    if (!Tools.getIntVal(grailsApplication.config.isdev,0)&&request?.exception?.stackTraceLines?.find{it.toString().trim().matches('.*OutOfMemoryError.*')}){
      def oNotice = Admin_notice.findByIs_read(1)
      if(oNotice?.type==1) {
        oNotice.is_read = 0
        mailerService.sendAdminNoticeMail(oNotice)
      } else if(oNotice?.type==2){
        oNotice.is_read = 0
        smsService.sendAdminNoticeSMS(oNotice)
      }
      try {
        oNotice?.save(flush:true)
      } catch(Exception e) {}
    }

    hsRes.hometype=Hometype.list([sort:'regorder',order:'asc'])
    hsRes.homeperson=Homeperson.list()
    hsRes.popdirection=Popdirection.findAllByModstatusAndIs_main(1,1,[sort:'rating',order:'desc'])    
    hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)

    hsRes.countryIds = hsRes.popdirection.collect{it.country_id}
    hsRes.countryIds.unique()
    hsRes.countries = Country.list()

    def oHomeSearch=new HomeSearch()
    hsRes.specoffer_records=oHomeSearch.csiFindSpecoffer()
    hsRes.specoffer_records.each{requestService.setStatistic('specoffer',0,it.id)}
    hsRes.urlphoto = grailsApplication.config.urlphoto        
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    
    //transliterate////////////////////////////////////////////   
    if(hsRes.context.lang){      
      def lsSpecoffer_records=[]
      for(record in hsRes.specoffer_records){                                      
        lsSpecoffer_records<<record.csiSetEnHomeSearch()
      }  
      hsRes.specoffer_records=lsSpecoffer_records            
    }

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def page_401 = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)

    hsRes.hometype=Hometype.list([sort:'regorder',order:'asc'])
    hsRes.homeperson=Homeperson.list()
    hsRes.popdirection=Popdirection.findAllByModstatusAndIs_main(1,1,[sort:'rating',order:'desc'])    
    hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)

    hsRes.countryIds = hsRes.popdirection.collect{it.country_id}
    hsRes.countryIds.unique()
    hsRes.countries = Country.list()

    def oHomeSearch=new HomeSearch()
    hsRes.specoffer_records=oHomeSearch.csiFindSpecoffer()
    hsRes.specoffer_records.each{requestService.setStatistic('specoffer',0,it.id)}
    hsRes.urlphoto = grailsApplication.config.urlphoto
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    
    //transliterate////////////////////////////////////////////   
    if(hsRes.context.lang){      
      def lsSpecoffer_records=[]
      for(record in hsRes.specoffer_records){                                 
        lsSpecoffer_records<<record.csiSetEnHomeSearch()
      }  
      hsRes.specoffer_records=lsSpecoffer_records            
    }

    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////////
  def login = {  
    requestService.init(this)
    requestService.setCookie('user','parararam',10000)
    def hsRes=requestService.getContextAndDictionary(true)
    def hsInrequest = requestService.getParams(['is_ajax','service'],['user_index','id'],['control','act','what','where']).inrequest
    hsInrequest+=requestService.getParams([],['home_id'],['code']).inrequest
    def sUser=requestService.getStr('user')
    def sProvider=requestService.getStr('provider')
    def sPassword=requestService.getStr('password')
    def sMoveTo=request.getHeader('referer')
    def iRemember=requestService.getIntDef('remember',0)
    if(!flash.user_id){//>>not from confirm
      if((hsInrequest?.user_index?:0) && session.user_enter_fail>Tools.getIntVal(grailsApplication.config.user_max_enter_fail,10)){
        try{
          if (! jcaptchaService.validateResponse("image", session.id, params.captcha)){
            flash.error=99 //error in captha
            redirect(action:'index',params:hsInrequest,base:hsRes.context.sequreServerURL)
            return
          } else {
            session.user_enter_fail=null
          }
        }catch(Exception e){
          flash.error=99 //error in captha
          redirect(action:'index',params:hsInrequest,base:hsRes.context.sequreServerURL)
          return
        }
      }
      if((sUser=='')||(sProvider=='')||(sUser=='логин')){
        flash.error = 1 // set user and provider  
        redirect(action:'index',params:hsInrequest,base:hsRes.context.sequreServerURL)
        return
      }
      if (!User.findWhere(email:sUser)){
        hsInrequest.user = sUser
        flash.error = 3 // new user. need registration
        redirect(action:'index',params:hsInrequest,base:hsRes.context.sequreServerURL)
        return
      }
      if (!sPassword){
        hsInrequest.user = sUser
        flash.error = 2 // empty password is not allowed
        redirect(action:'index',params:hsInrequest,base:hsRes.context.sequreServerURL)
        return
      }
    }//<<not from confirm
    if(sProvider.toLowerCase()==usersService.INTERNALPROVIDER){ 
      if(!usersService.loginInternalUser(sUser,sPassword,requestService,iRemember,flash?.user_id?:0)){
        flash.error=51 // Wrong password or user does not exists
      }else {
        def oUser=User.findWhere(email:sUser)
        if(oUser!=null){
          oUser.lastdate=new Date()
          if(!oUser.save(flush:true)) {
            log.debug(" Error on save User:")
            oUser.errors.each{log.debug(it)}
          }
        }
      }
      hsInrequest.user=sUser
      redirect(controller:'error',action:'index',params:hsInrequest,base:hsRes.context.sequreServerURL)
      return
    }
  }
  def facebook={
    requestService.init(this)
    requestService.setCookie('user','parararam',10000)
    def hsRes=requestService.getContextAndDictionary(true)
    def hsInrequest = requestService.getParams(['is_ajax','service'],['id','m_fb_id','home_id','fb_id'],['fb_name','fb_pic','fb_photo','fb_email','control','act','what','where']).inrequest
    if (hsInrequest.fb_id?:0){
      if(!usersService.loginUser('f_'+hsInrequest.fb_id.toString(),'','facebook',requestService,'')){
          // if user banned
          flash.error=5
      }
    } else if(hsInrequest.m_fb_id?:0 /*facebookConnectService.isLoggedIn(request)*/) {
      try {
        //def lId=facebookConnectService.getFacebookClient().users_getLoggedInUser()    
        def bNewUser = true
        if (User.findWhere(openid:'f_'+hsInrequest.m_fb_id.toString())?.ref_id?:0)
          bNewUser = false
        //def useObj = facebookConnectService.getFacebookUser()
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

    redirect(action:'index',params:hsInrequest,base:hsRes.context.sequreServerURL)
    return  
  }
  def vk={
    requestService.init(this)
    requestService.setCookie('user','parararam',10000)
    def hsRes=requestService.getContextAndDictionary(true)
    def hsInrequest = requestService.getParams(['is_ajax','service'],['id','vk_id','home_id'],['vk_pic','vk_photo','vk_name','vk_hash','control','act','what','where']).inrequest      
      
    def sStr=grailsApplication.config.vk.APIKey+hsInrequest.vk_id.toString()+grailsApplication.config.vk.SecretKey

    def bNewUser = true
    if (User.findWhere(openid:'vk_'+hsInrequest.vk_id.toString())?.ref_id?:0)
        bNewUser = false
    //if(hsInrequest.vk_hash==(sStr).encodeAsMD5()){
      
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
    /*}else{
      flash.error=3
    }*/

    redirect(action:'index', params:hsInrequest,base:hsRes.context.sequreServerURL)
    return  
  }   

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
    render 'error'
  }  
}
