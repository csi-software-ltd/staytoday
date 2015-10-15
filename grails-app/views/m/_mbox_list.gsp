<g:if test="${data.records}">
  <ul id="ajax_wrap" data-role="listview" data-split-icon="arrow-r" style="margin:0">
    <li data-role="divider" data-theme="a" style="padding:0 0 0 10px;height:40px">
      <span class="count float">
        <span>${message(code:'mobile.found')}</span><span id="ads_count">${data.count}</span>
      </span>
      <span class="pagination col">
        <g:paginate controller="m" action="mbox_list" prev="&lt;" next="&gt;" 
          maxsteps="1" omitFirst="${true}" omitLast="${true}" max="5" total="${data.count}" params="${params}" />
        <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
      </span>
    </li>        
  <g:each in="${data.records}" var="item">   
    <li data-icon="false" class="clearfix">
      <div class="ui-li-thumb relative" style="max-width:90px;max-height:130px;width:90px;margin-right:5px">
      <g:if test="${item?.is_system==1}">
        <img class="thumbnail shadow" alt="StayToday" border="0" src="${resource(dir:'images',file:'logo_large.png')}" />
        <p class="ui-li-desc" style="margin-top:3px;line-height:10px">
          <small style="white-space:normal">StayToday</small><br/>
        </p>
      </g:if>
      <g:elseif test="${(sender.client_id!=user.client_id && item.is_answer==0) || (sender.client_id==user.client_id && item.is_answer==1)}">      
        <img class="thumbnail shadow" alt="${user.nickname}" border="0" src="${((user.picture && !user.is_external)?imageurl+'t_':'')+(user.picture?:resource(dir:'images',file:'default-picture.png'))}" />
        <p class="ui-li-desc" style="margin-top:3px;line-height:10px">
          <small style="white-space:normal">${message(code:'inbox.view.you')}</small><br/>
          <small style="white-space:nowrap">${item.moddate}</small>
        </p>
      </g:elseif><g:elseif test="${(sender.client_id==user.client_id && item.is_answer==0) || (sender.client_id!=user.client_id && item.is_answer==1)}">                    
        <g:if test="${sender.client_id!=user.client_id}">
        <img class="thumbnail shadow" alt="${sender.nickname}" title="${nickname}" border="0" src="${((sender.picture && !sender.is_external)?imageurl+'t_':'')+(sender.picture?:resource(dir:'images',file:'default-picture.png'))}">
        </g:if><g:else>
        <img class="thumbnail shadow" alt="${recipient.nickname}" title="${recipient.nickname}" border="0" src="${((recipient.picture && !recipient.is_external)?imageurl+'t_':'')+(recipient.picture?:resource(dir:'images',file:'default-picture.png'))}">       
        </g:else>
        <p class="ui-li-desc" style="line-height:10px">
          <small style="white-space:normal">${(sender.client_id != user.client_id)? sender.nickname : recipient.nickname}</small><br/>
          <small style="white-space:nowrap">${item.moddate}</small>
        </p>
      </g:elseif>
      </div>
      <div class="ui-li-desc">
        <div class="bubble-container">
        <g:if test="${item.rectext}">
          <div style="clear:left;margin-top:5px">
            <p><small style="white-space: normal">${item.rectext}</small></p>
          </div>
        </g:if>
        <g:if test="${((sender.client_id == user.client_id && item.answertype_id in [1,2,8]) || (sender.client_id != user.client_id && item.answertype_id in [1,2,3,4])) && (mbox.modstatus < 4 || mbox.modstatus == 5)}">
          <div class="ui-body ui-body-e ui-corner-all st">
          <g:if test="${sender.client_id == user.client_id}">
            <g:if test="${item.answertype_id==1 && mbox.modstatus < 4}">
            <a data-role="button" data-theme="f" onclick="deleteRec(${item.id},1)">
              ${message(code:'inbox.view.delete.bookoffer')}
            </a>
            </g:if><g:elseif test="${item.answertype_id==2 && mbox.modstatus < 4}">
            <a data-role="button" data-theme="f" <g:if test="${mbox.modstatus!=4}">onclick="deleteRec(${item.id},0)"</g:if>>
              ${message(code:'inbox.view.delete.specoffer')}
            </a>
            </g:elseif><g:elseif test="${item.answertype_id==8 && mbox.modstatus == 5}">
            ${message(code:'inbox.view.freedays')}<br/>
            <a data-role="button" data-theme="f" onclick="removeBron(${item.mbox_id})">${message(code:'inbox.view.unlock')}</a>
            </g:elseif>
          </g:if><g:else>
            <g:if test="${item.answertype_id in [1,2] && mbox.modstatus < 4}">
            <a href="<g:createLink action='bron' params='[id:item.id,mbox_id:item.mbox_id]' base='${context?.mobileURL_lang}'/>" data-role="button" data-theme="f">${message(code:'button.booking')}</a>
            </g:if><g:elseif test="${item.answertype_id in [3,4]}">
            <p>${message(code:'inbox.view.decline.info')}</p>
            </g:elseif>
          </g:else>
          </div>
        </g:if>
        </div>
      </div>
    </li>
  </g:each>
  </ul>
</g:if><g:else>
  <ul data-role="listview" style="margin:0">
    <li class="ui-li ui-li-static">${message(code:'mobile.messages.notfound')}</li>
  </ul>
</g:else>
