class Hometype {
  def searchService
  static mapping = {
    version false
  }

  static constraints = {
  }

  Integer id
  Integer regorder
  String name
  String name_en
  String name2
  String name3
  String name3_en
  String name4
  String name5
  String urlname
  
  def getByIds(lsIds){
    def hsSql=[:]
    def hsList=[ids:lsIds]
    hsSql.select="*"
    hsSql.from="hometype"
    hsSql.where="id in (:ids)"
	  hsSql.order="id ASC"
    return searchService.fetchData(hsSql,null,null,null,hsList,Hometype.class)
  }
}
