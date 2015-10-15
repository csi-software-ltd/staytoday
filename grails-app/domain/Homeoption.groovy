class Homeoption {  
  
  static mapping = {
    version false
  }	  
  
  static constraints = { 
    fieldname(nullable:true)  
  }
  
  Integer id  
  String name
  String name_en
  String comments
  String comments_en
  String fieldname
  String icon
  Integer facilitygroup_id
  Integer regorder 
  Integer type
  Integer modstatus  
}
