<html>
  <head>    
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />  
    <meta name="layout" content="main"/>
    <g:javascript>
    function initialize(){
    <g:if test="${session.attention_message!='' && session.attention_message!=null}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
    </g:if>
    <g:if test="${session.attention_message_once}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message_once}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
    </g:if>
    }
    var iLim = ${textlimit};
    function textLimit(sId){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iLim){
        symbols = symbols.substring(0,iLim);
        $(sId).value = symbols;
        return false;
      }
    }
    function sendForm(){
		  $("editForm").submit();
    }
    function viewAgency(name,viewname){
      if($(name).checked){        
        $(viewname).show();
      } else {
        $(viewname).hide();        
      }
    }
    function viewGmt(name,viewname){
      if($(name).checked){        
        $(viewname).show();
      } else {
        $(viewname).hide();        
      }
    }
    </g:javascript>
    <style type="text/css">
      .form label { min-width: 90px !important }
      .form label.mini { min-width: 120px !important; padding-left: 0px	}
      .form input[type="text"].mini, .form input[type="password"].mini { width: 190px }
    </style>
  </head>
  <body onload="initialize()">
              <g:render template="/profile_menu" />                        
                    <g:if test="${(flash?.error?:[]).size()>0}">
                      <div class="notice" style="margin-top:5px">
                        <ul>
                        <g:each in="${flash?.error}"> 
                          <g:if test="${it==1}"><li>${message(code:"login.error.match")}</li></g:if>
                          <g:if test="${it==2}"><li>${message(code:"login.error.notenough")}</li></g:if>
                          <g:if test="${it==3}"><li>${message(code:"error.incorrect",args:["Email"])}</li></g:if>
                          <g:if test="${it==4}"><li>${message(code:'error.blank',args:[message(code:'common.nickname').capitalize()])}</li></g:if>
                          <g:if test="${it==5}"><li>${message(code:"error.db")}</li></g:if>
                          <g:if test="${it==6}"><li>${message(code:"error.email.exist")}!</li></g:if>
                          <g:if test="${it==7}"><li>${message(code:"error.incorrect",args:[message(code:"personal.phone").capitalize()])}</li></g:if>
                          <g:if test="${it==8}"><li>${message(code:"error.incorrect",args:[message(code:"profile.phone.additional")])}</li></g:if>
                          <g:if test="${it==9}"><li>${message(code:"error.blank",args:[message(code:"personal.phone").capitalize()])}</li></g:if>
                          <g:if test="${it==10}"><li>${message(code:"error.blank",args:[message(code:"profile.agency")])}</li></g:if>
                        </g:each>	
                        </ul>
                      </div>
                    </g:if>  
                      <div class="form shadow">
                        <h2 class="toggle border"><span class="edit_icon owner"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                      <g:if test="${infotext['itext'+context?.lang]}">
                        <div style="padding:0 20px">
                          <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                        </div><br/>
                      </g:if>  
                        <g:form name="editForm" url="${[action:'saveProfile']}" method="post" useToken="true" base="${context?.mainserverURL_lang}">      
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">
                          <g:if test="${user?.provider=='staytoday'}">
                            <tr>
                              <td width="50%">
                                <label for="firstname">${message(code:'register.name')}</label>
                                <input type="text" class="mini" id="firstname" maxlength="${stringlimit}" name="firstname" value="${inrequest?.firstname?:''}"/>
                              </td>                          
                              <td>
                                <label for="lastname" class="mini">${message(code:'register.surname')}</label>
                                <input type="text" class="mini" id="lastname" maxlength="${stringlimit}" name="lastname" value="${inrequest?.lastname?:''}"/>
                              </td>
                            </tr>
                          </g:if>
                            <tr>
                              <td>
                                <label for="nickname">${message(code:'register.nickname')}</label>
                                <input type="text" class="mini <g:if test="${(flash?.save_error?:[]).contains(4)}">red</g:if>" id="nickname" maxlength="${stringlimit}" name="nickname" value="${inrequest?.nickname?:''}"/>
                              </td>
                              <td>
                                <label for="provider" class="mini">${message(code:'personal.connect')}</label>
                                <input type="text" class="mini" name="provider" readonly value="${user.provider}"/>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="2">
                                <label for="description">${message(code:'profile.about')}</label>
                                <textarea rows="5" cols="40" id="description" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" name="description" style="width:565px">${inrequest?.description?:''}</textarea>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="2">
                                <label for="telef">${message(code:'personal.phone')}</label>
                                +<input type="text" id="ind" name="ind" value="${ind?:''}" size="3" <g:if test="${(flash?.save_error?:[]).contains(7)||(flash?.save_error?:[]).contains(9)}">class="red"</g:if> style="width:40px"/>
                                ( <input type="text" id="kod" name="kod" value="${kod?:''}" size="7" <g:if test="${(flash?.save_error?:[]).contains(7)||(flash?.save_error?:[]).contains(9)}">class="red"</g:if> style="width:50px"/> )
                                <input type="text" id="telef" name="telef" value="${telef?:''}" size="15" <g:if test="${(flash?.save_error?:[]).contains(7)|(flash?.save_error?:[]).contains(9)}">class="red"</g:if> style="width:100px"/>
                              </td>                              
                            </tr>
                          <g:if test="${(user.tel?:0)}">
                            <tr>
                              <td colspan="2">
                                <label for="telef1">${message(code:'profile.phone.additional')}</label>
                                +<input type="text" id="ind1" name="ind1" value="${ind1?:''}" size="3" <g:if test="${(flash?.save_error?:[]).contains(8)}">class="red"</g:if> style="width:40px"/>
                                ( <input type="text" id="kod1" name="kod1" value="${kod1?:''}" size="7" <g:if test="${(flash?.save_error?:[]).contains(8)}">class="red"</g:if> style="width:50px"/> )
                                <input type="text" id="telef1" name="telef1" value="${telef1?:''}" size="15" <g:if test="${(flash?.save_error?:[]).contains(8)}">class="red"</g:if> style="width:100px"/>
                              </td>                              
                            </tr>
                          </g:if>
                            <tr>
                              <td colspan="2">
                                <label for="skype">skype</label>
                                <input type="text" class="mini" id="skype" maxlength="${stringlimit}" name="skype" value="${inrequest?.skype?:''}"/>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="2">
                                <label for="www">${message(code:'profile.edit.web-site')}</label>
                                <input type="text" class="mini" id="www" maxlength="${stringlimit}" name="www" value="${inrequest?.www?:''}"/>
                              </td>
                            </tr>                            
                            <tr>
                              <td colspan="2" style="padding:18px 3px">
                                <b><input type="checkbox" id="is_agent" name="is_agent" value="1" <g:if test="${inrequest?.is_agent?:0}">checked</g:if> onclick="viewAgency(this,'agency');" style="margin-left:20px;"> ${message(code:'profile.agency.confirm')}</b>
                                <div id="agency" style="margin-top:10px;<g:if test="${(inrequest?.is_agent?:0)==0}">display:none</g:if>">
                                  <label for="agency">${message(code:'profile.agency')}</label>
                                  <input type="text" maxlength="${stringlimit}" name="agency" <g:if test="${(flash?.save_error?:[]).contains(10)}">class="red"</g:if> value="${inrequest?.agency}" />
                                </div>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="2" style="padding:18px 3px">
                                <b><input type="checkbox" id="is_gmt" name="is_gmt" value="1" <g:if test="${inrequest?.gmt_id?:0}">checked</g:if> onclick="viewGmt(this,'gmt');" style="margin-left:20px;">${message(code:'profile.timezone.specify')}</b>
                                <div id="gmt" style="margin-top:10px;<g:if test="${!(inrequest?.gmt_id?:0)}">display:none</g:if>">
                                  <label for="agency">${message(code:'profile.timezone')}</label>
                                  <select id="gmt_id" name="gmt_id" style="width:583px">
                                  <option selected="selected" value="0">${message(code:'common.nospecified')}</option>
                                  <g:each in="${gmt}" var="item">            
                                    <option <g:if test="${item?.id==user?.gmt_id}">selected="selected"</g:if> value="${item?.id}">
                                    ${item['name'+context?.lang]} ${item?.code}
                                  </option>
                                  </g:each>
                                  </select>
                                  </div>
                              </td>
                            </tr>                                                        
                            <tr>
                              <td colspan="2">
                                <label for="email">${message(code:'personal.email')}</label>
                                <input type="text" <g:if test="${(flash?.save_error?:[]).contains(3)||(flash?.save_error?:[]).contains(6)}">class="red"</g:if> id="email" name="email" maxlength="${stringlimit}" value="${inrequest?.email?:user?.email?:''}" <g:if test="${verifyStatus == 1}">disabled="disabled"</g:if>/>
                              </td>
                            </tr>                          
                            <tr>
                              <td colspan="2" style="padding: 20px">
                                <input type="button" class="button-glossy orange" value="${message(code:'button.save')}" onclick="sendForm()" style="margin-right:5px"/>
                                <input type="reset" class="button-glossy grey" value="${message(code:'button.reset')}" onclick=""/>
                              </td>
                            </tr>     
                          </table>
                        </g:form>
                        <h2 class="toggle border"><span class="edit_icon detail"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                        <label for="linkname">${message(code:'profile.link')}</label>
                        <input type="text" id="linktext" readonly style="width:75%" value="${context?.serverURL+((context?.is_dev)?"/"+context?.appname+"/id":"/id")+user?.id}"><br/><br/>
                      <g:if test="${!user.is_external}">
                        <h2 class="toggle border"><span class="edit_icon password"></span>${message(code:'profile.password.change')}</h2>
                        <g:form name="changepassForm" url="${[action:'changepass']}" method="POST" useToken="true" base="${context?.mainserverURL_lang}">            
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">
                            <tr>
                              <td>
                                <label for="pass">${message(code:'register.password')}</label>
                                <input type="password" class="mini <g:if test="${(flash?.save_error?:[]).contains(2)}">red</g:if>" id="pass" name="pass" maxlength="${stringlimit}" value=""/>
                              </td>
                              <td>
                                <label for="pass2">${message(code:'register.password.confirm')}</label>
                                <input type="password" class="mini <g:if test="${(flash?.save_error?:[]).contains(1)}">red</g:if>" id="pass2" name="pass2" maxlength="${stringlimit}" value=""/>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="2" style="padding:20px">
                                <input type="submit" class="button-glossy orange" value="${message(code:'button.save')}" />
                              </td>
                            </tr>     
                          </table>  
                        </g:form>
                      </g:if>
                        <h2 class="toggle border" style="margin-bottom:5px"><span class="edit_icon verified"></span>${message(code:'personal.verification').capitalize()}</h2>
                        <ul class="list" style="margin-top:0px;border-top:none">
                        <g:if test="${verifyStatus == -1}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.ban')}</span></li>
                        </g:if><g:elseif test="${verifyStatus == 0}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">
                          ${message(code:'verify.please')}, <g:link action="edit" base="${context?.mainserverURL_lang}">${message(code:'verify.specify')}</g:link> ${message(code:'verify.email')}</span></li>
                        </g:elseif><g:elseif test="${verifyStatus == 1}">
                          <li class="clearfix"><span class="icon"></span><span class="label">${message(code:'verify.email.confirmed')}</span></li>
                        </g:elseif><g:elseif test="${verifyStatus == 2}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.email.noconfirmed')}. ${message(code:'verify.please')}, <g:link controller="profile" action="verifyUser" base="${context?.mainserverURL_lang}">${message(code:'verify.confirm')}</g:link> ${message(code:'verify.email')}.</span></li>
                        </g:elseif>
                        </ul>
                        <ul class="list" style="margin-top:0px">            
                      <g:if test="${user.tel}">
                        <g:if test="${user.is_telcheck==1}">
                          <li class="clearfix"><span class="icon"></span><span class="label">${message(code:'verify.phone.confirmed')}</span></li>
                        </g:if><g:elseif test="${user.is_telcheck==0}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.phone.noconfirmed')}</span></li>
                        </g:elseif>
                      </g:if><g:else>
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.please')}, <g:link action="edit" base="${context?.mainserverURL_lang}">${message(code:'verify.specify')}</g:link> ${message(code:'verify.phone')}.</span></li>
                      </g:else>
                        </ul>
                        <ul class="list" style="margin-top:0px">            
                      <g:if test="${user.agency}">
                        <g:if test="${user.is_agentcheck==1}">
                          <li class="clearfix"><span class="icon"></span><span class="label">${message(code:'verify.agency.confirmed')}</span></li>
                        </g:if><g:elseif test="${user.is_agentcheck==0}">
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.agency.noconfirmed')}</span></li>
                        </g:elseif>
                      </g:if><g:else>
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.agency.nospecified')}</span></li>
                      </g:else>
                        </ul>
                        <ul class="list" style="margin-top:0px">
                        <g:if test="${client}">
                        <g:if test="${client.partnerstatus==2}">
                          <li class="clearfix"><span class="icon"></span><span class="label">${message(code:'verify.partner.confirmed')}</span></li>
                        </g:if><g:else>
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.please')}, <g:link controller="account" action="partner" base="${context?.mainserverURL_lang}">${message(code:'verify.confirm')}</g:link> ${message(code:'verify.partner')}.</span></li>
                        </g:else>
                        </g:if><g:else>
                          <li class="clearfix"><span class="icon none"></span><span class="label">${message(code:'verify.partner.nospecified')} ${message(code:'payout.notice.noclient')}</span></li>
                        </g:else>
                        </ul>
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
