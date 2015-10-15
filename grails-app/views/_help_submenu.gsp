            <tr style="height:110px">
              <td width="250" class="search">
                <a class="button" rel="nofollow" onclick="<g:if test='${isLoginDenied}'>showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>              
              </td>
              <td colspan="3" class="rent ss">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr height="30">
              <td class="bg_shadow">              
                <div class="topl relative"><g:if test="${actionName!='guestbook'}">
                  <span class="count" style="top:0">
                    <b>${message(code:'faq.records.found')}</b><span id="ads_count"></span>
                  </span>
                </g:if></div>
              </td>
              <td colspan="3" class="bg_shadow">
                <div class="breadcrumbs" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">                
                    <a href="${createLink(uri:'',base:context?.absolute_lang)}" rel="v:url" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span><span typeof="v:Breadcrumb">
                    <span property="v:title">${message(code:'label.help')}</span> &#8594;
                  </span>
                  <g:if test="${!(actionName in ['faq','guestbook'])}"><span typeof="v:Breadcrumb">                    
                    <span property="v:title"><g:if test="${actionName.substring(0,7)=='hosting'}">${Infotext.findByControllerAndAction('help','hosting')['name'+context?.lang]}</g:if>
                    <g:elseif test="${actionName.substring(0,9)=='traveling'}">${Infotext.findByControllerAndAction('help','traveling')['name'+context?.lang]}</g:elseif></span> &#8594;
                  </span></g:if>
                  ${infotext['header'+context?.lang]?:''}
                </div>
              </td>
            </tr>
            <tr>
              <td width="250" valign="top">
              <g:if test="${!(actionName in ['faq','guestbook'])}">
                <div class="header-list-item shadow" style="padding:0;margin-bottom:20px">
                  <div class="header-dropdown personal" style="top:0">
                    <div class="header-dropdown-section">
                      <ul id="nav" class="header-dropdown-list">
                      <g:each in="${help_submenu}" var="item">   
                        <li class="header-dropdown-list-item">
                          <g:if test="${controllerName==item?.controller && actionName==item?.action}"><b class="active link"></g:if><g:else><a href="<g:createLink controller='${item?.controller}' action='${item?.action}' base='${context?.absolute_lang}'/>"></g:else>
                            ${item['name'+context?.lang]}
                          <g:if test="${controllerName==item?.controller && actionName==item?.action}"></b></g:if><g:else></a></g:else>
                        </li>
                      </g:each>
                      </ul>
                    </div>
                  </div>
                </div>              
              </g:if>
                <ul class="collapsable_filters">
                <g:if test="${actionName=='faq'}">                
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
                        <div class="thumbnail shadow relative">                          
                          <a name="video_${item?.id}" title="${item?.name}" rel="nofollow">
                            <img width="200" height="140" border="0" src="${urlvideo+item?.picture}" border="0" />
                            <span class="video_arrow"></span>
                          </a>
                        </div>
                        <div class="description clearfix">
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
                </g:if><g:if test="${citycloud}">                
                  <li class="search_filter" id="cities_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('cities_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('cities_container');">${message(code:'common.cities')}</a> 
                    <div class="search_filter_content" style="line-height:20px">                    
                      <g:each in="${citycloud}" var="city">
                        <g:if test="${city.value.is_index}"><a rel="tag" href="<g:createLink mapping='${city.value.domain?('mainpage'+context?.lang):('hSearch'+context?.lang)}' params='${city.value.domain?[:]:[where:city.key,country:Country.get(city.value.country_id)?.urlname]}' base='${city.value.domain?'http://'+city.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${message(code:'common.flats_per_night_in')} ${city.value.name2}" style="font-size:${city.value.count>citycloudParams.maxFontCount?'20px':city.value.count>citycloudParams.middleFontCount?'15px':'10px'};color:#3F5765">${city.key}</a></g:if>
                        <g:else><span class="link" style="font-size:${city.value.count>citycloudParams.maxFontCount?'20px':city.value.count>citycloudParams.middleFontCount?'15px':'10px'};color:#3F5765" onclick="transit(${context.is_dev?1:0},['${city.key}','${Country.get(city.value.country_id)?.urlname}'],'','','','${context?.lang}')">${city.key}</span></g:else>                      
                      </g:each>
                    </div>                    
                  </li>
                </g:if>
                </ul>              
              </td>
              <td colspan="3" valign="top">
                <div class="form shadow">              
                  <div class="content" style="min-height:547px">              
                    <ul class="subnav">
                    <g:each in="${helpmenu}" var="item" status="i">
                      <g:if test="${i<2 && item?.relatedpages}">                      
                      <li class="${(controllerName==item.controller && actionName in item.relatedpages.split(','))?'selected':''}">
                        <g:if test="${!(controllerName==item.controller && actionName in item?.relatedpages.split(','))}"><a href="${createLink(controller:item.controller,action:item.action+'_basic',base:context?.mainserverURL_lang)}"></g:if>
                          ${item['name'+context?.lang]}
                        <g:if test="${!(controllerName==item.controller && actionName in item?.relatedpages.split(','))}"></a></g:if>
                      </li>
                      </g:if><g:else>
                      <li class="${(controllerName==item.controller && actionName==item.action)?'selected':''}">
                        <g:if test="${!(controllerName==item.controller && actionName==item.action)}"><a href="${createLink(controller:item.controller,action:item.action,base:context?.mainserverURL_lang)}"></g:if>
                          ${item['name'+context?.lang]}
                        <g:if test="${!(controllerName==item.controller && actionName==item.action)}"></a></g:if>
                      </g:else>
                    </g:each>                      
                    </ul>                
                    <div id="layers" class="dashboard-content">
                      <div rel="layer" ${(actionName=='guestbook')?"id=book":"style='padding:10px 10px'"}>
