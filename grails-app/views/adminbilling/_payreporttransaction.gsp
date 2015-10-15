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
  <g:if test="${!payreport&&!payreportWM}">
    <h1>Нет данных за указанный период</h1>
  </g:if><g:else>
    <div>
      <div style="float:left;font-size:9pt">Отчет по поступлению денежных средств от нанимателей</div><br/>
      <div style="float:left;font-size:9pt">${accountData.payee}</div>
      <div style="clear:both;text-align:center"></div><br/><br/><br/>
      <div style="float:left;font-size:9pt">Web Money</div>
      <div style="clear:both;text-align:center"></div>
      <table style="width:1020px;font-size:9pt">
        <thead>
          <tr>
            <th>Дата транзакции</th>
            <th>Наниматель (ФИО)</th>
            <th>% агрегатора</th>
            <th>Код нанимателя</th>
            <th>Договор найма</th>
            <th>Объект</th>
            <th>Начало</th>
            <th>Окончание</th>
            <th>Сумма</th>
            <th>Сумма комиссии агрегатора</th>
          </tr>
        </thead>
        <tbody>
        <g:each in="${payreportWM}" var="record">
          <tr>
            <td>${String.format('%td/%<tm/%<tY',record.plat_date)}</td>
            <td>${record.plat_name?:User.get(record.user_id)?.nickname}</td>
            <td>${record.persent}</td>
            <td>${record.user_id}</td>
            <td>${record.norder}</td>
            <td>${record.home_id}</td>
            <td>${String.format('%td/%<tm/%<tY',record.fromdate)}</td>
            <td>${String.format('%td/%<tm/%<tY',record.todate)}</td>
            <td>${record.summa}</td>
            <td>${Math.round(record.summa*record.persent)/100}</td>
          </tr>
        </g:each>
          <tr>
            <td colspan="8"></td>
            <td><b>ИТОГО</b></td>
            <td></td>
          </tr>
          <tr>
            <td colspan="8"></td>
            <td>${wmtotal}</td>
            <td>${wmcomtotal}</td>
          </tr>
        </tbody>
      </table>
      <div style="clear:both;text-align:center"></div><br/><br/><br/>
      <div style="float:left;font-size:9pt">Безналичный расчет</div>
      <div style="clear:both;text-align:center"></div>
      <table style="width:1020px;font-size:9pt">
        <thead>
          <tr>
            <th>Дата транзакции</th>
            <th>Наниматель (ФИО)</th>
            <th>% агрегатора</th>
            <th>Код нанимателя</th>
            <th>Договор найма</th>
            <th>Объект</th>
            <th>Начало</th>
            <th>Окончание</th>
            <th>Сумма</th>
            <th>Сумма комиссии агрегатора</th>
          </tr>
        </thead>
        <tbody>
        <g:each in="${payreport}" var="record">
          <tr>
            <td>${String.format('%td/%<tm/%<tY',record.plat_date)}</td>
            <td>${record.plat_name?:User.get(record.user_id)?.nickname}</td>
            <td>${record.persent}</td>
            <td>${record.user_id}</td>
            <td>${record.norder}</td>
            <td>${record.home_id}</td>
            <td>${String.format('%td/%<tm/%<tY',record.fromdate)}</td>
            <td>${String.format('%td/%<tm/%<tY',record.todate)}</td>
            <td>${record.summa}</td>
            <td>${Math.round(record.summa*record.persent)/100}</td>
          </tr>
        </g:each>
          <tr>
            <td colspan="8"></td>
            <td><b>ИТОГО</b></td>
            <td></td>
          </tr>
          <tr>
            <td colspan="8"></td>
            <td>${total}</td>
            <td>${comtotal}</td>
          </tr>
        </tbody>
      </table><br/><br/><br/>
      <div style="float:left;font-size:10pt;padding-left:200px"><b>Главный бухгалтер: _________________________________</b></div>
    </div>
  </g:else>
  </body>
</html>