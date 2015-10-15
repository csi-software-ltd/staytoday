<div id="ajax_wrap">
<g:if test="${payactlistcount>10}">
  <div class="results_header">
    <span class="pagination col">
      <g:paginate controller="account" prev="&lt;" next="&gt;"
        action="${actionName}" max="10" params="${inrequest}"	total="${payactlistcount}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if><g:if test="${payactlist}">
  <table class="dotted" cellpadding="0" cellspacing="0" rules="all" frame="none">
    <tr>            
      <th>${message(code:'history.name')}</th>
      <th nowrap>${message(code:'history.actnumber')}</th>
      <th width="80">${message(code:'history.summa_full')}</th>
      <th width="80">${message(code:'history.summa_service')}</th>
      <th nowrap>${message(code:'history.actdate')}</th>
      <th width="92">${message(code:'ads.price.action').toLowerCase()}</th>
    </tr>
  <g:each in="${payactlist}" var="record" status="i">
    <tr>
      <td>${record?.description}</td>
      <td align="center">${record?.id}</td>
      <td class="currency">${Math.round(record.summafull/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
      <td class="currency">${Math.round(record.summa/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
      <td align="center" nowrap><g:formatDate format="dd.MM.yyyy" date="${record.actdate}"/></td>
      <td>
        <span class="action_button orange nowrap">
          <g:link class="icon print" title="${message(code:'history.act.print')}" controller="account" action="index">${message(code:'button.print')}</g:link>
        </span>
      </td>
    </tr>
  </g:each>
  </table> 
</g:if><g:else>
  <div class="padd20">
    <p>${message(code:'istory.acts.nofound')}</p>
  </div>
</g:else>
</div>
