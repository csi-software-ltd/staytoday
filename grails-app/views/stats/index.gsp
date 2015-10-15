<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript>
    function initialize(iParam){
      $('statref_id').show();
      $('keyword').show();
      $('succeed').show();
      $('time').show();
      $('code').show();
      $('output').show();
      $('output').update('<label for="output">Статистика по:</label> <select name="output"><option value="0" <g:if test="${0==inrequest?.output}">SELECTED</g:if>>Просмотрам</option><option value="1" <g:if test="${1==inrequest?.output}">SELECTED</g:if>>Редактированию</option><option value="2" <g:if test="${2==inrequest?.output}">SELECTED</g:if>>Предложениям дня</option><option value="3" <g:if test="${3==inrequest?.output}">SELECTED</g:if>>Выводам в листинге</option><option value="4" <g:if test="${4==inrequest?.output}">selected="selected"</g:if>>Кол-ву контактов</option></select>');
      $('time_select').update('<label for="time">Период:</label> <select name="time"><option value="0">Весь</option><option value="1">Год</option><option value="2">Месяц</option><option value="3">2а месяца</option><option value="4">Неделя</option></select>');
      switch(iParam){
        case 0:
          sectionColor('keywrds');
          $('statref_id').hide();
          $('code').hide();
          $('output').hide();
          $('time_select').update('<label for="time">Период:</label> <select name="time"><option value="0">Весь</option><option value="1">Год</option><option value="2">Месяц</option><option value="3">2а месяца</option></select>');
          $('stat').value = 'keywords';
          break;
        case 1:
          sectionColor('section');
          $('keyword').hide();
          $('succeed').hide();
          $('code').hide();
          $('output').hide();
          $('stat').value = 'section';
          break;
        case 2:
          sectionColor('service');
          $('keyword').hide();
          $('succeed').hide();
          $('statref_id').hide();
          $('code').hide();
          $('output').hide();
          $('stat').value = 'service';
          break;
        case 3:
          sectionColor('home');
          $('keyword').hide();
          $('succeed').hide();
          $('stat').value = 'home';
          break;
        case 4:
          sectionColor('prop');
          $('keyword').hide();
          $('succeed').hide();
          $('output').update('<label for="output">Статистика по:</label> <select name="output"><option value="0" <g:if test="${0==inrequest?.output}">SELECTED</g:if>>Профилю</option><option value="1" <g:if test="${1==inrequest?.output}">SELECTED</g:if>>Валюте</option><option value="2" <g:if test="${2==inrequest?.output}">SELECTED</g:if>>Популярным направлениям</option><option value="3" <g:if test="${3==inrequest?.output}">SELECTED</g:if>>Страницам скидок</option><option value="4" <g:if test="${4==inrequest?.output}">SELECTED</g:if>>Странам</option><option value="5" <g:if test="${5==inrequest?.output}">SELECTED</g:if>>Статьям</option><option value="6" <g:if test="${6==inrequest?.output}">SELECTED</g:if>>Тегам статей</option></select>');
          $('stat').value = 'prop';
          break;
        }
        $('stat_submit_button').click();
      }
      
      function sectionColor(sSection){
        $('keywrds').style.color = 'black';
        $('section').style.color = 'black';
        $('service').style.color = 'black';
        $('home').style.color = 'black';
        $('prop').style.color = 'black';
        $(sSection).style.color = '#0080F0';
      }
    </g:javascript>	
  </head>  
  <body onload="initialize(${type});">
    <div align="center">
      <table cellpadding="5">
        <tr>
          <td nowrap><a href="javascript:void(0)" onclick="initialize(0)" id="keywrds"><h3><u>Ключевые слова</u></h3></a></td>
          <td><a href="javascript:void(0)" onclick="initialize(1)" id="section"><h3><u>Разделы</u></h3></a></td>
          <td><a href="javascript:void(0)" onclick="initialize(2)" id="service"><h3><u>Сервисы</u></h3></a></td>
          <td><a href="javascript:void(0)" onclick="initialize(3)" id="home"><h3><u>Объявления</u></h3></a></td>
          <td><a href="javascript:void(0)" onclick="initialize(4)" id="prop"><h3><u>Разное</u></h3></a></td>
          <td><g:link style="color:black" controller="stats" action="robots"><h3><u>Роботы</u></h3></g:link></td>
        </tr>
      </table>
    </div>
    <div id="homestat"></div>
    <div id="sitestat">
      <div>
        <g:formRemote name="allForm" url="[action:'liststats']" update="[success:'placeList']">
          <input type="hidden" name="stat" id="stat" value="direction">	  
          <table width="100%" cellpadding="5" cellspacing="0" border="0" class="glossy norounded">
            <tr>
              <td nowrap>
                <span id="statref_id">
                  <label for="statref_id">Откуда:</label> 
                  <select name="statref_id">
                    <option value="0" ></option> 
                    <g:each in="${statrefs}" var="statref">
                      <option value="${statref.id}" >${statref.name}</option>
                    </g:each>
                  </select>&nbsp;&nbsp;
                </span>
                <span id="keyword">
                  <label for="keyword">Ключевое слово:</label> 
                  <input type="text" name="keyword" style="width:200px">&nbsp;&nbsp;&nbsp;&nbsp;
                </span>
                <span id="succeed">
                  <label for="succeed">Успешность:</label> 
                  <select name="succeed">
                    <option value="1">да</option>
                    <option value="0">нет</option>
                  </select>&nbsp;&nbsp;&nbsp;&nbsp;
                </span>
                <span id="time">
                  <span id="time_select">
                    <label for="time">Период:</label> 
                    <select name="time">
                      <option value="0">Весь</option>
                      <option value="1">Год</option>
                      <option value="2">Месяц</option>
                      <option value="3">2а месяца</option>  
                      <option value="4">Неделя</option>
                    </select>
                  </span>&nbsp;&nbsp;&nbsp;&nbsp;
                </span>
                <span id="code">
                  <label for="code">Код записи:</label>
                  <input type="text" name="code" style="width:115px" value="${inrequest?.code}"/>&nbsp;&nbsp;&nbsp;&nbsp;              
                </span>
                <span id="output">
                  <label for="output">Статистика по:</label>
                  <select name="output">
                    <option value="0" <g:if test="${0==inrequest?.output}">selected="selected"</g:if>>Просмотрам</option>
                    <option value="1" <g:if test="${1==inrequest?.output}">selected="selected"</g:if>>Редактированию</option>
                    <option value="2" <g:if test="${2==inrequest?.output}">selected="selected"</g:if>>Предложениям дня</option>
                    <option value="3" <g:if test="${3==inrequest?.output}">selected="selected"</g:if>>Выводам в листинге</option>
                    <option value="4" <g:if test="${4==inrequest?.output}">selected="selected"</g:if>>Кол-ву контактов</option>
                  </select>
                </span>
              </td>
            </tr>
            <tr>
              <td align="right" nowrap>
                <span id="lang_id">
                  <label for="lang_id">Язык:</label>
                  <select name="lang_id">
                    <option value="0">все</option>
                    <option value="1">en</option>
                    <option value="2">ru</option>
                  </select>&nbsp;&nbsp;&nbsp;&nbsp;
                </span>
                <span id="site_id">
                  <label for="site_id">Тип версии:</label>
                  <select name="site_id">
                    <option value="0">все</option>
                    <option value="1">web</option>
                    <option value="2">mobile</option>
                  </select>&nbsp;&nbsp;&nbsp;&nbsp;
                </span>
                <input type="submit" class="button-glossy green" id="stat_submit_button" value="Показать" style="margin-right:10px">
                <input type="reset" class="button-glossy grey" value="Сброс"/>
              </td>
            </tr>
          </table>
        </g:formRemote>
      </div>
      <div id="placeList" align="left">			
      </div>
    </div>
  </body>
</html>
