class Mailing_queue {    
  def sessionFactory

  static constraints = {	
  }
  static mapping = {
    version false
  }
  Integer id
  Integer template_id
  String contact
  String name
  Integer modstatus
//////////////////////////////////////////////////////////////////////////////////////////////////
  void moveToArchive(lId,sModstatus,lTask_id) {

	def sSql="""INSERT INTO mailing_history(`template_id`, `contact`, `name`,  `modstatus`, `task_id`)
	  SELECT `template_id`, `contact`, `name`,  :modstatus, :task_id FROM mailing_queue where id=:lId
	"""
    def session = sessionFactory.getCurrentSession()
    def qSql = session.createSQLQuery(sSql)

    try{
      qSql.setLong('lId',lId)
	  qSql.setLong('modstatus',sModstatus)
	  qSql.setInteger('task_id',lTask_id)
	  qSql.executeUpdate()
    }catch(Exception e){
      log.debug('Error on copying mailing_queue to mailing_history')
      log.debug(e.toString())
    }
    session.clear()
  }

}
