class MboxrecSearch {
  def searchService

  static constraints = {
  }

  static mapping = {
    table 'DUMMY_NAME'
    version false
    cache false
  }

  Long id
  Long mbox_id
  Long user_id
  Long client_id
  Long home_id
  Long rechome_id
  Integer valuta_id
  Long price_rub

  Integer client_external
  Integer user_external
  Integer homeperson_id
  Integer answertype_id
  Integer is_answer
  Integer rule_minday_id
  Integer rule_maxday_id
  Integer rechomeperson_id
  Integer admin_id
  Integer is_system

  String client_nickname
  String client_picture
  String user_nickname
  String user_picture
  String home_name
  Integer home_country_id
  String home_city
  String address
  String rectext
  String linkname

  Date recdate_start
  Date recdate_end
  Date date_start
  Date date_end
  Date moddate

  def csiGetList(iMboxId,iUserId,iClientId,iAnswer,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*,ufrom_id as client_id,ufrom_name as client_nickname,ufrom_picture as client_picture,ufrom_external as client_external,uto_id as user_id,uto_name as user_nickname,uto_picture as user_picture,uto_external as user_external"
    hsSql.from="v_mboxrec"
    hsSql.where="(mbox_id=:iMboxId) and ((is_approved=1 and is_answer=:is_answer1) or (is_answer=:is_answer and admin_id=0))"+
      ((iUserId>0)?" and (((uto_id=:iUserId) and (ufrom_id=:iClientId)) xor ((uto_id=:iClientId) and (ufrom_id=:iUserId)))":"")
    hsSql.order="id DESC"

    hsLong['iMboxId']=iMboxId?:0
    hsLong['is_answer'] = iAnswer
    hsLong['is_answer1'] = (iAnswer+1)%2
    if(iUserId>0){
      hsLong['iUserId']=iUserId
      hsLong['iClientId']=iClientId?:0
    }

    /*def lSocId = 0
    if (User.get(iUserId)?.is_external)
      lSocId = User.findByRef_id(iUserId)?.id?:0
	hsLong['soc_id']=lSocId*/

    return searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'id',true,MboxrecSearch.class,null)
  }

}