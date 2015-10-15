<html>  
  <head>    
  <g:if test="${!inrequest.id}">          
    <g:if test="${inrequest?.year}"><g:each in="${filterYears}" var="year" status="i"><g:if test="${year==inrequest?.year}">
      <g:if test="${inrequest?.month}"><g:each in="${filterMonths[i]}" var="month" status="j"><g:if test="${(year==curYear?curMonth:12)-inrequest?.month==j}">
        <g:if test="${inrequest?.day}">
    <title>${(inrequest?.blog!='all')?inrequest?.blog+' - ':''}${infotext['promotext1'+context?.lang]?:''} ${message(code:'timeline.for')} ${((inrequest?.day<10)?'0'+inrequest?.day:inrequest?.day)+'.'+((year==curYear?curMonth:12)-j<10?'0'+((year==curYear?curMonth:12)-j):(year==curYear?curMonth:12)-j)+'.'+year}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}, ${(inrequest?.blog!='all')?inrequest?.blog:''} ${((inrequest?.day<10)?'0'+inrequest?.day:inrequest?.day)+'.'+((year==curYear?curMonth:12)-j<10?'0'+((year==curYear?curMonth:12)-j):(year==curYear?curMonth:12)-j)+'.'+year}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''} ${(inrequest?.blog!='all')?inrequest?.blog+' - ':''} ${message(code:'timeline.for')} ${((inrequest?.day<10)?'0'+inrequest?.day:inrequest?.day)+'.'+((year==curYear?curMonth:12)-j<10?'0'+((year==curYear?curMonth:12)-j):(year==curYear?curMonth:12)-j)+'.'+year}" />
        </g:if><g:else>
    <title>${(inrequest?.blog && inrequest?.blog!='all')?inrequest?.blog+' - ':''}${infotext['promotext1'+context?.lang]?:''} ${message(code:'timeline.for')} ${month} ${year} ${message(code:'timeline.yearr')}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}, ${(inrequest?.blog!='all')?inrequest?.blog+',':''} ${month} ${year} года" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''} ${(inrequest?.blog!='all')?inrequest?.blog+' - ':''} ${message(code:'timeline.for')} ${month} ${year} ${message(code:'timeline.yearr')}" />
        </g:else>
      </g:if></g:each></g:if><g:else>
    <title>${(inrequest?.blog && inrequest?.blog!='all')?inrequest?.blog+' - ':''}${infotext['promotext1'+context?.lang]?:''} ${message(code:'timeline.for')} ${year} ${message(code:'timeline.year')}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}, ${(inrequest?.blog!='all')?inrequest?.blog+',':''} ${year} год" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''} ${(inrequest?.blog!='all')?inrequest?.blog+' - ':''} ${message(code:'timeline.for')} ${year} ${message(code:'timeline.year')}" />    
      </g:else>
    </g:if></g:each></g:if><g:else>
      <g:if test="${!(inrequest?.tag || (inrequest?.blog && inrequest?.blog!='all'))}">
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />    
      </g:if><g:else>
    <title>${inrequest?.tag?message(code:'timeline.acticles.group')+' '+poptag+' - ':''}${(inrequest?.blog && inrequest?.blog!='all')?inrequest?.blog+' - ':''}${infotext['promotext1'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}, ${inrequest?.tag?poptag+', ':''} ${(inrequest?.blog && inrequest?.blog!='all')?inrequest?.blog:''}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''} ${inrequest?.tag?message(code:'timeline.acticles.group')+' '+poptag+' - ':''}${(inrequest?.blog && inrequest?.blog!='all')?inrequest?.blog+' - ':''}${infotext['promotext1'+context?.lang]?:''}" />
      </g:else>
    </g:else>
  </g:if><g:else>    
    <title>${article?.ceo_title?article?.ceo_title:article?.title?:infotext['title'+context?.lang]?:' '}</title>
    <meta name="keywords" content="${article?.ceo_keywords?article?.ceo_keywords:article?.tags?.join(', ')?:''}" />
    <meta name="description" content="${article?.ceo_description?article?.ceo_description:article?.shortdescription?:''}" />
    <meta property="fb:app_id" content="${fb_api_key}" />
    <meta property="og:type" content="article" />
    <meta property="og:site_name" content="StayToday" />
    <meta property="og:locale" content="ru_RU" />    
    <meta property="og:url" content="${createLink(mapping:'timeline'+context?.lang,params:[blog:article?.author,year:article?.inputdate?.getYear()+1900,month:article?.inputdate?.getMonth()+1<10?'0'+(article?.inputdate?.getMonth()+1):article?.inputdate?.getMonth()+1,day:article?.inputdate?.getDate()<10?'0'+article?.inputdate?.getDate():article?.inputdate?.getDate(),id:article?.linkname],absolute:true)}" />
    <meta property="og:title" content="${article?.title?:''}" />
    <meta property="og:description" content="${article?.shortdescription?:''}" />
    <meta property="og:image" content="${imageurl+article?.picture}" />
    <meta property="article:published_time" content="${g.formatDate(date:article?.inputdate,format:'yyyy-MM-dd\'T\'HH:mmZ',locale:Locale.ENGLISH)}" />
    <g:if test="${article?.googleplus_id!=''}">
    <meta property="article:author" content="https://plus.google.com/${article?.googleplus_id}/about" />
    </g:if>
    <meta property="article:section" content="${article.tags.last()}" />
    <meta property="article:tag" content="${article.tags.each{it.name}.join(', ')}" />    
    <meta name="twitter:card" content="summary" />
    <meta name="twitter:site" content="@StayTodayRu" />
    <link rel="canonical" href="${createLink(mapping:'timeline',params:[blog:article?.author,year:article?.inputdate?.getYear()+1900,month:article?.inputdate?.getMonth()+1<10?'0'+(article?.inputdate?.getMonth()+1):article?.inputdate?.getMonth()+1,day:article?.inputdate?.getDate()<10?'0'+article?.inputdate?.getDate():article?.inputdate?.getDate(),id:article?.linkname],absolute:true)}" />
    <link rel="image_src" href="${imageurl+article?.picture}" />
  </g:else>     
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
    </g:javascript>     
    <g:setupObserve function='clickPaginate' id='results'/>
    <style type="text/css">
      .rent { padding: 0px !important }
      .rent h1 { margin: 0 10px 0 20px !important; height: auto !important }
      h2.year { margin: 20px 10px 10px -15px }
      h2.year:first-of-type {	margin-top: 5px }
      h2.year a[href] { color: #333 }
      .search_filter_content ul { margin-left: 5px }
      .blog a[href] { text-decoration: underline }
      .blog a:hover { text-decoration: none }      
      #cboxLoadedContent .cboxPhoto { border: 5px solid white; border-radius: 10px; max-width: 800px; min-width: 220px }
      .text p, .text ul li { font-size: 14px }
      p.entry-content { line-height: 14px; margin: 3px 0 0; height: 82px }
    </style>
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" class="search s0">
                <g:if test="${isLoginDenied}"><a class="button" rel="nofollow" onclick="showLoginForm()">${message(code:'common.deliverhome')}</a></g:if>
                <g:else><a class="button" rel="nofollow" onclick="transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')">${message(code:'common.deliverhome')}</a></g:else>
              </td>
              <td width="730" colspan="3" class="rent">
                <h1 <g:if test="${inrequest.id}">itemprop="name"</g:if>>
                <g:if test="${!inrequest.id}">
                  <g:if test="${inrequest?.year}"><g:each in="${filterYears}" var="year" status="i"><g:if test="${year==inrequest?.year}">
                    <g:if test="${inrequest?.month}"><g:each in="${filterMonths[i]}" var="month" status="j"><g:if test="${(year==curYear?curMonth:12)-inrequest?.month==j}">
                      <g:if test="${inrequest?.day}">${(inrequest?.blog!='all')?inrequest?.blog+' - ':''}${infotext['promotext1'+context?.lang]?:''} за ${((inrequest?.day<10)?'0'+inrequest?.day:inrequest?.day)+'.'+((year==curYear?curMonth:12)-j<10?'0'+((year==curYear?curMonth:12)-j):(year==curYear?curMonth:12)-j)+'.'+year}</g:if>
                      <g:else>${(inrequest?.blog!='all')?inrequest?.blog+' - ':''}${infotext['promotext1'+context?.lang]?:''} ${message(code:'timeline.for')} ${month} ${year} ${message(code:'timeline.yearr')}</g:else>
                    </g:if></g:each></g:if>
                    <g:else>${(inrequest?.blog!='all')?inrequest?.blog+' - ':''}${infotext['promotext1'+context?.lang]?:''} ${message(code:'timeline.for')} ${year} ${message(code:'timeline.year')}</g:else>
                  </g:if></g:each></g:if><g:else>
                    <g:if test="${!(inrequest?.tag || (inrequest?.blog && inrequest?.blog!='all'))}">${infotext['header'+context?.lang]?:''}</g:if>
                    <g:else>
                      <g:if test="${inrequest?.tag}">${message(code:'timeline.acticles.group')} ${poptag} - </g:if>
                      <g:if test="${inrequest?.blog && inrequest?.blog!='all'}">${inrequest?.blog} - </g:if>
                      ${infotext['promotext1'+context?.lang]?:''}
                    </g:else>                    
                  </g:else>    
                </g:if><g:else>${article?.title?:''}</g:else>
                </h1>
              </td>
            </tr>
            <tr style="height:30px">              
              <td class="bg_shadow">
                <div class="topl relative"><g:if test="${!inrequest?.id}">
                  <span class="count"><b>${message(code:'timeline.records.found')}</b><span id="ads_count"></span></span>
                </g:if></div>
              </td>
              <td colspan="3" class="bg_shadow">
                <div class="breadcrumbs" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" rel="v:url" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span>
                  <g:if test="${inrequest?.year}"><g:each in="${filterYears}" var="year" status="i"><g:if test="${year==inrequest?.year}"><span typeof="v:Breadcrumb">
                    <g:link controller="index" action="timeline" base="${context.absolute_lang}" rel="v:url" property="v:title">${Infotext.findByControllerAndAction('index','timeline')['name'+context?.lang]}</g:link> &#8594;
                  </span>
                    <g:if test="${inrequest?.blog && inrequest?.blog!='all'}"><g:each in="${filterAuthors}" var="author"><g:if test="${author==inrequest?.blog}"><span typeof="v:Breadcrumb">
                      <g:link mapping="${'timeline'+context?.lang}" params="${[blog:author]}" rel="v:url" property="v:title">${author}</g:link> &#8594;
                  </span>
                    </g:if></g:each></g:if>
                    <g:if test="${inrequest?.month}"><g:each in="${filterMonths[i]}" var="month" status="j"><g:if test="${(year==curYear?curMonth:12)-inrequest?.month==j}"><span typeof="v:Breadcrumb">
                      <g:link mapping="${'timeline'+context?.lang}" params="${[blog:inrequest?.blog,year:year]}" rel="v:url" property="v:title">${year}</g:link> &#8594;
                  </span>
                      <g:if test="${inrequest?.day}"><span typeof="v:Breadcrumb">
                        <g:link mapping="${'timeline'+context?.lang}" params="${[blog:inrequest?.blog,year:year,month:((year==curYear?curMonth:12)-j<10?'0'+((year==curYear?curMonth:12)-j):(year==curYear?curMonth:12)-j)]}" rel="v:url" property="v:title">${month}</g:link> &#8594;
                  </span>
                        <g:if test="${inrequest?.id && article}"><span typeof="v:Breadcrumb">
                          <g:link mapping="${'timeline'+context?.lang}" params="${[blog:inrequest?.blog,year:year,month:((year==curYear?curMonth:12)-j<10?'0'+((year==curYear?curMonth:12)-j):(year==curYear?curMonth:12)-j),day:((inrequest?.day<10)?'0'+inrequest?.day:inrequest?.day)]}" rel="v:url" property="v:title">${((inrequest?.day<10)?'0'+inrequest?.day:inrequest?.day)+'.'+((year==curYear?curMonth:12)-j<10?'0'+((year==curYear?curMonth:12)-j):(year==curYear?curMonth:12)-j)+'.'+year}</g:link> &#8594;
                  </span>
                  ${article?.title?:''}
                        </g:if><g:else>${((inrequest?.day<10)?'0'+inrequest?.day:inrequest?.day)+'.'+((year==curYear?curMonth:12)-j<10?'0'+((year==curYear?curMonth:12)-j):(year==curYear?curMonth:12)-j)+'.'+year}</g:else>
                      </g:if><g:else>${month}</g:else>
                    </g:if></g:each></g:if>
                    <g:else>${year}</g:else>
                  </g:if></g:each></g:if>
                  <g:else>                                       
                    <g:if test="${(inrequest?.blog && inrequest?.blog!='all') || inrequest?.tag}"><span typeof="v:Breadcrumb">
                      <g:link controller="index" action="timeline" base="${context.absolute_lang}" rel="v:url" property="v:title">${Infotext.findByControllerAndAction('index','timeline')['name'+context?.lang]}</g:link> &#8594;
                  </span>
                      <g:if test="${inrequest?.blog && inrequest?.blog!='all'}">
                        <g:each in="${filterAuthors}" var="author"><g:if test="${author==inrequest?.blog}">
                          <g:if test="${inrequest?.tag}"><span typeof="v:Breadcrumb">
                            <g:link mapping="${'timeline'+context?.lang}" params="${[blog:author]}" rel="v:url" property="v:title">${author}</g:link> &#8594;
                  </span>
                  ${message(code:'timeline.acticles.group')} ${poptag}
                          </g:if><g:else>${author}</g:else>
                        </g:if></g:each>
                      </g:if><g:else>${message(code:'timeline.acticles.group')} ${poptag}</g:else>                      
                    </g:if><g:else>${infotext['name'+context?.lang]?:''}</g:else>
                  </g:else>
                </div>                                
              </td>
            </tr>            
            <tr>
              <td valign="top">
                <ul class="collapsable_filters">			    
                  <li class="search_filter" id="articles_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('articles_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('articles_container');">${message(code:'timeline.acticles')}</a>        
                    <div class="search_filter_content">
                      <h2 class="year padd20"><g:if test="${inrequest?.blog||inrequest.tag?.size()!=0}"><span class="link" onclick="transit(${context.is_dev?1:0},['timeline'],'','','','${context?.lang}')" style="color:#333">${message(code:'timeline.new')}</span></g:if><g:else><span class="link">${message(code:'timeline.new')}</span></g:else></h2>
                    <g:each in="${filterYears}" var="year" status="i">
                      <h2 class="year padd20"><g:if test="${inrequest?.year!=year||inrequest?.month}"><span class="link" onclick="transit(${context.is_dev?1:0},['${year}','all','timeline'],'','','','${context?.lang}')" style="color:#333">${year}</span></g:if><g:else><span class="link">${year}</span></g:else></h2>
                      <ul>
                      <g:each in="${filterMonths[i]}" var="month" status="j">
                        <li class="clearfix dott ${(j+1==filterMonths[i].size())?'last':''}">
                          <label><g:if test="${inrequest?.year!=year||inrequest?.month!=((year==curYear?curMonth:12)-j)||inrequest?.day||inrequest?.blog!='all'}"><span class="link" onclick="transit(${context.is_dev?1:0},['${((year==curYear?curMonth:12)-j<10?'0'+((year==curYear?curMonth:12)-j):(year==curYear?curMonth:12)-j)}','${year}','all','timeline'],'','','','${context?.lang}')" style="color:#787878">${month}</span></g:if><g:else><span class="link">${month}</span></g:else></label>
                        </li>
                      </g:each>                           
                      </ul>
                    </g:each>                                          
                    </div>                    
                  </li>
                  <li class="search_filter" id="author_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('author_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('author_container');">${message(code:'timeline.authors')}</a>        
                    <ul class="search_filter_content">                      
                    <g:each in="${filterAuthors}" var="author" status="i">
                      <li class="clearfix dott ${(i+1==filterAuthors.size())?'last':''}">
                        <label><g:if test="${author!=inrequest?.blog||inrequest?.year||inrequest.tag?.size()}"><span class="link" onclick="transit(${context.is_dev?1:0},['${author}','timeline'],'','','','${context?.lang}')">${author}</span></g:if><g:else><span class="link" style="color:#787878">${author}</span></g:else></label>
                      </li>
                    </g:each>
                    </ul>                                          
                  </li>
                  <li class="search_filter" id="tags_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('tags_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('tags_container');">${message(code:'common.tags')}</a>        
                    <div class="search_filter_content" style="line-height:20px">
                      <g:each in="${tagcloud}" var="tag">                      
                        <g:if test="${inrequest?.blog||inrequest.tag?.size()!=1||(inrequest.tag?.size()==1&&inrequest.tag[0]!=tag.key)}"><span class="link" onclick="transit(${context.is_dev?1:0},['timeline'],'tag=${tag.key}','','','${context?.lang}')" style="font-size:${tag.value>tagcloudParams.maxFontCount?'20px':tag.value>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765">${tag.key}</span></g:if><g:else><span class="link" style="font-size:${tag.value>tagcloudParams.maxFontCount?'20px':tag.value>tagcloudParams.middleFontCount?'15px':'10px'}">${tag.key}</span></g:else>                      
                      </g:each>
                    </div>                                          
                  </li>
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
                </ul>
              </td>
              <td colspan="3" valign="top">
                <div class="search-container body shadow">
                <g:if test="${!inrequest.id && infotext?.itext}">
                  <div style="padding:0 15px">
                    <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                  </div>
                </g:if>
                <g:if test="${blogs}">
                  <div id="search_body">                  
                    <div id="results">
                      <g:render template="/blog"/>
                    </div><!--noindex-->
                    <script type="text/javascript">
                      if(navigator.userAgent.search('Firefox')>-1)
                        $('ajax_wrap').setStyle({ marginTop: '-20px' });
                      else
                        $('ajax_wrap').setStyle({ marginTop: '-15px' });
                    </script><!--/noindex-->
                </g:if>
                  <g:if test="${inrequest.id && article}">  
                    <div class="text-topic">    
                      <p><b itemprop="description">${article?.shortdescription}</b></p>
                      <p><img itemprop="image" src="${(article?.picture)?imageurl+article?.picture:resource(dir:'images',file:'logo.png')}" alt="${article?.title}" border="0"/></p>
                      <div itemprop="text">
                        <g:rawHtml>${article?.atext}</g:rawHtml>
                      </div>
                      <ul class="details-list clearfix col" style="width:auto">
                        <li class="details-list-item">
                          <b>${message(code:'timeline.author')}:</b>
                          <g:if test="${article?.googleplus_id!=''}"><a href="https://plus.google.com/${article?.googleplus_id}?rel=author">${article?.author}</a></g:if>
                          <g:else><g:link mapping="${'timeline'+context?.lang}" params="${[blog:article?.author]}" itemprop="author">${article?.author}</g:link></g:else>
                        </li>
                        <li class="details-list-item">
                          <img src="${resource(dir:'images',file:'calendar.png')}" align="absmiddle" />
                          <span itemprop="datePublished">${String.format('%td.%<tm.%<tY',article?.inputdate)}</span>
                        </li>
                      </ul>                  
                      <div class="results_header padd20" style="clear:right;margin:0 -20px; padding-right:20px">
                        <b class="float">${message(code:'common.tags')}:</b> 
                        <span class="blog col">
                        <g:each in="${article.tags}" var="tag" status="j">
                          <g:link mapping="${'timeline'+context?.lang}" params="${[blog:article?.author,tag:tag.name]}" rel="tag">${tag.name}</g:link><g:if test="${(j+1)!=article.tags.size()}">, </g:if>
                        </g:each>        
                        </span>
                      </div><!--noindex-->
                      <g:javascript>
                        jQuery(document).ready(function(){
                          jQuery('.pics').colorbox({
                            rel: 'pics',
                            next: '',
                            previous: '',
                            loop: false                
                          });
                        });
                      </g:javascript><!--/noindex-->
                    </div>
                  </g:if>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
