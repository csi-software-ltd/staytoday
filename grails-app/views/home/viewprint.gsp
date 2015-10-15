<html>
  <head>
    <title>${home?.name?:''}</title>      
    <meta name="description" content="${home?.description?:''}" />       
    <meta name="layout" content="print" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'galleria.classic.css')}">    
    <g:javascript>    
      var iX=${home?.x?:Region.get(home?.region_id?:0)?.x?:0}, iY=${home?.y?:Region.get(home?.region_id?:0)?.y?:0},
          iScale=${home?.x?13:Region.get(home?.region_id?:0)?.scale?:0}, ADDRESS_SEARCH_ZOOM=13,
          map=null, geocoder=null, flag_marker_move=false, placemark={};      
    function initialize(){
      Yandex();
      onPaymentPeriodChange();
      setTimeout('window.print()', 1000);
    }	
    function Yandex() {
      ymaps.ready(function() {            
        map = new ymaps.Map("map_small_canvas",{ center:[iY/100000,iX/100000], zoom: iScale, behaviors: ["default"] });
          
        var circle = new ymaps.Placemark([iY/100000, iX/100000],{}, {
            draggable: false,
            hasBalloon: false,
            zIndexHover:0,
            hasHint: false,
            iconImageHref:"${resource(dir:'images',file:'circle.png')}",
            iconImageSize: [184, 184],
            iconImageOffset:[-112, -60]
        });                             
        map.geoObjects.add(circle);
      });
    }    
    function onPaymentPeriodChange() {
      var price_current = 0;
      var price_day = ${price_day};
      var price_weekend = ${price_weekend};
      var price_week = ${price_week};
      var price_month = ${price_month};
      var periodID = 'per_day';
      if (${havePrice}==1) {      
        periodID = $('payment_period').options[$('payment_period').selectedIndex].value;      
        switch(periodID){
          case 'per_day': {
            price_current = price_day; break;
          } 
          case 'per_week':{
            if(price_week == 0){
              if(price_weekend == 0)
                price_current = price_day * 7;
              else
                price_current = price_day * 5 + price_weekend * 2;
            } else {
              price_current = price_week;
            } break;
          }
          case 'per_month':{
            price_current = (price_month == 0) ? price_day * 30 : price_month; break;
          }          
        }
        price_current = (price_current / ${valutaRates}).toFixed(0)
        $('price_amount').update(price_current + "&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>");         
      } else {
        $('price_amount').update('<small>${message(code:'detail.price_not_set')}</small>');        
      }
    }
    </g:javascript>
    <style type="text/css">
      .title a.name[href] { white-space: normal; height: auto }
    </style>
  </head>  
  <body onload="initialize()">
            <tr>              
              <td width="980" colspan="2" class="rent" style="background:#fff;padding-bottom:10px">
                <h1 style="color:#3F5765">${home?.name}</h1>
                <div class="details paddtop col">                
                  <span class="ss_price b-rub" id="price_amount">
                    ${home.pricestandard_rub}<g:rawHtml>${valutaSym}</g:rawHtml>
                  </span>                
                  <select id="payment_period" name="payment_period" onchange="onPaymentPeriodChange()">
                    <option value="per_day" selected>${message(code:'common.day')}</option>
                    <option value="per_week">${message(code:'common.week')}</option>
                    <option value="per_month">${message(code:'common.month')}</option>
                  </select>                   
                </div>
                <ul class="details-list padd20">
                  <li class="details-list-item location">
                    <span class="icons"></span>
                    ${home?.shortaddress}
                  </li>
                  <li class="details-list-item">
                    <span><g:join in="${metro.collect{it['name'+context?.lang]}}" delimiter=", "/></span>
                  </li>   
                </ul>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <ul class="collapsable_filters">			    
                  <li class="search_filter">
                    <b class="filter_header">${message(code:'viewprint.location')}</b>        
                    <div class="search_filter_content">
                      <div id="map_wrapper" class="shadow">
                        <div id="search_map" style="position:relative;overflow:hidden">
                          <div id="map_small_canvas"></div>
                        </div>						
                      </div>
                    </div>
                  </li>
                  <li class="search_filter">
                    <b class="filter_header">${message(code:'viewprint.characteristics')}</b>
                    <ul class="search_filter_content" style="padding: 15px 0px 15px 5px">
                      <li class="clearfix">
                        <span class="facet_count">${Hometype.get(home.hometype_id)['name'+context?.lang]}</span>
                        <label>${message(code:'common.home_type')}</label>                      
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">${Homeperson.get(home.homeperson_id)['name'+context?.lang]}</span>
                        <label>${message(code:'viewprint.accommodates')}</label>                        
                      </li>                     
                      <li class="clearfix">
                        <span class="facet_count">${Homeroom.get(home.homeroom_id?:1)?.kol}</span>
                        <label>${message(code:'common.rooms')}</label>                          
                      </li>                      
                      <li class="clearfix">
                        <span class="facet_count">${home?.bed?:'-'}</span>
                        <label>${message(code:'common.beds')}</label>                      
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">${Homebath.get(home.homebath_id?:1)?.kol}</span>
                        <label>${message(code:'common.bathrooms')}</label>                      
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">${(home?.is_kitchen==1)?message(code:'detail.yes'):message(code:'detail.no')}</span>
                        <label>${message(code:'ads.services.additional.kitchen')}</label>                      
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">
                        <g:each in="${country}" var="item"><g:if test="${item?.id==home?.country_id}">
                          ${item['name'+context?.lang]}
                        </g:if></g:each>                          
                        </span>                      
                        <label>${message(code:'ads.country')}</label>
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">${home?.area?:''} м&sup2;</span>
                        <label>${message(code:'common.space')}</label>
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">
                        <g:if test="${deposit}">${deposit} <g:rawHtml>${valutaSym}</g:rawHtml></g:if>
                        <g:else>${message(code:'detail.not_needed')}</g:else>
                        </span>
                        <label>${message(code:'ads.price.deposit')}</label>
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">
                        <g:each in="${minday}" var="item"><g:if test="${item?.id==home?.rule_minday_id}">
                          ${item['name'+context?.lang]}
                        </g:if></g:each>
                        </span>
                        <label>${message(code:'detail.minday_rent')}</label>
                      </li>                  
                      <li class="clearfix">
                        <span class="facet_count">
                        <g:each in="${maxday}" var="item"><g:if test="${item?.id==home?.rule_maxday_id}">
                          ${item['name'+context?.lang]}
                        </g:if></g:each>
                        </span>
                        <label>${message(code:'detail.maxday_rent')}</label>
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">
                        <g:each in="${timein}" var="item"><g:if test="${item?.id==home?.rule_timein_id}">
                          ${item['name'+context?.lang]}
                        </g:if></g:each>                          
                        </span>
                        <label>${message(code:'ads.price.rule_timein')}</label>
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">
                        <g:each in="${timeout}" var="item"><g:if test="${item?.id==home?.rule_timeout_id}">
                          ${item['name'+context?.lang]}
                        </g:if></g:each>                          
                        </span>
                        <label>${message(code:'ads.price.rule_timeout')}</label>
                      </li>
                      <li class="clearfix">
                        <span class="facet_count">
                        <g:each in="${cancellation}" var="item"><g:if test="${item?.id==home?.rule_cancellation_id}">                        
                          ${item['name'+context?.lang]}
                        </g:if></g:each>
                        </span>                    
                        <label>${message(code:'ads.price.cancellation')}</label>
                      </li>
                    </ul>
                  </li>
                  <li class="search_filter">                    
                  <g:each in="${ownerUsers}" var="ownerUser">                    
                    <b class="filter_header">
                      <g:if test="${ownerUser.is_agent}">${message(code:'personal.agent')}</g:if><g:else>${message(code:'common.owner')}</g:else>
                    </b>
                    <div class="search_filter_content">
                      <div class="description">
                        <h2 class="title clearfix">${ownerUser?.nickname}</h2>                      
                        <div class="thumbnail shadow" style="height:auto">
                          <img width="200" alt="${ownerUser?.nickname}" alt="${ownerUser?.nickname}" border="0" 
                            src="${(ownerUser?.picture && !ownerUser.is_external)?imageurl:''}${(ownerUser?.picture)?ownerUser?.picture:resource(dir:'images',file:'user-default-picture.png')}" />
                        </div>
                      </div>
                    </div>
                  </g:each>                                
                  </li>
                <g:if test="${anotherHomes}">             
                  <li class="search_filter">                  
                    <b class="filter_header">${message(code:'viewprint.other_ads')}</b>
                    <ul class="search_filter_content" style="padding:15px 6px">
                    <g:each in="${anotherHomes}" var="home" status="i">              
                      <li class="clearfix">
                        <div class="relative">
                          <div class="thumbnail shadow" style="width:220px;height:160px">
                            <img width="220" height="160" src="${(home?.mainpicture)?photourl+'t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0"/>                            
                          </div>
                        <g:if test="${anotherPrices[i]!=0}">
                          <div class="photo_item_details">
                            <span class="ss_price b-rub">              
                              ${anotherPrices[i]}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>
                            </span>                            
                          </div>
                        </g:if>
                        </div>
                        <div class="description">
                          <h2 class="title"><g:shortString text="${home?.name}" length="18"/></h2>
                          <ul class="details-list">              
                            <li class="details-list-item location">
                              <g:if test="${anotherDistances[i]!=0}">${anotherDistances[i]} ${message(code:'common.km_from_choosen')}</g:if>
                              <g:else>${message(code:'common.same_place')}</g:else>
                            </li>
                          </ul>                          
                        </div> 
                      </li>
                    </g:each>                    
                    </ul>
                  </li>
                </g:if>
                </ul>
              </td>
              <td valign="top" class="body">                
                <h2 class="padd20">${message(code:'viewprint.photo_of_object')}</h2>
                <div class="slideshow">
                  <div class="galleria-container">                
                    <img id="big_photo" src="${photourl}${homephoto[0]?.picture}" alt="${home?.name}" />                      
                    <g:if test="${homephoto[0]?.ptext}">
                    <div class="galleria-info">
                      <div class="galleria-info-text">
                        <div class="galleria-info-description" id="big_photo_title">
                          ${homephoto[0]?.ptext}
                        </div>
                      </div>
                    </div>              
                    </g:if>
                  </div>
                </div>
                <h2 class="padd20">${message(code:'list.filtr.amenities')}</h2>
                <div style="min-height:400px">
                  <ul class="service" style="margin-left:20px">
                  <g:each in="${homeoption}" var="item" status="i"> 
                    <g:if test="${i < homeoption.size()/2}">
                    <li>
                    <g:if test="${item.fieldname}">
                      <g:if test="${home[item.fieldname]}">
                      <img class="service-icon" src="${resource(dir:'images',file:'enable.png')}" width="17" height="17" title="${message(code:'detail.convenience_/_allowed')}" alt="${message(code:'detail.no_amenity_/_not_allowed')}"/>
                      </g:if><g:else>
                      <img class="service-icon" src="${resource(dir:'images',file:'disable.png')}" width="17" height="17" title="${message(code:'detail.no_amenity_/_not_allowed')}" alt="${message(code:'detail.no_amenity_/_not_allowed')}"/>
                      </g:else>
                    </g:if>
                      <p>${item['name'+context?.lang]}<g:if test="${item.comments}">
                        <a class="tooltip" title="${item.comments}"><img src="${resource(dir:'images',file:'question.png')}" alt="${message(code:'detail.what_is_it')}"></a>
                      </g:if></p>
                    </li>
                    </g:if>
                  </g:each>
                  </ul>
                  <ul class="service">
                  <g:each in="${homeoption}" var="item" status="i"> 
                    <g:if test="${i >= homeoption.size()/2}">
                    <li>
                    <g:if test="${item.fieldname}">
                      <g:if test="${home[item.fieldname]}">
                      <img class="service-icon" src="${resource(dir:'images',file:'enable.png')}" width="17" height="17" title="${message(code:'detail.convenience_/_allowed')}" alt="${message(code:'detail.no_amenity_/_not_allowed')}"/>
                      </g:if><g:else>
                      <img class="service-icon" src="${resource(dir:'images',file:'disable.png')}" width="17" height="17" title="${message(code:'detail.no_amenity_/_not_allowed')}" alt="${message(code:'detail.no_amenity_/_not_allowed')}"/>
                      </g:else>
                    </g:if>
                      <p>${item['name'+context?.lang]}<g:if test="${item.comments}">                          
                        <a class="tooltip" title="${item.comments}"><img src="${resource(dir:'images',file:'question.png')}" alt="${message(code:'detail.what_is_it')}"></a>
                      </g:if></p>
                    </li>
                    </g:if>
                  </g:each>
                  </ul>
                </div>
                <h2 class="padd20">${message(code:'viewprint.description_of_object')}</h2>
                <div style="padding:0 12px">
                  <ul>
                  <g:if test="${home?.description}">
                    <li class="clearfix">
                      <p class="padd10">${home?.description}</p>
                    </li>
                  </g:if>
                  <g:if test="${home?.homerule}">
                    <li class="clearfix">
                      <h4>${message(code:'viewprint.rules')}</h4>
                      <p>${home?.homerule?home?.homerule:''}</p>                    
                    </li>
                  </g:if>
                  <g:if test="${home?.reachpersonal}">
                    <li class="clearfix">
                      <h4>${message(code:'viewprint.get_by_private_transport')}</h4>
                      <p><g:rawHtml>${home?.reachpersonal}</g:rawHtml></p>                    
                    </li>
                  </g:if>
                  <g:if test="${home?.reachpublic}">
                    <li class="clearfix">
                      <h4>${message(code:'viewprint.get_publick_transport')}</h4>
                      <p><g:rawHtml>${home?.reachpublic}</g:rawHtml></p>
                    </li>
                  </g:if>
                  </ul>					
                </div>
                <g:if test="${count}">
                <br/><br/><h2 class="padd20">${message(code:'list.reviews')}</h2>
                <div id="comments">
                  <table width="100%" cellpadding="5" cellspacing="0" border="0">
                  <g:each in="${records}" status="i" var="record">                      
                    <tr>
                      <td valign="top">
                        <div class="float padd10">
                          <div class="thumbnail shadow" style="width:68px;height:68px">
                            <img width="68" height="68" alt="${record.nickname}" title="${record.nickname}" src="${(record.picture && !record.is_external)?imageurl:''}${(record.smallpicture)?record.smallpicture:resource(dir:'images',file:'user-default-picture.png')}">
                          </div>
                          <div style="width:68px">                      
                            <small style="white-space:normal">${record?.nickname}</small><br/>
                            <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',record?.comdate)}</small>
                          </div>
                        </div>
                        <div class="col bubble-container" style="width:600px">                
                          <small>${record.comtext}</small>                      
                        </div>
                      </td>       
                    </tr>
                  </g:each>
                  </table>
                </div>
              </td>
            </tr>
          </g:if>

  </body>
</html>
