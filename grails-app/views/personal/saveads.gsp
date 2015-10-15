<html>
<head>
</head>
<body>
<g:form name="saveadsForm" controller="personal" action="editads" id="${inrequest?.id?:0}" base="${context?.mainserverURL_lang}">
  <g:collect in="${inrequest}" expr="it">
	<input type="hidden" name="${it.key}" value="${it.value}">
  </g:collect>
</g:form>
<script type="text/javascript">
document.getElementById("saveadsForm").submit();
</script>
</body>
</html>
	  