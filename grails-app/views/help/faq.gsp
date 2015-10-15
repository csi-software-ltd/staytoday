<html>
  <head>  
    <title>${infotext['title'+context?.lang]?:''}</title>  
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${createLink(controller:'help',action:'faq',base:context?.mainserverURL_lang)}" />
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(uri:'/help',base:context?.mobileURL_lang)}" />
    <link rel="alternate" media="handheld" href="${createLink(uri:'/help',base:context?.mobileURL_lang)}" />    
    <meta name="layout" content="main" />
    <script type="text/javascript">
      if(window.screen.width < 640)
        window.location = "${createLink(uri:'/help',base:context?.mobileURL_lang)}";   
    </script>    
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
      $('ads_count').update('6');
    </script>            
  </body>
</html>
