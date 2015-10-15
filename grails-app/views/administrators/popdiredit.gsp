<html>
  <head>
  <title>Административное приложение StayToday.ru</title>
  <meta name="layout" content="admin" />
  <g:javascript>  
    function returnToList(){
      $("returnToListForm").submit();
    }
    function deleteHome(lId){
      if (confirm('Вы уверены?')){
        <g:remoteFunction controller='administrators' action='popdirhomedelete' onSuccess='hideHome(lId)' params="'id='+lId" />
      }
      return false;
    }
    function hideHome(lId){
      $('popdirhome_'+lId.toString()).hide();
    }
    function deletepicuserphoto(){
      <g:remoteFunction controller='administrators' action='deletepopdirphoto' onSuccess='reloadImage(1)' params="'name=file1'" />
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
  <body onload="textCounter('shortdescr','shortdescr_limit',250);textCounter('shortdescr_en','shortdescr_limit_en',250);textCounter('annotation','annotation_limit',250);textCounter('annotation_en','annotation_limit_en',250);textCounter('annotation_cot','annotation_cot_limit',250);textCounter('annotation_cot_en','annotation_cot_limit_en',250);">

  <g:if test="${flash?.save_error||flash?.error}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
      <g:if test="${flash?.save_error==101}"><li>Непоправимая ошибка. Данные не сохранены.</li></g:if>
      <g:each in="${flash?.error}">
        <g:if test="${it==1}"><li>Вы не заполнили обязательное поле &laquo;Расш. Название&raquo;</li></g:if>
        <g:if test="${it==2}"><li>Вы не заполнили обязательное поле &laquo;Linkname&raquo;</li></g:if>
        <g:if test="${it==3}"><li>Поле &laquo;Расш. Название&raquo; не уникально!</li></g:if>
        <g:if test="${it==4}"><li>Поле &laquo;linkname&raquo; не уникально!</li></g:if>
        <g:if test="${it==5}"><li>Вы не заполнили обязательное поле &laquo;Название&raquo;</li></g:if>
      </g:each>
      </ul>
    </div>
  </g:if>  
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td colspan="4">
          <a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку популярных направлений</a>
        </td>
      </tr>
      <tr>
        <td colspan="4"><h1 class="blue">
        <g:if test="${inrequest.id}">
          Популярное направление &laquo;${inrequest.name}&raquo;
        </g:if><g:else>
          Добавление нового направления
        </g:else></h1></td>
      </tr>
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Главная фотка</h2>
        </td>
      </tr>
      <tr>
        <td colspan="2">
          <g:form name='ff1' method="post" url="${[action:'savepopdirphoto']}" enctype="multipart/form-data" target="upload_target">
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
        <td colspan="4">
          <g:form name="infotexteditForm" url="[controller:'administrators',action:'popdiredit', id:inrequest.id]" method="POST">
          <table width="100%" cellpadding="5" cellspacing="5" border="0">
            <tr>
              <td colspan="4" valign="top">
                <h2 class="toggle border"><span class="edit_icon question"></span>Служебные</h2>
              </td>
            </tr>     
            <tr>
              <td colspan="4" style="padding:0px">
                <table width="100%" cellpadding="5" cellspacing="5" border="0">
                  <tr>
                    <td width="100">Статус:</td>
                    <td>
                      <select name="modstatus">
                        <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>скрыто</option>
                        <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>активно</option>
                      </select>
                    </td>
                    <td>Страна:</td>
                    <td>
                      <select name="country_id">
                        <option value="0" <g:if test="${inrequest?.country_id==0}">selected="selected"</g:if>></option>
                        <g:each in="${countries}" var="country" status="i">                
                          <option value="${country?.id}" <g:if test="${inrequest?.country_id==country?.id}">selected="selected"</g:if>>${country?.name}</option>
                        </g:each>
                      </select>
                    </td>
                    <td width="150" nowrap>Число посещений:</td>
                    <td><input type="text" name="ncount" disabled value="${inrequest?.ncount?:''}" style="width:100px"/></td>
                    <td width="150" nowrap>
                      <input type="checkbox" name="is_main" <g:if test="${inrequest?.is_main}">checked</g:if> value="1"/>
                      <label for="is_main">На главной</label>
                    </td>
                    <td width="150" nowrap>
                      <input type="checkbox" name="is_index" <g:if test="${inrequest?.is_index}">checked</g:if> value="1"/>
                      <label for="is_index">Индексация</label>
                    </td>
                  </tr>
                  <tr>
                    <td width="150" nowrap colspan="4">
                      <input type="checkbox" name="is_specoffer" <g:if test="${inrequest?.is_specoffer}">checked</g:if> value="1"/>
                      <label for="is_specoffer">Спец. предложения</label>
                    </td>
                  </tr>                 
                </table>
              </td>
            </tr>
            <tr>
              <td colspan="4" valign="top">
                <h2 class="toggle border"><span class="edit_icon details"></span>Описание</h2>
              </td>
            </tr>      
            <tr>
              <td width="100">Linkname:</td>
              <td><input type="text" name="linkname" style="width:100%" value="${inrequest?.linkname?:''}"/></td>
              <td>Keyword:</td>
              <td><input type="text" name="keyword" style="width:100%" value="${inrequest?.keyword?:''}"/></td>
            </tr>
            <tr>
              <td>Заголовок:</td>
              <td colspan="3"><input type="text" name="header" style="width:100%" value="${inrequest?.header?:''}"/></td>
            </tr>
            <tr>
              <td>Заголовок EN:</td>
              <td colspan="3"><input type="text" name="header_en" style="width:100%" value="${inrequest?.header_en?:''}"/></td>
            </tr>
            <tr>
              <td>Название:</td>
              <td><input type="text" name="name2" style="width:100%" value="${inrequest?.name2?:''}"/></td>
              <td>Название EN:</td>
              <td><input type="text" name="name2_en" style="width:100%" value="${inrequest?.name2_en?:''}"/></td>
            </tr>
            <tr>             
              <td>Рейтинг:</td>
              <td><input type="text" name="rating" value="${inrequest?.rating?:''}"/></td>
            </tr>
            <tr>
              <td>Расш.название:</td>
              <td colspan="3"><input type="text" name="name" style="width:100%" value="${inrequest?.name?:''}"/></td>
            </tr>
            <tr>
              <td>Расш.название EN:</td>
              <td colspan="3"><input type="text" name="name_en" style="width:100%" value="${inrequest?.name_en?:''}"/></td>
            </tr>
            <tr>
              <td>Название(род):</td>
              <td colspan="3"><input type="text" name="name_r" style="width:100%" value="${inrequest?.name_r?:''}"/></td>
            </tr>
            <tr>
              <td>Тег статей:</td>
              <td colspan="3"><input type="text" name="tagname" style="width:100%" value="${inrequest?.tagname?:''}"/></td>
            </tr>
            <tr>
              <td>Пред. направление:</td>
              <td>
                <g:select id="previousdir" name='previousdir' value="${inrequest?.previousdir}" noSelection="${['0':'Select One...']}" from='${nearDirections}' optionKey="id" optionValue="name2"></g:select>
              </td>
              <td>След. направление:</td>
              <td>
                <g:select id="nextdir" name='nextdir' value="${inrequest?.nextdir}" noSelection="${['0':'Select One...']}" from='${nearDirections}' optionKey="id" optionValue="name2"></g:select>
              </td>
            </tr>
            <tr>
              <td>Shortdescription:</td>
              <td colspan="3">
                <textarea id="shortdescr" style="width:99%" name="shortdescription" cols="40" rows="4" onkeydown="textCounter(this.id,'shortdescr_limit',250);" onkeyup="textCounter(this.id,'shortdescr_limit',250);">${inrequest?.shortdescription?:''}</textarea>
                <span class="padd10">осталось <input type="text" class="limit" id="shortdescr_limit" readonly /> символов</span>
              </td>
            </tr>
            <tr>
              <td>Shortdescription EN:</td>
              <td colspan="3">
                <textarea id="shortdescr_en" style="width:99%" name="shortdescription_en" cols="40" rows="4" onkeydown="textCounter(this.id,'shortdescr_limit_en',250);" onkeyup="textCounter(this.id,'shortdescr_limit_en',250);">${inrequest?.shortdescription_en?:''}</textarea>
                <span class="padd10">осталось <input type="text" class="limit" id="shortdescr_limit_en" readonly /> символов</span>
              </td>
            </tr>
            <tr>
              <td>Annotation:</td>
              <td colspan="3">
                <textarea id="annotation" style="width:99%" name="annotation" cols="40" rows="4" onkeydown="textCounter(this.id,'annotation_limit',250);" onkeyup="textCounter(this.id,'annotation_limit',250);">${inrequest?.annotation?:''}</textarea>
                <span class="padd10">осталось <input type="text" class="limit" id="annotation_limit" readonly /> символов</span>
              </td>
            </tr>
            <tr>
              <td>Annotation EN:</td>
              <td colspan="3">
                <textarea id="annotation_en" style="width:99%" name="annotation_en" cols="40" rows="4" onkeydown="textCounter(this.id,'annotation_limit_en',250);" onkeyup="textCounter(this.id,'annotation_limit_en',250);">${inrequest?.annotation_en?:''}</textarea>
                <span class="padd10">осталось <input type="text" class="limit" id="annotation_limit_en" readonly /> символов</span>
              </td>
            </tr>
            <tr id="itext">
              <td colspan="4">
                <label for="itext">Текст описания:</label>
                <fckeditor:editor name="itext" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
                  <g:rawHtml>${inrequest?.itext}</g:rawHtml>
                </fckeditor:editor>            
              </td>
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
              <td>Title EN:</td>
              <td colspan="3"><input type="text" name="ceo_title_en" style="width:100%" value="${inrequest?.ceo_title_en?:''}"/></td>        
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
                <textarea style="width:99%" name="ceo_description" cols="40" rows="4">${inrequest?.ceo_description?:''}</textarea>
              </td>
            </tr>
            <tr>
              <td>Description EN:</td>
              <td colspan="3">
                <textarea style="width:99%" name="ceo_description_en" cols="40" rows="4">${inrequest?.ceo_description_en?:''}</textarea>
              </td>
            </tr>
            <tr>
              <td colspan="4" valign="top">
                <h2 class="toggle border"><span class="edit_icon"></span>Метатеги скидок</h2>
              </td>
            </tr>
            <tr>
              <td>Header_d:</td>
              <td colspan="3"><input type="text" name="header_d" style="width:100%" value="${inrequest?.header_d?:''}"/></td>
            </tr>
            <tr>
              <td>Header_d EN:</td>
              <td colspan="3"><input type="text" name="header_d_en" style="width:100%" value="${inrequest?.header_d_en?:''}"/></td>
            </tr>
            <tr>
              <td>Title_d:</td>
              <td colspan="3"><input type="text" name="ceo_title_d" style="width:100%" value="${inrequest?.ceo_title_d?:''}"/></td>
            </tr>
            <tr>
              <td>Title_d EN:</td>
              <td colspan="3"><input type="text" name="ceo_title_d_en" style="width:100%" value="${inrequest?.ceo_title_d_en?:''}"/></td>
            </tr>
            <tr>
              <td>Keywords_d:</td>
              <td colspan="3">
                <textarea style="width:99%" name="ceo_keywords_d" cols="40" rows="4">${inrequest?.ceo_keywords_d?:''}</textarea>
              </td>
            </tr>
            <tr>
              <td>Description_d:</td>
              <td colspan="3">
                <textarea style="width:99%" name="ceo_description_d" cols="40" rows="4">${inrequest?.ceo_description_d?:''}</textarea>
              </td>
            </tr>
            <tr>
              <td>Description_d EN:</td>
              <td colspan="3">
                <textarea style="width:99%" name="ceo_description_d_en" cols="40" rows="4">${inrequest?.ceo_description_d_en?:''}</textarea>
              </td>
            </tr>
            <tr>
              <td colspan="4" valign="top">
                <h2 class="toggle border"><span class="edit_icon"></span>Метатеги коттеджей</h2>
              </td>
            </tr>
            <tr>
              <td>Header_cot:</td>
              <td colspan="3"><input type="text" name="header_cot" style="width:100%" value="${inrequest?.header_cot?:''}"/></td>
            </tr>
            <tr>
              <td>Header_cot EN:</td>
              <td colspan="3"><input type="text" name="header_cot_en" style="width:100%" value="${inrequest?.header_cot_en?:''}"/></td>
            </tr>
            <tr>
              <td>Title_cot:</td>
              <td colspan="3"><input type="text" name="ceo_title_cot" style="width:100%" value="${inrequest?.ceo_title_cot?:''}"/></td>
            </tr>
            <tr>
              <td>Title_cot EN:</td>
              <td colspan="3"><input type="text" name="ceo_title_cot_en" style="width:100%" value="${inrequest?.ceo_title_cot_en?:''}"/></td>
            </tr>
            <tr>
              <td>Keywords_cot:</td>
              <td colspan="3">
                <textarea style="width:99%" name="ceo_keywords_cot" cols="40" rows="4">${inrequest?.ceo_keywords_cot?:''}</textarea>
              </td>
            </tr>
            <tr>
              <td>Description_cot:</td>
              <td colspan="3">
                <textarea style="width:99%" name="ceo_description_cot" cols="40" rows="4">${inrequest?.ceo_description_cot?:''}</textarea>
              </td>
            </tr>
            <tr>
              <td>Description_cot EN:</td>
              <td colspan="3">
                <textarea style="width:99%" name="ceo_description_cot_en" cols="40" rows="4">${inrequest?.ceo_description_cot_en?:''}</textarea>
              </td>
            </tr>
            <tr>
              <td>Annotation_cot:</td>
              <td colspan="3">
                <textarea id="annotation_cot" style="width:99%" name="annotation_cot" cols="40" rows="4" onkeydown="textCounter(this.id,'annotation_cot_limit',250);" onkeyup="textCounter(this.id,'annotation_cot_limit',250);">${inrequest?.annotation_cot?:''}</textarea>
                <span class="padd10">осталось <input type="text" class="limit" id="annotation_cot_limit" readonly /> символов</span>
              </td>
            </tr>
            <tr>
              <td>Annotation_cot EN:</td>
              <td colspan="3">
                <textarea id="annotation_cot_en" style="width:99%" name="annotation_cot_en" cols="40" rows="4" onkeydown="textCounter(this.id,'annotation_cot_limit_en',250);" onkeyup="textCounter(this.id,'annotation_cot_limit_en',250);">${inrequest?.annotation_cot_en?:''}</textarea>
                <span class="padd10">осталось <input type="text" class="limit" id="annotation_cot_limit_en" readonly /> символов</span>
              </td>
            </tr>            
            <tr id="itext_cot">
              <td colspan="4">
                <label for="itext_cot">Текст описания:</label>
                <fckeditor:editor name="itext_cot" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
                  <g:rawHtml>${inrequest?.itext_cot}</g:rawHtml>
                </fckeditor:editor>            
              </td>
            </tr>
<g:if test="${inrequest.id}">
            <tr>
              <td colspan="4" valign="top">
                <h2 class="toggle border"><span class="edit_icon photos"></span>Объявления</h2>
              </td>
            </tr>      
            <tr>
              <td colspan="4">
<g:if test="${records}">
                <table width="100%" class="dotted" cellpadding="0" cellspacing="0" border="0" rules="all" frame="border" style="border-style:dotted">
                <g:each in="${records}" var="item" status="i">
                  <tr id="popdirhome_${item.id}">
                    <td width="120">
                      <div class="thumbnail" id="thumbnail_${item.id}">
                        <g:if test="${item?.mainpicture}">
                          <img src="${(item?.mainpicture)?urlphoto+item?.client_id+'/t_'+item?.mainpicture:resource(dir:'images',file:'default-picture.png')}">
                        </g:if>
                      </div>
                    </td>
                    <td>
                      <input type="text" readonly value="${item.name}" style="width:100%"/>
                    </td>
                    <td width="80">
                      <div class="actions">
                        <span class="action_button nowrap">
                          <a class="icon delete" title="Удалить" href="#" onClick="deleteHome(${item.id})">Удалить</a>
                        </span>
                      </div>
                    </td>
                  </tr>
                </g:each>
                </table>
</g:if>
<g:else>
                <p>Объявления не заданы</p>
</g:else>          
              </td>
            </tr>
</g:if>
            <tr>
              <td colspan="4" align="right">
                <input type="submit" class="button-glossy green" <g:if test="${inrequest.id}">value="Сохранить"</g:if><g:else>value="Добавить"</g:else> style="margin-right:10px;">
                <input type="button" class="button-glossy grey" <g:if test="${inrequest.id}">style="display: none"</g:if> value="Отмена" onclick="returnToList();">
              </td>
            </tr>
            <input type="hidden" id="save" name="save" value="1" />
          </table>
          </g:form>
        </td>
      </tr>      
      <tr>
        <td colspan="4">
          <a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку популярных направлений</a>
        </td>
      </tr>
    </table>
  <g:form name="returnToListForm" url="${[controller:'administrators',action:'popdir', params:[fromEdit:1]]}">
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
        case 1: 
        case 2: sText="Ошибка загрузки"; break;
        case 3: sText="Слишком большой файл. Ограничение "+sMaxWeight+" Мб"; break;
        case 4: sText="Неверный тип файла. Используйте JPG или PNG"; break;
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
