<html>
  <head>
    <g:if test="${home?.city || home?.district || home?.street}"><title>${home?.city.capitalize()?:''}${home?.district?', '+home?.district.capitalize():''}${home?.street?', '+home?.street:''}${home?.homenumber?', '+home?.homenumber:''} - ${message(code:'detail.rent_per_night')}. ${home?.name?:''}</title></g:if>
    <g:else><title>${alikeWhere}, ${message(code:'detail.rent_per_night')}. ${home?.name?:''}</title></g:else>    
    <meta name="description" content="${shortDesc(text:home?.description?:'',length:'170')}" />
    <link rel="canonical" href="${createLink(mapping:homecity?.domain?'hViewDomain':'hView',params:homecity?.domain?[linkname:home?.linkname]:[country:Country.get(home?.country_id)?.urlname,city:homecity?.name?:(context?.lang?homecity_ru:home.city),linkname:home?.linkname],base:homecity?.domain?'http://'+homecity.domain+(context.is_dev?':8080/Arenda':''):context?.mainserverURL)}" />    
    <link rel="alternate" hreflang="ru" href="${createLink(mapping:'hView',params:[country:Country.get(home?.country_id)?.urlname,city:homecity?.name?:(context?.lang?homecity_ru:home.city),linkname:home?.linkname],base:context?.mobileURL)}" />
    <link rel="alternate" hreflang="en" href="${createLink(mapping:'hView_en',params:[country:Country.get(home?.country_id)?.urlname,city:homecity?.name_en?:Tools.transliterate(home?.city,0),linkname:home?.linkname],base:context?.mobileURL)}" />    
    <meta name="layout" content="m" />    
    <script type="text/javascript">
      var days=${days_between?:1}, persons=${inrequest?.homeperson_id?:1}, isNearSearch=${inrequest?.is_near?1:0},
          datePickerDate=new XDate("${formatDate(format:'yyyy-MM-dd',date:inrequest?.date_start?:new Date())}"),
          dateEnd=new XDate("${formatDate(format:'yyyy-MM-dd',date:inrequest?.date_end?:new Date()+1)}"),
          map_center={}, initialLocation=new google.maps.LatLng(${userPoint?.y/100000},${userPoint?.x/100000});
          
      function init(){        
        datePicker('date_start');              
        jQuery('#date_end').val(dateEnd.toString("ddd, d MMM, yyyy","${context?.lang?'en':'ru'}"));
        calkPrice();
        
        map_initialize();
        map_center=jQuery('#map_view_canvas').gmap('get', 'map').getCenter();
        $('route_info').hide(); 
        jQuery('route_info').html('');
        
        jQuery('#map_page').live('pageshow',function(){          
          jQuery('#map_view_canvas').gmap('refresh');           
        });
        
        var sText = ${Math.round(home?.pricestandard_rub / valutaRates)} + "<g:rawHtml>${valutaSym}</g:rawHtml>";            
        var sBalloon='<div style="width:220px;height:200px">'+
                '<h2><g:shortString text="${home?.name}" length="16" /></h2>'+
                '<ul class="details-list clearfix" style="width:100%">'+
                '<li class="details-list-item location"><span class="icons"></span><g:shortString text="${home?.shortaddress}" length="30" /></li>'+
                '<li class="details-list-item room_type"><span class="icons"></span>${Hometype.get(home?.hometype_id)['name'+context?.lang]}</li>'+
                '<li class="details-list-item person_capacity"><span class="icons"></span>${Homeperson.get(home?.homeperson_id)['name'+context?.lang]}</li>'+
                '<li class="details-list-item review_count"><span class="icons"></span>${home?.nref?:0} ${message(code:'common.reviews')}</li>'+    
                '</ul>'+      
                '<img class="thumbnail shadow" src="${home?.mainpicture?urlphoto+home?.client_id+'/t_'+home.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0" />'+
                '<span class="b-rub col">'+sText+'</span>'+
                '</div>';        
        jQuery('#map_view_canvas').gmap('addMarker', { 
          'position': new google.maps.LatLng(${home?.y/100000}, ${home?.x/100000}), 
          'animation' : google.maps.Animation.DROP,         
          'bounds':true
        }).click(function() {                
           jQuery('#map_view_canvas').gmap('openInfoWindow', { 'content': sBalloon }, this);
        });        
        //if(isNearSearch)
          showMap();        
        //jQuery('#map_view_canvas').gmap('refresh');                
      }
      function map_initialize(){     
        var iMapHeight, mapCenterDefault={destination:new google.maps.LatLng(55.75278, 37.62295),zoom:16};        
        iMapHeight = jQuery(window).height()-jQuery('#page_header').height();
        jQuery('#map_view_canvas').gmap({ 
          'center': mapCenterDefault.destination, 
          'zoom': mapCenterDefault.zoom, 
          'mapTypeControl': false,
          'mapTypeId': 'hybrid',
          'navigationControl': true,
          'streetViewControl': false
        });  
        jQuery('#map_view_canvas').css('minHeight',iMapHeight);
        jQuery('#map_view_canvas').gmap('refresh');        
      }      
      function datePicker(sId){
        var picker = jQuery('#'+sId);
        picker.mobipick({
          locale: "${context?.lang?'en':'ru'}",
          intlStdDate: false,
          minDate: (new XDate()),
          date: (new XDate(datePickerDate)),
        });
        picker.on('change',function(){
          var date = jQuery(this).val();
          var dateObject = jQuery(this).mobipick('option','date');
          datePickerDate = dateObject;
          updateDays();
        });      
      }      
      function setMapCenter(){
        jQuery('#map_view_canvas').gmap('option','center',map_center);
        if(isNearSearch)
          showMap();
        jQuery('#map_view_canvas').gmap('refresh');   
      }      
      function showMap(){        
        $('route_info').hide(); 
        jQuery('#route_info').html('');    
        var bounds = new google.maps.LatLngBounds(), 
            markers=jQuery('#map_view_canvas').gmap('get','markers'),
            bFlag=0, aMarkers=[], i=0;
        jQuery.each(markers, function(key, value){
          aMarkers[i]=value;  
          bFlag=1;  
          bounds.extend(value.position);
          value.setVisible(true);          
          i++;
        });
        $('map_view_canvas').show();        
        if(bFlag)
          jQuery('#map_view_canvas').gmap('get','map').fitBounds(bounds);          
        if(isNearSearch){
          var image = new google.maps.MarkerImage(
            "${resource(dir:'images/mobile',file:'image_yah.png')}",
            new google.maps.Size(32,32),
            new google.maps.Point(0,0),
            new google.maps.Point(16,32)
          );      
          var shadow = new google.maps.MarkerImage(
            "${resource(dir:'images/mobile',file:'shadow_yah.png')}",
            new google.maps.Size(52,32),
            new google.maps.Point(0,0),
            new google.maps.Point(16,32)
          );      
          var shape = {
            coord: [5,0,14,1,17,2,28,3,28,4,28,5,29,6,29,7,29,8,29,9,29,10,29,11,29,12,30,13,30,14,30,15,30,16,30,17,30,18,30,19,30,20,29,21,27,22,6,23,6,24,7,25,7,26,7,27,7,28,7,29,7,30,7,31,6,31,5,30,5,29,5,28,5,27,5,26,4,25,4,24,4,23,4,22,4,21,4,20,4,19,4,18,3,17,3,16,3,15,3,14,3,13,3,12,3,11,3,10,3,9,2,8,2,7,2,6,2,5,2,4,2,3,1,2,1,1,1,0,5,0],
            type: 'poly'
          };            
          jQuery('#map_view_canvas').gmap('addMarker',{ 
            'position': initialLocation, 
            'animation': google.maps.Animation.DROP,
            'icon': image,
            'shadow': shadow,
            'shape': shape,
            'bounds': true 
          }).click(function(){                
            jQuery('#map_view_canvas').gmap('openInfoWindow',{'content':'Вы здесь!'},this);
          });  
        }        
        if(aMarkers.length==2){
          jQuery('#map_view_canvas').gmap('displayDirections', {
            'origin': new google.maps.LatLng(aMarkers[0].position.lat(), aMarkers[0].position.lng()), 
            'destination': new google.maps.LatLng(aMarkers[1].position.lat(), aMarkers[1].position.lng()), 
            'travelMode': google.maps.DirectionsTravelMode.DRIVING
          }, {
            'suppressMarkers': true, 
            'panel': document.getElementById('dir_panel') 
          }, function(result, status){
            if (status === 'OK')
              setTimeout(refreshDistanceAndTime,1500);                     
            else
              alert('Маршрут не проложен!');
          });   
        }
        jQuery('#map_view_canvas').gmap('refresh');        
      }
      function refreshDistanceAndTime(){
        var lengthAndTime=jQuery('#dir_panel').find('.adp-summary'); 
        if(lengthAndTime.length){
          jQuery('#route_info').html(lengthAndTime); 
          $('route_info').show();
        }
      }
      var star_id=0;
      function addtofavorite(lId){
        jQuery.mobile.showPageLoadingMsg("b","${message(code:'spinner.message.mobile')}", false);  
        star_id=lId;
        <g:remoteFunction action="selectcompany" onSuccess="setFavorite(e)" onComplete="jQuery.mobile.hidePageLoadingMsg()"
          params="\'id=\'+lId" />
      }         
      function setFavorite(e){
        var bFlag=0;
        var arr=e.responseJSON.wallet;
        for(var i=0;i<arr.size();i++){
          if(star_id==arr[i]){
            $('star_icon_'+star_id).addClassName('starred');
            bFlag=1;
          }
        }
        if(!bFlag)
          $('star_icon_'+star_id).removeClassName("starred");      
        if(arr.length>0){
          $('favorite').removeClassName('no_active');
          $('favorite').href="${createLink(uri:'/favorites',base:context?.mobileURL_lang)}"; 
          $('favorite').parentElement.addClassName(' starred');
        } else {
          $('favorite').addClassName('no_active');
          $('favorite').href='javascript:void(0)';
          $('favorite').parentElement.removeClassName(' starred');
        }
      }
      function dayLess(){
        days--;
        if(days<=1){
          days=1;
          $('date_less').addClassName('ui-disabled');
        }else
          $('date_more').removeClassName('ui-disabled');
        updateDays();  
      }
      function dayMore(){
        days++;
        if(days>=31){
          days=31;
          $('date_more').addClassName('ui-disabled');
        }else   
          $('date_less').removeClassName('ui-disabled');
        updateDays();  
      }
      function updateDays(){
        jQuery('#days').html(days);
        jQuery('#daysh').val(days);        
        if(datePickerDate!=undefined){
          dateEnd = new XDate(datePickerDate);          
          jQuery('#date_end').val(dateEnd.addDays(jQuery('#daysh').val()).toString("ddd, d MMM, yyyy","${context?.lang?'en':'ru'}"));          
          jQuery('#date_starth').val(new XDate(datePickerDate).toString('yyyy-MM-dd'));
          jQuery('#date_endh').val(dateEnd.toString('yyyy-MM-dd'));
          calkPrice();
        }
      }    
      function personLess(){
        persons--;
        if(persons<=1){
          persons=1;
          $('person_less').addClassName('ui-disabled');
        }else 
          $('person_more').removeClassName('ui-disabled');
        updatePersons();  
      }      
      function personMore(){
        persons++;
        if(persons>=16){
          persons=16; 
          $('person_more').addClassName('ui-disabled');
        }else
          $('person_less').removeClassName('ui-disabled');  
        updatePersons();  
      }
      function updatePersons(){
        jQuery('#persons').html(persons);
        jQuery('#personsh').val(persons);
        calkPrice();
      }            
      function calkPrice(){
        jQuery('#calculatePriceOutputText').html('');
        jQuery('#calculatePriceErrorText').html(''); 
        
        var date_start=$('date_starth').value,            
            date_end=$('date_endh').value,  
            persons=$('personsh').value;
        if(!jQuery('#home_id').length)
          return            
        var home_id=$('home_id').value;
        jQuery.mobile.showPageLoadingMsg("b", "${message(code:'spinner.message.mobile')}", false);
        <g:remoteFunction action="calculatePrice" onSuccess="processResponse(e)" onComplete="jQuery.mobile.hidePageLoadingMsg()"
          params="\'date_start=\'+date_start+\'&date_end=\'+date_end+\'&homeperson_id=\'+persons+\'&home_id=\'+home_id" />
      }
      function processResponse(e){
        if(e.responseJSON.error!=undefined){      
          $('calculatePriceError').show();
          $('calculatePriceOutput').hide();
          jQuery('#calculatePriceErrorText').html(e.responseJSON.errorprop);
        }else if (e.responseJSON.result!=undefined) {
          $('calculatePriceOutput').show();
          $('calculatePriceError').hide();
          jQuery('#calculatePriceOutputText').html((e.responseJSON.result/${valutaRates}).toFixed(0) + "&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>");
        }else{
          $('calculatePriceError').hide();
          $('calculatePriceOutput').hide();
        }  
      }
      function sendMessage(){          
        var mtext=$('mtext').value,
            persons=$('personsh').value,
            date_start=$('date_starth').value,
            date_end=$('date_endh').value,
            home_id=$('home_id').value;
        jQuery.mobile.showPageLoadingMsg("b", "${message(code:'spinner.message.mobile')}", false);
        <g:remoteFunction action="addmbox" onSuccess="messageResponse(e)" onComplete="jQuery.mobile.hidePageLoadingMsg()"
          params="\'homeperson_id=\'+persons+\'&mtext=\'+mtext+\'&date_start=\'+date_start+\'&date_end=\'+date_end+\'&id=\'+home_id" />
      }      
      function messageResponse(e){      
        var sErrorMsg='';      
        if(e.responseJSON.mbox_error.size()>0){
          if(e.responseJSON.mbox_error==4){
            sErrorMsg+='${message(code:"inbox.zayavkaresponse.notext.error")}\n';
            $('mtext').addClassName('red');
          }
        }else{
          if(e.responseJSON.error)
            sErrorMsg+=e.responseJSON.errorprop;
          else if(e.responseJSON.result>0){
            $("mtext").value='';
            window.location = "${createLink(uri:'/inbox',base:context?.mobileURL_lang)}";
          }
        }
        if(sErrorMsg.length)
          alert(sErrorMsg);
      }
      function addAnswerComment(){        
        var com_id=$('com_id').value,
            com_text=$('answ_comtext').value;  
        jQuery.mobile.showPageLoadingMsg("b","${message(code:'spinner.message.mobile')}", false);        
         <g:remoteFunction action="addAnswerComment" onSuccess="location.reload(true)" onComplete="jQuery.mobile.hidePageLoadingMsg()"
          params="\'com_id=\'+com_id+\'&answ_comtext=\'+com_text" />       
      }      
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null)
          return;
        new Ajax.Updater(
          { success: $('output') },
          link.href,
          { evalScripts: true, onComplete: function(response) { jQuery('#ajax_wrap').listview(); } }
        );
      }
    </script>
  </head>
  <body onload="init()">
    <div data-role="page" id="detail_page"> 
      <div id="page_header" data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="text" role="heading">
              <a class="icon logo" href="${createLink(uri:''+(home?.city?'?where='+home?.city:'')+(inrequest?.date_start?'&date_start='+formatDate(format:'yyyy-MM-dd',date:inrequest?.date_start):'')+(inrequest?.date_end?'&date_end='+formatDate(format:'yyyy-MM-dd',date:inrequest?.date_end):'')+(inrequest?.homeperson_id?'&homeperson_id='+inrequest?.homeperson_id:'')+(inrequest?.is_near?'&is_near=1':''),base:context?.mobileURL_lang)}" rel="nofollow" title="${message(code:'label.main')}"></a>
            </li>
            <li class="divider-vertical"></li>          
            <li class="button_main">
              <a class="icon search" href="<g:createLink mapping='${'hSearch'+context?.lang}' params='${[where:home?.city,country:Country.get(home?.country_id)?.urlname]+(inrequest?.date_start?[date_start:formatDate(format:'yyyy-MM-dd',date:inrequest?.date_start)]:[:])+(inrequest?.date_end?[date_end:formatDate(format:'yyyy-MM-dd',date:inrequest?.date_end)]:[:])+(inrequest?.homeperson_id?[homeperson_id:inrequest?.homeperson_id]:[:])+(inrequest?.is_near?[is_near:inrequest?.is_near]:[:])}' base='${context?.mobileURL}'/>" title="${message(code:'button.search')}"></a>
            </li>
            <li class="divider-vertical"></li>
            <li class="button_user">
              <g:if test="${user}"><a class="icon logout" href="${createLink(action:'logout',base:context?.mobileURL_lang)}" title="${message(code:'label.logout')}"></a></g:if>
              <g:else><a id="login_link" class="icon login" href="${createLink(uri:'/auth'+(actionName!='auth'?'?from='+actionName:''),base:context?.mobileURL_lang)}" rel="nofollow" title="${message(code:'label.login')}"></a></g:else>
            </li>
            <li class="divider-vertical"></li>
            <li class="button_favorites <g:if test="${(wallet?:[]).size()>0}">starred</g:if>">
              <a id="favorite" class="icon favorite ${!user?'no_active':''}" href="${createLink(uri:'/favorites',base:context?.mobileURL_lang)}" rel="nofollow" title="${message(code:'label.favorite')}"></a>              
            </li>
            <li class="divider-vertical"></li>
            <li class="button_inbox <g:if test="${waiting_unread_count>0}">actively</g:if>">
            <g:if test="${user}">
              <a class="icon inbox" href="${createLink(uri:(msg_unread_count==1?'/mbox':'/inbox')+(msg_unread_count==1?'?id='+msg_unread_id:''),base:context?.mobileURL_lang)}" title="${message(code:'label.inbox')}">
                <g:if test="${msg_unread_count}"><div class="unread_count">${msg_unread_count}</div></g:if>
              </a>
            </g:if>
            </li>            
          </ul>
        </div>        
      </div>
      <div data-role="content">     
        <h2 class="title"><span class="name">${home?.name}</span>
          <a id="star_${home?.id}" class="star_icon_container" title="${message(code:'common.add_to_favourite')}">
            <div id="star_icon_${home.id}" onclick="addtofavorite(${home?.id})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.id)}">starred</g:if>"></div>
          </a>
        </h2>
        <ul class="ui-li-desc details-list">
          <li class="details-list-item location"><span class="icons"></span>${home?.address}</li>
          <li class="details-list-item room_type"><span class="icons"></span>${hometype['name'+context?.lang]}</li>
        </ul>
        <div class="ui-li-desc">
          <ul class="ui-li-desc details-list float" style="margin-top:5px">
            <li class="details-list-item person_capacity"><span class="icons"></span>${homeperson['name'+context?.lang]}</li>
            <li class="details-list-item review_count"><span class="icons"></span>${home?.nref?:0} ${message(code:'common.reviews')}</li>
          </ul>
          <p class="ui-li-aside" style="width:110px">
            <span class="b-rub">${Math.round(home?.pricestandard_rub / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
            <font color="gray">${message(code:'ads.price.price_per_day')}</font>
          </p>
        </div>
        <input type="hidden" id="home_id" name="home_id" value="${home?.id}" />
        <input type="hidden" id="owner_id" value="${ownerUsers[0]?.id}" />
      <g:if test="${homephoto}">
        <div class="relative">
          <div class="royalSlider rsDefault fwImage span_4 rsHor rsWithThumbs rsWithThumbsHor">          
          <g:each in="${homephoto}"> 
            <img src="${it.picture?urlphoto+home?.client_id+'/'+it.picture:resource(dir:'images',file:'default-picture.png')}" class="rsImg" data-rsTmb="${it.picture?urlphoto+home?.client_id+'/'+it.picture:resource(dir:'images',file:'default-picture.png')}" />
          </g:each>          
          </div>
        <g:if test="${curdiscount>0}">
          <div class="discount-container-l">
            <div class="discount-price-l">
            <g:if test="${curdiscount==2}">-${hot_discount_percent}</g:if>
            <g:elseif test="${curdiscount==1}">-${long_discount_percent}</g:elseif>
            <g:else>-${future_discount_percent}</g:else>
            </div>
          </div>          
        </g:if>
        </div>
        <script type="text/javascript">
          jQuery('.royalSlider').royalSlider({
            fullscreen: { enabled: true, nativeFS: true, buttonFS: true },
            controlNavigation: 'thumbnails',
            autoScaleSlider: true,
            autoScaleSliderWidth: 360,
            autoScaleSliderHeight: 300,
            loop: false,
            numImagesToPreload: 4,
            arrowsNavAutohide: true,
            arrowsNavHideOnTouch: true,
            keyboardNavEnabled: true 
          });
        </script>
      </g:if>
        <p style="white-space:normal">${home?.description}</p>
        <ul data-role="listview" data-inset="false" data-theme="a" data-split-icon="arrow-r" data-iconpos="right" style="margin:0px -15px"> 
        <g:if test="${home?.client_id != user?.client_id}">
          <li data-role="divider" data-theme="f"><a <g:if test="${user}">href="#calc_page"</g:if><g:else>data-rel="popup" href="#cboxPopup"</g:else>>${message(code:'button.contact')}</a></li>
        </g:if>
          <li><a href="#description_page">${message(code:'ads.desc')}</a></li>
          <li><a href="#map_page">${message(code:'viewprint.location').capitalize()}</a></li>
          <li><a href="#comfort_page">${message(code:'ads.services')}</a></li>
          <li><a href="#homerule_page">${message(code:'detail.terms_and_conditions').capitalize()}</a></li>
          <li><a href="#reviews_page">${message(code:'list.reviews').capitalize()}</a></li>
        <g:if test="${(discounts?.long && discounts?.long?.modstatus)||(discounts?.hot && discounts?.hot?.modstatus)}">
          <li id="discount_view"><a href="#discount_page">${message(code:'common.discounts').capitalize()}</a></li>
        </g:if>
        </ul>        
      </div>
      <div data-role="footer" data-position="fixed" id="footer_view">
        <div id="cboxPopup" class="new-modal" data-role="popup" data-position-to="window" data-theme="c" data-overlay-theme="a" data-history="false">
          <a href="#" id="cboxPopupClose" data-rel="back" data-role="button" data-theme="c" data-icon="delete" data-iconpos="notext" class="ui-btn-left">${message(code:'button.close')}</a>
          <h2 class="clearfix ui-header ui-bar-f" style="border-radius: .6em .6em 0 0">${message(code:'button.contact')}</h2>
          <div id="cboxContent" data-theme="d" data-role="content" role="main">
          <g:if test="${!user}">
            <p><b><a class="ui-link" id="messagesLoginLink" href="${createLink(uri:'/auth?from='+actionName,base:context?.mobileURL)}" rel="nofollow">Авторизуйтесь в системе</a>, чтобы послать сообщение.</b></p>
            <p>Авторизованный пользователь может:</p>
            <ul style="list-style:disc;margin-left:20px">
              <li>получать контактные данные владельца жилья;</li>
              <li>сохранять в свой список интересные объекты;</li>
              <li>оставлять отзывы;</li>
              <li>получать напоминания о событиях аренды;</li>
              <li>получать новости системы</li>
            </ul>
            <p>и многое др.</p>    
          </g:if><g:else>            
          </g:else>
          </div>
        </div>
      </div>  
    </div>

    <div data-role="page" id="calc_page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" href="#detail_page" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                      
            <li class="text" role="heading">${message(code:'button.contact')}</li>
          </ul>
        </div>
      </div>
      <div data-role="content" class="st">         
        <div data-role="fieldcontain" class="nopadd" style="border-bottom:none">
          <div class="ui-grid-a">
            <div class="ui-block-a">
              <div class="ui-li-thumb">
                <img class="thumbnail shadow" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0" />
              </div>
            </div>
            <div class="ui-block-b">
              <h3 style="color:#ff530d">${home?.name}</h3>
            </div>
          </div>
        </div><br/>        
        <div class="ui-body ui-body-b ui-corner-all st">
          <div data-role="fieldcontain" class="nopadd">
            <div class="ui-grid-a">
              <div class="ui-block-a"><label for="date_start" class="padd"><b>${message(code:'common.date_from')}:</b></label></div>
              <div class="ui-block-b"><input type="text" id="date_start" /></div>
            </div>
          </div>
          <input type="hidden" name="date_start" id="date_starth" value="${formatDate(format:'yyyy-MM-dd',date:inrequest?.date_start?:(new Date()))}" />
          <div class="ui-grid-a">
            <div class="ui-block-a" style="padding-top:18px"><label for="days" class="padd"><b>${message(code:'mobile.days')}:</b></label></div>
            <div class="ui-block-b">
              <a id="date_less" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-left" onclick="dayLess()">-</a>
              <span id="days">${days_between?:1}</span>
              <a id="date_more" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-right" onclick="dayMore()">+</a>
            </div>
          </div>
          <input type="hidden" id="daysh" value="${days_between?:1}" />
        </div>
        <div class="ui-body" style="padding:0 8px">
          <div data-role="fieldcontain" style="border-bottom:none">
            <div class="ui-grid-a">
              <div class="ui-block-a"><label for="date_end" class="padd"><b>${message(code:'common.date_to')}:</b></label></div>
              <div class="ui-block-b"><input type="text" id="date_end" disabled /></div>
            </div>
          </div>
          <input type="hidden" name="date_end" id="date_endh" value="${formatDate(format:'yyyy-MM-dd',date:inrequest?.date_end?:(new Date()+1))}" /> 
        </div>
        <div class="ui-body ui-body-b ui-corner-all st">
          <div class="ui-grid-a">
            <div class="ui-block-a" style="padding-top:18px"><label for="persons" class="padd"><b>${message(code:'mobile.guests')}:</b></label></div>
            <div class="ui-block-b">
              <a id="person_less" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-left" onclick="personLess()">-</a>
              <span id="persons">${inrequest?.homeperson_id?:1}</span>
              <a id="person_more" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-right" onclick="personMore()">+</a>              
            </div>
          </div>
          <input type="hidden" name="homeperson_id" id="personsh" value="1" />          
        </div><br/>        
        <div id="calculatePriceError" class="notice" style="display:none">
          <span id="calculatePriceErrorText"></span>
        </div>        
        <div class="ui-body ui-body-e ui-corner-all st" id="calculatePriceOutput" style="display:none">
          <div class="details clearfix">
            <span class="float">
              <b>${message(code:'detail.cost_of_rent')}:</b><!--br/>
              <small>включает все сборы <a title="Это итоговая сумма, включая все сборы, установленные хозяином и сервисом">
                <img src="images/question.png" alt="Что это такое?" style="margin-bottom:-3px"></a>
              </small -->
            </span>
            <span class="b-rub col" id="calculatePriceOutputText"></span>          
          </div>
        </div><br/>
        <div class="ui-body ui-body-b ui-corner-all st">        
          <div data-role="fieldcontain" class="nopadd">              
            <label for="mtext"><b>${message(code:'detail.message')}</b></label>
            <textarea rows="5" cols="60" id="mtext" name="mtext" placeholder="${message(code:'common.text_of_message')}"></textarea>              
          </div>
        </div>
        <div class="ui-body" style="padding:8px 0">
          <a id="send_button" data-role="button" data-theme="f" href="#" onclick="sendMessage()">${message(code:'button.send')}</a>
        </div>
      </div>
      <div data-role="footer" data-position="fixed">
        <div id="sendmailPopup" class="new-modal" data-role="popup" data-position-to="window" data-theme="c" data-overlay-theme="a" data-history="false">
          <a href="#" id="sendmailPopupClose" data-rel="back" data-role="button" data-theme="c" data-icon="delete" data-iconpos="notext" class="ui-btn-left">${message(code:'button.close')}</a>
          <h2 class="clearfix ui-header ui-bar-f" style="border-radius: .6em .6em 0 0">${message(code:'detail.message_sent')}!</h2>    
          <div id="sendmailContent" data-theme="d" data-role="content" role="main">
          <g:if test="${user && user?.tel}">
            <p>${message(code:'detail.copy_of_letter')} ${user?.email}.</p>
            <p>${message(code:'detail.you_can')} <b>связаться с владельцем жилья</b> прямо сейчас по телефону:</p>
            <a data-role="button" data-theme="f" href="tel:${user.tel}">позвоните ${user.nickname?:''}</a>
          </g:if>
            <p>${message(code:'inbox.view.decline.info')}.</p>              
          </div>
          <div class="segment buttons">
            <div class="ui-grid-a">
              <div class="ui-block-a">
                <b><a class="ui-link" href="<g:createLink controller='m' action='list' params='${(inrequest?.date_start?[date_start:formatDate(format:'yyyy-MM-dd',date:inrequest?.date_start)]:[:])+(inrequest?.date_end?[date_end:formatDate(format:'yyyy-MM-dd',date:inrequest?.date_end)]:[:])+(inrequest?.homeperson_id?[homeperson_id:inrequest?.homeperson_id]:[:])}' base='${context?.mobileURL_lang}' />" rel="nofollow" onclick="jQuery('#sendmailPopup').popup('close')">&laquo; ${message(code:'detail.return_to_search')}</a></b>
              </div>
              <div class="ui-block-b" align="right">
                <b><a class="ui-link" href="${createLink(uri:'/favorites',base:context?.mobileURL_lang)}" rel="nofollow" onclick="jQuery('#sendmailPopup').popup('close')">${message(code:'label.favorite')} &raquo;</a></b>
              </div>
            </div>
          </div>  
        </div>      
      </div>
    </div>

    <div data-role="page" id="description_page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" href="#detail_page" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                     
            <li class="text" role="heading">${message(code:'ads.desc')}</li>
          </ul>
        </div>
      </div>
      <div data-role="content">
        <ul class="description_details">
          <li class="clearfix">
            <span class="property">${message(code:'common.home_type')}</span>
            <span class="value">${hometype['name'+context?.lang]}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'common.homeperson')}</span>
            <span class="value">${homeperson['name'+context?.lang]}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'common.rooms')}</span>
            <span class="value">${homeroom['name'+context?.lang]}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'common.beds')}</span>
            <span class="value">${home?.bed?:'-'}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'common.bathrooms')}</span>
            <span class="value">${homebath.kol?:'-'}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'ads.services.additional.kitchen')}</span>
            <span class="value">${(home?.is_kitchen==1)?message(code:'detail.yes'):message(code:'detail.no')}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'ads.country')}</span>
            <span class="value">${country['name'+context?.lang]}</span>
          </li>
        <g:if test="${home?.city_id}">
          <li class="clearfix">
            <span class="property">${message(code:'ads.city').toLowerCase()}</span>
            <span class="value">${home?.city.capitalize()}</span>
          </li>
        </g:if><g:if test="${home?.district}">
          <li class="clearfix">
            <span class="property">${message(code:'ads.district')}</span>
            <span class="value">${(home?.district?:'').replace('р-н','').replace('r-n','').capitalize()}</span>
          </li>
        </g:if><g:if test="${home?.area}">
          <li class="clearfix">
            <span class="property">${message(code:'common.space')}</span>
            <span class="value">${home?.area?:''} м&sup2;</span>
          </li>
        </g:if>
        </ul>        
      </div>
    </div>  

    <div data-role="page" id="map_page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" href="#detail_page" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                     
            <li class="text" role="heading">${message(code:'viewprint.location').capitalize()}</li>
            <li class="divider-vertical"></li>             
            <li class="button_map"><a class="icon curmap" href="#" rel="nofollow" onclick="setMapCenter()" title="${message(code:'mobile.initial.positioning')}"></a></li>
            <li class="text" id="route_info" style="color:#fff"></li>
          </ul>
        </div>
      </div>
      <div data-role="content" style="padding:0 0">
         <div id="map_view_canvas" style="height:300px;width:100%"></div>
         <div id="dir_panel" style="display:none"></div>                                           
      </div>
    </div>

    <div data-role="page" id="comfort_page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" href="#detail_page" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                     
            <li class="text" role="heading">${message(code:'ads.services')}</li>
          </ul>
        </div>
      </div>
      <div data-role="content">
        <ul class="service clearfix">
        <g:each in="${homeoption}" var="item"><g:if test="${item.fieldname && home[item.fieldname]}">
          <li>          
            <img class="service-icon" src="${resource(dir:'images',file:'enable.png')}" width="17" height="17" title="${message(code:'detail.convenience_/_allowed')}" alt="${message(code:'detail.convenience_/_allowed')}" />                
            <p>${item['name'+context?.lang]}</p>          
          </li>
        </g:if></g:each>
        </ul>      
      </div>
    </div>  
    
    <div data-role="page" id="homerule_page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" href="#detail_page" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                     
            <li class="text" role="heading">${message(code:'detail.terms_and_conditions').capitalize()}</li>
          </ul>
        </div>
      </div>
      <div data-role="content">
        <ul class="description_details clearfix">
          <li class="clearfix">
            <span class="property">${message(code:'ads.price.deposit')}</span>
            <span class="value">
              <g:if test="${deposit}">${deposit} <g:rawHtml>${valutaSym}</g:rawHtml></g:if>
              <g:else>${message(code:'detail.not_needed')}</g:else>
            </span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'ads.price.fee.cleanup')}</span>
            <span class="value">
              <g:if test="${cleanup}">${cleanup} <g:rawHtml>${valutaSym}</g:rawHtml></g:if>
              <g:else>${message(code:'detail.no')}</g:else>                
            </span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'ads.price.fee')}</span>
            <span class="value">
              <g:if test="${fee}">${fee} <g:rawHtml>${valutaSym}</g:rawHtml> ${message(code:'detail.night_after')} ${Homeperson.get(home?.fee_homeperson)['name'+(context?.lang?:'2')]}</g:if>
              <g:else>${message(code:'detail.no')}</g:else>                
            </span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'ads.price.rule.minday')}</span>
            <span class="value">${minday['name'+context?.lang]}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'ads.price.rule.maxday')}</span>
            <span class="value">${maxday['name'+context?.lang]}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'ads.price.rule_timein')}</span>
            <span class="value">${timein['name'+context?.lang]}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'ads.price.rule_timeout')}</span>
            <span class="value">${timeout['name'+context?.lang]}</span>
          </li>
          <li class="clearfix">
            <span class="property">${message(code:'ads.price.cancellation')}</span>
            <span class="value"><a href="#cancellation_page">${cancellation['name'+context?.lang]}</a></span>
          </li>
        </ul>
        <g:if test="${home.homerule}">
          <br/><p>${home.homerule}</p>
        </g:if>
      </div>
    </div>  

    <div data-role="page" id="cancellation_page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" href="#homerule_page" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                      
            <li class="text" role="heading">${message(code:'ads.price.cancellation').capitalize()}</li>
          </ul>
        </div>
      </div>
      <div data-role="content" id="cancellation_content"> 
        <h1>${Infotext.findByControllerAndAction('mobile','cancellation')['header'+context?.lang]}</h1>
        <g:rawHtml>${Infotext.findByControllerAndAction('mobile','cancellation')['itext'+context?.lang]}</g:rawHtml>
      <g:each in="${cancellations}" var="item">
        <h4 id="${item.shortlink}">${item['name'+context?.lang]}: ${item['fullname'+context?.lang]}</h4>
        <ul>       
          <g:rawHtml>${item['nb'+context?.lang]?:''}</g:rawHtml>
          <g:rawHtml>${Infotext.findByControllerAndAction('mobile','cancellation')['itext2'+context?.lang]?:''}</g:rawHtml>
        </ul>
        <div>
          <div><ul><li>${item['itext1'+context?.lang]?:''}</li></ul></div>
          <div><ul><li>${item['itext2'+context?.lang]?:''}</li></ul></div>
          <div><ul><li>${item['itext3'+context?.lang]?:''}</li></ul></div>
        </div>
      </g:each>        
      </div>
    </div>

    <div data-role="page" id="reviews_page">
      <div data-role="header" data-position="fixed">      
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" href="#detail_page" title="${message(code:'mobile.back')}"></a></li>            
            <li class="divider-vertical"></li> 
            <li class="text" role="heading">${message(code:'list.reviews').capitalize()}</li>
          </ul>
        </div>
      </div>
      <div id="review_content" data-role="content" style="padding:0 0">        
        <ul id="output" class="ui-listview" data-role="listview" style="margin:10px 0">
          <li data-role="divider" data-theme="a" style="padding:0 0 0 10px;height:40px">
            <span class="count float">
              <span>${message(code:'mobile.found')}</span><span id="ads_count">${count}</span>
            </span>
            <span class="pagination col">
              <g:paginate controller="m" action="comments" params="${[home_id:home.id,owner_id:owneruser.id]}" 
                prev="&lt;" next="&gt;" maxsteps="1" omitFirst="${true}" omitLast="${true}" max="5" total="${count}"/>
              <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
            </span>
          </li>
        <g:each in="${records}" var="comm" status="i">
          <li data-icon="false" class="clearfix">
            <div class="ui-li-thumb relative" style="max-width:90px;max-height:130px;width:90px;margin-right:5px">
              <img class="thumbnail shadow" alt="${comm.nickname}" title="${comm.nickname}" src="${((comm.picture && !comm.is_external)?imageurl:'')+(comm.smallpicture?:resource(dir:'images',file:'user-default-picture.png'))}" />
              <p class="ui-li-desc">
                <small style="white-space:normal">${comm.nickname}</small><br/>
                <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',comm?.comdate)}</small>
              </p>
            </div>
            <div class="ui-li-desc">
              <div class="bubble-container">
                <small style="white-space:normal">${comm.comtext}</small>
              </div>
            <g:if test="${comm?.user_id==inrequest.u_id}">
              <span class="col" align="right">
                <a class="ui-link" href="javascript:void(0)" rel="nofollow" onclick="commentDelete(${comm.id});">${message(code:'reviews.delete')} &raquo;</a>
              </span>
            </g:if><g:if test="${(comm.typeid==2 && comm.home_id==inrequest?.u_id)||(comm.typeid==1 && Home.get(comm?.home_id).client_id==user?.client_id)}">
              <span class="col" align="right">
                <a class="ui-link" id="answerComment_link${i}" data-rel="popup" href="#answerCommentPopup" onclick="jQuery('#com_id').val(${comm.id})">${message(code:'reviews.respond')} &raquo;</a>
              </span>
            </g:if>
            </div>
          </li>
          <g:each in="${answerComments[i]}">
          <li data-icon="false" class="clearfix">
            <div class="ui-li-thumb relative" style="max-width:90px;max-height:130px;width:90px;margin-right:5px">
              <img class="thumbnail shadow" alt="${owneruser.nickname}" title="${owneruser.nickname}" src="${((owneruser.picture && !owneruser.is_external)?imageurl:'')+(owneruser.smallpicture?:resource(dir:'images',file:'user-default-picture.png'))}" />
              <p class="ui-li-desc">
                <small style="white-space:normal">${owneruser.nickname}</small><br/>
                <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',it?.comdate)}</small>
              </p>
            </div>
            <div class="ui-li-desc">
              <div class="bubble-container">
                <small style="white-space:normal">${it.comtext}</small>
              </div>
            </div>
          </li>
          </g:each>
        </g:each>
        </ul>
        <div style="margin:10px">
        <g:if test="${user && user?.client_id!=home?.client_id}">
          <div class="new-modal ui-corner-all ui-popup ui-body-c">
            <g:formRemote name="rating_form" url="[controller:'m',action:'addcomment']" onSuccess="location.reload(true)">
              <h2 class="clearfix ui-corner-top ui-header ui-bar-f">${message(code:'profile.review.send').capitalize()}</h2>
              <div class="segment nopadding ui-content">
                <div data-role="fieldcontain">
                  <label for="comment_text"><b>${message(code:'detail.message')}:</b></label>
                  <textarea id="comment_text" name="comtext" rows="5" cols="30" placeholder="${message(code:'common.text_of_message')}"></textarea>
                </div>
                <div data-role="fieldcontain">
                  <label for="comment_rating" class="select"><b>${message(code:'profile.rating')}:</b></label>
                  <select id="comment_rating" name="rating" data-theme="f">
                    <option value="0">${message(code:'profile.rating.neutral')}</option>
                    <option value="1">${message(code:'profile.rating.negative')}</option>
                    <option value="2">${message(code:'profile.rating.positive')}</option>
                  </select>
                </div>
                <input type="hidden" name="home_id" value="${home.id}" />
                <div style="display:none">
                  <input type="submit" id="rating_form_submit" />
                </div>
              </g:formRemote>
            </div>
            <div class="segment buttons">
              <a data-role="button" data-theme="f" onclick="$('rating_form_submit').click()">${message(code:'button.send')}</a>
            </div>
          </div>
        </g:if><g:elseif test="${!user}">
          <div class="new-modal ui-corner-all ui-popup ui-body-c">
            <h2 class="clearfix ui-corner-top ui-header ui-bar-f">${message(code:'profile.review.send').capitalize()}</h2>
            <div class="segment nopadding ui-content">
              <div data-role="fieldcontain">
                <p style="white-space:normal">${message(code:'detail.want_review')} <a href="${createLink(uri:'/auth??from='+actionName,base:context?.mobileURL_lang)}" rel="nofollow">${message(code:'login.auth')}</a>.</p>
              </div>
            </div>
          </div>
        </g:elseif>
        </div>              
      </div>
      <div data-role="footer" data-position="fixed" id="footer_review">
        <div id="answerCommentPopup" class="new-modal" data-role="popup" data-position-to="window" data-theme="c" data-overlay-theme="a" data-shadow="false" data-history="false">
          <a href="#" id="answerCommentPopupClose" data-rel="back" data-role="button" data-theme="c" data-icon="delete" data-iconpos="notext" class="ui-btn-left">${message(code:'button.close')}</a>
          <h2 class="clearfix ui-header ui-bar-f" style="border-radius: .6em .6em 0 0">${message(code:'reviews.response')}</h2>
          <div id="answerCommentContent" data-theme="d" data-role="content" role="main">            
            <div id="answerComment_error" class="notice" style="display:none">
              <span id="answerComment_errorText"></span>
            </div>
            <div data-role="fieldcontain" class="ui-hide-label">
              <label for="answ_comtext">${message(code:'detail.message')}:</label>
              <textarea rows="4" cols="60" id="answ_comtext" name="answ_comtext" placeholder="${message(code:'detail.message')}"></textarea>
            </div>             
          </div>
          <div class="segment buttons">
            <a data-role="button" data-rel="back" data-theme="f" href="#" onclick="addAnswerComment()">${message(code:'button.send')}</a>
            <input id="com_id" type="hidden" name="com_id" value="" />                      
          </div>
        </div>
      </div>
    </div>
    
    <div data-role="page" id="discount_page">    
      <div data-role="header" data-position="fixed">             
        <div class="nav">
          <ul class="actions clearfix">
            <li class="button_back"><a class="icon back" data-rel="back" href="#detail_page" title="${message(code:'mobile.back')}"></a></li>
            <li class="divider-vertical"></li>                      
            <li class="text" role="heading">${message(code:'common.discounts').capitalize()}</li>
          </ul>
        </div>
      </div>
      <div data-role="content" id="discount_content"> 
      <g:if test="${discounts?.long && discounts?.long?.modstatus}">
        <h2>${message(code:'common.discounts_for_long_offers')}</h2>
        <p>${message(code:'ads.price.discount').capitalize()} ${long_discount_percent} ${message(code:'detail.offered_for_orders_of_more_than')} 
    ${long_expire_days} ${message(code:'detail.if_the_rent_by_more_than')} ${hot_min_rent_days}. ${message(code:'ads.remarks').capitalize()}: ${discounts.long?.terms?:message(code:'detail.no')}</p>
      </g:if><g:if test="${discounts?.hot && discounts?.hot?.modstatus}">
        <h2>${message(code:'common.discounts_for_hot_offers')}</h2>
        <p>${message(code:'detail.discount')} ${hot_discount_percent} ${message(code:'detail.offered_for_orders_of_less_than')} ${hot_expire_days} ${message(code:'detail.if_the_rent_by_more_than')} ${hot_min_rent_days}. ${message(code:'ads.remarks').capitalize()}: ${discounts.hot.terms?:message(code:'detail.no')}</p>        
      </g:if>  
      </div>
    </div>
  </body>
</html>
