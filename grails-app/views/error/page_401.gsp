<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru">
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta http-equiv="content-language" content="ru" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />               
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />    
    <meta name="copyright" content="StayToday" />    
    <meta name="resource-type" content="document" />
    <meta name="document-state" content="dynamic" />
    <meta name="revisit" content="1" />
    <meta name="viewport" content="width=1000,maximum-scale=1.0" />     
    <meta name="robots" content="all" />
    <meta name="yandex-verification" content="7be9d97fe94c27b7" />
    <meta name="google-site-verification" content="6QeMPuVWeP1oOZ-4c7l-6oB9ZB0x2un7ZNG01Oy5z64" />    
    <meta name="alexaVerifyID" content="dzhfoUuqrBO_36HG1hBK_h70XGg" />
    <link rel="shortcut icon" href="${resource(file:'favicon.ico',absolute:true)}" type="image/x-icon" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'main.css')}" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'qtip.css')}" />
    <!--<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'balls.css')}" />-->
    <g:javascript src="links/link.js" />    
    <g:javascript src="jquery-1.8.3.js" />
    <!--<g:javascript library="newyear" />-->
    <g:javascript src="jquery.colorbox.min.js" />
    <g:javascript src="jquery.qtip.min.js" /> 
    <g:javascript src="application.js" />
    <g:javascript src="prototype/prototype.js" />    
    <g:javascript src='prototype/effects.js' />
    <g:javascript src='prototype/controls.js' />
    <g:javascript src='prototype/autocomplete.js' />    
  <g:if test="${!user}">
    <script src="https://vkontakte.ru/js/api/openapi.js" type="text/javascript" charset="windows-1251"></script>
    <script src="//connect.facebook.net/ru_RU/all.js"></script>
    <script src="//platform.twitter.com/widgets.js"></script>
  </g:if>
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/>
    <script type="text/javascript">
      var opened_popdirection=0, mouseOnDir=0;
      function onKey(e){
        var iKeycode;
        if (window.event) iKeycode = window.event.keyCode;
        else if (e) iKeycode = e.which;
        else return true;    
        if (iKeycode == 13)
          submitForm();
      } 
      function initialize() {
        $('where').onkeypress=onKey;    
        new Autocomplete('where', { serviceUrl:'${resource(dir:'home',file:'where_autocomplete')}' });
      }    
      function submitForm(){
        if($("where").value.length > 2){
          $("where").removeClassName('red0');
          $("where1").value = $("where").value;
          $("search_form").submit();
        }else{
          $("where").addClassName('red0');
        }
      }
      function togglePopDirection(iType){
        if ($("select_popdirection").style.display == 'none'){
          updatePopDirection(iType);
          opened_popdirection = 1;
        } else {
          jQuery("#select_popdirection").slideToggle();
          opened_popdirection = 0;
        }
      }
      function updatePopDirection(iType){
        <g:remoteFunction controller='index' action='selectpopcities' update='select_popdirection' onComplete="jQuery('#select_popdirection').slideDown()" params="\'country_id=\'+iType" />
      }
      function clickPopDirection(sWhere){
        <g:remoteFunction controller='home' action='cityselectstat'/>
        $('where1').value = sWhere;
        $("search_form").submit();
      }
      function checkDropDowns(){
        if (opened_popdirection){
          if (!mouseOnDir){
            opened_popdirection = 0;
            $("select_popdirection").style.display = 'none';
          }
        }
      }
    <g:if test="${!user}">    
      function showLoginForm(iValue,sName,sObj){
      <g:if test="${isLoginDenied}">
        jQuery.colorbox({
          html: "${loginDeniedText}".unescapeHTML(),
          scrolling: false
        });
      </g:if>
      }    
    </g:if>  
      jQuery(document).ready(function(){
        jQuery('#inbox[title],#waiting[title],#booking[title],#favorite[title],a.tooltip[title],a[rel="tag"][title]').qtip({
          position: { my: 'top center', at: 'bottom center' },
          style: { classes: 'ui-tooltip-shadow ui-tooltip-' + 'tipsy' }        
        });
        /*if(navigator.userAgent.search('Firefox')>-1) //NY
          jQuery('.header-dropdown').css('top','151px');*/        
      });
      function blurInput(){
        var lForm = document.getElementById('mainloginForm');
        if (lForm){
          if(lForm.user.value=='')
            lForm.user.value="${message(code:'login.user')}";
          if(lForm.password.value=='')
            lForm.password.value="${message(code:'login.password')}";        
        }
      }
      function focusLogin(){
        var lForm = document.getElementById('mainloginForm');
        if(lForm.user.value=="${message(code:'login.user')}"){
          lForm.user.style.color='black';
          lForm.user.value='';
        }
        if(lForm.password.value==''){
          lForm.password.style.color='gray';    
          lForm.password.value="${message(code:'login.password')}";    
        }
      }
      function focusPassword(){
        var lForm = document.getElementById('mainloginForm');
        if(lForm.user.value==''){
          lForm.user.style.color='gray';
          lForm.user.value="${message(code:'login.user')}";
        }
        if(lForm.password.value=="${message(code:'login.password')}"){
          lForm.password.style.color='black';
          lForm.password.value='';
        }
      }
      function reloadCaptcha(){
        <g:remoteFunction controller='index' action='reloadCaptcha' onSuccess='processRelResponse(e)' />
      }
      function processRelResponse(e){
        $('captcha_picture').innerHTML = e.responseJSON.captcha;
        $('captcha_picture').firstChild.setStyle({width: '120px'});
      }
      function processLogResponse(e){    
        var height;
        if(e.responseJSON.error){
          $('captcha_text').removeClassName('red');
          var sErrorMsg='';
          $('captcha_picture').innerHTML = e.responseJSON.captcha;
          $('captcha_picture').firstChild.setStyle({width: '120px'});
          var aErr=e.responseJSON.errorprop;
          if (e.responseJSON.user_enter_fail>e.responseJSON.user_max_enter_fail) {
            $('captcha_label').show();
            $('captcha_log').show();          
          } else {
            $('captcha_label').hide();
            $('captcha_log').hide();
          }
          if(aErr.indexOf(1)>-1){
            sErrorMsg+="${message(code:'login.error.fail')}<br/>";
          }
          if(aErr.indexOf(3)>-1){
            sErrorMsg+='${message(code:"login.error.email")} <a href="javascript:void(0)" rel="nofollow" onclick="showLoginForm(1,\'reg_lbox\',this);$(\'login_lbox\').hide()">${message(code:"login.error.email.register")}</a><br/>';
            document.getElementById('reg_email').value=document.getElementById('user').value
          }
          if(aErr.indexOf(2)>-1){
            sErrorMsg+='${message(code:"add.error.incorrect.code")}<br/>';
            $('captcha_text').addClassName('red');          
          }
          $('main_login_fail').innerHTML = sErrorMsg;
          $('main_login_fail').show();  
        }else{
          var setframes = ''
          $('main_login_fail').hide();    
          if(e.responseJSON.service==1 || e.responseJSON.service==2){
            setframes = location.href.split('?')[0] + "?" + ((location.href.split('?')[1])?location.href.split('?')[1]:"") + "&service="+e.responseJSON.service;
            location.assign(setframes);
          } else if(e.responseJSON.service==3){
            window.location = "${((context?.is_dev)?"/"+context?.appname+"/trip/waiting":"/trip/waiting")}";
          } else {
            location.reload(true);
          }
        }
      }
      function doLogin() {
        VK.Auth.login(authInfo,2);
      }
      function authInfo(response) {
        if (response.session) {
          VK.api('users.get',{uids:response.session.mid, fields:'uid,first_name,last_name,photo_rec,photo_big'}, function(data) {
            $("main_vk_id").value=data.response[0].uid;
            $("main_vk_name").value=data.response[0].first_name+' '+data.response[0].last_name;
            $("main_vk_pic").value=data.response[0].photo_rec;
            $("main_vk_photo").value=data.response[0].photo_big;
            $("main_vk_hash").value='';
            $("main_Vk_submit_button").click();
          });
        }
      }
      function setNickname(){
        if($('reg_firstname').value.length && $('reg_lastname').value.length)
          $('reg_nickname').value=$('reg_firstname').value+' '+$('reg_lastname').value;
      }
      function facebook(response) {
        if (response.authResponse) {
          FB.api('/me', function(response) {
            $("main_fb_id").value=response.id;
            $("main_fb_name").value=response.name;
            $("main_fb_email").value=response.email;
            FB.api('/me/picture', function(response) {
              $("main_fb_pic").value=response.data.url;
              FB.api('/me/picture?type=large', function(response) {
                $("main_fb_photo").value=response.data.url;
                fbLoginClick();
              });
            });
          });
        }
      }
      function fbLoginClick() {
        $("main_Fb_submit_button").click();
      }
      function doFBlogin() {
        FB.getLoginStatus(function(response) {
          if (response.status === 'connected') {
            facebook(response);
          } else {
            FB.login(facebook,{scope:'email'});
          }
        });
      }
    </script>
    <style type="text/css">.form.s label { min-width: 100px; padding-left: 0;}</style>
  </head>
  <body onload="initialize()">
    <table class="line" width="100%" height="100%" cellpadding="0" cellspacing="0" border="0" align="center" <g:if test="${(controllerName=='index' && actionName=='index')||(controllerName=='error' && actionName=='page_500')}">onclick="checkDropDowns();"</g:if> style="height:100%;margin-bottom:${!user?'-40':'-20'}px">
      <tr>
        <td width="100%" valign="top">
          <!--<div class="b-page_newyear">
            <div class="b-page__content">
              <i class="b-head-decor"><g:each var="i" in="${1..7}">
                <i class="b-head-decor__inner b-head-decor__inner_n${i}"><g:each var="j" in="${1..9}">
                  <div class="b-ball b-ball_n${j} b-ball_bounce"><div class="b-ball__right"></div><div class="b-ball__i"></div></div></g:each><g:each var="j" in="${1..6}">
                  <div class="b-ball b-ball_i${j}"><div class="b-ball__right"></div><div class="b-ball__i"></div></div><g:if test="${j==2}">
                  <div class="b-ball b-ball_i23"><div class="b-ball__right"></div><div class="b-ball__i"></div></div></g:if></g:each>
                </i></g:each>
              </i>
            </div>
          </div>--> 
          <table width="980" cellpadding="0" cellspacing="0" border="0" align="center">
            <tr style="height:57px">
              <td width="250" rowspan="2" valign="middle">
                <div class="relative" style="width:250px">
                  <a title="StayToday — ${message(code:'label.home')}" href="${g.createLink(uri:'',base:context?.absolute_lang)}" rel="home">
                    <img class="logo float" width="140" height="90" src="${resource(dir:'images',file:'logo.png')}" alt="${message(code:'label.home')}" border="0" />
                  </a>                
                  <table class="header-list abs col" cellpadding="0" cellspacing="0" border="0">
                    <tr style="height:57px">
                      <td class="header-list-item locale dropdown" style="padding:0">
                        <a href="javascript:void(0);" rel="nofollow" onclick="this.parentElement.toggleClassName(' open');this.toggleClassName('active')">
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
                                  <a href="javascript:void(0);" rel="nofollow" onclick="setLangUrl('ru')">Русский</a>
                                </li>
                                <li class="header-dropdown-list-item">
                                  <a href="javascript:void(0);" rel="nofollow" onclick="setLangUrl('en')">English</a>
                                </li>                              
                              </ul>                            
                            </div>
                          </div>                      
                        </div>
                      </td>                
                    </tr>
                  </table>
                </div>
              </td>
              <td width="480" valign="middle"><!--noindex-->
                <table class="header-list" cellpadding="0" cellspacing="0" border="0">
                  <tr style="height:57px">
                <g:if test="${user}">
                    <td class="header-list-item button_user dropdown">                      
                      <a href="javascript:void(0);" rel="nofollow" onclick="this.parentElement.toggleClassName(' open');this.toggleClassName('active')">
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
                                <g:link controller="user" action="logout" rel="nofollow" base="${context?.absolute_lang}">${message(code:'label.logout')}</g:link>
                              </li>
                            </ul>
                          </div>
                        </div>
                      </div>
                    </td>                    
                    <td class="header-list-item button_inbox">
                      <a id="inbox" href='<g:createLink controller="inbox" action="index"/>' rel="nofollow" title="${message(code:'label.inbox')}">
                        <span class="icon"></span>
                        <g:if test="${msg_unread_count}">
                          <div class="unread_count" style="display:block">${msg_unread_count}</div>
                        </g:if>
                      </a>
                    </td>
                    <td class="header-list-item button_waiting <g:if test="${waiting_unread_count>0}">actively</g:if>">
                      <a id="waiting" <g:if test="${waiting_unread_count==0}">class="no_active"</g:if> rel="nofollow" 
                        href='<g:createLink controller="personal" action="waiting"/>' title="${message(code:'label.waiting')}" onclick="yaCounter15816907.reachGoal('waiting_list');return true;">
                        <span class="icon"></span>
                      <g:if test="${waiting_unread_count}">
                        <div class="unread_count">${waiting_unread_count}</div>
                      </g:if>
                      </a>
                    </td>
                  <g:if test="${reserveStatus?.payAttached}">
                    <td class="header-list-item button_booking <g:if test="${reserveStatus?.booking_unread_count>0}">actively</g:if>">
                      <a id="booking" <g:if test="${reserveStatus?.booking_unread_count==0}">class="no_active" href="<g:createLink controller='personal' action='bookings'/>"</g:if><g:else>href="<g:createLink controller='personal' action='bookings' params='[modstatus:1]'/>"</g:else> 
                        rel="nofollow" title="${message(code:'label.booking')}" onclick="yaCounter15816907.reachGoal('booking_list');return true;">
                        <span class="icon"></span>
                      <g:if test="${reserveStatus?.booking_unread_count}">
                        <div class="unread_count">${reserveStatus?.booking_unread_count}</div>
                      </g:if>
                      </a>
                    </td>
                  </g:if>
                    <td class="header-list-item button_favorites <g:if test="${(wallet?:[]).size()>0}">starred</g:if>">
                      <a id="favorite" <g:if test="${(wallet?:[]).size()==0}">class="no_active" href="javascript:void(0);"</g:if>
                        <g:else>href='<g:createLink controller="trip" action="favorite"/>'</g:else> rel="nofollow" title="${message(code:'label.favorite')}">
                        <span class="icon"></span>
                      </a>                      
                    </td>
                </g:if><g:else>                    
                    <td class="header-list-item" nowrap="nowrap">
                      <g:link controller="index" action="howto">${Infotext.findByControllerAndAction('index','howto')['name'+context?.lang]}</g:link>
                    </td>
                </g:else>
                  </tr>
                </table><!--/noindex-->
              </td>
              <td width="250" align="right" valign="top">
                <table class="header-list" cellpadding="0" cellspacing="0" border="0">
                  <tr style="height:57px">                   
                    <td class="header-list-item help dropdown">
                      <a href="javascript:void(0);" rel="nofollow" onclick="this.parentElement.toggleClassName(' open');this.toggleClassName('active')">
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
                      <a href="javascript:void(0);" rel="nofollow" onclick="this.parentElement.toggleClassName(' open');this.toggleClassName('active')">                        
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
                                <a href="javascript:void(0);" rel="nofollow" onclick="setValute('${context.is_dev?'/'+context.appname:''}',${item.id},${(controllerName=='home' && actionName=='list')?1:0})">
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
              <td width="720" colspan="3">
                <span class="slog padd10">${message(code:'main.slogan')}</span>
              </td>
            </tr>
            <tr style="height:140px">
              <td width="250" class="rent">
                <g:if test="${isLoginDenied}"><a href="javascript:void(0)" rel="nofollow" onclick="showLoginForm()">${message(code:'common.deliverhome')}</a></g:if>
                <g:else><g:link controller="home" action="addnew">${message(code:'common.deliverhome')}</g:link></g:else>
              </td>
              <td colspan="3" class="search"><!--noindex-->
                <g:form name="search_form" id="search_form" url="${[controller:'home',action:'search']}" base="${context?.mainserverURL_lang}" method="get">
                  <table class="float" width="553" cellpadding="5" cellspacing="0" border="0">
                    <tr>
                      <td width="145" valign="top" class="padd10">
                        <label for="where">${message(code:'common.where')}</label>
                        <input type="text" id="where" name="wre" maxlength="${stringlimit}" value="" placeholder="${message(code:'common.where_default')}" autocomplete="off" style="width:145px"/>
                        <div id="where_autocomplete" class="autocomplete" style="display:none"></div>     
                      </td>                  
                      <td width="107" valign="top">
                        <label for="hometype_id">${message(code:'common.home_type')}</label>
                        <select id="hometype_id" name="hometype_id" style="width:107px">
                          <option <g:if test="${0==(inrequest?.hometype_id?:0)}">selected="selected"</g:if> value="">${message(code:'common.any')}</option>
                        <g:each in="${hometype}" var="item">            
                          <option <g:if test="${item?.id==inrequest?.hometype_id}">selected="selected"</g:if> value="${item.urlname}">${item['name'+context?.lang]}</option>
                        </g:each>
                        </select>
                      </td>
                      <td width="43" valign="top">
                        <label>${message(code:'common.guests')}</label>
                        <select id="homeperson_id" name="homeperson_id" style="width:43px">
                        <g:each in="${homeperson}" var="item">            
                          <option <g:if test="${item?.id==inrequest?.homeperson_id}">selected="selected"</g:if> value="${item?.id}">${item?.kol}</option>
                        </g:each>
                        </select>                  
                      </td>
                      <td width="85" valign="top">
                        <label>${message(code:'common.date_from')}</label>
                        <span class="dpicker">
                          <calendar:datePicker name="date_start" value="${inrequest?.date_start}" dateFormat="%d-%m-%Y"/>
                        </span>
                      </td>
                      <td width="85" valign="top" class="dpicker">
                        <label>${message(code:'common.date_to')}</label>
                        <span class="dpicker">
                          <calendar:datePicker name="date_end" value="${inrequest?.date_end}" dateFormat="%d-%m-%Y"/>                
                        </span>
                      </td>              
                    </tr>
                    <tr>
                      <td colspan="3" valign="top" class="padd10">
                        <div class="dropdown-list relative">
                          <span>
                            <small id="a_select_popdirection" class="select" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0" onclick="togglePopDirection(1)"><a href="javascript:void(0)" rel="nofollow" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0">${message(code:'common.choose_city')}</a></small>
                            <div class="select_dropdown" id="select_popdirection" style="display:none" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0"></div>              
                          </span>              
                        </div>
                      </td>
                      <td colspan="2" align="center">
                        <div class="dropdown-list relative">
                          <small class="link"><g:link controller="index" action="waiting">${message(code:'index.sendrequest')}</g:link></small>                          
                        </div>
                      </td>
                    </tr>
                  </table>                  
                  <input type="hidden" id="where1" name="where" value="" />
                </g:form><!--/noindex-->
                <a class="button main col" href="javascript:void(0)" rel="nofollow" onclick="submitForm()">${message(code:'common.renthome')}</a>                
              </td>
            </tr>
            <tr style="height:460px">
              <td width="250" valign="top">
                <h2 class="padd20">${message(code:'index.pop_directions').capitalize()}</h2>
                <ul class="search_filter_content" style="padding:5px 10px 0 20px">
                <g:each in="${countries}" var="country" status="i"><g:if test="${country.id in countryIds}">
                  <!--<li class="clearfix country">
                    <h3><span class="flagcon ${country?.icon}"></span>
                    <g:if test="${country.is_index}"><a href="<g:createLink controller='index' action='popdirectionAll' params='[id:country?.urlname]'/>"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${country?.urlname}'])"></g:else>
                    ${country['name'+context?.lang]}<g:if test="${country.is_index}"></a></g:if><g:else></span></g:else></h3>
                  </li>-->
                  <g:each in="${popdirection}" var="record" status="j"><g:if test="${country.id==record.country_id}">
                  <li class="clearfix dott ${(j==0)?'first':''}">
                    <b><g:if test="${record.is_index}"><a class="show_more_link" href="<g:createLink controller='index' action='direction' id='${record.linkname}' params='${[country:country.urlname]}'/>"></g:if>
                    <g:else><span class="show_more_link" onclick="transit(${context.is_dev?1:0},['${record.linkname}','${country.urlname}'],'','','','${context?.lang}')"></g:else>
                    ${record['name2'+context?.lang]}<g:if test="${record.is_index}"></a></g:if><g:else></span></g:else></b><br/>
                    <p class="desc">${record['name'+context?.lang]}</p>
                  </li>
                  </g:if></g:each>
                </g:if></g:each>    
                  <li class="clearfix dott last" style="text-align:right">
                    <g:link class="show_more_link" controller="index" action="popdirectionAll">${message(code:'index.all_directions')}</g:link> <font color="#FF530D">&raquo;</font>
                  </li>
                </ul>
              </td>
              <td colspan="3" valign="top" align="center">
                <div class="body shadow" style="width:710px;min-height:460px">
                  <div class="page-topic paddtop" style="border:none">
                    <h1>${infotext['header'+context?.lang]?:''}</h1>                    
                    <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml> <!--noindex-->                   
                    <div class="notice" id="main_login_fail" style="display:none">
                      ${message(code:'login.error.fail')}
                    </div>
                    <g:formRemote id="mainloginForm" name="mainloginForm" method="post" url="[controller: 'error', action: 'login']" onSuccess="processLogResponse(e)">
                      <table class="form s" width="100%" cellpadding="5" cellspacing="0" border="0">
                        <tr>
                          <td colspan="2" style="padding:20px 5px"><b>${message(code:'login.account')}:</b></td>
                        </tr>
                        <tr>
                          <td><label for="user" nowrap="nowrap">${message(code:'login.user')}</label></td>
                          <td><input type="text" onblur="blurInput();" onfocus="focusLogin();" value="${message(code:'login.user')}" name="user" id="user" /></td>
                        </tr>
                        <tr>
                          <td><label for="password">${message(code:'login.password')}</label></td>
                          <td><input type="password" onblur="blurInput();" onfocus="focusPassword();" value="${message(code:'login.password')}" name="password" id="password" /></td>
                        </tr>                                                
                        <tr id="captcha_label" style="height:20px;${(session.user_enter_fail?:0)>user_max_enter_fail?'':'display: none'}">
                          <td colspan="2">${message(code:'login.captcha')}:</td>             
                        </tr>
                        <tr id="captcha_log" style="${(session.user_enter_fail?:0)>user_max_enter_fail?'height:50px;':'display: none;'}">
                          <td width="100" valign="middle" id="captcha_picture"><jcaptcha:jpeg name="image" width="120"/></td>
                          <td valign="middle">                        
                            <input id="captcha_text" type="text" name="captcha" value="" style="width: 150px" />
                            <img src="${resource(dir:'images',file:'reload.png')}" onclick="reloadCaptcha();" alt="${message(code:'captcha.update')}" border="0" align="absmiddle" style="margin-left:15px;"/>
                          </td>
                        </tr>
                        <tr>
                          <td><b style="color:#FF530D"><input type="checkbox" value="1" name="remember" />&nbsp;${message(code:'login.remember')}</b></td>
                          <td>
                            <input type="submit" class="button-glossy orange" value="${message(code:'label.login')}" />
                            <span id="passrestore2" style="margin-left:20px">
                              <b><g:link controller="user" action="restore" rel="nofollow">${message(code:'login.restore')}?</g:link></b>
                            </span>
                          </td>
                        </tr>
                      </table>
                      <input type="hidden" value="staytoday" name="provider" />
                      <input type="hidden" id="log_is_ajax" name="is_ajax" value="1" />
                      <input type="hidden" name="user_index" value="1" />
                      <input type="hidden" id="iService_log" name="service" value="0" />
                    </g:formRemote>                    
                    <table width="100%" cellpadding="5" cellspacing="0" border="0">
                      <tr>
                        <td colspan="2" style="padding:20px 5px"><b>${message(code:'login.social')}:</b></td>
                      </tr>
                      <tr>
                        <td width="354" valign="top" align="right">                      
                          <div id="fb_auth" class="fb-button">
                            <a href="javascript:void(0)" rel="nofollow" onclick="doFBlogin();">
                              <span class="fb-button-left"></span>
                              <span class="fb-button-center"><b>Facebook</b></span>
                              <span class="fb-button-right"></span>                            
                            </a>
                          </div>
                        </td>
                        <td align="left" valign="top">                      
                          <div id="main_vk_auth" class="vk-button">
                            <a href="javascript:void(0)" rel="nofollow" onclick="doLogin()">
                              <span class="vk-button-left"></span>
                              <span class="vk-button-center"></span>
                              <span class="vk-button-right"></span>
                            </a>                        
                          </div>
                        </td>
                      </tr>
                    </table>
                    <g:formRemote name="mainFacebookloginForm" url="[controller: 'error', action: 'facebook']" onSuccess="processLogResponse(e)">      
                      <input type="hidden" id="main_fb_id" name="m_fb_id" value="0" />
                      <input type="hidden" id="main_fb_name" name="fb_name" value="" />
                      <input type="hidden" id="main_fb_pic" name="fb_pic" value="" />
                      <input type="hidden" id="main_fb_photo" name="fb_photo" value="" />
                      <input type="hidden" id="main_fb_email" name="fb_email" value="" />
                      <input type="hidden" id="main_fb_ajax" name="is_ajax" value="1" />  
                      <input type="submit" id="main_Fb_submit_button" style="display:none" value="" />
                      <input type="hidden" id="iService_fb" name="service" value="0" />
                    </g:formRemote>
                    <g:formRemote name="mainVkloginForms" url="[controller: 'error', action: 'vk']" onSuccess="processLogResponse(e)">
                      <input type="hidden" id="main_vk_id" name="vk_id" value="0" />
                      <input type="hidden" id="main_vk_name" name="vk_name" value="" />
                      <input type="hidden" id="main_vk_pic" name="vk_pic" value="" />
                      <input type="hidden" id="main_vk_photo" name="vk_photo" value="" />
                      <input type="hidden" id="main_vk_hash" name="vk_hash" value="" />
                      <input type="hidden" id="main_vk_ajax" name="is_ajax" value="1" />
                      <input type="submit" id="main_Vk_submit_button" style="display:none" value="" />
                      <input type="hidden" id="iService_vk" name="service" value="0" />
                    </g:formRemote>
                    <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml><!--/noindex-->
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td colspan="4" class="paddtop">        
                <ul class="homepage_badges">
                <g:each in="${specoffer_records}" var="home" status="i">
                  <li style="margin-left:${(i==0)?'21':'10'}px">
                    <div class="badge-content-container shadow dark">
                      <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}" target="_blank">
                        <img width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0"/>
                        <div class="slideshow_item_details">
                          <div class="slideshow_item_details_text">
                            <div class="ss_details_top">
                              <span class="ss_name"><g:shortString text="${home?.name}" length="18"/></span><br/>
                              <span class="ss_location"><g:shortString text="${home?.shortaddress}" length="22"/></span>
                            </div>                            
                          </div>
                        </div>
                        <div class="photo_item_details">
                          <span class="ss_price b-rub">              
                            ${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>
                          </span>                            
                        </div>
                      </g:link>
                    </div> 
                  </li>
                </g:each>
                </ul>
              </td>
            </tr>            
          </table>
        </td>
      </tr>
      <tr style="height:120px">
        <td width="100%" align="center" valign="top" class="line bottom">
          <div class="relative" style="width:980px;height:120px">            
            <div class="footer">
              <table width="980" cellpadding="0" cellspacing="0" border="0">
                <tr style="height:120px" valign="middle">
                  <td width="250" align="left">
                    <img src="${resource(dir:'images',file:'logo.png')}" alt="StayToday" width="60" hspace="20" style="float:left" />
                    <div class="social">
                      <span class="copy">
                        <!--noindex-->MMXII &copy; StayToday<br/><!--/noindex-->
                        <g:link controller="index" action="terms">${Infotext.findByAction('terms')['name'+context?.lang]}</g:link>
                      </span><br/>
                      <a class="social-link" href="http://www.facebook.com/StayTodayRu" target="_blank" title="${message(code:'social.fb')}">
                        <i class="social-icon fb"></i>                        
                      </a>
                      <a class="social-link" href="http://twitter.com/StayTodayRu" target="_blank" title="${message(code:'social.tw')}">
                        <i class="social-icon tw"></i>                        
                      </a>
                      <a class="social-link" href="http://www.vk.com/StayToday" target="_blank" title="${message(code:'social.vk')}">
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
                      <g:elseif test="${controllerName=='index' && actionName=='index' && item.action in ['about','timeline','popdirectionAll','mobile']}"><a href="${createLink(controller:item.controller,action:item.action,base:context?.mainserverURL_lang)}">${item['name'+context?.lang]}</a></g:elseif>
                      <g:elseif test="${item.action in ['popdirectionAll','mobile','widget','timeline']}"><b class="link" onclick="transit(${context.is_dev?1:0},['${item.action!='popdirectionAll'?item.action:'alldirections'}'],'','','','${context?.lang}')">${item['name'+context?.lang]}</b></g:elseif>
                      <g:else><b class="link" onclick="transit(${context.is_dev?1:0},['${item.action}','${item.controller}'])">${item['name'+context?.lang]}</b></g:else>
                      </li>
                    </g:each>
                    </ul>
                  </td>
                  <td class="analytics">
                    <b class="tel">8 (800) 555-1768</b>
                    <div class="counters_code" align="center"><!--noindex-->
                      <script id="top100Counter" type="text/javascript" src="https://counter.rambler.ru/top100.jcn?2812368"></script><!--/noindex-->
                      <noscript>
                        <a href="http://top100.rambler.ru/navi/2812368/" rel="nofollow"><img src="http://counter.rambler.ru/top100.cnt?2812368" alt="Rambler's Top100" border="0" /></a>
                      </noscript>
                      <script type="text/javascript"><!--
                        document.write("<a href='http://www.liveinternet.ru/click' rel='nofollow' target='_blank' title='LiveInternet'><img src='//counter.yadro.ru/hit?t45.12;r"+
                        escape(document.referrer)+((typeof(screen)=="undefined")?"":
                        ";s"+screen.width+"*"+screen.height+"*"+(screen.colorDepth?screen.colorDepth:screen.pixelDepth))+";u"+escape(document.URL)+
                        ";h"+escape(document.title.substring(0,80))+";"+Math.random()+"' alt='LiveInternet' border='0' width='30' height='30'><\/a>")
                      //--></script>
                    </div>
                  </td>
                  <td align="right">
                    <div class="developers">
                      <img src="${resource(dir:'images',file:'design-logo.png')}" alt="${message(code:'label.designlogo')}" border="0" />
                      <span class="copyright">
                        ${message(code:'label.design')}<br/>${message(code:'label.designlogo')}<br/>
                        <a href="http://www.fntw.ru" rel="nofollow" target="_blank">www.fntw.ru</a>
                      </span>
                    </div>
                    <div class="developers" style="margin-top:10px">
                      <img src="${resource(dir:'images',file:'develop-logo.png')}" alt="CSI Software" border="0" />
                      <span class="copyright">
                        ${message(code:'label.develop')}<br/>CSI Software<br/>
                        <a href="http://www.trace.ru" rel="nofollow" target="_blank">www.trace.ru</a>                      
                      </span>
                    </div>                    
                  </td>
                </tr>
              </table>            
            </div>
          </div>    
        </td>
      </tr>
    </table>     
  <g:if test="${!user}"><!--noindex-->
    <script type="text/javascript">
      VK.init({ apiId: ${vk_api_key} });
      FB.init({ appId: ${fb_api_key}, status: true, cookie: true, xfbml: true, oauth: true });
    </script><!--/noindex-->
  </g:if>
  </body>
</html>
