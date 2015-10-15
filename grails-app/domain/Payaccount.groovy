class Payaccount {    

  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  String name
  String name_en

  String toString() { "${name}" } 
}
