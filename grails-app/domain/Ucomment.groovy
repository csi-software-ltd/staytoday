class Ucomment {  
  
  static mapping = {
    version false
  }	  
  
  static constraints = {    
	refcomment_id(nullable:true)
  }
  
  Long id 
  Long home_id
  Long user_id
  Integer typeid
  String comtext
  Integer comstatus
  Date comdate
  Integer rating
  Long refcomment_id
  Integer is_mainpage

///////////////////////////////////////////////////////////////////////////////////////////////////

  static def csiGetClientCommentList(lHomeId) {
	def aUcomm = Ucomment.findAll('FROM Ucomment WHERE home_id=:home_id and comstatus>=0 and typeid=1 and user_id in (:clients)',
			[home_id:lHomeId,clients:(Trip.findAllByHome_idAndModstatusGreaterThanEquals(lHomeId,0).collect{it.user_id})?:[0 as long]])
	return aUcomm
  }

  static def csiGetNotClientCommentList(lHomeId) {
	def aUcomm = Ucomment.findAll('FROM Ucomment WHERE home_id=:home_id and comstatus>=0 and typeid=1 and user_id not in (:clients)',
			[home_id:lHomeId,clients:(Trip.findAllByHome_idAndModstatusGreaterThanEquals(lHomeId,0).collect{it.user_id})?:[0 as long]])
	return aUcomm
  }

}