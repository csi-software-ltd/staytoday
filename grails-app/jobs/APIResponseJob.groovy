import grails.util.Holders
class APIResponseJob {
  def apiService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((Holders.config.apiresponse.cron!=[:])?Holders.config.apiresponse.cron:"0 0/5 * * * ?")
	}

	def execute() {
		Api_response.findAllByModstatus(0).each{
			try {
				apiService.doResponse(it.id)
			} catch(Exception e) {}
		}
	}

}