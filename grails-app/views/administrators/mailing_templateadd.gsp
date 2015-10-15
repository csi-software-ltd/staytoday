<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />   
  </head>  
  <body>
  <g:if test="${flash?.error}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <g:if test="${flash?.error==1}"><li>Вы не заполнили обязательное поле &laquo;Название&raquo;</li></g:if>
      </ul>
    </div>
  </g:if>  
    <div class="your-space drop_shadow" style="padding-top:10px">
      <div class="button-glossy green header form">
        <h1>Добавление нового шаблона</h1>        
      </div>
      <g:form name="saveForm" url="[controller:'administrators',action:'save_mailing_template']" method="POST" useToken="true">      
      <table width="100%" cellpadding="5" cellspacing="5" border="0">
        <tr>
          <td>
            <label for="name">Название:</label>
            <input type="text" id="template_name" name="name" value="${inrequest?.name?:''}" style="width:250px">            
          </td>
          <td nowrap>		
            <label for="type">Тип:</label>
            <select name="type_id">                              
              <option value="1" <g:if test="${inrequest?.type_id==1}">selected="selected"</g:if>>email</option>
              <option value="2" <g:if test="${inrequest?.type_id==2}">selected="selected"</g:if>>sms</option>				  
            </select>			            
          </td>
        </tr>
        <tr>
          <td colspan="2" align="right">
            <input type="submit" class="button-glossy green" value="Добавить" style="margin-right:10px;" />
            <input type="reset" class="button-glossy grey" value="Отмена" onclick="javascript:history.back()" />
          </td>
        </tr>
      </g:form>   
    </div>
  </body>
</html>
