﻿<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />       
    <meta name="layout" content="main" />
    <g:javascript>
    function starred(lTId,iId){
      for (var i = iId;i>=0;i--)
        $('rating_'+lTId+'_'+i).addClassName('starred');
      for (var i = iId+1;i<5;i++)
        $('rating_'+lTId+'_'+i).removeClassName('starred');
    }   
    function unstarred(lTId,iId,iRat){
      for (var i = iId;i>=iRat;i--)
        $('rating_'+lTId+'_'+i).removeClassName('starred');
      for (var i = iId;i<iRat;i++)
        $('rating_'+lTId+'_'+i).addClassName('starred');
    }
    function declineTrip(lId){
      <g:remoteFunction controller='trip' action='declineTrip' onSuccess='location.reload(true)' params="'id='+lId" />
    }
    </g:javascript>
    <style type="text/css">
      .options .star_icon_container { padding: 0 !important }
      .actions { padding: 6px 10px }
      h2.title a.name { float: none !important }
    </style>    
  </head>
  <body>
              <g:render template="/trip_menu" />                                  
                      <div class="search-container">
                        <div id="search_body">
                          <div id="results">              
                          <g:if test="${data?.count > 20}">
                            <div class="results_header">    
                              <span class="pagination">
                                <g:paginate controller="trip" action="${actionName}" params="${inrequest}" 
                                  prev="&lt;" next="&gt;" max="20" total="${data?.count}" /> 
                                <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>				
                              </span>  
                            </div>
                          </g:if>
                          <g:if test="${data?.records}">
                            <table class="list-results" width="100%" cellpadding="5" cellspacing="0" border="0">
                            <g:each in="${data?.records}" var="trip" status="i">
                              <tr>
                                <td onmouseout="this.removeClassName('selected')" onmouseover="this.addClassName('selected')">
                                  <div class="relative clearfix" style="margin-bottom:10px">                                    
                                    <div class="thumbnail shadow">
                                      <g:link mapping="${'hView'+context?.lang}" params="${[country:Country.get(trip.home_country_id)?.urlname,city:trip.home_city,linkname:trip?.home_linkname]}">
                                        <img width="200" height="140" alt="${trip?.home_name}" title="${trip?.home_name}" border="0" 
                                          src="${(trip?.home_mainpicture)?urlphoto+trip?.home_client_id+'/t_'+trip?.home_mainpicture:resource(dir:'images',file:'default-picture.png')}">
                                      </g:link>
                                    </div>
                                    <div class="description">
                                      <div class="float">
                                        <h2 class="title padd20 clearfix">
                                          <g:link class="name" mapping="${'hView'+context?.lang}" params="${[country:Country.get(trip.home_country_id)?.urlname,city:trip.home_city,linkname:trip?.home_linkname]}">${trip.home_name}</g:link>
                                        </h2>
                                        <ul class="details-list" style="width:385px">
                                          <li class="details-list-item location">
                                            <span class="icons"></span>
                                            ${trip?.home_address}
                                          </li> 
                                          <li class="details-list-item room_type">
                                            <span class="icons"></span>
                                            ${Hometype.get(Home.get(trip?.home_id)?.hometype_id)['name'+context?.lang]}                                        
                                          </li>                                               
                                          <li class="details-list-item dates">
                                            <span class="icons"></span>
                                            ${String.format('%td.%<tm.%<tY',trip.fromdate)} - ${String.format('%td.%<tm.%<tY',trip.todate)}
                                          </li>
                                          <li class="details-list-item person_capacity">
                                            <span class="icons"></span>
                                          <g:each in="${homeperson}" var="item"><g:if test="${item?.id==trip?.homeperson_id}">
                                            ${item['name'+context?.lang]}
                                          </g:if></g:each>
                                          </li>
                                        </ul>
                                      </div>
                                      <div class="options col" style="margin-top:0px">
                                      <g:each var="count" in="${0..<5}">
                                        <g:link controller="trip" action="rating" params="[id:trip.id,rating:count+1,act:'current']" class="star_icon_container">
                                          <div id="rating_${trip.id}_${count}" onmouseover="starred(${trip.id},${count});" onmouseout="unstarred(${trip.id},${count},${trip?.rating});" 
                                            class="star_icon ${(trip?.rating>count)?'starred':''}" style="margin-right:-6px"></div>
                                        </g:link>
                                      </g:each>
                                      </div>  
                                    </div>
                                    <div class="description float" style="width:480px;margin-top:-5px">
                                      <h2 class="title padd20 paddtop float">
                                        <g:link mapping="${'pView'+context?.lang}" params="${[uid:'id'+trip.owner_id]}">${trip.user_nickname}</g:link>
                                      </h2>
                                      <div class="contprn col" align="right">                                        
                                        <div class="details col" style="width:200px">
                                        <g:if test="${trip?.paystatus==3}">
                                          <font color="red">${message(code:'trip.calceled')}</font><br/>
                                        </g:if>
                                          <span class="ss_price b-rub" style="padding:0 !important">                                      
                                            ${Math.round(trip?.price_rub / valutaRates)}<g:rawHtml>${valutaSym}</g:rawHtml>                                      
                                          </span><br/>                         
                                          <font color="#000">${message(code:'personal.cost')}</font>
                                        </div>
                                      </div>
                                    </div>
                                  </div>                                  
                                  <div class="clearfix dview">
                                    <div class="actions float">
                                      <span class="action_button">
                                        <g:link class="icon view" mapping="${'hView'+context?.lang}" params="${[country:Country.get(trip.home_country_id)?.urlname,city:trip.home_city,linkname:trip?.home_linkname,service:1]}">${message(code:'trip.review.send')}</g:link>
                                      </span>
                                      <!--<span class="action_button">
                                        <g:link class="icon print" controller="trip" action="paydocuments" id="${trip?.id}" target="_blank">распечатать документы</g:link>
                                      </span>-->
                                    <g:if test="${trip?.paystatus<2}">
                                      <span class="action_button">
                                        <a class="icon inactive" href="javascript:void(0)" onclick="declineTrip(${trip?.id});">${message(code:'trip.cancel')}</a>
                                      </span>
                                    </g:if>
                                    </div>
                                  </div>                                  
                                </td>
                              </tr>
                              <g:if test="${(i+1)!=data.records.size()}"> 
                              <tr><td class="dashed">&nbsp;</td></tr>
                              </g:if> 
                            </g:each>                              
                            </table>
                          </g:if>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                    <div rel="layer" style="display: none">
                    </div>
                  </div>
                </div>
              </td>
            </tr>    
  </body>
</html>
