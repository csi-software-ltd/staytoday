<div id="ajax_wrap">
  <div id="results_header" class="results_header">    
    <span class="pagination col">
      <g:paginate controller="inbox"  prev="&lt;" next="&gt;"
        action="${actionName}" max="20" maxsteps="1" params="${inrequest}"
        total="${data?.count}" /> 
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="{data?.records}">
  <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
  <g:each in="${data?.records}" var="record" status="i">
    <tr>
      <td onmouseover="this.addClassName('selected')" onmouseout="this.removeClassName('selected')">
        <div class="relative float" style="margin-bottom:10px">                    
        <g:if test="${((record?.homeclient_id == user?.client_id)&&((record?.is_answer==0&&record?.is_read==0)||record.is_readowner==0))||((record?.homeclient_id != user?.client_id)&&((record?.is_answer==1&&record?.is_read==0)||record.is_readclient==0))}">
          <div class="new">!</div>
        </g:if>
          <div class="thumbnail userpic shadow">
          <g:if test="${record?.homeclient_id == user?.client_id}">        
            <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+record?.client_id]}" target="_blank">
              <img width="68" height="68" alt="${record?.client_nickname}" title="${record?.client_nickname}" border="0" 
                src="${(record?.client_picture && !record?.client_external)?imageurl:''}${(record?.client_picture)?record?.client_picture:resource(dir:'images',file:'user-default-picture.png')}">
            </g:link>
          </g:if><g:else>
            <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(record.home_country_id)?.urlname,city:record.home_city,linkname:record?.linkname]}" target="_blank">
              <img width="68" height="68" alt="${record?.home_name}" title="${record?.home_name}" border="0" 
                src="${(record?.homepicture)?urlphoto+record?.homeclient_id+'/t_'+record?.homepicture:resource(dir:'images',file:'default-picture.png')}">
            </g:link>        
          </g:else>
          </div>
          <div style="width:68px">
            <small style="white-space:normal">
            <g:if test="${(Home.get(record?.home_id)?.client_id?:0) == user?.client_id}">                
              <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+record?.client_id]}" target="_blank">${record?.client_nickname}</g:link>
            </g:if><g:else>
              <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+record?.user_id]}" target="_blank">${record?.user_nickname}</g:link>
            </g:else>
            </small>
            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',record.moddate)}</small>
          </div>
        </div>
        <div class="description float" style="width:430px">                
          <h2 class="title padd20"><g:if test="${record?.homeclient_id == user?.client_id}">
            <g:if test="${((Home.get(record?.home_id)?.modstatus?:0) != -2) && ((Home.get(record?.home_id)?.modstatus?:0) != -3)}">          
            <a class="name" href="<g:createLink controller='inbox' action='view' id='${record?.id}' base='${context?.mainserverURL_lang}'/>" <g:if test="${(record?.is_answer==0 && record?.is_read==0)||record.is_readowner==0}">style="color:#FF530D"</g:if>>
              <g:if test="${record?.mtextowner}"><g:shortString text="${record.mtextowner}" length="26"/></g:if>
              <g:else>${message(code:'personal.mbox.with')} ${record?.client_nickname}</g:else>
            </a>
            </g:if><g:else>
              <g:if test="${record?.mtextowner}"><g:shortString text="${record.mtextowner}" length="26"/></g:if>
              <g:else>${message(code:'personal.mbox.with')} ${record?.client_nickname}</g:else>
            </g:else>
          </g:if><g:elseif test="${((Home.get(record?.home_id)?.modstatus?:0) != -2) && ((Home.get(record?.home_id)?.modstatus?:0) != -3)}"> 
            <a class="name" href="<g:createLink controller='inbox' action='view' id='${record?.id}' base='${context?.mainserverURL_lang}'/>" <g:if test="${(record?.is_answer==1 && record?.is_read==0)||record.is_readclient==0}">style="color:#FF530D"</g:if>>
              <g:if test="${record?.mtext}"><g:shortString text="${record.mtext}" length="26"/></g:if>
              <g:else>${message(code:'personal.mbox.with')} ${record?.user_nickname}</g:else>                  
            </a>
          </g:elseif><g:else>
            <g:if test="${record?.mtext}"><g:shortString text="${record.mtext}" length="26"/></g:if>
            <g:else>${message(code:'personal.mbox.with')} ${record?.user_nickname}</g:else>
          </g:else></h2>
          <ul class="details-list float">
            <li class="details-list-item location">
              <span class="icons"></span>
              ${(record?.homeclient_id == user?.client_id) ? record?.home_address : record?.shortaddress}                
            </li>
            <li class="details-list-item dates">
              <span class="icons"></span>
              ${String.format('%td.%<tm.%<tY',record?.date_start)} - ${String.format('%td.%<tm.%<tY',record?.date_end)}
            </li>
            <li class="details-list-item person_capacity">
              <span class="icons"></span>
            <g:each in="${homeperson}" var="item"><g:if test="${item?.id==record?.homeperson_id}">
              ${item['name'+context?.lang]}
            </g:if></g:each>                          
            </li>
          </ul>
        </div>              
        <div class="contprn col" align="right" style="width:180px">
          <div class="details">
          <g:if test="${((Home.get(record?.home_id)?.modstatus?:0) == -2) || ((Home.get(record?.home_id)?.modstatus?:0) == -3)}">
            <font color="red">${message(code:'inbox.list.deleted')}</font>
          </g:if>
          <g:elseif test="${record?.answertype_id>0 && record.modstatus>0}">
            <g:each in="${answertype}" var="item"><g:if test="${record?.answertype_id==item?.id}">
            <font color="${item?.color}">${item['name_mbox'+context?.lang]}</font>
            </g:if></g:each>
          </g:elseif><g:elseif test="${record.modstatus>0}">
            <font color="gray">${message(code:'personal.request')}</font>
          </g:elseif>          
          <g:else>
            <font color="red">${Mboxmodstatus.findByModstatus(record.modstatus)['name'+context?.lang]?:message(code:'inbox.list.deleted')}</font>
          </g:else><br/>
            <span class="ss_price b-rub" style="padding:0px !important;height:35px">
              ${Math.round(record?.price_rub / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
            </span><br/>
            <font color="#000">${message(code:'personal.cost')}</span>              
          </div>
        <g:if test="${record?.homeclient_id==user?.client_id && record?.answertype_id==0 && reminder[i]!=0}">
          <div class="reminder">
            ${message(code:'inbox.list.expires')}<br/> 
            <font color="#FF530D" size="+1">${reminder[i]}</font>
          </div>
        </g:if>
        </div>         
        <div style="clear:both;height:40px">
          <div class="dview">
            <div class="actions float">
              <span class="action_button">
                <g:if test="${((Home.get(record?.home_id)?.modstatus?:0) != -2) && ((Home.get(record?.home_id)?.modstatus?:0) != -3)}">
                  <g:link class="icon view" controller="inbox" action="view" id="${record?.id}" base="${context?.mainserverURL_lang}">${message(code:'inbox.list.detail')}</g:link>
                </g:if>                                                  
              </span>  
              <span id="hidden_${record?.id}" class="action_button">
              <g:if test="${!((record.client_id==user.id&&record.is_clfav==-1)||(record.user_id==user.id&&record.is_owfav==-1))}">
                <a class="icon inactive" href="javascript:void(0);" onclick="toggleDelete(${record?.id},1)">${message(code:'button.delete')}</a>
              </g:if><g:else>
                <a class="icon active" href="javascript:void(0);" onclick="toggleDelete(${record?.id},0)">${message(code:'inbox.list.undelete')}</a>
              </g:else>              
              </span>              
            </div>
          <g:if test="${!((record.client_id==user.id&&record.is_clfav==-1)||(record.user_id==user.id&&record.is_owfav==-1))}">
            <div id="options_${record?.id}" class="options float">              
              <span id="starred_${record?.id}" class="star float">
                <a href="javascript:void(0);" 
                <g:if test="${record?.homeclient_id == user?.client_id}"> 
                  <g:if test="${record?.is_owfav==1}">class="starred" onclick="toggleFavorite(${record?.id},1,0);" title="${message(code:'inbox.list.favorite.unmark')}"</g:if>
                  <g:else>onclick="toggleFavorite(${record?.id},1,1);" title="${message(code:'inbox.list.favorite.mark')}"</g:else>  
                </g:if><g:else>
                  <g:if test="${record?.is_clfav==1}">class="starred" onclick="toggleFavorite(${record?.id},0,0);" title="${message(code:'inbox.list.favorite.unmark')}"</g:if>
                  <g:else>onclick="toggleFavorite(${record?.id},0,1);" title="${message(code:'inbox.list.favorite.mark')}"</g:else>
                </g:else>>
                </a>
              </span>
            </div>
          </g:if>
          </div>
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
