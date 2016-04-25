<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { font-size: 12px }
  .dotted td a[href] { font-size: 12px }
  .dotted td { padding: 12px 3px; }
  #resultList { border: 1px solid #505050; height: auto; }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${inrequest}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div id="resultList">
    <table class="dotted" width="100%" cellpadding="2" cellspacing="0" border="0" rules="all" frame="none">
      <tr>
        <th>Код</th>
        <th>Имя</th>
        <th>Ник</th>
        <th>Тип</th>
        <th>Страна</th>
        <th>Статус</th>
        <th>Партнерка</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr+${i}">
        <td>${record.id}</td>
        <td>
          <g:link controller="administrators" action="clientDetails" id="${record.id}">${record.name}</g:link>
        </td>
        <td>${record.nickname}</td>
        <td>${record.type_id==1?'Физ. лицо':record.type_id==2?'Юр. лицо':'не задано'}</td>
        <td>${record.countryname}</td>
        <td>${record?.resstatus==1?'схема подтверждена':record?.resstatus==-1?'схема отклонена':record?.resstatus==2?'запрос на подключение':record?.resstatus==3?'запрос на изменение':'схема не подключена'}</td>
        <td>${record?.partnerstatus==0?'партнерка не подключена':record?.partnerstatus==2?'партнерка подключена':'запрос на подключение'}</td>
      </tr>
    </g:each>
    </table>
  </div>
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${inrequest}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
