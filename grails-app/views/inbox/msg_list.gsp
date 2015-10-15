<div id="ajax_wrap">
  <div class="results_header">    
    <span class="count" style="top:0">
      <b>${message(code:'inbox.records.found')}</b><span id="ads_count">${data?.count}</span>
    </span>
    <span class="pagination col">
      <g:paginate controller="inbox" action="${actionName}" params="[id:msg.id]" 
        prev="&lt;" next="&gt;" max="20" total="${data?.count}" /> 
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="{data?.records}">
  <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
  <g:each in="${data?.records}" var="record" status="i">
    <tr>
      <td valign="top" onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">      
      <g:if test="${record?.is_system==1}">
        <div class="float padd10">
          <div class="thumbnail userpic shadow">
            <img width="68" height="68" src="${resource(dir:'images',file:'logo_large.png')}" alt="StayToday" title="StayToday" border="0" />
          </div>
          <div style="width:68px">
            <small style="white-space:normal">StayToday</small><br/>
          </div>
        </div>
      </g:if>
      <g:elseif test="${((sender?.client_id != user?.client_id) && (record?.is_answer==0)) || 
        ((sender?.client_id == user?.client_id) && (record?.is_answer==1))}">
        <div class="float padd10">
          <div class="thumbnail userpic shadow">
            <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+user?.id]}" title="${user?.nickname}">
              <img width="68" height="68" alt="${user?.nickname}" border="0" 
                src="${(user?.picture && !user?.is_external)?imageurl+'t_':''}${(user?.picture)?user?.picture:resource(dir:'images',file:'user-default-picture.png')}">
            </g:link>                            
          </div>
          <div style="width:68px">
            <small style="white-space:normal">${message(code:'inbox.view.you')}</small><br/>
            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',record?.moddate)}</small>
          </div>
        </div>
      </g:elseif>
      <g:elseif test="${((sender?.client_id == user?.client_id) && (record?.is_answer==0)) || 
                    ((sender?.client_id != user?.client_id) && (record?.is_answer==1))}">                 
        <div class="float padd10">
          <div class="thumbnail userpic shadow">                  
          <g:if test="${sender?.client_id != user?.client_id}">
            <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+sender?.id]}">
              <img width="68" height="68" alt="${sender?.nickname}" title="${sender?.nickname}" border="0" 
                src="${(sender?.picture && !sender?.is_external)?imageurl+'t_':''}${(sender?.picture)?sender?.picture:resource(dir:'images',file:'user-default-picture.png')}">
            </g:link>
          </g:if><g:else>
            <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+recipient?.id]}">
              <img width="68" height="68" alt="${recipient?.nickname}" title="${recipient?.nickname}" border="0" 
                src="${(recipient?.picture && !recipient?.is_external)?imageurl+'t_':''}${(recipient?.picture)?recipient?.picture:resource(dir:'images',file:'user-default-picture.png')}">       
            </g:link>
          </g:else>
          </div>  
          <div style="width:68px">
            <small style="white-space:normal">${(sender?.client_id != user?.client_id) ? sender?.nickname : recipient?.nickname}</small><br/>
            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',record?.moddate)}</small>
          </div>
        </div>  
      </g:elseif>      
        <div class="bubble-container col" style="width:585px">
          <div class="header_container" style="height:35px">
            <font color="#333">
            <g:if test="${record?.is_answer==0 && record?.answertype_id==0}">
              ${message(code:'inbox.view.request')} <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(record.home_country_id)?.urlname,city:record.home_city,linkname:record?.linkname]}">${homeNames[i]}</g:link>
            </g:if><g:else>
              <g:if test="${record?.answertype_id in [1,2]}"> 
              ${recipient?.nickname} ${message(code:'inbox.view.preapproved')} <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(record.home_country_id)?.urlname,city:record.home_city,linkname:record?.linkname]}">${homeNames[i]}</g:link>
              </g:if><g:elseif test="${record?.answertype_id in [3,5,6]}">
              ${Answertype.get(record?.answertype_id)['name_recipient'+context?.lang]}
              </g:elseif><g:elseif test="${record?.answertype_id==4}">                            
                <g:if test="${(record?.rule_minday_id>0) && (days_between < record?.rule_minday_id)}">
              ${message(code:'inbox.view.decline.small')}
                </g:if><g:if test="${(record?.rule_maxday_id>1) && (days_between > record?.rule_maxday_id)}">
              ${message(code:'inbox.view.decline.big')}
                </g:if>
              </g:elseif><g:elseif test="${record?.answertype_id==7}">
              ${recipient?.nickname} ${message(code:'inbox.view.booking.confirmed')} <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(record.home_country_id)?.urlname,city:record.home_city,linkname:record?.linkname]}">${homeNames[i]}</g:link>                         
              </g:elseif><g:elseif test="${record?.answertype_id==8}">
              ${recipient?.nickname} ${message(code:'inbox.view.booking.refused')} <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(record.home_country_id)?.urlname,city:record.home_city,linkname:record?.linkname]}">${homeNames[i]}</g:link>                         
              </g:elseif><g:elseif test="${record?.answertype_id==9}">
              ${recipient?.nickname} ${message(code:'inbox.view.booking.denied')} <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(record.home_country_id)?.urlname,city:record.home_city,linkname:record?.linkname]}">${homeNames[i]}</g:link>                         
              </g:elseif><g:elseif test="${record?.answertype_id==10}">
              ${message(code:'inbox.view.booking.cancel')}
              </g:elseif>
            </g:else>                        
            </font>
            <ul class="details-list clearfix detail" style="width:550px">
            <g:if test="${record?.answertype_id != 4}">
              <li class="details-list-item dates">
                <span class="icons"></span>
                ${String.format('%td.%<tm.%<tY',record?.recdate_start)} - ${String.format('%td.%<tm.%<tY',record?.recdate_end)}
              </li>
              <li class="details-list-item person_capacity">
                <span class="icons"></span>
              <g:each in="${homeperson}" var="item"><g:if test="${item?.id==record?.rechomeperson_id}">
                  ${item['name'+context?.lang]}
              </g:if></g:each>                          
              </li>
            </g:if><g:elseif test="${record?.is_answer==1 && record?.answertype_id==4}">
              <g:if test="${record?.rule_minday_id > 0}">
              <li class="details-list-item none">
                <span class="icons"></span>                            
              <g:each in="${rule_minday}" var="item"><g:if test="${item?.id==record?.rule_minday_id}">
                ${message(code:'inbox.view.mindays')}: ${item?.nday}
              </g:if></g:each>
              </li>
              </g:if><g:if test="${record?.rule_maxday_id > 1}">
              <li class="details-list-item none">
                <span class="icons"></span>                                                      
              <g:each in="${rule_maxday}" var="item"><g:if test="${item?.id==record?.rule_maxday_id}">
                ${message(code:'inbox.view.maxdays')}: ${item?.nday}
              </g:if></g:each>
              </li>
              </g:if>
              <li class="details-list-item none">
                <span class="icons"></span>
                ${message(code:'inbox.view.reqdays')}: ${days_between}
              </li>                          
            </g:elseif><g:if test="${record?.answertype_id in [0,1,2,3,7,8,9]}">
              <li class="details-list-item none">
                <b>${Math.round(record?.price_rub / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></b>
                <g:if test="${record?.answertype_id==2}">
                  &nbsp;&mdash;&nbsp; <font color="red">${Answertype.get(record?.answertype_id)['name_mbox'+context?.lang]}</font>
                </g:if>
              </li>
            </g:if>
            </ul>                      
          </div>
        <g:if test="${record?.rectext}">
          <div class="clearfix">
            <p>${record?.rectext}</p>
          </div>
        </g:if>
        <g:if test="${(((sender?.client_id == user?.client_id)&&(record?.answertype_id in [1,2,8]))||
                      ((sender?.client_id != user?.client_id)&&(record?.answertype_id in [1,2,3,4])))&&
                      (msg?.modstatus < 4 || msg?.modstatus == 5)}">
          <div class="clearfix notice">
          <g:if test="${(sender?.client_id == user?.client_id)}">        
            <g:if test="${(record?.answertype_id==1) && (msg?.modstatus < 4)}"> 
            <a href="javascript:void(0)" onclick="deleteRec(${record?.id},1)">
              ${message(code:'inbox.view.delete.bookoffer')}
            </a>
            </g:if><g:elseif test="${(record?.answertype_id==2) && (msg?.modstatus < 4)}"> 
            <a href="javascript:void(0)" onclick="deleteRec(${record?.id},0)">
              ${message(code:'inbox.view.delete.specoffer')}
            </a>
            </g:elseif><g:elseif test="${(record?.answertype_id==8) && (msg?.modstatus == 5)}">
            ${message(code:'inbox.view.freedays')}<br/>
            <g:formRemote name="cancellation" url="[ controller: 'inbox', action: 'removeBron']" onSuccess="location.reload(true)" style="margin-top:5px">
              <input type="submit" class="button-glossy orange" value="${message(code:'inbox.view.unlock')}"/>
              <input type="hidden" name="id" value="${record?.mbox_id}"/>
            </g:formRemote>
            </g:elseif>
          </g:if><g:else>
            <g:if test="${(record?.answertype_id in [1,2]) && (msg?.modstatus < 4)}">
            <g:formRemote class="contprn" id="bron_form" name="bron" url="[controller:'inbox',action:'bron']" update="bron_lbox" onComplete="jQuery('#bron_link').click();">
              <script type="text/javascript">
                $('bron_button_head').show();
                $('bron_summa').show();
              </script>
              <div class="details float" style="margin:-5px 20px 0 0">
                <span class="ss_price b-rub" style="padding:0!important;height:32px;color:green">${totalPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                <font color="gray">${message(code:'inbox.bron.summa')}</font>
              </div>
              <input id="bron_button" type="submit" class="button-glossy green float" value="${message(code:Client.findById(sender?.client_id)?.is_reserve?'inbox.view.book_and_pay':'button.booking')}" style="width:auto"/>
              <input type="hidden" name="mbox_id" value="${record?.mbox_id}"/>
              <input type="hidden" name="id" value="${record?.id}"/>
            </g:formRemote>
            </g:if><g:elseif test="${record?.answertype_id in [3,4]}">
            <p>${message(code:'inbox.view.decline.info')}<br/>
              <a href="javascript:void(0)" onclick="$('alikesearch_form').submit();">${message(code:'inbox.view.similar')}</a>
              <g:link controller="trip" action="favorite" style="margin-left:15px">${message(code:'common.your_favourite')}</g:link>
            </p>
            <g:form name="alikesearch_form" url="${[controller:'home',action:'search']}" base="${context?.mainserverURL_lang}" method="get" base="${context?.mainserverURL_lang}">                            
              <input type="hidden" id="back_where" name="where" value="${alikeWhere}"/>
              <input type="hidden" id="back_alike" name="alike" value="${record?.home_id}"/>
              <input type="hidden" id="back_view" name="view" value="list"/>
            </g:form>
            </g:elseif>
          </g:else>
          </div>
        </g:if>            
        </div>
      </td>       
    </tr>
    <g:if test="${(i+1)!=data.records.size()}"> 
    <tr>
      <td class="dashed">&nbsp;</td>
    </tr>
    </g:if>    
  </g:each>
  </table>
</g:if>
</div>
