<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.wapforum.org/DTD/xhtml-mobile10.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="${context?.lang?'en':'ru'}" lang="${context?.lang?'en':'ru'}">
  <head>
    <title><g:layoutTitle default="Мобильная версия StayToday"/> | StayToday Mobile</title>
    <meta http-equiv="content-language" content="${context?.lang?'en':'ru'}" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />    
    <meta http-equiv="cache-control" content="no-cache" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta http-equiv="msthemecompatible" content="no" />
    <meta http-equiv="cleartype" content="on" />
    <meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0,user-scalable=no,maximum-scale=1.0" />
    <meta name="HandheldFriendly" content="true" />
    <meta name="PalmComputingPlatform" content="true" />
    <meta name="MobileOptimized" content="176" />
    <meta name="format-detection" content="telephone=no" />
    <meta name="format-detection" content="address=no" />
    <meta name="apple-mobile-web-app-capable" content="yes" />    
  <g:if test="${context?.serverURL.contains('alpha.trace.ru')}">
    <meta name="robots" content="noindex,nofollow,noarchive" />
  </g:if><g:else>
    <meta name="robots" content="index,follow,noarchive" />
  </g:else><g:if test="${!(actionName in ['list','detail','index'])}">
    <link rel="alternate" hreflang="ru" href="${context.curl_ru}<g:rawHtml>${(context.cquery?'?'+context.cquery:'')}</g:rawHtml>" />
    <link rel="alternate" hreflang="en" href="${context.curl_en}<g:rawHtml>${(context.cquery?'?'+context.cquery:'')}</g:rawHtml>" />
  </g:if>
    <link rel="shortcut icon" type="image/x-icon" href="${resource(file:'favicon.ico',absolute:true)}" />    
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'jquery.mobile-1.2.0.css')}" />    
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'mobile.css')}" />
  <g:if test="${actionName in ['index','detail','mbox']}">
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'mobipick.css')}" />
  </g:if><g:if test="${actionName=='detail'}">
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'royalslider.css')}" />    
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'rs-default.css')}" />
  </g:if>
    <g:javascript src="links/link.js" />    
    <g:javascript src="jquery-1.8.3.js" />
    <g:javascript src="jquery.mobile-1.2.0.min.js" />
  <g:if test="${actionName in ['index','list','detail']}">
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true&language=${context?.lang?'en':'ru'}"></script>
    <g:javascript src="mobile/jquery.ui.map.min.js" />
    <g:javascript src="mobile/jquery.ui.map.services.min.js" />    
  </g:if><g:if test="${actionName in ['index','detail','mbox']}">
    <g:javascript src="mobile/xdate.js" />
    <g:javascript src="mobile/xdate.i18n.js" />    
    <g:javascript src="mobile/mobipick.js" />
  </g:if><g:if test="${actionName=='detail'}">
    <g:javascript src="mobile/jquery.royalslider.min.js" />
  </g:if>
    <g:javascript src="application.js" />
    <g:javascript src="prototype/prototype.js" />
  <g:if test="${actionName=='index'}">    
    <g:javascript src="prototype/autocomplete.js" />    
  </g:if>
    <script type="text/javascript">          
      jQuery.mobile.defaultPageTransition = 'none';
      jQuery.mobile.defaultDialogTransition = 'none';
      jQuery.mobile.ajaxEnabled = false;
    </script>
    <g:layoutHead />    
    <!--<r:layoutResources/>-->
  </head>
  <body onload="${pageProperty(name:'body.onload')}">
  <g:if test="${!(actionName in ['detail','mbox','bron','page_404','about','terms','help','contract','liqpay','liqpayresult','payU','payuipn','payUresult','wmfail','wmoney','wmsuccess'])}">
    <div data-role="page"> 
      <div id="page_header" data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="text" role="heading">
            <g:if test="${actionName=='index'}"><span class="icon logo" title="${message(code:'label.main')}"></span></g:if>
            <g:else><a class="icon logo" href="${createLink(uri:'',base:context?.mobileURL_lang)}" title="${message(code:'label.main')}"></a></g:else>
            </li>
            <li class="divider-vertical"></li>          
            <li class="button_main">
              <g:if test="${actionName in ['index','list']}"><span class="icon search" title="${message(code:'button.search')}"></span></g:if>
              <g:else><a class="icon search" href="${createLink(action:'list',params:inrequest,base:context?.mobileURL_lang)}" rel="nofollow" title="${message(code:'button.search')}"></a></g:else>
            </li>
            <li class="divider-vertical"></li>
            <li class="button_user">
              <g:if test="${user}"><a class="icon logout" href="${createLink(action:'logout',base:context?.mobileURL_lang)}" title="${message(code:'label.logout')}"></a></g:if>
              <g:else><a id="login_link" class="icon login" href="${createLink(uri:'/auth'+(actionName!='auth'?'?from='+actionName:''),base:context?.mobileURL_lang)}" rel="nofollow" title="${message(code:'label.login')}"></a></g:else>
            </li>
            <li class="divider-vertical"></li>
            <li class="button_favorites <g:if test="${(wallet?:[]).size()>0}">starred</g:if>">
              <a id="favorite" class="icon favorite ${!user?'no_active':''}" href="${createLink(uri:'/favorites',base:context?.mobileURL_lang)}" rel="nofollow" title="${message(code:'label.favorite')}"></a>
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
      <g:layoutBody />
    </div>
  </g:if><g:else>
    <g:layoutBody />
  </g:else><!--noindex-->
    <noscript>
      <div><img src="//mc.yandex.ru/watch/24517196" style="position:absolute;left:-9999px;" alt="" /></div>
    </noscript>
    <g:javascript>    
      var _gaq = _gaq || [];
      _gaq.push(['_setAccount', 'UA-33193007-1']);
      _gaq.push(['_setDomainName', 'staytoday.ru']);
      _gaq.push(['_trackPageview']);      
      (function() {
        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
      })();
      (function (w, c) {
        (w[c] = w[c] || []).push(function() {
          try {            
            w.yaCounter24517196 = new Ya.Metrika({id:24517196, webvisor:true, clickmap:true, trackLinks:true, accurateTrackBounce:true, trackHash:true});
          } catch(e) { }
        });        
        var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; 
        s.src = (document.location.protocol == 'https:' ? 'https:' : 'http:') + '//mc.yandex.ru/metrika/watch.js';
        var n = document.getElementsByTagName('script')[0], f = function () { n.parentNode.insertBefore(s, n); };        
        if (w.opera == '[object Opera]')
          document.addEventListener('DOMContentLoaded', f, false);
        else 
          f();
      })(window, 'yandex_metrika_callbacks');
    </g:javascript>
    <r:layoutResources/><!--/noindex-->  
  </body>
</html>
