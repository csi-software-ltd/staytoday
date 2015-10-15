<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; font-size: 12px }
  .dotted td { padding: 12px 5px; }
  #resultList { border: 1px solid #505050; height: auto; }
  .action_button.anowrap { border: none !important; background: none !important; float: none !important; box-shadow: none !important; }
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
        <th rowspan="2" width="50">Фото</th>
        <th rowspan="2">Код</th>
        <th rowspan="2">Ник [пользователь]</th> 
        <th rowspan="2" width="30">Код пост.</th>		
        <th rowspan="2" width="64">Провайдер</th>
        <th colspan="3" width="105">Статус подтв.</th>        
        <th rowspan="2" width="113">Телефон,<br>skype</th>          
        <th rowspan="2" width="44">Отзывы</th>
        <th colspan="2" width="100">Дата</th>          
        <th rowspan="2" width="155">Действия</th>        		
      </tr>
      <tr>
        <th width="35">email</th>
        <th width="35">тел.</th>
        <th width="35">агент</th>
        <th width="50">регист.</th>
        <th width="50">посл. посещ.</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr+${i}" style="line-height:17px">
        <td>
          <img src="${(record?.picture && !record?.is_external)?imageurl:''}${(record?.picture)?record?.picture:resource(dir:'images',file:'user-default-picture.png')}" align="absmiddle" width="50" style="border:1px solid silver">
        </td>
        <td align="right">${record.id}</td>
        <td>${record.nickname}<br/><font color="#1D95CB">${record.name}</font></td>
        <td align="right"><g:if test="${record.client_id}"><g:link controller="administrators" action="homes" params="[user_id:record.id]">${record.client_id}</g:link></g:if><g:else>нет</g:else></td>
        <td align="center">${record.provider}</td>
        <td align="center">
          <span class="action_button anowrap">
            <g:if test="${record.modstatus==-1}"><span class="icon inactive" alt="забанен" title="забанен">&nbsp;</span></g:if>
            <g:elseif test="${record.modstatus in [0,2]}"><span class="icon inactive" alt="неподтвержден" title="неподтвержден">&nbsp;</span></g:elseif>			                        
            <g:elseif test="${record.modstatus==1}"><span class="icon active" alt="активен" title="активен">&nbsp;</span></g:elseif>                                    
          </span>            
        </td>
        <td align="center">
          <span class="action_button anowrap">
            <g:if test="${record.is_telcheck==0}"><span class="icon inactive" alt="не подтвержден" title="не подтвержден">&nbsp;</span></g:if>
            <g:else><span class="icon active" alt="подтвержден" title="подтвержден">&nbsp;</span></g:else>
          </span>
        </td>
        <td align="center">
          <span class="action_button anowrap">
            <g:if test="${record.is_agent==0}"><span class="" alt="нет" title="нет">нет</span></g:if>
            <g:elseif test="${record.is_agentcheck==0}"><span class="icon inactive" alt="не подтвержден" title="не подтвержден">&nbsp;</span></g:elseif>
            <g:else><span class="icon active" alt="подтвержден" title="подтвержден">&nbsp;</span></g:else>
          </span>
        </td>
        <td align="right">
          ${record.tel?:''}<br />
          <font color="#1D95CB">${record.skype?:''}</font>
        </td>
        <td align="center">${record.ncomment}</td>
        <td>${String.format('%td.%<tm.%<ty',record.inputdate)}</td>
        <td>${String.format('%td.%<tm.%<ty',record.lastdate)}</td>          
        <td>
          <span class="action_button" style="margin-bottom:3px;">
            <g:if test="${record.banned==0}"><span class="icon inactive" onClick="setMain(${record.id},1);">забанить аккаунт</span></g:if>
            <g:else><span class="icon active" onclick="setMain(${record.id},0);">активировать аккаунт</span></g:else>
          </span>            
          <span class="action_button" style="margin-bottom:3px;">
            <g:if test="${record.is_telcheck==1}"><span class="icon inactive" onClick="setTel(${record.id},0);">отменить телефон</span></g:if>
            <g:else><span class="icon active" onclick="setTel(${record.id},1);">подтвердить телефон</span></g:else>
          </span>            
        <g:if test="${record.is_agent!=0}">
          <span class="action_button" style="margin-bottom:3px;">
            <g:if test="${record.is_agentcheck==1}"><span class="icon inactive" onClick="setAgent(${record.id},0);">отменить агента</span></g:if>
            <g:else><span class="icon active" onclick="setAgent(${record.id},1);">подтвердить агента</span></g:else>
          </span>
        </g:if>
          <span class="action_button" style="margin-bottom:3px;">
            <span class="icon none" onclick="loginAsUser(${record.id})">войти под именем</span>
          </span>
        <g:if test="${record.is_external==0}">
          <span class="action_button" style="margin-bottom:3px;">
            <span class="icon none" onclick="changePass(${record.id})">сменить пароль</span>
          </span>
        </g:if>
        <g:if test="${record.tel?:''}">
          <span class="action_button">
            <span class="icon none" onclick="smsSend(${record.id})">Отправить смс</span>
          </span>
        </g:if>
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
