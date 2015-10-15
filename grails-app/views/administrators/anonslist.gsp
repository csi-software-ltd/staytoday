<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${data?.count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${inrequest}"
        total="${data?.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${data?.records}">
  <div id="resultList">  
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <thead>
        <tr>
          <th>Код</th>
          <th>Название</th>
          <th>Статус</th>         
          <th>Показы</th>     
          <th>Клики</th>                 		          
        </tr>
      </thead>
      <tbody>
      <g:each in="${data?.records}" var="item">
        <tr style="color:${item.modstatus?'black':'#AAAAAA'}">
          <td>${item?.id}</td>
          <td><g:link action="anonsDetail" id="${item.id}">${item?.title?:''}</g:link></td>         
          <td>${item?.modstatus?'активное':'неактивное'}</td>
          <td>${item?.ncount?:0}</td>
          <td>${item?.nclick?:0}</td>                
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${data?.count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${inrequest}"
        total="${data?.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>  
</g:if>
</div>
