import grails.test.*
import org.codehaus.groovy.grails.web.servlet.mvc.SynchronizerToken

class BlockIPTests extends GroovyTestCase {

	def requestServiceProxy
	def jcaptchaService
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }
    void testSomething() {

    }	

	void testAddhomeActionBlock() {
		def hc = new HomeController()

		hc.requestService = requestServiceProxy
		hc.jcaptchaService = jcaptchaService

        SynchronizerToken token = new SynchronizerToken()

		2000.times{
			hc.request.clearAttributes()
			hc.request.removeAllParameters()
			hc.response.committed = false
			hc.response.reset()
			token = new SynchronizerToken()
			hc.request.session.setAttribute(SynchronizerToken.KEY,token)
			hc.request.addParameter(SynchronizerToken.KEY,token.currentToken.toString())
			hc.addhome()
		}
		def test = [99, 11, 10, 1, 2, 5, 6]
		assertEquals test, hc.flash.error
	}

}
