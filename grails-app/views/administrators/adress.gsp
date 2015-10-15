<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
    function initialize(iParam){
      hideFilter();
      switch(iParam){
        case 0:
          sectionColor('district');
          $('country_id').show();
          $('country_filter').show();
          $('region_id').show();
          $('region_filter').show();
          $('type').setValue('0');
          $('district_add').show();
          $('popdir_id').hide();
          $('popdir_filter').hide();
          $('city_span').hide();
          
          $('submit_button').click();         
          break;
        case 1:
          sectionColor('city');
          $('country_id').show();
          $('country_filter').show();
          $('region_id').show();
          $('region_filter').show();
          $('type').setValue('1');
          $('city_add').show();
          $('popdir_id').hide();
          $('popdir_filter').hide();
          $('city_span').hide();
          
          $('submit_button').click();
          break;
        case 2:
          sectionColor('street');
          $('country_id').show();
          $('country_filter').show();
          $('region_id').show();
          $('region_filter').show();
          $('type').setValue('2');
          $('street_add').show();
          $('popdir_id').hide();
          $('popdir_filter').hide();
          $('city_span').hide();
         
          $('submit_button').click();
          break;
        case 3:
          sectionColor('country');
          $('type').setValue('3');
          $('country_add').show();
          $('popdir_id').hide();
          $('popdir_filter').hide();
          $('city_span').hide();
         
          $('submit_button').click();
          break;
        case 4:
          sectionColor('region');
          $('country_id').show();
          $('country_filter').show();
          $('popdir_id').hide();
          $('popdir_filter').hide();
          $('type').setValue('4');
          $('region_add').show();
          $('city_span').hide();
         
          $('submit_button').click();
          break;
        case 5:
          sectionColor('sights');
          $('country_id').show();
          $('country_filter').show();
          $('popdir_id').show();
          $('popdir_filter').show();
          $('region_id').hide();
          $('region_filter').hide();
          $('type').setValue('5');
          $('city_span').show();
            
          $('citysight_add').show();
          $('submit_button').click();
          break;
      }
	  }

    function hideFilter(){
      $('district_add').hide();
      $('city_add').hide();
      $('street_add').hide();
      $('country_add').hide();
      $('region_add').hide();
      $('country_id').hide();
      $('country_filter').hide();
      $('region_id').hide();
      $('region_filter').hide();
      $('citysight_add').hide();
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
          { evalScripts: true });		  
    }
      
	  function resetData(){
      $('name').setValue('');
      $('region_id').selectedIndex = 0;
      $('country_id').selectedIndex = 0;
    }
    
    function sectionColor(sSection){
      $('district').style.color = 'black';
      $('city').style.color = 'black';
      $('street').style.color = 'black';
      $('country').style.color = 'black';
      $('region').style.color = 'black';
      $('sights').style.color = 'black';
      $(sSection).style.color = '#0080F0';
    }
    function getPopdirsByCountryId(iId){
      <g:remoteFunction controller='administrators' action='popdir_by_country_id' update='popdir_id' params="\'country_id=\'+iId" />
    }
    function getRegionsByCountryId(iId){
      <g:remoteFunction controller='administrators' action='region_by_country_id' update='region_id' params="\'country_id=\'+iId" />
    }
    function getCityByPopdirId(iId){
      <g:remoteFunction controller='administrators' action='city_by_popdir_id' update='city_id' params="\'popdir_id=\'+iId" />
    }
    </g:javascript>  
  </head>  

	<body onload="initialize(${type})">
    <div align="center">
      <table>
        <tr>
          <td width="150"><a href="javascript:void(0)" onclick="initialize(3)" id="country"><h3><u>Страны</u></h3></a></td>
          <td width="150"><a href="javascript:void(0)" onclick="initialize(4)" id="region"><h3><u>Регионы</u></h3></a></td>
          <td width="150"><a href="javascript:void(0)" onclick="initialize(0)" id="district"><h3><u>Районы</u></h3></a></td>
          <td width="150"><a href="javascript:void(0)" onclick="initialize(1)" id="city"><h3><u>Города</u></h3></a></td>
          <td width="150"><a href="javascript:void(0)" onclick="initialize(2)" id="street"><h3><u>Улицы</u></h3></a></td>
          <td width="150"><a href="javascript:void(0)" onclick="initialize(5)" id="sights"><h3><u>Ориентиры</u></h3></a></td>
        </tr>
      </table>
    </div>
    <div id="homelist">
      <g:formRemote name="allForm" url="[action:'adresslist']" update="[success:'adresslist']">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
        <tr>
          <td nowrap>
            <label id="country_filter" for="country_id">Страна:</label>
            <select name="country_id" id="country_id" onchange="getPopdirsByCountryId(this.value);getRegionsByCountryId(this.value)">
              <option value=""></option>
              <g:each in="${country}" var="item">
                <option value="${item.id}" <g:if test="${item.id==inrequest?.country_id}">SELECTED</g:if>>${item.name}</option>
              </g:each>
            </select>&nbsp;&nbsp;
            
              <label id="popdir_filter" for="popdir_id">Направление:</label>
              <select name="popdir_id" id="popdir_id"  onchange="getCityByPopdirId(this.value);">              
                <option value="0"></option>
                <g:each in="${popdirs}" var="item">
                  <option value="${item.id}"  <g:if test="${item.id==inrequest?.popdir_id}">SELECTED</g:if>>${item.name2}</option>
                </g:each>
              </select>
              
            <span id="city_span">
              <label for="city_id">Город:</label>
              <select name="city_id" id="city_id">              
                <option value="0"></option>
                <g:each in="${cities}" var="item">
                  <option value="${item.id}"  <g:if test="${item.id==inrequest?.city_id}">SELECTED</g:if>>${item.name}</option>
                </g:each>
              </select>
              <br/>
            </span>
            
            
            <label id="region_filter" for="region_id">Регион:</label>
            <select name="region_id" id="region_id">
              <option value=""></option>
              <g:each in="${region}" var="item">
                <option value="${item.id}" <g:if test="${item.id==inrequest?.region_id}">SELECTED</g:if>>${item.name}</option>
              </g:each>
            </select>                                    
            
            <label for="name">Название:</label>
            <input type="text" id="name" name="name" value="${inrequest?.name}" style="width:150px">
          </td>
        </tr>
        <tr>
          <td align="right" nowrap>
            <g:link controller="administrators" action="adressadd" class="button-glossy orange" elementId="district_add" style="display:none">Добавить район</g:link>
            <g:link controller="administrators" action="adressadd" class="button-glossy orange" params="[type:1]" elementId="city_add" style="display:none">Добавить город</g:link>
            <g:link controller="administrators" action="adressadd" class="button-glossy orange" params="[type:2]" elementId="street_add" style="display:none">Добавить улицу</g:link>
            <g:link controller="administrators" action="adressadd" class="button-glossy orange" params="[type:3]" elementId="country_add" style="display:none">Добавить страну</g:link>
            <g:link controller="administrators" action="adressadd" class="button-glossy orange" params="[type:4]" elementId="region_add" style="display:none">Добавить регион</g:link>
            <g:link controller="administrators" action="adressadd" class="button-glossy orange" params="[type:5]" elementId="citysight_add" style="display:none">Добавить ориентир</g:link>
            <input type="submit" class="button-glossy green" id="submit_button" value="Показать" style="margin:0 10px">
            <input type="button" class="button-glossy grey" value="Сброс" onClick="resetData()"/>
          </td>
        </tr>        
      </table>
      <input type="hidden" id="type" name="type" value="0" />      
      </g:formRemote>
    </div>
    <div id="adresslist"></div>
  </body>
</html>
