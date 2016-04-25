//import org.codehaus.groovy.grails.commons.grailsApplication
import grails.converters.JSON
class MobileController {
  def requestService
  def usersService
  def mailerService
  def billingService
  def searchService
  def PROVIDER='staytoday'
  def androidGcmService
  
  def static final DATE_FORMAT='yyyy-MM-dd'
  def index = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)//for wallet icon    
    requestService.setStatistic('index',0,0,0,'','',[],true) 
    hsRes.hometype=Hometype.list([sort:'regorder'])    
    render "${params.jsoncallback}(${hsRes as JSON})"
    return 
  } 
  def findAreaNearPoint(iX,iY){
    def iRadius=Tools.getIntVal(grailsApplication.config.mobile.search_near.radius.max,100)//in km
    def aCoorinates=[]
    aCoorinates<<findCoordByCenterAndRadius(iX,iY,-135, iRadius)
    aCoorinates<<findCoordByCenterAndRadius(iX,iY,45, iRadius)
    return aCoorinates
  }  
  /////////////////////////////////////////////////////////////////////////////////////  
  def search = {  
    requestService.init(this) 
    def hsRes=requestService.getContextAndDictionary(false,false,true)   
    hsRes+=requestService.getParams(['homeperson_id','term','price',
      'valuta_id','pindex','usage','is_nosmoking','is_parking','is_visa','is_tv','is_internet','is_wifi','is_cond',
      'is_heat','is_kitchen','is_holod','is_microwave','is_wash','is_breakfast','is_swim','is_steam','is_gym',
      'is_hall','is_family','is_pets','is_invalid','is_area','is_beach','nref','rating','sort','bathroom','bed','bedroom',
      ],['price_min','price_max'],['title','description','city','district',
      'street','email','homenumber','keywords','date_start','date_end','hometype_id'],[])
      
    hsRes.inrequest.district_id=requestService.getIds('district_id') 
    hsRes.inrequest.metro_id=requestService.getIds('metro_id')
	
    hsRes.inrequest.where=requestService.getStr('where')
    hsRes.userPoint=[x:requestService.getLongDef('x',0),y:requestService.getLongDef('y',0)]	

    if(hsRes.inrequest?.date_start?:''){	  
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)  
    }
    if(hsRes.inrequest?.date_end?:''){	  
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
    }
    hsRes.urlphoto = grailsApplication.config.urlphoto
    
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
        
    hsRes.homeperson=Homeperson.list()
    hsRes.homeoption=Homeoption.findAllByFacilitygroup_idAndModstatus(1,1,[sort:'name',order:'asc'])

    hsRes.max=Tools.getIntVal(grailsApplication.config.mobile.search.listing.max,5)	  
    hsRes.paging_set=Tools.getIntVal(grailsApplication.config.mobile.paging_set,5)	
    
    hsRes+=searchService._getFilter(requestService)

    def bSearchNear=false
    if (hsRes.userPoint.x && hsRes.userPoint.y){
     
      hsRes.max=Tools.getIntVal(grailsApplication.config.mobile.search_near.max,1000)
      def coordinates=findAreaNearPoint(hsRes.userPoint.x/100000,hsRes.userPoint.y/100000)
      hsRes+=_getMapFilter(hsRes.hsFilter,Math.round(coordinates[0][0]).toLong(),Math.round(coordinates[0][1]).toLong(),Math.round(coordinates[1][0]).toLong(),Math.round(coordinates[1][1]).toLong())
      bSearchNear=true
    }
	
    def oHomeSearch=new HomeSearch()	

    hsRes+=oHomeSearch.csiFindByWhere(hsRes.inrequest.where,hsRes.max,(bSearchNear)?0:(requestService.getOffset()-1),_getMainFilter(hsRes.inrequest),hsRes.hsFilter,true,requestService.getIntDef('is_main',0)?true:false,bSearchNear,true)		    

    //hsRes+=_getFilterDictionary(hsRes.homeclassMax)
    hsRes+=searchService._getFilterDictionary(hsRes.homeclassMax,hsRes.districtMax,hsRes.metroMax)
    hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.count,hsRes.max,hsRes.paging_set)      		
	  
  //near>>
    if (hsRes.userPoint.x && hsRes.userPoint.y){	
      if(!hsRes.inrequest.sort){
        hsRes.records = oHomeSearch.sortByDistanceFromPoint(hsRes.records, hsRes.userPoint)	
      }
      hsRes.near_page=Tools.getIntVal(grailsApplication.config.mobile.near.paging,5)
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
    //statistic>>
    def lsRecIds = []
    hsRes.records.each{lsRecIds<<it.hid}
    if(bSearchNear)
      requestService.setStatistic('search',22,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds,true)
    else
      requestService.setStatistic('search',0,0,hsRes.count, hsRes.inrequest.where,'',lsRecIds,true)
    //<<statistic
    
    //discount default
    hsRes.discount_defaultext=message(code:'search.discount.defaulttext')
    
    //log.debug('hsRes='+hsRes)
    render "${params.jsoncallback}(${hsRes as JSON})"
    return    
  } 
  /////////////////////////////////////////////////////////////////////////////////////
  def _getMainFilter(hsInrequest){
    requestService.init(this)
    def hsMainFilter=[set:false]	
    hsMainFilter.homeperson_id=requestService.getIntDef('homeperson_id',0)
    //hsMainFilter.hometype_id=requestService.getIntDef('hometype_id',0)    
    hsMainFilter.hometype_id=Hometype.findByUrlname(requestService.getStr('hometype_id'))?.id?:0                 
    
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
    
    render "${params.jsoncallback}(${wallet as JSON})"
    return  
  }
/////////////////////////////////////////////////////////////////////////////////////  
  def view = {
    requestService.init(this) 
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    hsRes.inrequest=[:]    
    //>>for discount
    if(requestService.getStr('date_start')){	  
      hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, requestService.getStr('date_start'))  
    }
    if(requestService.getStr('date_end')){	  
      hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, requestService.getStr('date_end'))
    }
    //<<for discount
    
    def lId=requestService.getLongDef('id',0)	
    hsRes.urlphoto = grailsApplication.config.urlphoto    
    hsRes.home = Home.get(lId)	      
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    
    hsRes.ownerUsers = User.findAll('FROM User WHERE client_id=:client_id AND banned=0 ORDER BY modstatus, id',[client_id:hsRes.home.client_id])
    hsRes.homeoption_name=[:]    
    hsRes.homeoption=Homeoption.findAllByFacilitygroup_idAndModstatus(1,1,[sort:'id'])
    //because of old mobile client code
    for(oHOpt in hsRes.homeoption){
      if(oHOpt.fieldname)
        hsRes.homeoption_name[oHOpt.id-1]=oHOpt.fieldname
    }    
	  
    hsRes.homephoto=Homephoto.findAll('FROM Homephoto WHERE home_id=:home_id ORDER BY norder',[home_id:lId])	  	  
    hsRes.homeclass=Homeclass.get(hsRes.home.homeclass_id)
    hsRes.hometype=Hometype.get(hsRes.home.hometype_id)
    hsRes.homeperson=Homeperson.get(hsRes.home.homeperson_id)
    hsRes.homeroom=Homeroom.get(hsRes.home.homeroom_id?:1)
    hsRes.homebath=Homebath.get(hsRes.home.homebath_id?:1)
    hsRes.country=Country.get(hsRes.home?.country_id)
	  
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
      hsRes.hot_min_rent_days=Timetodecide.findByDays(hsRes.discounts?.hot?.minrentdays?:0)?.name2?:"любой срок"      
      
      hsRes.long_expire_days=Timetodecide.findByDays(hsRes.discounts?.long?.discexpiredays?:0)?.name2?:''
      hsRes.longt_expire_days=Timetodecide.findByDays(hsRes.discounts?.long?.minrentdays?:0)?.name2?:"любой срок"
    }
    requestService.setStatistic('homeview',0,hsRes.home.id,0,'','',[],true)
    render "${params.jsoncallback}(${hsRes as JSON})"
    return 	
  }
  def cancellation = { 
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.cancellation=Rule_cancellation.list()
	
    render "${params.jsoncallback}(${hsRes as JSON})"
    return 
  }  
  def comments = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    def oUcommentSearch = new UcommentSearch()
    hsRes+=requestService.getParams([],['home_id','owner_id'])//'u_id',
    
    hsRes.imageurl = grailsApplication.config.urluserphoto
	
    hsRes.owneruser = User.get(hsRes.inrequest?.owner_id)  
    hsRes.max=Tools.getIntVal(grailsApplication.config.mobile.comments.listing.max,3)
    hsRes.paging_set=Tools.getIntVal(grailsApplication.config.mobile.paging_set,5)
	
    hsRes+=oUcommentSearch.csiSelectUcommentsForHome(hsRes.inrequest.home_id,hsRes.max,requestService.getOffset()-1)
    hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.count,hsRes.max,hsRes.paging_set)  
    hsRes.answerComments=hsRes.records.collect {Ucomment.findAll('FROM Ucomment WHERE refcomment_id=:rc_id ORDER BY id DESC',[rc_id:it.id])}
    hsRes.home_clients=hsRes.records.collect {Home.get(it?.home_id).client_id}
    hsRes.home = Home.get(hsRes.inrequest.home_id)

    if((hsRes.user?.client_id?:0)!=hsRes.home.client_id)
      requestService.setStatistic('homeview',1,hsRes.inrequest.home_id,0,'','',[],true)
    render "${params.jsoncallback}(${hsRes as JSON})"
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
      render "${params.jsoncallback}(${hsOut as JSON})"
      return
    }
    hsRes.home = Home.get(hsRes.inrequest?.home_id?:0)
    if(hsRes.home){
      if (hsRes.user.client_id == hsRes.home.client_id) {
        hsOut.error=1
        render "${params.jsoncallback}(${hsOut as JSON})"
        return
      }
      def stringlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
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
    render "${params.jsoncallback}(${hsOut as JSON})"
    return	    
  }
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
    render "${params.jsoncallback}(${hsOut as JSON})"
    return    
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
    render "${params.jsoncallback}(${result as JSON})"
    return
  }
  ///////////////////////////////////////////////////////////////////////////////////////
  def login = {  
    requestService.init(this)
    requestService.setCookie('user','parararam',10000)
    def hsInrequest = requestService.getParams(['user_index','id'],['control','act','where']).inrequest  //['is_ajax','service'],  
    def sUser=requestService.getStr('user')        
    def sPassword=requestService.getStr('password')
    flash.error = 0                  
    if((sUser=='')||(!sPassword)){	  
      flash.error = 1 // set user and provider          
    }      
    
    if(!usersService.loginInternalUser(sUser,sPassword,requestService,1,0)){        
      flash.error=1 // Wrong password or user does not exists        		
    }	  
    if(flash.error){
      render "${params.jsoncallback}(${flash as JSON})"          	        
      return
    }else {	
      def oUser=User.findWhere(email:sUser)
      /*if(oUser!=null){
        oUser.lastdate=new Date()		
        if(!oUser.save(flush:true)) {
          log.debug(" Error on save User:")
          oUser.errors.each{log.debug(it)}	 
        }
      }*/
    if(requestService.getStr('deviceToken')){
      def lsDevice=Device.findAllWhere(device:requestService.getStr('deviceToken'))
      for(dev in lsDevice)    
        dev.delete(flush:true)

      def oDevice=new Device()
      oDevice.user_id=oUser?.id
      oDevice.device=requestService.getStr('deviceToken')

      if(!oDevice.save(flush:true)) {
        log.debug(" Error on save Device:")
       oDevice.errors.each{log.debug(it)}	 
      }    
    }
    redirect (action:'index',params:[jsoncallback:params.jsoncallback])
    return
    }          
  }
  ///////////////////////////////////////////////////////////////////////////////////////
  def logout = {
    requestService.init(this)        
    if(requestService.getStr('deviceToken')){
      def lsDevice=Device.findAllWhere(device:requestService.getStr('deviceToken'))
      for(dev in lsDevice)    
        dev.delete(flush:true)  
    }    
    
    usersService.logoutUser(requestService)      
    def hsRes=[logout:true]  
    render "${params.jsoncallback}(${hsRes as JSON})"   
    return
  }
  ///////////////////////////////////////////////////////////////////////////////////////
  def facebook={
    requestService.init(this)
    requestService.setCookie('user','parararam',10000)
    def hsInrequest = requestService.getParams([],['id','m_fb_id','fb_id'],['fb_name','fb_pic','fb_photo','fb_email']).inrequest //fb_id???
	if (hsInrequest.fb_id?:0){
      if(!usersService.loginUser('f_'+hsInrequest.fb_id.toString(),'','facebook',requestService,'')){
          // if user banned
          flash.error=5
      }
    } else if(hsInrequest.m_fb_id?:0 /*facebookConnectService.isLoggedIn(request)*/) {
      try {
        //def lId=facebookConnectService.getFacebookClient().users_getLoggedInUser()	
        def bNewUser = true
        if (User.findWhere(openid:'f_'+hsInrequest.m_fb_id.toString())?.ref_id?:0)
          bNewUser = false
        //def useObj = facebookConnectService.getFacebookUser()
        if(!usersService.loginUser('f_'+hsInrequest.m_fb_id,hsInrequest.fb_name,'facebook',requestService,hsInrequest.fb_photo,flash?.ref_id?:0)){
          // if user banned
          flash.error=5          
        }else{
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
          if(requestService.getStr('deviceToken')){
            def oDevice=new Device()
            oDevice.user_id=oUser?.id
            oDevice.device=requestService.getStr('deviceToken')
    
            if(!oDevice.save(flush:true)) {
              log.debug(" Error on save Device:")
              oDevice.errors.each{log.debug(it)}	 
            }
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
        /*
			  if(flash?.code){//>>from saveuser
				def oClient=Client.findWhere(code:flash.code?:0)
				if(oClient){
				  def iClientId=User.get(oUser.ref_id)?.client_id?:0
				  homeService.setHomeModstatus(2,flash?.home_id,oClient?.id,iClientId)
				  if(Home.get(flash?.home_id))
            homeService.setHomePropModstatus(2,flash?.home_id?:0)
				}
			  }//<<from saveuser
        */
			}
		}
      } catch (Exception e) {        
         log.debug('Facebook parse error :'+e.toString())        
		 flash.error=3
	  }
	}else{
	  flash.error=6
	}
  if(flash.error){
      render "${params.jsoncallback}(${flash as JSON})"          	        
      return
    }	
	  redirect (action:'index',params:[jsoncallback:params.jsoncallback])
    return
  }
  def calculatePrice = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(true)  
    def lId=requestService.getLongDef('home_id',0)
    def iMod=requestService.getIntDef('modifier',0)
    hsRes.inrequest=[:]
	
    hsRes.inrequest.homeperson_id=requestService.getLongDef('homeperson_id',0)	
    hsRes.inrequest.date_start=requestService.getStr('date_start')
    hsRes.inrequest.date_end=requestService.getStr('date_end')

    hsRes.result = 0
    if ((hsRes.inrequest?:[]).size()!=3) {
      hsRes.error = false
      hsRes.result = null
      render "${params.jsoncallback}(${hsRes as JSON})"
      return
    }
    //Alex>>	    
    hsRes += Home.calculateHomePrice(hsRes,lId,iMod?false:true)
    if (hsRes.errorprop)
      hsRes.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)

    def oValutarate = new Valutarate()
    hsRes.valutarate=oValutarate.csiGetRate(Home.get(lId)?.valuta_id?:857)      
    
    render "${params.jsoncallback}(${hsRes as JSON})"
    return
  }
  //messages>>
  def isMessAllowed={
    requestService.init(this)
    def hsOut=[error:0]
    def hsRes=requestService.getContextAndDictionary()
    if (!hsRes.user){
      hsOut.error=3
      render "${params.jsoncallback}(${hsOut as JSON})"
      return
    }
    def lId = requestService.getLongDef('id',0)
    hsRes.owner = User.get(lId)
    if (!hsRes.owner){      
      hsOut.error=3
      render "${params.jsoncallback}(${hsOut as JSON})"
      return
    }
    hsRes.user = User.get(hsRes.user.id)
    if (hsRes.owner.is_telaprove && !hsRes.user.is_telcheck){
      hsOut.error=1
      render "${params.jsoncallback}(${hsOut as JSON})"
      return
    }
    if (hsRes.owner.is_clientphoto && !hsRes.user.picture){
      hsOut.error=2
      render "${params.jsoncallback}(${hsOut as JSON})"
      return
    }  
    render "${params.jsoncallback}(${hsOut as JSON})"
    return
  }
 //////////////////////////////////////////////////////////////////////////////////////////////////
  def addmbox={
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,false,true) 
    hsRes+=requestService.getParams(['homeperson_id','answertype_id','modstatus','is_answer'],['id'],['mtext','date_start','date_end',[]])
    //hsRes.inrequest.is_telperm=requestService.getIntDef('is_telperm',-1)
    //hsRes.error=[]
    hsRes.hInrequest=[:]
    hsRes.hInrequest.mbox_error=[]
    if(hsRes.user.id&&hsRes.inrequest.id){
      if(hsRes.inrequest?.date_start?:''){	  
        hsRes.inrequest.date_start1=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)  
      }
      if(hsRes.inrequest?.date_end?:''){	  
        hsRes.inrequest.date_end1=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
      }
	  
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

          def stringlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
          if (hsRes.inrequest?.mtext.size()>stringlimit) hsRes.inrequest?.mtext = hsRes.inrequest?.mtext.substring(0, stringlimit)
          if (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false) {
            hsRes.inrequest.mtext = (hsRes.inrequest.mtext?:'').replaceAll('[0-9( )-]{6,}','[номер] ').replaceAll('\\S+@\\S*','[email]').replaceAll('\\S*@\\S+','[email]').trim()
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
          oMbox.is_telperm=1//hsRes.inrequest.is_telperm?1:0	    
          //oMbox.timezone=hsRes.inrequest.timezone		
          oMbox.home_id=hsRes.inrequest.id
          oMbox.homeowner_cl_id=Home.get(hsRes.inrequest.id).client_id?:0
          oMbox.answertype_id=0
          oMbox.modstatus=1
          oMbox.is_answer=0	      
          oMbox.is_read=0    
          oMbox.is_approved = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?1:0          
	  
          if (!oMbox.save(flush:true)){
            log.debug('error on save Mbox in MobileController:addmbox')
            oMbox.errors.each{log.debug(it)}
            hsRes.hInrequest.mbox_error<<5
          }	          		  		      
          if(!hsRes.hInrequest.mbox_error){
            def oMboxrec=new Mboxrec()		   
            oMboxrec.mbox_id =oMbox.id
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
              log.debug('error on save Mboxrec in MobileController:addmbox')
              oMboxrec.errors.each{log.debug(it)}
              hsRes.hInrequest.mbox_error<<5
            }		  
          }
          if(!hsRes.hInrequest.mbox_error){
            mailerService.sendMboxFirstMails(oMbox,hsRes.user.id,hsRes.context,(Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false))      
            
            def oHomeGCM = Home.get(oMbox.home_id)
            def oClientGCM = Client.get(oHomeGCM?.client_id)
            oClientGCM = oClientGCM.parent?Client.get(oClientGCM.parent):oClientGCM
            
            if(Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false){
            //GCM>>                            
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
            hsRes.hInrequest.user=[:]	
            def oUser=User.findWhere(client_id:oClientGCM?.id)	    
	          hsRes.hInrequest.user.nickname=oUser.nickname
	          hsRes.hInrequest.user.tel=oUser.tel
         }
        }else{		
          if (hsRes.errorprop)
            hsRes.hInrequest.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)
        } 		
      }
    } 
    
    hsRes.hInrequest.error=hsRes.error?1:0
    hsRes.hInrequest.result=hsRes.result
    //log.debug('hsRes.hInrequest='+hsRes.hInrequest)    
    render "${params.jsoncallback}(${hsRes.hInrequest as JSON})"    
  }
  def checkUser(hsRes) {   
    if(!hsRes?.user){	  	                       
      //redirect(controller:'index', action:'index')
      return false;
    }else
      return true;	
  }
  def checkAccess(hsRes) { 
    if (!(hsRes.message && (hsRes.message.user_id==hsRes.user?.id || hsRes.client_id == Home.get(hsRes.message.home_id)?.client_id))){
      //redirect(controller:'personal', action:'index')
      def hsOut=[error:true]
      render "${params.jsoncallback}(${hsOut as JSON})"
      return false
    }
    return true
  }  
  def init(hsRes){   
    def hsTmp=findClientId(hsRes)
    hsTmp.imageurl = grailsApplication.config.urlphoto + hsTmp.client_id.toString()+'/'
    hsTmp.homeurl = grailsApplication.config.urlphoto
    hsTmp.textlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
    hsTmp.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
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
////////////////////////////////////////////////////////////////////////////////
  def inbox_list={    
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false) 
	
    if (!checkUser(hsRes)) return   	
    hsRes+=init(hsRes)   
    
    hsRes.user = User.get(hsRes.user.id)
    hsRes.imageurl = grailsApplication.config.urluserphoto
    hsRes.urlphoto = grailsApplication.config.urlphoto         
    hsRes+=requestService.getParams(['ord'],['client'])
    hsRes.inrequest.modstatus=requestService.getIntDef('modstatus',-1)
    hsRes.data=[records:[],count:0]    
    
    if(hsRes.user){
      hsRes.max=Tools.getIntVal(grailsApplication.config.mobile.inbox_listing.max,5)  	  
      hsRes.paging_set=Tools.getIntVal(grailsApplication.config.mobile.inbox_listing.paging_set,5)	        	  	
     
      def oMbox=new MboxSearch()	  
      hsRes.data=oMbox.csiGetMbox(hsRes.user?.id?:0,hsRes.inrequest?.client,hsRes.inrequest?.modstatus,hsRes.max,requestService.getOffset()-1,0,hsRes.int?.ord?:0) 
      /*not used in mobile
      hsRes.reminder=[]
      
      def today = new Date()
      for(def i=0; i<hsRes.data.records.size();i++){
        use(groovy.time.TimeCategory){
          def duration = hsRes.data.records[i].moddate + Tools.getIntVal(grailsApplication.config.mbox.answertime.hours,4).hours - today
          def days = duration.days
          def hours = duration.hours
          def minutes = duration.minutes
          def seconds = duration.seconds
          if(days==0 && hours>=0 && minutes>=0 && seconds>=0)
            hsRes.reminder[i] = ((hours<10)?'0':'')+hours+':'+((minutes<10)?'0':'')+minutes+':'+((seconds<10)?'0':'')+seconds
          else
            hsRes.reminder[i] = 0
        }        
      }
      */
      hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.data.count,hsRes.max,hsRes.paging_set)
      
      hsRes.count=oMbox.csiGetMbox(hsRes.user?.id,hsRes.inrequest?.client?:0,-1,0,0).count
      hsRes.count_fav=oMbox.csiGetMbox(hsRes.user?.id,hsRes.inrequest?.client?:0,-2,0,0).count
      hsRes.count_del=oMbox.csiGetMbox(hsRes.user?.id,hsRes.inrequest?.client?:0,0,0,0).count

      hsRes.modstatus = Mboxmodstatus.findAllByModstatusGreaterThan(0)
      hsRes.status_count = []
      for(status in hsRes.modstatus)
        hsRes.status_count << oMbox.csiGetMbox(hsRes.user?.id?:0,hsRes.inrequest?.client?:0,status.modstatus,0,0).count
	  
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      hsRes.homeperson=Homeperson.list()
      hsRes.answertype=Answertype.list()	
      hsRes.home_clients=hsRes.data.records.collect {Home.get(it?.home_id).client_id}
    }
   
    //hsRes.testIsNeed=Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false
    render "${params.jsoncallback}(${hsRes as JSON})"
    return
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
    render "${params.jsoncallback}(${hsOut as JSON})"
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
    render "${params.jsoncallback}(${hsOut as JSON})"
    return        
  } 
//////////////////////////////////////////////////////////////////////////////// 
  def msg_list={   
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.sender=hsRes.recipient=[]    
    
    hsRes.user = User.get(hsRes.user.id)
    hsRes.imageurl = grailsApplication.config.urluserphoto          
    
    def lId=requestService.getLongDef('id',0)
    hsRes.message = Mbox.get(lId)
    if (!checkAccess(hsRes)) return
    
    if(hsRes.message?.date_end && hsRes.message?.date_start)      
      use(groovy.time.TimeCategory) {
        def duration = hsRes.message?.date_end - hsRes.message?.date_start
        hsRes.days_between = duration.days			
      }		          
    
    if(hsRes.message && hsRes.user){  
      if(hsRes.message.is_answer){
   	    if(hsRes.message.user_id==hsRes.user.id)
          hsRes.message.is_read=1
      }else if(hsRes.client_id==Home.get(hsRes.message.home_id?:0)?.client_id?:0)
        hsRes.message.is_read=1		
      
      if (!hsRes.message.save(flush:true)){
        log.debug('error on save Mbox in MobileController:addmbox')
        hsRes.message.errors.each{log.debug(it)}		    
      }
    
      def lClientId = Home.get(hsRes.message.home_id).client_id
      hsRes.ownerClient = Client.get(lClientId)
      hsRes.sender = User.findWhere(client_id:lClientId)
      hsRes.recipient = User.get(hsRes.message?.user_id)	       

      hsRes.data=[records:[],count:0]
	  
      hsRes.max=Tools.getIntVal(grailsApplication.config.mobile.msg_listing.max,5)  	  
      hsRes.paging_set=Tools.getIntVal(grailsApplication.config.mobile.msg_listing.paging_set,5)
	  
      def oMbox=new MboxrecSearch()	  
      hsRes.data=oMbox.csiGetList(hsRes.message?.id,hsRes.message?.user_id,hsRes.sender?.id,hsRes.user.id==hsRes.message.user_id?0:1,hsRes.max,requestService.getOffset()-1)      	  
	  	
      hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.data.count,hsRes.max,hsRes.paging_set)
	  
      hsRes.userHomes=Home.findAll('FROM Home WHERE client_id=:client_id and modstatus=1 ORDER BY name',[client_id:lClientId]) 
      hsRes.valutaSymbols=[]
      for(oHome in hsRes.userHomes)
        hsRes.valutaSymbols << Valuta.get(oHome.valuta_id).symbol
      
      hsRes.minday=Rule_minday.findAll('FROM Rule_minday')
      hsRes.maxday=Rule_maxday.findAll('FROM Rule_maxday')
      hsRes.homeperson=Homeperson.findAll('FROM Homeperson')
      hsRes.rule_minday=Rule_minday.findAll('FROM Rule_minday')
      hsRes.rule_maxday=Rule_maxday.findAll('FROM Rule_maxday')      
      hsRes.answergroup=Answergroup.list()
      hsRes.answertype=Answertype.list()
      hsRes.isHideContact=Tools.getIntVal(grailsApplication.config.mbox.hideContactMode,1)?true:false
      hsRes.iscanoffer = Mboxrec.findAllByMbox_idAndIs_answerAndAnswertype_idInList(hsRes.message?.id?:0,1,[1,2])?false:true
      hsRes.iscandecline = Mboxrec.findAllByMbox_idAndIs_answerAndAnswertype_idGreaterThanAndAdmin_id(hsRes.message?.id?:0,1,0,0)?false:true
      
      hsRes.home = Home.get(hsRes.message.home_id)
      hsRes.alikeWhere = hsRes.home.csiGetWhere()  

      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      //hsRes.cpecofferRates = oValutarate.csiGetRate(hsRes.home?.valuta_id?:857)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
	  
      hsRes.home_names=hsRes.data.records.collect {Home.get(it?.rechome_id).name}	         
      hsRes.cpecofferRates = oValutarate.csiGetRate(hsRes.home?.valuta_id?:857)      
      hsRes.ispaypossible = (Client.get(hsRes.message.homeowner_cl_id)?.resstatus==1)
      hsRes.resstatModifier = 1.0
      if (hsRes.ispaypossible)
        hsRes.resstatModifier = hsRes.resstatModifier + (Tools.getIntVal(grailsApplication.config.clientPrice.modifier,4) / 100)
      
      hsRes.displayPrice = Math.round(hsRes.message?.price_rub * hsRes.resstatModifier / hsRes.valutaRates)      
      hsRes.reserve = Reserve.get(hsRes.ownerClient?.reserve_id?:0)
      if(hsRes.ispaypossible && hsRes.reserve)
        hsRes.totalPrice = Math.round(billingService.getBronPrice(hsRes.message) / hsRes.valutaRates)
    }	
    render "${params.jsoncallback}(${hsRes as JSON})"
    return 
}
////////////////////////////////////////////////////////////////////////////////  
  def addanswer={  
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return	
    hsRes+=init(hsRes)							    
    hsRes+=requestService.getParams(['homeperson_id','rule_minday_id','rule_maxday_id','answertype_id'],['id','price_spec','home_id','home_id_spec'],['date_start','date_end'],[])           		    
  //check date>>
    hsRes.result = 0
	
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
    //check date<<	
    def hsOut=[:]
    if (hsRes.errorprop){
      hsOut.error=hsRes.error
      hsOut.errorprop = message(code:hsRes.errorprop, args:(hsRes.errorArgs?:[]), default:hsRes.errorprop)      
      render "${params.jsoncallback}(${hsOut as JSON})"   
      return		
	  }
			
    def lsMessage=requestService.getStr('message').split(',')
    for(message in lsMessage)
      if(message)
        hsRes.inrequest.message=message
      
    if(hsRes.inrequest?.id?:0){	 	
      def oMbox=Mbox.get(hsRes.inrequest?.id)
      if(oMbox.user_id==hsRes.user.id){
        oMbox.is_answer=0
        oMbox.answertype_id=0
      }else if(hsRes.client_id==Home.get(oMbox.home_id?:0)?.client_id?:0){	
        oMbox.is_answer=1
        oMbox.answertype_id=hsRes.inrequest.answertype_id?:6
        oMbox.controlstatus = (oMbox.controlstatus>0)?oMbox.controlstatus:((hsRes.inrequest.answertype_id in [3,4,5])?2:1)
        if (oMbox.inputdate>new Date(112,11,05))//release date this feature. Mboxes older this date have not an adequate statistic.
          oMbox.responsetime?:(oMbox.responsetime=(new Date().getTime()-oMbox.inputdate.getTime())/1000)
      }else
        flash.error=1
	
      hsRes.textlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)	
      if ((hsRes.inrequest?.message?:'').size()>hsRes.textlimit)
        hsRes.inrequest?.message = hsRes.inrequest?.message.substring(0, hsRes.textlimit)
		
      oMbox.is_read=0
      oMbox.moddate=new Date()
      oMbox.home_id=hsRes.inrequest.home_id?:oMbox.home_id	
      oMbox.mtext=hsRes.inrequest.message?:''	  
      if(hsRes.inrequest.answertype_id==1 || hsRes.inrequest.answertype_id==2){
        oMbox.modstatus = 3
      }else if(oMbox.modstatus<3){
		    oMbox.modstatus = 1
      } 
      if(hsRes.inrequest.answertype_id==2){       
        def oValutarate = new Valutarate()        
        def valutaRates = oValutarate.csiGetRate(Home.get(hsRes.inrequest.home_id_spec)?.valuta_id?:857)

        oMbox.price=hsRes.inrequest.price_spec?:0
        oMbox.price_rub=(oMbox.price*valutaRates).toLong()        
        oMbox.valuta_id=Home.get(hsRes.inrequest.home_id_spec)?.valuta_id?:857
        oMbox.date_start=date_start1
        oMbox.date_end=date_end1
        oMbox.homeperson_id=hsRes.inrequest.homeperson_id
        oMbox.home_id=hsRes.inrequest.home_id_spec
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
          log.debug('error on save Mbox in MobileController:addanswer')
          oMbox.errors.each{log.debug(it)}
          flash.error=2
        }		
      def oMboxrec=new Mboxrec()		
      if(!flash.error){		       		   
        oMboxrec.mbox_id =oMbox.id        
        oMboxrec.rectext=hsRes.inrequest.message?:''
        if(oMbox.user_id==hsRes.user.id){ //|| oMbox.user_id==lSocId)
          oMboxrec.is_answer=0
          oMboxrec.answertype_id=0
        } else if(hsRes.client_id==Home.get(oMbox.home_id?:0)?.client_id?:0){	
          oMboxrec.is_answer=1		
          oMboxrec.answertype_id=hsRes.inrequest?.answertype_id?:6
        }		       
        oMboxrec.inputdate=new Date()
        oMboxrec.home_id=hsRes.inrequest.home_id
		
        if(hsRes.inrequest.answertype_id==2){        		
          def oValutarate = new Valutarate()
          def valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
          oMboxrec.price=oMbox.price
          oMboxrec.price_rub=oMbox.price_rub          
          oMboxrec.valuta_id=oMbox.valuta_id
          oMboxrec.date_start=date_start1
          oMboxrec.date_end=date_end1
          oMboxrec.homeperson_id=hsRes.inrequest.homeperson_id
          oMboxrec.home_id=hsRes.inrequest.home_id_spec		  
        }else if(hsRes.inrequest.answertype_id==4){
          oMboxrec.rule_minday_id=hsRes.inrequest.rule_minday_id
          oMboxrec.rule_maxday_id=hsRes.inrequest.rule_maxday_id
        }else{
          oMboxrec.price_rub=oMbox.price_rub
          oMboxrec.price=oMbox.price
          oMboxrec.valuta_id=oMbox.valuta_id
          oMboxrec.date_start=oMbox.date_start
          oMboxrec.date_end=oMbox.date_end
          oMboxrec.homeperson_id=oMbox.homeperson_id
          oMboxrec.home_id=oMbox.home_id	
        }		
        if (!oMboxrec.save(flush:true)){
          log.debug('error on save Mboxrec in MobileController:addmbox')
          oMboxrec.errors.each{log.debug(it)}
          flash.error=2
        }		
      }    
    
      if(!flash.error && (User.get(oMbox.user_id)?.modstatus?:0)==1){
        def oHome=Home.get(hsRes.inrequest.home_id?:oMbox.home_id)        
        //<<Email
        def sMailTo=''
        def sNickname=''
        def sText='Ссылка на объявление- [@HOME].<br/> Тест сообщения: [@TEXT].<br/>Для ответа на заявку перейдите по [@URL]'
        def sHeader="Собщение по объявлению [@HOME]"       
        def lsText=[]
        if(oMbox.is_answer){//from owner
          if(hsRes.inrequest.answertype_id in [3,5,6])
            lsText=Email_template.findWhere(action:'#mbox_from_owner')          
          else if(hsRes.inrequest.answertype_id==2)
            lsText=Email_template.findWhere(action:'#mbox_spec')
          else if(hsRes.inrequest.answertype_id==1)//bron'
            lsText=Email_template.findWhere(action:'#mbox_from_owner')
          else if(hsRes.inrequest.answertype_id==4)//bron'
            lsText=Email_template.findWhere(action:'#mbox_from_owner_max_min_days')			
			
          def oUser=User.get(oMbox.user_id)
          sMailTo=oUser?.email
	        sNickname=oUser?.nickname
          hsRes.user_reciever=oUser
        }else{//from client
          lsText=Email_template.findWhere(action:'#mbox_from_client')            
          def oClient=Client.get(oHome?.client_id?:0)
          oClient=oClient.parent?Client.get(oClient.parent):oClient		  
          sMailTo=oClient?.name
          sNickname=User.findByClient_id(oClient?.id?:0l)?.nickname?:'владелец'
          hsRes.client_reciever=oClient          
        }				
		
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
		 
        if(oMbox.is_answer){//from owner
          hsRes.resstatModifier = 1.0
          if (Client.get(oMbox.homeowner_cl_id)?.resstatus==1) {
            hsRes.resstatModifier = hsRes.resstatModifier + (Tools.getIntVal(grailsApplication.config.clientPrice.modifier,4) / 100)
          }
          def oAnswertype=Answertype.get(hsRes.inrequest.answertype_id)
          if(hsRes.inrequest.answertype_id==6){
            sText=sText.replace('[@NICKNAME]',sNickname).replace('[@HOME]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
            .replace('[@ANSWER_TYPE_SHORTNAME]',oAnswertype?.shortname?:'')
            .replace('[@TEXT]',hsRes.inrequest.message?:'')		  
            sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
            sText=sText.replace('[@URL]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/inbox/view/'+oMbox.id+">ссылке</a>") 
          }else if(hsRes.inrequest.answertype_id==2){
            sText=sText.replace('[@NICKNAME]',sNickname)
            .replace('[@HOME]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
            .replace('[@SPEC_DATE_START]',String.format('%tY-%<tm-%<td',oMboxrec.date_start))
            .replace('[@SPEC_DATE_END]',String.format('%tY-%<tm-%<td',oMboxrec.date_end))
            .replace('[@SPEC_HOMEPERSON]',Homeperson.get(oMboxrec.homeperson_id)?.name?:'')
            .replace('[@SPEC_PRICE]',(Math.round(oMboxrec.price*hsRes.resstatModifier).toString()))
            .replace('[@SPEC_VALUTA]',Valuta.get(oMboxrec.valuta_id?:0).code)
            .replace('[@COMMENTS]',oMboxrec.rectext?:'')
          }else if(hsRes.inrequest.answertype_id==1){
            sText=sText.replace('[@NICKNAME]',sNickname).replace('[@HOME]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
            .replace('[@ANSWER_TYPE_SHORTNAME]',oAnswertype?.shortname?:'')
			      .replace('[@TEXT]',hsRes.inrequest.message?:'')//TODO!!		  
            sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
            sText=sText.replace('[@URL]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/inbox/view/'+oMbox.id+">СЃСЃС‹Р»РєРµ</a>") 
          }else if(hsRes.inrequest.answertype_id==3 || hsRes.inrequest.answertype_id==5){
		        sText=sText.replace('[@NICKNAME]',sNickname).replace('[@HOME]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")			
            .replace('[@ANSWER_TYPE_SHORTNAME]',oAnswertype?.shortname?:'')
			      .replace('[@TEXT]',hsRes.inrequest.message?:'')//TODO!!            		
            sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
            sText=sText.replace('[@URL]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/inbox/view/'+oMbox.id+">СЃСЃС‹Р»РєРµ</a>") 
          }else if(hsRes.inrequest.answertype_id==4){
		        sText=sText.replace('[@NICKNAME]',sNickname).replace('[@HOME]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")			
			      .replace('[@MIN_DAY]',Rule_minday.get(hsRes.inrequest.rule_minday_id?:0)?.name?:'')	
			      .replace('[@MAX_DAY]',Rule_maxday.get(hsRes.inrequest.rule_maxday_id?:0)?.name?:'')
			      .replace('[@ANSWER_TYPE_SHORTNAME]',oAnswertype?.shortname?:'')
            .replace('[@TEXT]',hsRes.inrequest.message?:'')//TODO!!		  
            sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
            sText=sText.replace('[@URL]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/inbox/view/'+oMbox.id+">СЃСЃС‹Р»РєРµ</a>") 
          }		  
        }else{//from client
          sText=sText.replace('[@NICKNAME]',sNickname).replace('[@HOME]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
          .replace('[@TEXT]',hsRes.inrequest.message?:'')		  
          sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
          sText=sText.replace('[@URL]',"<a href="+grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/inbox/view/'+oMbox.id+">СЃСЃС‹Р»РєРµ</a>") 		  
        }
        sText+=mailerService.mail_history(oMbox.id,oMboxrec.id,oHome.name)			
        sHeader=sHeader.replace('[@HOME]',oHome.name)
        
        try{ 
          if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
             mailerService.sendMailGAE(sText,grailsApplication.config.grails.mail.from1,grailsApplication.config.grails.mail.username,sMailTo,sHeader,1)        
          else{ 
            sendMail{
              to sMailTo         
              subject sHeader
              body( view:"/_mail",
              model:[mail_body:sText])
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email \n"+e.toString()+'in addanswer')
          //flash.error<<-100 
        }		   
      }
//>>Email

//GCM>>
      Long lClientId = 0
      if (hsRes?.user_reciever?.id) {        
        if(hsRes?.user_reciever?.client_id)
          lClientId=hsRes?.user_reciever?.client_id
        else if(hsRes?.user_reciever?.ref_id)
          lClientId=User.get(hsRes?.user_reciever?.ref_id)?.client_id?:0
      }else if (hsRes?.client_reciever?.id){
        lClientId=hsRes?.client_reciever?.id
      }
      hsRes.msg_unread_count = Mbox.executeQuery('select count(*) from Mbox where is_read=0 and ((homeowner_cl_id=:cl_id and is_answer=0) or (user_id=:u_id and is_answer=1)) and not(modstatus=6 or ((homeowner_cl_id=:cl_id and IFNULL(is_owfav,0)=-1)or(user_id=:u_id and IFNULL(is_clfav,0)=-1)))',[cl_id:lClientId,u_id:hsRes?.user_reciever?.id?:0.toLong()])[0]        
/*
      if(lClientId){//guests waiting -- posible in future
        hsRes.waiting_unread_count=Zayvka2client.countByClient_idAndModstatus(lClientId,0)
      }
*/     
    def sendGCM=[:]
    sendGCM.message='letter'
    sendGCM.msgcnt=hsRes.msg_unread_count.toString()
    
    //log.debug('sendGCM.msgcnt='+sendGCM.msgcnt)
    
    def lsDevices=[]
    if(hsRes?.user_reciever?.id){
      lsDevices=Device.findAllWhere(user_id:hsRes?.user_reciever?.id)
      //log.debug('hsRes?.user_reciever?.id='+hsRes?.user_reciever?.id)
    }  
    else if(lClientId){
      //log.debug('lClientId='+lClientId)
      def lsUsers=User.findAllWhere(client_id:lClientId)
      def user_ids=[]
      for(user in lsUsers)
        user_ids<<user.id
      
      lsDevices=Device.findAll("FROM Device WHERE user_id IN (:user_ids)",[user_ids:user_ids])
    }  
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
   	
    hsRes.hInrequest=[:]    
    hsRes.hInrequest.answertype_id=hsRes.inrequest.answertype_id   

	  render "${params.jsoncallback}(${hsRes.hInrequest as JSON})"
    return
  }
////////////////////////////////////////////////////////////////////////////////  
  def addanswer_new={  
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return	
    hsRes+=init(hsRes)							    
    hsRes+=requestService.getParams(['homeperson_id','rule_minday_id','rule_maxday_id','answertype_id','pricing_unit'],
                                    ['id','price','home_id','home_id_spec'],
                                    ['date_start','date_end'],[])           		      			      
      
    def oMbox=Mbox.get(hsRes.inrequest?.id)
    def ownerClient = Client.get(oMbox?.homeowner_cl_id?:0)
    def iscanoffer = Mboxrec.findAllByMbox_idAndIs_answerAndAnswertype_idInList(oMbox?.id?:0,1,[1,2])?false:true
    if (!oMbox||oMbox?.modstatus<0||((oMbox.modstatus==3||!iscanoffer)&&hsRes.inrequest.answertype_id in [1,2])||(!(oMbox.modstatus in 3..4)&&iscanoffer&&hsRes.inrequest.answertype_id==6)||!ownerClient) {
      //action not occurs when no Mbox, mbox was deleted, or adding new special offer when previous is actually, or adding info request before special offer was made      
      render "${params.jsoncallback}(${[error:true]})"
      return
    }    
	
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
    //check date<<	        			
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
      render "${params.jsoncallback}(${hsOut as JSON})"
      return
	  }
     
    if (hsRes.inrequest.message&&(Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)) {
      if (hsRes.inrequest.message.replace('(','').replace(')','').replace('-','').replaceAll("\\s","").matches('.*[0-9]{7,}.*')||hsRes.inrequest.message.matches('.*\\S+@\\S*.*')||hsRes.inrequest.message.matches('.*\\S*@\\S+.*')) {
        hsRes.inrequest.message = (hsRes.inrequest.message?:'').replaceAll('[0-9( )-]{7,}',' [номер] ').replaceAll('\\S+@\\S*','[email]').replaceAll('\\S*@\\S+','[email]').trim()
      }
    }
    hsRes.textlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
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
      //price instead of price_spec because of older version of mobile client
      oMbox.price_rub = hsRes.inrequest.pricing_unit?hsRes.inrequest.price*oMbox.getTripLength():hsRes.inrequest.price?:0
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
   	
    hsRes.hInrequest=[:]    
    hsRes.hInrequest.answertype_id=hsRes.inrequest.answertype_id   

	  render "${params.jsoncallback}(${hsRes.hInrequest as JSON})"
    return
  }
/////////////////////////////////////////////////////////////////////////////////////  
  def bron={//for bron info page
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false) 
    if (!checkUser(hsRes)) return
    //hsRes.urlphoto = grailsApplication.config.urlphoto         
    hsRes+=init(hsRes) 
    def lMboxId=requestService.getLongDef('mbox_id',0)
    def lId=requestService.getLongDef('id',0)	
    Long lSocId = 0   
    
    hsRes.urlphoto = grailsApplication.config.urlphoto
    
    hsRes.mbox=Mbox.findByIdAndUser_idInList(lMboxId,[lSocId,hsRes.user.id])
	  hsRes.mboxRec=Mboxrec.findWhere(id:lId,mbox_id:hsRes.mbox?.id)
    
	  if(hsRes.mboxRec){
      hsRes.homeperson=Homeperson.get(hsRes.mboxRec?.homeperson_id)
      hsRes.home=Home.get(hsRes.mboxRec?.home_id)
      hsRes.owner = User.findWhere(client_id:hsRes.home.client_id)      
      hsRes.date_start=String.format('%td/%<tm/%<tY',hsRes.mboxRec?.date_start)
      hsRes.date_end=String.format('%td/%<tm/%<tY',hsRes.mboxRec?.date_end)
      hsRes.moddate=String.format('%td/%<tm/%<tY %<tH:%<tM',hsRes.mbox?.moddate)
      hsRes.cancellation=Rule_cancellation.get(hsRes.home?.rule_cancellation_id)
      hsRes.timein=Rule_timein.get(hsRes.home?.rule_timein_id?:0).name
      hsRes.timeout=Rule_timeout.get(hsRes.home?.rule_timeout_id?:0).name
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      hsRes.displayPrice = Math.rint(100.0 * (hsRes.mboxRec.price_rub / hsRes.valutaRates)) / 100.0
    }
    /*if(hsRes.mbox){
      hsRes.homeperson=Homeperson.get(hsRes.mbox?.homeperson_id)
      hsRes.home=Home.get(hsRes.mbox?.home_id)
      hsRes.owner = User.findWhere(client_id:hsRes.home.client_id)      
      hsRes.date_start=String.format('%td/%<tm/%<tY',hsRes.mbox?.date_start)
      hsRes.date_end=String.format('%td/%<tm/%<tY',hsRes.mbox?.date_end)
      hsRes.moddate=String.format('%td/%<tm/%<tY %<tH:%<tM',hsRes.mbox?.moddate)
      hsRes.cancellation=Rule_cancellation.get(hsRes.home?.rule_cancellation_id)
      hsRes.timein=Rule_timein.get(hsRes.home?.rule_timein_id?:0).name
      hsRes.timeout=Rule_timeout.get(hsRes.home?.rule_timeout_id?:0).name
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
      hsRes.displayPrice = Math.rint(100.0 * (hsRes.mbox.price_rub / hsRes.valutaRates)) / 100.0
    }*/
    render "${params.jsoncallback}(${hsRes as JSON})"
    return
  }
/////////////////////////////////////////////////////////////////////////////////////
  def setMboxBron={
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false)
	  if (!checkUser(hsRes)) return
    hsRes+=init(hsRes) 
    def lId=requestService.getLongDef('id',0)
    def lMboxrecId=requestService.getLongDef('mboxrec_id',0)

	  def oMbox=Mbox.findByIdAndUser_idInList(lId,[hsRes.user.id])
	  def oMboxrec1=Mboxrec.findWhere(id:lMboxrecId,mbox_id:oMbox?.id)
    if(oMbox){
      oMbox.modstatus=4
      oMbox.answertype_id=7	  
      oMbox.moddate=new Date()
      oMbox.mtext=message(code:'inbox.bron.message.notpay', default:'')
      oMbox.is_answer=0
      oMbox.is_read=0
	  
      if (!oMbox.save(flush:true)){
        log.debug('error on save Mbox in MobileController:setMboxBron')
        oMbox.errors.each{log.debug(it)}
        flash.error=1
      }
      if(!flash.error){
        def oMboxrec=new Mboxrec()		     	       		   
        oMboxrec.mbox_id =oMbox.id
        oMboxrec.answertype_id=oMbox.answertype_id
        oMboxrec.rectext=message(code:'inbox.bron.message.notpay', default:'')       
        oMboxrec.is_answer=0        		       
        oMboxrec.inputdate=new Date()
        
        oMboxrec.price_rub=oMboxrec1.price_rub
        oMboxrec.price=oMboxrec1.price
        oMboxrec.valuta_id=oMboxrec1.valuta_id
        oMboxrec.date_start=oMboxrec1.date_start
        oMboxrec.date_end=oMboxrec1.date_end
        oMboxrec.homeperson_id=oMboxrec1.homeperson_id
        oMboxrec.home_id=oMboxrec1.home_id	
        		
        if (!oMboxrec.save(flush:true)){
          log.debug('error on save Mboxrec in MobileController:setMboxBron')
          oMboxrec.errors.each{log.debug(it)}
          flash.error=2          
        }

        if(!flash.error){		
		      mailerService.mbox_bron_client(hsRes.user.id?:0.toLong(),oMbox.home_id,oMbox.id,oMboxrec.id,hsRes.context)
		    }			
	    }
	    if(!flash.error){
		    def oTrip = new Trip(oMbox)
		    oTrip.save(flush:true)
	    }
    }  
    render "${params.jsoncallback}()"
    return
  }
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
    render "${params.jsoncallback}()"
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
        if(oMbox){         
          oMboxrecDel.delete(flush:true)          
          hsRes.record = Mboxrec.findAllByMbox_id(oMbox.id,[sort:'inputdate',order:'desc'])[0]
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
            oMbox.modstatus = 1
            oMbox.controlstatus = 0
               
            if(!oMbox.save(flush:true)){
              log.debug('error on save Mbox in MobileController:deletembox')
              oMbox.errors.each{log.debug(it)}
              flash.error=2
            }  
          }
        }
      }
    }
    render "${params.jsoncallback}()"
    return    
  }  
//<<messge  
/////////////////////////////////////////////////////////////////////////////////////
  def wallet_search = { 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,false,true)
    if (!checkUser(hsRes)) return
    hsRes.inrequest=[:]
    hsRes.count=0
    hsRes.max=Tools.getIntVal(grailsApplication.config.mobile.search.listing.max,30)
	  hsRes.paging_set=Tools.getIntVal(grailsApplication.config.mobile.paging_set,5)
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
      hsRes.urlphoto = grailsApplication.config.urlphoto   
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol      	  
	  	
      hsRes.paging=Paging.computeNavigation(requestService.getOffset(),hsRes.count,hsRes.max,hsRes.paging_set) 
    }
    render "${params.jsoncallback}(${hsRes as JSON})"
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
      render "${params.jsoncallback}(${hsRes.inrequestAjax as JSON})"    
      return
    }
    hsRes+=requestService.getParams([],['home_id'],['code','email','nickname','firstname','lastname','password1','password2'])	

    flash.error=[]
    def iRefId=0
	  def lId=0
    def needUser = true 

    if(hsRes.inrequest?.code){
      hsRes.inrequest?.email=Client.findWhere(code:hsRes.inrequest?.code)?.name?:''//email allways exists on staytoday provider	
    }
    
    if(!(hsRes.inrequest?.email?:''))
      flash.error<<1
    else if (!Tools.checkEmailString(hsRes.inrequest.email))
      flash.error<<2
    if(!(hsRes.inrequest?.nickname?:''))
      flash.error<<3	
    if((hsRes.inrequest?.password1?:'')!=(hsRes.inrequest?.password2?:''))
      flash.error<<4
    if((hsRes.inrequest?.password2?:'').size()<Tools.getIntVal(grailsApplication.config.user.passwordlength?:5))
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
        def oUser=User.findWhere(name:hsRes.inrequest.email)//авторизация не только после подачи объявления
        if(!oUser){
          oNewUser=new User()
          def sCode=java.util.UUID.randomUUID().toString()		  
          iRefId=oNewUser.csiInsertInternal([email:hsRes.inrequest?.email,password:Tools.hidePsw((hsRes.inrequest?.password2?:'')),firstname:(hsRes.inrequest?.firstname?:''),lastname:(hsRes.inrequest?.lastname?:''),nickname:(hsRes.inrequest?.nickname?:''),client_id:iClientId,code:sCode])		  
          hsRes.inrequest.error=1 //регистрация завершена
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
          '[@URL]',(grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/user/confirm/'+sCode))
          sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
          sHeader=sHeader.replace(
          '[@EMAIL]',hsRes.inrequest.email).replace(
          '[@URL]',(grailsApplication.config.grails.mailServerURL+'/user/confirm/'+sCode))

          try{ 
            if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
             mailerService.sendMailGAE(sText,grailsApplication.config.grails.mail.from1,grailsApplication.config.grails.mail.username,hsRes.inrequest.email,sHeader,1)        
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
//>>Email      
          //redirect(action:'addnew',params:hsRes.inrequest)		  
		  //return
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
    render "${params.jsoncallback}(${hsRes.inrequestAjax as JSON})"
    return             
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////  
  def rest={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.inrequest=[:]
    if(hsRes.user!=null){ 
      redirect (action:'index',params:[jsoncallback:params.jsoncallback])
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
        '[@URL]',(grailsApplication.config.grails.mailServerURL+((hsRes.context.is_dev)?"/${hsRes.context.appname}":"")+'/user/passwconfirm/'+sCode))
        sText=((sText?:'').size()>Tools.getIntVal(grailsApplication.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(grailsApplication.config.mail.textsize,500)):sText
        sHeader=sHeader.replace(
        '[@EMAIL]',hsRes.inrequest.name).replace(
        '[@URL]',(grailsApplication.config.grails.mailServerURL+'/user/passwconfirm/'+sCode))

        try{ 
          if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
             mailerService.sendMailGAE(sText,grailsApplication.config.grails.mail.from1,grailsApplication.config.grails.mail.username,hsRes.inrequest.name,sHeader,1)        
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
    render "${params.jsoncallback}(${hsRes.inrequest as JSON})"  
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
        def iMax=(Tools.getIntVal(grailsApplication.config.mobile.search.where_auto_complete.max,3)>=hsRes.records.size())?hsRes.records.size()-1:Tools.getIntVal(grailsApplication.config.mobile.search.where_auto_complete.max,3)       
        hsRes.records=hsRes.records[0..iMax]
      }
    }	
    render "${params.jsoncallback}(${hsRes as JSON})"
  }  
}
