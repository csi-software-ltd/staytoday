import org.codehaus.groovy.grails.commons.ConfigurationHolder
class BannersSearch {
  
  def searchService
  def sessionFactory
  
  static constraints = {
  }
  static mapping = {
    table 'DUMMY_NAME'
    version false
    cache false // виртуальная таблица
  }
  
  String  altname
  String  bname
  Integer btype
  Integer bclass
  String  burl
  String  filename
  Integer modstatus  
  Date date_start
  Date date_end  
  Long bcount
  Long bclick
  Long showlimit 
  String  applettext
  Integer hsize
  Integer vsize  
  Integer isPaid //TODO rename is_paid
  String filenameNew //TODO rename filename_new
  Integer term
  Integer isFileChange //TODO rename is_file_changed
  Integer isParChange  //TODO rename is_par_changed
  Integer isLaunch //TODO rename is_launch
  //-------------------------
  String advbannertypes_name
  Long x
  Long y
  Integer zoom    

  ////////////////////////////////////////////////////////////////////////////////////
  def csiGetBanners(hParams,iMax,iOffset){
    def hsSql=[select:"""
                  *,advbannertypes.name as advbannertypes_name                                  
                """,
               from:'advbanner,advbannertypes',
               where:'advbannertypes.id=advbanner.btype',
               order:((hParams.int.sort==0)?' date_start DESC ': ' date_end ASC ')]
    
    def hsFilter=[where:'',
                  long_par:[:],
                  string_par:[:] ]
    

    if(hParams.int.modstatus>0){
      hsFilter.where+=' AND modstatus=:modstatus'
      hsFilter.long_par['modstatus']=(hParams.int.modstatus==1)?1:0
    }
    if(hParams.int.btype>0){
      hsFilter.where+=' AND btype=:btype'
      hsFilter.long_par['btype']=hParams.int.btype
    }
    if(hParams.int.advbanner_id>0){
      hsFilter.where+=' AND advbanner.id=:advbanner_id'
      hsFilter.long_par['advbanner_id']=hParams.int.advbanner_id
    }
    if(hParams.string.datefrom!=''){
      hsFilter.where+=' AND date_start>=:datefrom'
      hsFilter.string_par['datefrom']=hParams.string.datefrom    
    }
    if(hParams.string.dateto!=''){
      hsFilter.where+=' AND date_end<=:dateto'
      hsFilter.string_par['dateto']=hParams.string.dateto    
    }
    
   
    return searchService.fetchDataByPages(hsSql,hsFilter,null,null,null,null,null,iMax,iOffset,
           'advbanner.id',true,BannersSearch.class)
  }
  //////////////////////////////////////////////////////////////////////////////////
  def csiSetBanner(hParams){
    def bNew=false
    if((hParams==null)||(hParams.int==null))
      return -1
    if((hParams.long.id?:0)==0)
      bNew=true;
    def bFileExists=(hParams.string['filename']?:''!='')

    def sSql
    if(bNew){
      sSql=("""
          INSERT INTO 
            advbanner(modstatus,btype,showlimit,
                hsize,vsize,bname,altname,burl,date_start,date_end
            """+(bFileExists?',filename,bclass':'')+
			   (((hParams.int.btype?:0)==2)?',zoom,x,y':'')+
            """       ) VALUES (
          :modstatus,:btype,
          :showlimit,:hsize,:vsize,:bname,
          :altname,:burl,:datefrom,:dateto
            """+(bFileExists?',:filename,:bclass ':'')+
			    (((hParams.int.btype?:0)==2)?',:zoom,:x,:y':'')+
			")")
    }else{
      sSql=("""
        UPDATE 
          advbanner 
        SET                     
          modstatus=:modstatus,
          btype=:btype,          
          showlimit=:showlimit,
          hsize=:hsize,
          vsize=:vsize,
          bname=:bname,          
          altname=:altname,
          burl=:burl,
          date_start=:datefrom,
          date_end=:dateto
          """+(bFileExists?',filename=:filename,bclass=:bclass ':'')+
		      (((hParams.int.btype?:0)==2)?',zoom=:zoom,x=:x,y=:y ':'')+			  
          """
        WHERE 
          id =:id
       """)
    } 
    
    def session = sessionFactory.getCurrentSession()    
    try{
      def qSql=session.createSQLQuery(sSql)
      if(!bNew)
        qSql.setLong("id",hParams.long.id)      
      qSql.setInteger("modstatus",hParams.int.modstatus)
      qSql.setInteger("btype",hParams.int.btype)
      qSql.setLong("showlimit",hParams.long.showlimit)
      qSql.setInteger("hsize",hParams.int.hsize)
      qSql.setInteger("vsize",hParams.int.vsize)            
      qSql.setString("bname",hParams.string.bname)        
      qSql.setString("altname",hParams.string.altname)
      qSql.setString("burl",hParams.string.burl)
      qSql.setString("datefrom",hParams.string.datefrom)
      qSql.setString("dateto",hParams.string.dateto)
      if(bFileExists){
        qSql.setString("filename",hParams.string.filename)
        qSql.setInteger("bclass",hParams.int.bclass)
      }
      if((hParams.int.btype?:0)==2){
        qSql.setLong("x",hParams.long.x)
        qSql.setLong("y",hParams.long.y)
        qSql.setInteger("zoom",hParams.int.zoom)
      }	  
        
      qSql.executeUpdate()
    }catch (Exception e) {
      log.debug("Error Banners:csiSetBanner\n"+e.toString());
    }
    session.clear()
    
    return bNew? searchService.getLastInsert() : hParams.long.id; 
  }
}
