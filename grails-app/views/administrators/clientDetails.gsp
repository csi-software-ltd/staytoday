<html>
  <head>
  <title>Административное приложение StayToday.ru</title>
  <meta name="layout" content="admin" />
  <g:javascript>  
    function confirmClient(){
      document.getElementById('resstatus').value = '1'
      document.getElementById('submitButton').click()
    }
    function declineClient(){
      document.getElementById('resstatus').value = '-1'
      document.getElementById('submitButton').click()
    }
    function returnToList(){
      $("returnToListForm").submit();
    }
    function deletescan(iNum){
      <g:remoteFunction controller='administrators' action='deletescan' id="${client?.id?:0}" onSuccess='reloadImage(iNum)' params="'type='+iNum" />
    }
    function processResponse(e){
      if (e.responseJSON.error.length) {
        document.getElementById('errContaner').show();
        e.responseJSON.error.indexOf(1)>-1?document.getElementById('err1').show():document.getElementById('err1').hide();
        e.responseJSON.error.indexOf(101)>-1?document.getElementById('err101').show():document.getElementById('err101').hide();
      } else {
        location.reload(true)
      }
    }
    function updatePayaccounts(lCountryId){
      <g:remoteFunction controller='administrators' action='payaccounts' update='payaccounts' params="'countryId='+lCountryId" />
    }
    function reloadImage(iNum){
        $('upload'+iNum).show();
        $('result'+iNum).hide();
    }
    function startSubmit(sName,iNum){
      $(sName).submit();
      $('loader'+iNum).show();
      return true;
    }
    function stopUpload(iNum,sFilename,sThumbname,iErrNo,sMaxWeight) {
      if((iNum<=0)||(iNum>2)) iNum=1;
      $('loader'+iNum).hide();
      if(iErrNo==0){
        $('upload'+iNum).hide();
        $('error'+iNum).hide();
        $('viewscan'+iNum).href+=sFilename;
        $('result'+iNum).show();
      }else{
        var sText="Ошибка загрузки";
        switch(iErrNo){
          case 1: sText="Удивительная ошибка загрузки"; break;
          case 2: sText="Ошибка загрузки"; break;
          case 3: sText="Слишком большой файл. Ограничение "+sMaxWeight+" Мб"; break;
          case 4: sText="Неверный тип файла. Используйте JPG или PNG"; break;
          case 5: sText="Ошибка сохранения в БД"; break;
          case 6: sText="Клиент не найден"; break;
        }
        $('error'+iNum).update(sText);
        $('error'+iNum).show();
      }
      return true;
    }
    function setPayaccount(iId){
      if(iId==1){
        $("settlaccount_div").show();
        $("webmoney_div").hide();
      }else if(iId==3){
        $("settlaccount_div").hide();
        $("webmoney_div").show();
      }else{
        $("settlaccount_div").hide();
        $("webmoney_div").hide();
      }      
    }
    function setForm(iId){
      if(iId!=2){
        $("kpp_div").hide(); 
        $("nds_div").hide();
        $("pcard_div").show();        
      }else{
        $("nds_div").show();
        $("kpp_div").show();
        $("pcard_div").hide();
      }  
    }
    function confirmPartner(){
      <g:remoteFunction controller='administrators' action='setpartnerstatus' id="${client?.id?:0}" onSuccess='location.reload(true)' params="'status=2'" />
    }
    function declinePartner(){
      <g:remoteFunction controller='administrators' action='setpartnerstatus' id="${client?.id?:0}" onSuccess='location.reload(true)' params="'status=-1'" />
    }
	</g:javascript>
  <style type="text/css">    
    .dotted td, .dotted th { padding: 10px 3px !important; font-size: 11px !important; line-height: 12px !important }
    .dotted td.red { color: #FF530D; border-color: #505050 !important }
  </style>
  </head>
  <body>
    <div id="errContaner" class="notice drop_shadow" style="display:none">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
        <li id="err101" style="display:none">Непоправимая ошибка. Данные не сохранены.</li>
        <li id="err1" style="display:none">Если Вы читаете это, то значит я где-то накосячил.</li>
      </ul>
    </div>
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td><a class="to-parent" href="#" onClick="returnToList();">К списку клиентов</a></td>
      </tr>
      <tr>
        <td><h1 class="blue">Клиент &laquo;${client.name}&raquo;</h1></td>
      </tr>
      <tr>
        <td>
          <g:formRemote name="clienteditForm" url="[controller:'administrators',action:'clientsave', id:id]" method="POST" onSuccess="processResponse(e)">
            <table width="100%" cellpadding="5" cellspacing="5" border="0">
              <tr>
                <td nowrap>
                  <label>Код клиента:</label>
                  <input type="text" disabled value="${client.id?:''}" style="width:70px"/>
                </td>
                <td nowrap>
                  <label>Имя:</label>
                  <input type="text" disabled value="${client.name?:''}" style="width:250px" />
                </td>
                <td nowrap>
                  <label>Никнейм:</label>
                  <input type="text" disabled value="${user?.nickname?:''}" style="width:250px" />
                </td>
              </tr>
              <tr>
                <td colspan="3">
                  <span nowrap>
                    <label>Дата последнего изменения:</label>
                    <input type="text" class="data" disabled value="${String.format('%tF %<tT',client?.moddate?:new Date())}"/>
                  </span>&nbsp;&nbsp;
                  <span nowrap>
                    <label>IP:</label>
                    <input type="text" disabled value="${client?.ip?:''}" style="width:140px" />
                  </span>&nbsp;&nbsp;
                  <span nowrap>
                    партнерка: &nbsp; <b id="pstatus"><font color="${client?.partnerstatus in [0,-1]?'red':client?.partnerstatus==2?'green':'gray'}">${client?.partnerstatus==0?'не подключена':client?.partnerstatus==2?'подтверждена':client?.partnerstatus==1?'запрос на подключение':'отклонена'}</font></b>
                  <g:if test="${client.partnerway_id!=1&&client.partnerstatus!=0}">
                    <ul class="verifications-list col" style="float:right">
                    <g:if test="${client.partnerstatus!=2}">
                      <li class="verifications-list-item" style="padding:0;cursor:pointer" title="подтвердить" onclick="confirmPartner()">
                        <div class="verifications-icon"></div>
                      </li>
                    </g:if>
                    <g:if test="${client.partnerstatus!=-1}">
                      <li class="verifications-list-item" style="padding:0;cursor:pointer" title="отклонить" onclick="declinePartner()">
                        <div class="verifications-icon decline"></div>
                      </li>
                    </g:if>
                    </ul>
                  </g:if>
                  </span>
                </td>
              </tr>
              <tr>
                <td>
                  <label for="partnerway_id">Схема партнерки:&nbsp;&nbsp;</label>
                  <g:select name="partnerway_id" from="${Partnerway.list()}" value="${client?.partnerway_id}" noSelection="${[0:'не задана']}" optionKey="id" optionValue="name" disabled="true"/>
                </td>
                <td colspan="2">
                  <label for="prequisite">Реквизиты:</label>
                  <input type="text" id="prequisite" value="${client?.prequisite}" style="width:85%!important" />
                </td>
              </tr>
              <tr>
                <td>
                  <label for="country_id">Страна:&nbsp;&nbsp;</label>
                  <g:select onchange="updatePayaccounts(this.value);" name="country_id" from="${country}" value="${client?.country_id?:0}" noSelection="${[0:'не задана']}" optionKey="id" optionValue="name" />
                </td>
                <td id="payaccounts">
                  <label for="payaccount_id">Тип аккаунта:</label>
                  <g:select name="payaccount_id" from="${paycountry}" value="${client?.payaccount_id?:0}" optionKey="payaccount_id" optionValue="${{Payaccount.get(it.payaccount_id).name}}" style="width:175px" onchange="setPayaccount(this.value)"/>
                </td>
                <td>
                  <div id="webmoney_div" <g:if test="${(client?.payaccount_id?:0)!=3}">style="display:none"</g:if>>
                    <label for="webmoney">Номер кошелька:</label>
                    <input type="text" name="webmoney" value="${client?.webmoney?:''}"/>
                  </div>                
                </td>
              </tr>
              <tr>
                <td>
                  <label for="type_id">Тип:</label>
                  <g:select name="type_id" from="${['Физ. лицо','Юр. лицо','Предприниматель']}" value="${client?.type_id?:0}" keys="${1..3}" onchange="setForm(this.value);"/>
                </td>
                <td colspan="2">
                  <label for="reserve_id">Схема бронирования:</label>
                  <g:select name="reserve_id" from="${reserve}" value="${client?.reserve_id?:0}" noSelection="${[0:'не задана']}" optionKey="id" optionValue="name" />&nbsp;&nbsp;
                  <span nowrap>
                    статус: &nbsp; <b id="status"></b>  
                    <g:javascript>
                      var color, status;
                      switch(${client?.resstatus?:0}){
                        case  1: color='green'; status='подтверждена'; break;
                        case -1: color='red'; status='отклонена'; break;
                        case  2: color='gray'; status='запрос на подключение'; break;
                        case  3: color='gray'; status='запрос на изменение'; break;
                        default: color='red'; status='не подключена';
                      }                                
                      $("status").update('<font color="'+color+'">'+status+'</font>');
                    </g:javascript>                                                                             
                  </span>
                </td>
              </tr>
              <tr>
                <td>
                  <label for="inn">ИНН:</label>
                  <input type="text" name="inn" value="${client?.inn?:''}"/>
                </td>
                <td>
                  <label for="ogrn">ОГРН:</label>
                  <input type="text" name="ogrn" value="${client?.ogrn?:''}"/>
                </td>
                <td>
                  <label for="license">Лицензия:</label>
                  <input type="text" name="license" value="${client?.license?:''}" style="width:245px"/>
                </td>
              </tr>                            
              <tr>               
                <td colspan="3" style="padding:0">                  
                  <div id="settlaccount_div" <g:if test="${(client?.payaccount_id?:0)!=1}">style="display:none"</g:if>>
                    <table class="form" width="100%" cellpadding="5" cellspacing="5" border="0">
                      <tr>
                        <td nowrap>
                          <span nowrap>
                            <label for="payee" class="nopadd">Получатель платежа:</label>
                            <input type="text" id="payee" name="payee" value="${client?.payee?:''}" maxlength="250" style="width:55%"/>
                          </span>&nbsp;&nbsp;
                          <span id="kpp_div" <g:if test="${client?.type_id!=2}">style="display:none"</g:if>>
                            <label for="kpp" class="nopadd">КПП:</label>
                            <input type="text" class="mini" id="kpp" name="kpp" value="${client?.kpp?:''}" maxlength="9" />
                          </span>
                        </td>
                      </tr>
                      <tr>
                        <td nowrap>
                          <span nowrap>
                            <label for="bankname" class="nopadd">Название банка:</label>
                            <input type="text" id="bankname" name="bankname" value="${client?.bankname?:''}" maxlength="250" style="width:59% !important"/>
                          </span>&nbsp;&nbsp;
                          <span nowrap>
                            <label for="bik" class="nopadd">БИК:</label>
                            <input type="text" class="mini" id="bik" name="bik" value="${client?.bik?:''}" maxlength="9" />
                          </span>                          
                        </td>
                      </tr>
                      <tr>
                        <td nowrap>
                          <span nowrap>
                            <label for="corraccount" class="nopadd" style="width:100px">Корр. счет:</label>
                            <input type="text" id="corraccount" name="corraccount" value="${client?.corraccount?:''}" maxlength="20" style="width:230px"/>
                          </span>
                          <span nowrap>
                            <label for="settlaccount" class="nopadd">Расчетный счет:</label>
                            <input type="text" id="settlaccount" name="settlaccount" value="${client?.settlaccount?:''}" maxlength="20" style="width:230px" />
                          </span>&nbsp;&nbsp;
                          <span id="nds_div"  <g:if test="${client?.type_id!=2}">style="display:none"</g:if>>
                            <label for="nds" class="nopadd">Величина НДС:</label>
                            <g:select class="mini" name="nds" from="${Nds.list()}" value="${client?.nds?:0}" optionKey="percent" optionValue="name" />
                          </span>                                
                          <span id="pcard_div" <g:if test="${client?.type_id==2}">style="display:none"</g:if>>
                            <label for="pcard" class="nopadd">Номер карты:</label>
                            <input type="text" id="pcard" name="pcard" value="${client?.pcard?:''}" style="width:150px" />
                          </span>                                     
                       </td>
                      </tr>
                      <tr>
                        <td>
                          <label for="paycomment" class="nopadd" style="line-height:15px">Комментарий к платежу:</label>
                          <textarea id="paycomment" name="paycomment" style="width:99%">${client?.paycomment?:''}</textarea>
                        </td>
                      </tr>
                    </table>
                  </div>
                </td>                
              </tr>
              <tr>
                <td colspan="3">
                  <label for="settlprocedure">Порядок дальнейших расчетов после бронирования на сайте:</label>
                  <textarea rows="5" cols="40" name="settlprocedure" style="width:99%">${client?.settlprocedure}</textarea>
                </td>
              </tr>
              <tr>
                <td colspan="3">
                  <label for="comment">Комментарий:</label>
                  <textarea rows="5" cols="40" name="comment" style="width:99%">${client?.comment}</textarea>
                </td>
              </tr>
            </table>
            <input id="resstatus" type="hidden" name="resstatus" value="0"/>
            <input id="submitButton" type="submit" class="button-glossy green" value="Сохранить" style="display:none"/>
          </g:formRemote>
        </td>
      </tr>
      <tr>
        <td style="padding:0 15px 20px" nowrap="nowrap">
          <fieldset style="float:left;width:45%">
            <legend>Паспорт</legend>
            <div id="upload1" style="${client?.scanpassport?'display:none':''}">              
              <g:form name='ff1' method="post" url="${[action:'savescanpassport',id:client.id]}" enctype="multipart/form-data" target="upload_target">
                <div id="error1" style="display: none;"></div>
                <label for="file1">Загрузить скан паспорта:</label>
                <input type="file" name="file1" size="23" accept="image/jpeg,image/gif" onchange="startSubmit('ff1',1)"/>                
              </g:form>
              <div id="loader1" class="spinner">
                <img src="${resource(dir:'images',file:'spinner.gif')}" border="0" />
              </div>
              <iframe id="upload_target" name="upload_target" src="#" style="width:0;height:0;border:0px solid #fff;"></iframe>
            </div>
            <div id="result1" style="${!(client?.scanpassport)?'display:none':''}">
              <a class="button-glossy lightblue mini" href="${urlscanpassport+client?.scanpassport}" target="_blank" id="viewscan1">Посмотреть паспорт</a>
              <a class="button-glossy red mini" href="javascript:void(0)" onclick="deletescan(1)">Удалить</a>
            </div>
          </fieldset>
          <fieldset style="float:right;width:45%">
            <legend>Договор</legend>
            <div id="upload2" style="${client?.scandogovor?'display:none':''}">              
              <g:form name='ff2' method="post" url="${[action:'savescandogovor',id:client.id]}" enctype="multipart/form-data" target="upload_target2">
                <div id="error2" style="display: none;"></div>
                <label for="file1">Загрузить скан договора:</label>
                <input type="file" name="file1" size="23" accept="image/jpeg,image/gif" onchange="startSubmit('ff2',2)"/>                
              </g:form>
              <div id="loader2" class="spinner">
                <img src="${resource(dir:'images',file:'spinner.gif')}" border="0" />
              </div>
              <iframe id="upload_target2" name="upload_target2" src="#" style="width:0;height:0;border:0px solid #fff;"></iframe>
            </div>
            <div id="result2" style="${!(client?.scandogovor)?'display:none':''}">
              <a class="button-glossy lightblue mini" href="${urlscandogovor+client?.scandogovor}" target="_blank" id="viewscan2">Посмотреть договор</a> 
              <a class="button-glossy red mini" href="javascript:void(0)" onclick="deletescan(2)">Удалить</a></td>
            </div>
          </fieldset>
        </td>
      </tr>
      <tr>
        <td align="right">
          <input type="button" onclick="document.getElementById('submitButton').click()" class="button-glossy green" value="Сохранить"/>
          <input type="button" class="button-glossy lightblue" <g:if test="${!(client.resstatus>1||client.resstatus==-1)}">disabled</g:if> value="Подтвердить" onClick="confirmClient();">
          <input type="button" class="button-glossy grey" <g:if test="${!(client.resstatus>0)}">disabled</g:if> value="Отклонить" onClick="declineClient();">
          <g:if test="${client.resstatus>=1 && clienthistory}">
            <input type="button" onclick="$('clienthistory_table').show()" class="button-glossy orange" value="История"/>          
          </g:if>  
        </td>
      </tr>     
      <tr>
        <td><a class="to-parent" href="#" onClick="returnToList();">К списку клиентов</a></td>
      </tr>
    </table>
    <table id="clienthistory_table" class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="all" frame="none" style="display:none">          
      <tr align="center">
        <th width="30">Дата</th>
        <th>Плательщик</th>
        <th>ИНН</th>   
        <th>БИК</th>                
        <th>Банк</th>     
        <th>Корсчет</th> 
        <th>Расчетный счет</th>
        <th>WebMoney</th>  
        <th width="50">Схема</th>        
        <th width="60">Платежный аккаунт</th>
        <th width="30">Тип влад</th>
        <th width="30">Изм</th>                           
      </tr>           
    <g:each in="${clienthistory}" var="item">
      <tr>
        <td>${String.format('%td.%<tm.%<ty %<tH:%<tM',item?.moddate)}</td>
        <td <g:if test="${client.name!=item?.name}"> class="red"</g:if>>${item?.name}</td>         
        <td <g:if test="${client.inn!=item?.inn}"> class="red"</g:if>>${item?.inn}</td>
        <td <g:if test="${client.bik!=item?.bik}"> class="red"</g:if>>${item?.bik}</td>
        <td <g:if test="${client.bankname!=item?.bankname}"> class="red"</g:if>>${item?.bankname}</td>
        <td <g:if test="${client.corraccount!=item?.corraccount}"> class="red"</g:if>>${item?.corraccount}</td>
        <td <g:if test="${client.settlaccount!=item?.settlaccount}"> class="red"</g:if>>${item?.settlaccount}</td>
        <td <g:if test="${client.webmoney!=item?.webmoney}"> class="red"</g:if>>${item?.webmoney}</td>
        <td <g:if test="${client.reserve_id!=item?.reserve_id}"> class="red"</g:if>>${Reserve.get(item.reserve_id)?.name}</td>
        <td <g:if test="${client.payaccount_id!=item?.payaccount_id}"> class="red"</g:if>>${(item?.payaccount_id==1)?'Расчетный счет':((item?.payaccount_id==2)?'Яндекс':'Webmoney')}</td>
        <td align="center" <g:if test="${client.type_id!=item?.type_id}"> class="red"</g:if>>${(item?.type_id==1)?'ФизЛ':((item?.type_id==2)?'ЮрЛ':'ИП')}</td>
        <td align="center">${((item?.is_client?:0)==0)?'admin':'user'}</td>                              
      </tr>
    </g:each>           
    </table>
    <g:form name="returnToListForm" url="${[controller:'administrators',action:'clients', params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
