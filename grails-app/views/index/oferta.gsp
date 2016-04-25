<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl}" />    
    <meta name="layout" content="main" />
    <style type="text/css">
      .page-topic { border-bottom: none !important }
      .rent h1 { margin: 0 10px 0 20px !important; height: auto !important }
      ul.faq_list { margin: 15px -20px 0px !important; }
      ul.faq_list li, div.answer { padding: 8px 20px !important; text-align: justify }      
      div.answer.last { border-bottom: none !important }
      ul.pay-list li { padding: 5px !important; width: 150px !important; border-bottom: none !important }      
    </style>
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" class="search s0">
                <a class="button" rel="nofollow" onclick="<g:if test='${isLoginDenied}'>showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>
              </td>
              <td width="730" colspan="3" class="rent s0">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr>              
              <td colspan="4" class="bg_shadow">              
                <ul class="breadcrumbs" itemscope itemtype="http://schema.org/BreadcrumbList">
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" itemprop="item">
                      <span itemprop="name">${message(code:'label.main')}</span>
                    </a><meta itemprop="position" content="1" />
                  </li> &#8594;
                  <li itemprop="itemListElement" itemscope itemtype="http://schema.org/ListItem">
                    <span itemprop="item">
                      <span itemprop="name">${infotext['header'+context?.lang]?:''}</span>
                    </span><meta itemprop="position" content="2" />
                  </li>
                </ul>
              </td>
            </tr>            
            <tr>
              <td colspan="4" valign="top" class="body shadow">
                <div class="page-topic">
                  <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                  <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                  <ul class="faq_list" style="margin-top:-1px !important">
                    <li class="question"><a name="use_5" href="javascript:void(0)">${infotext['promotext1'+context?.lang]?:''}</a></li>
                    <div class="answer">                      
                      <ul class="clearfix pay-list">                        
                      <g:each in="${payway}" var="it" status="i">
                        <li class="float" <g:if test="${i==0}">style="width:100px !important"</g:if>><span class="icons ${it.icon}" title="${it.name}"></span></li>
                      </g:each>
                      </ul>
                    </div>
                    <li class="question"><a name="use_6" href="javascript:void(0)">${infotext['promotext2'+context?.lang]?:''}</a></li>
                    <div class="answer last">
                      <ul class="clearfix pay-list">
                        <li class="float" style="width:120px !important"><span class="icons payu" title="${message(code:'oferta.agregator')} PayU"></span></li>
                        <li class="float"><span class="icons vbv" title="Verified by Visa"></span></li>
                        <li class="float" style="width:120px !important"><span class="icons mcc" title="MasterCard SecureCode"></span></li>
                        <li class="float" style="width:120px !important">
                          <a href="https://passport.webmoney.ru/asp/certview.asp?wmid=190007126215" rel="nofollow" target="_blank">
                            <span class="icons wma" title="${message(code:'oferta.attestat')} WebMoney"></span><br/>
                            <small>${message(code:'oferta.attestat')}</small>
                          </a>
                        </li>
                      </ul>
                      <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                    </div>
                  </ul>                  
                </div>
              </td>
            </tr>          
  </body>
</html>
