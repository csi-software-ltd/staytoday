<html>
  <head>
    <title>${Infotext.findByControllerAndAction('account','liqpay')['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${Infotext.findByControllerAndAction('account','liqpay')?.keywords?:''}" />
    <meta name="description" content="${Infotext.findByControllerAndAction('account','liqpay')['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />    
  </head>
  <body>
    <div data-role="page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                      
            <li class="text" role="heading">${Infotext.findByControllerAndAction('account','liqpay')['header'+context?.lang]?:''}</li>
          </ul>
        </div>
      </div>
      <div data-role="content" class="st"> 
        <h2>${infotext['promotext1'+context?.lang]?:''}</h2>
        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
        
        <form name="settinsForm" method="post" action="https://ecommerce.liqpay.com/ecommerce/CheckOutPagen">
          <input type="submit" data-theme="f" value="${message(code:'button.pay')}"/>
          <input name="version" value="1.0.0" type="hidden" />
          <input name="orderid" value="${payorder.norder}" type="hidden" />
          <input name="merrespurl" value="${createLink(controller:'m',action:'liqpayresult',base:'https://m.staytoday.ru')}" type="hidden" />
          <input name="merid" value="${configParams.merid}" type="hidden" />
          <input name="acqid" value="${configParams.acqid}" type="hidden" />
          <input name="purchaseamt" value="${purchaseamt}" type="hidden" />
          <input name="purchasecurrencyexponent" value="2" type="hidden" />
          <input name="purchasecurrency" value="643" type="hidden" />
          <input name="orderdescription" value="${orderdescription}" type="hidden" />
          <input name="signature" value="${signature}" type="hidden" />          
        </form>
        
      </div>
    </div>
  </body>
</html>
