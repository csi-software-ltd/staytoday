class Paytransfer {
  def searchService

  static constraints = {
  }
  static mapping = {
    //datasource 'admin'
    version false
  }
  Long id
  String paydate = ''
  String paynomer = ''
  Double summa = 0d
  Double summa_com = 0d
  String paycomment = ''
  Long payorder_id = 0
  String paylist = ''
  Integer paytype = 0
  String from_account = ''
  String from_name = ''
  String from_inn = ''
  String from_coraccount = ''
  String from_bik = ''
  String from_bank = ''
  String to_account = ''
  String to_name = ''
  String to_inn = ''
  String to_coraccount = ''
  String to_bik = ''
  String to_bank = ''
  Date moddate = new Date()
  Integer modstatus = 0
  Integer admin_id = 0
  Integer type = 0
  Integer vid = 0
  Integer valuta = 0
  Integer to_cod = 0

  def beforeUpdate() {
    moddate = new Date()
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectPaytransfer(lId,iModstatus,iPaytype,lPayorderId,sPayDateFrom,sPayDateTo,sModifyDateFrom,sModifyDateTo,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='paytransfer'
    hsSql.where="1=1"+
        ((lId>0)?' AND id =:id':'')+
        ((iModstatus>-1)?' AND modstatus =:modstatus':'')+
        ((iPaytype>-1)?' AND paytype =:paytype':'')+
        ((sPayDateFrom!='')?' AND paydate >=:paydateFrom':'')+
        ((sPayDateTo!='')?' AND paydate <=:paydateTo':'')+
        ((sModifyDateFrom!='')?' AND moddate >=:moddateFrom':'')+
        ((sModifyDateTo!='')?' AND moddate <=:moddateTo':'')+
        ((lPayorderId>0)?' AND payorder_id =:payorder_id OR paylist like CONCAT("%",:string_payorder_id,"%")':'')
    hsSql.order="id DESC"

    if(lId>0)
      hsLong['id']=lId
    if(iModstatus>-1)
      hsInt['modstatus']=iModstatus
    if(iPaytype>-1)
      hsInt['paytype']=iPaytype
    if(sPayDateFrom!='')
      hsString['paydateFrom']=sPayDateFrom
    if(sPayDateTo!='')
      hsString['paydateTo']=sPayDateTo
    if(sModifyDateFrom!='')
      hsString['moddateFrom']=sModifyDateFrom
    if(sModifyDateTo!='')
      hsString['moddateTo']=sModifyDateTo
    if(lPayorderId>0){
      hsLong['payorder_id']=lPayorderId
      hsString['string_payorder_id']=lPayorderId.toString()
    }

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,hsInt,hsString,
      null,null,iMax,iOffset,'id',true,Paytransfer.class)
  }

  static def createFromCsvLine(lsData,lsTitles){
    if (lsTitles[0]==-1||!lsData[lsTitles[0]])
      throw new Exception ('paydate was not set')
    if (lsTitles[1]==-1||!lsData[lsTitles[1]])
      throw new Exception ('paynomer was not set')
    if (lsTitles[5]==-1||!lsData[lsTitles[5]])
      throw new Exception ('from_name was not set')
    if (lsTitles[2]==-1||!lsData[lsTitles[2]])
      throw new Exception ('summa was not set')
    def oAccountdata = Accountdata.findByModstatus(1)
    def dSumma = lsData[lsTitles[2]].replace(',','.').isDouble()?lsData[lsTitles[2]].replace(',','.').toDouble():0d
    if (Paytransfer.findByPaydateAndPaynomerAndFrom_name(lsData[lsTitles[0]],lsData[lsTitles[1]],dSumma>0?(lsData[lsTitles[5]]):oAccountdata.payee))
      throw new Exception ('Not unique paytransfer identifier')
    def oPaytransfer = new Paytransfer()
    oPaytransfer.paydate = lsData[lsTitles[0]]
    oPaytransfer.paynomer = lsData[lsTitles[1]]
    oPaytransfer.summa = dSumma
    oPaytransfer.paycomment = lsData[lsTitles[3]]?:''
    def lsPayorders = (lsData[lsTitles[3]]?:'').findAll(/st\d+/)
    if (lsPayorders.size()==1) {
      oPaytransfer.payorder_id = Payorder.findByNorder(lsPayorders[0])?.id?:0
      oPaytransfer.paylist = ''
    } else if (lsPayorders.size()>1) {
      oPaytransfer.payorder_id = 0
      oPaytransfer.paylist = lsPayorders.collect{Payorder.findByNorder(it)?.id?.toString()?:''}.join(',')
    } else {
      oPaytransfer.payorder_id = 0
      oPaytransfer.paylist = ''
    }
    oPaytransfer.from_account = oPaytransfer.summa>0?(lsData[lsTitles[4]]?:''):oAccountdata.settlaccount
    oPaytransfer.from_name = oPaytransfer.summa>0?(lsData[lsTitles[5]]):oAccountdata.payee
    oPaytransfer.from_inn = oPaytransfer.summa>0?(lsData[lsTitles[6]]?:''):oAccountdata.inn
    oPaytransfer.from_coraccount = oPaytransfer.summa>0?(lsData[lsTitles[7]]?:''):oAccountdata.corraccount
    oPaytransfer.from_bik = oPaytransfer.summa>0?(lsData[lsTitles[8]]?:''):oAccountdata.bik
    oPaytransfer.from_bank = oPaytransfer.summa>0?(lsData[lsTitles[9]]?:''):oAccountdata.bankname
    oPaytransfer.to_account = oPaytransfer.summa<0?(lsData[lsTitles[4]]?:''):oAccountdata.settlaccount
    oPaytransfer.to_name = oPaytransfer.summa<0?(lsData[lsTitles[5]]?:''):oAccountdata.payee
    oPaytransfer.to_inn = oPaytransfer.summa<0?(lsData[lsTitles[6]]?:''):oAccountdata.inn
    oPaytransfer.to_coraccount = oPaytransfer.summa<0?(lsData[lsTitles[7]]?:''):oAccountdata.corraccount
    oPaytransfer.to_bik = oPaytransfer.summa<0?(lsData[lsTitles[8]]?:''):oAccountdata.bik
    oPaytransfer.to_bank = oPaytransfer.summa<0?(lsData[lsTitles[9]]?:''):oAccountdata.bankname
    if(oPaytransfer.from_name==oAccountdata.payee)
      oPaytransfer.paytype = 3
    else if (oPaytransfer.to_name==oAccountdata.payee)
      oPaytransfer.paytype = 1
    else
      oPaytransfer.paytype = 0
    if (oPaytransfer.payorder_id||oPaytransfer.paylist) {
      oPaytransfer.modstatus = 0
    } else {
      oPaytransfer.modstatus = 2
    }
    oPaytransfer.admin_id = 0
    oPaytransfer.type = lsData[lsTitles[16]].isInteger()?lsData[lsTitles[16]].toInteger():0
    oPaytransfer.vid = lsData[lsTitles[17]].isInteger()?lsData[lsTitles[17]].toInteger():0
    oPaytransfer.valuta = lsData[lsTitles[18]].isInteger()?lsData[lsTitles[18]].toInteger():0
    oPaytransfer.to_cod = lsData[lsTitles[19]].isInteger()?lsData[lsTitles[19]].toInteger():0
    return oPaytransfer.save()
  }

  static def createWMExpensesFromHtmlLine(lsData){
    if (!lsData[0])
      throw new Exception ('paydate was not set')
    if (!lsData[5])
      throw new Exception ('to_name was not set')
    if (!lsData[2])
      throw new Exception ('summa was not set')
    def oAccountdata = Accountdata.findByModstatus(2)
    def dSumma = lsData[2].replace(',','.').isDouble()?lsData[2].replace(',','.').toDouble():0d
    def dSummaCom = lsData[3].replace(',','.').isDouble()?lsData[3].replace(',','.').toDouble():0d
    if (Paytransfer.findByPaydateAndPaynomerAndFrom_name(lsData[0],'',oAccountdata.payee))
      throw new Exception ('Not unique paytransfer identifier')
    def oPaytransfer = new Paytransfer()
    oPaytransfer.paydate = lsData[0]
    oPaytransfer.paynomer = ''
    oPaytransfer.summa = dSumma
    oPaytransfer.summa_com = dSummaCom
    oPaytransfer.paycomment = lsData[6]?:''
    def lsPayorders = (lsData[6]?:'').findAll(/st\d+/)
    if (lsPayorders.size()==1) {
      oPaytransfer.payorder_id = Payorder.findByNorder(lsPayorders[0])?.id?:0
      oPaytransfer.paylist = ''
    } else if (lsPayorders.size()>1) {
      oPaytransfer.payorder_id = 0
      oPaytransfer.paylist = lsPayorders.collect{Payorder.findByNorder(it)?.id?.toString()?:''}.join(',')
    } else {
      oPaytransfer.payorder_id = 0
      oPaytransfer.paylist = ''
    }
    oPaytransfer.from_account = oAccountdata.corraccount
    oPaytransfer.from_name = oAccountdata.payee
    oPaytransfer.from_inn = oAccountdata.inn
    oPaytransfer.from_bank = oAccountdata.bankname
    oPaytransfer.to_account = lsData[5]?:''
    oPaytransfer.paytype = 3
    oPaytransfer.type = 2

    if (oPaytransfer.payorder_id||oPaytransfer.paylist) {
      oPaytransfer.modstatus = 0
    } else {
      oPaytransfer.modstatus = 2
    }
    return oPaytransfer.save()
  }

  void updateModstatus (iStatus=0){
    modstatus=iStatus
    save()
  }

}