import grails.util.Holders
class PostMailerJob {
  def postService
  static triggers = {
    //simple repeatInterval: 60000, repeatCount: 0 // execute job once in 200 seconds
    cron cronExpression: ((Holders.config.postmailer.cron!=[:])?Holders.config.postmailer.cron:"0 0 10 * * ?")
  }

  def execute() {
    log.debug("LOG>> PostMailerJob: start")
    if(Tools.getIntVal(Holders.config.is_sent_post_mail,0))
      postService.postMailer()	  
  }
}