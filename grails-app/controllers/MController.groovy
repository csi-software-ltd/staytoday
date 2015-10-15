import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.converters.JSON
class MController {
  def requestService
  def usersService
  def mailerService
  def billingService
  def searchService
  def homeService
  def smsService
  def PROVIDER='staytoday'  
  def static final DATE_FORMAT='yyyy-MM-dd'
  /////////////////////////////////////////////////////////////////////////////////////  
  def checkUser(hsRes) {
    if(!hsRes?.user){	  	                       
      response.sendError(401)
      return false;
    }
    return true;
  }
  def checkAccess(hsRes) { 
    if (!(hsRes.mbox && (hsRes.mbox.user_id==hsRes.user?.id || hsRes.client_id == Home.get(hsRes.mbox.home_id)?.client_id))){      
      def hsOut=[error:true]
      render "${params.jsoncallback}(${hsOut as JSON})"
      return false
    }
    return true
  }  
  def init(hsRes){   
    def hsTmp=findClientId(hsRes)
    hsTmp.imageurl = ConfigurationHolder.config.urlphoto + hsTmp.client_id.toString()+'/'
    hsTmp.homeurl = ConfigurationHolder.config.urlphoto
    hsTmp.textlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
    hsTmp.stringlimit = Tools.getIntVal(ConfigurationHolder.config.smalltext.limit,220)
    hsTmp.user = User.read(hsRes.user?.id)    
    if(hsRes.context.lang)     
      hsTmp.user = hsTmp.user.csiSetEnUser()    
    return hsTmp
  }
  def findClientId(hsRes){    
    def hsTmp=[:]
    hsTmp.client_id=0.toLong()	
    if(hsRes?.user?.client_id)
      hsTmp.client_id=hsRes?.user?.client_id	
    else if(hsRes?.user?.ref_id)	
      hsTmp.client_id=User.get(hsRes?.user?.ref_id)?.client_id?:0	        	
    return hsTmp	
  }
  def findAreaNearPoint(iX,iY){
    def iRadius=Tools.getIntVal(ConfigurationHolder.config.mobile.search_near.radius.max,100)//in km
    def aCoorinates=[]
    aCoorinates<<findCoordByCenterAndRadius(iX,iY,-135, iRadius)
    aCoorinates<<findCoordByCenterAndRadius(iX,iY,45, iRadius)
    return aCoorinates
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes+=requestService.getParams(['homeperson_id','sort'],['x','y'],['date_start','date_end'])      

    hsRes.inrequest.where=requestService.getStr('where')    
    if(hsRes.inrequest?.date_start?:'')
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)      
    if(hsRes.inrequest?.date_end?:'')
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
    if(hsRes.inrequest?.date_start && hsRes.inrequest?.date_end)
      use(groovy.time.TimeCategory) {
        def duration = hsRes.inrequest?.date_end - hsRes.inrequest?.date_start
        hsRes.days_between = duration.days			
      }    
    def cityList = Home.executeQuery("""select c.name,count(h.id),c.is_index,c.name2,c.country_id,c.domain,c.name_en
      from Home h, City c
      where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
      group by h.city_id
      having count(h.id) > :minCount
      order by c.name""",["minCount":(Tools.getIntVal(ConfigurationHolder.config.index.cityTagCloud.minCityCount,5) as long)])
    hsRes.citycloud = cityList.inject([:]){map,tag -> map[hsRes.context?.lang?tag[6]:tag[0]]=[count:tag[1],is_index:tag[2],name2:hsRes.context?.lang?tag[6]:tag[3],country_id:tag[4],domain:tag[5]];map}    
    hsRes.citycloudParams = [:]
    hsRes.citycloudParams.maxFontCount = Tools.getIntVal(ConfigurationHolder.config.index.cityTagCloud.fontCount.max,50)
    hsRes.citycloudParams.middleFontCount = Tools.getIntVal(ConfigurationHolder.config.index.cityTagCloud.fontCount.middle,20)

    return hsRes  
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def about = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)    
    
    return hsRes  
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def terms = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)    
    
    return hsRes  
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def contract = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)    
    
    return hsRes  
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def help = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)    
    
    return hsRes  
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def auth = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.from=requestService.getStr('from')
    
    return hsRes  
  }  
  def login = {  
    requestService.init(this)
    requestService.setCookie('user','parararam',10000)
    def hsInrequest = requestService.getParams(['user_index','id'],['control','act','where']).inrequest 
    def sUser=requestService.getStr('user')        
    def sPassword=requestService.getStr('password')
    flash.error=[]
    
    if((sUser=='')||(sUser=='Ð»Ð¾Ð³Ð¸Ð½'))
      flash.error << 1 // set user
    if(sUser!='' && sPassword!='' && !usersService.loginInternalUser(sUser,sPassword,requestService,1,0))
      flash.error << 2 // Wrong password or user does not exists        		
    if (sUser!='' && !User.findWhere(name:sUser)){
      hsInrequest.user = sUser
      flash.error << 3 // new user. need registration
    }
    if (!sPassword){
      hsInrequest.user = sUser
      flash.error << 4 // empty password is not allowed
    }
    if(flash.error){
      render ([error:flash.error] as JSON)
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  def logout = {
    requestService.init(this)           
    usersService.logoutUser(requestService)      
    def hsRes=requestService.getContextAndDictionary(true)    
    def sTmp=request.getHeader('referer')
    def sId=requestService.getStr('id')
    if(sId in ['inbox','bron'] || sTmp==null){
      redirect(uri:'',base:hsRes.context?.mobileURL_lang)      
      return
    }
    else
      redirect(url:sTmp)   
  } 
  def facebook={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    requestService.setCookie('user','parararam',10000)
    def hsInrequest = requestService.getParams([],['id','m_fb_id','fb_id'],['fb_name','fb_pic','fb_photo','fb_email']).inrequest
    if (hsInrequest.fb_id?:0){
      if(!usersService.loginUser('f_'+hsInrequest.fb_id.toString(),'','facebook',requestService,''))
        flash.error=5 // if user banned      
    } else if(hsInrequest.m_fb_id?:0) {
      try {        
        def bNewUser = true
        if (User.findWhere(openid:'f_'+hsInrequest.m_fb_id.toString())?.ref_id?:0)
          bNewUser = false        
        if(!usersService.loginUser('f_'+hsInrequest.m_fb_id,hsInrequest.fb_name,'facebook',requestService,hsInrequest.fb_photo,flash?.ref_id?:0))
          flash.error=5 // if user banned         
        else{
          def oUser=User.findWhere(openid:'f_'+hsInrequest.m_fb_id.toString())
          if(oUser!=null){
            oUser.lastdate=new Date()
            oUser.smallpicture=hsInrequest.fb_pic
            oUser.picture=hsInrequest.fb_photo
            oUser.email=oUser.email?:hsInrequest.fb_email

			      if(!oUser.save(flush:true)) {
				      log.debug(" Error on save User:")
				      oUser.errors.each{log.debug(it)}	 
			      }
            if (oUser.ref_id){
              def oRefUser=User.get(oUser.ref_id)
              oRefUser.nickname = oUser.nickname
              oRefUser.picture = oUser.picture
              oRefUser.smallpicture = oUser.smallpicture
              oRefUser.lastdate = oUser.lastdate
              if(!oRefUser.save(flush:true)) {
                log.debug(" Error on save User:")
                oRefUser.errors.each{log.debug(it)}
              }
            }
          }
        }
      } catch (Exception e) {        
        log.debug('Facebook parse error :'+e.toString())        
        flash.error=3
      }
    }else
      flash.error=6	
    if(flash.error){      
      render (flash as JSON)
      return
    }
    redirect(uri:'',base:hsRes.context?.mobileURL_lang)	  
    return
  }  
  def reg = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    return hsRes  
  }
  def restore = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    return hsRes  
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def s = {  
    requestService.init(this)    
    def hsRes=requestService.getContextAndDictionary(true)    
    def sWhere=requestService.getStr('where')
    def oCity = City.get(Sphinx.searchCityBySphinxLimit(sWhere,log,hsRes.context?.lang).ids[0])
    def iMetro_id = requestService.getIntDef('metro_id',0)
    def sMetro_id = requestService.getStr('metro_id')    
    def iType_id = requestService.getIntDef('hometype_id',0)
    def sType_url = requestService.getStr('hometype_id')
    def iBedroom = requestService.getIntDef('bedroom',0)
    def iNear = requestService.getIntDef('is_near',0)
    def iCitySight_id = requestService.getIntDef('citysight_id',0)
    def sCitySight_id = requestService.getStr('citysight_id')
     
    if (oCity) {      
      params.where = oCity['name'+hsRes.context?.lang]
      if (!iType_id && sType_url)
        iType_id = Hometype.findByUrlname(sType_url)?.id?:0
      if (!iCitySight_id && sCitySight_id)
        iCitySight_id = Citysight.findWhere(city_id:oCity.id,urlname:sCitySight_id)?.id?:0  
      if (!iMetro_id && sMetro_id)
        iMetro_id = Metro.findWhere(city_id:oCity.id,urlname:sMetro_id)?.id?:0          
      if (iMetro_id)
        redirect(mapping:"hSearchMetro"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,metro_url:Metro.get(iMetro_id)?.urlname]+(sType_url?[hometype_id:iType_id.toString()]:[:])-[hometype_id:sType_url,metro_id:iMetro_id.toString(),homeperson_id:'1',x:params.x,y:params.y,sort:params.sort,is_near:params.is_near]-(sType_url!='flats'?[bedroom:iBedroom.toString()]:[:]),base:hsRes.context.mobileURL,permanent:true)
      else if (iBedroom && sType_url=='flats')
        redirect(mapping:"hSearchRoom"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,type_url:'flats',bedroom:iBedroom]-[hometype_id:sType_url,homeperson_id:'1',x:params.x,y:params.y,sort:params.sort,is_near:params.is_near],base:hsRes.context.mobileURL,permanent:true)
      else if (iCitySight_id)
        redirect(mapping:"hSearchCitysight"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,citysight_url:Citysight.get(iCitySight_id)?.urlname]+(sType_url?[hometype_id:iType_id.toString()]:[:])-[hometype_id:sType_url,citysight_id:iCitySight_id.toString(),homeperson_id:'1',x:params.x,y:params.y,sort:params.sort,is_near:params.is_near,hometype_id:'all']-(sType_url!='flats'?[bedroom:iBedroom.toString()]:[:]),base:hsRes.context.mobileURL,permanent:true)
      else if (sType_url!='flats')
        redirect(mapping:"hSearchType"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,type_url:sType_url?:'all']-[hometype_id:sType_url,homeperson_id:'1',x:params.x,y:params.y,sort:params.sort,is_near:params.is_near]-(sType_url!='flats'?[bedroom:iBedroom.toString()]:[:]),base:hsRes.context.mobileURL,permanent:true)
      else
        redirect(mapping:"hSearch"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname,type_url:sType_url?:'all']-[hometype_id:sType_url,homeperson_id:'1',x:params.x,y:params.y,sort:params.sort,is_near:params.is_near],base:hsRes.context.mobileURL,permanent:true)
    } else {
      if (iNear){
        redirect(mapping:"near"+hsRes.context?.lang,params:[type_url:sType_url,x:params.x,y:params.y,date_start:params.date_start,date_end:params.date_end],base:hsRes.context.mobileURL_lang,permanent:true)
      }else if (iBedroom && sType_url=='flats')
        redirect(mapping:"cSearchRoom"+hsRes.context?.lang,params:params-[hometype_id:params.hometype_id,homeperson_id:'1']+[type_url:sType_url],base:hsRes.context.mobileURL,permanent:true)
      else if (sType_url)
        redirect(mapping:"cSearchType"+hsRes.context?.lang,params:params-[hometype_id:params.hometype_id,homeperson_id:'1']+[type_url:sType_url],base:hsRes.context.mobileURL,permanent:true)
      else
        redirect(action:'list',params:params-[homeperson_id:'1'],base:hsRes.context.mobileURL_lang,permanent:true)
    }      
    return
  }
  /////////////////////////////////////////////////////////////////////////////////////  
  def list = {   
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes+=requestService.getParams(['sort','is_near'],[],['type_url','date_start','date_end'])
    hsRes.inrequest.where=requestService.getStr('where')
    hsRes.userPoint=[x:requestService.getLongDef('x',0),y:requestService.getLongDef('y',0)]	        
    hsRes.inrequest.hometype_id=requestService.getIntDef('hometype_id',0)
    hsRes.inrequest.homeperson_id=requestService.getIntDef('homeperson_id',1)
    hsRes.inrequest.bedroom = requestService.getIntDef('bedroom',0)
    hsRes.inrequest.metro_url=requestService.getStr('metro_url')
    hsRes.inrequest.citysight_url=requestService.getStr('citysight_url')
    
    if(hsRes.inrequest?.date_start?:'')
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)      
    if(hsRes.inrequest?.date_end?:'')
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)                                           

    hsRes.inrequest.district_id=requestService.getIds('district_id') 
    hsRes.inrequest.metro_id=requestService.getIds('metro_id')    
    hsRes.inrequest.citysight_id=requestService.getIds('citysight_id')
    
    if (hsRes.inrequest.hometype_id && !Hometype.get(hsRes.inrequest.hometype_id)){
      response.sendError(404)
      return
    }
    if (hsRes.inrequest.hometype_id==1) {
      hsRes.inrequest.bedroom = requestService.getLongDef('bedroom',0)
      if(hsRes.inrequest.bedroom && !Homeroom.get(hsRes.inrequest.bedroom)){
        response.sendError(404)
        return
      }
    } else if (requestService.getLongDef('bedroom',0)) {
      response.sendError(404)
      return
    }

    def oCity = City.findByName(hsRes.inrequest.where)
    if(!oCity && hsRes.context?.lang)
      oCity = City.findByName_en(hsRes.inrequest.where)      
    
    if(oCity && hsRes.inrequest?.type_url!='all' && !hsRes.inrequest.hometype_id && !hsRes.inrequest.metro_id && !hsRes.inrequest.citysight_id)
      hsRes.inrequest.hometype_id=1
      
    if(oCity){
      if(hsRes.inrequest?.type_url=='flats' && !hsRes.inrequest.bedroom){
        redirect(mapping:"hSearch"+hsRes.context?.lang,params:params+[country:Country.get(oCity?.country_id)?.urlname]-[hometype_id:params.hometype_id,type_url:'flats',homeperson_id:'1'],base:hsRes.context?.mobileURL,permanent:true)
        return
      }  
    }
    if (oCity&&(Country.findByUrlname(params.country)?.id?:0)!=oCity.country_id) {
      redirect(action:'s',params:params-[hometype_id:params.hometype_id]+[hometype_id:Hometype.get(hsRes.inrequest.hometype_id?:0)?.urlname?:'all'],base:hsRes.context?.mainserverURL_lang,permanent:true)
      return
    }
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto    
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
        
    hsRes.homeperson=Homeperson.list()
    hsRes.homeoption=Homeoption.findAllByFacilitygroup_idAndModstatus(1,1,[sort:'name',order:'asc'])

    hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.search.listing.max,5)	  
    hsRes.paging_set=Tools.getIntVal(ConfigurationHolder.config.mobile.paging_set,5)	
    
    hsRes+=searchService._getFilter(requestService)

    def bSearchNear=false
    if (hsRes.userPoint.x && hsRes.userPoint.y){     
      hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.search_near.max,1000)
      def coordinates=findAreaNearPoint(hsRes.userPoint.x/100000,hsRes.userPoint.y/100000)
      hsRes+=_getMapFilter(hsRes.hsFilter,Math.round(coordinates[0][0]).toLong(),Math.round(coordinates[0][1]).toLong(),Math.round(coordinates[1][0]).toLong(),Math.round(coordinates[1][1]).toLong())
      bSearchNear=true
    }	
    def oHomeSearch=new HomeSearch()
    hsRes+=oHomeSearch.csiFindByWhere(hsRes.inrequest.where,hsRes.max,bSearchNear?0:(requestService.getOffset()-1),_getMainFilter(hsRes.inrequest),hsRes.hsFilter,true,hsRes.inrequest?.is_near?false:true,bSearchNear,true,true,hsRes.context?.lang)

    if ((hsRes.hometypeLinksData?.max{it?.value}?.value?:0)==0&&hsRes.inrequest.where&&!City.findByName(hsRes.inrequest.where)&&!City.findByName_en(hsRes.inrequest.where)) {
      response.sendError(404)
      return
    }
    
    hsRes+=searchService._getFilterDictionary(hsRes.homeclassMax,hsRes.districtMax,hsRes.metroMax,hsRes.citysightMax)
    hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.count,hsRes.max,hsRes.paging_set)      		
	  
    hsRes.linkparams = { map, linkname ->
      hsRes.string?.date_start?map+=[date_start:hsRes.string?.date_start]:'';
      hsRes.string?.date_end?map+=[date_end:hsRes.string?.date_end]:'';
      (hsRes.inrequest?.homeperson_id?:1)!=1?map+=[homeperson_id:hsRes.inrequest?.homeperson_id]:'';
      map;
    }
  //near>>
    if (hsRes.userPoint.x && hsRes.userPoint.y){
      if(!hsRes.inrequest.sort){
        hsRes.records = oHomeSearch.sortByDistanceFromPoint(hsRes.records, hsRes.userPoint)	
      }
      hsRes.near_page=Tools.getIntVal(ConfigurationHolder.config.mobile.near.paging,5)
      hsRes.offset=requestService.getOffset()
      hsRes.nextOffset=hsRes.near_page+requestService.getOffset()      
    //paging>>
      hsRes.records1=[]
      def i=0 
      for(oHome in hsRes.records){
	    if(i>=hsRes.offset && i<hsRes.nextOffset)
	      hsRes.records1<<oHome
	    i++ 
      }	
      hsRes.records=hsRes.records1
      hsRes.pointDistances = oHomeSearch.calcDistanceFromPoint(hsRes.records, hsRes.userPoint)      
    }
    if(hsRes.context?.lang)
      for(oHome in hsRes.records){
        oHome.name=Tools.transliterate(oHome.name,0)
        oHome.shortaddress=Tools.transliterate(oHome.shortaddress,0)
        oHome.city=City.findByName(oHome.city)?.name_en?:Tools.transliterate(oHome.city,0)                        
      }
    hsRes.discount_defaultext=message(code:'search.discount.defaulttext')

    hsRes.infotags=[:]
    hsRes.breadcrumbs=[:]
    
    hsRes.breadcrumbs.city = (hsRes.context?.lang)?City.findByName_en(hsRes.inrequest.where):City.findByName(hsRes.inrequest.where)
    hsRes.breadcrumbs.region = hsRes.breadcrumbs.city?Region.get(hsRes.breadcrumbs.city?.region_id?:0):((hsRes.context?.lang)?Region.findByName_en(hsRes.inrequest.where):Region.findByName(hsRes.inrequest.where))
    hsRes.breadcrumbs.regionalCity = City.findAll("from City where region_id=:r_id and id!=:id and homecount>0",[r_id:hsRes.breadcrumbs.city?.region_id?:0,id:hsRes.breadcrumbs.city?.id?:0])
    hsRes.breadcrumbs.direction = Popdirection.get(hsRes.breadcrumbs.region?.popdirection_id?:0)
    hsRes.breadcrumbs.country = Country.get(hsRes.breadcrumbs.region?.country_id?:0)
    hsRes.breadcrumbs.citysight = [:]
    hsRes.breadcrumbs.base = hsRes.breadcrumbs.city?.domain?'http://'+hsRes.breadcrumbs.city.domain+(hsRes.context.is_dev?':8080/Arenda':''):hsRes.context.absolute_lang
    

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
    hsRes.infotags.hometypes = Hometype.get(hsRes.inrequest.hometype_id?:0)?(Hometype.get(hsRes.inrequest.hometype_id?:0)['name3'+hsRes.context?.lang]?:message(code:'server.housing')):message(code:'server.housing')
    hsRes.infotags.hometypess = Hometype.get(hsRes.inrequest.hometype_id?:0)?(Hometype.get(hsRes.inrequest.hometype_id?:0)['name'+(hsRes.context?.lang?'3_en':'4')]?:message(code:'server.housings')):message(code:'server.housings')    
    hsRes.infotags.homeroom = Homeroom.get(hsRes.inrequest.bedroom?:0)?(Homeroom.get(hsRes.inrequest.bedroom?:0)['name'+(hsRes.context?.lang?'_en':'2')]?:''):''    
    hsRes.infotags.homerooms = Homeroom.get(hsRes.inrequest.bedroom?:0)?(Homeroom.get(hsRes.inrequest.bedroom?:0)['name3'+hsRes.context?.lang]?:''):''
    hsRes.infotags.homeroomss = Homeroom.get(hsRes.inrequest.bedroom?:0)?(Homeroom.get(hsRes.inrequest.bedroom?:0)['name'+(hsRes.context?.lang?'3_en':'4')]?:''):''     
    hsRes.infotags.homeroom2 = Homeroom.get(hsRes.inrequest.bedroom?:0)?(Homeroom.get(hsRes.inrequest.bedroom?:0)['name'+(hsRes.context?.lang?'3_en':'5')]?:''):''
    hsRes.sight_linkparams = { map, linkname ->
      hsRes.inrequest?.hometype_id?map+=[hometype_id:hsRes.inrequest?.hometype_id]:'';
      hsRes.inrequest?.longdiscount?map+=[longdiscount:1]:'';
      hsRes.inrequest?.hotdiscount?map+=[hotdiscount:1]:'';      
      hsRes.string?.date_start?map+=[date_start:hsRes.string?.date_start]:'';
      hsRes.string?.date_end?map+=[date_end:hsRes.string?.date_end]:'';
      (hsRes.inrequest?.homeperson_id?:1)!=1?map+=[homeperson_id:hsRes.inrequest?.homeperson_id]:'';
      map;
    }
    
    if(hsRes.context?.lang)
      homeService.generateInfotagsLang(hsRes)
    else
      homeService.generateInfotags(hsRes)

    return hsRes
  }   
  def search_list = {  
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,false,true)
    hsRes+=requestService.getParams(['hometype_id','homeperson_id','term','price',
      'valuta_id','pindex','usage','is_nosmoking','is_parking','is_visa','is_tv','is_internet','is_wifi','is_cond',
      'is_heat','is_kitchen','is_holod','is_microwave','is_wash','is_breakfast','is_swim','is_steam','is_gym',
      'is_hall','is_family','is_pets','is_invalid','is_area','is_beach','is_coffee','is_fen','is_hometheater',
      'is_iron','is_jacuzzi','is_kettle','is_vip','is_renthour','nref','rating','hotdiscount','longdiscount',
      'sort','bathroom','bed','bedroom','shome'],['price_min','price_max'],['title','description','city','district', 'street','email','homenumber','keywords','date_start','date_end','type_url'],[])
      
    hsRes.inrequest.district_id=requestService.getIds('district_id') 
    hsRes.inrequest.metro_id=requestService.getIds('metro_id')
    hsRes.inrequest.citysight_id=requestService.getIds('citysight_id')    
    hsRes.inrequest.where=requestService.getStr('where')
    hsRes.userPoint=[x:requestService.getLongDef('x',0),y:requestService.getLongDef('y',0)]	

    if(hsRes.inrequest?.date_start?:'')
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)      
    if(hsRes.inrequest?.date_end?:'')
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)

    if(hsRes.inrequest?.type_url!='all' && !hsRes.inrequest.hometype_id && !hsRes.inrequest.metro_id && !hsRes.inrequest.citysight_id)
      hsRes.inrequest.hometype_id = 1

    hsRes.urlphoto = ConfigurationHolder.config.urlphoto    
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
        
    hsRes.homeperson=Homeperson.list()
    hsRes.homeoption=Homeoption.findAllByFacilitygroup_idAndModstatus(1,1,[sort:'name',order:'asc'])

    hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.search.listing.max,5)	  
    hsRes.paging_set=Tools.getIntVal(ConfigurationHolder.config.mobile.paging_set,5)	
    hsRes.linkparams = { map, linkname ->
      hsRes.string?.date_start?map+=[date_start:hsRes.string?.date_start]:'';
      hsRes.string?.date_end?map+=[date_end:hsRes.string?.date_end]:'';
      (hsRes.inrequest?.homeperson_id?:1)!=1?map+=[homeperson_id:hsRes.inrequest?.homeperson_id]:'';
      map;
    }
    
    hsRes+=searchService._getFilter(requestService)

    hsRes.bSearchNear=false
    if (hsRes.userPoint.x && hsRes.userPoint.y){     
      hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.search_near.max,1000)
      def coordinates=findAreaNearPoint(hsRes.userPoint.x/100000,hsRes.userPoint.y/100000)
      hsRes+=_getMapFilter(hsRes.hsFilter,Math.round(coordinates[0][0]).toLong(),Math.round(coordinates[0][1]).toLong(),Math.round(coordinates[1][0]).toLong(),Math.round(coordinates[1][1]).toLong())
      hsRes.bSearchNear=true
    }	
    def oHomeSearch=new HomeSearch()
    hsRes+=oHomeSearch.csiFindByWhere(hsRes.inrequest.where,hsRes.max,(hsRes.bSearchNear)?0:(requestService.getOffset()-1),_getMainFilter(hsRes.inrequest),hsRes.hsFilter,true,requestService.getIntDef('is_near',0)?false:true,hsRes.bSearchNear,true,true,hsRes.context?.lang)

    hsRes+=searchService._getFilterDictionary(hsRes.homeclassMax,hsRes.districtMax,hsRes.metroMax)
    hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.count,hsRes.max,hsRes.paging_set)      		

  //near>>
    if (hsRes.userPoint.x && hsRes.userPoint.y){	
      if(!hsRes.inrequest.sort){
        hsRes.records = oHomeSearch.sortByDistanceFromPoint(hsRes.records, hsRes.userPoint)	
      }
      hsRes.near_page=Tools.getIntVal(ConfigurationHolder.config.mobile.near.paging,5)
      hsRes.offset=requestService.getOffset()
      hsRes.nextOffset=hsRes.near_page+requestService.getOffset()      
    //paging>>
      hsRes.records1=[]
      def i=0 
      for(oHome in hsRes.records){
	    if(i>=hsRes.offset && i<hsRes.nextOffset)
	      hsRes.records1<<oHome
	    i++ 
      }	
      hsRes.records=hsRes.records1
      hsRes.pointDistances = oHomeSearch.calcDistanceFromPoint(hsRes.records, hsRes.userPoint)      
    }
    if(hsRes.context?.lang)      
      for(oHome in hsRes.records){
        oHome.name=Tools.transliterate(oHome.name,0)
        oHome.shortaddress=Tools.transliterate(oHome.shortaddress,0)
        oHome.city=City.findByName(oHome.city)?.name_en?:Tools.transliterate(oHome.city,0)                        
      }        
    hsRes.discount_defaultext=message(code:'search.discount.defaulttext')

    render(template: "search_list", model: hsRes)
    return
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def _getMainFilter(hsInrequest){
    requestService.init(this)
    def hsMainFilter=[set:false]	
    hsMainFilter.homeperson_id=requestService.getIntDef('homeperson_id',0)
    hsMainFilter.hometype_id=requestService.getIntDef('hometype_id',0)
    if(!hsMainFilter.hometype_id){//here because of /type_flats hometype_id value                  
      if(hsInrequest?.hometype_id==1)
        hsMainFilter.hometype_id=1
      else
        hsMainFilter.hometype_id=Hometype.findByUrlname(requestService.getStr('hometype_id'))?.id?:0         
    }    
    hsMainFilter.order=requestService.getIntDef('sort',0)
    hsMainFilter.date_start=hsInrequest?.date_start
    hsMainFilter.date_end=hsInrequest?.date_end		
    return hsMainFilter
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def _getMapFilter(hsFilter,xl,yd,xr,yu){  
    requestService.init(this)
    def hsRes=[hsFilter:hsFilter]
    hsRes.hsFilter.coordinates=false    
    hsRes.hsFilter.xl=xl
    hsRes.hsFilter.yd=yd
    hsRes.hsFilter.xr=xr
    hsRes.hsFilter.yu=yu
    
    if(hsRes.hsFilter.xr!=0 && hsRes.hsFilter.yu!=0){//hsFilter.xl!=0 && hsFilter.yd!=0
      hsRes.hsFilter.coordinates=true
    }
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////
  def detail = {
    requestService.init(this) 
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes+=requestService.getParams(['homeperson_id'],[],['date_start','date_end'])    
    
    hsRes.inrequest.is_near=requestService.getIntDef('is_near',0)
    hsRes.userPoint=[x:requestService.getLongDef('x',0),y:requestService.getLongDef('y',0)]
    
    if(hsRes.inrequest?.date_start?:'')
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)      
    if(hsRes.inrequest?.date_end?:'')
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
    if(hsRes.inrequest?.date_start && hsRes.inrequest?.date_end)
      use(groovy.time.TimeCategory) {
        def duration = hsRes.inrequest?.date_end - hsRes.inrequest?.date_start
        hsRes.days_between = duration.days			
      }	      
    
    def lId=requestService.getLongDef('id',0)	
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto    
    hsRes.home = Home.get(lId)
    if(hsRes.home){
      if(hsRes.home.region_id==114&&(Country.findByUrlname(params.country)?.id?:0)==3){
        redirect(mapping:"hView",params:params-[city:params.city,id:lId.toString(),country:params.country]+[city:hsRes.home.city,country:'russia'],permanent:true,base:hsRes.context?.mobileURL_lang)
        return
      }
      if ((Country.findByUrlname(params.country)?.id?:0)!=hsRes.home.country_id) {
        response.sendError(404)
        return
      }
      hsRes.ownerUsers = User.findAll('FROM User WHERE client_id=:client_id AND banned=0 ORDER BY modstatus, id',[client_id:hsRes.home.client_id])
      if(!hsRes.ownerUsers){
        response.sendError(404)
        return
      }
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

      hsRes.homeoption_name=[:]
      hsRes.homeoption=Homeoption.findAllByFacilitygroup_idAndModstatus(1,1,[sort:'id'])
      //because of old mobile client code
      for(oHOpt in hsRes.homeoption){
        if(oHOpt.fieldname)
          hsRes.homeoption_name[oHOpt.id-1]=oHOpt.fieldname
      }  
      hsRes.homephoto=Homephoto.findAllByHome_id(lId,[sort:'norder'])    
      hsRes.hometype=Hometype.get(hsRes.home.hometype_id)
      hsRes.homeperson=Homeperson.get(hsRes.home.homeperson_id)
      hsRes.homeroom=Homeroom.get(hsRes.home.homeroom_id?:1)
      hsRes.homebath=Homebath.get(hsRes.home.homebath_id?:1)
      hsRes.country=Country.get(hsRes.home?.country_id)

      def oCity = City.findByName(hsRes.home.city)      
      hsRes.homecity = City.findByName(hsRes.home.city)      
      if(hsRes.context?.lang){
        if(!oCity)
          oCity = City.findByName_en(hsRes.home.city)      
        if(!hsRes.homecity)
          hsRes.homecity = City.findByName_en(hsRes.home.city)      
        hsRes.homecity_ru = hsRes.home.city
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

      if(hsRes.user)
        hsRes.user = User.get(hsRes.user.id)
      hsRes.client = User.findWhere(client_id:hsRes.home?.client_id)
        
      if(hsRes.context?.lang){
        hsRes.client = hsRes.client.csiSetEnUser()
        if (hsRes.user)
          hsRes.user = hsRes.user.csiSetEnUser()
      }        
      hsRes.alikeWhere = hsRes.home.csiGetWhere(hsRes.context?.lang)
      hsRes.deposit = Math.round(hsRes.home.deposit_rub / hsRes.valutaRates)
      hsRes.cleanup = Math.round(hsRes.home.cleanup_rub / hsRes.valutaRates)
      hsRes.fee = Math.round(hsRes.home.fee_rub / hsRes.valutaRates)
      hsRes.minday=Rule_minday.get(hsRes.home.rule_minday_id?:1)
      hsRes.maxday=Rule_maxday.get(hsRes.home.rule_maxday_id?:1)
      hsRes.timein=Rule_timein.get(hsRes.home.rule_timein_id?:1)
      hsRes.timeout=Rule_timein.get(hsRes.home.rule_timeout_id?:1)
      hsRes.cancellation=Rule_cancellation.get(hsRes.home.rule_cancellation_id?:1)

      hsRes.discounts = hsRes.home.csiGetHomeDiscounts()
      hsRes.curdiscount = hsRes.home.csiGetDisplayDiscount(hsRes.inrequest?.date_start?:null,hsRes.inrequest?.date_end?:null)    
      if(hsRes.discounts){
        hsRes.hot_discount_percent=Discountpercent.findByPercent(hsRes.discounts?.hot?.discount?:0)?.name?:''                       
        hsRes.long_discount_percent=Discountpercent.findByPercent(hsRes.discounts?.long?.discount?:0)?.name?:''
        hsRes.future_discount_percent=hsRes.discounts?.hot?.modstatus?Discountpercent.findByPercent(hsRes.discounts?.hot?.discount?:0)?.name:
                                                                      Discountpercent.findByPercent(hsRes.discounts?.long?.discount?:0)?.name
        hsRes.hot_expire_days=Timetodecide.findByDays(hsRes.discounts?.hot?.discexpiredays?:0)?.name2?:''
        hsRes.hot_min_rent_days=Timetodecide.findByDays(hsRes.discounts?.hot?.minrentdays?:0)?.name2?:"Ð»ÑŽÐ±Ð¾Ð¹ ÑÑ€Ð¾Ðº"      
        
        hsRes.long_expire_days=Timetodecide.findByDays(hsRes.discounts?.long?.discexpiredays?:0)?.name2?:''
        hsRes.longt_expire_days=Timetodecide.findByDays(hsRes.discounts?.long?.minrentdays?:0)?.name2?:"Ð»ÑŽÐ±Ð¾Ð¹ ÑÑ€Ð¾Ðº"
      }   
      hsRes.cancellations=Rule_cancellation.list()
      hsRes.imageurl = ConfigurationHolder.config.urluserphoto
      hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.comments.listing.max,3)
      hsRes.owneruser = hsRes.ownerUsers[0]
      hsRes+=new UcommentSearch().csiSelectUcommentsForHome(hsRes.home?.id,hsRes.max,requestService.getOffset()-1)
      hsRes.answerComments=hsRes.records.collect {Ucomment.findAllByRefcomment_id(it.id,[sort:'id',order:'desc'])}
      hsRes.home_clients=hsRes.records.collect {Home.get(it?.home_id).client_id}
    } else {
      response.sendError(404)
      return
    }

    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////    
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
  /////////////////////////////////////////////////////////////////////////////////////  
  def cancellation = { 
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.cancellation=Rule_cancellation.list()
	
    render hsRes as JSON
    return 
  }
  /////////////////////////////////////////////////////////////////////////////////////    
  def comments = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    def oUcommentSearch = new UcommentSearch()
    hsRes+=requestService.getParams([],['home_id','owner_id'])//'u_id',
    
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto	
    hsRes.owneruser = User.get(hsRes.inrequest?.owner_id)
    
    hsRes.count=0    
    hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.comments.listing.max,3)
    hsRes.paging_set=Tools.getIntVal(ConfigurationHolder.config.mobile.paging_set,5)
	
    hsRes+=oUcommentSearch.csiSelectUcommentsForHome(hsRes.inrequest?.home_id,hsRes.max,requestService.getOffset()-1)
    hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.count,hsRes.max,hsRes.paging_set)  
    hsRes.answerComments=hsRes.records.collect {Ucomment.findAll('FROM Ucomment WHERE refcomment_id=:rc_id ORDER BY id DESC',[rc_id:it.id])}
    hsRes.home_clients=hsRes.records.collect {Home.get(it?.home_id).client_id}
    hsRes.home = Home.get(hsRes.inrequest.home_id)

    render(template: "comments", model: hsRes)
    return
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  def addcomment={    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    hsRes+=requestService.getParams([],['home_id'],['comtext'])
    hsRes.inrequest.rating = requestService.getIntDef('rating',0)
    def hsOut=[:]
    if(!hsRes.user ||!(hsRes.inrequest?.comtext?:'')){
      hsOut.error=1
      render hsOut as JSON
      return
    }
    hsRes.home = Home.get(hsRes.inrequest?.home_id?:0)
    if(hsRes.home){
      if (hsRes.user.client_id == hsRes.home.client_id) {
        hsOut.error=1
        render hsOut as JSON
        return
      }
      def stringlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
      if (hsRes.inrequest?.comtext.size()>stringlimit)
        hsRes.inrequest?.comtext = hsRes.inrequest?.comtext.substring(0, stringlimit)
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
      mailerService.addcomment(hsRes.home,hsRes.context)
    }
    hsOut.error=0
    render hsOut as JSON
    return	    
  }
  /////////////////////////////////////////////////////////////////////////////////////  
  def commentDelete = {
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
	  def hsOut=[error:0]
    render "${params.jsoncallback}(${hsRes as JSON})"
    return    
  }
  /////////////////////////////////////////////////////////////////////////////////////  
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
        def stringlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
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
    render "${params.jsoncallback}(${hsRes as JSON})"
    return
  }
  ///////////////////////////////////////////////////////////////////////////////////////
  def calculatePrice = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    def lId=requestService.getLongDef('home_id',0)

    hsRes+=requestService.getParams(['homeperson_id'],null,['date_start','date_end'])

    hsRes.result = 0
    if ((hsRes.inrequest?:[]).size()!=3) {
      hsRes.error = false
      hsRes.result = null
      render hsRes as JSON
      return
    }
    //Alex>>
    hsRes += Home.calculateHomePrice(hsRes,lId)
    if (hsRes.errorprop)
      hsRes.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)

    render hsRes as JSON
    return
  }

  //messages>>
  def isMessAllowed={
    requestService.init(this)
    def hsOut=[error:0]
    def hsRes=requestService.getContextAndDictionary()
    if (!hsRes.user){
      hsOut.error=3
      render hsOut as JSON
      return
    }
    def lId = requestService.getLongDef('id',0)
    hsRes.owner = User.get(lId)
    if (!hsRes.owner){      
      hsOut.error=3
      render hsOut as JSON
      return
    }
    hsRes.user = User.get(hsRes.user.id)
    if (hsRes.owner.is_telaprove && !hsRes.user.is_telcheck){
      hsOut.error=1
      render hsOut as JSON
      return
    }
    if (hsRes.owner.is_clientphoto && !hsRes.user.picture){
      hsOut.error=2
      render hsOut as JSON
      return
    }  
    render hsOut as JSON
    return
  }
 //////////////////////////////////////////////////////////////////////////////////////////////////
  def addmbox={
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,false,true) 
    hsRes+=requestService.getParams(['homeperson_id','answertype_id','modstatus','is_answer'],['id'],['mtext','date_start','date_end',[]])
    hsRes.hInrequest=[:]
    hsRes.hInrequest.mbox_error=[]
    if(hsRes.user?.id && hsRes.inrequest.id){
      if(hsRes.inrequest?.date_start?:'')	  
        hsRes.inrequest.date_start1=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)  
      if(hsRes.inrequest?.date_end?:'')
        hsRes.inrequest.date_end1=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)	  
        
      if(!hsRes.inrequest.date_start1)	  
        hsRes.hInrequest.mbox_error<<1
      if(!hsRes.inrequest.date_end1)	  
        hsRes.hInrequest.mbox_error<<2		
      if(!hsRes.inrequest?.mtext?:'')
        hsRes.hInrequest.mbox_error<<4
		
      if(!hsRes.hInrequest.mbox_error){        	
        hsRes.result = 0
        hsRes+=Home.calculateHomePrice(hsRes,hsRes.inrequest.id,false)//toDO get price and valuta from client???
		
        if(!hsRes.error){
          def oTmpMbox=Mbox.findAll("FROM Mbox WHERE user_id=:user_id AND home_id=:home_id AND modstatus in (:ids)",[user_id:hsRes.user.id, home_id:hsRes.inrequest.id, ids:(Mboxmodstatus.findAllByIs_open(1)?.collect{it.modstatus}?:[])])[0]
          def oMbox
          if(!oTmpMbox){
            oMbox=new Mbox()
            oMbox.inputdate=new Date()
          }else
            oMbox=oTmpMbox

          def stringlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
          if (hsRes.inrequest?.mtext.size()>stringlimit) hsRes.inrequest?.mtext = hsRes.inrequest?.mtext.substring(0, stringlimit)
          if (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false) {
            hsRes.inrequest.mtext = (hsRes.inrequest.mtext?:'').replaceAll('[0-9( )-]{6,}','[Номер] ').replaceAll('\\S+@\\S*','[email]').replaceAll('\\S*@\\S+','[email]').trim()
          }

          def oValutarate = new Valutarate()
          def valutaRates = oValutarate.csiGetRate(Home.get(hsRes.inrequest.id).valuta_id?:857)
	    
          oMbox.price_rub=hsRes.result
          oMbox.price=(hsRes.result/valutaRates).toLong()          
          oMbox.valuta_id=Home.get(hsRes.inrequest.id).valuta_id?:857
          oMbox.user_id=hsRes.user.id
          oMbox.moddate=new Date()	    
          oMbox.date_start=hsRes.inrequest.date_start1
          oMbox.date_end=hsRes.inrequest.date_end1
          oMbox.homeperson_id=hsRes.inrequest.homeperson_id
          oMbox.mtext=hsRes.inrequest.mtext
          oMbox.mtextowner = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?hsRes.inrequest.mtext:''
          oMbox.is_telperm=1
          oMbox.home_id=hsRes.inrequest.id
          oMbox.homeowner_cl_id=Home.get(hsRes.inrequest.id).client_id?:0
          oMbox.answertype_id=0
          oMbox.modstatus=1
          oMbox.is_answer=0	      
          oMbox.is_read=0
          oMbox.is_approved = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?1:0          
	  
          if (!oMbox.save(flush:true)){
            log.debug('error on save Mbox in MController:addmbox')
            oMbox.errors.each{log.debug(it)}
            hsRes.hInrequest.mbox_error<<5
          }	          		  		      
          if(!hsRes.hInrequest.mbox_error){
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
            oMboxrec.valuta_id=Home.get(hsRes.inrequest.id).valuta_id?:857
            oMboxrec.is_approved = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?1:0
		  
            if (!oMboxrec.save(flush:true)){
              log.debug('error on save Mboxrec in MController:addmbox')
              oMboxrec.errors.each{log.debug(it)}
              hsRes.hInrequest.mbox_error<<5
            }		  
          }
          if(!hsRes.hInrequest.mbox_error){ 
            mailerService.sendMboxFirstMails(oMbox,hsRes.user.id,hsRes.context,(Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false))
            if ((Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)&&Tools.getIntVal(ConfigurationHolder.config.noticeSMS.active,1)&&User.findByClient_id(Home.get(hsRes.inrequest.id)?.client_id?:0)?.is_noticeSMS) {
              try {
                smsService.sendNoticeSMS(User.findByClient_id(Home.get(hsRes.inrequest.id)?.client_id?:0),Home.get(hsRes.inrequest.id)?.region_id?:0)
              } catch(Exception e) {
                log.debug("Cannot sent sms \n"+e.toString()+'in home/addmbox')
              }
            }
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

              if(lsDevices){
                def lsDevices_ids=[]
                for(device in lsDevices)
                  lsDevices_ids<<device.device
                if(lsDevices_ids)
                  androidGcmService.sendMessage(sendGCM,lsDevices_ids,'message', grailsApplication.config.android.gcm.api.key ?: '')  //ConfigurationHolder??? 
              }
              //GCM<<
            }
          }
        }else{
          if (hsRes.errorprop)
            hsRes.hInrequest.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)
        }
      }
    } 
    
    hsRes.hInrequest.error=hsRes.error?1:0
    hsRes.hInrequest.result=hsRes.result    
    render hsRes.hInrequest as JSON    
  } 
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def favorites = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)    
    if (!checkUser(hsRes)) return
    
    hsRes.inrequest=[:]
    hsRes.count=0
    hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.search.listing.max,30)	      
    if(hsRes.wallet){      
      def oHomeSearch=new HomeSearch()	
      hsRes+=oHomeSearch.csiFindWallet(hsRes.wallet,hsRes.max,requestService.getOffset())	        
      hsRes.discounts=[]
      def i=0
      for(home in hsRes.records){
        if(home.isHaveDiscountAdv(home))
          hsRes.discounts[i]=home?.csiGetDiscountText()                   
        i++
      }
      if(hsRes.context.lang){      
        def lsRecords=[]
        for(record in hsRes.records){
          def oHomeSearchTmp=new HomeSearch()          
          record.properties.each { prop, val ->
            if(["metaClass","class"].find {it == prop}) return
            if(oHomeSearchTmp.hasProperty(prop)) oHomeSearchTmp[prop] = val
          }
          oHomeSearchTmp.id=record.id          
          oHomeSearchTmp.name=Tools.transliterate(oHomeSearchTmp.name,0)
          oHomeSearchTmp.shortaddress=Tools.transliterate(oHomeSearchTmp.shortaddress,0)
          oHomeSearchTmp.city=City.findByName(oHomeSearchTmp.city)?.name_en?:Tools.transliterate(oHomeSearchTmp.city,0)          
          lsRecords<<oHomeSearchTmp             
        }  
        hsRes.records=lsRecords
      }
      hsRes.urlphoto = ConfigurationHolder.config.urlphoto
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol      
    }    
    return hsRes  
  }
  ////////////////////////////////////////////////////////////////////////////////
  def inbox={  
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)    

    //hsRes.user = User.get(hsRes.user.id)
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
    hsRes+=requestService.getParams(['ord'],['client'])    
    
    hsRes.data=[records:[],count:0]    
    //if(hsRes.user){
      hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.inbox_listing.max,5)  	  
      hsRes.paging_set=Tools.getIntVal(ConfigurationHolder.config.mobile.inbox_listing.paging_set,5)	        	  	
     
      def oMbox=new MboxSearch()	  
      hsRes.data=oMbox.csiGetMbox(hsRes.user?.id?:0,hsRes.inrequest?.client,-1,hsRes.max,requestService.getOffset()-1,0,hsRes.int?.ord?:0) 
      hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.data.count,hsRes.max,hsRes.paging_set)
      
      hsRes.count=oMbox.csiGetMbox(hsRes.user?.id,hsRes.inrequest?.client?:0,-1,0,0).count
      hsRes.count_fav=oMbox.csiGetMbox(hsRes.user?.id,hsRes.inrequest?.client?:0,-2,0,0).count
      hsRes.count_del=oMbox.csiGetMbox(hsRes.user?.id,hsRes.inrequest?.client?:0,0,0,0).count

      hsRes.modstatus = Mboxmodstatus.findAllByModstatusGreaterThan(0)
      hsRes.status_count = []
      for(status in hsRes.modstatus)
        hsRes.status_count << oMbox.csiGetMbox(hsRes.user?.id?:0,hsRes.inrequest?.client?:0,status.modstatus,0,0).count
	  
      if(hsRes.context?.lang){
        def lsData=[]
        for(record in hsRes.data.records){
          def oMboxTmp=new MboxSearch()          
          record.properties.each { prop, val ->
            if(["metaClass","class"].find {it == prop}) return
            if(oMboxTmp.hasProperty(prop)) oMboxTmp[prop] = val
          }
          oMboxTmp.id=record.id
          oMboxTmp.client_nickname=Tools.transliterate(oMboxTmp.client_nickname,0)
          oMboxTmp.user_nickname=Tools.transliterate(oMboxTmp.user_nickname,0)
          oMboxTmp.home_name=Tools.transliterate(oMboxTmp.home_name,0)
          oMboxTmp.home_address=Tools.transliterate(oMboxTmp.home_address,0)
          oMboxTmp.home_city=Tools.transliterate(oMboxTmp.home_city,0)
          oMboxTmp.shortaddress=Tools.transliterate(oMboxTmp.shortaddress,0) 

          lsData<<oMboxTmp          
        }
        hsRes.data.records=lsData
      }
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      hsRes.home_clients=hsRes.data.records.collect {Home.get(it?.home_id).client_id}
    //}    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////
  def inbox_list={    
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)	
    if (!checkUser(hsRes)) return   	
    hsRes+=init(hsRes)   
    
    hsRes.user = User.get(hsRes.user.id)
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto         
    hsRes+=requestService.getParams(['modstatus','ord'],['client'])
    hsRes.data=[records:[],count:0]    
    if(hsRes.user){
      hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.inbox_listing.max,5)  	  
      hsRes.paging_set=Tools.getIntVal(ConfigurationHolder.config.mobile.inbox_listing.paging_set,5)	        	  	
     
      def oMbox=new MboxSearch()	  
      hsRes.data=oMbox.csiGetMbox(hsRes.user?.id?:0,hsRes.inrequest?.client,hsRes.int?.modstatus,hsRes.max,requestService.getOffset()-1,0,hsRes.int?.ord?:0)       
      hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.data.count,hsRes.max,hsRes.paging_set)
	  
      if(hsRes.context?.lang){
        def lsData=[]
        for(record in hsRes.data.records){
          def oMboxTmp=new MboxSearch()          
          record.properties.each { prop, val ->
            if(["metaClass","class"].find {it == prop}) return
            if(oMboxTmp.hasProperty(prop)) oMboxTmp[prop] = val
          }
          oMboxTmp.id=record.id
          oMboxTmp.client_nickname=Tools.transliterate(oMboxTmp.client_nickname,0)
          oMboxTmp.user_nickname=Tools.transliterate(oMboxTmp.user_nickname,0)
          oMboxTmp.home_name=Tools.transliterate(oMboxTmp.home_name,0)
          oMboxTmp.home_address=Tools.transliterate(oMboxTmp.home_address,0)
          oMboxTmp.home_city=Tools.transliterate(oMboxTmp.home_city,0)
          oMboxTmp.shortaddress=Tools.transliterate(oMboxTmp.shortaddress,0) 

          lsData<<oMboxTmp          
        }
        hsRes.data.records=lsData
      }
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      hsRes.home_clients=hsRes.data.records.collect {Home.get(it?.home_id).client_id}
    }    
    render(template: "inbox_list", model: hsRes)
  }
  ////////////////////////////////////////////////////////////////////////////////
  def hidembox = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)    
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)  
    def lId = requestService.getLongDef('id',0)
    def iAction=requestService.getLongDef('act',0)
    hsRes.message = Mbox.get(lId)
    if (!checkAccess(hsRes)) return
    hsRes.message.modstatus=(iAction==1)?-100:1
    if(!hsRes.message.save(flush:true)) {
      log.debug(" Error on save Mbox:")
      hsRes.message.errors.each{log.debug(it)}
    }
    def hsOut=[error:false]
    render hsOut as JSON
    return    
  }
  ////////////////////////////////////////////////////////////////////////////////
  def selectmbox = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)    
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)  
    
    def lId = requestService.getLongDef('id',0)
    def iOwner=requestService.getLongDef('owner',0)
    def iAction=requestService.getLongDef('act',0)    
    hsRes.message = Mbox.get(lId)
    if (!checkAccess(hsRes)) return
    
    if(iOwner==1)
      hsRes.message.is_owfav=(iAction==1)?1:0
    else
      hsRes.message.is_clfav=(iAction==1)?1:0      
    if(!hsRes.message.save(flush:true)) {
      log.debug(" Error on save Mbox:")
      oUser.errors.each{log.debug(it)}
    }
    def hsOut=[error:false]
    render hsOut as JSON
    return        
  } 
//////////////////////////////////////////////////////////////////////////////// 
  def mbox={   
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.sender=hsRes.recipient=[]    
    
    hsRes.user = User.get(hsRes.user.id)
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto          
    
    def lId=requestService.getLongDef('id',0)
    hsRes.mbox = Mbox.get(lId)
    if (!checkAccess(hsRes)) return
    
    if(hsRes.mbox?.date_end && hsRes.mbox?.date_start)      
      use(groovy.time.TimeCategory) {
        def duration = hsRes.mbox?.date_end - hsRes.mbox?.date_start
        hsRes.days_between = duration.days			
      }		          
    
    if(hsRes.mbox && hsRes.user){
      if(hsRes.mbox.user_id==hsRes.user.id || hsRes.mbox.user_id==User.findByRef_id(hsRes.user.id)?.id?:0) 
        hsRes.mbox.is_readclient = 1
      else if(hsRes.client_id==hsRes.mbox.homeowner_cl_id) 
        hsRes.mbox.is_readowner = 1
      if(hsRes.mbox.is_answer){
        if(hsRes.mbox.user_id==hsRes.user.id || hsRes.mbox.user_id==User.findByRef_id(hsRes.user.id)?.id?:0)
          hsRes.mbox.is_read=1
      }else if(hsRes.client_id==Home.get(hsRes.mbox.home_id?:0)?.client_id?:0)
        hsRes.mbox.is_read=1		
      if (!hsRes.mbox.save(flush:true)){
        log.debug('error on save Mbox in MController:addmbox')
        hsRes.mbox.errors.each{log.debug(it)}		    
      }     
    
      def lClientId = Home.get(hsRes.mbox.home_id).client_id
      hsRes.sender = User.findWhere(client_id:lClientId)
      hsRes.ownerClient = Client.get(lClientId)
      hsRes.recipient = User.read(User.get(hsRes.mbox?.user_id)?.ref_id?:hsRes.mbox?.user_id)
      hsRes.home = Home.read(hsRes.mbox.home_id)
      if(hsRes.context?.lang){        
        hsRes.home=hsRes.home.csiSetEnHome()                                                               
        hsRes.recipient = hsRes.recipient.csiSetEnUser()               
        hsRes.sender=hsRes.sender.csiSetEnUser()   
      }       
      hsRes.data=[records:[],count:0]	  
      hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.msg_listing.max,5)  	  
      hsRes.paging_set=Tools.getIntVal(ConfigurationHolder.config.mobile.msg_listing.paging_set,5)
	  
      def oMbox=new MboxrecSearch()	  
      hsRes.data=oMbox.csiGetList(hsRes.mbox?.id,hsRes.mbox?.user_id,hsRes.sender?.id,hsRes.user.id==hsRes.mbox.user_id?0:1,hsRes.max,requestService.getOffset()-1)      	  
	  	
      hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.data.count,hsRes.max,hsRes.paging_set)
	  
      hsRes.userHomes=Home.findAllByClient_idAndModstatus(lClientId,1,[sort:'name'])      
      hsRes.answergroup=Answergroup.list()
      hsRes.answertype=Answertype.list()
      hsRes.isHideContact=Tools.getIntVal(ConfigurationHolder.config.mbox.hideContactMode,1)?true:false
      hsRes.payway=Payway.findAllByModstatusAndIs_invoice(1,0)
      hsRes.iscanoffer=Mboxrec.findAllByMbox_idAndIs_answerAndAnswertype_idInList(hsRes.mbox?.id?:0,1,[1,2])?false:true
      hsRes.iscandecline=Mboxrec.findAllByMbox_idAndIs_answerAndAnswertype_idGreaterThanAndAdmin_id(hsRes.mbox?.id?:0,1,0,0)?false:true
      
      //hsRes.home = Home.get(hsRes.mbox.home_id)
      hsRes.alikeWhere = hsRes.home.csiGetWhere()  
      hsRes.home_names=hsRes.data.records.collect {Home.get(it?.rechome_id).name}	
      
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)     
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      hsRes.cpecofferRates = oValutarate.csiGetRate(hsRes.home?.valuta_id?:857)      
      hsRes.ispaypossible = (Client.get(hsRes.mbox.homeowner_cl_id)?.resstatus==1)
      hsRes.resstatModifier = 1.0
      if (hsRes.ispaypossible)
        hsRes.resstatModifier = hsRes.resstatModifier + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100)
      
      hsRes.displayPrice = Math.round(hsRes.mbox?.price_rub * hsRes.resstatModifier / hsRes.valutaRates)      
      hsRes.reserve = Reserve.get(hsRes.ownerClient?.reserve_id?:0)
      if(hsRes.ispaypossible && hsRes.reserve)
        hsRes.totalPrice = Math.round(billingService.getBronPrice(hsRes.mbox) / hsRes.valutaRates)        
   	} else {
      redirect(uri:'',base:hsRes.context.mobileURL_lang)
      return
    }  
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////   
  def mbox_list={   
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.sender=hsRes.recipient=[]    
    
    hsRes.user = User.get(hsRes.user.id)
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto          
    
    def lId=requestService.getLongDef('id',0)
    hsRes.mbox = Mbox.get(lId)
    if (!checkAccess(hsRes)) return
    
    if(hsRes.mbox?.date_end && hsRes.mbox?.date_start)      
      use(groovy.time.TimeCategory) {
        def duration = hsRes.mbox?.date_end - hsRes.mbox?.date_start
        hsRes.days_between = duration.days			
      }		          
    
    if(hsRes.mbox && hsRes.user){  
      if(hsRes.mbox.is_answer){
   	    if(hsRes.mbox.user_id==hsRes.user.id)
          hsRes.mbox.is_read=1
      }else if(hsRes.client_id==Home.get(hsRes.mbox.home_id?:0)?.client_id?:0)
        hsRes.mbox.is_read=1		
      
      if (!hsRes.mbox.save(flush:true)){
        log.debug('error on save Mbox in MController:addmbox')
        hsRes.mbox.errors.each{log.debug(it)}		    
      }
    
      def lClientId = Home.get(hsRes.mbox.home_id).client_id
      hsRes.sender = User.findWhere(client_id:lClientId)
      hsRes.recipient = User.get(hsRes.mbox?.user_id)	       

      hsRes.data=[records:[],count:0]
	  
      hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.msg_listing.max,5)  	  
      hsRes.paging_set=Tools.getIntVal(ConfigurationHolder.config.mobile.msg_listing.paging_set,5)
	  
      def oMbox=new MboxrecSearch()	  
      hsRes.data=oMbox.csiGetList(hsRes.mbox?.id,hsRes.mbox?.user_id,hsRes.sender?.id,hsRes.user.id==hsRes.mbox.user_id?0:1,hsRes.max,requestService.getOffset()-1)      	  
	  	
      hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.data.count,hsRes.max,hsRes.paging_set)
	  
      hsRes.userHomes=Home.findAllByClient_idAndModstatus(lClientId,1,[sort:'name'])      
      hsRes.minday=Rule_minday.list()
      hsRes.maxday=Rule_maxday.list()
      hsRes.homeperson=Homeperson.list()
      hsRes.rule_minday=Rule_minday.list()
      hsRes.rule_maxday=Rule_maxday.list()
      hsRes.answergroup=Answergroup.list()
      hsRes.answertype=Answertype.list()
      
      hsRes.home = Home.get(hsRes.mbox.home_id)
      hsRes.alikeWhere = hsRes.home.csiGetWhere()  

      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
	  
      hsRes.home_names=hsRes.data.records.collect {Home.get(it?.rechome_id).name}	
    }	
    render(template: "mbox_list", model: hsRes)    
  }
  
////////////////////////////////////////////////////////////////////////////////  
  def addanswer={  
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return	
    hsRes+=init(hsRes)							    
    hsRes+=requestService.getParams(['homeperson_id','rule_minday_id','rule_maxday_id','answertype_id','pricing_unit'],['id','price_spec','home_id','home_id_spec'],['date_start','date_end'],[])

    def oMbox=Mbox.get(hsRes.inrequest?.id)
    def ownerClient = Client.get(oMbox?.homeowner_cl_id?:0)
    def iscanoffer = Mboxrec.findAllByMbox_idAndIs_answerAndAnswertype_idInList(oMbox?.id?:0,1,[1,2])?false:true
    if (!oMbox||oMbox?.modstatus<0||((oMbox.modstatus==3||!iscanoffer)&&hsRes.inrequest.answertype_id in [1,2])||(!(oMbox.modstatus in 3..4)&&iscanoffer&&hsRes.inrequest.answertype_id==6)||!ownerClient) {
      //action not occurs when no Mbox, mbox was deleted, or adding new special offer when previous is actually, or adding info request before special offer was made
      render(contentType:"application/json"){[error:true]}
      return
    }
    //validation>>
    def date_start1
    def date_end1

    if(hsRes.inrequest?.date_start?:'')
      date_start1=Date.parse('yyyy-MM-dd', hsRes.inrequest?.date_start)
    if(hsRes.inrequest?.date_end?:'')
      date_end1=Date.parse('yyyy-MM-dd', hsRes.inrequest?.date_end)
    if(date_start1 && date_start1>=date_end1) {
      hsRes.error = true
      hsRes.errorprop = "home.calculateHomePrice.badDate_end.errorprop"
    }
    if(date_start1){
      def dateStart=new Date()
      def date1= new GregorianCalendar()
      date1.setTime(dateStart)
      date1.set(Calendar.HOUR_OF_DAY ,0)
      date1.set(Calendar.MINUTE ,0)
      date1.set(Calendar.SECOND,0)
      date1.set(Calendar.MILLISECOND,0)
      if(date_start1<date1.getTime()) {
        hsRes.error = true
        hsRes.errorprop = "home.calculateHomePrice.badDate_start.errorprop"
      }
    }
    def lsMessage=requestService.getStr('message').split(',')
    for(message in lsMessage)
      if(message)
        hsRes.inrequest.message=message
    if(!hsRes.inrequest.message&&!(hsRes.inrequest.answertype_id in 1..2)){
      hsRes.error = true
      hsRes.errorprop = "mobile.addanswer.blank.message.errorprop"
    }
    if (!hsRes.error&&hsRes.inrequest.answertype_id==2) {
      hsRes.result = 0
      hsRes+=Home.calculateHomePrice(hsRes,hsRes.inrequest.home_id_spec,false)
    }
    def hsOut=[:]
    if (hsRes.errorprop){
      hsOut.error=hsRes.error
      hsOut.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)
      render hsOut as JSON
      return
	  }
    //validation<<

    if (hsRes.inrequest.message&&(Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)) {
      if (hsRes.inrequest.message.replace('(','').replace(')','').replace('-','').replaceAll("\\s","").matches('.*[0-9]{7,}.*')||hsRes.inrequest.message.matches('.*\\S+@\\S*.*')||hsRes.inrequest.message.matches('.*\\S*@\\S+.*')) {
        hsRes.inrequest.message = (hsRes.inrequest.message?:'').replaceAll('[0-9( )-]{7,}',' [номер] ').replaceAll('\\S+@\\S*','[email]').replaceAll('\\S*@\\S+','[email]').trim()
      }
    }
    hsRes.textlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
    if ((hsRes.inrequest?.message?:'').size()>hsRes.textlimit)
      hsRes.inrequest?.message = hsRes.inrequest?.message.substring(0, hsRes.textlimit)

    Long lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id?:0

    if(oMbox.user_id==hsRes.user.id || oMbox.user_id==lSocId){
      oMbox.mtext = hsRes.inrequest.message?:''
      if (oMbox.modstatus==4){
        oMbox.is_answer = 0
        oMbox.is_read = 0
        oMbox.answertype_id = 0
      }
    }else if(hsRes.client_id==oMbox.homeowner_cl_id){
      oMbox.mtextowner = hsRes.inrequest.message?:(hsRes.inrequest.answertype_id in 1..2)?'Можно бронировать':''
      if (oMbox.modstatus==4){
        oMbox.is_answer = 1
        oMbox.answertype_id = hsRes.inrequest.answertype_id
        oMbox.is_read = 0
      }
      oMbox.controlstatus = (oMbox.controlstatus>0)?oMbox.controlstatus:((hsRes.inrequest.answertype_id in [3,4,5])?2:1)
      if (oMbox.inputdate>new Date(112,11,05))//release date this feature. Mboxes older this date have not an adequate statistic.
        oMbox.responsetime?:(oMbox.responsetime=(new Date().getTime()-oMbox.inputdate.getTime())/1000)
    }else
      flash.error = 1

    oMbox.home_id = hsRes.inrequest.home_id?:oMbox.home_id
    oMbox.is_adminread = 0
    oMbox.lastusermessagedate = new Date()

    if(hsRes.inrequest.answertype_id==2){
      def oValutarate = new Valutarate()
      def valutaRates = oValutarate.csiGetRate(Home.get(hsRes.inrequest.home_id_spec)?.valuta_id?:857)

      oMbox.date_start = date_start1
      oMbox.date_end = date_end1
      oMbox.homeperson_id = hsRes.inrequest.homeperson_id
      oMbox.home_id = hsRes.inrequest.home_id_spec
      oMbox.price_rub = hsRes.inrequest.pricing_unit?hsRes.inrequest.price_spec*oMbox.getTripLength():hsRes.inrequest.price_spec?:0
      oMbox.price = (oMbox.price_rub/valutaRates).toLong()
      oMbox.valuta_id = Home.get(hsRes.inrequest.home_id_spec)?.valuta_id?:857
    }
    if(hsRes.inrequest.answertype_id==3){
      def start
      def end
      if(hsRes.inrequest?.date_start?:'')
        start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
      if(hsRes.inrequest?.date_end?:'')
        end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
      def hmp = new Homeprop()
      hmp.addHomepropUnavailability(hsRes.inrequest.home_id?:oMbox.home_id,start,end)
    }

    if(!flash.error)
      if(!oMbox.save(flush:true)){
        log.debug('error on save Mbox in MController:addanswer')
        oMbox.errors.each{log.debug(it)}
        flash.error=2
      }
    def oMboxrec=new Mboxrec()
    if(!flash.error){
      oMboxrec.mbox_id = oMbox.id
      if(oMbox.user_id==hsRes.user.id|| oMbox.user_id==lSocId){
        oMboxrec.is_answer=0
        oMboxrec.answertype_id=0
      } else if(hsRes.client_id==oMbox.homeowner_cl_id){
        oMboxrec.is_answer=1
        oMboxrec.answertype_id=hsRes.inrequest?.answertype_id?:6
      }
      oMboxrec.rectext = hsRes.inrequest.message?:(oMboxrec.answertype_id in 1..2)?'Можно бронировать':''
      oMboxrec.inputdate=new Date()
      oMboxrec.is_approved = oMbox.modstatus==4?1:0
      oMboxrec.home_id=hsRes.inrequest.home_id

      oMboxrec.price = oMbox.price
      oMboxrec.price_rub = oMbox.price_rub
      oMboxrec.valuta_id = oMbox.valuta_id
      oMboxrec.date_start = oMbox.date_start
      oMboxrec.date_end = oMbox.date_end
      oMboxrec.homeperson_id = oMbox.homeperson_id
      oMboxrec.home_id = oMbox.home_id

      if(hsRes.inrequest.answertype_id==4){
        oMboxrec.rule_minday_id = hsRes.inrequest.rule_minday_id
        oMboxrec.rule_maxday_id = hsRes.inrequest.rule_maxday_id
      }
      if (!oMboxrec.save(flush:true)){
        log.debug('error on save Mboxrec in MController:addmbox')
        oMboxrec.errors.each{log.debug(it)}
        flash.error=2
      }
    }

    if (oMbox?.modstatus==4&&!flash.error){
      mailerService.addanswermail(oMbox,oMboxrec,hsRes.context)
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
/////////////////////////////////////////////////////////////////////////////////////  
  def bron={//for bron info page    
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true) 
    if (!checkUser(hsRes)) return
    //hsRes.urlphoto = ConfigurationHolder.config.urlphoto         
    hsRes+=init(hsRes) 
    def lMboxId=requestService.getLongDef('mbox_id',0)
    def lId=requestService.getLongDef('id',0)	
    Long lSocId = 0   
    
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
    
    hsRes.mbox=Mbox.findByIdAndUser_idInList(lMboxId,[lSocId,hsRes.user.id])
	  hsRes.mboxRec=Mboxrec.findWhere(id:lId,mbox_id:hsRes.mbox?.id)
    
	  if(hsRes.mboxRec){
      hsRes.homeperson=Homeperson.get(hsRes.mboxRec?.homeperson_id)
      hsRes.home=Home.get(hsRes.mboxRec?.home_id)      
      hsRes.ownerClient = Client.get(hsRes.home?.client_id)
      hsRes.own = User.findWhere(client_id:hsRes.home?.client_id)
      
      hsRes.date_start=String.format('%td/%<tm/%<tY',hsRes.mboxRec?.date_start)
      hsRes.date_end=String.format('%td/%<tm/%<tY',hsRes.mboxRec?.date_end)
      hsRes.moddate=String.format('%td/%<tm/%<tY %<tH:%<tM',hsRes.mbox?.moddate)
      hsRes.cancellation=Rule_cancellation.get(hsRes.home?.rule_cancellation_id)
      hsRes.timein=Rule_timein.get(hsRes.home?.rule_timein_id?:0).name
      hsRes.timeout=Rule_timeout.get(hsRes.home?.rule_timeout_id?:0).name
      
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      hsRes.ispaypossible = (hsRes.ownerClient?.resstatus==1)
      hsRes.resstatModifier = 1.0
      if (hsRes.ispaypossible)
        hsRes.resstatModifier = hsRes.resstatModifier + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100)
      hsRes.displayPrice = Math.round(hsRes.mboxRec?.price_rub * hsRes.resstatModifier / hsRes.valutaRates)     
      hsRes.reserve = Reserve.get(hsRes.ownerClient?.reserve_id?:0)
      if(hsRes.ispaypossible && hsRes.reserve)
        hsRes.totalPrice = Math.round(billingService.getBronPrice(hsRes.mbox) / hsRes.valutaRates)
      hsRes.payway=Payway.findAllByModstatusAndIs_invoice(1,0)        
    } 
    return hsRes
  }
/////////////////////////////////////////////////////////////////////////////////////
  def setMboxBron={
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes) 
    def lId=requestService.getLongDef('id',0)
    def lMboxrecId=requestService.getLongDef('mboxrec_id',0)
    def iClose=requestService.getLongDef('close',0)
    hsRes+=requestService.getParams(['payway','oferta'])

    Long lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id?:0

    def oMbox=Mbox.findByIdAndUser_idInList(lId,[lSocId,hsRes.user.id])
    def oMboxrec1=Mboxrec.findWhere(id:lMboxrecId,mbox_id:oMbox?.id)
    if(oMbox&&oMboxrec1){
      hsRes.ispaypossible = (Client.get(oMbox.homeowner_cl_id)?.resstatus==1)
      if(Homeprop.findAll("FROM Homeprop WHERE (date_start<:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND (modstatus=3 OR modstatus=5) ORDER BY date_start",
          [date_start_query:oMbox.date_start,date_end_query:oMbox.date_end,home_id:oMbox.home_id])){
        hsRes.error = 3
      }
      if (!hsRes.ispaypossible) {
        if(!hsRes.error){
          try {
            billingService.createTripFromMbox(oMbox,oMboxrec1,hsRes.context,hsRes.user.id,iClose)
            hsRes.where = ''
          } catch(Exception e) {
            log.debug("InboxController:setMboxBron:\n"+e.toString())
            hsRes.error = 1
          }
        }
      } else {
        if (!hsRes.inrequest?.payway) {
          hsRes.error = 6
        }
        if (!hsRes.inrequest?.oferta) {
          hsRes.error = 2
        }
        def oPayway = Payway.get(hsRes.inrequest.payway)
        if (oPayway?.is_invoice&&(oMbox.date_start - new Date() < Tools.getIntVal(ConfigurationHolder.config.payorder.invoicelife.days,5))) {
          hsRes.error = 4
        }
        /*if (oPayway?.is_invoice&&(Client.get(oMbox.homeowner_cl_id)?.reserve_id in 3..4)) {
          hsRes.error = 7
        }*/
        if (billingService.getBronPrice(oMbox)>(oPayway?.limit?:Long.MAX_VALUE)) {
          hsRes.error = 5
        }
        if(!hsRes.error){
          try {
            if (oPayway?.is_invoice) {
              hsRes.where = createLink(controller:'m',action: 'paydocuments',id:(billingService.createOrderFromMbox(oMbox,hsRes.inrequest+[userip:request.remoteAddr])?.norder?:''),base:hsRes.context?.mobileURL_lang)
            } else if(oPayway.agr_id==3) {
              hsRes.where = createLink(controller:'m',action: 'liqpay',id:(billingService.createOrderFromMbox(oMbox,hsRes.inrequest+[userip:request.remoteAddr])?.norder?:''),base:hsRes.context?.mobileURL_lang)
            } else if(oPayway.agr_id==4) {
              hsRes.where = createLink(controller:'m',action: 'payU',id:(billingService.createOrderFromMbox(oMbox,hsRes.inrequest+[userip:request.remoteAddr])?.norder?:''),base:hsRes.context?.mobileURL_lang)
            } else if(oPayway.agr_id==5) {
              hsRes.where = createLink(controller:'m',action: 'wmoney',id:(billingService.createOrderFromMbox(oMbox,hsRes.inrequest+[userip:request.remoteAddr])?.norder?:''),base:hsRes.context?.mobileURL_lang)
            } else {
              billingService.createOrderFromMbox(oMbox,hsRes.inrequest)
              hsRes.where = createLink(controller:'m',action: 'index',absolute:true)
            }
            if (iClose) {
              billingService.closeAnotherMboxes(oMbox,hsRes.user.id?:0)
            }
          } catch(Exception e) {
            hsRes.error = 1
          }
        }
      }
      if(hsRes.error)
        render(contentType:"application/json"){[error:(hsRes.error)]}
      else
        render(contentType:"application/json"){[error:false,where:hsRes.where]}
    }else
      render(contentType:"application/json"){[error:true]}
  }
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////agr pages///////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////

  def liqpay = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return    

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,hsRes.inrequest?.id?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.configParams = [
      password:ConfigurationHolder.config.liqpay.password?ConfigurationHolder.config.liqpay.password.trim():'i35361y272ywp5l',
      merid:ConfigurationHolder.config.liqpay.merid?ConfigurationHolder.config.liqpay.merid.trim():'I0MO0A98',
      acqid:ConfigurationHolder.config.liqpay.acqid?ConfigurationHolder.config.liqpay.acqid.trim():'469584'
    ]
    hsRes.accdata = Accountdata.get(1)
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.orderdescription = hsRes.accdata.paycomment.replace('[@NORDER]',hsRes.payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_end))

    hsRes.purchaseamt = hsRes.context.is_dev?'000000000100':Tools.generatePriceForliqpay(hsRes.payorder.summa)
    hsRes.signature = (hsRes.configParams.password+hsRes.configParams.merid+hsRes.configParams.acqid+hsRes.payorder.norder+hsRes.purchaseamt+'643'+hsRes.orderdescription).encodeAsSHA1Bytes().encodeAsBase64()
    return hsRes
  }

  def liqpayresult = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    hsRes+=requestService.getParams(['responsecode'],null,['reasoncode','orderid','signature','reasoncodedesc'])
    hsRes.configParams = [
      password:ConfigurationHolder.config.liqpay.password?ConfigurationHolder.config.liqpay.password.trim():'i35361y272ywp5l',
      merid:ConfigurationHolder.config.liqpay.merid?ConfigurationHolder.config.liqpay.merid.trim():'I0MO0A98',
      acqid:ConfigurationHolder.config.liqpay.acqid?ConfigurationHolder.config.liqpay.acqid.trim():'469584'
    ]
    hsRes.signature = (hsRes.configParams.password+hsRes.configParams.merid+hsRes.configParams.acqid+hsRes.inrequest.orderid+hsRes.inrequest.responsecode+hsRes.inrequest.reasoncode+hsRes.inrequest.reasoncodedesc).encodeAsSHA1Bytes().encodeAsBase64()
    hsRes.payorder = Payorder.findByNorder(hsRes.inrequest?.orderid?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    try {
      if(hsRes.inrequest.signature==hsRes.signature&&hsRes.inrequest.responsecode==1&&hsRes.inrequest.reasoncode=='1') {
        billingService.doTransaction( billingService.createPaytrans(hsRes.payorder,hsRes.inrequest.reasoncodedesc,2) )
        hsRes.is_succes = 1
      } else if(hsRes.inrequest.signature==hsRes.signature){
        billingService.createPaytrans([payorder:hsRes.payorder,retcode:hsRes.inrequest.reasoncode],hsRes.inrequest.reasoncodedesc,20)
        hsRes.errortext = Payerrorcode.findByAgr_idAndReasoncode(3,hsRes.inrequest.reasoncode?:'36')?.clienttext
        hsRes.is_succes = 0
      } else {
        hsRes.is_succes = -1
      }
    } catch(Exception e) {
      log.error("AccountController:liqpayresult:\n"+e.toString())
      hsRes.is_succes = 2
    }
    hsRes.curinfotext = Infotext.findByControllerAndAction('account','liqpayresult')

    return hsRes
  }

  def payU = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return    

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,hsRes.inrequest?.id?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.configParams = [
      secretKey:ConfigurationHolder.config.payu.secretKey?ConfigurationHolder.config.payu.secretKey.trim():'!F6[bz*5a6b2++Q3EA7@',
      merchant:ConfigurationHolder.config.payu.merchant?ConfigurationHolder.config.payu.merchant.trim():'staytodq'
    ]
    hsRes.accdata = Accountdata.get(1)
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.payway = Payway.get(hsRes.payorder.payway_id)
    hsRes.orderdescription = hsRes.accdata.paycomment.replace('[@NORDER]',hsRes.payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_end))
    hsRes.orderdate = String.format('%tF %<tT', new Date())
    def str = ''
    str += hsRes.configParams.merchant.getBytes("UTF-8").length+hsRes.configParams.merchant
    str += hsRes.payorder.norder.getBytes("UTF-8").length+hsRes.payorder.norder
    str += hsRes.orderdate.getBytes("UTF-8").length+hsRes.orderdate
    str += hsRes.orderdescription.getBytes("UTF-8").length+hsRes.orderdescription
    str += hsRes.payorder.norder.getBytes("UTF-8").length+hsRes.payorder.norder
    str += hsRes.payorder.summa.toString().getBytes("UTF-8").length+hsRes.payorder.summa.toString()
    str += '1'.getBytes("UTF-8").length+'1'
    str += '0'.getBytes("UTF-8").length+'0'
    str += '0'.getBytes("UTF-8").length+'0'
    str += 'RUB'.getBytes("UTF-8").length+'RUB'
    str += (hsRes.payway?.payumethod?:'CCVISAMC').getBytes("UTF-8").length+(hsRes.payway?.payumethod?:'CCVISAMC')
    str += 'NET'.getBytes("UTF-8").length+'NET'
    hsRes.signature = Tools.generateHmacMD5(str,hsRes.configParams.secretKey).encodeAsHex()

    return hsRes
  }

  def payUresult = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    hsRes+=requestService.getParams(null,null,['ctrl','payrefno'])
    hsRes.result = requestService.getIntDef('result',0)
    hsRes.configParams = [
      secretKey:ConfigurationHolder.config.payu.secretKey?ConfigurationHolder.config.payu.secretKey.trim():'!F6[bz*5a6b2++Q3EA7@',
      merchant:ConfigurationHolder.config.payu.merchant?ConfigurationHolder.config.payu.merchant.trim():'staytodq'
    ]
    def curUrl  = request.requestURL.toString()-'.dispatch'-'/grails'+'?'+request.getQueryString().toString()-('&ctrl='+hsRes.inrequest.ctrl)
    if(!(Tools.generateHmacMD5(curUrl.getBytes("UTF-8").length+curUrl,hsRes.configParams.secretKey).encodeAsHex()==hsRes.inrequest.ctrl)){
      response.sendError(404)
      return
    }
    hsRes.curinfotext = Infotext.findByControllerAndAction('account','payUresult')

    return hsRes
  }

  def wmoney = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,hsRes.inrequest?.id?:'')
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.configParams = [
      secretKey:ConfigurationHolder.config.wmoney.secretKey?ConfigurationHolder.config.wmoney.secretKey.trim():'8D38F03EE4415A6E0199AB6D66051F64',
      merchant:ConfigurationHolder.config.wmoney.merchant?ConfigurationHolder.config.wmoney.merchant.trim():'R189136258947',
      testmode:Tools.getIntVal(ConfigurationHolder.config.wmoney.testmode,1)
    ]
    hsRes.accdata = Accountdata.get(1)
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.purchaseamt = hsRes.configParams.testmode?1f:(hsRes.payorder.summa as float)
    hsRes.orderdescription = hsRes.accdata.paycomment.replace('[@NORDER]',hsRes.payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',hsRes.mbox?.date_end)).getBytes("UTF-8").encodeAsBase64()
    return hsRes
  }

  def wmfail = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    hsRes+=requestService.getParams(null,null,['LMI_PAYMENT_NO'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,(hsRes.inrequest?.'LMI_PAYMENT_NO'?'st'+hsRes.inrequest?.'LMI_PAYMENT_NO':''))
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.curinfotext = Infotext.findByControllerAndAction('account','wmfail')

    return hsRes
  }

  def wmsuccess = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    hsRes+=requestService.getParams(null,null,['LMI_PAYMENT_NO'])
    hsRes.payorder = Payorder.findByUser_idAndNorder(hsRes.user.id,(hsRes.inrequest?.'LMI_PAYMENT_NO'?'st'+hsRes.inrequest?.'LMI_PAYMENT_NO':''))
    if (!hsRes.payorder) {
      response.sendError(404)
      return
    }
    hsRes.mbox = Mbox.get(hsRes.payorder.mbox_id)
    hsRes.curinfotext = Infotext.findByControllerAndAction('account','wmsuccess')

    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////// end agr  /////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////
  def removeBron={//removeHomepropUnavailability
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes) 
    def lId=requestService.getLongDef('id',0)
    def oMbox=Mbox.get(lId?:0)
    if(oMbox && (hsRes.client_id == Home.get(oMbox.home_id)?.client_id)){
      def start
      if(oMbox.date_start?:'')
        start= new Date(oMbox.date_start.getTime())   
      def hmp = new Homeprop()
      hmp.removeHomepropUnavailability(oMbox.home_id,start,oMbox.id)
    }
    render ''
    return 
  }
 ////////////////////////////////////////////////////////////////////////////////
  def deletembox = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    def lId = requestService.getLongDef('id',0)
    hsRes.record=[:]

    if(lId){
      def oMboxrecDel=Mboxrec.get(lId)
      if(oMboxrecDel){
        def oMbox=Mbox.get(oMboxrecDel.mbox_id)
        if(oMbox?.modstatus<4){
          oMboxrecDel.delete(flush:true)
          hsRes.record = Mboxrec.findAllByMbox_idAndIs_approvedAndAdmin_id(oMbox.id,1,0,[sort:'inputdate',order:'desc'])[0]
          if(hsRes.record){
            oMbox.date_start = hsRes.record.date_start
            oMbox.date_end = hsRes.record.date_end
            oMbox.home_id = hsRes.record.home_id
            oMbox.homeperson_id = hsRes.record.homeperson_id
            oMbox.price = hsRes.record.price
            oMbox.valuta_id = hsRes.record.valuta_id
            oMbox.price_rub = hsRes.record.price_rub
            oMbox.answertype_id = hsRes.record.answertype_id
            oMbox.is_answer = hsRes.record.is_answer
            oMbox.is_read = 0
            oMbox.moddate = hsRes.record.inputdate
            oMbox.mtext = hsRes.record.rectext
            oMbox.mtextowner = ''
            oMbox.modstatus = 1
            oMbox.controlstatus = 0

            if(!oMbox.save(flush:true)){
              log.debug('error on save Mbox in MController:deletembox')
              oMbox.errors.each{log.debug(it)}
              flash.error=2
            }
          }
          Payorder.findAllByMbox_idAndModstatus(oMbox.id,0).each{
            it.cancelBronOffer()
          }
        }
      }
    }
    render(contentType:"application/json"){[error:false]}
  }
//<<messge
/////////////////////////////////////////////////////////////////////////////////////
  def wallet_search = { 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,false,true)
    if (!checkUser(hsRes)) return
    hsRes.inrequest=[:]
    hsRes.count=0
    hsRes.max=Tools.getIntVal(ConfigurationHolder.config.mobile.search.listing.max,30)
	  hsRes.paging_set=Tools.getIntVal(ConfigurationHolder.config.mobile.paging_set,5)
    if(hsRes.wallet){    	
      hsRes.modstatus=Homemodstatus.list()
      hsRes.hometype=Hometype.list([sort:'id',order:'asc']) 
      hsRes.homeperson=Homeperson.list()
      hsRes.homeclass=Homeclass.list()
      def oHomeSearch=new HomeSearch()	
      hsRes+=oHomeSearch.csiFindWallet(hsRes.wallet,hsRes.max,requestService.getOffset())	  
      
      hsRes.discounts=[]
      def i=0
      for(home in hsRes.records){
        if(home.isHaveDiscountAdv(home)){
          hsRes.discounts[i]=home?.csiGetDiscountText()           
        }
        i++
      }
      hsRes.urlphoto = ConfigurationHolder.config.urlphoto   
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol      	  
	  	
      hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.count,hsRes.max,hsRes.paging_set) 
    }
    render hsRes as JSON
    return
  }
/////////////////////////////////////////////////////////////////////////////////////  
  def toRad(iNumber){
    return iNumber * Math.PI / 180; 
  }
  def toDeg(iNumber){
    return iNumber * 180/Math.PI; 
  }
/////////////////////////////////////////////////////////////////////////////////////  
  def findCoordByCenterAndRadius(iX,iY,brng,dist){      //brng =corner; we use:45;-135 degrees
    //iX=iX/100000
    //iY=iY/100000
    dist = dist / 6371;  
    brng = toRad(brng); 

    def lat1 = toRad(iY), lon1 = toRad(iX);

    def lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + 
      Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
      
    def lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) *
                                      Math.cos(lat1), 
                                      Math.cos(dist) - Math.sin(lat1) *
                                      Math.sin(lat2));         

    return [toDeg(lon2)*100000,toDeg(lat2)*100000];//x,y    
  } 
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveuser={ 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)  
    if(hsRes.user!=null){      
      hsRes.inrequestAjax = [:]
      hsRes.inrequestAjax.error=1
      hsRes.inrequestAjax.errorprop=[102]
      render hsRes.inrequestAjax as JSON
      return
    }
    hsRes+=requestService.getParams([],['home_id'],['code','email','nickname','firstname','lastname','password1','password2'])	

    flash.error=[]
    def iRefId=0
	  def lId=0
    def needUser = true 

    if(hsRes.inrequest?.code)
      hsRes.inrequest?.email=Client.findWhere(code:hsRes.inrequest?.code)?.name?:''//email allways exists on staytoday provider	    
    if(!(hsRes.inrequest?.email?:''))
      flash.error<<1
    else if (!Tools.checkEmailString(hsRes.inrequest.email))
      flash.error<<2
    if(!(hsRes.inrequest?.nickname?:''))
      flash.error<<3	
    if((hsRes.inrequest?.password1?:'')!=(hsRes.inrequest?.password2?:''))
      flash.error<<4
    if((hsRes.inrequest?.password2?:'').size()<Tools.getIntVal(ConfigurationHolder.config.user.passwordlength?:5))
      flash.error<<5	
	
    if(!(flash.error?:[]).size()){  
      def oClient=null
      def iClientId=0
     //>>client	 
      if((hsRes.inrequest?.code?:'') && hsRes.inrequest?.home_id){	   
        oClient=Client.findWhere(code:hsRes.inrequest?.code)        		
        if(oClient){//>>client exist		  		  
          if(oClient.name!=hsRes.inrequest?.email){//>>another email		  
            if(User.findWhere(email:hsRes.inrequest.email?:'bad_email'))
              if(User.findWhere(email:hsRes.inrequest.email?:'bad_email').is_external)
                flash.error<<10
              else
                flash.error<<6
            else if(Client.findWhere(name:hsRes.inrequest.email)){
              def delClient = Client.findWhere(name:hsRes.inrequest.email)
              def lsHome=Home.findAllWhere(client_id:delClient.id)
              for(home in lsHome){
                def oHomeProp=Homeprop.findWhere(home_id:home.id)
                oHomeProp.delete(flush:true)
                home.delete(flush:true)
              }
              delClient.delete(flush:true)
            }				
          }//<<another email
 	  
          if(!(flash.error?:[]).size()){		  
            iClientId=oClient.id		
            oClient.modstatus=2
            if(oClient.name!=hsRes.inrequest?.email)
              oClient.name=hsRes.inrequest?.email		        
            if(!oClient.save(flush:true)) {
              log.debug(" Error on save client:")
              oClient.errors.each{log.debug(it)}            
              flash.error<<101
            }
          }	
          if(!(flash.error?:[]).size()){
            if(!setHomeModstatus(2,hsRes.inrequest?.home_id,iClientId))
              flash.error<<101
            else if(Home.get(hsRes.inrequest?.home_id))
              if (!setHomePropModstatus(2,hsRes.inrequest?.home_id))
                flash.error<<101
          }
        }//<<client exist		
      }
       
      //<<client		  
      if(!(flash.error?:[]).size()&&needUser){
        def oNewUser
        def oUser=User.findWhere(name:hsRes.inrequest.email)//Ð°Ð²Ñ‚Ð¾Ñ€Ð¸Ð·Ð°Ñ†Ð¸Ñ Ð½Ðµ Ñ‚Ð¾Ð»ÑŒÐºÐ¾ Ð¿Ð¾ÑÐ»Ðµ Ð¿Ð¾Ð´Ð°Ñ‡Ð¸ Ð¾Ð±ÑŠÑÐ²Ð»ÐµÐ½Ð¸Ñ
        if(!oUser){
          oNewUser=new User()
          def sCode=java.util.UUID.randomUUID().toString()		  
          iRefId=oNewUser.csiInsertInternal([email:hsRes.inrequest?.email,password:Tools.hidePsw((hsRes.inrequest?.password2?:'')),firstname:(hsRes.inrequest?.firstname?:''),lastname:(hsRes.inrequest?.lastname?:''),nickname:(hsRes.inrequest?.nickname?:''),client_id:iClientId,code:sCode])		  
          hsRes.inrequest.error=1 //Ñ€ÐµÐ³Ð¸ÑÑ‚Ñ€Ð°Ñ†Ð¸Ñ Ð·Ð°Ð²ÐµÑ€ÑˆÐµÐ½Ð°
          if(iClientId) {//owner Dmitry>>
            oUser = User.get(iRefId)
            oUser.is_am = 1
            if(!oUser.save(flush:true)) {
              log.debug(" Error on save user:")
              oUser.errors.each{log.debug(it)}            
            }
          }//<<owner Dmitry
        //note>>
          if(hsRes.inrequest.code){
            def oNote = Note.findByUser_idAndNotetype_id(iRefId,1)
            if (!oNote)
              oNote = new Note(hsRes.inrequest.home_id, iRefId, Notetype.get(1))
            try{
              oNote.save(flush:true)
            } catch (Exception e){
              log.debug("Error on save Note in User:saveuser\n"+e.toString())
            }
          }
		  //<<note
		//<<Email 		
          def lsText=Email_template.findWhere(action:'#activation')
          def sText='[@EMAIL], for activation of your account use follow link [@URL]'
          def sHeader="Registration at StayToday" 
          if(lsText){
            sText=lsText.itext
            sHeader=lsText.title
          }
          sText=sText.replace(
          '[@NICKNAME]',hsRes.inrequest.nickname?:'').replace(
          '[@EMAIL]',hsRes.inrequest.email).replace(
          '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/user/confirm/'+sCode))
          sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
          sHeader=sHeader.replace(
          '[@EMAIL]',hsRes.inrequest.email).replace(
          '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+'/user/confirm/'+sCode))

          try{ 
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
             mailerService.sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,hsRes.inrequest.email,sHeader,1)        
            else{
              sendMail{
                to hsRes.inrequest.email        
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }          
          }catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString())
            flash.error<<-100 
          }		  
        }else{
          flash.error<<9
        }
      }	  	 
      if(!flash.error){
        redirect(action:'login', params:[jsoncallback:params.jsoncallback,user:hsRes.inrequest.email,password:requestService.getStr('password1'),provider:PROVIDER])
        return
      }      
    }    
    hsRes.inrequestAjax = [:]
    hsRes.inrequestAjax.error=((flash.error?:[]).size())?1:0//boolean to int
    hsRes.inrequestAjax.errorprop=flash.error
    render hsRes.inrequestAjax as JSON
    return             
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////  
  def rest={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.inrequest=[:]
    if(hsRes.user!=null){ 
      redirect(action:'index')
      return
    }    
    hsRes.inrequest.name=requestService.getStr('name')
    hsRes.inrequest.error=0    
    
    def oUser=User.findWhere(name:hsRes.inrequest.name)
    if(!oUser){       
      hsRes.inrequest.error=1 //USER NOT EXISTS      
      render "${params.jsoncallback}(${hsRes.inrequest as JSON})"
      return
    }
    if(!Tools.checkEmailString(hsRes.inrequest.name)){
      hsRes.inrequest.error=2 //ERROR IN EMAIL
      render "${params.jsoncallback}(${hsRes.inrequest as JSON})"
      return
    }         
    if(oUser.is_external){
      hsRes.inrequest.error=4 //External USER
      render "${params.jsoncallback}(${hsRes.inrequest as JSON})"
      return
    }
    if (!oUser.code) {
      oUser.code=java.util.UUID.randomUUID().toString()
      if(!oUser.save(flush:true)) {
        log.debug(" Error on save User:")
        oUser.errors.each{log.debug(it)}
      }
    }
    def sCode=oUser.code     
    if((hsRes.inrequest.name?:'').size()>0){
      //<<Email
      def lsText=Email_template.findAllWhere(action:'#restore')
      def sText='[@EMAIL], for restore of your password use follow link [@URL]'
      def sHeader="Restore password" 
      if((lsText?:[]).size()>0){
        sText=lsText[0].itext
        sHeader=lsText[0].title
      }
      sText=sText.replace(
      '[@NICKNAME]',oUser.nickname).replace(
      '[@EMAIL]',hsRes.inrequest.name).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/user/passwconfirm/'+sCode))
      sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
      sHeader=sHeader.replace(
      '[@EMAIL]',hsRes.inrequest.name).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+'/user/passwconfirm/'+sCode))

      try{ 
        if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
           mailerService.sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,hsRes.inrequest.name,sHeader,1)        
        else{
          sendMail{
            to hsRes.inrequest.name        
            subject sHeader
            body( view:"/_mail",
            model:[mail_body:sText])
          }
        }          
      }catch(Exception e) {
        log.debug("Cannot sent email \n"+e.toString())          
        hsRes.inrequest.error=-100          	  
      }
    //>>Email	                       
    }
    render hsRes.inrequest as JSON
    return
  }
  def phone_test={
    log.debug('phoneCall')  
  } 
//////////////////////////////////////////////////////////////////////////////////////////////////
  def where_autocomplete={
    requestService.init(this)	
    def sName=requestService.getStr('where')
    def hsRes=[:]    
    if(sName?:''){
      hsRes.records=Popkeywords.findAll('FROM Popkeywords WHERE name like :name ORDER BY rating DESC',[name:sName+'%'])         
      if((hsRes.records?:[]).size()){
        def iMax=(Tools.getIntVal(ConfigurationHolder.config.mobile.search.where_auto_complete.max,3)>=hsRes.records.size())?hsRes.records.size()-1:Tools.getIntVal(ConfigurationHolder.config.mobile.search.where_auto_complete.max,3)       
        hsRes.records=hsRes.records[0..iMax]
      }
    }	
    render "${params.jsoncallback}(${hsRes as JSON})"
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def page_404 = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    if(Tools.getIntVal(ConfigurationHolder.config.loginDenied.isLoginDenied,0)) {
      hsRes.isLoginDenied=true
      hsRes.loginDeniedText=ConfigurationHolder.config.loginDenied.text?:'Ð¡ÐµÑ€Ð²Ð¸Ñ Ð²Ñ€ÐµÐ¼ÐµÐ½Ð½Ð¾ Ð½ÐµÐ´Ð¾ÑÑ‚ÑƒÐ¿ÐµÐ½'
    }
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def page_500 = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)

    if (!Tools.getIntVal(ConfigurationHolder.config.isdev,0)&&request?.exception?.stackTraceLines?.find{it.toString().trim().matches('.*OutOfMemoryError.*')}){
      def oNotice = Admin_notice.findByIs_read(1)
      if(oNotice?.type==1) {
        oNotice.is_read = 0
        mailerService.sendAdminNoticeMail(oNotice)
      } else if(oNotice?.type==2){
        oNotice.is_read = 0
        smsService.sendAdminNoticeSMS(oNotice)
      }
      try {
        oNotice?.save(flush:true)
      } catch(Exception e) {}
    }
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def page_401 = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    return hsRes
  }  
}
