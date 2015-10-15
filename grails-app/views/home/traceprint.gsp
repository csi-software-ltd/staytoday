<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}, ${message(code:'common.guests')} - ${inrequest.homeperson_id?:1}, ${message(code:'common.home_type')} - <g:if test="${inrequest?.hometype_id}"><g:each in="${hometype}" var="item"><g:if test="${item?.id==inrequest?.hometype_id}">${item['name'+context?.lang]}</g:if></g:each></g:if><g:else>${message(code:'common.any')}</g:else><g:if test="${inrequest?.date_start && inrequest?.date_end}">, ${message(code:'detail.check_in')} - ${String.format('%td.%<tm.%<tY',inrequest?.date_start)}-${String.format('%td.%<tm.%<tY',inrequest?.date_end)}</g:if></title>      
    <meta name="keywords" content="${infotext.keywords?:''}" />
    <meta name="description" content="${infotext.description?:''}" />       
    <meta name="layout" content="print" />       
    <g:javascript>   
      var iX=${home?.x?:Region.get(home?.region_id?:0)?.x?:0}, iY=${home?.y?:Region.get(home?.region_id?:0)?.y?:0},
          iScale=${home?.x?13:Region.get(home?.region_id?:0)?.scale?:0}, ADDRESS_SEARCH_ZOOM=13,
          map=null, geocoder=null, placemark={}, isTrace=0, isTraceRout=0,
          traceX_a=0, traceY_a=0, traceAddress_a='', traceX_b=0, traceY_b=0, traceAddress_b='',route=null;
    function initialize(){     
      $("map_view").show();
      $("map_large_canvas").setStyle({height: '380px', width: '980px'});
      $("search_body").setStyle({width: '980px'});
      $("results").setStyle({width: '980px'});
      Yandex();
      setTimeout('window.print()', 1000);
    }
    function setMarkers(){		  
			traceX_b= ${home?.x?:0};
      traceY_b = ${home?.y?:0};      
      traceAddress_b='<g:rawHtml>${home?.shortaddress?:''}</g:rawHtml>';      
    }	
    function Yandex(){
      ymaps.ready(function() {       
        if(!map)          
          map = new ymaps.Map("map_large_canvas",{center:[iY/100000,iX/100000],zoom:iScale,behaviors:["default","scrollZoom"]});       
        map.controls.add("zoomControl")            
          .add("scaleLine")       
          .add(new ymaps.control.MapTools({ items: ["default"]}))
          .add(new ymaps.control.TypeSelector(['yandex#map','yandex#satellite','yandex#hybrid']))          
          .add(new ymaps.control.MiniMap({ type:'yandex#map' },{ size: [90,90] }))
          .add(new ymaps.control.TrafficControl({shown: false}), {top: 7, left: 100});
          
        function trace(sFrom,sTo){
          //removeAllMarkers();
          sFrom=sFrom.split(',');
          sTo=sTo.split(',');
          ymaps.route([
            // Список точек, которые необходимо посетить
            [sFrom[1],sFrom[0]], [sTo[1],sTo[0]]], {
            // Опции маршрутизатора
            mapStateAutoApply: true // автоматически позиционировать карту
          }).then(function(router) {
            if(route)
			        map.geoObjects.remove(route);
				    route = router;
            map.geoObjects.add(route);
            
            var points = route.getWayPoints();
            points.get(0).options.set('preset', {
                    iconImageHref: "${resource(dir:'images',file:'markerA.png')}", // картинка иконки
                    iconImageSize: [20, 34], // размеры картинки
                    iconImageOffset: [-10, -34] // смещение картинки
            });
            points.get(0).properties.set('iconContent', '');
            points.get(0).options.set('draggable', true);
            points.get(1).options.set('preset', {
                    iconImageHref: "${resource(dir:'images',file:'markerB.png')}", // картинка иконки
                    iconImageSize: [20, 34], // размеры картинки
                    iconImageOffset: [-10, -34] // смещение картинки
            });
            points.get(1).properties.set('iconContent', '');
            points.get(1).options.set('draggable', true);
            
            points.get(0).events.add("dragend", function (result) { 
              //var point=route.getWayPoints()[0];  
              var point =  this.geometry.getCoordinates();                
              $('traceA').value=point[1].toPrecision(8)+' , '+point[0].toPrecision(8);
              $('pointA').value=$('traceA').value;
              $('traceA_clear').addClassName(' visible');
              $('trace_button').click(); 
            },points.get(0));
            
            points.get(1).events.add("dragend", function (result) {            
              var point =  this.geometry.getCoordinates();                
              $('traceB').value=point[1].toPrecision(8)+' , '+point[0].toPrecision(8);
              $('pointB').value=$('traceB').value;
              $('traceB_clear').addClassName(' visible');              
              $('trace_button').click(); 
            },points.get(1));
            
            var distance = router.getLength();
            if(distance > 999)
              distance = (distance/1000).toFixed(1)+' ${message(code:'trace.km')}';
            else 
              distance = distance.toFixed(0)+' м';              
            var duration = router.getJamsTime()/60;           
            var days = Math.floor(duration/60/24); 
            var hours = Math.floor((duration - (days*24*60))/60);
            var minuts = Math.floor(duration - (days*24*60) - (hours*60));                                   
            if(duration >= 1440){
              duration = days+' ${message(code:'trace.days')} '+((hours>=1)?hours+' ${message(code:'trace.hours')} ':'')+((minuts<=59)?minuts+' ${message(code:'trace.minutes')}':'');              
            } else if((duration >=60) && (duration < 1440)){
              duration = hours+' ${message(code:'trace.hours')} '+((minuts<=59)?minuts+' ${message(code:'trace.minutes')}':'');              
            } else if(duration < 60){
              duration = minuts+' ${message(code:'trace.minutes')}';              
            }
            var way = router.getPaths().get(0),segments = way.getSegments();
            var itineraryList = [""];
            var action = [];
            action["back"] = "${message(code:'trace.back')}";
            action["left"] = "${message(code:'trace.left')}";
            action["right"] = "${message(code:'trace.right')}";
            action["none"] = "";			   
            action["hard right"] = "${message(code:'trace.hard_right')}";
            action["hard left"] = "${message(code:'trace.hard_left')}";
            action["slight right"] = "${message(code:'trace.slight_right')}";
            action["slight left"] = "${message(code:'trace.slight_left')}";        
            action["enter roundabout"] = "${message(code:'trace.enter_roundabout')}";        
            action["leave roundabout 1"] = "${message(code:'trace.leave_roundabout')}";
            action["leave roundabout 2"] = "${message(code:'trace.leave_roundabout')}";
            action["leave roundabout 3"] = "${message(code:'trace.leave_roundabout')}";        
            action["merge"] = "${message(code:'trace.merge')}";
            action["board ferry"] = "${message(code:'trace.board_ferry')}";
            
            for (var i=0; i  < segments.length; i++) {
              var segment = segments[i];  
              var s_distance = segment.getLength();
              var s_route = '';              
              if(s_distance > 999)
                s_distance = (s_distance/1000).toFixed(1)+' ${message(code:'trace.km')}';
              else
                s_distance = s_distance.toFixed(0)+' м';
              if(segment.getStreet()==''){
                s_route = (segment.getAction()=='none')?'${message(code:"trace.straight")}':action[segment.getAction()];
              } else {
                s_route = action[segment.getAction()]+((segment.getAction()!='none')?', ':'')+segment.getStreet();
              }              
              itineraryList.push('<li><i class="number">'+(i+1)+'.</i><span class="property">'+s_route+'</span><span class="value">'+s_distance+'</span></li>');
            }               
            var sOut='<b>${message(code:'trace.driving_directions')}</b>'+
            '<ul class="trace">'+
            '  <li><span class="property">${message(code:'trace.total_length')}:</span><span class="value"><b>'+distance+'</b></span></li>'+
            '  <li><span class="property">${message(code:'trace.average_travel_time')}:</span><span class="value"><b>'+duration+'</b></span></li>'+
            '</ul><br/>'+
            '<table width="100%" cellpadding="0" cellspacing="0" border="0">'+
            '  <tr>'+
            '    <td class="search_point-name" style="padding-left:0px;width:30px;"><span class="search_name start">&#9398;</span></td>'+
            '    <td class="search_point-input start" style="padding-right:0px">'+sFrom+'</td>'+
            '  </tr>'+
            '</table>'+
            '<ul class="trace" style="margin:0 0 5px 27px">';
            sOut+=itineraryList.join("");
            sOut+='</ul>'+
            '<table width="100%" cellpadding="0" cellspacing="0" border="0">'+
            '  <tr>'+
            '    <td class="search_point-name" style="padding-left:0px;width:30px"><span class="search_name finish">&#9399;</span></td>'+
            '    <td class="search_point-input finish" style="padding-right:0px">'+sTo+'</td>'+
            '  </tr>'+
            '</table>';            
            $('traces').update(sOut);            
          }, function(error) {
            if(error.message == "can't construct a route")
              alert("${message(code:'trace.error_route')}\r\n${message(code:'trace.try_change')}");
            else
              alert("${message(code:'trace.error')}: " + error.message);
          });
          return false;                
        }          					        
        trace('${inrequest?.pointA}','${inrequest?.pointB}'); 	   
      });                
    }     
    </g:javascript>
  </head>
  <body onload="initialize()">
            <tr>
              <td width="980" colspan="2" class="rent" style="background:#fff;padding-bottom:10px">
                <h1 style="color:#3F5765">${home?.name}</h1>
                <ul class="details-list padd20">
                  <li class="details-list-item location">
                    <span class="icons"></span>
                    ${home?.shortaddress}
                  </li>
                  <li class="details-list-item">
                    <span><g:join in="${metro.collect{it.name}}" delimiter=", "/></span>
                  </li>                  
                </ul>
              </td>              
            </tr>            
            <tr>
              <td colspan="2" valign="top" bgcolor="#fff">
                <div id="map_view" class="relative">
                  <input type="hidden" id="x" name="x" value="${home?.x}" />
                  <input type="hidden" id="y" name="y" value="${home?.y}" />
                  <input type="hidden" id="geocodeaddress" value="${home?.address}" />                  
                  <div id="map_large_canvas"></div>                  
                </div>              
                <div class="search-container">
                  <div id="search_body" class="body relative">
                    <div id="results"  style="padding-bottom:20px">
                      <div id="traces" class="paddtop padd20" style="width:60%"></div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
