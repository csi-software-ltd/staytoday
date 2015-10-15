<html>
  <head>
    <title>${infotext?.title?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <g:javascript library="jquery-1.8.3" />
<g:if test='${false}'>
    <g:javascript library="jquery.colorbox.min" />
</g:if>
    <g:javascript>
    var _arr_months = new Array('нулября', 'января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря');
    function initialize() {
      $('#pricephrase').html('<b>Итого: ' + number_to_string(${trip.price_rub}) + '</b>');
<g:if test='${false}'>
      $('#edit_data_link').colorbox({
        inline: true,        
        href: '#edit_data',
        scrolling: false,
        onLoad: function(){
          $('#edit_data').show();
        },
        onCleanup: function(){
          $('#edit_data').hide();
        }
      });
      $('#edit_price_link').colorbox({
        inline: true,        
        href: '#edit_price',
        scrolling: false,
        onLoad: function(){
          $('#edit_price').show();
        },
        onCleanup: function(){
          $('#edit_price').hide();
        }
      });
</g:if>
    }
    function printblank() {
      $('#buttons').hide();
      setTimeout('window.print()', 100);
    }
    function showbuttons() {
      $('#buttons').show();
    }
    function calcSum() {
      if(!isNaN(parseInt($('#price').html()))) {
        $('#price').html($('#price').html()?$('#price').html():'');
        $('#price_sum').html(parseInt($('#price').html())?parseInt($('#price').html())*(parseInt(document.getElementById('amount').innerHTML)?parseInt(document.getElementById('amount').innerHTML):1):'----');
      }
      var prc_toString = parseInt($('#price_sum').html());
      for (var i=2; i<6; ++i){
        if(!isNaN(parseInt($('#price'+i).html()))) {
          $('#price'+i).html(parseInt($('#price'+i).html())?parseInt($('#price'+i).html()):'');
          $('#price_sum'+i).html(parseInt($('#price'+i).html())?parseInt($('#price'+i).html())*(parseInt(document.getElementById('amount'+i).innerHTML)?parseInt(document.getElementById('amount'+i).innerHTML):1):'');
          prc_toString += parseInt($('#price_sum'+i).html());
        } else {
          $('#price'+i).html('');
          $('#price_sum'+i).html('');
        }
      }
      $('#pricephrase').html('<b>Итого: ' + number_to_string(prc_toString) + '</b>');
    }
<g:if test='${false}'>
    function editpriceprocess() {
      document.getElementById('name').innerHTML=document.getElementById('name_ed').value?document.getElementById('name_ed').value:''
      document.getElementById('units').innerHTML=document.getElementById('units_ed').value?document.getElementById('units_ed').value:''
      document.getElementById('amount').innerHTML=document.getElementById('amount_ed').value?document.getElementById('amount_ed').value:''
      if(!isNaN(parseInt($('#price_ed').val()))) {
        $('#price').html($('#price_ed').val()?$('#price_ed').val():'----');
        $('#price_sum').html(parseInt($('#price_ed').val())?parseInt($('#price_ed').val())*(parseInt(document.getElementById('amount_ed').value)?parseInt(document.getElementById('amount_ed').value):1):'----');
      }
      var prc_toString = parseInt($('#price_sum').html());
      for (var i=2; i<6; ++i){
        document.getElementById('name'+i).innerHTML=document.getElementById('name_ed'+i).value?document.getElementById('name_ed'+i).value:'';
        document.getElementById('units'+i).innerHTML=document.getElementById('units_ed'+i).value?document.getElementById('units_ed'+i).value:'';
        document.getElementById('amount'+i).innerHTML=document.getElementById('amount_ed'+i).value?document.getElementById('amount_ed'+i).value:'';
        if(!isNaN(parseInt($('#price_ed'+i).val()))) {
          $('#price'+i).html(parseInt($('#price_ed'+i).val())?parseInt($('#price_ed'+i).val()):'----');
          $('#price_sum'+i).html(parseInt($('#price_ed'+i).val())?parseInt($('#price_ed'+i).val())*(parseInt(document.getElementById('amount_ed'+i).value)?parseInt(document.getElementById('amount_ed'+i).value):1):'----');
          prc_toString += parseInt($('#price_sum'+i).html());
        } else {
          $('#price'+i).html('');
          $('#price_sum'+i).html('');
        }
      }
      $('#pricephrase').html('<b>Итого: ' + number_to_string(prc_toString) + '</b>');
      $.colorbox.close();
    }
    function editprocess() {
      $('#dsoNumber').html($('#dsoNumber_ed').val()?$('#dsoNumber_ed').val():'______________________________');
      $('#priceNumber').html($('#priceNumber_ed').val()?$('#priceNumber_ed').val():'________________________');
      $('#guestname').html($('#guestname_ed').val()?$('#guestname_ed').val():'_________________________________________________________');
      $('#position').html($('#position_ed').val()?$('#position_ed').val():'_______________________');
      $('#fname').html($('#fname_ed').val()?$('#fname_ed').val():'____________________________________');
      $('#position2').html($('#position2_ed').val()?$('#position2_ed').val():'_______________________');
      $('#fname2').html($('#fname2_ed').val()?$('#fname2_ed').val():'____________________________________');

      $('#pricedate').html('&quot;'+$('#pricedate_ed_day').val()+'&quot;&nbsp;'+_arr_months[$('#pricedate_ed_month').val()]+'&nbsp;&nbsp;'+$('#pricedate_ed_year').val()+' г.');
      $('#fromdate').html($('#fromdate_ed_day').val()+'.'+$('#fromdate_ed_month').val()+'.'+$('#fromdate_ed_year').val());
      $('#todate').html($('#todate_ed_day').val()+'.'+$('#todate_ed_month').val()+'.'+$('#todate_ed_year').val());

      $.colorbox.close();
    }
</g:if>
    function number_to_string(_number) {
      var _arr_numbers = new Array();
      _arr_numbers[1] = new Array('', 'один', 'два', 'три', 'четыре', 'пять', 'шесть', 'семь', 'восемь', 'девять', 'десять', 'одиннадцать', 'двенадцать', 'тринадцать', 'четырнадцать', 'пятнадцать', 'шестнадцать', 'семнадцать', 'восемнадцать', 'девятнадцать');
      _arr_numbers[2] = new Array('', '', 'двадцать', 'тридцать', 'сорок', 'пятьдесят', 'шестьдесят', 'семьдесят', 'восемьдесят', 'девяносто');
      _arr_numbers[3] = new Array('', 'сто', 'двести', 'триста', 'четыреста', 'пятьсот', 'шестьсот', 'семьсот', 'восемьсот', 'девятьсот');

      function number_parser(_num, _desc) {
        var _string = '';
        var _num_hundred = '';
        if (_num.length == 3) {
          _num_hundred = _num.substr(0, 1);
          _num = _num.substr(1, 3);
          _string = _arr_numbers[3][_num_hundred] + ' ';
        }
        if (_num < 20) _string += _arr_numbers[1][parseFloat(_num)] + ' ';
        else {
          var _first_num = _num.substr(0, 1);
          var _second_num = _num.substr(1, 2);
          _string += _arr_numbers[2][_first_num] + ' ' + _arr_numbers[1][_second_num] + ' ';
        }
        switch (_desc){
          case 0:
            var _last_num = parseFloat(_num.substr(-1));
            if (_num < 20 && _num > 10) _string += 'рублей ';
            else if (_last_num == 1) _string += 'рубль';
            else if (_last_num > 1 && _last_num < 5) _string += 'рубля';
            else _string += 'рублей';
            break;
          case 1:
            var _last_num = parseFloat(_num.substr(-1));
            if (_num < 20 && _num > 10) _string += 'тысяч ';
            else if (_last_num == 1) _string += 'тысяча ';
            else if (_last_num > 1 && _last_num < 5) _string += 'тысячи ';
            else _string += 'тысяч ';
            _string = _string.replace('один ', 'одна ');
            _string = _string.replace('два ', 'две ');
            break;
          case 2:
            var _last_num = parseFloat(_num.substr(-1));
            if (_num < 20 && _num > 10) _string += 'миллионов ';
            else if (_last_num == 1) _string += 'миллион ';
            else if (_last_num > 1 && _last_num < 5) _string += 'миллиона ';
            else _string += 'миллионов ';
            break;
          case 3:
            var _last_num = parseFloat(_num.substr(-1));
            if (_num < 20 && _num > 10) _string += 'миллиардов ';
            else if (_last_num == 1) _string += 'миллиард ';
            else if (_last_num > 1 && _last_num < 5) _string += 'миллиарда ';
            else _string += 'миллиардов ';
            break;
        }
        _string = _string.replace('  ', ' ');
        return _string;
      }
      function decimals_parser(_num) {
        var _first_num = _num.substr(0, 1);
        var _second_num = parseFloat(_num.substr(1, 2));
        var _string = ' ' + _first_num + _second_num;
        if (_second_num == 1) _string += ' копейка';
        else if (_second_num > 1 && _second_num < 5) _string += ' копейки';
        else _string += ' копеек';
        return _string;
      }
      if (!_number || _number == 0) return false;
      if (typeof _number !== 'number') {
        _number = _number.replace(',', '.');
        _number = parseFloat(_number);
        if (isNaN(_number)) return false;
      }
      _number = _number.toFixed(2);
      if(_number.indexOf('.') != -1) {
        var _number_arr = _number.split('.');
        var _number = _number_arr[0];
        var _number_decimals = _number_arr[1];
      }
      var _number_length = _number.length;
      var _string = '';
      var _num_parser = '';
      var _count = 0;
      for (var _p = (_number_length - 1); _p >= 0; _p--) {
        var _num_digit = _number.substr(_p, 1);
        _num_parser = _num_digit +  _num_parser;
        if ((_num_parser.length == 3 || _p == 0) && !isNaN(parseFloat(_num_parser))) {
          _string = number_parser(_num_parser, _count) + _string;
          _num_parser = '';
          _count++;
        }
      }
      if (_number_decimals) _string += decimals_parser(_number_decimals);
      return _string;
    }
    </g:javascript>
    <style type="text/css">
    body, table { font: normal normal 14px Helvetica,Tahoma,Geneva,Arial,sans-serif; }
    h2 { font: normal bold 16px/16px Helvetica,Tahoma,Geneva,Arial,sans-serif; color: #3f5765; }
    input[type="text"], select {
      background: #fff;
      background: -webkit-gradient(linear,center top,center bottom,from(#e8e8e8),to(#ffffff));
      background: -moz-linear-gradient(top,#e8e8e8,#ffffff);
      background: -o-linear-gradient(top, #e8e8e8, #ffffff);
      background: -ms-linear-gradient(#e8e8e8, #ffffff);
      background: linear-gradient(top, #e8e8e8, #ffffff);		
      filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffe8e8e8',endColorstr='#ffffffff',GradientType=0);	    
      border: 1px solid #cfcfcf;
      border-radius: 5px;	
      padding: 5px 8px 6px 8px;
      font: normal normal 12px/12px Arial;
      color: #333333;
      outline: none;
    }
<g:if test='${false}'>
    #colorbox, #cboxOverlay, #cboxWrapper { left: 0; overflow: hidden; position: absolute; top: 0; z-index: 999; }    
    #cboxOverlay { background: none repeat scroll 0 0 #000000; width: 100%; height: 100%; position: fixed; }
    #cboxMiddleLeft, #cboxBottomLeft { clear: left; }
    #cboxContent { overflow: hidden; position: relative; background: none repeat scroll 0 0 #FFFFFF; }
    #cboxLoadedContent { overflow: visible !important; margin-bottom: 28px; }
    #cboxTitle { display: none; }
    #cboxLoadingOverlay { left: 0; position: absolute; top: 0; width: 100%; }
    #cboxTopLeft, #cboxTopRight, #cboxBottomLeft, #cboxBottomRight { height: 21px; width: 21px; }
    #cboxMiddleLeft, #cboxMiddleRight { width: 21px; }
    #cboxTopCenter, #cboxBottomCenter { height: 21px;}
    #cboxCurrent { background: none repeat scroll 0 0 rgba(255, 255, 255, 0.7); padding: 2%; left: 0px; bottom: 5px; position: absolute; width: 96%; z-index: 2; display: block; color: #000; zoom: 1; }    
    #cboxClose { bottom: 0; height: 25px; position: absolute; right: 0; text-indent: -9999px; width: 25px; cursor: pointer; }
    #cboxClose.hover { background-position: -25px -25px; }
    #colorbox #cboxContent {
      background: -moz-linear-gradient(center top , #FFFFFF, #EFEFEF) repeat scroll 0 0 transparent;	
      background: -webkit-gradient(linear,center top,center bottom,from(#FFFFFF),to(#EFEFEF));
      background: -o-linear-gradient(top, #FFFFFF, #EFEFEF);
      background: -ms-linear-gradient(#FFFFFF, #EFEFEF);
      background: linear-gradient(top, #FFFFFF, #EFEFEF);
      filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffFFFFFF',endColorstr='#ffEFEFEF',GradientType=0);
      border: 2px solid #000000;
      border-radius: 6px;
      box-shadow: 0 1px 0 rgba(255, 255, 255, 0.8) inset, 0 -1px 0 rgba(255, 255, 255, 0.4) inset, 0 2px 8px 0 rgba(0, 0, 0, 0.9);
      overflow: visible;
      padding: 0;
    }
    #colorbox #cboxLoadedContent { margin-bottom: 0; }    
    #colorbox #cboxClose { background: url(${resource(dir:'images',file:'close.png')}) no-repeat scroll 0 0 transparent; height: 27px; left: -10px; top: -11px; width: 26px; }
    #colorbox #cboxTopLeft, #colorbox #cboxTopCenter, #colorbox #cboxTopRight, #colorbox #cboxMiddleLeft, #colorbox #cboxMiddleRight, 
    #colorbox #cboxBottomLeft, #colorbox #cboxBottomCenter, #colorbox #cboxBottomRight { visibility: hidden; }
    #colorbox .cboxLoading { background: url(${resource(dir:'images',file:'spinner.gif')}) no-repeat scroll center center transparent; height: 100%; width: 100%; }
    #cboxLoadedContent .new-modal {
      background: -moz-linear-gradient(center top , #FFFFFF, #F5F5F5) repeat scroll 0 0 transparent;	
      background: -webkit-gradient(linear,center top,center bottom,from(#FFFFFF),to(#F5F5F5));
      background: -o-linear-gradient(top, #FFFFFF, #F5F5F5);
      background: -ms-linear-gradient(#FFFFFF, #F5F5F5);
      background: linear-gradient(top, #FFFFFF, #F5F5F5);
      filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffFFFFFF',endColorstr='#ffF5F5F5',GradientType=0);	
      border-radius: 10px;
      color: #393C3D;
      width: 100%;
    }
    #cboxLoadedContent .new-modal .segment, #cboxLoadedContent .new-modal > h2 { border-bottom: 1px solid #D5D5D5; border-top: 1px solid #FFFFFF; padding: 15px; }
    #cboxLoadedContent .new-modal .segment h2 { margin: 0; padding: 0; }    
    #cboxLoadedContent .new-modal .segment.nopadding { padding: 5px; }
    #cboxLoadedContent .new-modal .segment:first-child, #cboxLoadedContent .new-modal > h2:first-child { border-top: 0 none; border-top-left-radius: 10px; border-top-right-radius: 10px; }
    #cboxLoadedContent .new-modal .segment:last-child, #cboxLoadedContent .new-modal > h2:last-child { border-bottom: 0 none; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px; }
    #cboxLoadedContent .new-modal .segment.buttons {
      background: -moz-linear-gradient(center top , #EEEEEE, #DDDDDD) repeat scroll 0 0 transparent;
      background: -webkit-gradient(linear,center top,center bottom,from(#EEEEEE),to(#DDDDDD));
      background: -o-linear-gradient(top, #EEEEEE, #DDDDDD);
      background: -ms-linear-gradient(#EEEEEE, #DDDDDD);
      background: linear-gradient(top, #EEEEEE, #DDDDDD);
      filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffEEEEEE',endColorstr='#ffDDDDDD',GradientType=0);		
      padding: 15px;
      border-bottom-left-radius: 10px;
      border-bottom-right-radius: 10px;
    }
    #cboxLoadedContent .new-modal .segment.buttons .button-glossy { margin-right: 5px; }    
    #cboxLoadedContent .new-modal h2 { margin: 0; text-shadow: 0 1px 1px white; }
</g:if>
    .button-glossy.orange {	border: 1px solid #eb470b; background: #eb470b;
      background: -webkit-gradient(linear,center top,center bottom,from(#fe520d),to(#eb470b));
      background: -moz-linear-gradient(top,#fe520d,#eb470b);
      background: -o-linear-gradient(top, #fe520d, #eb470b);
      filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fffe520d',endColorstr='#ffeb470b',GradientType=0);
    }
    .button-glossy.lightblue { border: 1px solid #1A70FE;	background: #2477FE;
      background: -webkit-gradient(linear,center top,center bottom,from(#4D90FE),to(#2477FE));
      background: -moz-linear-gradient(top, #4D90FE, #2477FE);
      background: -o-linear-gradient(top, #4D90FE, #2477FE);
      filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff4D90FE',endColorstr='#ff2477FE',GradientType=0);
    }
    .button-glossy {
      display: inline-block;
      text-align: center;
      min-width: 5em;
      min-height: 2em;
      cursor: pointer;
      position: relative;
      border-radius: .3em;
      font: normal bold 14px/17px Helvetica,Tahoma,Geneva,Arial,sans-serif;
      padding: 0 1em;	
      color: white !important;
      text-shadow: 0 -1px 1px rgba(0,0,0,0.5);
      text-decoration: none;
      box-shadow: inset 0 0 .2em rgba(255,255,255,0.3),inset 0 0 .2em rgba(255,255,255,0.3),0 1px 2px rgba(0,0,0,0.3);
      -o-box-shadow: inset 0 0 .2em rgba(255,255,255,0.3),inset 0 0 .2em rgba(255,255,255,0.3),0 1px 2px rgba(0,0,0,0.3);
      -moz-box-shadow: inset 0 0 .2em rgba(255,255,255,0.3),inset 0 0 .2em rgba(255,255,255,0.3),0 1px 2px rgba(0,0,0,0.3);
      -webkit-box-shadow: inset 0 0 .2em rgba(255,255,255,0.3),inset 0 0 .2em rgba(255,255,255,0.3),0 1px 2px rgba(0,0,0,0.3);
    }
    .button-glossy:before {
      position: absolute;
      content: " ";
      display: block;
      width: 100%;
      left: 0;
      bottom: 0;
      height: 50%;
      border-bottom-right-radius: .3em;
      border-bottom-left-radius: .3em;
      background: rgba(0,0,0,0.1);
      background: -webkit-gradient(linear,center top,center bottom,from(rgba(0,0,0,0.06)),to(rgba(0,0,0,0.1)));
      background: -moz-linear-gradient(top,rgba(0,0,0,0.06),rgba(0,0,0,0.1));
      filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#0f000000',endColorstr='#19000000',GradientType=0);
    }
    .button-glossy:hover {
      box-shadow: inset 0 0 .2em rgba(255,255,255,0.3),inset 0 0 .2em rgba(255,255,255,0.3),0 1px 5px rgba(0,0,0,0.6);
      -o-box-shadow: inset 0 0 .2em rgba(255,255,255,0.3),inset 0 0 .2em rgba(255,255,255,0.3),0 1px 5px rgba(0,0,0,0.6);
      -moz-box-shadow: inset 0 0 .2em rgba(255,255,255,0.3),inset 0 0 .2em rgba(255,255,255,0.3),0 1px 5px rgba(0,0,0,0.6);
      -webkit-box-shadow: inset 0 0 .2em rgba(255,255,255,0.3),inset 0 0 .2em rgba(255,255,255,0.3),0 1px 5px rgba(0,0,0,0.6);
      text-decoration: none !important;
    }    
    </style>
    <r:layoutResources/>
  </head>
  <body onload="initialize();">
    <div style="width:720px" onclick="showbuttons()" contenteditable="true">
      <font style="font-size:90%">${client?.payee?:'________________________________________________'}</font>
      <strong>ИНН:</strong>${client?.inn?:'_______________________'}&nbsp;<br/>
      <font style="font-size:90%">${home?.address?:'________________________________________________'}</font>
      <font size="7">______________________________</font><br/><br/>
      <font style="font-size:90%">Номер документа строгой отчетности <b id="dsoNumber">______________________________</b></font><br/><br/>
      <div align="center">
        <b>Счет № <span id="priceNumber">________________________</span> от <span id="pricedate">&laquo;__&raquo;&nbsp;_______&nbsp;&nbsp;${String.format('%tY', new Date())} г.</span></b><br /><br />
      </div>
      <font style="font-size:90%">Гость: <span id="guestname">_________________________________________________________</span></font><br/><br/>
      <div align="right">
        <b>Заезд <span id="fromdate">${String.format('%td.%<tm.%<tY',trip.fromdate)}</span>, Выезд <span id="todate">${String.format('%td.%<tm.%<tY',trip.todate)}</span></b><br /><br />
      </div>
      <table width="720" cellpadding="0" cellspacing="0" rules="groups" frame="border">
        <thead>
          <tr height="40">
            <th>№</th>          
            <th>Виды платежей</th>
            <th>Единицы</th> 
            <th>Кол-во</th>     
            <th>Цена, руб</th>
            <th>Сумма, руб</th>
          </tr>
        </thead>
        <tbody>
          <tr align="center" style="height:30px">
            <td>1</td>
            <td id="name">Проживание</td>
            <td id="units">к/с</td>
            <td id="amount">1</td>
            <td id="price">${trip.price_rub}</td>
            <td id="price_sum">${trip.price_rub}</td>
          </tr>
          <g:each in="${(2..5)}">
          <tr align="center" style="height:30px">
            <td>${it}</td>
            <td id="name${it}">&nbsp;</td>
            <td id="units${it}">&nbsp;</td>
            <td id="amount${it}">&nbsp;</td>
            <td id="price${it}">&nbsp;</td>
            <td id="price_sum${it}">&nbsp;</td>
          </tr>
          </g:each>
        </tbody>
      </table></br>
      <div id="pricephrase"></div></br></br>
      <strong><span id="position">_______________________</span>&nbsp;&nbsp;&nbsp;&nbsp;___________________&nbsp;&nbsp;&nbsp;&nbsp;(<span id="fname">____________________________________</span>)</strong></br></br>
      <strong><span id="position2">_______________________</span>&nbsp;&nbsp;&nbsp;&nbsp;___________________&nbsp;&nbsp;&nbsp;&nbsp;(<span id="fname2">____________________________________</span>)</strong></br></br>
      <font size="7">______________________________</font><br/>
    </div><br/>
    <div id="buttons">
      <input type="button" class="button-glossy orange" value="Печать" onclick="printblank()">
      <input type="button" class="button-glossy lightblue" value="Рассчитать сумму" onclick="calcSum()">
<g:if test='${false}'>
      <input type="button" class="button-glossy lightblue" value="Редактировать" onclick="$('#edit_data_link').click();">
      <input type="button" class="button-glossy lightblue" value="Редактировать прайс" onclick="$('#edit_price_link').click();">
</g:if>
    </div>

<g:if test='${false}'>
    <a id="edit_data_link" style="display:none"></a>
    <div id="edit_data" class="new-modal" style="display:none">
      <h2 class="clearfix">Редактирование</h2>
      <div id="edit_data_segment" class="segment nopadding">
        <div id="edit_data_container" class="lightbox_filter_container">
          <table class="form s" width="510" cellpadding="5" cellspacing="0" border="0">
            <tr>
              <td><label for="dsoNumber_ed">Номер ДСО</label></td>
              <td><input type="text" id="dsoNumber_ed" value=""/></td>
            </tr>
            <tr>
              <td><label for="priceNumber">Номер счета</label></td>
              <td><input type="text" id="priceNumber_ed" value=""/></td>
            </tr>
            <tr>
              <td><label for="guestname">ФИО гостя</label></td>
              <td><input type="text" id="guestname_ed" value="" style="width:100%"/></td>
            </tr>
            <tr>
              <td><label for="price_ed">Должность</label></td>
              <td><input type="text" id="position_ed" value="" style="width:100%"/></td>
            </tr>
            <tr>
              <td><label for="price_ed">Расшифровка</label></td>
              <td><input type="text" id="fname_ed" value="" style="width:100%" /></td>
            </tr>
            <tr>
              <td><label for="price_ed">Должность2</label></td>
              <td><input type="text" id="position2_ed" value="" style="width:100%" /></td>
            </tr>
            <tr>
              <td><label for="price_ed">Расшифровка2</label></td>
              <td><input type="text" id="fname2_ed" value="" style="width:100%"/></td>
            </tr>
            <tr>
              <td><label for="pricedate_ed">Дата счета</label></td>
              <td><g:datePicker name="pricedate_ed" value="${new Date()}" precision="day" years="${(new Date()).getYear()+1899..(new Date()).getYear()+1901}"/></td>
            </tr>
            <tr>
              <td><label for="fromdate_ed">Дата заезда</label></td>
              <td><g:datePicker name="fromdate_ed" value="${trip.fromdate}" precision="day" years="${trip.fromdate.getYear()+1899..trip.fromdate.getYear()+1901}"/></td>
            </tr>
            <tr>
              <td><label for="todate_ed">Дата выезда</label></td>
              <td><g:datePicker name="todate_ed" value="${trip.todate}" precision="day" years="${trip.todate.getYear()+1899..trip.todate.getYear()+1901}"/></td>
            </tr>
          </table>
        </div>
      </div>
      <div class="segment buttons">
        <input type="button" class="button-glossy orange" value="Сохранить" onclick="editprocess()"/>
      </div>
    </div>

    <a id="edit_price_link" style="display:none"></a>
    <div id="edit_price" class="new-modal" style="display:none">
      <h2 class="clearfix">Редактирование</h2>
      <div id="edit_price_segment" class="segment nopadding">
        <div id="edit_price_container" class="lightbox_filter_container">
      <table width="100%" cellpadding="0" cellspacing="0" rules="groups" frame="border">
        <thead>
          <tr height="40">
            <th>№</th>
            <th>Виды платежей</th>
            <th>Единицы</th>
            <th>Кол-во</th>
            <th>Цена, руб</th>
          </tr>
        </thead>
        <tbody>
          <tr align="center" style="height:30px">
            <td>1</td>
            <td><input type="text" id="name_ed" value="Проживание"/></td>
            <td><input type="text" id="units_ed" value="к/с"/></td>
            <td><input type="text" id="amount_ed" value="1"/></td>
            <td><input type="text" id="price_ed" value="${trip.price_rub}"/></td>
          </tr>
          <g:each in="${(2..5)}">
          <tr align="center" style="height:30px">
            <td>${it}</td>
            <td><input type="text" id="name_ed${it}" value=""/></td>
            <td><input type="text" id="units_ed${it}" value=""/></td>
            <td><input type="text" id="amount_ed${it}" value=""/></td>
            <td><input type="text" id="price_ed${it}" value=""/></td>
          </tr>
          </g:each>
        </tbody>
      </table>
        </div>
      </div>
      <div class="segment buttons">
        <input type="button" class="button-glossy orange" value="Сохранить" onclick="editpriceprocess()"/>
      </div>
    </div>
</g:if>
  <r:layoutResources/>
  </body>
</html>
