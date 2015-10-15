  <label for="payaccount_id">Тип аккаунта:</label>
  <g:select name="payaccount_id" from="${paycountry}" noSelection="${[0:'не задано']}" optionKey="payaccount_id" optionValue="${{Payaccount.get(it.payaccount_id).name}}" style="width:175px" onchange="setPayaccount(this.value)"/>
