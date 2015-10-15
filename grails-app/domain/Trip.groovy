import org.codehaus.groovy.grails.commons.ConfigurationHolder
class Trip {  
  
  static mapping = {
    version false
  }

  static constraints = {
  }

  Long id
  Long home_id
  Long user_id
  Long payorder_id
  Date fromdate
  Date todate
  Date inputdate
  Integer homeperson_id
  Long price
  Long price_rub
  Integer valuta_id
  Integer rating
  Integer modstatus
  Integer paystatus
  Integer controlstatus
  Integer rule_cancellation_id
  Long mbox_id
  Integer is_read

  Trip(){}
  Trip(oMbox,lPayorderId=0,iPaymentStatus=0){
    home_id = oMbox.home_id
    user_id = oMbox.user_id
    payorder_id = lPayorderId
    fromdate = new Date(oMbox.date_start.getTime())
    todate = new Date(oMbox.date_end.getTime())
    homeperson_id = oMbox.homeperson_id
    price = lPayorderId?Math.round(oMbox.price * (1.0 + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100))):oMbox.price
    price_rub = lPayorderId?Math.round(oMbox.price_rub * (1.0 + (Tools.getIntVal(ConfigurationHolder.config.clientPrice.modifier,4) / 100))):oMbox.price_rub
    valuta_id = oMbox.valuta_id
    rule_cancellation_id = Home.get(oMbox.home_id).rule_cancellation_id?:1
    mbox_id = oMbox.id
    rating = 0
    modstatus = 0
    paystatus = iPaymentStatus
    controlstatus = lPayorderId?0:-1
    inputdate = new Date()
    is_read = 1
  }
////////////////////////////////////////////////////////////////////////////////////////////////////

  Trip readevent(){
    is_read = 1
    this
  }

}