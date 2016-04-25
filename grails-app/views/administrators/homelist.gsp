<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; font-size: 12px }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${count}" offset="${inrequest?.offset?:0}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">  
  <div id="resultList">  
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <tr align="center">
        <th width="30">На глав- ной</th>          
        <th width="30">Пре длож дня</th>
        <th width="30">Код</th> 
        <th width="100">Фото</th>	    
        <th>Название</th>
        <th width="50">Владелец</th>
        <th width="50">Код клиента</th>
        <th>Email клиента</th>          
        <th width="50">Базовая цена</th>                  
        <th>Статус публик.</th>  
        <th>Статус модер.</th>
        <th width="62">Дата создания / модиф.</th>
        <th width="122">Действия</th>        		
      </tr>        
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
        <td align="center">
          <input type="checkbox" name="mainpageCheckbox" <g:if test="${record.is_mainpage}"> checked onClick="toMainpage(${record.id}, 0);" </g:if><g:else> onClick="toMainpage(${record.id}, 1);"</g:else> /> 
        </td>
        <td align="center">
          <input type="checkbox" name="specofferCheckbox" <g:if test="${record.is_specoffer}"> checked onClick="toSpecoffer(${record.id}, 0);" </g:if><g:else> onClick="toSpecoffer(${record.id}, 1);"</g:else> /> 
        </td>
        <td>${record.id}</td>
        <td>
          <g:if test="${record.mainpicture}"><img width="100" src="${imageurl+record.client_id+'/t_'+record.mainpicture}" border="0" align="absmiddle"></g:if>
          <g:else><img width="100" src="${resource(dir:'images',file:'default-picture.png')}" border="0"></g:else>
          <br/>
          <span>Всего: ${record.picture_cnt}</span>
        </td>          
        <td><g:link action="moderateHome" id="${record.id}">${record.name}</g:link></td>
        <td>${record.user_id}</td>
        <td>${record.client_id}</td>
        <td>${record.client_name}</td>          
        <td style="${record.unrealiable?'color:red':''}">${record.pricestatus==1?record.pricestandard:record.pricestatus==2?'неактивна':'отключена'}</td>
        <td>
          <g:each in="${modstatus}" var="it">
            <g:if test="${it.modstatus==record.modstatus}">${it.name}</g:if>
          </g:each>
        </td>
        <td align="center">
          <img width="24" height="24" src="${(record.is_confirmed==1)?resource(dir:'images',file:'checkmark.png'):resource(dir:'images',file:'checkmark-inactive.png')}" 
          alt="${(record.is_confirmed==1)?'Рассмотрен':'Не рассмотрен'}" title="${(record.is_confirmed==1)?'Рассмотрен':'Не рассмотрен'}"/>            
        </td>
        <td>${String.format('%td.%<tm.%<tY',record.inputdate)} ${String.format('%td.%<tm.%<tY',record.moddate)}</td>
        <td width="60">
          <div class="actions" nowrap>
            <span class="action_button anowrap">
              <g:link class="icon edit" title="Редактировать" controller="administrators" action="moderateHome" id="${record.id}"></g:link>
            </span>
            <span class="action_button anowrap">
              <g:link class="icon view" title="Посмотреть" mapping="hView" params="${[linkname:record?.linkname,city:record.city,country:Country.get(record.country_id)?.urlname]}" target="_blank"></g:link>
            </span>
            <span class="action_button" style="margin-top:3px">
              <span class="icon none" onClick="loginAsUser(${record.id})">войти под именем</span>
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
      <g:paginate controller="administrators" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${count}" offset="${inrequest?.offset?:0}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>  
</div>
