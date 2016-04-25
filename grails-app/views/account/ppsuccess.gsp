<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />    
  </head>
  <body>
              <g:render template="/account_menu" />
                      <h2 class="toggle border"><span class="edit_icon detail"></span><g:if test="${paymenttype==1}">${infotext['promotext1'+context?.lang]?:''}</g:if><g:else>${infotext['promotext2'+context?.lang]?:''}</g:else></h2>
                      <div style="padding:0 20px">
                      <g:if test="${paymenttype==1}">
                        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                        <div class="dott"></div>
                        <p><g:link controller="inbox" action="view" id="${mbox.id}">${message(code:'account.tombox')}</g:link></p>
                      </g:if><g:else>
                        <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                        <div class="dott"></div>
                        <p><g:link controller="account" action="partner">${message(code:'account.toaccount')}</g:link></p>
                      </g:else>
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
