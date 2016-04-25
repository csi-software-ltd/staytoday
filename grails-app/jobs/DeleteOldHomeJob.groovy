import grails.util.Holders
class DeleteOldHomeJob {
	static triggers = {
		//simple repeatInterval: 100000 // execute job once in 200 seconds
		cron cronExpression: ((Holders.config.oldhome.cron!=[:])?Holders.config.oldhome.cron:"0 0 5 1,10,20 * ?")
	}

	def execute() {
		log.debug("LOG>> deleting old homes")
		Home.findAll{ modstatus == -3 && moddate < (new Date()-7).clearTime() }.each{ home ->
			log.debug("Home for delete: "+home.id)
			Homediscount.get(home.longdiscount_id)?.delete(flush:true)
			Homediscount.get(home.hotdiscount_id)?.delete(flush:true)
			Homemetro.findAllByHome_id(home.id).each{ metro -> metro.delete(flush:true) }
			Homephoto.findAllByHome_id(home.id).each{ photo -> photo.delete(flush:true) }
			Homevideo.findAllByHome_id(home.id).each{ video -> video.delete(flush:true) }
			Homeprop.findAllByHome_id(home.id).each{ hmp -> hmp.delete(flush:true) }
			Ucomment.findAllByHome_idAndTypeid(home.id,1).each{ comment -> comment.delete(flush:true) }
			home.delete(flush:true)
		}
		log.debug("LOG>> Homes was delete succesfully")
	}
}