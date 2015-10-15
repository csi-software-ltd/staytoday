class Reserve {    

  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  String name
  String name_en
  String description
  String description_en
  Integer modstatus
  Integer paycomtype
  Integer is_main

  String toString() { "${name}" } 
}
