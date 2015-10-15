<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript library="prototype" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponseComment(e){
        if(e.responseJSON.error) alert('Данные не сохранены');
        else alert('Данные сохранены');
      }
      function processResponseReturn(e){
        if(e.responseJSON.error) alert('Ошибка возврата денег');
        else location.reload(true);
      }
      function processResponseDealDeclain(e){
        if(e.responseJSON.error) alert('Ошибка отмены сделки');
        else location.reload(true);
      }
      function penalization(){
        alert('Необходимо описание действий, выполняемых при назначении штрафа.');
      }
      function moneyReturn(){
        <g:remoteFunction controller='adminbilling' action='payorderMoneyReturn' onSuccess='processResponseReturn(e)' params="'id=${payorder.id}'" />
      }
      function addComment(){
        var comm = document.getElementById('admincomment').value
        <g:remoteFunction controller='adminbilling' action='payorderAddComment' onSuccess='processResponseComment(e)' params="'admincomment='+comm+'&id=${payorder.id}'" />
      }
      function declainDeal(){
        <g:remoteFunction controller='adminbilling' action='payorderDeclainDeal' onSuccess='processResponseDealDeclain(e)' params="'id=${payorder.id}'" />
      }
      jQuery(document).ready(function() {
        jQuery('#changehome_link').colorbox({
          inline: true,
          href: '#changehome_lbox',
          scrolling: false,
          onLoad: function(){
            jQuery('#changehome_lbox').show();
          },
          onCleanup: function(){
            jQuery('#changehome_lbox').hide();
            $('changehome_errors').hide();
          }
        });
      });
      function processResponse(e){
        if(e.responseJSON.error){
          $('changehome_errors').innerHTML = e.responseJSON.errortext;
          $('changehome_errors').show();
        } else location.reload(true);
      }
    </g:javascript>
  </head>
  <body>
  <g:if test="${(flash?.save_error?:[]).size()>0}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
    <g:each in="${flash?.save_error}">
      <g:if test="${it==101}"><li>Ошибка БД. Изменения не сохранены.</li></g:if>
    </g:each>
      </ul>
    </div>
  </g:if>

  <g:form name="editPayorderForm" url="[controller:'adminbilling',action:'savePayorderDetails', id:payorder.id]" method="post" base="${context?.mainserverURL_lang}">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td colspan="4" style="padding:0px">
          <a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку заказов</a>
        </td>
      </tr>
      <tr>
        <td colspan="4"><h1 class="blue">Детали заказа № ${payorder.norder}</h1></td>
      </tr>
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Общие данные</h2>
        </td>
      </tr>
      <tr>
        <td style="width:100px" nowrap>Код заказа:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.id}"/></td>
        <td style="width:100px" nowrap>Номер заказа:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.norder}"/></td>
      </tr>
      <tr>
        <td nowrap>Клиент:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.user_id}"/></td>
        <td nowrap>Ник клиента:</td>
        <td><input style="width:100%" type="text" name="paydate" disabled value="${User.get(payorder.user_id)?.nickname?:''}"/></td>
      </tr>
      <tr>
        <td nowrap>Владелец:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.client_id}"/></td>
        <td nowrap>Email владельца:</td>
        <td><input style="width:100%" type="text" disabled value="${client.name}"/></td>
      </tr>
      <tr>
        <td nowrap>Дом:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.home_id}"/></td>
        <td nowrap>Переписка:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.mbox_id}"/></td>
      </tr>
      <tr>
        <td nowrap>Агрегатор:</td>
        <td><input style="width:100%" type="text" disabled value="${Agr.get(payorder.agr_id)?.name?:''}"/></td>
        <td nowrap>Плат. средство:</td>
        <td><input style="width:100%" type="text" disabled value="${Payway.get(payorder.payway_id)?.name?:''}"/></td>
      </tr>
      <tr>
        <td nowrap>Дата заказа:</td>
        <td><input style="width:100%" type="text" disabled value="${String.format('%td.%<tm.%<tY',payorder.inputdate)}"/></td>
        <td nowrap>Дата модиф.:</td>
        <td><input style="width:100%" type="text" disabled value="${String.format('%td.%<tm.%<tY',payorder.moddate)}"/></td>
      </tr>
      <tr>
        <td nowrap>Промокод:</td>
        <td colspan="3"><input style="width:100%" type="text" disabled value="${payorder.promocode}"/></td>
      </tr>
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon ask"></span>Детали бронирования</h2>
        </td>
      </tr>
      <tr>
        <td nowrap>Дата заезда:</td>
        <td><input type="text" style="width:100%" disabled value="${String.format('%td.%<tm.%<tY',mbox?.date_start?:trip?.fromdate)}"/></td>
        <td nowrap>Дата отъезда:</td>
        <td><input type="text" style="width:100%" disabled value="${String.format('%td.%<tm.%<tY',mbox?.date_end?:trip?.todate)}"/></td>
      </tr>
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon period"></span>Суммы</h2>
        </td>
      </tr>
      <tr>
        <td nowrap>Сумма:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.summa}"/></td>
        <td nowrap>Комиссия агрегатора:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.summa_agr}"/></td>
      </tr>
      <tr>
        <td nowrap>Сумма на корр.счете:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.summa_ext}"/></td>
        <td nowrap>Сумма на лицевом счете:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.summa_int}"/></td>
      </tr>
      <tr>
        <td nowrap>Сумма оказанных услуг:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.summa_serv}"/></td>
        <td nowrap>Сумма комиссии:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.summa_com}"/></td>
      </tr>
      <tr>
        <td nowrap>Сумма владельцу:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.summa_own}"/></td>
        <td nowrap>Сумма возврата плательщику:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.summa_ret}"/></td>
      </tr>
      <tr>
        <td nowrap>Сумма сделки:</td>
        <td colspan="3"><input style="width:100%" type="text" disabled value="${payorder.summa_deal}"/></td>
      </tr>
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon question"></span>Статусы</h2>
        </td>
      </tr>
      <tr>
        <td nowrap>Активности:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.modstatus==0?'неактивно':payorder.modstatus==1?'активный':payorder.modstatus==2?'выполнен':'удален'}"/></td>
        <td nowrap>Приема средств:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.agrstatus==0?'нет':payorder.agrstatus==1?'перечислено':payorder.agrstatus==2?'возврат':payorder.agrstatus==3?'блокирована без перечисления':payorder.agrstatus==4?'блокировка снята без перечисления':'перечислено на внутренний счет'}"/></td>
      </tr>
      <tr>
        <td nowrap>Возврата:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.retstatus==0?'нет':payorder.retstatus==1?'возврат по причине неподтверждения брони':payorder.retstatus==2?'возврат по отмене брони клиентом':'возврат по отмене брони владельцем'}"/></td>
        <td nowrap>Перечисления:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.transtatus==0?'нет':payorder.transtatus==1?'да':'возврат'}"/></td>
      </tr>
      <tr>
        <td nowrap>Подтверждения заказа:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.confstatus==0?'нет':payorder.confstatus==1?'да':'отмена'}"/></td>
        <td nowrap>Подтверждения сделки:</td>
        <td><input style="width:100%" type="text" disabled value="${payorder.dealstatus==0?'нет':payorder.dealstatus==1?'да':'отмена'}"/></td>
      </tr>
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon ask"></span>Плательщик</h2>
        </td>
      </tr>
      <tr>
        <td>Название плательщика:</td>
        <td colspan="3"><textarea rows="5" cols="40" disabled style="width:99%">${payorder?.plat_name}</textarea></td>
      </tr>
      <tr>
        <td>Адрес плательщика:</td>
        <td colspan="3"><textarea rows="5" cols="40" disabled style="width:99%">${payorder?.plat_address}</textarea></td>
      </tr>
      <tr>
        <td nowrap>Счет плательщика:</td>
        <td colspan="3"><input style="width:100%" type="text" disabled value="${payorder.plat_schet}"/></td>
      </tr>
      <tr>
        <td nowrap>ИНН плательщика:</td>
        <td><input type="text" style="width:100%" disabled value="${payorder.plat_inn}"/></td>
        <td nowrap>КПП плательщика:</td>
        <td><input type="text" style="width:100%" disabled value="${payorder.plat_okpo}"/></td>
      </tr>
      <tr>
        <td nowrap>Корсчет банка плательщика:</td>
        <td><input type="text" style="width:100%" disabled value="${payorder.plat_corchet}"/></td>
        <td nowrap>БИК банка плательщика:</td>
        <td><input type="text" style="width:100%" disabled value="${payorder.plat_bik}"/></td>
      </tr>
      <tr>
        <td>Название банка плательщика:</td>
        <td colspan="3"><textarea rows="5" cols="40" disabled style="width:99%">${payorder?.plat_bank}</textarea></td>
      </tr>
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon period"></span>Транзакции</h2>
        </td>
      </tr>
      <g:each in="${paytrans}" var="it" status="i">
      <tr>
        <td valign="top">
          <h2 class="toggle border" style="width:50px"><span class="edit_icon period"></span>${i+1}</h2>
        </td>
      </tr>
      <tr>
        <td nowrap>Дата:</td>
        <td><input type="text" style="width:100%" disabled value="${String.format('%td.%<tm.%<tY %<tT',it.moddate)}"/></td>
        <td nowrap>Сумма:</td>
        <td><input type="text" style="width:100%" disabled value="${it.summa}"/></td>
      </tr>
      <tr>
        <td>Тип:</td>
        <td colspan="3"><input style="width:100%" type="text" disabled value="${Paytype.get(it?.paytype_id)?.name?:it?.paytype_id}"/></td>
      </tr>
      <tr>
        <td>Статус:</td>
        <td colspan="3"><input style="width:100%" type="text" disabled value="${it.modstatus==0?'необработана':'обработана'}"/></td>
      </tr>
      </g:each>
      <tr>
        <td colspan="4" valign="top">
          <h2 class="toggle border"><span class="edit_icon period"></span>Комментарий администратора</h2>
        </td>
      </tr>
      <tr>
        <td colspan="4"><textarea rows="5" cols="40" id="admincomment" style="width:99%">${payorder.admincomment}</textarea></td>
      </tr>
      <tr>
        <td colspan="4" align="right">
          <input type="button" onclick="addComment()" class="button-glossy green mini" value="Сохранить"/>
        <g:if test="${payorder.modstatus==1}">
          <input type="button" class="button-glossy lightblue mini" ${payorder.retstatus?'disabled':''} value="Вернуть деньги" onClick="moneyReturn();">
          <input type="button" class="button-glossy lightblue mini" value="Сменить объект аренды" onClick="$('changehome_link').click()">
          <input type="button" class="button-glossy lightblue mini" ${(payorder.dealstatus!=0||payorder.confstatus!=1)?'disabled':''} value="Отказ от сделки гостем" onClick="declainDeal();">
          <input type="button" class="button-glossy red mini" value="Наложить штраф" onClick="penalization();">
        </g:if>
        </td>
      </tr>
      <tr>
        <td colspan="4"><a class="to-parent" href="#" onClick="returnToList();">К списку заказов</a></td>
      </tr>
    </table>
  </g:form>

    <a id="changehome_link" href="javascript:void(0)" style="display:none"></a>
    <div id="changehome_lbox" class="new-modal" style="display:none">
      <h2 class="clearfix">Сменить объект аренды</h2>
      <g:formRemote name="saveUserForm" url="[controller:'adminbilling',action:'changepayorderhome']" method="post" onSuccess="processResponse(e)">
      <div id="reg_lbox_segment" class="segment nopadding">
        <div id="reg_lbox_container" class="lightbox_filter_container">
          <div class="notice" id="changehome_errors" style="width:98%;margin:0 0 5px 0;display:none"></div>
          <table class="form s" width="100%" cellpadding="5" cellspacing="0" border="0">
            <tr>
              <td><label for="home_id">Id объявления</label></td>
              <td><input type="text" name="home_id" value="${payorder.home_id}" /></td>
            </tr>
            <tr>
              <td>
                <label for="order_date_start">Заезд&nbsp;&nbsp;</label>                            
                <calendar:datePicker needDisable="false" name="order_date_start" value="${trip?.fromdate}" dateFormat="%d-%m-%Y"/>
              </td>
              <td>
                <label for="order_date_end">Отъезд&nbsp;&nbsp;</label>
                <calendar:datePicker needDisable="false" name="order_date_end" value="${trip?.todate}" dateFormat="%d-%m-%Y"/>
              </td>
            </tr>
            <tr>
              <td><label for="summa_deal">Сумма сделки</label></td>
              <td><input type="text" name="summa_deal" value="${payorder.summa_deal}" onfocus="setNickname()" /></td>
            </tr>
            <tr>
              <td><label for="summa_val">Сумма в валюте</label></td>
              <td><input type="text" name="summa_val" value="${payorder.summa_val}" /></td>
            </tr>
            <tr>
              <td><label for="valuta_id">Валюта</label></td>
              <td><input type="text" name="valuta_id" value="${payorder.valuta_id}" /></td>
            </tr>
            <tr>
              <td><label for="mbox_id">Код переписки</label></td>
              <td><input type="text" name="mbox_id" value="" /></td>
            </tr>
          </table>
          <input type="hidden" name="id" value="${payorder.id}" />
        </div>
      </div>
      <div class="segment buttons">
        <input id="sendmail" type="submit" class="button-glossy orange mini" value="Отправить" />
      </div>
      </g:formRemote>
    </div>

  <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'adminbilling',action:'payorder', params:[fromDetail:1]]}">
  </g:form>
  </body>
</html>
