<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
<g:if test="${inrequest.type}">
    <meta name="layout" content="print" />
</g:if><g:else>    
    <style type="text/css">
      table { border-top: 2px solid #000; border-left: 2px solid #000 }
      table td { border-bottom: 2px solid #000; border-right: 2px solid #000 }
      td li { list-style: none none outside !important; }   
    </style>
</g:else>
  </head>
  <body onload="setTimeout('window.print()', 1000);">
<g:if test="${inrequest.type}">
            <tr>
              <td width="980" colspan="2">
                <div class="body shadow" style="width:800px">
                  <ul class="description_details padd10" style="float:none;width:600px;height:auto">
                    <li class="clearfix">
                      <span class="property">Получатель платежа</span>
                      <span class="value">${client?.payee?:''}</span>
                    </li>
                    <li class="clearfix">
                      <span class="property">ИНН</span>
                      <span class="value">${client?.inn?:''}</span>
                    </li>
                    <li class="clearfix">
                      <span class="property">КПП</span>
                      <span class="value">${client?.kpp?:''}</span>
                    </li>
                    <li class="clearfix">
                      <span class="property">Название банка</span>
                      <span class="value">${client?.bankname?:''}</span>
                    </li>
                    <li class="clearfix">
                      <span class="property">БИК</span>
                      <span class="value">${client?.bik?:''}</span>
                    </li>
                    <li class="clearfix">
                      <span class="property">Корреспондентский счет</span>
                      <span class="value">${client?.corraccount?:''}</span>
                    </li>
                    <li class="clearfix">
                      <span class="property">Расчетный счет</span>
                      <span class="value">${client?.settlaccount?:''}</span>
                    </li>
                    <li class="clearfix">
                      <span class="property">Величина НДС</span>
                      <span class="value">${client?.nds?:''}</span>
                    </li>
                    <li class="clearfix">
                      Комментарий к платежу - ${client?.paycomment?:''}
                    </li>
                  </ul>
                  <div class="padd20">
                    ${client?.paymessage?:''}
                  </div>
                </div>
              </td>
            </tr>
</g:if><g:else>
            <tr>
              <td>
                <table width="720" cellpadding="5" cellspacing="0">
                  <tr>
                    <td width="220" valign="top" height="250" align="center">
                      &nbsp;
                      <b>Извещение</b>
                    </td>
                    <td valign="top">
                      <li>
                        <b>Получатель:</b>
                        <font style="font-size:90%">${client?.payee?:'____________________________________________________'}</font>                    
                      </li>
                      <li>
                        <b>ИНН:</b> ${client?.inn?:'_________________________'}&nbsp;
                        <b>КПП:</b> ${client?.kpp?:'_____________________'}                        
                      </li>
                      <li>
                        <b>Код ОКАТО:</b> _________________&nbsp;
                        <b>P/сч.:</b> ${client?.settlaccount?:'______________________'}
                      </li>
                      <li>
                        <b>в:</b>
                        <font style="font-size:90%">${client?.bankname?:'______________________________________________________________'}</font>                       
                      </li>
                      <li>
                        <b>БИК:</b> ${client?.bik?:'________________'}&nbsp;
                        <b>К/сч.:</b> ${client?.corraccount?:'______________________________'}
                      </li>
                      <li>
                        <b>Код бюджетной классификации (КБК):</b> _______________________
                      </li>
                      <li>
                        <b>Платеж:</b><br>
                        <font style="font-size:90%">${client?.paycomment?:'_________________________________________________________________'}</font>
                      </li>
                      <li>
                        <b>Плательщик:</b> ${inrequest?.payer?:'______________________________________________'}
                      </li>
                      <li>
                        <b>Адрес плательщика:</b>
                        <font style="font-size:90%">${inrequest?.payeraddress?:'___________________________________________'}</font>
                      </li>
                      <li>
                        <b>ИНН плательщика:</b> ${inrequest?.payerinn?:'___________'}&nbsp;
                        <font style="font-size:80%"><strong>№ л/сч. плательщика:</strong> ${inrequest?.payeraccount?:'______________'}</font>
                      </li>
                      <li>
                        <b>Сумма:</b>
                        <b>${inrequest?.payprice?:'______ руб.'}</b>
                        <b>00</b> коп. &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br />&nbsp;<br /><br />
                        Подпись:________________________ Дата: &quot;__&quot;&nbsp;_______&nbsp;&nbsp;2012 г.<br /><br />
                      </li>
                    </td>
                  </tr>
                  <tr>
                    <td valign="top" height="250" align="center">&nbsp;
                      <b>Квитанция</b>
                    </td>
                    <td valign="top">
                      <li>
                        <b>Получатель:</b>
                        <font style="font-size:90%">${client?.payee?:'____________________________________________________'}</font>
                      </li>
                      <li>
                        <b>ИНН:</b> ${client?.inn?:'_________________________'}&nbsp;
                        <b>КПП:</b> ${client?.kpp?:'_____________________'}                        
                      </li>
                      <li>
                        <b>Код ОКАТО:</b> _________________&nbsp;
                        <b>P/сч.:</b> ${client?.settlaccount?:'______________________'}
                      </li>
                      <li>
                        <b>в:</b>
                        <font style="font-size:90%">${client?.bankname?:'______________________________________________________________'}</font>                       
                      </li>
                      <li>
                        <b>БИК:</b> ${client?.bik?:'________________'}&nbsp;
                        <b>К/сч.:</b> ${client?.corraccount?:'______________________________'}
                      </li>
                      <li>
                        <b>Код бюджетной классификации (КБК):</b> _______________________
                      </li>
                      <li>
                        <b>Платеж:</b><br>
                        <font style="font-size:90%">${client?.paycomment?:'_________________________________________________________________'}</font>
                      </li>
                      <li>
                        <b>Плательщик:</b> ${inrequest?.payer?:'______________________________________________'}
                      </li>
                      <li>
                        <b>Адрес плательщика:</b>
                        <font style="font-size:90%">${inrequest?.payeraddress?:'___________________________________________'}</font>
                      </li>
                      <li>
                        <b>ИНН плательщика:</b> ${inrequest?.payerinn?:'___________'}&nbsp;
                        <font style="font-size:80%"><strong>№ л/сч. плательщика:</strong> ${inrequest?.payeraccount?:'______________'}</font>
                      </li>
                      <li>
                        <b>Сумма:</b>
                        <b>${inrequest?.payprice?:'______ руб.'}</b>
                        <b>00</b> коп. &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br />&nbsp;<br /><br />
                        Подпись:________________________ Дата: &quot;__&quot;&nbsp;_______&nbsp;&nbsp;2012 г.<br /><br />
                      </li>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
</g:else>
  </body>
</html>
