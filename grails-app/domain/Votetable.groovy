class Votetable {

  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  Integer modstatus
  Integer rating_1
  Integer rating_2
  Integer rating_3
  String name
  Integer sum
  
  String toString() {"${this.name}" }
}
