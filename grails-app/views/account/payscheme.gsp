<b><label for="reserve_id" class="nopadd">${message(code:'payout.scheme')}</label></b>
<select name="reserve_id" id="reserve_id" style="width:200px">
                              <g:each in="${reserve}" var="item">
                                <option value="${item.id}" onclick="enableSubmitButton();displaypayaccount(this.value);$('paydesc').update('${item['description'+context?.lang]}')" <g:if test="${item.id==client?.reserve_id}">selected="selected"</g:if>>
                                  ${item['name'+context?.lang]}
                                </option>
                              </g:each>
                              </select>
<script type="text/javascript">
  displaypayaccount('3');
</script>
