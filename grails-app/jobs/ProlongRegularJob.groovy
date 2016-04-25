import grails.util.Holders
class ProlongRegularJob {
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((Holders.config.prolongRegular.cron!=[:])?Holders.config.prolongRegular.cron:"0 0 3 1,15 * ?")
	}

	def execute() {
		log.debug("LOG>> Prolong regular arenda price")
		def homeToUpdate = Home.findAllByPricestatus(1)
		for (oH in homeToUpdate){
			try{
				def aHmp = Homeprop.findAll('FROM Homeprop WHERE home_id=:id AND term=2 AND modstatus=1',[id:oH.id])
				for (hmp in aHmp){
					hmp.delete(flush:true)
				}
				oH.addRegular()
			} catch (Exception e){
				log.debug("Error on save Home id:"+oH.id+"\n"+e.toString())
			}
		}
		log.debug("LOG>> Regular arenda price was prolonged succesfully")
	}
}