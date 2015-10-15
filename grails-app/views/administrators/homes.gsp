<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <style type="text/css">.glossy td { font-size: 15px; }</style>    
    <g:javascript>
      function initialize(){
        $('user_submit_button').click();
       
	      <g:if test="${temp_notification!=null}">
          alert('${temp_notification?.text}');	  
	      </g:if>      	      
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
      function toMainpage(iId,status){
        if (confirm(status?'Выводить на главной?':'Убрать с главной?')){
          <g:remoteFunction controller='administrators' action='toMainpage' onSuccess="\$('user_submit_button').click();" params="'id='+iId+'&status='+status" />
        } else {
          $('user_submit_button').click();
        }
      }
      function toSpecoffer(iId,status){
        if (confirm(status?'Выводить в предложениях дня?':'Убрать из предложений дня?')){
          <g:remoteFunction controller='administrators' action='toSpecoffer' onSuccess="\$('user_submit_button').click();" params="'id='+iId+'&status='+status" />
        } else {
          $('user_submit_button').click();
        }
      }
      function resetData(){
        $('home_id').setValue('');
        $('user_id').setValue('');
        $('client_name').setValue('');
        $('modstatus').selectedIndex = 0;
        $('is_mainpage').selectedIndex = 0;
        $('is_specoffer').selectedIndex = 0;
        $('country_id').selectedIndex = 0;
        $('region_id').selectedIndex = 0;
        $('popdir_id').selectedIndex = 0;
        $('is_confirmed').selectedIndex = 0;
        document.getElementById('reviews').children[0].checked=false;
        document.getElementById('hotdiscount').children[0].checked=false;
        document.getElementById('longdiscount').children[0].checked=false;
        $('inputdate').setValue('');
        $('inputdate_year').setValue('');
        $('inputdate_month').setValue('');
        $('inputdate_day').setValue('');
        $('inputdate_value').setValue('');
      }
      function loginAsUser(iId){
        <g:remoteFunction controller='administrators' action='loginAsHomeOwner' onSuccess='processResponse(e)' params="'id='+iId" />
      }
      function processResponse(e){
        window.open('${((context.is_dev)?"/"+context.appname+"/personal/adsoverview/":"/personal/adsoverview/")}'+e.responseJSON.id);
      }
      function updatePopdir(lCountryId){
        <g:remoteFunction controller='administrators' action='popdir_by_country_id' update='popdir_id' params="\'country_id=\'+lCountryId" />
        updateRegion(0);
      }
      function updateRegion(lPopdirId){
        <g:remoteFunction controller='administrators' action='region_by_popdir_id' update='region_id' params="\'popdir_id=\'+lPopdirId" />
      }
    </g:javascript>  
  </head>
	<body onload="initialize()">  
    <div id="homelist">
      <g:formRemote name="allForm" url="[action:'homelist']" update="[success:'companystat']">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
          <tr>
            <td colspan="2" nowrap>
              <span nowrap>
                <label for="home_id">Код:</label>
                <input type="text" id="home_id" name="id" value="${inrequest?.id}" style="width:60px">
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="linkname">Linkname:</label>
                <input type="text" id="linkname" name="linkname" value="${inrequest?.linkname}" style="width:220px">
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="user_id">Владелец:</label>
                <input type="text" id="user_id" name="user_id" value="${inrequest?.user_id}" style="width:80px">
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="client_name">Email клиента:</label>
                <input type="text" id="client_name" name="client_name" value="${inrequest?.client_name}" style="width:210px">
              </span>            
            </td>
          </tr>
          <tr>
            <td colspan="2" nowrap>
              <span nowrap>
                <label for="modstatus">Статус публикации:</label>
                <select id="modstatus" name="modstatus">
                  <option value=""></option>
                  <g:each in="${modstatus}" var="item">
                    <option value="${item.modstatus}" <g:if test="${item.modstatus==inrequest?.modstatus || (item.modstatus==1 && inrequest?.modstatus==null)}">selected="selected"</g:if>>${item.toString()}</option>
                  </g:each>
                </select>
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="is_confirmed">Статус модерации:</label>
                <select id="is_confirmed" name="is_confirmed">
                  <option value="0" <g:if test="${inrequest?.is_confirmed==0}">selected="selected"</g:if>>не рассмотрено</option>   
                  <option value="1" <g:if test="${inrequest?.is_confirmed==1}">selected="selected"</g:if>>рассмотрено</option>
                  <option value="-1" <g:if test="${inrequest?.is_confirmed==-1}">selected="selected"</g:if>>все</option>
                </select>            
              </span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <span nowrap>
                <label for="inputdate">Дата создания:</label>
                <calendar:datePicker name="inputdate" needDisable="false" dateFormat="%d-%m-%Y" value="${inrequest?.inputdate}"/>
              </span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </td>
          </tr>
          <tr>
            <td colspan="2" nowrap>
              <span nowrap>
                <label for="is_specoffer">Предложение дня:</label>
                <select name="is_specoffer" id="is_specoffer">
                  <option value="" <g:if test="${inrequest?.is_specoffer==null}">selected="selected"</g:if>>все</option>
                  <option value="0" <g:if test="${inrequest?.is_specoffer==0}">selected="selected"</g:if>>нет</option>
                  <option value="1" <g:if test="${inrequest?.is_specoffer==1}">selected="selected"</g:if>>да</option>
                </select>
              </span>
              <span nowrap>
                <label for="is_mainpage">На главной:</label>
                <select name="is_mainpage" id="is_mainpage">
                  <option value="" <g:if test="${inrequest?.is_mainpage==null}">selected="selected"</g:if>>все</option>
                  <option value="0" <g:if test="${inrequest?.is_mainpage==0}">selected="selected"</g:if>>нет</option>
                  <option value="1" <g:if test="${inrequest?.is_mainpage==1}">selected="selected"</g:if>>да</option>
                </select>
              </span>
              <span id="is_reserve" nowrap>
                <input type="checkbox" name="is_reserve" value="1"/>
                <label for="is_reserve">Бронирование</label>
              </span>
              <span id="reviews" nowrap>
                <input type="checkbox" name="ncomment" value="1"/>
                <label for="review">Наличие отзывов</label>
              </span>
              <span id="hotdiscount" nowrap>
                <input type="checkbox" name="hotdiscount" <g:if test="${inrequest?.hotdiscount}">checked</g:if> value="1"/>
                <label for="hotdiscount">Горящие скидки</label>
              </span>
              <span id="longdiscount" nowrap>
                <input type="checkbox" name="longdiscount" <g:if test="${inrequest?.longdiscount}">checked</g:if> value="1"/>
                <label for="longdiscount">Раннее бронирование</label>
              </span>
            </td>
          </tr>
          <tr>
            <td colspan="2" nowrap>
              <span nowrap>
                <label for="country_id">Страна:</label>
                <select name="country_id" id="country_id" onchange="updatePopdir(this.value)">
                  <option value=""></option>
                  <g:each in="${country}" var="item">
                    <option value="${item.id}" <g:if test="${item.id==inrequest?.country_id}">SELECTED</g:if>>${item.name}</option>
                  </g:each>
                </select>
              </span>&nbsp;&nbsp;
              <span id="popdir_span" nowrap>
                <label for="popdir_id">Направление:</label>
                <select name="popdir_id" id="popdir_id" onchange="updateRegion(this.value)" >
                  <option value="0"></option>
                  <g:each in="${popdirs}" var="item">
                    <option value="${item.id}" <g:if test="${item.id==inrequest?.popdir_id}">SELECTED</g:if>>${item.name2}</option>
                  </g:each>
                </select>			
              </span>&nbsp;&nbsp;
              <span id="region_span" nowrap>
                <label for="region_id">Регион:</label>
                <select name="region_id" id="region_id" style="width:315px">
                  <option value=""></option>
                  <g:each in="${region}" var="item">
                    <option value="${item.id}" <g:if test="${item.id==inrequest?.region_id}">SELECTED</g:if>>${item.name}</option>
                  </g:each>
                </select>				
              </span>            
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right" nowrap>
              <input type="submit" class="button-glossy green" id="user_submit_button" value="Показать" style="margin-right:10px">
              <input type="button" class="button-glossy grey" value="Сброс" onClick="resetData()"/>
            </td>
          </tr>
        </table>
      </g:formRemote>
    </div>
    <div id="companystat"></div>
    <div id="sitestat">
      <div id="placeList" align="left">        
    </div>
  </div>  
  </body>
</html>
