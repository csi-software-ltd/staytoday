<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.wapforum.org/DTD/xhtml-mobile10.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="${context?.lang?'en':'ru'}" lang="${context?.lang?'en':'ru'}">
  <head>
    <title>${Infotext.findByControllerAndAction('error','page_404')['title'+context?.lang]}</title>
    <meta http-equiv="content-language" content="${context?.lang?'en':'ru'}" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />        
    <meta name="robots" content="index,follow,noarchive" />
    <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no" />
    <meta name="HandheldFriendly" content="true" />
    <meta name="PalmComputingPlatform" content="true" />
    <meta name="MobileOptimized" content="176" />
    <meta name="format-detection" content="telephone=no" />    
    <meta name="apple-mobile-web-app-capable" content="yes" />    
    <link rel="shortcut icon" type="image/x-icon" href="${resource(file:'favicon.ico',absolute:true)}" />    
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'jquery.mobile-1.2.0.css')}" />    
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'mobile.css')}" />
    <g:javascript src="links/link.js" />    
    <g:javascript src="jquery-1.8.3.js" />
    <g:javascript src="jquery.mobile-1.2.0.min.js" />
    <g:javascript src="application.js" />
    <g:javascript src="prototype/prototype.js" />
    <script type="text/javascript">          
      jQuery.mobile.defaultPageTransition = 'none';
      jQuery.mobile.defaultDialogTransition = 'none';
      jQuery.mobile.ajaxEnabled = false;
    </script>
  </head>
  <body>
    <div data-role="page">
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="text" role="heading">
              <a class="icon logo" href="${createLink(uri:'',base:context?.mobileURL_lang)}" title="${message(code:'label.main')}"></a>
            </li>
            <li class="divider-vertical"></li>          
            <li class="button_main">
              <a class="icon search" href="${createLink(action:'list',params:inrequest,base:context?.mobileURL_lang)}" title="${message(code:'button.search')}"></a>
            </li>
            <li class="divider-vertical"></li>
            <li class="button_user">
              <g:if test="${user}"><a class="icon logout" href="${createLink(action:'logout',base:context?.mobileURL_lang)}" title="${message(code:'label.logout')}"></a></g:if>
              <g:else><a id="login_link" class="icon login" href="${createLink(uri:'/auth'+(actionName!='auth'?'?from='+actionName:''),base:context?.mobileURL_lang)}" title="${message(code:'label.login')}"></a></g:else>
            </li>
            <li class="divider-vertical"></li>
            <li class="button_favorites <g:if test="${(wallet?:[]).size()>0}">starred</g:if>">
              <a id="favorite" class="icon favorite ${!user?'no_active':''}" href="${createLink(uri:'/favorites',base:context?.mobileURL_lang)}" title="${message(code:'label.favorite')}"></a>              
            </li>
            <li class="divider-vertical"></li>
            <li class="button_inbox <g:if test="${waiting_unread_count>0}">actively</g:if>">
            <g:if test="${user}">
              <a class="icon inbox" href="${createLink(uri:(msg_unread_count==1?'/mbox':'/inbox')+(msg_unread_count==1?'?id='+msg_unread_id:''),base:context?.mobileURL_lang)}" title="${message(code:'label.inbox')}">
                <g:if test="${msg_unread_count}"><div class="unread_count">${msg_unread_count}</div></g:if>
              </a>
            </g:if>
            </li>            
          </ul>
        </div>
      </div>  
      <h1 role="heading">${Infotext.findByControllerAndAction('error','page_404')['header'+context?.lang]}</h1>
      <div data-role="content">
        <g:rawHtml>${Infotext.findByControllerAndAction('error','page_404')['itext'+context?.lang]}</g:rawHtml>
      </div>   
    </div>
  </body>
</html>
