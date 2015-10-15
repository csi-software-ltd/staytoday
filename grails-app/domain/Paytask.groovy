class Paytask {
  def mailerService
  static constraints = {
  }
  static mapping = {
    version false
  }

  Integer id
  Integer paytasktype_id
  String from_code = ''
  String to_code = ''
  String comment = ''
  Date inputdate = new Date()
  Date moddate = new Date()
  Integer modstatus = 0
  Integer summa = 0
  Long paytrans_id

  def beforeUpdate() {
    moddate = new Date()
  }

  static def createTask(_paytrans,iType,iSumma,isExternal,sComment=''){
    def oTask = new Paytask()
    oTask.paytasktype_id = iType
    oTask.from_code = getFromCode(_paytrans,isExternal)
    oTask.to_code = getToCode(_paytrans,iType)
    oTask.comment = sComment
    oTask.summa = iSumma
    oTask.paytrans_id = _paytrans.id
    return oTask.save(failOnError:true)
  }

  static String getFromCode(_paytrans,isExternal){
    if(isExternal){
      return Agr.get(Payorder.get(_paytrans.payorder_id)?.agr_id)?.inner_account?:''
    } else {
      return Accountdata.findByModstatus(1)?.corraccount?:''
    }
  }

  static String getToCode(_paytrans,iType){
    def result = ''
    switch(iType) {
      case 2:
      case 3:
      case 5:
        result = 'узнать у гостя'
        break
      case 1:
      case 4:
        def oClient = Client.get(Payorder.get(_paytrans.payorder_id)?.client_id)
        result = Payaccount.get(oClient?.payaccount_id)?.name?:''
        if(oClient?.payaccount_id==1){
          result += ' ' + oClient.corraccount
        } else if(oClient?.payaccount_id==2){
          result += ' ' + oClient.yandex
        } else if(oClient?.payaccount_id==3){
          result += ' ' + oClient.webmoney
        }
        break
      case 8:
        result = Agr.get(Payorder.get(_paytrans.payorder_id)?.agr_id)?.inner_account?:''
        break
    }
    return result
  }

  def sendNoticeMail(iId){
    switch(iId) {
      case 1:
        mailerService.sendWithdrawalMoneyMailOwner(this)
      break
      case 2:
      case 5:
        mailerService.sendRefundMoneyMailClientFullPrice(this)
      break
      case 3:
        mailerService.sendRefundMoneyMailClient(this)
      break
      case 4:
        mailerService.sendRefundMoneyMailOwner(this)
      break
      default:

      break
    }
  }

  def setcomplete(){
    sendNoticeMail(paytasktype_id)
    modstatus = 1
    return save(failOnError:true,flush:true)
  }

}