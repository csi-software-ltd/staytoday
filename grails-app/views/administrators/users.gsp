<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript library="jquery-1.8.3" />    
    <g:javascript library="jquery.colorbox.min" />    
    <style type="text/css">
      @media all and (-webkit-min-device-pixel-ratio:10000),not all and (-webkit-min-device-pixel-ratio:0) {
        .glossy td { font-size: 15px; }
      }
    </style>
    <g:javascript>
      var sUrl="${createLink(controller:'administrators',action:'users')}"+"/?fromModer=1";
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
      function setMain(iId,banned){	
        <g:remoteFunction controller='administrators' action='banned' onSuccess='location.assign(sUrl)' params="'id='+iId+'&banned='+banned" />
      }
      function setTel(iId,telchek){
        <g:remoteFunction controller='administrators' action='telchek' onSuccess='location.assign(sUrl)' params="'id='+iId+'&telchek='+telchek" />
      }
      function setAgent(iId,agentchek){
        <g:remoteFunction controller='administrators' action='agentchek' onSuccess='location.assign(sUrl)' params="'id='+iId+'&agentchek='+agentchek" />
      }
      function loginAsUser(iId){
        <g:remoteFunction controller='administrators' action='loginAsUser' onSuccess='processResponse(e)' params="'id='+iId" />
      }
      function changePass(iId){
        <g:remoteFunction controller='administrators' action='getUserPass' onSuccess='processResponsePass(e)' params="'id='+iId" />
      }
      function smsSend(iId){
        <g:remoteFunction controller='administrators' action='getUserforSMS' onSuccess='processResponseSMS(e)' params="'id='+iId" />
      }
      function processResponse(e){
        window.open('${((context.is_dev)?"/"+context.appname+"/personal/index":"/personal/index")}');
      }
      function changeUserPass(iId){
        $("main_id").value=iId;
        $("main_pass2").value=$("pass2form").value;
        $("main_pass").value=$("passform").value;
        $("main_pass_submit_button").click();
      }
      function sendUserSMS(iId){
        $("sms_id").value=iId;
        $("sms_stext").value=$("stext").value;
        $("sms_send_submit_button").click();
      }
      function processResponsePass(e){           
        var template ='<div id="filters_lightbox" class="new-modal">'+          
                      '  <h2 class="clearfix">Смена пароля</h2>'+
                      '  <div id="lightbox_filters" class="segment nopadding">'+
                      '    <div class="lightbox_filter_container">'+
                      '      <table width="100%" cellpadding="0" cellspacing="0" border="0">'+
                      '        <tr>'+
                      '	         <td>'+
                      '	           <div width="68" style="float:left;margin-right:15px">'+
                      '		           <div class="glossy drop_shadow" style="background:#d1d1d1;width:68px;height:68px;padding:8px">'+
                      ((e.responseJSON.smallpicture!='')?
                      '		             <img class="slideshow" width="68" height="68" alt="'+e.responseJSON.nickname+'" title="'+e.responseJSON.nickname+'"'+
                      '			             src="'+((e.responseJSON.smallpicture && !e.responseJSON.is_external)?'${imageurl}':'')+((e.responseJSON.smallpicture)?e.responseJSON.smallpicture:'')+'">'
                      :'${resource(dir:"images",file:"user-default-picture.png")}')+
                      '		           </div>'+
                      '		           <div class="user" style="width:68px">'+
                      '		             <small style="white-space:normal"><a href="${(context.is_dev)?'/'+context.appname:''}/profile/view/'+e.responseJSON.id+'">'+e.responseJSON.nickname+'</a></small><br/>'+
                      '		           </div>'+
                      '	           </div>'+
                      '          </td>'+
                      '        </tr>'+
                      '      </table><hr/>'+                      
                      '      <div id="pass_error" class="notice" style="margin:5px 0;width:95%;display:none;">'+
                      '        <span id="pass_errorText" style="font-size:12px"></span>'+
                      '      </div>'+
                      '      <table width="100%" cellpadding="5" cellspacing="0" border="0">'+                      
                      '        <tr>'+
                      '          <td nowrap>Новый пароль:</td>'+
                      '          <td><input type="password" id="passform" value="" style="width:100%"/></td>'+
                      '        </tr>'+
                      '        <tr>'+
                      '          <td nowrap>Повтор пароля:</td>'+
                      '          <td><input type="password" id="pass2form" value="" style="width:100%"/></td>'+
                      '        </tr>'+
                      '      </table>'+
                      '    </div>'+
                      '  </div>'+
                      '  <div class="segment buttons">'+
                      '    <input type="button" onclick="changeUserPass('+e.responseJSON.id+')" class="button-glossy green mini" value="Сменить"/>'+
                      '    <input type="button" onclick="jQuery.colorbox.close()" class="button-glossy grey mini" value="Отмена"/>'+
                      '  </div>'+                      
                      '</div>';                     
        jQuery.colorbox({
          html: template
        });        
			}	
      function processResponseSMS(e){           
        var template ='<div id="filters_lightbox" class="new-modal">'+                      
                      '  <div id="lightbox_filters" class="segment nopadding">'+
                      '    <div class="lightbox_filter_container">'+
                      '      <table width="100%" cellpadding="0" cellspacing="0" border="0">'+
                      '        <tr>'+
                      '	         <td>'+
                      '	           <div width="68" style="float:left;margin-right:15px">'+
                      '		           <div class="glossy drop_shadow" style="background:#d1d1d1;width:68px;height:68px;padding:8px">'+
                      ((e.responseJSON.smallpicture!='')?
                      '		             <img class="slideshow" width="68" height="68" alt="'+e.responseJSON.nickname+'" title="'+e.responseJSON.nickname+'"'+
                      '			             src="'+((e.responseJSON.smallpicture && !e.responseJSON.is_external)?'${imageurl}':'')+((e.responseJSON.smallpicture)?e.responseJSON.smallpicture:'')+'">'
                      :'${resource(dir:"images",file:"user-default-picture.png")}')+
                      '		           </div>'+
                      '		           <div class="user" style="width:68px">'+
                      '		             <small style="white-space:normal"><a href="${(context.is_dev)?'/'+context.appname:''}/profile/view/'+e.responseJSON.id+'">'+e.responseJSON.nickname+'</a></small><br/>'+
                      '		           </div>'+
                      '	           </div>'+
                      '          </td>'+
                      '	         <td>'+
                      '	           <div width="68" style="float:left;margin-right:15px">'+
                      '		           <div class="user" style="width:68px">'+
                      '		             <small style="white-space:normal"><span>'+e.responseJSON.tel+'</span></small><br/>'+
                      '		           </div>'+
                      '	           </div>'+
                      '          </td>'+
                      '        </tr>'+
                      '      </table>'+                      
                      '      <div id="sms_error" class="notice" style="margin:5px 0;width:95%;display:none;">'+
                      '        <span id="sms_errorText" style="font-size:12px"></span>'+
                      '      </div>'+
                      '      <table width="100%" cellpadding="5" cellspacing="0" border="0">'+
                      '        <tr>'+
                      '          <td colspan="4" valign="top">'+
                      '            <h2 class="toggle border"><span class="edit_icon password"></span>Текст смс</h2>'+
                      '          </td>'+
                      '        </tr>'+     
                      '        <tr colspan="4" valign="top">'+
                      '          <td><input type="text" maxlength="67" id="stext" value="" style="width:100%"/></td>'+
                      '        </tr>'+
                      '      </table>'+
                      '    </div>'+
                      '  </div>'+
                      '  <div class="segment buttons">'+
                      '    <input type="button" onclick="sendUserSMS('+e.responseJSON.id+')" class="button-glossy green mini" value="${message(code:'button.send')}"/>'+
                      '  </div>'+                      
                      '</div>';                     
        jQuery.colorbox({
          html: template
        });        
			}
      function changepassResponse(e){
        if(e.responseJSON.error){
          
          $('pass_error').show();
          $('pass_errorText').update(e.responseJSON.message);
        } else {
          alert('Пароль успешно изменен');
          location.reload(true);
        }
      }
      function sendsmsResponse(e){
        if(e.responseJSON.error){
          
          $('sms_error').show();
          $('sms_errorText').update(e.responseJSON.message);
        } else {
          alert('Смс отправлен.');
          location.reload(true);
        }
      }
      function resetDate(){
			  $('user_id').value='';
				$('name').value='';
				$('nickname').value='';
				$('provider').selectedIndex = 0;
				$('is_client').selectedIndex = 0;
				$('modstatus').selectedIndex = 0;
				$('telcheck').selectedIndex = 0;
				$('order').selectedIndex = 0;								
				$('ncomment').checked=false;				
        $('enter_date_from').value='';
        $('enter_date_to').value='';	
        $('registr_date_from').value='';
        $('registr_date_to').value='';
        $('enter_date_from_year').value='';
        $('enter_date_to_year').value='';
        $('enter_date_from_month').value='';
        $('enter_date_to_month').value='';
        $('enter_date_from_day').value='';
        $('enter_date_to_day').value='';
        $('registr_date_from_year').value='';
        $('registr_date_to_year').value='';
        $('registr_date_from_month').value='';
        $('registr_date_to_month').value='';
        $('registr_date_from_day').value='';
        $('registr_date_to_day').value='';
      }
    </g:javascript>  
  </head>  
  <body onload="\$('user_submit_button').click();">
    <div id="userlist" align="center">
      <g:formRemote name="allForm" url="[action:'userlist']" update="[success:'companystat']">
        <input type="hidden" name="stat" id="stat" value="direction">  
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:20px 5px">
          <tr>
            <td nowrap>
              <span id="user_id" nowrap>
                <label for="user_id">Код:</label>
                <input type="text" id="user_id" name="user_id" style="width:80px" value="${inrequest?.user_id?:''}">
              </span>&nbsp;&nbsp;          
              <span id="name" nowrap>
                <label for="name">Пользователь:</label>
                <input type="text" id="name" name="name" style="width:160px" value="${inrequest?.name?:''}">
              </span>
            </td>
            <td colspan="2" nowrap>
              <span id="nickname" nowrap>
                <label for="nickname">Никнейм:</label>
                <input type="text" id="nickname" name="nickname" style="width:100px" value="${inrequest?.nickname?:''}">
              </span>
              <span id="provider" nowrap>        
                <label for="provider">Провайдер:</label>
                <select name="provider" id="provider">
                  <option value=""></option> 
                  <g:each in="${provider}" var="item">
                    <option value="${item.provider}" <g:if test="${(inrequest?.provider?:'')==item.provider}">selected="selected"</g:if>>${item.provider}</option>
                  </g:each>
                </select>
              </span>&nbsp;&nbsp;          
              <span id="owner" nowrap>
                <label for="is_client">Поставщик:</label>
                <select name="is_client" id="is_client">
                  <option value="-1" <g:if test="${inrequest?.is_client==-1}">selected="selected"</g:if>></option>   
                  <option value="1" <g:if test="${inrequest?.is_client==1}">selected="selected"</g:if>>да</option>
                  <option value="0" <g:if test="${inrequest?.is_client==0}">selected="selected"</g:if>>нет</option>  
                </select>
              </span>
            </td>
          </tr>
          <tr>
            <td valign="middle" nowrap>
              <label for="registr_date_from">Дата регистрации с:</label>
              <calendar:datePicker name="registr_date_from" needDisable="false" dateFormat="%d-%m-%Y"  value="${inrequest?.registr_date_from}"/>
              <label for="registr_date_to">&nbsp;по:</label>
              <calendar:datePicker name="registr_date_to" needDisable="false" dateFormat="%d-%m-%Y"  value="${inrequest?.registr_date_to}"/>
            </td>
            <td colspan="2" valign="middle" nowrap>
              <span id="status" nowrap>
                <label for="modstatus">Cтатус аккаунта:</label>
                <select name="modstatus" id="modstatus">
                  <option value="-2" <g:if test="${inrequest?.modstatus==-2}">selected="selected"</g:if>></option>
                  <option value="0" <g:if test="${(inrequest?.modstatus?:0)==0}">selected="selected"</g:if>>неактивен</option>
                  <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>активен</option>
                  <option value="2" <g:if test="${inrequest?.modstatus==2}">selected="selected"</g:if>>неподтвержден</option>
                </select>
              </span>&nbsp;&nbsp;
              <span id="telchek_status" nowrap>
                <label for="telchek">Cтатус тел.:</label>
                <select name="telchek" id="telcheck">
                  <option value="-2" <g:if test="${inrequest?.telchek==-2}">selected="selected"</g:if>></option>
                  <option value="1" <g:if test="${inrequest?.telchek==1}">selected="selected"</g:if>>подтвержден</option>
                  <option value="0" <g:if test="${(inrequest?.telchek?:0)==0}">selected="selected"</g:if>>неподтвержден</option>
                </select>
              </span>
            </td>
          </tr>
          <tr>
            <td valign="middle" nowrap>
              <label for="enter_date_from">Дата посл. посещ. с:</label>
              <calendar:datePicker name="enter_date_from" needDisable="false" dateFormat="%d-%m-%Y"  value="${inrequest?.enter_date_from}"/>
              <label for="enter_date_to">&nbsp;по:</label>
              <calendar:datePicker name="enter_date_to" needDisable="false" dateFormat="%d-%m-%Y"  value="${inrequest?.enter_date_to}"/>
            </td>          
            <td valign="middle" nowrap>
              <span id="order">
                <label for="order">Сортировать по:</label>
                <select name="order" id="order">
                  <option value="0" <g:if test="${inrequest?.order==0}">selected="selected"</g:if>>дате регистрации</option>   
                  <option value="1" <g:if test="${inrequest?.order==1}">selected="selected"</g:if>>дате посл. посещ.</option>
                  <option value="2" <g:if test="${inrequest?.order==2}">selected="selected"</g:if>>коду пользователя</option>  
                </select>
              </span>
            </td>           
            <td>
              <span id="reviews" nowrap>              
                <input type="checkbox" id="ncomment" name="ncomment" value="1" <g:if test="${inrequest?.ncomment}">checked="checked"</g:if>/>
                <label for="review">Наличие отзывов</label>
              </span>
            </td>
          </tr>      
          <tr>
            <td colspan="3" align="right" nowrap>
              <input type="submit" class="button-glossy green" id="user_submit_button" value="Показать" style="margin-right:5px">        
              <input type="reset" class="button-glossy grey" value="Сброс" onClick="resetDate();return true;"/>
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
    <g:formRemote name="changepassForm" url="[controller: 'administrators', action: 'changeUserPass']" onSuccess="changepassResponse(e)">      
      <input type="hidden" id="main_id" name="id" value="0">
      <input type="hidden" id="main_pass2" name="pass2" value="">
      <input type="hidden" id="main_pass" name="pass" value="">
      <input type="submit" id="main_pass_submit_button" style="display:none" value="">
    </g:formRemote>
    <g:formRemote name="smsSendForm" url="[controller: 'administrators', action: 'sendUserSMS']" onSuccess="sendsmsResponse(e)">      
      <input type="hidden" id="sms_id" name="id" value="0">
      <input type="hidden" id="sms_stext" name="stext" value="">
      <input type="submit" id="sms_send_submit_button" style="display:none" value="">
    </g:formRemote>
  </body>
</html>
