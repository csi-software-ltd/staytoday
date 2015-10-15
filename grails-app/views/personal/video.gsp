<html>
  <head>    
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />           
    <meta name="layout" content="main"/>
    <g:javascript library="jquery-1.8.3" />    
    <g:javascript library="jquery-ui.min"/>
    <g:javascript library="jquery.qtip.min" /> 
    <g:javascript>
    var j = jQuery.noConflict();
    var apiX = null;    
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
      j('#pictures').sortable("disable");
      j('#pictures li').css({ 'cursor' : 'default' }); 
      j('#pictures li .layout').show();
      j('#sortsave').hide();
      j('#sortedit').show();
      j('#loader').hide();
    }    
    function deleteVideo(lId){
      if (confirm('Вы уверены?')){
        $('deleteVideoForm'+lId).submit();
      }
    }    
    function sortedit(){
      j('#sortsave').show();
      j('#sortedit').hide(); 
      j('#pictures').sortable("enable");      
      j('#pictures').sortable();      
      j('#pictures').disableSelection();
      j('#pictures li').css({ 'cursor' : 'move' }); 
      j('#pictures li .layout').hide();
      j('#loader').show();
    }
    function sortable(){         
      var aOrder = j('#pictures').sortable('toArray');
      <g:remoteFunction action='sort_video' params="\'ids=\'+aOrder" />
      initialize();      
    }    
    </g:javascript>
    <style type="text/css">#pictures .thumbnail { border-width: 5px !important }	</style>
  </head>
  <body onload="initialize()">
                <g:render template="/personal_menu" />
                          <div rel="layer">                                   
                            <div class="form">                  
                              <h2 class="toggle border"><span class="edit_icon videos"></span>${infotext['header'+context?.lang]?:''}</h2>
                            <g:if test="${homevideo}">  
                              <div style="padding:0 10px 0 20px">
                              <g:if test="${infotext['itext'+context?.lang]}">                        
                                <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>                        
                              </g:if>                    
                                <ul class="ui-sortable" id="pictures">
                                <g:each in="${homevideo}" var="item" status="i">            
                                  <li class="ui-state-default" id="photo_${item.id}">
                                    <div class="thumbnail shadow" id="thumbnail_${item.id}">
                                      <g:javascript>
                                      j(document).ready(function(){
                                        j.fn.qtip.defaults.hide.delay = 3500;                  
                                        j('#mm_${item.id}').qtip({      
                                          position: { my: 'top center', at: 'bottom center' },
                                          events: {
                                            show: function(event, api) {
                                              if (apiX)
                                                apiX.hide();
                                              apiX = api;
                                            }
                                          },
                                          style: { classes: 'ui-tooltip-shadow ui-tooltip-' + 'plain' },
                                          content: { text: j('#sm_${item.id}') }
                                        });                  
                                      });
                                      </g:javascript>              
                                      <img id="mm_${item?.id}" width="200" height="140" src="http://i.ytimg.com/vi/${item?.videoid}/0.jpg" />
                                      <div id="sm_${item.id}" style="display:none">              
                                        <div class="actions">                    
                                          <span class="action_button nowrap">
                                            <g:link class="icon edit" controller="personal" action="homevideo" params="${[id:item.id,home_id:item.home_id]}">${message(code:'button.change')}</g:link>
                                          </span>
                                          <span class="action_button nowrap">
                                            <a class="icon delete" href="javascript:deleteVideo(${item.id})">${message(code:'button.delete')}</a>                     
                                          </span>                    
                                          <g:form name="deleteVideoForm${item.id}" controller="personal" action="homevideodelete" params="${[id:item.id,home_id:item.home_id]}" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                                          </g:form>
                                        </div>   
                                      </div>                                  
                                    </div>
                                  </li>                    
                                </g:each>          
                                </ul>       
                              </div>
                            </g:if>
                              <div style="padding:45px 20px;clear:both">
                                <div id="loader" class="spinner" style="display:none">
                                  <img src="${resource(dir:'images',file:'spinner.gif')}" align="absmiddle" hspace="5" border="0">
                                  <span>${message(code:'ads.photo.order.sort')}</span>
                                </div>
                                <div class="clearfix float">
                                  <g:form  id="addForm" name="addForm" url="[controller:'personal',action:'homevideo']" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                                    <input type="hidden" name="home_id" value="${home?.id?:0}"/>
                                    <input type="submit" class="button-glossy orange" value="${message(code:'ads.video.add')}"/>          
                                  </g:form>
                                </div>
                              <g:if test="${homevideo.size() > 1}">                   
                                <div class="float" style="margin-left:5px">       
                                  <input type="button" class="button-glossy lightblue" id="sortedit" onclick="sortedit();return false;" value="${message(code:'ads.order.edit')}"/>
                                  <input type="button" style="display:none" class="button-glossy lightblue" id="sortsave" onclick="sortable();return false;" value="${message(code:'ads.order.save')}"/>            
                                </div>
                              </g:if>
                                <span class="actions col">
                                  <span class="action_button orange">
                                    <g:link class="icon none" target="_blank" controller="personal" action="adsoverview" id="${home?.id}" base="${context.sequreServerURL}">${Infotext.findByControllerAndAction('personal','adsoverview')['name'+context?.lang]}</g:link>
                                  </span>                
                                </span>
                              </div>                      
                            </div>
                          </div>
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
