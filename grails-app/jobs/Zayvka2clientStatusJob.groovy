import grails.util.Holders
class Zayvka2clientStatusJob {
	static triggers = {
		//simple repeatInterval: 250000 // execute job once in 250 seconds
		cron cronExpression: ((Holders.config.zayvka2clientStatus.cron!=[:])?Holders.config.zayvka2clientStatus.cron:"0 0 0 * * ?")
	}

	def execute() {
		log.debug("LOG>> Zayvka2clientStatusJob: start")
		def oZayavka = Zayvka2client.findAll('FROM Zayvka2client WHERE todate<CURRENT_DATE')
		for (oZ in oZayavka){
			try{
				oZ.modstatus = -2
				oZ.save(flush:true)
			} catch (Exception e){
				log.debug("Error on updating Zayvka2client status - id:"+oZ.id+"\n"+e.toString())
			}
		}

		def duration
		def today = new GregorianCalendar()
		today.setTime(new Date())
		today.set(Calendar.HOUR_OF_DAY ,0)
		today.set(Calendar.MINUTE ,0)
		today.set(Calendar.SECOND,0)
		today.set(Calendar.MILLISECOND,0)
		def tempDate = new GregorianCalendar()
		oZayavka = Zayavka.findAllByModstatusGreaterThan(-3)
		for (oZ in oZayavka){
			tempDate.setTime(oZ.inputdate)
			tempDate.set(Calendar.HOUR_OF_DAY ,0)
			tempDate.set(Calendar.MINUTE ,0)
			tempDate.set(Calendar.SECOND,0)
			tempDate.set(Calendar.MILLISECOND,0)
			use(groovy.time.TimeCategory) {
				duration = today.getTime() - tempDate.getTime()
			}
			if (duration.days>Timetodecide.get(oZ.timetodecide_id).days)
				try{
					oZ.modstatus = -3
					oZ.save(flush:true)
					if (!Mbox.findByZayvka_id(oZ.id)) {
						def oAdmin = Admin.get(Tools.getIntVal(Holders.config.notifyAdmin.id,2))
						if (oAdmin?.email)
							(new Zayavkabyemail(oAdmin.email,oAdmin.name,oZ.id.toString(),oZ.ztext,oZ.date_start,oZ.date_end,'#zayavkaExpireNotice')).save(flush:true)
					}
				} catch (Exception e){
					log.debug("Error on updating Zayavka status - id:"+oZ.id+"\n"+e.toString())
				}
		}
	  log.debug("LOG>> Zayvka2clientStatusJob: finish")
	}
}