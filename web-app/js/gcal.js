// FullCalendar v1.5.2 Google Calendar Plugin, Sun Aug 21 22:06:09 2011 -0700
// Copyright (c) 2011 Adam Shaw
// Dual licensed under the MIT and GPL licenses, located in MIT-LICENSE.txt and GPL-LICENSE.txt respectively.
(function(c){var a=c.fullCalendar,f=a.formatDate,k=a.parseISO8601,l=a.addDays,m=a.applyAll;a.sourceNormalizers.push(function(b){if("gcal"==b.dataType||void 0===b.dataType&&(b.url||"").match(/^(http|https):\/\/www.google.com\/calendar\/feeds\//))b.dataType="gcal",void 0===b.editable&&(b.editable=!1)});a.sourceFetchers.push(function(b,a,n){if("gcal"==b.dataType){var p=b.success;a=c.extend({},b.data||{},{"start-min":f(a,"u"),"start-max":f(n,"u"),singleevents:!0,"max-results":9999});var g=b.currentTimezone;
g&&(a.ctz=g=g.replace(" ","_"));return c.extend({},b,{url:b.url.replace(/\/basic$/,"/full")+"?alt=json-in-script&callback=?",dataType:"jsonp",data:a,startParam:!1,endParam:!1,success:function(b){var a=[];b.feed.entry&&c.each(b.feed.entry,function(b,d){var h=d.gd$when[0].startTime,e=k(h,!0),f=k(d.gd$when[0].endTime,!0),h=-1==h.indexOf("T"),j;c.each(d.link,function(b,a){"text/html"==a.type&&(j=a.href,g&&(j+=(-1==j.indexOf("?")?"?":"&")+"ctz="+g))});h&&l(f,-1);a.push({id:d.gCal$uid.value,title:d.title.$t,
url:j,start:e,end:f,allDay:h,location:d.gd$where[0].valueString,description:d.content.$t})});var e=[a].concat(Array.prototype.slice.call(arguments,1)),e=m(p,this,e);return c.isArray(e)?e:a}})}});a.gcalFeed=function(b,a){return c.extend({},a,{url:b,dataType:"gcal"})}})(jQuery);