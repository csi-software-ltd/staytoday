import org.codehaus.groovy.grails.commons.ConfigurationHolder

class HomeSearch {
  def searchService
  
  static mapping = {    
    table 'DUMMY_NAME'
    version false
    cache false 
  }	
  
  Long id
  Long hid
  Long client_id
  String name  
  Integer hometype_id
  Integer homeclass_id
  Integer homeroom_id
  Integer homebath_id
  Integer bed
  Integer homeperson_id
  Integer country_id
  Integer region_id
  Integer district_id
  Long city_id
  String city
  Long street_id
  String address
  String fulladdress
  String pindex
  String spcf
  Long x
  Long y
  Integer geostatus
  Integer modstatus
  String description
  //String description_site
  String remarks
  String fullinfo
  Date inputdate
  Date moddate
  String mainpicture
  Long pricestandard
  Integer valuta_id 
  
  String room 
  Long price 
  Integer rating
  String shortaddress

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

  String linkname
  Integer rule_minday_id
  Integer rule_maxday_id
  Integer nref
  Integer kol 
  String user_name
  String user_picture
  Integer user_social

  Long hotdiscount_id
  Long longdiscount_id
  Integer hotdiscexpiredays
  Integer hotminrentdays
  Integer hotdiscount
  Integer longdiscexpiredays
  Integer longminrentdays
  Integer longdiscount
  String hotdiscounttext
  String longdiscounttext
  Integer curdiscount

  Integer is_fiesta
  Integer is_vip
  Integer is_renthour
  Integer is_index
  Integer is_coffee
  Integer is_fen
  Integer is_hometheater
  Integer is_iron
  Integer is_jacuzzi
  Integer is_kettle    

  String toString() {"${this.hid}"}

  def csiFindMain(){
    def hsSql = [select :"""*,home.id AS id, user.smallpicture AS user_picture, user.name AS user_name, user.is_external AS user_social,
                          home.modstatus AS modstatus, 0 AS kol, 0 AS room, 0 AS hid, 0 as hotdiscount_id, 0 as longdiscount_id,
                          0 as hotdiscexpiredays, 0 as hotminrentdays, 0 as hotdiscount, 0 as longdiscexpiredays,
                          0 as longminrentdays, 0 as longdiscount, '' as hotdiscounttext, '' as longdiscounttext, 0 as curdiscount,
                          ifnull((select ifnull(price_rub,0) from homeprop where home_id=home.ID and modstatus=1 and term>0
                          and date_end>=current_date order by date_start asc limit 1),0)*IF((select resstatus from client where id=home.client_id)=1,1.0,1) as price""",
                 from   :'home, user ',
                 where  :'home.client_id=user.client_id and is_am=1 and mainpicture is not null and home.modstatus=1 and is_mainpage=1',
                 order  :'rand()']

    return searchService.fetchData(hsSql,null,null,null,null,HomeSearch.class,Tools.getIntVal(ConfigurationHolder.config.mainpage.home.max,10))
  }

  def csiFindSpecoffer(sId=''){
    def hsInt=[:]
    def hsSql = [select :"""*,home.id AS id, user.smallpicture AS user_picture, user.name AS user_name, user.is_external AS user_social,
                          home.modstatus AS modstatus, 0 AS kol, 0 AS room, 0 AS hid, 0 as hotdiscount_id, 0 as longdiscount_id,
                          0 as hotdiscexpiredays, 0 as hotminrentdays, 0 as hotdiscount, 0 as longdiscexpiredays,
                          0 as longminrentdays, 0 as longdiscount, '' as hotdiscounttext, '' as longdiscounttext, 0 as curdiscount,
                          ifnull((select ifnull(price_rub,0) from homeprop where home_id=home.ID and modstatus=1 and term>0
                          and date_end>=current_date order by date_start asc limit 1),0)*IF((select resstatus from client where id=home.client_id)=1,1.0,1) as price""",
                 from   :'home, user ',
                 where  :'home.client_id=user.client_id and is_am=1 and mainpicture is not null and home.modstatus=1 and ( is_specoffer=1 or is_specoffer_auto=1 )'+((sId!='')?' and home.country_id=:country_id':''),
                 order  :'rand()']
    if(sId!=''){
      def iCountryId=Country.findByUrlname(sId)?.id  
      hsInt['country_id']=iCountryId
    }
    return searchService.fetchData(hsSql,null,hsInt,null,null,HomeSearch.class,Tools.getIntVal(ConfigurationHolder.config.mainpage.specoffer_home.max,4))
  }

  def csiFindByWhere(sWhere,iMax,iOffset,hsMainFilter,hsFilter,bFindHome,bFindOption=false,bNear=false,bMetro=false,bCitySight=false,sLang=''){
  //log.debug('hsFilter='+hsFilter)
    def sSearch=(sWhere!='')?'@fulladdress '+sWhere:''
    
    def bFindDistrict=0
    def bFindMetro=0
    
    def oRegion=sLang?Region.findWhere(name_en:sWhere):Region.findWhere(name:sWhere)    
      
    hsFilter.region_id=0
    if(oRegion){
      hsFilter.region_id=oRegion?.id?:0
      if(oRegion.is_district)      
        bFindDistrict=1
      /*if(oRegion.is_metro)
        bFindMetro=1*/
      if(sWhere.contains('область'))
        sSearch='@fulladdress oblast'        
    }
    
    def oCity=sLang?City.findByName_en(sWhere):City.findByName(sWhere)   
      
    if(oCity?.is_metro)
      bFindMetro=1

    def hsSphinx=[:]
    def hsRes=[records:[]]
    
    def sIndexName=(hsMainFilter?.date_start)?'arenda_date':'arenda_gen'
    if(!bNear){
      if(bMetro && bFindMetro)
        sIndexName=(hsMainFilter?.date_start)?'arenda_date_metro':'arenda_gen_metro'
      if(bCitySight && hsFilter?.citysight_id)   
        sIndexName=(hsMainFilter?.date_start)?'arenda_date_citysight':'arenda_gen_citysight'
    }
//println('sIndexName='+sIndexName)    
    if(bFindHome
	  || bFindOption){   //used in action search too for raskladka. Used only count, not records>>
    //log.debug('hsMainFilter='+hsMainFilter)
    //log.debug('hsFilter='+hsFilter)	 /*[coordinates:hsFilter?.coordinates,xl:hsFilter?.xl,xr:hsFilter?.xr,yd:hsFilter?.yd,yu:hsFilter?.yu]*/
       
      hsSphinx=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,iMax,iOffset,hsMainFilter,hsFilter,true,null,null,bNear)
      if (hsFilter?.minrating?:0) {//using for widget. If count less than minimum permissible value, canceling "minrating" filter parameter.      
        hsSphinx?.totalFound>Tools.getIntVal(ConfigurationHolder.config.widget.searchlisting.minrating.resultcount,5)?'':(hsSphinx=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,iMax,iOffset,hsMainFilter,(hsFilter-[minrating:hsFilter?.minrating]),true,null,null,bNear))
      }
      hsRes.count=(hsSphinx.totalFound>Tools.getIntVal(ConfigurationHolder.config.sphinx.limit))?Tools.getIntVal(ConfigurationHolder.config.sphinx.limit):hsSphinx.totalFound              
    }   
	///<<
    if(bFindHome){//
      hsRes.ids=hsSphinx.ids
      def i=0
      for(fSearch in hsSphinx.attrValues){
        def oHomeSearch=new HomeSearch()
        oHomeSearch.id=hsRes.ids[i]
        oHomeSearch.client_id=fSearch[0]
        oHomeSearch.name=fSearch[1]
        oHomeSearch.rating=fSearch[2]
        oHomeSearch.mainpicture=fSearch[3]
        oHomeSearch.hometype_id=fSearch[4]
        oHomeSearch.homeclass_id=fSearch[5]
        oHomeSearch.homeroom_id=fSearch[6]
        oHomeSearch.room=fSearch[7]
        oHomeSearch.homebath_id=fSearch[8]
        oHomeSearch.bed=fSearch[9]
        oHomeSearch.homeperson_id=fSearch[10]
        oHomeSearch.country_id=fSearch[11]
        oHomeSearch.region_id=fSearch[12]
        oHomeSearch.district_id=fSearch[13]
        oHomeSearch.city=fSearch[14]
        oHomeSearch.shortaddress=fSearch[15]
        oHomeSearch.x=fSearch[16]
        oHomeSearch.y=fSearch[17]
        oHomeSearch.price=(!hsMainFilter?.date_start)?fSearch[18]:csiGetPrice(fSearch,hsMainFilter)
        oHomeSearch.rule_minday_id=fSearch[19]
        oHomeSearch.rule_maxday_id=fSearch[20]
        oHomeSearch.valuta_id=fSearch[21]
        oHomeSearch.is_parking=fSearch[22]
        oHomeSearch.is_nosmoking=fSearch[23]
        oHomeSearch.is_kitchen=fSearch[24]
        oHomeSearch.is_tv=fSearch[25]
        oHomeSearch.is_internet=fSearch[26]
        oHomeSearch.is_wifi=fSearch[27]
        oHomeSearch.is_holod=fSearch[28]
        oHomeSearch.is_microwave=fSearch[29]
        oHomeSearch.is_cond=fSearch[30]
        oHomeSearch.is_family=fSearch[31]
        oHomeSearch.is_pets=fSearch[32]
        oHomeSearch.is_invalid=fSearch[33]
        oHomeSearch.is_heat=fSearch[34]
        oHomeSearch.is_wash=fSearch[35]
        oHomeSearch.is_breakfast=fSearch[36]
        oHomeSearch.is_visa=fSearch[37]
        oHomeSearch.is_swim=fSearch[38]
        oHomeSearch.is_steam=fSearch[39]
        oHomeSearch.is_gym=fSearch[40]
        oHomeSearch.is_hall=fSearch[41]
        oHomeSearch.is_area=fSearch[42]
        oHomeSearch.is_beach=fSearch[43]
        oHomeSearch.nref=fSearch[44]
        oHomeSearch.kol=fSearch[45]        
        //46-modstatus
        oHomeSearch.hid=fSearch[47]	  
        
        oHomeSearch.linkname=(hsMainFilter?.date_start)?fSearch[53]:fSearch[48]

        oHomeSearch.hotdiscount_id=(hsMainFilter?.date_start)?fSearch[54]:fSearch[49]
        oHomeSearch.longdiscount_id=(hsMainFilter?.date_start)?fSearch[55]:fSearch[50]
        oHomeSearch.hotdiscexpiredays=(hsMainFilter?.date_start)?fSearch[56]:0
        oHomeSearch.hotminrentdays=(hsMainFilter?.date_start)?fSearch[57]:0
        oHomeSearch.hotdiscount=(hsMainFilter?.date_start)?fSearch[58]:0
        oHomeSearch.longdiscexpiredays=(hsMainFilter?.date_start)?fSearch[59]:0
        oHomeSearch.longminrentdays=(hsMainFilter?.date_start)?fSearch[60]:0
        oHomeSearch.longdiscount=(hsMainFilter?.date_start)?fSearch[61]:0
        oHomeSearch.hotdiscounttext=(hsMainFilter?.date_start)?fSearch[62]:fSearch[51]
        oHomeSearch.longdiscounttext=(hsMainFilter?.date_start)?fSearch[63]:fSearch[52]
        oHomeSearch.curdiscount=csiGetDisplayDiscount(fSearch,hsMainFilter)
        oHomeSearch.moddate=(hsMainFilter?.date_start || (bMetro && bFindMetro) || (bCitySight && hsFilter?.citysight_id))?null:Date.parse('yyyy-MM-dd hh:mm:ss', fSearch[55]?:String.format('%tF %<tT',new Date()))
        oHomeSearch.is_fiesta=(!hsMainFilter?.date_start)?fSearch[56]:(bMetro && bFindMetro)?fSearch[67]:fSearch[66]
        oHomeSearch.is_index=(!hsMainFilter?.date_start)?fSearch[58]:(bMetro && bFindMetro)?fSearch[69]:fSearch[68] 
        oHomeSearch.city_id=(!hsMainFilter?.date_start)?fSearch[59]:(bMetro && bFindMetro)?fSearch[70]:fSearch[69] 
        oHomeSearch.is_coffee=(!hsMainFilter?.date_start)?fSearch[60]:(bMetro && bFindMetro)?fSearch[71]:fSearch[70]        
        oHomeSearch.is_fen=(!hsMainFilter?.date_start)?fSearch[61]:(bMetro && bFindMetro)?fSearch[72]:fSearch[71]
        oHomeSearch.is_hometheater=(!hsMainFilter?.date_start)?fSearch[62]:(bMetro && bFindMetro)?fSearch[73]:fSearch[72]
        oHomeSearch.is_iron=(!hsMainFilter?.date_start)?fSearch[63]:(bMetro && bFindMetro)?fSearch[74]:fSearch[73]
        oHomeSearch.is_jacuzzi=(!hsMainFilter?.date_start)?fSearch[64]:(bMetro && bFindMetro)?fSearch[75]:fSearch[74]
        oHomeSearch.is_kettle=(!hsMainFilter?.date_start)?fSearch[65]:(bMetro && bFindMetro)?fSearch[76]:fSearch[75]
        oHomeSearch.is_vip=(!hsMainFilter?.date_start)?fSearch[66]:(bMetro && bFindMetro)?fSearch[77]:fSearch[76]
        oHomeSearch.is_renthour=(!hsMainFilter?.date_start)?fSearch[67]:(bMetro && bFindMetro)?fSearch[78]:fSearch[77]        

        hsRes.records<<oHomeSearch
        i++
      }
    }    
   
    if(!hsFilter.metro_id && !hsFilter.citysight_id)//because of sIndexName with metro index
      sIndexName=(hsMainFilter?.date_start)?'arenda_date':'arenda_gen'
    
    def hsFilter_coordinates=[coordinates:hsFilter?.coordinates,xl:hsFilter?.xl,xr:hsFilter?.xr,yd:hsFilter?.yd,yu:hsFilter?.yu]
    if(bFindOption){              
      if(bNear)	  
        hsSphinx.option=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,hsFilter_coordinates,false,null,[option:1],bNear)	  
	    else{
	      //hsSphinx.option=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,[region_id:hsFilter.region_id],false,null,[option:1],bNear)]
        hsSphinx.option=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,[region_id:hsFilter?.region_id,metro_id:hsFilter?.metro_id,citysight_id:hsFilter?.citysight_id,bedroom:hsFilter?.bedroom,is_vip:hsFilter?.is_vip,is_fiesta:hsFilter?.is_fiesta,is_renthour:hsFilter?.is_renthour],false,null,[option:1],bNear)
      }
      
      def attrValues=hsSphinx?.option?.attrValues[0]

      hsRes.option=[:]

      if(attrValues){
      //log.debug('attrValues='+attrValues+'size='+attrValues.size())
        hsRes.option.is_parking=attrValues[33]//30
        hsRes.option.is_nosmoking=attrValues[34]
        hsRes.option.is_kitchen=attrValues[35]
        hsRes.option.is_tv=attrValues[36]
        hsRes.option.is_internet=attrValues[37]
        hsRes.option.is_wifi=attrValues[38]
        hsRes.option.is_holod=attrValues[39]
        hsRes.option.is_microwave=attrValues[40]
        hsRes.option.is_cond=attrValues[41]
        hsRes.option.is_family=attrValues[42]
        hsRes.option.is_pets=attrValues[43]
        hsRes.option.is_invalid=attrValues[44]
        hsRes.option.is_heat=attrValues[45]
        hsRes.option.is_wash=attrValues[46]
        hsRes.option.is_breakfast=attrValues[47]
        hsRes.option.is_visa=attrValues[48]
        hsRes.option.is_swim=attrValues[49]
        hsRes.option.is_steam=attrValues[50]
        hsRes.option.is_gym=attrValues[51]
        hsRes.option.is_hall=attrValues[52]
        hsRes.option.is_area=attrValues[53]
        hsRes.option.is_beach=attrValues[54]
        //x,y here
        hsRes.option.is_coffee=attrValues[57]
        hsRes.option.is_fen=attrValues[58]
        hsRes.option.is_hometheater=attrValues[59]
        hsRes.option.is_iron=attrValues[60]
        hsRes.option.is_jacuzzi=attrValues[61]
        hsRes.option.is_kettle=attrValues[62]
        hsRes.option.is_vip=attrValues[63]
        hsRes.option.is_renthour=attrValues[64]  
        hsRes.option.is_fiesta=attrValues[65]        
        
	//log.debug('hsRes.option.is_tv='+hsRes.option.is_tv)
      }

      def optionKeys=hsRes.option.keySet().sort {hsRes.option[it]}.reverse()
      hsRes.option_filter=[:]
      optionKeys.each {
        hsRes.option_filter[it]=hsRes.option[it]
      }
	    if(bNear){	    
	      hsFilter_coordinates.nref=1
	      hsRes.option.nref=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,hsFilter_coordinates,null,null,null,bNear).totalFound
	      hsFilter_coordinates.nref=0
	      hsFilter_coordinates.rating=1
	      hsRes.option.rating=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,hsFilter_coordinates,null,null,null,bNear).totalFound
	      hsFilter_coordinates.rating=0  
	    }else{         
  	    hsRes.option.nref=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,[nref:1,region_id:hsFilter.region_id,metro_id:hsFilter.metro_id,citysight_id:hsFilter.citysight_id,bedroom:hsFilter?.bedroom,is_vip:hsFilter?.is_vip,is_fiesta:hsFilter?.is_fiesta,is_renthour:hsFilter?.is_renthour],null,null,null,bNear).totalFound
        hsRes.option.rating=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,[rating:1,region_id:hsFilter.region_id,metro_id:hsFilter.metro_id,citysight_id:hsFilter.citysight_id,bedroom:hsFilter?.bedroom,is_vip:hsFilter?.is_vip,is_fiesta:hsFilter?.is_fiesta,is_renthour:hsFilter?.is_renthour],null,null,null,bNear).totalFound
        hsRes.option.hotdiscount=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,[hotdiscount:1,region_id:hsFilter.region_id,metro_id:hsFilter.metro_id,citysight_id:hsFilter.citysight_id,bedroom:hsFilter?.bedroom,is_vip:hsFilter?.is_vip,is_fiesta:hsFilter?.is_fiesta,is_renthour:hsFilter?.is_renthour],null,null,null,bNear).totalFound
	      hsRes.option.longdiscount=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,[longdiscount:1,region_id:hsFilter.region_id,metro_id:hsFilter.metro_id,citysight_id:hsFilter.citysight_id,bedroom:hsFilter?.bedroom,is_vip:hsFilter?.is_vip,is_fiesta:hsFilter?.is_fiesta,is_renthour:hsFilter?.is_renthour],null,null,null,bNear).totalFound
	    }
      if(attrValues){
        hsRes.option.x=attrValues[55].toInteger()//53
        hsRes.option.y=attrValues[56].toInteger()
      }
	  //metro>>
      if(bFindMetro && bMetro){   
        if((hsFilter?.metro_id?:[]).size()==1)      
          hsSphinx.metro=Sphinx.searchBySphinxLimit(sSearch,(hsMainFilter?.date_start)?'arenda_date_metro':'arenda_gen_metro',log,0,0,hsMainFilter,[region_id:oCity.region_id],false,null,[metro:1],bNear)
        else
          hsSphinx.metro=Sphinx.searchBySphinxLimit(sSearch,(hsMainFilter?.date_start)?'arenda_date_metro':'arenda_gen_metro',log,0,0,hsMainFilter,[region_id:oCity.region_id,bedroom:hsFilter?.bedroom,is_vip:hsFilter?.is_vip,is_fiesta:hsFilter?.is_fiesta,is_renthour:hsFilter?.is_renthour],false,null,[metro:1],bNear)

        def attrValuesMetro=hsSphinx?.metro?.attrValues        
        hsRes.metroMax=[]
        for(attrValue in attrValuesMetro){
          hsRes.metroMax<<[id:attrValue[1],total:attrValue[2]]
        }                      
      }
      //metro<<
      //CitySight>>
      if(bCitySight){       
        hsSphinx.citysigth=Sphinx.searchBySphinxLimit(sSearch,(hsMainFilter?.date_start)?'arenda_date_citysight':'arenda_gen_citysight',log,0,0,hsMainFilter,[city_id:oCity?.id],false,null,[citysight:1],bNear)
       
        def attrValuesMetro=hsSphinx?.citysigth?.attrValues        
        hsRes.citysightMax=[]
        for(attrValue in attrValuesMetro){
          hsRes.citysightMax<<[id:attrValue[1],total:attrValue[2]]
        }
      }      
      //CitySight<<
    }
    //hometype>>
    if(bNear){
      //hsSphinx.hometype_id=Sphinx.searchBySphinxLimit(sSearch,(hsMainFilter?.date_start)?'arenda_date':"arenda_gen",log,0,0,hsMainFilter,hsFilter_coordinates,false,[hometype_id_filter:1])
    }else
      hsSphinx.hometype_id=Sphinx.searchBySphinxLimit(sSearch,hsMainFilter?.date_start?'arenda_date':'arenda_gen',log,0,0,hsMainFilter-[hometype_id:hsMainFilter?.hometype_id],[region_id:hsFilter.region_id],false,null,[hometype:1])
    hsRes.hometypeMax=[]
    for(fSearch in hsSphinx?.hometype_id?.attrValues)
      hsRes.hometypeMax<<[id:fSearch[1],total:fSearch[2]]
    hsRes.hometypeMax=hsRes.hometypeMax.sort{it.id}
    hsRes.hometypeLinksData = hsSphinx?.hometype_id?.attrValues.inject([:]){map,fSearch -> map[fSearch[1].toString()]=fSearch[2];map}
    //<<hometype
    //homeclass>>
    if(bNear)
	    hsSphinx.homeclass_id=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,hsFilter_coordinates,false,null,[homeclass:1],bNear)
	  else
	    hsSphinx.homeclass_id=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,[region_id:hsFilter.region_id,metro_id:hsFilter.metro_id,citysight_id:hsFilter.citysight_id,bedroom:hsFilter?.bedroom,is_vip:hsFilter?.is_vip,is_fiesta:hsFilter?.is_fiesta,is_renthour:hsFilter?.is_renthour],false,null,[homeclass:1],bNear)

    hsRes.homeclassMax=[]     
	  for(fSearch in hsSphinx?.homeclass_id?.attrValues)	
	    hsRes.homeclassMax<<[id:fSearch[1],total:fSearch[2]]        
    
    hsRes.homeclassMax=hsRes.homeclassMax.sort{it.id}
    //<<homeclass                
    
    //homeroom>>
    if(bNear)
      hsSphinx.homeroom_id=Sphinx.searchBySphinxLimit(sSearch,(hsMainFilter?.date_start)?'arenda_date':'arenda_gen',log,0,0,hsMainFilter,hsFilter_coordinates,false,null,[bedroom:1],bNear)
    else if (hsMainFilter?.hometype_id==1)
      hsSphinx.homeroom_id=Sphinx.searchBySphinxLimit(sSearch,(hsMainFilter?.date_start)?'arenda_date':'arenda_gen',log,0,0,hsMainFilter,[region_id:hsFilter.region_id],false,null,[bedroom:1],bNear)

    hsRes.homeroomMax = hsSphinx?.homeroom_id?.attrValues.inject([:]){map,fSearch -> map['id'+fSearch[1]]=fSearch[2];map}?:[:]
    //<<homeroom
//apartments>>
    hsSphinx.is_vip=Sphinx.searchBySphinxLimit(sSearch,hsMainFilter?.date_start?'arenda_date':'arenda_gen',log,0,0,hsMainFilter-[hometype_id:hsMainFilter.hometype_id],[region_id:hsFilter.region_id],false,null,[is_vip:1])       
    if(hsSphinx?.is_vip?.attrValues[0])
      hsRes.is_vipLinksData = hsSphinx?.is_vip?.attrValues[0][0]    
    hsSphinx.is_fiesta=Sphinx.searchBySphinxLimit(sSearch,hsMainFilter?.date_start?'arenda_date':'arenda_gen',log,0,0,hsMainFilter-[hometype_id:hsMainFilter.hometype_id],[region_id:hsFilter.region_id],false,null,[is_fiesta:1])   
    if(hsSphinx?.is_fiesta?.attrValues[0])
      hsRes.is_fiestaLinksData = hsSphinx?.is_fiesta?.attrValues[0][0]    
    hsSphinx.is_renthour=Sphinx.searchBySphinxLimit(sSearch,hsMainFilter?.date_start?'arenda_date':'arenda_gen',log,0,0,hsMainFilter-[hometype_id:hsMainFilter.hometype_id],[region_id:hsFilter.region_id],false,null,[is_renthour:1])   
    if(hsSphinx?.is_renthour?.attrValues[0])
      hsRes.is_renthourLinksData = hsSphinx?.is_renthour?.attrValues[0][0]
//apartments<<
    
    
    //districts>>
    if(bFindDistrict){
      if(!bNear){                                                                                
        hsSphinx.district=Sphinx.searchBySphinxLimit(sSearch,sIndexName,log,0,0,hsMainFilter,[region_id:hsFilter.region_id,metro_id:hsFilter.metro_id,citysight_id:hsFilter.citysight_id,bedroom:hsFilter?.bedroom,is_vip:hsFilter?.is_vip,is_fiesta:hsFilter?.is_fiesta,is_renthour:hsFilter?.is_renthour],false,null,[district:1],bNear)              
      
        def attrValuesDistrict=hsSphinx?.district?.attrValues           
        hsRes.districtMax=[]
        for(attrValue in attrValuesDistrict){
          hsRes.districtMax<<[district_id:attrValue[1],total:attrValue[2]]           
        }             
      }
    }   
    //districts<<
//log.debug("hsRes="+hsRes)
    return hsRes
  }  
  def csiFindByCity(sWhere,iMax,hsFilter){
    def sSearch=(sWhere!='')?'@city '+sWhere:''
	
    def hsSphinx=[:]
    def hsRes=[records:[]]  //hsFilter?.date_start-neccessary field
    hsSphinx=Sphinx.searchBySphinxLimit(sSearch,'arenda_date_city',log,0,0,hsFilter,hsFilter)	                    	         	   	

    hsRes.ids=hsSphinx.ids
    hsRes.clientIds=[]	
    def i=0
    for(fSearch in hsSphinx.attrValues){
	    def oHomeSearch=new HomeSearch()
	    oHomeSearch.id=hsRes.ids[i]	  
	    oHomeSearch.client_id=fSearch[0]
	    oHomeSearch.name=fSearch[1]
	    oHomeSearch.rating=fSearch[2]
	    oHomeSearch.mainpicture=fSearch[3]
	    oHomeSearch.hometype_id=fSearch[4]
	    oHomeSearch.homeclass_id=fSearch[5]	  
	    oHomeSearch.homeroom_id=fSearch[6]
	    oHomeSearch.room=fSearch[7]
	    oHomeSearch.homebath_id=fSearch[8]
	    oHomeSearch.bed=fSearch[9]
	    oHomeSearch.homeperson_id=fSearch[10]
      oHomeSearch.country_id=fSearch[11]
	    oHomeSearch.region_id=fSearch[12]
	    oHomeSearch.district_id=fSearch[13]
	    oHomeSearch.city_id=fSearch[14]
	    oHomeSearch.shortaddress=fSearch[15]
	    oHomeSearch.x=fSearch[16]	 
	    oHomeSearch.y=fSearch[17]	  
	    oHomeSearch.price=fSearch[18]
	    oHomeSearch.rule_minday_id=fSearch[19]	  
	    oHomeSearch.rule_maxday_id=fSearch[20]
	    oHomeSearch.valuta_id=fSearch[21]
	    oHomeSearch.is_parking=fSearch[22]
	    oHomeSearch.is_nosmoking=fSearch[23]
	    oHomeSearch.is_kitchen=fSearch[24]
	    oHomeSearch.is_tv=fSearch[25]
	    oHomeSearch.is_internet=fSearch[26]
	    oHomeSearch.is_wifi=fSearch[27]
	    oHomeSearch.is_holod=fSearch[28]
	    oHomeSearch.is_microwave=fSearch[29]
	    oHomeSearch.is_cond=fSearch[30]
	    oHomeSearch.is_family=fSearch[31]
	    oHomeSearch.is_pets=fSearch[32]
	    oHomeSearch.is_invalid=fSearch[33]
	    oHomeSearch.is_heat=fSearch[34]
	    oHomeSearch.is_wash=fSearch[35]
	    oHomeSearch.is_breakfast=fSearch[36]
	    oHomeSearch.is_visa=fSearch[37]
	    oHomeSearch.is_swim=fSearch[38]  
	    oHomeSearch.is_steam=fSearch[39]	  
	    oHomeSearch.is_gym=fSearch[40]
	    oHomeSearch.is_hall=fSearch[41]	  
	    oHomeSearch.is_area=fSearch[42]
	    oHomeSearch.is_beach=fSearch[43]	  
	    oHomeSearch.nref=fSearch[44]
	    oHomeSearch.kol=fSearch[45]
	    //46-modstatus
      oHomeSearch.hid=fSearch[49]
      oHomeSearch.linkname=fSearch[53]
      oHomeSearch.hotdiscount_id=0
      oHomeSearch.longdiscount_id=0
      oHomeSearch.hotdiscexpiredays=0
      oHomeSearch.hotminrentdays=0
      oHomeSearch.hotdiscount=0
      oHomeSearch.longdiscexpiredays=0
      oHomeSearch.longminrentdays=0
      oHomeSearch.longdiscount=0
      oHomeSearch.hotdiscounttext=''
      oHomeSearch.longdiscounttext=''
      oHomeSearch.curdiscount=0

      hsRes.clientIds<<fSearch[0]

   	  hsRes.records<<oHomeSearch
      i++
	  }
    return hsRes
  }
/////////////////////////////////////////////////////////////////////////////////////////// 
  def csiFindApartments(sWhere){
    def hsFilter=[:]
    def sSearch=(sWhere!='')?'@fulladdress '+sWhere:''
    
    def bFindDistrict=0
    
    def oRegion=Region.findWhere(name:sWhere)    
      
    hsFilter.region_id=0
    if(oRegion){
      hsFilter.region_id=oRegion?.id?:0
      if(oRegion.is_district)      
        bFindDistrict=1    
      if(sWhere.contains('область'))
        sSearch='@fulladdress oblast'        
    }        
    def hsRes=[:]    
    def hsSphinx=[:]
    //another variant with findFilter?.is_vip,findFilter?.is_fiesta,findFilter?.is_renthour
    hsSphinx.option=Sphinx.searchBySphinxLimit(sSearch,'arenda_gen',log,0,0,null,[region_id:hsFilter?.region_id],false,null,[option:1])    
      
    def attrValues=hsSphinx?.option?.attrValues[0]

    hsRes.option=[:]

    if(attrValues){
      hsRes.option.is_vip=attrValues[63]
      hsRes.option.is_renthour=attrValues[64]  
      hsRes.option.is_fiesta=attrValues[65]                
    }

    return hsRes
  }

  def calcDistance(records, alike){
    def oHome = Home.get(alike)
    def result = []
    for (rec in records)
      result<<Math.rint(100.0 * (searchService.getDistance(oHome.x,oHome.y,rec.x,rec.y) / 1000)) / 100.0
    return result
  }

  def sortByDistance(records, alike){
    def oHome = Home.get(alike)
    records.sort {r1, r2 -> searchService.getDistance(oHome.x,oHome.y,r1.x,r1.y) <=> searchService.getDistance(oHome.x,oHome.y,r2.x,r2.y)}
    return records
  }

  //>>for mobile version
  def calcDistanceFromPoint(records, oPoint){
    def result = []
    for (rec in records)
      result<<Math.rint(100.0 * (searchService.getDistance(oPoint.x,oPoint.y,rec.x,rec.y) / 1000)) / 100.0
    return result
  }

  def sortByDistanceFromPoint(records, oPoint){
    records.sort {r1, r2 -> searchService.getDistance(oPoint.x,oPoint.y,r1.x,r1.y) <=> searchService.getDistance(oPoint.x,oPoint.y,r2.x,r2.y)}
    return records
  }
  //<<

  def csiFindWallet(aIds,iMax,iOffset){
    def hsSql = [
      select :"""*,home.id, fulladdress,fullinfo, mapkeyphrase,client_id, home.name as name,  rating, mainpicture,
                hometype_id, homeclass_id, homeroom_id, homeroom.name as room, homebath_id, bed,
                homeperson_id, country_id, region_id, district_id,
                city_id, shortaddress, x, y,
                case pricestatus when 1 then ifnull(pricestandard_rub,0)*(ifnull((select discount from homediscount where modstatus=1 and home.hotdiscount_id=homediscount.id limit 1),100))/100 else ifnull((select ifnull(price_rub,0) from homeprop  where home_id=home.ID and modstatus=1 and term=1 and date_end>=current_date and price>0 order by date_start asc limit 1),0) end as price,
                rule_minday_id, rule_maxday_id,
                valuta_id, is_parking, is_nosmoking, is_kitchen, is_tv,
                is_internet, is_wifi, is_holod, is_microwave, is_cond,
                is_family, is_pets, is_invalid, is_heat, is_wash, is_breakfast,
                is_visa, is_swim, is_steam, is_gym, is_hall, is_area, is_beach, nref,
                homeperson.kol as kol, modstatus, home.id as hid,
                '' AS user_name, '' AS user_picture,0 AS user_social, hotdiscount_id, longdiscount_id,
                0 as hotdiscexpiredays, 0 as hotminrentdays, 0 as hotdiscount, 0 as longdiscexpiredays,
                0 as longminrentdays, 0 as longdiscount, '' as hotdiscounttext, '' as longdiscounttext, 0 as curdiscount""",
      from   :'home, homeperson, homeroom',
      where  :"""modstatus=1 and homeperson_id=homeperson.id and homeroom_id=homeroom.id
		            and home.id in (:ids)"""]
      //order  :'rand()']

    def hsList=[ids:aIds]

    return searchService.fetchDataByPages(hsSql,null,null,null,null,hsList,null,iMax,iOffset,'home.id',true,HomeSearch.class)
  }

  def csiFindPopdirection(iPdi, iMax=Tools.getIntVal(ConfigurationHolder.config.popdirection.home_quantity,9)){
    def hsSql = [select :"""*,
						              0 AS user_picture, 0 AS user_name, 0 AS user_social,0 AS kol, 0 AS room, 0 AS hid, 0 as hotdiscount_id, 0 as longdiscount_id,
                          0 as hotdiscexpiredays, 0 as hotminrentdays, 0 as hotdiscount, 0 as longdiscexpiredays,
                          0 as longminrentdays, 0 as longdiscount, '' as hotdiscounttext, '' as longdiscounttext, 0 as curdiscount,
                          ifnull((select ifnull(price_rub,0) from homeprop where home_id=home.ID and modstatus=1 and term>0
                          and date_end>=current_date order by date_start asc limit 1),0)*IF((select resstatus from client where id=home.client_id)=1,1.0,1) as price""",
                 from   :'home',
                 where  :'modstatus=1 AND popdirection_id=:popdirection_id',
                 order  :'rand()']
    def hsInt=[:]
    hsInt['popdirection_id']=iPdi

    return searchService.fetchData(hsSql,null,hsInt,null,null,HomeSearch.class,iMax)
  }

  def csiGetPrice(_fSearch,_hsMainFilter){
    def modifier = 1.0
    def _today = Calendar.getInstance()
    if (_fSearch[52]==2){
      if(_fSearch[63]
        &&(((_hsMainFilter?.date_end?.getTime()?:Long.MAX_VALUE)-(_hsMainFilter?.date_start?.getTime()?:0))/(1000*60*60*24))>_fSearch[60]
        &&(((_hsMainFilter?.date_start?.getTime()?:0)-_today.getTimeInMillis())/(1000*60*60*24))>_fSearch[59]) {
        modifier = modifier*_fSearch[61]/100
      } else if (_fSearch[62]
                &&(((_hsMainFilter?.date_end?.getTime()?:Long.MAX_VALUE)-(_hsMainFilter?.date_start?.getTime()?:0))/(1000*60*60*24))>_fSearch[57]
                &&(((_hsMainFilter?.date_start?.getTime()?:0)-_today.getTimeInMillis())/(1000*60*60*24))<_fSearch[56]) {
        modifier = modifier*_fSearch[58]/100
      }
    }
    return _fSearch[18]*modifier
  }

  Integer csiGetDisplayDiscount(_fSearch,_hsMainFilter){
  //return type of current discount for home. 0 - none, 1 - long, 2 - hot, 3 - have some discount, but filtered by request
    def _today = Calendar.getInstance()
    def discount = 0
    if (_hsMainFilter?.date_start){
      if(_fSearch[63]
        && _fSearch[52]==2
        &&(((_hsMainFilter?.date_end?.getTime()?:Long.MAX_VALUE)-(_hsMainFilter?.date_start?.getTime()?:0))/(1000*60*60*24))>_fSearch[60]
        &&(((_hsMainFilter?.date_start?.getTime()?:0)-_today.getTimeInMillis())/(1000*60*60*24))>_fSearch[59]) {
        discount = 1
      } else if (_fSearch[62]
                && _fSearch[52]==2
                &&(((_hsMainFilter?.date_end?.getTime()?:Long.MAX_VALUE)-(_hsMainFilter?.date_start?.getTime()?:0))/(1000*60*60*24))>_fSearch[57]
                &&(((_hsMainFilter?.date_start?.getTime()?:0)-_today.getTimeInMillis())/(1000*60*60*24))<_fSearch[56]) {
        discount = 2
      } else {
        discount = (_fSearch[62]||_fSearch[63])?3:0
      }
    } else {
      discount = _fSearch[51]?2:_fSearch[52]?3:0
    }
    return discount
  }
  Boolean isHaveDiscountAdv(HomeSearch oHome){
    if (!oHome||!(oHome.longdiscount_id||oHome.hotdiscount_id)||!(Homediscount.get(oHome.longdiscount_id)?.modstatus||Homediscount.get(oHome.hotdiscount_id)?.modstatus)) {
      return false
    }
    return true
  }
  def csiGetDiscountText(){
    return Homediscount.get(hotdiscount_id)?.modstatus?Discountpercent.findByPercent(Homediscount.get(hotdiscount_id)?.discount).name:Homediscount.get(longdiscount_id)?.modstatus?Discountpercent.findByPercent(Homediscount.get(longdiscount_id)?.discount).name:''
  }
  
  def csiFindByIds(sIds){
    def hsList=[:]
    def hsSql = [select :"""*,home.id AS id, user.smallpicture AS user_picture, user.name AS user_name, user.is_external AS user_social,
                          home.modstatus AS modstatus, 0 AS kol, 0 AS room, 0 AS hid, 0 as hotdiscount_id, 0 as longdiscount_id,
                          0 as hotdiscexpiredays, 0 as hotminrentdays, 0 as hotdiscount, 0 as longdiscexpiredays,
                          0 as longminrentdays, 0 as longdiscount, '' as hotdiscounttext, '' as longdiscounttext, 0 as curdiscount,
                          ifnull((select ifnull(price_rub,0) from homeprop where home_id=home.ID and modstatus=1 and term>0
                          and date_end>=current_date order by date_start asc limit 1),0)*IF((select resstatus from client where id=home.client_id)=1,1.0,1) as price""",
                 from   :'home, user ',
                 where  :'home.client_id=user.client_id and is_am=1 and home.modstatus=1 and home.id in (:aIds)']
    hsList['aIds']=sIds
    return searchService.fetchData(hsSql,null,null,null,hsList,HomeSearch.class,Tools.getIntVal(ConfigurationHolder.config.mainpage.last_home.max,4))
  }
  
  def csiGetAvgPriceByRoomTypeId(lCityId,iRoomTypeId){
    def hsSql = [select :"""ROUND(AVG(case pricestatus when 1 then ifnull(pricestandard_rub,0)*(ifnull((select discount from homediscount where modstatus=1 and home.hotdiscount_id=homediscount.id limit 1),100))
                  *IF((select resstatus from client where id=home.client_id)=1,1.0,1)/100 else ifnull((select ifnull(price_rub,0) from homeprop
                   where home_id=home.ID and modstatus=1 and term=1 and date_end>=current_date and price>0 order by date_start asc limit 1),0)
                  *IF((select resstatus from client where id=home.client_id)=1,1.0,1) end),0) as price_avg """,                                  
                from   :'home, homeperson, homeroom',
                where  :'modstatus=1 and homeperson_id=homeperson.id and homeroom_id=homeroom.id AND home.city_id=:lCityId'+
                       ((iRoomTypeId)?" and homeroom_id=:iRoomTypeId":""),                
                group  :'modstatus']
    def hsLong = ['lCityId': lCityId]
    
    if(iRoomTypeId)
      hsLong['iRoomTypeId']=iRoomTypeId    
 
    return searchService.fetchData(hsSql,hsLong,null,null,null)
  }
   def csiGetMinPriceByCity(lCityId){
    def hsSql = [select :"""ROUND(MIN(case pricestatus when 1 then ifnull(pricestandard_rub,0)*(ifnull((select discount from homediscount where modstatus=1 and home.hotdiscount_id=homediscount.id limit 1),100))
                  *IF((select resstatus from client where id=home.client_id)=1,1.0,1)/100 else ifnull((select ifnull(price_rub,0) from homeprop
                   where home_id=home.ID and modstatus=1 and term=1 and date_end>=current_date and price>0 order by date_start asc limit 1),0)
                  *IF((select resstatus from client where id=home.client_id)=1,1.0,1) end),0) as price_min""",
                from   :'home, homeperson, homeroom',
                where  :'modstatus=1 and homeperson_id=homeperson.id and homeroom_id=homeroom.id AND home.city_id=:lCityId',
                group  :'modstatus']
    def hsLong = ['lCityId': lCityId]        
    
    return searchService.fetchData(hsSql,hsLong,null,null,null)
  }
  
  def csiGetMinPriceByRegion(lsRegionIds){
    def hsSql = [select :"""ROUND(MIN(case pricestatus when 1 then ifnull(pricestandard_rub,0)*(ifnull((select discount from homediscount where modstatus=1 and home.hotdiscount_id=homediscount.id limit 1),100))
                  *IF((select resstatus from client where id=home.client_id)=1,1.0,1)/100 else ifnull((select ifnull(price_rub,0) from homeprop
                   where home_id=home.ID and modstatus=1 and term=1 and date_end>=current_date and price>0 order by date_start asc limit 1),0)
                  *IF((select resstatus from client where id=home.client_id)=1,1.0,1) end),0) as price_min""",
                from   :'home, homeperson, homeroom',
                where  :'modstatus=1 and homeperson_id=homeperson.id and homeroom_id=homeroom.id AND home.region_id in (:region_ids)',
                group  :'modstatus']
    
    def hsList = [region_ids:lsRegionIds]    
    
    return searchService.fetchData(hsSql,null,null,null,hsList)
  }
  HomeSearch csiSetEnHomeSearch(){
    def oHomeSearchTmp=new HomeSearch()
    this.properties.each { prop, val ->
        if(["metaClass","class"].find {it == prop}) return
        if(oHomeSearchTmp.hasProperty(prop)) oHomeSearchTmp[prop] = val
      }
    oHomeSearchTmp.id=this.id  
    oHomeSearchTmp.name=Tools.transliterate(oHomeSearchTmp.name,0)
    oHomeSearchTmp.shortaddress=Tools.transliterate(oHomeSearchTmp.shortaddress,0)       
    oHomeSearchTmp.city=City.get(oHomeSearchTmp.city_id)?.name_en?:Tools.transliterate(oHomeSearchTmp.city,0)
    return oHomeSearchTmp
  }  
}
