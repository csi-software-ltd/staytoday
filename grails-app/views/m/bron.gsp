<html>
  <head>
    <title>${home?.name}</title>    
    <meta name="description" content="${home?.description?:''}" />
    <meta name="layout" content="m" />
    <script type="text/javascript">
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
      jQuery(document).ready(function(){        
        jQuery('.action-list input:radio').change(function(){
          var m=jQuery(this),l=m.parents('.action-list-item');
          if(m.is(':checked')){
            jQuery('.action-list-item').not(l).removeClass('checked').find('.drawer').slideUp(200,function(){
              jQuery(this).hide()
            });
            l.addClass('checked').find('div.drawer').slideDown(200);
          }
        });
      });
    </script>
  </head>
  <body>
    <div data-role="page">
      <div data-role="header" data-position="fixed">
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" href="${createLink(controller:'m',action:'mbox',id:mbox.id)}" title="Назад"></a></li>
            <li class="divider-vertical"></li>           
            <li class="text" role="heading">${Infotext.findByControllerAndAction('inbox','bron')['title'+context?.lang]}</li>
          <g:if test="${message?.modstatus>3 || !isHideContact}">
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
        <g:formRemote name="bron_form" url="${[action:'setMboxBron',id:mbox?.id]}" onSuccess="mboxResponse(e)">
        <div class="new-modal ui-corner-all ui-popup ui-body-c" id="bron_lbox">
          <h2 class="clearfix ui-corner-top ui-header ui-bar-f">Вы бронируете</h2>
          <div class="segment nopadding ui-content">            
            <table width="100%" cellpadding="5" cellspacing="5" border="0">
              <tr>
                <td>
                  <img class="thumbnail userpic shadow" src="${urlphoto+home.client_id+'/t_'+home.mainpicture}" alt="${home.name}" border="0"/>
                </td>
                <td valign="middle">
                  <h3 style="margin:0">${home?.name}</h3>
                  <font color="#000">${home?.address}</font>                  
                </td>
              </tr>
              <tr>
                <td colspan="2" align="right">
                  <span class="b-rub">${displayPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                  <font color="gray">${message(code:"personal.cost")}</font>
                </td>
              </tr>
              <tr>
                <td colspan="2" style="border-bottom: 1px dotted silver">
                  <div style="float:left;font-size:13px;line-height:18px">
                    ${message(code:"common.date_from").capitalize()}:&nbsp;&nbsp; <b><font color="#000">${date_start}</font></b>&nbsp;&nbsp;${message(code:'ads.price.rule_timein')}:&nbsp;&nbsp;<b><font color="#000">${timein}</font></b><br/>
                    ${message(code:"common.date_to").capitalize()}: <b><font color="#000">${date_end}</font></b>&nbsp;&nbsp;${message(code:'ads.price.rule_timeout')}:&nbsp;&nbsp;<b><font color="#000">${timeout}</font></b><br/>
                    ${message(code:"common.guests").capitalize()}: <b><font color="#000">${homeperson['name'+context?.lang]}</font></b>
                  </div>
                </td>
              </tr>
            <g:if test="${cancellation}">
              <tr>
                <td colspan="2" style="border-bottom: 1px dotted silver">
                  <div style="font-size:13px;line-height:18px">
                  <g:if test="${reserve?.id!=3}">
                    ${message(code:'ads.price.cancellation').capitalize()}: <a class="ui-link" href="${createLink(mapping:'hView',params:[country:Country.get(home?.country_id)?.urlname,city:home?.city,linkname:home?.linkname],base:context?.mobileURL,fragment:'cancellation_page')}">${cancellation.name}</a><br/>
                    <i style="font-size:11px">${cancellation.fullname}</i><br/>
                  </g:if>
                    ${message(code:'ads.price.rule').capitalize()}: <a href="${createLink(mapping:'hView',params:[country:Country.get(home?.country_id)?.urlname,city:home?.city,linkname:home?.linkname],base:context?.mobileURL,fragment:'homerule_page')}">${message(code:'inbox.bron.rules.view')}</a>
                  </div>
                </td>
              </tr>              
            </g:if>
              <tr>
                <td colspan="2" style="border-bottom: 1px dotted silver">               
                  <div style="font-size:13px;line-height:18px">
                    ${message(code:'common.owner').capitalize()}: <b>${own?.nickname}</b><br/>
                  <g:if test="${mbox.is_telperm==1}">
                    ${message(code:'inbox.bron.contactphone')}: <b><font color="#000">${own?.tel}</font></b><br/>
                  </g:if>
                    ${message(code:'booking.date')}: <b><font color="#000">${moddate}</font></b>
                  </div>
                </td>
              </tr>
            <g:if test="${ispaypossible}"> 
              <tr>
                <td colspan="2" style="border-bottom: 1px dotted silver">
                  <h3 style="margin:0">Порядок расчетов</h3>
                  <p>${message(code:'detail.owner_scheme')} - <b><font color="#000">${reserve['name'+context?.lang]}</font></b>.<br/>
                    <g:rawHtml>${reserve['description'+context?.lang]}</g:rawHtml>
                  <g:if test="${reserve?.id==3}">
                    <br/><b><g:rawHtml>${ownerClient?.settlprocedure}</g:rawHtml></b>
                  </g:if></p>
                </td>
              </tr>
              <tr>
                <td colspan="2">
                  <g:rawHtml>${Infotext.findByControllerAndAction('inbox','bron')['itext'+context?.lang]?:''}</g:rawHtml>
                  
                  <div id="option-list" class="option-list" data-role="collapsible-set">
                    <div class="option-list-item neutral open" data-collapsed="false" data-role="collapsible" data-expanded-icon="arrow-d" data-collapsed-icon="arrow-r" data-content-theme="d">                    
                      <h3>Способ оплаты</h3>
                      <ul class="action-list">
                      <g:each in="${payway}" var="it" status="i">
                        <li class="action-list-item">
                          <label class="action" data-inline="true" data-corners="false">
                            <input type="radio" id="payway_${it.id}" name="payway" value="${it?.id}" <g:if test="${(it.is_invoice && (mbox.date_start - new Date() < invoicelife))||(totalPrice>(it.limit?:Long.MAX_VALUE))}">disabled</g:if> />
                            ${it['name'+context?.lang]}<br/>
                            <small class="fee">${message(code:'payout.fee').toLowerCase()} - ${it.fee}%</small>                       
                          </label>
                          <div data-role="content" class="drawer ui-body ui-body-c" style="display:none">
                            <g:rawHtml>${it['description'+context?.lang]}</g:rawHtml>
                          </div>
                        </li>
                      </g:each>
                    </div>
                  </div>                  
                  
                  <p><label for="oferta">
                    <input type="checkbox" name="oferta" value="1" />
                    <a href="${createLink(uri:'/contract',base:context?.mobileURL_lang)}">${message(code:'inbox.bron.offer.confirm')}</a>
                  </label></p>
                  <g:rawHtml>${Infotext.findByControllerAndAction('inbox','bron')['itext2'+context?.lang]?:''}</g:rawHtml>              
                </td>
              </tr>
            </g:if>
            </table>
          </div>
          <div class="segment buttons">
            <div align="center">
              <span class="b-rub">${totalPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
              <font color="gray">${message(code:'inbox.bron.summa')}</font>
            </div>
            <a data-role="button" data-theme="f" href="#" onclick="jQuery('#bron_form_submit').click()">${message(code:ispaypossible?'button.pay':'button.booking')}</a>
          </div>
        </div>
        <input type="hidden" name="mboxrec_id" value="${mboxRec.id}" />
        <div style="display:none">
          <input type="submit" id="bron_form_submit" />
        </div>
        </g:formRemote>
      </div>
      <div data-role="footer" data-position="fixed" id="footer_inbox_msg_bron">
      </div>
    </div>    
  </body>
</html>
