import org.codehaus.groovy.grails.commons.ConfigurationHolder
class Articles {
  def searchService

  static constraints = {
  }
  static mapping = {
    tags lazy:false
    version false
  }

  Integer id
  String title
  String linkname
  String shortdescription
  String atext
  String picture
  String author = 'staytoday'
  String googleplus_id = ''
  String ceo_title = ''
  String ceo_keywords = ''
  String ceo_description = ''
  Date inputdate = new Date()
  Integer modstatus = 1
  static hasMany = [tags:Articles_tags]
  static belongsTo = Articles_tags

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def getArticleList(iId,iModst,sTitle,sDate,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from="articles"
    hsSql.where="(1=1)"+
            ((iId>0)?" and id=:iId":"")+
            ((iModst>-1)?" and modstatus=:iModst":"")+
            ((sTitle!='')?' AND title like CONCAT("%",:sTitle,"%")':'')+
            ((sDate!='')?' AND inputdate=:sDate':'')
    hsSql.order="id DESC"

    if(iId>0)
      hsLong['iId']=iId
    if(iModst>-1)
      hsLong['iModst']=iModst
    if(sTitle!='')
      hsString['sTitle']=sTitle
    if(sDate!='')
      hsString['sDate']=sDate

    return searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,
                                          iMax,iOffset,'id',true,Articles.class,null,true)
  }

  static def getUniqueLinkname(sName,lId=0){
    def tempResult = Tools.transliterate(sName)
    int i = 0
    if (tempResult.matches("[0-9-]+"))
      tempResult = ((ConfigurationHolder.config.linkname.prefix)?ConfigurationHolder.config.linkname.prefix:"arenda_")+tempResult
    def result = tempResult

    while (Articles.findByLinknameAndIdNotEqual(result,lId)){
      i++
      result = tempResult + '_' + i
    }

    return result
  }

  def getTimeline(sAuthor,iYear,iMonth,iDay,lsTags,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]
    def hsList=[:]

    def dateStart, dateEnd
    if (iYear) {
      dateStart = Calendar.getInstance()
      dateStart.set(iYear,iMonth?iMonth-1:0,iDay?:1)
      if(!iMonth) {
        dateEnd = Calendar.getInstance()
        dateEnd.setTime(dateStart.getTime())
        dateEnd.add(Calendar.YEAR,1)
      } else if(!iDay) {
        dateEnd = Calendar.getInstance()
        dateEnd.setTime(dateStart.getTime())
        dateEnd.add(Calendar.MONTH,1)
      } else {
        dateEnd = Calendar.getInstance()
        dateEnd.setTime(dateStart.getTime())
        dateEnd.add(Calendar.DATE,1)
      }
    }

    hsSql.select="*"
    hsSql.from="articles"+
            ((lsTags)?',articles_tags_articles,articles_tags':'')
    hsSql.where="articles.modstatus=1"+
            ((sAuthor!=''&&sAuthor!='all')?' AND articles.author=:sAuthor':'')+
            ((iYear)?' AND articles.inputdate>=:sDateStart and articles.inputdate<:sDateEnd':'')+
            ((lsTags)?' AND articles.id=articles_tags_articles.articles_id AND articles_tags_articles.articles_tags_id=articles_tags.id AND articles_tags.name in (:lsTags)':'')
    hsSql.order="articles.inputdate DESC"

    if(sAuthor!=''&&sAuthor!='all')
      hsString['sAuthor']=sAuthor
    if(iYear){
      hsString['sDateStart']=String.format('%ty-%<tm-%<td',dateStart.getTime())
      hsString['sDateEnd']=String.format('%ty-%<tm-%<td',dateEnd.getTime())
    }
    if (lsTags) {
      hsSql.group="articles_id"
      hsList['lsTags']=lsTags
    }

    return searchService.fetchDataByPages(hsSql,null,null,null,hsString,hsList,null,
                                          iMax,iOffset,lsTags?'*':'articles.id',true,Articles.class,null,true)
  }
  void toggleModstatus() {
    modstatus = modstatus?0:1
    save(flush:true)
  }
}