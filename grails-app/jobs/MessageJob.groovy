import grails.util.Holders
class MessageJob {
	def messagesService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((Holders.config.message.cron!=[:])?Holders.config.message.cron:"0 0 2 * * ?")
	}

	def execute() {
		log.debug("LOG>> messageJob: start")
	  messagesService.processMessages()
	  log.debug("LOG>> messageJob: finish")
	}
}