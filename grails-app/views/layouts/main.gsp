<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><g:if test="${(controllerName=='index' && actionName in ['index','about','timeline','popdirection','direction','widget','mobile'])||(controllerName=='home' && actionName in ['list','detail'])||(controllerName=='profile' && actionName=='view')}">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="${context?.lang?'en':'ru'}" lang="${context?.lang?'en':'ru'}" xmlns:og="http://ogp.me/ns#" xmlns:fb="http://ogp.me/ns/fb#" <g:if test="${controllerName=='index' && actionName=='index'}">xmlns:website="http://ogp.me/ns/website#"</g:if><g:elseif test="${controllerName=='index' && actionName=='about'}">xmlns:business="http://ogp.me/ns/business#"</g:elseif><g:elseif test="${controllerName=='index' && actionName in ['mobile','widget','direction','popdirection']}">xmlns:place="http://ogp.me/ns/object#"</g:elseif><g:elseif test="${controllerName=='home' && actionName=='list'}">xmlns:place="http://ogp.me/ns/place#"</g:elseif><g:elseif test="${controllerName=='home' && actionName=='detail'}">xmlns:place="http://ogp.me/ns/product#"</g:elseif><g:elseif test="${controllerName=='profile' && actionName=='view'}">xmlns:profile="http://ogp.me/ns/profile#"</g:elseif><g:elseif test="${controllerName=='index' && actionName=='timeline' && inrequest?.id}">xmlns:article="http://ogp.me/ns/article#"</g:elseif>></g:if><g:else>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="${context?.lang?'en':'ru'}" lang="${context?.lang?'en':'ru'}"></g:else>
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
  <g:if test="${context?.serverURL.contains('alpha.trace.ru')}">
    <meta name="robots" content="noindex,nofollow,noarchive" />
    <meta name="yandex-verification" content="59594823c85168bd" />
    <meta name="google-site-verification" content="IqbfMOfL2uen65M_0YfYrn3erQDZqt19AKwHjdDX2DY" />
  </g:if><g:else>
    <meta name="robots" content="index,follow,noarchive" />
    <meta name="yandex-verification" content="7be9d97fe94c27b7" />
    <meta name="google-site-verification" content="6QeMPuVWeP1oOZ-4c7l-6oB9ZB0x2un7ZNG01Oy5z64" />    
    <meta name="alexaVerifyID" content="dzhfoUuqrBO_36HG1hBK_h70XGg" />
    <meta name="cmsmagazine" content="55af4ed6d7e3fafc627c933de458fa04" />
    <meta name="p:domain_verify" content="7112734ad32b4c76f6c5efd3342c2026" />
    <meta name="webmoney.attestation.label" content="webmoney attestation label#72FFF9AE-35C9-4725-AB7A-F8412C160213" />
    <meta name="apple-itunes-app" content="app-id=595979996" />
    <link rel="alternate" href="android-app://ru.trace.staytoday" />
  </g:else><g:if test="${!(controllerName=='home' && actionName in ['list','detail'])}">    
    <link rel="alternate" hreflang="ru" href="${context.curl_ru}<g:rawHtml>${(context.cquery?'?'+context.cquery:'')}</g:rawHtml>" />
    <link rel="alternate" hreflang="en" href="${context.curl_en}<g:rawHtml>${(context.cquery?'?'+context.cquery:'')}</g:rawHtml>" />    
  </g:if>
    <link rel="shortcut icon" type="image/x-icon" href="${resource(file:'favicon.ico',absolute:true)}" />
    <g:layoutHead />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'main.css')}" />
  <g:if test="${controllerName in ['personal','trip','profile','account','inbox','help']}">
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'personal.css')}" />
  </g:if><g:if test="${controllerName=='home' && actionName=='list'}">
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'jquery-ui.css')}" />
  </g:if>
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'qtip.css')}" />
    <!--<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'balls.css')}" />-->
    <g:javascript src="links/link.js" />
    <g:javascript library="jquery-1.8.3" />
    <g:javascript library="jquery-ui.min" />
    <g:javascript library="newyear" />
    <g:javascript library="jquery.colorbox.min" />
  <g:if test="${controllerName=='home' && actionName=='detail'}">    
    <g:javascript library="galleria" />    
  </g:if>
    <g:javascript library="jquery.qtip.min" />
  <g:if test="${controllerName=='index' && actionName in ['howto','howto_hosting','safety','safety_hosting']}">
    <g:javascript library="jquery.faded" /> 
  </g:if><g:if test="${(controllerName=='personal' && actionName=='calendar')||(controllerName=='home' && actionName=='detail')}">    
    <g:javascript library="jquery.calendar" />      
  </g:if> 
    <g:javascript library="application" />
    <g:javascript library="prototype/prototype" />
  <g:if test="${(controllerName=='home' && actionName=='list') || (controllerName=='index' && actionName=='index')}"> 
    <g:javascript library='prototype/autocomplete' />
  </g:if><g:if test="${(controllerName=='home' && actionName in ['addnew','list'])||(controllerName=='personal' && actionName=='map')||(controllerName=='index' && actionName=='index')||(controllerName=='error' && actionName=='page_500')}">      
    <g:javascript library="prototype/effects" />
    <g:javascript library="prototype/controls" />	     
  </g:if><g:if test="${controllerName=='home' && actionName=='list'}">      
    <g:javascript library="prototype/slider" />    
  </g:if><g:if test="${(controllerName=='home' && actionName =='traceprint')}">
    <script src="https://api-maps.yandex.ru/1.1/index.xml?key=${context?.mapkey}&modules=traffic" type="text/javascript"></script>      
  </g:if><g:elseif test="${(controllerName=='home' && actionName =='trace')}">
    <script src="https://api-maps.yandex.ru/2.0/?load=package.full&lang=${context?.lang?'en-US':'ru-RU'}" type="text/javascript"></script>
  </g:elseif><g:elseif test="${(controllerName=='home' && actionName in ['list','detail'])||(controllerName=='personal' && actionName in ['map','infras'])||(controllerName=='index' && actionName in ['cottages','direction'])}">
    <script src="https://api-maps.yandex.ru/2.0/?load=package.standard&lang=${context?.lang?'en-US':'ru-RU'}" type="text/javascript"></script>
  </g:elseif><g:if test="${!user || (controllerName=='home' && actionName=='detail') || (controllerName=='index' && actionName in ['mobile','widget'])}">
    <script src="http://vkontakte.ru/js/api/openapi.js" type="text/javascript" charset="windows-1251"></script>
    <script src="//connect.facebook.net/${context?.lang?'en_US':'ru_RU'}/all.js" type="text/javascript"></script>
    <script src="//platform.twitter.com/widgets.js" type="text/javascript"></script>
  <!--[if lt IE 9]>
    <g:javascript library="html5" />
  <![endif]-->
    <g:javascript>    
      function showLoginForm(iValue,sName,sObj){
      <g:if test="${isLoginDenied}">
        jQuery.colorbox({
          html: '<div class="new-modal"><h2 class="clearfix">'+'${loginDeniedText}'.unescapeHTML()+'</h2></div>',
          scrolling: false
        });
      </g:if><g:else>
        sName =  sName || 'login_lbox';
        jQuery(sObj).colorbox({
          inline: true, 
          href: '#'+sName,
          scrolling: false,
          onLoad: function(){
            jQuery('#'+sName).show();          
          },
          onCleanup: function(){
            jQuery('#'+sName).hide();
            jQuery('#main_login_fail').hide();
          }        
        });      
        jQuery('#'+sName+'_container').css({'height': 'auto'});
        jQuery.colorbox.resize();      
        setService(iValue);
      </g:else>
      }
      function setService(iValue){
        if(iValue==2){
          $("iService_log").value='1';
          $("iService_reg").value='1';
          $("iService_vk").value='1';
          $("iService_fb").value='1';
        } else if(iValue==3) {
          $("iService_log").value='2';
          $("iService_reg").value='2';
          $("iService_vk").value='2';
          $("iService_fb").value='2';
        } else if(iValue==4) {
          $("iService_log").value='3';
          $("iService_reg").value='3';
          $("iService_vk").value='3';
          $("iService_fb").value='3';
        } else {
          $("iService_log").value='0';
          $("iService_reg").value='0';
          $("iService_vk").value='0';
          $("iService_fb").value='0';
        }
      }      
    </g:javascript>    
	</g:if>
  <r:layoutResources/>
  <script type="text/javascript">
    var ua = navigator.userAgent;    
    jQuery(document).ready(function(){
      jQuery('#inbox[title],#waiting[title],#booking[title],#favorite[title],a.tooltip[title],a[rel="tag"][title]').qtip({      
        position: { my: 'top center', at: 'bottom center' },
        style: { classes: 'ui-tooltip-shadow ui-tooltip-' + 'tipsy' }        
      });      
    <g:if test="${(controllerName=='home' && actionName in ['detail','list'])||(controllerName=='profile' && actionName!='view')||(controllerName in ['trip','help'])||(controllerName=='personal' && actionName!='index')||(controllerName=='account' && actionName!='payment')}">
      if(ua.search('Firefox')>-1)      
        jQuery('#main_table').css('marginTop','-20px');      
      else if(ua.search('MSIE 10.0')>-1)
        jQuery('#main_table').css('marginTop','0');      
      else
        jQuery('#main_table').css('marginTop','-15px');
    </g:if><g:if test="${controllerName=='home' && actionName=='detail'}">
      jQuery(window).scroll(function(){
        if(jQuery(this).scrollTop()>890)
          jQuery('#tabs2').css({position:'fixed',top:0,width:'730px',zIndex: '999'});
        else
          jQuery('#tabs2').css({position:'relative'});                
      });  
    </g:if>      
      /*if(ua.search('Firefox')>-1){//NY
        jQuery('.header-dropdown').css('top','151px');
      <g:if test="${(controllerName=='personal' && actionName in ['ads','bookings','waiting','rules','requirements'])||(controllerName in ['trip'])}">        
        jQuery('.header-dropdown.personal').css('top','40px');
      </g:if><g:elseif test="${(controllerName=='personal' && actionName in ['editads','adsoverview','photo','homephoto','video','homevideo','map','homeprice','homeprop','calendar','infras','promote'])||(controllerName in ['account','profile','help'])}">        
        jQuery('.header-dropdown.personal').css('top','0px');
      </g:elseif><g:if test="${controllerName=='home' && actionName=='detail'}">
        jQuery('#flag_container.header-dropdown').css('top','41px');
      </g:if>       
      }*/
    });
  </script>
  </head>
  <body onload="${pageProperty(name:'body.onload')}">
    <table class="line" width="100%" cellpadding="0" cellspacing="0" border="0" align="center" <g:if test="${(controllerName=='index' && actionName=='index')||(controllerName=='home' && actionName=='list')||(controllerName=='error' && actionName=='page_500')}">onclick="checkDropDowns();"</g:if> style="height:100%;margin-bottom:${!user?'-40':'-20'}px">
      <tr>
        <td width="100%" valign="top">
          <!--<div class="b-page_newyear">
            <div class="b-page__content">
              <div class="b-head-decor"><g:each var="i" in="${1..7}">
                <div class="b-head-decor__inner b-head-decor__inner_n${i}"><g:each var="j" in="${1..9}">
                  <div class="b-ball b-ball_n${j} b-ball_bounce"><div class="b-ball__right"></div><div class="b-ball__i"></div></div></g:each><g:each var="j" in="${1..6}">
                  <div class="b-ball b-ball_i${j}"><div class="b-ball__right"></div><div class="b-ball__i"></div></div><g:if test="${j==2}">
                  <div class="b-ball b-ball_i23"><div class="b-ball__right"></div><div class="b-ball__i"></div></div></g:if></g:each>
                </div></g:each>
              </div>
            </div>
          </div>-->
          <table class="clearfix" id="main_table" width="980" cellpadding="0" cellspacing="0" border="0" align="center" <g:if test="${controllerName=='home' && actionName=='detail'}">itemscope itemtype="http://schema.org/Residence"</g:if><g:elseif test="${controllerName=='index' && actionName=='index'}">itemscope itemtype="http://schema.org/WebPage"</g:elseif><g:elseif test="${controllerName=='index' && actionName=='timeline' && inrequest?.id}">itemscope itemtype="http://schema.org/Article"</g:elseif><g:elseif test="${controllerName=='index' && actionName=='about'}">itemscope itemtype="http://schema.org/Organization"</g:elseif>>
            <tr style="height:57px">
              <td width="250" rowspan="2" valign="middle">
                <div class="relative" style="width:250px">
                  <a <g:if test="${controllerName=='index' && actionName=='about'}">href="${createLink(uri:'',base:context?.mainserverURL_lang)}" rel="home" itemprop="url"</g:if><g:else>onclick="transit(${context.is_dev?1:0},'','','','','${context?.lang}')" rel="nofollow"</g:else> title="StayToday — ${message(code:'label.home')}">
                    <img <g:if test="${controllerName=='index' && actionName=='about'}">itemprop="logo"</g:if> class="logo float" width="140" height="90" src="${resource(dir:'images',file:'logo.png')}" alt="${message(code:'label.home')}" border="0" />
                  </a><!--noindex-->
                  <table class="header-list abs col" cellpadding="0" cellspacing="0" border="0">
                    <tr style="height:57px">
                      <td class="header-list-item locale dropdown" style="padding:0">
                        <a rel="nofollow" onclick="this.parentElement.toggleClassName(' open');this.toggleClassName('active')">
                          <span class="icon"></span>
                          <span class="value_lang">${message(code:'label.lang')}</span>
                          <span class="arrow"></span>
                        </a>
                        <div class="header-dropdown">
                          <div class="header-dropdown-arrow"></div>
                          <div class="header-dropdown-inner">
                            <div class="header-dropdown-section">                            
                              <ul class="header-dropdown-list">
                                <li class="header-dropdown-list-item">
                                  <a rel="nofollow" onclick="setLangUrl('ru')">Русский</a>
                                </li>
                                <li class="header-dropdown-list-item">
                                  <a rel="nofollow" onclick="setLangUrl('en')">English</a>
                                </li>                              
                              </ul>                            
                            </div>
                          </div>                      
                        </div>
                      </td>                
                    </tr>
                  </table><!--/noindex-->
                </div>
              </td>
              <td width="480" align="left" valign="middle"><!--noindex-->
                <table class="header-list" cellpadding="0" cellspacing="0" border="0">
                  <tr style="height:57px">
                  <g:if test="${user}">
                    <td class="header-list-item button_user dropdown">                      
                      <a rel="nofollow" onclick="this.parentElement.toggleClassName(' open');this.toggleClassName('active')">
                        <span class="icon"></span>
                        <span id="header_user">${user?.nickname?:''}</span>
                        <span class="arrow"></span>
                      </a>
                      <div class="header-dropdown">
                        <div class="header-dropdown-arrow"></div>
                        <div class="header-dropdown-inner">
                          <div class="header-dropdown-section">
                            <ul class="header-dropdown-list">
                              <li class="header-dropdown-list-item">
                                <g:link controller="personal" action="index" rel="nofollow" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','index')['name'+context?.lang]}</g:link>
                              </li>                              
                              <li class="header-dropdown-list-item">
                                <g:link controller="personal" action="ads" rel="nofollow" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','ads')['name'+context?.lang]}</g:link>
                              </li>
                              <li class="header-dropdown-list-item">
                                <g:link controller="trip" action="next" rel="nofollow" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('trip','next')['name'+context?.lang]}</g:link>
                              </li>                            
                              <li class="header-dropdown-list-item">
                                <g:link controller="profile" action="edit" rel="nofollow" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('profile','edit')['name'+context?.lang]}</g:link>
                              </li>
                              <li class="header-dropdown-list-item">
                                <g:link controller="account" action="index" rel="nofollow" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('account','index')['name'+context?.lang]}</g:link>
                              </li>
                              <li class="header-dropdown-list-item">
                                <g:link controller="user" action="logout" id="${params.controller}" rel="nofollow" base="${context?.mainserverURL_lang}">${message(code:'label.logout')}</g:link>
                              </li>
                            </ul>
                          </div>
                        </div>
                      </div>
                    </td>                    
                    <td class="header-list-item button_inbox">
                      <a id="inbox" href="<g:createLink controller='inbox' action='${msg_unread_count==1?'view':'index'}' id="${msg_unread_count==1?msg_unread_id:''}" base="${context?.absolute_lang}"/>" rel="nofollow" title="${message(code:'label.inbox')}">
                        <span class="icon"></span>
                      <g:if test="${msg_unread_count}">
                        <div class="unread_count">${msg_unread_count}</div>
                      </g:if>
                      </a>
                    </td>
                    <td class="header-list-item button_waiting <g:if test="${waiting_unread_count>0}">actively</g:if>">
                      <a id="waiting" <g:if test="${waiting_unread_count==0}">class="no_active"</g:if> href="<g:createLink controller='personal' action='waiting' base='${context?.absolute_lang}'/>" 
                        rel="nofollow" title="${message(code:'label.waiting')}" onclick="yaCounter15816907.reachGoal('waiting_list');return true;">
                        <span class="icon"></span>
                      <g:if test="${waiting_unread_count}">
                        <div class="unread_count">${waiting_unread_count}</div>
                      </g:if>
                      </a>
                    </td>
                  <g:if test="${reserveStatus?.payAttached}">
                    <td class="header-list-item button_booking <g:if test="${reserveStatus?.booking_unread_count>0}">actively</g:if>">
                      <a id="booking" <g:if test="${reserveStatus?.booking_unread_count==0}">class="no_active" href="<g:createLink controller='personal' action='bookings' base='${context?.absolute_lang}'/>"</g:if><g:else>href="<g:createLink controller='personal' action='bookings' params='[modstatus:1]' base="${context?.absolute_lang}"/>"</g:else> rel="nofollow" title="${message(code:'label.booking')}" onclick="yaCounter15816907.reachGoal('booking_list');return true;">
                        <span class="icon"></span>
                      <g:if test="${reserveStatus?.booking_unread_count}">
                        <div class="unread_count">${reserveStatus?.booking_unread_count}</div>
                      </g:if>
                      </a>
                    </td>
                  </g:if>
                    <td class="header-list-item button_favorites <g:if test="${(wallet?:[]).size()>0}">starred</g:if>">
                      <a id="favorite" <g:if test="${(wallet?:[]).size()==0}">class="no_active"</g:if><g:else>href="<g:createLink controller='trip' action='favorite' base='${context?.absolute_lang}'/>"</g:else> rel="nofollow" title="${message(code:'label.favorite')}">
                        <span class="icon"></span>
                      </a>                      
                    </td>
                </g:if><g:else>                    
                    <td class="header-list-item" nowrap="nowrap">
                      <b <g:if test="${!(controllerName=='index' && actionName in ['howto','howto_hosting'])}">class="link" onclick="transit(${context.is_dev?1:0},['howto','index'],'','','','${context?.lang}')"</g:if>>${Infotext.findByControllerAndAction('index','howto')['name'+context?.lang]}</b>
                    </td>
                  <g:if test="${!(controllerName=='user' && actionName in ['addnew','index'])}">
                    <td class="header-list-item">
                      <a class="icon none" rel="nofollow" onclick="showLoginForm(1,'reg_lbox',this)">${message(code:'label.signup')}</a>
                    </td>                                        
                    <td class="header-list-item">
                      <a class="icon none" rel="nofollow" onclick="showLoginForm(1,'login_lbox',this)">${message(code:'label.login')}</a>
                    </td>
                  </g:if>
                </g:else>                  
                  </tr>
                </table><!--/noindex-->
              </td>             
              <td width="250" align="right" valign="top">
                <table class="header-list" cellpadding="0" cellspacing="0" border="0">
                  <tr style="height:57px">                   
                    <td class="header-list-item help dropdown">
                      <a rel="nofollow" onclick="this.parentElement.toggleClassName(' open');this.toggleClassName('active')">
                        <span class="icon"></span>
                        ${message(code:'label.help')}
                        <span class="arrow"></span>
                      </a>                     
                      <div class="header-dropdown">                      
                        <div class="header-dropdown-arrow"></div>
                        <div class="header-dropdown-inner">
                          <div class="header-dropdown-section">
                            <ul class="header-dropdown-list">
                            <g:each in="${helpmenu}" var="item" status="i">
                              <li class="header-dropdown-list-item">
                                <g:if test="${item.action=='faq' && controllerName=='index' && actionName=='index'}"><a href="${createLink(controller:item.controller,action:item.action)}" rel="help">${item['name'+context?.lang]}</a></g:if>
                                <g:else><b class="link" onclick="transit(${context.is_dev?1:0},['${item.action+(i<2?'_basic':'')}','help'],'','','','${context?.lang}')">${item['name'+context?.lang]}</b></g:else>
                              </li>                              
                            </g:each>
                            </ul>
                          </div>
                        </div>
                      </div>                     
                    </td>
                    <td class="header-list-item valute dropdown"><!--noindex-->
                      <a rel="nofollow" onclick="this.parentElement.toggleClassName(' open');this.toggleClassName('active')">                        
                        <span class="value_currency"><g:rawHtml>${context?.shown_valuta?.symbol?:''}</g:rawHtml>&nbsp;&nbsp;${context?.shown_valuta?.code}</span>
                        <span class="arrow"></span>
                      </a>
                      <div class="header-dropdown">
                        <div class="header-dropdown-arrow"></div>
                        <div class="header-dropdown-inner">
                          <div class="header-dropdown-section">                            
                            <ul class="header-dropdown-list">
                            <g:each in="${change_valuta_menu}" var="item" status="i">
                              <li id="currency_selector_${item?.code}" class="header-dropdown-list-item">
                                <a rel="nofollow" onclick="setValute('${context.is_dev?'/'+context.appname:''}',${item.id},${(controllerName=='home' && actionName=='list')?1:0})">
                                  <g:rawHtml>${item?.symbol?:''}</g:rawHtml>&nbsp;&nbsp;${item?.code}
                                </a>
                              </li>
                            </g:each>
                            </ul>                            
                          </div>
                        </div>                      
                      </div><!--/noindex-->
                    </td>
                  </tr>
                </table>              
              </td>
            </tr>
            <tr style="height:73px">
              <td width="730" colspan="3">
                <${(controllerName=='index' && actionName=='index')?'h1':'span'} class="slog padd10" <g:if test="${controllerName=='index' && actionName=='index'}">itemprop="name"</g:if>>${message(code:'main.slogan')}</${(controllerName=='index')&&(actionName=='index')?'h1':'span'}>
              </td>
            </tr>          
            <g:layoutBody />
          </table>
        </td>
      </tr>
      <tr style="height:120px">
        <td width="980" align="center" valign="top" class="line bottom">
          <div class="relative" style="width:980px;height:120px">            
            <div class="footer">
              <table width="980" cellpadding="0" cellspacing="0" border="0">
                <tr style="height:120px" valign="middle">
                  <td width="250" align="left">
                    <img class="float" src="${resource(dir:'images',file:'logo.png')}" alt="StayToday" width="60" hspace="20" />
                    <div class="social">
                      <span class="copy">
                      <g:if test="${controllerName=='index' && actionName=='index'}">
                        <span itemprop="copyrightYear">MMXI</span> - MMXIV &copy; <span itemprop="copyrightHolder">StayToday</span><br/>
                      </g:if><g:else><!--noindex-->MMXI - MMXIV &copy; StayToday<br/><!--/noindex--></g:else>
                        <span class="link" onclick="transit(${context.is_dev?1:0},['terms','index'],'','','','${context?.lang}')">${Infotext.findByAction('terms')['name'+context?.lang]}</span>
                      </span><br/>
                      <a class="social-link" href="http://www.facebook.com/StayTodayRu" target="_blank" title="${message(code:'social.fb')}">
                        <i class="social-icon fb"></i>
                      </a>
                      <a class="social-link" href="http://twitter.com/StayTodayRu" target="_blank" title="${message(code:'social.tw')}">
                        <i class="social-icon tw"></i>
                      </a>
                      <a class="social-link" href="http://vk.com/StayToday" target="_blank" title="${message(code:'social.vk')}">
                        <i class="social-icon vk"></i>
                      </a>
                      <a class="social-link" href="https://plus.google.com/+StaytodayRu/about" rel="publisher" target="_blank" title="${message(code:'social.gp')}">
                        <i class="social-icon gp"></i>
                      </a>
                      <a class="social-link" href="https://www.youtube.com/user/MrStayToday" target="_blank" title="${message(code:'social.yt')}">
                        <i class="social-icon yt"></i>
                      </a> 
                    </div>
                  </td>
                  <td valign="middle">
                    <ul class="header-list padd20 clearfix">
                    <g:each in="${bottommenu}" var="item" status="i">
                      <li class="header-list-item float">
                      <g:if test="${controllerName==item.controller && (actionName==item.action || actionName==item.relatedpages)}"><b>${item['name'+context?.lang]}</b></g:if>
                      <g:elseif test="${controllerName=='index' && actionName=='index' && item.action in ['about','timeline','popdirectionAll','mobile']}"><a href="${createLink(controller:item.controller,action:item.action,base:context.mainserverURL_lang)}">${item['name'+context?.lang]}</a></g:elseif>
                      <g:elseif test="${item.action in ['popdirectionAll','mobile','widget','timeline']}"><b class="link" onclick="transit(${context.is_dev?1:0},['${item.action!='popdirectionAll'?item.action:'alldirections'}'],'','','','${context?.lang}')">${item['name'+context?.lang]}</b></g:elseif>
                      <g:else><b class="link" onclick="transit(${context.is_dev?1:0},['${item.action}','${item.controller}'],'','','','${context?.lang}')">${item['name'+context?.lang]}</b></g:else>
                      </li>
                    </g:each>
                    </ul>
                  </td>
                  <td class="analytics">
                  <g:if test="${controllerName != 'personal'}">
                    <!-- <b class="tel">8 (800) 555-1768</b> -->
                    <div class="counters_code" align="center"><!--noindex-->
                      <script id="top100Counter" type="text/javascript" src="https://counter.rambler.ru/top100.jcn?2812368"></script><!--/noindex-->
                      <noscript>
                        <a href="http://top100.rambler.ru/navi/2812368/" rel="nofollow"><img src="http://counter.rambler.ru/top100.cnt?2812368" alt="Rambler's Top100" border="0" /></a>
                        <div>
                          <img src="//mc.yandex.ru/watch/15816907" style="position:absolute;left:-9999px;" alt="" />
                          <g:if test="${breadcrumbs?.city?.yacode||homecity?.yacode}"><img src="//mc.yandex.ru/watch/${breadcrumbs?.city?.yacode?:homecity.yacode}" style="position:absolute;left:-9999px;" alt="" /></g:if>
                        </div>
                      </noscript>
                      <script type="text/javascript"><!--
                        document.write("<a href='http://www.liveinternet.ru/click' rel='nofollow' target='_blank' title='LiveInternet'><img src='//counter.yadro.ru/hit?t45.12;r"+
                        escape(document.referrer)+((typeof(screen)=="undefined")?"":
                        ";s"+screen.width+"*"+screen.height+"*"+(screen.colorDepth?screen.colorDepth:screen.pixelDepth))+";u"+escape(document.URL)+
                        ";h"+escape(document.title.substring(0,80))+";"+Math.random()+"' alt='LiveInternet' border='0' width='30' height='30'><\/a>")
                      //--></script>
                    <g:if test="${controllerName=='index' && actionName=='index'}">
                      <br /><a href="http://naydidom.com/arenda-posutochno-kvartir-russia.html" target="_blank"><img style="margin-top:2px" src="http://naydidom.com/static/images/nd-icon.jpg" alt="Посуточная аренда квартир на Naydidom.com" /></a>
                    </g:if>
                    </div>
                  </g:if>
                  </td>
                  <td align="right"><!--noindex-->
                    <div class="developers">
                      <img src="${resource(dir:'images',file:'design-logo.png')}" alt="${message(code:'label.designlogo')}" border="0" />
                      <span class="copyright">
                        ${message(code:'label.design')}<br/>${message(code:'label.designlogo')}<br/>
                        <span class="link" onclick="exttransit(['fntw.ru'],'',1)">www.fntw.ru</span>
                      </span>
                    </div>
                    <div class="developers" style="margin-top:10px">
                      <img src="${resource(dir:'images',file:'develop-logo.png')}" alt="CSI Software" border="0" />
                      <span class="copyright">
                        ${message(code:'label.develop')}<br/>CSI Software<br/>
                        <span class="link" onclick="exttransit(['trace.ru'],'',1)">www.trace.ru</span>
                      </span>
                    </div><!--/noindex-->
                  </td>
                </tr>
              </table>            
            </div>
          </div>    
        </td>
      </tr>
    </table><!--noindex-->
  <g:if test="${!user && !(controllerName=='user' && actionName in ['addnew','index'])}">
    <g:render template="/login"/>
  </g:if>
    <g:javascript>    
      <g:if test="${!user || (controllerName=='home' && actionName=='detail') || (controllerName=='index' && actionName in ['mobile','widget'])}">
      VK.init({ apiId: ${vk_api_key} });
      FB.init({ appId: ${fb_api_key}, status: true, cookie: true, xfbml: true, oauth: true });      
      </g:if>      
    <g:if test="${controllerName != 'personal'}">
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
            w.yaCounter15816907 = new Ya.Metrika({id:15816907, webvisor:true, clickmap:true, trackLinks:true, accurateTrackBounce:true, trackHash:true});
          <g:if test="${breadcrumbs?.city?.yacode||homecity?.yacode}">
            w.yaCounter${breadcrumbs?.city?.yacode?:homecity.yacode} = new Ya.Metrika({id:${breadcrumbs?.city?.yacode?:homecity.yacode}, webvisor:true, clickmap:true, trackLinks:true, accurateTrackBounce:true, trackHash:true});
          </g:if>
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
      (function() { 
        var widget_id = '13557';
        var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; 
        s.src = '//code.jivosite.com/script/widget/' + widget_id; 
        var ss = document.getElementsByTagName('script')[0]; ss.parentNode.insertBefore(s, ss); 
      })();      
    </g:if>
    </g:javascript>
    <r:layoutResources/><!--/noindex-->    
  </body>
</html>
