<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 10px 5px; }  
  #resultList { border: 1px solid #505050; height: auto }
  .action_button.anowrap.none { border: none !important; background: none !important; float: none !important; box-shadow: none !important; }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div id="resultList">  
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <tr align="center">
        <th>Код</th>
        <th>Название</th> 
        <th>Shortlink</th>	
        <th>Страна</th>
        <th>Приоритет</th> 
        <th>Популярность</th>		
        <th>Статус</th>
        <th>Дата модификац.</th>
        <th>Действия</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}">
        <td>${record.id}</td>
        <td>${record.name2}</td>
        <td>${record.shortname}</td>
        <td>
        <g:each in="${countries}" var="country">
          <g:if test="${record?.country_id==country?.id}">
            ${country?.name}
          </g:if>
        </g:each>
        </td>
        <td>${record.rating}</td>
        <td>${record.ncount}</td>
        <td align="center">
          <span class="action_button anowrap none">
            <g:if test="${record.modstatus==0}"><span class="icon inactive" alt="скрыт" title="снят">&nbsp;</span></g:if>
            <g:elseif test="${record.modstatus==1}"><span class="icon active" alt="активен" title="активен">&nbsp;</span></g:elseif>
          </span>
        </td>
        <td>${String.format('%td.%<tm.%<tY %<tH:%<tM',record.moddate)}</td>
        <td width="60">
          <div class="actions" nowrap>
            <span class="action_button anowrap">
              <g:link class="icon edit" title="Редактировать" controller="administrators" action="popdiredit" id="${record.id}"></g:link>
            </span>
            <span class="action_button anowrap">
              <g:link class="icon delete" title="Удалить" controller="administrators" action="popdirdelete" id="${record.id}"></g:link>
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
      <g:paginate controller="administrators" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>  
</div>
