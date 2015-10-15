<script type="text/javascript">
	showMenu(1);
	removeAllMarkers();
</script>
<div id="ajax_wrap">
  <div id="results_header" class="results_header">
    <span class="count" style="top:0">
      <b>${message(code:'ads.infras.records.found')}</b><span id="ads_count">${count}</span>
    </span>
    <span class="pagination col">
      <g:paginate controller="personal" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="5" params="${params}" total="${count}" /> 
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
  <g:each in="${records}" var="record" status="i">
    <tr>
      <td>
        <script type="text/javascript">
          addMarker(${record.x},${record.y},${record.type_id},"${record.name}","${record.description}",${record.id},1);
        </script>
        <div class="relative">                    
          <div class="map_number">${i+1}</div>
          <g:each in="${homeguidebooktype}" var="type">
            <g:if test="${record.type_id==type.id}">
          <img class="float" src="${resource(dir:'images/infras',file:type.icon)}" alt="${type['name'+context?.lang]}" title="${type['name'+context?.lang]}" />
            </g:if>
          </g:each>
          <div class="description" style="width:500px">                              
            <h2 class="title" style="padding-left:0px"><a href="javascript:void(0)" onclick="openBalloon(${record.id});" >${record.name}</a></h2>
            <small>${record.description}</small>
          </div>
          <div class="actions col">
            <span class="action_button anowrap">
              <a class="icon edit" title="${message(code:'button.edit')}" href="javascript:void(0);" onclick="edit(${record.x},${record.y},${record.type_id},${record.id},'${record.name}','${record.description}')"></a>
            </span>
            <span class="action_button anowrap">
              <g:remoteLink class="icon delete" title="${message(code:'button.delete')}" url="[controller:'personal', action:'infrasdelete', id:record.id, params:[home_id:home.id]]" onSuccess="afterDelete(e)"></g:remoteLink>
            </span>
          </div>              
        </div>
      </td>
    </tr>
    <g:if test="${(i+1)!=records.size()}"> 
    <tr>
      <td class="dashed">&nbsp;</td>
    </tr>
    </g:if>    
  </g:each>
  </table>
</g:if>
</div>
