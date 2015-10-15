import grails.test.*
import java.util.Calendar
import java.util.Random
import org.codehaus.groovy.grails.web.servlet.mvc.SynchronizerToken
import javax.servlet.http.Cookie
import grails.converters.JSON

class HomeControllerTests extends GroovyTestCase {

	def requestServiceProxy
	def jcaptchaService
	def usersServiceProxy
	
	private static final String charset = "!0123456789abcdefghijklmnopqrstuvwxyz"
	private static final String charsetForEmail = "abcdefghijklmnopqrstuvwxyz"
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {

    }

	void testAddhomeAction() {//Подача объявления. Результаты: Записи в Client, Home, HomeProp со статусами 0. Отсутствие ошибок.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		hc.params.email = sb.toString()+"@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = []
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionNoHomeprop() {//Подача объявления. Результаты: Записи в Client, Home со статусами 0. Отсутствие ошибок.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 78 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = ""
		hc.params.district = ""
		hc.params.street = "Nevsky pr."
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		hc.params.email = sb.toString()+"@test.ru"
		hc.params.homenumber = "25"

		hc.params.usage = "on"
		hc.params.term = ""
		hc.addhome()
		def test = []
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionUserSt1NoClient() {//Подача объявления авторизованным пользователем со статусом 1, не имеющим клиента.
											   //Результаты: Записи в Client со статусом 1, Home, HomeProp со статусами 2. Отсутствие ошибок.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
		
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."

		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"

		def oNewUser = new User()
		def sCode=java.util.UUID.randomUUID().toString()
		def password2 = '12345'
		def firstname = "D"
		def lastname = "M"
		def nickname = firstname+" "+lastname
		def lId = 0
		oNewUser.csiInsertInternal([email:email,password:Tools.hidePsw(password2),firstname:firstname,lastname:lastname,nickname:nickname,client_id:lId,code:sCode])

		def hsDbUser=User.findWhere(name:email)
		hsDbUser.modstatus = 1
		hsDbUser.save(flush:true)		
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)

		hc.addhome()
		def test = []
		assertEquals test, hc.flash.error
		assert hc.response.redirectedUrl.startsWith("/personal/adsoverview")
	}

	void testAddhomeActionUserSt0NoClient() {//Подача объявления авторизованным пользователем со статусом 0, не имеющим клиента.
											   //Результаты: Записи в Client со статусом 2, Home, HomeProp со статусами 2. Смена статуса у юзера на 2. Отсутствие ошибок.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
		
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."

		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"

		def oNewUser = new User()
		def sCode=java.util.UUID.randomUUID().toString()
		def password2 = '12345'
		def firstname = "D"
		def lastname = "M"
		def nickname = firstname+" "+lastname
		def lId = 0
		oNewUser.csiInsertInternal([email:email,password:Tools.hidePsw(password2),firstname:firstname,lastname:lastname,nickname:nickname,client_id:lId,code:sCode])

		def hsDbUser=User.findWhere(name:email)
		hsDbUser.modstatus = 0
		hsDbUser.save(flush:true)		
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)

		hc.addhome()
		def test = []
		assertEquals test, hc.flash.error
		assert hc.response.redirectedUrl.startsWith("/personal/adsoverview")
	}

	void testAddhomeActionUserSt2HaveClient() {//Подача объявления авторизованным пользователем со статусом 2, имеющим клиента.
											   //Результаты: Записи в Home, HomeProp со статусами 2 и ссылками на существующего клиента. Смена статуса у юзера на 2. Отсутствие ошибок.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
		
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."

		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"

		def oNewUser = new User()
		def sCode=java.util.UUID.randomUUID().toString()
		def password2 = '12345'
		def firstname = "D"
		def lastname = "M"
		def nickname = firstname+" "+lastname
		def lId = 0
		oNewUser.csiInsertInternal([email:email,password:Tools.hidePsw(password2),firstname:firstname,lastname:lastname,nickname:nickname,client_id:lId,code:sCode])

	    def oClient = new Client()
		oClient.name = email
		oClient.inputdate = new Date()
		oClient.modstatus = 2
		oClient.code = java.util.UUID.randomUUID().toString()
		oClient.save(flush:true)
		lId = oClient.csiGetLastInsert()

		def hsDbUser=User.findWhere(name:email)
		hsDbUser.modstatus = 2
		hsDbUser.client_id = lId
		hsDbUser.save(flush:true)		
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)

		hc.addhome()
		def test = []
		assertEquals test, hc.flash.error
		assert hc.response.redirectedUrl.startsWith("/personal/adsoverview")
	}

	void testAddhomeActionBadUsage() {//Подача объявления. Не согласие с условиями использования. Результаты: Ошибка №11
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "bad"
		hc.params.term = "on"
		hc.addhome()
		def test = [11]
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionBadTitle() {//Подача объявления. Плохое название объявления. Результаты: Ошибка №1
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = ""
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [1]
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionBadDescription() {//Подача объявления. Плохое описание объявления. Результаты: Ошибка №2
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = ""
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [2]
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionBadDateStart() {//Подача объявления. Некорректная дата начала аренды. Результаты: Ошибка №3
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [3]
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionBadDateEnd() {//Подача объявления. Некорректная дата окончания аренды. Результаты: Ошибка №4
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [4]
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionInvalidDates() {//Подача объявления. Дата начала аренды позже даты окончания. Результаты: Ошибка №20
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2012"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [20]
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionBadPrice() {//Подача объявления. Некорректная цена. Результаты: Ошибка №5
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = null
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [5]
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionEmptyEmail() {//Подача объявления. Пустой емейл. Результаты: Ошибка №6
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = ""
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [6]
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionBadEmail() {//Подача объявления. Некорректный емейл. Результаты: Ошибка №7
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "somepost!!!test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [7]
		assertEquals test, hc.flash.error
	}

	void testAddhomeActionBadIndex() {//Подача объявления. Некорректный индекс. Результаты: Ошибка №8
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = "asdasd"
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [8]
		assertEquals test, hc.flash.error
	}

/*	void testAddhomeActionEmptyIndex() {//Подача объявления. Пустой индекс. Результаты: Ошибка №9
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = ""
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [9]
		assertEquals test, hc.flash.error
	}*///DEPRECATED:Now index may be null

	void testAddhomeActionEmailAlredyExist() {//Подача объявления. Емейл уже существует. Результаты: Ошибка №13
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "tesss@bk.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [13]
		assertEquals test, hc.flash.warning
	}

	void testAddhomeActionInvalidToken() {//Подача объявления. Тест защиты от многократной отправки формы. Результаты: Ошибка №102
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

		hc.params.hometype_id = 1 //1..8
		hc.params.homeclass_id = 3 //1..3
		hc.params.homeperson_id = 4 //1..16
		hc.params.country_id = 1 //1..4 (1-rus)
		hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
		hc.params.price = 5000
		hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
		hc.params.pindex = 195426
		hc.params.title = "fantastic flat"
		hc.params.description = "more ..."
		hc.params.city = "Moskow"
		hc.params.district = ""
		hc.params.street = "Arbat st."
		hc.params.email = "notExist@test.ru"
		hc.params.homenumber = "25"
		hc.params.date_start_year = "2011"
		hc.params.date_start_month = "12"
		hc.params.date_start_day = "15"
		hc.params.date_end_year = "2011"
		hc.params.date_end_month = "12"
		hc.params.date_end_day = "31"
		hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
		hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
		hc.params.usage = "on"
		hc.params.term = "on"
		hc.addhome()
		def test = [102]
		assertEquals test, hc.flash.error
	}


	void testCalculatePriceAction() {//Калькулятор цены.
									 //Результаты: Отсутствие ошибок. Ненулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		hc.params.home_id = 396
		hc.params.homeperson_id = 1
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_start_day = date1.get(Calendar.DATE)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+10))
		hc.params.date_end_year = date1.get(Calendar.YEAR)
		hc.params.date_end_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_end_day = date1.get(Calendar.DATE)

		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result != 0
		assertNull JSON.parse(hc.response.contentAsString)?.error
		assertNull JSON.parse(hc.response.contentAsString)?.errorprop
	}

	void testCalculatePriceActionNotThreeParams() {//Калькулятор цены. Не все входящие параметры.
												   //Результаты: Отсутствие ошибок. Нулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		hc.params.home_id = 396
		hc.params.homeperson_id = 1
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_start_day = date1.get(Calendar.DATE)

		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result as String == "null"
		assert JSON.parse(hc.response.contentAsString)?.error == false
		assertNull JSON.parse(hc.response.contentAsString)?.errorprop
	}

	void testCalculatePriceActionBadDateEnd() {//Калькулятор цены. Дата начала позже даты окончания.
											   //Результаты: Ошибка: "Ошибка: дата начала больше даты окончания". Нулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		hc.params.home_id = 396
		hc.params.homeperson_id = 1
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_start_day = date1.get(Calendar.DATE)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)-10))
		hc.params.date_end_year = date1.get(Calendar.YEAR)
		hc.params.date_end_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_end_day = date1.get(Calendar.DATE)

		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result == 0
		assert JSON.parse(hc.response.contentAsString)?.error == true
		assert JSON.parse(hc.response.contentAsString)?.errorprop == "Ошибка: дата начала больше даты окончания"
	}

	void testCalculatePriceActionBadDateStart() {//Калькулятор цены. Дата начала до текущей даты.
												 //Результаты: Ошибка: "Ошибка: некорректная дата начала". Нулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		hc.params.home_id = 396
		hc.params.homeperson_id = 1
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH))
		hc.params.date_start_day = date1.get(Calendar.DATE)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+10))
		hc.params.date_end_year = date1.get(Calendar.YEAR)
		hc.params.date_end_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_end_day = date1.get(Calendar.DATE)

		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result == 0
		assert JSON.parse(hc.response.contentAsString)?.error == true
		assert JSON.parse(hc.response.contentAsString)?.errorprop == "Ошибка: некорректная дата начала"
	}

	void testCalculatePriceActionBadHomeId() {//Калькулятор цены. Неверный home_id.
											  //Результаты: Ошибка: "Извините. Невероятная ошибка.". Нулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		hc.params.homeperson_id = 1
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_start_day = date1.get(Calendar.DATE)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+10))
		hc.params.date_end_year = date1.get(Calendar.YEAR)
		hc.params.date_end_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_end_day = date1.get(Calendar.DATE)

		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result == 0
		assert JSON.parse(hc.response.contentAsString)?.error == true
		assert JSON.parse(hc.response.contentAsString)?.errorprop == "Извините. Невероятная ошибка."
	}

	void testCalculatePriceActionMindayException() {//Калькулятор цены. Диапазон меньше минимально допустимого.
													//Результаты: Ошибка: "Минимальная длительность пребывания <кол-во дней> ночей". Нулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		hc.params.home_id = 396
		hc.params.homeperson_id = 1
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_start_day = date1.get(Calendar.DATE)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+1))
		hc.params.date_end_year = date1.get(Calendar.YEAR)
		hc.params.date_end_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_end_day = date1.get(Calendar.DATE)

		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result == 0
		assert JSON.parse(hc.response.contentAsString)?.error == true
		assert JSON.parse(hc.response.contentAsString)?.errorprop.startsWith("Минимальная длительность пребывания")
	}

	void testCalculatePriceActionMaxdayException() {//Калькулятор цены. Диапазон больше максимально допустимого.
													//Результаты: Ошибка: "Максимальная длительность пребывания <кол-во дней> ночей". Нулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		hc.params.home_id = 396
		hc.params.homeperson_id = 1
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_start_day = date1.get(Calendar.DATE)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+100))
		hc.params.date_end_year = date1.get(Calendar.YEAR)
		hc.params.date_end_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_end_day = date1.get(Calendar.DATE)

		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result == 0
		assert JSON.parse(hc.response.contentAsString)?.error == true
		assert JSON.parse(hc.response.contentAsString)?.errorprop.startsWith("Максимальная длительность пребывания")
	}

	void testCalculatePriceActionMaxpersonException() {//Калькулятор цены. Превышено максимальное кол-во человек.
													   //Результаты: Ошибка: "Максимальное количество гостей - <кол-во человек>". Нулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		hc.params.home_id = 396
		hc.params.homeperson_id = 10
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_start_day = date1.get(Calendar.DATE)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+10))
		hc.params.date_end_year = date1.get(Calendar.YEAR)
		hc.params.date_end_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_end_day = date1.get(Calendar.DATE)

		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result == 0
		assert JSON.parse(hc.response.contentAsString)?.error == true
		assert JSON.parse(hc.response.contentAsString)?.errorprop.startsWith("Максимальное количество гостей - ")
	}

	void testCalculatePriceActionRegularArendaException() {//Калькулятор цены. Регулярная аренда не задана, диапазон вне периода временной аренды.
														   //Результаты: Ошибка: "Эти даты недоступны". Нулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		def oHome = Home.get(396)
		oHome.pricestatus = 0
		oHome.save(flush:true)
		oHome.removeRegular()

		hc.params.home_id = 396
		hc.params.homeperson_id = 1
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+100))
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_start_day = date1.get(Calendar.DATE)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+10))
		hc.params.date_end_year = date1.get(Calendar.YEAR)
		hc.params.date_end_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_end_day = date1.get(Calendar.DATE)

		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result == 0
		assert JSON.parse(hc.response.contentAsString)?.error == true
		assert JSON.parse(hc.response.contentAsString)?.errorprop == "Эти даты недоступны"

		oHome = Home.get(396)
		oHome.pricestatus = 1
		oHome.save(flush:true)
		oHome.addRegular()
	}

	void testCalculatePriceActionInactiveArendaException() {//Калькулятор цены. Диапазон включает период неактивной аренды.
															//Результаты: Ошибка: "Эти даты недоступны". Нулевая цена.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)
		
		hc.params.home_id = 396
		hc.params.homeperson_id = 1
		def dateStart=new Date()
		def date1= new GregorianCalendar()
		date1.setTime(dateStart)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+100))
		hc.params.date_start_year = date1.get(Calendar.YEAR)
		hc.params.date_start_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_start_day = date1.get(Calendar.DATE)
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+10))
		hc.params.date_end_year = date1.get(Calendar.YEAR)
		hc.params.date_end_month = (date1.get(Calendar.MONTH)+1)
		hc.params.date_end_day = date1.get(Calendar.DATE)

	    def oHomeprop = new Homeprop()
		oHomeprop.home_id = hc.params.home_id
		oHomeprop.modstatus = 1
		def date = new Date()
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)-7))
	    oHomeprop.date_start = date1.getTime()
		date1.set(Calendar.DATE,(date1.get(Calendar.DATE)+5))
	    oHomeprop.date_end = date1.getTime()
	    oHomeprop.term = 0
		oHomeprop.save(flush:true)
		
		def id = oHomeprop.id
		
		hc.calculatePrice()
		assert JSON.parse(hc.response.contentAsString)?.result == 0
		assert JSON.parse(hc.response.contentAsString)?.error == true
		assert JSON.parse(hc.response.contentAsString)?.errorprop == "Эти даты недоступны"

		oHomeprop = Homeprop.get(id)
		oHomeprop.delete(flush:true)
	}

	void testAddmboxAction() {//Заявка по объявлению.
							  //Результаты: Записи в таблицах Mbox, Mboxrec со корректными ссылками на объявление.
							  //Отсутствие ошибок.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		hc.params.id = 767
		hc.params.homeperson_id = 2
		hc.params.is_telperm = 0
		hc.params.timezone = 16
		hc.params.mtext = 'some'
		hc.params.mail_date_start_year = "2014"
		hc.params.mail_date_start_month = "02"
		hc.params.mail_date_start_day = "15"
		hc.params.mail_date_end_year = "2014"
		hc.params.mail_date_end_month = "03"
		hc.params.mail_date_end_day = "02"

		Home.metaClass.static.calculateHomePrice = { def hsR, def lId -> return [error:false,result:10500] }

		hc.addmbox()

		assertFalse JSON.parse(hc.response.contentAsString).error == 1
		assert JSON.parse(hc.response.contentAsString).mbox_error == []
		assertNotNull Mbox.findByHome_id(767)
		assertNotNull Mboxrec.findByHome_id(767)

		def temp = Mbox.findByHome_id(767)
		temp?.delete(flush:true)
		def aTemp = Mboxrec.findAllByHome_id(767)
		for (t in aTemp)
			t.delete(flush:true)
	}

	void testAddmboxActionNullDateStart() {//Заявка по объявлению. Не указана дата начала.
										   //Результаты: ошибка №1.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		hc.params.id = 767
		hc.params.homeperson_id = 2
		hc.params.is_telperm = 0
		hc.params.timezone = 16
		hc.params.mtext = 'some'
		hc.params.mail_date_end_year = "2014"
		hc.params.mail_date_end_month = "03"
		hc.params.mail_date_end_day = "02"

		Home.metaClass.static.calculateHomePrice = { def hsR, def lId -> return [error:false,result:10500] }

		hc.addmbox()

		assertFalse JSON.parse(hc.response.contentAsString).error == 1
		assert JSON.parse(hc.response.contentAsString).mbox_error == [1]
		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)
	}

	void testAddmboxActionNullDateEnd() {//Заявка по объявлению. Не указана дата окончания.
										 //Результаты: ошибка №2 и 3.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		hc.params.id = 767
		hc.params.homeperson_id = 2
		hc.params.is_telperm = 0
		hc.params.timezone = 16
		hc.params.mtext = 'some'
		hc.params.mail_date_start_year = "2014"
		hc.params.mail_date_start_month = "02"
		hc.params.mail_date_start_day = "15"

		Home.metaClass.static.calculateHomePrice = { def hsR, def lId -> return [error:false,result:10500] }

		hc.addmbox()

		assertFalse JSON.parse(hc.response.contentAsString).error == 1
		assert JSON.parse(hc.response.contentAsString).mbox_error == [2,3]
		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)
	}

	void testAddmboxActionBadDateEnd() {//Заявка по объявлению. Не корректная дата окончания.
										//Результаты: ошибка №3.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		hc.params.id = 767
		hc.params.homeperson_id = 2
		hc.params.is_telperm = 0
		hc.params.timezone = 16
		hc.params.mtext = 'some'
		hc.params.mail_date_start_year = "2014"
		hc.params.mail_date_start_month = "03"
		hc.params.mail_date_start_day = "15"
		hc.params.mail_date_end_year = "2014"
		hc.params.mail_date_end_month = "03"
		hc.params.mail_date_end_day = "02"

		Home.metaClass.static.calculateHomePrice = { def hsR, def lId -> return [error:false,result:10500] }

		hc.addmbox()

		assertFalse JSON.parse(hc.response.contentAsString).error == 1
		assert JSON.parse(hc.response.contentAsString).mbox_error == [3]
		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)
	}

	void testAddmboxActionNullMtext() {//Заявка по объявлению. Не указан текст запроса.
									   //Результаты: ошибка №4.
		def hc = new HomeController()
		hc.requestService = requestServiceProxy

		def hsDbUser=User.findWhere(name:'tesssss@gmail.com')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'
		oCookie.maxAge = -1 //108000// 30 days
		hc.response.addCookie(oCookie)
		hc.request.setCookies(oCookie)

		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)

		hc.params.id = 767
		hc.params.homeperson_id = 2
		hc.params.is_telperm = 0
		hc.params.timezone = 16
		hc.params.mail_date_start_year = "2014"
		hc.params.mail_date_start_month = "02"
		hc.params.mail_date_start_day = "15"
		hc.params.mail_date_end_year = "2014"
		hc.params.mail_date_end_month = "03"
		hc.params.mail_date_end_day = "02"

		Home.metaClass.static.calculateHomePrice = { def hsR, def lId -> return [error:false,result:10500] }

		hc.addmbox()

		assertFalse JSON.parse(hc.response.contentAsString).error == 1
		assert JSON.parse(hc.response.contentAsString).mbox_error == [4]
		assertNull Mbox.findByHome_id(767)
		assertNull Mboxrec.findByHome_id(767)
	}

/*
	void testAvtogenerateAddhomeAction() {
		def hc = new HomeController()
		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService
		hc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]

        SynchronizerToken token = new SynchronizerToken()
//        hc.request.session.setAttribute(SynchronizerToken.KEY,token)

//        hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		1.times{

			hc.request.clearAttributes()
			hc.request.removeAllParameters()
			hc.response.committed = false
			hc.response.reset()
			hc.response.writer = null
			token = new SynchronizerToken()
			hc.request.session.setAttribute(SynchronizerToken.KEY,token)
			hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
			
			hc.params.hometype_id = rand.nextInt().abs() % 8 + 1 //1..8
			hc.params.homeclass_id = rand.nextInt().abs() % 3 + 1 //1..3
			hc.params.homeperson_id = rand.nextInt().abs() % 16 + 1 //1..16
			hc.params.country_id = 1 //1..4 (1-rus)
			def reg_ind = (int)(rand.nextInt().abs() % 2)
			if ( reg_ind == 1 ) {
				hc.params.region_id = 77 //1..79,83,86,87,89,95 (78-spb, 77-msk)
			} else {
				hc.params.region_id = 78 //1..79,83,86,87,89,95 (78-spb, 77-msk)
			}
			hc.params.price = ((rand.nextInt().abs() % 6 + 1)*1000)
			hc.params.valuta_id = 857 //857-rub, 840-usd, 978-eur
			hc.params.pindex = 195426
			sb.setLength(0)
			20.times {
				int pos = rand.nextInt(charset.length())
				sb.append(charset.charAt(pos))
			}
			hc.params.title = sb.toString()
			sb.setLength(0)
			40.times {
				int pos = rand.nextInt(charset.length())
				sb.append(charset.charAt(pos))
			}
			hc.params.description = sb.toString()
			hc.params.city = ""
			hc.params.district = ""
			int street_ind = (rand.nextInt().abs() % 20 + 1)
			switch (street_ind) {
				case 1: hc.params.street = "Nevsky pr."; break;
				case 2: hc.params.street = "Moskovsky pr."; break;
				case 3: hc.params.street = "Lenina pr."; break;
				case 4: hc.params.street = "Ligovsky pr."; break;
				case 5: hc.params.street = "Liteiny pr."; break;
				case 6: hc.params.street = "Leningradsky pr."; break;
				case 7: hc.params.street = "Nekrasova st."; break;
				case 8: hc.params.street = "Frunze st."; break;
				case 9: hc.params.street = "Turku st."; break;
				case 10: hc.params.street = "Dybenko st."; break;
				case 11: hc.params.street = "Lenskaya st."; break;
				case 12: hc.params.street = "Zamshina st."; break;
				case 13: hc.params.street = "Nauki pr."; break;
				case 14: hc.params.street = "Kulturi pr."; break;
				case 15: hc.params.street = "Kompositorov st."; break;
				case 16: hc.params.street = "Turistkaya st."; break;
				case 17: hc.params.street = "Arbat st."; break;
				default: hc.params.street = "Tverskaya st."; break;
			}
			sb.setLength(0)
			15.times {
				int pos = rand.nextInt(charsetForEmail.length())
				sb.append(charsetForEmail.charAt(pos))
			}
			hc.params.email = sb.toString()+"@test.ru"
			hc.params.homenumber = (rand.nextInt().abs() % 40 + 1).toString()
//			hc.params.date_start_year = "2011"
//			hc.params.date_start_month = "12"
//			hc.params.date_start_day = "15"
//			hc.params.date_end_year = "2011"
//			hc.params.date_end_month = "12"
//			hc.params.date_end_day = "31"
//			hc.params.date_start = hc.params.date_start_year+hc.params.date_start_month+hc.params.date_start_day
//			hc.params.date_end = hc.params.date_end_year+hc.params.date_end_month+hc.params.date_end_day
			hc.params.usage = "on"
			hc.params.term = ""
			hc.addhome()
		}
		def test = []
		assertEquals test, hc.flash.error
	}
*/
}