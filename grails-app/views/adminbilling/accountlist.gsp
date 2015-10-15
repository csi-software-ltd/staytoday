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
        <th width="60">Id</th>
        <th>Код</th>
        <th width="60">Владелец</th>
        <th width="100">Сумма</th>
        <th width="100">Сумма перевед.</th>
        <th width="100">Сумма услуг</th>
        <th width="100">Сумма комиссии</th>
        <th width="100">Сумма внешняя</th>
        <th width="70">Статус</th>
        <th width="70">Дата модиф.</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}">
        <td align="center">${record.id}</td>
        <td>${record.code}</td>
        <td align="center">${record.client_id}</td>
        <td align="right">${record.summa}</td>
        <td align="right">${record.summa_fin}</td>
        <td align="right">${record.summa_serv}</td>
        <td align="right">${record.summa_com}</td>
        <td align="right">${record.summa_ext}</td>
        <td align="center">${record.modstatus==0?'неактивен':'активен'}</td>
        <td align="center">${String.format('%td.%<tm.%<tY',record.moddate)}</td>
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
