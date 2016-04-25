import grails.util.Holders
class User {
  def sessionFactory 
  def searchService

  static constraints = {
    tel (nullable:true,maxSize:50)
    tel1 (nullable:true,maxSize:50)
    email(email:true,maxSize:50)
    password(nullable:true,maxSize:50) // ?? EMPTY PASSWORD ????
    client_id(nullable:true) 
    companylist(nullable:true) 	
    lastdate(nullable:true)
    picture(nullable:true)
    smallpicture(nullable:true)
    firstname(nullable:true)
    lastname(nullable:true)
    ref_id(nullable:true)
    login(nullable:true)
    is_am(nullable:true)
    code(nullable:true)	
    email(nullable:true)
    description(nullable:true)
	  is_zayvka(nullable:true)
	  agency(nullable:true)
  }
  static mapping = {
    cache false
  }
  Long id
  String name
  String firstname
  String lastname
  String nickname
  String description
  String openid
  String provider
  Integer banned
  Integer is_external
  Long client_id
  Integer is_am
  Integer is_agent
  Integer is_agentcheck
  String agency
  Integer is_zayvka
  String tel
  String tel1
  String email
  String login
  String password
  String skype = ''
  String www = ''
  Date inputdate
  Date lastdate
  Integer ncomment
  String companylist  
  String picture
  String smallpicture
  Long ref_id
  Integer activityrating
  Integer modstatus
  String code  
  String smscode
  Integer is_telcheck = 0
  Integer is_news
  Integer is_review
  Integer is_emailzayvka
  Integer is_improve
  Integer is_postreview
  Integer is_ratingnote
  Integer is_noticeSMS = 1
  Integer is_telaprove = 0
  Integer is_clientphoto = 0
  Integer gmt_id=0
  String outer_id = ''
  Integer is_subscribe = 0

  String toString() {"${this.openid}" }  
  //////////////////////////////////////////////////////////////////////////////
  def csiInsertInternal(hsUser){
    def session = sessionFactory.getCurrentSession()
    def iId=0
    hsUser.email=hsUser.email.toLowerCase()
    def sSql="""
          INSERT INTO user(openid,name,provider,banned,is_external,email,password,firstname,lastname,nickname,modstatus,client_id,code,activityrating)
          VALUES (:openid,:name,'staytoday',0,0,:name,:password,:firstname,:lastname,:nickname,:modstatus,:client_id,:code,:activityrating)
          ON DUPLICATE KEY  UPDATE name=:name,provider='staytoday',password=:password,modstatus=:modstatus
          """
    def qSql=session.createSQLQuery(sSql)
    qSql.setString("name",hsUser.email);
    qSql.setString("openid",Holders.config.user.internal.url+hsUser.email.replace('@','$'));
    qSql.setString("password",hsUser.password?:'');    
    qSql.setString("firstname",hsUser.firstname);
    qSql.setString("lastname",hsUser.lastname);	
    qSql.setString("nickname",hsUser.nickname);
    qSql.setLong("client_id",hsUser.client_id);
    qSql.setLong("modstatus",hsUser?.client_id?2:0);
    qSql.setLong("activityrating",Tools.getIntVal(Holders.config.addRating.newuser.rating,30));
    qSql.setString("code",hsUser.code?:'')
    
    try{
      qSql.executeUpdate();
      return searchService.getLastInsert();
    }catch(Exception e){
      log.debug("User:csiInsert Cannot add new user. \n"+e.toString())
    }
    session.clear()
    return iId
  }
  //////////////////////////////////////////////////////////////////////////////
  def csiInsertExternal(hsUser){
    def session = sessionFactory.getCurrentSession()
    def iId=0
    def sSql="""
          INSERT INTO user(openid,name,provider,banned,is_external,picture,ref_id,modstatus,nickname,activityrating)
          VALUES (:openid,:name,:provider,0,1,:picture,:ref_id,2,:name,:activityrating)
          ON DUPLICATE KEY  UPDATE name=:name,provider=:provider,picture=:picture
          """
    def qSql=session.createSQLQuery(sSql)
    qSql.setString("openid",hsUser.url?:'');
    qSql.setString("name",hsUser.name?:'');
    qSql.setString("provider",hsUser.provider?:'');
    qSql.setString("picture",hsUser.picture?:'');
    qSql.setLong("ref_id",hsUser.ref_id?:0);
    qSql.setLong("activityrating",Tools.getIntVal(Holders.config.addRating.newuser.rating,30));
    try{
      qSql.executeUpdate();
      return searchService.getLastInsert();
    }catch(Exception e){
      log.debug("Users:csiInsertExternal Cannot add new user. \n"+e.toString())
    }
    session.clear()
    return iId
  }

  def csiInsertAPIExternal(hsUser){
    def session = sessionFactory.getCurrentSession()
    def sSql="""
          INSERT INTO user(openid,name,email,provider,banned,is_external,modstatus,nickname,outer_id,activityrating)
          VALUES (:openid,:name,:email,:provider,0,1,2,:name,:outer_id,:activityrating)
          ON DUPLICATE KEY  UPDATE name=:name,provider=:provider
          """
    def qSql=session.createSQLQuery(sSql)
    qSql.setString("openid",hsUser.url?:'');
    qSql.setString("name",hsUser.name?:'');
    qSql.setString("email",hsUser.email?:'');
    qSql.setString("provider",hsUser.provider?:'');
    qSql.setString("outer_id",hsUser.outer_id?:'');
    qSql.setLong("activityrating",Tools.getIntVal(Holders.config.addRating.newuser.rating,30));
    try{
      qSql.executeUpdate();
      return searchService.getLastInsert();
    }catch(Exception e){
      log.debug("Users:csiInsertAPIExternal Cannot add new user. \n"+e.toString())
    }
    session.clear()
    return 0
  }
  ////////////////////////////////////////////////////////////////////////////////
  def csiSelectUsers(sUserName,sNickname,sProvider,iModstatus,isClient,iNComment,iUserId,iTelchek,sRegistrDateFrom,sRegistrDateTo,sEnterDateFrom,sEnterDateTo,iOrder,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='user'
    hsSql.where="1=1"+
                ((sUserName!='')?' AND name like CONCAT("%",:name,"%")':'')+
                ((sNickname!='')?' AND nickname like CONCAT("%",:nickname,"%")':'')+
                ((sProvider!='')?' AND provider=:provider':'')+
                ((iModstatus>-2)?' AND modstatus =:modstatus':'')+
                ((isClient>-1)?((!isClient)?' AND client_id =0':' AND client_id>0'):'')+
                ((iUserId>0)?' AND id =:id':'')+
                ((sRegistrDateFrom!='')?' AND inputdate >:inputdatefrom':'')+
                ((sRegistrDateTo!='')?' AND inputdate <:inputdateto':'')+
                ((sEnterDateFrom!='')?' AND lastdate >:lastdatefrom':'')+
                ((sEnterDateTo!='')?' AND lastdate <:lastdateto':'')+
                ((iNComment>0)?' AND ncomment>0':'')+
                ((iTelchek>-2)?' AND is_telcheck=:is_telcheck':'')
    hsSql.order=(iOrder==1)?"lastdate DESC":((iOrder==2)?"id":"inputdate DESC")
    if(sRegistrDateFrom!='')
      hsString['inputdatefrom']=sRegistrDateFrom
    if(sRegistrDateTo!='')
      hsString['inputdateto']=sRegistrDateTo
    if(sEnterDateFrom!='')
      hsString['lastdatefrom']=sEnterDateFrom
    if(sEnterDateTo!='')
      hsString['lastdateto']=sEnterDateTo
    if(iModstatus>-2)
      hsLong['modstatus']=iModstatus
    if(sUserName!='')
      hsString['name']=sUserName
    if(sNickname!='')
      hsString['nickname']=sNickname
    if(sProvider!='')
      hsString['provider']=sProvider
    if(iUserId>0)
      hsLong['id']=iUserId
    if(iTelchek>-2)
      hsLong['is_telcheck']=iTelchek

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'id',true,User.class)
  }
  //////////////////////////////////////////////////////////////////////////////
  def restorySession(sGuid){  
    def hsSql=[
      select:"*, user.id as id",
      from:"user,usession",
      where:"(user.id=usession.users_id) AND (user.banned=0) AND (usession.guid=:guid)"]
    
    return searchService.fetchData(hsSql,null,null,[guid:sGuid],null,User.class)
  }
  /////////////////////////////////////////////////////////////////////////////
  Boolean validateTelNumber(){
    this.smscode = Tools.generateSMScode()
    if(!this.save(flush:true))
      return false
    return true
  }
  /////////////////////////////////////////////////////////////////////////////
  Boolean mboxNewUser(hsUser){
    if (!hsUser.email||!hsUser.nickname) {
      log.debug("User:mboxNewUser. Not all the parameters are set. \n")
      return false
    }
    if (!(new Note(0, csiInsertInternal([email:hsUser.email,password:Tools.hidePsw(Tools.hidePsw(hsUser.email)[0..8]),firstname:'',lastname:'',nickname:(hsUser.nickname),client_id:0,code:java.util.UUID.randomUUID().toString()]), Notetype.get(1))).save(flush:true))
      return false
    return true
  }

  User apiNewUser(hsUser){
    if (!hsUser.email||!hsUser.nickname) {
      log.debug("User:apiNewUser. Not all the parameters are set. \n")
      return null
    }
    User.get(csiInsertAPIExternal(url:hsUser.company+'_'+hsUser.outer_id,name:hsUser.nickname,email:hsUser.email,provider:'api',outer_id:hsUser.outer_id))
  }
  ////////////////////////////////////////////////////////////////////////////
  /*def beforeUpdate() {
    lastdate = new Date()
  }*/
  def updateLastdate() {
    if ((new Date().getTime() - (lastdate?.getTime()?:0))>300000) {
      synchronized(searchService) {
        User.withTransaction { status ->
          refresh()
          lastdate = new Date()
          try {
            merge(flush:true)
          } catch(Exception e) {
            log.debug('User:updateLastdate. Error on update lastdate for user '+id+'\n'+e.toString())
          }
        }
      }
    }
  }
  def csiGetResponseStat() {
    def respTime = Mbox.executeQuery("""
        select COALESCE(avg(
          case
          WHEN responsetime<:step1 THEN 1
          WHEN responsetime<:step2 THEN 2
          WHEN responsetime<:step3 THEN 3
          ELSE 4
          END
        ),0)
        from Mbox
        where homeowner_cl_id=:cl_id and responsetime!=0
      """,[cl_id:client_id,step1:Tools.getIntVal(Holders.config.responsegradation.step1,3600) as Long,step2:Tools.getIntVal(Holders.config.responsegradation.step2,21600) as Long,step3:Tools.getIntVal(Holders.config.responsegradation.step3,86400) as Long])[0]
    def respCount = Mbox.countByHomeowner_cl_idAndResponsetimeNotEqual(client_id,0)
    def reqCount = Mbox.countByHomeowner_cl_idAndInputdateGreaterThan(client_id,new Date(112,11,05))
    def isDisplayRpCount = respCount>=Tools.getIntVal(Holders.config.responsegradation.respToDisplay,5)
    return [time:Math.round(respTime),rpCount:respCount,rqCount:reqCount,isDisplayRpCount:isDisplayRpCount]
  }
  def csiGetResponseTime() {
    def respTime = Mbox.executeQuery("""
        select COALESCE(avg(
          case
          WHEN responsetime<:step1 THEN 1
          WHEN responsetime<:step2 THEN 2
          WHEN responsetime<:step3 THEN 3
          ELSE 4
          END
        ),0)
        from Mbox
        where homeowner_cl_id=:cl_id and responsetime!=0
      """,[cl_id:client_id,step1:Tools.getIntVal(Holders.config.responsegradation.step1,3600) as Long,step2:Tools.getIntVal(Holders.config.responsegradation.step2,21600) as Long,step3:Tools.getIntVal(Holders.config.responsegradation.step3,86400) as Long])[0]
    return [time:Math.round(respTime)]
  }
  User csiSetEnUser(){
    def oUserTmp=new User()
    this.properties.each { prop, val ->
      if(["metaClass","class"].find {it == prop}) return
      if(oUserTmp.hasProperty(prop)) oUserTmp[prop] = val
    }    
    oUserTmp.id=id        
    oUserTmp.name=Tools.transliterate(oUserTmp.name,0)
    oUserTmp.nickname=Tools.transliterate(oUserTmp.nickname,0)
    oUserTmp.firstname=Tools.transliterate(oUserTmp.firstname,0)      
    
    return oUserTmp
  }
}
