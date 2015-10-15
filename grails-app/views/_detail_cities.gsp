                <div class="content" style="margin-top:-20px">
                  <ul id="tabs2" class="subnav">                         
                    <li class="selected" onclick="viewCell2(this,0,'#description')">${message(code:'common.home.desc')}</li>
                    <li onclick="viewCell2(this,0,'#amenities')">${message(code:'list.filtr.amenities')}</li>
                    <li onclick="viewCell2(this,0,'#homerule')">${message(code:'detail.terms_and_conditions')}</li>
                    <li id="inf" onclick="showInf();viewCell2(this,3,'infrastructure');$('map').click();">${message(code:'detail.infrastructure')}</li>
                    <li id="discount" onclick="viewCell2(this,0,'#discounts')" <g:if test="${!home.isHaveDiscountAdv(home)}">style="display:none"</g:if>>${message(code:'common.discounts')}</li>
                  </ul>                  
                  <div id="layers2" class="dashboard-content">
                    <div rel="layer">
                      <div id="description" class="clearfix">
                        <h2 class="toggle border"><span class="edit_icon detail"></span>${message(code:'common.home.desc').capitalize()}</h2>
                        <div class="description_text"><!--noindex-->
                          <g:if test="${context?.lang}"><div class="translate"><input type="button" class="button-glossy green" id="translate_button" value="Translate to English" /></div></g:if><!--/noindex-->
                          <div class="description_text_wrapper" id="description_text" itemprop="description">
                            <g:descrToHtml>${home?.description}</g:descrToHtml>
                          </div>                        
                        </div>
                        <ul class="description_details">
                          <li class="clearfix">
                            <span class="property">${message(code:'common.home_type')}</span>
                            <span class="value">${breadcrumbs.hometype['name'+context?.lang]}</span>
                          </li>
                          <li class="clearfix alt">
                            <span class="property">${message(code:'common.homeperson')}</span>
                            <span class="value">${Homeperson.get(home.homeperson_id)['name'+context?.lang]}</span>
                          </li>                     
                          <li class="clearfix">
                            <span class="property">${message(code:'common.rooms')}</span>
                            <span class="value">${Homeroom.get(home.homeroom_id?:1)?.kol}</span>
                          </li>                      
                          <li class="clearfix alt">
                            <span class="property">${message(code:'common.beds')}</span>
                            <span class="value">${home?.bed?:'-'}</span>
                          </li>
                          <li class="clearfix">
                            <span class="property">${message(code:'common.bathrooms')}</span>
                            <span class="value">${Homebath.get(home.homebath_id?:1)?.kol}</span>
                          </li>
                          <li class="clearfix alt">
                            <span class="property">${message(code:'ads.services.additional.kitchen')}</span>
                            <span class="value">${(home?.is_kitchen==1)?message(code:'detail.yes'):message(code:'detail.no')}</span>
                          </li>
                          <li class="clearfix">
                            <span class="property">${message(code:'ads.country')}</span>
                            <span class="value">${Country.get(home?.country_id)['name'+context?.lang]}</span>
                          </li>
                        <g:if test="${home?.city_id}">
                          <li class="clearfix alt">
                            <span class="property">${message(code:'ads.city').toLowerCase()}</span>
                            <span class="value">${home?.city.capitalize()}</span>
                          </li>
                        </g:if><g:if test="${home?.district}">
                          <li class="clearfix ${home?.city_id?'':'alt'}">
                            <span class="property">${message(code:'ads.district')}</span>
                            <span class="value">${(home?.district?:'').replace('р-н','').replace('r-n','').capitalize()}</span>
                          </li>
                        </g:if><g:if test="${home?.area}">
                          <li class="clearfix ${home?.city_id?(home?.district?'alt':''):(home?.district?'':'alt')}">
                            <span class="property">${message(code:'common.space')}</span>
                            <span class="value">${home?.area?:''} м&sup2;</span>
                          </li>
                        </g:if>
                        </ul>                    
                      </div>                      
                      <div id="amenities">
                        <h2 class="toggle border"><span class="edit_icon services"></span>${message(code:'list.filtr.amenities').capitalize()}</h2>
                        <fieldset class="nb paddtop clearfix">
                          <ul class="service">
                          <g:each in="${homeoption}" var="item" status="i"><g:if test="${i < homeoption.size()/2}">
                            <li><g:if test="${item.fieldname}">
                              <g:if test="${home[item.fieldname]}"><img class="service-icon" src="${resource(dir:'images',file:'enable.png')}" width="17" height="17" title="${message(code:'detail.convenience_/_allowed')}" alt="${message(code:'detail.convenience_/_allowed')}" /></g:if>
                              <g:else><img class="service-icon" src="${resource(dir:'images',file:'disable.png')}" width="17" height="17" title="${message(code:'detail.no_amenity_/_not_allowed')}" alt="${message(code:'detail.no_amenity_/_not_allowed')}" /></g:else>
                            </g:if>
                              <p>${item['name'+context?.lang]} <g:if test="${item['comments'+context?.lang]}">
                                <a class="tooltip" rel="nofollow" title="${item['comments'+context?.lang]}"><img src="${resource(dir:'images',file:'question.png')}" alt="${item['comments'+context?.lang]}" hspace="5" border="0" /></a>
                              </g:if></p>
                            </li>
                          </g:if></g:each>
                          </ul>
                          <ul class="service">
                          <g:each in="${homeoption}" var="item" status="i"><g:if test="${i >= homeoption.size()/2}">
                            <li><g:if test="${item.fieldname}">
                              <g:if test="${home[item.fieldname]}"><img class="service-icon" src="${resource(dir:'images',file:'enable.png')}" width="17" height="17" title="${message(code:'detail.convenience_/_allowed')}" alt="${message(code:'detail.convenience_/_allowed')}" /></g:if>
                              <g:else><img class="service-icon" src="${resource(dir:'images',file:'disable.png')}" width="17" height="17" title="${message(code:'detail.no_amenity_/_not_allowed')}" alt="${message(code:'detail.no_amenity_/_not_allowed')}" /></g:else>
                            </g:if>
                              <p>${item['name'+context?.lang]} <g:if test="${item['comments'+context?.lang]}">                          
                                <a class="tooltip" rel="nofollow" title="${item['comments'+context?.lang]}"><img src="${resource(dir:'images',file:'question.png')}" alt="${item['comments'+context?.lang]}" hspace="5" border="0" /></a>
                              </g:if></p>
                            </li>
                          </g:if></g:each>
                          </ul>
                        </fieldset>
                      </div>
                      <div id="homerule" class="clearfix">
                        <h2 class="toggle border"><span class="edit_icon period"></span>${message(code:'detail.terms_and_conditions').capitalize()}</h2>
                        <div class="description_text">
                          <div class="description_text_wrapper">
                          <g:if test="${home?.homerule}">
                            <p>${home?.homerule?:''}</p>
                          </g:if>
                            <p>${message(code:'detail.owner_scheme')} - <b><font color="#333">${reserve['name'+context?.lang]}</font></b><br/>
                              <g:rawHtml>${reserve['description'+context?.lang]}</g:rawHtml>
                              <g:if test="${reserve?.id==3}"><br/>${message(code:'payout.settlement')} - <b><font color="#333">${ownerClient['settlprocedure'+context?.lang]?:message(code:'payout.settlement.default')}</font></b></g:if>
                            </p>
                            <ul class="pay-list mini">                        
                            <g:each in="${payway}" var="it" status="i">
                              <li class="float"><span class="icons ${it.icon}" title="${it['name'+context?.lang]}"></span></li>
                            </g:each>
                            </ul>
                          </div>
                        </div>
                        <ul class="description_details">
                          <li class="clearfix">
                            <span class="property">${message(code:'ads.price.deposit')}</span>
                            <span class="value">
                              <g:if test="${deposit}">${deposit} <g:rawHtml>${valutaSym}</g:rawHtml></g:if>
                              <g:else>${message(code:'detail.not_needed')}</g:else>
                            </span>
                          </li>
                          <li class="clearfix alt">
                            <span class="property">${message(code:'ads.price.fee.cleanup')}</span>
                            <span class="value">
                              <g:if test="${cleanup}">${cleanup} <g:rawHtml>${valutaSym}</g:rawHtml></g:if>
                              <g:else>${message(code:'detail.no')}</g:else>
                            </span>
                          </li>
                          <li class="clearfix">
                            <span class="property">${message(code:'ads.price.fee')}</span>
                            <span class="value">
                            <g:if test="${fee}">${fee} <g:rawHtml>${valutaSym}</g:rawHtml> ${message(code:'detail.night_after')}
                              <g:each in="${homeperson}" var="item"><g:if test="${item?.id==home?.fee_homeperson}">
                                ${item['name'+(context?.lang?:'2')]}
                              </g:if></g:each>
                            </g:if><g:else>${message(code:'detail.no')}</g:else>
                            </span>
                          </li>
                          <li class="clearfix alt">
                            <span class="property">${message(code:'detail.minday_rent')}</span>
                            <span class="value"><g:each in="${minday}" var="item"><g:if test="${item?.id==home?.rule_minday_id}">
                              ${item['name'+context?.lang]}
                            </g:if></g:each></span>
                          </li>
                          <li class="clearfix">
                            <span class="property">${message(code:'detail.maxday_rent')}</span>
                            <span class="value"><g:each in="${maxday}" var="item"><g:if test="${item?.id==home?.rule_maxday_id}">
                              ${item['name'+context?.lang]}
                            </g:if></g:each></span>
                          </li>
                          <li class="clearfix alt">
                            <span class="property">${message(code:'ads.price.rule_timein')}</span>
                            <span class="value"><g:each in="${timein}" var="item"><g:if test="${item?.id==home?.rule_timein_id}">
                              ${item['name'+context?.lang]}
                            </g:if></g:each></span>
                          </li>
                          <li class="clearfix">
                            <span class="property">${message(code:'ads.price.rule_timeout')}</span>
                            <span class="value"><g:each in="${timeout}" var="item"><g:if test="${item?.id==home?.rule_timeout_id}">
                              ${item['name'+context?.lang]}
                            </g:if></g:each></span>
                          </li>
                          <li class="clearfix alt">
                            <span class="property">${message(code:'ads.price.cancellation')}</span>
                            <span class="value"><g:each in="${cancellation}" var="item"><g:if test="${item?.id==home?.rule_cancellation_id}">                        
                              <g:link base="${context.mainserverURL}" controller="home" action="cancellation" fragment="${item?.shortlink}">${item['name'+context?.lang]}</g:link>
                            </g:if></g:each></span>
                          </li>
                        </ul>
                      </div>
                      <div id="discounts" <g:if test="${!home.isHaveDiscountAdv(home)}">style="display:none"</g:if>>
                        <h2 class="toggle border"><span class="edit_icon price"></span>${message(code:'common.discounts').capitalize()}</h2>
                        <div class="description_text wrap">
                          <div class="description_text_wrapper">
                          <g:if test="${discounts?.long?.modstatus}">                          
                            <h3>${message(code:'common.discounts_for_long_offers')}</h3>
                            <p>${message(code:'ads.price.discount').capitalize()} ${Discountpercent.findByPercent(discounts.long?.discount)?.name} ${message(code:'detail.offered_for_orders_of_more_than')} ${Timetodecide.findByDays(discounts.long?.discexpiredays)?Timetodecide.findByDays(discounts.long?.discexpiredays)['name2'+context?.lang]:''} ${message(code:'detail.if_the_rent_by_more_than')} ${Timetodecide.findByDays(discounts.long?.minrentdays)?(Timetodecide.findByDays(discounts.long?.minrentdays)['name2'+context?.lang]?:message('ads.price.discount.anytime')):''}. ${message(code:'ads.remarks').capitalize()}: ${discounts.long?.terms?:message(code:'detail.no')}</p>
                          </g:if><g:if test="${discounts?.hot?.modstatus}">
                            <h3>${message(code:'common.discounts_for_hot_offers')}</h3>
                            <p>${message(code:'detail.discount')} ${Discountpercent.findByPercent(discounts.hot?.discount)?.name} ${message(code:'detail.offered_for_orders_of_less_than')} ${Timetodecide.findByDays(discounts.hot?.discexpiredays)?Timetodecide.findByDays(discounts.hot?.discexpiredays)['name2'+context?.lang]:''} ${message(code:'detail.if_the_rent_by_more_than')} ${Timetodecide.findByDays(discounts.hot?.minrentdays)?(Timetodecide.findByDays(discounts.hot?.minrentdays)['name2'+context?.lang]?:message('ads.price.discount.anytime')):''}. ${message(code:'ads.remarks').capitalize()}: ${discounts.hot.terms?:message(code:'detail.no')}</p>
                          </g:if>                            
                          </div>
                        </div>
                      </div>
                    </div><!--noindex--> 
                    <div rel="layer" style="display:none"></div>
                    <div rel="layer" style="display:none"></div>                    
                    <div rel="layer" style="display:none"><!--/noindex-->
                      <div id="infras" class="clearfix">                    
                        <div class="description_text ${infras?'':'wrap'}">
                          <div class="description_text_wrapper">
                            <h3>${message(code:'detail.my_guide')}</h3>
                            <div id="list" style="margin:10px -20px 0">
                            </div>                      
                          </div>
                        </div>
                      <g:if test="${infras}">
                        <ul class="description_details">
                        <g:while test="${i < infras.size()}">
                          <li class="clearfix ${(i%2)?'alt':''}">
                            <span class="property">${infras[i][0]}</span>
                            <span class="value">${infras[i][1]}</span>
                          </li><%i++%>
                        </g:while>
                        </ul>                    
                      </g:if>
                      </div><!--noindex-->
                    </div>
                    <div rel="layer" style="display:none"></div>
                  </div>  
                </div>
                <div id="mail_lbox" class="new-modal" style="width:840px;display:none">
                  <h2 class="clearfix">
                    <g:if test="${ownerUsers[0]?.id!=user?.id && ownerClient?.is_reserve}">${infotext['promotext1'+context?.lang]?:message(code:'detail.send_booking_request')}</g:if><g:else>${infotext['promotext2'+context?.lang]?:message(code:'detail.send_message')}</g:else> ${message(code:'detail.to_owner')}
                    <g:each in="${ownerUsers}" var="ownerUser">${ownerUser?.nickname}</g:each> <g:link class="tooltip" controller="index" action="oferta" fragment="use_6" target="_blank" title="${message(code:'detail.regulation_booking')}" base="${context?.absolute_lang}"><img alt="${message(code:'detail.regulation_booking')}" src="${resource(dir:'images',file:'question.png')}" border="0" style="margin-bottom:-2px" /></g:link>
                  </h2>
                  <g:formRemote url="${[controller:'home',action:'addmbox',id:home.id]}" name="sendmailForm" onSuccess="messageResponse(e)" onLoading="\$('sendmailprocessloader').show();" onLoaded="\$('sendmailprocessloader').hide();">       
                  <div id="lightbox_filters" class="segment nopadding" style="height:auto">
                    <div class="lightbox_filter_container" style="height:auto">
                    <g:if test="${msg_count}">
                      <div id="notice" class="notice" style="margin: 0 0 10px">
                        <b>${message(code:'detail.already_contacted')}</b>.
                        <a rel="nofollow" href='<g:createLink controller="inbox" action="index" params="${[client:ownerUsers[0]?.id]}" base="${context?.mainserverURL_lang}"/>'>
                        ${message(code:'booking.view.mbox').capitalize()}</a>
                      </div>
                    </g:if><g:elseif test="${user?.modstatus!=1 && !user?.email && user?.is_external}">
                      <div id="notice" class="notice" style="margin: 0 0 10px">
                        <b><a rel="nofollow" href='<g:createLink controller="profile" action="edit" base="${context?.mainserverURL_lang}"/>'>${message(code:'verify.specify').capitalize()}</a> ${message(code:'detail.email_in_your_profile')}</b>
                      </div>
                    </g:elseif><g:elseif test="${user?.modstatus!=1 && user?.email && user?.is_external && !user.ref_id}">
                      <div id="notice" class="notice" style="margin: 0 0 10px">
                        <b>${message(code:'detail.copy_of_letter')} ${user?.email}. ${message(code:'detail.you_can')}
                        <a rel="nofollow" href='<g:createLink controller="profile" action="edit" base="${context?.mainserverURL_lang}"/>'>${message(code:'detail.change')}</a>${message(code:'detail.it')}.</b>
                      </div>
                    </g:elseif><g:elseif test="${user?.modstatus!=1 && user?.email && !user?.is_external && !user.ref_id}">
                      <div id="notice" class="notice" style="margin: 0 0 10px">
                        <b>${message(code:'detail.copy_of_letter')} ${user?.email}.</b>
                      </div>
                    </g:elseif><!--/noindex-->
                    <!--<g:if test="${ownerClient?.is_reserve}">
                      <div class="notice" style="margin: 0 0 15px">
                        ${message(code:'detail.owner_scheme')} - <b>${reserve['name'+context?.lang]}</b>.<br/>
                        <g:rawHtml>${reserve['description'+context?.lang]}</g:rawHtml>
                        <g:if test="${reserve?.id==3}"><br/>${message(code:'payout.settlement')} - <b>${ownerClient['settlprocedure'+context?.lang]?:message(code:'payout.settlement.default')}</b></g:if>
                      </div>
                    </g:if>--><!--noindex-->
                      <table width="100%" cellpadding="3" cellspacing="0" border="0">
                        <tr valign="middle">
                          <td width="175">
                            <label for="mail_date_begin">${message(code:'detail.check_in')}&nbsp;</label>														
                            <calendar:datePicker id="mail_date_begin" name="mail_date_begin" value="${inrequest?.date_start}" dateFormat="%d-%m-%Y" onChange="calculateMailPrice();"/>							
                          </td>
                          <td width="175">
                            <label for="mail_date_end">${message(code:'detail.check_out')}&nbsp;</label>
                            <calendar:datePicker id="mail_date_end" name="mail_date_end" value="${inrequest?.date_end}" dateFormat="%d-%m-%Y" onChange="calculateMailPrice();"/>
                          </td>
                          <td width="100">
                            <label for="mail_homeperson_id">${message(code:'common.guests')}&nbsp;</label>
                            <select id="mail_homeperson_id" name="homeperson_id" onchange="calculateMailPrice();" style="width:50px">
                            <g:each in="${homeperson}" var="item" status="i">            
                              <option id="homeperson_id_option_${i}" <g:if test="${item?.id==inrequest?.homeperson_id}">selected="selected"</g:if> value="${item?.id}">
                                ${item?.kol}
                              </option>
                            </g:each>
                            </select>
                          </td>
                          <td width="auto" class="contprn">
                            <div id="calculateMailPriceError" class="notice" style="margin:0px;display:none">
                              <font color="red" id="calculateMailPriceErrorText"></font>
                            </div>
                            <div class="details col" id="calculateMailPriceOutput" style="display:none">
                              <span class="float">
                                ${message(code:'detail.cost_of_rent')}:<br/>
                                <small>${message(code:'detail.includes_all_fees')} <a class="tooltip" rel="nofollow" title="${message(code:'detail.total_amount')}">
                                  <img src="${resource(dir:'images',file:'question.png')}" alt="${message(code:'detail.what_is_it')}" border="0" style="margin-bottom:-3px" /></a>
                                </small>
                              </span>
                              <span class="ss_price b-rub col" id="calculateMailPriceOutputText" style="padding: 0px 40px !important"></span>                                      
                            </div>                          
                          </td>
                        </tr>                        
                      </table>
                      <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                      <g:textArea style="width:782px;height:60px" id="mtext" name="mtext" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" value="${inrequest?.mtext?:message(code:'detail.booking_request')}"></g:textArea>
                      <p>${message(code:'detail.contact_with_several_owners1')} <g:link controller="index" action="waiting" base="${context?.absolute_lang}">${message(code:'detail.orders_service')}</g:link>.</p>                        
                      <div id="mbox_error" class="notice" style="margin:10px 0;display:none"></div>
                      <g:if test="${!user}">
                      <table width="100%" cellpadding="3" cellspacing="0" border="0">
                        <tr>
                          <td colspan="3" style="padding-bottom:20px">
                            <b class="float">${message(code:'detail.information_for_feedback')}</b>
                            <b class="col">
                              <a href="javascript:void(0)" rel="nofollow" onclick="showLoginForm(3,'login_lbox',this);$('mail_lbox').hide()">${message(code:'login.enter')}</a>
                            </b>
                          </td>
                        </tr>
                        <tr>
                          <td width="110"><label for="reg_nickname">${message(code:'register.name')}</label></td>
                          <td width="190"><input type="text" id="mbox_nickname" name="nickname" maxlength="${stringlimit}" value="" /></td>
                          <td width="auto" rowspan="3"><g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml></td>
                        </tr>
                        <tr>
                          <td><label for="reg_email">email</label></td>
                          <td><input type="text" id="mbox_email" name="email" maxlength="${stringlimit}" value="" /></td>
                        </tr>
                        <tr>
                          <td><label for="reg_tel">${message(code:'detail.mobile')}</label></td>
                          <td><input type="text" id="mbox_tel" name="tel" value="" /></td>
                        </tr>
                      </table>
                      </g:if>
                    </div>
                  </div>
                  <div class="segment buttons" style="border-radius: 0 0 ${!user?'0 0':'10px 10px'}">
                    <input id="sendmail_button" type="submit" class="button-glossy orange" value="${message(code:'button.send')}" style="display:none" />
                    <input id="proxy_sendmail_button" type="button" class="button-glossy orange" value="${message(code:'button.send')}" onclick="this.disabled=true;yaCounter15816907.reachGoal('first_contact');testMessage()" />
                    <span id="sendmailprocessloader" style="display:none"><img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'detail.please_wait_data_processing')}" border="0" /></span>
                  </div>
                  <g:if test="${!user}">
                  <div class="segment buttons">
                    <table width="100%" cellpadding="5" cellspacing="0" border="0">
                      <tr>
                        <td colspan="2" style="padding-bottom:20px"><b>${message(code:'detail.use_soc_network_account')}</b></td>
                      </tr>
                      <tr>
                        <td valign="top" align="right">
                          <div id="fb_auth" class="fb-button">
                            <a href="javascript:void(0)" rel="nofollow" onclick="doFBlogin();">
                              <span class="fb-button-left"></span>
                              <span class="fb-button-center"><b>Facebook</b></span>
                              <span class="fb-button-right"></span>
                            </a>
                          </div>
                        </td>
                        <td width="150" align="left" valign="top">
                          <div id="main_vk_auth" class="vk-button">
                            <a href="javascript:void(0)" rel="nofollow" onclick="doLogin()">
                              <span class="vk-button-left"></span>
                              <span class="vk-button-center"></span>
                              <span class="vk-button-right"></span>
                            </a>
                          </div>
                        </td>
                      </tr>
                    </table>
                  </div>
                  </g:if>
                  </g:formRemote>
                </div>  
                <div id="answerComment_lbox" class="new-modal" style="height:250px;display:none">
                  <h2 class="clearfix">${message(code:'reviews.response')}</h2>
                  <g:formRemote name="answerCommentForm" url="${[controller:'home',action:'addAnswerComment']}" style="padding:5px" onSuccess="answerCommentResponse(e);">
                  <div id="answerComment_lbox_segment" class="segment nopadding">
                    <div id="answerComment_lbox_container" class="lightbox_filter_container" style="height:97px">
                      <div id="message_data"></div>         
                      <div id="answerComment_error" class="notice" style="width:97%;margin:0 0 10px;display:none">
                        <span id="answerComment_errorText"></span>
                      </div>          
                      <label for="answ_comtext">${message(code:'detail.message').capitalize()}:</label>
                      <textarea rows="4" cols="60" id="answ_comtext" onkeydown="textLimit(this.id);" onkeyup="textLimit(this.id);" name="answ_comtext" style="width:97%;margin-top:3px;height:70px"></textarea>
                    </div>
                  </div>
                  <div class="segment buttons">                  
                    <input type="submit" class="button-glossy orange" value="${message(code:'button.send')}" />
                    <input id="com_id" type="hidden" name="com_id" value="" />                                          
                  </div>
                  </g:formRemote>
                </div>
                <g:formRemote id="calculatePriceForm" name="calculateMailPriceForm" method="post" url="[ controller: 'home', action: 'calculatePrice' ]" onSuccess="processMailResponse(e)">
                  <input type="hidden" name="home_id" value="${home.id}" />
                  <input type="hidden" id="date_start_year_mail" name="date_start_year" value="" />
                  <input type="hidden" id="date_start_month_mail" name="date_start_month" value="" />
                  <input type="hidden" id="date_start_day_mail" name="date_start_day" value="" />
                  <input type="hidden" id="date_end_year_mail" name="date_end_year" value="" />
                  <input type="hidden" id="date_end_month_mail" name="date_end_month" value="" />
                  <input type="hidden" id="date_end_day_mail" name="date_end_day" value="" />
                  <input type="hidden" id="homeperson_id_mail" name="homeperson_id" value="" />
                  <input type="submit" class="button-glossy orange" id="calculateMailPriceFormSubmit" style="display: none" value="${message(code:'detail.calculate')}" />
                </g:formRemote>   
                <g:formRemote id="infraslistForm" name="infraslistForm" method="post" url="[ controller: 'home', action: 'infraslist' ]" update="[success:'list']">
                  <input type="hidden" name="home_id" value="${home.id}" />
                  <input type="submit" class="button-glossy orange" id="infraslistFormSubmit" style="display: none" value="${message(code:'button.view')}" />
                </g:formRemote><!--/noindex-->
              </td>
            </tr>          
          <g:if test="${directionCities}">
            <tr>
              <td colspan="4" class="page-topic paddtop">
                <h2 class="ins">${message(code:'common.rent')} ${breadcrumbs.hometype['name'+(context?.lang?'3'+context?.lang:'4')]} ${message(code:'list.filtr.footer.per_night_in_another_cities')}</h2>
                <p><g:each in="${directionCities}" var="city">
                  <g:if test="${city.value.is_index}"><a href="<g:createLink mapping='${city.value.domain?(breadcrumbs.hometype.urlname=='flats'?'mainpage'+context?.lang:'hSearchTypeDomain'+context?.lang):('hSearchType'+context?.lang)}' params='${city.value.domain?(breadcrumbs.hometype.urlname=='flats'?[:]:[type_url:breadcrumbs.hometype.urlname]):[where:city.key,country:breadcrumbs.country.urlname,type_url:breadcrumbs.hometype.urlname]}' base='${city.value.domain?'http://'+city.value.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="tag" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:if>
                  <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_${breadcrumbs.hometype.urlname}','${city.key}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" style="font-size:${city.value.count>tagcloudParams.maxFontCount?'20px':city.value.count>tagcloudParams.middleFontCount?'15px':'10px'};color:#3F5765"></g:else>
                  ${city.key}<g:if test="${city.value.is_index}"></a></g:if><g:else></span></g:else>  
                </g:each></p>
              </td>
            </tr>
          </g:if><g:if test="${alikeHomes}">
            <tr>
              <td colspan="4" style="padding:20px 0 0">
                <h2 class="padd20">${message(code:'detail.similar_ads').capitalize()}</h2>
                <ul class="homepage_badges">
                <g:each in="${alikeHomes}" var="home" status="i">
                  <li style="margin-left:${(i==0)?'21':'10'}px">
                    <div class="badge-content-container shadow dark" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
                      <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.get(home.city_id)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}' params='${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                      <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                        <img width="220" height="160" src="${(home?.mainpicture)?alikephotourl+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                        <div class="slideshow_item_details">
                          <div class="slideshow_item_details_text">
                            <div class="ss_details_top">
                              <span class="ss_name" itemprop="name"><g:shortString text="${home?.name}" length="18"/></span><br/>
                              <span class="ss_location" itemprop="description">${(alikeDistances[i]!=0)?alikeDistances[i]+' '+message(code:'common.km_from_choosen'):message(code:'common.same_place')}</span>
                            </div>
                          </div>
                        </div><g:if test="${alikePrices[i]!=0}">
                        <div class="photo_item_details">
                          <span class="ss_price b-rub" itemprop="price">${alikePrices[i]}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                        </div></g:if>
                      <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                    </div>  
                  </li>
                </g:each><g:if test="${alikeHomes.size()<4}"><g:each var="i" in="${(1..4-alikeHomes.size())}">
                  <li>
                    <div class="badge-content-container shadow dark">
                      <img width="220" height="160" src="${resource(dir:'images',file:'default-picture.png')}" alt="" border="0" />                      
                    </div>
                  </li>                  
                </g:each></g:if>
                </ul>
              </td>
            </tr>
          </g:if>
