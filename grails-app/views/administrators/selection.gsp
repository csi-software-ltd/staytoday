<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <style type="text/css">.glossy td { font-size: 15px; }</style>    
    <g:javascript>
  	function initialize(){
      $('user_submit_button').click();
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
      $('modstatus').selectedIndex = 0;     
      $('country_id').selectedIndex = 0;
      $('region_id').selectedIndex = 0;
      $('city_id').selectedIndex = 0;     
    }
     
      function updateRegion(lCountryId){
        <g:remoteFunction controller='administrators' action='region' update='region_id' params="\'countryId=\'+lCountryId" />        
      }
      function updateCity(lRegionId){
        <g:remoteFunction controller='administrators' action='city' update='city_id' params="\'regionId=\'+lRegionId" />
      }     
    </g:javascript>  
  </head>  

	<body onload="initialize()">
  
    <div id="homelist">
      <g:formRemote name="allForm" url="[action:'selectionlist']" update="[success:'companystat']">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">                
        <tr>
          <td nowrap>
            <span nowrap>
              <label for="country_id">Страна:</label>
              <select name="country_id" id="country_id" onchange="updateRegion(this.value)">
                <option value=""></option>
                <g:each in="${country}" var="item">
                  <option value="${item.id}" <g:if test="${item.id==inrequest?.country_id}">SELECTED</g:if>>${item.name}</option>
                </g:each>
              </select>
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="region_id">Регион:</label>
              <select name="region_id" id="region_id" style="width:315px" onchange="updateCity(this.value)">
                <option value=""></option>
                <g:each in="${region}" var="item">
                  <option value="${item.id}" <g:if test="${item.id==inrequest?.region_id}">SELECTED</g:if>>${item.name}</option>
                </g:each>
              </select>				
            </span>
            <span nowrap>
              <label for="region_id">Город:</label>
              <select name="city_id" id="city_id" style="width:315px">
                <option value=""></option>
                <g:each in="${city}" var="item">
                  <option value="${item.id}" <g:if test="${item.id==inrequest?.city_id}">SELECTED</g:if>>${item.name}</option>
                </g:each>
              </select>				
            </span>            
          </td>
        </tr>
        <tr>
          <td nowrap>
            <span nowrap>
              <label for="modstatus">Статус публикации:</label>
              <select id="modstatus" name="modstatus">
                <option value=""></option>
                <g:each in="${modstatus}" var="item">
                  <option value="${item.modstatus}" <g:if test="${item.modstatus==inrequest?.modstatus}">selected="selected"</g:if>>${item.toString()}</option>
                </g:each>
              </select>
            </span>&nbsp;&nbsp;            
          <span style="margin-left:350px" nowrap>
            <input type="submit" class="button-glossy green" id="user_submit_button" value="Показать" style="margin-right:10px">
            <input type="button" class="button-glossy grey" value="Сброс" onClick="resetData()"/>
          </span>
          </td>
        </tr>
      </table>
      </g:formRemote>
    </div>

    <div id="companystat"></div>
    <div id="sitestat">
      <div id="placeList" align="left">        
    </div>
  </div>
  
  </body>
</html>
