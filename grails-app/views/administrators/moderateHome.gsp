<html>
  <head>
  <title>Административное приложение StayToday.ru</title>
  <meta name="layout" content="admin" />  
  
  <g:javascript>
    var ADDRESS_SEARCH_ZOOM=13;
    var iX=${inrequest?.x?:Region.get(inrequest?.region_id?:0)?.x?:0},
    iY=${inrequest?.y?:Region.get(inrequest?.region_id?:0)?.y?:0},
    iScale=${inrequest?.x?13:Region.get(inrequest?.region_id?:0)?.scale?:0};//toDo ADDRESS_SEARCH_ZOOM
    var map = null;
    var geocoder = null;
	  var opened_popdirection = 0;
	  var mouseOnDir  = 0;        
    var flag_marker_move = false;
    var placemark={};  
    var tmp=[];      

	function initialize(){   
	  Yandex();
    
    new Ajax.Autocompleter1("street_auto","region_id","street_autocomplete",
      "${resource(dir:'administrators',file:'street_autocomplete')}",{});
    new Ajax.Autocompleter1("city_auto","region_id","city_autocomplete",
      "${resource(dir:'administrators',file:'city_autocomplete')}",{});
    new Ajax.Autocompleter1("district_auto","region_id","district_autocomplete",
      "${resource(dir:'administrators',file:'district_autocomplete')}",{});  
	}
    function updateRegion(lCountryId){
      <g:remoteFunction controller='home' action='region' update='region_id' params="\'countryId=\'+lCountryId" />
    }
	
    function deletePhoto(lId, lHId){
      if (confirm('Вы уверены?')){
        <g:remoteFunction controller='administrators' action='homephotodelete' onSuccess='location.reload(true)' params="'id='+lId+'&home_id='+lHId" />
      }
    }
	
    function showImage(sPic){
      var href = window.location.href.split('moderateHome');
      window.open(href[0]+"bigimage?picture="+sPic);
    }
	
    var activeMenu=-1;
    function ShowMenu(id){
      CancelHide();
      if(id!=activeMenu){
        if(activeMenu!=-1) HideAfterDelay();
        var parent="mm" + id;
        var child="sm" + id;
    
        menu=layer(parent);
        submenu=layer(child);    
        submenu.moveTo(menu.getAbsoluteLeft() - 140, menu.getAbsoluteTop() - 57);
        submenu.show();
        activeMenu=id;
      }
    }

    function HideMenu(){
      delay=setTimeout("HideAfterDelay()",3500);
    }
  
    function HideAfterDelay(){
      submenu.hide();
      activeMenu=-1;
    }

    function CancelHide(){
      if(self.delay) clearTimeout(delay);
    }
	
    function confirmADS(){
      $('confirm').value = '1';
	  $("save_ads").click();
    }

    function cancelConf(){
      var lId = ${home.id};
      <g:remoteFunction controller='administrators' action='cancelConf' onSuccess='location.assign(location.href)' params="'id='+lId" />
    }

    function declineADS(){
      $('decline').value = '1';
      $("save_ads").click();
    }
    function saveADS(){
      $('save').value = '1';
      $("moderateForm").submit();
    }
	
    function deleteADS(iId){
      if (confirm('Вы уверены?')){
        var hId = ${home.id};
        <g:remoteFunction controller='administrators' action='updateDeleteStatus' onSuccess='location.reload(true)' params="'id='+hId+'&status='+iId" />
      }
    }

    function returnToList(){
      $("returnToListForm").submit();
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
            //.add("typeSelector");
            .add(new ymaps.control.TypeSelector(['yandex#map','yandex#satellite','yandex#hybrid']));

// Создание метки и добавление ее на карту                    
            placemark = new ymaps.Placemark([iY/100000, iX/100000],{},
              {
                draggable: true,
                hasBalloon: false,
                iconImageHref:"${resource(dir:'images',file:'marker.png')}",
                iconImageSize: [20,34],
                iconImageOffset:[-10,-34]
              }
            );
           map.geoObjects.add(placemark);            
           
           placemark.events.add("dragend", function (result) { 
                var coordinates =  this.geometry.getCoordinates() ;
                var y = this.geometry.getCoordinates()[0]*100000;;
                var x = this.geometry.getCoordinates()[1]*100000;
                x=Math.round(x);
                y=Math.round(y);
                $('y').value  = y; // и добавляем в поля широту
                $('x').value = x; // и долготу  
            },placemark); 

            map.events.add("click",
              function(e) {
                var clickPoint=e.get("coordPosition");
                map.geoObjects.remove(placemark);
                
                map.setCenter(clickPoint, map.getZoom()); // центром карты делаем эту точку
                
                placemark = new ymaps.Placemark(clickPoint,{
                  },{
                    draggable: true,
                    hasBalloon: false,
                    iconImageHref:"${resource(dir:'images',file:'marker.png')}",
                    iconImageSize: [20,34],
                    iconImageOffset:[-10,-34]
                  }
                );
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
              }  
            );                                  
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
            iconImageHref:"${resource(dir:'images',file:'marker.png')}",
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
          placemark.events.add("dragend", function (result) { 
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
            
	 
	  //map<<
    function togglePopDirection(iType){
      if ($("select_popdirection").style.display == 'none'){
        updatePopDirection(iType);
        opened_popdirection = 1;
        $("select_popdirection").style.display = 'block'
      } else {
        $("select_popdirection").style.display = 'none';
        opened_popdirection = 0;
      }
    }
    function updatePopDirection(iType){
      <g:remoteFunction controller='administrators' action='selectpopdirection' update='select_popdirection' params="\'country_id=\'+iType" />
    }
    function clickPopDirection(sWhere){
	  if($('fulladdress').value.search(sWhere)==-1)
		$('fulladdress').value += ' '+sWhere;
      togglePopDirection();
    }
    function checkDropDowns(){
      if (opened_popdirection){
        if (!mouseOnDir){
          opened_popdirection = 0;
          $("select_popdirection").style.display = 'none';
        }
      }
    }
    function testLinkname(sLinkname){
      var lId = ${home.id}
      <g:remoteFunction controller='administrators' action='testLinkname' onSuccess='testLinknameResponse(e)' params="'id='+lId+'&linkname='+sLinkname" />
    }
    function testLinknameResponse(e){
      $('linkname').value = e.responseJSON.linkname;
      $('linknametestprocess').hide();
      $('linknametest').show();
    }
    function tstLink(){
      $('linknametestprocess').show();
      $('linknametest').hide();
    }
    
    function collectAddress(bFlag){	    
      var region = $('region_name').value, city = '';
      if(!bFlag){      
        if(region!=$('city_auto').value)
          city = $('city_auto').value;	               
        $('geocodeaddress').value = $('country_name').value+' '+region +' '+city+' '+$('street_auto').value+' '+$('homenumber').value;
      }else{
        city = $('city_auto').value;
        $('shortaddress').value = $('country_name').value+' '+city+' '+$('street_auto').value+' '+$('homenumber').value;        
      }      
    } 
    function getMetro(iRegId){	 
      var lId=${home?.id?:0};
      <g:remoteFunction controller='administrators' action='get_metro' update='metro_results' params="'home_id='+lId+'&region_id='+iRegId" />
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
      <g:remoteFunction controller='administrators' action='addmetro' update='metro' params="'metros='+metroIds+'&home_id='+lId" />
      tmp= [ ];
    }  
    function resetCheckboxes(name){
      var checkBoxes=document.getElementsByName(name);
      for(i=0;i<checkBoxes.length;i++){	    
        if($('checkbox_'+i).checked)
          $('checkbox_'+i).checked=false;          		
      } 	  
    }            
	</g:javascript>
  </head>  
  <body onload="initialize()">

  <g:if test="${(flash?.save_error?:[]).size()>0}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
		<g:each in="${flash?.save_error}">
		  <g:if test="${it==1}"><li>Вы не заполнили обязательное поле &laquo;Название&raquo;</li></g:if>
		  <g:if test="${it==2}"><li>Вы не заполнили обязательное поле &laquo;Описание&raquo;</li></g:if>
		  <g:if test="${it==3}"><li>Юзера не существует или у юзера нет емейла.</li></g:if>
      <g:if test="${it==4}"><li>Неверный формат номера телефона.</li></g:if>
		  <g:if test="${it==5}"><li>Вы не заполнили обязательное поле &laquo;Linkname&raquo;</li></g:if>
      <g:if test="${it==6}"><li>Неверный формат поля &laquo;Индекс&raquo;</li></g:if>
		  <g:if test="${it==10}"><li>Ошибка данных в справочниках</li></g:if>
		  <g:if test="${it==101}"><li>Ошибка БД. Изменения не сохранены.</li></g:if>
		</g:each>
      </ul>
    </div>
  </g:if>

  <g:form name="moderateForm" url="[controller:'administrators',action:'moderateHome', id:home.id]" method="post">
    <table width="100%" cellpadding="5" cellspacing="5" border="0" onclick="checkDropDowns();">
      <tr>
        <td colspan="5" style="padding:0px">
          <a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку объявлений</a>
        </td>        
      </tr>
      <tr>
        <td colspan="5"><h1 class="blue">Форма модерации объявления № ${home.id}</h1></td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon question"></span>Служебные</h2>
        </td>
      </tr>
      <tr>
        <td width="160" nowrap>Дата создания:</td>
        <td width="200"><input type="text" disabled value="${String.format('%td.%<tm.%<tY %<tH:%<tM',home.inputdate)}"/></td>
        <td width="160" nowrap>Дата модификации :</td>
        <td colspan="2"><input type="text" disabled value="${String.format('%td.%<tm.%<tY %<tH:%<tM',home.moddate)}"/></td>
      </tr>     
      <tr>
        <td nowrap>Статус публикации:</td>
        <td><input type="text" disabled value="${homemodstatus.name}"/></td>
        <td nowrap>Статус рассмотрения:</td>
        <td colspan="2"><input type="text" disabled <g:if test="${home.is_confirmed}">value="рассмотрен"</g:if><g:else>value="не рассмотрен"</g:else>/></td>
      </tr>
      <tr>
        <td nowrap>Популярное направление:</td>
        <td colspan="4">
          <select id="popdirection_id" name="popdirection_id">
            <option value="0">нет</option>
            <g:each in="${popdirections}" var="item">
              <option <g:if test="${item?.id==inrequest?.popdirection_id}">selected="selected"</g:if> value="${item?.id}">
                ${item?.name}
              </option>
            </g:each>
          </select>
        </td>
      </tr>
      <tr>
        <td>Выводить на главной:</td>
        <td>
          <select id="is_mainpage" name="is_mainpage">
            <option <g:if test="${inrequest?.is_mainpage==0}">selected="selected"</g:if> value="0">нет</option>
            <option <g:if test="${inrequest?.is_mainpage==1}">selected="selected"</g:if> value="1">да</option>
          </select>
        </td>
        <td colspan="3" nowrap>
          <label for="is_specoffer">Выводить в предложении дня:</label>&nbsp;&nbsp;&nbsp;&nbsp;
          <select id="is_specoffer" name="is_specoffer">
            <option <g:if test="${inrequest?.is_specoffer==0}">selected="selected"</g:if> value="0">нет</option>
            <option <g:if test="${inrequest?.is_specoffer==1}">selected="selected"</g:if> value="1">да</option>
          </select>
        </td>
      </tr>
      <tr>
        <td nowrap>Рейтинг публикации:</td>
        <td><input id="homeRating" type="text" disabled value="${home.rating}"/></td>
        <td colspan="3">
          <span class="actions">          
            <span class="action_button">
              <g:remoteLink class="icon view" action="recountRating" id="${home?.id}" onLoading="\$('calcratingprocess').show();" onLoaded="\$('calcratingprocess').hide();" onSuccess="\$('homeRating').value=e.responseJSON.rating" >Пересчитать рейтинг</g:remoteLink>
            </span>
            <span id="calcratingprocess" style="display:none"><img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/></span>
          </span>
        </td>
      </tr>
      <tr>
        <td nowrap>Дополнительный рейтинг:</td>
        <td><input type="text" name="extrarating" value="${inrequest.extrarating}"/></td>
        <td nowrap>Штрафной рейтинг:</td>
        <td colspan="2"><input type="text" name="ratingpenalty" value="${inrequest.ratingpenalty}"/></td>
      </tr>
      <tr>
        <td nowrap>Рейтинг пользователя:</td>
        <td><input type="text" name="ownerUserRating" type="text" disabled value="${ownerUser?.activityrating?:0}"/></td>
        <td nowrap>Рейтинг клиента:</td>
        <td colspan="2"><input type="text" name="ownerClientRating" value="${ownerClient?.addrating?:0}"/></td>
      </tr>
      <tr>
        <td nowrap>Количество показов:</td>
        <td><input type="text" disabled value="${statslisting?.quant?:0}"/></td>
        <td nowrap>Количество кликов:</td>
        <td colspan="2"><input type="text" disabled value="${statsclick?.quant?:0}"/></td>
      </tr>
      <tr>
        <td>Комментарий администратора:</td>
        <td colspan="4">
          <fckeditor:editor name="comments" width="100%" height="200" toolbar="ARENDA" fileBrowser="default">
            <g:rawHtml>${inrequest?.comments}</g:rawHtml>
          </fckeditor:editor>
        </td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon owner"></span>Информация о владельце</h2>
        </td>
      </tr>
      <tr>
      <g:if test="${ownerUser}">      
        <td class="user" valign="top">
          <span>
            <g:link action="users" params="${[user_id:ownerUser?.id?:0]}">${ownerUser?.id?:0}</g:link><br/>
            <b><g:link controller="profile" action="view" id="${ownerUser.id}">${ownerUser?.nickname}</g:link><br/>
            <small>${ownerUser?.email}</small></b><br/><br/>
            <small><i>зарегистрирован с: ${String.format('%td.%<tm.%<tY',ownerUser?.inputdate)}</i></small>
          </span>
        </td>
        <td>
          <ul class="verifications-list">
            <li class="verifications-list-item ${(ownerUser?.modstatus==0)?'verifications-none':''}" style="padding:6px 0px">
              <div class="verifications-icon ${(ownerUser?.modstatus==0)?'none':''}"></div>            
              <div class="verifications-legend ${ownerUser?.provider}"></div>
              <span class="label">Связан(а) через ${(ownerUser?.is_external==1)?'соц.сеть':ownerUser?.provider}</span><br />
              <span class="count">
                <g:if test="${ownerUser?.modstatus==1}">подтверждено</g:if>
                <g:elseif test="${ownerUser?.modstatus==-1}">забаненo</g:elseif>
                <g:elseif test="${ownerUser?.modstatus==0}">не подтверждено</g:elseif>
              </span>
            </li>
            <li class="verifications-list-item ${(ownerUser?.is_telcheck==0)?'verifications-none':''}" style="padding:6px 0px">
              <div class="verifications-icon ${(ownerUser?.is_telcheck==0)?'none':''}"></div>  
              <div class="verifications-legend phone"></div>
              <span class="label">Телефон</span><br />
              <span class="count">${(ownerUser?.is_telcheck==1)?'подтвержден':'не подтвержден'}</span>              
            </li>            
          </ul>
          <input type="text" id="owner_user_tel" name="owner_user_tel" value="${ownerUser?.tel}" />          
        </td>
        <td colspan="2"style="padding-left:40px">
          <ul class="verifications-list" style="width:180px">
            <li class="verifications-list-item ${(ownerClient?.resstatus!=1)?'verifications-none':''}" style="padding:6px 0px">
              <div class="verifications-icon ${(ownerClient?.resstatus!=1)?'none':''}"></div>                
              <span class="label">Бронирование через сайт</span><br/>
              <span class="count">${(ownerClient?.resstatus==1)?'подтверждено':'не подтверждено'}</span>              
            </li>
            <li class="verifications-list-item" style="padding:6px 0px">                
              <div class="verifications-legend phone"></div>
              <span class="label">Доп. телефон</span><br />
              <span class="count">${ownerUser?.tel1?'есть':'нет'}</span>
            </li>
          </ul>
          <input type="text" id="owner_user_tel1" name="owner_user_tel1" value="${ownerUser?.tel1}" />
			  </td>
			  <td valign="top">  
          <span class="actions">          
            <span class="action_button" style="float:right;margin-right:10px">
              <g:link class="icon view" target="_blank" mapping="pView" params="${[uid:'id'+ownerUser?.id]}">Посмотреть анкету</g:link>
            </span>
          </span>
          <ul class="verifications-list" style="margin:40px 0 0 -80px;">
            <li class="verifications-list-item" style="padding:6px 0px">                
              <div class="verifications-legend skype"></div>
              <span class="label">Skype</span><br />
              <span class="count">${ownerUser?.skype?'есть':'нет'}</span>
            </li>
          </ul>
          <input type="text" id="owner_user_skype" name="owner_user_skype" value="${ownerUser?.skype}" style="margin-left:-80px;" />     </td>
      </g:if><g:else>
			  <td colspan="5">
          <label for="owner_user">Владелец не определен. Добавить объявление владельцу с кодом:</label>
          <input type="text" class="mini" id="owner_user" name="owner_user" value=""/>
			  </td>
      </g:else>
			</tr>
    <g:if test="${ownerUser}">
      <tr>
        <td>О владельце:</td>
        <td colspan="4"><textarea rows="5" cols="40" name="owner_user_description" style="width:99%">${ownerUser?.description}</textarea></td>
      </tr>
    </g:if>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon"></span>Вид объявления</h2>
        </td>
      </tr>
      <tr>
        <td nowrap>Тип жилья:</td>
        <td>
          <select id="hometype_id" name="hometype_id">
            <g:each in="${hometype}" var="item">            
              <option <g:if test="${item?.id==home.hometype_id}">selected="selected"</g:if> value="${item.id}">${item.name}</option>
            </g:each>
          </select>                  
        </td>
        <td colspan="3">
          <input type="checkbox" id="is_vip" name="is_vip" <g:if test="${inrequest?.is_vip}">checked</g:if> value="1"/>
          <label for="is_vip">VIP</label>
          <input type="checkbox" id="is_fiesta" name="is_fiesta" <g:if test="${inrequest?.is_fiesta}">checked</g:if> value="1"/>
          <label for="is_fiesta">сдается под праздники</label>
          <input type="checkbox" id="is_renthour" name="is_renthour" <g:if test="${inrequest?.is_renthour}">checked</g:if> value="1"/>
          <label for="is_renthour">сдается на часы</label>
        </td>
      </tr>
      <tr>
        <td>Вместимость:</td>
        <td><input type="text" disabled value="${Homeperson.get(home.homeperson_id)?.name}"/></td>
        <!--<td nowrap>Класс жилья:</td>
        <td colspan="2"><input type="text" disabled style="width:100%" value="${Homeclass.get(home.homeclass_id)?.name}"/></td>-->
      </tr>
      <tr>
        <td colspan="5">
          <h2 class="toggle border"><span class="edit_icon details"></span>Описание</h2>
        </td>
      </tr>
      <tr>
        <td>Главное фото:</td>
        <td colspan="4">
          <div class="thumbnail">
            <g:if test="${home.mainpicture}"><img src="${imageurl+home.client_id+'/t_'+home.mainpicture}" border="0" align="absmiddle" ondblclick="showImage('${home.mainpicture}');"></g:if>
            <g:else><img src="${resource(dir:'images',file:'default-picture.png')}" border="0"></g:else>
          </div>
        </td>
      </tr>     
      <tr>
        <td>Название:</td>
        <td colspan="4"><input type="text" name="name" value="${inrequest?.name}" maxlength="35" style="width:100%"/></td>
      </tr>
      <tr>
        <td>Linkname:</td>
        <td colspan="2"><input type="text" id="linkname" name="linkname" value="${inrequest?.linkname}" style="width:100%" onblur="testLinkname(this.value)" onKeyPress="tstLink()"/></td>
        <td id="linknametest">Linkname проверен</td>
        <td><span id="linknametestprocess" style="display:none"><img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/></span></td>
      </tr>
      <tr>
        <td>Описание:</td>
        <td colspan="4"><textarea rows="5" cols="40" name="description" style="width:99%">${inrequest?.description}</textarea></td>
      </tr>
      <tr>
        <td nowrap>Количество комнат:</td>
        <td><input type="text" disabled <g:if test="${home.homeroom_id?:0}">value="${Homeroom.get(home.homeroom_id?:1)?.name}"</g:if><g:else>value=""</g:else>/></td>
        <td nowrap>Количество ванн:</td>
        <td colspan="2"><input type="text" disabled <g:if test="${home.homebath_id?:0}">value="${Homebath.get(home.homebath_id?:1)?.name}"</g:if><g:else>value=""</g:else>/></td>
      </tr>
      <tr>
        <td nowrap>Количество кроватей:</td>
        <td><input type="text" disabled value="${home.bed}"/></td>
        <td>Площадь, м<sup>2</sup>:</td>
        <td colspan="2"><input type="text" disabled value="${home.area}"/></td>
      </tr>
      <tr>
        <td nowrap>Особые условия:</td>
        <td colspan="4"><textarea rows="5" cols="40" name="remarks" style="width:99%">${inrequest?.remarks}</textarea></td>
      </tr>
      <tr>
        <td nowrap>Правила дома:</td>
        <td colspan="4"><textarea rows="5" cols="40" name="homerule" style="width:99%">${inrequest?.homerule}</textarea></td>
      </tr>
      <tr>
        <td colspan="5">
          <fieldset>
            <legend><h2 class="toggle"><span class="edit_icon services"></span>Основные опции</h2></legend>
            <ul style="float:left;width:45%">
            <g:each in="${homeoption}" var="item" status="i"> 
              <g:if test="${i < homeoption.size()/2}">
              <li>
                <input type="checkbox" disabled id="${item.fieldname?:'homeoption'+i}" <g:if test="${item.fieldname}"><g:if test="${home[item.fieldname]}">checked</g:if></g:if> value="1"/>
                <label for="${item.fieldname?:'homeoption'+i}">${item.name}</label>
              </li>                    
              </g:if>
            </g:each>
            </ul>
            <ul style="float:right;width:55%">
            <g:each in="${homeoption}" var="item" status="i"> 
              <g:if test="${i >= homeoption.size()/2}">
              <li>
                <input type="checkbox" disabled id="${item.fieldname?:'homeoption'+i}" <g:if test="${item.fieldname}"><g:if test="${home[item.fieldname]}">checked</g:if></g:if> value="1"/>
                <label for="${item.fieldname?:'homeoption'+i}">${item.name}</label>
              </li>
              </g:if>
            </g:each>
            </ul>
          </fieldset>
        </td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon photos"></span>Фотографии</h2>
        </td>
      </tr>      
	    <tr>
        <td colspan="5">  
<g:if test="${homephoto}">        
          <table width="100%" class="dotted" cellpadding="0" cellspacing="0" border="0" rules="all" frame="border" style="border-style:dotted">
          <g:each in="${homephoto}" var="item" status="i">            
            <tr>
              <td width="120">
                <div class="thumbnail <g:if test="${item?.is_main}">selected</g:if>" id="thumbnail_${item.id}">
                  <g:if test="${item?.picture}">
                  <img id="mm_${item.id}" src="${imageurl}${home.client_id+'/t_'+item?.picture}" ondblclick="showImage('${item?.picture}');">
                  </g:if>
                </div>
              </td>
              <td>
                <input type="text" name="ptext_${item.id}" value="${item.ptext}" style="width:100%"/>
              </td>
              <td width="80">
			  <g:if test="${!item.is_main}">
                <div class="actions">
                  <span class="action_button nowrap">
                    <a class="icon delete" title="Удалить" href="#" onclick="javascript:deletePhoto(${item.id}, ${item.home_id})">Удалить</a>                     
                  </span>                 
                </div>                  
			  </g:if>
              </td>
            </tr>                    
          </g:each>          
          </table> 
</g:if>
<g:else>
          <p>Фотографии не заданы</p>
</g:else>          
        </td>
      </tr> 
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon address"></span>Сведения о местонахождении</h2>
        </td>
      </tr>      
      <tr>
        <td>Страна:</td>
        <td>
          <select id="country_id" name="country_id" onChange="updateRegion(this.value)">
          <g:each in="${country}" var="item">            
            <option <g:if test="${item?.id==inrequest?.country_id}">selected="selected"</g:if> value="${item?.id}" onclick="$('country_name').value='${item.name}';">
              ${item?.name}
            </option>
          </g:each>			  
          </select>
          <input type="hidden" id="country_name" value="${Country.get(inrequest?.country_id)?.name?:''}"/>
        </td>
        <td>Регион:</td>
        <td colspan="2">
          <div id="region_result">              
            <select id="region_id" name="region_id">
            <g:each in="${region}" var="item">            
              <option <g:if test="${item?.id==inrequest?.region_id}">selected="selected"</g:if> value="${item?.id}" onclick="$('region_name').value='${item.name}';">
                ${item?.name}
              </option>
            </g:each>              
            </select>
            <input type="hidden" id="region_name" value="${Region.get(inrequest?.region_id)?.name?:''}"/>
          </div>
        </td>
      </tr>
      <tr>
        <td>Город:</td>
        <td colspan="4">
          <input type="text" id="city_auto" name="city" value="${inrequest?.city}" maxlength="35"/>
          <div id="city_autocomplete" class="autocomplete" style="display:none"></div>
          <a class="tooltip" href="javascript:void(0)" title="Начните ввод наименование города и система предложит вам варианты">
            <img alt="Начните ввод наименование города и система предложит вам варианты" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
          </a>
        </td>
      </tr>
      <tr>                    
        <td>Район:</td>
        <td colspan="4">                                 
          <input name="district" id="district_auto" type="text" value="${inrequest?.district}"/>
          <div id="district_autocomplete" class="autocomplete" style="display:none"></div>
          <a class="tooltip" href="javascript:void(0)" title="Начните ввод наименование района и система предложит вам варианты">
            <img alt="Начните ввод наименование района и система предложит вам варианты" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
          </a>
        </td>		
      </tr>
      <tr>
        <td>Почтовый индекс:</td>
        <td>                                		
          <input type="text" class="price" id="pindex" name="pindex" value="${inrequest?.pindex}"/>		  	
        </td>           
      </tr>
      <tr id="metro_tr" style="<g:if test="${!curregion?.is_metro}">display:none</g:if>">
        <td>Метро:</td>
        <td>          
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
        <td>Метро:</td>
        <td>                                            
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
        <td>Улица:</td>
        <td colspan="4">                            
          <input name="street" id="street_auto" type="text" value="${inrequest?.street}"/>
          <div id="street_autocomplete" class="autocomplete" style="margin:0px;padding:0px;position:absolute;width:280px;z-index:1000"></div>
          <a class="tooltip" href="javascript:void(0)" title="Начните ввод наименование улицы и система предложит вам варианты">
            <img alt="Начните ввод наименование улицы и система предложит вам варианты" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
          </a>
        </td>		
      </tr>		  		
      <tr>
        <td>Дом:</td>
        <td>                                	
          <input type="text" class="price" id="homenumber" name="homenumber" value="${inrequest?.homenumber}"/>		  	
        </td>
        <td>Квартира:</td>
        <td>
          <input type="text" class="price" id="spcf" name="spcf" value="${inrequest?.spcf}" />
        </td>
      </tr>
      <tr>
        <td>Адрес:</td>        
        <td colspan="4">
          <a href="javascript:collectAddress(0);">Заполнить адрес из адресных данных</a><br/>
          <g:textArea id="geocodeaddress" name="address" value="${home?.address}" rows="1" cols="40" style="width:99%"/>
        </td>
	  </tr>
      <tr>
        <td>Адрес для публикации:</td>
        <td colspan="4">
          <a href="javascript:collectAddress(1);">Заполнить адрес из адресных данных</a><br/>
          <input type="text" id="shortaddress" name="shortaddress" value="${inrequest?.shortaddress}" maxlength="255" style="width:100%"/>          
        </td>
      </tr>
      <tr>
        <td colspan="5">
            <input type="button" class="button-glossy green" value="Геокодировать адрес" id="showaddressonmap" onClick="showAddress(document.moderateForm.geocodeaddress.value);">
        </td>
      </tr>      
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon map"></span>Местоположение на карте</h2>
        </td>
      </tr>
      <tr>
          <td colspan="5">
            <div id="geocodererror" class="notice" style="display: none">
              <p>Заданный адрес не найден.</p>
              <p>Укажите вручную местонахождение вашего объекта, щелкнув на карте в нужном месте мышью.</p>
              <p>Вы можете откорректировать местоположение, перетащив маркер мышью.</p>
            </div>
          </td>
        </tr>      
      <tr>
        <td colspan="5">
          <div class="button-glossy grey drop_shadow" id="map_canvas" style="padding:0px;width:100%;height:300px"></div>
          <input type="hidden" id="x" name="x" value="${inrequest?.x}" />
          <input type="hidden" id="y" name="y" value="${inrequest?.y}" />
        </td>
      </tr>
      <tr>
        <td colspan="5" align="center">
          <h2 class="toggle border"><span class="edit_icon details"></span>Как добраться?</h2>
        </td>
      </tr>
      <tr>
        <td>Личным транспортом:</td>
        <td colspan="4">
          <g:textArea name="reachpersonal" value="${inrequest?.reachpersonal?:''}" rows="5" cols="40" style="width:99%" />
        </td>
      </tr>
      <tr>
        <td>Общественным транспортом:</td>
        <td colspan="4">
          <g:textArea name="reachpublic" value="${inrequest?.reachpublic?:''}" rows="5" cols="40" style="width:99%" />
        </td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon keywords"></span>Поиск</h2>
        </td>
      </tr>    
      <tr>
        <td>Ключевые слова:</td>
        <td colspan="4"><g:textArea name="mapkeywords" value="${inrequest?.mapkeywords?:''}" rows="5" cols="40" style="width:99%" /></td>
      </tr>
      <tr>
        <td>Адрес для поиска:<br/>
		  <span>
			<small id="a_select_popdirection" class="select" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0" onclick="togglePopDirection(1)"><a href="javascript:void(0)" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0">Добавить тег popdirection</a></small>
			<div class="select_dropdown" id="select_popdirection" style="display:none;" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0"></div>
		  </span>
		</td>
        <td colspan="4"><g:textArea id="fulladdress" name="fulladdress" value="${inrequest?.fulladdress?:''}" rows="5" cols="40" style="width:99%" /></td>
      </tr>
      <tr>
        <td>Информация для поиска:</td>
        <td colspan="4"><g:textArea name="fullinfo" value="${inrequest?.fullinfo?:''}" rows="5" cols="40" style="width:99%" /></td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon prices"></span>Базовая цена</h2>
        </td>
      </tr>
      <tr>
      <g:if test="${home.pricestatus==1}">
        <td>Базовая цена:</td>
        <td colspan="2">
          <input type="text" id="pricestandard" class="price" disabled value="${home.pricestandard}"/>
          <label class="currency" for="pricestandard"><g:rawHtml>${home_valuta?.symbol?:''}</g:rawHtml></label>
        </td>
        <td colspan="2">
          <input type="checkbox" id="is_pricebyday" name="is_pricebyday" <g:if test="${home?.is_pricebyday}">checked</g:if> value="1"/>
          <label for="is_pricebyday">Считать по дням</label>
        </td>
      </g:if>
      <g:else>
        <td colspan="3">Базовая цена не установлена</td>
        <td colspan="2">
          <input type="checkbox" id="is_pricebyday" name="is_pricebyday" <g:if test="${home?.is_pricebyday}">checked</g:if> value="1"/>
          <label for="is_pricebyday">Считать по дням</label>
        </td>
      </g:else>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon inactive"></span>Периоды неактивности аренды</h2>
        </td>          
      </tr>                  
      <tr>
        <td colspan="5">            
<g:if test="${homepropinactive}">        
          <table class="dotted" cellpadding="0" cellspacing="0" rules="all" frame="border">
            <tr>
              <th width="105">Дата начала</th>
              <th width="105">Дата окончания</th>
            </tr>
            <g:each in="${homepropinactive}">
            <tr>
              <td><g:formatDate format="dd.MM.yyyy" date="${it?.date_start}"/></td>
              <td><g:formatDate format="dd.MM.yyyy" date="${it?.date_end}"/></td>
            </tr>
            </g:each>
          </table>            
</g:if>
<g:else>
          <p>Периоды не заданы</p>
</g:else>         
        </td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon period"></span>Цены по периодам</h2>
        </td>         
      </tr>          
      <tr>
        <td colspan="5">
<g:if test="${homeprop}">
          <table class="dotted" cellpadding="0" cellspacing="0" rules="all" frame="border">
            <tr>
              <th width="85">Дата начала</th>
              <th width="105">Дата окончания</th>
              <th width="70">Посуточно</th>
              <th width="70">Выходные</th>
            </tr>
            <g:each in="${homeprop}">            
            <tr>
              <td><g:formatDate format="dd.MM.yyyy" date="${it?.date_start}"/></td>
              <td><g:formatDate format="dd.MM.yyyy" date="${it?.date_end}"/></td>
              <td>${it?.price?:0}</td>
              <td>${it?.priceweekend?:0}</td>
            </tr>
            </g:each>              
          </table>
</g:if>
<g:else>
          <p>Цены не заданы</p>
</g:else>          
        </td>
      </tr>      
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Скидки</h2>
        </td>
      </tr>
      <tr>
        <td colspan="5">
          <div class="description_text">
          <g:if test="${discounts.long}">
            <div class="description_text_wrapper">
              <h2>Заблаговременные скидки</h2>
              Скидка в ${Discountpercent.findByPercent(discounts.long.discount)?.name} предоставляется при заказе более чем за ${Timetodecide.findByDays(discounts.long.discexpiredays)?.name2} при условии аренды более чем на ${Timetodecide.findByDays(discounts.long.minrentdays)?.name2?:'любой срок'}. Особые условия: ${discounts.long.terms?:'нет'}
            </div>
            </g:if>
            <g:if test="${discounts.hot}">
            <div class="description_text_wrapper">
              <h2>Скидки последнего дня</h2>
              Скидка в ${Discountpercent.findByPercent(discounts.hot.discount)?.name} предоставляется при заказе менее чем за ${Timetodecide.findByDays(discounts.hot.discexpiredays)?.name2} при условии аренды более чем на ${Timetodecide.findByDays(discounts.hot.minrentdays)?.name2?:'любой срок'}. Особые условия: ${discounts.hot.terms?:'нет'}
            </div>
            </g:if>
          </div>
        </td>
      </tr>
      <tr>
        <td colspan="5" align="right">
          <input type="button" id="save_ads" class="button-glossy green mini" value="Сохранить" onclick="saveADS();">
        <g:if test="${home.is_confirmed}">
          <input type="button" class="button-glossy red mini" value="Отменить подтверждение" onclick="cancelConf();">
        </g:if>
					<input type="button" class="button-glossy lightblue mini" <g:if test="${!((home.modstatus==1||home.modstatus==4)&&!home.is_confirmed)}">disabled</g:if> value="Подтвердить" onClick="confirmADS();">
					<input type="button" class="button-glossy grey mini" <g:if test="${!((home.modstatus==1||home.modstatus==4)&&!home.is_confirmed)}">disabled</g:if> value="Отклонить" onClick="declineADS();">
					<input type="button" class="button-glossy red mini" <g:if test="${!(home.modstatus==-2)}">disabled</g:if> value="Удалить" onClick="deleteADS(-3);">
          <input type="button" class="button-glossy red mini" <g:if test="${!(home.modstatus==-2||home.modstatus==-3)}">disabled</g:if> value="Восстановить" onClick="deleteADS(2);">
        </td>
      </tr>
      <tr>
        <td colspan="5"><a class="to-parent" href="#" onclick="returnToList();">К списку объявлений</a></td>        
      </tr>
    </table>
    <input type="hidden" id="confirm" name="confirm" value="0" />
    <input type="hidden" id="decline" name="decline" value="0" />
    <input type="hidden" id="save" name="save" value="0" />
  </g:form>    
  <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'administrators',action:'homes',params:[fromDetails:1]]}">
  </g:form>
  
  </body>
</html>
