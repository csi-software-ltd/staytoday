import grails.test.*
import javax.servlet.http.Cookie
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class TripControllerTests extends GroovyTestCase {

	def requestServiceProxy

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {

    }

	void testRatingAction() {//Выставление рейтинга поездки.
							 //Результаты: Изменение рейтинга. Отсутствие ошибок. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.act = 'next'
		tc.params.id = 8
		tc.params.rating = 3

		tc.rating()
		assertEquals 3, Trip.get(tc.params.id).rating
		assert tc.response.redirectedUrl.startsWith("/trip/next")
	}

	void testRatingActionBadUser() {//Выставление рейтинга поездки. Некорректный пользователь
									//Результаты: Неизменность рейтинга. Отсутствие ошибок. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.act = 'next'
		tc.params.id = 8
		tc.params.rating = 5

		tc.rating()
		assertFalse 5 == Trip.get(tc.params.id).rating
		assert tc.response.redirectedUrl.startsWith("/trip/next")
	}

	void testSaveWaitingAction() {//Подача заявки.
								  //Результаты: Запись в таблице Zayavka. Отсутствие ошибок. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		//tc.params.email = 5
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = []
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionSaveError() {//Подача заявки. Ошибка сохранения данных в БД.
										   //Результаты: Ошибка №8. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "Санкт-Петербург"
		tc.params.ztext = "Некий текст"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [8]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionBadTel() {//Подача заявки. Некорректный телефон
										//Результаты: Ошибка №7. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.ind = '7sss'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [7]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionNullTel() {//Подача заявки. Не указан телефон.
										 //Результаты: Ошибка №9. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [9]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionNullCity() {//Подача заявки. Не указан город.
										  //Результаты: Ошибка №1. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.ztext = "Some text"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [1]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionNullPricefrom() {//Подача заявки. не указана цена от.
											   //Результаты: Ошибка №2. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [2]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionNullZtext() {//Подача заявки. Не указан текст заявки.
										   //Результаты: Ошибка №3. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [3]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionNullDate_Start() {//Подача заявки. Не указана дата начала.
												//Результаты: Ошибка №4. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [4]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionNullDate_end() {//Подача заявки. Не указана дата окончания.
											  //Результаты: Ошибка №5. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"

		tc.saveWaiting()
		def test = [5]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionBadDate_End() {//Подача заявки. Дата начала позже даты окончания.
											 //Результаты: Ошибка №6. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.date_start_year = "2015"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [6]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionNullEmail() {//Подача заявки. Не указан емейл.
										   //Результаты: Ошибка №10. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'forTest')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [10]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionBadEmail() {//Подача заявки. Некорректный емейл.
										  //Результаты: Ошибка №11. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'forTest')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.email = "asasasasas"
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [11]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

	void testSaveWaitingActionEmailExist() {//Подача заявки. Емейл уже занят.
											//Результаты: Ошибка №12. Корректный редиррект.
		def tc = new TripController()
		tc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'forTest')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		tc.response.addCookie(oCookie)
		tc.request.setCookies(oCookie)

		tc.params.country_id = 1
		tc.params.region_id = 78
		tc.params.hometype_id = 2
		tc.params.homeperson_id = 2
		tc.params.valuta_id = 857
		tc.params.timetodecide_id = 3
		tc.params.pricefrom = 500
		tc.params.priceto = 5000
		tc.params.city = "SPb"
		tc.params.ztext = "Some text"
		tc.params.ind = '7'
		tc.params.kod = '921'
		tc.params.telef = '3509648'
		tc.params.email = "tesss@bk.ru"
		tc.params.date_start_year = "2014"
		tc.params.date_start_month = "02"
		tc.params.date_start_day = "15"
		tc.params.date_end_year = "2014"
		tc.params.date_end_month = "03"
		tc.params.date_end_day = "02"

		tc.saveWaiting()
		def test = [12]
		assertEquals test, tc.flash.error
		assert tc.response.redirectedUrl.startsWith("/trip/waiting")
	}

}
