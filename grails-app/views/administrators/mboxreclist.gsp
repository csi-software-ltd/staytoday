<style type="text/css">
  .dotted { border: none }
  .dotted td, .dotted th { padding: 5px; font-size: 12px; }
  .dotted td { padding: 12px 5px; color: inherit; }
  #resultList { border: 1px solid #505050; height: auto }
</style>
<div id="ajax_wrap">
<g:if test="${mboxrec}">
  <div id="resultList">
    <table class="dotted" width="100%" cellpadding="0" cellspacing="0" border="0" rules="all" frame="none">
      <tr>
        <th align="center" width="150">дата/время сообщения</th>
        <th align="center" width="150">кому</th>
        <th align="center" width="511">текст сообщения (даблклик для редактирования)</th>
        <th align="center" width="150">тип ответа</th>
        <th align="center" width="150">статус модерации</th>
        <th align="center" width="150">кнопки</th>
      </tr>
    <g:each in="${mboxrec}" status="i" var="record">
      <tr id="tr+${i}" style="line-height:12px;${record.is_system?'background:gold':record.admin_id?'color:red':''}">
        <td align="center"><b>${String.format('%td.%<tm.%<ty %<tT',record.inputdate)}</b></td>
        <td>${record.is_answer?'клиенту':'владельцу'}</td>
        <td ondblclick="edit(${record.id},'${record.rectext?.replace('\n','')?.replace('\r','')}');">${record.rectext}</td>
        <td>${Answertype.get(record.answertype_id)?.shortdescription?:"запрос"}</td>
        <td>${record.is_approved==1?'одобрено':'неодобрено'}</td>
        <td>
          <span class="action_button anowrap">
            <g:remoteLink class="icon ${record.is_approved?'delete':'edit'}" title="${record.is_approved?'Неодобрить':'Одобрить'}" url="${[controller:'administrators',action:'mboxrecapprove',id:record.id,params:[status:(record.is_approved+1)%2]]}" onSuccess="processresponse()"></g:remoteLink>
          </span>
        </td>
      </tr>
    </g:each>
    </table>
  </div>

  <a id="rectext_edit_link" style="display:none"></a>
  <div id="rectext_edit_lbox" class="new-modal" style="height:185px;display:none">
    <div id="rectext_edit_lbox_segment" class="segment nopadding">
      <div id="rectext_edit_lbox_container" class="lightbox_filter_container">
        <g:formRemote name="rectext_editForm" url="${[controller:'administrators',action:'rectextedit']}" style="padding:5px" onSuccess="jQuery.colorbox.close();\$('mboxRecForm_submit').click();">
          <table width="100%" cellpadding="5" cellspacing="0" border="0">
            <tr>
              <td><textarea rows="4" cols="60" id="rectext" name="rectext" style="width:99%;height:100px"/></textarea></td>
            </tr>
            <tr>
              <td align="right">
                <input type="submit" class="button-glossy green mini" value="Сохранить"/>
              </td>
            </tr>
          </table>
          <input id="mboxrec_id" type="hidden" name="mboxrec_id" value="" />
        </g:formRemote>
      </div>
    </div>
  </div>

  <script type="text/javascript">
    jQuery('#rectext_edit_link').colorbox({
      inline: true,
      href: '#rectext_edit_lbox',
      scrolling: false,
      onLoad: function(){
        jQuery('#rectext_edit_lbox').show();
      },
      onCleanup: function(){
        jQuery('#rectext_edit_lbox').hide();
      }
    });
  </script>
</g:if>
</div>
