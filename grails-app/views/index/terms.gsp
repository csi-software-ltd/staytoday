﻿<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${createLink(controller:'index',action:'terms',base:context?.mainserverURL_lang)}" />
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(uri:'/terms',base:context?.mobileURL_lang)}" />
    <link rel="alternate" media="handheld" href="${createLink(uri:'/terms',base:context?.mobileURL_lang)}" />
    <meta name="layout" content="main" />
    <script type="text/javascript">
      if(window.screen.width < 640)
        window.location = "${createLink(uri:'/terms',base:context?.mobileURL_lang)}";   
    </script>
    <style type="text/css">
      ul.faq_list { margin: 15px -20px 0px !important; }
      ul.faq_list li, div.answer { padding: 8px 20px !important; text-align: justify }
      div.answer:last-of-type { border-bottom: none !important }
    </style>
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" class="search s0">
                <a class="button" rel="nofollow" onclick="<g:if test='${isLoginDenied}'>showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>
              </td>
              <td colspan="3" class="rent ss">
                <h1 class="header">${infotext['header']?:''}</h1>                
              </td>
            </tr>
            <tr>
              <td colspan="4" class="bg_shadow">              
                <ul class="breadcrumbs" itemscope itemtype="http://schema.org/BreadcrumbList">
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" itemprop="item">
                      <span itemprop="name">${message(code:'label.main')}</span>
                    </a><meta itemprop="position" content="1" />
                  </li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <span itemprop="item">
                      <span itemprop="name">${infotext['header'+context?.lang]?:''}</span>
                    </span><meta itemprop="position" content="2" />
                  </li>
                </ul>
              </td>
            </tr>
            <tr>
              <td width="980" colspan="4" valign="top">            
                <div id="search_body" class="body shadow" style="width:100%">
                  <div class="page-topic" style="border:none">
                    <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>                
                    <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                    <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
