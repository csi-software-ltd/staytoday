<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <style type="text/css">
      @font-face {
        src: url(http://staytoday.ru:8080/fonts/arial.ttf);
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: cp1251;
      }
      body { font-family: "Arial Unicode MS", Arial, sans-serif; }
    <g:if test="${payorder.payway_id==1}">
      body { width: 210mm; margin-left: auto; margin-right: auto; /*border: 1px #efefef solid;*/ font-size: 10px; }
      table.invoice_bank_rekv { border-collapse: collapse; border: 1px solid; }
      table.invoice_bank_rekv > tbody > tr > td, table.invoice_bank_rekv > tr > td { border: 1px solid; }
      table.invoice_items { border: 1px solid; border-collapse: collapse;}
      table.invoice_items td, table.invoice_items th { border: 1px solid;}
    </g:if><g:else>    
      table { border-top: 2px solid #000; border-left: 2px solid #000 }
      table td { border-bottom: 2px solid #000; border-right: 2px solid #000 }
      td li { list-style: none none outside !important; }
    </g:else>
    </style>
  </head>
  <body style="width:600px">
  <g:if test="${payorder.payway_id==1}">
    <table width="600" cellpadding="0" cellspacing="0">
      <tr>
        <td width="100%">
          <div><g:rawHtml>${infotext?.itext?:''}</g:rawHtml></div>
        </td>
      </tr>
    </table>
    <table width="600" cellpadding="2" cellspacing="2" class="invoice_bank_rekv">
      <tr>
        <td style="min-height:6mm; height:auto; width: 150px;">
          <div>ИНН ${client?.inn?:''}</div>
        </td>
        <td style="min-height:6mm; height:auto; width: 150px;">
          <div>КПП ${client?.kpp?:''}</div>
        </td>
        <td rowspan="2" style="min-height:19mm; height:auto; vertical-align: top; width: 25mm;">
          <div>Сч. №</div>
        </td>
        <td rowspan="2" style="min-height:19mm; height:auto; vertical-align: top; width: 60mm;">
          <div>${client?.settlaccount?:''}</div>
        </td>
      </tr>
      <tr>
        <td colspan="2" style="min-height:13mm; height:auto;">
          <table border="0" cellpadding="0" cellspacing="0" style="height: 13mm; width: 300px;">
            <tr>
              <td valign="top">
                <div>${client?.payee?:''}</div>
              </td>
            </tr>
            <tr>
              <td valign="bottom" style="height: 3mm;">
                <div style="font-size: 10pt;">Получатель</div>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td colspan="2" rowspan="2" style="min-height:13mm; width: 300px;">
          <table width="300" border="0" cellpadding="0" cellspacing="0" style="height: 13mm;">
            <tr>
              <td valign="top">
                <div>${client?.bankname?:''}</div>
              </td>
            </tr>
            <tr>
              <td valign="bottom" style="height: 3mm;">
                <div style="font-size:10pt;">Банк получателя        </div>
              </td>
            </tr>
          </table>
        </td>
        <td style="min-height:7mm;height:auto; width: 25mm;">
          <div>БИK</div>
        </td>
        <td rowspan="2" style="vertical-align: top; width: 60mm;">
          <div style=" height: 7mm; line-height: 7mm; vertical-align: middle;">${client?.bik?:''}</div>
          <div>${client?.corraccount?:''}</div>
        </td>
      </tr>
      <tr>
        <td style="width: 25mm;">
          <div>Сч. №</div>
        </td>
      </tr>
    </table>
    <br/>

    <div style="font-weight: bold; font-size: 16pt; padding-left:5px;width:600px">
      Счет № ${payorder?.norder} от ${String.format('%td.%<tm.%<tY',payorder?.plat_date)}</div>
    <br/>

    <div style="background-color:#000000; width:600px; font-size:1px; height:2px;">&nbsp;</div>

    <table width="600">
      <tr>
        <td style="width: 30mm;">
          <div style=" padding-left:2px;">Плательщик:    </div>
        </td>
        <td>
          <div style="font-weight:bold;  padding-left:2px;">
            ${inrequest?.payer?(inrequest?.payer+', ИНН '+(inrequest?.payerinn?:'')+'/'+(inrequest?.payerokpo?:'')+', '+(inrequest?.payeraddress?:'')):'Укажите полной название продающей организации'}
          </div>
        </td>
      </tr>
    </table>

    <table class="invoice_items" width="600" cellpadding="2" cellspacing="2">
      <thead>
        <tr>
          <th style="width:13mm;">№</th>
          <!--th style="width:20mm;">Код</th -->
          <th>Услуга</th>
          <th style="width:17mm;">Ед.</th>
          <th style="width:20mm;">Кол-во</th>            
          <th style="width:27mm;">Цена</th>
          <th style="width:27mm;">Сумма</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td align="center">1</td>
          <!--td align="left">1</td-->
          <td align="left">${client?.paycomment?client?.paycomment.replace('[@NORDER]',payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',mbox?.date_end)):'Бронирование'}</td>
          <td align="left">шт</td>
          <td align="right">1</td>
          <td align="right">${payorder?.summa}</td>
          <td align="right">${payorder?.summa}</td>
        </tr>
      </tbody>
    </table>
    
    <table border="0" width="600" cellpadding="1" cellspacing="1">
      <tr>
        <td></td>
        <td style="width:27mm; font-weight:bold;  text-align:right;">Итого:</td>
        <td style="width:27mm; font-weight:bold;  text-align:right;">${payorder?.summa}</td>
      </tr>
    </table>

    <br />
    <div>Всего наименований 1 на сумму ${payorder?.summa} рублей.<br />${priceASstring}</div>
    <br /><br />
    <div style="background-color:#000000; width:600px; font-size:1px; height:2px;">&nbsp;</div>
    <br/>
    <div style="position:relative">
      <div>Руководитель ______________________ (${client.chief})</div>
      <div style="position:absolute;width:75px;height:40px;top:-20px;left:120px"><img height="35" src="${resource(dir:'images',file:'signature1.png',absolute:true)}" /></div>
    </div>
    <br/>
    <div style="position:relative">
      <div>Главный бухгалтер ______________________ (${client.accountant})</div>
      <div style="position:absolute;width:75px;height:40px;top:-10px;left:110px"><img height="25" src="${resource(dir:'images',file:'signature2.png',absolute:true)}" /></div>
    </div>
    <br/>
    <div style="position:relative">
      <div style="width: 85mm;text-align:center;">М.П.</div>
      <div style="position:absolute;width:135px;height:135px;top:-60px;left:90px"><img height="135" src="${resource(dir:'images',file:'stamp.png',absolute:true)}" /></div>
    </div>
    <br/>

    <div style="width:600px;text-align:left;font-size:10pt;"><g:rawHtml>${infotext?.itext2?:''}</g:rawHtml></div>
</g:if><g:else>
    <table width="690" cellpadding="5" cellspacing="0">
      <tr>
        <td width="190" valign="top" height="250" align="center">
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
            <b>Платеж:</b><br/>
            <font style="font-size:90%">${client?.paycomment?client?.paycomment.replace('[@NORDER]',payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',mbox?.date_end)):'_________________________________________________________________'}</font>
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
            <b>${payorder?.summa?payorder?.summa+' руб.':'______ руб.'}</b>
            <b>00</b> коп. &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br />&nbsp;<br /><br />
            Подпись:______________________ Дата: &quot;__&quot;&nbsp;_______&nbsp;&nbsp;${new Date().getYear()+1900} г.<br /><br />
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
            <b>Платеж:</b><br/>
            <font style="font-size:90%">${client?.paycomment?client?.paycomment.replace('[@NORDER]',payorder?.norder).replace('[@START]',String.format('%td.%<tm.%<tY',mbox?.date_start)).replace('[@END]',String.format('%td.%<tm.%<tY',mbox?.date_end)):'_________________________________________________________________'}</font>
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
            <b>${payorder?.summa?payorder?.summa+' руб.':'______ руб.'}</b>
            <b>00</b> коп. &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br />&nbsp;<br /><br />
            Подпись:______________________ Дата: &quot;__&quot;&nbsp;_______&nbsp;&nbsp;${new Date().getYear()+1900} г.<br /><br />
          </li>
        </td>
      </tr>
    </table>
</g:else>
  </body>
</html>
