<g:if test="${region}">
	<g:each in="${region}" var="item" >
		<option value="${item.id}">${item.('name'+lang)}</option>
	</g:each>
</g:if>