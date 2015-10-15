<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
    <g:javascript>
      function initialize(){
      <g:if test="${session.attention_message!='' && session.attention_message!=null}">
        jQuery.colorbox({
          html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message}'.unescapeHTML()+'</div></div>',
          scrolling: false
        });
      </g:if>
      <g:if test="${session.attention_message_once}">
        jQuery.colorbox({
          html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message_once}'.unescapeHTML()+'</div></div>',
          scrolling: false
        });
      </g:if>
        $('submit_button').click();
      }
    </g:javascript>
    <g:setupObserve function='clickPaginate' id='results'/>
    <style type="text/css">.actions { padding: 7px 10px }</style>        
  </head>
  <body onload="initialize()">
              <g:render template="/ads_menu" />                                    
                      <div class="search-container">
                        <div id="search_body" style="display:none">
                          <g:formRemote name="homeForm" url="[action:'ads_list']" update="[success:'results']">
                            <div style="display:none">
                              <input type="submit" id="submit_button" value="${message(code:'data.view')}"/>                                      
                            </div>                            
                            <div style="position:absolute;z-index:2;top:6px;left:3px">                            
                              <span class="results_sort">
                                <label for="modstatus">${message(code:'data.list.view')}</label>
                                <select name="modstatus" id="modstatus" onchange="$('submit_button').click();">
                                  <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>${message(code:'ads.list.all')} (${status_count[0]})</option>
                                  <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>${message(code:'ads.list.active')} (${status_count[1]})</option>
                                  <option value="2" <g:if test="${inrequest?.modstatus==2}">selected="selected"</g:if>>${message(code:'ads.list.hidden')} (${status_count[2]})</option>
                                </select>
                              </span>       
                            </div>                            
                          </g:formRemote>
                          <div id="results"></div>	                                                       
                        </div>
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
