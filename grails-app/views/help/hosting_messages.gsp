<html>
  <head>  
    <title>${infotext['title'+context?.lang]?:''}</title>  
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl}" />    
    <meta name="layout" content="main" />    
  </head>
  <body>
            <g:render template="/help_submenu" />
                        <g:rawHtml>${infotext['promotext1'+context?.lang]?:''}</g:rawHtml>
                        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                      </div>
                      <div rel="layer" style="display:none"></div>
                      <div rel="layer" style="display:none"></div>
                      <div rel="layer" style="display:none"></div>       
                    </div>
                  </div>
                </div>
              </td>
            </tr>
    <script type="text/javascript">
      $('ads_count').update('8');
    </script>            
  </body>
</html>
