import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ResendDelayedSmsJob {
	def smsService
	static triggers = {
		//simple repeatInterval: 200000 // execute job once in 200 seconds
		cron cronExpression: ((ConfigurationHolder.config.resendDelayedSms.cron!=[:])?ConfigurationHolder.config.resendDelayedSms.cron:"0 0 0-12 * * ?")
	}

	def execute() {
		log.debug("LOG>> Start resend sms")
		def lsDelayed = DelayedSMS.findAllByModstatus(0)
		def curHour = (new Date()).getHours()
		lsDelayed.each{
			if (Tools.getIntVal(ConfigurationHolder.config.noticeSMS.daytime.start,8)<=((curHour+(Region.get(it.region_id)?.timediff?:0)+24)%24)&&((curHour+(Region.get(it.region_id)?.timediff?:0)+24)%24)<Tools.getIntVal(ConfigurationHolder.config.noticeSMS.daytime.end,22)) {
				try {
					switch(it.type) {
						case 0:
							smsService.sendNoticeSMS(User.get(it.user_id),it.region_id)
						break
						case 1:
							smsService.sendPaymentSMS(User.get(it.user_id),it.region_id)
						break
						case 2:
							smsService.sendCancelBronSMS(User.get(it.user_id),it.region_id)
						break
						case 3:
							smsService.sendOfferSMS(User.get(it.user_id),it.region_id)
						break
					}
					it.modstatus = 1
					it.save(flush:true)
				}	catch(Exception e) {
	  			log.debug("Cannot sent sms in ResendDelayedSmsJob\n"+e.toString())
				}
			}
		}
		log.debug("LOG>> Sms resended")
	}
}