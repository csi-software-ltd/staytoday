class Notice {
  def searchService

  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  String title=''
  String title_en=''
  String description=''
  String description_en=''
  String image=''
  String url=''
  Integer ncount=0
  Integer nclick=0
  Integer modstatus=0
  Integer is_index=0
  String page=''  
//////////////////////////////////////////////////////////////////////////////////////////  
  def getNotice(iId,iModstatus,iMax,iOffset){
    def hsSql=[select:"*",
        from:'notice',
        where:'']
        //order:'']
    
    def hsInt=[:]    
    
    hsSql.where="1=1"+
    (iId?" and id=:iId":"")+
    ((iModstatus>-1)?" and modstatus=:iModstatus":"")	
    
	  if(iId)			 
	    hsInt['iId']=iId
    if(iModstatus>-1)  
	    hsInt['iModstatus']=iModstatus	    	  	
 
    return searchService.fetchDataByPages(hsSql,null,null,hsInt,null,null,null,iMax,iOffset,'id',true,Notice.class)
  }
}