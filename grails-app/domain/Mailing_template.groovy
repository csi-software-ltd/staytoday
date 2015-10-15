class Mailing_template { 
  def searchService   
  
  static constraints = {	
  }
  static mapping = {
    version false
  }
  Integer id
  String mtext
  String header
  String name
  Integer modstatus
  Integer type_id
  Integer is_destemp
  
  def csiFindTemplate(iTemplate_id,iType_id,iModstatus,sName,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:''] 
	def hsLong=[:]
	def hsString=[:]
	
    hsSql.select="*"
    hsSql.from='mailing_template'
	hsSql.where="1=1"+
				((iTemplate_id>0)?' AND id =:iTemplate_id':'')+
				((iType_id>0)?' AND type_id =:iType_id':'')+
				((iModstatus>-1)?' AND modstatus =:iModstatus':'')+								
				((sName!='')?' AND (name like CONCAT("%",:sName) OR name like CONCAT(:sName,"%") OR name like CONCAT("%",:sName,"%"))':'')
				
	hsSql.order="id DESC"
	
    if(iTemplate_id>0)
      hsLong['iTemplate_id']=iTemplate_id
	if(iType_id>0)
      hsLong['iType_id']=iType_id
	if(iModstatus>-1)
      hsLong['iModstatus']=iModstatus	  	  
    if(sName)
      hsString['sName']=sName	

	def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'id',true,Mailing_template.class)
  }  
}
