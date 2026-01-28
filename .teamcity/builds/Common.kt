package builds

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script

const val GITHUB_OWNER = "neo4j"
const val GITHUB_REPOSITORY = "caniuse"
const val MAVEN_DEFAULT_ARGS = "--no-transfer-progress --batch-mode --show-version"

const val FULL_GITHUB_REPOSITORY = "$GITHUB_OWNER/$GITHUB_REPOSITORY"
const val GITHUB_URL = "https://github.com/$FULL_GITHUB_REPOSITORY"

const val DEFAULT_JAVA_VERSION = "11"

val NEO4J_VERSIONS =
    listOf<String>(
        "4.4",
        "5.1",
        "5.2",
        "5.3",
        "5.4",
        "5.5",
        "5.6",
        "5.7",
        "5.8",
        "5.9",
        "5.10",
        "5.11",
        "5.12",
        "5.13",
        "5.14",
        "5.15",
        "5.16",
        "5.17",
        "5.18",
        "5.19",
        "5.20",
        "5.21",
        "5.22",
        "5.23",
        "5.24",
        "5.25",
        "5.26.0",
        "5.26.1",
        "5.26.2",
        "5.26.3",
        "5.26.4",
        "5.26.5",
        "5.26.6",
        "5.26.7",
        "5.26.8",
        "5.26.9",
        "5.26.10",
        "5.26.11",
        "5.26.12",
        "5.26.13",
        "5.26.14",
        "5.26.15",
        "5.26.16",
        "5.26.17",
        "5.26.18",
        "5.26.19",
        "5.26.20",
        "2025.01",
        "2025.02",
        "2025.03",
        "2025.04",
        "2025.05",
        "2025.06",
        "2025.07",
        "2025.08",
        "2025.09",
        "2025.10",
        "2025.11",
        "2025.12",
    )

const val SEMGREP_DOCKER_IMAGE = "semgrep/semgrep:1.146.0"

enum class LinuxSize(val value: String) {
  SMALL("small"),
  LARGE("large")
}

fun Requirements.runOnLinux(size: LinuxSize = LinuxSize.SMALL) {
  startsWith("cloud.amazon.agent-name-prefix", "linux-${size.value}")
}

fun BuildType.thisVcs() = vcs {
  root(DslContext.settingsRoot)

  cleanCheckout = true
}

fun BuildFeatures.enableCommitStatusPublisher() = commitStatusPublisher {
  vcsRootExtId = DslContext.settingsRoot.id.toString()
  publisher = github {
    githubUrl = "https://api.github.com"
    authType = personalToken { token = "%github-commit-status-token%" }
  }
}

fun BuildFeatures.enablePullRequests() = pullRequests {
  vcsRootExtId = DslContext.settingsRoot.id.toString()
  provider = github {
    authType = token { token = "%github-pull-request-token%" }
    filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
  }
}

fun CompoundStage.dependentBuildType(bt: BuildType) =
    buildType(bt) {
      onDependencyCancel = FailureAction.CANCEL
      onDependencyFailure = FailureAction.FAIL_TO_START
    }

fun collectArtifacts(buildType: BuildType): BuildType {
  buildType.artifactRules =
      """
        +:target/staging-deploy => packages
    """
          .trimIndent()

  return buildType
}

fun BuildSteps.runMaven(
    javaVersion: String = DEFAULT_JAVA_VERSION,
    init: MavenBuildStep.() -> Unit
): MavenBuildStep {
  val maven =
      this.maven {
        dockerImagePlatform = MavenBuildStep.ImagePlatform.Linux
        dockerImage = "eclipse-temurin:${javaVersion}-jdk"
        dockerRunParameters = "--volume /var/run/docker.sock:/var/run/docker.sock"
      }

  init(maven)
  return maven
}

fun BuildSteps.setVersion(name: String, version: String): MavenBuildStep {
  return this.runMaven {
    this.name = name
    goals = "versions:set"
    runnerArgs = "$MAVEN_DEFAULT_ARGS -DnewVersion=$version -DgenerateBackupPoms=false"
  }
}

fun BuildSteps.commitAndPush(
    name: String,
    commitMessage: String,
    includeFiles: String = "\\*pom.xml",
    dryRunParameter: String = "dry-run"
): ScriptBuildStep {
  return this.script {
    this.name = name
    scriptContent =
        """
          #!/bin/bash -eu              
         
          git add $includeFiles
          git commit -m "$commitMessage"
          git push
        """
            .trimIndent()

    conditions { doesNotMatch(dryRunParameter, "true") }
  }
}
