import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SearchService {
  boolean transactional = false
  def sessionFactory
  // def m_oConnect=null <-- TODO? set one connection object?

  def fetchDataByPages(hsSql, hsFilter,
                hsLong,hsInt,hsString,hsList,lsNotUseInCount,
                iMax,iOffset,
                sCount,bComputeCount,
                clClassName,lsDictionaryIds=null,isEager=false){//set isEager=true if using eager fetched property in domain class and object will not be detached by session.clear() method
				
    def session=sessionFactory.getCurrentSession()
    def hsRes=[records:[],count:0]

    if(lsNotUseInCount==null) lsNotUseInCount=[]
    if(hsLong==null)   hsLong=[:]
    if(hsInt==null)    hsInt=[:]
    if(hsString==null) hsString=[:]
    if(hsList==null)   hsList=[:]
    if(hsFilter==null)  hsFilter=[:]
    if(hsFilter.string_par==null)  hsFilter.string_par=[:]
    if(hsFilter.long_par==null)    hsFilter.long_par=[:]
    if(hsFilter.list_par==null)    hsFilter.list_par=[:]

    def sFrom=  ' FROM '+hsSql.from+(hsFilter.from?:'')
    def sWhere= ' WHERE '+hsSql.where+(hsFilter.where?:'')
    def sSelect=' SELECT '+hsSql.select+(hsFilter.select?:'')
    def sOrder= ' ORDER BY '+(hsFilter.order?:'')+hsSql.order
	//def sGroup= ((hsSql.group!=null)?' GROUP BY '+hsSql.group :'')
    def sGroup= ((hsSql.group!=null)?' GROUP BY '+(hsFilter.group?:'')+hsSql.group :'')
    
    if(hsFilter.string_par.size()!=0)  hsString+=hsFilter.string_par
    if(hsFilter.long_par.size()!=0)    hsLong+=hsFilter.long_par
    if(hsFilter.list_par.size()!=0)    hsList+=hsFilter.list_par
    // int todo...

    try{
      def qSql
      if(bComputeCount){
        qSql=session.createSQLQuery("SELECT count("+sCount+")"+sFrom+sWhere+sGroup)

        for(hsElem in hsLong){
          if(!(hsElem.key in lsNotUseInCount))
            qSql.setLong(hsElem.key,hsElem.value);
        }
        for(hsElem in hsInt){
          if(!(hsElem.key in lsNotUseInCount))
            qSql.setInteger(hsElem.key,hsElem.value);
        }
        for(hsElem in hsString){
          if(!(hsElem.key in lsNotUseInCount))
            qSql.setString(hsElem.key,hsElem.value);
        }
        for(hsElem in hsList){
          if(!(hsElem.key in lsNotUseInCount))
            qSql.setParameterList(hsElem.key,hsElem.value);
        }
        hsRes.records=qSql.list()		
        if(hsRes.records==null)
          hsRes.records=[]	  		  
        else if(hsRes.records.size()!=0){
          if((sCount==''||sCount=='*') && hsSql.group)
            hsRes.count=hsRes.records.size()
          else
            hsRes.count=hsRes.records[0]            
          hsRes.records=[]
        }		
      }
      //--------------------------------
      if((lsDictionaryIds!=null)&&(hsRes.count!=0)){
        for(sField in lsDictionaryIds){
          qSql=session.createSQLQuery("SELECT DISTINCT "+sField+" "+sFrom+sWhere+sGroup)

          for(hsElem in hsLong){
            if(!(hsElem.key in lsNotUseInCount))
              qSql.setLong(hsElem.key,hsElem.value);
          }
          for(hsElem in hsInt){
            if(!(hsElem.key in lsNotUseInCount))
              qSql.setInteger(hsElem.key,hsElem.value);
          }
          for(hsElem in hsString){
            if(!(hsElem.key in lsNotUseInCount))
              qSql.setString(hsElem.key,hsElem.value);
          }
          for(hsElem in hsList){
            if(!(hsElem.key in lsNotUseInCount))
              qSql.setParameterList(hsElem.key,hsElem.value);
          }
          hsRes[sField]=qSql.list()		 
        }
      }

      if((hsRes.count==0) && bComputeCount)
        hsRes.records=[]
      else{
        qSql=session.createSQLQuery(sSelect+sFrom+sWhere+sGroup+sOrder)      
        if(iMax>0)
          qSql.setMaxResults(iMax )
        qSql.setFirstResult(iOffset)
        for(hsElem in hsLong)
          qSql.setLong(hsElem.key,hsElem.value);
        for(hsElem in hsInt)
          qSql.setInteger(hsElem.key,hsElem.value);
        for(hsElem in hsString)
          qSql.setString(hsElem.key,hsElem.value);
        for(hsElem in hsList)
          qSql.setParameterList(hsElem.key,hsElem.value);
        qSql.addEntity(clClassName)		
        hsRes.records=qSql.list()
        if(!bComputeCount)
          hsRes.count=hsRes.records?.size()
      }
    }catch (Exception e) {
      log.debug("Error fetchDataByPages\n"+e.toString()+"\n"+
                sSelect+"\n"+sFrom+"\n"+sWhere+"\n"+sGroup+"\n"+sOrder);
      hsRes.count=0
      hsRes.records=[]
    }  
    if (!isEager)
      session.clear()

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////
  def fetchData(hsSql,hsLong,hsInt,hsString,hsList,clClassName=null,iMax=-1){
    def session=sessionFactory.getCurrentSession()
    def hsRes=[]

    if(hsLong==null)   hsLong=[:]
    if(hsInt==null)    hsInt=[:]
    if(hsString==null) hsString=[:]
    if(hsList==null)   hsList=[:]

    def sSelect=' SELECT '+hsSql.select
    def sFrom=  ' FROM '+hsSql.from
    def sWhere= ((hsSql.where!=null)?' WHERE '+hsSql.where:'')
    def sOrder= ((hsSql.order!=null)?' ORDER BY '+hsSql.order:'')
    //hsSql.order= ' ORDER BY '+(hsFilter.order?:'')+(hsSql.order?:'')
    def sGroup= ((hsSql.group!=null)?' GROUP BY '+hsSql.group :'')
    
    try{
      def qSql
      qSql=session.createSQLQuery(sSelect+sFrom+sWhere+sGroup+sOrder)
      for(hsElem in hsLong)
        qSql.setLong(hsElem.key,hsElem.value);
      for(hsElem in hsInt)
        qSql.setInteger(hsElem.key,hsElem.value);
      for(hsElem in hsString)
        qSql.setString(hsElem.key,hsElem.value);
      for(hsElem in hsList)
        qSql.setParameterList(hsElem.key,hsElem.value);
      if(clClassName!=null)
        qSql.addEntity(clClassName)
      if(iMax>0)
        qSql.setMaxResults(iMax)     
      session.clear()  
      return qSql.list()
    }catch (Exception e) {
      log.debug("Error fetchData\n"+e.toString()+"\n"+
                sSelect+"\n"+sFrom+"\n"+sWhere+"\n"+sGroup+"\n"+sOrder);
      return []
    }
    return []
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////
  def getLastInsert(){
    def sSql="select last_insert_id()"
    def session = sessionFactory.getCurrentSession()    
    try{
      def qSql=session.createSQLQuery(sSql)
      def lsRecords=qSql.list()
      if(lsRecords.size()>0){
        session.clear()
        return lsRecords[0].toLong()
      }
    }catch (Exception e) {
      log.debug("Error SearchService::getLastInsert\n"+e.toString());
    }
    session.clear()
    return 0
  }

  def getDistance(x1,y1,x2,y2){
    def sSql="select distance("+x1+","+y1+","+x2+","+y2+")"
    def session = sessionFactory.getCurrentSession()    
    try{
      def qSql=session.createSQLQuery(sSql)
      def lsRecords=qSql.list()
      if(lsRecords.size()>0){
        session.clear()
        return lsRecords[0].toDouble()
      }
    }catch (Exception e) {
      log.debug("Error SearchService::getDistance\n"+e.toString());
    }
    session.clear()
    return 0
  }

  /////////////////////////////////////////////////////////////////////////////////////
  def _getMapFilter(hsFilter,xl,yd,xr,yu){  
    //requestService.init(this)
    def hsRes=[hsFilter:hsFilter]
    hsRes.hsFilter.coordinates=false    
    hsRes.hsFilter.xl=xl
    hsRes.hsFilter.yd=yd
    hsRes.hsFilter.xr=xr
    hsRes.hsFilter.yu=yu
    
    if(hsRes.hsFilter.xr!=0 && hsRes.hsFilter.yu!=0){//hsFilter.xl!=0 && hsFilter.yd!=0
      hsRes.hsFilter.coordinates=true
    }

    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def _getFilterDictionary(lsHomeclass,lsDistricts=[],lsMetros=[],lsCitysight=[]){    
    def hsRes=[:]    
    if(lsHomeclass){    
      def homeclass_ids=[]
      def oHomeclass=new Homeclass()      
      for(homeclass in lsHomeclass)    
        homeclass_ids<<homeclass.id   
      
      hsRes.homeclass=oHomeclass.getByIds(homeclass_ids)      
    }    
    if(lsDistricts){    
      def district_ids=[]
      def oDistrict=new District()      
      for(district in lsDistricts)    
        district_ids<<district.district_id   
      
      hsRes.district=oDistrict.getByIds(district_ids)     
    }
    if(lsMetros){
      def metro_ids=[]
      def oMetro=new Metro()
      for(metro in lsMetros)
        metro_ids<<metro.id
        
      hsRes.metro=oMetro.getByIds(metro_ids)  
    }
    if(lsCitysight){
      def citysight_ids=[]
      def oCitysight=new Citysight()
      for(citysight in lsCitysight)
        citysight_ids<<citysight.id
        
      hsRes.citysight=oCitysight.getByIds(citysight_ids)  
    }    
    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def _getMainFilter(hsInrequest,requestService){
    //requestService.init(this)
    def hsMainFilter=[set:false]  
    hsMainFilter.homeperson_id=requestService.getIntDef('homeperson_id',0)        
    
    hsMainFilter.hometype_id=requestService.getIntDef('hometype_id',0)
    if(!hsMainFilter.hometype_id){//here because of /type_flats hometype_id value                  
      if(hsInrequest?.hometype_id==1)
        hsMainFilter.hometype_id=1
      else
        hsMainFilter.hometype_id=Hometype.findByUrlname(requestService.getStr('hometype_id'))?.id?:0         
    }                     
    
    hsMainFilter.order=requestService.getIntDef('sort',0)
    hsMainFilter.date_start=hsInrequest?.date_start
    hsMainFilter.date_end=hsInrequest?.date_end   
    return hsMainFilter
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def _getFilter(requestService){
    //requestService.init(this)
    def hsRes=[:]
    hsRes.hsFilter=[set:false]
  
    hsRes.hsFilter.price_min=requestService.getLongDef('price_min',0)
    hsRes.hsFilter.price_max=requestService.getLongDef('price_max',0)
  
    hsRes.hsFilter.keywords=requestService.getStr('keywords') 
    hsRes.hsFilter.homeperson_id=requestService.getIntDef('homeperson_id',0)
    //hsRes.hsFilter.hometype_id=requestService.getIntDef('hometype_id',0)
    //if(requestService.getStr('homeclass_id'))
    hsRes.hsFilter.homeclass_id=requestService.getIds('homeclass_id')          
    hsRes.hsFilter.hometype_id_filter=requestService.getIds('hometype_id_filter')
    hsRes.hsFilter.room_id_filter=requestService.getIds('room_id_filter') 
    hsRes.hsFilter.nref=requestService.getLongDef('nref',0)
    hsRes.hsFilter.rating=requestService.getLongDef('rating',0)
    hsRes.hsFilter.hotdiscount=requestService.getLongDef('hotdiscount',0)
    hsRes.hsFilter.longdiscount=requestService.getLongDef('longdiscount',0)
    hsRes.hsFilter.is_parking=requestService.getLongDef('is_parking',0)
    hsRes.hsFilter.is_nosmoking=requestService.getLongDef('is_nosmoking',0)
    hsRes.hsFilter.is_kitchen=requestService.getLongDef('is_kitchen',0)
    hsRes.hsFilter.is_tv=requestService.getLongDef('is_tv',0)
    hsRes.hsFilter.is_internet=requestService.getLongDef('is_internet',0)
    hsRes.hsFilter.is_wifi=requestService.getLongDef('is_wifi',0)
    hsRes.hsFilter.is_holod=requestService.getLongDef('is_holod',0)
    hsRes.hsFilter.is_microwave=requestService.getLongDef('is_microwave',0)
    hsRes.hsFilter.is_cond=requestService.getLongDef('is_cond',0)
    hsRes.hsFilter.is_family=requestService.getLongDef('is_family',0)
    hsRes.hsFilter.is_pets=requestService.getLongDef('is_pets',0)
    hsRes.hsFilter.is_invalid=requestService.getLongDef('is_invalid',0)
    hsRes.hsFilter.is_heat=requestService.getLongDef('is_heat',0)
    hsRes.hsFilter.is_wash=requestService.getLongDef('is_wash',0)
    hsRes.hsFilter.is_breakfast=requestService.getLongDef('is_breakfast',0)
    hsRes.hsFilter.is_visa=requestService.getLongDef('is_visa',0)
    hsRes.hsFilter.is_swim=requestService.getLongDef('is_swim',0)
    hsRes.hsFilter.is_steam=requestService.getLongDef('is_steam',0)
    hsRes.hsFilter.is_gym=requestService.getLongDef('is_gym',0)
    hsRes.hsFilter.is_hall=requestService.getLongDef('is_hall',0)
    hsRes.hsFilter.is_area=requestService.getLongDef('is_area',0)
    hsRes.hsFilter.is_beach=requestService.getLongDef('is_beach',0)
    hsRes.hsFilter.is_fiesta=requestService.getLongDef('is_fiesta',0)
    hsRes.hsFilter.is_vip=requestService.getLongDef('is_vip',0)
    hsRes.hsFilter.is_renthour=requestService.getLongDef('is_renthour',0)
    
    hsRes.hsFilter.is_coffee=requestService.getLongDef('is_coffee',0)
    hsRes.hsFilter.is_fen=requestService.getLongDef('is_fen',0)
    hsRes.hsFilter.is_hometheater=requestService.getLongDef('is_hometheater',0)
    hsRes.hsFilter.is_iron=requestService.getLongDef('is_iron',0)
    hsRes.hsFilter.is_jacuzzi=requestService.getLongDef('is_jacuzzi',0)
    hsRes.hsFilter.is_kettle=requestService.getLongDef('is_kettle',0)    
  
    hsRes.hsFilter.bed=requestService.getLongDef('bed',0) 
    hsRes.hsFilter.bedroom=requestService.getLongDef('bedroom',0)
    hsRes.hsFilter.bathroom=requestService.getLongDef('bathroom',0)    

    hsRes.hsFilter.district_id=requestService.getIds('district_id')
    hsRes.hsFilter.metro_id=requestService.getIds('metro_id') 
    hsRes.hsFilter.citysight_id=requestService.getIds('citysight_id')    
    return hsRes
  }

  /////////////////////////////////////////////////////////////////////////////////////
  def findViewPortTile(aParam){
    def j=0;    
    def coordinates =[:]
    def aParamTmp=aParam.split(',')
    for(param in aParamTmp){
      def x1=-180000000;
      def x2=180000000;
      def y1=-85051128;
      def y2=85051128;   
    
      def y1cons=0;
      def y2cons=32;
      def yconsdel=0;
      def p1;
      def p2;
      def ydel;
    
      def consar=[-85051128,-83979259,-82676284,-81093213,-79171334,-76840816,-74019543,-70612614,-66513260,-61606396,-55776579,-48922499,-40979898,-31952162,-21943045,-11178401,0,11178401,21943045,31952162,40979898,48922499,55776579,61606396,66513260,70612614,74019543,76840816,79171334,81093213,82676284,83979259,85051128];
    
      def z=1;                     
    
      def L=param.length();
    
      def ZM=L;
      for(def i=0;i<L;++i) {        
        def test=param.getAt(i).toInteger();
      
        def xdel=Math.round((x1+x2)/2);        
        if(i<4){
          yconsdel=(y1cons+y2cons)/2;        
          ydel=consar[yconsdel.toInteger()];
        }else {
          ydel=Math.round((y1+y2)/2);
        }        
      
        switch(test) {
          case 0: p1=0; p2=1;break;
          case 1: p1=1; p2=1;break;
          case 2: p1=0; p2=0;break;
          case 3: p1=1; p2=0;break;
        }
        if(p1){ x1= xdel+1;
        }
        else{ x2= xdel;
        }
        if(p2){ y2= ydel; y2cons= yconsdel;
        }
        else  { y1= ydel+1; y1cons= yconsdel;
        }
      }    
      coordinates[j]=[]
      coordinates[j][0]=x1;
      coordinates[j][1]=y1;
      coordinates[j][2]=x2;
      coordinates[j][3]=y2;
      j++;
    }

    def minX=coordinates[0][0];
    def minY=coordinates[0][1];
    def maxX=coordinates[0][2];
    def maxY=coordinates[0][3];     

    for(def i=1;i<coordinates.size();i++){
      minX=(coordinates[i][0]>=minX)?minX:coordinates[i][0]
      minY=(coordinates[i][1]>=minY)?minY:coordinates[i][1]
      maxX=(coordinates[i][2]<=maxX)?maxX:coordinates[i][2]
      maxY=(coordinates[i][3]<=maxY)?maxY:coordinates[i][3]      
    }
    
    def fincoord=[minX/10,minY/10,maxX/10,maxY/10]    
  //def fincoord=[minX,minY,maxX,maxY]
    return fincoord   
  }

}
