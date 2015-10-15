import grails.test.*

class AministratorsControllerTests extends GroovyTestCase {

	def requestServiceProxy

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {

    }

	void testLoginAction() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.login = "dima"
		ac.params.password = '77777'
		ac.login()
		assertNull ac.flash.error
		assert ac.response.redirectedUrl.startsWith("/administrators/profile")
	}

	void testProfileSaveAction() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.name = "Dmitry"
		ac.params.email = 'tesssss@gmail.com'
		ac.session.admin = [:]
		ac.session.admin.id = 2
		ac.session.admin.menu = new Adminmenu()
		ac.session.admin.menu.id = 1
		ac.profilesave()
		assertNull ac.flash.error
		assertEquals "/administrators/profile", ac.response.getRedirectedUrl()
	}

	void testChangepassAction() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.pass = "77777"
		ac.params.confirm_pass = '77777'
		ac.session.admin = [:]
		ac.session.admin.id = 2
		ac.session.admin.menu = new Adminmenu()
		ac.session.admin.menu.id = 1
		ac.changepass()
		assertEquals 0, ac.flash.error
		assertEquals "/administrators/profile", ac.response.getRedirectedUrl()
	}

	void testChangepassActionWhenPassIsNull() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.pass = ""
		ac.params.confirm_pass = '77777'
		ac.session.admin = [:]
		ac.session.admin.id = 2
		ac.session.admin.menu = new Adminmenu()
		ac.session.admin.menu.id = 1
		ac.changepass()
		assertEquals 1, ac.flash.error
		assertEquals "/administrators/profile", ac.response.getRedirectedUrl()
	}

	void testChangepassActionPassIsTooSmall() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.pass = "123"
		ac.params.confirm_pass = '123'
		ac.session.admin = [:]
		ac.session.admin.id = 2
		ac.session.admin.menu = new Adminmenu()
		ac.session.admin.menu.id = 1
		ac.changepass()
		assertEquals 3, ac.flash.error
		assertEquals "/administrators/profile", ac.response.getRedirectedUrl()
	}

	void testChangepassActionPassIsNotEqual() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.pass = "77777"
		ac.params.confirm_pass = '12345'
		ac.session.admin = [:]
		ac.session.admin.id = 2
		ac.session.admin.menu = new Adminmenu()
		ac.session.admin.menu.id = 1
		ac.changepass()
		assertEquals 2, ac.flash.error
		assertEquals "/administrators/profile", ac.response.getRedirectedUrl()
	}

	void testBannedAction() {
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.id = 30
		ac.params.'amp;banned' = 1
		ac.banned()
		def res = User.get(30)
		assertEquals 1, res.banned
	}


	void testToMainpageAction() {
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.id = 211
		ac.params.'amp;status' = 1
		def res = Home.get(211)
		res.is_mainpage = 0
		res.save(flush:true)
		
		ac.toMainpage()
		assertEquals 1, res.is_mainpage
	}

	void testModerateHomeActionConfirmBranch() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.session.admin = [:]
		ac.session.admin.id = 2
		
		def res = Home.get(351)
		res.is_confirmed = 0
		res.modstatus = -2
		res.save(flush:true)
		ac.params.id = res.id
		ac.params.country_id = res.country_id
		ac.params.region_id = res.region_id
		ac.params.x = res.x
		ac.params.y = res.y
		ac.params.comments = res.comments
		ac.params.name = res.name
		ac.params.description = res.description
		ac.params.remarks = res.remarks
		ac.params.mapkeywords = res.mapkeywords
		ac.params.shortaddress = res.shortaddress
		ac.params.owner_user_tel = '+7(921)3509648'
		ac.params.save = 1
		ac.params.confirm = 1
		
		ac.moderateHome()
		assert ac.flash.save_error==[]
		assertEquals 1, res.is_confirmed
		assertEquals 1, res.modstatus
	}

	void testModerateHomeActionDeclineBranch() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.session.admin = [:]
		ac.session.admin.id = 2
		
		def res = Home.get(351)
		res.is_confirmed = 0
		res.modstatus = -2
		res.save(flush:true)
		ac.params.id = res.id
		ac.params.country_id = res.country_id
		ac.params.region_id = res.region_id
		ac.params.x = res.x
		ac.params.y = res.y
		ac.params.comments = res.comments
		ac.params.name = res.name
		ac.params.description = res.description
		ac.params.remarks = res.remarks
		ac.params.mapkeywords = res.mapkeywords
		ac.params.shortaddress = res.shortaddress
		ac.params.owner_user_tel = '+7(921)3509648'
		ac.params.save = 1
		ac.params.decline = 1
		
		ac.moderateHome()
		assert ac.flash.save_error==[]
		assertEquals 1, res.is_confirmed
		assertEquals '-1' as int, res.modstatus
	}

	void testModerateHomeActionEmptyName() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.session.admin = [:]
		ac.session.admin.id = 2
		
		def res = Home.get(351)
		res.is_confirmed = 0
		res.modstatus = -2
		res.save(flush:true)
		ac.params.id = res.id
		ac.params.country_id = res.country_id
		ac.params.region_id = res.region_id
		ac.params.x = res.x
		ac.params.y = res.y
		ac.params.comments = res.comments
		ac.params.name = ''
		ac.params.description = res.description
		ac.params.remarks = res.remarks
		ac.params.mapkeywords = res.mapkeywords
		ac.params.shortaddress = res.shortaddress
		ac.params.save = 1
		ac.params.decline = 1
		
		ac.moderateHome()
		assert ac.flash.save_error==[1]
		assertEquals 0, res.is_confirmed
		assertEquals '-2' as int, res.modstatus
	}

	void testModerateHomeActionEmptyDescription() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.session.admin = [:]
		ac.session.admin.id = 2
		
		def res = Home.get(351)
		res.is_confirmed = 0
		res.modstatus = -2
		res.save(flush:true)
		ac.params.id = res.id
		ac.params.country_id = res.country_id
		ac.params.region_id = res.region_id
		ac.params.x = res.x
		ac.params.y = res.y
		ac.params.comments = res.comments
		ac.params.name = res.name
		ac.params.description = ''
		ac.params.remarks = res.remarks
		ac.params.mapkeywords = res.mapkeywords
		ac.params.shortaddress = res.shortaddress
		ac.params.save = 1
		ac.params.decline = 1
		
		ac.moderateHome()
		assert ac.flash.save_error==[2]
		assertEquals 0, res.is_confirmed
		assertEquals '-2' as int, res.modstatus
	}

	void testModerateHomeActionBadReference() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.session.admin = [:]
		ac.session.admin.id = 2
		
		def res = Home.get(351)
		res.is_confirmed = 0
		res.modstatus = -2
		res.save(flush:true)
		ac.params.id = res.id
		ac.params.x = res.x
		ac.params.y = res.y
		ac.params.comments = res.comments
		ac.params.name = res.name
		ac.params.description = res.description
		ac.params.remarks = res.remarks
		ac.params.mapkeywords = res.mapkeywords
		ac.params.shortaddress = res.shortaddress
		ac.params.save = 1
		ac.params.decline = 1
		
		ac.moderateHome()
		assert ac.flash.save_error==[10]
		assertEquals 0, res.is_confirmed
		assertEquals '-2' as int, res.modstatus
	}


	void testInfotexteditAction() {
	
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.session.admin = [:]
		ac.session.admin.id = 2
		
		def res = Infotext.get(18)
		ac.params.id = res.id
		ac.params.title = res.title
		ac.params.keywords = res.keywords
		ac.params.description = res.description
		ac.params.name = res.name
		ac.params.promotext1 = res.promotext1
		ac.params.promotext2 = res.promotext2
		ac.params.itext = res.itext
		ac.params.itext2 = res.itext2
		ac.params.itext3 = res.itext3
		ac.params.save = 1
		
		ac.infotextedit()
		assert ac.flash.save_error==[]
	}


	void testReviewsToMainpageAction() {
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.id = 22
		ac.params.'amp;status' = 1
		def res = Ucomment.get(ac.params.id)
		res.is_mainpage = 0
		res.save(flush:true)
		
		ac.reviewsToMainpage()
		assertEquals 1, res.is_mainpage
	}

	void testReviewsConfirmActionConfirmBranch() {
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.id = 22
		ac.params.'amp;status' = 1
		def res = Ucomment.get(ac.params.id)
		res.comstatus = -1
		res.save(flush:true)
		
		ac.reviewsConfirm()
		assertEquals 1, res.comstatus
	}

	void testReviewsConfirmActionDeclineBranch() {
		def ac = new AdministratorsController()
		ac.requestService = requestServiceProxy
		ac.params.id = 22
		ac.params.'amp;status' = -1
		def res = Ucomment.get(ac.params.id)
		res.comstatus = 0
		res.save(flush:true)
		
		ac.reviewsConfirm()
		assertEquals '-1' as int, res.comstatus
	}

}
