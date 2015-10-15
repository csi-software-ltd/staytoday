import org.codehaus.groovy.grails.commons.ConfigurationHolder
class WidgetController {
  def requestService
  /////////////////////////////////////////////////////////////////////////////////////
  def search = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.where=requestService.getStr('where')
    hsRes.where=hsRes.where?:'Россия'
    hsRes.valuta_id=requestService.getIntDef('valuta_id',hsRes.context.shown_valuta.id)

    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def search_result = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,false,true)
    hsRes+=requestService.getParams(['valuta_id'],null,['where'])

    def oHomeSearch=new HomeSearch()

    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(hsRes.inrequest?.valuta_id?:hsRes.context.shown_valuta.id)
    hsRes.valutaSym = Valuta.get(hsRes.inrequest?.valuta_id?:hsRes.context.shown_valuta.id).symbol

    hsRes.inrequest.max = Tools.getIntVal(ConfigurationHolder.config.widget.searchlisting.quantity.max,1)
    hsRes+=oHomeSearch.csiFindByWhere(hsRes.inrequest.where?:'Россия',hsRes.inrequest.max,requestService.getOffset(),[order:-1],[minrating:Tools.getIntVal(ConfigurationHolder.config.widget.searchlisting.minrating.value,30)],true)

    hsRes.urlphoto = ConfigurationHolder.config.urlphoto
	
    requestService.setStatistic('widget',0,0,hsRes.count,hsRes.inrequest.where)
    return hsRes
  }
}