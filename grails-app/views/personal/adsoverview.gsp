<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main"/>
    <g:javascript src="jquery-1.8.3.js" />
    <g:javascript>
    function init(){
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
    }
		function initialize(){
			$('txt_activation').hide();
			$('txt_descr').hide();
			$('txt_map').hide();
			$('txt_photo').hide();
			$('txt_price').hide();
			$('txt_tel').hide();
			$('txt_morephotos').hide();
		}
		function deleteHome(){
			if (confirm("${message(code:'ads.detail.delete')}"))
        $('homeDelete').submit();			
		}
    function renderStat(lId){
      <g:remoteFunction controller='personal' action='viewdetstats' update="[success: 'detailData']" params="'id='+lId" onComplete="processResponce(e);" />
    }
    function processResponce(e){
      $('stat_link').click();
    }
    jQuery(document).ready(function() {
      jQuery('#rating_details_link').colorbox({
        inline: true,
        href: '#rating_details_lbox',
        scrolling: false,
        onLoad: function(){
          jQuery('#rating_details_lbox').show();
        },
        onCleanup: function(){
          jQuery('#rating_details_lbox').hide();
        }
      });
      jQuery('a#stat_link').colorbox({
        inline: true,
        href: '#stat_lbox',
        scrolling: false,
        onLoad: function(){
          jQuery('#stat_lbox').show();
        },
        onCleanup: function(){
          jQuery('#stat_lbox').hide();
        },
        onComplete: function(){
          jQuery.colorbox.resize();
        }
      });
    });
    </g:javascript>
    <style type="text/css">.count { position: relative !important }</style>
  </head>
  <body onload="init()">
              <g:render template="/personal_menu" />                        
                      <div class="body">                        
                        <h2 class="toggle border"><span class="edit_icon ask"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                        <div class="col relative" style="margin:-57px 10px 0">
                          <span class="count" style="top:0;left:0">
                            <b><a href="javascript:void(0)" onclick="renderStat(${home.id})">${message(code:'ads.detail.stat.view')}</a> |  
                            <a id="rating_details_link" href="javascript:void(0)">${message(code:'ads.detail.stat.rating')}</a></b><span id="ads_count">${home.rating}</span>                          
                          </span>                        
                        </div>
                      <g:if test="${home?.comments}">
                        <div class="notice" style="clear:both;margin:0 15px 20px 20px">
                          <b>${message(code:'ads.detail.comment')}:</b><br/>
                          <font color="#FF530D"><g:rawHtml>${home.comments}</g:rawHtml></font>
                        </div>                          
                      </g:if>                        
                      <g:if test="${infotext['itext'+context?.lang]}">
                        <div class="padd20">
                          <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                        </div>
                      </g:if>
                        <ul class="clearfix">
                        <g:each in="${homestep}" var="step" status="i">
                          <li class="todo-item">
                            <img width="24" height="24" src="${(step.term.split('\\.')[0]=='home'?home.(step.term.split('\\.')[1]):user.(step.term.split('\\.')[1])).toString().matches('[1+].*')?resource(dir:'images',file:'checkmark.png'):resource(dir:'images',file:'checkmark-inactive.png')}" title="${message(code:(step.term.split('\\.')[0]=='home'?home.(step.term.split('\\.')[1]):user.(step.term.split('\\.')[1])).toString().matches('[1+].*')?'ads.detail.step.completed':'ads.detail.step.incompleted')}" alt="${message(code:(step.term.split('\\.')[0]=='home'?home.(step.term.split('\\.')[1]):user.(step.term.split('\\.')[1])).toString().matches('[1+].*')?'ads.detail.step.completed':'ads.detail.step.incompleted')}">
                            <p class="text">
                              <span class="${(step.term.split('\\.')[0]=='home'?home.(step.term.split('\\.')[1]):user.(step.term.split('\\.')[1])).toString().matches('[1+].*')?'text-completed':''}">
                                <g:link class="text-completed" controller="${step?.controller}" action="${step?.action}" 
                                  id="${(step?.controller=='personal')?home?.id:''}" base="${context?.mainserverURL_lang}">${step['name'+context?.lang]}</g:link>
                              </span>
                              <span class="tip" id="txt_${step?.action}">${(step.term.split('\\.')[0]=='home'?home.(step.term.split('\\.')[1]):user.(step.term.split('\\.')[1])).toString().matches('[1+].*')?step['text_yes'+context?.lang]:step['text_no'+context?.lang]}</span>
                            </p>
                          </li>          
                        </g:each>
                        </ul>                        
                        <table cellpadding="5" cellspacing="0" border="0" style="margin:5px 15px">
                          <tr>
                            <td>
                              <g:form name="publish" controller="personal" action="publishHome" base="${context?.mainserverURL_lang}">
                                <input type="hidden" name="id" value="${home.id?:0}"/>
                              <g:if test="${home.modstatus==3}">
                                <input type="submit" class="button-glossy orange" value="${message(code:'ads.detail.publish')}"/>
                              </g:if><g:elseif test="${(home.modstatus==-1)&&(home.is_step_price!=0)}">
                                <input type="submit" class="button-glossy orange" value="${message(code:'ads.detail.publish.tobe')}"/>
                              </g:elseif><g:elseif test="${(home.modstatus==1)||(home.modstatus==4)}">
                                <input type="submit" class="button-glossy grey" value="${message(code:'ads.detail.publish.remove')}"/>
                              </g:elseif>
                              </g:form>
                            </td>
                            <td style="padding:20px 0">
                              <g:form name="homeDelete" controller="personal" action="deleteHome" base="${context?.mainserverURL_lang}">
                                <input type="hidden" name="id" value="${home.id?:0}"/>
                              <g:if test="${home.modstatus!=-2}">
                                <input type="button" class="button-glossy red" value="${message(code:'ads.detail.delete')}" onclick="deleteHome()"/>
                              </g:if>
                              </g:form>
                            </td>
                          </tr>
                        </table>
                        
                      <g:if test="${home.modstatus==1}">
                        <h2 class="toggle border"><span class="edit_icon promo"></span>${infotext['promotext2'+context?.lang]?:''}</h2>                        
                        <g:if test="${infotext?.itext2}">  
                        <div class="padd20">
                          <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                        </div>
                        </g:if>
                        <ul style="margin-top: -5px"> 
                        <g:each in="${advancedhomestep}" var="step">
                          <li class="todo-item">
                            <img width="24" height="24" src="${step.is_done?resource(dir:'images',file:'checkmark.png'):resource(dir:'images',file:'checkmark-inactive.png')}" title="${message(code:step.is_done?'ads.detail.step.completed':'ads.detail.step.incompleted')}" alt="${message(code:step.is_done?'ads.detail.step.completed':'ads.detail.step.incompleted')}">
                            <p class="text">
                              <span class="${step.is_done?'text-completed':''}">
                                <g:link class="text-completed" controller="${step?.controller}" action="${step?.action}" id="${(step?.controller=='personal')?home?.id:''}" base="${context?.mainserverURL_lang}">${step['name'+context?.lang]}</g:link>
                              </span>
                              <span class="tip" id="txtadv_${step?.action}">${step.is_done?step['text_yes'+context?.lang].replace('[@PARAM]',advancedhomestep[0].param.toString()):step['text_no'+context?.lang].replace('[@PARAM]',advancedhomestep[0].param.toString())}</span>
                            </p>
                          </li>          
                        </g:each>
                        </ul>
                      </g:if>                      
                      </div>
                      
                      <a id="stat_link" style="display:none"></a>
                      <div id="stat_lbox" class="new-modal" style="width:600px;display:none">
                        <h2 class="clearfix">${message(code:'ads.detail.stat.view').capitalize()}</h2>
                        <div id="stat_segment_lbox" class="segment nopadding">
                          <div id="stat_container_lbox" class="lightbox_filter_container" style="height:auto;width:600px">
                            <div id="stat_text">
                              <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                            </div>
                            <div id="detailData"></div>
                          </div>
                        </div>
                      </div>

                      <div id="rating_details_lbox" class="new-modal" style="display:none">
                        <h2 class="clearfix">${message(code:'ads.detail.stat.rating').capitalize()} - ${home.rating} ${message(code:'ads.detail.rating.is')}</h2>
                        <div id="rating_details_lbox_segment" class="segment nopadding">
                          <div id="rating_details_lbox_container" class="lightbox_filter_container" style="height: auto;width: auto">
                            <ul>
                            <g:each in="${rating_info}">
                              <li class="todo-item">
                                <p class="text"><span class="tip">${it['name'+context?.lang]}: ${ratingDetail.(it.paramname)}</span></p>
                              </li>
                            </g:each>
                            </ul>
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
