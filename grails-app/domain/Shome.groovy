//import org.codehaus.groovy.grails.commons.ConfigurationHolder
class Shome {
  def searchService
 
  static mapping = {
    version false
  }

  static constraints = {
  }
  Integer id
  Integer city_id
  Integer modstatus
  Integer type_id   
  Integer homecount=0
  String header
  String header_en
  String title
  String title_en
  String keywords
  String keywords_en  
  String description
  String description_en
  String promotext
  String promotext_en  
  String itext
  String itext_en
  String itext2
  String itext2_en
  String linkname
  String linkname_en
  
  
/////////////////////////////////constructor//////////////////////////////////////////////////////////////////////
  Shome csiSetData(lsRequest){
    if(!lsRequest?.id)
      modstatus=1
    
    city_id=lsRequest?.city_id?:0  
    type_id=lsRequest?.type_id?:0  
   
    header=lsRequest?.header?:''
    header_en=lsRequest?.header_en?:''
    title=lsRequest?.title?:''
    title_en=lsRequest?.title_en?:''
    keywords=lsRequest?.keywords?:''
    keywords_en=lsRequest?.keywords_en?:''    
    description=lsRequest?.description?:''
    description_en=lsRequest?.description_en?:''
    promotext=lsRequest?.promotext?:''
    promotext_en=lsRequest?.promotext_en?:''    
    itext=lsRequest?.itext?:''
    itext_en=lsRequest?.itext_en?:''
    itext2=lsRequest?.itext2?:''
    itext2_en=lsRequest?.itext2_en?:''
    linkname=lsRequest?.linkname?:''
    linkname_en=lsRequest?.linkname_en?:''
  
    this
  }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def csiSelectSHomes(iCityId,iTypeId,iModstatus,iMax,iOffset){   
    def hsSql=[select:'',from:'',where:'',order:'']    
    def hsInt=[:]
 
    hsSql.select="*"
    hsSql.from="shome"
    hsSql.where="1=1"+
	  ((iCityId>0)?" and city_id=:iCityId":"")+	 
    ((iTypeId)?" and type_id=:iTypeId":"")+    
    ((iModstatus)?" and modstatus=:iModstatus":"")        	
    hsSql.order="id DESC"
	
    if(iCityId>0)
      hsInt['iCityId']=iCityId
    if(iModstatus>0)
      hsInt['iModstatus']=iModstatus
    if(iTypeId)
      hsInt['iTypeId']=iTypeId
    return searchService.fetchDataByPages(hsSql,null,null,hsInt,null,null,null,iMax,iOffset,'id',true, Shome.class,null)	  
  }

}
