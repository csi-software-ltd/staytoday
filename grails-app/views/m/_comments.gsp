<g:if test="${records}">
  <ul data-role="listview" id="ajax_wrap" style="margin:0">
    <li data-role="divider" data-theme="a" style="padding:0 0 0 10px;height:40px">
      <span class="count float">
        <span>${message(code:'mobile.found')}</span><span id="ads_count">${count}</span>
      </span>
      <span class="pagination col">
        <g:paginate controller="m" action="comments" prev="&lt;" next="&gt;" maxsteps="1"
          max="3" total="${count}" params="${[home_id:home.id,owner_id:owneruser.id]}"/>
        <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
      </span>
    </li>
  <g:each in="${records}" var="comm" status="i">
    <li data-icon="false" class="clearfix">
      <div class="ui-li-thumb relative" style="max-width:90px;max-height:130px;width:90px;margin-right:5px">
        <img class="thumbnail shadow" alt="${comm.nickname}" title="${comm.nickname}" src="${((comm.picture && !comm.is_external)?imageurl:'')+(comm.smallpicture?:resource(dir:'images',file:'user-default-picture.png'))}" />
        <p class="ui-li-desc">
          <small style="white-space:normal">${comm.nickname}</small><br/>
          <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',comm?.comdate)}</small>
        </p>
      </div>
      <div class="ui-li-desc">
        <div class="bubble-container">
          <small style="white-space: normal">${comm.comtext}</small>
        </div>
      <g:if test="${comm?.user_id==inrequest.u_id}">
        <span class="col" align="right">
          <a class="ui-link" href="javascript:void(0)" onclick="commentDelete(${comm.id});">${message(code:'reviews.delete')} &raquo;</a>
        </span>
      </g:if><g:if test="${(comm.typeid==2 && comm.home_id==inrequest?.u_id)||(comm.typeid==1 && Home.get(comm?.home_id).client_id==user?.client_id)}">
        <span class="col" align="right">
          <a class="ui-link" id="answerComment_link${i}" data-rel="popup" href="#answerCommentPopup" onclick="jQuery('#com_id').val(${comm.id})">${message(code:'reviews.respond')} &raquo;</a>
        </span>
      </g:if>
      </div>
    </li>
    <g:each in="${answerComments[i]}">
    <li data-icon="false" class="clearfix">
      <div class="ui-li-thumb relative" style="max-width:90px;max-height:130px;width:90px;margin-right:5px">
        <img class="thumbnail shadow" alt="${owneruser.nickname}" title="${owneruser.nickname}" src="${((owneruser.picture && !owneruser.is_external)?imageurl:'')+(owneruser.smallpicture?:resource(dir:'images',file:'user-default-picture.png'))}" />
        <p class="ui-li-desc">
          <small style="white-space:normal">${owneruser.nickname}</small><br/>
          <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',it?.comdate)}</small>
        </p>
      </div>
      <div class="ui-li-desc">
        <div class="bubble-container">
          <small style="white-space:normal">${it.comtext}</small>
        </div>
      </div>
    </li>
    </g:each>
  </g:each>
  </ul>
</g:if><g:else>
  <ul data-role="listview" style="margin:0">
    <li class="ui-li ui-li-static">${message(code:'mobile.reviews.notfound')}</li>
  </ul>
</g:else>
