//import org.codehaus.groovy.grails.commons.grailsApplication
import grails.converters.JSON
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
//import org.apache.http.impl.cookie.BasicClientCookie
import groovy.json.JsonSlurper
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod
import groovy.xml.MarkupBuilder

class SmsService {
  def grailsApplication
	static transactional = false

  def sendVerifySms(oUser) {
		if(getGate(oUser.tel))
			return sendVerifySmsUkraine(oUser)
		else
			return sendVerifySmsDefault(oUser)
  }

  def sendNoticeSMS(oUser,iRegion_id=78) {
		if (!oUser)
			throw new Exception ('User not specified')
		else if (!oUser.tel)
			throw new Exception ('User.tel not specified')
		else if (!iRegion_id)
			throw new Exception ('Region_id not specified')
		def curHour = (new Date()).getHours()
		if (Tools.getIntVal(grailsApplication.config.noticeSMS.daytime.start,8)>((curHour+(Region.get(iRegion_id)?.timediff?:0)+24)%24)||((curHour+(Region.get(iRegion_id)?.timediff?:0)+24)%24)>=Tools.getIntVal(grailsApplication.config.noticeSMS.daytime.end,22)) {
			try {
				def oDelayed = new DelayedSMS(user_id:oUser.id,region_id:iRegion_id,type:0)
				oDelayed.save(flush:true)
			}	catch(Exception e) {
				throw e
			}
		} else if(getGate(oUser.tel))
			return sendNoticeSmsUkraine(oUser)
		else
			return sendNoticeSmsDefault(oUser)
  }

  def sendPaymentSMS(oUser,iRegion_id=78) {
		if (!oUser)
			throw new Exception ('User not specified')
		else if (!oUser.tel)
			throw new Exception ('User.tel not specified')
		else if (!iRegion_id)
			throw new Exception ('Region_id not specified')
		def curHour = (new Date()).getHours()
		if (Tools.getIntVal(grailsApplication.config.noticeSMS.daytime.start,8)>curHour+(Region.get(iRegion_id)?.timediff?:0)||curHour+(Region.get(iRegion_id)?.timediff?:0)>=Tools.getIntVal(grailsApplication.config.noticeSMS.daytime.end,22)) {
			try {
				def oDelayed = new DelayedSMS(user_id:oUser.id,region_id:iRegion_id,type:1)
				oDelayed.save(flush:true)
			}	catch(Exception e) {
				throw e
			}
		} else if(getGate(oUser.tel))
			return sendPaymentSMSUkraine(oUser)
		else
			return sendPaymentSMSDefault(oUser)
  }

  def sendCancelBronSMS(oUser,iRegion_id=78) {
		if (!oUser)
			throw new Exception ('User not specified')
		else if (!oUser.tel)
			throw new Exception ('User.tel not specified')
		else if (!iRegion_id)
			throw new Exception ('Region_id not specified')
		def curHour = (new Date()).getHours()
		if (Tools.getIntVal(grailsApplication.config.noticeSMS.daytime.start,8)>curHour+(Region.get(iRegion_id)?.timediff?:0)||curHour+(Region.get(iRegion_id)?.timediff?:0)>=Tools.getIntVal(grailsApplication.config.noticeSMS.daytime.end,22)) {
			try {
				def oDelayed = new DelayedSMS(user_id:oUser.id,region_id:iRegion_id,type:2)
				oDelayed.save(flush:true)
			}	catch(Exception e) {
				throw e
			}
		} else if(getGate(oUser.tel))
			return sendCancelBronSmsUkraine(oUser)
		else
			return sendCancelBronSmsDefault(oUser)
  }

  def sendOfferSMS(oUser,iRegion_id=78) {
		if (!oUser)
			throw new Exception ('User not specified')
		else if (!oUser.tel)
			throw new Exception ('User.tel not specified')
		else if (!iRegion_id)
			throw new Exception ('Region_id not specified')
		def curHour = (new Date()).getHours()
		if (Tools.getIntVal(grailsApplication.config.noticeSMS.daytime.start,8)>curHour+(Region.get(iRegion_id)?.timediff?:0)||curHour+(Region.get(iRegion_id)?.timediff?:0)>=Tools.getIntVal(grailsApplication.config.noticeSMS.daytime.end,22)) {
			try {
				def oDelayed = new DelayedSMS(user_id:oUser.id,region_id:iRegion_id,type:3)
				oDelayed.save(flush:true)
			}	catch(Exception e) {
				throw e
			}
		} else if(getGate(oUser.tel))
			return sendOfferSMSUkraine(oUser)
		else
			return sendOfferSMSDefault(oUser)
  }

	def getGate(sTel) {
		if (sTel.replace('+','').replace('(','').replace(')','').startsWith('380') ||
				sTel.replace('+','').replace('(','').replace(')','').startsWith('375')) {
			return 1
		} else {
			return 0
		}
	}

  void sendAdminNoticeSMS(_oNotice) {
		def jSonBody = [:]
		//jSonBody.apikey = "59S9QNLO7I8521U9QZYG1P7C4A4OSO383378IL6O941B74D018Y7F3TY4045UGI4"
		//jSonBody.apikey = "9AWDMO2EM4XPHP5X51521Y386N642SUIN6SDUIEZL3QD864Y4CPB25N3A04NC0FN"
		jSonBody.apikey = (grailsApplication.config.SMSgate.apikey)?grailsApplication.config.SMSgate.apikey.trim():"XXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZXXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZ"
		jSonBody.send = []
		def sendBody = [:]
		sendBody.id = 100500
		sendBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
		if(sendBody.from.size()>11)
			sendBody.from = sendBody.from[0..10]
		sendBody.to = _oNotice.contact.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
		sendBody.text = _oNotice.message
		jSonBody.send << sendBody

		def http = new HTTPBuilder('http://smspilot.ru')
		http.request(POST, JSON) {
	  	uri.path = '/api2.php'
	  	uri.query = [json:(jSonBody as JSON)]
	  	headers.Accept = 'application/json'
	  	response.success = { resp, json ->
	  	}
	  	response.failure = { resp ->
	  	}
		}
  }

  def sendOfferSMSDefault(oUser) {
		def error = 0
		def th=new Thread()
    th.start{
    	synchronized(this) {
				Sms.withTransaction {
					def jSonBody = [:]
					//jSonBody.apikey = "59S9QNLO7I8521U9QZYG1P7C4A4OSO383378IL6O941B74D018Y7F3TY4045UGI4"
					jSonBody.apikey = (grailsApplication.config.SMSgate.apikey)?grailsApplication.config.SMSgate.apikey.trim():"XXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZXXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZ"
					jSonBody.send = []
					def sendBody = [:]
					sendBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
					if(sendBody.from.size()>11)
						sendBody.from = sendBody.from[0..10]
					sendBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
					sendBody.text = Email_template.findWhere(action:'#offerSMS')?.itext?.trim()?:'Владелец подтвердил ваш запрос на бронь. Бронируйте. Staytoday.ru'
					sendBody.id = getNewSMSid(oUser.id,oUser.tel,sendBody.text)
					jSonBody.send << sendBody

					def servId = ''
					def http = new HTTPBuilder('http://smspilot.ru')
					http.request(POST, JSON) {
				  	uri.path = '/api2.php'
				  	uri.query = [json:(jSonBody as JSON)]
				  	headers.Accept = 'application/json'
				  	response.success = { resp, json ->
							def tempResponse = json.text
							def parsedJSON = JSON.parse(tempResponse)
							if (parsedJSON){
					  		try{
									if (parsedJSON.send[0].error!='0'){
						  			error = (parsedJSON.send[0].error as int)
									}
									servId = parsedJSON.send[0]?.server_id?:''
					  		} catch (Exception e){
									try{
						  			if (parsedJSON.error.code!='0'){
											error = (parsedJSON.error.code as int)
						  			}
									} catch (Exception er){
						  			log.debug ('\nError parsing json sms gate response: '+er)
						  			error = 500
									}
					  		}
							} else {
					  		try {
									def parsedXML = new XmlSlurper().parseText(tempResponse)
									error = ((parsedXML.code[0]?:404).toString() as int)
					  		} catch (Exception e){
									log.debug ('\nError parsing xml sms gate response: '+e)
									error = 500
					  		}
							}
				  	}
				  	response.failure = { resp ->
							error = 404
				  	}
					}
					updateSmsStatus(sendBody.id,error,servId)
				}
			}
		}
		return error
  }

  def sendOfferSMSUkraine(oUser) {
		def error = 0
		def th=new Thread()
    th.start{
    	synchronized(this) {
    		Sms.withTransaction {
					def jSonBody = [:]
					jSonBody.version = 'http'
					jSonBody.login = (grailsApplication.config.SMSgateUkraine.login)?grailsApplication.config.SMSgateUkraine.login.trim():"79213509648"
					jSonBody.password = (grailsApplication.config.SMSgateUkraine.password)?grailsApplication.config.SMSgateUkraine.password.trim():"info2012ST"
					jSonBody.command = 'send'
					jSonBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
					if(jSonBody.from.size()>11)
						jSonBody.from = jSonBody.from[0..10]
					jSonBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
					jSonBody.message = Email_template.findWhere(action:'#offerSMS')?.itext?.trim()?:'Владелец подтвердил ваш запрос на бронь. Бронируйте. Staytoday.ru'

					def smsId = getNewSMSid(oUser.id,oUser.tel,jSonBody.message)

					def servId = ''

					def http = new HTTPBuilder('http://smsukraine.com.ua')
					http.request(POST, TEXT) {
				  	uri.path = '/api/http.php'
				  	uri.query = jSonBody
				  	response.success = { resp, reader ->
				  		def responseText = reader.text
				  		def responseParts = responseText.split(':')
				  		if(responseParts.size()>=2) {
				  			if (responseParts[0].toString().trim()=='id') {
				  				servId = responseParts[1].toString().replaceAll(/(\D)/,'').trim()
				  			} else {
									switch(responseParts[1].toString().trim()) {
										case 'Sender phone should contains only english letters, digits, dot, underscore, dash':
											error = 201;break;
										case 'Please enter valid receiver phone number':
											error = 213;break;
				  					case 'Please enter SMS text':
				  						error = 220;break;
				  					case 'Not enough money':
				  						error = 241;break;
				  					case 'Wrong login/password':
				  						error = 101;break;
				  					default:error = 400;break;
				  				}
				  			}
				  		} else {
				  			log.debug ('\nError in response from sms gate Ukraine')
				  			error = 500
				  		}
				  	}
				  	response.failure = { resp ->
							error = 404
				  	}
					}
					updateSmsStatus(smsId,error,servId)
				}
			}
		}
		return error
  }

  def sendPaymentSMSDefault(oUser) {
		def error = 0
		def th=new Thread()
    th.start{
    	synchronized(this) {
				Sms.withTransaction {
					def jSonBody = [:]
					//jSonBody.apikey = "59S9QNLO7I8521U9QZYG1P7C4A4OSO383378IL6O941B74D018Y7F3TY4045UGI4"
					jSonBody.apikey = (grailsApplication.config.SMSgate.apikey)?grailsApplication.config.SMSgate.apikey.trim():"XXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZXXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZ"
					jSonBody.send = []
					def sendBody = [:]
					sendBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
					if(sendBody.from.size()>11)
						sendBody.from = sendBody.from[0..10]
					sendBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
					sendBody.text = Email_template.findWhere(action:'#paymentSMS')?.itext?.trim()?:'По одному из Ваших объявлений поступила оплата за бронь. Staytoday.ru'
					sendBody.id = getNewSMSid(oUser.id,oUser.tel,sendBody.text)
					jSonBody.send << sendBody

					def servId = ''
					def http = new HTTPBuilder('http://smspilot.ru')
					http.request(POST, JSON) {
				  	uri.path = '/api2.php'
				  	uri.query = [json:(jSonBody as JSON)]
				  	headers.Accept = 'application/json'
				  	response.success = { resp, json ->
							def tempResponse = json.text
							def parsedJSON = JSON.parse(tempResponse)
							if (parsedJSON){
					  		try{
									if (parsedJSON.send[0].error!='0'){
						  			error = (parsedJSON.send[0].error as int)
									}
									servId = parsedJSON.send[0]?.server_id?:''
					  		} catch (Exception e){
									try{
						  			if (parsedJSON.error.code!='0'){
											error = (parsedJSON.error.code as int)
						  			}
									} catch (Exception er){
						  			log.debug ('\nError parsing json sms gate response: '+er)
						  			error = 500
									}
					  		}
							} else {
					  		try {
									def parsedXML = new XmlSlurper().parseText(tempResponse)
									error = ((parsedXML.code[0]?:404).toString() as int)
					  		} catch (Exception e){
									log.debug ('\nError parsing xml sms gate response: '+e)
									error = 500
					  		}
							}
				  	}
				  	response.failure = { resp ->
							error = 404
				  	}
					}
					updateSmsStatus(sendBody.id,error,servId)
				}
			}
		}
		return error
  }

  def sendPaymentSMSUkraine(oUser) {
		def error = 0
		def th=new Thread()
    th.start{
    	synchronized(this) {
    		Sms.withTransaction {
					def jSonBody = [:]
					jSonBody.version = 'http'
					jSonBody.login = (grailsApplication.config.SMSgateUkraine.login)?grailsApplication.config.SMSgateUkraine.login.trim():"79213509648"
					jSonBody.password = (grailsApplication.config.SMSgateUkraine.password)?grailsApplication.config.SMSgateUkraine.password.trim():"info2012ST"
					jSonBody.command = 'send'
					jSonBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
					if(jSonBody.from.size()>11)
						jSonBody.from = jSonBody.from[0..10]
					jSonBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
					jSonBody.message = Email_template.findWhere(action:'#paymentSMS')?.itext?.trim()?:'По одному из Ваших объявлений поступила оплата за бронь. Staytoday.ru'

					def smsId = getNewSMSid(oUser.id,oUser.tel,jSonBody.message)

					def servId = ''

					def http = new HTTPBuilder('http://smsukraine.com.ua')
					http.request(POST, TEXT) {
				  	uri.path = '/api/http.php'
				  	uri.query = jSonBody
				  	response.success = { resp, reader ->
				  		def responseText = reader.text
				  		def responseParts = responseText.split(':')
				  		if(responseParts.size()>=2) {
				  			if (responseParts[0].toString().trim()=='id') {
				  				servId = responseParts[1].toString().replaceAll(/(\D)/,'').trim()
				  			} else {
									switch(responseParts[1].toString().trim()) {
										case 'Sender phone should contains only english letters, digits, dot, underscore, dash':
											error = 201;break;
										case 'Please enter valid receiver phone number':
											error = 213;break;
				  					case 'Please enter SMS text':
				  						error = 220;break;
				  					case 'Not enough money':
				  						error = 241;break;
				  					case 'Wrong login/password':
				  						error = 101;break;
				  					default:error = 400;break;
				  				}
				  			}
				  		} else {
				  			log.debug ('\nError in response from sms gate Ukraine')
				  			error = 500
				  		}
				  	}
				  	response.failure = { resp ->
							error = 404
				  	}
					}
					updateSmsStatus(smsId,error,servId)
				}
			}
		}
		return error
  }

  def sendCancelBronSmsDefault(oUser) {
		def error = 0
		def th=new Thread()
    th.start{
    	synchronized(this) {
				Sms.withTransaction {
					def jSonBody = [:]
					//jSonBody.apikey = "59S9QNLO7I8521U9QZYG1P7C4A4OSO383378IL6O941B74D018Y7F3TY4045UGI4"
					jSonBody.apikey = (grailsApplication.config.SMSgate.apikey)?grailsApplication.config.SMSgate.apikey.trim():"XXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZXXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZ"
					jSonBody.send = []
					def sendBody = [:]
					sendBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
					if(sendBody.from.size()>11)
						sendBody.from = sendBody.from[0..10]
					sendBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
					sendBody.text = Email_template.findWhere(action:'#cancelBronSMS')?.itext?.trim()?:'По одному из Ваших объявлений гость отменил бронь. Staytoday.ru'
					sendBody.id = getNewSMSid(oUser.id,oUser.tel,sendBody.text)
					jSonBody.send << sendBody

					def servId = ''
					def http = new HTTPBuilder('http://smspilot.ru')
					http.request(POST, JSON) {
				  	uri.path = '/api2.php'
				  	uri.query = [json:(jSonBody as JSON)]
				  	headers.Accept = 'application/json'
				  	response.success = { resp, json ->
							def tempResponse = json.text
							def parsedJSON = JSON.parse(tempResponse)
							if (parsedJSON){
					  		try{
									if (parsedJSON.send[0].error!='0'){
						  			error = (parsedJSON.send[0].error as int)
									}
									servId = parsedJSON.send[0]?.server_id?:''
					  		} catch (Exception e){
									try{
						  			if (parsedJSON.error.code!='0'){
											error = (parsedJSON.error.code as int)
						  			}
									} catch (Exception er){
						  			log.debug ('\nError parsing json sms gate response: '+er)
						  			error = 500
									}
					  		}
							} else {
					  		try {
									def parsedXML = new XmlSlurper().parseText(tempResponse)
									error = ((parsedXML.code[0]?:404).toString() as int)
					  		} catch (Exception e){
									log.debug ('\nError parsing xml sms gate response: '+e)
									error = 500
					  		}
							}
				  	}
				  	response.failure = { resp ->
							error = 404
				  	}
					}
					updateSmsStatus(sendBody.id,error,servId)
				}
			}
		}
		return error
  }

  def sendCancelBronSmsUkraine(oUser) {
		def error = 0
		def th=new Thread()
    th.start{
    	synchronized(this) {
    		Sms.withTransaction {
					def jSonBody = [:]
					jSonBody.version = 'http'
					jSonBody.login = (grailsApplication.config.SMSgateUkraine.login)?grailsApplication.config.SMSgateUkraine.login.trim():"79213509648"
					jSonBody.password = (grailsApplication.config.SMSgateUkraine.password)?grailsApplication.config.SMSgateUkraine.password.trim():"info2012ST"
					jSonBody.command = 'send'
					jSonBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
					if(jSonBody.from.size()>11)
						jSonBody.from = jSonBody.from[0..10]
					jSonBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
					jSonBody.message = Email_template.findWhere(action:'#cancelBronSMS')?.itext?.trim()?:'По одному из Ваших объявлений гость отменил бронь. Staytoday.ru'

					def smsId = getNewSMSid(oUser.id,oUser.tel,jSonBody.message)

					def servId = ''

					def http = new HTTPBuilder('http://smsukraine.com.ua')
					http.request(POST, TEXT) {
				  	uri.path = '/api/http.php'
				  	uri.query = jSonBody
				  	response.success = { resp, reader ->
				  		def responseText = reader.text
				  		def responseParts = responseText.split(':')
				  		if(responseParts.size()>=2) {
				  			if (responseParts[0].toString().trim()=='id') {
				  				servId = responseParts[1].toString().replaceAll(/(\D)/,'').trim()
				  			} else {
									switch(responseParts[1].toString().trim()) {
										case 'Sender phone should contains only english letters, digits, dot, underscore, dash':
											error = 201;break;
										case 'Please enter valid receiver phone number':
											error = 213;break;
				  					case 'Please enter SMS text':
				  						error = 220;break;
				  					case 'Not enough money':
				  						error = 241;break;
				  					case 'Wrong login/password':
				  						error = 101;break;
				  					default:error = 400;break;
				  				}
				  			}
				  		} else {
				  			log.debug ('\nError in response from sms gate Ukraine')
				  			error = 500
				  		}
				  	}
				  	response.failure = { resp ->
							error = 404
				  	}
					}
					updateSmsStatus(smsId,error,servId)
				}
			}
		}
		return error
  }

  def sendNoticeSmsDefault(oUser) {
		def error = 0
		def th=new Thread()
    th.start{
    	synchronized(this) {
				Sms.withTransaction {
					def jSonBody = [:]
					//jSonBody.apikey = "59S9QNLO7I8521U9QZYG1P7C4A4OSO383378IL6O941B74D018Y7F3TY4045UGI4"
					jSonBody.apikey = (grailsApplication.config.SMSgate.apikey)?grailsApplication.config.SMSgate.apikey.trim():"XXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZXXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZ"
					jSonBody.send = []
					def sendBody = [:]
					sendBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
					if(sendBody.from.size()>11)
						sendBody.from = sendBody.from[0..10]
					sendBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
					sendBody.text = Email_template.findWhere(action:'#noticeSMS')?.itext?.trim()?:'По одному из Ваших объявлений поступил запрос на бронь. Staytoday.ru'
					sendBody.id = getNewSMSid(oUser.id,oUser.tel,sendBody.text)
					jSonBody.send << sendBody

					def servId = ''
					def http = new HTTPBuilder('http://smspilot.ru')
					http.request(POST, JSON) {
				  	uri.path = '/api2.php'
				  	uri.query = [json:(jSonBody as JSON)]
				  	headers.Accept = 'application/json'
				  	response.success = { resp, json ->
							def tempResponse = json.text
							def parsedJSON = JSON.parse(tempResponse)
							if (parsedJSON){
					  		try{
									if (parsedJSON.send[0].error!='0'){
						  			error = (parsedJSON.send[0].error as int)
									}
									servId = parsedJSON.send[0]?.server_id?:''
					  		} catch (Exception e){
									try{
						  			if (parsedJSON.error.code!='0'){
											error = (parsedJSON.error.code as int)
						  			}
									} catch (Exception er){
						  			log.debug ('\nError parsing json sms gate response: '+er)
						  			error = 500
									}
					  		}
							} else {
					  		try {
									def parsedXML = new XmlSlurper().parseText(tempResponse)
									error = ((parsedXML.code[0]?:404).toString() as int)
					  		} catch (Exception e){
									log.debug ('\nError parsing xml sms gate response: '+e)
									error = 500
					  		}
							}
				  	}
				  	response.failure = { resp ->
							error = 404
				  	}
					}
				}
			}
		}
		return error
  }

  def sendNoticeSmsUkraine(oUser) {
		def error = 0
		def th=new Thread()
    th.start{
    	synchronized(this) {
    		Sms.withTransaction {
					def jSonBody = [:]
					jSonBody.version = 'http'
					jSonBody.login = (grailsApplication.config.SMSgateUkraine.login)?grailsApplication.config.SMSgateUkraine.login.trim():"79213509648"
					jSonBody.password = (grailsApplication.config.SMSgateUkraine.password)?grailsApplication.config.SMSgateUkraine.password.trim():"info2012ST"
					jSonBody.command = 'send'
					jSonBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
					if(jSonBody.from.size()>11)
						jSonBody.from = jSonBody.from[0..10]
					jSonBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
					jSonBody.message = Email_template.findWhere(action:'#noticeSMS')?.itext?.trim()?:'По одному из Ваших объявлений поступил запрос на бронь. Staytoday.ru'

					def smsId = getNewSMSid(oUser.id,oUser.tel,jSonBody.message)

					def servId = ''

					def http = new HTTPBuilder('http://smsukraine.com.ua')
					http.request(POST, TEXT) {
				  	uri.path = '/api/http.php'
				  	uri.query = jSonBody
				  	response.success = { resp, reader ->
				  		def responseText = reader.text
				  		def responseParts = responseText.split(':')
				  		if(responseParts.size()>=2) {
				  			if (responseParts[0].toString().trim()=='id') {
				  				servId = responseParts[1].toString().replaceAll(/(\D)/,'').trim()
				  			} else {
									switch(responseParts[1].toString().trim()) {
										case 'Sender phone should contains only english letters, digits, dot, underscore, dash':
											error = 201;break;
										case 'Please enter valid receiver phone number':
											error = 213;break;
				  					case 'Please enter SMS text':
				  						error = 220;break;
				  					case 'Not enough money':
				  						error = 241;break;
				  					case 'Wrong login/password':
				  						error = 101;break;
				  					default:error = 400;break;
				  				}
				  			}
				  		} else {
				  			log.debug ('\nError in response from sms gate Ukraine')
				  			error = 500
				  		}
				  	}
				  	response.failure = { resp ->
							error = 404
				  	}
					}
					updateSmsStatus(smsId,error,servId)
				}
			}
		}
		return error
  }

  def sendVerifySmsDefault(oUser) {

		def jSonBody = [:]
		//jSonBody.apikey = "59S9QNLO7I8521U9QZYG1P7C4A4OSO383378IL6O941B74D018Y7F3TY4045UGI4"
		jSonBody.apikey = (grailsApplication.config.SMSgate.apikey)?grailsApplication.config.SMSgate.apikey.trim():"XXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZXXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZ"
		jSonBody.send = []
		def sendBody = [:]
		sendBody.id = getNewSMSid(oUser)
		sendBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
		if(sendBody.from.size()>11)
			sendBody.from = sendBody.from[0..10]
		sendBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
		sendBody.text = oUser.smscode
		jSonBody.send << sendBody

		def error = 0
		def servId = ''
		def http = new HTTPBuilder('http://smspilot.ru')
		http.request(POST, JSON) {
	  	uri.path = '/api2.php'
	  	uri.query = [json:(jSonBody as JSON)]
	  	headers.Accept = 'application/json'
	  	response.success = { resp, json ->
				def tempResponse = json.text
				def parsedJSON = JSON.parse(tempResponse)
				if (parsedJSON){
		  		try{
						if (parsedJSON.send[0].error!='0'){
			  			error = (parsedJSON.send[0].error as int)
						}
						servId = parsedJSON.send[0]?.server_id?:''
		  		} catch (Exception e){
						try{
			  			if (parsedJSON.error.code!='0'){
								error = (parsedJSON.error.code as int)
			  			}
						} catch (Exception er){
			  			log.debug ('\nError parsing json sms gate response: '+er)
			  			error = 500
						}
		  		}
				} else {
		  		try {
						def parsedXML = new XmlSlurper().parseText(tempResponse)
						error = ((parsedXML.code[0]?:404).toString() as int)
		  		} catch (Exception e){
						log.debug ('\nError parsing xml sms gate response: '+e)
						error = 500
		  		}
				}
	  	}
	  	response.failure = { resp ->
				error = 404
	  	}
		}
		updateSmsStatus(sendBody.id,error,servId)
		return error
  }

  def sendVerifySmsUkraine(oUser) {

		def jSonBody = [:]
		jSonBody.version = 'http'
		jSonBody.login = (grailsApplication.config.SMSgateUkraine.login)?grailsApplication.config.SMSgateUkraine.login.trim():"79213509648"
		jSonBody.password = (grailsApplication.config.SMSgateUkraine.password)?grailsApplication.config.SMSgateUkraine.password.trim():"info2012ST"
		jSonBody.command = 'send'
		jSonBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
		if(jSonBody.from.size()>11)
			jSonBody.from = jSonBody.from[0..10]
		jSonBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
		jSonBody.message = oUser.smscode

		def smsId = getNewSMSid(oUser)

		def error = 0
		def servId = ''

		def http = new HTTPBuilder('http://smsukraine.com.ua')
		http.request(POST, TEXT) {
	  	uri.path = '/api/http.php'
	  	uri.query = jSonBody
	  	response.success = { resp, reader ->
	  		def responseText = reader.text
	  		def responseParts = responseText.split(':')
	  		if(responseParts.size()>=2) {
	  			if (responseParts[0].toString().trim()=='id') {
	  				servId = responseParts[1].toString().replaceAll(/(\D)/,'').trim()
	  			} else {
						switch(responseParts[1].toString().trim()) {
							case 'Sender phone should contains only english letters, digits, dot, underscore, dash':
								error = 201;break;
							case 'Please enter valid receiver phone number':
								error = 213;break;
	  					case 'Please enter SMS text':
	  						error = 220;break;
	  					case 'Not enough money':
	  						error = 241;break;
	  					case 'Wrong login/password':
	  						error = 101;break;
	  					default:error = 400;break;
	  				}
	  			}
	  		} else {
	  			log.debug ('\nError in response from sms gate Ukraine')
	  			error = 500
	  		}
	  	}
	  	response.failure = { resp ->
				error = 404
	  	}
		}

		updateSmsStatus(smsId,error,servId)
		return error
  }

  def getNewSMSid(oUser) {
		def oSms = new Sms(oUser)
		if(!oSms.save(flush:true)) {
	  	log.debug(" Error on add Sms:")
	  	oSms.errors.each{log.debug(it)}
	  	return oUser.id
		}else{
	  	return oSms.id
		}
  }

  def getNewSMSid(lId, sTel, sSmscode) {
		def oSms = new Sms(lId, sTel, sSmscode)
		if(!oSms.save(flush:true)) {
	  	log.debug(" Error on add Sms:")
	  	oSms.errors.each{log.debug(it)}
	  	return lId
		}else{
	  	return oSms.id
		}
  }

  def updateSmsStatus(lId,iStatus,sServerId) {
		def oSms = Sms.get(lId)
		if(oSms)
	  	oSms.updateStatusAndServerId(iStatus,sServerId)
		else {
	  	log.debug ('\nError updating sms status')
		}
  }
  
  def smsMailerDefault(sQ,smsId,oTemplate) {

		def error = 0
		def servId = ''

		def jSonBody = [:]
		//jSonBody.apikey = "59S9QNLO7I8521U9QZYG1P7C4A4OSO383378IL6O941B74D018Y7F3TY4045UGI4"
		jSonBody.apikey = (grailsApplication.config.SMSgate.apikey)?grailsApplication.config.SMSgate.apikey.trim():"XXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZXXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZ"
		jSonBody.send = []
		def smsText = oTemplate.mtext.replace('[@CONTACT]',sQ.contact).replace('[@NAME]',(sQ.name?sQ.name:'пользователь'))
		def sendBody = [:]
		sendBody.id = smsId
		sendBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
		if(sendBody.from.size()>11)
			sendBody.from = sendBody.from[0..10]
		sendBody.to = sQ.contact.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
		sendBody.text = smsText
		jSonBody.send << sendBody

		def http = new HTTPBuilder('http://smspilot.ru')
		http.request(POST, JSON) {
			uri.path = '/api2.php'
			uri.query = [json:(jSonBody as JSON)]
			headers.Accept = 'application/json'
			response.success = { resp, json ->
				def tempResponse = json.text
				def parsedJSON = JSON.parse(tempResponse)
				if (parsedJSON){
					try{
						if (parsedJSON.send[0].error!='0'){
							error = (parsedJSON.send[0].error as int)
						}
						servId = parsedJSON.send[0]?.server_id?:''
					} catch (Exception e){
						try{
							if (parsedJSON.error.code!='0'){
								error = (parsedJSON.error.code as int)
							}
						} catch (Exception er){
							log.debug ('\nError parsing json sms gate response: '+er)
							error = 500
						}
					}
				} else {
					try {
						def parsedXML = new XmlSlurper().parseText(tempResponse)
						error = ((parsedXML.code[0]?:404).toString() as int)
					} catch (Exception e){
						log.debug ('\nError parsing xml sms gate response: '+e)
						error = 500
					}
				}
			}
			response.failure = { resp ->
				error = 404
			}
		}

		return [error:error,servId:servId]
  }

  def smsMailerUkraine(sQ,oTemplate) {

		def error = 0
		def servId = ''

		def jSonBody = [:]
		jSonBody.version = 'http'
		jSonBody.login = (grailsApplication.config.SMSgateUkraine.login)?grailsApplication.config.SMSgateUkraine.login.trim():"79213509648"
		jSonBody.password = (grailsApplication.config.SMSgateUkraine.password)?grailsApplication.config.SMSgateUkraine.password.trim():"info2012ST"
		jSonBody.command = 'send'
		jSonBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
		if(jSonBody.from.size()>11)
			jSonBody.from = jSonBody.from[0..10]
		jSonBody.to = sQ.contact.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
		jSonBody.message = oTemplate.mtext.replace('[@CONTACT]',sQ.contact).replace('[@NAME]',(sQ.name?sQ.name:'пользователь'))

		def http = new HTTPBuilder('http://smsukraine.com.ua')
		http.request(POST, TEXT) {
	  	uri.path = '/api/http.php'
	  	uri.query = jSonBody
	  	response.success = { resp, reader ->
	  		def responseText = reader.text
	  		def responseParts = responseText.split(':')
	  		if(responseParts.size()>=2) {
	  			if (responseParts[0].toString().trim()=='id') {
	  				servId = responseParts[1].toString().replaceAll(/(\D)/,'').trim()
	  			} else {
						switch(responseParts[1].toString().trim()) {
							case 'Sender phone should contains only english letters, digits, dot, underscore, dash':
								error = 201;break;
							case 'Please enter valid receiver phone number':
								error = 213;break;
	  					case 'Please enter SMS text':
	  						error = 220;break;
	  					case 'Not enough money':
	  						error = 241;break;
	  					case 'Wrong login/password':
	  						error = 101;break;
	  					default:error = 400;break;
	  				}
	  			}
	  		} else {
	  			log.debug ('\nError in response from sms gate Ukraine')
	  			error = 500
	  		}
	  	}
	  	response.failure = { resp ->
				error = 404
	  	}
		}

		return [error:error,servId:servId]
  }

  def smsMailer() {
		def smsTemplate = Mailing_template.findAllByType_idAndModstatus(2,1)
		smsTemplate.each {
			def oMailingTask = new Mailing_task(it.id,it.mtext)
			oMailingTask.save(flush:true)
			def smsQueue = Mailing_queue.findAllByTemplate_id(it.id)
			smsQueue.each { sQ ->
				def smsText = it.mtext.replace('[@CONTACT]',sQ.contact).replace('[@NAME]',(sQ.name?sQ.name:'пользователь'))
				def sendResult
				def smsId = getNewSMSid(sQ.id,sQ.contact,smsText)
  			if(getGate(sQ.contact))
					sendResult = smsMailerUkraine(sQ,it)
  			else
					sendResult = smsMailerDefault(sQ,smsId,it)
				updateSmsStatus(smsId,sendResult.error,sendResult.servId)
				oMailingTask.updateNcount()
				sQ.moveToArchive(sQ.id,((sendResult.error==0)?1:sendResult.error),oMailingTask.id)
				sQ.delete(flush:true)
			}
			oMailingTask.updateModstatusAndDate_end()
		}
	}

  def sendSingleSmsToUser(oUser,sText) {
		if(getGate(oUser.tel))
			return sendSingleSmsToUserUkraine(oUser,sText)
		else
			return sendSingleSmsToUserDefault(oUser,sText)
  }

  def sendSingleSmsToUserUkraine(oUser,sText) {

		def jSonBody = [:]
		jSonBody.version = 'http'
		jSonBody.login = (grailsApplication.config.SMSgateUkraine.login)?grailsApplication.config.SMSgateUkraine.login.trim():"79213509648"
		jSonBody.password = (grailsApplication.config.SMSgateUkraine.password)?grailsApplication.config.SMSgateUkraine.password.trim():"info2012ST"
		jSonBody.command = 'send'
		jSonBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
		if(jSonBody.from.size()>11)
			jSonBody.from = jSonBody.from[0..10]
		jSonBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
		jSonBody.message = sText

		def smsId = getNewSMSid(oUser.id,oUser.tel,sText)

		def error = 0
		def servId = ''

		def http = new HTTPBuilder('http://smsukraine.com.ua')
		http.request(POST, TEXT) {
	  	uri.path = '/api/http.php'
	  	uri.query = jSonBody
	  	response.success = { resp, reader ->
	  		def responseText = reader.text
	  		def responseParts = responseText.split(':')
	  		if(responseParts.size()>=2) {
	  			if (responseParts[0].toString().trim()=='id') {
	  				servId = responseParts[1].toString().replaceAll(/(\D)/,'').trim()
	  			} else {
						switch(responseParts[1].toString().trim()) {
							case 'Sender phone should contains only english letters, digits, dot, underscore, dash':
								error = 201;break;
							case 'Please enter valid receiver phone number':
								error = 213;break;
	  					case 'Please enter SMS text':
	  						error = 220;break;
	  					case 'Not enough money':
	  						error = 241;break;
	  					case 'Wrong login/password':
	  						error = 101;break;
	  					default:error = 400;break;
	  				}
	  			}
	  		} else {
	  			log.debug ('\nError in response from sms gate Ukraine')
	  			error = 500
	  		}
	  	}
	  	response.failure = { resp ->
				error = 404
	  	}
		}

		updateSmsStatus(smsId,error,servId)
		return error
  }

  def sendSingleSmsToUserDefault(oUser,sText) {

		def jSonBody = [:]
		//jSonBody.apikey = "59S9QNLO7I8521U9QZYG1P7C4A4OSO383378IL6O941B74D018Y7F3TY4045UGI4"
		jSonBody.apikey = (grailsApplication.config.SMSgate.apikey)?grailsApplication.config.SMSgate.apikey.trim():"XXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZXXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZ"
		jSonBody.send = []
		def sendBody = [:]
		sendBody.id = getNewSMSid(oUser.id,oUser.tel,sText)
		sendBody.from = ((grailsApplication.config.SMSgate.from)?grailsApplication.config.SMSgate.from:'staytoday.ru')
		if(sendBody.from.size()>11)
			sendBody.from = sendBody.from[0..10]
		sendBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
		sendBody.text = sText
		jSonBody.send << sendBody

		def error = 0
		def servId = ''
		def http = new HTTPBuilder('http://smspilot.ru')
		http.request(POST, JSON) {
			uri.path = '/api2.php'
			uri.query = [json:(jSonBody as JSON)]
			headers.Accept = 'application/json'
			response.success = { resp, json ->
				def tempResponse = json.text
				def parsedJSON = JSON.parse(tempResponse)
				if (parsedJSON){
					try{
						if (parsedJSON.send[0].error!='0'){
							error = (parsedJSON.send[0].error as int)
						}
						servId = parsedJSON.send[0]?.server_id?:''
					} catch (Exception e){
						try{
							if (parsedJSON.error.code!='0'){
								error = (parsedJSON.error.code as int)
							}
						} catch (Exception er){
							log.debug ('\nError parsing json sms gate response: '+er)
							error = 500
						}
					}
				} else {
					try {
						def parsedXML = new XmlSlurper().parseText(tempResponse)
						error = ((parsedXML.code[0]?:404).toString() as int)
					} catch (Exception e){
						log.debug ('\nError parsing xml sms gate response: '+e)
						error = 500
					}
				}
			}
			response.failure = { resp ->
				error = 404
			}
		}
		updateSmsStatus(sendBody.id,error,servId)
		return error
  }

  Integer payuIdn(_payorder) {

    def configParams = [
      secretKey:grailsApplication.config.payu.secretKey?grailsApplication.config.payu.secretKey.trim():'!F6[bz*5a6b2++Q3EA7@',
      merchant:grailsApplication.config.payu.merchant?grailsApplication.config.payu.merchant.trim():'staytodq'
    ]

		def curtime = String.format('%tF %<tT', new Date())
    def reqstr = ''
    reqstr += configParams.merchant.getBytes("UTF-8").length+configParams.merchant
    reqstr += _payorder.tranagr_id.getBytes("UTF-8").length+_payorder.tranagr_id
    reqstr += (_payorder.summa.toString()+'.00').getBytes("UTF-8").length+_payorder.summa.toString()+'.00'
    reqstr += 'RUB'.getBytes("UTF-8").length+'RUB'
    reqstr += curtime.getBytes("UTF-8").length+curtime
    def reqsignature = Tools.generateHmacMD5(reqstr,configParams.secretKey).encodeAsHex()

		def jSonBody = [:]
		jSonBody.MERCHANT = configParams.merchant
		jSonBody.ORDER_REF = _payorder.tranagr_id
		jSonBody.ORDER_AMOUNT = (_payorder.summa.toString()+'.00')
		jSonBody.ORDER_CURRENCY = 'RUB'
		jSonBody.IDN_DATE = curtime
		jSonBody.ORDER_HASH = reqsignature

		Integer error = 0

		def http = new HTTPBuilder('https://secure.payu.ru')
		http.request(POST, URLENC) {
			uri.path = '/order/idn.php'
			body = jSonBody
	  	response.success = { resp, reader ->
	  		def responseText = reader.collect{it.key}[0].replace('<EPAYMENT>','').replace('</EPAYMENT>','').split('\\|')
				def respstr = ''
				respstr += responseText[0].getBytes("UTF-8").length+responseText[0]
				respstr += responseText[1].getBytes("UTF-8").length+responseText[1]
				respstr += responseText[2].getBytes("UTF-8").length+responseText[2]
				respstr += responseText[3].getBytes("UTF-8").length+responseText[3]
				if (Tools.generateHmacMD5(respstr,configParams.secretKey).encodeAsHex()==responseText[4]){
					if (!(responseText[1].toInteger() in [1,7])) {
						error = 500
					}
				} else {
					error = 500
				}
	  	}
	  	response.failure = { resp ->
				error = 404
	  	}
		}

		return error
  }

  def paypal_SetExpressCheckout(hsRequest) {

		def jSonBody = [:]
		jSonBody.USER = hsRequest.configParams.user
		jSonBody.PWD = hsRequest.configParams.pwd
		jSonBody.SIGNATURE = hsRequest.configParams.signature
		jSonBody.METHOD = 'SetExpressCheckout'
		jSonBody.VERSION = '124.0'
		jSonBody.NOSHIPPING = '1'
		jSonBody.ALLOWNOTE = '0'
		jSonBody.PAYMENTREQUEST_0_PAYMENTACTION = 'SALE'
		jSonBody.PAYMENTREQUEST_0_AMT = hsRequest.purchaseamt
		jSonBody.PAYMENTREQUEST_0_CURRENCYCODE = 'RUB'
		jSonBody.PAYMENTREQUEST_0_DESC = hsRequest.orderdescription
		jSonBody.PAYMENTREQUEST_0_INVNUM = hsRequest.payorder.norder-'st'
		jSonBody.RETURNURL = hsRequest.configParams.retURL
		jSonBody.CANCELURL = hsRequest.configParams.cancURL

		def responseMap

		def http = hsRequest.configParams.testmode?new HTTPBuilder('https://api-3t.sandbox.paypal.com'):new HTTPBuilder('https://api-3t.paypal.com')

		http.request(GET) {
	  	uri.path = '/nvp'
	  	uri.query = jSonBody
	  	response.success = { resp, reader ->
	  		responseMap = reader.text.split('&').inject([:]) {map, kv -> def (key, value) = kv.split('=').toList(); map[key] = value != null ? URLDecoder.decode(value) : null; map }
	  		if (responseMap?.ACK!="Success") log.debug ('Error while using paypal API: paypal_SetExpressCheckout: '+responseMap)
	  	}
	  	response.failure = { resp ->
				responseMap = null
	  	}
		}

		return responseMap
  }

  def paypal_GetExpressCheckoutDetails(hsRequest) {

		def jSonBody = [:]
		jSonBody.USER = hsRequest.configParams.user
		jSonBody.PWD = hsRequest.configParams.pwd
		jSonBody.SIGNATURE = hsRequest.configParams.signature
		jSonBody.METHOD = 'GetExpressCheckoutDetails'
		jSonBody.VERSION = '124.0'
		jSonBody.TOKEN = hsRequest.inrequest.token

		def responseMap

		def http = hsRequest.configParams.testmode?new HTTPBuilder('https://api-3t.sandbox.paypal.com'):new HTTPBuilder('https://api-3t.paypal.com')
		http.request(GET) {
	  	uri.path = '/nvp'
	  	uri.query = jSonBody
	  	response.success = { resp, reader ->
	  		responseMap = reader.text.split('&').inject([:]) {map, kv -> def (key, value) = kv.split('=').toList(); map[key] = value != null ? URLDecoder.decode(value) : null; map }
	  		if (responseMap?.ACK!="Success") log.debug ('Error while using paypal API: paypal_GetExpressCheckoutDetails: '+responseMap)
	  	}
	  	response.failure = { resp ->
				responseMap = null
	  	}
		}

		return responseMap
  }

  def paypal_DoExpressCheckoutPayment(hsRequest) {

		def jSonBody = [:]
		jSonBody.USER = hsRequest.configParams.user
		jSonBody.PWD = hsRequest.configParams.pwd
		jSonBody.SIGNATURE = hsRequest.configParams.signature
		jSonBody.METHOD = 'DoExpressCheckoutPayment'
		jSonBody.VERSION = '124.0'
		jSonBody.PAYMENTREQUEST_0_PAYMENTACTION = 'SALE'
		jSonBody.PAYMENTREQUEST_0_AMT = hsRequest.paymentdetails.AMT
		jSonBody.PAYMENTREQUEST_0_CURRENCYCODE = 'RUB'
		jSonBody.PAYERID = hsRequest.paymentdetails.PAYERID
		jSonBody.TOKEN = hsRequest.paymentdetails.TOKEN

		def responseMap

		def http = hsRequest.configParams.testmode?new HTTPBuilder('https://api-3t.sandbox.paypal.com'):new HTTPBuilder('https://api-3t.paypal.com')
		http.request(GET) {
	  	uri.path = '/nvp'
	  	uri.query = jSonBody
	  	response.success = { resp, reader ->
	  		responseMap = reader.text.split('&').inject([:]) {map, kv -> def (key, value) = kv.split('=').toList(); map[key] = value != null ? URLDecoder.decode(value) : null; map }
	  		if (responseMap?.ACK!="Success") log.debug ('Error while using paypal API: paypal_DoExpressCheckoutPayment: '+responseMap)
	  	}
	  	response.failure = { resp ->
				responseMap = null
	  	}
		}

		return responseMap
  }

  def postXMLdata(sUrl,sPath,hsBody) {
  	def responseText
    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)
    xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
    xml.booking(){
    	hsBody.each{ key, value ->
    		"${key}""${value}"
    	}
  	}
		println writer.toString()
		def http = new HTTPBuilder(sUrl)
		http.request(POST, TEXT) {
			uri.path = sPath
		  body = writer.toString()
/*		  body = {
		  	booking {
		    	hsBody.each{ key, value ->
		    		"${key}""${value}"
		    	}
		  	}
		  }*/
/*	  	response.success = { resp, resp_xml ->
	  		responseText = resp_xml.success
	  	}
	  	response.failure = { resp, resp_xml ->
	  		println resp.status
				responseText = resp_xml.error
	  	}*/
	  	response.success = { resp, reader ->
	  		responseText = reader.text
	  	}
	  	response.failure = { resp, reader ->
	  		println resp.status
				responseText = reader.text
	  	}
		}
		println responseText
		return responseText
  }

  def postJSONdata(sUrl,sPath,hsBody) {

		def result

		def http = new HTTPBuilder(sUrl)
		http.request(POST, ContentType.JSON) {
			uri.path = sPath
			body = hsBody
	  	response.success = { resp, json ->
	  		if (json.success=='quote sent') result = 1
	  		else {
	  			result = 0
	  			log.debug ('Error while using API: postJSONdata: '+sUrl+sPath+' - '+hsBody)
	  			log.debug (json.status)
	  		}
	  	}
	  	response.failure = { resp ->
  			log.debug ('Error while using API: postJSONdata: '+sUrl+sPath+' - '+hsBody)
  			log.debug (resp.status)
				result = 0
	  	}
		}

		return result
  }

	def megaindexscan() {
		def http = new HTTPBuilder('http://api.megaindex.ru')
		def queryMap = [:]
		queryMap.user = 'tesss@bk.ru'
		queryMap.password = '1Gfhjkm12'
		queryMap.imp = '1'
		queryMap.results = '500'
		queryMap.show_title = '1'
		queryMap.show_text = '1'
		def results = []
		def regions = Semantics_fromcity.list()
		Semantics.findAllByType(1,[max:2]).each{ semanticQuery ->
			queryMap.request = semanticQuery.name
			regions.each{ region ->
				queryMap.lr = region.kod
				['scan_yandex_position':1,'scan_google_position':2].each{ engine ->
					http.request(GET, ContentType.JSON) {
						uri.path = '/'+ engine.key
						uri.query = queryMap
						response.success = { resp, json ->
							try {
								if (!json.status) {
									results << new Semantics_stat(semantics_id:semanticQuery.id,sengine:engine.value,fromcity:region.city_from).setData(json.data.find{it?.domain?.equalsIgnoreCase('staytoday.ru')}).save(flush:true)?.position?:0
								} else {
									log.debug ('Error parsing megaindex scan response: '+json.err_msg.toString())
									results << json.err_msg
								}
							}catch(Exception e) {
								log.debug ('\nError parsing megaindex scan response: '+e.toString())
							}
						}
						response.failure = { resp ->
						}
					}
				}
			}
		}

		return results
  }

  def parseST() {
	def aRets = []
	def http = new HTTPBuilder('http://staytoday.ru')
	def urls = Ztempsiteurl.list()//.url-'http://staytoday.ru'

	urls.each { siteurl ->
	  http.request(GET, ContentType.HTML) {
		uri.path = siteurl.url-'http://staytoday.ru'
		headers.'User-Agent' = 'MJ12bot'

		response.success = { resp, reader ->
		  aRets << reader.BODY.HEAD.collect{ [it.TITLE.text(),it.META.find{it.@name.text()=='keywords'}.@content.text(),it.META.find{it.@name.text()=='description'}.@content.text()] }
		  try {
			siteurl.title = reader.BODY.HEAD.TITLE.text()
			siteurl.keywords = reader.BODY.HEAD.META.find{it.@name.text()=='keywords'}.@content.text()
			siteurl.description = reader.BODY.HEAD.META.find{it.@name.text()=='description'}.@content.text()
			siteurl.save(flush:true)
		  }catch(Exception e) {}
		}
		response.failure = { resp ->
		}
	  }
	}

	/*aRets.each{//save>>
	  it.each{
		if(it){
		  def oO = new Ztempbndata()
		  oO.type = (((it.type?:0) as int)?:0).toString()
		  oO.comn = (((it.room?:0) as int)?:0).toString()
		  oO.adress = it.adr?:''
		  oO.price = (((it.price?:0) as int)?:0).toString()
		  oO.sub = it.sub?:''
		  oO.tel = it.tel?:''
		  oO.site = 'bn.ru'
		  oO.date = ''
		  oO.ids = it.ids?:''
		  oO.save(flush:true)
		}
	  }
	}*/

	return aRets
  }

  def parseBN() {
	def aRets = []
	def http = new HTTPBuilder('http://www.bn.ru')
	for (int offset = 0; offset<=100; offset+=50){
	  http.request(GET, ContentType.HTML) {
		uri.path = '/zap_ars.phtml'
		uri.query = [start:offset]
		headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		response.success = { resp, reader ->
		  aRets << reader.BODY.depthFirst().find { it.@class.text() == 'results'}.TR.collect{
			if(it.TD[0].INPUT.size()){
			  if (!it.TD[1].A.size()){
				def tmp = [:]
				tmp.type = it.TD[1].text().replaceAll(/(\d)/,'')?0:1
				tmp.room = it.TD[1].text().replaceAll(/(\D)/,'')
				tmp.adr = it.TD[2].A.text()
				tmp.ids = it.TD[2].A.@href.text().replaceAll(/(\D)/,'')
				tmp.price = it.TD[6].DIV.B.text()
				tmp.sub = it.TD[7].text()
				tmp.tel = it.TD[8].text()
				tmp
			  } else {
				def tmp = [:]
				tmp.type = 1
				tmp.room = ''
				tmp.adr = it.TD[1].A.text()
				tmp.ids = it.TD[1].A.@href.text().replaceAll(/(\D)/,'')
				tmp.price = it.TD[4].STRONG.text().replaceAll(/(\D)/,'')
				tmp.sub = it.TD[5].text()
				tmp.tel = it.TD[6].text()
				tmp
			  }
			}
		  }
		}
		response.failure = { resp ->
		}
	  }
	}

	/*aRets.each{//save>>
	  it.each{
		if(it){
		  def oO = new Ztempbndata()
		  oO.type = (((it.type?:0) as int)?:0).toString()
		  oO.comn = (((it.room?:0) as int)?:0).toString()
		  oO.adress = it.adr?:''
		  oO.price = (((it.price?:0) as int)?:0).toString()
		  oO.sub = it.sub?:''
		  oO.tel = it.tel?:''
		  oO.site = 'bn.ru'
		  oO.date = ''
		  oO.ids = it.ids?:''
		  oO.save(flush:true)
		}
	  }
	}*/

	return aRets
  }

  def parse3xn() {
	def aRets = []
	def http = new HTTPBuilder('http://www.3xn.ru')
	for (int npage = 1; npage<=17; npage++){
	  http.request(GET, ContentType.HTML) {
		uri.path = '/index.php'
		uri.query = [cat:10,type:1,page:npage]
		headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		response.success = { resp, reader ->
			aRets << reader.BODY.depthFirst().find { it.@class.text() == 'searchresult'}.TBODY.TR.collect{
				it.TD[2].A.@href.text().toString().replaceAll(/(\D)/,'').toInteger()
			}
		}
		response.failure = { resp ->
		}
	  }
	}

	def aResult = []
	/*http.parser.'text/html' = { resp ->
	  InputStreamReader isr = new InputStreamReader(resp.entity.content, "windows-1251")
	  new XmlSlurper(new org.cyberneko.html.parsers.SAXParser()).parse(isr)
	}//Переопределяем стандартные парсеры, потому что наглый сайт не возвращает данные в UTF-8 несмотря на явное требование в заголовке запроса.
	http.parser.'text/plain' = { resp ->
	  InputStreamReader isr = new InputStreamReader(resp.entity.content, "windows-1251")
	}*/
	for ( int i=0; i<aRets.size(); i++)
	  for ( int k=0; k<aRets[i].size(); k++)
		http.request(GET, ContentType.HTML) {
		  uri.path = '/index.php'
		  uri.query = [action:'view',id:aRets[i][k]]
		  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		  response.success = { resp, reader ->
 			aResult << reader.BODY.depthFirst().find { it.@id.text() == 'view'}.DIV[1].TABLE[0].collect{
			  def tmp = [:]
			  tmp.room = it.TR[7].TD[1].text()
			  tmp.adr = it.TR[8].TD[1].text()
			  tmp.price = it.TR[6].TD[1].B.text().replaceAll(/(\D)/,'')
			  tmp.sub = it.TR[2].TD[1].text()
			  tmp.tel = it.TR[3].TD[1].text()
			  tmp.date = it.TR[1].TD[1].text()
			  tmp.ids = aRets[i][k]
			  tmp
			}
		  }
		  response.failure = { resp ->
		  }
		}

	/*aResult.each{//save>>
	  it.each{
		if(it){
		  def oO = new Ztempbndata()
		  oO.type = '1'
		  oO.comn = (((it.room?:0) as int)?:0).toString()
		  oO.adress = it.adr?:''
		  oO.price = (((it.price?:0) as int)?:0).toString()
		  oO.sub = it.sub?:''
		  oO.tel = it.tel?:''
		  oO.site = '3xn.ru'
		  oO.date = it.date?:''
		  oO.ids = it.ids?:''
		  oO.save(flush:true)
		}
	  }
	}*/

	return aResult
  }

  def parse3xnKomn() {
	def aRets = []
	def http = new HTTPBuilder('http://www.3xn.ru')
	for (int npage = 1; npage<=5; npage++){
	  http.request(GET, ContentType.HTML) {
		uri.path = '/index.php'
		uri.query = [cat:8,type:1,page:npage]
		headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		response.success = { resp, reader ->
			aRets << reader.BODY.depthFirst().find { it.@class.text() == 'searchresult'}.TBODY.TR.collect{
				it.TD[2].A.@href.text().toString().replaceAll(/(\D)/,'').toInteger()
			}
		}
		response.failure = { resp ->
		}
	  }
	}

	def aResult = []
	/*http.parser.'text/html' = { resp ->
	  InputStreamReader isr = new InputStreamReader(resp.entity.content, "windows-1251")
	  new XmlSlurper(new org.cyberneko.html.parsers.SAXParser()).parse(isr)
	}//Переопределяем стандартные парсеры, потому что наглый сайт не возвращает данные в UTF-8 несмотря на явное требование в заголовке запроса.
	http.parser.'text/plain' = { resp ->
	  InputStreamReader isr = new InputStreamReader(resp.entity.content, "windows-1251")
	}*/
	for ( int i=0; i<aRets.size(); i++)
	  for ( int k=0; k<aRets[i].size(); k++)
		http.request(GET, ContentType.HTML) {
		  uri.path = '/index.php'
		  uri.query = [action:'view',id:aRets[i][k]]
		  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		  response.success = { resp, reader ->
 			aResult << reader.BODY.depthFirst().find { it.@id.text() == 'view'}.DIV[1].TABLE[0].collect{ 			
			  def tmp = [:]
			  tmp.room = it.TR[7].TD[1].text()
			  tmp.adr = it.TR[8].TD[1].text()
			  tmp.price = it.TR[6].TD[1].B.text().replaceAll(/(\D)/,'')
			  tmp.sub = it.TR[2].TD[1].text()
			  tmp.tel = it.TR[3].TD[1].text()
			  tmp.date = it.TR[1].TD[1].text()
			  tmp.ids = aRets[i][k]
			  tmp
			}
		  }
		  response.failure = { resp ->
		  }
		}

	/*aResult.each{//save>>
	  it.each{
		if(it){
		  def oO = new Ztempbndata()
		  oO.type = '0'
		  oO.comn = (((it.room?:0) as int)?:0).toString()
		  oO.adress = it.adr?:''
		  oO.price = (((it.price?:0) as int)?:0).toString()
		  oO.sub = it.sub?:''
		  oO.tel = it.tel?:''
		  oO.site = '3xn.ru'
		  oO.date = it.date?:''
		  oO.ids = it.ids?:''
		  oO.save(flush:true)
		}
	  }
	}*/

	return aResult
  }

  def parseCottage() {
	def aRets = []
	def http = new HTTPBuilder('http://spb.cottage.ru')
	for (int npage = 1; npage<=5; npage++){
	  http.request(GET, ContentType.HTML) {
		uri.path = '/objects/rent/'
		uri.query = [p:npage]
		headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		response.success = { resp, reader ->
			aRets << reader.BODY.depthFirst().findAll { it.@class.text() == 'title'}.collect{
				it.A.@href.text().toString().replaceAll(/(\D)/,'').toInteger()
			}
		}
		response.failure = { resp ->
		}
	  }
	}

	def aResult = []
	def newhttp = new HTTPBuilder('http://www.cottage.ru')

	for ( int i=0; i<aRets.size(); i++)
	  for ( int k=0; k<aRets[i].size(); k++)
		newhttp.request(GET, ContentType.HTML) {
		  uri.path = '/objects/'+aRets[i][k]+'.html'
		  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		  response.success = { resp, reader ->
			  def tmp = [:]
			  tmp.price = getLocalText(reader.BODY.depthFirst().find { it.@class.text() == 'price'}).split('до')[0].replaceAll(/(\D)/,'')
			  tmp.sub = getLocalText(reader.BODY.depthFirst().find { it.@id.text() == 'lot-info'})
			  tmp.tel = reader.BODY.depthFirst().find { it.@id.text() == 'lot-info'}.STRONG.text()
			  tmp.ids = aRets[i][k]
			  tmp.date = getLocalText(reader.BODY.depthFirst().find { it.@id.text() == 'lot-id'}).split('/')[2].split(':')[1].trim()
			  tmp.adr = getAdrForCottage(reader.BODY.depthFirst().find { it.@id.text() == 'obj_properties'})
			  aResult << tmp
		  }
		  response.failure = { resp ->
		  }
		}

	/*aResult.each{//save>>
		if(it){
		  def oO = new Ztempbndata()
		  oO.type = '2'
		  oO.comn = '0'
		  oO.adress = it.adr?:''
		  oO.price = (((it.price?:0) as int)?:0).toString()
		  oO.sub = it.sub?:''
		  oO.tel = it.tel?:''
		  oO.site = 'cottage.ru'
		  oO.date = it.date?:''
		  oO.ids = it.ids?:''
		  oO.save(flush:true)
		}
	}*/

	return aResult
  }

  def parseCottageMsk() {
	def aRets = []
	def http = new HTTPBuilder('http://www.cottage.ru')
	for (int npage = 1; npage<=7; npage++){
	  http.request(GET, ContentType.HTML) {
		uri.path = '/objects/rent/'
		uri.query = [p:npage]
		headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		response.success = { resp, reader ->
			aRets << reader.BODY.depthFirst().findAll { it.@class.text() == 'title'}.collect{
				it.A.@href.text().toString().replaceAll(/(\D)/,'').toInteger()
			}
		}
		response.failure = { resp ->
		}
	  }
	}

	def aResult = []
	def newhttp = new HTTPBuilder('http://www.cottage.ru')

	for ( int i=0; i<aRets.size(); i++)
	  for ( int k=0; k<aRets[i].size(); k++)
		newhttp.request(GET, ContentType.HTML) {
		  uri.path = '/objects/'+aRets[i][k]+'.html'
		  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		  response.success = { resp, reader ->
			  def tmp = [:]
			  tmp.price = getLocalText(reader.BODY.depthFirst().find { it.@class.text() == 'price'}).split('до')[0].replaceAll(/(\D)/,'')
			  tmp.sub = getLocalText(reader.BODY.depthFirst().find { it.@id.text() == 'lot-info'})
			  tmp.tel = reader.BODY.depthFirst().find { it.@id.text() == 'lot-info'}.STRONG.text()
			  tmp.ids = aRets[i][k]
			  tmp.date = getLocalText(reader.BODY.depthFirst().find { it.@id.text() == 'lot-id'}).split('/')[2].split(':')[1].trim()
			  tmp.adr = getAdrForCottage(reader.BODY.depthFirst().find { it.@id.text() == 'obj_properties'})
			  aResult << tmp
		  }
		  response.failure = { resp ->
		  }
		}

	/*aResult.each{//save>>
		if(it){
		  def oO = new Ztempbndata()
		  oO.type = '2'
		  oO.comn = '0'
		  oO.adress = it.adr?:''
		  oO.price = (((it.price?:0) as int)?:0).toString()
		  oO.sub = it.sub?:''
		  oO.tel = it.tel?:''
		  oO.site = 'cottage.ru'
		  oO.date = it.date?:''
		  oO.ids = it.ids?:''
		  oO.save(flush:true)
		}
	}*/

	return aResult
  }

  String getLocalText(def parent) {
    String all = parent.text()
    parent.children().each {
        all -= it.text()
    }
    return all
  }
  def gorkvartira() {		
	def http = new HTTPBuilder('http://gorkvartira.ru')							
	def total=0
	//def city=[name:'abakan']
	for(city in Gorkvartira_city.findAll('FROM Gorkvartira_city')){
	  def aRes = []
      total=0	
	  http.request(GET, ContentType.HTML) {
        uri.path = '/city/'+city.name		
	    headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

	    response.success = { resp, reader ->	
          reader.BODY.depthFirst().findAll { it.@style.text() == 'float:left'}.collect{				
		    total=it.SPAN.text().toString().trim().toInteger()				
		  }	  
	    }
	    response.failure = { resp ->
	    }	  	  
	  }	  
	  
	  if(total<15)
	    total=15
		
	  def npageMax=(total/15).toInteger()
	  
	  if((total%15)>0)
	    npageMax+=1	  
	  
	  for (int npage = 1; npage<=npageMax; npage++){
	    http.request(GET, ContentType.HTML) {
		  uri.path = '/city/'+city.name
		  uri.query = [page:npage]
		  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		  response.success = { resp, reader ->    
		    reader.BODY.depthFirst().findAll { it.@class.text() == 'phone1'}.collect{				
				def tmp = [:]				
				tmp.phone=it.text().toString().replaceAll(/[a-zA-Zа-яА-Я]/,'').trim()
				tmp.name=it.A.text().toString().trim()
				/*
				def bFlag=1
				for(item in aRes)
				  if(item.find{it.key == 'phone'&& it.value==tmp.phone}){
				    bFlag=0					
				  }
				if(bFlag)  
				  aRes<<tmp				
				  */
				aRes<<tmp
			}	 
		  }
		  response.failure = { resp ->
		  }
	    }	  
	  }
	
	  aRes.each{//save>>
		if(it){
		  def oO = new Ztempbndata()
		  
		  oO.type = ''
		  oO.comn = '0'
		  oO.adress = city.name
		  oO.price = 0
		  oO.sub = it.name?:''
		  oO.tel = it.phone?:''
		  oO.site = 'gorkvartira.ru'
		  oO.date = ''
		  oO.ids = ''
		  oO.save(flush:true)
		}
	  }
	}
  }	
  //////////////////////////////////////////////////////////////////////////////////
  def stay_ua() {		
    def http = new HTTPBuilder('http://lugansk.stay.ua/')							
    def total=0
    log.debug('stay_ua()')
	
    def aRes = []
    def lsLink=[]	
    http.request(GET, ContentType.HTML) {
        //uri.path = '/city/'+city.name		
      headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

      response.success = { resp, reader ->	
        reader.BODY.depthFirst().findAll { it.@class.text() == 'new_img'}.collect{				
          lsLink<<it.A.@href.text().toString().trim()
        }	  
      }
      response.failure = { resp ->
	}	  	  
    }
  
	  //def i=0
	  for (sLink in lsLink){
	    //if(i>0)return		
		log.debug(sLink)
	    http.request(GET, ContentType.HTML) {		  
		  uri.path = sLink
		  //uri.query = [page:npage]
		  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		  response.success = { resp, reader ->    
		    reader.BODY.depthFirst().findAll { it.@class.text() == 'owner'}.collect{	
              def tmp = [:]
              //tmp.phone=''			  
			  
			  //log.debug('688='+it)
			  it=it.toString().replaceAll(/\s+/,' ')
			  //log.debug('690='+it)
			  if(it.split('дополнительно:').size()>1){
			    tmp.additional=it.split('дополнительно:')[1]
				it=it.minus(tmp.additional)				
			  }
			  it=it.minus('дополнительно:')
			    
			  if(it.split('e-mail:').size()>1){
			    tmp.email=it.split('e-mail:')[1]				
				it=it.minus(tmp.email)
			  }	
			  it=it.minus('e-mail:')
			  
			  def lsPhone=it.split('тел:')
			  for(sPhone in lsPhone){
			    tmp.phone=sPhone
			  }
			  
			  for(def j=2;j<7;j++)
			    tmp.phone=tmp.phone.replace('тел'+j+':',' ')
			  tmp.phone=tmp.phone.trim()	
			  
			  tmp.person=tmp.phone.find(~/[A-Za-zА-Яа-я]+/)	
			  if(!tmp.person)
			    tmp.person=tmp.phone.find(~/^[A-Za-zА-Яа-я]+\s?[A-Za-zА-Яа-я]+$/)
			  if(!tmp.person)
			    tmp.person=tmp.phone.find(~/^[A-Za-zА-Яа-я]+,?[A-Za-zА-Яа-я]+$/)	
			  tmp.phone=tmp.phone.minus(tmp.person)			  			  
			  
		   log.debug(tmp)  			  			
				//aRes<<tmp
					 
		
		  def oO = new Ztempbndata()
		  
		  oO.type = ''
		  oO.comn = '0'
		  oO.adress = 'lugansk'
		  oO.price = 0
		  oO.sub = tmp?.person?:''
		  oO.tel = tmp?.phone?:''
		  oO.email = tmp?.email?:''
          //oO.addit=it?.additional?:''		  
		  oO.addit=''	
		  oO.site = 'stay.ua'
		  oO.date = ''
		  oO.ids = ''
		  oO.save(flush:true)
		
	  
			}	 
		  }
		  response.failure = { resp ->
		  }
	    }
        //i++
      }
	  
	/*
	  aRes.each{//save>>
		if(it){
		  def oO = new Ztempbndata()
		  
		  if((it?.additional?:'').size()>250)
		    it.additional=it.additional.substring(0,250)  
		  
		  oO.type = ''
		  oO.comn = '0'
		  oO.adress = 'kiev'
		  oO.price = 0
		  oO.sub = it?.person?:''
		  oO.tel = it?.phone?:''
		  oO.email = it?.email?:''
          //oO.addit=it?.additional?:''		  
		  oO.addit=''	
		  oO.site = 'stay.ua'
		  oO.date = ''
		  oO.ids = ''
		  oO.save(flush:true)
		}
	  }    	  */
  }
//////////////////////////////////////////////////////////////////////////////////
  def unibo() {		
    def http = new HTTPBuilder('http://www.unibo.ru')							
    log.debug('unibo() START:')	
		
    for (int npage = 1; npage<=86; npage++){
      def aRes = []
      def lsLink=[]
      http.request(GET, ContentType.HTML) {        
        uri.path = '/category/199/'
        uri.query = [page:npage]		
	headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

	response.success = { resp, reader ->	
	  //def i=0
          reader.BODY.depthFirst().findAll { it.@class.text() == 'item-list-name'}.collect{		           
            if(it.A.@href.text().toString().contains('http://www.unibo.ru')){			  
	      //i++
	      //if(i==1){
		lsLink<<it.A.@href.text().toString().trim()		      	  		      		  
	        //log.debug(lsLink)
	      //}		       
	    }
          }	              
	}
	response.failure = { resp ->
	}	  	  
      }    
	  
  //log.debug(lsLink)
  
    def i=0
    for (sLink in lsLink){
      //log.debug('full: '+sLink)
      sLink=sLink.minus('http://www.unibo.ru/')     
		
      http.request(GET, ContentType.HTML) {		  
	uri.path = sLink
	//uri.query = [id:sQuery]
	 headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'		  		  
		  
	response.success = { resp, reader ->    
          def tmp=[:]
	  reader.BODY.depthFirst().collect{ 
	    if(it.A.@href.text().contains('mailto'))
	      tmp.email=it.A.@href.text().toString().minus('mailto:')
	  }
          reader.BODY.depthFirst().findAll { it.@class.text() == 'value city'}.collect{				   
	    tmp.adress=it.text().toString()			    			 
	  }
	  reader.BODY.depthFirst().findAll { it.@class.text() == 'value firm'}.collect{				    	    
	    tmp.sub=it.text().toString()			    			 
	  }
	  aRes<<tmp
	}		              				 			  		  		  	      
        response.failure = { resp ->}    
      }
      i++
    }	 
//log.debug('aRes='+aRes)
    aRes.each{//save>>
      if(it){
	def oO = new Ztempbndata()	 
		  
	oO.type = ''
	oO.comn = '0'
	oO.adress = it?.adress?:''
	oO.price = 0
	oO.sub = it?.sub?:''
        oO.tel = ''
        oO.email = it?.email?:''
        oO.addit=''		  	
        oO.site = 'unibo.ru'
        oO.date = ''
	oO.ids = ''
	oO.save(flush:true)
      }
    } 
  } 
    log.debug('unibo() FINISH:')	
  }
//////////////////////////////////////////////////////////////////////////////////
  def torent() {		
    def http = new HTTPBuilder('http://to-rent.ru')							
    log.debug('to-rent() START:')	
		
    for (int npage = 4; npage<=8; npage++){
      def aRes = []      
      http.request(GET, ContentType.HTML) {        
        uri.path = '/index.php/'

        uri.query = [page:npage,type:1,town_id:1,min_rent:0,rent:10000,rent_type:2]		
	headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

	response.success = { resp, reader ->	
	  //def i=0
          reader.BODY.depthFirst().findAll { it.@class.text() == 'cl' && it.@width.text() == '105'}.collect{		           
            def tmp=[:]		  
	      /*i++
	      if(i==1){*/
		tmp.sub=it.text().toString().trim().split(',')[0]
		if(it.text().toString().trim().split(',').size()>1)
                  tmp.tel=it.text().toString().trim().split(',')[1]
                aRes<<tmp		
	      //}	              	      
	    }
          }	              	
	response.failure = { resp ->}	  	  
      }              	   
    
//log.debug('aRes='+aRes)
    aRes.each{//save>>
      if(it){
	def oO = new Ztempbndata()	 
		  
	oO.type = ''
	oO.comn = '0'
	oO.adress = ''
	oO.price = 0
	oO.sub = it?.sub?:''
        oO.tel = it?.tel?:''
        oO.email = ''
        oO.addit=''		  	
        oO.site = 'to-rent.ru'
        oO.date = ''
	oO.ids = ''
	oO.save(flush:true)
      }
    } 
  }
    log.debug('to-rent() FINISH:')	
  }  
//////////////////////////////////////////////////////////////////////////////////
  def tospb() {		
	def http = new HTTPBuilder('http://www.2spb.ru')							
	log.debug('tospb() START:')	
		
	for (int npage = 0; npage<=1200; npage+=50){
	  def aRes = []
      def lsLink=[]
	  http.request(GET, ContentType.HTML) {        
        uri.path = '/index.php'
		uri.query = [index:npage]		
	    headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

	    response.success = { resp, reader ->	
          reader.BODY.depthFirst().findAll { it.@class.text() == 'tah11'}.collect{	
            def item=it.DIV		  
		    if(item.A.@href.text().toString().contains('http://www.2spb.ru'))			  
			  lsLink<<item.A.@href.text().toString().trim()
			  
		  }	  
	    }
	    response.failure = { resp ->
	    }	  	  
	  }
	  
  //log.debug(lsLink)
	  def i=0
	  for (sLink in lsLink){
	  //log.debug('full: '+sLink)
	    sLink=sLink.minus('http://www.2spb.ru/')
		def sQuery=sLink.split('id=')[1]
		sLink=sLink.split('id')[0]
		sLink=sLink.minus('?')
		
	    //if(i>4)continue		
		//log.debug(sLink)
		
	    http.request(GET, ContentType.HTML) {		  
		  uri.path = sLink
		  uri.query = [id:sQuery]
		  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'		  		  
		  
		  response.success = { resp, reader ->    
            def tmp=[:]
			reader.BODY.depthFirst().collect{ 
			  if(it.A.@href.text().contains('mailto'))
			    tmp.email=it.A.@href.text().toString().minus('mailto:')
			}

			reader.BODY.depthFirst().findAll { it.@class.text() == 'ar13'}.collect{			
			  def sName=''
			  if(it.text().toString().contains('Сдает:') && it.text().toString().contains('(')){
			    sName=it.text().toString().split(/\(/)[0]
			    sName=sName.minus('Сдает:')
			    //log.debug(sName)
			    tmp.name=sName
			  }
			}
            tmp.addit=sQuery			
		    aRes<<tmp					 			  		  		  	 
		  }
		  response.failure = { resp ->
		  }
	    }
        i++
      }	 
//log.debug('aRes='+aRes)
      aRes.each{//save>>
		if(it){
		  def oO = new Ztempbndata()	 
		  
		  oO.type = ''
		  oO.comn = '0'
		  oO.adress = 'spb'
		  oO.price = 0
		  oO.sub = it?.name?:''
		  oO.tel = it?.phone?:''
		  oO.email = it?.email?:''
      oO.addit=it?.addit?:''		  	
		  oO.site = '2spb.ru'
		  oO.date = ''
		  oO.ids = ''
		  oO.save(flush:true)
		}
	  }
	}  
	log.debug('tospb() FINISH:')	
  }	  
  /*
  def parseHelp17() {
	def aRets = []
	def http = new HTTPBuilder('http://help17.ru')
	
	def m=[domain: 'help17.ru', name: 'hash', value: '5446c7da7459d4515262cfe2a49ec940']
	
	def cookie = new BasicClientCookie(m.name, m.value)
	
	m.findAll { k,v -> !(k in ['name', 'value']) }.
            each { k,v -> cookie[k] = v }
	
    http.client.cookieStore.addCookie cookie	
	
	  http.request(GET, ContentType.TEXT) {
		uri.path = '/base/1/id4664'	
		headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
		//aResult << reader.BODY.depthFirst().find { it.@id.text() == 'komnata_4664'}.A.@onclick.text()
		response.success = { resp, reader ->
		  log.debug(reader.text)
		}
		response.failure = { resp ->
		}
	  }
  }
  def parseHelp17Ajax() {
  //shownumber('komnata', '4664', '5446c7da7459d4515262cfe2a49ec940', 'short')
  //$.post("http://help17.ru/template/pages/object/shownumber.ajax.php", { type: type, id: id, code: code, obtime: obtime },
	def aRets = []
	def http = new HTTPBuilder('http://help17.ru')
	
	def m=[domain: 'help17.ru', name: 'hash', value: '5446c7da7459d4515262cfe2a49ec940']
	
	def cookie = new BasicClientCookie(m.name, m.value)
	
	m.findAll { k,v -> !(k in ['name', 'value']) }.
            each { k,v -> cookie[k] = v }
	
    http.client.cookieStore.addCookie cookie	
	
	  http.request(GET, ContentType.TEXT) {
		//uri.path = '/template/pages/object/shownumber.ajax.php?type=komnata&id=4664&code=5446c7da7459d4515262cfe2a49ec940&obtime=short'
        uri.path = '/template/pages/object/shownumber.ajax.php'		
		uri.query = [type:'komnata',id:4664,code:'5446c7da7459d4515262cfe2a49ec940',obtime:'short']
		headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
		//aResult << reader.BODY.depthFirst().find { it.@id.text() == 'komnata_4664'}.A.@onclick.text()
		response.success = { resp, reader ->
		  log.debug(reader.text)
		}
		response.failure = { resp ->
		}
	  }
  }
*/
  String getAdrForCottage(def parent) {
    def all = ''
	def temp = 0
    parent.children().each {
	  it.children().each {
		if (temp == 3) all += it.text() + ', '
		if(it.text().startsWith('Регион')) temp = 3
		else temp = 0
	  }
    }
    parent.children().each {
	  it.children().each {
		if (temp == 1) all += it.text() + ', '
		if(it.text().startsWith('Расположение')) temp = 1
		else temp = 0
	  }
    }
    parent.children().each {
	  it.children().each {
		if (temp == 2) all += it.text() + ', '
		if (it.text().startsWith('Ближ. нас. пункт')) temp = 2
		else temp = 0
	  }
    }
    return all.trim()[0..-2]
  }

  def parseCottagesspb() {
		def aRets = []
		def http = new HTTPBuilder('http://www.cottagesspb.ru')
		for (int npage = 0; npage<=230; npage++){
	  	http.request(GET, ContentType.HTML) {
				uri.path = '/arenda/index.html'
				uri.query = [iPage:npage]
				headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

				response.success = { resp, reader ->
					aRets << reader.BODY.depthFirst().findAll { it.@href.text().toString().startsWith('dacha.html') && !it.text()}.collect{
						it.@href.text().toString().split('id_item=')[1].split('&')[0]
					}
				}

				response.failure = { resp ->
				}
	  	}
		}
		def aResult = []
		def newhttp = new HTTPBuilder('http://www.cottagesspb.ru')

		for ( int i=0; i<aRets.size(); i++)
	  	for ( int k=0; k<aRets[i].size(); k++)
			newhttp.request(GET, ContentType.HTML) {
		  	uri.path = '/arenda/dacha.html'
		  	uri.query = [id_item:aRets[i][k],id_type:466]
		  	headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		  	response.success = { resp, reader ->
			  	def tmp = [:]
			  	def data = getLocalText(reader.BODY.depthFirst().find { it.@class.text() == 'tpl_mainarea'}.TABLE[0].TR[0].TD[0].TABLE[0].TR[0].TD[1]).split('\n')
			  	try {
						tmp.sub = data[2]
						tmp.tel = data[3]
			  	}
			  	catch(Exception e) {
						tmp.sub = data[0]
						tmp.tel = data[0]
			  	}
			  	tmp.ids = aRets[i][k]
			  	aResult << tmp
		  	}
		  	response.failure = { resp ->
		  	}
			}

		/*aResult.each{//save>>
			if(it){
		  	def oO = new Ztempbndata()
		  	oO.type = ''
		  	oO.comn = '0'
		  	oO.adress = 'lenobl'
		  	oO.price = '0'
		  	oO.sub = it.sub?:''
		  	oO.tel = it.tel?:''
		  	oO.site = 'cottagesspb.ru'
		  	oO.date = ''
		  	oO.ids = it.ids?:''
				oO.email = ''
				oO.addit = ''
		  	oO.save(flush:true)
			}
		}*/
		return aResult
	}
  /*def sendMail() {		
		def http = new HTTPBuilder('http://mail-st.appspot.com')
		
    def email='<b>тестовый server</b>'//.replaceAll('\r\n','').decodeHTML()    
    //for (int npage = 0; npage<=230; npage++){
	  	http.request(GET, ContentType.HTML) {
				uri.path = '/mail_sender'
				uri.query = [email:email,to:'progatmp@mail.ru',subject:'заголовок']
				 //headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

				response.success = { resp, reader ->
					log.debug(reader)
				}

				response.failure = { resp ->
				}
	  	}
		//}			
	}*/
  //////////////////////////////////////////////////////////////////////////////////
  def ug_posut() {		
	def http = new HTTPBuilder('http://ug-posutochno.ru/')
 
	log.debug('ug-posutochno() START:')	
		
	for (int npage = 1; npage<=1; npage+=1){
	  def aRes = []
    def lsLink=[]
	  http.request(GET, ContentType.HTML) {        
    uri.path = '/index.php'
    //http://ug-posutochno.ru/index.php?CITY=2&FLAT=1&PG=4
		uri.query = [CITY:4,FLAT:3,PG:npage]		
	  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

	  response.success = { resp, reader ->	      
        reader.BODY.depthFirst().findAll {   it.@width.text() == '630'}.collect{	       
        //log.debug('it='+it)      		    
			  lsLink<<it.A.@href.text().toString().trim()
			  
		  }	  
	    }
	    response.failure = { resp ->
	    }	  	  
	  }
  //} 
  log.debug(lsLink)
  
  	/*http.parser.'text/html' = { resp ->
	  InputStreamReader isr = new InputStreamReader(resp.entity.content, "windows-1251")
	  new XmlSlurper(new org.cyberneko.html.parsers.SAXParser()).parse(isr)
	}//Переопределяем стандартные парсеры, потому что наглый сайт не возвращает данные в UTF-8 несмотря на явное требование в заголовке запроса.
	http.parser.'text/plain' = { resp ->
	  InputStreamReader isr = new InputStreamReader(resp.entity.content, "windows-1251")
	}*/
  
	  def i=0
	  for (sLink in lsLink){
    //log.debug('full: '+sLink)	    
		  def sQuery=sLink.split('ID=')[1]
		  sLink=sLink.split('ID')[0]
		  sLink=sLink.minus('?')
		
	  
	    http.request(GET, ContentType.HTML) {		  
		    uri.path = sLink
		    uri.query = [ID:sQuery]
		    headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'		  		  
		  
		    response.success = { resp, reader ->    
          def tmp=[:]
          reader.BODY.depthFirst().findAll{ it.@id.text()=="content"}.collect{  
          //log.debug('%%%%'+it.TABLE[0].TR[5].TD[0].P.B.FONT.text())          

          def sTel=it.TABLE[0].TR[5].TD[0].P.B.FONT.text().toString()
          //log.debug('!!!'+sTel)
          tmp.tel=sTel        
		      aRes<<tmp					 			  		  		  	 
        }
		  }
      
		  response.failure = { resp ->
		  }      	    
      }    
        i++
      }	 
//log.debug('aRes='+aRes)

      aRes.each{//save>>
		if(it){
		  def oO = new Ztempbndata()	 
		  
		  oO.type = ''
		  oO.comn = '0'
		  oO.adress = 'Пятигорск'
		  oO.price = 0
		  oO.sub = ''
		  oO.tel = it.tel
		  oO.email = ''
      oO.addit=''		  	
		  oO.site = 'http://ug-posutochno.ru'
		  oO.date = ''
		  oO.ids = ''
		  //oO.save(flush:true)
		}
	  }
	}
	log.debug('ug-posutochno() FINISH:')	
  }
 //////////////////////////////////////////////////////////////////////////////////
  def stay_ua1() {		
	def http = new HTTPBuilder('http://sevastopol.stay.ua/')
 
	log.debug('stay_ua() START:')	
  
  //searchIDs = [{"id":"3797"},{"id":"4343"},{"id":"4988"},{"id":"1778"},{"id":"2110"},{"id":"3297"},{"id":"4464"},{"id":"4469"},{"id":"5384"},{"id":"741"},{"id":"5349"},{"id":"4721"},{"id":"4141"},{"id":"1973"},{"id":"753"},{"id":"3340"},{"id":"2618"},{"id":"748"},{"id":"2547"},{"id":"739"},{"id":"3452"},{"id":"4139"},{"id":"5173"},{"id":"749"},{"id":"5492"},{"id":"762"},{"id":"4495"},{"id":"3701"},{"id":"2306"},{"id":"765"},{"id":"4344"},{"id":"4929"},{"id":"3359"},{"id":"733"},{"id":"4354"},{"id":"5160"},{"id":"5138"},{"id":"3245"},{"id":"752"},{"id":"4772"},{"id":"5164"},{"id":"3349"},{"id":"2174"},{"id":"737"},{"id":"4844"},{"id":"5180"},{"id":"4137"},{"id":"736"},{"id":"759"},{"id":"2033"},{"id":"4311"},{"id":"3809"},{"id":"5597"},{"id":"5155"},{"id":"3669"},{"id":"3334"},{"id":"2400"},{"id":"750"},{"id":"5376"},{"id":"3711"},{"id":"2132"},{"id":"738"},{"id":"5163"},{"id":"744"},{"id":"5275"},{"id":"734"},{"id":"4123"},{"id":"3649"},{"id":"730"},{"id":"5184"},{"id":"4931"},{"id":"1152"},{"id":"892"},{"id":"767"},{"id":"746"},{"id":"3030"},{"id":"5393"},{"id":"4934"},{"id":"5276"},{"id":"3731"},{"id":"2121"},{"id":"743"},{"id":"2691"},{"id":"747"},{"id":"3514"},{"id":"4501"},{"id":"3475"},{"id":"5487"},{"id":"3551"},{"id":"4249"},{"id":"4040"},{"id":"5158"},{"id":"5195"},{"id":"740"},{"id":"4815"},{"id":"1065"},{"id":"5060"},{"id":"745"},{"id":"2858"},{"id":"3271"},{"id":"4388"},{"id":"5181"},{"id":"5159"},{"id":"2558"},{"id":"2857"},{"id":"5001"},{"id":"4816"},{"id":"4135"},{"id":"3796"},{"id":"5344"},{"id":"3741"},{"id":"3792"},{"id":"2814"},{"id":"758"},{"id":"4138"},{"id":"2119"},{"id":"3765"},{"id":"5491"},{"id":"732"},{"id":"761"},{"id":"2473"},{"id":"3880"},{"id":"5193"},{"id":"2810"},{"id":"5277"},{"id":"3665"},{"id":"3699"},{"id":"5520"},{"id":"4318"},{"id":"731"},{"id":"4316"},{"id":"735"},{"id":"1122"},{"id":"4927"},{"id":"5192"},{"id":"5183"},{"id":"3875"},{"id":"5425"},{"id":"1019"},{"id":"3700"},{"id":"4196"},{"id":"3986"},{"id":"4433"},{"id":"2032"},{"id":"4803"},{"id":"4197"},{"id":"3498"},{"id":"1066"},{"id":"5350"},{"id":"5261"},{"id":"4102"},{"id":"4036"},{"id":"4386"},{"id":"4355"},{"id":"4134"},{"id":"4941"},{"id":"2142"},{"id":"3535"},{"id":"754"},{"id":"4132"},{"id":"5182"},{"id":"2253"},{"id":"5581"},{"id":"5368"},{"id":"5194"},{"id":"757"},{"id":"3459"},{"id":"4356"},{"id":"5185"},{"id":"5278"},{"id":"755"},{"id":"1160"},{"id":"5062"},{"id":"3286"},{"id":"4385"},{"id":"4140"},{"id":"5518"},{"id":"4313"},{"id":"4384"},{"id":"4977"},{"id":"756"},{"id":"4538"},{"id":"3668"},{"id":"3586"},{"id":"763"},{"id":"2738"},{"id":"3316"},{"id":"5104"},{"id":"742"},{"id":"5490"},{"id":"3607"},{"id":"3432"},{"id":"3810"},{"id":"3652"},{"id":"772"},{"id":"5191"},{"id":"766"},{"id":"5139"},{"id":"3992"},{"id":"5115"},{"id":"3543"},{"id":"3653"},{"id":"5137"}]; <!--Для прокрутки-->

		
	//for (int npage = 1; npage<=1; npage+=1){
	  def aIds=''
    def aRes = []
    def lsLink=[]
	  http.request(GET, ContentType.TEXT) {        
    //uri.path = '/index.php'
    //http://ug-posutochno.ru/index.php?CITY=2&FLAT=1&PG=4
		//uri.query = [CITY:4,FLAT:3,PG:npage]		
	  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
    
	  response.success = { resp, reader ->	      
        //reader.BODY.depthFirst().findAll {   it.@width.text() == '630'}.collect{	       
        def sIds=reader.text.split('var searchIDs =')[1].split(';')[0].trim()
        //log.debug('sIds='+sIds)      		    
			  //lsLink<<it.A.@href.text().toString().trim()
        def aJsonObj = new JsonSlurper().parseText(sIds)
        aIds=aJsonObj[0].id
        //log.debug('jsonObj='+jsonObj)        			  
		  }	  	    
	    response.failure = { resp ->
	    }	  	  
	  }
    
    log.debug('aIds='+aIds)
    
    def arIds=[]
    //aIds='"'+aIds+'"'
    //arIds<<aIds
    arIds<<'4925'
    arIds<<'5167'
    
    log.debug('arIds='+arIds)
 /*   
	  http.request(POST, ContentType.TEXT) {                    
    uri.path = '/ajax/load_adverts.php'
		uri.query = [ids:arIds,lang:'ru',order:'DESC']		
	  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

	    response.success = { resp, reader ->
        log.debug('reader.text='+reader.text)
        /*log.debug('reader.BODY='+reader.BODY)         
        reader.BODY.depthFirst().findAll {   it.@class.text() == 'new_img'}.collect{	       
        //log.debug('it='+it)      		    
			  lsLink<<it.A.@href.text().toString().trim()
			  
		  }	*/  
/*	    }
	    response.failure = { resp ->
	    }	  	  
	  }
*/	  
    
    HttpClient client = new HttpClient()  
    String url = "http://sevastopol.stay.ua/ajax/load_adverts.php"
    PostMethod method = new PostMethod(url)
    
    method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    
    method.addParameter("ids", arIds.toString());
    method.addParameter("lang", "ru");
    method.addParameter("order", "DESC");   
    
    int returnCode = client.executeMethod(method) //Response Code: 200, 302, 304 etc.
    def response = method.getResponseBodyAsString() // Actual response
    
    log.debug('***'+response)
   
    log.debug('lsLink='+lsLink) 
    log.debug('stay_ua() FINISH:') 
  } 
  def stay_ua_detail(){
  def http = new HTTPBuilder('http://lugansk.stay.ua')
  
  def text=""
  def f = new File("c:/tmp/stay_ua.txt")	
  f.eachLine() {line->
    text+=line
  }
  
  //log.debug(text)
  def out=[]
  def list = text.split(/<div\sclass=\'new_img\'>/)//toDO!!!
  log.debug('list.size()'+list.size())
  def k=0
  for(l in list){
    if(k>0)
      out<<l.split('>')[0].minus('<a href=').replace("'","").trim()     
    k++  
  }
  log.debug(out)
  
  
  //def i=0
	  for (sLink in out){
	    //if(i>0)return		
		log.debug('sLink='+sLink)
	    http.request(GET, ContentType.HTML) {		  
		  uri.path = sLink
		  //uri.query = [page:npage]
		  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

		  response.success = { resp, reader -> 
//log.debug('++++++'+reader.text)		  
		    reader.BODY.depthFirst().findAll { it.@class.text() == 'owner'}.collect{
//log.debug('!!!!!')		    
              def tmp = [:]
              //tmp.phone=''			  
			  
			  //log.debug('688='+it)
			  it=it.toString().replaceAll(/\s+/,' ')
			  //log.debug('690='+it)
			  if(it.split('дополнительно:').size()>1){
			    tmp.additional=it.split('дополнительно:')[1]
				it=it.minus(tmp.additional)				
			  }
			  it=it.minus('дополнительно:')
			    
			  if(it.split('e-mail:').size()>1){
			    tmp.email=it.split('e-mail:')[1]				
				it=it.minus(tmp.email)
			  }	
			  it=it.minus('e-mail:')
			  
			  def lsPhone=it.split('тел:')
			  for(sPhone in lsPhone){
			    tmp.phone=sPhone
			  }
			  
			  for(def j=2;j<7;j++)
			    tmp.phone=tmp.phone.replace('тел'+j+':',' ')
			  tmp.phone=tmp.phone.trim()	
			  
			  tmp.person=tmp.phone.find(~/[A-Za-zА-Яа-я]+/)	
			  if(!tmp.person)
			    tmp.person=tmp.phone.find(~/^[A-Za-zА-Яа-я]+\s?[A-Za-zА-Яа-я]+$/)
			  if(!tmp.person)
			    tmp.person=tmp.phone.find(~/^[A-Za-zА-Яа-я]+,?[A-Za-zА-Яа-я]+$/)	
			  tmp.phone=tmp.phone.minus(tmp.person)			  			  
			  
		   log.debug(tmp)  			  			
				//aRes<<tmp
					 
		
		  def oO = new Ztempbndata()
		  
		  oO.type = ''
		  oO.comn = '0'
		  oO.adress = 'lugansk'
		  oO.price = 0
		  oO.sub = tmp?.person?:''
		  oO.tel = tmp?.phone?:''
		  oO.email = tmp?.email?:''
          //oO.addit=it?.additional?:''		  
		  oO.addit=''	
		  oO.site = 'stay.ua'
		  oO.date = ''
		  oO.ids = ''
		  oO.save(flush:true)
		
	  
			}	 
		  }
		  response.failure = { resp ->
		    log.debug('fail='+resp)
		  }
	    }
        //i++
      }
  
  }

  def parseReferent() {
		def aRets = []
		def http = new HTTPBuilder('http://www.referent.ru')
		http.request(GET, ContentType.HTML) {
			uri.path = '/48/170712'
			headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

			response.success = { resp, reader ->
				reader.BODY.depthFirst().find { it.@class.text().toString().startsWith('rserver-wrapper')}.DIV.each{
					it.TABLE[0].TR.each{ tr ->
						def tmp = [:]
						tmp.bik = tr.TD[0].B[0].text()
						tmp.ca = tr.TD[0].B.size()>1?tr.TD[0].B[1].text():''
						tmp.bankname = tr.TD[1].B[0].text()
						aRets << tmp
					}
				}
			}

			response.failure = { resp ->
			}
		}

		/*aRets.each{//save>>
			if(it){
				new Bik(bankname:it.bankname,bik:it.bik,corraccount:it.ca).save(flush:true)
			}
		}*/

		return aRets.size()
	}

  def testapi() {
  	def responseText
    //def writer = new StringWriter()
    /*def xml = new MarkupBuilder(writer)
    xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")*/
    /*xml.request(id:122214){
    	user_id '1'
    	username 't'
    	usermail 'dsfsdfsdf@sdsdf.we'
    	home '766'
    	date_start '2015-12-15'
    	date_end '2015-12-17'
    	price '11221'
    	guest_num '2'
    	comment 'bla bla'
      secretcode 'aa390fe9-6ad5-47ea-951f-c0ed32f74e47'
  	}*/
  	/*xml.request(id:122213){
  		secretcode 'aa390fe9-6ad5-47ea-951f-c0ed32f74e47'
  		comment 'api comment'
  	}*/
  	def xmlstring = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<booking>
  <id>566daf8e58669338753d62af</id>
  <action>confirm</action>
  <timestamp>2015-12-13T17:49:02.256Z</timestamp>
  <created_at>2015-12-13T15:31:15.256Z</created_at>
  <total>200</total>
  <currency>USD</currency>
  <name>Jim</name>
  <email>jim@gmail.com</email>
  <propertyId>345</propertyId>
  <source>Flipkey</source>
  <checkIn>2015-12-20</checkIn>
  <checkOut>2015-12-26</checkOut>
  <guests>3</guests>
  <secret>aa390fe9-6ad5-47ea-951f-c0ed32f74e47</secret>
</booking>
  	"""
    //println writer.toString()
		def http = new HTTPBuilder('http://localhost:8080')
		http.request(POST, TEXT) {
			uri.path = '/Arenda/api/book/booking'
		  body = xmlstring
	  	response.success = { resp, reader ->
	  		responseText = reader.text
	  	}
	  	response.failure = { resp, reader ->
	  		println resp.status
				responseText = reader.text
	  	}
		}
		return responseText
  }
}