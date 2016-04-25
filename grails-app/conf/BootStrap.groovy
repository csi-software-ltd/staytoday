class BootStrap {
	def transactionManager
  def init = { servletContext ->
  	transactionManager.setNestedTransactionAllowed(true)
  	//System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2")
  }
  def destroy = {
  }
}