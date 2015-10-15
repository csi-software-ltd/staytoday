<g:javascript>
    function blurInput(){
      var lForm = document.getElementById('mainloginForm');
      if (lForm){
        if(lForm.user.value=='')
          lForm.user.value="${message(code:'login.user')}";
        if(lForm.password.value=='')
          lForm.password.value="${message(code:'login.password')}";        
      }
    }
    function focusLogin(){
      var lForm = document.getElementById('mainloginForm');
      if(lForm.user.value=="${message(code:'login.user')}"){
        lForm.user.style.color='black';
        lForm.user.value='';
      }
      if(lForm.password.value==''){
        lForm.password.style.color='gray';    
        lForm.password.value="${message(code:'login.password')}";    
      }
    }
    function focusPassword(){
      var lForm = document.getElementById('mainloginForm');
      if(lForm.user.value==''){
        lForm.user.style.color='gray';
        lForm.user.value="${message(code:'login.user')}";
      }
      if(lForm.password.value=="${message(code:'login.password')}"){
        lForm.password.style.color='black';
        lForm.password.value='';
      }
    }
    function reloadCaptcha(){
      <g:remoteFunction controller='index' action='reloadCaptcha' onSuccess='processRelResponse(e)' />
    }
    function processRelResponse(e){
      $('captcha_picture').innerHTML = e.responseJSON.captcha;
      $('captcha_picture').firstChild.setStyle({width: '120px'});
    }
    function processLogResponse(e){      
      if(e.responseJSON.error){
        $('loader_login').hide();
        $('captcha_text').removeClassName('red');
        var sErrorMsg='';
        $('captcha_picture').innerHTML = e.responseJSON.captcha;
        $('captcha_picture').firstChild.setStyle({width: '120px'});
        var aErr=e.responseJSON.errorprop;
        if (e.responseJSON.user_enter_fail>e.responseJSON.user_max_enter_fail) {
          $('captcha_label').show();
          $('captcha_log').show();          
        } else {
          $('captcha_label').hide();
          $('captcha_log').hide();
        }
        if(aErr.indexOf(1)>-1){
          sErrorMsg+="${message(code:'login.error.fail')}<br />";
        }
        if(aErr.indexOf(3)>-1){
          sErrorMsg+='${message(code:"login.error.email")} <a rel="nofollow" onclick="showLoginForm(1,\'reg_lbox\',this);$(\'login_lbox\').hide()">${message(code:"login.error.email.register")}</a><br />';
          document.getElementById('reg_email').value=document.getElementById('user').value
        }
        if(aErr.indexOf(2)>-1){
          sErrorMsg+='${message(code:"add.error.incorrect.code")}<br />';
          $('captcha_text').addClassName('red');          
        }
        $('main_login_fail').innerHTML = sErrorMsg;
        $('main_login_fail').show();
        
        jQuery('#login_lbox_container').css('height','auto');
        jQuery.colorbox.resize();

      }else{
        var setframes = ''
        $('main_login_fail').hide();
        if(e.responseJSON.service==2){
          setframes = location.href.split('?')[0] + "?service="+e.responseJSON.service;
          if($('mtext')) setframes += '&mtext='+$F('mtext');
          if($('mail_date_begin')&&$('mail_date_begin_value').value) setframes += '&date_start='+$F('mail_date_begin_value').split('-')[2]+'-'+$F('mail_date_begin_value').split('-')[1]+'-'+$F('mail_date_begin_value').split('-')[0];
          if($('mail_date_end')&&$('mail_date_end_value').value) setframes += '&date_end='+$F('mail_date_end_value').split('-')[2]+'-'+$F('mail_date_end_value').split('-')[1]+'-'+$F('mail_date_end_value').split('-')[0];
          if($('mail_homeperson_id')) setframes += '&homeperson_id='+$F('mail_homeperson_id');
          location.assign(setframes);
        } else if(e.responseJSON.service==1){
          setframes = location.href.split('?')[0] + "?" + ((location.href.split('?')[1])?location.href.split('?')[1].replace(/&service=2/g,'').replace(/&service=1/g,'').replace(/service=2/g,'').replace(/service=1/g,'')+'&':"") + "service="+e.responseJSON.service;
          location.assign(setframes);
        } else if(e.responseJSON.service==3){
          window.location = "${((context?.is_dev)?"/"+context?.appname+"/trip/waiting":"/trip/waiting")}";
        } else {
          location.reload(true);
        }
      }
    }
    function processRegResponse(e){
      var sErrorMsg='';      
      $('reg_email').removeClassName('red');
      $('reg_nickname').removeClassName('red');
      $('reg_password1').removeClassName('red');
      $('reg_password2').removeClassName('red');
      
      if(e.responseJSON.error>0){
        $('loader_login').hide();
        e.responseJSON.errorprop.forEach(function(err){
          switch (err) {             
            case 1: sErrorMsg+='<li>${message(code:"error.blank",args:["Email"])}</li>'; $("reg_email").addClassName('red'); break;
            case 2: sErrorMsg+='<li>${message(code:"error.incorrect",args:["Email"])}</li>'; $('reg_email').addClassName('red'); break;
            case 3: sErrorMsg+='<li>${message(code:"error.blank",args:[message(code:'register.nickname')])}</li>'; $('reg_nickname').addClassName('red'); break;
            case 4: sErrorMsg+='<li>${message(code:"login.error.match")}</li>'; $('reg_password2').addClassName('red'); break;
            case 5: sErrorMsg+='<li>${message(code:"login.error.notenough")}</li>'; $('reg_password1').addClassName('red'); break;
            case 6: sErrorMsg+='<li>${message(code:"login.error.notunique",args:["Email"])}</li>'; $('reg_email').addClassName('red'); break;
            case 9: sErrorMsg+='<li>${message(code:"login.error.notunique",args:["Email"])}</li>'; $('reg_email').addClassName('red'); break;
            case -100: sErrorMsg+='<li>${message(code:"login.error.sendmessage")}</li>'; break;
            case 101: sErrorMsg+='<li>${message(code:"login.error.bderror")}</li>'; break;
          } 
        });
        if(sErrorMsg.length){
          $('main_reg_fail').innerHTML=sErrorMsg;
          $('main_reg_fail').up('div').show();
        }
        jQuery('#reg_lbox_container').css('height','auto');
        jQuery.colorbox.resize(); 	
        
      } else {
        var setframes = ''
        if(e.responseJSON.service==2){
          setframes = location.href.split('?')[0] + "?service="+e.responseJSON.service;
          if($('mtext')) setframes += '&mtext='+$F('mtext');
          if($('mail_date_begin')&&$('mail_date_begin_value').value) setframes += '&date_start='+$F('mail_date_begin_value').split('-')[2]+'-'+$F('mail_date_begin_value').split('-')[1]+'-'+$F('mail_date_begin_value').split('-')[0];
          if($('mail_date_end')&&$('mail_date_end_value').value) setframes += '&date_end='+$F('mail_date_end_value').split('-')[2]+'-'+$F('mail_date_end_value').split('-')[1]+'-'+$F('mail_date_end_value').split('-')[0];
          if($('mail_homeperson_id')) setframes += '&homeperson_id='+$F('mail_homeperson_id');
          location.assign(setframes);
        } else if(e.responseJSON.service==1){
          setframes = location.href.split('?')[0] + "?" + ((location.href.split('?')[1])?location.href.split('?')[1].replace(/&service=2/g,'').replace(/&service=1/g,'').replace(/service=2/g,'').replace(/service=1/g,'')+'&':"") + "service="+e.responseJSON.service;
          location.assign(setframes);
        } else if(e.responseJSON.service==3){
          window.location = "${((context?.is_dev)?"/"+context?.appname+"/trip/waiting":"/trip/waiting")}";
        } else {
          location.reload(true);
        }
      }
    }
    function doLogin() {
      VK.Auth.login(authInfo,2);
    }
    function authInfo(response) {
      if (response.session) {
        $('loader_login').show();
        VK.api('users.get',{uids:response.session.mid, fields:'uid,first_name,last_name,photo_rec,photo_big'}, function(data) {
          $("main_vk_id").value=data.response[0].uid;
          $("main_vk_name").value=data.response[0].first_name+' '+data.response[0].last_name;
          $("main_vk_pic").value=data.response[0].photo_rec;
          $("main_vk_photo").value=data.response[0].photo_big;
          $("main_vk_hash").value='';
          $("main_Vk_submit_button").click();
        });
      }
    }
    function setNickname(){
      if($('reg_firstname').value.length && $('reg_lastname').value.length)
        $('reg_nickname').value=$('reg_firstname').value+' '+$('reg_lastname').value;
    }
    function facebook(response) {
      if (response.authResponse) {
        $('loader_login').show();
        FB.api('/me', function(response) {
          $("main_fb_id").value=response.id;
          $("main_fb_name").value=response.name;
          $("main_fb_email").value=response.email;
          FB.api('/me/picture', function(response) {
            $("main_fb_pic").value=response.data.url;
            FB.api('/me/picture?type=large', function(response) {
              $("main_fb_photo").value=response.data.url;
              fbLoginClick();
            });
          });
        });
      }
    }
    function fbLoginClick() {
      $("main_Fb_submit_button").click();
    }
    function doFBlogin() {
      FB.getLoginStatus(function(response) {
        if (response.status === 'connected') {
          facebook(response);
        } else {
          FB.login(facebook,{scope:'email'});
        }
      });
    }
</g:javascript>
          <div id="login_lbox" class="new-modal" style="display:none">
            <h2 class="clearfix">${message(code:'label.login')}</h2>
            <div id="login_lbox_segment" class="segment nopadding">
              <div id="login_lbox_container" class="lightbox_filter_container">
                <g:rawHtml>${(controllerName=='home' && actionName=='detail' && infotext['itext'+context?.lang])?infotext['itext'+context?.lang]:''}</g:rawHtml>              
                <div class="notice" id="main_login_fail" style="display:none">
                  ${message(code:'login.error.fail')}
                </div>
                <g:formRemote id="mainloginForm" onLoading="\$('loader_login').show();" name="mainloginForm" method="post" url="[controller: 'user', action: 'login']" onSuccess="processLogResponse(e)">                                                        
                  <table class="form s" width="100%" cellpadding="5" cellspacing="0" border="0">
                    <tr>
                      <td colspan="2" style="padding-bottom:20px">
                        <b style="float:left">${message(code:'login.account')}:</b>
                        <b style="float:right">
                          <a rel="nofollow" onclick="showLoginForm(1,'reg_lbox',this);$('login_lbox').hide()">${message(code:'label.signup')}</a>
                        </b>
                      </td>
                    </tr>
                    <tr>
                      <td><label for="user">${message(code:'login.user')}</label></td>
                      <td><input type="text" onblur="blurInput();" onfocus="focusLogin();" value="${message(code:'login.user')}" name="user" id="user" /></td>
                    </tr>
                    <tr>
                      <td><label for="password">${message(code:'login.password')}</label></td>
                      <td><input type="password" onblur="blurInput();" onfocus="focusPassword();" value="${message(code:'login.password')}" name="password" id="password" /></td>
                    </tr>          
                    <tr>
                      <td><b style="color:#FF530D"><input type="checkbox" value="1" name="remember" />&nbsp;${message(code:'login.remember')}</b></td>
                      <td>
                        <input type="submit" class="button-glossy orange" value="${message(code:'label.login')}" />
                        <span id="passrestore2" style="margin-left:20px">
                          <b><g:link controller="user" action="restore" rel="nofollow" base="${context?.mainserverURL_lang}">${message(code:'login.restore')}?</g:link></b>
                        </span>
                      </td>
                    </tr>  
                    <tr id="captcha_label" style="height:20px;${(session.user_enter_fail?:0)>user_max_enter_fail?'':'display:none'}">
                      <td colspan="2">${message(code:'login.captcha')}:</td>             
                    </tr>
                    <tr id="captcha_log" style="${(session.user_enter_fail?:0)>user_max_enter_fail?'height:50px;':'display:none;'}">
                      <td width="100" valign="middle" id="captcha_picture"><jcaptcha:jpeg name="image" width="120" alt=""/></td>
                      <td valign="middle">                        
                        <input id="captcha_text" type="text" name="captcha" value="" style="width:150px" />
                        <img src="${resource(dir:'images',file:'reload.png')}" onclick="reloadCaptcha();" alt="${message(code:'captcha.update')}" border="0" align="middle" style="margin-left:15px"/>
                      </td>
                    </tr>
                  </table>
                  <input type="hidden" value="staytoday" name="provider" />
                  <input type="hidden" id="log_is_ajax" name="is_ajax" value="1" />
                  <input type="hidden" name="user_index" value="1" />
                  <input type="hidden" id="iService_log" name="service" value="0" />
                </g:formRemote>
              </div>                
            </div>
            <div class="segment buttons">   
              <table width="100%" cellpadding="5" cellspacing="0" border="0">
                <tr>
                  <td colspan="2" style="padding-bottom:20px"><b>${message(code:'login.social')}:</b></td>
                </tr>
                <tr>
                  <td valign="top" align="right">                      
                    <div id="fb_auth" class="fb-button">
                      <a rel="nofollow" onclick="doFBlogin();">
                        <span class="fb-button-left"></span>
                        <span class="fb-button-center"><b>Facebook</b></span>
                        <span class="fb-button-right"></span>                            
                      </a>
                    </div>
                  </td>
                  <td width="150" align="left" valign="top">                      
                    <div id="main_vk_auth" class="vk-button">
                      <a rel="nofollow" onclick="doLogin()">
                        <span class="vk-button-left"></span>
                        <span class="vk-button-center"></span>
                        <span class="vk-button-right"></span>
                      </a>                        
                    </div>
                  </td>
                </tr>
              </table>
            </div>
          </div>
            
          <div id="reg_lbox" class="new-modal" style="width:520px;display:none">
            <h2 class="clearfix">${message(code:'register.site')}</h2>
            <g:formRemote name="saveUserForm" onLoading="\$('loader_login').show();" url="[controller:'user',action:'saveuser']" method="post" onSuccess="processRegResponse(e)">
            <div id="reg_lbox_segment" class="segment nopadding">
              <div id="reg_lbox_container" class="lightbox_filter_container" style="height:auto">
                <div class="notice" style="margin:0 0 5px 0;display:none">
                  <ul id="main_reg_fail">
                    <li></li>
                  </ul>
                </div>                
                <table class="form s" width="100%" cellpadding="5" cellspacing="0" border="0">
                  <tr>
                    <td colspan="2" style="padding-bottom:20px">
                      <b style="float:left">${message(code:'register.account')}:</b>
                      <b style="float:right">
                        <a rel="nofollow" onclick="showLoginForm(1,'login_lbox',this);$('reg_lbox').hide()">${message(code:'label.login')}</a>
                      </b>
                    </td>
                  </tr>
                  <tr>
                    <td><label for="reg_firstname">${message(code:'register.name')}</label></td>
                    <td><input type="text" id="reg_firstname" name="firstname" maxlength="${stringlimit}" value="" /></td>        
                  </tr>
                  <tr>
                    <td><label for="reg_lastname">${message(code:'register.surname')}</label></td>
                    <td><input type="text" id="reg_lastname" name="lastname" maxlength="${stringlimit}" value="" /></td>        
                  </tr>
                  <tr>
                    <td><label for="reg_nickname">${message(code:'register.nickname')}</label></td>
                    <td><input type="text" id="reg_nickname" name="nickname" maxlength="${stringlimit}" value="" onfocus="setNickname()" /></td>        
                  </tr>
                  <tr>
                    <td><label for="reg_email">email</label></td>
                    <td><input type="text" id="reg_email" name="email" maxlength="${stringlimit}" value="" /></td>        
                  </tr>
                  <tr>
                    <td><label for="reg_password1">${message(code:'register.password')}</label></td>
                    <td><input type="password" id="reg_password1" name="password1" value="" /></td>        
                  </tr>
                  <tr>
                    <td nowrap="nowrap"><label for="reg_password2">${message(code:'register.password.confirm')}</label></td>
                    <td><input type="password" id="reg_password2" name="password2" value="" /></td>
                  </tr>
                </table>                 
              </div>
            </div>
            <div class="segment buttons">                  
              <input id="sendmail" type="submit" class="button-glossy orange mini" value="${message(code:'button.send')}" />                  
            </div>
            <input type="hidden" id="reg_is_ajax" name="is_ajax" value="1" />
            <input type="hidden" id="iService_reg" name="service" value="0" />
          </g:formRemote>
          </div>                        
          <div id="loader_login" class="spinner" style="z-index:99999;top:380px;left:650px;display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.message')}" align="middle" hspace="5" border="0" />
          </div>

      <g:formRemote name="mainFacebookloginForm" url="[controller:'user',action:'facebook']" onSuccess="processLogResponse(e)">      
        <input type="hidden" id="main_fb_id" name="m_fb_id" value="0" />
        <input type="hidden" id="main_fb_name" name="fb_name" value="" />
        <input type="hidden" id="main_fb_pic" name="fb_pic" value="" />
        <input type="hidden" id="main_fb_photo" name="fb_photo" value="" />
        <input type="hidden" id="main_fb_email" name="fb_email" value="" />
        <input type="hidden" id="main_fb_ajax" name="is_ajax" value="1" />	
        <input type="submit" id="main_Fb_submit_button" style="display:none" value="" />
        <input type="hidden" id="iService_fb" name="service" value="0" />
      </g:formRemote>
      <g:formRemote name="mainVkloginForms" url="[controller:'user',action:'vk']" onSuccess="processLogResponse(e)">
        <input type="hidden" id="main_vk_id" name="vk_id" value="0" />
        <input type="hidden" id="main_vk_name" name="vk_name" value="" />
        <input type="hidden" id="main_vk_pic" name="vk_pic" value="" />
        <input type="hidden" id="main_vk_photo" name="vk_photo" value="" />
        <input type="hidden" id="main_vk_hash" name="vk_hash" value="" />
        <input type="hidden" id="main_vk_ajax" name="is_ajax" value="1" />
        <input type="submit" id="main_Vk_submit_button" style="display:none" value="" />
        <input type="hidden" id="iService_vk" name="service" value="0" />
      </g:formRemote>
