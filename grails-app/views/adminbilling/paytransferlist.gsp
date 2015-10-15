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
        <th>Код</th>
        <th>Дата оплаты</th>
        <th>Номер платежки</th>
        <th>Сумма</th>
        <th>Назначение</th>
        <th>Код заказа</th>
        <th>Тип операции</th>
        <th>Статус обраб.</th>
        <th>Дата модиф.</th>
        <th>Действия</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}">
        <td>${record.id}</td>
        <td>${record.paydate}</td>
        <td>${record.paynomer}</td>
        <td>${record.summa}</td>
        <td>${record.paycomment}</td>
        <td>${record.payorder_id?:record.paylist?:''}</td>
        <td>${record.paytype==1?'приход':record.paytype==2?'возврат':record.paytype==3?'вывод':'неопределено'}</td>
        <td>${record.modstatus==1?'обработанно':record.modstatus==2?'нераспознанно':'необработанно'}</td>
        <td>${String.format('%td.%<tm.%<tY',record.moddate)}</td>
        <td width="60">
          <div class="actions" nowrap>
            <span class="action_button anowrap">
              <g:link class="icon edit" title="Детали" controller="adminbilling" action="paytransferdetail" id="${record.id}"></g:link>
            </span>
          </div>
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
