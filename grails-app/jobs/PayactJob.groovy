import org.codehaus.groovy.grails.commons.ConfigurationHolder
class PayactJob {
  def billingService
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.payact.cron!=[:])?ConfigurationHolder.config.payact.cron:"0 0 5 1 * ?")
	}

	def execute() {
		log.debug("LOG>> PayactJob start>>>")
		billingService.processGenerateAct()
		billingService.computeClientSaldo()
		log.debug("LOG>> PayactJob finish<<<")
	}

}