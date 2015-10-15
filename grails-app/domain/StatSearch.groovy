class StatSearch {
  def searchService
  static constraints = {}
  static mapping = {
    version false
    cache false
    tablePerHierarchy false
  }

  //JFI:
  Long quant
  String name
  String type

  private getGroupLevel(iTime){
    switch (iTime){
      case 2: return 1
      case 3: return 1
      case 4: return 2
      case 5: return 2
      case 6: return 2
      default: return 0
    }
  }

  private getLogtime(iTime){
    switch (iTime){
      case 1: return ' AND logtime>date_add(current_date, INTERVAL - 1 YEAR)'
      case 2: return ' AND logtime>date_add(current_date, INTERVAL - 1 MONTH)'
      case 3: return ' AND logtime>date_add(current_date, INTERVAL - 2 MONTH)'
      case 4: return ' AND logtime>date_add(current_date, INTERVAL - 1 WEEK)'
      case 5: return ' AND logtime>date_add(current_date, INTERVAL - 1 MONTH)'
      case 6: return ' AND logtime>date_add(current_date, INTERVAL - 2 MONTH)'
      default: return ''
    }
  }
        
  /////////////////////////////////////////////////////////////////////////////////  
  def getStatsByKeyword(hsParams,iMax){
    def hsSql=[
      select:"id,keyword AS name,SUM(statkeywords.rating) AS quant,'keyword' AS type",
      from:'statkeywords',
      where:'succeed=:succeed  AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      order:'quant DESC',
      group:'keyword']
    
    def hsStr=[:]
    def hsList=[:]
    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time),
                'succeed':hsParams.int.succeed]
    
    if(hsParams.string.keyword!=''){
      hsSql.where+=' AND keyword like :keyword'
      hsStr['keyword']='%'+hsParams.string.keyword+'%'
    }
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,hsStr,hsList, StatSearch.class ,iMax)
  }
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsBySection(hsParams,iMax){
    def hsSql=[
      select:"statpagetype.id as id, statpagetype.fullname_ru as name,SUM(statpages.rating) AS quant,'section' AS type",
      from:'statpages, statpagetype',
      where:'statpages.statpagetype_id=statpagetype.id AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      order:'quant DESC',
      group:'statpagetype.id, statpagetype.fullname_ru']
    
    def hsStr=[:]
    def hsList=[:]
    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time)]
    
    if(hsParams.int.statref_id>0){
      hsSql.where+=' AND ref_id=:statref_id'
      hsLong['statref_id']=hsParams.int.statref_id
    }
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,hsStr,hsList, StatSearch.class ,iMax)
  }
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsByService(hsParams,iMax){
    def hsSql=[
      select:"statservice.service_id as id,statservicetype.name as name,SUM(statservice.rating) AS quant,'service' AS type",
      from:'statservice, statservicetype',
      where:'statservice.service_id=statservicetype.id AND statservicetype.is_active<>0 AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      order:'quant DESC',
      group:'statservice.service_id, statservicetype.name']
    
    def hsStr=[:]
    def hsList=[:]
    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time)]
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,hsStr,hsList, StatSearch.class ,iMax)
  }
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsByKeywordDetail(hsParams,iMax){
    def hsSql=[
      select:"id,DATE_FORMAT(logtime,'%d.%m.%Y') AS name,sum(rating)  AS quant,'keyword' AS type",
      from:'statkeywords',
      where: 'keyword=:keyword AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      group:'logtime',
      order:'logtime ASC']
    
    def hsStr=['keyword':hsParams.string.keyword] 
    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time)]
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,hsStr,null, StatSearch.class ,iMax)
  }
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsBySectionDetail(hsParams,iMax){
    def hsSql=[
      select:"id,DATE_FORMAT(logtime,'%d.%m.%Y') AS name,sum(rating)  AS quant,'section' AS type",
      from:'statpages',
      where: 'statpagetype_id=:id AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      group:'logtime',
      order:'logtime ASC']
    
    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time),'id':hsParams.long.id]
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,null,null, StatSearch.class ,iMax)
   }
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsBySectionDetailCircle(hsParams,iMax){
    def hsSql=[
      select:"statref.id AS id,statref.name AS name,sum(statpages.rating) AS quant,'section' AS type",
      from:'statpages,statref',
      where: 'statpages.ref_id=statref.id AND grouplevel=0 AND statpagetype_id=:id',
      group:'statref.name',
      order:'name ASC']
    def hsLong=['id':hsParams.long.id]
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,null,null, StatSearch.class ,iMax)
  }
    
 
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsByServiceDetail(hsParams,iMax){
    def hsSql=[
      select:"id,DATE_FORMAT(logtime,'%d.%m.%Y') AS name,sum(rating)  AS quant,'service' AS type",
      from:'statservice',
      where: 'service_id=:id AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      group:'logtime',
      order:'logtime ASC']
    
    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time),'id':hsParams.long.id]
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,null,null, StatSearch.class ,iMax)
   }
/////////////////////////////////////////////////////////////////////////////////////
/////////////StatHome////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
  def getStatsByHome(hsParams,iMax){
    def hsSql=[
      select:"home.id, home.name as name,'home' AS type",
      from:'stathome, home',
      where:'home_id=home.id AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      order:'quant DESC',
      group:'stathome.home_id']
    
    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time)]
    
    if(hsParams.int.statref_id>0){
      hsSql.where+=' AND ref_id=:statref_id'
      hsLong['statref_id']=hsParams.int.statref_id
    }
    if(hsParams.string.code!=''){
      hsSql.where+=' AND stathome.home_id=:code'
      hsLong['code']=hsParams.string.code.toLong()
    }
    if(hsParams.int.output==0){
      hsSql.select+=',SUM(rating_view) AS quant'
      hsSql.where+=' AND rating_view>0'
    } else if (hsParams.int.output==1){
      hsSql.select+=',SUM(rating_edit) AS quant'
      hsSql.where+=' AND rating_edit>0'
    } else if (hsParams.int.output==2) {
      hsSql.select+=',SUM(rating_specoffer) AS quant'
      hsSql.where+=' AND rating_specoffer>0'
    } else if (hsParams.int.output==3) {
      hsSql.select+=',SUM(rating_listing) AS quant'
      hsSql.where+=' AND rating_listing>0'
    } else if (hsParams.int.output==4) {
      hsSql.select+=',SUM(rating_contact) AS quant'
      hsSql.where+=' AND rating_contact>0'
    }
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,null,null, StatSearch.class ,iMax)
  }
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsByHomeDetail(hsParams,iMax,inclNull=false){
    def hsSql=[
      select:"id,DATE_FORMAT(logtime,'%d.%m.%Y') AS name, 'home' AS type",
      from:'stathome',
      where: 'home_id=:id AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      group:'logtime',
      order:'logtime ASC']
    
    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time),'id':hsParams.long.id]

    if(hsParams.int.output==0){
      hsSql.select+=',SUM(rating_view) AS quant'
    if (!inclNull)
      hsSql.where+=' AND rating_view>0'
    } else if (hsParams.int.output==1){
      hsSql.select+=',SUM(rating_edit) AS quant'
      hsSql.where+=' AND rating_edit>0'
    } else if (hsParams.int.output==2) {
      hsSql.select+=',SUM(rating_specoffer) AS quant'
      hsSql.where+=' AND rating_specoffer>0'
    } else if (hsParams.int.output==3) {
      hsSql.select+=',SUM(rating_listing) AS quant'
      if (!inclNull)
        hsSql.where+=' AND rating_listing>0'
    } else if (hsParams.int.output==4){
      hsSql.select+=',SUM(rating_contact) AS quant'
      hsSql.where+=' AND rating_contact>0'
    }
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,null,null, StatSearch.class ,iMax)
   }
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsByHomeDetailCircle(hsParams,iMax){
    def hsSql=[
      select:"statref.id AS id,statref.name AS name, 'home' AS type",
      from:'stathome,statref',
      where: 'stathome.ref_id=statref.id AND home_id=:id AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      group:'statref.name',
      order:'name ASC']
    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time),'id':hsParams.long.id]
    if(hsParams.int.output==0){
      hsSql.select+=',SUM(rating_view) AS quant'
      hsSql.where+=' AND rating_view>0'
    } else if (hsParams.int.output==1){
      hsSql.select+=',SUM(rating_edit) AS quant'
      hsSql.where+=' AND rating_edit>0'
    } else if (hsParams.int.output==2) {
      hsSql.select+=',SUM(rating_specoffer) AS quant'
      hsSql.where+=' AND rating_specoffer>0'
    } else if (hsParams.int.output==3) {
      hsSql.select+=',SUM(rating_listing) AS quant'
      hsSql.where+=' AND rating_listing>0'
    } else if (hsParams.int.output==3) {
      hsSql.select+=',SUM(rating_contact) AS quant'
      hsSql.where+=' AND rating_contact>0'
    }
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,null,null, StatSearch.class ,iMax)
  }
/////////////////////////////////////////////////////////////////////////////////////
////////////////StatProp/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
  def getStatsByProp(hsParams,iMax){
    def hsSql=[
      select:"'prop' AS type,SUM(statprop.rating) AS quant",
      from:'statprop',
      where:'grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      order:'quant DESC',
      group:'statprop.prop']

    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time)]

    if(hsParams.int.statref_id>0){
      hsSql.where+=' AND statprop.ref_id=:statref_id'
      hsLong['statref_id']=hsParams.int.statref_id
    }
    if(hsParams.int.output==0){
      hsSql.select+=',user.id,user.nickname as name'
      hsSql.from+=',user'
      hsSql.where+=' AND prop=user.id AND statpagetype_id=12'
    } else if (hsParams.int.output==1){
      hsSql.select+=',valuta.id,valuta.name as name'
      hsSql.from+=',valuta'
      hsSql.where+=' AND prop=valuta.id AND statpagetype_id=5'
    } else if (hsParams.int.output==2) {
      hsSql.select+=',popdirection.id,popdirection.name as name'
      hsSql.from+=',popdirection'
      hsSql.where+=' AND prop=popdirection.id AND statpagetype_id=15'
    } else if (hsParams.int.output==3) {
      hsSql.select+=',popdirection.id,popdirection.name2 as name'
      hsSql.from+=',popdirection'
      hsSql.where+=' AND prop=popdirection.id AND statpagetype_id=18'
    } else if (hsParams.int.output==4) {
      hsSql.select+=',country.id,country.name as name'
      hsSql.from+=',country'
      hsSql.where+=' AND prop=country.id AND statpagetype_id=26'
    } else if (hsParams.int.output==5) {
      hsSql.select+=',articles.id,articles.title as name'
      hsSql.from+=',articles'
      hsSql.where+=' AND prop=articles.id AND statpagetype_id=30'
    } else if (hsParams.int.output==6) {
      hsSql.select+=',articles_tags.id,articles_tags.name as name'
      hsSql.from+=',articles_tags'
      hsSql.where+=' AND prop=articles_tags.id AND statpagetype_id=31'
    }
    if(hsParams.string.code!=''){
      hsSql.where+=' AND statprop.prop=:code'
      hsLong['code']=hsParams.string.code.toLong()
    }
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,null,null, StatSearch.class ,iMax)
  }
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsByPropDetail(hsParams,iMax){
    def hsSql=[
      select:"id,DATE_FORMAT(logtime,'%d.%m.%Y') AS name, 'prop' AS type, SUM(rating) AS quant",
      from:'statprop',
      where: 'prop=:id AND grouplevel=:grouplevel'+getLogtime(hsParams.int.time),
      group:'logtime',
      order:'logtime ASC']

    def hsLong=['grouplevel':getGroupLevel(hsParams.int.time),'id':hsParams.long.id]

    if(hsParams.int.output==0)
      hsSql.where+=' AND statpagetype_id=12'
    else if (hsParams.int.output==1)
      hsSql.where+=' AND statpagetype_id=5'
    else if (hsParams.int.output==2)
      hsSql.where+=' AND statpagetype_id=15'
    else if (hsParams.int.output==3)
      hsSql.where+=' AND statpagetype_id=18'
    else if (hsParams.int.output==4)
      hsSql.where+=' AND statpagetype_id=26'
    else if (hsParams.int.output==5)
      hsSql.where+=' AND statpagetype_id=30'
    else if (hsParams.int.output==6)
      hsSql.where+=' AND statpagetype_id=31'
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,null,null, StatSearch.class ,iMax)
   }
  /////////////////////////////////////////////////////////////////////////////////
  def getStatsByPropDetailCircle(hsParams,iMax){
    def hsSql=[
      select:"statref.id AS id,statref.name AS name, 'home' AS type, SUM(rating) AS quant",
      from:'statprop,statref',
      where: 'statprop.ref_id=statref.id AND grouplevel=0 AND prop=:id',
      group:'statref.name',
      order:'name ASC']
    def hsLong=['id':hsParams.long.id]
    if(hsParams.int.output==0)
      hsSql.where+=' AND statpagetype_id=12'
    else if (hsParams.int.output==1)
      hsSql.where+=' AND statpagetype_id=5'
    else if (hsParams.int.output==2)
      hsSql.where+=' AND statpagetype_id=15'
    else if (hsParams.int.output==3)
      hsSql.where+=' AND statpagetype_id=18'
    else if (hsParams.int.output==4)
      hsSql.where+=' AND statpagetype_id=26'
    else if (hsParams.int.output==5)
      hsSql.where+=' AND statpagetype_id=30'
    else if (hsParams.int.output==6)
      hsSql.where+=' AND statpagetype_id=31'
    if(hsParams.inrequest?.site_id){
      (hsParams.inrequest.site_id==1)?(hsSql.where+=' AND site="web"'):(hsSql.where+=' AND site="mob"')
    }
    if(hsParams.inrequest?.lang_id){
      (hsParams.inrequest.lang_id==1)?(hsSql.where+=' AND lang="en"'):(hsSql.where+=' AND lang="ru"')
    }

    return searchService.fetchData(hsSql,hsLong,null,null,null, StatSearch.class ,iMax)
  }
}