class HomestepAdv {

  static mapping = {
    table 'DUMMY_NAME'
    version false
    cache false
  }

  static constraints = {
  }

  Integer id  
  String name
  String name_en
  String term
  String controller
  String action
  String text_yes
  String text_yes_en
  String text_no
  String text_no_en
  Integer stepgroup
  String comments 
  String nt_name
  String icon
  String notetext
  String notetext_en
  Integer email_template_id
  Integer max_notes
  Integer dayinterval
  Integer param
  Integer modstatus
  Boolean is_done
}
