import grails.util.Holders
import groovy.xml.MarkupBuilder
class ApiService {
  def billingService
  def mailerService
  def androidGcmService
  def smsService

  def DATE_FORMAT='yyyy-MM-dd'
  def context = getContext()

  def getContext(){
    [
      is_dev:(Tools.getIntVal(Holders.config.isdev,0)==1),
      serverURL:(Holders.config.grails.mailServerURL?:Holders.config.grails.serverURL),
      appname:Holders.config.grails.serverApp,
      lang:''
    ]
  }

  def error() {
    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)
    xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
    def api_error = Api_errors.get(10004)
    xml.error(){
      errorcode(api_error.id)
      errortext(api_error.errortext)
    }
    return writer.toString()
  }

  def availability(requestService) {
    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)
    xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
    if (requestService.getBodyXML()==null){
      def api_error = Api_errors.get(10000)
	    xml.error(){
        errorcode(api_error.id)
        errortext(api_error.errortext)
	  	}
      requestService.setStatus(400)
	  	return writer.toString()
    }
    def incomingParams = parseAvailabilityRequest(requestService.getBodyXML())
    if (incomingParams.error){
      requestService.setStatus(400)
      def api_error = Api_errors.get(incomingParams.error)
	    xml.error(){
        errorcode(api_error.id)
        errortext(api_error.errortext)
	  	}
    } else if(!doAvailabilityRequest(incomingParams.data)){
      requestService.setStatus(400)
      def api_error = Api_errors.get(10500)
      xml.error(){
        errorcode(api_error.id)
        errortext(api_error.errortext)
      }
    } else {
      xml.ok()
    }
    writer.toString()
  }

  def booking(requestService) {
    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)
    xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
    if (requestService.getBodyXML()==null){
      def api_error = Api_errors.get(10000)
      xml.error(){
        errorcode(api_error.id)
        errortext(api_error.errortext)
      }
      requestService.setStatus(400)
      return writer.toString()
    }
    def incomingParams = parseBookingRequest(requestService.getBodyXML())
    if (incomingParams.error){
      requestService.setStatus(400)
      def api_error = Api_errors.get(incomingParams.error)
      xml.error(){
        errorcode(api_error.id)
        errortext(api_error.errortext)
      }
    } else if(!doBookingRequest(incomingParams.data)){
      requestService.setStatus(400)
      def api_error = Api_errors.get(10500)
      xml.error(){
        errorcode(api_error.id)
        errortext(api_error.errortext)
      }
    } else {
      xml.ok()
    }
    writer.toString()
  }

  def info(requestService) {
    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)
    xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
    if (requestService.getBodyXML()==null){
      def api_error = Api_errors.get(10000)
      xml.error(){
        errorcode(api_error.id)
        errortext(api_error.errortext)
      }
      requestService.setStatus(400)
      return writer.toString()
    }
    def incomingParams = parseInfoRequest(requestService.getBodyXML())
    if (incomingParams.error){
      requestService.setStatus(400)
      def api_error = Api_errors.get(incomingParams.error)
      xml.error(){
        errorcode(api_error.id)
        errortext(api_error.errortext)
      }
    } else if(!doInfoRequest(incomingParams.data)){
      requestService.setStatus(400)
      def api_error = Api_errors.get(10500)
      xml.error(){
        errorcode(api_error.id)
        errortext(api_error.errortext)
      }
    } else {
      xml.ok()
    }
    writer.toString()
  }

  private def parseBookingRequest(_xml){
  	def result = [error:0,data:[:]]

  	if (!_xml.secret.text())
  		result.error = 10001
  	else if (!Api.findByGuid(_xml.secret.text()))
  		result.error = 10403
  	else if (!_xml.id.text())
  		result.error = 10002
    else if (!Mbox.findByOuter_id(_xml.id.text()))
      result.error = 10100
    else if (Mbox.findByOuter_id(_xml.id.text()).modstatus!=3)
      result.error = 10005

    if (result.error!=0) return result

    result.data.api = Api.findByGuid(_xml.secret.text())
    result.data.req_id = _xml.id.text()
    result
  }

  private def parseInfoRequest(_xml){
    def result = [error:0,data:[:]]

    if (!_xml.secret.text())
      result.error = 10001
    else if (!Api.findByGuid(_xml.secret.text()))
      result.error = 10403
    else if (!_xml.id.text())
      result.error = 10002
    else if (!Mbox.findByOuter_id(_xml.id.text()))
      result.error = 10100

    if (result.error!=0) return result

    result.data.api = Api.findByGuid(_xml.secret.text())
    result.data.req_id = _xml.id.text()
    result.data.comment = _xml.comment.text()
    result
  }

  private def parseAvailabilityRequest(_xml){
    def result = [error:0,data:[:]]

    if (!_xml.secret.text())
      result.error = 10001
    else if (!Api.findByGuid(_xml.secret.text()))
      result.error = 10403
    else if (!_xml.id.text())
      result.error = 10002
    else if (Mbox.findByOuter_id(_xml.id.text()))
      result.error = 10003
    /*else if (!_xml.user_id.text())
      result.error = 10002
    else if (!_xml.user_id.text().isInteger())
      result.error = 10101*/
    else if (!_xml.name.text())
      result.error = 10002
    else if (!_xml.email.text())
      result.error = 10002
    else if (!Tools.checkEmailString(_xml.email.text()))
      result.error = 10102
    else if (!_xml.propertyId.text())
      result.error = 10002
    else if (!_xml.propertyId.text().isInteger())
      result.error = 10103
    else if (!Home.get(_xml.propertyId.text().toInteger()))
      result.error = 10103
    else if (!Tools.safetyParseDate(_xml.checkIn.text(),DATE_FORMAT))
      result.error = 10002
    else if (!Tools.safetyParseDate(_xml.checkOut.text(),DATE_FORMAT))
      result.error = 10002
    else if (!_xml.total.text())
      result.error = 10002
    else if (!_xml.total.text().isInteger())
      result.error = 10104
    else if (!_xml.guests.text())
      result.error = 10002
    else if (!_xml.guests.text().isInteger())
      result.error = 10105

    if (result.error!=0) return result

    result.data.api = Api.findByGuid(_xml.secret.text())
    result.data.req_id = _xml.id.text()
    //result.data.user_id = _xml.user_id.text()
    result.data.username = _xml.name.text()
    result.data.usermail = _xml.email.text()
    result.data.home = _xml.propertyId.text().toInteger()
    result.data.date_start = Tools.safetyParseDate(_xml.checkIn.text(),DATE_FORMAT)
    result.data.date_end = Tools.safetyParseDate(_xml.checkOut.text(),DATE_FORMAT)
    result.data.price = _xml.total.text().toInteger()
    result.data.guest_num = _xml.guests.text().toInteger()
    result.data.comment = _xml.comment.text()
    result
  }

  private def doAvailabilityRequest(_data){
    def oUser = User.findWhere(openid:_data.api.company+'_'+Tools.hidePsw(_data.usermail))
    if (!oUser) {
      //new user registration>>
      if (!(oUser = new User().apiNewUser(email:_data.usermail,nickname:_data.username,outer_id:Tools.hidePsw(_data.usermail),company:_data.api.company))) {
        return false
      }
      //<<new User registration
    }

    def oMbox = new Mbox()
    oMbox.inputdate=new Date()

    def stringlimit = Tools.getIntVal(Holders.config.largetext.limit,5000)
    if (_data.comment.size()>stringlimit) _data.comment = _data.comment.substring(0, stringlimit)
    if (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false) {
      if (_data.comment.replace('(','').replace(')','').replace('-','').replaceAll("\\s","").matches('.*[0-9]{7,}.*')||_data.comment.matches('.*\\S+@\\S*.*')||_data.comment.matches('.*\\S*@\\S+.*')) {
        _data.comment = (_data.comment?:'').replaceAll('[0-9( )-]{7,}',' [номер] ').replaceAll('\\S+@\\S*','[email]').replaceAll('\\S*@\\S+','[email]').trim()
      }
    }

    def oHome = Home.get(_data.home)
    def valutaRates = new Valutarate().csiGetRate(oHome.valuta_id)

    oMbox.price_rub = _data.price
    oMbox.price = (_data.price/valutaRates).toLong()
    oMbox.valuta_id = oHome.valuta_id
    oMbox.user_id = oUser.id
    oMbox.moddate = new Date()
    oMbox.date_start = _data.date_start
    oMbox.date_end = _data.date_end
    oMbox.homeperson_id = _data.guest_num
    oMbox.mtext = _data.comment
    oMbox.mtextowner = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?_data.comment:''
    oMbox.is_telperm = 1
    oMbox.home_id = _data.home
    oMbox.homeowner_cl_id = oHome.client_id
    oMbox.answertype_id = 0
    oMbox.modstatus = 1
    oMbox.is_answer = 0
    oMbox.is_read = 0
    oMbox.outer_id = _data.req_id
    oMbox.is_approved = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?1:0

    if (!oMbox.save(flush:true)){
      log.debug('error on save Mbox in ApiService:doAvailabilityRequest')
      oMbox.errors.each{log.debug(it)}
      return false
    }

    def oMboxrec = new Mboxrec()
    oMboxrec.mbox_id = oMbox.id
    oMboxrec.answertype_id = 0
    oMboxrec.rectext = _data.comment
    oMboxrec.is_answer = 0
    oMboxrec.inputdate = new Date()
    oMboxrec.home_id = oMbox.home_id
    oMboxrec.homeperson_id = oMbox.homeperson_id
    oMboxrec.date_start = oMbox.date_start
    oMboxrec.date_end = oMbox.date_end
    oMboxrec.price_rub = _data.price
    oMboxrec.price = (_data.price/valutaRates).toLong()
    oMboxrec.valuta_id = oHome.valuta_id
    oMboxrec.is_approved = (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)?1:0

    if (!oMboxrec.save(flush:true)){
      log.debug('error on save Mboxrec in ApiService:doAvailabilityRequest')
      oMboxrec.errors.each{log.debug(it)}
      return false
    }

    mailerService.sendMboxFirstMails(oMbox,oUser.id,context,(Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false),false)
    if ((Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false)&&Tools.getIntVal(Holders.config.noticeSMS.active,1)&&User.findByClient_id(oHome.client_id)?.is_noticeSMS) {
      try {
        smsService.sendNoticeSMS(User.findByClient_id(oHome.client_id),oHome.region_id)
      } catch(Exception e) {
        log.debug("Cannot sent sms \n"+e.toString()+' in ApiService:doAvailabilityRequest')
      }
    }
    if(Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false){
      //GCM>>
      def oHomeGCM = Home.get(oMbox.home_id)
      def oClientGCM = Client.get(oHomeGCM?.client_id)
      oClientGCM = oClientGCM.parent?Client.get(oClientGCM.parent):oClientGCM
      def msg_unread_count = Mbox.executeQuery('select count(*) from Mbox where (((homeowner_cl_id=:cl_id and is_readowner=0)or(user_id=:u_id and is_readclient=0)) OR (is_read=0 and ((homeowner_cl_id=:cl_id and is_answer=0 and is_approved=1) or (user_id=:u_id and is_answer=1)))) and not(modstatus=6 or ((homeowner_cl_id=:cl_id and IFNULL(is_owfav,0)=-1)or(user_id=:u_id and IFNULL(is_clfav,0)=-1)))',[cl_id:oClientGCM?.id,u_id:0.toLong()])[0]
      def sendGCM=[:]
      sendGCM.message='letter'
      sendGCM.msgcnt = msg_unread_count.toString()

      def lsDevices=[]

      def lsUsers=User.findAllWhere(client_id:oClientGCM?.id)
      def user_ids=[]
      for(user in lsUsers)
        user_ids<<user.id
  
      lsDevices=Device.findAll("FROM Device WHERE user_id IN (:user_ids)",[user_ids:user_ids])
  
      if(lsDevices){
        def lsDevices_ids=[]

        for(device in lsDevices)
          lsDevices_ids<<device.device
        if(lsDevices_ids)
          androidGcmService.sendMessage(sendGCM,lsDevices_ids,'message', Holders.config.android.gcm.api.key ?: '')
      }
      //GCM<<
    }
    mailerService.sendAdminMboxMail(oMbox.id)

    true
  }

  private def doBookingRequest(_data){
    def oMbox = Mbox.findByOuter_id(_data.req_id)
    def oMboxrec = Mboxrec.findByMbox_id(oMbox.id,[sort:'id',order:'desc'])
    try {
      billingService.createTripFromMbox(oMbox,oMboxrec,context)
    } catch(Exception e) {
      log.debug('error on updating booking status in ApiService:doBookingRequest\n'+e.toString())
      return false
    }
    true
  }

  private def doInfoRequest(_data){
    def oMbox = Mbox.findByOuter_id(_data.req_id)

    def stringlimit = Tools.getIntVal(Holders.config.largetext.limit,5000)
    if (_data.comment.size()>stringlimit) _data.comment = _data.comment.substring(0, stringlimit)
    if (Tools.getIntVal(Dynconfig.findByName('mbox.noContactMode')?.value,0)?true:false) {
      if (_data.comment.replace('(','').replace(')','').replace('-','').replaceAll("\\s","").matches('.*[0-9]{7,}.*')||_data.comment.matches('.*\\S+@\\S*.*')||_data.comment.matches('.*\\S*@\\S+.*')) {
        _data.comment = (_data.comment?:'').replaceAll('[0-9( )-]{7,}',' [номер] ').replaceAll('\\S+@\\S*','[email]').replaceAll('\\S*@\\S+','[email]').trim()
      }
    }

    oMbox.mtext = _data.comment?:''
    if (oMbox.modstatus==4){
      oMbox.is_answer = 0
      oMbox.is_read = 0
      oMbox.answertype_id = 0
    }
    oMbox.is_adminread = 0
    oMbox.lastusermessagedate = new Date()
    if (!oMbox.save(flush:true)){
      log.debug('error on save Mbox in ApiService:doInfoRequest')
      oMbox.errors.each{log.debug(it)}
      return false
    }
    def oMboxrec = new Mboxrec()
    oMboxrec.mbox_id = oMbox.id
    oMboxrec.answertype_id = 0
    oMboxrec.rectext = _data.comment?:''
    oMboxrec.is_answer = 0
    oMboxrec.inputdate = new Date()
    oMboxrec.is_approved = oMbox.modstatus==4?1:0

    oMboxrec.price = oMbox.price
    oMboxrec.price_rub = oMbox.price_rub
    oMboxrec.valuta_id = oMbox.valuta_id
    oMboxrec.date_start = oMbox.date_start
    oMboxrec.date_end = oMbox.date_end
    oMboxrec.homeperson_id = oMbox.homeperson_id
    oMboxrec.home_id = oMbox.home_id

    if (!oMboxrec.save(flush:true)){
      log.debug('error on save Mboxrec in ApiService:doInfoRequest')
      oMboxrec.errors.each{log.debug(it)}
      return false
    }
    if (oMbox.modstatus==4){
      mailerService.addanswermail(oMbox,oMboxrec,context)
    }

    true
  }

  private Long createResponse(oObject,iApiId,iType){
    try {
      switch(iType) {
        case 1:
          return new Api_response(outer_id:oObject.outer_id,home_id:oObject.home_id,api_id:iApiId,type:iType).setDetailData(oObject).save(failOnError:true)?.id?:0l
          break
        default:
          return 0l
          break
      }
    } catch(Exception e) {
      log.debug('error on save ApiResponse in ApiService:createResponse\n'+e.toString())
      return 0l
    }
  }

  void doResponse (lId){
    Api_response.withTransaction { status ->
      def oResponse = Api_response.lock(lId)
      try {
        if (!oResponse)
          throw new Exception ('Transaction '+lId+' not exist')
        if (oResponse.modstatus == 1)
          throw new Exception ('Transaction already done')
        switch(oResponse.type) {
          case 1:
            transactionType1Handler(oResponse)
            break
          default:
            throw new Exception ('Where is no handler for transaction type '+oResponse.type)
            break
        }
        oResponse.modstatus = 1
        oResponse.save()
        status.flush()
      } catch(Exception e) {
        log.debug("ApiService:doResponse:\n"+e.toString())
        status.setRollbackOnly()
        throw e
      }
    }
  }

  private void transactionType1Handler(_paytrans){
    def sUrl = 'http://www.abookingnet.com'
    def sPath = '/api/host/booking/quote'
    def hsBody = [:]
    hsBody.id = _paytrans.outer_id
    hsBody.checkIn = _paytrans.date_start.format(DATE_FORMAT)
    hsBody.checkOut = _paytrans.date_end.format(DATE_FORMAT)
    hsBody.total = _paytrans.price
    hsBody.currency = "USD"
    hsBody.guests = _paytrans.homeperson_id
    hsBody.comment = _paytrans.mtext
    hsBody.api_key = Api.get(_paytrans.api_id)?.outer_key?:''
    if (!smsService.postJSONdata(sUrl,sPath,hsBody)) {
      throw new Exception ('Error: bad response: '+_paytrans.id)
    }
  }

}