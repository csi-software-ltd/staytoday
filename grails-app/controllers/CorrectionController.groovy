import java.net.URLDecoder
//import org.codehaus.groovy.grails.commons.grailsApplication

class CorrectionController {  
  def requestService    
 
  def index = { 
    requestService.init(this) 
    def hsRes=requestService.getContextAndDictionary()
  /*
    if(hsRes.spy_protection){	  
	  redirect(controller:'index',action:'captcha')
	  return
	}
	*/    
    def oGuestbook=new Guestbook()        				
    hsRes.ip=oGuestbook.csiGetIpCount("'"+request.getRemoteAddr()+"'")

    if(hsRes.ip.count<Tools.getIntVal(grailsApplication.config.guestbook.ip_max,10)){		  
	
      oGuestbook.gbtype_id=2            
      oGuestbook.adr=request.getHeader('referer')?:''      
      oGuestbook.rectitle=requestService.getStr('rectitleOrph')
      oGuestbook.rectext=requestService.getStr('rectextOrph')
      oGuestbook.user_id=hsRes?.user?.id?:null         
      oGuestbook.modstatus = 0			
      oGuestbook.ip =request.getRemoteAddr()
    
      if(!oGuestbook.save(flush:true)) {
        log.debug(" Error on save guestbook:")
        oGuestbook.errors.each{log.debug(it)}
        render(contentType:"application/json"){[error:true]}
        return
      }  
    }
    requestService.setStatistic('guestbook',16)
    render(contentType:"application/json"){[error:false]}
    return	
  }     
}
