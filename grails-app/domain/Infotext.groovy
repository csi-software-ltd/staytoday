class Infotext {  
  def searchService
  def sessionFactory
  
  static mapping = {
  }	  
  
  static constraints = {
	  icon(nullable:true)
    name_en(nullable:true)
    header_en(nullable:true)
    title_en(nullable:true)
    keywords(nullable:true)
    description(nullable:true)
    description_en(nullable:true)
    promotext1(nullable:true)
    promotext1_en(nullable:true)
    promotext2(nullable:true)    
    promotext2_en(nullable:true)
    itext(nullable:true)
    itext_en(nullable:true)
    itext2(nullable:true)
    itext2_en(nullable:true)
    itext3(nullable:true)    
    itext3_en(nullable:true)    
    relatedpages(nullable:true)
  }
  
  Integer id
  Integer itemplate_id 
  String controller
  String action  
  Integer npage  
  String icon
  String relatedpages
  String shortname
  String name
  String name_en
  String header
  String header_en
  String title
  String title_en
  String keywords
  String description
  String description_en
  String itext
  String itext_en
  String itext2
  String itext2_en
  String itext3  
  String itext3_en
  String promotext1
  String promotext1_en
  String promotext2  
  String promotext2_en
  Date moddate
  Integer is_anons=0
  
  def csiSelectInfotext(sAction,sController,iItemplate_id,iOrder,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsInt=[:]
    def hsString=[:]
	
    hsSql.select="*"
    hsSql.from='infotext'
    hsSql.where="1=1"+
				((iItemplate_id>-1)?' AND itemplate_id =:itemplate_id':'')+
				((sAction!='')?' AND (action like CONCAT("%",:action) OR action like CONCAT(:action,"%") OR action like CONCAT("%",:action,"%"))':'')+
				((sController!='')?' AND (controller like CONCAT("%",:controller) OR controller like CONCAT(:controller,"%") OR controller like CONCAT("%",:controller,"%"))':'')
    hsSql.order="id DESC"
    if(iItemplate_id>-1)
      hsInt['itemplate_id']=iItemplate_id
    if(sAction!='')
      hsString['action']=sAction
    if(sController!='')
      hsString['controller']=sController

    def hsRes=searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,
      null,null,iMax,iOffset,'id',true,Infotext.class)          
  }
  
  def csiGetLastInsert(){
    return searchService.getLastInsert()
  }

  def csiSelectMailtemplate(sAction,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsInt=[:]
    def hsString=[:]
	
    hsSql.select="*"
    hsSql.from='email_template'
    hsSql.where="1=1"+
        ((sAction!='')?' AND (action like CONCAT("%",:action) OR action like CONCAT(:action,"%") OR action like CONCAT("%",:action,"%"))':'')
    hsSql.order="id DESC"
    if(sAction!='')
      hsString['action']=sAction
       
    def hsRes=searchService.fetchDataByPages(hsSql,null,null,null,hsString,
      null,null,iMax,iOffset,'id',true,Email_template.class)          
  }
}
