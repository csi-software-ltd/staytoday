class Valutarate { 
  def searchService     
  static constraints = {
  }
  
  static mapping = {
    version false
  }
  
  Long id
  String code  
  String name
  Float vrate  
  Integer valuta_id
  Integer dim
  Date vdate
  
  def csiSearchCurrent(iValutaId){
    def hsSql=[select:'',from:'',where:'']
    def hsLong=[:]
    hsSql.select="*"
	hsSql.from="valutarate"    
    hsSql.where="valuta_id=:valuta_id and vdate=(SELECT max(vdate) from valutarate)"
	if(iValutaId>0)
	  hsLong['valuta_id']=iValutaId
    return searchService.fetchData(hsSql,hsLong,null,null,null,Valutarate.class)
  }

  Double csiGetRate(iValutaId){
	def rates = csiSearchCurrent(iValutaId)
	if(!rates)
		return 1
	else {
		return (rates[0].vrate/rates[0].dim)
	}
  }
}
