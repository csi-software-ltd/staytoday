//import org.codehaus.groovy.grails.commons.grailsApplication
class RawHtmlTagLib {

  def rawHtml = { attrs, body ->
    if (attrs.noline)
      out << body().replaceAll('\r\n','').decodeHTML()
    else
      out << body().decodeHTML()
  }

  def descrToHtml = { attrs, body ->
    out << ('<p>'+body().replaceAll('\r\n\r\n\r\n','</p><br/><br/><p>').replaceAll('\r\n\r\n','</p><br/><p>').replaceAll('\r\n','</p><p>')+'</p>').decodeHTML()
  }

  def userName = { attrs, body ->
    out << body().replace('.','·').replace('@','<img class="favicon" src="'+
        grailsApplication.config.grails.serverURL+'/images/favicon.gif" border="0">')
  }
  
  def shortString = { attrs ->
    String text = attrs.text
    int length  = attrs.length ? Integer.parseInt(attrs.length) : 100
  
    if ( text ) {
      if ( text.length() < length )
        out << text.encodeAsHTML()
      else {
        text = text[0..length-1]
        /*if ( text.lastIndexOf('. ') != -1 )
          out << text[0 .. text.lastIndexOf('. ') ]
        else if ( text.lastIndexOf(' ') != -1 )
          out << text[0 .. text.lastIndexOf(' ')] << '&hellip;'
        else*/
          out << text << '&hellip;'
      }
    }
  }
  
  def shortDesc = { attrs ->
    String text = attrs.text
    int length  = attrs.length ? Integer.parseInt(attrs.length) : 250
    
    if(text){
      if(text.length() > length)
        text = text[0..length-1]      
      out << text.replace('�','').replace('�','').replace('&mdash;','-').replace('"','').replace('\r\n\r\n','') << '..'
    }
  }  

  def shortDate = { attrs ->
    Date curdate = attrs.date
    Date today = new Date()
    
    if(curdate.date==today.date)
      out << formatDate(date:curdate,type:'time',style:'SHORT')
    else if(curdate.year==today.year)
      out << formatDate(format:'dd MMM.',date:curdate)
    else
      out << formatDate(format:'dd.MM.yyyy',date:curdate)
  }  
}
