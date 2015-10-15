            <tr style="height:110px">
              <td width="250" class="search">
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>                                
              </td>
              <td colspan="3" class="rent" style="padding-bottom:10px">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr>
              <td width="250" valign="top">
              <g:if test="${!(actionName in ['rules','requirements'])}">
                <div class="relative">
                  <span class="count" style="top:0">
                    <b>${message(code:actionName=='waiting'?'order.records.found':(actionName=='bookings'?'booking.records.found':'common.records.found'))}</b><span id="ads_count">${data?.count}</span>
                  </span>
                </div>
              </g:if>
                <div class="header-list-item" style="padding:0px">
                  <div class="header-dropdown personal">
                    <div class="header-dropdown-section">
                      <ul class="header-dropdown-list">
                      <g:each in="${adsmenu}" var="item">   
                        <li class="header-dropdown-list-item">                    
                          <g:link class="${(controllerName==item.controller && actionName==item.action)?'active':''}" controller="${item.controller}" action="${item.action}" base="${context?.mainserverURL_lang}">                            
                            ${item[(item.action=='ads'?'header':'name')+context?.lang]}
                          </g:link>
                        </li>
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
