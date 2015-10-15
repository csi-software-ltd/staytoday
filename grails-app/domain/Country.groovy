class Country {    
  
  static constraints = {
    name(nullable:true)
    name_en(nullable:true)
    icon(nullable:true)	
    regorder(nullable:true)	
	
  }
  static mapping = {
    version false
  }
  Integer id
  String name
  String name_en
  String urlname
  String icon
  String header
  String title
  String keywords
  String description
  String description_en
  String itext
  String itext_en
  Integer regorder  
  Integer modstatus
  Integer is_reserve = 0
  Integer is_index = 0
  Integer is_popcities = 0
  Integer valuta_id = 0

  String toString() { "${name}" } 
}
