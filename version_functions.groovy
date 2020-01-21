def getLatestArtifactVersion(groupId, artifactId) {
  return sh (script: "curl -s http://search.maven.org/solrsearch/select?q=g:\"${groupId}\"+AND+a:\"${artifactId}\" |grep -Po 'latestVersion.:.\\K[^\"]*'", returnStdout: true).trim()
}

def getLatestReleaseFromGitHubRepository (owner, repository) {
  return sh (script: "curl -s curl https://api.github.com/repos/${owner}/${repository}/releases/latest | grep -Po '\"name\"[^\\d]*\\K[\\d\\.]*'", returnStdout: true).trim()
}

/**
 * Fetch the latest Orbit repository URL
 * @param buildType R=Release, S=Stable, I=Integration
 */
def getLatestOrbitUrl (buildType) {
  assert ['R','S','I'].contains(buildType)
  def repoID= sh (script: "curl -s https://download.eclipse.org/tools/orbit/downloads/ |grep -m1 -Po 'drops/\\K${buildType}\\d+'", returnStdout: true).trim()
  def repoURL = "http://download.eclipse.org/tools/orbit/downloads/drops/${repoID}/repository"
  return repoURL
}

def getXtextTychoVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-eclipse/${branch}/releng/org.eclipse.xtext.tycho.parent/pom.xml |grep -Po '<tycho-version>\\K[^<]*'", returnStdout: true).trim()
}

def getXtextGradlePluginVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-lib/${branch}/gradle/versions.gradle |grep -Po 'xtext_gradle_plugin[^\\d]*\\K[\\d\\.]*'", returnStdout: true).trim()
}

/**
 * Get a version from 'versions.gradle' file
 * @param id Version identifier from ext.versions, e.g. 'xtext_gradle_plugin'
 * @param branch (Optional) Branch on GH repository to check
 * @param repository (Optional) Xtext repository name on GH
 */
def getVersionFromGradleVersions (id,branch='master',repository='xtext-lib') {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/${repository}/${branch}/gradle/versions.gradle |grep -Po \"${id}[^\\d]*\\K[^']*\"", returnStdout: true).trim()
}

/**
 * Grep an artifact version from a remote pom.xml file.
 * It is assumed that the version tag is in the line following the artifactId tag.
 */
def getArtifactVersionFromPOM (url, artifactId) {
  // first grep for <artifactId> and the line after
  // then grep the result for <version> tag
  return sh (script: "curl -s ${url} |grep \"<artifactId>${artifactId}</artifactId>\" -A 1 |grep -Po \"<version>\\K[^<]*\"", returnStdout: true).trim()
}

def getXtextGradleVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-lib/${branch}/gradle/wrapper/gradle-wrapper.properties |grep -Po 'distributionUrl=.*/gradle-\\K[\\d\\.]*'", returnStdout: true).trim()
}

def getXtextBootstrapVersion (branch) {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-lib/${branch}/gradle/versions.gradle |grep -Po 'xtext_bootstrap[^\\d]*\\K[\\d\\.]*'", returnStdout: true).trim()
}

/**
 * Get a version configured in Xtext's dev-bom BOM.
 * @param branch (Optional) Branch on GH repository to check
 * @param id groupId:artifactId. For example 'org.eclipse.platform:org.eclipse.core.commands'
 */

def getVersionFromBOM (id, branch='master') {
  return sh (script: "curl -s https://raw.githubusercontent.com/eclipse/xtext-lib/${branch}/org.eclipse.xtext.dev-bom/build.gradle |grep -Po 'api \\\"${id}:\\K[^\"]*'", returnStdout: true).trim()
}

return this
