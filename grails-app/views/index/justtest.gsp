<%@ page contentType="text/html;charset=UTF-8" %>
<html><head>
</head>
<body>
	                      <form name="settinsForm" method="get" action="https://www.sandbox.paypal.com/cgi-bin/webscr">
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                          <tr>
                            <td colspan="2" style="padding:20px">
			                        <input type="hidden" name="cmd" value="_express-checkout">
			                        <input type="hidden" name="token" value="${resp.TOKEN}">
                              <input type="submit" class="button-glossy orange" value="${message(code:'button.pay')}"/>
                            </td>
                          </tr>
                        </table>
                      </form>
</body>
</html>