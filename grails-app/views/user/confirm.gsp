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
                <a <g:if test="${isLoginDenied}">onclick="showLoginForm()"</g:if><g:else>href="<g:createLink controller='home' action='addnew'/>"</g:else>>${message(code:'common.deliverhome')}</a>
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
                <div id="view-container" class="paddtop">
                  <div class="notice">
                    <ul>
                      <g:if test="${inrequest?.error==2}"><li>${message(code:'confirm.error')}1</li></g:if>
                      <g:elseif test="${flash?.error==1}"><li>${message(code:'confirm.error')}2</li></g:elseif>
                      <g:else><li>${message(code:'verify.email.confirmed')}</li></g:else>
                    </ul>
                  </div>
                </div>
              </td>
            </tr>          
  </body>
</html>
