<html>
  <head>  
    <title>${infotext['title'+context?.lang]?:' '}</title>  
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl}" />
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
      function init(){
      <g:if test="${!(user?.is_subscribe?:0)}">
        $("subscribe").show();
        $("spec").show();
      </g:if><g:else> 
        $("spec_already").show();    
      </g:else>
      } 
      function processResponse(e){
        var iErr=e.responseJSON.error;
        $("errors").hide();
        $("error1").hide();
        $("error2").hide();
        if(iErr==1){
          $("errors").show();
          $("error1").show();
        }  
        if(iErr==2){
          $("errors").show();
          $("error2").show();  
        }  
        if(iErr==3 || iErr==4){
          $("spec").hide();
          $("subscribe").hide();
          $("spec_already").show();
          if(iErr==4)         
            location.reload(true);
        }
      }
    </g:javascript>
    <style type="text/css">
      .form { background: none !important; }
      .form label { min-width: 50px !important; padding: 0 10px !important; }
      .form input[type="text"] { width: 125px !important; }
      .notice { margin: 5px 0 !important; } 
      .notice ul li { line-height: 15px !important; color: #333 !important }
      .page-topic h2.ins { margin: 10px 12px !important }
      .ss_name { font: bold 14px/30px Arial !important; padding: 0 9px !important; }
      .details .ss_period { margin: 0px !important; height: 40px;}
      .page-topic.noborder { padding: 10px 0; border: none }
    </style>
  </head>
  <body onload="init()">
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
              <td valign="top">
                <ul class="collapsable_filters">
                  <li class="search_filter" id="subscribe_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('subscribe_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('subscribe_container');">${message(code:'specoffer.newsletter')}</a>        
                    <div class="search_filter_content" style="padding: 15px 6px">
                      <div id="subscribe" style="display:none">
                        <p style="margin:0 3px">${infotext['promotext1'+context?.lang]?:''}</p>
                      </div>                      
                      <g:formRemote name="sendForm" url="[controller:'index',action:'tospecoffer']" method="post" useToken="true" update="[success:'book']" onLoading="document.getElementById('loader').show();" onLoaded="document.getElementById('loader').hide();" onSuccess="processResponse(e)">
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                        <g:if test="${!user}">        
                          <tr>
                            <td style="padding-bottom:20px">
                              <b>${message(code:'waiting.feedback')}</b>
                            </td>
                          </tr>
                          <tr>
                            <td>
                              <label for="reg_nickname">${message(code:'register.nickname')}</label>
                              <input type="text" id="mbox_nickname" name="nickname" maxlength="${stringlimit}" value="" />
                            </td>
                          </tr>
                          <tr>
                            <td>
                              <label for="reg_email">email</label>
                              <input type="text" id="mbox_email" name="email" maxlength="${stringlimit}" value="" />
                            </td>
                          </tr>        
                        </g:if>
                          <tr>
                            <td>            
                              <span class="float" id="spec" style="padding-top:15px;display:none">
                                <input id="specButton" class="button-glossy orange" type="submit" value="${message(code:'specoffer.subscribe')}"/>                   
                              </span>
                            <g:if test="${!user}"> 
                              <b class="col" style="padding-top:23px">
                                <a href="javascript:void(0)" rel="nofollow" onclick="showLoginForm(0,'login_lbox',this);$('mail_lbox').hide()">${message(code:'label.login')}</a>
                              </b>
                            </g:if>
                            </td>
                          </tr>
                        </table>        
                      </g:formRemote>
                      <div id="spec_already" style="display:none">
                        <p style="margin:0 3px"><g:rawHtml>${infotext['promotext2'+context?.lang]?:''}</g:rawHtml></p>
                      </div>   
                      <div id="loader" class="spinner" style="display: none">
                        <img src="${resource(dir:'images',file:'spinner.gif')}" border="0">
                      </div>                      
                      <div id="errors" class="notice" style="display:none">
                        <ul>       
                          <li id="error1" style="display:none">${message(code:'error.blank',args:['Email'])} ${message(code:'faq.error.email')}</li>
                          <li id="error2" style="display:none">${message(code:'login.error.notunique',args:['Email'])} ${message(code:'register.or')} ${message(code:'login.auth')}</li>
                          <!--<g:if test="${it==3}"><li>Вы подписались на рассылку новостей</li></g:if>-->                   
                        </ul>       
                      </div>
                    </div>
                  </li>
                </ul>
              </td>
              <td valign="top" colspan="3">              
                <div class="body shadow" style="min-height:485px">
                <g:if test="${infotext['itext'+context?.lang]}">
                  <div class="page-topic">
                    <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                  </div>
                </g:if>                
                <g:if test="${popcity}">
                  <div class="page-topic noborder">
                    <h2 class="ins">${message(code:'specoffer.special')}</h2>
                    <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                    <g:each in="${popcity}" var="record" status="i">
                      <g:if test="${(i % 3)==0}"><tr></g:if>
                        <td width="33%" onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
                          <div class="relative" style="width:220px">                                                  
                            <div class="thumbnail shadow" style="width:220px;height:160px">
                              <img width="220" height="160" src="${resource(dir:'images',file:record?.picture?'cities/'+record.picture:'default-picture.png')}" alt="${record?.name}" border="0" />
                            </div>
                            <div class="slideshow_item_details">
                              <div class="slideshow_item_details_text">
                                <div class="ss_details_top">
                                  <span class="ss_name">
                                    ${message(code:'specoffer.prices.from')}                                    
                                  </span>                                  
                                </div>
                              </div>
                            </div>
                            <div class="photo_item_details">
                              <span class="ss_price b-rub"> 
                                ${Math.round(minPrice[i] / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
                              </span>
                            </div>                            
                            <div class="description" style="padding-left:0px">                            
                              <h2 class="title clearfix" style="padding-left:0px;margin:10px 0">
                                <g:if test="${record.is_index}"><a class="name" href="<g:createLink class='city' mapping='${record?.domain?'mainpage'+context?.lang:'hSearch'+context?.lang}' params='${(record.domain?[]:[where:record['name'+context?.lang],country:Country.get(record?.country_id)?.urlname])}' base='${record.domain?'http://'+record.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>"></g:if>
                                <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${record['name'+context?.lang]}','${Country.get(record?.country_id)?.urlname}'],'','','','${context?.lang}')"></g:else>
                                  ${record['name'+context?.lang]}
                                <g:if test="${record.is_index}"></a></g:if><g:else></span></g:else>  
                              </h2>
                              <ul class="search_filter_content" style="width:220px;padding:0">
                                <li class="clearfix">
                                  <b style="color:#333">${message(code:'specoffer.prices.average')}</b>
                                </li>
                                <li class="clearfix dott">
                                  <span class="facet_count">${Math.round(avgPrice1[i] / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                                  <g:if test="${record.is_index}"><a href="<g:createLink class='city' mapping='${record?.domain?'hSearchRoomDomain'+context?.lang:'hSearchRoom'+context?.lang}' params='${record.domain?[type_url:'flats',bedroom:1]:[where:record['name'+context?.lang],country:Country.get(record?.country_id)?.urlname,bedroom:1,type_url:'flats']}' base='${record.domain?'http://'+record.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>"></g:if>
                                  <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['room_1','type_flats','${record['name'+context?.lang]}','${Country.get(record?.country_id)?.urlname}'],'','','','${context?.lang}')"></g:else>
                                    ${message(code:'specoffer.oneroom')}
                                  <g:if test="${record.is_index}"></a></g:if><g:else></span></g:else>  
                                </li>
                                <li class="clearfix">
                                  <span class="facet_count">${Math.round(avgPrice2[i] / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                                  <g:if test="${record.is_index}"><a href="<g:createLink class='city' mapping='${record?.domain?'hSearchRoomDomain'+context?.lang:'hSearchRoom'+context?.lang}' params='${record.domain?[type_url:'flats',bedroom:2]:[where:record['name'+context?.lang],country:Country.get(record?.country_id)?.urlname,bedroom:2,type_url:'flats']}' base='${record.domain?'http://'+record.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>"></g:if>
                                  <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['room_2','type_flats','${record['name'+context?.lang]}','${Country.get(record?.country_id)?.urlname}'],'','','','${context?.lang}')"></g:else>
                                    ${message(code:'specoffer.tworoom')}
                                  <g:if test="${record.is_index}"></a></g:if><g:else></span></g:else>
                                </li>
                              </ul>                              
                            </div>
                          </div>
                        </td>
                      <g:if test="${((i % 3)==0) && (poprecords.size() == 1)}">
                        <td width="33%">&nbsp;</td><td width="33%">&nbsp;</td>
                      </g:if><g:elseif test="${((i % 3)==1) && (poprecords.size() == 2)}">
                        <td width="33%">&nbsp;</td>
                      </g:elseif><g:elseif test="${(i % 3)==2}">
                        </tr>
                        <g:if test="${i+1!=poprecords.size()}"><tr><td colspan="3" class="dashed">&nbsp;</td></tr></g:if>
                      </g:elseif>                  
                    </g:each>
                    </table>
                  </div>
                </g:if>                
                <g:if test="${popdirection}">
                  <div class="page-topic noborder">
                    <h2 class="ins">${message(code:'specoffer.popdirection')}</h2>
                    <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                    <g:each in="${popdirection}" var="record" status="i">
                      <tr>
                        <td onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
                          <div class="relative float" style="width:220px;height:160px">
                            <div class="thumbnail shadow" style="width:220px;height:160px">
                              <img width="220" height="160" src="${(record?.picture)?imageurl.toString()+'t_'+record?.picture:resource(dir:'images',file:'default-picture.png')}" alt="${record?.name2}" border="0" />
                            </div>
                            <div class="slideshow_item_details">
                              <div class="slideshow_item_details_text">
                                <div class="ss_details_top">
                                  <span class="ss_name">
                                    ${message(code:'specoffer.prices.from')}
                                  </span>                                  
                                </div>
                              </div>
                            </div>
                            <div class="photo_item_details">
                              <span class="ss_price b-rub">
                                ${Math.round(minPricePopdir[i] / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
                              </span>
                            </div>
                          </div>
                          <div class="description col">
                            <h2 class="clearfix title" style="padding-left:0px;margin-top:10px">
                              <g:if test="${record.is_index}"><a class="name" href="<g:createLink controller='index' action='direction' id='${record.linkname}' params='${[country:Country.get(record?.country_id)?.urlname]}'/>"></g:if>
                              <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${record.linkname}','${Country.get(record?.country_id)?.urlname}'],'','','','${context?.lang}')"></g:else>
                                ${record['name2'+context?.lang]}
                              <g:if test="${record.is_index}"></a></g:if><g:else></span></g:else>  
                            </h2>
                            <ul class="search_filter_content" style="padding:20px 0;width:460px">                          
                            <g:if test="${hotdiscount[i]>0}">
                              <li class="clearfix dott">
                                <span class="facet_count">${hotdiscount[i]}</span>
                                <g:link controller="index" action="popdiscounts" id="${record.linkname}" params="${[country:Country.get(record?.country_id)?.urlname]}" fragment="hotdiscount">
                                  ${message(code:'common.discounts_for_hot_offers')}
                                </g:link>                                
                              </li>
                            </g:if>
                            <g:if test="${longdiscount[i]>0}">
                              <li class="clearfix dott">
                                <span class="facet_count">${longdiscount[i]}</span>
                                <g:link controller="index" action="popdiscounts" id="${record.linkname}" params="${[country:Country.get(record?.country_id)?.urlname]}" fragment="longdiscount">
                                  ${message(code:'common.discounts_for_long_offers')}
                                </g:link>
                              </li>
                            </g:if>
                            <g:if test="${poprecords[i]>0}">
                              <li class="clearfix">
                                <span class="facet_count">${poprecords[i]}</span>                              
                                <g:link controller='index' action='direction' id='${record.linkname}' params='${[country:Country.get(record?.country_id)?.urlname]}'>
                                  ${message(code:'specoffer.special')}
                                </g:link>
                              </li>
                            </g:if>                          
                          </div>                        
                        </td>
                      </tr>
                      <g:if test="${i+1!=poprecords.size()}">
                      <tr><td class="dashed">&nbsp;</td></tr>
                      </g:if>
                    </g:each>
                    </table>
                  </div>
                </g:if>                
                </div>
              </td>
            </tr>  
  </body>
</html>
