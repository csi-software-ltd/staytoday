class Semantics_stat {

  static constraints = {
  }
  static mapping = {
    version false
  }

  Long id
  Integer semantics_id
  Integer sengine
  Date sdate = new Date()
  Integer fromcity
  Integer position = 501
  String path = ''
  String title = ''
  String text = ''
  Integer total = 0
  Integer pos_diff = 0

  ///////////////////////////////////////////////////////////////////////////////////////////////

  Semantics_stat setData(jsonData){
    if (jsonData) {
      position = ((jsonData?.position?:'')!='')?jsonData.position.toInteger():501
      path = jsonData?.path?:''
      title = jsonData?.title?:''
      text = jsonData?.text?:''
    }
    def temp = this
    pos_diff = position - (Semantics_stat.findAll(order:'desc',sort:'sdate',max:1) {semantics_id == temp.semantics_id && sengine == temp.sengine && fromcity == temp.fromcity}[0]?.position?:501)
    this
  }

}