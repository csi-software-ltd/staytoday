<table>
  <tr>
    <td>
      <div align="left" style="border:1px solid black">
        <table cellspacing="5">
          <g:each in="${data}" var="item">
            <tr>
<g:if test="${inrequest.stat=='home' || inrequest.stat=='prop'}">
			  <td>id№ ${item.id}</td>
			  <td width="15px"></td>
</g:if>
              <td><g:remoteLink url="[action:'viewstats', id:item.id, params:[keyword:item.name,time:inrequest.time,stat:inrequest.stat,output:inrequest.output,site_id:inrequest.site_id]]" update="placeDetail">${item.name}</g:remoteLink></td>
              <td width="15px"></td>
              <td>${item.quant}</td>
            </tr>
          </g:each>
        </table>
      </div>
    </td>
    <td width="50"></td>
    <td valign="top">
      <div id="placeDetail" align="left"></div>
    </td>
  </tr>
</table>


