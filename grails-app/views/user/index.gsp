<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>  
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />     
    <meta name="layout" content="main" />
    <g:javascript>
    function addReloadCaptcha(){
      <g:remoteFunction controller='index' action='reloadCaptcha' onSuccess='processRlResponse(e)'/>
    }
    function processRlResponse(e){
      $('add_captcha_picture').innerHTML = e.responseJSON.captcha;
      $('add_captcha_picture').firstChild.setStyle({width: '120px'});
    }
    function initialize(){      
      $("view-container").setStyle({ minHeight: window.innerHeight - ${(inrequest?.error)?'465':'415'} + 'px' });      
    }
    </g:javascript>     
  </head>
  <body onload="initialize()">
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">                
                <g:link class="button" controller="home" action="addnew">${message(code:'common.deliverhome')}</g:link>
              </td>
              <td colspan="3" class="rent ss">
                <h1>${infotext['header'+context?.lang]?:''}</h1>
              </td>
            </tr>
            <tr>              
              <td colspan="3" class="bg_shadow">              
                <div class="breadcrumbs">
                  <a href="${g.createLink(uri:'',base:context?.mainserverURL_lang)}">${message(code:'label.main')}</a> &#8594; ${infotext['header'+context?.lang]?:''}
                </div>
              </td>
            </tr>            
            <tr>
              <td>&nbsp;</td>
              <td colspan="3">                
              <g:if test="${flash?.error}">
                <div class="notice">
                  <ul>
                    <g:if test="${flash?.error== 1}"><li>${message(code:"error.blank",args:[message(code:'login.user').capitalize()])}</li></g:if>		
                    <g:if test="${flash?.error== 2}"><li>${message(code:"error.blank",args:[message(code:'login.password').capitalize()])}</li></g:if>
                    <g:if test="${flash?.error == 51}"><li>${message(code:'login.error.fail')}</li></g:if>					
                    <g:if test="${flash?.error == 5}"><li>${message(code:'login.error.ban')}</li></g:if>
                    <g:if test="${flash?.error == 3}"><li>${message(code:'login.error.auth')}</li></g:if>
                    <g:if test="${flash?.error == 6}"><li>${message(code:'login.error.noauth')}</li></g:if>
                    <g:if test="${flash?.error == 99}"><li>${message(code:'add.error.incorrect.code')}</li></g:if>
                  </ul>
                </div>
              </g:if>
              <g:if test="${!user}"> 
                <div class="form shadow paddtop padd10" id="view-container">                
                  <g:form name="loginForms" controller="user" action="login" base="${context?.mainserverURL_lang}">
                    <g:collect in="${inrequest}" expr="it"><g:if test="${it.key!='user'}">
                    <input type="hidden" name="${it.key}" value="${it.value}">
                    </g:if></g:collect>
                    <table width="100%" cellpadding="3" cellspacing="0" border="0">
                      <tr>
                        <td colspan="2">
                          <h2 class="padd20 float">${message(code:'login.account')}:</h2>
                          <b class="col" style="margin-right:115px">
                          <g:if test="${(inrequest?.control=='user')&&(inrequest?.act=='addnew')}">
                            <g:link controller="user" action="addnew" params="${[code:inrequest?.code?:'',home_id:inrequest?.home_id?:0]}" rel="nofollow">
                              ${message(code:'label.signup')}
                            </g:link>
                          </g:if>
                          </b>
                        </td>
                      </tr>
                      <tr>
                        <td><label for="user">${message(code:'login.user')}</label></td>
                        <td><input type="text" name="user" value="${inrequest?.user?:''}" <g:if test="${flash?.error == 1}">class="red"</g:if>/></td>
                      </tr>
                      <tr>
                        <td><label for="password">${message(code:'login.password')}</label></td>
                        <td><input type="password" name="password" value="" <g:if test="${flash?.error == 2}">class="red"</g:if>/></td>          
                      </tr>
                    <g:if test="${(session.user_enter_fail?:0)>inrequest.user_max_enter_fail}">
                      <tr>
                        <td colspan="2">
                          <label>${message(code:'add.code')}</label>
                          <small class="padd20">${message(code:'add.code.info')}</small>                        
                        </td>
                      </tr>
                      <tr style="height:50px">
                        <td width="100" align="center" valign="middle" id="add_captcha_picture">                        
                          <jcaptcha:jpeg name="image" />
                        </td>
                        <td>
                          <input type="text" name="captcha" value="" class="mini <g:if test="${flash?.error == 99}">red</g:if>"/>
                          <img src="${resource(dir:'images',file:'reload.png')}" onclick="addReloadCaptcha();" alt="${message(code:'captcha.update')}" title="${message(code:'captcha.update')}" border="0" align="absmiddle" style="margin-left:15px"/>
                        </td>	
                      </tr>
                    </g:if>                      
                      <tr>
                        <td class="padd20">
                          <b style="color:#FF530D"><input type="checkbox" id="remember" name="remember" <g:if test="${inrequest?.remember}">checked</g:if>/>&nbsp;${message(code:'login.remember')}</b>
                        </td>
                        <td>
                          <input type="submit" class="button-glossy orange" value="${message(code:'label.login')}"/>
                          <span id="passrestore2" class="blue" style="margin-left:20px">
                            <b><g:link controller="user" action="restore" rel="nofollow">${message(code:'login.restore')}?</g:link></b>
                          </span>              
                        </td>
                      </tr>
                    </table>
                    <input type="hidden" name="provider" value="staytoday"/>
                    <input type="hidden" name="user_index" value="1"/>
                  </g:form>
                  
                </div>
              </g:if>
              </td>
            </tr>
  </body>
</html>
