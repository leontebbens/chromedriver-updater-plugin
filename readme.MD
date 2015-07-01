Chromedriver Updater plugin
===========================

This is a simple plugin for [Gradle](http://www.gradle.org/) to keep your [Chromedriver](https://sites.google.com/a/chromium.org/chromedriver/) for [Selenium](http://seleniumhq.org) up to date.
It checks the [google code website](http://chromedriver.storage.googleapis.com/index.html) for a new version of the Chromedriver, and downloads it to the build directory.

The plugin has been successfully tested with Gradle 2.1 on Mac OSX.
It should work with newer versions and on other operations systems as well.


Apply plugin configuration
--------------------------

### Gradle 2.1 and higher

Put this in your `build.gradle`:

```groovy
plugins {
    id "eu.leontebbens.gradle.chromedriver-updater" version "1.1"
}
```


Usage
-----

After you applied the plugin configuration (see above) you can use the `ChromedriverUpdaterTask` task as follows in your `build.gradle``:

```groovy
import eu.leontebbens.gradle.ChromedriverUpdaterTask

task myTask(type: ChromedriverUpdaterTask) {
}
```

Next run `gradlew myTask` and the drivers will be downloaded to the directory `build`



Just checking
-------------

Set property `checkOnly=true` if you only want to check for a new Chromedriver version. No driver files are downloaded.
This can be used in a daily job in [Jenkins](http://jenkins-ci.org) to inform people by e-mail of a new Chromedriver versions.
Therefore the gradle build will FAIL when a new Chromedriver version is available.

```groovy
import eu.leontebbens.gradle.ChromedriverUpdaterTask

task myTask(type: ChromedriverUpdaterTask) {
    checkOnly = true
}
```


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

