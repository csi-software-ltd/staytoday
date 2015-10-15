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
      function initialize(){
        $('form_submit_button').click();
      }      
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null){
          return;
        }    
        new Ajax.Updater(
          { success: $('ajax_wrap') },
          link.href,
          { evalScripts: true });		  
      }      
      function allocate(){
        if (confirm('Распределить все новые?')){
          <g:remoteFunction controller='administrators' action='allocateAllNewProposal' onSuccess="\$('form_submit_button').click();"/>
        }
      }        
      function resetData(){
        $('z_id').value='';
        $('city').value='';
        $('modstatus').selectedIndex = 0;
        $('country_id').selectedIndex = 0;
        $('region_id').selectedIndex = 0;
        $("is_auto").checked=false;
      }  
      function closeZayavka(lId){
        if (confirm('Закрыть заявку и отправить письмо пользователю?')){
          <g:remoteFunction controller='administrators' action='closeproposal' onSuccess="\$('form_submit_button').click();" params="'id='+lId" />
        }
      }
    </g:javascript>
  </head>
	<body onload="initialize()">  
    <div id="homelist">
      <g:formRemote name="allForm" url="[action:'proposallist']" update="[success:'companystat']">
        <input type="hidden" name="stat" id="stat" value="direction">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:20px 5px">
          <tr>
            <td colspan="2" nowrap>
              <span nowrap>
                <label for="z_id">Код заявки:</label>
                <input type="text" id="z_id" name="id" value="${inrequest?.id}" style="width:80px">
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="city">Город:</label>
                <input type="text" id="city" name="city" value="${inrequest?.city}" style="width:170px">
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="country_id">Страна:</label>
                <select name="country_id" id="country_id">
                  <option value=""></option>
                  <g:each in="${country}" var="item">
                    <option value="${item.id}" <g:if test="${item.id==inrequest?.country_id}">selected="selected"</g:if>>${item.name}</option>
                  </g:each>
                </select>
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="region_id">Регион:</label>
                <select name="region_id" id="region_id" style="width:250px">
                  <option value=""></option>
                  <g:each in="${region}" var="item">
                    <option value="${item.id}" <g:if test="${item.id==inrequest?.region_id}">selected="selected"</g:if>>${item.name}</option>
                  </g:each>
                </select>				
              </span>            
            </td>
          </tr>
          <tr>
            <td nowrap>
              <label for="modstatus">Статус заявки:</label>
              <select id="modstatus" name="modstatus">
                <option value="-5" <g:if test="${inrequest?.modstatus==-5}">selected="selected"</g:if>>все</option>
                <option value="0" <g:if test="${!inrequest?.modstatus}">selected="selected"</g:if>>новая</option>
                <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>публикация</option>
                <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>не подтвержден email</option>
                <option value="-2" <g:if test="${inrequest?.modstatus==-2}">selected="selected"</g:if>>спам/некорректная</option>
                <option value="-3" <g:if test="${inrequest?.modstatus==-3}">selected="selected"</g:if>>просроченная</option>
                <option value="-4" <g:if test="${inrequest?.modstatus==-4}">selected="selected"</g:if>>закрытая</option>
              </select>
              <span nowrap>
                <input type="checkbox" id="is_auto" name="is_auto" <g:if test="${inrequest?.is_auto}">checked="checked"</g:if> value="1"/>
                <label for="is_auto">Авто</label>
              </span>
            </td>
            <td align="right" nowrap>
              <input type="submit" class="button-glossy green" id="form_submit_button" value="Показать"/>
              <input type="button" class="button-glossy grey" value="Сброс" onclick="resetData()" style="margin:0 10px"/>
              <input type="button" class="button-glossy lightblue" value="Распределить новые" style="margin-right:10px" onclick="allocate()">
            </td>
          </tr>
        </table>   
      </g:formRemote>        
      <div id="companystat"></div>
    </div>
  </body>
</html>
