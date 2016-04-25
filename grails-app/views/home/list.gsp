<html>
  <head>  
    <title>${title?:''}</title>
    <meta name="keywords" content="${keywords?:''}" />
    <meta name="description" content="${description?:''}" />
  <g:if test="${infotags.isfound && !inrequest?.bedroom && ((inrequest?.metro_id?:[]).size()==0) && ((inrequest?.citysight_id?:[]).size()==0) && ((inrequest?.district_id?:[]).size()==0) && !(inrequest?.longdiscount || inrequest?.hotdiscount) && inrequest?.hometype_id==1 && !inrequest?.homeclass_id && !(inrequest?.date_start || inrequest?.date_end) && inrequest?.view!='map'}">
    <meta property="fb:app_id" content="${fb_api_key}" />
    <meta property="og:type" content="place" />
    <meta property="og:site_name" content="StayToday" />
    <meta property="og:locale" content="${context?.lang?'en_US':'ru_RU'}" />
    <meta property="og:locale:alternate" content="${context?.lang?'ru_RU':'en_US'}" />    
    <meta property="og:url" content="${createLink(mapping:breadcrumbs.city.domain?('mainpage'+context?.lang):('hSearch'+context?.lang),params:breadcrumbs.city.domain?[:]:[where:breadcrumbs?.city?.('name'+context?.lang)?:'',country:breadcrumbs?.country?.urlname?:''],base:breadcrumbs.base)}" />
    <meta property="og:title" content="${title}" />
    <meta property="og:description" content="${description}" />
    <meta property="og:image" content="${resource(dir:'images',file:breadcrumbs?.city?.picture?'cities/'+breadcrumbs?.city?.picture:'logo_large.png')}" />
    <meta property="place:location:latitude" content="${breadcrumbs?.city?.y?breadcrumbs?.city?.y/100000:0}" />
    <meta property="place:location:longitude" content="${breadcrumbs?.city?.x?breadcrumbs?.city?.x/100000:0}" />
    <meta name="twitter:card" content="summary" />
    <meta name="twitter:site" content="@StayTodayRu" />    
  </g:if>
  <g:if test="${infotags.isfound}">
    <g:if test="${breadcrumbs.city.domain}">
    <link rel="canonical" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?('hSearchMetroDomain'+context?.lang):inrequest?.bedroom?('hSearchRoomDomain'+context?.lang):(inrequest?.citysight_id?:[]).size()==1?('hSearchCitysightDomain'+context?.lang):inrequest?.shome&&inrequest?.is_vip?('is_vip'+context?.lang):inrequest?.shome&&inrequest?.is_fiesta?('is_fiesta'+context?.lang):inrequest?.shome&&inrequest?.is_renthour?('is_renthour'+context?.lang):inrequest?.hometype_id!=1?('hSearchTypeDomain'+context?.lang):('mainpage'+context?.lang),params:((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1 || inrequest?.shome)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),base:breadcrumbs.base)}" />      
    <link rel="alternate" hreflang="ru" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?('hSearchMetroDomain'):inrequest?.bedroom?('hSearchRoomDomain'):(inrequest?.citysight_id?:[]).size()==1?('hSearchCitysightDomain'):inrequest?.shome&&inrequest?.is_vip?('is_vip'+context?.lang):inrequest?.shome&&inrequest?.is_fiesta?('is_fiesta'+context?.lang):inrequest?.shome&&inrequest?.is_renthour?('is_renthour'+context?.lang):inrequest?.hometype_id!=1?('hSearchTypeDomain'):'mainpage',params:((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1 || inrequest?.shome)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),base:breadcrumbs.base)}" />
    <link rel="alternate" hreflang="en" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?('hSearchMetroDomain_en'):inrequest?.bedroom?('hSearchRoomDomain_en'):(inrequest?.citysight_id?:[]).size()==1?('hSearchCitysightDomain_en'):inrequest?.shome&&inrequest?.is_vip?('is_vip_en'):inrequest?.shome&&inrequest?.is_fiesta?('is_fiesta_en'):inrequest?.shome&&inrequest?.is_renthour?('is_renthour_en'):inrequest?.hometype_id!=1?('hSearchTypeDomain_en'):'mainpage_en',params:((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1 || inrequest?.shome)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),base:breadcrumbs.base)}" />
    </g:if><g:else>
    <link rel="canonical" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?('hSearchMetro'+context?.lang):inrequest?.bedroom?('hSearchRoom'+context?.lang):(inrequest?.citysight_id?:[]).size()==1?('hSearchCitysight'+context?.lang):inrequest?.hometype_id!=1?('hSearchType'+context?.lang):('hSearch'+context?.lang),params:[where:breadcrumbs.city?.('name'+context?.lang)?:'',country:breadcrumbs?.country?.urlname?:'']+((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),absolute:true)}" />    
    <link rel="alternate" hreflang="ru" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?'hSearchMetro':inrequest?.bedroom?'hSearchRoom':(inrequest?.citysight_id?:[]).size()==1?'hSearchCitysight':inrequest?.hometype_id!=1?'hSearchType':'hSearch',params:[where:breadcrumbs.city?.name?:'',country:breadcrumbs?.country?.urlname?:'']+((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),absolute:true)}" />
    <link rel="alternate" hreflang="en" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?'hSearchMetro_en':inrequest?.bedroom?'hSearchRoom_en':(inrequest?.citysight_id?:[]).size()==1?'hSearchCitysight_en':inrequest?.hometype_id!=1?'hSearchType_en':'hSearch_en',params:[where:breadcrumbs.city?.name_en?:'',country:breadcrumbs?.country?.urlname?:'']+((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),absolute:true)}" />
    </g:else>
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?('hSearchMetro'+context?.lang):inrequest?.bedroom?('hSearchRoom'+context?.lang):(inrequest?.citysight_id?:[]).size()==1?('hSearchCitysight'+context?.lang):inrequest?.hometype_id!=1?('hSearchType'+context?.lang):('hSearch'+context?.lang),params:[where:breadcrumbs.city?.('name'+context?.lang)?:'',country:breadcrumbs?.country?.urlname?:'']+((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:]),base:context?.mobileURL)}" />
  </g:if><g:else>    
    <link rel="canonical" href="${createLink(mapping:(inrequest?.bedroom?'cSearchRoom'+context?.lang:'cSearchType'+context?.lang),params:[where:inrequest?.where?:'']+(inrequest?.bedroom?[bedroom:inrequest?.bedroom]:[:])+(inrequest?.where?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),absolute:true)}" />
  <g:if test="${inrequest?.where}">
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(mapping:(inrequest?.bedroom?'cSearchRoom'+context?.lang:'cSearchType'+context?.lang),params:[where:inrequest?.where?:'']+(inrequest?.bedroom?[bedroom:inrequest?.bedroom]:[:])+[type_url:breadcrumbs.hometype?.urlname?:'all']+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),base:context?.mobileURL)}" />
  </g:if>
    <link rel="alternate" hreflang="ru" href="${createLink(mapping:(inrequest?.bedroom?'cSearchRoom':'cSearchType'),params:[where:inrequest?.where?:'']+(inrequest?.bedroom?[bedroom:inrequest?.bedroom]:[:])+(inrequest?.where?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),absolute:true)}" />
    <link rel="alternate" hreflang="en" href="${createLink(mapping:(inrequest?.bedroom?'cSearchRoom':'cSearchType'),params:[where:inrequest?.where?:'']+(inrequest?.bedroom?[bedroom:inrequest?.bedroom]:[:])+(inrequest?.where?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:]),base:context.absolute_lang+(context?.lang?'':'/en'))}" />
  </g:else>
    <link rel="image_src" href="${resource(dir:'images',file:breadcrumbs?.city?.picture?'cities/'+breadcrumbs?.city?.picture:'logo_large.png',absolute:true)}" />
    <meta name="layout" content="main" />
    <g:javascript library="jquery-1.8.3" />
    <g:javascript library="list" />
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/>
    <script type="text/javascript">
      var lsHomes = new Array();
    </script>
    <g:javascript>       
    var opened_popdirection=0, mouseOnDir=0,
      iX=${inrequest?.x?:(option?.x?:3700000)}, iY=${inrequest?.y?:(option?.y?:5500000)},
      iScale=${inrequest?.zoom?:3}, MAX_MAP_ZOOM=15, MAX_MAP_ZOOM_DEF=23,
      map=null, HMap=[], placemark={}, oSlider={}, gBounds=null, citysightPlacemarkMap = {};
    function initialize(iVar){      
      new Autocomplete('where', { serviceUrl:'${resource(dir:"home",file:"where_autocomplete")}' }); 
      if(!iVar)      
        sliderf();//TODO map
      $('where').onkeypress=onKey;
      jQuery('.actions a.icon').removeClass('active');
      var cnt=cnt2=0;
    <g:if test="${citysightMax && inrequest?.citysight_id}">
      <g:each in="${citysight}" var="item" status="i">  
      if($('full_citysight_id_${item.id}').checked)
        cnt++;        
      </g:each>
    </g:if><g:elseif test="${districtMax || metroMax}">
      <g:each in="${district}" var="item" status="i">  
      if($('full_district_id_${item.id}').checked)
        cnt++;
      </g:each>
      <g:each in="${metro}" var="item" status="i">  
      if($('full_metro_id_${item.id}').checked)
        cnt++;
      </g:each>
    </g:elseif>
      if(cnt > 0)
        jQuery('#neighborhood_container').removeClass('search_filter closed').addClass('search_filter open');      
      if($('full_nref').checked)
        cnt2++;
      if($('full_rating').checked)
        cnt2++;
      if($('full_hotdiscount').checked)
        cnt2++;
      if($('full_longdiscount').checked)
        cnt2++;
      if(cnt2 > 0)
        jQuery('#additional_container').removeClass('search_filter closed').addClass('search_filter open');      
      if ($('view').value != 'map'){
        if(navigator.userAgent.search('Chrome')>-1)
          $('search_filters').setStyle({ overflow: 'visible' });        
        jQuery(".search_container").css({ minHeight: '755px' });
        $("search_body").setStyle({ minHeight: '1010px', height: '100%'});
        $("search_filters_wrapper").removeClassName('scrollable');
        $('search_filters').setStyle({ marginLeft: '-250px' });
        $("map_wrapper").show();
        $("map_view").hide();
        $("search_filters_toggle").hide();        
        if($('view').value=='list')
          jQuery('.actions a.icon:first').addClass('active');  
        if($('view').value=='photo')
          jQuery('.actions a.icon').eq(1).addClass('active');          
        Yandex(0); //small map
        window.onscroll = function(){ 
          Sidebar();
        }
      }else{      
        jQuery(".search_container").css({ minHeight: '600px' });
        $("search_body").setStyle({ minHeight: '600px', height: '600px'});
        $("search_filters_wrapper").addClassName('scrollable');        
        $('search_filters').setStyle({ marginLeft: '0px' });
        $("map_wrapper").hide();
        $("map_view").show();
        $("search_filters_toggle").show();                         
        if($("map_wrapper")!=undefined)
          $("map_wrapper").setStyle({height: '0px', width: '0px'});
        if($("map_large_canvas")!=undefined)
          $("map_large_canvas").setStyle({height: <g:if test="${metroNext || citysightNext}">'385px'</g:if><g:else>'404px'</g:else>, width: '730px'});
        jQuery(".actions a.icon").eq(2).addClass('active');             
        Yandex(1); //big map        
      }           	
      $("hometype_id").enable();
      $("homeperson_id").enable(); 
      $("date_start").enable();
      $("date_start_value").enable();      
      $("date_start_year").enable();
      $("date_start_month").enable();
      $("date_start_month").enable();	  
      $("date_start_day").enable(); 
      $("date_end").enable();
      $("date_end_value").enable();      
      $("date_end_year").enable();
      $("date_end_month").enable();
      $("date_end_month").enable();	  
      $("date_end_day").enable();            
      if(!iVar)
        setFilterForm();
    <g:if test="${(inrequest?.view?:'')!='map'}">      
      if($("map_large_canvas")!=undefined)
        $("map_large_canvas").setStyle({height: '0px', width: '0px'});
      if($("map_wrapper")!=undefined)
        $("map_wrapper").setStyle({height: '220px', width: '220px'});
      $("search_filters_wrapper").setStyle({ height: $("search_body").getHeight()+28+'px' });      
    </g:if><g:else>      
      $("search_filters_wrapper").setStyle({ height: '600px' });
      <g:if test="${citysightMax && inrequest?.citysight_id}"><g:each in="${citysight}" var="item" status="i">
        <g:if test="${(inrequest?.citysight_id?:[]).contains(item?.id.toLong())}">                  
          addCitysightMarker(${item.x},${item.y},"${item.title}",${item.id});
        </g:if>          
      </g:each></g:if>
    </g:else>
    }    	
    function getBanner(){		  
      var params='zoom='+map.getZoom()+'&x='+Math.round(map.getCenter()[1]*100000)+
				'&y='+Math.round(map.getCenter()[0]*100000);
      params=params.replace(/\&amp;/g,'&');
      <g:remoteFunction controller='home' action='getbanner' update='banner_place' params="params" />      
    }    
    function sliderf(){//iVar                  
      var slider = $('slider-range');
      oSlider=new Control.Slider(slider.select('.ui-slider-handle'), slider, {            
        range: $R(${valuta?.min?:100}, ${valuta?.max?:5000}),
        sliderValue:[${inrequest?.price_min?:(valuta?.min?:100)}, ${inrequest?.price_max?:(valuta?.max?:5000)}],
        values: [<g:join in="${arr}" delimiter=", "/>],
        step: ${valuta?.step?:100},
        spans: ["slider_span"],
        restricted: true,
        onChange: function(values){			
          $('slider_user_min').update(values[0]);
          if(values[1]==${valuta?.max?:5000})
            $('slider_user_max').update(values[1]+'+');
          else
            $('slider_user_max').update(values[1]);		   
          $("price_min").value = values[0];
          $("price_max").value = values[1];          
          setFilterAndSubmit();
        }
      });
      $('slider_user_min').update('${inrequest?.price_min?:(valuta?.min?:100)}');     
      var price_max=${inrequest?.price_max?:(valuta?.max?:5000)};
      if(price_max==${valuta?.max?:5000})
        price_max=price_max+'+';
      else
        price_max=price_max;        
      $('slider_user_max').update(''+price_max+'');
      $("price_min").value = ${inrequest?.price_min?:(valuta?.min?:100)};
      $("price_max").value = ${inrequest?.price_max?:(valuta?.max?:5000)};	  
    }    
    function setFilterForm(){
      if($("where").value.length>2){      	      
        setFilter();
        <g:if test="${!(inrequest?.search_in_map?:0)}">		
	      $('is_main_filter').value=1;//for gBounds
        </g:if>
      }
    }
    function submitForm(){
      if($("where").value.length>2){
        $("where").removeClassName('red0');
        disableFormParams();
        $('search_form').submit();
      }else{
        $("where").addClassName('red0');
      }
    }    
    function setFilterAndSubmit(iId){
      iId = iId || 0
      setFilter();
      $('wrongwhere').update('');
      $('is_main_filter').value=iId;//for gBounds
      $('filter_form_submit').click();
    }	
    function setFilter(){
      $('where').value="<g:rawHtml>${inrequest?.where?:''}</g:rawHtml>";
      $('where_filter').value = $('where').value;
      <g:each in="${homeperson}" var="item" status="i"><g:if test="${item?.id==inrequest?.homeperson_id}">	  
        $("homeperson_id_option_${i}").selected=true;
      </g:if></g:each>	  
      $('homeperson_id_filter').value = $('homeperson_id').value;
      var bHometype=0;
      <g:each in="${hometype}" var="item" status="i"><g:if test="${item?.id==inrequest?.hometype_id}">
        $('hometype_id_option_${i+1}').selected=true;
        bHometype=1;
      </g:if></g:each>
      if(!bHometype)
        $('hometype_id_option_0').selected=true;	  
      $('hometype_id_filter').value = $('hometype_id').value;	  
      $('sort_filter').value = $('sort').value;      
      $('view_filter').value = $('view').value;      
      $('date_start_month_filter').value = $('date_start_month').value;
      $('date_start_day_filter').value = $('date_start_day').value;
      $('date_start_year_filter').value = $('date_start_year').value;	  	  
      $('date_end_month_filter').value = $('date_end_month').value;
      $('date_end_day_filter').value = $('date_end_day').value;
      $('date_end_year_filter').value = $('date_end_year').value;	  
      if(!$("redo_search_in_map").checked){
        $("full_x").value=0;
        $("full_y").value=0;
        $("full_zoom").value=0;
        $("x_filter").value=0;
        $("y_filter").value=0;
        $("zoom_filter").value=0;
        $("params_filter").value="${param?:0}";	
        $("full_params").value=0;
      }	  	    
    }	
    function setPostFilter(sId){
      if($(sId).checked)
        $('full_'+sId).checked=true;
      else
        $('full_'+sId).checked=false;	                          
    }    
    function setFilterFromPostFilter(sId){
      var sId1=sId.replace('full_','');      
      if($(sId1)){
        if($(sId).checked)
          $(sId1).checked=true;
        else{
          $(sId1).checked=false;          
        }
      }      
      //remove hidden field in filterForm
      if(!$(sId).checked){
        var sName=jQuery('#'+sId).attr('name');
        var form = $('filter_form');
        var input = form[sName];
        if(input!=undefined){        
          if(sName=='district_id'){
            for(var i=0;i<input.length;i++)  
              if(input[i].type=="hidden" && input[i].value==$(sId).value)
                input[i].remove();
          }else if(sName=='metro_id'){
            for(var i=0;i<input.length;i++)  
              if(input[i].type=="hidden" && input[i].value==$(sId).value)
                input[i].remove();
          }else if(sName=='citysight_id'){
            for(var i=0;i<input.length;i++)  
              if(input[i].type=="hidden" && input[i].value==$(sId).value)
                input[i].remove();
          }else{                            
            input.value=0;  
          }
        }
      }          
    }   
    function setFilterFullAndSubmit(){
      setFilterFull();
      $('full_filter_form').submit();
    }
    function setFilterFull(){
      if($("redo_search_in_map").checked)
        $('full_search_in_map').value = 1;
      $('full_where').value = $('where').value;
      if($('hometype_id_fullfilter'))
        $('full_hometype_id').value = $('hometype_id_fullfilter').value;
      else $('full_hometype_id').value = $('hometype_id').value;
      $('full_homeperson_id').value = $('homeperson_id').value;
      $('full_sort').value = $('sort').value;	
      $('full_offset').value = $('offset_list').value;   
      $('full_view').value = $('view').value;
      $('full_keywords').value = $('keywords').value;     
      $('full_date_start_month').value = $('date_start_month').value;
      $('full_date_start_day').value = $('date_start_day').value;
      $('full_date_start_year').value = $('date_start_year').value;	  
      $('full_date_end_month').value = $('date_end_month').value;
      $('full_date_end_day').value = $('date_end_day').value;
      $('full_date_end_year').value = $('date_end_year').value;            
      
      if($("price_max").value!=${valuta?.max?:5000})
        $("full_price_max").value=$("price_max").value;
      else 
        $("full_price_max").value=0;      
      if($("price_min").value!=${valuta?.min?:100})  
        $("full_price_min").value=$("price_min").value;
      else
        $("full_price_min").value=0;      
      //disable for short link>>
      if($("full_search_in_map").getValue()==0)
        $("full_search_in_map").disable();
      if($("full_hometype_id").getValue()==0)
        $("full_hometype_id").disable();  
      if($("full_sort").getValue()==0)
        $("full_sort").disable();
      if($('full_offset').getValue()==0)
        $('full_offset').disable();
      if($("full_view").getValue()==0)
        $("full_view").disable();
      if($("full_keywords").getValue()=="")
        $("full_keywords").disable();
      if($("full_date_start_month").getValue()=="")
        $("full_date_start_month").disable();
      if($("full_date_start_day").getValue()=="")
        $("full_date_start_day").disable();
      if($("full_date_start_year").getValue()=="")
        $("full_date_start_year").disable();
      if($("full_date_end_month").getValue()=="")
        $("full_date_end_month").disable();
      if($("full_date_end_day").getValue()=="")
        $("full_date_end_day").disable();
      if($("full_date_end_year").getValue()=="")
        $("full_date_end_year").disable();        
      if($("full_price_min").getValue()==0)
        $("full_price_min").disable();
      if($("full_price_max").getValue()==0)
        $("full_price_max").disable();        
      if($("full_params").getValue()==0)
        $("full_params").disable();
      if($("full_x").getValue()==0)
        $("full_x").disable();
      if($("full_y").getValue()==0)
        $("full_y").disable(); 
      if($("full_zoom").getValue()==0)
        $("full_zoom").disable();
      if($("min_bedrooms") && $("min_bedrooms").getValue()=="")
        $("min_bedrooms").disable();
      if($("min_bathrooms").getValue()=="")
        $("min_bathrooms").disable();
      if($("min_beds").getValue()=="")
        $("min_beds").disable();            
    }    
    function addCitysightMarker(iX,iY,sName,iId){
      var sMarker="${resource(dir:'images',file:'marker_citysight.png')}";     
      var placemark = new ymaps.Placemark([iY/100000, iX/100000], { 
        hintContent:sName}, { 
        draggable: false,                
        hasHint: 1,
        hasBalloon: false,
        iconImageHref:sMarker,
        iconImageSize: [19,37],
        iconImageOffset:[-14,-35],
        iconContentOffset:[-1,10],        
      });      
      citysightPlacemarkMap[iId]=placemark;
      map.geoObjects.add(placemark);      
    }     
    function removeCitysightMarker(iId){
      if(map) {
        map.geoObjects.remove(citysightPlacemarkMap[iId]); 
        citysightPlacemarkMap[iId]=null;
      }
    }    
    function toggleCitysightMarker(iX,iY,sName,iId){
      <g:if test="${(inrequest?.view?:'')=='map'}">
        if(citysightPlacemarkMap[iId]!=null)
          removeCitysightMarker(iId);
        else
          addCitysightMarker(iX,iY,sName,iId); 
      </g:if>
    }    
    function addMarker(iX,iY,iNumber,bStyle,sName,sBalloon,bBigMap,sHome_linkname,sParams,sCountry,sCity){      
      var sMarker= (bStyle)?"${resource(dir:'images',file:'marker_select.png')}":"${resource(dir:'images',file:'marker.png')}";             
      var  placemark = new ymaps.Placemark([iY/100000, iX/100000], { 
        hintContent:(bBigMap)?'<div style=\"width:220px;margin:5px\" class=\"relative\">'+sBalloon+'</div>':sName }, { 
        draggable: false,                
        hasHint: 1,
        hasBalloon: false,
        iconImageHref:sMarker,
        iconImageSize: [25,41],
        iconImageOffset:[-14,-35],
        iconContentOffset:[-1,10],
        iconContentLayout:ymaps.templateLayoutFactory.createClass('<div class="point">'+iNumber+'</div>')
      });
      placemark.description = sBalloon;      
      placemark.events.add("click", function(result) { 
        var aParams=sParams.split(';');	
        if(aParams.length==3){
          var tmpParams=[];
          if(aParams[0].length)
            tmpParams.push('date_start='+aParams[0]);
          if(aParams[1].length)
            tmpParams.push('date_end='+aParams[1]);
          if(aParams[2].length && aParams[2]!='1')
            tmpParams.push('homeperson_id='+aParams[2]);
          if(tmpParams.length)
            tmpParams.push('from_search=1');
          transit(${context.is_dev?1:0},[sHome_linkname,sCity,sCountry],tmpParams.join(),1);
        }  
      },placemark);     
      if(map){ 
        if(!bStyle)
          gBounds.add(placemark);
        else
          map.geoObjects.add(placemark);
      }
      if(bStyle)
        tmpPlacemark=placemark;	  	  
    }	
    function removeMarkers(){
      if(map) map.geoObjects.remove(tmpPlacemark); 
    }
    function Yandex(bMap){
	    ymaps.ready(function() {
        if(!bMap){
          if(map)
            map.destroy();
          map = new ymaps.Map("map_small_canvas",{center:[iY/100000,iX/100000],zoom:iScale,behaviors:["default","scrollZoom"]});         
        } else {
          if(map)
            map.destroy();
          map = new ymaps.Map("map_large_canvas",{center:[iY/100000,iX/100000],zoom:iScale,behaviors:["default","scrollZoom"]});                 
        }  
        map.controls.add("smallZoomControl").add("scaleLine");             
        if(bMap){
          map.controls.add(new ymaps.control.MapTools({ items: ["default"]}))
            .add("searchControl")
            .add(new ymaps.control.MiniMap({ type:'yandex#map' },{ size: [90,90] }));                                 
        }                
        map.events.add("boundschange", function(e) {
          setCoordinatesAndCenter(0);updateMap(0);
        });  
        gBounds =new ymaps.GeoObjectCollection();
        for(var i = 0; i<lsHomes.length; i++)
          addMarker(lsHomes[i][0],lsHomes[i][1],lsHomes[i][2],lsHomes[i][3],lsHomes[i][4],lsHomes[i][5],lsHomes[i][6],lsHomes[i][7],lsHomes[i][8],lsHomes[i][9],lsHomes[i][10]);
        setBounds();        
        <g:if test="${!flash.error && inrequest?.view!='map'}">
          getBanner();
        </g:if>
      });      
    }
    function setBounds(){ 
      map.geoObjects.add(gBounds);
      if(gBounds.getBounds()!=null)
        map.setBounds(gBounds.getBounds());   
      map.setCenter(map.getCenter(), (map.getZoom()!=MAX_MAP_ZOOM_DEF)?(map.getZoom()-1):MAX_MAP_ZOOM, { checkZoomRange: true });              
    }    
    function setCoordinatesAndCenter(iVar){	
      var bFlag=0;
      if(!iVar){
        if($("redo_search_in_map").checked)
          bFlag=1;		
      }else
        bFlag=1;		
      if(bFlag){	
        var center=map.getCenter();
        iX=Math.round(center[1]*100000);
        iY=Math.round(center[0]*100000);	 
        iScale = map.getZoom();	 	  
        $("full_x").value=iX;
        $("full_y").value=iY;
        $("full_zoom").value=iScale;
        $("x_filter").value=iX;
        $("y_filter").value=iY;
        $("zoom_filter").value=iScale;				
      }else{
        $("full_x").value=0;
        $("full_y").value=0;
        $("full_zoom").value=0;
        $("x_filter").value=0;
        $("y_filter").value=0;
        $("zoom_filter").value=0;	
      }	  
    }	
    function updateMap(iVar){//toDO iVar??
      if($("redo_search_in_map").checked){	
        if(iVar>0)
          HMap=[];             
        request_geo_objectsin(iVar);          		  		  
      }
    }  
    var curBigMapSize='small';
    function setMap(){  
      if(curBigMapSize=='small'){
        setFullMap();
        curBigMapSize='big';
      }else{
        setSmallMap();
        curBigMapSize='small';
      }
    }     
    function resetButton(){//reset both full options,options from right panel	 		
      if($('nref')!=undefined)
        $('nref').checked=false;
      if($('rating')!=undefined)
        $('rating').checked=false;	
      if($('full_nref')!=undefined)
        $('full_nref').checked=false;
      if($('full_rating')!=undefined)
        $('full_rating').checked=false;	
      if($("hotdiscount"))
        $("hotdiscount").checked=false;        
      if($("longdiscount"))
        $("longdiscount").checked=false;        
      <g:each in="${hometypeFilter}" var="item" status="i">
        if($('home_type_${i}'))
          $('home_type_${i}').checked=false;
        if($('full_home_type_${item.id}'))
          $('full_home_type_${item.id}').checked=false;  
      </g:each>                          
      <g:each in="${homeoption}" var="item" status="i">
        <g:if test="${option[item.fieldname]}">                
		      if($('amenity_${i}'))
            $('amenity_${i}').checked=false;		  
        </g:if>                
        if($('full_amenity_${i}'))
          $('full_amenity_${i}').checked=false;
      </g:each>
      <g:each in="${homeoption_apartament}" var="item" status="i">              
        if($('full_apartament_${i}'))
          $('full_apartament_${i}').checked=false;
      </g:each>      
      <g:each in="${hidInrequest}" var="item" status="i">
        $('filter_form_option_${i}').value=0;
      </g:each>      
      <g:each in="${district}" var="item" status="i">
        if($('district_id_${item.id}'))
          $('district_id_${item.id}').checked=false;
        if($('full_district_id_${item.id}'))
          $('full_district_id_${item.id}').checked=false;
      </g:each>      
      <g:each in="${metro}" var="item" status="i">
        if($('metro_id_${item.id}'))
          $('metro_id_${item.id}').checked=false;
        if($('full_metro_id_${item.id}'))
          $('full_metro_id_${item.id}').checked=false;
      </g:each>
      <g:each in="${citysight}" var="item" status="i">
        if($('citysight_id_${item.id}'))
          $('citysight_id_${item.id}').checked=false;
        if($('full_citysight_id_${item.id}'))
          $('full_citysight_id_${item.id}').checked=false;
      </g:each>
      <g:each in="${hidInrequest_district_id}" var="item" status="i">
        $('filter_form_district_id_${i}').remove();
      </g:each>
      <g:each in="${hidInrequest_metro_id}" var="item" status="i">
        $('filter_form_metro_id_${i}').remove();                   
      </g:each>  
      <g:each in="${hidInrequest_citysight_id}" var="item" status="i">
        $('filter_form_citysight_id_${i}').remove();                   
      </g:each>
      <g:each in="${hidInrequest_apart}" var="item" status="i">
        $('filter_form_apart_${i}').remove();                   
      </g:each>
      if($("min_bedrooms"))      
        $('min_bedrooms').options[0].selected = true;
      $('min_bathrooms').options[0].selected = true;
      $('min_beds').options[0].selected = true;     
      if(!(oSlider.values[0]==${valuta?.min} && oSlider.values[1]==${valuta?.max}))               
        oSlider.reset();
      $('keywords').value='';     	          
      $('filter_form_submit').click();
    }
    function onKey(e){
      var iKeycode;
      if (window.event) iKeycode = window.event.keyCode;
      else if (e) iKeycode = e.which;
      else return true;    
      if (iKeycode == 13)
        submitForm();
    }  
    var star_id=0;
    function addtofavourite(lId){
      star_id=lId;
      <g:remoteFunction action='selectcompany' onSuccess='setFavourite(e)' params="\'id=\'+lId" />
    }  
    function setFavourite(e){
      var bFlag=0;
      var arr=e.responseJSON.wallet;
      for(var i=0;i<arr.size();i++){
        if(star_id==arr[i]){
          $('star_icon_'+star_id).addClassName("starred");
          bFlag=1;
        }
      }
      if(!bFlag)
        $('star_icon_'+star_id).removeClassName("starred");      
      if(arr.length>0){
        $('favorite').removeClassName('no_active');
        $('favorite').href='<g:createLink controller="trip" action="favorite" absolute="true"/>'; 
        $('favorite').parentElement.addClassName(' starred');
      } else {
        $('favorite').addClassName('no_active');
        $('favorite').href='javascript:void(0)';
        $('favorite').parentElement.removeClassName(' starred');
      }
    }  
    function togglePopDirection(iType){
      if($('regionalCity'))
        $('regionalCity').hide();
      if ($("select_popdirection").style.display == 'none'){
        updatePopDirection(iType);
        opened_popdirection = 1;
      } else {
        jQuery("#select_popdirection").slideToggle();
        opened_popdirection = 0;
      }
    }
    function updatePopDirection(iType){
      <g:remoteFunction controller='home' action='selectpopcities' update='select_popdirection' onComplete="jQuery('#select_popdirection').slideDown()" params="\'country_id=\'+iType" />
    }
    function clickPopDirection(sWhere){
      <g:remoteFunction controller='home' action='cityselectstat'/>
      $('where').value = sWhere;
      disableFormParams();
      $('search_form').submit();
    }
    function checkDropDowns(){
      if (opened_popdirection){
        if (!mouseOnDir){
          opened_popdirection = 0;
          $("select_popdirection").style.display = 'none';
        }
      }      
    }
    function openPrintPage(){     
      transit(${context.is_dev?1:0},['home/searchprint'],jQuery("#full_filter_form :input[value]").serialize().replace('&',','),1,'','${context?.lang}');
    }
    </g:javascript>
    <script type="text/javascript">
      function clickPaginate(event){
        event.stop();            
        var link = event.element();
        if(link.href == null)
          return;        
        var sLink=link.href.split('&sort')[0];     
        sLink+='&'+$('filter_form').serialize();          
        new Ajax.Updater({ success: $('results')}, sLink, { evalScripts: true });
        jQuery("body:not(:animated)").animate({ scrollTop: 0 }, 1000);
        jQuery("html").animate({ scrollTop: 0 }, 500)
      }              
    </script>
    <style type="text/css">
      .hlisting .fn.title{margin-top:0!important}
      .hlisting .title .name{margin-left:40px}
      .hlisting .description{margin-left:20px}
    </style>
  </head>
  <body onload="initialize(0)">
            <tr style="height:110px">
              <td width="250" class="rent s0">
                <a rel="nofollow" onclick="<g:if test="${isLoginDenied}">showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>
              </td>
              <td width="730" colspan="3" class="search" style="padding:3px 0 0 0"><!--noindex-->
                <g:form name="search_form" mapping="${context?.lang?'en':''}" controller="home" action="search" method="get">
                  <table class="float" width="553" cellpadding="5" cellspacing="0" border="0">
                    <tr>
                      <td width="145" valign="top" class="padd10">
                        <label for="where">${message(code:'common.where')}</label>
                        <input type="text" id="where" name="where" value="<g:rawHtml>${inrequest?.where?:''}</g:rawHtml>" placeholder="${message(code:'common.where_default')}" autocomplete="off" style="width:145px" />
                        <div id="where_autocomplete" class="autocomplete" style="display:none"></div>			
                      </td>                  
                      <td width="107" valign="top">
                        <label for="hometype_id">${message(code:'common.home_type')}</label>
                        <select id="hometype_id" name="hometype_id" style="width:107px">
                          <option id="hometype_id_option_0" <g:if test="${0==(inrequest?.hometype_id?:0)}">selected="selected"</g:if> value="all">${message(code:'common.any')}</option>
                        <g:each in="${hometype}" var="item" status="i">
                          <option id="hometype_id_option_${i+1}" <g:if test="${item?.id==inrequest?.hometype_id}">selected="selected"</g:if> value="${item?.urlname}">${item?.('name'+context?.lang)}</option>
                        </g:each>
                        </select>
                      </td>
                      <td width="43" valign="top">
                        <label for="homeperson_id">${message(code:'common.guests')}</label>
                        <select id="homeperson_id" name="homeperson_id" style="width:43px">
                        <g:each in="${homeperson}" var="item" status="i">            
                          <option id="homeperson_id_option_${i}" <g:if test="${item?.id==inrequest?.homeperson_id}">selected="selected"</g:if> value="${item?.id}">${item?.kol}</option>
                        </g:each>
                        </select>                  
                      </td>
                      <td width="85" valign="top">
                        <label for="date_start">${message(code:'common.date_from')}</label>
                        <span class="dpicker">
                          <calendar:datePicker name="date_start" value="${inrequest?.date_start}" dateFormat="%d-%m-%Y"/>
                        </span>
                      </td>
                      <td width="85" valign="top" class="dpicker">
                        <label for="date_end">${message(code:'common.date_to')}</label>
                        <span class="dpicker">
                          <calendar:datePicker name="date_end" value="${inrequest?.date_end}" dateFormat="%d-%m-%Y"/>                
                        </span>
                      </td>              
                    </tr>
                    <tr>
                      <td colspan="3" valign="top" class="padd10">
                        <div class="dropdown-list relative">                          
                          <small id="a_select_popdirection" class="select" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0" onclick="togglePopDirection(1)"><a rel="nofollow" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0">${message(code:'common.choose_city')}</a></small>
                          <div class="select_dropdown" id="select_popdirection" style="display:none" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0"></div>                          
                        </div>
                      </td>                      
                    </tr>
                  </table>                  
                  <input type="hidden" id="view" name="view" value="${inrequest?.view?:'list'}" />                  
                </g:form><!--/noindex-->
                <a class="button main col" rel="nofollow" onclick="submitForm()">${message(code:'common.renthome')}</a>
              </td>
            </tr>
            <tr style="height:30px">
              <td class="bg_shadow">
                <div class="topl relative"><g:if test="${inrequest?.where!=''}">
                  <span class="count"><b>${message(code:'common.records.found')}</b><span id="ads_count">${count?:0}</span></span>
                </g:if></div>
              </td>
              <td colspan="3" class="bg_shadow">
              <g:if test="${breadcrumbs.country}">                
                <div class="breadcrumbs padd10" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">
                    <a href="${createLink(uri:'',base:context?.absolute_lang)}" rel="v:url" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span>
                  <span typeof="v:Breadcrumb">
                    <g:if test="${breadcrumbs.country.is_index}"><a href="<g:createLink controller='index' action='popdirectionAll' params='[id:breadcrumbs.country?.urlname]' base='${context?.absolute_lang}'/>" rel="v:url" property="v:title"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${breadcrumbs.country?.urlname}'],'','','','${context?.lang}')" property="v:title"></g:else>
                  ${breadcrumbs.country?.('name'+context?.lang)}<g:if test="${breadcrumbs.country.is_index}"></a></g:if><g:else></span></g:else> &#8594;
                  </span>
                <g:if test="${breadcrumbs.region.is_show && breadcrumbs.city}">
                  <div class="dropdown-list relative inline">
                    <span class="select" typeof="v:Breadcrumb"><a rel="nofollow" onclick="$('regionalCity').show()" property="v:title">${breadcrumbs.region?.('name'+context?.lang)}</a></span>
                    <div id="regionalCity" class="select_dropdown" style="height:auto;display:none">
                      <img src="${resource(dir:'images',file:'delete.gif')}" alt="" onclick="$('regionalCity').hide()" border="0" class="col" />
                    <g:each in="${breadcrumbs.regionalCity}" var="item">
                      <g:if test="${item.is_index}"><a class="city" href="<g:createLink mapping='${item.domain?('hSearchTypeDomain'+context?.lang):('hSearchType'+context?.lang)}' params='${(item.domain?[type_url:'all']:[where:item?.name,country:breadcrumbs.country?.urlname,type_url:'all'])}' base='${item.domain?'http://'+item.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>"></g:if>
                      <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_all','${item?.('name'+context?.lang)}','${breadcrumbs.country?.urlname}'],'','','','${context?.lang}')"></g:else>
                      ${item?.('name'+context?.lang)}<g:if test="${item.is_index}"></a></g:if><g:else></span></g:else>
                    </g:each>
                    </div>                          
                  </div> &#8594;
                </g:if><g:if test="${breadcrumbs?.hometype}"><span typeof="v:Breadcrumb">
                  <g:if test="${breadcrumbs.city?.is_index}"><a href="<g:createLink mapping='${breadcrumbs.city.domain?('hSearchTypeDomain'+context?.lang):('hSearchType'+context?.lang)}' params='${breadcrumbs.city.domain?[type_url:'all']:[where:inrequest?.where,country:breadcrumbs.country.urlname,type_url:'all']}' base='${breadcrumbs.base}'/>" rel="v:url" property="v:title"></g:if>
                  <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_all','${inrequest?.where}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" property="v:title"></g:else>
                    ${breadcrumbs.city?.('name'+context?.lang)?:breadcrumbs.region?.('name'+context?.lang)}<g:if test="${breadcrumbs.city?.is_index}"></a></g:if><g:else></span></g:else><g:if test="${breadcrumbs.typebedroom||breadcrumbs.metro||breadcrumbs.citysight}"> &#8594;
                  </span><span typeof="v:Breadcrumb">
                    <a href="<g:createLink mapping='${breadcrumbs.city.domain?(breadcrumbs.hometype?.urlname=='flats'?('mainpage'+context?.lang):('hSearchTypeDomain'+context?.lang)):('hSearchType'+context?.lang)}' params='${breadcrumbs.city.domain?(breadcrumbs.hometype?.urlname=='flats'?[:]:[type_url:breadcrumbs.hometype?.urlname]):[where:inrequest?.where,country:breadcrumbs.country.urlname,type_url:breadcrumbs.hometype?.urlname]}' base='${breadcrumbs.base}'/>" rel="v:url" property="v:title">${breadcrumbs.hometype['name3'+context?.lang]}</a> &#8594; </span> ${(breadcrumbs?.metro && breadcrumbs?.metro?.('name'+context?.lang))?breadcrumbs?.metro?.('name'+context?.lang):(breadcrumbs?.citysight &&breadcrumbs?.citysight?.('name'+context?.lang))?breadcrumbs?.citysight?.('name'+context?.lang):((breadcrumbs?.typebedroom &&breadcrumbs.typebedroom['name3'+context?.lang])?breadcrumbs.typebedroom['name3'+context?.lang]:'')}</g:if><g:else> &#8594; </span> ${breadcrumbs.hometype['name3'+context?.lang]}</g:else>
                </g:if><g:elseif test="${breadcrumbs?.bedroom}"><span typeof="v:Breadcrumb">
                  <g:if test="${breadcrumbs?.city}">
                    <g:if test="${breadcrumbs.city.is_index}"><a href="<g:createLink mapping='${breadcrumbs.city.domain?('hSearchTypeDomain'+context?.lang):('hSearchType'+context?.lang)}' params='${breadcrumbs.city.domain?[type_url:'all']:[where:inrequest?.where,country:breadcrumbs.country.urlname,type_url:'all']}' base='${breadcrumbs.base}'/>" rel="v:url" property="v:title"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_all','${inrequest?.where}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" property="v:title"></g:else>
                    ${breadcrumbs?.city?.('name'+context?.lang)}<g:if test="${breadcrumbs.city.is_index}"></a></g:if><g:else></span></g:else>
                  </g:if><g:else>
                    <span onclick="transit(${context.is_dev?1:0},['type_all','${inrequest?.where}','cities'],'','','','${context?.lang}')" property="v:title">${breadcrumbs.region?.('name'+context?.lang)}</span>
                  </g:else> &#8594; </span> ${inrequest?.hometype_id?breadcrumbs.bedroom['name3'+context?.lang]:breadcrumbs.bedroom['name'+(context?.lang?'3'+context?.lang:'5')]}
                </g:elseif><g:elseif test="${breadcrumbs?.metro}"><span typeof="v:Breadcrumb">
                  <g:if test="${breadcrumbs.city.is_index}"><a href="<g:createLink mapping='${breadcrumbs.city.domain?('hSearchTypeDomain'+context?.lang):('hSearchType'+context?.lang)}' params='${breadcrumbs.city.domain?[type_url:'all']:[where:inrequest?.where,country:breadcrumbs.country.urlname,type_url:'all']}' base='${breadcrumbs.base}'/>" rel="v:url" property="v:title"></g:if>
                  <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_all','${inrequest?.where}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" property="v:title"></g:else>
                  ${breadcrumbs.city?.('name'+context?.lang)}<g:if test="${breadcrumbs.city.is_index}"></a></g:if><g:else></span></g:else> &#8594; </span> ${breadcrumbs.metro?.('name'+context?.lang)}
                </g:elseif><g:elseif test="${breadcrumbs?.citysight}"><span typeof="v:Breadcrumb">
                  <g:if test="${breadcrumbs.city.is_index}"><a href="<g:createLink mapping='${breadcrumbs.city.domain?('hSearchTypeDomain'+context?.lang):('hSearchType'+context?.lang)}' params='${breadcrumbs.city.domain?[type_url:'all']:[where:inrequest?.where,country:breadcrumbs.country.urlname,type_url:'all']}' base='${breadcrumbs.base}'/>" rel="v:url" property="v:title"></g:if>
                  <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_all','${inrequest?.where}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" property="v:title"></g:else>
                  ${breadcrumbs.city?.('name'+context?.lang)}<g:if test="${breadcrumbs.city.is_index}"></a></g:if><g:else></span></g:else> &#8594; </span> ${breadcrumbs.citysight?.('name'+context?.lang)}
                </g:elseif><g:elseif test="${breadcrumbs?.shome}"><span typeof="v:Breadcrumb">
                  <g:if test="${breadcrumbs.city.is_index}"><a href="<g:createLink mapping='${breadcrumbs.city.domain?('hSearchTypeDomain'+context?.lang):('hSearchType'+context?.lang)}' params='${breadcrumbs.city.domain?[type_url:'all']:[where:inrequest?.where,country:breadcrumbs.country.urlname,type_url:'all']}' base='${breadcrumbs.base}'/>" rel="v:url" property="v:title"></g:if>
                  <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_all','${inrequest?.where}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" property="v:title"></g:else>
                  ${breadcrumbs.city?.('name'+context?.lang)}<g:if test="${breadcrumbs.city.is_index}"></a></g:if><g:else></span></g:else> &#8594; </span> ${breadcrumbs.shome?.('name'+context?.lang)}
                </g:elseif><g:else>
                  ${breadcrumbs.city?(breadcrumbs.city?.('name'+context?.lang)?:(breadcrumbs.region?breadcrumbs.region?.('name'+context?.lang):'')):(breadcrumbs.region?breadcrumbs.region?.('name'+context?.lang):'')}
                </g:else>
                </div>
              </g:if><g:else>
                <div class="breadcrumbs padd10" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">
                    <a href="${createLink(uri:'',base:context?.absolute_lang)}" rel="v:url" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span> ${message(code:'common.renthome').capitalize()}
                </div>
              </g:else>                
              </td>
            </tr>
            <tr>
              <td width="980" colspan="4">
                <div class="search-container">
                  <div id="search_body" class="col relative" style="min-height:1010px;height:100%">
                    <div class="views"><!--noindex-->                      
                      <span class="results_sort">
                        <label for="sort">${message(code:'sort_by')}</label>
                        <select id="sort" onchange="setFilterAndSubmit()">
                          <option value="0" <g:if test="${(inrequest?.sort?:0)==0}">selected="selected"</g:if>>${message(code:'common.sort.default')}</option>
                          <option value="1" <g:if test="${(inrequest?.sort?:0)==1}">selected="selected"</g:if>>${message(code:'common.sort.price_asc')}</option>
                          <option value="2" <g:if test="${(inrequest?.sort?:0)==2}">selected="selected"</g:if>>${message(code:'common.sort.price_desc')}</option> 
                          <option value="3" <g:if test="${(inrequest?.sort?:0)==3}">selected="selected"</g:if>>${message(code:'common.sort.create_date')}</option>                             
                        </select>
                      </span><!--/noindex-->
                      <span class="actions col">
                      <g:if test="${(inrequest?.view?:'')=='map'}">
                        <a class="icon list" rel="nofollow" onclick="$('view').value='list';setFilterFullAndSubmit()">${message(code:'list')}</a> 
                        <a class="icon photo" rel="nofollow" onclick="$('view').value='photo';setFilterFullAndSubmit()">${message(code:'photo')}</a> 
                        <a class="icon map" rel="map nofollow">${message(code:'map')}</a>
                      </g:if><g:else>
                        <a class="icon list" rel="nofollow" onclick="$('view').value='list';${inrequest.where!=''?'setSmallMap(1);':''}curBigMapSize='small';initialize(1);setFilterAndSubmit(1);">${message(code:'list')}</a> 
                        <a class="icon photo" rel="nofollow" onclick="$('view').value='photo';${inrequest.where!=''?'setSmallMap(1);':''}curBigMapSize='small';initialize(1);setFilterAndSubmit(1);">${message(code:'photo')}</a> 
                        <a class="icon map" rel="map nofollow" onclick="$('view').value='map';setFilterFullAndSubmit()">${message(code:'map')}</a>
                      </g:else>                  
                      </span>      
                    </div>
                    <div class="body">                                         
                      <div style="padding:0 15px 2px 12px"><!--noindex-->
                        <div id="wrongwhere">
                        <g:if test="${inrequest?.wrongwhere && !context?.lang}">
                          <p>В запросе <b>«${inrequest.wrongwhere}»</b> восстановлена раскладка клавиатуры.</p>
                        </g:if>
                        </div><!--/noindex-->
                        <h1 class="sheader">${h1}</h1>
                      <g:if test="${inrequest?.where!=''}">
                        <g:rawHtml>${itext}</g:rawHtml>
                      </g:if><g:elseif test="${inrequest?.where=='' && inrequest?.view!='map'}">
                        <div class="page-topic nofound">
                          <g:rawHtml>${itext}</g:rawHtml>
                        </div>                      
                        <div class="tags-topic">
                          <p><g:each in="${tagcloud}" var="tag">
                            <g:if test="${tag.value.is_index}"><a rel="tag" href="<g:createLink mapping='${tag.value.domain?('hSearchTypeDomain'+context?.lang):('hSearch'+context?.lang)}' params='${tag.value.domain?[type_url:'all']:[where:tag.key,country:Country.get(context?.lang?(City.findByName_en(tag.key)?.country_id):(City.findByName(tag.key)?.country_id))?.urlname]}' base='${tag.value.domain?'http://'+tag.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${message(code:'common.flats_per_night_in')} ${tag.value.name2}" style="font-size:${tag.value.count>tagcloudParams.maxFontCount?'20px':tag.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:if>
                            <g:else><span class="link" style="font-size:${tag.value.count>tagcloudParams.maxFontCount?'20px':tag.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765" onclick="transit(${context.is_dev?1:0},['${tag.key}','${Country.get(City.findByName(tag.key)?.country_id)?.urlname}'],'','','','${context?.lang}')"></g:else>
                            ${tag.key}<g:if test="${tag.value.is_index}"></a></g:if><g:else></span></g:else> 
                          </g:each></p>                        
                        </div>
                      </g:elseif>
                      <g:if test="${inrequest?.where!='' && !(inrequest?.hometype_id==1 && inrequest?.bedroom)}">
                        <div class="hotlinks" align="right">
                        <g:each in="${hometype}" var="item" status="i"><g:if test='${hometypeLinksData?.("$item.id")>0}'>
                          <span class="hometype">
                          <g:if test="${inrequest?.hometype_id!=item?.id}">
                            <g:if test="${breadcrumbs.city}"><g:link rel="tag" mapping="${breadcrumbs.city.domain?(item.urlname=='flats'?('mainpage'+context?.lang):('hSearchTypeDomain'+context?.lang)):('hSearchType'+context?.lang)}" params="${(breadcrumbs.city.domain?(item.urlname=='flats'?[:]:[type_url:item.urlname]):[where:inrequest?.where,country:breadcrumbs.country.urlname,type_url:item.urlname])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" base="${breadcrumbs.base}" title="${item['name3'+context?.lang]} ${message(code:'list.per_night_in')} ${breadcrumbs.city['name'+(context?.lang?:'2')]}">${item['name3'+context?.lang]}</g:link></g:if>
                            <g:else><g:link rel="tag" mapping="${'cSearchType'+context?.lang}" params="${[where:inrequest?.where,type_url:item?.urlname]+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" title="${item['name3'+context?.lang]} ${message(code:'list.per_night1')} ${inrequest?.where}">${item['name3'+context?.lang]}</g:link></g:else>
                          </g:if><g:else><b>${item['name3'+context?.lang]}</b></g:else><sup>${hometypeLinksData?.("$item.id")?:0}</sup>
                          </span>
                        </g:if></g:each>
                        </div>                      
                      </g:if><g:if test="${inrequest?.citysight_id && citysightNext}">
                        <div class="hotlinks" align="right">
                        <g:each in="${citysightNext}" var="item" status="i"><g:if test="${i<searchFilterCitysightMax}">
                          <span class="citysight">
                          <g:if test="${!((inrequest?.citysight_id?:[]).contains(item?.id))}">
                            <g:if test="${breadcrumbs.city}"><g:link mapping="${breadcrumbs.city.domain?('hSearchCitysightDomain'+context?.lang):('hSearchCitysight'+context?.lang)}" params="${(breadcrumbs.city.domain?[citysight_url:Citysight.get(item.id)?.urlname]:[where:inrequest?.where,country:breadcrumbs.country.urlname,citysight_url:Citysight.get(item.id)?.urlname])+(inrequest?.hometype_id?[hometype_id:inrequest?.hometype_id]:[:])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" base="${breadcrumbs.base}">${Citysight.get(item?.id?:0)?.('name'+context?.lang)?:''}</g:link></g:if>
                            <g:else><g:link controller="home" action="list" params="[where:inrequest?.where,citysight_url:Citysight.get(item.id)?.urlname]+(inrequest?.hometype_id?[hometype_id:inrequest?.hometype_id]:[:])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}">${Citysight.get(item?.id?:0)?.('name'+context?.lang)?:''}</g:link></g:else>
                          </g:if><g:else><b>${Citysight.get(item?.id?:0)?.('name'+context?.lang)?:''}</b></g:else><sup>${item?.total}</sup>
                          </span>
                        </g:if></g:each>                        
                          <a class="show_more_link" rel="nofollow" onclick="viewCell('filters_lightbox_nav',3,'lightbox_filters')">
                            ${message(code:'common.more')}&hellip;
                          </a>                        
                        </div>                      
                      </g:if><g:elseif test="${metroNext && !breadcrumbs?.bedroom && inrequest?.metro_id}">
                        <div class="hotlinks" align="right">
                        <g:each in="${metroNext}" var="item" status="i"><g:if test="${i<searchFilterMetroMax}">
                          <span class="metro">
                          <g:if test="${!((inrequest?.metro_id?:[]).contains(item?.id))}">
                            <g:if test="${breadcrumbs.city}"><g:link mapping="${breadcrumbs.city.domain?('hSearchMetroDomain'+context?.lang):('hSearchMetro'+context?.lang)}" params="${(breadcrumbs.city.domain?[metro_url:Metro.get(item.id)?.urlname]:[where:inrequest?.where,country:breadcrumbs.country.urlname,metro_url:Metro.get(item.id)?.urlname])+(inrequest?.hometype_id?[hometype_id:inrequest?.hometype_id]:[:])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" base="${breadcrumbs.base}">${Metro.get(item?.id?:0)?.('name'+context?.lang)?:''}</g:link></g:if>
                            <g:else><g:link controller="home" action="list" params="[where:inrequest?.where,metro_url:Metro.get(item.id)?.urlname]+(inrequest?.hometype_id?[hometype_id:inrequest?.hometype_id]:[:])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}">${Metro.get(item?.id?:0)?.('name'+context?.lang)?:''}</g:link></g:else>
                          </g:if><g:else><b>${Metro.get(item?.id?:0)?.('name'+context?.lang)?:''}</b></g:else><sup>${item?.total}</sup>
                          </span>
                        </g:if></g:each>                        
                          <a class="show_more_link" rel="nofollow" onclick="viewCell('filters_lightbox_nav',${districtMax?4:3},'lightbox_filters')">
                            ${message(code:'common.more')}&hellip;
                          </a>                        
                        </div>
                      </g:elseif><g:if test="${inrequest?.where!='' && inrequest?.hometype_id==1 && !inrequest?.citysight_id && !inrequest?.metro_id}">
                        <div class="hotlinks" align="right">
                        <g:each in="${homeroom}" var="item" status="i"><g:if test="${homeroomMax['id'+(i+1)]>0}">
                          <span class="room">
                          <g:if test="${inrequest?.bedroom!=item?.id&&homeroomMax['id'+(i+1)]>0}">
                            <g:if test="${breadcrumbs.city}"><g:link rel="tag" mapping="${breadcrumbs.city.domain?('hSearchRoomDomain'+context?.lang):('hSearchRoom'+context?.lang)}" params="${(breadcrumbs.city.domain?[type_url:'flats',bedroom:item.id]:[where:inrequest?.where,country:breadcrumbs.country.urlname,type_url:'flats',bedroom:item.id])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" base="${breadcrumbs.base}" title="${item['name'+(context?.lang?('3'+context?.lang):'2')]} ${message(code:'list.filtr.header.flat_per_night_in')} ${breadcrumbs.city['name'+(context?.lang?'_en':'2')]}">${item['name3'+context?.lang]}</g:link></g:if>
                            <g:else><g:link rel="tag" mapping="${'cSearchRoom'+context?.lang}" params="${[where:inrequest?.where,bedroom:item?.id,type_url:'flats']+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" title="${context?.lang?item['name3'+context?.lang]:item?.name2} ${message(code:'list.filtr.header.flat_per_night_in')} ${inrequest?.where}">${item['name3'+context?.lang]}</g:link></g:else>
                          </g:if><g:else><b>${item['name3'+context?.lang]}</b></g:else><sup>${homeroomMax['id'+(i+1)]?:0}</sup>
                          </span>
                        </g:if></g:each>
                        </div>                      
                      </g:if>
                      <g:if test="${lsShome}">
                        <div class="hotlinks" align="right">                      
                        <g:each in="${lsShome}">
                          <g:if test="${it.homecount>=homecount}">
                            <span class="apartment">
                            <g:if test="${Shometype.get(it.type_id?:0)?.fieldname=='is_vip' && is_vipLinksData}">                            
                              <g:if test="${!inrequest?.(Shometype.get(it.type_id?:0)?.fieldname)}">
                                <g:link mapping="${Shometype.get(it.type_id?:0)?.fieldname+context?.lang}" params="${((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" base="${breadcrumbs.base}">${it?.('linkname'+context?.lang)}</g:link>                                                      
                              </g:if><g:else><b>${it?.('linkname'+context?.lang)}</b></g:else>
                              <sup>${is_vipLinksData}</sup>
                            </g:if>
                            <g:elseif test="${Shometype.get(it.type_id?:0)?.fieldname=='is_renthour' && is_renthourLinksData}">
                              <g:if test="${!inrequest?.(Shometype.get(it.type_id?:0)?.fieldname)}">
                                <g:link mapping="${Shometype.get(it.type_id?:0)?.fieldname+context?.lang}" params="${((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" base="${breadcrumbs.base}">${it?.('linkname'+context?.lang)}</g:link>                                                      
                              </g:if><g:else><b>${it?.('linkname'+context?.lang)}</b></g:else>
                              <sup>${is_renthourLinksData}</sup>         
                            </g:elseif>
                            <g:elseif test="${Shometype.get(it.type_id?:0)?.fieldname=='is_fiesta' && is_fiestaLinksData}">
                              <g:if test="${!inrequest?.(Shometype.get(it.type_id?:0)?.fieldname)}">
                                <g:link mapping="${Shometype.get(it.type_id?:0)?.fieldname+context?.lang}" params="${((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" base="${breadcrumbs.base}">${it?.('linkname'+context?.lang)}</g:link>                                                      
                              </g:if><g:else><b>${it?.('linkname'+context?.lang)}</b></g:else>
                              <sup>${is_fiestaLinksData}</sup>         
                            </g:elseif>                             
                          </g:if>                          
                          </span>                                                  
                        </g:each>
                        </div>
                      </g:if>                      
                      </div>
                    </div>
                  <g:if test="${inrequest?.where=='' && inrequest?.view!='map'}">
                    <!--noindex--><div class="slideshow" style="padding:0 0 28px 0"></div><!--/noindex-->
                  </g:if><g:else>
                    <div id="results" class="body">
                      <g:render template="/listing" />
                    </div>
                    <g:if test="${inrequest?.where!='' && records.size()==0 && inrequest?.view!='map'}">
                    <!--noindex--><div class="slideshow" style="padding:0 0 28px 0"></div><!--/noindex-->
                    </g:if>
                  </g:else>
                    <div id="map_view" class="body col relative">
                      <div id="map_large_canvas"></div>
                      <div id="search_filters_toggle" onclick="setMap()"></div>
                    </div>                                     
                  </div><!--noindex-->                  
                  <g:form name="waitingform" controller="index" action="waiting" base="${context?.absolute_lang}">
                    <input type="hidden" name="where" value="${inrequest?.where?:''}" />
                    <input type="hidden" name="country_id" value="${inrequest?.country_id?:0}" />
                    <input type="hidden" name="region_id" value="${inrequest?.region_id?:0}" />
                    <input type="hidden" name="hometype_id" value="${inrequest?.hometype_id?:0}" />
                    <input type="hidden" name="homeperson_id" value="${inrequest?.homeperson_id?:0}" />
                    <input type="hidden" name="valuta_id" value="${inrequest?.valuta_id?:0}" />
                    <input type="hidden" name="date_start" value="${string?.date_start?:''}" />
                    <input type="hidden" name="date_end" value="${string?.date_end?:''}" />
                  </g:form>                  
                  <g:formRemote name="filter_form" url="${[action:'search_table']}" method="get" update="[success:'results']" style="margin-top:-1px;">                                    
                    <input type="hidden" id="where_filter" name="where" />
                    <input type="hidden" id="homeperson_id_filter" name="homeperson_id" />
                    <input type="hidden" id="hometype_id_filter" name="hometype_id" />
                    <input type="hidden" id="price_min" name="price_min" value="0" />
                    <input type="hidden" id="price_max" name="price_max" value="0" />
                    <input type="hidden" id="sort_filter" name="sort" value="0" />                    
                    <input type="hidden" id="params_filter" name="param" value="" />
                    <input type="hidden" id="alike" name="alike" value="${alike?:0}" />      
                    <input type="hidden" id="date_start_day_filter" name="date_start_day" value="" />
                    <input type="hidden" id="date_start_month_filter" name="date_start_month" value="" />	  
                    <input type="hidden" id="date_start_year_filter" name="date_start_year" value="" />
                    <input type="hidden" id="date_end_day_filter" name="date_end_day" value="" />
                    <input type="hidden" id="date_end_month_filter" name="date_end_month" value="" />	  
                    <input type="hidden" id="date_end_year_filter" name="date_end_year" value="" />
                    <input type="hidden" id="view_filter" name="view" value="list" />
                    <input type="hidden" id="is_main_filter" name="is_main_filter" value="0" />
                    <input type="hidden" id="x_filter" name="x" value="0" />
                    <input type="hidden" id="y_filter" name="y" value="0" />
                    <input type="hidden" id="zoom_filter" name="zoom" value="0" />                  
                  <g:each in="${hidInrequest}" var="item" status="i">
                    <input id="filter_form_option_${i}" type="hidden" name="${item.key}" value="${item.value}" />
                  </g:each>                  
                  <g:each in="${hidInrequest_district_id}" var="item" status="i">
                    <input id="filter_form_district_id_${i}" type="hidden" name="district_id" value="${item}" />
                  </g:each>
                  <g:each in="${hidInrequest_metro_id}" var="item" status="i">
                    <input id="filter_form_metro_id_${i}" type="hidden" name="metro_id" value="${item}" />
                  </g:each>
                  <g:each in="${hidInrequest_citysight_id}" var="item" status="i">
                    <input id="filter_form_citycight_id_${i}" type="hidden" name="citysight_id" value="${item}" />
                  </g:each>
                  <g:each in="${hidInrequest_apart}" var="item" status="i">
                    <input id="filter_form_apart_${i}" type="hidden" name="${item?.key}" value="${item?.value}" />
                  </g:each>                                    
                    <div style="display:none">
                      <input id="filter_form_submit" type="submit" />			
                    </div>                                    
                    <div id="search_filters_wrapper" class="col">                    
                      <div id="search_filters"> 
                        <ul class="collapsable_filters">			    
                          <li class="search_filter" id="map_container">
                            <a class="filter_toggle" rel="nofollow" onclick="toggleFilter('map_container');"></a>
                            <a class="filter_header" rel="nofollow" onclick="toggleFilter('map_container');">${message(code:'list.filtr.watch_on_map')}</a>
                            <ul class="search_filter_content" style="padding:5px 10px 0 20px">              
                              <li id="newmapsearch_checkbox" class="clearfix" style="padding:10px 0px;display:${(inrequest?.view!='map')?'none':'block'}">
                                <label for="redo_search_in_map">
                                  <input type="checkbox" id="redo_search_in_map" name="redo_search_in_map" onclick="sinchronizeMap()" <g:if test="${inrequest?.search_in_map?:0}">checked</g:if> /> ${message(code:'list.filtr.search_on_map_fragment')}
                                </label>
                              </li>					
                              <li class="clearfix">
                                <div id="map_wrapper" class="shadow">
                                  <div id="search_map" class="relative">
                                    <div id="map_small_canvas"></div>
                                  </div>						
                                </div>                                
                              </li>
                            <g:if test="${(inrequest?.view?:'')!='map'}">
                              <li class="clearfix" style="padding:5px 0">
                                <a rel="nofollow" onclick="$('view').value='map';setFilterFullAndSubmit()">${message(code:'list.filtr.watch_on_big_map')}</a>
                              </li>
                            </g:if>
                            </ul>
                          </li>
                        <g:if test="${!inrequest.hometype_id && !inrequest?.citysight_id && !inrequest?.metro_id && !lsShome}">                        
                          <li class="search_filter" id="hometype_container">
                            <a class="filter_toggle" rel="nofollow" onclick="toggleFilter('hometype_container');"></a>
                            <a class="filter_header" rel="nofollow" onclick="toggleFilter('hometype_container');">${message(code:'common.home_type')}</a>
                            <ul class="search_filter_content">
                            <g:each in="${hometypeFilter}" var="item" status="i"><g:if test="${i<searchFilterMax}">
                              <li class="clearfix">
                                <span class="facet_count">${hometypeMax[i].total}</span>
                                <input type="checkbox" id="home_type_${i}" name="hometype_id_filter" value="${item?.id}" <g:if test="${(inrequest?.hometype_id_filter?:[]).contains(item?.id.toLong())}">checked="checked"</g:if> onclick="setPostFilter(this.id);setFilterAndSubmit()" />
                                <label for="home_type_${i}">${item?.('name'+context?.lang)}</label>
                              </li>                              
                            </g:if></g:each>
                              <li class="clearfix">
                                <a class="show_more_link" rel="nofollow" onclick="viewCell('filters_lightbox_nav',0,'lightbox_filters')">${message(code:'list.filtr.show_more')}</a>
                              </li>              
                            </ul>
                          </li>
                        </g:if>
                        <g:elseif test="${inrequest.hometype_id==1 && !inrequest.bedroom}">
                          <li class="search_filter" id="rooms_type_container">
                            <a class="filter_toggle" rel="nofollow" onclick="toggleFilter('rooms_type_container');"></a>
                            <a class="filter_header" rel="nofollow" onclick="toggleFilter('rooms_type_container');">${message(code:'common.number_of_rooms')}</a>
                            <ul class="search_filter_content">
                            <g:each in="${homeroom}" var="item" status="i"><g:if test="${i<searchFilterMax}">
                              <li class="clearfix">
                                <span class="facet_count">${homeroomMax['id'+(i+1)]?:0}</span>
                                <input type="checkbox" id="room_type_${i}" name="room_id_filter" value="${item?.id}" <g:if test="${(inrequest?.room_id_filter?:[]).contains(item?.id.toLong())}">checked="checked"</g:if> onclick="setPostFilter(this.id);setFilterAndSubmit()" />
                                <label for="room_type_${i}">${item['name3'+context?.lang]}</label>
                              </li>                              
                            </g:if></g:each>
                              <li class="clearfix">
                                <a class="show_more_link" rel="nofollow" onclick="viewCell('filters_lightbox_nav',0,'lightbox_filters')">${message(code:'list.filtr.show_more')}</a>
                              </li>              
                            </ul>
                          </li>
                        </g:elseif>
                        <g:if test="${!inrequest?.citysight_id && (districtMax || (metroMax && !breadcrumbs.metro))}">
                          <li class="search_filter" id="neighborhood_container">
                            <a class="filter_toggle" rel="nofollow" onclick="toggleFilter('neighborhood_container');"></a>
                            <a class="filter_header" rel="nofollow" onclick="toggleFilter('neighborhood_container');">${message(code:'list.filtr.district_and_metro')}</a>
                            <ul class="search_filter_content">
                            <g:if test="${districtMax}">
                              <li class="clearfix"><b style="color:#333">${message(code:'list.filtr.districts')}</b></li>
                              <li class="clearfix">
                                <ul>       
                                <g:each in="${districtMax}" var="item" status="i"><g:if test="${(item?.district_id?:0) && (i<searchFilterDistrictMax)}">
                                  <li class="clearfix">
                                    <span class="facet_count">${item.total}</span>
                                    <input type="checkbox" id="district_id_${item?.district_id}" name="district_id" value="${item?.district_id}" onclick="setPostFilter(this.id);setFilterAndSubmit()" <g:if test="${(inrequest?.district_id?:[]).contains(item?.district_id)}">checked</g:if> />
                                    <label for="district_id_${item?.district_id}">${District.get(item?.district_id?:0)?.('name'+context?.lang)?:''}</label>
                                  </li>
                                </g:if></g:each>                          
                                </ul>
                              </li>
                              <li class="clearfix ${(metroMax && !breadcrumbs.metro)?'dott':''}">
                                <a class="show_more_link" rel="nofollow" onclick="viewCell('filters_lightbox_nav',3,'lightbox_filters')">${message(code:'list.filtr.show_more')}</a>
                              </li>              
                            </g:if>                          
                            <g:if test="${metroMax && !breadcrumbs.metro}">
                              <li class="clearfix"><b style="color:#333">${message(code:'common.metro').capitalize()}</b></li>
                              <li class="clearfix">
                                <ul>                         
                                <g:each in="${metroMax}" var="item" status="i"><g:if test="${(item?.id?:0) && (i<searchFilterMetroMax)}">
                                  <li class="clearfix">
                                    <span class="facet_count">${item.total}</span>
                                    <g:if test="${(inrequest?.metro_id?:[]).size()==1 && (inrequest?.metro_id?:[]).contains(item?.id)}">
                                      <input type="checkbox" id="metro_id_${item?.id}" name="metro_id" value="${item?.id}" onclick="setPostFilter(this.id);setFilterFullAndSubmit();" <g:if test="${(inrequest?.metro_id?:[]).contains(item?.id)}">checked</g:if> />
                                    </g:if><g:else>
                                      <input type="checkbox" id="metro_id_${item?.id}" name="metro_id" value="${item?.id}" onclick="setPostFilter(this.id);setFilterAndSubmit()" <g:if test="${(inrequest?.metro_id?:[]).contains(item?.id)}">checked</g:if> />
                                    </g:else>
                                    <label for="metro_id_${item?.id}">${Metro.get(item?.id?:0)?.('name'+context?.lang)?:''}</label>
                                  </li>
                                </g:if></g:each>  
                                </ul>
                              </li>
                              <li class="clearfix">
                                <a class="show_more_link" rel="nofollow" onclick="viewCell('filters_lightbox_nav',${districtMax?4:3},'lightbox_filters')">${message(code:'list.filtr.show_more')}</a>
                              </li>              
                            </g:if>
                            </ul>
                          </li>
                        </g:if>                      
                          <li class="search_filter" id="price_container">
                            <a class="filter_toggle" rel="nofollow" onclick="toggleFilter('price_container');"></a>
                            <a class="filter_header" rel="nofollow" onclick="toggleFilter('price_container');">${message(code:'list.filtr.price')}</a>
                            <div class="search_filter_content">
                              <ul id="slider_values">
                                <li id="slider_user_min"></li>
                                <li id="slider_user_max"></li>
                              </ul>
                              <div id="slider-range" class="ui-slider ui-slider-horizontal ui-widget ui-widget-content ui-corner-all">
                                <div id="slider_span" class="ui-slider-range ui-widget-header ui-corner-all" style="left: 0%; width: 100%;"></div>                              
                                <a class="ui-slider-handle ui-state-default ui-corner-all" href="javascript:void(0);" style="left:0%;"></a>
                                <a class="ui-slider-handle ui-state-default ui-corner-all" href="javascript:void(0);" style="left:100%;"></a>
                              </div>                                    
                            </div>
                          </li>
                          <li class="search_filter" id="amenities_container">
                            <a class="filter_toggle" rel="nofollow" onclick="toggleFilter('amenities_container');"></a>
                            <a class="filter_header" rel="nofollow" onclick="toggleFilter('amenities_container');">${message(code:'list.filtr.amenities')}</a>
                            <ul class="search_filter_content">
                            <g:if test="${option_filter}"><g:each in="${option_filter.entrySet()}" var="item" status="i">
                              <g:if test="${i<searchFilterMax}"><g:each in="${homeoption}" var="item1" status="i1"><g:if test="${item.key==item1.fieldname}">                
                              <li class="clearfix">
                                <span class="facet_count">${item.value}</span>
                                <input type="checkbox" id="amenity_${i1}" name="${item1.fieldname}" value="1" <g:if test="${inrequest[item1.fieldname]}">checked</g:if> onclick="setPostFilter(this.id);setFilterAndSubmit()" />
                                <label for="amenity_${i1}">${item1?.('name'+context?.lang)}</label>
                              </li>	  
                              </g:if></g:each></g:if>
                            </g:each></g:if>
                              <li class="clearfix">
                                <a class="show_more_link" rel="nofollow" onclick="viewCell('filters_lightbox_nav',2,'lightbox_filters')">${message(code:'list.filtr.show_more')}</a>
                              </li>              
                            </ul>
                          </li>
                        <g:if test="${(option?.nref?:0)>0||(option?.rating?:0)>0}">				
                          <li class="search_filter" id="additional_container">
                            <a class="filter_toggle" rel="nofollow" onclick="toggleFilter('additional_container');"></a>
                            <a class="filter_header" rel="nofollow" onclick="toggleFilter('additional_container');">${message(code:'list.filtr.add_terms')}</a>
                            <ul class="search_filter_content">
                            <g:if test="${(option?.nref?:0)>0}">
                              <li class="clearfix">
                                <span class="facet_count">${option.nref?:0}</span>                      
                                <input type="checkbox" id="nref" name="nref" value="1" <g:if test="${inrequest?.nref}">checked</g:if> onclick="setPostFilter(this.id);setFilterAndSubmit()" />
                                <label for="nref">${message(code:'list.reviews')}</label>
                              </li>
                            </g:if><g:if test="${(option?.rating?:0)>0}">
                              <li class="clearfix">
                                <span class="facet_count">${option.rating?:0}</span>                      
                                <input type="checkbox" id="rating" name="rating" value="1" <g:if test="${inrequest?.rating}">checked</g:if> onclick="setPostFilter(this.id);setFilterAndSubmit()" />
                                <label for="rating">${message(code:'list.rating')}</label>
                              </li>
                            </g:if><g:if test="${(option?.hotdiscount?:0)>0}">
                              <li class="clearfix">
                                <span class="facet_count">${option.hotdiscount?:0}</span>                      
                                <input type="checkbox" id="hotdiscount" name="hotdiscount" value="1" <g:if test="${inrequest?.hotdiscount}">checked</g:if> onclick="setPostFilter(this.id);setFilterAndSubmit()" />
                                <label for="hotdiscount">${message(code:'list.hotdiscount')}</label>
                              </li>
                            </g:if><g:if test="${(option?.longdiscount?:0)>0}">
                              <li class="clearfix">
                                <span class="facet_count">${option.longdiscount?:0}</span>                      
                                <input type="checkbox" id="longdiscount" name="longdiscount" value="1" <g:if test="${inrequest?.longdiscount}">checked</g:if> onclick="setPostFilter(this.id);setFilterAndSubmit()" />
                                <label for="longdiscount">${message(code:'list.longdiscount')}</label>
                              </li>
                            </g:if>
                              <li class="clearfix">
                                <a class="show_more_link" rel="nofollow" onclick="viewCell('filters_lightbox_nav',1,'lightbox_filters')">${message(code:'list.filtr.show_more')}</a>
                              </li>              
                            </ul>
                          </li>
                        </g:if>                          
                          <li class="search_filter" id="keywords_container">
                            <a class="filter_toggle" rel="nofollow" onclick="toggleFilter('keywords_container');"></a>                  
                            <a class="filter_header" rel="nofollow" onclick="toggleFilter('keywords_container');">${message(code:'list.filtr.keywords')}</a>                  
                            <ul class="search_filter_content">
                              <li class="clearfix">
                                <input id="keywords" type="text" name="keywords" value="${inrequest?.keywords?:''}" placeholder="${message(code:'list.filtr.keywords_default')}" />
                                <input type="button" id="submit_keyword" onclick="setFilterAndSubmit()" />					  
                              </li>
                            </ul>
                          </li>
                          <li>
                            <div align="right" style="padding: 10px">				    					
                              <input type="button" class="button-glossy grey mini" value="${message(code:'button.reset')}" onclick="resetButton()" />
                            </div>
                          </li>
                        </ul>
                      </div>                        
                    </div>
                  </g:formRemote><!--/noindex-->
                </div>
              </td>
            </tr><!--noindex-->
            <tr>
              <td colspan="4">
                <div id="filters_lightbox" class="new-modal" style="display:none">
                  <div class="segment clearfix">
                    <ul id="filters_lightbox_nav">                    
                      <li id="lightbox_nav_room_type" class="filters_lightbox_nav_element">
                        <a rel="nofollow" onclick="viewCell('filters_lightbox_nav',0,'lightbox_filters');">${message(code:'list.full_filtr.housing')}</a>
                      </li>
                      <li id="lightbox_nav_additional" class="filters_lightbox_nav_element">
                        <a rel="nofollow" onclick="viewCell('filters_lightbox_nav',1,'lightbox_filters');">${message(code:'list.full_filtr.add_terms')}</a>
                      </li>                                          
                      <li id="lightbox_nav_amenities" class="filters_lightbox_nav_element">
                        <a rel="nofollow" onclick="viewCell('filters_lightbox_nav',2,'lightbox_filters');">${message(code:'list.filtr.amenities').capitalize()}</a>
                      </li>
                    <g:if test="${citysightMax  && inrequest?.citysight_id}">                      
                      <li id="lightbox_nav_metro" class="filters_lightbox_nav_element">
                        <a rel="nofollow" onclick="viewCell('filters_lightbox_nav',3,'lightbox_filters');">${message(code:'list.full_filtr.citysights')}</a>
                      </li>                      
                    </g:if><g:elseif test="${districtMax || metroMax}">
                      <g:if test="${districtMax}">
                      <li id="lightbox_nav_district" class="filters_lightbox_nav_element">
                        <a rel="nofollow" onclick="viewCell('filters_lightbox_nav',3,'lightbox_filters');">${message(code:'list.full_filtr.district')}</a>
                      </li>
                      </g:if><g:if test="${metroMax}">
                      <li id="lightbox_nav_metro" class="filters_lightbox_nav_element">
                        <a rel="nofollow" onclick="viewCell('filters_lightbox_nav',${districtMax?4:3},'lightbox_filters');">${message(code:'common.metro').capitalize()}</a>
                      </li>
                      </g:if>
                    </g:elseif>
                    </ul>
                  </div>
                  <g:form name="full_filter_form" mapping="${context?.lang?'en':''}" controller="home" action="search" method="get">
                    <input type="hidden" id="full_where" name="where" />                    
                    <input type="hidden" id="full_view" name="view" value="list" />                    
                    <input type="hidden" id="full_hometype_id" name="hometype_id" />
                    <input type="hidden" id="full_homeperson_id" name="homeperson_id" />
                    <input type="hidden" id="full_price_min" name="price_min" value="0" />
                    <input type="hidden" id="full_price_max" name="price_max" value="0" />
                    <input type="hidden" id="full_sort" name="sort" value="0" />
                    <input type="hidden" id="full_offset" name="offset" value="0" />                    
                    <input type="hidden" id="full_params" name="param" value="" />                    
                    <input type="hidden" id="full_x" name="x" value="0" />
                    <input type="hidden" id="full_y" name="y" value="0" />
                    <input type="hidden" id="full_zoom" name="zoom" value="0" />								
                    <input type="hidden" id="full_keywords" name="keywords" value="" />
                    <input type="hidden" id="full_date_start_day" name="date_start_day" value="" />
                    <input type="hidden" id="full_date_start_month" name="date_start_month" value="" />
                    <input type="hidden" id="full_date_start_year" name="date_start_year" value="" />
                    <input type="hidden" id="full_date_end_day" name="date_end_day" value="" />
                    <input type="hidden" id="full_date_end_month" name="date_end_month" value="" />
                    <input type="hidden" id="full_date_end_year" name="date_end_year" value="" />
                    <input type="hidden" id="full_search_in_map" name="search_in_map" value="0" />
                    <input type="hidden" id="offset_list" value="${inrequest?.offset?:0}" />
                    <g:if test="${!inrequest.hometype_id}">
                      <g:each in="${hometypeFilter}" var="item" status="i">
                        <g:if test="${i<searchFilterMax}">                        
                          <input type="checkbox" id="full_home_type_${i}" name="hometype_id_filter" value="${item?.id}" <g:if test="${(inrequest?.hometype_id_filter?:[]).contains(item?.id.toLong())}">checked="checked"</g:if> style="display:none" />
                        </g:if>
                      </g:each>
                    </g:if>
                    <ul id="lightbox_filters" class="segment nopadding">
                      <li id="lightbox_container_room_type" class="lightbox_filter_container" style="display:none">
                        <div class="lightbox_filters_left_column">
                        <g:if test="${!inrequest.hometype_id}">
                          <h3>${message(code:'common.home_type').capitalize()}</h3>
                          <ul id="lightbox_filter_content_size" class="search_filter_content">
                            <li class="clearfix">
                              <label for="hometype_id_fullfilter">${message(code:'common.home_type')}</label>
                              <select id="hometype_id_fullfilter">
                                <option value="all"></option>
                              <g:each in="${hometype}" var="item" status="i">
                                <option value="${item?.urlname}">${item?.('name'+context?.lang)}</option>
                              </g:each>
                              </select>
                            </li>
                          <g:each in="${homeoption_apartament}" var="item" status="i"> 
                            <li class="clearfix">
                            <g:if test="${option[item.fieldname]>0}">
                              <span class="facet_count">${option[item.fieldname]}</span>
                            </g:if>
                              <input id="full_apartament_${i}" type="checkbox" value="1" name="${item.fieldname}" <g:if test="${inrequest[item.fieldname]}">checked="checked"</g:if> />
                              <label for="full_apartament_${i}">${item?.('name'+context?.lang)}</label>
                            </li>
                          </g:each>
                          </ul>
                        </g:if>
                          <h3>${message(code:'list.full_filtr.size')}</h3>
                        <g:if test="${inrequest.hometype_id==1 && !inrequest.bedroom}">
                          <label>${message(code:'common.number_of_rooms').capitalize()}</label>
                          <ul class="search_filter_content">                            
                          <g:each in="${homeroom}" var="item" status="i"> 
                            <li class="clearfix">
                            <g:if test="${(homeroomMax['id'+(i+1)]?:0)>0}">                              
                              <span class="facet_count">${homeroomMax['id'+(i+1)]?:0}</span>
                            </g:if>
                              <input id="full_room_type_${i}" type="checkbox" value="${item?.id}" name="room_id_filter" <g:if test="${(inrequest?.room_id_filter?:[]).contains(item?.id.toLong())}">checked="checked"</g:if> onclick="setFilterFromPostFilter(this.id);" />
                              <label for="full_room_type_${i}">${item['name3'+context?.lang]}</label>
                            </li>
                          </g:each>              
                          </ul>
                        </g:if>                          
                          <ul id="lightbox_filter_content_size" class="search_filter_content">
                          <g:if test="${!(inrequest.hometype_id==1 && !inrequest.bedroom)}">
                            <li class="clearfix">
                              <label for="min_bedrooms">${message(code:'common.number_of_rooms').capitalize()}</label>
                              <select id="min_bedrooms" name="bedroom" class="dropdown">
                                <option value=""></option>
                              <g:each in="${homeroom}" var="item" status="i">                              
                                <option value="${item?.id}" <g:if test="${(inrequest?.bedroom?:0)==item?.id}">selected="selected"</g:if>>${item?.kol}</option>
                              </g:each>
                              </select>
                            </li>
                          </g:if>
                            <li class="clearfix">
                              <label for="min_bathrooms">${message(code:'list.full_filtr.minimum_bathrooms')}</label>
                              <select id="min_bathrooms" name="bathroom" class="dropdown">
                                <option value=""></option>
                              <g:each in="${homebath}" var="item" status="i">                              
                                <option value="${item?.id}" <g:if test="${(inrequest?.bathroom?:0)==item?.id}">selected="selected"</g:if>>${item?.kol}</option>
                              </g:each>                  
                              </select>
                            </li>
                            <li class="clearfix">
                              <label for="min_beds">${message(code:'list.full_filtr.minimum_beds')}</label>
                              <select id="min_beds" name="bed" class="dropdown">
                                <option value=""></option>
                              <g:each in="${homebed}" var="item" status="i">                              
                                <option value="${item?.id}" <g:if test="${(inrequest?.bed?:0)==item?.id}">selected="selected"</g:if>>${item?.name}</option>
                              </g:each>                  
                              </select>
                            </li>
                          </ul>
                        </div>
                        <div class="lightbox_filters_right_column">&nbsp;</div>
                      </li>
                      <li id="lightbox_container_additional" class="lightbox_filter_container" style="display:none">
                        <div class="lightbox_filters_left_column">
                          <ul class="search_filter_content">
                            <li class="clearfix">
                            <g:if test="${option?.nref>0}">
                              <span class="facet_count">${option.nref?:0}</span>                      
                            </g:if>  
                              <input id="full_nref" type="checkbox" name="nref" value="1" <g:if test="${inrequest?.nref}">checked="checked"</g:if> onclick="setFilterFromPostFilter(this.id);" />
                              <label for="full_nref">${message(code:'list.reviews')}</label>
                            </li>
                            <li class="clearfix">
                            <g:if test="${option?.rating>0}">
                              <span class="facet_count">${option.rating?:0}</span>                      
                            </g:if>
                              <input id="full_rating" type="checkbox" name="rating" value="1" <g:if test="${inrequest?.rating}">checked="checked"</g:if> onclick="setFilterFromPostFilter(this.id);" />
                              <label for="full_rating">${message(code:'list.rating')}</label>
                            </li>
                          </ul>
                          <h3>${message(code:'common.discounts').capitalize()}</h3>
                          <ul id="lightbox_filter_content_property_type" class="search_filter_content">                          
                            <li class="clearfix">
                            <g:if test="${option?.hotdiscount>0}">
                              <span class="facet_count">${option.hotdiscount?:0}</span>
                            </g:if>
                              <input id="full_hotdiscount" type="checkbox" name="hotdiscount" value="1" <g:if test="${inrequest?.hotdiscount}">checked="checked"</g:if> onclick="setFilterFromPostFilter(this.id);" />
                              <label for="full_hotdiscount">${message(code:'list.hotdiscount')}</label>
                            </li>
                            <li class="clearfix">
                            <g:if test="${option?.longdiscount>0}">
                              <span class="facet_count">${option.longdiscount?:0}</span>
                            </g:if>
                              <input id="full_longdiscount" type="checkbox" name="longdiscount" value="1" <g:if test="${inrequest?.longdiscount}">checked="checked"</g:if> onclick="setFilterFromPostFilter(this.id);" />
                              <label for="full_longdiscount">${message(code:'list.longdiscount')}</label>
                            </li>
                          </ul>
                        </div>                    
                      </li>                    
                      <li id="lightbox_container_amenities" class="lightbox_filter_container" style="display:none;">
                        <ul class="search_filter_content">
                        <g:each in="${homeoption}" var="item" status="i"> 
                          <li class="clearfix">
                          <g:if test="${option[item.fieldname]>0}">
                            <span class="facet_count">${option[item.fieldname]}</span>
                          </g:if>
                            <input id="full_amenity_${i}" type="checkbox" value="1" name="${item.fieldname}" <g:if test="${inrequest[item.fieldname]}">checked="checked"</g:if> onclick="setFilterFromPostFilter(this.id);" />
                            <label for="full_amenity_${i}">${item?.('name'+context?.lang)}</label>
                          </li>
                        </g:each>              
                        </ul>
                      </li>
                    <g:if test="${citysightMax && inrequest?.citysight_id}">
                      <li id="lightbox_container_citysight" class="lightbox_filter_container" style="display:none;">                      
                        <ul class="search_filter_content">
                        <g:each in="${citysight}" var="item" status="i">  
                          <g:each in="${citysightMax}" var="item1"><g:if test="${item?.id==item1.id}">
                            <li class="clearfix">                            
                              <span class="facet_count">${item1?.total?:0}</span>                              
                                <input id="full_citysight_id_${item.id}" name="citysight_id" type="checkbox" value="${item?.id}" <g:if test="${(inrequest?.citysight_id?:[]).contains(item1?.id?:0)}">checked="checked"</g:if> onclick='setFilterFromPostFilter(this.id);toggleCitysightMarker(${item.x},${item.y},"${item.title}",${item.id});' />                              
                              <label for="full_citysight_id_${item.id}">${item?.('name'+context?.lang)}</label>
                            </li>
                          </g:if></g:each>
                        </g:each>                                
                        </ul>                                                
                      </li>
                    </g:if><g:else>
                      <g:if test="${districtMax}">
                      <li id="lightbox_container_district" class="lightbox_filter_container" style="display:none;">                      
                        <ul class="search_filter_content">
                        <g:each in="${district}" var="item" status="i">  
                          <g:each in="${districtMax}" var="item1"><g:if test="${item?.id==item1.district_id}">
                            <li class="clearfix" style="margin-bottom:5px">                            
                              <span class="facet_count">${item1?.total?:0}</span>	                      
                              <input id="full_district_id_${item.id}" name="district_id" type="checkbox" value="${item?.id}" <g:if test="${(inrequest?.district_id?:[]).contains(item1?.district_id?:0)}">checked="checked"</g:if> onclick="setFilterFromPostFilter(this.id);" />
                              <label for="full_district_id_${item.id}">${item?.('name'+context?.lang)}</label>
                            </li>
                          </g:if></g:each>
                        </g:each>                                
                        </ul>                                                
                      </li>
                      </g:if><g:if test="${metroMax}">
                      <li id="lightbox_container_metro" class="lightbox_filter_container" style="display:none;">                      
                        <ul class="search_filter_content">
                        <g:each in="${metro}" var="item" status="i">  
                          <g:each in="${metroMax}" var="item1"><g:if test="${item?.id==item1.id}">
                            <li class="clearfix" style="margin-bottom:5px">                            
                              <span class="facet_count">${item1?.total?:0}</span>
                                <input id="full_metro_id_${item.id}" name="metro_id" type="checkbox" value="${item?.id}" <g:if test="${(inrequest?.metro_id?:[]).contains(item1?.id?:0)}">checked="checked"</g:if> onclick="setFilterFromPostFilter(this.id);" />                              
                              <label for="full_metro_id_${item.id}">${item?.('name'+context?.lang)}</label>
                            </li>
                          </g:if></g:each>
                        </g:each>                                
                        </ul>                                                
                      </li>
                      </g:if>
                    </g:else>
                    </ul>                    				
                    <div id="lightbox_filter_action_area" class="segment buttons">                  
                      <input type="button" id="lightbox_search_button" class="button-glossy orange mini" value="${message(code:'button.search')}" onclick="setFilterFullAndSubmit()" style="margin-right:10px" />
                      <input type="button" class="button-glossy grey mini" value="${message(code:'button.reset')}" onclick="resetButton()" />
                      <div style="display:none">
                        <input type="reset" id="full_form_reset" value="${message(code:'button.reset')}" />				  
                      </div>
                    </div>
                  </g:form>
                </div>
              </td>
            </tr><!--/noindex-->
            <g:render template="/listingbottom" />
   </body>
</html>
