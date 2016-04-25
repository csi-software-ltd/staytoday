﻿<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl}" />    
    <meta name="layout" content="main" />
    <g:javascript>
      jQuery(function(){
        jQuery('#slider').slides({          
          preload: false,
          preloadImage: '${resource(dir:"images",file:"spinner.gif")}',
          play: 50000,
          pause: 2500,
          hoverPause: true,
          generatePagination: false,
          pagination: true,
          paginationClass: 'pagination',
          currentClass: 'current'
        });
      });      
    </g:javascript> 
    <style type="text/css">
      #cboxTitle { display: none !important }
      a.name { display: block !important }
      .subnav li { width: 450px; text-align: center; font-size: 14px !important; }
      .dashboard-content h2 { margin: 0 20px 10px !important; }      
      .slides_container { width: 850px; height: 340px; overflow: hidden; position: relative; display: block; }
      .slides_control { position: relative; }
      .slides_control .slide { position: absolute; top: 0px; left: 0px; z-index: 0; display: none; clear: both; }
      .slides_control .slide.current { display: block; }
      .slides_control .slide .img { width: 430px }
      .slides_control .slide .txt { width: 400px; padding-left: 20px; text-align: justify; }
      .pagination { width: 950px; margin-bottom: 25px; }
      .pagination li { float: left; width: 190px; }      
      .pagination li a { text-align: center; text-decoration: none; color: #333; font-weight: bold }
      .circle { margin-left: 81px; line-height: 28px !important; min-height: 28px !important; min-width: 28px !important; border-radius: 28px !important; padding: 0px !important; }
      .pagination li .circle { background: #fff !important; filter: none !important; color: #333 !important; border-color: #333 !important; }
      .pagination li.current { display: block }     
      .pagination li.current .circle {
        color: #fff !important;
      	border-color: #eb470b !important;        
        background: #eb470b !important;
        background: linear-gradient(top, #fe520d, #eb470b) !important;
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fffe520d',endColorstr='#ffeb470b',GradientType=0) !important;
      }
      .pagination li .button-glossy:before { background: none !important }
      .caption { padding: 5px 30px }       
      .page-topic.btop { border-bottom: none; }
    </style>        
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" class="search s0">
                <a class="button" rel="nofollow" onclick="<g:if test='${isLoginDenied}'>showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>
              </td>
              <td width="730" colspan="3" class="rent ss">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>                
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
                      <span itemprop="name">${infotext['name'+context?.lang]?:''}</span>
                    </span><meta itemprop="position" content="2" />
                  </li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <span itemprop="item">
                      <span itemprop="name">${message(code:'label.travelers')}</span>
                    </span><meta itemprop="position" content="3" />
                  </li>
                </ul>
              </td>
            </tr>            
            <tr>
              <td colspan="4" valign="top" class="body shadow">
                <div class="page-topic" style="border:none">
                  <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>                
                </div>
                <div class="content" style="min-height:550px">
                  <ul id="tabs" class="subnav" align="center">
                    <li class="selected">${message(code:'label.travelers')}</li>
                    <li><g:link controller="index" action="safety_hosting" base="${context.absolute_lang}">${message(code:'label.owners')}</g:link></li>
                  </ul>
                  <div class="bg_shadow" style="height:20px"></div>
                  <div id="layers" class="dashboard-content">                  
                    <div rel="layer">
                      <h2 class="ins">${infotext['promotext1'+context?.lang]?:''}</h2>
                      <div id="slider" class="paddtop relative" align="center">
                        <div style="margin-bottom:-15px;width:780px;height:1px;background-color:#757575;z-index:1"></div>
                        <ul class="pagination">
                        <g:each in="${howto}" var="item" status="i">
                          <li>
                            <a rel="${i} nofollow" href="#">
                              <div class="button-glossy circle">${item?.number}</div>
                              <div class="caption">${item['name'+context?.lang]}</div>
                            </a>                        
                          </li>
                        </g:each>
                        </ul>                        
                        <div class="slides_container">
                        <g:each in="${howto}" var="item">
                          <div class="slide"> 
                            <div class="img float">
                              <img src="${resource(dir:'images/howto',file:item?.picture)}" width="430">
                            </div>
                            <div class="txt col">
                              <h3>${item['name'+context?.lang]}</h3>
                              <g:rawHtml>${item['description'+context?.lang]?:''}</g:rawHtml>
                            </div>
                          </div>                          
                        </g:each>
                        </div>
                        <a id="cboxPrevious" class="prev" href="#" rel="prev nofollow"></a>
                        <a id="cboxNext" class="next" href="#" rel="next nofollow"></a>
                      </div>
                    </div>
                  </div>                      
                </div>      
                <div class="page-topic btop">
                  <h2 class="padd20">${infotext['promotext2'+context?.lang]?:''}</h2>
                  <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                </div>
              <g:if test="${notice}">
                <div class="clearfix page-topic btop">                
                <g:each in="${notice}" var="it" status="i">
                  <div class="notice ads clearfix" style="margin-right:${(i%2)==0?'16':'0'}px" onclick="noticeClick(${it.id})">
                    <g:if test="${it?.is_index}"><a href="${serverUrl.toString()+'/'+it?.url}" title="${it['title'+context?.lang]}"></g:if>
                    <g:else><span class="link" onclick='transit(${context.is_dev?1:0},["${it?.url}"],"","","","${context?.lang}")'></g:else>
                      <img class="thumbnail userpic" src="${resource(dir:'images',file:(it?.image)?'anonses/'+it?.image:'user-default-picture.png')}" border="0" />
                      <div class="description" style="width:335px !important">
                        <h2>${it['title'+context?.lang]}</h2>
                        <p>${it['description'+context?.lang]}</p>
                      </div>
                    <g:if test="${it?.is_index}"></a></g:if><g:else></span></g:else>
                  </div>
                </g:each>
                </div>
              </g:if>                
              </td>
            </tr>    
  </body>
</html>
