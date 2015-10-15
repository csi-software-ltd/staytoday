
class Guestbook {

  def sessionFactory
  def searchService
  
  static constraints = {
    fio(maxSize:150)
    email(maxSize:50,email:true)
    adr(maxSize:250)
    tel_code(maxSize:50)
    tel(maxSize:50)    
    rectitle(maxSize:150)
  	email(nullable:true)
	home(nullable:true)	
    fio(nullable:true)	
	tel_code(nullable:true)	
	tel(nullable:true)		
    modstatus(nullable:true)	
    user_id(nullable:true)
	ip(nullable:true)
	recinfo(nullable:true)
  }
  
  Long id
  Long user_id
  
  Integer gbtype_id  
  Integer modstatus
  //Integer gberrortype_id 

  String fio
  String adr
  String tel_code
  String tel
  String email
  String home
  String rectitle
  String rectext  
  String recinfo
  String ip  
  
  ///////////////////////////////////////////////////////////////////////////
  def csiGetIpCount(ip){
    Calendar oCalendar=Calendar.getInstance();
	oCalendar.set(Calendar.HOUR_OF_DAY,0)
	oCalendar.set(Calendar.MINUTE,0)
	oCalendar.set(Calendar.SECOND,0)
	def sDate = Tools.escape(String.format('%tF', oCalendar.time))
		
	def hsRes=[:]
	def session=sessionFactory.getCurrentSession()	
    def qSql=session.createSQLQuery("SELECT count(ip) FROM guestbook WHERE regdate>'"+sDate+"' AND ip="+ip)
	hsRes.records=qSql.list()
	
	if((hsRes.records?:[]).size()!=0)
      hsRes.count=hsRes.records[0]
	else
  	  hsRes.count=0
	return hsRes  
  }
  ///////////////////////////////////////////////////////////////////////////
  def csiGetLastInsert(){  return searchService.getLastInsert(); }
  ///////////////////////////////////////////////////////////////////////////
  def csiChangeStatus(lId,bRead){
    def sSql = "UPDATE guestbook SET modstatus=${bRead?1:0} WHERE id=:id"
    def session = sessionFactory.getCurrentSession()
    def qSql=session.createSQLQuery(sSql)
    qSql.setLong("id",lId)    
    try{
      qSql.executeUpdate();
    }catch(Exception e){
      log.debug("Cannot change guestbook message status: Guestbook.csiChangeStatus \n"+e.toString())
    }
    session.clear()
  }
}
