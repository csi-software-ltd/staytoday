class Paytrans {

  static constraints = {
  }
  static mapping = {
    //datasource 'admin'
    version false
  }
  Long id
  String agrcode = ''
  String agrdate = ''
  Long payorder_id
  Long account_id
  Integer paytype_id
  Integer summa = 0
  Integer summa_val = 0
  Integer valuta_id = 857
  Date moddate = new Date()
  Integer modstatus = 0
  String returncode = ''
  String comment = ''

  def beforeUpdate() {
    moddate = new Date()
  }

}