        <g:if test="${(breadcrumbs?.city?.description || breadcrumbs.directionCities || relatedblog || breadcrumbs?.citysight?.itext || metroNext) && inrequest?.view!='map' && !params.offset}">
          <g:if test="${metroNext && !(breadcrumbs?.bedroom || inrequest?.metro_id || inrequest?.citysight_id) && metroNext[0].total>=homeMetroMinCount}">
            <tr>
              <td width="980" colspan="4">
                <div class="page-topic btop">
                  <h2 class="ins">${((inrequest?.bedroom?(inrequest?.hometype_id?infotags.homerooms:hsRes.infotags.homeroom2)+' ':'')+infotags.hometypes).capitalize()} ${message(code:'list.filtr.footer.per_night_near_metro')}</h2>
                  <p class="hotlinks" align="left" style="margin:13px 0"><g:each in="${metroNext}" var="item"><g:if test="${item.total>=homeMetroMinCount}">
                    <span class="metro">
                      <g:if test="${breadcrumbs.city}"><g:link class="link" rel="tag" mapping="${breadcrumbs.city.domain?('hSearchMetroDomain'+context?.lang):('hSearchMetro'+context?.lang)}" params="${(breadcrumbs.city.domain?[metro_url:Metro.get(item.id)?.urlname]:[where:inrequest?.where,country:breadcrumbs.country.urlname,metro_url:Metro.get(item.id)?.urlname])+(inrequest?.hometype_id?[hometype_id:inrequest?.hometype_id]:[:])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" base="${breadcrumbs.base}" title="${inrequest?.hometype_id?infotags.hometypes:message(code:'server.housing')} ${message(code:'list.per_night_metro')} ${(Metro.get(item.id)?.('name'+context?.lang)?:'').replace('m. "','').replace('м. "','').replace('"','')}">${(Metro.get(item.id)?.('name'+context?.lang)?:'').replace('m. "','').replace('м. "','').replace('"','')}</g:link></g:if>
                      <g:else><g:link class="link" rel="tag" controller="home" action="list" params="[where:inrequest?.where,metro_url:Metro.get(item.id)?.urlname]+(inrequest?.hometype_id?[hometype_id:inrequest?.hometype_id]:[:])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" title="${inrequest?.hometype_id?infotags.hometypes:message(code:'server.housing')} ${message(code:'list.per_night_metro')} ${(Metro.get(item.id)?.('name'+context?.lang)?:'').replace('m. "','').replace('м. "','').replace('"','')}" base="${context?.mainserverURL_lang}">${(Metro.get(item.id)?.('name'+context?.lang)?:'').replace('m. "','').replace('м. "','').replace('"','')}</g:link></g:else>
                      <sup>${item.total}</sup>
                    </span>
                  </g:if></g:each></p>
                </div>
              </td>
            </tr>
          </g:if>
          <g:if test="${!(breadcrumbs?.bedroom || inrequest?.metro_id || inrequest?.district_id)}">
            <tr>
              <td width="980" colspan="4">              
              <g:if test="${citysightMax && !inrequest?.citysight_id && infotags.isfound}">
                <div class="page-topic ${metroNext?'':'btop'}">
                  <h2 class="ins">${(((inrequest?.bedroom && inrequest?.hometype_id)?infotags.homerooms+' ':'')+infotags.hometypes).capitalize()} ${message(code:'list.filtr.footer.per_night_near_citysights')}</h2>
                  <p class="hotlinks" align="left" style="margin:13px 0"><g:each in="${citysightMax}" var="item"><g:if test="${!((inrequest?.citysight_id?:[]).contains(item?.id))}">                      
                    <span class="citysight">
                    <g:if test="${Citysight.get(item.id)?.is_index}">
                      <g:if test="${breadcrumbs.city}"><g:link class="link" rel="tag" mapping="${breadcrumbs.city.domain?('hSearchCitysightDomain'+context?.lang):('hSearchCitysight'+context?.lang)}" params="${(breadcrumbs.city.domain?[citysight_url:Citysight.get(item.id)?.urlname]:[where:inrequest?.where,country:breadcrumbs.country.urlname,citysight_url:Citysight.get(item.id)?.urlname])+(inrequest?.hometype_id?[hometype_id:inrequest?.hometype_id]:[:])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" base="${breadcrumbs.base}" title="${inrequest?.hometype_id?infotags.hometypes:message(code:'server.housing')} ${message(code:'list.per_night1')} ${Citysight.get(item?.id?:0)?.('name'+context?.lang)?:''}">${Citysight.get(item?.id?:0)?.('name'+context?.lang)?:''}</g:link></g:if>
                      <g:else><g:link class="link" rel="tag" controller="home" action="list" params="[where:inrequest?.where,hometype_id:inrequest?.hometype_id?:0,citysight_url:Citysight.get(item.id)?.urlname]+(inrequest?.hometype_id?[hometype_id:inrequest?.hometype_id]:[:])+((inrequest?.homeperson_id?:1)!=1?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.longdiscount?[longdiscount:inrequest?.longdiscount]:[:])+(inrequest?.hotdiscount?[hotdiscount:inrequest?.hotdiscount]:[:])+(inrequest?.date_start?[date_start_year:inrequest?.date_start_year,date_start_month:inrequest?.date_start_month,date_start_day:inrequest?.date_start_day]:[:])+(inrequest?.date_end?[date_end_year:inrequest?.date_end_year,date_end_month:inrequest?.date_end_month,date_end_day:inrequest?.date_end_day]:[:])+((inrequest?.view!='list')?[view:inrequest?.view]:[:])}" title="${inrequest?.hometype_id?infotags.hometypes:message(code:'server.housing')} ${message(code:'list.per_night1')} ${Citysight.get(item?.id?:0)?.('name'+context?.lang)?:''}" base="${context?.mainserverURL_lang}">${Citysight.get(item?.id?:0)?.('name'+context?.lang)?:''}</g:link></g:else>
                    </g:if><g:else>
                      <span class="link" onclick="transit(${context.is_dev?1:0},['${"sight_"+Citysight.get(item.id)?.urlname}','${breadcrumbs.city?.('name'+context?.lang)}','${breadcrumbs.country.urlname}'],'${[0:0].inject([:],sight_linkparams).collect{it.key+"="+it.value}.join(",")}','','','${context?.lang}')">${Citysight.get(item?.id?:0)?.('name'+context?.lang)?:''}</span>
                    </g:else><sup>${item?.total}</sup>
                    </span>
                  </g:if></g:each></p>
                </div>
              </g:if>
              </td>
            </tr>
            <g:if test="${!inrequest?.citysight_id && breadcrumbs.directionCities}">
            <tr>
              <td width="980" colspan="4">
                <div class="page-topic ${citysightMax?'':'btop'}">
                  <h2 class="ins">${message(code:'common.rent')} ${(inrequest?.bedroom?(inrequest?.hometype_id?infotags.homeroomss:infotags.homeroom2)+' ':'')+infotags.hometypess} ${message(code:'list.filtr.footer.per_night_in_another_cities')} ${!context?.lang?breadcrumbs.direction.name_r:''}</h2>
                  <p><g:each in="${breadcrumbs.directionCities}" var="city">
                    <g:if test="${inrequest?.bedroom}">
                      <g:if test="${city.value.is_index}"><a href="<g:createLink mapping='${city.value.domain?('hSearchRoomDomain'+context?.lang):('hSearchRoom'+context?.lang)}' params='${(city.value.domain?[bedroom:inrequest?.bedroom,type_url:'flats']:[where:city.key,country:breadcrumbs.country.urlname,bedroom:inrequest?.bedroom,type_url:'flats'])}' base='${city.value.domain?'http://'+city.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="tag" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:if>
                      <g:else><span class="link" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765" onclick="transit(${context.is_dev?1:0},['room_${inrequest?.bedroom}','type_flats','${city.key}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')"></g:else>
                    </g:if><g:elseif test="${inrequest?.hometype_id!=1}">
                      <g:if test="${city.value.is_index}"><a href="<g:createLink mapping='${city.value.domain?(breadcrumbs.hometype?.urlname=='flats'?('mainpage'+context?.lang):('hSearchTypeDomain'+context?.lang)):'hSearchType'+context?.lang}' params='${(city.value.domain?(breadcrumbs.hometype?.urlname=='flats'?[:]:[type_url:(breadcrumbs.hometype?.urlname?:'all')]):[where:city.key,country:breadcrumbs.country.urlname,type_url:breadcrumbs.hometype?.urlname?:'all'])}' base='${city.value.domain?'http://'+city.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="tag" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:if>
                      <g:else><span class="link" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765" onclick="transit(${context.is_dev?1:0},['type_${breadcrumbs.hometype?.urlname?:'all'}','${city.key}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')"></g:else>
                    </g:elseif><g:else>
                      <g:if test="${city.value.is_index}"><a href="<g:createLink mapping='${city.value.domain?('mainpage'+context?.lang):('hSearch'+context?.lang)}' params='${(city.value.domain?[:]:[where:city.key,country:breadcrumbs.country.urlname])}' base='${city.value.domain?'http://'+city.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:if>
                      <g:else><span class="link" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765" onclick="transit(${context.is_dev?1:0},['${city.key}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')"></g:else>
                    </g:else>                  
                    ${city.key}<g:if test="${city.value.is_index}"></a></g:if><g:else></span></g:else>  
                  </g:each></p>
                </div>               
              <g:if test="${shome}">
                <div class="page-topic">          
                    <h2 class="ins">${promotext+context?.lang}</h2>
                    <g:rawHtml>${itext2+context?.lang}</g:rawHtml>
                  </div>
              </g:if>
              <g:else>                
                <g:if test="${breadcrumbs.city?.description && !inrequest?.hometype_id && !context?.lang}">
                  <div class="page-topic">          
                    <h2 class="ins">Аренда жилья на сутки в ${infotags.vcity}</h2>
                    <g:rawHtml>${breadcrumbs.city.description?:''}</g:rawHtml>
                  </div>
                </g:if><g:elseif test="${inrequest?.hometype_id && !inrequest?.bedroom && breadcrumbs?.city?.(breadcrumbs?.hometype?.urlname+'_description') && !context?.lang}">
                  <div class="page-topic">
                    <h2 class="ins">Аренда ${infotags.hometypess} посуточно в ${infotags.vcity}</h2>
                    <g:rawHtml>${breadcrumbs?.city?.(breadcrumbs?.hometype?.urlname+'_description')}</g:rawHtml>
                  </div>                
                </g:elseif>
              </g:else>
              <g:if test="${relatedblog && !shome}">
                <div class="page-topic">
                  <h2 class="ins">Статьи на тему ${breadcrumbs.city?.name?:breadcrumbs.region?.name}</h2>
                <g:each in="${relatedblog}" var="blog" status="i">
                  <h3><g:link mapping="${'timeline'+context?.lang}" params="${[blog:blog?.author,year:blog.inputdate.getYear()+1900,month:blog.inputdate.getMonth()+1<10?'0'+(blog.inputdate.getMonth()+1):blog.inputdate.getMonth()+1,day:blog.inputdate.getDate()<10?'0'+blog.inputdate.getDate():blog.inputdate.getDate(),id:blog.linkname]}" rel="nofollow" absolute="true">${blog.title}</g:link></h3>
                  <p style="margin:2px 0">${blog?.shortdescription?:''}</p>
                  <span class="copyright">                    
                    <g:if test="${blog?.googleplus_id!=''}"><a href="https://plus.google.com/${blog?.googleplus_id}?rel=author" rel="nofollow">${blog?.author}</a></g:if>
                    <g:else><g:link mapping="${'timeline'+context?.lang}" params="${[blog:blog?.author]}" absolute="true">${blog?.author}</g:link></g:else>                
                    &nbsp; ${String.format('%td.%<tm.%<tY %<tH:%<tM',blog.inputdate)}
                  </span>                  
                </g:each>
                </div>
              </g:if>
              </td>
            </tr>
            </g:if>
          </g:if><g:if test="${(inrequest?.citysight_id?:[]).size()==1 && breadcrumbs.citysight?.itext && !context.lang && inrequest?.hometype_id==1}">
            <tr>
              <td width="980" colspan="4">                            
                <div class="page-topic">
                  <g:rawHtml>${breadcrumbs.citysight.itext?:''}</g:rawHtml>
                </div>
              </td>
            </tr>          
          </g:if>
        </g:if>
