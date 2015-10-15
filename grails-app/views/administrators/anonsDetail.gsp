<html>
  <head>
  <title>Административное приложение StayToday.ru</title>
  <meta name="layout" content="admin" />
  <g:javascript>  
    function returnToList(){
      $("returnToListForm").submit();
    }
    function deletepicuserphoto(){
      <g:remoteFunction controller='administrators' action='deleteanonsphoto' onSuccess='reloadImage(1)' params="'name=file1'" />
    }
    function processResponse(e){
      if (e.responseJSON.error.length) {
        document.getElementById('errContaner').show();
        e.responseJSON.error.indexOf(1)>-1?document.getElementById('err1').show():document.getElementById('err1').hide();
        e.responseJSON.error.indexOf(101)>-1?document.getElementById('err101').show():document.getElementById('err101').hide();
      } else if (!${inrequest?.id?:0}) {
        location.assign(location.href+'/'+e.responseJSON.id)
      }else{
        location.reload(true);
      }
    }    
	</g:javascript>
  </head>
  <body onload="">
    <div id="errContaner" class="notice drop_shadow" style="display:none">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <li id="err101" style="display:none">Непоправимая ошибка. Данные не сохранены.</li>
        <li id="err1" style="display:none">Не заполнено обязательное поле &laquo;Title&raquo;.</li>
      </ul>
    </div>
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td><a class="to-parent" href="#" onClick="returnToList();">К списку анонсов</a></td>
      </tr>
      <tr>
        <td>
        <g:if test="${inrequest?.id}">
          <h1 class="blue">Анонс &laquo;${inrequest.title}&raquo;</h1>
        </g:if><g:else>
          <h1 class="blue">Добавление нового анонса</h1>
        </g:else>
        </td>
      </tr>
<!--      
      <tr>
        <td valign="top">
          <h2 class="toggle border"><span class="edit_icon photo"></span>Изображение</h2>
        </td>
      </tr>
      <tr>
        <td valign="top">
          <input type="text" name="image" style="width:100%" value="${inrequest?.image?:''}" />                           
        </td>  
      </tr>    -->
      <!--<tr>
        <td>
          <g:form name='ff1' method="post" url="${[action:'saveanonsphoto']}" enctype="multipart/form-data" target="upload_target">
            <div id="error1" style="display: none;"></div>
            <div id="uploaded1" class="upload" style="float:left">
            <g:if test="${inrequest?.image}">
              <img width="68" height="68" src="${imageurl}${inrequest?.image}" border="0"/>
            </g:if><g:else>
              <img width="68" height="68" src="${resource(dir:'images',file:'user-default-picture.png')}" border="0"/>
            </g:else>
            </div>
            <div style="width:50%;padding-top:45px;float:right;">
              <input type="button" id="button1" value="Изменить" onclick="deletepicuserphoto()" <g:if test="${!inrequest?.image}">style="display: none;"</g:if>>
              <input type="file" name="file1" id="file1" size="23" accept="image/jpeg,image/gif" onchange="startSubmit('ff1')" <g:if test="${inrequest?.image}">style="display: none;"</g:if>/>
              <input type="hidden" name="is_uploaded1" id="is_uploaded1" value="${images?.photo_1?1:0}" />
            </div>
          </g:form>
          <div id="loader" style="position: absolute; top: 100px; display: none; width: 16px; height: 16px;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" border="0">
          </div>
          <iframe id="upload_target" name="upload_target" src="#" style="width:0;height:0;border:0px solid #fff;"></iframe>
        </td>
      </tr>-->
      <tr>
        <td valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Статистика</h2>
          <table width="100%" cellpadding="5" cellspacing="5" border="0">
            <tr>
              <td>Показы:</td>
              <td><input type="text"  maxlength="50" style="width:100%" value="${inrequest?.ncount?:0}" readonly/></td>              
              <td>Клики:</td>
              <td><input type="text"  maxlength="50" style="width:100%" value="${inrequest?.nclick?:0}" readonly/></td>             
            </tr>
          </table>
          <h2 class="toggle border"><span class="edit_icon details"></span>Описание</h2>
          <g:formRemote name="anonseditForm" url="[controller:'administrators',action:'anonssave', id:id]" method="post" onSuccess="processResponse(e)">
            <table width="100%" cellpadding="5" cellspacing="5" border="0">
              <tr>
                <td>Статус:</td>
                <td>
                <select name="modstatus" id="modstatus">
                  <option value="0">неактивное</option>                
                  <option value="1" <g:if test="${inrequest?.modstatus?:0}">selected="selected"</g:if>>активное</option>
                </select>
                </td>
              </tr>
              <tr>
                <td>Страницы:</td>
                <td>
                <select name="page" id="page" multiple="multiple" size="5" style="height:auto">
                  <g:each in="${pages}">
                    <option value="${it.id}" <g:if test="${(inrequest?.page?.tokenize(',')?:[]).contains(it.id.toString())}">selected="selected"</g:if>>${it.name}</option>
                  </g:each>                 
                </select>
                </td>
              </tr>
              <tr>
                <td>Заголовок:</td>
                <td colspan="3"><input type="text" name="title" maxlength="250" style="width:100%" value="${inrequest?.title?:''}"/></td>
              </tr>
              <tr>
                <td>Заголовок EN:</td>
                <td colspan="3"><input type="text" name="title_en" maxlength="250" style="width:100%" value="${inrequest?.title_en?:''}"/></td>
              </tr>
              <tr>                
                <td>Текст:</td>
                <td colspan="3"><textarea id="sight_description" name="description" cols="40" rows="4" style="width:100%">${inrequest?.description?:''}</textarea></td>                
              </tr>
              <tr>                
                <td>Текст EN:</td>
                <td colspan="3"><textarea id="sight_description_en" name="description_en" cols="40" rows="4" style="width:100%">${inrequest?.description_en?:''}</textarea></td>                
              </tr>              
              <tr>
                <td></td>
                <td colspan="3">в URL относительный путь без лидирующего '/'</td>
              </tr>
              <tr>
                <td>Url:</td>
                <td colspan="3"><input type="text" name="url" maxlength="250" style="width:100%" value="${inrequest?.url?:''}"/></td>
              </tr>
              <tr>
                <td>Изображение:</td>
                <td colspan="3"><input type="text" name="image" style="width:100%" value="${inrequest?.image?:''}" /></td>  
              </tr> 
              <tr>
                <td>Индексирование:</td>
                <td><input style="align:left" type="checkbox" name="is_index" style="width:100%" value="1" <g:if test="${inrequest?.is_index?:''}">checked</g:if> /></td>  
              </tr>              
              <tr>
                <td colspan="4" align="right">
                  <input type="submit" class="button-glossy green" value="Сохранить"/>
                </td>
              </tr>              
            </table>
            <input type="hidden" name="id" value="${inrequest?.id?:0}"/>
          </g:formRemote>
        </td>
      </tr>
      <tr>
        <td><a class="to-parent" href="#" onClick="returnToList();">К списку анонсов</a></td>
      </tr>
    </table>
  <g:form name="returnToListForm" url="${[controller:'administrators',action:'anons', params:[fromDetails:1]]}">
  </g:form>
<!-- /Загрузка имиджей -->
<script language="javascript" type="text/javascript">
  function reloadImage(iNum){
    $('uploaded'+iNum).update('<img width="68" height="68" src="${resource(dir:'images',file:'user-default-picture.png')}" border="0"/>');
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
      $('uploaded'+iNum).update('<img width="68" height="68" src="${imageurl}'+sFilename+'" border="0"/>');
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
