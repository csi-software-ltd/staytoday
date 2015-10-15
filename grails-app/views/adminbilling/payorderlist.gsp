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
        <th width="30">Код</th>
        <th width="70">№ заказа</th>
        <th>Плательщик</th>
        <th>Владелец</th>
        <th>Дом</th>
        <th width="130">Платежное ср-во</th>
        <th width="80">Агрегатор</th>
        <th width="70">Сумма</th>
        <th width="70">Сумма владельцу</th>
        <th width="65">Статус</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}">
        <td align="center">${record.id}</td>
        <td><g:link controller="adminbilling" action="payorderdetail" id="${record.id}">${record.norder}</g:link></td>
        <td>${record.plat_name}</td>
        <td align="center">${record.client_id}</td>
        <td align="center"><g:link controller="home" action="view" id="${record.home_id}" target="_blank">${record.home_id}</g:link></td>
        <td>${record.payway_name}</td>
        <td>${record.agr_name}</td>
        <td align="right">${record.summa}</td>
        <td align="right">${record.summa_own}</td>
        <td align="center"><g:rawHtml>${record.modstatus==0?'<font color="gray">неактивен</font>':record.modstatus==1?'<font color="green">активен</font>':record.modstatus==2?'<font color="blue">выполнен</font>':'<font color="red">удален</font>'}</g:rawHtml></td>
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
