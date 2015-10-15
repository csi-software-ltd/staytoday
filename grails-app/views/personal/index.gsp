<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main" />
    <g:javascript>
    function initialize(){
    <g:if test="${session.attention_message!='' && session.attention_message!=null}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
    </g:if>
    <g:if test="${session.attention_message_once}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message_once}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
    </g:if>    
    }    
    function toggleFilter(container){ 
      var li = $(container);                 
      if(li.className == 'search_filter'){
        li.className = 'search_filter closed open';
      } else if(li.className == 'search_filter closed open'){
        li.className = 'search_filter'; 
      } else if(li.className == 'search_filter closed'){
        li.className = 'search_filter open';
      } else if(li.className == 'search_filter open'){
        li.className = 'search_filter closed';
      }
    }	      
    </g:javascript>
  </head>
  <body onload="initialize()">
            <tr style="height:110px">
              <td width="250" class="search">
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>                                
              </td>
              <td colspan="3" class="rent s0">
                <h1 class="header">${user?.firstname?user?.firstname+' '+user?.lastname:user?.nickname}</h1>
                <g:link class="button col" mapping="${'pView'+context?.lang}" params="${[uid:'id'+user?.id]}" style="margin-right:20px;">${message(code:'personal.anketa')}</g:link>
              </td>
            </tr>
            <tr>
              <td width="250" valign="top">
                <ul class="collapsable_filters" style="width:240px">			    
                  <li class="search_filter" id="user_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('user_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('user_container');">
                      <g:if test="${user?.is_agent}">${message(code:'personal.agent')}</g:if><g:else>${message(code:'personal.owner')}</g:else>
                    </a>
                    <div class="search_filter_content">
                      <div class="description">
                        <h2 class="title">
                          <g:link class="name" mapping="${'pView'+context?.lang}" params="${[uid:'id'+user?.id]}" title="${message(code:'personal.anketa')}">${user?.nickname}</g:link>
                        </h2>                      
                        <div class="thumbnail shadow" style="height:auto;margin:5px 0 10px">
                          <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+user?.id]}" rel="nofollow" title="${message(code:'personal.anketa')}">
                            <img width="200" alt="${user?.nickname}" title="${user?.nickname}" border="0" 
                              src="${(user?.picture && !user.is_external)?imageurl:''}${(user?.picture)?user?.picture:resource(dir:'images',file:'user-default-picture.png')}">
                          </g:link>
                        </div>
                      </div>
                      <g:link class="button-glossy orange maxi" controller="profile" action="edit" rel="nofollow" base="${context?.absolute_lang}">${message(code:'button.edit')}</g:link>                      
                    </div>                                            
                  </li>
                  <li class="search_filter" id="verification_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('verification_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('verification_container');">${message(code:'personal.verification')}</a>
                    <div class="search_filter_content" style="padding: 15px 0px 15px 10px">
                      <ul class="verifications-list">
                        <li class="verifications-list-item ${(user?.modstatus==0)?'verifications-none':''}">
                          <div class="verifications-icon ${(user?.modstatus==0)?'none':''}"></div>
                          <div class="verifications-legend ${user?.provider}"></div>
                          <span class="label">${message(code:'personal.connect').capitalize()} ${user?.provider}</span>
                          <br/>
                          <span class="counts">${message(code:(user?.modstatus==1)?'personal.confirmed':'personal.notconfirmed')}</span>
                        </li>
                      <g:if test="${user?.tel}">
                        <li class="verifications-list-item ${(user?.is_telcheck==0)?'verifications-none':''}">
                          <div class="verifications-icon ${(user?.is_telcheck==0)?'none':''}"></div>
                          <div class="verifications-legend phone"></div>
                          <span class="label">${message(code:'personal.phone').capitalize()} ${message(code:(user?.is_telcheck==1)?'personal.confirmed.he':'personal.notconfirmed.he')}</span>
                          <br/>
                          <span class="counts" nowrap>${user?.tel}</span>
                        </li>
                      </g:if>                
                        <li class="verifications-list-item">
                          <div class="verifications-icon"></div>
                          <div class="verifications-legend positively_reviewed"></div>
                          <span class="label">${message(code:'personal.reviews.positive')}</span>
                          <br/>
                          <span class="counts" nowrap>${comments?.count?:0} ${message(code:'common.reviews')}</span>
                        </li>
                      </ul>              
                    </div> 
                  </li>
                  <li class="search_filter" id="links_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('links_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('links_container');">${message(code:'personal.quicklinks')}</a>
                    <div class="search_filter_content">
                      <ul>
                        <li><g:link controller="personal" action="ads" rel="nofollow" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','ads')['name'+context?.lang]}</g:link></li>
                        <li><g:link controller="personal" action="bookings" rel="nofollow" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','bookings')['name'+context?.lang]}</g:link></li>                
                        <li><g:link controller="personal" action="waiting" rel="nofollow" base="${context?.absolute_lang}">${message(code:'label.waiting')}</g:link></li>
                        <li><g:link controller="profile" action="reviews" rel="nofollow" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('profile','reviews')['name'+context?.lang]}</g:link></li>
                      <g:if test="${(wallet?:[]).size() > 0}">
                        <li><g:link controller="trip" action="favorite" rel="nofollow" base="${context?.absolute_lang}">${message(code:'label.favorite')} (${(wallet?:[]).size()})</g:link></li>
                      </g:if>
                      </ul>
                    </div>
                  </li>  
                </ul>
              </td>
              <td colspan="3" valign="top">
                <div class="content" style="min-height:510px">
                  <ul class="subnav">
                    <li class="${(controllerName=='personal' && actionName=='index')?'selected':''}">
                      <g:link controller="personal" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','index')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${(controllerName=='inbox' && actionName=='index')?'selected':''}">
                      <g:link controller="inbox" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('inbox','index')['name'+context?.lang]}</g:link>                
                    </li>
                    <li class="${(controllerName=='personal' && actionName in ['ads','adsoverview','editads','photo','homephoto','video','homevideo','map','homeprice','homeprop','calendar','infras','promote','bookings','waiting','rules','requirements'])?'selected':''}">
                      <g:link controller="personal" action="ads" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','ads')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${(controllerName=='trip' && actionName in ['next','current','previous','favorite','cancellation','waiting'])?'selected':''}">
                      <g:link controller="trip" action="next" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('trip','next')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${(controllerName=='profile' && actionName in ['edit','verifyUser','photo','reviews'])?'selected':''}">
                      <g:link controller="profile" action="edit" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('profile','edit')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${(controllerName=='account' && actionName in ['index','payout','paydocuments','payU','payUresult','liqpay','liqpayresult','wmoney','wmsuccess','wmfail','history','payorderdetail','refferals','settings','trippayment'])?'selected':''}" style="width:55px">
                      <g:link controller="account" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('account','index')['name'+context?.lang]}</g:link>
                    </li>
                  </ul>
                  <div class="dashboard-content">
                    <div rel="layer" class="paddtop">
                    <g:if test="${!user.is_external}">
                      <div class="notice" style="margin:10px 8px 20px 8px">
                        <b>${message(code:'dashboard.welcome')} StayToday!</b>
                        <div>                        
                          <p>${message(code:'dashboard.info')}</p>
                          <ul class="top">
                            <li><g:link controller="index" action="howto" base="${context?.absolute_lang}">${message(code:'dashboard.howto')}</g:link> — ${message(code:'dashboard.howto.view')}.</li>
                            <li><g:link controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'dashboard.housing')}</g:link> — ${message(code:'dashboard.housing.add')}.</li>
                            <!-- li><g:link controller="index" action="groups">Вступайте в группу</g:link> — группы позволяют вам путешествовать по интересам или по школьным связям.</li>
                            <li><g:link controller="profile" action="references">Создайте свою репутацию</g:link> — попросите друзей вас порекомендовать.</li -->
                            <li><a href="http://www.twitter.com/StayTodayRu" target="_blank">${message(code:'social.tw')}</a> — ${message(code:'dashboard.follow.twitter')}!</li>
                            <li><g:link controller="help" action="faq" base="${context?.absolute_lang}">${message(code:'dashboard.help')}</g:link> — ${message(code:'dashboard.help.view')}.</li>
                          </ul>              
                        </div>
                      </div>                      
                    </g:if>
                      
                      <div class="slideshow">
                        <h2 class="padd20">${message(code:'personal.alerts')}</h2>                        
                      <g:if test="${sys_message}">
                        <ul id="alerts" class="verifications-list" style="background:#fff">
                        <g:each in="${sys_message}" var="record" status="i">
                          <li class="${(record?.notetype_id==1)?'required':'recommended'}">
                            <g:rawHtml>${(record['notetext'+context?.lang]?:'sometext').replace('[@ID]',record.home_id.toString()).replace('[@NAME]',Home.get(record.home_id)?.name?:'').replace('[@LNAME]',Home.get(record.home_id)?.linkname?:'')}</g:rawHtml>
                          </li>
                        </g:each>                
                        </ul>              
                      </g:if><g:else>                        
                        <div style="padding:10px;background:#fff">
                          <b><font color="#bdd4de">${message(code:'personal.alerts.empty')}</font></b>
                        </div>
                      </g:else>                        
                      </div>                     
                      
                      <div class="slideshow">
                        <h2 class="padd20">${message(code:'personal.mbox')} (${msg_unread?.count?:0} ${message(code:'personal.mbox.new')})</h2>                                              
                        <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0" style="background:#fff">
                      <g:if test="${msg_unread.count}">
                        <g:each in="${msg_unread?.records}" var="record" status="i">
                          <tr>                
                            <td onmouseover="this.addClassName('selected');" onmouseout="this.removeClassName('selected');">
                              <div class="relative float" style="margin-bottom:10px">                    
                                <div class="thumbnail userpic shadow">
                                <g:if test="${record?.homeclient_id == user?.client_id}">        
                                  <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+record?.client_id]}">
                                    <img width="68" height="68" alt="${record?.client_nickname}" title="${record?.client_nickname}" 
                                      src="${(record?.client_picture && !record?.client_external)?imageurl:''}${(record?.client_picture)?record?.client_picture:resource(dir:'images',file:'user-default-picture.png')}">
                                  </g:link>
                                </g:if><g:else>
                                  <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(record.home_country_id)?.urlname,city:record.home_city,linkname:record?.linkname]}">
                                    <img width="68" height="68" alt="${record?.home_name}" title="${record?.home_name}" 
                                      src="${(record?.homepicture)?urlphoto+record?.homeclient_id+'/t_'+record?.homepicture:resource(dir:'images',file:'default-picture.png')}">
                                  </g:link>        
                                </g:else>
                                </div>
                                <div style="width:68px">
                                  <small style="white-space:normal">
                                  <g:if test="${(Home.get(record?.home_id)?.client_id?:0) == user?.client_id}">                
                                    <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+record?.client_id]}">${record?.client_nickname}</g:link>
                                  </g:if><g:else>
                                    <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+record?.user_id]}">${record?.user_nickname}</g:link>
                                  </g:else>
                                  </small>
                                  <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',record.inputdate)}</small>
                                </div>                                
                              </div>
                              <div class="description float" style="width:420px"> 
                                <h2 class="title">
                                <g:if test="${record?.homeclient_id == user?.client_id}">                  
                                  <g:link style="color:${(record?.is_answer==0 && record?.is_read==0)?'#FF530D':'#3F5765'};line-height:16px" controller="inbox" action="view" id="${record?.id}" base="${context.sequreServerURL}">
                                    <g:if test="${record?.mtext}">${record.mtext}</g:if>
                                    <g:else>${message(code:'personal.mbox.with')} ${record?.user_nickname}</g:else>
                                  </g:link>
                                </g:if><g:else>
                                  <g:link style="color:${(record?.is_answer==1 && record?.is_read==0)?'#FF530D':'#3F5765'};line-height:16px" controller="inbox" action="view" id="${record?.id}" base="${context.sequreServerURL}">
                                    <g:if test="${record?.mtext}">${record.mtext}</g:if>
                                    <g:else>${message(code:'personal.mbox.with')} с ${record?.user_nickname}</g:else>                  
                                  </g:link>
                                </g:else>
                                </h2>
                                <ul class="details-list float">
                                  <li class="details-list-item location">
                                    <span class="icons"></span>
                                    <g:if test="${record?.homeclient_id == user?.client_id}">${record?.home_address}</g:if>
                                    <g:else>${record?.shortaddress}</g:else>                        
                                  </li>
                                  <li class="details-list-item dates">
                                    <span class="icons"></span>
                                    ${String.format('%td.%<tm.%<tY',record?.date_start)} - ${String.format('%td.%<tm.%<tY',record?.date_end)}
                                  </li>
                                  <li class="details-list-item person_capacity">
                                    <span class="icons"></span>
                                  <g:each in="${homeperson}" var="item">            
                                    <g:if test="${item?.id==record?.homeperson_id}">
                                    ${item?.name}
                                    </g:if>
                                  </g:each>                          
                                  </li>
                                </ul>
                              </div>      
                              <div class="contprn col" align="right" style="width:180px">
                                <div class="details">                               
                                <g:if test="${record?.answertype_id>0}">
                                  <g:each in="${answertype}" var="item">   
                                    <g:if test="${record?.answertype_id==item?.id}">
                                  <font style="color:${item?.color};font-size:11px">${item?.name_mbox}</font>
                                    </g:if>
                                  </g:each>
                                </g:if><g:else>
                                  <font style="color:gray;font-size:11px">${message(code:'personal.request')}</font>
                                </g:else><br/>                                
                                  <span class="ss_price b-rub" style="padding-left:40px !important">
                                    ${Math.round(record?.price_rub / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>
                                  </span><br/>
                                  <font style="color:#000;font-size:12px">${message(code:'personal.cost')}</span>               
                                </div>                                
                              </div>
                            </td>
                          </tr>
                          <g:if test="${(i+1)!=msg_unread?.records.size()}"> 
                          <tr>
                            <td class="dashed">&nbsp;</td>
                          </tr>
                          </g:if>
                        </g:each>
                      </g:if><g:else>
                          <tr>
                            <td>
                              <b><font color="#bdd4de">${message(code:'personal.mbox.empty')}</font></b>
                            </td>
                          </tr>
                      </g:else>                        
                        </table>                      
                      </div>          
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>                    
                  </div>
                </div>
              </td>
            </tr>      
  </body>
</html>
