<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>  
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main" />
    <style type="text/css">
      input[type="file"]{ width: 40px !important; }
      noindex:-o-prefocus, input[type="file"] { width: 190px !important }
    </style>    
    <g:javascript library="jquery-1.8.3" />
    <g:javascript src="silverlight.js" />
    <g:javascript>      
      function addPhoto(){
        $('photoAddForm').submit();
      }    
      function deletepicuserphoto(){
        <g:remoteFunction controller='profile' action='deletepicuserphoto' onSuccess='reloadImage(1)' params="'name=file1'" />
      }      
      // обработчик unhandled exceptions
      function onSilverlightError(sender, args) {
        var appSource = "";
        if (sender != null && sender != 0) {
          appSource = sender.getHost().Source;
        }
        var errorType = args.ErrorType;
        var iErrorCode = args.ErrorCode;
        if (errorType == "ImageError" || errorType == "MediaError") {
          return;
        }
        var errMsg = "Unhandled Error in Silverlight Application " + appSource + "\n";
        errMsg += "Code: " + iErrorCode + "    \n";
        errMsg += "Category: " + errorType + "       \n";
        errMsg += "Message: " + args.ErrorMessage + "     \n";
        if (errorType == "ParserError") {
          errMsg += "File: " + args.xamlFile + "     \n";
          errMsg += "Line: " + args.lineNumber + "     \n";
          errMsg += "Position: " + args.charPosition + "     \n";
        } else if (errorType == "RuntimeError") {
          if (args.lineNumber != 0) {
            errMsg += "Line: " + args.lineNumber + "     \n";
            errMsg += "Position: " + args.charPosition + "     \n";
          }
          errMsg += "MethodName: " + args.methodName + "     \n";
        }
        throw new Error(errMsg);
      }      
      // Управление объектом 
      function capture() {
        var oSl = document.getElementById("slcamera");
        oSl.Content.Camera.CameraCapture();
      }
      function start() {
        var oSl = document.getElementById("slcamera");
        oSl.Content.Camera.CameraStart();
      }
      function stop() {
        var oSl = document.getElementById("slcamera");
        oSl.Content.Camera.CameraStop();
      }
      function save() {
        var oSl = document.getElementById("slcamera");
        oSl.Content.Camera.CameraSave();
      }      
      // Реакция на события
      function onCameraSave(s) {
        var Arr=s.split(','); 
        stopUpload(1,Arr[0],Arr[1],Arr[2],Arr[3]);
        $('cboxClose').click();
        //$('uploaded1').html('<img class="slideshow" src="${imageurl}'+s+'"/>');
      }
      function onCameraError(m) {
        if (m == 1){
          alert('${message(code:"photo.error.camera.notfound")}');
        } else if (m == 2) {
            if (confirm('${message(code:"photo.error.camera.nottaken")}'))
              capture();
        } else if (m == 3) {
            alert('${message(code:"photo.error.camera.noturl")}');
        } else if (m == 4) {
            alert('${message(code:"photo.error.camera.notaccess")}');
        } else if (m == 5) {
            if (confirm('${message(code:"photo.error.camera.notenabled")}'))
              start();
        } else {//web response
            alert(m);
        }
      }      
      jQuery.noConflict();
      jQuery(document).ready(function(){
        jQuery('#optionsContainer li.take_photo').colorbox({
          inline: true, 
          href: '#photo_lbox',
          scrolling: false,
          onLoad: function(){
            jQuery('#photo_lbox').show();          
          },
          onCleanup: function(){
            jQuery('#photo_lbox').hide();          
          }        
        });            
      });      
    </g:javascript> 
  </head>  
  <body>
              <g:render template="/profile_menu" />                        
                      <h2 class="toggle border"><span class="edit_icon photos"></span>${infotext['header'+context?.lang]?:''}</h2>
                      <div id="error1" class="notice" style="display:none"></div>
                      <g:form name="ff1" method="post" url="${[action:'savepicuserphoto']}" enctype="multipart/form-data" target="upload_target" base="${context?.mainserverURL_lang}">
                        <table cellpadding="0" cellspacing="0" border="0">                     
                          <tr>
                            <td width="210" class="padd10">
                              <div class="thumbnail shadow" id="uploaded1" style="width:200px;height:auto">
                              <g:if test="${user?.picture}">
                                <img width="200" src="${imageurl}${user?.picture}" />
                              </g:if><g:else>
                                <img width="200" src="${resource(dir:'images',file:'user-default-picture.png')}" border="0" />
                              </g:else>
                              </div>
                            </td>
                            <td width="200" class="padd10">
                              <input type="button" id="button1" class="button-glossy orange float" value="${message(code:'button.change')}" onclick="deletepicuserphoto();" style="${((images?.photo_1?:'')=='')?'display:none':''}" />
                              <input type="file" name="file1" id="file1" size="16" accept="image/gif,image/jpeg" onchange="startSubmit('ff1');" style="${(images?.photo_1)?'display:none':''}" />
                            </td>
                            <td valign="middle">
                              <ul id="optionsContainer" class="clearfix">
                                <li class="otherOption take_photo">
                                  <div class="optionIcon">
                                    <div id="webcamOption" class="optionImg"></div>
                                  </div>
                                  <span>${infotext['promotext1'+context?.lang]?:''}</span>
                                </li>
                                <!-- li id="up" class="otherOption swap">
                                  <div class="optionIcon">
                                    <div id="uploadOption" class="optionImg"></div>
                                  </div>
                                  <span>Загрузите новый файл со своего компьютера</span>
                                </li -->                    
                              </ul>                  
                            </td>
                          </tr>
                        </table>          
                        <input type="hidden" name="no" value="1" />
                        <input type="hidden" name="is_uploaded1" id="is_uploaded1" value="${images?.photo_1?1:0}" />  
                        <input type="hidden" name="no_webcam" value="1" />			
                      </g:form>
                      <div id="loader" style="display: none">
                        <img src="${resource(dir:'images',file:'spinner.gif')}" border="0" />
                      </div>
                      <iframe id="upload_target" name="upload_target" src="#" style="width:0;height:0;border:none"></iframe>        
                      <g:form name="photoAddForm" method="post" url="${[controller:'profile',action:'userphotoadd']}" useToken="true" base="${context?.mainserverURL_lang}">			
                      </g:form>			  
                      <div style="padding:45px 20px">
                        <input class="button-glossy orange float" type="button" name="submit" value="${message(code:'button.save')}" onclick="addPhoto()"/>
                        <g:form name="apage" url="${[controller:'profile',action:'edit']}" method="post">                        
                          <input class="button-glossy grey float" type="submit" name="submit" value="${message(code:'button.cancel')}" style="margin:0 5px"/>
                        </g:form>
                        <g:form  id="deleteForm" name="deleteForm" url="[controller:'profile',action:'userphotodelete']" method="post" useToken="true" class="float" base="${context?.mainserverURL_lang}">
                          <input class="button-glossy red float" type="submit" value="${message(code:'button.delete')}"/>          
                        </g:form>
                      </div>                    
                      
                      <div id="photo_lbox" class="new-modal" style="width:545px;display:none">
                        <h2 class="clearfix">${infotext['promotext1'+context?.lang]?:''}</h2>                
                        <div id="photo_lbox_segment" class="segment nopadding" style="height:310px">
                          <div id="silverlightControlHost" class="lightbox_filter_container">
                            <object id="slcamera" data="data:application/x-silverlight-2," type="application/x-silverlight-2" width="520" height="300">
                              <param name="source" value="${resource(dir:'js',file:'Camera.xap')}"/>
                              <param name="onError" value="onSilverlightError" />
                              <param name="background" value="white" />
                              <param name="minRuntimeVersion" value="4.0.50826.0" />
                              <param name="autoUpgrade" value="true" />                      
                              <param name="initparams" value="url=${url}/profile/savepicuserphoto,quality=85,name=file1"/>
                              <a href="http://go.microsoft.com/fwlink/?LinkID=149156&v=4.0.50826.0" style="text-decoration:none">
                                <img src="http://go.microsoft.com/fwlink/?LinkId=161376" alt="Get Microsoft Silverlight" border="0"/>
                              </a>
                            </object>
                            <iframe id="_sl_historyFrame" style="visibility:hidden;height:0px;width:0px;border:0px"></iframe>
                          </div>                  
                        </div>                				
                        <div id="lightbox_filter_action_area" class="segment buttons">                  
                          <input type="button" class="button-glossy green" value="${message(code:'photo.take')}" onclick="capture()" style="margin-right:3px"/>
                          <input type="button" class="button-glossy red" value="${message(code:'button.switchon')}" onclick="start()"/ style="margin:0 3px">
                          <input type="button" class="button-glossy grey" value="${message(code:'button.switchoff')}" onclick="stop()" style="margin:0 3px"/>
                          <input type="button" class="button-glossy lightblue" value="${message(code:'button.save')}" onclick="save()" style="margin-left:3px"/>
                        </div>              
                      </div>              

<!-- /Загрузка имиджей -->
<script language="javascript" type="text/javascript">
function reloadImage(iNum){
    $('uploaded'+iNum).update('<img width="200" src="${resource(dir:"images",file:"user-default-picture.png")}" border="0">');    
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
	  if((iNum<=0)||(iNum>4)) iNum=1;
	  
	  $('loader').hide();
    if(iErrNo==0){
    	$('is_uploaded'+iNum).value=1;
      $('uploaded'+iNum).show();
      $('uploaded'+iNum).update('<img width="200" src="${imageurl}'+sFilename+'">');
	    $('file'+iNum).hide();
      $('error'+iNum).hide();
      $('button'+iNum).show();
    }else{
      var sText="${message(code:'ads.error.upload')}";
      switch(iErrNo){
        case 1: 
        case 2: sText="${message(code:'ads.error.upload')}"; break;
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
