<html>
  <head>
    <title>${Infotext.findByControllerAndAction('account','payU')['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${Infotext.findByControllerAndAction('account','payU')?.keywords?:''}" />
    <meta name="description" content="${Infotext.findByControllerAndAction('account','payU')['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />    
  </head>
  <body>
    <div data-role="page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                      
            <li class="text" role="heading">${Infotext.findByControllerAndAction('account','payU')['header'+context?.lang]?:''}</li>
          </ul>
        </div>
      </div>
      <div data-role="content" class="st">  
        <h2>${Infotext.findByControllerAndAction('account','payU')['promotext'+(payway?.id in [4,6]?'2':'1')+context?.lang]?:''}${payway?.id==4?'WebMoney':payway?.id==6?'Qiwi':''}</h2>
        <g:rawHtml>${Infotext.findByControllerAndAction('account','payU')['itext'+(payway?.id==4?'2':payway?.id==6?'3':'')+context?.lang]?:''}</g:rawHtml>
        
        <form name="settinsForm" method="post" action="https://secure.payu.ru/order/lu.php">
          <input type="submit" data-theme="f" value="${message(code:'button.pay')}" />          
          <input name="MERCHANT" type="hidden" value="${configParams.merchant}" />
          <input name="ORDER_REF" type="hidden" value="${payorder.norder}" />
          <input name="ORDER_DATE" type="hidden" value="${orderdate}" />
          <input name="ORDER_PNAME[]" type="hidden" value="${orderdescription}" />
          <input name="ORDER_PCODE[]" type="hidden" value="${payorder.norder}" />
          <input name="ORDER_PRICE[]" type="hidden" value="${payorder.summa}" />
          <input name="ORDER_PRICE_TYPE[]" type="hidden" value="NET" />
          <input name="ORDER_QTY[]" type="hidden" value="1" />
          <input name="ORDER_VAT[]" type="hidden" value="0" />
          <input name="ORDER_SHIPPING" type="hidden" value="0" />
          <input name="PRICES_CURRENCY" type="hidden" value="RUB" />
          <input name="PAY_METHOD" type="hidden" value="${payway?.payumethod?:'CCVISAMC'}" />
          <input name="ORDER_HASH" type="hidden" value="${signature}" />
          <input name="BACK_REF" type="hidden" value="${createLink(controller:'m',action:'payUresult',base:'https://m.staytoday.ru')}" />
          <input name="LANGUAGE" type="hidden" value="${((context?.lang?:'RU')-'_').toUpperCase()}" />
        </form>
        
      </div> 
    </div>
  </body>
</html>
