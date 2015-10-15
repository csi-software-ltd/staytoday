<g:if test="${paycountry}">
                                <tr>
                                  <th width="180">${message(code:'payout.method')}</th>
                                  <th width="125">${message(code:'payout.arrive')}</th>
                                  <th width="100">${message(code:'payout.fee')}</th>
                                  <th>${message(code:'payout.notes')}</th>
                                </tr>
                              <g:each in="${paycountry}" var="${item}" status="i">
                                <tr>
                                  <td>
                                    <label for="payaccount_${item?.id}" style="padding-left:0">
                                      <g:radio id="payaccount_${item?.payaccount_id}" onchange="displayDetails(this.value);enableSubmitButton();" name="payaccount_id" value="${item.payaccount_id}" checked="${!i?true:false}" />
                                      ${Payaccount.get(item.payaccount_id)['name'+context?.lang]}
                                    </label>
                                  </td>
                                  <td>${item['term'+context?.lang]?:''}</td>
                                  <td>${item.percent?:''}</td>
                                  <td>${item['description'+context?.lang]?:''}</td>
                                </tr>
                              </g:each>
<script type="text/javascript">
  displayDetails(${paycountry[0].payaccount_id});
</script>
</g:if>
