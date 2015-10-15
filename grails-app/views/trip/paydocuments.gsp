<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
    <g:javascript>
    </g:javascript>
  </head>
  <body onload="\$('payer').focus()">
              <g:render template="/trip_menu" />
                    <g:if test="${infotext['itext'+context?.lang]}">
                      <div class="page-topic">
                        ${infotext['itext'+context?.lang]?:''}
                      </div><br/>
                    </g:if>
                      <h2 class="toggle border"><span class="edit_icon detail"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                      <ul class="description_details padd10" style="float:none;width:auto;height:auto">
                        <li class="clearfix">
                          <span class="property">Получатель платежа</span>
                          <span class="value">${client?.payee?:''}</span>
                        </li>
                        <li class="clearfix">
                          <span class="property">ИНН</span>
                          <span class="value">${client?.inn?:''}</span>
                        </li>
                        <li class="clearfix">
                          <span class="property">КПП</span>
                          <span class="value">${client?.kpp?:''}</span>
                        </li>
                        <li class="clearfix">
                          <span class="property">Название банка</span>
                          <span class="value">${client?.bankname?:''}</span>
                        </li>
                        <li class="clearfix">
                          <span class="property">БИК</span>
                          <span class="value">${client?.bik?:''}</span>
                        </li>
                        <li class="clearfix">
                          <span class="property">Корреспондентский счет</span>
                          <span class="value">${client?.corraccount?:''}</span>
                        </li>
                        <li class="clearfix">
                          <span class="property">Расчетный счет</span>
                          <span class="value">${client?.settlaccount?:''}</span>
                        </li>
                        <li class="clearfix">
                          <span class="property">Величина НДС</span>
                          <span class="value">${client?.nds?:''}</span>
                        </li>
                        <li class="clearfix">
                          Комментарий к платежу - ${client?.paycomment?:''}
                        </li>
                      </ul>
                      <div class="padd20">
                        ${client?.paymessage?:''}<br/>
                        <input type="button" class="button-glossy orange" value="${message(code:'account.print.details')}" onclick="$('paymenttype').value=1;$('settinsForm').submit();"/>
                      </div><br/>
                      <h2 class="toggle border"><span class="edit_icon detail"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                      <g:form name="settinsForm" url="[action:'tripdocprint']" target="_blank" base="${context?.mainserverURL_lang}">
                        <table class="form" width="100%" cellpadding="5" cellspacing="0" border="0">
                          <tr>
                            <td nowrap>
                              <label for="payer" class="nopadd">Плательщик (Ф.И.О.)</label>
                              <input type="text" id="payer" name="payer" value="" maxlength="250" onload="alert(1)" />
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="payeraddress" class="nopadd">Адрес плательщика</label>
                              <input type="text" id="payeraddress" name="payeraddress" value="" />
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="payerinn" class="nopadd">ИНН плательщика</label>
                              <input type="text" id="payerinn" name="payerinn" value="" maxlength="12" />
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="payeraccount" class="nopadd">№ лиц. счета плательщика</label>
                              <input type="text" id="payeraccount" name="payeraccount" value="" maxlength="20" />
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="payprice" class="nopadd">Сумма платежа</label>
                              <input type="text" class="price" id="payprice" name="payprice" value="${trip.price_rub} руб." />
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:20px">                              
                              <input type="button" class="button-glossy orange" value="${message(code:'account.print.receipt')}" onclick="$('paymenttype').value=0;$('settinsForm').submit();"/>
                            </td>
                          </tr>
                          <input type="hidden" value="1" name="type" id="paymenttype"/>
                          <input type="hidden" value="${trip.id}" name="trip_id"/>
                        </table>
                      </g:form>
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                  </div>
                </div>
              </td>
            </tr>                          
  </body>
</html>
