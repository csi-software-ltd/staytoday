<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="${context?.lang?'en':'ru'}" lang="${context?.lang?'en':'ru'}">
  <head>
    <title><g:layoutTitle default="StayToday.ru" /></title>  
    <meta http-equiv="content-language" content="${context?.lang?'en':'ru'}" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta name="copyright" content="StayToday" />    
    <meta name="resource-type" content="document" />
    <meta name="document-state" content="dynamic" />
    <meta name="revisit" content="1" />
    <meta name="viewport" content="width=1000,maximum-scale=1.0" />
    <meta name="robots" content="index,follow,noarchive" />        
    <meta name="yandex-verification" content="7be9d97fe94c27b7" />
    <meta name="google-site-verification" content="6QeMPuVWeP1oOZ-4c7l-6oB9ZB0x2un7ZNG01Oy5z64" />    
    <meta name="alexaVerifyID" content="dzhfoUuqrBO_36HG1hBK_h70XGg" />   
    <g:layoutHead />
    <link rel="shortcut icon" href="${resource(file:'favicon.ico',absolute:true)}" type="image/x-icon" />  
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'main.css')}" />
    <g:javascript library="jquery-1.8.3" />
    <g:javascript library="prototype" />
    <g:if test="${(controllerName=='home' && actionName in ['searchprint','viewprint'])}">
      <script src="http://api-maps.yandex.ru/2.0/?load=package.standard&lang=${context?.lang?'en-US':'ru-RU'}" type="text/javascript"></script>
    </g:if>
    <g:elseif test="${(controllerName=='home' && actionName =='traceprint')}">  
      <script src="http://api-maps.yandex.ru/2.0/?load=package.full&lang=${context?.lang?'en-US':'ru-RU'}" type="text/javascript"></script>
    </g:elseif>  
    <r:layoutResources/>
  </head>
  <body onload="${pageProperty(name:'body.onload')}">  
    <table class="line" width="100%" height="100%" cellpadding="0" cellspacing="0" border="0" align="center">
      <tr>
        <td width="100%">
          <table width="980" cellpadding="0" cellspacing="0" border="0" align="center">
            <tr style="height:140px">
              <td width="250" valign="middle" class="logo">
                <a title="StayToday — ${message(code:'print.main_page')}" href="${context?.mainserverURL_lang}">
                  <img src="${resource(dir:'images',file:'logo.png')}" alt="${message(code:'faq.notice.main')}" border="0" />
                </a>              
              </td>
              <td class="slog padd10">${message(code:'main.slogan')}</td>
            </tr>            
            <g:layoutBody />
            <tr style="height:20px">
              <td colspan="2">&nbsp;</td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
    <r:layoutResources/>
  </body>
</html>
