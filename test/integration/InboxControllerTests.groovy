import grails.test.*
import javax.servlet.http.Cookie
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.converters.JSON
class InboxControllerTests extends GroovyTestCase {

	def requestServiceProxy

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {

    }

	void testZayavkaresponseAction() {//Спецпредложение по заявке.
									  //Результаты: Записи в таблицах Zayvka2client, Mbox, Mboxrec со корректными ссылками на заявку.
									  //Отсутствие ошибок. Изменение статуса у записи таблицы Zayvka2client.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNull Mboxrec.findByHome_id(767)

		def oZayvka2client = new Zayvka2client(99,712,new Date(),1)
		oZayvka2client.save(flush:true)

		ic.params.id = 99
		ic.params.z2C_id = oZayvka2client.id
		ic.params.comments = 'some'
		ic.params.price = '3000'
		ic.params.homeperson_id = 2
		ic.params.home_id = 767
		ic.params.date_start_year = "2014"
		ic.params.date_start_month = "02"
		ic.params.date_start_day = "15"
		ic.params.date_end_year = "2014"
		ic.params.date_end_month = "03"
		ic.params.date_end_day = "02"

		Home.metaClass.calculateHomePrice = {def hsR, def lId -> return [error:false,result:1] }

		ic.zayavkaresponse()

		assertFalse JSON.parse(ic.response.contentAsString).error == true
		assertNotNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNotNull Mboxrec.findByHome_id(767)
		oZayvka2client = Zayvka2client.get(oZayvka2client.id)
		assert oZayvka2client.modstatus == 1

		oZayvka2client.delete(flush:true)
		def temp = Mbox.findByHome_idAndZayvka_id(767,99)
		temp?.delete(flush:true)
		def aTemp = Mboxrec.findAllByHome_id(767)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testZayavkaresponseActionNullComments() {//Спецпредложение по заявке. Пустое тело сообщения.
												  //Результаты: Ошибка 'notext'.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNull Mboxrec.findByHome_id(767)

		def oZayvka2client = new Zayvka2client(99,712,new Date(),1)
		oZayvka2client.save(flush:true)

		ic.params.id = 99
		ic.params.z2C_id = oZayvka2client.id
		ic.params.price = '3000'
		ic.params.homeperson_id = 2
		ic.params.home_id = 767
		ic.params.date_start_year = "2014"
		ic.params.date_start_month = "02"
		ic.params.date_start_day = "15"
		ic.params.date_end_year = "2014"
		ic.params.date_end_month = "03"
		ic.params.date_end_day = "02"

		Home.metaClass.calculateHomePrice = {def hsR, def lId -> return [error:false,result:1] }

		ic.zayavkaresponse()

		assert JSON.parse(ic.response.contentAsString).error == true
		assert JSON.parse(ic.response.contentAsString).message == "Вы не заполнили текст сообщения."
		assertNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNull Mboxrec.findByHome_id(767)

		oZayvka2client = Zayvka2client.get(oZayvka2client.id)
		oZayvka2client.delete(flush:true)
	}

	void testZayavkaresponseActionNullPrice() {//Спецпредложение по заявке. Не указана цена.
											   //Результаты: Ошибка 'noprice'.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNull Mboxrec.findByHome_id(767)

		def oZayvka2client = new Zayvka2client(99,712,new Date(),1)
		oZayvka2client.save(flush:true)

		ic.params.id = 99
		ic.params.z2C_id = oZayvka2client.id
		ic.params.comments = 'some'
		ic.params.homeperson_id = 2
		ic.params.home_id = 767
		ic.params.date_start_year = "2014"
		ic.params.date_start_month = "02"
		ic.params.date_start_day = "15"
		ic.params.date_end_year = "2014"
		ic.params.date_end_month = "03"
		ic.params.date_end_day = "02"

		Home.metaClass.calculateHomePrice = {def hsR, def lId -> return [error:false,result:1] }

		ic.zayavkaresponse()

		assert JSON.parse(ic.response.contentAsString).error == true
		assert JSON.parse(ic.response.contentAsString).message == "Вы не указали стоимость предложения."
		assertNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNull Mboxrec.findByHome_id(767)

		oZayvka2client = Zayvka2client.get(oZayvka2client.id)
		oZayvka2client.delete(flush:true)
	}

	void testZayavkaresponseActionBadPrice() {//Спецпредложение по заявке. Некорректная цена.
											  //Результаты: Ошибка 'badprice'.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNull Mboxrec.findByHome_id(767)

		def oZayvka2client = new Zayvka2client(99,712,new Date(),1)
		oZayvka2client.save(flush:true)

		ic.params.id = 99
		ic.params.z2C_id = oZayvka2client.id
		ic.params.comments = 'some'
		ic.params.price = '3000sdfsdffsd'
		ic.params.homeperson_id = 2
		ic.params.home_id = 767
		ic.params.date_start_year = "2014"
		ic.params.date_start_month = "02"
		ic.params.date_start_day = "15"
		ic.params.date_end_year = "2014"
		ic.params.date_end_month = "03"
		ic.params.date_end_day = "02"

		Home.metaClass.calculateHomePrice = {def hsR, def lId -> return [error:false,result:1] }

		ic.zayavkaresponse()

		assert JSON.parse(ic.response.contentAsString).error == true
		assert JSON.parse(ic.response.contentAsString).message == "Некорректный формат цены."
		assertNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNull Mboxrec.findByHome_id(767)

		oZayvka2client = Zayvka2client.get(oZayvka2client.id)
		oZayvka2client.delete(flush:true)
	}

/*	void testZayavkaresponseActionBadSpecoffer() {//Спецпредложение по заявке. Некорректное спецпредложение.
												  //Результаты: Ошибка.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNull Mboxrec.findByHome_id(767)

		def oZayvka2client = new Zayvka2client(99,712,new Date(),1)
		oZayvka2client.save(flush:true)

		ic.params.id = 99
		ic.params.z2C_id = oZayvka2client.id
		ic.params.comments = 'some'
		ic.params.price = '3000'
		ic.params.homeperson_id = 2
		ic.params.home_id = 767
		ic.params.date_start_year = "2014"
		ic.params.date_start_month = "02"
		ic.params.date_start_day = "15"
		ic.params.date_end_year = "2014"
		ic.params.date_end_month = "03"
		ic.params.date_end_day = "02"

		Home.metaClass = null
		Home.metaClass.calculateHomePrice = {def hsR, def lId -> return [error:true,result:0] }

		ic.zayavkaresponse()

		assert JSON.parse(ic.response.contentAsString).error == true
		assert JSON.parse(ic.response.contentAsString).message == ""
		assertNull Mbox.findByHome_idAndZayvka_id(767,99)
		assertNull Mboxrec.findByHome_id(767)

		oZayvka2client = Zayvka2client.get(oZayvka2client.id)
		oZayvka2client.delete(flush:true)
	}*/

	void testCancelMboxBronActionClientBranch() {//Отмена брони со стороны клиента.
									 //Результаты: корректный answertype_id у соответствующего Mbox. Добавление записи в таблицу Mboxrec.
									 //Отсутствие ошибок. Изменение статуса у записи таблицы Trip.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=hsDbUser.id
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()
		oMbox.date_end=new Date()+1
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=0
		oMbox.modstatus=1
		oMbox.is_answer=0
		oMbox.is_read=0
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		def oTrip = new Trip(oMbox)
		oTrip.save(flush:true)

		ic.params.id = oTrip.id
		ic.params.comments = 'some'

		ic.cancelMboxBron()

		assertNull ic.flash.error
		assertNotNull Mboxrec.findByHome_id(767)
		oMbox = Mbox.get(oMbox.id)
		oTrip = Trip.get(oTrip.id)
		assert oMbox.modstatus == 5
		assert oMbox.answertype_id == 8
		assert oTrip.modstatus == -1

		oTrip.delete(flush:true)
		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByHome_id(767)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testCancelMboxBronActionOwnerBranch() {//Отмена брони со стороны владельца.
									 //Результаты: корректный answertype_id у соответствующего Mbox. Добавление записи в таблицу Mboxrec.
									 //Отсутствие ошибок. Изменение статуса у записи таблицы Trip.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=hsDbUser.id
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()
		oMbox.date_end=new Date()+1
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=0
		oMbox.modstatus=1
		oMbox.is_answer=0
		oMbox.is_read=0
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		def oTrip = new Trip(oMbox)
		oTrip.save(flush:true)

		ic.params.id = oTrip.id
		ic.params.comments = 'some'
		ic.params.type = '1'

		ic.cancelMboxBron()

		assertNull ic.flash.error
		assertNotNull Mboxrec.findByHome_id(767)
		oMbox = Mbox.get(oMbox.id)
		oTrip = Trip.get(oTrip.id)
		assert oMbox.modstatus == 5
		assert oMbox.answertype_id == 9
		assert oTrip.modstatus == -1

		oTrip.delete(flush:true)
		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByHome_id(767)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testCancelMboxBronActionNullComments() {//Отмена брони со стороны клиента. Не указана причина отмены.
												 //Ошибка №1.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=hsDbUser.id
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()
		oMbox.date_end=new Date()+1
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=0
		oMbox.modstatus=1
		oMbox.is_answer=0
		oMbox.is_read=0
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		def oTrip = new Trip(oMbox)
		oTrip.save(flush:true)

		ic.params.id = oTrip.id

		ic.cancelMboxBron()

		assert ic.flash.error==1
		assertNull Mboxrec.findByHome_id(767)
		oMbox = Mbox.get(oMbox.id)
		oTrip = Trip.get(oTrip.id)
		assert oMbox.modstatus == 1
		assert oMbox.answertype_id == 0
		assert oTrip.modstatus == 0

		oTrip.delete(flush:true)
		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByHome_id(767)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testSetMboxBronAction() {//Подтверждение предварительной брони клиентом.
								  //Результаты: корректный answertype_id у соответствующего Mbox. Добавление записи в таблицу Mboxrec.
								  //Отсутствие ошибок. Запись в таблице Trip.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=hsDbUser.id
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()
		oMbox.date_end=new Date()+1
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=0
		oMbox.modstatus=1
		oMbox.is_answer=0
		oMbox.is_read=0
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id

		ic.setMboxBron()

		assertNull ic.flash.error
		assertNotNull Mboxrec.findByHome_id(767)
		assertNotNull Trip.findByHome_id(767)
		oMbox = Mbox.get(oMbox.id)
		def oTrip = Trip.findByHome_id(767)
		assert oMbox.modstatus == 4
		assert oMbox.answertype_id == 7
		assert oTrip.modstatus == 0

		oTrip.delete(flush:true)
		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByHome_id(767)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testSetMboxBronActionBadUser() {//Подтверждение чужой предварительной брони.
										 //Результаты: ошибка.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()
		oMbox.date_end=new Date()+1
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=0
		oMbox.modstatus=1
		oMbox.is_answer=0
		oMbox.is_read=0
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id

		ic.setMboxBron()

		assertNull ic.flash.error
		assertNull Mboxrec.findByHome_id(767)
		assertNull Trip.findByHome_id(767)
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.modstatus == 1
		assert oMbox.answertype_id == 0
		assert JSON.parse(ic.response.contentAsString).error == true
		

		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByHome_id(767)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testAddanswerActionAnswertype1() {//"Вперед бронируйте".
										   //Результаты: корректный answertype_id у соответствующего Mbox. Добавление записи в таблицу Mboxrec.
										   //Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=0
		oMbox.modstatus=1
		oMbox.is_answer=0
		oMbox.is_read=1
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.answertype_id = 1
		ic.params.message = 'bla bla'

		ic.addanswer()

		assertNull ic.flash.error
		assertNotNull Mboxrec.findByHome_id(767)
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.modstatus == 3
		assert oMbox.answertype_id == 1
		assert oMbox.is_answer == 1

		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByHome_id(767)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testAddanswerActionAnswertype2() {//"Спецпредложение".
										   //Результаты: корректный answertype_id у соответствующего Mbox. Добавление записи в таблицу Mboxrec.
										   //Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=0
		oMbox.modstatus=1
		oMbox.is_answer=0
		oMbox.is_read=1
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.answertype_id = 2
		ic.params.homeperson_id = 3
		ic.params.message = 'bla bla'
		ic.params.price = '4000'
		ic.params.home_id_spec = '768'
		ic.params.date_start_year = "2015"
		ic.params.date_start_month = "02"
		ic.params.date_start_day = "15"
		ic.params.date_end_year = "2015"
		ic.params.date_end_month = "03"
		ic.params.date_end_day = "02"

		ic.addanswer()

		assertNull ic.flash.error
		assertNotNull Mboxrec.findByMbox_id(oMbox.id)
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.modstatus == 3
		assert oMbox.answertype_id == 2
		assert oMbox.is_answer == 1
		assert oMbox.home_id == 768
		assert oMbox.price == 4000
		assert oMbox.homeperson_id == 3

		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByMbox_id(oMbox.id)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testAddanswerActionAnswertype4() {//"Желаемое количество дней недоступно".
										   //Результаты: корректный answertype_id у соответствующего Mbox. Добавление записи в таблицу Mboxrec.
										   //Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=0
		oMbox.modstatus=1
		oMbox.is_answer=0
		oMbox.is_read=1
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.answertype_id = 4
		ic.params.message = 'bla bla'
		ic.params.rule_minday_id = '5'
		ic.params.rule_maxday_id = '7'

		ic.addanswer()

		assertNull ic.flash.error
		assertNotNull Mboxrec.findByMbox_id(oMbox.id)
		oMbox = Mbox.get(oMbox.id)
		def oMboxrec = Mboxrec.findByMbox_id(oMbox.id)
		assert oMbox.modstatus == 1
		assert oMbox.answertype_id == 4
		assert oMbox.is_answer == 1
		assert oMboxrec.rule_minday_id == 5
		assert oMboxrec.rule_maxday_id == 7

		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByMbox_id(oMbox.id)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testAddanswerActionAnswertype0() {//Запрос со стороны клиента.
										   //Результаты: корректный answertype_id у соответствующего Mbox. Добавление записи в таблицу Mboxrec.
										   //Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=6
		oMbox.modstatus=1
		oMbox.is_answer=1
		oMbox.is_read=1
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.message = 'bla bla'

		ic.addanswer()

		assertNull ic.flash.error
		assertNotNull Mboxrec.findByMbox_id(oMbox.id)
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.modstatus == 1
		assert oMbox.answertype_id == 6
		assert oMbox.is_answer == 0

		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByMbox_id(oMbox.id)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testAddanswerActionBadUser() {//Некорректный юзер.
									   //Результаты: ошибка №1.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=6
		oMbox.modstatus=1
		oMbox.is_answer=1
		oMbox.is_read=1
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.message = 'bla bla'

		ic.addanswer()

		assert ic.flash.error==1
		assertNull Mboxrec.findByMbox_id(oMbox.id)

		oMbox = Mbox.get(oMbox.id)
		oMbox.delete(flush:true)
		def aTemp = Mboxrec.findAllByMbox_id(oMbox.id)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testHidemboxActionDel() {//Удаление треда переписки.
								  //Результаты: корректный modstatus у соответствующего Mbox.
								  //Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=6
		oMbox.modstatus=1
		oMbox.is_answer=1
		oMbox.is_read=1
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.'amp;act' = '1'

		ic.hidembox()

		assertFalse JSON.parse(ic.response.contentAsString).error == true
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.modstatus == 0

		oMbox.delete(flush:true)
	}

	void testHidemboxActionRest() {//Восстановление треда переписки.
								   //Результаты: корректный modstatus у соответствующего Mbox.
								   //Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=6
		oMbox.modstatus=0
		oMbox.is_answer=1
		oMbox.is_read=1
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.'amp;act' = '0'

		ic.hidembox()

		assertFalse JSON.parse(ic.response.contentAsString).error == true
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.modstatus == 1

		oMbox.delete(flush:true)
	}

	void testSelectmboxActionOwnerAdd() {//Добавление в избранное владельцем.
										 //Результаты: корректные параметры у соответствующего Mbox.
										 //Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=6
		oMbox.modstatus=1
		oMbox.is_answer=1
		oMbox.is_read=1
		oMbox.is_owfav == 0
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.'amp;act' = '1'
		ic.params.'amp;owner' = '1'

		ic.selectmbox()

		assertFalse JSON.parse(ic.response.contentAsString).error == true
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.is_owfav == 1

		oMbox.delete(flush:true)
	}

	void testSelectmboxActionOwnerRemove() {//Удаление из избранного владельцем.
											//Результаты: корректные параметры у соответствующего Mbox.
											//Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=6
		oMbox.modstatus=1
		oMbox.is_answer=1
		oMbox.is_read=1
		oMbox.is_owfav == 1
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.'amp;act' = '0'
		ic.params.'amp;owner' = '1'

		ic.selectmbox()

		assertFalse JSON.parse(ic.response.contentAsString).error == true
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.is_owfav == 0

		oMbox.delete(flush:true)
	}

	void testSelectmboxActionClientAdd() {//Добавление в избранное клиентом.
										  //Результаты: корректные параметры у соответствующего Mbox.
										  //Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=6
		oMbox.modstatus=1
		oMbox.is_answer=1
		oMbox.is_read=1
		oMbox.is_clfav == 0
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.'amp;act' = '1'
		ic.params.'amp;owner' = '0'

		ic.selectmbox()

		assertFalse JSON.parse(ic.response.contentAsString).error == true
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.is_clfav == 1

		oMbox.delete(flush:true)
	}

	void testSelectmboxActionClientRemove() {//Удаление из избранного клиентом.
											 //Результаты: корректные параметры у соответствующего Mbox.
											 //Отсутствие ошибок.
		def ic = new InboxController()
		ic.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		ic.response.addCookie(oCookie)
		ic.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		def oMbox = new Mbox()
		oMbox.price_rub=10500
		oMbox.price=100
		oMbox.valuta_id=857
		oMbox.user_id=37
		oMbox.moddate=new Date()
		oMbox.date_start=new Date()+1
		oMbox.date_end=new Date()+2
		oMbox.homeperson_id=2
		oMbox.mtext='some'
		oMbox.is_telperm=0
		oMbox.timezone=16
		oMbox.home_id=767
		oMbox.answertype_id=6
		oMbox.modstatus=1
		oMbox.is_answer=1
		oMbox.is_read=1
		oMbox.is_clfav == 1
		oMbox.inputdate=new Date()
		oMbox.save(flush:true)
		
		ic.params.id = oMbox.id
		ic.params.'amp;act' = '0'
		ic.params.'amp;owner' = '0'

		ic.selectmbox()

		assertFalse JSON.parse(ic.response.contentAsString).error == true
		oMbox = Mbox.get(oMbox.id)
		assert oMbox.is_clfav == 0

		oMbox.delete(flush:true)
	}

}