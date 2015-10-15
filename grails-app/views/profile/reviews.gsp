<html>
  <head>    
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />  
    <meta name="layout" content="main"/>
    <g:javascript>
    function initialize(){
    <g:if test="${tab==1}">
      $('your-tab').click();
    </g:if>
    }

    function viewCell(sObj1,iNum,sObj2){
      var tab = document.getElementById(sObj1);
      var tabs = tab.getElementsByTagName('li');      
      var layer = document.getElementById(sObj2);
      var divs = layer.getElementsByTagName('div');            
      var layers = new Array();      
      for (var i=0; i < divs.length; i++)
        if(divs[i].getAttribute("rel")=='layer')
          layers.push(i);        
      for (var i=0; i < tabs.length; i++){
        tabs[i].className = (i==iNum) ? 'selected' : '';
        divs[layers[i]].style.display = (i==iNum)? 'block' : 'none';
      }
    }
    function answerCommentResponse(e) {
      if (e.responseJSON.error){
        if (e.responseJSON.message){
          $('answerComment_error').show();
          $('answerComment_errorText').update(e.responseJSON.message);
        }
      } else {
        jQuery.colorbox.close();
        location.reload(true);
      }
    }
    </g:javascript>   
  </head>
  <body onload="initialize()">
              <g:render template="/profile_menu" />                        
                      <div class="content">                      
                        <ul id="reviews" class="nav">                        
                          <li id="you-tab" class="selected" onclick="viewCell('reviews',0,'layers');">                    
                            ${message(code:'reviews.you')}                    
                          </li>
                          <li id="your-tab" onclick="viewCell('reviews',1,'layers');">                    
                            ${message(code:'reviews.your')}
                          </li>  
                        </ul>                
                        <div id="layers" class="dashboard-content">
                          <div rel="layer" style="background:#fff">
                            <h2 class="toggle border">${infotext['promotext1'+context?.lang]}</h2>
                          <g:if test="${infotext['itext'+context?.lang]}">
                            <div style="padding:0 20px;">
                              <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>
                            </div><br/>
                          </g:if>                          
                            <div class="search-container">
                              <div id="search_body" class="relative">
                                <div id="results">
                                  <div class="results_header">    
                                    <span class="count" style="top:0">
                                      <b>${message(code:'reviews.records.found')}</b><span id="ads_count">${comments?.count}</span>
                                    </span>
                                    <span class="pagination col">
                                      <g:paginate controller="profile" prev="&lt;&lt;" next="&gt;&gt;"
                                        action="${actionName}" max="3" maxsteps="3" offset="${commentsOffset}" params="${[paginate:'comments']}"
                                        total="${comments?.count}" /> 
                                    </span>          
                                  </div>
                                <g:if test="${comments?.records}">
                                  <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                                  <g:each in="${comments?.records}" var="item" status="i">                  
                                    <tr>
                                      <td valign="top" onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
                                        <div class="float padd10">
                                          <div class="thumbnail userpic shadow">
                                            <img width="68" height="68" alt="${item?.nickname}" title="${item?.nickname}" 
                                              src="${(item?.picture && !item?.is_external)?imageurl:''}${(item?.smallpicture)?item?.smallpicture:resource(dir:'images',file:'user-default-picture.png')}"/>
                                          </div>
                                          <div style="width:68px">
                                            <small style="white-space:normal"><g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+item?.user_id]}">${item?.nickname}</g:link></small><br/>
                                            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',item?.comdate)}</small>
                                          </div>
                                        </div>
                                        <div class="col bubble-container" style="width:585px;height:68px;overflow-y:auto">
                                          <small>${item.comtext}</small>                                        
                                        </div>
                                        <div class="col" align="right" style="margin-right:10px">
                                          <a class="stars_icon none" id="answerComment_link${i}" href="javascript:void(0)" onclick="$('com_id').value=${item.id}" >${message(code:'reviews.respond')}</a>
                                        </div>
                                      </td>
                                    </tr>
                                  <g:if test="${(i+1)!=comments?.records.size()}"> 
                                    <tr>
                                      <td class="dashed">&nbsp;</td>
                                    </tr>
                                  </g:if>                                    
                                  <g:javascript>
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
                                      }
                                    });
                                  </g:javascript>
                                  <g:each in="${answerComments[i]}" var="answ" status="j">
                                    <tr>
                                      <td valign="top" onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
                                        <div class="float padd10" style="margin-left:84px">
                                          <div class="thumbnail userpic shadow">
                                            <img width="68" height="68" alt="${user?.nickname}" title="${user?.nickname}" 
                                              src="${(user?.picture && !user?.is_external)?imageurl:''}${(user?.smallpicture)?user?.smallpicture:resource(dir:'images',file:'user-default-picture.png')}"/>
                                          </div>
                                          <div style="width:68px">
                                            <small style="white-space:normal"><g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+user?.id]}">${user?.nickname}</g:link></small><br/>
                                            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',answ?.comdate)}</small>
                                          </div>
                                        </div>
                                        <div class="col bubble-container" style="width:500px;height:68px;overflow-y:auto">                                                                                 
                                          <small>${answ.comtext}</small>
                                        </div>
                                      </td>
                                    </tr>                                    
                                    <tr>
                                      <td class="dashed">&nbsp;</td>
                                    </tr>                                    
                                  </g:each>
                                </g:each>
                                  </table>
                                </g:if><g:else>
                                  <div class="padd20">
                                    <p>${message(code:'reviews.you.nofound')}.</p>
                                  </div>
                                </g:else>  
                                </div>
                              </div>
                            </div>
                          </div>
                          <div rel="layer" style="background:#fff;display: none">
                            <h2 class="toggle border">${infotext['promotext2'+context?.lang]}</h2>
                          <g:if test="${infotext['itext2'+context?.lang]}">
                            <div style="padding:0px 20px">
                              <g:rawHtml>${infotext['itext2'+context?.lang]}</g:rawHtml>
                            </div>
                          </g:if>
                            <div class="padd20">
                              <p>${message(code:'reviews.your.need.nofound')}!</p>
                            </div><br/>            
                            
                            <h2 class="toggle border">${message(code:'reviews.your.title')}</h2>
                            
                            <div class="search-container">
                              <div id="search_body" class="relative">
                                <div id="results">
                                  <div class="results_header">    
                                    <span class="count" style="top:0">
                                      <b>${message(code:'reviews.records.found')}</b><span id="ads_count">${total_yourcomments}</span>
                                    </span>
                                    <span class="pagination col">
                                      <g:paginate controller="profile" prev="&lt;&lt;" next="&gt;&gt;"
                                        action="${actionName}" max="3" maxsteps="3" offset="${yourcommentsOffset}" params="${[paginate:'yourcomments']}"
                                        total="${total_yourcomments}" /> 
                                    </span>          
                                  </div>
                                <g:if test="${yourcomments}">
                                  <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">                                                              
                                  <g:each in="${yourcomments}" var="item" status="i">                  
                                    <tr>
                                      <td valign="top" onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
                                        <div class="float padd10">
                                          <div class="thumbnail userpic shadow">
                                            <img width="68" height="68" alt="${user?.nickname}" title="${user?.nickname}" 
                                              src="${(user?.picture && !user?.is_external)?imageurl:''}${(user?.smallpicture)?user?.smallpicture:resource(dir:'images',file:'user-default-picture.png')}">
                                          </div>
                                          <div style="width:68px">
                                            <small style="white-space:normal"><g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+item?.user_id]}">${user?.nickname}</g:link></small><br/>
                                            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',item?.comdate)}</small>
                                          </div>
                                        </div> 
                                        <div class="col bubble-container" style="width:585px;height:68px;overflow-y:auto">                                        
                                          <small>${item.comtext}</small>              
                                        </div>
                                      </td>
                                    </tr>
                                    <g:if test="${(i+1)!=yourcomments.size()}"> 
                                    <tr>
                                      <td class="dashed">&nbsp;</td>
                                    </tr>
                                    </g:if>
                                  </g:each>
                                  </table>
                                </g:if><g:else>
                                  <div class="padd20">
                                    <p>${message(code:'reviews.your.nofound')}.</p>
                                  </div>
                                </g:else>
                                </div>
                              </div>
                            </div>
                          </div>  
                        </div>
                      </div>

                      <div id="answerComment_lbox" class="new-modal" style="display:none">
                        <h2 class="clearfix">${message(code:'reviews.response')}</h2>
                        <g:formRemote name="answerCommentForm" url="${[controller:'home',action:'addAnswerComment']}" style="padding:5px" onSuccess="answerCommentResponse(e);">
                        <div id="answerComment_lbox_segment" class="segment nopadding" style="height:190px">
                          <div id="answerComment_lbox_container" class="lightbox_filter_container">
                            <div id="message_data"></div>         
                            <div id="answerComment_error" class="notice" style="margin:0 0 10px 0;width:97%;display:none">
                              <span id="answerComment_errorText"></span>
                            </div>
                            <label for="answ_comtext">${message(code:'common.text_of_message').capitalize()}:</label>
                            <textarea rows="4" cols="60" id="answ_comtext" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" name="answ_comtext" style="width:97%;height:70px;margin-top:3px"/></textarea>
                          </div>
                        </div>
                        <div class="segment buttons"> 
                          <input type="submit" class="button-glossy orange" value="${message(code:'button.send')}"/>
                          <input id="com_id" type="hidden" name="com_id" value="" />
                        </div>
                        </g:formRemote>
                      </div>

                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>                    
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
