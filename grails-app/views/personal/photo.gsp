<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
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
    function deletePhoto(lId){
      if (confirm('Вы уверены?')){
        $('deletePhotoForm'+lId).submit();
      }
    }    
    function sendForm(){      
      $("mapForm").submit();			 	  
    }    
    function showImage(sPic){
      var href = window.location.href.split('photo');
      window.open(href[0]+"bigimage?picture="+sPic);
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
      <g:remoteFunction action='sort_photo' params="\'ids=\'+aOrder" />
      initialize();
    }
    
    function setMainPhoto(lId){
      <g:remoteFunction action='set_main_photo' update='main_photo' params="\'id=\'+lId" />
    }
    j(document).ready(function(){
      j('.bigPhotoSlider').colorbox({
        rel:'bigPhotoSlider',
        next:'',
        previous:'',
        loop: false,
        onLoad: function(){
          j('#cboxNext').show();
          j('#cboxPrevious').show();
        }
      });
      j('.bigMainPhoto').colorbox({
        rel:'bigMainPhoto',
        onLoad: function(){
          j('#cboxNext').show();
          j('#cboxPrevious').show();
        }
      });
    });
    </g:javascript>
    <style type="text/css">
    #pictures .thumbnail { border-width: 5px !important }	
    #cboxLoadedContent { border: 5px solid white; border-radius: 10px }
    </style>
  </head>
  <body onload="initialize()">
              <g:render template="/personal_menu" />
                          <div rel="layer">                                   
                            <div class="form">
                              <h2 class="toggle border"><span class="edit_icon photo"></span>${message(code:'ads.photo.main')}</h2>
                              <div class="padd20">
                                <div id="main_photo" class="thumbnail shadow" style="float:none">
                                  <g:if test="${home?.mainpicture}"><a class="bigMainPhoto" href="${imageurl}${home?.mainpicture}"><img width="200" height="140" src="${imageurl}${'t_'+home?.mainpicture}" border="0" /></a></g:if>
                                  <g:else><img width="200" height="140" src="${resource(dir:'images',file:'default-picture.png')}" border="0" /></g:else>
                                </div><br/><br/>
                              </div>
                            <g:if test="${homephoto}">
                              <h2 class="toggle border"><span class="edit_icon photos"></span>${infotext['header'+context?.lang]?:''}</h2>
                              <div style="padding:0 10px 0 20px">
                              <g:if test="${infotext['itext'+context?.lang]}">                        
                                <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>                        
                              </g:if> 
                              
                                <ul class="ui-sortable" id="pictures">
                                <g:each in="${homephoto}" var="item" status="i">            
                                  <li class="ui-state-default" id="photo_${item.id}">
                                    <div class="thumbnail shadow <g:if test="${item?.is_main}">selected</g:if>" id="thumbnail_${item.id}">
                                    <g:if test="${item?.picture}">
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
                                      <a class="bigPhotoSlider" href="${imageurl}${item?.picture}"><img id="mm_${item.id}" width="200" height="140" src="${imageurl}${'t_'+item?.picture}" border="0" /></a>
                                      <div id="sm_${item.id}" style="display:none">                                
                                        <span class="actions">                    
                                          <div id="main_${item.id}" style="display:${(item.is_main)?'none':'block'}">
                                            <span class="action_button nowrap">                    
                                              <a class="icon photo" href="javascript:setMainPhoto(${item.id});">${message(code:'ads.photo.main')}</a>                     
                                            </span>
                                          </div>
                                          <span class="action_button nowrap">
                                            <g:link class="icon edit" controller="personal" action="homephoto" params="${[id:item.id,home_id:item.home_id]}">${message(code:'button.change')}</g:link>
                                          </span>
                                          <div id="delete_${item.id}" style="display:${(item.is_main)?'none':'block'}">
                                            <span class="action_button nowrap">
                                              <a class="icon delete" href="javascript:deletePhoto(${item.id})">${message(code:'button.delete')}</a>                     
                                            </span>
                                          </div>
                                          <g:form name="deletePhotoForm${item.id}" controller="personal" action="homephotodelete" params="${[id:item.id,home_id:item.home_id]}" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                                          </g:form>
                                        </span>   
                                      </div>                  
                                    </g:if>
                                    </div>
                                  </li>                    
                                </g:each>          
                                </ul>          
                              </div>
                            </g:if>    
                              <div style="padding:45px 20px;clear:both">                          
                                <div id="loader" class="spinner" style="display:none">
                                  <img src="${resource(dir:'images',file:'spinner.gif')}" align="absmiddle" hspace="5" border="0" />
                                  <span>${message(code:'ads.order.sort')}</span>
                                </div>                        
                                <div class="cliarfix float">
                                  <g:form  id="addForm" name="addForm" url="[controller:'personal',action:'homephoto']" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                                    <input type="hidden" name="home_id" value="${home?.id?:0}"/>
                                    <input type="submit" class="button-glossy orange" value="${message(code:'ads.photo.add')}"/>          
                                  </g:form>
                                </div>
                              <g:if test="${homephoto.size() > 1}">                   
                                <div class="float" style="margin-left:5px">       
                                  <input type="button" class="button-glossy lightblue" id="sortedit" onclick="javascript:sortedit();return false;" value="${message(code:'ads.order.edit')}" />
                                  <input type="button" style="display:none" class="button-glossy lightblue" id="sortsave" onclick="javascript:sortable();return false;" value="${message(code:'ads.order.save')}" />            
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
                          <div rel="layer" style="display:none">
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
