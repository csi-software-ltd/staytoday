<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''} ${home?.name?:''}</title>           
    <meta name="layout" content="print"/>
    <style type="text/css">
      .rent { height: 100px; }
      .rent h1 { font: 30px/75px Tahoma !important; }
      h2.title { padding: 5px 20px }
      .thumbnail { width: 140px !important; height: 98px !important; margin: 0 20px 15px 0 } 
      .search_filter .filter_header { background: #ccc !important; border-bottom: 1px solid #ccc; font: bold 18px/40px Tahoma, Arial !important; color: #000 !important }      
      .search_filter_content, .page-topic p { color: #000 !important; font: normal 16px/25px Tahoma, Arial !important; letter-spacing: 0 }
      .price.b-rub { font: bold 19px/19px 'ALSRubl-Arial',Arial; }
    </style>
  </head>
  <body onload="setTimeout('window.print()', 1000);">
            <tr>
              <td colspan="2">
                <div class="rent s">
                  <h1>${infotext['header'+context?.lang]?:''}</h1>  
                </div>
                <div class="body" style="width:100%">                  
                  <ul class="collapsable_filters" style="width:100%">      
                    <li class="search_filter clearfix">
                      <a class="filter_header">${message(code:'account.object').capitalize()}</a>
                      <div class="search_filter_content">
                        <div class="thumbnail shadow">              
                          <img width="140" height="98" src="${urlphoto}${home?.client_id}/t_${home?.mainpicture}" alt="${home?.name}"/>
                        </div>                      
                        <h2 class="title"><a href="${(context?.is_dev)?'/'+context?.appname:''}/home/view/${home?.linkname}">${home?.name}</a></h2>
                        ${message(code:'booking.address')}:&nbsp;&nbsp; <b>${home?.address}</b>
                      </div>
                    </li>
                    <li class="search_filter clearfix">
                      <a class="filter_header">${infotext['promotext1'+context?.lang]?:''}</a>
                      <div class="search_filter_content">
                        ${message(code:"common.date_from").capitalize()}:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>${date_start}</b> &nbsp;&nbsp;&nbsp;&nbsp;${message(code:'ads.price.rule_timein')}:&nbsp; <b>${timein}</b><br/>
                        ${message(code:"common.date_to").capitalize()}:&nbsp;&nbsp; <b>${date_end}</b> &nbsp;&nbsp;&nbsp;&nbsp;${message(code:'ads.price.rule_timeout')}: <b>${timeout}</b><br/>
                        ${message(code:"common.guests").capitalize()}:&nbsp;&nbsp; <b>${homeperson}</b>                   
                      </div>
                    </li>
                    <li class="search_filter clearfix">
                      <a class="filter_header">${infotext['promotext2'+context?.lang]?:''}</a>
                      <div class="search_filter_content">
                        ${message(code:'history.triporder')}:&nbsp;&nbsp; <b>${payorder?.norder}</b><br/>
                      <g:if test="${trip?.valuta_id!=857}">
                        ${message(code:'trip.print.summa_val')}:&nbsp;&nbsp; <b class="price b-rub">${payorder?.summa_val}<g:rawHtml>${Valuta.get(trip?.valuta_id).symbol}</g:rawHtml></b><br/>
                      </g:if>
                        ${message(code:'trip.print.summa_deal')}:&nbsp;&nbsp; <b class="price b-rub">${payorder?.summa_deal}<g:rawHtml>${Valuta.get(857).symbol}</g:rawHtml></b><br/>
                        ${message(code:'trip.print.summa')}:&nbsp;&nbsp; <b class="price b-rub">${payorder?.summa}<g:rawHtml>${Valuta.get(857).symbol}</g:rawHtml></b>  
                      </div>              
                    </li>
                  <g:if test="${reserve?.id!=3}">
                    <li class="search_filter clearfix">
                      <a class="filter_header">${message(code:'viewprint.rules')}</a>
                      <div class="search_filter_content">
                        ${message(code:'ads.price.cancellation').capitalize()}:&nbsp;&nbsp; <b><a target="_blank" href="${(context?.is_dev)?'/'+context?.appname:''}/home/cancellation/#${cancellation.shortlink}">${cancellation['name'+context?.lang]}</a></b> 
                        &nbsp;&nbsp; (${cancellation['fullname'+context?.lang]})<br/>
                        ${message(code:'ads.price.rule').capitalize()}:&nbsp;&nbsp; <b><a target="_blank" href="${(context?.is_dev)?'/'+context?.appname:''}/home/view/${home?.linkname}/#homerule">${message(code:'inbox.bron.rules.view')}</a></b>
                      </div>
                    </li>
                  </g:if>
                    <li class="search_filter clearfix">
                      <a class="filter_header">${message(code:'trip.print.owner')}</a>
                      <div class="search_filter_content">
                        ${message(code:'common.owner').capitalize()}:&nbsp;&nbsp; <b><a href="${(context?.is_dev)?'/'+context?.appname:''}/id${own?.id}">${own?.nickname}</a></b><br/>
                        ${message(code:'inbox.bron.contactphone')}:&nbsp;&nbsp; <b><font color="#000">${own?.tel}</font></b><br/>
                        ${message(code:'booking.date')}:&nbsp;&nbsp; <b><font color="#000">${date_bron}</font></b>                      
                      </div>
                    </li>
                    <li class="search_filter clearfix">
                      <a class="filter_header">${message(code:'trip.print.support')}</a>
                      <div class="search_filter_content">
                        ${message(code:'inbox.bron.contactphone')}:&nbsp;&nbsp; <b>8 (800) 555-1768</b> &nbsp;&nbsp; (${message(code:'trip.print.support.callfree')})<br/>
                        Email:&nbsp;&nbsp; <b>info@staytoday.ru</b>                        
                      </div>
                    </li>
                  </ul>
                  <div class="page-topic btop">
                    <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>
                  </div>
                </div>
              </td>
            </tr>    
  </body>
</html>
