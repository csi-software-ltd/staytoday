<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
      function initialize(){
        $('client_submit_button').click();
      }

      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null){
          return;
        }
  
        new Ajax.Updater(
          { success: $('ajax_wrap') },
          link.href,
          { evalScripts: true });
      }

      function resetData(){
        $('resstatus').selectedIndex = 0;
        $('country_id').selectedIndex = 0;
        $('type_id').selectedIndex = 0;
		    $('partnerstatus').selectedIndex = 0;
        $('client_id').setValue('');
        $('client_name').setValue('');
        $('user_nickname').setValue('');
      }
    </g:javascript>
  </head>
	<body onload="initialize()">
    <div id="clients">
      <g:formRemote name="clientForm" url="[action:'clientlist']" update="[success:'client_list']">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
        <tr>
          <td colspan="2" nowrap>
            <span nowrap>
              <label for="client_id">Код:</label>
              <input type="text" id="client_id" name="id" value="${inrequest?.id}" style="width:60px">
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="client_name">Имя:</label>
              <input type="text" id="client_name" name="name" value="${inrequest?.name}" style="width:260px">
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="user_nickname">Ник:</label>
              <input type="text" id="user_nickname" name="nickname" value="${inrequest?.nickname}" style="width:260px">
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="type_id">Тип:</label>
              <select id="type_id" name="type_id">
                <option value="0" <g:if test="${!inrequest?.type_id}">selected="selected"</g:if>>Все</option>
                <option value="1" <g:if test="${inrequest?.type_id==1}">selected="selected"</g:if>>Физ. лица</option>
                <option value="2" <g:if test="${inrequest?.type_id==2}">selected="selected"</g:if>>Юр. лица</option>
              </select>
            </span>
          </td>
        </tr>
        <tr>
          <td nowrap>
            <span nowrap>
              <label for="resstatus">Статус:</label>
              <select id="resstatus" name="resstatus">
                <option value="-2" <g:if test="${inrequest?.resstatus==-2}">selected="selected"</g:if>>к рассмотрению</option>
                <option value="0" <g:if test="${inrequest?.resstatus==0}">selected="selected"</g:if>>не подключены</option>
                <option value="-1" <g:if test="${inrequest?.resstatus==-1}">selected="selected"</g:if>>отклонены</option>
                <option value="1" <g:if test="${inrequest?.resstatus==1}">selected="selected"</g:if>>подтверждены</option>
                <option value="2" <g:if test="${inrequest?.resstatus==2}">selected="selected"</g:if>>подключение</option>
                <option value="3" <g:if test="${inrequest?.resstatus==3}">selected="selected"</g:if>>изменение</option>
                <option value="-3" <g:if test="${inrequest?.resstatus==-3}">selected="selected"</g:if>>все</option>
              </select>
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="country_id" class="nopadd">Страна</label>
              <g:select name="country_id" from="${country}" value="${inrequest?.country_id?:0}" noSelection="${[0:'Все']}" optionKey="id" optionValue="name" />
            </span>&nbsp;&nbsp;
            <span nowrap>
              <label for="partnerstatus" class="nopadd">Партнерка:</label>
              <g:select name="partnerstatus" from="['отклонена','не подключена','подключение','подтверждена']" keys="${-1..2}" value="${inrequest?.partnerstatus}" noSelection="${['-100':'все']}" />
            </span>
          </td>
          <td align="right" nowrap>
            <input type="submit" class="button-glossy green" id="client_submit_button" value="Показать" style="margin: 0 10px" />
            <input type="button" class="button-glossy grey" value="Сброс" onClick="resetData()"/>
          </td>
        </tr>
      </table>   
      </g:formRemote>
    </div>
    
    <div id="client_list"></div>
  </body>
</html>
