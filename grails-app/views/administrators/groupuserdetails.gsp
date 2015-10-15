<script type="text/javascript">
  iAccesslevel=${user?.accesslevel?:0};
</script>
<g:if test="${inrequest?.part}">
  <div class="glossy drop_shadow" style="padding:0px;width:520px">
    <div class="header_container">
      <h2>${user?.login?:'' }</h2>
    </div>  
    <div class="error-box" style="display:none;text-align:center;color:red">           
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>
    
    <g:formRemote url="[controller:'administrators',action:'usersave']" name="userForm" onSuccess="userSaveResp(e)"><!--update="[success:'details']"-->
      <input type="hidden" name="id" value="${user?.id?:0}">
      <table width="100%" cellpadding="5" cellspacing="5" border="0">
        <tr>
          <td width="150">Логин:</td>
          <td><input type="text" id="login" name="login" value="${user?.login?:''}" style="width:99%"/></td>
        </tr>
        <tr>
          <td>Полное имя:</td>
          <td><input type="text" id="name" name="name" value="${user?.name?:''}" style="width:99%"/></td>
        </tr>
        <tr>
          <td>Email:</td>
          <td><input type="text" id="email" name="email" value="${user?.email?:''}" style="width:99%"/></td>
        </tr>
        <tr>
          <td>Группа:</td>
          <td>
            <select name="group" style="width:100%">
              <g:if test="${!user?.admingroup_id?:0}"><option value="0"></option></g:if>
              <g:each in="${groups}" var="group">
                <option value="${group.id}" <g:if test="${group.id==user?.admingroup_id?:0 }">selected</g:if>>${group.name}</option>
              </g:each>
            </select>
          </td>
        </tr>
        <tr>
          <td>Уровень доступа:</td>
          <td>
            <select name="accesslevel" id="accesslevel" onchange="changeAccesslevel(this.value)" style="width:100%">
              <option value="0" <g:if test="${0==user?.accesslevel }">selected</g:if>>Региональный</option>
              <option value="1" <g:if test="${1==user?.accesslevel }">selected</g:if>>Федеральный</option>
            </select>
          </td>
        </tr>
        <tr>
            <td>Регионы:</td>
            <td></td>
          </tr> 
          <tr>
            <td colspan="2">
            <div id="userregions" style="margin-left:30px;height:280px;border:1px solid gray;overflow:auto;<g:if test="${user?.accesslevel}">display:none</g:if>">                         
                <g:each in="${country}" var="item" status="i">
                  <span>${item.name}</span> <br/>                           
                    <table>
                      <g:each in="${regions[i]}" var="region">
                      <tr>
                        <td>
                          &nbsp;&nbsp;${region.name}
                        </td>
                        <td>
                          <input type="checkbox" name="region_id" value="${region.id}" <g:if test="${region.id in user_regions}">checked</g:if>/>
                        </td>
                      </tr>
                    </g:each>
                  </table>                                                       
                </g:each>                        
            </div>            
            <div id="allregions"  style="margin-left:30px;position:relative;<g:if test="${!user.accesslevel}">display:none</g:if>">
              Все регионы
            </div>
          </td>
          <td></td>
        </tr>      
        <tr>
          <td colspan="2" align="right" nowrap>        			
            <input type="button" class="button-glossy red mini" value="Удалить" onclick="deleteUser(${user?.id?:0})" style="margin:0 5px"/>            
            <input type="submit" class="button-glossy green mini" value="Сохранить" style="margin:0 5px;"/>            
            <input type="reset" class="button-glossy grey mini" value="Отмена" onclick="$('details').update('')" style="margin:0 5px"/>
          </td>
        </tr>
      </table>
    </g:formRemote>
    <hr/>  
    <g:formRemote url="[controller:'administrators',action:'changepass']" onSuccess="processPassResponse(e)" method="POST" name="changePassForm">
      <input type="hidden" name="ajax" value="1">				  
      <input type="hidden" name="id" id="change_pass_id" value="${user?.id?:0}">
      <div id="passmess" style="text-align:center;color:red"></div>     
      <table width="100%" cellpadding="5" cellspacing="5" border="0">
        <tr>
          <td width="150">Новый пароль:</td>
          <td><input type="password" name="pass" id="pass" style="width:100%;"/></td>
        </tr>
        <tr>
          <td>Повторите пароль:</td>
          <td><input type="password" name="confirm_pass" id="confirm_pass" style="width:100%;"/></td>
        </tr>
        <tr>
          <td colspan="2" align="right">
            <input type="submit" class="button-glossy green mini" value="Изменить пароль"/>                        
          </td>
        </tr>
      </table>
    </g:formRemote>                 
  </div>
</g:if>
<g:elseif test="${group}">
  <div class="glossy drop_shadow" style="padding:0px;width:320px">
    <div class="header_container">
      <h2>${group.name}</h2>
    </div>
    <g:formRemote url="[controller:'administrators',action:'groupsave']" name="groupForm" update="[success:'details']">
      <input type="hidden" name="id" value="${group.id}">
      <table width="100%" cellpadding="5" cellspacing="5" border="0">
        <tr>
          <td>Профиль пользователя</td>
          <td width="30"><input type="checkbox" name="is_profile" value="1" <g:if test="${group.is_profile}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Пользователи</td>
          <td><input type="checkbox" name="is_users" value="1" <g:if test="${group.is_users}">checked</g:if>/></td>          
        </tr>
        <tr>
          <td>Объявления</td>
          <td><input type="checkbox" name="is_homes" value="1" <g:if test="${group.is_homes}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Инфотекст</td>
          <td><input type="checkbox" name="is_infotext" value="1" <g:if test="${group.is_infotext}">checked</g:if>/></td>          
        </tr>
        <tr>
          <td>Отзывы</td>
          <td><input type="checkbox" name="is_reviews" value="1" <g:if test="${group.is_reviews}">checked</g:if>/></td>          
        </tr>
        <tr>
          <td>Статистика</td>
          <td><input type="checkbox" name="is_stats" value="1" <g:if test="${group.is_stats}">checked</g:if>/></td>          
        </tr>
        <tr>
          <td>Адресная система</td>
          <td><input type="checkbox" name="is_adress" value="1"  <g:if test="${group.is_adress}">checked</g:if>/></td>          
        </tr>
        <tr>
          <td>Баннеры</td>
          <td><input type="checkbox" name="is_banners" value="1"  <g:if test="${group.is_banners}">checked</g:if>/></td>          
        </tr>
        <tr>
          <td>Популярные направления</td>
          <td><input type="checkbox" name="is_popdir" value="1"  <g:if test="${group.is_popdir}">checked</g:if>/></td>          
        </tr>
		    <tr>
          <td>Обратная связь</td>
		      <td><input type="checkbox" name="is_guestbook" value="1"  <g:if test="${group.is_guestbook}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Рассылка email</td>
		      <td><input type="checkbox" name="is_mail" value="1"  <g:if test="${group.is_mail}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Уведомления</td>
          <td><input type="checkbox" name="is_notetype" value="1" <g:if test="${group.is_notetype}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Заявки</td>
          <td><input type="checkbox" name="is_zayavka" value="1" <g:if test="${group.is_zayavka}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Sms</td>
          <td><input type="checkbox" name="is_sms" value="1" <g:if test="${group.is_sms}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Переписка</td>
          <td><input type="checkbox" name="is_mbox" value="1" <g:if test="${group.is_mbox}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Статьи</td>
          <td><input type="checkbox" name="is_article" value="1" <g:if test="${group.is_article}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Клиенты</td>
          <td><input type="checkbox" name="is_clients" value="1" <g:if test="${group.is_clients}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Банк-клиент</td>
          <td><input type="checkbox" name="is_bank" value="1" <g:if test="${group.is_bank}">checked</g:if>/></td>
        </tr>
        <tr>
          <td>Выборки</td>
          <td><input type="checkbox" name="is_selection" value="1" <g:if test="${group.is_selection}">checked</g:if>/></td>
          <td></td>
        </tr>
        <tr>
          <td>Заказы</td>
          <td><input type="checkbox" name="is_payorder" value="1" <g:if test="${group.is_payorder}">checked</g:if>/></td>
          <td></td>
        </tr>
        <tr>
          <td>Аккаунты</td>
          <td><input type="checkbox" name="is_account" value="1" <g:if test="${group.is_account}">checked</g:if>/></td>
          <td></td>
        </tr>
        <tr>
          <td>Транзакции</td>
          <td><input type="checkbox" name="is_paytrans" value="1" <g:if test="${group.is_paytrans}">checked</g:if>/></td>
          <td></td>
        </tr>
        <tr>
          <td>Заказы</td>
          <td><input type="checkbox" name="is_paytask" value="1" <g:if test="${group.is_paytask}">checked</g:if>/></td>
          <td></td>
        </tr>
        <tr>
          <td>Анонсы</td>
          <td><input type="checkbox" name="is_anons" value="1" <g:if test="${group.is_anons}">checked</g:if>/></td>
          <td></td>
        </tr>
        <tr>
          <td>Отчеты</td>
          <td><input type="checkbox" name="is_payreport" value="1" <g:if test="${group.is_payreport}">checked</g:if>/></td>
          <td></td>
        </tr>        
        <tr>
          <td>Поездки</td>
          <td><input type="checkbox" name="is_trip" value="1" <g:if test="${group.is_trip}">checked</g:if>/></td>
          <td></td>
        </tr>
        <tr>
          <td>Типы апартаментов</td>
          <td><input type="checkbox" name="is_hometype" value="1" <g:if test="${group.is_hometype}">checked</g:if>/></td>
          <td></td>
        </tr>
        <tr>
          <td colspan="2" align="right">
            <input type="reset" class="button-glossy grey mini" value="Отмена" onclick="$('details').update('')" style="margin-right:10px"/>
            <input type="submit" class="button-glossy green mini" value="Сохранить"/>
          </td>        
        </tr>
      </table>
    </g:formRemote>
  </div>
</g:elseif>
