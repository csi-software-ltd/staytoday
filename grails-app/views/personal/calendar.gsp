<html>
  <head>    
    <title>${infotext['title'+context?.lang]?:''}</title>     
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />           
    <meta name="layout" content="main"/>
    <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'calendar.css')}" />            
    <g:javascript>
    var lId = ${home?.id};    
    function initialize(){
    <g:if test="${session.attention_message!='' && session.attention_message!=null}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
    </g:if>
    <g:if test="${session.attention_message_once}">
      jQuery.colorbox({
        html: '<div class="new-modal"><h2 class="clearfix" style="color:#ff530d">${message(code:'data.attention')} !</h2><div style="padding:15px">'+'${session.attention_message_once}'.unescapeHTML()+'</div></div>',
        scrolling: false
      });
    </g:if>
      showCal();
    }    
    function showCal(){
			jQuery('div#calendar').fullCalendar({
				firstDay: 1,
      <g:if test="${context?.lang=='_en'}">
				dayNamesShort:['Su','Mo','Tu','We','Th','Fr','Sa'],        
				monthNames:['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
      </g:if><g:else>
        dayNamesShort:['Вс','Пн','Вт','Ср','Чт','Пт','Сб'],
        monthNames:['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'],
      </g:else>
				buttonText:{today:"${message(code:'ads.calendar.today')}"},
				selectable:true,
				selectHelper:true,
				select: function(start, end) {
					if (confirm('${message(code:"ads.calendar.confirm.add")}')){
						var title = prompt('${message(code:"ads.calendar.promt")}');
						if (title!=null){
              <g:remoteFunction controller='personal' action='addUnavailability' onSuccess='processResponse(e)' params="'id='+lId+'&title='+title+'&start='+(start.getTime()-(new Date().getTimezoneOffset()+180)*60*1000)+'&end='+(end.getTime()-(new Date().getTimezoneOffset()+180)*60*1000)" />
						} else {
							jQuery('div#calendar').fullCalendar( 'unselect' );
						}
					} else {
						jQuery('div#calendar').fullCalendar( 'unselect' );
					}
				},
				eventSources: [{
						url:"${createLink(controller:'personal',action:'event')}",
						data:{
							id: lId,
							pc: 1,
              tz: new Date().getTimezoneOffset()
						}
					}
				],
				eventRender: function(event,element){
					if (event.className=='old'){
						jQuery('.fc-day'+event.dayNum).css({'background':'none','background-color':'#efefef'});//silver;            
					} else if (event.className=='inactive') {
						jQuery('.fc-day'+event.dayNum).css({'background':'none','background-color':'tomato'});//tomato;
					} else if (event.className=='active') {
						jQuery('.fc-day'+event.dayNum).css({'background':'none','background-color':'#ccffcc'});//white;
					} else if (event.className=='notavailable' || event.className=='reserved') {
						jQuery('.fc-day'+event.dayNum).css({'background':'none','background-color':'#efefef'});//silver;
						if (event.description){
							jQuery(element).qtip({	
                content: event.description,
								/*corner: { target: 'bottomLeft', tooltip: 'bottomMiddle' },*/
                position: { my: 'bottom center', at: 'top center' },
                show: 'mouseover',
								hide: 'mouseout',
                style: { classes: 'ui-tooltip-shadow ui-tooltip-' + 'tipsy' }
							});
						}
					}
				},
				eventClick: function (calEvent) {
					if (calEvent.className=='notavailable' || event.className=='reserved'){
						show_message(calEvent);
					} else if (calEvent.className=='active') {
						alert('${message(code:"ads.calendar.cost")}'+calEvent.title);
					} else if (calEvent.className=='reserved') {
						window.open('${((context?.is_dev)?"/"+context?.appname+"/inbox/view/":"/inbox/view/")}'+calEvent.mbox_id)
					}
				}
			});
    }
    
    function processResponse(e){
      if(e.responseJSON.error){
	    jQuery('div#calendar').fullCalendar( 'unselect' );
        alert('${message(code:"ads.error.calendar.period")}');
      }else{
        jQuery('div#calendar').fullCalendar( 'unselect' );
        jQuery('div#calendar').fullCalendar( 'refetchEvents' );
      }
    }
    
    function show_message(event){
	    var vis = getClientSTop()
	    if(!document.getElementById('message')) {
        var message = document.createElement('div');
        message.setAttribute('id', 'message');
        message.setAttribute('class', 'glossy messages');
        message.style.top = vis + 200 + 'px';
        document.body.appendChild(message);   
        
        var buttonClose = document.createElement('div');
        buttonClose.setAttribute('class', 'message-item-close'); 
        buttonClose.style.marginTop = '-14px';
        buttonClose.style.marginRight = '-14px';
        buttonClose.onclick = function(){
	        document.body.removeChild(message);
        }

        var text = document.createElement('div');
        text.setAttribute('class', 'text');
        text.innerHTML = '<g:rawHtml>${message(code:"ads.calendar.period")}</g:rawHtml>';        
        
        var buttonDel = document.createElement('input');
        buttonDel.setAttribute('type', 'button');
        buttonDel.setAttribute('class', 'button-glossy red'); 
        buttonDel.style.marginRight = '10px';        
        buttonDel.setAttribute('value', '${message(code:"button.delete")}');
        
        buttonDel.onclick = function(){
	        document.body.removeChild(message);
          if (confirm('${message(code:"ads.calendar.confirm.delete")}')){
            <g:remoteFunction controller='personal' action='removeUnavailability' onSuccess='processResponse(e)' params="'id='+lId+'&start='+(event.start.getTime()-(new Date().getTimezoneOffset()+180)*60*1000)" />
          }
        }
        
        var buttonEdit = document.createElement('input');
        buttonEdit.setAttribute('type', 'button'); 
        buttonEdit.setAttribute('class', 'button-glossy green');        
        buttonEdit.style.marginRight = '10px';
        buttonEdit.setAttribute('value', '${message(code:"button.change")}');
        buttonEdit.onclick = function(){
	        document.body.removeChild(message);
          var title = prompt('${message(code:"ads.calendar.desc")}',event.description?event.description:'');
          if (title!=null){
            <g:remoteFunction controller='personal' action='addUnavailability' onSuccess='processResponse(e)' params="'id='+lId+'&title='+title+'&start='+(event.start.getTime()-(new Date().getTimezoneOffset()+180)*60*1000)" />
          }
        }        
        
        message.appendChild(buttonClose);  
        message.appendChild(text);
        message.appendChild(buttonDel);        
        message.appendChild(buttonEdit);        
      }
    }
    
    function getClientSTop(){
	    return self.pageYOffset || (document.documentElement && document.documentElement.scrollTop) || (document.body && document.body.scrollTop)
    }
    
    window.onscroll = function  () {
	    if(document.getElementById('message')){
        var message = document.getElementById('message');
	      var vis = getClientSTop();
	      message.style.top=vis+200+"px";
	    }
    }
    </g:javascript>
  </head>
  <body onload="initialize();">
            <g:render template="/personal_menu" />
                      <div class="body" style="padding-top:10px">
                        <div class="slideshow">
                          <div id="calendar"></div>
                        </div>  
                      </div>
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>
                    <div rel="layer" style="display:none">
                    </div>                    
                  </div>
                </div>
              </td>
            </tr>
  </body>
</html>
