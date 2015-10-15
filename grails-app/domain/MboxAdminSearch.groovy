class MboxAdminSearch {
  def searchService

  static constraints = {
  }

  static mapping = {
    table 'DUMMY_NAME'
    version false
    cache false
  }

//////////mbox part//////////
  Long id
  Date date_start
  Date date_end
  String mtext
  Long price_rub
  Integer is_telperm
  Integer answertype_id
  Long home_id
  Integer modstatus
  Integer is_answer
  Integer is_read
  Integer is_approved
  Integer is_adminread
  Date inputdate
  Date moddate
  Date lastusermessagedate
  Long zayvka_id
//////////home part//////////
  String home_name
  String homepicture
  String linkname
  String home_city
  String home_country_id
  Long homeclient_id
  Integer home_region_id
//////////owner part/////////
  Long owner_id
  String owner_nickname
  String owner_picture
  Integer owner_external
  Integer is_reserve
  Integer reserve_id
//////////client part////////
  Long client_id
  String client_nickname
  String client_picture
  Integer client_external
  String client_email
  String owner_email

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def getMboxList(lId,lHomeId,lUserId,iModstatus,iTypeid,iAnswer,iReserve,iRead,sDateStart,sDateEnd,bIsStat,
                  iOrd,iMax,iOffset,lOwnerId=0l,iIsApproved=-2,iReserveId=0,lsRegionIds=[],sOwner,sUser){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]
    def hsList = [:] 

    hsSql.select="""*,mid as id,ufromid as client_id,ufrom as client_nickname,ufrompicture as client_picture,
                  ufrom_external as client_external,utoid as owner_id,uto as owner_nickname,utopicture as owner_picture,
                  uto_external as owner_external,hname as home_name,hid as home_id,ufrom_email as client_email, uto_email as owner_email"""
    hsSql.from="v_mbox"
    hsSql.where="(1=1)"+
            ((iReserve>0)?" and is_reserve=1":"")+
            ((iRead>-1)?" and is_read=:iRead":"")+
            ((iAnswer>-1)?" and is_answer=:iAnswer":"")+
            ((iTypeid==1)?" and date_start>CURRENT_DATE":(iTypeid==2)?" and date_start<CURRENT_DATE":"")+
            ((iModstatus!=0)?" and modstatus=:iModstatus":"")+
            ((lUserId>0)?" and ufromid=:lUserId":"")+
            ((lHomeId>0)?" and hid=:lHomeId":"")+
            ((lId>0)?" and mid=:lId":"")+
            ((sDateStart)?" and DATE(inputdate)>=:date_start":"")+
            ((sDateEnd)?" and DATE(inputdate)<=:date_end":"")+
            ((bIsStat>3)?" and modstatus=:modstatus":"")+
            ((lOwnerId>0)?" and utoid=:owner_id":"")+
            ((iIsApproved>-2)?" and is_approved=:is_approved":"")+
            ((iReserveId>0)?" and reserve_id=:reserve_id":"")+
            (lsRegionIds?' AND home_region_id in (:lsRegionIds)':'')+
            (sUser?" AND ufrom_email=:sUser":"")+
            (sOwner?" AND uto_email=:sOwner":"")
            
            
    hsSql.order=((iOrd==1)?"date_start DESC":(iOrd==2)?"mid DESC":"lastusermessagedate DESC")

    if(bIsStat==1)
      hsSql.group="ufromid"
    if(bIsStat==2)
      hsSql.group="utoid"
    if(bIsStat>3){
      hsSql.group="mid"
      hsLong['modstatus']=bIsStat-3-1
    }

    if(iRead>-1)
      hsLong['iRead']=iRead
    if(iAnswer>-1)
      hsLong['iAnswer']=iAnswer
    if(iModstatus!=0)
      hsLong['iModstatus']=iModstatus
    if(lUserId>0)
      hsLong['lUserId']=lUserId
    if(lHomeId>0)
      hsLong['lHomeId']=lHomeId
    if(lId>0)
      hsLong['lId']=lId
    if(sDateStart)
      hsString['date_start']=sDateStart
    if(sDateEnd)
      hsString['date_end']=sDateEnd
    if(lOwnerId>0)
      hsLong['owner_id']=lOwnerId
    if(iIsApproved>-2)
      hsLong['is_approved']=iIsApproved
    if(iReserveId>0)
      hsLong['reserve_id']=iReserveId
        
    if(lsRegionIds)  
      hsList['lsRegionIds']=lsRegionIds 

    if(sUser)
      hsString['sUser']=sUser
    if(sOwner)
      hsString['sOwner']=sOwner

    return searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,hsList,null,
                                          iMax,iOffset,'mid',true,MboxAdminSearch.class)
  }

}
