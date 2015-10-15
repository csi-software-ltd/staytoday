<html>
  <head>
    <title>${infotext['promotext1'+context?.lang]?:''} № ${payorder?.norder} ${message(code:'waiting.from')} <g:formatDate format="dd.MM.yyyy" date="${payorder?.inputdate}"/></title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main"/>
    <style type="text/css">
      .description_details { float: none !important; width: 100% !important; height: auto !important; border: none !important }      
      .dotted td, .dotted th, .dotted td a.link[href] { font-size: 11px !important }
      .dotted .currency { padding: 5px !important; text-align: right; font-weight: bold; white-space: nowrap }
      .dotted .currency .b-rub { font-size: 13px !important; lihe-height: 12px !important }      
    </style>
  </head>
  <body>
              <g:render template="/account_menu" />                            
                    <g:if test="${infotext['itext'+context?.lang]}">
                      <div style="padding:0px 20px">
                        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                      </div><br/>
                    </g:if>
                      <ul class="description_details">
                        <li class="clearfix alt">
                          <span class="property">${message(code:'account.status')}</span>                                                
                          <span class="value"><g:rawHtml>${payorder.modstatus==2?'<font color="blue">'+message(code:"history.status.completed")+'</font>':payorder.retstatus==2?'<font color="red">'+message(code:"history.status.canceled.guest")+'</font>':payorder.retstatus==3?'<font color="red">'+message(code:"history.status.canceled.owner")+'</font>':'<font color="green">'+message(code:"history.status.active")+'</font>'}</g:rawHtml></span>
                        </li>
                        <li class="clearfix">
                          <span class="property">${message(code:'account.object')}</span>
                          <span class="value"><g:link controller="home" action="view" id="${home?.id}">${home?.name}</g:link></span>
                        </li>
                        <li class="clearfix alt">
                          <span class="property">${message(code:'personal.guest')}</span>
                          <span class="value"><g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+guest?.id]}">${guest?.nickname}</g:link></span>
                        </li>
                      </ul><br/>
                      <table width="100%" class="dotted" cellpadding="0" cellspacing="0" rules="all" frame="none">
                        <tr>
                          <th width="16%">${message(code:'account.summa_deal')}</th>
                          <th width="16%">${message(code:'history.summa')}</th>                          
                          <th width="16%">${message(code:'payorder.summa_sys')}</th>
                          <th width="16%">${message(code:'payorder.fee_sys')}</th>
                          <th width="16%">${message(code:'account.summa_ret')}</th>
                          <th width="16%">${message(code:'history.summa_own')}</th>                          
                        </tr>
                        <tr>
                          <td class="currency">${Math.round(payorder.summa_deal/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
                          <td class="currency">${Math.round(payorder.summa/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>                          
                          <td class="currency">${Math.round((payorder.summa_int+payorder.summa_ext)/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
                          <td class="currency">${Math.round((payorder.summa_com+payorder.summa_agr)/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>                          
                          <td class="currency">${Math.round(payorder.summa_ret/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
                          <td class="currency">${Math.round(payorder.summa_own/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>
                        </tr>
                      </table><br/>
                      <h2 class="toggle border"><span class="edit_icon period"></span>${infotext['promotext2'+context?.lang]?:''}</h2>                      
                      <table width="100%" class="dotted" cellpadding="0" cellspacing="0" rules="all" frame="none">  
                        <tr>                            
                          <th width="98">${message(code:'payorder.transaction.date')}</th>
                          <th>${message(code:'payorder.transaction.type')}</th>
                          <th width="98">${message(code:'payorder.transaction.summa')}</th>                      
                        </tr>
                      <g:each in="${paytrans}" var="it" status="i">
                        <tr>                            
                          <td><g:formatDate format="dd.MM.yyyy HH:mm" date="${it.moddate}"/></td>
                          <td>${Paytype.get(it?.paytype_id)['name'+context?.lang]?:it?.paytype_id}</td>
                          <td class="currency">${Math.round(it.summa/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></td>                            
                        </tr>
                      </g:each>  
                      </table>
                      
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
