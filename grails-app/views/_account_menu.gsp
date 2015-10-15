            <tr style="height:110px">
              <td width="250" class="search">
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>                                
              </td>
              <td colspan="3" class="rent ss">
                <g:if test="${controllerName=='account' && actionName=='payorderdetail'}"><h1>Заказ № ${payorder?.norder} от <g:formatDate format="dd.MM.yyyy" date="${payorder?.inputdate}"/></h1></g:if>
                <g:else><h1>${infotext['header'+context?.lang]?:''}</h1></g:else>
              </td>
            </tr>
            <tr>
              <td valign="top">
              <g:if test="${controllerName=='account' && actionName=='payorderdetail'}">
                <b><g:link class="to-parent" controller="account" action="history" base="${context?.absolute_lang}">${message(code:'account.tolist')}</g:link></b>
              </g:if><g:else>
                <b><g:link class="to-parent" controller="personal" action="index" base="${context?.absolute_lang}">${message(code:'personal.dashboard')}</g:link></b>
              </g:else>
                <div class="header-list-item" style="padding:0">
                  <div class="header-dropdown personal" style="top:0">
                    <div class="header-dropdown-section">
                      <ul class="header-dropdown-list">
                      <g:each in="${accountmenu}" var="item">                     
                        <li class="header-dropdown-list-item">
                        <g:if test="${item.action=='payout' && !billingsettings}">
                          <a href="javascript:void(0)" style="color:#333">${Infotext.findByControllerAndAction('account','payout')['name'+context?.lang]}</a>
                        </g:if><g:else>
                          <g:link class="${(controllerName==item.controller && actionName in [item.action,item.relatedpages])?'active':''}" controller="${item.controller}" action="${item.action}" base="${context?.mainserverURL_lang}">                            
                            ${item[(item.action=='index'?'header':'name')+context?.lang]}
                          </g:link>
                        </g:else>
                        </li>
                      </g:each>
                      </ul>
                    </div>
                  </div>
                </div>
              </td>
              <td colspan="3" valign="top">
                <div class="content body shadow" style="min-height:${((controllerName=='account')&&(actionName=='history'))?'660':'532'}px">
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
