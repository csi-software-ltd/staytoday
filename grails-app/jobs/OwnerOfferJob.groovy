import grails.util.Holders
class OwnerOfferJob {
	def mailerService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
    //simple repeatInterval: 30000, repeatCount: 0 //test: execute job once after 30 seconds
		cron cronExpression: ((Holders.config.owneroffer.cron!=[:])?Holders.config.owneroffer.cron:"0 15 0/1 * * ?")
	}

	def execute() {
		log.debug("LOG>> Updating mbox modstatus")
		def aMbox = Mbox.findAllByModstatus(3)
		def tmpMboxrec
		for (mbox in aMbox) {
			tmpMboxrec = Mboxrec.findAllByMbox_idAndAnswertype_idInList(mbox.id,[1,2],[max:1, sort:'inputdate',order:'desc'])
			if(tmpMboxrec.size()>0){
				if (tmpMboxrec[0].inputdate.getTime()<new Date().getTime()-Tools.getIntVal(Holders.config.mbox.specofferlife.minutes,120)*60*1000){
					try{
						mbox.modstatus = 1
						mbox.save(flush:true)
						tmpMboxrec.each{
							it.answertype_id = 6
							it.save(flush:true,failOnError:true)
						}
						mailerService.offernotpaidmail(mbox)
					} catch (Exception e){
						log.debug("Error on save Mbox id:"+mbox.id+"\n"+e.toString())
					}
				}
			}
		}
		tmpMboxrec = Mboxrec.findAllByAnswertype_idInListAndInputdateLessThan([1,2],new Date(new Date().getTime()-Tools.getIntVal(Holders.config.mbox.specofferlife.minutes,120)*60*1000))
		tmpMboxrec.each{
			try{
				it.answertype_id = 6
				it.save(flush:true,failOnError:true)
			} catch (Exception e){
				log.debug("Error on save Mboxrec id:"+it.id+"\n"+e.toString())
			}
		}
		log.debug("LOG>> Mbox modstatus was updated succesfully")
	}
}