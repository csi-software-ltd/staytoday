class Metro {  
  def searchService  
  static mapping = {
    version false
  }	  
  
  static constraints = {    
  }
  
  Integer id  
  String name
  String name_en
  String urlname
  Integer modstatus
  Integer region_id
  Long x
  Long y 

  def getByIds(lsIds){
    def hsSql=[:]
    def hsList=[ids:lsIds]
    hsSql.select="*"
    hsSql.from="metro"
    hsSql.where="id in (:ids)"
	  hsSql.order="name ASC"
    return searchService.fetchData(hsSql,null,null,null,hsList,Metro.class)
  }   
}