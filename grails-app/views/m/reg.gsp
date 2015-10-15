<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />
    <g:javascript>      
      function processRegResponse(e){    
        var sErrorMsg='';        
        jQuery('#reg_email,#reg_nickname,#reg_password1,#reg_password2').removeClass('red');
        if(e.responseJSON.error>0){
          jQuery.mobile.hidePageLoadingMsg();
          e.responseJSON.errorprop.forEach(function(err){
            switch(err){
              case 1: {sErrorMsg+='${message(code:"error.blank",args:["Email"])}<br/>'; $('reg_email').addClassName('red'); break;}
              case 2: {sErrorMsg+='${message(code:"error.incorrect",args:["Email"])}<br/>'; $('reg_email').addClassName('red'); break;}                   
              case 3: {sErrorMsg+='${message(code:"error.blank",args:[message(code:"register.nickname").capitalize()])}<br/>'; $('reg_nickname').addClassName('red'); break;}
              case 4: {sErrorMsg+='${message(code:"login.error.match")}<br/>'; $('reg_password2').addClassName('red'); break;}
              case 5: {sErrorMsg+='${message(code:"login.error.notenough")}<br/>'; $('reg_password1').addClassName('red'); break;}
              case 6: {sErrorMsg+='${message(code:"login.error.notunique",args:["Email"])}<br/>'; $('reg_email').addClassName('red'); break;}
              case 9: {sErrorMsg+='${message(code:"login.error.notunique",args:["Email"])}<br/>'; $('reg_email').addClassName('red'); break;}
              case -100:{sErrorMsg+='${message(code:"login.error.sendmessage")}<br/>'; break;}
              case 101: {sErrorMsg+='${message(code:"login.error.bderror")}<br/>'; break;}
              case 102: {sErrorMsg+='${message(code:"register.error.mobile")}<br/>'; break;}
            }
          });
          if(sErrorMsg.length){
            jQuery('#reg_fail').html(sErrorMsg);
            $('reg_fail').show();
          }      
        }/*else {      
          if(e.responseJSON.user){          
            $('#user').val($('#reg_email').val());      
            $('#password').val($('#reg_password1').val());      
            setUser();      
          }
        }*/
      }
      function setNickname(){
        if($('reg_firstname').value && $('reg_lastname').value)
          $('reg_nickname').value=$('reg_firstname').value+' '+$('reg_lastname').value;
      }
    </g:javascript>
  </head>
  <body>
      <h1 role="heading">${infotext['header'+context?.lang]?:''}</h1>
      <div data-role="content" style="padding:8px">
        <g:formRemote name="saveUserForm" url="[action:'saveuser']" method="post" onLoading="jQuery.mobile.showPageLoadingMsg('b','${message(code:'spinner.message.mobile')}',false)" onSuccess="processRegResponse(e)">        
          <div class="notice" id="reg_fail" style="display:none"></div>
          <div class="ui-grid-a" style="padding:8px 4px">
            <div class="ui-block-a"><b>${message(code:'register.account.mobile')}:</b></div>
            <div class="ui-block-b" align="right"><b><a href="${createLink(uri:'/auth?from='+actionName,base:context?.mobileURL_lang)}">${message(code:'label.login').toLowerCase()}</a></b></div>
          </div>
          <div class="ui-body ui-body-b ui-corner-all" style="padding:8px">        
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="reg_firstname">${message(code:'register.name')}</label>
              <input type="text" id="reg_firstname" name="firstname" placeholder="${message(code:'register.name')}" maxlength="200" value=""/>
            </div>
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="reg_lastname">${message(code:'register.surname')}</label>
              <input type="text" id="reg_lastname" name="lastname" placeholder="${message(code:'register.surname')}" maxlength="200" value=""/>
            </div>
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="reg_nickname">${message(code:'register.nickname')}</label>
              <input type="text" id="reg_nickname" name="nickname" placeholder="${message(code:'register.nickname')}" maxlength="200" value="" onfocus="setNickname()"/>
            </div>
          </div><br/>
          <div class="ui-body ui-body-b ui-corner-all" style="padding:8px">        
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="reg_email">email</label>
              <input type="email" id="reg_email" name="email" placeholder="email" maxlength="200" value="" />
            </div>
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="reg_password1">${message(code:'register.password')}</label>
              <input type="password" id="reg_password1" name="password1" placeholder="${message(code:'register.password')}" value="" />
            </div>
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="reg_password2">${message(code:'register.password.confirm')}</label>
              <input type="password" id="reg_password2" name="password2" placeholder="${message(code:'register.password.confirm')}" value="" />              
            </div>
          </div>
          <div class="ui-body" style="padding:8px 0">
            <input type="submit" data-theme="f" value="${message(code:'button.send')}" />
          </div>
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
