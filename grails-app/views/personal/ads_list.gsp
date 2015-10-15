<script type="text/javascript">
  $('ads_count').update(${data?.count});
<g:if test="${data?.count > 0}">
  $('search_body').show();
</g:if>
</script>
<div id="ajax_wrap">
  <div id="results_header" class="results_header">    
    <span class="pagination col">
      <g:paginate controller="personal" action="${actionName}" prev="&lt;&lt;" next="&gt;&gt;"
        max="20" total="${data?.count}" params="${inrequest}"/> 
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="{data?.records}">
  <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
  <g:each in="${data?.records}" var="home" status="i">
    <tr>
      <td onmouseout="this.removeClassName('selected')" onmouseover="this.addClassName('selected')">
        <div class="relative clearfix" style="margin-bottom:10px">
          <div class="thumbnail shadow">
            <g:link controller="personal" action="adsoverview" id="${home.id}" base="${context.sequreServerURL}">
              <img width="200" height="140" src="${(home?.mainpicture)?imageurl+'t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0"/>
            </g:link>
          </div>
          <div class="photo_item_details list">
          <g:if test="${home.pricestandard}">
            <span class="ss_price b-rub" style="padding-left:40px !important">
              <g:if test="${home.pricestandard}">${home.pricestandard} <g:rawHtml>${Valuta.get(home.valuta_id)?.symbol?:''}</g:rawHtml></g:if>              
            </span>
            <span class="ss_period">${message(code:'list.per_night')}</span>              
          </g:if><g:else>
            <span class="ss_period padd20">${message(code:'list.not_available')}</span>              
          </g:else>
          </div>
          <div class="description">
            <h2 class="title padd20 clearfix">
              <g:link class="name" controller="personal" action="adsoverview" id="${home.id}" base="${context.sequreServerURL}">${home?.name}</g:link>
            </h2>
            <ul class="details-list clearfix">
              <li class="details-list-item location">
                <span class="icons"></span>
                ${home?.address}
              </li>
              <li class="details-list-item room_type">
                <span class="icons"></span>
              <g:each in="${hometype}" var="item"><g:if test="${item?.id==home?.hometype_id}">
                ${item['name'+context?.lang]}
              </g:if></g:each>
              </li>              
              <li class="details-list-item person_capacity">
                <span class="icons"></span>
              <g:each in="${homeperson}" var="item"><g:if test="${item?.id==home?.homeperson_id}">
                ${item['name'+context?.lang]}
              </g:if></g:each>
              </li>
              <li class="details-list-item review_count">
                <span class="icons"></span>
                ${home?.nref} ${message(code:'common.reviews')}
              </li>
            </ul>
          </div>
          <div class="message-item col">
          <g:each in="${modstatus}" var="item"><g:if test="${item?.modstatus==home.modstatus}">
            <div class="message-text" style="background-image: url(${resource(dir:'images',file: item?.icon+'.png')});">
              <b>${item['name'+context?.lang]}</b>
            </div>
          </g:if></g:each>
          </div>           
        </div>
        <div style="height:40px">
          <div class="dview">
            <span class="actions float">
              <span class="action_button">
                <g:link class="icon edit" controller="personal" action="adsoverview" id="${home.id}" base="${context.sequreServerURL}">${message(code:'ads.edit')}</g:link>
              </span>
              <span class="action_button">
                <g:link class="icon view" mapping="${'hView'+context?.lang}" params="${[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}">${message(code:'ads.view')}</g:link>
              </span>
              <span class="action_button">
                <g:link class="icon calendar" controller="personal" action="calendar" id="${home.id}" base="${context?.mainserverURL_lang}">${message(code:'ads.view.calendar')}</g:link>
              </span>
            </span>
          </div>          
        </div>
      </td>
    </tr>
    <g:if test="${(i+1)!=data.records.size()}"> 
    <tr><td class="dashed">&nbsp;</td></tr>
    </g:if>
  </g:each>
  </table>
  </g:if>
</div>
