<g:if test="${records}"> 
  <ul data-role="listview" id="ajax_wrap" style="margin:0">
    <li data-role="divider" data-theme="a" style="padding:0 0 0 10px;height:40px">
      <span class="count float">
        <span>${message(code:'mobile.found')}</span><span id="ads_count">${bSearchNear&&count>max?max:count}</span>
      </span>
      <span class="pagination col">
        <g:paginate controller="m" action="search_list" prev="&lt;" next="&gt;" 
          maxsteps="1" omitFirst="${true}" omitLast="${true}" max="5" total="${bSearchNear&&count>max?max:count}" params="${params}"/>
        <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>  
      </span>
    </li>        
  <g:each in="${records}" var="home">   
    <script type="text/javascript">
      jQuery('#map_canvas').gmap('addMarker', { 
        'position': new google.maps.LatLng(${home.y}/100000, ${home.x}/100000), 
        'animation' : google.maps.Animation.DROP, 'bounds':true 
      }).click(function() {                
        jQuery('#map_canvas').gmap('openInfoWindow', { 'content':setBalloonContent()}, this);
      });
      function setBalloonContent(){
        var sText = ${Math.round(home?.price / valutaRates)} + "<g:rawHtml>${valutaSym}</g:rawHtml>";            
        var sBalloon='<div style="width:220px;height:200px">'+
          '<h2><a href="<g:createLink mapping='${'hView'+context?.lang}' params='${[0:0].inject([linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:home.city],linkparams)}' base='${context.mobileURL}'/>" title="${message(code:'common.description_and_photos')} ${home?.name}"><g:shortString text="${home?.name}" length="16" /></a></h2>'+
          '<ul class="details-list clearfix" style="width:100%">'+
          '<li class="details-list-item location"><span class="icons"></span><g:shortString text="${home.shortaddress}" length="30" /></li>'+
          '<li class="details-list-item room_type"><span class="icons"></span>${Hometype.get(home.hometype_id).name}</li>'+
          '<li class="details-list-item person_capacity"><span class="icons"></span>${Homeperson.get(home.homeperson_id).name}</li>'+
          '<li class="details-list-item review_count"><span class="icons"></span>${home?.nref?:0} ${message(code:'common.reviews')}</li>'+    
          '</ul>'+      
          '<img class="thumbnail shadow" src="${home.mainpicture?urlphoto+home.client_id+'/t_'+home.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0" />'+
          '<span class="b-rub col">'+sText+'</span>'+
          '</div>';    
          return sBalloon;
      }           
    </script>          
    <li data-icon="false">
      <g:if test="${home.is_index}"><a href="<g:createLink mapping='${'hView'+context?.lang}' params='${[0:0].inject([linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:home.city],linkparams)}' base='${context.mobileURL}'/>" title="${message(code:'common.description_and_photos')} ${home?.name}"></g:if>
      <g:else><a href="javascript:void(0)" onclick="transit(0,['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id)?.urlname}'],'${[0:0].inject([:],linkparams).collect{it.key+"="+it.value}.join(",")}','','${context.mobileURL.minus('http://')}','${context?.lang}')" title="${message(code:'common.description_and_photos')} ${home?.name}"></g:else>
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
          <span name="star_${home.hid}" class="star_icon_container" title="${message(code:'common.add_to_favourite')}">
            <div name="star_icon_${home.hid}" onclick="addtofavorite(${home.hid})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.hid)}">starred</g:if>"></div>
          </span>
        </h2>
        <ul class="ui-li-desc details-list">
          <li class="details-list-item location"><span class="icons"></span><g:shortString text="${home.shortaddress}" length="50"/></li>
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
      </a>
    </li>
  </g:each>
  </ul>
</g:if><g:else>
  <ul data-role="listview" style="margin:0">
    <li class="ui-li ui-li-static">${message(code:'mobile.ads.notfound')}</li>
  </ul>
</g:else>
