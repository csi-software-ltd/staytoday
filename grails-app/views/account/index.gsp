<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main"/>    
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
      $('is_zayvka').onclick = function(){
        jQuery('#is_emailzayvka').parents('li').slideToggle(500);
      }
    }    
    </g:javascript>
  </head>
  <body onload="initialize()">
              <g:render template="/account_menu" />
                      <div style="padding:0px 20px">
                        <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                      </div><br/>
                      <h2 class="toggle border"><span class="edit_icon detail"></span>${message(code:'account.notifications')}</h2>
                      <g:form name="settinsForm" url="[action:'savenotesetting']" base="${context?.mainserverURL_lang}">
                        <table width="100%" cellpadding="5" cellspacing="0" border="0">
                          <tr>
                            <td width="35%" valign="top">
                              <div class="padd20">
                                <p><b>${infotext['promotext1'+context?.lang]?:''}</b><br/>
                                <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml></p>
                              </div>
                            </td>
                            <td>
                              <ul class="verifications-list">
                              <g:each in="${notegroup}" var="item" status="i">
                                <g:if test="${item?.template_id==1}">
                                <li class="verifications-list-item" <g:if test="${item.db_name=='is_emailzayvka'&&!user.is_zayvka}">style="display:none"</g:if>>
                                  <span class="label">
                                    <input <g:if test="${user.(item.db_name)}">checked="checked"</g:if> id="${item.db_name}" name="${item.db_name}" value="1" type="checkbox" />
                                    <label for="${item.db_name}"><g:rawHtml>${item['name'+context?.lang]}</g:rawHtml></label>
                                  <g:if test="${item?.help}">
                                    <a class="tooltip" href="javascript:void(0)" title="${item?.help}">
                                      <img alt="${item?.help}" src="${resource(dir:'images',file:'question.png')}" hspace="5" valign="bottom" border="0" />
                                    </a>
                                  </g:if>
                                  </span>
                                </li>
                                </g:if>
                              </g:each>
                              </ul>
                            </td>
                          </tr>
                          <tr>
                            <td valign="top" style="border-top: 1px solid #E7E7E7">
                              <div class="padd20">
                                <p><b>${infotext['promotext2'+context?.lang]?:''}</b><br/>
                                <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml></p>
                              </div>
                            </td>
                            <td style="border-top: 1px solid #E7E7E7">
                              <ul class="verifications-list">
                              <g:each in="${notegroup}" var="item" status="i">
                                <g:if test="${item?.template_id==2}">
                                <li class="verifications-list-item">
                                  <span class="label">
                                    <input <g:if test="${user.(item.db_name)}">checked="checked"</g:if> id="${item.db_name}" name="${item.db_name}" value="1" type="checkbox" />
                                    <label for="${item.db_name}"><g:rawHtml>${item['name'+context?.lang]}</g:rawHtml></label>
                                  <g:if test="${item?.help}">
                                    <a class="tooltip" href="javascript:void(0)" title="${item?.help}">
                                      <img alt="${item?.help}" src="${resource(dir:'images',file:'question.png')}" hspace="5" valign="bottom" border="0" />
                                    </a>                       
                                  </g:if>
                                  </span>
                                </li>
                                </g:if>
                              </g:each>                                                        
                              </ul>                              
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:20px">
                              <input type="submit" class="button-glossy orange" value="${message(code:'button.saveSetting')}"/>
                            </td>
                          </tr>
                        </table>
                      </g:form>
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
