<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <link rel="canonical" href="${createLink(controller:'inbox',action:'index',base:context?.mainserverURL_lang)}" />
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(uri:'/inbox',base:context?.mobileURL_lang)}" />
    <link rel="alternate" media="handheld" href="${createLink(uri:'/inbox',base:context?.mobileURL_lang)}" />
    <meta name="layout" content="main" />
    <g:javascript>
    function initialize(){
    <g:if test="${session.attention_message!='' && session.attention_message!=null}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
    </g:if>
    <g:if test="${session.attention_message_once}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message_once}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
    </g:if>    
      startHomeSearch();
    }    
    function toggleFavorite(iId,owner,action){
      <g:remoteFunction action='selectmbox' onSuccess='setFavorite(iId)' params="'id='+iId+'&owner='+owner+'&act='+action" />
    }
    function setFavorite(iId){
      var lsText = document.getElementById('testtets').text.split('(')
      if( document.getElementById('starred_'+iId).children[0].className!='starred')
        document.getElementById('testtets').text = lsText[0]+'('+(parseInt(lsText[1].split(')')[0])+1)+')';
      else
        document.getElementById('testtets').text = lsText[0]+'('+(parseInt(lsText[1].split(')')[0])-1)+')';
      startHomeSearch();
    }    
    function toggleDelete(lId,action){ 
      if(confirm("${message(code:action?'inbox.confirm.delete':'inbox.confirm.restore')}")){
        <g:remoteFunction action='hidembox' onSuccess='location.reload()' params="'id='+lId+'&act='+action" />
      }
    }
    function startHomeSearch(){       
      $('submit_button').click();
    }
    </g:javascript>
    <g:setupObserve function='clickPaginate' id='results'/>
    <style type="text/css">.actions { padding: 7px 10px }</style>        
  </head>
  <body onload="initialize()">  
            <tr style="height:110px">
              <td width="250" class="search">
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>                                
              </td>
              <td colspan="3" class="rent ss">
                <h1>${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr>
              <td valign="top">
                <div class="relative">
                  <span class="count" style="top:0">
                    <b>${message(code:'inbox.records.found')}</b><span id="ads_count">${count}</span>
                  </span>
                </div>
              </td>
              <td colspan="3" valign="top">            
                <div class="content body shadow" style="min-height:547px">
                  <ul class="subnav">
                    <li class="${(controllerName=='personal' && actionName=='index')?'selected':''}">
                      <g:link controller="personal" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','index')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${(controllerName=='inbox' && actionName=='index')?'selected':''}">
                      <g:link controller="inbox" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('inbox','index')['name'+context?.lang]}</g:link>                
                    </li>
                    <li class="${(controllerName=='personal' && actionName in ['ads','adsoverview','editads','photo','homephoto','video','homevideo','map','homeprice','homeprop','calendar','infras','promote','bookings','waiting','rules','requirements'])?'selected':''}">
                      <g:link controller="personal" action="ads" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('personal','ads')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${(controllerName=='trip' && actionName in ['next','current','previous','favorite','cancellation','waiting'])?'selected':''}">
                      <g:link controller="trip" action="next" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('trip','next')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${(controllerName=='profile' && actionName in ['edit','verifyUser','photo','reviews'])?'selected':''}">
                      <g:link controller="profile" action="edit" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('profile','edit')['name'+context?.lang]}</g:link>
                    </li>
                    <li class="${(controllerName=='account' && actionName in ['index','payout','history','refferals','settings'])?'selected':''}">
                      <g:link controller="account" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('account','index')['name'+context?.lang]}</g:link>
                    </li>
                  </ul>                
                  <div class="dashboard-content">
                    <div rel="layer">                    
                      <div class="search-container">
                        <div id="search_body">
                          <g:formRemote name="inboxForm" url="[action:'inbox_list']" update="[success:'results']">                                               
                            <div style="display:none">
                              <input type="submit" id="submit_button" value="${message(code:'data.view')}"/>        
                              <input type="hidden" name="client" value="${inrequest?.client}"/>
                            </div>                            
                            <div class="results_header abs">                            
                              <span class="results_sort">
                                <label for="modstatus">${message(code:'data.list.view')}</label>
                                <select name="modstatus" id="modstatus" onchange="startHomeSearch();">
                                  <option value="-1" <g:if test="${inrequest?.modstatus==-1}">selected="selected"</g:if>>${message(code:'inbox.list.all')} (${count})</option>
                                  <option id="testtets" value="-2" <g:if test="${inrequest?.modstatus==-2}">selected="selected"</g:if>>${message(code:'inbox.list.favorite')} (${count_fav})</option>
                                  <option value="0" <g:if test="${inrequest?.modstatus==0}">selected="selected"</g:if>>${message(code:'inbox.list.deleted')} (${count_del})</option>
                                <g:each in="${modstatus}" var="item" status="i">
                                  <option value="${item?.modstatus}" <g:if test="${inrequest?.modstatus==item?.modstatus}">selected="selected"</g:if>>${item['name'+context?.lang]} (${status_count[i]})</option>
                                </g:each>
                                </select>
                              </span>
                              <span class="results_sort">
                                <label for="ord">${message(code:'sort_by')}</label>
                                <select name="ord" id="ord" onchange="startHomeSearch();">
                                  <option value="0" <g:if test="${inrequest?.ord==0}">selected="selected"</g:if>>${message(code:'common.sort.mod_date')}</option>
                                  <option value="1" <g:if test="${inrequest?.ord==1}">selected="selected"</g:if>>${message(code:'common.sort.trip_date')}</option>              
                                </select>
                              </span>
                            </div>                            
                          </g:formRemote>                                                          
                          <div id="results"></div>	
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
