<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 10px 5px; font-size: 13px }
  .dotted td { padding: 12px 5px; line-height:15px; }
  #resultList { border: 1px solid #505050; height: auto }
  .action_button.anowrap { border: none !important; background: none !important; float: none !important; box-shadow: none !important; }  
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${messages?.count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="${max}" total="${messages?.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${messages?.records}">  
  <div id="resultList">  
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <tr align="center"> 
        <th width="100" nowrap>Дата регист.</th>
        <th width="100">Тип сообщения</th>          
        <th>Контактная информация, сообщение</th>
        <th width="30">Статус</th>
        <th width="90">Действия</th>
      </tr>        
    <g:each in="${messages?.records}" var="record">          
      <tr>
        <td>${record.regdate?String.format('%td.%<tm.%<tY %<tH:%<tM',record.regdate):""}</td>
        <td nowrap>${record.gbtypename}</td>        
        <td>
          <div class="user">
            <small>
              <g:if test="${record.fio}">ФИО: <b>${record.fio}</b><br/></g:if>            
              <g:if test="${(record.gbtype_id==2)&&(record.adr)}">Url: <b><a href="javascript:void(0)" onclick="window.open('${record.adr}')">${record.adr}</a></b><br/></g:if>
              <g:if test="${record.tel}">Телефон: <b>${(record.tel_code?"("+record.tel_code+")":"")+record.tel}</b><br/></g:if>
              <g:if test="${record.home}">Жилье: <b>${record.home}</b></g:if>
              <g:if test="${record.username}">Пользователь: <b><a href="mailto:${record.email}">${record.username}</a></b><br/></g:if>
              <g:if test="${(record.gbtype_id==2)&&(record.recinfo)}">Выделенный текст: <i>${record.recinfo}</i></g:if>
            </small>
            <hr class="dot" style="margin:5px 0"/>
            <small>
              <b>${record.rectitle}</b><br/>
              ${record.rectext}
            </small>
          </div>          
        </td>
        <td align="center">
          <span class="action_button anowrap" style="border:0px;background:none;float:none">
            <g:if test="${record.status==1}"><span class="icon active" alt="прочитанное" title="прочитанное">&nbsp;</span></g:if>			          
            <g:else><span class="icon inactive" alt="непрочитанное" title="непрочитанное">&nbsp;</span></g:else>                                    
          </span>            
        </td>
        <td>
          <div class="actions">
            <span class="action_button" style="margin-bottom:3px">                
              <a class="icon ${(record.status==0)?'active':'inactive'}" href="javascript:void(0)" onclick="changeStatus(${record.id},${record.status==1?0:1})">
                ${record.status?'пометить непрочитанным':'прочитать'}
              </a>
            </span>
            <span class="action_button">              
              <a href="javascript:void(0)" class="icon delete" onclick="deleteMessage(${record.id})">удалить</a>
            </span>
          </div>
        </td>
      </tr>
      <input type="hidden" id="status" name="status" value="${record.status}"/>
      <div style="display:none">
        <input type="submit" id="button_${record.id}" value="Отметить">
      </div>
      <g:form name="${'gbDeleteForm'+record.id}" url="${[controller:'administrators', action:'gbmessagedelete', id:record.id]}">
        <g:collect in="${inrequest}" expr="it">
          <input type="hidden" name="${it.key}" value="${it.value}">
        </g:collect>	   
      </g:form>
    </g:each>            
    </table>
  </div>
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${messages?.count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="${max}" total="${messages?.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
