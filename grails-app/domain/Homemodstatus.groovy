class Homemodstatus {    
  
  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  Integer modstatus
  String name
  String name_en
  String icon

  String toString() {"${this.name}"}
}
