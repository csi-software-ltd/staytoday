// Galleria Classic Theme 2011-08-01
// (c) 2012 Aino - http://galleria.aino.se
// Licensed under the MIT license https://raw.github.com/aino/galleria/master/LICENSE
Galleria.requires(1.25,"This version of Classic theme requires Galleria 1.2.5 or later");
(function(b){Galleria.addTheme({name:"classic",author:"Arenda",css:"../css/galleria.classic.css",defaults:{},init:function(){this.bind("loadstart",function(a){a.cached||(this.$("loader").show().fadeTo(200,0.4),this.$("info").toggle(this.hasInfo()),b(a.thumbTarget).css("opacity",1).parent().siblings().children().css("opacity",0.6))});this.bind("loadfinish",function(){var a=this;this.$("loader").fadeOut(200);setTimeout(function(){a.$("stage").addClass("galleria-loaded")},300)})}})})(jQuery);