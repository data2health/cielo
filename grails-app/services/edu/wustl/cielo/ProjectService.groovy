package edu.wustl.cielo

import com.google.cloud.storage.BlobId
import edu.wustl.cielo.enums.ActivityTypeEnum
import edu.wustl.cielo.enums.FileUploadType
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import grails.plugin.cache.Cacheable
import grails.plugin.springsecurity.annotation.Secured
import grails.util.Environment
import groovy.util.logging.Slf4j
import grails.web.mapping.LinkGenerator
import org.apache.commons.lang.RandomStringUtils
import org.springframework.validation.ObjectError
import com.thedeanda.lorem.Lorem
import com.thedeanda.lorem.LoremIpsum
import javax.servlet.http.Part

@Transactional
@Slf4j
class ProjectService {

    //Following used to allot the number of objects to add to a project
    static int numberOfTeamsPerProject = 1
    static int numberOfCodesPerProject = 2
    static int numberOfDatasPerProject = 3
    static int numberOfPublicationsPerProject   = 1
    static int numberOfAnnotationsPerProject    = 2
    static int numberOfCommentsPerProject       = 3
    static Lorem lorem                          = LoremIpsum.getInstance()
    static LinkGenerator grailsLinkGenerator
    static int DEFAULT_MAX      = 5
    static int DEFAULT_OFFSET   = 0

    def activityService
    def messageSource
    def springSecurityService
    def cloudService

    /**
     * Main call used in bootstrap to generated project data
     *
     * @param numberOfProjectsPerUser number of projects to setup
     */
    void bootstrapProjects(int numberOfProjectsPerUser) {

        if (Project.list().size() == 0) {
            List<UserAccount> users = UserAccount.list()
            List<SoftwareLicense> softwareLicenses = SoftwareLicense.list()
            List<Annotation> annotations = Annotation.list()

            users?.each { UserAccount user ->
                log.info('************************************************************')
                log.info("Creating ${numberOfProjectsPerUser} projects for username ${user.username}...")
                log.info('************************************************************')

                numberOfProjectsPerUser.times {
                    Project project = new Project(
                            name: user.username + '-project-' + it,
                            description: lorem.getParagraphs(5,8).substring(0, 254),
                            shared: [true, false].get(new Random().nextInt([true, false].size())),
                            projectOwner: user,
                            license: softwareLicenses?.get(new Random().nextInt(softwareLicenses?.size())),
                            views: new Random().nextInt(6)
                    )

                    if (project.shared && (user.id.toInteger() % 2 == 0)) {
                        def teams = Team.findAllWhere(administrator: user)
                        if (teams) {
                            ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_TEAMS
                            numberOfTeamsPerProject.times {
                                int randomId
                                while (randomId <= 0 || (project?.teams?.id?.contains(randomId))) {
                                    randomId = new Random().nextInt(teams.size())
                                }
                                Team team = teams[randomId]
                                project.addToTeams(team)
                                saveProjectAndLog(project)
                                Object[] params = [user.username, (grailsLinkGenerator.serverBaseURL?:"")
                                        + "/project/${project.id}", project.name]
                                activityService.saveActivityForEvent(activityTypeEnum,
                                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                        params, Locale.getDefault())
                                )
                            }
                        }
                    } else {
                        log.info("\t\tBundle ${project.name} is not shared. Not adding any teams to it...")
                    }
                    bootstrapAnnotationsForProject(project, annotations)
                    bootstrapCodesToProject(project, user.username)
                    bootstrapDatasForProject(project, user.username)
                    bootstrapPublicationsForProject(project, user.username)
                    boostrapCommentsForProject(project)
                }
            }
            log.info("\n")
        }
    }

    /**
     * Add code to a project
     *
     * @param project the project to add code to
     * @param username used to generate the name of the code
     */
    private void bootstrapCodesToProject(Project project, String username) {
        if (project) {
            /* Code for this project */
            log.info("\tCreating ${numberOfCodesPerProject} codes for bundle ${project.name}...")
            numberOfCodesPerProject.times {
                Code code = new Code(
                        name: username + '-code-' + it,
                        description: lorem.getParagraphs(5,8).substring(0, 254),
                        repository: 'http://www.github.com/',
                        revision: it,
                        url: new URL('http://www.www.org')
                )

                if (!code.save()) {
                    code.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("There was an error attempting to save code ${code.name} to ${project.name}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved code: ${code.name}")
                project.addToCodes(code)
                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_CODES
                Object[] params = [username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/project/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
            log.info("\n")
        }
    }

    /**
     * Add annotations to a project
     *
     * @param project the project to add annotations to
     * @param annotations the list of annotations to choose from
     */
    private void bootstrapAnnotationsForProject(Project project, List<Annotation> annotations) {
        def principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal?.id) : UserAccount.findByUsername("admin")

        if (project) {
            log.info("\t\tCreating ${numberOfAnnotationsPerProject} annotations for ${project.name}...\n")
            numberOfAnnotationsPerProject.times {
                Annotation annotation = (annotations?.get(new Random().nextInt(annotations?.size())))
                project.addToAnnotations(annotation)
                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_ANNOTATIONS
                Object[] params = [user.username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/project/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
        }
    }

    /**
     * Add data to a given project
     *
     * @param project the project to add data to
     * @param username used for generating the data name
     */
    private void bootstrapDatasForProject(Project project, String username) {
        /* Data for this bundle */
        if (project) {
            log.info("\tCreating ${numberOfDatasPerProject} data for bundle ${project.name}...")
            numberOfDatasPerProject.times {
                Data data = new Data(
                        name: username + '-data-' + it,
                        description: lorem.getParagraphs(2,5),
                        repository: 'http://www.data.gov/',
                        revision: it,
                        url: new URL('http://www.www.org')
                )

                if (!data.save()) {
                    data.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("There was an error attempting to save data ${data.name} to ${project.name}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved data: ${data.name}")
                project.addToDatas(data)
                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_DATAS
                Object[] params = [username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/project/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
            log.info("\n")
        }
    }

    /**
     * Add publications to a given project
     *
     * @param project the project to add the publications to
     * @param username used to setup the label of publication
     */
    private void bootstrapPublicationsForProject(Project project, String username) {
        /* Publications for this bundle */
        if (project) {
            log.info("\tCreating ${numberOfPublicationsPerProject} publications for bundle ${project.name}...")
            numberOfPublicationsPerProject.times {
                def publication = new Publication(label: username + '-publication-' + it,
                        url: "http://www.${RandomStringUtils.random(5, true, true)}.org",
                        issn: RandomStringUtils.random(5, true, true),
                        isbn: RandomStringUtils.random(5, true, true),
                        nbn: RandomStringUtils.random(5, true, true),
                        doi: RandomStringUtils.random(5, true, true),
                        sici: RandomStringUtils.random(5, true, true),
                        pmid: RandomStringUtils.random(5, true, true),
                        oai: RandomStringUtils.random(5, true, true)
                )

                if (!publication.save()) {
                    publication.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("There was an error attempting to save publication ${publication.label} to ${project.name}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved publication: ${publication.label}")
                project.addToPublications(publication)

                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_PUBLICATIONS
                Object[] params = [username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/project/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
            log.info("\n")
        }
    }

    /**
     * Add comments to a project
     *
     * @param project the project to add comments to
     */
    private void boostrapCommentsForProject(Project project) {
        /* Comments for this bundle */
        if (project) {
            log.info("\tCreating ${numberOfCommentsPerProject} comments for bundle ${project.name}...")
            List<UserAccount> users = UserAccount.list()

            numberOfCommentsPerProject.times {
                UserAccount user = users?.get(new Random().nextInt(users?.size()))
                Comment comment = new Comment(
                        text: lorem.getParagraphs(2,5),
                        commenter: user
                )

                if (!comment.save()) {
                    comment.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("There was an error attempting to save data ${comment.text} to ${project.name}. Exiting.")
                    System.exit(-1)
                }
                log.info("\t\tSaved comments: ${comment.text}")
                project.addToComments(comment)

                ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_COMMENTS
                Object[] params = [user.username, (grailsLinkGenerator.serverBaseURL?:"")
                        + "/project/${project.id}", project.name]
                activityService.saveActivityForEvent(activityTypeEnum,
                        messageSource.getMessage(activityTypeEnum.toString() + "_BODY_TEXT",
                                params, Locale.getDefault()))
            }
            saveProjectAndLog(project)
            log.info("\n")
        }
    }

    /**
     * Save the project in question and log the results
     *
     * @param project the project to save
     */
    private void saveProjectAndLog(Project project) {
        //flush here to avoid problems with subsequent saves
        if (project) {
            project.lastChanged = new Date()
            if (!project.save()) {
                project.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                log.error("There was an error attempting to save project ${project.name}. Exiting.")
                System.exit(-1)
            }
            log.info("\t\tSaved project: ${project.name}\n")
        }
    }

    /**
     * Get the top most viewed projects
     *
     * @param maxNumberOfProjects the number of projects to return
     * @param sharedOnly whether we should filter on sharedOnly
     *
     * @return a list of projects
     */
    @ReadOnly
    @Secured("IS_AUTHENTICATED_ANONYMOUSLY")
    @Cacheable("most_viewed_projects")
    List<Object> getMostViewedProjects(int maxNumberOfProjects, boolean sharedOnly = false) {
        return Project.withCriteria() {
            maxResults(maxNumberOfProjects)
            sharedOnly ? eq("shared", sharedOnly) : null
            order("views", "desc")
            order("dateCreated", "desc")
        }.collect { Project project ->
            [
                    projectId: project.id,
                    projectOwnerUserObject: project.projectOwner,
                    projectOwner: project.projectOwner.fullName,
                    profilePic: project.projectOwner.profile.picture?.fileContents,
                    projectName: project.name,
                    description: project.description,
                    ownerInstitution: project.projectOwner.profile.institution.fullName
            ]
        } as List<Object>
    }

    /**
     * Save a comment to a project
     *
     * @param user the user that is commenting
     * @param projectId the id of the project to save the comment to
     * @param commentStr the comment to save
     *
     * @return true if successful, false otherwise
     */
    boolean saveProjectComment(UserAccount user, Long projectId, String commentStr) {
        boolean saved

        if (user) {
            Project project = Project.findById(projectId)
            Comment comment

            if (project) {
                comment             = new Comment()
                comment.text        = commentStr
                comment.commenter   = user

                if (!comment.save()) {
                    comment.errors.getAllErrors().each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("Unable to save comment.")
                } else {
                    //now add the comment to the project
                    project.addToComments(comment)

                    if (!project.save()) {
                        project.errors.allErrors.each { ObjectError error ->
                            log.error(error.toString())
                        }
                        log.error("There was an error attempting to save project ${project.name}.")
                    } else saved = true
                }

            } else {
                log.error("Project with id ${projectId} was not found")
            }
        }
        return saved
    }

    /**
     * Add a reply to a comment
     *
     * @param user
     * @param commentId
     * @param commentStr
     * @return
     */
    boolean saveProjectCommentReply(UserAccount user, Long commentId, String commentStr) {
        boolean saved

        if (user) {
            Comment comment = Comment.findById(commentId)

            if (comment) {
                Comment reply = new Comment(commenter: user, text: commentStr)

                if (!reply.save()) {
                    reply.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("Unable to save reply.")
                } else {
                    //now add to the parent comment
                    comment.addToResponses(reply)

                    if (!comment.save()) {
                        comment.errors.allErrors.each { ObjectError error ->
                            log.error(error.toString())
                        }
                        log.error("Unable to save comment.")
                    } else saved = true
                }

            } else log.error("Comment with id ${commentId} was not found")
        }
        return saved
    }

    /**
     * Get list of all comments for a given project
     *
     * @param project to get comments from
     *
     * @return a list of Project comments or null list if none
     */
    List<Comment> getComments(Long projectId) {
        return Project.findById(projectId)?.comments
    }

    /**
     * Save changes to a project definition
     *
     * @param projectId the id of the project we are working with
     * @param (optional) newProjectName the projectName to change to. default: current
     * @param (optional) description the description to use. default: current
     * @param (optional) tags the annotations to assign. default: current
     *
     * @return true if successful, false otherwise
     */
    boolean saveProjectBasicChanges(Long projectId, String newProjectName, String description, List<Long> tags,
                                    Long softwareLicenseId, boolean shared) {
        boolean succeeded
        Project project = Project.findById(projectId)

        if (project) {
            if (project.name != newProjectName || project.annotations.collect { it.id } != tags ||
                project.description.toLowerCase() != description.toLowerCase() || project.shared != shared ||
                    project.license.id != softwareLicenseId) {
                if (project.name != newProjectName) {
                    //there are changes
                    project.name = newProjectName
                }

                if (project.annotations.collect { it.id }.sort() != tags.sort()) {
                    List<Long> oldTagIds = project.annotations.collect { it.id }
                    //remove old tags
                    oldTagIds.each {
                        project.annotations.remove(Annotation.findById(it))
                    }

                    //add new tags
                    tags.each {
                        project.annotations.add(Annotation.findById(it))
                    }
                }

                if (project.description.toLowerCase() != description.toLowerCase()) {
                    project.description = description
                }

                if (softwareLicenseId) {
                    project.license = SoftwareLicense.findById(softwareLicenseId)
                }

                project.shared      = shared
                project.lastChanged = new Date()

                //now save the project
                if (!project.save()) {
                    project.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    log.error("Unable to save project with new name and/or tags")
                    succeeded = false //previous step could have succeeded
                } else {
                    succeeded = true
                }
            }
            else succeeded == true //no changes were necessary but no failure so set to true
        }

        return succeeded
    }

    /**
     * Like a project post
     *
     * @param commentId the id of the post to like
     *
     * @return true if successful, false otherwise
     */
    boolean likeProjectComment(Long commentId) {
        boolean succeeded
        def principal       = springSecurityService?.principal
        UserAccount user    = UserAccount.get(principal?.id)
        Comment comment     = Comment.findById(commentId)

        if (user && comment) {
            comment.likedByUsers.add(user)

            if (!comment.save()) {
                comment.errors.getAllErrors().each { ObjectError error ->
                    log.error(error.toString())
                }
            } else succeeded = true
        }

        return succeeded
    }

    /**
     * Remove a like by user to a project comment
     *
     * @param commentId the comment to remove the like from
     *
     * @return true if successful, false otherwise
     */
    boolean removeProjectCommentLike(Long commentId) {
        boolean succeeded
        def principal       = springSecurityService?.principal
        UserAccount user    = UserAccount.get(principal?.id)
        Comment comment     = Comment.findById(commentId)

        if (user && comment) {
            comment.likedByUsers.remove(user)

            if (!comment.save()) {
                comment.errors.getAllErrors().each { ObjectError error ->
                    log.error(error.toString())
                }
            } else succeeded = true
        }

        return succeeded
    }

    /**
     * Increment the project views counter only when necessary
     *
     * @param project the project we are going to check
     */
    void incrementViewsCounter(Project project) {

        if (project) {
            if (!isViewerOwnerOrContributorToProject(project)) {

                project.views = project.views + 1

                if (!project.save()) {
                    project.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                }
            }
        }
    }

    /**
     * Get list of users that have liked the comment
     *
     * @param commentId  the id of the comment
     *
     * @return list of users that have liked the comment
     */
    TreeSet<UserAccount> getUsersWhoLikedComment(Long commentId) {
        return Comment.findById(commentId)?.likedByUsers
    }

    /**
     * Return a list of projects owned by a user
     *
     * @param user the user to filter on
     *
     * @return list of projects the user owns
     */
    List<Project> getMyProjects(UserAccount user, int max, int offset) {
        if (max == -1)      max     = DEFAULT_MAX
        if (offset == -1)   offset  = DEFAULT_OFFSET

        return Project.findAllByProjectOwner(user, [offset: offset * max, max: max, sort: 'dateCreated', order: 'desc'])
    }

    /**
     * Get the number of pages for my projects
     *
     * @param user the user that owns the projects
     * @param max the max number of projects to grab
     *
     * @return the number of pages of my projects per max per page
     */
    int getNumberOfPagesForMyProjects(UserAccount user, int max) {
        if (!max || max <= 0) max = DEFAULT_MAX

        int numberOfProjects = Project.findAllByProjectOwner(user).size()

        if (numberOfProjects == 0 || numberOfProjects <= max) return 1
        else return Math.ceil(numberOfProjects / max).toInteger().intValue()
    }

    /**
     * Delete project
     *
     * @param projectId the id of the project to delete
     *
     * @return true if successful, false otherwise
     */
    boolean deleteProject(UserAccount user, Long projectId) {
        Project project = Project.findById(projectId)

        if (project.projectOwner.equals(user)) {
            try {
                //grab the BlobId for all datas and codes
                List<BlobId> blobIdList = []

                //get all the blobIds for codes
                project.codes.each { code ->
                    blobIdList.add(code.blobId)
                }

                //get all the blobIds for codes
                project.datas.each { data ->
                    blobIdList.add(data.blobId)
                }

                project.delete(failOnError: true)

                //no error so now need to delete all the uploaded files
                blobIdList.each { blobId ->
                    cloudService.deleteFile(blobId)
                }
                return true
            } catch (Exception e) {
                project.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                return false
            }
        } else return false
    }

    /**
     * Get list of projects that are public
     *
     * @return list of projects that are public
     */
    List<Project> getPublicProjects(int offset, int max) {
        if (!offset || offset <= 0) offset  = DEFAULT_OFFSET
        if (!max || max <= 0)       max     = DEFAULT_MAX

        return Project.findAllWhere([shared: true], [offset: offset * max, max: max, sort: 'id', order: 'asc'])
    }

    /**
     * Get the number of pages for public projects
     *
     * @param max the number of items per page
     *
     * @return number indicating how many pages are available
     */
    int getNumberOfPagesForPublicProjects(int max) {
        if (!max || max <= 0) max = DEFAULT_MAX

        int numberOfProjects = Project.findAllByShared(true).size()

        if (numberOfProjects == 0 || numberOfProjects <= max) return 1
        else return Math.ceil(numberOfProjects / max).toInteger().intValue()
    }

    /**
     * Add a new team to an existing project
     *
     * @param user the user who is the administrator for the team
     * @param projectId the id of the project the team belongs to
     * @param teamName the name of the new team
     * @param members the list of ids for the members of the team
     *
     * @return true if successful, false otherwise
     */
    boolean addTeamToProject(UserAccount user, Long projectId, String teamName, List<Long> members) {
        Project project = Project.findById(projectId)

        if (project && members.size() > 0 && teamName) {

            Team team = new Team([name: teamName])
            team.administrator = user

            members.each { userId ->
                UserAccount member = UserAccount.findById(userId)
                if (member) {
                    team.members.add(member)
                }
            }

            if (!team.save()) {
                team.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                return false
            }

            project.teams.add(team)
            project.lastChanged = new Date()

            if (!project.save()) {
                project.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                return false
            }
            return true
        }
    }

    /**
     * Add an existing team to a project
     *
     * @param projectId the id of the project to make the change to
     * @param teamId the id of the team to associate to the project
     *
     * @return true if successful, false otherwise
     */
    boolean addTeamToProject(Long projectId, Long teamId) {
        Project project = Project.findById(projectId)

        if (project) {
            Team team = Team.findById(teamId)
            if (team) {
                project.addToTeams(team)

                if (!project.save()) {
                    project.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    return false
                }
                return true
            }
        }
        return false
    }
    /**
     * Create an empty project with just owner set
     *
     * @param projectOwner the user that is creating the new project
     *
     * @return a project (un-saved)
     */
    Project getNewEmptyProjectForUser(UserAccount owner) {
        return new Project(projectOwner: owner)
    }

    /**
     * Save an uploaded file or a URL to the project
     *
     * @param projectId the id of the project
     * @param type the type of upload
     * @param externalFileLink the external link or null
     * @param filePart the file object or null
     * @param filename the filename or null
     *
     * @return true if save was successful, false otherwise
     */
    boolean addBundleToProject(Long projectId, FileUploadType type, String externalFileLink, Part filePart,
                               String filename, String description) {

        boolean saveProject
        Project project = Project.findById(projectId)

        if (project) {
            URL fileURL
            BlobId blobId
            if (externalFileLink) {
                fileURL = new URI(externalFileLink).toURL()
            } else {
                Map results = cloudService.uploadFile("${project.id}_${filename}", type, filePart)
                fileURL  = new URI(results.url).toURL()
                blobId   = results.blobId
            }

            switch(type) {
                case FileUploadType.DATA:
                    Data data   = new Data()
                    data.name   = filename
                    data.url    = fileURL
                    data.blobId = blobId
                    data.description = description
                    if (blobId) data.repository = "GCS"
                    else data.repository = "EXTERNAL"
                    if (!data.save()) {
                        data.errors.allErrors.each { ObjectError error ->
                            log.error(error.toString())
                        }
                    } else  {
                        saveProject = true
                        project.addToDatas(data)
                    }
                    break
                case FileUploadType.CODE:
                    Code code   = new Code()
                    code.name   = filename
                    code.url    = fileURL
                    code.blobId = blobId
                    code.description = description
                    if (blobId) code.repository = "GCS"
                    else code.repository = "EXTERNAL"
                    if (!code.save()) {
                        code.errors.allErrors.each { ObjectError error ->
                            log.error(error.toString())
                        }
                    } else {
                        saveProject = true
                        project.addToCodes(code)
                    }
                    break
            }

            if (saveProject) {
                project.lastChanged = new Date()
                if (!project.save()) {
                    project.errors.allErrors.each { ObjectError error ->
                        log.error(error.toString())
                    }
                    return false
                }
                return true
            }
            return false
        }
        return false
    }

    /**
     * Save a new project
     *
     * @param projectAdmin the user creating the project
     * @param project the project, bound to params in controller, to save
     * @param annotationIds a list of annotation ids to apply to project
     * @param softwareLicenseId the id of the software license
     * @param teamName (optional) a new team name
     * @param teamMembers (optional) members of team
     *
     * @return true if successful, false otherwise
     */
    boolean saveNewProject(UserAccount projectAdmin, Project project, ArrayList<Long> annotationIds, Long softwareLicenseId,
                           String teamName, ArrayList<Long> teamMembers, Long teamId, Map dataUpload, Map codeUpload) {

        project.projectOwner = projectAdmin

        Team team
        if (teamId != -1L) {
            team = Team.findById(teamId)
            if (team) {
                project.teams.add(team)
            }
        }
        else if (teamName) {
            team = new Team(name: teamName, administrator: projectAdmin)
            teamMembers.each { memberId ->
                UserAccount userAccount = UserAccount.findById(memberId)
                if (userAccount) {
                    team.members.add(userAccount)
                }
            }

            if (!team.save()) {
                team.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                return false
            }
            project.teams.add(team)
        }

        annotationIds.each { id ->
            Annotation annotation = Annotation.findById(id)

            if (annotation) {
                project.annotations.add(annotation)
            }
        }

        SoftwareLicense softwareLicense = SoftwareLicense.findById(softwareLicenseId)
        if (softwareLicense) project.license = softwareLicense

        if (!project.save()) {
            project.errors.allErrors.each { ObjectError error ->
                log.error(error.toString())
            }
            return false
        }

        //use the project id as part of the name of the file to keep unique
        if (dataUpload) {
            if (dataUpload.part) {
                Map results = cloudService.uploadFile("${project.id}_${dataUpload.filename}", dataUpload.type, dataUpload.part)
                String dataURL  = results.url
                BlobId blobId   = results.blobId

                if (dataURL) {
                    project.addToDatas(new Data(url: dataURL, blobId: blobId, name: dataUpload.filename, description: dataUpload.description, repository: "gcs"))
                }
            } else {
                project.addToDatas(new Data(url: dataUpload.url, blobId: null, name: dataUpload.filename, description: dataUpload.description, repository: "external"))
            }
        }

        if (codeUpload) {
            if (codeUpload.part) {
                Map results     = cloudService.uploadFile("${project.id}_${codeUpload.filename}", codeUpload.type, codeUpload.part)
                String codeURL  = results.url
                BlobId blobId   = results.blobId

                if (codeURL) {
                    project.addToCodes(new Code(url: codeURL, blobId: blobId, name: codeUpload.filename, description: codeUpload.description, repository: "gcs"))
                }
            } else {
                project.addToCodes(new Code(url: codeUpload.url, blobId: null, name: codeUpload.filename, description: codeUpload.description, repository: "external"))
            }
        }

        //save again
        if (!project.save()) {
            project.errors.allErrors.each { ObjectError error ->
                log.error(error.toString())
            }
            return false
        }
        return true
    }

    /**
     * Remove a team from the associated project
     *
     * @param user the user that is attempting the change
     * @param teamId the id of the team to remove
     * @param projectId the id of the project to remove the team from,
     *
     * @return true if successful, false otherwise
     */
    boolean removeTeam(UserAccount user, Long teamId, Long projectId) {
        Project project = Project.findById(projectId)
        Team team       = Team.findById(teamId)

        if (team && project && (project.projectOwner.equals(user))) {
            project.removeFromTeams(team)

            if (!project.save()) {
                project.errors.allErrors.each { ObjectError error ->
                    log.error(error.toString())
                }
                return false
            }
            return true
        }
        return false
    }

    /**
     * Delete a bundle from a project
     *
     * @param user the user initiating the change
     * @param projectId the id of the project affected
     * @param bundleId the id of the bundle to remove
     * @param type the type of bundle: either CODE or DATA
     *
     * @return true if successful, false otherwise
     */
    boolean removeBundleFromProject(UserAccount user, Long projectId, Long bundleId, FileUploadType type) {
        Project project = Project.findById(projectId)
        BlobId fileBlobId

        if (project) {
            if (project.projectOwner.equals(user)) {
                boolean saveChanges
                switch (type) {
                   case FileUploadType.CODE:
                       Code code = Code.findById(bundleId)
                       if (code) {
                           fileBlobId = code.blobId
                           project.removeFromCodes(code)
                           saveChanges = true
                       }
                       break
                   case FileUploadType.DATA:
                       Data data = Data.findById(bundleId)
                       if (data) {
                           fileBlobId = data.blobId
                           project.removeFromDatas(data)
                           saveChanges = true
                       }
                       break
                }

                if (saveChanges) {
                    if (!project.save()) {
                        project.errors.allErrors.each { ObjectError error ->
                            log.error(error.toString())
                        }
                        return false
                    }
                    //now delete the file from cloud
                    if (Environment.current != Environment.TEST) {
                        if (fileBlobId) {
                            if (!cloudService.deleteFile(fileBlobId)) return false
                        }
                    }
                    return true
                }
            }
        }
        return false
    }


    /**
     * Is the logged in user the owner or contributor to a project
     *
     * @param project the project to check
     *
     * @return true if the user is the owner or a contributor to a project
     */
    private boolean isViewerOwnerOrContributorToProject(Project project) {

        boolean isOwnerOrContributor
        def principal                   = springSecurityService?.principal
        UserAccount user                = UserAccount.get(principal?.id)
        List<UserAccount> contributors  = new ArrayList<UserAccount>()

        project.teams?.each { Team team ->
            contributors.addAll(team.members)
        }

        //only should increment if the viewing user is not a contributor or owner of project
        if (user && project.projectOwner.equals(user) || contributors.contains(user)) {
            isOwnerOrContributor = true
        }

        return isOwnerOrContributor
    }
}
