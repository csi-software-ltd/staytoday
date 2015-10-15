<div id="ajax_wrap">
  <div id="results_header" class="results_header">    
    <span class="count float" style="top:0px">
      <b>${message(code:'reviews.records.found')}</b><span id="ads_count">${comments.count}</span>
    </span>
    <span class="pagination col">
      <g:paginate controller="profile" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${inrequest}"	total="${comments.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
  <g:if test="${comments.records}">
  <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">  
  <g:each in="${comments.records}" status="i" var="record">                      
    <tr>                      
      <td valign="top" onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
        <div class="float padd10">
          <div class="thumbnail userpic shadow">          
            <img width="68" height="68" alt="${record.nickname}" title="${record.nickname}" 
              src="${(record.picture && !record.is_external)?imageurl:''}${(record.smallpicture)?record.smallpicture:resource(dir:'images',file:'user-default-picture.png')}">
          </div>
          <div style="width:68px">
            <small style="white-space:normal"><g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+record?.user_id]}">${record?.nickname}</g:link></small><br/>
            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',record?.comdate)}</small>
          </div>
        </div>
        <div class="bubble-container col" style="width:585px;overflow-y:auto">          
          <small>${record?.comtext}</small>                            
        </div>        
        <div class="col" align="right" style="margin-right:10px">
          <g:link class="stars_icon ${(record?.rating==2)?'positively':((record?.rating==1)?'negatively':'neutrally')}" mapping="${'hView'+context?.lang}" params="${[country:Country.get(Home.get(record?.home_id)?.country_id)?.urlname,city:Home.get(record?.home_id)?.city,linkname:(Home.get(record?.home_id)?.linkname)]}">${message(code:'ads.view').capitalize()}</g:link>
        <g:if test="${record?.user_id==inrequest.u_id}">
          <a class="stars_icon none" href="javascript:void(0)" onclick="commentDelete(${record?.id},1);">${message(code:'reviews.delete')}</a>
        </g:if>
        <g:if test="${user?.id==owneruser?.id}">
          <a class="stars_icon none" id="answerComment_link${i}" href="javascript:void(0)" onclick="$('com_id').value=${record.id}" >${message(code:'reviews.response')}</a>
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
                $('answerComment_error').hide();
                $("answ_comtext").value = '';
                $('comments').click();
              }
            });
          </script>
        </g:if>              
        </div>      
      </td>       
    </tr>
  <g:if test="${(i+1)!=comments.records.size() || answerComments[i].size()>0}"> 
    <tr><td class="dashed">&nbsp;</td></tr>
  </g:if>                                           
  <g:each in="${answerComments[i]}" var="answ" status="j">
    <tr>
      <td valign="top" onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
        <div class="float padd10" style="margin-left:84px">
          <div class="thumbnail userpic shadow">
            <img width="68" height="68" alt="${owneruser?.nickname}" title="${owneruser?.nickname}" 
              src="${(owneruser?.picture && !owneruser?.is_external)?imageurl:''}${(owneruser?.smallpicture)?owneruser?.smallpicture:resource(dir:'images',file:'user-default-picture.png')}">
          </div>
          <div style="width:68px">
            <small style="white-space:normal"><g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+owneruser?.id]}">${owneruser?.nickname}</g:link></small><br/>
            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',answ?.comdate)}</small>
          </div>
        </div>            
        <div class="bubble-container col"  style="width:500px;overflow-y:auto">          
          <small>${answ.comtext}</small>
        </div>
      </td>
    </tr>
    <g:if test="${(i+1)!=comments.records.size() || (j+1)!=answerComments[i].size()}"> 
    <tr><td class="dashed">&nbsp;</td></tr>
    </g:if>
  </g:each>
</g:each>  
  </table> 
</g:if>  
</div>
