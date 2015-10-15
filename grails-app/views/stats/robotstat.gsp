<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; color: inherit; }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${statistic.count}</span>
    <span style="float:right">
      <g:paginate controller="stats" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${params}"
        total="${statistic.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${statistic.records}">
  <div id="resultList">
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" border="0" rules="all" frame="none">
      <thead>
        <tr align="center" style="color:#FFFFFF">
          <th>Робот</th>
          <th>Город</th>
          <th>Тип</th>
          <th>Подтип</th>
          <th>Дата посещения</th>
          <th>Дата обновления</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${statistic.records}" status="i" var="record">
        <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}" style="color:${record.spider_id==15?'blue':'red'}!important">
          <td>${record.spiderman}</td>
          <td>${record.keyword}</td>
          <td>${pagetypes[record.type]}</td>
          <td>${record.type==65?hometypes.find{it.id==record.prop.toInteger()}?.name3:record.type==66?Citysight.get(record.prop.toInteger())?.name:record.type==32?Metro.get(record.prop.toInteger())?.name:record.prop}</td>
          <td>${record.requesttime?String.format('%tF %<tT', record.requesttime):''}</td>
          <td>${record.moddate?String.format('%tF %<tT', record.moddate):''}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</g:if>
</div>
