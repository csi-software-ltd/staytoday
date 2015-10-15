import org.codehaus.groovy.grails.commons.ConfigurationHolder

class StatisticJob {
  static triggers = {
    //simple repeatInterval: 300000 // execute job once in 300 seconds
    cron cronExpression: ((ConfigurationHolder.config.statistic.cron!=[:])?ConfigurationHolder.config.statistic.cron:"0 0 0 * * ?")
  }

  def execute() {
    log.debug("LOG>> Process statistic")
    def oDaily = new Dailylog()
    try{
      oDaily.updateFromOnlinelog()
      oDaily.bot()
      def iLimit = Tools.getIntVal(ConfigurationHolder.config.statistic.limit,5000)
      def i = -1
      while(oDaily.find('from Dailylog')||oDaily.takeFromTempdailylog(iLimit)){
/*
		  if(rightNow>Tools.getIntVal(ConfigurationHolder.config.statistic.stop,6)){ 
		    log.debug("LOG>> Process statistic break HOUR_OF_DAY="+rightNow+" > "+Tools.getIntVal(ConfigurationHolder.config.statistic.stop,6))
		    break
		  }
*/		  
        if (++i>0) log.debug(i*iLimit+' processed >>'+new Date())
          oDaily.processDailyLog()
      }
      oDaily.truncateTempdailylog()
    }catch(Exception e){
      log.debug('Error in statistic job'+e.toString())
    }
    log.debug("LOG>> Statistic processed")
  }
}