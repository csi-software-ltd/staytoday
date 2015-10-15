import javax.servlet.http.Cookie
class SecurityFilters {

    def filters = {
        personal(controller: 'm', action: '*', invert: true) {
            before = {
                if(Tools.getIntVal(Dynconfig.findByName('global.https.enable')?.value,0))
                    if (!City.findByDomain(request.serverName)&&isRequiresHttps(controllerName,actionName)&&!request.getRequestURL().toString().startsWith(grailsApplication.config.grails.secureServerURL?grailsApplication.config.grails.secureServerURL?.trim():grailsApplication.config.grails.serverURL.trim())) {
                        response.setHeader("Pragma", "no-cache")
                        response.setDateHeader("Expires", 1L)
                        if(request.forwardURI.contains('/en/'))
                          redirect(controller: controllerName, action: actionName,base:grailsApplication.config.grails.secureServerURL.trim()+'/en',params:params,permanent:true)
                        else
                          redirect(controller: controllerName, action: actionName,base:grailsApplication.config.grails.secureServerURL.trim(),params:params,permanent:true)
                        return false
                    } else if(!City.findByDomain(request.serverName)&&!isRequiresHttps(controllerName,actionName)&&!request.getRequestURL().toString().startsWith(grailsApplication.config.grails.serverURL.trim())&&request.serverName.indexOf('m.staytoday')!=0) {
                        response.setHeader("Pragma", "no-cache")
                        response.setDateHeader("Expires", 1L)
                        if(request.forwardURI.contains('/en/'))
                          redirect(mapping:(controllerName=='home'&&actionName=='detail'?'hView':''),controller: controllerName, action: actionName,base:grailsApplication.config.grails.serverURL.trim()+'/en',permanent:true,params:(((controllerName=='profile'&&actionName=='view')||(controllerName=='home'&&actionName=='detail'))?params-[id:params.id.toString()]:params))
                        else
                          redirect(mapping:(controllerName=='home'&&actionName=='detail'?'hView':''),controller: controllerName, action: actionName,base:grailsApplication.config.grails.serverURL.trim(),permanent:true,params:(((controllerName=='profile'&&actionName=='view')||(controllerName=='home'&&actionName=='detail'))?params-[id:params.id.toString()]:params))
                        return false
                    }
            }
            after = { model ->
                if (Tools.getIntVal(Dynconfig.findByName('global.https.enable')?.value,0))
                    if (isRequiresHttps(controllerName,actionName)) {
                        if (model?.imageurl)
                            model.imageurl = model.imageurl.replace('http://','https://')
                        if (model?.urlphoto)
                            model.urlphoto = model.urlphoto.replace('http://','https://')
                        if (model?.homeimageurl)
                            model.homeimageurl = model.homeimageurl.replace('http://','https://')
                    }
            }
            afterView = {

            }
        }
        notdomain(controller: 'home|user|jcaptcha|error|m', invert: true){
            before = {
                if(!(request.serverName.indexOf('.staytoday')==-1)&&!(actionName in ['changeLang','changeValuta','selectpopcities','clickbanner','robots'])&&!actionName.startsWith('sitemap')){
                    response.sendError(404)
                    return false
                }
            }
        }
        admrestore(controller: 'administrators', action: 'login', invert: true) {
            before = {
                def sessId = request.cookies.find{it.name == 'ADMSESSIONID'}?.value
                if (sessId) {
                    def oCookie = new Cookie('JSESSIONID', sessId)
                    oCookie.domain = 'staytoday.ru'
                    oCookie.path = '/'
                    response.addCookie(oCookie)
                    response.setHeader("Pragma", "no-cache")
                    response.setDateHeader("Expires", 1L)
                }
            }
        }
        admsave(controller: 'administrators', action: 'login') {
            before = {
                def sessId = request.cookies.find{it.name == 'JSESSIONID'}?.value
                if (sessId) {
                    def oCookie = new Cookie('ADMSESSIONID', sessId)
                    oCookie.domain = 'staytoday.ru'
                    oCookie.path = '/'
                    response.addCookie(oCookie)
                    response.setHeader("Pragma", "no-cache")
                    response.setDateHeader("Expires", 1L)
                }
            }
        }
    }
    private boolean isRequiresHttps(controllerName,actionName) { controllerName in ['personal','inbox','account','trip','administrators','adminbilling','stats'] || (controllerName=='error' && actionName in ['page_401','login','facebook','vk','index'])}
}