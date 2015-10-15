class Notetype {
  def searchService
  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  String name
  String icon
  String notetext
  String notetext_en
  Integer email_template_id
  Integer max_notes
  Integer dayinterval
  Integer param
  Integer notegroup_id = 1
  Integer modstatus

  def csiGetNotetype(lId,lTemplateId,iMax,iOffset){
    def hsSql=[
      select:"*",
      from:'notetype',
      where:'1=1']
      //order:'id DESC']
        
    def hsLong=[:]
    
    if(lId){
      hsSql.where+=' AND id=:lId'
      hsLong['lId']=lId
    }
    if(lTemplateId){
      hsSql.where+=' AND email_template_id=:lTemplateId'
      hsLong['lTemplateId']=lTemplateId
    }
   	def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'id',true,Notetype.class)
  }  
}
