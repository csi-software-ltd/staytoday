import grails.util.Holders
class RatesJob {
  def ratesService
	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
		cron cronExpression: ((Holders.config.rates.cron!=[:])?Holders.config.rates.cron:"0 0 8 * * ?")
	}

  def execute() {
    ratesService.getRates()
  }
}