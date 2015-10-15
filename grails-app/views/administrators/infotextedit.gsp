<html>
  <head>
  <title>Административное приложение StayToday.ru</title>
  <meta name="layout" content="admin" />
  <g:javascript>  
    function selectOnchange(el){
      var objSel = document.getElementById(el); 
      var statusID = objSel.options[objSel.selectedIndex].value;
      
      if (statusID == 1)
        objSel.className = 'icon active always';
      else if(statusID == 0)
        objSel.className = 'icon inactive always';
    }  
	
    function returnToList(){
	  $("returnToListForm").submit();
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
  <body onload="textCounter('title1','titles_limit',250);textCounter('title1_en','titles_limit_en',250);textCounter('keywords1','keywords_limit',255);">

  <g:if test="${flash?.save_error}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <g:if test="${flash?.save_error==101}"><li>Непоправимая ошибка. Данные не сохранены.</li></g:if>
      </ul>
    </div>
  </g:if>
  <g:form name="infotexteditForm" url="[controller:'administrators',action:'infotextedit', id:inrequest.id]" method="POST">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td colspan="2"><a class="to-parent" href="#" onClick="returnToList();">К списку инфотекстов</a></td>        
      </tr>
      <g:if test="${type!=1}">
      <tr>
        <td colspan="4"><h1 class="blue">Информационный текст &laquo;${inrequest.name}&raquo;</h1></td>
      </tr>
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span></span>Системные</h2>          
        </td>
      </tr> 
      <tr>  
        <td width="170">Название в меню:</td>
        <td><input type="text" id="name" name="name" style="width:100%" value="${inrequest?.name?:''}" /></td>
      </tr>
      <tr>
        <td>Название в меню EN:</td>
        <td><input type="text" id="name_en" name="name_en" style="width:100%" value="${inrequest?.name_en?:''}" /></td>
      </tr>      
      <tr>
        <td>Связанные страницы:</td>
        <td><textarea id="relatedpages" rows="5" cols="40" name="relatedpages" style="width:99%">${inrequest?.relatedpages}</textarea></td>
      </tr>      
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span class="edit_icon"></span>Метатеги</h2>
        </td>
      </tr>
      <tr>
        <td width="170">Title:</td>
        <td><textarea id="title1" rows="5" cols="40" name="title" style="width:99%" onkeydown="textCounter(this.id,'titles_limit',250);" onkeyup="textCounter(this.id,'titles_limit',250);">${inrequest?.title}</textarea>
          <span class="padd10">осталось <input type="text" class="limit" id="titles_limit" readonly /> символов</span>
        </td>
      </tr>
      <tr>
        <td>Title EN:</td>
        <td><textarea id="title1_en" rows="5" cols="40" name="title_en" style="width:99%" onkeydown="textCounter(this.id,'titles_limit_en',250);" onkeyup="textCounter(this.id,'titles_limit_en',250);">${inrequest?.title_en}</textarea>
          <span class="padd10">осталось <input type="text" class="limit" id="titles_limit_en" readonly /> символов</span>
        </td>
      </tr>
      <tr>
        <td>Keywords:</td>
        <td><textarea id="keywords1" rows="5" cols="40" name="keywords" style="width:99%" onkeydown="textCounter(this.id,'keywords_limit',255);" onkeyup="textCounter(this.id,'keywords_limit',255);">${inrequest?.keywords?:''}</textarea>
        <span class="padd10">осталось <input type="text" class="limit" id="keywords_limit" readonly /> символов</span></td>
      </tr>
      <tr>
        <td>Description:</td>
        <td><textarea rows="5" cols="40" name="description" style="width:99%">${inrequest?.description?:''}</textarea></td>
      </tr>
      <tr>
        <td>Description EN:</td>
        <td><textarea rows="5" cols="40" name="description_en" style="width:99%">${inrequest?.description_en?:''}</textarea></td>
      </tr>      
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span class="edit_icon address"></span>Заголовки</h2>
        </td>
      </tr>      
      <tr>
        <td>H1:</td>
        <td><input type="text" name="header" style="width:100%" value="${inrequest?.header?:''}"/></td>        
      </tr>
      <tr>
        <td>H1 EN:</td>
        <td><input type="text" name="header_en" style="width:100%" value="${inrequest?.header_en?:''}"/></td>        
      </tr>      
      <tr>
        <td nowrap>К промо-тексту 1:</td>
        <td><input type="text" name="promotext1" style="width:100%" value="${inrequest?.promotext1?:''}"/></td>
      </tr>
      <tr>
        <td nowrap>К промо-тексту 1 EN:</td>
        <td><input type="text" name="promotext1_en" style="width:100%" value="${inrequest?.promotext1_en?:''}"/></td>
      </tr>      
      <tr>
        <td nowrap>К промо-тексту 2:</td>
        <td><input type="text" name="promotext2" style="width:100%" value="${inrequest?.promotext2?:''}"/></td>
      </tr>
      <tr>
        <td nowrap>К промо-тексту 2 EN:</td>
        <td><input type="text" name="promotext2_en" style="width:100%" value="${inrequest?.promotext2_en?:''}"/></td>
      </tr>
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Промо-тексты</h2>
        </td>
      </tr>
      <tr>
        <td width="170" nowrap>Промотекст 1:</td>
        <td>
          <div class="sort-header clearfix">
            <span class="action_button">
              <div class="display-filter">                
                <select id="stext1" class="icon inactive" onChange="selectOnchange('stext1');$('text1').toggle();/*$('text1_en').toggle()*/" style="border:none;">                  	                    
                  <option class="icon inactive" value="0" selected="selected">скрыт</option>			          
                  <option class="icon active" value="1">показан</option>                                    
                </select>
              </div>       
            </span>
          </div>
        </td>
      </tr>
      <tr id="text1" style="display:none">
        <td colspan="2">
          <fckeditor:editor name="itext" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
            <g:rawHtml>${inrequest?.itext}</g:rawHtml>
          </fckeditor:editor>            
        </td>
      </tr>
      <tr>
        <td width="170" nowrap>Промотекст 1 EN:</td>
        <td>
          <div class="sort-header clearfix">
            <span class="action_button">
              <div class="display-filter">                
                <select id="stext1_en" class="icon inactive" onChange="selectOnchange('stext1_en');$('text1_en').toggle()" style="border:none;">                  	                    
                  <option class="icon inactive" value="0" selected="selected">скрыт</option>			          
                  <option class="icon active" value="1">показан</option>                                    
                </select>
              </div>       
            </span>
          </div>
        </td>
      </tr>
      <tr id="text1_en" style="display:none">        
        <td colspan="2">          
          <fckeditor:editor name="itext_en" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
            <g:rawHtml>${inrequest?.itext_en?:''}</g:rawHtml>
          </fckeditor:editor>            
        </td>
      </tr>
      <tr>
        <td nowrap>Промотекст 2:</td>
        <td>
          <div class="sort-header clearfix">
            <span class="action_button">
              <div class="display-filter">                
                <select id="stext2" class="icon inactive" onChange="selectOnchange('stext2');$('text2').toggle();" style="border:none;">                  	                    
                  <option class="icon inactive" value="0" selected="selected">скрыт</option>			          
                  <option class="icon active" value="1">показан</option>                                    
                </select>
              </div>       
            </span>
          </div>
        </td>
      </tr>
      <tr id="text2" style="display:none">
        <td colspan="2">
          <fckeditor:editor name="itext2" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
            <g:rawHtml>${inrequest?.itext2?:''}</g:rawHtml>
          </fckeditor:editor>            
        </td>
      </tr>
      <tr>
        <td nowrap>Промотекст 2 EN:</td>
        <td>
          <div class="sort-header clearfix">
            <span class="action_button">
              <div class="display-filter">                
                <select id="stext2_en" class="icon inactive" onChange="selectOnchange('stext2_en');$('text2_en').toggle();" style="border:none;">                  	                    
                  <option class="icon inactive" value="0" selected="selected">скрыт</option>			          
                  <option class="icon active" value="1">показан</option>                                    
                </select>
              </div>       
            </span>
          </div>
        </td>
      </tr>
      <tr id="text2_en" style="display:none"> 
        <td colspan="2">      
          <fckeditor:editor name="itext2_en" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
            <g:rawHtml>${inrequest?.itext2_en}</g:rawHtml>
          </fckeditor:editor>            
        </td>
      </tr>
      <tr>
        <td nowrap>Промотекст 3:</td>
        <td>
          <div class="sort-header clearfix">
            <span class="action_button">
              <div class="display-filter">                
                <select id="stext3" class="icon inactive" onChange="selectOnchange('stext3');$('text3').toggle();" style="border:none;">                  	                                      
                  <option class="icon inactive" value="0" selected="selected">скрыт</option>			          
                  <option class="icon active" value="1">показан</option>                  
                </select>
              </div>       
            </span>
          </div>
        </td>
      </tr>
      <tr id="text3" style="display:none">
        <td colspan="2">
          <fckeditor:editor name="itext3" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
            <g:rawHtml>${inrequest?.itext3?:''}</g:rawHtml>
          </fckeditor:editor>            
        </td>
      </tr>
      <tr>
        <td nowrap>Промотекст 3 EN:</td>
        <td>
          <div class="sort-header clearfix">
            <span class="action_button">
              <div class="display-filter">                
                <select id="stext3_en" class="icon inactive" onChange="selectOnchange('stext3_en');$('text3_en').toggle()" style="border:none;">                  	                                      
                  <option class="icon inactive" value="0" selected="selected">скрыт</option>			          
                  <option class="icon active" value="1">показан</option>                  
                </select>
              </div>       
            </span>
          </div>
        </td>
      </tr>     
      <tr id="text3_en" style="display:none">        
        <td colspan="2">          
          <fckeditor:editor name="itext3_en" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
            <g:rawHtml>${inrequest?.itext3_en}</g:rawHtml>
          </fckeditor:editor>            
        </td>
      </tr>
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span></span>Анонс</h2>
        </td>
      </tr>
      <tr>
        <td width="170">Анонс:</td>
        <td><input type="checkbox"  name="is_anons" value="1" <g:if test="${inrequest?.is_anons}">checked</g:if>/>        
      </tr>
      </g:if><g:else>
      <tr>
        <td colspan="4"><h1 class="blue">Шаблон письма &laquo;${inrequest.name}&raquo;</h1></td>
      </tr>
      <tr>
        <td width="170">Название:</td>
        <td><textarea rows="5" cols="40" name="name" style="width:99%">${inrequest?.name}</textarea>        
      </tr>
      <tr>
        <td>Тема письма:</td>
        <td><textarea rows="5" cols="40" name="title" style="width:99%">${inrequest?.title}</textarea>        
      </tr>
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Текст письма</h2>
        </td>
      </tr>
      <tr id="text1">
        <td colspan="2">
          <fckeditor:editor name="itext" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
            <g:rawHtml>${inrequest?.itext}</g:rawHtml>
          </fckeditor:editor>            
        </td>
      </tr>
      </g:else>      
      <tr>
        <td colspan="2" align="right">
          <input type="submit" class="button-glossy green" value="Сохранить"/>
        </td>
      </tr>
      <tr>
        <td colspan="2"><a class="to-parent" href="#" onClick="returnToList();">К списку инфотекстов</a></td>        
      </tr>
    </table>
    <input type="hidden" id="save" name="save" value="1" />
    <input type="hidden" id="type" name="type" value="${type?:0}" />
  </g:form>    
  <g:form name="returnToListForm" url="${[controller:'administrators',action:'infotext', params:[fromEdit:1, type:type?:0]]}">
  </g:form>  
  </body>
</html>
