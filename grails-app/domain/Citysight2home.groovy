class Citysight2home {
  def sessionFactory
  static mapping = {
    version false
  }

  Integer id
  Integer citysight_id = 0 
  Integer home_id=0 
  
  def csiDeleteAllCitysight2home(){
    def sSql="""
      DELETE FROM
        citysight2home       
     """
     def session = sessionFactory.getCurrentSession()    
     try{
       def qSql=session.createSQLQuery(sSql)       
    
       qSql.executeUpdate()
     }catch (Exception e) {
       log.debug("Error Citysight:csiDeleteAllCitysight2home\n"+e.toString());
     }
     session.clear()
  }
}