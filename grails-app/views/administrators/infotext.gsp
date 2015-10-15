<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
    function initialize(iParam){
      switch(iParam){
        case 0:
          sectionColor('infotext');
          $('homelist').show();
          $('placeList').hide();          
          $('user_submit_button').click();
          $('companystat').setStyle({height: '450px'}); 
          break;
        case 1:
          sectionColor('mail');
          $('homelist').hide();
          $('placeList').show();
          $('mail_submit_button').click();
          $('companystat').setStyle({height: '575px'}); 
          break;
      }
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
      $('inf_action').setValue('');
      $('inf_controller').setValue('');
      $('itemplate_id').selectedIndex = 0;
    }
    
    function sectionColor(sSection){
      $('infotext').style.color = 'black';
      $('mail').style.color = 'black';
      $(sSection).style.color = '#0080F0';
    }
    </g:javascript>  
  </head>  

	<body onload="initialize(${type})">
    <div align="center">
      <table>
        <tr>
          <td width="168"><a href="javascript:void(0)" onclick="initialize(0)" id="infotext"><h3><u>Инфотексты</u></h3></a></td>
          <td><a href="javascript:void(0)" onclick="initialize(1)" id="mail"><h3><u>Шаблоны писем</u></h3></a></td>
        </tr>
      </table>
    </div>
    <div id="homelist">
      <g:formRemote name="allForm" url="[action:'infotextlist']" update="[success:'companystat']">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
        <tr>
          <td nowrap>
            <label for="menu">Меню:</label>
            <select id="itemplate_id" name="itemplate_id">
              <option value="-1" <g:if test="${inrequest?.itemplate_id==-1}">selected="selected"</g:if>></option>
              <option value="0" <g:if test="${inrequest?.itemplate_id==0}">selected="selected"</g:if>>без шаблона</option>
              <g:each in="${itemplate}" var="item">            
              <option value="${item?.id}" <g:if test="${inrequest?.itemplate_id==item?.id}">selected="selected"</g:if>>
                ${item?.name}
              </option>
            </g:each>              
            </select>&nbsp;&nbsp;          
            <label for="inf_controller">Контроллер:</label>
            <input type="text" id="inf_controller" name="inf_controller" value="${inrequest?.inf_controller}" style="width:100px">&nbsp;&nbsp;
            <label for="inf_action">Экшен:</label>
            <input type="text" id="inf_action" name="inf_action" value="${inrequest?.inf_action}" style="width:150px">
          </td>
        </tr>
        <tr>
          <td align="right" nowrap>
            <g:link controller="administrators" action="infotextadd" class="button-glossy orange">Добавить страницу</g:link>
            <input type="submit" class="button-glossy green" id="user_submit_button" value="Показать" style="margin:0 10px">
            <input type="button" class="button-glossy grey" value="Сброс" onClick="resetData();location.reload(true)"/>
          </td>
        </tr>
      </table>   
      </g:formRemote>
    </div>
    <div id="placeList" align="center">
      <g:formRemote name="allForm" url="[action:'infotextlist', id:1]" update="[success:'companystat']">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
        <tr>
          <td nowrap>
            <label for="inf_action">Экшен:</label>
            <input type="text" id="inf_action" name="inf_action" value="${inrequest?.inf_action}" style="width:150px">
          </td>
          <td align="right" nowrap>
            <g:link controller="administrators" action="infotextadd" params="[type:'1']" class="button-glossy orange">Добавить шаблон</g:link>
            <input type="submit" class="button-glossy green" id="mail_submit_button" value="Показать" style="margin:0 10px">
            <input type="button" class="button-glossy grey" value="Сброс" onClick="$('inf_action').setValue('');"/>
          </td>
        </tr>
      </table>   
      </g:formRemote>
    </div>    
    <div id="companystat"></div>
  </body>
</html>
