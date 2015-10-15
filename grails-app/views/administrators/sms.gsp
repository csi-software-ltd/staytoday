<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <style type="text/css">
      @media all and (-webkit-min-device-pixel-ratio:10000),not all and (-webkit-min-device-pixel-ratio:0) {
        .glossy td { font-size: 15px; }
      }
    </style>    
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
      $('user_id').setValue('');
      $('status').setValue('');
      $('inputdate').setValue('');
      $('inputdate_year').setValue('');
      $('inputdate_month').setValue('');
      $('inputdate_day').setValue('');
      $('inputdate_value').setValue('');
    }
    </g:javascript>
  </head>

	<body onload="initialize()">
  
    <div id="homelist">
      <g:formRemote name="allForm" url="[action:'smslist']" update="[success:'companystat']">
      <input type="hidden" name="stat" id="stat" value="direction">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:20px 5px">
        <tr>
          <td colspan="2" nowrap>
            <span nowrap>
              <label for="z_id">Код пользователя:</label>
              <input type="text" id="user_id" name="user_id" value="${inrequest?.user_id}" style="width:80px">
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="city">Статус SMS:</label>
              <input type="text" id="status" name="status" value="${inrequest?.status}" style="width:170px">
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="inputdate">Дата создания:</label>
              <calendar:datePicker name="inputdate" needDisable="false" dateFormat="%d-%m-%Y" value="${inrequest?.inputdate}"/>
            </span>&nbsp;&nbsp;
          </td>
        </tr>
        <tr>
          <td colspan="2" align="right" nowrap>
            <input type="submit" class="button-glossy green" id="user_submit_button" value="Показать"/>
            <input type="button" class="button-glossy grey" value="Сброс" onclick="resetData()" style="margin:0 10px"/>
          </td>
        </tr>
      </table>
      </g:formRemote>
        
      <div id="companystat"></div>
    </div>
  </body>
</html>
