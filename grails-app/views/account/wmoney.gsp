<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />    
  </head>
  <body>
              <g:render template="/account_menu" />
                      <h2 class="toggle border"><span class="edit_icon detail"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                      <g:if test="${infotext?.itext}">
                        <div style="padding:0 20px;text-align:justify">
                          <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                        </div>
                      </g:if>                      
                      <form name="settinsForm" method="post" action="https://merchant.webmoney.ru/lmi/payment.asp">
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                          <tr>
                            <td colspan="2" style="padding:20px">
                              <input type="submit" class="button-glossy orange" value="${message(code:'button.pay')}"/>
                            </td>
                          </tr>
                        </table>
                        <input type="hidden" name="LMI_PAYMENT_AMOUNT" value="${purchaseamt}">
                        <input type="hidden" name="LMI_PAYMENT_DESC_BASE64" value="${orderdescription}">
                        <input type="hidden" name="LMI_PAYMENT_NO" value="${payorder.norder-'st'}">
                        <input type="hidden" name="LMI_PAYEE_PURSE" value="${configParams.merchant}">
                      </form>
                    </div> 
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
