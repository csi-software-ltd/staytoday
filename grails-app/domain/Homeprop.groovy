class Homeprop {  
  
  static mapping = {
    version false
  }	  
  
  static constraints = {    
	addtax(nullable:true)
	date_start(nullable:true)
	date_end(nullable:true)
    valuta_id(nullable:true)
    term(nullable:true)
	remark(nullable:true)
    price(nullable:true)//TODO remove
	price_rub(nullable:true)//TODO remove  
    priceweekend(nullable:true)//TODO remove
    priceweekend_rub(nullable:true)//TODO remove
    priceweek(nullable:true)//TODO remove
    priceweek_rub(nullable:true)//TODO remove
    pricemonth(nullable:true)//TODO remove
    pricemonth_rub(nullable:true)//TODO remove
  }
  
  Long id 
  Long home_id
  Long price
  Long price_rub
  Long priceweekend
  Long priceweekend_rub
  Long priceweek
  Long priceweek_rub
  Long pricemonth
  Long pricemonth_rub
  Integer valuta_id
  Date date_start
  Date date_end
  Long mbox_id = 0
  Integer modstatus
  Integer term
  Long addtax   
  String remark
  ////////////////////////////////////////////////////////////////////////////////////////////////////
//owner Dmitry>>
  Boolean addHomepropUnavailability(lId,start,end,title='',iMod=3,lMbxId=0){
	def dateSt= new GregorianCalendar()
	try{
		dateSt.setTime(new Date(Date.parse(start)))
	} catch (Exception e) {
		dateSt.setTime(start)
	}
	dateSt.add(Calendar.HOUR_OF_DAY,1)
	dateSt.set(Calendar.HOUR_OF_DAY ,0)
	if (end==null) {
		def oHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<=:date_start_query AND date_end>=:date_start_query) AND home_id=:home_id AND (modstatus=3 OR modstatus=5)",[date_start_query:dateSt.getTime(),home_id:lId])
		if (!oHomeprop)
			return true
		oHomeprop[0].remark=title?:''
		if(!oHomeprop[0].save(flush:true)) {
			log.debug(" Error on save homeprop:")
			oHomeprop.errors.each{log.debug(it)}
		}
		return false
	}
	def dateEnd= new GregorianCalendar()
	try{
		dateEnd.setTime(new Date(Date.parse(end)))
	} catch (Exception e) {
		dateEnd.setTime(end)
		dateEnd.add(Calendar.DATE,-1)
	}
	dateEnd.add(Calendar.HOUR_OF_DAY,1)
	dateEnd.set(Calendar.HOUR_OF_DAY ,0)
	def today = new Date()
	def date1= new GregorianCalendar()
	date1.setTime(today)
	date1.set(Calendar.HOUR_OF_DAY ,0)
	date1.set(Calendar.MINUTE ,0)
	date1.set(Calendar.SECOND,0)
	date1.set(Calendar.MILLISECOND,0)
	if(dateEnd.getTime()<date1.getTime())
		return true
	def busyHomeprop = new Homeprop()
	busyHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<=:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND (modstatus=3 OR modstatus=5) ORDER BY date_start",[date_start_query:dateSt.getTime(),date_end_query:dateEnd.getTime(),home_id:lId])
	if (busyHomeprop.size()>0)
		return true

	def changingHomeprop = new Homeprop()
	changingHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<=:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND modstatus>0 ORDER BY date_start",[date_start_query:dateSt.getTime(),date_end_query:dateEnd.getTime(),home_id:lId])
	def dateEndMod= new GregorianCalendar()
	dateEndMod.setTime(dateEnd.getTime())
	dateEndMod.add(Calendar.DATE,1)
	def dateStMod= new GregorianCalendar()
	dateStMod.setTime(dateSt.getTime())
	dateStMod.add(Calendar.DATE,-1)
	for (hmp in changingHomeprop){
		if ( hmp.date_start < dateSt.getTime() ){
			if ( hmp.date_end > dateEnd.getTime() ){
				def tmpHmp1 = new Homeprop()
				tmpHmp1.properties = hmp.properties
				tmpHmp1.date_start = dateEndMod.getTime()
				tmpHmp1.save(flush:true)
				def tmpHmp2 = new Homeprop()
				tmpHmp2.properties = hmp.properties
				tmpHmp2.modstatus = 4
				tmpHmp2.date_start = dateSt.getTime()
				tmpHmp2.date_end = dateEnd.getTime()
				tmpHmp2.save(flush:true)
				hmp.date_end = dateStMod.getTime()
				hmp.save(flush:true)
			} else {
				def tmpHmp2 = new Homeprop()
				tmpHmp2.properties = hmp.properties
				tmpHmp2.modstatus = 4
				tmpHmp2.date_start = dateSt.getTime()
				tmpHmp2.save(flush:true)
				hmp.date_end = dateStMod.getTime()
				hmp.save(flush:true)
			}
		} else {
			if ( hmp.date_end > dateEnd.getTime() ){
				def tmpHmp2 = new Homeprop()
				tmpHmp2.properties = hmp.properties
				tmpHmp2.modstatus = 4
				tmpHmp2.date_end = dateEnd.getTime()
				tmpHmp2.save(flush:true)
				hmp.date_start = dateEndMod.getTime()
				hmp.save(flush:true)
			} else {
				hmp.modstatus = 4
				hmp.save(flush:true)
			}
		}
	}
	def oHomeprop=new Homeprop()
	oHomeprop.home_id=lId
    oHomeprop.price=0
    oHomeprop.valuta_id=0
    oHomeprop.date_start=dateSt.getTime()
    oHomeprop.date_end=dateEnd.getTime()
    oHomeprop.term=0
	oHomeprop.mbox_id=lMbxId?:0
	oHomeprop.modstatus=iMod?:3
	oHomeprop.remark=title?:''
	if(!oHomeprop.save(flush:true)) {
		log.debug(" Error on save homeprop:")
		oHomeprop.errors.each{log.debug(it)}
	}
	def oHome = Home.get(lId)
	if(oHome){
        try{
		  oHome.moddate = new Date()
		  oHome.caldate = new Date()
          if(!oHome.save(flush:true)) {
            log.debug(" Error on save home(Homprop.addHomepropUnavailability()):")
            oHome.errors.each{log.debug(it)}
          }
        }catch(Exception e){
          log.debug(" Error on save home(Homprop.addHomepropUnavailability()):"+e.toString())
        }
	}
	return false
  }
  
  Boolean removeHomepropUnavailability(lId,start,lMboxId=0){
		def dateSt= new GregorianCalendar()
		def oHome = Home.get(lId)
		try{
			dateSt.setTime(new Date(Date.parse(start)))
		} catch (Exception e) {
			dateSt.setTime(start)
		}
		dateSt.set(Calendar.HOUR_OF_DAY ,0)
		def busyHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<=:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND (modstatus=3 OR modstatus=5)  AND mbox_id=:mbox_id ORDER BY date_start",[date_start_query:dateSt.getTime(),date_end_query:dateSt.getTime(),home_id:lId,mbox_id:lMboxId.toLong()])
		if (busyHomeprop.size()>0) {
			def changingHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<=:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND modstatus=4 ORDER BY date_start",[date_start_query:busyHomeprop[0].date_start,date_end_query:busyHomeprop[0].date_end,home_id:lId])
			for(hmp in changingHomeprop){
				if(hmp.term==2 && oHome?.pricestatus!=1) {
					hmp.delete(flush:true)
				} else {
					hmp.modstatus = 1
					hmp.save(flush:true)
				}
			}
		}
		for(hmp in busyHomeprop){
			hmp.delete(flush:true)
		}
		if(oHome){
			try{
				oHome.moddate = new Date()
				oHome.caldate = new Date()
				if(!oHome.save(flush:true)) {
					log.debug(" Error on save home(Homprop.removeHomepropUnavailability()):")
					oHome.errors.each{log.debug(it)}
				}
			}catch(Exception e){
				log.debug(" Error on save home(Homprop.removeHomepropUnavailability()):"+e.toString())
			}
		}
		return false
  }

  void setHmpPrice_rub() {
		if(this.valuta_id==857){
			this.price_rub=this.price
			this.priceweekend_rub=this.priceweekend?:0
			this.priceweek_rub=this.priceweek?:0
			this.pricemonth_rub=this.pricemonth?:0
		} else {
			def oValutarate = new Valutarate()
			def valutaRates = oValutarate.csiGetRate(this.valuta_id)
			this.price_rub = Math.rint(this.price * valutaRates)
			this.priceweekend_rub = Math.rint((this.priceweekend?:0) * valutaRates)
			this.priceweek_rub = Math.rint((this.priceweek?:0) * valutaRates)
			this.pricemonth_rub = Math.rint((this.pricemonth?:0) * valutaRates)
		}
  }

  void synchronizeRegular(){
		def oHome = Home.get(this.home_id)
		if(oHome?.pricestatus==1){
			try{
				def aHmp = Homeprop.findAll('FROM Homeprop WHERE home_id=:id AND term=2 AND modstatus=1',[id:oHome.id])
				for (hmp in aHmp){
					hmp.delete(flush:true)
				}
				oHome.addRegular()
			} catch (Exception e){
				log.debug("Error on save Home id:"+oHome.id+"\n"+e.toString())
			}
		}
  }

  void remove(){
		def oHome= Home.get(this.home_id)
		this.modstatus = 1
		this.term = 2
		this.remark = ''
		this.price = oHome.pricestandard
		this.price_rub = oHome.pricestandard_rub
		this.priceweekend = oHome.priceweekend
		this.priceweekend_rub = oHome.priceweekend_rub
		this.priceweek = oHome.priceweek
		this.priceweek_rub = oHome.priceweek_rub
		this.pricemonth = oHome.pricemonth
		this.pricemonth_rub = oHome.pricemonth_rub
		this.valuta_id = oHome.valuta_id
		this.save(flush:true)
  }

  def csiGetOldHmp() {
		def oHmp = Homeprop.findAll('FROM Homeprop WHERE date_end<current_date and modstatus<5')
		return oHmp
  }

  def csiGetHmpUpdatePrice() {
		def oHmp = Homeprop.findAll('FROM Homeprop WHERE valuta_id!=857 and term!=0')
		return oHmp
  }
//<<owner Dmitry
}