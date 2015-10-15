<g:javascript>
  function toggleContainer(container){ 
    var toggle = document.getElementById(container);      
    if(toggle.className == 'set-availability action_button')
      toggle.className = 'set-availability action_button expanded';
    else if(toggle.className == 'set-availability action_button expanded')
      toggle.className = 'set-availability action_button';        
    $('toggle-info').toggle();
  }  
	function pblHome(st){
    <g:remoteFunction controller='personal' action='publishHome' onSuccess='location.reload(true)' params="'id='+st" />
	}    
</g:javascript>
<style type="text/css">
  .rent h1.header { margin: 18px 0 10px 20px  }
  .message-item   { margin: -12px 15px 0 20px }
  .action_button  {	background: #FF530D; border: 1px solid #ccc; }
  .action_button a.icon, .action_button .icon, .action_button div.instructions { color: #fff !important; text-shadow: 1px 1px 0 rgba(132, 132, 132, 0.6); }
  .action_button .icon.active   { color: #BFF358 !important; text-shadow: 1px 1px 0 rgba(132, 132, 132, 0.6); }
  .action_button .icon.inactive { color: #A50F00 !important; text-shadow: 1px 1px 0 rgba(228, 228, 228, 0.6); }
</style>
            <tr style="height:110px">
              <td width="250" class="search">
                <g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link>                                
              </td>
              <td colspan="3" class="rent s0">              
                <div class="thumbnail" id="homepic" style="margin-left:10px;width:140px;height:98px">
                  <g:if test="${home?.mainpicture}"><img width="140" height="98" id="homepic_img" src="${imageurl}${'t_'+home?.mainpicture}"></g:if>
                  <g:else><img width="140" height="98" src="${resource(dir:'images',file:'default-picture.png')}"></g:else>
                </div>
                <h1 class="header">${home?.name}</h1>                
                <div class="relative">
                  <div class="actions float padd20">
                  <g:if test="${home?.modstatus in [1,3]}">
                    <div id="toggle" class="set-availability action_button" onclick="toggleContainer('toggle');">
                    <g:if test="${home?.modstatus == 3}">
                      <span class="clearfix icon inactive">
                        <span class="label">${message(code:'personalmenu.home.hidden')}</span>
                        <span class="expand"></span>
                      </span>
                      <div id="toggle-info" style="display:none">
                        <div class="instructions">${message(code:'personalmenu.home.activate.text')}:</div>
                        <div class="toggle-action-container">
                          <a class="icon active" href="javascript:void(0)" onclick="pblHome(${home?.id});">${message(code:'personalmenu.home.activate')}</a>
                        </div>
                      </div> 
                    </g:if><g:elseif test="${home?.modstatus == 1}">
                      <span class="clearfix icon active">
                        <span class="label">${message(code:'personalmenu.home.actively')}</span>
                        <span class="expand"></span>
                      </span>
                      <div id="toggle-info" style="display:none">
                        <div class="instructions">${message(code:'personalmenu.home.hide.text')}:</div>
                        <div class="toggle-action-container">
                          <a class="icon inactive" href="javascript:void(0)" onclick="pblHome(${home?.id});">${message(code:'personalmenu.home.hide')}</a>
                        </div>
                      </div>                 
                    </g:elseif>
                    </div>                
                  </g:if>              
                    <span class="action_button">
                      <g:link class="icon view" target="_blank" mapping="${'hView'+context?.lang}" params="${[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}">${message(code:'personalmenu.home.view')}</g:link>
                    </span>                
                  </div>                              
                  <div class="message-item col">                
                    <div class="message-text" style="background-image: url(${resource(dir:'images',file: homemodstatus?.icon+'.png')});">
                      <b>${homemodstatus['name'+context?.lang]}</b>
                    </div>
                  </div>            
                </div>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <b><g:link controller="personal" action="ads" class="to-parent" base="${context?.absolute_lang}">${message(code:'personalmenu.tolist')}</g:link></b>
                <div class="header-list-item" style="padding:0px">
                  <div class="header-dropdown personal" style="top:0">
                    <div class="header-dropdown-section">
                      <ul class="header-dropdown-list">
                      <g:each in="${personalmenu}" var="item">                      
                        <li class="header-dropdown-list-item">
                        <g:if test="${item?.action=='infras' && !(home?.modstatus in [1,3])}">
                          <a href="javascript:void(0)" class="${item?.icon}" style="color:#333">
                            <span class="icon" style="background-position: -44px -110px;"></span>                          
                            ${item['name'+context?.lang]}                        
                          </a>
                        </g:if><g:else>
                          <g:link class="${item?.icon}${(controllerName == item?.controller && actionName in [item?.action, ((item?.action=='photo')?'homephoto':((item?.action=='video')?'homevideo':((item?.action=='homeprice')?'homeprop':'')))])?' active':''}" 
                            controller="${item?.controller}" action="${item?.action}" params="${[id:home?.id]}" base="${context?.mainserverURL_lang}">
                            <g:if test="${controllerName == item?.controller && item?.action!='adsoverview'}"><span class="icon"></span></g:if>
                            ${item['name'+context?.lang]}
                          </g:link>
                        </g:else>
                        </li>
                      </g:each>
                      </ul>
                    </div>
                  </div>
                </div>
              </td>
              <td colspan="3" valign="top">            
                <div class="content body shadow" style="min-height:532px">
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
                    <li class="${(controllerName=='account' && actionName in ['index','payout','paydocuments','payU','payUresult','liqpay','liqpayresult','wmoney','wmsuccess','wmfail','history','payorderdetail','refferals','settings','trippayment'])?'selected':''}" style="width:55px">
                      <g:link controller="account" action="index" base="${context?.absolute_lang}">${Infotext.findByControllerAndAction('account','index')['name'+context?.lang]}</g:link>
                    </li>
                  </ul>                
                  <div class="dashboard-content">
                    <div rel="layer">
                    <g:if test="${controllerName=='personal' && actionName in ['photo','homephoto','video','homevideo','homeprice','homeprop']}">
                      <div class="content">
                        <ul class="nav">
                        <g:if test="${controllerName=='personal' && actionName in ['photo','homephoto']}">                          
                          <li id="upload-tab" class="${(controllerName=='personal' && actionName=='photo')?'selected':''}">                    
                            ${Infotext.findByControllerAndAction('personal','photo')['name'+context?.lang]}
                          </li>
                          <li id="edit-tab" class="${(controllerName=='personal' && actionName=='homephoto')?'selected':''}">                    
                            ${Infotext.findByControllerAndAction('personal','homephoto')['name'+context?.lang]}
                          </li>
                        </g:if>
                        <g:elseif test="${controllerName=='personal' && actionName in ['video','homevideo']}">                          
                          <li id="upload-tab" class="${(controllerName=='personal' && actionName=='video')?'selected':''}">                    
                            ${Infotext.findByControllerAndAction('personal','video')['name'+context?.lang]}
                          </li>
                          <li id="edit-tab" class="${(controllerName=='personal' && actionName=='homevideo')?'selected':''}">                    
                            ${Infotext.findByControllerAndAction('personal','homevideo')['name'+context?.lang]}
                          </li>
                        </g:elseif>  
                        <g:elseif test="${controllerName=='personal' && actionName in ['homeprice','homeprop']}">
                          <li id="upload-tab" class="${(controllerName=='personal' && actionName=='homeprice')?'selected':''}">                    
                            ${Infotext.findByControllerAndAction('personal','homeprice')['name'+context?.lang]}
                          </li>
                          <li id="edit-tab" class="${(controllerName=='personal' && actionName=='homeprop')?'selected':''}">                    
                            ${Infotext.findByControllerAndAction('personal','homeprop')['name'+context?.lang]}
                          </li>
                        </g:elseif>                  
                        </ul>                
                        <div class="dashboard-content">
                    </g:if>
