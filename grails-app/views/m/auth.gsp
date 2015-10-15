<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />
    <script type="text/javascript" src="//connect.facebook.net/${context?.lang?'en_US':'ru_RU'}/all.js"></script>  
    <script type="text/javascript">
      FB.init({ appId: ${fb_api_key}, status: true, cookie: true, xfbml: true, oauth: true });
      function processLogResponse(e){            
        if(e.responseJSON.error){
          jQuery.mobile.hidePageLoadingMsg();          
          var sErrorMsg='';          
          e.responseJSON.error.forEach(function(err){
            switch (err) {             
              case 1: sErrorMsg+='${message(code:"error.blank",args:[message(code:"login.user").capitalize()])}<br/>'; break;            
              case 2: sErrorMsg+='${message(code:"login.error.fail")}<br/>'; break;
              case 3: sErrorMsg+='${message(code:"login.error.email")} <a href="${createLink(uri:'/reg',base:context?.mobileURL_lang)}">${message(code:"login.error.email.register")}</a><br/>'; break;
              case 4: sErrorMsg+='${message(code:"error.blank",args:[message(code:"login.password").capitalize()])}<br/>'; break;                          
            }
          });
          jQuery('#login_fail').html(sErrorMsg);
          $('login_fail').show();  
        }else{
          $('login_fail').hide();
          <g:if test="${from!=''}">
            location.assign(document.referrer);
          </g:if><g:else>
            location.assign("${createLink(uri:'',base:context?.mobileURL_lang)}");
          </g:else>
        }
      }
      function facebook(response){
        if(response.authResponse){
          jQuery.mobile.showPageLoadingMsg('b','${message(code:"spinner.message.mobile")}',false);
          FB.api('/me',function(response){
            $('fb_id').value=response.id;
            $('fb_name').value=response.name;
            $('fb_email').value=response.email;
            FB.api('/me/picture',function(response){
              $('fb_pic').value=response.data.url;
              FB.api('/me/picture?type=large',function(response){
                $('fb_photo').value=response.data.url;
                $('FacebookloginForm').submit();                
              });
            });
          });
        }
      }
      function doFBlogin() {
        FB.getLoginStatus(function(response){
          if(response.status === 'connected')
            facebook(response);
          else
            FB.login(facebook,{scope:'email'});          
        });
      }
    </script>
  </head>
  <body>
      <h1 role="heading">${infotext['header'+context?.lang]?:''}</h1>      
      <div data-role="content" style="padding:8px">
        <g:formRemote name="loginForm" url="[action:'login']" onLoading="jQuery.mobile.showPageLoadingMsg('b','${message(code:'spinner.message.mobile')}',false)" onSuccess="processLogResponse(e)">
          <div class="notice" id="login_fail" style="display:none">            
          </div>
          <div class="ui-grid-a" style="padding:8px 4px">
            <div class="ui-block-a"><b>${message(code:'login.enter').capitalize()}:</b></div>
            <div class="ui-block-b" align="right"><b><a href="${createLink(uri:'/reg',base:context?.mobileURL_lang)}">${message(code:'label.signup').toLowerCase()}</a></b></div>
          </div>
          <div class="ui-body ui-body-b ui-corner-all" style="padding:8px">        
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="user">${message(code:'login.user')}</label>
              <input type="email" name="user" id="user" placeholder="${message(code:'login.user')}" />
            </div>
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="password">${message(code:'login.password')}</label>
              <input type="password" name="password" id="password" placeholder="${message(code:'login.password')}" />
            </div>              
          </div>                
          <div class="ui-grid-a">
            <div class="ui-block-a">
              <input type="submit" data-theme="f" value="${message(code:'label.login')}" />
            </div>
            <div class="ui-block-b" id="passrestore2" style="padding:15px 4px" align="right">
              <b><a href="${createLink(uri:'/restore',base:context?.mobileURL_lang)}">${message(code:'login.restore')}?</a></b>
            </div>                                          
          </div>          
          <div class="ui-grid-a" style="padding:8px 4px">
            <b>${message(code:'login.social.mobile')}:</b>            
          </div>          
          <div class="ui-grid-a">
            <div class="ui-block-a">
              <a id="fb_auth" data-role="button" data-theme="fb" data-icon="fb-icon" href="#" onclick="doFBlogin()">Facebook</a>
            </div>
            <div class="ui-block-b">&nbsp;</div>
          </div>                    
          <input type="hidden" name="user_index" value="1" />
          <input type="hidden" name="act" value="${from?:''}" />
        </g:formRemote>
        <g:formRemote name="FacebookloginForm" id="FacebookloginForm" url="[action:'facebook']" onSuccess="processLogResponse(e)">      
          <input type="hidden" id="fb_id" name="m_fb_id" value="0" />
          <input type="hidden" id="fb_name" name="fb_name" value="" />
          <input type="hidden" id="fb_pic" name="fb_pic" value="" />
          <input type="hidden" id="fb_photo" name="fb_photo" value="" />
          <input type="hidden" id="fb_email" name="fb_email" value="" />
          <input type="hidden" id="fb_ajax" name="is_ajax" value="1" />          
          <input type="hidden" id="iService_fb" name="service" value="0" />
        </g:formRemote>        
      </div>
      <div data-role="footer" data-position="fixed" data-theme="a">
        <div data-role="navbar" role="navigation">
          <ul>
            <li><a href="${createLink(uri:'/about',base:context?.mobileURL_lang)}">${Infotext.findByControllerAndAction('index','about')['name'+context?.lang]}</a></li>  
            <li><a href="${createLink(uri:'/terms',base:context?.mobileURL_lang)}">${Infotext.findByControllerAndAction('index','terms')['name'+context?.lang]}</a></li>
            <li><a href="${createLink(uri:'/help',base:context?.mobileURL_lang)}">${Infotext.findByControllerAndAction('help','faq')['name'+context?.lang]}</a></li>
          </ul>
        </div>
      </div>      
  </body>
</html>
