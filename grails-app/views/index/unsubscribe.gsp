<html>
  <head>  
    <title>${infotext['title'+context?.lang]?:' '}</title>  
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <meta name="layout" content="main" />
    <g:javascript>
      function toggleFilter(container){ 
        var li = $(container);      
        if(li.className == 'search_filter')
          li.className = 'search_filter closed open';
        else if(li.className == 'search_filter closed open')
          li.className = 'search_filter'; 
        else if(li.className == 'search_filter closed')
          li.className = 'search_filter open';
        else if(li.className == 'search_filter open')
          li.className = 'search_filter closed';      
      }    
      function init(){
      <g:if test="${user?.id}">
        <g:if test="${(user?.is_subscribe?:0)}">
        $("subscribe").show();
        $("spec").show();
        </g:if><g:else>
        $("subscribe").hide();        
        $("spec_already").show();    
        </g:else>
      </g:if><g:else> 
        $("spec").show();    
      </g:else>        
      } 
      function processResponse(e){
        var iErr=e.responseJSON.error;
        $("errors").hide();
        $("error1").hide();
        $("error2").hide();
        $("error3").hide();
        if(iErr==1){
          $("errors").show();
          $("error1").show();
        }  
        if(iErr==2){
          $("errors").show();
          $("error2").show();  
        }  
        if(iErr==3){
          $("errors").show();
          $("error3").show(); 
        }
        if(iErr==4 || iErr==5){
          $("spec").hide();
          $("subscribe").hide();
          $("spec_already").show();
          if(iErr==5)
            location.reload(true);
        }
      }
    </g:javascript>
    <style type="text/css">
      .form { background: none !important; }
      .form label { min-width: 50px !important; padding: 0 10px !important; }
      .form input[type="text"], form input[type="password"] { width: 125px !important; }
      .notice { margin: 5px 0 !important; } 
      .notice ul li { line-height: 15px !important; color: #333 !important }
    </style>    
  </head>
  <body onload="init()">
            <tr style="height:110px">
              <td width="250" rowspan="2" class="search ss">
                <g:if test="${isLoginDenied}"><a class="button" href="javascript:void(0)" rel="nofollow" onclick="showLoginForm()">${message(code:'common.deliverhome')}</a></g:if>
                <g:else><g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link></g:else>
              </td>
              <td width="730" colspan="3" class="rent ss">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr>
              <td colspan="3" class="bg_shadow">              
                <div class="breadcrumbs" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" rel="v:url" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span>
                  ${infotext['header'+context?.lang]?:''}
                </div>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <ul class="collapsable_filters">
                  <li class="search_filter" id="unsubscribe_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('unsubscribe_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('unsubscribe_container');">${message(code:'unsubscribe.newsletter')}</a>        
                    <div class="search_filter_content" style="padding: 15px 6px">
                      <div id="subscribe" style="display:none">
                        <p style="margin:0"><g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml></p>
                      </div>
                      <g:formRemote name="sendForm" url="[controller:'index',action:'to_specoffer_refuse']" method="post" useToken="true" onLoading="document.getElementById('loader').show();" onLoaded="document.getElementById('loader').hide();" onSuccess="processResponse(e)">
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                        <g:if test="${!user}">
                          <tr>
                            <td style="padding-bottom:20px"><b>${infotext['promotext1'+context?.lang]?:''}</b></td>
                          </tr>         
                          <tr>
                            <td>
                              <label for="reg_email">email</label>
                              <input type="text" id="reg_email" name="email" maxlength="${stringlimit}" value="" />
                            </td>
                          </tr>
                          <tr>
                            <td>
                              <label for="reg_password">${message(code:'login.password')}</label>
                              <input type="password" id="password" name="password" value="${message(code:'login.password')}">
                            </td>
                          </tr>          
                        </g:if>
                          <tr>
                            <td>            
                              <div id="spec" style="padding-top:15px;display:none">
                                <input id="specButton" class="button-glossy orange" type="submit" value="${message(code:'unsubscribe.unsubscribe')}"/>                   
                              </div>
                            </td>
                          </tr>
                        </table>        
                      </g:formRemote> 
                      <div id="spec_already" style="display:none">
                        <p style="margin:0"><g:rawHtml>${infotext['itext3'+context?.lang]?:''}</g:rawHtml></p>
                      </div>   
                      <div id="errors" class="notice" style="display:none">
                        <ul>       
                          <li id="error1" style="display:none">${message(code:'error.blank',args:['Email'])} ${message(code:'faq.error.email')}</li>
                          <li id="error2" style="display:none">{message(code:'error.blank',args:[message(code:'login.password').capitalize()])}</li>
                          <li id="error3" style="display:none">${message(code:'restore.error.fail')}</li>
                          <!--<g:if test="${it==3}"><li>Вы подписались на рассылку новостей</li></g:if>-->                   
                        </ul>       
                      </div>   
                      <div id="loader" style="display: none">
                        <img src="${resource(dir:'images',file:'spinner.gif')}" border="0">
                      </div>
                    </div>
                  </li>
                </ul>
              </td>
              <td valign="top" colspan="3">              
                <div class="body shadow" style="min-height:485px">
                <g:if test="${infotext['itext'+context?.lang]}">
                  <div class="page-topic" style="border:none">
                    <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                  </div>
                </g:if>
                </div>
              </td>
            </tr>  
  </body>
</html>
