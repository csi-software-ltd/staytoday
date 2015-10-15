<html>
  <head>    
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />         
    <meta name="layout" content="main"/>
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
      document.getElementById('linktext').select()
      //textCounter('linkname','linkname_limit',${(inrequest?.linkname.size()>35)?inrequest?.linkname.size():35});
    }
    function textCounter(sId,sLimId,iMax){
      var symbols = $F(sId);
      var len = symbols.length;
      if(len > iMax){
        symbols = symbols.substring(0,iMax);
        $(sId).value = symbols;
        return false;
      }
      $(sLimId).value = iMax-len;
    }
    function clearForm(){
      setTimeout("textCounter('linkname','linkname_limit',${(inrequest?.linkname.size()>35)?inrequest?.linkname.size():35})",100);
    }
    </g:javascript>
  </head>
  <body onload="initialize();">
              <g:render template="/personal_menu" />
                    <g:if test="${(flash?.error?:[]).size()>0}">
                      <div class="notice">
                        <ul>
                        <g:each in="${flash?.save_error}">
                          <g:if test="${it==10}"><li>Ошибка данных в справочниках</li></g:if>
                        </g:each>
                        </ul>
                      </div>
                    </g:if>
                      <div class="form">
                        <g:form name="mapForm" url="${[controller:'personal',action:'promotesave',id:inrequest?.id?:0]}" method="POST" useToken="true" base="${context?.mainserverURL_lang}">
                          <h2 class="toggle border"><span class="edit_icon address"></span>${infotext['header'+context?.lang]?:''}</h2>
                        <g:if test="${infotext['itext'+context?.lang]}">
                          <div class="padd20">
                            <g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml>
                          </div>
                        </g:if>
                          <table class="form paddtop" width="100%" cellpadding="5" cellspacing="0" border="0">
                            <tr>
                              <td>
                                <label for="linkname">${message(code:'ads.home.link')}</label>
                                <input type="text" id="linktext" readonly style="width:66%" value="${g.createLink(base: (hcity?.domain?'http://'+hcity.domain+(context.is_dev?':8080/Arenda':''):context.mainserverURL), mapping:(hcity?.domain?'hViewDomain'+context?.lang:'hView'+context?.lang), params:(hcity?.domain?[linkname:inrequest.linkname]:[country:Country.get(inrequest.country_id)?.urlname,city:inrequest.city,linkname:inrequest.linkname]))}">
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:20px">
                                <!--div class="float">
                                  <input type="submit" class="button-glossy orange" value="Сохранить" style="margin-right:5x"/>
                                  <input type="reset" class="button-glossy grey" value="Сброс" onClick="clearForm()"/>
                                </div-->
                                <span class="actions col">
                                  <span class="action_button orange">
                                    <g:link class="icon none" target="_blank" controller="personal" action="adsoverview" id="${home?.id}" base="${context.sequreServerURL}">${Infotext.findByControllerAndAction('personal','adsoverview')['name'+context?.lang]}</g:link>
                                  </span>                
                                </span>
                              </td>
                            </tr>
                          </table>			  
                        </g:form>
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
