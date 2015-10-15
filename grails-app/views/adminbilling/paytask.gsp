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
    <div id="paytask" align="center">
      <g:formRemote name="allForm" url="[action:'paytasklist']" update="[success:'paytasklist']">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:20px 5px">
          <tr>
            <td colspan="2" valign="middle" nowrap>
              <span id="paytasktype_id" nowrap>
                <label for="paytasktype_id">Тип задания:</label>
                <select name="paytasktype_id">
                  <option value="0" <g:if test="${!inrequest?.paytasktype_id}">selected="selected"</g:if>>все</option>
                <g:each in="${paytasktype}">
                  <option value="${it.id}" <g:if test="${inrequest?.paytasktype_id==it.id}">selected="selected"</g:if>>${it.name}</option>
                </g:each>
                </select>
              </span>&nbsp;&nbsp;
              <span id="modstatus" nowrap>
                <label for="modstatus">Статус задания:</label>
                <select name="modstatus">
                  <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>все</option>
                  <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>необработано</option>
                  <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>обработано</option>
                </select>
              </span>
            </td>
          </tr>
          <tr>
            <td nowrap>
              <span id="paytask_id" nowrap>
                <label for="paytask_id">№ задания:</label>
                <input type="text" name="paytask_id" class="price" value="${inrequest?.paytask_id?:''}">
              </span>&nbsp;&nbsp;
              <span id="paytrans_id" nowrap>
                <label for="paytrans_id">№ транзакции:</label>
                <input type="text" name="paytrans_id" class="price" value="${inrequest?.paytrans_id?:''}">
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
    <div id="paytasklist"></div>
  </body>
</html>
