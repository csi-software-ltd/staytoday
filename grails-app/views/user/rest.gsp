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
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>
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
                <g:if test="${inrequest?.error==0}">
                  <div class="notice">
                    ${message(code:'restore.notice')}
                  </div>
                </g:if><g:else>
                  <div class="notice">
                    <ul>
                      <g:if test="${inrequest?.error==1}"><li>${message(code:'restore.error.fail')}</li></g:if>
                      <g:elseif test="${inrequest?.error==2}"><li>${message(code:'error.incorrect',args:[message(code:"login.user")])}</li></g:elseif>
                      <g:elseif test="${inrequest?.error==3}"><li>${message(code:'add.error.incorrect.code')}</li></g:elseif>
                      <g:elseif test="${inrequest?.error==4}"><li>${message(code:'login.error.bderror')}</li></g:elseif>
                    </ul>
                  </div> 
                </g:else>
                </div>
              </td>
            </tr>
  </body>
</html>
