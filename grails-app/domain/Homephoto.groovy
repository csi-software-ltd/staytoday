class Homephoto {  
  
  static mapping = {
    version false
  }	  
  
  static constraints = {    
  }
  
  Integer id  
  Long home_id
  String picture  
  Integer modstatus
  Integer norder
  Integer is_main
  Integer is_verified = 0
  Date moddate
  String ptext 
}