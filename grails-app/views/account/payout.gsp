<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main"/>
    <g:javascript>
    var lCountryId = ${country[0]?.id?:0};
    var iDetails = ${client?.payaccount_id?:0};
    function displayDetails(iId){
      for (var i=1;i<4;i++)
        $('details'+i).hide()
      if(iId>0) $('details'+iId).show()
      iDetails = iId;
    }
    function enableSubmitButton(sId){
      sId = sId || '';
      if(${client?1:0}){
        if(!sId)
          $('submitbutton').enable();
        else if(sId.value!=sId.defaultValue)
          $('submitbutton').enable();
        else $('submitbutton').disable();
      }
    }
    function payoutResponse(e){
      var sErrorMsg='';      
      $('payee').removeClassName('red');
      $('inn').removeClassName('red');
      $('kpp').removeClassName('red');
      $('bankname').removeClassName('red');
      $('bik').removeClassName('red');
      $('corraccount').removeClassName('red');
      $('settlaccount').removeClassName('red');
      $('confirmdogovor').removeClassName('red');
      $('webmoney').removeClassName('red');
      $('webmoney2').removeClassName('red');
      $('smscode').removeClassName('red');
      if(e.responseJSON.errorsList){
        e.responseJSON.errorsList.forEach(function(err){          
          switch(err){
            case 1: sErrorMsg+="${message(code:'error.blank',args:[message(code:'payout.transfer.bank').split(',')[0]])}<br/>"; $('payee').addClassName('red'); break;
            case 2: sErrorMsg+="${message(code:'error.blank',args:[message(code:'payout.transfer.bank').split(',')[1]])}<br/>"; $('inn').addClassName('red'); break;
            case 3: sErrorMsg+="${message(code:'error.incorrect',args:[message(code:'payout.transfer.bank').split(',')[2]])}<br/>"; $('kpp').addClassName('red'); break;
            case 4: sErrorMsg+="${message(code:'error.blank',args:[message(code:'payout.transfer.bank').split(',')[3]])}<br/>"; $('bankname').addClassName('red'); break;
            case 5: sErrorMsg+="${message(code:'error.incorrect',args:[message(code:'payout.transfer.bank').split(',')[4]])}<br/>"; $('bik').addClassName('red'); break;
            case 6: sErrorMsg+="${message(code:'error.incorrect',args:[message(code:'payout.transfer.bank').split(',')[5]])}<br/>"; $('corraccount').addClassName('red'); break;
            case 7: sErrorMsg+="${message(code:'error.incorrect',args:[message(code:'payout.transfer').split(',')[1]])}<br/>"; $('settlaccount').addClassName('red'); break;
            case 8: sErrorMsg+="${message(code:'ads.error.handbooks')}<br/>"; break;
            case 9: sErrorMsg+="${message(code:'payout.error.offer.confirm')}<br/>"; $('confirmdogovor').addClassName('red'); break;
            case 10: sErrorMsg+="${message(code:'payout.error.webmoney')}<br/>"; $('webmoney').addClassName('red'); $('webmoney2').addClassName('red'); break;
            case 11: sErrorMsg+="${message(code:'profile.error.wrongcode')}<br/>"; $('smscode').addClassName('red'); break;
            case 12: sErrorMsg+="${message(code:'error.incorrect',args:[message(code:'payout.transfer.bank').split(',')[1]])}<br/>"; $('inn').addClassName('red'); break;
            case 13: sErrorMsg+="${message(code:'payout.error.payaccount')}<br/>"; break;
            case 101: sErrorMsg+="${message(code:'error.db')}<br/>"; break;
          }
        });
        if(sErrorMsg.length){
          $('errorsText').innerHTML=sErrorMsg;
          $('errorsText').show();
        } else {
          $('errorsText').hide();
          sendMessage();
        }
      } else {
        location.reload(true)
      }
    }
    function changetext(iId){
      var oferta='', acc='';
      switch (iId) {
        case '1': {
          oferta='<g:rawHtml>${infotext['itext'+context?.lang]?infotext['itext'+context?.lang].replace('\n','').replace('\r',''):''}</g:rawHtml>'; 
          acc='${message(code:'payout.transfer').split(',')[0]}'; break;
        } case '2': {
          oferta='<g:rawHtml>${infotext['itext2'+context?.lang]?infotext['itext2'+context?.lang].replace('\n','').replace('\r',''):''}</g:rawHtml>'; 
          acc='${message(code:'payout.transfer').split(',')[1]}'; break;
        } case '3': {
          oferta='<g:rawHtml>${infotext['itext3'+context?.lang]?infotext['itext3'+context?.lang].replace('\n','').replace('\r',''):''}</g:rawHtml>'; 
          acc='${message(code:'payout.transfer').split(',')[2]}'; break;
        }
      }
      $('confirmdogovor').checked=false;
      $('dogovortextarea').update(oferta);
      $('acc').update(acc);
      $('offer_id').value=(iId==1?'':iId);
    }
    function updateDescription(iPayaccountId){
      <g:remoteFunction controller='account' action='paydescription' update='paydescription' params="'countryId='+lCountryId+'&payaccount='+iPayaccountId" />
    }
    function updatePayaccounts(_country){
      lCountryId = _country;
      <g:remoteFunction controller='account' action='payaccounts' update='payaccounts' params="'countryId='+lCountryId" />
    }
    function updateScheme(iType){
      <g:remoteFunction controller='account' action='payscheme' params="'id='+iType" update='reserveschemetd'/>
    }
    function displaypayaccount(iId){
      if(iId=='3'||iId=='4'){
        $('payoutmethod').hide();
        $('payoutcountry').hide();        
        $('settlproceduretr').show();
        displayDetails(0);
      } else {
        $('payoutmethod').show();
        $('payoutcountry').show();
        $('settlproceduretr').hide();
        displayDetails(iDetails);
      }
    }
    function setForm(iId){
      if(iId!=2){
        $("kpp_div").hide(); 
        $("nds_div").hide();
        $("pcard_div").show();        
      }else{
        $("nds_div").show();
        $("kpp_div").show();
        $("pcard_div").hide();
      }  
    }
    function sendMessage(){
      $("submitbutton").hide();
      <g:remoteFunction action='sendVerifyTel' onSuccess='processResponse(e)' />
      $("verifyMsg").hide();
    }
    function processResponse(e){
      $("verifyTel").show();
      $("is_presave").value=0;
    }

    jQuery(document).ready(function() {
      jQuery('#find_bank_link').colorbox({
        inline: true,
        href: '#find_bank_lbox',
        scrolling: false,
        onLoad: function(){
          jQuery('#find_bank_lbox').show();
        },
        onCleanup: function(){
          jQuery('#find_bank_lbox').hide();
          $('find_bik').value = '';
          $('find_bankname').value = '';
          $('findbankresult').innerHTML = '';
          enableSubmitButton($('bankname'));
          enableSubmitButton($('bik'));
          enableSubmitButton($('corraccount'));
        }
      });
    });

    function copyData(){
      var id = jQuery('.bankradio:checked').val();
      if(id){
        $('bankname').value = jQuery('#bankname'+id).text().trim();
        $('bik').value = jQuery('#bik'+id).text().trim();
        $('corraccount').value = jQuery('#corraccount'+id).val();
      }
      jQuery.colorbox.close();
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
                      <g:formRemote name="payoutInformationForm" url="[action:'savepayoutdetails']" method="post" onSuccess="payoutResponse(e)" onLoading="\$('saveBillInfoProcessLoader').show();" onLoaded="\$('saveBillInfoProcessLoader').hide();">
                        <div id="errorsText" class="notice" style="margin:5px 10px;${client?'display: none':''}">
                          ${message(code:'payout.notice.noclient')}
                        </div>
                        <table class="form" width="100%" cellpadding="5" cellspacing="0" border="0">
                          <tr>
                            <td colspan="2">
                              <b><label for="type_id" class="nopadd">${message(code:'ads.infras.category')}</label></b>
                              <select name="type_id" id="type_id" onchange="updateScheme(this.value);changetext(this.value);setForm(this.value);enableSubmitButton();" style="width:200px">
                                <option value="1" <g:if test="${client?.type_id==1}">selected="selected"</g:if>>${message(code:'payout.individual')}</option>
                                <option value="2" <g:if test="${client?.type_id==2}">selected="selected"</g:if>>${message(code:'payout.legalentity')}</option>
                              </select>
                            </td> 
                          </tr>
                          <tr>
                            <td id="reserveschemetd" style="padding:20px 7px 0">
                              <b><label for="reserve_id" class="nopadd">${message(code:'payout.scheme')}</label></b>
                              <select name="reserve_id" id="reserve_id" style="width:200px">
                              <g:each in="${reserve}" var="item">
                                <option value="${item.id}" onclick="enableSubmitButton();displaypayaccount(this.value);$('paydesc').update('${item['description'+context?.lang]}')" <g:if test="${item.id==client?.reserve_id}">selected="selected"</g:if>>${item['name'+context?.lang]}</option>
                              </g:each>
                              </select>
                            </td>
                            <td style="padding:20px 10px 5px">
                              <b>${message(code:'account.status')}</b>: &nbsp; <b id="status"></b>
                              <g:javascript>
                                var color, status;
                                switch(${client?.resstatus?:0}){
                                  case  1: color='green'; status='${message(code:'payout.scheme.confirm')}'; break;
                                  case -1: color='red'; status='${message(code:'payout.scheme.decline')}'; break;
                                  case  2: color='gray'; status='${message(code:'payout.scheme.reqconnect')}'; break;
                                  case  3: color='gray'; status='${message(code:'payout.scheme.reqchange')}'; break;
                                  default: color='red'; status='${message(code:'payout.scheme.noconnect')}';
                                }
                                if(${client?.is_reserve&&!client?.is_offeradmit?1:0}){color='gray'; status='${message(code:'payout.scheme.oferta.noconfirm')}';}
                                $("status").update('<font color="'+color+'">'+status+'</font>');
                              </g:javascript>
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:10px 25px;color:#FF530D" id="paydesc">
                              <g:rawHtml>${Reserve.get(client?.reserve_id?:0)?."description$context.lang"?:''}</g:rawHtml>
                            </td>
                          </tr>
                          <tr id="settlproceduretr" style="${!((client?.reserve_id?:3) in [3,4])?'display:none':''}">
                            <td colspan="2" style="padding:5px 25px">
                              <b><label for="settlprocedure" style="padding:0">${message(code:'payout.settlement')}:</label></b>
                              <textarea id="settlprocedure" name="settlprocedure" style="width:97%" onkeyup="enableSubmitButton(this)">${client?.settlprocedure?:message(code:'payout.settlement.default')}</textarea>
                            </td>
                          </tr>
                          <tr id="payoutcountry" style="${(client?.reserve_id?:3) in [3,4]?'display:none':''}">
                            <td width="55%">
                              <b><label for="country_id" class="nopadd">${message(code:'ads.country')}</label></b>
                              <g:select onchange="updatePayaccounts(this.value);enableSubmitButton();" class="mini" name="country_id" from="${country}" value="${client?.country_id?:0}" optionKey="id" optionValue="${'name'+context?.lang}" />
                            </td>
                            <td>
                            <!--<b><label for="valuta_id" class="nopadd">валюта</label></b>
                              <g:select class="mini" name="valuta_id" from="${valuta}" value="${client?.valuta_id?:country[0]?.valuta_id?:0}" optionKey="id" optionValue="name" />-->
                            </td>
                          </tr>
                          <tr id="payoutmethod" style="${(client?.reserve_id?:3) in [3,4]?'display:none':''}">
                            <td colspan="2" class="paddtop">
                              <table id="payaccounts" class="dotted" width="100%" cellpadding="0" cellspacing="0" frame="none" rules="all">
                                <tr>
                                  <th width="180">${message(code:'payout.method')}</th>
                                  <th width="125">${message(code:'payout.arrive')}</th>
                                  <th width="100">${message(code:'payout.fee')}</th>                                  
                                  <th>${message(code:'payout.notes')}</th>
                                </tr>
                                <g:each in="${paycountry}" var="${item}">
                                <tr>
                                  <td>
                                    <label for="payaccount_${item?.id}" style="padding-left:0">
                                      <g:radio id="payaccount_${item?.payaccount_id}" name="payaccount_id" value="${item.payaccount_id}" onchange="displayDetails(this.value);enableSubmitButton();" checked="${(client?.payaccount_id==item?.payaccount_id)?true:false}" style="white-space:nowrap"/>
                                      ${Payaccount.get(item.payaccount_id)['name'+context?.lang]}
                                    </label>  
                                  </td>
                                  <td>${item['term'+context?.lang]?:''}</td>
                                  <td align="center">${item.percent?:''}</td>
                                  <td>${item['description'+context?.lang]?:''}</td>
                                </tr>
                              </g:each>
                              </table>
                            </td>
                          </tr>
                          <!--<tr>
                            <td colspan="2" style="padding:18px 3px">
                              <b><label for="is_transferauto"> 
                                <input type="checkbox" name="is_transferauto" value="1" <g:if test="${client?.is_transferauto}">checked</g:if> />
                                переводить деньги автоматически
                                <a class="tooltip" href="javascript:void(0)" title="Автоматическое списывание денег, без подтверждения операции">
                                  <img alt="Автоматическое списывание денег, без подтверждения операции" src="${resource(dir:'images',file:'question.png')}" hspace="10" valign="baseline" border="0" />
                                </a>
                              </label></b>
                            </td>
                          </tr>-->
                          <tr id="details1" style="${client?.payaccount_id!=1||(client?.reserve_id?:3) in [3,4]?'display:none':''}">
                            <td colspan="2">
                              <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                                <tr>
                                  <td colspan="2">
                                    <label for="payee" class="nopadd"><b>${message(code:'payout.transfer.bank').split(',')[0]}</b></label>
                                    <input type="text" id="payee" name="payee" value="${client?.payee?:''}" maxlength="250" style="width:71%!important" onkeyup="enableSubmitButton(this)"/>
                                  </td>
                                </tr>
                                <tr>
                                  <td width="55%">
                                    <label for="inn" class="nopadd"><b>${message(code:'payout.transfer.bank').split(',')[1]}</b></label>
                                    <input type="text" class="mini" id="inn" name="inn" value="${client?.inn?:''}" maxlength="12" onkeyup="enableSubmitButton(this)"/>
                                  </td>
                                  <td>
                                    <div id="kpp_div" <g:if test="${client?.type_id!=2}">style="display:none"</g:if>>
                                      <label for="kpp" style="padding:0;min-width:100px">${message(code:'payout.transfer.bank').split(',')[2]}</label>
                                      <input type="text" class="mini" id="kpp" name="kpp" value="${client?.kpp?:''}" maxlength="9" onkeyup="enableSubmitButton(this)"/>
                                    </div>
                                  </td>
                                </tr>
                                <tr>
                                  <td colspan="2">
                                    <label for="bankname" class="nopadd"><b>${message(code:'payout.transfer.bank').split(',')[3]}</b></label>
                                    <input type="text" id="bankname" name="bankname" value="${client?.bankname?:''}" maxlength="250" style="width:365px" onkeyup="enableSubmitButton(this)"/>
                                    <input type="button" class="button-glossy orange" value="${message(code:'payout.bank.select')}" onclick="$('find_bank_link').click();"/>
                                  </td>
                                </tr>
                                <tr>
                                  <td>
                                    <label for="bik" class="nopadd"><b>${message(code:'payout.transfer.bank').split(',')[4]}</b></label>
                                    <input type="text" class="mini" id="bik" name="bik" value="${client?.bik?:''}" maxlength="9" onkeyup="enableSubmitButton(this)"/>
                                  </td>
                                  <td>
                                    <b><label for="corraccount" style="padding:0;min-width:100px">${message(code:'payout.transfer.bank').split(',')[5]}</label></b>
                                    <input type="text" id="corraccount" name="corraccount" value="${client?.corraccount?:''}" maxlength="25" style="width:195px" onkeyup="enableSubmitButton(this)"/>
                                  </td>
                                </tr>
                                <tr>
                                  <td>
                                    <b><label for="settlaccount" class="nopadd" id="acc">${message(code:'payout.transfer.bank').split(',')[6]}</label></b>
                                    <input type="text" id="settlaccount" name="settlaccount" value="${client?.settlaccount?:''}" maxlength="25" style="width:180px" onkeyup="enableSubmitButton(this)"/>
                                  </td>
                                  <td>                                   
                                    <div id="nds_div"  <g:if test="${client?.type_id!=2}">style="display:none"</g:if>>
                                      <label for="nds" style="padding:0;min-width:100px">${message(code:'payout.transfer.bank').split(',')[7]}</label>
                                      <g:select class="mini" name="nds" from="${Nds.list()}" value="${client?.nds?:0}" optionKey="percent" optionValue="${'name'+context?.lang}" onkeyup="enableSubmitButton(this)" />
                                    </div>                                    
                                    <div id="pcard_div" <g:if test="${client?.type_id==2}">style="display:none"</g:if>>
                                      <label for="pcard" style="padding:0;min-width:100px">${message(code:'payout.transfer.bank').split(',')[8]}</label>
                                      <input type="text" id="pcard" name="pcard" value="${client?.pcard?:''}" style="width:195px" onkeyup="enableSubmitButton(this)"/>
                                    </div>                                     
                                 </td>
                                </tr>
                                <tr>
                                  <td colspan="2">
                                    <label for="paycomment" class="nopadd" style="line-height:15px">${message(code:'payout.transfer.bank').split(',')[9]}</label>
                                    <textarea id="paycomment" name="paycomment" style="width:71%" onkeyup="enableSubmitButton(this)" placeholder="${message(code:'payout.transfer.bank.info')}">${client?.paycomment?:''}</textarea>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr id="details2" style="${client?.payaccount_id!=2||(client?.reserve_id?:3) in [3,4]?'display:none':''}">
                            <td>
                              <label for="yandex" class="nopadd">${message(code:'payout.transfer.money')}</label>
                              <input type="text" class="mini" id="yandex" name="yandex" maxlength="14" value="${client?.yandex?:''}" onkeydown="$('yandex2').show();enableSubmitButton(this);"/>
                            </td>
                            <td id="yandex2" style="display:none">
                              <label for="yandex2" class="nopadd">${message(code:'payout.transfer.money.confirm')}</label>
                              <input type="text" class="mini" id="yandex2" name="yandex2" maxlength="14" value=""/>
                            </td>
                          </tr>
                          <tr id="details3" style="${client?.payaccount_id!=3||(client?.reserve_id?:3) in [3,4]?'display:none':''}">
                            <td>
                              <b><label for="webmoney" class="nopadd">${message(code:'payout.transfer.money')}</label></b>
                              <input type="text" class="mini" id="webmoney" name="webmoney" maxlength="13" value="${client?.webmoney?:''}" onkeydown="$('webmoneytd').show();enableSubmitButton(this);"/>
                            </td>
                            <td id="webmoneytd" style="display:none">
                              <label for="webmoney2" class="nopadd" style="padding:0;min-width:130px">${message(code:'payout.transfer.money.confirm')}</label>
                              <input type="text" class="mini" id="webmoney2" name="webmoney2" maxlength="13" value=""/>
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:18px 3px">
                              <b style="color:#FF530D"><label for="confirmdogovor">
                                <input type="checkbox" id="confirmdogovor" name="confirmdogovor" value="1" <g:if test="${client?.is_offeradmit&&client.is_reserve}">checked</g:if> />
                                ${message(code:'payout.offer.accept')}
                              </label></b><input type="button" class="button-glossy orange" onclick="$('printForm').submit()" value="${message(code:'payout.offer.print')}"/>                              
                            </td>
                          </tr>
                          <tr id="dogovortext">
                            <td colspan="2" class="padd10">
                              <div id="dogovortextarea" style="color:#888;border:1px solid #CFCFCF;border-radius:5px;padding:0 8px;width:97%;height:250px;overflow-y:scroll" readonly rows="10"><g:rawHtml>${infotext['itext'+(client?.type_id==2?'2':client?.type_id==3?'3':'')+context?.lang]}</g:rawHtml></div>
                            </td>
                          </tr>
                        <g:if test="${infotext['promotext2'+context?.lang]?:''}">
                          <tr id="verifyMsg">
                            <td colspan="2" style="padding:0 20px">
                              <p><b>${infotext['promotext2'+context?.lang]?:''}</b></p>
                            </td>
                          </tr>
                        </g:if>
                          <tr id="verifyTel" style="display:none">
                            <td colspan="2">                              
                              <p class="padd20"><b style="color:#FF530D">${message(code:'verify.phone.confirm.notice')}</b></p>
                              <b><label for="smscode">${message(code:'verify.code')}:</label></b>
                              <input type="text" class="mini" id="smscode" name="smscode" value="" size="10"/>
                              <input type="submit" class="button-glossy orange" value="${message(code:'button.confirm')}"/>
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:20px">
                              <input id="submitbutton" type="submit" ${!client||(client?.is_offeradmit&&client?.is_reserve)?'disabled':''} class="button-glossy orange" value="${message(code:'button.saveSetting')}" onclick="yaCounter15816907.reachGoal('bron_submit'); return true;"/>
                              <span id="saveBillInfoProcessLoader" style="display:none"><img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/></span>
                            </td>
                          </tr>
                        </table>
                        <input type="hidden" id="is_presave" name="is_presave" value="1"/>
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

                <a id="find_bank_link" href="javascript:void(0)" rel="nofollow" style="display:none"></a>
                <div id="find_bank_lbox" class="new-modal" style="display:none">
                  <h2 class="clearfix">${message(code:'payout.bank.search')}</h2>                  
                  <div id="find_bank_lbox_segment" class="segment nopadding">
                    <div id="find_bank_lbox_container" class="lightbox_filter_container" style="height:auto">
                      <g:formRemote name="answerCommentForm" url="${[action:'findbank']}" style="padding:5px" update="[success:'findbankresult']" onComplete="jQuery.colorbox.resize();">
                        <table cellpadding="5" cellspacing="0" border="0">
                          <tr>
                            <td><label for="find_bankname" class="nopadd">${message(code:'payout.transfer.bank').split(',')[3]}</label></td>
                            <td colspan="2"><input type="text" id="find_bankname" name="bankname" value="" maxlength="250" style="width:100%" /></td>
                          </tr>                      
                          <tr>
                            <td><label for="find_bik" class="nopadd">${message(code:'payout.transfer.bank').split(',')[4]}</label></td>
                            <td><input type="text" id="find_bik" class="mini" name="bik" value="" maxlength="9" /></td>
                            <td><input type="submit" class="button-glossy orange" value="${message(code:'button.search')}" /></td>
                          </tr>
                        </table>
                      </g:formRemote>                      
                      <div id="findbankresult"></div>
                    </div>
                  </div>                  
                  <div class="segment buttons">
                    <input type="button" class="button-glossy orange" value="${message(code:'payout.bank.select')}" onclick="copyData()"/>
                  </div>
                </div>
                
                <g:form name="printForm" controller="account" action="offerprint" target="_blank" base="${context?.mainserverURL_lang}">
                  <input type="hidden" id="offer_id" name="offer_id" value="${client?.type_id==2?'2':client?.type_id==3?'3':''}" />
                </g:form>

              </td>
            </tr>
  </body>
</html>
