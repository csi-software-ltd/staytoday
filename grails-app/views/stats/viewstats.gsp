<g:formRemote name="detailForm" url="[action:'viewdetstats']" update="[success:'detailData']" id="detailForm">
  <table cellspacing="15" style="text-align:left;border-bottom:1px solid black">
    <tr>
      <td colspan="5"><h3>${inrequest?.keyword.replace('+',' ') }</h3></td>
    </tr>
    <tr>
      <td>Период:</td>
      <td>
        <select name="time" id="time">
          <option value="0" <g:if test="${0==inrequest?.time}">selected</g:if>>Весь</option>
          <option value="1" <g:if test="${1==inrequest?.time}">selected</g:if>>Год</option>
          <option value="2" <g:if test="${2==inrequest?.time}">selected</g:if>>Месяц</option>
          <option value="3" <g:if test="${3==inrequest?.time}">selected</g:if>>2а месяца</option>
<g:if test="${'keywords'!=inrequest?.stat}">
          <option value="4" <g:if test="${4==inrequest?.time}">selected</g:if>>Неделя</option>
</g:if>
<g:if test="${'home'==inrequest?.stat}">
          <option value="5" <g:if test="${5==inrequest?.time}">selected</g:if>>Месяц понедельно</option>
          <option value="6" <g:if test="${6==inrequest?.time}">selected</g:if>>2а месяца понедельно</option>
</g:if>
        </select>
      </td>
      <td><input type="submit" id="detail_button" class="button-glossy green" value="Показать"></td>
    </tr>
  </table>
  
<input type="hidden" name="output" value="${inrequest?.output}">
<input type="hidden" name="stat" value="${inrequest?.stat}">
<input type="hidden" name="keyword" value="${inrequest?.keyword}">
<input type="hidden" name="id" value="${inrequest?.id}">
<input type="hidden" name="site_id" value="${inrequest?.site_id}">
<input type="hidden" name="lang_id" value="${inrequest?.lang_id}">

</g:formRemote>
<script type="text/javascript">
  $('detail_button').click();
</script>
</div><br><br>

<div id="detailData" align="left">
  
</div>
