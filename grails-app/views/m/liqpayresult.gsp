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
        <p><g:if test="${is_succes==1}">
          ${message(code:'liqpayresult.notice')} 
          <a href="${createLink(controller:'m',action:'mbox',id:mbox.id,base:context?.mobileURL_lang)}">${message(code:'liqpayresult.mbox')}</a>
        </g:if><g:elseif test="${is_succes==0}">
          ${errortext} 
          <a href="${createLink(controller:'m',action:'mbox',id:mbox.id,base:context?.mobileURL_lang)}">${message(code:'liqpayresult.mbox')}</a>
        </g:elseif><g:elseif test="${is_succes==2}">
          ${message(code:'liqpayresult.fail')}
        </g:elseif><g:else>
          ${message(code:'payresult.error.false')}
        </g:else></p>
      </div>
    </div>              
  </body>
</html>
