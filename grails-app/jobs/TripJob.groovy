import grails.util.Holders
class TripJob {
	static triggers = {
		//simple repeatInterval: 150000 // execute job once in 150 seconds
		cron cronExpression: ((Holders.config.trip.cron!=[:])?Holders.config.trip.cron:"0 0 0 * * ?")
	}

	def execute() {
		log.debug("LOG>> Updating trip modstatus")
		def today = new GregorianCalendar()
		today.setTime(new Date())
		today.add(Calendar.HOUR_OF_DAY,1)
		today.set(Calendar.HOUR_OF_DAY ,0)
		today.set(Calendar.MINUTE ,0)
		today.set(Calendar.SECOND,0)
		today.set(Calendar.MILLISECOND,0)
		def aTrip = Trip.findAllByModstatus(2)
		for (trip in aTrip){
			if (today.getTime()>trip.todate){
				try{
					trip.modstatus = 3
					trip.save(flush:true,failOnError:true)
				} catch (Exception e){
					log.debug("Error on save Trip id:"+trip.id+"\n"+e.toString())
				}
			}
		}
		aTrip = Trip.findAllByModstatus(1)
		for (trip in aTrip){
			if (today.getTime()>=trip.fromdate){
				try{
					trip.modstatus = 2
					trip.save(flush:true,failOnError:true)
				} catch (Exception e){
					log.debug("Error on save Trip id:"+trip.id+"\n"+e.toString())
				}
			}
		}
		aTrip = Trip.findAllByModstatus(0)
		for (trip in aTrip){
			if (today.getTime()>trip.todate){
				try{
					trip.modstatus = -1
					trip.save(flush:true,failOnError:true)
				} catch (Exception e){
					log.debug("Error on save Trip id:"+trip.id+"\n"+e.toString())
				}
			}
		}
		log.debug("LOG>> Trip modstatus was updated succesfully")
		log.debug("LOG>> Updating payorder modstatus")
		for(payway in Payway.findAllByModstatus(1)) {
			def timelife = payway.is_invoice?Tools.getIntVal(Holders.config.payorder.invoicelife.days,7):Tools.getIntVal(Holders.config.payorder.noinvoicelife.days,3)
			def tmpOrder = Payorder.findAll("from Payorder where modstatus=0 and payway_id=:payway_id and inputdate<:inputdate",[payway_id:payway.id,inputdate:(today.getTime()-timelife)])
			tmpOrder.each{
				try{
					it.modstatus = -1
					it.save(flush:true,failOnError:true)
				} catch (Exception e){
					log.debug("Error on save PayOrder id:"+it.id+"\n"+e.toString())
				}
			}
		}
		log.debug("LOG>> payorder modstatus was updated succesfully")
	}
}