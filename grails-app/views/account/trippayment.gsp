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
    <style type="text/css">
      .results_header { margin: 0 8px !important; width: 98% !important }    
      .dotted td, .dotted th, .dotted td a.link[href] { font-size: 11px !important }
      .dotted td { padding: 5px !important; }
      .dotted .currency { text-align: right; font-weight: bold; white-space: nowrap }
      .dotted .currency .b-rub { font-size: 13px !important; lihe-height: 12px !important }
      .border { margin-bottom: 8px !important } 
    </style>
  </head>
  <body onload="\$('trippaymentFormSubmit').click();">
              <g:render template="/account_menu" />
                    <g:if test="${infotext['itext'+context?.lang]}">
                      <div class="page-topic" style="border:none">
                        <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                      </div><br/>
                    </g:if>
                      <h2 class="toggle border"><span class="edit_icon detail"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                      <div class="search-container">
                        <div id="search_body">
                          <g:formRemote name="trippaymentForm" url="[action:'trippaymenthistorylist']" update="[success:'trippaymentlist']">
                            <input type="submit" id="trippaymentFormSubmit" style="display: none" value="${message(code:'button.vew')}"/>
                            <input type="hidden" name="u_id" value="${user?.id}"/>
                          </g:formRemote>
                          <div id="trippaymentlist" class="accordeon"><!--ajax container-->
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
