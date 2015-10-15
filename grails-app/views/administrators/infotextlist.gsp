<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left;">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${params}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div id="resultList">  
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <thead>
        <tr>
        <g:if test="${inrequest.id!=1}">
          <th>Код</th>
          <th>Меню</th> 
          <th>№ в меню</th>
          <th>Название в меню</th>	
          <th>Title</th> 
          <th>Контроллер</th>		
          <th>Экшен</th>
          <th>Дата модификац.</th>                  
        </g:if>
        <g:else>
          <th>Код</th>
          <th>Экшен</th>
          <th>Название</th>
          <th>Тема письма</th>
        </g:else>
        </tr>        
      </thead>
      <tbody>
      <g:each in="${records}" status="i" var="record">
        <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
          <td>${record.id}</td>
        <g:if test="${inrequest.id!=1}">
          <td nowrap>
          <g:each in="${itemplate}" var="item">  
            ${(record.itemplate_id==item.id)?item.name:''}
          </g:each>  
          </td>
          <td>${record?.npage}</td>
          <td>${record.header}</td>
          <td><g:link action="infotextedit" id="${record.id}">${record.title}</g:link></td>
          <td>${record.controller}</td>
          <td>${record.action}</td>
          <td>${String.format('%td.%<tm.%<tY',record.moddate)}</td>
        </g:if>
        <g:else>
          <td><g:link action="infotextedit" id="${record.id}" params="[type:'1']">${record.action}</g:link></td>
          <td>${record.name}</td>          
          <td>${record.title}</td>
        </g:else>
        </tr>
      </g:each>
      </tbody>
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
