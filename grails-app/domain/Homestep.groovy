class Homestep {  
  def searchService
  static mapping = {
    version false
  }	  
  
  static constraints = {    
  }
  
  Integer id  
  String name
  String name_en
  String term
  String controller
  String action
  String text_yes
  String text_yes_en
  String text_no
  String text_no_en
  Integer stepgroup
  String comments 
  
//////////////////////////////////////////////////////////////////////////////////////////////////
  def csiGetAdvancedhomestep(lHid){
    def hsSql=[select:'',from:'',where:'',order:'']
	def hsLong=[:]
    hsSql.select="""*, notetype.name as nt_name,
				CASE notetype.id 
				  WHEN 3 THEN ( CASE WHEN (SELECT COUNT(*) FROM homephoto WHERE home_id=:lHid)>=notetype.param THEN 1 ELSE 0 END )
				  WHEN 4 THEN ( CASE WHEN (SELECT COUNT(*) FROM homeguidebook WHERE home_id=:lHid)>notetype.param OR IFNULL((SELECT infraoption FROM home WHERE id=:lHid),'')<>'' THEN 1 ELSE 0 END )
          WHEN 5 THEN ( CASE WHEN (SELECT COUNT(*) FROM homevideo WHERE home_id=:lHid)>=notetype.param THEN 1 ELSE 0 END )
				END as is_done"""
    hsSql.from="homestep, notetype"
    hsSql.where="homestep.notetype_id=notetype.id AND notetype.modstatus!=0 AND stepgroup=2"
    hsSql.order="homestep.id"
    hsLong['lHid']=lHid
    return searchService.fetchData(hsSql,hsLong,null,null,null,HomestepAdv.class,-1)
  }
}
