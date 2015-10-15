import org.codehaus.groovy.grails.commons.ConfigurationHolder
class AdditionalRatingJob {
  def ratingService
	static triggers = {
		//simple repeatInterval: 200000 // execute job once in 200 seconds
		cron cronExpression: ((ConfigurationHolder.config.additionalRating.cron!=[:])?ConfigurationHolder.config.additionalRating.cron:"0 0 5 ? * MON")
	}

	def execute() {
		log.debug("LOG>> Updating additional home rating")
		ratingService.processAdditionalRating()
		log.debug("LOG>> Rating was updated succesfully")
	}
}