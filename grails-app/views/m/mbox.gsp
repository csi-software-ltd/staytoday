<html>
  <head>
    <title>${message(code:'personal.mbox.with')} ${(sender?.client_id != user?.client_id) ? sender?.nickname : recipient?.nickname}</title>    
    <meta name="description" content="" />
    <meta name="layout" content="m" />    
    <script type="text/javascript">
      var days=${days_between?:1}, persons=${mbox?.homeperson_id?:1},
          datePickerDate=new XDate("${formatDate(format:'yyyy-MM-dd',date:mbox?.date_start?:new Date())}"), 
          dateEnd=new XDate("${formatDate(format:'yyyy-MM-dd',date:mbox?.date_end?:new Date()+1)}"), 
          rule_minday=1, rule_maxday=1, aRule_minday=[], aRule_maxday=[];      

      function init(){
        datePicker('date_start'); 
        jQuery('#date_start').val(datePickerDate.toString("ddd, d MMM, yyyy","${context?.lang?'en':'ru'}"));        
        jQuery('#date_end').val(dateEnd.toString("ddd, d MMM, yyyy","${context?.lang?'en':'ru'}"));
        
        if(days==1)
          $('date_less').addClassName('ui-disabled');
        if(days==31)
          $('date_more').addClassName('ui-disabled');
        
        if(persons==1)
          $('person_less').addClassName('ui-disabled');  
        if(persons==16)
          $('person_more').addClassName('ui-disabled');
          
        if(rule_minday>=aRule_minday.length)
          $('rule_minday_more').addClassName('ui-disabled');
        if(rule_minday==1)
          $('rule_minday_less').addClassName('ui-disabled'); 
          
        if(rule_maxday>=aRule_maxday.length)
          $('rule_maxday_more').addClassName('ui-disabled');
        if(rule_maxday==1)
          $('rule_maxday_less').addClassName('ui-disabled'); 
      }   
      function datePicker(sId){
        var picker = jQuery('#'+sId);
        picker.mobipick({
          locale: "${context?.lang?'en':'ru'}",
          intlStdDate: false,
          minDate: (new XDate()),
          date: (new XDate(datePickerDate)),
        });
        picker.on('change',function(){
          var date = jQuery(this).val();
          var dateObject = jQuery(this).mobipick('option','date');
          datePickerDate = dateObject;
          updateDays();
        });      
      }      
      function dayLess(){
        days--;
        if(days<=1){
          days=1;
          $('date_less').addClassName('ui-disabled');
        }else
          $('date_more').removeClassName('ui-disabled');
        updateDays();  
      }
      function dayMore(){
        days++;
        if(days>=31){
          days=31;
          $('date_more').addClassName('ui-disabled');
        }else   
          $('date_less').removeClassName('ui-disabled');
        updateDays();  
      }
      function updateDays(){
        jQuery('#days').html(days);
        jQuery('#daysh').val(days);
        if(datePickerDate!=undefined){
          dateEnd = new XDate(datePickerDate);  
          jQuery('#date_end').val(dateEnd.addDays(jQuery('#daysh').val()).toString("ddd, d MMM, yyyy","${context?.lang?'en':'ru'}"));          
          jQuery('#date_starth').val(new XDate(datePickerDate).toString('yyyy-MM-dd'));
          jQuery('#date_endh').val(dateEnd.toString('yyyy-MM-dd'));
          calkPrice();
        }
      }    
      function personLess(){
        persons--;
        if(persons<=1){
          persons=1;
          $('person_less').addClassName('ui-disabled');
        }else 
          $('person_more').removeClassName('ui-disabled');
        updatePersons();  
      }      
      function personMore(){
        persons++;
        if(persons>=16){
          persons=16; 
          $('person_more').addClassName('ui-disabled');
        }else
          $('person_less').removeClassName('ui-disabled');  
        updatePersons();  
      }
      function updatePersons(){
        jQuery('#persons').html(persons);
        jQuery('#personsh').val(persons);
        calkPrice();
      }
      
      function ruleMindayMore(){
        rule_minday++;
        if(rule_minday>=aRule_minday.length){
          rule_minday=aRule_minday.length;   
          $('rule_minday_more').addClassName('ui-disabled');
        }else
          $('rule_minday_less').removeClassName('ui-disabled');
        updateRuleMinday();  
      }      
      function ruleMindayLess(hsMinday){
        rule_minday--;
        if(rule_minday<=1){
          rule_minday=1; 
          $('rule_minday_less').addClassName('ui-disabled');
        }else      
         $('rule_minday_more').removeClassName('ui-disabled');
        updateRuleMinday();  
      }
      function updateRuleMinday(){
        if(aRule_minday.length>=rule_minday)          
          jQuery('#rule_minday').html(aRule_minday[rule_minday-1].name?aRule_minday[rule_minday-1].name:'${message(code:"mobile.oneday")}');    
      }
      function ruleMaxdayMore(){
        rule_maxday++;
        if(rule_maxday>=aRule_maxday.length){
          rule_maxday=aRule_maxday.length; 
          $('rule_maxday_more').addClassName('ui-disabled');
        }else
          $('rule_maxday_less').removeClassName('ui-disabled');
        updateRuleMaxday();  
      }
      function ruleMaxdayLess(){    
        rule_maxday--;
        if(rule_maxday<=1){
          rule_maxday=1;   
          $('rule_maxday_less').addClassName('ui-disabled');
        }else      
          $('rule_maxday_more').removeClassName('ui-disabled');
        updateRuleMaxday();  
      }      
      function updateRuleMaxday(){
        if(aRule_maxday.length>=rule_maxday)  
          jQuery('#rule_maxday').html(aRule_maxday[rule_maxday-1].name?aRule_maxday[rule_maxday-1].name:'${message(code:"mobile.oneday")}');     
      }      
      jQuery(document).ready(function(){      
        jQuery('.option-list-item h3').click(function(){     
          var lsMsg = jQuery('[name="message"]');
          for(var i=0; i<lsMsg.length; i++)
            lsMsg[i].value='';
          
          var m=jQuery(this).parent(),l=m.find('.action-list-item');
          jQuery('.option-list-item').removeClass('open');
          //jQuery(this).find('a').removeClass('ui-corner-bottom');
          m.addClass('open').find('.action-list').slideDown(200).end().siblings().find('.action-list').slideUp(200);          
          if(l.length===1)
            l.find('input:radio').attr('checked','checked').change();          
        });
        if (${recipient?.client_id != user?.client_id}) {
          jQuery('.option-list-item:first').addClass('open').find('input:radio').attr('checked','checked').change();
        }      
        jQuery('.action-list input:radio').change(function(){
          var m=jQuery(this),l=m.parents('.action-list-item');
          if(m.is(':checked')){
            jQuery('.action-list-item').not(l).removeClass('checked').find('.drawer').slideUp(200,function(){
              jQuery(this).hide()
            });
            l.addClass('checked').find('div.drawer').slideDown(200);
          }
        });      
        var o = jQuery(".action-list-item:visible").eq(0);
        o.find('input:radio:first').attr('checked','checked').change();
        o.find('.action .ui-icon').removeClass('ui-icon-radio-off').addClass('ui-icon-radio-on');
        o.find('action.ui-radio-off').removeClass('ui-radio-off').addClass('ui-radio-on');        
        //jQuery(".option-list-item:visible:last h3 a").addClass('ui-corner-bottom');       
        jQuery('[name="answertype_id"]').change(function(){
          var lsMsg=jQuery('[name="message"]');
          for(var i=0;i<lsMsg.length;i++)
            lsMsg[i].value=''; 
        });       
      });      
      function calkPrice(){
        var date_start=$('date_starth').value, date_end=$('date_endh').value,  
            personsh=$('personsh').value, home_id;
        jQuery('#calkError').html('');                 
        if(!jQuery('#home_id_spec').length)
          return            
        home_id=$('home_id_spec').value;        
        jQuery.ajax({
          url: "${createLink(action:'calculatePrice')}",
          data: {date_start:date_start,date_end:date_end,homeperson_id:personsh,home_id:home_id},
          success: function(json){
            processResponse(json);
          }
        });
      }
      function processResponse(json){
        if(json.error){
          jQuery('#err').html(json.errorprop);
          $('err').show();
        } else if (json.result){
          $('err').hide();
          $('price_spec').value = (json.result/ ${valutaRates}).toFixed(0);
        }else
          $('err').hide();
      }
      function addAnswerMessage(){
        jQuery.mobile.showPageLoadingMsg("b","${message(code:'spinner.message.mobile')}",false);    
        var date_start=$("date_starth").value, date_end=$("date_endh").value,
            home_id_spec=$("home_id_spec").value, price_spec=$("price_spec").value,
            lId=${mbox?.id}, aMessages=jQuery('[name="message"]'), sMessage='', answertype_id=0, pricing_unit=$("pricing_unit").value;

        for(var i=0;i<aMessages.length;i++)
          sMessage+=aMessages[i].value+',';

        if(jQuery('input[name="answertype_id"]:checked').length)
          answertype_id=jQuery('input[name="answertype_id"]:checked').val();

        <g:remoteFunction action="addanswer" onSuccess="mboxAnswerResponse(e)" onComplete="jQuery.mobile.hidePageLoadingMsg()" params="\'id=\'+lId+\'&answertype_id=\'+answertype_id+\'&message=\'+sMessage+\'&rule_minday_id=\'+rule_minday+\'&rule_maxday_id=\'+rule_maxday+\'&date_start=\'+date_start+\'&date_end=\'+date_end+\'&homeperson_id=\'+persons+\'&home_id_spec=\'+home_id_spec+\'&price_spec=\'+price_spec+\'&pricing_unit=\'+pricing_unit" />
      }
      function addClientMessage(){
        if(!jQuery("a#message_submit_button").is(":disabled")){
          jQuery.mobile.showPageLoadingMsg("b","${message(code:'spinner.message.mobile')}",false);    
          var lId=${mbox?.id}, aMessages=jQuery('[name="message"]'), sMessage='', answertype_id=0;
          for(var i=0;i<aMessages.length;i++)
            sMessage+=aMessages[i].value+',';
          jQuery("a#message_submit_button").prop('disabled', true);
          <g:remoteFunction action="addanswer" onSuccess="mboxClientResponse(e)" onComplete="jQuery.mobile.hidePageLoadingMsg()" params="\'id=\'+lId+\'&answertype_id=0&message=\'+sMessage" />
        }
      }

      function mboxClientResponse(e){
        if(e.responseJSON.error){
          if(e.responseJSON.errorprop)
            jQuery(".err").show().text(e.responseJSON.errorprop);
          jQuery("a#message_submit_button").prop('disabled', false);
        } else {
          location.reload(true)
        }
      }

      function mboxAnswerResponse(e){
        if(e.responseJSON.error){
          if(e.responseJSON.errorprop)
            alert(e.responseJSON.errorprop);
          jQuery("#message_form").find("a#message_submit_button").removeAttr("disabled");
        } else {
          location.reload(true)
        }
      }
      function deleteRec(iMboxRecId,bType){
        if(confirm('${message(code:bType?"inbox.view.booking.confirm.delete":"inbox.view.specoffer.confirm.delete")}')){
          jQuery.mobile.showPageLoadingMsg("b","${message(code:'spinner.message.mobile')}",false);
          <g:remoteFunction action="deletembox" params="\'id=\'+iMboxRecId" onSuccess="location.reload(true)" onComplete="jQuery.mobile.hidePageLoadingMsg()" />
        }
      }
      
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null)
          return;
        new Ajax.Updater(
          { success: $('output') },
          link.href,
          { evalScripts: true, onComplete: function(response) { jQuery('#ajax_wrap').listview(); } }
        );
      }
    </script>    
  </head>
  <body onload="${sender?.client_id==user?.client_id?'init()':''}">
    <div data-role="page">    
      <div data-role="header" data-position="fixed">
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" href="${createLink(uri:'/inbox',base:context?.mobileURL_lang)}" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>           
            <li class="text" role="heading">${message(code:'history.mbox').capitalize()}</li>
          <g:if test="${mbox?.modstatus>3 || !isHideContact}">
            <li class="divider-vertical"></li>             
            <li class="button_phone">
            <g:if test="${sender?.client_id != user?.client_id}">
              <g:if test="${sender?.tel}"><a class="icon phone" href="tel:${sender?.tel}" title="${message(code:'mobile.call')} ${sender?.nickname}">${message(code:'mobile.call')}</a></g:if>
            </g:if><g:else>
              <g:if test="${recipient?.tel}"><a class="icon phone" href="tel:${recipient?.tel}" title="${message(code:'mobile.call')} ${sender?.nickname}">${message(code:'mobile.call')}</a></g:if>
            </g:else>
            </li>
          </g:if>
          </ul>
        </div>
      </div>
      <div data-role="content">
        <div class="new-modal ui-corner-all ui-popup ui-body-c">
          <h2 class="clearfix ui-corner-top ui-header ui-bar-f">${message(code:'detail.send_message')}</h2>
          <div class="segment nopadding ui-content">
            <div class="ui-li-thumb relative" style="margin-right:15px">
              <img class="thumbnail shadow" src="${home.mainpicture?homeurl+home.client_id+'/t_'+home.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0" />
            </div>
            <p style="padding-top:10px; margin:0px"><b>${home.name}</b></p>
            <ul class="ui-li-desc details-list clearfix">
              <li class="details-list-item location"><span class="icons"></span>${home?.shortaddress}</li>
            <g:if test="${mbox?.answertype_id!=4}">
              <li class="details-list-item dates"><span class="icons"></span>${String.format('%td.%<tm.%<tY',mbox?.date_start)} - ${String.format('%td.%<tm.%<tY',mbox?.date_end)}</li>
              <li class="details-list-item person_capacity"><span class="icons"></span>${Homeperson.get(mbox?.homeperson_id).name}</li>
              </g:if><g:elseif test="${mbox?.is_answer==1 && mbox?.answertype_id==4}">
                <g:if test="${home.rule_minday_id>0}">
              <li class="details-list-item"><span class="icons"></span>${message(code:'inbox.view.mindays')}: ${Rule_minday.get(home?.rule_minday_id).nday}</li>
                </g:if><g:if test="${home?.rule_maxday_id>1}">
              <li class="details-list-item"><span class="icons"></span>${message(code:'inbox.view.maxdays')}: ${Rule_maxday.get(home?.rule_maxday_id).nday}</li>
                </g:if>
              <li class="details-list-item"><span class="icons"></span>${message(code:'inbox.view.reqdays')}: ${days_between}</li>
              </g:elseif>
              <li class="details-list-item">
                <span class="b-rub">${displayPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                <g:if test="${mbox?.answertype_id==2}">&mdash; <font color="red">${message(code:'waiting.special')}</font></g:if>
              </li>
            </ul><br/>            
          <g:if test="${recipient?.client_id != user?.client_id}">
            <form name="message_form">
              <input type="hidden" name="home_id" id="home_id" value="${home?.id}" />
              <div id="option-list" class="option-list" data-role="collapsible-set">
              <g:each in="${answergroup}" var="group" status="g">    
                <div class="option-list-item ${group.icon}<g:if test="${g==0 || (g==2 && (mbox?.answertype_id in [7,8,9] || mbox?.modstatus in [3,4]))}"> open" data-collapsed="false</g:if>" data-role="collapsible" data-expanded-icon="arrow-d" data-collapsed-icon="arrow-r" data-content-theme="d"
                  <g:if test="${g==0 && (mbox?.answertype_id in [7,8,9] || mbox?.modstatus in [3,4] || !iscanoffer)}">style="display: none"</g:if>
                  <g:elseif test="${g==1}">style="display: ${(iscandecline)?'block':'none'}"</g:elseif>
                  <g:elseif test="${g==2 && !(mbox?.modstatus in [3,4]) && iscanoffer}">style="display:none"</g:elseif>>
                  <h3>${group['name'+context?.lang]}</h3>
                  <ul class="action-list" style="display:${(g==0 || (g==2 && (mbox?.answertype_id in [7,8,9] || mbox?.modstatus in [3,4])))?'block':'none'}">
                  <g:each in="${answertype}" var="atype">
                    <g:if test="${atype?.answergroup_id==group?.id}">
                    <li class="action-list-item"
                      <g:if test="${atype?.id==1}">style="display:${iscandecline?'block':'none'}"</g:if>
                      <g:elseif test="${atype?.id==2}">style="display:${(mbox?.modstatus!=4)?'block':'none'}"</g:elseif>>
                      <label class="action" data-inline="true" data-corners="false">
                        <input type="radio" name="answertype_id" value="${atype?.id}" /> ${atype['name'+context?.lang]}
                        <g:if test="${atype?.helptext}"><a class="tooltip" href="javascript:void(0);" title="${atype?.helptext}">${message(code:'inbox.view.what')}</a></g:if>
                      </label>
                      <div data-role="content" class="drawer ui-body ui-body-c" style="display:none">
                        <p class="description">${atype['description'+context?.lang]}</p>
                        <div id="warning_${atype.id}" class="ui-body ui-body-e ui-corner-all st" style="display:none">
                        </div>
                      <g:if test="${atype.id==2}">
                        <div class="ui-body ui-body-b ui-corner-all st">
                          <div data-role="fieldcontain" style="border-bottom:none">
                            <div class="ui-grid-a">
                              <div class="ui-block-a"><label for="home_id" class="padd"><b>${message(code:'server.housing')}</b></label></div>
                              <div class="ui-block-b">
                                <label class="select"></label>
                                <select id="home_id_spec" name="home_id" data-theme="f" onchange="calkPrice()">
                                <g:each in="${userHomes}" var="item" status="i">
                                  <option <g:if test="${item.id==home.id}">selected="selected"</g:if> value="${item.id}">
                                    ${item.name}
                                  </option>
                                </g:each>
                                </select>
                              </div>
                            </div>
                          </div>
                        </div><br/>
                        <div class="ui-body ui-body-b ui-corner-all st">
                          <div data-role="fieldcontain" class="nopadd">
                            <div class="ui-grid-a">
                              <div class="ui-block-a"><label for="date_start" class="padd"><b>${message(code:'common.date_from')}</b></label></div>
                              <div class="ui-block-b"><input type="text" id="date_start" /></div>
                            </div>
                          </div>
                          <input type="hidden" name="date_start" id="date_starth" value="${formatDate(format:'yyyy-MM-dd',date:mbox?.date_start?:(new Date()))}" />
                          <label for="days" style="margin-right:11px"><b>${message(code:'mobile.days')}:</b></label>
                          <a id="date_less" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-left" onclick="dayLess()">-</a>
                          <b id="days">${days_between?:1}</b>
                          <a id="date_more" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-right" onclick="dayMore()">+</a>                      
                          <input type="hidden" id="daysh" value="${days_between?:1}" />
                        </div>
                        <div class="ui-body" style="padding:0 8px">
                          <div data-role="fieldcontain" style="border-bottom:none">
                            <div class="ui-grid-a">
                              <div class="ui-block-a"><label for="date_end" class="padd"><b>${message(code:'common.date_to')}</b></label></div>
                              <div class="ui-block-b"><input type="text" id="date_end" disabled /></div>
                            </div>
                          </div>
                          <input type="hidden" name="date_end" id="date_endh" value="${formatDate(format:'yyyy-MM-dd',date:mbox?.date_end?:(new Date()+1))}" />                             
                        </div>
                        <div class="ui-body ui-body-b ui-corner-all st">
                          <label for="homeperson_id"><b>${message(code:'mobile.guests')}:</b></label>
                          <a id="person_less" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-left" onclick="personLess()">-</a>  
                          <b id="persons">${mbox?.homeperson_id?:1}</b>
                          <a id="person_more" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-right" onclick="personMore()">+</a>
                          <input type="hidden" name="homeperson_id" id="personsh" value="${mbox?.homeperson_id?:1}" />
                        </div><br/>                      
                        <div class="ui-body ui-body-b ui-corner-all st">
                          <div data-role="fieldcontain" style="border-bottom:none">
                            <div class="ui-grid-b">
                              <div class="ui-block-a"><label for="price" class="padd"><b>${message(code:'list.filtr.price')}</b></label></div>
                              <div class="ui-block-b">
                                <input type="text" name="price" id="price_spec" value="${Math.round(mbox?.price_rub/cpecofferRates)}" size="10">
                              </div>
                              <div class="ui-block-c"><label for="price" id="valuta" class="padd"><b><g:rawHtml>${valutaSym}</g:rawHtml></b></label></div>
                            </div>
                            <div data-role="fieldcontain" style="border-bottom:none">
                              <select id="pricing_unit" name="pricing_unit" data-theme="f" onchange="calkPrice()">
                                <option value="0">${message(code:'list.full_period')}</option>
                                <option value="1">${message(code:'list.per_night')}</option>
                              </select>
                            </div>
                          </div>            
                          <span class="cleaning-fee">(${message(code:'inbox.view.fee')})</span>                          
                        </div>
                      </g:if><g:elseif test="${atype.id==4}">
                        <div class="ui-body ui-body-b ui-corner-all st">
                          <label for="rule_minday" style="margin-right:7px"><b>${message(code:'mobile.minday_rent')}:</b></label>
                          <a id="rule_minday_less" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-left" onclick="ruleMindayLess()">-</a>
                          <b id="rule_minday">${home?.rule_minday_id}</b>
                          <a id="rule_minday_more" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-right" onclick="ruleMindayMore()">+</a>
                        </div><br/>
                        <div class="ui-body ui-body-b ui-corner-all st">
                          <label for="rule_maxday"><b>${message(code:'mobile.maxday_rent')}:</b></label>
                          <a id="rule_maxday_less" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-left" onclick="ruleMaxdayLess()">-</a>
                          <b id="rule_maxday">${home?.rule_maxday_id}</b>
                          <a id="rule_maxday_more" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-right" onclick="ruleMaxdayMore()">+</a>
                        </div>
                        <div id="nights-error" class="ui-body ui-body-e ui-corner-all st" style="display:none">
                          ${message(code:'inbox.view.match')}
                        </div>
                      </g:elseif>
                      <g:if test="${atype.id==2}">
                        <div id="err" class="notice" style="display:none">
                        </div>
                      </g:if>
                        <div class="ui-body" style="padding:0 0 8px 0">
                          <div data-role="fieldcontain" class="ui-hide-label" style="border-bottom:none">
                            <label for="message_${g}">${message(code:'inbox.view.additmessage')}</label>
                            <textarea id="message_${g}" name="message" placeholder="${message(code:'inbox.view.additmessage')}"></textarea>
                          </div>
                          <a data-role="button" data-theme="f" id="message_submit_button" onclick="addAnswerMessage()">${message(code:'button.send')}</a>                        
                        </div>
                      </div>
                    </li>
                    </g:if>
                  </g:each>
                  </ul>
                </div>
              </g:each>              
              </div>
            </form>
          </div>
          </g:if><g:else>            
            <div data-role="fieldcontain" id="msg_response_div" class="ui-hide-label">
              <div id="warning_client" class="header-tooltip err" style="display:none"></div>
              <label for="message_new">${message(code:'common.text_of_message')}</label>
              <textarea id="message_new" name="message" rows="5" cols="30" placeholder="${message(code:'common.text_of_message')}"></textarea>
            </div>
          </div>
          <div class="segment buttons">
            <a data-role="button" data-theme="f" id="message_submit_button" onclick="addClientMessage()">${message(code:'button.send')}</a>
          </div>        
          </g:else>
        </div>

        <ul id="output" data-role="listview" data-split-icon="arrow-r" style="margin:10px -15px 0">
        <g:if test="${data.records}">
          <li data-role="divider" data-theme="a" style="padding:0 0 0 10px;height:40px">
            <span class="count float">
              <span>${message(code:'mobile.found')}</span><span id="ads_count">${data.count}</span>
            </span>
            <span class="pagination col">
              <g:paginate controller="m" action="mbox_list" prev="&lt;" next="&gt;" 
                maxsteps="1" omitFirst="${true}" omitLast="${true}" max="5" total="${data.count}" params="${params}" />
              <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
            </span>
          </li>        
          <g:each in="${data.records}" var="item">   
          <li data-icon="false" class="clearfix">
            <div class="ui-li-thumb relative" style="max-width:90px;max-height:130px;width:90px;margin-right:5px">
            <g:if test="${item?.is_system==1}">
              <img class="thumbnail shadow" alt="StayToday" border="0" src="${resource(dir:'images',file:'logo_large.png')}" />
              <p class="ui-li-desc" style="margin-top:3px;line-height:10px">
                <small style="white-space:normal">StayToday</small><br/>
              </p>
            </g:if>
            <g:elseif test="${(sender.client_id!=user.client_id && item.is_answer==0) || (sender.client_id==user.client_id && item.is_answer==1)}">      
              <img class="thumbnail shadow" alt="${user.nickname}" border="0" src="${((user.picture && !user.is_external)?imageurl+'t_':'')+(user.picture?:resource(dir:'images',file:'default-picture.png'))}" />
              <p class="ui-li-desc" style="margin-top:3px;line-height:10px">
                <small style="white-space:normal">${message(code:'inbox.view.you')}</small><br/>
                <small style="white-space:nowrap">${item.moddate}</small>
              </p>
            </g:elseif><g:elseif test="${(sender.client_id==user.client_id && item.is_answer==0) || (sender.client_id!=user.client_id && item.is_answer==1)}">                    
              <g:if test="${sender.client_id!=user.client_id}">
              <img class="thumbnail shadow" alt="${sender.nickname}" title="${nickname}" border="0" src="${((sender.picture && !sender.is_external)?imageurl+'t_':'')+(sender.picture?:resource(dir:'images',file:'default-picture.png'))}">
              </g:if><g:else>
              <img class="thumbnail shadow" alt="${recipient.nickname}" title="${recipient.nickname}" border="0" src="${((recipient.picture && !recipient.is_external)?imageurl+'t_':'')+(recipient.picture?:resource(dir:'images',file:'default-picture.png'))}">       
              </g:else>
              <p class="ui-li-desc" style="line-height:10px">
                <small style="white-space:normal">${(sender.client_id != user.client_id)? sender.nickname : recipient.nickname}</small><br/>
                <small style="white-space:nowrap">${item.moddate}</small>
              </p>
            </g:elseif>
            </div>
            <div class="ui-li-desc">
              <div class="bubble-container">
              <g:if test="${item.rectext}">
                <div style="clear:left;margin-top:5px">
                  <p><small style="white-space: normal">${item.rectext}</small></p>
                </div>
              </g:if>
              <g:if test="${((sender.client_id == user.client_id && item.answertype_id in [1,2,8]) || (sender.client_id != user.client_id && item.answertype_id in [1,2,3,4])) && (mbox.modstatus < 4 || mbox.modstatus == 5)}">
                <div class="ui-body ui-body-e ui-corner-all st">
                <g:if test="${sender.client_id == user.client_id}">
                  <g:if test="${item.answertype_id==1 && mbox.modstatus < 4}">
                  <a data-role="button" data-theme="f" onclick="deleteRec(${item.id},1)">
                    ${message(code:'inbox.view.delete.bookoffer')}
                  </a>
                  </g:if><g:elseif test="${item.answertype_id==2 && mbox.modstatus < 4}">
                  <a data-role="button" data-theme="f" <g:if test="${mbox.modstatus!=4}">onclick="deleteRec(${item.id},0)"</g:if>>
                    ${message(code:'inbox.view.delete.specoffer')}
                  </a>
                  </g:elseif><g:elseif test="${item.answertype_id==8 && mbox.modstatus == 5}">
                  ${message(code:'inbox.view.freedays')}<br/>
                  <a data-role="button" data-theme="f" onclick="removeBron(${item.mbox_id})">${message(code:'inbox.view.unlock')}</a>
                  </g:elseif>
                </g:if><g:else>
                  <g:if test="${item.answertype_id in [1,2] && mbox.modstatus < 4}">
                    <p class="ui-li-desc">
                      <span class="b-rub">${totalPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                      <font color="gray">${message(code:'inbox.bron.summa')}</font>                    
                    </p>
                    <a href="${createLink(controller:'m',action:'bron',id:item.id,params:[mbox_id:item.mbox_id],base:context?.mobileURL_lang)}" data-role="button" data-theme="f">${message(code:'button.booking')}</a>
                  </g:if><g:elseif test="${item.answertype_id in [3,4]}">
                  <p>${message(code:'inbox.view.decline.info')}</p>
                  </g:elseif>
                </g:else>
                </div>
              </g:if>
              </div>
            </div>
          </li>
          </g:each>
        </g:if><g:else>
          <li class="ui-li ui-li-static">${message(code:'mobile.messages.notfound')}</li>        
        </g:else>
        </ul>
      </div>
      <div data-role="footer" data-position="fixed" id="footer_inbox_msg">
      </div>
    </div>
  </body>
</html>
