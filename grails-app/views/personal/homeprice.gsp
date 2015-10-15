<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" /> 
    <meta name="layout" content="main" />
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/>
    <g:javascript>  
    var iLim = ${textlimit}
    function textLimit(sId){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iLim){
        symbols = symbols.substring(0,iLim);
        $(sId).value = symbols;
        return false;
      }
    }
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
	  <g:if test="${!home?.pricestatus}">	    
      $('priceFormDescription').show();
	  </g:if><g:else>
	    $('priceFormTable').show();		
	  </g:else>
	  <g:if test="${((home?.pricestatus?:0)==1)}">
	    $('priceFormCancel').show();
	  </g:if>      
    }
    function viewWeekendPrice(name,viewname){
      if($(name).checked)
        $(viewname).show();		 
      else
        $(viewname).hide();
    } 	        
    function turnoffDisc(iId){
      <g:remoteFunction controller='personal' id="${home.id}" action='discountdisable' onSuccess='disableResponse(e,iId)' params="'type='+iId" base="${context.sequreServerURL}"/>
    }
    function discountResponse(e,iId){
      if(!e.responseJSON.error){
        document.getElementById('errors'+(iId==1?'Long':'Hot')).hide();
        document.getElementById('disableDiscButton'+(iId==1?'Long':'Hot')).style.display = 'inline';
        document.getElementById('saveDiscButton'+(iId==1?'Long':'Hot')).value = "${message(code:'button.save')}";
      } else {
        document.getElementById('errors'+(iId==1?'Long':'Hot')).show();
      }
    }
    function disableResponse(e,iId){
      if(!e.responseJSON.error){
        document.getElementById('disableDiscButton'+(iId==1?'Long':'Hot')).style.display = 'none';
        document.getElementById('saveDiscButton'+(iId==1?'Long':'Hot')).value = "${message(code:'button.switchon')}";
      }
    }
    function resetPrice(){
      $("status_only").value=1;
      $('pricestatus').value=0;
      $("priceFormSubmit").click(function() {        
      });
    }
    </g:javascript>
    <style type="text/css">
      .form label.mini { min-width: 100px !important; }
      .form label.nopadd { min-width: 95px !important; padding: 0 20px 0 0 !important }
      .form select.dmini { width: auto !important }
      .action_button.btn { padding-right: 5px !important; margin-right: 2px !important }
    </style>
  </head>  
  <body onload="initialize();">
              <g:render template="/personal_menu" />
                          <div rel="layer">                                   
                          <g:if test="${(flash?.error?:[]).size()>0}">
                            <div class="notice" style="margin-top:5px">
                              <ul>
                              <g:each in="${flash?.error}"> 
                                <g:if test="${it==1}"><li>${message(code:'error.blank',args:[message(code:'ads.price.price_per_day')])}</li></g:if>
                                <g:if test="${it==2}"><li>${message(code:'ads.error.price',args:[message(code:'ads.price.price_per_day'),price_min])}</li></g:if>
                                <g:if test="${it==3}"><li>${message(code:'ads.error.price',args:[message(code:'ads.price.weekend'),price_min])}</li></g:if>
                                <g:if test="${it==4}"><li>${message(code:'ads.error.price.incorrect',args:[message(code:'ads.price.price_per_week'),message(code:'ads.price.price_per_day')])}</li></g:if>
                                <g:if test="${it==5}"><li>${message(code:'ads.error.price.incorrect',args:[message(code:'ads.price.price_per_month'),message(code:'ads.price.price_per_day')])}</li></g:if>
                              </g:each>	
                              </ul>
                            </div>
                          </g:if>
                          <g:if test="${(flash?.save_error?:[]).size()>0}">
                            <div class="notice">
                              <ul>
                              <g:each in="${flash?.save_error}"> 
                                <g:if test="${it==1}"><li>${message(code:'ads.error.price.period.nofound')}</li></g:if>
                                <g:if test="${it==2}"><li>${message(code:'ads.error.price.regular_and_period.nofound')}</li></g:if>
                              </g:each>	
                              </ul>
                            </div>
                          </g:if>   
                            <div style="background:#fff"> 
                              <h2 class="toggle border"><span class="edit_icon ask"></span>${message(code:'ads.price.valuta').capitalize()}</h2>
                            <g:if test="${(flash.valuta_change_error?:0)==1}">
                              <div class="notice" id="valuta_error">
                                ${message(code:'ads.price.valuta.info')}
                              </div>
                            </g:if>
                              <g:form name="valutaForm" url="${[action:'changeValuta',controller:'personal',params:[home_id:home?.id?:0]]}" base="${context.sequreServerURL}">                        
                                <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                                  <tr>
                                    <td>
                                      <label for="valuta_id" class="mini">${message(code:'ads.price.valuta')}</label>
                                      <select class="mini" id="valuta_id" name="valuta_id" onchange="$('valutaForm').submit();">
                                      <g:each in="${valuta}" var="item">            
                                        <option <g:if test="${item?.id==home?.valuta_id?:0}">selected="selected"</g:if> value="${item?.id}">
                                          ${item?.code}
                                        </option>
                                      </g:each>
                                      </select>
                                      <span><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>
                                      <input type="hidden" name="is_home" value="1"/>
                                      <input type="hidden" name="is_homeprop" value="1"/>
                                    </td>                              
                                  </tr>
                                </table>
                              </g:form><br/><br/>
                              <h2 class="toggle border"><span class="edit_icon price"></span>${message(code:'ads.price.regular')}</h2>
                            <g:if test="${infotext['itext'+context?.lang]}">
                              <div class="padd20">
                                <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>
                              </div>
                            </g:if>
                              <div class="padd20" id="priceFormDescription" style="display:none">
                                ${message(code:'ads.price.regular.info')}
                              </div>
           
                              <g:form name="priceForm" url="${[action:'addprice', id:home?.id?:0]}" method="post" useToken="true" base="${context.sequreServerURL}"> 	
                                <table class="form" id="priceFormTable" width="100%" cellpadding="3" cellspacing="0" border="0" style="display:none;">      
                                  <tr>
                                    <td width="50%">
                                      <label for="pricestandard" class="mini">${message(code:'ads.price.price_per_day')}</label>
                                      <input type="text" class="price <g:if test="${(flash?.error?:[]).contains(1)||(flash?.error?:[]).contains(2)}">red</g:if>" id="pricestandard" name="pricestandard" value="${inrequest?.pricestandard?:0}"/>
                                      <span class="currency"><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>
                                    </td>
                                    <td>                                            
                                      <label for="is_weekend" class="mini"><input type="checkbox" id="is_weekend" name="is_weekend" value="" <g:if test="${(inrequest?.priceweekend?:0)>0}">checked</g:if> onclick="viewWeekendPrice(this,'price_weekend');">${message(code:'ads.price.weekend')}</label>
                                      <span id="price_weekend" style="<g:if test="${(inrequest?.priceweekend?:0)==0}">display:none</g:if>">
                                        <input type="text" class="price <g:if test="${(flash?.error?:[]).contains(3)}">red</g:if>" id="priceweekend" name="priceweekend" value="${inrequest?.priceweekend}">
                                        <span class="currency"><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>            
                                      </span>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="2">
                                      <label for="priceweek" class="mini">${message(code:'ads.price.price_per_week')}</label>
                                      <input type="text" class="price <g:if test="${(flash?.error?:[]).contains(4)}">red</g:if>" id="priceweek" name="priceweek" value="${inrequest?.priceweek?:0}">
                                      <span class="currency"><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>
                                      <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.price.price_per_week.alt')}">
                                        <img alt="${message(code:'ads.price.price_per_week.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="bottom" border="0"/>
                                      </a>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="2">
                                      <label for="pricemonth" class="mini">${message(code:'ads.price.price_per_month')}</label>
                                      <input type="text" class="price <g:if test="${(flash?.error?:[]).contains(5)}">red</g:if>" id="pricemonth" name="pricemonth" value="${inrequest?.pricemonth?:0}">
                                      <span class="currency"><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>
                                      <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.price.price_per_month.alt')}">
                                        <img alt="${message(code:'ads.price.price_per_month.alt')}" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="bottom" border="0"/>
                                      </a>
                                    </td>
                                  </tr>
                                </table>
                                <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                                  <tr>
                                    <td colspan="2" class="padd20 paddtop">
                                      <input type="submit" class="button-glossy btn orange" id="priceFormSubmit" value="${message(code:((home?.pricestatus?:0)==1)?'button.save':(((home?.pricestatus?:0)==2)?'button.switchon':'button.tune'))}">          
                                      <input type="button" style="display:none" class="button-glossy btn grey" id="priceFormCancel" value="${message(code:'ads.price.regular.cancel')}" onclick="resetPrice();">			
                                    </td>
                                  </tr>
                                </table>
                                <input type="hidden" id="pricestatus" name="pricestatus" value="${((home?.pricestatus?:0)==2)?1:(((home?.pricestatus?:0)==0)?2:home?.pricestatus)}"/>	  
                                <input type="hidden" id="status_only" name="status_only" value="${!home?.pricestatus?1:0}"/>	  
                              </g:form><br/><br/>
                              
                              <h2 class="toggle border"><span class="edit_icon inactive"></span>${message(code:'ads.price.period')}</h2>
                            <g:if test="${homepropinactive}">
                              <g:if test="${infotext['itext2'+context?.lang]}">
                              <div class="padd20">
                                <g:rawHtml>${infotext['itext2'+context?.lang]}</g:rawHtml>
                              </div>
                              </g:if>
                    
                              <table width="100%" class="dotted" cellpadding="0" cellspacing="0" rules="all" frame="none">
                                <tr>
                                  <th width="85">${message(code:'ads.price.date_start')}</th>
                                  <th width="105">${message(code:'ads.price.date_end')}</th>
                                  <th width="193">${message(code:'ads.price.comment')}</th>
                                  <th>${message(code:'ads.price.action')}</th>
                                </tr>
                              <g:each in="${homepropinactive}">
                                <tr>
                                  <td><g:formatDate format="dd.MM.yyyy" date="${it?.date_start}"/></td>
                                  <td><g:formatDate format="dd.MM.yyyy" date="${it?.date_end}"/></td>
                                  <td>${it?.remark}</td>
                                  <td>
                                    <div class="actions">
                                      <span class="action_button btn orange">
                                        <g:link class="icon edit" controller="personal" action="homeprop" params="${[id:it?.id?:0,active:0]}" base="${context.sequreServerURL}">${message(code:'button.change')}</g:link>                      
                                      </span>
                                      <span class="action_button btn orange">
                                        <g:link class="icon delete" controller="personal" action="homeprop_delete" id="${it?.id?:0}" base="${context.sequreServerURL}">${message(code:'button.delete')}</g:link>                      
                                      </span>
                                    </div>   
                                  </td>
                                </tr>
                              </g:each>
                              </table>            
                            </g:if>
                              <div style="margin:10px 20px">
                                <g:link class="button-glossy btn orange" controller="personal" action="homeprop" params="${[home_id:home?.id?:0]}" base="${context.sequreServerURL}">${message(code:'button.add')}</g:link>
                              </div><br/><br/>
                              
                              <h2 class="toggle border"><span class="edit_icon period"></span>${message(code:'ads.price.period.setting')}</h2>
                            <g:if test="${homeprop}">
                              <g:if test="${infotext['itext3'+context?.lang]}">
                              <div class="padd20">
                                <g:rawHtml>${infotext['itext3'+context?.lang]}</g:rawHtml>
                              </div>
                              </g:if>
                              <table width="100%" class="dotted" cellpadding="0" cellspacing="0" rules="all" frame="none">
                                <tr>
                                  <th width="60">${message(code:'ads.price.date_start')}</th>
                                  <th width="60">${message(code:'ads.price.date_end')}</th>
                                  <th width="60">${message(code:'ads.price.daily').capitalize()}</th>
                                  <th width="60">${message(code:'ads.price.weekend').capitalize()}</th>                
                                  <th>${message(code:'ads.price.action')}</th>
                                </tr>
                              <g:each in="${homeprop}" var="item" status="i">            
                                <tr>
                                  <td><g:formatDate format="dd.MM.yyyy" date="${item?.date_start}"/></td>
                                  <td><g:formatDate format="dd.MM.yyyy" date="${item?.date_end}"/></td>
                                  <td class="currency">${item?.price?:0} <g:rawHtml>${homepropValuta[i]?.symbol?:''}</g:rawHtml></td>
                                  <td class="currency">${item?.priceweekend?:item?.price} <g:rawHtml>${homepropValuta[i]?.symbol?:''}</g:rawHtml></td>								                
                                  <td>
                                    <div class="actions" nowrap>
                                    <g:if test="${((item.modstatus?:0)==2)}">
                                      <span class="action_button btn orange">
                                        <g:link class="icon active" controller="personal" action="homepropactivate" params="${[id:item?.id?:0]}" base="${context.sequreServerURL}">${message(code:'button.switchon')}</g:link>
                                      </span>
                                    </g:if>
                                      <span class="action_button btn orange">
                                        <g:link class="icon edit" controller="personal" action="homeprop" params="${[id:item?.id?:0,active:1]}" base="${context.sequreServerURL}">${message(code:'button.change')}</g:link>
                                      </span>
                                      <span class="action_button btn orange">
                                        <g:link class="icon delete" controller="personal" action="homeprop_delete" id="${item?.id?:0}" base="${context.sequreServerURL}">${message(code:'button.delete')}</g:link>
                                      </span>
                                    </div>
                                  </td>
                                </tr>
                              </g:each>              
                              </table>
                            </g:if>
                              <div style="margin:10px 20px">                      
                                <g:link class="button-glossy orange" controller="personal" action="homeprop" params="${[home_id:home?.id?:0,active:1]}" base="${context.sequreServerURL}">${message(code:'button.add')}</g:link>
                              </div><br/><br/>
                              <div style="display:${home?.pricestatus==1?'block':'none'}">
                                <h2 class="toggle border"><span class="edit_icon price"></span>${message(code:'common.discounts').capitalize()}</h2>
                                <g:formRemote class="form" name="homeDiscountForm1" url="[action:'homediscount', id:home?.id?:0]" method="post" onSuccess="discountResponse(e,1)" onLoading="\$('saveDiscountProcessLoader').show();" onLoaded="\$('saveDiscountProcessLoader').hide();">
                                  <fieldset>
                                    <legend><b>${message(code:'common.discounts_for_long_offers')}</b></legend>
                                    <div id="errorsLong" class="notice" style="margin:0px;display: none">
                                      <span id="calculateMailPriceErrorText">${message(code:'home.discount.add.errortext')}</span>
                                    </div>
                                    <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                                      <tr>
                                        <td nowrap>
                                          <label for="discount" class="nopadd">${message(code:'ads.price.discount')}</label> 
                                          <g:select class="dmini" name="discount" from="${discountpercent}" noSelection='["0":"${message(code:'ads.price.discount.select')}"]' value="${longdiscount?.discount?:0}" optionKey="percent" optionValue="name" />
                                        </td>
                                        <td nowrap>
                                          <label for="discexpiredays">${message(code:'ads.price.longdiscount.discexpiredays')}</label> 
                                          <g:select class="dmini" name="discexpiredays" from="${Timetodecide.findAllByType(1)}" value="${longdiscount?.discexpiredays?:0}" optionKey="days" optionValue="${'name2'+context?.lang}" />
                                        </td>
                                      </tr>
                                      <tr>
                                        <td colspan="2" nowrap>
                                          <label for="minrentdays" class="nopadd">${message(code:'ads.price.discount.minrentdays')}</label>
                                          <g:select name="minrentdays" from="${Timetodecide.findAllByType(2)}" value="${longdiscount?.minrentdays?:0}" optionKey="days" optionValue="${'name2'+context?.lang}" noSelection='["0":"${message(code:'ads.price.discount.anytime')}"]' style="width:110px !important"/>
                                        </td>                                        
                                      </tr>
                                      <tr>
                                        <td colspan="2">
                                          <label for="terms" class="nopadd">${message(code:'ads.price.discount.terms')}</label>
                                          <input type="text" id="terms" name="terms" value="${longdiscount?.terms?:''}" maxlength="250" style="width:492px !important"/>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td colspan="2" style="padding:20px 0 0 0">
                                          <input id="saveDiscButtonLong" class="button-glossy orange" type="submit" value="${message(code:!longdiscount?.modstatus?'button.switchon':'button.save')}" style="margin-right:5px" />
                                          <input id="disableDiscButtonLong" class="button-glossy grey" type="button" value="${message(code:'button.switchoff')}" onclick="turnoffDisc(1);" style="display:${longdiscount?.modstatus?'inline':'none'}">
                                          <span id="saveDiscountProcessLoader" style="display:none"><img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/></span>
                                        </td>
                                      </tr>
                                    </table>
                                    <input type="hidden" id="discounttype" name="discounttype" value="1"/>
                                  </fieldset>
                                </g:formRemote><br/>
                                <g:formRemote name="homeDiscountForm2" url="[action:'homediscount', id:home?.id?:0]" method="post" onSuccess="discountResponse(e,2)" onLoading="\$('saveHotDiscountProcessLoader').show();" onLoaded="\$('saveHotDiscountProcessLoader').hide();">
                                  <fieldset>
                                    <legend><b>${message(code:'common.discounts_for_hot_offers')}</b></legend>
                                    <div id="errorsHot" class="notice" style="margin:0px;display: none">
                                      <span id="calculateMailPriceErrorText">${message(code:'home.discount.add.errortext')}</span>
                                    </div>
                                    <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                                      <tr>
                                        <td nowrap>
                                          <label for="discount" class="nopadd">${message(code:'ads.price.discount')}</label>
                                          <g:select class="dmini" name="discount" from="${discountpercent}" noSelection='["0":"${message(code:'ads.price.discount.select')}"]' value="${hotdiscount?.discount?:0}" optionKey="percent" optionValue="name" />
                                        </td>
                                        <td nowrap>
                                          <label for="discexpiredays">${message(code:'ads.price.hotdiscount.discexpiredays')}</label>
                                          <g:select class="dmini" name="discexpiredays" from="${Timetodecide.findAllByType(2)}" value="${hotdiscount?.discexpiredays?:0}" optionKey="days" optionValue="${'name2'+context?.lang}" />
                                        </td>
                                      </tr>
                                      <tr>
                                        <td colspan="2" nowrap>
                                          <label for="minrentdays" class="nopadd">${message(code:'ads.price.discount.minrentdays')}</label> 
                                          <g:select name="minrentdays" from="${Timetodecide.findAllByType(2)}" value="${hotdiscount?.minrentdays?:0}" optionKey="days" optionValue="${'name2'+context?.lang}" noSelection='["0":"${message(code:'ads.price.discount.anytime')}"]' style="width:110px !important" />
                                        </td>
                                      </tr>
                                      <tr>
                                        <td colspan="2">
                                          <label for="terms" class="nopadd">${message(code:'ads.price.discount.terms')}</label>
                                          <input type="text" id="terms" name="terms" value="${hotdiscount?.terms?:''}" maxlength="250" style="width:492px !important" />
                                        </td>
                                      </tr>
                                      <tr>
                                        <td colspan="2" style="padding:20px 0 0 0">
                                          <input id="saveDiscButtonHot" class="button-glossy orange" type="submit" value="${message(code:!hotdiscount?.modstatus?'button.switchon':'button.save')}" style="margin-right:5px" />
                                          <input id="disableDiscButtonHot" class="button-glossy grey" type="button" value="${message(code:'button.switchoff')}" onclick="turnoffDisc(2);" style="display:${hotdiscount?.modstatus?'inline':'none'}" />
                                          <span id="saveHotDiscountProcessLoader" style="display:none"><img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/></span>
                                        </td>
                                      </tr>
                                    </table>
                                    <input type="hidden" id="discounttype" name="discounttype" value="2"/>
                                  </fieldset>
                                </g:formRemote>
                              </div><br/>
                              <h2 class="toggle border"><span class="edit_icon detail"></span>${message(code:'ads.price.conditions')}</h2>
                              <g:form name="homeRuleForm" url="${[action:'homerule', id:home?.id?:0]}" method="post" useToken="true" base="${context.sequreServerURL}"> 	 
                                <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">        	
                                  <tr>
                                    <td>
                                      <label for="cancellation_id">${message(code:'ads.price.cancellation')}</label>
                                      <select id="cancellation_id" name="rule_cancellation_id">
                                      <g:each in="${cancellation}" var="item">            
                                        <option onclick="$('cancellation').update('${item?.fullname}');$('cancellation').parentElement.hash='${item?.shortlink}';" 
                                          <g:if test="${item?.id==home?.rule_cancellation_id}">selected="selected"</g:if> value="${item?.id}">
                                          ${item['name'+context?.lang]}
                                        </option>
                                      </g:each>
                                      </select>          
                                    </td>
                                  </tr>
                                  <tr>
                                    <td class="padd20">
                                      <p>
                                        <g:link class="tooltip" controller="home" action="cancellation" fragment="${cur_cancellation?.shortlink}" title="${message(code:'ads.price.more')}" base="${context.sequreServerURL}">
                                          <span id="cancellation">${cur_cancellation['fullname'+context?.lang]?:''}</span>
                                        </g:link>                
                                        <a class="tooltip" href="javascript:void(0)" title="${message(code:'ads.price.more')}">
                                          <img src="${resource(dir:'images',file:'question.png')}" alt="${message(code:'ads.price.more')}" hspace="5" valign="bottom" border="0"/>
                                        </a>
                                      </p>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td>
                                      <label for="deposit">${message(code:'ads.price.deposit')}</label>
                                      <input type="text" class="price" id="deposit" name="deposit" value="${home?.deposit?:''}">
                                      <span class="currency"><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td nowrap>
                                      <label for="fee">${message(code:'ads.price.fee')}</label>
                                      <input type="text" class="price" id="fee" name="fee" value="${home?.fee?:''}">
                                      <span class="currency"><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>
                                      <label for="fee_homeperson" style="float: none;padding-right:5px">${message(code:'ads.price.fee.homeperson')}</label>
                                      <select id="fee_homeperson" name="fee_homeperson" class="mini">
                                      <g:each in="${homeperson}" var="item">
                                        <option <g:if test="${item?.id==inrequest?.fee_homeperson}">selected="selected"</g:if> value="${item?.id}">
                                          ${item[context?.lang=='_en'?'name'+context?.lang:'name2']}
                                        </option>
                                      </g:each>
                                      </select>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td>
                                      <label for="cleanup">${message(code:'ads.price.fee.cleanup')}</label>
                                      <input type="text" class="price" id="cleanup" name="cleanup" value="${home?.cleanup?:''}">
                                      <span class="currency"><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td>
                                      <label for="homerule">${message(code:'ads.price.rule')}</label>
                                      <g:textArea id="homerule" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" name="homerule" value="${home?.homerule?:''}" rows="5" cols="40" style="width:500px" />
                                    </td>
                                  </tr>
                                  <tr>
                                    <td>
                                      <label for="minday_id">${message(code:'ads.price.rule.minday')}</label>
                                      <select id="minday_id" name="rule_minday_id" class="mini">
                                      <g:each in="${minday}" var="item">            
                                        <option <g:if test="${item?.id==home?.rule_minday_id}">selected="selected"</g:if> value="${item?.id}">
                                          ${item['name'+context?.lang]}
                                        </option>
                                      </g:each>
                                      </select>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td>
                                      <label for="maxday_id">${message(code:'ads.price.rule.maxday')}</label>
                                      <select id="maxday_id" name="rule_maxday_id" class="mini">
                                      <g:each in="${maxday}" var="item">            
                                        <option <g:if test="${item?.id==home?.rule_maxday_id}">selected="selected"</g:if> value="${item?.id}">
                                          ${item['name'+context?.lang]}
                                        </option>
                                      </g:each>
                                      </select>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td>
                                      <label for="timein_id">${message(code:'ads.price.rule_timein')}</label>
                                      <select id="timein_id" name="rule_timein_id" class="mini">
                                      <g:each in="${timein}" var="item">            
                                        <option <g:if test="${item?.id==home?.rule_timein_id}">selected="selected"</g:if> value="${item?.id}">
                                          ${item['name'+context?.lang]}
                                        </option>
                                      </g:each>
                                      </select>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td>
                                      <label for="timeout_id">${message(code:'ads.price.rule_timeout')}</label>
                                      <select id="timeout_id" name="rule_timeout_id" class="mini">
                                      <g:each in="${timeout}" var="item">            
                                        <option <g:if test="${item?.id==home?.rule_timeout_id}">selected="selected"</g:if> value="${item?.id}">
                                          ${item['name'+context?.lang]}
                                        </option>
                                      </g:each>
                                      </select>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="padding:20px">
                                      <div class="float">
                                        <input type="submit" class="button-glossy orange" value="${message(code:'button.save')}" style="margin-right:5px;">
                                        <input type="reset" class="button-glossy grey" value="${message(code:'button.cancel')}">
                                      </div>
                                      <span class="actions col">
                                        <span class="action_button btn orange">
                                          <g:link class="icon none" target="_blank" controller="personal" action="adsoverview" id="${home?.id}" base="${context.sequreServerURL}">${Infotext.findByControllerAndAction('personal','adsoverview')['name'+context?.lang]}</g:link>
                                        </span>                
                                      </span>
                                    </td>
                                  </tr>
                                </table>
                              </g:form>
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
