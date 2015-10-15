import org.codehaus.groovy.grails.commons.ConfigurationHolder
class HomeRatingJob {
  def ratingService
	static triggers = {
		//simple repeatInterval: 900000 // execute job once in 900 seconds
		cron cronExpression: ((ConfigurationHolder.config.homeRating.cron!=[:])?ConfigurationHolder.config.homeRating.cron:"0 0 6 * * ?")
	}

	def execute() {
		log.debug("LOG>> Updating home rating")
		ratingService.processRating()
		log.debug("LOG>> Rating was updated succesfully")
	}
}