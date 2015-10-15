<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />         
    <meta name="layout" content="m" />
  </head>
  <body>
      <h1 role="heading">${infotext['header'+context?.lang]?:''}</h1>      
      <div data-role="content">
        <div class="notice"> 
          <ul>
            <g:if test="${inrequest?.error==2}"><li>${message(code:'confirm.error')}1</li></g:if>
            <g:elseif test="${flash?.error==1}"><li>${message(code:'confirm.error')}2</li></g:elseif>
            <g:else><li>${message(code:'verify.email.confirmed')}</li></g:else>
          </ul>
        </div>
      </div>
  </body>
</html>
