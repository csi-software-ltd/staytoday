function transit(iType,lsLink,sParams,iOpen,sDomain,sLang){
  sParams = sParams || '';
  sDomain = sDomain || (iType==1?'localhost:8080':'staytoday.ru');
  iOpen = iOpen || 0;
  var loc = 'http://'+sDomain;
  if (iType==1)
    loc += '/Arenda';  
  if(sLang){
    loc += '/en';  
    if(!lsLink.length)  
      loc += '/';
  }    
  for (var i = lsLink.length - 1; i >= 0; i--)
    loc += '/' + lsLink[i].replace(/\s/g,'+');  
  var lsParams;
  if (sParams!='')
    lsParams = sParams.split(',');
  else
    lsParams = [];
    
  for (var i = lsParams.length - 1; i >= 0; i--) {
    if (i==lsParams.length - 1)
      loc += '?';
    else
      loc += '&';    
    loc += lsParams[i].replace(/\s/g,'+');
  }
  if (iOpen)
    window.open(loc);
  else
    location.assign(loc);  
}

function exttransit(lsLink,sParams,iOpen){
  sParams = sParams || ''
  iOpen = iOpen || 0
  var loc = 'http://'
  for (var i = lsLink.length - 1; i >= 0; i--) {
    loc += lsLink[i].replace(/\s/g,'+');
    if (i) loc+='/';
  }
  var lsParams;
  if (sParams!='')
    lsParams = sParams.split(',');
  else
    lsParams = [];  
  for (var i = lsParams.length - 1; i >= 0; i--) {
    if (i==lsParams.length - 1)
      loc += '?';
    else
      loc += '&';    
    loc += lsParams[i].replace(/\s/g,'+');
  }
  if (iOpen)
    window.open(loc);
  else
    location.assign(loc);  
}
function setLangUrl(sLang){
  var sUrl=location.href.replace('/en','');    
  if(sLang!='ru'){  
    var sUrlTmp='';
    if(location.href.split(location.host).length>1) 
      sUrlTmp=location.href.split(location.host)[1];    
    sUrlTmp=sUrlTmp.replace('/en','');    
    if(sUrlTmp.indexOf('/Arenda')>-1)
      sUrlTmp=sUrlTmp.replace('/Arenda','')      
    if(!sUrlTmp.length)
      sUrlTmp='/';    
    if(location.href.indexOf('/Arenda')>-1)
      sUrl=location.host+'/Arenda'+'/en'+sUrlTmp;
    else
      sUrl=location.host+'/en'+sUrlTmp;      
    sUrl=location.protocol+'//'+sUrl;
  }  
  location.assign(sUrl);
}
function setValute(sUrl,lId,from){            
  jQuery.ajax({
      url: sUrl+'/index/changeValuta',                     
      dataType: 'json',
      data:{id:lId,fromList:from},      
      timeout: 30000,        
      success: function(json){      
        location.reload(true);        
      },                   
      error: function(){       
      },
      complete : function(xhr, status) {                      
      }  
  });
}
function setVersion(ua){
  return ((ua.match(/(iPad|iPhone|iPod|android|webOS)/i))||
    (ua.match(/(up.browser|up.link|mmp|symbian|smartphone|midp|wap|vodafone|o2|pocket|kindle|mobile|pda|psp|treo)/i))||      
    (ua.match(/operamini/i))||
    (ua.match(/blackberry/i))||
    (ua.match(/(palmos|palm|hiptop|avantgo|plucker|xiino|blazer|elaine)/i))||
    (ua.match(/(windowsce; ppc;|windows ce; smartphone;|windows ce;iemobile)/i)));
}
