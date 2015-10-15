<div id="ajax_wrap">
<g:if test="${payorderlistcount>10}">
  <div id="results_header" class="results_header">
    <span class="pagination col">
      <g:paginate controller="account" prev="&lt;" next="&gt;"
        action="${actionName}" max="10" params="${inrequest}"	total="${payorderlistcount}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if><g:if test="${payorderlist}">
  <table class="dotted" cellpadding="0" cellspacing="0" rules="all" frame="none">  
    <tr>
      <th nowrap>${message(code:'account.paydate')}</th>
      <th nowrap>${message(code:'account.payorder')}</th>
      <th>${message(code:'account.status')}</th>
      <th>${message(code:'account.object')}</th>
      <th width="65">${message(code:'account.summa_deal')}</th>
      <th width="50">${message(code:'account.summa')}</th>
      <th width="50">${message(code:'account.summa_ret')}</th>
    </tr>
  <g:each in="${payorderlist}" var="record" status="i">
    <tr>
      <td nowrap><g:formatDate format="dd.MM.yyyy HH:MM" date="${record.inputdate}"/></td>
      <td>${record?.norder}</td>
      <td><g:rawHtml>${record.modstatus==2?'<font color="blue">'+message(code:"history.status.completed")+'</font>':record.retstatus==2?'<font color="red">'+message(code:"history.status.canceled.guest")+'</font>':record.retstatus==3?'<font color="red">'+message(code:"history.status.canceled.owner")+'</font>':'<font color="green">'+message(code:"history.status.active")+'</font>'}</g:rawHtml></td>
      <td><g:link class="link" controller="home" action="view" id="${record.home_id}">${Home.get(record?.home_id).name}</g:link></td>
      <td class="currency">${Math.round(record.summa_deal/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
      <td class="currency">${Math.round(record.summa/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
      <td class="currency">${Math.round(record.summa_ret/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
    </tr>
  </g:each>
  </table> 
</g:if><g:else>
  <div class="padd20">
    <p>${message(code:'account.paytrip.nofound')}</p>
  </div>
</g:else>
</div>
