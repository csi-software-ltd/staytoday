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
                        <p>${infotext['promotext2'+context?.lang]?:''} <g:link controller="inbox" action="view" id="${mbox.id}">${message(code:'account.tombox')}</g:link></p>
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
