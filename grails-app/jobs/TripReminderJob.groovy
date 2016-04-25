import grails.util.Holders
class TripReminderJob {
  def mailerService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((Holders.config.tripReminder.cron!=[:])?Holders.config.tripReminder.cron:"0 0 10 * * ?")
	}

	def execute() {
		log.debug("LOG>> TripReminderJob start")
		mailerService.tripRemindMailer(Trip.findAllByModstatusAndFromdateBetween(1,new Date()-1+Tools.getIntVal(Holders.config.tripReminder.daysbefore,1),new Date()+Tools.getIntVal(Holders.config.tripReminder.daysbefore,1)))
	}
}