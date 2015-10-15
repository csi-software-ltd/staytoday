<html>
<head>
  <title>${infotext['title'+context?.lang]?:''}</title>  
  <meta name="keywords" content="${infotext?.keywords?:''}" />
  <meta name="description" content="${infotext['description'+context?.lang]?:''}" />
  <link rel="canonical" href="${context.curl}" />  
  <meta name="layout" content="main"/>
  <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'cancellation_policies.css')}"> 
  <g:javascript>
    function initialize(){
      var link = (location.hash).substr(1);      
      if(link == '')
        viewCell('tabs',0,'layers');
      else
        $(link+'_nav').onclick();  
    }    
    function viewCell(sObj1,iNum,sObj2){
      var tabs = $(sObj1).getElementsByTagName('li');           
      var divs = $(sObj2).getElementsByTagName('div');            
      var layers = new Array();      
      for (var i=0; i < divs.length; i++)
        if(divs[i].getAttribute("rel")=='layer')
          layers.push(i);        
      for (var i=0; i < tabs.length; i++){
        tabs[i].className = (i==iNum) ? 'selected' : '';
        divs[layers[i]].style.display = (i==iNum)? 'block' : 'none';
      }
    } 
  </g:javascript>
  <style type="text/css">
    .subnav li { text-transform: lowercase !important }
    ul.top li { margin-left: 0 !important }
  </style>
</head>  
<body onload="initialize()">
            <tr style="height:110px">
              <td width="250" class="search s0">
                <g:if test="${isLoginDenied}"><a class="button" href="javascript:void(0)" rel="nofollow" onclick="showLoginForm()">${message(code:'common.deliverhome')}</a></g:if>
                <g:else><g:link class="button" controller="home" action="addnew" base="${context?.absolute_lang}">${message(code:'common.deliverhome')}</g:link></g:else>
              </td>
              <td colspan="3" class="rent ss">
                <h1 class="header">${infotext['header'+context?.lang]?:''}</h1>                
              </td>
            </tr>
            <tr>              
              <td colspan="4" class="bg_shadow">              
                <div class="breadcrumbs" xmlns:v="http://rdf.data-vocabulary.org/#">
                  <span typeof="v:Breadcrumb">
                    <a href="${createLink(uri:'',base:context?.mainserverURL_lang)}" rel="v:url" property="v:title">${message(code:'label.main')}</a> &#8594;
                  </span>
                  ${infotext['header'+context?.lang]?:''}
                </div>
              </td>
            </tr>            
            <tr>
              <td colspan="4" valign="top">
                <div class="content shadow" style="min-height: 547px;background:#fff">
                  <ul id="tabs" class="subnav"> 
                  <g:each in="${cancellation}" var="item" status="i">
                    <li id="${item?.shortlink}_nav" onclick="viewCell('tabs',${i},'layers')">${item['name'+context?.lang]}</li>
                  </g:each>
                  </ul>
                  <div id="layers" class="dashboard-content">
                    <div class="text-topic">
                      <g:rawHtml>${infotext['itext'+context?.lang]?:''}</g:rawHtml>
                    </div>                            
                  <g:each in="${cancellation}" var="item" status="i">        
                    <div rel="layer" class="relative" style="padding: 5px 20px 17px 20px">
                      <h4>${item['name'+context?.lang]}: ${item['fullname'+context?.lang]}</h4>
                      <ul class="top" style="margin-left:15px">
                        <g:rawHtml>${item['nb'+context?.lang]?:''}</g:rawHtml>
                        <g:rawHtml>${infotext['itext2'+context?.lang]?:''}</g:rawHtml>
                      </ul>
                      <div id="timeline-section">
                        <div id="timeline" align="center">
                        <g:if test="${i<4}">                
                          <div id="first-timeline-block">&nbsp;</div>
                          <div id="second-timeline-block" class="timeline-block${(i in [2,3])?'-severe':''}">
                            <g:if test="${i==0}">${message(code:'cancellation.within_24hours')}</g:if>
                            <g:elseif test="${i==1}">${message(code:'cancellation.within_5days')}</g:elseif>
                            <g:elseif test="${i==2}">${message(code:'cancellation.within_7days')}</g:elseif>
                            <g:elseif test="${i==3}">${message(code:'cancellation.within_30days')}</g:elseif>                  
                          </div>
                        </g:if><g:else>
                          <div id="first-half-timeline-block">${message(code:'cancellation.before_arrival')}</div>
                        </g:else>                
                          <div id="third-timeline-block">${message(code:'cancellation.during_stay')}</div>
                          <div id="fourth-timeline-block">&nbsp;</div>
                        </div>
                        <ol class="timeline-pins">
                          <g:if test="${i<4}">
                          <li id="first-pin">
                            <div class="label">
                              <div>${item['nday'+context?.lang]}</div>
                              <div class="arrow">
                                <div class="circle"></div>
                                <div class="time" nowrap>
                                  <g:if test="${i==0}">${message(code:'calendar.weekName').split(',')[3]},<br/>19 ${message(code:'calendar.monthNameP').split(',')[0]} 15:00</g:if>
                                  <g:elseif test="${i==1}">${message(code:'calendar.weekName').split(',')[6]},<br/>15 ${message(code:'calendar.monthNameP').split(',')[0]} 15:00</g:elseif>
                                  <g:elseif test="${i==2}">${message(code:'calendar.weekName').split(',')[4]},<br/>13 ${message(code:'calendar.monthNameP').split(',')[0]} 15:00</g:elseif>
                                  <g:elseif test="${i==3}">${message(code:'calendar.weekName').split(',')[2]},<br/>21 ${message(code:'calendar.monthNameP').split(',')[11]} 15:00</g:elseif>
                                </div>                                
                              </div>
                            </div>
                          </li>
                          </g:if>
                          <li id="second-pin">
                            <div class="label">
                              <div>${message(code:'detail.check_in')}</div>
                              <div class="arrow">
                                <div class="circle"></div>
                                <div class="time">${message(code:'calendar.weekName').split(',')[4]},<br/>20 ${message(code:'calendar.monthNameP').split(',')[0]} 15:00</div>
                              </div>
                            </div>
                            <div class="example">${message(code:'cancellation.example')}</div>
                          </li>
                          <li id="third-pin">
                            <div class="label">
                              <div>${message(code:'detail.check_out')}</div>
                              <div class="arrow">
                                <div class="circle"></div>
                                <div class="time">${message(code:'calendar.weekName').split(',')[0]},<br/>23 ${message(code:'calendar.monthNameP').split(',')[0]} 11:00</div>
                              </div>
                            </div>
                          </li>
                        </ol>
                      </div>
                      <div id="timeline-annotation" class="clearfix">              
                        <div class="tl-annotation">
                          <ul>
                            <li>${item['itext1'+context?.lang]?:''}</li>
                          </ul>
                        </div>              
                        <div class="tl-annotation">
                          <g:if test="${i<4}">
                          <div class="annotation-border"></div>
                          </g:if>
                          <ul>
                            <li>${item['itext2'+context?.lang]?:''}</li>
                          </ul>
                        </div>
                        <div class="tl-annotation">
                          <div class="annotation-border"></div>
                          <ul>
                            <li>${item['itext3'+context?.lang]?:''}</li>
                          </ul>
                        </div>
                      </div>        
                    </div>
                  </g:each>
                  </div>
                </div>
              </div>
            </div>
</body>
</html>
