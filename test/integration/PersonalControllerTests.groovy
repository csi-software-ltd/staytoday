import grails.test.*
import javax.servlet.http.Cookie
import org.springframework.mock.web.MockMultipartFile 
import org.springframework.mock.web.MockMultipartHttpServletRequest
import java.awt.image.BufferedImage      
import javax.imageio.ImageIO as IIO 
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PersonalControllerTests extends GroovyTestCase {
    def TEST_MAIL='progatmp@mail.ru'
	Long TEST_HOME_ID=200

	def requestServiceProxy
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {

    }
	
	void testSaveadsAction() {//�������������� ����������. ������ ��������.
							  //����������: ��������� �������� ���������� ����� � ������ ������� Client. ��������� �������� ���� is_step_descr �� 1.
							  //			���������� ���������� ������� �����. ���������� ���� homeoption. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.name = 'Domik v lesu'
		pc.params.description = 'new description'
		pc.params.homeroom_id = 2 //must be in 1..5
		pc.params.homebath_id = 3 //must be in 1..4
		pc.params.hometype_id = 4 //must be in 1..8
		pc.params.homeclass_id = 1 //must be in 1..3
		pc.params.homeperson_id = 8 //must be in 1..16
		pc.params.bed = 3
		pc.params.area = 130
		pc.params.remarks = 'Some unique remarks' 
		pc.params.is_steam = 1
		pc.params.is_family = 1
		pc.params.is_swim = 1
		pc.params.is_wash = 1
		pc.params.is_visa = 1
		pc.params.is_kitchen = 1
		pc.params.homeoption_all0 = 1
		pc.params.homeoption_all4 = 1
		pc.params.homeoption_kitchen2 = 1
		pc.params.homeoption_kitchen1 = 1
		pc.params.homeoption_bania0 = 1
		pc.params.homeoption_kids1 = 1

		pc.saveads()
		def test = []
		def testHome = Home.findById(pc.params.id)
		assertEquals test, pc.flash.save_error
		assert testHome.is_step_descr == 1
	}

	void testSaveadsActionEmptyName() {//�������������� ����������. ������ ��������. ������ ���� "�������� ����������". ����������: ������ 1.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.name = ''
		pc.params.description = 'new description'
		pc.params.homeroom_id = 2 //must be in 1..5
		pc.params.homebath_id = 3 //must be in 1..4
		pc.params.hometype_id = 4 //must be in 1..8
		pc.params.homeclass_id = 1 //must be in 1..3
		pc.params.homeperson_id = 8 //must be in 1..16

		pc.saveads()
		def test = [1]
		assertEquals test, pc.flash.save_error
	}

	void testSaveadsActionEmptyDescription() {//�������������� ����������. ������ ��������. ������ ���� "��������". ����������: ������ 2.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.name = '....'
		pc.params.description = ''
		pc.params.homeroom_id = 2 //must be in 1..5
		pc.params.homebath_id = 3 //must be in 1..4
		pc.params.hometype_id = 4 //must be in 1..8
		pc.params.homeclass_id = 1 //must be in 1..3
		pc.params.homeperson_id = 8 //must be in 1..16

		pc.saveads()
		def test = [2]
		assertEquals test, pc.flash.save_error
	}

	void testSaveadsActionBadBed() {//�������������� ����������. ������ ��������. ������������ ������ � ���� "���������� ��������". ����������: ������ 3.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.name = '...'
		pc.params.description = 'new description'
		pc.params.bed = -5
		pc.params.area = 130		
		pc.params.homeroom_id = 2 //must be in 1..5
		pc.params.homebath_id = 3 //must be in 1..4
		pc.params.hometype_id = 4 //must be in 1..8
		pc.params.homeclass_id = 1 //must be in 1..3
		pc.params.homeperson_id = 8 //must be in 1..16

		pc.saveads()
		def test = [3]
		assertEquals test, pc.flash.save_error
	}

	void testSaveadsActionBadArea() {//�������������� ����������. ������ ��������. ������������ ������ � ���� "������� � �2". ����������: ������ 4.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.name = '...'
		pc.params.description = 'new description'
		pc.params.bed = 3
		pc.params.area = 100500
		pc.params.homeroom_id = 2 //must be in 1..5
		pc.params.homebath_id = 3 //must be in 1..4
		pc.params.hometype_id = 4 //must be in 1..8
		pc.params.homeclass_id = 1 //must be in 1..3
		pc.params.homeperson_id = 8 //must be in 1..16

		pc.saveads()
		def test = [4]
		assertEquals test, pc.flash.save_error
	}

	void testSaveadsActionBadReference () {//�������������� ����������. ������ ��������. ������ � ������������. ����������: ������ 10.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.name = '....'
		pc.params.description = 'new description'

		pc.saveads()
		def test = [10]
		assertEquals test, pc.flash.save_error
	}


	void testSavemapAction() {//�������������� ����������. ������ �����.
							  //����������: ��������� �������� ����� x, y, geostatus � ������ ������� Home. ������������� ����� city, street, homenumber, address.
							  //			 ��������� �������� ���� is_step_map �� 1. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.country_id = 1
		pc.params.region_id = 78
		pc.params.pindex = 789456
		pc.params.x = 3065548
		pc.params.y = 5984516
		pc.params.city = 'SpB'
		pc.params.street = 'Nevskiy pr.'
		pc.params.homenumber = '115'
		pc.params.district = 'Central'

		pc.savemap()
		def test = []
		def testHome = Home.findById(pc.params.id)
		assertEquals test, pc.flash.save_error
		assert pc.response.redirectedUrl.startsWith("/personal/map/190")
		assert testHome.is_step_map == 1
	}

	void testSavemapActionBadIndex() {//�������������� ����������. ������ �����. ������� ������������ �������� �������.
									  //����������: ������ �1
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.country_id = 1
		pc.params.region_id = 78
		pc.params.pindex = 'asdasd'
		pc.params.x = 3065548
		pc.params.y = 5984516
		pc.params.city = 'SpB'
		pc.params.street = 'Nevskiy pr.'
		pc.params.homenumber = '115'
		pc.params.district = 'Central'

		pc.savemap()
		def test = [1]
		assertEquals test, pc.flash.save_error
		assert pc.response.redirectedUrl.startsWith("/personal/map/190")
	}

/*	void testSavemapActionEmptyIndex() {//�������������� ����������. ������ �����. ����������� ���� ������.
										//����������: ������ �2
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.country_id = 1
		pc.params.region_id = 78
		pc.params.pindex = ''
		pc.params.x = 3065548
		pc.params.y = 5984516
		pc.params.city = 'SpB'
		pc.params.street = 'Nevskiy pr.'
		pc.params.homenumber = '115'
		pc.params.district = 'Central'

		pc.savemap()
		def test = [2]
		assertEquals test, pc.flash.save_error
		assert pc.response.redirectedUrl.startsWith("/personal/map/190")
	}*///DEPRECATED:Now index may be null

	void testSavemapActionNoMapLocation() {//�������������� ����������. ������ �����. ���������� �� ��������� � �����.
										   //����������: ������ �3
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.country_id = 1
		pc.params.region_id = 78
		pc.params.pindex = 789456
		pc.params.x = 0
		pc.params.y = 0
		pc.params.city = 'SpB'
		pc.params.street = 'Nevskiy pr.'
		pc.params.homenumber = '115'
		pc.params.district = 'Central'

		pc.savemap()
		def test = [3]
		assertEquals test, pc.flash.save_error
		assert pc.response.redirectedUrl.startsWith("/personal/map/190")
	}

	void testSavemapActionBadReference() {//�������������� ����������. ������ �����. ������ � ������������.
										  //����������: ������ �10
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.pindex = 789456
		pc.params.x = 3065548
		pc.params.y = 5984516
		pc.params.city = 'SpB'
		pc.params.street = 'Nevskiy pr.'
		pc.params.homenumber = '115'
		pc.params.district = 'Central'

		pc.savemap()
		def test = [10]
		assertEquals test, pc.flash.save_error
		assert pc.response.redirectedUrl.startsWith("/personal/map/190")
	}


	void testHomephotoAction() {//�������������� ����������. ������ ����, ��������������/���������� ����������.
								//����������: ��������� ����������� ������ ����������. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 190
		pc.params.id = 30

		def test = pc.homephoto()
		assert test.images['photo_1']== 'b7d34a75-69bc-45b2-8eef-12544f6c0a94.jpg'
	}

	void testSavepichomephotoAction() {//�������������� ����������. ������ ����, ��������������/���������� ����������. �������� ����������.
									   //����������: ���������� �������� ����. ���������� � �����. ��������� ����������� ������ ����� ����������. ���������� ������.
		def pc = new PersonalController()
		pc.metaClass.request = new MockMultipartHttpServletRequest()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		def imgContentType = 'image/jpeg'

		def contentStream = new FileInputStream("d:/dl/10.jpg")
		def imageMultipartFile = new MockMultipartFile("file1", "d:/dl/10.jpg", imgContentType, contentStream)
		pc.request.addFile(imageMultipartFile)
		
		pc.savepichomephoto()

		assertEquals "/personal/savepictureresult", pc.modelAndView.viewName 
		assertEquals 0,pc.modelAndView.model.data[0].error
		assert pc.modelAndView.model.data[0].filename != ''
		assert pc.modelAndView.model.data[0].thumbname != ''
	}

	void testHomephotoaddAction() {//�������������� ����������. ������ ����, ��������������/���������� ����������. ���������� ����������.
								   //����������: ������ � ������� Homephoto. ���������� home_id, modstatus, norder, is_main. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		def path = java.util.UUID.randomUUID().toString()+'.jpg'

		pc.params.home_id = 190
		pc.session['homephotopic0'] = [:]
		pc.session['homephotopic0']['file1'] = path

		pc.homephotoadd()
		def testHomephoto = Homephoto.findByHome_idAndPicture(pc.params.home_id, path)
		assertNotNull testHomephoto
		assert pc.response.redirectedUrl.startsWith("/personal/photo/190")
	}

	void testSet_main_photoAction() {//�������������� ����������. ������ ����, ������� ������� ����������.
								     //����������: ��������� � ������� Homephoto. � ���������� ���� is_main ����� 1. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		def path = java.util.UUID.randomUUID().toString()+'.jpg'

		pc.params.id = 30

		pc.set_main_photo()
		def testHomephoto = Homephoto.findById(pc.params.id)
		assertNotNull testHomephoto
		assert testHomephoto.is_main == 1
	}

	void testHomephotodeleteAction() {//�������������� ����������. ������ ����, �������� ����������.
								      //����������: ������ � ������� Picturetemp(��� ���������� � �� thumbnail). ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		def path = java.util.UUID.randomUUID().toString()+'.jpg'

	    def oHomephoto = new Homephoto()
		oHomephoto.home_id=190
		oHomephoto.norder=(Homephoto.findAllWhere(home_id:oHomephoto.home_id)?:[]).size()+1
		oHomephoto.is_main=0
		oHomephoto.picture=path
		oHomephoto.modstatus=1
		oHomephoto.moddate=new Date()
		oHomephoto.ptext=""
		oHomephoto.save(flush:true)

		def lId = Homephoto.findByHome_idAndPicture(oHomephoto.home_id, path)?.id

		pc.params.id = lId
		pc.params.home_id = 190

		pc.homephotodelete()
		def testHomephoto = Homephoto.findById(pc.params.id)
		def testPicturetemp = Picturetemp.findByFilename(path)
		assertNull testHomephoto
		assertNotNull testPicturetemp
	}

	void testSort_photoAction() {//�������������� ����������. ������ ����, ��������� ������� ����������.
								 //����������: ��������� �������� norder � ������� ������� Homephoto, ��������� � �������� �����������. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		def path = java.util.UUID.randomUUID().toString()+'.jpg'

		pc.params.ids = ['photo_30, photo_92, photo_465, photo_463, photo_467']

		pc.sort_photo()
		def testHomephoto = Homephoto.get(465)
		assertNotNull testHomephoto
		assert testHomephoto.norder == 3
	}

//owner Alex>>
    def init(){
	    def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:TEST_MAIL)
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
	    return pc
	}
	
/*	void testImage(){/*��������� ������ �������� � ��������������� ����� �����:
	                  1. ������������ ���� ������ �� ���� ������� ���������� ������� ��������
					  2. � ������������� ���� ��������� ��������������� ������� � ������������� _RO ���������� � ������ 1.4: 
					    2.1 ������������ ����� ���������� �� ����� �������� �� ����� ��� �� ������ ��������� -10<_RO/0.014-100<10
						2.2 ������������ ����� ���������� �� ����� �������� ����� ��� �� ������ ��������� -10<_RO/0.014-100<10
                      3.������������ ���� ������ �� 1 ������� ���������� ������� ��������:
					    3.1 ���������
						3.2 �����������
	                *//*
	  def sPathIn="c:/image/"
	  def baseDir = new File(sPathIn)
	  baseDir.eachFileMatch (~/.*.[(jpeg)(jpg)(png)(JPEG)(JPG)(PNG)]/) { file ->
	    testSavepichomephotoImage(file)
	  }
	}

	void testSavepichomephotoImage(file) {		
		def sPathOut="c:/img1/0/"										
        def pc=init()			       	         		
		  println('filename='+file.absolutePath+'>>>>>>>>>>>Start')		  
          pc.request.clearAttributes()	
          pc.request.removeAllParameters()		  
		  pc.response.committed = false
		  pc.response.reset()
		  pc.response.writer = null		  
		  pc.metaClass.request = new MockMultipartHttpServletRequest()
		  
		  def contentStream = new FileInputStream(file.absolutePath)		  		  
		  
		  def lsFilename=file.getName().toLowerCase().tokenize('.')
          
		  def imgContentType = ''
		  if(lsFilename[1]=='jpeg')
		    imgContentType='image/jpeg'
		  else if(lsFilename[1]=='jpg')
		    imgContentType='image/jpeg'
		  else if(lsFilename[1]=='png')
		    imgContentType='image/png'
		  
		  def imageMultipartFile = new MockMultipartFile("file1", file.absolutePath, imgContentType, contentStream)
		  pc.request.addFile(imageMultipartFile)
		
		  pc.savepichomephoto()
		//assertEquals "/personal/savepictureresult", pc.modelAndView.viewName 
		//assertEquals 0,pc.modelAndView.model.error
		

		  assert pc.modelAndView.model.filename != ''
		  assert pc.modelAndView.model.thumbname != ''
		  println('out:filename='+sPathOut+pc.modelAndView.model.filename)
		  println('out:thumbname='+sPathOut+pc.modelAndView.model.thumbname)
		
		  BufferedImage biPictureOrig = javax.imageio.ImageIO.read(new File(file.absolutePath))						
		  BufferedImage biPicture = javax.imageio.ImageIO.read(new File(sPathOut+pc.modelAndView.model.filename))
		  def iWidth=biPictureOrig.getWidth(null)
		  def iHeight=biPictureOrig.getHeight(null)
		  def bSizeEqual=false
		  if(iWidth<Tools.getIntVal(ConfigurationHolder.config.photo.image.size,639) 
		  && iHeight<Tools.getIntVal(ConfigurationHolder.config.photo.image.height,426)){
		    bSizeEqual=true
		  }else{
            def fProportion=iWidth/iHeight/0.014-100
            if(-10<fProportion&&fProportion<10){
		      println('-10<<10')
              assert biPicture.getWidth(null)<=Tools.getIntVal(ConfigurationHolder.config.photo.image.size,639)
              assert biPicture.getHeight(null)<=Tools.getIntVal(ConfigurationHolder.config.photo.image.height,426)
		    }else{
              println('!(-10<<10)')		
		      bSizeEqual=true		        
		    }
	      }
		  if(bSizeEqual){
		    assert biPicture.getWidth(null)==Tools.getIntVal(ConfigurationHolder.config.photo.image.size,639)
            assert biPicture.getHeight(null)==Tools.getIntVal(ConfigurationHolder.config.photo.image.height,426)
		  }
		  println('filename='+file.absolutePath+'<<<<<<<<<<<<END')		
	}*/
	
/*	void testChangeValutaAction(){/* ��� ������ ������ ������ ������ 
	                                 ���������� ���� pricestandard(_rub),
									 priceweekend(_rub), priceweek(_rub), 
									 proicemonth(_rub) � home � price(_rub), 
									 priceweekend(_rub) � homeprop �� ���������� 
									 �� ������� ������ � ����� �� ���������� ����� �����.
	                              *//*
	  def pc=init()
	  def iValutaId=978//can change valuta 
	  pc.params.home_id =TEST_HOME_ID
	  pc.params.valuta_id=iValutaId	  
	  
	  def oRates=new Valutarate()
      def rates=oRates.csiSearchCurrent(pc.params.valuta_id)
	  
	  if(rates||pc.params.valuta_id==pc.iRubId){
	    def hsRates
        def hsDim
        if(rates){
	      rates=rates[0]
		  hsRates=rates.vrate
          hsDim=rates.dim
		}      
		pc.changeValuta()
	    def oHome=Home.get(pc.params.home_id)
	    assert oHome.valuta_id==pc.params.valuta_id                                       
        def iPriceStandart=(oHome.pricestandard_rub)?((iValutaId==pc.iRubId)?oHome.pricestandard_rub:(Math.round(100*hsDim/hsRates*oHome.pricestandard_rub)/100).toInteger()):0		
	   	assert oHome.pricestandard==iPriceStandart
        def iPriceWeekend=(oHome.priceweekend_rub)?((iValutaId==pc.iRubId)?oHome.priceweekend_rub:(Math.round(100*hsDim/hsRates*oHome.pricesweekend_rub)/100).toInteger()):0		
	    assert oHome.priceweekend==iPriceWeekend
		def iPriceWeek=(oHome.priceweek_rub)?((iValutaId==pc.iRubId)?oHome.priceweek_rub:(Math.round(100*hsDim/hsRates*oHome.priceweek_rub)/100).toInteger()):0
	    assert oHome.priceweek_rub==iPriceWeek
		def iPriceMonth=(oHome.pricemonth_rub)?((iValutaId==pc.iRubId)?oHome.pricemonth_rub:(Math.round(100*hsDim/hsRates*oHome.pricemonth_rub)/100).toInteger()):0 
        assert oHome.pricemonth_rub==iPriceMonth	    
		def lsHomeprop=Homeprop.findAllWhere(home_id:TEST_HOME_ID)
		for(oHomeprop in lsHomeprop){
		  def iHomepropPriceRub=(oHomeprop.price_rub)?((iValutaId==pc.iRubId)?oHomeprop.price_rub:(Math.round(100*hsDim/hsRates*oHomeprop.price_rub)/100).toInteger()):0
		  assert oHomeprop.price==iHomepropPriceRub
		  def iHomepropPriceWeekend=(oHomeprop.priceweekend_rub)?((iValutaId==pc.iRubId)?oHomeprop.priceweekend_rub:(Math.round(100*hsDim/hsRates*oHomeprop.priceweek_rub)/100).toInteger()):0
		  assert oHomeprop.priceweekend==iHomepropPriceWeekend
		}
      }		
	}*/
	
//bad 
    void testHomepropaddAction() {//���������� ���������.		
		def pc=init()		
		pc.params.home_id = TEST_HOME_ID
		
		Random rand = new Random()
		
		def year =rand.nextInt(3000)
		if(year<2012) year+=2012//>>toDO flash.error<<7
	    def month =rand.nextInt(11)+1
		def day =rand.nextInt(15)+1    
		def period=rand.nextInt(30)+1		
        pc.params.active=rand.nextInt(2)
		pc.params.price=rand.nextInt(10000)	
		pc.params.priceweekend=rand.nextInt(10000)			
		pc.params.date_start_year = year
		pc.params.date_start_month = month
		pc.params.date_start_day = day
		pc.params.date_end_year = year
		pc.params.date_end_month = month
		pc.params.date_end_day = day+period
		pc.params.date_start = pc.params.date_start_year+pc.params.date_start_month+pc.params.date_start_day
		pc.params.date_end = pc.params.date_end_year+pc.params.date_end_month+pc.params.date_end_day
		
		def oHome=Home.get(pc.params.home_id)		      
	
		pc.homepropadd()
		
		def test = []				        		  
		
		
		def iValutaId=oHome.valuta_id
		if(pc.params.active){
		//rates>>
          def price=pc.params.price
          def priceweekend=pc.params.priceweekend				    			
		  def oRates=new Valutarate()
          def rates=oRates.csiSearchCurrent(iValutaId)
	  
          if(rates||iValutaId==pc.iRubId){
              def hsRates
              def hsDim
              if(rates){
	            rates=rates[0]
		        hsRates=rates.vrate
                hsDim=rates.dim		    
			  }  
			  def price_rub=(price)?((iValutaId==pc.iRubId)?price:(Math.round(100*hsRates/hsDim*price)/100).toInteger()):0	  			
	          def priceweekend_rub=(priceweekend)?((iValutaId==pc.iRubId)?priceweekend:(Math.round(100*hsRates/hsDim*priceweekend)/100).toInteger()):0			
			  def price_min=Tools.getIntVal(ConfigurationHolder.config.price_rub.min,300)
			  
			  if(price_rub<price_min)
                test<<5
		      if(priceweekend_rub<price_min)
                test<<6
			  println('testHomepropaddAction rates: iValutaId='+iValutaId+' , price='+price+' , priceweekend='+priceweekend+' , price_rub='+price_rub+' , priceweekend_rub='+priceweekend_rub)
	      }
		  //rates<< 		
		}
		assertEquals test, pc.flash.error
		pc.params.date_start_day = day+period-1
		pc.params.date_start = pc.params.date_start_year+pc.params.date_start_month+pc.params.date_start_day
		
		pc.request.clearAttributes()
		//pc.request.removeAllParameters()
		pc.response.committed = false
		pc.response.reset()
		pc.response.writer = null
		
		pc.homepropadd()										
		
		def oClient=new Client()
		def homepropId=oClient.csiGetLastInsert()
		println('homepropId='+homepropId)
		println('active='+pc.params.active)		
		def oHomeprop=Homeprop.get(homepropId)
		assert pc.params.home_id==oHomeprop?.home_id
		assert oHomeprop.modstatus==1
		
		if(pc.params.active &&!test){
		  assert oHomeprop.term==1
		  assert oHomeprop.price==pc.params.price
		  assert oHomeprop.priceweekend==pc.params.priceweekend
		  assert oHomeprop.valuta_id==iValutaId		  
		  assert oHome.is_step_price==1
		  }
		else
		  assert oHomeprop.term==0
		test = [4]
		assertEquals test, pc.flash.error		
	}	  
   /*  TODO 
	void testAddpriceActionMinPrice() {//�������������� ����������. ������ ����. ���� ��������� ������ ���������� ��������� � ������.
	    def pc=init()		
		pc.params.id = TEST_HOME_ID	
        def price_min=Tools.getIntVal(ConfigurationHolder.config.price_rub.min,300)	
        
		def oHome=Home.get(TEST_HOME_ID)
		def iValutaId=840//
		if(oHome.valuta_id!=840){
		  println('')
		  assert 1==0
		}
		
        def price=pc.params.price
        def priceweekend=pc.params.priceweekend				    			
		def oRates=new Valutarate()
        def rates=oRates.csiSearchCurrent(iValutaId)
	  
          if(rates||iValutaId==pc.iRubId){
              def hsRates
              def hsDim
              if(rates){
	            rates=rates[0]
		        hsRates=rates.vrate
                hsDim=rates.dim		    
			  }  
			  def price_rub=(price)?((iValutaId==pc.iRubId)?price:(Math.round(100*hsRates/hsDim*price)/100).toInteger()):0	  			
	          def priceweekend_rub=(priceweekend)?((iValutaId==pc.iRubId)?priceweekend:(Math.round(100*hsRates/hsDim*priceweekend)/100).toInteger()):0			
			  def price_min=Tools.getIntVal(ConfigurationHolder.config.price_rub.min,300)
			  
			  if(price_rub<price_min)
                test<<5
		      if(priceweekend_rub<price_min)
                test<<6
			  println('testHomepropaddAction rates: iValutaId='+iValutaId+' , price='+price+' , priceweekend='+priceweekend+' , price_rub='+price_rub+' , priceweekend_rub='+priceweekend_rub)
	      }		
		
		Math.round(100*hsDim[sCode]/hsRates[sCode]*dNum)/100
		
		pc.params.pricestatus = 1
		pc.params.status_only = 0
		pc.params.pricestandard = 20
		pc.params.priceweekend = 1700
		pc.params.priceweek = 10000
		pc.params.pricemonth = 9

		pc.addprice()
		def test = [5]
		assertEquals test, pc.flash.error
		
	}*/									
	//owner Alex<<

	void testAddpriceActionSaveBranch() {//�������������� ����������. ������ ����. ��������� ����������� ���������� ������/��������� ���������� ������.
										 //����������: ��������� �������� ����� valuta_id, pricestandard, priceweekend, priceweek, pricemonth � ������ ������� Home. 
										 //			   ��������� �������� ����� pricestatus, is_step_price �� 1. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.valuta_id = 857
		pc.params.pricestatus = 1
		pc.params.status_only = 0
		pc.params.pricestandard = 1500
		pc.params.priceweekend = 1700
		pc.params.priceweek = 10000
		pc.params.pricemonth = 30000

		pc.addprice()
		def test = []
		def testHome = Home.findById(pc.params.id)
		assertEquals test, pc.flash.save_error
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/190")
		assert testHome.is_step_price == 1
		assert testHome.pricestatus == 1
	}

	void testAddpriceActionTuneBranch() {//�������������� ����������. ������ ����. ��������� ���������� ������.
										 //����������: ��������� �������� ���� pricestatus �� 2. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.pricestatus = 2
		pc.params.status_only = 1

		pc.addprice()
		def test = []
		def testHome = Home.findById(pc.params.id)
		assertEquals test, pc.flash.save_error
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/190")
		assert testHome.pricestatus == 2
	}

	void testAddpriceActionResetBranch() {//�������������� ����������. ������ ����. ������ ���������� ������. �������� ������������� ������ ���.
										  //����������: ��������� �������� ����� is_step_price, pricestatus �� 0. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.pricestatus = 0
		pc.params.status_only = 1

		pc.addprice()
		def test = []
		def testHome = Home.findById(pc.params.id)
		assert pc.flash.save_error==[1]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/190")
		assert testHome.is_step_price == 0
		assert testHome.pricestatus == 0
	}

	void testAddpriceActionEmptyPricestandard() {//�������������� ����������. ������ ����. ����������� ���� "���������"
												 //����������: ������ �1
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.valuta_id = 857
		pc.params.pricestatus = 1
		pc.params.status_only = 0
		pc.params.pricestandard = ""
		pc.params.priceweekend = 1700
		pc.params.priceweek = 10000
		pc.params.pricemonth = 30000

		pc.addprice()
		def test = [1]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/190")
	}

	void testAddpriceActionBadPricestandard() {//�������������� ����������. ������ ����. ���������� ���� ������ ���������� ���������.
											   //����������: ������ �2
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.valuta_id = 857
		pc.params.pricestatus = 1
		pc.params.status_only = 0
		pc.params.pricestandard = 9
		pc.params.priceweekend = 1700
		pc.params.priceweek = 10000
		pc.params.pricemonth = 30000

		pc.addprice()
		def test = [2]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/190")
	}

	void testAddpriceActionBadPriceweekend() {//�������������� ����������. ������ ����. ���� �� �������� ������ ���������� ���������.
											  //����������: ������ �3
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.valuta_id = 857
		pc.params.pricestatus = 1
		pc.params.status_only = 0
		pc.params.pricestandard = 1500
		pc.params.priceweekend = 9
		pc.params.priceweek = 10000
		pc.params.pricemonth = 30000

		pc.addprice()
		def test = [3]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/190")
	}

	void testAddpriceActionBadPriceweek() {//�������������� ����������. ������ ����. ����������� ���� ������ ���������� ���������.
										   //����������: ������ �4
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.valuta_id = 857
		pc.params.pricestatus = 1
		pc.params.status_only = 0
		pc.params.pricestandard = 1500
		pc.params.priceweekend = 1700
		pc.params.priceweek = 9
		pc.params.pricemonth = 30000

		pc.addprice()
		def test = [4]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/190")
	}

	void testAddpriceActionBadPricemonth() {//�������������� ����������. ������ ����. ���������� ���� ������ ���������� ���������.
											//����������: ������ �5
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.valuta_id = 857
		pc.params.pricestatus = 1
		pc.params.status_only = 0
		pc.params.pricestandard = 1500
		pc.params.priceweekend = 1700
		pc.params.priceweek = 10000
		pc.params.pricemonth = 9

		pc.addprice()
		def test = [5]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/190")
	}


	void testHomeruleAction() {//�������������� ����������. ������ �������������� �������.
							   //����������: ��������� �������� ����� rule_minday_id, rule_maxday_id, rule_timein_id, rule_timeout_id � ������ ������� Home. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 190
		pc.params.rule_minday_id = 5
		pc.params.rule_maxday_id = 5
		pc.params.rule_timein_id = 4
		pc.params.rule_timeout_id = 2
		pc.params.homerule = "bla bla bla"

		pc.homerule()
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/190")
		def test = Home.get(pc.params.id)
		assert test.rule_minday_id == 5
		assert test.rule_maxday_id == 5
		assert test.rule_timein_id == 4
		assert test.rule_timeout_id == 2
		assert test.homerule == "bla bla bla"
	}


	void testHomeprop_deleteAction() {//�������������� ����������. ������ ����. �������� Homeprop. �������� ���������� � ������������� ������ ���.
									  //����������: ��������� �������� ����� is_step_price �� 0. ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		Random rand = new Random(System.currentTimeMillis())
	    def oHomeprop = new Homeprop()
		oHomeprop.home_id = 140
		oHomeprop.modstatus = 1
		def date = new Date()
	    oHomeprop.date_start = date
	    oHomeprop.date_end = date
	    oHomeprop.term = rand.nextInt(2)
		oHomeprop.save(flush:true)
		
		pc.params.id = oHomeprop.id

		pc.homeprop_delete()
		def test = [2]
		def testHome = Home.findById(oHomeprop.home_id)
		assertEquals test, pc.flash.save_error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/140")
		assert testHome.is_step_price == 0
	}


	void testHomepropaddActionInactiveBranch() {//�������������� ����������. ������ ���������� ������������� ������. ���������� ������� ������������.
												//����������: ������ � ������� Homeprop ( term = 0 ). ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 139 as long
		pc.params.active = 0
		pc.params.date_start_year = "2014"
		pc.params.date_start_month = "02"
		pc.params.date_start_day = "15"
		pc.params.date_end_year = "2014"
		pc.params.date_end_month = "03"
		pc.params.date_end_day = "02"
		pc.params.date_start = pc.params.date_start_year+'-'+pc.params.date_start_month+'-'+pc.params.date_start_day
		pc.params.date_end = pc.params.date_end_year+'-'+pc.params.date_end_month+'-'+pc.params.date_end_day

	    def date_start=Date.parse('yyyy-MM-dd', pc.params.date_start)
	    def date_end=Date.parse('yyyy-MM-dd', pc.params.date_end)
		def hmP = Homeprop.findAll("FROM Homeprop WHERE ((date_start<=:date_start AND date_end>=:date_start) OR (date_start<=:date_end AND date_end>=:date_end) OR (date_start>=:date_start AND date_end<=:date_end)) AND home_id=:home_id",[date_start:date_start,date_end:date_end,home_id:pc.params.home_id])
		for (h in hmP)
			h.delete(flush:true)

		pc.homepropadd()
		def test = []
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/139")
	}

	void testHomepropaddActionActiveBranch() {//�������������� ����������. ������ ���������� ������������� ������. ���������� ������� ������.
												//����������: ������ � ������� Homeprop( term = 1 ).  ���������� ������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 139 as long
		pc.params.active = 1
		pc.params.date_start_year = "2014"
		pc.params.date_start_month = "04"
		pc.params.date_start_day = "15"
		pc.params.date_end_year = "2014"
		pc.params.date_end_month = "05"
		pc.params.date_end_day = "02"
		pc.params.date_start = pc.params.date_start_year+'-'+pc.params.date_start_month+'-'+pc.params.date_start_day
		pc.params.date_end = pc.params.date_end_year+'-'+pc.params.date_end_month+'-'+pc.params.date_end_day

	    def date_start=Date.parse('yyyy-MM-dd', pc.params.date_start)
	    def date_end=Date.parse('yyyy-MM-dd', pc.params.date_end)
		def hmP = Homeprop.findAll("FROM Homeprop WHERE ((date_start<=:date_start AND date_end>=:date_start) OR (date_start<=:date_end AND date_end>=:date_end) OR (date_start>=:date_start AND date_end<=:date_end)) AND home_id=:home_id",[date_start:date_start,date_end:date_end,home_id:pc.params.home_id])
		for (h in hmP)
			h.delete(flush:true)

		pc.params.valuta_id = 857
		pc.params.price = 2000
		pc.params.priceweekend = 2500

		pc.homepropadd()
		def test = []
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprice/139")
	}

	void testHomepropaddActionEmptyDateStart() {//�������������� ����������. ������ ���������� ������������� ������. ������ ���� ������.
												//����������: ������ �1.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 139 as long
		pc.params.active = 1
		pc.params.date_start_year = ""
		pc.params.date_start_month = ""
		pc.params.date_start_day = ""
		pc.params.date_end_year = "2014"
		pc.params.date_end_month = "03"
		pc.params.date_end_day = "02"
		pc.params.date_start = ""
		pc.params.date_end = pc.params.date_end_year+'-'+pc.params.date_end_month+'-'+pc.params.date_end_day

		pc.params.valuta_id = 857
		pc.params.price = 2000
		pc.params.priceweekend = 2500

		pc.homepropadd()
		def test = [1,50,7]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprop")
	}

	void testHomepropaddActionEmptyDateEnd() {//�������������� ����������. ������ ���������� ������������� ������. ������ ���� ���������.
											  //����������: ������ �2.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 139 as long
		pc.params.active = 1
		pc.params.date_start_year = "2014"
		pc.params.date_start_month = "02"
		pc.params.date_start_day = "15"
		pc.params.date_end_year = ""
		pc.params.date_end_month = ""
		pc.params.date_end_day = ""
		pc.params.date_start = pc.params.date_start_year+'-'+pc.params.date_start_month+'-'+pc.params.date_start_day
		pc.params.date_end = ""

		pc.params.valuta_id = 857
		pc.params.price = 2000
		pc.params.priceweekend = 2500

		pc.homepropadd()
		def test = [2,50,7]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprop")
	}

	void testHomepropaddActionBadDate() {//�������������� ����������. ������ ���������� ������������� ������. ���� ��������� ������ ���� ������.
										 //����������: ������ �3.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 139 as long
		pc.params.active = 1
		pc.params.date_start_year = "2014"
		pc.params.date_start_month = "02"
		pc.params.date_start_day = "15"
		pc.params.date_end_year = "2014"
		pc.params.date_end_month = "02"
		pc.params.date_end_day = "14"
		pc.params.date_start = pc.params.date_start_year+'-'+pc.params.date_start_month+'-'+pc.params.date_start_day
		pc.params.date_end = pc.params.date_end_year+'-'+pc.params.date_end_month+'-'+pc.params.date_end_day

		pc.params.valuta_id = 857
		pc.params.price = 2000
		pc.params.priceweekend = 2500

		pc.homepropadd()
		def test = [3]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprop")
	}

	void testHomepropaddActionBadHome() {//�������������� ����������. ������ ���������� ������������� ������. ������������ home_id
										 //����������: ������ �4.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 100500 as long
		pc.params.active = 1
		pc.params.date_start_year = "2014"
		pc.params.date_start_month = "02"
		pc.params.date_start_day = "15"
		pc.params.date_end_year = "2014"
		pc.params.date_end_month = "03"
		pc.params.date_end_day = "02"
		pc.params.date_start = pc.params.date_start_year+'-'+pc.params.date_start_month+'-'+pc.params.date_start_day
		pc.params.date_end = pc.params.date_end_year+'-'+pc.params.date_end_month+'-'+pc.params.date_end_day

		pc.params.valuta_id = 857
		pc.params.price = 2000
		pc.params.priceweekend = 2500

		pc.homepropadd()
		def test = [4]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprop")
	}

	void testHomepropaddActionBadPrice() {//�������������� ����������. ������ ���������� ������������� ������. ���������� ���� ������� ������.
										  //����������: ������ �5.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 139 as long
		pc.params.active = 1
		pc.params.date_start_year = "2014"
		pc.params.date_start_month = "06"
		pc.params.date_start_day = "15"
		pc.params.date_end_year = "2014"
		pc.params.date_end_month = "07"
		pc.params.date_end_day = "02"
		pc.params.date_start = pc.params.date_start_year+'-'+pc.params.date_start_month+'-'+pc.params.date_start_day
		pc.params.date_end = pc.params.date_end_year+'-'+pc.params.date_end_month+'-'+pc.params.date_end_day

		pc.params.valuta_id = 857
		pc.params.price = 9
		pc.params.priceweekend = 2500

		pc.homepropadd()
		def test = [5]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprop")
	}

	void testHomepropaddActionBadPriceweekend() {//�������������� ����������. ������ ���������� ������������� ������. ���� � �������� ������� ������.
												 //����������: ������ �6.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 139 as long
		pc.params.active = 1
		pc.params.date_start_year = "2014"
		pc.params.date_start_month = "06"
		pc.params.date_start_day = "15"
		pc.params.date_end_year = "2014"
		pc.params.date_end_month = "07"
		pc.params.date_end_day = "02"
		pc.params.date_start = pc.params.date_start_year+'-'+pc.params.date_start_month+'-'+pc.params.date_start_day
		pc.params.date_end = pc.params.date_end_year+'-'+pc.params.date_end_month+'-'+pc.params.date_end_day

		pc.params.valuta_id = 857
		pc.params.price = 2000
		pc.params.priceweekend = 9

		pc.homepropadd()
		def test = [6]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprop")
	}

	void testHomepropaddActionBadDateEnd() {//�������������� ����������. ������ ���������� ������������� ������. ���� ��������� �� ������� ����.
											//����������: ������ �7.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'tesss@bk.ru')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.home_id = 139 as long
		pc.params.active = 1
		pc.params.date_start_year = "2011"
		pc.params.date_start_month = "02"
		pc.params.date_start_day = "15"
		pc.params.date_end_year = "2011"
		pc.params.date_end_month = "03"
		pc.params.date_end_day = "02"
		pc.params.date_start = pc.params.date_start_year+'-'+pc.params.date_start_month+'-'+pc.params.date_start_day
		pc.params.date_end = pc.params.date_end_year+'-'+pc.params.date_end_month+'-'+pc.params.date_end_day

		pc.params.valuta_id = 857
		pc.params.price = 2000
		pc.params.priceweekend = 2500

		pc.homepropadd()
		def test = [7]
		assertEquals test, pc.flash.error
		assert pc.response.redirectedUrl.startsWith("/personal/homeprop")
	}

	void testPromotesaveAction() {//�������������� ����������. ������ �����������.
								  //����������: ���������� ������. ���������� ���������.
		def pc = new PersonalController()
		pc.requestService = requestServiceProxy
		
		def hsDbUser=User.findWhere(name:'aaa@aa.qw')
		
		def oSession=new Usession()
		def sGuid=oSession.createSession(hsDbUser.id)	
		def COOKIENAME = 'user'
		def oCookie = new Cookie(COOKIENAME, sGuid)
	    oCookie.path = '/'	
		oCookie.maxAge = -1 //108000// 30 days
		pc.response.addCookie(oCookie)
		pc.request.setCookies(oCookie)
		
		pc.params.id = 767
		pc.params.linkname = "testHome_100500"
		
		def oHome = Home.get(pc.params.id)
		oHome.linkname = "must_change"
		oHome.save(flush:true)

		pc.promotesave()
		assertEquals "testhome_100500", Home.get(pc.params.id).linkname
		assert pc.response.redirectedUrl.startsWith("/personal/promote")
	}

}