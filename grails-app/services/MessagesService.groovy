import org.codehaus.groovy.grails.commons.ConfigurationHolder
class MessagesService {

  static transactional = false
	
  def processMessages(){
		def aNotetype = Notetype.list()
		def aHome = Home.findAll('FROM Home WHERE modstatus>0')
		def bContinue = true
		def iTestparam
		def oUser
		def aNote = Note.list()
		for (note in aNote)
	  	note.delete(flush:true)
		for (home in aHome){
	  	bContinue = true
	  	oUser = User.findByClient_id(home.client_id)
	  	if (!oUser) continue
	  	for (oNotetype in aNotetype){
				if (oNotetype.id == 1){
		  		iTestparam = oUser.modstatus
		  		if (iTestparam == oNotetype.param) {
						if (!Note.findByUser_idAndNotetype_id(oUser.id,oNotetype.id)){
			  			//добавление записей в таблицы сообщений
			  			createNote(home.id,oUser,oNotetype,1)
			  			if (isMailneed(oNotetype,home.id,oUser))
								createNote(home.id,oUser,oNotetype,0)
						}
						bContinue = false
		  		}
				} else if (oNotetype.id == 2 && bContinue) {
		  		iTestparam = home.modstatus
		  		if (iTestparam != oNotetype.param) {
						//добавление записей в таблицы сообщений
						createNote(home.id,oUser,oNotetype,1)
						if (isMailneed(oNotetype,home.id,oUser))
			  			createNote(home.id,oUser,oNotetype,0)
					bContinue = false
		  		}
				} else if (oNotetype.id == 3 && bContinue) {
		  		iTestparam = Homephoto.findAllByHome_id(home.id).size()
		  		if (iTestparam < oNotetype.param) {
						//добавление записей в таблицы сообщений
						createNote(home.id,oUser,oNotetype,1)
						if (isMailneed(oNotetype,home.id,oUser))
			  			createNote(home.id,oUser,oNotetype,0)
						bContinue = false
		  		}
				} else if (oNotetype.id == 4 && bContinue) {
		  		iTestparam = !(Homeguidebook.findAllByHome_id(home.id).size() || ((home.infraoption?:'')!=''))
		  		if (iTestparam) {
						//добавление записей в таблицы сообщений
						createNote(home.id,oUser,oNotetype,1)
						if (isMailneed(oNotetype,home.id,oUser))
			  			createNote(home.id,oUser,oNotetype,0)
						bContinue = false
		  		}
				} else if (oNotetype.id == 5 && bContinue) {
		  		iTestparam = Homevideo.findAllByHome_id(home.id).size()
		  		if (iTestparam < oNotetype.param) {
						//добавление записей в таблицы сообщений
						createNote(home.id,oUser,oNotetype,1)
						if (isMailneed(oNotetype,home.id,oUser))
			  			createNote(home.id,oUser,oNotetype,0)
						bContinue = false
		  		}
				} else if (oNotetype.id == 7) {
		  		iTestparam = (oUser.description&&oUser.picture)?1:0
		  		if (iTestparam == oNotetype.param) {
						if (!Note.findByUser_idAndNotetype_id(oUser.id,oNotetype.id)){
			  			//добавление записей в таблицы сообщений
			  			createNote(home.id,oUser,oNotetype,1)
			  			if (isMailneed(oNotetype,home.id,oUser))
								createNote(home.id,oUser,oNotetype,0)
						}
		  		}
				}
	  	}
		}
		def oNotetype = Notetype.get(6)
		for(trip in Trip.findAllByModstatusAndTodateGreaterThan(3,new Date()-14)) {
			iTestparam = Ucomment.find('from Ucomment where user_id=:uId and home_id=:hId and typeid=1 and comstatus>=0 and comdate>:date',[uId:trip.user_id,hId:trip.home_id,date:trip.fromdate])
			if (!iTestparam) {
				oUser = User.get(trip.user_id)
				//добавление записей в таблицы сообщений
				createNote(trip.home_id,oUser,oNotetype,1)
				if (oUser?.email
						&& !Notebyemail.find('from Notebyemail where user_id=:uId and home_id=:hId and notetype_id=6 and inputdate>:date',[uId:trip.user_id,hId:trip.home_id,date:trip.todate])
						&& trip.todate<new Date()-5)
					createNote(trip.home_id,oUser,oNotetype,0)
			}
		}
		oNotetype = Notetype.get(1)
		for(user in User.findAllByClient_id(0)){
			iTestparam = user.modstatus
			if (iTestparam == oNotetype.param) {
				if (!Note.findByUser_idAndNotetype_id(user.id,oNotetype.id)){
					//добавление записей в таблицы сообщений
					createNote(0,user,oNotetype,1)
				}
			}
		}
  }

  def isMailneed(oNotetype,home_id,oUser) {
		def aNotes = Notebyemail.findAll('FROM Notebyemail WHERE home_id=:home_id AND notetype_id=:notetype_id ORDER BY inputdate DESC',[home_id:home_id,notetype_id:oNotetype.id])
		def duration
		def today = new GregorianCalendar()
		today.setTime(new Date())
		today.set(Calendar.HOUR_OF_DAY ,0)
		today.set(Calendar.MINUTE ,0)
		today.set(Calendar.SECOND,0)
		today.set(Calendar.MILLISECOND,0)
		if (aNotes.size()>0) {
	  	use(groovy.time.TimeCategory) {
				duration = today.getTime() - aNotes[0].inputdate
	  	}
	  	if (duration.days<=oNotetype.dayinterval 
		  		|| aNotes.size()>=oNotetype.max_notes)
				return false
	  	else if (Notegroup.get(oNotetype.notegroup_id).db_name)
				if (!oUser.(Notegroup.get(oNotetype.notegroup_id).db_name))
		  		return false
		}
		return true
  }

  def createNote(iHomeId,oUser,oNotetype,iType){
		def oNote
		if (iType) {
	  	oNote = new Note(iHomeId, oUser.id, oNotetype)
	  	try{
				oNote.save(flush:true,failOnError:true)
	  	} catch (Exception e){
				log.debug("Error on save Note\n"+e.toString())
	  	}
		} else {
	  	oNote = new Notebyemail(iHomeId, oUser, oNotetype)
	  	try{
				oNote.save(flush:true)
	  	} catch (Exception e){
				log.debug("Error on save Notebyemail\n"+e.toString())
	  	}
		}
  }

}
