<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>    
    <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
    <meta name="layout" content="m" />
    <script type="text/javascript">
      var popupcont='';
      function init() {           
        jQuery('#ul_filter.ui-listview li').click(function(){
          jQuery(this).parent().find('li.ui-btn-active').removeClass('ui-btn-active');
          jQuery(this).addClass('ui-btn-active');
        });
        jQuery('#sortFilter.ui-listview li').click(function(){
          jQuery(this).parent().find('li.ui-btn-active').removeClass('ui-btn-active');
          jQuery(this).addClass('ui-btn-active');
        });
      }
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null)
          return;
        new Ajax.Updater(
          { success: $('output') },
          link.href,
          { evalScripts: true, onComplete: function(response) { jQuery('#ajax_wrap').listview(); } }
        );
      }
      function setSort(iId){
        jQuery('#sort').val(iId);
      }      
      function setModstatus(iId){
        jQuery('#modstatus').val(iId);
      }
      function searchMbox(){        
        $('filter_form_submit').click();        
      }  
      function doremotesubmit(event){
        event.stop();
        <g:remoteFunction controller='m' action='inbox_list' params="\$('filter_form').serialize()+'&'+jQuery('#popupFiltr :input[value]').serialize()" onSuccess="jQuery('#output').html(e.responseText);jQuery('#ajax_wrap').listview();" before="\$(popupcont).click();"/>
      }
    </script>
  </head>
  <body onload="init()">
      <h1 role="heading">${infotext['header'+context?.lang]?:''}</h1>
      <div data-role="content" style="padding:0 0">      
        <ul id="output" data-role="listview" data-split-icon="arrow-r" style="margin:10px 0 0 0">
        <g:if test="${data.records}">
          <li data-role="divider" data-theme="a" style="padding:0 0 0 10px;height:40px">
            <span class="count float">
              <span>${message(code:'mobile.found')}</span><span id="ads_count">${data.count}</span>
            </span>
            <span class="pagination col">
              <g:paginate controller="m" action="inbox_list" prev="&lt;" next="&gt;" 
                maxsteps="1" omitFirst="${true}" omitLast="${true}" max="5" total="${data.count}" params="${params}" />
              <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>              
            </span>
          </li>
          <g:each in="${data.records}" var="item"> 
          <li data-icon="false">
            <a href="${createLink(controller:'m',action:'mbox',id:item.id,base:context?.mobileURL_lang)}" title="${message(code:'inbox.list.detail')}">
              <div class="ui-li-thumb relative" style="width:90px;max-width:90px;max-height:130px;margin-right:5px">
                <g:if test="${((item.homeclient_id==user.client_id)&&(item.is_answer==0)&&(item.is_read==0))||((item.homeclient_id!=user.client_id)&&(item.is_answer==1)&&(item.is_read==0))}"><div class="new" style="right:0">!</div></g:if>
              <g:if test="${item.homeclient_id==user.client_id}">  
                <img class="thumbnail userpic shadow" alt="${item.client_nickname}" title="${item.client_nickname}" border="0" src="${((item.client_picture && !item.client_external)?imageurl:'')+(item.client_picture?:resource(dir:'images',file:'default-picture.png'))}" />
              </g:if><g:else>
                <img class="thumbnail userpic shadow" alt="${item.home_name}" title="${item.home_name}" border="0" src="${item.homepicture?urlphoto+item.homeclient_id+'/t_'+item.homepicture:resource(dir:'images',file:'default-picture.png')}" />
              </g:else>
                <p class="ui-li-desc" style="margin-top:3px;line-height:10px">
                  <small style="white-space:normal">
                    <g:if test="${(Home.get(item?.home_id)?.client_id?:0) == user?.client_id}">${item.client_nickname}</g:if>
                    <g:else>${item.user_nickname}</g:else>
                  </small><br/>
                  <small style="white-space:nowrap">${String.format('%td.%<tm.%<tY %<tH:%<tM',item.moddate)}</small>
                </p>
              </div>
            <g:if test="${item.homeclient_id == user.client_id}">
              <h2 style="color:${(item.is_answer==0 && item.is_read==0)?'#FF530D':'#3f5765'}!important">
                ${item.mtextowner?:message(code:'personal.mbox.with')+item.user_nickname}
            </g:if><g:else>
              <h2 style="color:${(item.is_answer==1 && item.is_read==0)?'#FF530D':'#3f5765'}!important">
                ${item.mtext?:message(code:'personal.mbox.with')+item.user_nickname}
            </g:else>
              </h2>
              <ul class="ui-li-desc details-list">
                <li class="details-list-item location">
                  <span class="icons"></span>${(item?.homeclient_id==user?.client_id)?item?.home_address:item?.shortaddress}
                </li>
                <li class="details-list-item dates"><span class="icons"></span>
                  ${String.format('%td.%<tm.%<tY',item?.date_start)} - ${String.format('%td.%<tm.%<tY',item?.date_end)}
                </li>
                <li class="details-list-item person_capacity"><span class="icons"></span>
                  ${Homeperson.get(item.homeperson_id)['name'+context?.lang]}
                </li>
              </ul>
              <div class="ui-li-desc">
                <p class="ui-li-aside" style="width:180px;margin-top:5px">
                <g:if test="${item.answertype_id>0}">
                  <font style="color:${Answertype.get(item.answertype_id)?.color}">${Answertype.get(item.answertype_id)['name_mbox'+context?.lang]}</font>
                </g:if><g:else><font style="color:gray">${message(code:'personal.request')}</font></g:else>
                  <br/><span class="b-rub">${Math.round(item?.price_rub / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml></span><br/>
                  <font color="#333">${message(code:'personal.cost')}</font>
                </p>
              </div>
            </a>
          </li>
          </g:each>        
        </g:if><g:else>
          <li class="ui-li ui-li-static">${message(code:'mobile.messages.notfound')}</li>        
        </g:else>      
        </ul>
      </div>
      <div id="footer_inbox" data-role="footer" data-position="fixed" data-theme="a">        
        <form onsubmit="doremotesubmit(event)" name="filter_form" id="filter_form">
          <fieldset class="ui-grid-a">                          
            <div class="ui-block-a">
              <a href="#popupFiltr" data-role="button" data-rel="popup" data-overlay-theme="c" data-theme="f" data-inline="false" onclick="popupcont='popupFiltrClose'">${message(code:'filter_by')}</a>
              <div id="popupFiltr" data-role="popup" data-position-to="window" data-overlay-theme="a" data-history="false">
                <a href="#" id="popupFiltrClose" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">${message(code:'button.close')}</a>
                <ul id="ul_filter" data-role="listview" data-inset="true" data-theme="a">
                  <li data-role="divider" data-theme="f">${message(code:'filter_by')}</li>                  
                  <li class="ui-btn-active"><a href="#" onclick="setModstatus(-1);searchMbox()">${message(code:'inbox.list.all')} (${count})</a></li>
                  <li><a id="testtets" href="#" onclick="setModstatus(-2);searchMbox()">${message(code:'inbox.list.favorite')} (${count_fav})</a></li>
                  <li><a href="#" onclick="setModstatus(0);searchMbox()">${message(code:'inbox.list.deleted')} (${count_del})</a></li>
                <g:each in="${modstatus}" var="item" status="i">
                  <li><a href="#" onclick="setModstatus(${item?.modstatus});searchMbox()">${item['name'+context?.lang]} (${status_count[i]})</a></li>
                </g:each>                  
                </ul>              
              </div>          
            </div>
            <div class="ui-block-b">
              <a href="#popupSort" data-role="button" data-rel="popup" data-overlay-theme="c" data-theme="f" data-inline="false" onclick="popupcont='popupSortClose'">${message(code:'sort_by').capitalize()}</a>
              <div id="popupSort" data-role="popup" data-position-to="window" data-overlay-theme="a" data-history="false">
                <a href="#" id="popupSortClose" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">${message(code:'button.close')}</a>
                <ul id="sortFilter" data-role="listview" data-inset="true" data-theme="a">
                  <li data-role="divider" data-theme="f">${message(code:'sort_by')}</li>  
                  <li class="ui-btn-active"><a href="#" onclick="setSort(0);searchMbox()">${message(code:'common.sort.mod_date')}</a></li>
                  <li><a href="#" onclick="setSort(1);searchMbox()">${message(code:'common.sort.trip_date')}</a></li>
                </ul>
              </div>                
            </div>
          </fieldset>
          <input type="hidden" name="ord" id="sort" value="${inrequest?.ord?:0}" />
          <input type="hidden" name="modstatus" id="modstatus" value="-1" />
          <div style="display:none">
            <input type="submit" id="filter_form_submit" />
          </div>
        </form>
      </div>
  </body>
</html>
