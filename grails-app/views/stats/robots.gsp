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
      function resetData(){
        $('requesttime').setValue('');
        $('requesttime_year').setValue('');
        $('requesttime_month').setValue('');
        $('requesttime_day').setValue('');
        $('requesttime_value').setValue('');
      }
    </g:javascript>
  </head>  
  <body onload="\$('stat_submit_button').click()">
    <div align="center">
      <table cellpadding="5">
        <tr>
          <td><g:link style="color:black" controller="stats" action="index" id="0"><h3><u>Ключевые слова</u></h3></g:link></td>
          <td><g:link style="color:black" controller="stats" action="index" id="1"><h3><u>Разделы</u></h3></g:link></td>
          <td><g:link style="color:black" controller="stats" action="index" id="2"><h3><u>Сервисы</u></h3></g:link></td>
          <td><g:link style="color:black" controller="stats" action="index" id="3"><h3><u>Объявления</u></h3></g:link></td>
          <td><g:link style="color:black" controller="stats" action="index" id="4"><h3><u>Разное</u></h3></g:link></td>
          <td><g:link style="color:#0080F0" controller="stats" action="robots"><h3><u>Роботы</u></h3></g:link></td>
        </tr>
      </table>
    </div>
    <div id="sitestat">
      <div>
        <g:formRemote name="allForm" url="[action:'robotstat']" update="[success:'placeList']">
          <input type="hidden" name="stat" id="stat" value="direction">
          <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
            <tr>
              <td nowrap>
                <span nowrap>
                  <label for="city_name">Город:</label>
                  <input type="text" id="city_name" name="city_name" style="width:180px">
                </span>&nbsp;&nbsp;
                <span nowrap>
                  <label for="type">Тип листинга:</label>
                  <select id="type" name="type">
                    <option value="0">все</option>
                    <option value="31">комнаты</option>
                    <option value="32">метро</option>
                    <option value="65">типы</option>
                    <option value="66">ориентиры</option>
                    <option value="67">чистый(жильё)</option>
                  </select>
                </span>&nbsp;&nbsp;
                <span nowrap>
                  <label for="requesttime">Дата посещения:</label>
                  <calendar:datePicker name="requesttime" needDisable="false" dateFormat="%d-%m-%Y"/>
                </span>&nbsp;&nbsp;
              </td>
            </tr>
            <tr>
              <td align="right" nowrap>
                <input type="submit" class="button-glossy green" id="stat_submit_button" value="Показать" style="margin-right:10px">
                <input type="reset" class="button-glossy grey" value="Сброс" onclick="resetData()"/>
              </td>
            </tr>
          </table>
        </g:formRemote>
      </div>
      <div id="placeList" align="left">
      </div>
    </div>
  </body>
</html>