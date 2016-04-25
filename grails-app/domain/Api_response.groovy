class Api_response {
  static mapping = { version false }

  Long id
  String outer_id
  Long home_id
  Integer api_id
  Integer type
  Date date_start
  Date date_end
  Long price
  Integer homeperson_id
  String mtext
  Date moddate = new Date()
  Integer modstatus = 0

  def beforeUpdate() {
    moddate = new Date()
  }

  Api_response setDetailData(_data){
    date_start = _data.date_start
    date_end = _data.date_end
    price = _data.price
    homeperson_id = _data.homeperson_id
    mtext = _data.mtext
    this
  }
}