<html>
  <head>
    <title>Административное приложение StayToday.ru</title>
    <meta name="layout" content="admin" />
    <g:javascript library="jquery-1.8.3" />    
    <g:javascript library="jquery.colorbox.min" />    
    <style type="text/css">
      .dotted td { padding: 0px 15px !important }
    </style>
    <g:javascript>
      function returnToList(){
      $("returnToListForm").submit();
      }
      function smsSend(iId){
        <g:remoteFunction controller='administrators' action='getUserforSMS' onSuccess='processResponseSMS(e)' params="'id='+iId" />
      }      
      function sendUserSMS(iId){
        $("sms_id").value=iId;
        $("sms_stext").value=$("stext").value;
        $("sms_send_submit_button").click();
      }      
      function processResponseSMS(e){           
        var template ='<div id="filters_lightbox" class="new-modal">'+                      
                      '  <div id="lightbox_filters" class="segment nopadding">'+
                      '    <div class="lightbox_filter_container">'+
                      '      <table width="100%" cellpadding="0" cellspacing="0" border="0">'+
                      '        <tr>'+
                      '	         <td>'+
                      '	           <div width="68" style="float:left;margin-right:15px">'+
                      '		           <div class="glossy drop_shadow" style="background:#d1d1d1;width:68px;height:68px;padding:8px">'+
                      ((e.responseJSON.smallpicture!='')?
                      '		             <img class="slideshow" width="68" height="68" alt="'+e.responseJSON.nickname+'" title="'+e.responseJSON.nickname+'"'+
                      '			             src="'+((e.responseJSON.smallpicture && !e.responseJSON.is_external)?'${imageurl}':'')+((e.responseJSON.smallpicture)?e.responseJSON.smallpicture:'')+'">'
                      :'${resource(dir:"images",file:"user-default-picture.png")}')+
                      '		           </div>'+
                      '		           <div class="user" style="width:68px">'+
                      '		             <small style="white-space:normal"><a href="${(context.is_dev)?'/'+context.appname:''}/profile/view/'+e.responseJSON.id+'">'+e.responseJSON.nickname+'</a></small><br/>'+
                      '		           </div>'+
                      '	           </div>'+
                      '          </td>'+
                      '	         <td>'+
                      '	           <div width="68" style="float:left;margin-right:15px">'+
                      '		           <div class="user" style="width:68px">'+
                      '		             <small style="white-space:normal"><span>'+e.responseJSON.tel+'</span></small><br/>'+
                      '		           </div>'+
                      '	           </div>'+
                      '          </td>'+
                      '        </tr>'+
                      '      </table>'+                      
                      '      <div id="sms_error" class="notice" style="margin:5px 0;width:95%;display:none;">'+
                      '        <span id="sms_errorText" style="font-size:12px"></span>'+
                      '      </div>'+
                      '      <table width="100%" cellpadding="5" cellspacing="0" border="0">'+
                      '        <tr>'+
                      '          <td colspan="4" valign="top">'+
                      '            <h2 class="toggle border"><span class="edit_icon password"></span>Текст смс</h2>'+
                      '          </td>'+
                      '        </tr>'+     
                      '        <tr colspan="4" valign="top">'+
                      '          <td><input type="text" maxlength="67" id="stext" value="" style="width:100%"/></td>'+
                      '        </tr>'+
                      '      </table>'+
                      '    </div>'+
                      '  </div>'+
                      '  <div class="segment buttons">'+
                      '    <input type="button" onclick="sendUserSMS('+e.responseJSON.id+')" class="button-glossy green mini" value="${message(code:'button.send')}"/>'+
                      '  </div>'+                      
                      '</div>';                     
        jQuery.colorbox({
          html: template
        });        
			}    
      function sendsmsResponse(e){
        if(e.responseJSON.error){          
          $('sms_error').show();
          $('sms_errorText').update(e.responseJSON.message);
        } else {
          alert('SMS отправлено');
          location.reload(true);
        }
      }    
    </g:javascript>
  </head>  
  <body>
  <g:if test="${(flash?.error?:[]).size()>0}">
    <div class="notice drop_shadow">
      <div class="button-glossy orange header form">
        <h1>Ошибки</h1>
      </div>
      <ul>
      <g:each in="${flash?.error}">
        <g:if test="${it==1}"><li>Вы не заполнили обязательное поле &laquo;Город&raquo;</li></g:if>
        <g:if test="${it==2}"><li>Вы не заполнили обязательное поле &laquo;Текст заявки&raquo;</li></g:if>
        <g:if test="${it==3}"><li>Вы не заполнили обязательное поле &laquo;Цена от&raquo;</li></g:if>
        <g:if test="${it==10}"><li>Ошибка данных в справочниках</li></g:if>
        <g:if test="${it==101}"><li>Ошибка БД. Изменения не сохранены.</li></g:if>
      </g:each>
      </ul>
    </div>
  </g:if>  
  <g:form name="editForm" url="[controller:'administrators',action:'proposalsave', id:prop.id]" method="POST">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">
      <tr>
        <td colspan="7"><a class="to-parent" href="javascript:void(0)" onClick="returnToList();">К списку заявок</a></td>
      <tr>
      <tr>
        <td colspan="7"><h1 class="blue">Форма редактирования заявки № ${prop.id}</h1></td>
      </tr>
      <tr>
        <td colspan="7" valign="top">
          <h2 class="toggle border"><span class="edit_icon question"></span>Служебные</h2>
        </td>
      </tr>     
      <tr>
        <td nowrap>Статус:</td>
        <td colspan="2"><input type="text" disabled value="${(prop.modstatus==-4)?'закрытая':(prop.modstatus==-3)?'просроченная':(prop.modstatus==-2)?'спам/некорректная':(prop.modstatus==-1)?'нет подтверженного email':(prop.modstatus==0)?'новая':'публикация'}"/></td>
        <td nowrap>Кол-во предложений:</td>
        <td><input class="price" type="text" disabled value="${kolprop}"/></td>
        <td nowrap>Дата модификации:</td>
        <td><input type="text" class="price" disabled value="${String.format('%td.%<tm.%<tY',prop.lastUpdated)}"/></td>        
      </tr>
      <tr>
        <td colspan="7" valign="top">
          <h2 class="toggle border"><span class="edit_icon owner"></span>Информация о владельце</h2>
        </td>
      </tr>
      <tr>
        <td>Ник:</td>
        <td colspan="2"><b><g:link controller="profile" action="view" id="${user.id}">${user?.nickname}</g:link></b></td>
        <td colspan="2" nowrap>
          <label>
          <g:if test="${prop?.is_auto}">
            <g:link action="mbox" params="${[owner_id:User.findWhere(client_id:prop?.baseclient_id)?.id?:0, user_id:prop?.user_id]}">Email</g:link>:</label>
          </g:if>  
          <g:else>
            Email:
          </g:else>
          <input type="text" disabled value="${user?.email}"/>
        </td>
        <td colspan="2" nowrap>
          <label>Телефон:</label>
          <input type="text" disabled value="${prop.mobtel}"/>
        </td>
      </tr>
      <tr>
        <td colspan="7" valign="top">
          <h2 class="toggle border"><span class="edit_icon"></span>Данные заявки</h2>
        </td>
      </tr>
      <tr>
        <td>Страна:</td>
        <td>
          <select id="country_id" name="country_id">
          <g:each in="${country}" var="item">            
            <option <g:if test="${item?.id==prop?.country_id}">selected="selected"</g:if> value="${item?.id}">
              ${item?.name}
            </option>
          </g:each>			  
          </select>
        </td>
        <td>Город:</td>
        <td><input type="text" name="city" value="${prop.city}"/></td>        
        <td colspan="3" nowrap>
          <label>Регион:&nbsp;</label>
          <select id="region_id" name="region_id">
          <g:each in="${region}" var="item">            
            <option <g:if test="${item?.id==prop?.region_id}">selected="selected"</g:if> value="${item?.id}">
              ${item?.name}
            </option>
          </g:each>              
          </select>          
        </td>
      </tr>
      <tr>
        <td nowrap>Цена от:</td>
        <td><input type="text" class="price" id="pricefrom" name="pricefrom" value="${prop.pricefrom}"/></td>
        <td nowrap>Цена до:</td>
        <td><input type="text" class="price" id="priceto" name="priceto" value="${prop.priceto}"/></td>
        <td colspan="3" nowrap>
          <label>Валюта:&nbsp;</label>
          <select id="valuta_id" disabled name="valuta_id">
          <g:each in="${valuta}" var="item">            
            <option <g:if test="${item?.id==prop?.valuta_id}">selected="selected"</g:if> value="${item?.id}">
              ${item?.code}&nbsp;${item?.name}
            </option>
          </g:each>
          </select>
        </td>
      </tr>
      <tr>
        <td colspan="2" nowrap>
          <label>Дата заезда:</label>
          <input type="text" class="price" disabled value="${String.format('%td.%<tm.%<tY',prop.date_start)}"/>
        </td>
        <td colspan="2" nowrap>
          <label>Дата отъезда:</label>
          <input type="text" class="price" disabled value="${String.format('%td.%<tm.%<tY',prop.date_end)}"/>
        </td>
        <td nowrap>Дата заявки:</td>
        <td colspan="2"><input type="text" class="price" disabled value="${String.format('%td.%<tm.%<tY',prop.inputdate)}"/></td>        
      </tr>
      <tr>
        <td colspan="7">
          <h2 class="toggle border"><span class="edit_icon details"></span>Текст заявки</h2>
        </td>
      </tr>
      <tr>
        <td colspan="7">
          <textarea rows="5" cols="40" id="ztext" name="ztext" style="width:99%">${prop.ztext?:''}</textarea>
        </td>
      </tr>
      <tr>
        <td colspan="4" nowrap>
          <label>Заявка актуальна в течении:&nbsp;</label>
          <select id="timetodecide_id" disabled name="timetodecide_id">
          <g:each in="${timetodecide}" var="item">
            <option <g:if test="${item?.id==prop?.timetodecide_id}">selected="selected"</g:if> value="${item?.id}">
              ${item?.name}
            </option>
          </g:each>
          </select>
        </td>
        <td colspan="3" align="right">          
          <input type="submit" class="button-glossy green" value="${message(code:'button.save')}" style="margin-right:10px">
          <input type="button" class="button-glossy red" value="${message(code:prop.modstatus==-2?'button.recover':'button.delete')}" onClick="$('deleteForm').submit();">
        </td>
      </tr>
      <tr>
        <td colspan="7"><a class="to-parent" href="#" onClick="returnToList();">К списку заявок</a></td>
      </tr>
    </table>
  </g:form>  
  <g:if test="${prop.modstatus>-2}">
    <table width="100%" cellpadding="5" cellspacing="5" border="0">	  
      <tr>      
        <td><h2 class="toggle border"><span class="edit_icon period"></span>
        <g:if test="${prop.modstatus!=1}">Возможные предложения по заявке № ${prop.id}</g:if>
        <g:elseif test="${prop.modstatus==1}">Предложения по заявке № ${prop.id}</g:elseif>
        </h2></td>
      </tr>
	    <tr>
        <td>     
          <g:if test="${records}">                        
          <table class="dotted" width="100%" cellpadding="0" cellspacing="0" rules="rows" style="background-color: #fff">
            <thead>
              <tr>
              <g:if test="${prop.modstatus!=1}">                
                <th>Код</th>
                <th>Владелец</th>
                <th>Email</th>
                <th>Название объявления</th>
                <th>Адрес</th>
                <th>Стоимость</th>
              </g:if>
              <g:elseif test="${prop.modstatus==1}">                
                <th></th>
                <th>Код</th>
                <th>Email</th>
                <th>Дата</th>
                <th>Статус</th>
                <th>Действия</th>
              </g:elseif>
              </tr>
            </thead>
            <tbody>
            <g:each in="${records}" status="i" var="record">
              <tr id="tr_${i}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
              <g:if test="${prop.modstatus!=1}">                              
                <td>${record.hid}</td>
                <td>${User.findByClient_id(record.client_id).nickname}</td>
                <td>${User.findByClient_id(record.client_id).email}</td>
                <td>${record.name}</td>
                <td>${record.shortaddress}</td>
                <td>${Math.rint(100.0 * (record.price / valutaRates)) / 100.0} <sup><g:rawHtml>${valutaSym}</g:rawHtml></sup></td>
              </g:if>
              <g:elseif test="${prop.modstatus==1}">              
                <td width="5"><g:if test="${record.modstatus==1}"><a href="javascript:void(0)" onclick="$('trr_${record.id}').toggle();">+</a></g:if></td>
                <td>${record.id}</td>
                <td>${record.email}</td>
                <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
                <td>
                  <g:if test="${record?.modstatus==0}">новая</g:if>
                  <g:elseif test="${record?.modstatus==1}">публикация</g:elseif>
                  <g:elseif test="${record?.modstatus==-1}">отклонена</g:elseif>
                  <g:elseif test="${record?.modstatus==-2}">спам/некорректная</g:elseif>
                  <g:elseif test="${record?.modstatus==-3}">просроченная</g:elseif>                
                </td>
                <td>
                  <span class="action_button">
                    <span class="icon none" onclick="smsSend(${User.findByClient_id(record.client_id).id})">Отправить смс</span>
                  </span>                
                </td>
              </g:elseif>
              </tr>
              <g:if test="${record.modstatus==1}">
              <tr id="trr_${record.id}" style="display:none">                
                <td colspan="5" style="padding:5px 0 10px 40px !important">
                  <table width="100%" cellpadding="5" cellspacing="0" border="0" style="background: rgba(152,252,152, 0.4)">
                  <g:each in="${homes}" var="hm">
                    <g:each in="${hm}" var="home">
                      <g:if test="${home.client_id==record.client_id}">
                    <tr>                      
                      <td width="115">
                        <div class="thumbnail">
                          <img src="${(home?.mainpicture)?homeimageurl+home.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}"/>
                        </div>
                      </td>
                      <td>
                        <div class="user" style="float:left">
                          <b><g:link controller="home" action="view" id="${home?.id}" target="_blank">${home?.name}</g:link></b><br/>
                          <small>${home?.address}</small>
                        </div>
                        <div class="price" style="float:right;width:200px;">
                          <div class="price_data">
                            <div class="currency">
                              ${Math.rint(100.0 * (home.price / valutaRates)) / 100.0} <sup><g:rawHtml>${valutaSym}</g:rawHtml></sup>
                            </div>
                          </div> 
                          <div class="price_modifier" style="width:150px">за сутки</div>                          
                        </div>
                      </td>
                      <td width="24%">&nbsp;</td>
                    </tr>
                    <tr>
                      <td colspan="3" style="padding:0px"><hr class="dot" style="margin:0px"/></td>
                    </tr>
                      </g:if>
                    </g:each>
                  </g:each>
                  </table>
                </td>
              </tr>
              </g:if>
            </g:each>
            </tbody>
          </table>          
        </td>
      </tr>
<g:if test="${prop.modstatus==0}">
      <tr>
        <td align="right">
          <input type="button" class="button-glossy lightblue" value="Распределить" onclick="$('allocateForm').submit();">
        </td>
      </tr>
</g:if>
    </g:if>
    <g:else>
      <tr>
        <td>В настоящий момент предложений нет</td>
      </tr>
    </g:else>
    </table>
  </g:if>
  <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'administrators',action:'proposal', params:[fromEdit:1]]}">
  </g:form>
  <g:form  id="deleteForm" name="deleteForm" url="${[controller:'administrators',action:'proposaldelete',id:prop.id,params:[act:'editproposal']]}">
  </g:form>
  <g:form  id="allocateForm" name="allocateForm" url="${[controller:'administrators',action:'allocateproposal',id:prop.id,params:[clIds:(clientIds?:[]).toString(),clIdsStep:(clientIdsStep?:[]).toString()]]}">
  </g:form>
  <g:formRemote name="smsSendForm" url="[controller: 'administrators', action: 'sendUserSMS']" onSuccess="sendsmsResponse(e)">      
    <input type="hidden" id="sms_id" name="id" value="0">
    <input type="hidden" id="sms_stext" name="stext" value="">
    <input type="submit" id="sms_send_submit_button" style="display:none" value="">
  </g:formRemote>  
  </body>
</html>
