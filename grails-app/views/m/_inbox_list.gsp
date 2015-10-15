<g:if test="${data.records}">
  <ul data-role="listview" id="ajax_wrap" style="margin:0">
    <li data-role="divider" data-theme="a" style="padding:0 0 0 10px;height:40px">
      <span class="count float">
        <span>${message(code:'mobile.found')}</span><span id="ads_count">${data.count}</span>
      </span>
      <span class="pagination col">
        <g:paginate controller="m" action="inbox_list" prev="&lt;" next="&gt;"
          maxsteps="1" omitFirst="${true}" omitLast="${true}" max="5" total="${data.count}" params="${params}"/>
        <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>          
      </span>
    </li>
  <g:each in="${data.records}" var="item"> 
    <li data-icon="false">
      <a href="${createLink(uri:'/mbox?id='+item.id,base:context?.mobileURL_lang)}" title="${message(code:'inbox.list.detail')}">
        <div class="ui-li-thumb relative" style="width:90px;max-width:90px;max-height:130px;margin-right:5px">
          <g:if test="${((item.homeclient_id==user.client_id)&&(item.is_answer==0)&&(item.is_read==0))||((item.homeclient_id!=user.client_id)&&(item.is_answer==1)&&(item.is_read==0))}"><div class="new">!</div></g:if>
        <g:if test="${item.homeclient_id==user.client_id}">  
          <img class="thumbnail userpic shadow" alt="${item.client_nickname}" title="${item.client_nickname}" border="0" src="${((item.client_picture && !item.client_external)?imageurl:'')+(item.client_picture?:resource(dir:'images',file:'default-picture.png'))}" />
        </g:if><g:else>
          <img class="thumbnail userpic shadow" alt="${item.home_name}" title="${item.home_name}" border="0" src="${item.homepicture?urlphoto+item.homeclient_id+'/t_'+item.homepicture:resource(dir:'images',file:'default-picture.png')}" />
        </g:else>
          <p class="ui-li-desc" style="margin-top:3px;line-height:10px">
            <small style="white-space:normal">
              <g:if test="${(Home.get(item?.home_id)?.client_id?:0) == user?.client_id}">${item.client_nickname}</g:if>
              <g:else>${item.user_nickname}</g:else>
            </small><br/>
            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',item.moddate)}</small>
          </p>
        </div>
      <g:if test="${item.homeclient_id == user.client_id}">
        <h2 style="color:${(item.is_answer==0 && item.is_read==0)?'#FF530D':'#3f5765'}!important">
          ${item.mtextowner?:message(code:'personal.mbox.with')+item.user_nickname}
      </g:if><g:else>
        <h2 style="color:${(item.is_answer==1 && item.is_read==0)?'#FF530D':'#3f5765'}!important">
          ${item.mtext?:message(code:'personal.mbox.with')+item.user_nickname}
      </g:else>
        </h2>
        <ul class="ui-li-desc details-list">
          <li class="details-list-item location">
            <span class="icons"></span>${(item?.homeclient_id==user?.client_id)?item?.home_address:item?.shortaddress}
          </li>
          <li class="details-list-item dates"><span class="icons"></span>
            ${String.format('%td.%<tm.%<tY',item?.date_start)} - ${String.format('%td.%<tm.%<tY',item?.date_end)}
          </li>
          <li class="details-list-item person_capacity"><span class="icons"></span>
            ${Homeperson.get(item.homeperson_id)['name'+context?.lang]}
          </li>
        </ul>
        <div class="ui-li-desc">
          <p class="ui-li-aside" style="width:180px;margin-top:5px">
          <g:if test="${item.answertype_id>0}">
            <font style="color:${Answertype.get(item.answertype_id)?.color}">${Answertype.get(item.answertype_id)['name_mbox'+context?.lang]}</font>
          </g:if><g:else><font style="color:gray">${message(code:'personal.request')}</font></g:else>
            <br/><span class="b-rub">${Math.round(item?.price_rub / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
            <font color="#333">${message(code:'personal.cost')}</font>
          </p>
        </div>
      </a>
    </li>
  </g:each>        
  </ul>
</g:if><g:else>
  <ul data-role="listview" style="margin:0">
    <li class="ui-li ui-li-static">${message(code:'mobile.messages.notfound')}</li>
  </ul>
</g:else>
