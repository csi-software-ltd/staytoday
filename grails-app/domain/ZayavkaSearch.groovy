import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ZayavkaSearch {
  def sessionFactory
  def searchService
  
  static constraints = {
  }
  static mapping = {
    table 'DUMMY_NAME'
    version false
    cache false
  }

  Long id
  Long user_id
  Integer country_id
  Integer region_id
  String city
  Date date_start
  Date date_end
  Date inputdate
  Integer homeperson_id
  Long pricefrom
  Long pricefrom_rub
  Long priceto
  Long priceto_rub
  Integer valuta_id
  Integer hometype_id
  String ztext
  String mobtel
  Integer timetodecide_id
  Integer modstatus
  Integer is_auto
  Date last_updated
  
  String user_nickname
  String user_email
  String user_smallpicture
  Integer is_external
  
  Integer kolprop
  
  Date todate
  Integer modst
  Long z2C_id
  Long baseclient_id

  String toString() {"${this.ztext}" }
//////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectZayavka(lId,iCountry_id,iRegion_id,iModstatus,sCity,iAuto,iMax,iOffset,lsRegionIds=[]){    
	def hsSql=[select:'',from:'',where:'',order:''] 
	def hsLong=[:]
	def hsInt=[:]
	def hsString=[:]
  def hsList = [:] 
	
    hsSql.select="*, user.is_external as is_external, user.smallpicture as user_smallpicture, user.nickname as user_nickname, user.email as user_email, (select count(*) from zayvka2client where zayvka_id=zayavka.id and modstatus>=0) as kolprop, zayavka.last_updated as todate, zayavka.modstatus as modst, zayavka.id as z2C_id"
    hsSql.from='zayavka, user'
    hsSql.where="zayavka.user_id = user.id and zayavka.is_auto =:is_auto"+
				((lId>0)?' AND zayavka.id =:lId':'')+
				((iCountry_id>0)?' AND zayavka.country_id =:iCountry_id':'')+
				((iRegion_id>0)?' AND zayavka.region_id =:iRegion_id':'')+
        ((iModstatus>-5)?' AND zayavka.modstatus =:iModstatus':'')+
				((sCity!='')?' AND (zayavka.city like CONCAT("%",:sCity) OR zayavka.city like CONCAT(:sCity,"%") OR zayavka.city like CONCAT("%",:sCity,"%"))':'')+
        (lsRegionIds?' AND region_id in (:lsRegionIds)':'')
    hsSql.order="zayavka.inputdate DESC"

    hsLong['is_auto']=iAuto
    if(lId>0)
      hsLong['lId']=lId
    if(iCountry_id>0)
      hsInt['iCountry_id']=iCountry_id
    if(iRegion_id>0)
      hsInt['iRegion_id']=iRegion_id
    if(iModstatus>-5)
      hsInt['iModstatus']=iModstatus
    if(sCity!='')
      hsString['sCity']=sCity        
    if(lsRegionIds)  
      hsList['lsRegionIds']=lsRegionIds
    
	  def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,hsInt,hsString,
      hsList,null,iMax,iOffset,'zayavka.id',true,ZayavkaSearch.class)
  }

  def csiSelectZayavka2client(lClientId,iMax,iOffset){    
	def hsSql=[select:'',from:'',where:'',order:''] 
	def hsLong=[:]
	def hsInt=[:]
	def hsString=[:]
	
    hsSql.select="*, user.is_external as is_external, user.smallpicture as user_smallpicture, user.nickname as user_nickname, user.email as user_email, 0 as kolprop, zayvka2client.todate as todate, zayvka2client.modstatus as modst, zayvka2client.id as z2C_id"
    hsSql.from='zayavka, user, zayvka2client'
	hsSql.where="zayavka.user_id = user.id AND zayavka.id = zayvka2client.zayvka_id AND zayvka2client.modstatus>=0 AND zayvka2client.client_id =:lClientId"
	hsSql.order="zayavka.inputdate DESC"

	hsLong['lClientId']=lClientId

	def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'zayvka2client.id',true,ZayavkaSearch.class)
  }
}