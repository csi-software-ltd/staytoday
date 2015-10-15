<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 10px 5px; font-size: 12px }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="adminbilling" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div id="resultList">
    <table class="dotted scrollable" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <tr align="center">
        <th>Код</th>
        <th>№ заказа</th>
        <th>Счет владельца</th>
        <th>Владелец</th>
        <th>Тип транзакции</th>
        <th>Дата</th>
        <th>Сумма</th>
        <th>Статус</th>
        <th>Комментарий</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
        <td>${record.id}</td>
        <td>${record.payorder_id}</td>
        <td>${record.account_id}</td>
        <td><g:link controller="administrators" action="clientDetails" id="${record.client_id}" target="_blank">${record.client_id}</g:link></td>
        <td>${record.paytype_name}</td>
        <td>${String.format('%td.%<tm.%<tY %<tT',record.moddate)}</td>
        <td>${record.summa}</td>
        <td>${record.modstatus?'обработано':'необработано'}</td>
        <td><img alt="${record.comment}" src="${resource(dir:'images',file:'question.png')}" alt="${record.comment}" title="${record.comment}" hspace="10" valign="baseline" border="0"/></td>
      </tr>
    </g:each>
    </table>
  </div>
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="adminbilling" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>  
</div>
