<g:javascript>
  function correctText(sStr) {
    var sStr = sStr.replace("'", ' ');
    sStr = sStr.replace("\x0D\x0A", ' ');
    sStr = sStr.replace('"', ' ');
    sStr = sStr.replace('%', ' ');
    sStr = sStr.replace('?', ' ');
    return sStr;
  }
  function correctSuccess(e) {        
    if(!e.responseJSON.error){
      var template= '<div id="ansOrphCont" class="new-modal">'+
                    '  <h2 class="clearfix">${message(code:'correction.mistake')}</h2>'+
                    '  <div class="segment nopadding" style="height: 160px">'+
                    '    <div class="lightbox_filter_container" style="height: 160px">'+
                    '      <div id="ansOrph">${message(code:'correction.comment_send')}</div>'+
                    '    </div>'+
                    '  </div>'+
                    '</div>';
      jQuery.colorbox({
        html: template,
        scrolling: false,            
        onLoad: function(){ 
          jQuery('#contOrph').hide(); 
        }
      });
    }
  }
  function updateCorrection(){     		      		
    var params='rectitleOrph='+$("rectitleOrph").value+
	    '&'+'rectextOrph='+correctText($("rectextOrph").value);	
    params=params.replace(/\&amp;/g,'&');
    <g:remoteFunction controller='correction' action='index' onSuccess='correctSuccess(e)' params="params" /> 
  }
</g:javascript>
          <div id="contOrph" class="new-modal" style="display:none">
            <h2 class="clearfix">${message(code:'correction.mistake')}</h2>
            <div class="segment nopadding" style="height: 180px">
              <div class="lightbox_filter_container" style="height: 180px">
                <h4 id="inpOrph" style="margin-top:0px"></h4>                
                <label for="rectextOrph">${message(code:'ads.price.comment')}:</label><br/>
                <textarea cols="30" rows="4" id="rectextOrph" name="rectextOrph" style="width:97%"></textarea>						  
                <input type="hidden" name="rectitleOrph" id="rectitleOrph" value="" />	                
              </div>
            </div>
            <div class="segment buttons">                
              <input type="submit" class="button-glossy orange" onclick="updateCorrection();return false;" value="${message(code:'button.send')}" />              
            </div>
          </div>              
