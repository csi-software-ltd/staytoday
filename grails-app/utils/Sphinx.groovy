import groovy.sql.Sql
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.sphx.api.*

class Sphinx {
  static int COMMENTS_MAX=32000
  static int RATING_MAX=32000
  static long MAX_PRICE=1000000
  static int MAX_DATE_DAYS=100000
  static int MIN_DATE_DAYS=15000
  static int MAX_DAY_NUMBER_IN_YEAR=366
  static int MAX_TERM=10
    

  static searchBySphinxLimit(sName,sIndexName,oLog,iMax=null, iOffset=null, hsMainFilter=null, hsFilter=null,bOrderBy=null,hsGroup=null,findFilter=null,isNosName=false){   			
    def hsResult=[ids:[],weights:[],totalFound:0,attrValues:[]]		       

	if((hsFilter?.keywords?:'')!=''){        
	  if((sName?:'').size()>0)
	    sName='('+sName+')'
	  def lsNames=hsFilter.keywords.tokenize()
	  sName+='&(@fullinfo '
	  def i=0
	  for(sName1 in lsNames){
	    if(i==0)
	      sName+= '('+sName1+')'
	    else
	      sName+= '&('+sName1+')'
	    i++
	  }
	  sName+= ')'
  }

	StringBuffer q = new StringBuffer();
	if((sName?:[]).size()>0){
    q.append (sName)
	}
	if((sName?:'').size()>0 || isNosName){	//for mobile flag
	  SphinxClient cl = new SphinxClient();		
	  cl.SetServer (ConfigurationHolder.config.sphinx.server,Tools.getIntVal(ConfigurationHolder.config.sphinx.port));		
	  //cl.SetWeights (100, 1);
	  cl.SetMatchMode (SphinxClient.SPH_MATCH_EXTENDED2);
	  cl.SetLimits (((iOffset?:0)>0)?iOffset:0,((iMax?:0)>0)?iMax:Tools.getIntVal(ConfigurationHolder.config.sphinx.limit), Tools.getIntVal(ConfigurationHolder.config.sphinx.limit));	  
	
	  if(hsMainFilter?.date_start!=null){  	  
	    try{ 
        cl.SetFilterRange('term',1,MAX_TERM,false) 		
		    if(hsMainFilter?.date_end!=null){
		      def date_number						
		      use(groovy.time.TimeCategory) {              
            date_number=(hsMainFilter?.date_end - hsMainFilter?.date_start).days                       
          } 			
			    if(date_number>MAX_DAY_NUMBER_IN_YEAR)
			      date_number=MAX_DAY_NUMBER_IN_YEAR
          cl.SetFilterRange('mind',1,date_number,false)
          cl.SetFilterRange('maxd',date_number,MAX_DAY_NUMBER_IN_YEAR,false)				           	  
		    }
		    def date_start_days
		    def dateFrom=Date.parse("yyyy-MM-dd", '1970-01-01')  
		    use(groovy.time.TimeCategory) { 
          date_start_days=(hsMainFilter?.date_start - dateFrom).days
        }
        if(hsFilter?.date_start_change){//city>>		  
          cl.SetFilterRange('date_end_df',date_start_days+1-Tools.getIntVal(ConfigurationHolder.config.zayvka.days_range,7),MAX_DATE_DAYS,false)		  
		      cl.SetFilterRange('date_start_df',MIN_DATE_DAYS,date_start_days+1+Tools.getIntVal(ConfigurationHolder.config.zayvka.days_range,7),false)
		    }else{//city<<
          cl.SetFilterRange('date_end_df',date_start_days+1,MAX_DATE_DAYS,false)		  
		      cl.SetFilterRange('date_start_df',MIN_DATE_DAYS,date_start_days+1,false)		
		    }
	    }catch(Exception e){		
		    oLog.debug(e.toString())
		  }
	  }
	  
    if(hsMainFilter?.homeperson_id){
	    def oHomeperson=new Homeperson()		
	    cl.SetFilterRange ('homeperson_id',hsMainFilter?.homeperson_id,oHomeperson.getMax()[0],false)          			    
    }
    if(hsMainFilter?.hometype_id)	
	    cl.SetFilter('hometype_id',hsMainFilter?.hometype_id,false)
		
	  if((hsFilter?.homeclass_id?:[]).size()!=0){
	    int[] homeclass_ids
		  homeclass_ids=new int[hsFilter?.homeclass_id?.size()]
		  def i=0
	
		  for (homeclass_id in hsFilter.homeclass_id){		 
		    if(homeclass_id){
		      homeclass_ids[i]=homeclass_id		 						
		    }
		    i++
		  }	
      cl.SetFilter('homeclass_id', homeclass_ids, false)		
	  }
		if((hsFilter?.hometype_id_filter?:[]).size()!=0){
			int[] hometype_ids
			hometype_ids=new int[hsFilter?.hometype_id_filter?.size()]
			def i=0

			for (hometype_id in hsFilter.hometype_id_filter){
				if(hometype_id){
					hometype_ids[i]=hometype_id
				}
				i++
			}
			cl.SetFilter('hometype_id', hometype_ids, false)
	  }
    if((hsFilter?.room_id_filter?:[]).size()!=0){
			int[] room_ids
			room_ids=new int[hsFilter?.room_id_filter?.size()]
			def i=0

			for (room_id in hsFilter.room_id_filter){
				if(room_id){
					room_ids[i]=room_id
				}
				i++
			}
			cl.SetFilter('homeroom_id', room_ids, false)
	  }
    //district filter>>
	  if((hsFilter?.district_id?:[]).size()!=0){
	    int[] district_ids
	    district_ids=new int[hsFilter?.district_id?.size()]	    
	    def i=0
	    for (district_id in hsFilter.district_id){		 
	      if(district_id){        
		      district_ids[i]=district_id		 						
          i++
        }	      
	    }	
      cl.SetFilter('district_id', district_ids, false)		
	  }
    //district filter<<
    //metro filter>>
    if((hsFilter?.metro_id?:[]).size()!=0){
	    int[] metro_ids
	    metro_ids=new int[hsFilter?.metro_id?.size()]	    
	    def i=0
	    for (metro_id in hsFilter.metro_id){		 
	      if(metro_id){
		      metro_ids[i]=metro_id		 						
          i++
        }	      
	    }	
      cl.SetFilter('metro_id', metro_ids, false)		
	  }
    //metro filter<<
	  
	  if(hsFilter?.region_id)
	    cl.SetFilter('region_id', hsFilter?.region_id, false)
      
    if(hsFilter?.city_id)
	    cl.SetFilter('city_id', hsFilter?.city_id, false)
      
	  if(hsFilter?.exclude_ids){
	    long[] values = new long[hsFilter?.exclude_ids.size()]
		  def i=0
		  for(lId in hsFilter?.exclude_ids){         
		    values[i]=lId
        i++		  
      }		  
	    cl.SetFilter('client_id', values, true)
	  }
	  if(hsFilter?.client_id)
	    cl.SetGroupBy('client_id',SphinxClient.SPH_GROUPBY_ATTR)//

    if(findFilter?.hometype?:0){
      cl.SetSelect("""
		    hometype_id AS hometype_idF, COUNT(*) AS @count
		  """)
      cl.SetFilter('hometype_id',0,true)
		  cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus
      cl.SetGroupBy('hometype_id',SphinxClient.SPH_GROUPBY_ATTR)      
    }
    if(findFilter?.homeclass?:0){
      cl.SetSelect("""
		    homeclass_id AS homeclass_idF, COUNT(*) AS @count
		  """)
      cl.SetFilter('homeclass_id',0,true)
		  cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus
      cl.SetGroupBy('homeclass_id',SphinxClient.SPH_GROUPBY_ATTR)      
    }
    if(findFilter?.bedroom?:0){
			cl.SetSelect("""
				homeroom_id AS homeroom_idF, COUNT(*) AS @count
			""")
			cl.SetFilter('homeroom_id',0,true)
			cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus
			cl.SetGroupBy('homeroom_id',SphinxClient.SPH_GROUPBY_ATTR)
    }
    if(findFilter?.district?:0){
      cl.SetSelect("""
		    district_id AS district_idF, COUNT(*) AS @count
		  """)
      cl.SetFilter('district_id',0,true)
		  cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus
      cl.SetGroupBy('district_id',SphinxClient.SPH_GROUPBY_ATTR)      
    }    
    if(sIndexName=='arenda_gen_metro' || sIndexName=='arenda_date_metro'){
	    if(findFilter?.metro?:0){
		    cl.SetSelect("""
		      metro_id AS metro_idF, COUNT(*) AS @count
		    """)
        cl.SetFilter('metro_id',0,true)
		    cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus
        cl.SetGroupBy('metro_id',SphinxClient.SPH_GROUPBY_ATTR)           
	    }
    }
    if(sIndexName=='arenda_gen_citysight' || sIndexName=='arenda_date_citysight'){
    //println('hsFilter?.citysight_id='+hsFilter?.citysight_id)
      if((hsFilter?.citysight_id?:[]).size()!=0){
	      int[] citysight_ids
	      citysight_ids=new int[hsFilter?.citysight_id?.size()]	    
	      def i=0
	      for (citysight_id in hsFilter.citysight_id){		 
	        if(citysight_id){
		        citysight_ids[i]=citysight_id		 						
            i++
          }	      
	      }	
        cl.SetFilter('citysight_id', citysight_ids, false)		
	    }
      /*
      if(hsFilter?.citysight_id){	    
        cl.SetFilter('citysight_id', hsFilter?.citysight_id, false)		
	    }*/
	    if(findFilter?.citysight?:0){
		    cl.SetSelect("""
		      citysight_id AS citysight_idF, COUNT(*) AS @count
		    """)
        cl.SetFilter('citysight_id',0,true)
		    cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus
        cl.SetGroupBy('citysight_id',SphinxClient.SPH_GROUPBY_ATTR)           
	    }
    }
    
    if(findFilter?.option?:0){	  										
		  cl.SetSelect("""
		    SUM(is_parking) AS is_parkingSUM, SUM(is_nosmoking) AS is_nosmokingSUM, SUM(is_kitchen) AS is_kitchenSUM,
		    SUM(is_tv) AS is_tvSUM, SUM(is_internet) AS is_internetSUM, SUM(is_wifi) AS is_wifiSUM, SUM(is_holod) AS is_holodS,
		    SUM(is_microwave) AS is_microwaveS, SUM(is_cond) AS is_condS, SUM(is_family) AS is_familyS,
        SUM(is_pets) AS is_petsS, SUM(is_invalid) AS is_invalidS, SUM(is_heat) AS is_heatS, SUM(is_wash) AS is_washS,
		    SUM(is_breakfast) AS is_breakfastS, SUM(is_visa) AS is_visaS, SUM(is_swim) AS is_swimS, SUM(is_steam) AS is_steamS,
		    SUM(is_gym) AS is_gymS, SUM(is_hall) AS is_hallS, SUM(is_area) AS is_areaS, SUM(is_beach) AS is_beachS,
        AVG(x) AS avgX, AVG(y) AS avgY, SUM(is_coffee) AS is_coffeeS, SUM(is_fen) AS is_fenS, SUM(is_hometheater) AS is_hometheaterS,
        SUM(is_iron) AS is_ironS, SUM(is_jacuzzi) AS is_jacuzziS, SUM(is_kettle) AS is_kettleS, SUM(is_vip) AS is_vipS, SUM(is_renthour) AS is_renthourS, SUM(is_fiesta) AS is_fiestaS		  
		  """)     
		  cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus      
	  }
    
    if(findFilter?.is_vip?:0){
      cl.SetSelect("""
		    COUNT(*) AS @count
		  """)
      cl.SetFilter('is_vip',0,true)
		  cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus
      cl.SetGroupBy('is_vip',SphinxClient.SPH_GROUPBY_ATTR)      
    }
    if(findFilter?.is_fiesta?:0){
      cl.SetSelect("""
		    COUNT(*) AS @count
		  """)
      cl.SetFilter('is_fiesta',0,true)
		  cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus
      cl.SetGroupBy('is_fiesta',SphinxClient.SPH_GROUPBY_ATTR)      
    }
    if(findFilter?.is_renthour?:0){
      cl.SetSelect("""
		    COUNT(*) AS @count
		  """)
      cl.SetFilter('is_renthour',0,true)
		  cl.SetGroupBy('modstatus',SphinxClient.SPH_GROUPBY_ATTR)////modstatus
      cl.SetGroupBy('is_renthour',SphinxClient.SPH_GROUPBY_ATTR)      
    }
	  
	  if(hsFilter?.coordinates){	 
	    cl.SetFilterRange ('x',hsFilter?.xl,hsFilter?.xr,false)	  
	    cl.SetFilterRange ('y',hsFilter?.yd,hsFilter?.yu,false)	    	    	    
    }	  
	 
	  if(hsFilter?.price_max){	    
	    cl.SetFilterFloatRange ('price',hsFilter?.price_min,((hsFilter?.price_max?:0)==(Valuta.findWhere(regorder:1)?.max?:5000))?MAX_PRICE:hsFilter?.price_max,false)     
	  }
	//>>options
	  if(hsFilter?.is_parking)
	    cl.SetFilter('is_parking',1, false)	
	  if(hsFilter?.is_nosmoking)
	    cl.SetFilter('is_nosmoking',1, false)
    if(hsFilter?.is_kitchen)
	    cl.SetFilter('is_kitchen',1, false)
	  if(hsFilter?.is_tv)
	    cl.SetFilter('is_tv',1, false)
	  if(hsFilter?.is_internet)
	    cl.SetFilter('is_internet',1, false)
	  if(hsFilter?.is_wifi)
	    cl.SetFilter('is_wifi',1, false)
	  if(hsFilter?.is_holod)
	    cl.SetFilter('is_holod',1, false)
    if(hsFilter?.is_microwave)
	    cl.SetFilter('is_microwave',1, false)
	  if(hsFilter?.is_cond)
	    cl.SetFilter('is_cond',1, false)
    if(hsFilter?.is_family)
	    cl.SetFilter('is_family',1, false)
	  if(hsFilter?.is_pets)
	    cl.SetFilter('is_pets',1, false)
	  if(hsFilter?.is_invalid)
	    cl.SetFilter('is_invalid',1, false)
	  if(hsFilter?.is_heat)
	    cl.SetFilter('is_heat',1, false)
	  if(hsFilter?.is_wash)
	    cl.SetFilter('is_wash',1, false)
	  if(hsFilter?.is_breakfast)
	    cl.SetFilter('is_breakfast',1, false)
	  if(hsFilter?.is_visa)
	    cl.SetFilter('is_visa',1, false)
	  if(hsFilter?.is_swim)
	    cl.SetFilter('is_swim',1, false)
	  if(hsFilter?.is_steam)
	    cl.SetFilter('is_steam',1, false)		
	  if(hsFilter?.is_gym)
	    cl.SetFilter('is_gym',1, false)
	  if(hsFilter?.is_hall)
	    cl.SetFilter('is_hall',1, false)
	  if(hsFilter?.is_area)
	    cl.SetFilter('is_area',1, false)
	  if(hsFilter?.is_beach)
	    cl.SetFilter('is_beach',1, false)
	  if(hsFilter?.is_fiesta)
	    cl.SetFilter('is_fiesta',1, false)
    if(hsFilter?.is_coffee)
	    cl.SetFilter('is_coffee',1, false)
    if(hsFilter?.is_fen)
	    cl.SetFilter('is_fen',1, false)
    if(hsFilter?.is_hometheater)
	    cl.SetFilter('is_hometheater',1, false)
    if(hsFilter?.is_iron)
	    cl.SetFilter('is_iron',1, false)
    if(hsFilter?.is_jacuzzi)
	    cl.SetFilter('is_jacuzzi',1, false)
    if(hsFilter?.is_kettle)
	    cl.SetFilter('is_kettle',1, false) 
    if(hsFilter?.is_vip)
	    cl.SetFilter('is_vip',1, false)
    if(hsFilter?.is_renthour)
	    cl.SetFilter('is_renthour',1, false)      

    if(hsFilter?.nref)
			cl.SetFilterRange ('nref',1,COMMENTS_MAX,false)
    if(hsFilter?.rating)
			cl.SetFilterRange ('rating',1,RATING_MAX,false)
	  if(hsFilter?.hotdiscount&&hsFilter?.longdiscount){
	  	cl.SetFilter('is_discount',1, false)
	  } else if(hsFilter?.hotdiscount) {
	    cl.SetFilter('is_hotdiscount',1, false)
	  } else if(hsFilter?.longdiscount){
	    cl.SetFilter('is_longdiscount',1, false)
	  }
    if(hsFilter?.minrating)
	    cl.SetFilterRange ('rating',0,hsFilter.minrating,true)
		
	  if(hsFilter?.bed)
	    cl.SetFilter('bed',hsFilter?.bed, false)
	  if(hsFilter?.bedroom)
	    cl.SetFilter('homeroom_id',hsFilter?.bedroom, false)
	  if(hsFilter?.bathroom)
	    cl.SetFilter('homebath_id',hsFilter?.bathroom, false)
      //<<options			 

		if(bOrderBy){
      if(sIndexName=='arenda_gen_metro' || sIndexName=='arenda_date_metro'  || sIndexName=='arenda_gen_citysight'  || sIndexName=='arenda_date_citysight'){
        if((hsMainFilter?.order?:0)==0)
          cl.SetGroupBy('hid',SphinxClient.SPH_GROUPBY_ATTR,"rating desc, hid desc")
        else if((hsMainFilter?.order?:0)==1)//по возрастанию цены			
          cl.SetGroupBy('hid',SphinxClient.SPH_GROUPBY_ATTR,"price asc")
			  else if((hsMainFilter?.order?:0)==2)//по убыванию цены
				  cl.SetGroupBy('hid',SphinxClient.SPH_GROUPBY_ATTR,"price desc")
			  else if((hsMainFilter?.order?:0)==3)//по дате создания
				  cl.SetGroupBy('hid',SphinxClient.SPH_GROUPBY_ATTR,"hid asc")
			  else if((hsMainFilter?.order?:0)==-1)//by random
          cl.SetGroupBy('hid',SphinxClient.SPH_GROUPBY_ATTR,"@random")
      }else{
	      if((hsMainFilter?.order?:0)==0)
	        cl.SetSortMode ( SphinxClient.SPH_SORT_EXTENDED, "rating desc, hid desc");
			  else if((hsMainFilter?.order?:0)==1)//по возрастанию цены
				  cl.SetSortMode ( SphinxClient.SPH_SORT_ATTR_ASC, "price");
			  else if((hsMainFilter?.order?:0)==2)//по убыванию цены
				  cl.SetSortMode ( SphinxClient.SPH_SORT_ATTR_DESC, "price");
			  else if((hsMainFilter?.order?:0)==3)//по дате создания
				  cl.SetSortMode ( SphinxClient.SPH_SORT_ATTR_DESC, "hid");
			  else if((hsMainFilter?.order?:0)==-1)//by random
				  cl.SetSortMode ( SphinxClient.SPH_SORT_EXTENDED, "@random");
			}
	  }

    try{
	    SphinxResult res = cl.Query(q.toString(), sIndexName);
		  hsResult.totalFound=res.totalFound

		  for ( def i=0; i<res.matches.size(); i++ ){
		    SphinxMatch info = res.matches[i];
		    hsResult.ids << info.docId
		    hsResult.weights << info.weight
	      //oLog.debug( (i+1) + ". id=" + info.docId + ", weight=" + info.weight+',attrValues='+info.attrValues );
		    hsResult.attrValues<<info.attrValues
		  }
    }catch(Exception e){
			hsResult.error='Sphinx: Index request error'
      oLog.debug(hsResult.error+"\n"+e.toString())
			oLog.debug(cl.GetLastError())
    }
  }

    if(hsResult.ids.size()==0 ){
      hsResult.ids=[0.toLong()]
      hsResult.weights =[0]
    }
    return hsResult
  }

  static searchCityBySphinxLimit(sName,oLog,sLang){
		def hsResult=[ids:[],weights:[],totalFound:0,attrValues:[]]

		StringBuffer q = new StringBuffer();
		if((sName?:[]).size()>0){
      if(sLang)
        sName+='|(@name_en '+sName+')'
			q.append (sName)
		}
		if (sName) {
			SphinxClient cl = new SphinxClient();
			cl.SetServer (ConfigurationHolder.config.sphinx.server,Tools.getIntVal(ConfigurationHolder.config.sphinx.port));
			cl.SetMatchMode (SphinxClient.SPH_MATCH_EXTENDED2);
			cl.SetLimits (0,1,Tools.getIntVal(ConfigurationHolder.config.sphinx.limit));
			try{
				SphinxResult res = cl.Query(q.toString(), 'arenda_city');
				hsResult.totalFound=res.totalFound

				for ( def i=0; i<res.matches.size(); i++ ){
					SphinxMatch info = res.matches[i];
					hsResult.ids << info.docId
					hsResult.weights << info.weight
					hsResult.attrValues<<info.attrValues
				}
			}catch(Exception e){
				hsResult.error='Sphinx: Index request error'
				oLog.debug(hsResult.error+"\n"+e.toString())
				oLog.debug(cl.GetLastError())
			}
		}
    if(hsResult.ids.size()==0 ){
      hsResult.ids=[0.toLong()]
      hsResult.weights =[0]
    }
    return hsResult
	}

}