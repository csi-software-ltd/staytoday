<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main" />
    <g:javascript>
      var saved = false;
      function initialize(){
        textCounter('ptext','ptext_limit',250);
      }
      window.addEventListener('unload', function(){
        var needSave = false;
        for(i=1;i<=${maxI};i++){
          if($('is_uploaded'+i).value==1)
            needSave = true;
        }
        if(needSave && !saved)
          if(confirm('${message(code:"ads.photo.confirm.save")}?'))
            $('photoAddForm').submit();
      }, false);
      function deleteHomePhoto(){
        if (confirm('${message(code:'ads.photo.confirm.delete')}?')){
          $('deleteForm').submit();
        }
      }      
      function addPhoto(){
        saved = true;
        $('photoAddForm').submit();
      }      
      function cancelP(){
        saved = true;
        $('apage').submit();
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
        if ($('n'+sId))
          $('n'+sId).value = $(sId).value;
      }    
      function deletepichomephoto(lId,iNo){
        <g:remoteFunction controller='personal' action='deletepichomephoto' onSuccess='reloadImage(iNo)' params="'name=file'+iNo+'&id='+lId" />
      }
    </g:javascript>
  </head>  
  <body>
              <g:render template="/personal_menu" />
                          <div rel="layer">
                          <g:if test="${infotext['itext'+context?.lang]}">
                            <div class="padd20">
                              <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                            </div>
                          </g:if>
                            <div class="form">    
                              <table width="100%" cellpadding="5" cellspacing="5" border="0">
                              <g:while test="${i <= maxI}"><g:if test="${(i==1)||!(id?:0)}">
                                <tr id="tr_${i}" <g:if test="${i!=1}">style="display:none"</g:if>>
                                  <td align="left" valign="top">
                                    <div class="notice" id="error${i}" style="display:none"></div>
                                    <g:form name='ff${i}' method="post" url="${[action:'savepichomephoto',id:id?:0]}" enctype="multipart/form-data" target="upload_target${i}" base="${context?.mainserverURL_lang}">
                                      <table cellpadding="0" cellspacing="0" border="0">                     
                                        <tr>
                                          <td colspan="3" id="big_photo${i}">
                                          <g:if test="${images?.('photo_'+i)}"> 
                                            <div class="thumbnail ${images?.('photo_'+i)?'shadow':''}" style="width:710px;height:425px">                                            
                                              <img width="710" height="425" src="${imageurl}${images?.('photo_'+i)}" border="0" />                                            
                                            </div>
                                          </g:if>
                                          </td>            
                                        </tr> 
                                        <tr>
                                          <td class="paddtop" width="220">
                                            <div class="thumbnail shadow" id="uploaded${i}">
                                              <g:if test="${images?.('photo_'+i)}"><a href="${imageurl}${images?.('photo_'+i)}" target="_blank"><img width="200" height="140" src="${imageurl}${images?.('thumb_'+i)}" border="0" /></a></g:if>
                                              <g:else><img width="200" height="140" src="${resource(dir:'images',file:'default-picture.png')}" border="0" /></g:else>
                                            </div>
                                          </td>
                                          <td align="left">                
                                            <input type="button" id="button${i}" class="button-glossy orange" value="${message(code:'button.change')}" onclick="deletepichomephoto(${id?:0},${i})" style="z-index:100;<g:if test="${(images?.('thumb_'+i)?:'')==''}">display:none</g:if>" />
                                          </td>
                                          <td id="btn${i}">
                                            <input type="file" <g:if test="${(i==1)&&!(id?:0)}">multiple</g:if> name="file${i}" id="file${i}" size="23" accept="image/jpeg,image/gif" onchange="startSubmit('ff${i}')" <g:if test="${images?.('thumb_'+i)}">style="display:none"</g:if>/>
                                          </td>
                                        </tr>
                                      </table>
                                      <input type="hidden" id="no${i}" name="no" value="${i}"/>
                                      <input type="hidden" name="is_uploaded${i}" id="is_uploaded${i}" value="${images?.('photo_'+i)?1:0}" />          
                                    </g:form>
                                    <div id="loader" class="spinner" style="display:none">
                                      <img src="${resource(dir:'images',file:'spinner.gif')}" align="absmiddle" hspace="5" border="0">                                      
                                    </div>
                                    <iframe id="upload_target${i}" name="upload_target${i}" src="#" style="width:0;height:0;border:0px"></iframe>        
                                  </td>
                                </tr> 
                                <tr id="trr_${i}" <g:if test="${i!=1}">style="display: none"</g:if>>
                                  <td>
                                    <label for="title">${message(code:'ads.caption')}</label>
                                    <input size="500" type="text" name="ptext${i}" id="ptext${i}" value="${homephoto?.ptext?:''}" onkeydown="textCounter(this.id,'ptext_limit${i}',250);" onkeyup="textCounter(this.id,'ptext_limit${i}',250);">
                                    <span class="padd10">${message(code:'ads.characters.left')} <input type="text" class="limit" id="ptext_limit${i}" name="ptext_limit${i}" readonly /></span>  
                                  </td>
                                </tr>
                                  <g:javascript>
                                    textCounter('ptext${i}','ptext_limit${i}',250);
                                  </g:javascript>
                                </g:if>
                                <%i++%>
                              </g:while>
                                <tr>
                                  <td style="padding:20px 10px">
                                    <div class="float" style="margin-right:5px">
                                      <input class="button-glossy green" type="button" name="submit" value="${message(code:'button.save')}" onclick="addPhoto()"/>
                                    <g:if test="${homephoto && !homephoto?.is_main}">
                                      <input class="button-glossy red" type="button" name="delete" value="${message(code:'button.delete')}" onclick="deleteHomePhoto()"/>
                                    </g:if>
                                    </div>                                    
                                    <g:form class="float" id="apage" name="apage" url="${[controller:'personal',action:'photo',id:home_id]}" method="post" base="${context?.mainserverURL_lang}">
                                      <input class="button-glossy grey" type="button" value="${message(code:'button.cancel')}" onclick="cancelP()"/>
                                    </g:form>
                                  </td>                    
                                </tr>
                              </table>                            
<!-- /Загрузка имиджей -->
<script language="javascript" type="text/javascript">
  function reloadImage(iNum){
    $('uploaded'+iNum).update('<img width="200" height="140" src="${resource(dir:'images',file:'default-picture.png')}" border="0">');
    $('big_photo'+iNum).update('');
    $('button'+iNum).hide();
    $('file'+iNum).value='';
    $('file'+iNum).show();
    $('is_uploaded'+iNum).value=0;
  }

  function startSubmit(sName){
	  $(sName).submit();
    $('loader').show();
    return true;
  }

  function stopUpload(iNum,sFilename,sThumbname,iErrNo,sMaxWeight) {
	  if((iNum<=0)||(iNum>${maxI})) iNum=1;
	  
	  $('loader').hide();
    if(iNum==2){
      if($('file1').style.display!="none")
        $('btn1').update('<input type="file" name="file1" id="file1" size="23" accept="image/jpeg,image/gif" onchange="startSubmit('+'ff1'+')"/>');
      else
        $('btn1').update('<input type="file" name="file1" id="file1" size="23" accept="image/jpeg,image/gif" onchange="startSubmit('+'ff1'+')" style="display: none;"/>');
      $('no1').value=0;
    }
    if(iErrNo==0){
      $('is_uploaded'+iNum).value=1;
      $('uploaded'+iNum).show();
      $('uploaded'+iNum).update('<img width="200" height="140" src="${imageurl}'+sThumbname+'"></a>');
      $('big_photo'+iNum).update('<div class="thumbnail shadow" style="width:710px;height:425px"><img width="710" height="425" src="${imageurl}'+sFilename+'"></div>');
      $('file'+iNum).hide();
      $('error'+iNum).hide();
      $('button'+iNum).show();
      if (iNum<=${maxI}){
        $('tr_'+(iNum)).show();
        $('trr_'+(iNum)).show();
      }
    }else{
      if (iNum<=${maxI}){
        $('tr_'+(iNum)).show();
        $('trr_'+(iNum)).show();
      }
      var sText="${message(code:'ads.error.upload')}";
      switch(iErrNo){ 
        case 1:
        case 2:
        case 5: sText="${message(code:'ads.error.upload')}"; break;
        case 3: sText="${message(code:'ads.error.file.large')} "+sMaxWeight+" Мб"; break;
        case 4: sText="${message(code:'ads.error.file.type')}"; break;
      }
      $('is_uploaded'+iNum).value=0;
      $('error'+iNum).update(sText);
    	$('error'+iNum).show();
    }
    return true;
  }
</script>

  <g:form  id="deleteForm" name="deleteForm" url="${[controller:'personal',action:'homephotodelete',params:[id:id,home_id:home_id]]}" method="post" useToken="true" base="${context?.mainserverURL_lang}">
  </g:form>
  <g:form id="photoAddForm" name="photoAddForm" url="${[controller:'personal',action:'homephotoadd',params:[id:id,home_id:home_id]]}" method="post" useToken="true" base="${context?.mainserverURL_lang}">
    <g:while test="${i > 1}">
      <%i--%>
      <input style="width:100%" size="500" type="hidden" name="ptext${i}" id="nptext${i}" value="${(i==1)?(homephoto?.ptext?:''):''}">
    </g:while>
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
