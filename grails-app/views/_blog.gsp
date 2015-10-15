<script type="text/javascript">
  $('ads_count').update(${blogs.count});  
</script>
<div id="ajax_wrap">  
<g:if test="${blogs.count>10}">
  <div id="results_header" class="results_header">
    <span class="pagination">
      <g:paginate controller="index" action="timeline_list" maxsteps="7" prev="&lt;" next="&gt;"
        max="10" total="${blogs.count}" params="${inrequest}" />
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
<g:if test="{blogs.records}">
  <table class="hfeed list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
  <g:each in="${blogs.records}" var="blog" status="i"><!--noindex-->
    <g:if test="${dates[i] && !inrequest?.day}">
    <tr style="height:40px">
      <td colspan="2" class="dashed group" valign="middle">
        <span class="block">
          <h2 class="title">
            <img src="${resource(dir:'images',file:'calendar.png')}" valign="middle" />
            <span class="link" onclick="transit(${context.is_dev?1:0},['${dates[i].getDate()<10?'0'+dates[i].getDate():dates[i].getDate()}','${dates[i].getMonth()+1<10?'0'+(dates[i].getMonth()+1):dates[i].getMonth()+1}','${dates[i].getYear()+1900}','${inrequest?.blog?:'all'}','timeline'],'','','','${context?.lang}')" style="color:#3F5765">${String.format('%td.%<tm.%<tY',dates[i])}</span>
          </h2>        
        </span>
      </td>
    </tr>
    </g:if><g:elseif test="${dates[i] && inrequest?.day}">
    <tr><td class="dashed">&nbsp;</td></tr>
    </g:elseif><!--noindex-->
    <tr>
      <td class="hentry entry" onmouseout="this.removeClassName('selected')" onmouseover="this.addClassName('selected')">
        <div class="relative contprn">
          <div class="thumbnail shadow" style="margin-bottom:10px">
            <g:link mapping="${'timeline'+context?.lang}" params="${[blog:blog?.author,year:blog.inputdate.getYear()+1900,month:blog.inputdate.getMonth()+1<10?'0'+(blog.inputdate.getMonth()+1):blog.inputdate.getMonth()+1,day:blog.inputdate.getDate()<10?'0'+blog.inputdate.getDate():blog.inputdate.getDate(),id:blog.linkname]}">
              <img width="200" height="140" src="${(blog?.picture)?imageurl+'t_'+blog?.picture:resource(dir:'images',file:'default-picture.png')}" border="0"/>
            </g:link>
          </div>
          <div class="description" style="width:480px">
            <h2 class="entry-title clearfix title">
              <g:link class="name" mapping="${'timeline'+context?.lang}" params="${[blog:blog?.author,year:blog.inputdate.getYear()+1900,month:blog.inputdate.getMonth()+1<10?'0'+(blog.inputdate.getMonth()+1):blog.inputdate.getMonth()+1,day:blog.inputdate.getDate()<10?'0'+blog.inputdate.getDate():blog.inputdate.getDate(),id:blog.linkname]}" rel="bookmark">${blog.title}</g:link>
            </h2>
            <p class="entry-content">${blog?.shortdescription}</p>
            <ul class="post-info details-list clearfix">
              <li class="details-list-item person_capacity">
                <span class="icons"></span>
                <span class="vcard author">
                  <g:if test="${blog?.googleplus_id!=''}"><a class="url fn" href="https://plus.google.com/${blog?.googleplus_id}?rel=author" rel="nofollow" target="_blank">${blog?.author}</a></g:if>
                  <g:else><g:link class="url fn" mapping="${'timeline'+context?.lang}" params="${[blog:blog?.author]}">${blog?.author}</g:link></g:else>
                </span>
              </li>
              <li class="details-list-item dates">
                <span class="icons"></span>
                <span class="updated" title="${g.formatDate(date:blog?.inputdate,format:'yyyy-MM-dd HH:mmZ')}">${String.format('%td.%<tm.%<tY',blog.inputdate)}</span>
              </li>
            </ul>
          </div>
        </div>        
        <div style="clear:left" class="results_header">
          <b class="tags float padd10">${message(code:'common.tags')}:</b>
          <span class="tags blog col" style="margin-right:10px">
          <g:each in="${blog.tags}" var="tag" status="j">
            <g:if test="${inrequest?.blog!=blog?.author||inrequest.tag?.size()!=1||(inrequest.tag?.size()==1&&inrequest.tag[0]!=tag.name)}"><g:link mapping="${'timeline'+context?.lang}" params="${[blog:blog?.author,tag:tag.name]}" rel="tag">${tag.name}</g:link></g:if><g:else><span class="link" style="color:#787878">${tag.name}</span></g:else><g:if test="${(j+1)!=blog.tags.size()}">, </g:if>
          </g:each>
          </span>          
        </div>
      </td>
    </tr>
    <g:if test="${!(dates[i+1] || (i+1)==blogs.records.size())}"><!--noindex-->
    <tr><td class="dashed">&nbsp;</td></tr><!--/noindex-->
    </g:if>
  </g:each>
  </table>  
</g:if>
</div>
