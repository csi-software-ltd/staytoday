class Mboxmodstatus {    
  
  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  Integer modstatus
  String name
  String name_en
  String color
  Integer is_open
  
  String toString() {"${this.name}"}
}
