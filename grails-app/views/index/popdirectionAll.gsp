<html>
  <head>
  <g:if test="${breadcrumbs?.country}">
    <title>${breadcrumbs?.country[context?.lang?'name'+context?.lang:'title']?:''}</title>    
    <meta name="keywords" content="${context?.lang?'':breadcrumbs?.country.keywords?:''}" />
    <meta name="description" content="${breadcrumbs?.country['description'+context?.lang]?:''}" />
    <meta property="fb:app_id" content="${fb_api_key}" />
    <meta property="og:type" content="object" />
    <meta property="og:site_name" content="StayToday" />
    <meta property="og:locale" content="${context?.lang?'en_US':'ru_RU'}" />
    <meta property="og:locale:alternate" content="${context?.lang?'ru_RU':'en_US'}" />
    <meta property="og:url" content="${createLink(controller:'index',action:'popdirectionAll',params:[id:breadcrumbs?.country?.urlname],base:context?.mainserverURL_lang)}" />		
    <meta property="og:title" content="${breadcrumbs?.country[context?.lang?'name'+context?.lang:'title']?:''}" />
    <meta property="og:description" content="${breadcrumbs?.country['description'+context?.lang]?:''}" />
    <meta property="og:image" content="${popdirection[0]?.picture?imageurl+'t_'+popdirection[0]?.picture:''}" />
    <link rel="canonical" href="${createLink(controller:'index',action:'popdirectionAll',params:[id:breadcrumbs?.country?.urlname],base:context?.mainserverURL_lang)}" />		
  </g:if><g:else>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${context?.lang?'':infotext?.keywords?:''}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl}" />    
  </g:else>
    <meta name="layout" content="main" />
    <g:javascript>
      function noticeClick(iId){
        <g:remoteFunction controller='index' action='noticeClick' params="\'id=\'+iId" />
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
      jQuery('.search_filter_content li.dott:last').css('border-bottom','none');
    </g:javascript>
    <style type="text/css">p.entry-content { height: 70px; line-height: 15px; margin: 5px 0 0;</style>
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">
                <a class="button" rel="nofollow" onclick="<g:if test='${isLoginDenied}'>showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>
              </td>
              <td width="730" colspan="3" class="rent ss">
                <h1 class="header">
                  <g:if test="${breadcrumbs?.country}"><span class="flagcon ${breadcrumbs?.country.icon}"></span>${breadcrumbs?.country['name'+context?.lang]?:''}</g:if>
                  <g:else>${infotext['header'+context?.lang]?:''}</g:else>
                </h1>              
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
                      <span itemprop="name"><g:if test="${breadcrumbs?.country}">${breadcrumbs?.country['name'+context?.lang]?:''}</g:if><g:else>${infotext['name'+context?.lang]?:''}</g:else></span>
                    </span><meta itemprop="position" content="2" />
                  </li>
                </ul>
              </td>
            </tr>            
            <tr>
              <td valign="top">
                <ul class="collapsable_filters">
                <g:if test="${citycloud && !breadcrumbs?.country}">
                  <li class="search_filter" id="cities_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('cities_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('cities_container');">${message(code:'common.cities')}</a> 
                    <div class="search_filter_content" style="line-height:20px">                    
                      <g:each in="${citycloud}" var="city">
                        <g:if test="${city.value.is_index}"><a rel="tag" href="<g:createLink mapping='${city.value.domain?'mainpage'+context?.lang:'hSearch'+context?.lang}' params='${city.value.domain?[:]:[where:city.key,country:Country.get(city.value.country_id)?.urlname]}' base='${city.value.domain?'http://'+city.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${message(code:'common.flats_per_night_in')} ${city.value.name2}" style="font-size:${city.value.count>citycloudParams.maxFontCount?'20px':city.value.count>citycloudParams.middleFontCount?'15px':'10px'};color:#3F5765">${city.key}</a></g:if>
                        <g:else><span class="link" style="font-size:${city.value.count>citycloudParams.maxFontCount?'20px':city.value.count>citycloudParams.middleFontCount?'15px':'10px'};color:#3F5765" onclick="transit(${context.is_dev?1:0},['type_flats','${city.key}','${Country.get(city.value.country_id)?.urlname}'],'','','','${context?.lang}')">${city.key}</span></g:else>                      
                      </g:each>
                    </div>                    
                  </li>
                </g:if><g:if test="${tagcloud}">
                  <li class="search_filter" id="tags_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('tags_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('tags_container');">${message(code:'common.tags')}</a>        
                    <ul class="search_filter_content">
                    <g:each in="${tagcloud}" var="tag">
                      <li style="display:inline-block">
                        <g:link mapping="${'timeline'+context?.lang}" params="${[tag:tag.key]}" style="font-size:${tag.value>tagcloudParams.maxFontCount?'20px':tag.value>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765">${tag.key}</g:link>
                      </li>
                    </g:each>
                    </ul>
                  </li>
                </g:if><g:if test="${breadcrumbs?.country && specoffer_records}">                  
                  <li class="search_filter" id="specoffer_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('specoffer_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('specoffer_container');">${message(code:'common.special')}</a>        
                    <ul class="search_filter_content" style="padding:15px 6px">
                    <g:each in="${specoffer_records}" var="home" status="i">
                      <li class="hlisting offer-rent clearfix">
                        <div class="item housing clearfix">
                          <div class="thumbnail shadow" style="width:220px;height:160px">
                            <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.findByName(home.city)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.findByName(home.city)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.findByName(home.city)?.domain?'http://'+City.findByName(home.city).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                            <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <img class="photo" width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0"/>
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                          </div>
                          <div class="photo_item_details">
                            <span class="price ss_price b-rub">${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></span>
                          </div>
                        </div>
                        <div class="fn title">
                          <g:if test="${home.is_index}"><a class="url name" href="<g:createLink mapping='${City.findByName(home.city)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.findByName(home.city)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.findByName(home.city)?.domain?'http://'+City.findByName(home.city).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="self bookmark" title="${home?.name}"></g:if>
                          <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                            <g:shortString text="${home?.name}" length="18"/>
                          <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                        </div>
                        <div class="description">
                          <ul class="details-list">              
                            <li class="details-list-item location">
                              <g:shortString text="${home?.shortaddress}" length="30"/>                  
                            </li>
                          </ul>
                        </div>
                        <div class="lister fn">${User.findWhere(client_id:home?.client_id).nickname}</div>
                      </li>
                    </g:each>
                    </ul>
                  </li>
                </g:if><g:if test="${anotherCountries?:0}">
                  <li class="search_filter" id="countries_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('countries_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('countries_container');">${message(code:'common.countries')}</a>        
                    <ul class="search_filter_content">
                    <g:each in="${anotherCountries}" var="country" status="i">
                      <li class="clearfix country dott ${(i+1==anotherCountries.size())?'last':''}">
                        <b><span class="flagcon ${country?.icon}"></span><g:link controller="index" action="popdirectionAll" params="[id:country?.urlname]" base="${context?.mainserverURL_lang}">${country['name'+context?.lang]}</g:link></b>
                      </li>
                    </g:each>
                    </ul>
                  </li>
                </g:if><g:if test="${breadcrumbs?.country && lastHome}">
                  <li class="search_filter" id="history_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('history_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('history_container');">${message(code:'label.recently_sm')}</a>
                    <ul class="search_filter_content" style="padding:15px 6px">
                    <g:each in="${lastHome}" var="home" status="i">                    
                      <li class="clearfix">
                        <div class="relative">
                          <div class="thumbnail shadow" style="width:220px;height:160px">
                            <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.findByName(home.city)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.findByName(home.city)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.findByName(home.city)?.domain?'http://'+City.findByName(home.city).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                            <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <img width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                          </div>                        
                          <div class="photo_item_details">
                            <span class="ss_price b-rub">${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></span>
                          </div>                        
                        </div>
                        <div class="description">
                          <h2 class="title">
                            <g:if test="${home.is_index}"><a class="name" href="<g:createLink mapping='${City.findByName(home.city)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.findByName(home.city)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.findByName(home.city)?.domain?'http://'+City.findByName(home.city).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                            <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <g:shortString text="${home?.name}" length="18"/>
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                          </h2>
                          <ul class="details-list" style="clear:left;width:220px">              
                            <li class="details-list-item location">
                              <g:shortString text="${home?.shortaddress}" length="30"/>                  
                            </li>
                          </ul>
                        </div>
                      </li>
                    </g:each>                    
                    </ul>                    
                  </li>
                </g:if>                  
                </ul>               
              </td>
              <td colspan="3" valign="top">
                <div class="search_container">
                  <div id="search_body" class="body shadow">
                    <div class="page-topic" style="${breadcrumbs?.country?'border-bottom:none':''}">
                      <g:rawHtml><g:if test="${breadcrumbs?.country}">${breadcrumbs?.country['itext'+context?.lang]?:''}</g:if>
                      <g:elseif test="${infotext['itext'+context?.lang]}">${infotext['itext'+context?.lang]?:''}</g:elseif></g:rawHtml>
                    </div>
                <g:if test="${!breadcrumbs?.country}">                    
                    <ul class="search_filter_content">
                    <g:each in="${countries}" var="country"><g:if test="${country.id in countryIds}">                        
                      <li class="clearfix country">
                        <h3><span class="flagcon ${country?.icon}"></span>
                        <g:if test="${country.is_index}"><a href="<g:createLink controller='index' action='popdirectionAll' params='[id:country?.urlname]' base='${context?.mainserverURL_lang}'/>"></g:if>
                        <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${country?.urlname}'],'','','','${context?.lang}')"></g:else>
                        ${country['name'+context?.lang]}<g:if test="${country.is_index}"></a></g:if><g:else></span></g:else></h3>
                      </li>
                      <g:each in="${popdirection}" var="record" status="i"><g:if test="${country.id==record.country_id}">
                      <li class="clearfix dott">
                        <b><g:if test="${record.is_index}"><a class="show_more_link" href="<g:createLink controller='index' action='direction' id='${record.linkname}' params='${[country:country.urlname]}' base='${context?.mainserverURL_lang}'/>"></g:if>
                        <g:else><span class="show_more_link" onclick="transit(${context.is_dev?1:0},['${record.linkname}','${country.urlname}'],'','','','${context?.lang}')"></g:else>
                        ${record['name2'+context?.lang]}<g:if test="${record.is_index}"></a></g:if><g:else></span></g:else></b><br/>
                        <span>${record['name'+context?.lang]}</span>
                      </li>
                      </g:if></g:each>
                    </g:if></g:each>    
                    </ul>
                  <g:if test="${notice}"><!--noindex-->
                    <div class="page-topic btop" style="border-bottom:none">                
                    <g:each in="${notice}" var="it" status="i">
                      <div class="notice ads width clearfix" onclick="noticeClick(${it.id})">
                        <g:if test="${it?.is_index}"><a href="${serverUrl.toString()+'/'+it?.url}" title="${it?.title}"></g:if>
                        <g:else><span class="link" onclick='transit(${context.is_dev?1:0},["${it?.url}"],"","","","${context?.lang}")'></g:else>                        
                          <img class="thumbnail userpic" src="${resource(dir:'images',file:(it?.image)?'anonses/'+it?.image:'user-default-picture.png')}" border="0" />
                          <div class="description">
                            <h2 class="ins">${it['title'+context?.lang]}</h2>
                            <p>${it['description'+context?.lang]}</p>
                          </div>
                        <g:if test="${it?.is_index}"></a></g:if><g:else></span></g:else>
                      </div>
                    </g:each>                   
                    </div><!--/noindex-->
                  </g:if>                    
                </g:if><g:else>
                    <div id="results">
                    <g:each in="${countries}" var="country" status="i"><g:if test="${country.id in countryIds}">
                      <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                      <g:each in="${popdirection}" var="record" status="j"><g:if test="${country.id==record.country_id}">
                        <tr><td class="dashed">&nbsp;</td></tr>
                        <tr>
                          <td onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
                            <div class="thumbnail shadow clearfix">
                              <img width="200" height="140" src="${(record?.picture)?imageurl+'t_'+record?.picture:resource(dir:'images',file:'default-picture.png')}" alt="${record['name2'+context?.lang]}" border="0" />
                            </div>
                            <div class="title padd20">
                              <g:if test="${record.is_index}"><a class="name" href="<g:createLink controller='index' action='direction' id='${record.linkname}' params='${[country:country.urlname]}' base='${context?.mainserverURL_lang}'/>"></g:if>
                              <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${record.linkname}','${country.urlname}'],'','','','${context?.lang}')"></g:else>
                              ${record['name2'+context?.lang]}<g:if test="${record.is_index}"></a></g:if><g:else></span></g:else>
                            </div>
                            <div class="description" style="width:485px">                              
                              <p align="justify">${record['shortdescription'+context?.lang]}</p>
                            <g:if test="${directionCities}">                            
                              <p><g:each in="${directionCities[j]}" var="city">
                                <g:if test="${city[2]}"><a rel="tag" href="<g:createLink class='city' mapping='${city[4]?'mainpage'+context?.lang:'hSearch'+context?.lang}' params='${city[4]?[:]:[where:city[context?.lang?1:0],country:country.urlname]}' base='${city[4]?'http://'+city[4]+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${message(code:'common.flats_per_night_in')} ${city[context?.lang?1:3]}"></g:if>
                                <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_flats','${city[context?.lang?1:0]}','${country.urlname}'],'','','','${context?.lang}')"></g:else>
                                ${city[context?.lang?1:0]}<g:if test="${city[2]}"></a></g:if><g:else></span></g:else> &nbsp;                                 
                              </g:each>
                              <a href="javascript:void(0)" rel="nofollow" onclick="transit(${context.is_dev?1:0},['${record.linkname}','${country.urlname}'],'','','','${context?.lang}')" title="${message(code:'list.filtr.show_more').capitalize()}">${message(code:'common.more')}&hellip;</a></p>
                            </g:if>
                            </div>
                          </td>
                        </tr>
                        <g:if test="${notice && (j+1==Math.round(popdirection.size()/2))}"><!--noindex-->
                        <tr><td class="dashed">&nbsp;</td></tr>
                        <tr>
                          <td>
                          <g:each in="${notice}">
                            <div class="notice ads width clearfix" style="width:685px !important" onclick="noticeClick(${it.id})">
                              <g:if test="${it?.is_index}"><a href="${serverUrl.toString()+'/'+it?.url}" title="${it?.title}"></g:if>
                              <g:else><span class="link" onclick='transit(${context.is_dev?1:0},["${it?.url}"],"","","","${context?.lang}")'></g:else>                        
                                <img class="thumbnail userpic" src="${resource(dir:'images',file:(it?.image)?'anonses/'+it?.image:'user-default-picture.png')}" border="0" />
                                <div class="description">
                                  <h2 class="ins">${it['title'+context?.lang]}</h2>
                                  <p>${it['description'+context?.lang]}</p>
                                </div>
                              <g:if test="${it?.is_index}"></a></g:if><g:else></span></g:else>
                            </div>
                          </g:each>                   
                          </td>
                        </tr><!--/noindex-->
                        </g:if>                        
                      </g:if></g:each>
                      </table>                      
                    </g:if></g:each>
                    </div>
                </g:else>                  
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
