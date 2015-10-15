class Mailing_history {
  def searchService    
  
  static constraints = {	
  }
  static mapping = {
    version false
  }
  Integer id
  Integer template_id
  String contact
  String name
  Integer modstatus
  Integer task_id
  Date moddate
  
   def csiFindHistory(sName,sContact,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:''] 	
	def hsString=[:]
	
    hsSql.select="*"
    hsSql.from='mailing_history'
	hsSql.where="1=1"+										
				((sName!='')?' AND (name like CONCAT("%",:sName) OR name like CONCAT(:sName,"%") OR name like CONCAT("%",:sName,"%"))':'')+
				((sContact!='')?' AND (contact like CONCAT("%",:sContact) OR contact like CONCAT(:sContact,"%") OR contact like CONCAT("%",:sContact,"%"))':'')
				
	hsSql.order="id DESC"	
   	  
    if(sName)
      hsString['sName']=sName
    if(sContact)
      hsString['sContact']=sContact	  

	def hsRes=searchService.fetchDataByPages(hsSql,null,null,null,hsString,
      null,null,iMax,iOffset,'id',true,Mailing_history.class)
  }  
}
