<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${params}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
<g:if test="${records}">
  <div id="resultList">
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none">
      <thead>
        <tr>
          <g:if test="${inrequest?.type!=5}">
          <th>Код</th>
          </g:if>
          <g:if test="${inrequest?.type!=4}">
          <th>Название</th>
          </g:if><g:else>
          <th>Страна</th>
          </g:else>
          <g:if test="${inrequest?.type!=3 && inrequest?.type!=5}">
          <th>Регион</th>
          </g:if>
          <g:if test="${inrequest?.type==1}">
          <th>Координаты</th>
          <th>Метро</th>
          <th>Опция индексирования</th>
          </g:if>
          <g:if test="${inrequest?.type==4}">
          <th>Направление</th>
          </g:if>
          <g:if test="${inrequest?.type==3}">
          <th>Опция индексирования</th>
          </g:if>
          <g:if test="${inrequest?.type==5}">          
          <th>Город</th>
          <th>Кол.-во ближних объявлений </th>
          <th>Статус</th>
          </g:if>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${records}" status="i" var="record">
        <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
          <g:if test="${inrequest?.type!=5}">
          <td>${record.id}</td>
          </g:if>
          <g:if test="${inrequest?.type==3}">
            <td><g:link action="adressadd" params="${[country_id:record.id,type:3]}">${record.name}</g:link></td>
          </g:if>          
          <g:elseif test="${inrequest?.type==1}">
            <td><g:link action="adressadd" params="${[city_id:record.id,type:1]}">${record.name}</g:link></td>
          </g:elseif>
          <g:elseif test="${inrequest?.type==5}">
            <td><g:link action="adressadd" params="${[id:record.id,type:5]}">${record.name}</g:link></td>
          </g:elseif>         
          <g:else>
            <td>${record.name}</td>
          </g:else>
          <g:if test="${inrequest?.type==4}">
            <td><g:link action="adressadd" params="${[region_id:record.id,type:4]}">${record.region}</g:link></td>
          </g:if>
          <g:elseif test="${inrequest?.type!=3 && inrequest?.type!=5}">
            <td>${record.region}</td>
          </g:elseif>
          <g:if test="${inrequest?.type==1}">
            <td>${(City.get(record?.id)?.x?:0)?'есть':'нет'}</td>
            <td>${(City.get(record?.id)?.is_metro?:0)?'есть':'нет'}</td>
            <td>${(City.get(record?.id)?.is_index?:0)?'Вкл':'Выкл'}</td>
          </g:if>
          <g:if test="${inrequest?.type==4}">
            <td>${Popdirection.get(record.district)?.name2?:''}</td>          
          </g:if>
          <g:if test="${inrequest?.type==3}">
            <td>${Country.get(record?.id?:0)?.is_index?'Вкл':'Выкл'}</td>
          </g:if>
          <g:if test="${inrequest?.type==5}">
            <td>${City.get(record?.city_id?:0)?.name?:''}</td>
            <td>${record.homecount?:0}</td>
            <td>${((record.modstatus?:0)==0)?'не активно':'активно'}</td>
          </g:if>           
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px 20px">
    <span style="float:left">Найдено: ${count}</span>
    <span style="float:right">
      <g:paginate controller="administrators" prev="&lt;&lt;" next="&gt;&gt;"
        action="${actionName}" max="20" params="${params}"
        total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
