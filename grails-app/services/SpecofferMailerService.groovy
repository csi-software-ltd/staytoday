import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SpecofferMailerService {
  def mailerService
	boolean transactional = false
  grails.gsp.PageRenderer groovyPageRenderer

	synchronized mailer(){   
    def hsRes=[:]
    
    hsRes.popcity=City.findAllByIs_specofferAndHomecountGreaterThan(1,10,[max:9,sort:'homecount',order:'desc'])    
    hsRes.popdirection=Popdirection.findAllByIs_specofferAndModstatus(1,1,[sort:'country_id',order:'asc'])
    
    hsRes.imageurl = ConfigurationHolder.config.urlpopdiphoto
    def oValutarate = new Valutarate()
    hsRes.valutaRates = oValutarate.csiGetRate(857)
    hsRes.valutaSym = Valuta.get(857).symbol
    
    hsRes.stringlimit = Tools.getIntVal(ConfigurationHolder.config.smalltext.limit,220)
    if(hsRes?.user?.id)
      hsRes.user=User.get(hsRes?.user?.id)
    
    def oHomeSearch=new HomeSearch()       
    hsRes.avgPrice1=[]
    hsRes.avgPrice2=[]
    hsRes.minPrice=[]
    for(city in hsRes.popcity){
      def AVG1=oHomeSearch.csiGetAvgPriceByRoomTypeId(city.id,1)      
      AVG1=((AVG1?:[]).size()==1)?AVG1[0]:0
      def AVG2=oHomeSearch.csiGetAvgPriceByRoomTypeId(city.id,2)      
      AVG2=((AVG2?:[]).size()==1)?AVG2[0]:0
      def MinPrice=oHomeSearch.csiGetMinPriceByCity(city.id)
      MinPrice=((MinPrice?:[]).size()==1)?MinPrice[0]:0      
      
      hsRes.avgPrice1<<AVG1
      hsRes.avgPrice2<<AVG2
      hsRes.minPrice<<MinPrice                            
    } 
    
    hsRes.minPricePopdir=[]
    hsRes.hotdiscount=[]
    hsRes.longdiscount=[]
    hsRes.poprecords=[] 
    for(popdir in hsRes.popdirection){
      def reg_ids=[]
      def lsRegion=Region.findAllWhere(popdirection_id:popdir.id)
      for(region in lsRegion){
        reg_ids<<region.id
      }
      def minPricePopdir=oHomeSearch.csiGetMinPriceByRegion(reg_ids)
      minPricePopdir=((minPricePopdir?:[]).size()==1)?minPricePopdir[0]:0
      hsRes.minPricePopdir <<minPricePopdir 
      hsRes.hotdiscount << oHomeSearch.csiFindByWhere(popdir.keyword,Tools.getIntVal(ConfigurationHolder.config.discounts.quantity.max,-1),0,[order:-1],[hotdiscount:1],true,false).count
      hsRes.longdiscount << oHomeSearch.csiFindByWhere(popdir.keyword,Tools.getIntVal(ConfigurationHolder.config.discounts.quantity.max,-1),0,[order:-1],[longdiscount:1],true,false).count     
      def poprecords = oHomeSearch.csiFindPopdirection(popdir.id)
      hsRes.poprecords<<(poprecords?:[]).size()
    }

    
    hsRes.serverUrl=ConfigurationHolder.config.grails.serverURL?:''
    hsRes.notice=[]
   
    def i=0      
    for(notice in Notice.findAll("FROM Notice WHERE modstatus=1 ORDER BY rand()")){                    
      if(i<2)
        hsRes.notice<<notice                              
      i++        
    }      
    //log.debug('hsRes.notice='+hsRes.notice)                                  
    
    def oEmail_template = Email_template.findWhere(action:"#specoffer")        
    
    if(oEmail_template){
      def lsUsers=User.findAll("FROM User WHERE is_subscribe=1 AND modstatus!=-1")
      
      def sHeader=oEmail_template?.title            
      hsRes.itext=oEmail_template.itext      
      def sHtml =  groovyPageRenderer.render(view: "/_mail_specoffer", model: hsRes) 
      
      def th=new Thread()
		  th.start{		
        lsUsers.each {user->
          def mailText = sHtml.replace('[@NICKNAME]',user.nickname)
          
          try{
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              mailerService.sendMailGAE(mailText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,user.email,sHeader,0)        
            else{          
              sendMail{
                to user.email
                subject sHeader
                html mailText              
              }   
            }           
          }catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString().replace("'","").replace('"','')+" in SpecofferMailerService")
          }
          th.sleep(Tools.getIntVal(ConfigurationHolder.config.notemail.delay,15) *1000)
        }
      }
    }		       		
	}
}