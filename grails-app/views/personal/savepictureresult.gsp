<script language="javascript" type="text/javascript">
<g:each in="${data}" var="itt">
  window.top.window.stopUpload(${itt.num},"${itt.filename}","${itt.thumbname}",${itt.error},${itt.maxweight});
</g:each>
</script>

