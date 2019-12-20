Chromedriver Updater plugin
===========================

This is a simple plugin for [Gradle](http://www.gradle.org/) to keep your [Chromedriver](https://sites.google.com/a/chromium.org/chromedriver/) for [Selenium](http://seleniumhq.org) up to date.
It checks the [google code website](http://chromedriver.storage.googleapis.com/index.html) for a new version of the Chromedriver, and downloads it to the build directory.

The plugin has been successfully tested with Gradle 4.10.3 on Mac OS and Windows 10.
It should work with newer versions and on other operations systems as well.


Apply plugin configuration
--------------------------

Put these in your `build.gradle` (Gradle 4.10.3 or higher):

```groovy
plugins {
    id "eu.leontebbens.gradle.chromedriver-updater" version "1.6.2"
}
```

```groovy
apply plugin: "eu.leontebbens.gradle.chromedriver-updater"
```

Next run `gradlew updateChromedriver` and the drivers will be downloaded to the directory `build`.

### Configuration
Plugin allows you to download the latest chromedriver version according to the [latest version selection](https://sites.google.com/a/chromium.org/chromedriver/downloads/version-selection). To configure the major chromedriver version, add these lines to your build script file:
```groovy
chromedriver {
  majorVersion = "78"
}
```

### Example integration test

To use the downloaded driver in your tests:
```
task integrationTest(type: Test, dependsOn: updateChromedriver) {
    systemProperties = [
            'webdriver.chrome.driver' : "${updateChromedriver.driverLocation}"
    ] 

   // your testing code here ...

}
``` 
And run `gradlew updateChromedriver integrationTest` to use the downloaded driver in you Integration Tests.

More about the property `webdriver.chrome.driver` [here](https://sites.google.com/a/chromium.org/chromedriver/getting-started).


### Proxy
When you are behind a proxy, use the http.proxyHost and http.proxyPort JVM parameters
```
gradlew myTask -Dhttp.proxyHost=myproxy.com -Dhttp.proxyPort=8080
```


Just checking
-------------

Run `gradlew checkChromedriver` if you only want to check for a new Chromedriver version. No driver files are downloaded.
This can be used in a daily job in [Jenkins](http://jenkins-ci.org) to inform people by e-mail of a new Chromedriver versions.
Therefore the gradle build will FAIL when a new Chromedriver version is available.


License
-------

The plugin is licensed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Questions
---------
Raise an issue on [Github](Gitbub.com/leontebbens) or [Tweet me](twitter.com/leontebbens).
Suggestions are welcome too!

Léon Tebbens

