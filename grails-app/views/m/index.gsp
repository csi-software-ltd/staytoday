<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />    
    <meta name="layout" content="m" />
    <link rel="canonical" href="${createLink(uri:'',base:context?.absolute_lang)}" />
    <link rel="alternate" hreflang="ru" href="${createLink(uri:'',base:context?.mobileURL)}" />
    <link rel="alternate" hreflang="en" href="${createLink(uri:'',base:context?.mobileURL+(context?.lang?'':'/en'))}" />    
    <g:javascript>
      var initialLocation, browserSupportFlag=new Boolean(),
          days=${days_between?:1}, persons=${inrequest?.homeperson_id?:1},
          datePickerDate=new XDate("${formatDate(format:'yyyy-MM-dd',date:inrequest?.date_start?:new Date())}"),
          dateEnd=new XDate("${formatDate(format:'yyyy-MM-dd',date:inrequest?.date_end?:new Date()+1)}");
      function init(){        
        datePicker('date_start');        
        jQuery('#date_start').val(datePickerDate.toString("ddd, d MMM, yyyy","${context?.lang?'en':'ru'}"));
        jQuery('#date_end').val(dateEnd.toString("ddd, d MMM, yyyy","${context?.lang?'en':'ru'}"));
        jQuery('#where').bind("input", function() {       
          where_autocomplete();
        });        
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
      function where_autocomplete(){ 
        var wList = jQuery('#where_autocomplete');
        var where = jQuery('#where').val();
        if(where.length>1){
          jQuery.mobile.showPageLoadingMsg("b", "${message(code:'spinner.message.mobile')}", false);
          jQuery.ajax({
            url: "${createLink(contriller:'m',action:'where_autocomplete')}",
            data: {where:where},                           
            dataType: 'jsonp',
            jsonp: 'jsoncallback',            
            success: function(json){      
              var res=json.records, html='';
              for(var i=0, len=res.length; i<len; i++) {
                var sWhere="\'"+res[i].name+"\'";
                html+= '<li onclick="setWhereVal('+sWhere+')">'+res[i].name+'</li>';
              }
              wList.html(html);
              wList.listview('refresh');
            },
            complete: function(xhr, status) {       
              jQuery.mobile.hidePageLoadingMsg();
            }
          });       
        }else{
          wList.html('');
          wList.listview('refresh');
        }
      }
      function setWhereVal(sWhere){
        jQuery('#where').val(sWhere);
        var wList = jQuery('#where_autocomplete');
        wList.html('');
        wList.listview('refresh');
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
      }
      function search(){  
        var where=$('where').value;
        if(!where.length)
          searchNear();
        else
          jQuery('form[name="search_form"]').submit();
      }
      function searchNear() {  
        if(navigator.geolocation) {           
          browserSupportFlag = true;
          navigator.geolocation.getCurrentPosition(function(position) {                        
            setCurPosition(position.coords.latitude,position.coords.longitude);
          }, function(error) {
            if(error.TIMEOUT!=undefined){
              alert('${message(code:'mobile.nolocation')}');
            }else{
              handleNoGeoLocation(browserSupportFlag);
            }
          },{ timeout: 40000, enableHighAccuracy: true });
        } else if (google.gears) {      
          browserSupportFlag = true;
          var geo = google.gears.factory.create('beta.geolocation');
          geo.getCurrentPosition(function(position) {            
            setCurPosition(position.coords.latitude,position.coords.longitude);
          }, function() {
            handleNoGeoLocation(browserSupportFlag);
          });
        } else {
          browserSupportFlag = false;
          handleNoGeolocation(browserSupportFlag);
        }
      }  
      function setCurPosition(latitude,longitude){      
        initialLocation = new google.maps.LatLng(latitude,longitude);        
        $('cur_y').value=Math.round(latitude*100000);
        $('cur_x').value=Math.round(longitude*100000);
        $('is_near').value=1;
        jQuery('form[name="search_form"]').submit();        
      }
      function handleNoGeolocation(errorFlag) {
        if (errorFlag == true)
          alert('${message(code:"mobile.geolocation.unavailable")}');     
        else
          alert('${message(code:"mobile.geolocation.unavailable")}');                       
      }      
    </g:javascript>
  </head>
  <body onload="init()">
      <div data-role="content" class="st">
        <g:form name="search_form" action="s" base="${context?.mobileURL_lang}" method="get">
          <div class="ui-body ui-body-b ui-corner-all st">
            <label for="where"><b>${message(code:'mobile.where')}:</b></label>
            <input type="search" name="where" id="where" class="ui-body-c" data-inline="true" value="${inrequest?.where?:''}" placeholder="${message(code:'mobile.search.near')}" />
            <ul id="where_autocomplete" data-role="listview" data-inset="true" data-theme="a" style="margin:0"></ul>
          </div>    
          <div class="ui-body" style="padding:0 8px">
            <div data-role="fieldcontain" class="nopadd">
              <div class="ui-grid-a">
                <div class="ui-block-a"><label for="hometype_id" class="select padd"><b>${message(code:'common.home_type')}:</b></label></div>
                <div class="ui-block-b">
                  <g:select data-theme="a" name="hometype_id" value="${inrequest?.hometype_id?:0}" from="${Hometype.list([sort:'regorder'])}" optionValue="${'name'+context?.lang}" optionKey="urlname" noSelection="${['all':message(code:'common.any')]}"/>                
                </div>              
              </div>
            </div>
          </div>     
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
                <a id="date_less" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-left ui-disabled" onclick="dayLess()">-</a>
                <b id="days">${days_between?:1}</b>
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
              <div class="ui-block-a" style="padding-top:18px"><label for="persons"><b>${message(code:'mobile.guests')}:</b></label></div>
              <div class="ui-block-b">
                <a id="person_less" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-left ui-disabled" onclick="personLess()">-</a>
                <b id="persons">${inrequest?.homeperson_id?:1}</b>
                <a id="person_more" data-role="button" data-inline="true" data-theme="a" data-corners="false" class="ui-corner-right" onclick="personMore()">+</a>
              </div>
              <input type="hidden" name="homeperson_id" id="personsh" value="${inrequest?.homeperson_id?:1}" />
            </div>
          </div>          
          <div class="ui-body" style="padding:8px 0">
            <input type="button" data-theme="f" value="${message(code:'mobile.search')}" onclick="search()" />
            <input type="hidden" name="sort" id="sort" value="0" />
            <input type="hidden" id="cur_x" name="x" value="0" />
            <input type="hidden" id="cur_y" name="y" value="0" />
            <input type="hidden" id="is_near" name="is_near" value="0" />
          </div>
        </g:form>
        <div class="ui-body st">
          <h3>${message(code:'index.pop_directions').capitalize()}</h3>
        <g:each in="${citycloud}" var="city">
          <g:if test="${city.value.is_index}"><a class="ui-link" href="<g:createLink mapping='${'hSearch'+context?.lang}' params='${[where:city.key,country:Country.get(city.value.country_id)?.urlname]}' base='${context?.mobileURL}'/>" rel="tag" title="${message(code:'common.flats_per_night_in')} ${city.value.name2}" style="font-size:${city.value.count>citycloudParams.maxFontCount?'20px':city.value.count>citycloudParams.middleFontCount?'15px':'10px'};white-space:nowrap"></g:if>
          <g:else><span class="ui-link" style="font-size:${city.value.count>citycloudParams.maxFontCount?'24px':city.value.count>citycloudParams.middleFontCount?'17px':'10px'};white-space:nowrap" onclick="transit(0,['${city.key}','${Country.get(city.value.country_id)?.urlname}'],'','','${context.mobileURL.minus('http://')}','${context?.lang}')"></g:else>
            ${city.key}<g:if test="${city.value.is_index}"></a></g:if><g:else></span></g:else> 
        </g:each></div>
      </div>
      <div data-role="footer" data-position="fixed" data-theme="a">
        <div data-role="navbar" role="navigation">
          <ul>
            <li><a href="${createLink(uri:'/about',base:context?.mobileURL_lang)}">${Infotext.findByControllerAndAction('index','about')['name'+context?.lang]}</a></li>  
            <li><a href="${createLink(uri:'/terms',base:context?.mobileURL_lang)}">${Infotext.findByControllerAndAction('index','terms')['name'+context?.lang]}</a></li>
            <li><a href="${createLink(uri:'/help',base:context?.mobileURL_lang)}">${Infotext.findByControllerAndAction('help','faq')['name'+context?.lang]}</a></li>
          </ul>
        </div>
      </div>    
  </body>
</html>
