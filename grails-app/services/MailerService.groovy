import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.context.i18n.LocaleContextHolder as LCH
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod

class MailerService {
    def messageSource
    def zayavkaService
    grails.gsp.PageRenderer groovyPageRenderer
    static transactional = false
  /*
  synchronized sendEmail() {
    for(def i=0;i<5;i++){
    try{ 
    sendMail{
      to 'test@test.ru'         
      subject 'sHeader'
      html 'sText'
      }
    }catch(Exception e) {
    log.debug("Cannot sent email \n"+e.toString())          
    }
    this.sleep(30000)
      log.debug("sent email: "+i)   
  } 
  }  
*/
  def zayavkaMailer(){
    def th=new Thread()
    th.start{
      synchronized(this) {
        def lsZayavkabyemail = Zayavkabyemail.findAllWhere(modstatus:0)
        Zayavkabyemail.withTransaction { status ->
          lsZayavkabyemail.each {zE->
            def oEmail_template = Email_template.findWhere(action:(zE.e_template?:'#emailProposal'))

            if(oEmail_template){
              def mailText = oEmail_template.itext.replace('[@CITY]',zE.city).replace('[@TEXT]',zE.ztext).replace(
                '[@D_ST]',String.format('%tY-%<tm-%<td',zE.date_start)).replace(
                '[@D_END]',String.format('%tY-%<tm-%<td',zE.date_end)).replace('[@NICKNAME]',zE.name)
              mailText = mailText.replace('[@URL]',(ConfigurationHolder.config.grails.mailServerURL+((Tools.getIntVal(ConfigurationHolder.config.isdev,0)==1)?("/"+ConfigurationHolder.config.grails.serverApp):"")+'/personal/waiting/'))
              def sHeader=oEmail_template?.title?.replace('[@NICKNAME]',zE.name)
              try{
                if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
                  sendMailGAE(mailText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,zE.email,sHeader,1)        
                else{
                sendMail{
                  to zE.email
                  subject sHeader
                  body( view:"/_mail",
                  model:[mail_body:mailText])
                  }
                }
                zE.modstatus=1
                zE.save(flush:true)
              }catch(Exception e) {
                log.debug("Cannot sent email \n"+e.toString().replace("'","").replace('"','')+" in MailerService")
              }
              th.sleep(Tools.getIntVal(ConfigurationHolder.config.notemail.delay,15) *1000)
            }
          }
        }
        log.debug("LOG>> ZayavkaMailerJob: finish")
      }
    }
  }

  def sendMboxFirstMails(oMbox,lUserClientId,oContext,isNeedFirstMail=false,isNeedSecondMail=false){
    def th=new Thread()
    th.start{
      synchronized(this) {
        def oUser = User.get(lUserClientId?:0)
        def oHome = Home.get(oMbox.home_id)
        def oClient = Client.get(oHome?.client_id)
        oClient = oClient.parent?Client.get(oClient.parent):oClient
        if (isNeedFirstMail) {
          //Email to owner>>
          def lsText=Email_template.findWhere(action:'#mbox_first')
          def sText='New message to [@HOME]'
          def sHeader="New message to [@HOME]"
          if(lsText){
            sText=lsText.itext
            sHeader=lsText.title
          }

          sText=sText.replace('[@NAME]',User.findWhere(client_id:oClient?.id?:0)?.nickname?:oClient?.name).replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
          .replace('[@NICKNAME]',oUser?.nickname?:'')//TODO!!!
          .replace('[@DATE_START]',String.format('%tY-%<tm-%<td',oMbox.date_start))
          .replace('[@DATE_END]',String.format('%tY-%<tm-%<td',oMbox.date_end))
          .replace('[@GUEST]',Homeperson.get(oMbox.homeperson_id)?.name)
          .replace('[@PRICE]',oMbox.price.toString())
          .replace('[@VALUTA]',Valuta.get(oMbox.valuta_id?:0).code)
          .replace('[@TEXT]',oMbox.mtext)

          sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
          sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/inbox/view/'+oMbox.id+">ссылке</a>")
          sHeader=sHeader.replace('[@NICKNAME]',oUser?.nickname?:'')

          try{
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oClient.name,sHeader,1)        
            else{
            sendMail{
              to oClient.name
              subject sHeader
              body( view:"/_mail",
              model:[mail_body:sText])
              }
            }
          }catch(Exception e) {
            log.debug("Cannot sent email to owner\n"+e.toString()+'in addmbox')
          }
          //<<Email to owner
        }
        if (isNeedSecondMail) {
          th.sleep(Tools.getIntVal(ConfigurationHolder.config.notemail.delay,15) *1000)
          //Email to client user>>
          def lsText=Email_template.findWhere(action:'#mbox_newUser')
          def sText='New message to [@NICKNAME] and [@PASSWORD]'
          def sHeader="New message to [@NICKNAME]"
          if(lsText){
            sText=lsText.itext
            sHeader=lsText.title
          }

          sText=sText.replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
            .replace('[@NICKNAME]',oUser?.nickname?:'')//TODO!!!
            .replace('[@DATE_START]',String.format('%tY-%<tm-%<td',oMbox.date_start))
            .replace('[@DATE_END]',String.format('%tY-%<tm-%<td',oMbox.date_end))
            .replace('[@GUEST]',Homeperson.get(oMbox.homeperson_id)?.name)
            .replace('[@PRICE]',oMbox.price.toString())
            .replace('[@VALUTA]',Valuta.get(oMbox.valuta_id?:0).code)
            .replace('[@TEXT]',oMbox.mtext)
            .replace('[@PASSWORD]',Tools.hidePsw(oUser.email)[0..8])
            .replace('[@EMAIL]',oUser.email)

          //sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
          sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/user/passwconfirm/'+oUser.code+">ссылке</a>")
          sHeader=sHeader.replace('[@NICKNAME]',oUser?.nickname?:'')

          try{
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
            else{
            sendMail{
              to oUser.email
              subject sHeader
              body( view:"/_mail",
              model:[mail_body:sText])
              }
            }
          }catch(Exception e) {
            log.debug("Cannot sent email to client user\n"+e.toString()+'in addmbox')
          }
          //<<Email to client user
        }
      }
    }
  }

  def sendChangePayoutSchemeMails(oClient,oUser){
    def th=new Thread()
    th.start{
      synchronized(this) {
        //Email to client>>
        def lsText=Email_template.findWhere(action:'#change_payout_scheme')
        def sText='Request for change payout scheme'
        def sHeader='Request for change payout scheme'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }

        sText=sText.replace('[@NICKNAME]',oUser?.nickname)
        .replace('[@ACTION]',oClient?.resstatus==2?messageSource.getMessage('account.payout.resstatus.type2', null, LCH.getLocale()):messageSource.getMessage('account.payout.resstatus.type3', null, LCH.getLocale()))
        .replace('[@COUNTRY]',Country.get(oClient?.country_id?:0)?.name?:'')
        .replace('[@VALUTA]',Valuta.get(oClient?.valuta_id?:0)?.name?:'')
        .replace('[@ACCTYPE]',Payaccount.get(oClient?.payaccount_id?:0)?.name?:'')
        .replace('[@RESERVE]',Reserve.get(oClient?.reserve_id?:0)?.name?:'')
        .replace('[@AUTO]',oClient?.is_transferauto?oClient.is_transferauto.toString():'0')

        sHeader=sHeader.replace('[@NICKNAME]',oUser?.nickname?:'')

        try{
          if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
            sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
          else{
          sendMail{
            to oUser.email
            subject sHeader
            body( view:"/_mail",
            model:[mail_body:sText])
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in savepayoutdetails')
        }
        //<<Email to client
        if (!Zayavkabyemail.findByCityAndModstatus((oClient?.id?:0).toString(),0)) {
          def oAdmin = Admin.get(Tools.getIntVal(ConfigurationHolder.config.notifyAdmin.id,2))
          if (oAdmin?.email&&oClient)
            Zayavkabyemail.withTransaction { status ->
              (new Zayavkabyemail(oAdmin.email,oAdmin.name,oClient.id,'',new Date(),new Date(),'#changePayoutNotice')).save(flush:true)
            }
        }
      }
    }
  }

  def closeZayavkaMail(oZayavka){
    def th=new Thread()
    th.start{
      synchronized(this) {
        def oEmail_template = Email_template.findWhere(action:'#closeZayavka')
        def oUser = User.get(oZayavka?.user_id?:0)
        if(oEmail_template&&oUser?.email){
          def mailText = oEmail_template.itext.replace('[@CITY]',oZayavka.city).replace(
            '[@D_ST]',String.format('%tY-%<tm-%<td',oZayavka.date_start)).replace(
            '[@D_END]',String.format('%tY-%<tm-%<td',oZayavka.date_end)).replace('[@NICKNAME]',oUser.nickname)
          def sHeader=oEmail_template?.title?.replace('[@NICKNAME]',oUser.nickname)
          try{
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(mailText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
            else{
            sendMail{
              to oUser.email
              subject sHeader
              body( view:"/_mail",
              model:[mail_body:mailText])
              }
            }
          }catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString().replace("'","").replace('"','')+" in MailerService")
          }
        } else { log.debug("Cannot sent email in closeZayavkaMail\n\t User.email or Email_template was not found") }
      }
    }
  }

  def sendAdminNoticeMail(_oNotice){
    try{
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(_oNotice.message,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,_oNotice.contact,'Error on staytoday',1)        
      else{
      sendMail{
        to _oNotice.contact
        subject 'Error on staytoday'
        body( view:"/_mail",
          model:[mail_body:_oNotice.message])
        }  
      }
    }catch(Exception e) {
      log.debug("Cannot sent email \n"+e.toString())
    }
  }

  def sendDeclineHomeMail(oHome, oUser, oContext){
    def lsText=Email_template.findWhere(action:'#declineHome')
    def sText='[@NICKNAME], your [@HOME] was declined at Staytoday. You may see more details, if follow this link [@URL]'
    def sHeader="Decline your Home at Staytoday"
    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }
    sText=sText.replace(
      '[@NICKNAME]',oUser?.nickname?:'').replace(
      '[@HOME]',oHome?.name?:'').replace(
      '[@EMAIL]',oUser?.email?:'').replace(
      '[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/personal/adsoverview/'+oHome.id+">"+oHome.name+"</a>")
      sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
      sHeader=sHeader.replace(
      '[@EMAIL]',oUser?.email?:'').replace(
      '[@HOME]',oHome?.name?:'').replace(
      '[@NICKNAME]',oUser?.nickname?:'').replace(
      '[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/personal/adsoverview/'+oHome.id+">"+oHome.name+"</a>")

    try{
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
      else{
      sendMail{
        to oUser?.email
        subject sHeader
        body( view:"/_mail",
        model:[mail_body:sText])
        }
      }
    }catch(Exception e) {
      log.debug("Cannot sent email \n"+e.toString())
    }
  }

  def sendChangeCommentsMail(oHome, oUser, oContext){
  def lsText=Email_template.findWhere(action:'#commentsHome')
  def sText='[@NICKNAME], moderator add some comments to your [@HOME]. You may see more details, if follow this link [@URL]'
  def sHeader="Moderator add some comments to your Home"
  if(lsText){
    sText=lsText.itext
    sHeader=lsText.title
  }
  sText=sText.replace(
    '[@NICKNAME]',oUser?.nickname?:'').replace(
    '[@HOME]',oHome?.name?:'').replace(
    '[@EMAIL]',oUser?.email?:'').replace(
    '[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/personal/adsoverview/'+oHome.id+">"+oHome.name+"</a>").replace(
    '[@COMMENTS]',oHome?.comments?:'')
    
    sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
    sHeader=sHeader.replace(
    '[@EMAIL]',oUser?.email?:'').replace(
    '[@HOME]',oHome?.name?:'').replace(
    '[@NICKNAME]',oUser?.nickname?:'').replace(
    '[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/personal/adsoverview/'+oHome.id+">"+oHome.name+"</a>")

  try{
    if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
      sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
    else{
    sendMail{
      to oUser?.email
      subject sHeader
        body( view:"/_mail",
        model:[mail_body:sText])
      }  
    }
  }catch(Exception e) {
    log.debug("Cannot sent email \n"+e.toString())
  }
  }

  def sendAcceptHomeMail(oHome, oUser, oContext){
  def lsText=Email_template.findWhere(action:'#acceptHome')
  def sText='[@NICKNAME], your [@HOME] was accepted at Arenda. You may see more details, if follow this link [@URL]'
  def sHeader="Accepted your Home at Arenda"
  if(lsText){
    sText=lsText.itext
    sHeader=lsText.title
  }
  sText=sText.replace(
    '[@NICKNAME]',oUser?.nickname?:'').replace(
    '[@HOME]',oHome?.name?:'').replace(
    '[@EMAIL]',oUser?.email?:'').replace(
    '[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/personal/adsoverview/'+oHome.id+">"+oHome.name+"</a>")
    sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
    sHeader=sHeader.replace(
    '[@EMAIL]',oUser?.email?:'').replace(
    '[@HOME]',oHome?.name?:'').replace(
    '[@NICKNAME]',oUser?.nickname?:'').replace(
    '[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/personal/adsoverview/'+oHome.id+">"+oHome.name+"</a>")

  try{
    if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
      sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
    else{
    sendMail{
    to oUser?.email
    subject sHeader
        body( view:"/_mail",
        model:[mail_body:sText])
      }  
    }
  }catch(Exception e) {
    log.debug("Cannot sent email \n"+e.toString())
  }
  }

  def sendCancelBronMail(oMbox, oUser, lMboxrec_id, oContext){
    def lsText=Email_template.findWhere(action:'#mbox_cancel_bron_client')
    def sText='Отмена бронирования [@HOME]'
    def sHeader="Отмена бронирования [@HOME]" 
    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }
    def oHome=Home.get(oMbox.home_id)
    def oClient=Client.get(oHome?.client_id)
    oClient=oClient.parent?Client.get(oClient.parent):oClient
    sText=sText.replace(
      '[@NAME]',oClient?.name).replace(
      '[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>").replace(
      '[@NICKNAME]',oUser?.nickname?:'')                            
    sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText

    sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/inbox/view/'+oMbox.id+">ссылке</a>")
    sHeader=sHeader.replace('[@HOME]',oHome.name)
    sText+=mail_history(oMbox.id,lMboxrec_id,oHome.name)

    try{
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oClient.name,sHeader,1)        
      else{      
      sendMail{
        to oClient.name         
        subject sHeader
        body( view:"/_mail",
          model:[mail_body:sText])
        }
      }
      if(oUser?.email){
        if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
          sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
        else{
        sendMail{
          to oUser.email
          subject sHeader
          body( view:"/_mail",
            model:[mail_body:sText])
          }  
        }
      }  
    }catch(Exception e) {
      log.debug("Cannot sent email \n"+e.toString()+'in addmbox')
    }
  }

  def sendOwnerCancelBronMail(oMbox, oUser, lMboxrec_id, oContext){
    def lsText=Email_template.findWhere(action:'#mbox_cancel_bron_owner')
    def sText='Отмена бронирования [@HOME]'
    def sHeader="Отмена бронирования [@HOME]" 
    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }
    def oHome=Home.get(oMbox.home_id)
    def oClient=Client.get(oHome?.client_id)
    def sNickname = User.findByClient_id(oHome?.client_id)?.nickname
    oClient=oClient.parent?Client.get(oClient.parent):oClient
    sText=sText.replace(
      '[@NAME]',oClient?.name).replace(
      '[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>").replace(
      '[@NICKNAME]',sNickname?:'')
    sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText

    sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/inbox/view/'+oMbox.id+">ссылке</a>")
    sHeader=sHeader.replace('[@HOME]',oHome.name)
    sText+=mail_history(oMbox.id,lMboxrec_id,oHome.name)

    try{
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oClient.name,sHeader,1)        
      else{      
      sendMail{
        to oClient.name         
        subject sHeader
        body( view:"/_mail",
          model:[mail_body:sText])
        }  
      }
      if(oUser?.email){
        if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
          sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
        else{ 
        sendMail{
          to oUser.email
          subject sHeader
          body( view:"/_mail",
            model:[mail_body:sText])
          }  
        }
      }  
    }catch(Exception e) {
      log.debug("Cannot sent email \n"+e.toString()+'in addmbox')
    }
  }

  def sendActivationMail(oUser, oContext){
    def lsText=Email_template.findWhere(action:'#activation')
    def sText='[@EMAIL], for activation of your account use follow link [@URL]'
    def sHeader="Registration at Arenda" 
    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }
    sText=sText.replace(
      '[@NICKNAME]',oUser.nickname).replace(
      '[@EMAIL]',oUser.email).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/user/confirm/'+oUser.code))
    sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
    sHeader=sHeader.replace(
      '[@EMAIL]',oUser.email).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+'/user/confirm/'+oUser.code))

    try{
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
      else{
      sendMail{
        to oUser.email
        subject sHeader
        body( view:"/_mail",
          model:[mail_body:sText])
        }
      }
      return 0
    }catch(Exception e) {
      log.debug("Cannot sent email in sendActivationMail \n"+e.toString())
      return -100
    }
  }
  
  def sendActivationProposalMail(oUser, oContext, bNewUser){ 
    def lsText=Email_template.findWhere(action:'#activationProposal')
    def sText='[@EMAIL], for activation of your proposal use follow link [@URL]'
    def sHeader="Registration at Arenda"

    if(bNewUser){
      lsText=Email_template.findWhere(action:'#activationProposal_newUser')
    }

    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }
    sText=sText.replace(
      '[@NICKNAME]',oUser.nickname).replace(
      '[@EMAIL]',oUser.email).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/user/confirm/'+oUser.code))
    //sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
    sHeader=sHeader.replace(
      '[@EMAIL]',oUser.email)
    if(bNewUser)
      sText=sText.replace('[@PASSWORD]',Tools.hidePsw(oUser.email)[0..8])
        .replace('[@EMAIL]',oUser.email)
        .replace('[@URL_PASSW_CHANGE]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/user/passwconfirm/'+oUser.code+">ссылке</a>")

    try{
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
      else{
      sendMail{
        to oUser.email
        subject sHeader
        body( view:"/_mail",
          model:[mail_body:sText])
        }  
      }
      return 1
    }catch(Exception e) {
      log.debug("Cannot sent email in sendActivationMail \n"+e.toString())
      return -1
    }
  }

  def sendProposalMail(oUser, oContext){
    def lsText=Email_template.findWhere(action:'#proposal')
    def sText='[@EMAIL], Tnx for adding your proposal'
    def sHeader="Registration at Arenda"
    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }
    sText=sText.replace(
      '[@NICKNAME]',oUser.nickname).replace(
      '[@EMAIL]',oUser.email).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/user/confirm/'+oUser.code))
    sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
    sHeader=sHeader.replace(
      '[@EMAIL]',oUser.email).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+'/user/confirm/'+oUser.code))

    try{
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
      else{
      sendMail{
        to oUser.email
        subject sHeader
        body( view:"/_mail",
          model:[mail_body:sText])
        }  
      }
      return 1
    }catch(Exception e) {
      log.debug("Cannot sent email in sendActivationMail \n"+e.toString())
      return -1
    }
  }
  
  def sendAdminZayavkaMail(iZayavkaId){
    def oAdmin = Admin.get(Tools.getIntVal(ConfigurationHolder.config.notifyAdmin.id,2))
    if (oAdmin?.email){
      def lsText=Email_template.findWhere(action:'#zayavkaAdminNotify')
      def sText='Новая заявка id=[@ID]'
      def sHeader="Новая заявка id=[@ID]" 
      if(lsText){
        sText=lsText.itext
        sHeader=lsText.title
      }
      sText=sText.replace('[@ID]',iZayavkaId.toString())
      sHeader=sHeader.replace('[@ID]',iZayavkaId.toString())

      try{
        if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
          sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oAdmin?.email,sHeader,1)        
        else{
        sendMail{
          to oAdmin?.email
          subject sHeader
          html sText
          }
        }
        return 1
      }catch(Exception e) {
        log.debug("Cannot sent email in sendAdminZayavkaMail \n"+e.toString())
        return -1
      }
    }
  }

  void sendAdminMboxMail(lMboxId){
    def th=new Thread()
    th.start{
      synchronized(this) {
        def oAdmin = Admin.get(Tools.getIntVal(ConfigurationHolder.config.notifyAdmin.id,2))
        if (oAdmin?.email){
          def lsText=Email_template.findWhere(action:'#mboxAdminNotify')
          def sText='Новый запрос id=[@ID]'
          def sHeader="Новый запрос id=[@ID]"
          if(lsText){
            sText=lsText.itext
            sHeader=lsText.title
          }
          sText=sText.replace('[@ID]',lMboxId.toString())
          sHeader=sHeader.replace('[@ID]',lMboxId.toString())

          try{
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oAdmin?.email,sHeader,1)
            else{
              sendMail{
                to oAdmin?.email
                subject sHeader
                html sText
              }
            }
          }catch(Exception e) {
            log.debug("Cannot sent email in sendAdminMboxMail \n"+e.toString())
          }
        }
      }
    }
  }

  def sendZayavkaResponseMail(oMbox, oMboxrec, oContext){
    def lsText=Email_template.findWhere(action:'#zayavka_spec')
    def sText='Ссылка на объявление- [@HOME].<br/> Тест сообщения: [@TEXT].<br/>Для ответа на заявку перейдите по [@URL]'
    def sHeader="Собщение по объявлению [@HOME]"
    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }

    def oUser=User.get(oMbox.user_id)
    def sNickname=oUser?.nickname

    def oHome=Home.get(oMbox.home_id)

    sText=sText.replace('[@NICKNAME]',sNickname)
      .replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
      .replace('[@HOMESPEC]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
      .replace('[@SPEC_DATE_START]',String.format('%tY-%<tm-%<td',oMboxrec.date_start))
      .replace('[@SPEC_DATE_END]',String.format('%tY-%<tm-%<td',oMboxrec.date_end))
      .replace('[@SPEC_HOMEPERSON]',Homeperson.get(oMboxrec.homeperson_id)?.name?:'')
      .replace('[@SPEC_PRICE]',oMboxrec.price.toString())
      .replace('[@SPEC_VALUTA]',Valuta.get(oMboxrec.valuta_id?:0).code)
    sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText

    sHeader=sHeader.replace('[@HOME]',oHome.name)
    sText+=mail_history(oMbox.id,oMboxrec.id,oHome.name)

    try{
      if(oUser?.email){
        if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
          sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
        else{
        sendMail{
          to oUser?.email
          subject sHeader
          body( view:"/_mail",
            model:[mail_body:sText])
          }  
        }
      }  
    }catch(Exception e) {
      log.debug("Cannot sent email \n"+e.toString()+'in addmbox')
    }
  }

  void addanswermail(oMbox,oMboxrec,oContext){
    def th=new Thread()
    th.start{
      synchronized(this) {
        def oUser_reciever
        def oClient_reciever
        if(!oMbox.is_answer || User.get(oMbox.user_id)?.email){
          def oHome=Home.get(oMbox.home_id)
          //<<Email
          def sMailTo=''
          def sNickname=''
          def sText='Ссылка на объявление- [@HOME].<br/> Тест сообщения: [@TEXT].<br/>Для ответа на заявку перейдите по [@URL]'
          def sHeader="Собщение по объявлению [@HOME]" 
          def lsText=[]
          if(oMboxrec.is_answer){//from owner
            if(oMboxrec.answertype_id in [3,5,6])
              lsText=Email_template.findWhere(action:'#mbox_from_owner')
            else if(oMboxrec.answertype_id in 1..2)
              lsText=Email_template.findWhere(action:'#mbox_spec')
            else if(oMboxrec.answertype_id==4)//bron'
              lsText=Email_template.findWhere(action:'#mbox_from_owner_max_min_days')

            def oUser=User.get(oMbox.user_id)
            sMailTo=oUser?.email
            sNickname=oUser?.nickname
            oUser_reciever=oUser
          }else{//from client
            lsText=Email_template.findWhere(action:'#mbox_from_client')
            def oClient=Client.get(oHome?.client_id?:0)
            oClient=oClient.parent?Client.get(oClient.parent):oClient
            sMailTo=oClient?.name
            sNickname=User.findByClient_id(oClient?.id?:0l)?.nickname?:'владелец'
            oClient_reciever=oClient          
          }

          if(lsText){
            sText=lsText.itext
            sHeader=lsText.title
          }

          if(oMboxrec.is_answer){//from owner
            def resstatModifier = 1.0
            if (Client.get(oMbox.homeowner_cl_id)?.resstatus==1) {
              resstatModifier = resstatModifier + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100)
            }
            def oAnswertype=Answertype.get(oMboxrec.answertype_id)
            if(oMboxrec.answertype_id==6){
              sText=sText.replace('[@NICKNAME]',sNickname).replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
              .replace('[@ANSWER_TYPE_SHORTNAME]',oAnswertype?.shortname?:'')
              .replace('[@TEXT]',oMboxrec.rectext?:'')     
              sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
              sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/inbox/view/'+oMbox.id+">ссылке</a>") 
            }else if(oMboxrec.answertype_id in 1..2){
              def oHomeSpec=Home.get(oMbox.home_id)
              sText=sText.replace('[@NICKNAME]',sNickname)
              .replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
              .replace('[@HOMESPEC]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHomeSpec.linkname+">"+oHomeSpec.name+"</a>")
              .replace('[@SPEC_DATE_START]',String.format('%td/%<tm/%<tY',oMboxrec.date_start))
              .replace('[@SPEC_DATE_END]',String.format('%td/%<tm/%<tY',oMboxrec.date_end))
              .replace('[@SPEC_HOMEPERSON]',Homeperson.get(oMboxrec.homeperson_id)?.name?:'')
              .replace('[@SPEC_PRICE]',(Math.round(oMboxrec.price*resstatModifier).toString()))
              .replace('[@SPEC_VALUTA]',Valuta.get(oMboxrec.valuta_id?:0).code)
              .replace('[@COMMENTS]',oMboxrec.rectext?:'')
              sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/inbox/view/'+oMbox.id+">ссылке</a>")
            }else if(oMboxrec.answertype_id==3 || oMboxrec.answertype_id==5){
              sText=sText.replace('[@NICKNAME]',sNickname).replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")     
              .replace('[@ANSWER_TYPE_SHORTNAME]',oAnswertype?.shortname?:'')
              .replace('[@TEXT]',oMboxrec.rectext?:'')//TODO!!               
              sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
              sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/inbox/view/'+oMbox.id+">ссылке</a>") 
            }else if(oMboxrec.answertype_id==4){
              sText=sText.replace('[@NICKNAME]',sNickname).replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")     
              .replace('[@MIN_DAY]',Rule_minday.get(oMboxrec.rule_minday_id?:0)?.name?:'') 
              .replace('[@MAX_DAY]',Rule_maxday.get(oMboxrec.rule_maxday_id?:0)?.name?:'')
              .replace('[@ANSWER_TYPE_SHORTNAME]',oAnswertype?.shortname?:'')
              .replace('[@TEXT]',oMboxrec.rectext?:'')//TODO!!     
              sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
              sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/inbox/view/'+oMbox.id+">ссылке</a>") 
            }
            def sOffers = ''
            if (oMboxrec.answertype_id in 3..5) {
              //doppredlogenia
              Mbox.withNewSession{
                def oAddrecords = zayavkaService.csiFindClient([region_id:oHome.region_id,date_start:oMbox.date_start,date_end:oMbox.date_end,
                                                                pricefrom_rub:oHome.csiGetSomePrice().toInteger(),priceto_rub:(oHome.csiGetSomePrice().toInteger()),
                                                                homeperson_id:oMbox.homeperson_id,hometype_id:oHome.hometype_id,city:oHome.city],[oHome.client_id])
                if (oAddrecords.records.size()>0)
                  sOffers = getAddOffer(oAddrecords.records,oContext)
              }
            }
            sText = sText.replace('[@ADDOFFER]',sOffers)
          }else{//from client
            sText=sText.replace('[@NICKNAME]',sNickname).replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
            .replace('[@TEXT]',oMboxrec.rectext?:'')     
            sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
            sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/inbox/view/'+oMbox.id+">ссылке</a>")      
          }
          sText+=mail_history(oMbox.id,oMboxrec.id,oHome.name)
          sHeader=sHeader.replace('[@HOME]',oHome.name)
          try{
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,sMailTo,sHeader,1)
            else{
              sendMail{
                to sMailTo
                subject sHeader
                body( view:"/_mail",
                  model:[mail_body:sText])
              }
            }
          }catch(Exception e) {
            log.debug("Cannot sent email \n"+e.toString()+'in addanswermail')
          }
        }
  //>>Email
  //GCM>>
        Long lClientId = 0
        if (oUser_reciever?.id) {
          if(oUser_reciever?.client_id)
            lClientId=oUser_reciever?.client_id
          else if(oUser_reciever?.ref_id)
            lClientId=User.get(oUser_reciever?.ref_id)?.client_id?:0
        }else if (oClient_reciever?.id){
          lClientId=oClient_reciever?.id
        }
        def msg_unread_count = Mbox.executeQuery('select count(*) from Mbox where is_read=0 and ((homeowner_cl_id=:cl_id and is_answer=0 and is_approved=1) or (user_id=:u_id and is_answer=1)) and not(modstatus=6 or ((homeowner_cl_id=:cl_id and IFNULL(is_owfav,0)=-1)or(user_id=:u_id and IFNULL(is_clfav,0)=-1)))',[cl_id:lClientId,u_id:oUser_reciever?.id?:0.toLong()])[0]

        def sendGCM=[:]
        sendGCM.message='letter'
        sendGCM.msgcnt=msg_unread_count.toString()

        def lsDevices=[]
        if(oUser_reciever?.id){
          lsDevices=Device.findAllWhere(user_id:oUser_reciever?.id)
        } else if(lClientId){
          def lsUsers=User.findAllWhere(client_id:lClientId)
          def user_ids=[]
          for(user in lsUsers)
            user_ids<<user.id
          lsDevices=Device.findAll("FROM Device WHERE user_id IN (:user_ids)",[user_ids:user_ids])
        }

        if(lsDevices){
          def lsDevices_ids=[]

          for(device in lsDevices)
            lsDevices_ids<<device.device
          if(lsDevices_ids)
            androidGcmService.sendMessage(sendGCM,lsDevices_ids,'message', grailsApplication.config.android.gcm.api.key ?: '')
        }
  //GCM<<
      }
    }
  }

  void noanswermail(oMbox){
    def th=new Thread()
    th.start{
      synchronized(this) {
        Mbox.withNewSession{
          def oHome=Home.get(oMbox.home_id)
          def oUser=User.get(oMbox.user_id)
          def oContext = [is_dev:Tools.getIntVal(ConfigurationHolder.config.isdev,0),appname:ConfigurationHolder.config.grails.serverApp]
          def oAddrecords = zayavkaService.csiFindClient([region_id:oHome.region_id,date_start:oMbox.date_start,date_end:oMbox.date_end,
                                                          pricefrom_rub:oHome.csiGetSomePrice().toInteger(),priceto_rub:(oHome.csiGetSomePrice().toInteger()),
                                                          homeperson_id:oMbox.homeperson_id,hometype_id:oHome.hometype_id,city:oHome.city],[oHome.client_id])
          if (oAddrecords.records.size()>0&&oUser.email){
            //<<Email
            def sText='[@ADDOFFER]'
            def sHeader="Нет ответа от владельца"
            def lsText=Email_template.findWhere(action:'#mbox_noanswer')
            if(lsText){
              sText=lsText.itext
              sHeader=lsText.title
            }

            def sOffers = getAddOffer(oAddrecords.records,oContext)
            sText = sText.replace('[@ADDOFFER]',sOffers)
            try{
              if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
                sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)
              else{
                sendMail{
                  to oUser.email
                  subject sHeader
                  body( view:"/_mail",
                    model:[mail_body:sText])
                }
              }
            }catch(Exception e) {
              log.debug("Cannot sent email \n"+e.toString()+'in noanswermail')
            }
          }
          //>>Email
        }
      }
    }
  }

  void offernotpaidmail(oMbox){
    def th=new Thread()
    th.start{
      synchronized(this) {
        Mbox.withNewSession{
          def oUser=User.get(oMbox.user_id)
          def oContext = [is_dev:Tools.getIntVal(ConfigurationHolder.config.isdev,0),appname:ConfigurationHolder.config.grails.serverApp]
          if (oUser?.email){
            //<<Email
            def sText='К сожалению, время, отведенное на бронирование жилья по вашему запросу, истекло. Если вы хотели бы вернуться к вопросу бронирования этого предложения, вам надо сделать повторный запрос на бронирование.'
            def sHeader="Бронь не оплачена"
            def lsText=Email_template.findWhere(action:'#mbox_offernotpaid')
            if(lsText){
              sText=lsText.itext
              sHeader=lsText.title
            }
            //instead of requestService>>
            def appname=ConfigurationHolder.config.grails.serverApp
            def is_dev=Tools.getIntVal(ConfigurationHolder.config.isdev,0)
            //<<
            
            def oHome=Home.get(oMbox?.home_id)
            sText=sText
            .replace('[@NICKNAME]',oUser?.nickname)
            .replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((is_dev)?"/${appname}":"")+'/home/view/'+oHome?.linkname+">"+oHome?.name+"</a>")
            .replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((is_dev)?"/${appname}":"")+'/inbox/view/'+oMbox?.id+">ссылке</a>")

            try{
              if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
                sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)
              else{
                sendMail{
                  to oUser.email
                  subject sHeader
                  body( view:"/_mail",
                    model:[mail_body:sText])
                }
              }
            }catch(Exception e) {
              log.debug("Cannot sent email \n"+e.toString()+'in offernotpaidmail')
            }
            //>>Email
          }
        }
      }
    }
  }

  def getAddOffer(lsRecords,oContext){
    def sText = '<br/>Вы можете сделать запрос на аренду по следующим альтернативным вариантам:<br/>[OFFERLINKS]<br/>'
    def sAddoffer = Email_template.findWhere(action:'#mbox_addoffer').itext
    sText = sAddoffer?:sText
    def sLinks = ''
    (lsRecords.size()<=5?lsRecords.size():5).times{
      sLinks += "<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+lsRecords[it].linkname+">"+lsRecords[it].name+"</a><br/>"
    }
    sText = sText.replace('[OFFERLINKS]',sLinks)
    return sText
  }
  //Alex>>
  def mail_history(MboxId,MboxrecId,sHomeName){
    def sText='===============<br/>История переписки:<br/>'
    def sHistoryText=Email_template.findWhere(action:'#mbox_history').itext
    def oMbox=Mbox.get(MboxId)
    def oMboxrecMain=Mboxrec.get(MboxrecId)
    for(oMboxrec in Mboxrec.findAll('FROM Mboxrec WHERE mbox_id=:mbox_id AND ((admin_id=0 AND ((is_answer=:isAnswer AND is_approved=1) OR is_answer!=:isAnswer)) OR (admin_id>0 AND is_answer=:isAnswer AND is_approved=1)) AND id!=:mbr_id ORDER BY id DESC',[mbox_id:MboxId,mbr_id:MboxrecId,isAnswer:oMboxrecMain.is_answer])){
      def oAnswertype=Answertype.get(oMboxrec.answertype_id)
      sText+=sHistoryText.replace('[@NICKNAME]',(oMboxrec.is_answer)?User.get(Mbox.get(oMboxrec.mbox_id)?.user_id)?.nickname:'&laquo;'+sHomeName+'&raquo;')
        .replace('[@FROM]',(oMboxrec.is_system)?'&laquo;Staytoday.ru&raquo;':(oMboxrec.is_answer)?'&laquo;'+sHomeName+'&raquo;':User.get(oMbox.user_id)?.email?:'')
        .replace('[@DATE]',String.format('%tY-%<tm-%<td %<tH:%<tM',oMboxrec.inputdate))
        .replace('[@TEXT]',oMboxrec.rectext?:'')
        .replace('[@ANSWER_TYPE_SHORTNAME]',oAnswertype?.shortname?:'')
        .replace('[@ANSWER_TYPE_NAME]',oAnswertype?.name?:'')
    }
    return sText
  }
  ////////////////////////////////////////////////////////////////////////////////
  def mbox_bron_client(lUserId,lHomeId,lMboxId,lMboxrecId,lsContext){
    def oUser=User.get(lUserId?:0.toLong()) 
    def lsText=Email_template.findWhere(action:'#mbox_bron_client')
    def sText='Бронирование [@HOME]'
    def sHeader="Бронирование [@HOME]" 
    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }
    def oHome=Home.get(lHomeId)     
    def oClient=Client.get(oHome?.client_id)
    oClient=oClient.parent?Client.get(oClient.parent):oClient     
          
    sText=sText.replace('[@NAME]',oClient?.name)
      .replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((lsContext.is_dev)?"/${lsContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
      .replace('[@NICKNAME]',oUser?.nickname?:'')
    sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText

    sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((lsContext.is_dev)?"/${lsContext.appname}":"")+'/inbox/view/'+lMboxId+">ссылке</a>")
                
    sHeader=sHeader.replace('[@HOME]',oHome.name)

    sText+=mail_history(lMboxId,lMboxrecId,oHome.name)      
           
    try{ 
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oClient.name,sHeader,1)        
      else{
      sendMail{
       to oClient.name         
        subject sHeader
        body( view:"/_mail",
        model:[mail_body:sText])
        }
      }       
      if(oUser?.email){ 
        if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
          sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
        else{        
        sendMail{
          to oUser.email        
          subject sHeader
          body( view:"/_mail",
        model:[mail_body:sText])
          }
        }
      }        
    }catch(Exception e) {
      log.debug("Cannot sent email \n"+e.toString()+'in setMboxBron')
      //flash.error<<-100 
    }    
  }
  ////////////////////////////////////////////////////////////////////////////////
  def addcomment(oHome,lsContext){
    def lsText=Email_template.findWhere(action:'#homeReview')
    def sText='Новый  отзыв по [@HOME]'
    def sHeader="Новый  отзыв по [@HOME]" 
    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }                   
        
    def sUrl=ConfigurationHolder.config.grails.mailServerURL.toString()+((lsContext.is_dev)?"/${lsContext.appname}":"")+"/home/view/${oHome.linkname}"

    sText=sText.replace('[@HOME]','&laquo;'+oHome.name+'&raquo;')
      .replace('[@URL]',"<a href="+sUrl+">"+oHome.name+"</a>")     
                                                          
    sHeader=sHeader.replace('[@HOME]',oHome.name)

  def oClient=Client.get(oHome?.client_id?:0)     
    oClient=oClient.parent?Client.get(oClient.parent):oClient
      
    try{ 
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oClient.name,sHeader)        
      else{
      sendMail{
        to oClient.name         
        subject sHeader
        body( view:"/_mail",
        model:[mail_body:sText])
        }
      }
    }catch(Exception e) {
      log.debug("Cannot sent email \n"+e.toString()+'in addcomment')         
    } 
  }
////////////////////////////////////////////////////////////////////////////////  
  def addhome(lUserId,sCode,lsContext){
    def oUser=User.get(lUserId?:0.toLong())
    def lsText=Email_template.findWhere(action:'#activation')
    def sText='[@EMAIL], for activation of your account use follow link [@URL]'
    def sHeader="Registration at Arenda" 
    if(lsText){
      sText=lsText.itext
      sHeader=lsText.title
    }
    sText=sText.replace(
      '[@NICKNAME]',oUser?.nickname).replace(
      '[@EMAIL]',oUser?.email).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+((lsContext.is_dev)?"/${lsContext.appname}":"")+'/user/confirm/'+sCode))
    sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
    sHeader=sHeader.replace(
      '[@EMAIL]',oUser?.email).replace(
      '[@URL]',(ConfigurationHolder.config.grails.mailServerURL+'/user/confirm/'+sCode))
      
    try{
      if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
        sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser?.email,sHeader)        
      else{    
      sendMail{
        to oUser?.email         
        subject sHeader
        body( view:"/_mail",
        model:[mail_body:sText])
        }
      }
    }catch(Exception e) {
      log.debug("Cannot sent email \n"+e.toString()+' in addhome')
     //flash.error<<-100 
    }  
  } 
  
  def sendMailGAE(htmlContent,from,sender,to,subject,isTemplate=0){
    HttpClient client = new HttpClient()  
    String url = "http://mail-st.appspot.com/mail_sender"
    PostMethod method = new PostMethod(url)
    
    method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    
    method.addParameter("email", htmlContent);
    method.addParameter("from", from);
    method.addParameter("to", to);
    method.addParameter("sender", sender);
    method.addParameter("subject",subject);
    method.addParameter("key","msg_sender_ST");
    method.addParameter("template",isTemplate.toString());
    
    int returnCode = client.executeMethod(method) //Response Code: 200, 302, 304 etc.
    def response = method.getResponseBodyAsString() // Actual response
    
    //log.debug('***'+returnCode)
    //log.debug('***'+response)
  //Alex<<
  }

  def sendCloseMboxMail(oMbox, oContext){
    def th=new Thread()
    th.start{
      def lsText=Email_template.findWhere(action:'#close_mbox_owner')
      def sText='Владелец закрыл ваш запрос по [@HOME].<br/>'
      def sHeader="Собщение по объявлению [@HOME]"
      if(lsText){
        sText=lsText.itext
        sHeader=lsText.title
      }

      def oUser=User.get(oMbox.user_id)
      def sNickname=oUser?.nickname

      def oHome=Home.get(oMbox.home_id)

      sText=sText.replace('[@NICKNAME]',sNickname)
        .replace('[@HOME]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/home/view/'+oHome.linkname+">"+oHome.name+"</a>")
        .replace('[@SPEC_DATE_START]',String.format('%tY-%<tm-%<td',oMbox.date_start))
        .replace('[@SPEC_DATE_END]',String.format('%tY-%<tm-%<td',oMbox.date_end))
        .replace('[@SPEC_HOMEPERSON]',Homeperson.get(oMbox.homeperson_id)?.name?:'')
        .replace('[@SPEC_PRICE]',oMbox.price.toString())
        .replace('[@SPEC_VALUTA]',Valuta.get(oMbox.valuta_id?:0).code)
      sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText

      sHeader=sHeader.replace('[@HOME]',oHome?.name)

      try{
        if(oUser?.email){
          if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
            sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)
          else{
          sendMail{
            to oUser?.email
            subject sHeader
            body( view:"/_mail",
              model:[mail_body:sText])
            }
          }
        }
      }catch(Exception e) {
        log.debug("Cannot sent email \n"+e.toString()+'in addmbox')
      }
    }
  }

  def mbox_bron_client_with_payment(lUserId,oMbox){
    def th=new Thread()
    th.start{
      synchronized(this) {
        //Email to client>>
        def lsText=Email_template.findWhere(action:'#mbox_bron_client_with_payment')
        def sText='Money has come'
        def sHeader='Money has come'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
        def oUserGuest = User.get(lUserId)
        def oUserOwner = User.findByClient_id(oMbox.homeowner_cl_id)
        def oHome = Home.get(oMbox.home_id)

        sText=sText.replace('[@NAME]',oUserGuest?.nickname)
        .replace('[@HOME]',oHome?.name)
        .replace('[@DATE]',String.format('%tY-%<tm-%<td',oMbox.date_start))
        .replace('[@NUMBER]',(oMbox.date_end - oMbox.date_start).toString())
        .replace('[@OWNER]',oUserOwner?.nickname)

        try{
          if(oUserGuest?.email){
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUserGuest?.email,sHeader,1)
            else{
              sendMail{
                to oUserGuest?.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in mbox_bron_client_with_payment')
        }
        //<<Email to client
      }
    }
  }

  def mbox_bron_owner_with_payment(lClId,oMbox){
    def th=new Thread()
    th.start{
      synchronized(this) {
        //Email to owner>>
        def lsText=Email_template.findWhere(action:'#mbox_bron_owner_with_payment')
        def sText='Money has come'
        def sHeader='Money has come'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
        def oClient = Client.get(lClId)
        def oUserOwner = User.findByClient_id(lClId)
        def oHome = Home.get(oMbox.home_id)
        def oUserGuest = User.get(oMbox.user_id)

        sText=sText.replace('[@NAME]',oUserOwner?.nickname)
        .replace('[@HOME]',oHome?.name)
        .replace('[@DATE]',String.format('%tY-%<tm-%<td',oMbox.date_start))
        .replace('[@NUMBER]',(oMbox.date_end - oMbox.date_start).toString())
        .replace('[@GHOST]',oUserGuest?.nickname)

        try{
          if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
            sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oClient?.name,sHeader,1)
          else{
            sendMail{
              to oClient?.name
              subject sHeader
              body( view:"/_mail",
              model:[mail_body:sText])
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in mbox_bron_owner_with_payment')
        }
        //<<Email to client
      }
    }
  }

  def mbox_cancel_bron_offer(oMbox){
    def th=new Thread()
    th.start{
      synchronized(this) {
        //Email to client>>
        def lsText=Email_template.findWhere(action:'#mbox_cancel_bron_offer')
        def sText='Owner cancel specoffer'
        def sHeader='Owner cancel specoffer'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
        def oUserGuest = User.get(oMbox?.user_id)
        def oHome = Home.get(oMbox?.home_id)

        sText=sText.replace('[@NAME]',oUserGuest?.nickname)
        .replace('[@HOME]',oHome?.name)

        try{
          if(oUserGuest?.email){
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUserGuest?.email,sHeader,1)
            else{
              sendMail{
                to oUserGuest?.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in mbox_cancel_bron_offer')
        }
        //<<Email to client
      }
    }
  }

  void tripRemindMailer(lsTrip){
    if (!lsTrip){
      log.debug("LOG>> TripReminderJob finish")
      return
    }
    def th=new Thread()
    th.start{
      synchronized(this) {
        Trip.withTransaction { status ->
          def oClient_Email_template = Email_template.findWhere(action:('#tripClientReminde'))
          def oOwner_Email_template = Email_template.findWhere(action:('#tripOwnerReminde'))
          lsTrip.each {trip->
            def oClient = User.get(trip.user_id)
            def oHome = Home.get(trip.home_id)
            def oOwner = User.findByClient_id(oHome?.client_id)
            if (oClient?.email) {
              def mailText = oClient_Email_template?.itext
              def sHeader = oClient_Email_template?.title

              mailText=mailText.replace('[@NAME]',oClient?.nickname)
              .replace('[@HOME]',oHome?.name)
              .replace('[@NUMBER]',(trip.todate - trip.fromdate).toString())
              .replace('[@OWNER]',oOwner?.nickname)

              try{
                if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
                  sendMailGAE(mailText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oClient.email,sHeader,1)
                else{
                  sendMail{
                    to oClient.email
                    subject sHeader
                    body( view:"/_mail",
                    model:[mail_body:mailText])
                  }
                }
              }catch(Exception e) {
                log.debug("Cannot sent email \n"+e.toString().replace("'","").replace('"','')+" in MailerService")
              }
            }
            if (oOwner?.email) {
              def mailText = oOwner_Email_template?.itext
              def sHeader=oOwner_Email_template?.title

              mailText=mailText.replace('[@NAME]',oOwner?.nickname)
              .replace('[@HOME]',oHome?.name)
              .replace('[@NUMBER]',(trip.todate - trip.fromdate).toString())
              .replace('[@GHOST]',oClient?.nickname)

              try{
                if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
                  sendMailGAE(mailText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oOwner.email,sHeader,1)
                else{
                  sendMail{
                    to oOwner.email
                    subject sHeader
                    body( view:"/_mail",
                    model:[mail_body:mailText])
                  }
                }
              }catch(Exception e) {
                log.debug("Cannot sent email \n"+e.toString().replace("'","").replace('"','')+" in MailerService")
              }
            }
            th.sleep(Tools.getIntVal(ConfigurationHolder.config.notemail.delay,15) *1000)
          }
        }
        log.debug("LOG>> TripReminderJob: finish")
      }
    }
  }

  void trip_bron_confirm_notice(oUserGuest,oTrip){
    def th=new Thread()
    th.start{
      synchronized(this) {
        //Email to client>>
        def lsText=Email_template.findWhere(action:'#trip_bron_confirm_notice')
        def sText='Owner confirm bron'
        def sHeader='Owner confirm bron'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
        if (!oUserGuest) {
          log.debug("No addressee in trip_bron_confirm_notice")
          return
        }

        def oHome = Home.get(oTrip?.home_id)
        def oUserOwner = User.findByClient_id(oHome?.client_id)
        def oPayorder = Payorder.get(oTrip?.payorder_id)
        def oCancellation = Rule_cancellation.get(oTrip?.rule_cancellation_id)

        sText=sText.replace('[@NAME]',oUserGuest?.nickname)
        .replace('[@HOME]',oHome?.name)
        .replace('[@HOMEURL]',ConfigurationHolder.config.grails.serverURL+'/home/view/'+oHome?.linkname)
        .replace('[@ADDRESS]',oHome?.address)
        .replace('[@HOMECLASS]',Homeclass.get(oHome?.homeclass_id?:0)?.name)
        .replace('[@DATE_START]',String.format('%td/%<tm/%<tY',oTrip?.fromdate))
        .replace('[@TIMEIN]',Rule_timein.get(oHome?.rule_timein_id?:0).name)
        .replace('[@DATE_END]',String.format('%td/%<tm/%<tY',oTrip?.todate))
        .replace('[@TIMEOUT]',Rule_timeout.get(oHome?.rule_timeout_id?:0).name)
        .replace('[@HOMEPERSON]',Homeperson.get(oTrip?.homeperson_id?:0)?.kol?.toString())
        .replace('[@NORDER]',oPayorder?.norder)
        .replace('[@SUMMA_VAL]',oPayorder?.summa_val?.toString())
        .replace('[@VAL]',Valuta.get(oTrip?.valuta_id?:0)?.symbol)
        .replace('[@SUMMA_DEAL]',oPayorder?.summa_deal?.toString())
        .replace('[@SUMMA]',oPayorder?.summa?.toString())
        .replace('[@CANCELLATION]',oCancellation.name)
        .replace('[@CANCELLATIONFULL]',oCancellation?.fullname)
        .replace('[@CANCELLATIONURL]',ConfigurationHolder.config.grails.serverURL+'/home/cancellation/#'+oCancellation?.shortlink)
        .replace('[@RULEURL]',ConfigurationHolder.config.grails.serverURL+'/home/view/'+oHome?.linkname+'/#homerule')
        .replace('[@OWNER]',oUserOwner?.nickname)
        .replace('[@OWNERURL]',ConfigurationHolder.config.grails.serverURL+'/id'+oUserOwner?.id)
        .replace('[@TEL]',oUserOwner?.tel)
        .replace('[@DATE_BRON]',String.format('%td/%<tm/%<tY %<tH:%<tM',oTrip.inputdate))
        
        try{
          if(oUserGuest?.email){
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUserGuest?.email,sHeader,1)
            else{
              sendMail{
                to oUserGuest?.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in trip_bron_confirm_notice')
        }
        //<<Email to client
      }
    }
  }

  void sendNewUser(lUserClientId,oContext){
    def th=new Thread()
    th.start{
      synchronized(this) {
     
        //th.sleep(Tools.getIntVal(ConfigurationHolder.config.notemail.delay,15) *1000)
        
        def oUser = User.get(lUserClientId?:0)
        //Email to client user>>
        def lsText=Email_template.findWhere(action:'#send_newUser')
        def sText='New message to [@NICKNAME] and [@PASSWORD]'
        def sHeader="New message to [@NICKNAME]"
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }

        sText=sText.replace('[@NICKNAME]',oUser?.nickname?:'')       
          .replace('[@PASSWORD]',Tools.hidePsw(oUser.email)[0..8])
          .replace('[@EMAIL]',oUser.email)

        //sText=((sText?:'').size()>Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500))?sText.substring(0,Tools.getIntVal(ConfigurationHolder.config.mail.textsize,500)):sText
        sText=sText.replace('[@URL]',"<a href="+ConfigurationHolder.config.grails.mailServerURL+((oContext.is_dev)?"/${oContext.appname}":"")+'/user/passwconfirm/'+oUser.code+">ссылке</a>")
        sHeader=sHeader.replace('[@NICKNAME]',oUser?.nickname?:'')

        try{
          if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
            sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser.email,sHeader,1)        
          else{
          sendMail{
            to oUser.email
            subject sHeader
            body( view:"/_mail",
            model:[mail_body:sText])
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client user\n"+e.toString()+'in addmbox')
        }
          //<<Email to client user        
      }
    }
  }

  void sendRefundMoneyMailClientFullPrice(oTask){
    def th=new Thread()
    th.start{
      synchronized(this) {
        //Email to client>>
        def lsText=Email_template.findWhere(action:'#refund_money_client')
        def sText='money refund complete'
        def sHeader='money refund complete'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
        def oPaytrans = Paytrans.get(oTask?.paytrans_id?:0l)
        def oPayorder = Payorder.get(oPaytrans?.payorder_id?:0l)
        def oUserGuest = User.get(oPayorder?.user_id?:0l)
        if (!oUserGuest) {
          log.debug("No addressee in refund_money_client")
          return
        }

        sText=sText.replace('[@NAME]',oUserGuest?.nickname)
        .replace('[@SUMMA]',oTask?.summa.toString())
        .replace('[@NORDER]',oPayorder?.norder)

        try{
          if(oUserGuest?.email){
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUserGuest?.email,sHeader,1)
            else{
              sendMail{
                to oUserGuest?.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in refund_money_client')
        }
        //<<Email to client
      }
    }
  }

  void sendRefundMoneyMailOwner(oTask){
    def th=new Thread()
    th.start{
      synchronized(this) {
        //Email to client>>
        def lsText=Email_template.findWhere(action:'#refund_money_owner')
        def sText='money refund complete'
        def sHeader='money refund complete'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
        def oPaytrans = Paytrans.get(oTask?.paytrans_id?:0l)
        def oPayorder = Payorder.get(oPaytrans?.payorder_id?:0l)
        def oUserOwner = User.findByClient_id(oPayorder?.client_id?:0l)
        if (!oUserOwner) {
          log.debug("No addressee in refund_money_owner")
          return
        }

        sText=sText.replace('[@NAME]',oUserOwner?.nickname)
        .replace('[@SUMMA]',oTask?.summa.toString())
        .replace('[@NORDER]',oPayorder?.norder)

        try{
          if(oUserOwner?.email){
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUserOwner?.email,sHeader,1)
            else{
              sendMail{
                to oUserOwner?.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in refund_money_owner')
        }
        //<<Email to client
      }
    }
  }

  void sendWithdrawalMoneyMailOwner(oTask){
    def th=new Thread()
    th.start{
      synchronized(this) {
        //Email to client>>
        def lsText=Email_template.findWhere(action:'#withdrawal_money_owner')
        def sText='money withdrawal complete'
        def sHeader='money withdrawal complete'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
        def oPaytrans = Paytrans.get(oTask?.paytrans_id?:0l)
        def oPayorder = Payorder.get(oPaytrans?.payorder_id?:0l)
        def oUserOwner = User.findByClient_id(oPayorder?.client_id?:0l)
        if (!oUserOwner) {
          log.debug("No addressee in withdrawal_money_owner")
          return
        }

        sText=sText.replace('[@NAME]',oUserOwner?.nickname)
        .replace('[@SUMMA]',oTask?.summa.toString())
        .replace('[@NORDER]',oPayorder?.norder)

        try{
          if(oUserOwner?.email){
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUserOwner?.email,sHeader,1)
            else{
              sendMail{
                to oUserOwner?.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in withdrawal_money_owner')
        }
        //<<Email to client
      }
    }
  }

  void sendRefundMoneyMailClient(oTask){
    def th=new Thread()
    th.start{
      synchronized(this) {
        //Email to client>>
        def lsText=Email_template.findWhere(action:'#refund_money_client_2')
        def sText='money refund complete'
        def sHeader='money refund complete'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
        def oPaytrans = Paytrans.get(oTask?.paytrans_id?:0l)
        def oPayorder = Payorder.get(oPaytrans?.payorder_id?:0l)
        def oUserGuest = User.get(oPayorder?.user_id?:0l)
        if (!oUserGuest) {
          log.debug("No addressee in refund_money_client")
          return
        }

        sText=sText.replace('[@NAME]',oUserGuest?.nickname)
        .replace('[@SUMMA]',oTask?.summa.toString())
        .replace('[@NORDER]',oPayorder?.norder)

        try{
          if(oUserGuest?.email){
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUserGuest?.email,sHeader,1)
            else{
              sendMail{
                to oUserGuest?.email
                subject sHeader
                body( view:"/_mail",
                model:[mail_body:sText])
              }
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in refund_money_client')
        }
        //<<Email to client
      }
    }
  }

  void sendConfirmPayoutMail(oClient){
    def th=new Thread()
    th.start{
      synchronized(this) {
      //Email to client>>
        def lsText=Email_template.findWhere(action:'#confirm_payout')
        def sText='your payout scheme was confirmed'
        def sHeader='your payout scheme was confirmed'
        if(lsText){
          sText=lsText.itext
          sHeader=lsText.title
        }
        def oUser = User.findByClient_id(oClient?.id?:0l)
        if (!oUser) {
          log.debug("No addressee in confirm_payout")
          return
        }

        sText = sText.replace('[@USERNAME]',oUser.nickname)
        .replace('[@RESERVE]',Reserve.get(oClient?.reserve_id)?.name?:'')
        .replace('[@PAYACCOUNT]',Payaccount.get(oClient?.payaccount_id)?.name?:'')

        try{
          if(oUser?.email){
            if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
              sendMailGAE(sText,ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser?.email,sHeader,1)
            else{
              sendMail{
                to oUser.email
                subject sHeader
                body( view:"/_mail",
                  model:[mail_body:sText])
              }
            }
          }
        }catch(Exception e) {
          log.debug("Cannot sent email to client\n"+e.toString()+'in confirm_payout')
        }
      //<<Email to client
      }
    }
  }

  void sendBronInvitationMail(_mboxrec,_mbox,lPrice){
    if (!_mboxrec||!_mbox) return
    def th=new Thread()
    th.start{
      synchronized(this) {
        Home.withNewSession {
          def model = [context:collectContext()]
          model.rec_id = _mboxrec.id
          model.mbox_id = _mbox.id
          model.urlphoto = ConfigurationHolder.config.urlphoto
          model.homeperson = Homeperson.get(_mboxrec.homeperson_id)
          model.home = Home.read(_mboxrec.home_id)
          model.ownerUser = User.findWhere(client_id:model.home?.client_id)
          model.date_start = String.format('%td/%<tm/%<tY',_mboxrec.date_start)
          model.date_end = String.format('%td/%<tm/%<tY',_mboxrec.date_end)
          model.moddate=String.format('%td/%<tm/%<tY %<tH:%<tM',_mbox.moddate)
          model.cancellation = Rule_cancellation.get(model.home.rule_cancellation_id)
          model.timein = Rule_timein.get(model.home.rule_timein_id?:0).name
          model.timeout = Rule_timeout.get(model.home.rule_timeout_id?:0).name
          def oValutarate = new Valutarate()
          model.valutaRates = oValutarate.csiGetRate(857)
          model.valutaSym = Valuta.get(857).symbol
          model.ownerClient = Client.get(_mbox.homeowner_cl_id)
          model.resstatModifier = 1.0
          model.ispaypossible = (model.ownerClient?.resstatus==1)
          model.invoicelife = Tools.getIntVal(ConfigurationHolder.config.payorder.invoicelife.days,5)
          if (model.ispaypossible) {
            model.resstatModifier = model.resstatModifier + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100)
          }
          model.displayPrice = Math.round(_mboxrec.price_rub / model.valutaRates * model.resstatModifier)
          model.payway = Payway.findAllByModstatus(1)
          model.reserve = Reserve.get(model.ownerClient?.reserve_id?:0)
          if(model.ispaypossible&&model.reserve)
            model.totalPrice = Math.round(lPrice / model.valutaRates)          
          def oUser = User.get(_mbox.user_id)
          def sHeader = 'Предварительное бронирование'
          try{
            if(oUser?.email){
              if(Tools.getIntVal(ConfigurationHolder.config.mail_gae,0))
                sendMailGAE(groovyPageRenderer.render(view: "/_mail_bron", model: model),ConfigurationHolder.config.grails.mail.from1,ConfigurationHolder.config.grails.mail.username,oUser?.email,sHeader,0)
              else{
                sendMail{
                  to oUser.email
                  subject sHeader
                  body( view:"/_mail_bron",
                    model:model)
                }
              }
            }
          }catch(Exception e) {
            log.debug("Cannot sent email to client\n"+e.toString()+'in sendBronInvitationMail')
          }
        }
      }
    }
  }

  private def collectContext() {
    [
      is_dev:(Tools.getIntVal(ConfigurationHolder.config.isdev,0)==1),
      serverURL:(ConfigurationHolder.config.grails.mailServerURL?:ConfigurationHolder.config.grails.serverURL),
      sequreServerURL:Tools.getIntVal(Dynconfig.findByName('global.https.enable')?.value,0)?(ConfigurationHolder.config.grails.secureServerURL?:ConfigurationHolder.config.grails.serverURL):ConfigurationHolder.config.grails.serverURL,
      appname:ConfigurationHolder.config.grails.serverApp,
      lang:''
    ]
  }

}
