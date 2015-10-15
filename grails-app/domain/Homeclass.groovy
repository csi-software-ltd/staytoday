class Homeclass {  
  def searchService
  
  static mapping = {
    version false
  }  
  static constraints = {    
  }
  
  Integer id  
  String name
  String name2
  String name3
  String name4
  
  def getByIds(lsIds){
    def hsSql=[:]
    def hsList=[ids:lsIds]
    hsSql.select="*"
    hsSql.from="homeclass"
    hsSql.where="id in (:ids)"
	  hsSql.order="id ASC"
    return searchService.fetchData(hsSql,null,null,null,hsList,Homeclass.class)
  }  
}
