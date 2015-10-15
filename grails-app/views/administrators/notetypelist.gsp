<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 10px 5px; font-size: 13px;line-height:15px; }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="${max}" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div id="resultList">
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <tr align="center"> 
        <th>Код</th>
        <th>Название</th>
        <th>Текст</th>
        <th>Шаблон email</th>          
        <th>Макс. кол-во сообщений</th>                  
        <th>Интервал отправки<br/>сообщений, дней</th>  
        <th>Параметр критерия</th>                		
        <th>Действия</th>
      </tr>        
    <g:each in="${records}" status="i" var="record">
      <tr>
        <td>${record.id}</td>                    
        <td>${record.name}</td>
        <td class="user"><small>${record.notetext}</small></td>
        <td>
        <g:each in="${email_template}" var="item">
          <g:if test="${record?.email_template_id==item?.id}">
            ${item?.name}
          </g:if>
        </g:each>          
        </td>          
        <td align="right">${record.max_notes}</td>
        <td align="right">${record.dayinterval}</td>
        <td align="right">${record.param}</td>
        <td>
          <div class="actions" nowrap>
            <span class="action_button anowrap">
              <g:link class="icon edit" title="Редактировать" controller="administrators" action="notetypeedit" id="${record.id}"></g:link>
            </span>
            <span class="action_button anowrap">
              <g:link class="icon delete" title="Удалить" controller="administrators" action="notedelete" id="${record.id}"></g:link>
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
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${params}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>  
</g:if>
</div>
