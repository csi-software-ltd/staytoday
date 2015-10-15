class Region {    
  
  static constraints = {
	name(nullable:true)	
	shortname(nullable:true)	
	tel_code(nullable:true)
	country_id(nullable:true)	
	is_map(nullable:true)
	showonmap(nullable:true)	
	x(nullable:true)
	y(nullable:true)
	scale(nullable:true)
	regorder(nullable:true)	
	is_metro(nullable:true)
	parent(nullable:true)
	title(nullable:true)
  is_district(nullable:true)
  name_en(nullable:true)	
	shortname_en(nullable:true)	
  }
  static mapping = {
    version false
  }
  Integer id
  String name
  String title
  String shortname
  String tel_code = ''
  Integer is_map = 1
  Integer modstatus = 0
  Integer showonmap = 0
  Integer x = 0
  Integer y = 0
  Integer scale = 0
  Integer regorder = 0
  Integer is_metro = 0
  Integer parent = 0
  Integer country_id
  Integer is_district
  Integer timediff = 0
  Integer is_show = 1
  Integer popdirection_id = 0
  
  String name_en
  String shortname_en

  String toString() {"${this.name}" }  
}
