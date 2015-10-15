import org.codehaus.groovy.grails.commons.ConfigurationHolder
class HomeService {
  def messageSource

	def setHomeModstatus(iModstatus,iHome_id,iClient_id, newClient_id = 0){
		def oHome=Home.findWhere(id:iHome_id,client_id:iClient_id)
		if(oHome){
			oHome.modstatus=iModstatus
			if (newClient_id)
				oHome.client_id=newClient_id
			if(!oHome.save(flush:true)) {
				log.debug(" Error on save home:")
				oHome.errors.each{log.debug(it)}
				return false
			}
		}else{
			return false
		}
		return true
	}

	def setHomePropModstatus(iModstatus,iHome_id){
		def oHomeprop=Homeprop.findWhere(home_id:iHome_id)
		if(oHomeprop){
			oHomeprop.modstatus=iModstatus
			if(!oHomeprop.save(flush:true)) {
				log.debug(" Error on save homeprop:")
				oHomeprop.errors.each{log.debug(it)}
			}
		}else{
			return false
		}
		return true
	}

	def getSomePrice(oHome){
		def hsTmp = [:]
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		date1.set(Calendar.HOUR_OF_DAY ,0)
		date1.set(Calendar.MINUTE ,0)
		date1.set(Calendar.SECOND,0)
		date1.set(Calendar.MILLISECOND,0)
		def tmpPrice = Homeprop.findAll("FROM Homeprop WHERE date_end>=:date_start_query AND home_id=:home_id AND modstatus=1 AND term>0 ORDER BY date_start",[date_start_query:date1.getTime(),home_id:oHome.id])
		def resstatModifier = 1.0
		if (Client.get(oHome.client_id)?.resstatus==1) {
			resstatModifier = resstatModifier + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100)
		}
		if(tmpPrice.size()>0){
			hsTmp.price_day = Math.round(tmpPrice[0].price_rub*resstatModifier)
			hsTmp.price_weekend = Math.round((tmpPrice[0].priceweekend_rub?:0)*resstatModifier)
			hsTmp.price_week = Math.round((tmpPrice[0].priceweek_rub?:0)*resstatModifier)
			hsTmp.price_month = Math.round((tmpPrice[0].pricemonth_rub?:0)*resstatModifier)
			hsTmp.havePrice = 1
		} else {
			hsTmp.price_day = 0
			hsTmp.price_weekend = 0
			hsTmp.price_week = 0
			hsTmp.price_month = 0
			hsTmp.havePrice = 0
		}
		return hsTmp
	}

	def createClient(sEmail,iModstatus){
		def oClient=new Client()
		oClient.name=sEmail
		oClient.inputdate=new Date()
		oClient.modstatus=iModstatus
		oClient.code=java.util.UUID.randomUUID().toString()
		if(!oClient.save(flush:true)) {
			log.debug(" Error on save client:")
			oClient.errors.each{log.debug(it)}
			return false
		}
		return true
	}

  Integer getShomeType(_request) {
    Shometype.list().find{_request?."${it.fieldname}"==1}?.id?:0
  }

	def generateInfotags(hsRes){
    if(hsRes.shome){      
      hsRes.h1=(hsRes.shome.header?:'')
        .replace('[VCITY]',hsRes.infotags.vcity+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))
	      .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))
      hsRes.title=(hsRes.shome.title?:'')
        .replace('[VCITY]',' в '+hsRes.infotags.vcity+((hsRes.inrequest?.view=='map')?' на карте':''))
	      .replace('[CITY]',' '+hsRes.infotags.city+((hsRes.inrequest?.view=='map')?' на карте':''))
      hsRes.keywords=(hsRes.shome?.keywords?:'') 
        .replace('[VCITY]',' в '+hsRes.infotags.vcity+((hsRes.inrequest?.view=='map')?' на карте':''))
	      .replace('[CITY]',' '+hsRes.infotags.city+((hsRes.inrequest?.view=='map')?' на карте':'')) 
      hsRes.description=(hsRes.shome?.description?:'')        
        .replace('[VCITY]',' в '+hsRes.infotags.vcity+((hsRes.inrequest?.view=='map')?' на карте':''))
	      .replace('[CITY]',hsRes.infotags.city+((hsRes.inrequest?.view=='map')?' на карте':''))
      hsRes.itext=(hsRes.shome.itext?:'')
        .replace('[VCITY]',hsRes.infotags.vcity+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))
	      .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))                
      hsRes.promotext=(hsRes.shome?.promotext?:'')
        .replace('[VCITY]',hsRes.infotags.vcity+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))
	      .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))                  
      hsRes.itext2=(hsRes.shome?.itext2?:'')
        .replace('[VCITY]',hsRes.infotags.vcity+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))
	      .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))                  
    }else{  
      if(hsRes.infotags.isfound){
        if((hsRes.inrequest?.citysight_id?:[]).size()==1 && !hsRes.inrequest?.bedroom && !hsRes.inrequest?.hometype_id && !(hsRes.inrequest?.longdiscount || hsRes.inrequest?.hotdiscount) && !(hsRes.inrequest?.date_start || hsRes.inrequest?.date_end) && hsRes.inrequest?.view!='map'){
          hsRes.title=hsRes.breadcrumbs?.citysight.title?:''
          hsRes.keywords=hsRes.breadcrumbs?.citysight?.keywords?:''
          hsRes.description=hsRes.breadcrumbs?.citysight?.description?:''
        }else{
          hsRes.title=(hsRes.infotext.title?:'')
            .replace('[VCITY]',' в '+hsRes.infotags.vcity)
            .replace('[CITY]',' '+hsRes.infotags.city)
            .replace('[METRO]',(hsRes.inrequest?.metro_id?:[]).size()==1?' у '+hsRes.breadcrumbs.metro?.name:'').replace('"','')
            .replace('[VSIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+(hsRes.breadcrumbs.citysight?.type==1?'':(hsRes.breadcrumbs.citysight?.type==2?'на ':'в '))+hsRes.breadcrumbs.citysight?.name2:'')
            .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+hsRes.breadcrumbs.citysight?.name:'')
            .replace('[HOMEROOM]',(hsRes.inrequest?.bedroom && hsRes.inrequest?.hometype_id==1)?hsRes.infotags.homeroom[0..-3]+'ую ':'')
            .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest?.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype)
            .replace('[HOMETYPESS]',hsRes.infotags.hometypess)+          
            ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?' - раннее бронирование':'')+
            ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - горящие предложения':'')+
            ((hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - скидки':'')+
            ((hsRes.inrequest?.view=='map')?': поиск на карте':'')+
            ((hsRes.breadcrumbs.city?.seo_title && (hsRes.inrequest?.metro_id?:[]).size()==0 && (hsRes.inrequest?.citysight_id?:[]).size()==0 && (hsRes.inrequest?.district_id?:[]).size()==0 && !hsRes.inrequest?.bedroom && !hsRes.inrequest?.hometype_id && !hsRes.inrequest?.homeclass_id && !(hsRes.inrequest?.longdiscount || hsRes.inrequest?.hotdiscount) && !(hsRes.inrequest?.date_start || hsRes.inrequest?.date_end) && hsRes.inrequest?.view!='map')?'. '+hsRes.breadcrumbs.city.seo_title:'')    
          hsRes.keywords=(hsRes.infotext?.keywords?:'')
            .replace('[METRO]',((hsRes.inrequest?.metro_id?:[]).size()==1)?' у '+hsRes.breadcrumbs.metro?.name:'').replace('"','')
            .replace('[VSIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+(hsRes.breadcrumbs.citysight?.type==1?'':(hsRes.breadcrumbs.citysight?.type==2?'на ':'в '))+hsRes.breadcrumbs.citysight?.name2:'')
            .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+hsRes.breadcrumbs.citysight?.name:'')
            .replace('[VCITY]',' в '+hsRes.infotags.vcity+((hsRes.inrequest?.view=='map')?' на карте':''))
            .replace('[CITY]',' '+hsRes.infotags.city+((hsRes.inrequest?.view=='map')?' на карте':''))
            .replace('[HOMEROOMS]',(hsRes.inrequest?.bedroom && hsRes.inrequest?.hometype_id==1)?hsRes.infotags.homeroom2+' ':'')
            .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest?.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
            .replace('[HOMEROOM]',(hsRes.inrequest?.bedroom && hsRes.inrequest?.hometype_id==1)?hsRes.infotags.homeroom[0..-3]+'ую ':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype)
            .replace('[HOMETYPES]',(hsRes.inrequest?.hometype_id>0)?hsRes.infotags.hometypes:hsRes.infotags.hometypess)
            .replace('[HOMETYPESS]',hsRes.infotags.hometypess)+
            (hsRes.inrequest?.longdiscount?', раннее бронирование':'')+
            (hsRes.inrequest?.hotdiscount?', горящие предложения':'')+
            ((hsRes.breadcrumbs.city?.seo_keywords && (hsRes.inrequest?.metro_id?:[]).size()==0 && (hsRes.inrequest?.citysight_id?:[]).size()==0 && (hsRes.inrequest?.district_id?:[]).size()==0 && !hsRes.inrequest?.bedroom && !hsRes.inrequest?.hometype_id && !hsRes.inrequest?.homeclass_id && !(hsRes.inrequest?.longdiscount || hsRes.inrequest?.hotdiscount) && !(hsRes.inrequest?.date_start || hsRes.inrequest?.date_end) && hsRes.inrequest?.view!='map')?', '+hsRes.breadcrumbs.city.seo_keywords:'')
          hsRes.description=(hsRes.infotext?.description?:'')
            .replace('[METRO]',((hsRes.inrequest?.metro_id?:[]).size()==1)?' у '+hsRes.breadcrumbs?.metro?.name:'').replace('"','')
            .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+(hsRes.breadcrumbs.citysight?.type==1?'':(hsRes.breadcrumbs.citysight?.type==2?'на ':'в '))+hsRes.breadcrumbs.citysight?.name2:'')
            .replace('[VCITY]',' в '+hsRes.infotags.vcity+((hsRes.inrequest?.view=='map')?' на карте':''))
            .replace('[CITY]',hsRes.infotags.city+((hsRes.inrequest?.view=='map')?' на карте':''))
            .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
            .replace('[HOMETYPESS]',hsRes.infotags.hometypess)+
            (hsRes.inrequest?.longdiscount?'. Скидки на раннее бронирование':'')+
            (hsRes.inrequest?.hotdiscount?'. Скидки на горящие предложения':'')+
            ((hsRes.breadcrumbs.city?.seo_description && (hsRes.inrequest?.metro_id?:[]).size()==0 && (hsRes.inrequest?.citysight_id?:[]).size()==0 && (hsRes.inrequest?.district_id?:[]).size()==0 && !hsRes.inrequest?.bedroom && !hsRes.inrequest?.hometype_id && !hsRes.inrequest?.homeclass_id && !(hsRes.inrequest?.longdiscount || hsRes.inrequest?.hotdiscount) && !(hsRes.inrequest?.date_start || hsRes.inrequest?.date_end) && hsRes.inrequest?.view!='map')?'. '+hsRes.breadcrumbs.city.seo_description:'')
        }
        hsRes.h1=(hsRes.infotext?.header?:'')
          .replace('[METRO]',(hsRes.inrequest?.metro_id?:[]).size()==1?'у '+hsRes.breadcrumbs.metro.name+((hsRes.inrequest?.view=='map')?' на карте':''):'').replace('"','')
          .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+(hsRes.breadcrumbs.citysight?.type==1?'':(hsRes.breadcrumbs.citysight?.type==2?'на ':'в '))+hsRes.breadcrumbs.citysight?.name2+((hsRes.inrequest?.view=='map')?' на карте':''):'')
          .replace('[VCITY]',hsRes.infotags.vcity+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))
          .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))
          .replace('[HOMEROOM]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroom[0..-3]+'ую ':'')
          .replace('[HOMETYPE]',hsRes.infotags.hometype)+
          ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?' - раннее бронирование':'')+
          ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - горящие предложения':'')+
          ((hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - скидки':'')      
        hsRes.itext=(hsRes.infotext.itext2?:'')
          .replace('[METRO]',(hsRes.inrequest?.metro_id?:[]).size()==1?'рядом с '+hsRes.breadcrumbs.metro.name+((hsRes.inrequest?.view=='map')?' на карте':''):'').replace('"','')
          .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+(hsRes.breadcrumbs.citysight?.type==1?'':(hsRes.breadcrumbs.citysight?.type==2?'на ':'в '))+hsRes.breadcrumbs.citysight?.name2+((hsRes.inrequest?.view=='map')?' на карте':''):'')
          .replace('[VCITY]',hsRes.infotags.vcity+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))
          .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' на карте':''))
          .replace('[HOMEROOM]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroom[0..-3]+'ую ':'')
          .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest?.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
          .replace('[HOMETYPE]',hsRes.infotags.hometype)
          .replace('[HOMETYPESS]',hsRes.infotags.hometypess)
          .replace('\n',' ').replace('\r',' ').replace('</p>',', скидки')+
          ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?', раннее бронирование':'')+
          ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?', горящие предложения':'')+'.</p>'
      }else{
        if(hsRes.inrequest?.where!=''){
          hsRes.title=(hsRes.infotext?.title?:'')
            .replace('[VCITY]',' '+hsRes.inrequest?.where)
            .replace('[CITY]',' '+hsRes.inrequest?.where)
            .replace('[METRO]','').replace('[VSIGN]','').replace('[SIGN]','')
            .replace('[HOMEROOM]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroom[0..-3]+'ую ':'')
            .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest?.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype)
            .replace('[HOMETYPESS]',hsRes.infotags.hometypess)+          
            ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?' - раннее бронирование':'')+
            ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - горящие предложения':'')+
            ((hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - скидки':'')+
            ((hsRes.inrequest?.view=='map')?': поиск на карте':'')        
          hsRes.keywords=(hsRes.infotext?.keywords?:'').replace('[METRO]','').replace('[SIGN]','').replace('[VSIGN]','')
            .replace('[CITY]',' '+hsRes.inrequest?.where+((hsRes.inrequest?.view=='map')?' на карте':''))
            .replace('[VCITY]',' '+hsRes.inrequest?.where+((hsRes.inrequest?.view=='map')?' на карте':''))
            .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest?.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
            .replace('[HOMEROOM]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroom[0..-3]+'ую ':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype)
            .replace('[HOMETYPESS]',hsRes.infotags.hometypess)+
            (hsRes.inrequest?.longdiscount?', раннее бронирование':'')+
            (hsRes.inrequest?.hotdiscount?', горящие предложения':'')
          hsRes.description=(hsRes.infotext?.description?:'').replace('[METRO]','').replace('[SIGN]','')
            .replace('[VCITY]',' '+hsRes.inrequest?.where+((hsRes.inrequest?.view=='map')?' на карте':''))
            .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
            .replace('[HOMETYPESS]',hsRes.infotags.hometypess)+
            (hsRes.inrequest?.longdiscount?'. Скидки на раннее бронирование':'')+
            (hsRes.inrequest?.hotdiscount?'. Скидки на горящие предложения':'')          
          hsRes.h1=(hsRes.infotext?.promotext1?:'')
            .replace('[CITY]',hsRes.inrequest?.where+((hsRes.inrequest?.view=='map')?' на карте':''))
            .replace('[HOMEROOM]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroom[0..-3]+'ую ':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype)
            ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?' - раннее бронирование':'')+
            ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - горящие предложения':'')+
            ((hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - скидки':'')          
          hsRes.itext=(hsRes.infotext?.itext3?:'').replace('[CITY]',hsRes.inrequest?.where+((hsRes.inrequest?.view=='map')?' на карте':''))
            .replace('[HOMEROOM]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroom[0..-3]+'ую ':'')
            .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest?.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype)
            .replace('[HOMETYPESS]',hsRes.infotags.hometypess)
            .replace('\n',' ').replace('\r',' ').replace('</p>',', скидки')+
            ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?', раннее бронирование':'')+
            ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?', горящие предложения':'')+'.</p>'
        }else{
          hsRes.title=(hsRes.infotext?.promotext2?:'')+((hsRes.inrequest?.view=='map')?': поиск на карте':'')
          hsRes.keywords=(hsRes.infotext?.keywords?:'').replace('[METRO]','').replace('[SIGN]','').replace('[VSIGN]','').replace('[CITY]',(hsRes.inrequest?.view=='map')?' на карте':'').replace('[VCITY]',(hsRes.inrequest?.view=='map')?' на карте':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype).replace('[HOMETYPES]',hsRes.infotags.hometypes).replace('[HOMEROOMS]','').replace('[HOMEROOM]','')
          hsRes.description=(hsRes.infotext?.description?:'').replace('[METRO]','').replace('[SIGN]','').replace('[VCITY]',(hsRes.inrequest?.view=='map')?' на карте':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype).replace('[HOMETYPESS]',hsRes.infotags.hometypess).replace('[HOMEROOMSS]','').replace('[HOMEROOM]','')
          hsRes.h1=(hsRes.infotext?.promotext2?:'')+((hsRes.inrequest?.view=='map')?': поиск на карте':'')
          hsRes.itext=(hsRes.infotext?.itext?:'').replace('\n',' ').replace('\r',' ')
        }
      }
    }  
	}
	def generateInfotagsLang(hsRes){
    if(hsRes.shome){      
      hsRes.h1=(hsRes.shome.header_en?:'')
        .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))
      hsRes.title=(hsRes.shome.title_en?:'')
        .replace('[CITY]',' in '+hsRes.infotags.city)
      hsRes.keywords=(hsRes.shome?.keywords_en?:'')
        .replace('[VCITY]',' in '+hsRes.infotags.vcity+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))
        .replace('[CITY]',hsRes.infotags.city+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))      
      hsRes.description=(hsRes.shome?.description_en?:'')        
        .replace('[VCITY]',' in '+hsRes.infotags.vcity+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))
        .replace('[CITY]',hsRes.infotags.city+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))
      hsRes.itext_en=(hsRes.shome.itext_en?:'')
        .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' on the map':''))
      hsRes.promotext_en=(hsRes.shome.promotext_en?:'')
        .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' on the map':''))
      hsRes.itext2_en=(hsRes.shome.itext2_en?:'')
        .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' on the map':''))
    }else{  
      if(hsRes.infotags.isfound){
        if((hsRes.inrequest?.citysight_id?:[]).size()==1 && !hsRes.inrequest?.bedroom && !hsRes.inrequest?.hometype_id && !(hsRes.inrequest?.longdiscount || hsRes.inrequest?.hotdiscount) && !(hsRes.inrequest?.date_start || hsRes.inrequest?.date_end) && hsRes.inrequest?.view!='map'){
          hsRes.title=hsRes.breadcrumbs?.citysight['title'+hsRes.context?.lang]?:''
          //hsRes.keywords=hsRes.breadcrumbs?.citysight?.keywords?:''
          hsRes.description=hsRes.breadcrumbs?.citysight['description'+hsRes.context?.lang]?:''
        }else{
          hsRes.title=(hsRes.infotext['title'+hsRes.context?.lang]?:'')	        
            .replace('[CITY]',' in '+hsRes.infotags.city)
            .replace('[METRO]',(hsRes.inrequest?.metro_id?:[]).size()==1?' near '+hsRes.breadcrumbs.metro['name'+hsRes.context?.lang]:'').replace('"','')
            .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' near '+hsRes.breadcrumbs.citysight['name2'+hsRes.context?.lang]:'')
            .replace('[HOMEROOM]',hsRes.inrequest?.bedroom?hsRes.infotags.homeroom+' ':'')
            .replace('[HOMEROOMS]',hsRes.inrequest?.bedroom?hsRes.infotags.homerooms+' ':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype)
            .replace('[HOMETYPES]',hsRes.infotags.hometypes)+          
            ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('list.longdiscount',null,new Locale("en")):'')+
            ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('list.hotdiscount',null,new Locale("en")):'')+
            ((hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('server.discounts',null,new Locale("en")):'')+
            ((hsRes.inrequest?.view=='map')?': '+messageSource.getMessage('server.on_map',null,new Locale("en")):'')
          hsRes.description=(hsRes.infotext['description'+hsRes.context?.lang]?:'')
            .replace('[METRO]',(hsRes.inrequest?.metro_id?:[]).size()==1?' near '+hsRes.breadcrumbs?.metro['name'+hsRes.context?.lang]:'').replace('"','')
            .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+(hsRes.breadcrumbs.citysight?.type==1?'near':(hsRes.breadcrumbs.citysight?.type==2?'on':'in'))+' '+hsRes.breadcrumbs.citysight['name2'+(hsRes.context?.lang?'_en':'')]:'')
            .replace('[VCITY]',' in '+hsRes.infotags.vcity+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))
            .replace('[CITY]',hsRes.infotags.city+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))
            .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
            .replace('[HOMETYPESS]',hsRes.infotags.hometypess)+
            (hsRes.inrequest?.longdiscount?'. '+messageSource.getMessage('list.longdiscount',null,new Locale("en")):'')+
            (hsRes.inrequest?.hotdiscount?'. '+messageSource.getMessage('list.hotdiscount',null,new Locale("en")):'')	                
        }
        hsRes.h1=(hsRes.infotext['header'+hsRes.context?.lang]?:'')
          .replace('[METRO]',(hsRes.inrequest?.metro_id?:[]).size()==1?'near '+hsRes.breadcrumbs.metro['name'+hsRes.context?.lang]+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''):'').replace('"','') 
          .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+(hsRes.breadcrumbs.citysight?.type==1?'near':(hsRes.breadcrumbs.citysight?.type==2?'on':'in'))+' '+hsRes.breadcrumbs.citysight['name2'+(hsRes.context?.lang?'_en':'')]+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''):'')
          .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))
          .replace('[HOMEROOM]',hsRes.inrequest?.bedroom?hsRes.infotags.homerooms+' ':'')
          .replace('[HOMETYPE]',hsRes.infotags.hometype)+
          ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('list.longdiscount',null,new Locale("en")):'')+
          ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('list.hotdiscount',null,new Locale("en")):'')+
          ((hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('server.discounts',null,new Locale("en")):'')
          
        hsRes.itext=(hsRes.infotext['itext2'+hsRes.context?.lang]?:'')
          .replace('[METRO]',(hsRes.inrequest?.metro_id?:[]).size()==1?'near '+hsRes.breadcrumbs.metro['name'+hsRes.context?.lang]+((hsRes.inrequest?.view=='map')?' on the map':''):'').replace('"','')
          .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+(hsRes.breadcrumbs.citysight?.type==1?'near':(hsRes.breadcrumbs.citysight?.type==2?'on':'in'))+' '+hsRes.breadcrumbs.citysight['name2'+(hsRes.context?.lang?'_en':'')]+((hsRes.inrequest?.view=='map')?' on the map':''):'')
          .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' on the map':''))
          .replace('[HOMEROOM]',hsRes.inrequest?.bedroom?hsRes.infotags.homeroom+' ':'')
          .replace('[HOMETYPE]',hsRes.infotags.hometype)
          .replace('[HOMETYPES]',hsRes.infotags.hometypes)
          .replace('\n',' ').replace('\r',' ').replace('</p>',', '+messageSource.getMessage('server.discounts',null,new Locale("en")))+
          ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?', '+messageSource.getMessage('list.longdiscount',null,new Locale("en")):'')+
          ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?', '+messageSource.getMessage('list.hotdiscount',null,new Locale("en")):'')+'.</p>'      
      }else{
        if(hsRes.inrequest?.where!=''){
          hsRes.title=(hsRes.infotext['title'+hsRes.context?.lang]?:'')
            .replace('[CITY]',' '+hsRes.inrequest?.where)	       
            .replace('[METRO]','').replace('[SIGN]','')
            .replace('[HOMEROOM]',hsRes.inrequest?.bedroom?((hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroom:(hsRes.inrequest?.hometype_id?hsRes.infotags.homeroom:hsRes.infotags.homeroom2))+' ':'')
            .replace('[HOMEROOMS]',hsRes.inrequest?.bedroom?hsRes.infotags.homerooms+' ':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype)
            .replace('[HOMETYPES]',hsRes.infotags.hometypes)+	        
            ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('list.longdiscount',null,new Locale("en")):'')+
            ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('list.hotdiscount',null,new Locale("en")):'')+
            ((hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('server.discounts',null,new Locale("en")):'')+
            ((hsRes.inrequest?.view=='map')?': '+messageSource.getMessage('server.on_map',null,new Locale("en")):'')
         
          hsRes.description=(hsRes.infotext['description'+hsRes.context?.lang]?:'').replace('[METRO]','').replace('[SIGN]','')
            .replace('[VCITY]',' '+hsRes.inrequest?.where+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))
            .replace('[HOMEROOMSS]',(hsRes.inrequest?.bedroom && hsRes.inrequest.hometype_id==1)?hsRes.infotags.homeroomss+' ':'')
            .replace('[HOMETYPESS]',hsRes.infotags.hometypess)+
            (hsRes.inrequest?.longdiscount?'. '+messageSource.getMessage('list.longdiscount',null,new Locale("en")):'')+
            (hsRes.inrequest?.hotdiscount?'. '+messageSource.getMessage('list.hotdiscount',null,new Locale("en")):'')          
            
          hsRes.h1=(hsRes.infotext['promotext1'+hsRes.context?.lang]?:'')
            .replace('[CITY]',hsRes.inrequest?.where+((hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):''))
            .replace('[HOMEROOM]',hsRes.inrequest?.bedroom?hsRes.infotags.homeroom+' ':'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype)+
            ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('list.longdiscount',null,new Locale("en")):'')+
            ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('list.hotdiscount',null,new Locale("en")):'')+
            ((hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?' - '+messageSource.getMessage('server.discounts',null,new Locale("en")):'')	       
         
          hsRes.itext=(hsRes.infotext['itext2'+hsRes.context?.lang]?:'')
          .replace('[METRO]',(hsRes.inrequest?.metro_id?:[]).size()==1?'near '+hsRes.breadcrumbs.metro['name'+hsRes.context?.lang]+((hsRes.inrequest?.view=='map')?' on the map':''):'').replace('"','')
          .replace('[SIGN]',(hsRes.inrequest?.citysight_id?:[]).size()==1?' '+(hsRes.breadcrumbs.citysight?.type==1?'near':(hsRes.breadcrumbs.citysight?.type==2?'on':'in'))+' '+hsRes.breadcrumbs.citysight['name2'+(hsRes.context?.lang?'_en':'')]+((hsRes.inrequest?.view=='map')?' on the map':''):'')
          .replace('[VCITY]',hsRes.infotags.vcity+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' on the map':''))
          .replace('[CITY]',hsRes.infotags.city+((!hsRes.inrequest?.metro_id && !hsRes.inrequest?.citysight_id && hsRes.inrequest?.view=='map')?' on the map':''))
          .replace('[HOMEROOM]',hsRes.inrequest?.bedroom?hsRes.infotags.homeroom+' ':'')
          .replace('[HOMETYPE]',hsRes.infotags.hometype)
          .replace('[HOMETYPES]',hsRes.infotags.hometypes)
          .replace('\n',' ').replace('\r',' ').replace('</p>',', '+messageSource.getMessage('server.discounts',null,new Locale("en")))+
          ((hsRes.inrequest?.longdiscount && !hsRes.inrequest?.hotdiscount)?', '+messageSource.getMessage('list.longdiscount',null,new Locale("en")):'')+
          ((!hsRes.inrequest?.longdiscount && hsRes.inrequest?.hotdiscount)?', '+messageSource.getMessage('list.hotdiscount',null,new Locale("en")):'')+'.</p>'
        }else{
          hsRes.title=(hsRes.infotext['promotext2'+hsRes.context?.lang]?:'')+((hsRes.inrequest?.view=='map')?': '+messageSource.getMessage('server.on_map',null,new Locale("en")):'')
          hsRes.description=(hsRes.infotext['description'+hsRes.context?.lang]?:'').replace('[METRO]','').replace('[SIGN]','').replace('[VCITY]',(hsRes.inrequest?.view=='map')?' '+messageSource.getMessage('server.on_map',null,new Locale("en")):'')
            .replace('[HOMETYPE]',hsRes.infotags.hometype).replace('[HOMETYPESS]',hsRes.infotags.hometypess).replace('[HOMEROOMSS]','').replace('[HOMEROOM]','')
          hsRes.h1=(hsRes.infotext['promotext2'+hsRes.context?.lang]?:'')+((hsRes.inrequest?.view=='map')?': '+messageSource.getMessage('server.on_map',null,new Locale("en")):'')	     
          hsRes.itext=(hsRes.infotext['itext'+hsRes.context?.lang]?:'').replace('\n',' ').replace('\r',' ')
        }
      }
    }  
	}
}
