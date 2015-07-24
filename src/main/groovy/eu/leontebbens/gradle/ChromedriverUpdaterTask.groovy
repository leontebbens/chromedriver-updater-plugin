package eu.leontebbens.gradle


import groovyx.net.http.HTTPBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.TEXT
import java.net.URL
import java.io.File
import java.util.zip.*


class ChromedriverUpdaterTask extends DefaultTask {

    def checkOnly = false
    def latestVersion = "unknown"
    def localVersion = "unknown"
    def chromedriverSiteUrl = "http://chromedriver.storage.googleapis.com"

    ChromedriverUpdaterTask() {
        setGroup("Build")
        setDescription("Checks the latest release of the selenium chromedriver files on the googlecode website. And downloads them when a new release is available")
    }

    @TaskAction
    def chromedriverAction() {
        latestVersion = getLatestVersion();
        localVersion = getLocalVersion();
        if (checkOnly) {
            isChromedriverUptodate()
        } else {
            updateChromedriver()
        }
    }

    private isChromedriverUptodate()
    {
        if (latestVersion == localVersion) {
            println("Great! Your Chromedriver is up to date (version $localVersion)")
        } else {
            throw new AssertionError("A new Chromedriver is available: $latestVersion")
        }
    }
    
    private updateChromedriver() {
        if (latestVersion == localVersion) {
            println("Great! Your Chromedriver is already up to date (version $localVersion)")
        } else {
            def latestDriverBaseUrl = chromedriverSiteUrl + "/" + latestVersion
            println("Downloading Chromedriver $latestVersion from $chromedriverSiteUrl ...")
            new File(project.buildDir.toString()).mkdir()
            downloadFile(chromedriverSiteUrl + "/LATEST_RELEASE", project.buildDir.toString() + File.separator + "LATEST_RELEASE")
            downloadAndUnzip(latestDriverBaseUrl + File.separator + "chromedriver_mac32.zip", project.buildDir.toString() + File.separator + "mac")
            downloadAndUnzip(latestDriverBaseUrl + File.separator + "chromedriver_win32.zip", project.buildDir.toString() + File.separator + "win")
            downloadAndUnzip(latestDriverBaseUrl + File.separator + "chromedriver_linux64.zip", project.buildDir.toString() + File.separator + "linux")
            println("Download complete: the latest Chromedriver is available in " + project.buildDir.toString())
        }
    }

    private String getLatestVersion() {
        def versionFileUrl = chromedriverSiteUrl + '/LATEST_RELEASE'
        def ver = ""
        getLogger().info("Reading file " + versionFileUrl)
        try {
            def http = new HTTPBuilder(versionFileUrl)
            def httpProxyHost = System.properties.'http.proxyHost'
            def httpProxyPort = System.properties.'http.proxyPort'
            if (httpProxyHost==null || httpProxyPort==null)
                getLogger().info("not using http-proxy because -Dhttp.proxyHost and -Dhttp.proxyPort are not defined")
            else {
                getLogger().info("using http proxy " + httpProxyHost + ":" + httpProxyPort)
                http.setProxy(httpProxyHost, httpProxyPort as int, 'http')
            }
            // https proxy config results in Peer not Authenticated error, and googlecode is not https so I disbled this for now
//            def httpsProxyHost = System.properties.'https.proxyHost'
//            def httpsProxyPort = System.properties.'https.proxyPort'
//            if (httpsProxyHost==null && httpsProxyPort==null)
//                getLogger().info("not using https-proxy because -Dhttps.proxyHost and -Dhttps.proxyPort are not defined")
//            else {
//                getLogger().info("using https proxy " + httpsProxyHost + ":" + httpsProxyPort)
//                http.setProxy(httpsProxyHost, httpsProxyPort as int, 'https')
//            }
            http.request(GET,TEXT) { req ->
                headers.'User-Agent' = 'GroovyHTTPBuilder/1.0'
                response.success = { resp, reader -> ver = reader.getText() 
                  logger.info("Latest available Chromedriver version is " + ver)
                }
                response.failure = { resp ->
                    getLogger().error("Error reading file " + versionFileUrl)
                }
            }
        } catch (e) {
            getLogger().error("Error reading file: ", e)
        }
        ver
    }
    
    private String getLocalVersion() {
        def ver = ""
    
        try {
            def file = new File(project.buildDir.toString() + File.separator + "LATEST_RELEASE")
            if (file.exists()) {
                ver = file.text
                logger.info("Local Chromedriver version is " + ver)
            }
         } catch (e) {
            getLogger().error("Error reading file: ", e)
        }
        ver               
    }
    
    private void downloadFile(def remoteUrl, def localUrl)
    {
        def file = new FileOutputStream(localUrl)
        def out = new BufferedOutputStream(file)
        out << new URL(remoteUrl).openStream()
        out.close()
    }
    
    private void downloadAndUnzip(def remoteZipURL, def localDir) {
        def zipEntry
        String fileContent = (remoteZipURL).toURL().withInputStream { s ->
            new ZipInputStream( s ).with { zipStream ->
                new StringWriter().with { stringWriter ->
                    while( zipEntry = zipStream.nextEntry ) {
                        if( zipEntry.name.startsWith( 'chromedriver' ) ) {
                            stringWriter << zipStream
                            break
                        }
                        zipStream.closeEntry()
                    }
                    stringWriter.toString()
                }
            }
        }
        new File(localDir).mkdir()
        def file = new FileOutputStream(localDir + File.separator + zipEntry.name)
        def out = new BufferedOutputStream(file)
        out << fileContent
        out.close()
    }

    
}
