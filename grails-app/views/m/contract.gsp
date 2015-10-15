<html>
  <head>
    <title>${Infotext.findByControllerAndAction('index','contract')['title'+context?.lang]}</title>    
    <meta name="description" content="${Infotext.findByControllerAndAction('index','contract')['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />
    <link rel="canonical" href="${createLink(controller:'index',action:'contract',base:context?.absolute_lang)}" />
  </head>
  <body>
    <div data-role="page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                      
            <li class="text" role="heading">${Infotext.findByControllerAndAction('index','contract')['header'+context?.lang]}</li>
          </ul>
        </div>
      </div>
      <div data-role="content" class="st">               
        <g:rawHtml>${Infotext.findByControllerAndAction('index','contract')['itext'+context?.lang]}</g:rawHtml>        
      </div>
    </div>
  </body>
</html>
