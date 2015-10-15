<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th, .dotted td a[href] { font-size: 12px }
  .dotted td { padding: 12px 3px; }
  #resultList { border: 1px solid #505050; height: auto; }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div id="resultList">
    <table class="dotted" width="100%" cellpadding="2" cellspacing="0" border="0" rules="all" frame="none">
      <tr align="center">
        <th>Код</th>
        <th>Фото</th>
        <th>Название</th>
        <th>Описание</th>
        <th width="50">Дата создания</th>
        <th width="25">Статус</th>
        <th width="70">Действия</th>
      </tr>
    <g:each in="${records}" status="i" var="record">
      <tr id="tr+${i}">
        <td rowspan="2">${record.id}</td>
        <td rowspan="2">
          <img src="${record?.picture?imageurl.toString()+record?.picture:resource(dir:'images',file:'default-picture.png')}" align="absmiddle" width="120" style="border:1px solid silver">
        </td>
        <td rowspan="2">${record.title}</td>
        <td>${record.shortdescription}</td>
        <td rowspan="2">${String.format('%td.%<tm.%<ty',record.inputdate)}</td>
        <td rowspan="2" align="center">
          <img width="24" height="24" src="${(record.modstatus==1)?resource(dir:'images',file:'checkmark.png'):resource(dir:'images',file:'checkmark-inactive.png')}" alt="${(record.modstatus==1)?'Активна':'Не активна'}" title="${(record.modstatus==1)?'Активна':'Не активна'}"/>
        </td>
        <td rowspan="2">
          <span class="action_button anowrap">
            <g:link class="icon edit" title="Посмотреть детали" controller="administrators" action="articleDetails" id="${record.id}"></g:link>
          </span>
          <span class="action_button anowrap">
            <g:link class="icon view" title="Посмотреть на сайте" mapping="timeline" params="${[blog:record?.author,year:record.inputdate.getYear()+1900,month:record.inputdate.getMonth()+1<10?'0'+(record.inputdate.getMonth()+1):record.inputdate.getMonth()+1,day:record.inputdate.getDate()<10?'0'+record.inputdate.getDate():record.inputdate.getDate(),id:record.linkname]}" target="_blank"></g:link>
          </span>
          <span class="action_button anowrap">
            <g:remoteLink class="icon ${record.modstatus==0?'active':'inactive'}" title="${message(code:record.modstatus==0?'button.recover':'button.hide')}" controller="administrators" action="togglearticlestatus" id="${record.id}" onSuccess="\$('article_submit_button').click();"></g:remoteLink>
          </span>
        </td>
      </tr>
      <tr>
        <td style="padding: 0 3px;">Теги:
          <g:each in="${record.tags}" var="tag" status="j"><g:if test="${j}">,</g:if>
          ${tag.name}
          </g:each>
        </td>
      </tr>
    </g:each>
    </table>
  </div>
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
