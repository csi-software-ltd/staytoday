import java.text.DateFormat
import java.text.SimpleDateFormat

class Dailylog {
  def sessionFactory 
  def searchService  

  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
  
  static mapping = {
    version false
  }  

  static constraints = {
    reference (nullable:true)	   
  }
  Long id
  Date requesttime
  String userip
  String reference
  Integer ref_id
  Integer type
  String page
  String keyword
  String reclist
  Integer reccount
  String lang
  Long users_id
  String site
  Long  home_id
  String prop  
  
  String toString() {"${this.page}"
    
  }

  def cleanUp(){
    def session = sessionFactory.getCurrentSession()
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()    
  }           
  def processDailyLog(){
    log.debug('processDailyLog Start:'+new Date())
    def session = sessionFactory.getCurrentSession()
    def sSql
    def qSql
	
    def hsSql=[select:'',from:'',group:'']      
    
    hsSql.select="*,min(requesttime) as requesttime"
    hsSql.from="dailylog"
	hsSql.group="requesttime"
	def result=searchService.fetchData(hsSql,null,null,null,null,Dailylog.class)
	
    DateFormat formatter ; 
    Date date ; 
    formatter = new SimpleDateFormat("yyyy-MM-dd");
    date = (Date)formatter.parse(result[0].requesttime.toString()); 

    Calendar oCalendar=Calendar.getInstance();
    oCalendar.setTimeInMillis(date.getTime());		    	
    oCalendar.set(oCalendar.DAY_OF_YEAR,1)
    def sYearDate = Tools.escape(String.format('%tF', oCalendar.time))
    
	oCalendar.setTimeInMillis(date.getTime());
    oCalendar.set(oCalendar.DAY_OF_MONTH,1)
    def sMonthDate = Tools.escape(String.format('%tF', oCalendar.time))
    
	oCalendar.setTimeInMillis(date.getTime());
    oCalendar.set(oCalendar.DAY_OF_WEEK,2)
    def sWeekDate = Tools.escape(String.format('%tF', oCalendar.time))	
	
    def sDate
    def i = 0
    def connection = session.connection()
    def state = connection.createStatement()
          
      for (iGrouplevel in [0,1,2]){       
		log.debug('processDailyLog with Grouplevel='+iGrouplevel+' Start:'+new Date())
        switch(iGrouplevel){
          case 0: sDate = sYearDate; break
          case 1: sDate = sMonthDate; break
          case 2: sDate = sWeekDate; break
        }        
		
        //Statkeywords
        if (iGrouplevel != 2){
          sSql = "INSERT INTO statkeywords (keyword,logtime,grouplevel,rating,site,lang,succeed)"+
                 "(SELECT keyword,'"+sDate+"',"+iGrouplevel+",1 as rat,substr(site,1,3),lang,if(reccount,1,0) as succ "+
                 "FROM dailylog  WHERE keyword<>'') "+
                 "ON DUPLICATE KEY UPDATE "+
                 "rating=rating+1,succeed=if(reccount,1,0) "
		  state.addBatch(sSql)
        }

		//Statpages
		sSql = "INSERT INTO statpages (statpagetype_id,logtime,grouplevel,rating,site,lang,ref_id) "+
               "SELECT st.id,'"+sDate+"',"+iGrouplevel+",1,substr(site,1,3),lang,ref_id "+
               "FROM dailylog, statpagetype st "+
               "WHERE st.page=dailylog.page AND st.is_active=1  ON DUPLICATE KEY UPDATE rating=(rating+1)"
	    state.addBatch(sSql)
		
		sSql = "INSERT INTO statservice (service_id,logtime,grouplevel,rating,site,lang) "+
               "SELECT type,'"+sDate+"',"+iGrouplevel+",1,substr(site,1,3),lang "+
               "FROM dailylog   ON DUPLICATE KEY UPDATE rating=(rating+1)"
        state.addBatch(sSql)
		
        //Statprop
		sSql = "INSERT INTO statprop (statpagetype_id,prop,logtime,grouplevel,rating,site,lang,ref_id)"+
               "(SELECT st.id,prop,'"+sDate+"',"+iGrouplevel+",1 as rat,substr(site,1,3),lang,ref_id "+
               "FROM dailylog, statpagetype st "+
			   "WHERE st.page=dailylog.page AND st.is_active=1 AND prop<>'') "+
               "ON DUPLICATE KEY UPDATE rating=rating+1"
		state.addBatch(sSql)
	  log.debug('processDailyLog with Grouplevel='+iGrouplevel+' Stop:'+new Date())	
      }
        //Stathome
		log.debug('processDailyLog Stathome Start:'+new Date())
        //1
        sSql = """
          SELECT *
          FROM dailylog WHERE (page='homeview' and type=0) or (page='widget' and type=25)
          """
        
        qSql = session.createSQLQuery(sSql)        
        qSql.addEntity(Dailylog.class)
        def lsHomeView = qSql.list()
        sSql=''        
        lsHomeView.each{
            //online for reading in console )			
			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_view,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sYearDate+"',0"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_view=(rating_view+1)"
	
            state.addBatch(sSql)
			
			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_view,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sMonthDate+"',1"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_view=(rating_view+1)"
    
			state.addBatch(sSql)

			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_view,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sWeekDate+"',2"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_view=(rating_view+1)"

			state.addBatch(sSql)
        }
        //2
        sSql = """
               SELECT *
               FROM dailylog 
               WHERE page='edithome'              
               """
        qSql = session.createSQLQuery(sSql)        
        qSql.addEntity(Dailylog.class)
        def lsHomeEdit = qSql.list()
        sSql=''
        lsHomeEdit.each{
		
			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_edit,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sYearDate+"',0"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_edit=(rating_edit+1)"
	
            state.addBatch(sSql)

			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_edit,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sMonthDate+"',1"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_edit=(rating_edit+1)"
    
			state.addBatch(sSql)

			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_edit,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sWeekDate+"',2"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_edit=(rating_edit+1)"

			state.addBatch(sSql)
        }		
        //3
        sSql = """
               SELECT *
               FROM dailylog 
               WHERE page='specoffer'
               """
        qSql = session.createSQLQuery(sSql)        
        qSql.addEntity(Dailylog.class)
        def lsHomeSpecoffer = qSql.list()
        sSql=''
        lsHomeSpecoffer.each{
		
			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_specoffer,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sYearDate+"',0"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_specoffer=(rating_specoffer+1)"
	
            state.addBatch(sSql)

			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_specoffer,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sMonthDate+"',1"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_specoffer=(rating_specoffer+1)"
    
			state.addBatch(sSql)

			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_specoffer,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sWeekDate+"',2"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_specoffer=(rating_specoffer+1)"

			state.addBatch(sSql)
        }
        //4
        sSql = """
          SELECT *
          FROM dailylog WHERE page='search'
          """
        
        qSql = session.createSQLQuery(sSql)        
        qSql.addEntity(Dailylog.class)
        def lsHomeListing = qSql.list()
        sSql=''        
        
        lsHomeListing.each{
          def lsRecIds = it.reclist.tokenize(',')

          for (sId in lsRecIds){
            def iId=0
            try{
              iId=sId.trim().toLong()
            }catch(Exception e){
              continue;
            }
            //online for reading in console )
			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_listing,site,lang,ref_id) "+
                   "VALUES ("+iId+",'"+sYearDate+"',0"+",1,'"+Tools.escape(it.site)[0..2]+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_listing=(rating_listing+1)"
	
            state.addBatch(sSql)

			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_listing,site,lang,ref_id) "+
                   "VALUES ("+iId+",'"+sMonthDate+"',1"+",1,'"+Tools.escape(it.site)[0..2]+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_listing=(rating_listing+1)"
    
			state.addBatch(sSql)

			sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_listing,site,lang,ref_id) "+
                   "VALUES ("+iId+",'"+sWeekDate+"',2"+",1,'"+Tools.escape(it.site)[0..2]+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
				   "ON DUPLICATE KEY UPDATE rating_listing=(rating_listing+1)"

			state.addBatch(sSql)
          }
        }
        //5
        sSql = """
               SELECT *
               FROM dailylog 
               WHERE page='firstcontact'
               """
        qSql = session.createSQLQuery(sSql)        
        qSql.addEntity(Dailylog.class)
        def lsHomeContact = qSql.list()
        sSql=''
        lsHomeContact.each{
    
      sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_contact,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sYearDate+"',0"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
           "ON DUPLICATE KEY UPDATE rating_contact=(rating_contact+1)"
  
            state.addBatch(sSql)

      sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_contact,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sMonthDate+"',1"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
           "ON DUPLICATE KEY UPDATE rating_contact=(rating_contact+1)"
    
      state.addBatch(sSql)

      sSql = "INSERT INTO stathome(home_id,logtime,grouplevel,rating_contact,site,lang,ref_id) "+
                   "VALUES ("+it.home_id+",'"+sWeekDate+"',2"+",1,'"+Tools.escape(it.site[0..(it.site.size()<2?it.site.size():2)])+"','"+Tools.escape(it.lang)+"',"+it.ref_id+") "+
           "ON DUPLICATE KEY UPDATE rating_contact=(rating_contact+1)"

      state.addBatch(sSql)
        }
        //end
	
      try{
        state.executeBatch() 
      }catch(Exception e){
        log.debug('\nError Dailylog:: processDailylog \n'+e.toString())
      }
	  log.debug('processDailyLog Stathome Stop:'+new Date())
      //connection.commit() -- not need (autocommit - true)
      cleanUp() // workaround memory leak in grails (?)
      //('change status for processed')
  
      sSql = "TRUNCATE dailylog"
      qSql = session.createSQLQuery(sSql) 
      try{	  
        qSql.executeUpdate()		
	  }catch(Exception e){
        log.debug('\nError on TRUNCATE dailylog \n'+e.toString())
      }
/*temporarily not need
    sSql = "select id,request from statinfo"
    qSql = session.createSQLQuery(sSql)
    def lsRequests = qSql.list()
    lsRequests.each{
      sSql = "update statinfo set q=(${it[1]}) where id=${it[0]}"
      qSql = session.createSQLQuery(sSql)
      try{
        qSql.executeUpdate()
      }catch(Exception e){
        log.debug('Error on update statinfo \n'+e.toString())
      }
    }*/
    session.clear() //DM
  }  
  //////////////////////////////////////////////////////////////////////////////////////////
  def takeFromTempdailylog(iLimit){
    log.debug("INSERT INTO dailylog Start: "+new Date())
 
    def sSql="""INSERT INTO dailylog(`requesttime`, `userip`, `reference`,  `ref_id`, `type`, `page`, `keyword`, `reclist`, `reccount`, `lang`,  `users_id`, `site`,  `status`, `home_id`, `prop`, `version`)
	  SELECT `requesttime`, `userip`, `reference`,  `ref_id`, `type`, `page`, `keyword`, `reclist`, `reccount`, `lang`,  `users_id`, `site`,  `status`, `home_id`, `prop`, `version` FROM temp_dailylog where status=0 ORDER BY id LIMIT :limit 
	"""
    def session = sessionFactory.getCurrentSession()
    def qSql = session.createSQLQuery(sSql)

    try{
      qSql.setInteger('limit',iLimit)	  	
      if (!qSql.executeUpdate()){
	    return false
		}
log.debug("INSERT INTO dailylog Stop: "+new Date())
log.debug("update temp_dailylog Start: "+new Date())
        sSql = "update temp_dailylog set status=1 where status=0 ORDER BY id LIMIT :limit" 
		qSql = session.createSQLQuery(sSql)
        qSql.setInteger('limit',iLimit)
        qSql.executeUpdate()
log.debug("update temp_dailylog Stop: "+new Date())

    }catch(Exception e){
      log.debug('Error on copying temp_dailylog to dailylog')
      log.debug(e.toString())
    }
    session.clear() //DM	
    return true
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  def updateFromOnlinelog(){	
	def sSql="""insert into temp_dailylog (`requesttime`, `userip`, `reference`,  `ref_id`, `type`, `page`, `keyword`, `reclist`, `reccount`, `lang`,  `users_id`, `site`,  `status`, `home_id`, `prop`, `version`,`useragent`)
      SELECT  `requesttime`, `userip`, `reference`,  `ref_id`, `type`, `page`, `keyword`, `reclist`, `reccount`, `lang`,  `users_id`, `site`,  `status`, `home_id`, `prop`, `version`, `useragent` FROM onlinelog
	"""
    def session = sessionFactory.getCurrentSession()
    def qSql = session.createSQLQuery(sSql)
    try{
      qSql.executeUpdate()

      sSql = "TRUNCATE onlinelog"
      qSql = session.createSQLQuery(sSql)
      qSql.executeUpdate()

    }catch(Exception e){
      log.debug('Error on copying onlinelog to temp_dailylog')
      log.debug(e.toString())
    }
    session.clear() //DM
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////
  def truncateTempdailylog(){
    def session = sessionFactory.getCurrentSession()    
      def sSql="TRUNCATE temp_dailylog"
      def qSql = session.createSQLQuery(sSql)
      try{
        qSql.executeUpdate()
      }catch(Exception e){
        log.debug('Error on truncating temp_dailylog')
        log.debug(e.toString())
      }
      session.clear() //DM    
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  def bot(){
    log.debug("update bot temp_dailylog Start: "+new Date())
    def session = sessionFactory.getCurrentSession()
    def connection = session.connection()
    def state = connection.createStatement()

    def i=1
    def sSql=""
    for(oSpider in Spider.findAll('FROM Spider')){
      sSql="update temp_dailylog set status=2 where useragent like '%"+oSpider.name+"%'"
      state.addBatch(sSql)
      sSql="""insert into statspider(rating, logdate, spider_id) select count(*),date_sub(current_date, INTERVAL 1 DAY),"""+i+
           """ from temp_dailylog where useragent like '%"""+oSpider.name+"""%' on duplicate key update rating=rating+(select count(*) from temp_dailylog where useragent like '%"""+oSpider.name+"""%');"""
      state.addBatch(sSql)
      if (oSpider.id in [1l,15l]) {
        [31,32,65,66,67].each{
          sSql="""INSERT INTO statspiderpage(spider_id,keyword,type,prop,requesttime)
                  SELECT $oSpider.id, keyword, $it, prop, requesttime
                  FROM temp_dailylog tdl
                  WHERE useragent LIKE '%$oSpider.name%' AND type=$it
                  ORDER BY requesttime DESC ON DUPLICATE KEY
                  UPDATE requesttime = tdl.requesttime
               """
          state.addBatch(sSql)
        }
      }
      i++
    }
    //end
    try{
      state.executeBatch()
    }catch(Exception e){
      log.debug('Error Dailylog:: process bot')
    }

    log.debug("update bot temp_dailylog Stop: "+new Date())
  }
}