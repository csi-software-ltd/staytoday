class Admingroup {

  static constraints = {
  }
  static mapping = {
    version false
  }
  Integer id
  String name

  String menu
  Integer is_superuser
  Integer is_profile
  Integer is_groupmanage
  Integer is_users  
  Integer is_homes
  Integer is_reviews
  Integer is_adress
  Integer is_banners
  Integer is_infotext
  Integer is_popdir
  Integer is_stats
  Integer is_guestbook
  Integer is_notetype
  Integer is_mail
  Integer is_zayavka
  Integer is_sms
  Integer is_mbox
  Integer is_article
  Integer is_clients
  Integer is_bank
  Integer is_selection
  Integer is_payorder
  Integer is_account
  Integer is_paytrans
  Integer is_paytask
  Integer is_anons
  Integer is_payreport
  Integer is_trip
  Integer is_hometype
}