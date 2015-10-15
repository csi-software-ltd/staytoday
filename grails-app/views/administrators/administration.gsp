<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin"/>
    <g:javascript>
      var iAccesslevel=0;
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
      function deleteUser(lId){
        if (confirm('Вы уверены?')){
          <g:remoteFunction controller='administrators' action='deleteuser' onSuccess='processDeleteUserResponse(e)' params="\'id=\'+lId" />
        }
      }      
      function processDeleteUserResponse(e){
        if (e.responseJSON.done){
          $('details').update('');
          selectGroupUser(1);
        }else{
          if (e.responseJSON.message)
            $('mess').update(e.responseJSON.message);
          $('message').style.display = 'block';
        }
      }      
      function updateDetails(lId,iPart){
        hideAll();		
        $('details').show();
        <g:remoteFunction controller='administrators' action='groupuserdetails' update='details' params="'id='+lId+'&part='+iPart" />
      }      
      function selectGroupUser(lId){
        hideAll();
        <g:remoteFunction controller='administrators' action='groupuserlist' update='groupuser' params="\'id=\'+lId" />
        if (lId) {
          user();
        }else{
          group();
        }
      }      
      function showGroupWindow(){
        hideAll();
        $('details').hide();
        $('creategroup').style.display = 'block';
      }      
      function showUserWindow(){
        hideAll();
        $('details').hide();
        $('createuser').style.display = 'block';		
      }      
      function hideGroupWindow(){
        $('creategroup').style.display = 'none';
        $('name').value = '';
        $('groupmess').update('');
      }      
      function hideUserWindow(){
        $('createuser').style.display = 'none';
        $('login').value = '';
        $('usermess').update('');
      }      
      function hideAll(){
	    $('details').update('');
        hideGroupWindow();
        hideUserWindow();
        closeMessage();
        hidePassWindow();		
      }      
      function processGroupResponse(e){
        if (e.responseJSON.done){
          if (e.responseJSON.message){
            $('mess').update(e.responseJSON.message);
            hideGroupWindow();
            $('message').style.display = 'block';
          }else{
            selectGroupUser(0);
            hideGroupWindow();
            if(e.responseJSON.id)
              updateDetails(e.responseJSON.id,0);
          }
        }else{
          $('groupmess').update(e.responseJSON.message);
        }
      }      
      function processUserResponse(e){
        if (e.responseJSON.done){
          if (e.responseJSON.message){
            $('mess').update(e.responseJSON.message);
            hideUserWindow();
            $('message').style.display = 'block';
          }else{
            selectGroupUser(1);
            hideUserWindow();
            if(e.responseJSON.id)
              updateDetails(e.responseJSON.id,1);
          }
        }else{
          $('usermess').update(e.responseJSON.message);
        }
      }      
      function selectGuestbook(iCheked){
        if (iCheked){
          $('gbmain').enable();
          $('gberrors').enable();
          $('gbcall').enable();
          $('gbpcrequest').enable();
        }else{
          $('gbmain').disable();
          $('gberrors').disable();
          $('gbcall').disable();
          $('gbpcrequest').disable();
        }
      }     
      function closeMessage(){
        $('message').style.display = 'none';
      }      
      function showPassWindow(lId){
        hideAll();              
      }      
      function hidePassWindow(){ /*    
        $('pass').value='';
        $('confirm_pass').value='';
        $('passmess').update('');*/
      }      
      function processPassResponse(e){        
        if (e.responseJSON.done){
          ['confirm_pass','pass'].forEach(function(ids){
            if($(ids))
              $(ids).removeClassName('red');
          });
          //$('passmess').update(e.responseJSON.message);
          $('passmess').update('');
          //hidePassWindow();
          //$('message').style.display = 'block';
        }else{
          $('passmess').update(e.responseJSON.message);                    
          $('confirm_pass').addClassName('red');
          $('pass').addClassName('red');
        }
      }      
      function group(){
        $('userlink').style.color = 'black';
        $('grouplink').style.color = '#0080F0';
      }      
      function user(){
        $('userlink').style.color = '#0080F0';
        $('grouplink').style.color = 'black';
      }
      function userSaveResp(e){
        ['login','name','email'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        var sErrorMsg='';
        if(e.responseJSON.error){
          e.responseJSON.errors.forEach(function(err){
          switch (err) {
            case 1: sErrorMsg+='<li>Незаполненно поле "Логин"</li>';
                    $("login").addClassName('red');            
                    break;
            case 2: sErrorMsg+='<li>Незаполненно поле "Полное имя"</li>';                   
                    $("name").addClassName('red');
                    break;
            case 3: sErrorMsg+='<li>Незаполненно поле "Email"</li>';                   
                    $("email").addClassName('red');
                    break;
          }
        });
        }else if(iAccesslevel==1){
          var lsRegions=jQuery('input:checkbox[name="region_id"]:checked');
          for(var i=0;i<lsRegions.length;i++)
            lsRegions[i].checked=false;         
        }        
        $("errorlist").innerHTML=sErrorMsg;
        $("errorlist").up('div').show();               
      }
      function changeAccesslevel(iLevel){
        if (iLevel==1){
          $('userregions').style.display = 'none';
          $('allregions').style.display = 'block';           
        }else{
          $('userregions').style.display = 'block';
          $('allregions').style.display = 'none';
        }
        iAccesslevel=iLevel;
      }
    </g:javascript>
    <style type="text/css">
      a.link[href]{font-size:16px;font-weight:bold}
    </style>
  </head>  
  <body onload="selectGroupUser(0);">
    <div align="center">
      <table cellpadding="5">  
        <tr>
          <td>
            <g:remoteLink id="grouplink" url="${[controller:'administrators', action:'groupuserlist',id:0]}" update="[success:'groupuser']" onSuccess="hideAll();group();"><h3><u>Группы</u></h3></g:remoteLink>
          </td>
          <td>
            <g:remoteLink id="userlink" url="${[controller:'administrators', action:'groupuserlist',id:1]}" update="[success:'groupuser']" onSuccess="hideAll();user();"><h3><u>Пользователи</u></h3></g:remoteLink>
          </td>
        </tr>
      </table>
      </div>
      <table width="100%" cellpadding="5" cellspacing="5" border="0">
        <tr>
          <td width="225" valign="top">
            <div id="groupuser">              
            </div>
          </td>
          <td valign="top">            
            <div id="details">              
            </div>           
            <div id="creategroup" style="display:none">              
              <div class="glossy drop_shadow" style="padding:0px;width:250px">
                <div class="header_container">
                  <h2>Добавить группу</h2>
                </div>
                <g:formRemote url="[controller:'administrators',action:'creategroup']" onSuccess="processGroupResponse(e)" method="POST" name="createGroupForm">
                  <table width="100%" cellpadding="5" cellspacing="5" border="0">
                    <tr>
                      <td>
                        <label for="name">Введите имя группы:</label>
                        <input type="text" name="name" id="name" style="width:100%;"/>
                      </td>
                    </tr>
                    <tr>
                      <td align="right">
                        <input type="submit" class="button-glossy green mini" value="Добавить" style="margin-right:10px"/>
                        <input type="button" class="button-glossy grey mini" value="Отмена" onclick="hideGroupWindow();"/>
                      </td>
                    </tr>
                  </table>
                </g:formRemote>
              </div>
              <div id="groupmess" style="text-align:center;color:red"></div>
            </div>
            <div id="createuser" style="display:none;">
              <div class="glossy drop_shadow" style="padding:0px;width:250px">
                <div class="header_container">
                  <h2>Добавить пользователя</h2>
                </div>
                <g:formRemote url="[controller:'administrators',action:'createuser']" onSuccess="processUserResponse(e)" method="POST" name="createUserForm">
                  <table width="100%" cellpadding="5" cellspacing="5">
                    <tr>
                      <td>
                        <label for="login" nowrap>Введите логин пользователя:</label>
                        <input type="text" name="login" id="login" style="width:100%;"/>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <label for="pass">Задайте пароль:</label>
                        <input type="password" name="pass" id="pass" style="width:100%;"/>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <label for="confirm_pass">Повторите пароль:</label>
                        <input type="password" name="confirm_pass" id="confirm_pass" style="width:100%;"/>
                      </td>
                    </tr>
                    <tr>
                      <td align="right">
                        <input type="submit" class="button-glossy green mini" value="Добавить" style="margin-right:10px"/>
                        <input type="button" class="button-glossy grey mini" value="Отмена" onclick="hideUserWindow();"/>
                      </td>
                    </tr>
                  </table>
                </g:formRemote>                  
                <div id="usermess" style="text-align:center;color:red"></div>
              </div>
            </div>
            <div id="message" style="display:none;">
              <div class="notice drop_shadow" style="width:200px">
                <table width="200" cellpadding="5" cellspacing="0">
                  <tr>
                    <td align="center"><div id="mess"></div></td>
                  </tr>
                  <tr>
                    <td align="center">
                      <input type="button" value="ОК" style="width:80px" onclick="closeMessage()"/>
                    </td>
                  </tr>
                </table>
              </div>
            </div>
          </td>
        </tr>
      </table>
  </body>
</html>
