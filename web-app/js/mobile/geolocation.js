// Note that using Google Gears requires loading the Javascript
// at http://code.google.com/apis/gears/gears_init.js
  var browserSupportFlag =  new Boolean();
  function searchHomeNear() {  
    // Try W3C Geolocation (Preferred) 
    if(navigator.geolocation) {   
      browserSupportFlag = true;
      navigator.geolocation.getCurrentPosition(function(position) {    
        isNearSearch=1;      
        setCurPosition(position.coords.latitude,position.coords.longitude);
      }, function(error) {
        if(error.TIMEOUT!=undefined){
          if(isAndroid || isIphone){
            navigator.notification.alert(
              'Не удалось определить Ваше текущее местоположение.\nПовторите несколько позже.',  // message
              function(){},         // callback
              'Внимание!',            // title
              'OK'                  // buttonName
            ); 
          }else    
            alert('Не удалось определить Ваше текущее местоположение.\nПовторите несколько позже.');
            jQuery.mobile.changePage('index.html#main_page');
          }else{
            handleNoGeoLocation(browserSupportFlag);
          }
      },{ timeout: 40000,enableHighAccuracy: true });
    // Try Google Gears Geolocation
    } else if (google.gears) {      
      browserSupportFlag = true;
      var geo = google.gears.factory.create('beta.geolocation');
      geo.getCurrentPosition(function(position) {      
        isNearSearch=1;
        setCurPosition(position.coords.latitude,position.coords.longitude);
      }, function() {
        handleNoGeoLocation(browserSupportFlag);
      });
    // Browser doesn't support Geolocation
    } else {
      browserSupportFlag = false;
      handleNoGeolocation(browserSupportFlag);
    }
  }  
  function setCurPosition(latitude,longitude){      
    initialLocation = new google.maps.LatLng(latitude,longitude);        
    if($('#cur_y').length)
      $('#cur_y').val(Math.round(latitude*100000));
    if($('#cur_x').length)
      $('#cur_x').val(Math.round(longitude*100000));
    searchHome(0,1,1);    
  }  
  function handleNoGeolocation(errorFlag) {
    if (errorFlag == true) {
      if(isAndroid || isIphone){
        navigator.notification.alert(
          'Сервис геолокации недоступен!',  // message
          function(){},         // callback
          'Внимание!',            // title
          'OK'                  // buttonName
        ); 
      }else  
        alert("Сервис геолокации недоступен!");     
    } else {
      if(isAndroid || isIphone){
        navigator.notification.alert(
          'Сервис геолокации недоступен!',  // message
          function(){},         // callback
          'Внимание!',            // title
          'OK'                  // buttonName
        ); 
      }else  
        alert("Сервис геолокации недоступен!");       
    }
    jQuery.mobile.changePage('index.html#main_page');    
  }
