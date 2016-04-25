import grails.util.Holders
class Home {
  def searchService
  def ratingService

  static mapping = { version false }

  static constraints = {
		linkname(unique:true)
    homebath_id(nullable:true)
    bed(nullable:true)
    district_id(nullable:true) 	
		city_id(nullable:true) 
		street_id(nullable:true) 
		spcf(nullable:true) 	    
		remarks(nullable:true)
    fullinfo(nullable:true)
    moddate(nullable:true)
		caldate(nullable:true)
    fulladdress(nullable:true)
		homeroom_id(nullable:true)	
		is_parking(nullable:true)
    is_nosmoking(nullable:true)
    is_kitchen(nullable:true)
    is_tv(nullable:true)
    is_internet(nullable:true)
    is_wifi(nullable:true)
    is_holod(nullable:true)
    is_microwave(nullable:true)
    is_cond(nullable:true)
    is_family(nullable:true)
    is_pets(nullable:true)
    is_invalid(nullable:true)
    is_heat(nullable:true)
    is_wash(nullable:true)
    is_breakfast(nullable:true)
    is_visa(nullable:true)
    is_swim(nullable:true)
    is_steam(nullable:true)
    is_gym(nullable:true)
    is_hall(nullable:true)
    is_area(nullable:true)
    is_beach(nullable:true)
		homeoption(nullable:true)
		area(nullable:true)
		mainpicture(nullable:true)
		mainpicture_site(nullable:true)
		is_step_descr(nullable:true)
    is_step_map(nullable:true)
    is_step_photo(nullable:true)
		is_step_price(nullable:true)
		homenumber(nullable:true)
		city(nullable:true)    
		street(nullable:true)
		district(nullable:true)
		pricestandard(nullable:true)
		pricestatus(nullable:true)
		comments(nullable:true)
		homerule(nullable:true)
		mapkeywords(nullable:true)
		mapkeyphrase(nullable:true)
		rule_cancellation_id(nullable:true)
		rule_minday_id(nullable:true)
		rule_maxday_id(nullable:true)
		rule_timein_id(nullable:true)
		rule_timeout_id(nullable:true)
		shortaddress(nullable:true)    
		nref(nullable:true)
		reachpublic(nullable:true)
		reachpersonal(nullable:true)
    deposit(nullable:true)
    deposit_rub(nullable:true)
		infraoption(nullable:true)
		popdirection_id(nullable:true)
		rating(nullable:true)
		metro_id(nullable:true)    
		advtext(nullable:true)
    infraoption_en(nullable:true)
    is_vip(nullable:true)
  }
  Long id
  Long client_id
  String name  
  String linkname
  Integer is_mainpage
  Integer rating
  Integer extrarating
  Integer ratingpenalty
  Integer is_specoffer
  Integer is_specoffer_auto
  Integer is_index
  String mainpicture
  String mainpicture_site
  Integer hometype_id
  Integer homeclass_id
  Integer homeroom_id
  Integer homebath_id
  Integer bed
  Integer homeperson_id
  Integer is_fiesta
  Integer country_id
  Integer region_id
  Integer district_id
  String district
  Long city_id
  String city  
  Long street_id
  String street
  String homenumber
  String address
  String shortaddress  
  String mapkeywords
  String mapkeyphrase
  String pindex
  String fulladdress
  String spcf
  Long x
  Long y
  Integer geostatus
  Integer metro_id
  String reachpublic
  String reachpersonal
  Integer modstatus
  Integer is_confirmed
  Integer area
  String description  
  String advtext
  String remarks
  String comments
  String fullinfo
  Long pricestandard  
  Long pricestandard_rub
  Long priceweekend
  Long priceweekend_rub  
  Long priceweek
  Long priceweek_rub  
  Long pricemonth
  Long pricemonth_rub
  Integer pricestatus
  Integer is_pricebyday
  Integer rule_minday_id
  Integer rule_maxday_id
  Integer rule_timein_id
  Integer rule_timeout_id
  Integer rule_cancellation_id
  String homerule
  Integer valuta_id
  Long deposit
  Long deposit_rub
  Long fee
  Long fee_rub
  Integer fee_homeperson
  Long cleanup
  Long cleanup_rub
  Long longdiscount_id
  Long hotdiscount_id
  Date inputdate
  Date moddate
  Date caldate
  Integer is_parking
  Integer is_nosmoking
  Integer is_kitchen
  Integer is_tv
  Integer is_internet
  Integer is_wifi
  Integer is_holod
  Integer is_microwave
  Integer is_cond
  Integer is_family
  Integer is_pets
  Integer is_invalid
  Integer is_heat
  Integer is_wash
  Integer is_breakfast
  Integer is_visa
  Integer is_swim
  Integer is_steam
  Integer is_gym
  Integer is_hall
  Integer is_area
  Integer is_beach
  Integer is_iron
  Integer is_fen
  Integer is_kettle
  Integer is_coffee
  Integer is_hometheater
  Integer is_jacuzzi
  Integer is_renthour
  Integer is_vip
  String homeoption
  String infraoption
  String infraoption_en
  Integer is_step_descr
  Integer is_step_map
  Integer is_step_photo
  Integer is_step_price
  Integer nref
  Integer popdirection_id
  Integer unrealiable = 0
/////////////////////////////////constructor//////////////////////////////////////////////////////////////////////
  Home(){}
  Home(oRequest,lId){
		client_id = lId
		name = oRequest.title[0..(oRequest.title.size()<=35?oRequest.title.size()-1:34)]
		linkname = csiGetUniqueLinkname(name)
		is_mainpage = 0
		rating = 0
		extrarating = 0
		ratingpenalty = 0
		is_specoffer = 0
		is_specoffer_auto = 0
		is_index = 1
		mainpicture = ''
		mainpicture_site = ''
		hometype_id = oRequest.hometype_id
		homeclass_id = 1
		homeroom_id = 0
		homebath_id = 0
		bed = 0
		homeperson_id = oRequest.homeperson_id
		is_fiesta = 0
		country_id = oRequest.country_id
		region_id = oRequest.region_id
		district_id = 0
		district = oRequest.district?:''
		city_id = 0
		city = oRequest.city?:Region.get(oRequest.region_id).is_show==0?Region.get(oRequest.region_id).name:''
		street_id = 0
		street = oRequest.street?:''
		homenumber = oRequest.homenumber?:''
		address = Country.get(oRequest.country_id).name+' '+Region.get(oRequest.region_id).name+(((Region.get(oRequest.region_id).name!=oRequest.city)&&oRequest.city)?(' '+oRequest.city):'')+((oRequest.street?:'')?(' '+oRequest.street):'')+((oRequest.homenumber?:'')?(' '+oRequest.homenumber):'')
		shortaddress = Country.get(oRequest.country_id).name+' '+Region.get(oRequest.region_id).name+(((Region.get(oRequest.region_id).name!=oRequest.city)&&oRequest.city)?(' '+oRequest.city):'')+((oRequest.street?:'')?(' '+oRequest.street):'')
		mapkeywords = ''
		mapkeyphrase = ''
		pindex = oRequest.pindex?oRequest.pindex.toString():''
		fulladdress = ''
		spcf = ''
		x = 0
		y = 0
		geostatus = 0
		metro_id = 0
		reachpublic = ''
		reachpersonal = ''
		modstatus = 0
		is_confirmed = 0
		area = 0
		description = oRequest.description?:''
		advtext = ''
		remarks = ''
		comments = ''
		fullinfo = ''
		rule_minday_id = 1
		rule_maxday_id = 1
		rule_timein_id = 1
		rule_timeout_id = 1
		rule_cancellation_id = 1
		homerule = ''
		deposit = 0
		deposit_rub = 0
		fee = 0
		fee_rub = 0
		fee_homeperson = 1
		cleanup = 0
		cleanup_rub = 0
  	longdiscount_id = 0
  	hotdiscount_id = 0
		inputdate = new Date()
		moddate = new Date()
		caldate = new Date()
		is_parking = 0
		is_nosmoking = 0
		is_kitchen = 0
		is_tv = 0
		is_internet = 0
		is_wifi = 0
		is_holod = 0
		is_microwave = 0
		is_cond = 0
		is_family = 0
		is_pets = 0
		is_invalid = 0
		is_heat = 0
		is_wash = 0
		is_breakfast = 0
		is_visa = 0
		is_swim = 0
		is_steam = 0
		is_gym = 0
		is_hall = 0
		is_area = 0
		is_beach = 0
		homeoption = ''
		infraoption = ''
		is_step_descr = 0
		is_step_map = 0
		is_step_photo = 0
		is_step_price = 0
		is_iron = 0
    is_fen = 0
    is_kettle = 0
    is_coffee = 0
    is_hometheater = 0
    is_jacuzzi = 0
    is_renthour = 0
		nref = 0
		popdirection_id = 0
		unrealiable = 0
		//set price>>
		is_pricebyday = 0
		valuta_id = oRequest.valuta_id
		priceweekend = 0
		priceweek = 0
		pricemonth = 0           
    
		if(!oRequest.term){
	  	pricestandard = oRequest.price
	  	pricestatus = 2
		} else {
	  	pricestandard = 0
	  	pricestatus = 0
		}
		csiSetPrice_rub()
		//<<set price
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def csiGetHome(iClientId,iModstatusId,iMax,iOffset) {
    def hsSql=[select:'',from:'',where:'',order:'']    
    def hsLong=[:]
    
    hsSql.select="*"
    hsSql.from="home"
    hsSql.where="modstatus>=-2"+
	  ((iClientId>0)?" and client_id=:iClientId":"")+	    
      ((iModstatusId==1)?" and modstatus=1":((iModstatusId>1)?" and modstatus!=1":""))        	
    hsSql.order="inputdate DESC"
	
    if(iClientId>0)
      hsLong['iClientId']=iClientId	
    return searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'id',true, Home.class,null)	  
  }

  def csiGetPartnerHome() {
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*"
    hsSql.from="home join client on (client.id=home.client_id)"
    hsSql.where="home.modstatus=1 and home.region_id in (77,78,42,24,66,54,23,2,257,52,56,64,16,117,115,165) and client.partnerstatus=2"
    hsSql.order="home.id asc"

    searchService.fetchData(hsSql,null,null,null,null,Home.class)
  }
//owner Dmitry>>
  def csiGetUniqueLinkname(sName,lId=0){
		def tempResult = Tools.transliterate(sName)
		int i = 0
		if (tempResult.matches("[0-9-]+"))
			tempResult = ((Holders.config.linkname.prefix)?Holders.config.linkname.prefix:"arenda_")+tempResult
		def result = tempResult

		while (Home.findByLinknameAndIdNotEqual(result,lId)){
			i++
			result = tempResult + '_' + i
		}

		return result
  }

  static def calculateHomePrice (hsRes, lId, bUseModifier=true){
		def date_start
		def date_end

		if(hsRes.inrequest?.date_start?:'')
			date_start=Date.parse('yyyy-MM-dd', hsRes.inrequest?.date_start)
		if(hsRes.inrequest?.date_end?:'')
			date_end=Date.parse('yyyy-MM-dd', hsRes.inrequest?.date_end)
		def start = Calendar.getInstance()
		start.setTime(date_start)
		def end = Calendar.getInstance()
		end.setTime(date_end)

		hsRes.home = Home.get(lId)
		if (!hsRes.home) {
			hsRes.error = true
			hsRes.errorprop = "home.calculateHomePrice.unbelievable.errorprop"
			return hsRes
		}
		if (hsRes.home.is_pricebyday)
			end.add(Calendar.DATE,1)
		if(start.getTime()>=end.getTime()) {
			hsRes.error = true
			hsRes.errorprop = "home.calculateHomePrice.badDate_end.errorprop"
			return hsRes
		}
		def date1 = Calendar.getInstance()
		date1.set(Calendar.HOUR_OF_DAY ,0)
		date1.set(Calendar.MINUTE ,0)
		date1.set(Calendar.SECOND,0)
		date1.set(Calendar.MILLISECOND,0)
		if(start.getTime()<date1.getTime()) {
			hsRes.error = true
			hsRes.errorprop = "home.calculateHomePrice.badDate_start.errorprop"
			return hsRes
		}
		def maxperson = Homeperson.get(hsRes.home.homeperson_id?:1)
		def cancellation = Rule_cancellation.get(hsRes.home.rule_cancellation_id?:1)
		def minday = Rule_minday.get(hsRes.home.rule_minday_id?:1)
		def maxday = Rule_maxday.get(hsRes.home.rule_maxday_id?:1)
		def timediap = (end.getTimeInMillis()-start.getTimeInMillis())/(1000*60*60*24)
		if (timediap < minday.nday) {
			hsRes.error = true
			hsRes.errorprop = "home.calculateHomePrice.minday.errorprop"
			hsRes.errorArgs = [minday.nday]
			return hsRes
		}
		if (maxday.nday) {
			if (timediap > maxday.nday) {
				hsRes.error = true
				hsRes.errorprop = "home.calculateHomePrice.maxday.errorprop"
				hsRes.errorArgs = [maxday.nday]
				return hsRes
			}
		}
		if (hsRes.inrequest?.homeperson_id > maxperson.kol) {
			hsRes.error = true
			hsRes.errorprop = "home.calculateHomePrice.maxperson.errorprop"
			hsRes.errorArgs = [maxperson.kol]
			return hsRes
		}
		def feePrice = 0
		if (Homeperson.get(hsRes.inrequest?.homeperson_id).kol > Homeperson.get(hsRes.home.fee_homeperson).kol){
			feePrice = hsRes.home.fee_rub * (Homeperson.get(hsRes.inrequest?.homeperson_id).kol - Homeperson.get(hsRes.home.fee_homeperson).kol)
		}
		def tmpHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<=:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND modstatus=1 AND term<2 ORDER BY date_start",[date_start_query:start.getTime(),date_end_query:end.getTime(),home_id:lId])
		def busyHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND (modstatus=3 OR modstatus=5) ORDER BY date_start",[date_start_query:start.getTime(),date_end_query:end.getTime(),home_id:lId])
		if (busyHomeprop.size()>0) {
			hsRes.error = true
			hsRes.errorprop = "home.calculateHomePrice.unavailable.errorprop"
			return hsRes
		}
		def month_counter = 0
		def week_counter = 0
		hsRes.result = 0
		def discountModifier = hsRes.home.csiGetDiscountModifier(hsRes.home,start,end,date1)
		def resstatModifier = 1.0
		if (Client.get(hsRes.home.client_id)?.resstatus==1&&bUseModifier) {
			resstatModifier = resstatModifier + (Tools.getIntVal(Holders.config.clientPrice.modifier,4) / 100)
		}
		if (!tmpHomeprop) {
			if (hsRes.home.pricestatus==1) {
				if (hsRes.home.pricemonth_rub) {
					while (timediap>=30) {
						timediap -= 30
						hsRes.result += hsRes.home.pricemonth_rub * discountModifier * resstatModifier
						hsRes.result += feePrice * 30 * discountModifier * resstatModifier
						month_counter++
					}
				}
				if (hsRes.home.priceweek_rub) {
					while (timediap>=7) {
						timediap -= 7
						hsRes.result += hsRes.home.priceweek_rub * discountModifier * resstatModifier
						hsRes.result += feePrice * 7 * discountModifier * resstatModifier
						week_counter++
					}
				}
				if (hsRes.home.priceweekend_rub) {
					start.set(Calendar.DATE, (start.get(Calendar.DATE)+(30 * month_counter)))
					start.set(Calendar.DATE, (start.get(Calendar.DATE)+(7 * week_counter)))
					while (start.getTime() < end.getTime()) {
						if (start.get(Calendar.DAY_OF_WEEK)==1
						  || start.get(Calendar.DAY_OF_WEEK)==7){
							hsRes.result += hsRes.home.priceweekend_rub * discountModifier * resstatModifier
							hsRes.result += feePrice * discountModifier * resstatModifier
						} else {
							hsRes.result += hsRes.home.pricestandard_rub * discountModifier * resstatModifier
							hsRes.result += feePrice * discountModifier * resstatModifier
						}
						start.set(Calendar.DATE, (start.get(Calendar.DATE)+1))
					}
				} else {
					hsRes.result += timediap * hsRes.home.pricestandard_rub * discountModifier * resstatModifier
					hsRes.result += timediap * feePrice * discountModifier * resstatModifier
				}
				return hsRes
			} else {
				hsRes.error = true
				hsRes.errorprop = "home.calculateHomePrice.unavailable.errorprop"
				return hsRes
			}
		}
		for (hmP in tmpHomeprop ) {
			if (!hmP.term){
				hsRes.error = true
				hsRes.errorprop = "home.calculateHomePrice.unavailable.errorprop"
				return hsRes
			}
		}
		def hmP_counter = 0
		def tempTimediap = 0
		while (start.getTime() < end.getTime()) {
			if( (hmP_counter < tmpHomeprop.size()) && start.getTime() >= tmpHomeprop[hmP_counter].date_start) {
				if( end.getTime() < tmpHomeprop[hmP_counter].date_end ) {
					tempTimediap = ( end.getTimeInMillis() - start.getTimeInMillis() ) / (1000*60*60*24)
					if (tmpHomeprop[hmP_counter].priceweekend_rub) {
						while (start.getTime() < end.getTime()) {
							if (start.get(Calendar.DAY_OF_WEEK)==1||start.get(Calendar.DAY_OF_WEEK)==7){
								hsRes.result += tmpHomeprop[hmP_counter].priceweekend_rub * resstatModifier
								hsRes.result += feePrice * resstatModifier
							} else {
								hsRes.result += tmpHomeprop[hmP_counter].price_rub * resstatModifier
								hsRes.result += feePrice * resstatModifier
							}
							start.set(Calendar.DATE, (start.get(Calendar.DATE)+1))
						}
					} else {
						hsRes.result += tempTimediap*tmpHomeprop[hmP_counter].price_rub * resstatModifier
						hsRes.result += tempTimediap*feePrice * resstatModifier
					}
					start.setTime(end.getTime())
				} else if (end.getTime() > tmpHomeprop[hmP_counter].date_end) {
					tempTimediap = ( tmpHomeprop[hmP_counter].date_end.getTime() - start.getTimeInMillis() ) / (1000*60*60*24) + 1
					if (tmpHomeprop[hmP_counter].priceweekend_rub) {
						while (start.getTime() <= tmpHomeprop[hmP_counter].date_end) {
							if (start.get(Calendar.DAY_OF_WEEK)==1||start.get(Calendar.DAY_OF_WEEK)==7){
								hsRes.result += tmpHomeprop[hmP_counter].priceweekend_rub * resstatModifier
								hsRes.result += feePrice * resstatModifier
							} else {
								hsRes.result += tmpHomeprop[hmP_counter].price_rub * resstatModifier
								hsRes.result += feePrice * resstatModifier
							}
							start.set(Calendar.DATE, (start.get(Calendar.DATE)+1))
						}
					} else {
						hsRes.result += tempTimediap*tmpHomeprop[hmP_counter].price_rub * resstatModifier
						hsRes.result += tempTimediap*feePrice * resstatModifier
					}
					start.setTime(tmpHomeprop[hmP_counter].date_end)
				} else {
					tempTimediap = ( tmpHomeprop[hmP_counter].date_end.getTime() - start.getTimeInMillis() ) / (1000*60*60*24)
					if (tmpHomeprop[hmP_counter].priceweekend_rub) {
						while (start.getTime() < tmpHomeprop[hmP_counter].date_end) {
							if (start.get(Calendar.DAY_OF_WEEK)==1||start.get(Calendar.DAY_OF_WEEK)==7){
								hsRes.result += tmpHomeprop[hmP_counter].priceweekend_rub * resstatModifier
								hsRes.result += feePrice * resstatModifier
							} else {
								hsRes.result += tmpHomeprop[hmP_counter].price_rub * resstatModifier
								hsRes.result += feePrice * resstatModifier
							}
							start.set(Calendar.DATE, (start.get(Calendar.DATE)+1))
						}
					} else {
						hsRes.result += tempTimediap*tmpHomeprop[hmP_counter].price_rub * resstatModifier
						hsRes.result += tempTimediap*feePrice * resstatModifier
					}
					start.setTime(tmpHomeprop[hmP_counter].date_end)
				}
				hmP_counter++
			} else {
				if (hsRes.home.pricestatus!=1) {
					hsRes.error = true
					hsRes.errorprop = "home.calculateHomePrice.unavailable.errorprop"
					return hsRes
				}
				if (hmP_counter >= tmpHomeprop.size()) {
					tempTimediap = ( end.getTimeInMillis() - start.getTimeInMillis() ) / (1000*60*60*24)
					if (hsRes.home.pricemonth_rub) {
						month_counter = 0
						while (tempTimediap>=30) {
							tempTimediap -= 30
							hsRes.result += hsRes.home.pricemonth_rub * discountModifier * resstatModifier
							hsRes.result += feePrice * 30 * discountModifier * resstatModifier
							month_counter++
						}
					}
					if (hsRes.home.priceweek_rub) {
						week_counter = 0
						while (tempTimediap>=7) {
							tempTimediap -= 7
							hsRes.result += hsRes.home.priceweek_rub * discountModifier * resstatModifier
							hsRes.result += feePrice * 7 * discountModifier * resstatModifier
							week_counter++
						}
					}
					if (hsRes.home.priceweekend_rub) {
						start.set(Calendar.DATE, (start.get(Calendar.DATE)+(30 * month_counter)))
						start.set(Calendar.DATE, (start.get(Calendar.DATE)+(7 * week_counter)))
						while (start.getTime() < end.getTime()) {
							if (start.get(Calendar.DAY_OF_WEEK)==1||start.get(Calendar.DAY_OF_WEEK)==7){
								hsRes.result += hsRes.home.priceweekend_rub * discountModifier * resstatModifier
								hsRes.result += feePrice * discountModifier * resstatModifier
							} else {
								hsRes.result += hsRes.home.pricestandard_rub * discountModifier * resstatModifier
								hsRes.result += feePrice * discountModifier * resstatModifier
							}
							start.set(Calendar.DATE, (start.get(Calendar.DATE)+1))
						}
					} else {
						hsRes.result += tempTimediap*hsRes.home.pricestandard_rub * discountModifier * resstatModifier
						hsRes.result += tempTimediap*feePrice * discountModifier * resstatModifier
					}
					start.setTime(end.getTime())
				} else {
					tempTimediap = ( tmpHomeprop[hmP_counter].date_start.getTime() - start.getTimeInMillis() ) / (1000*60*60*24)
					if (hsRes.home.pricemonth_rub) {//TODO:>??? Запихать в отдельную функцию. 6 обязательных параметров.
						month_counter = 0
						while (tempTimediap>=30) {
							tempTimediap -= 30
							hsRes.result += hsRes.home.pricemonth_rub * discountModifier * resstatModifier
							hsRes.result += feePrice * 30 * discountModifier * resstatModifier
							month_counter++
						}
					}
					if (hsRes.home.priceweek_rub) {
						week_counter = 0
						while (tempTimediap>=7) {
							tempTimediap -= 7
							hsRes.result += hsRes.home.priceweek_rub * discountModifier * resstatModifier
							hsRes.result += feePrice * 7 * discountModifier * resstatModifier
							week_counter++
						}
					}
					if (hsRes.home.priceweekend_rub) {
						start.set(Calendar.DATE, (start.get(Calendar.DATE)+(30 * month_counter)))
						start.set(Calendar.DATE, (start.get(Calendar.DATE)+(7 * week_counter)))
						while (start.getTime() < tmpHomeprop[hmP_counter].date_start) {
							if (start.get(Calendar.DAY_OF_WEEK)==1||start.get(Calendar.DAY_OF_WEEK)==7){
								hsRes.result += hsRes.home.priceweekend_rub * discountModifier * resstatModifier
								hsRes.result += feePrice * discountModifier * resstatModifier
							} else {
								hsRes.result += hsRes.home.pricestandard_rub * discountModifier * resstatModifier
								hsRes.result += feePrice * discountModifier * resstatModifier
							}
							start.set(Calendar.DATE, (start.get(Calendar.DATE)+1))
						}
					} else {
						hsRes.result += tempTimediap*hsRes.home.pricestandard_rub * discountModifier * resstatModifier
						hsRes.result += tempTimediap*feePrice * discountModifier * resstatModifier
					}
					start.setTime(tmpHomeprop[hmP_counter].date_start)
					start.set(Calendar.DATE, (start.get(Calendar.DATE)-1))
				}
			}
			start.set(Calendar.DATE, (start.get(Calendar.DATE)+1))
		}
		return hsRes
  }

	def csiGetDiscountModifier(oHome,_start,_end,_today=Calendar.getInstance()) {
		def modifier = 1.0
		if (isHaveDiscountAdv(oHome)) {
			def hsDiscounts = csiGetHomeDiscounts()
			if(hsDiscounts.long?.modstatus
					&&((_end.getTimeInMillis()-_start.getTimeInMillis())/(1000*60*60*24))>hsDiscounts.long.minrentdays
					&&((_start.getTimeInMillis()-_today.getTimeInMillis())/(1000*60*60*24))>hsDiscounts.long.discexpiredays) {
				modifier = modifier*hsDiscounts.long.discount/100
			} else if (hsDiscounts.hot?.modstatus
								&&((_end.getTimeInMillis()-_start.getTimeInMillis())/(1000*60*60*24))>hsDiscounts.hot.minrentdays
								&&((_start.getTimeInMillis()-_today.getTimeInMillis())/(1000*60*60*24))<hsDiscounts.hot.discexpiredays) {
				modifier = modifier*hsDiscounts.hot.discount/100
			}
		}
		return modifier
	}

	Integer csiGetDisplayDiscount(Date _start,Date _end,_today=Calendar.getInstance()) {
    def discount = 0
		if (isHaveDiscountAdv(this)) {
			def hsDiscounts = csiGetHomeDiscounts()
    	if (_start){
				def oHmp = Homeprop.find("from Homeprop where date_end>=:date_start_query AND home_id=:home_id AND modstatus=1 AND term>0",[date_start_query:_start,home_id:id],[sort:'date_start'])
      	if(hsDiscounts.long?.modstatus
        	&& oHmp?.term==2
        	&&(((_end?.getTime()?:Long.MAX_VALUE)-(_start?.getTime()?:0))/(1000*60*60*24))>hsDiscounts.long.minrentdays
        	&&(((_start?.getTime()?:0)-_today.getTimeInMillis())/(1000*60*60*24))>hsDiscounts.long.discexpiredays) {
        	discount = 1
      	} else if (hsDiscounts.hot?.modstatus
                && oHmp?.term==2
                &&(((_end?.getTime()?:Long.MAX_VALUE)-(_start?.getTime()?:0))/(1000*60*60*24))>hsDiscounts.hot.minrentdays
                &&(((_start?.getTime()?:0)-_today.getTimeInMillis())/(1000*60*60*24))<hsDiscounts.hot.discexpiredays) {
        	discount = 2
      	} else {
        	discount = 3
      	}
    	} else {
      	discount = hsDiscounts.hot?.modstatus?2:hsDiscounts.long?.modstatus?3:0
    	}
    }
    return discount
	}

	Boolean isHaveDiscountAdv(oHome){
		if (!oHome||!(oHome.longdiscount_id||oHome.hotdiscount_id)||!(Homediscount.get(oHome.longdiscount_id)?.modstatus||Homediscount.get(oHome.hotdiscount_id)?.modstatus)) {
			return false
		}
		return true
	}

	def csiGetHomeDiscounts(){
		return [long:Homediscount.get(longdiscount_id),hot:Homediscount.get(hotdiscount_id)]
	}

	def eventHome(hsRes){
		def dateStart=new Date((hsRes.inrequest.start-(hsRes.inrequest.tz+180)*60)*1000)
		def dateEnd=new Date((hsRes.inrequest.end-(hsRes.inrequest.tz+180)*60)*1000)
		def tmpDate= new GregorianCalendar()
		tmpDate.setTime(dateStart)
		//tmpDate.add(Calendar.HOUR_OF_DAY,1)
		tmpDate.set(Calendar.HOUR_OF_DAY ,0)
		def today = new Date()
		def date1= new GregorianCalendar()
		date1.setTimeInMillis(today.getTime()-(hsRes.inrequest.tz+180)*1000*60)
		date1.set(Calendar.HOUR_OF_DAY ,0)
		date1.set(Calendar.MINUTE ,0)
		date1.set(Calendar.SECOND,0)
		date1.set(Calendar.MILLISECOND,0)
		def endDate= new GregorianCalendar()
		endDate.setTime(dateEnd)
		//endDate.add(Calendar.HOUR_OF_DAY,1)
		endDate.set(Calendar.HOUR_OF_DAY ,0)
		def event = []
		int i=0;
		while (tmpDate.getTime()<=date1.getTime()&&tmpDate.getTime()<=endDate.getTime()) {
			def events = [:]
			events.title=''
			events.start=tmpDate.getTime()
			events.end=tmpDate.getTime()
			events.className='old'
			events.dayNum = i-1
			event[i]=events
			tmpDate.add(Calendar.DATE,1)
			i++
		}
		if(event.size()>0)
			tmpDate.add(Calendar.DATE,-1)
		def tmpHomeprop = new Homeprop()
		tmpHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<=:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND modstatus=1 ORDER BY date_start",[date_start_query:tmpDate.getTime(),date_end_query:endDate.getTime(),home_id:hsRes.home.id])
		def busyHomeprop = new Homeprop()
		busyHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<=:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id AND (modstatus=3 OR modstatus=5) ORDER BY date_start",[date_start_query:tmpDate.getTime(),date_end_query:endDate.getTime(),home_id:hsRes.home.id])
		if(event.size()>0)
			tmpDate.add(Calendar.DATE,1)
		def oValutarate = new Valutarate()
		hsRes.valutaRates = oValutarate.csiGetRate(hsRes.context.shown_valuta.id)
		def resstatModifier = 1.0
		if (Client.get(hsRes.home.client_id)?.resstatus==1) {
			resstatModifier = resstatModifier + (Tools.getIntVal(Holders.config.clientPrice.modifier,4) / 100)
		}
		if(!tmpHomeprop){
			if (hsRes.home.pricestatus==1) {
				if (hsRes.home.priceweekend_rub) {
					while (tmpDate.getTime()<=endDate.getTime()) {
						def events = [:]
						if (tmpDate.get(Calendar.DAY_OF_WEEK)==1||tmpDate.get(Calendar.DAY_OF_WEEK)==2){
							events.title=(Math.rint(100.0 * (hsRes.home.priceweekend_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
						} else
							events.title=(Math.rint(100.0 * (hsRes.home.pricestandard_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
						events.start=tmpDate.getTime()
						events.end=tmpDate.getTime()
						events.className='active'
						events.dayNum = i-1
						event[i]=events
						tmpDate.set(Calendar.DATE, (tmpDate.get(Calendar.DATE)+1))
						i++
					}
				} else {
					while (tmpDate.getTime()<=endDate.getTime()) {
						def events = [:]
						events.title=(Math.rint(100.0 * (hsRes.home.pricestandard_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
						events.start=tmpDate.getTime()
						events.end=tmpDate.getTime()
						events.className='active'
						events.dayNum = i-1
						event[i]=events
						tmpDate.set(Calendar.DATE, (tmpDate.get(Calendar.DATE)+1))
						i++
					}
				}
			} else {
				while (tmpDate.getTime()<=endDate.getTime()) {
					def events = [:]
					events.title=''
					events.start=tmpDate.getTime()
					events.end=tmpDate.getTime()
					events.className='inactive'
					events.dayNum = i-1
					event[i]=events
					tmpDate.set(Calendar.DATE, (tmpDate.get(Calendar.DATE)+1))
					i++
				}
			}
		} else {
			def hmP_counter = 0
			while (tmpDate.getTime()<=endDate.getTime()) {
				if( (hmP_counter < tmpHomeprop.size()) && tmpDate.getTime() > tmpHomeprop[hmP_counter].date_start) {
					if (tmpHomeprop[hmP_counter].term==0) {
						while( tmpDate.getTime() <= tmpHomeprop[hmP_counter].date_end&&tmpDate.getTime()<=endDate.getTime() ) {
							def events = [:]
							events.title=''
							events.start=tmpDate.getTime()
							events.end=tmpDate.getTime()
							events.className='inactive'
							events.dayNum = i-1
							event[i]=events
							tmpDate.set(Calendar.DATE, (tmpDate.get(Calendar.DATE)+1))
							i++
						}
						def events = [:]
						events.title=''
						events.start=tmpDate.getTime()
						events.end=tmpDate.getTime()
						events.className='inactive'
						events.dayNum = i-1
						event[i]=events
						i++
					} else if (tmpHomeprop[hmP_counter].priceweekend_rub) {
						while( tmpDate.getTime() <= tmpHomeprop[hmP_counter].date_end&&tmpDate.getTime()<=endDate.getTime() ) {
							def events = [:]
							if (tmpDate.get(Calendar.DAY_OF_WEEK)==1||tmpDate.get(Calendar.DAY_OF_WEEK)==2){
								events.title=(Math.rint(100.0 * (tmpHomeprop[hmP_counter].priceweekend_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
							} else
								events.title=(Math.rint(100.0 * (tmpHomeprop[hmP_counter].price_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
							events.start=tmpDate.getTime()
							events.end=tmpDate.getTime()
							events.className='active'
							events.dayNum = i-1
							event[i]=events
							tmpDate.set(Calendar.DATE, (tmpDate.get(Calendar.DATE)+1))
							i++
						}
						def events = [:]
						if (tmpDate.get(Calendar.DAY_OF_WEEK)==1||tmpDate.get(Calendar.DAY_OF_WEEK)==2){
							events.title=(Math.rint(100.0 * (tmpHomeprop[hmP_counter].priceweekend_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
						} else
							events.title=(Math.rint(100.0 * (tmpHomeprop[hmP_counter].price_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
						events.start=tmpDate.getTime()
						events.end=tmpDate.getTime()
						events.className='active'
						events.dayNum = i-1
						event[i]=events
						i++
					} else {
						while( tmpDate.getTime() <= tmpHomeprop[hmP_counter].date_end&&tmpDate.getTime()<=endDate.getTime() ) {
							def events = [:]
							events.title=(Math.rint(100.0 * (tmpHomeprop[hmP_counter].price_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
							events.start=tmpDate.getTime()
							events.end=tmpDate.getTime()
							events.className='active'
							events.dayNum = i-1
							event[i]=events
							tmpDate.set(Calendar.DATE, (tmpDate.get(Calendar.DATE)+1))
							i++
						}
						def events = [:]
						events.title=(Math.rint(100.0 * (tmpHomeprop[hmP_counter].price_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
						events.start=tmpDate.getTime()
						events.end=tmpDate.getTime()
						events.className='active'
						events.dayNum = i-1
						event[i]=events
						i++
					}
					hmP_counter++
				} else {
					if (hsRes.home.pricestatus!=1) {
						def events = [:]
						events.title=''
						events.start=tmpDate.getTime()
						events.end=tmpDate.getTime()
						events.className='inactive'
						events.dayNum = i-1
						event[i]=events
						i++
					} else {
						if (hsRes.home.priceweekend_rub) {
							def events = [:]
							if (tmpDate.get(Calendar.DAY_OF_WEEK)==1||tmpDate.get(Calendar.DAY_OF_WEEK)==2){
								events.title=(Math.rint(100.0 * (hsRes.home.priceweekend_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
							} else
								events.title=(Math.rint(100.0 * (hsRes.home.pricestandard_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
							events.start=tmpDate.getTime()
							events.end=tmpDate.getTime()
							events.className='active'
							events.dayNum = i-1
							event[i]=events
							i++
						} else {
							def events = [:]
							events.title=(Math.rint(100.0 * (hsRes.home.pricestandard_rub * resstatModifier / hsRes.valutaRates)) / 100.0).toString()
							events.start=tmpDate.getTime()
							events.end=tmpDate.getTime()
							events.className='active'
							events.dayNum = i-1
							event[i]=events
							i++
						}
					}
				}
				tmpDate.set(Calendar.DATE, (tmpDate.get(Calendar.DATE)+1))
			}
		}
		if (busyHomeprop.size()>0){
			def hmP_counter = 0
			for (ev in event) {
				if( (hmP_counter < busyHomeprop.size()) && ev.start > busyHomeprop[hmP_counter].date_start && ev.start >= date1.getTime()) {
					ev.title=''
					if (busyHomeprop[hmP_counter].modstatus==3)
						ev.className='notavailable'
					else {
						ev.className='reserved'
						ev.mbox_id=Mbox.findAll('FROM Mbox WHERE home_id=:id AND (date_start<=:date and date_end>=:date)',[id:busyHomeprop[hmP_counter].home_id,date:ev.start])[0]?.id?:0
					}
					ev.description=busyHomeprop[hmP_counter].remark?:''
					if (ev.start > busyHomeprop[hmP_counter].date_end)
						hmP_counter++
				}
			}
		}
		return event
  }

  void publish(){
	def oNote = null
	if (this.modstatus==3){
		this.modstatus = 1
		oNote = Note.findByHome_idAndNotetype_id(this.id,2)
	} else if (this.modstatus ==-1){
		this.modstatus = 4
		this.is_confirmed = 0
		oNote = Note.findByHome_idAndNotetype_id(this.id,2)
	} else if (this.modstatus ==1)
		this.modstatus = 3
	else if (this.modstatus ==4)
		this.modstatus = -1
	this.moddate = new Date()
	if(!this.save(flush:true)) {
		log.debug(" Error on save home:")
		this.errors.each{log.debug(it)}
	}
	if (oNote){
		oNote.modstatus=0
		if(!oNote.save(flush:true)) {
		  log.debug(" Error on save Note(home.publish()) id:"+oNote.id)
		  oNote.errors.each { log.debug(it)}
		}
	}
	def badHomeprop = Homeprop.findAll("FROM Homeprop WHERE home_id=:home_id AND modstatus=2",[home_id:this.id])
	for(hmp in badHomeprop){
		hmp.delete(flush:true)
	}
	return
  }

  String csiGetWhere(sLang=''){
	return city?:district?:region_id?Region.get(region_id)['name'+sLang]:''
  }

  void removeRegular() {
		def regHomeprop = Homeprop.findAll('FROM Homeprop WHERE home_id=:id AND term=2 AND modstatus=1',[id:this.id])
		for(hmp in regHomeprop){
			hmp.delete(flush:true)
		}
		if (longdiscount_id||hotdiscount_id)
			try {
				Homediscount.dropStatus(this)
			} catch(Exception e) {
				log.debug("Cannot update Discount \n"+e.toString()+' in Homediscount/dropStatus')
			}
  }

  void addRegular() {
		def regHomeprop = Homeprop.findAll('FROM Homeprop WHERE home_id=:id AND term=2 AND modstatus=1',[id:this.id])
		if (regHomeprop.size()>0) {//change hmp`s>>
			for(hmp in regHomeprop){
				hmp.price = this.pricestandard
				hmp.price_rub = this.pricestandard_rub
				hmp.priceweekend = this.priceweekend
				hmp.priceweekend_rub = this.priceweekend_rub
				hmp.priceweek = this.priceweek
				hmp.priceweek_rub = this.priceweek_rub
				hmp.pricemonth = this.pricemonth
				hmp.pricemonth_rub = this.pricemonth_rub
				hmp.modstatus = 1
				hmp.save(flush:true)
			}
		//<<change hmp`s
		} else {//add hmp`s>>
			def date=new Date()
			def date_start = new GregorianCalendar()
			date_start.setTime(date)
			date_start.set(Calendar.HOUR_OF_DAY ,0)
			date_start.set(Calendar.MINUTE ,0)
			date_start.set(Calendar.SECOND,0)
			date_start.set(Calendar.MILLISECOND,0)
			def date_end = new GregorianCalendar()
			date_end.setTime(date)
			date_end.set(Calendar.HOUR_OF_DAY ,0)
			date_end.set(Calendar.MINUTE ,0)
			date_end.set(Calendar.SECOND,0)
			date_end.set(Calendar.MILLISECOND,0)
			date_end.add(Calendar.YEAR,2)
			def tmpHomeprop = Homeprop.findAll("FROM Homeprop WHERE (date_start<=:date_end_query AND date_end>=:date_start_query) AND home_id=:home_id ORDER BY date_start",[date_start_query:date_start.getTime(),date_end_query:date_end.getTime(),home_id:this.id])
			if (!tmpHomeprop){
				createHmp(date_start.getTime(),date_end.getTime())
			} else {
				for(hmp in tmpHomeprop){
					if(hmp.modstatus==2 || hmp.modstatus==0) hmp.delete(flush:true)
					else {
						if ( hmp.date_start <= date_start.getTime() ){
							date_start.setTime(hmp.date_end)
							date_start.add(Calendar.DATE,1)
						} else {
							def tempDate = new GregorianCalendar()
							tempDate.setTime(hmp.date_start)
							tempDate.add(Calendar.DATE,-1)
							createHmp(date_start.getTime(),tempDate.getTime())
							date_start.setTime(hmp.date_end)
							date_start.add(Calendar.DATE,1)
						}
					}
				}
				if (date_start.getTime() < date_end.getTime())//last period
					createHmp(date_start.getTime(),date_end.getTime())
			}
		}//<<add hmp`s
		//update saved hmp`s>>
		def regSavedHomeprop = Homeprop.findAll('FROM Homeprop WHERE home_id=:id AND term=2 AND modstatus=4',[id:this.id])
		if (regSavedHomeprop.size()>0) {//change hmp`s>>
			for(hmp in regSavedHomeprop){
				hmp.price = this.pricestandard
				hmp.price_rub = this.pricestandard_rub
				hmp.priceweekend = this.priceweekend
				hmp.priceweekend_rub = this.priceweekend_rub
				hmp.priceweek = this.priceweek
				hmp.priceweek_rub = this.priceweek_rub
				hmp.pricemonth = this.pricemonth
				hmp.pricemonth_rub = this.pricemonth_rub
				hmp.modstatus = 4
				hmp.save(flush:true)
			}
		}//<<update saved hmp`s
  }
  private void createHmp(d_st, d_end) {
		def newHmp = new Homeprop()
		newHmp.home_id = this.id
		newHmp.valuta_id = this.valuta_id
		newHmp.modstatus = 1
		newHmp.term = 2
		newHmp.remark = ''
		newHmp.price = this.pricestandard
		newHmp.price_rub = this.pricestandard_rub
		newHmp.priceweekend = this.priceweekend
		newHmp.priceweekend_rub = this.priceweekend_rub
		newHmp.priceweek = this.priceweek
		newHmp.priceweek_rub = this.priceweek_rub
		newHmp.pricemonth = this.pricemonth
		newHmp.pricemonth_rub = this.pricemonth_rub
		newHmp.date_start = d_st
		newHmp.date_end = d_end
		newHmp.save(flush:true)
  }

  def csiGetHomeWithoutPrice() {
    def hsSql = [select :'*',
                 from   :'home',
                 where  :"""modstatus=1 and 0=ifnull((select ifnull(price_rub,0) from homeprop where home_id=home.ID 
						 and modstatus=1 and term>0 and date_end>=current_date order by date_start asc limit 1),0)""",
                 order  :'id']

    return searchService.fetchData(hsSql,null,null,null,null,Home.class)
  }

  void csiSetPrice_rub() {
	if(this.valuta_id==857){
	  this.pricestandard_rub=this.pricestandard
	  this.priceweekend_rub=this.priceweekend?:0
	  this.priceweek_rub=this.priceweek?:0
	  this.pricemonth_rub=this.pricemonth?:0
	} else {
      def oValutarate = new Valutarate()
      def valutaRates = oValutarate.csiGetRate(this.valuta_id)
	  this.pricestandard_rub = Math.rint((this.pricestandard?:0) * valutaRates)
	  this.priceweekend_rub = Math.rint((this.priceweekend?:0) * valutaRates)
	  this.priceweek_rub = Math.rint((this.priceweek?:0) * valutaRates)
	  this.pricemonth_rub = Math.rint((this.pricemonth?:0) * valutaRates)
	}
  }
  void csiSetExtrasPrice_rub() {
	if(this.valuta_id==857){
	  this.deposit_rub=this.deposit?:0
	  this.fee_rub=this.fee?:0
	  this.cleanup_rub=this.cleanup?:0
	} else {
      def oValutarate = new Valutarate()
      def valutaRates = oValutarate.csiGetRate(this.valuta_id)
	  this.deposit_rub = Math.rint((this.deposit?:0) * valutaRates)
	  this.fee_rub = Math.rint((this.fee?:0) * valutaRates)
	  this.cleanup_rub = Math.rint((this.cleanup?:0) * valutaRates)
	}
  }
  def csiGetHomeUpdatePrice() {
    def oHome = Home.findAll('FROM Home WHERE valuta_id!=857')
	return oHome
  }

  Boolean addOwner(lId) {
	def oUser
	def oClient = Client.get(this.client_id)
	if (!oClient) return false
	def oBaseUser = User.get(lId)
	if (!oBaseUser) return false
	if (oBaseUser.provider!='staytoday')
	  if(!oBaseUser.ref_id){
		if (!oBaseUser.email) return false
		def iRefId=oBaseUser.csiInsertInternal([email:oBaseUser.email?:oClient.name,password:Tools.hidePsw(''),firstname:'',lastname:'',nickname:'',client_id:0,code:''])
		oBaseUser.ref_id=iRefId
		oBaseUser.code=''
		oBaseUser.save(flush:true)
		oUser=User.get(iRefId)
		oUser.nickname = oBaseUser.nickname
		oUser.description = oBaseUser.description
		oUser.banned = oBaseUser.banned
		oUser.is_external = oBaseUser.is_external
		oUser.tel = oBaseUser.tel
		oUser.tel1 = oBaseUser.tel1
		oUser.companylist = oBaseUser.companylist
		oUser.inputdate = oBaseUser.inputdate
		oUser.lastdate = oBaseUser.lastdate
		oUser.ncomment = oBaseUser.ncomment
		oUser.picture = oBaseUser.picture
		oUser.smallpicture = oBaseUser.smallpicture
		oUser.modstatus = oBaseUser.modstatus
	  } else {
		oUser = User.get(oBaseUser.ref_id)
	  }
	else
	  oUser = oBaseUser
	if (!oUser) return false
	if (oUser.client_id){
	  this.client_id = oUser.client_id
	  this.modstatus = 2
	} else {
	  oUser.client_id = this.client_id
	  this.modstatus = 2
	  oClient.modstatus = oBaseUser.modstatus
	  oClient.name = oUser.email
	  if(!oClient.save(flush:true)) {
		log.debug(" Error on save client(home.addOwner):")
		oClient.errors.each{log.debug(it)}
		return false
	  }
	}

	def oHmps = Homeprop.findAllByHome_id(this.id)
	for (hmp in oHmps){
	  hmp.modstatus = 2
	  hmp.save(flush:true)
	}
	oUser.save(flush:true)
	this.save(flush:true)
	return true
  }

  def calculateHomeRating() {
  	def result = ratingService.calculateHomeRating(id,extrarating,ratingpenalty,infraoption,client_id)
  	rating = result.rating
  	return result
  }

  static def findHomeForSpecoffer(){
  	return Home.findAllByModstatus(1,[max:10,sort:'rating',order:'desc'])
  }

  def csiGetSomePrice(){
    def tmpPrice = Homeprop.findAll("FROM Homeprop WHERE date_end>=:date_start_query AND home_id=:home_id AND modstatus=1 AND term>0 ORDER BY date_start",[date_start_query:new Date().clearTime(),home_id:id])
		def resstatModifier = 1.0
		if (Client.get(client_id)?.resstatus==1) {
			resstatModifier = resstatModifier + (Tools.getIntVal(Holders.config.clientPrice.modifier,4) / 100)
		}
    if(tmpPrice.size()>0)
    	return Math.rint(tmpPrice[0].price*resstatModifier)
    else
    	return 0
  }
//owner Dmitry<<
  Home csiSetEnHome(){
    def oHomeTmp=new Home()
    this.properties.each { prop, val ->
      if(["metaClass","class"].find {it == prop}) return
      if(oHomeTmp.hasProperty(prop)) oHomeTmp[prop] = val
    }    
    oHomeTmp.id=this.id    
    oHomeTmp.city=City.get(oHomeTmp.city_id)?.name_en?:Tools.transliterate(oHomeTmp.city,0) 
    oHomeTmp.name=Tools.transliterate(oHomeTmp.name,0)
    oHomeTmp.shortaddress=Tools.transliterate(oHomeTmp.shortaddress,0)
    oHomeTmp.address=Tools.transliterate(oHomeTmp.address,0)
    oHomeTmp.street=Tools.transliterate(oHomeTmp.street,0)
    oHomeTmp.district=Tools.transliterate(oHomeTmp.district,0)
    oHomeTmp.homenumber=Tools.transliterate(oHomeTmp.homenumber,0)
    return oHomeTmp
  }
  Home csiSetUnrealiable (iStatus){
  	unrealiable = iStatus?:0
  	this
  }
}