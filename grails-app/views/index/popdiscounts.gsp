<html>
  <head>
    <title>${popdirection['ceo_title_d'+context?.lang]?popdirection['ceo_title_d'+context?.lang]:popdirection['name'+context?.lang]}</title>
    <meta name="keywords" content="${context?.lang?'':(popdirection?.ceo_keywords_d?popdirection?.ceo_keywords_d:popdirection?.keyword)}" />
    <meta name="description" content="${popdirection['ceo_description_d'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl+(context.cquery?'?':'')}<g:rawHtml>${context.cquery}</g:rawHtml>" />
    <meta name="layout" content="main" />
    <g:javascript>
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
    var star_id=0;
    function addtofavourite(lId){
      star_id=lId;
      <g:remoteFunction controller='home' action='selectcompany' onSuccess='setFavourite(e)' params="\'id=\'+lId" />
    }
    function setFavourite(e){
      var bFlag=0;
      var arr=e.responseJSON.wallet;
      var hotElem = $('star_icon_hot_'+star_id)
      var longElem = $('star_icon_long_'+star_id)
      for(var i=0;i<arr.size();i++){
        if(star_id==arr[i]){
          if (hotElem) hotElem.addClassName("starred");
          if (longElem) longElem.addClassName("starred");
          bFlag=1;
        }
      }
      if(!bFlag){
        if (hotElem) hotElem.removeClassName("starred");
        if (longElem) longElem.removeClassName("starred");
      }
      if(arr.length>0){
        $('favorite').removeClassName('no_active');
        $('favorite').href='<g:createLink controller="trip" action="favorite"/>';
        $('favorite').parentElement.addClassName(' starred');
      } else {
        $('favorite').addClassName('no_active');
        $('favorite').href='javascript:void(0)';
        $('favorite').parentElement.removeClassName(' starred');
      }
    }
    </g:javascript>
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">                
                <a class="button" rel="nofollow" onclick="transit(${context.is_dev?1:0},['cities'],'','','','${context?.lang}')">${message(code:'common.renthome')}</a>
              </td>
              <td colspan="3" class="rent ss">
                <h1>${popdirection['header_d'+context?.lang]?:popdirection['ceo_title_d'+context?.lang]?:popdirection['name2'+context?.lang]+'. '+popdirection['name'+context?.lang]}</h1>
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
                    <g:if test="${breadcrumbs.country.is_index}"><a href="<g:createLink controller='index' action='popdirectionAll' params='[id:breadcrumbs.country.urlname]' base='${context?.mainserverURL_lang}'/>" itemprop="item"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" itemprop="item"></g:else>
                      <span itemprop="name">${breadcrumbs.country['name'+context?.lang]}</span>
                    <g:if test="${breadcrumbs.country.is_index}"></a></g:if><g:else></span></g:else><meta itemprop="position" content="2" />
                  </li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <g:if test="${popdirection?.is_index}"><a href="<g:createLink controller='index' action='direction' id='${popdirection.linkname}' params='${[country:breadcrumbs.country.urlname]}' base='${context?.mainserverURL_lang}'/>" itemprop="item"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${popdirection?.linkname}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" itemprop="item"></g:else>
                      <span itemprop="name">${popdirection['name2'+context?.lang]}</span>
                    <g:if test="${popdirection?.is_index}"></a></g:if><g:else></span></g:else><meta itemprop="position" content="3" />
                  </li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <span itemprop="item">
                      <span itemprop="name">${message(code:'common.discounts').capitalize()}</span>
                    </span><meta itemprop="position" content="4" />
                  </li>
                </ul>              
              </td>
            </tr>
            <tr>
              <td valign="top">
                <ul class="collapsable_filters">
                <g:if test="${directionCities}">
                  <li class="search_filter" id="cities_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('cities_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('cities_container');">${message(code:'common.discounts.cities')}</a>
                    <ul class="search_filter_content">
                    <g:each in="${directionCities}" var="city">  
                      <li style="display:inline-block">
                        <g:if test="${city.value.is_index}"><a href="<g:createLink mapping='${city.value.domain?'hSearchTypeDomain'+context?.lang:'hSearch'+context?.lang}' params='${city.value.domain?[longdiscount:1,hotdiscount:1,type_url:'all']:[where:city.key,country:breadcrumbs.country.urlname,longdiscount:1,hotdiscount:1]}' base='${city.value.domain?'http://'+city.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:if>
                        <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${city.key}','${breadcrumbs.country.urlname}'],'hotdiscount=1,longdiscount=1','','','${context?.lang}')" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:else>
                        ${city.key}<g:if test="${city.value.is_index}"></a></g:if><g:else></span></g:else>
                      </li>
                    </g:each>
                    </ul>                      
                  </li>
                </g:if>
                <g:if test="${previousdirection || nextdirection}">
                  <li class="search_filter" id="direction_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('direction_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('direction_container');"><font size="2">${message(code:'common.discounts.directions')}</font></a>
                    <ul class="search_filter_content">
                    <g:if test="${previousdirection}"> 
                      <li class="clearfix dott">
                        <g:link action="popdiscounts" id="${previousdirection?.linkname}" params="${[country:Country.get(previousdirection?.country_id)?.urlname]}">${previousdirection['name2'+context?.lang]}</g:link>
                      </li>
                    </g:if><g:if test="${nextdirection}">
                      <li class="clearfix dott last">
                        <g:link action="popdiscounts" id="${nextdirection?.linkname}" params="${[country:Country.get(nextdirection?.country_id)?.urlname]}">${nextdirection['name2'+context?.lang]}</g:link>
                      </li>
                    </g:if>
                    </ul>
                  </li>
                </g:if>
                </ul>
              </td>
              <td colspan="3" valign="top">
                <div class="search_container form shadow">
                  <div id="search_body" style="min-height:482px">
                  <g:if test="${popdirection['ceo_description_d'+context?.lang]}">
                    <div style="padding:0px 20px">
                      <p><b><g:rawHtml>${popdirection['ceo_description_d'+context?.lang]?:''}</g:rawHtml></b></p>
                    </div>
                  </g:if>
                  <g:if test="${hotdiscount.records}">
                    <a name="hotdiscount" rel="nofollow"></a>
                    <h2 class="toggle border"><span class="edit_icon price"></span> ${message(code:'common.discounts_for_hot_offers')}</h2>
                    <div style="padding:0 20px">
                      <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                    </div>
                    <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                    <g:each in="${hotdiscount.records}" var="home" status="i">
                      <g:if test="${(i % 3)==0}"><tr></g:if>
                        <td class="hlisting offer-rent" width="33%" onmouseout="this.removeClassName(' selected');" onmouseover="this.addClassName(' selected');">
                          <div class="item housing clearfix" style="width:220px">                                                  
                            <div class="thumbnail shadow" style="width:220px;height:160px">
                              <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.findByName(home.city)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.findByName(home.city)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.findByName(home.city)?.domain?'http://'+City.findByName(home.city).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                              <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                                <img class="photo" width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                              <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                              <div class="discount-container photo">                
                                <div class="discount-price">-${home?.hotdiscounttext}</div>
                              </div>                                
                            </div>
                            <div class="photo_item_details">
                              <span class="price ss_price b-rub">${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></span>
                            </div>
                          </div>
                          <div class="fn title">
                            <g:if test="${home.is_index}"><a class="url name" href="<g:createLink mapping='${City.findByName(home.city)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.findByName(home.city)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.findByName(home.city)?.domain?'http://'+City.findByName(home.city).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="self bookmark" title="${home?.name}"></g:if>
                            <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <g:shortString text="${home?.name}" length="16"/>
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                            <a id="star_${home?.id}" class="star_icon_container" href="javascript:void(0)" rel="nofollow" title="${message(code:'common.add_to_favourite')}">
                              <div id="star_icon_hot_${home?.id}" onclick="addtofavourite(${home?.id})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.id)}">starred</g:if>"></div>
                            </a>
                          </div>              
                          <div class="description">
                            <ul class="details-list" style="width:220px">              
                              <li class="details-list-item location">
                                <g:shortString text="${home?.shortaddress}" length="30"/>
                              </li>
                            </ul>
                          </div>
                          <div class="lister fn">${User.findWhere(client_id:home?.client_id).nickname}</div>
                        </td>
                      <g:if test="${((i % 3)==0) && (hotdiscount.records.size() == 1)}">
                        <td width="33%">&nbsp;</td><td width="33%">&nbsp;</td>
                      </g:if><g:elseif test="${((i % 3)==1) && (hotdiscount.records.size() == 2)}">
                        <td width="33%">&nbsp;</td>
                      </g:elseif><g:elseif test="${(i % 3)==2}">
                      </tr>
                      <g:if test="${i+1!=hotdiscount.records.size()}"><tr><td colspan="3" class="dashed">&nbsp;</td></tr></g:if>
                      </g:elseif>
                    </g:each>                        
                    </table>
                  </g:if>
<!--longdiscount-->
                  <g:if test="${longdiscount.records}">
                    <a name="longdiscount" rel="nofollow"></a>
                    <h2 class="toggle border"><span class="edit_icon price"></span> ${message(code:'common.discounts_for_long_offers')}</h2>
                    <div style="padding:0 20px">
                      <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                    </div>
                    <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                    <g:each in="${longdiscount.records}" var="home" status="i">
                      <g:if test="${(i % 3)==0}"><tr></g:if>
                        <td class="hlisting offer-rent" width="33%" onmouseout="this.removeClassName(' selected');" onmouseover="this.addClassName(' selected');">
                          <div class="item housing clearfix" style="width:220px">                                                  
                            <div class="thumbnail shadow" style="width:220px;height:160px">
                              <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.findByName(home.city)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.findByName(home.city)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.findByName(home.city)?.domain?'http://'+City.findByName(home.city).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                              <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                                <img class="photo" width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                              <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                              <div class="discount-container photo">
                                <div class="discount-price">-${home?.longdiscounttext}</div>
                              </div>
                            </div>
                            <div class="photo_item_details">
                              <span class="price ss_price b-rub">${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></span>
                            </div>
                          </div>
                          <div class="fn title">
                            <g:if test="${home.is_index}"><a class="url name" href="<g:createLink mapping='${City.findByName(home.city)?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang}' params='${City.findByName(home.city)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.findByName(home.city)?.domain?'http://'+City.findByName(home.city).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="self bookmark" title="${home?.name}"></g:if>
                            <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <g:shortString text="${home?.name}" length="16"/>
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                            <a id="star_${home?.id}" class="star_icon_container" href="javascript:void(0)" rel="nofollow" title="${message(code:'common.add_to_favourite')}">
                              <div id="star_icon_long_${home?.id}" onclick="addtofavourite(${home?.id})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.id)}">starred</g:if>"></div>
                            </a>
                          </div>                          
                          <div class="description">
                            <ul class="details-list" style="width:220px">              
                              <li class="details-list-item location">
                                <g:shortString text="${home?.shortaddress}" length="30"/>                  
                              </li>
                            </ul>
                          </div>
                          <div class="lister fn">${User.findWhere(client_id:home?.client_id).nickname}</div>
                        </td>
                      <g:if test="${((i % 3)==0) && (longdiscount.records.size() == 1)}">
                        <td width="33%">&nbsp;</td><td width="33%">&nbsp;</td>
                      </g:if><g:elseif test="${((i % 3)==1) && (longdiscount.records.size() == 2)}">
                        <td width="33%">&nbsp;</td>
                      </g:elseif><g:elseif test="${(i % 3)==2}">
                      </tr>
                      <g:if test="${i+1!=longdiscount.records.size()}"><tr><td colspan="3" class="dashed">&nbsp;</td></tr></g:if>
                      </g:elseif>                  
                    </g:each>                        
                    </table>
                  </g:if>                    
                  </div>                  
                </div>
              </td>
            </tr>
  </body>
</html>
