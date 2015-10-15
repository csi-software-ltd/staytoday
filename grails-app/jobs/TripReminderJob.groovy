import org.codehaus.groovy.grails.commons.ConfigurationHolder

class TripReminderJob {
  def mailerService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((ConfigurationHolder.config.tripReminder.cron!=[:])?ConfigurationHolder.config.tripReminder.cron:"0 0 10 * * ?")
	}

	def execute() {
		log.debug("LOG>> TripReminderJob start")
		mailerService.tripRemindMailer(Trip.findAllByModstatusAndFromdateBetween(1,new Date()-1+Tools.getIntVal(ConfigurationHolder.config.tripReminder.daysbefore,1),new Date()+Tools.getIntVal(ConfigurationHolder.config.tripReminder.daysbefore,1)))
	}
}