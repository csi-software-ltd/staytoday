class ApiController {
  def requestService
  def apiService

  def beforeInterceptor = [action:this.&checkMethod]
  def static final api_methods = ['availability','booking','info']

  def checkMethod() {
    if (!api_methods.contains(params.id)){
      requestService.init(this)
      requestService.setStatus(405)
      render(contentType: 'text/xml', encoding: 'UTF-8', text:apiService.error())
      return false
    }
  }

	def book = {
    requestService.init(this)
    render(contentType: 'text/xml', encoding: 'UTF-8', text:apiService."${params.id}"(requestService))
	}
}