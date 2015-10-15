<div align="left">
<table>
<tr>
  <td valign="top" rowspan="2">
    <table border="1">
      <tr>
        <td>&nbsp;</td>
        <td>Дата</td>
        <td>Количество</td>
      </tr>
      <g:each in="${data}" var="item" status="i">
      <tr>  
        <td>${i+1}</td><td>${item.name}</td><td>  ${item.quant}</td>
      </tr>
      </g:each>
    </table>
  </td>
  <td valign="top" width="250"><img src="http://chart.apis.google.com/chart?cht=bvg&chs=350x150${bar}"></td>
</tr>
<tr>
  <g:if test="${(circle!='')}">   
  <td valign="top" width="450"><img src="http://chart.apis.google.com/chart?cht=p3&chs=400x150${circle}"></td>
  </g:if>
</tr>
</table>
</div>
