def createBranch(branch) {
    def rc = sh (script: "git checkout -b ${branch}", returnStatus: true)
    return rc == 0
}

def boolean branchExists(branch) {
    def rc = sh (script: "git rev-parse --verify ${branch}", returnStatus: true)
    return rc == 0
}

def boolean deleteBranch(branch) {
    def rc = sh (script: "git branch -D ${branch}", returnStatus: true)
    return rc == 0
}

def boolean pull(branch, remote='origin') {
    def rc = sh (script: "git pull ${remote} ${branch}", returnStatus: true)
    return rc == 0
}


def commit(message, gitName='genie.xtext', gitEmail='genie.xtext@git.eclipse.org') {
    def git_cmd
    print sh(
        script: 'git add -A',
        returnStdout: true
    )
    // return status, but ignore
    sh(
        script: "git commit -a -m '${message}\n\nSigned-off-by: ${gitName} <${gitEmail}>'",
        returnStatus: true
    )

    print sh(
            script: "git show --name-only HEAD",
                returnStdout: true
            )
    
    return git_cmd
}


def void printChanges() {
    git_changes = sh (
        script: 'git show --name-only HEAD',
        returnStdout: true
    ).trim()
    print git_changes
}


def getGitRemote(name = '', type = 'fetch') {
    dir("workDir") {
    gitRemote = sh (
        script: "git remote -v | grep '${name}' | grep ${type} | awk '{print \$2}' | head -1",
        returnStdout: true
    ).trim()
    }
    return gitRemote
}


def tag(tagName) {
    def git_cmd
    git_cmd = sh (
        script: "git tag --force -a ${tagName} -m 'release ${tagName}'",
        returnStdout: true
    ).trim()
    return git_cmd
}


def push(branch, openPR=false) {
    def rc = sh (
        script: "git push --force --tags origin ${branch}",
        returnStatus: true
    )
    /*
    if (rc == 0 && openPR) {
        def message = sh (script: "git log -1 --pretty='format:%s'", returnStdout: true)
        sh(
        script: "hub pull-request -m '**** TEST TEST ${message}'",
        returnStatus: true
        )
        if (rc == 0 && openPR) {
          def message = sh (script: "git log -1 --pretty='format:%s'", returnStdout: true)
          rc = createPullRequest('eclipse',path,message,branch,'master')
        }
        return rc
    }
    */
    return rc
}

def getGitCommit() {
    git_commit = sh (
        script: 'git rev-parse HEAD',
        returnStdout: true
    ).trim()
    return git_commit
}

def resetHard() {
    def git_cmd
      git_cmd = sh (
          script: 'git reset --hard',
          returnStdout: true
      ).trim()
    return git_cmd
}

def checkoutBranch(branchName) {
    def git_cmd
      git_cmd = sh (
          script: "git checkout ${branchName}",
          returnStdout: true
      ).trim()
    return git_cmd
}

/**
 * Creates a Pull Request using GitHub REST API
 * @param repoOwner Repository owner
 * @param repoName Repository name
 * @param title The title of the pull request.
 * @param The name of the branch where your changes are implemented. 
 *        For cross-repository pull requests in the same network, namespace head with a user like this: username:branch.
 * @param The name of the branch you want the changes pulled into. 
 *        This should be an existing branch on the current repository. 
 *        You cannot submit a pull request to one repository that requests a merge to a base of another repository.
 * @see https://developer.github.com/v3/pulls/#create-a-pull-request
 */
def createPullRequest (repoOwner, repoName, title, head, base='master') {
  dir(repoName) {
  def data = "{\"title\":\"${title}\",\"head\":\"${head}\",\"base\",\"${base}\"}"
  //def rc = sh (script: "curl -s -X POST -H 'authToken: ${GITHUB_AUTH_TOKEN}' --data '${data}' https://api.github.com/repos/${repoOwner}/${repoName}/pulls", returnStatus: true)
  def message = sh (script: "git log -1 --pretty='format:%s'", returnStdout: true)
  def rc = sh (script: "hub pull-request -m '${message}' -h head -b base", returnStatus: true)
  println "PR OPEN RC: ${rc}"
  return rc
  }
}

return this
