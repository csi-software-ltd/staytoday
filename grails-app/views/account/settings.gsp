<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main"/>
    <g:javascript>
    function billingResponse(e){
      var sErrorMsg='';
      var aErr=e.responseJSON.errorsList;
      $('payee').removeClassName('red');
      $('inn').removeClassName('red');
      $('kpp').removeClassName('red');
      $('bankname').removeClassName('red');
      $('bik').removeClassName('red');
      $('corraccount').removeClassName('red');
      $('settlaccount').removeClassName('red');
      if(e.responseJSON.errorsList){
        if(aErr.indexOf(1)>-1){
          sErrorMsg+="${message(code:'account.billingSettings.error.payee')}";
          $('payee').addClassName('red');
        }
        if(aErr.indexOf(2)>-1){
          sErrorMsg+="${message(code:'account.billingSettings.error.inn')}";
          $('inn').addClassName('red');
        }         
        if(aErr.indexOf(3)>-1){
          sErrorMsg+="${message(code:'account.billingSettings.error.kpp')}";
          $('kpp').addClassName('red');
        }
        if(aErr.indexOf(4)>-1){
          sErrorMsg+="${message(code:'account.billingSettings.error.bankname')}";
          $('bankname').addClassName('red');
        }
        if(aErr.indexOf(5)>-1){
          sErrorMsg+="${message(code:'account.billingSettings.error.bik')}";
          $('bik').addClassName('red');
        }
        if(aErr.indexOf(6)>-1){
          sErrorMsg+="${message(code:'account.billingSettings.error.corraccount')}";
          $('corraccount').addClassName('red');
        }
        if(aErr.indexOf(7)>-1){
          sErrorMsg+="${message(code:'account.billingSettings.error.settlaccount')}";
          $('settlaccount').addClassName('red');
        }
        if(aErr.indexOf(101)>-1){
          sErrorMsg+="${message(code:'account.billingSettings.error.database')}";
        }
        if(sErrorMsg.length){
          $('errorsText').innerHTML=sErrorMsg+'<br/>';
          $('errorsText').show();
        }
      } else {
        $('errorsText').innerHTML='';
        $('errorsText').hide();
      }
    }
    </g:javascript>
  </head>
  <body>
              <g:render template="/account_menu" />
                      <div class="padd20">
                        ${infotext['itext'+context?.lang]?:''}
                      </div><br/>
                      <h2 class="toggle border"><span class="edit_icon detail"></span>${infotext['promotext1'+context?.lang]?:''}</h2>
                      <g:formRemote name="billingInformationForm" url="[action:'savebillingdetails']" method="post" onSuccess="billingResponse(e)" onLoading="\$('saveBillInfoProcessLoader').show();" onLoaded="\$('saveBillInfoProcessLoader').hide();">                      
                        <div id="errorsText" class="notice" style="margin:0px;display: none">
                        </div>
                        <table class="form" width="100%" cellpadding="3" cellspacing="0" border="0">
                          <tr>
                            <td nowrap>
                              <label for="payee" class="nopadd">${message(code:'payout.transfer.bank').split(',')[0]}</label>
                              <input type="text" id="payee" name="payee" value="${client?.payee?:''}" maxlength="250" style="width:68% !important"/>
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="inn" class="nopadd">${message(code:'payout.transfer.bank').split(',')[1]}</label>
                              <input type="text" id="inn" name="inn" value="${client?.inn?:''}" maxlength="12" />
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="kpp" class="nopadd">${message(code:'payout.transfer.bank').split(',')[2]}</label>
                              <input type="text" id="kpp" name="kpp" value="${client?.kpp?:''}" maxlength="9" />
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="bankname" class="nopadd">${message(code:'payout.transfer.bank').split(',')[3]}</label>
                              <input type="text" id="bankname" name="bankname" value="${client?.bankname?:''}" maxlength="250" style="width:68% !important"/>
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="bik" class="nopadd">${message(code:'payout.transfer.bank').split(',')[4]}</label>
                              <input type="text" id="bik" name="bik" value="${client?.bik?:''}" maxlength="9" />
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="corraccount" class="nopadd">${message(code:'payout.transfer.bank').split(',')[5]}</label>
                              <input type="text" id="corraccount" name="corraccount" value="${client?.corraccount?:''}" maxlength="20" style="width:68% !important"/>
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="settlaccount" class="nopadd">${message(code:'payout.transfer').split(',')[1]}</label>
                              <input type="text" id="settlaccount" name="settlaccount" value="${client?.settlaccount?:''}" maxlength="20" style="width:68% !important"/>
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="nds" class="nopadd">${message(code:'payout.transfer.bank').split(',')[7]}</label>
                              <g:select class="dmini" name="nds" from="${Nds.list()}" value="${client?.nds?:0}" optionKey="percent" optionValue="name" />
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="paycomment" class="nopadd">${message(code:'payout.transfer.bank').split(',')[9]}</label>
                              <input type="text" id="paycomment" name="paycomment" value="${client?.paycomment?:''}" maxlength="250" style="width:68% !important"/>
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="paymessage" class="nopadd"  style="padding-right:10px">Сообщение для пользователя</label>
                              <input type="text" id="paymessage" name="paymessage" value="${client?.paymessage?:''}" maxlength="250" style="width:68% !important"/>
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <label for="margin" class="nopadd">Наценка в процентах</label>
                              <input type="text" id="margin" name="margin" value="${client?.margin?:''}" maxlength="250" />
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2" style="padding:20px">
                              <input type="submit" ${client?'':'disabled'} class="button-glossy orange" value="${message(code:'button.saveSetting')}"/>
                              <span id="saveBillInfoProcessLoader" style="display:none"><img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/></span>
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
              </td>
            </tr>
  </body>
</html>
