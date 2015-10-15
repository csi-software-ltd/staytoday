<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru">
  <head>
    <title><g:layoutTitle default="StayToday" /></title>
    <meta http-equiv="content-language" content="ru" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />      
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />               
    <meta name="copyright" content="StayToday" />    
    <meta name="resource-type" content="document" />
    <meta name="document-state" content="dynamic" />
    <meta name="revisit" content="1" />
    <meta name="viewport" content="width=1000,maximum-scale=1.0" />     
    <meta name="robots" content="noindex,nofollow" />   
    <g:layoutHead />
    <link rel="stylesheet" href="${resource(dir:'css',file:'style.css')}" />    
    <g:javascript library="application" />
    <g:javascript library="prototype/prototype" />
    <g:if test="${controllerName=='administrators' && (actionName=='adressadd' || actionName=='moderateHome')}">
      <g:javascript library='prototype/effects' />
      <g:javascript library='prototype/controls' />
    </g:if>
    <calendar:resources lang="ru" theme="tiger"/>
    <g:if test="${controllerName=='administrators' && (actionName=='editbanners' || actionName=='adressadd' || actionName=='moderateHome')}">    
      <script src="https://api-maps.yandex.ru/2.0/?load=package.standard&lang=ru-RU" type="text/javascript"></script>
    </g:if>
    <r:layoutResources/>
  </head>
  <body onload="${pageProperty(name:'body.onload')}">
    <table class="topmenu" width="100%" height="100%" cellpadding="0" cellspacing="0" border="0" align="center" <g:if test="${actionName=='moderateHome'}">onclick="checkDropDowns();"</g:if>>
      <tr>
        <td width="100%" align="center">
          <table width="1000" cellpadding="0" cellspacing="0" border="0">
            <tr height="54">
              <td>
                <table width="100%" cellpadding="0" cellspacing="0" border="0">
                  <tr>
                    <td class="header-list-item" style="border:none">
                      Административное приложение &nbsp;&nbsp;
                      <span id="logo">
                        <img height="40" src="${resource(dir:'images',file:'logo.png')}" alt="StayToday" align="absmiddle" border="0" />
                      </span>
                    </td>
                  <g:if test="${admin}">                        
                    <td class="header-list-item button_user">
                      <a href="javascript:void(0);">
                        <span class="icon"></span>
                        <span id="header_user">${admin?.login?:''}</span>                        
                      </a>                      
                    </td>
                  <g:if test="${admin?.menu?.find{it.id==4}}">
                    <td class="header-list-item button_home <g:if test="${admin.home_notmoderate_count>0}">actively</g:if>">
                      <a <g:if test="${admin.home_notmoderate_count==0}">class="no_active"</g:if> href="<g:createLink controller='administrators' action='${admin.home_notmoderate_count==1?'moderateHome':'homes'}' id="${admin.home_notmoderate_count==1?admin.home_notmoderate_id:''}" base="${context?.mainserverURL_lang}"/>">
                        <span class="icon"></span>
                      <g:if test="${admin.home_notmoderate_count}">
                        <div class="unread_count">${admin.home_notmoderate_count}</div>
                      </g:if>
                      </a>
                    </td>
                  </g:if>                  
                  <g:if test="${admin?.menu?.find{it.id==16}}">
                    <td class="header-list-item button_waiting <g:if test="${admin.mbox_notmoderate_count>0}">actively</g:if>">
                      <a <g:if test="${admin.mbox_notmoderate_count==0}">class="no_active"</g:if> href="<g:createLink controller='administrators' action='${admin.mbox_notmoderate_count==1?'mboxDetails':'mbox'}' id="${admin.mbox_notmoderate_count==1?admin.mbox_notmoderate_id:''}" base="${context?.mainserverURL_lang}"/>">
                        <span class="icon"></span>
                      <g:if test="${admin.mbox_notmoderate_count}">
                        <div class="unread_count">${admin.mbox_notmoderate_count}</div>
                      </g:if>
                      </a>
                    </td>
                  </g:if>
                  <g:if test="${admin?.menu?.find{it.id==28}}">
                    <td class="header-list-item button_booking <g:if test="${admin.trip_notread_count>0}">actively</g:if>">
                      <a <g:if test="${admin.trip_notread_count==0}">class="no_active"</g:if> href="<g:createLink controller='adminbilling' action='trip'/>">
                        <span class="icon"></span>
                      <g:if test="${admin.trip_notread_count}">
                        <div class="unread_count">${admin.trip_notread_count}</div>
                      </g:if>
                      </a>
                    </td>
                  </g:if>
                  <g:if test="${admin?.menu?.find{it.id==25}}">
                    <td class="header-list-item button_inbox <g:if test="${admin.paytask_notcomplete_count>0}">actively</g:if>">
                      <a <g:if test="${admin.paytask_notcomplete_count==0}">class="no_active"</g:if> href="<g:createLink controller='adminbilling' action='paytask'/>">
                        <span class="icon"></span>
                      <g:if test="${admin.paytask_notcomplete_count}">
                        <div class="unread_count">${admin.paytask_notcomplete_count}</div>
                      </g:if>
                      </a>
                    </td>
                  </g:if>
                    <td class="header-list-item" valign="middle" nowrap style="border:none">
                      <g:form name="menuForm" url="[controller:'administrators',action:'menu']" method="post">
                        <label style="line-height:29px" for="menu">Меню</label>&nbsp;
                        <select name="menu" id="menu" onChange="$('menuForm').submit();">
                        <g:each in="${admin?.menu}" var="item">
                          <option value="${item.id}" <g:if test="${action_id==item.id}">selected="selected"</g:if>>${item.name}</option>
                        </g:each>
                        </select>                        
                      </g:form>
                    </td>
                    <td align="right" class="header-list-item link" style="border:none">
                      <g:link controller="administrators" action="logout">Выйти</g:link>                      
                    </td>
                  </g:if>                    
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>  
        <td align="left">
        <g:if test="${session.attention_message!='' && session.attention_message!=null}">      
          <div class="your-space drop_shadow">
            <div class="button-glossy lightblue header form">
              <h1>Сообщение</h1>
            </div>                            
            <p>${session.attention_message}</p>            
          </div>        
        </g:if>
        </td>
      </tr>
    <g:if test="${(controllerName=='administrators' && !(actionName in ['index','profile','infotextadd','adressadd','mailing_templateadd']))||controllerName=='stats'||controllerName=='adminbilling'}">
      <tr height="100%">
        <td align="left">
          <div class="your-space place drop_shadow">
            <div class="button-glossy green header" style="margin-bottom:-1px">
            <g:each in="${admin?.menu}" var="item">
              <g:if test="${action_id==item.id}">
              <h1>${item.name}</h1>
              </g:if>
            </g:each>
            </div>
            <g:layoutBody />
          </div>
        </td>
      </tr>         
    </g:if><g:else>
      <tr height="100%">
        <td align="left">        
          <g:layoutBody />          
        </td>
      </tr>
    </g:else>
    </table>
    <r:layoutResources/>
  </body>
</html>
