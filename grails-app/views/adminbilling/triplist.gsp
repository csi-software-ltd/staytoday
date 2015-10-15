<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th, .dotted td a[href] { font-size: 12px }
  .dotted td { padding: 12px 3px; font-weight: inherit;line-height:12px }
  #resultList { border: 1px solid #505050; height: auto; }
  .action_button.nobutton { margin-right:0;width:22px;border:none!important;background:none!important;float:none!important; box-shadow:none!important;}
  .action_button .icon.noread { background: url('../images/action-button-icons.png') no-repeat 0 -177px; } 
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="adminbilling" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div id="resultList">
    <table class="dotted" width="100%" cellpadding="2" cellspacing="0" border="0" rules="all" frame="none">
      <tr align="center">
        <th width="30" rowspan="2">Код</th>
        <th width="30" rowspan="2">Код пере- писки</th>
        <th width="100" rowspan="2">Владелец</th>
        <th width="100" rowspan="2">Клиент</th>
        <th width="100" rowspan="2">Жилье</th>
        <th width="50" rowspan="2">Цена<br/>Даты</th>
        <th width="50" rowspan="2">Дата брони</th>
        <th colspan="3">Статусы</th>
        <th width="50" rowspan="2">Рейтинг</th>        
        <th width="50" rowspan="2">Дейст- вия</th>
      </tr>
      <tr>
        <th width="50">поездки</th>
        <th width="50">оплаты</th>
        <th width="50">контроля</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr_${i}" align="center" style="background:${record.is_read?'none':'gold'}">
        <td>${record.id}</td>
        <td><g:link controller="administrators" action="mboxDetails" id="${record.mbox_id}" target="_blank">${record.mbox_id}</g:link></td>
        <td align="left">
          <g:link controller="administrators" action="users" params="${[user_id:record.owner_id]}">${record.owner_id}:</g:link><br/>          
          <small style="white-space:normal"><g:link mapping="pView" params="${[uid:'id'+record?.owner_id]}">${record?.owner_name}</g:link></small>          
        </td>
        <td align="left">          
          <g:link controller="administrators" action="users" params="${[user_id:record.user_id]}">${record.user_id}:</g:link><br/> 
          <small style="white-space:normal"><g:link  mapping="pView" params="${[uid:'id'+record?.user_id]}">${record?.cl_name}</g:link></small>          
        </td>
        <td align="left">      
          <g:link controller="administrators" action="moderateHome" id="${record.home_id}">${record.home_id}:</g:link><br/>          
          <small><g:link mapping="hView" params="${[linkname:record?.home_linkname,city:record.home_city,country:Country.get(record.home_country_id)?.urlname]}" target="_blank">${record.home_name}</g:link></small>
        </td>
        <td>
          <b>${record.price_rub}</b><br/><br/>
          ${String.format('%td/%<tm/%<ty',record.fromdate)} 
          ${String.format('%td/%<tm/%<ty',record.todate)}
        </td>
        <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
        <td><span class="action_button nobutton">
          <span id="modstatus_${i}" class="icon ${(record.modstatus in [0,-1])?'inactive':(record.modstatus==3?'noread':'active')}" title="${record.modstatus}">&nbsp;</span>
        </span></td>
        <td><span class="action_button nobutton">
          <span id="paystatus_${i}" class="icon ${(record.paystatus in [-1,3])?'inactive':((record.paystatus in [1,2])?'noread':'active')}" title="${record.paystatus}">&nbsp;</span>
        </span></td>
        <td><span class="action_button nobutton">
          <span id="controlstatus_${i}" class="icon ${(record.controlstatus in [1,2])?'inactive':'active'}" title="${record.controlstatus}">&nbsp;</span>
        </span></td>
        <td>${record.rating}</td>
        <td>
        <g:if test="${!record.is_read}">
          <span class="action_button anowrap">
            <a href="javascript:void(0)" class="icon view" title="Прочитать" onClick="readevent(${record.id})"></a>
          </span>
        </g:if><g:if test="${!record.modstatus}">
          <span class="action_button anowrap">
            <a href="javascript:void(0)" class="icon photo" title="Подтвердить бронь" onClick="acceptbron(${record.id})"></a>
          </span>
        </g:if>
        </td>
      </tr>
      <script type="text/javascript">
        var modstatus,paystatus,controlstatus;
        switch(${record.modstatus}){
          case 0:  modstatus='неподтвержденные';break;
          case 1:  modstatus='предстоящие';break;
          case 2:  modstatus='текущие';break;
          case 3:  modstatus='прошлые';break;
          case -1: modstatus='недействительные';break;
        }
        switch(${record.paystatus}){
          case 0:  paystatus='не требуется';break;
          case 1:  paystatus='оплачено';break;
          case -1: paystatus='оплачено по недействительному заказу';break;
          case 2:  paystatus='сделка завершена';break;
          case 3:  paystatus='отказ от заезда';break;
        }
        switch(${record.controlstatus}){
          case -1: controlstatus='не требуется';break;
          case 0:  controlstatus='уведомлений не было';break;
          case 1:  controlstatus='уведомление "прошли сутки"';break;
          case 2:  controlstatus='уведомление "заезд сегодня"';break; 
        }
        jQuery('#modstatus_${i}').attr('title',modstatus);
        jQuery('#paystatus_${i}').attr('title',paystatus);
        jQuery('#controlstatus_${i}').attr('title',controlstatus);
      </script>
    </g:each>
    </table>
  </div>
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="adminbilling" action="${actionName}" params="${params}" 
        prev="&lt;&lt;" next="&gt;&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
