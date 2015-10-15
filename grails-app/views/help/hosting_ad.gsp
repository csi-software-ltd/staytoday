<html>
  <head>  
    <title>${infotext['title'+context?.lang]?:''}</title>  
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl}" />    
    <meta name="layout" content="main" />    
    <g:javascript src="jquery-1.8.3.js" />    
    <g:javascript>
    var j=jQuery.noConflict();
    j(document).ready(function() {       
      j("ul.faq_list li.question a").click(function(){
        var li=j(this).parent(),
            name=li.attr('id').substring(8),
            div=j("#answer"+name);
        j('.answer.active').removeClass('active');
        if(div.className=='answer.active')
          div.removeClass('active');
        else
          div.addClass('active');
      });      
    });
    </g:javascript>  
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
      $('ads_count').update('9');
    </script>            
  </body>
</html>
