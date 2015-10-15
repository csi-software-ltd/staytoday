class MboxSearch {
  def searchService
  def sessionFactory
  
  static constraints = {    
    valuta_id(nullable:true)
    price_rub(nullable:true)    
    is_telperm(nullable:true)
    is_clfav(nullable:true)
    is_owfav(nullable:true)
    answertype_id(nullable:true)
    timezone(nullable:true)	
    moddate(nullable:true)    
    inputdate(nullable:true)    	
  }
  
  static mapping = {
    table 'DUMMY_NAME'
    version false
    cache false 
  }
  
  Long mid
  Long client_id
  Long user_id
  Long home_id
  Long price_rub
  Long homeclient_id
  
  Integer valuta_id
  Integer modstatus
  Integer client_external
  Integer user_external
  Integer homeperson_id  
  Integer is_telperm
  Integer answertype_id
  Integer timezone
  Integer is_answer
  Integer is_read
  Integer is_readclient
  Integer is_readowner
  Integer is_clfav
  Integer is_owfav
  Integer is_approved

  String client_nickname
  String client_picture
  String user_nickname
  String user_picture
  String home_name
  String home_address
  Integer home_country_id
  String home_city
  String shortaddress
  String homepicture
  String mtext
  String mtextowner
  String linkname
  
  Date date_start
  Date date_end
  Date inputdate  
  Date moddate

  def csiGetMbox(iUserId,iClientId,iModstatusId,iMax,iOffset,bCountUnread=0,iOrd=0){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*,mid as id,ufromid as client_id,ufrom as client_nickname,ufrompicture as client_picture,ufrom_external as client_external,utoid as user_id,uto as user_nickname,utopicture as user_picture,uto_external as user_external,hid as home_id,hname as home_name,address as home_address"
    hsSql.from="v_mbox"
    hsSql.where="(1=1)"+
     ((iUserId)?((iClientId)?" and (((utoid=:iUserId and is_approved=1) and (ufromid=:iClientId)) xor ((utoid=:iClientId) and (ufromid=:iUserId)))":" and ((utoid=:iUserId and is_approved=1) xor (ufromid=:iUserId or ufromid=:soc_id))"):"")+
      ((iModstatusId>0)?" and (modstatus=:iModstatusId) and not((utoid=:iUserId and IFNULL(is_owfav,0)=-1)or(ufromid=:iUserId and IFNULL(is_clfav,0)=-1))":"")+
      ((iModstatusId==0)?" and ((utoid=:iUserId and IFNULL(is_owfav,0)=-1)or(ufromid=:iUserId and IFNULL(is_clfav,0)=-1))":"")+
      ((iModstatusId==-1)?" and not(modstatus=6 or ((utoid=:iUserId and IFNULL(is_owfav,0)=-1)or(ufromid=:iUserId and IFNULL(is_clfav,0)=-1)))":"")+
      ((iModstatusId==-2)?" and ((utoid=:iUserId and IFNULL(is_owfav,0)=1)or(ufromid=:iUserId and IFNULL(is_clfav,0)=1)) and not(modstatus=6)":"")+
      ((bCountUnread&&iUserId)?" and is_read=0 and ((is_answer=1 and (ufromid=:iUserId or ufromid=:soc_id)) or (is_answer=0 and utoid=:iUserId and is_approved=1))":"")
    hsSql.order=((iOrd>0)?"date_start DESC":"moddate DESC")

    def lSocId = 0
    if (User.get(iUserId)?.is_external)
      lSocId = User.findByRef_id(iUserId)?.id?:0

    hsLong['iUserId']=iUserId?:0
    if(iUserId && iClientId)
      hsLong['iClientId']=iClientId
    if(iUserId && !iClientId)
      hsLong['soc_id']=lSocId
    if(iModstatusId>0)
      hsLong['iModstatusId']=iModstatusId

    return searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'mid',true,MboxSearch.class,null)
  }
}
