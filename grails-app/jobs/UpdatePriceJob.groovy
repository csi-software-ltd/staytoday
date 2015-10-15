import org.codehaus.groovy.grails.commons.ConfigurationHolder

class UpdatePriceJob {
	static triggers = {
		//simple repeatInterval: 200000 // execute job once in 200 seconds
		cron cronExpression: ((ConfigurationHolder.config.updatePrice.cron!=[:])?ConfigurationHolder.config.updatePrice.cron:"0 15 8 * * ?")
	}

	def execute() {
		log.debug("LOG>> Updating Price")
		def oHomeprop = new Homeprop()
		def hmpToUpdate = oHomeprop.csiGetHmpUpdatePrice()
		for (oH in hmpToUpdate){
			try{
				oH.setHmpPrice_rub()
				oH.save(flush:true)
			} catch (Exception e){
				log.debug("Error on updating Homeprop price - id:"+oH.id+"\n"+e.toString())
			}
		}
		def oHome = new Home()
		def homeToUpdate = oHome.csiGetHomeUpdatePrice()
		for (oH in homeToUpdate){
			try{
				oH.csiSetPrice_rub()
				oH.csiSetExtrasPrice_rub()
				oH = oH.merge()
				oH.save(flush:true)
			} catch (Exception e){
				log.debug("Error on save Home id:"+oH.id+"\n"+e.toString())
			}
		}
		log.debug("LOG>> Price was updated succesfully")
	}
}