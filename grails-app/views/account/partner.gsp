<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main"/>
    <g:javascript>
    function payoutResponse(e){
      var sErrorMsg='';
      ['confirmdogovor','partnerway_id','prequisite'].forEach(function(ids){
        if($(ids))
          $(ids).removeClassName('red');
      });
      if(e.responseJSON.errorsList.length){
        e.responseJSON.errorsList.forEach(function(err){
          switch(err){
            case 1: sErrorMsg+="${message(code:'payout.error.offer.confirm')}<br/>"; $('confirmdogovor').addClassName('red'); break;
            case 2: sErrorMsg+="${message(code:'partner.error.partnerway')}<br/>"; $('partnerway_id').addClassName('red'); break;
            case 3: sErrorMsg+="${message(code:'partner.error.prequisite')}<br/>"; $('prequisite').addClassName('red'); break;
            case 101: sErrorMsg+="${message(code:'error.db')}<br/>"; break;
          }
        });
        if(sErrorMsg.length){
          $('errorsText').innerHTML=sErrorMsg;
          $('errorsText').show();
        } else {
          $('errorsText').hide();
        }
      } else if(e.responseJSON.where){
        location.assign(e.responseJSON.where);
      } else {
        location.reload(true)
      }
    }
    </g:javascript>
    <style type="text/css">
      .form label.nopadd { min-width: 145px }
      ul.list { margin: 0 !important; padding: 0 !important; border-top: none !important; }
      ul.list li { padding: 0 0 5px 0 !important; margin-left: 20px !important; font: normal 13px/18px Tahoma !important; border-bottom: none !important; }
      ul.top { margin-left: 20px !important; } 
      ul.top li { list-style-type: square !important; margin-left: 20px !important; } 
    </style>
  </head>
  <body>
              <g:render template="/account_menu" />
                      <h2 class="toggle border"><span class="edit_icon prices"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                      <g:formRemote name="partnerConfirmationForm" url="[action:'partnerconfirmation']" method="post" onSuccess="payoutResponse(e)" onLoading="\$('savePartnerProcessLoader').show();" onLoaded="\$('savePartnerProcessLoader').hide();">
                        <div id="errorsText" class="notice" style="margin:5px 10px;${client?'display: none':''}">
                          ${message(code:'payout.notice.noclient')}
                        </div>
                        <table class="form" width="100%" cellpadding="5" cellspacing="0" border="0">
                          <tr>
                            <td style="padding:0 7px 0">
                              <b><label for="partnerway_id" class="nopadd">${message(code:'partner.scheme')}</label></b>
                              <select name="partnerway_id" id="partnerway_id" style="width:200px" onchange="this.options[this.selectedIndex].onfocus()">
                              <g:each in="${partnerway}" var="item">
                                <option value="${item.id}" onfocus="$('waydesc').update('${item['description'+context?.lang]}')" <g:if test="${item.id==client?.partnerway_id}">selected="selected"</g:if>>${item['name'+context?.lang]}</option>
                              </g:each>
                              </select>
                            </td>
                            <td style="padding:0 10px 5px">
                              <b>${message(code:'account.status')}</b>: &nbsp; <b id="status"></b>
                              <g:javascript>
                                var color, status;
                                switch(${client?.partnerstatus?:0}){
                                  case -1: color='red'; status='${message(code:'payout.scheme.decline')}'; break;
                                  case  1: color='gray'; status='${message(code:'payout.scheme.reqconnect')}'; break;
                                  case  2: color='green'; status='${message(code:'payout.scheme.confirm')}'; break;
                                  case  3: color='gray'; status='${message(code:'payout.scheme.reqchange')}'; break;
                                  default: color='red'; status='${message(code:'payout.scheme.noconnect')}';
                                }
                                $("status").update('<font color="'+color+'">'+status+'</font>');
                              </g:javascript>
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:10px 25px;color:#FF530D" id="waydesc">
                              <g:rawHtml>${Partnerway.get(client?.partnerway_id?:1)?."description$context.lang"?:''}</g:rawHtml>
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:15px 5px">
                              <label for="prequisite" class="nopadd">${message(code:'partner.requisite.label')}</label>
                              <input type="text" id="prequisite" name="prequisite" value="${client?.prequisite}" maxlength="500" style="width:71%!important" />
                            </td>
                          </tr>
                          <tr id="dogovortext">
                            <td colspan="2" class="padd10">
                              <div id="dogovortextarea" style="color:#888;border:1px solid #CFCFCF;border-radius:5px;padding:0 8px;width:97%;height:250px;overflow-y:scroll" readonly rows="10"><g:rawHtml>${infotext['itext'+context?.lang]}</g:rawHtml></div>
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:18px 3px">
                              <b style="color:#FF530D"><label for="confirmdogovor">
                                <input type="checkbox" id="confirmdogovor" name="confirmdogovor" value="1" <g:if test="${client?.partnerstatus>0}">checked</g:if> />
                                ${message(code:'payout.offer.accept')}
                              </label></b><input type="button" class="button-glossy orange" onclick="$('printForm').submit()" value="${message(code:'payout.offer.print')}"/>
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:20px">
                              <input id="submitbutton" type="submit" ${!client||client?.partnerstatus==2?'disabled':''} class="button-glossy orange" value="${message(code:'payout.scheme.reqconnect')}" />
                            <g:if test="${client&&client?.partnerstatus!=0}">
                              <g:remoteLink class="button-glossy orange mini" controller="account" action="recallpartner" onSuccess="location.reload(true)">${message(code:'partner.program.disconnect')}</g:remoteLink>
                            </g:if>
                              <span id="savePartnerProcessLoader" style="display:none"><img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/></span>
                            </td>
                          </tr>
                        </table>
                      </g:formRemote>
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

                <g:form name="printForm" controller="account" action="partnerofferprint" target="_blank" base="${context?.mainserverURL_lang}">
                </g:form>

              </td>
            </tr>
  </body>
</html>