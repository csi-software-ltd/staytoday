import grails.util.Holders
class ZayavkaMailerJob {
  def mailerService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((Holders.config.zayavkamailer.cron!=[:])?Holders.config.zayavkamailer.cron:"0 0/15 * * * ?")
	}

	def execute() {
		log.debug("LOG>> ZayavkaMailerJob: start")
		mailerService.zayavkaMailer()
	}
}