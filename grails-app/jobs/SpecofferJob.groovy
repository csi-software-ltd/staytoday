import grails.util.Holders
class SpecofferJob {
  static triggers = {
    //simple repeatInterval: 250000 // execute job once in 250 seconds
    cron cronExpression: ((Holders.config.specoffer.cron!=[:])?Holders.config.specoffer.cron:"0 0 5 * * ?")
  }

  def execute() {
    if(Tools.getIntVal(Holders.config.is_use_autoSpecoffer,1)){
      log.debug("LOG>> SpecofferJob: start")
      for(home in Home.findAllByIs_specoffer_auto(1)) {
        try {
          home.is_specoffer_auto = 0
          home.save(flush:true)
        } catch(Exception e) {
          log.debug("Error on save Home id: "+home.id+"\n"+e.toString())
        }
      }
      for(home in Home.findHomeForSpecoffer()) {
        try {
          home.is_specoffer_auto = 1
          home.save(flush:true)
        } catch(Exception e) {
          log.debug("Error on save Home for specoffer - id: "+home.id+"\n"+e.toString())
        }
      }
      //postService.postMailer()
      log.debug("LOG>> SpecofferJob: finish")
    }
  }
}