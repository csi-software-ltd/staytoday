class District {    
  def searchService
  
  static constraints = {	
    name(nullable:true)
    name_en(nullable:true)
    region_id(nullable:true)  
  }
  static mapping = {
    version false
  }
  Integer id
  String name  
  String name_en
  Integer region_id  

  String toString() {"${this.name}" } 

  def getByIds(lsIds){
    def hsSql=[:]
    def hsList=[ids:lsIds]
    hsSql.select="*"
    hsSql.from="district"
    hsSql.where="id in (:ids)"
	  hsSql.order="name ASC"
    return searchService.fetchData(hsSql,null,null,null,hsList,District.class)
  }  
}
