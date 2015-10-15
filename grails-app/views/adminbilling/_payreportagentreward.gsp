<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>${infotext?.title?infotext.title:''}</title>
    <meta name="keywords" content="${infotext?.keywords?infotext.keywords:''}" />
    <meta name="description" content="${infotext?.description?infotext.description:''}" />
    <style type="text/css">
      @font-face {
        src: url(http://staytoday.ru:8080/fonts/arial.ttf);
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: cp1251;
      }
      @page {
        size: 29.7cm 21cm;
      }
    <g:each in="${payreports}" var="reports" status="i">
    <g:if test="${i}">
      @page report${reports.orders[0].client_id} {size: 29.7cm 21cm;}
      .report${reports.orders[0].client_id} { page: report${reports.orders[0].client_id} }
    </g:if>
    </g:each>
      body { font-family: "Arial Unicode MS", Arial, sans-serif; }
      table { border-top: 2px solid #000; border-left: 1px solid #000; }
      table th { border-bottom: 2px solid #000; border-right: 1px solid #000 }
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000 }
    </style>
  </head>
  <body style="width:1020px">
  <g:each in="${payreports}" var="reports" status="i">
    <div class="report${reports.orders[0].client_id}">
      <div style="float:right;width:200px;font-size:9pt">Приложение №2 к Публичной оферте (Агентскому договору) учетный № принципала - ${User.findByClient_id(reports.orders[0].client_id)?.id}</div>
      <div style="clear:both;text-align:center"><b>АКТ НА АГЕНТСКОЕ ВОЗНАГРАЖДЕНИЕ</b></div>
      <div style="clear:both;text-align:center;font-size:10pt">за период с "${reportStart.get(Calendar.DATE)}" ${reportMonth} ${reportYear} г. по "${reportEnd.get(Calendar.DATE)}" ${reportMonth} ${reportYear} г.</div>
      <div style="float:left;font-size:10pt">г.Санкт-Петербург</div>
      <div style="float:right;font-size:10pt">${reportEnd.get(Calendar.DATE)} ${reportMonth} ${reportYear} г.</div>
      <div style="clear:both;text-align:center"></div><br/>
      <div style="clear:both;font-size:10pt">Настоящий Акт составлен в подтверждение того, что "Агентом" ${accountData.payee} в лице Генерального директора ${accountData.chief} оказаны, а "Принципалом" ${Client.get(reports.orders[0].client_id)?.payee?:User.findByClient_id(reports.orders[0].client_id)?.nickname} приняты услуги по организации и сдачи внаем объектов недвижимого имущества  и принятию денежных средств от Нанимателей за отчетный период:</div><br/>
      <table style="width:1020px;font-size:9pt">
        <thead>
          <tr>
            <th rowspan="2" width="30">№ п/п</th>
            <th rowspan="2">Наниматель</th>
            <th rowspan="2">Код нанимателя</th>
            <th rowspan="2">№ договора найма</th>
            <th rowspan="2">Код объекта</th>
            <th colspan="2">Срок найма</th>
            <th rowspan="2">Сумма по договору (в руб.)</th>
            <th rowspan="2">Дата транзакции</th>
            <th rowspan="2" colspan="2">Дата поступления денежных средств и № документа</th>
            <th rowspan="2" width="30">Сумма</th>
            <th rowspan="2" width="30">Вознаграждение агента (%)</th>
            <th rowspan="2" width="30">Вознаграждение агента (руб.)</th>
          </tr>
          <tr>
            <th width="60">с</th>
            <th width="60">по</th>
          </tr>
          <tr>
            <th>1</th>
            <th>2</th>
            <th>3</th>
            <th>4</th>
            <th>5</th>
            <th>6</th>
            <th>7</th>
            <th>8</th>
            <th>9</th>
            <th colspan="2">10</th>
            <th>11</th>
            <th>12</th>
            <th>13</th>
          </tr>
        </thead>
        <tbody>
        <g:each in="${reports.orders}" var="record" status="j">
          <tr>
            <td>${j+1}</td>
            <td>${record.plat_name?:User.get(reports.orders[0].user_id)?.nickname}</td>
            <td>${record.user_id}</td>
            <td>${record.norder}</td>
            <td>${record.home_id}</td>
            <td>${String.format('%tm/%<td/%<tY',Trip.findByPayorder_id(record.id)?.fromdate)}</td>
            <td>${String.format('%tm/%<td/%<tY',Trip.findByPayorder_id(record.id)?.todate)}</td>
            <td>${record.summa_deal}</td>
            <td>${String.format('%tm/%<td/%<tY',record.plat_date)}</td>
            <td>${record.in_paydate?String.format('%tm/%<td/%<tY',record.in_paydate):'нет'}</td>
            <td>${record.in_paynumber?:'нет'}</td>
            <td>${record.summa}</td>
            <td>${commissionPersent}</td>
            <td>${record.summa_com}</td>
          </tr>
        </g:each>
          <tr>
            <td></td>
            <td colspan="2"><b>ИТОГО</b></td>
            <td colspan="4" style="border:none;border-right:1px solid #000"></td>
            <td>${reports.summadealtotal}</td>
            <td colspan="3" style="border:none;border-right:1px solid #000"></td>
            <td>${reports.summatotal}</td>
            <td colspan="2" style="border:none"></td>
          </tr>
          <tr>
            <td colspan="14" style="border:none"></td>
          </tr>
          <tr>
            <td colspan="11" style="border:none" align="right">из них поступило на расчетный счет Агента&nbsp;&nbsp;</td>
            <td style="border-top:1px solid #000;border-left:1px solid #000">${reports.summatotal}</td>
            <td colspan="2" style="border:none"></td>
          </tr>
          <tr>
            <td colspan="13" style="border:none" align="right">Сумма агентского вознаграждения к начислению:&nbsp;&nbsp;</td>
            <td style="border-top:1px solid #000;border-left:1px solid #000">${reports.comsumma}</td>
          </tr>
        </tbody>
      </table><br/>
      <div style="clear:both;font-size:10pt">Всего оказанно услуг ${reports.orders.size()} на сумму ${reports.comsumma} рублей. Вышеперечисленные услуги оказаны Агентом полностью и в срок. Агентом перечислено Принципалу сумма денежных средств в размере ${reports.summafin} руб. (${reports.summafinWords}). Агентом удержано агентское вознаграждение в размере ${reports.comsumma} руб. (${reports.comsummaWords}). Принципал претензий по объему, качеству и срокам оказания услуг претензий не имеет.</div><br/>
      <div style="float:left;font-size:10pt"><b>Принципал: __________________  /${Client.get(reports.orders[0].client_id)?.payee?:User.findByClient_id(reports.orders[0].client_id)?.nickname}/</b></div>
      <div style="float:right;font-size:10pt;padding-right:300px"><b>Агент: __________________/${accountData.chief}/</b></div>
    </div>
  </g:each>
  <g:if test="${!payreports}">
    <h1>Нет данных за указанный месяц</h1>
  </g:if>
  </body>
</html>