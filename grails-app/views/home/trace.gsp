<html>
  <head>
    <title>${message(code:'ads.howtoget')} ${home?.name?:''}</title>      
    <meta name="description" content="${home?.description?:''}" />       
    <meta name="layout" content="main"/>
    <g:javascript>   
    var iX=${home?.x?:Region.get(home?.region_id?:0)?.x?:0}, iY=${home?.y?:Region.get(home?.region_id?:0)?.y?:0},
        iScale=${home?.x?13:Region.get(home?.region_id?:0)?.scale?:0}, ADDRESS_SEARCH_ZOOM=13,
        map=null, geocoder=null, placemark={}, isTrace=0, traceX=0, traceY=0, 
        traceAddress='', isTraceRout=0, isTraceMarkerA=0, isTraceMarkerB=0,route=null,gGroup=null;	
    function initialize(){
      Yandex();      
    }
    function setFirmTrace(){		  
      traceX=${home?.x?:0};
      traceY=${home?.y?:0};
      $('traceB').value = (traceY/100000).toPrecision(8)+', '+(traceX/100000).toPrecision(8);
      $('pointB').value = $('traceB').value;      
      $('traceB_clear').addClassName(' visible');      
      traceAddress='<g:rawHtml>${home?.shortaddress?:''}</g:rawHtml>';
      $('pointA').value = $('traceA').value;
      map.setCenter([traceY/100000,traceX/100000], ADDRESS_SEARCH_ZOOM);			   
      addMarker(traceX,traceY,traceAddress,1);		         
      showTrace(1);		   
    }
    function showTrace(iVar){		 
      $('traceA').onkeypress=onKeyTrace;
      $('traceB').onkeypress=onKeyTrace;          		  
      isTraceRout=0;
      isTraceMarkerA=0;
      isTraceMarkerB=0;
      if(iVar)
        isTraceMarkerB=1;		  
    }
    function onKeyTrace(e){
      var iKeycode;
      if (window.event) iKeycode = window.event.keyCode;
      else if (e) iKeycode = e.which;
      else return true;    
      if (iKeycode == 13){      
        $('trace_button').click();
      } else {
        if($('traceA').value!='')
          $('traceA_clear').addClassName('visible');
        else
          $('traceA_clear').removeClassName('visible');
        if($('traceB').value!='')
          $('traceB_clear').addClassName('visible');
        else
          $('traceB_clear').removeClassName('visible');
      }      
    }	
    function addMarker(x,y,sBooble,iNumber) {
      var sHref='';
      switch(iNumber){
        case 0:  sHref="${resource(dir:'images',file:'markerA.png')}"; break;		  
        case 1:  sHref="${resource(dir:'images',file:'markerB.png')}"; break;	            
        default: sHref="${resource(dir:'images',file:'marker.png')}";
      }  
      var placemark = new ymaps.Placemark([y/100000, x/100000], {
        hintContent:sBooble,           
        balloonContent:sBooble
      }, {
        draggable: true,                
        hasHint: false,
        iconImageHref:sHref,
        iconImageSize: [20,34],
        iconImageOffset:[-10,-34]          
      });
      placemark.events.add("dragend", function(result) { 
        var coordinates =  this.geometry.getCoordinates();        
        if(!iNumber){
			    $('traceA').value=coordinates[0].toPrecision(8)+', '+coordinates[1].toPrecision(8);
          $('pointA').value=$('traceA').value;
          $('traceA_clear').addClassName('visible');
        } else {
  			  $('traceB').value=coordinates[0].toPrecision(8)+', '+coordinates[1].toPrecision(8);
          $('pointB').value=$('traceB').value;
          $('traceB_clear').addClassName('visible');
        } 
      },placemark);	  
		  if(!iNumber)
		    isTraceMarkerA=1;
		  else
        isTraceMarkerB=1;        
      gGroup.add(placemark);
      map.geoObjects.add(gGroup);           	      
    } 
    function Yandex(){
      ymaps.ready(function() {       
        if(!map)          
          map = new ymaps.Map("big_map",{center:[iY/100000,iX/100000],zoom:iScale,behaviors:["default","scrollZoom"]});       
        map.controls.add("zoomControl")            
          .add("scaleLine")       
          .add(new ymaps.control.MapTools({ items: ["default"]}))
          .add(new ymaps.control.TypeSelector(['yandex#map','yandex#satellite','yandex#hybrid']))          
          .add(new ymaps.control.MiniMap({ type:'yandex#map' },{ size: [90,90] }))
          .add(new ymaps.control.TrafficControl({shown: false}), {top: 7, left: 100});          

        map.events.add("click", function(e) {
          if(!isTraceRout){
            var GPoint=e.get("coordPosition");
            if(!isTraceMarkerA){
              var sName=GPoint[0].toPrecision(8)+', '+GPoint[1].toPrecision(8);
              addMarker(GPoint[1]*100000,GPoint[0]*100000,sName,0);
              $("traceA").value=sName;
              $('pointA').value=sName;
              $('traceA_clear').addClassName('visible');
            } else if(!isTraceMarkerB){
              var sName=GPoint[0].toPrecision(8)+', '+GPoint[1].toPrecision(8);
              addMarker(GPoint[1]*100000,GPoint[0]*100000,sName,1); 
              $("traceB").value=sName;
              $('pointB').value=sName;              
              $('traceB_clear').addClassName('visible');
            }
          }
        });  
        gGroup  = new ymaps.GeoObjectCollection();

        setFirmTrace();        
      });
    }      
    $('trace_button').observe('click', respondToClick);
    function respondToClick(event) {
      trace($('traceA').value,$('traceB').value);
      isTraceRout=1;
    }        
    function trace(sFrom,sTo){    
      removeAllMarkers();
      ymaps.route([sFrom, sTo], {     
        mapStateAutoApply: true // автоматически позиционировать карту
      }).then(function (router) {
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
          $('traceA').value=point[0].toPrecision(8)+', '+point[1].toPrecision(8);
          $('pointA').value=$('traceA').value;
          $('traceA_clear').addClassName('visible');
          $('trace_button').click(); 
        },points.get(0));            
        points.get(1).events.add("dragend", function (result) {            
          var point =  this.geometry.getCoordinates();                
          $('traceB').value=point[0].toPrecision(8)+', '+point[1].toPrecision(8);
          $('pointB').value=$('traceB').value;
          $('traceB_clear').addClassName('visible');              
          $('trace_button').click(); 
        },points.get(1));
            
        var distance = router.getLength();
        if(distance > 999)
          distance = (distance/1000).toFixed(1)+' '+"${message(code:'trace.km')}";
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
        var way = router.getPaths().get(0), segments = way.getSegments();
        var itineraryList = [""];
        var action = [];
        action["back"] = "${message(code:'trace.back')}";
        action["left"] = "${message(code:'trace.left')}";
        action["right"] = "${message(code:'trace.right')}";
        action["none"] = "${message(code:'trace.none')}";			   
        action["hard right"] = "${message(code:'trace.hard_right')}";
        action["hard left"] = "${message(code:'trace.hard_left')}";
        action["slight right"] = "${message(code:'trace.slight_right')}";
        action["slight left"] = "${message(code:'trace.slight_left')}";        
        action["exit right"] = "${message(code:'trace.exit_right')}";
        action["exit left"] = "${message(code:'trace.exit_left')}";
        action["exit none"] = "${message(code:'trace.exit_none')}";
        action["enter roundabout"] = "${message(code:'trace.enter_roundabout')}";        
        action["leave roundabout 1"] = "${message(code:'trace.leave_roundabout_1')}";
        action["leave roundabout 2"] = "${message(code:'trace.leave_roundabout_2')}";
        action["leave roundabout 3"] = "${message(code:'trace.leave_roundabout_3')}";        
        action["merge"] = "${message(code:'trace.merge')}";
        action["board ferry"] = "${message(code:'trace.board_ferry')}";
        
        for (var i=0; i  < segments.length; i++) {
          var segment = segments[i];  
          var s_distance = segment.getLength();
          var s_route = '';          
          if(s_distance > 999)
            s_distance = (s_distance/1000).toFixed(1)+' '+"${message(code:'trace.km')}";
          else
            s_distance = s_distance.toFixed(0)+' м';
          if(segment.getStreet()==''){
            s_route = action[segment.getAction()];            
          } else {
            s_route = action[segment.getAction()]+', '+segment.getStreet();            
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
      }, function (error) {
        if(error.message == "can't construct a route")
          alert("${message(code:'trace.error_route')}\r\n${message(code:'trace.try_change')}");
        else
          alert("${message(code:'trace.error')}: " + error.message);
      });
      return false;                
    }     
    function viewCell(sObj1,iNum,sObj2){
      var tabs = $(sObj1).getElementsByTagName('li');           
      var divs = $(sObj2).getElementsByTagName('div');            
      var layers = new Array();      
      for (var i=0; i < divs.length; i++)
        if(divs[i].getAttribute("rel")=='layer')
          layers.push(i);        
      for (var i=0; i < tabs.length; i++){
        tabs[i].className = (i==iNum) ? 'selected' : '';
        divs[layers[i]].style.display = (i==iNum)? 'block' : 'none';
        if(iNum==0){
          $('print').show();$('filters').show();
        } else {
          $('print').hide();$('filters').hide();
        }
      }
    } 
    function removeAllMarkers(){
      if (gGroup){
        gGroup.each(function (el, i) {
          map.geoObjects.remove(el);       
        });         
        gGroup.removeAll();
      }
    }    
    </g:javascript>    
  </head>  
  <body onload="initialize()">
            <tr style="height:110px">
              <td width="250" class="search">
                <g:if test="${isLoginDenied}"><a class="button" href="javascript:void(0)" rel="nofollow" onclick="showLoginForm()">${message(code:'common.deliverhome')}</a></g:if>
                <g:else><g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link></g:else>                
              </td>
              <td colspan="3" class="rent ss">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <ul id="filters" class="collapsable_filters" style="width:250px">			    
                  <li class="search_filter" id="trace_container" style="width:250px;height:615px;overflow-y:auto">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('trace_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('trace_container');">${message(code:'trace.make_route')}</a>        
                    <div class="search_filter_content">
                      <table class="search_point" width="210" cellpadding="0" cellspacing="0" border="0">
                        <tr>
                          <td class="search_point-name" style="width:30px">
                            <span class="search_name start">&#9398;</span>
                          </td>
                          <td class="search_point-input">
                            <span class="form-input">
                              <span class="form-input_box">
                                <input type="text" id="traceA" class="form-input_input" autocomplete="off"/>                    
                                <span class="form-input_clear" id="traceA_clear" onclick="$('traceA').value='';this.removeClassName(' visible')"></span>
                              </span>
                            </span>
                          </td>                            
                        </tr>
                        <tr>
                          <td class="search_point-name" style="width:30px">                    
                            <span class="search_name finish">&#9399;</span>
                          </td>
                          <td class="search_point-input">
                            <span class="form-input">
                              <span class="form-input_box">                   
                                <input type="text" id="traceB" class="form-input_input" autocomplete="off"/>
                                <span class="form-input_clear" id="traceB_clear" onclick="$('traceB').value='';this.removeClassName(' visible')"></span>
                              </span>
                            </span>
                          </td>
                        </tr>
                        <tr>
                          <td colspan="2" align="right">
                            <input type="button" id="trace_button" class="button-glossy orange mini" onclick="$('traces_desc').hide();$('pointA').value=$('traceA').value;"  value="${message(code:'trace.route')}"/>
                          </td>
                        </tr>
                      </table>
                      <div id="traces_desc" class="paddtop">
                        <b>${infotext['promotext2'+context?.lang]?:''}</b>                        
                        <p><g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml></p>                        
                      </div>
                      <div id="traces" class="paddtop" style="margin-left:-5px"></div>                                                          
                    </div>
                  </li>
                </ul>
              </td>
              <td colspan="3" valign="top" class="body">
                <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>              
                <div class="content" style="min-height:510px">                                  
                  <ul id="tabs" class="subnav first">                         
                    <li class="selected" id="map" onclick="viewCell('tabs',0,'layers');YMaps.load(Yandex);">                    
                      ${message(code:'ads.howtoget.personal')}
                    </li>
                    <li onclick="viewCell('tabs',1,'layers');" style="display:${(home?.reachpublic)?'block':'none'}">                    
                      ${message(code:'ads.howtoget.public')}                  
                    </li>                  	
                    <li id="print">
                      <span class="actions">
                        <a class="icon print" href="javascript:void(0)" rel="nofollow" onclick="$('traceprintForm').submit();"></a>
                        <g:form name="traceprintForm" method="post" url="[controller:'home',action:'traceprint',id: home?.id]" target="_blank" base="${context?.mainserverURL_lang}">
                          <input type="hidden" id="pointA" name="pointA" value=""/> 
                          <input type="hidden" id="pointB" name="pointB" value=""/>                                           
                        </g:form>              
                      </span>
                    </li>
                  </ul>
                  <div class="bg_shadow" style="height:20px"></div>
                  <div id="layers" class="dashboard-content" style="margin-top:-20px">
                    <div rel="layer">                                    
                      <div class="toppadd padd20" style="margin-right:10px">
                        <p><g:rawHtml>${home?.reachpersonal?.replace('"',"'")?.replace('\\'," ")?.replace("\n","<br/>")?.replace("\r","<br/>")}</g:rawHtml></p>
                      </div>
                      <div class="slideshow" valign="top">                    
                        <div id="big_map" style="height:512px;width:710px;border:1px solid #fff"></div>                    
                        <input type="hidden" id="x" name="x" value="${home?.x}" />
                        <input type="hidden" id="y" name="y" value="${home?.y}" />
                        <input type="hidden" id="geocodeaddress" value="${home?.address}" />
                      </div>
                    </div>
                    <div rel="layer" style="display:none">
                    <g:if test="${home?.reachpublic}">                      
                      <div class="padd20">
                        <p><g:rawHtml>${home?.reachpublic?.replace('"',"'")?.replace('/s'," ")?.replace('\n',"<br/>")?.replace('\r'," ")}</g:rawHtml></p>      
                      </div>
                    </g:if>            
                    </div>
                    <div rel="layer" style="display:none">                    
                    </div>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
