<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>      
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <link rel="canonical" href="${createLink(controller:'trip',action:'favorite',base:context?.mainserverURL_lang)}" />
    <link rel="alternate" media="only screen and (max-width: 640px)" href="${createLink(uri:'/favorites',base:context?.mobileURL_lang)}" />
    <link rel="alternate" media="handheld" href="${createLink(uri:'/favorites',base:context?.mobileURL_lang)}" />
    <meta name="layout" content="main" />
    <g:javascript>
  	function clickPaginate(event){
      event.stop();
      var link = event.element();
      if(link.href == null){
        return;
      }  
      new Ajax.Updater(
        { success: $('results') },
        link.href,
        { evalScripts: true });		  
    }
    function initialize(){
      $('form_submit').click();	
    }
    var star_id=0;
    function addtofavourite(lId){
      star_id=lId;
      <g:remoteFunction controller='home' action='selectcompany' onSuccess='setFavourite(e)' params="\'id=\'+lId" />
    }
    function setFavourite(e){
      var bFlag=0;
      var arr=e.responseJSON.wallet;
      for(var i=0;i<arr.size();i++){
        if(star_id==arr[i]){
          $('star_icon_'+star_id).addClassName("starred");
          bFlag=1;
        }
      }
      if(!bFlag)
        $('star_icon_'+star_id).removeClassName("starred");      
      if(arr.length>0){
        $('favorite').removeClassName('no_active');
        $('favorite').href='<g:createLink controller="trip" action="favorite"/>'; 
        $('favorite').parentElement.addClassName(' starred');
      } else {
        $('favorite').addClassName('no_active');
        $('favorite').href='javascript:void(0)';
        $('favorite').parentElement.removeClassName(' starred');
      }
    }	
    </g:javascript>
    <style type="text/css">      
      .hlisting .fn.title{margin-top:0!important}
      .hlisting .title .name{margin-left:40px}
      .hlisting .description{margin-left:20px}
      .services-list.list{border:0!important;margin:0!important;padding:0!important}
    </style>  
  </head>
  <body onload="initialize()">
              <g:render template="/trip_menu" />
                      <div class="search-container">
                        <div id="search_body">
                          <div id="results"></div>              
                          <g:formRemote name="filter_form" url="${[action:'search_table']}" method="GET" update="[success:'results']">	      	  
                            <input id="form_submit" type="submit" value="Показать" style="display:none"/>			
                          </g:formRemote>
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
