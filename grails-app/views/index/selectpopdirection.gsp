<div style="border-bottom: 1px solid #c0c0c0">
  <img class="col" src="${resource(dir:'images',file:'delete.gif')}" onClick="togglePopDirection();" border="0" />
<g:each in="${countries}" var="country" status="i"><g:if test="${country.id in countryIds}">
  <a onClick="updatePopDirection(${country.id})" href="javascript:void(0)">${country.name}</a> 
</g:if></g:each>  
</div>
<div id="sel_where" style="cursor:pointer;height:140px;overflow-y:auto;" onBlur="togglePopDirection();">
<g:each in="${data}" var="item">
  <div onclick="clickPopDirection('${item.keyword}')" onmouseover="this.addClassName('selected');" onmouseout="this.removeClassName('selected')">
    ${item.name2}
  </div>
</g:each>
</div>
