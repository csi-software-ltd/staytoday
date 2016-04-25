import grails.util.Holders
class CitySightJob {
  def searchService
	static triggers = {		
    //simple startDelay:10000, repeatInterval: 10000, repeatCount: 0
		cron cronExpression: ((Holders.config.citysight.cron!=[:])?Holders.config.citysight.cron:"0 15 3 * * ?")
	}

  def execute() {
    log.debug("LOG>> Citysight Job Start")
    
    def oCitysight2home1=new Citysight2home()
    oCitysight2home1.csiDeleteAllCitysight2home()

    def lsCitysights=Citysight.findAllWhere(modstatus:1,type:1)
    for(citysight in lsCitysights){
      def count=0
      def lsHomes=Home.findAllWhere(city_id:citysight.city_id.toLong(),modstatus:1)	  
      for (home in lsHomes){
        if(citysight.radius>=searchService.getDistance(citysight.x,citysight.y,home.x,home.y) / 1000){          
          def oCitysight2home=new Citysight2home()
          oCitysight2home.home_id=home.id
          oCitysight2home.citysight_id=citysight.id
          
          if (!oCitysight2home.save(flush:true)){
            log.debug('error on save Citysight2home in CitySightJob')
            oCitysight2home.errors.each{log.debug(it)}
          }
          count++;
        }          
      }
      if(count){      
        citysight.homecount=count
        if (!citysight.save(flush:true)){
          log.debug('error on save Citysight in CitySightJob')
          citysight.errors.each{log.debug(it)}
        }
      }
    }
    Citysight.findAll{ modstatus == 1 && type in [2,3] }.each{ sight -> sight.type == 2 ? sight.homecount = Home.findAll{ city_id == sight.city_id as Long && modstatus == 1 && street_id == sight.street_id }.each{ new Citysight2home(home_id:it.id,citysight_id:sight.id).save(flush:true) }.size() : (sight.homecount = Home.findAll{ city_id == sight.city_id as Long && modstatus == 1 && district_id == sight.district_id }.each{ new Citysight2home(home_id:it.id,citysight_id:sight.id).save(flush:true) }.size())
      sight.save(flush:true)
    }
    log.debug("LOG>> Citysight Job End") 
  }
}