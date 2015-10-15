<html>
  <head>
  <title>Административное приложение StayToday.ru</title>
  <meta name="layout" content="admin" />
  <g:javascript>  
    function returnToList(){
      $("returnToListForm").submit();
    }
    function deletepicuserphoto(){
      <g:remoteFunction controller='administrators' action='deletearticlephoto' onSuccess='reloadImage(1)' params="'name=file1'" />
    }
    function processResponse(e){
      if (e.responseJSON.error.length) {
        document.getElementById('errContaner').show();
        e.responseJSON.error.indexOf(1)>-1?document.getElementById('err1').show():document.getElementById('err1').hide();
        e.responseJSON.error.indexOf(101)>-1?document.getElementById('err101').show():document.getElementById('err101').hide();
      } else if (!${id}) {
        location.assign(location.href+'/'+e.responseJSON.id)
      }
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
	</g:javascript>
  </head>
  <body onload="textCounter('shortdescr','shortdescr_limit',400);textCounter('ceo_descr','ceodescr_limit',250);">

    <div id="errContaner" class="notice drop_shadow" style="display:none">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <li id="err101" style="display:none">Непоправимая ошибка. Данные не сохранены.</li>
        <li id="err1" style="display:none">Не заполнено обязательное поле &laquo;название&raquo;.</li>
      </ul>
    </div>
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td><a class="to-parent" href="#" onClick="returnToList();">К списку статей</a></td>
      </tr>
      <tr>
        <g:if test="${id}">
          <td><h1 class="blue">Статья &laquo;${inrequest.title}&raquo;</h1></td>
        </g:if><g:else>
          <td><h1 class="blue">Добавление новой статьи</h1></td>
        </g:else>
      </tr>
      <tr>
        <td valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Главная фотка</h2>
        </td>
      </tr>
      <tr>
        <td>
          <g:form name='ff1' method="post" url="${[action:'savearticlephoto']}" enctype="multipart/form-data" target="upload_target">
            <div id="error1" style="display: none;"></div>
            <div id="uploaded1" class="upload" style="float:left">
            <g:if test="${inrequest?.picture}">
              <img width="220" height="160" src="${imageurl}${inrequest?.picture}" border="0"/>
            </g:if><g:else>
              <img width="220" height="160" src="${resource(dir:'images',file:'default-picture.png')}" border="0"/>
            </g:else>
            </div>
            <div style="width:50%;padding-top:45px;float:right;">
              <input type="button" id="button1" value="Изменить" onclick="deletepicuserphoto()" <g:if test="${!inrequest?.picture}">style="display: none;"</g:if>>
              <input type="file" name="file1" id="file1" size="23" accept="image/jpeg,image/gif" onchange="startSubmit('ff1')" <g:if test="${inrequest?.picture}">style="display: none;"</g:if>/>
              <input type="hidden" name="is_uploaded1" id="is_uploaded1" value="${images?.photo_1?1:0}" />
            </div>
          </g:form>
          <div id="loader" style="position: absolute; top: 100px; display: none; width: 16px; height: 16px;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" border="0">
          </div>
          <iframe id="upload_target" name="upload_target" src="#" style="width:0;height:0;border:0px solid #fff;"></iframe>
        </td>
      </tr>
      <tr>
        <td valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Заголовки</h2>
        </td>
      </tr>
      <tr>
        <td>
          <g:formRemote name="articleeditForm" url="[controller:'administrators',action:'articlesave', id:id]" method="post" onSuccess="processResponse(e)">
            <table width="100%" cellpadding="5" cellspacing="5" border="0">
              <tr>
                <td width="160">Название:</td>
                <td colspan="3"><input type="text" name="title" maxlength="50" style="width:100%" value="${inrequest?.title?:''}"/></td>
              </tr>
              <tr>
                <td>Анонс:</td>
                <td colspan="3">
                  <textarea id="shortdescr" style="width:99%" name="shortdescription" cols="40" rows="4" onKeyDown="textCounter(this.id,'shortdescr_limit',400);" onKeyUp="textCounter(this.id,'shortdescr_limit',400);">${inrequest?.shortdescription?:''}</textarea>
                  <span class="padd10">осталось <input type="text" class="limit" id="shortdescr_limit" readonly /> символов</span>
                </td>
              </tr>
              <tr>
                <td>Теги:</td>
                <td>
                  <g:select size="5" name="tags" from="${Articles_tags.list()}" style="height:auto" value="${inrequest?.tags*.id}" optionKey="id" optionValue="name" multiple="multiple"/>
                </td>
                <td>Новый тег:</td>
                <td><input type="text" name="newtag" value="" style="width:100%" /></td>
              </tr>
              <tr>
                <td>Автор:</td>
                <td width="35%"><input type="text" name="author" value="${inrequest?.author}" style="width:100%" /></td>
                <td width="200" align="center">ID профиля Google+:</td>
                <td align="right"><input type="text" name="googleplus_id" value="${inrequest?.googleplus_id}" size="21" style="width:100%" /></td>
              </tr>              
              <tr>
                <td colspan="4" valign="top">
                  <h2 class="toggle border"><span class="edit_icon"></span>Метатеги</h2>
                </td>
              </tr>
              <tr>
                <td>Title:</td>
                <td colspan="3"><input type="text" name="ceo_title" style="width:100%" value="${inrequest?.ceo_title?:''}"/></td>
              </tr>
              <tr>
                <td>Keywords:</td>
                <td colspan="3">
                  <textarea style="width:99%" name="ceo_keywords" cols="40" rows="4">${inrequest?.ceo_keywords?:''}</textarea>
                </td>
              </tr>
              <tr>
                <td>Description:</td>
                <td colspan="3">
                  <textarea id="ceo_descr" style="width:99%" name="ceo_description" cols="40" rows="4" onKeyDown="textCounter(this.id,'ceodescr_limit',250);" onKeyUp="textCounter(this.id,'ceodescr_limit',250);">${inrequest?.ceo_description?:''}</textarea>
                  <span class="padd10">осталось <input type="text" class="limit" id="ceodescr_limit" readonly /> символов</span>
                </td>
              </tr>
              <tr>
                <td colspan="4" valign="top">
                  <h2 class="toggle border"><span class="edit_icon details"></span>Текст статьи</h2>
                </td>
              </tr>
              <tr id="text1">
                <td colspan="4">
                  <fckeditor:editor name="atext" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
                    <g:rawHtml>${inrequest?.atext}</g:rawHtml>
                  </fckeditor:editor>
                </td>
              </tr>
              <tr>
                <td colspan="4" align="right">
                  <input type="submit" class="button-glossy green" value="Сохранить"/>
                </td>
              </tr>
            </table>
          </g:formRemote>
        </td>
      </tr>
      <tr>
        <td><a class="to-parent" href="#" onClick="returnToList();">К списку статей</a></td>
      </tr>
    </table>
  <g:form name="returnToListForm" url="${[controller:'administrators',action:'article', params:[fromDetails:1]]}">
  </g:form>
<!-- /Загрузка имиджей -->
<script language="javascript" type="text/javascript">
  function reloadImage(iNum){
    $('uploaded'+iNum).update('<img width="220" height="160" src="${resource(dir:'images',file:'default-picture.png')}" border="0"/>');
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
      $('uploaded'+iNum).update('<img width="220" height="160" src="${imageurl}'+sFilename+'" border="0"/>');
      $('file'+iNum).hide();
      $('error'+iNum).hide();
      $('button'+iNum).show();
    }else{
      var sText="Ошибка загрузки";
      switch(iErrNo){
        case 1: sText="Удивительная ошибка загрузки"; break;
        case 2: sText="Ошибка загрузки"; break;
        case 3: sText="Слишком большой файл. Ограничение "+sMaxWeight+" Мб"; break;
        case 4: sText="Неверный тип файла. Используйте JPG или PNG"; break;
        case 5: sText="Ошибка сохранения в БД"; break;
        case 6: sText="Клиент не найден"; break;
      }
      $('is_uploaded'+iNum).value=0;
      $('error'+iNum).update(sText);
      $('error'+iNum).show();
    }
    return true;
  }
</script>
  </body>
</html>
