class Client {
  def searchService
  static mapping = {
    version false
  }

  static constraints = {
    parent(nullable:true)
    rule_id(nullable:true)
    ogrn(nullable:true)
    license(nullable:true)
    scanpassport(nullable:true)
    scandogovor(nullable:true)
    comment(nullable:true)
    type_id(nullable:true)
    moddate(nullable:true)
    admitdate(nullable:true)
    settlprocedure_en(nullable:true)    
  }

  Long id
  Long parent
  String name
  Date inputdate
  Integer modstatus
  Integer rule_id
  Integer is_notification = 0
  Integer addrating = 0
  String code

  String payee = ''
  String inn = ''
  String kpp = ''
  String bankname = ''
  String bik = ''
  String corraccount = ''
  String settlaccount = ''
  Integer nds = 0
  String paycomment = ''
  String paymessage = ''
  String yandex = ''
  String webmoney = ''
  String margin = ''

  Integer is_reserve = 1
  Integer is_offeradmit = 0
  Integer resstatus = 1
  Integer country_id = 0
  Integer valuta_id = 0
  Integer reserve_id = 3
  Integer is_transferauto = 1
  Integer payaccount_id = 0
  String ogrn = ''
  String license = ''
  String scanpassport = ''
  String scandogovor = ''
  String settlprocedure = 'окончательный расчет за аренду при заселении'
  Integer type_id = 0
  String comment = ''
  String pcard = ''
  Date moddate = new Date()
  Date admitdate
  String ip = ''

  String settlprocedure_en = ''

  String toString() {"${this.name}"}
  //////////////////////////////////////////////////////////////////////////////////////
  def csiGetLastInsert(){
    return searchService.getLastInsert()
  }

  void billingDetailsUpdate(_inrequest){
    try {
      payee = _inrequest?.payee?:payee
      inn = _inrequest?.inn?:inn
      if((_inrequest?.type_id?:0)==2){
        kpp = _inrequest?.kpp?:kpp
      }else{
        kpp = ''
      }      
      bankname = _inrequest?.bankname?:bankname
      bik = _inrequest?.bik?:bik
      corraccount = _inrequest?.corraccount?:corraccount
      settlaccount = _inrequest?.settlaccount?:settlaccount
      if((_inrequest?.type_id?:0)==2){
        nds = _inrequest?.nds?:0
        pcard='' 
      }else{
        nds=0
        pcard= _inrequest?.pcard?:''      
      }  
      paycomment = _inrequest?.paycomment?:''
      paymessage = _inrequest?.paymessage?:''
      margin = _inrequest?.margin?:''
      save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  Client csiSetAdmit(){
    is_offeradmit = 1
    admitdate = new Date()
    this
  }

  void payoutDetailsUpdate(_inrequest,_ip){
    try {
      payaccount_id = _inrequest?.payaccount_id?:payaccount_id
      reserve_id = _inrequest?.reserve_id?:reserve_id
      country_id = _inrequest?.country_id?:country_id
      type_id = _inrequest?.type_id?:type_id
      ip = _ip?:''
      settlprocedure = _inrequest?.settlprocedure?:settlprocedure
      moddate = new Date()
      if(!(reserve_id in 3..4)){
        switch(resstatus) {
          case 0:
            resstatus = 2
            break
          case -1:
          case 1:
            resstatus = 3
          break
        }
        is_reserve = 0
      } else {
        resstatus = 1
        is_reserve = 1
      }
      csiSetAdmit()
      save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  void payoutDetailsModerate(_inrequest){
    payaccount_id = _inrequest?.payaccount_id?:payaccount_id
    reserve_id = _inrequest?.reserve_id?:reserve_id
    country_id = _inrequest?.country_id?:country_id
    inn = _inrequest?.inn?:''
    ogrn = _inrequest?.ogrn?:''
    license = _inrequest?.license?:''
    comment = _inrequest?.comment?:''
    resstatus = _inrequest?.resstatus?:resstatus
    type_id = _inrequest?.type_id?:type_id
    is_reserve = (_inrequest?.resstatus==1)?1:(_inrequest?.resstatus==-1)?0:is_reserve
    settlprocedure = _inrequest?.settlprocedure?:''
    if(payaccount_id==1){//ðàñ÷åòíûé ñ÷åò
      settlaccount = _inrequest?.settlaccount?:settlaccount
      payee = _inrequest?.payee?:payee
      inn = _inrequest?.inn?:inn
      if((_inrequest?.type_id?:0)==2){
        kpp = _inrequest?.kpp?:kpp
      }else{
        kpp = ''
      }
      bankname = _inrequest?.bankname?:bankname
      bik = _inrequest?.bik?:bik
      corraccount = _inrequest?.corraccount?:corraccount
      settlaccount = _inrequest?.settlaccount?:settlaccount
      if((_inrequest?.type_id?:0)==2){
        nds = _inrequest?.nds?:0
        pcard = ''
      }else{
        nds = 0
        pcard= _inrequest?.pcard?:''
      }
      paycomment = _inrequest?.paycomment?:''
      webmoney = ''
    }else if(payaccount_id==3){
      webmoney = _inrequest?.webmoney?:webmoney
      settlaccount = ''
      payee = ''
      kpp = ''
      bankname = ''
      bik = ''
      corraccount = ''
      settlaccount = ''
      nds = 0
      pcard = ''
    }else{
      webmoney = ''
      settlaccount = ''
      payee = ''
      inn = ''
      kpp = ''
      bankname = ''
      bik = ''
      corraccount = ''
      settlaccount = ''
      nds = 0
      pcard = ''
    }
    save(flush:true)
  }

  void cleanDetails(){
    try {
      webmoney = ''
      settlaccount = ''
      payee = ''
      inn = ''
      kpp = ''
      bankname = ''
      bik = ''
      corraccount = ''
      settlaccount = ''
      nds = 0
      pcard = ''
      payaccount_id = 0
      save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  void yandexDetailsUpdate(_inrequest){
    try {
      yandex = _inrequest?.yandex?:yandex
      save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  void webmoneyDetailsUpdate(_inrequest){
    try {
      webmoney = _inrequest?.webmoney?:webmoney
      save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  void savescanpassport(_data){
    try {
      scanpassport = _data?.filename?:''
      save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  void savescandogovor(_data){
    try {
      scandogovor = _data?.filename?:''
      save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  Client updateName(_name){
    name = _name?:name
    this
  }

}