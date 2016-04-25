//import org.codehaus.groovy.grails.commons.grailsApplication

class NoteMailerService {
  def mailerService
  def grailsApplication
	boolean transactional = true

	synchronized noteMailer(){

		def lsNoteByEmail =Notebyemail.findAllWhere(modstatus:0)

		def th=new Thread()
		th.start{
			Notebyemail.withTransaction { status ->
				lsNoteByEmail.each {nE->
					def oEmail_template = Email_template.get(nE.email_template_id?:0)

					if(oEmail_template){
						def mailText = oEmail_template.itext.replace('[@ID]',Home.get(nE.home_id?:0.toLong())?.linkname?:'')
							.replace('[@NAME]',Home.get(nE.home_id?:0.toLong())?.name?:'')
							.replace('[@USER]',User.get(nE.user_id?:0.toLong())?.nickname?:'')
						if(User.get(nE.user_id?:0.toLong())?.code?:''){
							mailText =mailText.replace(
								'[@URL]',(grailsApplication.config.grails.mailServerURL+((Tools.getIntVal(grailsApplication.config.isdev,0)==1)?("/"+grailsApplication.config.grails.serverApp):"")+'/user/confirm/'+User.get(nE.user_id?:0.toLong())?.code?:''))
						}
						def sHeader=oEmail_template?.title
						try{
              if(Tools.getIntVal(grailsApplication.config.mail_gae,0))
                mailerService.sendMailGAE(mailText,grailsApplication.config.grails.mail.from1,grailsApplication.config.grails.mail.username,nE.email,sHeader,1)        
              else{
							sendMail{
								to nE.email
								subject sHeader
								body( view:"/_mail",
									model:[mail_body:mailText])
                }   
							}
							nE.modstatus=1
							if (!nE.save(flush:true)){
								log.debug('error on save in Notebyemail:NoteMailService')
								nE.errors.each{log.debug(it)}
							}
						}catch(Exception e) {
							log.debug("Cannot sent email \n"+e.toString().replace("'","").replace('"','')+" in NoteMailService")
						}
						th.sleep(Tools.getIntVal(grailsApplication.config.notemail.delay,15) *1000)
					}
				}
			}
			log.debug("LOG>> NoteMailerJob: finish")
		}
	}
}