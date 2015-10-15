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
        $('inputdate_from').setValue('');
        $('inputdate_to').setValue('');
        $('inputdate_from_year').setValue('');
        $('inputdate_to_year').setValue('');
        $('inputdate_from_month').setValue('');
        $('inputdate_to_month').setValue('');
        $('inputdate_from_day').setValue('');
        $('inputdate_to_day').setValue('');
      }
    </g:javascript>
  </head>  
  <body onload="\$('form_submit_button').click();">
    <div id="payorder" align="center">
      <g:formRemote name="allForm" url="[action:'payorderlist']" update="[success:'payorderlist']">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:20px 5px">
        <tr>
          <td colspan="2" nowrap>
            <span id="norder" nowrap>
              <label for="norder">№ заказа:</label>
              <input type="text" name="norder" class="price" value="${inrequest?.norder?:''}">
            </span>&nbsp;&nbsp;
            <span id="client_id" nowrap>
              <label for="client_id">Код клиента:</label>
              <input type="text" name="client_id" class="price" value="${inrequest?.client_id?:''}">
            </span>&nbsp;&nbsp;
            <span id="home_id" nowrap>
              <label for="home_id">Код объявления:</label>
              <input type="text" name="home_id" class="price" value="${inrequest?.home_id?:''}">
            </span>&nbsp;&nbsp;
            <span id="plat_name" nowrap>
              <label for="plat_name">Плательщик:</label>
              <input type="text" name="plat_name" value="${inrequest?.plat_name?:''}" style="width:237px" />
            </span>
          </td>
        </tr>
        <tr>
          <td colspan="2" valign="middle" nowrap>
            <span id="status" nowrap>
              <label for="agr_id">Агрегатор:</label>
              <select name="agr_id">
                <option value="0" <g:if test="${!inrequest?.agr_id}">selected="selected"</g:if>>все</option>
              <g:each in="${agr}">
                <option value="${it.id}" <g:if test="${inrequest?.agr_id==it.id}">selected="selected"</g:if>>${it.name}</option>
              </g:each>
              </select>
            </span>&nbsp;&nbsp;
            <span id="payway_id" nowrap>
              <label for="payway_id">Платежное средство:</label>
              <select name="payway_id">
                <option value="0" <g:if test="${!inrequest?.payway_id}">selected="selected"</g:if>>все</option>
              <g:each in="${payway}">
                <option value="${it.id}" <g:if test="${inrequest?.payway_id==it.id}">selected="selected"</g:if>>${it.name}</option>
              </g:each>
              </select>
            </span>&nbsp;&nbsp;
            <span id="modstatus" nowrap>
              <label for="modstatus">Статус заказа:</label>
              <select name="modstatus">
                <option value="-2" <g:if test="${inrequest?.modstatus==-2}">selected="selected"</g:if>>все</option>
                <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>неактивный</option>
                <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>активный</option>
                <option value="2" <g:if test="${inrequest?.modstatus==2}">selected="selected"</g:if>>выполнен</option>
                <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>удален</option>
              </select>
            </span>
          </td>
        </tr>
        <tr>
          <td valign="middle" nowrap>
            <label for="inputdate_from">Дата заказа с:</label>
            <calendar:datePicker name="inputdate_from" needDisable="false" dateFormat="%d-%m-%Y"  value=""/>
            <label for="inputdate_to">&nbsp;по:</label>
            <calendar:datePicker name="inputdate_to" needDisable="false" dateFormat="%d-%m-%Y"  value=""/>
          </td>
          <td align="right" nowrap>
            <input type="submit" class="button-glossy green" id="form_submit_button" value="Показать" style="margin-right:5px">
            <input type="reset" class="button-glossy grey" value="Сброс" onClick="resetDate();return true;"/>
          </td>
        </tr>
      </table>
      </g:formRemote>
    </div>
    <div id="payorderlist"></div>
  </body>
</html>
