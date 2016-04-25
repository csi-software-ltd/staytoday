//import org.codehaus.groovy.grails.commons.grailsApplication
import grails.converters.JSON
class HelpController {  
  def requestService
  def jcaptchaService
  def mailerService
  def MAIL_SUPPORT='info@staytoday.ru'
  def beforeInterceptor = [action:this.&checkId]
  def afterInterceptor = [action:this.&getCities]

  def checkId() {
    if (params.id){
      response.sendError(404)
      return
    }
  }
  def getCities(hsRes) {
    def cityList = Home.executeQuery("""select c.name,c.name_en,count(h.id),c.is_index,c.name2,c.country_id,c.domain
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > :minCount
      order by c.name""",["minCount":(Tools.getIntVal(grailsApplication.config.index.cityTagCloud.minCityCount,5) as long)])
    hsRes.citycloud = cityList.inject([:]){map,tag -> map[tag[hsRes.context?.lang?1:0]]=[count:tag[2],is_index:tag[3],name2:tag[hsRes.context?.lang?1:4],country_id:tag[5],domain:tag[6]];map}
    hsRes.citycloudParams = [:]
    hsRes.citycloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.max,50)
    hsRes.citycloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.middle,20)
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////// Help Hosting //////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def hosting_basic={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',54)
    return hsRes  
  }
  def hosting_account={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',55)
    return hsRes  
  }
  def hosting_ads={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',56)
    return hsRes  
  }
  def hosting_ad={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',57)
    return hsRes  
  }
  def hosting_photo={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',58)
    return hsRes  
  }
  def hosting_video={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',59)
    return hsRes  
  }
  def hosting_messages={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',60)
    return hsRes  
  }
  def hosting_trips={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',61)
    return hsRes  
  }
  def hosting_cancellation={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',62)
    return hsRes  
  }
  def hosting_reviews={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',63)
    return hsRes  
  }
  def hosting_functions={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=8 ORDER BY npage ASC')
    requestService.setStatistic('help',64)
    return hsRes  
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////// Help Traveling ////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def traveling_basic={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=9 ORDER BY npage ASC')
    requestService.setStatistic('help',46)
    return hsRes  
  }
  def traveling_personal={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=9 ORDER BY npage ASC')
    requestService.setStatistic('help',47)
    return hsRes  
  }
  def traveling_search={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=9 ORDER BY npage ASC')
    requestService.setStatistic('help',48)
    return hsRes  
  }
  def traveling_messages={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=9 ORDER BY npage ASC')
    requestService.setStatistic('help',49)
    return hsRes  
  }
  def traveling_trips={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=9 ORDER BY npage ASC')
    requestService.setStatistic('help',50)
    return hsRes  
  }
  def traveling_cancellation={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=9 ORDER BY npage ASC')
    requestService.setStatistic('help',51)
    return hsRes  
  }
  def traveling_reviews={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=9 ORDER BY npage ASC')
    requestService.setStatistic('help',52)
    return hsRes  
  }
  def traveling_functions={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.help_submenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=9 ORDER BY npage ASC')
    requestService.setStatistic('help',53)
    return hsRes  
  } 
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def faq={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    if(request.getHeader("User-Agent")?.contains('Mobile')&&!request.getHeader("User-Agent")?.contains('iPad'))
      redirect(uri:'/help',base:hsRes.context?.mobileURL_lang,permanent:true)    
    hsRes.urlvideo=grailsApplication.config.urlvideolesson
    hsRes.lessons=Video_lesson.findAllByType_idNotEqual(3,[sort:'nomer',order:'asc'])
    requestService.setStatistic('help',45)
    return hsRes
  }  
  //////////////////////////////////////////////////////////////////////////////////////////////////  
  def guestbook={ 
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,true,true,false,true)
  /*if(hsRes.spy_protection){	  
      redirect(controller:'index',action:'captcha')
	  return
    }*/
    //hsRes.part = requestService.getStr('part')
    if (hsRes.user){
      hsRes.inrequest = [error:0]
      if (hsRes.user.provider!='staytoday')
        hsRes.inrequest.fio = hsRes.user.name
      else
        hsRes.inrequest.email = hsRes.user.name
    }
    def gbtype_id = requestService.getLongDef('gbtype_id',1)
    hsRes += getDictionary()
    hsRes.gbtype = Gbtype.get(gbtype_id)
    hsRes.mail_support=grailsApplication.config.mail.support?:MAIL_SUPPORT
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def add = {
    requestService.init(this)    
    def hsRes = requestService.getContextAndDictionary(false,true,true)
	/*if(hsRes.spy_protection){	  
	  redirect(controller:'index',action:'captcha')
	  return
    }*/	
    //hsRes.part = requestService.getStr('part')
    hsRes.inrequest = getMessageParams()
    
    if (!request.post)
      hsRes.inrequest.error=1
    else if ((hsRes.inrequest.email=='')||(!Tools.checkEmailString(hsRes.inrequest.email)))
      hsRes.inrequest.error = 2
    else if (hsRes.inrequest.rectitle=='')
      hsRes.inrequest.error = 3
    else if (hsRes.inrequest.fio.size()>150)
      hsRes.inrequest.error = 4
    else if (hsRes.inrequest.email.size()>50)
      hsRes.inrequest.error = 5
    else if (hsRes.inrequest.adr.size()>250)
      hsRes.inrequest.error = 6
    else if ((hsRes.inrequest.tel_code.size()>50))
      hsRes.inrequest.error = 7
    else if ((hsRes.inrequest.tel.size()>50))
      hsRes.inrequest.error = 8
    else if (hsRes.inrequest.home.size()>150)
      hsRes.inrequest.error = 9
    else if (hsRes.inrequest.rectitle.size()>150)
      hsRes.inrequest.error = 10
    else if (hsRes.inrequest.rectext=='')
      hsRes.inrequest.error = 11
    else if (!hsRes.user){
      try{
        if (! jcaptchaService.validateResponse("image", session.id, params.captcha))
          hsRes.inrequest.error=99
      }catch(Exception e){
        hsRes.inrequest.error=99
      }
    }
    if (hsRes.inrequest.error==0){	
      def oGuestbook1=new Guestbook()   
      hsRes.ip=oGuestbook1.csiGetIpCount("'"+request.getRemoteAddr()+"'")	  

      def aTmpRectext=hsRes.inrequest.rectext.split(/a\shref/)
      if(aTmpRectext.size()==1){
        if(hsRes.ip.count<Tools.getIntVal(grailsApplication.config.guestbook.ip_max,10)){		  	
          try{		
            def oGuestbook = new Guestbook(user_id  :(hsRes.user?.id)?:0,
                                       gbtype_id :hsRes.inrequest.gbtype_id,                                       
                                       fio       :hsRes.inrequest.fio,
                                       adr       :hsRes.inrequest.adr,
                                       tel       :hsRes.inrequest.tel,
                                       tel_code  :hsRes.inrequest.tel_code,
                                       email     :hsRes.inrequest.email,
                                       rectitle  :hsRes.inrequest.rectitle,
                                       rectext   :hsRes.inrequest.rectext,
                                       home      :hsRes.inrequest.home,
                                       //gberrortype_id:0,
                                       recinfo   :'',
                                       modstatus :0,
                                       ip        :request.getRemoteAddr())
            if (!oGuestbook.save(flush:true)){
              log.debug(" Error on save Guestbook:")    
              oGuestbook.errors.each{log.debug(it)}
              hsRes.inrequest.error = 9000
            }
            def iInsertId=oGuestbook.csiGetLastInsert() 
            session.guestbookinsertid=iInsertId
            redirect(controller: "help", action: "done", base:hsRes.context.mainserverURL_lang)//,params:[part:hsRes.part])
          }catch(Exception e){
            log.debug('Guestbook:add. Error on add Guestbook \n'+e.toString())
            hsRes.inrequest.error = 9000
          }
        }else{
          hsRes.inrequest.error = 100
        }
      }else{
        hsRes.inrequest.error = 9000
        requestService.setStatistic('guestbook',21)//спамовый запрос
      }
    } else {hsRes.err = 1}
    hsRes += getDictionary()
    hsRes += [gbtype:Gbtype.get(hsRes.inrequest.gbtype_id)]
	
    hsRes.mail_support=grailsApplication.config.mail.support?:MAIL_SUPPORT
	
    render(view:'guestbook',model:hsRes)
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def done = {
    def hsRes = [done:false]	
    requestService.init(this)
    hsRes += requestService.getContextAndDictionary(false,true,true)    
    if(hsRes.spy_protection){	  
      redirect(controller:'index',action:'captcha')
      return
    }
    
    def iId = -1
    try{
      iId = session.guestbookinsertid.toLong()
    }catch(Exception e){
      iId = -1
    }
    session.guestbookinsertid=0
    hsRes.done = (iId>0)	
    if(hsRes.done){
      def oGuestbook = Guestbook.get(iId)
      if(oGuestbook == null)
        hsRes.done = false
      else{
        def iType = 0
        if (oGuestbook.gbtype_id == 2) iType = 18
        else if (oGuestbook.gbtype_id == 3) iType = 19
        else if (oGuestbook.gbtype_id == 1) iType = 20
        requestService.setStatistic('guestbook',iType)
		
        if (grailsApplication.config.mail.guestbook.to){
          def sHtml= """
                  id                ${iId}
                  gbtype_id         ${oGuestbook.gbtype_id}
                  user_id           ${oGuestbook.user_id}                  
                  fio               ${oGuestbook.fio}
                  email             ${oGuestbook.email}
                  tel_code          ${oGuestbook.tel_code}
                  tel               ${oGuestbook.tel}
                  adr               ${oGuestbook.adr}
                  home              ${oGuestbook.home}
                  rectitle          ${oGuestbook.rectitle}
                  rectext           ${oGuestbook.rectext}
                  """
          def sSubject="Guestbook  ${iId}, ${oGuestbook.rectitle}"
          
          if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
             mailerService.sendMailGAE(sHtml,grailsApplication.config.grails.mail.from1,grailsApplication.config.grails.mail.username,grailsApplication.config.mail.guestbook.to,sSubject)        
          else{
            sendMail{
              to grailsApplication.config.mail.guestbook.to
              subject sSubject
              html sHtml
              log.debug("LOG>>Sent mail guestbook 'Guestbook  ${iId}, ${oGuestbook.rectitle}'")
            }  
          }
        }
      }
    }
    hsRes.mail_support=grailsApplication.config.mail.support?:MAIL_SUPPORT
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def selectgbtype = {
    requestService.init(this)
    def gbtype_id = requestService.getIntDef('gbtype_id',1)
    def gbtype = Gbtype.get(gbtype_id)
    render ([is_extfields:gbtype.is_extfields] as JSON)
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def getMessageParams(){
    requestService.init(this)
    def hsRequest = [error:0]
    try{
      hsRequest.gbtype_id  = requestService.getIntDef('gbtype',1)     
      hsRequest.fio        = requestService.getStr('fio')
      hsRequest.email      = requestService.getStr('email')
      hsRequest.adr        = requestService.getStr('adr')
      hsRequest.tel        = requestService.getPhone('tel')
      hsRequest.tel_code   = requestService.getPhoneCode('tel_code')
      hsRequest.rectext    = requestService.getStr('rectext')
      hsRequest.rectitle   = requestService.getStr('rectitle')
      hsRequest.home    = requestService.getStr('home')
    }catch(Exception e){
      hsRequest.error = 100
    }
    return hsRequest
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def getDictionary(){
    return [gbtypes: Gbtype.findAll('FROM Gbtype')]
  }
}
