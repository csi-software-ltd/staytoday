class TripSearch {  
  def searchService

  static mapping = {
    table 'NAME'
    version false
    cache false
  }	  
  
  static constraints = {    
  }
  
  Long id
  Long home_id
  Long user_id
  Date fromdate
  Date todate
  Integer homeperson_id
  Long price
  Long price_rub
  Integer valuta_id
  Integer rating
  Integer modstatus
  Integer paystatus
  Integer rule_cancellation_id
  Long mbox_id
  
  String user_nickname
  Long owner_id
  
  String home_name
  String home_address
  String home_mainpicture
  String home_linkname
  Integer home_country_id
  String home_city
  Long home_client_id
  
//////////////////////////////////////////////////////////////////////////////////////////////////

  def csiGetTrip(lId,lSocId,iModstatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsLong=[:]

    hsSql.select="*,home.city as home_city,home.country_id as home_country_id, home.linkname as home_linkname, home.name as home_name, home.address as home_address, home.mainpicture as home_mainpicture, user.nickname as user_nickname, user.id as owner_id, home.client_id as home_client_id"
    hsSql.from='trip, home, user, client'
    hsSql.where="trip.home_id = home.id AND home.client_id = client.id AND user.client_id = client.id AND (trip.user_id =:id OR trip.user_id =:soc_id)"+
				((iModstatus==0)?' AND (trip.modstatus=0 OR trip.modstatus=1)':'')+
				((iModstatus>1)?' AND trip.modstatus =:modstatus':'')
    hsSql.order="trip.fromdate DESC"
	
    hsLong['id']=lId
    hsLong['soc_id']=lSocId
    if(iModstatus>1)
      hsLong['modstatus']=iModstatus

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'trip.id',true,TripSearch.class)
  }

  def csiGetBron(lCl_Id,iModstatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*,home.city as home_city,home.country_id as home_country_id, home.linkname as home_linkname, home.name as home_name, home.address as home_address, home.mainpicture as home_mainpicture, user.nickname as user_nickname, user.id as owner_id, home.client_id as home_client_id"
    hsSql.from='trip, home, user'
    hsSql.where="trip.home_id = home.id AND home.client_id=:lCl_Id AND trip.user_id = user.id"+
				((iModstatus==1)?' AND trip.modstatus=0':'')+
				((iModstatus==2)?' AND trip.modstatus>0':'')+
				((iModstatus==-1)?' AND trip.modstatus=-1':'')
    hsSql.order="trip.inputdate DESC"
	
	  hsLong['lCl_Id']=lCl_Id

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'trip.id',true,TripSearch.class)
  }
}
