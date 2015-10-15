class Homediscount {

  static mapping = {
    version false
  }

  static constraints = {
  }

  Long id
  Integer modstatus
  Integer type
  Integer discount
  Integer minrentdays
  Integer discexpiredays
  String terms

  Homediscount(){}
  Homediscount(iType,iDisc,iMinRent,iDiscExp,sTerms){
    modstatus = 1
    type = iType?:1
    discount = iDisc?:95
    minrentdays = iMinRent?:0
    discexpiredays = iDiscExp?:(iType==1)?45:5
    terms = sTerms?:''
  }

  static void processDiscount(inrequest) {
    if (!inrequest.discounttype || inrequest.discounttype>2) throw new Exception ('Type not specified')
    if (!inrequest.id) throw new Exception ('Home_id not specified')
    def oHome = Home.get(inrequest.id)
    if (!oHome) throw new Exception ('Where are no Homes with specified id')

    def oDiscount
    if (!(oDiscount = Homediscount.get((inrequest.discounttype==1)?oHome.longdiscount_id:oHome.hotdiscount_id)))
      oDiscount = new Homediscount(inrequest.discounttype,inrequest.discount,inrequest.minrentdays,inrequest.discexpiredays,inrequest.terms)
    else
      oDiscount.updateDiscount(inrequest)

    try {
      oDiscount.save(flush:true)
      if(inrequest.discounttype==1)
        oHome.longdiscount_id = oDiscount.id
      else
        oHome.hotdiscount_id = oDiscount.id
      oHome.save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  void updateDiscount(_inrequest) {
    modstatus = 1
    discount = _inrequest.discount?:95
    minrentdays = _inrequest.minrentdays?:0
    discexpiredays = _inrequest.discexpiredays?:(type==1)?45:5
    terms = _inrequest.terms?:''
  }

  static void disableDiscount(inrequest) {
    if (!inrequest.type || inrequest.discounttype>2) throw new Exception ('Type not specified')
    if (!inrequest.id) throw new Exception ('Home_id not specified')
    def oHome = Home.get(inrequest.id)
    if (!oHome) throw new Exception ('Where are no Homes with specified id')

    def oDiscount
    if(inrequest.type==1)
      oDiscount = Homediscount.get(oHome.longdiscount_id)
    else
      oDiscount = Homediscount.get(oHome.hotdiscount_id)
    if (!oDiscount) throw new Exception ('Where are no discounts with same type belongs to home id: '+inrequest.id)

    try {
      oDiscount.modstatus = 0
      oDiscount.save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

  static void dropStatus(_oHome) {
    def oLong = Homediscount.get(_oHome.longdiscount_id?:0)
    def oHot = Homediscount.get(_oHome.hotdiscount_id?:0)
    if (oLong) oLong.modstatus = 0
    if (oHot) oHot.modstatus = 0
    try {
      oLong?.save(flush:true)
      oHot?.save(flush:true)
    } catch(Exception e) {
      throw e
    }
  }

}