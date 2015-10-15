<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 10px 5px; font-size: 12px }
  #resultList { border: 1px solid #505050; height: auto }
  .action_button.anowrap { border: none !important; background: none !important; float: none !important; box-shadow: none !important; }
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
        <th>Код</th>         
        <th>Название</th>	
        <th>Тип</th> 
        <th>Статус</th>		
      </tr>        
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}">
        <td>${record.id}</td>                
        <td><g:link action="mailing_templateedit" id="${record.id}" params="${[type:record?.type_id?:0]}">${record.name}</g:link></td>
        <td>${((record.type_id?:0)==1)?'email':'sms'}</td>          
        <td>
          <span class="action_button anowrap" style="border:0px;background:none;float:none">
            <g:if test="${record.modstatus==1}"><span class="icon active" alt="активен" title="активен">&nbsp;</span></g:if>
            <g:else><span class="icon inactive" alt="неактивен" title="неактивен">&nbsp;</span></g:else>
          </span>
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
