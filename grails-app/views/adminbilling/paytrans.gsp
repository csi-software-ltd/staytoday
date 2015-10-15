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
      function resetDate(){
        $('moddate_from').setValue('');
        $('moddate_to').setValue('');
        $('moddate_from_year').setValue('');
        $('moddate_to_year').setValue('');
        $('moddate_from_month').setValue('');
        $('moddate_to_month').setValue('');
        $('moddate_from_day').setValue('');
        $('moddate_to_day').setValue('');
      }
    </g:javascript>  
  </head>  
  <body onload="\$('form_submit_button').click();">
    <div id="paytrans" align="center">
      <g:formRemote name="allForm" url="[action:'paytranslist']" update="[success:'paytranslist']">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:20px 5px">
        <tr>
          <td colspan="2" nowrap>
            <span id="payorder_id" nowrap>
              <label for="payorder_id">№ заказа:</label>
              <input type="text" name="payorder_id" style="width:80px" value="${inrequest?.payorder_id?:''}">
            </span>&nbsp;&nbsp;
            <span id="account_id" nowrap>
              <label for="account_id">Номер счета владельца:</label>
              <input type="text" name="account_id" style="width:80px" value="${inrequest?.account_id?:''}">
            </span>&nbsp;&nbsp;
            <span id="client_id" nowrap>
              <label for="client_id">Владелец:</label>
              <input type="text" name="client_id" style="width:80px" value="${inrequest?.client_id?:''}">
            </span>
          </td>
        </tr>
        <tr>
          <td colspan="2" valign="middle" nowrap>
            <span id="paytype_id" nowrap>
              <label for="paytype_id">Тип транзакции:</label>
              <select name="paytype_id">
                <option value="0" <g:if test="${!inrequest?.paytype_id}">selected="selected"</g:if>>все</option>
              <g:each in="${paytype}">
                <option value="${it.id}" <g:if test="${inrequest?.paytype_id==it.id}">selected="selected"</g:if>>${it.name}</option>
              </g:each>
              </select>
            </span>&nbsp;&nbsp;
            <span id="modstatus" nowrap>
              <label for="modstatus">Статус транзакции:</label>
              <select name="modstatus">
                <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>все</option>
                <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>необработана</option>
                <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>обработана</option>
              </select>
            </span>
          </td>
        </tr>
        <tr>
          <td valign="middle" nowrap>
            <label for="moddate_from">Дата транзакции с:</label>
            <calendar:datePicker name="moddate_from" needDisable="false" dateFormat="%d-%m-%Y"  value=""/>
            <label for="moddate_to">&nbsp;по:</label>
            <calendar:datePicker name="moddate_to" needDisable="false" dateFormat="%d-%m-%Y"  value=""/>
          </td>
          <td align="right" nowrap>
            <input type="submit" class="button-glossy green" id="form_submit_button" value="Показать" style="margin-right:5px">
            <input type="reset" class="button-glossy grey" value="Сброс" onClick="resetDate();return true;"/>
          </td>
        </tr>
      </table>
      </g:formRemote>
    </div>
    <div id="paytranslist"></div>
  </body>
</html>
