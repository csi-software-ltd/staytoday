<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
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
    </g:javascript>  
  </head>  
  <body onload="\$('form_submit_button').click();">
    <div id="account" align="center">
      <g:formRemote name="allForm" url="[action:'accountlist']" update="[success:'accountlist']">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:20px 5px">
          <tr>
            <td nowrap>
              <span id="code" nowrap>
                <label for="code">Код счета:</label>
                <input type="text" name="code" style="width:80px" value="${inrequest?.code?:''}">
              </span>&nbsp;&nbsp;
              <span id="client_id" nowrap>
                <label for="client_id">Код клиента:</label>
                <input type="text" name="client_id" style="width:80px" value="${inrequest?.client_id?:''}">
              </span>&nbsp;&nbsp;
              <span id="modstatus" nowrap>
                <label for="modstatus">Статус:</label>
                <select name="modstatus">
                  <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>все</option>
                  <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>неактивный</option>
                  <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>активный</option>
                </select>
              </span>
            </td>
            <td align="right" nowrap>
              <input type="submit" class="button-glossy green" id="form_submit_button" value="Показать" style="margin-right:5px">
              <input type="reset" class="button-glossy grey" value="Сброс"/>
            </td>
          </tr>
        </table>
      </g:formRemote>
    </div>
    <div id="accountlist"></div>
  </body>
</html>
