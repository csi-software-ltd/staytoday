class Clienthistory {
  
  static mapping = {
    version false
  }

  static constraints = {       
    ogrn(nullable:true)
    license(nullable:true)
    comment(nullable:true)
    type_id(nullable:true)
  }
  
  Long id 
  Long client_id
  String name
  Date moddate  
  String inn = ''
  String kpp = ''
  String bankname = ''
  String bik = ''
  String webmoney = ''  
  String corraccount = ''
  String settlaccount = ''
  String pcard = ''
  String ogrn = ''
  String license = ''
  Integer reserve_id = 0
  Integer is_transferauto = 1
  Integer payaccount_id = 0
  Integer type_id = 0
  Integer is_client=0
  Integer resstatus = 0
  String payee = ''  
  String comment = ''
  String ip = ''

  String toString() {"${this.name}"}
  //////////////////////////////////////////////////////////////////////////////////////
  
  void setData(oClient,bFrom){
    try {
      name = oClient?.name?:''
      client_id = oClient?.id?:0
      payee = oClient?.payee?:''
      inn = oClient?.inn?:''
      kpp = oClient?.kpp?:''
      bankname = oClient?.bankname?:''
      bik = oClient?.bik?:''
      corraccount = oClient?.corraccount?:''
      settlaccount = oClient?.settlaccount?:''
      pcard= oClient?.pcard?:''
      moddate=new Date()
      webmoney = oClient?.webmoney?:''
      ogrn = oClient?.ogrn?:''
      license = oClient?.license?:''
      reserve_id = oClient?.reserve_id?:0 
      is_transferauto = oClient?.is_transferauto?:1
      payaccount_id = oClient?.payaccount_id?:1
      type_id = oClient?.type_id?:1
      is_client = bFrom
      resstatus = oClient?.resstatus?:1
      comment = oClient?.comment?:''
      ip = oClient?.ip?:''

      save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  void preSaveData(_client,_inrequest,_ip){
    try {
      name = _client?.name?:''
      client_id = _client?.id?:0
      payee = _inrequest?.payee?:''
      inn = _inrequest?.inn?:''
      kpp = _inrequest?.kpp?:''
      bankname = _inrequest?.bankname?:''
      bik = _inrequest?.bik?:''
      corraccount = _inrequest?.corraccount?:''
      settlaccount = _inrequest?.settlaccount?:''
      pcard= _inrequest?.pcard?:''
      moddate=new Date()
      webmoney = _inrequest?.webmoney?:''
      ogrn = _inrequest?.ogrn?:''
      license = _inrequest?.license?:''
      reserve_id = _inrequest?.reserve_id?:0 
      is_transferauto = _inrequest?.is_transferauto?:1
      payaccount_id = _inrequest?.payaccount_id?:1
      type_id = _inrequest?.type_id?:1
      is_client = 2
      switch(_client?.resstatus) {
        case 0:
          resstatus = 2
          break
        case -1:
        case 1:
          resstatus = 3
          break
        case 2:
        case 3:
          resstatus = 3
          break
        default:
          resstatus = 1
          break
      }
      comment = _inrequest?.comment?:''
      ip = _ip?:''

      save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

}