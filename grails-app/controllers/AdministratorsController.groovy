//import java.util.Calendar
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import java.text.SimpleDateFormat

class AdministratorsController {
  
  def requestService
  def imageService
  def mailerService
  def zayavkaService
  def usersService
  def smsService
  def androidGcmService
  def billingService

  def static final COOKIENAME = 'admin'
  def static final BASEINFO_PICSESSION = 'admin_baseinfo_pics'
  def static final BASEINFO_KEEPPICSESSION = 'admin_baseinfo_keeppics'
  def beforeInterceptor = [action:this.&checkAdmin,except:['login','index']]
  def static final DATE_FORMAT='yyyy-MM-dd'
  
  def checkAdmin() {
    if(session?.admin?.id!=null){
      def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
      session.attention_message=oTemp_notification?oTemp_notification.text:null
      if (session.admin?.menu?.find{it.id==4}) {
        session.admin.home_notmoderate_count = Home.countByIs_confirmedAndModstatus(0,1)
        if (session.admin.home_notmoderate_count==1) {
          session.admin.home_notmoderate_id = Home.findByIs_confirmedAndModstatus(0,1)?.id?:0
        }
      }      
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
      redirect(controller:'administrators', action:'index', params:[redir:1], base:(Tools.getIntVal(Dynconfig.findByName('global.https.enable')?.value,0)?(ConfigurationHolder.config.grails.secureServerURL?:ConfigurationHolder.config.grails.serverURL):ConfigurationHolder.config.grails.serverURL))
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
      redirect(action:'profile');
      return false
    }
    return true
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    if (session?.admin?.id){
      redirect(action:'profile')
      return
    } else return params
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def login = {
    requestService.init(this)
    def sAdmin=requestService.getStr('login')
    def sPassword=requestService.getStr('password')	
    
    if (sAdmin==''){
      flash.error = 1 // set login
      redirect(controller:'administrators',action:'index')//TODO change action
      return
    }
    def oAdminlog = new Adminlog()
    def blocktime = Tools.getIntVal(ConfigurationHolder.config.admin.blocktime,1800)
    def unsuccess_log_limit = Tools.getIntVal(ConfigurationHolder.config.admin.unsuccess_log_limit,5)
    sPassword=Tools.hidePsw(sPassword)
    def oAdmin=Admin.find('from Admin where login=:login',
                             [login:sAdmin.toLowerCase()])
    if(!oAdmin){
      flash.error=2 // Wrong password or admin does not exists
      redirect(controller:'administrators',action:'index')
      return
    }else if (oAdminlog.csiCountUnsuccessLogs(oAdmin.id, new Date(System.currentTimeMillis()-blocktime*1000))[0]>=unsuccess_log_limit){
      flash.error=3 // Admin blocked
      oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:2)
      if (!oAdminlog.save(flush:true)){
        log.debug('error on save Adminlog in Admin:login')
        oAdminlog.errors.each{log.debug(it)}
      }
      redirect(controller:'administrators',action:'index')
      return	
    }else if (oAdmin.password != sPassword) {
      flash.error=2 // Wrong password or admin does not exists
      oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:0)
      if (!oAdminlog.save(flush:true)){
        log.debug('error on save Adminlog in Admin:login')
        oAdminlog.errors.each{log.debug(it)}
      }
      redirect(controller:'administrators',action:'index')
      return
    }	
    
    def oAdminmenu = new Adminmenu()
    session.admin = [id            : oAdmin.id,
                     login         : oAdmin.login,                     
                     group         : oAdmin.admingroup_id,
                     menu          : oAdminmenu.csiGetMenu(oAdmin.admingroup_id),
                     accesslevel   : oAdmin.accesslevel,
                     regions       : oAdmin.accesslevel?[]:oAdmin.csiGetRegionIds(oAdmin.id),
                     mbox_notmoderate_count   : 0                     
                    ]					       
  	
    if(!session.admin.accesslevel && !session.admin.regions)
      session.admin.regions=[-1]

    //println(session.admin)
    oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:1)
    if (!oAdminlog.save(flush:true)){
      log.debug('error on save Adminlog in Admin:login')
      oAdminlog.errors.each{log.debug(it)}
    }
    redirect(action:'homes',params:[ext:1])
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def logout = {
    requestService.init(this)
    session.admin = null
    
    redirect(controller:'administrators',action: 'index')
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def menu = {
    requestService.init(this)
    def iPage = requestService.getIntDef('menu',1)	
    switch (iPage){	
      case  1: redirect(action:'profile'); return
      case  2: redirect(action:'administration'); return
      case  3: redirect(action:'users'); return	 	  
      case  4: redirect(action:'homes'); return
      case  5: redirect(action:'infotext'); return
      case  6: redirect(action:'reviews'); return
      case  7: redirect(controller:'stats',action:'index'); return
      case  8: redirect(action:'adress'); return
      case  9: redirect(action:'showbanners'); return 
      case 10: redirect(action:'popdir'); return      
      case 11: redirect(action:'guestbook'); return
      case 12: redirect(action:'notetype'); return
      case 13: redirect(action:'proposal'); return
      case 14: redirect(action:'sms'); return
      case 15: redirect(action:'mail'); return
      case 16: redirect(action:'mbox'); return
      case 17: redirect(action:'article'); return
      case 18: redirect(action:'clients'); return
      case 19: redirect(controller:'adminbilling',action:'paytransfer'); return
      case 20: redirect(action:'selection'); return
      case 21: redirect(controller:'adminbilling',action:'payorder'); return
      case 22: redirect(controller:'adminbilling',action:'account'); return
      case 23: redirect(controller:'adminbilling',action:'paytrans'); return
      case 25: redirect(controller:'adminbilling',action:'paytask'); return
      case 26: redirect(action:'anons'); return
      case 27: redirect(controller:'adminbilling',action:'payreport'); return
      case 28: redirect(controller:'adminbilling',action:'trip'); return
      case 29: redirect(action:'shome'); return
      default: redirect(action:'profile'); return
    }
    return [admin:session.admin,action_id:iPage]
  }
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////Administrator`s profile >>>//////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def profile = {
    requestService.init(this)
    def hsRes = [administrator:Admin.get(session.admin.id),action_id:1]	
    hsRes.admin = session.admin
    def oAdminlog = new Adminlog()
    def lsLogs = oAdminlog.csiGetLogs(hsRes.admin.id)
    if (lsLogs.size()>1){
      hsRes.lastlog = lsLogs[1]
      hsRes.unsuccess_log_amount = oAdminlog.csiCountUnsuccessLogs(hsRes.admin.id, new Date()-7)[0]
      hsRes.unsuccess_limit = Tools.getIntVal(ConfigurationHolder.config.admin.unsuccess_log_showlimit,5)
    }
    hsRes.groupname = Admingroup.get(hsRes.administrator.admingroup_id).name
	  /*if(requestService.getLongDef('ext',0))
	    hsRes.temp_notification=Temp_notification.findWhere(id:2,status:1)*/
    return hsRes
  }
 ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def profilesave = {
    checkAccess(1)
    requestService.init(this)
    def hsRes = requestService.getParams([],[],['name','email'])	
    hsRes.inrequest.id=session.admin.id	
    if (hsRes.inrequest.id){
      def oAdmin = Admin.get(hsRes.inrequest.id)             
      oAdmin.name = hsRes.inrequest.name?:''
      oAdmin.email = hsRes.inrequest.email?:''        
      if (!oAdmin.save(flush:true)){
        log.debug('error on save Admin: Administrators.usersave')
        oAdmin.errors.each{log.debug(it)}
      } 
    }
    redirect(action:'profile')
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def changepass = {
    requestService.init(this)
    def hsRes = [done:true,message:'Ошибка']
    def sPass = requestService.getStr('pass')
    def lAjax = requestService.getLongDef('ajax',0)
    if (lAjax) checkAccess(2)
    def lId = lAjax?requestService.getLongDef('id',0):session.admin.id
    //def lId = session.admin.id
    /*if(!sPass)
      flash.error=1
    else*/
	if(sPass.size()<Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,5)){
      flash.error=3	
      hsRes = [done: false,message:message(code:'admin.passw.min.length.error', default:'')+' '+Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,5)]	  
    }else if (lId>1){
      if (sPass==requestService.getStr('confirm_pass')){
        def oAdmin = new Admin()
        oAdmin.changePass(lId,Tools.hidePsw(sPass))
        hsRes.message = message(code:'passw.done', default:'')
        flash.error=0
      } else {
        hsRes = [done: false,message:message(code:'admin.passwordequal.error', default:'')]        
        flash.error=2
      }
    }
    if (lAjax){
      render hsRes as JSON
      return
    }
    redirect(action:'profile')
  }

  def resizeExistingThumbs={
    checkAccess(2)
    def pathtophoto = ConfigurationHolder.config.pathtophoto
    imageService.init(this,'userphotopic','userphotokeeppic',pathtophoto+File.separatorChar)
    def cl_id
    Homephoto.list().each{
      cl_id = Home.get(it.home_id)?.client_id?:0
      if (cl_id>0) {
        imageService.resizeExistingThumbs(
          it.picture,
          pathtophoto+cl_id.toString()+File.separatorChar,
          Tools.getIntVal(ConfigurationHolder.config.photo.thumb.size,100),//thumb size
          Tools.getIntVal(ConfigurationHolder.config.photo.thumb.height,74),//thumb height
          false,//square
          false//bSaveThumpWithSize
        )
      }
    }
    render ""
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Administrator`s profile <<<///////////////////////////////////////////////////////// 
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Users >>>///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def users = {    
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=3
    hsRes.admin = session.admin	
    hsRes.provider=Provider.findAll('FROM Provider')	
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto    
    
    def fromModer = requestService.getIntDef('fromModer',0)
    if (fromModer){           
      hsRes.inrequest = session.lastRequest
      
      try {
        hsRes.inrequest.registr_date_from=Date.parse(DATE_FORMAT, hsRes.inrequest?.registr_date_from)
        hsRes.inrequest.registr_date_to=Date.parse(DATE_FORMAT, hsRes.inrequest?.registr_date_to)
        hsRes.inrequest.enter_date_from=Date.parse(DATE_FORMAT, hsRes.inrequest?.enter_date_from)
        hsRes.inrequest.enter_date_to=Date.parse(DATE_FORMAT, hsRes.inrequest?.enter_date_to)        
      } catch(Exception e) {}      
    }else{
      hsRes.inrequest=[:]
      hsRes.inrequest.modstatus = -2
      hsRes.inrequest.is_client = -1
      hsRes.inrequest.telchek = -2
      hsRes.inrequest.user_id=requestService.getStr('user_id')
    }
    
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////////
  def userlist = {
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=3
    hsRes.admin = session.admin
    def oUser=new User()	
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto          
  
    hsRes+=requestService.getParams(['ncomment'],['order','user_id'],['name','provider','nickname'],['registr_date_from','registr_date_to','enter_date_from','enter_date_to'])
    hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-2)
    hsRes.inrequest.is_client = requestService.getIntDef('is_client',-1)
    hsRes.inrequest.telchek = requestService.getIntDef('telchek',-2)    
    
    session.lastRequest = [:]
    session.lastRequest = hsRes.inrequest      
      
    hsRes+=oUser.csiSelectUsers(hsRes.inrequest?.name?:'',hsRes.inrequest.nickname?:'',hsRes.inrequest.provider?:'',hsRes.inrequest.modstatus,hsRes.inrequest.is_client,hsRes.inrequest?.ncomment?:0,hsRes.inrequest?.user_id?:0,hsRes.inrequest?.telchek,hsRes.inrequest.registr_date_from?:'',hsRes.inrequest?.registr_date_to?:'',hsRes.inrequest.enter_date_from?:'',hsRes.inrequest?.enter_date_to?:'',hsRes.inrequest.order?:0,20,requestService.getOffset())    

    return hsRes
  }

  def banned={
    requestService.init(this)
    def iId=requestService.getLongDef('id',0)
    def iBanned=requestService.getLongDef('banned',0)

    if(iId>0){
      def oUser=User.get(iId)
      oUser.banned=iBanned
	
      if(!oUser.save(flush:true)) {
        log.debug(" Error on save User:")
        oUser.errors.each{log.debug(it)}
      }
      if (iBanned==1&&oUser?.client_id!=0) {
        for(home in Home.findAllByClient_id(oUser.client_id)) {
          home.modstatus = -1
          if(!home.save(flush:true)) {
            log.debug(" Error on save Home:")
            home.errors.each{log.debug(it)}
          }
        }
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def telchek={
    requestService.init(this)
    def iId=requestService.getLongDef('id',0)
    def iTelchek=requestService.getLongDef('telchek',0)

    if(iId>0){
      def oUser=User.get(iId)
	  if(oUser?.tel){
		oUser.is_telcheck=iTelchek
	
		if(!oUser.save(flush:true)) {
          log.debug(" Error on save User(administrator/telchek/"+iId+"):")
          oUser.errors.each{log.debug(it)}
		}
	  }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def agentchek={
    requestService.init(this)
    def iId=requestService.getLongDef('id',0)
    def iAgentchek=requestService.getLongDef('agentchek',0)

    if(iId>0){
      def oUser=User.get(iId)
	  if(oUser?.is_agent){
		oUser.is_agentcheck=iAgentchek
	
		if(!oUser.save(flush:true)) {
          log.debug(" Error on save User(administrator/agentchek/"+iId+"):")
          oUser.errors.each{log.debug(it)}
		}
	  }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def loginAsUser={
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=3
    hsRes.admin = session.admin
    
    def lId=requestService.getLongDef('id',0)
	def oUser = User.get(lId)
	if (oUser) {
	  if (oUser.provider == 'staytoday')
		usersService.loginInternalUser(oUser.name, '', requestService, 0, oUser.id)
	  else 
		usersService.loginUser(oUser.openid, oUser.name, oUser.provider, requestService, oUser.picture, oUser.ref_id)


	  render(contentType:"application/json"){[error:false]}
	}

	render(contentType:"application/json"){[error:true]}
  }

  def getUserPass={
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=3
    hsRes.admin = session.admin
    
    def lId=requestService.getLongDef('id',0)
    def oUser = User.get(lId)
    if (oUser) {
      render oUser as JSON
    }

    render(contentType:"application/json"){[error:true]}
  }

  def changeUserPass={
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=3
    hsRes.admin = session.admin
    
    def lId=requestService.getLongDef('id',0)
    def oUser = User.get(lId)
    if (oUser) {
      def sPass=requestService.getStr('pass')
      def sPass2=requestService.getStr('pass2')
      if(sPass2!=sPass){
        hsRes.error = true
        hsRes.message = message(code:'admin.changeUserPass.notEqual.error',args:[], default:'').toString()
      } else if(sPass2.size()<Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,5)){
        hsRes.error = true
        hsRes.message = message(code:'admin.changeUserPass.tooEasy.error',args:[], default:'').toString()
      } else  {
        oUser.password=Tools.hidePsw(sPass2)
        if( !oUser.save(flush:true)) {
          log.debug(" Error on save user")
          hsRes.error = true
          hsRes.message = message(code:'admin.changeUserPass.savePass.error',args:[], default:'').toString()
        }
      }
    }
    render hsRes as JSON
  }

  def getUserforSMS={
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=3
    hsRes.admin = session.admin
    
    def lId=requestService.getLongDef('id',0)
    def oUser = User.get(lId)
    if (oUser) {
      render oUser as JSON
    }

    render(contentType:"application/json"){[error:true]}
  }

  def sendUserSMS={
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=3
    hsRes.admin = session.admin
    
    def lId=requestService.getLongDef('id',0)
    def oUser = User.get(lId)
    if (oUser) {
      def sText=requestService.getStr('stext')
      if(!sText){
        hsRes.error = true
        hsRes.message = message(code:'admin.sendUserSMS.emptyText.error',args:[], default:'').toString()
      } else if(!oUser.tel) {
        hsRes.error = true
        hsRes.message = message(code:'admin.sendUserSMS.notel.error',args:[], default:'').toString()
      } else {
        def result = smsService.sendSingleSmsToUser(oUser,sText)
        switch (result){
          case 0: break;
          case 1: break;
          case 110:
            hsRes.error = true
            hsRes.message = message(code:'admin.sendUserSMS.err110.error',args:[], default:'').toString()
            break;
          case 213:
          case 211:
            hsRes.error = true
            hsRes.message = message(code:'admin.sendUserSMS.err213.error',args:[], default:'').toString()
            break;
          case 241:
            hsRes.error = true
            hsRes.message = message(code:'admin.sendUserSMS.err241.error',args:[], default:'').toString()
            break;
          default:
            hsRes.error = true
            hsRes.message = message(code:'admin.sendUserSMS.unspecific.error',args:[], default:'').toString()
            break;
        }
      }
    }
    render hsRes as JSON
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Users <<<///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Homes >>>///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  def homes = {    
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=4
    hsRes.admin = session.admin	
    
    def fromDetails = requestService.getIntDef('fromDetails',0)
    if (fromDetails){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetails = 1
      try {
        hsRes.inrequest.inputdate=Date.parse(DATE_FORMAT, hsRes.inrequest?.inputdate)
      } catch(Exception e) {}     
    }else{
      hsRes.inrequest =[:]
      hsRes+=requestService.getParams(null,['user_id'])
    }

    if(hsRes.admin.accesslevel){    
      hsRes.country=Country.findAll('FROM Country ORDER BY regorder')
      if(hsRes.inrequest?.country_id?:0){
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
        hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id ORDER BY rating DESC',[country_id:hsRes.inrequest?.country_id])
      } else if((hsRes.country?:[]).size()>0){
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])
        hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id ORDER BY rating DESC',[country_id:hsRes.country[0].id])
      }
    }else{               
      def lsCountryIds=[]
      
      def lsRegions=Region.findAll("FROM Region WHERE id IN (:ids)",[ids:hsRes.admin.regions])
      
      for(oRegion in lsRegions){
        if(!(oRegion.country_id in lsCountryIds))
          lsCountryIds<<oRegion.country_id
      }            
      
      hsRes.country=Country.findAll('FROM Country WHERE id IN (:ids) ORDER BY regorder',[ids:lsCountryIds?:[-1]])
      
      if(hsRes.inrequest?.country_id?:0){
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id AND id IN (:ids) ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id,ids:hsRes.admin.regions])
        def lsPopdirIds=[]
        for(oReg in hsRes.region){
          lsPopdirIds<<oReg.popdirection_id
        }
        hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id AND id IN (:ids) ORDER BY rating DESC',[country_id:hsRes.inrequest?.country_id,ids:lsPopdirIds?:[-1]])
      } else if((hsRes.country?:[]).size()>0){
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id AND id IN (:ids) ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id,ids:hsRes.admin.regions])
        def lsPopdirIds=[]
        for(oReg in hsRes.region){
          lsPopdirIds<<oReg.popdirection_id
        }
        hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id AND id IN (:ids) ORDER BY rating DESC',[country_id:hsRes.country[0].id,ids:lsPopdirIds?:[-1]])
      }      
    }
      
    hsRes.modstatus=Homemodstatus.findAll('FROM Homemodstatus ORDER BY modstatus')
    
    if(requestService.getLongDef('ext',0))
	    hsRes.temp_notification=Temp_notification.findWhere(id:2,status:1)
      
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////////
  def homelist = {    
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=4
    hsRes.admin = session.admin
    
    def oHomeSearchAdmin=new HomeSearchAdmin()    
    if (session.lastRequest?.fromDetails?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['inputdate_year', 'inputdate_month', 'inputdate_day', 'country_id', 'region_id','popdir_id','ncomment','hotdiscount','longdiscount','is_reserve'],['id','user_id'],['client_name','linkname'],['inputdate'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-5)
      hsRes.inrequest.is_confirmed = requestService.getIntDef('is_confirmed',0)
      hsRes.inrequest.is_mainpage = requestService.getIntDef('is_mainpage',-1)
      hsRes.inrequest.is_specoffer = requestService.getIntDef('is_specoffer',-1)
      hsRes.inrequest.offset = requestService.getOffset()
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }
    hsRes.imageurl = ConfigurationHolder.config.urlphoto
    def inputdate=''
    def inputdateNext=''
    if (hsRes.inrequest.inputdate) {
      inputdate = hsRes.inrequest.inputdate_year.toString()+'-'+hsRes.inrequest.inputdate_month.toString()+'-'+hsRes.inrequest.inputdate_day.toString()
      def day = hsRes.inrequest.inputdate_day + 1
      inputdateNext = hsRes.inrequest.inputdate_year.toString()+'-'+hsRes.inrequest.inputdate_month.toString()+'-'+day.toString()
    }        

    hsRes+=oHomeSearchAdmin.csiSelectHomes(hsRes.inrequest.id?:0,hsRes.inrequest.country_id?:0,
                hsRes.inrequest.region_id?:0,(hsRes.inrequest.modstatus!=null)?hsRes.inrequest.modstatus:-5,
                hsRes.inrequest.is_confirmed?:0,(hsRes.inrequest.is_mainpage!=null)?hsRes.inrequest.is_mainpage:-1,
                (hsRes.inrequest.is_specoffer!=null)?hsRes.inrequest.is_specoffer:-1,inputdate?:'',inputdateNext?:'',
                hsRes.inrequest.user_id?:0,hsRes.inrequest.client_name?:'',hsRes.inrequest.is_reserve?:0,hsRes.inrequest.ncomment?:0,
                hsRes.inrequest.hotdiscount?:0,hsRes.inrequest.longdiscount?:0,hsRes.inrequest.linkname?:'',0,
                requestService.getLongDef('order',0),20,hsRes.inrequest.offset?:0,false,hsRes.inrequest?.popdir_id?:0,hsRes.admin.regions)
    hsRes.modstatus=Homemodstatus.list()
    return hsRes
  }

  def toMainpage={
    checkAccess(4)
    requestService.init(this)
    def iId=requestService.getLongDef('id',0)

    def iStatus=requestService.getLongDef('status',0)
    if(iId>0){
      def oHome=Home.get(iId)
      oHome.is_mainpage=iStatus
	
      if(!oHome.save(flush:true)) {
        log.debug(" Error on save User:")
        oUser.errors.each{log.debug(it)}	 
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def toSpecoffer={
    checkAccess(4)
    requestService.init(this)
    def iId=requestService.getLongDef('id',0)
    def iStatus=requestService.getLongDef('status',0)

    if(iId>0){
      def oHome=Home.get(iId)
      oHome.is_specoffer=iStatus
	
      if(!oHome.save(flush:true)) {
        log.debug(" Error on save User:")
        oUser.errors.each{log.debug(it)}	 
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def cancelConf={
    checkAccess(4)
    requestService.init(this)
    def lId=requestService.getLongDef('id',0)

    if(lId>0){
      def oHome = Home.get(lId)
      oHome.is_confirmed = 0

      if(!oHome?.save(flush:true)) {
        log.debug(" Error on save User:")
        oUser.errors.each{log.debug(it)}
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def loginAsHomeOwner={
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=3
    hsRes.admin = session.admin
    
    def lId=requestService.getLongDef('id',0)
    def oUser = User.findByClient_id(Home.get(lId).client_id)
    if (oUser) {
      if (oUser.provider == 'staytoday')
        usersService.loginInternalUser(oUser.name, '', requestService, 0, oUser.id)
      else 
        usersService.loginUser(oUser.openid, oUser.name, oUser.provider, requestService, oUser.picture, oUser.ref_id)

      def res = [:]
      res.id = lId
      render res as JSON
    }

    render(contentType:"application/json"){[error:true]}
  }

  def moderateHome = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=4
    hsRes.admin = session.admin    
    
    def lId=requestService.getLongDef('id',0)
    hsRes.home = Home.get(lId)
    hsRes.imageurl = ConfigurationHolder.config.urlphoto
    if(hsRes.home){
      def bSave=requestService.getLongDef('save',0)
      hsRes.ownerUser = User.findByClient_id(hsRes.home?.client_id?:0)
      hsRes.ownerClient = Client.findById(hsRes.home?.client_id?:0)
      if(!bSave)
        hsRes.inrequest=hsRes.home
      else {
        flash.save_error=[]
        hsRes+=requestService.getParams(['country_id','region_id','popdirection_id','is_mainpage','is_specoffer',
                                         'extrarating','ratingpenalty','is_pricebyday','is_fiesta','ownerClientRating',
                                         'is_renthour','pindex','hometype_id','is_vip'],
                                        ['x','y','owner_user'],
                                        ['comments','name','description','remarks','mapkeywords','city','address','shortaddress',
                                         'fulladdress','fullinfo','reachpersonal','reachpublic','homerule',
                                         'owner_user_tel','owner_user_tel1','owner_user_skype','linkname',
                                         'owner_user_description','district','street','homenumber','spcf'])
        def lsDirectory=requestService.getParams(['country_id','region_id']).inrequest
        if((lsDirectory?:[]).size()!=2)
          flash.save_error<<10
        if(!(hsRes.inrequest?.name?:''))
          flash.save_error<<1
        if(!(hsRes.inrequest?.linkname?:''))
          flash.save_error<<5
        if(!(hsRes.inrequest?.description?:''))
          flash.save_error<<2
        if (hsRes.inrequest?.owner_user_tel&&!hsRes.inrequest?.owner_user_tel?.matches("\\+([0-9]{1,5})\\(([0-9]{1,7})\\)([0-9]{3,10})"))
          flash.save_error<<4
        if (hsRes.inrequest?.owner_user_tel1&&!hsRes.inrequest?.owner_user_tel1?.matches("\\+([0-9]{1,5})\\(([0-9]{1,7})\\)([0-9]{3,10})"))
          flash.save_error<<4
        if (!(hsRes.inrequest?.pindex?:0) && requestService.getStr('pindex').size()>0)
          flash.save_error<<6        
        
        def lConf=requestService.getLongDef('confirm',0)
        def lDecl=requestService.getLongDef('decline',0)
        if(!(flash.save_error.size())){
          hsRes.home.name = hsRes.inrequest?.name
          hsRes.home.linkname = hsRes.home.csiGetUniqueLinkname(hsRes.inrequest.linkname,hsRes.home.id)
          hsRes.home.description = hsRes.inrequest?.description
          hsRes.home.remarks = hsRes.inrequest?.remarks?:''
          hsRes.home.mapkeywords = hsRes.inrequest?.mapkeywords?:''
          hsRes.home.address = hsRes.inrequest?.address?:''
          hsRes.home.shortaddress = hsRes.inrequest?.shortaddress?:''
          hsRes.home.country_id = hsRes.inrequest?.country_id
          hsRes.home.region_id = hsRes.inrequest?.region_id
		      hsRes.home.city = hsRes.inrequest?.city?:''
          hsRes.home.x = hsRes.inrequest?.x?:0
          hsRes.home.y = hsRes.inrequest?.y?:0
          hsRes.home.fulladdress = hsRes.inrequest?.fulladdress?:''                    

          hsRes.home.street=hsRes.inrequest?.street?:''
          hsRes.home.district=hsRes.inrequest?.district?:''
          hsRes.home.pindex=hsRes.inrequest?.pindex?hsRes.inrequest.pindex.toString():''
          hsRes.home.homenumber=hsRes.inrequest?.homenumber?:''
          hsRes.home.spcf=hsRes.inrequest?.spcf?:''          
          
          if (hsRes.home.city){          
            if(hsRes.home.city.matches('.*(?=.*[A-Za-z]).*')){           
              hsRes.home.city_id = City.findByName_en(hsRes.home.city)?.id?:0           
            }  
            else  
              hsRes.home.city_id = City.findByName(hsRes.home.city)?.id?:0
          }  
          else 
            hsRes.home.city_id = 0 
            
          if (hsRes.home.district){
            if(hsRes.home.district.matches('.*(?=.*[A-Za-z]).*'))
              hsRes.home.district_id = District.findByRegion_idAndName_en(hsRes.home.region_id, hsRes.home.district)?.id?:0
            else  
              hsRes.home.district_id = District.findByRegion_idAndName(hsRes.home.region_id, hsRes.home.district)?.id?:0
          }else 
             hsRes.home.district_id = 0  
          
          if (hsRes.home.street) {
            if(hsRes.home.street.matches('.*(?=.*[A-Za-z]).*')) 
              hsRes.home.street_id = Street.findByRegion_idAndName_en(hsRes.home.region_id, hsRes.home.street)?.id?:0
            else
              hsRes.home.street_id = Street.findByRegion_idAndName(hsRes.home.region_id, hsRes.home.street)?.id?:0
          } else 
            hsRes.home.street_id = 0          
          
          hsRes.home.fullinfo = hsRes.inrequest?.fullinfo?:''
          hsRes.home.reachpersonal = hsRes.inrequest?.reachpersonal?:''
          hsRes.home.reachpublic = hsRes.inrequest?.reachpublic?:''
          hsRes.home.homerule = hsRes.inrequest?.homerule?:''
          hsRes.home.popdirection_id = hsRes.inrequest?.popdirection_id?:0
          hsRes.home.is_mainpage = hsRes.inrequest?.is_mainpage?:0
          hsRes.home.is_specoffer = hsRes.inrequest?.is_specoffer?:0
          hsRes.home.is_pricebyday = hsRes.inrequest?.is_pricebyday?:0
          hsRes.home.extrarating = hsRes.inrequest?.extrarating?.abs()?:0
          hsRes.home.ratingpenalty = hsRes.inrequest?.ratingpenalty?.abs()?:0
          hsRes.home.is_fiesta = hsRes.inrequest?.is_fiesta?:0
          hsRes.home.is_renthour = hsRes.inrequest?.is_renthour?:0        
          hsRes.home.hometype_id = hsRes.inrequest?.hometype_id?:0 
          hsRes.home.is_vip = hsRes.inrequest?.is_vip?:0          
          
          if (hsRes.ownerUser && (hsRes.inrequest?.comments?:'') 
            && hsRes.home.comments != (hsRes.inrequest?.comments?:'') && !lDecl){
            hsRes.home.comments = hsRes.inrequest?.comments?:''//here for use in first param of next function
            mailerService.sendChangeCommentsMail(hsRes.home,hsRes.ownerUser,hsRes.context)
          }
          hsRes.home.comments = hsRes.inrequest?.comments?:''
          if( lConf ) {
            if(hsRes.home.modstatus == 4)
              mailerService.sendAcceptHomeMail(hsRes.home,hsRes.ownerUser,hsRes.context)
            hsRes.home.is_confirmed = 1
            hsRes.home.modstatus = 1
            hsRes.home.is_index = 1
          } else if( lDecl ) {
            hsRes.home.is_confirmed = 1
            hsRes.home.modstatus = -1
            mailerService.sendDeclineHomeMail(hsRes.home,hsRes.ownerUser,hsRes.context)
          }
          if(!hsRes.home.save(flush:true)) {
            log.debug(" Error on save home:")
            hsRes.home.errors.each{log.debug(it)}
            flash.save_error<<101
          }
          if(hsRes.inrequest?.owner_user){
            if (!hsRes.home.addOwner(hsRes.inrequest?.owner_user)){
              flash.save_error<<3
            }
          }
          if(hsRes.ownerUser){
            hsRes.ownerUser.tel = hsRes.inrequest?.owner_user_tel?:''
            hsRes.ownerUser.tel1 = hsRes.inrequest?.owner_user_tel1?:''
            hsRes.ownerUser.skype = hsRes.inrequest?.owner_user_skype?:''
            hsRes.ownerUser.description = hsRes.inrequest?.owner_user_description?:''
            if(!hsRes.ownerUser.save(flush:true)){
              log.debug(" Error on save User(administrator/moderateHome/"+lId+"):")
              hsRes.ownerUser.errors.each{log.debug(it)}
              flash.save_error<<101
            }
          }
          if(hsRes.ownerClient){
            hsRes.ownerClient.addrating = hsRes.inrequest?.ownerClientRating?:0
            if(!hsRes.ownerClient.save(flush:true)){
              log.debug(" Error on save Client(administrator/moderateHome/"+lId+"):")
              hsRes.ownerClient.errors.each{log.debug(it)}
              flash.save_error<<101
            }
          }
          
          def lsHomemetro=[]	
          def lsMetro=[]
          if(session.admin_metrolist){		  
            for(metrolist in session.admin_metrolist)
              if(metrolist?.home_id==hsRes.home.id)		
                lsMetro=metrolist?.metros
            session.admin_metrolist=null                                  
      
            for(oHomemetro in Homemetro.findAllWhere(home_id:hsRes.home.id)){
              if(oHomemetro.metro_id.toLong() in lsMetro)
                lsHomemetro<<oHomemetro.metro_id.toLong()					  
              else{		 
                oHomemetro.delete(flush:true)		   
              }
            }
        
            for(metro_id in lsMetro){
              if(metro_id in lsHomemetro){            	  
              } else if(metro_id!=''){		  
                def oHomemetro=new Homemetro()
                oHomemetro.home_id=hsRes.home.id			
                oHomemetro.metro_id=metro_id.toInteger()
                if(!oHomemetro.save(flush:true)) {
                  log.debug(" Error on save homemetro:")
                  oHomemetro.errors.each{log.debug(it)}        		
                }		        
              }		  		
            }
          }          
        }
      }
	    hsRes.hometype=Hometype.list()
	    hsRes.homeclass=Homeclass.list()
	    hsRes.homeperson=Homeperson.list()
      hsRes.homeroom=Homeroom.list()
      hsRes.homebath=Homebath.list()
      
      hsRes.homeoption=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=1 ORDER BY id')
		
      hsRes.country=Country.findAll('FROM Country ORDER BY regorder')
      if(hsRes.inrequest?.country_id?:0){
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
      } else if((hsRes.country?:[]).size()>0)
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])
      hsRes.homepropinactive=Homeprop.findAll('FROM Homeprop WHERE modstatus<4 AND home_id=:home_id AND term=0 ORDER BY date_start',[home_id:lId])
      hsRes.homeprop=Homeprop.findAll('FROM Homeprop WHERE modstatus<3 AND home_id=:home_id AND term=1 ORDER BY date_start',[home_id:lId])
      hsRes.homephoto=Homephoto.findAll('FROM Homephoto WHERE home_id=:home_id ORDER BY norder',[home_id:lId])
      
      if(bSave){
        for(oHphoto in hsRes.homephoto){
          if(oHphoto){
            oHphoto.ptext=requestService.getStr('ptext_'+oHphoto.id)
            if(!oHphoto.save(flush:true)) {
              log.debug(" Error on save oHphoto:")
              oHphoto.errors.each{log.debug(it)}
            }
          }
        }
      }
      
      hsRes.homemodstatus=Homemodstatus.findByModstatus(hsRes.home.modstatus)
      hsRes.popdirections=Popdirection.findAll('FROM Popdirection WHERE modstatus=1 ORDER BY rating desc')
      hsRes.home_valuta=Valuta.get(hsRes.home?.valuta_id?:0)
      hsRes.discounts = hsRes.home.csiGetHomeDiscounts()
      
      hsRes.curregion=Region.get(hsRes.home.region_id?:0.toLong)
	    
      if(hsRes.curregion.is_metro){	    
        hsRes.metro=Metro.findAll('FROM Metro WHERE region_id=:region_id AND modstatus=1 ORDER BY name',[region_id:hsRes.curregion.id])
        hsRes.metros=''		
        hsRes.metro_ids=[]
        for(oHomemetro in Homemetro.findAllWhere(home_id:lId)){
          hsRes.metro_ids<<oHomemetro?.metro_id
          def oMetro=Metro.get(oHomemetro?.metro_id)		
          if(oMetro)        
            hsRes.metros+=oMetro.name+'\n'		  		  
        }
      }      
      
      //>>stats
      def hsParams = [:]
      hsParams.int = [:]
      hsParams.long = [:]
      hsParams.int.time = 1
      hsParams.int.output = 0
      hsParams.long.id = hsRes.home.id

      def oStats=new StatSearch()
      hsRes.statsclick = oStats.getStatsByHomeDetail(hsParams,0,true)[0]
      hsParams.int.output = 3
      hsRes.statslisting = oStats.getStatsByHomeDetail(hsParams,0,true)[0]
      //<<stats
    } else {
      redirect(action:'index')
      return
    }
    return hsRes
  }
  
  def addmetro={
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
	  
    def lId=requestService.getLongDef('home_id',0)
    
    def metro_ids=requestService.getList('metros')	 	      	 
    if(!session.admin_metrolist)
      session.admin_metrolist=[]
    else {
      def i=0
      for(metrolist in session.admin_metrolist){
        if(metrolist?.home_id==lId)
          session.admin_metrolist[i]=null
        i++
      }
    }
    session.admin_metrolist<<[home_id:lId,metros:metro_ids]	  	  
  
    hsRes.metros=''	
    def oMetro=[:]
    for(metro in metro_ids){
      if(metro!=''){          
        oMetro=Metro.get(metro)
        if(oMetro)
          hsRes.metros+=oMetro.name+'\n'	    
      }	  
    }
    
    return hsRes
  }
  
  def get_metro={
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  
    
    def lId=requestService.getLongDef('home_id',0)
    def iRegId=requestService.getIntDef('region_id',0)	
    hsRes.metro_ids=[]	
    if(Region.get(iRegId)?.is_metro){	
      hsRes.metro=Metro.findAll('FROM Metro WHERE region_id=:region_id AND modstatus=1 ORDER BY name',[region_id:iRegId])		  
      
      for(oHomemetro in Homemetro.findAllWhere(home_id:lId,modstatus:1))
        hsRes.metro_ids<<oHomemetro?.metro_id		       
    }
    return hsRes
  }
  
  
  def bigimage={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  
    hsRes.imageurl = ConfigurationHolder.config.urlphoto
    hsRes.image = requestService.getStr('picture')
    hsRes.home = Home.get((Homephoto.findByPicture(hsRes.image)?.home_id))
    return hsRes
  }
  
  def homephotodelete={
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  
    hsRes.imageurl = ConfigurationHolder.config.urlphoto
    def lId = requestService.getLongDef('id',0)
    def lHomeId = requestService.getLongDef('home_id',0)
    if(lId>0){ 	  
      def oHomephoto = Homephoto.get(lId)
      hsRes.home = Home.get(oHomephoto?.home_id)
      if(oHomephoto&&hsRes.home){
        def tmpNorder = oHomephoto.norder
        def tmpHomephoto
        imageService.init(this,'','',ConfigurationHolder.config.pathtophoto+hsRes.home.client_id.toString()+File.separatorChar)    
        def lsPictures = []      
        lsPictures<<oHomephoto.picture              	  
        oHomephoto.delete(flush:true)
        imageService.deletePictureFilesFromHd(lsPictures)
        while (tmpNorder <= Homephoto.findAllByHome_id(lHomeId).size()){
          tmpHomephoto = Homephoto.findByHome_idAndNorder(lHomeId, ++tmpNorder)
          tmpHomephoto.norder = tmpNorder-1
          tmpHomephoto.save(flush:true)
        }
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def testLinkname={
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes+=requestService.getParams(null,['id'])
    hsRes.inrequest.linkname = requestService.getStr('linkname')

    if(hsRes.inrequest.linkname){
      def oHome = new Home()
      hsRes.inrequest.linkname = oHome.csiGetUniqueLinkname(hsRes.inrequest.linkname,hsRes.inrequest.id)
    }
    render hsRes.inrequest as JSON
  }

  def recountRating={
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes+=requestService.getParams(null,['id'])
    def oHome = Home.get(hsRes.inrequest.id)
    if(oHome){
      render oHome.calculateHomeRating() as JSON
    } else
      response.sendError(404)
  }

  def updateDeleteStatus={
    checkAccess(4)
    requestService.init(this)
    def iId=requestService.getLongDef('id',0)
    def iStatus=requestService.getLongDef('status',0)

    if(iId>0){
      def oHome=Home.get(iId)
      oHome.modstatus=iStatus
  
      if(!oHome.save(flush:true)) {
        log.debug(" Error on save Home:")
        oUser.errors.each{log.debug(it)}   
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def selectpopdirection={
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def country_id=requestService.getIntDef('country_id',1)
    hsRes.data = Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id AND modstatus=1 ORDER BY rating desc, name2 asc',[country_id:country_id?:1])
    hsRes.popdirection=Popdirection.findAll('FROM Popdirection WHERE modstatus=1 ORDER BY rating desc',[max:Tools.getIntVal(ConfigurationHolder.config.popdirection.quantity,10)])
    hsRes.countryIds = hsRes.popdirection.collect{it.country_id}
    hsRes.countryIds.unique()
    hsRes.countries = Country.list()
    return hsRes
  }

//owner Dmitry<<
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Homes <<<///////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Infotext >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  def infotext = {    
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.itemplate=Itemplate.findAll('FROM Itemplate ORDER BY name')  	
    hsRes.action_id=5
    hsRes.admin = session.admin	

    def fromEdit = requestService.getIntDef('fromEdit',0)
    hsRes.type = requestService.getIntDef('type',0)
    if (fromEdit){
      session.lastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.lastRequest
    }
    return hsRes
  }

  def infotextlist = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=5
    hsRes.admin = session.admin
    def oInfotext=new Infotext()

    if (session.lastRequest?.fromEdit?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromEdit = 0
    } else {
      hsRes+=requestService.getParams(['id'],[],['inf_action','inf_controller'])
      hsRes.inrequest.itemplate_id = requestService.getIntDef('itemplate_id',-1)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    if (!hsRes.inrequest.id){
      hsRes+=oInfotext.csiSelectInfotext(hsRes.inrequest.inf_action?:'',hsRes.inrequest.inf_controller?:'',(hsRes.inrequest.itemplate_id!=null)?hsRes.inrequest.itemplate_id:-1,requestService.getLongDef('order',0),20,requestService.getOffset())
      hsRes.itemplate=Itemplate.findAll('FROM Itemplate ORDER BY name')
    } else {
      hsRes+=oInfotext.csiSelectMailtemplate(hsRes.inrequest.inf_action?:'',20,requestService.getOffset())
    }
    return hsRes
  }

  def infotextedit = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=5
    hsRes.admin = session.admin
    def lId=requestService.getLongDef('id',0)
    def lType=requestService.getLongDef('type',0)
    if (!lType)
      hsRes.infotext = Infotext.get(lId)
    else
      hsRes.emailTemplate = Email_template.get(lId)
    if(hsRes.infotext){
      def bSave=requestService.getLongDef('save',0)
      if(!bSave)
        hsRes.inrequest=hsRes.infotext
      else {
        flash.save_error=[]
        hsRes+=requestService.getParams(['is_anons'],[],['title','keywords','description','promotext1','promotext2','itext','itext2','itext3','header',
                                                         'title_en','description_en','promotext1_en','promotext2_en','itext_en','itext2_en','itext3_en','header_en',
                                                         'relatedpages','name_en','name'])
        hsRes.infotext.title = hsRes.inrequest.title?:''
        hsRes.infotext.keywords = hsRes.inrequest.keywords?:''
        hsRes.infotext.description = hsRes.inrequest.description?:''
        hsRes.infotext.header = hsRes.inrequest.header?:''
        hsRes.infotext.promotext1 = hsRes.inrequest.promotext1?:''
        hsRes.infotext.promotext2 = hsRes.inrequest.promotext2?:''
        hsRes.infotext.itext = hsRes.inrequest.itext?:''
        hsRes.infotext.itext2 = hsRes.inrequest.itext2?:''
        hsRes.infotext.itext3 = hsRes.inrequest.itext3?:''
        hsRes.infotext.is_anons = hsRes.inrequest.is_anons?:0
        hsRes.infotext.moddate = new Date()
        hsRes.infotext.title_en = hsRes.inrequest.title_en?:''
        hsRes.infotext.description_en = hsRes.inrequest.description_en?:''        
        hsRes.infotext.header_en = hsRes.inrequest.header_en?:''
        hsRes.infotext.promotext1_en = hsRes.inrequest.promotext1_en?:''
        hsRes.infotext.promotext2_en = hsRes.inrequest.promotext2_en?:''
        hsRes.infotext.itext_en = hsRes.inrequest.itext_en?:''
        hsRes.infotext.itext2_en = hsRes.inrequest.itext2_en?:''
        hsRes.infotext.itext3_en = hsRes.inrequest.itext3_en?:''
        hsRes.infotext.relatedpages = hsRes.inrequest.relatedpages?:''
        hsRes.infotext.name = hsRes.inrequest.name?:''
        hsRes.infotext.name_en = hsRes.inrequest.name_en?:''        
        if(!hsRes.infotext.save(flush:true)) {
          log.debug(" Error on save infotext:")
          hsRes.infotext.errors.each{log.debug(it)}
          flash.save_error<<101
        }
        hsRes.inrequest=hsRes.infotext
      }
    } else if (hsRes.emailTemplate){
      def bSave=requestService.getLongDef('save',0)
      if(!bSave)
        hsRes.inrequest=hsRes.emailTemplate.properties
      else {
        flash.save_error=[]
        hsRes+=requestService.getParams([],[],['title','itext','name'])
        hsRes.emailTemplate.title = hsRes.inrequest.title?:''
        hsRes.emailTemplate.itext = hsRes.inrequest.itext?:''
        hsRes.emailTemplate.name = hsRes.inrequest.name?:''       
        if(!hsRes.emailTemplate.save(flush:true)) {
          log.debug(" Error on save emailTemplate:")
          hsRes.emailTemplate.errors.each{log.debug(it)}
          flash.save_error<<101
        }
        hsRes.inrequest=hsRes.emailTemplate.properties
      }
    } else {
      redirect(action:'index')
      return
    }
    hsRes.type=lType
    return hsRes
  }
  //owner Dmitry<<
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////// Infotext Add //////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //owner Marina<<
  def infotextadd = {    
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false) 
    hsRes.action_id=5
    hsRes.admin = session.admin  
    hsRes+=requestService.getParams(['itemplate_id','npage','type'],[],['tcontroller','taction','name'])
    hsRes.itemplate=Itemplate.findAll('FROM Itemplate ORDER BY name')   

    return hsRes
  }
  
  def saveinfotext={    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams(['itemplate_id','npage','type'],[],['tcontroller','taction','name'])
    
    if((!hsRes.inrequest?.tcontroller && !hsRes.inrequest?.type)|| !hsRes.inrequest?.taction || !hsRes.inrequest?.name){
      redirect(action:'infotextadd')
      return
    }
    def iId
    if(!hsRes.inrequest?.type) {
      def oInfotext = new Infotext()
      oInfotext.itemplate_id = hsRes.inrequest?.itemplate_id?:0
      oInfotext.controller = hsRes.inrequest?.tcontroller
      oInfotext.action = hsRes.inrequest?.taction    
      oInfotext.npage = hsRes.inrequest?.npage?:0
      oInfotext.icon = ''
      oInfotext.shortname = ''
      oInfotext.name = hsRes.inrequest?.name
      oInfotext.header = ''
      oInfotext.title = hsRes.inrequest?.name
      oInfotext.keywords = ''
      oInfotext.description = ''
      oInfotext.itext = ''
      oInfotext.itext2 = ''
      oInfotext.itext3 = ''
      oInfotext.promotext1 = ''
      oInfotext.promotext2 = ''
      oInfotext.moddate = new Date()
    
      if(!oInfotext.save(flush:true)) {
        log.debug(" Error on save Infotext:")
        oInfotext.errors.each{log.debug(it)}
      }
      iId = oInfotext.id
    } else {
      def oEmailTemplate = new Email_template()
      oEmailTemplate.action = hsRes.inrequest?.taction
      oEmailTemplate.name = hsRes.inrequest?.name
      oEmailTemplate.title = hsRes.inrequest?.name
      oEmailTemplate.itext = ''
    
      if(!oEmailTemplate.save(flush:true)) {
        log.debug(" Error on save Email_template:")
        oEmailTemplate.errors.each{log.debug(it)}
      }
      iId = oEmailTemplate.id
    }
	
    redirect(action:'infotextedit',id:iId, params: [type:hsRes.inrequest?.type?:0])
    return
  }  
  //owner Marina<<
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Infotext <<<////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Reviews<<</////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  def reviews={ 
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=6
    hsRes.admin = session.admin	

    return hsRes
  }

  def reviewslist = {    
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=6
    hsRes.admin = session.admin
	
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    def oUcommentSearch = new UcommentSearch()
    hsRes+=requestService.getParams(['comdate_year', 'comdate_month', 'comdate_day'],['home_id','user_id'],[],['comdate'])
    hsRes.inrequest.comstatus = requestService.getIntDef('comstatus',-2)
    hsRes.inrequest.rating = requestService.getIntDef('rating',-1)
	hsRes.inrequest.typeid = requestService.getIntDef('typeid',0)
    def inputdate=''
    def inputdateNext=''
    if (hsRes.inrequest.comdate) {
      inputdate = hsRes.inrequest.comdate_year.toString()+'-'+hsRes.inrequest.comdate_month.toString()+'-'+hsRes.inrequest.comdate_day.toString()
      def day = hsRes.inrequest.comdate_day + 1
      inputdateNext = hsRes.inrequest.comdate_year.toString()+'-'+hsRes.inrequest.comdate_month.toString()+'-'+day.toString()
    }
    hsRes+=oUcommentSearch.csiSelectUcomments(hsRes.inrequest.home_id?:0,hsRes.inrequest.user_id?:0,(hsRes.inrequest.comstatus!=null)?hsRes.inrequest.comstatus:-2,(hsRes.inrequest.rating!=null)?hsRes.inrequest.rating:-1,hsRes.inrequest.typeid?:0,inputdate?:'',inputdateNext?:'',20,requestService.getOffset())
	
    return hsRes
  }

  def reviewsToMainpage={
    requestService.init(this)
    def iId=requestService.getLongDef('id',0)
    def iStatus=requestService.getLongDef('status',0)

    if(iId>0){
      def oUcomment=Ucomment.get(iId)
      oUcomment.is_mainpage=iStatus
	
      if(!oUcomment.save(flush:true)) {
        log.debug(" Error on save Ucomment:")
        oUcomment.errors.each{log.debug(it)}	 
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def reviewsConfirm={
    requestService.init(this)
    def iId=requestService.getLongDef('id',0)
    def iStatus=requestService.getLongDef('status',0)

    if(iId>0){
      def oUcomment=Ucomment.get(iId)
      if (iStatus==1) {
        if (oUcomment.comstatus==-1 && oUcomment.typeid==1) {
          def oHome = Home.get(oUcomment.home_id)
          def oUser = User.get(oUcomment.user_id)
          oHome.nref++
          oUser.ncomment++
          if(!oHome.save(flush:true)) {
            log.debug(" Error on save Home:")
            oHome.errors.each{log.debug(it)}
          }
          if(!oUser.save(flush:true)) {
            log.debug(" Error on save User:")
            oUser.errors.each{log.debug(it)}
          }
        }
      } else if (iStatus==-1) {
        if (oUcomment.comstatus!=-1 && oUcomment.typeid==1) {
          def oHome = Home.get(oUcomment.home_id)
          def oUser = User.get(oUcomment.user_id)
          oHome.nref--
          oUser.ncomment--
          if(!oHome.save(flush:true)) {
            log.debug(" Error on save Home:")
            oHome.errors.each{log.debug(it)}
          }
          if(!oUser.save(flush:true)) {
            log.debug(" Error on save User:")
            oUser.errors.each{log.debug(it)}
          }
        }
      }
      oUcomment.comstatus=iStatus
	
      if(!oUcomment.save(flush:true)) {
        log.debug(" Error on save Ucomment:")
        oUcomment.errors.each{log.debug(it)}	 
      }
    }

    render(contentType:"application/json"){[error:false]}
  }
//<<owner Dmitry
  def reviewsData={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true) 
    hsRes.inrequest=[:]    
    hsRes.inrequest.ids=[]
    def ids=requestService.getList('ids')
    for(id in ids)
      hsRes.inrequest.ids<<id.toLong()
    
    def comments=Ucomment.findAll('FROM Ucomment WHERE (comstatus=0) AND (id IN (:ids)) ORDER BY home_id',[ids:hsRes.inrequest.ids])   
    for(comm in comments){

      comm.comstatus=1

      if(!comm.save(flush:true)) {
        log.debug(" Error on save Ucomment:")
        comm.errors.each{log.debug(it)}
      }
    }

    render(contentType:"application/json"){[error:false]}
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Reviews<<</////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Adress>>>//////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  def adress = {
    checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=8
    hsRes.admin = session.admin

    def fromEdit = requestService.getIntDef('fromEdit',0)
    hsRes.type = requestService.getIntDef('type',0)
    if (fromEdit){
      session.lastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.lastRequest
    }
    hsRes.country=Country.findAll('FROM Country ORDER BY regorder')
    if(hsRes.inrequest?.country_id?:0){
      hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
      hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id ORDER BY rating DESC',[country_id:hsRes.inrequest.country_id])
    }else if((hsRes.country?:[]).size()>0){
  	  hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])
      hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id ORDER BY rating DESC',[country_id:hsRes.country[0].id])
    }      
      def lsRegions=[]    
      if(hsRes.inrequest?.popdir_id?:0)
        lsRegions=Region.findAllWhere(popdirection_id:hsRes.inrequest.popdir_id)
      else if((hsRes.popdirs?:[]).size()>0)
        lsRegions=Region.findAllWhere(popdirection_id:hsRes.popdirs[0].id)
        
      if(lsRegions){  
        def lsRegIds=[]
        for(reg in lsRegions){
          lsRegIds<<reg.id
        }
        
        hsRes.cities=City.findAll('FROM City WHERE region_id IN (:reg_ids) ORDER BY name',[reg_ids:lsRegIds])                  
      }
    return hsRes
  }

  def adresslist = {
    checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=8
    hsRes.admin = session.admin
    def oSearch = new AdressSearch()

    if (session.lastRequest?.fromEdit?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromEdit = 0
    } else {
      hsRes+=requestService.getParams(['country_id','region_id','type','offset','city_id','popdir_id'],[],['district_name','name'])
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }
    
    params.offset=hsRes.inrequest?.offset?:0//for paginate

    if(!hsRes.inrequest?.type)
      hsRes+=oSearch.getDistrictList(hsRes.inrequest?.country_id?:0,hsRes.inrequest?.region_id?:0,hsRes.inrequest?.name?:'',20,hsRes.inrequest?.offset?:0)
    else if (hsRes.inrequest?.type==1)
      hsRes+=oSearch.getCityList(hsRes.inrequest?.country_id?:0,hsRes.inrequest?.region_id?:0,hsRes.inrequest?.name?:'',hsRes.inrequest?.district_name?:'',20,hsRes.inrequest?.offset?:0)
    else if (hsRes.inrequest?.type==2)
      hsRes+=oSearch.getStreetList(hsRes.inrequest?.country_id?:0,hsRes.inrequest?.region_id?:0,hsRes.inrequest?.name?:'',20,hsRes.inrequest?.offset?:0)
    else if (hsRes.inrequest?.type==3)
      hsRes+=oSearch.getCountryList(hsRes.inrequest?.name?:'',20,hsRes.inrequest?.offset?:0)
    else if (hsRes.inrequest?.type==4)
      hsRes+=oSearch.getRegionList(hsRes.inrequest?.country_id?:0,hsRes.inrequest?.name?:'',20,hsRes.inrequest?.offset?:0)
    else if (hsRes.inrequest?.type==5){
      def oCitysight=new Citysight()  
      hsRes+=oCitysight.findCitysight(hsRes.inrequest?.country_id?:0,hsRes.inrequest?.popdir_id?:0,hsRes.inrequest?.city_id?:0,hsRes.inrequest?.name?:'',20,hsRes.inrequest?.offset?:0)     
    }
	//log.debug(hsRes)
    return hsRes
  }

  def adressadd = {
    checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=8
    hsRes.admin = session.admin
    flash.error=[]
	
    hsRes.region=Region.findAll('FROM Region ORDER BY regorder DESC, name')
    hsRes.country=Country.list()
    hsRes.valuta = Valuta.findAllByModstatus(1)        

    hsRes+=requestService.getParams(['id','region_id','save','country_id','cry_regorder','cry_valuta_id','cry_reserve',
                                     'is_index','city_id','is_metro','x','y','radius','is_index','is_specoffer','modstatus',
                                     'sightstreet_id','sightdistrict_id','citysigttype'],[],
                                     ['name','district','name2','header','title','keywords','description','itext',
                                     'urlname','tagname','seo_title','seo_keywords','seo_description',
                                     'flats_description','rooms_description','cottages_description','houses_description','beds_description',
                                     'name_en','itext_en','title_en','description_en','name2_en'])
    hsRes.inrequest.type = requestService.getIntDef('type',0)
    
    def lDistrictId
   //edit start>>
    if(!hsRes.inrequest.save && hsRes.inrequest.type==4 && hsRes.inrequest.region_id){
      def oRegion=Region.get(hsRes.inrequest.region_id)
      hsRes.inrequest.name=oRegion?.name?:''
      hsRes.inrequest.name_en=oRegion?.name_en?:''      
      hsRes.inrequest.country_id=oRegion?.country_id?:0
      hsRes.inrequest.popdir_id=oRegion?.popdirection_id?:0
      def iCountryId=requestService.getIntDef('country_id',0)
      if(hsRes.inrequest.country_id)
        hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id ORDER BY rating DESC',[country_id:hsRes.inrequest.country_id])
    }
    if(!hsRes.inrequest.save && hsRes.inrequest.type==5 && hsRes.inrequest.id){
      def oCitysight=Citysight.get(hsRes.inrequest?.id)
      hsRes.inrequest.name=oCitysight?.name?:''
      hsRes.inrequest.name_en=oCitysight?.name_en?:''
      hsRes.inrequest.name2=oCitysight?.name2?:''
      hsRes.inrequest.name2_en=oCitysight?.name2_en?:''
      hsRes.inrequest.x=oCitysight.x
      hsRes.inrequest.y=oCitysight.y
      hsRes.inrequest.urlname=oCitysight.urlname
      hsRes.inrequest.keywords=oCitysight.keywords
      hsRes.inrequest.title=oCitysight.title
      hsRes.inrequest.title_en=oCitysight.title_en
      hsRes.inrequest.header=oCitysight.header
      hsRes.inrequest.description=oCitysight.description
      hsRes.inrequest.description_en=oCitysight.description_en
      hsRes.inrequest.itext=oCitysight.itext
      hsRes.inrequest.city_id=oCitysight.city_id
      hsRes.inrequest.radius=oCitysight.radius
      hsRes.inrequest.id=oCitysight.id
      hsRes.inrequest.country_id=City.get(oCitysight.city_id)?.country_id?:0
      hsRes.inrequest.popdir_id=Region.get(City.get(oCitysight.city_id)?.region_id?:0)?.popdirection_id?:0
      hsRes.inrequest.is_index=oCitysight.is_index
      hsRes.inrequest.modstatus=oCitysight.modstatus
      hsRes.inrequest.citysigttype = oCitysight.type
      hsRes.inrequest.sightstreet_id = oCitysight.street_id
      hsRes.inrequest.sightdistrict_id = oCitysight.district_id
    }
    if(hsRes.inrequest.type==5 && hsRes.inrequest.id){
      if(hsRes.inrequest.country_id)
        hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id ORDER BY rating DESC',[country_id:hsRes.inrequest.country_id])
      else if((hsRes.country?:[]).size()>0)
        hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id ORDER BY rating DESC',[country_id:hsRes.country[0].id])

      def lsRegions=[]    
      if(hsRes.inrequest.popdir_id)
        lsRegions=Region.findAllWhere(popdirection_id:hsRes.inrequest.popdir_id)
      else if((hsRes.popdirs?:[]).size()>0)  
        lsRegions=Region.findAllWhere(popdirection_id:hsRes.popdirs[0].id)
      def lsRegIds=[]
      for(reg in lsRegions){
        lsRegIds<<reg.id
      }
      hsRes.cities=City.findAll('FROM City WHERE region_id IN (:reg_ids) ORDER BY name',[reg_ids:lsRegIds])      
    }
    
    if(!hsRes.inrequest.save && hsRes.inrequest.type==3 && hsRes.inrequest.country_id){
      hsRes.hinrequest=Country.get(hsRes.inrequest?.country_id)
    }
    
    if(!hsRes.inrequest.save && hsRes.inrequest.type==1 && hsRes.inrequest.city_id){
      hsRes.hinrequest=City.get(hsRes.inrequest?.city_id)  
      hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.hinrequest?.country_id?:0])          
    }
    //<<edit
    if (hsRes.inrequest.save==1){
      if (!(hsRes.inrequest?.name?:''))
        flash.error<<2
      if (hsRes.inrequest?.type<3 && !(hsRes.inrequest?.region_id?:0))
        flash.error<<1
      if ((hsRes.inrequest?.type==4||hsRes.inrequest?.type==1) && !(hsRes.inrequest?.country_id?:0))
        flash.error<<5
      if (hsRes.inrequest?.type==2){
        if (!(hsRes.inrequest?.district?:''))
          flash.error<<3
        else {
          lDistrictId = District.findByName(hsRes.inrequest?.district?:'')?.id
          if (!lDistrictId)
            flash.error<<4
        }
      }
      if (hsRes.inrequest?.type==3 && !(hsRes.inrequest?.urlname?:''))
        flash.error<<6
      if(hsRes.inrequest?.type==5){
        if((hsRes.inrequest?.city_id?:0)==0)
          flash.error<<7
        else if(Citysight.findWhere(urlname:hsRes.inrequest?.urlname,city_id:hsRes.inrequest?.city_id)&&!hsRes.inrequest?.id){
          flash.error<<8
        }
        if (hsRes.inrequest?.citysigttype==2&&!hsRes.inrequest?.sightstreet_id) {
          flash.error<<9
        } else if (hsRes.inrequest?.citysigttype==3&&!hsRes.inrequest?.sightdistrict_id) {
          flash.error<<10
        }
      }

      if(!(flash.error.size())){
        if (!hsRes.inrequest.type) {
          def oDistrict = new District()
          oDistrict.name = hsRes.inrequest.name
          oDistrict.name_en = hsRes.inrequest.name_en?:''
          oDistrict.region_id = hsRes.inrequest.region_id
          if(!oDistrict.save(flush:true)) {
            log.debug(" Error on save District:")
            oDistrict.errors.each{log.debug(it)}	 
          }
        } else if (hsRes.inrequest.type==1) {
          def oCity = new City()
          if(hsRes.inrequest?.city_id)
            oCity=City.get(hsRes.inrequest?.city_id)
          
          oCity.name = hsRes.inrequest.name
          oCity.name_en = hsRes.inrequest.name_en?:''
          oCity.region_id = hsRes.inrequest.region_id
          oCity.country_id = hsRes.inrequest.country_id
          oCity.tel_code = ''
          oCity.name2 = hsRes.inrequest.name2?:''
          oCity.tagname = hsRes.inrequest.tagname?Tools.transliterate(hsRes.inrequest.tagname):''
          oCity.is_index = hsRes.inrequest.is_index?:0
          oCity.is_metro = hsRes.inrequest.is_metro?:0
          oCity.x=hsRes.inrequest.x?:0
          oCity.y=hsRes.inrequest.y?:0
          oCity.description = hsRes.inrequest.description?:''
          oCity.flats_description = hsRes.inrequest.flats_description?:''
          oCity.rooms_description = hsRes.inrequest.rooms_description?:''
          oCity.cottages_description = hsRes.inrequest.cottages_description?:''
          oCity.houses_description = hsRes.inrequest.houses_description?:''
          oCity.beds_description = hsRes.inrequest.beds_description?:''          
          oCity.seo_title=hsRes.inrequest.seo_title?:''
          oCity.seo_keywords=hsRes.inrequest.seo_keywords?:''
          oCity.seo_description=hsRes.inrequest.seo_description?:''
          oCity.is_specoffer = hsRes.inrequest?.is_specoffer?:0

          //oCity.district_id = lDistrictId
          if(!oCity.save(flush:true)) {
            log.debug(" Error on save City:")
            oCity.errors.each{log.debug(it)}
          }
        } else if (hsRes.inrequest.type==2) {
          def oStreet = new Street()
          oStreet.name = hsRes.inrequest.name
          oStreet.name_en = hsRes.inrequest.name_en?:''
          oStreet.region_id = hsRes.inrequest.region_id
          oStreet.city_id = 0
          oStreet.street = ''
          oStreet.strtype_id = 0
          oStreet.modstatus = 1
          oStreet.district_id = lDistrictId
          if(!oStreet.save(flush:true)) {
            log.debug(" Error on save Street:")
            oStreet.errors.each{log.debug(it)}
          }
        } else if (hsRes.inrequest.type==3) {
          def oCountry = new Country()
          if(hsRes.inrequest?.country_id)
            oCountry=Country.get(hsRes.inrequest?.country_id)
          else
            oCountry.modstatus = 0  
            
          oCountry.name = hsRes.inrequest.name
          oCountry.icon = ''
          oCountry.regorder = hsRes.inrequest.cry_regorder?:(Country.count()+1)
          oCountry.is_reserve = hsRes.inrequest.cry_reserve?:0
          oCountry.valuta_id = hsRes.inrequest.cry_valuta_id?:0          
          
          oCountry.header = hsRes.inrequest.header?:''
          oCountry.title = hsRes.inrequest.title?:''
          oCountry.keywords = hsRes.inrequest.keywords?:''
          oCountry.description = hsRes.inrequest.description?:''          
          oCountry.itext = hsRes.inrequest.itext?:''
          oCountry.is_index = hsRes.inrequest.is_index?:0
          oCountry.urlname = hsRes.inrequest.urlname?:''
          
          oCountry.name_en = hsRes.inrequest.name_en?:''
          oCountry.itext_en = hsRes.inrequest.itext_en?:''
          oCountry.description_en = hsRes.inrequest.description_en?:''
          
          if(!oCountry.save(flush:true)) {
            log.debug(" Error on save Country:")
            oCountry.errors.each{log.debug(it)}
          }
        } else if (hsRes.inrequest.type==4) {          
          def oRegion = new Region()
          
          if(hsRes.inrequest?.region_id)
            oRegion=Region.get(hsRes.inrequest?.region_id)
            
          oRegion.name = hsRes.inrequest.name
          oRegion.shortname = hsRes.inrequest.name
          
          oRegion.name_en = hsRes.inrequest.name_en?:''
          oRegion.shortname_en = hsRes.inrequest.name_en?:''
          
          oRegion.country_id = hsRes.inrequest.country_id          
          oRegion.popdirection_id=requestService.getIntDef('popdir_id',0)
          if(!oRegion.save(flush:true)) {
            log.debug(" Error on save Region:")
            oRegion.errors.each{log.debug(it)}   
          }
        } else if (hsRes.inrequest.type==5) {  
          def oCitysight=new Citysight()
          if(hsRes.inrequest?.id)
            oCitysight=Citysight.get(hsRes.inrequest?.id)
            
          oCitysight.name  = hsRes.inrequest?.name?:''
          oCitysight.name_en  = hsRes.inrequest?.name_en?:''
          oCitysight.name2  = hsRes.inrequest?.name2?:''
          oCitysight.name2_en  = hsRes.inrequest?.name2_en?:oCitysight.name2?:''
          oCitysight.urlname  = hsRes.inrequest?.urlname?:''
          oCitysight.title=hsRes.inrequest?.title?:''
          oCitysight.title_en=hsRes.inrequest?.title_en?:''
          oCitysight.header=hsRes.inrequest?.header?:''
          oCitysight.keywords=hsRes.inrequest?.keywords?:''
          oCitysight.description=hsRes.inrequest?.description?:''
          oCitysight.description_en=hsRes.inrequest?.description_en?:''
          oCitysight.itext=hsRes.inrequest?.itext?:''          
          oCitysight.x=hsRes.inrequest?.x?:0
          oCitysight.y=hsRes.inrequest?.y?:0          
          oCitysight.radius=hsRes.inrequest?.radius?:0
          oCitysight.city_id=hsRes.inrequest?.city_id?:0
          oCitysight.is_index=hsRes.inrequest?.is_index?:0
          oCitysight.modstatus=1
          oCitysight.type=hsRes.inrequest?.citysigttype?:1
          oCitysight.street_id=hsRes.inrequest?.sightstreet_id?:0
          oCitysight.district_id=hsRes.inrequest?.sightdistrict_id?:0

          if(!oCitysight.save(flush:true)) {
            log.debug(" Error on save Citysight:")
            oCitysight.errors.each{log.debug(it)}   
          }          
        }        

        redirect(action:'adress', params: [type:hsRes.inrequest?.type?:0, fromEdit:1])
        return
      }
    }
    return hsRes
  }
  def citysight_del={
    checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getParams(['id','type'],[],[])
    def oCitysight=Citysight.get(hsRes.inrequest.id)
    oCitysight.modstatus=0
    if(!oCitysight.save(flush:true)) {
      log.debug(" Error on save Citysight")
      oCitysight.errors.each{log.debug(it)}
    }

    redirect(action:'adress', params: [type:hsRes.inrequest?.type?:0, fromEdit:1])      
    return
  }
  def popdir_by_country_id={
    //checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    //hsRes.action_id=8
    hsRes.admin = session.admin    
    def iCountryId=requestService.getIntDef('country_id',0)
    if(iCountryId){
      if(hsRes.admin.accesslevel)
        hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id ORDER BY rating DESC',[country_id:iCountryId])
      else{                   
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id AND id IN (:ids) ORDER BY regorder DESC, name',[country_id:iCountryId,ids:hsRes.admin.regions])
        def lsPopdirIds=[]
        for(oReg in hsRes.region){
          lsPopdirIds<<oReg.popdirection_id
        }
        hsRes.popdirs=Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id AND id IN (:ids) ORDER BY rating DESC',[country_id:iCountryId,ids:lsPopdirIds?:[-1]])                 
      }      
    }    
    
    return hsRes
  }
  def region_by_country_id={
    //checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    //hsRes.action_id=8
    //hsRes.admin = session.admin    
    def iCountryId=requestService.getIntDef('country_id',0)
   
    if(iCountryId){     
      hsRes.regions=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC',[country_id:iCountryId])           
    }
                 
    return hsRes
  }
  def region_by_popdir_id={
    //checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    //hsRes.action_id=8
    hsRes.admin = session.admin    
    def iPopdirId=requestService.getIntDef('popdir_id',0)
    if(iPopdirId){
      if(hsRes.admin.accesslevel)
        hsRes.regions=Region.findAll('FROM Region WHERE popdirection_id=:popdirection_id ORDER BY regorder DESC',[popdirection_id:iPopdirId])
      else
        hsRes.regions=Region.findAll("FROM Region WHERE id IN (:ids) AND popdirection_id=:popdirection_id",[popdirection_id:iPopdirId,ids:hsRes.admin.regions])                 
    }  
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Adress<<<//////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////Banners >>>/////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  def showbanners={  
    checkAccess(9)
    requestService.init(this)
    def hParams=requestService.getParams(['modstatus','advbanner_id','btype','sort'],[],
                           [],['datefrom','dateto'])
    def hsRes = [inrequest:hParams.inrequest,datefrom:null,dateto:null,action_id:9]
    
    //try to parse dates for control calendar:datePicker
    if(hParams.string['datefrom']!=''){
      try{
        hsRes.datefrom=Date.parse(DATE_FORMAT, hParams.string['datefrom'])
      }catch(Exception e){
      }
    }
    if(hParams.string['dateto']!=''){
      try{
        hsRes.dateto=Date.parse(DATE_FORMAT, hParams.string['dateto'])
      }catch(Exception e){
      }
    }
    hsRes.advbannertypes=Advbannertypes.findAll('FROM Advbannertypes ORDER BY name')
    hsRes.admin = session.admin
    return hsRes 
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def listbanners={
    checkAccess(9)
    requestService.init(this)
    def hParams=requestService.getParams(['modstatus','advbanner_id','btype','sort'],[],
                          [],['datefrom','dateto'])
    def hsRes = [inrequest:hParams.inrequest, max : requestService.getMax()]
    def oBanners =new BannersSearch()
    hsRes.data=oBanners.csiGetBanners(hParams,hsRes.max,requestService.getOffset())
    return hsRes
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def editbanners={
    checkAccess(9)
    requestService.init(this)
    def hParams=requestService.getParams(['modstatus','advbanner_id','btype','sort','edit','error','zoom'],['x','y'],
                           [],['datefrom','dateto'])
    
    def hsRes=requestService.getContextAndDictionary(true) 
    hsRes.action_id=9
    hsRes.admin = session.admin
    hsRes.inrequest=hParams.inrequest

    hsRes.advbannertypes = Advbannertypes.findAll('FROM Advbannertypes')
    
    hsRes.url = ConfigurationHolder.config.urlphoto+'ar_banners/'?:'/'
    if(hsRes.url.size()==0)
      hsRes.url='/';       

    hsRes.data=Advbanner.get(requestService.getLongDef('id',0))
/*    
for(r in hsRes)
println(r)
*/
    return hsRes
  }  
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def savebanner={  
    checkAccess(9)
    requestService.init(this)
    
    def hParams=requestService.getParams(['hid_modstatus','hid_advbanner_id','hid_btype','hid_sort'],[],
      [],['hid_datefrom','hid_dateto'])

    def hsReturnParams=[:]
    for( item in hParams.inrequest)
      hsReturnParams[item.key.replace('hid_','')]=item.value

    hParams=requestService.getParams(['btype','modstatus','hsize','vsize','edit','zoom'],
    ['x','y','id','showlimit','bcount','bclick'],['bname','altname','burl'],['datefrom','dateto'])

    if(hParams.string.datefrom==''){	 
      hParams.string.datefrom = new SimpleDateFormat("yyyy-MM-dd").format(new Date())	   
    }
    if(hParams.string.dateto==''){	   
      def lDateEnd=Calendar.getInstance().getTimeInMillis()
      lDateEnd+=60*60*24*30*1000L
      def dateEnd = new Date(lDateEnd)
      hParams.string.dateto = new SimpleDateFormat("yyyy-MM-dd").format(dateEnd)
    }			
	       
    hParams.int.bclass=0                                               
    hsReturnParams['id']=hParams.long['id']	
    hsReturnParams.edit=hParams.int.edit
    def oBanners =new BannersSearch()
    def sPrevFile=''    

    def fileImage= request.getFile('filename')	
	
    if((fileImage==null || fileImage.getSize()==0) && !hParams.int.edit){	
      hsReturnParams.error=1
      redirect(action:"editbanners",params:hsReturnParams)
      return
    }
    if((fileImage)&&fileImage.getSize()>0&&(ConfigurationHolder.config.pathtophoto)){
        //check type
      def sContentType=fileImage.getContentType()
      if(sContentType in ["image/pjpeg","image/jpeg","image/png","image/x-png","image/gif"])
        hParams.int.bclass=1
      else if(sContentType=="application/x-shockwave-flash")
        hParams.int.bclass=2

      if(hParams.int.bclass>0){
        try{
          def sOrignalName=fileImage.originalFilename
           // prev file
          def sPath=ConfigurationHolder.config.pathtophoto+'ar_banners'+File.separatorChar
          if(hParams.int.id>0){
            def oBan=oBanners.csiGetBannerById(hParams.long.id)
            if((oBan!=null)&&((oBan.filename?:[]).size()>0)&&(sOrignalName!=oBan.filename))
              sPrevFile=sPath+oBan.filename.replace('/',File.separatorChar.toString())
          }
          //  load file    
          sPath+=sOrignalName[0..1]+File.separatorChar
          def d2= new File(sPath)
          d2.mkdirs()
          fileImage.transferTo(new File(sPath+sOrignalName))
          hParams.string['filename']=sOrignalName[0..1]+'/'+sOrignalName
          if(hParams.int.bclass==1){ //image
            BufferedImage biTemp = javax.imageio.ImageIO.read(new File(sPath+sOrignalName))
            hParams.int['hsize']=biTemp.getWidth(null)
            hParams.int['vsize']=biTemp.getHeight(null)
          }
        }catch(Exception e){
          log.debug(e.toString())
        }
      }
    }
    hsReturnParams['id']=oBanners.csiSetBanner(hParams)

    //delete prev file via service
    if((fileImage!=null || fileImage.getSize()>0)&&(hParams.int.bclass>0) &&(sPrevFile!='')){
      try{
        def lsPath=sPrevFile.tokenize(File.separatorChar.toString())
        def oTempPic=new Picturetemp([filename:lsPath[-1],fullname:sPrevFile])
        oTempPic.save(flush:true)
      }catch(Exception e){
        log.debug(e.toString())
      }
    }
    hsReturnParams.edit=1
    redirect(action:"editbanners",params:hsReturnParams)
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def deletebanner={
    checkAccess(9)
    requestService.init(this)
    
    def hParams=requestService.getParams(['hid_modstatus','hid_advbanner_id',
                                          'hid_btype','hid_sort'],[],
                                          [],['hid_datefrom','hid_dateto'])
    
    def hsReturnParams=[:]
    for( item in hParams.inrequest)
      hsReturnParams[item.key.replace('hid_','')]=item.value
      
    def iId=requestService.getLongDef('id',0)
    if(iId){
      def oAdvbanner =new Advbanner()
      def oBan=Advbanner.get(iId)
      def sPrevFile=''
    
      if((oBan!=null)&&((oBan.filename?:[]).size()>0)){
        sPrevFile=(ConfigurationHolder.config.pathtophoto+'ar_banners'+File.separatorChar+
          oBan.filename.replace('/',File.separatorChar.toString()))
        //delete prev file via service
        if(sPrevFile!=''){
           try{
             def lsPath=sPrevFile.tokenize(File.separatorChar.toString())
             def oTempPic=new Picturetemp([filename:lsPath[-1],fullname:sPrevFile])
             oTempPic.save(flush:true)
          }catch(Exception e){
           log.debug(e.toString())
          }
        }
      }
      oAdvbanner.csiDeleteBanner(iId)	  	
    }
    redirect(action:"showbanners",params:hsReturnParams)
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////Banners <<<////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Popdirection >>>////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  def popdir = {    
    checkAccess(10)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=10
    hsRes.admin = session.admin	
    hsRes.countries=Country.list()
    
    def fromEdit = requestService.getIntDef('fromEdit',0)
    if (fromEdit){
      session.lastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.lastRequest
    }
    return hsRes
  }

  def popdirlist = {
    checkAccess(10)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true) 
    hsRes.countries=Country.list()    
    hsRes.action_id=10
    hsRes.admin = session.admin
    def oSearch = new Popdirection()

    if (session.lastRequest?.fromEdit?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromEdit = 0
    }else {
      hsRes+=requestService.getParams(['popdir_id','popdir_country'],null,['popdir_name'])
      hsRes.inrequest.popdir_modstatus=requestService.getIntDef('popdir_modstatus',-1)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    hsRes+=oSearch.csiSelectPopdirection(hsRes.inrequest?.popdir_id?:0,hsRes.inrequest?.popdir_country?:0,hsRes.inrequest?.popdir_name?:'',hsRes.inrequest?.popdir_modstatus!=-1?hsRes.inrequest?.popdir_modstatus:-1,20,requestService.getOffset())
	
    return hsRes
  }

  def popdirdelete = {
    checkAccess(10)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=10
    hsRes.admin = session.admin	

    def lId=requestService.getLongDef('id',0)
    if (lId){
      hsRes.popdir = Popdirection.get(lId)
      hsRes.popdir.delete(flush:true)
    }

    redirect(action:"popdir",params:[fromEdit:1])
    return
  }

  def popdirhomedelete = {
    checkAccess(10)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=10
    hsRes.admin = session.admin	

    def lId=requestService.getLongDef('id',0)
    if (lId){
      def oHome = Home.get(lId)
      oHome.popdirection_id = 0
      if(!oHome.save(flush:true)) {
        log.debug(" Error on save home(administrators/popdirhomedelete/"+lId+"):")
        oHome.errors.each{log.debug(it)}
      }
    }
    render ''
  }

  def popdiredit = {
    checkAccess(10)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=10
    hsRes.admin = session.admin
    def lId=requestService.getLongDef('id',0)
    if (lId)
      hsRes.popdir = Popdirection.get(lId)
    else {
      hsRes.popdir = new Popdirection()
      hsRes.popdir.inputdate = new Date()
      hsRes.popdir.ncount = 0
    }
    if(hsRes.popdir){
      imageService.init(this,'popdirphotopic','popdirphotokeeppic',ConfigurationHolder.config.pathtophoto+'popdir'+File.separatorChar) // 0
      def bSave=requestService.getLongDef('save',0)
      def hsPics
      if(!bSave) {
        hsRes.inrequest=hsRes.popdir
        imageService.finalizeFileSession(['file1'])
      } else {
        flash.error=[]
        hsRes+=requestService.getParams(['rating','modstatus','country_id','is_main','previousdir','nextdir','id','is_index','is_specoffer'],
          [],['name','linkname','shortdescription','annotation','keyword','itext','ceo_title','ceo_keywords',
          'ceo_description','ceo_title_d','ceo_keywords_d','ceo_description_d','name2','name_r','header',
          'header_d','tagname','header_cot','ceo_title_cot','ceo_keywords_cot','ceo_description_cot','annotation_cot','itext_cot',
          'name_en','name2_en','header_en','shortdescription_en','annotation_en','header_d_en','header_cot_en','ceo_title_en',
          'ceo_title_d_en','ceo_title_cot_en','ceo_description_en','ceo_description_d_en','ceo_description_cot_en','annotation_cot_en'])
        if (!hsRes.inrequest.name)
          flash.error<<1
        if (!hsRes.inrequest.name2)
          flash.error<<5
        if (!hsRes.inrequest.linkname)
          flash.error<<2
        if(!lId){
          if (Popdirection.findByName(hsRes.inrequest?.name?:''))
            flash.error<<3
          /*if (Popdirection.findBylinkname(hsRes.inrequest?.linkname?:''))
            flash.error<<4*/
        }
        if(!flash.error){
          try {
            hsRes.popdir.country_id = hsRes.inrequest.country_id?:0
            hsRes.popdir.name = hsRes.inrequest.name
            hsRes.popdir.name2 = hsRes.inrequest.name2
            hsRes.popdir.name_r = hsRes.inrequest.name_r?:hsRes.inrequest.name2
            hsRes.popdir.header = hsRes.inrequest.header?:''
            hsRes.popdir.linkname = hsRes.inrequest.linkname
            hsRes.popdir.shortdescription = hsRes.inrequest.shortdescription?:''
            hsRes.popdir.tagname = hsRes.inrequest.tagname?Tools.transliterate(hsRes.inrequest.tagname):''
            hsRes.popdir.annotation = hsRes.inrequest.annotation?:''
            hsRes.popdir.keyword = hsRes.inrequest.keyword?:''
            hsRes.popdir.itext = hsRes.inrequest.itext?:''
            hsRes.popdir.ceo_title = hsRes.inrequest.ceo_title?:''
            hsRes.popdir.ceo_keywords = hsRes.inrequest.ceo_keywords?:''
            hsRes.popdir.ceo_description = hsRes.inrequest.ceo_description?:''
            hsRes.popdir.header_d = hsRes.inrequest.header_d?:''
            hsRes.popdir.ceo_title_d = hsRes.inrequest.ceo_title_d?:''
            hsRes.popdir.ceo_keywords_d = hsRes.inrequest.ceo_keywords_d?:''
            hsRes.popdir.ceo_description_d = hsRes.inrequest.ceo_description_d?:''
            hsRes.popdir.rating = hsRes.inrequest.rating?:0
            hsRes.popdir.nextdir = hsRes.inrequest.nextdir?:0
            hsRes.popdir.previousdir = hsRes.inrequest.previousdir?:0
            hsRes.popdir.is_main = hsRes.inrequest.is_main?:0
            hsRes.popdir.is_index = hsRes.inrequest.is_index?:0
            hsRes.popdir.modstatus = hsRes.inrequest.modstatus?:0
            hsRes.popdir.header_cot = hsRes.inrequest.header_cot?:''
            hsRes.popdir.ceo_title_cot = hsRes.inrequest.ceo_title_cot?:''
            hsRes.popdir.ceo_keywords_cot = hsRes.inrequest.ceo_keywords_cot?:''
            hsRes.popdir.ceo_description_cot = hsRes.inrequest.ceo_description_cot?:''
            hsRes.popdir.annotation_cot = hsRes.inrequest.annotation_cot?:''
            hsRes.popdir.itext_cot = hsRes.inrequest.itext_cot?:''
            hsRes.popdir.moddate = new Date()            
            hsRes.popdir.is_specoffer=hsRes.inrequest?.is_specoffer?:0   

            hsRes.popdir.name_en = hsRes.inrequest.name_en?:''
            hsRes.popdir.name2_en = hsRes.inrequest.name2_en?:''
            hsRes.popdir.header_en = hsRes.inrequest.header_en?:''            
            hsRes.popdir.shortdescription_en = hsRes.inrequest.shortdescription_en?:''
            hsRes.popdir.annotation_en = hsRes.inrequest.annotation_en?:''
            hsRes.popdir.header_d_en = hsRes.inrequest.header_d_en?:''
            hsRes.popdir.header_cot_en = hsRes.inrequest.header_cot_en?:''
            hsRes.popdir.ceo_title_en = hsRes.inrequest.ceo_title_en?:''
            hsRes.popdir.ceo_title_d_en = hsRes.inrequest.ceo_title_d_en?:''
            hsRes.popdir.ceo_title_cot_en = hsRes.inrequest.ceo_title_cot_en?:''
            hsRes.popdir.ceo_description_en = hsRes.inrequest.ceo_description_en?:''
            hsRes.popdir.ceo_description_d_en = hsRes.inrequest.ceo_description_d_en?:''          
            hsRes.popdir.ceo_description_cot_en = hsRes.inrequest.ceo_description_cot_en?:''                        
            hsRes.popdir.annotation_cot_en = hsRes.inrequest.annotation_cot_en?:''
            
            hsPics=imageService.getSessionPics('file1')
            if((hsRes.popdir.picture?:'')!=''&&!hsPics)
              imageService.putIntoSessionFromDb(hsRes.popdir.picture,'file1') // 2
            hsRes.popdir.picture=(hsPics?.photo)?:hsRes.popdir.picture?:''

            hsRes.popdir.save(flush:true,failOnError:true)
            imageService.finalizeFileSession(['file1'])
          } catch(Exception e) {
            flash.save_error = 101
            log.debug('Administrators:popdiredit. Error on save popdir:'+lId+'\n'+e.toString())
          }
          hsRes.inrequest=hsRes.popdir
        }
      }

      imageService.startFileSession() // 1
      if(hsRes.popdir.picture!='')
        imageService.putIntoSessionFromDb(hsRes.popdir.picture,'file1') // 2
      hsPics=imageService.getSessionPics('file1') // 3
      if(hsPics!=null){
        hsRes.inrequest.picture=hsPics.photo
      }

      hsRes.countries=Country.list()
      hsRes.nearDirections = Popdirection.findAllByModstatusAndIs_active(1,1)
      def oHome = new HomeSearch()
      hsRes.records = oHome.csiFindPopdirection(hsRes.popdir.id?:0,0)
      hsRes.urlphoto = ConfigurationHolder.config.urlphoto
      hsRes.imageurl = ConfigurationHolder.config.urlpopdiphoto
    } else {
      redirect(action:'index')
      return
    }
    return hsRes
  }

  def savepopdirphoto={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)

    imageService.init(this,'popdirphotopic','popdirphotokeeppic',ConfigurationHolder.config.pathtophoto+'popdir'+File.separatorChar) // 0
    def iNo=1

    //ÇÀÃÐÓÆÀÅÌ ÃÐÀÔÈÊÓ
    def hsData= imageService.loadPicture(
      "file1",
      Tools.getIntVal(ConfigurationHolder.config.photo.weight,2097152), //weight
      Tools.getIntVal(ConfigurationHolder.config.popdirphoto.image.size,710),  // size
      Tools.getIntVal(ConfigurationHolder.config.popdirphoto.thumb.size,220), //thumb size
      true,//SaveThumb
      false,//square
      Tools.getIntVal(ConfigurationHolder.config.popdirphoto.image.height,425),//height
      Tools.getIntVal(ConfigurationHolder.config.popdirphoto.thumb.height,160),//thumb height
      false,
      false
    ) // 3

    hsData['num']=1 //<- ÍÅÎÁÕÎÄÈÌÎ ÄËß ÊÎÐÐÅÊÒÍÎÉ ÐÀÁÎÒÛ js â savepictureresult

    // savepictureresult ÎÁÙÈÉ ØÀÁËÎÍ, ÅÑËÈ ÈÑÏÎËÜÇÎÂÀÒÜ ÑÊÐÈÏÒÛ ÀÍÀËÎÃÈ×ÍÎ ÑÄÅËÀÍÍÎÌÓ
    render(view:'savepictureresult',model:hsData)
    return
  }

  def deletepopdirphoto={
    //TODO: check user logged in
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)  

    //ÎÁßÇÀÒÅËÜÍÀß ÈÍÈÖÈÀËÈÇÀÖÈß TODO: path into cfg
    imageService.init(this,'popdirphotopic','popdirphotokeeppic',ConfigurationHolder.config.pathtophoto+'popdir'+File.separatorChar)
    
    def sName=requestService.getStr("name")

    imageService.deletePicture(sName)//4
    render(contentType:"application/json"){[error:false]}
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Popdirection >>>////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////User administration >>>////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def administration = {
    checkAccess(2)
    requestService.init(this)
    def hsRes = [inrequest:[part:requestService.getLongDef('part',0)],action_id:2]
    if (hsRes.inrequest.part){
      hsRes += [groupusers:Admin.findAll('from Admin')]
    } else {
      hsRes += [groupusers:Admingroup.findAll('from Admingroup')]
    }
    hsRes.admin = session.admin
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def groupuserlist = {
    checkAccess(2)
    requestService.init(this)
    def hsRes = [inrequest:[part:requestService.getLongDef('id',0)]]
    if (hsRes.inrequest.part){
      hsRes += [groupusers:Admin.findAll('from Admin where id<>1')]
    }else{
      hsRes += [groupusers:Admingroup.findAll('from Admingroup')]
    }
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def groupuserdetails = {
    checkAccess(2)
    requestService.init(this)
    def hsRes = [inrequest:[id   :requestService.getLongDef('id',0),
                            part :requestService.getLongDef('part',0)]]    

    if (hsRes.inrequest.part){
	    hsRes += [groups:Admingroup.findAll('from Admingroup')]
      if (hsRes.inrequest.id&&hsRes.inrequest.id != 1){        
        hsRes += [user:Admin.get(hsRes.inrequest.id)]          
        def oAdmin = new Admin()
        hsRes += [
                   user_regions : hsRes.user.accesslevel?[]:oAdmin.csiGetRegionIds(hsRes.user.id)                   
                  ]                 
        if(!hsRes.user.accesslevel && !hsRes.user_regions)
          hsRes.user_regions=[-1]                
                       
        hsRes.country=Country.list(order:"regorder")                  
        
        hsRes.regions=[]
        for(oCountry in hsRes.country){
          hsRes.regions<<Region.findAll("from Region WHERE country_id=:country_id order by regorder desc, name",[country_id:oCountry.id])
        }                        
      }
    }else{
      hsRes += [group:Admingroup.get(hsRes.inrequest.id)]
    }
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def groupsave = {
    checkAccess(2)
    requestService.init(this)
    def hParams = requestService.getParams(['id','is_profile','is_groupmanage','is_users','is_homes',  
                                            'is_reviews','is_adress','is_banners','is_review', 
                                            'is_infotext','is_popdir','is_stats','is_guestbook','is_notetype','is_mail',
                                            'is_zayavka','is_sms','is_mbox','is_article','is_selection','is_clients',
                                            'is_bank','is_payorder','is_account','is_paytrans','is_paytask','is_anons',
                                            'is_payreport','is_trip','is_hometype'])
    if (hParams.inrequest.id>0){
      def oAdmingroup = Admingroup.get(hParams.inrequest.id)
      try {
        def sMenu = '1,'
        oAdmingroup.is_profile = 1
        if (hParams.inrequest.is_users)      {oAdmingroup.is_users=1;    sMenu += '3,'}
        else oAdmingroup.is_users=0
        if (hParams.inrequest.is_homes)      {oAdmingroup.is_homes=1;    sMenu += '4,'}
        else oAdmingroup.is_homes=0
        if (hParams.inrequest.is_infotext)   {oAdmingroup.is_infotext=1; sMenu += '5,'}
        else oAdmingroup.is_infotext=0
        if (hParams.inrequest.is_reviews)    {oAdmingroup.is_reviews=1;  sMenu += '6,'}
        else oAdmingroup.is_reviews=0
        if (hParams.inrequest.is_stats)      {oAdmingroup.is_stats=1;    sMenu += '7,'}
        else oAdmingroup.is_stats=0	  
        if (hParams.inrequest.is_adress)     {oAdmingroup.is_adress=1;   sMenu += '8,'}	  	  
        else oAdmingroup.is_adress=0
        if (hParams.inrequest.is_banners)    {oAdmingroup.is_banners=1;  sMenu += '9,'}
        else oAdmingroup.is_banners=0          
        if (hParams.inrequest.is_popdir)     {oAdmingroup.is_popdir=1;   sMenu += '10,'}
        else oAdmingroup.is_popdir=0
		    if (hParams.inrequest.is_guestbook)  {oAdmingroup.is_guestbook=1;   sMenu += '11,'}
        else oAdmingroup.is_guestbook=0
		    if (hParams.inrequest.is_notetype)   {oAdmingroup.is_notetype=1;   sMenu += '12,'}
        else oAdmingroup.is_notetype=0		
        if (hParams.inrequest.is_zayavka)   {oAdmingroup.is_zayavka=1;   sMenu += '13,'}
        else oAdmingroup.is_zayavka=0
        if (hParams.inrequest.is_sms)   {oAdmingroup.is_sms=1;   sMenu += '14,'}
        else oAdmingroup.is_sms=0
		    if (hParams.inrequest.is_mail)       {oAdmingroup.is_mail=1;   sMenu += '15,'}
        else oAdmingroup.is_mail=0
        if (hParams.inrequest.is_mbox)   {oAdmingroup.is_mbox=1;   sMenu += '16,'}
        else oAdmingroup.is_mbox=0
        if (hParams.inrequest.is_article)   {oAdmingroup.is_article=1;   sMenu += '17,'}
        else oAdmingroup.is_article=0
        if (hParams.inrequest.is_clients)   {oAdmingroup.is_clients=1;   sMenu += '18,'}
        else oAdmingroup.is_clients=0
        if (hParams.inrequest.is_bank)   {oAdmingroup.is_bank=1;   sMenu += '19,'}
        else oAdmingroup.is_bank=0
        if (hParams.inrequest.is_selection)   {oAdmingroup.is_selection=1;   sMenu += '20,'}
        else oAdmingroup.is_selection=0
        if (hParams.inrequest.is_payorder)   {oAdmingroup.is_payorder=1;   sMenu += '21,'}
        else oAdmingroup.is_payorder=0
        if (hParams.inrequest.is_account)   {oAdmingroup.is_account=1;   sMenu += '22,'}
        else oAdmingroup.is_account=0
        if (hParams.inrequest.is_paytrans)   {oAdmingroup.is_paytrans=1;   sMenu += '23,'}
        else oAdmingroup.is_paytrans=0
        if (hParams.inrequest.is_paytask)   {oAdmingroup.is_paytask=1;   sMenu += '25,'}
        else oAdmingroup.is_paytask=0
        if (hParams.inrequest.is_anons)   {oAdmingroup.is_anons=1;   sMenu += '26,'}
        else oAdmingroup.is_anons=0
        if (hParams.inrequest.is_payreport)   {oAdmingroup.is_payreport=1;   sMenu += '27,'}
        else oAdmingroup.is_payreport=0
        if (hParams.inrequest.is_trip)   {oAdmingroup.is_trip=1;   sMenu += '28,'}
        else oAdmingroup.is_trip=0
        if (hParams.inrequest.is_hometype)   {oAdmingroup.is_hometype=1;   sMenu += '29,'}
        else oAdmingroup.is_hometype=0                
        
        if (oAdmingroup.is_superuser)        sMenu+='2,'	  
	  
        oAdmingroup.menu = sMenu
    
        if (!oAdmingroup.save(flush:true)){
          log.debug('error on save Admingroup: Administrators.groupsave')
          oAdmingroup.errors.each{log.debug(it)}
        }
      } catch(Exception e){
        log.debug('error in Administrators.groupsave')
        log.debug(e.toString())
      }
      def hsRes = [id:hParams.inrequest.id]
      hsRes['part'] = 0

      redirect(action:'groupuserdetails',params:hsRes)
      return
    }

    render(contentType:"application/json"){[error:true]}
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def usersave = {
    checkAccess(2)
    requestService.init(this)
    def hParams = requestService.getParams(['group','accesslevel'],['id'],['login','name','email','confirm_pass','pass']) 
    if (!hParams.inrequest.accesslevel) hParams.inrequest.region_ids = requestService.getIds('region_id')    
    
    def errors=[]
    
    if(!hParams.inrequest.login)
      errors<<1
    if(!hParams.inrequest.name)
      errors<<2
    if(!hParams.inrequest.email)
      errors<<3
    
    if(errors){    
      render(contentType:"application/json"){[error:true,errors:errors]}
      return
    }  
    if (hParams.inrequest.id>1){
      def oAdmin = Admin.get(hParams.inrequest.id)
      try{
        oAdmin.admingroup_id = hParams.inrequest.group?:0        
        oAdmin.login = hParams.inrequest.login?:''
        oAdmin.name = hParams.inrequest.name?:''
        oAdmin.email = hParams.inrequest.email?:''
        //oAdmin.password = oAdmin.password?:''//wtf???
        oAdmin.accesslevel=hParams.inrequest.accesslevel?:0
        if (!oAdmin.save(flush:true)){
          log.debug('error on save Admin: Administrators.usersave')
          oAdmin.errors.each{log.debug(it)}
        }        
        
        def lsAdminReg = Admin2reg.findAllWhere(admin_id:hParams.inrequest.id)
        lsAdminReg.each{it.delete()}
        if (!hParams.inrequest.accesslevel)
          hParams.inrequest.region_ids.each{
            def oAdmin2reg = new Admin2reg(admin_id  :hParams.inrequest.id,
                                           region_id :it)
            if (!oAdmin2reg.save(flush:true)){
              log.debug('error on add Admin2reg: Administrators.usersave')
              oAdmin2reg.errors.each{log.debug(it)}
            }
          }
      }catch(Exception e){
        log.debug('error in Administrators.usersave')
        log.debug(e.toString())
      }                  
      //def hsRes = [id:hParams.inrequest.id]
      //hsRes['part'] = 1

      //redirect(action:'groupuserdetails',params:hsRes)
      //return
      render(contentType:"application/json"){[error:false]}
      return
    }

    render(contentType:"application/json"){[error:true]}
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def creategroup = {
    checkAccess(2)
    requestService.init(this)
    def hsRes = [done:true, message:'Ошибка']
    def sName = requestService.getStr('name')
    if (sName) {
      def lsAdmingroups = Admingroup.findAllWhere(name:sName)
      if (!lsAdmingroups){
         def oAdmingroup = new Admingroup(name:sName,menu:'',is_profile:0,is_groupmanage:0,
                                          is_users:0,is_homes:0,is_adress:0,is_banners:0,
                                          is_reviews:0,is_infotext:0,is_popdir:0,is_stats:0,
                                          is_superuser:0,is_guestbook:0,is_notetype:0,is_mail:0,
                                          is_mbox:0,is_zayavka:0,is_sms:0,is_selection:0,
                                          is_article:0,is_clients:0,is_bank:0,is_payorder:0,
                                          is_account:0,is_paytrans:0,is_paytask:0,is_payreport:0,is_trip:0,is_hometype:0)
        if (!oAdmingroup.save(flush:true)){
          log.debug('Error on create Admingroup: Administrators.creategroup')
          oAdmingroup.errors.each{log.debug(it)}
          hsRes = [done:true,message:message(code:'admin.group.add.error', default:'')]
        }else{
          hsRes = [done:true]
		  hsRes.id=oAdmingroup.id
        }
      }else{
        hsRes = [done:false,message:message(code:'admin.group.add.alreadyexists.error', default:'')]
      }
    }else{
      hsRes = [done:false,message:message(code:'admin.group.add.entername.error', default:'')]
    }
    render hsRes as JSON
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def createuser = {
    checkAccess(2)
    requestService.init(this)
    def hsRes = [done:true, message:message(code:'error', default:'')]
    def sLogin = requestService.getStr('login')
    def sPass = requestService.getStr('pass')
    if (sLogin) {
      def lsAdmin = Admin.findAllWhere(login:sLogin)
      if (!lsAdmin){
        if(sPass.size()<Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,5)){
         	
          hsRes = [done: false,message:message(code:'admin.passw.min.length.error', default:'')+' '+Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,5)]	  
        }else if (sPass==requestService.getStr('confirm_pass')){
          def oAdmin = new Admin(login:sLogin,password:Tools.hidePsw(sPass),
                                 email:'',name:'',admingroup_id:0,accesslevel:0)
          if (!oAdmin.save(flush:true)){
            log.debug('Error on create Admin: Administrators.createuser')
            oAdmin.errors.each{log.debug(it)}
            hsRes = [done:true,message:message(code:'admin.adduser.error', default:'')]			
          }else{
            hsRes = [done:true]
			hsRes.id=oAdmin.id
          }
        }else{
          hsRes = [done:false, message:message(code:'admin.passwordequal.error', default:'')]
        }
      }else{
        hsRes = [done:false,message:message(code:'admin.user.alreadyexists.error', default:'')]
      }
    }else{
      hsRes = [done:false, message:message(code:'admin.enter.user.login', default:'')]
    }
    render hsRes as JSON
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def deleteuser = {
    checkAccess(2)
    requestService.init(this)
    def hsRes = [done:false, message:'Ошибка']
    def lId = requestService.getLongDef('id',0)
    if (lId>1){
      if (lId == session.admin.id)
        hsRes.message = message(code:'admin.user.not.delete.error', default:'')
      else{
        def oAdmin = Admin.get(lId)
		if(oAdmin){
          oAdmin.delete()
          hsRes.done = true
		}
      }
    }
    render hsRes as JSON
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def deletegroup = {
    checkAccess(2)
    requestService.init(this)
    def lId = requestService.getLongDef('id',0)
    if (lId>0){
      def oAdmingroup = Admingroup.get(lId)
      oAdmingroup.delete()
    }

    render(contentType:"application/json"){[ok:true]}
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////User administration <<<////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Guestbook >>>////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def guestbook = {
    checkAccess(11)
    requestService.init(this)
    def hParams = requestService.getParams(['type','status'],[],[],['dateto','datefrom'])
    def hsRes = [inrequest:hParams.inrequest, action_id:11 ]
    if(hParams.string['datefrom']!=''){
      try{
        hsRes.datefrom=Date.parse("yyyy-MM-dd", hParams.string['datefrom'])
      }catch(Exception e){
      }
    }
    if(hParams.string['dateto']!=''){
      try{
        hsRes.dateto=Date.parse("yyyy-MM-dd", hParams.string['dateto'])
      }catch(Exception e){
      }
    }
    hsRes += requestService.getContextAndDictionary(true)
    def oAdmin = Admin.get(session.admin.id)
    def oAdmingroup = Admingroup.get(oAdmin.admingroup_id)
    hsRes.inrequest.types = []    
    hsRes.admin = session.admin
    hsRes.gbtype=Gbtype.findAll("FROM Gbtype")
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def guestbookrecords = {
    checkAccess(11)
    requestService.init(this)
    def hParams=requestService.getParams(['type','status'],[],[],['datefrom','dateto'])
    def hsRes = [inrequest:hParams.inrequest]
    hsRes.max = requestService.getMax()
    def oGuestbook = new GbmessageSearch()
    hsRes += [messages : oGuestbook.csiGetMessages(hParams,hsRes.max,requestService.getOffset()),
              regdictionary : session.admin.regdictionary]
    hsRes += requestService.getContextAndDictionary(true)
    hsRes['imageurl']=ConfigurationHolder.config.urlimg	
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def gbmessagedelete = {  
    checkAccess(11)
    requestService.init(this)
    def lId = requestService.getLongDef('id',0)
    def hParams = requestService.getParams(['type','status'],[],[],['datefrom','dateto'])
    def oMessage = Guestbook.get(lId)    
    oMessage.delete(flush:true)
    redirect(action:'guestbook',params:hParams.inrequest)
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def gbchangestatus = {    
    checkAccess(11)
    requestService.init(this)
    def lId = requestService.getLongDef('id',0)
    def iStatus=requestService.getLongDef('status',0)    
    def hsRes = [done:true]
    try{
      def oMessage = Guestbook.get(lId)
      def bRead = oMessage?.modstatus?false:true
      oMessage.csiChangeStatus(lId,bRead)
    }catch(Exception e){
      log.debug('Administrators:gbchangestatus : Error on change Guestbook.status')
      log.debug(e.toString())
      hsRes.done = false
    }

    render(contentType:"application/json"){[error:false]}
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Guestbook <<<///////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Notetype >>>///////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def notetype={
    checkAccess(12)
    requestService.init(this)
    def hsRes = [action_id:12]
    hsRes.email_template = Email_template.list()          
    hsRes.admin = session.admin
    return hsRes
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def notetypelist = {
    checkAccess(12)
    requestService.init(this)
    def hParams=requestService.getParams(['id','email_template_id']) 
    def hsRes = [inrequest:hParams.inrequest]
    hsRes.email_template = Email_template.list()              
    hsRes.max = 20
    def oNotetype = new Notetype()
    hsRes += oNotetype.csiGetNotetype(hsRes.inrequest?.id?:0,hsRes.inrequest?.email_template_id?:0,hsRes.max,requestService.getOffset())
    return hsRes
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def notetypeedit={
    checkAccess(12)
    requestService.init(this)
    def hsRes=requestService.getParams(['email_template_id','max_notes','dayinterval','param','modstatus','bSave'],['id'],
                           ['name','notetext','icon','notetext_en'],[])    
   	hsRes.action_id=12
    hsRes.admin = session.admin
    hsRes.email_template = Email_template.list()          

    if(!hsRes.inrequest.bSave)
      hsRes.inrequest=Notetype.get(requestService.getLongDef('id',0))
/*    
for(r in hsRes)
println(r)
*/
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def notetypesave={
    checkAccess(12)
    requestService.init(this)
    def hsRes=requestService.getParams(['email_template_id','max_notes','dayinterval','param','modstatus'],['id'],
                           ['name','notetext','icon','notetext_en'],[])    

    if(hsRes.inrequest.id){
      def oNotetype=Notetype.get(hsRes.inrequest.id)
      if(oNotetype){
        oNotetype.email_template_id=hsRes.inrequest?.email_template_id?:0
        oNotetype.max_notes=hsRes.inrequest?.max_notes?:0
        oNotetype.dayinterval=hsRes.inrequest?.dayinterval?:0
        oNotetype.param=hsRes.inrequest?.param?:0
        oNotetype.name=hsRes.inrequest?.name?:''
        oNotetype.notetext=hsRes.inrequest?.notetext?:''
        oNotetype.modstatus=hsRes.inrequest?.modstatus?:0
        oNotetype.notetext_en=hsRes.inrequest?.notetext_en?:''
	
        if (!oNotetype.save(flush:true)){
          log.debug('error on save Notetype. in Admin:notetypesave')
          oNotetype.errors.each{log.debug(it)}
        }
      }
    }	    
/*    
for(r in hsRes)
println(r)
*/  hsRes.inrequest.bSave=1
    redirect(action:'notetypeedit',params:hsRes.inrequest)
  }    
  
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Notetype <<<///////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Proposal >>>///////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def proposal = {
    checkAccess(13)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=13
    hsRes.admin = session.admin
    
    def fromEdit = requestService.getIntDef('fromEdit',0)
    if (fromEdit){
      session.lastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.lastRequest
    }    
    
    if(hsRes.admin.accesslevel){
      hsRes.country=Country.list()
	    hsRes.region=Region.list()
    }else{
      hsRes.region=Region.findAll("FROM Region WHERE id in (:ids)",[ids:hsRes.admin.regions])
      def lsCountryIds=[]
      for(oRegion in hsRes.region){
        if(!(oRegion.country_id in lsCountryIds))
          lsCountryIds<<oRegion.country_id
      }                  
      hsRes.country=Country.findAll('FROM Country WHERE id IN (:ids) ORDER BY regorder',[ids:lsCountryIds?:[-1]])      
    }
	
    return hsRes
  }

  def proposallist = {
    checkAccess(13)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=13
    hsRes.admin = session.admin
	
    if (session.lastRequest?.fromModer?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromModer = 0
    } else {
      hsRes+=requestService.getParams(['country_id', 'region_id','modstatus','is_auto'],['id'],['city'])
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    def oZayavkaSearch=new ZayavkaSearch()
    hsRes+=oZayavkaSearch.csiSelectZayavka(hsRes.inrequest?.id?:0,hsRes.inrequest?.country_id?:0,hsRes.inrequest?.region_id?:0,hsRes.inrequest?.modstatus?:0,hsRes.inrequest?.city?:'',hsRes.inrequest?.is_auto?:0,20,requestService.getOffset(),hsRes.admin.regions)
    return hsRes
  }

  def editproposal = {
    checkAccess(13)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=13
    hsRes.admin = session.admin
    def lId=requestService.getLongDef('id',0)
	
	hsRes.prop = Zayavka.get(lId)
	if (!hsRes.prop){
	  redirect(action:'index')
	  return
	}
	hsRes.country=Country.list()
	hsRes.region=Region.list()
	hsRes.user = User.get(hsRes.prop.user_id)
	hsRes.kolprop = Zayvka2client.countByZayvka_idAndModstatusGreaterThanEquals(hsRes.prop.id,0)
	hsRes.valuta=Valuta.findAll('FROM Valuta WHERE modstatus=1 ORDER BY regorder')
	hsRes.timetodecide=Timetodecide.list()
	if (hsRes.prop.modstatus==1) {
	  def obj = new Zayvka2clientSearch()
	  def hObj = new HomeAlikeSearch()
	  hsRes.records = obj.csiSelectZayvka2client(lId)
	  hsRes.homes = Mbox.findAllByZayvka_id(lId).collect{ hObj.csiGetHomeWithPrice(it.home_id)}
	} else if (hsRes.prop.modstatus!=-2) {
	  hsRes+=zayavkaService.csiFindClient(hsRes.prop,[hsRes.prop.baseclient_id])
	}
	hsRes.homeimageurl = ConfigurationHolder.config.urlphoto
  hsRes.imageurl = ConfigurationHolder.config.urluserphoto
	def oValutarate = new Valutarate()
	hsRes.valutaRates = oValutarate.csiGetRate(hsRes.prop.valuta_id)
	hsRes.valutaSym = Valuta.get(hsRes.prop.valuta_id).symbol

	return hsRes
  }

  def proposalsave = {
    checkAccess(13)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=13
    hsRes.admin = session.admin
    def lId=requestService.getLongDef('id',0)
	
	hsRes.prop = Zayavka.get(lId)
	if (!hsRes.prop){
	  redirect(action:'index')
	  return
	}
	
	flash.error = []
	hsRes+=requestService.getParams(['country_id', 'region_id'],['id','pricefrom','priceto'],['city','ztext'])
	def lsDirectory=requestService.getParams(['country_id','region_id']).inrequest
	if((lsDirectory?:[]).size()!=2)
	  flash.error<<10
	if(!(hsRes.inrequest?.city?:''))
	  flash.error<<1
	if(!(hsRes.inrequest?.ztext?:''))
	  flash.error<<2
	if(!(hsRes.inrequest?.pricefrom?:0))
	  flash.error<<3
	if (!flash.error.size()){
		hsRes.prop.pricefrom = hsRes.inrequest?.pricefrom
		hsRes.prop.priceto = hsRes.inrequest?.priceto?:0
		hsRes.prop.city = hsRes.inrequest?.city
		hsRes.prop.ztext = hsRes.inrequest?.ztext
		hsRes.prop.country_id = hsRes.inrequest?.country_id
		hsRes.prop.region_id = hsRes.inrequest?.region_id
		hsRes.prop.setPrice_rub()
		if (!hsRes.prop.save(flush:true)){
			log.debug('error on save Zayavka. in Admin:proposalsave')
			hsRes.prop.errors.each{log.debug(it)}
			flash.error<<101
		}
	}
	
	redirect(action:'editproposal',id:lId)
	return
  }

  def proposaldelete = {
    checkAccess(13)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=13
    hsRes.admin = session.admin
    def lId=requestService.getLongDef('id',0)

	hsRes.prop = Zayavka.get(lId)
	if (!hsRes.prop){
	  redirect(action:'index')
	  return
	}
	
	if (hsRes.prop.modstatus!=-2) {
		hsRes.prop.modstatus = -2
	} else if (Zayvka2client.countByZayvka_idAndModstatusGreaterThanEquals(hsRes.prop.id,0)){
		hsRes.prop.modstatus = 1
	} else if (User.get(hsRes.prop.user_id).modstatus!=1){
		hsRes.prop.modstatus = -1
	} else {
		hsRes.prop.modstatus = 0
	}

	if (!hsRes.prop.save(flush:true)){
		log.debug('error on save Zayavka. in Admin:proposaldelete')
		hsRes.prop.errors.each{log.debug(it)}
		flash.error=[]
		flash.error<<101
	}
	def sAct=requestService.getStr('act')
	
	redirect(action:sAct,id:lId,params:[fromEdit:1])
	return
  }

  def allocateproposal = {
    checkAccess(13)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=13
    hsRes.admin = session.admin
    def lId=requestService.getLongDef('id',0)
	def clIds=requestService.getStr('clIds')
	def clIdsStep=requestService.getStr('clIdsStep')

	hsRes.prop = Zayavka.get(lId)
	if (!hsRes.prop){
	  redirect(action:'index')
	  return
	}

	def t = 0
	def step = 1
	def temp = 0
  User oUser = null
  int counter = 0
	clIds = clIds.replace(' ','').replace('[','').replace(']','').split(',')-(hsRes.prop.baseclient_id?.toString()?:'')
	clIdsStep = clIdsStep.replace(' ','').replace('[','').replace(']','').split(',').collect{ t += (it as int) }
	def todate = new GregorianCalendar()
	todate.setTime(hsRes.prop.inputdate)
	todate.add(Calendar.DATE,Timetodecide.get(hsRes.prop.timetodecide_id).days)
	for (ids in clIds){
		for(int i=step-1; i<4; i++)
			if(clIdsStep[i]<=temp)
				step++
			else break
		def oTObj = new Zayvka2client(lId,ids.toLong(),todate.getTime(),step)
    oUser = User.findByClient_id(ids)
		try{
			oTObj.save(flush:true)
			hsRes.prop.modstatus = 1
			hsRes.prop.save(flush:true)
      if (oUser?.is_emailzayvka && counter<Tools.getIntVal(ConfigurationHolder.config.zayvka_mail.max,10)) {
        (new Zayavkabyemail(oUser.email,oUser.nickname,hsRes.prop.city,hsRes.prop.ztext,hsRes.prop.date_start,hsRes.prop.date_end)).save(flush:true)
        ++counter
      }
		} catch (Exception e){
			log.debug("Error on save Zayvka2client in Admin:allocateproposal\n"+e.toString())
		}
		temp++
	}

	redirect(action:'editproposal',id:lId)
	return
  }

  def allocateAllNewProposal = {
    checkAccess(13)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=13
    hsRes.admin = session.admin
    
    def t = 0
    def step = 1
    def temp = 0
    User oUser = null
    int counter = 0
    def aProps = []
    if(hsRes.admin.accesslevel)
      aProps =Zayavka.findAllByModstatus(0)
    else
      aProps =Zayavka.findAll("FROM Zayavka WHERE modstatus=0 AND region_id IN (:ids)",[ids:hsRes.admin.regions])
             
    for (prop in aProps){
      def rec = zayavkaService.csiFindClient(prop,[prop.baseclient_id])
      if (rec.clientIds.size()>0){
        t = 0
        step = 1
        temp = 0
        counter = 0
        rec.clientIdsStep = rec.clientIdsStep.collect{ t += (it as int) }
        def todate = new GregorianCalendar()
        todate.setTime(prop.inputdate)
        todate.add(Calendar.DATE,Timetodecide.get(prop.timetodecide_id).days)
        for (ids in rec.clientIds){
          for(int i=step-1; i<4; i++)
            if(rec.clientIdsStep[i]<=temp)
              step++
            else break
          def oTObj = new Zayvka2client(prop.id,ids.toLong(),todate.getTime(),step)
          oUser = User.findByClient_id(ids)
          try{
            oTObj.save(flush:true)
            prop.modstatus = 1
            prop.save(flush:true)
            if (oUser?.is_emailzayvka && counter<Tools.getIntVal(ConfigurationHolder.config.zayvka_mail.max,10)) {
              (new Zayavkabyemail(oUser.email,oUser.nickname,prop.city,prop.ztext,prop.date_start,prop.date_end)).save(flush:true)
              ++counter
            }
          } catch (Exception e){
            log.debug("Error on save Zayvka2client in Admin:allocateAllNewProposal\n"+e.toString())
          }
          temp++
        }
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def closeproposal = {
    checkAccess(13)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=13
    hsRes.admin = session.admin
    def lId=requestService.getLongDef('id',0)

    hsRes.prop = Zayavka.get(lId)
    if (!hsRes.prop){
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.prop.modstatus = -4
      hsRes.prop.save(flush:true)
      mailerService.closeZayavkaMail(hsRes.prop)
    } catch(Exception e) {
      log.debug('error on save Zayavka. in Admin:closeproposal\n'+ e.toString())
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:true]}
    return
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Proposal <<<////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Sms >>>/////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def sms = {
    checkAccess(14)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=14
    hsRes.admin = session.admin

    return hsRes
  }

  def smslist = {
    checkAccess(14)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=14
    hsRes.admin = session.admin
	
	hsRes+=requestService.getParams(null,['user_id'],['status'])
	hsRes.inrequest.inputdate = requestService.getDate('inputdate')

	def oSmsSearch = new SmsSearch()
    hsRes+=oSmsSearch.csiSelectSms(hsRes.inrequest.user_id?:0,hsRes.inrequest.status?:'',hsRes.inrequest.inputdate?:'',DATE_FORMAT,20,requestService.getOffset())

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Sms <<</////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Mail >>>/////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def mail = {
    checkAccess(15)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=15
    hsRes.admin = session.admin

    return hsRes
  }
  def mailing_templatelist={
    checkAccess(15)
    requestService.init(this)	
    //def hsRes=requestService.getContextAndDictionary(true)
    //hsRes.action_id=15
    //hsRes.admin = session.admin
	def hsRes=[:]
	hsRes+=requestService.getParams(['type_id'],null,['name'])
	hsRes.inrequest.modstatus=requestService.getLongDef('modstatus',-1)
	
	def oMailing_template=new Mailing_template()
	
	hsRes+=oMailing_template.csiFindTemplate(hsRes.inrequest.template_id?:0,hsRes.inrequest.type_id?:0,hsRes.inrequest.modstatus,hsRes.inrequest.name?:'',20,requestService.getOffset())  
    return hsRes
  }    
  def mailing_historylist={
    checkAccess(15)
    requestService.init(this)
	def hsRes=[:]
	hsRes+=requestService.getParams(null,null,['contact','name'])
    
    def oMailing_history=new Mailing_history()
	
    hsRes+=oMailing_history.csiFindHistory(hsRes.inrequest.name?:'',hsRes.inrequest.contact?:'',20,requestService.getOffset())
	return hsRes
  }  
  
  def task_list={
    checkAccess(15)
    requestService.init(this)
	def hsRes=[:]
	hsRes+=requestService.getParams(null,null,['contact','name'])
    hsRes.count=Mailing_task.findAll('FROM Mailing_task').size()
    hsRes.records=Mailing_task.findAll('FROM Mailing_task ORDER BY id DESC',[max:20,offset:requestService.getOffset()])

	return hsRes
  }  
  
  def mailing_templateadd = {    
    checkAccess(15)     
  }
  def save_mailing_template={    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams(['type_id'],null,['name'])
    
    if(!hsRes.inrequest?.name){
	  flash.error=1
      redirect(action:'mailing_templateadd')
      return
    }      

    def oMailing_template = new Mailing_template()
	oMailing_template.name=hsRes.inrequest.name
	oMailing_template.type_id=hsRes.inrequest.type_id
	oMailing_template.header=''
	oMailing_template.mtext=''
	oMailing_template.modstatus=0 
        oMailing_template.is_destemp=0	
    
    if(!oMailing_template.save(flush:true)) {
      log.debug(" Error on save Mailing_template:")
      oMailing_template.errors.each{log.debug(it)}
    }   
	
    redirect(action:'mailing_templateedit',params:[id:oMailing_template.id,type:((hsRes.inrequest.type_id?:0)==1)?1:0])
    return
  }
  def mailing_templateedit = {
    checkAccess(15)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=15
    hsRes.admin = session.admin
	
    def lId=requestService.getLongDef('id',0)
    def bType=requestService.getLongDef('type',0)//1-email
   
    hsRes.mt = Mailing_template.get(lId)   
      
    if(hsRes.mt){
      def bSave=requestService.getLongDef('save',0)
      if(!bSave)
        hsRes.inrequest=hsRes.mt
      else {
        flash.save_error=[]
        hsRes+=requestService.getParams(['modstatus','is_destemp'],[],['name','mtext_fck','mtext','header'])
		
	if(!hsRes.inrequest.name)
	  flash.save_error<<1
	if(!((hsRes.inrequest.mtext_fck)||(hsRes.inrequest.mtext)))
	  flash.save_error<<2
	if(!flash.error){
          hsRes.mt.modstatus = hsRes.inrequest.modstatus?:0
          hsRes.mt.name = hsRes.inrequest.name?:''
          hsRes.mt.mtext = (bType==1)?(hsRes.inrequest.mtext_fck?:''):(hsRes.inrequest.mtext?:'')
          hsRes.mt.header = hsRes.inrequest.header?:''
	  hsRes.mt.is_destemp = hsRes.inrequest.is_destemp?:0
        
          if(!hsRes.mt.save(flush:true)) {
            log.debug(" Error on save infotext:")
            hsRes.mt.errors.each{log.debug(it)}
            flash.save_error<<101
          }		
          hsRes.inrequest=hsRes.mt
	}
      }     
    } else {
      redirect(action:'index')
      return
    }
    hsRes.type=bType
	hsRes.queue=Mailing_queue.findAll('FROM Mailing_queue WHERE modstatus!=1 AND template_id=:template_id ORDER BY id DESC',[template_id:lId.toInteger()])
    return hsRes
  }  
  def csvfileadd={
    checkAccess(15)
	requestService.init(this)
	def lTemplate_id=requestService.getIntDef('template_id',0)
    def fileCSV= request.getFile('file')
	flash.save_error=[]
	
	//fileCSV.inputStream.splitEachLine(";") {fields ->	
	fileCSV.inputStream.getText('windows-1251').splitEachLine(";") {fields ->
	  if((fields[1]?:'').trim().size()>0){
        if(Mailing_queue.findWhere(contact:fields[1],template_id:lTemplate_id)){	        
	      //flash.save_error<<3
	    }else{	    
	      def oMailing_queue=new Mailing_queue()
          oMailing_queue.contact=fields[1]?:''
	      oMailing_queue.name=fields[0]?:''
	      oMailing_queue.template_id=lTemplate_id
	      oMailing_queue.modstatus=0
	      try{
            if(!oMailing_queue.save(flush:true)) {
              log.debug(" Error on save Mailing_queue:")
              oMailing_queue.errors.each{log.debug(it)}
              flash.save_error<<101
            }	  
	      }catch(Exception e) {
	  	    log.debug('Error on save Mailing_queue: '+e.toString())    	
          }
	    }
	  }
	}
    redirect(action:'mailing_templateedit',params:[id:lTemplate_id,type:requestService.getLongDef('type',0)])	
  }
  def delqueue={
    checkAccess(15)
	requestService.init(this)
	def lTemplate_id=requestService.getIntDef('template_id',0)
	def lsMailing_queue=Mailing_queue.findAllWhere(template_id:lTemplate_id)
	for(oMailing_queue in lsMailing_queue){
	  try{
	    oMailing_queue.delete(flush:true)
	  }catch(Exception e){
	    log.debug('Can not delete Mailing_queue '+e.toString()+' in delqueue')
      }	  
	}

	render(contentType:"application/json"){[error:false]}
  }
  def delqueueelem={
    checkAccess(15)
	requestService.init(this)
	
	def lTemplate_id=requestService.getIntDef('template_id',0)//not necessary is for safety???
	def lId=requestService.getIntDef('id',0)	
	def oMailing_queue=Mailing_queue.findWhere(template_id:lTemplate_id,id:lId)
	if(oMailing_queue){	
	  try{
	    oMailing_queue.delete(flush:true)
	  }catch(Exception e){
	    log.debug('Can not delete Mailing_queue '+e.toString()+' in delqueueelem')
      }	  
	}
	render(contentType:"application/json"){[error:false]}
  }

  def addqueueelem={
    checkAccess(15)
    requestService.init(this)

    def lTemplate_id=requestService.getIntDef('template_id',0)//not necessary is for safety???
    def sContact=requestService.getStr('contact')	
    def sName=requestService.getStr('name')
    if(Mailing_queue.findWhere(contact:sContact,template_id:lTemplate_id)){
	      //flash.save_error<<3
    }else if(lTemplate_id>0 && sContact){
      def oMailing_queue=new Mailing_queue()
      oMailing_queue.contact=sContact
      oMailing_queue.name=sName?:''
      oMailing_queue.template_id=lTemplate_id
      oMailing_queue.modstatus=0
      try{
        if(!oMailing_queue.save(flush:true)) {
          log.debug(" Error on save Mailing_queue:")
          oMailing_queue.errors.each{log.debug(it)}
          flash.save_error<<101
        }
      }catch(Exception e) {
        log.debug('Error on save Mailing_queue: '+e.toString())
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  def addqueueelemlist={
    checkAccess(15)
    requestService.init(this)

    def lTemplate_id = requestService.getIntDef('id',0)//not necessary is for safety???
    def iType = requestService.getLongDef('type',0)//not necessary is for safety???

    def lsUsers

    if(iType) lsUsers = User.findAll('from User where is_news=1 and modstatus=1 and client_id!=0 and 0=ifnull(ref_id,0)')
    else lsUsers = User.findAll('from User where is_news=1 and modstatus=1 and client_id=0 and 0=ifnull(ref_id,0)')

    lsUsers.each { user ->
      if(Mailing_queue.findWhere(contact:user.email,template_id:lTemplate_id)){
        //flash.save_error<<3
      }else if(lTemplate_id>0 && user.email){
        def oMailing_queue=new Mailing_queue()
        oMailing_queue.contact=user.email
        oMailing_queue.name=user.nickname?:''
        oMailing_queue.template_id=lTemplate_id
        oMailing_queue.modstatus=0
        try{
          if(!oMailing_queue.save(flush:true)) {
            log.debug(" Error on save Mailing_queue:")
            oMailing_queue.errors.each{log.debug(it)}
            flash.save_error<<101
          }
        }catch(Exception e) {
          log.debug('Error on save Mailing_queue: '+e.toString())
        }
      }
    }

    render(contentType:"application/json"){[error:false]}
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////Mail <<<////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Mbox<<<////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  def mbox={
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin    
    
    def fromDetails = requestService.getIntDef('fromDetails',0)
    if (fromDetails){
      hsRes.inrequest = session.lastRequest
			session.lastRequest.fromDetails = 1
      try {
        if (hsRes.inrequest.date_start)
          hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
        if (hsRes.inrequest.date_end)
          hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
      } catch(Exception e) {}
    }else{
      hsRes.inrequest =[:]
      hsRes.inrequest.owner_id = requestService.getLongDef("owner_id",0)
      hsRes.inrequest.user_id = requestService.getLongDef("user_id",0)
    }
    hsRes.automoderate = Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)

    return hsRes
  }

  def mboxlist = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin

    if (session.lastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['typeid','is_approved','modstatus','order','is_stat','offset'],
                                      ['id','home_id','owner_id','user_id'],[],['date_start','date_end'])
      hsRes.inrequest.is_answer = requestService.getIntDef('is_answer',0)
      hsRes.inrequest.is_read = requestService.getIntDef('is_read',0)
      
      if(!hsRes.inrequest.owner_id && requestService.getStr('owner_id')){        
        hsRes.inrequest.owner_id_str=requestService.getStr('owner_id')        
      }        
      if(!hsRes.inrequest.user_id && requestService.getStr('user_id')){      
        hsRes.inrequest.user_id_str=requestService.getStr('user_id')        
      }
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    def oMbox = new MboxAdminSearch()
    if(hsRes.inrequest.is_stat?:0){
      
      hsRes.userfrom_id=oMbox.getMboxList(hsRes.inrequest.id?:0,hsRes.inrequest.home_id?:0,hsRes.inrequest.user_id?:0,
                              hsRes.inrequest.modstatus?:0,hsRes.inrequest.typeid?:0,hsRes.inrequest.is_answer?:0,0,
                              hsRes.inrequest.is_read?:0,hsRes.inrequest.date_start?:'',hsRes.inrequest.date_end?:'',1,
                              hsRes.inrequest.order?:0,0,0,0l,-2,0,hsRes.admin.regions,hsRes.inrequest.owner_id_str,hsRes.inrequest.user_id_str).records.size()

      hsRes.userto_id=oMbox.getMboxList(hsRes.inrequest.id?:0,hsRes.inrequest.home_id?:0,hsRes.inrequest.user_id?:0,
                              hsRes.inrequest.modstatus?:0,hsRes.inrequest.typeid?:0,hsRes.inrequest.is_answer?:0,0,
                              hsRes.inrequest.is_read?:0,hsRes.inrequest.date_start?:'',hsRes.inrequest.date_end?:'',2,
                              hsRes.inrequest.order?:0,0,0,0l,-2,0,hsRes.admin.regions,hsRes.inrequest.owner_id_str,hsRes.inrequest.user_id_str).records.size()

      def lsMboxModstatus=Mboxmodstatus.list()
      
      hsRes.id_in_mstat=[]
      def i=0
      for(mstat in lsMboxModstatus){
        hsRes.id_in_mstat[i]=[id:mstat?.id,name:mstat?.name,count:0]
        hsRes.id_in_mstat[i].count=oMbox.getMboxList(hsRes.inrequest.id?:0,hsRes.inrequest.home_id?:0,hsRes.inrequest.user_id?:0,
                              hsRes.inrequest.modstatus?:0,hsRes.inrequest.typeid?:0,hsRes.inrequest.is_answer?:0,0,
                              hsRes.inrequest.is_read?:0,hsRes.inrequest.date_start?:'',hsRes.inrequest.date_end?:'',mstat?.id+3,
                              hsRes.inrequest.order?:0,0,0,0l,-2,0,hsRes.admin.regions,hsRes.inrequest.owner_id_str,hsRes.inrequest.user_id_str).records.size()               
        i++
      }                             
    }else
      hsRes+=oMbox.getMboxList(hsRes.inrequest.id?:0,hsRes.inrequest.home_id?:0,hsRes.inrequest.user_id?:0,
                              hsRes.inrequest.modstatus?:0,hsRes.inrequest.typeid?:0,hsRes.inrequest.is_answer?:0,0,
                              hsRes.inrequest.is_read?:0,hsRes.inrequest.date_start?:'',hsRes.inrequest.date_end?:'',0,
                              hsRes.inrequest.order?:0,20,hsRes.inrequest.offset?:0,
                              hsRes.inrequest.owner_id?:0l,hsRes.inrequest.is_approved?:0,0,hsRes.admin.regions,hsRes.inrequest.owner_id_str,hsRes.inrequest.user_id_str)
                          

    return hsRes
  }

  def setautomoderate = {
    if(!checkAccess(16)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin

    Dynconfig.findByName('mbox.noContactMode')?.updateValue(requestService.getIntDef('id',0)).save(flush:true)

    render(contentType:"application/json"){[error:false]}
  }

  def mboxDetails = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin
    def lId = requestService.getIntDef('id',0)

    hsRes.mbox = Mbox.get(lId)?.adminread()
    
    if (!hsRes.mbox) {
      redirect(action:'index')
      return
    }
    hsRes.home = Home.get(hsRes.mbox.home_id)
    hsRes.ownerUser = User.findByClient_id(hsRes.home.client_id)
    hsRes.client = User.get(hsRes.mbox.user_id)
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.homeimageurl = ConfigurationHolder.config.urlphoto

    return hsRes
  }
  
  def mboxComment = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin    
    Mbox.get(requestService.getLongDef('id',0))?.commentadd(requestService.getStr('comment'))?.save()
    
    render(contentType:"application/json"){[error:false]}
  }

  def mboxreclist = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin
    def lId = requestService.getIntDef('id',0)
    hsRes.mboxrec = Mboxrec.findAllByMbox_id(lId,[sort:'id',order:'desc'])

    return hsRes
  }

  def rectextedit = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin

    def lId = requestService.getLongDef('mboxrec_id',0)
    def sText = requestService.getStr('rectext')
    def oMboxrec = Mboxrec.get(lId)
    if (oMboxrec&&sText) {
      def oMbox = Mbox.get(oMboxrec.mbox_id)
      if(oMbox?.mtext==oMboxrec.rectext){
        oMbox.mtext = sText?:oMboxrec.rectext
        oMbox.save(flush:true)
      }
      oMboxrec.rectext = sText?:oMboxrec.rectext
      oMboxrec.save(flush:true)
    }

    render(contentType:"application/json"){[error:false]}
  }

  def mboxrecapprove = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin

    def lId = requestService.getLongDef('id',0)
    def iStatus = requestService.getIntDef('status',0)
    def oMboxrec = Mboxrec.get(lId)
    if (oMboxrec) {
      def oMbox = Mbox.get(oMboxrec.mbox_id)
      if(iStatus==1&&!Mboxrec.findAllByMbox_idAndIs_approvedAndAdmin_idAndIdGreaterThanAndIs_answer(oMboxrec.mbox_id,1,0,oMboxrec.id,oMboxrec.is_answer)&&!oMboxrec.admin_id){
        oMbox.(oMboxrec.is_answer?'mtext':'mtextowner') = oMboxrec.rectext
        oMbox.is_approved = 1
        oMbox.is_read = 0
        oMbox.is_answer = oMboxrec.is_answer
        oMbox.answertype_id = oMboxrec.answertype_id?:oMbox.answertype_id
        if(oMbox.modstatus in [1,2,5,6] && oMboxrec.answertype_id in 1..2){
          oMbox.modstatus = 3
          def oUser = User.get(oMbox.user_id)
          if (oUser?.tel) smsService.sendOfferSMS(oUser)
          mailerService.sendBronInvitationMail(oMboxrec,oMbox,billingService.getBronPrice(oMbox))
        } else if(oMbox.modstatus in [1,2,6]){
          oMbox.modstatus = 1
        }
        if (oMbox.isDirty('is_approved')) {
          oMbox.moddate = new Date()
          mailerService.sendMboxFirstMails(oMbox,oMbox.user_id,hsRes.context,true,false)
          if (Tools.getIntVal(ConfigurationHolder.config.noticeSMS.active,1)&&User.findByClient_id(oMbox.homeowner_cl_id)?.is_noticeSMS) {
            try {
              smsService.sendNoticeSMS(User.findByClient_id(oMbox.homeowner_cl_id),Home.get(oMbox.home_id)?.region_id?:0)
            } catch(Exception e) {
              log.debug("Cannot sent sms \n"+e.toString()+'in home/addmbox')
            }
          }
          //GCM>>
          def oClientGCM = Client.get(oMbox.homeowner_cl_id)
          oClientGCM = oClientGCM.parent?Client.get(oClientGCM.parent):oClientGCM
          def msg_unread_count = Mbox.executeQuery('select count(*) from Mbox where (((homeowner_cl_id=:cl_id and is_readowner=0)or(user_id=:u_id and is_readclient=0)) OR (is_read=0 and ((homeowner_cl_id=:cl_id and is_answer=0 and is_approved=1) or (user_id=:u_id and is_answer=1)))) and not(modstatus=6 or ((homeowner_cl_id=:cl_id and IFNULL(is_owfav,0)=-1)or(user_id=:u_id and IFNULL(is_clfav,0)=-1)))',[cl_id:oClientGCM?.id,u_id:0.toLong()])[0]
          def sendGCM = [:]
          sendGCM.message = 'letter'
          sendGCM.msgcnt = msg_unread_count.toString()

          def lsDevices = []

          def lsUsers=User.findAllWhere(client_id:oClientGCM?.id)
          def user_ids=[]
          for(user in lsUsers)
            user_ids<<user.id

          lsDevices=Device.findAll("FROM Device WHERE user_id IN (:user_ids)",[user_ids:user_ids])

          if(lsDevices){
            def lsDevices_ids=[]

            for(device in lsDevices)
              lsDevices_ids << device.device
            if(lsDevices_ids)
              androidGcmService.sendMessage(sendGCM,lsDevices_ids,'message', grailsApplication.config.android.gcm.api.key ?: '')  //ConfigurationHolder???
          }
          //GCM<<
        } else {
          mailerService.addanswermail(oMbox,oMboxrec,hsRes.context)
        }
        oMbox.save(flush:true)
      } else if (iStatus==0) {
        def oPrevMboxrec = Mboxrec.findAll(sort:'id',order:'desc',max:1){ mbox_id == oMboxrec.mbox_id && id != oMboxrec.id && (( is_answer == oMboxrec.is_answer && is_approved == 1 ) || ( admin_id == 0 && is_answer == (oMboxrec.is_answer?0:1) ))}[0]
        oMbox.(oMboxrec.is_answer?'mtext':'mtextowner') = oPrevMboxrec?.rectext?:''
        oMbox.is_answer = oPrevMboxrec?.is_answer?:0
        oMbox.save(flush:true)
      }
      oMboxrec.is_approved = iStatus?:0
      oMboxrec.save(flush:true)
    }

    render(contentType:"application/json"){[error:false]}
  }

  def mboxapprove = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin

    def lId = requestService.getLongDef('id',0)
    def iStatus = requestService.getIntDef('status',0)
    def oMbox = Mbox.get(lId)
    if (oMbox) {
      oMbox.is_approved = iStatus?:0
      oMbox.save(flush:true)
    }

    render(contentType:"application/json"){[error:false]}
  }

  def mboxrecadd = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 16
    hsRes.admin = session.admin

    def lId = requestService.getLongDef('id',0)
    def sText = requestService.getStr('rectext')
    def iAnswer = requestService.getIntDef('is_answer',0)
    def oMbox = Mbox.get(lId)
    if (oMbox&&sText) {
      def oMboxrec = new Mboxrec().setmboxData(oMbox)
      oMboxrec.rectext = sText?:''
      oMboxrec.is_answer = iAnswer%2
      oMboxrec.admin_id = hsRes.admin.id
      oMboxrec.is_system = iAnswer>1?1:0
      oMboxrec.answertype_id = iAnswer%2?6:0
      oMboxrec.save(flush:true)
      oMbox.(iAnswer%2?'is_readclient':'is_readowner') = 0
      oMbox.(iAnswer%2?'mtext':'mtextowner') = sText
      oMbox.save(flush:true)
      mailerService.addanswermail(oMbox,oMboxrec,hsRes.context)
    }

    render(contentType:"application/json"){[error:false]}
  }

  def movetoprop = {
    checkAccess(16)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=16
    hsRes.admin = session.admin
    def lId=requestService.getLongDef('id',0)

    hsRes.mbox = Mbox.get(lId)
    if (!hsRes.mbox){
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      if(!Zayavka.findAllByUser_idAndInputdateGreaterThan(hsRes.mbox.user_id,new Date()-1)){
        zayavkaService.createZayavkaFromMbox(hsRes.mbox)
        def oUser = User.get(hsRes.mbox.user_id)
        if (oUser?.email){
          def oHome = Home.get(hsRes.mbox.home_id)
          (new Zayavkabyemail(oUser.email,oUser.nickname,oHome.city?:Region.get(oHome.region_id)?.name,hsRes.mbox.mtext,hsRes.mbox.date_start,hsRes.mbox.date_end,'#proposalAdmin')).save(flush:true)
        }
      }
    } catch(Exception e) {
      log.debug('error on add Zayavka. in Admin:movetoprop\n'+ e.toString())
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Mbox<<<////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Article>>>/////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def article={
    checkAccess(17)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 17
    hsRes.admin = session.admin

    def fromDetails = requestService.getIntDef('fromDetails',0)
    if (fromDetails){
      session.lastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.lastRequest
      try {
        hsRes.inrequest.inputdate=Date.parse(DATE_FORMAT, hsRes.inrequest?.inputdate)
      } catch(Exception e) {}
    }

    return hsRes
  }

  def articlelist = {
    checkAccess(17)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 17
    hsRes.admin = session.admin

    if (session.lastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['id'],null,['title'],['inputdate'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    hsRes.imageurl = ConfigurationHolder.config.urlarticlesphoto
    def oArticle = new Articles()
    hsRes+=oArticle.getArticleList(hsRes.inrequest.id?:0,hsRes.inrequest.modstatus?:0,hsRes.inrequest.title?:'',hsRes.inrequest.inputdate?:'',20,requestService.getOffset())

    return hsRes
  }

  def articleDetails = {
    checkAccess(17)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)    
    hsRes.action_id=17
    hsRes.admin = session.admin

    hsRes.id=requestService.getIntDef('id',0)
    hsRes.save=requestService.getIntDef('save',0)

    imageService.init(this,'articlephotopic','articlephotokeeppic',ConfigurationHolder.config.pathtophoto+'articles'+File.separatorChar) // 0
    if (!hsRes.save)
      imageService.finalizeFileSession(['file1'])
    imageService.startFileSession() // 1

    if(hsRes.save) {
      hsRes+=requestService.getParams(null,null,['title','shortdescription','atext'])
      hsRes.inrequest.tags = Articles.get(hsRes.id)?.tags
    } else {
      hsRes.inrequest = Articles.get(hsRes.id)
    }
    if((Articles.get(hsRes.id)?.picture?:'')!='')
      imageService.putIntoSessionFromDb(Articles.get(hsRes.id)?.picture,'file1') // 2
    def hsPics=imageService.getSessionPics('file1') // 3
    if(hsPics!=null){
      hsRes.inrequest.picture=hsPics.photo
    }
    hsRes.imageurl = ConfigurationHolder.config.urlarticlesphoto

    return hsRes
  }

  def savearticlephoto={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)

    imageService.init(this,'articlephotopic','articlephotokeeppic',ConfigurationHolder.config.pathtophoto+'articles'+File.separatorChar) // 0
    def iNo=1

    //ÇÀÃÐÓÆÀÅÌ ÃÐÀÔÈÊÓ
    def hsData= imageService.loadPicture(
      "file1",
      Tools.getIntVal(ConfigurationHolder.config.photo.weight,2097152), //weight
      Tools.getIntVal(ConfigurationHolder.config.articlephoto.image.size,710),  // size
      Tools.getIntVal(ConfigurationHolder.config.articlephoto.thumb.size,220), //thumb size
      true,//SaveThumb
      false,//square
      Tools.getIntVal(ConfigurationHolder.config.articlephoto.image.height,425),//height
      Tools.getIntVal(ConfigurationHolder.config.articlephoto.thumb.height,160),//thumb height
      false,
      false
    ) // 3

    hsData['num']=1 //<- ÍÅÎÁÕÎÄÈÌÎ ÄËß ÊÎÐÐÅÊÒÍÎÉ ÐÀÁÎÒÛ js â savepictureresult

    // savepictureresult ÎÁÙÈÉ ØÀÁËÎÍ, ÅÑËÈ ÈÑÏÎËÜÇÎÂÀÒÜ ÑÊÐÈÏÒÛ ÀÍÀËÎÃÈ×ÍÎ ÑÄÅËÀÍÍÎÌÓ
    render(view:'savepictureresult',model:hsData)
    return
  }

  def deletearticlephoto={
    //TODO: check user logged in
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)  

    //ÎÁßÇÀÒÅËÜÍÀß ÈÍÈÖÈÀËÈÇÀÖÈß TODO: path into cfg
    imageService.init(this,'articlephotopic','articlephotokeeppic',ConfigurationHolder.config.pathtophoto+'articles'+File.separatorChar)
    
    def sName=requestService.getStr("name")
    imageService.deletePicture(sName)//4
    render(contentType:"application/json"){[error:false]}
  }

  def articlesave = {
    checkAccess(17)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=17
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(['id'],null,['title','shortdescription','atext','author','googleplus_id','ceo_title','ceo_keywords','ceo_description','newtag'])
    hsRes.tags = requestService.getList('tags')
    hsRes.inrequest.save = 1
    flash.error = []
    if (!hsRes.inrequest.title) {
      flash.error << 1
    }

    if(hsRes.inrequest.id && !flash.error) {
      hsRes.article = Articles.get(hsRes.inrequest.id)
    } else {
      hsRes.article = new Articles()
    }
    if (hsRes.article && !flash.error) {
      try {
        hsRes.article.title = hsRes.inrequest.title
        hsRes.article.linkname = Articles.getUniqueLinkname(hsRes.inrequest.title,hsRes.inrequest.id?:0)
        hsRes.article.shortdescription = hsRes.inrequest.shortdescription?:''
        hsRes.article.atext = hsRes.inrequest.atext?:''
        hsRes.article.author = hsRes.inrequest.author?:''
        hsRes.article.googleplus_id = hsRes.inrequest.googleplus_id?:''
        hsRes.article.ceo_title = hsRes.inrequest.ceo_title?:''
        hsRes.article.ceo_keywords = hsRes.inrequest.ceo_keywords?:''
        hsRes.article.ceo_description = hsRes.inrequest.ceo_description?:''

        imageService.init(this,'articlephotopic','articlephotokeeppic',ConfigurationHolder.config.pathtophoto+'articles'+File.separatorChar) // 0
        def hsPics=imageService.getSessionPics('file1')
        if((hsRes.article.picture?:'')!=''&&!hsPics)
          imageService.putIntoSessionFromDb(hsRes.article.picture,'file1') // 2
        imageService.finalizeFileSession(['file1'])
        hsRes.article.picture=(hsPics?.photo)?:hsRes.article.picture?:''

        hsRes.article.save(flush:true)
        def lsOldTags = hsRes.article.tags.collect{it.id} - hsRes.tags
        def lsNewTags = (hsRes.tags?:[]) - hsRes.article.tags.collect{it.id}
        lsOldTags.each{hsRes.article.removeFromTags(Articles_tags.get(it))}
        lsNewTags.each{Articles_tags.get(it)?hsRes.article.addToTags(Articles_tags.get(it)):null}
        if(hsRes.inrequest.newtag&&!Articles_tags.findByName(Tools.transliterate(hsRes.inrequest.newtag))){
          hsRes.article.addToTags(new Articles_tags(name:Tools.transliterate(hsRes.inrequest.newtag)).save(flush:true,failOnError:true))
        } else if(hsRes.inrequest.newtag&&Articles_tags.findByName(Tools.transliterate(hsRes.inrequest.newtag))){
          hsRes.article.addToTags(Articles_tags.findByName(Tools.transliterate(hsRes.inrequest.newtag)))
        }
        flash.id = hsRes.article.id
      } catch(Exception e) {
        flash.error << 101
        log.debug('Administrators:articlesave. Error on save article:'+hsRes.inrequest.id+'\n'+e.toString())
      }
    }

    render flash as JSON
    return
  }

  def togglearticlestatus={
    checkAccess(17)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 17
    hsRes.admin = session.admin

    Articles.get(requestService.getIntDef('id',0))?.toggleModstatus()

    render(contentType:"application/json"){[error:false]}
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Article<<</////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Clients>>>/////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def clients={
    checkAccess(18)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 18
    hsRes.admin = session.admin

    def fromDetails = requestService.getIntDef('fromDetails',0)
    if (fromDetails){
      session.lastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.lastRequest
    }
    hsRes.country = Country.findAllByIs_reserve(1)

    return hsRes
  }

  def clientlist = {
    checkAccess(18)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.action_id = 18
    hsRes.admin = session.admin

    if (session.lastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['country_id','type_id'],['id'],['name','nickname'])
      hsRes.inrequest.resstatus = requestService.getIntDef('resstatus',0)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    def oClient = new ClientSearch()
    hsRes+=oClient.csiSelectClients(hsRes.inrequest?.id?:0,hsRes.inrequest?.country_id?:0,hsRes.inrequest?.resstatus,hsRes.inrequest?.nickname?:'',hsRes.inrequest?.name?:'',hsRes.inrequest?.type_id?:0,20,requestService.getOffset())

    return hsRes
  }

 def clientDetails = {
    checkAccess(18)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=18
    hsRes.admin = session.admin

    hsRes.id=requestService.getIntDef('id',0)
    hsRes.client = Client.get(hsRes.id)
    if (!hsRes.client) {
      response.sendError(404)
      return
    }
    hsRes.clienthistory=Clienthistory.findAll("FROM Clienthistory WHERE client_id=:client_id ORDER BY moddate DESC",[client_id:hsRes.client.id])

    hsRes.user = User.findByClient_id(hsRes.client.id)
    hsRes.valuta = Valuta.findAllByModstatus(1)
    hsRes.country = Country.findAllByIs_reserve(1)
    hsRes.reserve = Reserve.findAllByModstatus(1)
    hsRes.paycountry = Paycountry.findAllByCountry_id(hsRes.client?.country_id?:0)
    hsRes.urlscanpassport = ConfigurationHolder.config.urlscanpassport
    hsRes.urlscandogovor = ConfigurationHolder.config.urlscandogovor

    return hsRes
  }

  def payaccounts = {
    checkAccess(18)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=18
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(['countryId'])
    hsRes.paycountry = Paycountry.findAllByCountry_id(hsRes.inrequest?.countryId?:0)

    return hsRes
  }

  def clientsave = {
    checkAccess(18)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=18
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(['country_id','payaccount_id','reserve_id','is_transferauto','resstatus','type_id','nds'],['id'],
    ['inn','ogrn','license','comment','settlaccount','webmoney','payee','kpp','bankname','bik','corraccount','settlaccount','pcard','paycomment','settlprocedure'])
    def lsDirectory = requestService.getParams(['payaccount_id','reserve_id','country_id'])
    flash.error = []
    if(lsDirectory?.size()<3)
      flash.error<<1
    if(hsRes.inrequest.id&&!flash.error) {
      hsRes.client = Client.get(hsRes.inrequest.id)
    } else {
      flash.error << 1
    }

    if (hsRes.client) {
      try {
        hsRes.client.payoutDetailsModerate(hsRes.inrequest)
        def oClienthistory=new Clienthistory()
        oClienthistory.setData(hsRes.client,0)
        if (hsRes.inrequest.resstatus==1)
          mailerService.sendConfirmPayoutMail(hsRes.client)
      } catch(Exception e) {
        flash.error << 101
        log.debug('Administrators:clientsave. Error on save client:'+hsRes.inrequest.id+'\n'+e.toString())
      }
    }

    render flash as JSON
    return
  }

  def savescanpassport={
    checkAccess(18)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=18
    hsRes.admin = session.admin

    hsRes.id=requestService.getIntDef('id',0)
    hsRes.client = Client.get(hsRes.id)

    imageService.init(this,'scanpassphotopic','scanpassphotokeeppic',ConfigurationHolder.config.pathtophoto+'scanpass'+File.separatorChar) // 0
    //ÇÀÃÐÓÆÀÅÌ ÃÐÀÔÈÊÓ
    def hsData= imageService.rawUpload(
      "file1",
      Tools.getIntVal(ConfigurationHolder.config.photo.weight,2097152), //weight
    ) // 3

    hsData['num']=1 //<- ÍÅÎÁÕÎÄÈÌÎ ÄËß ÊÎÐÐÅÊÒÍÎÉ ÐÀÁÎÒÛ js â savepictureresult

    if (hsRes.client&&!hsData.error) {
      try {
        hsRes.client.savescanpassport(hsData)
      } catch(Exception e) {
        hsData.error = 5
        log.debug('Administrators:savescanpassport. Error on save client:'+hsRes.inrequest.id+'\n'+e.toString())
      }
    } else if (!hsData.error) {
      hsData.error = 6
    }

    render(view:'savepictureresult',model:hsData)
    return
  }

  def savescandogovor={
    checkAccess(18)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=18
    hsRes.admin = session.admin

    hsRes.id=requestService.getIntDef('id',0)
    hsRes.client = Client.get(hsRes.id)

    imageService.init(this,'scandogphotopic','scandogphotokeeppic',ConfigurationHolder.config.pathtophoto+'scandog'+File.separatorChar) // 0
    //ÇÀÃÐÓÆÀÅÌ ÃÐÀÔÈÊÓ
    def hsData= imageService.rawUpload(
      "file1",
      Tools.getIntVal(ConfigurationHolder.config.photo.weight,2097152), //weight
    ) // 3

    hsData['num']=2 //<- ÍÅÎÁÕÎÄÈÌÎ ÄËß ÊÎÐÐÅÊÒÍÎÉ ÐÀÁÎÒÛ js â savepictureresult

    if (hsRes.client&&!hsData.error) {
      try {
        hsRes.client.savescandogovor(hsData)
      } catch(Exception e) {
        hsData.error = 5
        log.debug('Administrators:savescandogovor. Error on save client:'+hsRes.inrequest.id+'\n'+e.toString())
      }
    } else if (!hsData.error) {
      hsData.error = 6
    }

    render(view:'savepictureresult',model:hsData)
    return
  }

  def deletescan={
    checkAccess(18)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=18
    hsRes.admin = session.admin

    hsRes.id=requestService.getIntDef('id',0)
    hsRes.type=requestService.getIntDef('type',0)
    hsRes.client = Client.get(hsRes.id)
    if (hsRes.client&&hsRes.type) {
      //ÎÁßÇÀÒÅËÜÍÀß ÈÍÈÖÈÀËÈÇÀÖÈß TODO: path into cfg
      try {
        if(hsRes.type==1) {
          imageService.init(this,'scanpassphotopic','scanpassphotokeeppic',ConfigurationHolder.config.pathtophoto+'scanpass'+File.separatorChar)
          imageService.rawDeletePictureFiles(hsRes.client.scanpassport)//4
          hsRes.client.savescanpassport([:])
        } else if(hsRes.type==2) {
          imageService.init(this,'scandogphotopic','scandogphotokeeppic',ConfigurationHolder.config.pathtophoto+'scandog'+File.separatorChar)
          imageService.rawDeletePictureFiles(hsRes.client.scandogovor)//4
          hsRes.client.savescandogovor([:])
        }
      } catch(Exception e) {
        log.debug('Administrators:deletescan. Error on save client:'+hsRes.inrequest.id+'\n'+e.toString())
      }
    }
    render(contentType:"application/json"){[error:false]}
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Clients<<</////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def selection = {    
    checkAccess(20)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=20
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(['city_id','region_id','country_id'])    

    hsRes.country=Country.findAll('FROM Country ORDER BY regorder')
    if(hsRes.inrequest?.country_id?:0){
      hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
    } else if((hsRes.country?:[]).size()>0)
  	  hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])    
    if(hsRes.inrequest.region_id)    
      hsRes.city=City.findAll('FROM City WHERE region_id=:region_id ORDER BY name',[region_id:hsRes.inrequest.region_id])
    else if((hsRes.region?:[]).size()>0)
      hsRes.city=City.findAll('FROM City WHERE region_id=:region_id ORDER BY name',[region_id:hsRes.region[0].id])    
      
    hsRes.modstatus=Homemodstatus.findAll('FROM Homemodstatus ORDER BY modstatus')
    def fromModer = requestService.getIntDef('fromModer',0)
    if (fromModer){
      session.lastRequest.fromModer = fromModer
      hsRes.inrequest = session.lastRequest
    }
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////////
  def selectionlist = {    
    checkAccess(20)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=20
    hsRes.admin = session.admin
    def oHomeSearchAdmin=new HomeSearchAdmin()
    if (session.lastRequest?.fromModer?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromModer = 0
    } else {
      hsRes+=requestService.getParams(['country_id', 'region_id','city_id'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-5)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }
    //hsRes.imageurl = ConfigurationHolder.config.urlphoto

    hsRes+=oHomeSearchAdmin.csiSelectHomes(0,hsRes.inrequest.country_id?:0,
                hsRes.inrequest.region_id?:0,(hsRes.inrequest.modstatus!=null)?hsRes.inrequest.modstatus:-5,
                -1,-1,
                -1,'','',
                0,'',0,0,
                0,0,'',hsRes.inrequest.city_id?:0,
                1,20,requestService.getOffset(),true)//requestService.getLongDef('order',0)
    //hsRes.modstatus=Homemodstatus.list() 

    hsRes.count=(hsRes['home.client_id']?:[]).size()
    return hsRes
  }
    ///////////////////////////////////////////////////////////////////////////////////////////  
  def selectioncsv={
    checkAccess(20)
    requestService.init(this)   
    def hsRes=[:]    
    def sCsv
    
    hsRes+=requestService.getParams(['country_id','region_id','city_id'])
    hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-5)
         
    sCsv = 'Ник,Имя,E-Mail,Телефон\n'
    def oHomeSearchAdmin=new HomeSearchAdmin()
    hsRes+=oHomeSearchAdmin.csiSelectHomes(0,hsRes.inrequest.country_id?:0,
                hsRes.inrequest.region_id?:0,(hsRes.inrequest.modstatus!=null)?hsRes.inrequest.modstatus:-5,
                -1,-1,
                -1,'','',
                0,'',0,0,
                0,0,'',hsRes.inrequest.city_id?:0,
                1,-1,0,true)
    
    for (row in hsRes.records)
        sCsv += (User.findWhere(client_id:row.client_id,is_am:1)?.nickname?:'')+','+(User.findWhere(client_id:row.client_id,is_am:1)?.firstname?:'')+','+(User.findWhere(client_id:row.client_id,is_am:1)?.email?:'')+','+(User.findWhere(client_id:row.client_id,is_am:1)?.tel?:'')+'\n'
      response.setHeader("Content-disposition", "attachment; filename=Viborka.csv")    
   
    response.contentType = 'text/csv;charset=windows-1251'
    response.outputStream << sCsv.getBytes('windows-1251');
    response.outputStream.flush()     
  }  
  def region={
    requestService.init(this)
    
    def lCountryId=requestService.getIntDef('countryId',0)    
    return [region: Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',
             [country_id:lCountryId])]
  }
  def city={
    requestService.init(this)
   
    def lRegionId=requestService.getIntDef('regionId',0)    
    return [city: City.findAll('FROM City WHERE region_id=:region_id ORDER BY name',
             [region_id:lRegionId])]
  } 
  def city_by_popdir_id={
    requestService.init(this)    
    def iPopDirId=requestService.getIntDef('popdir_id',0)
    def hsRes=[:]
    hsRes.records=[]
    if(iPopDirId){
      def lsRegions=Region.findAllWhere(popdirection_id:iPopDirId)
      def lsRegIds=[]
      for(reg in lsRegions){
        lsRegIds<<reg.id
      }
      hsRes.records=City.findAll('FROM City WHERE region_id IN (:reg_ids) ORDER BY name',[reg_ids:lsRegIds])
    }
    return hsRes
  }
  def anons = {    
    checkAccess(26)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=26
    hsRes.admin = session.admin
    
    def fromDetails = requestService.getIntDef('fromDetails',0)
    if (fromDetails){
      session.lastRequest.fromDetails =fromDetails
      hsRes.inrequest = session.lastRequest      
    } 
    
    return hsRes
  }
  def anonslist={
    checkAccess(26)
    requestService.init(this)     
    def hsRes=[:]          
    hsRes+=requestService.getParams(['anons_id'])
    hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-1) 
    hsRes.max=20

    def oNotice=new Notice()
    hsRes.data=oNotice.getNotice(hsRes.inrequest.anons_id,hsRes.inrequest.modstatus ,hsRes.max,requestService.getOffset())
    
    if (session.lastRequest?.fromDetails?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetails = 0
    } else {     
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }    
    
    return hsRes                  
  }  
  def anonsDetail={
    checkAccess(26)
    requestService.init(this)     
    def hsRes=[:]  
    hsRes.action_id=26
    hsRes.admin = session.admin    
    hsRes.imageurl = ConfigurationHolder.config.urlphoto
    hsRes+=requestService.getParams(['id'])    
    
    if(hsRes.inrequest.id){
      hsRes.inrequest=Notice.get(hsRes.inrequest.id)     
    }
    hsRes.pages=Infotext.findAllWhere(is_anons:1)
      
    return hsRes
  }
  def anonssave = {
    checkAccess(26)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id=26
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(['id','modstatus','is_index'],null,['title','description','url','page','image','title_en','description_en'])
      
    //hsRes.inrequest.page=requestService.getStringList('page').toString().replace('[','').replace(']','')
        
    hsRes.inrequest.save = 1
    flash.error = []
    if (!hsRes.inrequest.title) {
      flash.error << 1
    }
    if(flash.error){
      render flash as JSON
      return
    }        
    if(hsRes.inrequest.id) {
      hsRes.anons = Notice.get(hsRes.inrequest.id)
    } else {
      hsRes.anons = new Notice()
    }
    if (hsRes.anons) {      
      hsRes.anons.title = hsRes.inrequest?.title       
      hsRes.anons.description = hsRes.inrequest?.description?:''
      hsRes.anons.title_en = hsRes.inrequest?.title_en?:''       
      hsRes.anons.description_en = hsRes.inrequest?.description_en?:''
      hsRes.anons.url = hsRes.inrequest?.url?:''
      hsRes.anons.modstatus=hsRes.inrequest?.modstatus?:0         
      hsRes.anons.page = hsRes.inrequest?.page?:''
//log.debug('hsRes.inrequest?.image='+hsRes.inrequest?.image)
      hsRes.anons.image = hsRes.inrequest?.image?:''
      hsRes.anons.is_index = hsRes.inrequest?.is_index?:0 
 /*
      imageService.init(this,'anonsphotopic','anonsphotokeeppic',ConfigurationHolder.config.pathtophoto+File.separatorChar) // 0
      def hsPics=imageService.getSessionPics('file1')
      if((hsRes.anons.image?:'')!=''&&!hsPics)
        imageService.putIntoSessionFromDb(hsRes.anons.image,'file1') // 2
      imageService.finalizeFileSession(['file1'])
      hsRes.anons.image=(hsPics?.photo)?:hsRes.anons.image?:''
*/
      hsRes.anons.save(flush:true)
      if (!hsRes.anons.save(flush:true)){
        log.debug('error on save anons in Admin:anonssave')
        hsRes.anons.errors.each{log.debug(it)}            
      }  
    } 
    flash.id = hsRes.anons.id
    render flash as JSON    
    return
  }
  def deleteanonsphoto={
    //TODO: check user logged in
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)  
    
    imageService.init(this,'anonsphotopic','anonsphotokeeppic',ConfigurationHolder.config.pathtophoto+File.separatorChar)
    
    def sName=requestService.getStr("name")
    imageService.deletePicture(sName)//4
    render(contentType:"application/json"){[error:false]}
  }
  def saveanonsphoto={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)

    imageService.init(this,'anonsphotopic','anonsphotokeeppic',ConfigurationHolder.config.pathtophoto+File.separatorChar) // 0
    def iNo=1

    //ÇÀÃÐÓÆÀÅÌ ÃÐÀÔÈÊÓ
    def hsData= imageService.loadPicture(
      "file1",
      Tools.getIntVal(ConfigurationHolder.config.photo.weight,2097152), //weight
      Tools.getIntVal(ConfigurationHolder.config.anonsphoto.image.size,68),  // size
      Tools.getIntVal(ConfigurationHolder.config.anonsphoto.thumb.size,68), //thumb size
      false,//SaveThumb
      false,//square
      Tools.getIntVal(ConfigurationHolder.config.anonsphoto.image.height,68),//height
      Tools.getIntVal(ConfigurationHolder.config.anonsphoto.thumb.height,68),//thumb height
      false,
      false
    ) // 3

    hsData['num']=1 //<- ÍÅÎÁÕÎÄÈÌÎ ÄËß ÊÎÐÐÅÊÒÍÎÉ ÐÀÁÎÒÛ js â savepictureresult

    // savepictureresult ÎÁÙÈÉ ØÀÁËÎÍ, ÅÑËÈ ÈÑÏÎËÜÇÎÂÀÒÜ ÑÊÐÈÏÒÛ ÀÍÀËÎÃÈ×ÍÎ ÑÄÅËÀÍÍÎÌÓ
    render(view:'savepictureresult',model:hsData)
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def city_autocomplete={
    requestService.init(this)
	  def hsRes = requestService.getContextAndDictionary(true)
    def sName=requestService.getStr('city')
    def iRegId=requestService.getIntDef('region_id',0)
       
    if ((sName?:'').size()>2){
      if(sName.matches('.*(?=.*[A-Za-z]).*')){    
        hsRes.records=City.findAll('FROM City WHERE (name_en like :name1 OR name_en like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])         
        hsRes.en=1
      }  
      else
        hsRes.records=City.findAll('FROM City WHERE (name like :name1 OR name like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])                     
    }	
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def district_autocomplete={
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    def sName=requestService.getStr('district')
    def iRegId=requestService.getIntDef('region_id',0)
       
    if ((sName?:'').size()>2){
      if(sName.matches('.*(?=.*[A-Za-z]).*')){
        hsRes.records=District.findAll('FROM District WHERE (name_en like :name1 OR name_en like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])                         
        hsRes.en=1
      }else      
        hsRes.records=District.findAll('FROM District WHERE (name like :name1 OR name like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])                         
    }	
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def street_autocomplete={
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    def sName=requestService.getStr('street')
    def iRegId=requestService.getIntDef('region_id',0)
    
    if ((sName?:'').size()>2){
      if(sName.matches('.*(?=.*[A-Za-z]).*')){
        hsRes.records=Street.findAll('FROM Street WHERE (name_en like :name1 OR name_en like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])         
        hsRes.en=1
      }else
        hsRes.records=Street.findAll('FROM Street WHERE (name like :name1 OR name like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])         
    }	
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Home spec>>>/////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def shome = {    
    checkAccess(29)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=29
    hsRes.admin = session.admin

    //hsRes+=requestService.getParams(['city_id'],[],['type'])    
    def lsCity_id=[]
    def lsShomeCity_id=Shome.list().collect{it?.city_id}        
  
    for(city_id in City.list().collect{it?.id}){      
      if(lsShomeCity_id.contains(city_id) && !lsCity_id.contains(city_id))
        lsCity_id<<city_id
    }
    hsRes.city=City.findAll('FROM City WHERE id IN (:ids) ORDER BY name',[ids:lsCity_id?:[-1]])           
    hsRes.shometype=Shometype.list()
    
    def fromEdit = requestService.getIntDef('fromEdit',0)
    if (fromEdit){
      session.lastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.lastRequest
    }
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////////
  def shomelist = {    
    checkAccess(29)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=29
    hsRes.admin = session.admin
    
    if (session.lastRequest?.fromEdit?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromEdit = 0
    } else {
      hsRes+=requestService.getParams(['city_id','modstatus','type_id'])      
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }
    
    def oShome=new Shome()
    hsRes+=oShome.csiSelectSHomes(hsRes.inrequest.city_id?:0,hsRes.inrequest.type_id?:0,hsRes.inrequest.modstatus?:0,20,requestService.getOffset())        
    return hsRes
  }
  def shomeedit={
    checkAccess(29)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  	
    hsRes.action_id=29
    hsRes.admin = session.admin
    def lId=requestService.getIntDef('id',0)
    def bSave=requestService.getIntDef('save',0)
    
    if(!bSave){
      if(lId)
        hsRes.inrequest=Shome.get(lId)    
    }else{  
      hsRes+=requestService.getParams(['city_id','type_id','id'],[],
      ['header','header_en','title','title_en','keywords','keywords_en','description','description_en',
       'promotext','promotext_en','itext','itext_en','itext2','itext2_en','linkname','linkname_en'])    
        
      flash.error=[]
      if(!hsRes.inrequest?.city_id)
        flash.error<<1
      if(!hsRes.inrequest?.type_id)
        flash.error<<2 
      if(!hsRes.inrequest?.header)
        flash.error<<3
      if(!hsRes.inrequest?.header_en)
        flash.error<<4   
      if(!hsRes.inrequest?.linkname)
        flash.error<<5
      if(!hsRes.inrequest?.linkname_en)
        flash.error<<6        
      
      if(!flash.error){    
        def oShome    
        if(hsRes.inrequest?.id)
          oShome=Shome.get(hsRes.inrequest?.id?:0)
        else
          oShome=new Shome()
        
        if(oShome){
          try{
            oShome.csiSetData(hsRes.inrequest).save(failOnError:true)
          }catch(Exception e) {
            log.debug("Error save data in Admin/shomesave\n"+e.toString());
            flash.error<<100
          }
          if(!hsRes.inrequest?.id){
            hsRes.inrequest?.id=oShome.id
            redirect(action:'shomeedit',id:hsRes.inrequest?.id)
            return
          }  
        }      
      }  
    }
    
    hsRes.city=City.list([sort:'name',order:'asc'])
    hsRes.shometype=Shometype.list()
    return hsRes    
  }
  def shomestatus={
    checkAccess(29)
    requestService.init(this)
    
    def iId=requestService.getIntDef('id',0)
    def iStatus=requestService.getIntDef('status',0)
    
    def oShome=Shome.get(iId)
    if(oShome){
      oShome.modstatus=iStatus
      
      if (!oShome.save(flush:true)){
        log.debug('error on save Shome in Admin:shomestatus')
        oShome.errors.each{log.debug(it)}
      }
    }
    render(contentType:"application/json"){[error:false]}
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////Home spec<<</////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
