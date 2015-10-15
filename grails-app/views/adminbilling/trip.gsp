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
      function readevent(iId){
        <g:remoteFunction controller='adminbilling' action='readtripevent' onSuccess="\$('trip_submit_button').click()" params="'id='+iId" />
      }
      function acceptbron(iId){
        <g:remoteFunction controller='adminbilling' action='accepttrip' onSuccess="\$('trip_submit_button').click()" params="'id='+iId" />
      }
    </g:javascript>
  </head>
	<body onload="\$('trip_submit_button').click()">
    <div id="reviews">
      <g:formRemote name="tripForm" url="[action:'triplist']" update="[success:'trip_list']">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
          <tr>
            <td nowrap>
              <label for="owner_id">Код владельца:</label>
              <input type="text" id="owner_id" name="owner_id" value="${inrequest?.owner_id}" style="width:80px">
              <label for="user_id">Код пользователя:</label>
              <input type="text" id="user_id" name="user_id" value="${inrequest?.user_id}" style="width:80px">
              <label for="home_id">Код объявления:</label>
              <input type="text" id="home_id" name="home_id" value="${inrequest?.home_id}" style="width:80px">
              <label for="modstatus">Статус поездки:</label>
              <select id="modstatus" name="modstatus">
                <option value="-2" <g:if test="${inrequest?.modstatus==-2}">selected="selected"</g:if>>все</option>
                <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>недействительные</option>
                <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>неподтвержденные</option>
                <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>предстоящие</option>
                <option value="2" <g:if test="${inrequest?.modstatus==2}">selected="selected"</g:if>>текущие</option>
                <option value="3" <g:if test="${inrequest?.modstatus==3}">selected="selected"</g:if>>прошлые</option>
              </select>
            </td>
          </tr>
          <tr>
            <td nowrap>
              <label for="paystatus">Статус оплаты:</label>
              <select id="paystatus" name="paystatus">
                <option value="-2" <g:if test="${inrequest?.paystatus==-2}">selected="selected"</g:if>>все</option>
                <option value="-1" <g:if test="${inrequest?.paystatus==-1}">selected="selected"</g:if>>оплачено по недействительному заказу</option>
                <option value="0" <g:if test="${inrequest?.paystatus==0}">selected="selected"</g:if>>не требуется</option>
                <option value="1" <g:if test="${inrequest?.paystatus==1}">selected="selected"</g:if>>оплачено</option>
                <option value="2" <g:if test="${inrequest?.paystatus==2}">selected="selected"</g:if>>сделка завершена</option>
                <option value="3" <g:if test="${inrequest?.paystatus==3}">selected="selected"</g:if>>отказ от заезда</option>
              </select>&nbsp;&nbsp;
              <label for="order">Сортировать по:</label>
              <select id="order" name="order">
                <option value="0" <g:if test="${!inrequest?.order}">selected="selected"</g:if>>дате создания</option>
                <option value="1" <g:if test="${inrequest?.order==1}">selected="selected"</g:if>>дате заезда</option>
              </select>
            </td>
          </tr>
          <tr>
            <td align="right" nowrap>
              <input type="submit" class="button-glossy green" id="trip_submit_button" value="Показать" />
              <input type="reset" class="button-glossy grey" value="Сброс" onclick="resetData()" style="margin: 0 5px"/>
            </td>
          </tr>
        </table>
      </g:formRemote>
    </div>   
    <div id="trip_list"></div>
  </body>
</html>
