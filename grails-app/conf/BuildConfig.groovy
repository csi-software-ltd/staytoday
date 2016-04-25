/*grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
*/
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
        //excludes 'xercesImpl'
    }
    //log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    log "warn"
    checksums false // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    repositories {
        inherits true // Whether to inherit repository definitions from plugins 

        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
        //runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
        runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1') {
            excludes 'xalan'
            excludes 'xml-apis'
            excludes 'groovy'
        }
        //runtime 'org.springframework:spring-test:4.2.4.RELEASE'
    }
    plugins {
        runtime ":hibernate4:4.3.10"
        //runtime ":jquery:1.8.0"
        //runtime ":prototype:latest.release"
        runtime ":prototype:1.0"
        runtime ":resources:1.2.14"
        runtime ':jcaptcha:1.2.1'
        runtime ':calendar:1.2.1'
        runtime ':android-gcm:0.1'
        runtime ':compress:0.4'
        runtime ':profiler:0.3'
        runtime ':quartz:1.0.2'
        runtime ':fckeditor:0.9.5'
        runtime ':mail:1.0'
        compile ':imagetools:0.1'

        // Uncomment these (or add new ones) to enable additional resources capabilities
        compile ":cache-headers:1.1.7"
        runtime ":zipped-resources:1.0.1"
        runtime ":cached-resources:1.1"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:7.0.55.3"
        runtime (":grails-melody:1.58.0") {
            excludes "com.lowagie:itext:2.1.7"
        }
        compile ":rendering:1.0.0"
        compile ":excel-export:0.1.6"
        compile ":jdbc-pool:7.0.47"

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        //compile ':cache:1.1.8'
        // asset-pipeline 2.0+ requires Java 7, use version 1.9.x with Java 6
        compile ":asset-pipeline:2.5.7"
        //compile ':cache:1.0.1'
    }
}