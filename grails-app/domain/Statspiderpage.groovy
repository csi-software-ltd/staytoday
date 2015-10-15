class Statspiderpage {
  def searchService

  static mapping = {
    version false
  }

  Long id
  Integer spider_id
  String keyword
  Integer type
  String prop
  Date requesttime

  String spiderman
  Date moddate
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectStatistic(iType,sCityname,sInputdate,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsLong=[:]
    def hsString=[:]
  
    hsSql.select = "*, spider.name as spiderman, city.moddate as moddate"
    hsSql.from = 'statspiderpage, spider, city'
    hsSql.where = "spider.id = statspiderpage.spider_id and city.name = statspiderpage.keyword"+
        ((iType>0)?' AND statspiderpage.type =:type':'')+
        ((sCityname!='')?' AND statspiderpage.keyword like CONCAT("%",:city_name,"%")':'')+
        ((sInputdate!='')?' AND statspiderpage.requesttime >=:requesttime':'')
    hsSql.order = "statspiderpage.requesttime DESC"

    if(iType>0)
      hsLong['type'] = iType
    if(sCityname!='')
      hsString['city_name'] = sCityname
    if(sInputdate!='')
      hsString['requesttime'] = sInputdate

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'statspiderpage.id',true,Statspiderpage.class)
  }

}