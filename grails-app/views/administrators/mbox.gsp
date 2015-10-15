<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
      function initialize(){
        $('mbox_submit_button').click();
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
        $('typeid').selectedIndex = 0;
        $('is_approved').selectedIndex = 0;        
        $('is_read').selectedIndex = 0;
        $('modstatus').selectedIndex = 0;        
        $('is_answer').selectedIndex = 0;        
		    $('order').selectedIndex = 0;
        $('id').setValue('');
        $('home_id').setValue('');
        $('owner_id').setValue('');        
        $('user_id').setValue('');        
        $('date_start').setValue('');
        $('date_start_year').setValue('');
        $('date_start_month').setValue('');
        $('date_start_day').setValue('');
        $('date_start_value').setValue('');
        $('date_end').setValue('');
        $('date_end_year').setValue('');
        $('date_end_month').setValue('');
        $('date_end_day').setValue('');
        $('date_end_value').setValue('');        
      }
      function movetoprop(lId){
        if (confirm('Перевести запрос клиента в заявки?')){
          <g:remoteFunction controller='administrators' action='movetoprop' onSuccess="\$('mbox_submit_button').click();" params="'id='+lId" />
        }
      }
      function loginAsUser(iId){
        <g:remoteFunction controller='administrators' action='loginAsUser' onSuccess='processResponse(e)' params="'id='+iId" />
      }
      function processResponse(e){
        window.open('${((context.is_dev)?"/"+context.appname+"/inbox/index":"/inbox/index")}');
      }
      function setAuto(iId){
        <g:remoteFunction controller='administrators' action='setautomoderate' onSuccess='location.reload(true)' params="'id='+iId" />
      }
    </g:javascript>
  </head>
	<body onload="initialize()">
    <div id="reviews">
      <g:formRemote name="mboxForm" url="[action:'mboxlist']" update="[success:'mbox_list']" onSuccess="\$('is_stat').value=0;">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:15px">
          <tr>
            <td nowrap>
              <label for="id">Код:</label>
              <input type="text" id="id" name="id" value="${inrequest?.id}" style="width:80px">
              &nbsp;         
              <label for="home_id">Код жилья:</label>
              <input type="text" id="home_id" name="home_id" value="${inrequest?.home_id}" style="width:80px">
              &nbsp;
              <label for="owner_id">Код владельца:</label>
              <input type="text" id="owner_id" name="owner_id" value="${inrequest?.owner_id?:inrequest?.owner_id_str?:''}" style="width:200px">
              &nbsp;
              <label for="user_id">Код клиента:</label>
              <input type="text" id="user_id" name="user_id" value="${inrequest?.user_id?:inrequest?.user_id_str?:''}" style="width:200px">              
            </td>
          </tr>
          <tr>
            <td nowrap>            
              <label for="typeid">Время заезда:</label>
              <select id="typeid" name="typeid">
                <option value="0" <g:if test="${!inrequest?.typeid}">selected="selected"</g:if>>все</option>
                <option value="1" <g:if test="${inrequest?.typeid==1}">selected="selected"</g:if>>предстоящие</option>
                <option value="2" <g:if test="${inrequest?.typeid==2}">selected="selected"</g:if>>прошедшие</option>
              </select>
              <label for="is_approved">Модерация:</label>
              <g:select id="is_approved" name="is_approved" value="${inrequest?.is_approved}" from="${['все','отклонено','новые','одобрено']}" keys="${-2..1}"/>&nbsp;
              <label for="is_read">Прочтение:</label>
              <select id="is_read" name="is_read">
                <option value="-1" <g:if test="${inrequest?.is_read==-1}">selected="selected"</g:if>>все</option>
                <option value="0" <g:if test="${inrequest?.is_read==0}">selected="selected"</g:if>>не прочтено</option>
                <option value="1" <g:if test="${inrequest?.is_read==1}">selected="selected"</g:if>>прочтено</option>
              </select>&nbsp;
              <label for="modstatus">Переписка:</label>
              <g:select id="modstatus" name="modstatus" value="${inrequest?.modstatus}" from="${Mboxmodstatus.list()}" optionKey="modstatus" optionValue="name" noSelection="['0':'все']"/>
            </td>
          </tr>
          <tr>
            <td>
              <label for="is_answer">От кого:</label>
              <select id="is_answer" name="is_answer">
                <option value="-1" <g:if test="${inrequest?.is_answer==-1}">selected="selected"</g:if>>все</option>
                <option value="0" <g:if test="${inrequest?.is_answer==0}">selected="selected"</g:if>>клиент</option>
                <option value="1" <g:if test="${inrequest?.is_answer==1}">selected="selected"</g:if>>владельц</option>
              </select>&nbsp;
              <label for="order">Сортировать по:</label>
              <select id="order" name="order">
                <option value="0" <g:if test="${!inrequest?.order}">selected="selected"</g:if>>дате модификации</option>
                <option value="1" <g:if test="${inrequest?.order==1}">selected="selected"</g:if>>дате заезда</option>
                <option value="2" <g:if test="${inrequest?.order==2}">selected="selected"</g:if>>дате создания</option>
              </select>&nbsp;&nbsp;
              <span nowrap>
                <label for="date_start">Дата создания c:</label>
                <calendar:datePicker name="date_start" needDisable="false" dateFormat="%d-%m-%Y" value="${inrequest?.date_start}"/>
              </span>
              <span nowrap>
                <label for="date_end">по:</label>
                <calendar:datePicker name="date_end" needDisable="false" dateFormat="%d-%m-%Y" value="${inrequest?.date_end}"/>
              </span>              
            </td>
          </tr>
          <tr>
            <td align="right" nowrap>
              <g:if test="${admin.accesslevel}">
                <input type="button" class="button-glossy red" onclick="setAuto(${automoderate?0:1})" value="${automoderate?'Отключить':'Включить'} автомодерацию" style="margin:0 5px" />
              </g:if>  
              <input type="submit" class="button-glossy green" id="mbox_submit_button" value="Показать" />
              <input type="button" class="button-glossy grey" value="Сброс" onclick="resetData()" style="margin: 0 5px"/>
              <input type="button" class="button-glossy lightblue" value="Статистика" onclick="$('is_stat').value=1;$('mbox_submit_button').click();"/>
            </td>
          </tr>
        </table>
        <input type="hidden" id="is_stat" name="is_stat" value="0"/>
      </g:formRemote>
    </div>   
    <div id="mbox_list"></div>
  </body>
</html>
