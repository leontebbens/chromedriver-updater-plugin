package eu.leontebbens


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
            println("Great! Your chromedriver is up to date (version $localVersion)")
        } else {
            throw new AssertionError("A new chromedriver is available: $latestVersion")
        }
    }
    
    private updateChromedriver() {
        if (latestVersion == localVersion) {
            println("Your chromedriver is already up to date (version $localVersion)")
        } else {
            def latestDriverBaseUrl = chromedriverSiteUrl + "/" + latestVersion
            println("Downloading from $chromedriverSiteUrl to " + project.buildDir)
            new File(project.buildDir.toString()).mkdir()
            downloadFile(chromedriverSiteUrl + "/LATEST_RELEASE", project.buildDir.toString() + File.separator + "LATEST_RELEASE")
            downloadAndUnzip(latestDriverBaseUrl + File.separator + "chromedriver_mac32.zip", project.buildDir.toString() + File.separator + "mac")
            downloadAndUnzip(latestDriverBaseUrl + File.separator + "chromedriver_win32.zip", project.buildDir.toString() + File.separator + "win")
            downloadAndUnzip(latestDriverBaseUrl + File.separator + "chromedriver_linux64.zip", project.buildDir.toString() + File.separator + "linux")
        }
    }

    private String getLatestVersion() {
        def versionFileUrl = chromedriverSiteUrl + '/LATEST_RELEASE'
        def ver = ""
        getLogger().info("Reading file " + versionFileUrl)
        try {
            def http = new HTTPBuilder(versionFileUrl)
              http.request(GET,TEXT) { req ->
                headers.'User-Agent' = 'GroovyHTTPBuilder/1.0'
                response.success = { resp, reader -> ver = reader.getText() 
                  logger.info("Latest available chromedriver version is " + ver)
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
                logger.info("Local chromedriver version is " + ver)
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
