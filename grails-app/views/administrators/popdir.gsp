<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
      function initialize(){
        $('find_direction_submit_button').click();
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
        $('popdir_id').setValue('');
        $('popdir_name').setValue('');
      }
    </g:javascript>  
  </head>  
  <body onload="initialize()">
    <div id="popdirlist">
      <g:formRemote name="findDirectionForm" url="[action:'popdirlist']" update="[success:'companystat']">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
          <tr>
            <td nowrap>
              <span nowrap>
                <label for="popdir_id">Код:</label>
                <input type="text" class="price" id="popdir_id" name="popdir_id" value="${inrequest?.popdir_id}"/>
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="popdir_name">Название:</label>
                <input type="text" id="popdir_name" name="popdir_name" value="${inrequest?.popdir_name}" style="width:250px">
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="popdir_modstatus">Статус:</label>
                <select name="popdir_modstatus">
                  <option value="-1"></option>
                  <option value="0">скрыт</option>
                  <option value="1">активен</option>
                </select>
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="popdir_country">Страна:</label>
                <select name="popdir_country">
                  <option value="0"></option>
                  <g:each in="${countries}" var="country" status="i">                
                  <option value="${country?.id}">${country?.name}</option>
                  </g:each>
                </select>
              </span>
            </td>
          </tr>
          <tr>
            <td align="right" nowrap>
              <g:link controller="administrators" action="popdiredit" class="button-glossy orange">Добавить направление</g:link>
              <input type="submit" class="button-glossy green" id="find_direction_submit_button" value="Показать" style="margin:0 10px">
              <input type="button" class="button-glossy grey" value="Сброс" onClick="resetData();$('find_direction_submit_button').click();"/>
            </td>
          </tr>
        </table>   
      </g:formRemote>
    </div>
    <div id="companystat"></div>
  </body>
</html>
