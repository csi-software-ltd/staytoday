class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}    
    "/favicon.ico"(uri:"/favicon.ico")

    name en: "/en/$controller/$action?/$id?" {      
      controller = { (request.requestURI - request.contextPath).split('/')[2]}
      action = {(request.requestURI - request.contextPath).split('/')[3]}       
		}    
		name mainpage: "/" {
      controller = { request.serverName.indexOf('m.staytoday')==0?'m':(request.serverName.indexOf('.staytoday')==-1?'index':'home') }
			action = { request.serverName.indexOf('m.staytoday')==0?'index':(request.serverName.indexOf('.staytoday')==-1?'index':'list') }      
			hometype_id = { request.serverName.indexOf('.staytoday')==-1?0:1 }
		}
    name mainpage_en: "/en" {
      controller = { request.serverName.indexOf('m.staytoday')==0?'m':(request.serverName.indexOf('.staytoday')==-1?'index':'home') }
			action = { request.serverName.indexOf('m.staytoday')==0?'index':(request.serverName.indexOf('.staytoday')==-1?'index':'list') }      
			hometype_id = { request.serverName.indexOf('.staytoday')==-1?0:1 }
		}
    
		"/administrators"(controller : "administrators", action:"index")
		"/timeline"(controller : "index", action:"timeline")
    "/en/timeline"(controller : "index", action:"timeline")
		"/mobile"(controller : "index", action:"mobile")
    "/en/mobile"(controller : "index", action:"mobile")
		"/widget"(controller : "index", action:"widget")
    "/en/widget"(controller : "index", action:"widget")
		"/waiting"(controller : "index", action:"waiting")
    "/en/waiting"(controller : "index", action:"waiting")
    "/payment/$hash/$id"(controller : "account", action:"payment")
    "/en/payment/$hash/$id"(controller : "account", action:"payment")
    
		name timeline: "/timeline/$blog?/$year?/$month?/$day?/$id?"{
			controller = "index"
			action = "timeline"
			constraints {
				year(matches:/\d{4}/)
				month(matches:/\d{2}/)
				day(matches:/\d{2}/)
			}
		}
    name timeline_en: "/en/timeline/$blog?/$year?/$month?/$day?/$id?"{
			controller = "index"
			action = "timeline"
			constraints {
				year(matches:/\d{4}/)
				month(matches:/\d{2}/)
				day(matches:/\d{2}/)
			}
		}
		name pView: "/$uid"{
			controller = "profile"
			action = "view"
			id = {params.uid.replace('id','')}
			constraints {
				uid(matches:/id\d+/)
			}
		}
    name pView_en: "/en/$uid"{
			controller = "profile"
			action = "view"
			id = {params.uid.replace('id','')}
			constraints {
				uid(matches:/id\d+/)
			}
		}
		"/discounts/$id"(controller:"index", action:"discounts")
    "/en/discounts/$id"(controller:"index", action:"discounts")
    "/en/$id" {
			controller = "index"
			action = "popdirectionAll"
			constraints {
				id(validator:{ def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
		"/$id" {
			controller = "index"
			action = "popdirectionAll"
			constraints {
				id(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
		"/$country/$id" {
			controller = "index"
			action = "direction"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				id(validator:{def t = true; def item = it; Popdirection.withNewSession{ t = Popdirection.findByLinkname(item)?true:false}; return t})
			}
		}
    "/en/$country/$id" {
			controller = "index"
			action = "direction"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				id(validator:{def t = true; def item = it; Popdirection.withNewSession{ t = Popdirection.findByLinkname(item)?true:false}; return t})
			}
		}
		"/$country/$id/discounts" {
			controller = "index"
			action = "popdiscounts"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				id(validator:{def t = true; def item = it; Popdirection.withNewSession{ t = Popdirection.findByLinkname(item)?true:false}; return t})
			}
		}
    "/en/$country/$id/discounts" {
			controller = "index"
			action = "popdiscounts"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				id(validator:{def t = true; def item = it; Popdirection.withNewSession{ t = Popdirection.findByLinkname(item)?true:false}; return t})
			}
		}
		"/$country/$id/cottages" {
			controller = "index"
			action = "cottages"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				id(validator:{def t = true; def item = it; Popdirection.withNewSession{ t = Popdirection.findByLinkname(item)?true:false}; return t})
			}
		}
    "/en/$country/$id/cottages" {
			controller = "index"
			action = "cottages"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				id(validator:{def t = true; def item = it; Popdirection.withNewSession{ t = Popdirection.findByLinkname(item)?true:false}; return t})
			}
		}
		name hSearchTypeDomain: "/type_$type_url"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item)}; t?.id?:params.type_url=='all'?0:params.type_url }
		}
    name hSearchTypeDomain_en: "/en/type_$type_url"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item)}; t?.id?:params.type_url=='all'?0:params.type_url }
			constraints {
				type_url(validator:{ return true })
			}
		}
		name hSearchRoomDomain: "/type_$type_url/room_$bedroom"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				type_url(validator:{return it=='flats'})
			}
		}
    name hSearchRoomDomain_en: "/en/type_$type_url/room_$bedroom"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				type_url(validator:{return it=='flats'})
			}
		}    
		name hSearchMetroDomain: "/metro_$metro_url"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
		}
    name hSearchMetroDomain_en: "/en/metro_$metro_url"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
			constraints {
				metro_url(validator:{ return true })
			}
		}
   	name hSearchCitysightDomain: "/sight_$citysight_url"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			citysight_id = { def t; Citysight.withNewSession{ t = Citysight.findWhere(city_id:(City.findByDomain(request.serverName)?.id?:0),urlname:params.citysight_url)}; t?.id?:params.citysight_url }
		}
    name hSearchCitysightDomain_en: "/en/sight_$citysight_url"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			citysight_id = { def t; Citysight.withNewSession{ t = Citysight.findWhere(city_id:(City.findByDomain(request.serverName)?.id?:0),urlname:params.citysight_url)}; t?.id?:params.citysight_url }
			constraints {
				citysight_url(validator:{ return true })
			}
		}
		name hViewDomain: "/home_$linkname"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "detail"
			id = { def t; def item = params.linkname; Home.withNewSession{ t = Home.findByLinkname(params.linkname)}; t?.id?:params.linkname }
		}
    name hViewDomain_en: "/en/home_$linkname"{
			controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "detail"
			id = { def t; def item = params.linkname; Home.withNewSession{ t = Home.findByLinkname(params.linkname)}; t?.id?:params.linkname }
			constraints {
				linkname(validator:{ return true })
			}
		}
		"/alldirections"(controller : "index", action:"popdirectionAll")
    "/en/alldirections"(controller : "index", action:"popdirectionAll")
		"/$id"(controller : "index", action:"popdirection")
    "/en/$id"(controller : "index", action:"popdirection")
		//"/robots.txt"(uri : "/robots.txt")
		"/robots.txt"(controller:'index',action:'robots')
		"/sitemap_main"(uri:"/sitemap_main.xml")
		"/sitemap_home.xml"(uri:'sitemap_home')
		"/sitemap_direction.xml"(uri:'sitemap_direction')
		"/sitemap_profile.xml"(uri:'sitemap_profile')
		"/sitemap_blog.xml"(uri:'sitemap_blog')
		"/sitemap_city.xml"(uri:'sitemap_city')
		"/sitemap_map.xml"(uri:'sitemap_map')
		"/sitemap_hometype.xml"(uri:'sitemap_hometype')
		"/sitemap_homeroom.xml"(uri:'sitemap_homeroom')
		"/sitemap_hotdiscount.xml"(uri:'sitemap_hotdiscount')
		"/sitemap_longdiscount.xml"(uri:'sitemap_longdiscount')
		"/sitemap_metro.xml"(uri:'sitemap_metro')
		"/sitemap_roommap.xml"(uri:'sitemap_roommap')
		"/sitemap_metromap.xml"(uri:'sitemap_metromap')
    "/sitemap_citysight.xml"(uri:'sitemap_citysight')    
		"/advToYandex.xml"(controller:'index',action:'advToYandex')		
		"/advToAbook.xml"(controller:'index',action:'advToAbook')		
 		"/islandsToYandex.xml"(controller:'index',action:'islandsToYandex')
		
		"/sitemap_home"(controller:'index',action:'sitemap_home')
		"/sitemap_direction"(controller:'index',action:'sitemap_direction')
		"/sitemap_profile"(controller:'index',action:'sitemap_profile')
		"/sitemap_blog"(controller:'index',action:'sitemap_blog')
		"/sitemap_city"(controller:'index',action:'sitemap_city')
		"/sitemap_map"(controller:'index',action:'sitemap_map')
		"/sitemap_hometype"(controller:'index',action:'sitemap_hometype')
		"/sitemap_homeroom"(controller:'index',action:'sitemap_homeroom')
		"/sitemap_hotdiscount"(controller:'index',action:'sitemap_hotdiscount')
		"/sitemap_longdiscount"(controller:'index',action:'sitemap_longdiscount')
		"/sitemap_metro"(controller:'index',action:'sitemap_metro')
		"/sitemap_roommap"(controller:'index',action:'sitemap_roommap')
		"/sitemap_metromap"(controller:'index',action:'sitemap_metromap')
   	"/sitemap_citysight"(controller:'index',action:'sitemap_citysight')
    "/advToYandex"(controller:'index',action:'advToYandex')
    "/advToAbook"(controller:'index',action:'advToAbook')
    "/islandsToYandex"(controller:'index',action:'islandsToYandex')
    
    "/sitemap_main_mobile"(uri:"/sitemap_main_mobile.xml")
    "/sitemap_home_mobile.xml"(uri:'sitemap_home_mobile')
    "/sitemap_city_mobile.xml"(uri:'sitemap_city_mobile')
		"/sitemap_hometype_mobile.xml"(uri:'sitemap_hometype_mobile')
		"/sitemap_homeroom_mobile.xml"(uri:'sitemap_homeroom_mobile')
		"/sitemap_hotdiscount_mobile.xml"(uri:'sitemap_hotdiscount_mobile')
		"/sitemap_longdiscount_mobile.xml"(uri:'sitemap_longdiscount_mobile')
		"/sitemap_metro_mobile.xml"(uri:'sitemap_metro_mobile')
		"/sitemap_citysight_mobile.xml"(uri:'sitemap_citysight_mobile')
		"/sitemap_home_mobile"(controller:'index',action:'sitemap_home_mobile')
		"/sitemap_city_mobile"(controller:'index',action:'sitemap_city_mobile')
		"/sitemap_hometype_mobile"(controller:'index',action:'sitemap_hometype_mobile')
		"/sitemap_homeroom_mobile"(controller:'index',action:'sitemap_homeroom_mobile')
		"/sitemap_hotdiscount_mobile"(controller:'index',action:'sitemap_hotdiscount_mobile')
		"/sitemap_longdiscount_mobile"(controller:'index',action:'sitemap_longdiscount_mobile')
		"/sitemap_metro_mobile"(controller:'index',action:'sitemap_metro_mobile')		
   	"/sitemap_citysight_mobile"(controller:'index',action:'sitemap_citysight_mobile')    
    
		name hView: "/$country/$city/home_$linkname"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "detail"
			id = { def t; Home.withNewSession{ t = Home.findByLinkname(params.linkname)}; t?.id?:params.linkname }
		}
    
    name hView_en: "/en/$country/$city/home_$linkname"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "detail"
			id = { def t; Home.withNewSession{ t = Home.findByLinkname(params.linkname)}; t?.id?:params.linkname }
		}
		"/home/view/$linkname" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "view"
			id = { def t; Home.withNewSession{ t = Home.findByLinkname(params.linkname)}; t?.id?:params.linkname }
		}
		"/en/home/view/$linkname" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "view"
			id = { def t; Home.withNewSession{ t = Home.findByLinkname(params.linkname)}; t?.id?:params.linkname }
		}
		name cSearchType: "/cities/$where/type_$type_url" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?false:(City.findByName_en(item.decodeURL())?false:true))}; return t})
			}
		}
    name cSearchType_en: "/en/cities/$where/type_$type_url" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?false:(City.findByName_en(item.decodeURL())?false:true))}; return t})
			}
		}
		name cSearchRoom: "/cities/$where/type_$type_url/room_$bedroom" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?false:(City.findByName_en(item.decodeURL())?false:true))}; return t})
			}
		}
    name cSearchRoom_en: "/en/cities/$where/type_$type_url/room_$bedroom" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?false:(City.findByName_en(item.decodeURL())?false:true))}; return t})
			}
		}
		"/cities/$where/metro_$metro_url" {
			controller = "home"
			action = "list"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
			constraints {
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?false:(City.findByName_en(item.decodeURL())?false:true))}; return t})
			}
		}
    "/en/cities/$where/metro_$metro_url" {
			controller = "home"
			action = "list"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
			constraints {
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?false:(City.findByName_en(item.decodeURL())?false:true))}; return t})
			}
		}
		"/cities/$where" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			constraints {
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?false:(City.findByName_en(item.decodeURL())?false:true))}; return t})
			}
		}
    "/en/cities/$where" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			constraints {         
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?false:(City.findByName_en(item.decodeURL())?false:true))}; return t})
			}
		}
		name hSearchType: "/$country/$where/type_$type_url"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?true:false)}; return t})
			}
		}
    name hSearchType_en: "/en/$country/$where/type_$type_url"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName_en(item.decodeURL())?true:false)}; return t})
			}
		}
		name hSearchRoom: "/$country/$where/type_$type_url/room_$bedroom"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?true:false)}; return t})
				type_url(validator:{return it=='flats'})
			}
		}
    name hSearchRoom_en: "/en/$country/$where/type_$type_url/room_$bedroom"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName_en(item.decodeURL())?true:false)}; return t})
				type_url(validator:{return it=='flats'})
			}
		}
		name hSearchMetro: "/$country/$where/metro_$metro_url"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?true:false)}; return t})
			}
		}
    name hSearchMetro_en: "/en/$country/$where/metro_$metro_url"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName_en(item.decodeURL())?true:false)}; return t})
			}
		}
   	name hSearchCitysight: "/$country/$where/sight_$citysight_url"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			citysight_id = { def t; Citysight.withNewSession{ t = Citysight.findWhere(city_id:((City.findByName_en(params.where.decodeURL())?.id?:0)?:(City.findByName(params.where.decodeURL())?.id?:0)),urlname:params.citysight_url)}; t?.id?:params.citysight_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?true:false)}; return t})
			}
		}
    name hSearchCitysight_en: "/en/$country/$where/sight_$citysight_url"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "list"
			citysight_id = { def t; Citysight.withNewSession{ t = Citysight.findWhere(city_id:(City.findByName_en(params.where.decodeURL())?.id?:0)?:(City.findByName(params.where.decodeURL())?.id?:0),urlname:params.citysight_url)}; t?.id?:params.citysight_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName_en(item.decodeURL())?true:false)}; return t})
			}
		}
		name hSearch: "/$country/$where"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }	
      action = { params?.hometype_id?'s':'list' }      
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName(item.decodeURL())?true:false)}; return t})
			}
		}
    name hSearch_en: "/en/$country/$where"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }	
			action = { params?.hometype_id?'s':'list' }   
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				where(validator:{def t = true; def item = it; City.withNewSession{ t = (City.findByName_en(item.decodeURL())?true:false)}; return t})
			}
		}
		"/$country/$where/type_$type_url"{
			controller = "home"
			action = "s"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
    "/en/$country/$where/type_$type_url"{
			controller = "home"
			action = "s"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
		"/$country/$where/metro_$metro_url"{
			controller = "home"
			action = "s"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
    "/en/$country/$where/metro_$metro_url"{
			controller = "home"
			action = "s"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
		"/$country/$where/sight_$citysight_url"{
			controller = "home"
			action = "s"
			citysight_id = { def t; Citysight.withNewSession{ t = Citysight.findWhere(city_id:(City.findByName_en(params.where.decodeURL())?.id?:0)?:(City.findByName(params.where.decodeURL())?.id?:0),urlname:params.citysight_url)}; t?.id?:params.citysight_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
    "/en/$country/$where/sight_$citysight_url"{
			controller = "home"
			action = "s"
			citysight_id = { def t; Citysight.withNewSession{ t = Citysight.findWhere(city_id:(City.findByName_en(params.where.decodeURL())?.id?:0)?:(City.findByName(params.where.decodeURL())?.id?:0),urlname:params.citysight_url)}; t?.id?:params.citysight_url }
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
		"/$country/$where/room_$bedroom"{
			controller = "home"
			action = "s"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
    "/en/$country/$where/room_$bedroom"{
			controller = "home"
			action = "s"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
    "/$country/$where/type_$type_url/room_$bedroom"{
			controller = "home"
			action = "s"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				type_url(validator:{return it=='flats'})
			}
		}
    "/en/$country/$where/type_$type_url/room_$bedroom"{
			controller = "home"
			action = "s"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
				type_url(validator:{return it=='flats'})
			}
		}
		"/$country/$where"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "s"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
    "/en/$country/$where"{
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':'home' }
			action = "s"
			constraints {
				country(validator:{def t = true; def item = it; Country.withNewSession{ t = Country.findByUrlname(item)?true:false; }; return t})
			}
		}
		"/cities/$where/room_$bedroom" {
			controller = "home"
			action = "s"
		}
    "/en/cities/$where/room_$bedroom" {
			controller = "home"
			action = "s"
		}
		"/cities/$where/type_$type_url" {
			controller = "home"
			action = "s"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
		}
    "/en/cities/$where/type_$type_url" {
			controller = "home"
			action = "s"
			hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
		}
		"/cities/$where/metro_$metro_url" {
			controller = "home"
			action = "s"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
		}
    "/en/cities/$where/metro_$metro_url" {
			controller = "home"
			action = "s"
			metro_id = { def t; def item = params.metro_url; Metro.withNewSession{ t = Metro.findByUrlname(item)}; t?.id?:params.metro_url }
		}
		"/cities/$where" {
			controller = "home"
			action = "s"
		}
    "/en/cities/$where" {
			controller = "home"
			action = "s"
		}
		"/cities" {
			controller = { request.serverName.indexOf('.staytoday')==-1?'home':false }
			action = "list"
		}
    "/en/cities" {
			controller = { request.serverName.indexOf('.staytoday')==-1?'home':false }
			action = "list"
		}
		name near: "/near/type_$type_url" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':false }
      hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
      is_near = 1
			action = "list"
		}
    name near_en:  "/en/near/type_$type_url" {
			controller = { request.serverName.indexOf('m.staytoday')==0?'m':false }
      hometype_id = { def t; def item = params.type_url; Hometype.withNewSession{ t = Hometype.findByUrlname(item) }; t?.id?:params.type_url }
      is_near = 1
			action = "list"
		}
		"/home/s/$where" {
			controller = "home"
			action = "s"
		}
    "/en/home/s/$where" {
			controller = "home"
			action = "s"
		}
		"/personal/editads"(controller : "personal", action:"saveads")
    "/en/personal/editads"(controller : "personal", action:"saveads")
                
    name is_vip: "/vip" {
      controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			shome = 1
      is_vip = 1
		}
    name is_vip_en: "/en/vip" {
      controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			shome = 1
      is_vip = 1
		}
    name is_fiesta: "/prazdniki" {
      controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			shome = 1
      is_fiesta = 1
		}     
    name is_fiesta_en: "/en/prazdniki" {
      controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			shome = 1
      is_fiesta = 1
		}
    name is_renthour: "/perhours" {
      controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			shome = 1
      is_renthour = 1
		}                    
    name is_renthour_en: "/en/perhours" {
      controller = { request.serverName.indexOf('.staytoday')==-1?false:'home' }
			action = "list"
			shome = 1
      is_renthour = 1
		}
    
    "/about"(controller:"m", action:"about")
    "/en/about"(controller:"m", action:"about")
    "/terms"(controller:"m", action:"terms")
    "/en/terms"(controller:"m", action:"terms")
    "/contract"(controller:"m", action:"contract")
    "/en/contract"(controller:"m", action:"contract")    
    "/help"(controller:"m", action:"help")
    "/en/help"(controller:"m", action:"help")
    "/auth"(controller:"m", action:"auth")
    "/en/auth"(controller:"m", action:"auth")
    "/reg"(controller:"m",action:"reg")
    "/en/reg"(controller:"m",action:"reg")
    "/restore"(controller:"m", action:"restore")
    "/en/restore"(controller:"m", action:"restore")    
    "/favorites"(controller:"m", action:"favorites")
    "/en/favorites"(controller:"m", action:"favorites")
    "/mbox/$id?"(controller:"m", action:"mbox")
    "/en/mbox/$id?"(controller:"m", action:"mbox")
    "/bron/$id?"(controller:"m", action:"bron")
    "/en/bron/$id?"(controller:"m", action:"bron")
    "/inbox"(controller:"m", action:"inbox")
    "/en/inbox"(controller:"m", action:"inbox")        

    "404"(controller:{request.serverName.indexOf('m.staytoday')==0?"m":"error"}, action:"page_404")
    "401"(controller:{request.serverName.indexOf('m.staytoday')==0?"m":"error"}, action:"page_401")
    "500"(controller:{request.serverName.indexOf('m.staytoday')==0?"m":"error"}, action:"page_500")
	}
}