<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript library="prototype" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processPaytransfer(){
        $("processPaytransferForm").submit();
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

  <g:form name="editPaytransferForm" url="[controller:'adminbilling',action:'savePaytransferDetails', id:paytransfer.id]" method="post">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td colspan="5" style="padding:0px">
          <a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку объявлений</a>
        </td>
      </tr>
      <tr>
        <td colspan="5"><h1 class="blue">Детали платежа № ${paytransfer.id}</h1></td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon details"></span>Общие данные</h2>
        </td>
      </tr>
      <tr>
        <td nowrap>Номер платежки:</td>
        <td><input type="text" name="paynomer" disabled value="${paytransfer.paynomer}"/></td>
        <td nowrap>Дата платежки:</td>
        <td colspan="2"><input type="text" name="paydate" disabled value="${paytransfer.paydate}"/></td>
      </tr>
      <tr>
        <td nowrap>Сумма:</td>
        <td><input type="text" name="summa" disabled value="${paytransfer.summa}"/></td>
        <td nowrap>Тип операции:</td>
        <td colspan="2">
          <select name="paytype" <g:if test="${paytransfer.modstatus==1}">disabled</g:if>>
            <option value="0" <g:if test="${paytransfer?.paytype==0}">selected="selected"</g:if>>неопределено</option>
            <option value="1" <g:if test="${paytransfer?.paytype==1}">selected="selected"</g:if>>приход</option>
            <option value="2" <g:if test="${paytransfer?.paytype==2}">selected="selected"</g:if>>возврат</option>
            <option value="3" <g:if test="${paytransfer?.paytype==3}">selected="selected"</g:if>>вывод</option>
          </select>
      </tr>
      <tr>
        <td>Связанные заказы:</td>
        <td colspan="4"><input type="text" name="payorder" <g:if test="${paytransfer.modstatus==1}">disabled</g:if> style="width:100%" value="${paytransfer.payorder_id?:paytransfer.paylist?:''}"/></td>
      </tr>
      <tr>
        <td>Назначение платежа:</td>
        <td colspan="4"><textarea rows="5" cols="40" name="paycomment" style="width:99%">${paytransfer?.paycomment}</textarea></td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon ask"></span>Плательщик</h2>
        </td>
      </tr>
      <tr>
        <td nowrap>Счет плательщика:</td>
        <td><input type="text" name="from_account" disabled value="${paytransfer.from_account}"/></td>
        <td nowrap>ИНН плательщика:</td>
        <td colspan="2"><input type="text" name="from_inn" disabled value="${paytransfer.from_inn}"/></td>
      </tr>
      <tr>
        <td>Название плательщика:</td>
        <td colspan="4"><textarea rows="5" cols="40" name="from_name" disabled style="width:99%">${paytransfer?.from_name}</textarea></td>
      </tr>
      <tr>
        <td nowrap>Корсчет банка плательщика:</td>
        <td><input type="text" name="from_coraccount" disabled value="${paytransfer.from_coraccount}"/></td>
        <td nowrap>БИК банка плательщика:</td>
        <td colspan="2"><input type="text" name="from_bik" disabled value="${paytransfer.from_bik}"/></td>
      </tr>
      <tr>
        <td>Название банка плательщика:</td>
        <td colspan="4"><textarea rows="5" cols="40" name="from_bank" disabled style="width:99%">${paytransfer?.from_bank}</textarea></td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon period"></span>Получатель</h2>
        </td>
      </tr>
      <tr>
        <td nowrap>Счет получателя:</td>
        <td><input type="text" name="to_account" disabled value="${paytransfer.to_account}"/></td>
        <td nowrap>ИНН получателя:</td>
        <td colspan="2"><input type="text" name="to_inn" disabled value="${paytransfer.to_inn}"/></td>
      </tr>
      <tr>
        <td>Название получателя:</td>
        <td colspan="4"><textarea rows="5" cols="40" name="to_name" disabled style="width:99%">${paytransfer?.to_name}</textarea></td>
      </tr>
      <tr>
        <td nowrap>Корсчет банка получателя:</td>
        <td><input type="text" name="to_coraccount" disabled value="${paytransfer.to_coraccount}"/></td>
        <td nowrap>БИК банка получателя:</td>
        <td colspan="2"><input type="text" name="to_bik" disabled value="${paytransfer.to_bik}"/></td>
      </tr>
      <tr>
        <td>Название банка получателя:</td>
        <td colspan="4"><textarea rows="5" cols="40" name="to_bank" disabled style="width:99%">${paytransfer?.to_bank}</textarea></td>
      </tr>
      <tr>
        <td colspan="5" valign="top">
          <h2 class="toggle border"><span class="edit_icon owner"></span>Служебные</h2>
        </td>
      </tr>
      <tr>
        <td nowrap>Статус:</td>
        <td><input type="text" name="modstatus" disabled value="${paytransfer.modstatus==1?'обработанно':paytransfer.modstatus==2?'нераспознанно':'необработанно'}"/></td>
        <td nowrap>Администратор:</td>
        <td colspan="2">
          <g:select name="admin_id" from="${Admin.findAllByAdmingroup_id(1)}" value="${paytransfer.admin_id}" optionKey="id" optionValue="login" noSelection="${['0':'']}"/>
        </td>
      </tr>
      <tr>
        <td nowrap>Тип:</td>
        <td><input type="text" name="type" disabled value="${paytransfer.type}"/></td>
        <td nowrap>Вид:</td>
        <td colspan="2"><input type="text" name="vid" disabled value="${paytransfer.vid}"/></td>
      </tr>
      <tr>
        <td nowrap>Валюта:</td>
        <td><input type="text" name="valuta" disabled value="${paytransfer.valuta}"/></td>
        <td nowrap>Код:</td>
        <td colspan="2"><input type="text" name="to_cod" disabled value="${paytransfer.to_cod}"/></td>
      </tr>
      <tr>
        <td colspan="5" align="right">
          <input type="submit" class="button-glossy green" value="Сохранить">
        <g:if test="${paytransfer.modstatus==0}">
          <input type="button" class="button-glossy red" value="Обработать" onClick="processPaytransfer();">
        </g:if>
        </td>
      </tr>
      <tr>
        <td colspan="5"><a class="to-parent" href="#" onClick="returnToList();">К списку объявлений</a></td>
      </tr>
    </table>
  </g:form>
  <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'adminbilling',action:'paytransfer', params:[fromDetail:1]]}">
  </g:form>
  <g:form  id="processPaytransferForm" name="processPaytransferForm" url="${[controller:'adminbilling', action:'processPaytransfer', id:paytransfer.id]}">
  </g:form>
  </body>
</html>
