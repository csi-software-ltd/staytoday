//используется для нормирования Y коодинаты
var consar=new Array(-85051128,-83979259,-82676284,-81093213,-79171334,-76840816,-74019543,-70612614,-66513260,-61606396,-55776579,-48922499,-40979898,-31952162,-21943045,-11178401,0,11178401,21943045,31952162,40979898,48922499,55776579,61606396,66513260,70612614,74019543,76840816,79171334,81093213,82676284,83979259,85051128);
//первая часть — само преобразовании координат в деление, название функций и их содержимое взято с викимапии
function getdatakvname(x, y, curzoomkv){
  var xdel=0, ydel=0, xline=0, yline=0,
      x1=-180000000, x2=180000000, y1=-85051128, y2=85051128, y1cons=0, y2cons=32, yconsdel=0, n=0, z=curzoomkv-1;
  while(z>=0){
    xdel=Math.round((x1+x2)/2);
    if(n<4){yconsdel=(y1cons+y2cons)/2; ydel=consar[yconsdel];}
    else{ydel=Math.round((y1+y2)/2);}
    if(x<=xdel){x2=xdel; xline=xline*2;}
    else{x1=xdel+1; xline=xline*2+1;}
    if(y<=ydel){y2=ydel; y2cons=yconsdel; yline=yline*2;}
    else{y1=ydel+1; y1cons=yconsdel; yline=yline*2+1;}
    z--;
    n++;
  }
  var out=new Array();
  out.xline=xline;
  out.yline=yline;
  return out;
}
function cheakpoint(x, y, xline, yline, curzoomkv){
  var xdel=0, ydel=0, xlinetest=0, ylinetest=0, test=0, 
      x1=-180000000, x2=180000000, y1=-85051128, y2=85051128, y1cons=0, y2cons=32, yconsdel=0, n=0, z=curzoomkv-1;          
  while(z>=0){
    xdel=Math.round((x1+x2)/2);
    if(n<4){yconsdel=(y1cons+y2cons)/2; ydel=consar[yconsdel]}
    else{ydel=Math.round((y1+y2)/2)}
    test=Math.pow(2, z);
    xlinetest=xline&test;
    ylinetest=yline&test;
    if(xlinetest>0){x1=xdel+1}
    else{x2=xdel}
    if(ylinetest>0){y1=ydel+1; y1cons=yconsdel}
    else{y2=ydel; y2cons=yconsdel}
    z--;
    n++;
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
function request_geo_objectsin(iVar){ 
  require_block={};    
  curzoomkv=map.getZoom()-1;
  var bounds=map.getBounds(),
      bounds_sw=bounds[0],
      bounds_ne=bounds[1],
      x1point=Math.round(bounds_sw[1]*1000000),
      y1point=Math.round(bounds_sw[0]*1000000),
      x2point=Math.round(bounds_ne[1]*1000000),
      y2point=Math.round(bounds_ne[0]*1000000);              
  if(x1point<-180000000){x1point=-180000000}if(x2point<-180000000){x2point=-180000000}if(x1point>180000000) {x1point=180000000}if(x2point>180000000) {x2point=180000000}if(y1point<-85051128) {y1point=-85051128}if(y2point<-85051128) {y2point=-85051128}if(y1point>85051128) {y1point=85051128}if(y2point>85051128) {y2point=85051128}  
  outar=[];
  outar=getdatakvname(x1point, y1point, curzoomkv);
  var xline=outar.xline,
      yline=outar.yline,
      maks=Math.pow(2, curzoomkv)-1,
      vlez=0, xsdvig=0,
      xlinet=xline,
      ylinet=yline;
  while(vlez!=1){
    outar=cheakpoint(x2point, y1point, xlinet, ylinet, curzoomkv);
    vlez=outar.res;
    xsdvig++;
    xlinet=xlinet+1;
    if(xlinet>maks){xlinet=0}
  }
  vlez=0;
  var ysdvig=0,
      xlinet=xline,
      ylinet=yline;
  while(vlez!=1){
    outar=cheakpoint(x1point, y2point, xlinet, ylinet, curzoomkv);
    vlez=outar.res;
    ysdvig++;
    ylinet=ylinet+1;
    if(ylinet>maks){ylinet=0}
  }
  var temp='', newtemp='',
      ylinesave=yline,
      ysdvigsave=ysdvig;
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
    clearMap();   
    $("params_filter").value=HMapTmp;
    setFilterAndSubmit();		
  }  
}
function disableFormParams(){
  if($("hometype_id").getValue()==0)
    $("hometype_id").disable();        
  if($("homeperson_id").getValue()==0)
    $("homeperson_id").disable();	         
  if($("date_start").getValue()=="")
    $("date_start").disable();        
  if($("date_start_value").getValue()=="")
    $("date_start_value").disable();	          
  if($("date_start_year").getValue()=="")
    $("date_start_year").disable();	
  if($("date_start_month").getValue()=="")
    $("date_start_month").disable();
  if($("date_start_month").getValue()=="")
    $("date_start_month").disable();	  
  if($("date_start_day").getValue()=="")
    $("date_start_day").disable();	  
  if($("date_start_hour").getValue()=="")
    $("date_start_hour").disable();	  
  if($("date_start_minute").getValue()=="")
    $("date_start_minute").disable();
  if($("date_end").getValue()=="")
    $("date_end").disable();	  
  if($("date_end_value").getValue()=="")
    $("date_end_value").disable();	  	  
  if($("date_end_year").getValue()=="")
    $("date_end_year").disable();	
  if($("date_end_month").getValue()=="")
    $("date_end_month").disable();
  if($("date_end_month").getValue()=="")
    $("date_end_month").disable();	  
  if($("date_end_day").getValue()=="")
    $("date_end_day").disable();	  
  if($("date_end_hour").getValue()=="")
    $("date_end_hour").disable();	  
  if($("date_end_minute").getValue()=="")
    $("date_end_minute").disable();	  
}
jQuery.noConflict();
jQuery(document).ready(function(){
  jQuery('a.show_more_link').colorbox({
    inline: true, 
    href: '#filters_lightbox',
    scrolling: false,
    onLoad: function(){
      jQuery('#filters_lightbox').show();          
    },
    onCleanup: function(){
      jQuery('#filters_lightbox').hide();          
    }        
  });            
  jQuery('.actions a.icon').click(function(){
    jQuery(this).parent().find('.active').removeClass('active');
    jQuery(this).addClass('active');
  }); 
});
function viewCell(sObj1,iNum,sObj2){
  var tab = $(sObj1);
  var tabs = tab.getElementsByTagName('li');      
  var layer = $(sObj2);
  var divs = layer.getElementsByTagName('li');            
  var layers = new Array();      
  for (var i=0; i < divs.length; i++){
    if(divs[i].className=='lightbox_filter_container')
      layers.push(i);        
  }
  for (var i=0; i < tabs.length; i++){
    tabs[i].className = (i==iNum) ? 'filters_lightbox_nav_element active' : 'filters_lightbox_nav_element';        
    divs[layers[i]].style.display = (i==iNum)? 'block' : 'none';        
  }
}    
function toggleFilter(container){ 
  var li = $(container);                 
  if(li.className == 'search_filter'){
    li.className = 'search_filter closed open';
  } else if(li.className == 'search_filter closed open'){
    li.className = 'search_filter'; 
  } else if(li.className == 'search_filter closed'){
    li.className = 'search_filter open';
  } else if(li.className == 'search_filter open'){
    li.className = 'search_filter closed';
  }
  Sidebar();
}    
function Sidebar(){     
  var g, i, f, x, h, c;      
  h = $('search_filters');
  c = $('search_body');
  g = h.getHeight();
  f = c.getHeight();
  i = c.cumulativeOffset().top + f; //offset
  x = (document.documentElement.scrollTop || document.body.scrollTop || window.pageXOffset);
  //if((document.body.clientHeight || window.innerHeight || document.documentElement.clientHeight || document.documentElement.getHeight()) < g){
    return
  //}
  if((x >= c.cumulativeOffset().top) && (g < f)){
    if(!h.hasClassName("fixed"))
      h.addClassName("fixed");
    if(((g+x) >= i)){
      h.setStyle({bottom:"0",position:"absolute",top:"auto"});
    }else{
      if(h.getStyle("position")==="absolute")
        h.setStyle({position:"",top:"0",bottom:"auto"});    
    }
  }else{
    if(h.hasClassName("fixed"))
      h.removeClassName("fixed")
    if(h.getStyle("position")==="absolute")
      h.setStyle({position:"",top:"0"})        
  }
}    
function setFullMap(){       
  $("results_header").setStyle({ width: '980px'});  
  $("results").setStyle({ width: '980px'});
  $("search_body").setStyle({ width: '980px'});          
  jQuery('.body').css({ width: '980px'});
  $("map_view").setStyle({width: '980px'});
  $("map_large_canvas").setStyle({width: '980px'});            
  $("search_filters_toggle").removeClassName('search_filters_toggle_on').addClassName('search_filters_toggle_off');       
  $("search_filters_wrapper").hide();
  setCoordinatesAndCenter(1);
  Yandex(1);
  setFilterAndSubmit();    	      
}  
function setSmallMap(iVar){
  iVar = iVar || 0
  $("results_header").setStyle({ width: '730px'});
  $("results").setStyle({ width: '730px'});    
  $("search_body").setStyle({ width: '730px'});
  jQuery('.body').css({ width: '730px'});
  $("search_filters_wrapper").addClassName('scrollable');
  $("map_view").setStyle({width: '730px'});
  $("map_large_canvas").setStyle({width: '730px'});
  $("search_filters_toggle").removeClassName('search_filters_toggle_off').addClassName('search_filters_toggle_on');
  $("search_filters_wrapper").show();
  $("newmapsearch_checkbox").hide();
  setCoordinatesAndCenter(1);
  Yandex(1);
  if(!iVar)
    setFilterAndSubmit();
}  	  
function clearMap(){      
  if (gBounds){
    gBounds.each(function (el, i) {
      map.geoObjects.remove(el);       
    });         
    gBounds.removeAll();
  }
}
function sinchronizeMap(){
  if($("redo_search_in_map").checked){
    $('is_main_filter').value=0;
    setCoordinatesAndCenter(0);	   
    updateMap(1);
  }else{
    setFilterAndSubmit();
  }
}
