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
      function startBannerSearch(){
        $('submit_button').click();
      }
      
      function fsort(iVal){
        $('sort').value=iVal;
        startBannerSearch();
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
    <g:setupObserve function='clickPaginate' id='ajax_wrap'/>
  </head>  
  <body onload="startBannerSearch();">
    <div>    
      <g:formRemote name="recordForm" url="[action:'listbanners']" update="[success:'companystat']">
        <input type="hidden" name='sort' value='0' id='sort'>
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
          <tr>
            <td colspan="2" nowrap>              
              <span nowrap>
                <label for="advbanner_id">Код:</label>
                <input type="text" style="width:75px" name="advbanner_id" value="${inrequest?.advbanner_id}">
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="btype">Тип баннера:</label>
                <select name="btype">
                  <option value="0" <g:if test="${0==inrequest?.btype}">selected</g:if>></option>
                  <g:each in="${advbannertypes}" var="type">
                    <option value="${type.id}"  <g:if test="${type.id==inrequest?.btype}">selected</g:if>>${type.name}</option>
                  </g:each>
                </select>
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="modstatus">Статус баннера:</label>
                <select name="modstatus">
                  <option value="0" <g:if test="${0==inrequest?.modstatus}">selected</g:if>></option>
                  <option value="1" <g:if test="${1==inrequest?.modstatus}">selected</g:if>>публикация</option>
                  <option value="2" <g:if test="${2==inrequest?.modstatus}">selected</g:if>>неактивен</option>
                </select>
              </span>&nbsp;&nbsp; 
              <span nowrap>
                <label for="datefrom">Период с:</label>
                <calendar:datePicker name="datefrom" needDisable="false" dateFormat="%d-%m-%Y"  value="${datefrom}"/>
                <label for="dateto">&nbsp;по:</label>
                <calendar:datePicker name="dateto"  needDisable="false" dateFormat="%d-%m-%Y" value="${dateto}"/>
              </span>
            </td>
          </tr>
          <tr>
            <td nowrap>
              <label for="order">Сортировать по:</label> 
              <select name="order">
                <option value="0" onclick="fsort(0);return false;">дате начала</option>
                <option value="1" onclick="fsort(1);return false;">дате окончания</option>
              </select>
            </td>
            <td align="right" nowrap>
              <g:link class="button-glossy orange" action="editbanners" params="${inrequest}">Добавить баннер</g:link>
              <input type="submit" id="submit_button" class="button-glossy green" value="Найти" style="margin:0 10px;"/>
              <input type="reset" class="button-glossy grey" value="Сброс" onclick="$('submit_reset').click();resetDate();return true;">
            </td>     
          </tr>
        </table>        
      </g:formRemote>
      <g:formRemote name="resetForm" url="[action:'listbanners']" update="[success:'companystat']">
        <input type="submit" id="submit_reset" style="display:none;">
      </g:formRemote>
      <div id="companystat">        
      </div>
    </div>
  </body>
</html>
        
