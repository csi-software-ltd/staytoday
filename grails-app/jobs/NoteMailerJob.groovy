import org.codehaus.groovy.grails.commons.ConfigurationHolder

class NoteMailerJob {
  def noteMailerService
	static triggers = {
		//simple repeatInterval: 250000 // execute job once in 250 seconds
		cron cronExpression: ((ConfigurationHolder.config.notemailer.cron!=[:])?ConfigurationHolder.config.notemailer.cron:"0 0 11 * * ?")
	}

  def execute() {
  	if (Tools.getIntVal(ConfigurationHolder.config.notemailer.active1,1)) {
			log.debug("LOG>> NoteMailerJob: start")
			noteMailerService.noteMailer()
  	}
  }
}