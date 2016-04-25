//import org.codehaus.groovy.grails.commons.grailsApplication
class ZayavkaService {
  def grailsApplication
  static transactional = false
	
  def csiFindClient(oZayavka,lsExcludeIds=[]){
    def sCity=oZayavka.city
    def hsFilter=[region_id:oZayavka.region_id,
			    date_start:oZayavka.date_start,date_end:oZayavka.date_end,
			    price_min:oZayavka.pricefrom_rub,price_max:oZayavka.priceto_rub,
			    homeperson_id:oZayavka.homeperson_id,hometype_id:oZayavka.hometype_id,
			    client_id:1,exclude_ids:lsExcludeIds]
	def oHomeSearch=new HomeSearch()

	def hsRes=oHomeSearch.csiFindByCity(sCity,0,hsFilter)
	//log.debug('1. client_ids:='+hsRes.clientIds)
	//log.debug('1. records:='+hsRes.records)
	hsRes.clientIdsStep=[]
	hsRes.clientIdsStep<<hsRes.clientIds.size()
	//log.debug('1. hsRes.clientIdsStep:='+hsRes.clientIdsStep)   	  
	//>>2
	hsRes.clientIds += lsExcludeIds
	if(hsRes.clientIds.size())
	  hsFilter.exclude_ids=hsRes.clientIds
	hsFilter.homeperson_id=0
	hsFilter.hometype_id=0
	def hsResSecond=oHomeSearch.csiFindByCity(sCity,0,hsFilter)
	  //log.debug('2. client_ids:='+hsResSecond.clientIds)
	  //log.debug('2. records:='+hsResSecond.records)
    hsRes.records+=hsResSecond.records	  
	hsRes.clientIds+=hsResSecond.clientIds	  
	hsRes.clientIdsStep<<hsResSecond.clientIds.size()
	  //log.debug('2. hsRes.clientIdsStep:='+hsRes.clientIdsStep)
	//>>3 
	if(hsRes.clientIds.size())
	  hsFilter.exclude_ids=hsRes.clientIds
	hsFilter.price_max+=(hsFilter?.price_max*Tools.getIntVal(grailsApplication.config.zayvka.price_max.persent,0.5)).toLong()          		  
    def hsResThird=oHomeSearch.csiFindByCity(sCity,0,hsFilter)		  
	  //log.debug('3. client_ids:='+hsResThird.clientIds)
	  //log.debug('3. records:='+hsResThird.records)
    hsRes.records+=hsResThird.records		
	hsRes.clientIds+=hsResThird.clientIds		
	hsRes.clientIdsStep<<hsResThird.clientIds.size()
	  //log.debug('3. hsRes.clientIdsStep:='+hsRes.clientIdsStep)  
	//>>4
	if(hsRes.clientIds.size())
	  hsFilter.exclude_ids=hsRes.clientIds
	hsFilter.date_start_change=1
	def hsResFourth=oHomeSearch.csiFindByCity(sCity,0,hsFilter)          		  
	  //log.debug('4. client_ids:='+hsResFourth.clientIds)
	  //log.debug('4. records:='+hsResFourth.records)
	hsRes.records+=hsResFourth.records
	hsRes.clientIds+=hsResFourth.clientIds		 
    hsRes.clientIdsStep<<hsResFourth.clientIds.size()
	  //log.debug('4. hsRes.clientIdsStep:='+hsRes.clientIdsStep)  		  	      	
	//log.debug(hsRes)
	return hsRes
  }

  void createZayavkaFromMbox(oMbox){
		if (!oMbox)
			throw new Exception ('Mbox not specified')
  	def oUser = User.get(oMbox.user_id)
		if (!oUser)
			throw new Exception ('User was not found')
  	def oHome = Home.get(oMbox.home_id)
		if (!oHome)
			throw new Exception ('Home was not found')
		if (oMbox.inputdate+3<new Date())
			throw new Exception ('Mbox is too old')

		try{
			new Zayavka([country_id:oHome.country_id,region_id:oHome.region_id,city:oHome.city?:Region.get(oHome.region_id)?.name,
										date_start:String.format('%tY-%<tm-%<td',oMbox.date_start),date_end:String.format('%tY-%<tm-%<td',oMbox.date_end),
										homeperson_id:oMbox.homeperson_id,pricefrom:oHome.csiGetSomePrice(),priceto:oHome.csiGetSomePrice(),
										valuta_id:oMbox.valuta_id,hometype_id:oHome.hometype_id,ztext:Mboxrec.findByMbox_id(oMbox.id,[sort:'id',order:'asc'])?.rectext?:Email_template.findWhere(action:'#autoZayavka')?.title?:'some text',
										timetodecide_id:Timetodecide.findByDays(1)?.id?:1,mobtel:oUser.tel?:'-',baseclient_id:oMbox.homeowner_cl_id],oUser.id,'yyyy-MM-dd',1).save(flush:true)
		} catch (Exception e){
			throw e
		}
	}

}