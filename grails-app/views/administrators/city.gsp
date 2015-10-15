<g:if test="${city}">
  <option value=""></option>
	<g:each in="${city}" var="item" >
		<option value="${item.id}">${item.name}</option>
	</g:each>
</g:if>