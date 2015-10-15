import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.converters.JSON
import java.util.regex.*
class ProfileController {
  def requestService
  def imageService  
  def mailerService
  def smsService

  def checkUser(hsRes) {   
    if(!hsRes?.user){	  	                       
      redirect(controller:'index', action:'index', base:hsRes.context.mainserverURL_lang)
      return false;
    }
	def oTemp_notification=Temp_notification.findWhere(id:1,status:1)	  
	session.attention_message=oTemp_notification?oTemp_notification.text:null
	return true
  }
  def init(hsRes){   
    def hsTmp=findClientId(hsRes)
    hsTmp.imageurl = ConfigurationHolder.config.urlphoto + 'user/'
	  hsTmp.textlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
	  hsTmp.stringlimit = Tools.getIntVal(ConfigurationHolder.config.smalltext.limit,220)
    hsTmp.user = User.read(hsRes.user?.id)
    
    if(hsRes.context.lang){      
      hsTmp.user = hsTmp.user.csiSetEnUser()   
    }
    
    session.attention_message_once=null
    def oClient=Client.get(hsTmp?.client_id?:0)    
    if(oClient&&!(oClient?.is_notification?:0)){
      def oTemp_notification=Temp_notification.findWhere(id:4,status:1)	  
	    session.attention_message_once=oTemp_notification?oTemp_notification.text:null
      oClient.is_notification=1
      try{  
        if( !oClient.save(flush:true)) {
          log.debug(" Error on save client in profile init():")    
          oClient.errors.each{log.debug(it)}
        }
      }catch(Exception e){
        log.debug(" Error on save client in profile init(): \n"+e.toString())
      }
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
 //>>owner Dmitry
  def getVerifyStatus(hsRes){    
    def hsTmp=[:]
    hsTmp.verifyStatus=hsRes?.user?.modstatus?:2
    if(hsTmp.verifyStatus==2 && !hsRes.user?.email)
      hsTmp.verifyStatus=0
    return hsTmp	
  }

  def index = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	  if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)	
    return hsRes	
	}
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////EditProfile>>>//////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def edit={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes+=getVerifyStatus(hsRes)
    def bSave=requestService.getLongDef('save',0)
    if(!bSave){
      hsRes.inrequest=hsRes.user
      if(hsRes.inrequest.tel?:'') {
        hsRes.ind = hsRes.inrequest.tel.split("\\(")[0].replace('+','')
        hsRes.telef = hsRes.inrequest.tel.split('\\)')[1]
        hsRes.kod = hsRes.inrequest.tel.split('\\(')[1].split('\\)')[0]
      }
      if(hsRes.inrequest.tel1?:'') {
        hsRes.ind1 = hsRes.inrequest.tel1.split("\\(")[0].replace('+','')
        hsRes.telef1 = hsRes.inrequest.tel1.split('\\)')[1]
        hsRes.kod1 = hsRes.inrequest.tel1.split('\\(')[1].split('\\)')[0]
      }
    } else {
      hsRes+=requestService.getParams(['is_agent','gmt_id'],[],['firstname','lastname','description','nickname','email','agency','skype','www'])
      hsRes.ind = requestService.getStr('ind')
      hsRes.telef = requestService.getStr('telef')
      hsRes.kod = requestService.getStr('kod')
      hsRes.ind1 = requestService.getStr('ind1')
      hsRes.telef1 = requestService.getStr('telef1')
      hsRes.kod1 = requestService.getStr('kod1')
    }
    hsRes.gmt=Gmt.findAll('FROM Gmt ORDER BY id')
    requestService.setStatistic('editprofile',14)
    return hsRes
  }

  def photo={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    imageService.init(this,'userphotopic'+hsRes.user.id,'userphotokeeppic'+hsRes.user.id,ConfigurationHolder.config.pathtophoto+'user'+File.separatorChar) // 0
    imageService.startFileSession() // 1
    hsRes.images=[:]
    if((hsRes.user.picture?:'')!='')
      imageService.putIntoSessionFromDb(hsRes.user.picture,'file1') // 2
    def hsPics=imageService.getSessionPics('file1') // 3
    if(hsPics!=null){ 
      hsRes.images['photo_1']=hsPics.photo
      hsRes.images['thumb_1']=hsPics.thumb
    }  
    hsRes.url=hsRes.context.serverURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")

    requestService.setStatistic('editprofile',16)
    return hsRes
  }  
  /////////////////////////////////////////////////////////////////////////////////////////
  def savepicuserphoto={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    imageService.init(this,'userphotopic'+hsRes.user.id,'userphotokeeppic'+hsRes.user.id,ConfigurationHolder.config.pathtophoto+'user'+File.separatorChar,"images","alpha2.jpg","mask.jpg") // 0
    def iNo=1    	
	
    //ЗАГРУЖАЕМ ГРАФИКУ
    def hsData= imageService.loadPicture(
      "file1",
      Tools.getIntVal(ConfigurationHolder.config.photo.weight,2097152), //weight
      Tools.getIntVal(ConfigurationHolder.config.userphoto.image.size,210),  // size
      Tools.getIntVal(ConfigurationHolder.config.userphoto.thumb.size,50), //thumb size
      true,//SaveThumb
      false,//square		
      Tools.getIntVal(ConfigurationHolder.config.userphoto.image.height,210),//height
      Tools.getIntVal(ConfigurationHolder.config.userphoto.thumb.height,50),//thumb height			
      true,
      false
		) // 3
	
    hsData['num']=1 //<- НЕОБХОДИМО ДЛЯ КОРРЕКТНОЙ РАБОТЫ js в savepictureresult  
	
    // savepictureresult ОБЩИЙ ШАБЛОН, ЕСЛИ ИСПОЛЬЗОВАТЬ СКРИПТЫ АНАЛОГИЧНО СДЕЛАННОМУ	
    if(requestService.getIntDef('no_webcam',0)){
      render(view:'savepictureresult',model:hsData)	
      return
    }else{	
      render hsData.filename+','+hsData.thumbname+','+hsData.error+','+hsData.maxweight
      return
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  def deletepicuserphoto={
    //TODO: check user logged in
    requestService.init(this)	
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    //ОБЯЗАТЕЛЬНАЯ ИНИЦИАЛИЗАЦИЯ TODO: path into cfg
    imageService.init(this,'userphotopic'+hsRes.user.id,'userphotokeeppic'+hsRes.user.id,ConfigurationHolder.config.pathtophoto+'user'+File.separatorChar)
    
    def sName=requestService.getStr("name")

    imageService.deletePicture(sName)//4
    render(contentType:"application/json"){[error:false]}
  }

  def userphotoadd={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
	      
    imageService.init(this,'userphotopic'+hsRes.user.id,'userphotokeeppic'+hsRes.user.id,ConfigurationHolder.config.pathtophoto+'user'+File.separatorChar) // 0
    def hsPics=imageService.getSessionPics('file1')
    if((hsRes.user.picture?:'')!=''&&!hsPics)
      imageService.putIntoSessionFromDb(hsRes.user.picture,'file1') // 2
    imageService.finalizeFileSession(['file1'])
    hsRes.user.picture=(hsPics?.photo)?:hsRes.user.picture
    hsRes.user.smallpicture=(hsPics?.thumb)?:hsRes.user.smallpicture
    try{  
      if( !hsRes.user.save(flush:true)) {
        log.debug(" Error on save homephoto:")    
        hsRes.user.errors.each{log.debug(it)}
      }
    }catch(Exception e){
      log.debug('Profile:userphotoadd. ERROR ON ADD photo \n'+e.toString())
    }
    redirect(action:'edit', base:hsRes.context.mainserverURL_lang)
  }

  def userphotodelete={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    imageService.init(this,'','',ConfigurationHolder.config.pathtophoto+'user'+File.separatorChar)    
    def lsPictures = []
    lsPictures<<hsRes.user.picture
    hsRes.user.picture=''
    hsRes.user.smallpicture=''
    if( !hsRes.user.save(flush:true)) {
      log.debug(" Error on save homephoto:")    
      hsRes.user.errors.each{log.debug(it)}
    }
    imageService.deletePictureFilesFromHd(lsPictures)

    redirect(action:'edit', base:hsRes.context.mainserverURL_lang)
  }

  def changepass={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
	
    flash.error = []
	
    def sPassword1=requestService.getStr('pass')
    def sPassword2=requestService.getStr('pass2')
      
    if(sPassword2!=sPassword1)
      flash.error<<1
    else if(sPassword2.size()<Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,5))
      flash.error<<2
    else  {
      hsRes.user.password=Tools.hidePsw(sPassword2)
      if( !hsRes.user.save(flush:true)) {
        log.debug(" Error on save user")
        hsRes.user.errors.each { log.debug(it) }
      }
    }
	
    redirect(action:'edit', base:hsRes.context.mainserverURL_lang)
  }

  def saveProfile={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
	
    flash.error = []
    hsRes+=requestService.getParams(['is_agent','gmt_id'],[],['firstname','lastname','description','nickname','email','agency','ind','kod','telef','ind1','kod1','telef1','skype','www'])
    def lsTel = requestService.getParams(null,null,['ind','kod','telef']).inrequest
    def lsTel1 = requestService.getParams(null,null,['ind1','kod1','telef1']).inrequest

    if(lsTel && (lsTel?:[]).size()!=3)
      flash.error<<7
    else if (lsTel){
      for (t in lsTel){
        if (t.key!='telef'&&!t.value.replace('+','').replace(' ','').replace('-','').matches("[0-9]+")){
          if(!flash.error.find{it==7})
            flash.error<<7
        } else if (t.key=='telef'&&!t.value.replace('+','').replace(' ','').replace('-','').matches("[0-9]{5,}")){
          if(!flash.error.find{it==7})
            flash.error<<7
        }
      }
    }
    if(lsTel1 && (lsTel1?:[]).size()!=3)
      flash.error<<8
    else if (lsTel1){
      for (t1 in lsTel1)
        if (!t1.value.replace('+','').replace(' ','').replace('-','').matches("[0-9]+"))
          if(!flash.error.find{it==8})
            flash.error<<8
    }
    if (!lsTel && hsRes.user.tel)
      flash.error<<9
    def bNeedMail = false
    if (getVerifyStatus(hsRes).verifyStatus!=1){
      if(hsRes.inrequest?.email!=hsRes.user.email){
        if (!Tools.checkEmailString(hsRes.inrequest.email))
          flash.error<<3
        else if(User.findByEmail(hsRes.inrequest.email) && User.findByEmail(hsRes.inrequest.email)?.id!=hsRes.user.id)
          flash.error<<6
      }
    }
    if(!hsRes.inrequest?.nickname)
      flash.error<<4
    if(hsRes.inrequest?.is_agent)
      if(!hsRes.inrequest?.agency)
        flash.error<<10

    if(!(flash.error.size())) {
      if ((hsRes.inrequest?.description?:'').size()>hsRes.textlimit) hsRes.inrequest?.description = hsRes.inrequest?.description.substring(0, hsRes.textlimit)
      hsRes.user.firstname = hsRes.inrequest.firstname?:''
      hsRes.user.lastname = hsRes.inrequest.lastname?:''
      hsRes.user.description = hsRes.inrequest.description?:''
      hsRes.user.nickname = hsRes.inrequest.nickname
      hsRes.user.is_agent = hsRes.inrequest.is_agent?:0
      hsRes.user.skype = hsRes.inrequest.skype?:''
      hsRes.user.www = hsRes.inrequest.www?:''
      hsRes.user.gmt_id = hsRes.inrequest.gmt_id?:0
      
      if(hsRes.inrequest?.is_agent?:0)
        hsRes.user.agency = hsRes.inrequest.agency
      def oldTel = hsRes.user.tel?:''
      hsRes.user.tel = ''
      hsRes.user.tel1 = ''
      if (lsTel)
        hsRes.user.tel = '+'+hsRes.inrequest.ind.replace('+','').replace(' ','').replace('-','')+'('+hsRes.inrequest.kod.replace('+','').replace(' ','').replace('-','')+')'+hsRes.inrequest.telef.replace('+','').replace(' ','').replace('-','')
      if (lsTel1)
        hsRes.user.tel1 = '+'+hsRes.inrequest.ind1.replace('+','').replace(' ','').replace('-','')+'('+hsRes.inrequest.kod1.replace('+','').replace(' ','').replace('-','')+')'+hsRes.inrequest.telef1.replace('+','').replace(' ','').replace('-','')
      if(oldTel != hsRes.user.tel && hsRes.user.tel != '')
        hsRes.user.is_telcheck  = 0
      if (getVerifyStatus(hsRes).verifyStatus!=1 && hsRes.inrequest?.email && hsRes.inrequest?.email!=hsRes.user.email){
        hsRes.user.email = hsRes.inrequest.email
        hsRes.user.name = hsRes.inrequest.email
        if (hsRes.user.client_id){
          if(!Client.get(hsRes.user.client_id)?.updateName(hsRes.inrequest.email)?.save(flush:true)) {
            log.debug(" Error on update client's name(profile/saveProfile)")
            hsRes.user.errors.each { log.debug(it) }
          }
        }
        if (!hsRes.user.code)
          hsRes.user.code=java.util.UUID.randomUUID().toString()
        bNeedMail = true
      }
      if(hsRes.context.lang){
        def oUser=User.get(hsRes.user.id)
        hsRes.user.properties.each { prop, val ->
          if(["metaClass","class"].find {it == prop}) return
          if(oUser.hasProperty(prop)) oUser[prop] = val
        }    
        hsRes.user=oUser
      }        
      
      if( !hsRes.user.save(flush:true)) {
        log.debug(" Error on save user")
        hsRes.user.errors.each { log.debug(it) }
        flash.error<<5
      }
    }
    if (bNeedMail && !(flash.error.size())){
      //<<Email
      def lsText=Email_template.findWhere(action:'#activation')
      def sText='[@EMAIL], for activation of your account use follow link [@URL]'
      def sHeader="Registration at Staytoday" 
      if(lsText){
        sText=lsText.itext
        sHeader=lsText.title
      }
      sText=sText.replace(
      '[@NICKNAME]',hsRes.user.nickname).replace(
      '[@EMAIL]',hsRes.user.email).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/user/confirm/'+hsRes.user.code))
      sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
      sHeader=sHeader.replace(
      '[@EMAIL]',hsRes.user.email).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+'/user/confirm/'+hsRes.user.code))

      try{ 
        if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
          mailerService.sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,hsRes.user.email,sHeader,1)        
        else{
        sendMail{
          to hsRes.user.email         
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
    }
    hsRes.inrequest.save = 1
    redirect(action:'edit', params:hsRes.inrequest, base:hsRes.context.mainserverURL_lang)
    return
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////EditProfile<<<//////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Verify>>>///////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def verifyUser={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
	hsRes+=getVerifyStatus(hsRes)
	hsRes.user = User.get(hsRes.user?.id)
	hsRes.isSMSsend = Sms.isSMSsend(hsRes.user?.tel?:'')
	hsRes+=requestService.getParams([],[],['sendmail'])
	hsRes.maxerror = Tools.getIntVal(ConfigurationHolder.config.user_max_enter_fail,10)
	if (hsRes.inrequest.sendmail
	  && hsRes.verifyStatus==2) {
		if (!hsRes.user.code) {
			hsRes.user.code=java.util.UUID.randomUUID().toString()
			if(!hsRes.user.save(flush:true)) {
				log.debug(" Error on save User:")
				hsRes.user.errors.each{log.debug(it)}
			}
		}
		flash.error = mailerService.sendActivationMail(hsRes.user, hsRes.context)
	}
	requestService.setStatistic('editprofile',15)
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

  def verifyTel={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
	if (!checkUser(hsRes))
		return

	hsRes.user = User.get(hsRes.user.id)
	if(session.user_enter_fail>Tools.getIntVal(ConfigurationHolder.config.user_max_enter_fail,10)){
		render (["error":true,"fail_count":session.user_enter_fail] as JSON)
		return
	}

	hsRes+=requestService.getParams(['smscode'])
	if(hsRes.user.smscode == hsRes.inrequest.smscode.toString()){
	  hsRes.user.is_telcheck = 1
	  hsRes.user.save(flush:true)
	  session.user_enter_fail=null
	  render(contentType:"application/json"){[error:false]}
	} else {
	  if(hsRes.inrequest.smscode)
		if(session.user_enter_fail)
          session.user_enter_fail++
		else
          session.user_enter_fail=1
	  render (["error":true,"fail_count":(session.user_enter_fail?:0)] as JSON)
	}
	return
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Verify<<<///////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////ViewProfile<<<//////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def view={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    def lId=requestService.getLongDef('id',0)

    hsRes.viewedUser = User.read(lId)
    if (hsRes.viewedUser?.ref_id)
      hsRes.viewedUser = User.read(hsRes.viewedUser?.ref_id)
    if (!hsRes.viewedUser) {
      response.sendError(404)
      return
    }
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.textlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
    hsRes.verify = [:]
    def modSt = 0
    if ((hsRes.user?.id == hsRes.viewedUser.id)||(session?.admin?.id!=null))
      hsRes.verify.owner = true
    else {
      hsRes.verify.owner = false
      modSt = 1
    }
    def oUcommentSearch = new UcommentSearch()
    if(hsRes.viewedUser?.client_id){
      def oHome=new Home()
      hsRes.data=oHome.csiGetHome(hsRes.viewedUser.client_id,modSt,120,0)                  
      hsRes.modstatus=Homemodstatus.list()
      hsRes.photourl = ConfigurationHolder.config.urlphoto + hsRes.viewedUser.client_id.toString()+'/'    
      hsRes.ownerClient = Client.get(hsRes.viewedUser.client_id)
      hsRes.payway=Payway.findAllByModstatusAndIs_invoice(1,0)
      hsRes.goodComments=oUcommentSearch.csiSelectUcommentsForMyHomes(hsRes.viewedUser.client_id?:0,2,1,requestService.getOffset())
      hsRes.comments=oUcommentSearch.csiSelectUcommentsForMyHomes(hsRes.viewedUser.client_id?:0,-1,1,requestService.getOffset())
      if(hsRes.data){
        hsRes.hometype=Hometype.list([sort:'id',order:'asc'])
        hsRes.homeperson=Homeperson.list()
        hsRes.homeoption=Homeoption.findAllByFacilitygroup_id(1,[sort:'id'])
      }
      
      if(hsRes.context?.lang){
        def lsHomes=[]
        for(home in hsRes.data.records){                            
          lsHomes<<home.csiSetEnHome()          
        }
        hsRes.data.records=lsHomes 
        hsRes.viewedUser = hsRes.viewedUser.csiSetEnUser()                       
      }                  
    }
    hsRes.myComments=oUcommentSearch.csiSelectMyUcomments(hsRes.viewedUser.id?:0,1,requestService.getOffset())
    hsRes.commentsOnMe=oUcommentSearch.csiSelectUcommentsOnMe(hsRes.viewedUser.id?:0,20,0)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    requestService.setStatistic('viewprofile',0,0,0,'',lId)
    return hsRes
  }

  def viewComments = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams([],['client_id','u_id'])

    def oUcommentSearch = new UcommentSearch()
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.comments=oUcommentSearch.csiSelectUcommentsForMyHomes(hsRes.inrequest.client_id?:0,-1,20,requestService.getOffset())
    hsRes.answerComments=hsRes.comments.records.collect {Ucomment.findAllByRefcomment_id(it.id)}
    hsRes.owneruser = User.findByClient_id(hsRes.inrequest?.client_id?:(0 as long))
    return hsRes
  }

  def viewMyComments = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams([],['lId','u_id'])

    def oUcommentSearch = new UcommentSearch()
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.myComments = oUcommentSearch.csiSelectMyUcomments(hsRes.inrequest.lId?:0,20,requestService.getOffset())
    hsRes.answerComments=hsRes.myComments.records.collect {Ucomment.findAllByRefcomment_id(it.id)}
    hsRes.answerOwners=hsRes.answerComments.collect {
      User.get(it[0]?.user_id)
    }

    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////ViewProfile<<<//////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Reviews<<<//////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def reviews={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.user = User.get(hsRes.user.id)
    if (params.paginate == 'comments') {
      hsRes.commentsOffset = requestService.getOffset()
      hsRes.tab = 0
    } else if (params.paginate == 'yourcomments') {
      hsRes.yourcommentsOffset = requestService.getOffset()
      hsRes.tab = 1
    }
    if (requestService.getOffset()==0)
      requestService.setStatistic('editprofile',17)
    params.offset = null
    params.max = null
    hsRes.yourcomments=Ucomment.findAll('FROM Ucomment WHERE user_id=:user_id AND comstatus>=0 ORDER BY comdate',[user_id:hsRes.user.id], [max:3, offset:(hsRes.yourcommentsOffset?:0)])
    hsRes.total_yourcomments = Ucomment.findAll('FROM Ucomment WHERE user_id=:user_id AND comstatus>=0 ORDER BY comdate',[user_id:hsRes.user.id]).size()
    def oUcommentSearch = new UcommentSearch()
    hsRes.comments=oUcommentSearch.csiSelectUcommentsForMyHomes(hsRes.user.client_id?:0,-1,3,hsRes.commentsOffset?:0)
    hsRes.answerComments=hsRes.comments.records.collect {Ucomment.findAllByRefcomment_id(it.id)}

    return hsRes
  }
//<<owner Dmitry
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Reviews<<<//////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////// 
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Comments>>>/////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def addcomment={    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams([],['home_id'],['comtext'])
    hsRes.inrequest.rating = requestService.getIntDef('rating',0)
    if(!hsRes.user ||!(hsRes.inrequest?.comtext?:'')){
      redirect(action:'view', id:hsRes.inrequest?.home_id, base:hsRes.context.mainserverURL_lang)
      return
    }
    hsRes.rUser = User.get(hsRes.inrequest?.home_id?:0)
    if(hsRes.rUser){
	  def stringlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
	  if (hsRes.inrequest?.comtext.size()>stringlimit) hsRes.inrequest?.comtext = hsRes.inrequest?.comtext.substring(0, stringlimit)
      def oUcomment = new Ucomment()
      oUcomment.user_id = hsRes.user.id
      oUcomment.home_id = hsRes.inrequest?.home_id
      oUcomment.typeid = 2
      oUcomment.comtext = hsRes.inrequest?.comtext
      oUcomment.comstatus = 0
      oUcomment.rating = hsRes.inrequest?.rating
      oUcomment.refcomment_id = 0
      oUcomment.is_mainpage = 0
      oUcomment.comdate=new Date()
	  if(!oUcomment.save(flush:true)) {
        log.debug(" Error on save Ucomment:")
        oUcomment.errors.each{log.debug(it)}
      }
    } else {
      response.sendError(404)
      return
	}

    redirect(action:'view', params:[id:hsRes.inrequest?.home_id], base:hsRes.context.mainserverURL_lang)
  }

  def viewCommentsOnMe = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams([],['lId','u_id'])

    def oUcommentSearch = new UcommentSearch()
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.commentsOnMe = oUcommentSearch.csiSelectUcommentsOnMe(hsRes.inrequest.lId?:0,20,requestService.getOffset())
    hsRes.answerComments=hsRes.commentsOnMe.records.collect {Ucomment.findAllByRefcomment_id(it.id)}
    hsRes.owneruser = User.get(hsRes.inrequest.lId)
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Comments<<</////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
}
