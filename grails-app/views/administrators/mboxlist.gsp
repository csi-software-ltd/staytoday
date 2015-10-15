<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th, .dotted td a[href] { font-size: 12px }
  .dotted td { padding: 12px 3px; font-weight: inherit; line-height:12px }
  #resultList { border: 1px solid #505050; height: auto; }
  .action_button.nobutton { margin-right:0;width:22px;border:none!important;background:none!important;float:none!important; box-shadow:none!important;}
  .action_button .icon.noread { background: url('../images/action-button-icons.png') no-repeat 0 -177px; } 
</style>
<div id="ajax_wrap">
<g:if test="${inrequest?.is_stat}">
  <table class="dotted" width="100%" cellpadding="2" cellspacing="0" border="0" rules="all" frame="none">
    <tr>
      <td>количество уникальных клиентов : ${userfrom_id?:0}</td>
    </tr>
    <tr>
      <td>количество уникальных владельцев : ${userto_id?:0}</td>
    </tr>
    <tr>
      <th>количество цепочек:</th>
    </tr>
  <g:each in="${id_in_mstat}" status="i" var="record">
    <g:if test="${record?.count}">
    <tr>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;${record?.count} в статусе ${record?.name}</td>
    </tr>
    </g:if>
  </g:each>   
  </table>
</g:if><g:else>
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
    <table class="dotted" width="100%" cellpadding="2" cellspacing="0" border="0" rules="all" frame="none">
      <tr align="center">
        <th rowspan="2" width="25">Код</th>
        <th rowspan="2">Объекты и участники аренды</th>        
        <th rowspan="2">Цена<br/>Даты</th>
        <th rowspan="2">Сообщение</th>
        <th colspan="2" width="100">Дата</th>
        <th colspan="4">Статусы</th>        
        <th rowspan="2" width="60">Тип ответа</th>
        <th rowspan="2" width="25">За яв ка</th>
        <th rowspan="2" width="25">Дей ст вия</th>
      </tr>
      <tr align="center">
        <th width="50">созд.</th>
        <th width="50">посл. сообщ.</th>
        <th width="17"><abbr title="Статус прочтения">пр</abbr></th>
        <th width="17"><abbr title="Статус ответа">от</abbr></th>
        <th width="17"><abbr title="Статус модификации">мд</abbr></th>
        <th><abbr title="Статус бронирования">бр</abbr></th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr+${i}" style="background:${record.is_approved&&record.is_adminread?'none':'gold'}">
        <td align="center">${record.id}</td>
        <td>
          <ul class="verifications-list" style="width:300px">
            <li class="verifications-list-item" style="padding:3px;border:none">
              <span class="count">Клиент:</span>
              <span class="label"><b>${record.client_id}</b> <g:link mapping="pView" params="${[uid:'id'+record?.client_id]}">${record?.client_nickname}</g:link></span>              
            </li>
            <li class="verifications-list-item" style="padding:3px;border:none">
              <span class="count">Владелец:</span>
              <span class="label"><b>${record.owner_id}</b> <g:link  mapping="pView" params="${[uid:'id'+record?.owner_id]}">${record?.owner_nickname}</g:link></span>
            </li>
            <li class="verifications-list-item" style="padding:3px;border:none">
              <span class="count">Жилье:</span>
              <span class="label"><b>${record.home_id}</b> <g:link mapping="hView" params="${[linkname:record?.linkname,city:record.home_city,country:Country.get(record.home_country_id)?.urlname]}" target="_blank">${record.home_name}</g:link> 
                <span class="action_button nobutton" style="display:inline-block;margin-bottom:-6px"><g:link class="icon edit" title="Редактировать объявление" controller="administrators" action="moderateHome" id="${record.home_id}"></g:link></span>
              </span>
            </li>
          </ul>
        </td>
        <td>
          <b>${record.price_rub}</b><br/><br/>
          ${String.format('%td/%<tm/%<ty',record.date_start)}
          ${String.format('%td/%<tm/%<ty',record.date_end)}
        </td>
        <td><g:shortString text="${record.mtext}" length="60"/></td>
        <td>${shortDate(date:record.inputdate)}</td>
        <td>${shortDate(date:record.lastusermessagedate)}</td>
        <td align="center"><span class="action_button nobutton">
          <span class="icon ${record.is_read?'active':'noread'}" title="${record.is_read?'прочитано':'не прочитано'}">&nbsp;</span>
        </span></td>
        <td align="center"><span class="action_button nobutton">
          <span class="icon ${record.is_answer?'active':'noread'}" title="${record.is_answer?'ответ получен':'ответ не получен'}">&nbsp;</span>
        </span></td>
        <td align="center"><span class="action_button nobutton">
          <span class="icon ${record.is_approved==1?'active':(record.is_approved==-1?'inactive':'noread')}" title="${record.is_approved==1?'одобрено':(record.is_approved==-1?'не одобрено':'не просмотрено')}">&nbsp;</span>
        </span></td>
        <td align="center"><span class="action_button nobutton">
          <span class="icon ${record.modstatus in [3,4]?'active':(record.modstatus in [1,2,6]?'noread':'inactive')}" title="${Mboxmodstatus.findByModstatus(record.modstatus)?.name?:'ошибка'}">&nbsp;</span>
        </span></td>
        <td><font color="${Answertype.get(record.answertype_id)?.color}" style="font-size:11px">${Answertype.get(record.answertype_id)?.shortdescription?:"запрос"}</font></td>
        <td align="center">${record.zayvka_id}</td>
        <td>
          <span class="action_button anowrap">
            <g:link class="icon edit" title="Посмотреть детали" controller="administrators" action="mboxDetails" id="${record.id}"></g:link>
          </span>
          <span class="action_button anowrap">
            <a href="javascript:void(0)" class="icon photo" title="Войти под именем" onclick="loginAsUser(${record.owner_id})"></a>
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
        prev="&lt;&lt;" next="&gt;&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</g:else>
</div>
