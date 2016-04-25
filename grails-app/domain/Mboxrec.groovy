class Mboxrec {
  static constraints = {   
    answertype_id(nullable:true)    
    inputdate(nullable:true)   
    rectext(nullable:true)
    is_answer(nullable:true)
    home_id(nullable:true)
    homeperson_id(nullable:true)
    date_start(nullable:true)
    date_end(nullable:true)
    rule_minday_id(nullable:true)
    rule_maxday_id(nullable:true)
    price(nullable:true) 
    price_rub(nullable:true)
    valuta_id(nullable:true) 	
  }
  
  static mapping = {    
    version false   
  }

  Long id
  Long mbox_id
  Integer answertype_id
  String rectext
  Integer is_answer
  Integer is_approved = 1
  Integer admin_id = 0
  Integer is_system = 0
  Date inputdate
  Long home_id
  Integer homeperson_id
  Date date_start
  Date date_end
  Integer rule_minday_id
  Integer rule_maxday_id
  Long price
  Long price_rub
  Integer valuta_id

///////////////////////////////////////////////////////////////////////////////////////////////////////
  def afterInsert() {
    if(is_answer&&answertype_id in [1,2,3,4,5,7,9]) Mbox.withNewSession{ Mbox.get(mbox_id)?.updateRating(Tools.getIntVal(Dynconfig.findByName(answertype_id==1?'mbox.rating.mb_offer':answertype_id==2?'mbox.rating.mb_specoffer':answertype_id in 3..5?'mbox.rating.mb_decline':answertype_id==7?'mbox.rating.mb_bookconfirm':'mbox.rating.mb_cancel')?.value,0))?.save(flush:true) }
  }

  Mboxrec setmboxData(_oMbox){
    mbox_id = _oMbox.id
    answertype_id = 0
    rectext = ''
    is_answer = 0
    inputdate = new Date()
    home_id = _oMbox.home_id
    homeperson_id = _oMbox.homeperson_id
    date_end = _oMbox.date_end
    date_start = _oMbox.date_start
    price = _oMbox.price
    price_rub = _oMbox.price_rub
    valuta_id = _oMbox.valuta_id
    this
  }

}