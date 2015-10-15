<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin"/>
    <g:javascript>
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null){
          return;
        }    
        new Ajax.Updater(
          { success: $('ajax_wrap') },
          link.href,
          { evalScripts: true }
        );
      }        
      function startAdminSearch(){
        $('submit_button').click();
      }        
      function deleteMessage(lId){
        if (confirm('Вы уверены?')){
          $('gbDeleteForm'+lId).submit();
        }
      }      
      function changeStatus(lId,status){
        <g:remoteFunction controller='administrators' action='gbchangestatus' onSuccess='startAdminSearch()' params="'id='+lId+'&status='+status" />
      }      
      function resetDate(){
        $('datefrom').setValue('');
        $('dateto').setValue('');		 
        $('datefrom_year').setValue('');
        $('dateto_year').setValue('');
        $('datefrom_month').setValue('');
        $('dateto_month').setValue('');
        $('datefrom_day').setValue('');
        $('dateto_day').setValue('');	  
      } 
    </g:javascript>
  </head>  
  <body onload="startAdminSearch();">
    <div align="center"> 
      <g:formRemote name="recordForm" url="[controller:'administrators', action:'guestbookrecords']" update="[success:'tableAjax']">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
          <tr>
            <td nowrap>
              <span nowrap>
                <label for="type">Тип сообщения:</label>
                <select name="type">
                  <option value="0" selected/>
                  <g:each in="${gbtype}">	
                    <option value="${it.id}" <g:if test="${it.id==inrequest?.types}">SELECTED</g:if>>${((it.name?:'').size()>30)?it.name.substring(0,30):it.name}</option>		  
                  </g:each>
                </select>
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="datefrom">Период с:</label>
                <calendar:datePicker name="datefrom" needDisable="false" value="${datefrom}" dateFormat="%d-%m-%Y"/>
                <label for="dateto">&nbsp;по:</label>
                <calendar:datePicker name="dateto" needDisable="false" value="${dateto}" dateFormat="%d-%m-%Y"/>              
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="status">Статус:</label>
                <select name="status">
                  <option value="0" <g:if test="${(!inrequest?.status)}">selected="selected"</g:if>>непрочитанные</option>
                  <option value="1" <g:if test="${inrequest?.status==1}">selected="selected"</g:if>>прочитанные</option>
                </select>            
              </span>
            </td>
          </tr>
          <tr>
            <td align="right" nowrap>
              <input type="submit" class="button-glossy green" id="submit_button" value="Найти" style="margin-right:10px">
              <input type="reset" class="button-glossy grey" value="Сброс" onclick="resetDate();return true;"/>
            </td>        
          </tr>          
        </table>
      </g:formRemote>
    </div>
    <div id="tableAjax">      
    </div>  
  </body>
</html>
