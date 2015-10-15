class HomeSearchMods {
  def searchService
  def sessionFactory  
  
  static mapping = {    
    table 'NAME'
    version false
    cache false 
  }	
  
  Long id
  Integer is_step_descr
  Integer is_step_map
  Integer is_step_photo
  Integer is_step_price
  Integer modstatus
  
  Integer user_modstatus
  String user_tel
  
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  
  def csiSelectHomes(){
    def session = sessionFactory.getCurrentSession()
	def hsSql=[select:'',from:'',where:'',order:''] 
	def hsLong=[:]
	def hsInt=[:]
	def hsString=[:]
	
    hsSql.select="*, user.modstatus AS user_modstatus, user.tel AS user_tel"
    hsSql.from='home, user'
	hsSql.where="home.client_id = user.client_id AND (home.modstatus=1 OR home.modstatus=3)"
	hsSql.order="home.inputdate DESC"

	def hsRes=searchService.fetchData(hsSql,null,null,null,null,HomeSearchMods.class)
  }
}