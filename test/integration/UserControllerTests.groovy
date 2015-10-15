import grails.test.*
import java.util.Random
import org.codehaus.groovy.grails.web.servlet.mvc.SynchronizerToken
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import javax.servlet.http.Cookie

class UserControllerTests extends GroovyTestCase {

	def requestServiceProxy
	def usersServiceProxy
	def jcaptchaService
	
	private static final String charsetForEmail = "abcdefghijklmnopqrstuvwxyz"

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }
    
	void testSomething() {

    }

	void testLoginAction() {//Обработка авторизации. Логин через провайдер "arenda". Результаты: Корректный редиррект. Отсутствие ошибок.
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		uc.usersService = usersServiceProxy
		uc.usersService = [loginInternalUser:{ def sUser, def sPassword, def requestService, def iRemember, def user_id -> return true }]
		uc.usersService.INTERNALPROVIDER = 'staytoday'
		uc.params.user = 'asd@test.ru'
		uc.params.provider = 'staytoday'
		uc.params.password = 'qwerty'
		uc.login()
		assert uc.response.redirectedUrl.startsWith("/user/index")
		assertNull uc.flash.error
	}

	void testLoginActionWithProvOrPassIsNull() {//Обработка авторизации. Некорректные user или провайдер. Результаты: Ошибка №1
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		uc.usersService = usersServiceProxy
		uc.usersService = [loginInternalUser:{ def sUser, def sPassword, def requestService, def iRemember, def user_id -> return true }]
		uc.params.user = ''
		uc.params.provider = 'staytoday'
		uc.params.password = 'qwerty'
		uc.login()
		assert uc.response.redirectedUrl.startsWith("/user/index")
		assertEquals 1, uc.flash.error
	}

	void testLoginActionFailed() {//Обработка авторизации. Неудачный логин. Результаты: Ошибка №51
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		uc.usersService = usersServiceProxy
		uc.usersService = [loginInternalUser:{ def sUser, def sPassword, def requestService, def iRemember, def user_id -> return false }]
		uc.usersService.INTERNALPROVIDER = 'staytoday'
		uc.params.user = 'asd@test.ru'
		uc.params.provider = 'staytoday'
		uc.params.password = 'qwerty'
		uc.login()
		assert uc.response.redirectedUrl.startsWith("/user/index")
		assertEquals 51, uc.flash.error
	}


	void testSaveUserActionNoClient() {//simple registration. Результат: новая запись в таблице User. Отсутствие ошибок.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		uc.params.email = email
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.saveuser()
		def x = []
		assert uc.response.redirectedUrl.startsWith("/user/login")
		assertEquals x, uc.flash.error
	}

	void testSaveUserActionNoEmail() {//Регистрация без емейла. Ошибка №1.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.saveuser()
		def x = [1]
		assert uc.response.redirectedUrl.startsWith("/user/addnew")
		assertEquals x, uc.flash.error
	}

	void testSaveUserActionBadEmail() {//Регистрация с некорректным емейлом. Ошибка №2
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		uc.params.email = "bad"
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.saveuser()
		def x = [2]
		assert uc.response.redirectedUrl.startsWith("/user/addnew")
		assertEquals x, uc.flash.error
	}

	void testSaveUserActionEmptyNickname() {//регистрация с пустым ником. Ошибка №3
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		uc.params.email = email
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		def hsRes = uc.saveuser()
		def x = [3]
		assert uc.response.redirectedUrl.startsWith("/user/addnew")
		assertEquals x, uc.flash.error
	}

	void testSaveUserActionNotEqualPass() {//регистрация с несовпадающими паролями. Ошибка №4
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		uc.params.email = email
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty1'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.saveuser()
		def x = [4]
		assert uc.response.redirectedUrl.startsWith("/user/addnew")
		assertEquals x, uc.flash.error
	}

	void testSaveUserActionBadPass() {//регистрация с некорректным паролем. Ошибка №5
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		uc.params.email = email
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwe'
		uc.params.password2 = 'qwe'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.saveuser()
		def x = [5]
		assert uc.response.redirectedUrl.startsWith("/user/addnew")
		assertEquals x, uc.flash.error
	}

	void testSaveUserActionClient0NotChangeEmail() {//Регистрация во время подачи объявления. Емейл не меняется.
													//Результат: Записи в таблице User, Client, Home, Homeprop со статусами 2. Отсутствие ошибок.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

	    def oClient = new Client()
		oClient.name = email
		oClient.inputdate = new Date()
		oClient.modstatus = 0
		oClient.code = java.util.UUID.randomUUID().toString()
		oClient.save(flush:true)
		def lId = oClient.id

		def oReq = [:]
		oReq.title = "name"
        oReq.hometype_id = 1
        oReq.homeclass_id = 1
        oReq.homeperson_id = 1
        oReq.country_id = 1
        oReq.region_id = 1
		oReq.city = ''
		oReq.street = ''
        oReq.address = "....."
		oReq.description = "...."
		oReq.pindex = "123456"
	    oReq.price=0
		oReq.valuta_id = 857
		oReq.term=1

		def oHome = new Home(oReq,lId)
		oHome.save(flush:true)
		def lHomeId=oHome.id

	    def oHomeprop = new Homeprop()
		oHomeprop.home_id = lHomeId
		oHomeprop.modstatus = 0
	    oHomeprop.price = 2505
	    oHomeprop.valuta_id = 857
		def date = new Date()
	    oHomeprop.date_start = date
	    oHomeprop.date_end = date
	    oHomeprop.term = 1
		oHomeprop.save(flush:true)
		
		uc.params.code = oClient.code
		uc.params.home_id = lHomeId
		uc.params.email = email
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.saveuser()
		def x = []
		assert uc.response.redirectedUrl.startsWith("/user/login")
		assertEquals x, uc.flash.error
	}

	void testSaveUserActionClient0ChangeEmail() {//Регистрация во время подачи объявления. Емейл меняется со значения "change@test.ru" на новый.
												 //Результаты: Записи в таблице User, Client, Home, Homeprop со статусами 2. Отсутствие ошибок. У клиента новый емейл.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)

		def email = "change@test.ru"

	    def oClient = new Client()
		oClient.name = email
		oClient.inputdate = new Date()
		oClient.modstatus = 0
		oClient.code = java.util.UUID.randomUUID().toString()
		oClient.save(flush:true)
		def lId = oClient.id

		def oReq = [:]
		oReq.title = "name"
        oReq.hometype_id = 1
        oReq.homeclass_id = 1
        oReq.homeperson_id = 1
        oReq.country_id = 1
        oReq.region_id = 1
		oReq.city = ''
		oReq.street = ''
        oReq.address = "....."
		oReq.description = "...."
		oReq.pindex = "123456"
	    oReq.price=0
		oReq.valuta_id = 857
		oReq.term=1

		def oHome = new Home(oReq,lId)
		oHome.save(flush:true)
		def lHomeId=oHome.id
		
	    def oHomeprop = new Homeprop()
		oHomeprop.home_id = lHomeId
		oHomeprop.modstatus = 0
	    oHomeprop.price = 2505
	    oHomeprop.valuta_id = 857
		def date = new Date()
	    oHomeprop.date_start = date
	    oHomeprop.date_end = date
	    oHomeprop.term = 1
		oHomeprop.save(flush:true)
		
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		email = sb.toString()+"@test.ru"
		
		uc.params.code = oClient.code
		uc.params.home_id = lHomeId
		uc.params.email = email
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.saveuser()
		def x = []
		assert uc.response.redirectedUrl.startsWith("/user/login")
		assertEquals x, uc.flash.error
	}


	void testSaveUserActionClient0ChangeEmailArendaAuth() {//Регистрация во время подачи объявления. Емейл меняется со значения "change@test.ru" на уже зарегистрированный "qwerty@asd.ru".
														   //После этого происходит успешная авторизация с данным емейлом.
														   //Результаты: Записи в таблице Home, Homeprop со статусами 2, ссылающиеся на уже существовавший клиент. Отсутствие ошибок.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)

		def email = "change@test.ru"

	    def oClient = new Client()
		oClient.name = email
		oClient.inputdate = new Date()
		oClient.modstatus = 0
		oClient.code = java.util.UUID.randomUUID().toString()
		oClient.save(flush:true)
		def lId = oClient.id

		def oReq = [:]
		oReq.title = "name"
        oReq.hometype_id = 1
        oReq.homeclass_id = 1
        oReq.homeperson_id = 1
        oReq.country_id = 1
        oReq.region_id = 1
		oReq.city = ''
		oReq.street = ''
        oReq.address = "....."
		oReq.description = "...."
		oReq.pindex = "123456"
	    oReq.price=0
		oReq.valuta_id = 857
		oReq.term=1

		def oHome = new Home(oReq,lId)
		oHome.save(flush:true)
		def lHomeId=oHome.id
		
	    def oHomeprop = new Homeprop()
		oHomeprop.home_id = lHomeId
		oHomeprop.modstatus = 0
	    oHomeprop.price = 2505
	    oHomeprop.valuta_id = 857
		def date = new Date()
	    oHomeprop.date_start = date
	    oHomeprop.date_end = date
	    oHomeprop.term = 1
		oHomeprop.save(flush:true)

		email = "qwerty@asd.ru"
		
		uc.params.code = oClient.code
		uc.params.home_id = lHomeId
		uc.params.email = email
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.saveuser()
		def x = [6]
		assert uc.response.redirectedUrl.startsWith("/user/addnew")
		assertEquals x, uc.flash.error

		uc.request.clearAttributes()
		uc.request.removeAllParameters()
		uc.response.committed = false
		uc.response.reset()
		uc.flash.error = null

		uc.params.control = 'user'
		uc.params.act = 'addnew'
		uc.params.code = oClient.code
		uc.params.home_id = lHomeId

		def hsDbUser=User.findWhere(name:email)

		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		uc.response.addCookie(oCookie)
		uc.request.setCookies(oCookie)
		
		uc.index()
		assertNull uc.flash.error
	}

	void testSaveUserActionClient0ChangeEmailArendaAuthWithoutClient() {//Регистрация во время подачи объявления. Емейл меняется со значения "change@test.ru" на уже зарегистрированный.
																		//У существующего User нет связанного клиента. После этого происходит успешная авторизация с данным емейлом.
																		//Результаты: Записи в таблице Client, Home, Homeprop со статусами 2. Смена статуса у User на 2, привязка клиента к юзеру. Отсутствие ошибок.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)

		def email = "change@test.ru"

	    def oClient = new Client()
		oClient.name = email
		oClient.inputdate = new Date()
		oClient.modstatus = 0
		oClient.code = java.util.UUID.randomUUID().toString()
		oClient.save(flush:true)
		def lId = oClient.id

		def oReq = [:]
		oReq.title = "name"
        oReq.hometype_id = 1
        oReq.homeclass_id = 1
        oReq.homeperson_id = 1
        oReq.country_id = 1
        oReq.region_id = 1
		oReq.city = ''
		oReq.street = ''
        oReq.address = "....."
		oReq.description = "...."
		oReq.pindex = "123456"
	    oReq.price=0
		oReq.valuta_id = 857
		oReq.term=1

		def oHome = new Home(oReq,lId)
		oHome.save(flush:true)
		def lHomeId=oHome.id
		
	    def oHomeprop = new Homeprop()
		oHomeprop.home_id = lHomeId
		oHomeprop.modstatus = 0
	    oHomeprop.price = 2505
	    oHomeprop.valuta_id = 857
		def date = new Date()
	    oHomeprop.date_start = date
	    oHomeprop.date_end = date
	    oHomeprop.term = 1
		oHomeprop.save(flush:true)

		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		email = sb.toString()+"@test.ru"
		
		uc.params.code = oClient.code
		uc.params.home_id = lHomeId
		uc.params.email = email
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname

		def oNewUser = new User()
		def sCode=java.util.UUID.randomUUID().toString()
		def password2 = '12345'
		def firstname = "D"
		def lastname = "M"
		def nickname = firstname+" "+lastname
		lId = 0
		oNewUser.csiInsertInternal([email:email,password:Tools.hidePsw(password2),firstname:firstname,lastname:lastname,nickname:nickname,client_id:lId,code:sCode])
		def hsDbUser=User.findWhere(name:email)
		hsDbUser.modstatus = 0
		hsDbUser.save(flush:true)

		def hsRes = uc.saveuser()
		def x = [6]
		assert uc.response.redirectedUrl.startsWith("/user/addnew")
		assertEquals x, uc.flash.error

		uc.request.clearAttributes()
		uc.request.removeAllParameters()
		uc.response.committed = false
		uc.response.reset()
		uc.flash.error = null

		uc.params.control = 'user'
		uc.params.act = 'addnew'
		uc.params.code = oClient.code
		uc.params.home_id = lHomeId

		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		uc.response.addCookie(oCookie)
		uc.request.setCookies(oCookie)
		
		uc.index()
		assertNull uc.flash.error
	}


	void testSaveUserActionClient0VkAuth() {//Регистрация через Вконтакте во время подачи объявления.
											//Результаты: Записи в таблице Client, Home, Homeprop со статусами 2.
											//			  Записи в таблице User:1)Основной аккаунт. Провайдер "arenda". Статус 2.
											//									2)Аккаунт Вконтакте. Провайдер "vkontakte". Статус 2.
											//			  Отсутствие ошибок.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

	    def oClient = new Client()
		oClient.name = email
		oClient.inputdate = new Date()
		oClient.modstatus = 0
		oClient.code = java.util.UUID.randomUUID().toString()
		oClient.save(flush:true)
		def lId = oClient.id

		def oReq = [:]
		oReq.title = "name"
        oReq.hometype_id = 1
        oReq.homeclass_id = 1
        oReq.homeperson_id = 1
        oReq.country_id = 1
        oReq.region_id = 1
		oReq.city = ''
		oReq.street = ''
        oReq.address = "....."
		oReq.description = "...."
		oReq.pindex = "123456"
	    oReq.price=0
		oReq.valuta_id = 857
		oReq.term=1

		def oHome = new Home(oReq,lId)
		oHome.save(flush:true)
		def lHomeId=oHome.id
		
	    def oHomeprop = new Homeprop()
		oHomeprop.home_id = lHomeId
		oHomeprop.modstatus = 0
	    oHomeprop.price = 2505
	    oHomeprop.valuta_id = 857
		def date = new Date()
	    oHomeprop.date_start = date
	    oHomeprop.date_end = date
	    oHomeprop.term = 1
		oHomeprop.save(flush:true)

		def vk_id = rand.nextInt().abs() % 100000000 + 1
		uc.params.code = oClient.code
		uc.params.home_id = lHomeId
		uc.params.email = email
		uc.params.vk_id = vk_id

		def hsRes = uc.saveuser()
		def x = []
		assert uc.response.redirectedUrl.startsWith("/user/vk")
		assertEquals x, uc.flash.error
		
		def ref_id = uc.flash.ref_id
		
		uc.request.clearAttributes()
		uc.request.removeAllParameters()
		uc.response.committed = false
		uc.response.reset()


		def sStr=ConfigurationHolder.config.vk.APIKey+vk_id.toString()+ConfigurationHolder.config.vk.SecretKey
		uc.params.vk_id = vk_id
		uc.params.vk_pic = ""
		uc.params.vk_photo = ""
		uc.params.vk_name = "Dm. M."
		uc.params.vk_hash = (sStr).encodeAsMD5()
		uc.flash.ref_id = ref_id
		
		uc.vk()
		assert uc.response.redirectedUrl.startsWith("/personal/adsoverview")
		assert uc.flash.error == []
	}

	void testSaveUserActionVkAuthHaveUserAndClient() {//Регистрация через Вконтакте во время подачи объявления. Аккаунт Вконтакте уже имеет связанные записи в User, Client, Home, HomeProp
													  //Результаты: Записи в таблице Home, Homeprop со статусами 2, ссылающиеся на уже существовавший клиент.
													  //			Отсутствие ошибок.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

	    def oClient = new Client()
		oClient.name = email
		oClient.inputdate = new Date()
		oClient.modstatus = 0
		oClient.code = java.util.UUID.randomUUID().toString()
		oClient.save(flush:true)
		def lId = oClient.id

		def oReq = [:]
		oReq.title = "name"
        oReq.hometype_id = 1
        oReq.homeclass_id = 1
        oReq.homeperson_id = 1
        oReq.country_id = 1
        oReq.region_id = 1
		oReq.city = ''
		oReq.street = ''
        oReq.address = "....."
		oReq.description = "...."
		oReq.pindex = "123456"
	    oReq.price=0
		oReq.valuta_id = 857
		oReq.term=1

		def oHome = new Home(oReq,lId)
		oHome.save(flush:true)
		def lHomeId=oHome.id
		
	    def oHomeprop = new Homeprop()
		oHomeprop.home_id = lHomeId
		oHomeprop.modstatus = 0
	    oHomeprop.price = 2505
	    oHomeprop.valuta_id = 857
		def date = new Date()
	    oHomeprop.date_start = date
	    oHomeprop.date_end = date
	    oHomeprop.term = 1
		oHomeprop.save(flush:true)

		def vk_id = 76442440
		uc.params.code = oClient.code
		uc.params.home_id = lHomeId
		uc.params.email = email
		uc.params.vk_id = vk_id

		def hsRes = uc.saveuser()
		def x = []
		assert uc.response.redirectedUrl.startsWith("/user/vk")
		assertEquals x, uc.flash.error
		
		def ref_id = uc.flash.ref_id
		
		uc.request.clearAttributes()
		uc.request.removeAllParameters()
		uc.response.committed = false
		uc.response.reset()


		def sStr=ConfigurationHolder.config.vk.APIKey+vk_id.toString()+ConfigurationHolder.config.vk.SecretKey
		uc.params.vk_id = vk_id
		uc.params.vk_pic = ""
		uc.params.vk_photo = ""
		uc.params.vk_name = "Dm. M."
		uc.params.vk_hash = (sStr).encodeAsMD5()
		uc.flash.ref_id = ref_id
		
		uc.vk()
		assert uc.response.redirectedUrl.startsWith("/personal/adsoverview")
		assert uc.flash.error == []
	}


	void testConfirmAction () {//Подтверждение аккаунта. Результаты: запись в таблице Client, User со статусом 1. 
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

	    def oClient = new Client()
		oClient.name = email
		oClient.inputdate = new Date()
		oClient.modstatus = 0
		oClient.code = java.util.UUID.randomUUID().toString()
		oClient.save(flush:true)
		def lId = oClient.csiGetLastInsert()
		
		def oNewUser = new User()
		def sCode=java.util.UUID.randomUUID().toString()
		def password2 = 'qwerty'
		def firstname = "D"
		def lastname = "M"
		def nickname = firstname+" "+lastname
		oNewUser.csiInsertInternal([email:email,password:Tools.hidePsw(password2),firstname:firstname,lastname:lastname,nickname:nickname,client_id:lId,code:sCode])

		uc.params.id = sCode
		uc.confirm()
		assert uc.response.redirectedUrl.startsWith("/user/login")
	}

	void testConfirmActionBadResult () {//Неудачное подтверждение аккаунта. Результаты: Ошибка №1.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		def sCode=java.util.UUID.randomUUID().toString()
		uc.params.id = sCode
		uc.confirm()
		assert uc.flash.error == 1
	}


	void testRestAction() {//Восстановление пароля. Результаты: отсутствие ошибок.
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		uc.jcaptchaService = jcaptchaService
		uc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]
	
		SynchronizerToken token = new SynchronizerToken()
		uc.request.session.setAttribute(SynchronizerToken.KEY,token)
		uc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())

		uc.params.name = "futdrfausdjaxjl@test.ru"
		def hsRes = uc.rest()
		assertEquals 0, hsRes.inrequest.error
	}

	void testRestActionInvalidCaptha() {//Восстановление пароля. Неправильная капча. Результаты: Ошибка №3.
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		uc.jcaptchaService = jcaptchaService
		uc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return false }]
	
		SynchronizerToken token = new SynchronizerToken()
		uc.request.session.setAttribute(SynchronizerToken.KEY,token)
		uc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
		
		uc.params.name = "futdrfausdjaxjl@test.ru"
		def hsRes = uc.rest()
		assertEquals 3, hsRes.inrequest.error
		assert uc.response.redirectedUrl.startsWith("/user/restore")
	}

	void testRestActionUserNotExist() {//Восстановление пароля. Неправильно указанный емейл. Результаты: Ошибка №1.
		def uc = new UserController()
		uc.requestService = requestServiceProxy
	
		SynchronizerToken token = new SynchronizerToken()
		uc.request.session.setAttribute(SynchronizerToken.KEY,token)
		uc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
		
		uc.params.name = "notExist@test.ru"
		def hsRes = uc.rest()
		assertEquals 1, hsRes.inrequest.error
		assert uc.response.redirectedUrl.startsWith("/user/restore")
	}

	void testRestActionInvalidToken() {//Восстановление пароля. Тест защиты от многократной отправки формы. Результаты: Ошибка №5.
		def uc = new UserController()
		uc.requestService = requestServiceProxy
	
		def hsRes = uc.rest()
		assertEquals 5, hsRes.inrequest.error
		assert uc.response.redirectedUrl.startsWith("/user/restore")
	}


	void testPasswsetupAction() {//Восстановление пароля, этап смены пароля. Пароль меняется со значения "12345" на "qwerty123"
								 //Результаты: Запись в таблице User с новым паролем.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		def oNewUser = new User()
		def sCode=java.util.UUID.randomUUID().toString()
		def password2 = '12345'
		def firstname = "D"
		def lastname = "M"
		def nickname = firstname+" "+lastname
		def lId = 0
		oNewUser.csiInsertInternal([email:email,password:Tools.hidePsw(password2),firstname:firstname,lastname:lastname,nickname:nickname,client_id:lId,code:sCode])

		uc.session.regusercode = sCode
		uc.session.startchange = false
		uc.params.password1 = 'qwerty123'
		uc.params.password2 = 'qwerty123'
		def err = uc.passwsetup()
		assert uc.response.redirectedUrl.startsWith("/user/login")
		assert err.inrequest.error==-1
	}

	void testPasswsetupActionNotEqualPass() {//Восстановление пароля, этап смены пароля. Несовпадение паролей. Результаты: ошибка №1, запись в таблице User со старым паролем.
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		def oNewUser = new User()
		def sCode=java.util.UUID.randomUUID().toString()
		def password2 = '12345'
		def firstname = "D"
		def lastname = "M"
		def nickname = firstname+" "+lastname
		def lId = 0
		oNewUser.csiInsertInternal([email:email,password:Tools.hidePsw(password2),firstname:firstname,lastname:lastname,nickname:nickname,client_id:lId,code:sCode])

		uc.session.regusercode = sCode
		uc.session.startchange = false
		uc.params.password1 = 'qwerty123'
		uc.params.password2 = 'qwerty'
		def err = uc.passwsetup()
		assert err.inrequest.error==1
	}

	void testPasswsetupActionBadPass() {//Восстановление пароля, этап смены пароля. Некорректный пароль. Результаты: ошибка №2, запись в таблице User со старым паролем
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		def oNewUser = new User()
		def sCode=java.util.UUID.randomUUID().toString()
		def password2 = '12345'
		def firstname = "D"
		def lastname = "M"
		def nickname = firstname+" "+lastname
		def lId = 0
		oNewUser.csiInsertInternal([email:email,password:Tools.hidePsw(password2),firstname:firstname,lastname:lastname,nickname:nickname,client_id:lId,code:sCode])

		uc.session.regusercode = sCode
		uc.session.startchange = false
		uc.params.password1 = '123'
		uc.params.password2 = '123'
		def err = uc.passwsetup()
		assert err.inrequest.error==2
	}

	void testPasswsetupActionStartChangeTrue() {//Восстановление пароля, этап смены пароля. Начальная проверка кода восстановления. 
												//Результаты: ошибка №0, запись в таблице User со старым паролем
		def uc = new UserController()
		uc.requestService = requestServiceProxy

		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"

		def oNewUser = new User()
		def sCode=java.util.UUID.randomUUID().toString()
		def password2 = '12345'
		def firstname = "D"
		def lastname = "M"
		def nickname = firstname+" "+lastname
		def lId = 0
		oNewUser.csiInsertInternal([email:email,password:Tools.hidePsw(password2),firstname:firstname,lastname:lastname,nickname:nickname,client_id:lId,code:sCode])

		uc.session.regusercode = sCode
		uc.session.startchange = true
		def err = uc.passwsetup()
		assert err.inrequest.error==0
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////OLD TESTS///////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*	
	void testSetupAction() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		def lId = 0
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"
        def sCode=java.util.UUID.randomUUID().toString()          
        def oTempUser=new Tempusers([code:sCode,email:email, initdate:new Date(),client_id:lId])
        oTempUser.save(flush:true)
		uc.session.regusercode = sCode
		uc.session.startchange = false
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.setup()
		def x = -1
		assert uc.response.redirectedUrl.startsWith("/user/login")
		assertEquals x, hsRes.hinrequest.error
	}
*//*
	void testSetupActionBadRegusercode() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
        def sCode=java.util.UUID.randomUUID().toString()          
		uc.session.regusercode = sCode
		def hsRes = uc.setup()
		assertEquals "/user/restore", uc.response.redirectedUrl
	}
*//*
	void testSetupActionEmptyNickname() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		def lId = 0
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"
        def sCode=java.util.UUID.randomUUID().toString()          
        def oTempUser=new Tempusers([code:sCode,email:email, initdate:new Date(),client_id:lId])
        oTempUser.save(flush:true)
		uc.session.regusercode = sCode
		uc.session.startchange = false
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'qwerty'
		uc.params.nickname = ''
		def hsRes = uc.setup()
		assertEquals 4, hsRes.hinrequest.error
	}
*//*
	void testSetupActionNotEqualPass() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		def lId = 0
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"
        def sCode=java.util.UUID.randomUUID().toString()          
        def oTempUser=new Tempusers([code:sCode,email:email, initdate:new Date(),client_id:lId])
        oTempUser.save(flush:true)
		uc.session.regusercode = sCode
		uc.session.startchange = false
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwerty'
		uc.params.password2 = 'asdfgh'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.setup()
		assertEquals 1, hsRes.hinrequest.error
	}
*//*
	void testSetupActionTooSmallPass() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		def lId = 0
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"
        def sCode=java.util.UUID.randomUUID().toString()          
        def oTempUser=new Tempusers([code:sCode,email:email, initdate:new Date(),client_id:lId])
        oTempUser.save(flush:true)
		uc.session.regusercode = sCode
		uc.session.startchange = false
		uc.params.firstname = "D"
		uc.params.lastname = "M"
		uc.params.password1 = 'qwe'
		uc.params.password2 = 'qwe'
		uc.params.nickname = uc.params.firstname+" "+uc.params.lastname
		def hsRes = uc.setup()
		assertEquals 2, hsRes.hinrequest.error
	}
*//*
	void testSetupActionStartchangeTrue() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		def lId = 0
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		15.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"
        def sCode=java.util.UUID.randomUUID().toString()          
        def oTempUser=new Tempusers([code:sCode,email:email, initdate:new Date(),client_id:lId])
        oTempUser.save(flush:true)
		uc.session.regusercode = sCode		
		uc.session.startchange = true
		def hsRes = uc.setup()
		assertEquals 0, hsRes.hinrequest.error
	}
*//*
	void testRegAction() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		uc.jcaptchaService = jcaptchaService
		uc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return true }]
	
		SynchronizerToken token = new SynchronizerToken()
		uc.request.session.setAttribute(SynchronizerToken.KEY,token)
		uc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
		
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		10.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"
		uc.params.name = email
		def hsRes = uc.reg()
		assertEquals 0, hsRes.inrequest.error
	}
*//*
	void testRegActionInvalidCaptha() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
		uc.jcaptchaService = jcaptchaService
		uc.jcaptchaService = [validateResponse:{ def str, def id, def captcha -> return false }]
	
		SynchronizerToken token = new SynchronizerToken()
		uc.request.session.setAttribute(SynchronizerToken.KEY,token)
		uc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
		
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		10.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		def email = sb.toString()+"@test.ru"
		uc.params.name = email
		def hsRes = uc.reg()
		assertEquals 3, hsRes.inrequest.error
		assert uc.response.redirectedUrl.startsWith("/user/registration")
	}
*//*
	void testRegActionBadEmail() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
	
		SynchronizerToken token = new SynchronizerToken()
		uc.request.session.setAttribute(SynchronizerToken.KEY,token)
		uc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
		
		Random rand = new Random(System.currentTimeMillis())
		StringBuffer sb = new StringBuffer(100)
		sb.setLength(0)
		10.times {
			int pos = rand.nextInt(charsetForEmail.length())
			sb.append(charsetForEmail.charAt(pos))
		}
		uc.params.name = sb.toString()
		def hsRes = uc.reg()
		assertEquals 2, hsRes.inrequest.error
		assert uc.response.redirectedUrl.startsWith("/user/registration")
	}
*//*
	void testRegActionUserExist() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
	
		SynchronizerToken token = new SynchronizerToken()
		uc.request.session.setAttribute(SynchronizerToken.KEY,token)
		uc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
		
		uc.params.name = "futdrfausdjaxjl@test.ru"
		def hsRes = uc.reg()
		assertEquals 1, hsRes.inrequest.error
		assert uc.response.redirectedUrl.startsWith("/user/registration")
	}
*//*
	void testRegActionInvalidToken() {
		def uc = new UserController()
		uc.requestService = requestServiceProxy
	
		def hsRes = uc.reg()
		assertEquals 5, hsRes.inrequest.error
		assert uc.response.redirectedUrl.startsWith("/user/registration")
	}
*/
}