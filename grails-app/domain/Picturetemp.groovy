class Picturetemp { //TODO: Rename into Temppicture
  def sessionFactory
  def searchService
  
  static constraints = {
  }

  String fullname
  String filename
  String toString() {"${this.filename}" }
  
  def csiGetOldFiles(iTimelive) { 
    def hsInt=[t:iTimelive]
    def hsSql=[select:'*',
               from:'picturetemp',
               where:'(UNIX_TIMESTAMP(now())-UNIX_TIMESTAMP(picdate))>:t'
              ]
    return searchService.fetchData(hsSql,null,hsInt,null,null,Picturetemp.class)
  }
  def csiGetFilesForDelete(iTimelive) {
    def hsInt=[t:iTimelive]
    def hsSql=[select:'*',
               from:"""picturetemp 
                        left join homephoto ON (picturetemp.filename=homephoto.picture or picturetemp.filename=CONCAT("t_",homephoto.picture))
                        left join user ON (picturetemp.filename=user.picture or picturetemp.filename=CONCAT("t_",user.picture))
                        left join articles ON (picturetemp.filename=articles.picture or picturetemp.filename=CONCAT("t_",articles.picture))
                        left join popdirection ON (picturetemp.filename=popdirection.picture or picturetemp.filename=CONCAT("t_",popdirection.picture))""",
               where:'(UNIX_TIMESTAMP(now())-UNIX_TIMESTAMP(picdate))>:t and 0=ifnull(home_id,0) and 0=ifnull(user.id,0)'
              ]
    return searchService.fetchData(hsSql,null,hsInt,null,null,Picturetemp.class)
  }
  //////////////////////////////////////////////////////////////////////////////////
  def csiDeleteByIds(lsIds) { 
    if((lsIds?:[]).size()==0)    return
    def session = sessionFactory.getCurrentSession()
    def sSql="DELETE FROM picturetemp WHERE id in (:ids)"
    try{
      def qSql=session.createSQLQuery(sSql)
      qSql.setParameterList("ids",lsIds)
      qSql.executeUpdate()
    }catch (Exception e) {
      log.debug("Error Picturetemp:csiDeleteById\n"+e.toString());
    }
    session.clear()  
  }  
  ///////////////////////////////////////////////////////////////////////////////////
  def csiDeleteByFilenames(lsIds) { 
    if((lsIds?:[]).size()==0)    return
    def session = sessionFactory.getCurrentSession()
    def sSql="DELETE FROM picturetemp WHERE filename in (:ids)"
    try{
      def qSql=session.createSQLQuery(sSql)
      qSql.setParameterList("ids",lsIds)
      qSql.executeUpdate()
    }catch (Exception e) {
      log.debug("Error Picturetemp:csiDeleteByFilename\n"+e.toString());
    }
    session.clear()  
  }  
  
}
