//import org.codehaus.groovy.grails.commons.grailsApplication
import grails.converters.JSON
class HomeController {
  def requestService
  def bannersService
  def jcaptchaService
  def mailerService
  def smsService
  def usersService
  def searchService
  def homeService
  def androidGcmService
  def COOKIENAME='last_home_detail'  

  def static final DATE_FORMAT='yyyy-MM-dd'

  def index = { }
  def cancellation = {
    if(!(request.serverName.indexOf('.staytoday')==-1)){
      response.sendError(404)
      return
    }
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.cancellation=Rule_cancellation.findAll('FROM Rule_cancellation')

    return hsRes
  }
  def search = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def sWhere=requestService.getStr('where')
    def oCity = City.get(Sphinx.searchCityBySphinxLimit(sWhere,log,hsRes.context?.lang).ids[0])
    def iMetro_id = requestService.getIntDef('metro_id',0)
    def sType_url = requestService.getStr('hometype_id')
    def iBedroom = requestService.getIntDef('bedroom',0)
    def iCitySight_id = requestService.getIntDef('citysight_id',0)
    if (oCity) {
      params.where = oCity['name'+hsRes.context?.lang]      
      if (sType_url&&sType_url.isInteger()) sType_url = Hometype.get(sType_url.toInteger())?.urlname
      if (iMetro_id){
        redirect(mapping:"hSearchMetro"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,metro_url:Metro.get(iMetro_id)?.urlname]+(sType_url&&sType_url!='all'?[hometype_id:Hometype.findByUrlname(sType_url)?.id]:[:])-[hometype_id:sType_url.toString(),metro_id:iMetro_id.toString(),homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id]-(sType_url!='flats'?[bedroom:iBedroom.toString()]:[:]),permanent:true)
      } else if (iBedroom&&sType_url=='flats'){
        redirect(mapping:"hSearchRoom"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname]+(sType_url?[type_url:sType_url]:[:])-[hometype_id:sType_url.toString(),homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id],permanent:true)
      } else if (iCitySight_id){
        redirect(mapping:"hSearchCitysight"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,citysight_url:Citysight.get(iCitySight_id)?.urlname]+(sType_url&&sType_url!='all'?[hometype_id:Hometype.findByUrlname(sType_url)?.id]:[:])-[hometype_id:sType_url?.toString(),citysight_id:iCitySight_id.toString(),homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id]-(sType_url!='flats'?[bedroom:iBedroom.toString()]:[:]),permanent:true)
      } else if (sType_url!='flats'){
        redirect(mapping:"hSearchType"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,type_url:Hometype.findByUrlname(sType_url?:'')?.urlname?:'all']-[hometype_id:sType_url?.toString(),homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id]-(sType_url!='flats'?[bedroom:iBedroom.toString()]:[:]),permanent:true)
      } else {    
        redirect(mapping:"hSearch"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname]-[hometype_id:sType_url?.toString(),homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id],permanent:true)
      }
    } else {
      if (iBedroom&&sType_url=='flats')
        redirect(mapping:"cSearchRoom"+hsRes.context?.lang,params:params-[hometype_id:params.hometype_id,homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id]+[type_url:sType_url],permanent:true)
      else if (sType_url)
        redirect(mapping:"cSearchType"+hsRes.context?.lang,params:params-[hometype_id:params.hometype_id,homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id]+[type_url:sType_url],permanent:true)
      else
        redirect(action:'list',params:params-[homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id],base:hsRes.context?.mainserverURL_lang,permanent:true)
    }
    return
  }
  /////////////////////////////////////////////////////////////////////////////////////  
  def s = {
    requestService.init(this)    
    def hsRes=requestService.getContextAndDictionary(true)
    def sWhere=requestService.getStr('where')
    def oCity = City.get(Sphinx.searchCityBySphinxLimit(sWhere,log,hsRes.context?.lang).ids[0])
    def iMetro_id = requestService.getIntDef('metro_id',0)
    def iType_id = requestService.getIntDef('hometype_id',0)
    def iBedroom = requestService.getIntDef('bedroom',0)
    def iCitySight_id = requestService.getIntDef('citysight_id',0)
    def sCitySight_id = requestService.getStr('citysight_id')
    if (oCity) {      
      params.where = oCity['name'+hsRes.context?.lang]
      if (!iCitySight_id&&sCitySight_id)
        iCitySight_id = Citysight.findWhere(city_id:oCity.id,urlname:sCitySight_id)?.id?:0
      if (iMetro_id)
        redirect(mapping:"hSearchMetro"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,metro_url:Metro.get(iMetro_id)?.urlname]-[metro_id:iMetro_id.toString(),homeperson_id:'1',homeclass_id:params.homeclass_id],permanent:true)
      else if (iBedroom&&(iType_id==1||!iType_id))
        redirect(mapping:"hSearchRoom"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,type_url:'flats']-[homeperson_id:'1',hometype_id:params.hometype_id,homeclass_id:params.homeclass_id],permanent:true)
      else if(iCitySight_id)
        redirect(mapping:"hSearchCitysight"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,citysight_url:Citysight.get(iCitySight_id)?.urlname]-[citysight_id:iCitySight_id.toString(),homeperson_id:'1',hometype_id:'0',homeclass_id:params.homeclass_id],permanent:true)
      else if ((iType_id && iType_id!=1 && Hometype.get(iType_id)) || (!iType_id && requestService.getStr('type_url')))
        redirect(mapping:"hSearchType"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,type_url:Hometype.get(iType_id)?.urlname?:'all']-[hometype_id:params.hometype_id,homeperson_id:'1',homeclass_id:params.homeclass_id],permanent:true)
      else
        redirect(mapping:"hSearch"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname]-[hometype_id:params.hometype_id,homeperson_id:'1',homeclass_id:params.homeclass_id],permanent:true)
    } else {
      if (iBedroom&&iType_id==1)
        redirect(mapping:"cSearchRoom"+hsRes.context?.lang,params:params-[hometype_id:params.hometype_id,homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id]+[type_url:Hometype.get(iType_id)?.urlname],permanent:true)
      else if (iType_id)
        redirect(mapping:"cSearchType"+hsRes.context?.lang,params:params-[hometype_id:params.hometype_id,homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id]+[type_url:Hometype.get(iType_id)?.urlname],permanent:true)
      else
        redirect(action:'list',params:params-[homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id],base:hsRes.context?.mainserverURL_lang,permanent:true)
    }
    return
  }
  def list = {           
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    def coordinates=searchService.findViewPortTile(requestService.getStr('param')?:'0')
    hsRes.param=requestService.getStr('param')
    hsRes+=requestService.getParams(['hometype_id','term','price',//'country_id','region_id',
      'valuta_id','pindex','usage','is_nosmoking','is_parking','is_visa','is_tv','is_internet','is_wifi','is_cond',
      'is_heat','is_kitchen','is_holod','is_microwave','is_wash','is_breakfast','is_swim','is_steam','is_gym',
      'is_hall','is_family','is_pets','is_invalid','is_area','is_beach','nref','rating','hotdiscount','longdiscount',
      'sort','bathroom','bed','date_start_day','date_start_month','date_start_year','date_end_day',
      'date_end_month','date_end_year','is_fiesta','offset',
      'is_coffee','is_fen','is_hometheater','is_iron','is_jacuzzi','is_kettle','is_vip','is_renthour','shome'],
      ['price_min','price_max','x','y','zoom'],
      ['title','description','city','district','street','email','homenumber','keywords','type_url'],
      ['date_start','date_end'])
    hsRes.inrequest.view=requestService.getStr('view')?:'list'
    hsRes.inrequest.is_map=requestService.getLongDef('is_map',0)
    hsRes.inrequest.search_in_map=requestService.getLongDef('search_in_map',0)
    hsRes.alike=requestService.getIntDef('alike',0)

    if (hsRes.inrequest.hometype_id&&!Hometype.get(hsRes.inrequest.hometype_id)) {
      response.sendError(404)
      return
    }
    if (hsRes.inrequest.hometype_id==1) {
      hsRes.inrequest.bedroom = requestService.getLongDef('bedroom',0)
      if(hsRes.inrequest.bedroom&&!Homeroom.get(hsRes.inrequest.bedroom)){
        response.sendError(404)
        return
      }
    } else if (requestService.getLongDef('bedroom',0)) {
      response.sendError(404)
      return
    }                
    if (hsRes.inrequest.shome&&!Shome.findByCity_idAndType_idAndModstatus(City.findByDomain(request.serverName)?.id,homeService.getShomeType(hsRes.inrequest),1)) {
      response.sendError(404)
      return
    }

    hsRes.inrequest.homeperson_id=requestService.getIntDef('homeperson_id',1)

    if(hsRes.inrequest?.date_start?:'')
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)    
    if(hsRes.inrequest?.date_end?:'')
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)    

    hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
    hsRes.inrequest.where=requestService.getPreparedStr('where')
    hsRes.inrequest.where1=requestService.getPreparedStr('where').toLowerCase()
    hsRes.inrequest.wrongwhere=requestService.getStr('wrongwhere')
    hsRes.inrequest.homeclass_id=requestService.getIds('homeclass_id')
    hsRes.inrequest.district_id=requestService.getIds('district_id') 
    hsRes.inrequest.metro_id=requestService.getIds('metro_id')    
    hsRes.inrequest.citysight_id=requestService.getIds('citysight_id')
    hsRes.inrequest.hometype_id_filter=requestService.getIds('hometype_id_filter')
    hsRes.inrequest.room_id_filter=requestService.getIds('room_id_filter')            
	
    //hidInrequest is for full filter to search_table
	  hsRes.hidInrequest=requestService.getParams(['term','price',//'country_id','region_id',
      'valuta_id','pindex','usage','is_nosmoking','is_parking','is_visa','is_tv','is_internet','is_wifi','is_cond',
      'is_heat','is_kitchen','is_holod','is_microwave','is_wash','is_breakfast','is_swim','is_steam','is_gym',
      'is_hall','is_family','is_pets','is_invalid','is_area','is_beach','bathroom','bed','bedroom',
      'is_coffee','is_fen','is_hometheater','is_iron','is_jacuzzi','is_kettle'],[],[],[]).inrequest //'homeclass_id','district_id','metro_id'
    hsRes.hidInrequest_district_id=hsRes.inrequest.district_id?:[]
    hsRes.hidInrequest_metro_id=hsRes.inrequest.metro_id?:[]
    hsRes.hidInrequest_citysight_id=hsRes.inrequest.citysight_id?:[]
    
    hsRes.hidInrequest_apart=requestService.getParams(['is_fiesta','is_vip','is_renthour']).inrequest    

    def oCity = City.findByName(hsRes.inrequest.where)
    if((oCity && hsRes.context?.lang) || (City.findByName_en(hsRes.inrequest.where) && !hsRes.context?.lang)){
      response.setHeader("Pragma", "no-cache");
      response.setDateHeader("Expires", 1L);
      response.setHeader("Cache-Control", "no-cache");
      response.addHeader("Cache-Control", "no-store");
      redirect(action:'s',params:params-[lang:params.lang,homeclass_id:params.homeclass_id],permanent:true)
      return
    }
    if(!oCity && hsRes.context?.lang)
      oCity = City.findByName_en(hsRes.inrequest.where)      

    if(oCity && hsRes.inrequest?.type_url!='all' && !hsRes.inrequest.hometype_id && !hsRes.inrequest.citysight_id && !hsRes.inrequest.metro_id)
        hsRes.inrequest.hometype_id=1
    
    if(oCity && !oCity?.domain){                   
      if(hsRes.inrequest?.type_url=='flats'&&!hsRes.inrequest.bedroom){      
        redirect(mapping:"hSearch"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname]-[hometype_id:hsRes.inrequest?.hometype_id,type_url:'flats',homeperson_id:'1',view:'list',homeclass_id:params.homeclass_id],permanent:true)
        return
      }  
    }    

    if (oCity?.domain&&request.serverName!=oCity?.domain) {    
      if ((hsRes.inrequest.metro_id?:[]).size()==1)
        redirect(mapping:"hSearchMetroDomain"+hsRes.context?.lang,params:params+[metro_url:Metro.get(hsRes.inrequest.metro_id[0])?.urlname]-[where:params.where,country:params.country,metro_id:params.metro_id,homeclass_id:params.homeclass_id],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      else if (hsRes.inrequest.bedroom&&hsRes.inrequest.hometype_id==1)
        redirect(mapping:"hSearchRoomDomain"+hsRes.context?.lang,params:params-[where:params.where,country:params.country,hometype_id:params.hometype_id,homeclass_id:params.homeclass_id],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      else if ((hsRes.inrequest.citysight_id?:[]).size()==1)
        redirect(mapping:"hSearchCitysightDomain"+hsRes.context?.lang,params:params+[citysight_url:Citysight.get(hsRes.inrequest.citysight_id[0])?.urlname]-[where:params.where,country:params.country,citysight_id:params.citysight_id,homeclass_id:params.homeclass_id],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      else if (hsRes.inrequest.hometype_id>1)
        redirect(mapping:'hSearchTypeDomain'+hsRes.context?.lang,params:params-[where:params.where,country:params.country,hometype_id:params.hometype_id,homeclass_id:params.homeclass_id],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      else if (hsRes.inrequest.hometype_id==1)
        redirect(mapping:"mainpage"+hsRes.context?.lang,params:params-[where:params.where,country:params.country,hometype_id:params.hometype_id,type_url:params.type_url,homeclass_id:params.homeclass_id],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      else if(hsRes.inrequest.is_vip && !hsRes.inrequest.is_fiesta && !hsRes.inrequest.is_renthour && Shome.findByCity_idAndType_idAndModstatus(oCity.id,1,1))
        redirect(mapping:"is_vip"+hsRes.context?.lang,params:params-[where:params.where,country:params.country,hometype_id:params.hometype_id,type_url:params.type_url,is_vip:'1',homeclass_id:params.homeclass_id],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      else if(!hsRes.inrequest.is_vip && hsRes.inrequest.is_fiesta && !hsRes.inrequest.is_renthour && Shome.findByCity_idAndType_idAndModstatus(oCity.id,2,1))
        redirect(mapping:"is_fiesta"+hsRes.context?.lang,params:params-[where:params.where,country:params.country,hometype_id:params.hometype_id,type_url:params.type_url,is_fiesta:'1',homeclass_id:params.homeclass_id],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      else if(!hsRes.inrequest.is_vip && !hsRes.inrequest.is_fiesta && hsRes.inrequest.is_renthour && Shome.findByCity_idAndType_idAndModstatus(oCity.id,3,1))
        redirect(mapping:"is_renthour"+hsRes.context?.lang,params:params-[where:params.where,country:params.country,hometype_id:params.hometype_id,type_url:params.type_url,is_renthour:'1',homeclass_id:params.homeclass_id],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      else
        redirect(mapping:"hSearchTypeDomain"+hsRes.context?.lang,params:params-[where:params.where,country:params.country,hometype_id:params.hometype_id,type_url:params.type_url,homeclass_id:params.homeclass_id]+[type_url:'all'],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      return
    } else if(City.findByDomain(request.serverName)){
      if (hsRes.inrequest.hometype_id==1&&params.type_url=='flats'&&!hsRes.inrequest.bedroom)
        redirect(mapping:"mainpage"+hsRes.context?.lang,params:params-[where:params.where,country:params.country,hometype_id:params.hometype_id,type_url:params.type_url,homeclass_id:params.homeclass_id],base:'http://'+request.serverName+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
      if (params.country) {
        response.sendError(404)
        return
      }
      hsRes.inrequest.where = City.findByDomain(request.serverName)['name'+hsRes.context?.lang]
    } else if (!City.findByDomain(request.serverName)&&(Country.findByUrlname(params.country?:'')?.id?:0)!=(oCity?.country_id?:0)) {
      redirect(action:'s',params:params-[hometype_id:params.hometype_id,homeclass_id:params.homeclass_id]+[hometype_id:hsRes.inrequest.hometype_id?:0],base:hsRes.context?.mainserverURL_lang,permanent:true)
      return
    }

    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    def oValuta=Valuta.get(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = oValuta.symbol
    hsRes.valuta = Valuta.get(hsRes.context.shown_valuta.id)    

    def oHomeSearch=new HomeSearch()
    hsRes+=searchService._getFilter(requestService)

    //min, max values after change valuta>>     
    if(session.changeValuta){
      hsRes.inrequest.price_min=oValuta.min
      hsRes.inrequest.price_max=oValuta.max
      hsRes.hsFilter.price_min=oValuta.min
      hsRes.hsFilter.price_max=oValuta.max
      session.changeValuta=null
    }     
    //<<
    if((((hsRes.hsFilter?.price_min?:0)>0 && (hsRes.hsFilter?.price_min?:0)<oValuta.min) || hsRes.hsFilter.price_min>oValuta.max) || (((hsRes.hsFilter.price_max?:0)>0 && (hsRes.hsFilter.price_max?:0)<oValuta.min) || hsRes.hsFilter.price_max>oValuta.max)){
      hsRes.inrequest.price_min=oValuta.min
      hsRes.hsFilter.price_min=oValuta.min   
      hsRes.hsFilter.price_max=oValuta.max
      hsRes.inrequest.price_max=oValuta.max       
    }    
    if((hsRes.hsFilter?.price_min?:0)==0){
      hsRes.hsFilter?.price_min=oValuta.min
      hsRes.inrequest.price_min=oValuta.min
    }  
    if((hsRes.hsFilter?.price_max?:0)==0){
      hsRes.hsFilter?.price_max=oValuta.max
      hsRes.inrequest.price_max=oValuta.max      
    }      
    
    //>>change price into rub
    hsRes.hsFilter.price_min=Math.round(hsRes.hsFilter.price_min * hsRes.valutaRates)
    if(hsRes.hsFilter.price_max==(oValuta.max?:5000)){
      hsRes.hsFilter.price_max=Valuta.findWhere(regorder:1).max?:5000
    }else{
      hsRes.hsFilter.price_max=Math.round(hsRes.hsFilter.price_max * hsRes.valutaRates)
    }
    
    hsRes+=searchService._getMapFilter(hsRes.hsFilter,Math.round(coordinates[0]).toLong(),Math.round(coordinates[1]).toLong(),Math.round(coordinates[2]).toLong(),Math.round(coordinates[3]).toLong())
    hsRes.inrequest.max=Tools.getIntVal(grailsApplication.config.search.listing.max,30)

    flash.error=[]
    if(hsRes.inrequest.price_min>hsRes.inrequest.price_max){
      flash.error<<3
    }
    if(hsRes.inrequest.date_start){
      def dateStart=new Date()
      def date1= new GregorianCalendar()
      date1.setTime(dateStart)
      date1.set(Calendar.HOUR_OF_DAY ,0)
      date1.set(Calendar.MINUTE ,0)
      date1.set(Calendar.SECOND,0)
      date1.set(Calendar.MILLISECOND,0)

      if(hsRes.inrequest.date_start<date1.getTime()){
        flash.error<<1
      }
      if(hsRes.inrequest.date_end && (hsRes.inrequest.date_start>=hsRes.inrequest.date_end)){
        flash.error<<2
      }
    }
    if(!flash.error){
      hsRes+=oHomeSearch.csiFindByWhere(hsRes.inrequest.where,hsRes.inrequest.max,requestService.getOffset(),searchService._getMainFilter(hsRes.inrequest,requestService),hsRes.hsFilter,true,true,false,true,true,hsRes.context?.lang)	
      if(hsRes.context?.lang){      
        for(oHome in hsRes.records){
          oHome.name=Tools.transliterate(oHome.name,0)
          oHome.shortaddress=Tools.transliterate(oHome.shortaddress,0)
          oHome.city=City.findByName(oHome.city)?.name_en?:Tools.transliterate(oHome.city,0)                        
        }        
      }
      if(hsRes.count==0 && !hsRes.context?.lang){	
        def str_en="qwertyuiop[]asdfghjkl;'zxcvbnm,."
        def str_ru="йцукенгшщзхъфывапролджэячсмитьбю"
        def letter=''	   
        def sWhere=''
        String inrequestWhere=new String(hsRes.inrequest.where1)	   
        for(def i=0; i<hsRes.inrequest.where1.size();i++){
          letter=hsRes.inrequest.where1.getAt(i)		 
          for(def j=0; j<str_en.size();j++){
            if(letter==str_en.getAt(j))
              sWhere+=str_ru.getAt(j)
          }
        }
	   
        //def sWhat=hsRes.inrequest.what.tr("qwertyuiop[]asdfghjkl;'zxcvbnm,.","йцукенгшщзхъфывапролджэячсмитьбю")
        if(sWhere!='' && sWhere!=hsRes.inrequest.where1){
          hsRes.inrequest.wrongwhere=hsRes.inrequest.where
          hsRes.inrequest.where=sWhere
          if(hsRes.inrequest?.date_start){
            def date_start=hsRes.inrequest.date_start.toCalendar() 
            hsRes.inrequest.date_start_day=date_start.get(Calendar.DATE) 
            hsRes.inrequest.date_start_month=date_start.get(Calendar.MONTH)+1
            hsRes.inrequest.date_start_year=date_start.get(Calendar.YEAR)			
          }
          if(hsRes.inrequest?.date_end){	              
            def date_end=hsRes.inrequest.date_end.toCalendar()
            hsRes.inrequest.date_end_day=date_end.get(Calendar.DATE) 
            hsRes.inrequest.date_end_month=date_end.get(Calendar.MONTH)+1
            hsRes.inrequest.date_end_year=date_end.get(Calendar.YEAR)	  
          }
          if (!hsRes.inrequest.metro_id) {
            hsRes.inrequest -= [metro_id:null]
          }
          redirect(action:'search',params:hsRes.inrequest,base:hsRes.context?.mainserverURL_lang)
          return		 
        }	   
        if((hsRes?.inrequest?.wrongwhere?:'').size()>0)
          hsRes.inrequest.where=hsRes.inrequest.wrongwhere
        hsRes.inrequest.wrongwhere=''
      }     
    } else {
      hsRes.option=[:]
      hsRes.records=[]
      hsRes.count=0
    }
    if ((hsRes.hometypeLinksData?.max{it?.value}?.value?:0)==0&&hsRes.inrequest.where&&!City.findByName(hsRes.inrequest.where)&&!City.findByName_en(hsRes.inrequest.where)) {
      response.sendError(404)
      return
    }
    hsRes.modstatus=Homemodstatus.list()
    hsRes.hometypeFilter=[]
    hsRes.hometype=Hometype.list([sort:'regorder'])
    if(!hsRes.inrequest.hometype_id) {
      if (hsRes.hometypeMax){
        def oHometype=new Hometype()
        def homeType_ids=[]
        for(oHometypeMax in hsRes.hometypeMax)
          homeType_ids<<oHometypeMax.id
        hsRes.hometypeFilter = oHometype.getByIds(homeType_ids)
      }
    }
    hsRes+=searchService._getFilterDictionary(null,hsRes.districtMax,hsRes.metroMax,hsRes.citysightMax)

    hsRes.homeperson=Homeperson.list()

    hsRes.homeoption=Homeoption.findAllByFacilitygroup_idAndModstatus(1,1,[sort:'id'])
    hsRes.homeoption_apartament=Homeoption.findAllByFacilitygroup_idAndModstatus(12,1,[sort:'id'])
    hsRes.country=Country.list([sort:'regorder'])
  
    if (hsRes.alike){//owner Dmitry
      if(!hsRes.inrequest.sort)
        hsRes.records = oHomeSearch.sortByDistance(hsRes.records, hsRes.alike)
      hsRes.alikeDistances = oHomeSearch.calcDistance(hsRes.records, hsRes.alike)
    }
    if(hsRes.inrequest?.country_id?:0)
      hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
    else if((hsRes.country?:[]).size()>0)
  	  hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])	  
	  
    hsRes.urlphoto = grailsApplication.config.urlphoto    

    def hc_ids=[] //owner Marina
    for(homeclass_id in hsRes.inrequest?.homeclass_id)
	    hc_ids<<homeclass_id.toString()
    hsRes.inrequest?.homeclass_id=hc_ids
    
    hsRes.homeroom=Homeroom.list([sort:'id'])
    hsRes.homebath=Homebath.list([sort:'id'])        

    hsRes.urlphoto = grailsApplication.config.urlphoto
    hsRes.listBannerLine = Tools.getIntVal(grailsApplication.config.search.bannerLineNumber.list,5)
    hsRes.photoBannerLine = Tools.getIntVal(grailsApplication.config.search.bannerLineNumber.photo,3)
    hsRes.linkparams = { map, linkname ->
      hsRes.string?.date_start?map+=[date_start:hsRes.string?.date_start]:'';
      hsRes.string?.date_end?map+=[date_end:hsRes.string?.date_end]:'';      
      (hsRes.inrequest?.homeperson_id?:1)!=1?map+=[homeperson_id:hsRes.inrequest?.homeperson_id]:'';      
      if (hsRes.string?.date_start||hsRes.string?.date_end||(hsRes.inrequest?.homeperson_id?:1)!=1)
        map+=[from_search:1]
      map;
    }    

    hsRes.homebed=[]	
    def i
    for(i=1;i<16;i++)
      hsRes.homebed<<[id:i,name:i.toString()]
    hsRes.homebed<<[id:16,name:'16+']	
  
    hsRes.arr=[];
    i=0
    for(i=(hsRes?.valuta?.min?:100);i<=(hsRes?.valuta?.max?:5000);i=i+(hsRes?.valuta?.step?:100))
      hsRes.arr<<i;	       

    hsRes.searchFilterMax=(Tools.getIntVal(grailsApplication.config.search.filter_option.max,5)>=(hsRes.option_filter?:[]).size())?(hsRes.option_filter?:[]).size():Tools.getIntVal(grailsApplication.config.search.filter_option.max,5)
    hsRes.searchFilterDistrictMax=(Tools.getIntVal(grailsApplication.config.search.filter_option_district.max,3)>=(hsRes.district?:[]).size())?(hsRes.district?:[]).size():Tools.getIntVal(grailsApplication.config.search.filter_option_district.max,3)
    hsRes.searchFilterMetroMax=(Tools.getIntVal(grailsApplication.config.search.filter_option_metro.max,3)>=(hsRes.metro?:[]).size())?(hsRes.metro?:[]).size():Tools.getIntVal(grailsApplication.config.search.filter_option_metro.max,3)
    hsRes.searchFilterCitysightMax=(Tools.getIntVal(grailsApplication.config.search.filter_option_citysight.max,3)>=(hsRes.citysightMax?:[]).size())?(hsRes.citysightMax?:[]).size():Tools.getIntVal(grailsApplication.config.search.filter_option_citysight.max,3)
    hsRes.homeMetroMinCount = Tools.getIntVal(grailsApplication.config.search.metroDisplayCountHome.min,5)
    
    hsRes.districtMax=(hsRes.districtMax?:[]).sort{it.total}.reverse()
    
    hsRes.metroMax=(hsRes.metroMax?:[]).sort{it.total}.reverse()        
    if ((hsRes.inrequest.metro_id?:[]).size()==1) {
      def circlesort = {list -> list.sort{it.id}; list.drop(list.indexOf(list.find{it.id==hsRes.inrequest.metro_id[0]}))+list.take(list.indexOf(list.find{it.id==hsRes.inrequest.metro_id[0]})) }
      hsRes.metroNext = circlesort(hsRes.metroMax.clone())
    } else {
      hsRes.metroNext = hsRes.metroMax
    }
    hsRes.citysightMax=(hsRes?.citysightMax?:[]).sort{it.total}.reverse()
    if ((hsRes.inrequest.citysight_id?:[]).size()==1) {
      def circlesort = {list -> list.sort{it.id}; list.drop(list.indexOf(list.find{it.id==hsRes.inrequest.citysight_id[0]}))+list.take(list.indexOf(list.find{it.id==hsRes.inrequest.citysight_id[0]})) }
      hsRes.citysightNext = circlesort(hsRes.citysightMax.clone())
    } else {
      hsRes.citysightNext = hsRes.citysightMax
    }
    
    i=0		
    for(item in hsRes.option_filter?.keySet()){
      if(i<hsRes.searchFilterMax)
        if(hsRes.hidInrequest.find{it.key == item})
          hsRes.hidInrequest.remove(item)  
	    i++
    }
    if(!hsRes.inrequest?.citysight_id){
      i=0
      for(item in hsRes.districtMax){
        if(i<hsRes.searchFilterDistrictMax)
          if(hsRes.hidInrequest_district_id.contains(item.district_id.toLong())){
            hsRes.hidInrequest_district_id=hsRes.hidInrequest_district_id.minus(item.district_id.toLong())
          }
  	    i++
      }
      if((hsRes.inrequest?.metro_id?:[]).size()!=1){
        i=0
        for(item in hsRes.metroMax){
          if(i<hsRes.searchFilterMetroMax)
            if(hsRes.hidInrequest_metro_id.contains(item.id.toLong())){
              hsRes.hidInrequest_metro_id=hsRes.hidInrequest_metro_id.minus(item.id.toLong())
            }
          i++
        }
      }
    }

    hsRes.infotags=[:]
    hsRes.breadcrumbs=[:]
    
    hsRes.breadcrumbs.city = (hsRes.context?.lang)?City.findByName_en(hsRes.inrequest.where):City.findByName(hsRes.inrequest.where)
    hsRes.breadcrumbs.region = hsRes.breadcrumbs.city?Region.get(hsRes.breadcrumbs.city?.region_id?:0):((hsRes.context?.lang)?Region.findByName_en(hsRes.inrequest.where):Region.findByName(hsRes.inrequest.where))
    hsRes.breadcrumbs.regionalCity = City.findAll("from City where region_id=:r_id and id!=:id and homecount>0",[r_id:hsRes.breadcrumbs.city?.region_id?:0,id:hsRes.breadcrumbs.city?.id?:0])
    hsRes.breadcrumbs.direction = Popdirection.get(hsRes.breadcrumbs.region?.popdirection_id?:0)
    hsRes.breadcrumbs.country = Country.get(hsRes.breadcrumbs.region?.country_id?:0)
    hsRes.breadcrumbs.citysight =  [:]
    hsRes.breadcrumbs.base = hsRes.breadcrumbs.city?.domain?'http://'+hsRes.breadcrumbs.city.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL

    if (hsRes.inrequest.hometype_id){
      hsRes.breadcrumbs.hometype = Hometype.get(hsRes.inrequest.hometype_id)
      if ((hsRes.inrequest?.metro_id?:[]).size()==1)
        hsRes.breadcrumbs.metro = Metro.get(hsRes.inrequest.metro_id[0])
      else if ((hsRes.inrequest?.citysight_id?:[]).size()==1)
        hsRes.breadcrumbs.citysight = Citysight.get(hsRes.inrequest.citysight_id[0])
      else if (hsRes.inrequest.bedroom)
        hsRes.breadcrumbs.typebedroom = Homeroom.get(hsRes.inrequest.bedroom)
    } else if ((hsRes.inrequest?.metro_id?:[]).size()==1)
      hsRes.breadcrumbs.metro = Metro.get(hsRes.inrequest.metro_id[0])
    else if ((hsRes.inrequest?.citysight_id?:[]).size()==1)
      hsRes.breadcrumbs.citysight = Citysight.get(hsRes.inrequest.citysight_id[0])
    else if (hsRes.inrequest.bedroom)
      hsRes.breadcrumbs.bedroom = Homeroom.get(hsRes.inrequest.bedroom)
    else if (hsRes.inrequest.shome){
      if(hsRes.inrequest.is_vip)
        hsRes.breadcrumbs.shome = Shometype.findWhere(fieldname:'is_vip') 
      else if(hsRes.inrequest.is_fiesta)
        hsRes.breadcrumbs.shome = Shometype.findWhere(fieldname:'is_fiesta')
      else if(hsRes.inrequest.is_renthour)
        hsRes.breadcrumbs.shome = Shometype.findWhere(fieldname:'is_renthour')        
    }  
      
    if (hsRes.breadcrumbs.direction) {
      hsRes.breadcrumbs.directionCities = Region.executeQuery("""select c.name, c.homecount, c.is_index, c.domain, c.name_en
          from Region r, City c
          where c.region_id=r.id and r.popdirection_id=:p_id and c.homecount>0 and c.id!=:c_id
          order by c.name""",[p_id:hsRes.breadcrumbs.direction.id,c_id:hsRes.breadcrumbs.city?.id?:0]).inject([:]){map,tag -> map[hsRes.context?.lang?tag[4]:tag[0]]=[count:tag[1],is_index:tag[2],domain:tag[3]];map}
    }

    hsRes.infotags.isfound = hsRes.breadcrumbs.city?true:false
    hsRes.infotags.vcity = hsRes.context?.lang?(hsRes.breadcrumbs.city?(hsRes.breadcrumbs.city['name'+hsRes.context?.lang]?:hsRes.inrequest.where):hsRes.inrequest.where):(hsRes.breadcrumbs.city?.name2?:hsRes.inrequest.where)
    hsRes.infotags.city = hsRes.breadcrumbs.city?(hsRes.breadcrumbs.city['name'+hsRes.context?.lang]?:hsRes.inrequest.where):hsRes.inrequest.where
    hsRes.infotags.hometype = Hometype.get(hsRes.inrequest.hometype_id?:0)?(Hometype.get(hsRes.inrequest.hometype_id?:0)['name'+(hsRes.context?.lang?'_en':'2')]?:message(code:'server.housing')):message(code:'server.housing')
    hsRes.infotags.hometyper = Hometype.get(hsRes.inrequest.hometype_id?:0)?(Hometype.get(hsRes.inrequest.hometype_id?:0)['name'+(hsRes.context?.lang?'_en':'5')]?:message(code:'server.housings')):message(code:'server.housings')
    hsRes.infotags.hometypes = Hometype.get(hsRes.inrequest.hometype_id?:0)?(Hometype.get(hsRes.inrequest.hometype_id?:0)['name3'+hsRes.context?.lang]?:message(code:'server.housings')):message(code:'server.housing')
    hsRes.infotags.hometypess = Hometype.get(hsRes.inrequest.hometype_id?:0)?(Hometype.get(hsRes.inrequest.hometype_id?:0)['name'+(hsRes.context?.lang?'3_en':'4')]?:message(code:'server.housings')):message(code:'server.housings')    
    hsRes.infotags.homeroom = Homeroom.get(hsRes.inrequest.bedroom?:0)?(Homeroom.get(hsRes.inrequest.bedroom?:0)['name'+(hsRes.context?.lang?'_en':'2')]?:''):''    
    hsRes.infotags.homerooms = Homeroom.get(hsRes.inrequest.bedroom?:0)?(Homeroom.get(hsRes.inrequest.bedroom?:0)['name3'+hsRes.context?.lang]?:''):''
    hsRes.infotags.homeroomss = Homeroom.get(hsRes.inrequest.bedroom?:0)?(Homeroom.get(hsRes.inrequest.bedroom?:0)['name'+(hsRes.context?.lang?'3_en':'4')]?:''):''     
    hsRes.infotags.homeroom2 = Homeroom.get(hsRes.inrequest.bedroom?:0)?(Homeroom.get(hsRes.inrequest.bedroom?:0)['name'+(hsRes.context?.lang?'3_en':'5')]?:''):''
    hsRes.infotags.homeroom6 = Homeroom.get(hsRes.inrequest.bedroom?:0)?(Homeroom.get(hsRes.inrequest.bedroom?:0)['name'+(hsRes.context?.lang?'3_en':'6')]?:''):''

    if (request.getHeader("User-Agent")?.contains('Mobile')&&!request.getHeader("User-Agent")?.contains('iPad')) {
      if (hsRes.infotags.isfound) {
        redirect(mapping:(hsRes.inrequest?.metro_id?:[]).size()==1?('hSearchMetro'+hsRes.context?.lang):hsRes.inrequest?.bedroom?('hSearchRoom'+hsRes.context?.lang):(hsRes.inrequest?.citysight_id?:[]).size()==1?('hSearchCitysight'+hsRes.context?.lang):hsRes.inrequest?.hometype_id!=1?('hSearchType'+hsRes.context?.lang):('hSearch'+hsRes.context?.lang),params:[where:hsRes.breadcrumbs.city?.('name'+hsRes.context?.lang)?:'',country:hsRes.breadcrumbs?.country?.urlname?:'']+((hsRes.inrequest?.bedroom && !((hsRes.inrequest?.metro_id?:[]).size()==1))?[bedroom:hsRes.inrequest?.bedroom,type_url:'flats']:[:])+((hsRes.inrequest?.bedroom && (hsRes.inrequest?.metro_id?:[]).size()==1)?[bedroom:hsRes.inrequest?.bedroom]:[:])+((hsRes.inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(hsRes.inrequest?.metro_id[0])?.urlname]:[:])+((hsRes.inrequest?.citysight_id?:[]).size()==1&&!hsRes.inrequest?.bedroom?[citysight_url:Citysight.get(hsRes.inrequest?.citysight_id[0])?.urlname]:[:])+((hsRes.inrequest?.citysight_id?:[]).size()==1&&hsRes.inrequest?.bedroom?[citysight_id:hsRes.inrequest?.citysight_id[0]]:[:])+((hsRes.inrequest?.district_id?:[]).size()==1?[district_id:hsRes.inrequest?.district_id[0]]:[:])+(hsRes.inrequest?.hometype_id!=1 && !((hsRes.inrequest?.metro_id?:[]).size()==1 || (hsRes.inrequest?.citysight_id?:[]).size()==1)?[type_url:hsRes.breadcrumbs.hometype?.urlname?:'all']:[:])+(hsRes.inrequest?.hometype_id && ((hsRes.inrequest?.metro_id?:[]).size()==1 || ((hsRes.inrequest?.citysight_id?:[]).size()==1)&&!hsRes.inrequest?.bedroom)?[hometype_id:hsRes.inrequest.hometype_id]:[:])+(hsRes.inrequest?.longdiscount?[longdiscount:hsRes.inrequest?.longdiscount]:[:])+(hsRes.inrequest?.hotdiscount?[hotdiscount:hsRes.inrequest?.hotdiscount]:[:]),base:hsRes.context?.mobileURL,permanent:true)
        return
      } else if (hsRes.inrequest?.where) {
        redirect(controller:'home',action:'list',params:[where:hsRes.inrequest?.where?:'']+(hsRes.inrequest?.bedroom?[bedroom:hsRes.inrequest?.bedroom,type_url:'flats']:[:])+((hsRes.inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(hsRes.inrequest?.metro_id[0])?.urlname]:[:])+((hsRes.inrequest?.citysight_id?:[]).size()==1?[citysight_url:Citysight.get(hsRes.inrequest?.citysight_id[0])?.urlname]:[:])+((hsRes.inrequest?.district_id?:[]).size()==1?[district_id:hsRes.inrequest?.district_id[0]]:[:])+(hsRes.inrequest?.longdiscount?[longdiscount:hsRes.inrequest?.longdiscount]:[:])+(hsRes.inrequest?.hotdiscount?[hotdiscount:hsRes.inrequest?.hotdiscount]:[:]),base:hsRes.context?.mobileURL_lang,permanent:true)
        return
      }
    }

    hsRes.sight_linkparams = { map, linkname ->
      hsRes.inrequest?.hometype_id?map+=[hometype_id:hsRes.inrequest?.hometype_id]:'';
      hsRes.inrequest?.longdiscount?map+=[longdiscount:1]:'';
      hsRes.inrequest?.hotdiscount?map+=[hotdiscount:1]:'';
      hsRes.hclass_id?map+=[homeclass_id:hsRes.hclass_id]:'';      
      hsRes.string?.date_start?map+=[date_start:hsRes.string?.date_start]:'';
      hsRes.string?.date_end?map+=[date_end:hsRes.string?.date_end]:'';
      (hsRes.inrequest?.homeperson_id?:1)!=1?map+=[homeperson_id:hsRes.inrequest?.homeperson_id]:'';
      map;
    }
    if (hsRes.breadcrumbs.city && !hsRes.context?.lang) {
      def oArticles = new Articles()
      hsRes.relatedblog = oArticles.getTimeline('all',0,0,0,[hsRes.breadcrumbs.city.tagname],1,0).records
    }
    if(hsRes.inrequest.where==''){
      def tagList = Home.executeQuery("""select c.name,count(h.id),c.is_index,c.domain,c.name_en,c.name2
            from Home h, City c
            where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
            group by h.city_id
            having count(h.id) > :minCount
            order by c.name""",["minCount":(Tools.getIntVal(grailsApplication.config.index.cityTagCloud.minCityCount,5) as long)])
      hsRes.tagcloud = tagList.inject([:]){map,tag -> map[hsRes.context?.lang?tag[4]:tag[0]]=[count:tag[1],is_index:tag[2],domain:tag[3],name2:hsRes.context?.lang?tag[4]:tag[5]];map}
    }
    hsRes.tagcloudParams = [:]
    hsRes.tagcloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.max,50)
    hsRes.tagcloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.middle,20)
       
    def lsRecIds = []
    hsRes.records.each{lsRecIds<<it.hid}
    if((requestService.getStr('param')?:'0')!='0')
      requestService.setStatistic('search',7,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    else if(hsRes.inrequest.view=='photo')//owner Dmitry
      requestService.setStatistic('search',5,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    else if (hsRes.inrequest.view=='map')
      requestService.setStatistic('search',6,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    else if(hsRes.inrequest.sort)
      requestService.setStatistic('search',4,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    else
      requestService.setStatistic('search',0,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    if (hsRes.inrequest.metro_id)
      requestService.setStatistic('listingfilter',32,0,hsRes.count, hsRes.inrequest.where,hsRes.inrequest.metro_id[0],lsRecIds)
    else if (hsRes.inrequest.bedroom)
      requestService.setStatistic('listingfilter',31,0,hsRes.count, hsRes.inrequest.where,hsRes.inrequest.bedroom,lsRecIds)
    else if (hsRes.inrequest.citysight_id)
      requestService.setStatistic('listingfilter',66,0,hsRes.count, hsRes.inrequest.where,hsRes.inrequest.citysight_id[0],lsRecIds)
    else if (hsRes.inrequest.hometype_id)
      requestService.setStatistic('listingfilter',65,0,hsRes.count, hsRes.inrequest.where,hsRes.inrequest.hometype_id,lsRecIds)
    else
      requestService.setStatistic('listingfilter',67,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    if(requestService.getOffset())
      requestService.setStatistic('listingfilter',33)
    if(hsRes.inrequest.is_fiesta)
      requestService.setStatistic('listingfilter',34)

    //title, keywords, description, h1  
    if(hsRes.inrequest?.shome && City.findByDomain(request.serverName)){
      def iType_id      
      
      if(hsRes.inrequest?.is_vip)
        iType_id=Shometype.findWhere(fieldname:'is_vip')?.id?:0
      else if(hsRes.inrequest?.is_fiesta)
        iType_id=Shometype.findWhere(fieldname:'is_fiesta')?.id?:0 
      else if(hsRes.inrequest?.is_renthour)
        iType_id=Shometype.findWhere(fieldname:'is_renthour')?.id?:0                                
      
      hsRes.shome=Shome.findWhere(city_id:City.findByDomain(request.serverName)?.id?:0,type_id:iType_id,modstatus:1)      
    }

    hsRes.lsShome=Shome.findAll("FROM Shome WHERE city_id=:city_id AND modstatus=1",[city_id:City.findByDomain(request.serverName)?.id?:0])//AND homecount>0
    hsRes.homecount=Tools.getIntVal(grailsApplication.config.home.list.apartment_type.homecount.min,1)
    
    if(hsRes.context?.lang)
      homeService.generateInfotagsLang(hsRes)
    else
      homeService.generateInfotags(hsRes)        
    
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def search_table = { 
    requestService.init(this) 	
    def hsRes=requestService.getContextAndDictionary(false,true,true)	
    def coordinates=searchService.findViewPortTile(requestService.getStr('param'))

    hsRes+=requestService.getParams(['hometype_id','homeperson_id','term','price',//'country_id','region_id',
      'valuta_id','pindex','usage','is_nosmoking','is_parking','is_visa','is_tv','is_internet','is_wifi','is_cond',
      'is_heat','is_kitchen','is_holod','is_microwave','is_wash','is_breakfast','is_swim','is_steam','is_gym',
      'is_hall','is_family','is_pets','is_invalid','is_area','is_beach','nref','rating','hotdiscount','longdiscount',
      'sort','bathroom','bed','bedroom','date_start_day','date_start_month','date_start_year','date_end_day',
      'date_end_month','date_end_year','is_fiesta','offset',
      'is_coffee','is_fen','is_hometheater','is_iron','is_jacuzzi','is_kettle','is_vip','is_renthour','shome'],
      ['price_min','price_max'],
      ['title','description','city','district','street','email','homenumber','keywords'],
      ['date_start','date_end'])
	  
    hsRes.inrequest.view=requestService.getStr('view')
    hsRes.inrequest.is_main_filter=requestService.getLongDef("is_main_filter",0)    
    hsRes.alike=requestService.getIntDef('alike',0)
    hsRes.inrequest.metro_id=requestService.getIds('metro_id')
    hsRes.inrequest.citysight_id=requestService.getIds('citysight_id')

    if(hsRes.inrequest?.date_start?:'')
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
    if(hsRes.inrequest?.date_end?:'')
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)	  
    hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
    hsRes.inrequest.where=requestService.getPreparedStr('where')    
    hsRes.homeperson=Homeperson.list()
    hsRes.homeoption=Homeoption.findAllByFacilitygroup_idAndModstatus(1,1,[sort:'id'])
    
    def oHomeSearch=new HomeSearch()
    hsRes+=searchService._getFilter(requestService)
        
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    def oValuta=Valuta.get(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = oValuta.symbol
    //>>change price into rub
    hsRes.hsFilter.price_min=Math.round(hsRes.hsFilter.price_min * hsRes.valutaRates)
    if(hsRes.hsFilter.price_max==(oValuta.max?:5000)){
      hsRes.hsFilter.price_max=Valuta.findWhere(regorder:1).max?:5000
    }else{
      hsRes.hsFilter.price_max=Math.round(hsRes.hsFilter.price_max * hsRes.valutaRates)
    }
    //<<   
    
    hsRes+=searchService._getMapFilter(hsRes.hsFilter,Math.round(coordinates[0]).toLong(),Math.round(coordinates[1]).toLong(),Math.round(coordinates[2]).toLong(),Math.round(coordinates[3]).toLong())
    hsRes.inrequest.max=Tools.getIntVal(grailsApplication.config.search.listing.max,30)
    
    flash.error=[]
    if(hsRes.inrequest.price_min>hsRes.inrequest.price_max){
      flash.error<<3	  
    }
    if(hsRes.inrequest.date_start){
      def dateStart=new Date()
      def date1= new GregorianCalendar()
      date1.setTime(dateStart) 	  	  
      date1.set(Calendar.HOUR_OF_DAY ,0)
      date1.set(Calendar.MINUTE ,0)
      date1.set(Calendar.SECOND,0)
      date1.set(Calendar.MILLISECOND,0)		    	 
	  	 
      if(hsRes.inrequest.date_start<date1.getTime()){	   
        flash.error<<1
      }
      if(hsRes.inrequest.date_end && (hsRes.inrequest.date_start>=hsRes.inrequest.date_end)){	   
        flash.error<<2
      }	        
    }
	
    if(!flash.error){    
      hsRes+=oHomeSearch.csiFindByWhere(hsRes.inrequest.where,hsRes.inrequest.max,requestService.getOffset(),searchService._getMainFilter(hsRes.inrequest,requestService),hsRes.hsFilter,true,false,false,true,true,hsRes.context?.lang)      
      if(hsRes.context?.lang){       
        for(oHome in hsRes.records){
          oHome.name=Tools.transliterate(oHome.name,0)
          oHome.shortaddress=Tools.transliterate(oHome.shortaddress,0)
          oHome.city=City.findByName(oHome.city)?.name_en?:Tools.transliterate(oHome.city,0)                    
        }       
      }        
    }else{
      hsRes.option=[:]
      hsRes.homeclassMax=[]	  	  
      hsRes.records=[]      
      hsRes.count=0	   
    }
	  
    hsRes.modstatus=Homemodstatus.list()
    hsRes.hometype=Hometype.findAll('FROM Hometype ORDER BY id')    

    if (hsRes.alike){//owner Dmitry
      if(!hsRes.inrequest.sort)
        hsRes.records = oHomeSearch.sortByDistance(hsRes.records, hsRes.alike)
      hsRes.alikeDistances = oHomeSearch.calcDistance(hsRes.records, hsRes.alike)
      hsRes.inrequest.alike = hsRes.alike
    }
    if(!hsRes.inrequest.hometype_id) {
      if (hsRes.hometype_id)//grails bug :( fixed only in 2.2 version
        hsRes.hometypeFilter = Hometype.getAll(hsRes.hometype_id)
    }
    hsRes.urlphoto = grailsApplication.config.urlphoto           
    hsRes.listBannerLine = Tools.getIntVal(grailsApplication.config.search.bannerLineNumber.list,5)
    hsRes.photoBannerLine = Tools.getIntVal(grailsApplication.config.search.bannerLineNumber.photo,3)
    hsRes.linkparams = { map, linkname ->
      hsRes.string?.date_start?map+=[date_start:hsRes.string?.date_start]:'';
      hsRes.string?.date_end?map+=[date_end:hsRes.string?.date_end]:'';
      (hsRes.inrequest?.homeperson_id?:1)!=1?map+=[homeperson_id:hsRes.inrequest?.homeperson_id]:'';
      if (hsRes.string?.date_start||hsRes.string?.date_end||(hsRes.inrequest?.homeperson_id?:1)!=1)
        map+=[from_search:1]
      map;
    }

    def lsRecIds = []
    hsRes.records.each{lsRecIds<<it.hid}
    if((requestService.getStr('param')?:'0')!='0')
      requestService.setStatistic('search',7,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    else if(hsRes.inrequest.view=='photo')//owner Dmitry
      requestService.setStatistic('search',5,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    else if (hsRes.inrequest.view=='map')
      requestService.setStatistic('search',6,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    else if(hsRes.inrequest.sort)
      requestService.setStatistic('search',4,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    else
      requestService.setStatistic('search',0,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds)
    if (hsRes.inrequest.metro_id)
      requestService.setStatistic('listingfilter',32)
    else if (hsRes.inrequest.bedroom)
      requestService.setStatistic('listingfilter',31)
    else if (hsRes.inrequest.citysight_id)
      requestService.setStatistic('listingfilter',66)
    else if (hsRes.inrequest.hometype_id)
      requestService.setStatistic('listingfilter',65)
    else
      requestService.setStatistic('listingfilter',67)
    if (requestService.getOffset())
      requestService.setStatistic('listingfilter',33)
    if (hsRes.inrequest.is_fiesta)
      requestService.setStatistic('listingfilter',34)

    return hsRes
  }

  def selectregion={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def country_id=requestService.getIntDef('country_id',1)
    hsRes.data = Region.findAllByCountry_idAndModstatus(country_id?:1,1,[sort:'regorder',order:'desc'])
    hsRes.countries = Country.findAllByModstatus(1,[sort:'regorder',order:'asc'])
    return hsRes
  }
  
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

  def selectpopcities={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def country_id=requestService.getIntDef('country_id',1)
    hsRes.data = City.findAll('FROM City WHERE country_id=:country_id AND homecount>10 ORDER BY case when homecount>:homecountsort then homecount else 0 end desc, name asc',[country_id:country_id?:1,homecountsort:Tools.getIntVal(grailsApplication.config.popcities.homecountsort,70)])
    hsRes.countries = Country.findAllByIs_popcities(1)
    return hsRes
  }
  def cityselectstat = {
    requestService.init(this)
    requestService.setStatistic('cityselect')
    render ''
  }
  def mapstatistic = {
    requestService.init(this)
    def lId = requestService.getLongDef('id',0)
    requestService.setStatistic('homeview',35,lId)
    render ''
  }
  def infstatistic = {
    requestService.init(this)
    def lId = requestService.getLongDef('id',0)
    requestService.setStatistic('homeview',37,lId)
    render ''
  }

  def searchprint = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    def coordinates=searchService.findViewPortTile(requestService.getStr('param'))	

    hsRes.param=requestService.getStr('param')

    hsRes+=requestService.getParams(['hometype_id','homeclass_id','homeperson_id','country_id','region_id','term','price',
      'valuta_id','pindex','usage','is_nosmoking','is_parking','is_visa','is_tv','is_internet','is_wifi','is_cond',
      'is_heat','is_kitchen','is_holod','is_microwave','is_wash','is_breakfast','is_swim','is_steam','is_gym',
      'is_hall','is_family','is_pets','is_invalid','is_area','is_beach','nref','rating','sort','bathroom','bed','bedroom',
      'is_fiesta','is_coffee','is_fen','is_hometheater','is_iron','is_jacuzzi','is_kettle'],['price_min','price_max','x','y','zoom'],['title','description','city','district',
      'street','email','homenumber','keywords'],['date_start','date_end'])
    hsRes.inrequest.view=requestService.getStr('view')	  
    hsRes.inrequest.is_map=requestService.getLongDef('is_map',0)
    hsRes.inrequest.search_in_map=requestService.getLongDef('search_in_map',0)    
    hsRes.alike=requestService.getIntDef('alike',0)
    hsRes.inrequest.homeperson_id=hsRes.inrequest.homeperson_id?:1
    
    if(hsRes.inrequest?.date_start?:''){	
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
      def date_start=hsRes.inrequest.date_start.toCalendar() 
      hsRes.inrequest.date_start_day=date_start.get(Calendar.DATE) 
      hsRes.inrequest.date_start_month=date_start.get(Calendar.MONTH)+1
      hsRes.inrequest.date_start_year=date_start.get(Calendar.YEAR)  
    }
    if(hsRes.inrequest?.date_end?:''){
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
      def date_end=hsRes.inrequest.date_end.toCalendar()
      hsRes.inrequest.date_end_day=date_end.get(Calendar.DATE) 
      hsRes.inrequest.date_end_month=date_end.get(Calendar.MONTH)+1
      hsRes.inrequest.date_end_year=date_end.get(Calendar.YEAR)
    }		    

    hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
    hsRes.inrequest.where=requestService.getPreparedStr('where')
    hsRes.inrequest.where1=requestService.getPreparedStr('where').toLowerCase()
    hsRes.inrequest.wrongwhere=requestService.getStr('wrongwhere')
    if(requestService.getStr('homeclass_id'))
      hsRes.inrequest.homeclass_id=requestService.getIds('homeclass_id')

    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    def oValuta=Valuta.get(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = oValuta.symbol        

    def oHomeSearch=new HomeSearch()
    hsRes+=searchService._getFilter(requestService)        
    
    if((((hsRes.hsFilter?.price_min?:0)>0 && (hsRes.hsFilter?.price_min?:0)<oValuta.min) || hsRes.hsFilter.price_min>oValuta.max) || (((hsRes.hsFilter.price_max?:0)>0 && (hsRes.hsFilter.price_max?:0)<oValuta.min) || hsRes.hsFilter.price_max>oValuta.max)){
      hsRes.inrequest.price_min=oValuta.min
      hsRes.hsFilter.price_min=oValuta.min   
      hsRes.hsFilter.price_max=oValuta.max
      hsRes.inrequest.price_max=oValuta.max       
    }
    
    if((hsRes.hsFilter?.price_min?:0)==0){
      hsRes.hsFilter?.price_min=oValuta.min
      hsRes.inrequest.price_min=oValuta.min
    } 
      
    if((hsRes.hsFilter?.price_max?:0)==0){
      hsRes.hsFilter?.price_max=oValuta.max
      hsRes.inrequest.price_max=oValuta.max      
    }  
    
    
    //>>change price into rub
    hsRes.hsFilter.price_min=Math.round(hsRes.hsFilter.price_min * hsRes.valutaRates)
    if(hsRes.hsFilter.price_max==(oValuta.max?:5000)){
      hsRes.hsFilter.price_max=Valuta.findWhere(regorder:1).max?:5000
    }else{
      hsRes.hsFilter.price_max=Math.round(hsRes.hsFilter.price_max * hsRes.valutaRates)
    }         
    
    hsRes+=searchService._getMapFilter(hsRes.hsFilter,Math.round(coordinates[0]).toLong(),Math.round(coordinates[1]).toLong(),Math.round(coordinates[2]).toLong(),Math.round(coordinates[3]).toLong())
    hsRes.inrequest.max=Tools.getIntVal(grailsApplication.config.search.listing.max,30)
	
    def bDateFlag=true
    if(hsRes.inrequest.date_start){
      def dateStart=new Date()
      def date1= new GregorianCalendar()
      date1.setTime(dateStart) 	  	  
      date1.set(Calendar.HOUR_OF_DAY ,0)
      date1.set(Calendar.MINUTE ,0)
      date1.set(Calendar.SECOND,0)
      date1.set(Calendar.MILLISECOND,0)		
    	 
      if((hsRes.inrequest.date_start<date1.getTime()) || (hsRes.inrequest.date_end
        && (hsRes.inrequest.date_start>=hsRes.inrequest.date_end))){
        hsRes.option=[:]
        hsRes.homeclass_id=[]	  	  
        hsRes.records=[]    
        
        bDateFlag=false
      }
    }
    if(bDateFlag){
      hsRes+=oHomeSearch.csiFindByWhere(hsRes.inrequest.where,hsRes.inrequest.max,requestService.getOffset(),searchService._getMainFilter(hsRes.inrequest,requestService),hsRes.hsFilter,true,true,false,true,false,hsRes.context?.lang)
      
      if(hsRes.context?.lang){      
        for(oHome in hsRes.records){
          oHome.name=Tools.transliterate(oHome.name,0)
          oHome.shortaddress=Tools.transliterate(oHome.shortaddress,0)
          oHome.city=City.findByName(oHome.city)?.name_en?:Tools.transliterate(oHome.city,0)                        
        }        
      }      
      
      if(hsRes.count==0 && !hsRes.context?.lang){	
        def str_en="qwertyuiop[]asdfghjkl;'zxcvbnm,."
        def str_ru="йцукенгшщзхъфывапролджэячсмитьбю"
        def letter=''	   
        def sWhere=''
        String inrequestWhere=new String(hsRes.inrequest.where1)	   
        for(def i=0; i<hsRes.inrequest.where1.size();i++){
          letter=hsRes.inrequest.where1.getAt(i)		 
          for(def j=0; j<str_en.size();j++){
            if(letter==str_en.getAt(j))
              sWhere+=str_ru.getAt(j)
          }
        }
	   
        //def sWhat=hsRes.inrequest.what.tr("qwertyuiop[]asdfghjkl;'zxcvbnm,.","йцукенгшщзхъфывапролджэячсмитьбю")
        if(sWhere!='' && sWhere!=hsRes.inrequest.where1){
          hsRes.inrequest.wrongwhere=hsRes.inrequest.where
          hsRes.inrequest.where=sWhere	     	     
          redirect(action:'searchprint',params:hsRes.inrequest,base:hsRes.context?.mainserverURL_lang)
          return		 
        }	   
        if((hsRes?.inrequest?.wrongwhere?:'').size()>0)
          hsRes.inrequest.where=hsRes.inrequest.wrongwhere
        hsRes.inrequest.wrongwhere=''
      }
    }
    if (hsRes.count == 0) {
      response.sendError(404)
      return
    }
    hsRes.modstatus=Homemodstatus.list()
    hsRes.hometype=Hometype.findAll('FROM Hometype ORDER BY id')
    def oMetro = new Metro()
    hsRes.metro=oMetro.getByIds(hsRes.hsFilter.metro_id?:[0])
    hsRes.bedroom = Homeroom.get(hsRes.inrequest.bedroom)
    hsRes.homeperson=Homeperson.findAll('FROM Homeperson')

    if (hsRes.alike){//owner Dmitry
      if(!hsRes.inrequest.sort)
        hsRes.records = oHomeSearch.sortByDistance(hsRes.records, hsRes.alike)
      hsRes.alikeDistances = oHomeSearch.calcDistance(hsRes.records, hsRes.alike)
    }
	  
    hsRes.urlphoto = grailsApplication.config.urlphoto 
    
    hsRes.hsFilter.price_min=requestService.getLongDef('price_min',0)
    hsRes.hsFilter.price_max=requestService.getLongDef('price_max',0)    
    
    requestService.setStatistic('searchprint')
   
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////  
//owner Dmitry>>
  def view = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def lId=requestService.getLongDef('id',0)
    if(lId)
      hsRes.home = Home.get(lId)    

    if(hsRes.home){
      redirect(mapping:'hView',params:params+[country:Country.get(hsRes.home.country_id)?.urlname,city:hsRes.home.city,linkname:hsRes.home.linkname]-[id:lId.toString(),linkname:lId.toString()],base:hsRes.context?.mainserverURL_lang,permanent:true)
      return
    } else {
      response.sendError(404)
      return
    }
  }
  def detail = {    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.inrequest=[:]
    hsRes.inrequest.timezone=requestService.getIntDef('timezone',0)
    def lId=requestService.getLongDef('id',0)
    hsRes.from_search=requestService.getIntDef('from_search',0)

    hsRes.home = Home.read(lId)

    hsRes.imageurl = grailsApplication.config.urluserphoto
    hsRes.coordRange = Tools.getIntVal(grailsApplication.config.home.view.alike.coordinates.range,20000)
    hsRes.is_map=requestService.getLongDef('is_map',0)
    hsRes.inrequest.date_start=requestService.getStr('date_start')
    hsRes.inrequest.date_end=requestService.getStr('date_end')
    hsRes.inrequest.homeperson_id = requestService.getIntDef('homeperson_id',1)
    hsRes.inrequest.mtext = requestService.getStr('mtext')
    hsRes.textlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
    hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
    if(hsRes.inrequest?.date_start?:'')
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
    if(hsRes.inrequest?.date_end?:'')
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
    if(hsRes.home){   
      def oCity = City.findByName(hsRes.home.city)
      hsRes.homecity = City.findByName(hsRes.home.city)
      hsRes.ownerUsers = User.findAll('FROM User WHERE client_id=:client_id AND banned=0 ORDER BY modstatus, id',[client_id:hsRes.home.client_id])
      if(!hsRes.ownerUsers){
        response.sendError(404)
        return
      }
      if(hsRes.context?.lang){
        if(!oCity)
          oCity = City.findByName_en(hsRes.home.city)
        if(!hsRes.homecity)
          hsRes.homecity = City.findByName_en(hsRes.home.city)
        hsRes.homecity_ru = hsRes.home.city
      }

      if(hsRes.context?.lang){
        hsRes.home = hsRes.home.csiSetEnHome()
      }else if((hsRes.home.city?:'').matches('.*(?=.*[A-Za-z]).*')){
        def oHomeTmp=new Home()
        hsRes.home.properties.each { prop, val ->
          if(["metaClass","class"].find {it == prop}) return
          if(oHomeTmp.hasProperty(prop)) oHomeTmp[prop] = val
        }
        oHomeTmp.id=hsRes.home.id
        oHomeTmp.city=City.get(oHomeTmp.city_id)?.name?:oHomeTmp.city
        hsRes.home = oHomeTmp
      }

      def sCity=requestService.getStr('city')
      if(oCity && !oCity?.domain && ((!hsRes.context.lang && sCity.matches('.*(?=.*[A-Za-z]).*')) || (hsRes.context.lang && sCity.matches('.*(?=.*[А-я]).*')))){ //&& requestService.getStr('lang')) {
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 1L);
        response.setHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        redirect(mapping:"hView"+hsRes.context.lang,params:params-[city:params.city,id:lId.toString()]+[city:hsRes.home.city],permanent:true)
        return
      }

      if(request.getHeader("User-Agent")?.contains('Mobile')&&!request.getHeader("User-Agent")?.contains('iPad')){
        redirect(mapping:'hView'+hsRes.context?.lang,params:[country:Country.get(hsRes.home.country_id)?.urlname,city:hsRes.home.city,linkname:hsRes.home.linkname],base:hsRes.context?.mobileURL,permanent:true)
        return
      }
      if (oCity?.domain&&request.serverName!=oCity?.domain) {
        redirect(mapping:"hViewDomain"+hsRes.context.lang,params:params+[linkname:hsRes.home.linkname]-[country:params.country,id:lId.toString(),linkname:lId.toString(),lang:params.lang,city:params.city],base:'http://'+oCity.domain+(hsRes.context.is_dev?':8080/Arenda':''),permanent:true)
        return
      } else if(!City.findByDomain(request.serverName)&&hsRes.home.region_id==114&&(Country.findByUrlname(params.country)?.id?:0)==3){
        redirect(mapping:"hView"+hsRes.context.lang,params:params-[city:params.city,id:lId.toString(),country:params.country]+[city:hsRes.home.city,country:'russia'],permanent:true)
        return
      } else if(City.findByDomain(request.serverName)){
        if (params.country||City.findByDomain(request.serverName)['name'+hsRes.context?.lang]!=hsRes.home.city) {
          response.sendError(404)
          return
        }
      }
      if (hsRes.user?.client_id != hsRes.home.client_id && hsRes.home.modstatus !=1 && session?.admin?.id==null) {
        response.sendError(404)
        return
      }
      if (!City.findByDomain(request.serverName)&&((Country.findByUrlname(params.country)?.id?:0)!=hsRes.home.country_id)) {
        response.sendError(404)
        return
      }

      if (requestService.getStr('mode')==Tools.generateModeParam(hsRes.home.id,hsRes.home.client_id)) {
        hsRes.yandexMode = true
      }
      if (hsRes.user)
        hsRes.user = User.get(hsRes.user.id)
      hsRes.client = User.findWhere(client_id:hsRes.home?.client_id)

      if(hsRes.context?.lang){
        hsRes.client = hsRes.client.csiSetEnUser()
        if (hsRes.user)
          hsRes.user = hsRes.user.csiSetEnUser()
      }
      def oMboxSearch=new MboxSearch()
      hsRes.msg_count=Mbox.findAll("FROM Mbox WHERE user_id=:user_id AND home_id=:home_id AND modstatus in (:ids)",[user_id:hsRes.user?.id?:0l, home_id:hsRes.home?.id?:0l, ids:(Mboxmodstatus.findAllByIs_open(1)?.collect{it.id}?:[])])?.size()?:0
      if(hsRes.user?.id?:0){
        def lsMbox=Mbox.findAllWhere(user_id:hsRes.user?.id?:0,home_id:hsRes.home?.id?:0)
        for(oMbox in lsMbox)
	      if(Mboxmodstatus.findWhere(modstatus:oMbox.modstatus?:0)?.is_open?:0)
		    hsRes.mbox_id_is_open=oMbox.id
      }
      hsRes += homeService.getSomePrice(hsRes.home)
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      def hAS = new HomeAlikeSearch()
      hsRes.anotherHomes = hAS.csiFindAnotherHomes(hsRes.home.x,hsRes.home.y,hsRes.home.id,hsRes.home.client_id)

      if(hsRes.context?.lang){
        def lsAnotherHomes=[]
        for(aH in hsRes.anotherHomes){
          def aHTmp = new HomeAlikeSearch()
          aH.properties.each { prop, val ->
            if(["metaClass","class"].find {it == prop}) return
            if(aHTmp.hasProperty(prop)) aHTmp[prop] = val
          }
          aHTmp.id=aH.id
          aHTmp.name=Tools.transliterate(aHTmp.name,0)
          aHTmp.address=Tools.transliterate(aHTmp.address,0)
          aHTmp.city=City.get(aHTmp.city_id)?.name_en?:Tools.transliterate(aHTmp.city,0)

          lsAnotherHomes<<aHTmp
        }
        hsRes.anotherHomes=lsAnotherHomes
      }

      hsRes.anotherDistances = []
      hsRes.anotherPrices = []
      for (aH in hsRes.anotherHomes){
        hsRes.anotherDistances << Math.rint(100.0 * (aH.distance / 1000)) / 100.0
        hsRes.anotherPrices << Math.round(aH.price / hsRes.valutaRates)
      }

      hsRes.alikeHomes = hAS.csiFindHomes(hsRes.home.x,hsRes.home.y,hsRes.home.client_id,hsRes.price_day)

      if(hsRes.context?.lang){
        def lsAlikeHomes=[]
        for(aH in hsRes.alikeHomes){
          def aHTmp = new HomeAlikeSearch()
          aH.properties.each { prop, val ->
            if(["metaClass","class"].find {it == prop}) return
            if(aHTmp.hasProperty(prop)) aHTmp[prop] = val
          }
          aHTmp.id=aH.id
          aHTmp.name=Tools.transliterate(aHTmp.name,0)
          aHTmp.address=Tools.transliterate(aHTmp.address,0)
          aHTmp.city=City.get(aHTmp.city_id)?.name_en?:Tools.transliterate(aHTmp.city,0)

          lsAlikeHomes<<aHTmp
        }
        hsRes.alikeHomes=lsAlikeHomes
      }

      hsRes.alikeDistances = []
      hsRes.alikePrices = []
      for (aH in hsRes.alikeHomes){
        hsRes.alikeDistances << Math.rint(100.0 * (aH.distance / 1000)) / 100.0
        hsRes.alikePrices << Math.round(aH.price / hsRes.valutaRates)
      }
      hsRes.deposit = Math.round(hsRes.home.deposit_rub / hsRes.valutaRates)
	    hsRes.cleanup = Math.round(hsRes.home.cleanup_rub / hsRes.valutaRates)
	    hsRes.fee = Math.round(hsRes.home.fee_rub / hsRes.valutaRates)
      hsRes.alikeWhere = hsRes.home.csiGetWhere(hsRes.context?.lang)
      if(hsRes.context?.lang){
        def lsOwnerUsers=[]
        for(oUser in hsRes.ownerUsers){
          lsOwnerUsers<<oUser.csiSetEnUser()
        }
        hsRes.ownerUsers=lsOwnerUsers
      }
      hsRes.ownerClient = Client.get(hsRes.ownerUsers[0]?.client_id)
      hsRes.reserve = Reserve.get(hsRes.ownerClient?.reserve_id?:0)
      hsRes.payway=Payway.findAllByModstatusAndIs_invoice(1,0)
      hsRes.homeperson=Homeperson.list()

      hsRes.homeoption=Homeoption.findAllByFacilitygroup_idAndModstatus(1,1,[sort:'id'])

      hsRes.country=Country.list([sort:'regorder'])
      if(hsRes.inrequest?.country_id?:0){
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
      }else if((hsRes.country?:[]).size()>0)
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])
      hsRes.homephoto=Homephoto.findAll('FROM Homephoto WHERE home_id=:home_id ORDER BY norder',[home_id:lId])
      hsRes.homevideo=Homevideo.findAll('FROM Homevideo WHERE home_id=:home_id ORDER BY norder',[home_id:lId])
      hsRes.homemodstatus=Homemodstatus.findByModstatus(hsRes.home.modstatus)
      hsRes.cancellation=Rule_cancellation.list()
      hsRes.minday=Rule_minday.list()
      hsRes.maxday=Rule_maxday.list()
      hsRes.timein=Rule_timein.list()
      hsRes.timeout=Rule_timeout.list()
      hsRes.photourl = grailsApplication.config.urlphoto + hsRes.home.client_id.toString()+'/'
      hsRes.alikephotourl = grailsApplication.config.urlphoto
      hsRes.service=requestService.getLongDef('service',0)
      hsRes.gmt=Gmt.findAll('FROM Gmt ORDER BY id')
      hsRes.textlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
      hsRes.onlinetimediff = Tools.getIntVal(grailsApplication.config.onlinetimediff,5)*60*1000
      hsRes.notactiveUserParam = Tools.getIntVal(grailsApplication.config.notactiveUserParam,30)*24*60*60
      hsRes.discounts = hsRes.home.csiGetHomeDiscounts()
      hsRes.curdiscount = hsRes.home.csiGetDisplayDiscount(hsRes.inrequest?.date_start?:null,hsRes.inrequest?.date_end?:null)
      hsRes.responseStat = hsRes.ownerUsers[0]?.csiGetResponseStat()
      def oUcommentSearch = new UcommentSearch()
      hsRes.comments = oUcommentSearch.csiSelectUcommentsForHome(hsRes.home.id,3,0)

      hsRes.infras = []
      hsRes.i = 0

      if (hsRes.home['infraoption'+hsRes.context?.lang]){
        def aTemp = hsRes.home['infraoption'+hsRes.context?.lang].split(',')
        aTemp.each{ hsRes.infras << it.split(':') }
      }
      hsRes.breadcrumbs=[:]
      hsRes.breadcrumbs.country = Country.get(hsRes.home.country_id)
      hsRes.breadcrumbs.direction = Popdirection.get(Region.get(hsRes.home.region_id)?.popdirection_id)
      hsRes.breadcrumbs.hometype = Hometype.get(hsRes.home?.hometype_id)      
      hsRes.breadcrumbs.city = hsRes.homecity
      hsRes.breadcrumbs.base = hsRes.breadcrumbs.city?.domain?'http://'+hsRes.breadcrumbs.city.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.mainserverURL

      if (hsRes.user?.client_id != hsRes.home.client_id && session?.admin?.id==null)
        if (requestService.getIntDef('widget',0))
          requestService.setStatistic('widget',25,hsRes.home.id)
        else
          requestService.setStatistic('homeview',0,hsRes.home.id)
      hsRes.homeguidebooktype = Homeguidebooktype.list()
    } else {         
      response.sendError(404)
      return
    }
    if(hsRes.inrequest.timezone)
      hsRes.cur_timezone=Gmt.get(hsRes.inrequest.timezone)
    def newFlag=1
    def sCookie=requestService.getCookie(COOKIENAME)
    sCookie=sCookie?:''
    if(sCookie){
      for(sCook in sCookie.split(',')){
        if(lId.toString()==sCook)
          newFlag=0
      }
    }
    if(newFlag){
      if(sCookie.split(',').size()>=Tools.getIntVal(grailsApplication.config.cookie.home.last_visited.max,4)){
        def i=0
        def sTmp=''
        for(sCook in sCookie.split(',')){
          if(i==1)
            sTmp=sCook
          else if(i>0 && i<Tools.getIntVal(grailsApplication.config.cookie.home.last_visited.max,4))
            sTmp+=','+sCook
          i++
        }
        sCookie=sTmp
      }
      if(sCookie)
        sCookie+=','+lId
      else
        sCookie=lId.toString()
    }
    requestService.setCookie(COOKIENAME,sCookie,Tools.getIntVal(grailsApplication.config.user.timeout,259200))
    hsRes.metro=[]
    def lsHMetro=Homemetro.findAllWhere(home_id:lId)
    for(metro in lsHMetro)
      hsRes.metro<<Metro.get(metro.metro_id)
    hsRes.citysight = Citysight2home.findAllByHome_id(hsRes.home.id).collect{Citysight.get(it.citysight_id)}        
    hsRes.directionCities = Region.executeQuery("""select c.name, c.homecount, c.is_index, c.domain, c.name_en
          from Region r, City c
          where c.region_id=r.id and r.popdirection_id=:p_id and c.homecount>0 and c.id!=:c_id
          order by c.name""",[p_id:hsRes.breadcrumbs.direction?.id?:0,c_id:hsRes.home?.city_id.toInteger()?:0]).inject([:]){map,tag -> map[tag[hsRes.context?.lang?4:0]]=[count:tag[1],is_index:tag[2],domain:tag[3]];map}
    hsRes.tagcloudParams = [:]
    hsRes.tagcloudParams.maxFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.max,50)
    hsRes.tagcloudParams.middleFontCount = Tools.getIntVal(grailsApplication.config.index.cityTagCloud.fontCount.middle,20)

    return hsRes
  }

  def viewprint = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true) 
    hsRes.inrequest=[:]
    def lId=requestService.getLongDef('id',0)	
    hsRes.home = Home.read(lId)	
    hsRes.imageurl = grailsApplication.config.urluserphoto
    if(hsRes.home){
      if (hsRes.user?.client_id != hsRes.home.client_id && hsRes.home.modstatus !=1 && session?.admin?.id==null) {		
        redirect(controller:'index', action:'index',base:hsRes.context?.mainserverURL_lang)
        return
      }
      hsRes.client = User.findWhere(client_id:hsRes.home?.client_id) 
      hsRes += homeService.getSomePrice(hsRes.home)
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      def hAS = new HomeAlikeSearch()
      hsRes.anotherHomes = hAS.csiFindAnotherHomes(hsRes.home.x,hsRes.home.y,hsRes.home.id,hsRes.home.client_id)
      hsRes.anotherDistances = []
      hsRes.anotherPrices = []
      for (aH in hsRes.anotherHomes){
        hsRes.anotherDistances << Math.rint(100.0 * (aH.distance / 1000)) / 100.0
        hsRes.anotherPrices << Math.rint(100.0 * (aH.price / hsRes.valutaRates)) / 100.0
      }
      hsRes.deposit = Math.rint(hsRes.home.deposit_rub / hsRes.valutaRates)
      hsRes.ownerUsers = User.findAll('FROM User WHERE client_id=:client_id AND banned=0 ORDER BY modstatus, id',[client_id:hsRes.home.client_id])
      hsRes.homeclass=Homeclass.findAll('FROM Homeclass ORDER BY id')
  
      hsRes.homeoption=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=1 and modstatus=1 ORDER BY id')
		
      hsRes.country=Country.findAll('FROM Country ORDER BY regorder')
      if(hsRes.inrequest?.country_id?:0){
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
      }else if((hsRes.country?:[]).size()>0)
        hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])	  
      hsRes.homephoto=Homephoto.findAll('FROM Homephoto WHERE home_id=:home_id AND is_main=1 ORDER BY norder',[home_id:lId])
      hsRes.homemodstatus=Homemodstatus.findByModstatus(hsRes.home.modstatus)
      hsRes.cancellation=Rule_cancellation.findAll('FROM Rule_cancellation')
      hsRes.minday=Rule_minday.findAll('FROM Rule_minday')
      hsRes.maxday=Rule_maxday.findAll('FROM Rule_maxday')
      hsRes.timein=Rule_timein.findAll('FROM Rule_timein')
      hsRes.timeout=Rule_timeout.findAll('FROM Rule_timeout')
      hsRes.photourl = grailsApplication.config.urlphoto + hsRes.home.client_id.toString()+'/'
    } else {	
      redirect(controller:'index', action:'index',base:hsRes.context?.mainserverURL_lang)
      return
    }

    def oUcommentSearch = new UcommentSearch()
    hsRes.imageurl = grailsApplication.config.urluserphoto
    hsRes+=oUcommentSearch.csiSelectUcommentsForHome(lId,120,requestService.getOffset())
    
    hsRes.metro=[]
    def lsHMetro=Homemetro.findAllWhere(home_id:lId) 
    for(metro in lsHMetro)
      hsRes.metro<<Metro.get(metro.metro_id)
      
    if(hsRes.context?.lang){                          
      hsRes.home = hsRes.home.csiSetEnHome()                  
     
      def lsAnotherHomes=[]
      for(aH in hsRes.anotherHomes){        
        def aHTmp = new HomeAlikeSearch()
        aH.properties.each { prop, val ->
          if(["metaClass","class"].find {it == prop}) return
          if(aHTmp.hasProperty(prop)) aHTmp[prop] = val
        }
        aHTmp.id=aH.id
        aHTmp.name=Tools.transliterate(aHTmp.name,0)
        aHTmp.address=Tools.transliterate(aHTmp.address,0)
        aHTmp.city=City.get(aHTmp.city_id)?.name_en?:Tools.transliterate(aHTmp.city,0)
        
        lsAnotherHomes<<aHTmp          
      }
      hsRes.anotherHomes=lsAnotherHomes
      
      def lsOwnerUsers=[]
      for(oUser in hsRes.ownerUsers){                
        lsOwnerUsers<<oUser.csiSetEnUser()
      }
      hsRes.ownerUsers=lsOwnerUsers                  
    }  
    
    requestService.setStatistic('viewprint')

    return hsRes	
  }
  
  def trace = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true) 
    hsRes.inrequest=[:]
    def lId=requestService.getLongDef('id',0)	
    hsRes.home = Home.read(lId)

    if(!hsRes.home){
      response.sendError(404)
      return
    }

    if(hsRes.context?.lang){
      hsRes.home = hsRes.home.csiSetEnHome()                                
    }    

    requestService.setStatistic('homeview',36,lId)
    return hsRes
  }
  
  def traceprint = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true) 
    hsRes.inrequest=[:]

    hsRes+=requestService.getParams(null,['id'],['pointA','pointB'])
    hsRes.home = Home.get(hsRes.inrequest?.id)

    if(!hsRes.home){
      response.sendError(404)
      return
    }

    hsRes.metro=[]
    def lsHMetro=Homemetro.findAllWhere(home_id:hsRes.inrequest?.id) 
    for(metro in lsHMetro)
      hsRes.metro<<Metro.get(metro.metro_id)
     
    if(hsRes.context.lang){
      hsRes.home = hsRes.home.csiSetEnHome()                    
    }
    return hsRes  
  }
  
  def calculatePrice = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary()
    def lId=requestService.getLongDef('home_id',0)
    def iMod=requestService.getIntDef('modifier',0)
    hsRes+=requestService.getParams(['homeperson_id'],[],[],['date_start','date_end'])
    hsRes.result = 0
    if ((hsRes.inrequest?:[]).size()!=3) {
      hsRes.error = false
      hsRes.result = null
      render hsRes as JSON
      return
    }
    //Alex>>	
    hsRes += Home.calculateHomePrice(hsRes,lId,iMod?false:true)
    if (hsRes.errorprop)
      hsRes.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)
    def oValutarate = new Valutarate()
    requestService.setStatistic('homeview',3,lId)
    render ([error:hsRes.error,errorprop:hsRes.errorprop,result:hsRes.result,valutarate:oValutarate.csiGetRate(Home.get(lId)?.valuta_id?:857)] as JSON)
    return
  }
  
  def check_date={
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)  
    def lId=requestService.getLongDef('home_id',0)
    hsRes+=requestService.getParams([],[],[],['date_start','date_end'])
    hsRes.result = 0
    if ((hsRes.inrequest?:[]).size()!=2) {
      hsRes.error = false
      hsRes.result = null
      render hsRes as JSON
      return
    }
	
    def date_start
    def date_end
	
    if(hsRes.inrequest?.date_start?:'')
      date_start=Date.parse('yyyy-MM-dd', hsRes.inrequest?.date_start)
    if(hsRes.inrequest?.date_end?:'')
      date_end=Date.parse('yyyy-MM-dd', hsRes.inrequest?.date_end)
    if(date_start>=date_end) {
      hsRes.error = true
      hsRes.errorprop = "home.calculateHomePrice.badDate_end.errorprop"
      //return hsRes
    }
    def dateStart=new Date()
    def date1= new GregorianCalendar()
    date1.setTime(dateStart) 	  	  
    date1.set(Calendar.HOUR_OF_DAY ,0)
    date1.set(Calendar.MINUTE ,0)
    date1.set(Calendar.SECOND,0)
    date1.set(Calendar.MILLISECOND,0)
    if(date_start<date1.getTime()) {
      hsRes.error = true
      hsRes.errorprop = "home.calculateHomePrice.badDate_start.errorprop"
      //return hsRes
    }
    /*
    hsRes.home = Home.get(lId)
    if (!hsRes.home) {
      hsRes.error = true
      hsRes.errorprop = "home.calculateHomePrice.unbelievable.errorprop"
      //return hsRes
    }*/
    def hsOut=[:]
    hsOut.error=hsRes.error
    if (hsRes.errorprop)
      hsOut.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)
    
    render hsOut as JSON
    return
  }

  def infraslist = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes+=requestService.getParams(null,['home_id'])

    def oHomeguidebook = new Homeguidebook()
    hsRes.infr=oHomeguidebook.csiSelectInfras(hsRes.inrequest?.home_id?:0,5,requestService.getOffset())
    hsRes.homeguidebooktype = Homeguidebooktype.list()
    
    if(hsRes.context.lang){
      def lsHomeguid=[]
      for(infr in hsRes.infr.records){
        def oHomeguidebookTmp = new Homeguidebook()        
       
        infr.properties.each { prop, val ->        
          if(["metaClass","class"].find {it == prop}) return
          if(oHomeguidebookTmp.hasProperty(prop)) oHomeguidebookTmp[prop] = val
        }
        oHomeguidebookTmp.id=infr.id
        oHomeguidebookTmp.name=Tools.transliterate(oHomeguidebookTmp.name,0)
        oHomeguidebookTmp.description=Tools.transliterate(oHomeguidebookTmp.description,0)      
          
        lsHomeguid<<oHomeguidebookTmp        
      }
      hsRes.infr.records=lsHomeguid
    }
    
    return hsRes
  }

//owner Dmitry<<
  def addnew = {
    if(!(request.serverName.indexOf('.staytoday')==-1)){
      response.sendError(404)
      return
    }
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (hsRes.isLoginDenied) {
      redirect(controller:'index',action:'index',base:hsRes.context?.mainserverURL_lang)
      return
    }
    if (params.id){
      response.sendError(404)
      return
    }
    hsRes.vk_api_key=grailsApplication.config.vk.APIKey
    hsRes+=requestService.getParams(['hometype_id','homeperson_id','country_id','region_id','term','price','valuta_id','pindex','usage'],[],['title','description','city','district','street','email','homenumber','date_start','date_end'])
    if(hsRes.inrequest?.date_start?:'')
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
    if(hsRes.inrequest?.date_end?:'')
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
    hsRes.hometype=Hometype.findAll('FROM Hometype ORDER BY regorder ASC')
    hsRes.homeperson=Homeperson.findAll('FROM Homeperson')
    hsRes.country=Country.findAll('FROM Country ORDER BY regorder')
    hsRes.valuta=Valuta.findAll('FROM Valuta WHERE modstatus=1 ORDER BY regorder')
    hsRes.textlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
    hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
    if(hsRes.inrequest?.country_id?:0){
      hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
      /*if(hsRes.inrequest?.region_id?:0){	  
	    hsRes.city=City.findAll('FROM City WHERE region_id=:region_id ORDER BY name',[region_id:hsRes.inrequest?.region_id])
      hsRes.district=District.findAll('FROM District WHERE region_id=:region_id ORDER BY name',[region_id:hsRes.inrequest?.region_id])			  		
	  }*/
    }else if((hsRes.country?:[]).size()>0)
  	  hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])
    if (hsRes.user && !hsRes.inrequest?.email){
      def oUser = User.get(hsRes.user.id)
      hsRes.inrequest.email = oUser?.email?:''
    }

    hsRes.urlvideo=grailsApplication.config.urlvideolesson
    hsRes.lessons=Video_lesson.findAll('FROM Video_lesson WHERE type_id=1 ORDER BY nomer ASC')    
    
    hsRes.popdirection=Popdirection.findAllByModstatusAndIs_main(1,1,[sort:'rating',order:'desc'])    
    hsRes.countryIds = hsRes.popdirection.collect{it.country_id}
    hsRes.countryIds.unique()
    hsRes.countries = Country.list()

    if ((flash.error?:[]).class!=ArrayList) { flash.error = null }//dirty hack. Do not use this at production!

    requestService.setStatistic('homeaddnew')
    return hsRes
  }
  
  def addhome={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    flash.error=[]	
    flash.warning=[]
    if(hsRes.capcha_fail){//hsRes.spy_protection||	  
      redirect(controller:'index',action:'captcha')
      return
    }	
    hsRes+=requestService.getParams(['hometype_id','homeperson_id','country_id','region_id','price','valuta_id','pindex'],[],['title','description','city','district','street','email','homenumber'],['date_start','date_end'])
    hsRes.inrequest.term=(requestService.getStr('term')=='on')?1:0	
    hsRes.inrequest.usage=(requestService.getStr('usage')=='on')?1:0
    def bSave=true
    withForm{
      if(!hsRes.user){
        try{
          if (!jcaptchaService.validateResponse("image", session.id, params.captcha))
            flash.error<<99
        }catch(Exception e){
          flash.error<<99
        }	  
      }
	//>>ipBlock
	
      if(!(flash.error.size())){		  	  
        def lsTemp_ipblock=Temp_ipblock.findAllWhere(userip:request.remoteAddr,status:0)	  
        for(temp in lsTemp_ipblock){	
          def oTemp_ipblockTmp=Temp_ipblock.get(temp.id)
          oTemp_ipblockTmp.delete()	    
        }
      }else{
        def oTemp_ipblock=new Temp_ipblock()	
        oTemp_ipblock.userip=request.remoteAddr
        oTemp_ipblock.requesttime=new Date()
        oTemp_ipblock.status=0	  
        if(!oTemp_ipblock.save(flush:true)){
          log.debug(" Error on save Temp_ipblock:")
          oTemp_ipblock.errors.each{log.debug(it)}	 
        }
      }
  //<<ipBlock  		
      def lsDirectory=requestService.getParams(['hometype_id','homeperson_id','country_id','region_id','valuta_id']).inrequest

      def date_start
      def date_end	
	
      if(!(hsRes.inrequest.usage))
        flash.error<<11
      if((lsDirectory?:[]).size()!=5)
        flash.error<<10
      if(!(hsRes.inrequest?.title?:''))
        flash.error<<1
      if(!(hsRes.inrequest?.description?:''))
        flash.error<<2
      if(hsRes.inrequest?.term){
        def bDate=true
        if(!(hsRes.inrequest?.date_start?:'')){
          flash.error<<3
          bDate=false
        }
        if(!(hsRes.inrequest?.date_end?:'')){
          flash.error<<4
          bDate=false
        }
        if(bDate){
          date_start=requestService.getDate('date_start')
          date_end=requestService.getDate('date_end')		  
          if(date_start>date_end)
            flash.error<<20
        }
      }
      if(!(hsRes.inrequest?.price?:''))
        flash.error<<5
      if(!hsRes?.user ||(hsRes.user.provider!='staytoday' && hsRes.user.modstatus!=1)){	  
        if(!(hsRes.inrequest?.email?:''))
          flash.error<<6
        else if (!Tools.checkEmailString(hsRes.inrequest.email))
          flash.error<<7
      }
      if (!(hsRes.inrequest?.pindex?:0) && requestService.getStr('pindex').size()>0)
        flash.error<<8
      if(User.findWhere(email:hsRes.inrequest?.email)?.is_external==0
        && (!hsRes?.user || (hsRes.user.provider!='staytoday')))	
        flash.warning<<13 	            	  
      if(User.findWhere(email:hsRes.inrequest?.email?:'bad_email')?.is_external==1
        && (!hsRes?.user || (hsRes.user.provider!='staytoday'))
        && User.findByEmail(hsRes.inrequest?.email?:'bad_email')?.id!=hsRes?.user?.id?:0)
        flash.warning<<12
      if (!hsRes.inrequest.city&&(!Region.get(hsRes.inrequest.region_id)||Region.get(hsRes.inrequest.region_id).is_show)) {
        flash.error<<14
      }
      //>>process business  
      if(!(flash.error.size())
		&& !(flash.warning.size())){
        def lId=0
        def sCode=''	  
        def oClient=null
      //<<user
        def oUser
		def iRefId=0
        if(hsRes.user){
          if(hsRes.user.provider=='staytoday'){
            oUser=User.get(hsRes.user.id)
            oClient=Client.get(hsRes.user.client_id?:0)
            if(oClient){
              lId=oClient.id
              sCode=oClient.code
            }else{
              def bFlag=false
              if(oUser.modstatus==1){
                if(!homeService.createClient(hsRes.user.name,1))
                  bSave=false
                else
                  bFlag=true
              }else{//oUser.modstatus
                if(!homeService.createClient(hsRes.user.name,2))
                  bSave=false
                else
                  bFlag=true
              }
              if(bFlag){
                oClient=Client.findWhere(name:hsRes.user.name?:0)
                lId=oClient?.id?:0//oClient.csiGetLastInsert()
                sCode=oClient?.code?:0
                oUser.client_id = lId
                oUser.is_am = 1
                if(!oUser.save(flush:true)){
                  log.debug(" Error on save home:")
                  oUser.errors.each{log.debug(it)}
                  bSave=false
                }
              }
            }
          } else {//owner Dmitry>>
            oUser=User.get(hsRes.user.id)
            def oRefUser
            def bFlag=false
            if(!homeService.createClient(hsRes.inrequest?.email?:oUser.email,oUser.modstatus))
              bSave=false
            else
              bFlag=true
            if(bFlag){
              oClient=Client.findWhere(name:hsRes.inrequest.email?:oUser.email?:'')
              lId=oClient?.id?:0
              sCode=oClient?.code?:0
              iRefId=oUser.csiInsertInternal([email:hsRes.inrequest?.email?:oUser.email,password:Tools.hidePsw((hsRes.inrequest?.password2?:'')),firstname:(hsRes.inrequest?.firstname?:''),lastname:(hsRes.inrequest?.lastname?:''),nickname:(hsRes.inrequest?.nickname?:''),client_id:lId,code:sCode])
              oRefUser = User.get(iRefId)
              oRefUser.is_am = 1
              oRefUser.nickname = oUser.nickname
              oRefUser.description = oUser.description
              oRefUser.banned = oUser.banned
              oRefUser.is_external = oUser.is_external
              oRefUser.tel = oUser.tel
              oRefUser.tel1 = oUser.tel1
              oRefUser.companylist = oUser.companylist              
              oRefUser.inputdate = oUser.inputdate
              oRefUser.lastdate = oUser.lastdate
              oRefUser.ncomment = oUser.ncomment
              oRefUser.picture = oUser.picture
              oRefUser.smallpicture = oUser.smallpicture
              oRefUser.modstatus = oUser.modstatus
			  oRefUser.is_telcheck = oUser.is_telcheck
			  oRefUser.is_news = oUser.is_news
			  oRefUser.is_review = oUser.is_review
			  oRefUser.is_zayvka = oUser.is_zayvka
			  oRefUser.is_emailzayvka = oUser.is_emailzayvka
			  oRefUser.is_improve = oUser.is_improve
              if(!oRefUser.save(flush:true)) {
                log.debug(" Error on save user:")
                oRefUser.errors.each{log.debug(it)}
                bSave=false
              }
              oUser.ref_id = iRefId
              oUser.email = hsRes.inrequest.email?:oUser.email
              oUser.code=''
              if(!oUser.save(flush:true)){
                log.debug(" Error on save user:")
                oUser.errors.each{log.debug(it)}
                bSave=false
              }
            }
          }//<<owner Dmitry
        }else{//>>user	  	  
          oClient=Client.findWhere(name:hsRes.inrequest?.email)	  
	
          if(oClient){
          //flash.error<<100
            lId=oClient.id
            sCode=oClient.code					 
            def lsHome=Home.findAllWhere(client_id:oClient.id, modstatus:0)
            for(home in lsHome){
              def lsHomeProp=Homeprop.findAllWhere(home_id:home.id, modstatus:0)
              for(oHomeProp in lsHomeProp)
                oHomeProp.delete(flush:true)
              home.delete(flush:true)
            }				        
          }else{
            if(!homeService.createClient(hsRes.inrequest?.email,0)){
              bSave=false
            }else{
              oClient=Client.findWhere(name:hsRes.inrequest?.email)
              lId=oClient?.id?:0//oClient.csiGetLastInsert()
              sCode=oClient?.code?:0
            }
          }
        }
        if(bSave){ 
          def lHomeId=0
		  def stringlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
		  if (hsRes.inrequest?.description.size()>stringlimit) hsRes.inrequest?.description = hsRes.inrequest?.description.substring(0, stringlimit)
          def oHome=new Home(hsRes.inrequest,lId)	
          if(!oHome.save(flush:true)) {
            log.debug(" Error on save home:")
            oHome.errors.each{log.debug(it)}
            bSave=false            
          }else{
            lHomeId=oHome.id
          }	
		 
          if(lHomeId){
            def oHomeprop=new Homeprop()
            oHomeprop.home_id=lHomeId
            oHomeprop.valuta_id=hsRes.inrequest?.valuta_id
            oHomeprop.price=hsRes.inrequest?.price
            oHomeprop.setHmpPrice_rub()
            oHomeprop.modstatus=0
            if(hsRes.inrequest?.term){
              oHomeprop.date_start=date_start?:null
              oHomeprop.date_end=date_end?:null
              oHomeprop.term=1
            } else {
              def dateStart=new Date()
              def date1= new GregorianCalendar()
              date1.setTime(dateStart) 	  	  
              date1.set(Calendar.HOUR_OF_DAY ,0)
              date1.set(Calendar.MINUTE ,0)
              date1.set(Calendar.SECOND,0)
              date1.set(Calendar.MILLISECOND,0)
              oHomeprop.date_start=date1.getTime()
              date1.add(Calendar.YEAR,2)
              oHomeprop.date_end=date1.getTime()
              oHomeprop.term=2
            }
            if(!oHomeprop.save(flush:true)) {
              log.debug(" Error on save homeprop:")
              oHomeprop.errors.each{log.debug(it)}
              bSave=false		  
            }
            if(bSave&&oUser){
              homeService.setHomeModstatus(2,lHomeId,lId)
              homeService.setHomePropModstatus(2,lHomeId)
              if(oUser.modstatus==1){			    
              }else{
                oUser.modstatus=2	
                if(!oUser.save(flush:true)) {
                  log.debug(" Error on save User:")
                  oUser.errors.each{log.debug(it)}
                  bSave=false            
                }
				//note>>
				def oNote = Note.findByUser_idAndNotetype_id(iRefId?:oUser.id,1)
				if (!oNote)
				  oNote = new Note(lHomeId, iRefId?:oUser.id, Notetype.get(1))
				try{
				  oNote.save(flush:true)
				} catch (Exception e){
				  log.debug("Error on save Note in Home:addhome\n"+e.toString())
				}
				//<<note
                //<<Email
				mailerService.addhome(oUser.id,sCode,hsRes.context)                
              //>>Email
              }
              if(!bSave){
                redirect(controller:'index', action:'error',base:hsRes.context?.mainserverURL_lang)
                return			  
              }else{
                if(oUser.provider=='vkontakte'){
                  def aVk_id = oUser.openid.split('_')
                  def sStr=grailsApplication.config.vk.APIKey+aVk_id[1].toString()+grailsApplication.config.vk.SecretKey
                  flash.home_id=lHomeId
                  redirect(controller:'user', action:'vk',params:[vk_id:aVk_id[1],vk_hash:(sStr).encodeAsMD5()],base:hsRes.context?.mainserverURL_lang)
                  return
                } else if (oUser.provider=='facebook') {
                  def aFb_id = oUser.openid.split('_')
                  flash.home_id=lHomeId
                  redirect(controller:'user', action:'facebook',params:[fb_id:aFb_id[1]],base:hsRes.context?.mainserverURL_lang)
                  return
                } else {
                  redirect(controller:'personal', action:'adsoverview', id:lHomeId,base:hsRes.context?.mainserverURL_lang)
                  return
                }
              }
            }
            if(!bSave){
              redirect(controller:'index', action:'error',base:hsRes.context?.mainserverURL_lang)
              return			  
            }else{			
              redirect(controller:'user', action:'addnew',params:[email:hsRes.inrequest.email,code:sCode,home_id:lHomeId],base:hsRes.context?.mainserverURL_lang)
              return
            }  
          } 	 	    
        }
      }
    }.invalidToken {
      flash.error<<102      
    }
    if(!bSave){
      redirect(controller:'index', action:'error',base:hsRes.context?.mainserverURL_lang)
      return			  
    }else if(flash.error?.size() || flash.warning?.size())
      redirect(action:'addnew',params:hsRes.inrequest,base:hsRes.context?.mainserverURL_lang)
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Comments>>>/////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  def addcomment={    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams([],['home_id'],['comtext'])
    hsRes.inrequest.rating = requestService.getIntDef('rating',0)
    if(!hsRes.user ||!(hsRes.inrequest?.comtext?:'')){
      redirect(action:'view', id:hsRes.inrequest?.home_id,base:hsRes.context?.mainserverURL_lang)
      return
    }
    hsRes.home = Home.get(hsRes.inrequest?.home_id?:0)
    if(hsRes.home){
      if (hsRes.user.client_id == hsRes.home.client_id) {
        redirect(action:'view', id:hsRes.inrequest?.home_id,base:hsRes.context?.mainserverURL_lang)
        return
      }
      def stringlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
      if (hsRes.inrequest?.comtext.size()>stringlimit) hsRes.inrequest?.comtext = hsRes.inrequest?.comtext.substring(0, stringlimit)
      def oUcomment = new Ucomment()
      oUcomment.user_id = hsRes.user.id
      oUcomment.home_id = hsRes.inrequest?.home_id
      oUcomment.typeid = 1
      oUcomment.comtext = hsRes.inrequest?.comtext
      oUcomment.comstatus = 0
      oUcomment.rating = hsRes.inrequest?.rating
      oUcomment.refcomment_id = 0
      oUcomment.is_mainpage = 0
      oUcomment.comdate=new Date()
	    if(!oUcomment.save(flush:true)) {
        log.debug(" Error on save Ucomment:")
        oUcomment.errors.each{log.debug(it)}
      }
      if (!hsRes.home.nref)
        hsRes.home.nref = 1
      else
        hsRes.home.nref++
	    if(!hsRes.home.save(flush:true)) {
        log.debug(" Error on save Home:")
        hsRes.home.errors.each{log.debug(it)}
      }
      def oUser = User.get(hsRes.user.id)
      if (!oUser.ncomment)
        oUser.ncomment = 1
      else
        oUser.ncomment++
	    if(!oUser.save(flush:true)) {
        log.debug(" Error on save User:")
        oUser.errors.each{log.debug(it)}
      }
      def oNote = Note.findByUser_idAndNotetype_id(oUser.id,6)
      if (oNote){
        oNote.modstatus=0
        if(!oNote.save(flush:true)) {
          log.debug(" Error on save Note(home/addcomment/) id:"+oNote.id)
          oNote.errors.each { log.debug(it)}
        }
      }
      mailerService.addcomment(hsRes.home,hsRes.context)
    }

    redirect(action:'view', params:[id:hsRes.inrequest?.home_id, service:1],base:hsRes.context?.mainserverURL_lang)
  }

  def comments = {
    // rating = ucomment.rating + 1
    // '0' - no rating, '1' - neutral, '2' - negative, '3' - positive
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    def oUcommentSearch = new UcommentSearch()
    hsRes+=requestService.getParams([],['home_id','u_id','owner_id'])
    hsRes.owneruser = User.get(hsRes.inrequest?.owner_id)
    if(hsRes.context?.lang){
      hsRes.owneruser = hsRes.owneruser.csiSetEnUser()
    }
    hsRes.imageurl = grailsApplication.config.urluserphoto
    hsRes+=oUcommentSearch.csiSelectUcommentsForHome(hsRes.inrequest.home_id,3,requestService.getOffset())
    hsRes.answerComments=hsRes.records.collect {Ucomment.findAllByRefcomment_id(it.id)}
    hsRes.home = Home.get(hsRes.inrequest.home_id)
    
    if(hsRes.context?.lang){
      def lsComments=[]
      for(record in hsRes.records){             
        def oUCTmp = new UcommentSearch()
        record.properties.each { prop, val ->
          if(["metaClass","class"].find {it == prop}) return
          if(oUCTmp.hasProperty(prop)) oUCTmp[prop] = val
        }
        oUCTmp.id=record.id
        oUCTmp.nickname=Tools.transliterate(oUCTmp.nickname,0)
        
        lsComments<<oUCTmp          
      }
      hsRes.records=lsComments              
    }
    return hsRes
  }

  def commentDelete = {
    // rating = ucomment.rating + 1
    // '0' - no rating, '1' - neutral, '2' - negative, '3' - positive
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)

    hsRes+=requestService.getParams([],['id'])
	def oUcomment = Ucomment.get(hsRes.inrequest?.id?:0)
	if(oUcomment && hsRes.user.id == oUcomment.user_id){
        if (oUcomment.comstatus!=-1 && oUcomment.typeid==1) {
          def oHome = Home.get(oUcomment.home_id)
          def oUser = User.get(oUcomment.user_id)
          oHome.nref--
          oUser.ncomment--
          if(!oHome.save(flush:true)) {
            log.debug(" Error on save Home:")
            oHome.errors.each{log.debug(it)}
          }
          if(!oUser.save(flush:true)) {
            log.debug(" Error on save User:")
            oUser.errors.each{log.debug(it)}
          }
        }
		oUcomment.delete(flush:true)
	}

    render ''
  }

  def addAnswerComment = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes+=requestService.getParams(null,['com_id'],['answ_comtext'])

    def result = [:]
    result.error = false
    if (!hsRes.inrequest?.answ_comtext) {
      result.error = true
      result.message = message(code:'answerComment.nocomtext.errormessage')
    }
    if (!result.error && hsRes.user && hsRes.inrequest?.com_id) {
      try {
        def stringlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
        if (hsRes.inrequest?.answ_comtext.size()>stringlimit) hsRes.inrequest?.answ_comtext = hsRes.inrequest?.answ_comtext.substring(0, stringlimit)
        def oUcomment = new Ucomment()
        oUcomment.user_id = hsRes.user.id
        oUcomment.home_id = 0
        oUcomment.typeid = 0
        oUcomment.comtext = hsRes.inrequest?.answ_comtext
        oUcomment.comstatus = 0
        oUcomment.rating = 0
        oUcomment.refcomment_id = hsRes.inrequest?.com_id
        oUcomment.is_mainpage = 0
        oUcomment.comdate=new Date()
        oUcomment.save(flush:true)
      } catch(Exception e) {
        log.debug('Home:addAnswerComment. Error on save answer for Ucomment:'+hsRes.inrequest.com_id+'\n'+e.toString())
        result.error = true
        result.message = message(code:'answerComment.database.errormessage')
      }
    }

    render result as JSON
    return
  }

  def commentstatistic = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    def lId = requestService.getLongDef('id',0)
    requestService.setStatistic('homeview',1,lId)
    render ''
  }

  def contactstatistic = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    def lId = requestService.getLongDef('id',0)
    def iType = requestService.getIntDef('type',0)
    if (iType==1) {
      requestService.setStatistic('homeview',23,lId)
    } else if (iType==2) {
      requestService.setStatistic('homeview',24,lId)
    }
    render ''
  }

//<<owner Dmitry
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Comments<<</////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Calendar>>>/////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  def event = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes+=requestService.getParams(['pc','tz'],['id','start','end'])
    hsRes.home = Home.get(hsRes.inrequest.id)
    if (!hsRes.home) {
      redirect(controller:'index', action:'index',base:hsRes.context?.mainserverURL_lang)
      return
    }
    def event = hsRes.home.eventHome(hsRes)
    def valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    def i = 0
    for (ev in event) {
      if (ev.className=='active')
        ev.title = message(code:'home.eventHome.active.title', args:[(ev.title?:''), valutaSym], default:'').decodeHTML()
      else if (ev.className=='notavailable')
        ev.title = message(code:'home.eventHome.notavailable.title', args:[(ev.title?:'')], default:'')
      else if (ev.className=='reserved'&&hsRes.inrequest.pc)
        ev.title = message(code:'home.eventHome.reserved.title', args:[(ev.title?:'')], default:'')
      else if (ev.className=='reserved'&&!hsRes.inrequest.pc)
        ev.title = message(code:'home.eventHome.notavailable.title', args:[(ev.title?:'')], default:'')
      else
        ev.title = message(code:'', args:[(ev.title?:'')], default:'')
    }
    render event as JSON
    return
  }
  
  def calendarstatistic = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    def lId = requestService.getLongDef('id',0)
    requestService.setStatistic('homeview',2,lId)
    render ''
  }
//<<owner Dmitry
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Calendar<<</////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////
  def region={
    requestService.init(this)
    //def hsCont = requestService.getContextAndDictionary(true)
    
    def lCountryId=requestService.getIntDef('countryId',0)    
    return [region: Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',
             [country_id:lCountryId]),lang:requestService.getStr('lang')]
  }
  /*
  def city={
    requestService.init(this)
    //def hsCont = requestService.getContextAndDictionary(true)
    def lRegionId=requestService.getIntDef('regionId',0)    
    return [city: City.findAll('FROM City WHERE region_id=:region_id ORDER BY name',
             [region_id:lRegionId])]
  }
  */
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def city_autocomplete={
    requestService.init(this)
	  def hsRes = requestService.getContextAndDictionary(true)
    def sName=requestService.getStr('city')
    def iRegId=requestService.getIntDef('region_id',0)
       
    if ((sName?:'').size()>2){
      if(hsRes.context?.lang)
        hsRes.records=City.findAll('FROM City WHERE (name_en like :name1 OR name_en like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])         
      else
	      hsRes.records=City.findAll('FROM City WHERE (name like :name1 OR name like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])         
    }	
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def district_autocomplete={
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    def sName=requestService.getStr('district')
    def iRegId=requestService.getIntDef('region_id',0)
       
    if ((sName?:'').size()>2){
      if(hsRes.context?.lang)
        hsRes.records=District.findAll('FROM District WHERE (name_en like :name1 OR name_en like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])                         
      else      
        hsRes.records=District.findAll('FROM District WHERE (name like :name1 OR name like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])                         
    }	
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def street_autocomplete={
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    def sName=requestService.getStr('street')
    def iRegId=requestService.getIntDef('region_id',0)
    
    if ((sName?:'').size()>2){
      if(hsRes.context?.lang)
        hsRes.records=Street.findAll('FROM Street WHERE (name_en like :name1 OR name_en like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])         
      else
        hsRes.records=Street.findAll('FROM Street WHERE (name like :name1 OR name like :name2) AND region_id=:region_id',[name1:sName+'%',name2:'% '+sName+'%',region_id:iRegId])         
    }	
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def where_autocomplete={
    requestService.init(this)	
    def hsRes = requestService.getContextAndDictionary(true)
    def sName=requestService.getStr('query')
       
    if(sName?:''){      
      hsRes.records=Popkeywords.findAll('FROM Popkeywords WHERE name like :name ORDER BY rating DESC',[name:sName+'%'])
        
      if((hsRes.records?:[]).size()){
        def iMax=(Tools.getIntVal(grailsApplication.config.search.where_auto_complete.max,5)>=hsRes.records.size())?hsRes.records.size()-1:Tools.getIntVal(grailsApplication.config.search.where_auto_complete.max,5)       
        hsRes.records=hsRes.records[0..iMax]
      }
    }	
    def hsOut=[suggestions:[]]
    hsOut.query=sName
    
    for(item in hsRes.records)
      hsOut.suggestions<<item.name
   
    render hsOut as JSON
  }
  ////////////////////////////////////////////////////////////////////////////////
  def selectcompany = {
    requestService.init(this)
    def hsResult = requestService.getContextAndDictionary(false,false,true)    
    def lId = requestService.getLongDef('id',0)
    def oUser = User.get(hsResult.user?.id)
    def wallet=[wallet:[]]
    if (oUser) {     
      def lsCompanylist=hsResult.wallet	
      if (lId in lsCompanylist)
        lsCompanylist -= lId
      else
        lsCompanylist << lId
      def sCompanylist = ''
      lsCompanylist.each{ sCompanylist += (sCompanylist?', ':'') + it }
      oUser.companylist = sCompanylist
      if(!oUser.save(flush:true)) {
        log.debug(" Error on save users:")
        oUser.errors.each{log.debug(it)}
      }
      wallet=[wallet:lsCompanylist]
    }
    render wallet as JSON
    return
  } 
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def addmbox={
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,false,true) 
    hsRes+=requestService.getParams(['homeperson_id','is_telperm','answertype_id','timezone','modstatus','is_answer'],['id'],['mtext','email','nickname','tel'],['mail_date_begin','mail_date_end'])
    //hsRes.inrequest.is_telperm=requestService.getIntDef('is_telperm',-1)(is_telperm temporary not used)
    def date_start=requestService.getDate('mail_date_begin')
    def date_end=requestService.getDate('mail_date_end')

    flash.error=[]
    hsRes.hInrequest=[:]
    hsRes.hInrequest.mbox_error=[]

    //validating input data>>
    if (!hsRes.user) {
      if (!hsRes.inrequest.email) {
        hsRes.hInrequest.mbox_error<<6
      }else if (!Tools.checkEmailString(hsRes.inrequest.email)){
        hsRes.hInrequest.mbox_error<<8
      } else if (User.findWhere(email:hsRes.inrequest.email?:'')){
        hsRes.hInrequest.mbox_error<<5 // old user. need avtorization
      }
      if (!hsRes.inrequest.nickname)
        hsRes.hInrequest.mbox_error<<7
      if (hsRes.inrequest.tel) {
        hsRes.inrequest.tel = hsRes.inrequest.tel.replaceAll('[+( )-]','')
        if (!hsRes.inrequest.tel.isLong()||hsRes.inrequest.tel.size()<7) {
          hsRes.hInrequest.mbox_error<<10
        }
      }
    }
    if(!date_start)
      hsRes.hInrequest.mbox_error<<1
    if(!date_end)
      hsRes.hInrequest.mbox_error<<2
    if(date_start>date_end)
      hsRes.hInrequest.mbox_error<<3
    if(date_start<new Date())
      hsRes.hInrequest.mbox_error<<9
    if(!hsRes.inrequest?.mtext?:'')
      hsRes.hInrequest.mbox_error<<4
    if (hsRes.inrequest.id&&!hsRes.hInrequest.mbox_error) {
      hsRes.inrequest.date_start=hsRes.inrequest.mail_date_begin
      hsRes.inrequest.date_end=hsRes.inrequest.mail_date_end
      hsRes.result = 0
      hsRes+=Home.calculateHomePrice(hsRes,hsRes.inrequest.id,false)
    }
    //<<end of validating

    if (!hsRes.hInrequest.mbox_error&&!hsRes.error) {
      if (!hsRes.user) {
        //new user registration>>
        hsRes.isNeedSecondMail = true
        def oUser = new User()
        if (oUser.mboxNewUser([email:hsRes.inrequest.email,nickname:hsRes.inrequest.nickname])) {
          if (usersService.loginInternalUser(hsRes.inrequest.email,Tools.hidePsw(hsRes.inrequest.email)[0..8],requestService,1)){
            hsRes.user = User.findByName(hsRes.inrequest.email)
            if (hsRes.user&&hsRes.inrequest.tel) {
              hsRes.user.tel = '+'+hsRes.inrequest.tel[0]+'('+hsRes.inrequest.tel[1..3]+')'+hsRes.inrequest.tel[4..-1]
              hsRes.user.save(flush:true)
            }
          }
        }
        //<<new User registration
      }

      if(hsRes.user?.id&&hsRes.inrequest.id){
        def oTmpMbox=Mbox.findAll("FROM Mbox WHERE user_id=:user_id AND home_id=:home_id AND modstatus in (:ids)",[user_id:hsRes.user.id, home_id:hsRes.inrequest.id, ids:(Mboxmodstatus.findAllByIs_open(1)?.collect{it.modstatus}?:[])])[0]
        def oMbox
        if(!oTmpMbox){
          oMbox=new Mbox()
          oMbox.inputdate=new Date()
        }else
          oMbox=oTmpMbox

        def stringlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
        if (hsRes.inrequest?.mtext.size()>stringlimit) hsRes.inrequest?.mtext = hsRes.inrequest?.mtext.substring(0, stringlimit)
        if (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false) {
          if (hsRes.inrequest.mtext.replace('(','').replace(')','').replace('-','').replaceAll("\\s","").matches('.*[0-9]{7,}.*')||hsRes.inrequest.mtext.matches('.*\\S+@\\S*.*')||hsRes.inrequest.mtext.matches('.*\\S*@\\S+.*')) {
            hsRes.inrequest.mtext = (hsRes.inrequest.mtext?:'').replaceAll('[0-9( )-]{7,}',' [номер] ').replaceAll('\\S+@\\S*','[email]').replaceAll('\\S*@\\S+','[email]').trim()
          }
        }

        def oValutarate = new Valutarate()
        //def valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
        def valutaRates = oValutarate.csiGetRate(Home.get(hsRes.inrequest.id)?.valuta_id?:857)
    
        oMbox.price_rub=hsRes.result
        oMbox.price=(hsRes.result/valutaRates).toLong()
        //oMbox.valuta_id=hsRes.context?.shown_valuta.id?(hsRes.context?.shown_valuta?.id.toInteger()):null
        oMbox.valuta_id=Home.get(hsRes.inrequest.id)?.valuta_id?:857
        oMbox.user_id=hsRes.user.id
        oMbox.moddate=new Date()
        oMbox.date_start=date_start
        oMbox.date_end=date_end
        oMbox.homeperson_id=hsRes.inrequest.homeperson_id
        oMbox.mtext=hsRes.inrequest.mtext
        oMbox.mtextowner = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?hsRes.inrequest.mtext:''
        oMbox.is_telperm=1//hsRes.inrequest.is_telperm?1:0 (is_telperm temporary not used)
        oMbox.timezone=hsRes.inrequest.timezone
        oMbox.home_id=hsRes.inrequest.id
        oMbox.homeowner_cl_id=Home.get(hsRes.inrequest.id).client_id?:0
        oMbox.answertype_id=0
        oMbox.modstatus=1
        oMbox.is_answer=0
        oMbox.is_read=0
        oMbox.is_approved = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?1:0
  
        if (!oMbox.save(flush:true)){
          log.debug('error on save Mbox in HomeController:addmbox')
          oMbox.errors.each{log.debug(it)}
          flash.error<<1
        }
        if(!flash.error){
          hsRes.hInrequest.mbxId = oMbox.id
          def oMboxrec=new Mboxrec()
          oMboxrec.mbox_id = oMbox.id
          oMboxrec.answertype_id=0
          oMboxrec.rectext=hsRes.inrequest.mtext
          oMboxrec.is_answer=0
          oMboxrec.inputdate=new Date()
          oMboxrec.home_id=oMbox.home_id
          oMboxrec.homeperson_id=oMbox.homeperson_id
          oMboxrec.date_start=oMbox.date_start
          oMboxrec.date_end=oMbox.date_end
          oMboxrec.price_rub=hsRes.result
          oMboxrec.price=(hsRes.result/valutaRates).toLong()
          //oMboxrec.valuta_id=hsRes.context?.shown_valuta.id?(hsRes.context?.shown_valuta?.id.toInteger()):null
          oMboxrec.valuta_id=Home.get(hsRes.inrequest.id).valuta_id?:857
          oMboxrec.is_approved = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?1:0

          if (!oMboxrec.save(flush:true)){
            log.debug('error on save Mboxrec in HomeController:addmbox')
            oMboxrec.errors.each{log.debug(it)}
            flash.error<<1
          }
        }
        if(!flash.error){
          mailerService.sendMboxFirstMails(oMbox,hsRes.user.id,hsRes.context,(Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false),hsRes.isNeedSecondMail?:false)
          if ((Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)&&Tools.getIntVal(grailsApplication.config.noticeSMS.active,1)&&User.findByClient_id(Home.get(hsRes.inrequest.id)?.client_id?:0)?.is_noticeSMS) {
            try {
              smsService.sendNoticeSMS(User.findByClient_id(Home.get(hsRes.inrequest.id)?.client_id?:0),Home.get(hsRes.inrequest.id)?.region_id?:0)
            } catch(Exception e) {
              log.debug("Cannot sent sms \n"+e.toString()+'in home/addmbox')
            }
          }
          requestService.setStatistic('firstcontact',26,hsRes.inrequest.id)
          if(Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false){
            //GCM>>
            def oHomeGCM = Home.get(oMbox.home_id)
            def oClientGCM = Client.get(oHomeGCM?.client_id)
            oClientGCM = oClientGCM.parent?Client.get(oClientGCM.parent):oClientGCM
            hsRes.msg_unread_count = Mbox.executeQuery('select count(*) from Mbox where (((homeowner_cl_id=:cl_id and is_readowner=0)or(user_id=:u_id and is_readclient=0)) OR (is_read=0 and ((homeowner_cl_id=:cl_id and is_answer=0 and is_approved=1) or (user_id=:u_id and is_answer=1)))) and not(modstatus=6 or ((homeowner_cl_id=:cl_id and IFNULL(is_owfav,0)=-1)or(user_id=:u_id and IFNULL(is_clfav,0)=-1)))',[cl_id:oClientGCM?.id,u_id:0.toLong()])[0]        
            def sendGCM=[:]
            sendGCM.message='letter'
            sendGCM.msgcnt=hsRes.msg_unread_count.toString()              
      
            def lsDevices=[]    

            def lsUsers=User.findAllWhere(client_id:oClientGCM?.id)
            def user_ids=[]
            for(user in lsUsers)
              user_ids<<user.id
        
            lsDevices=Device.findAll("FROM Device WHERE user_id IN (:user_ids)",[user_ids:user_ids])
        
            //log.debug('lsDevices='+lsDevices)
            if(lsDevices){
              def lsDevices_ids=[]
        
              for(device in lsDevices)
                lsDevices_ids<<device.device
              if(lsDevices_ids)
                androidGcmService.sendMessage(sendGCM,lsDevices_ids,'message', grailsApplication.config.android.gcm.api.key ?: '')  //grailsApplication??? 
            }
            //GCM<<
          }
          mailerService.sendAdminMboxMail(oMbox.id)          
        }
      }
    }

    if (hsRes.errorprop)
      hsRes.hInrequest.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)
    hsRes.hInrequest.error=hsRes.error?1:0//boolean to int
    hsRes.hInrequest.result=hsRes.result

    render hsRes.hInrequest as JSON
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////  
  def gettimezone={
    requestService.init(this)

    def iId=requestService.getIntDef('id',0)
    def sId=(iId>0)?'UTC+'+iId:'UTC'+iId
    def hsRes=[:]
    hsRes.gmt=Gmt.findWhere(code:sId)?:Gmt.findWhere(code:'UTC+4')

    render hsRes.gmt as JSON
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  def getbanner={   
    requestService.init(this)
    def iX=requestService.getLongDef('x',0)
    def iY=requestService.getLongDef('y',0)
    def iZoom=requestService.getIntDef('zoom',0)
    def hsRes=bannersService.getBanners(iX,iY,iZoom,Tools.getIntVal(grailsApplication.config.banner.max_radius,100),request.getHeader("User-Agent")?:'')
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////  
  def isMessAllowed={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!hsRes.user){
      render(contentType:"application/json"){[error:3]}
      return
    }
    def lId = requestService.getLongDef('id',0)
    hsRes.owner = User.get(lId)
    if (!hsRes.owner){
      render(contentType:"application/json"){[error:3]}
      return
    }
    hsRes.user = User.get(hsRes.user.id)
    if (hsRes.owner.is_telaprove && !hsRes.user.is_telcheck){
      render(contentType:"application/json"){[error:1]}
      return
    }
    if (hsRes.owner.is_clientphoto && !hsRes.user.picture){
      render(contentType:"application/json"){[error:2]}
      return
    }
    render(contentType:"application/json"){[error:false]}
    return
  }

}
