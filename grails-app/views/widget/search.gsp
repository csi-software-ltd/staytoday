<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru">
  <head>
    <r:external uri="/js/jquery-1.8.3.js"/>
    <r:external uri="/js/bootstrap-carousel.min.js"/>
    <script type="text/javascript" src="http://img.yandex.net/webwidgets/1/WidgetApi_no_jquery.js"></script>
    <r:external uri="/js/prototype/prototype.js" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'main.css')}" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'bootstrap.css')}" />
    <script type="text/javascript">
      var opened_popdirection = 0;
      var mouseOnDir  = 0;      
      jQuery(document).ready(function(){
        jQuery('#wg_where').width(jQuery('#staytoday_search').width() - 38 + 'px');
      });      
      function togglePopDirection(iType){
        if ($("select_popdirection").style.display == 'none'){
          updatePopDirection(iType);
          opened_popdirection = 1;
        } else {
          jQuery("#select_popdirection").slideToggle();
          opened_popdirection = 0;
        }
      }
      function updatePopDirection(iType){
        <g:remoteFunction controller='home' action='selectpopcities' update='select_popdirection' onComplete="jQuery('#select_popdirection').slideDown()" params="\'country_id=\'+iType" />
      }
      function clickPopDirection(sWhere){
        $('wg_where').value = sWhere;
        togglePopDirection();
        $('wg_submitButton').click();
      }
      function checkDropDowns(){
        if (opened_popdirection){
          if (!mouseOnDir){
            opened_popdirection = 0;
            $("select_popdirection").hide();
          }
        }
      }
      widget.onload = function(){
        widget.adjustIFrameHeight();
      }
    </script>    
  </head>
  <body onload="document.getElementById('wg_submitButton').click()" onclick="checkDropDowns();" style="background:transparent;width:auto">    
    <div id="results" style="width:auto;height:210px"></div>
    <div id="staytoday_search" style="width:auto">
      <g:formRemote name="search_form" url="[action:'search_result']" method="POST" update="results" before="document.getElementById('sitesearchvalue').value=document.getElementById('wg_where').value">
        <table width="100%" cellpadding="0" cellspacing="0" border="0">
          <tr>
            <td valign="top" style="padding:5px 11px 0">
              <input type="text" id="wg_where" name="where" value="<g:rawHtml>${where?:''}</g:rawHtml>" placeholder="страна, регион, город" autocomplete="off" />
              <input type="submit" id="wg_submitButton" value="снять жилье" />
              <input type="hidden" name="valuta_id" value="${valuta_id}" />
            </td>            
          </tr>
          <tr>
            <td valign="middle" style="padding:5px 11px">
              <div class="dropdown-list relative" nowrap>
                <span class="float">
                  <small id="a_select_popdirection" class="select" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0" onclick="togglePopDirection(1)"><a href="javascript:void(0)" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0">Выбрать город</a></small>
                  <div class="select_dropdown" id="select_popdirection" style="margin-top:-190px;width:207px;display:none" onmouseover="mouseOnDir=1" onmouseout="mouseOnDir=0"></div>
                </span>
                <span style="float:right">
                  <small class="select" style="background:none;padding:0px">
                    <a href="javascript:void(0)" onclick="document.getElementById('sitesearch_form').submit()">StayToday.ru</a>
                  </small>
                </span>
              </div>
            </td>
          </tr>
        </table>
      </g:formRemote>
      <g:form name="sitesearch_form" url="[controller:'home',action:'search']" method="POST" target="_blank">
        <input type="hidden" name="where" id="sitesearchvalue" value="${where}" />
      </g:form>
    </div>    
  </body>
</html>
