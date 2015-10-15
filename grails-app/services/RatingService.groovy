import org.codehaus.groovy.grails.commons.ConfigurationHolder
class RatingService {

	static transactional = true

	def calculateHomeRating(lId,iExtrarating,iRatingpenalty,sInfraoption,lClientId) {
  	def result = [photo:0,verifiedphoto:0,clientComm:0,comments:0,trips:0,infras:0,videos:0,descr:0,resstat:0,extrarating:0,ratingpenalty:0,bonuspromote:0,rating:0]
		result.photo = Homephoto.findAllByHome_id(lId).size()>Tools.getIntVal(ConfigurationHolder.config.photo.normal.kol,3)?Tools.getIntVal(ConfigurationHolder.config.photo.normal.rating,30):Homephoto.findAllByHome_id(lId).size()>Tools.getIntVal(ConfigurationHolder.config.photo.excellent.kol,10)?Tools.getIntVal(ConfigurationHolder.config.photo.excellent.rating,50):0
		result.verifiedphoto = Homephoto.findByHome_idAndIs_main(lId,1)?.is_verified==1?Tools.getIntVal(ConfigurationHolder.config.photo.verified.rating,100):0
		for ( comm in Ucomment.csiGetClientCommentList(lId) ){
			result.clientComm += comm.rating==2?Tools.getIntVal(ConfigurationHolder.config.clientComm.good.rating,50):comm.rating==0?Tools.getIntVal(ConfigurationHolder.config.clientComm.normal.rating,20):0
		}
		for ( comm in Ucomment.csiGetNotClientCommentList(lId) ){
			result.comments += comm.rating==2?Tools.getIntVal(ConfigurationHolder.config.comm.good.rating,10):comm.rating==0?Tools.getIntVal(ConfigurationHolder.config.comm.normal.rating,5):0
		}
		for ( trip in Trip.findAllByHome_idAndModstatusGreaterThanEquals(lId,0) ){
			result.trips += trip.rating==5?Tools.getIntVal(ConfigurationHolder.config.trip.excellent.rating,100):trip.rating==4?Tools.getIntVal(ConfigurationHolder.config.trip.good.rating,80):trip.rating==3?Tools.getIntVal(ConfigurationHolder.config.trip.normal.rating,60):0
		}
		result.infras += ((sInfraoption?:'')!='')?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.infraoption.rating,15):0
		result.infras += Homeguidebook.findAllByHome_id(lId).size()?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.homeguidebook.rating,15):0
		result.videos += Homevideo.findAllByHome_id(lId).size()>=Tools.getIntVal(ConfigurationHolder.config.ratingSystem.video.kol,1)?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.video.rating,50):0
		result.descr += User.findByClient_id(lClientId)?.description?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.userdescription.rating,10):0
		result.descr += User.findByClient_id(lClientId)?.picture?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.userpicture.rating,10):0
		result.resstat += Client.get(lClientId)?.resstatus==1?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.resstatus.rating,500):0
		result.bonuspromote += Payorder.findAllByBonusendGreaterThanAndHome_id(new Date(),lId).size()*Tools.getIntVal(ConfigurationHolder.config.ratingSystem.bonuspromotebron.rating,100)
		result.extrarating += iExtrarating + (Client.get(lClientId)?.addrating?:0)
		result.ratingpenalty -= iRatingpenalty

		result.each{ if(it.key!='rating') result.rating += it.value }

  	return result
	}

	def processRating(){
		def aHome = Home.findAllByModstatusGreaterThan(0)
		for (home in aHome){
			//1 - Êîëè÷åñòâî ôîòîê
			home.rating = Homephoto.findAllByHome_id(home.id).size()>Tools.getIntVal(ConfigurationHolder.config.photo.normal.kol,3)?Tools.getIntVal(ConfigurationHolder.config.photo.normal.rating,30):Homephoto.findAllByHome_id(home.id).size()>Tools.getIntVal(ConfigurationHolder.config.photo.excellent.kol,10)?Tools.getIntVal(ConfigurationHolder.config.photo.excellent.rating,50):0
			//2 - Ãëàâíàÿ ôîòî îò ôîòîãðàôà
			home.rating += Homephoto.findByHome_idAndIs_main(home.id,1)?.is_verified==1?Tools.getIntVal(ConfigurationHolder.config.photo.verified.rating,100):0
			//3 - Îòçûâû ïî îáúåêòó(êëèåíòà)
			for ( comm in Ucomment.csiGetClientCommentList(home.id) ){
				home.rating += comm.rating==2?Tools.getIntVal(ConfigurationHolder.config.clientComm.good.rating,50):comm.rating==0?Tools.getIntVal(ConfigurationHolder.config.clientComm.normal.rating,20):0
			}
			//4 - Îòçûâû ïî îáúåêòó(íå êëèåíòà)
			for ( comm in Ucomment.csiGetNotClientCommentList(home.id) ){
				home.rating += comm.rating==2?Tools.getIntVal(ConfigurationHolder.config.comm.good.rating,10):comm.rating==0?Tools.getIntVal(ConfigurationHolder.config.comm.normal.rating,5):0
			}
			//5 - Ðåéòèíãè ïîåçäêè
			for ( trip in Trip.findAllByHome_idAndModstatusGreaterThanEquals(home.id,0) ){
				home.rating += trip.rating==5?Tools.getIntVal(ConfigurationHolder.config.trip.excellent.rating,100):trip.rating==4?Tools.getIntVal(ConfigurationHolder.config.trip.good.rating,80):trip.rating==3?Tools.getIntVal(ConfigurationHolder.config.trip.normal.rating,60):0
			}
			//6 - Èíôðàñòðóêòóðà ðÿäîì
			home.rating += ((home.infraoption?:'')!='')?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.infraoption.rating,15):0
			//7 - Ïóòåâîäèòåëü
			home.rating += Homeguidebook.findAllByHome_id(home.id).size()?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.homeguidebook.rating,15):0
			//8 - Êîëè÷åñòâî âèäåî
			home.rating += Homevideo.findAllByHome_id(home.id).size()>=Tools.getIntVal(ConfigurationHolder.config.ratingSystem.video.kol,1)?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.video.rating,50):0
			//9 - "áàëëû çà êà÷åñòâî èíôîðìàöèè" - ïîäàðîê îò àäìèíèñòðàòîðà
			home.rating += home.extrarating
			//10 - "øòðàôíûå áàëëû çà íàðóøåíèå ðåãëàìåíòà ðàçìåùåíèÿ èíôîðìàöèè" - ïîäàðîê îò àäìèíèñòðàòîðà
			home.rating -= home.ratingpenalty
			//11
			home.rating += User.findByClient_id(home.client_id)?.description?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.userdescription.rating,10):0
			//12
			home.rating += User.findByClient_id(home.client_id)?.picture?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.userpicture.rating,10):0
			//13
			home.rating += Client.get(home.client_id)?.resstatus==1?Tools.getIntVal(ConfigurationHolder.config.ratingSystem.resstatus.rating,500):0
			//14
			home.rating += Payorder.findAllByBonusendGreaterThanAndHome_id(new Date(),home.id).size()*Tools.getIntVal(ConfigurationHolder.config.ratingSystem.bonuspromotebron.rating,100)
			//15
			home.rating += Client.get(home.client_id)?.addrating?:0
		}
	}

	def processAdditionalRating(){
		def lsUser = User.findAllByClient_idNotEqualAndModstatus(0,1)
		for(user in lsUser) {
			if((new Date().getTime() - user.inputdate.getTime())/(1000*60*60*24)<=Tools.getIntVal(ConfigurationHolder.config.addRating.newuser.days,30)) {
				user.activityrating = Tools.getIntVal(ConfigurationHolder.config.addRating.newuser.rating,30)
			} else if ((new Date().getTime() - user.lastdate.getTime())/(1000*60*60*24)<=Tools.getIntVal(ConfigurationHolder.config.addRating.activeuser.days,20)){
				user.activityrating = Tools.getIntVal(ConfigurationHolder.config.addRating.activeuser.rating,20)
			} else {
				user.activityrating = 0
			}
			switch(user.csiGetResponseTime().time) {
				case 1:user.activityrating += Tools.getIntVal(ConfigurationHolder.config.addRating.responsetime.step1.rating,0);break;
				case 2:user.activityrating += Tools.getIntVal(ConfigurationHolder.config.addRating.responsetime.step2.rating,0);break;
				case 3:user.activityrating += Tools.getIntVal(ConfigurationHolder.config.addRating.responsetime.step3.rating,0);break;
				case 4:user.activityrating += Tools.getIntVal(ConfigurationHolder.config.addRating.responsetime.step4.rating,0);break;
				default:break;
			}
		}
	}
}