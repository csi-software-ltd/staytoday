class Account {
  def searchService

  static constraints = {
  }
  static mapping = {
    //datasource 'admin'
    version false
  }
  Long id
  String code
  Long client_id
  Date inputdate = new Date()
  Integer modstatus = 1
  Date moddate = new Date()
  Integer type_id = 1
  Integer summa = 0
  Integer summa_fin = 0
  Integer summa_serv = 0
  Integer summa_com = 0
  Integer summa_adv = 0
  Integer summa_ext = 0

  static def getInstanceForOwner(_lClient_id){
    def oAccount = Account.findByClient_id(_lClient_id)
    if (!oAccount) {
      oAccount = new Account(client_id:_lClient_id,code:Tools.generateAccountcode(_lClient_id,1)).save()
    }
    return oAccount
  }

  def moneyEarningsAccountUpdate(_summa){
    summa += _summa
    return save(failOnError:true)
  }

  def moneyReturnAccountUpdate(_summa){
    summa -= _summa
    return save(failOnError:true)
  }

  def moneyEarningsExternAccountUpdate(_summa){
    summa_ext += _summa
    return save(failOnError:true)
  }

  def moneyReturnExternAccountUpdate(_summa){
    summa_ext -= _summa
    return save(failOnError:true)
  }

  def moneyExternToInternAccountUpdate(_summaInt,_summaExt){
    summa += _summaInt
    summa_ext -= _summaExt
    return save(failOnError:true)
  }

  def moneyExternToInternAfterFinalizationAccountUpdate(_summa){
    summa += _summa
    summa_ext -= _summa
    return save(failOnError:true)
  }

  def moneySiteComissionExternAccountUpdate(_summa){
    summa_ext -= _summa
    summa_com += _summa
    return save(failOnError:true)
  }

  def moneySiteComissionInternAccountUpdate(_summa){
    summa -= _summa
    summa_com += _summa
    return save(failOnError:true)
  }

  def moneyOutExternAccountUpdate(_summa){
    summa_ext -= _summa
    summa_fin += _summa
    return save(failOnError:true)
  }

  def moneyOutInternAccountUpdate(_summa){
    summa -= _summa
    summa_fin += _summa
    return save(failOnError:true)
  }

  def changeHomeOldExternAccountUpdate(_summa){
    summa_ext -= _summa
    return save(failOnError:true)
  }

  def changeHomeOldInternAccountUpdate(_summa){
    summa -= _summa
    return save(failOnError:true)
  }

  def changeHomeNewExternAccountUpdate(_summa){
    summa_ext += _summa
    return save(failOnError:true)
  }

  def changeHomeNewInternAccountUpdate(_summa){
    summa += _summa
    return save(failOnError:true)
  }

  def doConfirmationPayment(_summa){
    summa_com += _summa
    return save(failOnError:true)
  }

  def csiSelectAccount(sCode,iModstatus,lClientId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='account'
    hsSql.where="1=1"+
        ((sCode!='')?' AND code like CONCAT("%",:code,"%")':'')+
        ((iModstatus>-1)?' AND modstatus =:modstatus':'')+
        ((lClientId>0)?' AND client_id =:client_id':'')
    hsSql.order="id DESC"

    if(sCode!='')
      hsString['code']=sCode
    if(iModstatus>-1)
      hsInt['modstatus']=iModstatus
    if(lClientId>0)
      hsLong['client_id']=lClientId

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,hsInt,hsString,
      null,null,iMax,iOffset,'id',true,Account.class)
  }

}