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
    </g:javascript>
  </head>
  
  <body onload="startAdminSearch();">
    <div> 
      <g:formRemote name="recordForm" url="[controller:'administrators', action:'notetypelist']" update="[success:'tableAjax']">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
          <tr>
            <td nowrap>
              <span nowrap>
                <label for="id">Код:</label>
                <input type="text" class="price" name="id" value=""/>
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="email_template_id">Шаблон email:</label>
          		  <select name="email_template_id">
                  <option value="0"></option>
                  <g:each in="${email_template}">
                    <option value="${it?.id}" <g:if test="${inrequest?.email_template_id==it?.id}">selected="selected"</g:if>>${it?.name}</option>
                  </g:each>
                </select>
              </span>
            </td>
          </tr>
          <tr>
            <td align="right" nowrap>
              <input type="submit" class="button-glossy green" id="submit_button" value="Найти" style="margin-right:10px;"/>
              <input type="reset" class="button-glossy grey" value="Сброс"/>
            </td>        
          </tr>      
        </table>
      </g:formRemote>
      <div id="tableAjax">   
      </div>
    </div>
  </body>
</html>
