<script type="text/javascript">
  $('ads_count').update(${count});
</script>
<div id="ajax_wrap">
<g:if test="${count > inrequest?.max}">
  <div class="results_header">    
    <span class="pagination">
      <g:paginate action="search_table" maxsteps="7" prev="&lt;" next="&gt;"
        max="${inrequest?.max}" total="${count}"/> 
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
<g:if test="${records}">
  <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
  <g:each in="${records}" var="home" status="i">    
    <tr>
      <td onmouseout="this.removeClassName('selected')" onmouseover="this.addClassName('selected')">
        <div class="hlisting offer-rent relative">                                      
          <div class="item housing thumbnail shadow">
            <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}" target="_blank" title="${home?.name}">
              <img class="photo" width="200" height="140" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0" />
            </g:link>
          <g:if test="${home.isHaveDiscountAdv(home)}">              
            <div class="discount-container">                
              <div class="discount-price">
                -${home?.csiGetDiscountText()}
              </div>
            </div>
          </g:if><g:if test="${home?.is_fiesta}">
            <div class="fiesta-container" title="${message(code:'common.party')}"></div>
          </g:if><g:if test="${Client.get(home?.client_id)?.is_reserve}">
            <ul class="services-list list">
              <li class="services-list-item bron"><span class="icons active" title="${message(code:'common.booking_through_site')}"></span></li>
            </ul>
          </g:if>
          </div>
          <div class="photo_item_details list">
            <span class="price ss_price b-rub" style="padding-left:40px !important">
              ${Math.round(home?.price / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
            </span>
            <span class="ss_period">${message(code:'list.per_night')}</span>              
          </div>          
          <div class="fn title">
            <g:link class="url name" mapping="${'hView'+context?.lang}" params="${[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}" rel="self bookmark">${home?.name}</g:link>
            <a href="javascript:void(0)" id="star_${home?.id}" class="star_icon_container" title="${message(code:'common.add_to_favourite')}">
              <div id="star_icon_${home?.hid}" onclick="addtofavourite(${home?.hid})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.hid)}">starred</g:if>"></div>
            </a>
          </div>          
          <div class="description">
            <ul class="details-list">
              <li class="details-list-item location clearfix">
                <span class="icons"></span>
                <g:shortString text="${home?.shortaddress}" length="56"/>
              </li>
              <li class="details-list-item room_type clearfix">
                <span class="icons"></span>
              <g:each in="${hometype}" var="item"><g:if test="${item?.id==home?.hometype_id}">
                ${item['name'+context?.lang]}
              </g:if></g:each>
              </li>              
              <li class="details-list-item person_capacity clearfix">
                <span class="icons"></span>
              <g:each in="${homeperson}" var="item"><g:if test="${item?.id==home?.homeperson_id}">
                ${item['name'+context?.lang]}
              </g:if></g:each>
              </li>
              <li class="details-list-item review_count clearfix">
                <span class="icons"></span>
                ${home?.nref?:0} ${message(code:'common.reviews')}
              </li>                      
            </ul>
          </div>
          <div class="lister fn">${User.findWhere(client_id:home?.client_id).nickname}</div>
          <ul class="services-list col">
          <g:each in="${homeoption}"><g:if test="${it.icon}">
            <li class="services-list-item ${it.icon}">
              <span class="icons ${home[it.fieldname]?'active':'passive'}" title="${it['name'+context?.lang]}"></span>                
            </li>              
          </g:if></g:each>
          </ul>
        </div>
      </td>
    </tr>
    <g:if test="${(i+1)!=records.size()}"> 
    <tr><td class="dashed">&nbsp;</td></tr>
    </g:if>    
  </g:each> 
  </table>
</g:if>                 
</div>
