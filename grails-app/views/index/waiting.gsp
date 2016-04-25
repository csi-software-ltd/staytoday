<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${createLink(controller:'index',action:'waiting',base:context?.mainserverURL_lang)}" />
    <meta name="layout" content="main" />
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/>
    <g:javascript>
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
          <g:if test="${it==102}">sHtml+='<li>${message(code:'error.blank',args:[message(code:'register.nickname').capitalize()])}</li>';                          
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
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">
                <a class="button" rel="nofollow" onclick="<g:if test='${isLoginDenied}'>showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>
              </td>
              <td width="730" colspan="3" class="rent ss">
                <h1 class="header" style="width:710px">${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr>
              <td colspan="3" class="bg_shadow">              
                <ul class="breadcrumbs" itemscope itemtype="http://schema.org/BreadcrumbList">
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" itemprop="item">
                      <span itemprop="name">${message(code:'label.main')}</span>
                    </a><meta itemprop="position" content="1" />
                  </li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <span itemprop="item">
                      <span itemptop="name">${infotext['header'+context?.lang]?:''}</span>
                    </span><meta itemprop="position" content="2" />
                  </li>
                </ul>
              </td>
            </tr>            
            <tr>
              <td valign="top" style="padding-top:10px">
                <h2 class="padd20">${message(code:'index.pop_directions').capitalize()}</h2>                
                <ul class="search_filter_content">                      
                <g:each in="${countries}" var="country" status="i"><g:if test="${country.id in countryIds}">                
                  <g:each in="${popdirection}" var="record" status="j"><g:if test="${country.id==record.country_id}">
                  <li class="clearfix dott ${(j==0)?'first':''}">
                    <b><g:if test="${record.is_index}"><a class="show_more_link" href="<g:createLink controller='index' action='direction' id='${record.linkname}' params='${[country:country.urlname]}' base='${context?.mainserverURL_lang}'/>"></g:if>
                    <g:else><span class="show_more_link" onclick="transit(${context.is_dev?1:0},['${record.linkname}','${country.urlname}'],'','','','${context?.lang}')"></g:else>
                    ${record['name2'+context?.lang]}<g:if test="${record.is_index}"></a></g:if><g:else></span></g:else></b><br/>
                    <p class="desc">${record['name'+context?.lang]}</p>
                  </li>
                  </g:if></g:each>
                </g:if></g:each>    
                  <li class="clearfix dott last" style="text-align:right">
                    <g:link class="show_more_link" controller="index" action="popdirectionAll" base="${context?.absolute_lang}">${message(code:'index.all_directions')}</g:link> <font color="#FF530D">&raquo;</font>
                  </li>              
                </ul>                  
              </td>
              <td colspan="3" valign="top">                                  
                <div class="form shadow" style="min-height:487px">
                <g:if test="${infotext['itext'+context?.lang]}">
                  <div style="padding: 10px 20px 5px">
                    <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                  </div>
                </g:if>
                  <div id="error" class="notice" style="display:none"></div>                                                
                <g:if test="${!inrequest.isadd}">  
                  <h2 class="toggle border"><span class="edit_icon map"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                  <g:if test="${infotext['itext2'+context?.lang]}">
                    <div style="padding:0 20px 5px">
                      <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                    </div>
                  </g:if><!--noindex-->
                  <g:form name="addWaitingForm" url="${[action:'saveWaiting']}" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                    <input type="hidden" name="lang" value="${context?.lang?'en':''}"/>
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
                          <input type="text" class="mini" id="city_auto" name="city" maxlength="${stringlimit}" value="${inrequest?.city}">
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
                            <option <g:if test="${item?.id==(inrequest?.valuta_id?:context?.shown_valuta?.id)}">selected="selected"</g:if> value="${item?.id}">
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
                          + <input type="text" id="ind" name="ind" value="${ind?:''}" size="3" style="width:40px" />
                          ( <input type="text" id="kod" name="kod" value="${kod?:''}" size="7" style="width:50px" /> )
                          <input type="text" id="telef" name="telef" value="${telef?:''}" size="15" style="width:100px" />
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
                        <b><a href="javascript:void(0)" rel="nofollow" onclick="showLoginForm(3,'login_lbox',this);$('reg_lbox').hide()">${message(code:'login.enter')}</a></b>                         
                      </span>
                    </div>                    
                    <div style="padding:0 20px 5px">
                      <p>${message(code:'waiting.noauth')}</p>
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
                  </g:form><!--/noindex-->
                </g:if><g:else><!--noindex-->
                  <div class="notice">
                  <g:if test="${user && user?.modstatus!=1}">
                    <g:rawHtml>${message(code:'waiting.request.add.noverified',args:[user.email?:''])}</g:rawHtml> <g:link controller="profile" action="verifyUser">${message(code:'personal.verification')}</g:link>
                  </g:if><g:else>
                    <g:rawHtml>${message(code:'waiting.request.add')}</g:rawHtml>
                  </g:else>
                  </div><!--/noindex-->
                </g:else>
                </div>
              </td>
            </tr>
  </body>
</html>
