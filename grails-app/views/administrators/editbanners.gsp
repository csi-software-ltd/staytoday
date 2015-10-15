<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript src="swfobject.js" />
    <g:javascript>
    function load(){
    <g:if test="${data?.bclass==2}">
      swfobject.removeSWF("banner");
      var iW=${data?.hsize};
      var iH=${data?.vsize};
      if(iW<50) iW=468;
      if(iH<50) iH=60;
      var par = {name:"opaque", wmode:"opaque"};
      var att = { width:iW, height:iH, wmode:"opaque",data:"${url}${data?.filename}"};
      swfobject.createSWF(att, par, "banner");
    </g:if>    
      Yandex();    
    <g:if test="${(data?.btype?:0)!=2}">
      $("map_div").hide();
      $("map_canvas").setStyle({
        visibility: 'hidden'
      });
    </g:if>      
    }
   
    var iX=${data?.x?:3760000},
    iY=${data?.y?:5575000},
    iScale=${data?.zoom?:5};//Moskow default
    var map = null;       
    var flag_marker_move = false;
    var placemark={}; 

    function confirmDelete(){
      if(confirm("Удалить?"))
        document.delete.submit();
    }
    var edit=${inrequest?.edit?:0};

    function checkData(){
      $('file_error').hide();
  
      if(!edit && $('filename').value.length==0){
        $('file_error').show();
        return false;
      }    
      return true;
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
            
            map.events.add("boundschange",
              function(e) {
                $('zoom').value =e.get('newZoom');                                                                              
              }  
            );
          });               
    }
    
    function showMap(iVal){    
      if(iVal==2){        
        $("map_div").show();
        $("map_canvas").setStyle({
          visibility: 'visible'
        });
      }else{	
        $("map_div").hide();
        $("map_canvas").setStyle({
          visibility: 'hidden'
        });
      }
    }	  
    </g:javascript>
  </head>      
  <body onload="load()">
  <g:if test="${(inrequest?.error?:0)==1}">
    <div class="notice drop_shadow" style="float:none">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <li>Выберите файл для баннера</li>
      </ul>
    </div>
  </g:if>  
  <table width="100%" cellpadding="5" cellspacing="0" border="0">
    <g:form url="[action:'savebanner']" method="POST" enctype="multipart/form-data" onSubmit="return checkData();">
      <input type="hidden" name="edit" value="${inrequest?.edit}"/>
      <input type="hidden" name="id" value="${data?.id}">    
      <tr>
        <td colspan="4">
          <g:link class="to-parent" action="showbanners" params="${hid_inrequest}">К списку баннеров</g:link>  
        </td>
      </tr>
      <tr>
        <td colspan="4"><h1 class="blue">Редактирование баннера</h1></td>
      </tr>      
      <tr>      
        <td colspan="4">
          <div id="banner" align="center"> 
          <g:if test="${data?.bclass==1}">
            <img src="${url}${data?.filename}" width="100%"/>
          </g:if>
          </div>
        </td>
      </tr>
      <tr>
        <td nowrap><label for="filename">Файл баннера:</label></td>
        <td><input  type="file" size="4" id="filename" name="filename"/></td>
        <td nowrap><label for="bname">Название баннера:</label></td>
        <td><input type="text" name="bname" style="width:385px" value="${data?.bname}"/></td>
      </tr>
      <tr>
        <td nowrap><label for="btype">Тип баннера:</label></td>
        <td>
          <select name="btype" onChange="showMap(this.value)">            
          <g:each in="${advbannertypes}" var="type">
            <option value="${type.id}" <g:if test="${type.id==data?.btype}">selected</g:if>>${type.name}</option>
          </g:each>
          </select>
        </td>
        <td nowrap><label for="modstatus">Статус баннера:</label></td>
        <td>
          <select name="modstatus">
            <option value="0" <g:if test="${0==data?.modstatus}">selected</g:if>>неактивный</option>
            <option value="1" <g:if test="${1==data?.modstatus}">selected</g:if>>публикация</option>
          </select>          
        </td>      
      </tr>
      <tr>
        <td nowrap><label for="altname">Alt:</label></td>
        <td><input type="text" name="altname" style="width:120px" value="${data?.altname}"/></td>
        <td nowrap><label for="burl">Url:</label></td>
        <td><input type="text" name="burl" style="width:385px" value="${data?.burl}"/></td>
      </tr>     
      <tr>
        <td nowrap><label for="datefrom">Публиковать с:</label></td>
        <td nowrap><calendar:datePicker name="datefrom" dateFormat="%d-%m-%Y" value="${data?.date_start}"/></td>
        <td><label for="dateto">по:</label></td>
        <td nowrap><calendar:datePicker name="dateto"  dateFormat="%d-%m-%Y" value="${data?.date_end}"/></td>        
      </tr>
      <tr>
        <td nowrap><label for="showlimit">Максимум:</label></td>
        <td><input type="text" style="width:120px" name="showlimit" value="${data?.showlimit}"/></td>
        <td><label for="bcount">Показы:</label></td>
        <td nowrap>
          <span>
            <input type="text" style="width:120px" name="bcount" value="${data?.bcount}" disabled />
          </span>&nbsp;&nbsp;
          <span nowrap>
            <label for="bclick">Клики:</label>
            <input type="text" style="width:120px" name="bclick" value="${data?.bclick}" disabled />
          </span>
        </td>
      </tr>
      <tr>
        <td nowrap><label for="hsize">Ширина, px:</label></td>
        <td><input type="text" style="width:120px" id="hsize" name="hsize" value="${data?.hsize}"/></td>
        <td nowrap><label for="vsize">Высота, px:</label></td>
        <td><input type="text" style="width:120px" id="vsize" name="vsize" value="${data?.vsize}"/></td>        
      </tr>
      <tr>
        <td colspan="4" align="right">
          <input type="submit" class="button-glossy green" value="Сохранить" style="margin-right:10px;"/>          
        </td>
      </tr>
      <g:collect in="${inrequest}" expr="it">
        <input type="hidden" name="hid_${it.key}" value="${it.value}"/>
      </g:collect>
      <input type="hidden" id="x" name="x" value="${data?.x}" />
      <input type="hidden" id="y" name="y" value="${data?.y}" /> 
      <input type="hidden" id="zoom" name="zoom" value="${data?.zoom?:5}" />
    </g:form> 
    <g:form url="[action:'deletebanner']" method="POST" useToken="true" id="delete" name="delete">
      <input type="hidden" name="id" value="${data?.id}">    
      <g:collect in="${inrequest}" expr="it">
      <input type="hidden" name="hid_${it.key}" value="${it.value}"/>
      </g:collect>
    </g:form>
    
    <tr>
      <td colspan="4">
        <div id="map_div">
          <label>Выберите место показа баннера:</label>
          <div id="map_canvas" style="width:100%;height:300px;"></div> 
        </div>
      </td>
    </tr>
    
    <tr>
      <td colspan="4">
        <g:link class="to-parent" action="showbanners" params="${hid_inrequest}">К списку баннеров</g:link>  
      </td>
    </tr>
  </table>
</body>
</html>
