<html>
  <head>    
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />         
    <meta name="layout" content="main"/>
    <g:javascript>
    var ADDRESS_SEARCH_ZOOM=13,
        iX=${inrequest?.x?:Region.get(inrequest?.region_id?:0)?.x?:0},
        iY=${inrequest?.y?:Region.get(inrequest?.region_id?:0)?.y?:0},
        iScale=${inrequest?.x?13:Region.get(inrequest?.region_id?:0)?.scale?:0},
        map=null, geocoder=null, flag_marker_move=false, placemark={}, tmp=[],
        iLim = ${textlimit};        
    function textLimit(sId, lim){
      lim = lim || iLim;
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > lim){
        symbols = symbols.substring(0,lim);
        $(sId).value = symbols;
        return false;
      }
    }    
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
      Yandex();	
      new Ajax.Autocompleter1("street_auto","region_id","street_autocomplete",
        "${resource(dir:'home',file:'street_autocomplete')}",{});
      new Ajax.Autocompleter1("city_auto","region_id","city_autocomplete",
        "${resource(dir:'home',file:'city_autocomplete')}",{});
      new Ajax.Autocompleter1("district_auto","region_id","district_autocomplete",
        "${resource(dir:'home',file:'district_autocomplete')}",{});
      <g:if test="${!inrequest?.x ||!inrequest?.y}">
        showAddress($('geocodeaddress').value);
      </g:if>     
    }    
    function updateRegion(lCountryId){
      <g:remoteFunction controller='home' action='region' update='region_id' params="\'countryId=\'+lCountryId+'&lang=${context?.lang}'" />
    }	
    function sendForm(){ 
      $("submit_form_button").click();          			 	  
    }    
		function Yandex(){
	    ymaps.ready(function() {            
        map = new ymaps.Map("map_canvas",{ center:[iY/100000,iX/100000], zoom: iScale, behaviors: ["default","scrollZoom"] });
        map.controls.add("smallZoomControl")            
          .add("scaleLine")                       
          .add(new ymaps.control.TypeSelector(['yandex#map','yandex#satellite','yandex#hybrid']));    
        placemark = new ymaps.Placemark([iY/100000, iX/100000],{}, {
          draggable: true,
          hasBalloon: false,
          iconImageHref:"${resource(dir:'images',file:'marker_select.png')}",
          iconImageSize: [20,34],
          iconImageOffset:[-10,-34]
        });              
        map.geoObjects.add(placemark);                           
        placemark.events.add("dragend", function(result) { 
          var coordinates =  this.geometry.getCoordinates() ;
          var y = this.geometry.getCoordinates()[0]*100000;;
          var x = this.geometry.getCoordinates()[1]*100000;
          x=Math.round(x);
          y=Math.round(y);
          $('y').value  = y; // и добавляем в поля широту
          $('x').value = x; // и долготу  
        },placemark);
        map.events.add("click", function(e) {
          var clickPoint=e.get("coordPosition");
          map.geoObjects.remove(placemark);
          map.setCenter(clickPoint, map.getZoom()); // центром карты делаем эту точку
          placemark = new ymaps.Placemark(clickPoint,{},{
            draggable: true,
            hasBalloon: false,
            iconImageHref:"${resource(dir:'images',file:'marker_select.png')}",
            iconImageSize: [20,34],
            iconImageOffset:[-10,-34]
          });
          map.geoObjects.add(placemark);
          var x=clickPoint[1]*100000;
          var y=clickPoint[0]*100000;
          x=Math.round(x);
          y=Math.round(y);
          $('y').value  = y; // и добавляем в поля широту
          $('x').value = x; // и долготу                                   
          placemark.events.add("dragend", function(result) { 
            var coordinates =  this.geometry.getCoordinates() ;
            var y = this.geometry.getCoordinates()[0]*100000;
            var x = this.geometry.getCoordinates()[1]*100000;
            x=Math.round(x);
            y=Math.round(y);
            $('y').value  = y; // и добавляем в поля широту
            $('x').value = x; // и долготу  
          },placemark);
        });                    
      }); 
    }            
	  function showAddress(fulladdress) {      
      var geocoder2 = ymaps.geocode(fulladdress, {results: 1, boundedBy: map.getBounds()});
      // Результат поиска передается в callback-функцию
      geocoder2.then(
        function (res) {
          map.geoObjects.remove(placemark);
          if (res.geoObjects.getLength()) {
          // point - первый элемент коллекции найденных объектов
            var point = res.geoObjects.get(0);
            point.options.set({draggable: true,
              iconImageHref:"${resource(dir:'images',file:'marker_select.png')}",
              iconImageSize: [20,34],
              iconImageOffset:[-10,-34]}
            );
            // Добавление полученного элемента на карту
            map.geoObjects.add(point);
            // Центрирование карты на добавленном объекте
            map.setZoom(ADDRESS_SEARCH_ZOOM);
            map.panTo(point.geometry.getCoordinates());
            var x = point.geometry.getCoordinates()[1]*100000;
            var y = point.geometry.getCoordinates()[0]*100000;
            x=Math.round(x);
            y=Math.round(y);
            $('y').value  = y;
            $('x').value = x;           
            placemark=point;
            placemark.events.add("dragend", function(result) { 
              var coordinates =  this.geometry.getCoordinates() ;
              var y = this.geometry.getCoordinates()[0]*100000;;
              var x = this.geometry.getCoordinates()[1]*100000;
              x=Math.round(x);
              y=Math.round(y);
              $('y').value  = y; // и добавляем в поля широту
              $('x').value = x; // и долготу  
            },placemark);
            $('geocodererror').hide();
          }else
            $('geocodererror').show();
        },
        // Обработка ошибки
        function (error) {
          $('geocodererror').show();
        }
      );
    }    	  
    function collectAddress(){	    
      var region = $('region_name').value, city = '';		
      if(region!=$('city_auto').value)
        city = $('city_auto').value;	       
      $('geocodeaddress').value = $('country_name').value+' '+region +' '+city+' '+$('street_auto').value+' '+$('homenumber').value;
    }	
    function getMetro(iRegId){	 
      var lId=${home?.id?:0};
      <g:remoteFunction controller='personal' action='get_metro' update='metro_results' params="'home_id='+lId+'&region_id='+iRegId" />
      $('metro_name').value='';
    }  
    function addMetroChecked(name) {
      var checkBoxes=document.getElementsByName(name);   
      var iChecked=0;
      for(i=0;i<checkBoxes.length;i++){	    
        if($('checkbox_'+i).checked){		
          addMetroInTmp($('id_'+i).value);
          iChecked++;
        }
      } 
      if(iChecked>5){
        tmp=[];
        alert("${message(code:'ads.error.metro')}");
      }else{
        addMetro(tmp);
      }	  	  
    }	  
    function addMetroInTmp(rubId){
      var flag=true, arrTmp=new Array();    
      if(tmp.length>0){
        for(var i=0;i<tmp.length;i++){
          arrTmp.push(tmp[i]);
          if(tmp[i]==rubId){
            flag=false;
            arrTmp.pop(); 
          }
        }
      }
      if(flag)
        arrTmp.push(rubId);  
      tmp= [ ];
      for(var i=0;i<arrTmp.length;i++){
        tmp.push(arrTmp[i]);
      }	  
    }  
    function addMetro(metroIds){
      var lId=${home?.id?:0};
      <g:remoteFunction controller='personal' action='addmetro' update='metro' params="'metros='+metroIds+'&home_id='+lId" />
      tmp= [ ];
    }  
    function resetCheckboxes(name){
      var checkBoxes=document.getElementsByName(name);
      for(i=0;i<checkBoxes.length;i++){	    
        if($('checkbox_'+i).checked)
          $('checkbox_'+i).checked=false;          		
      } 	  
    }
    function processResponse(e){
      var sErrorMsg='';
      ['pindex','city_auto'].forEach(function(ids){
        if($(ids))
          $(ids).removeClassName('red');
      });
      if(e.responseJSON.save_error.length){        
        e.responseJSON.save_error.forEach(function(err){
            switch (err) {             
              case 1: sErrorMsg+='<li>${message(code:'error.incorrect',args:[message(code:'ads.postindex')])}</li>'; $("pindex").addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:'error.blank',args:[message(code:'ads.postindex')])}</li>'; $("pindex").addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:'ads.error.map')}</li>'; break;
              case 4: sErrorMsg+='<li>${message(code:'error.blank',args:[message(code:'ads.city')])}</li>'; $("city_auto").addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:'ads.error.handbooks')}</li>';  break;              
            } 
          });
        if(sErrorMsg.length){
          $('map_fail').innerHTML=sErrorMsg;
          $('map_fail').up('div').show();
        }  
      }else{
        $('map_fail').innerHTML='';
        $('map_fail').up('div').hide();
      }
    }
    </g:javascript>
  </head>
  <body onload="initialize()">
              <g:render template="/personal_menu" />                                            
                      <div class="notice" style="margin-top:10px; display:none">
                        <ul id="map_fail">                        
                        </ul>
                      </div>                    
                      <div class="form"> 
                        <g:formRemote name="mapForm" url="${[controller:'personal',action:'savemap',id:inrequest?.id?:0]}" method="POST" onSuccess="processResponse(e)">
                          <h2 class="toggle border"><span class="edit_icon detail"></span>${message(code:'ads.address').capitalize()}</h2>
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">        
                            <tr>
                              <td>
                                <label for="country_id">${message(code:'ads.country')}</label>		
                                <select id="country_id" name="country_id" onchange="updateRegion(this.value)">
                                <g:each in="${country}" var="item">            
                                  <option <g:if test="${item?.id==inrequest?.country_id}">selected="selected"</g:if> value="${item?.id}" onclick="$('country_name').value='${item.name}';">
                                    ${item['name'+context?.lang]}
                                  </option>
                                </g:each>			  
                                </select>
                                <input type="hidden" id="country_name" value="${Country.get(inrequest?.country_id)?.name?:''}"/>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="region_id">${message(code:'ads.region')}</label>   		  
                                <div id="region_result">              
                                  <select id="region_id" name="region_id" onchange="$('metro_div').hide();getMetro(this.value);">
                                  <g:each in="${region}" var="item">           
                                    <option value="${item?.id}" <g:if test="${item?.id==inrequest?.region_id}">selected="selected"</g:if>  onclick="$('region_name').value='${item.name}';<g:if test="${item?.is_metro}">$('metro_tr').show();</g:if><g:else>$('metro_tr').hide();</g:else>;">
                                      ${item['name'+context?.lang]}
                                    </option>
                                  </g:each>              
                                  </select>
                                  <input type="hidden" id="region_name" value="${Region.get(inrequest?.region_id)?.name?:''}"/>
                                </div>
                              </td>		
                            </tr>      
                            <tr>          
                              <td>
                                <label for="city">${message(code:'ads.city')}</label>
                                <input name="city" id="city_auto" type="text" maxlength="${stringlimit}" value="${hinrequest?.city}"/>
                                <div id="city_autocomplete" class="autocomplete" style="display:none"></div>
                                <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.city.alt')}">
                                  <img alt="${message(code:'ads.city.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
                                </a>
                              </td>			         
                            </tr>
                            <tr>
                              <td>
                                <label for="district">${message(code:'ads.district')}</label>					 
                                <input name="district" id="district_auto" type="text" maxlength="${stringlimit}" value="${hinrequest?.district}"/>
                                <div id="district_autocomplete" class="autocomplete" style="display:none"></div>
                                <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.district.alt')}">
                                  <img alt="${message(code:'ads.district.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
                                </a>
                              </td>		
                            </tr>
                            <tr>
                              <td>
                                <label for="pindex">${message(code:'ads.postindex')}</label>		
                                <input type="text" class="price" id="pindex" name="pindex" maxlength="${stringlimit}" value="${inrequest?.pindex}"/>		  	
                              </td>           
                            </tr>
                            <tr id="metro_tr" style="<g:if test="${!curregion?.is_metro}">display:none</g:if>">
                              <td>
                                <label for="metro_name">${message(code:'common.metro')}</label>
                                <div id="metro" class="float">
                                  <g:textArea name="metro_name" value="${(metros!='null')?metros:''}" rows="4" cols="30" disabled="disabled" />
                                </div>
                                <div class="actions float padd10" id="metro_edit">                    
                                  <span class="action_button orange nowrap">
                                    <a class="icon edit" href="javascript:void(0)" onclick="$('metro_div').show();$('metro_edit').hide();return false;">${message(code:'button.change')}</a>
                                  </span>
                                  <span class="action_button orange nowrap">
                                    <a class="icon delete" href="javascript:void(0)" onclick="addMetro('');resetCheckboxes('li_metro');$('metro_div').hide();return false;">${message(code:'ads.metro.delete.all')}</a>                     
                                  </span>
                                </div>
                              </td>
                            </tr>
                            <tr id="metro_div" style="display:none">
                              <td>            
                                <label for="metro" id="metro_label">${message(code:'ads.metro.select')}</label>
                                <div id="metro_results" class="overflow float">
                                  <ul>
                                  <g:each in="${metro}" var="item" status="i">
                                    <li>
                                      <input name="li_metro" id="checkbox_${i}" type="checkbox" <g:each in="${metro_ids}"><g:if test="${it.toLong()==item.id}">checked</g:if></g:each>/>
                                      ${item['name'+context?.lang]}
                                    </li>
                                    <input id="id_${i}" type="hidden" value="${item.id}"/>
                                  </g:each>
                                  </ul>
                                </div>
                                <div class="float padd10">                                  
                                  <div class="actions">                                  
                                    <span class="action_button orange nowrap">
                                      <a class="icon edit" href="#" onclick="addMetroChecked('li_metro');$('metro_div').hide();$('metro_edit').show();return false;">${message(code:'button.save')}</a>
                                    </span>
                                    <span class="action_button orange nowrap">
                                      <a class="icon noactive" href="javascript:void(0)" onclick="$('metro_div').hide();$('metro_edit').show();return false;">${message(code:'button.close')}</a>
                                    </span>                                    
                                  </div>
                                </div>
                              </td>                
                            </tr>
                            <tr>
                              <td>
                                <label for="street">${message(code:'ads.street')}</label>				
                                <input name="street" id="street_auto" type="text" maxlength="${stringlimit}" value="${hinrequest?.street}">
                                <div id="street_autocomplete" class="autocomplete" style="display:none"></div>
                                <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.street.alt')}">
                                  <img alt="${message(code:'ads.street.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
                                </a>
                              </td>		
                            </tr>		  		
                            <tr>
                              <td>
                                <label for="homenumber">${message(code:'ads.homenumber')}</label>		
                                <input type="text" class="price" id="homenumber" name="homenumber" maxlength="${stringlimit}" value="${inrequest?.homenumber}"/>		  	
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="spcf">${message(code:'ads.spcf')}</label>		
                                <input type="text" class="price" id="spcf" name="spcf" maxlength="${stringlimit}" value="${inrequest?.spcf}"/>		  	
                              </td>
                            </tr>
                          </table><br/><br/>
                          <h2 class="toggle border"><span class="edit_icon map"></span>${message(code:'ads.map')}</h2>
                          <div class="padd20">
                            <g:rawHtml>${message(code:'ads.map.info')}</g:rawHtml>					
                            <a href="javascript:collectAddress();">${message(code:'ads.map.filladdress')}</a><br/>
                            <input type="text" id="geocodeaddress" name="geocodeaddress" maxlength="${stringlimit}" value="${inrequest?.address}" style="width:95%; margin:2px 10px 5px 0 !important;"/><br/>
                            <input type="button" class="button-glossy orange" value="${message(code:'ads.map.findaddress')}" id="showaddressonmap" onclick="showAddress($('geocodeaddress').value);">
                          </div>
            
                          <div id="geocodererror" class="notice" style="display: none">
                            <g:rawHtml>${message(code:'ads.error.address')}</g:rawHtml>
                          </div>
                            
                          <div class="shadow" id="map_canvas" style="margin:10px 20px;width:690px;height:300px"></div>
                          <input type="hidden" id="x" name="x" value="${inrequest?.x}" />
                          <input type="hidden" id="y" name="y" value="${inrequest?.y}" />
                          <br/><br/>
        
                          <h2 class="toggle border"><span class="edit_icon question"></span>${message(code:'ads.howtoget')}?</h2>
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">
                            <tr>
                              <td>
                                <label for="reachpersonal">${message(code:'ads.howtoget.personal')}</label>
                                <g:textArea id="reachpersonal" name="reachpersonal" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" value="${inrequest?.reachpersonal?:''}" rows="5" cols="40" style="width:490px" />
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="reachpublic" style="padding-right:17px">${message(code:'ads.howtoget.public')}</label>
                                <g:textArea id="reachpublic" name="reachpublic" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" value="${inrequest?.reachpublic?:''}" rows="5" cols="40" style="width:490px" />
                              </td>
                            </tr>
                          </table><br/><br/>
                          <h2 class="toggle border"><span class="edit_icon keywords"></span>${message(code:'button.search')}</h2>            
                          <div class="padd20">
                            <p>${infotext['itext'+context?.lang]}</p>
                          </div>
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">
                            <tr>
                              <td>
                                <label for="mapkeywords">${message(code:'list.filtr.keywords')}</label>
                                <g:textArea id="mapkeywords" name="mapkeywords" onKeyDown="textLimit(this.id,${stringlimit});" onKeyUp="textLimit(this.id,${stringlimit});" value="${inrequest?.mapkeywords?:''}" rows="5" cols="40" style="width:490px" />
                              </td>
                            </tr>
                          </table>
                          <div style="padding:45px 20px;clear:both">
                            <div class="float">
                              <input type="button" class="button-glossy orange" value="${message(code:'button.save')}" onclick="sendForm();" style="margin-right:5px"/>
                              <input type="reset" class="button-glossy grey" value="${message(code:'button.reset')}"/>
                              <input type="submit" id="submit_form_button" style="display:none"/>
                            </div>
                            <span class="actions col">
                              <span class="action_button orange">
                                <g:link class="icon none" target="_blank" controller="personal" action="adsoverview" id="${home?.id}" base="${context.sequreServerURL}">${Infotext.findByControllerAndAction('personal','adsoverview')['name'+context?.lang]}</g:link>
                              </span>                
                            </span>
                          </div>
                        </g:formRemote>
                      </div>                     
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
</html>
