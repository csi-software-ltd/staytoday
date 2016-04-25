class Partnerway {
  static mapping = { version false }

  Integer id
  String name
  String name_en
  String description
  String description_en
  Integer modstatus

  String toString() { "${name}" }
}