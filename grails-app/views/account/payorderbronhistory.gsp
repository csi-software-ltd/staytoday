<div id="ajax_wrap">
<g:if test="${payorderlistcount>10}">
  <div class="results_header">
    <span class="pagination col">
      <g:paginate controller="account" prev="&lt;" next="&gt;"
        action="${actionName}" max="10" params="${inrequest}"	total="${payorderlistcount}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if><g:if test="${payorderlist}">
  <table class="dotted" cellpadding="0" cellspacing="0" rules="all" frame="none">  
    <tr>
      <th nowrap>${message(code:'history.tripdate')}</th>
      <th nowrap>${message(code:'history.triporder')}</th>
      <th>${message(code:'account.status')}</th>        
      <th>${message(code:'account.object')}</th>                
      <th width="65">${message(code:'account.summa_deal')}</th>
      <th width="50">${message(code:'history.summa')}</th>
      <th width="50">${message(code:'history.summa_own')}</th>        
      <th width="94">${message(code:'ads.price.action').toLowerCase()}</th>
    </tr>
  <g:each in="${payorderlist}" var="record" status="i">
    <tr>
      <td nowrap><g:formatDate format="dd.MM.yyyy HH:MM" date="${record.inputdate}"/></td>
      <td>${record?.norder}</td>
      <td><g:rawHtml>${record.modstatus==2?'<font color="blue">'+message(code:"history.status.completed")+'</font>':record.retstatus==2?'<font color="red">'+message(code:"history.status.canceled.guest")+'</font>':record.retstatus==3?'<font color="red">'+message(code:"history.status.canceled.owner")+'</font>':'<font color="green">'+message(code:"history.status.active")+'</font>'}</g:rawHtml></td>        
      <td><g:link class="link" controller="home" action="view" id="${record.home_id}">${Home.get(record?.home_id).name}</g:link></td>
      <td class="currency">${Math.round(record.summa_deal/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
      <td class="currency">${Math.round(record.summa/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>        
      <td class="currency">${Math.round(record.summa_own/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>        
      <td>
        <span class="action_button orange nowrap">
          <g:link class="icon view" title="${message(code:'history.details.alt')}" controller="account" action="payorderdetail" id="${record.norder}">${message(code:'history.details')}</g:link>
        </span>        
        <span class="action_button orange nowrap">
          <g:link class="icon edit" title="${message(code:'booking.view.mbox').capitalize()}" controller="inbox" action="view" id="${record.mbox_id}">${message(code:'history.mbox')}</g:link>
        </span>        
      </td>
    </tr>
  </g:each>
  </table> 
</g:if>
</div>
