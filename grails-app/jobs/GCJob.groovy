import grails.util.Holders
class GCJob {
  static triggers = {
    //simple repeatInterval: 200000 // execute job once in 200 seconds
    cron cronExpression: ((Holders.config.gc.cron!=[:])?Holders.config.gc.cron.trim():"0 0 9 ? * MON")
  }

    def execute() {
    	log.debug("LOG>> GCJob: start")
			System.gc()
			System.runFinalization()
			System.gc()
			log.debug("LOG>> GCJob: finish")
    }
}