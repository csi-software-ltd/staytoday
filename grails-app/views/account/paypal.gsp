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
                    <g:if test="${(ppresponse.ACK=='Success'||ppresponse.ACK=='SuccessWithWarning')&&ppresponse.TOKEN}">
                      <g:if test="${infotext?.itext}">
                        <div style="padding:0 20px;text-align:justify">
                          <g:rawHtml>${(infotext['itext'+context?.lang]?:'').replace('[@PRICE]',payorder.summa.toString())}</g:rawHtml>
                        </div>
                      </g:if>                      
                      <form name="settinsForm" method="post" action="${configParams.paymenturl}">
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                          <tr>
                            <td colspan="2" style="padding:20px">
                              <input type="hidden" name="cmd" value="_express-checkout">
                              <input type="hidden" name="token" value="${ppresponse.TOKEN}">
                              <input type="submit" class="button-glossy orange" value="${message(code:'button.pay')}"/>
                            </td>
                          </tr>
                        </table>
                      </form>
                    </g:if><g:elseif test="${infotext?.itext2}">
                        <div style="padding:0 20px;text-align:justify">
                          <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                        </div>
                    </g:elseif>
                    </div> 
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
