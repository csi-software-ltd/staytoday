import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.context.i18n.LocaleContextHolder as LCH
class Payorder {
  def searchService

  static constraints = {
    promocode(nullable:true)
    out_paydate(nullable:true)
    in_paydate(nullable:true)
    in_paynumber(nullable:true)
  }
  static mapping = {
    //datasource 'admin'
    version false
  }
  Long id
  String norder
  Integer agr_id
  Long user_id
  Long client_id
  Long home_id
  Long mbox_id
  Integer payway_id
  Integer reserve_id
  Integer paycomtype
  String tranagr_id = ''
  String userip = ''
  Integer summa
  Integer summa_deal
  Integer summa_val
  Integer valuta_id
  Integer summa_agr = 0
  Integer summa_ext = 0
  Integer summa_int = 0
  Integer summa_com = 0
  Integer summa_serv = 0
  Integer summa_own = 0
  Integer summa_ret = 0
  Date inputdate = new Date ()
  Date moddate = new Date ()
  Date bonusend = new Date ()
  Date findate = new Date ()
  Integer modstatus = 0
  Integer agrstatus = 0
  Integer retstatus = 0
  Integer transtatus = 0
  Integer confstatus = 0
  Integer dealstatus = 0
  String plat_name = ''
  String plat_address =''
  String plat_inn = ''
  String plat_okpo = ''
  String plat_bik = ''
  String plat_bank = ''
  String plat_corchet = ''
  String plat_schet = ''
  String promocode = ''
  String admincomment = ''
  Date in_paydate = new Date()
  Date out_paydate = new Date()
  String in_paynumber = ''
  Date plat_date = new Date()

  def beforeUpdate() {
    moddate = new Date()
  }

  static def createOrder( Mbox _oMbox,_oReserve,_request,_summa){
    def oPrevOrder = Payorder.findAllByUser_id(_oMbox.user_id,[max:1,sort:'id',order:'desc'])[0]
    def oOrder = new Payorder()
    oOrder.agr_id = Payway.get(_request.payway)?.agr_id
    oOrder.user_id = _oMbox.user_id
    oOrder.client_id = _oMbox.homeowner_cl_id
    oOrder.home_id = _oMbox.home_id
    oOrder.mbox_id = _oMbox.id
    oOrder.payway_id = _request.payway
    oOrder.reserve_id = _oReserve.id
    oOrder.paycomtype = _oReserve.paycomtype
    oOrder.userip = _request.userip?:''
    oOrder.summa = _summa
    oOrder.summa_deal = Math.round(_oMbox.price_rub + _oMbox.price_rub * (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100))
    oOrder.summa_val = Math.round(_oMbox.price + _oMbox.price * (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100))
    oOrder.valuta_id = _oMbox.valuta_id
    if (oPrevOrder) {
      oOrder.plat_name = oPrevOrder.plat_name
      oOrder.plat_address = oPrevOrder.plat_address
      oOrder.plat_inn = oPrevOrder.plat_inn
      oOrder.plat_okpo = oPrevOrder.plat_okpo
      oOrder.plat_bik = oPrevOrder.plat_bik
      oOrder.plat_bank = oPrevOrder.plat_bank
      oOrder.plat_corchet = oPrevOrder.plat_corchet
      oOrder.plat_schet = oPrevOrder.plat_schet
    }
    def cur_orderId = Payorder.list(max:1,sort:'id',order:'desc')[0]?.id?:0
    oOrder.norder = Tools.generateNorder(cur_orderId+1,1)

    return oOrder.save()
  }

  static def createOrder( Payorder _oPayorder,_request, Client _oClient,messageSource){
    def oOrder = new Payorder()
    oOrder.agr_id = _oPayorder.agr_id
    oOrder.user_id = _oPayorder.user_id
    oOrder.client_id = _oClient.id
    oOrder.home_id = _request.home_id
    oOrder.mbox_id = _request.mbox_id
    oOrder.payway_id = _oPayorder.payway_id
    oOrder.reserve_id = _oPayorder.reserve_id
    oOrder.paycomtype = _oPayorder.paycomtype
    oOrder.tranagr_id = _oPayorder.tranagr_id
    oOrder.userip = _oPayorder.userip
    if(_oPayorder.retstatus>0) {
      oOrder.summa = _oPayorder.summa_ret
    } else {
      oOrder.summa = _oPayorder.summa_int?:_oPayorder.summa_ext
    }
    oOrder.summa_deal = _request.summa_deal
    oOrder.summa_val = _request.summa_val
    oOrder.valuta_id = _request.valuta_id
    oOrder.summa_agr = 0
    if (_oPayorder.transtatus>0) {
      oOrder.summa_ext = 0
      oOrder.summa_int = oOrder.summa
    } else {
      oOrder.summa_ext = oOrder.summa
      oOrder.summa_int = 0
    }
    oOrder.summa_com = 0
    oOrder.summa_serv = 0
    oOrder.summa_own = 0
    oOrder.summa_ret = 0
    oOrder.inputdate = new Date ()
    oOrder.moddate = new Date ()
    oOrder.bonusend = new Date ()
    oOrder.findate = new Date ()
    oOrder.modstatus = 1
    oOrder.agrstatus = _oPayorder.agrstatus==2?1:_oPayorder.agrstatus
    oOrder.retstatus = 0
    oOrder.transtatus = _oPayorder.transtatus==2?1:_oPayorder.transtatus
    oOrder.confstatus = 1
    oOrder.dealstatus = 0
    oOrder.plat_name = _oPayorder.plat_name
    oOrder.plat_address =_oPayorder.plat_address
    oOrder.plat_inn = _oPayorder.plat_inn
    oOrder.plat_okpo = _oPayorder.plat_okpo
    oOrder.plat_bik = _oPayorder.plat_bik
    oOrder.plat_bank = _oPayorder.plat_bank
    oOrder.plat_corchet = _oPayorder.plat_corchet
    oOrder.plat_schet = _oPayorder.plat_schet
    oOrder.promocode = _oPayorder.promocode
    String [] args = [_oPayorder.home_id.toString(),_oPayorder.mbox_id.toString(),_oPayorder.client_id.toString()]
    oOrder.admincomment = messageSource.getMessage('admin.changepayorderhome.description.text', args, new Locale("ru","RU"))
    oOrder.plat_date = new Date()
    def cur_orderId = Payorder.list(max:1,sort:'id',order:'desc')[0]?.id?:0
    oOrder.norder = Tools.generateNorder(cur_orderId+1,1)

    return oOrder.save()
  }

  def moneyEarningsOrderUpdate(_summa){
    summa_int = _summa
    modstatus = 1
    transtatus = 1
    return save(failOnError:true)
  }

  def moneyReturnOrderUpdate(_summa,iRetstatus){
    summa_int -= _summa
    summa_ret = _summa
    transtatus = 2
    retstatus = iRetstatus
    confstatus = 2
    return save(failOnError:true)
  }

  def moneyEarningsExternOrderUpdate(_summa,String _tranagr_id=''){
    if (agr_id==4) {
      tranagr_id = _tranagr_id
    } else if(agr_id==5){
      in_paydate = new Date()
      in_paynumber = _tranagr_id
    }
    summa_ext = _summa
    modstatus = 1
    agrstatus = 1
    return save(failOnError:true)
  }

  def moneyReturnExternOrderUpdate(_summa,iRetstatus){
    summa_ext -= _summa
    summa_ret = _summa
    agrstatus = 2
    retstatus = iRetstatus
    confstatus = 2
    return save(failOnError:true)
  }

  def cancelBronOffer(){
    modstatus = -1
    confstatus = 2
    return save(flush:true)
  }
  def confirmBron(){
    confstatus = 1
    return save(failOnError:true)
  }

  def confirmOrderFinallyUpdate(){
    dealstatus = 1
    modstatus = 2
    findate = new Date()
    bonusend = new Date() + Tools.getIntVal(ConfigurationHolder.config.promote.bonusforbron.days,10) + 1
    return save(failOnError:true)
  }

  def finalizeOrderAfterReturn(){
    modstatus = 2
    findate = new Date()
    return save(failOnError:true)
  }

  def moneyExternToInternOrderUpdate(_summa,iTransstatus){
    summa_int = summa_ext - _summa
    summa_agr = _summa
    summa_ext = 0
    agrstatus = 5
    transtatus = iTransstatus
    return save(failOnError:true)
  }

  def moneyExternToInternAfterFinalizationOrderUpdate(_summa){
    summa_int = summa_ext
    summa_com -= _summa
    summa_agr = _summa
    summa_ext = 0
    agrstatus = 5
    transtatus = 1
    return save(failOnError:true)
  }

  def moneySiteComissionExternOrderUpdate(_summa){
    summa_ext -= _summa
    summa_com = _summa
    return save(failOnError:true)
  }

  def moneySiteComissionInternOrderUpdate(_summa){
    summa_int -= _summa
    summa_com = _summa
    return save(failOnError:true)
  }

  def moneyOutExternOrderUpdate(_summa){
    summa_ext -= _summa
    summa_own = _summa
    return save(failOnError:true)
  }

  def moneyOutInternOrderUpdate(_summa){
    summa_int -= _summa
    summa_own = _summa
    return save(failOnError:true)
  }

  def declineDealOrderUpdate(){
    dealstatus = 2
    return save(failOnError:true)
  }

  def changeHomeOldExternOrderUpdate(_summa){
    summa_ext -= _summa
    modstatus = 2
    return save(failOnError:true)
  }

  def changeHomeOldInternOrderUpdate(_summa){
    summa_int -= _summa
    modstatus = 2
    return save(failOnError:true)
  }

  def updateTransferDetail(_in_paynumber,_in_paydate){
    in_paynumber = _in_paynumber
    try {
      in_paydate = Date.parse('dd.MM.yyyy',_in_paydate)
    } catch(Exception e) {
      in_paydate =  new Date()
    }
    return save(failOnError:true)
  }

  def updateTransferOutDetail(_out_paydate){
    try {
      out_paydate = Date.parse('dd.MM.yyyy',_out_paydate)
    } catch(Exception e) {
      try {
        out_paydate = Date.parse('yyyyMMdd',_out_paydate)
      } catch(Exception err) {
        out_paydate =  new Date()
      }
    }
    return save(failOnError:true)
  }

}