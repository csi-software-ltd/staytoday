class Note {

  static constraints = {
  }
  static mapping = {
    version false
  }
  Long id
  Long home_id
  Long user_id
  String notetext
  String notetext_en
  Integer notetype_id
  Integer modstatus = 1
  Date inputdate = new Date()
////////////////////////constructor//////////////////////////////////////////////////////
  Note(){}
  Note(lHid, lUid, oNotetype){
	home_id = lHid
	user_id = lUid
  notetext = oNotetype.notetext?:''
	notetext_en = oNotetype.notetext_en?:''
	notetype_id = oNotetype.id
  }
//////////////////////////////////////////////////////////////////////////////////////////
}
