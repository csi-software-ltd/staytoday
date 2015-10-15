class Paycountry {

  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  Integer country_id
  Integer payaccount_id
  String description
  String description_en
  String term
  String term_en
  String percent

  String toString() { "${name}" }
}
