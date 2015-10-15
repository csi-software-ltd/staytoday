class Transfer {

  static constraints = {
  }
  static mapping = {
    version false
  }
  Long id
  String nomer
  String from_account
  String from_coraccount
  String from_bank
  String from_bik
  String from_name
  String to_account
  String to_coraccount
  String to_bank
  String to_bik
  String to_name
  Integer summa
  Integer order_id
  Integer paytype_id
  Integer admin_id
  String orderlist
  Date transdate
  Date inputdate
  Date moddate
  Integer modstatus
  Integer transtatus

}