<div id="ajax_wrap2">
  <div class="results_header relative">
    <span class="count float" style="top:0px">
      <b>${message(code:'reviews.records.found')}</b><span id="ads_count">${count}</span>
    </span>
    <span class="pagination col" style="margin-right:10px">
      <g:paginate controller="home" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="3" params="${inrequest}"  total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div style="height:${(user && user?.client_id!=home?.client_id)?'308':'408'}px;overflow-y:auto">
    <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
    <g:each in="${records}" var="record" status="i">                       
      <tr>                      
        <td valign="top" onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
          <div class="float padd10">
            <div class="thumbnail userpic shadow">
              <img width="68" height="68" alt="${record.nickname}" alt="${record.nickname}" src="${(record.picture && !record.is_external)?imageurl:''}${(record.smallpicture)?record.smallpicture:resource(dir:'images',file:'user-default-picture.png')}">
            </div>
            <div style="width:68px">
              <small style="white-space:normal"><g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+record?.user_id]}" base="${context?.mainserverURL_lang}">${record?.nickname}</g:link></small><br/>
              <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',record?.comdate)}</small>
            </div>
          </div>
          <div class="bubble-container col" style="width:570px;height:68px;overflow-y:auto">                
            <small>${record?.comtext}</small>
          </div> 
        <g:if test="${record?.user_id==inrequest.u_id}">
          <div class="col" align="right" style="margin-right:10px">
            <a class="stars_icon none" href="javascript:void(0)" rel="nofollow" onclick="commentDelete(${record?.id});">${message(code:'reviews.delete')}</a>
          </div>
        </g:if>
        <g:if test="${(record.typeid==2 && record.home_id==inrequest?.u_id)||(record.typeid==1 && Home.get(record?.home_id).client_id==user?.client_id)}">
          <div class="col" align="right" style="margin-right:10px">
            <a class="stars_icon none" id="answerComment_link${i}" href="javascript:void(0)" rel="nofollow" onclick="$('com_id').value=${record.id}">${message(code:'reviews.respond')}</a>
          </div>
          <script type="text/javascript">
          jQuery('#answerComment_link'+${i}).colorbox({
            inline: true,
            href: '#answerComment_lbox',
            scrolling: false,
            onLoad: function(){
              jQuery('#answerComment_lbox').show();
            },
            onCleanup: function(){
              jQuery('#answerComment_lbox').hide();
              jQuery('#answerComment_error').hide();
              jQuery('#answ_comtext').value = '';
            }
          });
          </script>
        </g:if>        
        </td>       
      </tr>
      <g:if test="${(i+1)!=records.size()}"> 
      <tr><td class="dashed">&nbsp;</td></tr>
      </g:if>                                             
      <g:each in="${answerComments[i]}" var="answ" status="j">
      <tr>
        <td valign="top" onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
          <div class="float padd10" style="margin-left:84px">
            <div class="thumbnail userpic shadow">
              <img width="68" height="68" alt="${owneruser?.nickname}" title="${owneruser?.nickname}" src="${(owneruser?.picture && !owneruser?.is_external)?imageurl:''}${(owneruser?.smallpicture)?owneruser?.smallpicture:resource(dir:'images',file:'user-default-picture.png')}">
            </div>
            <div style="width:68px">
              <small style="white-space:normal"><g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+owneruser?.id]}" base="${context?.mainserverURL_lang}">${owneruser?.nickname}</g:link></small><br/>
              <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',answ?.comdate)}</small>
            </div>
          </div>
          <div class="col bubble-container" style="width:485px;height:68px;overflow-y:auto">          
            <small>${answ.comtext}</small>
          </div>
          <g:if test="${answ?.user_id==inrequest.u_id}">
            <div align="right" style="float:right">
              <small><a href="javascript:void(0)" rel="nofollow" onclick="commentDelete(${answ?.id});">${message(code:'reviews.response.delete')} &raquo;</a></small>
            </div>
          </g:if>
        </td>
      </tr>
      <tr><td class="dashed">&nbsp;</td></tr>
      </g:each>
    </g:each>
    </table>             
  </div>
</g:if>
</div>
