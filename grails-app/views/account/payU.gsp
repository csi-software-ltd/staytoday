<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />    
  </head>
  <body>
              <g:render template="/account_menu" />
                      <h2 class="toggle border"><span class="edit_icon detail"></span> ${infotext['promotext'+(payway?.id in [4,6]?'2':'1')+context?.lang]?:''}${payway?.id==4?'WebMoney':payway?.id==6?'Qiwi':''}</h2>
                      <g:if test="${infotext?.itext}">
                        <div style="padding:0 20px;text-align:justify">
                          <g:rawHtml>${infotext['itext'+(payway?.id==4?'2':payway?.id==6?'3':'')+context?.lang]?:''}</g:rawHtml>
                        </div>
                      </g:if>
                      <form name="settinsForm" method="post" action="https://secure.payu.ru/order/lu.php">
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                          <tr>
                            <td colspan="2" style="padding:20px">
                              <input type="submit" class="button-glossy orange" value="${message(code:'button.pay')}"/>
                            </td>
                          </tr>
                        </table>
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
                        <input name="BACK_REF" type="hidden" value="${createLink(controller:'account',action:'payUresult',base:context.sequreServerURL)}" />
                        <input name="LANGUAGE" type="hidden" value="${((context?.lang?:'RU')-'_').toUpperCase()}" />
                      </form>
                    </div> 
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
