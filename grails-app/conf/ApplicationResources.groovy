modules = {
  application {
    resource url:'js/application.js', disposition: 'head'
  }
  'jquery.qtip.min' {
    dependsOn 'jquery-1.8.3'
    resource url:'js/jquery.qtip.min.js', disposition: 'head'
  }
  'jquery-1.8.3' {
    resource url:'js/jquery-1.8.3.js', disposition: 'head'
  }
  'jquery-ui.min' {
    dependsOn 'jquery-1.8.3'
    resource url:'js/jquery-ui.min.js', disposition: 'head'
  }
  'jquery.faded' {
    dependsOn 'jquery-1.8.3'
    dependsOn 'jquery-ui.min'
    resource url:'js/jquery.faded.js', disposition: 'head'
  }
  'jquery.colorbox.min' {
    dependsOn 'jquery-1.8.3'
    resource url:'js/jquery.colorbox.min.js', disposition: 'head'
  }
  'jquery.calendar' {
    resource url:'js/jquery.calendar.js', disposition: 'head'
  }
  swfobject {
    resource url:'js/swfobject.js', disposition: 'head'
  }
  slideshow {
    resource url:'js/slideshow.js', disposition: 'head'
  }
  silverlight {
    resource url:'js/silverlight.js', disposition: 'head'
  }
  newyear {
    resource url:'js/newyear.js', disposition: 'head'
  }
  html5 {
    resource url:'js/html5.js', disposition: 'head'
  }
  gcal {
    resource url:'js/gcal.js', disposition: 'head'
  }
  'galleria.classic' {
    resource url:'js/galleria.classic.js', disposition: 'head'
  }
  galleria {
    resource url:'js/galleria.js', disposition: 'head'
  }
  'bootstrap-carousel.min' {
    resource url:'js/bootstrap-carousel.min.js', disposition: 'head'
  }
  'prototype/prototype' {
    dependsOn 'jquery-ui.min'
    dependsOn 'jquery.qtip.min'
    dependsOn 'jquery.colorbox.min'
    dependsOn 'application'
    resource url:'js/prototype/prototype.js', disposition: 'head'
  }
  'prototype/effects' {
    resource url:'js/prototype/effects.js', disposition: 'head'
  }
  'prototype/controls' {
    dependsOn 'prototype/effects'
    resource url:'js/prototype/controls.js', disposition: 'head'
  }
  'prototype/slider' {
    dependsOn 'prototype/prototype'
    resource url:'js/prototype/slider.js', disposition: 'head'
  }
  'prototype/autocomplete' {
    resource url:'js/prototype/autocomplete.js', disposition: 'head'
  }
  'prototype/scriptaculous' {
    dependsOn 'prototype/prototype'
    dependsOn 'prototype'
    resource url:'js/prototype/scriptaculous.js', disposition: 'head'
  }
  'list' {
    dependsOn 'prototype/prototype'
    resource url:'js/list.js', disposition: 'head'
  }
  'links/link' {
    resource url:'js/links/link.js', disposition: 'head'
  }
}
