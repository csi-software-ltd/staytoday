<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}: ${inrequest.where?:inrequest.where}, ${message(code:'common.guests')} - ${inrequest.homeperson_id?:inrequest.homeperson_id}, ${message(code:'common.home_type')} - <g:if test="${inrequest?.hometype_id}"><g:each in="${hometype}" var="item"><g:if test="${item?.id==inrequest?.hometype_id}">${item['name'+context?.lang]}</g:if></g:each></g:if><g:else>${message(code:'common.any')}</g:else><g:if test="${inrequest?.date_start && inrequest?.date_end}">, ${message(code:'detail.check_in')} - ${String.format('%td.%<tm.%<tY',inrequest?.date_start)}-${String.format('%td.%<tm.%<tY',inrequest?.date_end)}</g:if></title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="print" />    
    <g:javascript>    	
    var iX=${inrequest?.x?:(option?.x?:3700000)}, iY=${inrequest?.y?:(option?.y?:5500000)},
        iScale=${inrequest?.zoom?:3}, MAX_MAP_ZOOM=17,
        map = null, placemark={}, xArray=new Array(), yArray=new Array(), nameArray=new Array(), gBounds = {};	
    function initialize(){
      $("map_view").show();
      $("map_large_canvas").setStyle({height: '380px', width: '980px'});
      $("search_body").setStyle({width: '980px'});
      $("results").setStyle({width: '980px'});
      Yandex();
      setTimeout('window.print()', 1000);
    }	
    function addMarker(iX,iY,iNumber,bStyle,sName){
      var sMarker= (bStyle)?"${resource(dir:'images',file:'marker_select.png')}":"${resource(dir:'images',file:'marker.png')}";             
      var placemark = new ymaps.Placemark([iY/100000, iX/100000],
        { hintContent:sName },   
        { draggable: false,                
          hasHint: 1,
          hasBalloon: false,
          iconImageHref:sMarker,
          iconImageSize: [25,41],
          iconImageOffset:[-14,-35],
          iconContentOffset:[0,10],
          iconContentLayout:ymaps.templateLayoutFactory.createClass('<div class="point">'+iNumber+'</div>')
        }
      );
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
      if(map)map.geoObjects.remove(tmpPlacemark);
    }    
    function Yandex(){
      ymaps.ready(function () {      
        map = new ymaps.Map("map_large_canvas",{ center:[iY/100000,iX/100000], zoom: iScale, behaviors:["default","scrollZoom"]});         
        map.controls.add("smallZoomControl").add("scaleLine").add(new ymaps.control.MapTools({ items: ["default"]}));                                    		                
        gBounds = new ymaps.GeoObjectCollection();
        for(var i = 0; i<xArray.length; i++)
          addMarker(xArray[i],yArray[i],i+1,0,nameArray[i]);          
        setBounds();        
      });      
    }
    function setBounds(){
      map.geoObjects.add(gBounds);
      map.setBounds(gBounds.getBounds());          
      map.setCenter(map.getCenter(),(map.getZoom()!=MAX_MAP_ZOOM)?(map.getZoom()-1):(map.getZoom()-2),{ checkZoomRange: true });    
    }     
    </g:javascript>
    <style type="text/css">span.currentStep { background: none !important }</style>
  </head>
  <body onload="initialize()">
            <tr>
              <td width="980" colspan="2" class="rent ss" style="background:#fff">
                <h1 style="color:#3F5765">${infotext['promotext1'+context?.lang]?infotext['promotext2'+context?.lang]:''}</h1>
                <ul class="details-list inline padd20">
                  <li class="details-list-item location">
                    <span class="icons"></span>
                    ${inrequest?.where?:''} <g:join in="${metro.collect{it['name'+context?.lang]}}" delimiter=", "/>
                  </li>                                 			  
                  <li class="details-list-item room_type">
                    <span class="icons"></span>
                    <g:if test="${inrequest?.hometype_id}"><g:each in="${hometype}" var="item"><g:if test="${item?.id==inrequest?.hometype_id}">${item['name'+context?.lang]}</g:if></g:each></g:if>
                    <g:else>${message(code:'common.any')}</g:else>${bedroom?(inrequest?.hometype_id?', '+bedroom['name3'+context?.lang]:', '+bedroom['name'+(context?.lang?'3'+context?.lang:'5')]):''}
                  </li>
                  <li class="details-list-item person_capacity">
                    <span class="icons"></span>
                    ${inrequest?.homeperson_id?:''}
                  </li>
                <g:if test="${inrequest.date_start?:inrequest?.date_start && inrequest?.date_end?:inrequest?.date_end}">
                  <li class="details-list-item dates">
                    <span class="icons"></span>                  
                    ${String.format('%td.%<tm.%<tY',inrequest?.date_start)} - ${String.format('%td.%<tm.%<tY',inrequest?.date_end)}
                  </li>
                </g:if><g:if test="${inrequest.price_min?:inrequest?.price_min && inrequest?.price_max?:inrequest?.price_max}">
                  <li class="details-list-item prices">
                    <span class="icons"></span>                  
                    ${inrequest?.price_min} - ${inrequest?.price_max} 
                    <span class="b-rub"><g:rawHtml>${valutaSym}</g:rawHtml></span>
                  </li>
                </g:if>
                </ul>
              </td>              
            </tr>            
            <tr>
              <td colspan="2" valign="top">
                <div id="map_view" class="relative">
                  <div id="map_large_canvas"></div>                      
                </div>              
                <div class="search-container">
                  <div id="search_body" class="body relative">
                    <div id="results">
                      <div id="results_header" class="relative">
                        <span class="count" style="top:0px">
                          <b>${message(code:'common.records.found')}</b><span id="ads_count">${count}</span>
                        </span>
                        <span class="pagination col" style="margin-right:10px">
                          <g:paginate controller="home" action="searchprint" prev="&lt;&lt;" next="&gt;&gt;" params="${hsFilter+[where:inrequest.where,date_start_day:inrequest.date_start_day,date_start_month:inrequest.date_start_month,date_start_year:inrequest.date_start_year,date_end_day:inrequest.date_end_day,date_end_month:inrequest.date_end_month,date_end_year:inrequest.date_end_year]}"
                            max="${inrequest?.max}" total="${count}"/> 
                        </span>  
                      </div>        
                      <div id="tableAJAX" style="clear:both">            
                      <g:if test="${records}">                        
                        <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                        <g:each in="${records}" var="home" status="i">    
                          <g:javascript>
                            xArray.push(${home?.x?:0});
                            yArray.push(${home?.y?:0});
                            nameArray.push("${home?.name?:''}");
                          </g:javascript>
                          <tr><td class="dashed">&nbsp;</td></tr>                          
                          <tr>
                            <td>
                              <div class="hlisting relative">
                                <div class="map_number">${i+1}</div>
                                <div class="item housing thumbnail shadow relative">   
                                  <img width="200" height="140" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                                <g:if test="${home?.curdiscount}">              
                                  <div class="discount-container">                
                                    <div class="discount-price">
                                      <g:if test="${home?.curdiscount==2}">-${home?.hotdiscounttext}</g:if>
                                      <g:elseif test="${home?.curdiscount==1}">-${home?.longdiscounttext}</g:elseif>
                                      <g:else>-${home?.hotdiscounttext ? home.hotdiscounttext : home.longdiscounttext}</g:else>
                                    </div>
                                  </div>
                                </g:if><g:if test="${home?.is_fiesta}">
                                  <div class="fiesta-container" title="Подходит для проведения праздников"></div>
                                </g:if><g:if test="${Client.get(home?.client_id)?.is_reserve}">
                                  <ul class="services-list list">
                                    <li class="services-list-item bron">
                                      <span class="icons active" title="Бронирование через сайт">&nbsp;</span>                
                                    </li>
                                  </ul>
                                </g:if>
                                </div>
                                <div class="photo_item_details list">
                                  <span class="ss_price b-rub" style="padding-left:40px!important">
                                    ${Math.round(home?.price / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
                                  </span>
                                  <span class="ss_period">${message(code:'list.per_night')}</span>
                                </div>
                                <div class="description" style="margin-left:20px">
                                  <h2 class="title" style="margin-bottom:5px">${home?.name}</h2>  
                                  <ul class="details-list">
                                    <li class="details-list-item location clearfix">
                                      <span class="icons"></span>
                                      ${home?.shortaddress}
                                    </li>  
                                  <g:if test="${!inrequest?.hometype_id}">
                                    <li class="details-list-item room_type clearfix">
                                      <span class="icons"></span>
                                    <g:each in="${hometype}" var="item"><g:if test="${item?.id==home?.hometype_id}">
                                      ${item?.name}
                                    </g:if></g:each>
                                    </li>
                                  </g:if>
                                    <li class="details-list-item person_capacity clearfix">
                                      <span class="icons"></span>
                                    <g:each in="${homeperson}" var="item"><g:if test="${item?.id==home?.homeperson_id}">
                                      ${item['name'+context?.lang]}
                                    </g:if></g:each>
                                    </li>
                                    <li class="details-list-item review_count clearfix">
                                      <span class="icons"></span>
                                      ${home?.nref} ${message(code:'common.reviews')}
                                    </li>
                                      <g:if test="${alikeDistances}"><g:if test="{alikeDistances[i]!=0}">${alikeDistances[i]} ${message(code:'common.km_from_choosen')}</g:if>
                                      <g:else>${message(code:'common.same_place')}</g:else></g:if>
                                  </ul>
                                </div>
                              </div>
                            </td>
                          </tr>
                        </g:each> 
                        </table>
                      </g:if>
                      </div>                      
                    </div>            
                  </div>
                </div>
              </td>
            </tr>
      
 </body>
</html>
