class Admin {
  
  def sessionFactory
  def searchService
  
  static constraints = {
  }
  
  static mapping = {
    version false
  }
  
  Long id
  String login
  String password
  String name
  String email
  Integer admingroup_id
  Integer accesslevel

  def changePass(lId,sPass){
    def session = sessionFactory.getCurrentSession()
    def sSql = "UPDATE admin SET password=:pass WHERE id=:id"
    def qSql = session.createSQLQuery(sSql)
    qSql.setLong('id',lId)
    qSql.setString('pass',sPass)
    qSql.executeUpdate()
    session.clear()
  }
  ///////////////////////////////////////////////////////////////////
  def csiGetRegionIds(lAdminId){
    def hsSql = [select :"region_id",
                 from   :"admin2reg",
                 where  :"admin_id=:id"]
    def hsLong = [id:lAdminId]
    
    def lsRegionIds=searchService.fetchData(hsSql,hsLong,null,null,null)
    
    if(lsRegionIds){
      def lsRegTmp=[]
      for(oReg in lsRegionIds){
        lsRegTmp<<oReg.toInteger()
      }
      lsRegionIds=lsRegTmp
    }
    return lsRegionIds
  }
}
