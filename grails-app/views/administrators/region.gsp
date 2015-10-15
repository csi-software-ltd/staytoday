<g:if test="${region}">
  <option value=""></option>
	<g:each in="${region}" var="item" >
		<option value="${item.id}">${item.name}</option>
	</g:each>
</g:if>