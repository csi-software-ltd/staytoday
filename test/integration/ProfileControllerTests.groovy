import grails.test.*
import javax.servlet.http.Cookie
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ProfileControllerTests extends GroovyTestCase {

	def requestServiceProxy

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {

    }

	void testChangepassAction() {//Редактирование профиля. Смена пароля.
								 //Результаты: Изменение пароля. Отсутствие ошибок.
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.pass = 'qwerty'
		prc.params.pass2 = 'qwerty'

		prc.changepass()
		def test = []
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

	void testChangepassActionNotEqualPass() {//Редактирование профиля. Смена пароля. Пароли не идентичны.
											 //Результаты: Ошибка №1
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.pass = 'asdfg'
		prc.params.pass2 = 'qwerty'

		prc.changepass()
		def test = [1]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

	void testChangepassActionToEasyPass() {//Редактирование профиля. Смена пароля. Пароль слишком короткий.
										   //Результаты: Ошибка №2
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.pass = 'qwe'
		prc.params.pass2 = 'qwe'

		prc.changepass()
		def test = [2]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}


	void testSaveProfileAction() {//Редактирование профиля. Изменение данных.
								  //Результаты: Изменение значений нескольких полей строки таблицы User. Отсутствие ошибок.
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.firstname = 'test'
		prc.params.lastname = 'test'
		prc.params.description = 'some descr'
		prc.params.nickname = 'nickname'
		prc.params.ind = '7'
		prc.params.kod = '921'
		prc.params.telef = '350'

		prc.saveProfile()
		def test = []
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

	void testSaveProfileActionEmptyNickname() {//Редактирование профиля. Изменение данных. Пустое поле ник.
											   //Результаты: Ошибка №4
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)

		prc.params.firstname = 'test'
		prc.params.lastname = 'test'
		prc.params.description = 'some descr'
		prc.params.nickname = ''
		prc.params.ind = '7'
		prc.params.kod = '921'
		prc.params.telef = '350'

		prc.saveProfile()
		def test = [4]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

	void testSaveProfileActionBadEmail() {//Редактирование профиля. Изменение данных. Некорректный емейл.
										  //Результаты: Ошибка №3
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.get(80)
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.firstname = 'test'
		prc.params.lastname = 'test'
		prc.params.description = 'some descr'
		prc.params.nickname = 'nickname'
		prc.params.email = 'email'
		prc.params.ind = '7'
		prc.params.kod = '921'
		prc.params.telef = '350'

		prc.saveProfile()
		def test = [3]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

	void testSaveProfileActionEmailExist() {//Редактирование профиля. Изменение данных. Емейл уже занят.
											//Результаты: Ошибка №6
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.get(80)
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.firstname = 'test'
		prc.params.lastname = 'test'
		prc.params.description = 'some descr'
		prc.params.nickname = 'nickname'
		prc.params.email = 'tesss@bk.ru'
		prc.params.ind = '7'
		prc.params.kod = '921'
		prc.params.telef = '350'

		prc.saveProfile()
		def test = [6]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

	void testSaveProfileActionEmptyTel() {//Редактирование профиля. Изменение данных. Удаление существующего телефона.
										  //Результаты: Ошибка №9
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.firstname = 'test'
		prc.params.lastname = 'test'
		prc.params.description = 'some descr'
		prc.params.nickname = 'nickname'

		prc.saveProfile()
		def test = [9]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}
	
	void testSaveProfileActionEmptyTelPart() {//Редактирование профиля. Изменение данных. Не указана часть номера телефона.
											  //Результаты: Ошибка №7
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.firstname = 'test'
		prc.params.lastname = 'test'
		prc.params.description = 'some descr'
		prc.params.nickname = 'nickname'
		prc.params.kod = '921'
		prc.params.telef = '350'

		prc.saveProfile()
		def test = [7]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

	void testSaveProfileActionBadTelPart() {//Редактирование профиля. Изменение данных. Не корректно указана часть номера телефона.
											//Результаты: Ошибка №7
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.firstname = 'test'
		prc.params.lastname = 'test'
		prc.params.description = 'some descr'
		prc.params.nickname = 'nickname'
		prc.params.ind = 'sdf7'
		prc.params.kod = '921'
		prc.params.telef = '350'

		prc.saveProfile()
		def test = [7]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

	void testSaveProfileActionEmptyTel1Part() {//Редактирование профиля. Изменение данных. Не указана часть номера дополнительного телефона.
											   //Результаты: Ошибка №8
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.firstname = 'test'
		prc.params.lastname = 'test'
		prc.params.description = 'some descr'
		prc.params.nickname = 'nickname'
		prc.params.ind = '7'
		prc.params.kod = '921'
		prc.params.telef = '350'
		prc.params.kod1 = '921'
		prc.params.telef1 = '350'

		prc.saveProfile()
		def test = [8]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

	void testSaveProfileActionBadTel1Part() {//Редактирование профиля. Изменение данных. Не корректно указана часть номера дополнительного телефона.
											 //Результаты: Ошибка №8
		def prc = new ProfileController()
		prc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'vpeqysxhbbrwdmi@test.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		prc.response.addCookie(oCookie)
		prc.request.setCookies(oCookie)
		
		prc.params.firstname = 'test'
		prc.params.lastname = 'test'
		prc.params.description = 'some descr'
		prc.params.nickname = 'nickname'
		prc.params.ind = '7'
		prc.params.kod = '921'
		prc.params.telef = '350'
		prc.params.ind1 = 'sdd7'
		prc.params.kod1 = '921'
		prc.params.telef1 = '350'

		prc.saveProfile()
		def test = [8]
		assertEquals test, prc.flash.error
		assert prc.response.redirectedUrl.startsWith("/profile/edit")
	}

}
