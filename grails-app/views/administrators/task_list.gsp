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
          <th>Код</th>		
          <th>Название шаблона</th>
          <th>Дата окончания</th>
          <th>Статус</th>		  
          <th>Кол-во писем</th>		                      		  
        </tr>        
      </thead>
      <tbody>
      <g:each in="${records}" status="i" var="record">
        <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
          <td>${record.id}</td> 
          <td>${Mailing_template.get(record.template_id?:0)?.name?:''}</td>                
          <td><g:formatDate format="dd.MM.yyyy" date="${record.date_end}"/></td>		  
          <td>${((record.modstatus?:0)==1)?'отработано':(((record.modstatus?:0)==2)?'в обработке':'не отработано')}</td>
          <td>${record.ncount}</td>		  
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
