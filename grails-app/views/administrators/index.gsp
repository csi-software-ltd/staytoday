<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
  </head>  
  <body>    
  <g:if test="${redir}">
    <g:form url="[controller:'administrators',action:'index']" method="POST" name="indexForm" id="indexForm">
      <input type="submit" style="display:none;">
    </g:form>
    <script>
      eval("$('indexForm').submit();");
    </script>
  </g:if>
  <g:else>
    <g:if test="${flash?.error}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>    
      <ul>
        <g:if test="${flash.error==1}"><li>Не введен логин</li></g:if>
        <g:elseif test="${flash.error==2}"><li>Пароль введен неверно, или администратора с таким логином не существует</li></g:elseif>
        <g:elseif test="${flash.error==3}"><li>Доступ временно заблокирован</li></g:elseif>      
      </ul>
    </div>
    </g:if>
    
    <div class="your-space drop_shadow" style="padding-top:10px;width:422px">
      <div class="button-glossy green header form" style="width:400px">
        <h1>Войти в панель управления</h1>
      </div>
      <g:form url="[controller:'administrators',action:'login']" method="POST">
        <table cellpadding="5" cellspacing="5" border="0">
          <tr>
            <td width="80"><label for="login">Логин:</label></td>
            <td><input type="text" name="login" id="login"></td>
            <td></td>
          </tr>
          <tr>
            <td><label for="password">Пароль:</label></td>
            <td><input type="password" name="password"></td>
            <td><input type="submit" class="button-glossy green" value="Войти"></td>
          </tr>
        </table>
      </g:form>    
    </div>
    </g:else>
  </body>
</html>
