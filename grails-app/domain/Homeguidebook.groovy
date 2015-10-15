class Homeguidebook {
  def searchService
  def sessionFactory

  static mapping = {
    version false
  }

  static constraints = {
	description(nullable:true)  
  }

  Long id
  String name
  String description
  Long home_id
  Integer type_id
  Long x
  Long y

///////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectInfras(lId,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
	def hsSql=[select:'',from:'',where:'',order:''] 
	def hsLong=[:]
	def hsInt=[:]
	def hsString=[:]
	
    hsSql.select="*"
    hsSql.from='homeguidebook'
	hsSql.where="x!=0 and y!=0"+
				((lId>0)?' AND home_id =:id':'')
	hsSql.order="id DESC"
    if(lId>0)
      hsLong['id']=lId

	def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'id',true,Homeguidebook.class)
  }

}