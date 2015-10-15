<div id="tableAJAX" class="clearfix">
<g:if test="${records}">
  <div id="carousel" class="carousel slide" align="center">
    <div class="carousel-inner">
    <g:each in="${records}" var="home" status="i">
      <div class="${!i?'active ':''}item">        
        <div class="thumbnail shadow">
          <g:link mapping="${'hView'+context?.lang}" params="${[linkname:home?.linkname,widget:1,country:Country.get(home.country_id)?.urlname,city:home.city]}" target="_blank" title="${home?.name}">
            <img width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0"/>
          </g:link>
        <g:if test="${home?.curdiscount}">
          <div class="discount-container">
            <div class="discount-price">
            <g:if test="${home?.curdiscount==2}">-${home?.hotdiscounttext}</g:if>
            <g:elseif test="${home?.curdiscount==1}">-${home?.longdiscounttext}</g:elseif>
            <g:else>-${home?.hotdiscounttext ? home.hotdiscounttext : home.longdiscounttext}</g:else>
            </div>
          </div>
        </g:if>
          <div class="date-container">
            ${String.format('%td.%<tm.%<tY',home.moddate>(new Date()-30)?home.moddate:new Date()-3)}
          </div>
        </div>
        <div class="photo_item_details">
          <span class="ss_price b-rub">${Math.round(home?.price / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
        </div>
        <div class="description">
          <h2 class="title">            
            <g:link mapping="${'hView'+context?.lang}" class="name" params="${[linkname:home?.linkname,widget:1,country:Country.get(home.country_id)?.urlname,city:home.city]}" target="_blank">
              <g:shortString text="${home?.name}" length="18"/>
            </g:link>
          </h2>
          <ul class="details-list" style="clear:left;width:220px">
            <li class="details-list-item location">
              <g:shortString text="${home?.shortaddress}" length="30"/>
            </li>
          </ul>
        </div>
      </div>
    </g:each>
    </div>
    <a class="carousel-control left" href="#carousel" data-slide="prev">&lsaquo;</a>
    <a class="carousel-control right" href="#carousel" data-slide="next">&rsaquo;</a>
  </div>
  <script type="text/javascript">
  jQuery('.carousel').carousel({
    interval: 4000
  });
  </script>
</g:if><g:else>
  <g:if test="${flash.error}">
  <div id="notice" class="notice">
    <g:if test="${flash.error.contains(1)}">
    <b>Некорректная &laquo;Дата заезда&raquo;</b>
    </g:if>
    <g:if test="${flash.error.contains(2)}">
    <b>Некорректная последовательность параметров &laquo;Дата заезда&raquo; и &laquo;Дата отъезда&raquo;</b>
    </g:if>
  </div>
  </g:if><g:else>
  <div style="padding:12px">
    По Вашему запросу ничего не найдено. Пожалуйста, задайте другие параметры поиска
  </div>
  </g:else>
</g:else>
</div>
