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
      body { font-family: "Arial Unicode MS", Arial, sans-serif; }
      table { border-top: 2px solid #000; border-left: 1px solid #000; }
      table th { border-bottom: 2px solid #000; border-right: 1px solid #000 }
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000 }
    </style>
  </head>
  <body style="width:600px">
    <h4>Отчет по движению денег в разрезе заказов с ${String.format('%tF',reportdate_from)} по ${String.format('%tF',reportdate_to)}</h4>
    <table>
      <thead>
        <th width="110">Заказ</th>
        <th width="100">Тип</th>
        <th width="100">Сумма</th>
        <th width="200">Дата</th>
      </thead>
      <tbody>
      <g:each in="${payreport}" >
      <tr>
        <td>${Payorder.get(it.payorder_id)?.norder}</td>
        <td>${it.paytype_id==8?'расход':'приход'}</td>
        <td>${it.summa}</td>
        <td>${String.format('%tF %<tT',it.moddate)}</td>
      </tr>
      </g:each>
      </tbody>
    </table>
  </body>
</html>