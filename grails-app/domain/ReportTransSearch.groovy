import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ReportTransSearch {
  def searchService

  static constraints = {
  }
  static mapping = {
    table 'DUMMY_NAME'
    version false
    cache false
  }

  //trip
  Long id
  Long home_id
  Long user_id
  Long payorder_id
  Date fromdate
  Date todate

  //payorder
  String norder
  Integer agr_id
  Integer payway_id
  Integer client_id
  Integer summa
  Integer summa_com = 0
  String plat_name = ''
  Date plat_date

  //agr
  String name
  Integer modstatus
  Double persent

//////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csigetReport(isWM,sDateStart,sDateEnd){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select = '*'
    hsSql.from = 'trip join payorder on trip.payorder_id = payorder.id join agr on payorder.agr_id = agr.id'
    hsSql.where = 'trip.payorder_id > 0 and agr.modstatus = 1 and agr.id > 1'+
      ((isWM)?' and agr.id = 5':' and agr.id != 5')+
      ((sDateStart)?" and payorder.plat_date >=:date_start":"")+
      ((sDateEnd)?" and payorder.plat_date <=:date_end":"")
    hsSql.order="payorder.plat_date ASC"

    if(sDateStart)
      hsString['date_start'] = sDateStart
    if(sDateEnd)
      hsString['date_end'] = sDateEnd

    def hsRes = searchService.fetchData(hsSql,null,null,hsString,null,ReportTransSearch.class)
  }

  def csigetDealReport(sDateStart,sDateEnd){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select = '*, "" as name, 1 as modstatus, 0 as persent'
    hsSql.from = 'trip join payorder on trip.payorder_id = payorder.id'
    hsSql.where = 'trip.payorder_id > 0'+
      ((sDateStart)?" and trip.todate >=:date_start":"")+
      ((sDateEnd)?" and trip.todate <=:date_end":"")
    hsSql.order="trip.todate ASC"

    if(sDateStart)
      hsString['date_start'] = sDateStart
    if(sDateEnd)
      hsString['date_end'] = sDateEnd

    def hsRes = searchService.fetchData(hsSql,null,null,hsString,null,ReportTransSearch.class)
  }

}