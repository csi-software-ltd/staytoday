<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main" />
    <g:javascript>      
    function initialize(){
	    textCounter('vtext','vtext_limit',250);
    }      
    function deleteHomeVideo(){
      if (confirm('Вы уверены?')){
        $('deleteForm').submit();
      }
    }      
    function addVideo(){
      $('videoAddForm').submit();
    }      
    function textCounter(sId,sLimId,iMax){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iMax){
        symbols = symbols.substring(0,iMax);
        $(sId).value = symbols;
        return false;
      }
      $(sLimId).value = iMax-len;
    }
    function setVideo(){
      var lsCode=$('code').value.split('/');	  	 
      $('picture').update('<img width="200" height="140" src="http://img.youtube.com/vi/'+lsCode[lsCode.length-1]+'/default.jpg">');
      $('big_video').update('<iframe width="710" height="425" src="http://www.youtube.com/embed/'+lsCode[lsCode.length-1]+'?version=3&rel=0&showinfo=0&autohide=0&fs=0&loop=0&iv_load_policy=0&wmode=transparent" frameborder="0"></iframe>');      
    }
    </g:javascript>
  </head>  
  <body onload="initialize();">
              <g:render template="/personal_menu" />
                          <div rel="layer">                                   
                            <div class="form">    
                              <div id="error1" class="notice" style="display:none;"></div>
                              <g:form name="videoAddForm" method="post" url="${[controller:'personal',action:'homevideoadd',params:[id:id,home_id:home_id]]}" useToken="true" base="${context?.mainserverURL_lang}">			          
                                <table width="100%" cellpadding="5" cellspacing="5" border="0">                               
                                  <tr>
                                    <td colspan="2">
                                      <div id="big_video" class="thumbnail shadow" style="width:710px;height:425px">
                                      <g:if test="${inrequest?.code}"> 
                                        <iframe width="710" height="425" src="${inrequest?.code}?version=3&rel=0&showinfo=0&autohide=0&fs=0&loop=0&iv_load_policy=0&wmode=transparent" frameborder="0"></iframe>                  
                                      </g:if>
                                      </div>
                                    </td>            
                                  </tr>
                                <g:if test="${infotext['itext'+context?.lang]}">
                                  <tr>
                                    <td colspan="2" style="padding:10px">
                                      <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>   
                                    </td>
                                  </tr>
                                </g:if>
                                  <tr>
                                    <td width="220">
                                      <div id="picture" class="thumbnail shadow">
                                        <g:if test="${inrequest?.code}"><img width="200" height="140" src="http://img.youtube.com/vi/${inrequest?.videoid}/default.jpg"></g:if>
                                        <g:else><img width="200" height="140" src="${resource(dir:'images',file:'default-picture.png')}" border="0"></g:else>
                                      </div>
                                    </td>
                                    <td>
                                      <label for="code">${message(code:'ads.video.link')}</label>
                                      <input type="text" id="code" name="code" value="${inrequest?.code?:''}"/>
                                      <input type="button" class="button-glossy orange" style="z-index:100" value="${message(code:'button.view')}" onclick="setVideo()"/>
                                    </td>              
                                  </tr>                            
                                  <tr>
                                    <td colspan="2" class="paddtop">
                                      <label for="title">${message(code:'ads.caption')}</label>
                                      <input size="500" type="text" name="vtext" id="vtext" value="${inrequest?.vtext?:''}" onkeydown="textCounter(this.id,'vtext_limit',250);" onkeyup="textCounter(this.id,'vtext_limit',250);"/>
                                      <span class="padd10">${message(code:'ads.characters.left')} <input type="text" class="limit" id="vtext_limit" name="vtext_limit" readonly /></span>                                            
                                    </td>
                                  </tr>                                                                
                                  <tr>
                                    <td colspan="3" style="padding:20px 10px">
                                      <input class="button-glossy green" type="button" value="${message(code:'button.save')}" onclick="addVideo();"/>
                                      <input class="button-glossy red" type="button" name="delete" value="${message(code:'button.delete')}" onclick="deleteHomeVideo()"/>
                                      <g:link controller="personal" action="video" id="${home_id}" class="button-glossy grey">${message(code:'button.cancel')}</g:link>                                      
                                    </td>
                                  </tr>         
                                </table> 
                              </g:form>        
                              <g:form id="deleteForm" name="deleteForm" url="${[controller:'personal',action:'homevideodelete',params:[id:id,home_id:home_id]]}" method="post" useToken="true" base="${context?.mainserverURL_lang}">
                              </g:form>                              
                            </div>
                          </div>
                          <div rel="layer" style="display:none">
                          </div>
                        </div>
                      </div>
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>                    
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
