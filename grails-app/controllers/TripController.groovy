//import org.codehaus.groovy.grails.commons.grailsApplication
import grails.converters.JSON
class TripController {  
  def requestService
  def imageService
  def mailerService
  def usersService
  def billingService
  
  def static final DATE_FORMAT='yyyy-MM-dd'
  
  def checkUser(hsRes) {   
    if(!hsRes?.user){	  	                       
      response.sendError(401)
      return false;
    }    
    def oTemp_notification=Temp_notification.findWhere(id:1,status:1)	  
    session.attention_message=oTemp_notification?oTemp_notification.text:null
    return true
  }
 
  def init(hsRes){
    def hsTmp=findClientId(hsRes)
    hsTmp.imageurl = grailsApplication.config.urlphoto + hsTmp.client_id.toString()+'/'
    session.attention_message_once=null
    def oClient=Client.get(hsTmp?.client_id?:0)    
    if(oClient&&!(oClient?.is_notification?:0)){
      def oTemp_notification=Temp_notification.findWhere(id:4,status:1)	  
	    session.attention_message_once=oTemp_notification?oTemp_notification.text:null
      oClient.is_notification=1
      try{  
        if( !oClient.save(flush:true)) {
          log.debug(" Error on save client in personal init():")    
          oClient.errors.each{log.debug(it)}
        }
      }catch(Exception e){
        log.debug(" Error on save client in personal init(): \n"+e.toString())
      }
    }
    hsTmp.tripmenu=Infotext.findAllByItemplate_id(6,[sort:'npage',order:'asc'])
    
    hsTmp.user = User.read(hsRes.user?.id)
    
    if(hsRes.context.lang){     
      hsTmp.user = hsTmp.user.csiSetEnUser()    
    }
    
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
  def current = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    hsRes.imageurl = grailsApplication.config.urluserphoto  
    hsRes.urlphoto = grailsApplication.config.urlphoto
    hsRes.homeperson=Homeperson.findAll('FROM Homeperson')
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

    def lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id
    def oTrip = new TripSearch()
    hsRes.data=oTrip.csiGetTrip(hsRes.user?.id,lSocId?:0,2,20,requestService.getOffset())
    
    if(hsRes.context.lang){      
      def lsRecords=[]
      for(record in hsRes.data.records){
        def oTripSearchTmp=new TripSearch()        
        
        record.properties.each { prop, val ->
          if(["metaClass","class"].find {it == prop}) return
          if(oTripSearchTmp.hasProperty(prop)) oTripSearchTmp[prop] = val
        }
        oTripSearchTmp.id=record.id
        oTripSearchTmp.home_name=Tools.transliterate(oTripSearchTmp.home_name,0)
        oTripSearchTmp.home_address=Tools.transliterate(oTripSearchTmp.home_address,0)
        oTripSearchTmp.home_city=City.findByName(oTripSearchTmp.home_city)?.name_en?:Tools.transliterate(oTripSearchTmp.home_city,0)
        oTripSearchTmp.user_nickname=Tools.transliterate(oTripSearchTmp.user_nickname,0)                            
        
        lsRecords<<oTripSearchTmp             
      }  
      hsRes.data.records=lsRecords
    }
    
    requestService.setStatistic('pcservices',43)
    return hsRes
  }
  def declineTrip = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!checkUser(hsRes)) return

    def lId = requestService.getLongDef('id',0)
    hsRes.trip = Trip.get(lId)
    if (!hsRes.trip) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if (hsRes.trip.paystatus>1){
      render(contentType:"application/json"){[error:true]}
      return
    }
    try {
      if(hsRes.trip.payorder_id) {
        billingService.doTransaction( billingService.createPaytrans([payorder_id:hsRes.trip.payorder_id],'',19) )
      } else {
        billingService.declineTrip(hsRes.trip)
      }
    } catch(Exception e) {
      log.debug('Error on declain deal to trip: '+e.toString())
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  def next = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    hsRes.imageurl = grailsApplication.config.urluserphoto  
    hsRes.urlphoto = grailsApplication.config.urlphoto
    hsRes.homeperson=Homeperson.findAll('FROM Homeperson')
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

    def lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id
    def oTrip = new TripSearch()
    hsRes.data=oTrip.csiGetTrip(hsRes.user?.id,lSocId?:0,0,20,requestService.getOffset())
    
    if(hsRes.context.lang){      
      def lsRecords=[]
      for(record in hsRes.data.records){
        def oTripSearchTmp=new TripSearch()        
        
        record.properties.each { prop, val ->
          if(["metaClass","class"].find {it == prop}) return
          if(oTripSearchTmp.hasProperty(prop)) oTripSearchTmp[prop] = val
        }
        oTripSearchTmp.id=record.id
        oTripSearchTmp.home_name=Tools.transliterate(oTripSearchTmp.home_name,0)
        oTripSearchTmp.home_address=Tools.transliterate(oTripSearchTmp.home_address,0)
        oTripSearchTmp.home_city=City.findByName(oTripSearchTmp.home_city)?.name_en?:Tools.transliterate(oTripSearchTmp.home_city,0)
        oTripSearchTmp.user_nickname=Tools.transliterate(oTripSearchTmp.user_nickname,0)                            
        
        lsRecords<<oTripSearchTmp             
      }  
      hsRes.data.records=lsRecords
    }
    
    requestService.setStatistic('pcservices',42)
    return hsRes
  }

  def tripprint = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=findClientId(hsRes)

    hsRes.inrequest=[:]
    hsRes.imageurl = grailsApplication.config.urluserphoto
    hsRes.urlphoto = grailsApplication.config.urlphoto
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    def lId = requestService.getLongDef('id',0)
    if(lId>0){
      hsRes.trip=Trip.get(lId)
      hsRes.date_start=String.format('%td/%<tm/%<tY',hsRes.trip.fromdate)
      hsRes.date_end=String.format('%td/%<tm/%<tY',hsRes.trip.todate)
      hsRes.date_bron=String.format('%td/%<tm/%<tY %<tH:%<tM',hsRes.trip.inputdate)
      hsRes.home=Home.read(hsRes.trip.home_id)                        
      hsRes.cancellation=Rule_cancellation.get(hsRes.trip?.rule_cancellation_id)
      hsRes.timein=Rule_timein.get(hsRes.home?.rule_timein_id?:0)['name'+hsRes.context?.lang]
      hsRes.timeout=Rule_timeout.get(hsRes.home?.rule_timeout_id?:0)['name'+hsRes.context?.lang]
      hsRes.own=User.findWhere(client_id:hsRes.home?.client_id)      
      hsRes.ownClient=Client.get(hsRes.home?.client_id?:0)
      hsRes.reserve = Reserve.get(hsRes.ownClient?.reserve_id?:0)      
      hsRes.homeperson=Homeperson.get(hsRes.trip?.homeperson_id)['name'+hsRes.context?.lang]
      hsRes.payorder=Payorder.get(hsRes.trip?.payorder_id)
      
      if(hsRes.context.lang){
        hsRes.home=hsRes.home.csiSetEnHome()                                            
        hsRes.own=hsRes.own.csiSetEnUser()
      }
    }
    return hsRes
  }

  def previous = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)	

    hsRes.imageurl = grailsApplication.config.urluserphoto  
    hsRes.urlphoto = grailsApplication.config.urlphoto
    hsRes.homeperson=Homeperson.list()
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol

    def lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id
    def oTrip = new TripSearch()
    hsRes.data=oTrip.csiGetTrip(hsRes.user?.id,lSocId?:0,3,20,requestService.getOffset())
    
    if(hsRes.context.lang){      
      def lsRecords=[]
      for(record in hsRes.data.records){
        def oTripSearchTmp=new TripSearch()        
        
        record.properties.each { prop, val ->
          if(["metaClass","class"].find {it == prop}) return
          if(oTripSearchTmp.hasProperty(prop)) oTripSearchTmp[prop] = val
        }
        oTripSearchTmp.id=record.id
        oTripSearchTmp.home_name=Tools.transliterate(oTripSearchTmp.home_name,0)
        oTripSearchTmp.home_address=Tools.transliterate(oTripSearchTmp.home_address,0)
        oTripSearchTmp.home_city=City.findByName(oTripSearchTmp.home_city)?.name_en?:Tools.transliterate(oTripSearchTmp.home_city,0)
        oTripSearchTmp.user_nickname=Tools.transliterate(oTripSearchTmp.user_nickname,0)                            
        
        lsRecords<<oTripSearchTmp             
      }  
      hsRes.data.records=lsRecords
    }
    
    
    requestService.setStatistic('pcservices',44)
    return hsRes
  }
  def favorite ={
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if(request.getHeader("User-Agent")?.contains('Mobile')&&!request.getHeader("User-Agent")?.contains('iPad'))
      redirect(uri:'/favorites',base:hsRes.context?.mobileURL_lang,permanent:true)    
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    requestService.setStatistic('pcservices',39)
    return hsRes
  }

  def rating = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
	
    hsRes+=requestService.getParams(['rating'],['id'],['act'])
    def lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id
    def oTrip = Trip.get(hsRes.inrequest.id?:0)
    if (oTrip && (oTrip.user_id==hsRes.user?.id || oTrip.user_id==lSocId)){
      oTrip.rating = (hsRes.inrequest.rating>=0&&hsRes.inrequest.rating<=5)?hsRes.inrequest.rating:0
      oTrip.save(flush:true)
    }

    redirect(controller:'trip', action:hsRes.inrequest.act?:'next', base:hsRes.context.mainserverURL_lang)
    return
  }

  def cancellation = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return

    hsRes+=requestService.getParams(null,['id'],['comments'])
    def lSocId = 0
    if (hsRes.user?.is_external)
      lSocId = User.findByRef_id(hsRes.user?.id)?.id
    hsRes.trip = Trip.get(hsRes.inrequest.id?:0)
    if (!(hsRes.trip && (hsRes.trip.user_id==hsRes.user?.id || hsRes.trip.user_id==lSocId))){
      redirect(controller:'trip', action:'next', base:hsRes.context.mainserverURL_lang)
      return
    }
    hsRes.cancellation=Rule_cancellation.get(hsRes.trip?.rule_cancellation_id)
	  hsRes.textlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)	

    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def search_table = { 
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,false,true)
    if (!checkUser(hsRes)) return
    hsRes.inrequest=[:]
    hsRes.count=0
    hsRes.inrequest.max=Tools.getIntVal(grailsApplication.config.search.listing.max,30)
    if(hsRes.wallet){    	
      hsRes.modstatus=Homemodstatus.list()
      hsRes.hometype=Hometype.list([sort:'id']) 
      hsRes.homeperson=Homeperson.list()
      hsRes.homeoption=Homeoption.findAllByFacilitygroup_id(1,[sort:'id'])
      def oHomeSearch=new HomeSearch()	
      hsRes+=oHomeSearch.csiFindWallet(hsRes.wallet,hsRes.inrequest.max,requestService.getOffset())
      
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
      
      hsRes.urlphoto = grailsApplication.config.urlphoto   
      def oValutarate = new Valutarate()
      hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
      hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    }
    return hsRes
  }
////////////////////////////////////////////////////////////////////////////////////////////
  def waiting = {
	requestService.init(this)
  def hsRes=requestService.getContextAndDictionary(false,true,true)
  if (!checkUser(hsRes)) return
  hsRes+=init(hsRes)
  hsRes.tripmenu=Infotext.findAllByItemplate_id(6,[sort:'npage',order:'asc'])
	//hsRes.user = User.get(hsRes.user?.id)
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
    hsRes.hometype=Hometype.list([sort:'regorder'])
    hsRes.homeperson=Homeperson.list()
	  hsRes.country=Country.list([sort:'regorder'])
	  hsRes.textlimit = Tools.getIntVal(grailsApplication.config.largetext.limit,5000)
	  hsRes.stringlimit = Tools.getIntVal(grailsApplication.config.smalltext.limit,220)
	  hsRes.timetodecide=Timetodecide.findAllByDaysLessThan(31)
    hsRes.valuta=Valuta.findAllByModstatus(1,[sort:'regorder'])
	
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
    
    if(hsRes.inrequest?.country_id?:0){
      hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.inrequest?.country_id])
    }else if((hsRes.country?:[]).size()>0)
  	  hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])

    if(!bSave)
      requestService.setStatistic('waiting_protected')
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
      if (!hsRes.inrequest.email) {
        flash.error<<101        
      }else if (!Tools.checkEmailString(hsRes.inrequest.email)){
        flash.error<<103        
      } else if (User.findWhere(email:hsRes.inrequest.email?:'')){
        // old user. need avtorization
        flash.error<<100          
      }
      if (!hsRes.inrequest.nickname)     
        flash.error<<102
    }
    //<<end of validating

      if (!flash.error && !hsRes.user) {
        //new user registration>>
        hsRes.isNeedSecondMail = true
        def oUser = new User()
        if (oUser.mboxNewUser([email:hsRes.inrequest.email,nickname:hsRes.inrequest.nickname])) {
          if (usersService.loginInternalUser(hsRes.inrequest.email,Tools.hidePsw(hsRes.inrequest.email)[0..8],requestService,1)){
            hsRes.user = User.findByName(hsRes.inrequest.email)
          }
        }
        //<<new User registration
      }
      
  	if(!(flash.error.size())){
  		if (!hsRes.user.email){
  			hsRes.user.email = hsRes.inrequest.email
  			if (!hsRes.user.code) {
  				hsRes.user.code=java.util.UUID.randomUUID().toString()
  			}
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
    requestService.setStatistic('waiting_protected',28)
    redirect(action:'waiting',params:hsRes.inrequest, base:hsRes.context.mainserverURL_lang)
    return
  }
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////trip documents//////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
  def paydocuments = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return

    hsRes+=requestService.getParams(null,['id'])
    hsRes.trip = Trip.get(hsRes.inrequest?.id?:0)
    if (!hsRes.trip) {
      redirect(action:'current', base:hsRes.context.mainserverURL_lang)
      return
    }
    hsRes.client = Client.get(Home.get(hsRes.trip.home_id)?.client_id)

    return hsRes
  }
  def tripdocprint = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return

    hsRes+=requestService.getParams(['type'],['trip_id'],['payer','payeraddress','payerinn','payeraccount','payprice'])
    hsRes.trip = Trip.get(hsRes.inrequest?.trip_id?:0)
    if (!hsRes.trip) {
      redirect(action:'current', base:hsRes.context.mainserverURL_lang)
      return
    }
    hsRes.client = Client.get(Home.get(hsRes.trip.home_id)?.client_id)

    return hsRes
  }
}
