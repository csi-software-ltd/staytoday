<html>
  <head>
    <title>${Infotext.findByControllerAndAction('error','page_500')['title'+context?.lang]}</title>    
    <meta name="description" content="${Infotext.findByControllerAndAction('error','page_500')['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />    
  </head>
  <body>
      <h1 role="heading">${Infotext.findByControllerAndAction('error','page_500')['header'+context?.lang]}</h1>
      <div data-role="content">        
        <g:rawHtml>${Infotext.findByControllerAndAction('error','page_500')['itext'+context?.lang]}</g:rawHtml>        
      </div>
      <div id="footer" data-role="footer" data-position="fixed" data-theme="a">      
        <div id="menuPopup" data-role="popup" data-transition="slideup" data-position-to="#footer" data-history="false">
          <a href="#" id="menuPopupClose" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">${message(code:'button.close')}</a>
          <ul id="menuPopupUl" data-theme="a" data-inset="true" data-role="listview">
          <g:if test="${user}">
            <li><a href="${createLink(action:'logout',base:context?.mobileURL_lang)}">${message(code:'label.logout')}</a></li>
            <li><a href="${createLink(uri:'/favorites',base:context?.mobileURL_lang)}">${message(code:'label.favorite')}</a></li>
          </g:if><g:else>
            <li><a href="javascript:void(0)" rel="nofollow" data-rel="back" onclick="jQuery('#login_link').trigger('click')">${message(code:'label.login')}</li>
          </g:else>
            <li><a href="${createLink(uri:'/about',base:context?.mobileURL_lang)}">${Infotext.findByControllerAndAction('index','about')['name'+context?.lang]}</a></li>
          </ul>
        </div>
      </div>
  </body>
</html>
