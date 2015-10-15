<html>
  <head>
    <title>${title?:''}</title>    
    <meta name="description" content="${description?:''}" />    
  <g:if test="${infotags?.isfound}">
    <g:if test="${breadcrumbs.city.domain}">
    <link rel="canonical" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?('hSearchMetroDomain'+context?.lang):inrequest?.bedroom?('hSearchRoomDomain'+context?.lang):(inrequest?.citysight_id?:[]).size()==1?('hSearchCitysightDomain'+context?.lang):inrequest?.hometype_id!=1?('hSearchTypeDomain'+context?.lang):('mainpage'+context?.lang),params:((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:]),base:breadcrumbs.base)}" />      
    </g:if><g:else>
    <link rel="canonical" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?('hSearchMetro'+context?.lang):inrequest?.bedroom?('hSearchRoom'+context?.lang):(inrequest?.citysight_id?:[]).size()==1?('hSearchCitysight'+context?.lang):inrequest?.hometype_id!=1?('hSearchType'+context?.lang):('hSearch'+context?.lang),params:[where:breadcrumbs.city?.('name'+context?.lang)?:'',country:breadcrumbs?.country?.urlname?:'']+((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:]),absolute:true)}" />    
    </g:else>
    <link rel="alternate" hreflang="ru" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?'hSearchMetro':inrequest?.bedroom?'hSearchRoom':(inrequest?.citysight_id?:[]).size()==1?'hSearchCitysight':inrequest?.hometype_id!=1?'hSearchType':'hSearch',params:[where:breadcrumbs.city?.name?:'',country:breadcrumbs?.country?.urlname?:'']+((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:]),base:context?.mobileURL)}" />
    <link rel="alternate" hreflang="en" href="${createLink(mapping:(inrequest?.metro_id?:[]).size()==1?'hSearchMetro_en':inrequest?.bedroom?'hSearchRoom_en':(inrequest?.citysight_id?:[]).size()==1?'hSearchCitysight_en':inrequest?.hometype_id!=1?'hSearchType_en':'hSearch_en',params:[where:breadcrumbs.city?.name_en?:'',country:breadcrumbs?.country?.urlname?:'']+((inrequest?.bedroom && !((inrequest?.metro_id?:[]).size()==1))?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.bedroom && (inrequest?.metro_id?:[]).size()==1)?[bedroom:inrequest?.bedroom]:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&!inrequest?.bedroom?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1&&inrequest?.bedroom?[citysight_id:inrequest?.citysight_id[0]]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.hometype_id!=1 && !((inrequest?.metro_id?:[]).size()==1 || (inrequest?.citysight_id?:[]).size()==1)?[type_url:breadcrumbs.hometype?.urlname?:'all']:[:])+(inrequest?.hometype_id && ((inrequest?.metro_id?:[]).size()==1 || ((inrequest?.citysight_id?:[]).size()==1)&&!inrequest?.bedroom)?[hometype_id:inrequest.hometype_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:]),base:context?.mobileURL)}" />    
  </g:if><g:else>
    <link rel="canonical" href="${createLink(controller:'home',action:'list',params:[where:inrequest?.where?:'']+(inrequest?.bedroom?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:]),absolute:true)}" />    
    <link rel="alternate" hreflang="ru" href="${createLink(controller:'m',action:'list',params:[where:inrequest?.where?:'']+(inrequest?.bedroom?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:]),base:context?.mobileURL)}" />
    <link rel="alternate" hreflang="en" href="${createLink(controller:'m',action:'list',params:[where:inrequest?.where?:'']+(inrequest?.bedroom?[bedroom:inrequest?.bedroom,type_url:'flats']:[:])+((inrequest?.metro_id?:[]).size()==1?[metro_url:Metro.get(inrequest?.metro_id[0])?.urlname]:[:])+((inrequest?.citysight_id?:[]).size()==1?[citysight_url:Citysight.get(inrequest?.citysight_id[0])?.urlname]:[:])+((inrequest?.district_id?:[]).size()==1?[district_id:inrequest?.district_id[0]]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:]),base:context?.mobileURL+(context?.lang?'':'/en'))}" />    
  </g:else>  
    <meta name="layout" content="m" />
    <script type="text/javascript">
      var isNearSearch=0, popupcont='',
          initialLocation=new google.maps.LatLng(${userPoint?.y?userPoint?.y/100000:0},${userPoint?.x?userPoint?.x/100000:0});      
      function init() {           
        jQuery('#sortFilter.ui-listview li').click(function(){
          jQuery(this).parent().find('li.ui-btn-active').removeClass('ui-btn-active');
          jQuery(this).addClass('ui-btn-active');
        });
        map_initialize();           
        jQuery('#buying_slider_min').change(function() {
          var min = parseInt(jQuery(this).val());
          var max = parseInt(jQuery('#buying_slider_max').val());
          if (min > max) {
            jQuery(this).val(max);
            jQuery(this).slider('refresh');
          }
        });
        jQuery('#buying_slider_max').change(function() {
          var min = parseInt(jQuery('#buying_slider_min').val());
          var max = parseInt(jQuery(this).val());
          if (min > max) {
            jQuery(this).val(min);
            jQuery(this).slider('refresh');
          }
        });
      }
      function map_initialize(){     
        var iMapHeight, mapCenterDefault={destination:new google.maps.LatLng(55.75278, 37.62295),zoom:5};        
        iMapHeight = jQuery(window).height()-jQuery('#page_header').height();
        jQuery('#map_canvas').gmap({ 
          'center': mapCenterDefault.destination, 
          'zoom': mapCenterDefault.zoom, 
          'mapTypeControl': false,
          'mapTypeId': 'hybrid',
          'navigationControl': true,
          'streetViewControl': false
        });  
        jQuery('#map_canvas').css('minHeight',iMapHeight);
        jQuery('#map_canvas').gmap('refresh');        
      }      
      function searchHome(){ 
        var where=$('where').value;
        showListing();
        if(!where.length)
          isNearSearch=1;
        else
          isNearSearch=0;
        $('filter_form_submit').click();        
      }      
      function showListing(){
        clearMap();
        $('map_canvas').hide();
        $('footer_listing').show();
        $('view-map').removeClassName('ui-btn-active');
        $('view-list').addClassName('ui-btn-active');
        $('output').show();   
      }      
      function showMap(){
        $('footer_listing').hide();  
        $('output').hide();        
        $('view-list').removeClassName('ui-btn-active');
        $('view-map').addClassName('ui-btn-active');
         
        var bounds = new google.maps.LatLngBounds(); 
        var markers=jQuery('#map_canvas').gmap('get','markers');
        var bFlag=0;
        jQuery.each(markers, function(key, value){
          bFlag=1;  
          bounds.extend(value.position);
          value.setVisible(true); 
        });
        $('map_canvas').show();
        if(bFlag)
          jQuery('#map_canvas').gmap('get', 'map').fitBounds(bounds);         
        if(isNearSearch){
          var image = new google.maps.MarkerImage(
            "${resource(dir:'images/mobile',file:'image_yah.png')}",
            new google.maps.Size(32,32),
            new google.maps.Point(0,0),
            new google.maps.Point(16,32)
          );      
          var shadow = new google.maps.MarkerImage(
            "${resource(dir:'images/mobile',file:'shadow_yah.png')}",
            new google.maps.Size(52,32),
            new google.maps.Point(0,0),
            new google.maps.Point(16,32)
          );      
          var shape = {
            coord: [5,0,14,1,17,2,28,3,28,4,28,5,29,6,29,7,29,8,29,9,29,10,29,11,29,12,30,13,30,14,30,15,30,16,30,17,30,18,30,19,30,20,29,21,27,22,6,23,6,24,7,25,7,26,7,27,7,28,7,29,7,30,7,31,6,31,5,30,5,29,5,28,5,27,5,26,4,25,4,24,4,23,4,22,4,21,4,20,4,19,4,18,3,17,3,16,3,15,3,14,3,13,3,12,3,11,3,10,3,9,2,8,2,7,2,6,2,5,2,4,2,3,1,2,1,1,1,0,5,0],
            type: 'poly'
          };            
          jQuery('#map_canvas').gmap('addMarker',{ 
            'position': initialLocation, 
            'animation': google.maps.Animation.DROP,
            'icon': image,
            'shadow': shadow,
            'shape': shape,
            'bounds': true 
          }).click(function(){                
            jQuery('#map_view_canvas').gmap('openInfoWindow',{'content':'Вы здесь!'},this);
          });  
        } 
        jQuery('#map_canvas').gmap('refresh');      
      }
      function clearMap(){
        jQuery('#map_canvas').gmap('closeInfoWindow');
        jQuery('#map_canvas').gmap('clear','markers'); 
        jQuery('#map_canvas').gmap('set','bounds',null);
      }
      function resetFiltr(){
        $('buying_slider_min').value=100;
        $('buying_slider_max').value=5000;  
        if(jQuery('#buying_slider_min').length){
          jQuery('#buying_slider_min').trigger('change');
          jQuery('#buying_slider_max').trigger('change');
        }
        jQuery('#popupFiltr input[type=checkbox]:checked').each(function() {
          jQuery(this).attr('checked', false);
        });
        jQuery('.ui-checkbox-on').each(function() { 
          jQuery(this).removeClass('ui-checkbox-on');      
         });
        jQuery('.ui-icon-checkbox-on').each(function() { 
          jQuery(this).removeClass('ui-icon-checkbox-on');
          jQuery(this).addClass('ui-icon-checkbox-off');
        });
      }
      function setSort(iId){
        jQuery('#sorth').val(iId);
      } 
      //////////////////////////////////////////////////
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null)
          return;
        new Ajax.Updater(
          { success: $('output') },
          link.href,
          { evalScripts: true, onComplete: function(response) { jQuery('#ajax_wrap').listview(); } }
        );
      }
      function doremotesubmit(event){
        event.stop();
        <g:remoteFunction controller='m' action='search_list' params="\$('filter_form').serialize()+'&'+jQuery('#popupFiltr :input[value]').serialize()" onSuccess="jQuery('#output').html(e.responseText);jQuery('#ajax_wrap').listview();" before="\$(popupcont).click();"/>
      }
    </script>
  </head>
  <body onload="init()">
      <div data-role="content" style="padding:0 0">                
        <div id="listing_navbar" data-role="navbar" data-iconpos="left" data-theme="f">
          <ul>
            <li><a id="view-list" data-role="button" class="ui-btn-active" href="#" rel="nofollow" onclick="showListing()">${message(code:'list').capitalize()}</a></li>
            <li><a id="view-map" data-role="button" href="#" rel="nofollow" onclick="showMap()">${message(code:'map').capitalize()}</a></li>
          </ul>
        </div>     
        <div id="map_canvas" style="height:300px;width:100%;display:none">
        </div>     
        <ul id="output" data-role="listview" data-split-icon="arrow-r" style="margin:0">
        <g:if test="${records}"> 
          <li data-role="divider" data-theme="a" style="padding:0 0 0 10px;height:40px">
            <span class="count float">
              <span>${message(code:'mobile.found')}</span><span id="ads_count">${count}</span>
            </span>
            <span class="pagination col">
              <g:paginate controller="m" action="search_list" prev="&lt;" next="&gt;" 
                maxsteps="1" omitFirst="${true}" omitLast="${true}" max="5" total="${count}" params="${params}" />
              <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
            </span>
          </li>        
          <g:each in="${records}" var="home" status="i">   
          <script type="text/javascript">
              var sText${i} = ${Math.round(home?.price / valutaRates)} + "<g:rawHtml>${valutaSym}</g:rawHtml>";            
              var sBalloon${i}='<div style="width:220px;height:200px">'+
                '<h2><a href="<g:createLink mapping='${'hView'+context?.lang}' params='${[0:0].inject([linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:home.city],linkparams)}' base='${context.mobileURL}'/>" title="${message(code:'common.description_and_photos')} ${home?.name}"><g:shortString text="${home?.name}" length="16" /></a></h2>'+
                '<ul class="details-list clearfix" style="width:100%">'+
                '<li class="details-list-item location"><span class="icons"></span><g:shortString text="${home.shortaddress}" length="30" /></li>'+
                '<li class="details-list-item room_type"><span class="icons"></span>${Hometype.get(home.hometype_id).name}</li>'+
                '<li class="details-list-item person_capacity"><span class="icons"></span>${Homeperson.get(home?.homeperson_id).name}</li>'+
                '<li class="details-list-item review_count"><span class="icons"></span>${home?.nref?:0} ${message(code:'common.reviews')}</li>'+    
                '</ul>'+      
                '<img class="thumbnail shadow" src="${home.mainpicture?urlphoto+home.client_id+'/t_'+home.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0" />'+
                '<span class="b-rub col">'+sText${i}+'</span>'+
                '</div>';          
            jQuery('#map_canvas').gmap('addMarker', { 
              'position': new google.maps.LatLng(${home.y}/100000, ${home.x}/100000), 
              'animation' : google.maps.Animation.DROP, 'bounds':true 
            }).click(function() {                
              jQuery('#map_canvas').gmap('openInfoWindow', { 'content': sBalloon${i} }, this);
            });
          </script>          
          <li data-icon="false">
            <g:if test="${home.is_index}"><a href="<g:createLink mapping='${'hView'+context?.lang}' params='${[0:0].inject([linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:home.city],linkparams)+
            (inrequest?.is_near?[is_near:1,x:userPoint?.x,y:userPoint?.y]:[:])}' base='${context.mobileURL}'/>" title="${message(code:'common.description_and_photos')} ${home?.name}"></g:if>
            <g:else><a href="javascript:void(0)" onclick="transit(0,['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'${[0:0].inject([:],linkparams).collect{it.key+"="+it.value}.join(",")}','','${context.mobileURL.minus('http://')}','${context?.lang}')" title="${message(code:'common.description_and_photos')} ${home?.name}"></g:else>            
              <div class="ui-li-thumb relative" style="margin-right:15px">
                <img class="thumbnail userpic shadow" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0" />
              <g:if test="${home?.curdiscount}">
                <div class="discount-container">                
                  <div class="discount-price">
                    <g:if test="${home?.curdiscount==2}">-${home?.hotdiscounttext}</g:if>
                    <g:elseif test="${home?.curdiscount==1}">-${home?.longdiscounttext}</g:elseif>
                    <g:else>-${home?.hotdiscounttext ? home.hotdiscounttext : home.longdiscounttext}</g:else>
                  </div>
                </div>
              </g:if><g:if test="${home?.is_fiesta}">
                <div class="fiesta-container" title="${message(code:'common.party')}"></div>
              </g:if>
              </div>
              <h2><span class="name"><g:shortString text="${home?.name}" length="16"/></span>
                <span name="star_${home.hid}" class="star_icon_container" title="${message(code:'common.add_to_favourite')}">
                  <div name="star_icon_${home.hid}" onclick="addtofavorite(${home.hid})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.hid)}">starred</g:if>"></div>
                </span>
              </h2>
              <ul class="ui-li-desc details-list">
                <li class="details-list-item location"><span class="icons"></span><g:shortString text="${home.shortaddress}" length="50"/></li>
                <li class="details-list-item room_type"><span class="icons"></span>${Hometype.get(home?.hometype_id)['name'+context?.lang]}</li>
              </ul>
              <div class="ui-li-desc">
                <ul class="ui-li-desc details-list float" style="margin-top:5px">
                  <li class="details-list-item person_capacity"><span class="icons"></span>${Homeperson.get(home?.homeperson_id)['name'+context?.lang]}</li>
                  <li class="details-list-item review_count"><span class="icons"></span>${home?.nref?:0} ${message(code:'common.reviews')}</li>
                </ul>
                <p class="ui-li-aside" style="margin-top:15px">
                  <span class="b-rub">${Math.round(home?.price / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                </p>
              </div>
            </a>
          </li>
          </g:each>
        </g:if><g:else>
          <li class="ui-li ui-li-static">${message(code:'mobile.ads.notfound')}</li>
        </g:else>
        </ul>
      </div>
      <div id="footer_listing" data-role="footer" data-position="fixed" data-theme="a">        
        <form onsubmit="doremotesubmit(event)" name="filter_form" id="filter_form">
          <fieldset class="ui-grid-a">
            <div class="ui-block-a">
              <a href="#popupSort" data-role="button" data-rel="popup" data-overlay-theme="c" data-theme="f" data-inline="false" onclick="popupcont='popupSortClose'">${message(code:'sort_by').capitalize()}</a>
              <div id="popupSort" data-role="popup" data-position-to="window" data-overlay-theme="a" data-history="false">
                <a href="#" id="popupSortClose" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">${message(code:'button.close')}</a>
                <ul id="sortFilter" data-role="listview" data-inset="true" data-theme="a">
                  <li data-role="divider" data-theme="f">${message(code:'sort_by').capitalize()}</li>
                  <li class="ui-btn-active"><a href="#" onclick="setSort(0);searchHome()">${message(code:'common.sort.default')}</a></li>
                  <li><a href="#" onclick="setSort(1);searchHome()">${message(code:'common.sort.price_asc')}</a></li>
                  <li><a href="#" onclick="setSort(2);searchHome()">${message(code:'common.sort.price_desc')}</a></li>
                  <li><a href="#" onclick="setSort(3);searchHome()">${message(code:'common.sort.create_date')}</a></li>
                </ul>              
              </div>          
            </div>
            <div class="ui-block-b">
              <a href="#popupFiltr" data-role="button" data-rel="popup" data-overlay-theme="c" data-theme="f" data-inline="false" onclick="popupcont='popupFiltrClose'">${message(code:'filter_by')}</a>
              <div id="popupFiltr" data-role="popup" data-position-to="window" data-overlay-theme="a" data-history="false" style="width:285px">
                <a href="#" id="popupFiltrClose" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">${message(code:'button.close')}</a>            
                <ul id="ul_filter" data-role="listview" data-theme="a" data-inset="true">            
                  <li data-role="divider" data-theme="f">${message(code:'filter_by')}</li>
                  <li style="padding:0 15px">
                    <div data-role="collapsible-set" data-inset="false" data-expanded-icon="arrow-d" data-collapsed-icon="arrow-r" data-content-theme="d" data-theme="a" style="margin:0">
                    <g:if test="${citysightMax && inrequest?.citysight_id}">
                      <div data-role="collapsible" data-collapsed="true">
                        <h3 style="margin:0 -15px">${message(code:'list.full_filtr.citysights')}</h3>
                        <div data-role="controlgroup" data-type="vertical" data-inset="false" id="popupFilterUlCitysight">
                        <g:if test="${citysight}"><g:each in="${citysight}" var="item"><g:each in="${citysightMax}" var="item1">
                          <g:if test="${item?.id==item1.id}">      
                          <input id="full_citysight_id_${item.id}" name="citysight_id" <g:if test="${inrequest?.citysight_id.find{it==item.id}}">checked</g:if> type="checkbox" value="${item?.id}" class="custom"/>
                          <label for="full_citysight_id_${item.id}" data-iconpos="left" data-theme="a">
                            ${item['name'+context?.lang]} <span class="counts">(${item1.total?:0})</span>
                          </label>
                          </g:if>
                        </g:each></g:each></g:if><g:else>${message(code:'mobile.nodata')}</g:else>
                        </div>
                      </div>                     
                    </g:if><g:else>
                      <g:if test="${districtMax}">
                      <div data-role="collapsible" data-collapsed="true">
                        <h3 style="margin:0 -15px">${message(code:'list.filtr.districts')}</h3>
                        <div data-role="controlgroup" data-type="vertical" data-inset="false" id="popupFilterUlDistrict">
                        <g:if test="${district}"><g:each in="${district}" var="item"><g:each in="${districtMax}" var="item1">
                          <g:if test="${item.id==item1.district_id}">      
                          <input id="full_district_id_${item.id}" name="district_id" type="checkbox" value="${item.id}" class="custom"/>
                          <label for="full_district_id_${item.id}" data-iconpos="left" data-theme="a">
                            ${item['name'+context?.lang]} <span class="counts">(${item1.total?:0})</span>
                          </label>
                          </g:if>
                        </g:each></g:each></g:if><g:else>${message(code:'mobile.nodata')}</g:else>
                        </div>
                      </div>
                      </g:if><g:if test="${metroMax}">
                      <div data-role="collapsible" data-collapsed="true">
                        <h3 style="margin:0 -15px">${message(code:'common.metro').capitalize()}</h3>
                        <div data-role="controlgroup" data-type="vertical" data-inset="false" id="popupFilterUlMetro">
                        <g:if test="${metro&&!inrequest?.citysight_id}"><g:each in="${metro}" var="item"><g:each in="${metroMax}" var="item1">
                          <g:if test="${item.id==item1.id}">
                          <input id="full_metro_id_${item.id}" name="metro_id" <g:if test="${inrequest?.metro_id.find{it==item.id}}">checked</g:if> type="checkbox" value="${item.id}" class="custom"/>
                          <label for="full_metro_id_${item.id}" data-iconpos="left" data-theme="a">
                            ${item['name'+context?.lang]} <span class="counts">(${item1.total?:0})</span>
                          </label>
                          </g:if>
                        </g:each></g:each></g:if><g:else>${message(code:'mobile.nodata')}</g:else>
                        </div>
                      </div>
                      </g:if>
                    </g:else>
                      <div data-role="collapsible" data-collapsed="true">
                        <h3 style="margin:0 -15px">${message(code:'list.filtr.price').capitalize()}</h3>
                        <div data-role="fieldcontain" data-inset="false" class="priceRangeInfo" style="width:97%">
                          <input readonly type="range" class="minBuyingSlider" name="price_min" id="buying_slider_min" value="100" min="100" max="5000" data-theme="d" data-track-theme="f"/>
                          <input readonly type="range" class="maxBuyingSlider" name="price_max" id="buying_slider_max" value="5000" min="100" max="5000" data-theme="d" data-track-theme="f" />                  
                        </div>
                      </div>                    
                      <div data-role="collapsible" data-collapsed="true">
                        <h3 style="margin:0 -15px">${message(code:'list.filtr.amenities').capitalize()}</h3>
                        <div data-role="controlgroup" data-type="vertical" data-inset="false" id="popupFilterUl">
                        <g:each in="${homeoption}" var="item" status="i">
                          <input type="checkbox" class="custom" value="1" id="${item.fieldname}" name="${item.fieldname}" />
                          <label for="${item.fieldname}" data-iconpos="left" data-theme="a">
                            ${item.name} <g:if test="${option?."${item.fieldname}">0}"><span class="counts">(${option?."${item.fieldname}"})</span></g:if>
                          </label>
                        </g:each>                        
                        </div>
                      </div>
                      <div data-role="collapsible" data-collapsed="true">
                        <h3 style="margin:0 -15px">${message(code:'list.filtr.add_terms').capitalize()}</h3>
                        <div data-role="controlgroup" data-type="vertical" data-inset="false" id="popupFilterUlAddit">
                        <g:if test="${option?.nref>0 || option?.rating>0}">
                          <g:if test="${option?.nref>0}">
                          <input type="checkbox" class="custom" value="1" id="nref" name="nref" />
                          <label for="nref" data-iconpos="left" data-theme="a">
                            ${message(code:'list.reviews')} <span class="counts">(${option?.nref?:0})</span>
                          </label> 
                          </g:if><g:if test="${option?.rating>0}">
                          <input type="checkbox" class="custom" value="1" id="rating" name="rating" />
                          <label for="rating" data-iconpos="left" data-theme="a">
                            ${message(code:'list.rating')} <span class="counts">(${option?.rating?:0})</span>
                          </label>
                          </g:if>
                        </g:if>                        
                        </div>                  
                      </div>
                    </div>                             
                  </li>              
                  <li data-role="divider" style="padding:5px">
                    <div class="ui-grid-a">
                      <div class="ui-block-a" style="width:45%">
                        <input type="button" data-theme="d" onclick="resetFiltr()" value="${message(code:'button.reset')}" />
                      </div>
                      <div class="ui-block-b" style="width:55%">
                        <input type="button" data-theme="f" onclick="searchHome()" value="${message(code:'button.view')}" />
                      </div>
                    </div>                  
                  </li>
                </ul>
              </div>          
            </div>        
          </fieldset>          
          <input type="hidden" name="where" id="where" value="${inrequest?.where?:''}" />          
          <input type="hidden" name="date_start" value="${inrequest?.date_start?formatDate(format:'yyyy-MM-dd',date:inrequest?.date_start):''}" />
          <input type="hidden" name="date_end" value="${inrequest?.date_end?formatDate(format:'yyyy-MM-dd',date:inrequest?.date_end):''}" />
          <input type="hidden" name="homeperson_id" value="${inrequest?.homeperson_id?:1}" />
          <input type="hidden" name="hometype_id" value="${inrequest?.hometype_id?:0}" />
          <input type="hidden" name="bedroom" value="${inrequest?.bedroom?:''}" />
          <input type="hidden" id="cur_x" name="x" value="${userPoint?.x?:0}" />
          <input type="hidden" id="cur_y" name="y" value="${userPoint?.y?:0}" />          
          <input type="hidden" name="sort" id="sorth" value="${inrequest?.sort?:0}" />
          <input type="hidden" id="is_near" name="is_near" value="${inrequest?.is_near?:0}" />
          <div style="display:none">
            <input type="submit" id="filter_form_submit" />
          </div>
        </form>
      </div>    
  </body>
</html>
