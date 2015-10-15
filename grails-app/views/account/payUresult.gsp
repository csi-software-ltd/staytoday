<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />    
  </head>
  <body>
              <g:render template="/account_menu" />      
                      <h2 class="toggle border"><span class="edit_icon detail"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                      <div style="padding:0 20px">
                        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                        <div class="dott"></div>                      
                        <p><g:if test="${result==0}">
                          <g:rawHtml>${message(code:'payUresult.notice',args:[inrequest?.payrefno])}</g:rawHtml>
                        </g:if><g:elseif test="${result==1}">
                          ${message(code:'payUresult.error.fail')}
                        </g:elseif><g:elseif test="${result==-1}">
                          ${message(code:'payUresult.error.qiwi')}
                        </g:elseif><g:else>
                          ${message(code:'payresult.error.false')}
                        </g:else></p>
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
