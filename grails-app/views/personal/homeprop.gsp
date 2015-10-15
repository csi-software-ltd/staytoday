<html>
  <head>
    <title>${message(code:!active?'ads.price.period.inactivity':'ads.price.period.rent')}</title>
    <meta name="layout" content="main" />
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/> 
    <g:javascript>      
    function viewWeekendPrice(name,viewname){
      if($(name).checked)
        $(viewname).show();		 
      else
        $(viewname).hide();	    
    }	 
    </g:javascript>
    <style type="text/css">
      .form label.mini { min-width: 100px !important; }
    </style>
  </head>  
  <body>
              <g:render template="/personal_menu" />
                          <div rel="layer">                                   
                          <g:if test="${(flash?.error?:[]).size()>0}">
                            <div class="notice" style="margin-top:5px">
                              <ul>
                              <g:each in="${flash?.error}"> 
                                <g:if test="${it==1}"><li>${message(code:'error.blank',args:[message(code:'ads.price.date_start')])}</li></g:if>
                                <g:if test="${it==2}"><li>${message(code:'error.blank',args:[message(code:'ads.price.date_end')])}</li></g:if>
                                <g:if test="${it==3}"><li>${message(code:'ads.error.price.date',args:[message(code:'ads.price.date_start'),message(code:'ads.price.date_end')])}</li></g:if>
                                <g:if test="${it==4}"><li>${message(code:'ads.error.price.date.exist')}</li></g:if>
                                <g:if test="${it==5}"><li>${message(code:'ads.error.price',args:[message(code:'ads.price.daily'),price_min])}</li></g:if>
                                <g:if test="${it==6}"><li>${message(code:'ads.error.price',args:[message(code:'ads.price.weekend'),price_min])}</li></g:if>
                                <g:if test="${it==7}"><li>${message(code:'ads.error.price.date.expired')}</li></g:if>			
                              </g:each>	
                              </ul>
                            </div>
                          </g:if>
                            <div class="paddtop" style="background:#fff">                           
                              <g:form name="photoAddForm" method="post" url="${[controller:'personal',action:'homepropadd',params:[id:inrequest?.id?:0,home_id:home?.id?:0]]}" useToken="true" base="${context.sequreServerURL}">			
                                <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                                <g:if test="${active}">          
                                  <tr>
                                    <td width="50%">
                                      <label for="price" class="mini">${message(code:'ads.price.daily').capitalize()}</label>
                                      <input type="text" class="price <g:if test="${(flash?.error?:[]).contains(5)}">red</g:if>" id="price" name="price" value="${inrequest?.price?:''}"/>
                                      <span class="currency"><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>
                                    </td>         
                                    <td>
                                      <label for="is_weekend" class="mini"><input type="checkbox" id="is_weekend" name="is_weekend" value="" <g:if test="${(inrequest?.priceweekend?:0)>0}">checked</g:if> onclick="viewWeekendPrice(this,'price_weekend');"/>${message(code:'ads.price.weekend')}</label>
                                      <span id="price_weekend" style="<g:if test="${(inrequest?.priceweekend?:0)==0}">display:none</g:if>">
                                        <input type="text" class="price <g:if test="${(flash?.error?:[]).contains(6)}">red</g:if>" id="priceweekend" name="priceweekend" value="${inrequest?.priceweekend?:0}"/>
                                        <span class="currency"><g:rawHtml>${cur_valuta?.symbol?:''}</g:rawHtml></span>
                                      </span>
                                    </td>
                                  </tr>        
                                </g:if>		
                                <g:if test="${!(inrequest?.modstatus==3)}">
                                  <tr>
                                    <td>
                                      <label for="date_start" class="mini">${message(code:'ads.price.date_start')}</label>
                                      <calendar:datePicker name="date_start" value="${inrequest?.date_start?:''}" dateFormat="%d-%m-%Y"/>
                                    </td>
                                    <td>
                                      <label for="date_end" class="mini">${message(code:'ads.price.date_end')}</label>
                                      <calendar:datePicker name="date_end" value="${inrequest?.date_end?:''}" dateFormat="%d-%m-%Y"/>
                                    </td>            
                                  </tr>
                                </g:if>
                                <g:if test="${!active}">
                                  <tr>
                                    <td colspan="2">
                                      <label for="remark" class="mini">${message(code:'ads.price.comment')}</label>
                                      <input type="text" maxlength="${stringlimit}" name="remark" value="${inrequest?.remark?:''}" style="width:500px" />
                                    </td>
                                  </tr>
                                  <input type="hidden" name="modstatus" value="${inrequest?.modstatus}"/>
                                </g:if>
                                </table>
                                <input type="hidden" name="active" value="${active}"/>
                              </g:form>			  
                              <div style="padding:20px;height:40px">
                                <input class="button-glossy orange float" type="submit" name="submit" value="${message(code:((inrequest?.modstatus?:0)==2)?'button.switchon':'button.save')}" onclick="$('photoAddForm').submit()" style="margin-right:5px"/>
                                <g:form name="apage" url="${[controller:'personal',action:'homeprice',id:home?.id?:0]}" method="post" class="float" base="${context.sequreServerURL}">                        
                                  <input class="button-glossy grey" type="submit" value="${message(code:'button.cancel')}" />
                                </g:form>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>                    
                  </div>
                </div>
              </td>
            </tr>                         
  </body>
</html>
