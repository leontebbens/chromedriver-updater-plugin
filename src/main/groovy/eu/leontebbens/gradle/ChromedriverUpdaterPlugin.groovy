package eu.leontebbens.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

class ChromedriverUpdaterPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('updateChromedriver', type: ChromedriverUpdaterTask) {
          group = 'Chromedriver'
          checkOnly = false
        }
        project.task('checkChromedriver', type: ChromedriverUpdaterTask) {
          group = 'Chromedriver'
          checkOnly = true
        }
    }
}

