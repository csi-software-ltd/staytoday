<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />
  </head>
  <body>
      <h1 role="heading">${infotext['header'+context?.lang]?:''}</h1>
      <div data-role="content" style="padding:0 0">               
        <ul id="wallet_output" data-role="listview" data-split-icon="arrow-r" style="margin:10px 0 0 0">
        <g:if test="${records}">
          <li data-role="divider" data-theme="a" style="padding:0 0 0 10px;height:40px">
            <span class="count float">
              <span>${message(code:'mobile.found')}</span><span id="ads_count">${count}</span>
            </span>
            <span class="pagination col">
              <g:paginate uri="/favorites" prev="&lt;" next="&gt;" maxsteps="1"
                max="5" total="${count}" />            
            </span>
          </li>        
          <g:each in="${records}" var="home"> 
          <li data-icon="false">
            <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:Home.get(home.hid)?.linkname]}" title="${message(code:'common.description_and_photos')} ${home?.name}">
              <div class="ui-li-thumb relative" style="margin-right:15px">
                <img class="thumbnail userpic shadow" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0" />
              <g:if test="${home?.curdiscount}">
                <div class="discount-container">                
                  <div class="discount-price">
                    <g:if test="${home?.curdiscount==2}">-${home?.hotdiscounttext}</g:if>
                    <g:elseif test="${home?.curdiscount==1}">-${home?.longdiscounttext}</g:elseif>
                    <g:else>-${home?.hotdiscounttext ? home.hotdiscounttext : home.longdiscounttext}</g:else>
                  </div>
                </div>
              </g:if><g:if test="${home?.is_fiesta}">
                <div class="fiesta-container" title="${message(code:'common.party')}"></div>
              </g:if>              
              </div>
              <h2><span class="name"><g:shortString text="${home?.name}" length="16"/></span>
                <span name="star_${home?.hid}" class="star_icon_container" title="${message(code:'common.add_to_favourite')}">
                  <div name="star_icon_${home?.hid}" onclick="addtofavorite(${home?.hid})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.hid)}">starred</g:if>"></div>
                </span>
              </h2>
              <ul class="ui-li-desc details-list">
                <li class="details-list-item location"><span class="icons"></span><g:shortString text="${home?.shortaddress}" length="50"/></li>
                <li class="details-list-item room_type"><span class="icons"></span>${Hometype.get(home?.hometype_id)['name'+context?.lang]}</li>              
              </ul>
              <div class="ui-li-desc">
                <ul class="ui-li-desc details-list float" style="margin-top:5px">
                  <li class="details-list-item person_capacity"><span class="icons"></span>${Homeperson.get(home?.homeperson_id)['name'+context?.lang]}</li>
                  <li class="details-list-item review_count"><span class="icons"></span>${home?.nref?:0} ${message(code:'common.reviews')}</li>
                </ul>
                <p class="ui-li-aside" style="margin-top:15px">
                  <span class="b-rub">${Math.round(home?.price / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                </p>
              </div>
            </g:link>
          </li>
          </g:each>
        </g:if><g:else>
          <li class="ui-li ui-li-static">${message(code:'mobile.favorites.notfound')}</li>
        </g:else>
        </ul>
      </div>
  </body>
</html>
