package edu.wustl.cielo

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Blob
import com.google.cloud.storage.Acl
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import edu.wustl.cielo.enums.FileUploadType
import javax.servlet.http.Part
import grails.web.mapping.LinkGenerator
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.eclipse.jgit.revwalk.RevCommit
import grails.gorm.transactions.Transactional

@Transactional
class CloudService {
    def grailsApplication
    def userAccountService
    LinkGenerator grailsLinkGenerator

    String BUCKET_NAME
    String GCS_MOUNT_ROOT_DIR
    String APP_ROOT
    String SERVER_BASE_URL
    Storage storage

    void init() {
        BUCKET_NAME         = grailsApplication.config.getProperty("GCS.BUCKET_NAME")
        GCS_MOUNT_ROOT_DIR  = grailsApplication.config.getProperty("GCS.MOUNT_ROOT_DIR")
        APP_ROOT            = grailsApplication.mainContext.servletContext.getRealPath('/')
        SERVER_BASE_URL     = grailsLinkGenerator.serverBaseURL
        storage = StorageOptions.getDefaultInstance().getService()
    }

    /**
     * Upload a file to google cloud - using Storage class (old way)
     *
     * @param projectId the id of the project the file belongs to
     * @param fileName the name of the file
     * @param filePart the file
     *
     * @return return the downloadlink for the upload
     */
    Map uploadFile(Long projectId, String fileName, FileUploadType type, Part filePart) {
        // Modify access list to allow all users with link to upload file
        String subFolder = type.toString().toLowerCase()
        List<Acl> acls = new ArrayList<>()
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))

        Blob blob =
                storage.create(
                        BlobInfo.newBuilder(BUCKET_NAME, subFolder + File.separator + fileName).setAcl(acls).build(),
                        filePart.getInputStream())

        // return the public download link
        return [url: blob.getMediaLink(), blobId: blob.getBlobId()]
    }


    /**
     * Upload a file to google cloud
     *
     * @param projectId the id of the project the file belongs to
     * @param fileName the name of the file
     * @param filePart the file
     *
     * @return return the downloadlink for the upload
     */
    Map uploadFile(Long projectId, String fileName, Part filePart) {

        String commitHash
        URL url

        if (!APP_ROOT) init()

        //check if remote repo does not exist
        if (!projectGitRepoExists(projectId, true)) {
            //create remote repo
            createProjectGitRepo(projectId, true)
        }

        boolean localGitRepoExists = projectGitRepoExists(projectId, false)
        //if local repo does not exist then create and initialize
        if (!localGitRepoExists) {
            localGitRepoExists = createProjectGitRepo(projectId, false)
        }

        if (localGitRepoExists) {
            //get the File path so that we can check in a file to that location
            String path = getLocalRepoPath(projectId)
            File checkInFile = new File(path + File.separator + fileName)

            if (checkInFile) {
                File repo = new File(path)
                //now need to push to remote branch
                commitHash = commitFile(checkInFile, repo, filePart.inputStream.bytes)

                if (commitHash) {
                    if (!remoteOriginAvailable(projectId, repo)) {
                        addRemoteOrigin(projectId, repo)
                    }

                    if (!isUpstreamSetOnRepo(repo)) {
                        log.debug("Upstream was not set. Going to set that up now.")
                        setupUpstreamOnRepo(repo)
                    }
                    runExternalProcess("git push origin master", repo)
                }

                url = new URI(SERVER_BASE_URL + grailsLinkGenerator.link(controller: "project", action: "downloadFile", id: projectId,
                        params: [name: fileName, hash: commitHash])).toURL()
            }
        }

        return [url: url, blobId: null]
    }

    /**
     * Delete the uploaded file
     *
     * @param blobId the BlobId of file to delete
     *
     * @return true if successful false otherwise
     */
    boolean deleteFile(BlobId blobId) {
        if (!BUCKET_NAME) init()

        return storage.delete(blobId)
    }

    /**
     * Remove the file from the
     *
     * @param filename
     * @param projectId
     *
     * @return
     */
    boolean deleteFile(String filename, Long projectId) {
        /**TODO: once bundles are defined and the UI is changed to reflect a list of files, need to
         * ensure that we delete the file from the Git repo the issue here is whether we checkout the original at the
         * given point in history before the check in for that file and then push that to master so there is no history
         * for that. Either way, requires more thought and effort into how to manage. For the time being, deleting just
         * removes the reference from the project which is sufficient
        **/
    }

    /**
     * Check to see if the project git remote repo folder exists
     *
     * @param projectId the id of the project in question
     *
     * @return true if successful, false otherwise
     */
    boolean projectGitRepoExists(Long projectId, boolean remoteRepo) {
        //check to see if in the root there is a folder for the project if there is then return true else false
        boolean exists = false
        String path

        if (!APP_ROOT) init()

         if (remoteRepo) {
             path = GCS_MOUNT_ROOT_DIR + File.separator + Constants.REPO_SUBDIR + File.separator + projectId.toString()
             if (new File(path).exists()) {
                 exists = (new File(path + File.separator + "HEAD")).exists()
             }
         } else {
             path = APP_ROOT + 'WEB-INF' + File.separator + Constants.REPO_SUBDIR  + File.separator + projectId.toString() +
                     File.separator + Constants.REPO_MASTER
             exists = (new File(path + File.separator + ".git")).exists()
         }

        return exists
    }

    /**
     * Create and initialize git repo
     *
     * @param projectId the id of the project to create and initialize repo for
     *
     * @return true if successful
     */
    boolean createProjectGitRepo(Long projectId, boolean isRemoteRepo) {
        boolean successful = false
        File folder = createProjectSubdir(projectId, isRemoteRepo)

        if (folder) successful = initGitRepo(folder, isRemoteRepo)

        return successful
    }

    /**
     * Create the subfolder
     *
     * @param projectId the project id to use to create folder
     *
     * @return instance of a File
     */
    File createProjectSubdir(Long projectId, boolean remoteRepo) {
        File projectFolder

        if (!APP_ROOT) init()

        if (remoteRepo) {
            projectFolder = new File(GCS_MOUNT_ROOT_DIR + File.separator +
                    Constants.REPO_SUBDIR + File.separator + projectId.toString())
        } else {
            projectFolder = new File(APP_ROOT + File.separator + 'WEB-INF' + File.separator + "repos" +
                    File.separator + projectId.toString() +
                    File.separator + Constants.REPO_MASTER)
        }

        projectFolder.mkdirs()

        return projectFolder
    }

    /**
     * Initialize the file path as a bare git repo
     *
     * @param projectGitFolder the folder to init
     *
     * @return true if successful, false otherwise
     */
    boolean initGitRepo(File projectGitFolder, boolean isBareRepo) {
        boolean succeeded = false

        if (projectGitFolder) {

            if (isBareRepo) {

                //WORKAROUND: the jgit library attempts to create sym links which are not allowed in GCS
                //need to call git CLI directly. The reason this happens is because the jgit library calls FS.getDefault()
                //which is not the same as the FS provided by GCS
                runExternalProcess("git init --bare", projectGitFolder)

                succeeded = new File(projectGitFolder.path.toString() + File.separator + "HEAD").exists()
            } else {
                InitCommand initCommand = Git.init()
                initCommand.setDirectory(projectGitFolder)
                initCommand.call()

                succeeded = new File(projectGitFolder.path.toString() + File.separator + ".git").exists()
            }
        }
        return succeeded
    }

    /**
     * Get the path to the local repo
     *
     * @param projectId the id of the project that we need to get the repo path for
     *
     * @return a string representation of the path
     */
    String getLocalRepoPath(Long projectId) {
        if (!APP_ROOT) init()

        return (APP_ROOT + 'WEB-INF' + File.separator + "repos" +
                File.separator + projectId.toString() +
                File.separator + Constants.REPO_MASTER)
    }

    /**
     * Get the path to the remote repo
     *
     * @param projectId the id of the project that we need to get the repo path for
     *
     * @return string representation of the path to the remote repo
     */
    String getRemoteRepo(Long projectId) {
        if (!GCS_MOUNT_ROOT_DIR) init()

        return (GCS_MOUNT_ROOT_DIR + File.separator +
                Constants.REPO_SUBDIR + File.separator + projectId.toString())
    }

    /**
     * Commit a given file to a repo
     *
     * @param fileToCommit file to commit
     * @param repo the file instance of repo
     * @param content the content to add to fileToCommit
     *
     * @return the hash of the commit
     */
    String commitFile(File fileToCommit, File repo, byte[] content) {

        fileToCommit.bytes = content
        Git git = Git.open(repo)
        git.add().addFilepattern(fileToCommit.name).call()
        UserAccount loggedInUser = userAccountService.getLoggedInUser()
        RevCommit revCommit = git.commit().setMessage("Adding " + fileToCommit.name)
                .setAuthor(loggedInUser.fullName, loggedInUser.profile.emailAddress)
                .call()

        return revCommit?.id?.name
    }

    /**
     * Check whether the repo has a remote origin
     *
     * @param projectId the project id which is used to define the directory to look in
     * @param repo the repo that we need to check
     *
     * @return true if remote origin is setup false otherwise
     */
    boolean remoteOriginAvailable(Long projectId, File repo) {
        //don't use runExternalProcess here because we are checking the output and not the err.text
        Process remoteRepoList = "git remote -v".execute([], repo)
        String output

        synchronized (remoteRepoList){
            remoteRepoList.wait()
            output = remoteRepoList.text
        }

        return output.indexOf("/${projectId}") != -1
    }

    /**
     * Add remote origin to a given repo
     *
     * @param projectId the id of the project the repo is for
     * @param repo the repo to setup remote origin on
     */
    void addRemoteOrigin(Long projectId, File repo) {
        runExternalProcess("git remote add origin ${getRemoteRepo(projectId)}", repo)
    }

    /**
     * Check whether upstream is setup on local repo
     *
     * @param repo the repo to check
     *
     * @return true if it's setup, false otherwise
     */
    boolean isUpstreamSetOnRepo(File repo) {
        boolean successful
        Process process = "git branch -rvv".execute([], repo)

        synchronized (process) {
            process.waitFor()
        }

        if (process.exitValue() != 0) {
            log.error("Error ${process.exitValue()}: ${process.err.text}")
        } else {
            if (!process.text == "") {
                successful = true
            }
        }

        return successful
    }

    /**
     * Setup upstream on local repo
     *
     * @param repo the repo to setup the upstream on
     */
    void setupUpstreamOnRepo(File repo) {
        runExternalProcess("git push --set-upstream origin master", repo)
    }

    /**
     * Download a given file from repository
     *
     * @param projectId the id of the project
     * @param fileName the name of the file we want
     * @param gitCommitHash the commit hash for the file
     *
     * @return inputstream of the file
     */
    byte[] downloadFile(Long projectId, String fileName, String gitCommitHash) {
        String pathToRepo = getLocalRepoPath(projectId)
        String currentHash
        byte[] arrayOfBytes

        //now we need to see if the current hash is same if so, then just grab the latest file
        //if not, then need to archive the gitCommitHash to another repo and grab the file there... unless,
        //we already did that before, in which case just grab it there first
        currentHash = getHashForHead(pathToRepo)

        if (currentHash.equalsIgnoreCase(gitCommitHash)) {
            arrayOfBytes = new FileInputStream(pathToRepo + File.separator + fileName).bytes
        } else {
            //need to see if someone has already downloaded this file, if so, no need to do anything but brag the file
            File pathToCommit = new File(getPathToHashCommitCheckoutDirectory(projectId, gitCommitHash))

            if (pathToCommit.exists() && new File(pathToCommit.absolutePath.toString() + File.separator + fileName).exists()) {
                arrayOfBytes = new FileInputStream(pathToCommit.absolutePath.toString() + File.separator + fileName).bytes
            } else {
                //create the directory first
                pathToCommit.mkdirs()

                //need to check out the commit first then get the file
                runExternalProcess("git archive --format=tar ${gitCommitHash} " +
                        "-o ${pathToCommit.absolutePath + File.separator + gitCommitHash + ".tar"}", new File(pathToRepo))

                File tarFile = new File(pathToCommit.absolutePath + File.separator + gitCommitHash + ".tar")

                if (pathToCommit.exists() && tarFile.exists()) {
                    //untar the archive file
                    runExternalProcess("tar -xvf ${tarFile.name}", new File(pathToCommit.absolutePath + File.separator))

                    //delete the tar file since its no longer needed no need to wait for it to complete
                    tarFile.delete()

                    InputStream requestedFile = new FileInputStream(pathToCommit.absolutePath + File.separator + fileName)
                    arrayOfBytes = requestedFile.bytes
                    requestedFile.close()
                }
            }
        }
        return arrayOfBytes
    }

    /**
     * Get the hash for the current state of the repo
     *
     * @param pathToLocalRepo the local repository to check
     *
     * @return the current commit hash of the repository
     */
    String getHashForHead(String pathToLocalRepo) {

        Process process = "git show-ref --hash --heads".execute([], new File(pathToLocalRepo))

        synchronized (process) {
            process.waitFor()
        }

        return process.text.trim()
    }

    /**
     * Get the path for where the hash commit checkout should be
     *
     * @param projectId the project id for the project we care about
     * @param gitCommitHash the hash of the commit we care about
     *
     * @return the path to the directory that should have the file the user is looking for
     */
    String getPathToHashCommitCheckoutDirectory(Long projectId, String gitCommitHash) {
        if (!APP_ROOT) init()

        return (APP_ROOT + 'WEB-INF' + File.separator + "repos" +
                File.separator + projectId.toString() +
                File.separator + gitCommitHash)
    }

    /**
     * Run a process logging any errors
     *
     * @param command the command you want to run
     * @param workingDirectory the working directory or null
     *
     * @return true if no error else false
     */
    boolean runExternalProcess(String command, File workingDirectory) {
        boolean successful = false
        Process process

        if (workingDirectory) {
            process = command.execute([], workingDirectory)
        } else {
            process = command.execute()
        }

        synchronized (process) {
            process.waitFor()
        }

        if (process.exitValue() != 0) {
            log.error("Error ${process.exitValue()}: ${process.err.text}")
        } else successful = true

        return successful
    }

    /**
     * Delete the local & remote repository if it exists
     *
     * @param projectId the id of the project that we want to delete
     *
     * @return true if successul, false otherwise
     */
    boolean deleteRepo(Long projectId) {
        boolean succeeded = true

        if (projectGitRepoExists(projectId, false)) {
            //honestly, nothing fancy needed here. Just delete the directory
            String localPath = getLocalRepoPath(projectId) - (File.separator + Constants.REPO_MASTER)
            File localRepo = new File(localPath)
            localRepo.deleteDir()

            if (localRepo.exists()) succeeded = false

        }

        //only continue if the previous step did not fail
        if (projectGitRepoExists(projectId, true) && succeeded) {
            //Again, nothing fancy needed. Just delete the directory for remote
            String remotePath = getRemoteRepo(projectId)
            File remoteRepo = new File(remotePath)
            remoteRepo.deleteDir()

            if (remoteRepo.exists()) succeeded = false
        }

        return succeeded
    }
}
