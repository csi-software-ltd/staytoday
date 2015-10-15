<html>
  <head>
    <title>${curinfotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${curinfotext?.keywords?:''}" />
    <meta name="description" content="${curinfotext['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />    
  </head>
  <body>
    <div data-role="page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                      
            <li class="text" role="heading">${curinfotext['header'+context?.lang]?:''}</li>
          </ul>
        </div>
      </div>
      <div data-role="content" class="st">   
        <h2>${curinfotext['promotext1'+context?.lang]?:''}</h2>                      
        <g:rawHtml>${curinfotext['itext'+context?.lang]?:''}</g:rawHtml>
        <p>${curinfotext['promotext2'+context?.lang]?:''}. 
          <a href="${createLink(controller:'m',action:'mbox',id:mbox.id,base:context?.mobileURL_lang)}">${message(code:'account.tombox')}</a>
        </p>
      </div>
    </div>            
  </body>
</html>
