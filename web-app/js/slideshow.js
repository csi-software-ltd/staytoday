// JavaScript Slideshow - http://www.bretteleben.de  2010-04-12
// (c) 2010 Andreas Berger - andreas_berger@bretteleben.de
// License: http://www.gnu.org/copyleft/gpl.html
function daisychain(sl,is_main){ //daisychain onload-events
  if(window.onload) {
    var ld=window.onload;
    window.onload=function(){ld();sl(is_main);};
  }else{
    window.onload=function(){sl(is_main);};
  }
}
function slideshow(imges,divid,autoplay,mix,showctr,main){//declarations and defaults
  var slideid=divid, startwhen=autoplay, shuffle=mix, showcontr=showctr, 
      picwid=200, pichei=200, sdur=3, fdur=1, steps=20, self=this, stopit=1, startim=1, u=0,
      ftim=fdur*1000/steps, stim=sdur*1000, emax=imges.length,      
      parr=new Array(), ptofade, pnext, factor, mytimeout;
  if(shuffle) //shuffle images if set
    for(var i=0;i<=Math.floor(Math.random()*imges.length);i++)
      imges.push(imges.shift());
  this.b_myfade = function(){ //push images into array and get things going
    var a, idakt, paktidakt, ie5exep;
    for(a=1;a<=emax;a++){
      idakt = slideid + '_item_' + a;
      paktidakt = document.getElementById(idakt);
      ie5exep = new Array(paktidakt);
      parr = parr.concat(ie5exep);
    }
    if(startwhen){
      stopit=0;
      mytimeout=setTimeout(function(){self.b_slide();},stim);
    }
  }
  this.b_slide = function(){ //prepare current and next and trigger slide
    clearTimeout(mytimeout);
    u=0;
    ptofade=parr[startim-1];
    if(startim<emax){pnext=parr[startim];}
    else{pnext=parr[0];}
    pnext.style.zIndex=1;
    pnext.style.visibility="visible";
    pnext.style.filter="Alpha(Opacity=100)";
    try{pnext.style.removeAttribute("filter");} catch(err){}
    pnext.style.MozOpacity=1;
    pnext.style.opacity=1;
    ptofade.style.zIndex=2;
    ptofade.style.visibility="visible";
    ptofade.style.filter="Alpha(Opacity=100)";
    ptofade.style.MozOpacity=1;
    ptofade.style.opacity=1;
    factor=100/steps;
    if(stopit=="0"){
      this.b_slidenow();
    }
  }
  this.b_forw = function(){ //one step forward
    stopit=1;
    clearTimeout(mytimeout);
    ptofade=parr[startim-1];
    if(startim<emax){pnext=parr[startim];startim=startim+1;}
    else{pnext=parr[0];startim=1;}
    ptofade.style.visibility="hidden";
    ptofade.style.zIndex=1;
    pnext.style.visibility="visible";
    pnext.style.zIndex=2;
    self.b_slide();
  }
  this.b_back = function(){ //one step back
    stopit=1;
    clearTimeout(mytimeout);
    if(u==0){ //between two slides
      ptofade=parr[startim-1];
      if(startim<emax){pnext=parr[startim];}
      else{pnext=parr[0];}
      pnext.style.visibility="hidden";
      ptofade.style.zIndex=1;
      ptofade.style.visibility="visible";
      if(startim>=2){startim=startim-1;}
      else{startim=emax;}
      self.b_slide();
    }else{ //whilst sliding
      self.b_slide();
    }
  }
  this.b_slidenow = function(){ //slide as said, then give back
    var check1, maxalpha, curralpha;
    check1=ptofade.style.MozOpacity;
    maxalpha=(100-factor*u)/100*105;
    if(check1<=maxalpha/100){u=u+1;}
    curralpha=100-factor*u;
    ptofade.style.filter="Alpha(Opacity="+curralpha+")";
    ptofade.style.MozOpacity=curralpha/100;
    ptofade.style.opacity=curralpha/100;
    if(u<steps){ //slide not finished
      if(stopit=="0"){mytimeout=setTimeout(function(){self.b_slidenow();},ftim);}
      else {this.b_slide();}
    }else{ //slide finished
      if(startim<emax){
        ptofade.style.visibility="hidden";
        ptofade.style.zIndex=1;
        pnext.style.zIndex=2;
        startim=startim+1;u=0;
        mytimeout=setTimeout(function(){self.b_slide();},stim);
      }else{
        ptofade.style.visibility="hidden";
        ptofade.style.zIndex=1;
        pnext.style.zIndex=2;
        startim=1;u=0;
        mytimeout=setTimeout(function(){self.b_slide();},stim);
      }
    }
  }
  this.b_start= function(){ //manual start
    var s = $('ss_button_pause_play');
    if(s.className == 'ss_button_icon ss_button_play'){
      s.removeClassName('ss_button_icon ss_button_play').addClassName('ss_button_icon ss_button_pause');
      s.observe('click', self.b_stop);  
    }
    if(stopit==1){
      stopit=0;
      mytimeout=setTimeout(function(){self.b_slide();},stim);
    }
  }
  this.b_stop= function(){ //manual stop
    var s = $('ss_button_pause_play');
    if(s.className == 'ss_button_icon ss_button_pause'){
      s.removeClassName('ss_button_icon ss_button_pause').addClassName('ss_button_icon ss_button_play');
      s.observe('click', self.b_start);     
    }
    clearTimeout(mytimeout);
    stopit=1;
    this.b_slide();
  }
  this.b_view = function(startim){ //manual view
    stopit=1;        
    for(a=1;a<=emax;a++){
      var obj_item = $(slideid+'_item_'+a),
          obj_thumb = $(slideid+'_thumb_'+a),
          obj_frame = $(slideid+'_frame_'+a);
      if(a==startim){
        obj_thumb.addClassName(' active');
        obj_frame.src='http://www.youtube.com/embed/'+imges[a-1].code+'?version=3&rel=0&showinfo=0&autohide=0&fs=0&loop=0&iv_load_policy=0&wmode=transparent';                
        obj_item.style.zIndex=2;
        obj_frame.style.zIndex=3;        
        obj_item.style.visibility="visible";        
        obj_item.style.filter="Alpha(Opacity=100)";
        obj_item.style.MozOpacity=1;
        obj_item.style.opacity=1;
      }else{        
        obj_thumb.removeClassName(' active');        
        obj_frame.src='';
        obj_frame.style.zIndex=0;        
        obj_item.style.zIndex=0;
        obj_item.style.visibility="hidden";
        obj_item.style.filter="Alpha(Opacity=0)";
        obj_item.style.MozOpacity=0;
        obj_item.style.opacity=0;      
      }
    }
  }
  this.b_insert = function(main){ //insert css and images
    var b, thestylid, thez, thevis, slidehei;
    slidehei=(showcontr)?(pichei+25):(pichei); //add space for the controls
    var myhtml='<div id="slideshow_container" style="'+((main==0)?'height:512px !important;border: 1px solid #eee':'')+'">';  
    for (b = 1; b <= imges.length; b++){ 
      thez=1;thevis='hidden';
      if(b<=1) {thez=2; thevis='visible';}
      if(main==1){ // for slideshow in mainpage
        myhtml+='<div id="'+slideid+'_item_'+b+'" class="slideshow_item" style="visibility:'+thevis+';z-index:'+thez+';">'+
        '  <a href="'+imges[b-1].base+imges[b-1].appname+imges[b-1].country+imges[b-1].city+'home_'+imges[b-1].linkname+'" class="image_link">'+
        '    <img src="'+imges[b-1].picture+'" alt="'+imges[b-1].name+'" border="0" />'+
        '  </a>'+
        '  <div class="slideshow_item_details">'+
        '    <div class="slideshow_item_details_text">'+
        '      <div class="ss_details_top">'+
        '        <a href="'+imges[b-1].base+imges[b-1].appname+imges[b-1].country+imges[b-1].city+'home_'+imges[b-1].linkname+'" class="ss_name">'+imges[b-1].name+'</a><br/>'+
        '        <span class="ss_location">'+imges[b-1].shortaddress+'</span>'+
        '      </div>'+
        '      <div class="ss_details_bottom">'+
        '        <span class="ss_price b-rub">'+imges[b-1].price+'</span><span class="ss_period">'+imges[b-1].sutki+'</span>'+
        '      </div>'+
        '    </div>'+
        '  </div>'+
        '</div>';      
      } else { // for slideshow in homeviewpage
        myhtml+='<div itemprop="video" itemscope itemtype="http://schema.org/VideoObject" id="'+slideid+'_item_'+b+'" class="slideshow_item" style="visibility:'+thevis+';z-index:'+thez+';">'+
        '  <meta itemprop="name" content="'+imges[b-1].name+'" />'+
        '  <meta itemprop="thumbnail" content="http://img.youtube.com/vi/'+imges[b-1].code+'/default.jpg" />'+
        '  <a href="javascript:void(0)" class="image_link">'+
        '    <iframe id="'+slideid+'_frame_'+b+'" src="http://www.youtube.com/embed/'+imges[b-1].code+'?version=3&rel=0&showinfo=0&fs=0&loop=0&iv_load_policy=0&wmode=transparent" frameborder="0" style="width:708px;height:425px"></iframe>'+
        '  </a>'+
        '  <div class="galleria-info" style="top:350px">'+
        '    <div class="galleria-info-text">'+
        '      <div itemprop="description" class="galleria-info-description">'+imges[b-1].desc+'</div>'+
        '    </div>'+
        '  </div>'+
        '</div>';
      }
    }
    if(main==0){ //add thumbnails    
      myhtml+='<div class="galleria-thumbnails-container galleria-carousel">'+
              '  <div class="galleria-thumb-nav-left disabled"></div>'+
              '  <div class="galleria-thumbnails-list" style="overflow:hidden;position:relative;">'+
              '    <div class="galleria-thumbnails" style="overflow:hidden;position:relative;width:auto;height:79px">';
      for(b=1; b<=imges.length; b++){ 
        myhtml+='    <div id="'+slideid+'_thumb_'+b+'" class="galleria-image" style="overflow: hidden; position: relative">'+
                '      <img src="http://img.youtube.com/vi/'+imges[b-1].code+'/default.jpg" onclick="'+slideid+'.b_view('+b+')" style="display: block; opacity: 1; width: 103px; height: 67px; position: absolute; top: 1px; left: 1px;">'+
                '    </div>';
      }
      myhtml+='    </div>'+
              '  </div>'+
              '  <div class="galleria-thumb-nav-right disabled"></div>'+
              '</div>';
    }
    myhtml+='</div>';
    if(showcontr){ //show controls
      myhtml+='<div id="slideshow_controls" class="rounded_top" style="display: none;">'+
              '  <a id="ss_button_prev" class="ss_button_icon" href="javascript:void(0);" onclick="'+slideid+'.b_back();"></a>'+
              '  <a id="ss_button_pause_play" class="ss_button_icon ss_button_pause" href="javascript:void(0);" onclick="'+slideid+'.b_stop();"></a>'+
              '  <a id="ss_button_next" class="ss_button_icon" href="javascript:void(0);" onclick="'+slideid+'.b_forw();"></a>'+
              '</div>'; 
    }  
    $(divid).update(myhtml);
    self.b_myfade();
    if(main==0)
      $(slideid+'_thumb_1').addClassName(' active');
  }
  daisychain(this.b_insert,main); //call autostart-function
}
