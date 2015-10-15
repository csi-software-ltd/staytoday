<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />   
    <meta name="layout" content="main" />
    <g:javascript src="jquery-1.8.3.js" />
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
    jQuery(document).ready(function() {
      jQuery('a.colorbox').colorbox({
        inline: true,
        href: '#bron_lbox',
        scrolling: false,
        onLoad: function(){
          jQuery('#bron_lbox').show();
        },
        onCleanup: function(){
          jQuery('#bron_lbox').hide();
        }
      });
      setInterval(function(){ 
        jQuery('.action_button a.active').effect('pulsate','slow') 
      }, 500)
    });    
    function setBronStatus(id,st){
      <g:remoteFunction controller='personal' action='bookingsConfirm' onSuccess='processResponse(e)' params="'id='+id+'&st='+st" />
    }
    function processResponse(e){
      if(e.responseJSON.error){
        alert(e.responseJSON.message);
      } else if (e.responseJSON.trip){
        $('comments').removeClassName('red');
        $('bron_error').hide();
        var sMsg='';
        sMsg ='<table width="100%" cellpadding="0" cellspacing="0" border="0">'+
              '  <tr>'+
              '    <td valign="top">'+
              '      <div class="thumbnail shadow" style="width:140px;height:98px">'+              
              '        <img width="140" height="98" src="${urlphoto}'+e.responseJSON.home.client_id+'/t_'+e.responseJSON.home.mainpicture+'" alt="'+e.responseJSON.home.name+'"/>'+
              '      </div>'+
              '      <div class="col" style="padding:15px 0 15px 10px;width:400px">'+
              '        <h2 class="title" style="padding-left:0px"><a href="${(context?.is_dev)?'/'+context?.appname:''}/home/view/'+e.responseJSON.home.linkname+'" style="color:#ff530d">'+e.responseJSON.home.name+'</a></h2>'+
              '        <p><b>${message(code:"booking.address")}</b>: '+e.responseJSON.home.address+'</p>'+
              '      </div>'+
              '    </td>'+
              '  </tr>'+
              '  <tr>'+
              '    <td class="contprn">'+              
              '      <div class="float" style="line-height:18px;width:340px">'+
              '        ${message(code:"common.date_from").capitalize()}: <b>'+e.responseJSON.date_start+'</b><br/>'+
              '        ${message(code:"common.date_to").capitalize()}: <b>'+e.responseJSON.date_end+'</b><br/>'+
              '        ${message(code:"common.guests").capitalize()}: <b>'+e.responseJSON.homeperson+'</b>'+                          
              '      </div>'+
              '      <div class="details col" align="right" style="width:200px">'+
              '        <font color="gray">${message(code:"personal.cost")}</font><br/>'+
              '        <span class="ss_price b-rub">'+e.responseJSON.trip.price_rub+'&nbsp;<span class="ss_price b-rub">Р</span></span>'+            
              '      </div>'+              
              '    </td>'+
              '  </tr>'+              
              '  <tr>'+
              '    <td style="line-height:18px">'+              
              '      ${message(code:"booking.customer")}: <b><a href="${(context?.is_dev)?'/'+context?.appname:''}/id'+e.responseJSON.user.id+'">'+e.responseJSON.user.nickname+'</a></b><br/>'+
              '      ${message(code:"booking.date")}: <b>'+e.responseJSON.inputdate+'</b>'+              
              '    </td>'+
              '  </tr>'+              
              '</table>'+
              '<input type="hidden" name="id" value="'+e.responseJSON.trip.id+'"/>';
        $("bron_data").update(sMsg);
        jQuery('a.colorbox').click();
      } else {
        $('bronForm').submit();
      }
    }
    function cancelBronResponse(e){
      if(e.responseJSON.error){
        $('comments').addClassName('red');
        $('bron_error').show();
        $('bron_errorText').update(e.responseJSON.message);
        $('cancelMboxBronForm_submitButtun').disabled=false;
        jQuery('a.colorbox').click();
      } else {
        <g:remoteFunction controller='inbox' action='removeBron' onSuccess='location.reload(true)' params="'id='+e.responseJSON.lId" />
      }
    }
    </g:javascript>
    <style type="text/css">
      .actions { padding: 7px 10px }
      .action_button div.instructions { width: 162px !important }      
      .page-topic { padding: 10px 14px !important }
      .page-topic p { color: #FF530D; font-weight: bold }
    </style>    
  </head>
  <body>
              <g:render template="/ads_menu" />                                      
                      <div class="search-container">                      
                        <div id="search_body">
                          <div id="results">
                            <div id="results_header" class="results_header relative">
                              <g:form name="bronForm" action="bookings" base="${context?.mainserverURL_lang}">      
                                <div style="position:absolute;z-index:2;top:0;left:3px">                            
                                  <span class="results_sort">
                                    <label for="modstatus">${message(code:'data.list.view')}</label>
                                    <select name="modstatus" id="modstatus" onchange="$('bronForm').submit();">
                                      <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>${message(code:'booking.all')} (${status_count[0]})</option>
                                      <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>${message(code:'booking.unconfirmed')} (${status_count[1]})</option>
                                      <option value="2" <g:if test="${inrequest?.modstatus==2}">selected="selected"</g:if>>${message(code:'booking.confirmed')} (${status_count[2]})</option>
                                      <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>${message(code:'booking.canceled')} (${status_count[3]})</option>
                                    </select>
                                  </span>       
                                </div>                            
                              </g:form>                              
                              <span class="pagination col">
                                <g:paginate controller="personal" prev="&lt;&lt;" next="&gt;&gt;"
                                  action="${actionName}" max="20" params="${inrequest}" total="${data?.count}" /> 
                              </span>
                            </div>
                          <g:if test="${inrequest?.modstatus==1 && status_count[1]>0 && infotext['itext'+context?.lang]}">
                            <div class="page-topic">
                              <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                            </div>
                          </g:if>                            
                            <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                            <g:each in="${data?.records}" var="trip" status="i">
                              <tr>
                                <td onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
                                  <div class="relative" style="margin-bottom:10px">
                                  <g:if test="${trip?.modstatus==0}">
                                    <div class="new">!</div>
                                  </g:if>                                  
                                    <div class="thumbnail shadow">
                                      <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(trip.home_country_id)?.urlname,city:trip.home_city,linkname:trip?.home_linkname]}" target="_blank" title="${trip?.home_name}">
                                        <img width="200" height="140" src="${(trip?.home_mainpicture)?urlphoto+trip?.home_client_id+'/t_'+trip?.home_mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0"/>
                                      </g:link>
                                    </div>                                    
                                    <div class="description">                 
                                      <h2 class="title padd20 clearfix"><g:link class="name" mapping="${'hView'+context?.lang}" params="${[country:Country.get(trip.home_country_id)?.urlname,city:trip.home_city,linkname:trip?.home_linkname]}" target="_blank">${trip?.home_name}</g:link></h2>
                                      <ul class="details-list">
                                        <li class="details-list-item location">
                                          <span class="icons"></span>
                                          ${trip?.home_address}
                                        </li>
                                        <li class="details-list-item room_type">
                                          <span class="icons"></span>
                                          ${Hometype.get(Home.get(trip?.home_id)?.hometype_id)['name'+context?.lang]}                                        
                                        </li>                                        
                                        <li class="details-list-item dates">
                                          <span class="icons"></span>
                                          ${String.format('%td.%<tm.%<tY',trip.fromdate)} - ${String.format('%td.%<tm.%<tY',trip.todate)}
                                        </li>                        
                                        <li class="details-list-item person_capacity">
                                          <span class="icons"></span>
                                        <g:each in="${homeperson}" var="item"><g:if test="${item?.id==trip?.homeperson_id}">
                                          ${item['name'+context?.lang]}
                                        </g:if></g:each>
                                        </li>
                                      </ul>
                                    </div>
                                    <div class="description" style="width:480px">
                                      <h2 class="title padd20 paddtop float">
                                        <g:link style="color:#ff530d" mapping="${'pView'+context?.lang}" params="${[uid:'id'+trip.owner_id]}" target="_blank">${trip.user_nickname}</g:link>
                                      </h2>
                                      <div class="contprn col" align="right" style="margin-top:-10px">                                        
                                        <div class="details col" style="width:200px">
                                        <g:if test="${trip?.modstatus==0}">
                                          <font color="gray">${message(code:'booking.bron.notconfirmed')}</font>
                                        </g:if><g:elseif test="${trip?.modstatus>0}">
                                          <font color="green">${message(code:'booking.bron.confirmed')}</font>
                                        </g:elseif><g:elseif test="${trip?.modstatus==-1}">
                                          <font color="red">${message(code:'booking.bron.canceled')}</font>
                                        </g:elseif><br/>
                                          <span class="ss_price b-rub" style="padding:0 !important">                                      
                                            ${Math.round(trip?.price_rub / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
                                          </span><br/>
                                          <font color="#000">${message(code:"personal.cost")}</font>              
                                        </div>
                                      </div>
                                    </div>
                                  </div>                                  
                                  <div class="clearfix dview" id="item_${i+1}">
                                    <span class="actions float">
                                      <span class="action_button">
                                        <g:link class="icon view" controller="inbox" action="view" id="${trip?.mbox_id}" target="_blank" base="${context.sequreServerURL}">${message(code:'booking.view.mbox')}</g:link>
                                      </span>
                                      <span class="action_button">
                                        <g:link class="icon calendar" controller="personal" action="calendar" id="${trip?.home_id}" base="${context.sequreServerURL}">${message(code:'ads.view.calendar')}</g:link>
                                      </span>                                      
                                    <g:if test="${trip?.modstatus==0}">                                      
                                      <span class="action_button">
                                        <a class="icon active" href="javascript:void(0)" onclick="setBronStatus(${trip?.id},1);">${message(code:'booking.bron.confirm')}</a>
                                      </span>
                                      <span class="action_button">
                                        <a class="icon inactive" href="javascript:void(0)" onclick="setBronStatus(${trip?.id},-1);">${message(code:'booking.bron.cancel')}</a>
                                      </span>
                                    </g:if><g:elseif test="${trip?.modstatus==1}">                                      
                                      <span class="action_button">
                                        <a class="icon inactive" href="javascript:void(0)" onclick="setBronStatus(${trip?.id},-1);">${message(code:'booking.bron.cancel')}</a>
                                      </span>                                      
                                    </g:elseif><g:if test="${trip?.modstatus>0}">
                                      <span class="action_button">
                                        <g:link class="icon print" controller="personal" action="docprint3g" id="${trip?.id}" target="_blank" base="${context.sequreServerURL}">${message(code:'booking.bron.f3g')}</g:link>
                                      </span>
                                      <span class="action_button">
                                        <g:link class="icon print" controller="personal" action="tripprint" id="${trip?.id}" target="_blank" base="${context?.mainserverURL_lang}">${message(code:'trip.bron.print')}</g:link>
                                      </span>
                                    </g:if>
                                    </span>
                                  </div>                                  
                                </td>      
                              </tr>
                              <g:if test="${(i+1)!=data.records.size()}"> 
                              <tr><td class="dashed">&nbsp;</td></tr>
                              </g:if>    
                            </g:each>
                            </table>
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

                <a class="colorbox" href="javascript:void(0)" style="display:none">..</a>
                <div id="bron_lbox" class="new-modal" style="width:600px;display:none">
                  <h2 class="clearfix">${message(code:'booking.cancellation')}</h2>
                  <g:formRemote name="cancelMboxBronForm" url="[controller:'inbox',action:'cancelMboxBron',params:[type:1]]" onSuccess="cancelBronResponse(e);" before="\$('cancelMboxBronForm_submitButtun').disabled=true;">
                    <div id="bron_segment_lbox" class="segment nopadding">
                      <div id="bron_container_lbox" class="lightbox_filter_container" style="height:auto">
                        <div id="bron_data"></div>
                        <div class="dashed" style="margin:15px 0">&nbsp;</div>
                        <h2 class="clearfix">${message(code:'booking.cancellation.reason')}:</h2>
                        <div id="bron_error" class="notice" style="margin:5px 0;width:97%;display:none;">
                          <span id="bron_errorText"></span>
                        </div>
                        <g:textArea onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" id="comments" name="comments" value="" rows="5" cols="40" style="width:97%;margin-top:5px"/>
                      </div>
                    </div>
                    <div class="segment buttons">
                      <input type="submit" id="cancelMboxBronForm_submitButtun" class="button-glossy orange" value="${message(code:'booking.bron.cancel').capitalize()}"/>
                    </div>
                  </g:formRemote>
                </div>
              
              </td>
            </tr>
  </body>
</html>
