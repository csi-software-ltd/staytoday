<html>
  <head>    
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />  
    <meta name="layout" content="main"/>
    <g:javascript>
    function sendMessage(){
      $("verifyTelButton").hide();
      $('loader').show();
      <g:remoteFunction controller='profile' action='sendVerifyTel' onSuccess='processResponse(e)' />
    }
    function processResponse(e){
      if(e.responseJSON.error){
        $('loader').hide();
        $("verifyTel").hide();
        $("verifyTelError").show();
      }else{
        $('loader').hide();
        $("verifyTelError").hide();
        $("verifyTel").show();
      }
    }
    function processVerifyResponse(e){
      if(e.responseJSON.error){
        $("smscode").addClassName('red');
        if(e.responseJSON.fail_count<=${maxerror}){
          $("smscode_error").show();
        } else {
          $("smscode_error").innerHTML = "${message(code:'profile.error.sms')}";
          $("smscode_error").show();
        }
      }else{
        $("smscode").removeClassName('red');
        location.reload(true);
      }
    }
    </g:javascript>
  </head>
  <body>
              <g:render template="/profile_menu" />                        
                    <g:if test="${flash?.error==0}">
                      <div class="notice">
                        ${message(code:'profile.verify.notice')}
                      </div>
                    </g:if><g:elseif test="${flash?.error==-100}">
                      <div class="notice">
                        <ul>
                          <g:if test="${flash?.error==-100}"><li>${message(code:'profile.error.sendmail')}</li></g:if>
                        </ul>
                      </div> 
                    </g:elseif>
                      <div class="form shadow">
                        <h2 class="toggle border"><span class="edit_icon ${(verifyStatus==1)?'verified':'password'}"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                        <g:if test="${infotext['itext'+context?.lang]}">
                        <div style="padding:0 20px">
                          <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                        </div>
                      </g:if>  
                        <ul class="list">
                        <g:if test="${verifyStatus==-1}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.ban')}</span></li>
                        </g:if>
                        <g:elseif test="${verifyStatus==0}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.email.noconfirmed')}</span><br/>
                            <div class="text"><p>${message(code:'verify.please')}, <g:link action="edit">${message(code:'verify.specify')}</g:link> ${message(code:'verify.email')}.</p></div>
                          </li>
                        </g:elseif>
                        <g:elseif test="${verifyStatus==1}">
                          <li class="clearfix"><span class="icon"></span><span class="label">${message(code:'verify.email.confirmed')}</span></li>
                        </g:elseif>
                        <g:elseif test="${verifyStatus==2}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.email.noconfirmed')}</span><br/>
                            <div class="padd20">
                              <p>${message(code:'verify.email.confirm')} <b>${user.email}</b>.</p>
                              <p>${message(code:'verify.email.confirm.info')} <g:link action="verifyUser" params="[sendmail:true]">${message(code:'verify.email.confirm.request')}</g:link>.</p>                            
                            </div>
                          </li>        
                        </g:elseif>
                          <li class="clearfix">
                            <b class="padd20">${user?.email}</b>
                          </li>
                        </ul><br/>
                        <h2 class="toggle border"><span class="edit_icon ${(user.tel && user.is_telcheck==1)?'verified':'password'}"></span>${infotext['promotext2'+context?.lang]?:''}</h2>
                      <g:if test="${infotext['itext2'+context?.lang]}">
                        <div style="padding:0 20px">
                          <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                        </div>
                      </g:if>                          
                        <ul class="list">
                      <g:if test="${user.tel}">
                        <g:if test="${user.is_telcheck==1}">
                          <li class="clearfix"><span class="icon"></span><span class="label">${message(code:'verify.phone.confirmed')}</span></li>
                          <li class="clearfix">
                            <b class="padd20">${user?.tel}</b>
                          </li>  
                        </g:if>
                        <g:elseif test="${user.is_telcheck==0}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.phone.noconfirmed')}</li>
                          <div class="padd20" id="verifyTelButton" style="clear:both;${isSMSsend?'display:none':''}">
                            <p>${message(code:'verify.phone.confirm.info')}:</p>
                            <div class="rounded" style="width:${user.tel.size()*12.2+(user.tel.size()<=9?112:92)}px">
                              <table width="100%" cellpadding="0" cellspacing="8" border="0">
                                <tr>
                                  <td>${user.tel}</td>
                                  <td align="center">
                                    <span class="action_button orange" style="margin-right:0">
                                      <a class="icon none" href="javascript:void(0)" onclick="sendMessage()">${message(code:'verify.phone.confirm')}</a>
                                    </span>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </div>
                          <div class="padd20" id="verifyTel" style="clear:both;${isSMSsend?'':'display:none'}">
                            <g:rawHtml>${message(code:'verify.phone.confirm.notice')}</g:rawHtml>            
                            <g:formRemote id="verifyTelForm" name="verifyTelForm" method="post" url="[ controller: 'profile', action: 'verifyTel' ]" onSuccess="processVerifyResponse(e)">
                              <table>
                                <tr>
                                  <td>
                                    <label for="smscode">${message(code:'verify.code')}:</label>
                                    <input type="text" class="mini" id="smscode" name="smscode" value="" size="10"/>
                                  </td>
                                  <td><input type="submit" class="button-glossy orange" value="${message(code:'button.confirm')}"/></td>
                                </tr>
                              </table>
                            </g:formRemote>
                          </div>
                          <div class="notice" id="smscode_error" style="display:none">
                            ${message(code:'profile.error.wrongcode')}
                          </div>
                          <div id="verifyTelError" class="notice" style="display:none;clear:both">
                            ${message(code:'profile.error.sendsms')} <g:link controller="help" action="guestbook">${message(code:'profile.error.sendsms.contact')}</g:link> ${message(code:'profile.error.sendsms.admin')}.
                          </div>
                        </g:elseif>                                                
                      </g:if><g:else>
                          <li class="clearfix">${message(code:'verify.please')}, <g:link action="edit">${message(code:'verify.specify')}</g:link> ${message(code:'verify.phone')}</li>
                      </g:else>
                        </ul><br/>
                        <h2 class="toggle border"><span class="edit_icon ${(user.agency && user.is_agentcheck==1)?'verified':'password'}"></span>${message(code:'verify.agency')}</h2>
                      <g:if test="${infotext['itext3'+context?.lang]}">
                        <div style="padding:0 20px">
                          <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                        </div>
                      </g:if>                
                        <ul class="list">
                      <g:if test="${user.agency}">
                        <g:if test="${user.is_agentcheck==1}">
                          <li class="clearfix"><span class="icon"></span><span class="label">${message(code:'verify.agency.confirmed')}</span></li>
                        </g:if>
                        <g:elseif test="${user.is_agentcheck==0}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.agency.noconfirmed')}</span></li>
                        </g:elseif>
                          <li class="clearfix">
                            <b class="padd20">${user?.agency}</b>
                          </li>
                      </g:if><g:else>
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.agency.nospecified')}</span></li>
                      </g:else>
                        </ul>
                    
                        <div id="loader" style="display: none">
                          <img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/>
                        </div>
                        
                      </div>
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>                    
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
