<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
      function initialize(){
      <g:if test="${inrequest?.type==2}">
        new Ajax.Autocompleter1("district_auto","region_id","district_autocomplete",
          "${resource(dir:'home',file:'district_autocomplete')}",{});
      </g:if>
      <g:if test="${inrequest?.type==1 || inrequest?.type==5}">      
        Yandex(); 
      </g:if>      
      }      
      function returnToList(){
        $("returnToListForm").submit();
      }
      function toggleType(iId){
        jQuery('[class^=typedata_]').hide();
        jQuery('.typedata_'+iId).show();
      }
      function getPopdirsByCountryId(iId){
         <g:remoteFunction controller='administrators' action='popdir_by_country_id' update='popdir_id' params="\'country_id=\'+iId" />
      }
      function getRegionsByCountryId(iId){
         <g:remoteFunction controller='administrators' action='region_by_country_id' update='region_div' params="\'country_id=\'+iId" />
      }
    <g:if test="${inrequest?.type==1 || inrequest?.type==5}">
      var ADDRESS_SEARCH_ZOOM=${inrequest?.type==1?13:16}, map=null, flag_marker_move=false, placemark={},
          iX=${inrequest?.x?:(hinrequest?.x?:3760000)},
          iY=${inrequest?.y?:(hinrequest?.y?:5575000)},
          iScale=ADDRESS_SEARCH_ZOOM;//Moskow default
      function Yandex(){
        ymaps.ready(function() {
          map = new ymaps.Map("map_canvas",{ center:[iY/100000,iX/100000], zoom: iScale, behaviors: ["default","scrollZoom"] });            
          map.controls.add("smallZoomControl")            
          .add("scaleLine")           
          .add("searchControl")
          //.add("typeSelector");
          .add(new ymaps.control.TypeSelector(['yandex#map','yandex#satellite','yandex#hybrid']));            
          placemark = new ymaps.Placemark([iY/100000, iX/100000],{}, {
            draggable: true,
            hasBalloon: false,
            iconImageHref:"${resource(dir:'images',file:'markerH.png')}",
            iconImageSize: [20,34],
            iconImageOffset:[-10,-34]
          });            
          map.geoObjects.add(placemark);            
          placemark.events.add("dragend", function(result) { 
            var coordinates =  this.geometry.getCoordinates() ;
            var y = this.geometry.getCoordinates()[0]*100000;
            var x = this.geometry.getCoordinates()[1]*100000;
            x=Math.round(x);
            y=Math.round(y);
            $('y').value = y; // и добавляем в поля широту
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
            placemark.events.add("dragend", function (result) { 
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
        geocoder2.then(function(res) {
          map.geoObjects.remove(placemark);
          if (res.geoObjects.getLength()) {
            // point - первый элемент коллекции найденных объектов
            var point = res.geoObjects.get(0);
            point.options.set({draggable: true,
              iconImageHref:"${resource(dir:'images',file:'marker_select.png')}",
              iconImageSize: [20,34],
              iconImageOffset:[-10,-34]
            });          
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
    </g:if>
    function getCityByPopdirId(iId){
      <g:remoteFunction controller='administrators' action='city_by_popdir_id' update='city_id' params="\'popdir_id=\'+iId" />
    }    
  </g:javascript>
  </head>
  <body onload="initialize()">
  <g:if test="${(flash?.error?:[]).size()>0}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
	  <g:each in="${flash?.error}"> 
        <g:if test="${it==1}"><li>Вы не заполнили обязательное поле &laquo;Регион&raquo;</li></g:if>
        <g:if test="${it==2}"><li>Вы не заполнили обязательное поле &laquo;Название&raquo;</li></g:if>
        <g:if test="${it==3}"><li>Вы не заполнили обязательное поле &laquo;Район&raquo;</li></g:if>
		    <g:if test="${it==4}"><li>Указанного Вами района не найдено. Проверьте корректность заполнения или <g:link controller="administrators" action="adressadd">добавте</g:link> новый район.</li></g:if>
        <g:if test="${it==5}"><li>Вы не заполнили обязательное поле &laquo;Страна&raquo;</li></g:if>
        <g:if test="${it==6}"><li>Вы не заполнили обязательное поле &laquo;Имя в GOOGLE&raquo;</li></g:if>
        <g:if test="${it==7}"><li>Вы не заполнили обязательное поле &laquo;Город&raquo;</li></g:if>
        <g:if test="${it==8}"><li>В данном городе уже существует такое значение поля &laquo;URL&raquo;</li></g:if>
        <g:if test="${it==9}"><li>Вы не заполнили обязательное поле &laquo;Код улицы&raquo;</li></g:if>
        <g:if test="${it==10}"><li>Вы не заполнили обязательное поле &laquo;Код района&raquo;</li></g:if>
      </g:each>
      </ul>
    </div>
  </g:if>
  
    <div class="your-space drop_shadow" style="padding-top:10px">
      <div class="button-glossy green header form">
      <g:if test="${!inrequest?.type}">
        <h1>Добавление нового района</h1>
      </g:if><g:elseif test="${inrequest?.type==1}">
        <h1>${inrequest?.city_id?'Редактирование города':'Добавление нового города'}</h1>
      </g:elseif><g:elseif test="${inrequest?.type==2}">
        <h1>Добавление новой улицы</h1>
      </g:elseif><g:elseif test="${inrequest?.type==3}">
        <h1>${inrequest?.country_id?'Редактирование страны':'Добавление новой страны'}</h1>
      </g:elseif><g:elseif test="${inrequest?.type==4}">
        <h1>${inrequest?.region_id?'Редактирование региона':'Добавление нового региона'}</h1>
      </g:elseif><g:elseif test="${inrequest?.type==5}">
        <h1>${inrequest?.id?'Редактирование ориентира':'Добавление нового ориентира'}</h1>
      </g:elseif>
      </div>
      <g:form name="saveAdressForm" url="[controller:'administrators',action:'adressadd']" method="post">
      <table width="100%" cellpadding="5" cellspacing="5" border="0">
        <tr>
          <td colspan="5" style="padding:0px">
            <a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку</a>
          </td>        
        </tr>
        <tr>
          <td><label for="name">Название:</label>
          <input type="text" id="name" name="name" value="${inrequest?.name?:(hinrequest?.name?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td><label for="name">Название En:</label>
          <input type="text" id="name_en" name="name_en" value="${inrequest?.name_en?:(hinrequest?.name_en?:'')}" style="width:100%"/>
          </td>
        </tr>
    <g:if test="${inrequest?.type!=3}">
      <g:if test="${inrequest?.type==5}">
        <tr>
          <td>
            <label for="name2">Название2:</label>
            <input type="text" id="name2" name="name2" value="${inrequest?.name2}" style="width:100%"/>
            <small>для типа "Точка" прописывать с предлогом</small>
          </td>
        </tr>
        <tr>
          <td>
            <label for="name2_en">Название2 En:</label>
            <input type="text" id="name2_en" name="name2_en" value="${inrequest?.name2_en}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td nowrap>
            <label for="citysigttype">Тип:</label>
            <select name="citysigttype" id="citysigttype" onchange="toggleType(this.value)">
              <option value="1" <g:if test="${1==(inrequest?.citysigttype?:1)}">selected="selected"</g:if>>Точка</option>
              <option value="2" <g:if test="${2==(inrequest?.citysigttype?:1)}">selected="selected"</g:if>>Улица</option>
              <option value="3" <g:if test="${3==(inrequest?.citysigttype?:1)}">selected="selected"</g:if>>Район</option>
            </select>&nbsp;&nbsp;
            <span class="typedata_2" style="${2!=(inrequest?.citysigttype?:1)?'display:none':''}">
              <label for="sightstreet_id">Код улицы:</label>
              <input type="text" id="sightstreet_id" name="sightstreet_id" value="${inrequest?.sightstreet_id?:''}" style="width:300px;"/>
            </span>
            <span class="typedata_3" style="${3!=(inrequest?.citysigttype?:1)?'display:none':''}">
              <label for="sightdistrict_id">Код района:</label>
              <input type="text" id="sightdistrict_id" name="sightdistrict_id" value="${inrequest?.sightdistrict_id?:''}" style="width:300px"/>
            </span>
          </td>
        </tr>
      </g:if>
      <g:if test="${inrequest?.type==5 ||inrequest?.type==4 || inrequest?.type==1 || !inrequest?.type}">
        <tr>
          <td nowrap>
            <label for="country_id">Страна:</label>
            <select name="country_id" id="country_id" <g:if test="${inrequest?.type==5 || inrequest?.type==4 || inrequest?.type==1 || !inrequest?.type}">onchange="getPopdirsByCountryId(this.value);getRegionsByCountryId(this.value)"</g:if>>
              <option value=""></option>
              <g:each in="${country}" var="item">
                <option value="${item.id}" <g:if test="${item.id==(inrequest?.country_id?:(hinrequest?.country_id?:0))}">selected="selected"</g:if>>${item.name}</option>
              </g:each>
            </select>
          <g:if test="${inrequest?.type}">
            <div id="popdirs">            
              <label for="popdir_id">Направление:</label>
              <select name="popdir_id" id="popdir_id" <g:if test="${inrequest?.type==5}">onchange="getCityByPopdirId(this.value);"</g:if>>              
                <option value="0"></option>
                <g:each in="${popdirs}" var="item">
                  <option value="${item.id}"  <g:if test="${item.id==inrequest?.popdir_id}">selected="selected"</g:if>>${item.name2}</option>
                </g:each>
              </select>            
            </div>
          </g:if>
          </td>
        </tr>
      </g:if><g:if test="${inrequest?.type==5}">
        <tr>
          <td nowrap>
            <label id="city_filter" for="city_id">Город:</label>
            <select name="city_id" id="city_id">              
              <option value="0"></option>
            <g:each in="${cities}" var="item">
              <option value="${item.id}" <g:if test="${item.id==inrequest?.city_id}">selected="selected"</g:if>>${item.name}</option>
            </g:each>
            </select>
          </td>
        </tr>
      </g:if><g:if test="${inrequest?.type!=4 && inrequest?.type!=5}">
        <tr>
          <td nowrap>
            <div id="region_div">          
            <label for="region_id">Регион:</label>
            <select name="region_id" id="region_id">
              <option value=""></option>
              <g:each in="${region}" var="item">
                <option value="${item.id}" <g:if test="${item.id==(inrequest?.region_id?:(hinrequest?.region_id?:0))}">selected="selected"</g:if>>${item.name}</option>
              </g:each>
            </select> 
            </div>            
          </td>
        </tr>
        <g:if test="${inrequest?.type==2}">
        <tr>
          <td>
            <label for="district_auto">Район:</label>
            <input type="text" id="district_auto" name="district" value="${inrequest?.district}" style="width:100%"/>
            <div id="district_autocomplete" class="autocomplete" style="margin:0px;padding:0px;position:absolute;width:280px;z-index:1000"></div>
          </td>
        </tr>
        </g:if><g:if test="${inrequest?.type==1}">
        <tr>
          <td>
            <label for="name2">Название2:</label>
            <input type="text" id="name2" name="name2" value="${inrequest?.name2?:(hinrequest?.name2?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="tagname">Тег статей:</label>
            <input type="text" id="tagname" name="tagname" value="${inrequest?.tagname?:(hinrequest?.tagname?:'')}" style="width:83%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="city_is_metro">Наличие метро:</label>
            <input type="checkbox" id="city_is_metro" name="is_metro" value="1" <g:if test="${inrequest?.is_metro?:(hinrequest?.is_metro?:0)}">checked</g:if>/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="city_is_index">Опция индексирования:</label>
            <input type="checkbox" id="city_is_index" name="is_index" value="1" <g:if test="${inrequest?.is_index?:(hinrequest?.is_index?:0)}">checked</g:if>/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="city_is_specoffer">Опция спец. предложений:</label>
            <input type="checkbox" id="city_is_specoffer" name="is_specoffer" value="1" <g:if test="${inrequest?.is_specoffer?:(hinrequest?.is_specoffer?:0)}">checked</g:if>/>
          </td>
        </tr>
        <tr>
          <td>
            <div id="geocodererror" class="notice" style="display: none">
              <p>Заданный адрес не найден.</p>
              <p>Укажите вручную местонахождение вашего объекта, щелкнув на карте в нужном месте мышью.</p>
              <p>Вы можете откорректировать местоположение, перетащив маркер мышью.</p>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <label for="cry_regorder">Выберите место города:</label>
            <input type="button" class="button-glossy orange" value="Найти адрес на карте" id="showaddressonmap" onclick="showAddress($('name').value);">         
            <div id="map_canvas" style="width:100%;height:300px;"></div> 
          </td>
        </tr>
        <tr>
          <td>
            <label for="seo_title">Seo_title:</label>
            <input type="text" id="seo_title" name="seo_title" value="${inrequest?.seo_title?:(hinrequest?.seo_title?:'')}" style="width:86%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="seo_keywords">Seo_keywords:</label>
            <input type="text" id="seo_keywords" name="seo_keywords" value="${inrequest?.seo_keywords?:(hinrequest?.seo_keywords?:'')}" style="width:79%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="seo_description">Seo_description:</label>
            <textarea id="seo_description" name="seo_description" style="width:76%">${inrequest?.seo_description?:(hinrequest?.seo_description?:'')}</textarea>
          </td>
        </tr>
        <tr>
          <td>
            <label for="description">Описание жилья:</label>
            <fckeditor:editor name="description" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
              <g:rawHtml>${inrequest?.description?:(hinrequest?.description?:'')}</g:rawHtml>
            </fckeditor:editor>
          </td>
        </tr>
        <tr>
          <td>
            <label for="flat_description">Описание квартир:</label>
            <fckeditor:editor name="flats_description" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
              <g:rawHtml>${inrequest?.flats_description?:(hinrequest?.flats_description?:'')}</g:rawHtml>
            </fckeditor:editor>
          </td>
        </tr>
        <tr>
          <td>
            <label for="room_description">Описание комнат:</label>
            <fckeditor:editor name="rooms_description" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
              <g:rawHtml>${inrequest?.rooms_description?:(hinrequest?.rooms_description?:'')}</g:rawHtml>
            </fckeditor:editor>
          </td>
        </tr>        
        <tr>
          <td>
            <label for="cottages_description">Описание коттеджей:</label>
            <fckeditor:editor name="cottages_description" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
              <g:rawHtml>${inrequest?.cottages_description?:(hinrequest?.cottages_description?:'')}</g:rawHtml>
            </fckeditor:editor>
          </td>
        </tr>
        <tr>
          <td>
            <label for="houses_description">Описание домиков:</label>
            <fckeditor:editor name="houses_description" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
              <g:rawHtml>${inrequest?.houses_description?:(hinrequest?.houses_description?:'')}</g:rawHtml>
            </fckeditor:editor>
          </td>
        </tr>
        <tr>
          <td>
            <label for="beds_description">Описание койко-мест:</label>
            <fckeditor:editor name="beds_description" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
              <g:rawHtml>${inrequest?.beds_description?:(hinrequest?.beds_description?:'')}</g:rawHtml>
            </fckeditor:editor>
          </td>
        </tr>        
        </g:if>
      </g:if><g:if test="${inrequest?.type==5}">
        <tr>
          <td>
            <label for="sight_radius">Индексация:</label>
            <input type="checkbox" id="sight_is_index" name="is_index" value="1" <g:if test="${inrequest?.is_index || hinrequest?.is_index}">checked</g:if> />
          </td>
        </tr>
        <tr>
          <td>
            <label for="sight_urlname">URL:</label>
            <input type="text" id="sight_urlname" name="urlname" value="${inrequest?.urlname?:(hinrequest?.urlname?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="sight_title">Title:</label>
            <input type="text" id="sight_title" name="title" value="${inrequest?.title?:(hinrequest?.title?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="sight_title_en">Title EN:</label>
            <input type="text" id="sight_title_en" name="title_en" value="${inrequest?.title_en?:(hinrequest?.title_en?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="sight_keywords">Keywords:</label>
            <input type="text" id="sight_keywords" name="keywords" value="${inrequest?.keywords?:(hinrequest?.keywords?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="sight_description">Description:</label>
            <textarea id="sight_description" name="description" cols="40" rows="4" style="width:100%">${inrequest?.description?:(hinrequest?.description?:'')}</textarea>
          </td>
        </tr>
        <tr>
          <td>
            <label for="sight_description">Description EN:</label>
            <textarea id="sight_description_en" name="description_en" cols="40" rows="4" style="width:100%">${inrequest?.description_en?:(hinrequest?.description_en?:'')}</textarea>
          </td>
        </tr>        
        <tr>
          <td>
            <label for="sight_header">Заголовок H1:</label>
            <input type="text" id="sight_header" name="header" value="${inrequest?.header?:(hinrequest?.header?:'')}" style="width:100%"/>
          </td>
        </tr>        
        <tr>
          <td>
            <label for="country_itext">Описание с html:</label>
            <fckeditor:editor name="itext" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
              <g:rawHtml>${inrequest?.itext?:(hinrequest?.itext?:'')}</g:rawHtml>
            </fckeditor:editor>
          </td>
        </tr>
        <tr class="typedata_1" style="${1!=(inrequest?.citysigttype?:1)?'display:none':''}">
          <td>
            <label for="sight_radius">Радиус:</label>
            <input type="text" id="sight_radius" name="radius" value="${inrequest?.radius?:(hinrequest?.radius?:'')}" style="width:50px"/> км
          </td>
        </tr>
        <tr class="typedata_1" style="${1!=(inrequest?.citysigttype?:1)?'display:none':''}">
          <td>
            <label for="cry_regorder">Выберите место ориентира:</label>
            <div id="map_canvas" style="width:100%;height:300px;"></div> 
          </td>
        </tr>
      </g:if>
    </g:if><g:else><!--country-->
        <tr>
          <td>
            <label for="country_header">Имя в GOOGLE:</label>
            <input type="text" id="country_urlname" name="urlname" value="${inrequest?.urlname?:(hinrequest?.urlname?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="country_header">Заголовок H1:</label>
            <input type="text" id="country_header" name="header" value="${inrequest?.header?:(hinrequest?.header?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="country_title">Title:</label>
            <input type="text" id="country_title" name="title" value="${inrequest?.title?:(hinrequest?.title?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="country_keywords">Keywords:</label>
            <input type="text" id="country_keywords" name="keywords" value="${inrequest?.keywords?:(hinrequest?.keywords?:'')}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="country_description">Description:</label>
            <textarea style="width:99%" cols="40" rows="4" id="country_description" name="description">${inrequest?.description?:(hinrequest?.description?:'')}</textarea>
          </td>
        </tr>
        <tr>
          <td>
            <label for="country_description_en">Description EN:</label>
            <textarea style="width:99%" cols="40" rows="4" id="country_description_en" name="description_en">${inrequest?.description_en?:(hinrequest?.description_en?:'')}</textarea>            
          </td>
        </tr>
        <tr>
          <td>
            <label for="country_itext">Расширенный текст с html:</label>
            <fckeditor:editor name="itext" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
              <g:rawHtml>${inrequest?.itext?:(hinrequest?.itext?:'')}</g:rawHtml>
            </fckeditor:editor>
          </td>
        </tr>
        <tr>
          <td>
            <label for="country_itext">Расширенный текст EN с html:</label>
            <fckeditor:editor name="itext_en" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
              <g:rawHtml>${inrequest?.itext_en?:(hinrequest?.itext_en?:'')}</g:rawHtml>
            </fckeditor:editor>
          </td>
        </tr>
        <tr>
          <td>
            <label for="country_is_index">Опция индексирования:</label>
            <input type="checkbox" id="country_is_index" name="is_index" value="1" <g:if test="${inrequest?.is_index?:(hinrequest?.is_index?:0)}">checked</g:if>/>
          </td>
        </tr>
        <tr>
          <td>
            <label for="cry_regorder">Порядок сортировки:</label>
            <input type="text" id="country_regorder" name="cry_regorder" value="${inrequest?.cry_regorder?:(hinrequest?.regorder?:0)}" style="width:100%"/>
          </td>
        </tr>
        <tr>
          <td>
          <span>
            <label for="cry_valuta_id">Стандартная валюта:</label>
            <g:select name="cry_valuta_id" from="${valuta}" value="${inrequest?.cry_valuta_id?:(hinrequest?.valuta_id?:0)}" optionKey="id" optionValue="code" noSelection="${['0':'none']}" style="width:35%"/>
            <input type="checkbox" id="country_reserve" name="cry_reserve" value="1" <g:if test="${inrequest?.cry_reserve?:(hinrequest?.is_reserve?:0)}">checked</g:if> />
            Доступна для вывода
          </span>
          </td>
        </tr>
    </g:else>
        <tr>
          <td colspan="2" align="right">
            <input type="button" class="button-glossy green" value="${((inrequest?.type==5 && inrequest?.id) || (inrequest?.type==4 && inrequest?.region_id) || (inrequest?.type==3 && inrequest?.country_id) || (inrequest?.type==1 && inrequest?.city_id))?'Сохранить':'Добавить'}" style="margin-right:10px;" onClick="$('saveAdressForm').submit();"/>
            <input type="reset" class="button-glossy grey" value="Отмена" onclick="$('returnToListForm').submit()" />
            <g:if test="${inrequest?.type==5 && inrequest?.modstatus}"> 
              <input type="button" class="button-glossy red" value="Удалить" onclick="$('citysight_delForm').submit()" />
            </g:if>
          </td>
        </tr>        
      </table>
      <input type="hidden" id="type" name="type" value="${inrequest?.type?:0}" />
      <input type="hidden" id="save" name="save" value="1"/>
      <g:if test="${inrequest?.type==4}">
        <input type="hidden" id="region_id" name="region_id" value="${inrequest?.region_id}"/>
      </g:if>
      <g:if test="${inrequest?.type==3}">
        <input type="hidden" id="country_id" name="country_id" value="${inrequest?.country_id}"/>
      </g:if>
      <g:if test="${inrequest?.type==1}">
        <input type="hidden" id="city_id" name="city_id" value="${inrequest?.city_id}"/>
        <input type="hidden" id="x" name="x" value="${inrequest?.x?:(hinrequest?.x?:3760000)}"/>
        <input type="hidden" id="y" name="y" value="${inrequest?.y?:(hinrequest?.y?:5575000)}"/>
      </g:if>
      <g:if test="${inrequest?.type==5}"> 
        <input type="hidden" id="sight_id" name="id" value="${inrequest?.id}"/>      
        <input type="hidden" id="x" name="x" value="${inrequest?.x?:(hinrequest?.x?:3760000)}"/>
        <input type="hidden" id="y" name="y" value="${inrequest?.y?:(hinrequest?.y?:5575000)}"/>
      </g:if>
      </g:form>
      <g:form name="returnToListForm" url="${[controller:'administrators',action:'adress', params:[fromEdit:1, type:inrequest?.type?:0]]}">     
      </g:form>
      <g:form name="citysight_delForm" url="${[controller:'administrators',action:'citysight_del', params:[fromEdit:1, type:inrequest?.type?:0, id:inrequest?.id]]}">
      </g:form>     
      <a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку</a>             
    </div>
  </body>
</html>
