<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />   
    <meta name="layout" content="main" />
    <style type="text/css">
      ul.verifications-list li.verifications-list-item .label { line-height: 17px !important; }
    </style>
  </head>
  <body>
              <g:render template="/ads_menu" />                                        
                      <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html> 
