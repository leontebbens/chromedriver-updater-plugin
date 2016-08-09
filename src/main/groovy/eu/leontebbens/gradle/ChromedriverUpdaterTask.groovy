package eu.leontebbens.gradle

import groovyx.net.http.HTTPBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.zip.ZipInputStream

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET

class ChromedriverUpdaterTask extends DefaultTask {
    def checkOnly = false
    def latestVersion = "unknown"
    def localVersion = "unknown"
    def chromedriverSiteUrl = "http://chromedriver.storage.googleapis.com"
    def targetDir = "${project.buildDir}"

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
            def osName = System.getProperty("os.name").toLowerCase()
            def arch = osName.contains("windows") ? "win" : osName.contains("mac") ? "mac" : "linux"
            def driver = "$targetDir/$arch/chromedriver${arch.equals('win') ? '.exe' : ''}"
            println("webdriver.chrome.driver=$driver")
            System.setProperty("webdriver.chrome.driver", "$driver")
        }
    }

    private isChromedriverUptodate() {
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
            def latestDriverBaseUrl = "$chromedriverSiteUrl/$latestVersion"
            println("Downloading Chromedriver $latestVersion from $chromedriverSiteUrl ...")
            new File(project.buildDir.toString()).mkdir()
            downloadFile("$chromedriverSiteUrl/LATEST_RELEASE", "$targetDir/LATEST_RELEASE")
            downloadAndUnzip("$latestDriverBaseUrl/chromedriver_mac32.zip", "$targetDir/mac")
            downloadAndUnzip("$latestDriverBaseUrl/chromedriver_win32.zip", "$targetDir/win")
            downloadAndUnzip("$latestDriverBaseUrl/chromedriver_linux64.zip", "$targetDir/linux")
            println("Download complete: the latest Chromedriver is available in $targetDir")
        }
    }

    private String getLatestVersion() {
        def versionFileUrl = "$chromedriverSiteUrl/LATEST_RELEASE"
        def ver = ""
        getLogger().info("Reading file $versionFileUrl")
        try {
            def http = new HTTPBuilder(versionFileUrl)
            def httpProxyHost = System.properties.'http.proxyHost'
            def httpProxyPort = System.properties.'http.proxyPort'
            if (httpProxyHost == null || httpProxyPort == null)
                getLogger().info("not using http-proxy because -Dhttp.proxyHost and -Dhttp.proxyPort are not defined")
            else {
                getLogger().info("using http proxy " + httpProxyHost + ":" + httpProxyPort)
                http.setProxy(httpProxyHost, httpProxyPort as int, 'http')
            }

            http.request(GET, TEXT) { req ->
                headers.'User-Agent' = 'GroovyHTTPBuilder/1.0'
                response.success = { resp, reader ->
                    ver = reader.text.trim()
                    logger.info("Latest available Chromedriver version is $ver")
                }
                response.failure = { resp ->
                    getLogger().error("Error reading file $versionFileUrl")
                }
            }
        } catch (e) {
            getLogger().error("Error reading file", e)
        }
        ver
    }

    private String getLocalVersion() {
        def ver = ""

        try {
            def file = new File("$targetDir/LATEST_RELEASE")
            if (file.exists()) {
                ver = file.text
                logger.info("Local Chromedriver version is $ver")
            }
        } catch (e) {
            getLogger().error("Error reading file", e)
        }
        ver
    }

    private void downloadFile(def remoteUrl, def localUrl) {
        def file = new FileOutputStream(localUrl)
        def out = new BufferedOutputStream(file)
        out << new URL(remoteUrl).openStream()
        out.close()
    }

    private void downloadAndUnzip(def remoteZipURL, def localDir) {
        def zipEntry
        String fileContent = (remoteZipURL).toURL().withInputStream { s ->
            new ZipInputStream(s).with { zipStream ->
                new StringWriter().with { stringWriter ->
                    while (zipEntry = zipStream.nextEntry) {
                        if (zipEntry.name.startsWith('chromedriver')) {
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
        def file = new FileOutputStream("$localDir/${zipEntry.name}")
        def out = new BufferedOutputStream(file)
        out << fileContent
        out.close()
    }
}
