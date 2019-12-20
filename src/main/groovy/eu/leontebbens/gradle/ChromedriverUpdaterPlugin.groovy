package eu.leontebbens.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

class ChromedriverUpdaterPluginExtension {
    String majorVersion = ""
}

class ChromedriverUpdaterPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('chromedriver', ChromedriverUpdaterPluginExtension)

        project.task('updateChromedriver', type: ChromedriverUpdaterTask) {
            group = 'Chromedriver'
            checkOnly = false
            doFirst {
                println("Major version: $extension.majorVersion")
                majorVersion = extension.majorVersion
            }
        }
        project.task('checkChromedriver', type: ChromedriverUpdaterTask) {
            group = 'Chromedriver'
            checkOnly = true
            doFirst {
                println("Major version: $extension.majorVersion")
                majorVersion = extension.majorVersion
            }
        }
    }
}

