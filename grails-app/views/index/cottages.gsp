<html>
  <head>
    <title>${popdirection['ceo_title_cot'+context?.lang]?popdirection['ceo_title_cot'+context?.lang]:message(code:'direction.cottages')+' '+popdirection['name'+(context?.lang?'2'+context?.lang:'_r')]}</title>
    <meta name="keywords" content="${context?.lang?'':(popdirection?.ceo_keywords_cot?popdirection?.ceo_keywords_cot:popdirection?.keyword)}" />
    <meta name="description" content="${popdirection['ceo_description_cot'+context?.lang]?popdirection['ceo_description_cot'+context?.lang]:popdirection['annotation_cot'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl+(context.cquery?'?':'')}<g:rawHtml>${context.cquery}</g:rawHtml>" />
    <meta name="layout" content="main"/>    
    <g:javascript>
    var iX=${inrequest?.x?:(option?.x?:3700000)}, iY=${inrequest?.y?:(option?.y?:5500000)},
      iScale=${inrequest?.zoom?:3}, MAX_MAP_ZOOM=17,
      map=null, HMap=[], placemark={}, gBounds=null, lsHomes = new Array(), lsCities = new Array(),tmpPlacemark= new Array();
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
        lsHomes.push([${home?.x?:0},${home?.y?:0},"${home?.name?:''}",sBalloon,"home_"+"${home?.linkname}".unescapeHTML().replace(/&#39;/g, "\'"),"${home.city}"])        
      </g:each>
    }
    function Yandex(){
      ymaps.ready(function () {      
        map = new ymaps.Map("map_large_canvas",{ center:[iY/100000,iX/100000], zoom: iScale, behaviors: ["default","scrollZoom"] });         
        map.controls.add("smallZoomControl")            
          .add("scaleLine")       
          .add(new ymaps.control.MapTools({ items: ["default"]}));
        gBounds =new ymaps.GeoObjectCollection();
        for(var i=0; i<lsHomes.length; i++)
          addMarker(lsHomes[i][0],lsHomes[i][1],lsHomes[i][2],lsHomes[i][3],lsHomes[i][4],lsHomes[i][5]);
        setBounds();        
      });
    }
    function addMarker(iX,iY,iNumber,sBalloon,sLinkname,sCity){
      var sMarker= "${resource(dir:'images',file:'markerH.png')}";             
      var placemark = new ymaps.Placemark([iY/100000, iX/100000], { hintContent:'<div style=\"width:220px;margin:5px\" class=\"relative\">'+sBalloon+'</div>' }, {
        draggable: false,                
        hasHint: 1,
        hasBalloon: false,
        iconImageHref:sMarker,
        iconImageSize: [25,41],
        iconImageOffset:[-14,-35],
        //iconContentLayout:ymaps.templateLayoutFactory.createClass('<div style="color:white">'+iNumber+'</div>')
      });
      placemark.events.add("click", function (result) {        
        transit(${context.is_dev?1:0},[sLinkname,sCity,'${breadcrumbs.country.urlname}'],'',1);         
      },placemark);      
      if(map)
        gBounds.add(placemark);                    
    }
    function setBounds(){
      map.geoObjects.add(gBounds);
      map.setBounds(gBounds.getBounds());   
      map.setCenter(map.getCenter(), (map.getZoom()!=MAX_MAP_ZOOM)?(map.getZoom()):(map.getZoom()-1), { checkZoomRange: true });    
    }      
    </g:javascript>    
    <style type="text/css">
      .rent { padding: 0px !important }
      .rent h1 { margin: 0 10px 0 20px !important; height: auto !important }
      h2.ins { margin: 20px 12px 10px!important} 
      .search_filter_content li.dott:last-of-type { border-bottom: none }     
    </style>
  </head>
  <body onload="initialize()">  
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">
                <g:link class="button" uri="/cities" base="${context?.mainserverURL_lang}">${message(code:'common.renthome')}</g:link>
              </td>
              <td colspan="3" class="rent">
                <h1 class="header">${popdirection['header_cot'+context?.lang]?:message(code:'direction.cottages')+' '+popdirection['name'+(context?.lang?'2'+context?.lang:'_r')]}</h1>
              </td>
            </tr>
            <tr>              
              <td colspan="3" class="bg_shadow">
                <ul class="breadcrumbs" itemscope itemtype="http://schema.org/BreadcrumbList">
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" itemprop="item">
                      <span itemprop="name">${message(code:'label.main')}</span>
                    </a><meta itemprop="position" content="1" />
                  </li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <g:if test="${breadcrumbs.country.is_index}"><a href="<g:createLink controller='index' action='popdirectionAll' params='[id:breadcrumbs.country.urlname]' base='${context?.mainserverURL_lang}'/>" itemprop="item"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" itemprop="item"></g:else>
                      <span itemprop="name">${breadcrumbs.country['name'+context?.lang]}</span>
                    <g:if test="${breadcrumbs.country.is_index}"></a></g:if><g:else></span></g:else><meta itemprop="position" content="2" />
                  </li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <g:if test="${popdirection?.is_index}"><a href="<g:createLink controller='index' action='direction' id='${popdirection.linkname}' params='${[country:breadcrumbs.country.urlname]}' base='${context?.mainserverURL_lang}'/>" itemprop="item"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${popdirection?.linkname}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" itemprop="item"></g:else>
                      <span itemprop="item">${popdirection['name2'+context?.lang]}</span>
                    <g:if test="${popdirection?.is_index}"></a></g:if><g:else></span></g:else><meta itemprop="position" content="3" />
                  </li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <span itemprop="item">
                      <span itemprop="name">${message(code:'direction.cottages')}</span>
                    </span><meta itemprop="position" content="4" />
                  </li>
                </ul>
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
                        <g:if test="${city.value.is_index}"><a rel="tag" href="<g:createLink mapping='${city.value.domain?'hSearchTypeDomain'+context?.lang:'hSearchType'+context?.lang}' params='${city.value.domain?[type_url:'cottages']:[where:city.key,country:breadcrumbs.country.urlname,type_url:'cottages']}' base='${city.value.domain?'http://'+city.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${message(code:'cottages.per_night_in')} ${city.value.name2}" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:if>
                        <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_cottages','${city.key}','${Country.get(City.get(city.value.city_id)?.country_id)?.urlname}'],'','','','${context?.lang}')" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:else>
                        ${city.key}<g:if test="${city.value.is_index}"></a></g:if><g:else></span></g:else>
                      </li>
                    </g:each>
                    </ul>                      
                  </li>
                </g:if><g:if test="${anotherCottageDirections}">
                  <li class="search_filter" id="direction_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('direction_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('direction_container');">${message(code:'direction.another_direction')}</a>
                    <ul class="search_filter_content">
                    <g:each in="${anotherCottageDirections}" var="item" status="i">
                      <li class="clearfix dott ${(i+1==anotherCottageDirections.size())?'last':''}">
                        <g:link action="cottages" id="${item?.linkname}" params="${[country:Country.get(item?.country_id)?.urlname]}" base="${context?.mainserverURL_lang}">${item['name2'+context?.lang]}</g:link>
                      </li>
                    </g:each>
                    </ul>
                  </li>
                </g:if><g:if test="${lastHome}">
                  <li class="search_filter" id="history_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('history_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('history_container');">${message(code:'common.viewed_recently')}</a>
                    <ul class="search_filter_content" style="padding:15px 6px">
                    <g:each in="${lastHome}" var="home" status="i">                    
                      <li class="clearfix">
                        <div class="relative">
                          <div class="thumbnail shadow" style="width:220px;height:160px">
                            <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.get(home.city_id)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                            <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <img width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                          </div>                        
                          <div class="photo_item_details">
                            <span class="ss_price b-rub">${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></span>
                          </div>                        
                        </div>
                        <div class="description">
                          <h2 class="title">
                            <g:if test="${home.is_index}"><a class="name" href="<g:createLink mapping='${City.get(home.city_id)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                            <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <g:shortString text="${home?.name}" length="18"/>
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                          </h2>
                          <ul class="details-list" style="clear:left;width:220px">              
                            <li class="details-list-item location">
                              <g:shortString text="${home?.shortaddress}" length="30"/>                  
                            </li>
                          </ul>                          
                        </div> 
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
                  <g:if test="${popdirection['annotation_cot'+context?.lang]}">
                    <div class="page-topic">
                      <p><b><g:rawHtml>${popdirection['annotation_cot'+context?.lang]?:''}</g:rawHtml></b></p>
                    </div>
                  </g:if>
                    <div class="page-topic" style="padding:0;">
                      <div id="map_large_canvas" style="height:385px;width:730px"></div>                   
                    </div>
                  <g:if test="${!context?.lang && popdirection?.itext_cot}">
                    <div class="page-topic">
                      <g:rawHtml>${popdirection?.itext_cot?:''}</g:rawHtml>
                    </div>
                  </g:if>
                  <g:if test="${poprecords}">
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
                              <g:shortString text="${home?.name}" length="15"/>
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
