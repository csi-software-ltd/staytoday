<html>
  <head>
    <title>${Infotext.findByControllerAndAction('account','wmoney')['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${Infotext.findByControllerAndAction('account','wmoney')?.keywords?:''}" />
    <meta name="description" content="${Infotext.findByControllerAndAction('account','wmoney')['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />    
  </head>
  <body>
    <div data-role="page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                      
            <li class="text" role="heading">${Infotext.findByControllerAndAction('account','wmoney')['header'+context?.lang]?:''}</li>
          </ul>
        </div>
      </div>
      <div data-role="content" class="st">   
        <h2>${Infotext.findByControllerAndAction('account','wmoney')['promotext1'+context?.lang]?:''}</h2>
        <g:rawHtml>${Infotext.findByControllerAndAction('account','wmoney')['itext'+context?.lang]?:''}</g:rawHtml>
        
        <form name="settinsForm" method="post" action="https://merchant.webmoney.ru/lmi/payment.asp">
          <input type="submit" data-theme="f" value="${message(code:'button.pay')}"/>
          <input type="hidden" name="LMI_PAYMENT_AMOUNT" value="${purchaseamt}">
          <input type="hidden" name="LMI_PAYMENT_DESC_BASE64" value="${orderdescription}">
          <input type="hidden" name="LMI_PAYMENT_NO" value="${payorder.norder-'st'}">
          <input type="hidden" name="LMI_PAYEE_PURSE" value="${configParams.merchant}">
        </form>
        
      </div> 
    </div>
  </body>
</html>
