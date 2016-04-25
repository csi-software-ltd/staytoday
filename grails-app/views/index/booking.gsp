<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <link rel="canonical" href="${context.curl}" />    
    <meta name="layout" content="main" />
    <g:javascript>
      function noticeClick(iId){
        <g:remoteFunction controller='index' action='noticeClick' params="\'id=\'+iId" />
      }
    </g:javascript> 
    <style type="text/css">      
      .page-topic { border: none !important }
      .page-topic h2.ins { margin: 10px 0; }
      ul.top { margin-bottom: 10px }      
      div.answer { padding: 12px 20px !important; text-align: justify }
      div.answer:last-of-type { border-bottom: none }
      .circle { 
        margin-right: 10px!important; line-height: 28px!important; min-height: 28px!important; min-width: 28px!important; border-radius: 28px!important; padding: 0px!important;
        color: #fff!important; border-color: #eb470b!important; background: #eb470b!important; background: linear-gradient(top, #fe520d, #eb470b) !important;
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fffe520d',endColorstr='#ffeb470b',GradientType=0) !important;
      }
      .button-glossy:before { background: none !important;}
      .description { padding-right: 30px; width:70% }
    </style>
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" class="search s0">
                <a class="button" rel="nofollow" onclick="<g:if test='${isLoginDenied}'>showLoginForm()</g:if><g:else>transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')</g:else>">${message(code:'common.deliverhome')}</a>
              </td>
              <td width="730" colspan="3" class="rent ss">
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
                </div>
                <div class="page-topic">                  
                  <h2 class="ins">${infotext['promotext1'+context?.lang]?:''}</h2>                  
                  <g:each in="${howto}" var="item" status="i">                    
                  <div class="clearfix answer">
                    <img class="thumbnail shadow" style="float:${i%2==0?'left':'right'}" alt="${item?.name?:''}" src="${resource(dir:'images',file:(item?.picture)?'howto/'+item?.picture:'default-picture.png')}" border="0" />
                    <div class="description ${i%2==0?'col':'float'}">
                      <h3><span class="button-glossy circle">${item?.number}</span><a href="javascript:void(0)" rel="nofollow">${item['name'+context?.lang]?:''}</a></h3>
                      <g:rawHtml>${item['description'+context?.lang]?:''}</g:rawHtml>
                    </div>
                  </div>                    
                  </g:each>                  
                </div>      
                <div class="page-topic">                  
                  <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                </div>
                <div class="page-topic">
                  <h2 class="ins">${infotext['promotext2'+context?.lang]?:''}</h2>
                  <g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml>
                </div>                
              <g:if test="${notice}">
                <div class="page-topic">
                <g:each in="${notice}" var="it" status="i">
                  <div class="notice ads width clearfix" onclick="noticeClick(${it.id})">
                    <g:if test="${it?.is_index}"><a href="${serverUrl.toString()+'/'+it?.url}" title="${it?.title}"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${it?.url}'],[],1,'','${context?.lang}')"></g:else>
                      <img class="thumbnail userpic" src="${resource(dir:'images',file:(it?.image)?'anonses/'+it?.image:'user-default-picture.png')}" border="0" />
                      <div class="description">
                        <h2 class="ins">${it?.title}</h2>
                        <p>${it?.description}</p>
                      </div>
                    <g:if test="${it?.is_index}"></a></g:if><g:else></span></g:else>
                  </div>
                </g:each>
                </div>
              </g:if>
              </td>
            </tr>          
  </body>
</html>
