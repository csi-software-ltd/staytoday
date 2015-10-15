<script type="text/javascript">
  jQuery(document).ready(function(){
    jQuery(".pay-list input:radio").change(function(){
      var m=jQuery(this),l=m.parents(".pay-list li");
      if(m.is(":checked")){
        jQuery(".pay-list li").not(l).removeClass("checked").find(".answer").slideUp(200,function(){
          jQuery(this).hide()
        });
        l.addClass("checked").find(".answer").slideDown(200);        
      }      
    });
  });
</script>
<g:formRemote name="bron_form" url="${[action:'setMboxBron',id:mbox?.id]}" onSuccess="mboxResponse(e)">
  <div class="new-modal float" style="width:470px;${ispaypossible?'border-right:1px dotted silver;border-radius:0px':''}">
    <h2 class="clearfix">${infotext['header'+context?.lang]?:''}</h2>
    <div id="bron_lbox_segment" class="segment nopadding" style="height:auto">
      <div id="bron_lbox_container" class="lightbox_filter_container" style="height:auto;padding:0px">
        <table width="100%" cellpadding="5" cellspacing="5" border="0">
          <tr>
            <td width="115">
              <div class="thumbnail shadow">
                <img width="200" height="140" src="${urlphoto}${home.client_id}/t_${home.mainpicture}" alt="${home.name}" border="0"/>
              </div>
            </td>
            <td valign="middle">
              <div style="float:left;width:220px">
                <h2 style="margin-bottom:5px"><a href="${(context?.is_dev)?'/'+context?.appname:''}/home/view/${home.linkname}">${home.name}</a></h2>
                <small><b><font color="#000">${message(code:"booking.address")}</font></b>: ${home.address}</small>
              </div>
              <div class="contprn col" align="right" style="width:180px">
                <div class="details">
                  <span class="ss_price b-rub">${displayPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                  <font color="gray">${message(code:"personal.cost")}</font>
                </div>
              </div>
            </td>
          </tr>
          <tr>
            <td colspan="2" style="border-bottom: 1px dotted silver">
              <div style="float:left;font-size:13px;line-height:18px">
                ${message(code:"common.date_from").capitalize()}:&nbsp;&nbsp; <b><font color="#000">${date_start}</font></b> &nbsp;&nbsp;&nbsp;&nbsp;${message(code:'ads.price.rule_timein')}:&nbsp; ${timein}<br/>
                ${message(code:"common.date_to").capitalize()}: <b><font color="#000">${date_end}</font></b> &nbsp;&nbsp;&nbsp;&nbsp;${message(code:'ads.price.rule_timeout')}: ${timeout}<br/>
                ${message(code:"common.guests").capitalize()}: <b><font color="#000">${homeperson['name'+context?.lang]}</font></b>
              </div>
            </td>
          </tr>
        <g:if test="${cancellation}">
          <tr>
            <td colspan="2" style="padding-bottom:15px;border-bottom: 1px dotted silver">
              <div style="font-size:13px;line-height:18px">
              <g:if test="${reserve?.id!=3}">
                ${message(code:'ads.price.cancellation').capitalize()}: <a target="_blank" href="${(context?.is_dev)?'/'+context?.appname:''}/home/cancellation/#${cancellation.shortlink}">${cancellation['name'+context?.lang]}</a><br/>
                <i style="font-size:11px">${cancellation['fullname'+context?.lang]}</i><br/>
              </g:if>
                ${message(code:'ads.price.rule').capitalize()}: <a target="_blank" href="${(context?.is_dev)?'/'+context?.appname:''}/home/view/${home.linkname}#homerule">${message(code:'inbox.bron.rules.view')}</a>
              </div>
            </td>
          </tr>
        </g:if>
          <tr>
            <td colspan="2" style="padding-bottom:15px;${ispaypossible?'border-bottom: 1px dotted silver':''}">
              <div style="font-size:13px;line-height:18px">
                ${message(code:'common.owner').capitalize()}: <b><a href="${(context?.is_dev)?'/'+context?.appname:''}/id${ownerUser.id}">${ownerUser.nickname}</a></b><br/>
                ${message(code:'booking.date')}: <b><font color="#000">${String.format('%td/%<tm/%<tY %<tH:%<tM',new Date())}</font></b>
              </div>
            </td>
          </tr>
        </table>
      <g:if test="${ispaypossible}"> 
        <div style="padding: 10px">
          <h2 class="clearfix">${infotext['promotext1'+context?.lang]?:''}</h2>
          <p>${message(code:'detail.owner_scheme')} - <b><font color="#000">${reserve['name'+context?.lang]}</font></b>.<br/>
          <g:rawHtml>${reserve['description'+context?.lang]}</g:rawHtml><g:if test="${reserve?.id==3}"><br/><b><g:rawHtml>${ownerClient?.settlprocedure}</g:rawHtml></b></g:if></p>
          <div class="contprn col" align="right" style="width:180px">
            <div class="details">
              <span class="ss_price b-rub" style="color:#FF530D">${totalPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
              <font color="gray">${message(code:'inbox.bron.summa')}</font>
            </div>
          </div>          
        </div>
      </g:if>
      </div>
    </div>
  </div>
<g:if test="${ispaypossible}">      
  <div class="new-modal col" style="width:499px">
    <h2 class="clearfix" style="background:none">${infotext['promotext2'+context?.lang]?:''}</h2>    
    <div id="bron_lbox_segment_r" class="segment nopadding" style="height:auto;padding:0px">
      <div id="bron_lbox_container_r" class="lightbox_filter_container" style="overflow-y:scroll;height:585px;padding:0 15px">
        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
        <ul class="pay-list">               
        <g:each in="${payway}" var="it" status="i">
          <li class="clearfix">
            <g:radio id="payway_${it.id}" name="payway" disabled="${((it.is_invoice&&(mbox.date_start - new Date() < invoicelife))||totalPrice>(it.limit?:Long.MAX_VALUE))?'true':'false'}" value="${it.id}" />
            <span class="icons ${it.icon}" title="${it.name}"></span>
            <label for="payway_${it.id}">${it['name'+context?.lang]}<br/>
              <span class="fee">${message(code:'payout.fee').toLowerCase()} - ${it.fee}%</span>
            </label>
            <div class="answer">
              <g:rawHtml>${it['description'+context?.lang]}</g:rawHtml>
            </div>
          </li>
        </g:each>
        </ul>
        <p><b><label for="oferta">
          <input type="checkbox" name="oferta" value="1" />
          <g:link controller="index" action="contract" target="_blank">${message(code:'inbox.bron.offer.confirm')}</g:link>
        </label></b></p>          
        <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
      </div>
    </div>
  </div>
</g:if>
  <div class="segment buttons" style="width:${ispaypossible?940:440}px;clear:both">
    <b style="color:#FF530D"><input type="checkbox" name="close" value="1" id="closemboxes" />
    <label for="closemboxes">${g.message(code:"inbox.bron.closeanothermboxes.message")}</label></b>
    <input type="submit" class="button-glossy orange" value="${message(code:ispaypossible?'button.pay':'button.booking')}" style="float:right"/>
  </div>
  <input type="hidden" name="mboxrec_id" value="${mboxRec.id}" />
</g:formRemote>
