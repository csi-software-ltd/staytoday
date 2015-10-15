 <g:if test="${result.size()>20}">
  <p>${message(code:'findbank.error.overmuch')}</p>
</g:if><g:elseif test="${!result.size()}">
  <p>${message(code:'findbank.error.nofound')}</p>
</g:elseif><g:else>
  <br/>
  <table class="dotted cbox" cellpadding="5" cellspacing="0" rules="none" frame="none">
  <g:each in="${result}" var="it" status="i">
    <tr>
      <td><g:radio class="bankradio" name="i" value="${i}"></g:radio></td>
      <td id="bik${i}">${it.bik}</td>
      <td id="bankname${i}">${it.bankname}</td>      
    </tr>
    <input type="hidden" id="corraccount${i}" value="${it.corraccount}"/>
  </g:each>
  </table>
</g:else>
