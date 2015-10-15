<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main"/>
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'cancellation_policies.css')}"> 
    <g:javascript>
    var iLim = ${textlimit}
    function textLimit(sId){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iLim){
        symbols = symbols.substring(0,iLim);
        $(sId).value = symbols;
        return false;
      }
    }

    function cancelBron(){
      $("cancelMboxBronForm").submit();
    }
    </g:javascript>
    <style type="text/css">
      .subnav li { text-transform: lowercase !important }
      ul.top li { margin-left: 0 !important }
    </style>
  </head>  
  <body>
            <tr style="height:110px">
              <td width="250" class="search">
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>                                
              </td>
              <td colspan="3" class="rent" style="padding-bottom:0px">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr>
              <td colspan="4" valign="top">            
                <div class="content shadow" style="min-height:545px;background:#fff">
                  <ul class="subnav">
                    <li class="${((controllerName=='personal')&&(actionName=='index'))?'selected':''}">
                      <g:link controller="personal" action="index">${Infotext.findByControllerAndAction('personal','index')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${((controllerName=='inbox')&&(actionName=='index'))?'selected':''}">
                      <g:link controller="inbox" action="index">${Infotext.findByControllerAndAction('inbox','index')['name'+context?.lang]}</g:link>                
                    </li>
                    <li class="${((controllerName=='personal')&&(actionName in ['ads','adsoverview','editads','photo','homephoto','video','homevideo','map','homeprice','homeprop','calendar','infras','promote','bookings','waiting','rules','requirements']))?'selected':''}">
                      <g:link controller="personal" action="ads">${Infotext.findByControllerAndAction('personal','ads')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${((controllerName=='trip')&&(actionName in ['next','current','previous','favorite','cancellation','waiting']))?'selected':''}">
                      <g:link controller="trip" action="next">${Infotext.findByControllerAndAction('trip','next')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${((controllerName=='profile')&&(actionName in ['edit','verifyUser','photo','reviews']))?'selected':''}">
                      <g:link controller="profile" action="edit">${Infotext.findByControllerAndAction('profile','edit')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${((controllerName=='account')&&(actionName in ['index','payout','history','refferals','settings']))?'selected':''}">
                      <g:link controller="account" action="index">${Infotext.findByControllerAndAction('account','index')['name'+context?.lang]}</g:link>
                    </li>
                  </ul>                
                  <div class="dashboard-content">
                    <div rel="layer" class="relative" style="padding: 0px 20px 17px 20px">
                    <g:if test="${infotext['itext'+context?.lang]}">
                      <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                    </g:if>                    
                      <h4>${cancellation['name'+context?.lang]}: ${cancellation['fullname'+context?.lang]}</h4>
                      <ul class="top">
                        <g:rawHtml>${cancellation['nb'+context?.lang]?:''}</g:rawHtml>
                        <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                      </ul>
                      <div id="timeline-section">
                        <div id="timeline" align="center">
                        <g:if test="${cancellation.id<4}">                
                          <div id="first-timeline-block">&nbsp;</div>
                          <div id="second-timeline-block" class="timeline-block${(cancellation.id in [2,3])?'-severe':''}">
                            <g:if test="${cancellation.id==0}">${message(code:'cancellation.within_24hours')}</g:if>
                            <g:elseif test="${cancellation.id==1}">${message(code:'cancellation.within_5days')}</g:elseif>
                            <g:elseif test="${cancellation.id==2}">${message(code:'cancellation.within_7days')}</g:elseif>
                            <g:elseif test="${cancellation.id==3}">${message(code:'cancellation.within_30days')}</g:elseif>                  
                          </div>
                        </g:if>
                        <g:else>
                          <div id="first-half-timeline-block">${message(code:'cancellation.before_arrival')}</div>
                        </g:else>                
                          <div id="third-timeline-block">${message(code:'cancellation.during_stay')}</div>
                          <div id="fourth-timeline-block">&nbsp;</div>
                        </div>
                        <ol class="timeline-pins">
                        <g:if test="${cancellation.id<4}">
                          <li id="first-pin">
                            <div class="label">
                              <div>${cancellation['nday'+context?.lang]}</div>
                              <div class="arrow">
                                <div class="circle"></div>
                                <div class="time">
                                  <g:if test="${cancellation.id==0}">${message(code:'calendar.weekName').split(',')[3]},<br/>19 ${message(code:'calendar.monthNameP').split(',')[0]} 15:00</g:if>
                                  <g:elseif test="${cancellation.id==1}">${message(code:'calendar.weekName').split(',')[6]},<br/>15 ${message(code:'calendar.monthNameP').split(',')[0]} 15:00</g:elseif>
                                  <g:elseif test="${cancellation.id==2}">${message(code:'calendar.weekName').split(',')[4]},<br/>13 ${message(code:'calendar.monthNameP').split(',')[0]} 15:00</g:elseif>
                                  <g:elseif test="${cancellation.id==3}">${message(code:'calendar.weekName').split(',')[2]},<br/>21 ${message(code:'calendar.monthNameP').split(',')[11]} 15:00</g:elseif>
                                </div>
                              </div>
                            </div>
                          </li>
                        </g:if>
                          <li id="second-pin">
                            <div class="label">
                              <div>${message(code:'common.date_from')}</div>
                              <div class="arrow">
                                <div class="circle"></div>
                                <div class="time">${message(code:'calendar.weekName').split(',')[4]},<br/>20 ${message(code:'calendar.monthNameP').split(',')[0]} 15:00</div>
                              </div>
                            </div>
                            <div class="example">${message(code:'cancellation.example')}</div>
                          </li>
                          <li id="third-pin">
                            <div class="label">
                              <div>${message(code:'common.date_from')}</div>
                              <div class="arrow">
                                <div class="circle"></div>
                                <div class="time">${message(code:'calendar.weekName').split(',')[0]},<br/>23 ${message(code:'calendar.monthNameP').split(',')[0]} 11:00</div>
                              </div>
                            </div>
                          </li>
                        </ol>
                      </div>
                      <div id="timeline-annotation" class="clearfix">              
                        <div class="tl-annotation">
                          <ul>
                            <li>${cancellation['itext1'+context?.lang]?:''}</li>
                          </ul>
                        </div>              
                        <div class="tl-annotation">
                        <g:if test="${cancellation.id<4}">
                          <div class="annotation-border"></div>
                        </g:if>
                          <ul>
                            <li>${cancellation['itext2'+context?.lang]}</li>
                          </ul>
                        </div>
                        <div class="tl-annotation">
                          <div class="annotation-border"></div>
                          <ul>
                            <li>${cancellation['itext3'+context?.lang]}</li>
                          </ul>
                        </div>
                      </div>
                    <g:if test="${flash?.error}">
                      <div class="notice">
                        <ul>
                          <g:if test="${flash?.error==1}"><li>${message(code:'error.blank',args:[message(code:'booking.cancellation.reason')])}</li></g:if>
                          <g:if test="${flash?.error==2}"><li>${message(code:'profile.error.sendmail')}</li></g:if>
                        </ul>
                      </div>
                    </g:if>                          
                      <g:form id="cancelMboxBronForm" name="cancelMboxBronForm" controller="inbox" action="cancelMboxBron" id="${trip.id}" base="${context?.absolute_lang}">
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                          <tr>
                            <td>
                              <label for="comments">${message(code:'booking.cancellation.reason')}</label>
                              <textarea style="width:76%" id="comments" onkeydown="textLimit(this.id);" onkeyup="textLimit(this.id);" rows="5" cols="40" name="comments">${inrequest?.comments}</textarea>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:10px 0 0 203px">
                              <input type="button" class="button-glossy orange" onclick="cancelBron();" value="${message(code:'booking.bron.cancel').capitalize()}" />
                            </td>
                          </tr>
                        </table>
                      </g:form>
                      </div>
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                  </div>
                </div>
              </td>
            </tr>                    
  </body>
</html>
