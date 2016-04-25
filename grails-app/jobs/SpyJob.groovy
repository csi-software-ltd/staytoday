import grails.util.Holders
class SpyJob {
  static triggers = {
    simple startDelay:60000, repeatInterval: (Tools.getIntVal(Holders.config.spy_timeout1,30000))
  }
  def execute() {
    try{
      def oTemp_ipblock=new Temp_ipblock()
      oTemp_ipblock.csiDelete(0)
      oTemp_ipblock.csiDelete(1)
      def userIp=oTemp_ipblock.findSpy(0)
      if((userIp?:[]).size()>0){
        log.debug("LOG>> Process spy. UserIp: "+userIp)
        for(ip in userIp){
          def oTemp_ipblockTmp=new Temp_ipblock()
          oTemp_ipblockTmp.userip=ip
          oTemp_ipblockTmp.requesttime=new Date()
          oTemp_ipblockTmp.status=1
          if(!oTemp_ipblockTmp.save(flush:true)){
            log.debug(" Error on save Temp_ipblock:")
            oTemp_ipblockTmp.errors.each{log.debug(it)}
          }
        }
        log.debug("LOG>> Spy processed")
      }
    }catch(Exception e){
      log.debug('Error in spy job'+e.toString())
    }
  }
}