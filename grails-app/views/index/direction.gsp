<html>
  <head>
    <title>${popdirection['ceo_title'+context?.lang]?popdirection['ceo_title'+context?.lang]:popdirection['name'+context?.lang]}</title>
    <meta name="keywords" content="${context?.lang?'':(popdirection?.ceo_keywords?popdirection?.ceo_keywords:popdirection?.keyword)}" />
    <meta name="description" content="${popdirection['ceo_description'+context?.lang]?popdirection['ceo_description'+context?.lang]:(context?.lang?'':popdirection?.itext)}" />
    <meta property="fb:app_id" content="${fb_api_key}" />
    <meta property="og:type" content="object" />
    <meta property="og:site_name" content="StayToday" /> 
    <meta property="og:locale" content="${context?.lang?'en_US':'ru_RU'}" />
    <meta property="og:locale:alternate" content="${context?.lang?'ru_RU':'en_US'}" />    
    <meta property="og:url" content="${createLink(controller:'index',action:'direction',id:popdirection?.linkname,params:[country:breadcrumbs?.country?.urlname],base:context?.mainserverURL_lang)}" />		
    <meta property="og:title" content="${popdirection['header'+context?.lang]?:popdirection['name2'+context?.lang]+'. '+popdirection['name'+context?.lang]}" />
    <meta property="og:description" content="${popdirection['ceo_description'+context?.lang]?popdirection['ceo_description'+context?.lang]:(context?.lang?'':popdirection?.itext)}" />
    <meta property="og:image" content="${imageurl+popdirection?.picture}" />    
    <link rel="canonical" href="${createLink(controller:'index',action:'direction',id:popdirection?.linkname,params:[country:breadcrumbs?.country?.urlname],base:context?.mainserverURL_lang)}" />
    <link rel="image_src" href="${imageurl+popdirection?.picture}" />  
    <meta name="layout" content="main"/>
    <g:javascript>
      var iX=${inrequest?.x?:(option?.x?:3700000)}, iY=${inrequest?.y?:(option?.y?:5500000)},
        iScale=${inrequest?.zoom?:3}, MAX_MAP_ZOOM=17,
        map=null, HMap=[], placemark={}, gBounds=null, lsHomes = new Array(), lsCities = new Array(),tmpPlacemark= new Array();
      function noticeClick(iId){
        <g:remoteFunction controller='index' action='noticeClick' params="\'id=\'+iId" />
      }
      function toggleFilter(container){ 
        var li = $(container);                 
        if(li.className == 'search_filter')
          li.className = 'search_filter closed open';
        else if(li.className == 'search_filter closed open')
          li.className = 'search_filter'; 
        else if(li.className == 'search_filter closed')
          li.className = 'search_filter open';
        else if(li.className == 'search_filter open')
          li.className = 'search_filter closed';      
      }
      var star_id=0;
      function addtofavourite(lId){
        star_id=lId;
        <g:remoteFunction controller='home' action='selectcompany' onSuccess='setFavourite(e)' params="\'id=\'+lId" />
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
          $('favorite').href='<g:createLink controller="trip" action="favorite"/>'; 
          $('favorite').parentElement.addClassName(' starred');
        } else {
          $('favorite').addClassName('no_active');
          $('favorite').href='javascript:void(0)';
          $('favorite').parentElement.removeClassName(' starred');
        }
      }    
      function initialize(){
        if($("map_large_canvas")!=undefined)
          $("map_large_canvas").setStyle({height: '385px', width: '730px'});
        getBounds();
        Yandex();
      }
      function getBounds(){     
        var sBalloon="baloon";
        <g:each in="${poprecordsMap}" var="home" status="i">
          var sText =  ${Math.round(home?.price / valutaRates)} + "&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>";
          sBalloon ='<div class="thumbnail shadow" style="width:220px;height:160px">'+
                    '  <img width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />';
          <g:if test="${home?.curdiscount}">              
          sBalloon+='  <div class="discount-container">'+
                    '    <div class="discount-price">';
            <g:if test="${home?.curdiscount==2}">sBalloon+='-${home?.hotdiscounttext}';</g:if>
            <g:elseif test="${home?.curdiscount==1}">sBalloon+='-${home?.longdiscounttext}';</g:elseif>
            <g:else>sBalloon+='-${home?.hotdiscounttext ? home.hotdiscounttext : home.longdiscounttext}';</g:else>
            sBalloon+='  </div>'+
                      '</div>';                      
          </g:if>              
          sBalloon+='</div><div class="photo_item_details">'+
                    '  <span class="ss_price b-rub">'+sText+'</span>'+
                    '</div><div class="description" style="padding-left:0px">'+        
                    '  <h2 class="title" style="padding-left:0px;margin-top:10px">'+
                    '    <g:shortString text="${home?.name}" length="16"/>'+
                    '  </h2>'+              
                    '  <ul class="details-list" style="clear:left;width:205px">'+
                    '    <li class="details-list-item location">'+
                    '      <g:shortString text="${home?.shortaddress}" length="30"/>'+
                    '    </li>'+
                    '  </ul>'+      
                    '</div>';      
          lsHomes.push([${home?.x?:0},${home?.y?:0},0,"${home?.name?:''}",sBalloon,"home_"+"${home?.linkname}".unescapeHTML().replace(/&#39;/g, "\'"),"${home.city}"]);        
        </g:each>
        <g:each in="${directionCitiesMap}" var="city" status="i">
          <g:if test="${(city[2]?:0) && (city[3]?:0)}">      
            lsCities.push([${city[2]?:0},${city[3]?:0},1,"${city[context?.lang?8:0]?:''}",sBalloon]);        
          </g:if>  
        </g:each>      
      }  
      function Yandex(){
        ymaps.ready(function () {      
          map = new ymaps.Map("map_large_canvas",{ center:[iY/100000,iX/100000], zoom: iScale, behaviors: ["default","scrollZoom"] });        
          map.controls.add("smallZoomControl")            
            .add("scaleLine")       
            .add(new ymaps.control.MapTools({ items: ["default"]}));                                    		                            
          map.events.add("boundschange",
            function(e) {
              if(map.getZoom()<${map_home_scale?:11}){
                removeHomeMarkers();
                tmpPlacemark= new Array();          
              }else{  
                if(!tmpPlacemark.length)
                  updateHomeMap();       
              }  
            }
          );        
          gBounds =new ymaps.GeoObjectCollection();
          if(map.getZoom()>=${map_home_scale?:11})         
            for(var i=0; i<lsHomes.length; i++)
              addMarker(lsHomes[i][0],lsHomes[i][1],lsHomes[i][2],lsHomes[i][3],lsHomes[i][4],lsHomes[i][5],lsHomes[i][6]);        
          for(var i=0; i<lsCities.length; i++)
            addMarker(lsCities[i][0],lsCities[i][1],lsCities[i][2],lsCities[i][3],lsCities[i][4],'','');
          setBounds();        
        });     
      }  
      function setBounds(){
        map.geoObjects.add(gBounds);
        map.setBounds(gBounds.getBounds());          
        map.setCenter(map.getCenter(), (map.getZoom()!=MAX_MAP_ZOOM)?(map.getZoom()):(map.getZoom()-1), { checkZoomRange: true });    
      }
      function addMarker(iX,iY,bCity,sName,sBalloon,sLinkname,sCity){        
        var  placemark = null;      
        if(bCity){
          placemark = new ymaps.Placemark([iY/100000, iX/100000], { hintContent:'<b>'+sName+'</b><br/><g:rawHtml>${message(code:'direction.map.marker_press')}</g:rawHtml>' }, {
            draggable: false,                
            hasHint: 1,
            hasBalloon: false,
            iconImageHref:"${resource(dir:'images/infras',file:'house.png')}",
            iconImageSize: [48, 36],
            iconImageOffset:[-14, -30],          
          });
        }else{
          placemark = new ymaps.Placemark([iY/100000, iX/100000], { hintContent:'<div style=\"width:220px;margin:5px\" class=\"relative\">'+sBalloon+'</div>' }, {
            draggable: false,                
            hasHint: 1,
            hasBalloon: false,
            iconImageHref:"${resource(dir:'images',file:'markerH.png')}",
            iconImageSize: [25,41],
            iconImageOffset:[-14,-35],
            //iconContentLayout:ymaps.templateLayoutFactory.createClass('<div class="point">'+iNumber+'</div>')
          });
        }      
        if(bCity){ 
          placemark.events.add("click", function (result) {        
            transit(${context.is_dev?1:0},['type_all',sName,'${breadcrumbs.country.urlname}'],'view=map',1,'','${context?.lang}');        
          },placemark);
        }else{
          placemark.events.add("click", function (result) {        
            transit(${context.is_dev?1:0},[sLinkname,sCity,'${breadcrumbs.country.urlname}'],'',1,'','${context?.lang}');        
          },placemark);
        }      
        if(map)         
          gBounds.add(placemark);                                                                                          
        if(!bCity)
          tmpPlacemark.push(placemark);	  	  
      }	
      function removeHomeMarkers(){
        for(var i=0;i<tmpPlacemark.length;i++ ){        
          map.geoObjects.remove(tmpPlacemark[i]);        
          gBounds.remove(tmpPlacemark[i]);
        }
      }
      function updateHomeMap(){
        for(var i=0; i<lsHomes.length; i++)
          addMarker(lsHomes[i][0],lsHomes[i][1],lsHomes[i][2],lsHomes[i][3],lsHomes[i][4],lsHomes[i][5],lsHomes[i][6]);
      }        
    </g:javascript>
    <style type="text/css">
      .search_filter_content li.dott:last-of-type { border-bottom: none }
      h2.ins { margin: 20px 12px 10px!important}
    </style>
  </head>
  <body onload="initialize()">  
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">
                <a class="button" rel="nofollow" onclick="transit(${context.is_dev?1:0},['cities'],'','','','${context?.lang}')">${message(code:'common.renthome')}</a>
              </td>
              <td colspan="3" class="rent ss">
                <h1>${popdirection['header'+context?.lang]?:popdirection['name2'+context?.lang]+'. '+popdirection['name'+context?.lang]}</h1>
              </td>
            </tr>
            <tr>              
              <td colspan="3" class="bg_shadow">
                <div class="breadcrumbs" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" rel="v:url" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span><span typeof="v:Breadcrumb">
                    <g:if test="${breadcrumbs.country.is_index}"><a href="<g:createLink controller='index' action='popdirectionAll' params='[id:breadcrumbs.country.urlname]' base='${context?.mainserverURL_lang}'/>" rel="v:url" property="v:title"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" property="v:title"></g:else>
                      ${breadcrumbs.country['name'+context?.lang]}<g:if test="${breadcrumbs.country.is_index}"></a></g:if><g:else></span></g:else> &#8594;
                  </span>
                  ${popdirection['name2'+context?.lang]}
                </div>
              </td>
            </tr>
            <tr>
              <td valign="top">                
                <ul class="collapsable_filters">
                <g:if test="${directionCities}">
                  <li class="search_filter" id="cities_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('cities_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('cities_container');">${message(code:'direction.cities_of_direction')}</a>
                    <ul class="search_filter_content">
                    <g:each in="${directionCities}" var="city">  
                      <li style="display:inline-block">
                        <g:if test="${city.value.is_index}"><a rel="tag" href="<g:createLink mapping='${city.value.domain?'hSearchTypeDomain'+context?.lang:'hSearchType'+context?.lang}' params='${city.value.domain?[type_url:'all']:[where:city.key,country:breadcrumbs.country.urlname,type_url:'all']}' base='${city.value.domain?'http://'+city.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${message(code:'common.daily_housing_rental_in')} ${city.value.name2}" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:if>
                        <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_all','${city.key}','${Country.get(City.get(city.value.city_id)?.country_id)?.urlname}'],'','','','${context?.lang}')" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:else>
                        ${city.key}<g:if test="${city.value.is_index}"></a></g:if><g:else></span></g:else>                      
                      </li>
                    </g:each>
                    </ul>                      
                  </li>
                </g:if>
                <g:if test="${previousdirection || nextdirection}">
                  <li class="search_filter" id="direction_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('direction_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('direction_container');">${message(code:'direction.another_direction')}</a>
                    <ul class="search_filter_content">
                    <g:if test="${previousdirection}"> 
                      <li class="clearfix dott">
                        <g:link action="direction" id="${previousdirection?.linkname}" params="${[country:Country.get(previousdirection?.country_id)?.urlname]}" base="${context?.mainserverURL_lang}">${previousdirection['name2'+context?.lang]}</g:link>
                      </li>
                    </g:if><g:if test="${nextdirection}">
                      <li class="clearfix dott">
                        <g:link action="direction" id="${nextdirection?.linkname}" params="${[country:Country.get(nextdirection?.country_id)?.urlname]}" base="${context?.mainserverURL_lang}">${nextdirection['name2'+context?.lang]}</g:link>
                      </li>
                    </g:if>
                      <li class="clearfix dott last" style="text-align:right">
                        <a href="javascript:void(0)" rel="nofollow" onclick="transit(${context.is_dev?1:0},['${breadcrumbs.country.urlname}'],'','','','${context?.lang}')">
                          ${message(code:'list.filtr.show_more')}
                        </a>
                      </li>              
                    </ul>
                  </li>
                </g:if>
                <g:if test="${(hotdiscount>0)||(longdiscount>0)|| articles || popdirection?.is_cottage}">
                  <li class="search_filter" id="additional_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('additional_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('additional_container');">${message(code:'direction.useful_info')}</a>
                    <ul class="search_filter_content">
                    <g:if test="${hotdiscount>0}">
                      <li class="clearfix dott">
                        <span class="facet_count">${hotdiscount}</span>
                        <g:link controller="index" action="popdiscounts" id="${popdirection.linkname}" params="${[country:breadcrumbs.country.urlname]}" fragment="hotdiscount" style="line-height:12px" base="${context?.mainserverURL_lang}">
                          ${message(code:'common.discounts_for_hot_offers')}
                        </g:link>
                      </li>
                    </g:if>
                    <g:if test="${longdiscount>0}">
                      <li class="clearfix dott">
                        <span class="facet_count">${longdiscount}</span>
                        <g:link controller="index" action="popdiscounts" id="${popdirection.linkname}" params="${[country:breadcrumbs.country.urlname]}" fragment="longdiscount" style="line-height:12px" base="${context?.mainserverURL_lang}">
                          ${message(code:'common.discounts_for_long_offers')}
                        </g:link>
                      </li>
                    </g:if>                    
                    <g:if test="${articles}">
                      <li class="clearfix dott">
                        <span class="facet_count">${articles}</span>
                        <g:link mapping="${'timeline'+context?.lang}" params="${[tag:popdirection.tagname]}" absolute="true" style="line-height:12px">
                          ${message(code:'direction.articles_about_topic')} ${popdirection.('name2'+context?.lang)}
                        </g:link>
                      </li>
                    </g:if>
                    <g:if test="${popdirection?.is_cottage}">
                      <li class="clearfix dott">
                        <span class="facet_count">${cottages}</span>
                        <g:link controller="index" action="cottages" id="${popdirection.linkname}" params="${[country:breadcrumbs.country.urlname]}" base="${context?.mainserverURL_lang}">
                          ${message(code:'direction.cottages')} ${popdirection['name'+(context?.lang?('2'+context?.lang):'_r')]}
                        </g:link>
                      </li>
                    </g:if>
                    </ul>
                  </li>
                </g:if>    
                <g:if test="${lastHome}">
                  <li class="search_filter" id="history_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('history_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('history_container');">${message(code:'label.recently_sm')}</a>
                    <ul class="search_filter_content" style="padding:15px 6px">
                    <g:each in="${lastHome}" var="home" status="i">                    
                      <li class="hlisting offer-rent clearfix">
                        <div class="item housing clearfix">
                          <div class="thumbnail shadow" style="width:220px;height:160px">
                            <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.get(home.city_id)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                            <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <img class="photo" width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                          </div>                        
                          <div class="photo_item_details">
                            <span class="price ss_price b-rub">${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></span>
                          </div>                        
                        </div>
                        <div class="fn title">
                          <g:if test="${home.is_index}"><a class="url name" href="<g:createLink mapping='${City.get(home.city_id)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="self bookmark" title="${home?.name}"></g:if>
                          <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                            <g:shortString text="${home?.name}" length="18"/>
                          <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                        </div>                        
                        <div class="description">
                          <ul class="details-list" style="width:220px">              
                            <li class="details-list-item location">
                              <g:shortString text="${home?.shortaddress}" length="30"/>
                            </li>
                          </ul>
                        </div>
                        <div class="lister fn">${User.findWhere(client_id:home?.client_id).nickname}</div>                        
                      </li>
                    </g:each>                    
                    </ul>                    
                  </li>
                </g:if>
                </ul>
              </td>
              <td colspan="3" valign="top" class="body shadow">
                <div class="search_container">
                  <div id="search_body">
                  <g:if test="${popdirection['annotation'+context?.lang]}">
                    <div class="page-topic">
                      <p><b><g:rawHtml>${popdirection['annotation'+context?.lang]}</g:rawHtml></b></p>
                    </div>
                  </g:if>
                    <div class="page-topic" style="padding:0">
                      <div id="map_large_canvas"></div>                   
                    </div>
                  <g:if test="${!context?.lang && popdirection?.itext}">
                    <div class="page-topic">
                      <g:rawHtml>${popdirection?.itext?:''}</g:rawHtml>
                    </div>
                  </g:if><g:if test="${notice}"><!--noindex-->  
                    <div class="page-topic">
                    <g:each in="${notice}" var="it" status="i">
                      <div class="notice ads width clearfix" onclick="noticeClick(${it.id})">
                        <g:if test="${it?.is_index}"><a href="${serverUrl.toString()+'/'+it?.url}" title="${it?.title}"></g:if>
                        <g:else><span class="link" onclick='transit(${context.is_dev?1:0},["${it?.url}"],"","","","${context?.lang}")'></g:else>                        
                          <img class="thumbnail userpic" src="${resource(dir:'images',file:(it?.image)?'anonses/'+it?.image:'user-default-picture.png')}" border="0" />                          
                          <div class="description">
                            <h2>${it['title'+context?.lang]}</h2>
                            <p>${it['description'+context?.lang]}</p>
                          </div>
                        <g:if test="${it?.is_index}"></a></g:if><g:else></span></g:else>
                      </div>
                    </g:each>                   
                    </div><!--/noindex-->
                  </g:if><g:if test="${poprecords}">                    
                    <h2 class="ins">${message(code:'direction.we_recommend')}</h2>
                    <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                    <g:each in="${poprecords}" var="home" status="i">
                      <g:if test="${(i % 3)==0}"><tr></g:if>
                        <td class="hlisting offer-rent" width="33%" onmouseout="this.removeClassName(' selected');" onmouseover="this.addClassName(' selected');">
                          <div class="item housing clearfix" style="width:220px">                                                  
                            <div class="thumbnail shadow" style="width:220px;height:160px">
                              <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.get(home.city_id)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                              <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                                <img class="photo" width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                              <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                            </div>
                            <div class="photo_item_details">
                              <span class="price ss_price b-rub">${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></span>
                            </div>
                          </div>
                          <div class="fn title">
                            <g:if test="${home.is_index}"><a class="url name" href="<g:createLink mapping='${City.get(home.city_id)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="self bookmark" title="${home?.name}"></g:if>
                            <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <g:shortString text="${home?.name}" length="16"/>
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                            <a id="star_${home?.id}" class="star_icon_container" href="javascript:void(0)" rel="nofollow" title="${message(code:'common.add_to_favourite')}">
                              <div id="star_icon_${home?.id}" onclick="addtofavourite(${home?.id})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.id)}">starred</g:if>"></div>
                            </a>
                          </div>                          
                          <div class="description">
                            <ul class="details-list" style="width:220px">              
                              <li class="details-list-item location">
                                <g:shortString text="${home?.shortaddress}" length="30"/>
                              </li>
                            </ul>                          
                          </div>
                          <div class="lister fn">${User.findWhere(client_id:home?.client_id).nickname}</div>
                        </td>
                      <g:if test="${((i % 3)==0) && (poprecords.size() == 1)}">
                        <td width="33%">&nbsp;</td><td width="33%">&nbsp;</td>
                      </g:if><g:elseif test="${((i % 3)==1) && (poprecords.size() == 2)}">
                        <td width="33%">&nbsp;</td>
                      </g:elseif><g:elseif test="${(i % 3)==2}">
                      </tr>
                      <g:if test="${i+1!=poprecords.size()}"><tr><td colspan="3" class="dashed">&nbsp;</td></tr></g:if>
                      </g:elseif>
                    </g:each>
                    </table>
                  </g:if>                  
                  </div>                  
                </div>
              </td>
            </tr>
  </body>
</html>
