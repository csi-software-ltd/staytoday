//import org.codehaus.groovy.grails.commons.ConfigurationHolder
class PaytransSearch {
  def searchService

  static constraints = {
  }
  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }
  Long id
  String agrcode
  Long payorder_id
  Long account_id
  Integer paytype_id
  Integer summa
  Integer summa_val
  Integer valuta_id
  Date moddate
  Integer modstatus
  String returncode
  String comment

  String paytype_name
  Long client_id

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def csiSelectPaytrans(lPayorderId,iModstatus,lAccountId,iPaytypeId,lClientId,sModdateFrom,sModdateTo,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, paytype.name as paytype_name, account.client_id as client_id"
    hsSql.from='paytrans, account, paytype'
    hsSql.where="account.id = paytrans.account_id and paytype.id = paytrans.paytype_id"+
        ((lPayorderId>0)?' AND paytrans.payorder_id =:payorder_id':'')+
        ((iModstatus>-1)?' AND paytrans.modstatus =:modstatus':'')+
        ((lAccountId>0)?' AND paytrans.account_id =:account_id':'')+
        ((iPaytypeId>0)?' AND paytrans.paytype_id =:paytype_id':'')+
        ((lClientId>0)?' AND account.client_id =:client_id':'')+
        ((sModdateFrom!='')?' AND paytrans.moddate >=:moddateFrom':'')+
        ((sModdateTo!='')?' AND paytrans.moddate <=:moddateTo':'')
    hsSql.order="paytrans.id DESC"

    if(lPayorderId>0)
      hsLong['payorder_id']=lPayorderId
    if(iModstatus>-1)
      hsLong['modstatus']=iModstatus
    if(lAccountId>0)
      hsLong['account_id']=lAccountId
    if(iPaytypeId>0)
      hsLong['paytype_id']=iPaytypeId
    if(lClientId>0)
      hsLong['client_id']=lClientId
    if(sModdateFrom!='')
      hsString['moddateFrom']=sModdateFrom
    if(sModdateTo!='')
      hsString['moddateTo']=sModdateTo

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'paytrans.id',true,PaytransSearch.class)
  }

}