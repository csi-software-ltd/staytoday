import org.codehaus.groovy.grails.commons.ConfigurationHolder

class HomemodstatusJob {
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((ConfigurationHolder.config.modstatus.cron!=[:])?ConfigurationHolder.config.modstatus.cron:"0 15 0 * * ?")
	}

	def execute() {
		log.debug("LOG>> Updating home modstatus")
		def obj = new HomeSearchMods()
		def aHomes = obj.csiSelectHomes()
		def oHome
		for (home in aHomes){
			if (!home.is_step_descr
				||!home.is_step_map
				||!home.is_step_photo
				||!home.is_step_price
				||!(home.user_modstatus==1)
				||!(home.user_tel?:0)){
				try{
					oHome = Home.get(home.id)
					oHome.modstatus = 2
					oHome.save(flush:true)
					log.debug("Modstatus was updated for Home id: "+home.id+"\n")
				} catch (Exception e){
					log.debug("Error on save Home id:"+home.id+"\n"+e.toString())
				}
			}
		}
		log.debug("LOG>> Home modstatus was updated succesfully")
	}
}
