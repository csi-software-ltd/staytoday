<html>
  <head>
  <title>Административное приложение StayToday.ru</title>
  <meta name="layout" content="admin" />
  <g:javascript library="jquery-1.8.3" /> 
  <g:javascript library="jquery.colorbox.min" />
  <g:javascript>
    function returnToList(){
      $("returnToListForm").submit();
    }
    function edit(lId,sText){
      $('mboxrec_id').value = lId;
      $('rectext').value = sText;
      $('rectext_edit_link').click();
    }
    function processresponse(){
      if (${mbox.is_approved==0?1:0}) location.reload(true);
      else $('mboxRecForm_submit').click();
    }
	</g:javascript>
  <style type="text/css">    
    ul.verifications-list li.verifications-list-item { float:left; width:250px; padding:5px 15px!important; border:none }
    ul.verifications-list li.verifications-list-item.mini { width:160px}
    ul.verifications-list li.verifications-list-item .verifications-legend { height:22px;width:22px;background:url(${resource(dir:'images',file:'action-button-icons.png')}) no-repeat left bottom;}
    ul.verifications-list li.verifications-list-item .verifications-legend.active { background-position: 0 -1px; }    
    ul.verifications-list li.verifications-list-item .verifications-legend.noactive { background-position: 0 -23px; }    
    ul.verifications-list li.verifications-list-item .verifications-legend.noread { background-position: 0 -177px; }
    ul.verifications-list li.verifications-list-item .verifications-legend.none { background: none }
  </style>
  </head>  
  <body onload="\$('mboxRecForm_submit').click();">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td colspan="4" style="padding:0px">
          <a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку переписок</a>
        </td>
      </tr>
      <tr>
        <td colspan="4"><h1 class="blue">Детали переписки №${mbox.id}</h1></td>
      </tr>
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon question"></span>Статусы</h2>
        </td>
      </tr>
      <tr>
        <td colspan="4">
          <ul class="verifications-list" style="float:left">
            <li class="verifications-list-item mini">              
              <div class="verifications-legend ${mbox.is_approved==1?'active':mbox.is_approved==0?'noread':'noactive'}"></div>
              <span class="label">Статус модерации</span><br/>
              <span class="count">${mbox.is_approved==1?'одобрено':mbox.is_approved==0?'не просмотрено':'предано анафеме'}</span>
            </li> 
            <li class="verifications-list-item mini">              
              <div class="verifications-legend ${mbox.is_answer?'active':'noactive'}"></div>
              <span class="label">Статус ответа</span><br/>
              <span class="count">${mbox.is_answer?'со стороны владельца':'со стороны клиента'}</span>
            </li>
            <li class="verifications-list-item mini">              
              <div class="verifications-legend ${mbox.is_read?'active':'noread'}"></div>
              <span class="label">Статус прочтения</span><br/>
              <span class="count">${mbox.is_read?'прочтено':'не прочтено'}</span>
            </li>
            <li class="verifications-list-item mini">              
              <div class="verifications-legend ${mbox.modstatus in [3,4]?'active':(mbox.modstatus in [1,2,6]?'noread':'noactive')}"></div>
              <span class="label">Статус бронирования</span><br/>
              <span class="count">${Mboxmodstatus.findByModstatus(mbox.modstatus)?.name?:'ошибка'}</span>
            </li>
            <li class="verifications-list-item" style="width:60px">                           
              <span class="label">Заявка</span><br/>
              <span class="count">${mbox.zayvka_id}</span>
            </li>                        
          </ul>
          <g:remoteLink class="button-glossy ${mbox.is_approved>=0?'red':'green'} mini" url="${[controller:'administrators',action:'mboxapprove',id:mbox.id,params:[status:mbox.is_approved>=0?-1:1]]}" onSuccess="location.reload(true)" style="float:right">${mbox.is_approved>=0?'Отклонить':'Одобрить'}</g:remoteLink>
        </td>
      </tr>      
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon"></span>Объект и участники</h2>
        </td>
      </tr>
      <tr>
        <td colspan="4">
          <ul class="verifications-list" style="float:left">
            <li class="verifications-list-item">  
              <b class="label">Гость:</b>
              <img src="${(client?.smallpicture && !client?.is_external)?imageurl:''}${(client?.smallpicture)?client?.smallpicture:resource(dir:'images',file:'user-default-picture.png')}" align="left" width="50" hspace="15" style="margin-bottom:40px;border:1px solid silver" />
              <span class="count">                
                <g:link action="users" params="${[user_id:client?.id?:0]}">${client?.id?:0}</g:link><br/>
                <g:link mapping="pView" params="${[uid:'id'+client?.id]}">${client?.nickname}</g:link><br/>
                email: <font color="green">${client?.email}</font>
              <g:if test="${client?.tel}">
                <br/>тел: <font color="green">${client?.tel}</font>
              </g:if><g:if test="${client?.tel1}">
                <br />д.тел: <font color="green">${client?.tel1}</font>
              </g:if><g:if test="${client?.skype}">
                <br />skype: <font color="green">${client?.skype}</font>
              </g:if>
              </span>
            </li>
            <li class="verifications-list-item">
              <b class="label">Хозяин:</b>
              <img src="${(ownerUser?.smallpicture && !ownerUser?.is_external)?imageurl:''}${(ownerUser?.smallpicture)?ownerUser?.smallpicture:resource(dir:'images',file:'user-default-picture.png')}" align="left" width="50" hspace="15" style="margin-bottom:40px;border:1px solid silver" />
              <span class="count">
                <g:link action="users" params="${[user_id:ownerUser?.id?:0]}">${ownerUser?.id?:0}</g:link><br/>                
                <g:link mapping="pView" params="${[uid:'id'+ownerUser?.id]}">${ownerUser?.nickname}</g:link><br/>
                email: <font color="green">${ownerUser?.email}</font><br/>
                тел: <font color="green">${ownerUser?.tel}</font>
              <g:if test="${ownerUser?.tel1}">
                <br />д.тел: <font color="green">${ownerUser?.tel1}</font>
              </g:if><g:if test="${ownerUser?.skype}">
                <br />skype: <font color="green">${ownerUser?.skype}</font>
              </g:if>
              </span>
            </li>
            <li class="verifications-list-item" style="width:300px">
              <b class="label">Объект:</b>
              <img src="${home.mainpicture?homeimageurl+home.client_id+'/t_'+home.mainpicture:resource(dir:'images',file:'default-picture.png')}" align="left" width="100" hspace="15" style="border:1px solid silver">
              <span class="count">
                <g:link action="moderateHome" id="${home?.id?:0}">${home?.id?:0}</g:link><br/>                
                <g:link mapping="hView" params="${[linkname:home?.linkname,city:home?.city,country:Country.get(home?.country_id)?.urlname]}" target="_blank">${home.name}</g:link><br/>
                ${home.address}<br/>
                заезд: ${String.format('%td.%<tm.%<ty',mbox.date_start)} - ${String.format('%td.%<tm.%<ty',mbox.date_end)}<br/>
                гости: ${Homeperson.get(mbox.homeperson_id)?.kol?:'0'}
                <div class="price_data">
                  <div class="currency">${mbox?.price_rub} <sup>руб.</sup></div>          
                </div>
              </span>
            </li>
          </ul>
        </td>
      </tr>    
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Комментарий администратора</h2>
        </td>
      </tr>      
      <tr>
        <td colspan="4">
          <g:formRemote name="mboxComment" url="[action:'mboxComment',id:mbox.id]">
            <g:textArea name="comment" style="width:845px;height:40px;float:left">${mbox?.comment}</g:textArea>
            <input type="submit" class="button-glossy green mini" value="Сохранить" style="float:right" />
          </g:formRemote>
        </td>
      </tr>    
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Чиркануть письмишко</h2>
        </td>
      </tr>
      <tr>
        <td colspan="4" nowrap>
          <g:formRemote name="mboxRecForm" url="[action:'mboxrecadd',id:mbox.id]" onSuccess="\$('mboxRecForm_submit').click();\$('newrectext').value='';">
            <ul class="verifications-list" style="float:left">
              <li class="verifications-list-item mini" style="margin-top:-7px">
                <b class="label">Направление:</b><br/><br/>
                <span class="count">
                  <g:radioGroup name="is_answer" labels="['Клиент → Владелец','Владелец → Клиент','Администратор → Владелец','Администратор → Клиент']" values="${0..3}" value="0">
                    <g:rawHtml><font color="#333">${it.radio} ${it.label}</font><br/></g:rawHtml>
                  </g:radioGroup>              
                </span>
              </li>
              <li class="verifications-list-item" style="width:640px">  
                <textarea id="newrectext" name="rectext" style="width:100%;height:85px"/></textarea>
              </li>
            </ul>
            <input type="submit" class="button-glossy green mini" value="Отправить" style="margin-top:30px;float:right" />            
          </g:formRemote>
        </td>
      </tr>
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon promo"></span>Сообщения</h2>
        </td>
      </tr>
      <tr>
        <td colspan="4">
          <div id="mboxrec_list"></div>
        </td>
      </tr>
      <tr>
        <td colspan="4"><a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку переписок</a></td>
      </tr>
    </table>
  <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'administrators',action:'mbox', params:[fromDetails:1]]}">
  </g:form>
  <g:formRemote name="mboxRecForm" url="[action:'mboxreclist',id:mbox.id]" update="[success:'mboxrec_list']">
    <input type="submit" id="mboxRecForm_submit" value="Найти" style="display:none">
  </g:formRemote>

  </body>
</html>
