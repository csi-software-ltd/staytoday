<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
      function initialize(){
        $('article_submit_button').click();
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
		    $('modstatus').selectedIndex = 0;
        $('article_id').setValue('');
        $('article_title').setValue('');
        $('inputdate').setValue('');
        $('inputdate_year').setValue('');
        $('inputdate_month').setValue('');
        $('inputdate_day').setValue('');
        $('inputdate_value').setValue('');
      }
    </g:javascript>
  </head>
	<body onload="initialize()">
    <div id="articles">
      <g:formRemote name="articleForm" url="[action:'articlelist']" update="[success:'article_list']">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
        <tr>
          <td nowrap>
            <span nowrap>
              <label for="article_id">Код:</label>
              <input type="text" id="article_id" name="id" value="${inrequest?.id}" style="width:60px">
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="article_title">Название:</label>
              <input type="text" id="article_title" name="title" value="${inrequest?.title}" style="width:260px">
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="inputdate">Дата создания:</label>
              <calendar:datePicker name="inputdate" needDisable="false" dateFormat="%d-%m-%Y" value="${inrequest?.inputdate}"/>
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="modstatus">Статус:</label>
              <select id="modstatus" name="modstatus">
                <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>все</option>
                <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>неактивные</option>
                <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>активные</option>
              </select>                        
            </span>
          </td>
        </tr>
        <tr>
          <td align="right" nowrap>
            <g:link class="button-glossy orange" controller="administrators" action="articleDetails">Добавить новую</g:link>
            <input type="submit" class="button-glossy green" id="article_submit_button" value="Показать" style="margin: 0 10px" />
            <input type="button" class="button-glossy grey" value="Сброс" onClick="resetData()"/>
          </td>
        </tr>
      </table>   
      </g:formRemote>
    </div>
    
    <div id="article_list"></div>
  </body>
</html>
