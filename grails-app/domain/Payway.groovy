class Payway {

  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  String name
  String name_en
  String icon
  Integer modstatus
  Float fee
  Integer limit
  Integer agr_id
  Integer is_invoice
  String description
  String description_en
  String payumethod

  String toString() {"${this.name}" }
}
