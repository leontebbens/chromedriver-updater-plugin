package eu.leontebbens.gradle

import de.undercouch.gradle.tasks.download.DownloadAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class ChromedriverUpdaterTask extends DefaultTask {
    @Input
    def majorVersion = ""
    @Input
    def checkOnly = false
    def latestVersion = "unknown"
    def localVersion = "unknown"
    @Internal
    def chromedriverSiteUrl = "http://chromedriver.storage.googleapis.com"
    @Input
    def targetDir = "${project.buildDir}/chromedriver"
    @OutputFile
    def LOCAL_VER = "$targetDir/LOCAL_VERSION"
    @OutputFile
    def driverLocation = getDriverLocation();

    ChromedriverUpdaterTask() {
        setGroup("Build")
        setDescription("Checks the latest release of the selenium chromedriver files on the googlecode website. And downloads them when a new release is available")
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @TaskAction
    def chromedriverAction() {
        latestVersion = getLatestVersion();
        localVersion = getLocalVersion();
        if (checkOnly) {
            isChromedriverUptodate()
        } else {
            updateChromedriver()
            println("webdriver.chrome.driver=$driverLocation")
            ant.chmod(perm: "755", file: driverLocation)
        }
    }

    /**
     * let other gradle tasks to use this driver location
     * @return downloaded chrome driver location on the disk
     */
    public String getDriverLocation() {
        def arch = calcArch()
        driverLocation = "$targetDir/$arch/chromedriver${arch.equals('win') ? '.exe' : ''}"
        return driverLocation
    }

    private String getLatestReleaseDownloadUrl() {
        def postfix = !majorVersion.isEmpty() ? "_$majorVersion" : ""
        return "$chromedriverSiteUrl/LATEST_RELEASE$postfix"
    }

    private String calcArch() {
        def osName = System.getProperty("os.name").toLowerCase()
        return osName.contains("windows") ? "win" : osName.contains("mac") ? "mac" : "linux"
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

            new File(project.buildDir.toString()).mkdir()
            println("Downloading Chromedriver $latestVersion from $latestDriverBaseUrl ...")
            downloadFile(getLatestReleaseDownloadUrl(), LOCAL_VER)
            def arch = calcArch()
            def bit = arch.equals('win') ? '32' : '64'
            def osArchSuffix = getOsArchDependentSuffix(arch)
            downloadAndUnzip("$latestDriverBaseUrl", "chromedriver_${arch}${bit}${osArchSuffix}.zip", "$targetDir/${arch}")
            println("Download complete: the latest Chromedriver is available in $targetDir")
        }
    }

    private String getOsArchDependentSuffix(String arch) {
        if (arch == "mac" && System.getProperty("os.arch") == "aarch64") {
            return "_m1"
        } else {
            return ""
        }
    }

    private String getLatestVersion() {
        def target = new File("$targetDir/LATEST_RELEASE")
        downloadFile(getLatestReleaseDownloadUrl(), target)
        return target.text.trim()
    }

    private String getLocalVersion() {
        def ver = ""

        try {
            def file = new File(LOCAL_VER)
            if (file.exists()) {
                ver = file.text.trim()
                logger.info("Local Chromedriver version is $ver")
            }
        } catch (e) {
            getLogger().error("Error reading file", e)
        }
        ver
    }

    private void downloadFile(def remoteUrl, def target) {
        logger.info("Downloading $remoteUrl to $target")
        DownloadAction da = new DownloadAction(project)
        da.dest(target)
        da.src(remoteUrl)
        da.onlyIfNewer(false)
        try {
            da.execute()
        } catch (Exception e) {
            getLogger().error("unable to download $remoteUrl, ${e.getMessage()}")
            throw e
        }
    }

    private void downloadAndUnzip(def remoteZipURL, def zipName, String localDir) {
        String tmpZipFile = "$targetDir/tempZip"
        downloadFile("$remoteZipURL/$zipName", tmpZipFile)
        new File(localDir).mkdirs()
        new AntBuilder().unzip(src: tmpZipFile, dest: localDir)
        new File(tmpZipFile).delete();
    }
}
