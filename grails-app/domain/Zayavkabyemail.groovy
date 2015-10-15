class Zayavkabyemail {

  static constraints = {
  }
  static mapping = {
    version false
  }
  Long id
  String email
  String name
  String city
  String ztext
  Date date_start
  Date date_end
  Date inputdate = new Date()
  Integer modstatus = 0
  String e_template

////////////////////////constructor//////////////////////////////////////////////////////
  Zayavkabyemail(){}
  Zayavkabyemail(sEmail,sName,sCity,sZtext,dDateS,dDateE,sEtemplate="#emailProposal"){
    email = sEmail
    name = sName
    city = sCity
    ztext = sZtext
    date_start = dDateS
    date_end = dDateE
    e_template = sEtemplate
  }
/////////////////////////////////////////////////////////////////////////////////////////
}