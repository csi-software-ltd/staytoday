<html>
  <head>  
    <title>${infotext['title'+context?.lang]?:' '}</title>  
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />    
<g:if test="${err!=1}">
    <link rel="canonical" href="${context.curl}" />
    <meta name="layout" content="main" />
    <g:javascript>
      function updateExtFields(e){
        if (e.responseJSON.is_extfields){
          $('extfields').show();
          $('extfieldsadr').show();
        } else {
          $('extfields').hide();
          $('extfieldsadr').hide();
          $('tel_code').value='';
          $('tel').value='';
          $('adr').value='';
        }
      }
      function reloadCaptcha(){
        <g:remoteFunction controller='index' action='reloadCaptcha' onSuccess='processRelResponse(e)'/>
      }
      function processRelResponse(e){
        $('captcha_picture').innerHTML = e.responseJSON.captcha;
        $('captcha_picture').firstChild.setStyle({width: '120px'});
      }      
    </g:javascript>  
</g:if>
  </head>
  <body>
<g:if test="${err!=1}">
            <g:render template="/help_submenu" />             
</g:if>                                              
                      <g:if test="${(inrequest)&&(inrequest.error!=0)}">
                        <div class="notice">
                          <ul>
                            <g:if test="${inrequest?.error==2}"><li>${message(code:'error.blank',args:[message(code:'personal.email').capitalize()])} ${message(code:'faq.error.email')}</li></g:if>
                            <g:if test="${inrequest?.error==3}"><li>${message(code:'error.blank',args:[message(code:'faq.rectitle').capitalize()])}</li></g:if>
                            <g:if test="${inrequest?.error==11}"><li>${message(code:'error.blank',args:[message(code:'common.text_of_message').capitalize()])}</li></g:if>
                            <g:if test="${inrequest?.error==4}"><li>${message(code:'faq.error.incorrect',args:[message(code:'faq.how_to_contact').capitalize(),'150'])}</li></g:if>
                            <g:if test="${inrequest?.error==5}"><li>${message(code:'faq.error.incorrect',args:[message(code:'personal.email').capitalize(),'50'])}</li></g:if>
                            <g:if test="${inrequest?.error==6}"><li>${message(code:'faq.error.incorrect',args:[message(code:'ads.address').capitalize(),'250'])}</li></g:if>
                            <g:if test="${inrequest?.error==7}"><li>${message(code:'faq.error.incorrect',args:[message(code:'faq.phonecode').capitalize(),'50'])}</li></g:if>
                            <g:if test="${inrequest?.error==8}"><li>${message(code:'faq.error.incorrect',args:[message(code:'personal.phone').capitalize(),'50'])}</li></g:if>
                            <g:if test="${inrequest?.error==9}"><li>${message(code:'faq.error.incorrect',args:[message(code:'server.housing').capitalize(),'150'])}</li></g:if>
                            <g:if test="${inrequest?.error==10}"><li>${message(code:'faq.error.incorrect',args:[message(code:'faq.rectitle').capitalize(),'150'])}</li></g:if>
                            <g:if test="${inrequest?.error==99}"><li>${message(code:'add.error.incorrect.code')}</li></g:if>
                            <g:if test="${inrequest?.error==100}"><li>${message(code:'faq.error.limit')}</li></g:if>
                          </ul>
                          <g:if test="${inrequest?.error==9000}">
                            ${message(code:'faq.error.send')}: <a href="mailto:${mail_support}?body=${inrequest?.toString().replace('\n',' ').replace('\r',' ')}">${mail_support}</a>
                          </g:if>
                        </div>
                      </g:if>
                        <div class="padd20">
                          <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                        </div>
                        <g:formRemote name="sendForm" url="[controller:'help',action:'add']" method="POST" useToken="true" update="[success:'book']" onLoading="document.getElementById('loader').show();" onLoaded="document.getElementById('loader').hide();">
                          <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                            <tr>
                              <td colspan="3">
                                <label for="gbtype">${message(code:'faq.gbtype')}</label>
                                <select name="gbtype" id="gbtype" onchange="<g:remoteFunction action='selectgbtype' onSuccess='updateExtFields(e)' params="'gbtype_id='+this.value"/>">
                                <g:each in="${gbtypes}" var="type">
                                  <option value="${type.id}" <g:if test="${gbtype.id==type.id}">selected</g:if>>${type['name'+context?.lang]}</option>
                                </g:each>
                                </select>
                              </td>                                    
                            </tr>
                            <tr>
                              <td colspan="3" class="regfield">
                                <label for="fio">${message(code:'faq.how_to_contact')}?</label>
                                <input type="text" name="fio" value="${inrequest?.fio}" />
                              </td>
                            </tr>
                            <tr <g:if test="${!gbtype.is_extfields}">style="display:none"</g:if>>
                              <td colspan="3" class="regfield">
                                <label for="region" id="reg_label">${message(code:'ads.region')}</label>
                              </td>        
                            </tr>
                            <tr>
                              <td colspan="3" class="required">
                                <label for="email">${message(code:'personal.email')}</label>
                                <input type="text" name="email" value="${inrequest?.email}"/>
                              </td>
                            </tr>
                            <tr id="extfields" <g:if test="${!gbtype.is_extfields}">style="display:none"</g:if>>                          
                              <td colspan="3" class="regfield">
                                <label for="tel" id="tel_label">${message(code:'personal.phone')}</label>
                                <input type="text" name="tel_code" id="tel_code" value="${inrequest?.tel_code}" style="width:40px"/>
                                <input type="text" name="tel" id="tel" value="${inrequest?.tel}" style="width:150px"/>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="3" class="regfield">
                                <label for="home">${message(code:'server.housing')}</label>
                                <input type="text" name="home" value="${inrequest?.home}" />
                              </td>
                            </tr>
                            <tr id="extfieldsadr" <g:if test="${!gbtype.is_extfields}">style="display:none"</g:if>>
                              <td colspan="3" class="regfield">
                                <label for="adr" id="adr_label">${message(code:'ads.address')}</label>
                                <input type="text" name="adr" id="adr" value="${inrequest?.adr}"/>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="3" class="required">
                                <label for="rectitle">${message(code:'faq.rectitle')}</label>
                                <input type="text" name="rectitle" value="${inrequest?.rectitle}" />
                              </td>
                            </tr>
                            <tr>
                              <td colspan="3" class="required">
                                <label for="rectext">${message(code:'common.text_of_message')}</label>
                                <g:textArea name="rectext" rows="3" value="${inrequest?.rectext}" />
                              </td>
                            </tr>
                          <g:if test="${!user}">
                            <tr id="captcha_label" height="50">
                              <td class="padd20" width="175">${message(code:'login.captcha')}:</td>             
                              <td width="120" valign="middle" id="captcha_picture"><jcaptcha:jpeg name="image" width="120"/></td>
                              <td valign="middle">                        
                                <input id="captcha_text" type="text" name="captcha" value="" style="width: 140px;"/>
                                <img src="${resource(dir:'images',file:'reload.png')}" alt="${message(code:'captcha.update')}" title="${message(code:'captcha.update')}" onclick="reloadCaptcha();" border="0" align="absmiddle" style="margin-left:5px;"/>
                              </td>
                            </tr>
                          </g:if>
                            <tr>
                              <td colspan="3" style="padding:20px">
                                <input class="button-glossy orange" type="button" value="${message(code:'button.send')}" style="margin-right:5px" onclick="this.disabled=true;document.getElementById('proxyButton').click();"/>
                                <input id="proxyButton" class="button-glossy orange" type="submit" style="display:none"/>
                                <input class="button-glossy grey" type="reset" value="${message(code:'button.reset')}" />
                                <img id="loader" src="${resource(dir:'images',file:'spinner.gif')}" align="absmiddle" hspace="5" border="0" style="display:none">
                              </td>
                            </tr>
                          </table>
                        </g:formRemote>
<g:if test="${err!=1}">          
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
</g:if>    
  </body>
</html>
