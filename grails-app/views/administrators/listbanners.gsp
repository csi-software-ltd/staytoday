<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; }
  #resultList { border: 1px solid #505050; height:auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${data?.count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${inrequest}"
        total="${data?.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${data?.records}">
  <div id="resultList">  
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <thead>
        <tr>
          <th>Код</th>
          <th>Название</th>
          <th>Тип</th>
          <th>Период показа</th>
          <th>Показы/клики/CTR</th>     
          <th>Ссылка</th>
          <th>Действия</th>        		          
        </tr>
      </thead>
      <tbody>
      <g:each in="${data?.records}" var="item">
        <tr style="color:${item.modstatus?'black':'#AAAAAA'}">
          <td>${item.id}</td>
          <td>${item.bname}</td>
          <td>${item.advbannertypes_name}</td>
          <td>${String.format('%td.%<tm.%<tY',item.date_start)}-${String.format('%td.%<tm.%<tY',item.date_end)}</td>
          <td>${item.bcount}/${item.bclick}/${(item.bcount&&item.bclick)?((100*item.bclick)).toBigDecimal().divide(item.bcount,new java.math.MathContext(2)):0}%</td>       
          <td>
          <g:if test="${(item.burl?:'').size()>0}">
            <a href="${item.burl.find(/http:/)?item.burl:'http://'+item.burl}" target="_blank">${item.burl.find(/http:/)?item.burl:'http://'+item.burl}</a>
          </g:if>
          </td>
          <td align="center">
            <div class="actions" nowrap>
              <span class="action_button anowrap">
                <g:link class="icon edit" title="Редактировать" controller="administrators" action="editbanners" id="${item?.id}" params="[edit:1]"></g:link>
              </span>
              <span class="action_button anowrap">
                <g:link class="icon delete" title="Удалить" controller="administrators" action="deletebanner" id="${item?.id}"></g:link>
              </span>
            </div>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${data?.count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${inrequest}"
        total="${data?.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>  
</g:if>
</div>
