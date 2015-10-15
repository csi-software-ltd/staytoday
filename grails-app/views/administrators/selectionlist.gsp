<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; font-size: 12px }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">  
  <div style="padding:10px 20px">
    <span style="float:left"><g:link action="selectioncsv" params="${inrequest}" target="_blank">CSV</g:link>&nbsp;&nbsp;&nbsp;&nbsp;Найдено: ${count}</span>
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
          <th>Ник</th> 
          <th>Имя</th>          
          <th>E-Mail</th>
          <th>Телефон</th>           	
        </tr>        
      </thead>
      <tbody>
      <g:each in="${records}" status="i" var="record">
        <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}"> 
          <td>${User.findWhere(client_id:record.client_id,is_am:1)?.nickname?:''}</td>        
          <td>${User.findWhere(client_id:record.client_id,is_am:1)?.firstname?:''}</td>
          <td>${User.findWhere(client_id:record.client_id,is_am:1)?.email?:''}</td>
          <td>${User.findWhere(client_id:record.client_id,is_am:1)?.tel?:''}</td>          
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
