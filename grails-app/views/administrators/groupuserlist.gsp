<div class="glossy drop_shadow" style="width: 210px;padding:0px">
  <div class="header_container green">
    <h2>${inrequest?.part?'Пользователи':'Группы'}</h2>
  </div>
  <div style="padding:10px">
  <g:each in="${groupusers}" var="item" status="i">
    <a <g:if test="${i==0}">id="first"</g:if> href="javascript:void(0)" onclick="updateDetails(${item.id},${inrequest?.part})">${inrequest?.part?item.login:item.name}</a><br>
  </g:each>
    <div align="right" style="margin-top: 50px">
    <g:if test="${inrequest?.part}">
      <input type="button" class="button-glossy orange" value="Добавить" onclick="showUserWindow()"/>
    </g:if><g:else>
      <input type="button" class="button-glossy orange" value="Добавить" onclick="showGroupWindow()"/>
    </g:else>
    </div>  
  </div>
</div>
