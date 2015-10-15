<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'qtip.css')}" />
    <g:javascript library="jquery-1.8.3" />
    <g:javascript library="jquery.colorbox.min" />
    <g:javascript library="jquery.qtip.min" />    
    <g:javascript>  
      function initialize(){  
      <g:if test="${(type?:0)!=1}">
        textCounter('mtext','mtext_limit');
        alt();
      </g:if>
      }
      function textCounter(sId,sLimId){
        var symbols = $F(sId);
        var len = symbols.length;      
        $(sLimId).value = len;
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function alt(){
        jQuery('a[title]').qtip({        
          position: { my: 'bottom center', at: 'top center' },
          show: 'mouseover',
          hide: 'mouseout',
          style: { classes: 'ui-tooltip-shadow ui-tooltip-' + 'plain' }
        });      
      }
      function delQueue(lId){
        <g:remoteFunction controller='administrators' action='delqueue' onSuccess='location.reload(true)' params="\'template_id=\'+lId" />
      }
      function delQueueElem(tempId,lId){
        <g:remoteFunction controller='administrators' action='delqueueelem' onSuccess='location.reload(true)' params="'template_id='+tempId+'&id='+lId" />
      }
      function addQueueElem(){	      
        jQuery('#addqueue').colorbox({
          inline: true, 
          href: '#addqueue_lbox',
          scrolling: false,
          onLoad: function(){
            jQuery('#addqueue_lbox').show();          
          },
          onCleanup: function(){
            jQuery('#addqueue_lbox').hide();            
          }        
        }); 
        
        $('addqueue').show();
      }
    </g:javascript>
  </head>  
  <body onload="initialize()">
  <g:if test="${flash?.save_error}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <g:if test="${(flash?.save_error?:[]).contains(1)}"><li>Поле &laquo;Название&raquo; обязательно для заполнения.</li></g:if>
        <g:if test="${(flash?.save_error?:[]).contains(2)}"><li>Поле &laquo;Текст&raquo; обязательно для заполнения.</li></g:if>		
        <g:if test="${(flash?.save_error?:[]).contains(101)}"><li>Ошибка. Данные не сохранены.</li></g:if>
      </ul>
    </div>
  </g:if>
  <g:form name="infotexteditForm" url="[controller:'administrators',action:'mailing_templateedit', id:inrequest.id]" method="POST">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td colspan="3"><a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку шаблонов</a></td>        
      </tr>     
      <tr>
        <td style="width:100px">Название:</td>
        <td colspan="2"><input type="text" name="name" style="width:100%" value="${inrequest?.name?:''}"/></td> 
      </tr>
    <g:if test="${(type?:0)==1}">
      <tr>
        <td style="width:100px">Заголовок:</td>
        <td colspan="2"><input type="text" name="header" style="width:100%" value="${inrequest?.header?:''}"/></td> 
      </tr>
    </g:if>	  
      <tr>
        <td>Текст: 
          <g:if test="${(type?:0)!=1}"><a href="javascript:void(0)" title="Одно sms не более 70 символов">
            <img hspace="5" border="0" valign="bottom" alt="Одно sms не более 70 символов" src="${resource(dir:'images',file:'question.png')}">
          </a></g:if>
        </td>
        <td colspan="2">
        <g:if test="${(type?:0)==1}">
          <fckeditor:editor name="mtext_fck" width="100%" height="300" toolbar="ARENDA" fileBrowser="default">
            <g:rawHtml>${inrequest?.mtext}</g:rawHtml>
          </fckeditor:editor> 
        </g:if><g:else>
          <g:textArea name="mtext" value="${inrequest?.mtext}" rows="5" cols="40" style="width:99%" onKeyDown="textCounter(this.id,'mtext_limit');" onKeyUp="textCounter(this.id,'mtext_limit');" />
          <small style="white-space:nowrap"><i>Набрано символов:&nbsp;<input type="text" class="cursive" readonly id="mtext_limit" name="mtext_limit"/></i></small>		  
        </g:else>
        </td>
      </tr>
      <tr>
        <td>Статус:</td>
        <td>			 
          <select name="modstatus">            
            <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>неактивен</option>                  
            <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>активен</option>                 
          </select>			  		
        </td>      
        <td>Рассылать с дизайн шаблоном: 
          <input type="checkbox" name="is_destemp" value="1" <g:if test="${inrequest?.is_destemp}">checked</g:if>/>
        </td>
      </tr>
      <tr>
        <td colspan="2"></td>          
        <td align="right">
          <input type="submit" class="button-glossy green" value="Сохранить"/>
        </td>
      </tr>
      <input type="hidden" id="save" name="save" value="1" />
      <input type="hidden" id="type" name="type" value="${type?:0}" />
    </g:form> 	 
      <tr>
        <td colspan="3">
          <g:form name="saveFileForm" url="[controller:'administrators',action:'csvfileadd']" method="POST" enctype="multipart/form-data">
            <input type="file" name="file" />
            <input type="hidden" id="type" name="type" value="${type?:0}" />
            <input type="hidden" name="template_id" value="${mt?.id}"/>			
            <input type="submit" class="button-glossy lightblue mini" value="Загрузить csv файл"/>
          </g:form>
        </td>
      </tr>	  
      <tr>
        <td colspan="3"><a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку шаблонов</a></td>        
      </tr>
      <tr>
        <td colspan="3">
          <a href="javascript:void(0)" onclick="delQueue(${inrequest?.id?:0})" class="button-glossy red mini">Удалить всю очередь</a>
          <a id="addqueue" href="javascript:void(0)" onclick="addQueueElem()" class="button-glossy green mini">Добавить запись в очередь</a>
          <g:remoteLink class="button-glossy orange mini" url="${[action:'addqueueelemlist',params:[id:inrequest?.id?:0,type:1]]}" onSuccess="location.reload(true)">Новости для владельцев</g:remoteLink>
          <g:remoteLink class="button-glossy orange mini" action="addqueueelemlist" params="[id:inrequest?.id?:0,type:0]" onSuccess="location.reload(true)">Новости для пользователей</g:remoteLink>
        </td>
      </tr>
      <tr>
        <td colspan="3" valign="top">
          <div style="height:300px;overflow-y:auto;">
            <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
              <tr> 
                <th>Код</th>		
                <th>Имя контакта</th>	          
                <th>Контакт</th>
                <th width="60" style="border-right: 1px solid #333">Действия</th>			
              </tr>        
            <g:each in="${queue}" status="i" var="record">
              <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td>${record.id}</td>
                <td>${record.name?:''}</td>          
                <td>${record.contact?:''}</td>
                <td>
                  <div class="actions">
                    <span class="action_button anowrap">
                      <a class="icon delete" href="javascript:void(0)" onclick="delQueueElem(${inrequest?.id?:0},${record?.id?:0});" title="Удалить"></a>
                    </span>
                  </div>
                </td>
              </tr>
            </g:each>
            </table>
          </div>
          <g:form name="returnToListForm" url="${[controller:'administrators',action:'mail', params:[fromEdit:1]]}">
          </g:form> 
        </td>
      </tr>
    </table>
    
    
    <div id="addqueue_lbox" class="new-modal" style="display:none">
      <h2 class="clearfix">Добавление новой записи в очередь</h2>
      <g:formRemote url="[controller:'administrators',action:'addqueueelem']" onSuccess="location.reload(true)" method="POST" name="addForm">
      <div class="segment nopadding">
        <div class="lightbox_filter_container" style="height:100px">                    
          <table width="100%" cellpadding="5" cellspacing="0" border="0">
            <tr>
              <td nowrap>Имя контакта:</td>    
              <td><input type="text" name="name" id="add_name" style="width:100%;"/></td>    
            </tr>
            <tr>
              <td>Контакт:</td>    
              <td><input type="text" name="contact" id="addcontact" style="width:100%;"/></td>
            </tr>
          </table>      
        </div>
      </div>
      <div class="segment buttons">
        <input type="submit" class="button-glossy green mini" value="Добавить" />
        <input type="button" class="button-glossy grey mini" value="Отмена" onclick="jQuery.colorbox.close()" />
        <input type="hidden" name="template_id" value="${inrequest?.id?:0}" />
      </div>
      </g:formRemote>
    </div>
    
  </body>
</html>
