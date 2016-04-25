import grails.util.Holders
class TripConfirmControlJob {
	def billingService
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((Holders.config.tripConfirmControl.cron!=[:])?Holders.config.tripConfirmControl.cron:"0 17 2/3 * * ?")
	}

	def execute() {
		log.debug("LOG>> TripConfirmControlJob: start")
		def oAdmin = Admin.get(Tools.getIntVal(Holders.config.notifyAdmin.id,2))
		def now = new Date()
		def lsTrip = Trip.findAllByFromdateLessThanEqualsAndControlstatusInList(now,[0,1])
		lsTrip.each{
			try{
				if (oAdmin?.email)
					(new Zayavkabyemail(oAdmin.email,oAdmin.name,Payorder.get(it.payorder_id)?.norder?:'','',it.fromdate,it.todate,'#noConfirmTodayTripNotice')).save(flush:true)
				it.controlstatus = 2
				it.is_read = 0
				it.save(flush:true)
			} catch (Exception e){
				log.debug("Error on updating controlstatus (fromdate) for Trip - id:"+it.id+"\n"+e.toString())
			}
		}
		lsTrip = Trip.findAllByInputdateLessThanEqualsAndControlstatus(new Date(now.getTime()-1000*60*60*Tools.getIntVal(Holders.config.trip.noconfirm.hours,24)),0)
		lsTrip.each{
			try{
				if (oAdmin?.email)
					(new Zayavkabyemail(oAdmin.email,oAdmin.name,Payorder.get(it.payorder_id)?.norder?:'',Tools.getIntVal(Holders.config.trip.noconfirm.hours,24).toString(),it.fromdate,it.todate,'#noConfirmDailyTripNotice')).save(flush:true)
				it.controlstatus = 1
				it.is_read = 0
				it.save(flush:true)
			} catch (Exception e){
				log.debug("Error on updating controlstatus (inputdate) for Trip - id:"+it.id+"\n"+e.toString())
			}
		}
		lsTrip = Trip.findAll { modstatus == 2 && payorder_id > 0 && fromdate < new Date()-1 && paystatus == 1 }
		lsTrip.each{
			def fromdate = Calendar.getInstance()
			fromdate.setTime(it.fromdate)
			fromdate.add(Calendar.HOUR_OF_DAY,Rule_timein.get(Home.get(it.home_id)?.rule_timein_id?:1)?.kol?:15)
			if(fromdate.getTime()<new Date()-1&&Payorder.get(it.payorder_id)?.dealstatus==0){
				//generate transaction
				if (billingService.createPaytrans([payorder_id:it.payorder_id],'',15)){
					it.paystatus = 2
					it.save(flush:true)
				}
			}
		}
		log.debug("LOG>> TripConfirmControlJob: finish")
	}
}