<label for="region_id">Регион:</label>
<select name="region_id" id="region_id">
  <option value="0"></option>
  <g:each in="${regions}" var="item">
    <option value="${item.id}">${item.name}</option>
  </g:each>
</select>