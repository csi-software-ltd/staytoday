  <div class="notice">
    Вы не робот? Введите код подтверждения
  </div>
  <g:form name="captchaForm" controller="index" action="spy_protection" method="POST" useToken="true">    
    <table cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td><jcaptcha:jpeg name="image"/></td>			
        <td><input type="text" name="captcha" value=""/></td>
      </tr>
      <tr>
        <td style="width:230px"><input type="submit" class="button-glossy orange" value="Отправить"/></td>
        <td><input type="reset" class="button-glossy grey" value="Очистить форму" onClick="clearForm()"/></td>
      </tr>
    </table>      
  </g:form>
