<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 10px 5px; font-size: 12px }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left;">Найдено: ${count}</span>
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
        <th>Название</th>	
        <th>Контакт</th>
        <th>Текст</th>
        <th>Тип</th> 
        <th>Статус</th>
        <th>Дата</th>		  
      </tr>        
    <g:each in="${records}" status="i" var="record">
      <tr>
        <td>${record.name}</td>                
        <td>${record.contact}</td>
        <td>${Mailing_task.get(record.task_id?:0)?.mtext?:''}</td>
        <td>${((Mailing_template.get(record?.template_id?:0)?.type_id?:0)==1)?'email':'sms'}</td>          
        <td>${((record.modstatus?:0)==1)?'отработано':'не отработано'}</td>
        <td><g:formatDate format="dd.MM.yyyy" date="${record.moddate}"/></td>		  
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
