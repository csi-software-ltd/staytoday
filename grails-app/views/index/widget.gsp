﻿<html>
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
    <meta property="og:type" content="object" />
    <meta property="og:site_name" content="StayToday" />
    <meta property="og:locale" content="${context?.lang?'en_US':'ru_RU'}" />
    <meta property="og:locale:alternate" content="${context?.lang?'ru_RU':'en_US'}" />    
    <meta property="og:url" content="${createLink(controller:'index',action:'widget',base:context?.mainserverURL_lang)}" />        
    <meta property="og:title" content="${infotext['title'+context?.lang]?:''}" />
    <meta property="og:description" content="${infotext['description'+context?.lang]?:''}" />
    <meta property="og:image" content="${resource(dir:'images',file:'logo_large.png',base:context?.mainserverURL_lang)}" />
    <link rel="canonical" href="${context.curl}" />    
    <link rel="image_src" href="${resource(dir:'images',file:'logo_large.png',absolute:true)}" />    
    <meta name="layout" content="main" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'bootstrap.css')}" />
    <g:javascript>      
    function initialize(){
      VK.Widgets.Like("vk_like", {type: "button", height: 20, width: 140});
    }
    </g:javascript>      
    <style type="text/css">
      .desc h2 { background: none !important; padding-left: 0px !important; font-size: 20px !important; line-height: 24px !important }
      .share-box { margin: 6px 10px -6px 10px }
      noindex:-o-prefocus,.share-box { margin: 0px 10px } /* Opera */
    </style>
  </head>
  <body onload="initialize()">
            <tr style="height:110px">
              <td width="250" class="search">
                <a class="button" rel="nofollow" onclick="<g:if test='${isLoginDenied}'>showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>             
              </td>
              <td colspan="3" class="rent ss">
                <h1 class="header" style="width:710px">${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr>
              <td colspan="4" class="bg_shadow">              
                <ul class="breadcrumbs" itemscope itemtype="http://schema.org/BreadcrumbList">
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" itemprop="item">
                      <span itemprop="name">${message(code:'label.main')}</span>
                    </a><meta itemprop="position" content="1" />
                  <li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <span itemprop="item">
                      <span itemprop="name">${infotext['name'+context?.lang]?:''}</span>
                    </span><meta itemprop="position" content="2" />
                  </li>
                </ul>
              </td>
            </tr>
            <tr>
              <td colspan="4" class="body shadow"><!--noindex-->	
                <div class="bg_shadow padd10" style="background-color:transparent">
                  <ul class="share-box">
                    <li class="share-item">
                      <a class="twitter-share-button twitter-count-horizontal" href="http://twitter.com/share" data-counturl="${context?.serverURL}${(context?.is_dev)?'/'+context?.appname:''}/index/mobile" data-count="horizontal" data-lang="${context?.lang?'en':'ru'}" rel="nofollow" target="_blank">${message(code:'detail.tweet')}</a>
                    </li>
                    <li class="share-item">
                      <div class="fb-like" data-width="90" data-layout="button_count" data-action="like" data-show-faces="true" data-share="false" data-font="tahoma"></div>
                    </li>
                    <li class="share-item">
                      <g:plusone size="medium" count="true"></g:plusone>
                    </li>          
                    <li class="share-item">
                      <div id="vk_like"></div>
                    </li>
                    <li class="share-item" id="mm">
                      <a class="mrc__plugin_uber_like_button" href="http://connect.mail.ru/share" rel="nofollow" target="_blank" data-mrc-config="{'type':'button','caption-mm':'1','caption-ok':'1','counter':'true','width':'100%','nt':'1'}">Нравится</a>
                      <script src="http://cdn.connect.mail.ru/js/loader.js" type="text/javascript" charset="UTF-8"></script>
                    </li>
                  </ul>
                </div><!--/noindex-->
                <div style="width:100%;min-height:534px">
                  <div class="clearfix" style="padding:15px">
                    <div class="desc">
                      <h2>${infotext['promotext1'+context?.lang]?:''}</h2>
                      <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                    </div>
                    <div class="float" style="width:48%">                        
                      <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                    </div>
                    <div class="col" style="width:48%">
                      <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml> 
                    </div>
                  </div>
                </div><!--noindex-->
                <g:plusoneScript lang="${context?.lang?'en-US':'ru'}" /><!--/noindex-->
              </td>
            </tr>          
  </body>
</html>
