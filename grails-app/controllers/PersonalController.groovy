import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.converters.JSON

import org.springframework.web.servlet.support.RequestContextUtils as RCU
import org.springframework.context.i18n.LocaleContextHolder as LCH

class PersonalController {
  def requestService
  def imageService
  def billingService
  def ruToEnService
  def iRubId=857 //rub valuta_id for price conversion 
  def static final DATE_FORMAT='yyyy-MM-dd'
  
  def checkUser(hsRes) {   
    if(!hsRes?.user){
      response.sendError(401)
      return false
    }
	  def oTemp_notification=Temp_notification.findWhere(id:1,status:1)	  
	  session.attention_message=oTemp_notification?oTemp_notification.text:null	  
    return true
  }
 
  def init(hsRes){   
    //checkUser(hsRes)
    def hsTmp=findClientId(hsRes)
    hsTmp.imageurl = ConfigurationHolder.config.urlphoto + hsTmp.client_id.toString()+'/'
    hsTmp.textlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
    hsTmp.stringlimit = Tools.getIntVal(ConfigurationHolder.config.smalltext.limit,220)
    
    hsTmp.user = User.read(hsRes.user?.id)
    
    if(hsRes.context.lang){      
      hsTmp.user = hsTmp.user.csiSetEnUser()      
    }
    
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
    hsTmp.adsmenu=Infotext.findAllByItemplate_id(5,[sort:'npage',order:'asc'])
    
    return hsTmp
  }
  def findClientId(hsRes){    
    def hsTmp=[:]
    hsTmp.client_id=0.toLong()	
    if(hsRes?.user?.client_id)
      hsTmp.client_id=hsRes?.user?.client_id	
    return hsTmp
  }
  def getHome(iClient_id,lId){//ToDO with session??
    def hsRes=[:]
    hsRes.home=Home.findWhere(client_id:iClient_id,id:lId)    
    if (hsRes.home){
      hsRes.homemodstatus=Homemodstatus.findByModstatus(hsRes.home.modstatus)
      hsRes.comments=Ucomment.findAll('FROM Ucomment WHERE home_id=:home_id AND comstatus=1 AND rating=2 ORDER BY comdate',[home_id:lId])
    }
    return hsRes
  }  
  def index = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
   
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto  
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    hsRes.homeperson=Homeperson.list()
    hsRes.answertype=Answertype.list()
  
    hsRes.countcomms = 0
    hsRes.comments=[]
    hsRes.data=[records:[],count:0]
	
    if(hsRes.client_id){
      //def oHome=new Home()
      //hsRes.data=oHome.getHome(hsRes.client_id,hsRes.inrequest?.modstatus?:0,20,requestService.getOffset())
      def oUcommentSearch = new UcommentSearch()
      hsRes.comments=oUcommentSearch.csiSelectUcommentsForMyHomes(hsRes.client_id?:0,2,1,requestService.getOffset())
    }
    def oMboxSearch=new MboxSearch()
    hsRes.msg_unread=oMboxSearch.csiGetMbox(hsRes.user?.id,0,-1,Tools.getIntVal(ConfigurationHolder.config.pc.msg_unread_display.max,10),0,1)//TODO:paginate??
    hsRes.sys_message = Note.findAllByUser_idAndModstatus(hsRes.user?.id,1,[sort:'inputdate',order:'desc',max:Tools.getIntVal(ConfigurationHolder.config.pc.sys_message_display.max,10)])
	
    requestService.setStatistic('pc')
    return hsRes	
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////ads>>>///////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////  
  def ads={
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def oHome=new Home()      
    hsRes.status_count = []
    for(status in [0,1,2])
      hsRes.status_count << oHome.csiGetHome(hsRes.client_id,status,20,requestService.getOffset()).count?:0

    requestService.setStatistic('pcservices',41)
    return hsRes
  }
  def ads_list={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    hsRes.inrequest = requestService.getParams(['modstatus']).inrequest
    hsRes.data=[records:[],count:0]

    if(hsRes.client_id){
      def oHome=new Home()
      hsRes.data=oHome.csiGetHome(hsRes.client_id,hsRes.inrequest?.modstatus?:0,20,requestService.getOffset())
      
      if(hsRes.context?.lang){
        def lsHomes=[]
        for(home in hsRes.data.records){                                      
          lsHomes<<home.csiSetEnHome()
        }
        hsRes.data.records=lsHomes
      }
      
      hsRes.modstatus=Homemodstatus.list()
      hsRes.homeperson=Homeperson.list()
      hsRes.hometype=Hometype.list()
    }
    return hsRes
  }
  def editads={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    flash.error=[]	
    def lId=requestService.getLongDef('id',0)	
    if(lId){	  
      def bSave=requestService.getLongDef('save',0)
      if(bSave){
        hsRes+=requestService.getParams(['hometype_id','homeclass_id','homeperson_id','homeroom_id','homebath_id','is_parking','is_kitchen','is_tv','is_internet','is_wifi','is_nosmoking','is_holod',
          'is_microwave','is_cond','is_family','is_pets','is_invalid','is_heat','is_wash','is_breakfast','is_visa',
          'is_swim','is_steam','is_gym','is_hall','is_area','is_beach','is_fiesta',
          'is_iron','is_fen','is_kettle','is_coffee','is_hometheater','is_jacuzzi','is_renthour'],['id'],['name','description','homeoption'])
        hsRes.inrequest.area=requestService.getIntDef('area',0)
        hsRes.inrequest.bed=requestService.getIntDef('bed',0)
        hsRes.inrequest.remarks=requestService.getStr('remarks')
		
        hsRes.save=1
      }
      hsRes+=getHome(hsRes.client_id,lId)	  
      if(hsRes.home){
        if(!bSave)
          hsRes.inrequest=hsRes.home		
        hsRes.hometype=Hometype.findAll('FROM Hometype ORDER BY name')
        hsRes.homeperson=Homeperson.list()
        hsRes.homeroom=Homeroom.list()
        hsRes.homebath=Homebath.list()

        /*hsRes.homeoption_name=['is_nosmoking','is_parking','is_visa','is_tv','is_internet','is_wifi','is_cond',
          'is_heat','is_kitchen','is_holod','is_microwave','is_wash','is_breakfast','is_swim','is_steam','is_gym',
          'is_hall','is_family','is_pets','is_invalid','is_area','is_beach',
          'is_iron','is_fen','is_kettle','is_coffee','is_hometheater','is_jacuzzi']*/          
          
        hsRes.homeoption=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=1 ORDER BY id')
        hsRes.homeoption_all=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=2 ORDER BY regorder, name')
        hsRes.homeoption_kitchen=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=3 ORDER BY regorder, name')
        hsRes.homeoption_bania=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=4 ORDER BY regorder, name')
        hsRes.homeoption_kids=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=5 ORDER BY regorder, name')
        requestService.setStatistic('edithome',9,hsRes.home.id)
      }else{
        response.sendError(404)
        return
      }
    }else{
      response.sendError(404)
      return
    }
    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////////////  
  def saveads={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes+=requestService.getParams(['hometype_id','homeperson_id','homeroom_id','homebath_id','is_parking','is_kitchen','is_tv','is_internet','is_wifi','is_nosmoking','is_holod',
      'is_microwave','is_cond','is_family','is_pets','is_invalid','is_heat','is_wash','is_breakfast','is_visa',
      'is_swim','is_steam','is_gym','is_hall','is_area','is_beach','is_fiesta',
      'is_iron','is_fen','is_kettle','is_coffee','is_hometheater','is_jacuzzi','is_renthour'],['id'],['name','description']) 
    def lsDirectory=requestService.getParams(['hometype_id','homeperson_id','homeroom_id','homebath_id']).inrequest
    hsRes.inrequest.area=requestService.getIntDef('area',0)
    hsRes.inrequest.bed=requestService.getIntDef('bed',0)
    hsRes.inrequest.remarks=requestService.getStr('remarks')

    flash.save_error=[]

    if((lsDirectory?:[]).size()!=4)
      flash.save_error<<10
    if(!(hsRes.inrequest?.name?:''))
      flash.save_error<<1
    if(!(hsRes.inrequest?.description?:''))
      flash.save_error<<2    
    if(!(hsRes.inrequest?.bed?:0) && requestService.getStr('bed').size()>0)
      flash.save_error<<3
    else if(!((hsRes.inrequest?.bed?:0) in 0..100))
      flash.save_error<<3
    if(!(hsRes.inrequest?.area?:0) && requestService.getStr('area').size()>0)
      flash.save_error<<4
    else if(!((hsRes.inrequest?.area?:0) in 0..32767))
      flash.save_error<<4
    //option>>  
    def homeoption=''
	  def i=0	  
	  def lsHomeoption_all=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=2 ORDER BY regorder, name')	  	  
	  for(option in lsHomeoption_all){
	    if(requestService.getLongDef('homeoption_all'+i,0))		  
	      if(homeoption)		  
          homeoption+=','+option.id
        else
          homeoption+=option.id
      i++
    }
    def lsHomeoption_kitchen=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=3 ORDER BY regorder, name')	  	  
    i=0	  
    for(option in lsHomeoption_kitchen){
      if(requestService.getLongDef('homeoption_kitchen'+i,0))		  
        if(homeoption)		  
          homeoption+=','+option.id
        else
          homeoption+=option.id
      i++
    }
    def lsHomeoption_bania=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=4 ORDER BY regorder, name')	  	  
    i=0	  
    for(option in lsHomeoption_bania){
      if(requestService.getLongDef('homeoption_bania'+i,0))		  
        if(homeoption)		  
          homeoption+=','+option.id
        else
          homeoption+=option.id
      i++
    }
    def lsHomeoption_kids=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=5 ORDER BY regorder, name')	  	  
    i=0	  
    for(option in lsHomeoption_kids){
      if(requestService.getLongDef('homeoption_kids'+i,0))		  
        if(homeoption)		  
          homeoption+=','+option.id
        else
          homeoption+=option.id
      i++
    }	  
    //<<option	            	  
    //>>process business  
    if(!(flash.save_error.size())){        	 	        	  
      def oHome=Home.findWhere(client_id:hsRes.client_id,id:hsRes.inrequest?.id?:0)
      if(oHome){
		if (hsRes.inrequest?.description.size()>hsRes.textlimit) hsRes.inrequest?.description = hsRes.inrequest?.description.substring(0, hsRes.textlimit)
		if ((hsRes.inrequest?.remarks?:'').size()>hsRes.textlimit) hsRes.inrequest?.remarks = hsRes.inrequest?.remarks.substring(0, hsRes.textlimit)
        //owner Dmitry>>
        if(oHome.name!=hsRes.inrequest?.name || oHome.description!=hsRes.inrequest?.description||
          oHome.area!=(hsRes.inrequest?.area?:0) || oHome.bed!=(hsRes.inrequest?.bed?:0)||
          oHome.remarks!=(hsRes.inrequest?.remarks?:'')) {
          oHome.is_confirmed = 0
        }
        //owner Dmitry<<
        if (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false) {
          if (hsRes.inrequest.description.replace('(','').replace(')','').replace('-','').replaceAll("\\s","").matches('.*[0-9]{7,}.*')||hsRes.inrequest.description.matches('.*\\S+@\\S*.*')||hsRes.inrequest.description.matches('.*\\S*@\\S+.*')) {
            hsRes.inrequest.description = (hsRes.inrequest.description?:'').replaceAll('[0-9( )-]{7,}',' [номер] ').replaceAll('\\S+@\\S*','[email]').replaceAll('\\S*@\\S+','[email]').trim()
          }
        }
        oHome.name=hsRes.inrequest?.name
        oHome.description=hsRes.inrequest?.description
        oHome.hometype_id=hsRes.inrequest?.hometype_id
        oHome.homeperson_id=hsRes.inrequest?.homeperson_id
        oHome.homeroom_id=hsRes.inrequest?.homeroom_id
        oHome.homebath_id=hsRes.inrequest?.homebath_id
        oHome.is_fiesta=hsRes.inrequest?.is_fiesta?:0

        oHome.area=hsRes.inrequest?.area?:0
        oHome.bed=hsRes.inrequest?.bed?:0
        oHome.remarks=hsRes.inrequest?.remarks?:''

        oHome.is_parking=hsRes.inrequest?.is_parking?:0
        oHome.is_kitchen=hsRes.inrequest?.is_kitchen?:0
        oHome.is_tv=hsRes.inrequest?.is_tv?:0
        oHome.is_internet=hsRes.inrequest?.is_internet?:0
        oHome.is_wifi=hsRes.inrequest?.is_wifi?:0
        oHome.is_nosmoking=hsRes.inrequest?.is_nosmoking?:0
        oHome.is_holod=hsRes.inrequest?.is_holod?:0
        oHome.is_microwave=hsRes.inrequest?.is_microwave?:0
        oHome.is_cond=hsRes.inrequest?.is_cond?:0
        oHome.is_family=hsRes.inrequest?.is_family?:0
        oHome.is_pets=hsRes.inrequest?.is_pets?:0
        oHome.is_invalid=hsRes.inrequest?.is_invalid?:0
        oHome.is_heat=hsRes.inrequest?.is_heat?:0
        oHome.is_wash=hsRes.inrequest?.is_wash?:0
        oHome.is_breakfast=hsRes.inrequest?.is_breakfast?:0
        oHome.is_visa=hsRes.inrequest?.is_visa?:0
        oHome.is_swim=hsRes.inrequest?.is_swim?:0
        oHome.is_steam=hsRes.inrequest?.is_steam?:0
        oHome.is_gym=hsRes.inrequest?.is_gym?:0
        oHome.is_hall=hsRes.inrequest?.is_hall?:0
        oHome.is_area=hsRes.inrequest?.is_area?:0
        oHome.is_beach=hsRes.inrequest?.is_beach?:0		
        oHome.homeoption=homeoption
        oHome.is_step_descr=1
        
        oHome.is_iron=hsRes.inrequest?.is_iron?:0
        oHome.is_fen=hsRes.inrequest?.is_fen?:0
        oHome.is_kettle=hsRes.inrequest?.is_kettle?:0
        oHome.is_coffee=hsRes.inrequest?.is_coffee?:0
        oHome.is_hometheater=hsRes.inrequest?.is_hometheater?:0
        oHome.is_jacuzzi=hsRes.inrequest?.is_jacuzzi?:0
        oHome.is_renthour=hsRes.inrequest?.is_renthour?:0        
 
        if(!oHome.save(flush:true)) {
          log.debug(" Error on save home:")
          oHome.errors.each{log.debug(it)}
          flash.save_error<<101            
        }
        updateFulladressAndInfo(oHome.id)//owner Dmitry
      }
    }

    hsRes.inrequest.homeoption=homeoption
    hsRes.inrequest.save=1
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////ads<<<//////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////map>>>/////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////    
  def map={  
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)

    def lId=requestService.getLongDef('id',0)
    if(lId){    
      hsRes+=getHome(hsRes.client_id,lId)
      def oHome=hsRes.home
      if(oHome){  
        hsRes.country=Country.findAll('FROM Country ORDER BY regorder')
        
        if(oHome.country_id?:0)
          hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:oHome.country_id])
        else if((hsRes.country?:[]).size()>0)
          hsRes.region=Region.findAll('FROM Region WHERE country_id=:country_id ORDER BY regorder DESC, name',[country_id:hsRes.country[0].id])
          
        hsRes.curregion=Region.get(oHome.region_id?:0.toLong)
	    
        if(hsRes.curregion.is_metro){	    
          hsRes.metro=Metro.findAll('FROM Metro WHERE region_id=:region_id AND modstatus=1 ORDER BY name',[region_id:hsRes.curregion.id])
          hsRes.metros=''		
          hsRes.metro_ids=[]
          for(oHomemetro in Homemetro.findAllWhere(home_id:lId)){
            hsRes.metro_ids<<oHomemetro?.metro_id
            def oMetro=Metro.get(oHomemetro?.metro_id)		
            if(oMetro)        
              hsRes.metros+=oMetro['name'+hsRes.context?.lang]+'\n'		  		  
          }
        }
 
          
        hsRes.inrequest=oHome
        hsRes.hinrequest=[:]
        hsRes.hinrequest.city=City.get(oHome.city_id)?(City.get(oHome.city_id)['name'+hsRes.context?.lang]?:oHome.city):oHome.city
        hsRes.hinrequest.street=Street.get(oHome.street_id)?(Street.get(oHome.street_id)['name'+hsRes.context?.lang]?:oHome.street):oHome.street
        hsRes.hinrequest.district=District.get(oHome.district_id)?(District.get(oHome.district_id)['name'+hsRes.context?.lang]?:oHome.district):oHome.district
        hsRes.inrequest.address=oHome.address
        requestService.setStatistic('edithome',11,hsRes.home.id)
      } else {
        response.sendError(404)
        return
      }
    } else {
	    response.sendError(404)
	    return
	  }

    return hsRes	
  }
  /////////////////////////////////////////////////////////////////////////////////////////// 
  def savemap={   
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes+=requestService.getParams(['country_id','region_id','pindex'],['id','x','y'],['city','district','street','homenumber','spcf','mapkeywords','reachpersonal','reachpublic'])    	
         
    def lsDirectory=requestService.getParams(['country_id','region_id']).inrequest
   
    flash.save_error=[]
   
    if((lsDirectory?:[]).size()!=2)
      flash.save_error<<10	
    if (!(hsRes.inrequest?.pindex?:0) && requestService.getStr('pindex').size()>0)
      flash.save_error<<1
    if(!(hsRes.inrequest?.x?:0))
      flash.save_error<<3
    if(!(hsRes.inrequest?.y?:0))
      if(!flash.save_error.find{it==3})
        flash.save_error<<3
    if (!hsRes.inrequest.city&&(!Region.get(hsRes.inrequest.region_id)||Region.get(hsRes.inrequest.region_id).is_show)) {
      flash.save_error<<4
    }

    if(!(flash.save_error.size())){        	 	        	  
      def oHome=Home.findWhere(client_id:hsRes.client_id,id:hsRes.inrequest?.id?:0)
      if(oHome){
        if ((hsRes.inrequest?.mapkeywords?:'').size()>hsRes.stringlimit) hsRes.inrequest?.mapkeywords = hsRes.inrequest?.mapkeywords.substring(0, hsRes.stringlimit)
        if ((hsRes.inrequest?.reachpersonal?:'').size()>hsRes.textlimit) hsRes.inrequest?.reachpersonal = hsRes.inrequest?.reachpersonal.substring(0, hsRes.textlimit)
        if ((hsRes.inrequest?.reachpublic?:'').size()>hsRes.textlimit) hsRes.inrequest?.reachpublic = hsRes.inrequest?.reachpublic.substring(0, hsRes.textlimit)
        //owner Dmitry>>
        if ((oHome.city!=(hsRes.inrequest?.city?:'')) || (oHome.street!=(hsRes.inrequest?.street?:''))||
          (oHome.district!=(hsRes.inrequest?.district?:'')) || (oHome.pindex!=(hsRes.inrequest?.pindex?hsRes.inrequest.pindex.toString():''))||
          (oHome.homenumber!=(hsRes.inrequest?.homenumber?:'0')) || (oHome.mapkeywords!=(hsRes.inrequest?.mapkeywords?:''))||
          (oHome.reachpersonal!=(hsRes.inrequest?.reachpersonal?:'')) || (oHome.reachpublic!=(hsRes.inrequest?.reachpublic?:''))||
          (oHome.x!=hsRes.inrequest?.x)||(oHome.y!=hsRes.inrequest?.y)) {
          oHome.is_confirmed = 0
          oHome.shortaddress=Country.get(hsRes.inrequest?.country_id).name+' '+Region.get(hsRes.inrequest?.region_id).name+(((Region.get(hsRes.inrequest?.region_id).name!=hsRes.inrequest?.city)&&hsRes.inrequest?.city)?(' '+hsRes.inrequest?.city):'')+((hsRes.inrequest?.street?:'')?(' '+hsRes.inrequest?.street):'')+((hsRes.inrequest?.homenumber?:'')?(' '+hsRes.inrequest?.homenumber):'')//owner Dmitry<<
        }
        //owner Dmitry<<
        oHome.country_id=hsRes.inrequest?.country_id
        oHome.region_id=hsRes.inrequest?.region_id
        oHome.city=hsRes.inrequest?.city?:Region.get(hsRes.inrequest?.region_id).is_show==0?Region.get(hsRes.inrequest?.region_id).name:''
        oHome.city=oHome.city.trim()
        def cityName=new StringBuffer(oHome.city)
        cityName.setCharAt(0, Character.toUpperCase(cityName.charAt(0)))
        oHome.city=cityName
        oHome.street=hsRes.inrequest?.street?:''
        oHome.district=hsRes.inrequest?.district?:''
        oHome.address=Country.get(hsRes.inrequest?.country_id).name+' '+Region.get(hsRes.inrequest?.region_id).name+(((Region.get(hsRes.inrequest?.region_id).name!=hsRes.inrequest?.city)&&hsRes.inrequest?.city)?(' '+hsRes.inrequest?.city):'')+((hsRes.inrequest?.street?:'')?(' '+hsRes.inrequest?.street):'')+((hsRes.inrequest?.homenumber?:'')?(' '+hsRes.inrequest?.homenumber):'')		
        oHome.pindex=hsRes.inrequest?.pindex?hsRes.inrequest.pindex.toString():''
        oHome.homenumber=hsRes.inrequest?.homenumber?:''
        oHome.spcf=hsRes.inrequest?.spcf?:''
        oHome.geostatus=1
        oHome.x=hsRes.inrequest?.x
        oHome.y=hsRes.inrequest?.y
        oHome.mapkeywords=hsRes.inrequest?.mapkeywords?:''
        oHome.reachpersonal=hsRes.inrequest?.reachpersonal?:''
        oHome.reachpublic=hsRes.inrequest?.reachpublic?:''
        oHome.is_step_map=1
        if (oHome.district){
          if(hsRes.context?.lang)
            oHome.district_id = District.findByRegion_idAndName_en(oHome.region_id, oHome.district)?.id?:0
          else  
            oHome.district_id = District.findByRegion_idAndName(oHome.region_id, oHome.district)?.id?:0
        }else 
          oHome.district_id = 0
        if (oHome.city) {          
          if(hsRes.context?.lang)
            oHome.city_id = City.findByName_en(oHome.city)?.id?:0
          else
            oHome.city_id = City.findByName(oHome.city)?.id?:0          
        } else 
          oHome.city_id = 0
        if (oHome.street) {
          if(hsRes.context?.lang)
            oHome.street_id = Street.findByRegion_idAndName_en(oHome.region_id, oHome.street)?.id?:0
          else
            oHome.street_id = Street.findByRegion_idAndName(oHome.region_id, oHome.street)?.id?:0
        } else 
          oHome.street_id = 0
 
        if(!oHome.save(flush:true)) {
          log.debug(" Error on save home:")
          oHome.errors.each{log.debug(it)}
          flash.save_error<<101            
        }	
        updateFulladressAndInfo(oHome.id)//owner Dmitry
      }
   
      if(!flash.save_error){        
        def lsHomemetro=[]	
        hsRes.metro=[]
        if(session.metrolist){		  
          for(metrolist in session.metrolist)
            if(metrolist?.home_id==hsRes.inrequest?.id)		
              hsRes.metro=metrolist?.metros
          session.metrolist=[]    
              
	
          for(oHomemetro in Homemetro.findAllWhere(home_id:hsRes.inrequest?.id)){
            if(oHomemetro.metro_id.toLong() in hsRes.metro)
              lsHomemetro<<oHomemetro.metro_id.toLong()					  
            else{		 
              oHomemetro.delete(flush:true)		   
            }
          }
      
          for(metro_id in hsRes.metro){
            if(metro_id in lsHomemetro){            	  
            } else if(metro_id!=''){		  
              def oHomemetro=new Homemetro()
              oHomemetro.home_id=hsRes.inrequest?.id			
              oHomemetro.metro_id=metro_id.toInteger()
              if(!oHomemetro.save(flush:true)) {
                log.debug(" Error on save homemetro:")
                oHomemetro.errors.each{log.debug(it)}        		
              }		        
            }		  		
          }
        }        
        updateFulladressAndInfo(hsRes.inrequest?.id)//owner Dmitry
      }	  
    }
    //hsRes.inrequest.save=1
    //redirect(controller:'personal',action:'map',params:hsRes.inrequest,base:hsRes.context.sequreServerURL)
    
    def hsOut=[save_error:flash.save_error]  
    render hsOut as JSON    
    return
  } 
  
  def addmetro={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId=requestService.getLongDef('home_id',0)

    if(getHome(hsRes.client_id,lId).home){
      def metro_ids=requestService.getList('metros')	 	      	 
      if(!session.metrolist)
        session.metrolist=[]
      else {
        def i=0
        for(metrolist in session.metrolist){
          if(metrolist?.home_id==lId)
            session.metrolist[i]=null
          i++
        }
      }
      session.metrolist<<[home_id:lId,metros:metro_ids]	  	  
    
      hsRes.metros=''	
      def oMetro=[:]
      for(metro in metro_ids){
        if(metro!=''){          
          oMetro=Metro.get(metro)
          if(oMetro)
            hsRes.metros+=oMetro['name'+hsRes?.context?.lang]+'\n'	    
        }	  
      }
    }
    return hsRes
  }
  
  def get_metro={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)  
    //hsRes+=init(hsRes)
    def lId=requestService.getLongDef('home_id',0)
    def iRegId=requestService.getIntDef('region_id',0)	
    hsRes.metro_ids=[]	
    if(Region.get(iRegId)?.is_metro){	
      hsRes.metro=Metro.findAll('FROM Metro WHERE region_id=:region_id AND modstatus=1 ORDER BY name',[region_id:iRegId])		  
      if(getHome(hsRes?.user?.client_id,lId).home){
        for(oHomemetro in Homemetro.findAllWhere(home_id:lId,modstatus:1))
          hsRes.metro_ids<<oHomemetro?.metro_id		 
      }
    }
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////map<<<//////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////   
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////photo>>>////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def photo={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId=requestService.getLongDef('id',0)		
    hsRes+=getHome(hsRes.client_id,lId)
    if(hsRes.home){	  	 
      hsRes.homephoto=Homephoto.findAll('FROM Homephoto WHERE home_id=:home_id ORDER BY norder',[home_id:lId])	  
    }else{
      response.sendError(404)
      return
    } 	
    requestService.setStatistic('edithome',10,hsRes.home.id)
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  def homephoto={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.id = requestService.getLongDef('id',0)
    hsRes.home_id = requestService.getLongDef('home_id',0)
	hsRes.i = 1
	hsRes.maxI = Tools.getIntVal(ConfigurationHolder.config.photomultiupload.max,4)
    imageService.init(this,'homephotopic'+hsRes.id,'homephotokeeppic'+hsRes.id,ConfigurationHolder.config.pathtophoto+hsRes.client_id.toString()+File.separatorChar) // 0
    imageService.startFileSession() // 1
	imageService.finalizeFileSession(null)
    if(hsRes.id>0){ 	  
      hsRes.homephoto = Homephoto.get(hsRes.id)      
      hsRes+=getHome(hsRes.client_id,hsRes.homephoto?.home_id?:0.toLong())
      if(hsRes.homephoto && hsRes.home){               
        hsRes.images=[:]
        if((hsRes.homephoto.picture?:'')!='')
          imageService.putIntoSessionFromDb(hsRes.homephoto.picture,'file1') // 2
        def hsPics=imageService.getSessionPics('file1') // 3
        if(hsPics!=null){ 
          hsRes.images['photo_1']=hsPics.photo
          hsRes.images['thumb_1']=hsPics.thumb
        }
      } else {
	    response.sendError(404)
		return
	  }
    } else {
	  if(hsRes.home_id)
		hsRes+=getHome(hsRes.client_id,hsRes.home_id)
	  else {
		response.sendError(404)
		return
	  }
    }
    return hsRes
  }
   /////////////////////////////////////////////////////////////////////////////////////////////////////////
  def homephotoadd={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)

    imageService.init(this,'homephotopic'+lId,'homephotokeeppic'+lId,ConfigurationHolder.config.pathtophoto+hsRes.client_id.toString()+File.separatorChar) // 0

    def hsPics
    def oHomephoto
	def bHomeSave
	def lHomeId = requestService.getLongDef('home_id',0)
	def lsFiles = []

	for (int i=1; i<=Tools.getIntVal(ConfigurationHolder.config.photomultiupload.max,4); i++){
	  hsPics=imageService.getSessionPics(('file'+i))
	  if (!hsPics)
		continue
	  lsFiles << ('file'+i)
      bHomeSave=false
      if(lId>0){
		oHomephoto = Homephoto.get(lId)
		if(oHomephoto){
          if(!Home.findWhere(client_id:hsRes.client_id,id:oHomephoto?.home_id?:0)){
			oHomephoto = null
          }else{
			lHomeId=oHomephoto.home_id
          }
		}
      }else{

		if(hsPics?.photo && lHomeId && Home.findWhere(client_id:hsRes.client_id,id:lHomeId)){
          oHomephoto = new Homephoto()
          oHomephoto.home_id=lHomeId
          oHomephoto.norder=(Homephoto.findAllWhere(home_id:oHomephoto.home_id)?:[]).size()+1
          if(Homephoto.findAllWhere(home_id:lHomeId))
			oHomephoto.is_main=0
          else
			oHomephoto.is_main=1
		}
      }
      if(oHomephoto){
		def oHome = Home.findWhere(client_id:hsRes.client_id,id:oHomephoto?.home_id?:0)
		//owner Dmitry>>
		if ((oHomephoto.picture!=((hsPics?.photo)?:''))||(oHomephoto.ptext!=requestService.getStr(('ptext'+i)))) {
          oHome?.is_confirmed = 0
          bHomeSave=true
		}
		//owner Dmitry<<
		oHomephoto.picture=(hsPics?.photo)?:oHomephoto.picture
		oHomephoto.modstatus=1
		oHomephoto.moddate=new Date()
		oHomephoto.ptext=requestService.getStr(('ptext'+i))
		def oNote = Note.findByHome_idAndNotetype_id(oHome.id,3)
		if (oNote && oNote.modstatus){
          if (oHomephoto.norder>=Notetype.get(3).param){
			oNote.modstatus=0
			if(!oNote.save(flush:true)) {
              log.debug(" Error on save Note(personal/homephotoadd/) id:"+oNote.id)
              oNote.errors.each { log.debug(it)}
			}
          }
		}
		try{
          if( !oHomephoto.save(flush:true)) {
			log.debug(" Error on save homephoto:")
			oHomephoto.errors.each{log.debug(it)}
          }
		}catch(Exception e){
          log.debug('Personal:homephotoadd. ERROR ON ADD Homephoto \n'+e.toString())
		}
		if(oHomephoto.is_main){
          oHome.mainpicture=oHomephoto.picture
          oHome.is_step_photo=1
          bHomeSave=true
		}
		if(bHomeSave){
          oHome.moddate = new Date()
          try{
			if( !oHome.save(flush:true)) {
              log.debug(" Error on save home:")
              oHome.errors.each{log.debug(it)}
			}
          }catch(Exception e){
			log.debug('Personal:home. ERROR ON ADD Home \n'+e.toString())
          }
		}
	  }
	}
	if(lId>0&&!lsFiles&&Homephoto.get(lId)){
	  imageService.putIntoSessionFromDb(Homephoto.get(lId).picture,'file1')
	  lsFiles << 'file1'
	}
    imageService.finalizeFileSession(lsFiles)
    redirect(action:'photo',params:[id:lHomeId],base:hsRes.context.mainserverURL_lang)
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  def homephotodelete={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    def lHomeId = requestService.getLongDef('home_id',0)
    if(lId>0){ 	  
      def oHomephoto = Homephoto.get(lId)
      if(oHomephoto&&Home.findWhere(client_id:hsRes.client_id,id:oHomephoto.home_id)){
        def tmpNorder = oHomephoto.norder
        def tmpHomephoto
        imageService.init(this,'','',ConfigurationHolder.config.pathtophoto+hsRes.client_id.toString()+File.separatorChar)    
        def lsPictures = []      
        lsPictures<<oHomephoto.picture              	  
        oHomephoto.delete(flush:true)
        imageService.deletePictureFilesFromHd(lsPictures)
        while (tmpNorder <= Homephoto.findAllByHome_id(lHomeId).size()){
          tmpHomephoto = Homephoto.findByHome_idAndNorder(lHomeId, ++tmpNorder)
          tmpHomephoto.norder = tmpNorder-1
          tmpHomephoto.save(flush:true)
        }
      }
    }  
    redirect(action:'photo',params:[id:lHomeId],base:hsRes.context.mainserverURL_lang)
  }  
  /////////////////////////////////////////////////////////////////////////////////////////
  def savepichomephoto={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
	def iNo = requestService.getLongDef('no',0)
    def bFlag=false
    if(lId>0){
      def oHomephoto = Homephoto.get(lId)
      if(Home.findWhere(client_id:hsRes.client_id,id:oHomephoto?.home_id?:0.toLong()))
        bFlag=true
	}else{
	  bFlag=true
	}	
	def hsData = [:]
	hsData.data=[]
	imageService.init(this,'homephotopic'+lId,'homephotokeeppic'+lId,ConfigurationHolder.config.pathtophoto+hsRes.client_id.toString()+File.separatorChar,"images","alpha.jpg","mask.jpg") // 0

	if(bFlag){
	  if(iNo==1){
		request.multiFileMap?.file1?.each { file ->
		  if (iNo<=Tools.getIntVal(ConfigurationHolder.config.photomultiupload.max,4)){
			//ЗАГРУЖАЕМ ГРАФИКУ
			hsData.data << imageService.loadMultiplePicture(
				file,
				Tools.getIntVal(ConfigurationHolder.config.photo.weight,2097152), //weight
				Tools.getIntVal(ConfigurationHolder.config.photo.image.size,639),  // size
				Tools.getIntVal(ConfigurationHolder.config.photo.thumb.size,100), //thumb size
				true,//SaveThumb
				false,//square		
				Tools.getIntVal(ConfigurationHolder.config.photo.image.height,426),//height
				Tools.getIntVal(ConfigurationHolder.config.photo.thumb.height,74),//thumb height			
				false,
				true,
				iNo
			) // 3
		  }
		  iNo++
		}
	  } else {
		hsData.data << imageService.loadPicture(
			"file"+(iNo?:1),
			Tools.getIntVal(ConfigurationHolder.config.photo.weight,2097152), //weight
			Tools.getIntVal(ConfigurationHolder.config.userphoto.image.size,210),  // size
			Tools.getIntVal(ConfigurationHolder.config.userphoto.thumb.size,50), //thumb size
			true,//SaveThumb
			false,//square		
			Tools.getIntVal(ConfigurationHolder.config.userphoto.image.height,210),//height
			Tools.getIntVal(ConfigurationHolder.config.userphoto.thumb.height,50),//thumb height			
			true,
			false
		) // 3

		hsData.data[0]['num']=iNo?:1 //<- НЕОБХОДИМО ДЛЯ КОРРЕКТНОЙ РАБОТЫ js в savepictureresult  
	  }
	  // savepictureresult ОБЩИЙ ШАБЛОН, ЕСЛИ ИСПОЛЬЗОВАТЬ СКРИПТЫ АНАЛОГИЧНО СДЕЛАННОМУ
	  render(view:'savepictureresult',model:hsData)
	  return
	}

    render(contentType:"application/json"){[error:true]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  def deletepichomephoto={
    //TODO: check user logged in
    requestService.init(this)	
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)

    //ОБЯЗАТЕЛЬНАЯ ИНИЦИАЛИЗАЦИЯ TODO: path into cfg
    imageService.init(this,'homephotopic'+lId,'homephotokeeppic'+lId,ConfigurationHolder.config.pathtophoto+hsRes.client_id.toString()+File.separatorChar)
    
    def sName=requestService.getStr("name")

    imageService.deletePicture(sName)//4
    render(contentType:"application/json"){[error:false]}
  }
  ///////////////////////////////////////////////////////////////////////////////////////////  
  def bigimage={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.image = requestService.getStr('picture')
    return hsRes
  }
  def set_main_photo={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    def oHomephoto=Homephoto.get(lId)
    hsRes.mainpicture=''
    if(oHomephoto){
      def oHome=Home.findWhere(client_id:hsRes.client_id,id:oHomephoto?.home_id)
      if(oHome){
        def oRrevMainHomephoto=Homephoto.findWhere(is_main:1,home_id:oHome.id)
        if(oRrevMainHomephoto){
          oRrevMainHomephoto.is_main=0
          if(!oRrevMainHomephoto.save(flush:true)) {
            log.debug(" Error on save homephoto:")
            oRrevMainHomephoto.errors.each{log.debug(it)}                   		  
          }else{
            oHomephoto.is_main=1
            if(!oHomephoto.save(flush:true)) {
              log.debug(" Error on save homephoto:")
              oHomephoto.errors.each{log.debug(it)}                   		  
            }else{
              oHome.mainpicture=oHomephoto.picture
              if(!oHome.save(flush:true)) {
                log.debug(" Error on save home:")
                oHome.errors.each{log.debug(it)}                   		  
              }
            }
          }
          hsRes.prev_main_photo_id=oRrevMainHomephoto.id
          hsRes.mainpicture=oHome.mainpicture
          hsRes.cur_main_photo_id=oHomephoto.id
        } else {
          oHomephoto.is_main=1
          if(!oHomephoto.save(flush:true)) {
            log.debug(" Error on save homephoto:")
            oHomephoto.errors.each{log.debug(it)}
          }else{
            oHome.mainpicture=oHomephoto.picture
            oHome.is_step_photo=1
          }
          hsRes.mainpicture=oHome.mainpicture
          hsRes.cur_main_photo_id=oHomephoto.id
        }
        oHome.moddate = new Date()
        if(!oHome.save(flush:true)) {
          log.debug(" Error on save home:")
          oHome.errors.each{log.debug(it)}
        }		
      }
    }	
    return hsRes	
  }
  def sort_photo={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def getIds = requestService.getStringList('ids')
    def lsId=[]
    def oHomephoto
    for(photo in  getIds){
      lsId=photo.split('photo_')
      if((lsId?:[]).size()>1){
        for ( i in 1..<(lsId?:[]).size() ){
          oHomephoto=Homephoto.get(lsId[i].minus(',').toLong())
          if(oHomephoto && Home.findWhere(client_id:hsRes.client_id,id:oHomephoto?.home_id)){
            oHomephoto.norder=i
            try{
              if(!oHomephoto.save(flush:true)) {
                log.debug(" Error on save homephoto:")
                oHomephoto.errors.each{log.debug(it)}
              }  
            }catch(Exception e){
              log.debug(" Error on save homephoto:"+e.toString())
            }			  
          }
        }
      }
    }
    //redirect(action:'photo',params:[id:oHomephoto?.home_id?:0])
    render(contentType:"application/json"){[error:false]}//AJAX
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////photo<<<////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////  
  /////////////video>>>////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def video={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId=requestService.getLongDef('id',0)		
    hsRes+=getHome(hsRes.client_id,lId)
    if(hsRes.home){	  	 
      hsRes.homevideo=Homevideo.findAll('FROM Homevideo WHERE home_id=:home_id ORDER BY norder',[home_id:lId])	  
    }else{
      response.sendError(404)
      return
    } 	
    requestService.setStatistic('edithome',10,hsRes.home.id)
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  def homevideo={  
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.id = requestService.getLongDef('id',0)
    hsRes.home_id = requestService.getLongDef('home_id',0)
    hsRes+=getHome(hsRes.client_id,hsRes.home_id?:0.toLong())
    
    if(hsRes.id>0){ 	  
      hsRes.homevideo = Homevideo.get(hsRes.id)            
      if(hsRes.homevideo && hsRes.home)          
        hsRes.inrequest=hsRes.homevideo
	  else {
		response.sendError(404)
		return
	  }
    } else {
	  if(hsRes.home_id)
		hsRes+=getHome(hsRes.client_id,hsRes.home_id)
	  else {
		response.sendError(404)
		return
	  }
    }
    return hsRes
  }
   /////////////////////////////////////////////////////////////////////////////////////////////////////////
  def homevideoadd={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)    
    hsRes+=requestService.getParams([],['home_id'],['code','vtext'])
    	      
    def oHomevideo
    def lHomeId = 0
    def bHomeSave = false
    if(lId>0){ 	  
      oHomevideo = Homevideo.get(lId)
      if(oHomevideo){
        if(!Home.findWhere(client_id:hsRes.client_id,id:oHomevideo?.home_id?:0.toLong())){   
          oHomevideo = null		
        }else{
          lHomeId=oHomevideo.home_id		 
        }  
      }	  	  	
    }else{      	 
      if(hsRes.inrequest?.code && hsRes.inrequest?.home_id && Home.get(hsRes.inrequest?.home_id?:0.toLong())){
        oHomevideo = new Homevideo()
        oHomevideo.home_id=hsRes.inrequest?.home_id	          
        oHomevideo.norder=(Homevideo.findAllWhere(home_id:oHomevideo.home_id)?:[]).size()+1
        lHomeId=hsRes.inrequest?.home_id
      }
    }
    
    if(oHomevideo){
      def oHome = Home.findWhere(client_id:hsRes.client_id,id:oHomevideo?.home_id?:0.toLong())      
      if ((oHomevideo.code!=((hsRes.inrequest?.code)?:''))||(oHomevideo.vtext!=hsRes.inrequest?.vtext)) {	
        oHome?.is_confirmed = 0
        bHomeSave=true
      }      
      def lsCode=hsRes.inrequest.code.split('/')
      oHomevideo.code = 'http://www.youtube.com/v/' + lsCode[lsCode.length-1]	    
      oHomevideo.videoid=lsCode[lsCode.length-1]	  
      oHomevideo.modstatus=1
      oHomevideo.moddate=new Date()
      oHomevideo.vtext=hsRes.inrequest?.vtext
      def oNote = Note.findByHome_idAndNotetype_id(oHome.id,5)
      if (oNote && oNote.modstatus){
        if (oHomevideo.norder>=Notetype.get(5).param){
          oNote.modstatus=0
          if(!oNote.save(flush:true)) {
            log.debug(" Error on save Note(personal/homevideoadd/) id:"+oNote.id)
            oNote.errors.each { log.debug(it)}
          }
        }
      }
      try{  
        if( !oHomevideo.save(flush:true)) {
          log.debug(" Error on save homevideo:")    
          oHomevideo.errors.each{log.debug(it)}
        }
      }catch(Exception e){
        log.debug('Personal:homevideoadd. ERROR ON ADD Homevideo \n'+e.toString())
      }
      if(bHomeSave){
        oHome.moddate = new Date()
        try{  
          if( !oHome.save(flush:true)) {
            log.debug(" Error on save home:")    
            oHome.errors.each{log.debug(it)}
          }
        }catch(Exception e){
          log.debug('Personal:home. ERROR ON ADD Home \n'+e.toString())
        }
      }
    }
    redirect(action:'video',params:[id:lHomeId?:0],base:hsRes.context.mainserverURL_lang)
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  def homevideodelete={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    def lHomeId = requestService.getLongDef('home_id',0)
    if(lId>0){ 	  
      def oHomevideo = Homevideo.get(lId)
      if(oHomevideo && Home.findWhere(client_id:hsRes.client_id,id:oHomevideo.home_id)){
        def tmpNorder = oHomevideo.norder
        def tmpHomevideo        
        oHomevideo.delete(flush:true)
        while (tmpNorder <= Homevideo.findAllByHome_id(lHomeId).size()){
          tmpHomevideo = Homevideo.findByHome_idAndNorder(lHomeId, ++tmpNorder)
          tmpHomevideo.norder = tmpNorder-1
          tmpHomevideo.save(flush:true)
        }
      }
    }  
    redirect(action:'video',params:[id:lHomeId],base:hsRes.context.mainserverURL_lang)
  }  
  ///////////////////////////////////////////////////////////////////////////////////////////    
  def sort_video={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def getIds = requestService.getStringList('ids')
    def lsId=[]
    def oHomevideo
    for(video in  getIds){
      lsId=video.split('photo_')
      if((lsId?:[]).size()>1){
        for ( i in 1..<(lsId?:[]).size() ){
          oHomevideo=Homevideo.get(lsId[i].minus(',').toLong())
          if(oHomevideo && Home.findWhere(client_id:hsRes.client_id,id:oHomevideo?.home_id)){
            oHomevideo.norder=i
            try{
              if(!oHomevideo.save(flush:true)) {
                log.debug(" Error on save homevideo:")
                oHomevideo.errors.each{log.debug(it)}
              }  
            }catch(Exception e){
              log.debug(" Error on save homevideo:"+e.toString())
            }			  
          }
        }
      }
    }    

    render(contentType:"application/json"){[error:false]}//AJAX
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////video<<<////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////  
  /////////////price>>>////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def homeprice={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    if(lId>0){
      def bSave = requestService.getLongDef('save',0)
      hsRes.price_min=Tools.getIntVal(ConfigurationHolder.config.price_rub.min,300)
      hsRes+=getHome(hsRes.client_id,lId)
      if(!hsRes.home){
        response.sendError(404)
        return
      }
      if(bSave)
        hsRes+=requestService.getParams(['pricestatus','status_only'],['pricestandard','priceweekend','priceweek','pricemonth'])
      else
        hsRes.inrequest=hsRes.home
      hsRes.valuta=Valuta.findAll('FROM Valuta WHERE modstatus=1 ORDER BY regorder')
      hsRes.cur_valuta=Valuta.get(hsRes.home?.valuta_id?:0)
      hsRes.homepropinactive=Homeprop.findAll('FROM Homeprop WHERE modstatus<4 AND home_id=:home_id AND term=0 ORDER BY date_start',[home_id:lId])
      hsRes.homeprop=Homeprop.findAll('FROM Homeprop WHERE modstatus<3 AND home_id=:home_id AND term=1 ORDER BY date_start',[home_id:lId])
      hsRes.homepropValuta=[]
      for(homeprop in hsRes.homeprop){
        hsRes.homepropValuta<<Valuta.get(homeprop.valuta_id?:0)
      }
      hsRes.cancellation=Rule_cancellation.findAll('FROM Rule_cancellation')
      hsRes.cur_cancellation=Rule_cancellation.get(hsRes.home?.rule_cancellation_id?:0)
      hsRes.minday=Rule_minday.findAll('FROM Rule_minday')
      hsRes.maxday=Rule_maxday.findAll('FROM Rule_maxday')
      hsRes.timein=Rule_timein.findAll('FROM Rule_timein')
      hsRes.timeout=Rule_timeout.findAll('FROM Rule_timeout')
      hsRes.homeperson=Homeperson.list()
      hsRes.discountpercent=Discountpercent.list()
      hsRes.longdiscount = Homediscount.get(hsRes.home.longdiscount_id)
      hsRes.hotdiscount = Homediscount.get(hsRes.home.hotdiscount_id)
      requestService.setStatistic('edithome',12,hsRes.home?.id)
    } else {
      response.sendError(404)
      return
    }
    return hsRes
  }

  def changeValuta={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId=requestService.getLongDef('home_id',0)
    def iValutaId=requestService.getLongDef('valuta_id',0)
	
    flash.valuta_change_error=1

    if(lId>0&&iValutaId>0&&getHome(hsRes.client_id,lId).home){	
      def oRates=new Valutarate()
      def rates=oRates.csiSearchCurrent(iValutaId)	  
      if(rates||iValutaId==iRubId){
        def hsRates
        def hsDim
        if(rates){
          rates=rates[0]
          hsRates=rates.vrate
          hsDim=rates.dim
        }
        
        def oHome=Home.get(lId)
        oHome.valuta_id=iValutaId		             	                                   
        oHome.pricestandard=(oHome.pricestandard_rub)?((iValutaId==iRubId)?oHome.pricestandard_rub:(Math.round(100*hsDim/hsRates*oHome.pricestandard_rub)/100).toInteger()):0	  
        oHome.priceweekend=(oHome.priceweekend_rub)?((iValutaId==iRubId)?oHome.priceweekend_rub:(Math.round(100*hsDim/hsRates*oHome.priceweekend_rub)/100).toInteger()):0
        oHome.priceweek=(oHome.priceweek_rub)?((iValutaId==iRubId)?oHome.priceweek_rub:(Math.round(100*hsDim/hsRates*oHome.priceweek_rub)/100).toInteger()):0
        oHome.pricemonth=(oHome.pricemonth_rub)?((iValutaId==iRubId)?oHome.pricemonth_rub:(Math.round(100*hsDim/hsRates*oHome.pricemonth_rub)/100).toInteger()):0 
        oHome.deposit=(oHome.deposit_rub)?((iValutaId==iRubId)?oHome.deposit_rub:(Math.round(100*hsDim/hsRates*oHome.deposit_rub)/100).toInteger()):0
        oHome.fee=(oHome.fee_rub)?((iValutaId==iRubId)?oHome.fee_rub:(Math.round(100*hsDim/hsRates*oHome.fee_rub)/100).toInteger()):0
        oHome.cleanup=(oHome.cleanup_rub)?((iValutaId==iRubId)?oHome.cleanup_rub:(Math.round(100*hsDim/hsRates*oHome.cleanup_rub)/100).toInteger()):0
        oHome.moddate = new Date()
        oHome.caldate = new Date()
        try{
          if(!oHome.save(flush:true)) {
            log.debug(" Error on save home:")
            oHome.errors.each{log.debug(it)}
            flash.valuta_change_error=-1  
          }  
        }catch(Exception e){
          log.debug(" Error on save home: "+e.toString())
        }
		//}
        if(!flash.error){//&&requestService.getLongDef('is_homeprop',0)){
          def lsHomeprop=Homeprop.findAllWhere(home_id:lId)
          for(oHomeprop in lsHomeprop){
            oHomeprop.valuta_id=iValutaId		  
            oHomeprop.price=(oHomeprop.price_rub)?((iValutaId==iRubId)?oHomeprop.price_rub:(Math.round(100*hsDim/hsRates*oHomeprop.price_rub)/100).toInteger()):0
            oHomeprop.priceweekend=(oHomeprop.priceweekend_rub)?((iValutaId==iRubId)?oHomeprop.priceweekend_rub:(Math.round(100*hsDim/hsRates*oHomeprop.priceweekend_rub)/100).toInteger()):0
            if (oHomeprop.term==2){
              oHomeprop.priceweek=(oHomeprop.priceweek_rub)?((iValutaId==iRubId)?oHomeprop.priceweek_rub:(Math.round(100*hsDim/hsRates*oHomeprop.priceweek_rub)/100).toInteger()):0
              oHomeprop.pricemonth=(oHomeprop.pricemonth_rub)?((iValutaId==iRubId)?oHomeprop.pricemonth_rub:(Math.round(100*hsDim/hsRates*oHomeprop.pricemonth_rub)/100).toInteger()):0
            }
            try{
              if(!oHomeprop.save(flush:true)) {
                log.debug(" Error on save homeprop:")
                oHomeprop.errors.each{log.debug(it)}
                flash.valuta_change_error=-1  
              }  
            }catch(Exception e){
              log.debug(" Error on save homeprop: "+e.toString())
            }
          }		
        }          	  	  	  	 	  	 		
      }else{
        flash.valuta_change_error=-2
      }
    }else{
      flash.valuta_change_error=-2
    }    				 
    redirect(action:'homeprice',params:[id:lId],base:hsRes.context.mainserverURL_lang)	
  }

  def addprice={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['pricestatus','status_only'],['pricestandard','priceweekend','priceweek','pricemonth'])

    flash.error=[]
    if(!hsRes.inrequest?.status_only){
      if(!hsRes.inrequest?.pricestandard)
        flash.error<<1
    }
    def oHome
    if(!flash.error&&lId>0){
      oHome=Home.findWhere(client_id:hsRes.client_id,id:lId)
      if(oHome){
        if(!hsRes.inrequest?.status_only){
        //owner Dmitry>>
        /*
          if ((oHome.pricestandard!=(hsRes.inrequest?.pricestandard?:0))||
            (oHome.priceweekend!=(hsRes.inrequest?.priceweekend?:0))||
            (oHome.priceweek!=(hsRes.inrequest?.priceweek?:0))||
            (oHome.pricemonth!=(hsRes.inrequest?.pricemonth?:0))) {
            oHome?.is_confirmed = 0
        }*/
          //owner Dmitry<<
          oHome.pricestandard=hsRes.inrequest?.pricestandard?:0
          oHome.priceweekend=hsRes.inrequest?.priceweekend?:0
          oHome.priceweek=hsRes.inrequest?.priceweek?:0
          oHome.pricemonth=hsRes.inrequest?.pricemonth?:0
          oHome.is_step_price=1
          //rates>>
          def iValutaId=oHome.valuta_id
          def oRates=new Valutarate()
          def rates=oRates.csiSearchCurrent(iValutaId)

          if(rates||iValutaId==iRubId){
            def hsRates
            def hsDim
            if(rates){
              rates=rates[0]
              hsRates=rates.vrate
              hsDim=rates.dim
            }
            oHome.pricestandard_rub=(oHome.pricestandard)?((iValutaId==iRubId)?oHome.pricestandard:(Math.round(100*hsRates/hsDim*oHome.pricestandard)/100).toInteger()):0
            oHome.priceweekend_rub=(oHome.priceweekend)?((iValutaId==iRubId)?oHome.priceweekend:(Math.round(100*hsRates/hsDim*oHome.priceweekend)/100).toInteger()):0
            oHome.priceweek_rub=(oHome.priceweek)?((iValutaId==iRubId)?oHome.priceweek:(Math.round(100*hsRates/hsDim*oHome.priceweek)/100).toInteger()):0
            oHome.pricemonth_rub=(oHome.pricemonth)?((iValutaId==iRubId)?oHome.pricemonth:(Math.round(100*hsRates/hsDim*oHome.pricemonth)/100).toInteger()):0

            hsRes.price_min=Tools.getIntVal(ConfigurationHolder.config.price_rub.min,300)

            if(oHome.pricestandard_rub<hsRes.price_min)
              flash.error<<2
            if(oHome.priceweekend_rub && (oHome.priceweekend_rub<hsRes.price_min))
              flash.error<<3
            if(oHome.priceweek_rub && (oHome.priceweek_rub<oHome.pricestandard_rub))
              flash.error<<4
            if(oHome.pricemonth_rub && (oHome.pricemonth_rub<oHome.pricestandard_rub))
              flash.error<<5
          }
          //rates<<
        } else if(hsRes.int?.pricestatus==0){
          oHome.removeRegular()
        }
        //owner Dmitry>>
        /*
        if (oHome.pricestatus!=(hsRes.inrequest?.pricestatus?:0)) {
          oHome?.is_confirmed = 0
        }
        */
        //owner Dmitry<<
        oHome.pricestatus=hsRes.inrequest?.pricestatus?:0

        flash.save_error=[]
        if(hsRes.inrequest?.status_only && !hsRes.inrequest?.pricestatus)
          if(!Homeprop.findAll("FROM Homeprop WHERE term=1 AND home_id=:home_id AND date_end>:date",[home_id:oHome.id,date:new Date()])){
            flash.save_error<<1
            oHome.is_step_price=0
          }
        if(!flash.error){
          if (hsRes.inrequest?.pricestatus==1)
            oHome.addRegular()
          oHome.moddate = new Date()
          oHome.caldate = new Date()
          oHome = oHome.merge()
          try{
            if(!oHome.save(flush:true)) {
              log.debug(" Error on save homephoto:")
              oHome.errors.each{log.debug(it)}
            } 
          }catch(Exception e){
            log.debug(" Error on save homephoto:"+e.toString())
          }
        }
      }
    }

    hsRes.inrequest.id=lId
    hsRes.inrequest.save=1
    redirect(action:'homeprice',params:hsRes.inrequest,base:hsRes.context.mainserverURL_lang)
  }

  def homerule={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['fee_homeperson','rule_cancellation_id','rule_minday_id','rule_maxday_id','rule_timein_id','rule_timeout_id'],['cleanup','deposit','fee'],['homerule'])
    hsRes+=getHome(hsRes.client_id,lId)
    if(hsRes.home){
      if ((hsRes.inrequest?.homerule?:'').size()>hsRes.textlimit) hsRes.inrequest?.homerule = hsRes.inrequest?.homerule.substring(0, hsRes.textlimit)
      hsRes.home.rule_cancellation_id=hsRes.inrequest?.rule_cancellation_id?:1
      hsRes.home.rule_minday_id=hsRes.inrequest?.rule_minday_id?:1
      hsRes.home.rule_maxday_id=hsRes.inrequest?.rule_maxday_id?:1
      hsRes.home.rule_timein_id=hsRes.inrequest?.rule_timein_id?:1
      hsRes.home.rule_timeout_id=hsRes.inrequest?.rule_timeout_id?:1
      hsRes.home.deposit=hsRes.inrequest?.deposit?:0
      hsRes.home.fee=hsRes.inrequest?.fee?:0
      hsRes.home.cleanup=hsRes.inrequest?.cleanup?:0
      hsRes.home.fee_homeperson=hsRes.inrequest?.fee_homeperson?:1
      hsRes.home.homerule=hsRes.inrequest?.homerule?:''
      hsRes.home.csiSetExtrasPrice_rub()
      hsRes.home.moddate = new Date()
      if (hsRes.home.homerule!=(hsRes.inrequest?.homerule?:''))
        hsRes.home.is_confirmed = 0
      hsRes.home = hsRes.home.merge()
      if(!hsRes.home.save(flush:true)) {
        log.debug(" Error on save home:")
        hsRes.home.errors.each{log.debug(it)}
      }
    }
    redirect(action:'homeprice',params:[id:lId],base:hsRes.context.mainserverURL_lang)
  }

  def homeprop_delete={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    def oHomeprop
    def home_id = 0
    if(lId>0){
      oHomeprop=Homeprop.get(lId)
      def oHome=Home.findWhere(client_id:hsRes.client_id,id:oHomeprop?.home_id?:0.toLong())
      home_id = oHomeprop?.home_id?:0
      if(oHomeprop && oHome){
        if(oHomeprop.modstatus==3) //owner Dmitry
          oHomeprop.removeHomepropUnavailability(oHomeprop.home_id,oHomeprop.date_start)
        else{
          if(oHome.pricestatus==1)
            oHomeprop.remove()
          else
            oHomeprop.delete(flush:true)
        }
        oHome.moddate = new Date()
        oHome.caldate = new Date()
        if(!((oHome.pricestatus==1)||Homeprop.findAll("FROM Homeprop WHERE term=1 AND modstatus=1 AND home_id=:home_id AND date_end>:date",[home_id:oHome.id,date:new Date()]))){
          flash.save_error=[]
          flash.save_error<<2
          oHome.is_step_price=0
        }
        try{
          if(!oHome.save(flush:true)) {
            log.debug(" Error on save homephoto:")
            oHome.errors.each{log.debug(it)}
          } 
        }catch(Exception e){
          log.debug(" Error on save homephoto:"+e.toString())
        }
      }
    }
    redirect(action:'homeprice',params:[id:home_id],base:hsRes.context.mainserverURL_lang)	
  }
  
  def homeprop={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.price_min=Tools.getIntVal(ConfigurationHolder.config.price_rub.min,300)
    def bSave = requestService.getLongDef('save',0)
    if(bSave){
      hsRes+=requestService.getParams([],['id','price','priceweekend','valuta_id'],['date_start','date_end'])	 
      try{
        hsRes.inrequest.date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
      }catch(Exception e){
      }
      try{
        hsRes.inrequest.date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
      }catch(Exception e){
      }
    }  
    def lId = requestService.getLongDef('id',0)
    def lHomeId = requestService.getLongDef('home_id',0)//new
    hsRes.active = requestService.getLongDef('active',0)
    def oHomeprop
    if(lHomeId){
      hsRes+=getHome(hsRes.client_id,lHomeId)
      if (!hsRes.home){
        response.sendError(404)
        return
      }
    }else if(lId&&!bSave){
      oHomeprop=Homeprop.get(lId)
      if(oHomeprop?.term==2){
        redirect(action:'homeprice', id:oHomeprop?.home_id,base:hsRes.context.mainserverURL_lang)
        return
      }
      hsRes+=getHome(hsRes.client_id,oHomeprop?.home_id?:0.toLong())
      if(hsRes.home){
        hsRes.inrequest=oHomeprop
        hsRes.cur_valuta=Valuta.get(hsRes.home?.valuta_id?:0.toLong())
      } else {
        response.sendError(404)
        return
      }
    } else {
	  response.sendError(404)
	  return
	}
    return hsRes
  } 
  
  def homepropadd={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes+=requestService.getParams(['modstatus'],['id','home_id','active','price','priceweekend'],['remark'],['date_start','date_end'])
	//def lId = requestService.getLongDef('id',0)
    if(hsRes.inrequest?.home_id || hsRes.inrequest?.id){//new or edit       
      flash.error = []
      def date_start
      def date_end
      if ((hsRes.inrequest?.modstatus?:0)!=3){ //<<owner Dmitry
        def bDate = true
        if(!(hsRes.inrequest?.date_start?:'')){
          flash.error<<1
          bDate=false
        }  
        if(!(hsRes.inrequest?.date_end?:'')){
          flash.error<<2
          bDate=false
        }
        if(bDate){
          if(hsRes.inrequest.date_start>hsRes.inrequest.date_end)
          flash.error<<3
        }
        try{	
          date_start=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_start)
          date_end=Date.parse(DATE_FORMAT, hsRes.inrequest?.date_end)
        }catch(Exception e){
          flash.error<<50
        }
        def dateStart=new Date()
        def date1= new GregorianCalendar()
        date1.setTime(dateStart) 	  	  
        date1.set(Calendar.HOUR_OF_DAY ,0)
        date1.set(Calendar.MINUTE ,0)	  
        date1.set(Calendar.SECOND,0)	  
        if(date_end<date1.getTime())
          flash.error<<7		  
      }
      if(!(flash.error?:[]).size()){	    
        def oHomeprop
        def bFlag=false
        if(hsRes.inrequest?.id){
          oHomeprop=Homeprop.get(hsRes.inrequest?.id)		
          if(oHomeprop&&Home.findWhere(client_id:hsRes.client_id,id:oHomeprop?.home_id?:0)){
            if (hsRes.inrequest?.modstatus==3){ //<<owner Dmitry
              date_start = oHomeprop.date_start
              date_end = oHomeprop.date_end
            }
            if(!Homeprop.findAll("FROM Homeprop WHERE ((date_start<=:date_start AND date_end>=:date_start) OR (date_start<=:date_end AND date_end>=:date_end) OR (date_start>=:date_start AND date_end<=:date_end)) AND home_id=:home_id AND id!=:id AND modstatus!=4 AND term!=2",[date_start:date_start,date_end:date_end,id:hsRes.inrequest?.id,home_id:hsRes.inrequest?.home_id]))
              bFlag=true
          }
        }else{    
          if(Home.findWhere(client_id:hsRes.client_id,id:hsRes.inrequest?.home_id?:0))
            if(!Homeprop.findAll("FROM Homeprop WHERE ((date_start<=:date_start AND date_end>=:date_start) OR (date_start<=:date_end AND date_end>=:date_end) OR (date_start>=:date_start AND date_end<=:date_end)) AND home_id=:home_id AND modstatus!=4 AND term!=2",[date_start:date_start,date_end:date_end,home_id:hsRes.inrequest?.home_id])){
              bFlag=true			
              oHomeprop=new Homeprop()
            }
        }
        if(bFlag){
          try{		  
            def oHome
            if(hsRes.inrequest?.id)
              oHome = Home.findWhere(client_id:hsRes.client_id,id:oHomeprop?.home_id?:0)
            else
              oHome = Home.findWhere(client_id:hsRes.client_id,id:hsRes.inrequest?.home_id?:0)
            //owner Dmitry>>
            /*
            if ((oHomeprop.home_id!=(hsRes.inrequest?.home_id?:0))||
              (oHomeprop.date_start!=date_start)|| (oHomeprop.date_end!=date_end)) {			
              oHome?.is_confirmed = 0				
            }*/
            //owner Dmitry<<
            oHomeprop.home_id=hsRes.inrequest?.home_id?:0
            oHomeprop.date_start=date_start
            oHomeprop.date_end=date_end
            if (hsRes.inrequest?.modstatus!=3)//<<owner Dmitry
              oHomeprop.modstatus=1
            oHomeprop.remark=hsRes.inrequest?.remark?:''
            if(hsRes.inrequest?.active){
              //owner Dmitry>>
              /*
              if ((oHomeprop.price!=(hsRes.inrequest?.price?:0))||(oHomeprop.priceweekend!=(hsRes.inrequest?.priceweekend?:0))) {
                oHome?.is_confirmed = 0				
              }*/
              //owner Dmitry<<
              oHome.is_step_price=1			
              oHomeprop.term=1
              oHomeprop.price=hsRes.inrequest?.price?:0
              oHomeprop.priceweekend=hsRes.inrequest?.priceweekend?:0
              oHomeprop.valuta_id=oHome?.valuta_id?:0	
              //rates<<
              def iValutaId=oHome.valuta_id
              def oRates=new Valutarate()
              def rates=oRates.csiSearchCurrent(iValutaId)
	  
              if(rates||iValutaId==iRubId){
                def hsRates
                def hsDim
                if(rates){
                  rates=rates[0]
                  hsRates=rates.vrate
                  hsDim=rates.dim		    
                }  
                oHomeprop.price_rub=(oHomeprop.price)?((iValutaId==iRubId)?oHomeprop.price:(Math.round(100*hsRates/hsDim*oHomeprop.price)/100).toInteger()):0	  			
                oHomeprop.priceweekend_rub=(oHomeprop.priceweekend)?((iValutaId==iRubId)?oHomeprop.priceweekend:(Math.round(100*hsRates/hsDim*oHomeprop.priceweekend)/100).toInteger()):0			
                hsRes.price_min=Tools.getIntVal(ConfigurationHolder.config.price_rub.min,300)
			  
                if(oHomeprop.price_rub<hsRes.price_min)
                  flash.error<<5
                if(oHomeprop.priceweekend_rub && oHomeprop.priceweekend_rub<hsRes.price_min)
                  flash.error<<6
              }
              //rates<<
            }else{
              oHomeprop.term=0
            }
            if(!flash.error){
              try{
                oHome.moddate = new Date()
                oHome.caldate = new Date()
                oHome = oHome.merge()
                if(!oHome.save(flush:true)) {
                  log.debug(" Error on save Home(personal/homepropadd):")
                  oHome.errors.each{log.debug(it)}
                } 
              }catch(Exception e){
                log.debug(" Error on save Home(personal/homepropadd):"+e.toString())
              }
              if(!oHomeprop.save(flush:true)) {
                log.debug(" Error on save homeprop:")
                oHomeprop.errors.each{log.debug(it)}
              }
              if (hsRes.inrequest?.modstatus!=3)
                oHomeprop.synchronizeRegular()
            }
          }catch(Exception e){
            log.debug(" Error on save homeprop:"+e.toString())
          }		  
        } else { 
          flash.error<<4 
        }	
      }    	      
      if ((flash.error?:[]).size()) {
        hsRes.inrequest.save=1	  
        redirect(action:'homeprop',params:hsRes.inrequest,base:hsRes.context.mainserverURL_lang)
        return
      }
    }
    redirect(action:'homeprice',params:[id:hsRes.inrequest?.home_id?:0],base:hsRes.context.mainserverURL_lang)
  }
  
  def homepropactivate={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    def oHomeprop
    if(lId>0){
      oHomeprop=Homeprop.get(lId)
      def oHome
      if(oHomeprop)
        oHome=Home.findWhere(client_id:hsRes.client_id,id:oHomeprop?.home_id)
      if(oHome){	    
        try{
          oHomeprop.modstatus=1
          if(!oHomeprop.save(flush:true)) {
            log.debug(" Error on save homephoto:")
            oHomeprop.errors.each{log.debug(it)}
          }   
        }catch(Exception e){
          log.debug(" Error on save homeprop:"+e.toString())
        }
        try{
          oHome.moddate = new Date()
          oHome.caldate = new Date()
          oHome.is_step_price=1
          if(!oHome.save(flush:true)) {
            log.debug(" Error on save home:")
            oHome.errors.each{log.debug(it)}
          } 
        }catch(Exception e){
          log.debug(" Error on save home:"+e.toString())
        }		
      }
    }
    redirect(action:'homeprice',params:[id:oHomeprop?.home_id?:0],base:hsRes.context.mainserverURL_lang)
  }
  def homediscount = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!checkUser(hsRes)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=findClientId(hsRes)
    hsRes+=requestService.getParams(['minrentdays','discounttype','discexpiredays','discount'],['id'],['terms'])
    if (!hsRes.inrequest.id || !hsRes.inrequest.discount) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!Home.findWhere(client_id:hsRes.client_id,id:hsRes.inrequest.id)){
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Homediscount.processDiscount(hsRes.inrequest)
    } catch(Exception e) {
      log.debug("Cannot update Discount \n"+e.toString()+' in personal/homediscount')
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
  }

  def discountdisable = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    if (!checkUser(hsRes)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=findClientId(hsRes)
    hsRes+=requestService.getParams(['type'],['id'])
    if (!hsRes.inrequest.id || !hsRes.inrequest.type) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!Home.findWhere(client_id:hsRes.client_id,id:hsRes.inrequest.id)){
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Homediscount.disableDiscount(hsRes.inrequest)
    } catch(Exception e) {
      log.debug("Cannot update Discount \n"+e.toString()+' in personal/homediscount')
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////price<<<////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Calendar>>>/////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  def calendar = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    hsRes+=getHome(hsRes.client_id,lId)
    if (!hsRes.home){
      response.sendError(404)
      return
    }
    return hsRes
  }

  def event = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes+=requestService.getParams(['pc','tz'],['id','start','end'])
    hsRes.home = Home.get(hsRes.inrequest.id)
    if (!hsRes.home) {
      redirect(controller:'index', action:'index',base:hsRes.context.mainserverURL_lang)
      return
    }
    def event = hsRes.home.eventHome(hsRes)
    def valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    def i = 0
    for (ev in event) {
      if (ev.className=='active')
        ev.title = message(code:'home.eventHome.active.title', args:[(ev.title?:''), valutaSym.decodeHTML()], default:'')
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

  def addUnavailability = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    def lId = requestService.getLongDef('id',0)
    def start = requestService.getLongDef('start',0)
    def end = requestService.getLongDef('end',0)
    def title = requestService.getStr('title')

    def stringlimit = Tools.getIntVal(ConfigurationHolder.config.smalltext.limit,220)
    if ((title?:'').size()>stringlimit) title = title.substring(0, stringlimit)

    def hmp = new Homeprop()
    requestService.setStatistic('edithome',13,lId)
    render(contentType:"application/json"){[error:hmp.addHomepropUnavailability(lId,new Date(start),new Date(end)+1,title)]}
    return
  }

  def removeUnavailability = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    def lId = requestService.getLongDef('id',0)
    def start = requestService.getLongDef('start',0)

    def hmp = new Homeprop()
    requestService.setStatistic('edithome',13,lId)
    render(contentType:"application/json"){[error:hmp.removeHomepropUnavailability(lId,new Date(start))]}
    return
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Calendar<<</////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def adsoverview={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    hsRes+=getHome(hsRes.client_id,lId)
    //hsRes.user = User.get(hsRes.user?.id)

    if(hsRes.home) {
      if (hsRes.home.modstatus==2) {
        if (hsRes.home.is_step_descr && hsRes.home.is_step_map && hsRes.home.is_step_photo
          && hsRes.home.is_step_price && hsRes.user.modstatus==1 && (hsRes.user.tel?:0)){
          hsRes.home.modstatus=3
          hsRes.homemodstatus=Homemodstatus.findByModstatus(hsRes.home.modstatus)
        }
      } else if (hsRes.home.modstatus==1 || hsRes.home.modstatus==3) {
        if (!hsRes.home.is_step_descr || !hsRes.home.is_step_map || !hsRes.home.is_step_photo
          || !hsRes.home.is_step_price ||!(hsRes.user.modstatus==1) ||!(hsRes.user.tel?:0)){
          hsRes.home.modstatus=2
          hsRes.homemodstatus=Homemodstatus.findByModstatus(hsRes.home.modstatus)
        }
      } else if (hsRes.home.modstatus==4) {
        if (!hsRes.home.is_step_descr || !hsRes.home.is_step_map || !hsRes.home.is_step_photo
          || !hsRes.home.is_step_price || !(hsRes.user.modstatus==1) || !(hsRes.user.tel?:0)){
          hsRes.home.modstatus=-1
          hsRes.homemodstatus=Homemodstatus.findByModstatus(hsRes.home.modstatus)
        }
      }
      if(!hsRes.home.save(flush:true)) {
        log.debug(" Error on save home:")
        hsRes.home.errors.each{log.debug(it)}
      }
      hsRes.homestep=Homestep.findAll('FROM Homestep WHERE stepgroup=:stepgroup',[stepgroup:1])
      def obj = new Homestep()
      hsRes.advancedhomestep=obj.csiGetAdvancedhomestep(hsRes.home.id)
      hsRes.rating_info = Rating_info.findAllByModstatus(1)
      hsRes.ratingDetail = hsRes.home.calculateHomeRating()
    } else {
      response.sendError(404)
      return
    }

    requestService.setStatistic('edithome',8,hsRes.home.id)
    return hsRes
  }

  def publishHome={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)	
    hsRes+=getHome(hsRes.client_id,lId)

    if(hsRes.home) {
      hsRes.home.publish()
    } else {
      response.sendError(404)
      return
    }
    redirect(action:'adsoverview',params:[id:lId?:0],base:hsRes.context.mainserverURL_lang)
  }

  def deleteHome={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)	
    hsRes+=getHome(hsRes.client_id,lId)

    if(hsRes.home) {
      hsRes.home.modstatus = -2
	  hsRes.home.moddate = new Date()
      if(!hsRes.home.save(flush:true)) {
        log.debug(" Error on save home:")
        hsRes.home.errors.each{log.debug(it)}
      }
    } else {
      response.sendError(404)
      return
    }
    redirect(action:'adsoverview',params:[id:lId?:0],base:hsRes.context.mainserverURL_lang)
  }

  def viewdetstats={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)	
    hsRes+=getHome(hsRes.client_id,lId)

    if(hsRes.home) {
      hsRes.bar = ''
      hsRes.barlisting = ''
      def hsParams = [:]
      hsParams.int = [:]
      hsParams.long = [:]
      hsParams.int.time = 6
      hsParams.int.output = 0
      hsParams.long.id = lId

      def oStats=new StatSearch()
      hsRes.data=oStats.getStatsByHomeDetail(hsParams,0,true)
      hsParams.int.output = 3
      hsRes.datalisting=oStats.getStatsByHomeDetail(hsParams,0,true)

/*    def iMaxBar=0//старая версия. может понадобиться.
      for(row in hsRes.data){
        iMaxBar=Math.max(row.quant,iMaxBar.toLong())
        hsRes.bar+=((hsRes.bar!='')?',':'')+row.quant.toString()
      }
      for(row in hsRes.datalisting){
        iMaxBar=Math.max(row.quant,iMaxBar.toLong())
        hsRes.barlisting+=((hsRes.barlisting!='')?',':'')+row.quant.toString()
      }
      hsRes.bar="&chd=t:"+hsRes.bar+"|"+hsRes.barlisting+"&chds=0,"+iMaxBar+"&chxt=y,x&chxl=0:|0|"+
      Math.round(iMaxBar/2)+'|'+iMaxBar+"|1:"
      def i=0
      for(row in hsRes.data){
        hsRes.bar+='|'+(++i)
      }*/
      def iMaxBar=0
      for(row in hsRes.data){
        iMaxBar=Math.max(row.quant,iMaxBar.toLong())
        hsRes.bar+=((hsRes.bar!='')?',':'')+row.quant.toString()
      }
      for(row in hsRes.datalisting){
        iMaxBar=Math.max(row.quant,iMaxBar.toLong())
        hsRes.barlisting+=((hsRes.barlisting!='')?',':'')+row.quant.toString()
      }
      hsRes.bar="&chd=t:"+hsRes.bar+"|"+hsRes.barlisting+"&chds=0,"+iMaxBar+"&chxt=y,x&chxl=0:"
      for(int i=hsRes.data.size(); i>0; i--){
        hsRes.bar+='|'+hsRes.data[i-1].name
      }
      hsRes.bar+="|1:|0|"+Math.round(iMaxBar/4)+'|'+Math.round(iMaxBar/2)+'|'+Math.round(3*iMaxBar/4)+'|'+iMaxBar+'&chdl='+message(code:"ads.stat.visits")+'|'+message(code:"ads.stat.shows")
    } else {
      response.sendError(404)
      return
    }
    return hsRes
  }

//owner Dmitry>>
  def updateFulladressAndInfo ( iHome_id ) {
    def oHome = Home.get(iHome_id)
    if (oHome) {
      //oHome.fulladdress=Country.get(oHome.country_id).name+' '+Region.get(oHome.region_id).name+(((Region.get(oHome.region_id).name!=oHome.city)&&oHome.city)?(' '+oHome.city):'')+((oHome.street?:'')?(' '+oHome.street):'')+((oHome.homenumber?:'')?(' '+oHome.homenumber):'')+((oHome.mapkeywords?:'')?(' '+oHome.mapkeywords):'')
      
      def sCity=''
      if(oHome.city_id){
        if(City.get(oHome.city_id).name && (Region.get(oHome.region_id).shortname!=City.get(oHome.city_id).name))
          sCity=City.get(oHome.city_id).name+' '
        sCity+=City.get(oHome.city_id).name_en  
      }else if(oHome.city){
        if(!(oHome.city?:'').matches('.*(?=.*[A-Za-z]).*')){
          if(Region.get(oHome.region_id).shortname!=oHome.city)                    
            sCity=oHome.city+' '          
          sCity+=Tools.transliterate(oHome.city,0)  
        }else{        
          sCity=oHome.city
        }
      }
      oHome.fulladdress=Country.get(oHome.country_id).name+' '+Country.get(oHome.country_id).name_en+' '+Region.get(oHome.region_id).shortname+(sCity?(' '+sCity):'')+((Region.get(oHome.region_id)?.popdirection_id)?(' '+Popdirection.get(Region.get(oHome.region_id)?.popdirection_id)?.keyword?:''):'')
      
      oHome.fullinfo=Country.get(oHome.country_id).name+' '+Region.get(oHome.region_id).name+(((Region.get(oHome.region_id).name!=oHome.city)&&oHome.city)?(' '+oHome.city):'')+((oHome.street?:'')?(' '+oHome.street):'')+((oHome.homenumber?:'')?(' '+oHome.homenumber):'')+((oHome.mapkeywords?:'')?(' '+oHome.mapkeywords):'')+
        ((oHome.description?:'')?(' '+oHome.description):'')+((oHome.remarks?:'')?(' '+oHome.remarks):'')
      /*def homeoption_name=['is_nosmoking','is_parking','is_visa','is_tv','is_internet','is_wifi','is_cond',
        'is_heat','is_kitchen','is_holod','is_microwave','is_wash','is_breakfast','is_swim','is_steam','is_gym',
        'is_hall','is_family','is_pets','is_invalid','is_area','is_beach']*/
      def homeoption=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id=1 ORDER BY id')
      for (opt in homeoption){
        if (oHome."${opt.fieldname}"==1)
          oHome.fullinfo += ' '+opt.name
      }
      def homeoption_dop=Homeoption.findAll('FROM Homeoption WHERE facilitygroup_id!=1 ORDER BY id')
      for (option in homeoption_dop){
        if ((oHome.homeoption?:'').find((option?.id).toString()))
          oHome.fullinfo += ' '+option.name
      }
      def homemetro = Homemetro.findAll('FROM Homemetro WHERE home_id=:home_id ORDER BY id',[home_id:iHome_id])
      for (metro in homemetro){
        //oHome.fulladdress += ' '+Metro.get(metro.metro_id).name
        oHome.fullinfo += ' '+Metro.get(metro.metro_id).name
      }
	  if (oHome.fulladdress.size()>21800) oHome.fulladdress = oHome.fulladdress.substring(0, 21800)
	  if (oHome.fullinfo.size()>21800) oHome.fullinfo = oHome.fullinfo.substring(0, 21800)
      oHome.moddate = new Date()
      if(!oHome.save(flush:true)) {
        log.debug(" Error on save home:")
        oHome.errors.each{log.debug(it)}
      }
    }
  }
//<<owner Dmitry
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Infras>>>///////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  def infras = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    hsRes+=getHome(hsRes.client_id,lId)
    if (!hsRes.home){
	  response.sendError(404)
      return
    }

    hsRes.homeinfr=Homeinfr.list()
    hsRes.homedistance=Homedistance.list()
    hsRes.homeguidebook=Homeguidebook.findAllByHome_id(hsRes.home.id)
    hsRes.homeguidebooktype = Homeguidebooktype.list()        
    
    hsRes.infrdistance = []
    def bTemp = []
    int i=0
    def aTemp = hsRes.home.infraoption?.split(',')//no lang because en and ru fields values are sinchronized 
    aTemp.each{ bTemp << it.split(':') }
    hsRes.homeinfr.each{
      if(i<bTemp.size()){
        if(it.name != bTemp[i][0])
				hsRes.infrdistance<<0
        else {
          hsRes.infrdistance<<bTemp[i][1]
          i++
        }
      }
    }

    return hsRes
  }

  def infrasave = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('id',0)
    hsRes+=getHome(hsRes.client_id,lId)
    if (!hsRes.home){
	  response.sendError(404)
      return
    }

    hsRes.homeinfr=Homeinfr.list()
    
    hsRes.home.infraoption = ''
    hsRes.home.infraoption_en = ''
    hsRes.homeinfr.each{              
      def oHomedistance = Homedistance.get(requestService.getIntDef(it.name,0))
      if (oHomedistance){ 
        hsRes.home.infraoption += it.name+':'+oHomedistance.name+','
        hsRes.home.infraoption_en += it.name_en+':'+oHomedistance.name_en+','
      }
    }  
    hsRes.home.moddate = new Date()
    if(!hsRes.home.save(flush:true)) {
      log.debug(" Error on save home in Personal:infrasave :")
      hsRes.home.errors.each{log.debug(it)}
    } else {
      def oNote = Note.findByHome_idAndNotetype_id(hsRes.home.id,4)
      if (oNote && oNote.modstatus){
        if (hsRes.home.infraoption != ''){
          oNote.modstatus=0
          if(!oNote.save(flush:true)) {
            log.debug(" Error on save Note(personal/infrasave/) id:"+oNote.id)
            oNote.errors.each { log.debug(it)}
          }
        }
      }
	}

    redirect(action:'infras', params:[id:lId],base:hsRes.context.mainserverURL_lang)
    return
  }

  def infrasAdd = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    def lId = requestService.getLongDef('home_id',0)
    hsRes+=getHome(hsRes.client_id,lId)
    if (!hsRes.home){
	  response.sendError(404)
      return
    }
    def result = [:]
    result.error = false
    hsRes+=requestService.getParams(['type_id'],['x','y','infr_id'],['nameinfr','description'])
    if(!hsRes.inrequest?.nameinfr)
      result.error = true
    if (!result.error){
      def oHomeguidebook
      if(!hsRes.inrequest?.infr_id)
        oHomeguidebook = new Homeguidebook()
      else
        oHomeguidebook = Homeguidebook.get(hsRes.inrequest?.infr_id)
      if (oHomeguidebook){
        oHomeguidebook.name = hsRes.inrequest?.nameinfr
        oHomeguidebook.description = hsRes.inrequest?.description?:''
        oHomeguidebook.home_id = hsRes.home?.id
        oHomeguidebook.type_id = hsRes.inrequest?.type_id?:10
        oHomeguidebook.x = hsRes.inrequest?.x?:0
        oHomeguidebook.y = hsRes.inrequest?.y?:0
        if(!oHomeguidebook.save(flush:true)) {
          log.debug(" Error on save Homeguidebook:")
          oHomeguidebook.errors.each{log.debug(it)}
        } else {
		  def oNote = Note.findByHome_idAndNotetype_id(hsRes.home.id,4)
		  if (oNote && oNote.modstatus){
			if (Homeguidebook.findAllByHome_id(hsRes.home.id).size()>0){
			  oNote.modstatus=0
			  if(!oNote.save(flush:true)) {
				log.debug(" Error on save Note(personal/infrasAdd/) id:"+oNote.id)
				oNote.errors.each { log.debug(it)}
			  }
			}
		  }
		}
      }
    }

    render result as JSON
    return
  }

  def infraslist = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
	if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes+=requestService.getParams(null,['home_id'])
    hsRes+=getHome(hsRes.client_id,hsRes.inrequest?.home_id)
    if (!hsRes.home){
	  response.sendError(404)
      return
    }
    def oHomeguidebook = new Homeguidebook()
    hsRes+=oHomeguidebook.csiSelectInfras(hsRes.inrequest?.home_id?:0,5,requestService.getOffset())
    hsRes.homeguidebooktype = Homeguidebooktype.list() 
  
    return hsRes
  }

  def infrasdelete = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes+=requestService.getParams(null,['home_id','id'])
    hsRes+=getHome(hsRes.client_id,hsRes.inrequest?.home_id)
    if (!hsRes.home){
	  response.sendError(404)
      return
    }
	
    def oHomeguidebook = Homeguidebook.get(hsRes.inrequest?.id)
    oHomeguidebook.delete(flush:true)

    def result = [:]
    result.lIds = hsRes.inrequest?.id

    render result as JSON
    return
  }
//<<owner Dmitry
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Infras<<<///////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Bookings>>>/////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  
  def bookings = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes+=findClientId(hsRes)

    hsRes.inrequest=[:]
    hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
    hsRes.homeperson=Homeperson.list()    
    hsRes.textlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)
    hsRes.stringlimit = Tools.getIntVal(ConfigurationHolder.config.smalltext.limit,220)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    def oTrip = new TripSearch()
    hsRes.data=oTrip.csiGetBron(hsRes.user.client_id?:0,hsRes.inrequest.modstatus,20,requestService.getOffset())
    
    //transliterate////////////////////////////////////////////   
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
    hsRes.status_count = []
    for(status in [0,1,2,-1])
      hsRes.status_count << oTrip.csiGetBron(hsRes.user.client_id?:0,status,20,requestService.getOffset()).count?:0

    requestService.setStatistic('pcservices',40)
    return hsRes
  }

  def bookingsConfirm = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return	
    hsRes+=findClientId(hsRes)

    def lId = requestService.getIntDef('id',0)
    def iMod = requestService.getIntDef('st',0)
    def oTrip = Trip.get(lId?:0)
    if(oTrip){
      if(hsRes.client_id != Home.get(oTrip.home_id)?.client_id){
        redirect(controller:'personal', action:'index',base:hsRes.context.mainserverURL_lang)
        return
      }
      if(iMod==1){
        if(oTrip.payorder_id) {
          try {
            billingService.doTransaction( billingService.createPaytrans(oTrip,'',14) )
          } catch(Exception e) {
            hsRes.error = true
            hsRes.message = message(code:'mbox.bron.bderror.errormessage',args:[], default:'').toString()
            render hsRes as JSON
            return
          }
        } else {
          billingService.confirmTrip(oTrip)
        }
      } else if (iMod==-1){
        hsRes.trip = oTrip
        hsRes.homeperson=Homeperson.get(oTrip.homeperson_id)['name'+hsRes.context?.lang]
        hsRes.home=Home.get(oTrip.home_id)
        hsRes.user=User.get(oTrip.user_id)
        hsRes.date_start=String.format('%td/%<tm/%<tY',oTrip.fromdate)
        hsRes.date_end=String.format('%td/%<tm/%<tY',oTrip.todate)
        hsRes.inputdate=String.format('%td/%<tm/%<tY %<tH:%<tM',oTrip.inputdate)
      }
    } else {
      redirect(controller:'personal', action:'index',base:hsRes.context.mainserverURL_lang)
      return
    }
    render hsRes as JSON
  }

  def docprint3g = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=findClientId(hsRes)

    hsRes+=requestService.getParams(null,['id'])
    hsRes.trip = Trip.get(hsRes.inrequest?.id?:0)
    if (!hsRes.trip) {
      redirect(action:'bookings',base:hsRes.context.mainserverURL_lang)
      return
    }
    hsRes.client = Client.get(hsRes.client_id?:0)
    hsRes.home = Home.get(hsRes.trip.home_id)

    return hsRes
  }

  def tripprint = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=findClientId(hsRes)

    hsRes.inrequest=[:]
    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
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
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Bookings<<</////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////// 
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Waiting>>>//////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def waiting = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes+=findClientId(hsRes)

    hsRes.imageurl = ConfigurationHolder.config.urluserphoto
    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
    hsRes.country=Country.list()
    hsRes.region=Region.list()
    hsRes.homeperson=Homeperson.list()
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.context.shown_valuta.id).symbol
    def oZayavka = new ZayavkaSearch()
    hsRes.data=oZayavka.csiSelectZayavka2client(hsRes.user.client_id?:0,20,requestService.getOffset())
    hsRes.userHomes=Home.findAll('FROM Home WHERE client_id=:client_id and modstatus=1 ORDER BY name',[client_id:hsRes.client_id])
    hsRes.textlimit = Tools.getIntVal(ConfigurationHolder.config.largetext.limit,5000)

    return hsRes
  }

  def startmessage = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=findClientId(hsRes)

    def lId = requestService.getIntDef('id',0)
    def oZayvka2client = Zayvka2client.get(lId?:0)
    def oZayavka = Zayavka.get(oZayvka2client?.zayvka_id?:0)
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
    if(!Home.findByClient_idAndModstatus(hsRes.client_id?:0,1)){
      hsRes.error = true
      hsRes.message = message(code:'personal.startmessage.nohomes.error',args:[], default:'').toString()
      render hsRes as JSON
      return
    }
    if(oZayavka){
      hsRes.zayavka = oZayavka
      hsRes.z2C_id = lId
      hsRes.userZ = User.get(oZayavka.user_id)
      hsRes.date_start=String.format('%td-%<tm-%<tY',oZayavka.date_start)
      hsRes.date_end=String.format('%td-%<tm-%<tY',oZayavka.date_end)
      hsRes.homeperson=Homeperson.get(oZayavka.homeperson_id)['name'+hsRes.context?.lang]
      hsRes.adress=''+(Country.get(oZayavka.country_id).name)+" "+(Region.get(oZayavka.region_id).name)+((oZayavka.region_id!=77&&oZayavka.region_id!=78)?oZayavka.city:'')
      hsRes.prcFrom=Math.rint(100.0 * (oZayavka.pricefrom_rub / hsRes.valutaRates)) / 100.0
      hsRes.prcTo=Math.rint(100.0 * (oZayavka.priceto_rub / hsRes.valutaRates)) / 100.0
    } else {
      hsRes.error = true
      hsRes.message = message(code:'personal.startmessage.nozayavka.error',args:[], default:'').toString()
    }	
    render hsRes as JSON
  }

  def declineZayavka = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=findClientId(hsRes)

    def lId = requestService.getIntDef('id',0)
    def oZayvka2client = Zayvka2client.findByIdAndClient_id(lId,hsRes.client_id)
    if(oZayvka2client)
      if(oZayvka2client.modstatus==0){
        oZayvka2client.modstatus=-1
        oZayvka2client.save(flush:true)
      }

    render hsRes as JSON
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Waiting<<<//////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Promote>>>//////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def promote = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=findClientId(hsRes)
    def lId = requestService.getLongDef('id',0)
    hsRes+=getHome(hsRes.client_id,lId)
    if (!hsRes.home){
      response.sendError(404)
      return
    }
    hsRes.imageurl = ConfigurationHolder.config.urlphoto + hsRes.client_id.toString()+'/'
    hsRes.inrequest = hsRes.home
    hsRes.hcity = City.get(hsRes.home.city_id?:0)

    return hsRes
  }

  def promotesave = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false)
    if (!checkUser(hsRes)) return
    hsRes+=findClientId(hsRes)
    def lId = requestService.getLongDef('id',0)

    redirect(action:'promote', params:[id:lId],base:hsRes.context.mainserverURL_lang)
    return
    /*def sName = requestService.getStr('linkname')
    hsRes+=getHome(hsRes.client_id,lId)
    if (!hsRes.home){
      response.sendError(404)
      return
    }
    if(sName){
      hsRes.home.linkname = hsRes.home.getUniqueLinkname(sName,lId)
      hsRes.home.save(flush:true)
    }

    redirect(action:'promote', params:[id:lId])
    return*/
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////Promote<<<//////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def rules = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    return hsRes
  }
  
  def requirements = {
    requestService.init(this)  
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes+=findClientId(hsRes)
    //hsRes.user = User.get(hsRes.user.id)
    hsRes.requirements=Rule_booking.list()    
    return hsRes
  }

  def savereqsetting = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true,true)
    if (!checkUser(hsRes)) return
    hsRes+=init(hsRes)
    hsRes.user = User.get(hsRes.user?.id)

    hsRes+=requestService.getParams(['is_telaprove','is_clientphoto'])

    hsRes.user.is_telaprove = hsRes.inrequest?.is_telaprove?:0
    hsRes.user.is_clientphoto = hsRes.inrequest?.is_clientphoto?:0

    if(!hsRes.user.save(flush:true)) {
      log.debug(" Error on save user. in Personal:savereqsetting:")
      hsRes.user.errors.each{log.debug(it)}
    }

    redirect(action:'requirements',base:hsRes.context.mainserverURL_lang)
    return
  }  
}
