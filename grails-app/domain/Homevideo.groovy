class Homevideo {  
  
  static mapping = {
    version false
  }	  
  
  static constraints = {
    vtext(nullable:true)  
  }
  
  Integer id  
  Long home_id
  String code 
  String videoid
  Integer modstatus
  Integer norder  
  Date moddate
  String vtext 
}
