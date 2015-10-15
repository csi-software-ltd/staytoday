<html>
  <head>
  <title>Административное приложение StayToday.ru</title>
  <meta name="layout" content="admin" />
  <g:javascript>    	
    function returnToList(){
	  $("returnToListForm").submit();
    }
	</g:javascript>
  </head>  
  <body>

  <g:if test="${flash?.save_error}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <g:if test="${flash?.save_error==101}"><li>Непоправимая ошибка. Данные не сохранены.</li></g:if>
      </ul>
    </div>
  </g:if>    
  <g:form name="notetypeeditForm" url="${[controller:'administrators',action:'notetypesave', id:inrequest.id]}" method="POST">
    <input type="hidden" id="modstatus" name="modstatus" value="${inrequest?.modstatus}">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td colspan="2"><g:link action="notetype" class="to-parent">К списку уведомлений</g:link></td>
      </tr>
      <tr>
        <td colspan="2"><h1 class="blue">Редактирование уведомления &laquo;${inrequest?.name}&raquo;</h1></td>
      </tr>
      <tr>
        <td width="160">Название:</td>
        <td><input type="text" name="name" style="width:100%" value="${inrequest?.name}"/>        
      </tr>
      <tr>
        <td>Текст:</td>
        <td><textarea rows="5" cols="40" name="notetext" style="width:99%">${inrequest?.notetext}</textarea>        
      </tr>
      <tr>
        <td>Текст EN:</td>
        <td><textarea rows="5" cols="40" name="notetext_en" style="width:99%">${inrequest?.notetext_en}</textarea>        
      </tr>
      <tr>
        <td nowrap>Шаблон email:</td>
        <td>
          <select name="email_template_id">
            <option value="0"></option>
            <g:each in="${email_template}">
              <option value="${it?.id}" <g:if test="${inrequest?.email_template_id==it?.id}">selected="selected"</g:if>>${it?.name}</option>
            </g:each>
          </select>
        </td>        
      </tr>
      <tr>
        <td colspan="2">
          <label for="max_notes">Максимальное кол-во сообщений:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
          <input class="price" type="text" name="max_notes" value="${inrequest?.max_notes}"/>        
        </td>
      </tr>
      <tr>
        <td colspan="2">
          <label for="dayinterval">Интервал отправки сообщений, дней:</label>
          <input class="price" type="text" name="dayinterval" value="${inrequest?.dayinterval}"/>        
        </td>
      </tr>
      <tr>
        <td colspan="2">
          <label for="param">Параметр критерия:&nbsp;&nbsp;&nbsp;&nbsp;</label>
          <input class="price" type="text" name="param" value="${inrequest?.param}"/>        
        </td>
      </tr>	  
      <tr>
        <td colspan="2" align="right">
          <input type="submit" class="button-glossy green" value="Сохранить" style="margin-right:10px"/>
        <g:if test="${!inrequest?.modstatus}">
          <input type="submit" class="button-glossy lightblue" value="Активировать" onClick="$('modstatus').value=1;$('notetypeeditForm').submit();">
        </g:if><g:else>
          <input type="submit" class="button-glossy red" value="Деактивировать" onClick="$('modstatus').value=0;$('notetypeeditForm').submit();">		  
        </g:else>
        </td>
      </tr>
    </table>
  </g:form>      
  </body>
</html>
