import org.codehaus.groovy.grails.commons.ConfigurationHolder
class Valuta {    
  
  static constraints = {	
  }
  static mapping = {
    version false
  }
  Integer id
  Integer modstatus
  Integer regorder
  Integer min
  Integer max
  Integer step
  String code
  String name  
  String symbol    

  String toString() {"${this.code}" }  
  
////////////////////////////////////////////////////////////////////////////////////////////////////

  void setValutaCookie(requestService, value) {
	requestService.setCookie('SHOWN_VALUTA',value.toString(),Tools.getIntVal(ConfigurationHolder.config.valuta.timeout,2592000))
  }
}
