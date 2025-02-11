package builds

import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.sequential
import jetbrains.buildServer.configs.kotlin.toId
import jetbrains.buildServer.configs.kotlin.triggers.vcs

class Build(
    name: String,
    branchFilter: String,
    forPullRequests: Boolean,
    triggerRules: String? = null
) :
    Project({
      this.id(name.toId())
      this.name = name

      val complete = Empty("${name}-complete", "complete")

      val bts = sequential {
        if (forPullRequests)
            buildType(WhiteListCheck("${name}-whitelist-check", "white-list check"))
        if (forPullRequests) dependentBuildType(PRCheck("${name}-pr-check", "pr check"))

        dependentBuildType(
            Maven(
                "${name}-build",
                "build",
                "sortpom:verify license:check spotless:check compile",
                javaVersion = DEFAULT_JAVA_VERSION,
            ))

        parallel {
          NEO4J_VERSIONS.forEach { version ->
            sequential {
              listOf<Boolean>(true, false).forEach { isEnterprise ->
                dependentBuildType(
                    Maven(
                        "${name}-test-${version}-${if (isEnterprise) "enterprise" else "community"}",
                        "test with $version ${if (isEnterprise) "enterprise" else "community"}",
                        "verify",
                        javaVersion = DEFAULT_JAVA_VERSION,
                        size = LinuxSize.LARGE) {
                          params {
                            text("env.NEO4J_VERSION", version)
                            text("env.ENTERPRISE", isEnterprise.toString())
                          }
                        })
              }
            }
          }
        }

        dependentBuildType(complete)
        if (!forPullRequests)
            collectArtifacts(dependentBuildType(Release("${name}-release", "release")))
      }

      bts.buildTypes().forEach {
        it.thisVcs()

        it.features {
          enableCommitStatusPublisher()
          if (forPullRequests) enablePullRequests()
        }

        buildType(it)
      }

      complete.triggers {
        vcs {
          this.branchFilter = branchFilter
          this.triggerRules = triggerRules
        }
      }
    })
