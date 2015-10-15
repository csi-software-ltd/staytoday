<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>  
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />       
    <meta name="layout" content="main" />
    <g:javascript>
    function initialize(){      
      $("view-container").setStyle({ minHeight: window.innerHeight - 385 + 'px' });      
    }    
    </g:javascript>
  </head>
  <body onload="initialize()">
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">                
              <g:if test="${isLoginDenied}">
                <a class="button" href="javascript:void(0)" rel="nofollow" onclick="showLoginForm()">${message(code:'common.deliverhome')}</a>
              </g:if><g:else>
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>
              </g:else>
              </td>
              <td colspan="3" class="rent ss">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>
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
                <div id="view-container" class="paddtop">
                <g:if test="${inrequest.error==-1}">
                  <div class="notice">
                    ${message(code:'password.change.notice')}
                  </div>
                </g:if><g:else>
                  <g:if test="${inrequest.error}">
                  <div class="notice">
                    <ul>
                      <g:if test="${inrequest?.error==1}"><li>${message(code:'login.error.match')}</li></g:if>
                      <g:elseif test="${inrequest?.error==2}"><li>${message(code:'login.error.notenough')}</li></g:elseif>
                      <g:elseif test="${inrequest?.error==3}"><li>${message(code:'login.error.bderror')}</li></g:elseif>
                    </ul>
                  </div>
                  </g:if>
                  <div class="form shadow paddtop padd20">
                    <g:form url="[controller:'user',action:'passwsetup']" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                      <table width="100%" cellpadding="3" cellspacing="0" border="0">
                        <tr>
                          <td>
                            <label for="password1">${message(code:'register.password')}</label>
                            <input type="password" name="password1" value="" <g:if test="${inrequest?.error==2}">class="red"</g:if> />
                          </td>                        
                        </tr>
                        <tr>
                          <td>
                            <label for="password2">${message(code:'register.password.confirm')}</label>
                            <input type="password" name="password2" value="" <g:if test="${inrequest?.error == 1}">class="red"</g:if>/>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:10px 0 10px 205px">
                            <input type="submit" class="button-glossy orange" value="${message(code:'button.change')}"/>
                          </td>
                        </tr>
                      </table>
                    </g:form>
                  </div>
                </g:else>
                </div>
              </td>
            </tr>
  </body>
</html>
