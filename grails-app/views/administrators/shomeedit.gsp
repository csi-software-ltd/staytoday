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
  <body onload="textCounter('header','header_limit',250);textCounter('header_en','header_limit_en',250);textCounter('title','title_limit',250);textCounter('title_en','title_limit_en',250);textCounter('keywords1','keywords_limit',250);textCounter('keywords_en','keywords_limit_en',250);textCounter('description','description_limit',250);textCounter('description_en','description_limit_en',250);textCounter('promotext','promotext_limit',250);textCounter('promotext_en','promotext_limit_en',250);textCounter('linkname','linkname_limit',50);textCounter('linkname_en','linkname_limit_en',50);" >

  <g:if test="${flash?.error}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <g:if test="${flash?.error.contains(3)}"><li>Вы не заполнили обязательное поле &laquo;H1&raquo;</li></g:if>
        <g:if test="${flash?.error.contains(4)}"><li>Вы не заполнили обязательное поле &laquo;H1 EN&raquo;</li></g:if>
        <g:if test="${flash?.error.contains(5)}"><li>Вы не заполнили обязательное поле &laquo;Название ссылки&raquo;</li></g:if>
        <g:if test="${flash?.error.contains(6)}"><li>Вы не заполнили обязательное поле &laquo;Название ссылки EN&raquo;</li></g:if>                
        <g:if test="${flash?.error.contains(1)}"><li>Вы не заполнили обязательное поле &laquo;Город&raquo;</li></g:if>
        <g:if test="${flash?.error.contains(2)}"><li>Вы не заполнили обязательное поле &laquo;Тип&raquo;</li></g:if>        
        <g:if test="${flash?.error.contains(100)}"><li>Непоправимая ошибка. Данные не сохранены.</li></g:if>
      </ul>
    </div>
  </g:if>
  <g:form name="shomeeditForm" url="${[controller:'administrators',action:'shomeedit']}" method="POST">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td colspan="2"><a class="to-parent" href="#" onClick="returnToList();">К списку</a></td>        
      </tr>   
      <tr>
        <td colspan="4"><h1 class="blue"><g:if test="${inrequest?.linkname}">Апартаменты &laquo;${inrequest?.linkname}&raquo;</g:if><g:else>Новые апартаменты</g:else>&nbsp;&nbsp;&nbsp;<g:if test="${inrequest?.modstatus!=null}">${inrequest?.modstatus?'активны':'неактивны'}</g:if></h1></td>
      </tr>
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span></span>Системные</h2>          
        </td>
      </tr>       
      <tr>  
        <td width="160">Название ссылки:</td>
        <td>
          <input type="text" id="linkname" rows="5" cols="40" name="linkname" style="width:100%" onkeydown="textCounter(this.id,'linkname_limit',250);" onkeyup="textCounter(this.id,'linkname_limit',50);" value="${inrequest?.linkname?:''}" />
          <span class="padd10">осталось <input type="text" class="limit" id="linkname_limit" readonly /> символов</span>          
        </td>
      </tr>
      <tr>
        <td width="160">Название ссылки EN:</td>
        <td>
          <input type="text" id="linkname_en" rows="5" cols="40" name="linkname_en" style="width:100%" onkeydown="textCounter(this.id,'linkname_limit_en',50);" onkeyup="textCounter(this.id,'linkname_limit_en',50);" value="${inrequest?.linkname_en?:''}" />
          <span class="padd10">осталось <input type="text" class="limit" id="linkname_limit_en" readonly /> символов</span>          
        </td>
      </tr>
      <tr>
        <td width="160">Город:</td>
        <td><g:select name="city_id" from="${city}" value="${inrequest?.city_id?:0}" noSelection="${[0:'не задан']}" optionKey="id" optionValue="name" /></td>
      </tr>  
      <tr>
        <td width="160">Тип:</td>
        <td><g:select name="type_id" from="${shometype}" value="${inrequest?.type_id?:0}" noSelection="${[0:'не задан']}" optionKey="id" optionValue="name" /></td>
      </tr>      
      <tr>
        <td width="160">Количество:</td>
        <td><input type="text" name="homecount" style="width:100%" value="${inrequest?.homecount?:0}" readonly /></td>
      </tr>                       
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span class="edit_icon"></span>Метатеги</h2>
        </td>
      </tr>
      <tr>
        <td width="160">Title:</td>
        <td><textarea id="title" rows="5" cols="40" name="title" style="width:100%" onkeydown="textCounter(this.id,'title_limit',250);" onkeyup="textCounter(this.id,'title_limit',250);">${inrequest?.title?:''}</textarea>
          <span class="padd10">осталось <input type="text" class="limit" id="title_limit" readonly /> символов</span>
        </td>
      </tr>
      <tr>
        <td width="160">Title EN:</td>
        <td><textarea id="title_en" rows="5" cols="40" name="title_en" style="width:100%" onkeydown="textCounter(this.id,'title_limit_en',250);" onkeyup="textCounter(this.id,'title_limit_en',250);">${inrequest?.title_en?:''}</textarea>
          <span class="padd10">осталось <input type="text" class="limit" id="title_limit_en" readonly /> символов</span>
        </td>
      </tr>
      <tr>
        <td>Keywords:</td>
        <td><textarea id="keywords1" rows="5" cols="40" name="keywords" style="width:100%" onkeydown="textCounter(this.id,'keywords_limit',250);" onkeyup="textCounter(this.id,'keywords_limit',250);">${inrequest?.keywords?:''}</textarea>
        <span class="padd10">осталось <input type="text" class="limit" id="keywords_limit" readonly /> символов</span></td>
      </tr>
      <tr>
        <td>Keywords EN:</td>
        <td><textarea id="keywords_en" rows="5" cols="40" name="keywords_en" style="width:100%" onkeydown="textCounter(this.id,'keywords_limit_en',250);" onkeyup="textCounter(this.id,'keywords_limit_en',250);">${inrequest?.keywords_en?:''}</textarea>
        <span class="padd10">осталось <input type="text" class="limit" id="keywords_limit_en" readonly /> символов</span></td>
      </tr>
      <tr>
        <td>Description:</td>
        <td><textarea rows="5" cols="40" id="description" name="description" style="width:99%" onkeydown="textCounter(this.id,'description_limit',250);" onkeyup="textCounter(this.id,'description_limit',250);">${inrequest?.description?:''}</textarea>
        <span class="padd10">осталось <input type="text" class="limit" id="description_limit" readonly /> символов</span></td>       
      </tr>
      <tr>
        <td>Description EN:</td>
        <td><textarea rows="5" cols="40" id="description_en" name="description_en" style="width:99%" onkeydown="textCounter(this.id,'description_limit_en',250);" onkeyup="textCounter(this.id,'description_limit_en',250);">${inrequest?.description_en?:''}</textarea>
        <span class="padd10">осталось <input type="text" class="limit" id="description_limit_en" readonly /> символов</span></td>
      </tr>
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span class="edit_icon address"></span>Заголовки</h2>
        </td>
      </tr>
      <tr>  
        <td width="160">H1:</td>
        <td><input type="text" id="header" name="header" style="width:100%" onkeydown="textCounter(this.id,'header_limit',250);" onkeyup="textCounter(this.id,'header_limit',250);" value="${inrequest?.header?:''}" />
          <span class="padd10">осталось <input type="text" class="limit" id="header_limit" readonly /> символов</span>          
        </td>
      </tr>
      <tr>
        <td width="160">H1 EN:</td>
        <td><input type="text" id="header_en" name="header_en" style="width:100%" onkeydown="textCounter(this.id,'header_limit_en',250);" onkeyup="textCounter(this.id,'header_limit_en',250);" value="${inrequest?.header_en?:''}" />
          <span class="padd10">осталось <input type="text" class="limit" id="header_limit_en" readonly /> символов</span>          
        </td>
      </tr>      
      <tr>
        <td>К промо-тексту 2:</td>
        <td><textarea id="promotext" rows="5" cols="40" name="promotext" style="width:100%" onkeydown="textCounter(this.id,'promotext_limit',250);" onkeyup="textCounter(this.id,'promotext_limit',250);">${inrequest?.promotext?:''}</textarea>
        <span class="padd10">осталось <input type="text" class="limit" id="promotext_limit" readonly /> символов</span></td>
      </tr>
      <tr>
        <td>К промо-тексту 2 EN:</td>
        <td><textarea id="promotext_en" rows="5" cols="40" name="promotext_en" style="width:100%" onkeydown="textCounter(this.id,'promotext_limit_en',250);" onkeyup="textCounter(this.id,'promotext_limit_en',250);">${inrequest?.promotext_en?:''}</textarea>
        <span class="padd10">осталось <input type="text" class="limit" id="promotext_limit_en" readonly /> символов</span></td>
      </tr>
      <tr>
        <td colspan="2" valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Промо-тексты</h2>
        </td>
      </tr>      
      <tr>
        <td width="160" nowrap>Промо-текст 1:</td>        
        <td>
          <div class="sort-header clearfix">
            <span class="action_button">
              <div class="display-filter">                
                <select id="stext1" class="icon inactive" onChange="selectOnchange('stext1');$('text1').toggle();" style="border:none;">                  	                    
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
        <td width="160" nowrap>Промо-текст 1 EN:</td>
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
        <td width="160" nowrap>Промо-текст 2:</td>        
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
            <g:rawHtml>${inrequest?.itext2}</g:rawHtml>
          </fckeditor:editor>            
        </td>
      </tr>
      <tr>
        <td width="160" nowrap>Промо-текст 2 EN:</td>
        <td>
          <div class="sort-header clearfix">
            <span class="action_button">
              <div class="display-filter">                
                <select id="stext2_en" class="icon inactive" onChange="selectOnchange('stext2_en');$('text2_en').toggle()" style="border:none;">                  	                    
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
            <g:rawHtml>${inrequest?.itext2_en?:''}</g:rawHtml>
          </fckeditor:editor>            
        </td>
      </tr>      
      <tr>
        <td colspan="2" align="right">
          <input type="submit" class="button-glossy green" value="Сохранить"/>
        </td>
      </tr>
      <tr>
        <td colspan="2"><a class="to-parent" href="#" onClick="returnToList();">К списку</a></td>        
      </tr>
    </table>    
    <input type="hidden" name="id" value="${inrequest?.id?:0}" />
    <input type="hidden" id="save" name="save" value="1" />    
  </g:form>    
  <g:form name="returnToListForm" url="${[controller:'administrators',action:'shome', params:[fromEdit:1]]}">
  </g:form>  
  </body>
</html>
