<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />   
    <meta name="layout" content="main" />
    <g:javascript src="jquery-1.8.3.js" />    
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/>
    <g:javascript>
    var iLim = ${textlimit}
    var defsumma = 0;
    function textLimit(sId){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iLim){
        symbols = symbols.substring(0,iLim);
        $(sId).value = symbols;
        return false;
      }
    }

    function startMessage(id){
      <g:remoteFunction controller='personal' action='startmessage' onSuccess='processResponse(e)' params="'id='+id" />
    }
    function processResponse(e){
      if(e.responseJSON.error){
        alert(e.responseJSON.message);
      } else {
        $('comments').removeClassName('red');
        $('bron_error').hide();        
        var sMsg= '<table width="100%" cellpadding="0" cellspacing="0" border="0">'+
                  '  <tr>'+
                  '	   <td class="contprn">'+
                  '      <div class="relative float">'+ 
                  '	       <div class="thumbnail userpic shadow">'+
                  '	         <img width="68" height="68" alt="'+e.responseJSON.userZ.nickname+'" title="'+e.responseJSON.userZ.nickname+'"'+
                  '			       src="'+((e.responseJSON.userZ.smallpicture && !e.responseJSON.userZ.is_external)?'${imageurl}':'')+((e.responseJSON.userZ.smallpicture)?e.responseJSON.userZ.smallpicture:"${resource(dir:'images',file:'user-default-picture.png')}")+'">'+
                  '		     </div>'+
                  '		     <div style="width:68px">'+
                  '		       <small style="white-space:normal"><a href="${(context?.is_dev)?'/'+context?.appname:''}/id'+e.responseJSON.zayavka.user_id+'">'+e.responseJSON.userZ.nickname+'</a></small><br/>'+
                  '		     </div>'+    
                  '      </div>'+
                  '      <div class="padd20 float" style="width:340px">'+
                  '		     <b style="font-size:14px;line-height:17px">'+e.responseJSON.zayavka.ztext+'</b><br/>'+
                  '        <span>'+e.responseJSON.adress+'</span>'+
                  '        <div style="line-height:17px">'+
                  '          ${message(code:"common.date_from").capitalize()}: <b>'+e.responseJSON.date_start+'</b><br/>'+
                  '          ${message(code:"common.date_to").capitalize()}: <b>'+e.responseJSON.date_end+'</b><br/>'+
                  '          ${message(code:"common.guests").capitalize()}: <b>'+e.responseJSON.homeperson+'</b>'+            
                  '        </div>'+
                  '      </div>'+
                  '      <div class="details col" align="right" style="width:160px">'+  
                  '        <font color="#000">${message(code:'ads.price.price_per_day')}</font><br/>'+
                  '        ${message(code:"waiting.from")} <span class="ss_price b-rub">'+e.responseJSON.prcFrom+"<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>"+
                  '        ${message(code:"waiting.to")} <span class="ss_price b-rub" style="line-height:30px">'+e.responseJSON.prcTo+"<g:rawHtml>${valutaSym}</g:rawHtml></span>"+
                  '      </div>'+
                  '    </td>'+
                  '  </tr>'+
                  '</table>'+
                  '<input type="hidden" name="z2C_id" value="'+e.responseJSON.z2C_id+'"/>'+
                  '<input type="hidden" name="id" value="'+e.responseJSON.zayavka.id+'"/>';
        $('message_data').update(sMsg);
        $('msg_homeperson').selectedIndex = e.responseJSON.zayavka.homeperson_id-1;
        
        var aStart = new Array();
        var aEnd = new Array();
        aStart = e.responseJSON.date_start.split('-');
        aEnd = e.responseJSON.date_end.split('-');
        $('date_start_value').value=e.responseJSON.date_start;
        $('date_end_value').value=e.responseJSON.date_end;
        $('date_start_year').value=aStart[2];
        $('date_end_year').value=aEnd[2];
        $('date_start_month').value=aStart[1];
        $('date_end_month').value=aEnd[1];
        $('date_start_day').value=aStart[0];
        $('date_end_day').value=aEnd[0];
        jQuery('#special_lbox_container').css('height','auto');
        jQuery.colorbox.resize(); 
        jQuery('#special').click();
      }
    }

    jQuery.noConflict();
    jQuery(document).ready(function(){
      jQuery('#special').colorbox({ 
        inline: true, 
        href: '#special_lbox', 
        onLoad: function(){                             
          jQuery('#special_lbox').show();           
        },
        onComplete: function(){
          calculateMailPrice();          
          jQuery('#special_lbox_container').css('height','auto');
          jQuery.colorbox.resize();          
        },
        onCleanup: function(){
          jQuery('#special_lbox').hide();          
        }
      });
    });
    
    function declineZayavka(id){
      <g:remoteFunction controller='personal' action='declineZayavka' onSuccess='location.reload(true)' params="'id='+id"/>
    }
    function processMailResponse(e){
      defsumma = (e.responseJSON.result/${valutaRates}).toFixed(0);
      if(e.responseJSON.error){
        $('calculateMailPriceError').show();
        $('calculateMailPriceOutput').hide();
        $('calculateMailPriceErrorText').update(e.responseJSON.errorprop);
      }else if (e.responseJSON.result!=null) {
        $('calculateMailPriceOutput').show();
        $('calculateMailPriceError').hide();
        $('calculateMailPriceOutputText').update((e.responseJSON.result/ ${valutaRates}).toFixed(0) + "&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>");
        $('msg_price').value=(e.responseJSON.result/${valutaRates}).toFixed(0);
      }else{
        $('calculateMailPriceError').hide();
        $('calculateMailPriceOutput').hide();
      }
      jQuery('#special_lbox_container').css('height','auto');
      jQuery.colorbox.resize(); 
    }   
    
    function calculateMailPrice(){
      $('home_id_mail').value=$('msg_home').value;
      $('date_start_year_mail').value=$('date_start_year').value;
      $('date_start_month_mail').value=$('date_start_month').value;
      $('date_start_day_mail').value=$('date_start_day').value;
      $('date_end_year_mail').value=$('date_end_year').value;
      $('date_end_month_mail').value=$('date_end_month').value;
      $('date_end_day_mail').value=$('date_end_day').value;
      $('homeperson_id_mail').value=$('msg_homeperson').value;      
      $('calculateMailPriceFormSubmit').click();
    }
    
    function zayavkaResponse(e){
      if(e.responseJSON.error){        
        $('bron_error').show();
        $('bron_errorText').update(e.responseJSON.message);
        jQuery('#special_lbox_container').css('height','auto');
        jQuery.colorbox.resize();
        $("waiting_submit").enable();        
      } else {
        location.reload(true);
      }
    }
    function submitMsg(){
      if(defsumma!=0&&(parseInt($('msg_price').value)>defsumma*1.2||parseInt($('msg_price').value)<defsumma*0.8)) {
        if (confirm('${message(code:"inbox.view.offerconfirm")}')){
          $('main_waiting_submit').click();
        }
      } else {
        $('main_waiting_submit').click();
      }
    }
    </g:javascript>
    <style type="text/css">
      .contprn .details .ss_price.b-rub{padding:0!important;height:30px;}
    </style>
  </head>
  <body>
              <g:render template="/ads_menu" />                                      
                      <div class="search-container">
                        <div id="search_body" class="relative">
                        <g:if test="${infotext['itext'+context?.lang]}">
                          <div class="page-topic">
                            <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                          </div>
                        </g:if>
                          <div id="results">
                          <g:if test="${data?.records}">
                            <g:if test="${data?.count > 20}">
                            <div id="results_header" class="results_header">                         
                              <span class="pagination col">
                                <g:paginate controller="personal" prev="&lt;&lt;" next="&gt;&gt;"
                                  action="${actionName}" max="20" params="${inrequest}"
                                  total="${data?.count}" /> 
                              </span>
                            </div>
                            </g:if>
                            <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">        
                            <g:each in="${data?.records}" var="record" status="i">
                              <tr>
                                <td onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
                                  <div class="relative float" style="margin-bottom:10px">
                                    <div class="thumbnail userpic shadow">                          
                                      <img width="68" height="68" alt="${record.user_nickname}" title="${record.user_nickname}" 
                                        src="${(record.user_smallpicture && !record.is_external)?imageurl:''}${(record.user_smallpicture)?record.user_smallpicture:resource(dir:'images',file:'user-default-picture.png')}"/>
                                    </div>
                                    <div style="width:68px">
                                      <small style="white-space:normal">
                                        <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+record?.user_id]}">${record?.user_nickname}</g:link>
                                      </small>
                                    </div>
                                  </div>
                                  <div class="description float" style="width:400px">                
                                    <ul class="details-list float">
                                      <li class="details-list-item">
                                        <b>${record?.ztext}</b>
                                      </li>
                                      <li class="details-list-item location">
                                        <span class="icons"></span>
                                      <g:each in="${country}" var="item">
                                        <g:if test="${item?.id==record?.country_id}">${item['name'+context?.lang]}</g:if>
                                      </g:each>
                                      <g:each in="${region}" var="item">
                                        <g:if test="${item?.id==record?.region_id}">${item['name'+context?.lang]}</g:if>
                                      </g:each>
                                      <g:if test="${record.region_id!=78 && record.region_id!=77}">${record?.city}</g:if>
                                      </li>
                                      <li class="details-list-item dates">
                                        <span class="icons"></span>
                                        ${String.format('%td.%<tm.%<tY',record.date_start)} - ${String.format('%td.%<tm.%<tY',record.date_end)}
                                        </li>
                                        <li class="details-list-item person_capacity">
                                          <span class="icons"></span>
                                        <g:each in="${homeperson}" var="item">            
                                          <g:if test="${item?.id==record?.homeperson_id}">
                                          ${item['name'+context?.lang]}
                                          </g:if>
                                        </g:each>
                                        </li>
                                        <li class="details-list-item">
                                          <font color="#FF530D">${message(code:'waiting.expires')}: ${String.format('%td.%<tm.%<tY',record.todate)}</font>
                                        </li>
                                      </ul>
                                    </div>
                                  </div>
                                  <div class="contprn col" align="right" style="width:215px">                                        
                                    <div class="details">
                                      <font color="#000">${message(code:'ads.price.price_per_day')}</font><br/>
                                      ${message(code:"waiting.from")} <span class="ss_price b-rub">${Math.round(record?.pricefrom_rub / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                                      ${message(code:"waiting.to")} <span class="ss_price b-rub" style="line-height:30px">${Math.round(record?.priceto_rub / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>                                      
                                      <font id="status"></font>
                                      <g:javascript>                                       
                                        var color, status;
                                        switch(${record?.modst}){
                                          case 0:  { color='gray'; status='${message(code:"waiting.new")}'; break; }
                                          case 1:  { color='green';status='${message(code:"waiting.special")}'; break; }
                                          case -1: { color='red';  status='${message(code:"waiting.declined")}'; break; }
                                          case -2: { color='#000'; status='${message(code:"waiting.expired")}'; break; }                                            
                                        }
                                        jQuery('#status').html(status);
                                        jQuery('#status').css('color',color);
                                      </g:javascript>                                        
                                    </div>
                                  </div>                                  
                                  <div style="clear:both;height:40px">
                                    <div class="dview">
                                      <div class="actions float" style="padding:8px 10px">
                                    <g:if test="${!record.modst}">
                                      <g:if test="${userHomes}">
                                        <span class="action_button">
                                          <a class="icon active" href="javascript:void(0)" onclick="startMessage(${record.z2C_id});">${message(code:'waiting.request.special')}</a>
                                        </span>
                                      </g:if><g:else>
                                        <font color="red"><g:message code="personal.waiting.nohomes.error"/></font>
                                      </g:else>
                                        <span class="action_button">
                                          <a class="icon inactive" href="javascript:void(0)" onclick="declineZayavka(${record.z2C_id});">${message(code:'waiting.request.decline')}</a>
                                        </span>
                                    </g:if><g:else>
                                        <span class="action_button">
                                          <g:link class="icon edit" controller="inbox" action="view" id="${Mbox.findByZayvka_id(record.id).id}">${message(code:'booking.view.mbox')}</g:link>
                                        </span>
                                    </g:else>
                                      </div>
                                    </div>
                                  </div>
                                </td>
                              </tr>
                            </g:each>                      
                            </table>
                          </g:if>
                          </div>
                        </div>
                      </div>
                      
                      
                      <div id="special_lbox" class="new-modal" style="width:630px;display:none">
                        <h2 id="tmpName" class="clearfix">${message(code:'waiting.special.send')}</h2>
                      <g:formRemote name="sendMessageForm" url="[controller:'inbox',action:'zayavkaresponse']" onSuccess="zayavkaResponse(e);" onLoading="\$('waiting_submit').disable()">
                        <div id="special_lbox_segment" class="segment nopadding" style="padding:0">
                          <div id="special_lbox_container" class="lightbox_filter_container">
                            <div id="message_data"></div>
                            <div class="dashed" style="margin:10px 0">&nbsp;</div>
                            <div style="padding:5px">
                              <small>${Answertype.get(2)['description'+context?.lang]}</small>
                            </div>
                            <div id="bron_error" class="notice" style="margin:0;display:none">
                              <span id="bron_errorText"></span>
                            </div>                            
                            <table width="100%" cellpadding="5" cellspacing="0" border="0">
                              <tr>
                                <td><label for="msg_home">${message(code:userHomes.size()>1?'inbox.view.housing':'server.housing')}</label></td>
                                <td colspan="3">
                                  <select name="home_id" id="msg_home" onchange="calculateMailPrice()" style="width:475px">
                                  <g:each in="${userHomes}" var="item">
                                    <option value="${item.id}">${item.name} (${item?.address})</option>
                                  </g:each>
                                  </select>
                                </td>
                              </tr>
                              <tr>
                                <td><label for="msg_homeperson">${message(code:'common.homeperson')}</label></td>
                                <td style="padding-bottom:0">
                                  <select name="homeperson_id" id="msg_homeperson" onChange="calculateMailPrice();">
                                  <g:each in="${homeperson}" var="item">
                                    <option value="${item.id}">${item['name'+context?.lang]}</option>
                                  </g:each>
                                  </select>
                                </td>
                                <td><label for="msg_price">${message(code:'personal.cost')} <a class="tooltip" href="javascript:void(0)" title="${message(code:'inbox.view.fullcost.alt')}"><img alt="${message(code:'inbox.view.fullcost.alt')}" src="${resource(dir:'images',file:'question.png')}" valign="absmiddle" border="0"/></a></label></td>
                                <td nowrap="nowrap" style="padding-bottom:0">
                                  <input type="text" class="price" id="msg_price" name="price" value="" />
                                  <span class="currency"><g:rawHtml>${valutaSym}</g:rawHtml></span>
                                </td>
                              </tr>
                              <tr>                                
                                <td colspan="4" style="padding-top:0;padding-left:265px">
                                  <font color="#FF530D" size="1">${message(code:'inbox.view.fullcost')}</font>
                                </td>
                              </tr>
                              <tr>
                                <td><label for="mail_date_start">${message(code:'common.date_from')}</label></td>
                                <td><calendar:datePicker id="date_start" name="date_start" value="" dateFormat="%d-%m-%Y" onChange="calculateMailPrice();"/></td>
                                <td><label for="mail_date_end">${message(code:'common.date_to')}</label></td>
                                <td><calendar:datePicker id="date_end" name="date_end" value="" dateFormat="%d-%m-%Y" onChange="calculateMailPrice();"/></td>
                              </tr>
                              <tr>
                                <td colspan="4">                                  
                                  <textarea rows="4" cols="40" id="comments" name="comments" placeholder="${message(code:'waiting.special.text')}" onkeydown="textLimit(this.id)" onkeyup="textLimit(this.id)" style="width:570px;height:40px"></textarea>
                                </td>
                              </tr>
                              <tr>
                                <td colspan="4" class="contprn" style="padding-top:10px">
                                  <div id="calculateMailPriceError" class="notice" style="margin:0;display:none">
                                    <font color="red" id="calculateMailPriceErrorText"></font>
                                  </div>
                                  <div class="details clearfix" id="calculateMailPriceOutput" style="display:none">
                                    <span class="float">
                                      ${message(code:'waiting.cost')}:<br/>
                                      <small>${message(code:'waiting.cost.all')} <a class="tooltip" title="${message(code:'waiting.cost.alt')}">
                                        <img src="${resource(dir:'images',file:'question.png')}" alt="Что это такое?" style="margin-bottom:-3px"></a>
                                      </small>
                                    </span>
                                    <span class="ss_price b-rub col" id="calculateMailPriceOutputText" style="padding:0 20px !important"></span>                                      
                                  </div> 
                                </td>
                              </tr>
                            </table>                            
                          </div>
                        </div>
                        <div class="segment buttons">
                          <input type="button" id="waiting_submit" class="button-glossy orange mini" value="${message(code:'button.send')}" onclick="submitMsg()"/>
                          <input type="submit" id="main_waiting_submit" class="button-glossy orange mini" value="${message(code:'button.send')}" style="display:none"/>
                        </div>
                      </g:formRemote>
                      </div>
              
                      <g:formRemote id="calculatePriceForm" name="calculateMailPriceForm" method="post" url="[ controller: 'home', action: 'calculatePrice' ]" onSuccess="processMailResponse(e)">
                        <input type="hidden" id="home_id_mail" name="home_id" value=""/>
                        <input type="hidden" id="date_start_year_mail" name="date_start_year" value=""/>
                        <input type="hidden" id="date_start_month_mail" name="date_start_month" value=""/>
                        <input type="hidden" id="date_start_day_mail" name="date_start_day" value=""/>
                        <input type="hidden" id="date_end_year_mail" name="date_end_year" value=""/>
                        <input type="hidden" id="date_end_month_mail" name="date_end_month" value=""/>
                        <input type="hidden" id="date_end_day_mail" name="date_end_day" value=""/>
                        <input type="hidden" id="homeperson_id_mail" name="homeperson_id" value=""/>
                        <input type="hidden" name="modifier" value="1"/>
                        <input type="submit" class="button-glossy orange" id="calculateMailPriceFormSubmit" style="display:none" value="Рассчитать"/>
                      </g:formRemote>
                      <a id="special" style="display:none"></a>                                  
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
