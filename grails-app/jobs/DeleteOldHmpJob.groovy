import grails.util.Holders
class DeleteOldHmpJob {
	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 //test: execute job once after 30 seconds
		cron cronExpression: ((Holders.config.deleteOldHmp.cron!=[:])?Holders.config.deleteOldHmp.cron:"0 0 1 * * ?")
	}

	def execute() {
		log.debug("LOG>> Deleting old Homeprops")
		def oHomeprop = new Homeprop()
		def oldHmp = oHomeprop.csiGetOldHmp()
		for (oH in oldHmp){
			try{
				oH.delete(flush:true)
			} catch (Exception e){
				log.debug("Error on deleting old Homeprop id:"+oH.id+"\n"+e.toString())
			}
		}
		log.debug("LOG>> Old Homeprops was deleted succesfully")
		log.debug("LOG>> Updating Homes Without Price")
		def oHome = new Home()
		def badHome = oHome.csiGetHomeWithoutPrice()
		for (bH in badHome){
			try{
				bH.modstatus = 2
				bH.is_step_price = 0
				bH.save(flush:true)
				log.debug("Modstatus was updated for Home id: "+bH.id+"\n")
			} catch (Exception e){
				log.debug("Error on save Home id:"+bH.id+"\n"+e.toString())
			}
		}
		log.debug("LOG>> Homes was updated succesfully")
		log.debug("LOG>> Updating Region status")
		def aRegion = Region.list()
		for (region in aRegion){
			if (Home.findByRegion_idAndModstatus(region.id,1))
				region.modstatus = 1
			else
				region.modstatus = 0
			try{
				region.save(flush:true)
			} catch (Exception e){
				log.debug("Error on save Region id:"+region.id+"\n"+e.toString())
			}
		}
		log.debug("LOG>> Region status was updated succesfully")
		log.debug("LOG>> Updating City homecount")
		def lsHomecount = Home.executeQuery("""select c.id,count(h.id)
          from Home h, City c
          where h.modstatus=1 and h.city_id!=0 and h.city_id=c.id
          group by h.city_id
          order by c.name""").inject([:]){map,tag -> map[tag[0]]=tag[1];map}
		City.list().each{
			it.homecount = lsHomecount[it.id]?.toInteger()?:0
			it.save(flush:true,failOnError:true)
		}
    log.debug("LOG>> City homecount was updated succesfully")
    log.debug("LOG>> Updating Shome homecount")
    def oHomeSearch=new HomeSearch()
    def lsShome=Shome.list()    
    for(oShome in lsShome){                  
      def hsRes=oHomeSearch.csiFindApartments(City.get(oShome?.city_id?:0)?.name?:'')
      
      if(Shometype.get(oShome.type_id?:0)?.fieldname=='is_vip')
        oShome.homecount=hsRes.option.is_vip
      else if(Shometype.get(oShome.type_id?:0)?.fieldname=='is_fiesta')
        oShome.homecount=hsRes.option.is_fiesta
      else if(Shometype.get(oShome.type_id?:0)?.fieldname=='is_renthour')
        oShome.homecount=hsRes.option.is_renthour      
      
      try{
				oShome.save(flush:true)
			} catch (Exception e){
				log.debug("Error on save oShome \n"+e.toString())
			}        
    }
    log.debug("LOG>> Shome homecount was updated succesfully")
	}
}