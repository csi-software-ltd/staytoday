<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <meta property="fb:app_id" content="${fb_api_key}" />
    <meta property="og:type" content="website" />
    <meta property="og:site_name" content="StayToday" />
    <meta property="og:locale" content="${context?.lang?'en_US':'ru_RU'}" />
    <meta property="og:locale:alternate" content="${context?.lang?'ru_RU':'en_US'}" />    
    <meta property="og:url" content="${createLink(uri:'',base:context?.mainserverURL_lang)}" />    
    <meta property="og:title" content="${infotext['title'+context?.lang]?:''}" />
    <meta property="og:description" content="${infotext['description'+context?.lang]?:''}" />    
    <meta property="og:image" content="${resource(dir:'images',file:'logo_large.png',absolute:true)}" />    
    <meta name="twitter:card" content="summary" />
    <meta name="twitter:site" content="@StayTodayRu" />
    <meta name="twitter:domain" content="StayToday.ru" />
    <meta name="twitter:app:name:iphone" content="StayToday" />    
    <meta name="twitter:app:name:googleplay" content="StayToday" />
    <meta name="twitter:app:url:iphone" content="http://staytoday.ru/mobile" />    
    <meta name="twitter:app:url:googleplay" content="http://staytoday.ru/mobile" />
    <meta name="twitter:app:id:iphone" content="id595979996" />    
    <meta name="twitter:app:id:googleplay" content="ru.trace.staytoday" />    
    <link rel="canonical" href="${createLink(uri:'',base:context?.mainserverURL_lang)}" />
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(uri:'',base:context?.mobileURL_lang)}" />
    <link rel="alternate" media="handheld" href="${createLink(uri:'',base:context?.mobileURL_lang)}" />
    <link rel="image_src" href="${resource(dir:'images',file:'logo_large.png',absolute:true)}" />
    <meta name="layout" content="main" />
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/>
    <g:javascript> 
    var opened_popdirection=0, mouseOnDir=0;
    function onKey(e){
      var iKeycode;
      if (window.event) iKeycode = window.event.keyCode;
      else if (e) iKeycode = e.which;
      else return true;    
      if (iKeycode == 13)
        submitForm();
    }	
    function initialize() {        
      $('where').onkeypress=onKey;	  
      new Autocomplete('where', { serviceUrl:'${resource(dir:'home',file:'where_autocomplete')}' });        
      $("hometype_id").enable();
      $("homeperson_id").enable(); 
      $("date_start").enable();
      $("date_start_value").enable();      
      $("date_start_year").enable();
      $("date_start_month").enable();
      $("date_start_month").enable();	  
      $("date_start_day").enable();
      $("date_end").enable();
      $("date_end_value").enable();      
      $("date_end_year").enable();
      $("date_end_month").enable();
      $("date_end_month").enable();	  
      $("date_end_day").enable(); 
      $("where").enable();      
    }
    function disableFormParams(){
      $("where").disable();        
      if($("hometype_id").getValue()==0)
        $("hometype_id").disable();        
      if($("homeperson_id").getValue()==0)
        $("homeperson_id").disable();	        
      if($("date_start").getValue()=="")
        $("date_start").disable();        
      if($("date_start_value").getValue()=="")
        $("date_start_value").disable();	          
      if($("date_start_year").getValue()=="")
        $("date_start_year").disable();      
      if($("date_start_month").getValue()=="")
        $("date_start_month").disable();    
      if($("date_start_month").getValue()=="")
        $("date_start_month").disable();        
      if($("date_start_day").getValue()=="")
        $("date_start_day").disable();        
      if($("date_start_hour").getValue()=="")
        $("date_start_hour").disable();        
      if($("date_start_minute").getValue()=="")
        $("date_start_minute").disable();    
      if($("date_end").getValue()=="")
        $("date_end").disable();        
      if($("date_end_value").getValue()=="")
        $("date_end_value").disable();        
      if($("date_end_year").getValue()=="")
        $("date_end_year").disable();      
      if($("date_end_month").getValue()=="")
        $("date_end_month").disable();    
      if($("date_end_month").getValue()=="")
        $("date_end_month").disable();        
      if($("date_end_day").getValue()=="")
        $("date_end_day").disable();        
      if($("date_end_hour").getValue()=="")
        $("date_end_hour").disable();        
      if($("date_end_minute").getValue()=="")
        $("date_end_minute").disable();  
    }    
    function submitForm(){
      if($("where").value.length > 2){
        $("where").removeClassName('red0');
        $("where1").value = $("where").value;
        disableFormParams();	
        $("search_form").submit();	
      }else{
        $("where").addClassName("red0");
      }
    }    
    function processResponse(e){
      $('votingList').hide();
      $('votingResult').show();
    }    
    function togglePopDirection(iType){
      if ($("select_popdirection").style.display == 'none'){
        updatePopDirection(iType);
        opened_popdirection = 1;
      } else {
        jQuery("#select_popdirection").slideToggle();
        opened_popdirection = 0;
      }
    }
    function updatePopDirection(iType){
      <g:remoteFunction controller='index' action='selectpopcities' update='select_popdirection' onComplete="jQuery('#select_popdirection').slideDown()" params="\'country_id=\'+iType" />
    }
    function clickPopDirection(sWhere){
      <g:remoteFunction controller='home' action='cityselectstat'/>
      $('where1').value = sWhere;
      disableFormParams();
      $("search_form").submit();
    }
    function checkDropDowns(){
      if (opened_popdirection){
        if (!mouseOnDir){
          opened_popdirection = 0;
          $("select_popdirection").style.display = 'none';
        }
      }
    }
    function noticeClick(iId){
      <g:remoteFunction controller='index' action='noticeClick' params="\'id=\'+iId" />
    }    
    </g:javascript>
  </head>
  <body onload="initialize()">
            <tr style="height:140px">              
              <meta itemprop="url" content="${createLink(mapping:'mainpage',base:context?.mainserverURL_lang)}" />
              <td class="rent" width="250">
                <a rel="nofollow" onclick="<g:if test="${isLoginDenied}">showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>
              </td>
              <td width="730" colspan="3" class="search"><!--noindex-->
                <g:form name="search_form" mapping="${context?.lang?'en':''}" controller="home" action="search" method="get">
                  <table class="float" width="553" cellpadding="5" cellspacing="0" border="0">
                    <tr>
                      <td width="145" valign="top" class="padd10">
                        <label for="where">${message(code:'common.where')}</label>
                        <input type="text" id="where" name="wre" maxlength="${stringlimit}" value="" placeholder="${message(code:'common.where_default')}" autocomplete="off" style="width:145px" />
                        <div id="where_autocomplete" class="autocomplete" style="display:none"></div>			
                      </td>                  
                      <td width="107" valign="top">
                        <label for="hometype_id">${message(code:'common.home_type')}</label>
                        <select id="hometype_id" name="hometype_id" style="width:107px">
                          <option value="all" <g:if test="${0==(inrequest?.hometype_id?:0)}">selected="selected"</g:if>>${message(code:'common.any')}</option>
                        <g:each in="${hometype}" var="item">            
                          <option value="${item?.urlname}" <g:if test="${item?.id==inrequest?.hometype_id}">selected="selected"</g:if>>${item['name'+context?.lang]}</option>
                        </g:each>
                        </select>
                      </td>
                      <td width="43" valign="top">
                        <label>${message(code:'common.guests')}</label>
                        <select id="homeperson_id" name="homeperson_id" style="width:43px">
                        <g:each in="${homeperson}" var="item">            
                          <option value="${item?.id}" <g:if test="${item?.id==inrequest?.homeperson_id}">selected="selected"</g:if>>${item?.kol}</option>
                        </g:each>
                        </select>                  
                      </td>
                      <td width="85" valign="top">
                        <label>${message(code:'common.date_from')}</label>
                        <span class="dpicker">
                          <calendar:datePicker name="date_start" value="${inrequest?.date_start}" dateFormat="%d-%m-%Y"/>
                        </span>
                      </td>
                      <td width="85" valign="top" class="dpicker">
                        <label>${message(code:'common.date_to')}</label>
                        <span class="dpicker">
                          <calendar:datePicker name="date_end" value="${inrequest?.date_end}" dateFormat="%d-%m-%Y"/>                
                        </span>
                      </td>              
                    </tr>
                    <tr>
                      <td colspan="3" valign="top" class="padd10">
                        <div class="dropdown-list relative">                          
                          <small id="a_select_popdirection" class="select" onmouseover="javascript:mouseOnDir=1" onmouseout="javascript:mouseOnDir=0" onclick="togglePopDirection(1)"><a rel="nofollow" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0">${message(code:'common.choose_city')}</a></small>
                          <div class="select_dropdown" id="select_popdirection" style="display:none" onmouseover="javascript:mouseOnDir=1" onmouseout="javascript:mouseOnDir=0"></div>                          
                        </div>
                      </td>
                      <td colspan="2" align="center">
                        <div class="dropdown-list relative">
                          <small class="link"><a rel="nofollow" onclick="transit(${context.is_dev?1:0},['waiting'],'','','','${context?.lang}')">${message(code:'index.sendrequest')}</a></small>                          
                        </div>
                      </td>
                    </tr>
                  </table>
                  <input type="hidden" id="where1" name="where" value="" />
                </g:form><!--/noindex-->
                <a class="button main col" rel="nofollow" onclick="submitForm()">${message(code:'common.renthome')}</a>
              </td>
            </tr>
            <tr style="height:455px">
              <td width="250" valign="top">              
                <h2 class="padd20">${message(code:'index.pop_directions').capitalize()}</h2>
                <ul class="search_filter_content" style="padding:5px 10px 0 20px">
                <g:each in="${countries}" var="country" status="i"><g:if test="${country.id in countryIds}">                
                  <!--<li class="clearfix country">
                    <h3><span class="flagcon ${country?.icon}"></span>
                    <a rel="nofollow" onclick="transit(${context.is_dev?1:0},['${country?.urlname}'])">${country['name'+context?.lang]}</a></h3>
                  </li>-->
                  <g:each in="${popdirection}" var="record" status="j"><g:if test="${country.id==record.country_id}">
                  <li class="clearfix dott ${(j==0)?'first':''}">
                    <b><g:if test="${record.is_index}"><a class="show_more_link" href="<g:createLink controller='index' action='direction' id='${record.linkname}' params='${[country:country.urlname]}' base='${context?.mainserverURL_lang}'/>" rel="nofollow" itemprop="breadcrumb"></g:if>
                    <g:else><span class="show_more_link" onclick="transit(${context.is_dev?1:0},['${record.linkname}','${country.urlname}'],'','','','${context?.lang}')"></g:else>
                    ${record['name2'+context?.lang]}<g:if test="${record.is_index}"></a></g:if><g:else></span></g:else></b><br/>
                    <p class="desc">${record['name'+context?.lang]}</p>
                  </li>
                  </g:if></g:each>
                </g:if></g:each>    
                  <li class="clearfix dott last" style="text-align:right">
                    <a class="show_more_link" rel="nofollow" onclick="transit(${context.is_dev?1:0},['alldirections'],'','','','${context?.lang}')">${message(code:'index.all_directions')}</a> <font color="#FF530D">&raquo;</font>
                  </li>
                </ul>              
              </td><!--noindex-->
              <td colspan="3" class="slideshow" valign="top">
                <div id="slideshow" onmouseover="$('slideshow_controls').show()" onmouseout="$('slideshow_controls').hide()"></div>
                <g:javascript library="slideshow" />                
                <g:javascript>
                  var imges = [
                  <g:each in="${records}" var="home" status="i">      
                    {"price":"${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>",
                     "picture":"${urlphoto}${home?.client_id}/${home?.mainpicture}",
                     "shortaddress":"${home?.shortaddress}",
                     "reviews":"${home?.nref?:0} ${message(code:'common.reviews')}",
                     "name":"${home?.name}",
                     "id":${home?.id},
                     "linkname":"${home?.linkname}",
                     "country":"${!City.get(home.city_id)?.domain?Country.get(home?.country_id)?.urlname+'/':''}",
                     "city":"${!City.get(home.city_id)?.domain?home?.city+'/':''}",
                     "appname":"/",
                     "base":"${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id)?.domain+(context.is_dev?':8080/Arenda':'')+(context?.lang?'/en':''):context.mainserverURL_lang}",
                     "sutki":"${message(code:'list.per_night')}"
                    }<g:if test="${i!=records.size()-1}">,</g:if>
                  </g:each>
                  ];
                  var slideshow = new slideshow(imges,'slideshow',1,1,1,1);
                </g:javascript>
              </td><!--/noindex-->
            </tr>            
            <tr>            
              <td colspan="4" class="line text-topic">
                <h2 class="noline" itemprop="about">${infotext['promotext1'+context?.lang]?:''}</h2>
                <p itemprop="significantLinks"><g:each in="${tagcloud}" var="tag">
                  <g:if test="${tag.value.is_index}"><a href="<g:createLink mapping='${tag.value.domain?'mainpage'+context?.lang:'hSearch'+context?.lang}' params='${tag.value.domain?[:]:[where:tag.key,country:Country.get(tag.value.country_id)?.urlname]}' base='${tag.value.domain?'http://'+tag.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="tag" title="${message(code:'common.flats_per_night_in')} ${tag.value.name2}" style="font-size:${tag.value.count>tagcloudParams.maxFontCount?'20px':tag.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:if>
                  <g:else><span class="link" style="font-size:${tag.value.count>tagcloudParams.maxFontCount?'24px':tag.value.count>tagcloudParams.middleFontCount?'17px':'10px'};color:#3F5765" onclick="transit(${context.is_dev?1:0},['','${tag.key}','${Country.get(tag.value.country_id)?.urlname}'])"></g:else>
                  ${tag.key}<g:if test="${tag.value.is_index}"></a></g:if><g:else></span></g:else> 
                </g:each></p>                
                <div itemprop="mainContentOfPage">
                  <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                </div>              
              </td>
            </tr>            
            <tr>
              <td colspan="4" style="padding:10px 0">
                <ul class="homepage_badges">
                <g:each in="${specoffer_records}" var="home" status="i">
                  <li style="margin-left:${(i==0)?'21':'10'}px">
                    <div class="badge-content-container shadow dark pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
                      <img width="220" height="160" src="${home?.mainpicture?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                      <div class="slideshow_item_details">
                        <div class="slideshow_item_details_text">
                          <div class="ss_details_top">
                            <span class="ss_name" itemprop="name"><g:shortString text="${home?.name}" length="18"/></span><br/>
                            <span class="ss_location" itemprop="description"><g:shortString text="${home?.shortaddress}" length="22"/></span>
                          </div>                            
                        </div>
                      </div>
                      <div class="photo_item_details">
                        <span class="ss_price b-rub" itemprop="price">              
                          ${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>
                        </span>
                      </div>
                    </div>
                  </li>
                </g:each>
                </ul>
              </td>
            </tr>
            <tr>
              <td colspan="4" class="page-topic">                
                <h2 class="ins">${infotext['promotext2'+context?.lang]?:''}</h2>
                <div itemprop="description">
                  <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                </div>
                <div class="wherefore">
                  <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                </div><!--noindex-->
                <script type="text/javascript" src="//yandex.st/share/share.js" charset="utf-8"></script>
                <div align="right">
                  <span>${message(code:'social.share')}</span> 
                  <div class="yashare-auto-init" data-yashareL10n="ru" data-yashareType="none" data-yashareQuickServices="facebook,twitter,vkontakte,odnoklassniki,gplus,lj" data-yashareImage="${resource(dir:'images',file:'logo_large.png',absolute:true)}" style="display:inline-block"></div>
                </div><!--/noindex-->
              </td>
            </tr>
          <g:if test="${notice}"><!--noindex-->
            <tr>
              <td colspan="4" class="page-topic" style="padding:10px 0">
              <g:each in="${notice}" var="it" status="i">
                <div class="notice ads clearfix" style="margin-right:${(i%2)==0?'16':'0'}px" onclick="noticeClick(${it.id})">
                  <g:if test="${it?.is_index}"><a href="${serverUrl.toString()+'/'+it?.url}" title="${it['title'+context?.lang]}"></g:if>
                  <g:else><div class="link" onclick='transit(${context.is_dev?1:0},["${it?.url}"],"","","","${context?.lang}")'></g:else>
                    <img class="thumbnail userpic" src="${resource(dir:'images',file:(it?.image)?'anonses/'+it?.image:'user-default-picture.png')}" alt="${it['title'+context?.lang]}" border="0" />
                    <div class="description">
                      <h2 class="ins">${it['title'+context?.lang]}</h2>
                      <p>${it['description'+context?.lang]}</p>
                    </div>
                  <g:if test="${it?.is_index}"></a></g:if><g:else></div></g:else>
                </div>
                </g:each>
              </td>
            </tr><!--/noindex-->            
          </g:if><g:if test="${lastHome}">
            <tr>
              <td colspan="4" style="padding-top:15px">
                <h2 class="padd20">${message(code:'label.recently')}</h2>
                <ul class="homepage_badges" style="background:#ccc">
                <g:each in="${lastHome}" var="home" status="i">
                  <li style="margin-left:${(i==0)?'21':'10'}px">
                    <div class="badge-content-container shadow dark pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}">
                      <img width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                      <div class="slideshow_item_details">
                        <div class="slideshow_item_details_text">
                          <div class="ss_details_top">
                            <span class="ss_name"><g:shortString text="${home?.name}" length="18"/></span><br/>
                            <span class="ss_location"><g:shortString text="${home?.shortaddress}" length="22"/></span>
                          </div>
                        </div>
                      </div>
                      <div class="photo_item_details">
                        <span class="ss_price b-rub">${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></span>
                      </div>
                    </div>
                  </li>
                </g:each><g:if test="${lastHome.size()<4}"><g:each var="i" in="${(1..4-lastHome.size())}">
                  <li>
                    <div class="badge-content-container shadow dark">
                      <img width="220" height="160" src="${resource(dir:'images',file:'default-picture.png')}" alt="" border="0" />                      
                    </div>
                  </li>                  
                </g:each></g:if>
                </ul>
              </td>
            </tr>
          </g:if>
  </body>
</html>
