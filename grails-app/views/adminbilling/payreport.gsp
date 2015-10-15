<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <style type="text/css">
      #reportdate_from_value, #reportdate_to_value, #reportdate_start_value, #reportdate_end_value,
      #reportdealdate_start_value, #reportdealdate_end_value { width: 90px }
      input[type="submit"] { min-width: 4em }
    </style>
  </head>
  <body>
    <div id="payorder" align="center">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy rounded_bottom" style="padding:20px 5px">
        <tr>
          <g:form name="orderMoneyForm" url="[action:'payreportordermoney']" target="_blank">
          <td>Движение денег по заказам</td>
          <td valign="middle" nowrap>
            <label for="reportdate_from">Дата заказа с:</label>
            <calendar:datePicker name="reportdate_from" needDisable="false" dateFormat="%d-%m-%Y"  value="${new Date()}"/>
            <label for="reportdate_to">&nbsp;по:</label>
            <calendar:datePicker name="reportdate_to" needDisable="false" dateFormat="%d-%m-%Y"  value="${new Date()}"/>
          </td>
          <td align="right" nowrap>
            <input type="submit" class="button-glossy red" id="orderMoneyForm_submit_button" value="PDF">
          </td>
          </g:form>
        </tr>
        <tr>
          <g:form name="agentRewardForm" controller="adminbilling" target="_blank">
          <td>Отчет Акт на агентское вознаграждение</td>
          <td valign="middle" nowrap>
            <label for="client_id">Код клиента:&nbsp;&nbsp;</label>
            <input type="text" id="client_id" name="client_id" value="" style="width:60px">            
            <label for="agentreward_date">&nbsp;за месяц:</label>
            <g:datePicker name="agentreward_date" precision="month" value="${new Date()}" relativeYears="[113-new Date().getYear()..0]"/>            
          </td>
          <td align="right" nowrap>
            <g:actionSubmit value="XLS" class="button-glossy green" action="payreportagentrewardXLS" style="margin-right:5px"/>
            <g:actionSubmit value="PDF" class="button-glossy red" action="payreportagentreward"/>
          </td>
          </g:form>
        </tr>
        <tr>
          <g:form name="agentForm" controller="adminbilling" target="_blank">
          <td>Отчет Агента</td>
          <td valign="middle" nowrap>
            <label for="client_id">Код клиента:&nbsp;&nbsp;</label>
            <input type="text" id="client_id" name="client_id" value="" style="width:60px">            
            <label for="agent_date">&nbsp;за месяц:</label>
            <g:datePicker name="agent_date" precision="month" value="${new Date()}" relativeYears="[113-new Date().getYear()..0]"/>            
          </td>
          <td align="right" nowrap>
            <g:actionSubmit value="XLS" class="button-glossy green" action="payreportagentXLS" style="margin-right:5px"/>
            <g:actionSubmit value="PDF" class="button-glossy red" action="payreportagent"/>
          </td>
          </g:form>
        </tr>
        <tr>
          <g:form name="transactionForm" controller="adminbilling" target="_blank">
          <td>Отчет по транзакциям</td>
          <td valign="middle" nowrap>
            <label for="reportdate_start">Дата отчета с:</label>
            <calendar:datePicker name="reportdate_start" needDisable="false" dateFormat="%d-%m-%Y" dayadd="0" value="${new Date()}"/>
            <label for="reportdate_end">&nbsp;по:</label>
            <calendar:datePicker name="reportdate_end" needDisable="false" dateFormat="%d-%m-%Y"  value="${new Date()}"/>
          </td>
          <td align="right" nowrap>
            <g:actionSubmit value="XLS" class="button-glossy green" action="payreporttransactionXLS" style="margin-right:5px"/>
            <g:actionSubmit value="PDF" class="button-glossy red" action="payreporttransaction"/>
          </td>
          </g:form>
        </tr>
        <tr>
          <g:form name="dealForm" controller="adminbilling" target="_blank">
          <td>Отчет по сделкам</td>
          <td valign="middle" nowrap>
            <label for="reportdealdate_start">Дата отчета с:</label>
            <calendar:datePicker name="reportdealdate_start" needDisable="false" dateFormat="%d-%m-%Y" dayadd="0" value="${new Date()}"/>
            <label for="reportdealdate_end">&nbsp;по:</label>
            <calendar:datePicker name="reportdealdate_end" needDisable="false" dateFormat="%d-%m-%Y"  value="${new Date()}"/>
          </td>
          <td align="right" nowrap>
            <g:actionSubmit value="XLS" class="button-glossy green" action="payreportdealXLS" style="margin-right:5px"/>
            <g:actionSubmit value="PDF" class="button-glossy red" action="payreportdeal"/>
          </td>
          </g:form>
        </tr>
      </table>      
    </div>
  </body>
</html>
