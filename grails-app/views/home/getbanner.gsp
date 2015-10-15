<g:if test="${banner}">
  <g:if test="${banner?.bclass == 1}">
    <!--<g:link controller="index" action="clickbanner" target="_blank" id="${banner?.id}">-->      
      <img src="${url}${banner?.filename}" alt="${banner?.altname?:''}" style="margin:0 -7px"
        width="${(banner.hsize>730)?730:banner?.hsize}" height="${(banner.vsize>160)?160:banner?.vsize}" border="0" />
    <!--</g:link>-->
  </g:if><g:if test="${banner?.bclass == 2}">	
    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" height="${banner?.vsize}" style="margin:0 -7px"
      width="${banner?.hsize}" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=5,0,0,0">
      <param name="movie" value="${url}${banner?.filename}" />
      <param name="menu" value="false" />
      <param name="quality" value="high" />
      <param name="wmode" value="opaque" />
      <param name="scale" value="exactfit" />
      <embed src="${url}${banner?.filename}" menu="false" quality="high" wmode="opaque" scale="exactfit" type="application/x-shockwave-flash" 
        height="${(banner.vsize>160)?160:banner?.vsize}" width="${(banner?.hsize>730)?730:banner?.hsize}" pluginspage="http://www.macromedia.com/shockwave/downloadindex.cgi?P1_Prod_Version=ShockwaveFlash">
      </embed>
    </object>	
  </g:if>
 </g:if>
