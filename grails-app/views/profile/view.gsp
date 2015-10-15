<html>
  <head>
    <title>${viewedUser?.nickname?:viewedUser?.firstname} - ${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${context?.lang?'':(infotext?.keywords?:'')}" />
    <meta name="description" content="${viewedUser?.description?shortString(text:viewedUser?.description,length:'200'):infotext['description'+context?.lang]?:''}" />
    <meta property="fb:app_id" content="${fb_api_key}" />
    <meta property="og:type" content="profile" />
    <meta property="og:site_name" content="StayToday" />
    <meta property="og:locale" content="${context?.lang?'en_US':'ru_RU'}" />
    <meta property="og:locale:alternate" content="${context?.lang?'ru_RU':'en_US'}" />    
    <meta property="og:url" content="${createLink(mapping:'pView'+context?.lang,params:[uid:'id'+viewedUser?.id],absolute:true)}" />
    <meta property="og:title" content="${viewedUser?.firstname?:viewedUser?.nickname}" />
    <meta property="og:description" content="${viewedUser?.description?shortString(text:viewedUser?.description,length:'200'):infotext['description'+context?.lang]?:''}" />
    <meta property="og:image" content="${(viewedUser?.picture && !viewedUser.is_external)?imageurl:''}${(viewedUser?.picture)?viewedUser?.picture:resource(dir:'images',file:'user-default-picture.png',absolute:true)}" />
    <link rel="canonical" href="${createLink(mapping:'pView'+context?.lang,params:[uid:'id'+viewedUser?.id],absolute:true)}" />
    <link rel="image_src" href="${(viewedUser?.picture && !viewedUser.is_external)?imageurl:''}${(viewedUser?.picture)?viewedUser?.picture:resource(dir:'images',file:'user-default-picture.png',absolute:true)}" />  
    <meta name="layout" content="main" />
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
    function viewCell(sObj1,iNum,sObj2){
      var tab = document.getElementById(sObj1);
      var tabs = tab.getElementsByTagName('li');      
      var layer = document.getElementById(sObj2);
      var divs = layer.getElementsByTagName('div');            
      var layers = new Array();      
      for (var i=0; i < divs.length; i++)
        if(divs[i].getAttribute("rel")=='layer')
          layers.push(i);        
      for (var i=0; i < tabs.length; i++){
        tabs[i].className = (i==iNum) ? 'selected' : '';
        divs[layers[i]].style.display = (i==iNum)? 'block' : 'none';
      }
    }
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
    function commentDelete(iId,ids){
      if (confirm('Точно удалить?')){
        <g:remoteFunction controller='home' action='commentDelete' onSuccess='afterDelComm(ids)' params="'id='+iId" />
      }
    }
    function afterDelComm(ids) {
      $('comments_col'+ids).innerHTML = (parseInt($('comments_col'+ids).innerHTML)-1).toString();
      if (ids == 1) $('commentsFormSubmit').click();
      else if (ids == 2) $('myCommentsFormSubmit').click();
      else $('commentsonmeFormSubmit').click();
    }
    function answerCommentResponse(e) {
      if (e.responseJSON.error){
        if (e.responseJSON.message){
          $('answerComment_error').show();
          $('answerComment_errorText').update(e.responseJSON.message);
        }
      } else {
        jQuery.colorbox.close();
      }
    }
    </g:javascript>
    <style type="text/css">
      div[rel="layer"]{ background: #fff }
      .actions { padding: 7px 10px }
      .hlisting .fn.title{margin-top:0!important}
      .hlisting .title .name{margin-left:40px}
      .hlisting .description{margin-left:20px}
    </style>    
  </head>
  <body>
            <tr style="height:110px">
              <td width="250" class="search">
                <g:if test="${isLoginDenied}"><a class="button" rel="nofollow" onclick="showLoginForm()">${message(code:'common.deliverhome')}</a></g:if>
                <g:else><a class="button" rel="nofollow" onclick="transit(${context.is_dev?1:0},['addnew','home'],'','','','${context?.lang}')">${message(code:'common.deliverhome')}</a></g:else>
              </td>
              <td colspan="3" class="rent ss">
                <h1>${infotext['promotext1'+context?.lang]?:''} ${viewedUser?.firstname?:viewedUser?.nickname}!</h1>                
              </td>
            </tr>
            <tr itemscope itemtype="http://schema.org/Person">
              <td width="250" valign="top">
                <ul class="collapsable_filters" style="width:240px">			    
                  <li class="search_filter" id="user_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('user_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('user_container');">
                      <g:if test="${viewedUser?.is_agent}">${message(code:'personal.agent')}</g:if><g:else>${message(code:'personal.owner')}</g:else>
                    </a>
                    <div class="search_filter_content">                    
                      <div class="description">
                        <h2 class="title" itemprop="name">${viewedUser?.nickname}</h2>                        
                        <div class="thumbnail shadow" style="height:auto;margin:5px 0 10px">
                          <img itemprop="image" width="200" alt="${viewedUser?.nickname}" title="${viewedUser?.nickname}" border="0" 
                            src="${(viewedUser?.picture && !viewedUser.is_external)?imageurl:''}${(viewedUser?.picture)?viewedUser?.picture:resource(dir:'images',file:'user-default-picture.png')}" />                                                  
                        </div>
                      </div>
                    <g:if test="${verify.owner}">
                      <g:link class="button-glossy orange maxi" controller="profile" action="edit">${message(code:'button.edit')}</g:link>                        
                    </g:if>                    
                    </div>                                            
                  </li>
                  <li class="search_filter" id="verification_container">
                    <a class="filter_toggle" href="javascript:void(0);" onclick="toggleFilter('verification_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" onclick="toggleFilter('verification_container');">${message(code:'personal.verification')}</a>
                    <div class="search_filter_content" style="padding: 15px 0px 15px 5px">
                      <ul class="verifications-list">
                        <li class="verifications-list-item ${(viewedUser?.modstatus==0)?'verifications-none':''}">
                          <div class="verifications-icon ${(viewedUser?.modstatus==0)?'none':''}"></div>
                          <div class="verifications-legend ${viewedUser?.provider}"></div>
                          <span class="label">${message(code:'personal.connect').capitalize()} ${viewedUser?.provider}</span><br/>
                          <span class="counts">${message(code:(user?.modstatus==1)?'personal.confirmed':'personal.notconfirmed')}</span>
                        </li>
                      <g:if test="${viewedUser?.tel}">
                        <li class="verifications-list-item ${(viewedUser?.is_telcheck==0)?'verifications-none':''}">
                          <div class="verifications-icon ${(viewedUser?.is_telcheck==0)?'none':''}"></div>
                          <div class="verifications-legend phone"></div>
                          <span class="label">${message(code:'personal.phone').capitalize()} ${message(code:(viewedUser?.is_telcheck==1)?'personal.confirmed.he':'personal.notconfirmed.he')}</span><br/>
                          <!--noindex--><span class="counts" nowrap>
                          <g:if test="${!verify.owner}">                  
                            ${(viewedUser?.tel).split('\\)')[0]})
                          <g:each in="${viewedUser?.tel.split('\\)')[1][0..-3]}" var="item" status="i">
                            <span class="p">&nbsp;&nbsp;</span>
                          </g:each>
                            ${(viewedUser?.tel)[-2,-1]}                  
                          </g:if><g:else>                  
                            ${viewedUser?.tel}
                          </g:else>
                          </span><!--/noindex-->
                        </li>
                      </g:if>               
                        <li class="verifications-list-item">
                          <div class="verifications-icon"></div>
                          <div class="verifications-legend positively_reviewed"></div>
                          <span class="label">${message(code:'personal.reviews.positive')}</span><br/>
                          <span class="counts" nowrap>${goodComments?.count?:0} ${message(code:'common.reviews')}</span>
                        </li>
                      </ul>              
                    </div> 
                  </li>
                <g:if test="${ownerClient?.is_reserve}">  
                  <li class="search_filter" id="bron_container">
                    <a class="filter_toggle" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('bron_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" rel="nofollow" onclick="toggleFilter('bron_container');">${message(code:'common.secure_payments')}</a>        
                    <div class="search_filter_content" style="padding:5px 0 15px">
                      <p class="clearfix">
                        <img class="thumbnail userpic" src="${resource(dir:'images/anonses',file:'bron.png')}" alt="${message(code:'common.secure_payment_via_website')}" title="${message(code:'common.secure_payment_via_website')}" hspace="12" align="left" style="border:none" />
                        <font color="#333">${message(code:'profile.secure.info')}</font>
                        <g:link class="tooltip" controller="index" action="oferta" fragment="use_6" target="_blank" title="${message(code:'common.secure_payments')}" base="${context?.absolute_lang}"><img alt="${message(code:'common.secure_payments')}" src="${resource(dir:'images',file:'question.png')}" border="0" style="margin-bottom:-2px" /></g:link>
                      </p>
                      <ul class="clearfix pay-list mini">                        
                      <g:each in="${payway}" var="it" status="i">
                        <li class="float"><span class="icons ${it.icon}" title="${it['name'+context?.lang]}"></span></li>
                      </g:each>
                      </ul>                      
                    </div>
                  </li>
                </g:if>                  
                <g:if test="${user && !verify.owner}"><!--noindex-->
                  <li class="search_filter" id="reviews_container">
                    <a class="filter_toggle" href="javascript:void(0);" onclick="toggleFilter('reviews_container');"></a>
                    <a class="filter_header" href="javascript:void(0);" onclick="toggleFilter('reviews_container');">${message(code:'profile.review.send')}</a>
                    <div class="search_filter_content" style="padding: 15px 10px 15px 0px">
                      <g:form action="addcomment" base="${context?.mainserverURL_lang}">
                        <table width="100%" cellpadding="5" cellspacing="5" border="0">
                          <tr>
                            <td colspan="2">
                              <textarea id="comtext" rows="3" cols="30" onKeyDown="textLimit(this.id);" onKeyUp="textLimit(this.id);" name="comtext" placeholder="текст отзыва" style="width:200px"/></textarea>
                            </td>
                          </tr>
                          <tr>
                            <td><label>${message(code:'profile.rating')}</label></td>
                            <td align="right">
                              <select name="rating">
                                <option value="0">${message(code:'profile.rating.neutral')}</option>
                                <option value="1">${message(code:'profile.rating.negative')}</option>
                                <option value="2">${message(code:'profile.rating.positive')}</option>
                              </select>
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" align="right">
                              <input type="submit" class="button-glossy orange" value="${message(code:'button.send')}">
                            </td>
                          </tr>
                        </table>
                        <input type="hidden" name="home_id" value="${viewedUser?.id}" />
                      </g:form>
                    </div>
                  </li><!--/noindex-->
                </g:if>
                </ul>
              </td>
              <td colspan="3" valign="top">
                <div class="content body shadow" style="min-height:736px">
                  <ul id="tabs1" class="subnav">
                    <li class="selected" onclick="viewCell('tabs1',0,'layers1')">                    
                      ${Infotext.findByControllerAndAction('personal','ads')['name'+context?.lang]} (${(data?.records?.size())?:0})
                    </li>
                    <li id="comments" onclick="viewCell('tabs1',1,'layers1');$('commentsFormSubmit').click();">                    
                      ${message(code:'list.reviews')} (<span id="comments_col1">${comments?.count?:0}</span>)
                    </li>
                    <li id="myComments" onclick="viewCell('tabs1',2,'layers1');$('myCommentsFormSubmit').click();">
                      ${message(code:'reviews.your')} (<span id="comments_col2">${myComments?.count?:0}</span>)
                    </li>
                    <li id="commentsOnMe" onclick="viewCell('tabs1',3,'layers1');$('commentsonmeFormSubmit').click();">                    
                      ${message(code:'reviews.you')} (<span id="comments_col3">${commentsOnMe?.count?:0}</span>)
                    </li>
                  </ul>
                  <div style="height:20px;margin-bottom:-20px" class="bg_shadow"></div>
                <g:if test="${viewedUser?.description}">
                  <div class="notice" style="margin:10px 10px 5px">                    
                    <p itemprop="description">${viewedUser?.description}</p>                  
                  </div>                                
                </g:if>
                  <div id="layers1" class="dashboard-content">
                    <div rel="layer">
                    <g:if test="${data?.records}">
                      <div class="search_container">
                        <div id="search_body">                          
                          <div id="results">
                            <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">                                             
                            <g:each in="${data?.records}" var="home" status="i">                      
                              <tr>
                                <td onmouseout="this.removeClassName('selected');" onmouseover="this.addClassName('selected');">
                                  <div class="hlisting offer-rent relative">
                                    <div class="item housing thumbnail shadow">
                                      <g:link mapping="${City.get(home.city_id)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}" params="${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:home.city]}" base="${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}">
                                        <img class="photo" width="200" height="140" src="${(home?.mainpicture)?photourl+'t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0" />
                                      </g:link>
                                    <g:if test="${home?.is_fiesta}">
                                      <div class="fiesta-container" title="${message(code:'common.party')}"></div>
                                    </g:if>
                                    </div>
                                    <div class="photo_item_details list">                                        
                                      <span class="price ss_price b-rub" style="padding-left:40px !important">
                                        ${Math.round((home?.pricestandard_rub?:0) / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>
                                      </span>
                                      <span class="ss_period">${message(code:'list.per_night')}</span>     
                                    </div>
                                    <div class="fn title">
                                      <g:link class="url name" mapping="${City.get(home.city_id)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}" params="${City.get(home.city_id)?.domain?[linkname:home?.linkname]:[linkname:home.linkname,country:Country.get(home.country_id)?.urlname,city:home.city]}" base="${City.get(home.city_id)?.domain?'http://'+City.get(home.city_id).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}" rel="self bookmark">${home?.name?:''}</g:link>
                                    </div>
                                    <div class="description">
                                      <ul class="details-list">
                                        <li class="details-list-item location clearfix">
                                          <span class="icons"></span>
                                          <g:shortString text="${home?.shortaddress?:'нет данных'}" length="56"/>
                                        </li>       
                                        <li class="details-list-item room_type clearfix">
                                          <span class="icons"></span>
                                        <g:each in="${hometype}" var="item"><g:if test="${item?.id==home?.hometype_id}">
                                          ${item['name'+context?.lang]}
                                        </g:if></g:each>
                                        </li>                                        
                                        <li class="details-list-item person_capacity clearfix">
                                          <span class="icons"></span>
                                        <g:each in="${homeperson}" var="item"><g:if test="${item?.id==home?.homeperson_id}">
                                          ${item['name'+context?.lang]}
                                        </g:if></g:each>
                                        </li>
                                        <li class="details-list-item review_count clearfix">
                                          <span class="icons"></span>${homecomments}
                                          ${home?.nref} ${message(code:'common.reviews')}
                                        </li>
                                      </ul>
                                    </div>
                                    <div class="lister fn">${User.findWhere(client_id:home?.client_id).nickname}</div>
                                    <ul class="services-list col">
                                    <g:each in="${homeoption}"><g:if test="${it.icon}">
                                      <li class="services-list-item ${it.icon}">
                                        <span class="icons ${home[it.fieldname]?'active':'passive'}" title="${it['name'+context?.lang]}"></span>                
                                      </li>              
                                    </g:if></g:each>
                                    </ul>
                                  </div>
                                </td>                                
                              </tr>
                              <g:if test="${(i+1)!=data.records.size()}"> 
                              <tr><td class="dashed">&nbsp;</td></tr>
                              </g:if>                              
                            </g:each>                                            
                            </table>
                          </div>
                        </div>
                      </div>
                    </g:if>
                    </div><!--noindex-->
                    <div rel="layer" style="display: none">
                      <div class="search-container">
                        <div id="search_body">
                          <g:formRemote name="commentsForm" url="[action:'viewComments']" update="[success:'review_list']">
                            <input type="submit" id="commentsFormSubmit" style="display:none" value="Показать"/>
                            <input type="hidden" name="client_id" value="${viewedUser?.client_id}"/>
                            <input type="hidden" name="u_id" value="${user?.id}"/>
                          </g:formRemote>
                          <div id="review_list"></div>
                        </div>
                      </div>
                    </div>
                    <div rel="layer" style="display: none">
                      <div class="search-container">
                        <div class="body">
                          <g:formRemote name="myCommentsForm" url="[action:'viewMyComments']" update="[success:'myreview_list']">
                            <input type="submit" id="myCommentsFormSubmit" style="display:none" value="Показать"/>
                            <input type="hidden" name="lId" value="${viewedUser?.id}"/>
                            <input type="hidden" name="u_id" value="${user?.id}"/>
                          </g:formRemote>
                          <div id="myreview_list"></div>
                        </div>
                      </div>
                    </div>
                    <div rel="layer" style="display: none">
                      <div class="search-container">
                        <div class="body">
                          <g:formRemote name="commentsonmeForm" url="[action:'viewCommentsOnMe']" before="jQuery('#reviewonme_list').html('');" update="[success:'reviewonme_list']">
                            <input type="submit" id="commentsonmeFormSubmit" style="display:none" value="Показать"/>
                            <input type="hidden" name="lId" value="${viewedUser?.id}"/>
                            <input type="hidden" name="u_id" value="${user?.id}"/>
                          </g:formRemote><!--/noindex-->
                          <div id="reviewonme_list">
                          <g:each in="${commentsOnMe.records}" var="comm">
                            <div itemprop="review" itemscope itemtype="http://schema.org/Review">
                              <span itemprop="author" itemscope itemtype="http://schema.org/Person">
                                <span itemprop="name">
                                  <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+comm.user_id]}" itemprop="url">${comm.nickname}</g:link>
                                </span>
                              </span>
                              <span itemprop="datePublished">${g.formatDate(date:comm?.comdate,format:'yyyy-MM-dd\'T\'HH:mmZ',locale:Locale.ENGLISH)}</span>
                              <p itemprop="${comm.rating==1?'contra':(comm.rating==2?'pro':'description')}">${comm.comtext}</p>
                            </div>
                          </g:each>
                          </div><!--noindex-->
                        </div>
                      </div>
                    </div>
                  </div>
                </div>                
                <div id="answerComment_lbox" class="new-modal" style="display:none">
                  <h2 class="clearfix">${message(code:'reviews.response')}</h2>
                  <g:formRemote name="answerCommentForm" url="${[controller:'home',action:'addAnswerComment']}" style="padding:5px" onSuccess="answerCommentResponse(e);">
                  <div id="answerComment_lbox_segment" class="segment nopadding">
                    <div id="answerComment_lbox_container" class="lightbox_filter_container" style="height:auto">
                      <div id="message_data"></div>         
                      <div id="answerComment_error" class="notice" style="width:97%;margin:0 0 10px;display:none">
                        <span id="answerComment_errorText"></span>
                      </div>          
                      <label for="answ_comtext">${message(code:'detail.message').capitalize()}:</label>
                      <textarea rows="4" cols="60" id="answ_comtext" onkeydown="textLimit(this.id);" onkeyup="textLimit(this.id);" name="answ_comtext" style="width:97%;height:70px;margin-top:3px"/></textarea>
                    </div>
                  </div>
                  <div class="segment buttons">                  
                    <input type="submit" class="button-glossy orange" value="${message(code:'button.send')}"/>
                    <input id="com_id" type="hidden" name="com_id" value="" />
                  </div>
                  </g:formRemote>
                </div><!--/noindex-->
              </td>
            </tr>
  </body>
</html>
