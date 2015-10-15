<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />   
    <meta name="layout" content="main" />  
  </head>
  <body> 
              <g:render template="/ads_menu" />
                      <div style="padding:0px 20px">
                        <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>
                      </div><br/>
                        <h2 class="toggle border"><span class="edit_icon detail"></span>${message(code:'booking.settings')}</h2>
                        <g:form name="settingsForm" url="[action:'savereqsetting']" base="${context?.mainserverURL_lang}">
                          <table width="100%" cellpadding="5" cellspacing="0" border="0">
                          <g:each in="${requirements}" var="item" status="i">        
                            <tr>
                              <td width="45%" valign="top" style="${(i>0)?'border-top: 1px solid #E7E7E7':''}">
                                <div class="padd20">
                                  <p><b>${item['name'+context?.lang]}</b><br/>
                                  <g:rawHtml>${item['comments'+context?.lang]}</g:rawHtml></p>
                                </div>
                              </td>
                              <td style="${(i>0)?'border-top: 1px solid #E7E7E7':''}">
                                <ul class="verifications-list">
                                  <li class="verifications-list-item">
                                    <span class="label">
                                      <input <g:if test="${user[item?.paramname]}">checked="checked"</g:if> id="${item?.paramname}" name="${item?.paramname}" value="1" type="checkbox"/>
                                      <label for="${item?.paramname}">${item['fullname'+context?.lang]}</label>
                                    </span>
                                  </li>
                                </ul>
                              </td>
                            </tr>
                          </g:each>        
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
