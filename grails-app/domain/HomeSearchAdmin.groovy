class HomeSearchAdmin {
  def searchService

  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }

  Long id
  Long client_id
  Long pricestandard
  String name
  String linkname
  String city
  Integer country_id
  Integer region_id
  Integer pricestatus
  Integer modstatus
  Integer is_confirmed
  Date inputdate
  Date moddate
  Integer is_mainpage
  Integer is_specoffer
  Integer unrealiable

  String mainpicture
  Integer picture_cnt

  String client_name

  Long user_id

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectHomes(lId,iCountry_id,iRegion_id,iModstatus,iIs_confirmed,iIs_mainpage,iIs_specoffer,sInputdate,sInputdateNext,lUserid,sClient_name,iReserve,iComm,iHotdiscount,iLongdiscount,sLinkname,iCity_id,iOrder,iMax,iOffset,bGroup=false,iPopDirId=0,lsRegionIds=[],iUnrealiable=0){
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsLong=[:]
    def hsInt=[:]
    def hsString=[:]
	
    hsSql.select="*, client.name AS client_name, ifnull((SELECT COUNT(*) from homephoto where home_id = home.id ),0) as picture_cnt, user.id as user_id"
    hsSql.from='home, client, user'
    hsSql.where="home.client_id = client.id and user.client_id=client.id"+
				((lId>0)?' AND home.id =:id':'')+
				((iCountry_id>0)?' AND home.country_id =:country_id':'')+
				((iRegion_id>0)?' AND region_id =:region_id':'')+
        ((iCity_id>0)?' AND city_id =:city_id':'')+        
				((iModstatus>-5)?' AND home.modstatus =:modstatus':'')+
				((iIs_confirmed>-1)?' AND is_confirmed =:is_confirmed':'')+
				((iIs_mainpage>-1)?' AND is_mainpage =:is_mainpage':'')+
				((iIs_specoffer>-1)?' AND is_specoffer =:is_specoffer':'')+
				((sInputdate!='')?' AND home.inputdate >=:inputdate AND home.inputdate <=:inputdateNext':'')+
				((lUserid>0)?' AND user.id =:user_id':'')+
        ((iReserve>0)?' AND client.is_reserve=1':'')+
        ((iComm>0)?' AND home.nref>0':'')+
        ((iUnrealiable>0)?' AND home.unrealiable>0':'')+
        ((sClient_name!='')?' AND (client.name like CONCAT("%",:client_name) OR client.name like CONCAT(:client_name,"%") OR client.name like CONCAT("%",:client_name,"%"))':'')+
				((sLinkname!='')?' AND (home.linkname like CONCAT("%",:linkname) OR home.linkname like CONCAT(:linkname,"%") OR home.linkname like CONCAT("%",:linkname,"%"))':'')+
        ((iHotdiscount>0)?' AND 1=IFNULL((select modstatus from homediscount where modstatus=1 and homediscount.id = home.hotdiscount_id),0)':'')+
        ((iLongdiscount>0)?' AND 1=IFNULL((select modstatus from homediscount where modstatus=1 and homediscount.id = home.longdiscount_id),0)':'')+
        ((!iRegion_id && iPopDirId)?' AND region_id IN (select id from region where popdirection_id=:popdirection_id)':'')+
        (lsRegionIds?' AND region_id in (:lsRegionIds)':'')
   
   hsSql.order=(iOrder)?"client.name":"home.inputdate DESC"
    if(bGroup)
      hsSql.group="home.client_id"

    if(lUserid>0)
      hsLong['user_id']=lUserid
    if(lId>0)
      hsLong['id']=lId
    if(iCountry_id>0)
      hsInt['country_id']=iCountry_id
    if(iRegion_id>0)
      hsInt['region_id']=iRegion_id
    if(iCity_id>0)
      hsInt['city_id']=iCity_id
    if(iModstatus>-5)
      hsInt['modstatus']=iModstatus
    if(iIs_confirmed>-1)
      hsInt['is_confirmed']=iIs_confirmed
    if(iIs_mainpage>-1)
      hsInt['is_mainpage']=iIs_mainpage
    if(iIs_specoffer>-1)
      hsInt['is_specoffer']=iIs_specoffer
    if(sInputdate!=''){
      hsString['inputdate']=sInputdate
      hsString['inputdateNext']=sInputdateNext
    }
    if(sClient_name!='')
      hsString['client_name']=sClient_name
    if(sLinkname!='')
      hsString['linkname']=sLinkname
    if(!iRegion_id && iPopDirId)
      hsInt['popdirection_id']=iPopDirId
     
    def hsList = [:]     
    if(lsRegionIds)  
      hsList['lsRegionIds']=lsRegionIds
//log.debug(hsSql)
	def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,hsInt,hsString,
      hsList,null,iMax,iOffset,'home.id',true,HomeSearchAdmin.class,bGroup?['home.client_id']:[])
  }
}
