class Zayavka {

  static constraints = {
  }
  static mapping = {
    version false
  }

  Long id
  Long user_id
  Integer country_id
  Integer region_id
  String city
  Date date_start
  Date date_end
  Date inputdate = new Date()
  Integer homeperson_id
  Long pricefrom
  Long pricefrom_rub
  Long priceto
  Long priceto_rub
  Integer valuta_id
  Integer hometype_id
  String ztext
  String mobtel
  Integer timetodecide_id
  Integer modstatus
  Integer is_auto = 0
  Long baseclient_id = 0l
  Date lastUpdated

  String toString() {"${this.ztext}" }

////////////////////////constructor//////////////////////////////////////////////////////
  Zayavka(){}
  Zayavka(oRequest,lUid,DATE_FORMAT,bAuto=0){
    user_id = lUid
    country_id = oRequest.country_id
    region_id = oRequest.region_id?:0
    city = oRequest.city
    date_start = Date.parse(DATE_FORMAT, oRequest.date_start)
    date_end = Date.parse(DATE_FORMAT, oRequest.date_end)
    homeperson_id = oRequest.homeperson_id
    pricefrom = oRequest.pricefrom?:0
    priceto = oRequest.priceto?:0
    valuta_id = oRequest.valuta_id
    hometype_id = oRequest.hometype_id?:0
    ztext = oRequest.ztext
    mobtel = oRequest.mobtel?:'+'+oRequest.ind.replace(' ','').replace('-','')+'('+oRequest.kod.replace(' ','').replace('-','')+')'+oRequest.telef.replace(' ','').replace('-','')
    timetodecide_id = oRequest.timetodecide_id
    modstatus = bAuto?0:(User.get(lUid).modstatus==1)?0:-1
    is_auto = bAuto?:0
    baseclient_id = oRequest.baseclient_id?:baseclient_id?:0l
    setPrice_rub()
  }
//////////////////////////////////////////////////////////////////////////////////////////

  void setPrice_rub() {
    if(this.valuta_id==857){
      this.pricefrom_rub=this.pricefrom?:0
      this.priceto_rub=this.priceto?:0
    } else {
      def oValutarate = new Valutarate()
      def valutaRates = oValutarate.csiGetRate(this.valuta_id)
      this.pricefrom_rub = Math.rint((this.pricefrom?:0) * valutaRates)
      this.priceto_rub = Math.rint((this.priceto?:0) * valutaRates)
    }
  }

}