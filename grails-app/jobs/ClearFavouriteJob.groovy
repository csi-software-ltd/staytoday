import org.codehaus.groovy.grails.commons.ConfigurationHolder
class ClearFavouriteJob {
	static triggers = {
		//simple repeatInterval: 200000 // execute job once in 200 seconds
		cron cronExpression: ((ConfigurationHolder.config.clearfavourite.cron!=[:])?ConfigurationHolder.config.clearfavourite.cron:"0 0 2 * * ?")
	}

  def execute() { 
    def lsUsers=User.findAll("FROM User WHERE companylist IS NOT null AND companylist!=''")
	def bFlag=0
	def sCompanylist='' 
    for(oUser in lsUsers){ 
      bFlag=0  
	  sCompanylist='' 
	  for(id in oUser.companylist.split(',')){  	           						
        if(Home.findWhere(id:id.toLong()?:0.toLong(),modstatus:1)){		
		  if(sCompanylist)
		    sCompanylist+=','+id
		  else
		    sCompanylist=id
		}else{
		  bFlag=1
		  log.debug('User: '+oUser.id+' , remove id: '+id)
        }		
	  }
      if(bFlag){	  
	    oUser.companylist=sCompanylist
		if (!oUser.save(flush:true)){
          log.debug('error on save User in ClearFavouriteJob')
          oUser.errors.each{log.debug(it)}
        }
	  }		
    }    
  }
}
