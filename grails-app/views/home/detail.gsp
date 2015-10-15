<html>
  <head>
    <g:if test="${home?.city || home?.district || home?.street}"><title>${home?.city.capitalize()?:''}${home?.district?', '+home?.district.capitalize():''}${home?.street?', '+home?.street:''}${home?.homenumber?', '+home?.homenumber:''} - ${message(code:'detail.rent_per_night')}. ${home?.name?:''}</title></g:if>
    <g:else><title>${alikeWhere}, ${message(code:'detail.rent_per_night')}. ${home?.name?:''}</title></g:else>
    <meta name="keywords" content="${home?.name?:''}" />
    <meta name="description" content="${shortDesc(text:home?.description?:'')}" />
    <meta property="fb:app_id" content="${fb_api_key}" />
    <meta property="og:type" content="product" />
    <meta property="og:site_name" content="StayToday" />
    <meta property="og:locale" content="${context?.lang?'en_US':'ru_RU'}" />
    <meta property="og:locale:alternate" content="${context?.lang?'ru_RU':'en_US'}" />    
    <meta property="og:url" content="${createLink(mapping:homecity?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang),params:homecity?.domain?[linkname:home?.linkname]:[country:Country.get(home?.country_id)?.urlname,city:home.city,linkname:home?.linkname],base:homecity?.domain?'http://'+homecity.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL)}" />
    <meta property="og:title" content="${home?.name}" />
    <meta property="og:description" content="${shortDesc(text:home?.description?:'')}" />
    <meta property="og:image" content="${photourl+home?.mainpicture}" />
    <meta property="og:image:width" content="560" />
    <meta property="og:image:height" content="373" />
  <g:if test="${homevideo.size()>0}">
    <meta property="og:video" content="http://www.youtube.com/embed/${homevideo[0].videoid}?version=3&rel=0&showinfo=0&fs=0&loop=0&iv_load_policy=0&wmode=transparent" />
    <meta property="og:video:type" content="application/x-shockwave-flash" />
  </g:if>
    <meta property="product:retailer" content="${client?.nickname}" />
    <meta property="product:category" content="Real Estate, Rent" />
    <meta property="product:availability" content="preorder" />    
    <meta property="price:amount" content="${Math.round(price_day/valutaRates)}" />
    <meta property="price:currency" content="${context?.shown_valuta?.code}" />
    <meta property="review:rating" content="${home?.rating/10}" />
    <meta property="review:count" content="${home?.nref}" />
    <meta name="twitter:card" content="product" />
    <meta name="twitter:site" content="@StayTodayRu" />
    <meta name="twitter:data1" content="${Math.round(price_day/valutaRates)} ${(context.shown_valuta.id==857)?message(code:'detail.rub'):Valuta.get(context.shown_valuta.id).symbol.decodeHTML()} ${context?.shown_valuta?.code}" />
    <meta name="twitter:label1" content="Price" />
    <meta name="twitter:data2" content="${home?.city}" />
    <meta name="twitter:label2" content="Location" />    
    <link rel="canonical" href="${createLink(mapping:homecity?.domain?'hViewDomain':'hView',params:homecity?.domain?[linkname:home?.linkname]:[country:Country.get(home?.country_id)?.urlname,city:homecity?.name?:(context?.lang?homecity_ru:home.city),linkname:home?.linkname],base:homecity?.domain?'http://'+homecity.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL)}" />    
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(mapping:'hView'+context?.lang,params:[country:Country.get(home?.country_id)?.urlname,city:home.city,linkname:home?.linkname],base:context?.mobileURL)}" />    
    <link rel="alternate" hreflang="ru" href="${createLink(mapping:homecity?.domain?'hViewDomain':'hView',params:homecity?.domain?[linkname:home?.linkname]:[country:Country.get(home?.country_id)?.urlname,city:homecity?.name?:(context?.lang?homecity_ru:home.city),linkname:home?.linkname],base:homecity?.domain?'http://'+homecity.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL)}" />
    <link rel="alternate" hreflang="en" href="${createLink(mapping:homecity?.domain?'hViewDomain_en':'hView_en',params:homecity?.domain?[linkname:home?.linkname]:[country:Country.get(home?.country_id)?.urlname,city:homecity?.name_en?:Tools.transliterate(home.city,0),linkname:home?.linkname],base:homecity?.domain?'http://'+homecity.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL)}" />
    <link rel="image_src" href="${photourl+home?.mainpicture}" />    
    <g:if test="${homevideo.size()>0}">
    <link rel="video_src" href="http://www.youtube.com/embed/${homevideo[0].videoid}?version=3&rel=0&showinfo=0&fs=0&loop=0&iv_load_policy=0&wmode=transparent" type="application/x-shockwave-flash" />
    </g:if>
    <meta name="layout" content="main" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'calendar.css')}" />    
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger" />
    <g:javascript>   
      var iX=${home?.x?:Region.get(home?.region_id?:0)?.x?:0}, iY=${home?.y?:Region.get(home?.region_id?:0)?.y?:0},
          iScale=${home?.x?13:Region.get(home?.region_id?:0)?.scale?:0}, ADDRESS_SEARCH_ZOOM=13,
          map=null, geocoder=null, flag_marker_move=false, placemark={}, tmp=[],
          xArray=new Array(), yArray=new Array(), typeArray=new Array(), nameArray=new Array(), descriptionArray=new Array(), idsArray=new Array(),
          gGroup=null, calCount=0, comCount=0, contact1Count=0, contact2Count=0, mapCount=0, infCount=0,circle={},
          arr=new Array(${homeguidebooktype.size()}), iLim = ${textlimit}, imgCircle="${resource(dir:'images',file:'circle.png')}";
      <g:each in="${homeguidebooktype}" var="type" status="i">
        arr[${type.id-1}] = "${type?.icon?:'marker.png'}";
      </g:each>
      function textLimit(sId){
        var symbols = $(sId), len = symbols.length;
        if(len > iLim){
          symbols = symbols.substring(0,iLim);
          $(sId).value = symbols;
          return false;
        }
      }
      function initialize(){
        $('calculatePriceError').hide();
        $('calculatePriceOutput').hide();
        onPaymentPeriodChange();
      <g:if test="${service==1}">
        $('comments').click();
      </g:if><g:elseif test="${service==2}">
        $('message').click();
      </g:elseif>
        var d = new Date(), gmtHours = -d.getTimezoneOffset()/60;
        getUserTimezone(gmtHours);
        request_geo_objectsin();
        $('infraslistFormSubmit').click();
        if($("date_start_value") && $("date_start_value").value)
          $('calculatePriceFormSubmit').click();
        VK.Widgets.Like("vk_like", {type: "button", height: 20, width: 145});        
        var dds = jQuery('#layers2').find('.description_details'),
            dts = jQuery('#layers2').find('.description_text').not('.wrap');           
        for (var i=0; i < dds.length; i++){          
          jQuery(dds[i]).height(jQuery(dds[i]).height()+36);          
          jQuery(dts[i]).height((jQuery(dds[i]).height()<324) ? 324 : jQuery(dds[i]).height());
        }        
      <g:if test="${context?.lang}">
        function Translate(){      
          var desc = jQuery('#description_text'),           
              machine_translation = desc.attr('data-machine-translation'),
              button = jQuery('#translate_button');      
          if(machine_translation){
            desc.html(machine_translation);
            button.val('${message(code:"detail.show_original")}');          
          }else{          
            button.attr('disabled',true);          
            jQuery.getJSON('https://translate.yandex.net/api/v1.5/tr.json/translate',{
              key: 'trnsl.1.1.20140115T122716Z.0ba57bc8167f2256.84bc50e05f3f09f8a50e529d8a77e4d39a97846d',
              lang: 'ru-en',
              format: 'html',
              text: desc.html()
            }, function(data){
              if(data && data.text){
                desc.attr('data-machine-translation',data.text);
                button.attr('disabled',false);
                button.val('${message(code:"detail.translate")}');              
              }
            });
          }
        }
        (function(){
          var translate=jQuery('.translate');
          if(translate){
            var desc=jQuery('#description_text'),
                button=jQuery('#translate_button'),
                original=desc.html(),
                bTranslated=false;                
            button.click(function(){
              if(bTranslated){
                bTranslated=false;
                button.val('${message(code:"detail.translate")}');
                desc.html(original);
              }else{
                bTranslated=true;
                Translate();                
              }
            });
          }
          button.click();
          button.click();
        })();        
      </g:if>        
      }      
      function getUserTimezone(iVar){
      <g:if test="${cur_timezone}">
        $("usertimezone").update('${cur_timezone.code} ${cur_timezone.name}');
      </g:if><g:else>
        <g:remoteFunction controller='home' action='gettimezone' onSuccess='setUserTimezone(e)' params="'id='+iVar" />
      </g:else>
      }
      function setUserTimezone(e){
        if(e.responseJSON.id&&e.responseJSON.code&&e.responseJSON.name){
          $("usertimezone").update(e.responseJSON.code+'  '+e.responseJSON.name);	    
          $("timezone_option_"+e.responseJSON.id).selected="selected";
        }
      }          
      function messageResponse(e){
        var sErrorMsg='';
        var aMbox=e.responseJSON.mbox_error;
        document.getElementById('proxy_sendmail_button').disabled=false;
        $('mail_date_begin_value').removeClassName('red');
        $('mail_date_end_value').removeClassName('red');
        $('mtext').removeClassName('red');
      <g:if test="${!user}">
        $('mbox_email').removeClassName('red');
        $('mbox_nickname').removeClassName('red');
        $('mbox_tel').removeClassName('red');
      </g:if>
        if(aMbox.length>0){
          aMbox.forEach(function(err){
            switch (err) {             
              case 1: sErrorMsg+="${message(code:'error.blank',args:[message(code:'common.date_from').capitalize()])}<br/>"; if($('mail_date_begin_value')) $('mail_date_begin_value').addClassName('red'); break;
              case 2: sErrorMsg+="${message(code:'error.blank',args:[message(code:'common.date_to').capitalize()])}<br/>"; if($('mail_date_end_value')) $('mail_date_end_value').addClassName('red'); break;
              case 3: sErrorMsg+="${message(code:'detail.check_in_no_older_than_check_out')}<br/>"; break;		  
              case 4: sErrorMsg+="${message(code:'error.blank',args:[message(code:'common.text_of_message').capitalize()])}<br/>"; $('mtext').addClassName('red'); break;
              case 5: sErrorMsg+="${message(code:'detail.email_already_used')} <a class=\"icon none\" href=\"javascript:void(0)\" onclick=\"showLoginForm(3,'login_lbox',this)\">${message(code:'common.log_in').capitalize()}</a>, ${message(code:'common.or_change_email')}.<br/>"; $('mbox_email').addClassName('red'); document.getElementById('user').value=document.getElementById('mbox_email').value; break;
              case 6: sErrorMsg+="${message(code:'error.blank',args:['Email'])}<br/>"; $('mbox_email').addClassName('red'); break;
              case 7: sErrorMsg+="${message(code:'error.blank',args:[message(code:'common.nickname').capitalize()])}<br/>"; $('mbox_nickname').addClassName('red'); break;
              case 8: sErrorMsg+="${message(code:'error.incorrect',args:['Email'])}<br/>"; $('mbox_email').addClassName('red'); break;
              case 9: sErrorMsg+="${message(code:'detail.error.data')} ${String.format("%tF",new Date()+1)}.<br/>"; if($('mail_date_begin_value')) $('mail_date_begin_value').addClassName('red'); break;
              case 10: sErrorMsg+="${message(code:'error.incorrect',args:[message(code:'personal.phone').capitalize()])}<br/>"; $('mbox_tel').addClassName('red'); break;
            }
          });
        }else{
          if(e.responseJSON.error>0){                 
            sErrorMsg+=e.responseJSON.errorprop;       
          }else if(e.responseJSON.result>0){
            //ссылку не трогать!!!!
            window.location = "${createLink(mapping:context.lang?'en':'',controller:'inbox',action:'view',absolute:true)}"+"/"+e.responseJSON.mbxId;
          <g:if test="${false}">
            $('homeperson_id_option_0').selected=true;
            $("mtext").value='';/*		  
            $('is_telperm_true').checked=true;
            var d = new Date()
            var gmtHours = -d.getTimezoneOffset()/60;
            getUserTimezone(gmtHours);*/		  
            $("mail_date_begin").value="";
            $("mail_date_begin_year").value="";
            $("mail_date_begin_month").value="";
            $("mail_date_begin_day").value="";
            $("mail_date_begin_value").value="";		  
            $("mail_date_end").value="";
            $("mail_date_end_year").value="";
            $("mail_date_end_month").value="";
            $("mail_date_end_day").value="";
            $("mail_date_end_value").value="";    
            viewSendMail();
            var sText = "${message(code:'detail.cost_of_renting_this_prop')} <b>"+(e.responseJSON.result/ ${valutaRates}).toFixed(0) + "&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml></b>"
            $("text_price").update(sText);
          </g:if>
          }
        }
        if(sErrorMsg.length){
          $('mbox_error').innerHTML='<font color="red">'+sErrorMsg+'</font>';
          $('mbox_error').show();
          jQuery.colorbox.resize();
        }
      }	  
      function viewSendMail(){
        var sHtml ='<div id="sendmail_lbox" class="new-modal">'+
                   '  <h2 class="clearfix"><img width="24" height="24" hspace="3" valign="middle" alt="${message(code:'detail.message_sent')}" src="${resource(dir:'images',file:'checkmark.png')}">'+
                   '  ${message(code:'detail.message_sent')}!</h2>'+
                   '  <div class="segment nopadding">'+
                   '    <div class="lightbox_filter_container" style="height: 190px">'+
                   '      <div id="text_price" class="notice" style="margin:5px 0"></div>'+
                   '      <p>${message(code:'inbox.view.decline.info')}</p>'+
                   '    </div>'+
                   '  </div>'+
                   '  <div class="segment buttons"><span>'+
                   <g:if test="${from_search}">
                   '    <a href="javascript:window.close()" class="to-parent float">${message(code:'detail.return_to_search')}</a>'+
                   </g:if><g:if test="${user}">
                   '    <a href="${createLink(controller:'trip',action:'favorite',base:context?.mainserverURL_lang)}" class="to-parent right">${message(code:'common.your_favourite')}</a>'+
                   </g:if>
                   '  </span></div>'+
                   '</div>';
                    jQuery.colorbox({ html: sHtml, scrolling: false, onLoad: function(){ 
                      jQuery('#mail_lbox').hide(); 
                    }});
      }
      function toggleFilter(container){ 
        var li = $(container);
        switch(li.className){
          case 'search_filter': li.className = 'search_filter closed open'; break;
          case 'search_filter closed open': li.className = 'search_filter'; break;
          case 'search_filter closed': li.className = 'search_filter open'; break;
          case 'search_filter open': li.className = 'search_filter closed'; break;
        }
      }    
      function viewCell(sObj1,iNum,sObj2){
        var li = jQuery('#'+sObj1).find('.header-dropdown-list-item'),
            tabs = jQuery('#'+sObj1).find('li').not(li),
            layers = jQuery('#'+sObj2).find('div[rel="layer"]');
        for (var i=0; i < tabs.length; i++){
          if(i==iNum)
            tabs[i].addClassName('selected');
          else
            tabs[i].removeClassName('selected');          
          layers[i].style.display = (i==iNum)? 'block' : 'none';
        }
        jQuery('#slideshow_container').find('iframe').css('display',(iNum==1)?'block':'none');        
      }
      function viewCell2(el,iNum,sName){
        var tabs = jQuery('#tabs2').find('li'),
            layers = jQuery('#layers2').find('div[rel="layer"]');
        for (var i=0; i < tabs.length; i++){
          tabs[i].removeClassName('selected');          
          layers[i].style.display = (i==iNum)? 'block' : 'none';
        }        
        if(iNum==3){         
          var dd = jQuery('#infras').find('.description_details'),
              dt = jQuery('#infras').find('.description_text').not('.wrap');
          jQuery(dd).height('auto');          
          jQuery(dt).height((jQuery(dd).height()<jQuery(dt).height()) ? jQuery(dt).height() : jQuery(dd).height());
        }
        el.addClassName('selected');
        viewTop(sName);
      }      
      function viewTop(sName){
        var el = jQuery(sName), offset = 80;
        if(!el)
          return true;
        else if(sName=='infrastructure')
          document.location.hash = sName;        
        else {  
          if($('tabs2').getStyle('position')==='fixed')
            offset = 40;
          jQuery("body:not(:animated)").animate({ scrollTop: (el.offset().top - offset) }, 500);
          jQuery("body,html").animate({ scrollTop: (el.offset().top - offset) }, 500)
          document.location.hash = sName.substring(1);
          return false;
        }
      };
      jQuery(window).load(function(){        
        var link = document.location.hash;
        if(link && link.length > 1){
          link = '#' + link.substring(1);
          viewTop(link);          
        }
      });
      function Yandex() {      
        if(!map){
          ymaps.ready(function() {            
            map = new ymaps.Map("map_canvas",{ center:[iY/100000,iX/100000], zoom: iScale, behaviors: ["default"] });
            map.controls.add("smallZoomControl").add("scaleLine").add("searchControl")            
              .add(new ymaps.control.TypeSelector(['yandex#map','yandex#satellite','yandex#hybrid']))
              .add(new ymaps.control.MapTools({ items: ["default"]}))
              .add(new ymaps.control.MiniMap({ type: 'yandex#map' }, { size: [90,90] }));            
            gGroup  = new ymaps.GeoObjectCollection();
            for(var i = 0; i<xArray.length; i++)
              addMarker(xArray[i],yArray[i],typeArray[i],nameArray[i],descriptionArray[i],idsArray[i]);
            map.geoObjects.add(gGroup);
            circle = new ymaps.Placemark([iY/100000, iX/100000],{}, {
              draggable: false,
              hasBalloon: false,
              zIndexHover:0,
              hasHint: false,
              iconImageHref: imgCircle,
              iconImageSize: [184, 184],
              iconImageOffset:[-112, -60]
            });              
            map.geoObjects.add(circle);
            map.events.add("boundschange", function(e) {
              map.geoObjects.remove(circle);
              if(e.get('newZoom')<=13){
                circle = new ymaps.Placemark([iY/100000, iX/100000],{}, {
                  draggable: false,
                  hasBalloon: false,
                  zIndexHover:0,
                  hasHint: false,
                  iconImageHref: imgCircle,
                  iconImageSize: [184, 184],
                  iconImageOffset:[-112, -60]
                });              
                map.geoObjects.add(circle);
              }
              if(e.get('newZoom')==14){
                circle = new ymaps.Placemark([iY/100000, iX/100000],{}, {
                  draggable: false,
                  hasBalloon: false,
                  zIndexHover:0,
                  hasHint: false,
                  iconImageHref: imgCircle,
                  iconImageSize: [372, 372],
                  iconImageOffset:[-228, -120]
                });              
                map.geoObjects.add(circle);
              }   
              if(e.get('newZoom')==15){
                circle = new ymaps.Placemark([iY/100000, iX/100000],{}, {
                  draggable: false,
                  hasBalloon: false,
                  zIndexHover:0,
                  hasHint: false,
                  iconImageHref: imgCircle,
                  iconImageSize: [740, 740],
                  iconImageOffset:[-453, -240]
                });              
                map.geoObjects.add(circle);
              }             
            });	  
          });
        }
      }      
      function calculatePrice(){
        $('calculatePriceFormSubmit').click();
      }
      function calculateMailPrice(){
        $('date_start_year_mail').value=$('mail_date_begin_year').value;
        $('date_start_month_mail').value=$('mail_date_begin_month').value;
        $('date_start_day_mail').value=$('mail_date_begin_day').value;
        $('date_end_year_mail').value=$('mail_date_end_year').value;
        $('date_end_month_mail').value=$('mail_date_end_month').value;
        $('date_end_day_mail').value=$('mail_date_end_day').value;      
        $('homeperson_id_mail').value=$('mail_homeperson_id').value;
        $('calculateMailPriceFormSubmit').click();
      }	      
      function processResponse(e){
        if(e.responseJSON.error){
          $('calculatePriceError').show();
          $('calculatePriceOutput').hide();
          $('calculatePriceErrorText').update(e.responseJSON.errorprop);
        }else if (e.responseJSON.result!=null) {
          $('calculatePriceOutput').show();        
          $('calculatePriceError').hide();
          var sText = (e.responseJSON.result/ ${valutaRates}).toFixed(0) + "&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>";
          $('calculatePriceOutputText').update(sText);
        }else{
          $('calculatePriceError').hide();
          $('calculatePriceOutput').hide();
        }     
      }
      function processMailResponse(e){        
        if(e.responseJSON.error){
          $('calculateMailPriceError').show();
          $('calculateMailPriceOutput').hide();
          $('calculateMailPriceErrorText').update(e.responseJSON.errorprop);          
        }else if (e.responseJSON.result!=null) {
          $('calculateMailPriceOutput').show();
          $('calculateMailPriceError').hide();
          var sText = (e.responseJSON.result/ ${valutaRates}).toFixed(0) + "&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>";
          $('calculateMailPriceOutputText').update(sText);          
        }else{
          $('calculateMailPriceError').hide();
          $('calculateMailPriceOutput').hide();          
        }
        jQuery('#lightbox_filter_container').css('height','auto');      
        jQuery.colorbox.resize();
      } 
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null)
          return;          
        new Ajax.Updater(
          { success: link.up('div').up('div').id },
          link.href,
          { evalScripts: true }
        );
        if(link.up('div').up('div').id=='ajax_wrap1')
          removeAllMarkers(0);
      }      
      function onPaymentPeriodChange() {
        var price_current = 0, periodID = 'per_day',
            price_day = ${price_day}, price_weekend = ${price_weekend},
            price_week = ${price_week}, price_month = ${price_month};            
        if (${havePrice}==1) {      
          periodID = $('payment_period').options[$('payment_period').selectedIndex].value;      
          switch(periodID){
            case 'per_day': price_current = price_day; break;
            case 'per_week': price_current = (price_week==0) ? ((price_weekend==0) ? (price_day*7):(price_day*5 + price_weekend*2)) : price_week; break;
            case 'per_month': price_current = (price_month==0) ? price_day*30 : price_month; break;            
          }
          price_current = (price_current / ${valutaRates}).toFixed(0);
          var sText = price_current + "&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>"
          $('price_amount').update(sText);
        } else
          $('price_amount').update('<small>${message(code:'detail.price_not_set')}</small>');        
      }    
      function showCal(){
        if (calCount==0) {
          calCount++;
          <g:remoteFunction controller='home' action='calendarstatistic' id="${home.id}"/>
          jQuery('div#calendar').fullCalendar({
            firstDay: 1,
            height: 514,
            dayNamesShort:<g:if test="${context?.lang}">['Su','Mn','Tu','We','Th','Fr','Sa']</g:if><g:else>['Вс','Пн','Вт','Ср','Чт','Пт','Сб']</g:else>,
            monthNames:<g:if test="${context?.lang}">['january', 'february', 'march', 'april', 'may', 'june', 'july', 'august', 'september', 'october', 'november', 'december']</g:if><g:else>['январь', 'февраль', 'март', 'апрель', 'май', 'июнь', 'июль', 'август', 'сентябрь', 'октябрь', 'ноябрь', 'декабрь']</g:else>,
            buttonText:{today:"${message(code:'ads.calendar.today')}"},
          <g:if test="${ownerUsers[0]?.id!=user?.id}">
            selectable:true,
            select: function(start, end) {
              if(confirm('${message(code:'detail.contact_owner')}')){
                var start_date=start.getDate(), start_month=start.getMonth()+1, start_year=start.getFullYear(),
                    end_date=end.getDate()+1, end_month=end.getMonth()+1, end_year=end.getFullYear(),
                    dStart='', dEnd='';
                if(start_date<9) dStart+="0"+start_date+"-";
                else dStart+=start_date+"-";
                if(start_month<9) dStart+="0"+start_month+"-";
                else dStart+=start_month+"-";
                dStart+=start.getFullYear();
                if(end_date<9) dEnd+="0"+end_date+"-";
                else dEnd+=end_date+"-";
                if(end_month<9) dEnd+="0"+end_month+"-";
                else dEnd+=end_month+"-";
                dEnd+=end.getFullYear();
                startMessage(dStart,dEnd);
              }
            },
          </g:if>
            eventSources: [{
              url:"${(context?.is_dev)?'/'+context?.appname:''}/home/event",
              data:{ id: ${home.id}, tz: new Date().getTimezoneOffset() }
            }],
            eventRender: function(event,element){
              var color;
              switch(event.className[0]){
                case 'old': color='#efefef'; break;//silver 
                case 'inactive': color='#ff530d'; break;//tomato
                case 'active': color='#ccffcc'; break;//green
                case 'notavailable': color='#ff530d'; break;//tomato
                case 'reserved': color='#ff530d'; break;
              }
              jQuery('.fc-day'+event.dayNum).css({'background':'none','background-color':color});            
            }
          });
        }
      }    
      function showMap(){
        if (mapCount==0) {
          mapCount++;
          <g:remoteFunction controller='home' action='mapstatistic' id="${home.id}"/>
        }
      }
      function showInf(){
        if (infCount==0) {
          infCount++;
          <g:remoteFunction controller='home' action='infstatistic' id="${home.id}"/>
        }        
      }
      function showCom(){
        if (comCount==0) {
          comCount++;
          <g:remoteFunction controller='home' action='commentstatistic' id="${home.id}"/>
        }
        $('commentsFormSubmit').click();
      }    
      var star_id=0;
      function addtofavourite(lId){
        star_id=lId;
        <g:remoteFunction controller='home' action='selectcompany' onSuccess='setFavourite(e)' params="\'id=\'+lId" />
      }
      function setFavourite(e){
        var bFlag=0;
        var arr=e.responseJSON.wallet;
        for(var i=0;i<arr.size();i++){
          if(star_id==arr[i]){
            $('star_icon_'+star_id).addClassName("starred");
            bFlag=1;
          }
        }
        if(!bFlag)
          $('star_icon_'+star_id).removeClassName("starred");      
        if(arr.length>0){	  
          $('favorite').removeClassName('no_active');
          $('favorite').href='<g:createLink controller="trip" action="favorite" absolute="true"/>'; 
          $('favorite').parentElement.addClassName(' starred');
        } else {
          $('favorite').addClassName('no_active');
          $('favorite').href='javascript:void(0)';
          $('favorite').parentElement.removeClassName(' starred');
        }
      }
      //используется для нормирования Y коодинаты
      var consar=new Array(-85051128,-83979259,-82676284,-81093213,-79171334,-76840816,-74019543,-70612614,-66513260,-61606396,-55776579,-48922499,-40979898,-31952162,-21943045,-11178401,0,11178401,21943045,31952162,40979898,48922499,55776579,61606396,66513260,70612614,74019543,76840816,79171334,81093213,82676284,83979259,85051128);
      //первая часть — само преобразовании координат в деление, название функций и их содержимое взято с викимапии
      function getdatakvname(x, y, curzoomkv){
        var xdel=0, ydel=0, xline=0, yline=0,
            x1=-180000000, x2=180000000, y1=-85051128, y2=85051128,
            y1cons=0, y2cons=32, yconsdel=0, n=0, z=curzoomkv-1;
        while(z>=0){
          xdel=Math.round((x1+x2)/2);
          if(n<4){yconsdel=(y1cons+y2cons)/2; ydel=consar[yconsdel];}
          else  {ydel=Math.round((y1+y2)/2);}
          if(x<=xdel){x2=xdel; xline=xline*2;}
          else    {x1=xdel+1; xline=xline*2+1;}
          if(y<=ydel){y2=ydel; y2cons=yconsdel; yline=yline*2;}
          else   {y1=ydel+1; y1cons=yconsdel; yline=yline*2+1;}
          z--;
          n++;
        }
        var out=new Array();
        out.xline=xline;
        out.yline=yline;
        return out;
      }
      function cheakpoint(x, y, xline, yline, curzoomkv){
        var xdel=0, ydel=0, 
            x1=-180000000, x2=180000000, y1=-85051128, y2=85051128,
            y1cons=0, y2cons=32, yconsdel=0, n=0, xlinetest=0, ylinetest=0, test=0, z=curzoomkv-1;
        while(z>=0){
          xdel=Math.round((x1+x2)/2);
          if(n<4){yconsdel=(y1cons+y2cons)/2; ydel=consar[yconsdel]}
          else  {ydel=Math.round((y1+y2)/2)}
          test=Math.pow(2, z);
          xlinetest=xline&test;
          ylinetest=yline&test;
          if(xlinetest>0){x1=xdel+1}
          else      {x2=xdel}
          if(ylinetest>0){y1=ydel+1; y1cons=yconsdel}
          else      {y2=ydel; y2cons=yconsdel}
          z--;
          n++
        }
        var out=new Array();
        if((x>=x1)&&(x<=x2)&&(y>=y1)&&(y<=y2)){out.res=1}
        else{out.res=0}
        return out;
      }    
      function retcode(xline, yline, curzoomkv){
        var xparam=0, yparam=0, test=0, xlinetest=0, ylinetest=0, line='', z=curzoomkv-1;
        while(z>=0){
          test=Math.pow(2, z);
          xlinetest=xline&test;
          ylinetest=yline&test;
          if(xlinetest>0){xparam=1}
          else{xparam=0}
          if(ylinetest>0){yparam=2}
          else{yparam=0}
          linepart=xparam+yparam;
          line=line+linepart;
          z--;
        }
        return line;
      }	
      function request_geo_objectsin(){ 
        require_block={}; 
        var HMap=[];
        curzoomkv=13;//REDO this!
        var range=${coordRange}*10, 
            x1point=Math.round((${home?.x}*10-range)), y1point=Math.round((${home?.y}*10-range)),
            x2point=Math.round((${home?.x}*10+range)), y2point=Math.round((${home?.y}*10+range));             
        if(x1point<-180000000){x1point=-180000000}if(x2point<-180000000){x2point=-180000000}if(x1point>180000000) {x1point=180000000}if(x2point>180000000) {x2point=180000000}if(y1point<-85051128) {y1point=-85051128}if(y2point<-85051128) {y2point=-85051128}if(y1point>85051128) {y1point=85051128}if(y2point>85051128) {y2point=85051128}  
        outar=[];
        outar=getdatakvname(x1point, y1point, curzoomkv);
        var xline=outar.xline, yline=outar.yline, maks=Math.pow(2, curzoomkv)-1,
            vlez=0, xsdvig=0, xlinet=xline, ylinet=yline;
        while(vlez!=1){
          outar=cheakpoint(x2point, y1point, xlinet, ylinet, curzoomkv);
          vlez=outar.res;
          xsdvig++;
          xlinet=xlinet+1;
          if(xlinet>maks){xlinet=0}
        }
        vlez=0;
        var ysdvig=0, xlinet=xline, ylinet=yline;
        while(vlez!=1){
          outar=cheakpoint(x1point, y2point, xlinet, ylinet, curzoomkv);
          vlez=outar.res;
          ysdvig++;
          ylinet=ylinet+1;
          if(ylinet>maks){ylinet=0}
        }
        var temp='', newtemp='', ylinesave=yline, ysdvigsave=ysdvig;
        while(xsdvig>0){
          while(ysdvig>0){
            temp=retcode(xline, yline, curzoomkv);
            var lineleng=0;        
            xml_url=temp;    
            require_block[temp]=xml_url;
            ysdvig--; yline++;
            if(yline>maks){yline=0}
          }
          yline=ylinesave;
          ysdvig=ysdvigsave;
          xsdvig--;
          xline++;
          if(xline>maks){xline=0}      
        }
        var i=0, flag=0, params='', HMapTmp=new Array();
        for(gotarrn in require_block){  
          HMapTmp.push(gotarrn);
          for(elem in HMap)      
            if(HMap[elem]==gotarrn)
              flag=1;
        }
        if(!flag){//TODO
          HMap=HMapTmp;
          $("params_filter").value=HMapTmp;
        }
      }
      function messageBox(start, end){      
        var lId = ${ownerUsers[0]?.id?:0}
        jQuery('#messageval').colorbox({
          inline: true, 
          href: '#mail_lbox',
          scrolling: false,
          onLoad: function(){
            var aStart=new Array(), aEnd=new Array();
            aStart=start.split('-');
            aEnd=end.split('-');
            $('mail_date_begin_value').value=start;
            $('mail_date_end_value').value=end;
            $('mail_date_begin_year').value=aStart[2];
            $('mail_date_end_year').value=aEnd[2];
            $('mail_date_begin_month').value=aStart[1];
            $('mail_date_end_month').value=aEnd[1];
            $('mail_date_begin_day').value=aStart[0];
            $('mail_date_end_day').value=aEnd[0];
            $('mail_homeperson_id').selectedIndex = $('homeperson_id').selectedIndex;        
            calculateMailPrice();          
            jQuery('#mail_lbox').show();
          },
          onCleanup: function(){
            jQuery('#mail_lbox').hide();
            if(!${user?1:0})
              setService(0);
          }                        
        });
        if(!${user?1:0})
          setService(3);
        jQuery('#messageval').click();
      }
      function allowResponse(e){
        if (e.responseJSON.error){
          var template;
          if (e.responseJSON.error==1)
            template='<div class="new-modal"><h2 class="clearfix">${message(code:'detail.phone_confirmation')} <a class="icon none" href="${createLink(controller:'profile',action:'verifyUser',base:context?.absolute_lang)}">${message(code:'detail.confirm')}</a> ${message(code:'detail.your_phone')}.</h2></div>';
          else if (e.responseJSON.error==2)
            template='<div class="new-modal"><h2 class="clearfix">${message(code:'detail.photo_confirmation')} <a class="icon none" href="${createLink(controller:'profile',action:'photo',base:context?.absolute_lang)}">${message(code:'detail.add')}</a> ${message(code:'detail.photos_in_profile')}.</h2></div>';
          else
            template='<div class="new-modal"><h2 class="clearfix">${message(code:'login.error.bderror')}.</h2></div>';
          jQuery.colorbox({ html: template });
        } else
          $('messageval').click();        
      }
      function startMessageZacStat(){
        if (contact2Count==0) {
          contact2Count++;
          <g:remoteFunction controller='home' action='contactstatistic' id='${home.id}' params="'type=2'" />
        }
        startMessage();
      }
      function startMessageConStat(){
        if (contact1Count==0) {
          contact1Count++;
          <g:remoteFunction controller='home' action='contactstatistic' id='${home.id}' params="'type=1'" />
        }
        jQuery('#mail_lbox').find('h2').html('${message(code:"detail.check_availability")+', '+message(code:"common.owner")+' '+ownerUsers[0]["nickname"]}');
        jQuery('#mtext').html('${message(code:"detail.available_for_dates")}');
        startMessage();
      }
      function startMessage(start, end){
        start = start || $('date_start_value').value || '';
        end = end  || $('date_end_value').value || '';
      <g:if test="${mbox_id_is_open}">
        window.open("${createLink(mapping:context.lang?'en':'', controller:'inbox', action:'view', id:mbox_id_is_open, absolute:true)}");
      </g:if><g:else>
        window.scrollTo(0,0);
        messageBox(start, end);
      </g:else>
      }    
      function addMarker(iX,iY,iType,sName,sDescription,lIds){
        iType = iType || 10;
        sName = sName || '';
        sDescription = sDescription || '';
        lIds = lIds || 0;      
        tmpPlacemark = new ymaps.Placemark([iY/100000, iX/100000], { 
          hintContent: sName,
          balloonContentHeader: sName,
          balloonContent: sDescription
        }, {
          draggable: false,                
          hasHint: 1,
          iconImageHref:"${((context?.is_dev)?"/"+context?.appname+"/images/infras/":"/images/infras/")}"+arr[iType-1],
          iconImageSize: [27,26],
          iconImageOffset:[-10,-32]
        });  
        tmpPlacemark.ids = lIds;
        gGroup.add(tmpPlacemark);     
        //map.geoObjects.add(tmpPlacemark);
      }
      function removeAllMarkers(bFlag){
        xArray=[], yArray=[], typeArray=[], nameArray=[], descriptionArray=[], idsArray=[];      
        if (gGroup){
          gGroup.each(function (el, i) {
            map.geoObjects.remove(el);       
          }); 
          if(!bFlag)        
            gGroup.removeAll();
        }
      }
      function addInfras(){
        if (gGroup){        
          for(var i = 0; i<xArray.length; i++)
            addMarker(xArray[i],yArray[i],typeArray[i],nameArray[i],descriptionArray[i],idsArray[i]);        
          removeAllMarkers(1);
          map.geoObjects.add(gGroup);
        }
      }
      function openBalloon(iId) {
        var placemarkOpen = null;
        gGroup.each(function (obj) {        
          if(obj.ids == iId) 
            placemarkOpen = obj;
        });      
        gGroup.each(function (el,i) {
          el.balloon.close(function(e){});
        });     
        map.panTo([placemarkOpen.geometry.getCoordinates()[0],placemarkOpen.geometry.getCoordinates()[1]], {
          flying: true,
          callback: function() {          
            if(placemarkOpen)
              placemarkOpen.balloon.open();          
          }
        });      
      }      
      function showErrorWindow(iValue){      
        var sValue='';
        switch(iValue){
          case 1: sValue="${message(code:'detail.spam').capitalize()}"; break;
          case 2: sValue="${message(code:'detail.wrong_info').capitalize()}"; break;
          case 3: sValue="${message(code:'detail.inappropriate_content').capitalize()}"; break;
          case 4: sValue="${message(code:'detail.other').capitalize()}"; break;
        }  
        $('inpOrph').update(sValue);
        $("rectitleOrph").value=sValue;	        
        $('rectextOrph').value='';
        jQuery.colorbox({ inline: true, href: '#contOrph', scrolling: false, onLoad: function(){
          jQuery('#contOrph').show();
          jQuery('#flag_link').parent().parent().removeClass(' open');
        }, onCleanup: function(){
          jQuery('#contOrph').hide();          
        }});     
      }
      function commentDelete(iId){
        if (confirm("${message(code:'reviews.delete')}"))
          <g:remoteFunction controller='home' action='commentDelete' onSuccess='afterDelComm()' params="'id='+iId" />        
      }
      function afterDelComm() {
        $('comments_col').innerHTML = (parseInt($('comments_col').innerHTML)-1).toString();
        $('commentsFormSubmit').click();
      }
      function answerCommentResponse(e) {
        if (e.responseJSON.error){
          if (e.responseJSON.message){
            $('answerComment_error').show();
            $('answerComment_errorText').update(e.responseJSON.message);
          }
        } else {
          jQuery.colorbox.close();
          $('commentsFormSubmit').click();
        }
      }
      function testMessage(){//tests not needed now
        $('sendmail_button').click();
      }            
    </g:javascript>
    <style type="text/css">    
      .title a.name{height:auto}
      .form label.mini{min-width:125px}
      ul.verifications-list li.verifications-list-item{border-bottom:1px solid #E0E0E0;line-height:13px;padding:11px 15px 9px}
      ul.verifications-list li.verifications-list-item:last-child{border-bottom-width:0}
      ul.verifications-list li.verifications-list-item .counts{color:#333/*#8B99A5*/;font-size:11px}
      ul.verifications-list li.verifications-list-item .counts .p{background:#BCCEDD;color:#E7ECEF;margin-right:1px;width:3px}
      ul.verifications-list li.verifications-list-item .label{font-size:12px;line-height:13px}
      ul.verifications-list li.verifications-list-item .verifications-legend{float:left;height:25px;margin:0 6px 0 -6px;width:25px;background-repeat:no-repeat;background-position:left bottom}
      ul.verifications-list li.verifications-list-item .verifications-legend.phone{background-image:url(${resource(dir:'images',file:'icons-verification-phone.png')});}
      ul.verifications-list li.verifications-list-item .verifications-legend.staytoday{background-image:url(${resource(dir:'images',file:'icons-verification-staytoday.png')});}
      ul.verifications-list li.verifications-list-item .verifications-legend.date{background-image:url(${resource(dir:'images',file:'icons-verification-date.png')});}
      ul.verifications-list li.verifications-list-item .verifications-legend.online{background-image:url(${resource(dir:'images',file:'action-button-icons.png')});background-position:0 -1px}
      ul.verifications-list li.verifications-list-item .verifications-legend.offline{background-image:url(${resource(dir:'images',file:'action-button-icons.png')});background-position:0 -177px}    
      ul.verifications-list li.verifications-list-item .verifications-legend.response{background-image:url(${resource(dir:'images',file:'icons-verification-response.png')});}   
      ul.verifications-list li.verifications-list-item .verifications-legend.frequency{background-image: url(${resource(dir:'images',file:'icons-verification-frequency.png')});} 
      ul.verifications-list li.verifications-list-item .verifications-icon{background:url(${resource(dir:'images',file:'icons-verification.png')}) no-repeat scroll left bottom transparent;float:right;height:26px;margin-top:0;width:25px}
      ul.verifications-list li.verifications-list-item .verifications-icon.none{background-image:url(${resource(dir:'images',file:'checkmark-inactive.png')});}
      #show_phone p{font-size:11px;line-height:15px;color:#333;margin:10px 0 0;padding:0}
      .translate{margin:10px 0 0 20px}
      .toggle.border{background:#EAEAE6 url(${resource(dir:'images',file:'bg_filter_header.png')})!important;margin-bottom:0!important;padding:3px 15px!important}
    </style>
  </head>  
  <body onload="initialize()">  
            <tr style="height:110px"><!--noindex-->
              <td width="250" rowspan="2" class="search" style="padding-bottom:70px">
                <a class="button" rel="nofollow" onclick="$('alike_search_form').submit();">${message(code:'common.renthome_nearby')}</a>
                <g:form name="alike_search_form" url="${[action:'search']}" base="${context?.mainserverURL_lang}" method="get">
                  <input type="hidden" id="params_filter" name="param" value="" />
                  <input type="hidden" id="back_where" name="where" value="${alikeWhere}" />
                  <input type="hidden" id="back_alike" name="alike" value="${home.id}" />
                  <input type="hidden" id="back_view" name="view" value="list" />
                </g:form>
              </td><!--/noindex-->
              <td width="730" colspan="3" class="rent s0">
                <h1 class="header" itemprop="name">${home?.name}</h1>              
                <meta itemprop="url" content="${createLink(mapping:homecity?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang),params:homecity?.domain?[linkname:home?.linkname]:[country:Country.get(home?.country_id)?.urlname,city:home.city,linkname:home?.linkname],base:homecity?.domain?'http://'+homecity.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL)}" />               
                <span itemprop="address" itemscope itemtype="http://schema.org/PostalAddress">                  
                  <meta itemprop="addressCountry" content="${breadcrumbs.country.name}" />                  
                  <meta itemprop="addressLocality" content="${home?.city}" />              
                  <meta itemprop="addressRegion" content="${Region.get(home?.region_id)?.name}" />
                  <meta itemprop="postalCode" content="${home?.pindex?:''}" />
                  <meta itemprop="streetAddress" content="${home?.street}, ${home?.homenumber}" />
                </span>
                <meta itemprop="telephone" content="${yandexMode?client?.tel:'8(800)555-17-68'}" />
                <span itemprop="geo" itemscope itemtype="http://schema.org/GeoCoordinates">
                  <meta itemprop="latitude" content="${home?.y?home?.y/100000:0}" />
                  <meta itemprop="longitude" content="${home?.x?home?.x/100000:0}" />
                </span><!--noindex-->
                <a id="star_${home?.id}" class="star_icon_container" href="javascript:void(0)" rel="nofollow" title="${message(code:'common.add_to_favourite')}">
                  <div id="star_icon_${home?.id}" onclick="addtofavourite(${home?.id})" class="star_icon <g:if test="${(wallet?:[]).contains(home?.id)}">starred</g:if>"></div>
                </a><!--/noindex-->
                <div class="details col" style="margin:10px 10px 0 0px">
                  <span class="ss_price b-rub" id="price_amount">
                    ${Math.round(price_day/valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>
                  </span>
                  <select id="payment_period" name="payment_period" onchange="onPaymentPeriodChange()" class="period">
                    <option value="per_day">${message(code:'common.day')}</option>
                    <option value="per_week">${message(code:'common.week')}</option>
                    <option value="per_month">${message(code:'common.month')}</option>
                  </select>                   
                </div>
                <ul class="details-list padd20">
                  <li class="details-list-item location" style="color:#fff">
                    <span class="icons"></span>
                    <span><g:shortString text="${home?.shortaddress}" length="56"/></span>
                  </li>
                  <li class="details-list-item" style="width:690px;color:#fff">
                    <span><g:join in="${metro.collect{it['name'+context?.lang]}}" delimiter=", "/></span>
                  </li>
                </ul>
              </td>
            </tr>
            <tr>              
              <td colspan="3" class="bg_shadow">
              <g:if test="${breadcrumbs.country}">
                <div class="breadcrumbs" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">
                    <a href="${createLink(uri:'',base:context?.absolute_lang)}" rel="v:url" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span>
                  <g:if test="${breadcrumbs.country.is_index}"><span typeof="v:Breadcrumb">
                    <a href="<g:createLink controller='index' action='popdirectionAll' params='[id:breadcrumbs.country.urlname]' base='${context?.absolute_lang}'/>" rel="v:url" property="v:title"></g:if>
                  <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" property="v:title"></g:else>
                    ${breadcrumbs?.country['name'+context?.lang]}<g:if test="${breadcrumbs.country.is_index}"></a></g:if><g:else></span></g:else> &#8594;
                  </span><span typeof="v:Breadcrumb">
                    <g:if test="${homecity?.is_index}"><a href="<g:createLink mapping='${(homecity?.domain?'hSearchTypeDomain':'hSearchType')+context?.lang}' params='${homecity?.domain?[type_url:'all']:[where:home.city,country:breadcrumbs.country.urlname,type_url:'all']}' base='${breadcrumbs.base}'/>" rel="v:url" property="v:title"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['type_all','${home.city}','${breadcrumbs.country.urlname}'],'','','','${context?.lang}')" property="v:title"></g:else>
                    ${home?.city.capitalize()}<g:if test="${homecity?.is_index}"></a></g:if><g:else></span></g:else> &#8594;
                  </span><span typeof="v:Breadcrumb">
                    <g:link mapping="${homecity?.domain?(breadcrumbs.hometype?.urlname=='flats'?('mainpage'+context?.lang):('hSearchTypeDomain'+context?.lang)):('hSearchType'+context?.lang)}" params="${homecity?.domain?(breadcrumbs.hometype?.urlname=='flats'?[:]:[type_url:breadcrumbs.hometype?.urlname]):[where:home.city,country:breadcrumbs.country.urlname,type_url:breadcrumbs.hometype?.urlname]}" base="${breadcrumbs.base}" rel="v:url" property="v:title">${breadcrumbs.hometype['name3'+context?.lang]}</g:link> &#8594;
                  </span>
                  <g:if test="${home?.hometype_id==1 && home?.homeroom_id}"><span typeof="v:Breadcrumb">
                    <g:link rel="tag" mapping="${(homecity?.domain?'hSearchRoomDomain':'hSearchRoom')+context?.lang}" params="${homecity?.domain?[type_url:'flats',bedroom:home.homeroom_id]:[where:home.city,country:breadcrumbs.country.urlname,type_url:'flats',bedroom:home.homeroom_id]}" base="${breadcrumbs.base}" rel="v:url" property="v:title">${Homeroom.get(home.homeroom_id?:1)['name3'+context?.lang]}</g:link> &#8594;
                  </span></g:if>         
                  ${home?.name}
                </div>
              </g:if>                
              </td>
            </tr>
            <tr>
              <td valign="top">                                
                <ul class="collapsable_filters"><!--noindex-->
                  <li class="search_filter" id="calculator_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('calculator_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('calculator_container');">${message(code:'detail.cost_calculator')}</a>        
                    <div class="search_filter_content">                      
                      <g:formRemote id="calculatePriceForm" name="calculatePriceForm" method="post" url="[controller:'home',action:'calculatePrice']" onSuccess="processResponse(e)">
                        <table class="calculate shadow" cellpadding="5" cellspacing="0" border="0">
                          <tr>
                            <td><label for="date_start">${message(code:'common.date_from')}</label></td>
                            <td><span class="dpicker">
                              <calendar:datePicker name="date_start" value="${inrequest?.date_start}" dateFormat="%d-%m-%Y" onChange="calculatePrice();"/>
                            </span></td>
                          </tr>
                          <tr>
                            <td><label for="date_end">${message(code:'common.date_to')}</label></td>
                            <td><span class="dpicker">
                              <calendar:datePicker name="date_end" value="${inrequest?.date_end}" dateFormat="%d-%m-%Y" onChange="calculatePrice();"/>
                            </span></td>               
                          </tr>
                          <tr>
                            <td><label for="homeperson_id">${message(code:'common.guests')}</label></td>
                            <td>
                              <select id="homeperson_id" name="homeperson_id" onchange="calculatePrice();" style="width:45px">
                              <g:each in="${homeperson}" var="item">            
                                <option <g:if test="${item?.id==inrequest?.homeperson_id}">selected="selected"</g:if> value="${item?.id}">
                                  ${item?.kol}
                                </option>
                              </g:each>
                              </select>
                            </td>                      
                          </tr>
                          <tr>
                            <td colspan="2" valign="top">                  
                              <div id="calculatePriceError" class="notice" style="display:none">
                                <span id="calculatePriceErrorText"></span>
                              </div>
                              <div class="details" id="calculatePriceOutput" style="display:none">
                                <span class="float">
                                  ${message(code:'detail.cost_of_rent')}:<br/>
                                  <small>${message(code:'detail.includes_all_fees')} <a class="tooltip" rel="nofollow" title="${message(code:'detail.total_amount')}">
                                    <img src="${resource(dir:'images',file:'question.png')}" alt="${message(code:'detail.what_is_it')}" style="margin-bottom:-3px"></a>
                                  </small>
                                </span>
                                <span class="ss_price b-rub col" id="calculatePriceOutputText"></span>
                              </div>
                            </td>
                          </tr>
                        </table>
                        <div id="calculatePriceButton" style="margin-top:10px;${ownerUsers[0]?.id==user?.id?'display:none':''}">
                        <g:if test="${isLoginDenied}">
                          <a class="button-glossy lightblue maxi" href="javascript:void(0)" rel="nofollow" onclick="showLoginForm()">${message(code:'button.request')}</a>
                        </g:if><g:else>
                          <a class="button-glossy lightblue maxi" id="message" rel="nofollow" onclick="startMessageZacStat();yaCounter15816907.reachGoal('owner_calculate'); return true;">${message(code:'button.request')}</a>
                          <a id="messageval" style="display:none">${message(code:'button.contact')}</a>
                        </g:else>
                        </div>                        
                        <input type="hidden" name="home_id" value="${home.id}" />
                        <input type="submit" id="calculatePriceFormSubmit" style="display:none" value="${message(code:'detail.calculate')}" />
                      </g:formRemote>
                    </div>
                  </li><!--/noindex-->
                  <li class="search_filter" id="user_container">
                  <g:each in="${ownerUsers}" var="ownerUser">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('user_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('user_container');">
                      <g:if test="${ownerUser.is_agent}">${message(code:'personal.agent')}</g:if><g:else>${message(code:'personal.owner')}</g:else>
                    </a>
                    <div class="search_filter_content">
                      <div class="description clearfix" itemscope itemtype="http://schema.org/Person">
                        <div class="title" itemprop="name">
                          <g:link class="name" mapping="${'pView'+context?.lang}"  params="${[uid:'id'+ownerUser?.id]}" itemprop="url" absolute="true">${ownerUser?.nickname}</g:link>
                        </div>                                          
                        <div class="thumbnail shadow" style="height:auto;margin:5px 0 10px">
                          <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+ownerUser?.id]}" rel="nofollow" title="${message(code:'common.view_profile')}" absolute="true">
                            <img width="200" alt="${ownerUser?.nickname}" border="0" src="${(ownerUser?.picture && !ownerUser.is_external)?imageurl:''}${(ownerUser?.picture)?ownerUser?.picture:resource(dir:'images',file:'user-default-picture.png')}" />
                          </g:link>
                        </div>                        
                      </div>
                    <g:if test="${ownerUser?.id!=user?.id}">
                      <g:if test="${isLoginDenied}">
                      <a class="button-glossy orange maxi" href="javascript:void(0)" rel="nofollow" onclick="showLoginForm()">${message(code:'button.contact')}</a>
                      </g:if><g:else>
                      <a class="button-glossy orange maxi" id="message" rel="nofollow" onclick="startMessageConStat();yaCounter15816907.reachGoal('owner_contant');return true;">${message(code:'button.contact')}</a>
                      <a id="messageval" style="display:none">${message(code:'button.contact').toLowerCase()}</a>
                      </g:else>
                    </g:if><g:if test="${ownerUser?.id==user?.id}">
                      <g:link class="button-glossy lightblue maxi" controller="profile" action="edit" absolute="true" rel="nofollow">${message(code:'button.edit')}</g:link>
                    </g:if>
                      <ul class="verifications-list">
                      <g:if test="${(new Date().getTime() - ownerUser.lastdate.getTime())/1000<notactiveUserParam}">
                        <li class="verifications-list-item" style="padding-right:0">
                        <g:if test="${(new Date().getTime() - ownerUser.lastdate.getTime())<onlinetimediff}">
                          <div class="verifications-legend online"></div>
                          <span class="label">${message(code:'detail.online_status')}</span><br/>
                          <span class="counts" nowrap="nowrap">${message(code:'detail.now_online')}</span>
                        </g:if><g:else>
                          <div class="verifications-legend offline"></div>
                          <span class="label">${message(code:'detail.online_status')}</span><br/>
                          <span class="counts" nowrap="nowrap">${message(code:'detail.was_online')} ${String.format('%td.%<tm.%<tY %<tH:%<tM',ownerUser.lastdate)}</span>
                        </g:else>
                        </li>
                      </g:if>
                      <g:if test="${responseStat.time}">
                        <li class="verifications-list-item">
                          <div class="verifications-legend response"></div>
                          <span class="label">${message(code:'detail.response_time')}</span><br/>
                          <span class="counts" nowrap="nowrap">${message(code:'responsegradation.steps.'+responseStat.time)}</span>
                        </li>
                      </g:if>
                      <g:if test="${responseStat.isDisplayRpCount}">
                        <li class="verifications-list-item">
                          <div class="verifications-legend frequency"></div>
                          <span class="label">${message(code:'detail.frequency_responses')}</span><br/>
                          <span class="counts" nowrap="nowrap">${Math.round(((responseStat.rpCount/(responseStat.rqCount?:1))*100))+'%'}</span>
                        </li>
                      </g:if>
                      <g:if test="${yandexMode}">
                        <li class="verifications-list-item">                          
                          <div class="verifications-legend phone"></div>
                          <span class="label">${message(code:'personal.phone')}</span><br/>
                          <span class="counts" nowrap="nowrap">${ownerUser.tel}</span>
                        </li>
                        <li class="verifications-list-item">                          
                          <div class="verifications-legend date"></div>
                          <span class="label">${message(code:'detail.date_of_modification')}</span><br/>
                          <span class="counts" nowrap="nowrap">${String.format('%td.%<tm.%<tY',home.moddate>(new Date()-30)?home.moddate:new Date()-3)}</span>
                        </li>
                      </g:if><g:elseif test="${ownerUser.tel}"><!--noindex-->
                        <li class="verifications-list-item">                          
                          <div class="verifications-legend phone"></div>
                          <span class="label">${message(code:'personal.phone').capitalize()}</span><br/>
                          <span class="counts" nowrap="nowrap">
                            ${ownerUser.tel.split('\\)')[0]})
                          <g:each in="${ownerUser.tel.split('\\)')[1][0..-3]}" var="item" status="i">
                            <span class="p">&nbsp;&nbsp;</span>
                          </g:each>
                            ${ownerUser.tel[-2,-1]}                          
                          </span>
                          <div class="paddtop">
                            <a class="show_more_link" href="javascript:void(0)" rel="nofollow" onclick="jQuery('#show_phone').slideToggle();yaCounter15816907.reachGoal('show_phone');return true;" style="font-size:14px">${message(code:'detail.view_phone')}</a>
                            <div id="show_phone" style="display:none">
                              <p align="justify"><g:rawHtml>${message(code:'detail.view_phone.alt')}</g:rawHtml>.</p>
                            </div>
                          </div>
                        </li><!--/noindex-->
                      </g:elseif>      
                      </ul>
                    </div>
                  </g:each>                                
                  </li>
                <g:if test="${ownerUsers[0]?.id!=user?.id && ownerClient?.is_reserve}">  
                  <li class="search_filter" id="bron_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('bron_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('bron_container');">${message(code:ownerClient?.reserve_id in [1,2,3,4]?'common.secure_payments':'payout.scheme')}</a>        
                    <div class="search_filter_content" style="padding:5px 0">
                    <g:if test="${ownerClient?.reserve_id in [1,2,3,4]}">
                      <p class="clearfix"><img class="thumbnail userpic" src="${resource(dir:'images/anonses',file:'bron.png')}" alt="${message(code:'common.secure_payment_via_website')}" title="${message(code:'common.secure_payment_via_website')}" hspace="12" align="left" style="border:none" />
                        <font color="#333">${message(code:'profile.secure.info')}</font>
                        <g:link class="tooltip" controller="index" action="oferta" fragment="use_6" target="_blank" title="${message(code:'common.secure_payments')}" base="${context?.absolute_lang}"><img alt="${message(code:'common.secure_payments')}" src="${resource(dir:'images',file:'question.png')}" border="0" style="margin-bottom:-2px" /></g:link></p>                      
                    </g:if>
                      <ul class="clearfix pay-list mini">                        
                        <li class="float"><span class="icons vbv" title="Verified by Visa"></span></li>
                        <li class="float"><span class="icons mcc" title="MasterCard SecureCode"></span></li>
                        <li class="float"><span class="icons wma" title="${message(code:'oferta.attestat')} WebMoney"></span></li>
                      </ul>
                      <p style="font-size:11px;line-height:15px;color:#333" align="justify">
                        <g:rawHtml>${message(code:'inbox.view.secure.info')}</g:rawHtml>
                      </p>                      
                    </div>
                  </li>
                </g:if><g:if test="${metro}">             
                  <li class="search_filter" id="metro_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('metro_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('metro_container');">${message(code:'detail.metro_near')}</a>
                    <ul class="search_filter_content">
                    <g:each in="${metro}" var="item" status="i">
                      <li class="clearfix dott ${(i+1==metro.size())?'last':''}">
                        <g:link class="show_more_link" mapping="${homecity?.domain?('hSearchMetroDomain'+context?.lang):('hSearchMetro'+context?.lang)}" params="${homecity?.domain?[metro_url:item.urlname]:[where:home.city,country:breadcrumbs.country.urlname,metro_url:item.urlname]}" base="${breadcrumbs.base}">${item['name'+context?.lang].replace('м. "','').replace('m. "','').replace('"','')}</g:link>
                      </li>
                    </g:each>
                    </ul>
                  </li>
                </g:if><g:if test="${citysight}">             
                  <li class="search_filter" id="citysights_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('citysights_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('citysights_container');">${message(code:'detail.citysights')}</a>
                    <ul class="search_filter_content">
                    <g:each in="${citysight}" var="item" status="i">
                      <li class="clearfix dott ${(i+1==citysight.size())?'last':''}">
                        <g:link class="show_more_link" mapping="${homecity?.domain?('hSearchCitysightDomain'+context?.lang):('hSearchCitysight'+context?.lang)}" params="${homecity?.domain?[citysight_url:item.urlname]:[where:home.city,country:breadcrumbs.country.urlname,citysight_url:item.urlname]}" base="${breadcrumbs.base}">${item['name'+context?.lang]}</g:link>
                      </li>
                    </g:each>                    
                    </ul>
                  </li>
                </g:if><g:if test="${anotherHomes}">             
                  <li class="search_filter" id="another_homes_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('another_homes_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('another_homes_container');">${message(code:'detail.my_other_ads')}</a>
                    <ul class="search_filter_content" style="padding:15px 6px">
                    <g:each in="${anotherHomes}" var="home" status="i">              
                      <li class="hlisting offer-rent clearfix">
                        <div class="item housing relative clearfix">
                          <div class="thumbnail shadow" style="width:220px;height:160px">
                            <g:if test="${home.is_index}"><a href="<g:createLink mapping='${City.get(home.city_id)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}' params='${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" title="${home?.name}"></g:if>
                            <g:else><span class="pointer" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id).urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                              <img class="photo" width="220" height="160" src="${(home?.mainpicture)?photourl+'t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" alt="${home?.name}" border="0" />
                            <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                          </div>
                        <g:if test="${anotherPrices[i]!=0}">
                          <div class="photo_item_details">
                            <span class="price ss_price b-rub">${anotherPrices[i]}<g:rawHtml>${valutaSym}</g:rawHtml></span>                            
                          </div>
                        </g:if>
                        </div>
                        <div class="fn title">
                          <g:if test="${home.is_index}"><a class="url name" href="<g:createLink mapping='${City.get(home.city_id)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}' params='${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}' base='${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}'/>" rel="self bookmark" title="${home?.name}"></g:if>
                          <g:else><span class="name" onclick="transit(${context.is_dev?1:0},['${"home_"+home?.linkname}','${home.city}','${Country.get(home.country_id).urlname}'],'','','','${context?.lang}')" title="${home?.name}"></g:else>
                            <g:shortString text="${home?.name}" length="18"/>
                          <g:if test="${home.is_index}"></a></g:if><g:else></span></g:else>
                        </div>
                        <div class="description">
                          <ul class="details-list">
                            <li class="details-list-item location">
                              <g:if test="${anotherDistances[i]!=0}">${anotherDistances[i]} ${message(code:'common.km_from_choosen')}</g:if>
                              <g:else>${message(code:'common.same_place')}</g:else>
                            </li>
                          </ul>
                        </div> 
                        <div class="lister fn">${User.findWhere(client_id:home?.client_id).nickname}</div>
                      </li>
                    </g:each>                    
                    </ul>                    
                  </li>
                </g:if>    
                </ul>              
              </td>
              <td colspan="3" valign="top" class="body shadow">
                <div class="content" style="min-height:620px">
                  <ul id="tabs1" class="subnav first">                         
                    <li class="selected" onclick="viewCell('tabs1',0,'layers1')">${message(code:'photo')} (${homephoto.size()})</li>
                    <li id="video" <g:if test="${homevideo.size()==0}">style="display:none"</g:if> onclick="viewCell('tabs1',1,'layers1')">${message(code:'video')} (${homevideo.size()})</li>
                    <li id="map" onclick="viewCell('tabs1',2,'layers1');showMap();Yandex();$('inf').click();">${message(code:'map')}</li>
                    <li id="panoram" <g:if test="${!homecity?.is_panoram}">style="display:none"</g:if> onclick="viewCell('tabs1',3,'layers1')">${message(code:'streetview')}</li>
                    <li id="comments" onclick="viewCell('tabs1',4,'layers1');showCom();">${message(code:'list.reviews')} (<span id="comments_col">${home?.nref?:0}</span>)</li>
                    <li onclick="viewCell('tabs1',5,'layers1');showCal();">${message(code:'calendar')}</li>
                    <li class="header-list-item flag dropdown">
                      <span class="actions relative">
                        <g:link class="icon print" controller="home" action="viewprint" id="${home.id}" rel="nofollow" target="_blank" title="${message(code:'common.print_version')}" base="${context?.mainserverURL_lang}"></g:link>                        
                        <a id="flag_link" class="icon flag" href="javascript:void(0)" rel="nofollow" onclick="this.parentElement.parentElement.toggleClassName(' open')" title="${message(code:'detail.report_ads')}"></a>
                      <g:if test="${breadcrumbs.country.urlname in ['russia','ukraine','belorussia','kazakhstan']}">
                        <g:link class="icon trace" controller="home" action="trace" id="${home?.id}" rel="nofollow" target="_blank" title="${message(code:'ads.howtoget')}" base="${context?.mainserverURL_lang}"></g:link>
                      </g:if><g:else>
                        <a class="icon trace" style="background:none;cursor:default;margin-right:6px" rel="nofollow"></a>
                      </g:else>
                      </span><!--noindex-->
                      <div id="flag_container" class="header-dropdown">
                        <div class="header-dropdown-arrow"></div>
                        <div class="header-dropdown-inner">
                          <div class="header-dropdown-section">
                            <ul class="header-dropdown-list">
                              <li class="header-dropdown-list-item">
                                <a href="javascript:void(0)" rel="nofollow" onclick="showErrorWindow(1)">${message(code:'detail.spam')}</a>
                              </li>
                              <li class="header-dropdown-list-item">
                                <a href="javascript:void(0)" rel="nofollow" onclick="showErrorWindow(2)">${message(code:'detail.wrong_info')}</a>
                              </li>
                              <li class="header-dropdown-list-item">
                                <a href="javascript:void(0)" rel="nofollow" onclick="showErrorWindow(3)">${message(code:'detail.inappropriate_content')}</a>
                              </li>
                              <li class="header-dropdown-list-item">
                                <a href="javascript:void(0)" rel="nofollow" onclick="showErrorWindow(4)">${message(code:'detail.other')}</a>                
                              </li>
                            </ul>
                          </div>
                        </div>
                      </div><!--/noindex-->
                    </li>                                  
                  </ul><!--noindex-->
                  <div class="padd10">
                    <ul class="share-box">
                      <li class="share-item">
                        <a class="twitter-share-button twitter-count-horizontal" href="http://twitter.com/share" data-counturl="${g.createLink(mapping:homecity?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang),params:homecity?.domain?[linkname:home?.linkname]:[country:Country.get(home?.country_id)?.urlname,city:home.city,linkname:home?.linkname],base:homecity?.domain?'http://'+homecity.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL)}" data-count="horizontal" data-lang="${context?.lang?'en':'ru'}" rel="nofollow" target="_blank">${message(code:'detail.tweet')}</a>                        
                      </li>
                      <li class="share-item">
                        <div class="fb-like" data-width="90" data-layout="button_count" data-action="like" data-show-faces="true" data-share="false" data-font="tahoma"></div>
                      </li>
                      <li class="share-item">
                        <g:plusone size="medium" count="true"></g:plusone>
                      </li>          
                      <li class="share-item">
                        <div id="vk_like"></div>
                      </li>
                      <li class="share-item" id="mm">
                        <a class="mrc__plugin_uber_like_button" href="http://connect.mail.ru/share" data-mrc-config="{'type':'button','caption-mm':'1','caption-ok':'1','counter':'true','width':'100%','nt':'1'}" rel="nofollow" target="_blank">${message(code:'detail.like')}</a>
                        <script src="http://cdn.connect.mail.ru/js/loader.js" type="text/javascript" charset="UTF-8"></script>
                      </li>
                    </ul>
                  </div><a name="infrastructure"></a>
                  <div id="layers1" class="dashboard-content" style="max-height:540px">
                    <div rel="layer" class="relative">
                    <g:if test="${homephoto.size()>0}">
                      <g:if test="${curdiscount}">
                      <div class="discount-container l"> 
                        <div class="discount-price">                      
                          <g:if test="${curdiscount==2}">-${Discountpercent.findByPercent(discounts.hot.discount)?.name}</g:if>
                          <g:elseif test="${curdiscount==1}">-${Discountpercent.findByPercent(discounts.long.discount)?.name}</g:elseif>
                          <g:else>-${discounts.hot?.modstatus?Discountpercent.findByPercent(discounts.hot.discount)?.name:Discountpercent.findByPercent(discounts.long.discount)?.name}</g:else>
                        </div>                        
                      </div>
                      </g:if><g:if test="${home?.is_fiesta}">
                      <div class="fiesta-container l" title="${message(code:'common.party')}"></div>
                      </g:if>                    
                      <div id="galleria" class="slideshow" valign="top" style="height:512px"><!--/noindex-->
                      <g:each in="${homephoto}" var="item">
                        <div itemprop="photo" itemscope itemtype="http://schema.org/ImageObject">
                          <span itemprop="name">${home?.name}</span>
                          <img src="${photourl}${item?.picture}" alt="${item?.ptext?:home?.name}" itemprop="image" />                          
                        </div>
                      </g:each><!--noindex-->
                      </div>
                      <g:javascript>
                        Galleria.addTheme({name:"classic",author:"Arenda",css:"${createLinkTo(dir:'css',file:'galleria.classic.css')}",defaults:{},init:function(){this.bind("loadstart",function(a){a.cached||(this.$("loader").show().fadeTo(200,0.4),this.$("info").toggle(this.hasInfo()),jQuery(a.thumbTarget).css("opacity",1).parent().siblings().children().css("opacity",0.6))});this.bind("loadfinish",function(){var a=this;this.$("loader").fadeOut(200);setTimeout(function(){a.$("stage").addClass("galleria-loaded")},300)})}});
                        jQuery("#galleria").galleria({ width: 710, height: 512 });
                      </g:javascript>
                    </g:if>
                    </div>
                    <div rel="layer" style="display:none">
                    <g:if test="${homevideo.size()>0}">
                      <div class="slideshow" valign="top" style="height:512px">
                        <div id="slideshow" style="height:512px"><!--/noindex-->
                        <g:each in="${homevideo}" var="item">
                          <div itemprop="video" itemscope itemtype="http://schema.org/VideoObject">
                            <span itemprop="name">${home?.name}</span>
                            <img itemprop="thumbnail" src="http://img.youtube.com/vi/${item?.videoid}/default.jpg" />
                            <span itemprop="description">${item?.vtext?:home?.name}</span>
                          </div>
                        </g:each><!--noindex-->                        
                        </div>
                        <g:javascript library="slideshow" />
                        <g:javascript>
                          var vimges = [
                          <g:each in="${homevideo}" var="item" status="i">      
                            {"code":"${item?.videoid}",
                             "name":"${home?.name}",                       
                             "desc":"${item?.vtext?:home?.name}",                       
                             "id":${item?.id}
                            }<g:if test="${i!=homevideo.size()-1}">,</g:if>
                          </g:each>
                          ];                      
                          var slideshow = new slideshow(vimges,'slideshow',0,0,0,0);                      
                        </g:javascript>
                      </div>
                    </g:if>
                    </div>
                    <div rel="layer" style="display:none">
                      <div class="slideshow" valign="top">                    
                        <div id="map_canvas" style="height:512px;width:710px;border:1px solid #fff"></div>
                        <input type="hidden" id="x" name="x" value="${home?.x}" />
                        <input type="hidden" id="y" name="y" value="${home?.y}" />
                        <input type="hidden" id="geocodeaddress" value="${home?.address}" />
                      </div>
                    </div>
                    <div rel="layer" style="display:none"><g:if test="${homecity?.is_panoram}">
                      <div class="slideshow" valign="top">
                        <object id="stview" width="710" height="512" type="application/x-shockwave-flash" data="http://yandex.st/swf/streetview/2_23/sv-player-blog.swf">
                          <param name="allowFullScreen" value="true" />
                          <param name="allowNetworking" value="internal" />
                          <param name="allowScriptAccess" value="never" />
                          <param name="flashVars" value="lang=${context?.lang?'en':'ru'}&l=stv&ll=${home.x/100000}%2C${home.y/100000}&cr=0&view=-51.17%2C0%2C89.99999987427145%2C61.20772630566201" />
                          <param name="wmode" value="transparent" />
                        </object>                        
                      </div></g:if>
                    </div>
                    <div rel="layer" style="display:none">                    
                      <div class="slideshow" style="min-height:514px;padding: 0 0 28px 0">
                        <g:formRemote name="commentsForm" url="[action:'comments']" before="jQuery('#review_list').html('')" update="[success:'review_list']">
                          <input type="submit" id="commentsFormSubmit" style="display:none" value="${message(code:'button.view')}" />
                          <input type="hidden" name="home_id" value="${home.id}" />
                          <input type="hidden" name="owner_id" value="${ownerUsers[0]?.id}" />
                          <input type="hidden" name="u_id" value="${user?.id?:0}" />
                        </g:formRemote>
                        <div id="review_list" style="height:${(user && user?.client_id!=home?.client_id)?'348':'448'}px;border-bottom:1px solid #f0f0f0"><!--/noindex-->
                        <g:each in="${comments.records}" var="comm">
                          <div itemprop="review" itemscope itemtype="http://schema.org/Review">
                            <span itemprop="author" itemscope itemtype="http://schema.org/Person">
                              <span itemprop="name">
                                <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+comm.user_id]}" itemprop="url" absolute="true">${comm.nickname}</g:link>
                              </span>
                            </span>
                            <span itemprop="datePublished">${g.formatDate(date:comm?.comdate,format:'yyyy-MM-dd\'T\'HH:mmZ',locale:Locale.ENGLISH)}</span>
                            <p itemprop="${comm.rating==1?'contra':(comm.rating==2?'pro':'description')}">${comm.comtext}</p>
                          </div>
                        </g:each><!--noindex-->
                        </div>
                      <g:if test="${user && user?.client_id!=home?.client_id}">                        
                        <div class="calculate shadow" style="width:97%;margin:5px auto">
                          <h2 class="padd20">${message(code:'trip.review.send')}</h2>
                          <g:form action="addcomment" base="${context?.mainserverURL_lang}">
                            <table class="form" cellpadding="3" cellspacing="0" border="0">
                              <tr>
                                <td valign="top">
                                  <label class="mini" for="comtext">${message(code:'detail.message')}</label>
                                  <textarea name="comtext" id="comtext" onkeydown="textLimit(this.id);" onkeyup="textLimit(this.id);" style="width:500px"></textarea>
                                </td>
                              </tr>
                              <tr>
                                <td valign="top">
                                  <label for="rating" class="mini">${message(code:'detail.vote_review')}</label>
                                  <select name="rating" style="width:188px">
                                    <option value="0">${message(code:'profile.rating.neutral')}</option>
                                    <option value="1">${message(code:'profile.rating.negative')}</option>
                                    <option value="2">${message(code:'profile.rating.positive')}</option>
                                  </select>
                                  <input type="submit" class="button-glossy orange mini" value="${message(code:'button.send')}" />
                                </td>
                              </tr>
                            </table>
                            <input type="hidden" name="home_id" value="${home?.id}" />
                          </g:form>                    
                        </div>
                      </g:if><g:elseif test="${!user}">                        
                        <div class="padd20">
                          <h2 class="ins">${message(code:'trip.review.send')}</h2>
                          <p>${message(code:'detail.want_review')} <a href="javascript:void(0)" rel="nofollow" onclick="showLoginForm(2,'login_lbox',this)">${message(code:'login.auth')}</a>.</p>
                        </div>
                      </g:elseif>
                      </div>
                    </div>
                    <div rel="layer" style="display:none">
                      <div class="slideshow" valign="top">
                        <div id="calendar"></div>
                      </div>
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                  </div>                
                </div><!--/noindex-->
                <g:render template="/detail_cities"/><!--noindex-->
                <g:render template="/correction"/>        
                <g:plusoneScript lang="${context?.lang?'en-US':'ru'}" /><!--/noindex-->
  </body>
</html>
