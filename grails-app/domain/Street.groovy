class Street {      
  static constraints = {
    district_id(nullable:true)	
    strtype_id(nullable:true)
    street(nullable:true)
  }
  static mapping = {
    version false
  }
  Integer id
  String name  
  String name_en
  Integer district_id 
  Integer region_id
  Long city_id
  String street
  Integer strtype_id
  Integer modstatus  

  String toString() {"${this.name}" }  
}
