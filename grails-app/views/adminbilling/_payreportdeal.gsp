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
      body { font-family: "Arial Unicode MS", Arial, sans-serif; }
      table { border-top: 2px solid #000; border-left: 1px solid #000; }
      table th { border-bottom: 2px solid #000; border-right: 1px solid #000 }
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000 }
    </style>
  </head>
  <body style="width:1020px">
  <g:if test="${!payreport}">
    <h1>Нет данных за указанный период</h1>
  </g:if><g:else>
    <div>
      <div style="float:left;font-size:9pt">Отчет по сделкам</div><br/>
      <div style="float:left;font-size:9pt">${accountData.payee}</div>
      <div style="clear:both;text-align:center"></div><br/><br/><br/>
      <table style="width:1020px;font-size:9pt">
        <thead>
          <tr>
            <th>Срок найма (начало)</th>
            <th>Срок найма (окончание)</th>
            <th>Наниматель (ФИО)</th>
            <th>Код нанимателя</th>
            <th>Наймодатель (ФИО)</th>
            <th>Код наймодателя</th>
            <th>№ договора найма</th>
            <th>Код объекта</th>
            <th>Сумма</th>
          </tr>
        </thead>
        <tbody>
        <g:each in="${payreport}" var="record">
          <tr>
            <td>${String.format('%td/%<tm/%<tY',record.fromdate)}</td>
            <td>${String.format('%td/%<tm/%<tY',record.todate)}</td>
            <td>${record.plat_name?:User.get(record.user_id)?.nickname}</td>
            <td>${record.user_id}</td>
            <td>${Client.get(record.client_id)?.payee?:User.findByClient_id(record.client_id)?.nickname}</td>
            <td>${User.findByClient_id(record.client_id)?.id}</td>
            <td>${record.norder}</td>
            <td>${record.home_id}</td>
            <td>${record.summa}</td>
          </tr>
        </g:each>
          <tr>
            <td colspan="8"></td>
            <td><b>ИТОГО</b></td>
          </tr>
          <tr>
            <td colspan="8"></td>
            <td>${total}</td>
          </tr>
        </tbody>
      </table><br/><br/><br/>
      <div style="float:left;font-size:10pt;padding-left:200px"><b>Главный бухгалтер: _________________________________ ${accountData.accountant}</b></div>
    </div>
  </g:else>
  </body>
</html>