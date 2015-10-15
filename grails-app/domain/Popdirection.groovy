class Popdirection {  
  def searchService

  static mapping = {
  }	  

  static constraints = {
    moddate(nullable:true)
    keyword(nullable:true)
    itext(nullable:true)
    ceo_title(nullable:true)
    ceo_keywords(nullable:true)
    ceo_description(nullable:true)
    ceo_description_en(nullable:true)
    modstatus(nullable:true)
    name_en(nullable:true)
    name2_en(nullable:true)
    header_en(nullable:true)
    annotation_en(nullable:true)
    ceo_title_en(nullable:true)
    ceo_title_cot_en(nullable:true)
    ceo_description_cot(nullable:true)
    ceo_description_cot_en(nullable:true)
    header_cot_en(nullable:true)
    annotation_cot_en(nullable:true)
    linkname(blank:false,nullable:false)
  }
  
  Integer id
  Integer country_id
  String name  
  String name_en  
  String name2 
  String name2_en   
  String name_r
  String header = ''
  String header_en = ''
  String shortname = ''
  String linkname
  String tagname = ''
  String shortdescription
  String shortdescription_en
  String annotation
  String annotation_en
  String keyword
  String itext
  String ceo_title
  String ceo_title_en=''
  String ceo_keywords
  String ceo_description
  String ceo_description_en
  String header_d = ''
  String header_d_en = ''
  String ceo_title_d
  String ceo_title_d_en
  String ceo_keywords_d
  String ceo_description_d
  String ceo_description_d_en
  String ceo_title_cot =''
  String ceo_title_cot_en=''
  String header_cot = ''
  String header_cot_en = ''
  String ceo_keywords_cot = ''
  String ceo_description_cot = ''
  String ceo_description_cot_en = ''
  String annotation_cot = ''
  String annotation_cot_en = ''
  String itext_cot = ''
  String picture = ''
  Integer rating
  Integer nextdir = 0
  Integer previousdir = 0
  Integer is_main = 1
  Integer is_active = 1
  Integer is_index = 0
  Integer is_cottage = 0
  Integer ncount
  Date inputdate
  Date moddate = new Date()
  Integer modstatus
  Integer is_specoffer=0
 
  def csiSelectPopdirection(lId,iCountryId,sName,iMod,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsInt=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='popdirection'
    hsSql.where="1=1"+
        ((sName!='')?' AND (name2 like CONCAT("%",:name,"%"))':'')+
		((lId>0)?" and id=:id":"")+
		((iMod>-1)?" and modstatus=:modstatus":"")+
    ((iCountryId)?" and country_id=:country_id":"")
    hsSql.order="id DESC"
    if(sName!='')
      hsString['name']=sName
    if(lId>0)
      hsInt['id']=lId
    if(iMod>-1)
      hsInt['modstatus']=iMod
    if(iCountryId) 
      hsInt['country_id']=iCountryId  

    def hsRes=searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,
      null,null,iMax,iOffset,'id',true,Popdirection.class)
  }

  def beforeDelete(){
    Home.withNewSession {
      def oHomes = Home.findAllByPopdirection_id(id)
      for (home in oHomes){
        home.popdirection_id = 0
        if(!home.save(flush:true)) {
          log.debug(" Error on save home(administrators/popdirdelete/"+id+"):")
          home.errors.each{log.debug(it)}
        }
      }
    }
  }

}
