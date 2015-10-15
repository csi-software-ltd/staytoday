            <tr style="height:110px">
              <td width="250" class="search">
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>                                
              </td>
              <td colspan="3" class="rent s0 padd20">              
                <div class="thumbnail userpic" style="margin-top:17px">
                  <img width="68" height="68" alt="${user?.nickname}" title="${user?.nickname}" 
                    src="${(user?.picture && !user.is_external)?imageurl:''}${(user?.smallpicture)?user?.smallpicture:resource(dir:'images',file:'user-default-picture.png')}"/>
                </div>
                <div class="float" style="width:385px;margin:15px 0 15px 15px">
                  <h1 class="header" style="margin-left:0px">${user?.nickname}</h1>
                  <p style="color:#fff">${message(code:'personal.member')} ${String.format('%td.%<tm.%<tY',user?.inputdate)}</p>
                  <span class="actions col" style="margin:-35px 40px 0">
                    <span class="action_button orange">
                      <g:link class="icon view" target="_blank" mapping="${'pView'+context?.lang}" params="${[uid:'id'+user?.id]}">${message(code:'personal.anketa').capitalize()}</g:link>
                    </span>
                  </span>
                </div>                            
                <ul class="verifications-list col" style="width:235px">
                  <li class="verifications-list-item" style="border-bottom:1px dotted #3F5765">
                    <div class="verifications-icon"></div>
                    <div class="verifications-legend ${user?.provider}"></div>
                    <span class="label" style="color:#fff">${message(code:'personal.connect').capitalize()} ${user?.provider}</span><br/>
                    <span class="counts">${message(code:'personal.confirmed')}</span>
                  </li>
                <g:if test="${user?.tel}">
                  <li class="verifications-list-item ${(user?.is_telcheck==0)?'verifications-none':''}" style="border-bottom:0">
                    <div class="verifications-icon ${(user?.is_telcheck==0)?'none':''}"></div>
                    <div class="verifications-legend phone"></div>
                    <span class="label" style="color:#fff">${message(code:'personal.phone').capitalize()} ${message(code:user?.is_telcheck==1?'personal.confirmed.he':'personal.notconfirmed.he')}</span><br/>
                    <span class="counts" style="color:#8B99A5">${user?.tel}</span>
                  </li>
                </g:if>                        
                </ul>                            
              </td>
            </tr>
            <tr>
              <td valign="top">
                <b><g:link controller="personal" action="index" class="to-parent" base="${context?.absolute_lang}">${message(code:'personal.dashboard')}</g:link></b>
                <div class="header-list-item" style="padding:0px">
                  <div class="header-dropdown personal" style="top:0">
                    <div class="header-dropdown-section">
                      <ul class="header-dropdown-list">
                      <g:each in="${profilemenu}" var="item">                    
                        <g:if test="${!(user.is_external && item?.action=='photo')}">
                        <li class="header-dropdown-list-item">                        
                          <g:link class="${item?.icon}${((controllerName == item?.controller)&&(actionName in [item?.action, ((item?.action=='uedit')?'photo':((item?.action=='homeprice')?'homeprop':''))]))?' active':''}" controller="${item?.controller}" action="${item?.action}" params="${[id:home?.id]}" base="${context?.mainserverURL_lang}">
                            <span class="icon"></span>
                            ${item['name'+context?.lang]}
                          </g:link>                                          
                        </li>         
                        </g:if>
                      </g:each>
                      </ul>
                    </div>
                  </div>
                </div>
              </td>
              <td colspan="3" valign="top">            
                <div class="content body shadow" style="min-height:532px">
                  <ul class="subnav">
                    <li class="${((controllerName=='personal')&&(actionName=='index'))?'selected':''}">
                      <g:link controller="personal" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','index')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${((controllerName=='inbox')&&(actionName=='index'))?'selected':''}">
                      <g:link controller="inbox" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('inbox','index')['name'+context?.lang]}</g:link>                
                    </li>
                    <li class="${((controllerName=='personal')&&(actionName in ['ads','adsoverview','editads','photo','homephoto','video','homevideo','map','homeprice','homeprop','calendar','infras','promote','bookings','waiting','rules','requirements']))?'selected':''}">
                      <g:link controller="personal" action="ads" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','ads')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${((controllerName=='trip')&&(actionName in ['next','current','previous','favorite','cancellation','waiting']))?'selected':''}">
                      <g:link controller="trip" action="next" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('trip','next')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${((controllerName=='profile')&&(actionName in ['edit','verifyUser','photo','reviews']))?'selected':''}">
                      <g:link controller="profile" action="edit" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('profile','edit')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${((controllerName=='account')&&(actionName in ['index','payout','paydocuments','payU','payUresult','liqpay','liqpayresult','wmoney','wmsuccess','wmfail','history','payorderdetail','refferals','settings','trippayment']))?'selected':''}" style="width:55px">
                      <g:link controller="account" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('account','index')['name'+context?.lang]}</g:link>
                    </li>
                  </ul>                
                  <div class="dashboard-content">
                    <div rel="layer">
