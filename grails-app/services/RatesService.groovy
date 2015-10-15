import org.codehaus.groovy.grails.commons.ConfigurationHolder
class RatesService {
  def sessionFactory
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP  
  boolean transactional = true
  
  private escape(sText){
    return sText.replace('\\','\\\\').replace('\"','\\\"').replace('\'','\\\'')
  } 
    // -- does not work: wsdl from M$ incorrect for CXF
    //def soapProxy = new WSClient("http://cbr.ru/DailyInfoWebServ/DailyInfo.asmx?WSDL", this.class.classLoader)
    //soapProxy.initialize()
    //def result = soapProxy.GetLatestDateTime()  
    
  def getRates() {
    log.debug("Fetch CBR rates ")  
    def sUrl=ConfigurationHolder.config.cbr?.url
    def sCodepage=ConfigurationHolder.config.cbr?.codepage
    
    def oFeed
    def sContent
    
    def i=0
    while(!sContent){          
      if(i)
        Thread.currentThread().sleep(Tools.getIntVal(ConfigurationHolder.config.rates.delay,300) *1000)
     
      try{    
        def oUrl = new URL(sUrl ?: 'http://www.cbr.ru/scripts/XML_daily.asp')
        sContent = oUrl.getText(sCodepage ?: "Windows-1251") 
      }catch(Exception e){
        log.debug("ERROR on fetch CBR rates "+e.toString())
        sContent=null
      }                                                                   
      //log.debug(new Date().getDateTimeString()+' sContent: '+i+' = '+sContent)      
      i++                          
    }

    if(sContent){
      try{
        oFeed  = new XmlParser().parseText(sContent)
        log.debug("Parse CBR rates success")
      }catch(Exception e){
        log.debug("ERROR on parse CBR rates "+e.toString())
      }

      def session = sessionFactory.getCurrentSession()       
      def connection = session.connection()         
      def state = connection.createStatement()         
      
      def lsItems = oFeed?.Valute

      if(lsItems==null) return
      def sVal
      def sSql
      for(oItem in lsItems){
        sVal=escape(oItem.Value.text()).replace(',','.')
        sSql = "INSERT INTO valutarate(valuta_id,vrate,vdate,code,name,dim)"+
               " VALUES ('"+escape(oItem.NumCode.text())+"','"+
               sVal+ "', CURDATE(),'"+escape(oItem.CharCode.text())+
               "','"+escape(oItem.Name.text())+
               "','"+escape(oItem.Nominal.text())+"')"+
            "ON DUPLICATE KEY UPDATE vrate='"+sVal+"';\n" 
        state.addBatch(sSql)
      } 
      
      try{
        state.executeBatch()        
      }catch(Exception e){
        log.debug("ERROR on save CBR rates"+e.toString())
      }
      session.flush()
      session.clear()
      propertyInstanceMap.get().clear()
    }              
  }
}
