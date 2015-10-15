<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main"/>
    <script type="text/javascript">
    function clickPaginate(event){
      event.stop();
      var link = event.element();
      if(link.href == null){
        return;
      }
      new Ajax.Updater(
        { success: link.up('div').up('div').up('div').id },
        link.href,
        { evalScripts: true }
      );
    }
    </script>
    <g:javascript>
      function accordeon(sId){
        jQuery('.accordeon:not(#'+sId+')').slideUp();
        jQuery('#'+sId).slideDown();
      }
    </g:javascript>
    <style type="text/css">      
      .results_header { margin: 0 8px !important; width: 98% !important }
      .dotted { margin: 0 8px 8px !important }
      .dotted td, .dotted th, .dotted td a.link[href] { font-size: 11px !important }
      .dotted td { padding: 5px !important; }
      .dotted .currency { text-align: right; font-weight: bold; }
      .dotted .currency .b-rub, .action_button.pan .currency .b-rub { font-size: 13px !important; lihe-height: 12px !important }
      .border { margin-bottom: -1px !important; cursor: pointer !important }
      .accordeon { margin-top: 8px }
    </style>
  </head>
  <body onload="\$('payorderbronFormSubmit').click();">
              <g:render template="/account_menu" />
                    <g:if test="${infotext['itext'+context?.lang]}">
                      <div class="page-topic">
                        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                      </div>
                    </g:if>
                      <div class="page-topic clearfix" style="border:none">
                        <span class="action_button pan">
                          <span class="icon none">${message(code:'account.booking')},<br/>${message(code:'account.summa')}</span>
                          <span class="currency">${Math.round(((account?.summa?:0)+(account?.summa_ext?:0))/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                        </span>
                        <span class="action_button pan">
                          <span class="icon none">${message(code:'account.booking')},<br/>${message(code:'account.summa_fin')}</span>
                          <span class="currency">${Math.round((account?.summa_fin?:0)/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                        </span>
                        <span class="action_button pan">
                          <span class="icon none">${message(code:'account.promotion')},<br/>${message(code:'account.summa_adv')}</span>
                          <span class="currency">${Math.round((account?.summa_adv?:0)/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                        </span>
                        <span class="action_button pan" style="margin-right:0px">
                          <span class="icon none">${message(code:'account.promotion')},<br/>${message(code:'account.summa_serv')}</span>
                          <span class="currency">${Math.round((account?.summa_serv?:0)/valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span>
                        </span>
                      </div>
                    <g:if test="${account}"><!--only for homeowners-->
                      <div class="search-container" style="min-height:500px">                      
                        <h2 class="toggle border" onclick="$('payorderbronFormSubmit').click();"><span class="edit_icon period"></span>${infotext['promotext1'+context?.lang]?:''}</h2>                      
                        <g:formRemote name="payorderbronForm" url="[action:'payorderbronhistory']" update="[success:'payorderbronlist']" onComplete="accordeon('payorderbronlist');">
                          <input type="submit" id="payorderbronFormSubmit" style="display:none" value="${message(code:'button.view')}"/>
                          <input type="hidden" name="u_id" value="${user?.id}"/>
                        </g:formRemote>
                        <div id="payorderbronlist" style="display:none" class="accordeon"><!--ajax container-->
                        </div>                        
                        <h2 class="toggle border" onclick="$('payorderactFormSubmit').click();"><span class="edit_icon ask"></span>${infotext['promotext2'+context?.lang]?:''}</h2>
                        <g:formRemote name="payorderactForm" url="[action:'payorderacthistory']" update="[success:'payorderactlist']" onComplete="accordeon('payorderactlist');">
                          <input type="submit" id="payorderactFormSubmit" style="display:none" value="${message(code:'button.view')}"/>
                          <input type="hidden" name="u_id" value="${user?.id}"/>
                        </g:formRemote>
                        <div id="payorderactlist" style="display:none" class="accordeon"><!--ajax container-->
                        </div>                        
                      </div>
                    </g:if>                      
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
