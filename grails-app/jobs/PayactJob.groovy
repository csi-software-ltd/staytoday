import grails.util.Holders
class PayactJob {
  def billingService
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((Holders.config.payact.cron!=[:])?Holders.config.payact.cron:"0 0 5 1 * ?")
	}

	def execute() {
		log.debug("LOG>> PayactJob start>>>")
		billingService.processGenerateAct()
		billingService.computeClientSaldo()
		log.debug("LOG>> PayactJob finish<<<")
	}

}