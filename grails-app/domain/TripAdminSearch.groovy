class TripAdminSearch {
  def searchService

  static mapping = {
    table 'NAME'
    version false
    cache false
  }

  Long id
  Long home_id
  Long user_id
  Long payorder_id
  Date fromdate
  Date todate
  Date inputdate
  Integer homeperson_id
  Long price
  Long price_rub
  Integer valuta_id
  Integer rating
  Integer modstatus
  Integer paystatus
  Integer controlstatus
  Integer rule_cancellation_id
  Long mbox_id
  Integer is_read

  Long owner_id
  String owner_name

  String cl_name

  String home_name
  String home_linkname
  Integer home_country_id
  String home_city
//////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectTrip(lOwnerId,lUserId,lHomeId,iPaystatus,iModstatus,iOrder,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.id as owner_id, u1.nickname as owner_name, u2.nickname as cl_name, h.name as home_name, h.linkname as home_linkname, h.country_id as home_country_id, h.city as home_city"
    hsSql.from='trip t join home h on t.home_id=h.id join user u1 on h.client_id=u1.client_id, user u2'
    hsSql.where="t.user_id=u2.id"+
        (iModstatus>-2?' AND t.modstatus =:modstatus':'')+
        (iPaystatus>-2?' AND t.paystatus =:paystatus':'')+
        (lOwnerId>0?' AND u1.id =:oId':'')+
        (lUserId>0?' AND t.user_id =:user_id':'')+
				(lHomeId>0?' AND t.home_id =:home_id':'')
    hsSql.order=iOrder?"t.fromdate DESC":"t.id DESC"

    if(iModstatus>-2)
      hsLong['modstatus']=iModstatus
    if(iPaystatus>-2)
      hsLong['paystatus']=iPaystatus
    if(lOwnerId>0)
      hsLong['oId']=lOwnerId
    if(lUserId>0)
      hsLong['user_id']=lUserId
    if(lHomeId>0)
      hsLong['home_id']=lHomeId

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'t.id',true,TripAdminSearch.class)
  }

}