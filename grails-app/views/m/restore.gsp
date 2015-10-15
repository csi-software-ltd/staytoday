<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />
    <g:javascript>      
      function processRestResponse(e){ 
        var sErrorMsg='';
        if(e.responseJSON.error>0){
          jQuery.mobile.hidePageLoadingMsg();
          e.responseJSON.errorprop.forEach(function(err){
            switch(err){
              case 1: sErrorMsg='${message(code:'restore.error.fail')}<br/>'; break;
              case 2: sErrorMsg='${message(code:'error.incorrect',args:["Email"])}<br/>'; break;
              case 3: sErrorMsg='${message(code:'restore.error.mobile')}<br/>'; break;
              case -100: sErrorMsg='${message(code:'login.error.bderror')}';
            }
          });
          if(sErrorMsg.length){
            jQuery('#restore_fail').html(sErrorMsg);
            $('restore_fail').show();
          }
        }else if(e.responseJSON.error==0){  
          jQuery('#restore_fail').html('');  
          sErrorMsg='${message(code:"restore.notice")}';    
          jQuery('#restore_fail').html(sErrorMsg);
          $('restore_fail').show();
        }        
      }    
    </g:javascript>
  </head>
  <body>
      <h1 role="heading">${infotext['header'+context?.lang]?:''}</h1>
      <div data-role="content" style="padding:8px">    
        <g:formRemote name="restoreForm" url="[action:'rest']" method="post" onLoading="jQuery.mobile.showPageLoadingMsg('b','${message(code:'spinner.message.mobile')}',false)" onSuccess="processRestResponse(e)">
          <div class="notice" id="restore_fail" style="display:none">          
          </div>
          <div class="ui-grid-a" style="padding:8px 4px">          
            <div class="ui-block-a"><b>${message(code:'login.user')}:</b></div>
            <div class="ui-block-b" align="right"><b><a href="${createLink(uri:'/auth?from='+actionName,base:context?.mobileURL_lang)}">${message(code:'login.auth')}</a></b></div>
          </div>
          <div class="ui-body ui-body-b ui-corner-all" style="padding:8px">
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="rest_mail_name">email</label>
              <input type="email" name="name" id="rest_mail_name" placeholder="email" />            
            </div>
          </div>
          <div class="ui-body" style="padding:8px 0">
            <input type="submit" data-theme="f" value="${message(code:'button.recover')}"/>
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
