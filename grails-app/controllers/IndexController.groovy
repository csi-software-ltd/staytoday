//import org.codehaus.groovy.grails.commons.grailsApplication
import grails.converters.JSON
import java.text.SimpleDateFormat
import java.net.*

class IndexController {
  def jcaptchaService
  def requestService
  def bannersService
  def mailerService
  def usersService
  def billingService
  def smsService
  def ruToEnService
  def apiService
  
  def COOKIENAME='last_home_detail'
  def static final DATE_FORMAT='yyyy-MM-dd'
  def beforeInterceptor = [action:this.&checkId,except:['changeValuta','clickbanner','popdirection','direction','popdirectionAll','discounts','popdiscounts','cottages','timeline_list','timeline']]

  def checkId() {    
    if (params.id){    
      response.sendError(404)
      return
    }
  }
  def changeLang={
  }

  def index = {          
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    if(request.getHeader("User-Agent")?.contains('Mobile')&&!request.getHeader("User-Agent")?.contains('iPad'))
      redirect(uri:'',base:hsRes.context?.mobileURL_lang,permanent:true)
      
    hsRes.inrequest = requestService.getParams(['modstatus']).inrequest
    hsRes.hometype=Hometype.list([sort:'regorder',order:'asc'])
    hsRes.homeperson=Homeperson.list()    
    hsRes.popdirection=Popdirection.findAllByModstatusAndIs_main(1,1,[sort:'rating',order:'desc'])
    hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
    
    hsRes.countryIds = hsRes.popdirection.collect{it.country_id}
    hsRes.countryIds.unique()
    hsRes.countries = Country.list()

    def oHomeSearch=new HomeSearch()
    hsRes.records=oHomeSearch.csiFindMain()            
    hsRes.specoffer_records=oHomeSearch.csiFindSpecoffer()
    hsRes.specoffer_records.each{requestService.setStatistic('specoffer',0,it.id)}
    hsRes.urlphoto = grailsApplication.config.urlphoto    
    hsRes.imageurl = grailsApplication.config.urluserphoto
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    if (session.isVote == 1 || requestService.getCookie('isVote'))
      hsRes.mayVote = 0
    else 
      hsRes.mayVote = 1

    def tagList = Home.executeQuery("""select c.name,count(h.id),c.is_index,c.name2,c.country_id,c.domain,c.name_en
    from Home h, City c
    where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
    group by h.city_id
    having count(h.id) > :minCount
    order by c.name""",["minCount":(Tools.getIntVal(grailsApplication.config.index.cityTagCloud.minCityCount,5) as long)])

    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[hsRes.context?.lang?tag[6]:tag[0]]=[count:tag[1],is_index:tag[2],name2:hsRes.context?.lang?tag[6]:tag[3],country_id:tag[4],domain:tag[5]];map}     
    
    hsRes.tagcloudParams = [:]
    hsRes.tagcloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.max,50)
    hsRes.tagcloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.middle,20)

    requestService.setStatistic('index')
    
    def sLastHome=requestService.getCookie(COOKIENAME)
    hsRes.lastHome=[]
    if(sLastHome){          
      hsRes.lastHome=oHomeSearch.csiFindByIds(sLastHome.split(','))
    }
//transliterate////////////////////////////////////////////   
    if(hsRes.context.lang){      
      def lsRecords=[]
      for(record in hsRes.records){        
        lsRecords<<record.csiSetEnHomeSearch()
      }      
      hsRes.records=lsRecords
      def lsSpecoffer_records=[]
      for(record in hsRes.specoffer_records){        
        lsSpecoffer_records<<record.csiSetEnHomeSearch()            
      }  
      hsRes.specoffer_records=lsSpecoffer_records
      
      def lsLastHome=[]      
      for(record in hsRes.lastHome){        
        lsLastHome<<record.csiSetEnHomeSearch()            
      }      
      hsRes.lastHome=lsLastHome
    }
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////// captcha >>>//////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////    
  def captcha={    
  }
  def spy_protection={
    if (jcaptchaService.validateResponse("image", session.id, params.captcha)){	  
      def oTemp_ipblock=new Temp_ipblock()
      def lsTemp_ipblock=Temp_ipblock.findAllWhere(userip:request.remoteAddr)	  
      for(temp in lsTemp_ipblock){	
        def oTemp_ipblockTmp=Temp_ipblock.get(temp.id)
        oTemp_ipblockTmp.delete()	    
      }	    
    }
    redirect(action:'index', base:hsRes.context.mainserverURL_lang)//request.getHeader("Referer"))	
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////// captcha <<<//////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////    
  def changeValuta={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    def value = requestService.getIntDef('id',857)
    def oValuta = new Valuta()
    oValuta.setValutaCookie(requestService, value)
    if(requestService.getIntDef('fromList',0))
      session.changeValuta=true
    requestService.setStatistic('changevaluta',0,0,0,'',value.toString())
    render(contentType:"application/json"){[error:false]}
    return
  }
  def about={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    if(request.getHeader("User-Agent")?.contains('Mobile')&&!request.getHeader("User-Agent")?.contains('iPad'))
      redirect(uri:'/about',base:hsRes.context?.mobileURL_lang,permanent:true)

    hsRes.urlvideo = grailsApplication.config.urlvideolesson
    hsRes.lessons = Video_lesson.findAllByType_id(3,[sort:'nomer',order:'asc'])

    requestService.setStatistic('about')
    return hsRes
  }
  def about_en={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    requestService.setStatistic('about_en')     
    redirect(uri: "/en/index/about")
  }
  def safety={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    hsRes.howto=Howto.findAllByPageAndType_id('safety',1,[sort:'number',order:'asc'])
    requestService.setStatistic('safety')
    return hsRes
  }
  def safety_hosting={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    hsRes.howto=Howto.findAllByPageAndType_id('safety',2,[sort:'number',order:'asc'])
    requestService.setStatistic('safety_hosting')
    return hsRes
  }  
  def howto={
    requestService.init(this)    
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    hsRes.howto=Howto.findAllByPageAndType_id('howto',1,[sort:'number',order:'asc'])
    hsRes.urlvideo=grailsApplication.config.urlvideolesson    
    hsRes.lessons=Video_lesson.findAllByType_id(2,[sort:'nomer',order:'asc'])
    requestService.setStatistic('howto')
    return hsRes
  }
  def howto_hosting={
    requestService.init(this)    
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    hsRes.howto=Howto.findAllByPageAndType_id('howto',2,[sort:'number',order:'asc'])    
    hsRes.urlvideo=grailsApplication.config.urlvideolesson
    hsRes.lessons=Video_lesson.findAllByType_id(1,[sort:'nomer',order:'asc'])    
    requestService.setStatistic('howto_hosting')
    return hsRes
  }
  def booking={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.howto=Howto.findAllByPageAndType_id('booking',1,[sort:'number',order:'asc'])    
    requestService.setStatistic('booking')
    return hsRes
  }
  def terms={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if(request.getHeader("User-Agent")?.contains('Mobile')&&!request.getHeader("User-Agent")?.contains('iPad'))
      redirect(uri:'/terms',base:hsRes.context?.mobileURL_lang,permanent:true)
    requestService.setStatistic('terms')
    return hsRes    
  }
  def oferta={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.payway = Payway.findAllByModstatus(1)
    requestService.setStatistic('oferta')
    return hsRes
  }
  def contract={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if(request.getHeader("User-Agent")?.contains('Mobile')&&!request.getHeader("User-Agent")?.contains('iPad'))
      redirect(uri:'/contract',base:hsRes.context?.mobileURL_lang,permanent:true)
    requestService.setStatistic('contract')
    return hsRes  
  }
  def mobile={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    requestService.setStatistic('mobile')
    return hsRes
  }
  def widget={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    requestService.setStatistic('widget')
    return hsRes
  }  
  def voting={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes+=requestService.getParams(['mainVote','save'])
    def oVote = Votetable.get(1)
    if (hsRes.inrequest.save) {
      session.isVote = 1
      requestService.setCookie('isVote',1.toString(),Tools.getIntVal(grailsApplication.config.vote.timeout,2592000))	
      def rat_incr = "rating_" + hsRes.inrequest.mainVote
      oVote."${rat_incr}"++
      oVote.sum++
      oVote.save(flush:true)
    }
    hsRes.bar=""+((oVote.rating_3*100)/oVote.sum)+','+((oVote.rating_2*100)/oVote.sum)+','+((oVote.rating_1*100)/oVote.sum)
    hsRes.title="Результаты+голосования"
    hsRes.bar="&chtt="+hsRes.title+"&chd=t:"+hsRes.bar+"&chds=0,100&chxt=y,x&chxl=0:|Так себе|Нормально|Отлично|1:|0%|"+
      Math.round(100/2)+"%|100%"
    return hsRes
  }
  def error={  
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////
  def clickbanner={
    requestService.init(this)
    def lId=requestService.getLongDef('id',0)
    def sUrl=grailsApplication.config.default_banner_url
    if(lId>0)
      sUrl=bannersService.getRedirectBanner(lId)              	
    redirect(url:sUrl)
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////
  def popdirection={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def lId=requestService.getStr('id')
    if(lId)
      hsRes.popdirection = Popdirection.findByShortname(lId)      
    if(hsRes.popdirection){
      redirect(base:hsRes.context.mainserverURL_lang, controller:'index',action:'direction',params:[id:hsRes.popdirection.linkname,country:Country.get(hsRes.popdirection.country_id)?.urlname],permanent:true)
      return
    } else {
      response.sendError(404)
      return
    }
  }
  def direction={
    requestService.init(this)                                               
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    def lId=requestService.getStr('id')
    def sCountry=requestService.getStr('country')
    def oCountry = Country.findByUrlname(sCountry)
    if(lId)
      hsRes.popdirection = Popdirection.findWhere(linkname:lId,is_active:1,country_id:oCountry?.id)
    if(hsRes.popdirection){
      hsRes.popdirection.ncount++
      if(!hsRes.popdirection.save(flush:true)) {
        log.debug(" Error on save popdirection(index/popdirection/"+lId+"):")
        hsRes.popdirection.errors.each{log.debug(it)}
      }
      hsRes.breadcrumbs=[:]
      hsRes.breadcrumbs.country = oCountry
      hsRes.hometype=Hometype.list()
      hsRes.homeclass=Homeclass.list()
      hsRes.homeperson=Homeperson.list()
      hsRes.urlphoto = grailsApplication.config.urlphoto
      hsRes.imageurl = grailsApplication.config.urlpopdiphoto
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      def oHome = new HomeSearch()
      hsRes.poprecords = oHome.csiFindPopdirection(hsRes.popdirection.id)        
      hsRes.poprecordsMap = oHome.csiFindPopdirection(hsRes.popdirection.id,-1)      
      hsRes.hotdiscount = oHome.csiFindByWhere(hsRes.popdirection.keyword,Tools.getIntVal(grailsApplication.config.discounts.quantity.max,-1),0,[order:-1],[hotdiscount:1],true,false).count
      hsRes.longdiscount = oHome.csiFindByWhere(hsRes.popdirection.keyword,Tools.getIntVal(grailsApplication.config.discounts.quantity.max,-1),0,[order:-1],[longdiscount:1],true,false).count     
      hsRes.nextdirection = Popdirection.get(hsRes.popdirection.nextdir)
      hsRes.previousdirection = Popdirection.get(hsRes.popdirection.previousdir)
      hsRes.articles = Articles.withCriteria {
        createAlias('tags','t')
        eq('modstatus',1)
        eq('t.name',hsRes.popdirection.tagname)
        projections {
          count('t.id')
        }
      }[0]
      hsRes.cottages = oHome.csiFindByWhere(hsRes.popdirection.keyword,-1,0,[hometype_id:2,order:-1],[:],true).count
      hsRes.directionCitiesMap = Region.executeQuery("""select DISTINCT c.name, c.homecount,c.x,c.y,c.is_index,c.name2,c.country_id,c.domain,c.name_en,c.id
        from Region r, City c, Home h
        where c.region_id=r.id and r.popdirection_id=:p_id and c.homecount>0 and h.city_id=c.id and h.modstatus=1
        order by c.name""",[p_id:hsRes.popdirection.id])
      hsRes.directionCities = hsRes.directionCitiesMap.inject([:]){map,tag -> map[tag[hsRes.context?.lang?8:0]]=[count:tag[1],is_index:tag[4],name2:tag[hsRes.context?.lang?8:5],country_id:tag[6],domain:tag[7],city_id:tag[9]];map}
      hsRes.tagcloudParams = [:]
      hsRes.tagcloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.max,50)
      hsRes.tagcloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.middle,20)
      
      def sLastHome=requestService.getCookie(COOKIENAME)
      hsRes.lastHome=[]
      if(sLastHome)
        hsRes.lastHome=oHome.csiFindByIds(sLastHome.split(','))
    } else if (oCountry?.id==3&&lId=='krym') {
      redirect(base:hsRes.context.mainserverURL_lang, controller:'index',action:'direction',params:[id:'krym',country:'russia'],permanent:true)
      return
    } else {
      response.sendError(404)
      return
    }
    hsRes.map_home_scale= Tools.getIntVal(grailsApplication.config.map.home.scale,11)    
    requestService.setStatistic('popdirection',0,0,0,'',hsRes.popdirection.id.toString()) 

    if(hsRes.context?.lang){
      def lsPoprecords=[]
      for(record in hsRes.poprecords){        
        lsPoprecords<<record.csiSetEnHomeSearch()  
      }
      hsRes.poprecords=lsPoprecords    
      
      def lsPoprecordsMap=[]
      for(record in hsRes.poprecordsMap){        
        lsPoprecordsMap<<record.csiSetEnHomeSearch()           
      }  
      hsRes.poprecordsMap=lsPoprecordsMap
      
      def lsLastHome=[]      
      for(record in hsRes.lastHome){        
        lsLastHome<<record.csiSetEnHomeSearch()            
      }      
      hsRes.lastHome=lsLastHome      
    }  
    
    return hsRes
  }
  def popdirectionAll={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    hsRes.sId=requestService.getStr('id')
    if (hsRes.sId&&!Country.findByUrlname(hsRes.sId)) {
      response.sendError(404)
      return
    }
    def oHomeSearch=new HomeSearch()
    hsRes.countries = []
    hsRes.breadcrumbs=[:]
    if (hsRes.sId){
      hsRes.countries<<Country.findByUrlname(hsRes.sId)
      hsRes.popdirection=Popdirection.findAllByModstatusAndCountry_id(1,hsRes.countries[0].id,[sort:'rating',order:'desc'])
      hsRes.breadcrumbs.country = hsRes.countries[0]
      hsRes.anotherCountries = Country.findAllByUrlnameNotEqualAndIs_index(hsRes.sId,1)
      hsRes.directionCities = hsRes.popdirection.collect{ Region.executeQuery("""select c.name,c.name_en,c.is_index,c.name2,c.domain
        from Region r, City c
        where c.region_id=r.id and r.popdirection_id=:p_id
        order by c.homecount DESC""",[p_id:it.id],[max:3])
      }        
      hsRes.specoffer_records=oHomeSearch.csiFindSpecoffer(hsRes.sId)
      hsRes.specoffer_records.each{requestService.setStatistic('specoffer',0,it.id)}
      hsRes.urlphoto = grailsApplication.config.urlphoto
      hsRes.imageurl = grailsApplication.config.urlpopdiphoto
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      
      def sLastHome=requestService.getCookie(COOKIENAME)
      hsRes.lastHome=[]
      if(sLastHome)
        hsRes.lastHome=oHomeSearch.csiFindByIds(sLastHome.split(','))
//transliterate////////////////////////////////////////////   
      if(hsRes.context.lang){      
        def lsSpecoffer_records=[]
        for(record in hsRes.specoffer_records){          
          lsSpecoffer_records<<record.csiSetEnHomeSearch()           
        }  
        hsRes.specoffer_records=lsSpecoffer_records
      
        def lsLastHome=[]      
        for(record in hsRes.lastHome){
          def oHomeSearchTmp=new HomeSearch()
          oHomeSearchTmp.name=Tools.transliterate(record.name,0)
          oHomeSearchTmp.shortaddress=Tools.transliterate(record.shortaddress,0)
          oHomeSearchTmp.price=record.price
          oHomeSearchTmp.client_id=record.client_id
          oHomeSearchTmp.mainpicture=record.mainpicture
          oHomeSearchTmp.nref=record.nref
          oHomeSearchTmp.id=record.id    
          oHomeSearchTmp.city=City.get(record.city_id)?.name_en?:Tools.transliterate(record.city,0)         
          oHomeSearchTmp.city_id=record.city_id
          oHomeSearchTmp.linkname=record.linkname
          oHomeSearchTmp.country_id=record.country_id
          lsLastHome<<oHomeSearchTmp              
        }      
        hsRes.lastHome=lsLastHome
      }
    } else {
      hsRes.countries=Country.findAllByModstatus(1)
      hsRes.popdirection=Popdirection.findAllByModstatus(1,[sort:'rating',order:'desc'])
    }
    hsRes.countryIds = hsRes.popdirection.collect{it.country_id}
    hsRes.countryIds.unique()
    hsRes.nodashed = hsRes.countries.collect{ cnt ->
      hsRes.popdirection.findLastIndexOf {it.country_id==cnt.id}
    }       
    def cityList = Home.executeQuery("""select c.name,c.name_en,count(h.id),c.is_index,c.name2,c.country_id,c.domain
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > :minCount
      order by c.name""",["minCount":(Tools.getIntVal(grailsApplication.config.index.cityTagCloud.minCityCount,5) as long)])
    hsRes.citycloud = cityList.inject([:]){map,tag -> map[tag[hsRes.context?.lang?1:0]]=[count:tag[2],is_index:tag[3],name2:tag[hsRes.context?.lang?1:4],country_id:tag[5],domain:tag[6]];map}
    hsRes.citycloudParams = [:]
    hsRes.citycloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.max,50)
    hsRes.citycloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.middle,20)    
    def tagList = Articles.withCriteria {
      createAlias('tags','t')
      eq('modstatus',1)
      projections {
        groupProperty('t.name')
        count('t.id')
      }
    }
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=tag[1];map}
    hsRes.tagcloudParams = [:]
    hsRes.tagcloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.timeline.filter.fontCount.max,5)
    hsRes.tagcloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.timeline.filter.fontCount.middle,1)
    
    if(hsRes.sId)
      requestService.setStatistic('directioncountry',0,0,0,'',hsRes.countries[0].id.toString())
    else
      requestService.setStatistic('alldirection')    
    return hsRes
  }
  
  def discounts={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def lId=requestService.getStr('id')
    if(lId)
      hsRes.popdirection = Popdirection.findByShortname(lId)    
    if(hsRes.popdirection){
      redirect(action:'popdiscounts',params:[id:hsRes.popdirection.linkname,country:Country.get(hsRes.popdirection.country_id)?.urlname],base:hsRes.context.mainserverURL_lang,permanent:true)
      return
    } else {
      response.sendError(404)
      return
    }
  }
  def popdiscounts={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    def lId=requestService.getStr('id')
    def sCountry=requestService.getStr('country')
    def oCountry = Country.findByUrlname(sCountry)
    if(lId)
      hsRes.popdirection = Popdirection.findWhere(linkname:lId,is_active:1,country_id:oCountry?.id)
    if(hsRes.popdirection){
      hsRes.breadcrumbs=[:]
      hsRes.breadcrumbs.country = Country.get(hsRes.popdirection.country_id)
      hsRes.hometype=Hometype.list([sort:'id',order:'asc'])

      hsRes.homeperson=Homeperson.list()
      hsRes.urlphoto = grailsApplication.config.urlphoto
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      def oHome = new HomeSearch()
      hsRes.hotdiscount = oHome.csiFindByWhere(hsRes.popdirection.keyword,Tools.getIntVal(grailsApplication.config.discounts.quantity.max,-1),0,[order:-1],[hotdiscount:1],true,false)
      hsRes.longdiscount = oHome.csiFindByWhere(hsRes.popdirection.keyword,Tools.getIntVal(grailsApplication.config.discounts.quantity.max,-1),0,[order:-1],[longdiscount:1],true,false)
      hsRes.nextdirection = Popdirection.get(hsRes.popdirection.nextdir)
      hsRes.previousdirection = Popdirection.get(hsRes.popdirection.previousdir)
      hsRes.directionCities = Region.executeQuery("""select c.name,c.name_en,c.homecount,c.is_index,c.domain
          from Region r, City c
          where c.region_id=r.id and r.popdirection_id=:p_id and c.homecount>0
          order by c.name""",[p_id:hsRes.popdirection.id]).inject([:]){map,tag -> map[tag[hsRes.context?.lang?1:0]]=[count:tag[2],is_index:tag[3],domain:tag[4]];map}
      hsRes.tagcloudParams = [:]
      hsRes.tagcloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.max,50)
      hsRes.tagcloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.middle,20)
    //Transliterate
      if(hsRes.context?.lang){      
        for(oHomeTmp in hsRes.hotdiscount.records){
          oHomeTmp.name=Tools.transliterate(oHomeTmp.name,0)
          oHomeTmp.shortaddress=Tools.transliterate(oHomeTmp.shortaddress,0)
          oHomeTmp.city=City.findByName(oHomeTmp.city)?.name_en?:Tools.transliterate(oHomeTmp.city,0)                        
        }  
        for(oHomeTmp in hsRes.longdiscount.records){
          oHomeTmp.name=Tools.transliterate(oHomeTmp.name,0)
          oHomeTmp.shortaddress=Tools.transliterate(oHomeTmp.shortaddress,0)
          oHomeTmp.city=City.findByName(oHomeTmp.city)?.name_en?:Tools.transliterate(oHomeTmp.city,0)                        
        }        
      }            
    } else if (oCountry?.id==3&&lId=='krym') {
      redirect(base:hsRes.context.mainserverURL_lang, controller:'index',action:'popdiscounts',params:[id:'krym',country:'russia'],permanent:true)
      return
    } else {
      response.sendError(404)
      return
    }
    requestService.setStatistic('discounts',0,0,0,'',hsRes.popdirection.id.toString())
    return hsRes
  }
  def cottages={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    def lId=requestService.getStr('id')
    def sCountry=requestService.getStr('country')
    def oCountry = Country.findByUrlname(sCountry)
    if(lId)
      hsRes.popdirection = Popdirection.findWhere(linkname:lId,is_cottage:1,country_id:oCountry?.id)    
    if(hsRes.popdirection){
      hsRes.breadcrumbs=[:]
      hsRes.breadcrumbs.country = oCountry
      hsRes.hometype=Hometype.list([sort:'id',order:'asc'])
      hsRes.homeclass=Homeclass.list([sort:'id',order:'asc'])

      hsRes.homeperson=Homeperson.list()
      hsRes.urlphoto = grailsApplication.config.urlphoto
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      def oHome = new HomeSearch()
      hsRes.poprecords = oHome.csiFindByWhere(hsRes.popdirection.keyword,Tools.getIntVal(grailsApplication.config.popdirection.home_quantity,9),0,[hometype_id:2,order:-1],[:],true).records
      hsRes.poprecordsMap = oHome.csiFindByWhere(hsRes.popdirection.keyword,-1,0,[hometype_id:2,order:-1],[:],true).records
      hsRes.anotherCottageDirections = Popdirection.findAllByIs_cottageAndIdNotEqual(1,hsRes.popdirection.id)

      hsRes.directionCitiesMap = Region.executeQuery("""select DISTINCT c.name,c.homecount,c.is_index,c.name2,c.country_id,c.domain,c.name_en,c.id
          from Region r, City c, Home h
          where c.region_id=r.id and r.popdirection_id=:p_id and c.homecount>0 and h.city_id=c.id and h.hometype_id IN (2,4) and h.modstatus=1
          order by c.name""",[p_id:hsRes.popdirection.id])
      hsRes.directionCities = hsRes.directionCitiesMap.inject([:]){map,tag -> map[tag[hsRes.context?.lang?6:0]]=[count:tag[1],is_index:tag[2],name2:tag[hsRes.context?.lang?6:3],country_id:tag[4],domain:tag[5],city_id:tag[7]];map}
              
      hsRes.tagcloudParams = [:]
      hsRes.tagcloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.max,50)
      hsRes.tagcloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.middle,20)
      
      def sLastHome=requestService.getCookie(COOKIENAME)
      hsRes.lastHome=[]
      if(sLastHome)
        hsRes.lastHome=oHome.csiFindByIds(sLastHome.split(','))
    } else if (oCountry?.id==3&&lId=='krym') {
      redirect(base:hsRes.context.mainserverURL_lang, controller:'index',action:'cottages',params:[id:'krym',country:'russia'],permanent:true)
      return
    } else {
      response.sendError(404)
      return
    }    
    hsRes.map_home_scale= Tools.getIntVal(grailsApplication.config.map.home.scale,11)    
    
    if(hsRes.context?.lang){
      def lsPoprecords=[]
      for(record in hsRes.poprecords){        
        lsPoprecords<<record.csiSetEnHomeSearch()   
      }
      hsRes.poprecords=lsPoprecords
     
      def lsPoprecordsMap=[]
      for(record in hsRes.poprecordsMap){
        def oHomeSearchTmp=new HomeSearch()
        oHomeSearchTmp.name=Tools.transliterate(record.name,0)
        oHomeSearchTmp.shortaddress=Tools.transliterate(record.shortaddress,0)
        oHomeSearchTmp.price=record.price
        oHomeSearchTmp.client_id=record.client_id
        oHomeSearchTmp.mainpicture=record.mainpicture
        oHomeSearchTmp.nref=record.nref
        oHomeSearchTmp.id=record.id    
        oHomeSearchTmp.city=City.get(record.city_id)?.name_en?:Tools.transliterate(record.city,0)         
        oHomeSearchTmp.city_id=record.city_id
        oHomeSearchTmp.linkname=record.linkname
        oHomeSearchTmp.country_id=record.country_id
        oHomeSearchTmp.x=record.x
        oHomeSearchTmp.y=record.y
        lsPoprecordsMap<<oHomeSearchTmp             
      }  
      hsRes.poprecordsMap=lsPoprecordsMap
      
      def lsLastHome=[]      
      for(record in hsRes.lastHome){
        def oHomeSearchTmp=new HomeSearch()
        oHomeSearchTmp.name=Tools.transliterate(record.name,0)
        oHomeSearchTmp.shortaddress=Tools.transliterate(record.shortaddress,0)
        oHomeSearchTmp.price=record.price
        oHomeSearchTmp.client_id=record.client_id
        oHomeSearchTmp.mainpicture=record.mainpicture
        oHomeSearchTmp.nref=record.nref
        oHomeSearchTmp.id=record.id    
        oHomeSearchTmp.city=City.get(record.city_id)?.name_en?:Tools.transliterate(record.city,0)         
        oHomeSearchTmp.city_id=record.city_id
        oHomeSearchTmp.linkname=record.linkname
        oHomeSearchTmp.country_id=record.country_id
        lsLastHome<<oHomeSearchTmp              
      }      
      hsRes.lastHome=lsLastHome      
    }    
    
    requestService.setStatistic('popdirection',0,0,0,'',hsRes.popdirection.id.toString())    
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def selectregion={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def country_id=requestService.getStr('country_id')
    hsRes.data = Region.findAllByCountry_idAndModstatus(country_id,1,[sort:'regorder',order:'desc'])
    hsRes.countries = Country.findAllByModstatus(1,[sort:'regorder',order:'asc'])
    return hsRes
  }
  def reloadCaptcha={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.Ajax = [:]
    hsRes.Ajax.captcha=jcaptcha.jpeg(name:'image').toString()
    render hsRes.Ajax as JSON
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////  
  def selectpopdirection={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def country_id=requestService.getIntDef('country_id',1)
    hsRes.data = Popdirection.findAll('FROM Popdirection WHERE country_id=:country_id AND modstatus=1 ORDER BY rating desc, name2 asc',[country_id:country_id?:1])
    hsRes.popdirection=Popdirection.findAll('FROM Popdirection WHERE modstatus=1 ORDER BY rating desc',[max:Tools.getIntVal(grailsApplication.config.popdirection.quantity,10)])
    hsRes.countryIds = hsRes.popdirection.collect{it.country_id}
    hsRes.countryIds.unique()
    hsRes.countries = Country.list()    
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////  
  def selectpopcities={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def country_id=requestService.getIntDef('country_id',1)
    hsRes.data = City.findAll('FROM City WHERE country_id=:country_id AND homecount>10 ORDER BY case when homecount>:homecountsort then homecount else 0 end desc, name asc',[country_id:country_id?:1,homecountsort:Tools.getIntVal(grailsApplication.config.popcities.homecountsort,70)])
    hsRes.countries = Country.findAllByIs_popcities(1)
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////
  private def _getFilterDictionary(lsHomeclass){
    def hsRes=[:]
    if( (lsHomeclass?:[]).size() == 0) lsHomeclass=[0]    
    def oHomeclass=new Homeclass()    
    hsRes.homeclass=oHomeclass.getByIds(lsHomeclass) 	
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def timeline = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes+=requestService.getParams(['year','month','day'],null,['blog','id'])
    hsRes.inrequest.tag = requestService.getStringList('tag')    
    
    hsRes+=Tools.getCalendarFilter(message(code:'calendar.monthName').split(','),Tools.getIntVal(grailsApplication.config.timeline.filter.min.year,2011),Tools.getIntVal(grailsApplication.config.timeline.filter.min.month,8))
    hsRes.filterAuthors = Articles.findAll('from Articles where modstatus=1 group by author order by count(author) desc',[max:10]).collect{it.author}
    if ((hsRes.inrequest.blog?:'all')!='all'&&!hsRes.filterAuthors.contains(hsRes.inrequest.blog)) {
      //not working correctly, when authors more than 10
      response.sendError(404)
      return
    }
    def tagList = Articles.withCriteria {
      createAlias('tags','t')
      eq('modstatus',1)
      projections {
        groupProperty('t.name')
        count('t.id')
      }
    }
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=tag[1];map}
    hsRes.tagcloudParams = [:]
    hsRes.tagcloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.timeline.filter.fontCount.max,5)
    hsRes.tagcloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.timeline.filter.fontCount.middle,1)
    hsRes.popdirections=Popdirection.findAll('FROM Popdirection WHERE modstatus=1 ORDER BY rating desc',[max:Tools.getIntVal(grailsApplication.config.popdirection.quantity,10)])
    hsRes.imageurl = grailsApplication.config.urlarticlesphoto
    def cityList = Home.executeQuery("""select c.name,c.name_en,count(h.id),c.is_index,c.country_id,c.name2,c.domain
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > :minCount
      order by c.name""",["minCount":(Tools.getIntVal(grailsApplication.config.index.cityTagCloud.minCityCount,5) as long)])
    hsRes.citycloud = cityList.inject([:]){map,tag -> map[tag[hsRes.context?.lang?1:0]]=[count:tag[2],is_index:tag[3],country_id:tag[4],name2:tag[hsRes.context?.lang?1:5],domain:tag[6]];map}
    hsRes.citycloudParams = [:]
    hsRes.citycloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.max,50)
    hsRes.citycloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.middle,20)    
    if (hsRes.inrequest.tag)
      hsRes.poptag=Popdirection.findByTagnameInList(hsRes.inrequest?.tag)?."${'name2'+hsRes.context?.lang}"?:(City.findByTagnameInList(hsRes.inrequest?.tag)?."${'name'+hsRes.context?.lang}"?:(Articles_tags.findByName(hsRes.inrequest?.tag[0])?."${'header'+hsRes.context?.lang}"?:''))        
    if (hsRes.inrequest.id){
      hsRes.article = Articles.findByLinknameAndAuthor(hsRes.inrequest.id,hsRes.inrequest?.blog)
      if (!hsRes.article) {
        response.sendError(404)
        return
      }
      Onlinelog.withNewSession { session ->
        requestService.setStatistic('article',0,0,0,'',hsRes.article.id.toString())
      }
    } else {
      def oArticles = new Articles()
      hsRes.blogs = oArticles.getTimeline(hsRes.inrequest.blog?:'',hsRes.inrequest.year?:0,hsRes.inrequest.month?:0,hsRes.inrequest.day?:0,hsRes.inrequest.tag?:'',10,requestService.getOffset())

      def temp = null
      hsRes.dates = hsRes.blogs.records.collect {
        if(it.inputdate!=temp) {
          temp=it.inputdate
          it.inputdate
        } else { null }
      }
      Onlinelog.withNewSession { session ->
        requestService.setStatistic('timeline')
        hsRes.inrequest.tag.each{
          requestService.setStatistic('articletag',0,0,0,'',Articles_tags.findByName(it)?.id?:'')
        }
      }
    }
    return hsRes
  }

  def timeline_list = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes+=requestService.getParams(['year','month','day'],null,['blog','id'])
    hsRes.inrequest.tag = requestService.getStringList('tag')

    if(!hsRes.inrequest.id) {
      def oArticles = new Articles()
      hsRes.blogs = oArticles.getTimeline(hsRes.inrequest.blog?:'',hsRes.inrequest.year?:0,hsRes.inrequest.month?:0,hsRes.inrequest.day?:0,hsRes.inrequest.tag?:'',10,requestService.getOffset())
      def temp = null
      hsRes.dates = hsRes.blogs.records.collect {
        if(it.inputdate!=temp) {
          temp=it.inputdate
          it.inputdate
        } else { null }
      }
    } else {
        hsRes.article = Articles.findByLinkname(hsRes.inrequest.id)      
    }
    if (!hsRes.blogs&&!hsRes.article) {
      response.sendError(404)
      return
    }
    hsRes.imageurl = grailsApplication.config.urlarticlesphoto
    //return hsRes
    render(template: "/blog", model: hsRes)
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  def waiting = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)    
    hsRes.popdirection=Popdirection.findAllByModstatusAndIs_main(1,1,[sort:'rating',order:'desc'])    
    hsRes.countryIds = hsRes.popdirection.collect{it.country_id}
    hsRes.countryIds.unique()
    hsRes.countries = Country.list()
    
    hsRes.user = User.get(hsRes.user?.id)
    hsRes+=requestService.getParams(['country_id','region_id','hometype_id','homeperson_id','valuta_id','timetodecide_id','isadd'],[],['city','pricefrom','priceto','ztext','email','nickname','where'],['date_start','date_end'])
    def bSave=requestService.getLongDef('save',0)
    if(!bSave){
      if(hsRes.user?.tel?:'') {
        hsRes.ind = hsRes.user.tel.split("\\(")[0].replace('+','')
        hsRes.telef = hsRes.user.tel.split('\\)')[1]
        hsRes.kod = hsRes.user.tel.split('\\(')[1].split('\\)')[0]
      }
    } else {
      hsRes.ind = requestService.getStr('ind')
      hsRes.telef = requestService.getStr('telef')
      hsRes.kod = requestService.getStr('kod')
    }
    if(hsRes.inrequest?.date_start?:'')
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
    if(hsRes.inrequest?.date_end?:'')
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
    hsRes.hometype=Hometype.list([sort:'name',order:'asc'])
    hsRes.homeperson=Homeperson.list()
	  hsRes.country=Country.list([sort:'regorder',order:'asc'])
	  hsRes.textlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
	  hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
	  hsRes.timetodecide=Timetodecide.findAllByDaysLessThan(31)
    hsRes.valuta=Valuta.findAllByModstatus(1,[sort:'regorder',order:'asc'])
	
    if(hsRes.inrequest.where){
      def oCountry=Country.findWhere(name:hsRes.inrequest.where)
      if(oCountry){
        hsRes.inrequest.country_id=oCountry.id
      }else{      
        def oRegion=Region.findWhere(name:hsRes.inrequest.where)
        if(oRegion){
          hsRes.inrequest.region_id=oRegion.id
          hsRes.inrequest.country_id=oRegion.country_id
          
          def oCity=City.findWhere(name:hsRes.inrequest.where)
          if(oCity)
            hsRes.inrequest.city=oCity.name          
        }else{
          def oCity=City.findWhere(name:hsRes.inrequest.where)
          if(oCity){
            hsRes.inrequest.city=oCity.name
            oRegion=Region.findWhere(id:oCity.region_id)
            hsRes.inrequest.region_id=oRegion?.id?:0
            hsRes.inrequest.country_id=oRegion?.country_id?:0
          }
        }        
      }
    } 
    
    if(hsRes.inrequest?.country_id?:0)
      hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
    else if((hsRes.country?:[]).size()>0)
  	  hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])

    if(!bSave)
      requestService.setStatistic('waiting_public')
    return hsRes
  }

  def saveWaiting = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    //if (!checkUser(hsRes)) return
	
    hsRes.user = User.get(hsRes.user?.id)
    flash.error = []
    hsRes+=requestService.getParams(['country_id','region_id','hometype_id','homeperson_id','valuta_id','save','timetodecide_id'],['pricefrom','priceto'],['city','ztext','ind','kod','telef','email','nickname'],['date_start','date_end'])
    def lsTel = requestService.getParams(null,null,['ind','kod','telef']).inrequest
	
    if(lsTel && (lsTel?:[]).size()!=3)
      flash.error<<7
    else if (lsTel){
      for (t in lsTel)
        if (!t.value.replace(' ','').replace('-','').matches("[0-9]+"))
          if(!flash.error.find{it==7})
            flash.error<<7
    } else if (!lsTel){
      flash.error<<9
    }
    if(!hsRes.inrequest.city)
      flash.error<<1
    if(!hsRes.inrequest.pricefrom)
      flash.error<<2
    else if (hsRes.inrequest.priceto && (hsRes.inrequest.priceto < hsRes.inrequest.pricefrom)){
      def temp = hsRes.inrequest.pricefrom
      hsRes.inrequest.pricefrom = hsRes.inrequest.priceto
      hsRes.inrequest.priceto = temp
    }
    if(!hsRes.inrequest.ztext)
      flash.error<<3
    def bDate = true
    if(!(hsRes.inrequest?.date_start?:'')){
      flash.error<<4
      bDate = false
    }
    if(!(hsRes.inrequest?.date_end?:'')){
      flash.error<<5
      bDate = false
    }
    if(bDate){
      def date_start=requestService.getDate('date_start')
      def date_end=requestService.getDate('date_end')		  
      if(date_start>date_end)
        flash.error<<6
    }      

    if (hsRes.user && !hsRes.user.email){
      if(hsRes.inrequest?.email?:''){
        if (!Tools.checkEmailString(hsRes.inrequest.email))
          flash.error<<11
        else if(User.findByEmail(hsRes.inrequest.email) && User.findByEmail(hsRes.inrequest.email)?.id!=hsRes.user.id)
          flash.error<<12
      } else
        flash.error<<10
    }  

    //validating input data>>
    if (!hsRes.user) {
      if (!hsRes.inrequest.email)
        flash.error<<101        
      else if (!Tools.checkEmailString(hsRes.inrequest.email))
        flash.error<<103        
      else if (User.findWhere(email:hsRes.inrequest.email?:''))        
        flash.error<<100  // old user. need avtorization      
      if (!hsRes.inrequest.nickname)     
        flash.error<<102
    }
    //<<end of validating

    if (!flash.error && !hsRes.user) {
      //new user registration>>
      hsRes.isNeedSecondMail = true
      def oUser = new User()
      if (oUser.mboxNewUser([email:hsRes.inrequest.email,nickname:hsRes.inrequest.nickname]))
        if (usersService.loginInternalUser(hsRes.inrequest.email,Tools.hidePsw(hsRes.inrequest.email)[0..8],requestService,1))
          hsRes.user = User.findByName(hsRes.inrequest.email)      
      //<<new User registration
    }
      
    if(!(flash.error.size())){
      if (!hsRes.user.email){
        hsRes.user.email = hsRes.inrequest.email
        if (!hsRes.user.code)
          hsRes.user.code=java.util.UUID.randomUUID().toString()        
      }
      if (!hsRes.user.tel)
        hsRes.user.tel = '+'+hsRes.inrequest.ind.replace(' ','').replace('-','')+'('+hsRes.inrequest.kod.replace(' ','').replace('-','')+')'+hsRes.inrequest.telef.replace(' ','').replace('-','')
      if (hsRes.user.getDirtyPropertyNames())
        if( !hsRes.user.save(flush:true)) {
          log.debug(" Error on save user(trip/saveWaiting):")
          hsRes.user.errors.each { log.debug(it) }
          flash.error<<8
        }
      if(!(flash.error.size())){
        def stringlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
        if (hsRes.inrequest?.ztext.size()>stringlimit) hsRes.inrequest?.ztext = hsRes.inrequest?.ztext.substring(0, stringlimit)
          def oZayavka = new Zayavka(hsRes.inrequest, hsRes.user.id, DATE_FORMAT)
        try{
          oZayavka.save(flush:true)
          if (hsRes.user.modstatus!=1)         
            hsRes.inrequest.isadd = mailerService.sendActivationProposalMail(hsRes.user, hsRes.context,hsRes?.isNeedSecondMail?:false)            
          else{
            hsRes.inrequest.isadd = mailerService.sendProposalMail(hsRes.user, hsRes.context)                           		 
            mailerService.sendAdminZayavkaMail(oZayavka.id) 
          }    
        } catch (Exception e){
          log.debug(" Error on save Zayavka(trip/saveWaiting):"+e.toString())
          flash.error<<8
          Zayavka.withSession { session -> session.clear() }
        }
      }
    }
    hsRes.inrequest.save = 1
    requestService.setStatistic('waiting_public',27)
    redirect(controller:"index",action:'waiting',params:hsRes.inrequest,base:hsRes.context.mainserverURL_lang)
    return
  }    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  def sitemap_home = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        for(oHome in Home.findAll("from Home where modstatus=1 and CHAR_LENGTH(IFNULL(description,''))>70")){
          def oHomecity = City.findByName(oHome.city)
          if ((oHomecity?.domain&&request.serverName==oHomecity?.domain)||(!oHomecity?.domain&&request.serverName.indexOf('.staytoday')==-1)) {
            url {
              loc(g.createLink(base: (oHomecity?.domain?'http://'+oHomecity.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(oHomecity?.domain?'hViewDomain':'hView'), params:(oHomecity?.domain?[linkname:oHome.linkname]:[country:Country.get(oHome.country_id)?.urlname,city:oHome.city,linkname:oHome.linkname])))
              lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(oHome.moddate.getTime())).toString()[0..-3]+':00')
              if(oHome.is_mainpage || oHome.is_specoffer || oHome.is_specoffer_auto)
                priority(0.80)
              else
                priority(0.64)
            }
          }
        }
      }
    }
  }

  def sitemap_direction = {
    if (!(request.serverName.indexOf('.staytoday')==-1)) {
      response.sendError(404)
      return
    }
    def hsRes=[:]
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        Country.findAllByIs_index(1).each{ country ->
          url {
            loc(g.createLink(base: "http://staytoday.ru", controller:'index', action:'popdirectionAll', params:[id:country.urlname]))
            changefreq('daily')
            priority(0.80)
          }
        }
        for(oPopDir in Popdirection.findAllWhere(modstatus:1)){
          url {
            loc(g.createLink(base: "http://staytoday.ru", controller:'index', action:'direction', params:[id:oPopDir.linkname,country:Country.get(oPopDir.country_id)?.urlname]))
            changefreq('daily')
            priority(0.80)
          }
          url {
            loc(g.createLink(base: "http://staytoday.ru", controller:'index', action:'popdiscounts', params:[id:oPopDir.linkname,country:Country.get(oPopDir.country_id)?.urlname]))
            changefreq('daily')
            priority(0.64)
          } 
        }
      }
    }
  }

  def sitemap_map = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
      group by h.city_id
      having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        hsRes.tagcloud.each { tag ->
          url {
            loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?'hSearchTypeDomain':'hSearch'), params:(tag.value.domain?[type_url:'all',view:'map']:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,view:'map'])))
            if(tag.value.moddate)
              lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
            else changefreq('daily')
            priority(0.64)
          }
        }
      }
    }
  }

  def sitemap_hometype = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
      group by h.city_id
      having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        def htypelist = Hometype.list()
        hsRes.tagcloud.each { tag ->
          hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[hometype_id:0],[:],false,false)
          hsRes.hometypeMax.each{ htype ->
            url {
              loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?(htype.id==1?'mainpage':'hSearchTypeDomain'):(htype.id==1?'hSearch':'hSearchType')), params:(tag.value.domain?(htype.id==1?[:]:[type_url:htypelist.find{it.id==htype.id}.urlname]):((htype.id==1?[:]:[type_url:htypelist.find{it.id==htype.id}.urlname])+[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname]))))
              if(tag.value.moddate)
                lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
              else changefreq('daily')
              priority(0.64)
            }
          }
        }
      }
    }
  }

  def sitemap_homeroom = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
      group by h.city_id
      having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        hsRes.tagcloud.each { tag ->
          hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[hometype_id:1],[:],false,false)
          hsRes.homeroomMax.each{ hroom ->
            url {
              loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?'hSearchRoomDomain':'hSearchRoom'), params:(tag.value.domain?[type_url:'flats',bedroom:hroom.key-'id']:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,type_url:'flats',bedroom:hroom.key-'id'])))
              if(tag.value.moddate)
                lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
              else changefreq('daily')
              priority(0.64)
            }
          }
        }
      }
    }
  }

  def sitemap_roommap = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
      group by h.city_id
      having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        hsRes.tagcloud.each { tag ->
          hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[hometype_id:1],[:],false,false)
          hsRes.homeroomMax.each{ hroom ->
            url {
              loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?'hSearchRoomDomain':'hSearchRoom'), params:(tag.value.domain?[type_url:'flats',bedroom:hroom.key-'id',view:'map']:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,type_url:'flats',bedroom:hroom.key-'id',view:'map'])))
              if(tag.value.moddate)
                lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
              else changefreq('daily')
              priority(0.64)
            }
          }
        }
      }
    }
  }

  def sitemap_longdiscount = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
      group by h.city_id
      having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        hsRes.tagcloud.each { tag ->
          if (oHome.csiFindByWhere(tag.key,-1,0,[:],[longdiscount:1],false,true).count)
            url {
              loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?'hSearchTypeDomain':'hSearchType'), params:(tag.value.domain?[type_url:'all',longdiscount:1]:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,type_url:'all',longdiscount:1])))
              if(tag.value.moddate)
                lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
              else changefreq('daily')
              priority(0.64)
            }
        }
      }
    }
  }

  def sitemap_hotdiscount = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
      group by h.city_id
      having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        hsRes.tagcloud.each { tag ->
          if (oHome.csiFindByWhere(tag.key,-1,0,[:],[hotdiscount:1],false,true).count)
            url {
              loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?'hSearchTypeDomain':'hSearchType'), params:(tag.value.domain?[type_url:'all',hotdiscount:1]:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,type_url:'all',hotdiscount:1])))
              if(tag.value.moddate)
                lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
              else changefreq('daily')
              priority(0.64)
            }
        }
      }
    }
  }

  def sitemap_city = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
      group by h.city_id
      having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        hsRes.tagcloud.each { tag ->
          url {
            loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?'hSearchTypeDomain':'hSearchType'), params:(tag.value.domain?[type_url:'all']:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,type_url:'all'])))
            if(tag.value.moddate)
              lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
            else changefreq('daily')
            priority(0.64)
          }
        }
      }
    }
  }

  def sitemap_metro = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
          from Home h, City c
          where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
          group by h.city_id
          having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        hsRes.tagcloud.each { tag ->
          if (City.findByName(tag.key)?.is_metro){
            hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[:],[:],false,true,false,true)
            hsRes.metroMax.each{ hmetro ->
              url {
                loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?'hSearchMetroDomain':'hSearchMetro'), params:(tag.value.domain?[metro_url:Metro.get(hmetro.id)?.urlname]:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,metro_url:Metro.get(hmetro.id)?.urlname])))
                if(tag.value.moddate)
                  lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
                else changefreq('daily')
                priority(0.64)
              }
            }
          }
        }
      }
    }
  }

  def sitemap_metromap = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
      group by h.city_id
      having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        hsRes.tagcloud.each { tag ->
          if (City.findByName(tag.key)?.is_metro){
            hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[:],[:],false,true,false,true)
            hsRes.metroMax.each{ hmetro ->
              url {
                loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?'hSearchMetroDomain':'hSearchMetro'), params:(tag.value.domain?[metro_url:Metro.get(hmetro.id)?.urlname,view:'map']:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,metro_url:Metro.get(hmetro.id)?.urlname,view:'map'])))
                if(tag.value.moddate)
                  lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
                else changefreq('daily')
                priority(0.64)
              }
            }
          }
        }
      }
    }
  }

  def sitemap_profile = {
    if (!(request.serverName.indexOf('.staytoday')==-1)) {
      response.sendError(404)
      return
    }
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        for(oUser in User.findAll("from User where modstatus=1 and 0=IFNULL(ref_id,0) and CHAR_LENGTH(IFNULL(description,''))>70")){
          url {
            loc(g.createLink(base: "http://staytoday.ru", mapping:'pView', params:[uid:'id'+oUser.id]))
            changefreq('daily')
            priority(0.64)
          }
        }
      }
    }
  }

  def sitemap_blog = {
    if (!(request.serverName.indexOf('.staytoday')==-1)) {
      response.sendError(404)
      return
    }
    def hsRes=[:]
    hsRes+=Tools.getCalendarFilter(message(code:'calendar.monthName').split(','),Tools.getIntVal(grailsApplication.config.timeline.filter.min.year,2011),Tools.getIntVal(grailsApplication.config.timeline.filter.min.month,8))
    hsRes.filterAuthors = Articles.findAll('from Articles where modstatus=1 group by author order by count(author) desc',[max:10]).collect{it.author}
    def tagList = Articles.withCriteria {
      createAlias('tags','t')
      eq('modstatus',1)
      projections {
        groupProperty('t.name')
        count('t.id')
      }
    }
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=tag[1];map}  
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        for(oArticle in Articles.findAllByModstatus(1,[fetch:[tags:"lazy"]])){
          url {
            loc(g.createLink(base: "http://staytoday.ru", mapping:'timeline', params:[blog:oArticle?.author,year:oArticle.inputdate.getYear()+1900,month:oArticle.inputdate.getMonth()+1<10?'0'+(oArticle.inputdate.getMonth()+1):oArticle.inputdate.getMonth()+1,day:oArticle.inputdate.getDate()<10?'0'+oArticle.inputdate.getDate():oArticle.inputdate.getDate(),id:oArticle.linkname]))
            changefreq('daily')
            priority(0.80)
          }
        }
        def i=0
        for(year in hsRes.filterYears){
          url {
            loc(g.createLink(base: "http://staytoday.ru", mapping:'timeline', params:[blog:'all',year:year]))
            changefreq('daily')
            priority(0.64)
          }
          def j=0
          for(month in hsRes.filterMonths[i]){
            url {
              loc(g.createLink(base: "http://staytoday.ru", mapping:'timeline', params:[blog:'all',year:year,month:((year==hsRes.curYear?hsRes.curMonth:12)-j<10?'0'+((year==hsRes.curYear?hsRes.curMonth:12)-j):(year==hsRes.curYear?hsRes.curMonth:12)-j)]))
              changefreq('daily')
              priority(0.64)
            }
            j++
          }
          i++
        }
        for(author in hsRes.filterAuthors){
          url {
              loc(g.createLink(base: "http://staytoday.ru", mapping:'timeline', params:[blog:author]))
              changefreq('daily')
              priority(0.64)
            }
        }
        for(tag in hsRes.tagcloud){
          url {
              loc(g.createLink(base: "http://staytoday.ru", mapping:'timeline', params:[tag:tag.key]))
              changefreq('daily')
              priority(0.64)
            }
        }
      }
    }
  }

  def sitemap_citysight = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id),c.domain,c.moddate
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id and c.domain=:domain
      group by h.city_id
      having count(h.id) > 0""",[domain:(request.serverName.indexOf('.staytoday')==-1?'':request.serverName)])
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1],domain:tag[2],moddate:tag[3]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation': "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd") {
        hsRes.tagcloud.each { tag ->
          hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[:],[:],false,true,false,false,true)
          hsRes.citysightMax.each{ hcitysight ->
            url {
              loc(g.createLink(base: (tag.value.domain?'http://'+tag.value.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL), mapping:(tag.value?.domain?'hSearchCitysightDomain':'hSearchCitysight'), params:(tag.value.domain?[citysight_url:Citysight.get(hcitysight.id)?.urlname]:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,citysight_url:Citysight.get(hcitysight.id)?.urlname])))
              if(tag.value.moddate)
                lastmod((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(tag.value.moddate.getTime())).toString()[0..-3]+':00')
              else changefreq('daily')
              priority(0.64)
            }
          }
        }
      }
    }
  }

  def advToYandex = {
    def bValid = false
    try {
      def ipAddr = this.request.remoteAddr
      ipAddr = ipAddr!='0:0:0:0:0:0:0:1'?ipAddr:'127.0.0.1'
      InetAddress thost = InetAddress.getByAddress(ipAddr.split('\\.').collect{it as int} as byte[])
      def hostName = thost.getCanonicalHostName()

      InetAddress tIP = InetAddress.getByName(hostName)
      def ip = tIP.getHostAddress()

      log.debug("advToYandex. incoming ipAddr: "+ipAddr)
      log.debug("advToYandex. incoming hostName: "+hostName)
      if ((hostName.matches('.*yandex.[com|ru|net|by].*')&&ip.toString()==ipAddr)||(Dynconfig.findByName('global.advToYandex.allowedIP')?.value?:'').split('\\|').find{ipAddr.matches(it)}) bValid = true

    } catch (UnknownHostException e) {
      log.debug("NO HOST: "+e.toString())
    } catch(Exception e2) {
      log.error("advToYandex:\n"+e2.toString())
    }

    log.debug("advToYandex. is_valid: "+bValid)
    if(!bValid)
      response.sendError(404)
    else
      render(contentType: 'text/xml', encoding: 'UTF-8') {
        mkp.yieldUnescaped '<?xml version="1.0" encoding="utf-8"?>'
        def today = Calendar.getInstance()
        def expire = today.clone()
        expire.add(Calendar.YEAR, 1)
        'realty-feed'(xmlns: "http://webmaster.yandex.ru/schemas/feed/realty/2010-06") {
          'generation-date'((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(today.getTime())).toString()[0..-3]+':00')
          for(oHome in Home.findAllWhere(modstatus:1)){
            def oUser = User.findByClient_id(oHome.client_id)
            def oHmp = Homeprop.findAll("FROM Homeprop WHERE date_end>=current_date AND home_id=:home_id AND modstatus=1 AND term>0 ORDER BY date_start",[home_id:oHome.id])[0]
            if (!oHmp) continue;
            offer('internal-id':oHome.id) {
              type('аренда')
              'property-type'('жилая')
              switch(oHome.hometype_id) {
                case 1: category('квартира');break;
                case 2: category('дом');break;
                case 4: category('комната');break;
                case 8: category('комната');break;
                case 9: category('комната');break;
                default: category('квартира');break;
              }
              url(g.createLink(base: "http://staytoday.ru", mapping:'hView', params:[country:Country.get(oHome.country_id)?.urlname,city:oHome.city,linkname:oHome.linkname,mode:Tools.generateModeParam(oHome.id,oHome.client_id)]))
              'creation-date'((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ").format(oHome.inputdate?:today.getTime())).toString()[0..-3]+':00')
              'last-update-date'((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ").format(oHome.moddate>(new Date()-30)?oHome.moddate:new Date()-3)).toString()[0..-3]+':00')
              'expire-date'((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ").format(expire.getTime())).toString()[0..-3]+':00')
              'manually-added'(true)
              'payed-adv'(false)
              location{
                country(Country.get(oHome.country_id).name)
                if (oHome.region_id!=77&&oHome.region_id!=78&&oHome.city_id!=182117) {//for moscow, spb, sevastopol region not need
                  region(Region.get(oHome.region_id)?.name)
                }
                'locality-name'(oHome.city?:'')
                if (oHome.district) {
                  if (oHome.region_id==77||oHome.region_id==78||oHome.city_id==182117) {//for moscow, spb, sevastopol
                    'sub-locality-name'(oHome.district?:'')
                  } else {
                    district(oHome.district?:'')
                  }
                }
                address(oHome.street+' '+oHome.homenumber)
                latitude(oHome.y/100000)
                longitude(oHome.x/100000)
              }
              'sales-agent'{
                name(oUser.firstname?:oUser.nickname)
                phone(oUser.tel)
                category('владелец')
              }
              price{
                value(oHmp.price)
                currency(Valuta.get(oHmp.valuta_id).code)
                period('день')
              }
              def lsPhoto = Homephoto.findAllByHome_id(oHome.id)
              for(photo in lsPhoto){
                image('http://img.staytoday.ru/'+oHome.client_id+'/'+photo.picture)
              }
              description(oHome.description)
              if (oHome.area>0) {
                area{
                  value(oHome.area)
                  unit('кв. м')
                }
              }
              rooms(Homeroom.get(oHome.homeroom_id)?.kol?:1)
              'rooms-offered'(Homeroom.get(oHome.homeroom_id)?.kol?:1)
            }
          }
        }
      }
  }

  def advToAbook = {
    def counter = 0
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="utf-8"?>'
      def today = Calendar.getInstance()
      def expire = today.clone()
      expire.add(Calendar.YEAR, 1)
      def valutaRate = new Valutarate().csiGetRate(840)
      'realty-feed'{
        'generation-date'((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(today.getTime())).toString()[0..-3]+':00')
        for(oHome in (Tools.getIntVal(Dynconfig.findByName('global.partner.exportonlyverified.enable')?.value,0)?new Home().csiGetPartnerHome():Home.findAll{ modstatus == 1 && region_id in [77,78,42,24,66,54,23,2,257,52,56,64,16,117,115,165]})){//66 - екат
          def oUser = User.findByClient_id(oHome.client_id)
          def oHmp = Homeprop.findAll("FROM Homeprop WHERE date_end>=current_date AND home_id=:home_id AND modstatus=1 AND term>0 AND price_rub>=1500 ORDER BY date_start",[home_id:oHome.id])
          if (oHmp.size()==0/*||++counter>10*/) continue;
          ++counter
          offer('internal-id':oHome.id,'last-update':(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ").format(oHome.moddate>(new Date()-30)?oHome.moddate:new Date()-3)).toString()[0..-3]+':00') {
            name(oHome.id+'-'+Tools.transliterate(oHome.name,0))
            category(Hometype.get(oHome.hometype_id)?.name_en?:'apartment')
            url(g.createLink(base: "http://staytoday.ru", mapping:'hView_en', params:[country:Country.get(oHome.country_id)?.urlname,city:City.get(oHome.city_id)?.name_en?:Tools.transliterate(oHome.city,0),linkname:oHome.linkname]))
            location{
              country(Country.get(oHome.country_id).name_en)
              city(City.get(oHome.city_id)?.name_en?:Tools.transliterate(oHome.city,0))
              address(Tools.transliterate(oHome.street+' '+oHome.homenumber,0))
              latitude(oHome.y/100000)
              longitude(oHome.x/100000)
            }
            'sales-agent'{
              name(Tools.transliterate(oUser?.firstname?:oUser?.nickname,0))
              phone(oUser?.tel)
              email(oUser?.email)
            }
            rateperiods{
              oHmp.each{ hmp ->
                rateperiod{
                  startdate(hmp.date_start.format(DATE_FORMAT))
                  enddate(hmp.date_end.format(DATE_FORMAT))
                  dailyrate(hmp.valuta_id!=840?Math.rint(100.0 * (hmp.price_rub?:0) / valutaRate) / 100.0:hmp.price)
                  weekendrate(hmp.valuta_id!=840?Math.rint(100.0 * (hmp.priceweekend_rub?:0) / valutaRate) / 100.0:hmp.priceweekend)
                  weekrate(hmp.valuta_id!=840?Math.rint(100.0 * (hmp.priceweek_rub?:0) / valutaRate) / 100.0:hmp.priceweek)
                  monthrate(hmp.valuta_id!=840?Math.rint(100.0 * (hmp.pricemonth_rub?:0) / valutaRate) / 100.0:hmp.pricemonth)
                  currency('USD')
                }
              }
            }
            if (oHome.deposit>0||oHome.fee>0||oHome.cleanup>0){
              fees{
                if(oHome.deposit>0) fee(type:'deposit'){ cost(oHome.valuta_id!=840?Math.rint(100.0 * oHome.deposit / valutaRate) / 100.0:oHome.deposit); currency('USD') }
                if(oHome.fee>0) fee(type:'per_addperson'){ cost(oHome.valuta_id!=840?Math.rint(100.0 * oHome.fee / valutaRate) / 100.0:oHome.fee); currency('USD') }
                if(oHome.cleanup>0) fee(type:'cleaning'){ cost(oHome.valuta_id!=840?Math.rint(100.0 * oHome.cleanup / valutaRate) / 100.0:oHome.cleanup); currency('USD') }
              }
            }
            def lsPhoto = Homephoto.findAllByHome_id(oHome.id)
            for(photo in lsPhoto){
              image('http://img.staytoday.ru/'+oHome.client_id+'/'+photo.picture)
            }
            description(oHome.description)
            if (oHome.area>0) {
              area{
                value(oHome.area)
                unit('sq. m')
              }
            }
            rooms(Homeroom.get(oHome.homeroom_id)?.kol?:1)
            'rooms-offered'(Homeroom.get(oHome.homeroom_id)?.kol?:1)
            details{
              maximumOccupancy(oHome.homeperson_id)
              bathrooms(oHome.homebath_id)
              minimumStayLength(oHome.rule_minday_id)
              checkin(Rule_timein.get(oHome.rule_timein_id).name_en)
              checkout(Rule_timeout.get(oHome.rule_timeout_id).name_en)
            }
            suitability{
              is_parking(oHome.is_parking as Boolean)
              is_nosmoking(oHome.is_nosmoking as Boolean)
              is_kitchen(oHome.is_kitchen as Boolean)
              is_tv(oHome.is_tv as Boolean)
              is_wifi(oHome.is_wifi as Boolean)
              is_holod(oHome.is_holod as Boolean)
              is_microwave(oHome.is_microwave as Boolean)
              is_cond(oHome.is_cond as Boolean)
              is_heat(oHome.is_heat as Boolean)
              is_wash(oHome.is_wash as Boolean)
              is_breakfast(oHome.is_breakfast as Boolean)
              is_invalid(oHome.is_invalid as Boolean)
              is_family(oHome.is_family as Boolean)
              is_visa(oHome.is_visa as Boolean)
              is_hall(oHome.is_hall as Boolean)
              is_area(oHome.is_area as Boolean)
              is_kettle(oHome.is_kettle as Boolean)
              is_hometheater(oHome.is_hometheater as Boolean)
              is_coffee(oHome.is_coffee as Boolean)
              is_jacussi(oHome.is_jacuzzi as Boolean)
            }
            rating(oHome.rating)
            paymentType(Partnerway.get(Client.get(oHome.client_id)?.partnerway_id?:0)?.name_en?:'Cash')
          }
        }
      }
    }
    log.debug("advToAbook. offer count: "+counter)
  }

  def islandsToYandex = {        
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)    
    def oValuta=Valuta.get(857)    
    
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      site(xmlns:"http://interactive-answers.webmaster.yandex.ru/schemas/site/0.0.1",
        'xmlns:xsi':"http://www.w3.org/2001/XMLSchema-instance",
        'xsi:schemaLocation':"http://interactive-answers.webmaster.yandex.ru/schemas/site-0.0.1.xsd") {
        
        title(hsRes.infotext?.title?:'')
        description(hsRes.infotext?.description?:'')
        rootUrl("http://staytoday.ru")
        protocol('HTTP')
        
        Resource {
          fixed(name:"staytoday.ru/home/search",terminal:"true")          
        }
        filters {
          dropDown {
            description(caption:"Страна"){
              setParameter(name:"country")
            }
            Country.findAllByModstatus(1).each{ country ->            
              dropDownValue(key:country.urlname,caption:country.name){
                dropDown {
                  description(caption:"Город"){
                    setParameter(name:"where")
                  } 
                  City.findAllByCountry_idAndHomecountGreaterThan(country.id,0,[sort:'name',order:'asc']).each{ city ->
                    dropDownValue(key:city.name,caption:city.name){
                      if(city.is_metro==1){
                        dropDown {
                          description(caption:"Метро"){
                            setParameter(name:"metro_id")
                          }
                          Metro.findAllByRegion_idAndModstatus(city.region_id,1,[sort:'name',order:'asc']).each{ metro ->
                            dropDownValue(key:metro.id,caption:metro.name.replace('"',''))
                          }                          
                        }                                     
                      }else if(Citysight.findAllByCity_idAndHomecountGreaterThan(city.id,0,[sort:'name',order:'asc']).size()>0){
                        dropDown {
                          description(caption:"Ориентир"){
                            setParameter(name:"citysight_id")
                          }                      
                          Citysight.findAllByCity_idAndHomecountGreaterThan(city.id,0,[sort:'name',order:'asc']).each{ sight ->
                            dropDownValue(key:sight.id,caption:sight.name) 
                          }                          
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          dropDown {
            description(caption:"Вид жилья"){
              setParameter(name:"hometype_id")
            } 
            Hometype.list().each{ hometype ->
              dropDownValue(key:hometype.urlname,caption:hometype.name)
            }
          }
          /*dropDown {
            description(caption:"Гости"){
              setParameter(name:"homeperson_id")
            } 
            Homeperson.list().each{ homeperson ->
              dropDownValue(key:homeperson.id,caption:homeperson.name)
            }
          }
          rangeDate(format:"dd-MM-yyyy",defaultFrom:"0",defaultTo:"1",captionFrom:"Дата заезда",captionTo:"Дата отъезда"){
            description(caption:"Даты"){
              setParameter(name:"date_start")
              setParameter(name:"date_end")
            }
          }*/
          rangeFilter(min:oValuta.min,max:oValuta.max,step:oValuta.step,unit:oValuta.code,captionFrom:"от",captionTo:"до"){
            description(caption:"Цена"){
              setParameter(name:"price_min")
              setParameter(name:"price_max")
            }
          }
        }
      }
    }    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  def sitemap_home_mobile = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:mobile': "http://www.google.com/schemas/sitemap-mobile/1.0") {
        for(oHome in Home.findAll("from Home where modstatus=1 and CHAR_LENGTH(IFNULL(description,''))>70")){        
          def oHomecity = City.findByName(oHome.city)
            url {
              loc(g.createLink(base:hsRes.context.mobileURL, mapping:'hView', params:[country:Country.get(oHome.country_id)?.urlname,city:oHome.city,linkname:oHome.linkname]))
              'mobile:mobile'()
            }          
        }
      }
    }
  }
  
  def sitemap_city_mobile = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def tagList = Home.executeQuery("""select c.name,count(h.id)
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > 0""")
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:mobile': "http://www.google.com/schemas/sitemap-mobile/1.0"){        
        hsRes.tagcloud.each { tag ->
          url {
            loc(g.createLink(base:hsRes.context.mobileURL, mapping:'hSearchType', params:([where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,type_url:'all'])))
            'mobile:mobile'()
          }
        }
      }
    }
  }

  def sitemap_hometype_mobile = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id)
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > 0""")
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:mobile': "http://www.google.com/schemas/sitemap-mobile/1.0") {
        def htypelist = Hometype.list()
        hsRes.tagcloud.each { tag ->
          hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[hometype_id:0],[:],false,false)
          hsRes.hometypeMax.each{ htype ->
            url {
              loc(g.createLink(base:hsRes.context.mobileURL, mapping:htype.id==1?'hSearch':'hSearchType', params:(htype.id==1?[:]:[type_url:htypelist.find{it.id==htype.id}.urlname])+[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname]))
              'mobile:mobile'()
            }
          }
        }
      }
    }
  }

  def sitemap_homeroom_mobile = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id)
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > 0""")
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:mobile': "http://www.google.com/schemas/sitemap-mobile/1.0") {
        hsRes.tagcloud.each { tag ->
          hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[hometype_id:1],[:],false,false)
          hsRes.homeroomMax.each{ hroom ->
            url {
              loc(g.createLink(base:hsRes.context.mobileURL, mapping:'hSearchRoom', params:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,type_url:'flats',bedroom:hroom.key-'id'])) 
              'mobile:mobile'()              
            }
          }
        }
      }
    }
  }

  def sitemap_longdiscount_mobile = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id)
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > 0""")
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:mobile': "http://www.google.com/schemas/sitemap-mobile/1.0") {
        hsRes.tagcloud.each { tag ->
          if (oHome.csiFindByWhere(tag.key,-1,0,[:],[longdiscount:1],false,true).count)
            url {
              loc(g.createLink(base:hsRes.context.mobileURL, mapping:'hSearchType', params:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,type_url:'all',longdiscount:1])) 
              'mobile:mobile'()              
            }
        }
      }
    }
  }

  def sitemap_hotdiscount_mobile = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id)
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > 0""")
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:mobile': "http://www.google.com/schemas/sitemap-mobile/1.0") {
        hsRes.tagcloud.each { tag ->
          if (oHome.csiFindByWhere(tag.key,-1,0,[:],[hotdiscount:1],false,true).count)
            url {
              loc(g.createLink(base:hsRes.context.mobileURL, mapping:'hSearchType', params:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,type_url:'all',hotdiscount:1]))
              'mobile:mobile'()
            }
        }
      }
    }
  } 

  def sitemap_metro_mobile = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id)
          from Home h, City c
          where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
          group by h.city_id
          having count(h.id) > 0""")
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:mobile': "http://www.google.com/schemas/sitemap-mobile/1.0") {
        hsRes.tagcloud.each { tag ->
          if (City.findByName(tag.key)?.is_metro){
            hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[:],[:],false,true,false,true)
            hsRes.metroMax.each{ hmetro ->
              url {
                loc(g.createLink(base:hsRes.context.mobileURL, mapping:'hSearchMetro', params:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,metro_url:Metro.get(hmetro.id)?.urlname]))
                'mobile:mobile'()
              }
            }
          }
        }
      }
    }
  }

  def sitemap_citysight_mobile = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def oHome = new HomeSearch()
    def tagList = Home.executeQuery("""select c.name,count(h.id)
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > 0""")
    hsRes.tagcloud = tagList.inject([:]){map,tag -> map[tag[0]]=[count:tag[1]];map}
    render(contentType: 'text/xml', encoding: 'UTF-8') {
      mkp.yieldUnescaped '<?xml version="1.0" encoding="UTF-8"?>'
      urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9",
        'xmlns:mobile': "http://www.google.com/schemas/sitemap-mobile/1.0") {
        hsRes.tagcloud.each { tag ->
          hsRes+=oHome.csiFindByWhere(tag.key,-1,0,[:],[:],false,true,false,false,true)
          hsRes.citysightMax.each{ hcitysight ->
            url {
              loc(g.createLink(base:hsRes.context.mobileURL, mapping:'hSearchCitysight', params:[where:tag.key,country:Country.get(City.findByName(tag.key)?.country_id)?.urlname,citysight_url:Citysight.get(hcitysight.id)?.urlname]))
              'mobile:mobile'()
            }
          }
        }
      }
    }
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def robots = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    if(Tools.getIntVal(grailsApplication.config.is_robots_txt,0)){
      if(hsRes.context.is_dev){
        render(contentType: 'text/plain', encoding: 'UTF-8'){ 
          mkp.yield Temp_robots.findWhere(is_dev:1,domain:request.serverName)?.robots?:''
        }
      } else {
        render(contentType: 'text/plain', encoding: 'UTF-8'){ 
          mkp.yield Temp_robots.findWhere(is_dev:0,domain:request.serverName)?.robots?:''
        }
      }
    }else{
      render(contentType: 'text/plain', encoding: 'UTF-8'){ 
        mkp.yield Temp_robots.findWhere(is_dev:1,domain:request.serverName)?.robots?:''
      }
    }		
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////
  def wmresult = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes+=requestService.getParams(null,null,['LMI_PAYMENT_NO','LMI_PAYEE_PURSE','LMI_PAYMENT_AMOUNT','LMI_MODE',
                                      'LMI_SYS_INVS_NO','LMI_SYS_TRANS_NO','LMI_SYS_TRANS_DATE','LMI_PAYER_PURSE',
                                      'LMI_PAYER_WM','LMI_HASH'])
    if (!hsRes.inrequest?.'LMI_PAYMENT_NO') {
      render ""
      return
    }
    hsRes.payorder = Payorder.findByNorder((hsRes.inrequest?.'LMI_PAYMENT_NO'?'st'+hsRes.inrequest?.'LMI_PAYMENT_NO':''))
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.configParams = [
      secretKey:grailsApplication.config.wmoney.secretKey?grailsApplication.config.wmoney.secretKey.trim():'8D38F03EE4415A6E0199AB6D66051F64',
      merchant:grailsApplication.config.wmoney.merchant?grailsApplication.config.wmoney.merchant.trim():'R309646236390',
      testmode:Tools.getIntVal(grailsApplication.config.wmoney.testmode,1)
    ]
    hsRes.purchaseamt = hsRes.configParams.testmode?1f:(hsRes.payorder.summa as float)
    def requestStr = ''
    requestStr += hsRes.inrequest.LMI_PAYEE_PURSE?:''
    requestStr += hsRes.inrequest.LMI_PAYMENT_AMOUNT?:''
    requestStr += hsRes.inrequest.LMI_PAYMENT_NO?:''
    requestStr += hsRes.inrequest.LMI_MODE?:''
    requestStr += hsRes.inrequest.LMI_SYS_INVS_NO?:''
    requestStr += hsRes.inrequest.LMI_SYS_TRANS_NO?:''
    requestStr += hsRes.inrequest.LMI_SYS_TRANS_DATE?:''
    requestStr += hsRes.configParams.secretKey
    requestStr += hsRes.inrequest.LMI_PAYER_PURSE?:''
    requestStr += hsRes.inrequest.LMI_PAYER_WM?:''

    if (requestStr.encodeAsMD5().toUpperCase()==hsRes.inrequest.LMI_HASH
        &&hsRes.inrequest.LMI_MODE==hsRes.configParams.testmode.toString()
        &&hsRes.inrequest.LMI_PAYEE_PURSE==hsRes.configParams.merchant
        &&(hsRes.inrequest.LMI_PAYMENT_AMOUNT as float)==hsRes.purchaseamt) {
      billingService.doTransaction( billingService.createPaytrans(hsRes.payorder,'',2) )
      log.debug('Wm payment is complete')
    } else {
      log.debug('InCorrect Wm payment')
      response.sendError(404)
      return
    }

    render "YES"
    return
  }
  def specoffer={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true,false,true)
    hsRes.popcity=City.findAllByIs_specofferAndHomecountGreaterThan(1,10,[max:9,sort:'homecount',order:'desc'])    
    hsRes.popdirection=Popdirection.findAllByIs_specofferAndModstatus(1,1,[sort:'country_id',order:'asc'])
    
    hsRes.imageurl = grailsApplication.config.urlpopdiphoto
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    
    hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
    if(hsRes?.user?.id)
      hsRes.user=User.get(hsRes?.user?.id)
    
    def oHomeSearch=new HomeSearch()       
    hsRes.avgPrice1=[]
    hsRes.avgPrice2=[]
    hsRes.minPrice=[]
    for(city in hsRes.popcity){
      def AVG1=oHomeSearch.csiGetAvgPriceByRoomTypeId(city.id,1)      
      AVG1=((AVG1?:[]).size()==1)?AVG1[0]:0
      def AVG2=oHomeSearch.csiGetAvgPriceByRoomTypeId(city.id,2)      
      AVG2=((AVG2?:[]).size()==1)?AVG2[0]:0
      def MinPrice=oHomeSearch.csiGetMinPriceByCity(city.id)
      MinPrice=((MinPrice?:[]).size()==1)?MinPrice[0]:0      
      
      hsRes.avgPrice1<<AVG1
      hsRes.avgPrice2<<AVG2
      hsRes.minPrice<<MinPrice                            
    } 
    
    hsRes.minPricePopdir=[]
    hsRes.hotdiscount=[]
    hsRes.longdiscount=[]
    hsRes.poprecords=[] 
    for(popdir in hsRes.popdirection){
      def reg_ids=[]
      def lsRegion=Region.findAllWhere(popdirection_id:popdir.id)      
      for(region in lsRegion){
        reg_ids<<region.id
      }
      def minPricePopdir=oHomeSearch.csiGetMinPriceByRegion(reg_ids)
      minPricePopdir=((minPricePopdir?:[]).size()==1)?minPricePopdir[0]:0
      hsRes.minPricePopdir << minPricePopdir 
      hsRes.hotdiscount << oHomeSearch.csiFindByWhere(popdir.keyword,Tools.getIntVal(grailsApplication.config.discounts.quantity.max,-1),0,[order:-1],[hotdiscount:1],true,false).count
      hsRes.longdiscount << oHomeSearch.csiFindByWhere(popdir.keyword,Tools.getIntVal(grailsApplication.config.discounts.quantity.max,-1),0,[order:-1],[longdiscount:1],true,false).count     
      def poprecords = oHomeSearch.csiFindPopdirection(popdir.id)
      hsRes.poprecords<<(poprecords?:[]).size()
    }
    return hsRes
    //render(view:'_mail_specoffer',model:hsRes)
    //return
  }
  def tospecoffer={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams([],[],['email','nickname'])    
    
    flash.error=0
    if(hsRes?.user?.id){
      def oUser=User.get(hsRes?.user?.id)
      oUser.is_subscribe=1
      
      if(!oUser.save(flush:true)) {
        log.debug(" Error on save user")
        oUser.user.errors.each{log.debug(it)}
      }
      flash.error=3
    }else{
      if(!hsRes.inrequest?.email){
        flash.error=1
      }else{
        if(User.findWhere(email:hsRes.inrequest?.email)){
          flash.error=2
        }else{
          def oUser = new User()
          if (oUser.mboxNewUser([email:hsRes.inrequest.email,nickname:hsRes.inrequest.nickname])) {
            if (usersService.loginInternalUser(hsRes.inrequest.email,Tools.hidePsw(hsRes.inrequest.email)[0..8],requestService,1)){
              hsRes.user = User.findByName(hsRes.inrequest.email)
              hsRes.user.is_subscribe=1
              hsRes.user.lastdate=new Date()
              
              if(!hsRes.user.save(flush:true)) {
                log.debug(" Error on save user")
                hsRes.user.user.errors.each{log.debug(it)}
              }
            }
            
            mailerService.sendNewUser(hsRes.user.id,hsRes.context)
            flash.error=4
          }
        }
      }
    }
    render flash as JSON
    return     
  }
  def unsubscribe={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
    if(hsRes?.user?.id)
      hsRes.user=User.get(hsRes?.user?.id)     
    return hsRes
  }
  
  def to_specoffer_refuse={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams([],[],['email','password'])    
    
    flash.error=0
    if(hsRes?.user?.id){
      def oUser=User.get(hsRes?.user?.id)
      oUser.is_subscribe=0
      
      if(!oUser.save(flush:true)) {
        log.debug(" Error on save user")
        oUser.user.errors.each{log.debug(it)}
      }
      flash.error=4
    }else{
      if(!hsRes.inrequest?.email){
        flash.error=1
      }else if(!hsRes.inrequest?.password){
        flash.error=2
      }else{
        if(!User.findWhere(email:hsRes.inrequest?.email)){
          flash.error=3
        }else{
          def oUser = User.findWhere(email:hsRes.inrequest?.email)    
          
          if (usersService.loginInternalUser(hsRes.inrequest.email,hsRes.inrequest.password,requestService,1)){            
            oUser.is_subscribe=0            
            oUser.lastdate=new Date()
              
            if(!oUser.save(flush:true)) {
              log.debug(" Error on save user")
              oUser.user.errors.each{log.debug(it)}
            }
            
            flash.error=5
          }
        }
      }
    }
    render flash as JSON
    return     
  }  
  
  def noticeClick={
    requestService.init(this)
    def iId=requestService.getIntDef('id',0)
    if(iId){
      def oNotice=Notice.get(iId)
      if(oNotice){
        oNotice.nclick++
        if(!oNotice.save(flush:true)) {
          log.debug(" Error on save Notice:")    
          oNotice.errors.each{log.debug(it)}
        } 
      }
    }
    render(contentType:"application/json"){[error:false]}
    return
  }

  def justtest = {
/*    def _paytrans = Api_response.get(1)
    def sUrl = 'http://www.abookingnet.com'
//    def sUrl = 'http://api.abookingnet.mod.bz'
    def sPath = '/api/host/booking/quote.xml'
//    def sPath = '/api/provider/xml/booking/quote'
    def hsBody = [:]
    hsBody.id = _paytrans.outer_id
    hsBody.checkIn = _paytrans.date_start.format(DATE_FORMAT)
    hsBody.checkOut = _paytrans.date_end.format(DATE_FORMAT)
    hsBody.total = _paytrans.price
    hsBody.currency = "USD"
    hsBody.guests = _paytrans.homeperson_id
    hsBody.comment = _paytrans.mtext
    hsBody.api_key = /*Api.get(_paytrans.api_id)?.outer_key?:''
    if (!smsService.postJSONdata(sUrl,sPath,hsBody)) {
      println ('Error: bad response: '+_paytrans.id)
    }
    //render smsService.testapi()
    //render(contentType: 'text/xml', encoding: 'UTF-8', text:smsService.testapi())
    //apiService.doResponse(1)*/
    render "<OK/>"
  }

  def justtest3 = {
/*    println 2222
      response.setHeader("Content-disposition", "attachment; filename=Viborka.csv")    
   
    response.contentType = 'text/csv;charset=windows-1251'
    response.outputStream << 'sCsv'.getBytes('windows-1251');
    response.outputStream.flush()     */
    render file: 'sCsv'.getBytes('windows-1251'), contentType: 'image/jpeg'
  }
  def justtest2 = {
    println "justtest2"
    println request.JSON
    render ([success:true] as JSON)
    //render ([success:false,status:100500] as JSON)
  }

  def mail_test={
    requestService.init(this)       
    
    log.debug("message=" + requestService.getStr("message"))
    log.debug("subject=" + requestService.getStr("subject"))
    log.debug("from=" + requestService.getStr("from"))
    log.debug("to=" + requestService.getStr("to"))
    log.debug("sender=" + requestService.getStr("sender"))
    log.debug("date=" + requestService.getStr("date"))
    
  }
}