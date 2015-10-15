class Citysight {
  def searchService
  
  static mapping = {
    version false
  }
  Integer id
  Integer city_id = 0
  Integer street_id = 0
  Integer district_id = 0
  Integer homecount=0
  Integer radius=0
  Integer x=0
  Integer y=0
  Integer is_index=0
  String name = ''
  String name_en = ''
  String name2 = ''
  String name2_en = ''
  String title = ''
  String title_en = ''
  String keywords = ''
  String description = ''
  String description_en = ''
  String header = ''
  String itext = ''
  String urlname = ''
  Integer modstatus=1
  Integer type = 1

  String toString() {"${this.name}" }
  
  def findCitysight(iCountryId,iPopDirId,iCityId,sName,iMax,iOffset){
    def hsSql=[
      select:"*",
      from:'citysight cs'+((iCountryId || iPopDirId)?',city':'')+(iPopDirId?',region':''),
      where:'1=1'+
      (iCityId?' and cs.city_id=:iCityId':'')+
      (sName?' and cs.name like :sName':'')+
      ((iCountryId || iPopDirId)?' and city.id=cs.city_id':'')+
      (iCountryId?' and city.country_id=:iCountryId':'')+
      (iPopDirId?' and region.popdirection_id=:iPopDirId and city.region_id=region.id':''),
      order:'cs.id DESC']
    
    def hsStr=[:]
    def hsLong=[:]
    
    if(sName!=''){      
      hsStr['sName']='%'+sName+'%'
    }
    if(iCountryId!=0){      
      hsLong['iCountryId']=iCountryId
    }
    if(iPopDirId!=0){     
      hsLong['iPopDirId']=iPopDirId
    }
    if(iCityId!=0){     
      hsLong['iCityId']=iCityId
    }
	//log.debug(hsSql)
	def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsStr,
      null,null,iMax,iOffset,'cs.id',true,Citysight.class)
  }
  def getByIds(lsIds){
    def hsSql=[:]
    def hsList=[ids:lsIds]
    hsSql.select="*"
    hsSql.from="citysight"
    hsSql.where="id in (:ids)"
	  hsSql.order="name ASC"
    return searchService.fetchData(hsSql,null,null,null,hsList,Citysight.class)
  }  
}
