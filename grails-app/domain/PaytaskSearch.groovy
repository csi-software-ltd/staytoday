//import org.codehaus.groovy.grails.commons.ConfigurationHolder
class PaytaskSearch {
  def searchService

  static constraints = {
  }
  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }
  Integer id
  Integer paytasktype_id
  String from_code
  String to_code
  String comment
  Date inputdate
  Date moddate
  Integer modstatus
  Long paytrans_id
  Integer summa

  String paytasktype_name

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def csiSelectPaytask(iPaytaskId,iModstatus,iPaytasktypeId,lPaytransId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, paytasktype.name as paytasktype_name"
    hsSql.from='paytask, paytasktype'
    hsSql.where="paytask.paytasktype_id = paytasktype.id"+
        ((iPaytaskId>0)?' AND paytask.id =:paytask_id':'')+
        ((iModstatus>-1)?' AND paytask.modstatus =:modstatus':'')+
        ((iPaytasktypeId>0)?' AND paytask.paytasktype_id =:paytasktype_id':'')+
        ((lPaytransId>0)?' AND paytask.paytrans_id =:paytrans_id':'')
    hsSql.order="paytask.id DESC"

    if(iPaytaskId>0)
      hsLong['paytask_id']=iPaytaskId
    if(iModstatus>-1)
      hsLong['modstatus']=iModstatus
    if(iPaytasktypeId>0)
      hsLong['paytasktype_id']=iPaytasktypeId
    if(lPaytransId>0)
      hsLong['paytrans_id']=lPaytransId

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'paytask.id',true,PaytaskSearch.class)
  }

}