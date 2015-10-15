<html>
  <head>    
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl}" />    
    <meta name="layout" content="main"/>
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
    function initialize(){    
      textCounter('title','title_limit',35);
      revertDateRange();
      new Ajax.Autocompleter1("street_auto","region_id","street_autocomplete",
        "${resource(dir:'home',file:'street_autocomplete')}",{});
      new Ajax.Autocompleter1("city_auto","region_id","city_autocomplete",
        "${resource(dir:'home',file:'city_autocomplete')}",{});
      new Ajax.Autocompleter1("district_auto","region_id","district_autocomplete",
        "${resource(dir:'home',file:'district_autocomplete')}",{});
    }    
    function toggleFilter(container){ 
      var li = $(container);      
      if(li.className == 'search_filter')
        li.className = 'search_filter closed open';
      else if(li.className == 'search_filter closed open')
        li.className = 'search_filter'; 
      else if(li.className == 'search_filter closed')
        li.className = 'search_filter open';
      else if(li.className == 'search_filter open')
        li.className = 'search_filter closed';      
    } 
    function textCounter(sId,sLimId,iMax){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iMax){
        symbols = symbols.substring(0,iMax);
        $(sId).value = symbols;
        return false;
      }
      $(sLimId).value = iMax-len;
    }    
    function revertDateRange(){
      if($("term").checked)
        $("date_range").show();		 
      else
        $("date_range").hide();	    
    }    
    function updateRegion(lCountryId){
      <g:remoteFunction controller='home' action='region' update='region_id' params="\'countryId=\'+lCountryId+'&lang=${context?.lang}'" />
    }  
    function clearForm(){
      setTimeout("textCounter('title','title_limit',35)",100);
    }    
    function validateEmail(email) {
      if (email.length > 0) {
        var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
        if (reg.test(email) == false) {
          alert('Неверный формат поля e-Mail');
          return false;
        }
      }
      return true;
    }    
    function sendForm(){ 
    <g:if test="${!user}">  
      if(validateEmail($F("email")))
    </g:if>
      $("homeForm").submit();			 	  
    }
    function addReloadCaptcha(){
      <g:remoteFunction controller='index' action='reloadCaptcha' onSuccess='processRlResponse(e)'/>
    }
    function processRlResponse(e){
      $('add_captcha_picture').innerHTML = e.responseJSON.captcha;
      $('add_captcha_picture').firstChild.setStyle({width: '120px'});
    }
    </g:javascript>
  </head>
  <body onload="initialize()">
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">
                <g:link class="button" uri="/cities" base="${context?.mainserverURL_lang}">${message(code:'common.renthome')}</g:link>
              </td>
              <td colspan="3" class="rent ss">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>
              </td>
            </tr>
            <tr>              
              <td colspan="3" class="bg_shadow">              
                <div class="breadcrumbs" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" rel="v:url" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span>
                  ${infotext['header'+context?.lang]?:''}
                </div>
              </td>
            </tr>            
            <tr>
              <td valign="top">
                <ul class="collapsable_filters">      
                  <li class="search_filter" id="lesson_container">                 
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('lesson_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('lesson_container');">${message(code:'common.lessons')}</a>
                    <ul class="search_filter_content">
                    <g:each in="${lessons}" var="item" status="i">
                      <li class="clearfix">
                        <g:javascript>
                        jQuery(document).ready(function(){
                          jQuery('a[name="video_${item?.id}"]').colorbox({
                            inline: true,                            
                            href: '#video_lbox_${item?.id}',
                            scrolling: false,
                            onLoad: function(){
                              jQuery('#video_lbox_${item?.id}').show();          
                            },
                            onCleanup: function(){
                              jQuery('#video_lbox_${item?.id}').hide();
                            }
                          });                                
                        });
                        </g:javascript>              
                        <div class="thumbnail shadow dark relative">                          
                          <a name="video_${item?.id}" rel="nofollow" title="${item?.name}">
                            <img width="200" height="140" border="0" src="${urlvideo+item?.picture}" border="0" />
                            <span class="video_arrow"></span>
                          </a>
                        </div>
                        <div class="description">
                          <h2 class="title"><a class="name" name="video_${item?.id}" rel="nofollow">${item?.name}</a></h2>                          
                        </div>                        
                        <div id="video_lbox_${item?.id}" class="new-modal" style="width:810px;height:658px;display:none">
                          <h2 class="clearfix">${item?.name}</h2>
                          <div class="segment nopadding">
                            <object class="flashfox" width="800" height="600" data="${urlvideo}flashfox.swf" type="application/x-shockwave-flash">
                              <param name="movie" value="${urlvideo}flashfox.swf" />
                              <param name="wmode" value="transparent" />
                              <param name="allowfullscreen" value="true" />
                              <param name="flashvars" value="autoplay=true&controls=true&src=${urlvideo+item?.video}" />
                              <embed width="800" height="600" type="application/x-shockwave-flash" quality="high" src="${urlvideo+item?.video}" />                              
                            </object>                            
                          </div>
                        </div>
                        
                      </li>
                    </g:each>
                    </ul>
                  </li>
                  <li class="search_filter" id="popdirection_container">                 
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('popdirection_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('popdirection_container');">${message(code:'common.directions')}</a>
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
                  </li>
                </ul>
              </td>
              <td colspan="3" valign="top">
              <g:if test="${flash?.error?:[]}">
                <div class="notice">
                  <ul>
                  <g:each in="${flash?.error}"> 
                    <g:if test="${it==1}"><li>${message(code:'error.blank',args:[message(code:'common.home.name').capitalize()])}</li></g:if>
                    <g:if test="${it==2}"><li>${message(code:'error.blank',args:[message(code:'common.home.desc').capitalize()])}</li></g:if>
                    <g:if test="${it==3}"><li>${message(code:'add.single').capitalize()}: ${message(code:'error.blank',args:[message(code:'ads.price.date_start')])}</li></g:if>
                    <g:if test="${it==4}"><li>${message(code:'add.single').capitalize()}: ${message(code:'error.blank',args:[message(code:'ads.price.date_end')])}</li></g:if>
                    <g:if test="${it==20}"><li>${message(code:'add.single').capitalize()}: ${message(code:'ads.error.price.date',args:[message(code:'ads.price.date_start'),message(code:'ads.price.date_end')])}</li></g:if>
                    <g:if test="${it==5}"><li>${message(code:'error.blank',args:[message(code:'list.filtr.price').capitalize()])}</li></g:if>
                    <g:if test="${it==6}"><li>${message(code:'error.blank',args:["Email"])}</li></g:if>
                    <g:if test="${it==7}"><li>${message(code:'error.incorrect',args:["Email"])}</li></g:if>
                    <g:if test="${it==8}"><li>${message(code:'error.incorrect',args:[message(code:'ads.postindex')])}</li></g:if>
                    <g:if test="${it==9}"><li>${message(code:'error.blank',args:[message(code:'ads.postindex')])}</li></g:if>
                    <g:if test="${it==10}"><li>${message(code:'ads.error.handbooks')}</li></g:if>
                    <g:if test="${it==11}"><li>${message(code:'add.error.terms')}</li></g:if>                     
                    <g:if test="${it==14}"><li>${message(code:'error.blank',args:[message(code:'ads.city').capitalize()])}</li></g:if>
                    <g:if test="${it==99}"><li id="wrong_code">${message(code:'add.error.incorrect.code')}</li></g:if>
                    <g:if test="${it==102}"><li>${message(code:'add.error.senddata')}</li></g:if>		
                  </g:each>
                  </ul>
                </div>
              </g:if>
              <g:if test="${(flash?.warning?:[]).size()>0}">
                <div class="notice">
                  <ul><g:each in="${flash?.warning}"> 
                    <g:if test="${it==12 && !user}"><li>${message(code:'add.error.need')} <a href="javascript:void(0)" onclick="showLoginForm(1,'login_lbox',this)">${message(code:'login.social')}</a> ${message(code:'register.or')} ${message(code:'add.error.change.email')}.</li></g:if>
                    <g:if test="${it==12 && user}"><li>${message(code:'add.error.need')} ${message(code:'add.error.change.email')}</li></g:if>
                    <g:if test="${it==13 && !user}"><li id="wrong_email">${message(code:'add.error.need')} <a class="icon none" href="javascript:void(0)" onclick="showLoginForm(1,'login_lbox',this)">${message(code:'login.account')}</a> ${message(code:'register.or')} ${message(code:'add.error.change.email')}</li></g:if>
                    <g:if test="${it==13 && user}"><li>${message(code:'add.error.need')} ${message(code:'add.error.change.email')}</li></g:if>
                  </g:each></ul>
                </div>
              </g:if>
              
                <div class="form shadow" style="padding:5px 15px 5px 20px">
                <g:if test="${infotext['itext'+context?.lang]}">
                  <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                  <div id="rules" class="answer" style="display:none">
                    <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                  </div>
                  <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                </g:if>
                <g:form name="homeForm" url="${[action:'addhome']}" method="post" useToken="true" style="margin-top:20px" base="${context?.absolute_lang}">
                  <h2 class="padd20">${message(code:'ads.type').toLowerCase()}</h2>   
                  <g:if test="${infotext['promotext1'+context?.lang]}">
                    <p class="padd20">${infotext['promotext1'+context?.lang]?:''}</p>
                  </g:if>                     
                  <table width="100%" cellpadding="3" cellspacing="0" border="0">
                    <tr>
                      <td>
                        <label for="hometype_id">${message(code:'common.home_type')}</label>		
                        <select id="hometype_id" name="hometype_id" class="mini">
                        <g:each in="${hometype}" var="item">            
                          <option <g:if test="${item?.id==inrequest?.hometype_id}">selected="selected"</g:if> value="${item?.id}">
                            ${item['name'+context?.lang]}
                          </option>
                        </g:each>
                        </select>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <label for="homeperson_id">${message(code:'common.homeperson')}</label>		
                        <select id="homeperson_id" name="homeperson_id" class="mini">
                        <g:each in="${homeperson}" var="item">            
                          <option <g:if test="${item?.id==inrequest?.homeperson_id}">selected="selected"</g:if> value="${item?.id}">
                            ${item['name'+context?.lang]}
                          </option>
                        </g:each>
                        </select>
                      </td>
                    </tr>
                  </table><br/><br/>
                  <h2 class="padd20">${message(code:'ads.desc').toLowerCase()}</h2>                  
                  <g:if test="${infotext?.promotext2}">
                    <p class="padd20">${infotext['promotext2'+context?.lang]?:''}</p>
                  </g:if>   
                  <table width="100%" cellpadding="3" cellspacing="0" border="0">                    
                    <tr>
                      <td valign="top" nowrap>
                        <label for="title">${message(code:'common.home.name')}</label>		
                        <input type="text" id="title" maxlength="40" name="title" <g:if test="${(flash?.error?:[]).contains(1)}">class="red"</g:if> size="35" value="${inrequest?.title}" onkeydown="textCounter(this.id,'title_limit',35);" onkeyup="textCounter(this.id,'title_limit',35);" />
                        <span class="padd10">${message(code:'ads.characters.left')} <input type="text" class="limit" id="title_limit" name="title_limit" readonly /></span>  
                      </td>
                    </tr>
                    <tr>
                      <td valign="top">
                        <label for="description">${message(code:'common.home.desc')}</label>		
                        <g:textArea class="${(flash?.error?:[]).contains(2)?'red':''}" id="description" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" name="description" value="${inrequest?.description}" rows="5" cols="40" />
                      </td>
                    </tr>
                    <tr>
                      <td valign="middle">
                        <label for="term">${message(code:'add.single')}</label>		
                        <input type="checkbox" id="term" name="term" <g:if test="${inrequest?.term}">checked</g:if> onclick="revertDateRange()"/>              
                        <a class="tooltip" href="javascript:void(0)" title="${message(code:'add.single.alt')}">
                          <img alt="${message(code:'add.single.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
                        </a>
                      </td>
                    </tr>	    
                    <tr>
                      <td> 
                        <div id="date_range" style="display:none">
                          <label for="date_start">${message(code:'ads.price.date_start')}</label>
                          <calendar:datePicker name="date_start" value="${inrequest?.date_start}" dateFormat="%d-%m-%Y"/>	 
                          <label for="date_end" style="float:none;min-width:100px">${message(code:'ads.price.date_end')}</label>
                          <calendar:datePicker name="date_end" value="${inrequest?.date_end}" dateFormat="%d-%m-%Y"/>
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <label for="price">${message(code:'ads.price.price_per_day')}</label>
                        <input type="text" id="price" name="price" class="price" value="${inrequest?.price}" <g:if test="${(flash?.error?:[]).contains(5)}">class="red"</g:if>>		  	
                        <select id="valuta_id" name="valuta_id" class="mini">
                        <g:each in="${valuta}" var="item">            
                          <option <g:if test="${item?.id==inrequest?.valuta_id}">selected="selected"</g:if> value="${item?.id}">
                            <g:rawHtml>${item?.symbol?:''}</g:rawHtml>&nbsp;&nbsp;${item?.code}                            
                          </option>
                        </g:each>
                        </select>
                      </td>
                    </tr>
                  </table><br/><br/>
                  <h2 class="padd20">${message(code:'add.contact')}</h2>
                  <p class="padd20">${message(code:'add.contact.info')}</p>                  
                  <table width="100%" cellpadding="3" cellspacing="0" border="0">
                  <g:if test="${!user||(user?.provider!='staytoday' && user?.modstatus!=1)}">
                    <tr id="tr_email">
                      <td>
                        <label for="email">email</label>		
                        <input type="text" id="email" maxlength="${stringlimit}" name="email" value="${inrequest?.email}" <g:if test="${(flash?.error?:[]).contains(6)||(flash?.error?:[]).contains(7)||(flash?.error?:[]).contains(12)||(flash?.error?:[]).contains(100)}">class="red"</g:if>>
                      </td>
                    </tr>
                  </g:if> 
                    <tr>
                      <td>
                        <label for="country_id">${message(code:'ads.country')}</label>		
                        <select id="country_id" name="country_id" onchange="updateRegion(this.value)">
                        <g:each in="${country}" var="item">            
                          <option <g:if test="${item?.id==inrequest?.country_id}">selected="selected"</g:if> value="${item?.id}">
                            ${item['name'+context?.lang]}
                          </option>
                        </g:each>
                        </select>
                        <g:if test="${!inrequest?.country_id}"><script type="text/javascript">$('country_id').selectedIndex = 0;</script></g:if>
                      </td>
                    </tr>
                    <tr>
                      <td nowrap>
                        <label for="region_id">${message(code:'ads.region')}</label>		                        
                      <g:if test="${region}">
                        <select id="region_id" name="region_id">
                        <g:each in="${region}" var="item">            
                          <option <g:if test="${item?.id==inrequest?.region_id}">selected="selected"</g:if> value="${item?.id}">
                            ${item['name'+context?.lang]}
                          </option>
                        </g:each>
                        </select>
                      </g:if>                        
                      </td>		
                    </tr>
                    <tr>
                      <td valign="middle">
                        <label for="city">${message(code:'ads.city')}</label>
                        <input name="city" id="city_auto" maxlength="${stringlimit}" type="text" value="${inrequest?.city}" <g:if test="${(flash?.error?:[]).contains(14)}">class="red"</g:if> />
                        <div id="city_autocomplete" class="autocomplete" style="display:none"></div>
                        <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.city.alt')}">
                          <img alt="${message(code:'ads.city.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
                        </a>
                      </td>			
                    </tr>
                    <tr>
                      <td valign="middle">
                        <label for="district">${message(code:'ads.district')}</label>					 
                        <input name="district" id="district_auto" maxlength="${stringlimit}" type="text" value="${inrequest?.district}" />
                        <div id="district_autocomplete" class="autocomplete" style="display:none"></div>
                        <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.district.alt')}">
                          <img alt="${message(code:'ads.district.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
                        </a>
                      </td>		
                    </tr>
                    <tr>
                      <td>
                        <label for="pindex">${message(code:'ads.postindex')}</label>		
                        <input type="text" id="pindex" maxlength="${stringlimit}" name="pindex" value="${inrequest?.pindex}" class="mini <g:if test="${(flash?.error?:[]).contains(8)||(flash?.error?:[]).contains(9)}">red</g:if>">		  	
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <label for="street">${message(code:'ads.street')}</label>				
                        <input name="street" id="street_auto" maxlength="${stringlimit}" type="text" value="${inrequest?.street}" />
                        <div id="street_autocomplete" class="autocomplete" style="display:none"></div>
                        <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.street.alt')}">
                          <img alt="${message(code:'ads.street.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
                        </a>
                      </td>		
                    </tr>		  		
                    <tr>
                      <td>
                        <label for="homenumber">${message(code:'ads.homenumber')}</label>		
                        <input type="text" id="homenumber" maxlength="${stringlimit}" name="homenumber" value="${inrequest?.homenumber}" class="mini" />
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:15px 3px 15px 200px">
                        <input type="checkbox" id="usage" name="usage" <g:if test="${inrequest?.usage}">checked</g:if>>	  	
                        <b class="${(flash?.error?:[]).contains(11)?'red':''}"><g:link controller="index" action="terms" target="_blank" base="${context?.absolute_lang}">${message(code:'add.terms.confirm')}</g:link></b>
                      </td>
                    </tr>	 
                  </table>
                  <table cellpadding="3" cellspacing="0" border="0">
                  <g:if test="${!user}">                  
                    <tr style="height:50px">
                      <td>
                        <h2 class="padd20" style="margin-bottom:0">${message(code:'add.code')}</h2>
                        <small class="padd20">${message(code:'add.code.info')}</small>                        
                      </td>
                      <td width="100" align="center" valign="middle" id="add_captcha_picture">
                        <jcaptcha:jpeg name="image" width="120"/>
                      </td>
                      <td valign="middle">                        
                        <input type="text" name="captcha" class="mini <g:if test="${(flash?.error?:[]).contains(99)}">red</g:if>" value=""/>
                        <img src="${resource(dir:'images',file:'reload.png')}" onclick="addReloadCaptcha();" alt="${message(code:'captcha.update')}" title="${message(code:'captcha.update')}" border="0" align="absmiddle" style="margin-left:15px"/>
                      </td>
                    </tr>
                  </g:if>
                    <tr>
                      <td width="195">&nbsp;</td>
                      <td colspan="2" style="padding: 45px 3px">
                        <input type="button" class="button-glossy orange" value="${message(code:'button.add')}" onclick="sendForm()"/>
                        <input type="reset" class="button-glossy grey" value="${message(code:'button.reset')}" onClick="clearForm()"/>
                      </td>
                    </tr>
                  </table>
                </g:form>
                </div>
              </td>
            </tr>
   
  </body>
</html>
