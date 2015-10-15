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
      function initialize(iParam){
        switch(iParam){
          case 0:
            sectionColor('template');
            $('mailing_templatelist').show();
            $('mailing_historylist').hide();          
            $('submit_button').click();
            $('data_list').setStyle({height: '550px'}); 
            break;
          case 1:
            sectionColor('mail_history');
            $('mailing_templatelist').hide();
            $('mailing_historylist').show();
            $('mail_history_submit_button').click();
            $('data_list').setStyle({height: '590px'}); 
            break;
          case 2:
            sectionColor('task_history');
            $('mailing_templatelist').hide();
            $('mailing_historylist').hide();
            $('mailing_tasklist').show();
            $('mail_task_submit_button').click();
            $('data_list').setStyle({height: '680px'}); 
            break;  
        }
      }
      function sectionColor(sSection){
        $('template').style.color = 'black';
        $('mail_history').style.color = 'black';
        $('task_history').style.color = 'black';
        $(sSection).style.color = '#0080F0';
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
    </g:javascript>
  </head>
  <body onload="initialize(${type?:0})">
    <div align="center">
      <table cellpadding="5">
        <tr>
          <td nowrap><a href="javascript:void(0)" onclick="initialize(0)" id="template"><h3><u>Шаблоны писем</u></h3></a></td>
          <td nowrap><a href="javascript:void(0)" onclick="initialize(1)" id="mail_history"><h3><u>История писем</u></h3></a></td>
          <td nowrap><a href="javascript:void(0)" onclick="initialize(2)" id="task_history"><h3><u>История рассылок</u></h3></a></td>
        </tr>
      </table>
    </div>  
    <div id="mailing_templatelist">
      <g:formRemote name="allForm" url="[action:'mailing_templatelist']" update="[success:'data_list']">	
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded" style="padding:20px 5px">
          <tr>
            <td nowrap>
              <span nowrap>
                <label for="id">Код:</label>
                <input type="text" id="template_id" name="template_id" value="${inrequest?.template_id?:''}" style="width:100px">                    
              </span>&nbsp;&nbsp;  
              <span nowrap>
                <label for="name">Название:</label>
                <input type="text" id="template_name" name="name" value="${inrequest?.template_name?:''}" style="width:150px">
              </span>&nbsp;&nbsp;  
              <span nowrap>            
                <label for="type">Тип:</label>
                <select name="type_id">
                  <option value="0" selected/>                  
                  <option value="1" <g:if test="${inrequest?.type_id==1}">selected="selected"</g:if>>email</option>
                  <option value="2" <g:if test="${inrequest?.type_id==2}">selected="selected"</g:if>>sms</option>				  
                </select>			  
              </span>&nbsp;&nbsp;
              <span nowrap>            
                <label for="type">Статус:</label>
                <select name="modstatus">
                  <option value="-1" selected/>
                  <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>неактивный</option>                  
                  <option value="1" <g:if test="${inrequest?.modstatus==1}">selected="selected"</g:if>>активный</option>                 
                </select>			  
              </span>				
            </td>
          </tr>
          <tr>
            <td align="right" nowrap>
              <g:link controller="administrators" action="mailing_templateadd" class="button-glossy orange">Добавить шаблон</g:link>
              <input type="submit" class="button-glossy green" id="submit_button" value="Показать" style="margin:0 10px" />
              <input type="reset" class="button-glossy grey" value="Сброс" />
            </td>
          </tr>
        </table>
      </g:formRemote>     
    </div>
    <div id="mailing_historylist" align="center">
      <g:formRemote name="allForm" url="[action:'mailing_historylist']" update="[success:'data_list']">
        <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
          <tr>
            <td nowrap>
              <span nowrap>
                <label for="inf_action">Название:</label>
                <input type="text" id="mh_name" name="name" value="" style="width:200px">         
              </span>&nbsp;&nbsp;
              <span nowrap>
                <label for="inf_action">Контакт:</label>
                <input type="text" id="mh_contact" name="contact" value="" style="width:200px">
              </span>
            </td>
            <td align="right" nowrap>            
              <input type="submit" class="button-glossy green" id="mail_history_submit_button" value="Показать" style="margin:0 10px">
              <input type="reset" class="button-glossy grey" value="Сброс"/>
            </td>
          </tr>
        </table>   
      </g:formRemote>
    </div>
    <div id="mailing_tasklist" align="center" style="display:none">
      <g:formRemote name="allForm" url="[action:'task_list']" update="[success:'data_list']"> 
        <div style="display:none">	  
          <input type="submit" class="button-glossy green" id="mail_task_submit_button" value="Показать" />                    
        </div>	
      </g:formRemote>
    </div>    
    <div id="data_list"></div>
  </body>
</html>
