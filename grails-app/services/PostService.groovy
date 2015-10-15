import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.MailSender

class PostService {
  def mailerService
  
  boolean transactional = true

  synchronized postMailer(){    
    def error = 0	
	
    def mailTemplate = Mailing_template.findAllByType_idAndModstatus(1,1)
	
    def th=new Thread()
    th.start{	
      mailTemplate.each {
	    Mailing_task.withTransaction { status ->
	    def oMailingTask = new Mailing_task(it.id,it.mtext)		  
	    oMailingTask.save(flush:true)
	  
  	  def mailQueue = Mailing_queue.findAllByTemplate_id(it.id) 	   	  
        	  
	    mailQueue.each { mQ ->
	      error = 0		
		
	      def mailText = it.mtext.replace('[@CONTACT]',mQ.contact).replace('[@NAME]',(mQ.name?mQ.name:'пользователь'))				
	      def sHeader=it.header
	      try{
                MailSender mailSender = new JavaMailSenderImpl()
                mailSender.host = ConfigurationHolder.config.grails.mail.host
                mailSender.port = Tools.getIntVal(ConfigurationHolder.config.grails.mail.port,465)
                mailSender.username = ConfigurationHolder.config.grails.mail.username2
                mailSender.password = ConfigurationHolder.config.grails.mail.password2
                mailSender.javaMailProperties =ConfigurationHolder.config.grails.mail.props			
		      mailSender.setDefaultEncoding("UTF-8");

		      def oItem=it
          if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
            mailerService.sendMailGAE(mailText,ConfigurationHolder.config.grails.mail.from2,ConfigurationHolder.config.grails.mail.username2,mQ.contact,sHeader,(oItem.is_destemp)?1:0)        
          else{      
            sendMail(mailSender) {		              
                  to mQ.contact         			  
                  subject sHeader		  
		            if(oItem.is_destemp)
		              body( view:"/_mail", model:[mail_body:mailText])
		            else  
		              html mailText                                                     
            }
		      }

        }catch(Exception e) {
		      log.debug("Cannot sent email \n"+e.toString().replace("'","").replace('"','')+" in PostService")
          error=1			
        }		  
        th.sleep(Tools.getIntVal(ConfigurationHolder.config.postmail.delay,15) *1000)			  
		  
	      mQ.moveToArchive(mQ.id,((error==0)?1:0),oMailingTask.id)
	      if(!error){
		      oMailingTask.updateNcount()
		      mQ.delete(flush:true)
	      }
	    }	   
	    oMailingTask.updateModstatusAndDate_end()	  	    
	  }
    }
      log.debug("LOG>> PostMailerJob: finish")	 
    }
  }  
}
