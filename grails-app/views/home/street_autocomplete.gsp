<ul style="margin:0;padding:0px">
<g:each in="${records}" var="item" >
  <li style="list-style: none none;margin:0px">${item['name'+context?.lang]?:''}</li>
</g:each>
</ul>
