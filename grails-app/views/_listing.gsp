<g:javascript>
  $('ads_count').update(${count});
  if(navigator.userAgent.search('Firefox')>-1)
    $('results_header').setStyle({ marginTop: '-20px' });      
  else
    $('results_header').setStyle({ marginTop: '-15px' });
</g:javascript>
<div id="results_header" class="results_header">      
  <span class="pagination">
    <g:paginate controller="home" action="search_table" maxsteps="7" prev="&lt;" next="&gt;"
      max="${inrequest.max}" total="${count}"/> 
    <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>       
  </span>
  <span class="actions col" style="margin: 6px 3px">
    <a class="icon s print" rel="nofollow" onclick="setFilterFull();openPrintPage();">${message(code:'common.print_version')}</a>
  </span>
</div>
<div id="tableAJAX" style="clear:both">
  <g:if test="${records}">          
      <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
      <g:each in="${records}" var="home" status="i"><!--noindex-->
      <g:javascript>
        var sText = ${Math.round(home?.price / valutaRates)} + "<g:rawHtml>${valutaSym}</g:rawHtml>";
        var sBalloon='<div class="map_number">${i+1}</div>'+
          '<div class="thumbnail shadow" style="width:220px;height:160px">'+
          '  <img width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />';
        <g:if test="${home?.curdiscount}">              
        sBalloon+='<div class="discount-container">'+
          '    <div class="discount-price">';
          <g:if test="${home?.curdiscount==2}">sBalloon+='-${home?.hotdiscounttext}';</g:if>
          <g:elseif test="${home?.curdiscount==1}">sBalloon+='-${home?.longdiscounttext}';</g:elseif>
          <g:else>sBalloon+='-${home?.hotdiscounttext ? home.hotdiscounttext : home.longdiscounttext}';</g:else>
        sBalloon+='</div>'+
          '  </div>';                      
        </g:if><g:if test="${home?.is_fiesta}">
        sBalloon+='<div class="fiesta-container" title="${message(code:'common.party')}"></div>';
        </g:if>                
        sBalloon+='</div><div class="photo_item_details">';
        <g:if test="${Client.get(home?.client_id)?.is_reserve}">
        sBalloon+='<ul class="services-list photo">'+
          '    <li class="services-list-item bron">'+
          '      <span class="icons active" title="${message(code:'common.booking_through_site')}">&nbsp;</span>'+
          '    </li>'+
          '  </ul>';
        </g:if>
        sBalloon+='<span class="ss_price b-rub" style="padding-left:${Client.get(home?.client_id)?.is_reserve?'40':'4'}px !important">'+sText+'</span>'+
          '</div><div class="description" style="padding-left:0px">'+        
          '  <h2 class="title" style="padding-left:0;margin-top:10px"><g:shortString text="${home?.name}" length="16"/></h2>'+              
          '  <ul class="details-list" style="clear:left;width:205px">'+
          '    <li class="details-list-item location"><g:shortString text="${home?.shortaddress}" length="30"/></li>'+
          '  </ul>'+      
          '</div>';
        lsHomes.push([${home?.x?:0},${home?.y?:0},${i+1},0,"${home?.name?:''}",sBalloon,${(inrequest?.view?:'')=='map'},"home_"+"${home?.linkname}".unescapeHTML().replace(/&#39;/g, "\'"),"${string?.date_start};${string?.date_end};${inrequest?.homeperson_id}","${Country.get(home.country_id)?.urlname?:''}","${home.city}"])                         
      </g:javascript><!--/noindex--> 
      <g:if test="${inrequest?.view=='list'}">
        <g:if test="${i==listBannerLine}"><!--noindex-->                 
          <tr>
            <td style="padding:0 5px">    
              <div class="notice ads mini clearfix" style="margin-right:10px">
                <img class="thumbnail userpic" src="${resource(dir:'images/anonses',file:'bron.png')}" alt="" border="0" />
                <div class="description">
                  ${message(code:'list.safe_payment.want')} <b><g:link class="link" controller="index" action="safety" base="${context?.absolute_lang}" rel="nofollow">${message(code:'list.safe_payment.safe')}</g:link></b> ?<br/> 
                  <g:rawHtml>${message(code:'list.safe_payment.search')}</g:rawHtml> <g:link class="tooltip" controller="index" action="oferta" fragment="use_6" base="${context?.absolute_lang}" rel="nofollow" title="${message(code:'common.secure_payments')}"><img alt="${message(code:'common.secure_payments')}" src="${resource(dir:'images',file:'bron.png')}" border="0" style="margin-bottom:-5px" /></g:link>
                </div>
              </div>
              <div class="notice ads mini clearfix">
                <img class="thumbnail userpic" src="${resource(dir:'images/anonses',file:'waiting.png')}" alt="" border="0" />
                <div class="description">                  
                  <g:rawHtml>${message(code:'list.waiting_info')}</g:rawHtml>
                  <g:link class="tooltip" controller="help" action="traveling_functions" fragment="question_275" base="${context?.absolute_lang}" rel="nofollow" title="${message(code:'common.what_is_waiting_list')}"><img alt="${message(code:'common.what_is_waiting_list')}" src="${resource(dir:'images',file:'question.png')}" border="0" style="margin-bottom:-3px" /></g:link>${message(code:'list.waiting_info.leaving')} 
                  <b class="link" onclick="$('waitingform').submit()">${message(code:'list.waiting_info.request')}</b>
                </div>
              </div>
            </td>
          </tr>
          <tr><td class="dashed">&nbsp;</td></tr>
          <tr style="height:160px">
            <td id="banner_place" style="padding:5px 0"></td>
          </tr>
          <tr><td class="dashed">&nbsp;</td></tr><!--/noindex-->          
        </g:if>
        <tr>
          <td onmouseout="this.removeClassName('selected');removeMarkers()" onmouseover="this.addClassName('selected');addMarker(${home.x},${home.y},${i+1},1);">
            <div class="hlisting offer-rent relative">
              <div class="map_number">${i+1}</div>
              <div class="item housing thumbnail shadow">
                <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.get(home.city_id)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}' params='${[0:0].inject(City.get(home.city_id)?.domain?[linkname:home?.linkname]:[linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:home.city],linkparams)}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${message(code:'common.description_and_photos')} ${infotags.hometyper} ${home?.name}"></g:if>
                <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'${[0:0].inject([:],linkparams).collect{it.key+"="+it.value}.join(",")}','','','${context?.lang}')" title="${home?.name}"></g:else>
                  <img class="photo" width="200" height="140" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
              <g:if test="${home?.curdiscount}">
                <div class="discount-container">                
                  <div class="discount-price">
                    <g:if test="${home?.curdiscount==2}">-${home?.hotdiscounttext}</g:if>
                    <g:elseif test="${home?.curdiscount==1}">-${home?.longdiscounttext}</g:elseif>
                    <g:else>-${home?.hotdiscounttext ? home.hotdiscounttext : home.longdiscounttext}</g:else>
                  </div>
                </div>
              </g:if><g:if test="${home?.is_fiesta}">
                <div class="fiesta-container" title="${message(code:'common.party')}"></div>
              </g:if><g:if test="${Client.get(home?.client_id)?.is_reserve}">
                <ul class="services-list list">
                  <li class="services-list-item bron"><span class="icons active" title="${message(code:'common.booking_through_site')}"></span></li>
                </ul>
              </g:if>
              </div>                                
              <div class="photo_item_details list">
                <span class="price ss_price b-rub" style="padding-left:40px!important">
                  ${Math.round(home?.price / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
                </span>
                <span class="ss_period">${message(code:'list.per_night')}</span>
              <g:if test="${alike?:0}">
                <span class="ss_distance">
                  <g:if test="${alikeDistances&&alikeDistances[i]!=0}">${alikeDistances[i]} ${message(code:'common.km_from_choosen')}</g:if>
                  <g:else>${message(code:'common.same_place')}</g:else>
                </span>
              </g:if>
              </div>              
              <div class="fn title">
                <g:if test="${home.is_index}"><a class="url name" href="<g:createLink mapping='${City.get(home.city_id)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}' params='${[0:0].inject(City.get(home.city_id)?.domain?[linkname:home?.linkname]:[linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:(City.get(home.city_id?:0)?City.get(home.city_id?:0)?.('name'+context?.lang):home.city)],linkparams)}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="self bookmark" title="${home?.name}"></g:if>
                <g:else><span class="fn name" title="${home?.name}" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${City.get(home.city_id?:0)?City.get(home.city_id?:0)?.('name'+context?.lang):home.city}','${Country.get(home.country_id)?.urlname}'],'${[0:0].inject([:],linkparams).collect{it.key+"="+it.value}.join(",")}','','','${context?.lang}')"></g:else>
                  <g:shortString text="${home?.name}" length="56"/>
                <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                <a id="star_${home?.id}" class="star_icon_container" href="javascript:void(0)" rel="nofollow" title="${message(code:'common.add_to_favourite')}">
                  <span id="star_icon_${home?.hid}" onclick="addtofavourite(${home?.hid})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.hid)}">starred</g:if>"></span>
                </a>
              </div>            
              <div class="description">
                <ul class="details-list">
                  <li class="details-list-item location clearfix">
                    <span class="icons"></span>
                    <g:shortString text="${home?.shortaddress}" length="56"/>
                  </li>
                <g:if test="${!inrequest?.hometype_id}">
                  <li class="details-list-item room_type clearfix">
                    <span class="icons"></span>
                  <g:each in="${hometype}" var="item"><g:if test="${item?.id==home?.hometype_id}">
                    ${item?.('name'+context?.lang)}
                  </g:if></g:each>
                  </li>
                </g:if>
                  <li class="details-list-item person_capacity clearfix">
                    <span class="icons"></span>
                  <g:each in="${homeperson}" var="item"><g:if test="${item?.id==home?.homeperson_id}">
                    ${item?.('name'+context?.lang)}
                  </g:if></g:each>
                  </li>
                  <li class="details-list-item review_count clearfix">
                    <span class="icons"></span>
                    ${home?.nref} ${message(code:'common.reviews')}
                  </li>
                </ul>
              </div>
              <div class="lister fn">${User.findWhere(client_id:home?.client_id).nickname}</div>
              <ul class="services-list col">
              <g:each in="${homeoption}"><g:if test="${it.icon}">
                <li class="services-list-item ${it.icon}">
                  <span class="icons ${home[it.fieldname]?'active':'passive'}" title="${it?.('name'+context?.lang)}"></span>                
                </li>              
              </g:if></g:each>
              </ul>              
            </div>
          </td>
        </tr>
        <g:if test="${i+1!=records.size()}">
        <tr><td class="dashed">&nbsp;</td></tr>
        </g:if>
      </g:if><g:elseif test="${inrequest?.view=='photo'}">
        <g:if test="${(i % 3)==0 && (i / 3)==photoBannerLine}"><!--noindex-->        
        <tr>
          <td colspan="3" style="padding:0 5px">    
            <div id="waiting" class="notice" style="margin:5px 0px">
              <g:rawHtml>${message(code:'list.waiting_info')}</g:rawHtml>
              <g:link class="tooltip" controller="help" action="traveling_functions" fragment="question_275" base="${context?.absolute_lang}" rel="nofollow" title="${message(code:'common.what_is_waiting_list')}"><img alt="${message(code:'common.what_is_waiting_list')}" src="${resource(dir:'images',file:'question.png')}" border="0" style="margin-bottom:-3px" /></g:link>${message(code:'list.waiting_info.leaving')} 
              <b class="link" onclick="$('waitingform').submit()">${message(code:'list.waiting_info.request')}</b>
            </div>  
          </td>
        </tr>
        <tr><td colspan="3" class="dashed">&nbsp;</td></tr>
        <tr style="height:160px">
          <td colspan="3" id="banner_place" style="padding:5px 0"></td>
        </tr>
        <tr><td colspan="3" class="dashed">&nbsp;</td></tr>        
        <tr>
          <td colspan="3" style="padding:0 5px">    
            <div id="waiting" class="notice" style="margin:5px 0px">
              ${message(code:'list.safe_payment.want')} <b><g:link class="link" controller="index" action="safety" base="${context?.absolute_lang}" rel="nofollow">${message(code:'list.safe_payment.safe')}</g:link></b> ? 
              <g:rawHtml>${message(code:'list.safe_payment.search')}</g:rawHtml> <g:link class="tooltip" controller="index" action="oferta" fragment="use_6" base="${context?.absolute_lang}" rel="nofollow" title="${message(code:'common.secure_payments')}"><img alt="${message(code:'common.secure_payments')}" src="${resource(dir:'images',file:'bron.png')}" border="0" style="margin-bottom:-5px" /></g:link>
            </div>  
          </td>
        </tr>        
        <tr><td colspan="3" class="dashed">&nbsp;</td></tr><!--/noindex-->        
        </g:if>
        <g:if test="${(i % 3)==0}"><tr></g:if>      
          <td onmouseout="this.removeClassName(' selected');removeMarkers()" onmouseover="this.addClassName(' selected');addMarker(${home.x},${home.y},${i+1},1);">
            <div class="relative" style="width:220px">
              <div class="map_number">${i+1}</div>
              <div class="thumbnail shadow" style="width:220px;height:160px">
                <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.get(home.city_id)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}' params='${[0:0].inject(City.get(home.city_id)?.domain?[linkname:home?.linkname]:[linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:home.city],linkparams)}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                <g:else><span class="pointer" title="${home?.name}" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'${[0:0].inject([:],linkparams).collect{it.key+"="+it.value}.join(",")}','','','${context?.lang}')"></g:else>
                  <img width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0"/>
                <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
              <g:if test="${home?.curdiscount}">
                <div class="discount-container photo">
                  <div class="discount-price">
                    <g:if test="${home?.curdiscount==2}">-${home?.hotdiscounttext}</g:if>
                    <g:elseif test="${home?.curdiscount==1}">-${home?.longdiscounttext}</g:elseif>
                    <g:else>-${home?.hotdiscounttext ? home.hotdiscounttext : home.longdiscounttext}</g:else>
                  </div>
                </div>
              </g:if><g:if test="${home?.is_fiesta}">
                <div class="fiesta-container photo" title="${message(code:'common.party')}"></div>
              </g:if>
              </div>
              <div class="photo_item_details">
              <g:if test="${Client.get(home?.client_id)?.is_reserve}">
                <ul class="services-list photo">
                  <li class="services-list-item bron">
                    <span class="icons active" title="${message(code:'common.booking_through_site')}">&nbsp;</span>                
                  </li>
                </ul>
              </g:if>
                <span class="ss_price b-rub" style="padding-left:${Client.get(home?.client_id)?.is_reserve?'40':'4'}px !important">
                  ${Math.round(home?.price / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
                </span>                            
              </div>
              <div class="description" style="padding-left:0px">
                <h2 class="title" style="padding-left:0px;margin-top:10px">
                  <g:if test="${home.is_index}"><a class="name" href="<g:createLink mapping='${City.get(home.city_id)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}' params='${[0:0].inject(City.get(home.city_id)?.domain?[linkname:home?.linkname]:[linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:home.city],linkparams)}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                  <g:else><span class="name" title="${home?.name}" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'${[0:0].inject([:],linkparams).collect{it.key+"="+it.value}.join(",")}','','','${context?.lang}')"></g:else>
                    <g:shortString text="${home?.name}" length="16"/>
                  <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                  <a id="star_${home?.id}" class="star_icon_container" href="javascript:void(0)" rel="nofollow" title="${message(code:'common.add_to_favourite')}">
                    <span id="star_icon_${home?.hid}" onclick="addtofavourite(${home?.hid})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.hid)}">starred</g:if>"></span>
                  </a>
                </h2>              
                <ul class="details-list" style="width:205px">              
                  <li class="details-list-item location">
                    <g:shortString text="${home?.shortaddress}" length="30"/>                  
                  </li>
                </ul>
              </div>
            </div>        
          </td>
        <g:if test="${((i % 3)==0) && (count == 1)}">
          <td width="33%">&nbsp;</td><td width="33%">&nbsp;</td>
        </g:if><g:elseif test="${((i % 3)==1) && (count == 2)}">
          <td width="33%">&nbsp;</td>
        </g:elseif><g:elseif test="${(i % 3)==2}">
        </tr>
        <g:if test="${i+1!=records.size()}"><tr><td colspan="3" class="dashed">&nbsp;</td></tr></g:if>
        </g:elseif>                        
      </g:elseif>
    </g:each> 
      </table>     
    <g:if test="${inrequest?.view!='map'}"><!--noindex-->                      
      <div class="slideshow" style="padding:0 0 28px 0;background-color:#E0EFEF">
      <g:if test="${count>6}">
        <div class="results_header">    
          <span class="pagination">
            <g:paginate controller="home" action="search_table" maxsteps="7" prev="&lt;" next="&gt;"
            max="${inrequest.max}" total="${count}"/> 
            <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>       
          </span>
          <span class="actions col" style="margin:6px 3px">
            <a class="icon s print" rel="nofollow" onclick="setFilterFull();openPrintPage();">${message(code:'common.print_version')}</a>                              
          </span>
        </div>
      </g:if>
      </div><!--/noindex-->
    </g:if>
  </g:if><g:else>
    <g:if test="${flash.error}">
      <div id="notice" class="notice">
      <g:if test="${flash.error.contains(1)}"><b><g:rawHtml>${message(code:'list.incorrect.check_in')}</g:rawHtml></b></g:if>
      <g:if test="${flash.error.contains(2)}"><b><g:rawHtml>${message(code:'list.incorrect.sequence.check_in.check_out')}</g:rawHtml></b></g:if>
      </div>
    </g:if>
    <g:elseif test="${inrequest?.where!=''}">                        
      <div style="padding:0 15px 10px 12px">    
        <p>${message(code:'list.not_found')}</p>
      <g:if test="${inrequest?.view!='map'}">
        <div id="waiting" class="notice" style="margin:5px 0px">
          <g:rawHtml>${message(code:'list.waiting_info')}</g:rawHtml>
          <g:link class="tooltip" controller="help" action="traveling_functions" fragment="question_275" base="${context?.absolute_lang}" rel="nofollow" title="${message(code:'common.what_is_waiting_list')}"><img alt="${message(code:'common.what_is_waiting_list')}" src="${resource(dir:'images',file:'question.png')}" border="0" style="margin-bottom:-3px" /></g:link>${message(code:'list.waiting_info.leaving')} 
          <b class="link" onclick="$('waitingform').submit()">${message(code:'list.waiting_info.request')}</b>
        </div>  
      </g:if>      
      </div>
    </g:elseif>
  </g:else>
</div>
