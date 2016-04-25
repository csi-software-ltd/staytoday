import grails.util.Holders
class SpecofferMailJob {
  def specofferMailerService
  static triggers = {
    //simple startDelay:10000, repeatInterval: 10000, repeatCount: 0
    //simple repeatInterval: 30000, repeatCount: 0 //test: execute job once after 30 seconds
    cron cronExpression: ((Holders.config.specoffer.cron!=[:])?Holders.config.specoffer.cron:"0 0 0 ? * 1") //sunday
  } 
  
  def execute() {
    log.debug("LOG>> SpecofferMailJob: start")
    specofferMailerService.mailer()
    log.debug("LOG>> SpecofferMailJob: finish")
  }
}