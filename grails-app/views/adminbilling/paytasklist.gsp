<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; font-size: 12px }
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
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <tr align="center">
        <th nowrap>№ задания</th>
        <th nowrap>№ транзакции</th>
        <th nowrap>Тип задания</th>
        <th nowrap>Со счета</th>
        <th nowrap>На счет</th>
        <th>Комментарий</th>
        <th>Дата</th>
        <th>Сумма</th>
        <th>Статус</th>
        <th>Действия</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}">
        <td>${record.id}</td>
        <td>${record.paytrans_id}</td>
        <td>${record.paytasktype_name}</td>
        <td>${record.from_code}</td>
        <td>${record.to_code}</td>
        <td>${record.comment}</td>
        <td>${String.format('%td.%<tm.%<tY %<tT',record.moddate)}</td>
        <td>${record.summa}</td>
        <td>${record.modstatus?'обработано':'необработано'}</td>
        <td>
        <g:if test="${!record.modstatus}">
          <span class="action_button anowrap">
            <g:remoteLink class="icon delete" title="Отработать" controller="adminbilling" action="paytaskcomplete" id="${record.id}" onSuccess="\$('form_submit_button').click();"></g:remoteLink>
          </span>
        </g:if>
        </td>
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
