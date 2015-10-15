class AdressSearch {
  def searchService
  
  static mapping = {    
    table 'DUMMY_NAME'
    version false
    cache false 
  }	
  
  Long id
  String name
  String region
  String district
  
//////////////////////////////////////////////////////////////////////////////////////////////

  def getDistrictList(iCountryId,iRegionId,sName,iMax,iOffset){
    def hsSql=[
      select:"district.id,district.name AS name,region.name AS region,district.name AS district",
      from:'district, region',
      where:'district.region_id=region.id',
      order:'district.id DESC']
    
    def hsStr=[:]
    def hsLong=[:]
    
    if(sName!=''){
      hsSql.where+=' AND district.name like :name'
      hsStr['name']='%'+sName+'%'
    }
    if(iCountryId!=0){
      hsSql.where+=' AND region.country_id=:country'
      hsLong['country']=iCountryId
    }
    if(iRegionId!=0){
      hsSql.where+=' AND district.region_id=:region'
      hsLong['region']=iRegionId
    }
	
	def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsStr,
      null,null,iMax,iOffset,'district.id',true,AdressSearch.class)
  }

  def getCityList(iCountryId,iRegionId,sName,sDistrictName,iMax,iOffset){
    def hsSql=[
      select:"city.id,city.name AS name,region.name AS region, IFNULL(city.name2, '') AS district",
      from:'city, region',
      where:'city.region_id=region.id',
      order:'city.id DESC']
    
    def hsStr=[:]
    def hsLong=[:]
    
    if(sName!=''){
      hsSql.where+=' AND city.name like :name'
      hsStr['name']='%'+sName+'%'
    }
    if(iCountryId!=0){
      hsSql.where+=' AND region.country_id=:country'
      hsLong['country']=iCountryId
    }
    if(iRegionId!=0){
      hsSql.where+=' AND city.region_id=:region'
      hsLong['region']=iRegionId
    }
	
	def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsStr,
      null,null,iMax,iOffset,'city.id',true,AdressSearch.class)
  }

  def getStreetList(iCountryId,iRegionId,sName,iMax,iOffset){
    def hsSql=[
      select:"street.id,street.name AS name,region.name AS region,street.name AS district",
      from:'street, region',
      where:'street.region_id=region.id',
      order:'street.id DESC']
    
    def hsStr=[:]
    def hsLong=[:]
    
    if(sName!=''){
      hsSql.where+=' AND street.name like :name'
      hsStr['name']='%'+sName+'%'
    }
    if(iCountryId!=0){
      hsSql.where+=' AND region.country_id=:country'
      hsLong['country']=iCountryId
    }
    if(iRegionId!=0){
      hsSql.where+=' AND street.region_id=:region'
      hsLong['region']=iRegionId
    }
	
    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsStr,
      null,null,iMax,iOffset,'street.id',true,AdressSearch.class)
  }

  def getCountryList(sName,iMax,iOffset){
    def hsSql=[
      select:"country.id,country.name AS name,country.name AS region,country.name AS district",
      from:'country',
      where:'1=1',
      order:'country.regorder asc, country.name asc']

    def hsStr=[:]

    if(sName!=''){
      hsSql.where+=' AND country.name like :name'
      hsStr['name']='%'+sName+'%'
    }

    def hsRes=searchService.fetchDataByPages(hsSql,null,null,null,hsStr,
      null,null,iMax,iOffset,'country.id',true,AdressSearch.class)
  }

  def getRegionList(iCountryId,sName,iMax,iOffset){
    def hsSql=[
      select:"region.id,country.name AS name,region.name AS region,region.popdirection_id AS district",//popdirection_id as district!!!
      from:'region, country',
      where:'region.country_id=country.id',
      order:'region.id DESC']

    def hsStr=[:]
    def hsLong=[:]

    if(sName!=''){
      hsSql.where+=' AND region.name like :name'
      hsStr['name']='%'+sName+'%'
    }
    if(iCountryId!=0){
      hsSql.where+=' AND region.country_id=:country'
      hsLong['country']=iCountryId
    }

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsStr,
      null,null,iMax,iOffset,'region.id',true,AdressSearch.class)
  }

}