<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
    <style type="text/css">.form label.nopadd { min-width: 120px }</style>
  </head>
  <body>
              <g:render template="/account_menu" />      
                    <g:if test="${infotext['itext'+context?.lang]?:''}">
                      <div style="padding:10px 20px">
                        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                      </div>
                    </g:if>
                      <h2><g:rawHtml>${result}</g:rawHtml></h2>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
