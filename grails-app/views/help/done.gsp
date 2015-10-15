<div class="padd20">
<g:if test="${done}">
  <p>${message(code:'faq.notice.done')}.</p>
  <ul>
    <li><g:link controller="help" action="guestbook">${message(code:'faq.notice.send')}</g:link></li>
    <li><g:link controller="index" action="index">${message(code:'faq.notice.main')}</g:link></li>
  </ul>
</g:if><g:else>
  <p>${message(code:'faq.error.send')}: <a href="mailto:${mail_support}?body=${inrequest?inrequest.toString().replace('\n',' ').replace('\r',' '):''}">${mail_support}</a></p>
</g:else>
</div>
