Chromedriver Updater plugin
===========================

This is a simple plugin for [Gradle](http://www.gradle.org/) to keep your [Chromedriver](https://sites.google.com/a/chromium.org/chromedriver/) for [Selenium](http://seleniumhq.org) up to date.
It checks the [google code website](http://chromedriver.storage.googleapis.com/index.html) for a new version of the Chromedriver, and downloads it to the build directory.

The plugin has been successfully tested with Gradle 2.1 on Mac OSX.
It should work with newer versions and on other operations systems as well.


Apply plugin configuration
--------------------------

Put this in your `build.gradle` (Gradle 2.1 or higher):

```groovy
plugins {
    id "eu.leontebbens.gradle.chromedriver-updater" version "1.2"
}
```


Usage
-----

After you applied the plugin configuration (see above) you can use the `ChromedriverUpdaterTask` task as follows in your `build.gradle`:

```groovy
apply plugin: "eu.leontebbens.gradle.chromedriver-updater"
```

Next run `gradlew updateChromedriver` and the drivers will be downloaded to the directory `build`


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

