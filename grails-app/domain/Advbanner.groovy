import grails.util.Holders
class Advbanner {
  def searchService
  def sessionFactory

  Long id
  String bname  
  Date date_start
  Date date_end  
  Long bcount  
  Long bclick
  Integer showlimit
  Integer modstatus
  Integer btype
  Integer bclass
  String burl
  String filename 
  Integer hsize
  Integer vsize
  String altname
  String applettext 
  Long x
  Long y
  Integer zoom
  
  
  String toString() {"${this.bname}"}  

  //////////////////////////////////////////////////////////////////////////////////
  def getBannerByTypeXYZoom(iType,iX,iY,iZoom,iMax,iMaxDistance=0){
    def hsSql=[select:"*",
        from:'advbanner',
        where:'',
        order:'rand()']
    
    def hsInt=['type':iType]
    def hsLong=[:]
    def hsList=[:]

    hsSql.where="""
	  advbanner.btype=:type AND """+
	  ((iX>0&&iY>0)?"distance(x,y,:iX,:iY)/1000<=:iMaxDistance AND advbanner.zoom<=:iZoom AND ":"")+	  
      """advbanner.date_start<=current_date() AND  
      advbanner.date_end>=current_date() AND 
      advbanner.modstatus>0 AND      
      advbanner.bcount < CASE advbanner.showlimit 
                           WHEN 0 THEN 1000000000 ELSE advbanner.showlimit 
                         END 
                 """     
	  if(iX>0&&iY>0){			 
	    hsLong['iZoom']=iZoom
	    hsLong['iX']=iX	 
	    hsLong['iY']=iY	 
		hsLong['iMaxDistance']=iMaxDistance
	  }	
	
    return searchService.fetchData(hsSql,hsLong,hsInt,null,hsList,Advbanner.class,iMax)
  }  
  
  //////////////////////////////////////////////////////////////////////////////////
  def updateShowBanners(lsIds) { 
    if((lsIds?:[]).size()==0)    return
    
    def session = sessionFactory.getCurrentSession()
    def sSql="UPDATE advbanner SET bcount=bcount+1 WHERE id in (:ids)"
    try{
      def qSql=session.createSQLQuery(sSql)
      qSql.setParameterList("ids",lsIds)
      qSql.executeUpdate()
    }catch (Exception e) {
      log.debug("Error Banners:updateShowBanners\n"+e.toString());
    }
    session.clear()  
  }
  //////////////////////////////////////////////////////////////////////////////////
  def updateClickBannersAndRedirect(iId) { 
    if(iId==0)  return Holders.config.default_banner_url
	
	def oBanner=Advbanner.get(iId)
	if((oBanner?.burl?:'').size()==0 && (oBanner?.bclass?:0)==1)
      return Holders.config.default_banner_url
	  
    def session = sessionFactory.getCurrentSession()
    def sSql="UPDATE advbanner SET bclick=bclick+1 WHERE id =:id"
    try{
      def qSql=session.createSQLQuery(sSql)
      qSql.setLong("id",iId)
      qSql.executeUpdate()
    }catch (Exception e) {
      log.debug("Error Banners:updateClickBanners\n"+e.toString());
    }
    session.clear()    
    def sOut=(oBanner==null)?Holders.config.default_banner_url : oBanner.burl
    return (sOut.indexOf('http://')<0)?('http://'+sOut):sOut
  }  
  ///////////////////////////////////////////////////////////////////////////////////////
  def csiDeleteBanner(iId){
    def sSql="""
      DELETE FROM
        advbanner 
      WHERE 
        id =:id
     """
     def session = sessionFactory.getCurrentSession()    
     try{
       def qSql=session.createSQLQuery(sSql)
       qSql.setLong("id",iId)
    
       qSql.executeUpdate()
     }catch (Exception e) {
       log.debug("Error Banners:csiDeleteBanner\n"+e.toString());
     }
     session.clear()
  }  
}
