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
              <g:if test="${isLoginDenied}">
                <a class="button" href="javascript:void(0)" rel="nofollow" onclick="showLoginForm()">${message(code:'common.deliverhome')}</a>
              </g:if><g:else>
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>
              </g:else>
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
              <td colspan="3" valign="top">
                <div class="form shadow paddtop padd10" id="view-container">
                <g:if test="${infotext['itext'+context?.lang]}">
                  <p><g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml></p>
                </g:if>
                <g:if test="${inrequest?.error}">
                  <div class="notice">
                    <ul>
                      <g:if test="${inrequest?.error==1}"><li>${message(code:"restore.error.fail")}</li></g:if>
                      <g:if test="${inrequest?.error==2}"><li>${message(code:'error.incorrect',args:[message(code:"login.user")])}</li></g:if>
                      <g:if test="${inrequest?.error==3}"><li>${message(code:'add.error.incorrect.code')}</li></g:if>
                      <g:if test="${inrequest?.error==4}"><li>${message(code:'restore.error.need')} <g:link controller="user" action="index" rel="nofollow">${message(code:'add.error.login.social')}</g:link></li></g:if>
                      <g:if test="${inrequest?.error==5}"><li>${message(code:'restore.error.senddata')}</li></g:if>
                      <g:if test="${inrequest?.error==-100}"><li>${message(code:'login.error.bderror')}</li></g:if>
                    </ul>
                  </div>
                </g:if>                
                  <g:form url="[controller:'user',action:'rest']" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                    <table width="100%" cellpadding="3" cellspacing="0" border="0">
                      <tr>
                        <td colspan="3">
                          <label for="name">${message(code:'login.user')}</label>
                          <input type="text" name="name" value="${inrequest.name}" <g:if test="${inrequest?.error in [1,2]}">class="red"</g:if>/>
                        </td>        
                      </tr>
                      <tr style="height:50px">
                       <td width="195">
                          <h2 class="padd20" style="margin-bottom:0">${message(code:'add.code')}</h2>
                          <small class="padd20">${message(code:'add.code.info')}</small>                        
                        </td>
                        <td width="100" align="center" valign="middle" id="add_captcha_picture">
                          <jcaptcha:jpeg name="image" width="120"/>
                        </td>
                        <td valign="middle">
                          <input type="text" name="captcha" class="mini <g:if test="${inrequest?.error==3}">red</g:if>" value=""/>
                          <img src="${resource(dir:'images',file:'reload.png')}" onclick="addReloadCaptcha();" alt="${message(code:'captcha.update')}" title="${message(code:'captcha.update')}" border="0" align="absmiddle" style="margin-left:15px"/>
                        </td>
                      </tr>
                      <tr>
                        <td>&nbsp;</td>
                        <td colspan="2" style="padding:30px 0">
                          <input type="submit" class="button-glossy orange" value="${message(code:'button.recover')}" />
                        </td>
                      </tr>
                    </table>
                  </g:form>
                </div>
              </td>
            </tr>
  </body>
</html>
