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
                    <g:if test="${infotext['itext'+context?.lang]}">
                      <div style="padding:0 20px">
                        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                      </div>
                    </g:if>
                      <form name="settinsForm" method="post" action="https://ecommerce.liqpay.com/ecommerce/CheckOutPagen">
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                          <tr>
                            <td colspan="2" style="padding:20px">
                              <input type="submit" class="button-glossy orange" value="${message(code:'button.pay')}"/>
                            </td>
                          </tr>
                          <input name="version" value="1.0.0" type="hidden" />
                          <input name="orderid" value="${payorder.norder}" type="hidden" />
                          <input name="merrespurl" value="${createLink(controller:'account',action:'liqpayresult',absolute:true,base:context.sequreServerURL)}" type="hidden" />
                          <input name="merid" value="${configParams.merid}" type="hidden" />
                          <input name="acqid" value="${configParams.acqid}" type="hidden" />
                          <input name="purchaseamt" value="${purchaseamt}" type="hidden" />
                          <input name="purchasecurrencyexponent" value="2" type="hidden" />
                          <input name="purchasecurrency" value="643" type="hidden" />
                          <input name="orderdescription" value="${orderdescription}" type="hidden" />
                          <input name="signature" value="${signature}" type="hidden" />
                        </table>
                      </form>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
