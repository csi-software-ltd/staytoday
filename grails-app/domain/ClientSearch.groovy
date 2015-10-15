class ClientSearch {
  def searchService

  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }

  Long id
  String name
  Integer resstatus
  Integer country_id
  Integer type_id

  String nickname
  String countryname

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectClients(lId,iCountry_id,iResstatus,sNickname,sName,iTypeId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsLong=[:]
    def hsString=[:]
	
    hsSql.select="*,user.nickname as nickname, IFNULL(country.name, 'not set') as countryname"
    hsSql.from='client left join country on client.country_id = country.id,user'
    hsSql.where="client.id = user.client_id"+
        ((lId>0)?' AND client.id =:id':'')+
        ((iCountry_id>0)?' AND country.id =:country_id':'')+
        ((iTypeId>0)?' AND client.type_id =:type_id':'')+
        ((sName!='')?' AND (client.name like CONCAT("%",:client_name) OR client.name like CONCAT(:client_name,"%") OR client.name like CONCAT("%",:client_name,"%"))':'')+
        ((sNickname!='')?' AND (user.nickname like CONCAT("%",:nickname) OR user.nickname like CONCAT(:nickname,"%") OR user.nickname like CONCAT("%",:nickname,"%"))':'')+
        ((iResstatus==-2)?' AND (client.resstatus = 2 or client.resstatus = 3)':(iResstatus>-2)?' AND client.resstatus =:resstatus':'')
    hsSql.order="client.inputdate DESC"

    if(lId>0)
      hsLong['id']=lId
    if(iCountry_id>0)
      hsLong['country_id']=iCountry_id
    if(iTypeId>0)
      hsLong['type_id']=iTypeId
    if(sName!='')
      hsString['client_name']=sName
    if(sNickname!='')
      hsString['nickname']=sNickname
    if(iResstatus>-2)
      hsLong['resstatus']=iResstatus

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'client.id',true,ClientSearch.class)
  }
}