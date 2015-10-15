import grails.test.*
import java.util.Random

class StatisticTests extends GroovyTestCase {
	static transactional = false
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {

    }
	
/*	void testStatistic() {
		def sLang='ru' //TODO
		def sWeb='web' //TODO
		def iRefId = 6
		def page=''
		int page_ind
		Random rand = new Random(System.currentTimeMillis())
	100000.times{
		page_ind = (rand.nextInt().abs() % 6 + 1)
		switch (page_ind) {
			case 1: page = "index"; break;
			case 2: page = "pc"; break;
			case 3: page = "terms"; break;
			case 4: page = "homeaddnew"; break;
			case 5: page = "useraddnew"; break;
			default: page = "howto"; break;
		}

		def oOnlinelog = new Onlinelog() 
		oOnlinelog.userip = "192.168.0.100"
		oOnlinelog.reference = "test_stat"
		oOnlinelog.ref_id = iRefId
		oOnlinelog.page=page
		oOnlinelog.type=0
		oOnlinelog.reccount=0
		oOnlinelog.home_id=0
		oOnlinelog.keyword=''
		oOnlinelog.lang=sLang
		oOnlinelog.users_id=0
		oOnlinelog.site=sWeb
		oOnlinelog.prop=''
		
		oOnlinelog.useragent=''
		oOnlinelog.save(flush:true)
	}
	}*/
}
