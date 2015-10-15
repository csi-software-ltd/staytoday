<html>
  <head>    
    <title>${infotext['title'+context?.lang]?:''}</title>     
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />  
    <meta name="layout" content="main"/>
    <g:javascript>
    var ADDRESS_SEARCH_ZOOM=13;
    var iX=${home?.x?:Region.get(home?.region_id?:0)?.x?:0},
    iY=${home?.y?:Region.get(home?.region_id?:0)?.y?:0},
    iScale=${home?.x?13:Region.get(home?.region_id?:0)?.scale?:0};//toDo ADDRESS_SEARCH_ZOOM
    var map = null; 
    var geocoder = null; 
    var placemark={};
    var tmpPlacemark={};
    var gGroup=null;
    var oPlacemark=null;
    var arr = new Array(${homeguidebooktype.size()});
    var arrInd = new Array();
    <g:each in="${homeguidebooktype}" var="type" status="i">arr[${type.id-1}] = "${type?.icon?:'marker.png'}";arrInd.push(${type.id});</g:each>

    function initialize(){
      <g:if test="${session.attention_message!='' && session.attention_message!=null}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
      </g:if>
    <g:if test="${session.attention_message_once}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message_once}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
    </g:if>      
      textCounter('nameinfr','name_limit',35);
      textCounter('description','description_limit',200);
      Yandex();
      $('infraslistFormSubmit').click();
    }
    
    function textCounter(sId,sLimId,iMax){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iMax){
        symbols = symbols.substring(0,iMax);
        $(sId).value = symbols;
        return false;
      }
      $(sLimId).value = iMax-len;
    } 
    //map>>
    function Yandex(){
      ymaps.ready(function () {
            // Создание экземпляра карты и его привязка к созданному контейнеру
        map = new ymaps.Map("map_canvas",{
              center:[iY/100000,iX/100000],
              zoom: iScale,
              behaviors: ["default","scrollZoom"]
              }
            );
        map.controls.add("smallZoomControl")            
            .add("scaleLine")
            .add("searchControl")            
            .add(new ymaps.control.TypeSelector(['yandex#map','yandex#satellite','yandex#hybrid']))
            .add(new ymaps.control.MapTools({ items: ["default"]}));                    

           // Создание метки и добавление ее на карту                    
            placemark = new ymaps.Placemark([iY/100000, iX/100000],{},
              {
                draggable: false,
                hasBalloon: false,
                iconImageHref:"${resource(dir:'images/infras',file:'house.png')}",
                iconImageSize: [48,36],
                iconImageOffset:[-10,-34]
              }
            );  
            
            map.geoObjects.add(placemark);           

            gGroup  = new ymaps.GeoObjectCollection();            
        }); 
    }
    function addMarker(iX,iY,iType,sName,sDescription,lIds,bDraggable){
      iType = iType || 10
      bDraggable = bDraggable || 0
      lIds = lIds || 0
      sName = sName || ''
      sDescription = sDescription || ''
      
      // Создание метки и добавление ее на карту                    
      tmpPlacemark = new ymaps.Placemark([iY/100000, iX/100000],
        {
           hintContent:sName,
           balloonContentHeader:sName,
           balloonContent:sDescription
        },   
        {
          draggable: (bDraggable)?false:true,                
          hasHint: 1,
          iconImageHref:"${((context?.is_dev)?"/"+context?.appname+"/images/infras/":"/images/infras/")}"+arr[iType-1],
          iconImageSize: [32,37],
          iconImageOffset:[-10,-32]          
        }
      );        
      if(!bDraggable){
        $('infr_y').value = iY; // и добавляем в поля широту
        $('infr_x').value = iX; // и долготу    
      }
      
      tmpPlacemark.ids = lIds;
      gGroup.add(tmpPlacemark);
      
      //map.geoObjects.add(tmpPlacemark);
      map.geoObjects.add(gGroup);
      
      tmpPlacemark.events.add("dragend", function (result) { 
        var coordinates =  this.geometry.getCoordinates() ;
        var y = this.geometry.getCoordinates()[0]*100000;
        var x = this.geometry.getCoordinates()[1]*100000;
        x=Math.round(x);
        y=Math.round(y);
        $('infr_y').value  = y; // и добавляем в поля широту
        $('infr_x').value = x; // и долготу  
      },tmpPlacemark);
    }
	
    function removeMarkers(){
      gGroup.remove(tmpPlacemark);      
      map.geoObjects.remove(tmpPlacemark);      
    }
    function removeAllMarkers(){
      if (gGroup){
        gGroup.each(function (el, i) {
          map.geoObjects.remove(el);       
        });         
        gGroup.removeAll();
      }
    }
	  //map<<
    function changeType(iId){
      removeMarkers();
      addMarker($('infr_x').value,$('infr_y').value,iId);
    }
    function openBalloon(iId) {
      var placemarkOpen = null;
      gGroup.each(function (obj) {        
        if(obj.ids == iId) 
          placemarkOpen = obj;
      });      
      
      gGroup.each(function (el,i) {
        el.balloon.close(function(e){});
      });
      
      map.panTo([placemarkOpen.geometry.getCoordinates()[0],placemarkOpen.geometry.getCoordinates()[1]], {
        flying: true,
        callback: function() {
          //if (YMaps.State.SUCCESS) {
          if(placemarkOpen)
            placemarkOpen.balloon.open();
          //}
        }
      });     
	}
    function showMenu(iId) {
      if (iId){
        $('infras').show();
        $('infrasadderror').hide();
        $('infrasadd').hide();
        $('nameinfr').value='';
        $('description').value='';
        $('infr_id').value='';
        $('guidebooktype').selectedIndex = 9;
        setTimeout("textCounter('nameinfr','name_limit',35)",100);
        setTimeout("textCounter('description','description_limit',200)",100);
        removeMarkers();
        if(oPlacemark){
          gGroup.add(oPlacemark);
          map.geoObjects.add(gGroup);
          oPlacemark = null;
        }
      }	else {
        $('infras').hide();
        $('infrasadd').show();
        addMarker((iX+1000),(iY-1000));
      }
    }
    function processResponse(e){
      if(e.responseJSON.error){
        $('infrasadderror').show();
      }else{
        removeAllMarkers();
        $('infraslistFormSubmit').click();
        showMenu(1);
      }
    }
    function afterDelete(e){
      gGroup.each(function (obj) {        
        if(obj.ids == e.responseJSON.lIds) 
          tmpplacemark = obj;
      });
      removeMarkers();
      $('infraslistFormSubmit').click();
    }
    function edit(iX,iY,iType,lIds,sName,sDescription){
      gGroup.each(function (obj) {        
        if(obj.ids == 0) 
          tmpPlacemark = obj;
      });

      if(tmpPlacemark.ids==0) removeMarkers();
      if(oPlacemark){
        gGroup.add(oPlacemark);        
        map.geoObjects.add(gGroup);
        oPlacemark = null;
      }
      setTimeout("textCounter('nameinfr','name_limit',35)",100);
      setTimeout("textCounter('description','description_limit',200)",100);
      $('nameinfr').value=sName;
      $('description').value=sDescription;
      $('guidebooktype').selectedIndex = arrInd.indexOf(iType);
      $('infr_y').value  = iY;
      $('infr_x').value = iX;
      $('infr_id').value = lIds;

      gGroup.each(function (obj) {        
        if(obj.ids == lIds) 
          tmpPlacemark = obj;
      });
      oPlacemark = tmpPlacemark;
      removeMarkers();
      $('infras').hide();
      $('infrasadd').show();
      addMarker(iX,iY,iType);
     }
    function clickPaginate(event){
      event.stop();
      var link = event.element();
      if(link.href == null){
        return;
      }

      new Ajax.Updater(
        { success: $('ajax_wrap') },
        link.href,
        { evalScripts: true }
      );
    }
    </g:javascript>
    <style type="text/css">
      .description_details { height: auto !important }
      .description_details li { padding: 3px 12px !important }
      .description_details .value { text-align: left !important }
    </style>
  </head>
  <body onload="initialize()">
             <g:render template="/personal_menu" />                    
                      <h2 class="toggle border"><span class="edit_icon infras"></span>${infotext['promotext1'+context?.lang]}</h2>
                    <g:if test="${infotext['itext'+context?.lang]}">
                      <div style="padding:0 20px">
                        <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>
                      </div>
                    </g:if>                    
                      <g:form name="InfraForm" url="${[action:'infrasave', id:home?.id?:0]}" method="POST" base="${context?.mainserverURL_lang}">
                        <ul class="description_details relative" style="width:47%;margin-right:30px;">
                        <g:each in="${homeinfr}" var="item" status="i"> 
                          <g:if test="${i < homeinfr.size()/2}">
                          <li class="clearfix">
                            <label class="property" for="${item.name}">${item['name'+context?.lang]}</label>
                            <select class="value" name="${item.name}" id="${item.name}">
                              <option value=""></option>
                            <g:each in="${homedistance}" var="hd">
                              <option value="${hd.id}" <g:if test="${hd.name==infrdistance[i]}">selected="selected"</g:if>>${hd['name'+context?.lang]}</option>
                            </g:each>
                            </select>
                          </li>
                          </g:if>
                        </g:each>
                        </ul>
                        <ul class="description_details col" style="width:47%">
                        <g:each in="${homeinfr}" var="item" status="i"> 
                          <g:if test="${i >= homeinfr.size()/2}">
                          <li class="clearfix">
                            <label class="property" for="${item.name}">${item['name'+context?.lang]}</label>
                            <select class="value" name="${item.name}" id="${item.name}">
                              <option value=""></option>
                              <g:each in="${homedistance}" var="hd">
                              <option value="${hd.id}" <g:if test="${hd.name==infrdistance[i]}">selected="selected"</g:if>>${hd['name'+context?.lang]}</option>
                              </g:each>
                            </select>
                          </li>
                          </g:if>
                        </g:each>
                        </ul>
                        <div class="padd20">
                          <input type="submit" class="button-glossy orange" value="${message(code:'button.save')}"/>
                        </div>
                      </g:form><br/><br/>
                      <h2 class="toggle border"><span class="edit_icon map"></span>${infotext['promotext2'+context?.lang]}</h2>
                    <g:if test="${infotext['itext2'+context?.lang]}">            
                      <div style="padding:0 20px">
                        <g:rawHtml>${infotext['itext2'+context?.lang]}</g:rawHtml>
                      </div>
                    </g:if>
                      <div class="shadow" id="map_canvas" style="margin:10px 20px;width:690px;height:300px"></div>                                            
                      <input type="hidden" id="x" name="x" value="${home?.x}" />
                      <input type="hidden" id="y" name="y" value="${home?.y}" />
                    
                      <div id="infras" style="padding:10px 20px">
                        <input type="button" class="button-glossy orange float" value="${message(code:'ads.infras.addnew')}" style="margin-right:5px;" onclick="showMenu(0);">
                        <span class="actions col">
                          <span class="action_button orange">
                            <g:link class="icon none" target="_blank" controller="personal" action="adsoverview" id="${home?.id}" base="${context.sequreServerURL}">${Infotext.findByControllerAndAction('personal','adsoverview')['name'+context?.lang]}</g:link>
                          </span>                
                        </span>                          
                      </div>
                      
                      <div id="infrasadd" style="display:none">
                      <g:if test="${infotext['itext3'+context?.lang]}">
                        <div style="padding:0 20px">
                          <g:rawHtml>${infotext['itext3'+context?.lang]}</g:rawHtml>
                        </div>
                      </g:if>                    
                        <g:formRemote id="infrasAddForm" name="infrasAddForm" method="post" url="[ controller: 'personal', action: 'infrasAdd' ]" onSuccess="processResponse(e)">
                          <div class="notice" id="infrasadderror" style="display:none">
                            ${message(code:'error.blank',args:[message(code:'ads.infras.name')])}
                          </div>
                          <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                            <tr>
                              <td>
                                <label for="nameinfr">${message(code:'ads.infras.name')}</label>
                                <input type="text" id="nameinfr" name="nameinfr" value="" size="35" onkeydown="textCounter(this.id,'name_limit',35);" onkeyup="textCounter(this.id,'name_limit',35);"/>
                                <span class="padd10">${message(code:'ads.characters.left')} <input type="text" class="limit" id="name_limit" name="name_limit" readonly /></span>  
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="guidebooktype">${message(code:'ads.infras.category')}</label>
                                <select name="type_id" id="guidebooktype" onChange="changeType(this.value);">
                                <g:each in="${homeguidebooktype}" var="hbt">
                                  <option value="${hbt.id}" <g:if test="${hbt.id==10}">selected="selected"</g:if>>${hbt['name'+context?.lang]}</option>
                                </g:each>
                                </select>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="description">${message(code:'common.home.desc')}</label>
                                <textarea rows="2" cols="50" id="description" name="description" onkeydown="textCounter(this.id,'description_limit',200);" onkeyup="textCounter(this.id,'description_limit',200);"></textarea>
                                <span class="padd10">${message(code:'ads.characters.left')} <input type="text" class="limit" id="description_limit" name="description_limit" readonly /></span>  
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:10px 20px">
                                <input type="submit" class="button-glossy orange" value="${message(code:'button.save')}" style="margin-right:5px;">
                                <input type="button" class="button-glossy grey" value="${message(code:'button.cancel')}" onclick="showMenu(1);">
                              </td>
                            </tr>
                          </table>  
                          <input type="hidden" name="home_id" value="${home?.id}" />
                          <input type="hidden" id="infr_x" name="x" value="${(home.x?:0)+1000}" />
                          <input type="hidden" id="infr_y" name="y" value="${(home.y?:0)-1000}" />
                          <input type="hidden" id="infr_id" name="infr_id" value="" />
                        </g:formRemote>
                      </div>
                      <div id="infraslist" class="search-container paddtop" style="clear:both">
                        <div id="search_body">
                          <div id="list"></div>
                        </div>
                      </div>
                      <g:formRemote id="infraslistForm" name="infraslistForm" method="post" url="[ controller: 'personal', action: 'infraslist' ]" update="[success:'list']">
                        <input type="hidden" name="home_id" value="${home.id}" />
                        <input type="submit" class="button-glossy orange" id="infraslistFormSubmit" style="display: none" value="Показать">
                      </g:formRemote>
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>                    
                  </div>
                </div>
              </td>
            </tr>
  </body>      
  </body>
</html>
