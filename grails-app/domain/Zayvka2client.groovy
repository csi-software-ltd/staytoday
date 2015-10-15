class Zayvka2client {

  static constraints = {
  }
  static mapping = {
    version false
  }
  Long id
  Long zayvka_id
  Long client_id
  Date todate
  Date inputdate = new Date()
  Integer modstatus
  Integer step

////////////////////////constructor//////////////////////////////////////////////////////
  Zayvka2client(){}
  Zayvka2client(lZId,lClId,dTodate,iStep){
	zayvka_id = lZId
	client_id = lClId
	todate = dTodate
	modstatus = 0
	step = iStep
  }
/////////////////////////////////////////////////////////////////////////////////////////
}
