//import org.codehaus.groovy.grails.commons.ConfigurationHolder
class PayorderSearch {
  def searchService

  static constraints = {
  }
  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }
  Long id
  String norder
  Integer agr_id
  Long client_id
  Long home_id
  Integer payway_id
  Integer summa
  Integer summa_own
  Date inputdate
  Integer modstatus
  String plat_name

  String agr_name
  String payway_name

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def csiSelectPayorder(sNorder,iModstatus,iPaywayId,iAgrId,lClientId,lHomeId,sPlatName,sInputdateFrom,sInputdateTo,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, agr.name as agr_name, payway.name as payway_name"
    hsSql.from='payorder, agr, payway'
    hsSql.where="agr.id = payorder.agr_id and payway.id=payorder.payway_id"+
        ((sNorder!='')?' AND norder like CONCAT("%",:norder,"%")':'')+
        ((iModstatus>-2)?' AND payorder.modstatus =:modstatus':'')+
        ((iPaywayId>0)?' AND payway_id =:payway_id':'')+
        ((iAgrId>0)?' AND payorder.agr_id =:agr_id':'')+
        ((lClientId>0)?' AND client_id =:client_id':'')+
        ((lHomeId>0)?' AND home_id =:home_id':'')+
        ((sPlatName!='')?' AND plat_name like CONCAT("%",:plat_name,"%")':'')+
        ((sInputdateFrom!='')?' AND inputdate >=:inputdateFrom':'')+
        ((sInputdateTo!='')?' AND inputdate <=:inputdateTo':'')
    hsSql.order="payorder.id DESC"

    if(sNorder!='')
      hsString['norder']=sNorder
    if(iModstatus>-2)
      hsInt['modstatus']=iModstatus
    if(iPaywayId>0)
      hsInt['payway_id']=iPaywayId
    if(iAgrId>0)
      hsInt['agr_id']=iAgrId
    if(lClientId>0)
      hsLong['client_id']=lClientId
    if(lHomeId>0)
      hsLong['home_id']=lHomeId
    if(sPlatName!='')
      hsString['plat_name']=sPlatName
    if(sInputdateFrom!='')
      hsString['inputdateFrom']=sInputdateFrom
    if(sInputdateTo!='')
      hsString['inputdateTo']=sInputdateTo

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,hsInt,hsString,
      null,null,iMax,iOffset,'payorder.id',true,PayorderSearch.class)
  }

}