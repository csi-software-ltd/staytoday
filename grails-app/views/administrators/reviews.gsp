<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
      function initialize(){
        $('user_submit_button').click();
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
      
      function reviewsToMainpage(iId,status){
        if (confirm(status?'Выводить на главной?':'Убрать с главной?')){
          <g:remoteFunction controller='administrators' action='reviewsToMainpage' onSuccess='initialize()' params="'id='+iId+'&status='+status" />
        }
      }

      function reviewsConfirm(iId,status){
        if (confirm((status==1)?'Подтвердить отзыв?':'Снять отзыв?')){
          <g:remoteFunction controller='administrators' action='reviewsConfirm' onSuccess='initialize()' params="'id='+iId+'&status='+status" />
        }
      }

      function resetData(){
        $('rating').selectedIndex = 0;
        $('comstatus').selectedIndex = 0;
		$('typeid').selectedIndex = 0;
        $('home_id').setValue('');
        $('user_id').setValue('');
        $('comdate').setValue('');
        $('comdate_year').setValue('');
        $('comdate_month').setValue('');
        $('comdate_day').setValue('');
        $('comdate_value').setValue('');
      }
     
      function toggleContainer(el,container){ 
        var toggle = $(el);           
      
        if(toggle.className == 'set-availability action_button')
          toggle.className = 'set-availability action_button expanded';
        else if(toggle.className == 'set-availability action_button expanded')
          toggle.className = 'set-availability action_button'; 
        
        $(container).toggle();
      }
      
      function reviewsData(){
        if (confirm('Подтвердить отмеченные?')){
          var iIds='';
          var inputs = $('resultList').getElementsByTagName('input'); 
          for (var i=0; i<inputs.length; i++)
            if(inputs[i].getAttribute("type")=='checkbox' && inputs[i].checked)
              iIds = iIds + inputs[i].value + ',';  
          iIds = iIds.substring(0,iIds.length-1);
          <g:remoteFunction controller='administrators' action='reviewsData' onSuccess='initialize()' params="'ids='+iIds" />
        }
      }
    </g:javascript>  
  </head>  

	<body onload="initialize()">
  
    <div id="reviews">
      <g:formRemote name="allForm" url="[action:'reviewslist']" update="[success:'companystat']">
      <input type="hidden" name="stat" id="stat" value="direction">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
        <tr>
          <td colspan="2" nowrap>
            <label for="user_id">Код объявления:</label>
            <input type="text" id="home_id" name="home_id" value="${inrequest?.home_id}" style="width:80px">
            &nbsp;&nbsp;
            <label for="user_id">Код пользователя:</label>
            <input type="text" id="user_id" name="user_id" value="${inrequest?.user_id}" style="width:80px">
            &nbsp;&nbsp;
            <label for="comdate">Дата создания:</label>
            <calendar:datePicker name="comdate" needDisable="false" dateFormat="%d-%m-%Y" value="${inrequest?.comdate}"/>&nbsp;&nbsp;&nbsp;&nbsp;
            <label for="typeid">Тип отзыва:</label>
            <select id="typeid" name="typeid">
              <option value="0" <g:if test="${inrequest?.typeid==0}">selected="selected"</g:if>>все</option>
              <option value="1" <g:if test="${inrequest?.typeid==1}">selected="selected"</g:if>>объявления</option>
              <option value="2" <g:if test="${inrequest?.typeid==2}">selected="selected"</g:if>>пользователи</option>
            </select>
          </td> 
        </tr>
        <tr>
          <td nowrap>
            <label for="comstatus">Статус отзыва:</label>
            <select id="comstatus" name="comstatus">
              <option value="0" <g:if test="${inrequest?.comstatus==null}">selected="selected"</g:if>>не рассмотрен</option>   
              <option value="1" <g:if test="${inrequest?.comstatus==1}">selected="selected"</g:if>>подтвержден</option>
              <option value="-1" <g:if test="${inrequest?.comstatus==-1}">selected="selected"</g:if>>снят</option>
              <option value="-2" <g:if test="${inrequest?.comstatus==-2}">selected="selected"</g:if>>все</option>
            </select>&nbsp;&nbsp;
            <label for="rating">Рейтинг:</label>
            <select name="rating" id="rating">
              <option value="-1" selected="selected">все</option>
              <option value="0">нейтральный</option>
              <option value="1">отрицательный</option>
              <option value="2">положительный</option>
            </select>
          </td>
          <td align="right" nowrap>
            <input type="submit" class="button-glossy green" id="user_submit_button" value="Показать">
            <input type="button" class="button-glossy grey" value="Сброс" onClick="resetData()" style="margin: 0 10px"/>
            <input type="button" class="button-glossy orange" value="Рассмотреть" onClick="reviewsData()"/>
          </td>
        </tr>
      </table>   
      </g:formRemote>
    </div>
    
    <div id="companystat"></div>
    <div id="sitestat">
      <div id="placeList" align="left">        
    </div>  
  
  </body>
</html>
