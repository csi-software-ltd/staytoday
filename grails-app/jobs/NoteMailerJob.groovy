import grails.util.Holders
class NoteMailerJob {
  def noteMailerService
	static triggers = {
		//simple repeatInterval: 250000 // execute job once in 250 seconds
		cron cronExpression: ((Holders.config.notemailer.cron!=[:])?Holders.config.notemailer.cron:"0 0 11 * * ?")
	}

  def execute() {
  	if (Tools.getIntVal(Holders.config.notemailer.active1,1)) {
			log.debug("LOG>> NoteMailerJob: start")
			noteMailerService.noteMailer()
  	}
  }
}