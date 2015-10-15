class Homeperson {
  def sessionFactory  
  
  static mapping = {
    version false
  }	  
  
  static constraints = {    
  }
  
  Integer id  
  String name
  String name_en
  Integer kol
  String name2
  
  def getMax(){
    def session = sessionFactory.getCurrentSession()
    def iId=0
    def sSql="select max(kol) FROM homeperson"
	def qSql=session.createSQLQuery(sSql)
	return qSql.list()
  }  
}