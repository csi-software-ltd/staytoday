import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PostMailerJob {
  def postService
  static triggers = {
    //simple repeatInterval: 60000, repeatCount: 0 // execute job once in 200 seconds
    cron cronExpression: ((ConfigurationHolder.config.postmailer.cron!=[:])?ConfigurationHolder.config.postmailer.cron:"0 0 10 * * ?")
  }

  def execute() {
    log.debug("LOG>> PostMailerJob: start")
    if(Tools.getIntVal(ConfigurationHolder.config.is_sent_post_mail,0))
      postService.postMailer()	  
  }
}