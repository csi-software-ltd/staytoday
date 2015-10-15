<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <meta name="twitter:card" content="summary" />
    <meta name="twitter:site" content="@StayTodayRu" />
    <meta name="twitter:domain" content="StayToday.ru" />
    <meta name="twitter:app:name:iphone" content="StayToday" />    
    <meta name="twitter:app:name:googleplay" content="StayToday" />
    <meta name="twitter:app:url:iphone" content="http://staytoday.ru/mobile" />    
    <meta name="twitter:app:url:googleplay" content="http://staytoday.ru/mobile" />
    <meta name="twitter:app:id:iphone" content="id595979996" />    
    <meta name="twitter:app:id:googleplay" content="ru.trace.staytoday" />    
    <meta property="fb:app_id" content="${fb_api_key}" />
    <meta property="og:type" content="business" />
    <meta property="og:site_name" content="StayToday" />
    <meta property="og:locale" content="${context?.lang?'en_US':'ru_RU'}" />
    <meta property="og:locale:alternate" content="${context?.lang?'ru_RU':'en_US'}" />    
    <meta property="og:url" content="${createLink(controller:'index',action:'about',base:context?.mainserverURL_lang)}" />
    <meta property="og:title" content="StayToday" />
    <meta property="og:description" content="${infotext['description'+context?.lang]?:''}" />
    <meta property="og:image" content="${resource(dir:'images',file:'logo_large.png',absolute:true)}" />
    <meta property="contact_data:street_address" content="ул. Смоленская, 14, оф. 206" />
    <meta property="contact_data:locality" content="Санкт-Петербург" />    
    <meta property="contact_data:postal_code" content="196084" />
    <meta property="contact_data:country_name" content="Россия" />
    <meta property="contact_data:region" content="Санкт-Петербург" />
    <meta property="contact_data:email" content="info@staytoday.ru" />
    <meta property="contact_data:phone_number" content="8 (800) 555-17-68" />
    <meta property="contact_data:website" content="http://StayToday.ru" />
    <meta property="hours:day" content="пн,вт,ср,чт,пт" />
    <meta property="hours:start" content="11:00" />
    <meta property="hours:end" content="19:00" />
    <link rel="canonical" href="${createLink(controller:'index',action:'about',base:context?.mainserverURL_lang)}" />
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(uri:'/about',base:context?.mobileURL_lang)}" />
    <link rel="alternate" media="handheld" href="${createLink(uri:'/about',base:context?.mobileURL_lang)}" />
    <link rel="image_src" href="${resource(dir:'images',file:'logo_large.png',absolute:true)}" />    
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
    </g:javascript>
    <style type="text/css">
      .search_filter_content { color: #3F5765 !important; }
      dt { font-weight: bold }
      dd, div.answer p { margin: 0 }
      dl { margin: 0; padding: 4px 0 }      
      ul.faq_list { margin: 15px 0px 0px !important; }
      ul.faq_list li, div.answer { padding: 7px 20px !important; text-align: justify }      
    </style>
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">
                <a class="button" rel="nofollow" onclick="<g:if test='${isLoginDenied}'>showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>
              </td>
              <td width="730" colspan="3" class="rent ss">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr>              
              <td colspan="3" class="bg_shadow">              
                <div class="breadcrumbs" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">
                    <a onclick="transit(${context.is_dev?1:0},'','','','','${context?.lang}')" rel="nofollow" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span>
                  ${infotext['header'+context?.lang]?:''}
                </div>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <ul class="collapsable_filters">			    
                  <li class="search_filter" id="contacts_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('contacts_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('contacts_container');">${infotext['promotext1'+context?.lang]?:''}</a>        
                    <div class="search_filter_content">
                      <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                    </div>
                  </li>
                  <li class="search_filter" id="bank_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('bank_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('bank_container');">${infotext['promotext2'+context?.lang]?:''}</a>        
                    <div class="search_filter_content">
                      <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                    </div>
                  </li>
                  <li id="video_container" class="search_filter">
                    <a class="filter_toggle" onclick="toggleFilter('video_container');" rel="nofollow" href="javascript:void(0);"></a>
                    <a class="filter_header" onclick="toggleFilter('video_container');" rel="nofollow" href="javascript:void(0);">${message(code:'common.videos')}</a>
                    <ul class="search_filter_content">
                    <g:each in="${lessons}" var="item" status="i">
                      <li class="clearfix" style="width:225px;margin-right:20px">
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
                          <a name="video_${item?.id}" rel="nofollow" title="${item?.name}">
                            <img width="200" height="140" border="0" src="${urlvideo+item?.picture}" border="0" />
                            <span class="video_arrow"></span>
                          </a>
                        </div>
                        <div class="description">
                          <h2 class="title">
                            <a class="name" name="video_${item?.id}" rel="nofollow">${item?.name}</a>
                          </h2><br/>
                        </div>
                        <div id="video_lbox_${item?.id}" class="new-modal" style="width:810px;height:658px;display:none">
                          <h2 class="clearfix">${item?.name}</h2>
                          <div class="segment nopadding">
                            <object class="flashfox" type="application/x-shockwave-flash" data="${urlvideo}flashfox.swf" width="800" height="600">
                              <param name="movie" value="${urlvideo}flashfox.swf" />
                              <param name="wmode" value="transparent" />
                              <param name="allowfullscreen" value="true" />
                              <param name="flashvars" value="autoplay=true&controls=true&src=${urlvideo+item?.video}" />
                              <embed type="application/x-shockwave-flash" src="${urlvideo+item?.video}" quality="high" width="800" height="600" />
                            </object>
                          </div>
                        </div>
                      </li>
                    </g:each>
                    </ul>
                  </li>
                </ul>
              </td>
              <td colspan="3" valign="top">            
                <div id="search_body" class="body shadow">
                  <div class="page-topic" style="border:none;${notice?'padding-bottom:0':''}">                
                    <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                  </div>
                <g:if test="${notice}">
                  <div class="page-topic" style="border:none">
                  <g:each in="${notice}" var="it" status="i">
                    <div class="notice ads width clearfix" onclick="noticeClick(${it.id})">
                      <g:if test="${it?.is_index}"><a href="${serverUrl.toString()+'/'+it?.url}" title="${it['title'+context?.lang]}" target="_blank"></g:if>
                      <g:else><span class="link" onclick='transit(${context.is_dev?1:0},["${it?.url}"],"","","","${context?.lang}")'></g:else>
                        <img class="thumbnail userpic" src="${resource(dir:'images',file:(it?.image)?'anonses/'+it?.image:'user-default-picture.png')}"/>
                        <div class="description">
                          <h2>${it['title'+context?.lang]}</h2>
                          <p>${it['description'+context?.lang]}</p>
                        </div>
                      <g:if test="${it?.is_index}"></a></g:if><g:else></span></g:else>
                    </div>
                  </g:each>
                  </div>
                </g:if>
                </div>
              </td>
            </tr>          
  </body>
</html>
