<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
    <style type="text/css">.form label.nopadd { min-width: 120px }</style>
  </head>
  <body onload="\$('payer').focus()">
              <g:render template="/account_menu" />
                    <g:if test="${(payorder.payway_id==1 && infotext['itext'+context?.lang])||(payorder.payway_id!=1 && infotext['itext2'+context?.lang])}">
                      <div class="page-topic" style="border:none">
                        <g:rawHtml>${payorder.payway_id==1?(infotext['itext'+context?.lang]?:''):(infotext['itext2'+context?.lang]?:'')}</g:rawHtml>
                      </div>
                    </g:if>
                      <h2 class="toggle border"><span class="edit_icon detail"></span>${infotext['promotext1'+context?.lang]?:''}</h2>                      
                      <g:form name="settinsForm" url="[action:'payorderdocprint']" base="${context?.mainserverURL_lang}" target="_blank">
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                          <tr>
                            <td colspan="2">
                              <label for="payer" class="nopadd">${message(code:'paydocuments.fields').split(',')[0]}</label>
                              <input type="text" id="payer" name="payer" value="${payorder.plat_name}" maxlength="250" style="width:519px !important" />
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2">
                              <label for="payeraddress" class="nopadd">${message(code:'paydocuments.fields').split(',')[1]}</label>
                              <input type="text" id="payeraddress" name="payeraddress" value="${payorder.plat_address}" style="width:519px !important" />
                            </td>
                          </tr>
                          <tr>
                            <td <g:if test="${payorder.payway_id==1}">width="50%"</g:if><g:else>colspan="2"</g:else>>
                              <label for="payerinn" class="nopadd">${message(code:'paydocuments.fields').split(',')[2]}</label>
                              <input type="text" class="mini" id="payerinn" name="payerinn" value="${payorder.plat_inn}" maxlength="12" />
                            </td>
                          <g:if test="${payorder.payway_id==1}">
                            <td>
                              <label for="payerokpo" style="min-width:149px">${message(code:'paydocuments.fields').split(',')[3]}</label>
                              <input type="text" class="mini" id="payerokpo" name="payerokpo" value="${payorder.plat_okpo}" />
                            </td>
                          </g:if>
                          </tr>
                          <g:if test="${payorder.payway_id!=1}">
                          <tr>
                            <td colspan="2">
                              <label for="payeraccount" class="nopadd">${message(code:'paydocuments.fields').split(',')[4]}</label>
                              <input type="text" id="payeraccount" name="payeraccount" value="${payorder.plat_schet}" maxlength="20" />
                            </td>
                          </tr>
                          </g:if>
                          <tr>
                            <td colspan="2">
                              <label for="payprice" class="nopadd">${message(code:'paydocuments.fields').split(',')[5]}</label>
                              <input type="text" class="price" id="payprice" disabled name="payprice" value="${payorder.summa} руб." />
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:20px">                              
                              <input type="button" class="button-glossy orange" value="${message(code:payorder.payway_id==1?'account.print.invoice':'account.print.receipt')}" onclick="$('printtype').value=0;$('settinsForm').submit();"/>
                              <input type="button" class="button-glossy orange" value="${message(code:'paydocuments.import.pdf')}" onclick="$('printtype').value=1;$('settinsForm').submit();"/>
                            </td>
                          </tr>
                          <input type="hidden" value="0" name="type" id="printtype"/>
                          <input type="hidden" value="${payorder.norder}" name="order_id"/>
                        </table>
                      </g:form>
                    </div>
                  </div>
                </div>
              </td>
            </tr>                          
  </body>
</html>
