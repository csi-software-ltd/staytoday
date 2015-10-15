class City {
  static constraints = {
    tel_code(nullable:true)
    description(nullable:true)
    seo_title(nullable:true)
    seo_keywords(nullable:true)
    seo_description(nullable:true)
    rooms_description(nullable:true)
    houses_description(nullable:true)
    beds_description(nullable:true)
    moddate(nullable:true)
    name_en(nullable:false)
    name_en(blank:false)
  }
  static mapping = {
    version false
  }
  Integer id
  Integer district_id = 0
  Integer region_id
  Integer country_id
  String name
  String name_en
  String name2 = ''
  String tagname = ''
  String tel_code
  Integer is_metro=0
  Integer is_index=0
  Integer homecount=0
  Integer x=0
  Integer y=0
  String description=''
  String flats_description = ''  
  String rooms_description = ''
  String cottages_description = ''
  String houses_description = ''
  String beds_description = ''  
  String seo_title = ''
  String seo_keywords = ''
  String seo_description = ''
  String picture = ''
  String domain = ''
  String yacode = ''
  Integer is_specoffer = 0
  Integer is_panoram = 0
  Date moddate = new Date()

  String toString() { "${this.name}" }

  def beforeUpdate() {
    moddate = new Date()
  }

}