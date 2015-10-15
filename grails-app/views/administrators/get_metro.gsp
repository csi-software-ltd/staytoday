<g:each in="${metro}" var="item" status="i">	 	  
  <span><input name="li_metro" id="checkbox_${i}" type="checkbox" <g:each in="${metro_ids}"><g:if test="${it.toLong()==item.id}">checked</g:if></g:each>/>&nbsp;&nbsp;${item.name}</span><br/>		
  <input id="id_${i}" type="hidden" value="${item.id}"/>
</g:each>

