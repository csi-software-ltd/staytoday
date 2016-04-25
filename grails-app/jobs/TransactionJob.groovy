import grails.util.Holders
class TransactionJob {
  def billingService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((Holders.config.transaction.cron!=[:])?Holders.config.transaction.cron:"0 0/15 * * * ?")
	}

	def execute() {
		Paytrans.findAllByModstatus(0).each{
			try {
				billingService.doTransaction(it.id)
			} catch(Exception e) {}
		}
	}

}