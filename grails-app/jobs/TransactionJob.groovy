import org.codehaus.groovy.grails.commons.ConfigurationHolder
class TransactionJob {
  def billingService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((ConfigurationHolder.config.transaction.cron!=[:])?ConfigurationHolder.config.transaction.cron:"0 0/15 * * * ?")
	}

	def execute() {
		Paytrans.findAllByModstatus(0).each{
			try {
				billingService.doTransaction(it.id)
			} catch(Exception e) {}
		}
	}

}