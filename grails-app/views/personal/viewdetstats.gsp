<style type="text/css">
  .dotted th, .dotted td { font-size: 13px; line-height: 18px; }
</style>
<g:if test="${data}">
<table width="100%" cellpadding="5" cellspacing="0" border="0">
  <tr>
    <td valign="top">
      <table class="dotted" cellpadding="0" cellspacing="0" border="0" rules="cols" frame="border">
        <tr>
          <!--th>№</th -->
          <th>${message(code:'ads.stat.date')}</th>
          <th>${message(code:'ads.stat.visits')}</th>
          <th>${message(code:'ads.stat.shows')}</th>
        </tr>
        <g:each in="${data}" var="item" status="i">
        <tr>  
          <!--td align="center">${i+1}</td -->
          <td>${item.name}</td>
          <td align="right">${item.quant}</td>
          <td align="right">${datalisting[i].quant}</td>
        </tr>
        </g:each>
      </table>
    </td>
    <td width="65%" align="center" valign="top">
      <img src="http://chart.apis.google.com/chart?cht=bhg&chco=66CC00,2477FE&chs=270x${61*data.size()+20}${bar}" style="margin-top:-20px">
    </td>
  </tr>
</table>
</g:if>
<g:else>
	<p align="center">${message(code:'ads.stat.shows.nofound')}</p>
</g:else>

