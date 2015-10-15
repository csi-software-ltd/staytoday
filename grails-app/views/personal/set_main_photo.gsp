<script type="text/javascript">
  $('thumbnail_'+${cur_main_photo_id}).addClassName('selected');
  $('thumbnail_'+${prev_main_photo_id}).removeClassName('selected');
  $('main_'+${cur_main_photo_id}).hide();
  $('main_'+${prev_main_photo_id}).show();
  $('delete_'+${cur_main_photo_id}).hide();
  $('delete_'+${prev_main_photo_id}).show();
  <g:if test="${mainpicture}">
    $('homepic_img').src="${imageurl}${'t_'+mainpicture}";    
  </g:if>
  <g:else>
    $('homepic_img').src="${resource(dir:"images",file:"default-picture.png")}";
  </g:else>
</script>
<g:if test="${mainpicture}"><a class="bigMainPhoto cboxElement" href="${imageurl}${mainpicture}"><img width="200" height="140" src="${imageurl}${'t_'+mainpicture}"></a></g:if>
<g:else><img width="200" height="140" src="${resource(dir:'images',file:'default-picture.png')}"></g:else>