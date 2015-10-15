<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>     
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main"/>
    <g:javascript>
    var iLim = ${textlimit}
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
      textCounter('name','name_limit',35);
      <g:if test="${save}">
        $('ho_all').show();		
        $('ho_kitchen').show();
        $('ho_bania').show();
        $('ho_kids').show();		
      </g:if>      
    }    
    function textCounter(sId,sLimId,iMax){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iMax){
        symbols = symbols.substring(0,iMax);
        $(sId).value = symbols;
        return false;
      }
      $(sLimId).value = iMax-len;
    }	
    function textLimit(sId){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iLim){
        symbols = symbols.substring(0,iLim);
        $(sId).value = symbols;
        return false;
      }
    }	
    function clearForm(){
      setTimeout("textCounter('name','name_limit',35)",100);
    }    
    function sendForm(){
      $("adsForm").submit();
    }
    </g:javascript>
    <style type="text/css">
    .form fieldset label { float:none!important; padding:0!important; line-height:17px!important; }
    .form fieldset ul.service li label { float:left!important; min-width:5px!important; padding:0 5px!important;width:85%;margin-bottom:5px}
    </style>
  </head>
  <body onload="initialize()">
              <g:render template="/personal_menu" />                        
                    <g:if test="${((flash?.error?:[]).size()>0) || ((flash?.save_error?:[]).size()>0)}">
                      <div class="notice" style="margin-top:10px">
                        <ul>
                        <g:each in="${flash?.error}"> 
                          <g:if test="${it==-1}"><li>${message(code:'ads.error.nofound')}</li></g:if>
                          <g:if test="${it==-2}"><li>${message(code:'ads.error.incorrectly.link')}</li></g:if>       
                        </g:each>
                        <g:each in="${flash?.save_error}"> 
                          <g:if test="${it==1}"><li>${message(code:'error.blank',args:[message(code:'common.home.name')])}</li></g:if>
                          <g:if test="${it==2}"><li>${message(code:'error.blank',args:[message(code:'common.home.desc')])}</li></g:if>
                          <g:if test="${it==3}"><li>${message(code:'error.incorrect',args:[message(code:'common.number_of_bed')])}</li></g:if>
                          <g:if test="${it==4}"><li>${message(code:'error.incorrect',args:[message(code:'ads.detail.size')])}</li></g:if>
                          <g:if test="${it==10}"><li>${message(code:'ads.error.handbooks')}</li></g:if>
                          <g:if test="${it==101}"><li>${message(code:'ads.error.nosave')}</li></g:if>
                        </g:each>	      
                        </ul>
                      </div>
                    </g:if>
                      <div class="form">
                        <g:form name="adsForm" url="${[action:'saveads']}" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                          <input type="hidden" name="id" value="${inrequest?.id?:0}"/>
                          <h2 class="toggle border"><span class="edit_icon"></span>${message(code:'ads.type')}</h2>
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">
                            <tr>
                              <td width="68%">
                                <label for="hometype_id">${message(code:'common.home_type')}</label>
                                <select id="hometype_id" name="hometype_id" class="mini">
                                <g:each in="${hometype}" var="item">            
                                  <option <g:if test="${item?.id==inrequest?.hometype_id}">selected="selected"</g:if> value="${item?.id}">
                                    ${item['name'+context?.lang]}
                                  </option>
                                </g:each>
                                </select>
                              </td>
                              <td valign="middle">
                                <label for="is_fiesta">
                                  <input type="checkbox" id="is_fiesta" name="is_fiesta" <g:if test="${inrequest?.is_fiesta}">checked</g:if> value="1"/>
                                  ${message(code:'ads.party.allowed')}
                                </label>                                
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="homeperson_id">${message(code:'common.homeperson')}</label>		
                                <select id="homeperson_id" name="homeperson_id" class="mini">
                                <g:each in="${homeperson}" var="item">            
                                  <option <g:if test="${item?.id==inrequest?.homeperson_id}">selected="selected"</g:if> value="${item?.id}">
                                    ${item['name'+context?.lang]}
                                  </option>
                                </g:each>
                                </select>
                                <a class="tooltip" href="javascript:void(0)" title="${message(code:'common.homeperson.alt')}">
                                  <img alt="${message(code:'common.homeperson.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
                                </a>
                              </td>
                              <td valign="middle">
                                <label for="is_renthour">
                                  <input type="checkbox" id="is_renthour" name="is_renthour" <g:if test="${inrequest?.is_renthour}">checked</g:if> value="1"/>
                                  ${message(code:'profile.edit.hours')}
                                </label>                                
                              </td>
                            </tr>
                          </table><br/><br/>
                          <h2 class="toggle border"><span class="edit_icon detail"></span>${message(code:'ads.desc')}</h2>
                          <table width="100%" cellpadding="3" cellspacing="0" border="0">
                            <tr>
                              <td>
                                <label for="name">${message(code:'common.home.name')}</label>		
                                <input type="text" id="name" name="name" <g:if test="${(flash?.save_error?:[]).contains(1)}">class="red"</g:if> size="35" value="${inrequest?.name}" onkeydown="textCounter(this.id,'name_limit',35);" onkeyup="textCounter(this.id,'name_limit',35);">
                                <span class="padd10">${message(code:'ads.characters.left')} <input type="text" class="limit" id="name_limit" name="name_limit" readonly /></span>  
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="description">${message(code:'common.home.desc')}</label>		
                                <g:textArea id="description" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" class="${(flash?.save_error?:[]).contains(2)?'red':''}" name="description" value="${inrequest?.description}" rows="5" cols="40" />
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="homeroom_id">${message(code:'common.number_of_rooms')}</label>		
                                <select id="homeroom_id" name="homeroom_id" class="mini">
                                <g:each in="${homeroom}" var="item">            
                                  <option <g:if test="${item?.id==inrequest?.homeroom_id}">selected="selected"</g:if> value="${item?.id}">
                                    ${item['name'+context?.lang]}
                                  </option>
                                </g:each>
                                </select>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="homebath_id">${message(code:'common.number_of_bath')}</label>		
                                <select id="homebath_id" name="homebath_id" class="mini">
                                <g:each in="${homebath}" var="item">            
                                  <option <g:if test="${item?.id==inrequest?.homebath_id}">selected="selected"</g:if> value="${item?.id}">
                                    ${item['name'+context?.lang]}
                                  </option>
                                </g:each>
                                </select>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="bed">${message(code:'common.number_of_bed')}</label>		
                                <input type="text" id="bed" name="bed" class="mini <g:if test="${(flash?.save_error?:[]).contains(3)}">red</g:if>" value="${inrequest?.bed?:''}">          
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="area">${message(code:'ads.size')}</label>		
                                <input type="text" id="area" name="area" class="mini <g:if test="${(flash?.save_error?:[]).contains(4)}">red</g:if>" value="${inrequest?.area?:''}"/>
                                <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.size.alt')}">
                                  <img alt="${message(code:'ads.size.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0"/>
                                </a>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <label for="remarks">${message(code:'ads.remarks')}</label>		
                                <g:textArea id="remarks" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" name="remarks" value="${inrequest?.remarks}" rows="5" cols="40"/>
                              </td>
                            </tr>
                          </table><br/><br/>
                          <fieldset>
                            <legend><h2 class="toggle"><span class="edit_icon services"></span>${message(code:'ads.services')}</h2></legend>
                            <ul class="service">
                            <g:each in="${homeoption}" var="item" status="i"><g:if test="${i < homeoption.size()/2}">
                              <li>
                                <input type="checkbox" id="${item.fieldname?:'homeoption'+i}" name="${item.fieldname?:'homeoption'+i}" <g:if test="${item.fieldname}"><g:if test="${inrequest[item.fieldname]}">checked</g:if></g:if> value="1" />
                                <label for="${item.fieldname?:'homeoption'+i}">${item['name'+context?.lang]} <g:if test="${item['comments'+context?.lang]}"><a class="tooltip" href="javascript:void(0)" title="${item['comments'+context?.lang]}">
                                  <img alt="${item['comments'+context?.lang]}" src="${resource(dir:'images',file:'question.png')}" hspace="5" border="0" />
                                </a></g:if></label>
                              </li>                    
                            </g:if></g:each>
                            </ul>
                            <ul class="service">
                            <g:each in="${homeoption}" var="item" status="i"><g:if test="${i >= homeoption.size()/2}">
                              <li nowrap>
                                <input type="checkbox" id="${item.fieldname?:'homeoption'+i}" name="${item.fieldname?:'homeoption'+i}" <g:if test="${item.fieldname}"><g:if test="${inrequest[item.fieldname]}">checked</g:if></g:if> value="1" />
                                <label for="${item.fieldname?:'homeoption'+i}">${item['name'+context?.lang]} <g:if test="${item['comments'+context?.lang]}"><a class="tooltip" href="javascript:void(0)" title="${item['comments'+context?.lang]}">
                                  <img alt="${item['comments'+context?.lang]}" src="${resource(dir:'images',file:'question.png')}" hspace="5" border="0" />
                                </a></g:if></label>
                              </li>
                            </g:if></g:each>
                            </ul>
                          </fieldset><br/><br/>
                          <fieldset>
                            <legend><h2 class="toggle"><span class="edit_icon additional"></span>${message(code:'ads.services.additional')}</h2></legend> 
                            <ul class="nowrap">
                              <li><a onclick="$('ho_all').show();$('ho_kitchen').hide();$('ho_bania').hide();$('ho_kids').hide();">${message(code:'ads.services.additional.all')}</a></li>
                              <li><a onclick="$('ho_all').hide();$('ho_kitchen').show();$('ho_bania').hide();$('ho_kids').hide();">${message(code:'ads.services.additional.kitchen')}</a></li> 
                              <li><a onclick="$('ho_all').hide();$('ho_kitchen').hide();$('ho_bania').show();$('ho_kids').hide();">${message(code:'ads.services.additional.bania')}</a></li>
                              <li><a onclick="$('ho_all').hide();$('ho_kitchen').hide();$('ho_bania').hide();$('ho_kids').show();">${message(code:'ads.services.additional.kids')}</a></li>                    
                            </ul>
                            <fieldset id="ho_all" style="display:none;clear:left">
                              <legend>${message(code:'ads.services.additional.all')}</legend>                       
                              <ul style="float:left;width:45%">
                              <g:each in="${homeoption_all}" var="item" status="i">
                                <g:if test="${i < homeoption_all.size()/2}">
                                <li>
                                  <input type="checkbox" id="homeoption_all${i}" name="homeoption_all${i}" <g:if test="${(inrequest?.homeoption?:'').find((item?.id?:0).toString())}">checked</g:if> value="1"/>
                                  <label for="homeoption_all${i}">${item['name'+context?.lang]}</label>                     
                                </li>
                                </g:if>
                              </g:each>
                              </ul>
                              <ul style="float:right;width:55%">
                              <g:each in="${homeoption_all}" var="item" status="i">
                                <g:if test="${i >= homeoption_all.size()/2}">
                                <li>
                                  <input type="checkbox" id="homeoption_all${i}" name="homeoption_all${i}" <g:if test="${(inrequest?.homeoption?:'').find((item?.id?:0).toString())}">checked</g:if> value="1"/>                        
                                  <label for="homeoption_all${i}">${item['name'+context?.lang]}</label>                     
                                </li>
                                </g:if>
                              </g:each>
                              </ul>          
                            </fieldset>                    
                            <fieldset id="ho_kitchen" style="display:none;clear:both">          		
                              <legend>${message(code:'ads.services.additional.kitchen')}</legend>                     
                              <ul style="float:left;width:45%">
                              <g:each in="${homeoption_kitchen}" var="item" status="i"> 
                                <g:if test="${i < homeoption_kitchen.size()/2}">
                                <li>
                                  <input type="checkbox" id="homeoption_kitchen${i}" name="homeoption_kitchen${i}" <g:if test="${(inrequest?.homeoption?:'').find((item?.id?:0).toString())}">checked</g:if> value="1"/>
                                  <label for="homeoption_kitchen${i}">${item['name'+context?.lang]}</label>
                                </li>
                                </g:if>
                              </g:each>
                              </ul>
                              <ul style="float:right;width:55%">
                              <g:each in="${homeoption_kitchen}" var="item" status="i"> 
                                <g:if test="${i >= homeoption_kitchen.size()/2}">
                                <li>
                                  <input type="checkbox" id="homeoption_kitchen${i}" name="homeoption_kitchen${i}" <g:if test="${(inrequest?.homeoption?:'').find((item?.id?:0).toString())}">checked</g:if> value="1"/>
                                  <label for="homeoption_kitchen${i}">${item['name'+context?.lang]}</label>
                                </li>
                                </g:if>
                              </g:each>
                              </ul>         
                            </fieldset>                    
                            <fieldset id="ho_bania" style="display:none;clear:both">          		            
                              <legend>${message(code:'ads.services.additional.bania')}</legend>
                              <ul style="float:left;width:45%">
                              <g:each in="${homeoption_bania}" var="item" status="i"> 
                                <g:if test="${i < homeoption_bania.size()/2}">
                                <li>
                                  <input type="checkbox" id="homeoption_bania${i}" name="homeoption_bania${i}" <g:if test="${(inrequest?.homeoption?:'').find((item?.id?:0).toString())}">checked</g:if> value="1"/>
                                  <label for="homeoption_bania${i}">${item['name'+context?.lang]}</label>
                                </li>
                                </g:if>
                              </g:each>
                              </ul>
                              <ul style="float:right;width:55%">
                              <g:each in="${homeoption_bania}" var="item" status="i"> 
                                <g:if test="${i >= homeoption_bania.size()/2}">
                                <li>
                                  <input type="checkbox" id="homeoption_bania${i}" name="homeoption_bania${i}" <g:if test="${(inrequest?.homeoption?:'').find((item?.id?:0).toString())}">checked</g:if> value="1"/>
                                  <label for="homeoption_bania${i}">${item['name'+context?.lang]}</label>
                                </li>
                                </g:if>
                              </g:each>
                              </ul>         
                            </fieldset>                    
                            <fieldset id="ho_kids" style="display:none;clear:both">         
                              <legend>${message(code:'ads.services.additional.kids')}</legend>                    
                              <ul style="float:left;width:45%">
                              <g:each in="${homeoption_kids}" var="item" status="i"> 
                                <g:if test="${i < homeoption_kids.size()/2}">
                                <li>
                                  <input type="checkbox" id="homeoption_kids${i}" name="homeoption_kids${i}" <g:if test="${(inrequest?.homeoption?:'').find((item?.id?:0).toString())}">checked</g:if> value="1"/>
                                  <label for="homeoption_kids${i}">${item['name'+context?.lang]}</label>
                                </li>
                                </g:if>
                              </g:each>
                              </ul>
                              <ul style="float:right;width:55%">
                              <g:each in="${homeoption_kids}" var="item" status="i"> 
                                <g:if test="${i >= homeoption_kids.size()/2}">
                                <li>
                                  <input type="checkbox" id="homeoption_kids${i}" name="homeoption_kids${i}" <g:if test="${(inrequest?.homeoption?:'').find((item?.id?:0).toString())}">checked</g:if> value="1"/>
                                  <label for="homeoption_kids${i}">${item['name'+context?.lang]}</label>
                                </li>
                                </g:if>
                              </g:each>
                              </ul>          
                            </fieldset>                   
                          </fieldset>
                          <div style="padding:45px 20px">
                            <span class="actions col">
                              <span class="action_button orange">
                                <g:link class="icon none" target="_blank" controller="personal" action="adsoverview" id="${home?.id}" base="${context.sequreServerURL}">${Infotext.findByControllerAndAction('personal','adsoverview')['name'+context?.lang]}</g:link>
                              </span>                
                            </span>
                            <div style="display:inline-block">
                              <input type="button" class="button-glossy orange" value="${message(code:'button.save')}" onclick="sendForm()"/>
                              <input type="reset" class="button-glossy grey" value="${message(code:'button.reset')}" onClick="clearForm()"/>
                            </div>
                          </div>
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
