<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main"/>
    <g:javascript library='prototype/autocomplete' />
    <calendar:resources lang="${context?.lang?'en':'ru'}" theme="tiger"/>
    <g:javascript> 
    var opened_region = 0;
    var mouseOnReg  = 0;
    var opened_popdirection = 0;
    var mouseOnDir  = 0;
    function onKey(e){
      var iKeycode;
      if (window.event) iKeycode = window.event.keyCode;
      else if (e) iKeycode = e.which;
      else return true;    
      if (iKeycode == 13)
        submitForm();
    }	
    function initialize() {
      $('where').onkeypress=onKey;	  
      new Autocomplete('where', { serviceUrl:'${resource(dir:'home',file:'where_autocomplete')}' });  
    }    
    function submitForm(){
      if($("where").value.length > 2){
        $("where").removeClassName('red0');
        $("where1").value = $("where").value;
        $("search_form").submit();
      }else{
        $("where").addClassName('red0');
      }
    }
    function togglePopDirection(iType){
      if ($("select_popdirection").style.display == 'none'){
        updatePopDirection(iType);
        opened_popdirection = 1;
      } else {
        jQuery("#select_popdirection").slideToggle();
        opened_popdirection = 0;
      }
    }
    function updatePopDirection(iType){
      <g:remoteFunction controller='index' action='selectpopcities' update='select_popdirection' onComplete="jQuery('#select_popdirection').slideDown()" params="\'country_id=\'+iType" />
    }    
    function clickPopDirection(sWhere){
      <g:remoteFunction controller='home' action='cityselectstat'/>
      $('where1').value = sWhere;
      $("search_form").submit();
    }
    function checkDropDowns(){
      if (opened_popdirection){
        if (!mouseOnDir){
          opened_popdirection = 0;
          $("select_popdirection").style.display = 'none';
        }
      }
    }
    </g:javascript>      
  </head>
  <body onload="initialize()">
            <tr style="height:140px">
              <td width="250" class="rent">
              <g:if test="${isLoginDenied}"><a rel="nofollow" onclick="showLoginForm()">${message(code:'common.deliverhome')}</a></g:if>
              <g:else><g:link controller="home" action="addnew" base="${context?.mainserverURL_lang}">${message(code:'common.deliverhome')}</g:link></g:else>
              </td>
              <td width="730" colspan="3" class="search"><!--noindex-->
                <g:form name="search_form" id="search_form" url="${[controller:'home',action:'search']}" base="${context?.mainserverURL_lang}" method="get">
                  <table class="float" width="553" cellpadding="5" cellspacing="0" border="0">
                    <tr>
                      <td width="145" valign="top" class="padd10">
                        <label for="where">${message(code:'common.where')}</label>
                        <input type="text" id="where" name="wre" maxlength="${stringlimit}" value="" placeholder="${message(code:'common.where_default')}" autocomplete="off" style="width:145px"/>
                        <div id="where_autocomplete" class="autocomplete" style="display:none"></div>			
                      </td>                  
                      <td width="107" valign="top">
                        <label for="hometype_id">${message(code:'common.home_type')}</label>
                        <select id="hometype_id" name="hometype_id" style="width:107px">
                          <option <g:if test="${0==(inrequest?.hometype_id?:0)}">selected="selected"</g:if> value="">${message(code:'common.any')}</option>
                        <g:each in="${hometype}" var="item">            
                          <option <g:if test="${item?.id==inrequest?.hometype_id}">selected="selected"</g:if> value="${item.urlname}">${item.name}</option>
                        </g:each>
                        </select>
                      </td>
                      <td width="43" valign="top">
                        <label>${message(code:'common.guests')}</label>
                        <select id="homeperson_id" name="homeperson_id" style="width:43px">
                        <g:each in="${homeperson}" var="item">            
                          <option <g:if test="${item?.id==inrequest?.homeperson_id}">selected="selected"</g:if> value="${item?.id}">${item?.kol}</option>
                        </g:each>
                        </select>                  
                      </td>
                      <td width="85" valign="top">
                        <label>${message(code:'common.date_from')}</label>
                        <span class="dpicker">
                          <calendar:datePicker name="date_start" value="${inrequest?.date_start}" dateFormat="%d-%m-%Y"/>
                        </span>
                      </td>
                      <td width="85" valign="top" class="dpicker">
                        <label>${message(code:'common.date_to')}</label>
                        <span class="dpicker">
                          <calendar:datePicker name="date_end" value="${inrequest?.date_end}" dateFormat="%d-%m-%Y"/>                
                        </span>
                      </td> 
                    </tr>
                    <tr>
                      <td colspan="3" valign="top" class="padd10">
                        <div class="dropdown-list relative">
                          <span>
                            <small id="a_select_popdirection" class="select" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0" onclick="togglePopDirection(1)"><a rel="nofollow" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0">${message(code:'common.choose_city')}</a></small>
                            <div class="select_dropdown" id="select_popdirection" style="display:none" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0"></div>              
                          </span>              
                        </div>
                      </td>
                      <td colspan="2" align="center">
                        <div class="dropdown-list relative">
                          <small class="link"><g:link controller="index" action="waiting" base="${context?.mainserverURL_lang}">${message(code:'index.sendrequest')}</g:link></small>                          
                        </div>
                      </td>
                    </tr>
                  </table>                  
                  <input type="hidden" id="where1" name="where" value="" />
                </g:form><!--/noindex-->
                <a class="button main col" rel="nofollow" onclick="submitForm()">${message(code:'common.renthome')}</a>                
              </td>
            </tr>
            <tr style="height:455px">
              <td width="250" valign="top">
                <h2 class="padd20">${message(code:'index.pop_directions')}</h2>
                <ul class="search_filter_content" style="padding: 0 10px 0 20px">
                <g:each in="${countries}" var="country" status="i"><g:if test="${country.id in countryIds}">
                  <!--<li class="clearfix country">
                    <h3><span class="flagcon ${country?.icon}"></span>
                    <g:if test="${country.is_index}"><a href="<g:createLink controller='index' action='popdirectionAll' params='[id:country?.urlname]'/>"></g:if>
                    <g:else><span class="link" onclick="transit(${context.is_dev?1:0},['${country?.urlname}'])"></g:else>
                    ${country['name'+context?.lang]}<g:if test="${country.is_index}"></a></g:if><g:else></span></g:else></h3>
                  </li>-->
                  <g:each in="${popdirection}" var="record" status="j"><g:if test="${country.id==record.country_id}">
                  <li class="clearfix dott ${(j==0)?'first':''}">
                    <b><g:if test="${record.is_index}"><a class="show_more_link" href="<g:createLink controller='index' action='direction' id='${record.linkname}' params='${[country:country.urlname]}'/>"></g:if>
                    <g:else><span class="show_more_link" onclick="transit(${context.is_dev?1:0},['${record.linkname}','${country.urlname}'],'','','','${context?.lang}')"></g:else>
                    ${record['name2'+context?.lang]}<g:if test="${record.is_index}"></a></g:if><g:else></span></g:else></b><br/>
                    <p class="desc">${record['name'+context?.lang]}</p>
                  </li>
                  </g:if></g:each>
                </g:if></g:each>    
                  <li class="clearfix dott last" style="text-align:right">
                    <g:link class="show_more_link" controller="index" action="popdirectionAll" base="${context?.mainserverURL_lang}">${message(code:'index.all_directions')}</g:link> <font color="#FF530D">&raquo;</font>
                  </li>
                </ul>
              </td>
              <td width="730" colspan="3" valign="top" align="center">
                <div class="body shadow" style="width:710px;min-height:565px">
                  <div class="padd20 paddtop" align="left">
                    <h1>${infotext['header'+context?.lang]?:''}</h1>
                    <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>                    
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td colspan="4" class="paddtop">        
                <ul class="homepage_badges">
                <g:each in="${specoffer_records}" var="home" status="i">
                  <li style="margin-left:${(i==0)?'21':'10'}px">
                    <div class="badge-content-container shadow dark">
                      <g:link mapping="${City.findByName(home.city)?.domain?('hViewDomain'+context?.lang):('hView'+context?.lang)}" params="${City.findByName(home.city)?.domain?[linkname:home?.linkname]:[country:Country.get(home.country_id)?.urlname,city:home.city,linkname:home?.linkname]}" base='${City.findByName(home.city)?.domain?'http://'+City.findByName(home.city).domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL}' target="_blank">
                        <img width="220" height="160" src="${(home?.mainpicture)?urlphoto+home?.client_id+'/t_'+home?.mainpicture:resource(dir:'images',file:'default-picture.png')}" border="0"/>
                        <div class="slideshow_item_details">
                          <div class="slideshow_item_details_text">
                            <div class="ss_details_top">
                              <span class="ss_name"><g:shortString text="${home?.name}" length="18"/></span><br/>
                              <span class="ss_location"><g:shortString text="${home?.shortaddress}" length="22"/></span>
                            </div>                            
                          </div>
                        </div>
                        <div class="photo_item_details">
                          <span class="ss_price b-rub">              
                            ${Math.round(home?.price / valutaRates)}&nbsp;<g:rawHtml>${valutaSym}</g:rawHtml>
                          </span>                            
                        </div>
                      </g:link>
                    </div> 
                  </li>
                </g:each>
                </ul>
              </td>
            </tr>            
  </body>
</html>   
