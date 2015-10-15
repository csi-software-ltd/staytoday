<script type="text/javascript">
  function selectOnchange(el){
    var objSel = document.getElementById(el); 
    var statusID = objSel.options[objSel.selectedIndex].value;
      
    if (statusID == 1)
      objSel.className = 'icon active always';
    else if(statusID == 0)
      objSel.className = 'icon inactive always';
  }	 
</script>
<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; }
  #resultList { border: 1px solid #505050; height: auto }
</style>

<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${params}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div id="resultList">  
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <thead>
        <tr> 
          <th>Код</th>
          <th>Код объекта</th>
		  <th>Тип отзыва</th>
          <th>Код польз.</th>
          <th>Статус</th>
          <th>Сведения об отзыве и его авторе</th>
          <th>Действия</th>
        </tr>        
      </thead>
      <tbody>
      <g:each in="${records}" status="i" var="record">
        <tr>
          <td>${record?.id}</td>
          <td>${record?.home_id}</td>
		  <td>${(record?.typeid==1)?'home':'user'}</td>
          <td>${record?.user_id}</td>
          <td align="center">
            <span class="action_button anowrap" style="border:0px;background:none;float:none">
              <g:if test="${record.comstatus==-1}"><span class="icon inactive" alt="забанен" title="снят">&nbsp;</span></g:if>			          
              <g:elseif test="${record.comstatus==1}"><span class="icon active" alt="активен" title="активен">&nbsp;</span></g:elseif>                                    
              <g:elseif test="${record.comstatus==0}"><span class="icon view" alt="не рассмотрен" title="не рассмотрен">&nbsp;</span></g:elseif>
            </span>            
          </td>        
          <td>
            <div style="width: 60%; float: left">
              <span nowrap>
                <img src="${(record?.picture && !record?.is_external)?imageurl:''}${(record?.picture)?record?.picture:resource(dir:'images',file:'user-default-picture.png')}" align="absmiddle" width="50" style="border:1px solid silver">
                <span style="color:#1D95CB"><b>${record.nickname}</b></span>
              </span>
            </div>
            <div style="width: 40%;float:right">
              <span style="color:silver">${String.format('%td.%<tm.%<tY %<tH:%<tM',record?.comdate)}</span>
              <span>
                <img src="${(record?.rating==2)?resource(dir:'images',file:'icons-verification-positively_reviewed.png'):
                  ((record?.rating==1)?resource(dir:'images',file:'icons-verification-negatively_reviewed.png'):'')}" align="absmiddle"/>
              </span>              
            </div>
            <div class="text" style="clear: both">
              <small>${record.comtext}</small>
            </div>
          </td>
          <td valign="top">
            <span class="actions">
            <g:if test="${record.comstatus in [1,-1]}">
              <span class="action_button nowrap" style="float:none">
                <g:if test="${record.comstatus==1}"><span class="icon inactive" onClick="reviewsConfirm(${record.id},-1);">снять</span></g:if>
                <g:if test="${record.comstatus==-1}"><span class="icon active" onClick="reviewsConfirm(${record.id},1);">подтвердить</span></g:if>
              </span>
            </g:if>
            <g:elseif test="${record.comstatus==0}">
              <div id="toggle_${record?.id}" class="set-availability action_button" onClick="toggleContainer(this,'toggle-info_${record?.id}');">
                <span class="clearfix icon view" style="width:100px">
                  <span class="label">не рассмотрен</span>
                  <span class="expand"></span>
                </span>
                <div id="toggle-info_${record?.id}" class="toggle-info" style="display: none;">
                  <div class="toggle-action-container">
                    <a class="toggle-action icon active" href="javascript:void(0)" onclick="reviewsConfirm(${record.id},1);">подтвердить</a>
                    <a class="toggle-action icon inactive" href="javascript:void(0)" onclick="reviewsConfirm(${record.id},-1);">снять</a>
                  </div>
                </div>                 
              </div>                       
            </g:elseif>                          
            </span><br/>
            <g:if test="${record?.comstatus==0}">
            <input type="checkbox" value="${record?.id}"/>
            </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>    
  </div>
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${params}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>  
</g:if>
</div>
