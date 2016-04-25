import org.springframework.web.servlet.support.RequestContextUtils as RCU
//import org.codehaus.groovy.grails.commons.ConfigurationHolder

class LocaleUrlFilters {

    def filters = {
        all(controller:'administrators|jcaptcha',invert:true) {
          before = {                                
            if(!request.xhr/* && !(controllerName=='home' && actionName=='s')*/){              
              //log.debug('request.forwardURI='+request.forwardURI) 
              //log.debug('RCU.getLocale(request).language.toString()='+RCU.getLocale(request).language.toString())  
              if(!(request.forwardURI.contains('/en/')||(request.forwardURI.size() >= 3 && request.forwardURI.substring(request.forwardURI.size() - 3)=='/en'))){
                if(RCU.getLocale(request).language.toString()!='ru')
                  RCU.getLocaleResolver(request).setLocale(request, response, new Locale("ru","RU"))
                
                if(controllerName=='index' && actionName=='index' && request.forwardURI.size() >= 12 && request.forwardURI.substring(request.forwardURI.size() - 12)=='/index/index'){                
                  redirect(mapping:'mainpage',permanent:true)                  
                  return false
                }  
              }else{     
                if(RCU.getLocale(request).language.toString()!='en')
                  RCU.getLocaleResolver(request).setLocale(request, response, new Locale("en"))

                //log.debug('request.forwardURI='+request.forwardURI) 
                //log.debug('controllerName='+controllerName+', actionName='+actionName)
                  
                if(!(request.forwardURI.size() >= 3 && request.forwardURI.substring(request.forwardURI.size() - 3)=='/en')){
                  if(controllerName=='index' && actionName=='index'){
                    redirect(mapping:'mainpage_en',permanent:true)                  
                    return false
                  } 
                } 
              }                        
            }
          }
          after = {
          }
          afterView = {

          }
        }
    }
}