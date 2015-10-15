class Articles_tags {
  static constraints = {
  }
  static mapping = {
    version false
  }

  Integer id
  String name
  String header = ''
  String header_en = ''
  static hasMany = [articles:Articles]

  String toString() {
  	"${name}"
  }
}
