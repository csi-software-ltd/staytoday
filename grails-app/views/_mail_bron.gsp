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
              Предварительное бронирование
            </p>
          </td>
        </tr>
        <tr>
          <td colspan="2" style="background:url('http://staytoday.ru/images/mail-width.png') top center repeat-x">
            <table width="100%" cellpadding="0" cellspacing="0" border="0">
              <tr>
                <td width="5" style="background:url('http://staytoday.ru/images/mail-height.png') left top repeat-y">&nbsp;</td>
                <td style="padding:5px 0;background:url('http://staytoday.ru/images/mail-width.png') bottom center repeat-x">
                  <div style="background:#fff;padding:5px">
                    <table width="700" cellpadding="5" cellspacing="5" border="0">
                      <tr>
                        <td width="204">
                          <div style="width:204px;height:144px;border:1px solid #ccc">
                            <div style="width:200px;height:140px;border:2px solid #fff;background:url(${home?.mainpicture?urlphoto+home?.client_id+'/t_'+home.mainpicture:resource(dir:'images',file:'default-picture.png',absolute:true)}) no-repeat"></div>
                          </div>
                        </td>  
                        <td valign="middle">
                          <h2 style="margin:0;font:bold 16px/20px Arial">
                            <a style="color:#FF530D" href="${(context?.is_dev)?context?.serverURL+'/'+context?.appname:context?.serverURL}/home/view/${home.linkname}">${home.name}</a>
                          </h2>
                          <p style="font:normal 13px/18px Tahoma,Arial"><b>${message(code:"booking.address")}</b>: ${home.address}</p>
                        </td>
                        <td align="right" nowrap>
                          <span style="color:#FF530D;font:30px/30px Arial;letter-spacing:-2px">${displayPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                          <font color="gray" face="Arial" size="2">${message(code:'personal.cost')}</font>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="3" style="border-bottom:1px dotted silver">
                          <div style="font:normal 13px/18px Tahoma,Arial">                        
                            ${message(code:"common.date_from").capitalize()}:&nbsp;&nbsp;&nbsp; <b>${date_start}</b> &nbsp;&nbsp; ${message(code:'ads.price.rule_timein')}:&nbsp; ${timein}<br/>
                            ${message(code:"common.date_to").capitalize()}:&nbsp; <b>${date_end}</b> &nbsp;&nbsp; ${message(code:'ads.price.rule_timeout')}:&nbsp; ${timeout}<br/>
                            ${message(code:"common.guests").capitalize()}:&nbsp; <b>${homeperson['name'+context?.lang]}</b>
                          </div>
                        </td>
                      </tr>
                    <g:if test="${cancellation}">
                      <tr>
                        <td colspan="3" style="padding-bottom:15px;border-bottom:1px dotted silver">
                          <div style="font:normal 13px/18px Tahoma,Arial">
                          <g:if test="${reserve?.id!=3}">
                            ${message(code:'ads.price.cancellation').capitalize()}:&nbsp; <b><a style="color:#FF530D" target="_blank" href="${(context?.is_dev)?context?.serverURL+'/'+context?.appname:context?.serverURL}/home/cancellation/#${cancellation.shortlink}">${cancellation['name'+context?.lang]}</a></b>&nbsp;&nbsp;
                            <i><font size="2" face="Arial">${cancellation['fullname'+context?.lang]}</font></i><br/>
                          </g:if>
                            ${message(code:'ads.price.rule').capitalize()}:&nbsp; <b><a style="color:#FF530D" target="_blank" href="${(context?.is_dev)?context?.serverURL+'/'+context?.appname:context?.serverURL}/home/view/${home.linkname}#homerule">${message(code:'inbox.bron.rules.view')}</a></b>
                          </div>
                        </td>
                      </tr>
                    </g:if>
                      <tr>
                        <td colspan="3" style="padding-bottom:15px;${ispaypossible?'border-bottom: 1px dotted silver':''}">
                          <div style="font:normal 13px/18px Tahoma,Arial">
                            ${message(code:'common.owner').capitalize()}:&nbsp; <b><a style="color:#FF530D" href="${(context?.is_dev)?context?.serverURL+'/'+context?.appname:context?.serverURL}/id${ownerUser.id}">${ownerUser.nickname}</a></b><br/>
                            ${message(code:'booking.date')}:&nbsp; <b>${String.format('%td/%<tm/%<tY %<tH:%<tM',new Date())}</b>
                          </div>
                        </td>
                      </tr>
                    <g:if test="${ispaypossible}"> 
                      <tr>
                        <td colspan="3" style="padding-bottom:15px;border-bottom:1px dotted silver">
                          <h2 style="font:bold 16px Arial">Порядок расчетов</h2>                
                          <p style="font:13px/18px Tahoma,Arial">${message(code:'detail.owner_scheme')} - <b>${reserve['name'+context?.lang]}</font></b>.<br/>
                            <g:rawHtml>${reserve['description'+context?.lang]}</g:rawHtml>                            
                            <g:if test="${reserve?.id==3}"><br/><b><g:rawHtml>${ownerClient?.settlprocedure}</g:rawHtml></b></g:if>
                          </p>
                          <ul style="padding:0;margin:0;list-style:none outside none">
                            <li style="padding:5px 0;display:inline-block;float:left;border-radius:5px;border:1px solid #CFCFCF;margin-right:2px">
                              <span style="display:inline-block;float:left;background:url('http://staytoday.ru/images/payments.png') 0 0;height:50px;width:160px" title="Банковская карта"></span>
                            </li>
                            <li style="padding:5px 0;display:inline-block;float:left;border-radius:5px;border:1px solid #CFCFCF;margin-right:2px">
                              <span style="display:inline-block;float:left;background:url('http://staytoday.ru/images/payments.png') 0 -100px;height:50px;width:160px" title="WebMoney"></span>
                            </li>
                            <li style="padding:5px 0;display:inline-block;float:left;border-radius:5px;border:1px solid #CFCFCF">
                              <span style="display:inline-block;float:left;background:url('http://staytoday.ru/images/payments.png') 17px -150px;height:50px;width:160px" title="Qiwi"></span>
                            </li>
                          </ul>
                        </td>
                      </tr>
                      <tr>
                        <td>
                          <h2 style="font:bold 16px/30px Arial">${message(code:'inbox.bron.summa').capitalize()}</h2>
                          <div align="right">
                            <span style="color:#FF530D;font:30px/30px Arial;letter-spacing:-2px">${totalPrice}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                            <font color="gray" face="Arial" size="2">${message(code:'inbox.bron.summa')}</font>
                          </div>
                        </td>
                        <td colspan="2" style="padding:35px 0 0 50px">
                          <a style="font:bold 19px/38px Helvetica,Tahoma;background:#FE520D;border:1px solid #EB470B;border-radius:0.3em;box-shadow:0 0 0.2em rgba(255,255,255,0.3) inset,0 0 0.2em rgba(255,255,255,0.3) inset,0 1px 2px rgba(0,0,0,0.3);color:#FFF;padding:6px 20px;text-align:center;text-decoration:none;text-shadow:0 -1px 1px rgba(0,0,0,0.5);" 
                            href="${(context?.is_dev)?context?.sequreServerURL+'/'+context?.appname:context?.sequreServerURL}/payment/${Tools.generateModeParam(rec_id,mbox_id)}/${rec_id}">Оплатить</a>
                        </td>
                      </tr>                    
                      <!--<tr>
                        <td colspan="3" style="padding-bottom:15px;border-top:1px dotted silver">infotext</td>
                      </tr>-->
                    </g:if>  
                    </table>
                  </div>
                </td>        
                <td width="5" style="background:url('http://staytoday.ru/images/mail-height.png') right top repeat-y">&nbsp;</td>
              </tr>
            </table>            
          </td>        
        </tr>
        <tr>
          <td valign="middle" style="padding-top:10px">
            <p style="padding:8px 14px;color:#666;font:normal 12px/12px Tahoma,Arial">2011-2014 © StayToday</p>
          </td>
          <td align="right" valign="middle" style="padding:18px 14px 8px 0">
            <ul style="float:right;margin:0;padding:0;list-style:none none inside">
              <li style="float:left;margin-left:0;font:normal 12px/12px Tahoma,Arial;color:#666">Присоединяйтесь к нам на:&nbsp;&nbsp; <a style="color:#333" href="http://facebook.com/StayTodayRu" title="StayToday на Facebook">Facebook</a> &nbsp;</li>
              <li style="float:left;margin-left:0;border-left:1px solid #CCC;font:normal 12px/12px Tahoma,Arial">&nbsp; <a style="color:#333" href="http://twitter.com/staytodayRu" title="StayToday в Twitter">Twitter</a> &nbsp;</li>
              <li style="float:left;margin-left:0;border-left:1px solid #CCC;font:normal 12px/12px Tahoma,Arial">&nbsp; <a style="color:#333" href="http://vk.com/StayToday"  title="StayToday в ВКонтакте">ВКонтакте</a> &nbsp;</li>
              <li style="float:left;margin-left:0;border-left:1px solid #CCC;font:normal 12px/12px Tahoma,Arial">&nbsp; <a style="color:#333" href="https://www.youtube.com/user/MrStayToday" title="StayToday на YouTube">YouTube</a></li>
            </ul>              
          </td>
        </tr>
      </table>
    </div>
  </body>
</html>
