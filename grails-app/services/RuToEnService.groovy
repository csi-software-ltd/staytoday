//import org.codehaus.groovy.grails.commons.ConfigurationHolder
class RuToEnService {

  def setEnStreet(){
    def lsStreet=Street.list();
    
    for(oStreet in lsStreet){
      oStreet.name_en=Tools.transliterate(oStreet.name,0)      
  
			if(!oStreet.save(flush:true)) {
				log.debug(" Error on save Street:")
				oStreet.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}

	def setEnRegion(){
    def lsRegion=Region.list();
    
    for(oRegion in lsRegion){
      oRegion.name_en=Tools.transliterate(oRegion.name,0)
      oRegion.shortname_en=Tools.transliterate(oRegion.shortname,0)
  
			if(!oRegion.save(flush:true)) {
				log.debug(" Error on save Region:")
				oRegion.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	} 
  def setEnPopdirection(){
    def lsPopdirection=Popdirection.list();
    
    for(oPopdir in lsPopdirection){
      //oPopdir.name_en=Tools.transliterate(oPopdir.name,0)
      //oPopdir.name2_en=Tools.transliterate(oPopdir.name2,0)
      oPopdir.header_en=Tools.transliterate(oPopdir.header,0)
      oPopdir.annotation_en=Tools.transliterate(oPopdir.annotation,0)
      oPopdir.ceo_title_en=Tools.transliterate(oPopdir.ceo_title,0)
      oPopdir.ceo_title_cot_en=Tools.transliterate(oPopdir.ceo_title_cot,0)
      oPopdir.header_cot_en=Tools.transliterate(oPopdir.header_cot,0)
      oPopdir.shortdescription_en=Tools.transliterate(oPopdir.shortdescription,0)
      oPopdir.header_d_en=Tools.transliterate(oPopdir.header_d,0)
      oPopdir.ceo_title_d_en=Tools.transliterate(oPopdir.ceo_title_d,0)
      oPopdir.ceo_description_d_en=Tools.transliterate(oPopdir.ceo_description_d,0)            
      
  
			if(!oPopdir.save(flush:true)) {
				log.debug(" Error on save Popdir:")
				oPopdir.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}  
  def setEnCity(){
    def lsCity=City.list();
    
    for(oCity in lsCity){
      oCity.name_en=Tools.transliterate(oCity.name,0)    
  
			if(!oCity.save(flush:true)) {
				log.debug(" Error on save City:")
				oCity.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}
  def setEnInfotext(){
    def lsInfotext=Infotext.list();
    
    for(oInfotext in lsInfotext){
      oInfotext.title_en=Tools.transliterate(oInfotext.title,0)      
      oInfotext.promotext1_en=Tools.transliterate(oInfotext.promotext1,0)
      oInfotext.promotext2_en=Tools.transliterate(oInfotext.promotext2,0)
      oInfotext.itext_en=Tools.transliterate(oInfotext.itext,0) 
      oInfotext.itext2_en=Tools.transliterate(oInfotext.itext2,0)      
      oInfotext.itext3_en=Tools.transliterate(oInfotext.itext3,0)      
  
			if(!oInfotext.save(flush:true)) {
				log.debug(" Error on save Infotext:")
				oInfotext.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}  
  def setEnHome(){
    def lsHome=Home.list();
    
    for(oHome in lsHome){
      oHome.name_en=Tools.transliterate(oHome.name,0)
      oHome.shortaddress_en=Tools.transliterate(oHome.shortaddress,0)                      
  
			if(!oHome.save(flush:true)) {
				log.debug(" Error on save Home:")
				oHome.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}
  def setEnCitysight(){
    def lsCitysight=Citysight.list();
    
    for(oCitysight in lsCitysight){
      oCitysight.name_en=Tools.transliterate(oCitysight.name,0)
      oCitysight.title_en=Tools.transliterate(oCitysight.title,0)      
  
			if(!oCitysight.save(flush:true)) {
				log.debug(" Error on save oCitysight:")
				oCitysight.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}
  def setEnMetro(){
    def lsMetro=Metro.list();
    
    for(oMetro in lsMetro){
      oMetro.name_en=Tools.transliterate(oMetro.name,0)  
  
			if(!oMetro.save(flush:true)) {
				log.debug(" Error on save oMetro:")
				oMetro.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}  
  def setEnDistrict(){
    def lsDistrict=District.list();
    
    for(oDistrict in lsDistrict){
      oDistrict.name_en=Tools.transliterate(oDistrict.name,0)  
  
			if(!oDistrict.save(flush:true)) {
				log.debug(" Error on save oDistrict:")
				oDistrict.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	} 
  def setEnHomeoption(){
    def lsHomeoption=Homeoption.list();
    
    for(oHomeoption in lsHomeoption){
      oHomeoption.name_en=Tools.transliterate(oHomeoption.name,0)  
  
			if(!oHomeoption.save(flush:true)) {
				log.debug(" Error on save Homeoption:")
				oHomeoption.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}
  def setEnNotice(){
    def lsNotice=Notice.list();
    
    for(oNotice in lsNotice){
      oNotice.title_en=Tools.transliterate(oNotice.title,0)
      oNotice.description_en=Tools.transliterate(oNotice.description,0)
  
			if(!oNotice.save(flush:true)) {
				log.debug(" Error on save Notice")
				oNotice.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}
  /*
  def setEnHomeFulladress(){
    def lsHomes=Home.list()
    
    for(oHome in lsHomes){
      def sName=City.get(oHome?.city_id?:0)?.name_en?:''
      oHome.fulladdress+=' '+sName
      
      if(!oHome.save(flush:true)) {
				log.debug(" Error on save Home")
				oHome.errors.each{log.debug(it)}
				return false
			}
    }  
  }*/  
  def setEnHomeInfraoption(){
    def lsHomes=Home.list()
    def lsHomeinfr=Homeinfr.list()
    def lsHomedistance=Homedistance.list()
    
    for(oHome in lsHomes){          
      def lsPares=(oHome.infraoption?:'').split(',')
      oHome.infraoption_en=''
      
      for(sPare in lsPares){
        def lsPare=sPare.split(':')
        def lsPareEn=''
        
        for(homeinfr in lsHomeinfr){
          if(lsPare[0]==homeinfr.name)
            lsPareEn=homeinfr.name_en+':'
        }   
        if(lsPareEn){        
          for(homedist in lsHomedistance){
            if(lsPare[1]==homedist.name)
              lsPareEn+=homedist.name_en
          }
        
          oHome.infraoption_en+=lsPareEn+','
        }  
      }                  
      
      if(!oHome.save(flush:true)) {
				log.debug(" Error on save Home")
				oHome.errors.each{log.debug(it)}
				return false
			}
    }  
  }
  
  def setEnReserveDescription(){
    def lsReserve=Reserve.list()
    
    for(oReserve in lsReserve){      
      oReserve.description_en=Tools.transliterate(oReserve.description,0)
      
      if(!oReserve.save(flush:true)) {
				log.debug(" Error on save Reserve")
				oReserve.errors.each{log.debug(it)}
				return false
			}
    }  
  }
  
  def setEnClientSettlprocedure(){
    def lsClient=Client.list()
    
    for(oClient in lsClient){      
      oClient.settlprocedure_en=Tools.transliterate(oClient.settlprocedure,0)
      
      if(!oClient.save(flush:true)) {
				log.debug(" Error on save Client")
				oClient.errors.each{log.debug(it)}
				return false
			}
    }  
  }
  
  def setEnHomeFulladress(){    
    def lsHomes=Home.list()   
    //def lsHomes=Home.findAll("FROM Home WHERE fulladdress like :name",[name:'%Астрахань%'])        
    
    for(oHome in lsHomes){     
      def sNewPart=''      
      sNewPart+=oHome.country_id?(' '+Country.get(oHome?.country_id?:0)?.name_en?:''):''  
      sNewPart+=oHome.city_id?(' '+City.get(oHome?.city_id?:0)?.name_en?:''):''         
      oHome.fulladdress+=sNewPart   
      
      if(!oHome.save(flush:true)) {
				log.debug(" Error on save Home")
				oHome.errors.each{log.debug(it)}
				return false
			}
    }  
  }
  def setEnPopkeywords(){
    def lsPopkeywords=Popkeywords.list();
    
    for(oPopkeys in lsPopkeywords){
      oPopkeys.name_en=Tools.transliterate(oPopkeys.name,0)
      
  
			if(!oPopkeys.save(flush:true)) {
				log.debug(" Error on save Popkeys:")
				oPopkeys.errors.each{log.debug(it)}
				return false
			}
		}
		return true
	}  
  
}
