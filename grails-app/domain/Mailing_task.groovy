class Mailing_task {    
  
  static constraints = {	
  }
  static mapping = {
    version false
  }
  Integer id
  Integer template_id
  String mtext
  Date date_start
  Date date_end
  Integer modstatus
  Integer ncount
/////////////////////////////////constructor//////////////////////////////////////////////////////////////////////
  Mailing_task(){}
  Mailing_task(itemplate_id,smtext){
	template_id = itemplate_id
	mtext = smtext
	date_start = new Date()
	date_end = new Date()
	modstatus = 0
	ncount = 0
  }
  
  Mailing_task(itemplate_id,smtext,bSave){
	template_id = itemplate_id
	mtext = smtext
	date_start = new Date()
	date_end = new Date()
	modstatus = 0
	ncount = 0
	this.save(flush:true)
  }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  void updateNcount(){
	this.ncount = this.ncount + 1
	this.save(flush:true)
  }

  void updateModstatusAndDate_end(){
	this.modstatus = 1
	this.date_end = new Date()
	this.save(flush:true)
  }

}
