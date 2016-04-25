//special thanks to DM
//import org.codehaus.groovy.grails.commons.grailsApplication
import java.math.RoundingMode

class StatsController {
  def requestService  
  def beforeInterceptor = [action:this.&checkAdmin]
  
  def checkAdmin() {   
    if(session?.admin?.id==null) {
      redirect(controller:'administrators', action:'index')
      return false;
    }
  }
  ////////////////////////////////////////////////////////////////////////////////////////////
  private getReqParams(){
	requestService.init(this)
    def hsParams=requestService.getParams(['statref_id','time','succeed','output','site_id','lang_id'],['id'],['stat','keyword','code'])
    return hsParams
  }
  ////////////////////////////////////////////////////////////////////////////////////////////
  def checkAccess(iActionId){
    def bDenied = true
    session.admin.menu.each{if (iActionId==it.id) bDenied = false}
    if (bDenied) {redirect(controller:'administrators',action:'profile');return}
  }
  ///////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    checkAccess(7)
    requestService.init(this)

    def hsParams=getReqParams()
    def hsRes=[ statrefs:Statref.findAll('FROM Statref ORDER BY name'),
                inrequest:hsParams.inrequest, action_id:7, admin:session.admin, type: requestService.getIntDef('id',0) ]
    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////////////
  def liststats={
    checkAccess(7)
    requestService.init(this)
    def oStats=new StatSearch()
    def hsParams=getReqParams()
    def hsRes=[data:[],inrequest:hsParams.inrequest]    	

    if(hsParams.inrequest.stat=='keywords')
      hsRes.data=oStats.getStatsByKeyword(hsParams,Tools.getIntVal(grailsApplication.config.request.max,30))
    else if(hsParams.inrequest.stat=='section')
      hsRes.data=oStats.getStatsBySection(hsParams,0)
    else if(hsParams.inrequest.stat=='service')
      hsRes.data=oStats.getStatsByService(hsParams,0)
    else if(hsParams.inrequest.stat=='home')
      hsRes.data=oStats.getStatsByHome(hsParams,Tools.getIntVal(grailsApplication.config.request.max,30))
    else if(hsParams.inrequest.stat=='prop')
      hsRes.data=oStats.getStatsByProp(hsParams,Tools.getIntVal(grailsApplication.config.request.max,30))
    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////////////
/*  
  def liststatscsv={
    checkAccess(8)
    requestService.init(this)
    def oStats=new StatSearch()
    def hsParams=getReqParams()
    def lsRes=[]    
    def sCsv
    if(hsParams.string.stat=='rubric')
      lsRes=oStats.getStatsByRubric(hsParams,requestService.getMax())
    else if(hsParams.string.stat=='direction')      
      lsRes=oStats.getStatsByDirection(hsParams,requestService.getMax()) 
    else if(hsParams.string.stat=='keywords')
      lsRes=oStats.getStatsByKeyword(hsParams,requestService.getMax())
    else if(hsParams.string.stat=='section')
      lsRes=oStats.getStatsBySection(hsParams,requestService.getMax())    
    else if(hsParams.string.stat=='service')
      lsRes=oStats.getStatsByService(hsParams,requestService.getMax())
    else if (hsParams.string.stat=='company'){
      def hsCompanyParams = requestService.getParams(['code','grouplevel'])
      lsRes = getCompanyStat(hsCompanyParams.inrequest)
      sCsv = '"Дата","Ключевые слова","Рубрики","Мидсайт","EA","Yandex","Google","На сайт"\n'
      for (row in lsRes)
        sCsv += '"'+String.format('%td.%<tm.%<tY',row.logtime)+'",'+row.keysearch.toString()+','+row.rubr.toString()+','+row.midsite.toString()+',"'+row.eamidsite.toString()+'('+row.percent.toString()+'%)",'+ row.yamidsite.toString()+','+row.gomidsite.toString()+','+row.extsite+'\n'
      response.setHeader("Content-disposition", "attachment; filename=company${hsCompanyParams.inrequest?.code}.csv")
    }
    if (!(hsParams.string.stat=='company')){
      sCsv='"Название","Значение"\n'
      for(row in lsRes)
        sCsv+='"'+row.name.replace('"','\\"')+'",'+row.quant.toString()+"\n"
      response.setHeader("Content-disposition", "attachment; filename=${hsParams.string.stat}.csv")
    }
    response.contentType = 'text/csv;charset=windows-1251'
    response.outputStream << sCsv.getBytes('windows-1251');
    response.outputStream.flush()      
  }
*/
  ///////////////////////////////////////////////////////////////////////////////////////////  
  def viewstats={
    checkAccess(7)
    requestService.init(this)
    def hsParams=getReqParams()
    def hsRes=[ statrefs:Statref.findAll('FROM Statref ORDER BY name'),
                inrequest:hsParams.inrequest]
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////  
  def viewdetstats={
    checkAccess(7)
    requestService.init(this)    
    
    def hsParams=getReqParams()
    hsParams.string.keyword=hsParams.string.keyword?.replace('+',' ')//1) dirty hack-- workaround grails' bug
    // 1) seems there is grails bug with tag remoteLink
    // grails changes spaces in params into '+' but send it (Ajax.Updater) through a POST
    // request, so + is unnecessary and one spoils params
    // Changing (via option) request type to method "get" does not result:
    // '+' encodes as %2b
    // DM 
        
    def hsRes=[ inrequest:hsParams.inrequest, data:[],circle:'',bar:'' ]
    def lsCircle=[]
    
    def oStats=new StatSearch()

    if(hsParams.string.stat=='keywords')
      hsRes.data=oStats.getStatsByKeywordDetail(hsParams,0)
    else if(hsParams.string.stat=='section'){
      hsRes.data=oStats.getStatsBySectionDetail(hsParams,0)
      lsCircle=oStats.getStatsBySectionDetailCircle(hsParams,0)
    }
    else if(hsParams.string.stat=='service')
      hsRes.data=oStats.getStatsByServiceDetail(hsParams,0)
	else if(hsParams.string.stat=='home'){
      hsRes.data=oStats.getStatsByHomeDetail(hsParams,0)
	  if(hsParams.int.output==0)
		lsCircle=oStats.getStatsByHomeDetailCircle(hsParams,0)
	}
	else if(hsParams.inrequest.stat=='prop'){
      hsRes.data=oStats.getStatsByPropDetail(hsParams,0)
	  if(hsParams.int.output!=1)
		lsCircle=oStats.getStatsByPropDetailCircle(hsParams,0)
	}

    def 
      iMaxBar=0,
      iMaxCircle=0
    def
      sCircleLabel=''
        
    for(row in hsRes.data){
      iMaxBar=Math.max(row.quant,iMaxBar.toLong())
	  hsRes.bar+=((hsRes.bar!='')?',':'')+row.quant.toString()
	}
	if(lsCircle!=[]){
	  for(row in lsCircle){
		iMaxCircle=Math.max(row.quant,iMaxCircle.toLong())
		hsRes.circle+=((hsRes.circle!='')?',':'')+row.quant.toString()
		sCircleLabel+=row.name+'|'
	  }
      hsRes.circle="&chd=t:"+hsRes.circle+"&chds=0,"+iMaxCircle+'&chl='+sCircleLabel
    }
    hsRes.bar="&chd=t:"+hsRes.bar+"&chds=0,"+iMaxBar+"&chxt=y,x&chxl=0:|0|"+
      Math.round(iMaxBar/2)+'|'+iMaxBar+"|1:"
	def i=0
    for(row in hsRes.data){
		hsRes.bar+='|'+(++i)
	}
    return hsRes
  }

  ///////////////////////////////////////////////////////////////////////////////////////////
  def robots = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    return hsRes
  }

  def robotstat = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes += requestService.getParams(['type'],null,['city_name'],['requesttime'])

    def oStat = new Statspiderpage()
    hsRes.statistic = oStat.csiSelectStatistic(hsRes.inrequest.type?:0,hsRes.inrequest.city_name?:'',
                                               hsRes.inrequest.requesttime?:'',20,requestService.getOffset())
    hsRes.pagetypes = [31:'комнаты',32:'метро',65:'тип жилья',66:'ориентиры',67:'чистый(жильё)']
    hsRes.hometypes = Hometype.list()

    return hsRes
  }

}