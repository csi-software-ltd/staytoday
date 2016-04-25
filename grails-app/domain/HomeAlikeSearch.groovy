import grails.util.Holders
class HomeAlikeSearch {
  def searchService

  static mapping = {
    table 'DUMMY_NAME'
    version false
    cache false
  }

  Long id
  Long client_id
  String name
  String city
  Long city_id
  Long pricestandard_rub
  Long priceweekend_rub
  Long priceweek_rub
  Long pricemonth_rub
  Long x
  Long y
  String mainpicture
  Integer pricestatus
  Integer homeclass_id
  Integer country_id
  Integer is_index
  String address
  String linkname
  
  Long price
  Double distance

  def csiFindHomes(x2,y2,lId,lPrice){	
    def hsSql = [select :"""*,distance(x,y,:x2,:y2) as distance,
                         ifnull((select ifnull(price_rub,0) from homeprop where home_id=home.ID and modstatus=1 and term>0 
                         and date_end>=current_date AND (price_rub>=:pricem AND price_rub<=:priceb) order by date_start asc limit 1),0)*IF((select resstatus from client where id=home.client_id),1.0,1) as price""",
                 from   :'home ',
                 where  :"""((x>=:xm AND x<=:xb) AND (y>=:ym AND y<=:yb)) AND modstatus=1 AND client_id!=:id 
						 AND (0 != (ifnull((select ifnull(price_rub,0) from homeprop where home_id=home.ID and modstatus=1 and term>0 
						 and date_end>=current_date AND (price_rub>=:pricem AND price_rub<=:priceb) order by date_start asc limit 1),0)))""",
                 order  :'distance(x,y,:x2,:y2)']

	def coordRange = Tools.getIntVal(Holders.config.home.view.alike.coordinates.range,20000)
	def priceRange = Tools.getIntVal(Holders.config.home.view.alike.price.range,0.25)
	def hsLong = [:]
	hsLong['x2']=x2
    hsLong['y2']=y2
	hsLong['xm']=x2-coordRange
	hsLong['xb']=x2+coordRange
	hsLong['ym']=y2-coordRange
	hsLong['yb']=y2+coordRange
	hsLong['pricem']=lPrice-(priceRange*lPrice as long)
	hsLong['priceb']=lPrice+(priceRange*lPrice as long)
	hsLong['id']=lId

    return searchService.fetchData(hsSql,hsLong,null,null,null,HomeAlikeSearch.class,Tools.getIntVal(Holders.config.home.view.alike.homes_max,3))
  }
  def csiFindAnotherHomes(x2,y2,lId,lClient_id){	
    def hsSql = [select :"""*,distance(x,y,:x2,:y2) as distance,
                         ifnull((select ifnull(price_rub,0) from homeprop where home_id=home.ID and modstatus=1 and term>0 
                         and date_end>=current_date order by date_start asc limit 1),0)*IF((select resstatus from client where id=home.client_id),1.0,1) as price""",
                 from   :'home ',
                 where  :'client_id=:client_id AND modstatus=1 AND id!=:id',
                 order  :'distance(x,y,:x2,:y2)']
	def hsLong = [:]
	hsLong['x2']=x2
    hsLong['y2']=y2
	hsLong['id']=lId
	hsLong['client_id']=lClient_id

    return searchService.fetchData(hsSql,hsLong,null,null,null,HomeAlikeSearch.class,Tools.getIntVal(Holders.config.home.view.client_another_homes_max,3))
  }

  def csiGetHomeWithPrice(lId){	
    def hsSql = [select :"""*,0 as distance,
                         ifnull((select ifnull(price_rub,0) from homeprop where home_id=home.ID and modstatus=1 and term>0 
                         and date_end>=current_date order by date_start asc limit 1),0)*IF((select resstatus from client where id=home.client_id),1.0,1) as price""",
                 from   :'home ',
                 where  :'id=:id',
                 order  :'rand()']
	def hsLong = [:]
	hsLong['id']=lId

    return searchService.fetchData(hsSql,hsLong,null,null,null,HomeAlikeSearch.class)
  }
}