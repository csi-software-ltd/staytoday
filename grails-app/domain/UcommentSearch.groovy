import org.codehaus.groovy.grails.commons.ConfigurationHolder

class UcommentSearch {
  def sessionFactory
  def searchService

  static mapping = {
    table 'DUMMY_NAME'
    version false
    cache false 
  }

  Long id 
  Long home_id
  Long user_id
  Integer typeid
  String comtext
  Integer comstatus
  Date comdate
  Integer rating
  Long refcomment_id
  Integer is_mainpage
  
  String nickname
  String picture
  String smallpicture
  Integer is_external

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectUcommentsForHome(lHomeId,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsLong=[:]
	
    hsSql.select="*"
    hsSql.from='ucomment, user'
    hsSql.where="ucomment.user_id = user.id AND comstatus>=0 AND typeid=1"+
				((lHomeId>0)?' AND ucomment.home_id =:home_id':'')
    hsSql.order="ucomment.id DESC"
    if(lHomeId>0)
      hsLong['home_id']=lHomeId

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'ucomment.id',true,UcommentSearch.class)   
  }

  def csiSelectUcommentsForMyHomes(lClientId,iRating,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsLong=[:]
    def hsInt=[:]
	
    hsSql.select="*"
    hsSql.from='ucomment, home, user'
    hsSql.where="home.id=ucomment.home_id AND user.id=ucomment.user_id AND comstatus>=0 AND typeid=1"+
				((lClientId>-1)?' AND home.client_id =:client_id':'')+
				((iRating>-1)?' AND ucomment.rating =:rating':'')
    hsSql.order="ucomment.id DESC"
    if(lClientId>-1)
      hsLong['client_id']=lClientId
    if(iRating>-1)
      hsInt['rating']=iRating

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,hsInt,null,
      null,null,iMax,iOffset,'ucomment.id',true,UcommentSearch.class)          
  }

  def csiSelectUcomments(lHomeId,lUserId,iComstatus,iRating,iTypeid,sInputdate,sInputdateNext,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsLong=[:]
    def hsInt=[:]
    def hsString=[:]
	
    hsSql.select="*"
    hsSql.from='ucomment, user'
    hsSql.where="ucomment.user_id = user.id"+
				((lHomeId>0)?' AND home_id =:home_id':'')+
				((lUserId>0)?' AND user_id =:user_id':'')+
				((iComstatus>-2)?' AND comstatus =:comstatus':'')+
				((iRating>-1)?' AND rating =:rating':'')+
				((iTypeid>0)?' AND typeid =:typeid':'')+
				((sInputdate!='')?' AND comdate >=:inputdate AND comdate <=:inputdateNext':'')
    hsSql.order="ucomment.id DESC"

    if(lHomeId>0)
      hsLong['home_id']=lHomeId
    if(lUserId>0)
      hsLong['user_id']=lUserId
    if(iComstatus>-2)
      hsInt['comstatus']=iComstatus
    if(iRating>-1)
      hsInt['rating']=iRating
    if(iTypeid>0)
      hsInt['typeid']=iTypeid
    if(sInputdate!=''){
      hsString['inputdate']=sInputdate
      hsString['inputdateNext']=sInputdateNext
    }
    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,hsInt,hsString,
      null,null,iMax,iOffset,'ucomment.id',true,UcommentSearch.class)          
  }
  def csiSelectMyUcomments(lId,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsLong=[:]
    def hsInt=[:]
	
    hsSql.select="*"
    hsSql.from='ucomment, user'
    hsSql.where="comstatus>=0 and ucomment.user_id = user.id and typeid>0"+
				((lId>0)?' AND user_id=:lId':'')
    hsSql.order="ucomment.id DESC"
    if(lId>0)
      hsLong['lId']=lId

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'ucomment.id',true,UcommentSearch.class)
  }

  def csiSelectUcommentsOnMe(lId,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsLong=[:]
    def hsInt=[:]
	
    hsSql.select="*"
    hsSql.from='ucomment, user'
    hsSql.where="comstatus>=0 and ucomment.user_id = user.id and typeid=2"+
				((lId>0)?' AND home_id=:lId':'')
    hsSql.order="ucomment.id DESC"
    if(lId>0)
      hsLong['lId']=lId

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'ucomment.id',true,UcommentSearch.class)
  }

}
