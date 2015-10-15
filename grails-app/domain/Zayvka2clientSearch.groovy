class Zayvka2clientSearch {
  def sessionFactory
  def searchService
  static constraints = {
  }
  static mapping = {
    version false
  }
  Long id
  Long zayvka_id
  Long client_id
  Date todate
  Date inputdate
  Integer modstatus
  
  String email
/////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectZayvka2client(lId){
    def session = sessionFactory.getCurrentSession()
	def hsSql=[select:'',from:'',where:'',order:''] 
	def hsLong=[:]
	def hsInt=[:]
	def hsString=[:]
	
    hsSql.select="*, client.name as email"
    hsSql.from='zayvka2client, client'
	hsSql.where="zayvka2client.client_id = client.id"+
				((lId>0)?' AND zayvka2client.zayvka_id =:lId':'')
	hsSql.order="zayvka2client.inputdate DESC"

    if(lId>0)
      hsLong['lId']=lId

	def hsRes=searchService.fetchData(hsSql,hsLong,null,null,null,Zayvka2clientSearch.class)
  }
}
