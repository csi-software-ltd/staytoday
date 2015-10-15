<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <style type="text/css">
      @media all and (-webkit-min-device-pixel-ratio:10000),not all and (-webkit-min-device-pixel-ratio:0) {
        .glossy td { font-size: 15px; }
      }
    </style>  
    <g:javascript>
      function startAnonsSearch(){
        $('submit_button').click();
      }                     
    </g:javascript>
    <g:setupObserve function='clickPaginate' id='ajax_wrap'/>
  </head>  
  <body onload="startAnonsSearch();">
    <div>    
      <g:formRemote name="recordForm" url="[action:'anonslist']" update="[success:'anons_list']">
        <input type="hidden" name='sort' value='0' id='sort'>
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
          <tr>
            <td colspan="2" nowrap>              
              <span nowrap>
                <label for="advbanner_id">Код:</label>
                <input type="text" style="width:75px" name="anons_id" value="${inrequest?.anons_id}">
              </span>&nbsp;&nbsp;             
              <span nowrap>
                <label for="modstatus">Статус:</label>
                <select name="modstatus">
                  <option value="-1"></option>
                  <option value="0" <g:if test="${0==inrequest?.modstatus}">selected</g:if>>неактивное</option>
                  <option value="1" <g:if test="${1==inrequest?.modstatus}">selected</g:if>>активное</option>                  
                </select>
              </span>&nbsp;&nbsp;              
            </td>                              
            <td align="right" nowrap>
              <g:link class="button-glossy orange" action="anonsDetail" params="${inrequest}">Добавить анонс</g:link>
              <input type="submit" id="submit_button" class="button-glossy green" value="Найти" style="margin:0 10px;"/>
              <input type="reset" class="button-glossy grey" value="Сброс" onclick="$('submit_reset').click();return true;">
            </td>     
          </tr>
        </table>        
      </g:formRemote>
      <g:formRemote name="resetForm" url="[action:'anonslist']" update="[success:'anons_list']">
        <input type="submit" id="submit_reset" style="display:none;">
      </g:formRemote>
      <div id="anons_list">        
      </div>
    </div>
  </body>
</html>
        
