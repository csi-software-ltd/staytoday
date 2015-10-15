class Tempusers {
  def sessionFactory
  def searchService
  
  static constraints = {
    email(email:true)
	initdate(nullable:true)
	client_id(nullable:true)
  }
  
  String email
  String code
  Date initdate
  Long client_id
  
  def clearOldRegistrations(iTimelive){
    def session = sessionFactory.getCurrentSession()
    def sSql="DELETE FROM tempusers WHERE (UNIX_TIMESTAMP(now())-UNIX_TIMESTAMP(initdate))>:t"
    try{
      def qSql=session.createSQLQuery(sSql)
      qSql.setInteger("t",iTimelive);
      qSql.executeUpdate()
    }catch (Exception e) {
      log.debug("Error Tempusers::clearOldRegistrations\n"+e.toString());
      def hsRes=[]
    }
    session.clear()
  }
  
  def getTempusers(sEmail,iMax,iOffset){   
	def hsSql=[select:'',from:'',where:''] 	
	def hsString=[:]
	
    hsSql.select="*"
    hsSql.from='tempusers'
	hsSql.where="1=1"+
                ((sEmail!='')?' AND email like CONCAT("%",:email,"%")':'')	            			    
    if(sEmail!='')           
       hsString['email']=sEmail	   
	def hsRes=searchService.fetchDataByPages(hsSql,null,null,null,hsString,
      null,null,iMax,iOffset,'id',true,Tempusers.class)
  }  
}
