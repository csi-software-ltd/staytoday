class Video_lesson {    
  
  static constraints = {
    picture(nullable:true)    
    type_id(nullable:true)
  }
  static mapping = {
    version false
  }
  
  Integer id
  Integer type_id
  Integer nomer
  String name  
  String picture
  String video  
}
