class Notebyemail {

  static constraints = {
  }
  static mapping = {
    version false
  }
  Long id
  Long home_id
  Long user_id
  Integer email_template_id
  String email
  Integer notetype_id
  Integer modstatus = 0
  Date inputdate = new Date()
////////////////////////constructor//////////////////////////////////////////////////////
  Notebyemail(){}
  Notebyemail(lHid, oUser, oNotetype){
	home_id = lHid
	user_id = oUser.id
	email_template_id = oNotetype.email_template_id
	email = oUser.email
	notetype_id = oNotetype.id
  }
//////////////////////////////////////////////////////////////////////////////////////////
}