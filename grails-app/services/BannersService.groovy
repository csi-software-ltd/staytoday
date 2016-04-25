//import org.codehaus.groovy.grails.commons.grailsApplication
class BannersService {
  def grailsApplication
  static boolean transactional = false
  static scope = "request"
  
  //////////////////////////////////////////////////////////////////////////////////////////////////  
  def getBanners(iX,iY,iZoom,iMaxDistance,sReferer){ 
	def hsRes =[banner:[]]
	def oAdvbanner=new Advbanner()
	if(iX && iY)					 
      hsRes.banner=oAdvbanner.getBannerByTypeXYZoom(2,iX,iY,iZoom,1,iMaxDistance)
    if(!hsRes.banner)
	  hsRes.banner=oAdvbanner.getBannerByTypeXYZoom(1,0,0,0,1)

	if(hsRes.banner?.size()>0){
	  hsRes.banner=hsRes.banner[0]	      	  
	 
	  def nonbotflag=true
      for(oSpider in Spider.findAll('FROM Spider'))   
	    if(sReferer.split(oSpider.name).size()>1)
	      nonbotflag=false   
      def lsIds=[]//[] for future number of banners>1
      lsIds<<hsRes.banner.id  

	  if(nonbotflag){      	
        oAdvbanner.updateShowBanners(lsIds)
	  } 
	}
	hsRes.url=grailsApplication.config.urlphoto+'ar_banners/' 
	return hsRes
  }    
 
  /////////////////////////////////////////////////////////////////////////////////
  def getRedirectBanner(iId){
    def oAdvbanner=new Advbanner()
    return oAdvbanner.updateClickBannersAndRedirect(iId)
  }
}
