<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />    
    <meta name="layout" content="main" />
    <g:javascript>
    function setNickname_reg(){
      if($('firstname').value.length && $('lastname').value.length)
        $('nickname').value=$('firstname').value+' '+$('lastname').value;
    }
    function doLoginReg() {
      VK.Auth.login(authInfoReg,2);
    }
    function authInfoReg(response) {
      if (response.session) {
        VK.api('users.get',{uids:response.session.mid, fields:'uid,first_name,last_name,photo_rec,photo_big'}, function(data) {
          $("reg_vk_id").value=data.response[0].uid;
          $("reg_vk_name").value=data.response[0].first_name+' '+data.response[0].last_name;
          $("reg_vk_pic").value=data.response[0].photo_rec;
          $("reg_vk_photo").value=data.response[0].photo_big;
          $("reg_vk_hash").value='';
          $("saveUserForm").submit();
        });
      }
    }    
    function facebook_ah(response) {
      if (response.authResponse) {
        $("fb_auth").value = '1';
        FB.api('/me', function(response) {
          $("reg_fb_id").value=response.id;
          $("reg_fb_name").value=response.name;
          FB.api('/me/picture', function(response) {
            $("reg_fb_pic").value=response;
            FB.api('/me/picture?type=large', function(response) {
              $("reg_fb_photo").value=response;
              fbLoginClick_ah();
            });
          });
        });
      }
    }
    function fbLoginClick_ah() {
      $("saveUserForm").submit();
    }
    function doFBlogin_ah() {
      FB.login(facebook_ah);
    }
    </g:javascript>  
  </head>
  <body>
            <tr style="height:140px">
              <td width="250" class="search ss">                
                <g:link class="button" uri="/cities">${message(code:'common.renthome')}</g:link>
              </td>
              <td colspan="3" class="rent s">
                <h1>${infotext['header'+context?.lang]?:''}</h1>
              </td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td colspan="3" valign="top">
              <g:if test="${flash?.error}">
                <div class="notice" style="margin:0 0 10px 0">
                  <ul>
                    <g:if test="${(flash?.error?:[]).contains(1)}"><li>${message(code:"error.blank",args:["Email"])}</li></g:if>
                    <g:if test="${(flash?.error?:[]).contains(2)}"><li>${message(code:"error.incorrect",args:["Email"])}</li></g:if>
                    <g:if test="${(flash?.error?:[]).contains(3)}"><li>${message(code:'error.blank',args:[message(code:'common.nickname').capitalize()])}</li></g:if>
                    <g:if test="${(flash?.error?:[]).contains(4)}"><li>${message(code:"login.error.match")}</li></g:if>
                    <g:if test="${(flash?.error?:[]).contains(5)}"><li>${message(code:"login.error.notenough")}</li></g:if>
                    <g:if test="${(flash?.error?:[]).contains(6)}"><li>${message(code:"login.error.notunique",args:["Email"])} ${message(code:"register.or")} <g:link controller="user" action="index" params="${[control:'user', act:'addnew',code:inrequest?.code?:'',home_id:inrequest?.home_id?:0]}" rel="nofollow">${message(code:'login.auth')}</g:link></li></g:if>
                    <g:if test="${(flash?.error?:[]).contains(10)}"><li>${message(code:"add.error.need")} ${message(code:"add.error.login.social")}, ${message(code:"register.or")} ${mesage(code:"add.error.change.email")}</li></g:if>
                    <g:if test="${(flash?.error?:[]).contains(101)}"><li>${message(code:'login.error.bderror')}</li></g:if>
                    <g:if test="${(flash?.error?:[]).contains(-100)}"><li>${message(code:"login.error.sendmessage")}</li></g:if> 
                  </ul>
                </div>
              </g:if>             
                <div class="form shadow paddtop padd20">
                  <h2 class="padd20">${message(code:'register.account')}</h2>                
                  <g:form name="saveUserForm" url="[controller:'user',action:'saveuser']" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                    <table width="100%" cellpadding="3" cellspacing="0" border="0">
                    <g:if test="${!(inrequest?.passw?:0)}">  
                      <tr>
                        <td colspan="2">
                          <label for="firstname">${message(code:'register.name')}</label>
                          <input type="text" id="firstname" name="firstname" maxlength="${stringlimit}" value="${inrequest?.firstname?:''}"/>
                        </td>        
                      </tr>
                      <tr>
                        <td colspan="2">
                          <label for="lastname">${message(code:'register.surname')}</label>
                          <input type="text" id="lastname" name="lastname" maxlength="${stringlimit}" value="${inrequest?.lastname?:''}"/>
                        </td>        
                      </tr>
                      <tr>
                        <td colspan="2">
                          <label for="nickname">${message(code:'register.nickname')}</label>
                          <input type="text" id="nickname" name="nickname" maxlength="${stringlimit}" value="${inrequest?.nickname?:''}" onfocus="setNickname_reg()" <g:if test="${(flash?.error?:[]).contains(3)}">class="red"</g:if>/>
                        </td>        
                      </tr>	  
                      <g:if test="${!user||(user.provider!='staytoday')}">	  
                      <tr>
                        <td colspan="2">
                          <label for="email">email</label>
                          <input type="text" id="email" name="email" maxlength="${stringlimit}" value="${inrequest?.email?:''}" <g:if test="${(flash?.error?:[]).contains(1)||(flash?.error?:[]).contains(2)||(flash?.error?:[]).contains(6)}">class="red"</g:if>/>
                        </td>        
                      </tr>
                      </g:if>
                    </g:if><g:else>
                      <tr>      
                        <td colspan="2"><input type="hidden" name="passw" value="1"/></td>      
                      </tr>	
                    </g:else>	
                      <tr>
                        <td colspan="2">
                          <label for="password1">${message(code:'register.password')}</label>
                          <input type="password" name="password1" value="" <g:if test="${(flash?.error?:[]).contains(5)}">class="red"</g:if>/>
                        </td>        
                      </tr>
                      <tr>
                        <td colspan="2">
                          <label for="password2">${message(code:'register.password.confirm')}</label>
                          <input type="password" name="password2" value="" <g:if test="${(flash?.error?:[]).contains(4)}">class="red"</g:if>/>
                        </td>
                      </tr>
                      <tr>
                        <td width="130" style="padding-left:207px"><input type="submit" class="button-glossy orange" value="${message(code:'button.send')}"/></td>
                        <td>
                          <b><g:link controller="user" action="restore" rel="nofollow">${message(code:'login.restore')}?</g:link></b>
                        </td>
                      </tr>	
                    </table>
                    <input type="hidden" id="code" name="code" value="${inrequest?.code?:''}"/>
                    <input type="hidden" id="home_id" name="home_id" value="${inrequest?.home_id?:0}"/>
                    <input type="hidden" id="reg_vk_id" name="vk_id" value="0"/>
                    <input type="hidden" id="reg_vk_name" name="vk_name" value=""/>
                    <input type="hidden" id="reg_vk_pic" name="vk_pic" value=""/>
                    <input type="hidden" id="reg_vk_photo" name="vk_photo" value=""/>
                    <input type="hidden" id="reg_vk_hash" name="vk_hash" value=""/>
                    <input type="hidden" id="reg_fb_id" name="m_fb_id" value="0">
                    <input type="hidden" id="reg_fb_name" name="fb_name" value="">
                    <input type="hidden" id="reg_fb_pic" name="fb_pic" value="">
                    <input type="hidden" id="reg_fb_photo" name="fb_photo" value="">
                    <input type="hidden" id="fb_auth" name="fb_auth" value="0"/>
                  </g:form><br/><br/>            
                  <table width="97%" cellpadding="6" cellspacing="0" border="0">
                    <tr>
                      <td colspan="2" class="dashed"><i style="background:#fff;padding:10px;margin-left:45%" class="red">${message(code:'register.or')}</i></td>
                    </tr>
                    <tr>
                      <td colspan="2"><br/><h2 class="padd20">${message(code:'login.social')}</h2></td>
                    </tr>
                    <tr>
                      <td width="130" style="padding-left:200px">                      
                        <div id="fb_auth" class="fb-button" style="float:right">
                          <a href="javascript:void(0)" rel="nofollow" onclick="doFBlogin_ah();">
                            <span class="fb-button-left"></span>
                            <span class="fb-button-center"><b>Facebook</b></span>
                            <span class="fb-button-right"></span>                            
                          </a>
                        </div>
                      </td>
                      <td>                      
                        <div class="vk-button">
                          <a id="reg_vk_auth" href="javascript:void(0)" rel="nofollow" onclick="doLoginReg()">
                            <span class="vk-button-left"></span>
                            <span class="vk-button-center"></span>
                            <span class="vk-button-right"></span>
                          </a>                        
                        </div>
                      </td>
                    </tr>
                  </table>
                  <script type="text/javascript">
                    VK.init({ apiId: ${vk_api_key} });
                    VK.UI.button('reg_vk_auth');
                    FB.init({
                      appId: ${fb_api_key},
                      status: true,
                      cookie: true,
                      xfbml: true,
                      oauth: true
                    });
                  </script> 
                </div>
              </td>
            </tr>  
  </body>
</html>
