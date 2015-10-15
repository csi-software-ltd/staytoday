<div style="border-bottom: 1px solid #c0c0c0">
  <img src="${resource(dir:'images',file:'delete.gif')}" onClick="toggleRegion();" border="0" class="col" />
  <g:each in="${countries}" var="country" status="i">
		<a onClick="updateRegion(${country.id})" href="javascript:void(0)">${country.name}</a> 
  </g:each>  
</div>
<div id="sel_where" style="cursor:pointer;height:140px;overflow-y:auto;" onBlur="toggleRegion();">
  <g:each in="${data}" var="item">
    <div onclick="clickRegion('${item.name}')" onmouseover="this.addClassName('selected');" onmouseout="this.removeClassName('selected')">
       ${item.name}
    </div>
  </g:each>
</div>
