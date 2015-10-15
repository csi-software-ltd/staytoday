<div id="ajax_wrap1">
  <div class="results_header relative">
    <span class="count float" style="top:0px">
      <b>${message(code:'ads.infras.records.found')}</b><span id="ads_count">${infr.count}</span>
    </span>
    <span class="pagination col" style="margin-right:10px">
      <g:paginate controller="home" prev="&lt;" next="&gt;"
        action="${actionName}" max="5" params="${inrequest}"	total="${infr.count}" />
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>       
    </span>
  </div>
<g:if test="${infr.records}">
  <table width="100%" cellpadding="5" cellspacing="0" border="0">
  <g:each in="${infr.records}" var="record" status="i">
    <script type="text/javascript">
      xArray.push(${record.x})
      yArray.push(${record.y})
      typeArray.push(${record.type_id})
      nameArray.push("${record.name}")
      descriptionArray.push("${record.description}")
	  idsArray.push(${record.id})
    </script>
    <tr>
      <td onmouseout="this.removeClassName('selected')" onmouseover="this.addClassName('selected')">
        <div class="relative" style="margin:10px">                    
          <div class="map_number">${i+1}</div>
          <g:each in="${homeguidebooktype}" var="type"><g:if test="${record.type_id==type.id}">                
          <img class="float" src="${resource(dir:'images/infras',file:type.icon)}" alt="${type.name}">
          </g:if></g:each>
          <div class="description">                              
            <h2 class="title"><a class="name" href="javascript:void(0)" rel="nofollow" onclick="openBalloon(${record.id});">${record.name}</a></h2>
            <div><small>${record.description}</small></div>
          </div>
        </div>
      </td>
    </tr>
    <g:if test="${(i+1)!=infr.records.size()}"> 
    <tr><td class="dashed">&nbsp;</td></tr>
    </g:if>       
  </g:each>
  </table>
  <script type="text/javascript">
    addInfras();
  </script>
</g:if> 
</div>
