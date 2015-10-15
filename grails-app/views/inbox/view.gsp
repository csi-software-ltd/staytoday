<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''} ${(sender?.client_id != user?.client_id) ? sender?.nickname : recipient?.nickname}</title>    
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" /> 
    <link rel="canonical" href="${createLink(controller:'inbox',action:'view',id:msg?.id,base:context?.mainserverURL_lang)}" />
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(uri:'/mbox?id='+msg?.id,base:context?.mobileURL_lang)}" />
    <link rel="alternate" media="handheld" href="${createLink(uri:'/mbox?id='+msg?.id,base:context?.mobileURL_lang)}" />
    <meta name="layout" content="main" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'messages.css')}"/>    
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/>
    <g:javascript src="jquery-1.8.3.js" />    
    <g:javascript>	
    var arrVId = new Array();
    var arrVValuta = new Array();
    var arrVSum = new Array(); 
    <g:each in="${userHomes}" var="home" status="i">
      arrVId.push('${home.id}');      
      arrVValuta.push('${Valuta.get(home.valuta_id).symbol.decodeHTML()}');
      arrVSum.push('${home.pricestandard}'); 
    </g:each>    
    var iLim = ${textlimit};
    var bronCounter=0;
    var defsumma = ${Math.round(msg?.price_rub/cpecofferRates)};
    function textLimit(sId){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iLim){
        symbols = symbols.substring(0,iLim);
        $(sId).value = symbols;
        return false;
      }
    }    
    function toggleFilter(container){ 
      var li = $(container);           
      
      if(li.className == 'search_filter')
        li.className = 'search_filter closed open';
      else if(li.className == 'search_filter closed open')
        li.className = 'search_filter'; 
      else if(li.className == 'search_filter closed')
        li.className = 'search_filter open';
      else if(li.className == 'search_filter open')
        li.className = 'search_filter closed';      
    }    
    jQuery.noConflict();   
    jQuery(document).ready(function() { 
      startMsgSearch();      
    <g:if test="${msg?.answertype_id==0 && msg?.modstatus in [1,2,6]}">
      jQuery('ul.pagination li').eq(0).addClass('current');
      jQuery('.slides_control .slide').eq(0).addClass('current');
    </g:if><g:elseif test="${msg?.answertype_id in [3,4,5,6] && msg?.modstatus in [1,6]}">
      jQuery('ul.pagination li').eq(1).addClass('current').end().slice(0,1).addClass('past');
      jQuery('.slides_control .slide').eq(1).addClass('current');
    </g:elseif><g:elseif test="${msg?.answertype_id in [1,2,6] && msg?.modstatus in [1,3,6]}">
      jQuery('ul.pagination li').eq(2).addClass('current').end().slice(0,2).addClass('past');
      jQuery('.slides_control .slide').eq(2).addClass('current');
    </g:elseif><g:elseif test="${msg?.answertype_id in [6,7] && msg?.modstatus in [4,6] && Trip.findByMbox_id(msg?.id)?.modstatus<=0}">
      jQuery('ul.pagination li').eq(3).addClass('current').end().slice(0,3).addClass('past');
      jQuery('.slides_control .slide').eq(3).addClass('current');      
    </g:elseif><g:elseif test="${(msg?.answertype_id in [8,9] && msg?.modstatus in [5,6]) || (msg?.modstatus in [4,6] && Trip.findByMbox_id(msg?.id)?.modstatus>0)}">
      jQuery('ul.pagination li').eq(4).addClass('current').end().slice(0,4).addClass('past');
      jQuery('.slides_control .slide').eq(4).addClass('current');
    </g:elseif>      
      jQuery(".option-list-item h4").click(function(){
        var lsMsg=jQuery("[name='message']");
        for(var i=0;i<lsMsg.length;i++)
          lsMsg[i].value='';          
        var m=jQuery(this).parent(),l=m.find(".action-list-item");
        jQuery(".option-list-item").removeClass("open");
        m.addClass("open").find(".action-list").slideDown(200).end().siblings().find(".action-list").slideUp(200);
        if(l.length===1){
          l.find("input:radio").attr("checked","checked").change()
        }
      });
      if (${recipient?.client_id != user?.client_id}) 
        jQuery(".option-list-item:first").addClass("open").find(".action-list").show();
        
      jQuery(".action-list input:radio").change(function(){
        var m=jQuery(this),l=m.parents(".action-list-item");
        if(m.is(":checked")){
          jQuery(".action-list-item").not(l).removeClass("checked").find(".drawer").slideUp(200,function(){
            jQuery(this).hide()
          });
          l.addClass("checked").find("div.drawer").slideDown(200)   
        }
      });
      jQuery(".action-list-item:visible").eq(0).find('input:radio:first').attr("checked","checked").change();
      
      jQuery("[name='answertype_id']").change(function(){
        var lsMsg=jQuery("[name='message']");
        for(var i=0;i<lsMsg.length;i++)
          lsMsg[i].value=''; 
      });          
      var k=jQuery("form#message_form");
      k.submit(function(){
        var l=jQuery(this);
        //checkDate();
        jQuery(this).find(".action-list-item:not(.checked)").find("input, select, textarea").attr("disabled","disabled")
      });
      jQuery('#bron_link').colorbox({
        inline: true,
        href: '#bron_lbox',
        scrolling: false,
        onLoad: function(){
          jQuery('#bron_lbox').show();
        },
        onCleanup: function(){
          jQuery('#bron_lbox').hide();
        },
        onComplete: function(){
          $("bron_lbox_segment").setStyle({ height: Math.max($("bron_lbox_container_r").offsetHeight,$("bron_lbox_container").offsetHeight)+'px'});
          jQuery.colorbox.resize();
        }
      });
    });
    function checkDate(){     
      var l = jQuery("form#message_form").find("input[type='button']"),
      err = jQuery("form#message_form").find(".err"),h=false;
      jQuery.ajax({
        url: "${createLink(controller:'home',action:'check_date',base:context?.mainserverURL_lang)}",
        data: { date_start_month: jQuery('#date_start_month').val(), 
                date_start_day: jQuery('#date_start_day').val(),
                date_start_year: jQuery('#date_start_year').val(),                  
                date_end_month: jQuery('#date_end_month').val(),
                date_end_day: jQuery('#date_end_day').val(),
                date_end_year: jQuery('#date_end_year').val()
        },
        success: function(json){
          if(json.error){
            err.show().text(json.errorprop);
            h=false
          }else{
            err.hide().text("");
            h=true
          }
          k();
        },
        dataType:"json"
      });
      k();        
      function k(){
        if(h){
          l.attr("disabled","disabled");
          location.reload(true)
        }else{
          l.removeAttr("disabled")
        }
      } 
    }
    function mboxAnswerResponse(e){
      if(e.responseJSON.error){
        if(e.responseJSON.errorprop){
          jQuery("form#message_form").find(".err").show().text(e.responseJSON.errorprop);
        }
        $('loader').hide();
        jQuery("form#message_form").find("input[type='button']").removeAttr("disabled")
        jQuery("form#message_form").find("input[type='submit']").removeAttr("disabled")
      } else {
        location.reload(true)
      }
    }
    function deleteRec(lId,type){
      if(confirm('${message(code:type?"inbox.view.booking.confirm.delete":"inbox.view.specoffer.confirm.delete")}')){
        <g:remoteFunction action='deletembox' onSuccess='location.reload(true)' params="'id='+lId" />
      }
    }
    function startMsgSearch(){     
      $('submit_button').click();
    }
    function submitMsg(lId){
      if(lId==2&&defsumma!=0&&$('pricing_unit').value!=1&&(parseInt($('price').value)>defsumma*1.2||parseInt($('price').value)<defsumma*0.8)) {
        if (confirm('${message(code:"inbox.view.offerconfirm")}')){
          $('msg_submit_button').click();
          $('loader').show();
        } else $('proxy_submit_button_'+lId).disabled = false;
      } else {
        $('msg_submit_button').click();
        $('loader').show();
      }
    }
    function changePrice(lId){
      var k = arrVId.indexOf(lId), valuta = arrVValuta[k].unescapeHTML().replace(/&#39;/g, "\'");
      $('valuta').innerHTML = valuta;
    }
    function testMessage(lId){
      lId = lId || '' 
      var l = $F("message_inquiry_"+lId);
      //tests not needed now
      if (l!='' || (lId<3 && lId!=''))
        submitMsg(lId);
      else {
        $('proxy_submit_button_'+lId).disabled = false;
        alert('Введите текст сообщения');
      }
    }
    function mboxResponse(e){
      if(e.responseJSON.error==3) alert("${message(code:'inbox.bron.error.baddate')}");
      else if (e.responseJSON.error==7) alert("${g.message(code:"inbox.bron.error.disabledinvoice")}");
      else if (e.responseJSON.error==6) alert("${g.message(code:"inbox.bron.error.nopayway")}");
      else if (e.responseJSON.error==5) alert("${message(code:'inbox.bron.error.paywaylimit')}");
      else if (e.responseJSON.error==4) alert("${message(code:'inbox.bron.error.laterinvoice')}");
      else if (e.responseJSON.error==2) alert("${message(code:'inbox.bron.error.nooferta')}");
      else if (e.responseJSON.error==1) alert("${message(code:'inbox.bron.error.bderror')}");
      else if (!e.responseJSON.error&&e.responseJSON.where) location.assign(e.responseJSON.where);
      else location.reload(true);
    }
    function calculateMailPrice(){
      $('home_id_mail').value=$('home_id_spec').value;
      $('date_start_year_mail').value=$('date_start_year').value;
      $('date_start_month_mail').value=$('date_start_month').value;
      $('date_start_day_mail').value=$('date_start_day').value;
      $('date_end_year_mail').value=$('date_end_year').value;
      $('date_end_month_mail').value=$('date_end_month').value;
      $('date_end_day_mail').value=$('date_end_day').value;
      $('homeperson_id_mail').value=$('homeperson_id').value;
      $('calculateMailPriceFormSubmit').click();
    }
    function processMailResponse(e){
      defsumma = (e.responseJSON.result/e.responseJSON.valutarate).toFixed(0);
      if (e.responseJSON.result&&$('pricing_unit').value!=1) {
        $('price').value=(e.responseJSON.result/e.responseJSON.valutarate).toFixed(0);
      } else {
        $('price').value = arrVSum[arrVId.indexOf($('home_id_spec').value)];
      }
    }
    </g:javascript>
    <g:setupObserve function='clickPaginate' id='ajax_wrap'/>
    <style type="text/css"> 
      .slides_container { width: 730px; height: 30px; overflow: hidden; position: relative; }
      .slides_control { position: relative; }
      .slides_control .slide { position: absolute; top: 0px; left: 0px; z-index: 0; display: none; }
      .slides_control .slide.current { display: block; }      
      .slides_control .slide .txt { width: 700px; height: 30px; padding: 0 10px 0 20px; text-align: justify; }
      .slides_control .slide.current .txt p { font-weight: bold; color: #FF530D; }      
      #slider .pagination { width: 730px; }
      #slider .pagination li { float: left; width: 146px; }      
      #slider .pagination li a { text-align: center; text-decoration: none; color: #333; font-weight: bold }
      #slider .circle { margin-left: 57px; line-height: 28px !important; min-height: 28px !important; min-width: 28px !important; border-radius: 28px !important; padding: 0px !important; }
      #slider .pagination li .circle { background: #fff !important; filter: none !important; color: #333 !important; border-color: #333 !important; }
      #slider .pagination li.current, .pagination li.past { display: block }      
      #slider .pagination li.current .circle {
        color: #fff !important;
      	border-color: #eb470b !important;        
        background: #eb470b !important;
        background: linear-gradient(top, #fe520d, #eb470b) !important;
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fffe520d',endColorstr='#ffeb470b',GradientType=0) !important;
      }
      #slider .pagination li.past .circle {	
        color: #fff !important;
        border-color: #459a00 !important;
        background: #4aa400 !important;
        background: linear-gradient(top, #5ccd00, #4aa400) !important;	
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff5ccd00',endColorstr='#ff4aa400',GradientType=0) !important;
      }
      #slider .pagination li .button-glossy:before { background: none !important }
      .caption { padding: 5px 10px } 
    </style>
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" class="search s0">
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>                                
              </td>
              <td colspan="3" class="rent s0 padd20">
                <div class="float" style="width:485px">
                  <h1 style="margin-left:0px">${message(code:'personal.mbox.with')} ${(sender?.client_id != user?.client_id) ? sender?.nickname : recipient?.nickname}</h1>
                  <p style="margin:0">
                  <g:if test="${(msg.user_id==user.id&&msg?.mtext)||(msg.user_id!=user.id&&msg?.mtextowner)}">
                    <font color="#fff"><g:shortString text="${msg.user_id==user.id?msg?.mtext:msg?.mtextowner}" length="100"/></font><br/>
                  </g:if>
                    <font color="#8B99A5">${message(code:'inbox.view.last')}: ${String.format('%td.%<tm.%<tY %<tH:%<tM',msg?.moddate)}</font>
                  </p>
                </div>
                <ul class="verifications-list col" style="width:220px">
                  <li class="verifications-list-item" style="border-bottom:1px dotted #3F5765">                    
                    <div class="verifications-legend"></div>
                    <span class="label" style="color:#8B99A5">${message(code:'inbox.view.status.answer')}</span><br/>
                    <span class="counts"><b>
                    <g:if test="${msg?.answertype_id>0}">
                      <g:each in="${answertype}" var="item"><g:if test="${msg?.answertype_id == item?.id}">
                      <font style="color:${item?.color}">${item['name_mbox'+context?.lang]}</font>
                      </g:if></g:each>
                    </g:if><g:else>${message(code:'personal.request')}</g:else>                    
                    </b></span>
                  </li>                
                  <li class="verifications-list-item" style="border-bottom:none">                    
                    <div class="verifications-legend"></div>
                    <span class="label" style="color:#8B99A5">${message(code:'inbox.view.status.bron')}</span><br/>
                    <span class="counts">
                    <g:if test="${msg?.modstatus!=0}">
                      <g:each in="${modstatus}" var="item" status="i">  
                        <g:if test="${msg?.modstatus==item?.modstatus}">
                      <b style="color:${item?.color}">${item['name'+context?.lang]}</b>
                        </g:if>
                      </g:each>                      
                    </g:if><g:else>
                      <b style="color:red">${message(code:'inbox.list.deleted')}</b>
                    </g:else>                    
                    </b></span>
                  </li>                
                </ul>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <b><g:link controller="inbox" action="index" class="to-parent" base="${context?.mainserverURL_lang}">${message(code:'inbox.view.tolist')}</g:link></b>
                <ul class="collapsable_filters" style="width:240px">			    
                  <li class="search_filter" id="user_container">
                    <a class="filter_toggle" href="javascript:void(0);" onclick="toggleFilter('user_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" onclick="toggleFilter('user_container');">
                      ${message(code:(sender?.client_id != user?.client_id)?'personal.owner':'personal.guest')}                                  
                    </a>
                    <div class="search_filter_content">
                      <div class="description" style="margin-bottom:10px">
                        <h2 class="title">
                          <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+((sender?.client_id != user?.client_id)?sender?.id:recipient?.id)]}" title="${message(code:'common.view_profile')}" style="color:#3F5765">
                            ${(sender?.client_id != user?.client_id) ? sender?.nickname : recipient?.nickname}
                          </g:link>
                        </h2>                      
                        <p style="margin:0">${message(code:'personal.member')} ${String.format('%td.%<tm.%<tY',(sender?.client_id != user?.client_id) ? sender?.inputdate : recipient?.inputdate)}</p>
                      <g:if test="${sender?.client_id != user?.client_id || recipient?.picture}">
                        <div class="thumbnail shadow" style="height:auto">
                        <g:if test="${sender?.client_id != user?.client_id}"> 
                          <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+sender?.id]}">
                            <img width="200" alt="${sender?.nickname}" title="${sender?.nickname}" border="0" 
                              src="${(sender?.picture && !sender?.is_external)?imageurl:''}${(sender?.picture)?sender?.picture:resource(dir:'images',file:'user-default-picture.png')}" />       
                          </g:link>
                        </g:if><g:else>
                          <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+recipient?.id]}">
                            <img width="200" alt="${recipient?.nickname}" title="${recipient?.nickname}" border="0" 
                              src="${(recipient?.picture && !recipient?.is_external)?imageurl:''}${(recipient?.picture)?recipient?.picture:resource(dir:'images',file:'user-default-picture.png')}" />       
                          </g:link>
                        </g:else>
                        </div>
                      </g:if>
                      </div>
                      <!--<g:link class="button-glossy orange maxi" mapping="pView" params="${[uid:'id'+((sender?.client_id != user?.client_id)?sender?.id:recipient?.id)]}">Посмотреть</g:link>-->
                    </div>                                            
                  </li>
                  <li class="search_filter" id="verification_container">
                    <a class="filter_toggle" href="javascript:void(0);" onclick="toggleFilter('verification_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" onclick="toggleFilter('verification_container');">${message(code:'personal.verification')}</a>
                    <div class="search_filter_content" style="padding: 15px 0px 15px 5px">
                      <ul class="verifications-list">
                      <g:if test="${sender?.client_id != user?.client_id}">
                        <li class="verifications-list-item ${(sender?.modstatus==0)?'verifications-none':''}">
                          <div class="verifications-icon ${(sender?.modstatus==0)?'none':''}"></div>
                          <div class="verifications-legend ${sender?.provider}"></div>
                          <span class="label">${message(code:'personal.connect')} ${sender?.provider}</span><br/>
                          <span class="counts">${message(code:(sender?.modstatus==1)?'personal.confirmed':'personal.notconfirmed')}</span>
                        </li>
                      </g:if><g:else>
                        <li class="verifications-list-item ${(recipient?.modstatus==0)?'verifications-none':''}">
                          <div class="verifications-icon ${(recipient?.modstatus==0)?'none':''}"></div>
                          <div class="verifications-legend ${recipient?.provider}"></div>
                          <span class="label">${message(code:'personal.connect')} ${recipient?.provider}</span><br/>
                          <span class="counts">${message(code:(recipient?.modstatus==1)?'personal.confirmed':'personal.notconfirmed')}</span>
                        </li>                      
                      </g:else>
                      <g:if test="${(msg?.is_telperm==1) && (((sender?.client_id != user?.client_id) && sender?.tel)||
                        ((sender?.client_id == user?.client_id) && recipient?.tel))}">
                        <li class="verifications-list-item ${((sender?.client_id != user?.client_id)&&(sender?.is_telcheck==0))||
                          ((sender?.client_id == user?.client_id)&&(recipient?.is_telcheck==0))?'verifications-none':''}">
                          <div class="verifications-icon ${((sender?.client_id != user?.client_id)&&(sender?.is_telcheck==0))||
                            ((sender?.client_id == user?.client_id)&&(recipient?.is_telcheck==0))?'none':''}"></div>
                          <div class="verifications-legend phone"></div>
                          <span class="label">${message(code:'personal.phone')} ${message(code:((sender?.client_id != user?.client_id)&&(sender?.is_telcheck==1))||
                            ((sender?.client_id == user?.client_id)&&(recipient?.is_telcheck==1))?'personal.confirmed.he':'personal.notconfirmed.he')}</span>
                          <br/>
                          <span class="counts tel" nowrap>
                          <g:if test="${msg?.modstatus>3 || !isHideContact}">
                            <g:if test="${(sender?.client_id != user?.client_id) && sender?.tel}">${sender?.tel}</g:if>
                            <g:elseif test="${(sender?.client_id == user?.client_id) && recipient?.tel}">${recipient?.tel}</g:elseif>
                          </g:if><g:else>
                            <g:if test="${(sender?.client_id != user?.client_id) && sender?.tel}">${(sender?.tel).split('\\)')[0]})</g:if>
                            <g:elseif test="${(sender?.client_id == user?.client_id) && recipient?.tel}">${(recipient?.tel).split('\\)')[0]})</g:elseif>
                            <g:each in="${(sender?.client_id != user?.client_id)?sender?.tel.split('\\)')[1][0..-3]:recipient?.tel.split('\\)')[1][0..-3]}" var="item" status="i">
                            <span class="p">&nbsp;&nbsp;</span>
                            </g:each>
                            <g:if test="${(sender?.client_id != user?.client_id) && sender?.tel}">${(sender?.tel)[-2,-1]}</g:if>
                            <g:elseif test="${(sender?.client_id == user?.client_id) && recipient?.tel}">${(recipient?.tel)[-2,-1]}</g:elseif>
                          </g:else>
                          </span>
                        </li>
                      </g:if>
                      <g:if test="${(((sender?.client_id != user?.client_id) && sender?.skype)||
                        ((sender?.client_id == user?.client_id) && recipient?.skype))}">
                        <li class="verifications-list-item">
                          <div class="verifications-icon"></div>
                          <div class="verifications-legend skype"></div>
                          <span class="label">skype</span><br/>
                          <span class="counts tel">${(sender?.client_id != user?.client_id) ? sender?.skype : recipient?.skype}</span>
                        </li>
                      </g:if>
                      </ul>
                    </div>
                  </li>
                <g:if test="${sender?.client_id != user?.client_id}">  
                  <li class="search_filter" id="bron_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('bron_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('bron_container');">${message(code:ownerClient?.is_reserve&&ownerClient?.reserve_id!=3?'common.secure_payments':'account.booking')}</a>
                    <div class="search_filter_content" style="padding:5px 0 15px">
                    <g:if test="${ownerClient?.is_reserve&&ownerClient?.reserve_id!=3}">
                      <p class="clearfix"><img class="thumbnail userpic" src="${resource(dir:'images/anonses',file:'bron.png')}" alt="${message(code:'common.secure_payment_via_website')}" title="${message(code:'common.secure_payment_via_website')}" hspace="12" align="left" style="border:none" />
                        <font color="#333">${message(code:'profile.secure.info')}</font>
                        <g:link class="tooltip" controller="index" action="oferta" fragment="use_6" target="_blank" title="${message(code:'common.secure_payments')}" base="${context?.absolute_lang}"><img alt="${message(code:'common.secure_payments')}" title="${message(code:'common.secure_payments')}" src="${resource(dir:'images',file:'question.png')}" border="0" style="margin-bottom:-2px" /></g:link></p>
                    </g:if>
                      <p>${message(code:'detail.owner_scheme')} - <b><font color="#333">${reserve['name'+context?.lang]}</font></b><br/>
                        <g:rawHtml>${reserve['description'+context?.lang]}</g:rawHtml>
                        <g:if test="${reserve?.id==3}"><br/><b><font color="#333">${ownerClient['settlprocedure'+context?.lang]}</font></b></g:if>
                      </p>                    
                      <ul class="clearfix pay-list mini">                        
                      <g:each in="${payway}" var="it" status="i">
                        <li class="float"><span class="icons ${it.icon}" title="${it['name'+context?.lang]}"></span></li>
                      </g:each>
                      </ul>
                      <p style="font-size:11px;line-height:15px;color:#333" align="justify">
                        <g:rawHtml>${message(code:'inbox.view.secure.info')}</g:rawHtml>
                      </p>
                    </div>
                  </li>                
                </g:if>
                </ul>
              </td>
              <td colspan="3" valign="top">
                <div class="body shadow">
                <g:if test="${Client.get(home.client_id)?.is_reserve}">
                  <div id="slider" class="paddtop relative" align="center">
                    <div style="margin-bottom:-15px;width:585px;height:1px;background-color:#757575;z-index:1"></div>
                    <ul class="pagination">
                    <g:each in="${howto}" var="item" status="i">
                      <li>
                        <a rel="${i}" href="#">
                          <div class="button-glossy circle" alt="${item?.description?:''}">${item?.number}</div>
                          <div class="caption">${item['name'+context?.lang]}</div>
                        </a>                        
                      </li>
                    </g:each>
                    </ul>
                    <div class="slides_container">
                      <div class="slides_control">
                      <g:each in="${howto}" var="item">
                        <div class="slide"> 
                          <div class="txt">                            
                            <g:rawHtml>${item['description'+context?.lang]?:''}</g:rawHtml>                            
                          </div>                          
                        </div>                          
                      </g:each>
                      </div>
                    </div>                    
                  </div>
                </g:if>
                  <div class="padd20" <g:if test="${Client.get(home.client_id)?.is_reserve==0}">style="padding-top:5px"</g:if>>
                    <div style="padding-right:15px;text-align:justify">  
                    <g:rawHtml><g:if test="${recipient?.client_id != user?.client_id}">
                      <g:if test="${(msg?.answertype_id == 0) && !(msg?.modstatus < 0)}">${infotext['itext'+context?.lang]?:''}</g:if>
                      <g:elseif test="${(msg?.answertype_id in [1,2]) && (msg?.modstatus != 4)}">${infotext['itext2'+context?.lang]?:''}</g:elseif>
                      <g:else><br/></g:else>
                    </g:if><g:else><br/></g:else></g:rawHtml>
                    </div>
                  <g:if test="${flash?.save_error}">
                    <div class="notice" style="color:red"></div>
                  </g:if>                
                    <div id="bubble_top" class="bubble-container clearfix col" style="width:96%">
                      <div class="header_container" style="height:110px">
                        <div class="thumbnail shadow" style="margin-top:4px;width:140px;height:98px">                                          
                          <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}">
                            <img width="140" height="98" alt="${home?.name}" title="${home?.name}" border="0" 
                              src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}">
                          </g:link>
                        </div>
                        <div class="padd10 float" style="width:340px">
                          <h2 class="title" style="margin:5px 0"><g:shortString text="${home?.name}" length="30"/></h2>
                          <ul class="details-list detail t clearfix" style="margin:0">
                            <li class="details-list-item location">
                              <span class="icons"></span>
                              ${(sender?.client_id != user?.client_id) ? home?.shortaddress : home?.address }
                            </li>
                            <li class="details-list-item room_type">
                              <span class="icons"></span>
                            <g:each in="${hometype}" var="item"><g:if test="${item?.id==home?.hometype_id}">
                              ${item['name'+context?.lang]}
                            </g:if></g:each>
                            </li>                            
                            <li class="details-list-item dates">
                              <span class="icons"></span>
                              ${String.format('%td.%<tm.%<tY',msg?.date_start)} - ${String.format('%td.%<tm.%<tY',msg?.date_end)}
                            </li>
                            <li class="details-list-item person_capacity">
                              <span class="icons"></span>
                            <g:each in="${homeperson}" var="item"><g:if test="${item?.id==msg?.homeperson_id}">
                              ${item['name'+context?.lang]}
                            </g:if></g:each>
                            </li>
                          </ul>                                                  
                        </div>
                        <div class="contprn col relative" align="right" style="width:180px">
                          <div class="details">
                            <span class="ss_price b-rub" style="height:35px">${displayPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                            <font color="${msg?.answertype_id==2?'red':'gray'}">${message(code:msg?.answertype_id==2?'waiting.special':'personal.cost')}</font>                      
                          </div>
                        <g:if test="${sender?.client_id==user?.client_id && msg?.answertype_id==0 && reminder!=0}">
                          <div class="reminder">
                            ${message(code:'inbox.list.expires')}<br/> 
                            <font color="#FF530D" size="+1">${reminder}</font>
                          </div>
                        </g:if>
                          <div class="details" id="bron_summa" style="position:absolute;bottom:-10px;left:-80px;display:none">
                            <span class="ss_price b-rub" style="height:32px;color:green">${totalPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                            <font color="gray">${message(code:'inbox.bron.summa')}</font>
                          </div>                        
                          <input type="button" id="bron_button_head" class="button-glossy green col" onclick="$('bron_button').click()" value="${message(code:Client.findById(sender?.client_id)?.is_reserve?'inbox.view.book_and_pay':'button.booking')}" style="margin-top:10px;width:auto;display:none" />
                        </div>                        
                      </div>
                      <div style="clear:both">                        
                  <g:if test="${recipient?.client_id != user?.client_id}">
                    <g:if test="${!(msg.modstatus<0)}">
                      <g:if test="${((home?.modstatus?:0)==-2) || ((home?.modstatus?:0)==-3)}">
                        <ul class="option-list">
                          <li class="option-list-item negative open">
                            <h4><a href="javascript:void(0);">${message(code:'inbox.view.deteted')}!</a></h4>
                            <ul class="action-list">
                              <li class="action-list-item">
                                <div class="drawer clearfix" style="margin-top:-1px">
                                  <p>${message(code:'inbox.view.deteted.info')}</p>
                                </div>
                              </li>
                            </ul>                              
                          </li>
                        </ul>
                      </g:if><g:else>
                        <g:formRemote name="message_form" url="${[action:'addanswer',id:msg?.id]}" onSuccess="mboxAnswerResponse(e)">
                          <input type="hidden" value="${home?.id}" name="home_id"/>			  
                          <ul id="option-list" class="option-list">
                            <g:each in="${answergroup}" var="group" status="g">            
                            <li class="option-list-item ${group?.icon}${(g==0 || (g==2 && (msg?.answertype_id in [7,8,9] || msg?.modstatus in [3,4])))?' open':''}" 
                              <g:if test="${g==0 && (msg?.answertype_id in [7,8,9] || msg?.modstatus in [3,4] || !iscanoffer)}">style="display:none"</g:if>
                              <g:elseif test="${g==1}">style="display: ${(iscandecline)?'block':'none'}"</g:elseif>
                              <g:elseif test="${g==2&&!(msg?.modstatus in [3,4])&&iscanoffer}">style="display: none"</g:elseif>>
                              <h4><a href="javascript:void(0);">${group['name'+context?.lang]}</a></h4>
                              <ul class="action-list" style="display: ${(g==0  || (g==2 && (msg?.answertype_id in [7,8,9] || msg?.modstatus in [3,4])))?'block':'none'}">
                            <g:each in="${answertype}" var="atype">
                              <g:if test="${atype?.answergroup_id==group?.id}">
                                <li class="action-list-item" 
                                  <g:if test="${atype?.id==1}">style="display: ${iscandecline?'block':'none'}"</g:if>
                                  <g:elseif test="${atype?.id==2}">style="display: ${(msg?.modstatus != 4)?'block':'none'}"</g:elseif>>
                                  <label class="action">
                                    <input type="radio" name="answertype_id" value="${atype?.id}"/>
                                    ${atype['name'+context?.lang]}
                                    <g:if test="${atype?.helptext}">
                                    <a class="tooltip" href="javascript:void(0);" title="${atype['helptext'+context?.lang]}">${message(code:'inbox.view.what')}</a>
                                    </g:if>
                                  </label>                                
                                  <div class="drawer clearfix" style="display:none">
                                    <p class="description">${atype['description'+context?.lang]}</p>
                                    <div id="warning_${atype?.id}" class="notice err" style="margin:0!important;color:red;display:none"></div>                                  
                                    <fieldset class="${atype?.id==2?'available-special-offer':atype?.id==4?'unavailable-partial':''}" <g:if test="${!(atype.id in 1..2)}">style="margin-bottom:0"</g:if>>
                                    <g:if test="${atype?.id==2}">
                                      <div class="overlay"></div>
                                      <div class="input-row">
                                        <label for="home_id_spec">${message(code:userHomes.size()>1?'inbox.view.housing':'server.housing')}</label>
                                        <select id="home_id_spec" name="home_id_spec" onchange="changePrice(this.value);calculateMailPrice();" style="width:520px">
                                        <g:each in="${userHomes}" var="item" status="i">
                                          <option id="home_id_option_${i}" <g:if test="${item?.id==home?.id}">selected="selected"</g:if> value="${item?.id}">
                                            ${item?.name} (${item?.address})
                                          </option>
                                        </g:each>
                                        </select>
                                      </div>
                                      <div class="input-row">
                                        <label for="date_start">${message(code:'common.date_from')}</label>
                                        <calendar:datePicker id="date_start" name="date_start" value="${msg?.date_start}" dateFormat="%d-%m-%Y" onChange="calculateMailPrice();"/>
                                      </div>
                                      <div class="input-row">
                                        <label for="date_end">${message(code:'common.date_to')}</label>
                                        <calendar:datePicker id="date_end" name="date_end" value="${msg?.date_end}" dateFormat="%d-%m-%Y" onChange="calculateMailPrice();"/>
                                      </div>									
                                      <div class="input-row">
                                        <label for="homeperson_id">${message(code:'common.guests')}</label>
                                        <select id="homeperson_id" name="homeperson_id" onChange="calculateMailPrice();">
                                        <g:each in="${homeperson}" var="item" status="i">            
                                          <option id="homeperson_id_option_${i}" <g:if test="${item?.id==msg?.homeperson_id}">selected="selected"</g:if> value="${item?.id}">
                                            ${item?.kol}
                                          </option>
                                        </g:each>
                                        </select>
                                      </div>
                                      <div class="input-row">
                                        <label for="price">${message(code:'personal.cost')}</label>
                                        <select id="pricing_unit" name="pricing_unit" onchange="calculateMailPrice();">
                                          <option value="0">${message(code:'list.full_period')}</option>
                                          <option value="1">${message(code:'list.per_night')}</option>
                                        </select>
                                        <input id="price" type="text" value="${Math.round(msg?.price_rub/cpecofferRates)}" size="10" name="price">
                                        <span id="valuta" class="currency"><g:rawHtml>${Valuta.get(home?.valuta_id)?.symbol}</g:rawHtml></span>
                                        <!--span class="price-per-night padd10">&mdash;&nbsp; <b>${message(code:'inbox.view.fullcost')}</b> <a class="tooltip" href="javascript:void(0)" title="${message(code:'inbox.view.fullcost.alt')}"><img alt="${message(code:'inbox.view.fullcost.alt')}" src="${resource(dir:'images',file:'question.png')}" valign="absmiddle" border="0"/></a></span-->
                                        <span class="cleaning-fee">(${message(code:'inbox.view.fee')})</span>
                                        <span class="new-spinner"></span>
                                      </div><br/>
                                    </g:if><g:elseif test="${atype?.id==4}">                                    
                                      <div class="input-row">
                                        <label for="rule_minday_id">${message(code:'detail.minday_rent')}</label>
                                        <select id="rule_minday_id" name="rule_minday_id">
                                        <g:each in="${minday}" var="item" status="i">            
                                          <option id="rule_minday_id_option_${i}" <g:if test="${item?.id==home?.rule_minday_id}">selected="selected"</g:if> value="${item?.id}">
                                            ${item['name'+context?.lang]}
                                          </option>
                                        </g:each>
                                        </select>
                                      </div>
                                      <div class="input-row">
                                        <label for="rule_maxday_id">${message(code:'detail.maxday_rent')}</label>
                                        <select id="rule_maxday_id" name="rule_maxday_id">                                       
                                        <g:each in="${maxday}" var="item" status="i">            
                                          <option id="rule_maxday_id_option_${i}" <g:if test="${item?.id==home?.rule_maxday_id}">selected="selected"</g:if> value="${item?.id}">
                                            ${item['name'+context?.lang]}
                                          </option>
                                        </g:each>
                                        </select>
                                      </div>
                                      <p class="nights-error">${message(code:'inbox.view.match')}</p><br/>
                                    </g:elseif>
                                      <textarea id="message_inquiry_${atype?.id}" onkeydown="textLimit(this.id);" onkeyup="textLimit(this.id);" name="message" style="width:97%" placeholder="${message(code:'inbox.view.additmessage')}"></textarea><br/>
                                      <input type="submit" id="msg_submit_button" value="${message(code:'button.send')}" style="display:none" />
                                    <g:if test="${(atype.id in 1..2) && !ownerClient?.is_offeradmit}">
                                      <b style="color:#FF530D"><label for="confirmdogovor_${atype?.id}">
                                        <input type="checkbox" id="confirmdogovor_${atype?.id}" name="confirmdogovor_${atype?.id}" value="1"/>
                                        <g:link controller="account" action="offerprint" base="${context?.mainserverURL_lang}" target="_blank">${message(code:'payout.offer.accept')}</g:link>
                                      </label></b>
                                    </g:if>                                  
                                      <input id="proxy_submit_button_${atype?.id}" class="button-glossy orange" type="button" onclick="this.disabled=true;testMessage(${atype?.id});" value="${message(code:atype.id in [1,2]?'button.approve':'detail.send_message')}" />                                      
                                    </fieldset>
                                  <g:if test="${atype.id in 1..2}">
                                    <p class="description">${message(code:'payout.settlement')}: <b>${ownerClient['settlprocedure'+context?.lang]?:message(code:'payout.settlement.default')}</b> <g:link class="tooltip" controller="account" action="payout" title="${message(code:'inbox.view.settlement')}" base="${context?.mainserverURL_lang}"><img alt="${message(code:'inbox.view.settlement')}" src="${resource(dir:'images',file:'question.png')}" valign="absmiddle" border="0"/></g:link></p>    
                                  </g:if>
                                  </div>
                                </li>
                              </g:if>
                            </g:each>
                              </ul>
                            </li>                                                       
                          </g:each>
                          </ul>                        
                        </g:formRemote>
                      </g:else>  
                    </g:if><g:else>
                        <ul class="option-list">
                          <li class="option-list-item negative open">
                            <h4><a href="javascript:void(0);">${message(code:'inbox.view.close')}</a></h4>
                            <ul class="action-list">
                              <li class="action-list-item">
                                <div class="drawer clearfix" style="margin-top:-1px">
                                  <p>${message(code:msg.modstatus==-101?'inbox.view.close.you':'inbox.view.close.guest')}.</p>
                                </div>
                              </li>
                            </ul>
                          </li>
                        </ul>
                    </g:else>
                  </g:if><g:else>
                    <g:if test="${!(msg?.modstatus<0)}">
                      <g:if test="${((home?.modstatus?:0)==-2) || ((home?.modstatus?:0)==-3)}">
                        <ul class="option-list">
                          <li class="option-list-item negative open">
                            <h4><a href="javascript:void(0);">${message(code:'inbox.view.deteted')}!</a></h4>
                            <ul class="action-list">
                              <li class="action-list-item">
                                <div class="drawer clearfix" style="margin-top:-1px">
                                  <p>${message(code:'inbox.view.deteted.info')}</p>
                                </div>
                              </li>
                            </ul>
                          </li>
                        </ul>                         
                      </g:if><g:else>
                        <g:formRemote name="answer_form" url="${[controller:'inbox',action:'addanswer',id:msg?.id]}" onSuccess="location.reload(true)">                      
                          <input type="hidden" name="answertype_id" value="0"/>
                          <ul class="option-list">
                            <li class="option-list-item neutral open">
                              <h4><a href="javascript:void(0);">${message(code:'inbox.view.discussion')}</a></h4>                            
                              <ul class="action-list">
                                <li class="action-list-item">
                                  <div class="drawer clearfix" style="margin-top:-1px">                                  
                                    <textarea id="message_inquiry_" onkeydown="textLimit(this.id);" onkeyup="textLimit(this.id);" placeholder="${message(code:'inbox.view.discussion.info')}" class="required" name="message" style="width:98%"></textarea><br/>
                                    <input type="submit" id="msg_submit_button" value="${message(code:'button.send')}" style="display:none">
                                    <input id="proxy_submit_button_" class="button-glossy orange" type="button" onclick="this.disabled=true;testMessage();" value="${message(code:'detail.send_message')}"/>
                                  </div>
                                </li>
                              </ul>
                            </li>                        
                          </ul>
                        </g:formRemote>
                      </g:else>                        
                    </g:if><g:else>
                        <ul class="option-list">
                          <li class="option-list-item negative open">
                            <h4><a href="javascript:void(0);">${message(code:'inbox.view.close')}</a></h4>
                            <ul class="action-list">
                              <li class="action-list-item">
                                <div class="drawer clearfix" style="margin-top:-1px">
                                  <p>${message(code:msg.modstatus==-100?'inbox.view.close.you':'inbox.view.close.owner')}</p>
                                </div>
                              </li>
                            </ul>
                          </li>
                        </ul>
                    </g:else>
                  </g:else>
                    </div>                  
                  </div>
                </div>
                <div class="search-container paddtop" style="clear:both">
                  <div id="search_body">
                    <g:formRemote name="messagesForm" url="[action:'msg_list']" update="[success:'results']">
                      <input type="hidden" name="id" value="${msg?.id}">
                      <input type="submit" id="submit_button" value="Найти" style="display:none">          	            
                    </g:formRemote>
                    <div id="results"></div>
                  </div>
                </div>                
                <div id="loader" style="top: 380px; left: 400px; display: none;position:absolute">
                  <img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/>
                </div>
                <a id="bron_link" style="display:none"></a>
                <div id="bron_lbox" class="new-modal" style="height:auto;display:none;width:${!ispaypossible?470:970}px">
                </div>
                <g:formRemote id="calculatePriceForm" name="calculateMailPriceForm" method="post" url="[ controller:'home', action:'calculatePrice']" onSuccess="processMailResponse(e)">
                  <input type="hidden" id="home_id_mail" name="home_id" value=""/>
                  <input type="hidden" id="date_start_year_mail" name="date_start_year" value=""/>
                  <input type="hidden" id="date_start_month_mail" name="date_start_month" value=""/>
                  <input type="hidden" id="date_start_day_mail" name="date_start_day" value=""/>
                  <input type="hidden" id="date_end_year_mail" name="date_end_year" value=""/>
                  <input type="hidden" id="date_end_month_mail" name="date_end_month" value=""/>
                  <input type="hidden" id="date_end_day_mail" name="date_end_day" value=""/>
                  <input type="hidden" id="homeperson_id_mail" name="homeperson_id" value=""/>
                  <input type="hidden" name="modifier" value="1"/>
                  <input type="submit" class="button-glossy orange" id="calculateMailPriceFormSubmit" style="display: none" value="Рассчитать"/>
                </g:formRemote>
              </td>
            </tr>                  

  </body>
</html>
