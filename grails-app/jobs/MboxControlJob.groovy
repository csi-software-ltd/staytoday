import org.codehaus.groovy.grails.commons.ConfigurationHolder

class MboxControlJob {
	def zayavkaService
	def mailerService
	static triggers = {
		//simple repeatInterval: 200000 // execute job once in 200 seconds
		cron cronExpression: ((ConfigurationHolder.config.mboxControl.cron!=[:])?ConfigurationHolder.config.mboxControl.cron:"0 55 * * * ?")
	}

	def execute() {
		log.debug("LOG>> MboxControlJob: start")
		def oAdmin = Admin.get(Tools.getIntVal(ConfigurationHolder.config.notifyAdmin.id,2))
		def now = new Date()
		def lsMbox = Mbox.findAllByModdateLessThanAndControlstatus(new Date(now.getTime()-1000*60*Tools.getIntVal(ConfigurationHolder.config.noAnswer.strictCondition.minutes,720)),-1)
		lsMbox.each{
			try{
				it.controlstatus = -2
				it.modstatus = 2
				it.save(flush:true)
				if (oAdmin?.email)
					(new Zayavkabyemail(oAdmin.email,oAdmin.name,it.id.toString(),Tools.getIntVal(ConfigurationHolder.config.noAnswer.strictCondition.minutes,720).toString(),it.date_start,it.date_end,'#noAnswerNotice')).save(flush:true)
				if (!Mbox.findAllByUser_idAndControlstatusInList(it.user_id,[-1,0,1])) {
					if(!Zayavka.findAllByUser_idAndInputdateGreaterThan(it.user_id,new Date()-1)){
						zayavkaService.createZayavkaFromMbox(it)
						mailerService.noanswermail(it)
					}
				}
			} catch (Exception e){
				log.debug("Error on updating controlstatus (strict) for Mbox - id:"+it.id+"\n"+e.toString())
			}
		}
		lsMbox = Mbox.findAllByModdateLessThanAndControlstatus(new Date(now.getTime()-1000*60*Tools.getIntVal(ConfigurationHolder.config.noAnswer.softCondition.minutes,120)),0)
		lsMbox.each{
			try{
				if (oAdmin?.email)
					(new Zayavkabyemail(oAdmin.email,oAdmin.name,it.id.toString(),Tools.getIntVal(ConfigurationHolder.config.noAnswer.softCondition.minutes,120).toString(),it.date_start,it.date_end,'#noAnswerNotice')).save(flush:true)
				it.controlstatus = -1
				it.save(flush:true)
			} catch (Exception e){
				log.debug("Error on updating controlstatus (soft) for Mbox - id:"+it.id+"\n"+e.toString())
			}
		}
		lsMbox = Mbox.findAllByControlstatus(2)
		lsMbox.each{
			try{
				it.controlstatus = -2
				it.save(flush:true)
				if (!Mbox.findAllByUser_idAndControlstatusInList(it.user_id,[-1,0,1])) {
					if (oAdmin?.email)
						(new Zayavkabyemail(oAdmin.email,oAdmin.name,it.id.toString(),it.mtext,it.date_start,it.date_end,'#denialNotice')).save(flush:true)
					if(!Zayavka.findAllByUser_idAndInputdateGreaterThan(it.user_id,new Date()-1))
						zayavkaService.createZayavkaFromMbox(it)
				}
			} catch (Exception e){
				log.debug("Error on updating controlstatus (denial) for Mbox - id:"+it.id+"\n"+e.toString())
			}
		}
		log.debug("LOG>> MboxControlJob: finish")
	}
}