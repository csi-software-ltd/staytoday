/*grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
*/
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
        //excludes 'xercesImpl'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    //log "warn"
    checksums false // Whether to verify checksums on resolve
    repositories {
        inherits true // Whether to inherit repository definitions from plugins 

        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
        runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
            excludes 'xalan'
            excludes 'xml-apis'
            excludes 'groovy'
        }
    }
    plugins {
        runtime ":hibernate:$grailsVersion"
        //runtime ":jquery:1.8.0"
        //runtime ":prototype:latest.release"
        runtime ":prototype:1.0"
        runtime ":resources:1.2.RC2"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        compile ":cache-headers:1.1.5"
        runtime ":zipped-resources:1.0.1"
        runtime ":cached-resources:1.1"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"
        runtime (":grails-melody:1.19") {
            excludes "com.lowagie:itext:2.1.7"
        }
        compile ":rendering:0.4.3"
        compile ":excel-export:0.1.6"

        //compile ':cache:1.0.1'
    }
}
