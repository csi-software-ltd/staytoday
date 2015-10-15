<div style="border-bottom: 1px solid #c0c0c0">
  <img class="col" src="${resource(dir:'images',file:'delete.gif')}" onclick="togglePopDirection();" border="0" />
<g:each in="${countries}" var="country" status="i">
  <a onclick="updatePopDirection(${country?.id})" href="javascript:void(0)" rel="nofollow">${country['name'+context?.lang]}</a> 
</g:each>  
</div>
<div id="sel_where" style="cursor:pointer;height:140px;overflow-y:auto;" onblur="togglePopDirection();">
<g:each in="${data}" var="item">
  <div onclick="clickPopDirection('${item['name'+context?.lang]}')" onmouseover="this.addClassName('selected');" onmouseout="this.removeClassName('selected')">
    ${item['name'+context?.lang]}
  </div>
</g:each>
</div>
