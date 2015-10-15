<html>
  <head>
    <title>Административное приложение StayToday</title>
    <meta name="layout" content="admin" />
    <g:javascript>
    function initialize(){	  
	  <g:if test="${temp_notification!=null}">
         alert('${temp_notification?.text}');	  
	  </g:if>      	  
    }
    </g:javascript>
  </head>  
  <body onload="initialize()">

  <g:if test="${flash?.error}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <g:if test="${flash?.error==1}"><li>Вы не заполнили обязательное поле &laquo;Пароль&raquo;</li></g:if>
        <g:if test="${flash?.error==2}"><li>Пароли не совпадают</li></g:if>
        <g:if test="${flash?.error==3}"><li>Слишком короткий пароль</li></g:if>
      </ul>
    </div>
  </g:if>

  <div class="your-space drop_shadow" style="padding-top:10px">
    <div class="button-glossy green header form">
      <h1>Профиль пользователя</h1>
    </div>
    <g:form url="[controller:'administrators',action:'profilesave']" method="POST">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td width="60"><label>Логин:</label></td>
        <td><input type="text" readonly value="${admin?.login}"/></td>
        <td width="60"><label>Группа:</label></td>
        <td width="230"><input type="text" style="width:100%" readonly value="${groupname}"/></td>
      </tr>
      <tr>
        <td><label for="name">Имя:</label></td>
        <td><input type="text" name="name" value="${administrator?.name}"/></td>
        <td><label for="email">Email:</label></td>
        <td><input type="text" name="email" style="width:100%" value="${administrator?.email }"/></td>
      </tr>
      <tr>
        <td colspan="2">
        <g:if test="${false}">
          <g:remoteLink class="button-glossy red" action="resizeExistingThumbs" onLoading="\$('calcratingprocess').show();" onLoaded="\$('calcratingprocess').hide();" onSuccess="alert('Диск успешно отформатирован.');" >Не нажимать!</g:remoteLink>
          <span id="calcratingprocess" style="display:none"><img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/></span>
        </g:if>
        </td>
        <td></td>
        <td><input type="submit" class="button-glossy green" value="Изменить профиль"/></td>
      </tr>    
    </table>
    </g:form>
    <hr/>
    <g:form url="[controller:'administrators',action:'changepass']" method="POST">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td width="110"><label for="pass">Новый пароль:</label></td>
        <td><input type="password" style="width:100%" name="pass"/></td>
        <td></td>
      </tr>
      <tr>
        <td><label for="confirm_pass">Повторить:</label></td>
        <td><input type="password" style="width:100%" name="confirm_pass"/></td>
        <td width="225"><input type="submit" class="button-glossy green" value="Изменить пароль" style="width:218px"/></td>
      </tr>
    </table>
    <hr/>
    <small><i>
      Последний вход пользователя: <b>${(lastlog?.logtime!=null)?String.format('%td.%<tm.%<tY %<tH:%<tM',lastlog?.logtime):''}</b> с IP адреса <b>${lastlog?.ip}</b>
    <g:if test="${(unsuccess_log_amount)&&(unsuccess_log_amount > unsucess_limit)}">
      <br/><font color="red">Неуспешных попыток доступа за последние 7 дней: <b>${unsuccess_log_amount}</b></font>
    </g:if>
    </i></small>
    </g:form>
  </div>
  </body>
</html>
