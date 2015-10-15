class Mbox {

  static constraints = {
    price(nullable:true)
    price_rub(nullable:true)
    valuta_id(nullable:true)
    is_telperm(nullable:true)
    is_owfav(nullable:true)
    is_clfav(nullable:true)
    answertype_id(nullable:true)
    timezone(nullable:true)
    moddate(nullable:true)
    inputdate(nullable:true)
  }

  static mapping = {
    version false
  }

  Long id
  Date date_start
  Date date_end
  Integer homeperson_id
  String mtext
  String mtextowner = ''
  String comment = ''
  Integer valuta_id
  Long price_rub
  Long price
  Integer is_telperm
  Integer answertype_id
  Long user_id
  Long home_id
  Long homeowner_cl_id
  Integer timezone
  Integer modstatus
  Integer is_answer
  Integer is_read
  Integer is_readclient = 1
  Integer is_readowner = 1
  Integer is_approved
  Integer is_adminread = 0
  Integer is_clfav
  Integer is_owfav
  Date inputdate
  Date moddate
  Date lastusermessagedate = new Date()
  Long zayvka_id = 0
  Integer controlstatus = 0
  Long responsetime = 0
//////////////////////////////////////////////////////////////////

  Mbox adminread(){
    is_adminread = 1
    save(flush:true)
  }
  Mbox commentadd(sText){
    comment = sText?:''
    this
  }

  Integer getTripLength(){
    Integer iDays = date_end - date_start
    if (Home.get(home_id)?.is_pricebyday) {
      iDays++
    }
    iDays?:0
  }

}
