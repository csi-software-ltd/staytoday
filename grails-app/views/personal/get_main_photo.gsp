<g:if test="${mainpicture}"><img src="${imageurl}${'t_'+mainpicture}"></g:if>
<g:else><img src="${resource(dir:"images",file:"default-picture.png")}"></g:else>
