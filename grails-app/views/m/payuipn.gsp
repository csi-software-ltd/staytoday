﻿<html>
  <head>
    <title>${Infotext.findByControllerAndAction('account','payuipn')['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${Infotext.findByControllerAndAction('account','payuipn')?.keywords?:''}" />
    <meta name="description" content="${Infotext.findByControllerAndAction('account','payuipn')['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />    
  </head>
  <body>
    <div data-role="page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                      
            <li class="text" role="heading">${Infotext.findByControllerAndAction('account','payuipn')['header'+context?.lang]?:''}</li>
          </ul>
        </div>
      </div>
      <div data-role="content" class="st">  
        <g:rawHtml>${Infotext.findByControllerAndAction('account','payuipn')['itext'+context?.lang]?:''}</g:rawHtml>
        <h2><g:rawHtml>${result}</g:rawHtml></h2>
      </div>
    </div>    
  </body>
</html>
