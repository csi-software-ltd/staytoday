class GbmessageSearch {
  
  def searchService

  static mapping = {
    version false
    table 'DUMMY_NAME'
    cache false
  }
  Long id
  Long user_id
  
  Integer gbtype_id
  Integer status

  String gbtypename  
  String fio
  String adr
  String tel_code
  String tel
  String email  
  String rectitle
  String rectext
  String recinfo
  String username
  String home  
  Date regdate  
  
  /////////////////////////////////////////////////////////////////////////////////////
  def csiGetMessages(hParams,iMax,iOffset){
    def hsSql = [:]
    hsSql.select = """ guestbook.id        as id,
                       guestbook.user_id  as user_id,
                       guestbook.gbtype_id as gbtype_id,                       
                       gbtype.name         as gbtypename,
                       guestbook.fio       as fio,
                       guestbook.adr       as adr,
                       guestbook.tel_code  as tel_code,
                       guestbook.tel       as tel,
                       guestbook.email     as email,                       
                       guestbook.rectitle  as rectitle,
                       guestbook.rectext   as rectext,
                       guestbook.modstatus    as status,
                       guestbook.regdate   as regdate,                       
                       guestbook.recinfo   as recinfo,
					   guestbook.home      as home,
					   user.name           as username"""
    hsSql.from = "guestbook left outer join gbtype on gbtype.id=guestbook.gbtype_id left outer join user on user.id=guestbook.user_id "
    hsSql.where = "guestbook.modstatus=:status "+
                                    ((hParams.int.type>0)?" AND guestbook.gbtype_id=:type":"")+
                                    ((hParams.string.datefrom!='')?" AND guestbook.regdate>=:datefrom":"")+
                                    ((hParams.string.dateto!='')?" AND guestbook.regdate<=:dateto ":"")
    hsSql.order = "id DESC"
    def hsLong = [:]
    def hsList = null
    hsLong += [status:(hParams.int.status==1)?1:0]
    hsLong += (hParams.int.type>0)?[type:hParams.int.type]:[:]
    def hsString = (hParams.string.datefrom!='')?[datefrom:hParams.string.datefrom]:[:]
    hsString += (hParams.string.dateto!='')?[dateto:hParams.string.dateto]:[:]
    //def hsRes = [records:searchService.fetchData(hsSql,hsLong,null,hsString,hsList,GbmessageSearch.class)]
	def hsRes = searchService.fetchDataByPages(hsSql, null,
                hsLong,null,hsString,hsList,null,
                iMax,iOffset,
                'guestbook.id',true,
                GbmessageSearch.class)
    return hsRes
  }
}
