<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; font-size: 12px }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">  
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${params}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">  
  <div id="resultList">  
    <table class="dotted scrollable" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <thead>
        <tr>
          <th>Название</th>
          <th>Город</th> 
          <th>Тип</th>                    
          <th>Статус</th>   
          <th>Количество</th>
        	<th>Действия</th>
        </tr>        
      </thead>
      <tbody>
      <g:each in="${records}" status="i" var="record">
        <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}"> 
          <td>${record.linkname}</td>
          <td>${City.get(record.city_id)?.name?:''}</td>
          <td>${Shometype.get(record.type_id)?.name?:''}</td>          
          <td>${record.modstatus?'активный':'неактивный'}</td>
          <td>${record.homecount}</td>
          <td><span class="action_button anowrap">
              <g:link class="icon edit" title="Редактировать" controller="administrators" action="shomeedit" id="${record.id}"></g:link>             
            </span>
            <span class="action_button anowrap">
              <g:remoteLink class="icon ${record.modstatus?'inactive':'active'}" title="${record.modstatus?'Деактивировать':'Активировать'}" url="${[controller:'administrators',action:'shomestatus',id:record?.id,params:[status:record?.modstatus?0:1]]}" onSuccess="initialize()"></g:remoteLink>                            
            </span>             
           </td>                          
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
