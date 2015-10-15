<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript library="jquery-1.8.3" />    
    <g:javascript library="jquery.colorbox.min" />    
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
        $('modify_date_from').setValue('');
        $('modify_date_to').setValue('');	
        $('pay_date_from').setValue('');
        $('pay_date_to').setValue('');
        $('modify_date_from_year').setValue('');
        $('modify_date_to_year').setValue('');
        $('modify_date_from_month').setValue('');
        $('modify_date_to_month').setValue('');
        $('modify_date_from_day').setValue('');
        $('modify_date_to_day').setValue('');
        $('pay_date_from_year').setValue('');
        $('pay_date_to_year').setValue('');
        $('pay_date_from_month').setValue('');
        $('pay_date_to_month').setValue('');
        $('pay_date_from_day').setValue('');
        $('pay_date_to_day').setValue('');
      }
    </g:javascript>  
  </head>  
  <body onload="\$('form_submit_button').click();">
    <div id="paytransfer" align="center">
      <g:formRemote name="allForm" url="[action:'paytransferlist']" update="[success:'paytransferlist']">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:20px 5px">
          <tr>
            <td nowrap>
              <span id="transfer_id" nowrap>
                <label for="transfer_id">Код:</label>
                <input type="text" name="transfer_id" class="price">
              </span>&nbsp;&nbsp;          
              <span id="payorder_id" nowrap>
                <label for="payorder_id">Код заказа:</label>
                <input type="text" name="payorder_id" style="width:160px">
              </span>
            </td>
            <td>
              <span id="status" nowrap>
                <label for="modstatus">Cтатус обработки:</label>
                <select name="modstatus">
                  <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>все</option>
                  <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>необработанно</option>
                  <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>обработанно</option>
                  <option value="2" <g:if test="${inrequest?.modstatus==2}">selected="selected"</g:if>>нераспознанно</option>
                </select>
              </span>&nbsp;&nbsp;
              <span id="paytype" nowrap>
                <label for="paytype">Тип операции:</label>
                <select name="paytype">
                  <option value="-1" <g:if test="${inrequest?.paytype==-1}">selected="selected"</g:if>>все</option>
                  <option value="0" <g:if test="${inrequest?.paytype==0}">selected="selected"</g:if>>неопределено</option>
                  <option value="1" <g:if test="${inrequest?.paytype==1}">selected="selected"</g:if>>приход</option>
                  <option value="2" <g:if test="${inrequest?.paytype==2}">selected="selected"</g:if>>возврат</option>
                  <option value="3" <g:if test="${inrequest?.paytype==3}">selected="selected"</g:if>>вывод</option>
                </select>
              </span>
            </td>
          </tr>
          <tr>
            <td valign="middle" nowrap>
              <label for="pay_date_from">Дата оплаты с:</label>
              <calendar:datePicker name="pay_date_from" needDisable="false" dateFormat="%d-%m-%Y"  value=""/>
              <label for="pay_date_to">&nbsp;по:</label>
              <calendar:datePicker name="pay_date_to" needDisable="false" dateFormat="%d-%m-%Y"  value=""/>
            </td>
            <td valign="middle" nowrap>
              <label for="modify_date_from">Дата модификации с:</label>
              <calendar:datePicker name="modify_date_from" needDisable="false" dateFormat="%d-%m-%Y"  value=""/>
              <label for="modify_date_to">&nbsp;по:</label>
              <calendar:datePicker name="modify_date_to" needDisable="false" dateFormat="%d-%m-%Y"  value=""/>
            </td>
          </tr>
          <tr style="height:40px"><td colspan="2"></td></tr>
          <tr>
            <td>&nbsp;</td>
            <td style="padding-right:20px" align="right" nowrap>
              <input type="submit" class="button-glossy green" id="form_submit_button" value="Показать" style="margin-right:5px">
              <input type="reset" class="button-glossy grey" value="Сброс" onClick="resetDate();return true;"/>
            </td>
          </tr>
        </table>
      </g:formRemote>
      <div class="clearfix" align="left" style="margin:-100px 10px 0">
        <g:form name="import" method="post" url="${[action:'paytransferimportCSV']}" enctype="multipart/form-data" target="upload_target">
          <label for="file">Импорт из Банк-клиента(CSV):</label>
          <input type="file" name="file" id="file" size="23" accept="text/csv" onchange="$('CSVimport_submit_button').click()"/>
          <input type="submit" id="CSVimport_submit_button" style="display:none">
        </g:form>
      </div>
      <div class="clearfix" align="right" style="margin:-30px 10px 0">
        <g:form name="import" method="post" url="${[action:'paytransferimportWM']}" enctype="multipart/form-data" target="upload_target">
          <label for="file">Импорт WM выписки:</label>
          <input type="file" name="file" id="file" size="23" accept="application/vnd.ms-excel" onchange="$('WMimport_submit_button').click()"/>
          <input type="submit" id="WMimport_submit_button" style="display:none">
        </g:form>
      </div>
      <iframe id="upload_target" name="upload_target" src="#" style="width:900px;height:40px;border:none;"></iframe>
    </div>
    <div id="paytransferlist" style="margin-top:30px"></div>
  </body>
</html>
