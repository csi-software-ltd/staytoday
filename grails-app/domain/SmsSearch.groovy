class SmsSearch {
  def searchService
  def sessionFactory
  
  static constraints = {
  }
  
  static mapping = {
    version false
  }
  
  Long id
  Long user_id
  Integer status
  String server_id
  Date inputdate
  String tel
  String smscode

  String user_name
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectSms(lUserId,sStatus,dInputdate,DATE_FORMAT,iMax,iOffset){
    def session = sessionFactory.getCurrentSession()
	def hsSql=[select:'',from:'',where:'',order:''] 
	def hsLong=[:]
	def hsString=[:]
	
    hsSql.select="*, user.nickname AS user_name"
    hsSql.from='sms, user'
	hsSql.where="sms.user_id = user.id"+
				((lUserId>0)?' AND sms.user_id =:user_id':'')+
				((sStatus!='')?' AND sms.status =:status':'')+
				((dInputdate!='')?' AND sms.inputdate >=:inputdate AND sms.inputdate <=:inputdateNext':'')
	hsSql.order="sms.id DESC"
    if(lUserId>0)
      hsLong['user_id']=lUserId
    if(sStatus!='')
      hsString['status']=sStatus
	if(dInputdate!=''){
	  hsString['inputdate']=dInputdate.format(DATE_FORMAT)
	  hsString['inputdateNext']=(dInputdate+1).format(DATE_FORMAT)
	}

	def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'sms.id',true,SmsSearch.class)
  }

}
