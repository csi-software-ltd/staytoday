<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 10px 5px; font-size: 12px }
  #resultList { border: 1px solid #505050; height: auto}
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
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" border="0" rules="all" frame="none">      
      <tr align="center">
        <th>Код</th>
        <th>Автор, email, телефон</th>          
        <th>Город (регион)</th>
        <th>Период аренды</th>
        <th>Стоимость</th>
        <th>Дата заявки</th>
        <th>Статус</th>
        <th>Кол-во предл.</th>
        <th>Действия</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
        <td>${record.id}</td>
        <td class="user">
          <b><font color="#1D95CB">${record.user_nickname}</font></b><br/>
          <span>                        
            <g:if test="${record?.is_auto}">
              <g:link action="mbox" params="${[owner_id:User.findWhere(client_id:record?.baseclient_id)?.id?:0, user_id:record?.user_id]}">${record.user_email}</g:link><br/>
            </g:if>
            ${record.mobtel}
          </span>
        </td>
        <td class="user">
          <span>${record.city}<br/>(${Region.get(record.region_id)?.name?:''})</span>
        </td>
        <td>${String.format('%td.%<tm.%<tY',record.date_start)} - ${String.format('%td.%<tm.%<tY',record.date_end)}</td>
        <td>${record.pricefrom_rub} - ${record.priceto_rub}</td>
        <td>${String.format('%td.%<tm.%<tY %<tT',record.inputdate)}</td>
        <td>
          <g:if test="${record?.modstatus==0}">новая</g:if>
          <g:elseif test="${record?.modstatus==1}">публикация</g:elseif>
          <g:elseif test="${record?.modstatus==-1}">не подтвержден email</g:elseif>
          <g:elseif test="${record?.modstatus==-2}">спам/некорректная</g:elseif>
          <g:elseif test="${record?.modstatus==-3}">просроченная</g:elseif>
          <g:elseif test="${record?.modstatus==-4}">закрытая</g:elseif>
        </td>
        <td>${record.kolprop}</td>
        <td width="75" nowrap>
          <div class="actions" nowrap>
            <span class="action_button anowrap">
              <g:link class="icon edit" title="Редактировать" controller="administrators" action="editproposal" id="${record.id}"></g:link>
            </span>
          <g:if test="${record.modstatus==0||record.modstatus==-3}">
            <span class="action_button anowrap">
              <a href="javascript:void(0)" class="icon delete" title="${message(code:'button.closemail',default:'Закрыть с письмом')}" onclick="closeZayavka(${record.id})"></a>
            </span>
          </g:if><g:if test="${record.modstatus>-4}">
            <span class="action_button anowrap">
              <g:link class="icon ${message(code:record.modstatus==-2?'active':'inactive')}" title="${message(code:record.modstatus==-2?'button.recover':'button.ban')}" controller="administrators" action="proposaldelete" id="${record.id}" params="[act:'proposal']"></g:link>
            </span>
          </g:if>
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
