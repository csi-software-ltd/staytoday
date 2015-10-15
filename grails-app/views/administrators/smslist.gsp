<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${params}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">  
  <div id="resultList">  
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" border="0" rules="all" frame="none">
      <thead>
        <tr align="center">
          <th rowspan="2">Код</th>
          <th colspan="2">Пользователь</th>
          <th rowspan="2">Дата</th>
          <th rowspan="2">server_id</th>
          <th rowspan="2">Статус</th>
          <th rowspan="2">Номер телефона</th>
          <th rowspan="2">Смс код</th>
        </tr>
        <tr align="center">
          <th>код</th>
          <th>никнейм</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${records}" status="i" var="record">
        <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
          <td>${record.id}</td>
          <td>${record.user_id}</td>
          <td nowrap>
            <div class="user">
              <b><a href="javascript:void(0)">${record.user_name}</a></b>
            </div>
          </td>
          <td nowrap>${String.format('%td.%<tm.%<tY %<tH:%<tM',record.inputdate)}</td>
          <td>${record.server_id}</td>
          <td>${record.status}</td>
          <td>${record.tel}</td>
          <td><abbr title="${record.smscode}"><g:shortString text="${record.smscode}" length="30"/></abbr></td>
        </tr>
      </g:each>
      </tbody>
    </table>        
  </div>
</g:if>
</div>
