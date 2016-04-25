import grails.util.Holders
class ArchivingMboxJob {
	static triggers = {
		//simple repeatInterval: 200000 // execute job once in 200 seconds
		cron cronExpression: ((Holders.config.archivingMbox.cron!=[:])?Holders.config.archivingMbox.cron:"0 0 4 * * ?")
	}

	def execute() {
		log.debug("LOG>> ArchivingMboxJob: start")
		/*def lsMbox = Mbox.findAllByModdateLessThanAndModstatusNotEqual(new Date()-Tools.getIntVal(Holders.config.archivingMbox.mboxlife.days,30),6)
		lsMbox.each{
			try{
				it.modstatus = 6
				it.save(flush:true)
			} catch (Exception e){
				log.debug("Error on arhiving Mbox - id:"+it.id+"\n"+e.toString())
			}
		}*/
    
    def lsMbox = Mbox.findAllByDate_startLessThan(new Date()-1)
		lsMbox.each{
			try{
				it.modstatus = 6
				if (it.controlstatus==1) {
					it.controlstatus = 3
				}
				it.save(flush:true)
				Mboxrec.findAllByMbox_idAndAnswertype_idInList(it.id,[1,2]).each{ mboxrec ->
					mboxrec.answertype_id = 6
					mboxrec.save(flush:true)
				}
			} catch (Exception e){
				log.debug("Error on arhiving Mbox - id:"+it.id+"\n"+e.toString())
			}
		}
    
		log.debug("LOG>> ArchivingMboxJob: finish")
	}
}
