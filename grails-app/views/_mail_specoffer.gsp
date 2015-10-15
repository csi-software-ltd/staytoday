<%@ page contentType="text/html"%>
<html>
  <head>    
    <meta http-equiv="content-language" content="ru" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  </head>
  <body>
    <div style="padding:5px;background:#dce8f6">
      <table width="730" cellspacing="0" cellpadding="0" border="0" align="center" style="margin:0 auto">
        <tr>
          <td colspan="2" align="right" valign="middle" style="border-bottom:1px dotted #A8A8A8">
            <p style="padding:5px 14px">
              <a style="outline:none;color:#FF530D;font:normal 12px/12px Tahoma,Arial" href="http://staytoday.ru/index/specoffer">Посмотреть письмо как веб-страницу</a>
            </p> 
          </td>
        </tr>
        <tr>
          <td width="180" rowspan="2" valign="middle">
            <a style="outline:none" title="StayToday — главная страница" href="http://staytoday.ru">
              <img width="100" src="http://staytoday.ru/images/logo.png" vspace="8" hspace="14" alt="На главную" border="0" />
            </a>              
          </td>
          <td width="550" align="right">
            <table cellpadding="0" cellspacing="0" border="0" align="right">
              <tr valign="middle">
                <td style="padding:8px 14px;font:normal bold 12px/24px Tahoma,Arial" align="center">
                  <a style="color:#3F5765" href="http://staytoday.ru/personal/index">Войти</a>
                </td>                                                      
                <td style="padding:8px 14px;font:normal bold 12px/24px Tahoma,Arial" align="center">
                  <a style="color:#FF530D" href="http://staytoday.ru/help/faq">Помощь</a>
                </td>
                <td style="padding:8px 14px;font:normal bold 12px/24px Tahoma,Arial" align="center">
                  <a style="color:#FF530D" href="http://staytoday.ru/cities">Поиск жилья</a>
                </td>
                <td style="padding:8px 14px;font:normal bold 12px/24px Tahoma,Arial" align="center">
                  <a style="color:#FF530D" href="http://staytoday.ru/help/guestbook">Связь</a>
                </td>                    
              </tr>
            </table>              
          </td>
        </tr>
        <tr>
          <td height="54" align="right" valign="middle">
            <p style="padding:8px 14px;color:#3F5765;font:normal 26px/26px Tahoma;letter-spacing:-1px">
              жильё посуточно для отдыха и путешествий
            </p>
          </td>
        </tr>
        <tr>
          <td colspan="2">            
            <div style="background:#fff;border-radius:0.3em;border:1px solid #ccc">
            <!--<g:rawHtml>${itext}</g:rawHtml>-->
            <g:if test="${popcity}">            
              <div style="padding:10px 0;text-align:justify">
                <h2 style="background:url('http://staytoday.ru/images/bg_line.png') no-repeat 0 5px;padding-left:20px;margin:10px 12px;color:#3F5765;font:bold 16px/20px Arial;letter-spacing:-1px">
                  Специальные предложения
                </h2>
                <table width="730" cellpadding="5" cellspacing="0" border="0">
                <g:each in="${popcity}" var="record" status="i">
                  <g:if test="${(i % 3)==0}"><tr></g:if>
                    <td style="width:33%;border:6px solid #fff">
                      <div style="width:222px">
                        <div style="width:222px;height:162px;border:1px solid #ccc">
                          <div style="width:220px;height:160px;border:1px solid #fff;background:url(${resource(dir:'images',file:record?.picture?'cities/'+record.picture:'default-picture.png',absolute:true)}) no-repeat">
                            <div style="margin-top:18px;margin-left:18px;background:#fff;color:#333;display:inline-block;opacity:0.8;padding:0 9px;font:bold 14px/30px Arial">
                              Цены на аренду жилья от                                    
                            </div>
                            <div style="float:right;margin-top:72px;display:inline-block;background:#FF530D;color:#fff;height:40px;opacity:0.8;padding:0 4px;width:auto;font:normal 30px/40px Arial;letter-spacing:-2px"> 
                              ${Math.round(minPrice[i] / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
                            </div>
                          </div>                            
                        </div>
                        <div style="display:inline-block">
                          <h2 style="font:normal 20px/20px Tahoma,Arial;letter-spacing:0;margin:10px 0">
                            <a style="font:normal 20px/20px Tahoma,Arial;letter-spacing:0;margin:-2px 0 0;color:#3F5765" href="<g:createLink mapping='mainpage' base='${'http://'+record?.domain}'/>">
                              ${record?.name}
                            </a>
                          </h2>
                          <ul style="list-style:none none outside;color:#787878;font:12px/12px Tahoma;width:220px;padding:0;margin:0">
                            <li style="clear:both;margin-left:0;line-height:22px;vertical-align:top">
                              <b style="color:#333">Средние цены на аренду жилья</b>
                            </li>
                            <li style="clear:both;margin-left:0;line-height:22px;vertical-align:top;border-bottom:1px dotted #A8A8A8;padding:3px 0">
                              <span style="color:#3F5765;display:block;float:right;font:bold 12px/22px Tahoma;margin:0;padding:0 7px;text-align:center">${Math.round(avgPrice1[i] / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                              <a style="color:#FF530D;outline:none" href="<g:createLink mapping='hSearchRoomDomain' params='${[bedroom:1,type_url:'flats']}' base='${'http://'+record?.domain}' />">
                                однокомнатные
                              </a>
                            </li>
                            <li style="clear:both;margin-left:0;line-height:22px;vertical-align:top">
                              <span style="color:#3F5765;display:block;float:right;font:bold 12px/22px Tahoma;margin:0;padding:0 7px;text-align:center">${Math.round(avgPrice2[i] / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                              <a style="color:#FF530D;outline:none" href="<g:createLink mapping='hSearchRoomDomain' params='${[bedroom:2,type_url:'flats']}' base='${'http://'+record?.domain}' />">
                                двухкомнатные
                              </a>
                            </li>
                          </ul>                              
                        </div>
                      </div>
                    </td>
                  <g:if test="${((i % 3)==0) && (poprecords.size() == 1)}">
                    <td style="width:33%">&nbsp;</td><td style="width:33%">&nbsp;</td>
                  </g:if><g:elseif test="${((i % 3)==1) && (poprecords.size() == 2)}">
                    <td style="width:33%">&nbsp;</td>
                  </g:elseif><g:elseif test="${(i % 3)==2}">
                  </tr>
                    <g:if test="${i+1!=poprecords.size()}">
                  <tr><td colspan="3" style="background:url('http://staytoday.ru/images/bg_line.png') repeat-x left top transparent;height:1px;line-height:1px;padding:0">&nbsp;</td></tr>
                    </g:if>
                  </g:elseif>                  
                </g:each>
                </table>
              </div>
            </g:if>
            <g:if test="${notice}">              
              <g:each in="${notice}" var="it" status="i">
              <div style="clear:both;min-height:68px;margin:10px;padding:10px;border:1px solid #EDD89F;background:#FFF3D0;border-radius:4px;box-shadow:inset 0 0 2px 1px #fff,0 1px 1px rgba(0,0,0,0.05);font:normal 13px/18px Tahoma">
                <a style="outline:none;text-decoration:none" href="${serverUrl.toString()+'/'+it?.url}" title="${it?.title}">                  
                  <img width="68" height="68" style="float:left" src="${resource(dir:'images',file:(it?.image)?'anonses/'+it?.image:'user-default-picture.png',absolute:true)}" border="0" />
                  <div style="display:inline-block;width:565px;font:normal 12px/13px Tahoma;padding-left:20px">
                    <h2 style="color:#3F5765;font:bold 16px/20px Arial;margin:0;letter-spacing:-1px">
                      ${it?.title}
                    </h2>
                    <p style="font:normal 13px/18px Tahoma,Arial;text-align:justify;color:#333;margin:10px 0 5px">${it?.description}</p>
                  </div>
                </a>
              </div>
              </g:each>
              <div style="background:url('http://staytoday.ru/images/bg_line.png') repeat-x left top transparent;height:1px;line-height:1px;padding:0;margin-top:20px">&nbsp;</div>
            </g:if>            
            <g:if test="${popdirection}">
              <div style="padding:10px 0;text-align:justify">
                <h2 style="background:url('http://staytoday.ru/images/bg_line.png') no-repeat 0 5px;padding-left:20px;margin:10px 12px;color:#3F5765;font:bold 16px/20px Arial;letter-spacing:-1px">
                  Популярные направления путешествий
                </h2>
                <table width="100%" cellpadding="5" cellspacing="0" border="0">
                <g:each in="${popdirection}" var="record" status="i">
                  <tr>
                    <td style="border:6px solid #fff">
                      <div style="display:inline-block;width:222px;height:162px;border:1px solid #ccc">
                        <div style="width:220px;height:160px;border:1px solid #fff;background:url(${record?.picture?imageurl.toString()+'t_'+record.picture:resource(dir:'images',file:'default-picture.png',absolute:true)}) no-repeat">
                          <div style="margin-top:18px;margin-left:18px;background:#fff;color:#333;display:inline-block;opacity:0.8;padding:0 9px;font:bold 14px/30px Arial">
                            Цены на аренду жилья от                                    
                          </div>
                          <div style="float:right;margin-top:72px;display:inline-block;background:#FF530D;color:#fff;height:40px;opacity:0.8;padding:0 4px;width:auto;font:normal 30px/40px Arial;letter-spacing:-2px"> 
                            ${Math.round(minPricePopdir[i] / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
                          </div>
                        </div>
                      </div>                      
                      <div style="display:inline-block;padding-left:20px;float:right">
                        <h2 style="font:normal 20px/20px Tahoma,Arial;letter-spacing:0;margin:10px 0 0;">
                          <a style="font:normal 20px/20px Tahoma,Arial;letter-spacing:0;margin-top:-2px;color:#3F5765" href="<g:createLink controller='index' action='direction' id='${record.linkname}' params='${[country:Country.get(record?.country_id)?.urlname]}' absolute='true' />">
                            ${record?.name2}
                          </a>
                        </h2>
                        <ul style="list-style:none none outside;color:#787878;font:normal 12px/12px Tahoma;padding:20px 0;width:460px;margin:0">                          
                        <g:if test="${hotdiscount[i]>0}">
                          <li style="clear:both;margin-left:0;line-height:22px;vertical-align:top;border-bottom:1px dotted #A8A8A8;padding: 3px 0">
                            <span style="color:#3F5765;display:block;float:right;font:bold 12px/22px Tahoma;margin:0;padding:0 7px;text-align:center">${hotdiscount[i]}</span>
                            <a style="color:#FF530D" href="<g:createLink controller='index' action='popdiscounts' id='${record.linkname}' params='${[country:Country.get(record?.country_id)?.urlname]}' fragment='hotdiscount' absolute='true' />">
                              ${message(code:'common.discounts_for_hot_offers')}
                            </a>                                
                          </li>
                        </g:if>
                        <g:if test="${longdiscount[i]>0}">
                          <li style="clear:both;margin-left:0;line-height:22px;vertical-align:top;border-bottom:1px dotted #A8A8A8;padding: 3px 0">
                            <span style="color:#3F5765;display:block;float:right;font:bold 12px/22px Tahoma;margin:0;padding:0 7px;text-align:center">${longdiscount[i]}</span>
                            <a style="color:#FF530D" href="<g:createLink controller='index' action='popdiscounts' id='${record.linkname}' params='${[country:Country.get(record?.country_id)?.urlname]}' fragment='longdiscount' absolute='true' />">
                              ${message(code:'common.discounts_for_long_offers')}
                            </a>
                          </li>
                        </g:if>
                        <g:if test="${poprecords[i]>0}">
                          <li style="clear:both;margin-left:0;line-height:22px;vertical-align:top;padding: 3px 0">
                            <span style="color:#3F5765;display:block;float:right;font:bold 12px/22px Tahoma;margin:0;padding:0 7px;text-align:center">${poprecords[i]}</span>                              
                            <a style="color:#FF530D" href="<g:createLink controller='index' action='direction' id='${record.linkname}' params='${[country:Country.get(record?.country_id)?.urlname]}' absolute='true' />">
                              Специальные предложения
                            </a>
                          </li>
                        </g:if>                          
                      </div>                        
                    </td>
                  </tr>
                  <g:if test="${i+1!=poprecords.size()}">
                  <tr><td style="background:url('http://staytoday.ru/images/bg_line.png') repeat-x left top transparent;height:1px;line-height:1px;padding:0">&nbsp;</td></tr>
                  </g:if>
                </g:each>
                </table>
              </div>
            </g:if>
            </div>
          </td>        
        </tr>
        <tr>
          <td valign="middle">
            <p style="padding:8px 14px;color:#666;font:normal 12px/12px Tahoma,Arial">2011-2014 © StayToday</p>
          </td>
          <td align="right" valign="middle" style="padding:8px 14px 8px 0">
            <ul style="float:right;margin:0;padding:0;list-style:none none inside">
              <li style="float:left;margin-left:0;font:normal 12px/12px Tahoma,Arial;color:#666">Присоединяйтесь к нам на:&nbsp;&nbsp; <a style="color:#333" href="http://facebook.com/StayTodayRu" title="StayToday на Facebook">Facebook</a> &nbsp;</li>
              <li style="float:left;margin-left:0;border-left:1px solid #CCC;font:normal 12px/12px Tahoma,Arial">&nbsp; <a style="color:#333" href="http://twitter.com/staytodayRu" title="StayToday в Twitter">Twitter</a> &nbsp;</li>
              <li style="float:left;margin-left:0;border-left:1px solid #CCC;font:normal 12px/12px Tahoma,Arial">&nbsp; <a style="color:#333" href="http://vk.com/StayToday"  title="StayToday в ВКонтакте">ВКонтакте</a> &nbsp;</li>
              <li style="float:left;margin-left:0;border-left:1px solid #CCC;font:normal 12px/12px Tahoma,Arial">&nbsp; <a style="color:#333" href="https://www.youtube.com/user/MrStayToday" title="StayToday на YouTube">YouTube</a></li>
            </ul>              
          </td>
        </tr>
        <tr>
          <td colspan="2" align="right" valign="middle" style="border-top:1px dotted #A8A8A8">
            <p style="padding:5px 14px;font:normal 12px/12px Tahoma,Arial;color:#333">              
              Не хотите получать специальные предложения? 
              <a style="outline:none;color:#FF530D" href="http://staytoday.ru/index/unsubscribe">Отпишитесь</a> от этой рассылки
            </p>            
          </td>
        </tr>
      </table>
    </div>
  </body>
</html>
