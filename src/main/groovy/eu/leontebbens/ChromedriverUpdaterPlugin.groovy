package eu.leontebbens

import org.gradle.api.Project
import org.gradle.api.Plugin
import eu.leontebbens.ChromedriverUpdaterTask


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

