<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <style type="text/css">.glossy td { font-size: 15px; }</style>    
    <g:javascript>
  	function initialize(){
      $('user_submit_button').click();
    }	  
	  function clickPaginate(event){
      event.stop();
      var link = event.element();
      if(link.href == null)
        return;        
      new Ajax.Updater(
        { success: $('ajax_wrap') },
        link.href,
        { evalScripts: true });		  
    }
    function resetData(){     
      $('modstatus').selectedIndex = 0;     
      $('city_id').selectedIndex = 0;
      $('type_id').selectedIndex = 0;      
    }       
    </g:javascript>  
  </head>  

	<body onload="initialize()">
  
    <div id="homelist">
      <g:formRemote name="allForm" url="[action:'shomelist']" update="[success:'AJAXlist']">
      <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">                
        <tr>
          <td>            
            <span>
              <label for="region_id">Город:</label>
              <select name="city_id" id="city_id">
                <option value=""></option>
                <g:each in="${city}" var="item">
                  <option value="${item.id}" <g:if test="${item.id==inrequest?.city_id}">selected="selected"</g:if>>${item.name}</option>
                </g:each>
              </select>				
            </span>            
          </td>  
          <td>            
            <span>
              <label for="region_id">Тип:</label>              
              <g:select name="type_id" from="${shometype}" value="${inrequest?.type_id?:0}" noSelection="${[0:'']}" optionKey="id" optionValue="name" />                                               				
            </span>            
          </td>          
          <td>
            <span>
              <label for="modstatus">Статус публикации:</label>
              <select id="modstatus" name="modstatus">
                <option value=""></option>               
                <option value="1" <g:if test="${1==inrequest?.modstatus}">selected="selected"</g:if>>активный</option>
                <option value="0" <g:if test="${0==inrequest?.modstatus}">selected="selected"</g:if>>неактивный</option>               
              </select>
            </span>         
          </td>        
          <tr>
            <td colspan="3" align="right" nowrap>
              <g:link action="shomeedit" class="button-glossy orange">Добавить</g:link>
              <input type="submit" class="button-glossy green" id="user_submit_button" value="Показать" style="margin-right:10px">
              <input type="button" class="button-glossy grey" value="Сброс" onClick="resetData()"/>
            </td>
          </tr>
        </tr>         
      </table>
      </g:formRemote>
    </div>

    <div id="AJAXlist"></div>    
  
  </body>
</html>
