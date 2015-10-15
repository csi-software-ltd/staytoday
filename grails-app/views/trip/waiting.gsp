<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/>
    <g:javascript>
    var iLim = ${textlimit}
    function textLimit(sId){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iLim){
        symbols = symbols.substring(0,iLim);
        $(sId).value = symbols;
        return false;
      }
    }

    function updateRegion(lCountryId){
      <g:remoteFunction controller='home' action='region' update='region_id' params="\'countryId=\'+lCountryId+'&lang=${context?.lang}'" />
    }
    function resetDate(){
      $('date_start_year').setValue('');
      $('date_end_year').setValue('');
      $('date_start_month').setValue('');
      $('date_end_month').setValue('');
      $('date_start_day').setValue('');
      $('date_end_day').setValue('');
    }
    function initialize(){    
      <g:if test="${(flash?.error?:[]).size()>0}">                      
        var sHtml='<ul>';                       
        <g:each in="${flash?.error}"> 
          <g:if test="${it==1}">sHtml+='<li>${message(code:"error.blank",args:[message(code:"ads.city").capitalize()])}</li>';                        
            $('city_auto').addClassName('red');                          
          </g:if>
          <g:if test="${it==2}">sHtml+='<li>${message(code:"error.blank",args:[message(code:"waiting.price.from").capitalize()])}</li>';                                                    
            $('pricefrom').addClassName('red');                         
          </g:if>
          <g:if test="${it==3}">sHtml+='<li>${message(code:"error.blank",args:[message(code:"waiting.request.text").capitalize()])}</li>';
            $('ztext').addClassName('red');                            
          </g:if>
          <g:if test="${it==4}">sHtml+='<li>${message(code:"error.blank",args:[message(code:"common.date_from").capitalize()])}</li>';                         
            $("date_start_value").addClassName('red');                                                     
          </g:if>
          <g:if test="${it==5}">sHtml+='<li>${message(code:"error.blank",args:[message(code:"common.date_to").capitalize()])}</li>';                          
            $("date_end_value").addClassName('red');                         
          </g:if>
          <g:if test="${it==6}">sHtml+='<li>${message(code:"ads.error.price.date",args:[message(code:"common.date_from").capitalize(),message(code:"common.date_to").capitalize()])}</li>';                          
            $("date_start_value").addClassName('red');
            $("date_end_value").addClassName('red');                           
          </g:if>
          <g:if test="${it==7}">sHtml+='<li>${message(code:"error.incorrect",args:[message(code:"personal.phone").capitalize()])}</li>';                          
            $("telef").addClassName('red');                            
          </g:if>
          <g:if test="${it==8}">sHtml+='<li>${message(code:"error.db")}</li>';</g:if>
          <g:if test="${it==9}">sHtml+='<li>${message(code:"error.blank",args:[message(code:"personal.phone").capitalize()])}</li>';                          
            $("telef").addClassName('red');                         
          </g:if>
          <g:if test="${it==10}">sHtml+='<li>${message(code:"error.blank",args:["Email"])}</li>';                          
            $("email").addClassName('red');                           
          </g:if>
          <g:if test="${it==11}">sHtml+='<li>${message(code:"error.incorrect",args:["Email"])}</li>';                          
            $("email").addClassName('red');                            
          </g:if>
          <g:if test="${it==12}">sHtml+='<li>${message(code:"error.email.exist")}!</li>';                          
            $("email").addClassName('red');                         
          </g:if>
          <g:if test="${it==100}">sHtml+='<li>${message(code:"error.email.exist")}. <a href="javascript:void(0)" onclick="showLoginForm(3,\'login_lbox\',this);">${message(code:'common.log_in').capitalize()}</a>, ${message(code:'common.or_change_email')}</li>';                        
            $("reg_email").addClassName('red');                            
          </g:if>
          <g:if test="${it==101}">sHtml+='<li>${message(code:"error.blank",args:["Email"])}</li>';                         
            $("reg_email").addClassName('red');                           
          </g:if>                          
          <g:if test="${it==102}">sHtml+='<li>${message(code:"error.blank",args:[message(code:"register.nickname").capitalize()])}</li>';                          
            $("reg_nickname").addClassName('red');                           
          </g:if>
          <g:if test="${it==103}">sHtml+='<li>${message(code:"error.incorrect",args:["Email"])}</li>';                          
            $("reg_email").addClassName('red');                            
          </g:if>
        </g:each>	
        sHtml+='</ul>';                    
        $("error").update(sHtml);
        $("error").show();
    </g:if>
    }
    </g:javascript>
    <style type="text/css">
      .form label { min-width: 90px !important }
      .form label.mini { min-width: 75px !important; padding-left: 0px	}
      .form input[type="text"].mini, .form input[type="password"].mini { width: 190px }
      .count { position: relative !important }
    </style>    
  </head>
  <body onload="initialize()">
              <g:render template="/trip_menu" />                                  
                      <div class="form shadow" style="min-height:487px">
                      <g:if test="${infotext['itext'+context?.lang]}">
                        <div style="padding: 10px 20px 5px">
                          <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                        </div>
                      </g:if>
                        <div id="error" class="notice" style="margin-top:10px;display:none"></div>                                                
                      <g:if test="${!inrequest.isadd}"> 
                        <h2 class="toggle border"><span class="edit_icon map"></span>${infotext['promotext1'+context?.lang]?:''}</h2>                      
                        <g:if test="${infotext['itext2'+context?.lang]}">
                        <div style="padding:0 20px 5px">
                          <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                        </div>
                        </g:if>
                        <g:form name="addWaitingForm" url="${[action:'saveWaiting']}" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">
                            <tr>
                              <td width="50%">
                                <label for="country_id">${message(code:'ads.country')}</label>		
                                <select class="mini" id="country_id" name="country_id" onchange="updateRegion(this.value)">
                                <g:each in="${country}" var="item">            
                                  <option <g:if test="${item?.id==inrequest?.country_id}">selected="selected"</g:if> value="${item?.id}">
                                    ${item['name'+context?.lang]}
                                  </option>
                                </g:each>
                                </select>
                              </td>
                              <td>
                                <label for="region_id">${message(code:'ads.region')}</label>		
                                <div id="region_result">
                                  <select class="mini" id="region_id" name="region_id">
                                  <g:each in="${region}" var="item">            
                                    <option <g:if test="${item?.id==inrequest?.region_id}">selected="selected"</g:if> value="${item?.id}">
                                      ${item['name'+context?.lang]}
                                    </option>
                                  </g:each>
                                  </select>
                                </div>
                              </td>		
                            </tr>
                            <tr>
                              <td>
                                <label for="city">${message(code:'ads.city')}</label>
                                <input type="text" class="mini" id="city_auto" name="city" maxlength="${stringlimit}" value="${inrequest?.city}" />
                              </td>
                              <td>
                                <label for="hometype_id">${message(code:'common.home_type')}</label>		
                                <select class="mini" id="hometype_id" name="hometype_id">
                                  <option <g:if test="${item?.id==inrequest?.hometype_id}">selected="selected"</g:if> value="0">${message(code:'common.any')}</option>
                                <g:each in="${hometype}" var="item">            
                                  <option <g:if test="${item?.id==inrequest?.hometype_id}">selected="selected"</g:if> value="${item?.id}">
                                    ${item['name'+context?.lang]}
                                  </option>
                                </g:each>
                                </select>
                              </td>
                            </tr>
                          </table><br/><br/>
                          <h2 class="toggle border"><span class="edit_icon detail"></span>${infotext['promotext2'+context?.lang]?:''}</h2>
                        <g:if test="${infotext['itext3'+context?.lang]}">
                          <div style="padding:0 20px 5px">
                            <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                          </div>
                        </g:if>                      
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">                          
                            <tr>
                              <td width="35%" valign="middle">
                                <label for="date_start">${message(code:'common.date_from')}</label>
                                <calendar:datePicker name="date_start" value="${inrequest?.date_start}" dateFormat="%d-%m-%Y"/>
                              </td>	 
                              <td width="30%" valign="middle">
                                <label for="date_end" class="mini">${message(code:'common.date_to')}</label>
                                <calendar:datePicker name="date_end" value="${inrequest?.date_end}" dateFormat="%d-%m-%Y"/>
                              </td>
                              <td>
                                <label for="homeperson_id" class="mini">${message(code:'common.guests')}</label>		
                                <select id="homeperson_id" name="homeperson_id" style="width:140px">
                                <g:each in="${homeperson}" var="item">            
                                  <option <g:if test="${item?.id==inrequest?.homeperson_id}">selected="selected"</g:if> value="${item?.id}">
                                    ${item?.kol}
                                  </option>
                                </g:each>
                                </select>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="pricefrom" style="padding-right:16px">${message(code:'waiting.price.from')}</label>
                                <input type="text" class="price" id="pricefrom" name="pricefrom" value="${inrequest?.pricefrom}" />
                              </td>
                              <td>
                                <label for="priceto" class="mini">${message(code:'waiting.price.to')}</label>
                                <input type="text" class="price" id="priceto" name="priceto" value="${inrequest?.priceto}" />
                              </td>
                              <td>
                                <label for="valuta_id" class="mini">${message(code:'ads.price.valuta')}</label>
                                <select id="valuta_id" name="valuta_id" style="width:140px">
                                <g:each in="${valuta}" var="item">            
                                  <option <g:if test="${item?.id==inrequest?.valuta_id}">selected="selected"</g:if> value="${item?.id}">
                                    <g:rawHtml>${item?.symbol?:''}</g:rawHtml>&nbsp;&nbsp;${item?.code}
                                  </option>
                                </g:each>
                                </select>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="3">
                                <label for="ztext">${message(code:'waiting.request.text')}</label>
                                <textarea style="width:560px" rows="5" cols="40" id="ztext" name="ztext" onkeydown="textLimit(this.id);" onkeyup="textLimit(this.id);">${inrequest?.ztext?:''}</textarea>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="3">
                                <label for="telef">${message(code:'personal.phone')}</label>
                                +<input type="text" id="ind" name="ind" value="${ind?:''}" size="3" style="width:40px"/>
                                ( <input type="text" id="kod" name="kod" value="${kod?:''}" size="7" style="width:50px"/> )
                                <input type="text" id="telef" name="telef" value="${telef?:''}" size="15" style="width:100px"/>
                              </td>
                            </tr>
                          <g:if test="${user && !user?.email}">                                      
                            <tr>
                              <td colspan="3">                    
                                <label for="email" class="mini">${message(code:'personal.email')}</label>
                                <input type="text" id="email" name="email" maxlength="${stringlimit}" value="${inrequest?.email}" />          
                              </td>
                            </tr>
                          </g:if>
                            <tr>
                              <td colspan="3">
                                <label for="timetodecide_id">${message(code:'waiting.request.expired')}</label>
                                <select id="timetodecide_id" name="timetodecide_id" class="mini">
                                <g:each in="${timetodecide}" var="item">
                                  <option <g:if test="${item?.id==inrequest?.timetodecide_id}">selected="selected"</g:if> value="${item?.id}">
                                    ${context?.lang=='_en'?item['name2'+context?.lang]:item?.name}
                                  </option>
                                </g:each>
                                </select>            
                              </td>
                            </tr> 
                          </table><br/><br/>
                        <g:if test="${!user}">                          
                          <h2 class="toggle border"><span class="edit_icon owner"></span>${message(code:'waiting.feedback')}</h2>
                          <div class="col relative" style="margin:-57px 10px 0">                            
                            <span class="count">
                              <b><a href="javascript:void(0)" onclick="showLoginForm(3,'login_lbox',this);$('reg_lbox').hide()">${message(code:'label.login')}</a></b>                         
                            </span>
                          </div>
                          <div id="mbox_error" class="notice" id="main_reg_fail" style="margin:10px 0;display:none"></div>
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">  
                            <tr>
                              <td width="50%">
                                <label for="reg_nickname">${message(code:'register.nickname')}</label>
                                <input type="text" class="mini" id="reg_nickname" name="nickname" maxlength="${stringlimit}" value="${inrequest?.nickname?:''}" />
                              </td>
                              <td rowspan="2"><p>${message(code:'waiting.email.info')}</p></td>
                            </tr>
                            <tr>
                              <td>
                                <label for="reg_email">email</label>
                                <input type="text" class="mini" id="reg_email" name="email" maxlength="${stringlimit}" value="${inrequest?.email?:''}" />
                              </td>
                            </tr>
                          </table>
                        </g:if>
                          <div style="padding:20px">
                            <input type="submit" class="button-glossy orange" value="${message(code:'button.save')}" style="margin-right:5px" onclick="yaCounter15816907.reachGoal('waiting_new');return true;" />
                            <input type="reset" class="button-glossy grey" onclick="resetDate();" value="${message(code:'button.reset')}" />
                          </div>
                        </g:form>
                      </g:if><g:else>
                        <div class="notice">
                        <g:if test="${user && user?.modstatus!=1}">
                          ${message(code:'waiting.request.add.noverified',args:[user.email?:''])} <g:link controller="profile" action="verifyUser">${message(code:'personal.verification')}</g:link>
                        </g:if><g:else>
                          ${message(code:'waiting.request.add')}
                        </g:else>
                        </div>
                      </g:else>
                      </div>
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                  </div>
                </div>
              </td>
            </tr>                                        
  </body>
</html>
