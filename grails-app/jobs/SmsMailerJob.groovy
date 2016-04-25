import grails.util.Holders
class SmsMailerJob {
	def smsService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((Holders.config.smsMailer.cron!=[:])?Holders.config.smsMailer.cron:"0 0 10 * * ?")
	}

	def execute() {
		log.debug("LOG>> SmsMailerJob: start")
		smsService.smsMailer()
		log.debug("LOG>> SmsMailerJob: finish")
	}
}