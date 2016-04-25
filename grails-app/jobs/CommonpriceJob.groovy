import grails.util.Holders
class CommonpriceJob {
	static triggers = {
		//simple repeatInterval: 100000 // execute job once in 200 seconds
		cron cronExpression: ((Holders.config.commonprice.cron!=[:])?Holders.config.commonprice.cron:"0 0 5 * * ?")
	}

	def execute() {
		log.debug("LOG>> mark uncommon prices homes")
		Commonpriceintervals.list().each{ interval ->
			Home.findAllByHometype_idAndHomeroom_id(interval.hometype_id,interval.homeroom_id).each { home ->
				home.csiSetUnrealiable(
					Homeprop.findAll{
						home_id == home.id &&
						modstatus == 1 &&
						term > 0 &&
						(price_rub < interval.price_min ||
						price_rub > interval.price_max)
					}?1:0
				).save(flush:true)
			}
		}
		log.debug("LOG>> Homes was marked succesfully")
	}
}