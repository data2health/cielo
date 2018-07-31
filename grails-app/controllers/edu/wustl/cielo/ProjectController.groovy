package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import edu.wustl.cielo.enums.FileUploadType
import org.springframework.security.access.AccessDeniedException
import javax.servlet.http.Part

class ProjectController {

    ProjectService projectService
    def springSecurityService
    def messageSource
    def cloudService

    static allowedMethods = [save: "POST"]

    /**
     * Call to service that retrieves the most 'popular' projects
     *
     * @return a list of properties for most popular projects
     */
    @Secured('permitAll')
    def getMostPopularProjects() {
        List<Object> popularBundles

        if (params.max && params.publicOnly) {

            popularBundles = projectService.getMostViewedProjects(Integer.valueOf(params.max),
                    Boolean.valueOf(params.publicOnly))
        }
        render([projects: popularBundles] as JSON)
    }

    @Secured('isAuthenticated()')
    def view() {
        Object principal    = springSecurityService.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null
        Project project
        boolean showTeams
        boolean showBundles

        try {
            project = projectService.getProject(Long.valueOf(params.id))
        } catch (AccessDeniedException ade) {
            log.error(ade.message)
        }

        if (!project) {
            redirect(controller: "errors", action: "denied")
        } else {
            showTeams   = params.teams ? Boolean.valueOf(params.teams) : false
            showBundles = params.bundles ? Boolean.valueOf(params.bundles) : false

            projectService.incrementViewsCounter(project)

            return [userProfile: user.profile,
                    annotations: Annotation.list(),
                    project: project,
                    usersAccess: projectService.getListOfUsersAccess(project),
                    showTeams: showTeams,
                    showBundles: showBundles]
        }
    }

    @Secured('isAuthenticated()')
    def saveProjectComment() {
        Map messages = [:]
        boolean succeeded
        Long projectId = Long.valueOf(params.id)
        String commentStr = params.comment
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (projectId && commentStr && user) {
            succeeded = projectService.saveProjectComment(user, projectId, commentStr)
        }

        if (!succeeded) messages.danger = messageSource.getMessage('project.comment.save.failure', null,
                'Unable to save project comment', request.locale)
        render ([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def saveCommentReply () {
        Map messages = [:]
        boolean succeeded
        Long commentId = Long.valueOf(params.id)
        String commentStr = params.reply
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (commentId && commentStr && user) {
            succeeded = projectService.saveProjectCommentReply(user, commentId, commentStr)
        }

        if (!succeeded) messages.danger = messageSource.getMessage('project.comment.save.failure', null,
                'Unable to save project comment', request.locale)
        render ([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def getProjectComments() {

        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null
        Project project = Project.findById(Long.valueOf(params.id))

        render (template: "/project/projectComments",
                model: [project: project, user: user])

    }

    @Secured('isAuthenticated()')
    def saveProjectBasicChanges() {
        Map messages = [:]
        boolean succeeded
        Long projectId = Long.valueOf(params.id)

        if (projectId) {
            String newName          = params.name
            String description      = params.desc
            List<Long> tags         = []
            Long softwareLicenseId
            boolean shared

            if (params."tags[]")    {
                if (params."tags[]".class.simpleName == "String") tags.add( Long.valueOf(params."tags[]") )
                else {
                    params."tags[]".each { stringId ->
                        tags.add(Long.valueOf(stringId))
                    }
                }
            }
            if (params.licenseId)   softwareLicenseId  = Long.valueOf(params.licenseId)
            if (params.shared)      shared = Integer.valueOf(params.shared) ==  1

            succeeded = projectService.saveProjectBasicChanges(projectId, newName, description, tags,
                    softwareLicenseId, shared)
        }
        if (succeeded) messages.success = messageSource.getMessage('project.creation.succeeded', null, 'Successfully saved project',
                request.locale)
        else messages.danger = messageSource.getMessage('project.creation.failed', null, 'Unable to save project',
                request.locale)
        render ([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def likeComment() {
        boolean succeeded
        Long commentId = Long.valueOf(params.id)

        if (commentId) {
            succeeded = projectService.likeProjectComment(commentId)
        }
        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def removeCommentLike() {
        boolean succeeded
        Long commentId = Long.valueOf(params.id)

        if (commentId) {
            succeeded = projectService.removeProjectCommentLike(commentId)
        }
        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def getCommentLikeUsers() {
        TreeSet<UserAccount> users
        Long commentId = Long.valueOf(params.id)

        if (commentId) {
           users = projectService.getUsersWhoLikedComment(commentId)
        }
        render(template: "/templates/commentLikesUsers", model: [users: users])
    }


    @Secured('isAuthenticated()')
    def myProjects() {
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null
        List<Project> myProjects
        int max     = -1
        int offset  = -1

        if (user) {
            if (params.max)     max     = Integer.valueOf(params.max)
            if (params.offset)  offset  = Integer.valueOf(params.offset)

            myProjects = projectService.getMyProjects(user, max, offset)
        }

        return [userProfile: user?.profile, projects: myProjects, offset: (offset <= 0 ? 1 : (offset + 1)),
                numberOfPages: projectService.getNumberOfPagesForMyProjects(user, max)]
    }

    @Secured('isAuthenticated()')
    def publicProjectsList() {
        int max     = -1
        int offset  = -1
        List<Project> projects
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (params.max)     max     = Integer.valueOf(params.max)
        if (params.offset)  offset  = Integer.valueOf(params.offset)

        projects = projectService.getPublicProjects(offset, max)

        return [projects: projects, userProfile: user?.profile,
                offset: offset <= 0 ? 1 : offset,
                numberOfPages: projectService.getNumberOfPagesForPublicProjects(max),
                max: max]
    }

    @Secured('isAuthenticated()')
    def deleteProject() {
        Map messages = [:]
        boolean succeeded
        Long projectId = params.id ? Long.valueOf(params.id) : -1L
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (user) {
            succeeded = projectService.deleteProject(user, projectId)
        }

        if (!succeeded) messages.danger = messageSource.getMessage("project.deletion.failed", null, Locale.getDefault())
        else messages.success = messageSource.getMessage("project.deletion.succeeded", null, Locale.getDefault())
        render([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def addTeamToProject() {
        boolean succeeded
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null
        String teamName
        Long projectId = params.id ? Long.valueOf(params.id) : -1L
        List<Long> userIds = []

        if (params.teamId) {
            Long teamId = Long.valueOf(params.teamId)
            succeeded   = projectService.addTeamToProject(projectId, teamId)
        } else {
            teamName = params.name

            if (params."members[]".class.simpleName == "String[]") {
                userIds = params."members[]"
            } else {
                userIds.add(params."members[]")
            }

            if (user) {
                succeeded = projectService.addTeamToProject(user, projectId, teamName, userIds)
            }
        }
        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def getTeams() {
        Long projectId = params.id ? Long.valueOf(params.id) : -1L

        render(template: "/project/teams", model: [project: Project.findById(projectId)])
    }

    @Secured('isAuthenticated()')
    def newProject() {
        Object principal        = springSecurityService.principal
        UserAccount user        = principal ? UserAccount.get(principal.id) : null

       render(template: "newProjectWizard", model: [annotations: Annotation.list(),
                                                    licences: SoftwareLicense.list(),
                                                    users: UserAccount.list() - user,
                                                    teams: Team.list()])
    }

    @Secured('isAuthenticated()')
    def saveProject() {
        Map messages = [:]
        boolean succeeded
        String teamName
        Object principal        = springSecurityService.principal
        UserAccount user        = principal ? UserAccount.get(principal.id) : null
        ArrayList annotations   = []
        ArrayList teamMembers   = []
        Long licenseId          = -1L
        Long teamId             = -1L
        Project project
        Map dataUpload          = [:]
        Map codeUpload          = [:]

        if (user) {
            project = projectService.getNewEmptyProjectForUser(user)
            bindData(project, params, [exclude: ['annotations']])

            if (params.annotations) {
                annotations         = params.annotations.tokenize(',').collect { Long.valueOf(it) }
            }

            if (params.teamName) {
                teamName    = params.teamName
                teamMembers = params.members.tokenize(',').collect { Long.valueOf(it) }
            } else if (params.teamSelect) {
                teamId = Long.valueOf(params.teamSelect)
            }

            if (params.license) licenseId = Long.valueOf(params.license)

            if (params.dataFile) {
                dataUpload = [filename: params.dataFile.filename, type: FileUploadType.DATA, part: params.dataFile.part]

            } else if (params.dataExternalFileName) {
                dataUpload = [filename: params.dataExternalFileName, type: FileUploadType.CODE, part: null, url: params.dataUrlInput]
            }

            if (params.dataUploadDescription) dataUpload.description = params.dataUploadDescription

            if (params.codeFile) {
                codeUpload = [filename: params.codeFile.filename, type: FileUploadType.CODE, part: params.codeFile.part]
            } else if (params.codeExternalFileName) {
                codeUpload = [filename: params.codeExternalFileName, type: FileUploadType.CODE, part: null, url: params.codeUrlInput]
            }

            if (params.codeUploadDescription) codeUpload.description = params.codeUploadDescription


            succeeded = projectService.saveNewProject(user, project, annotations, licenseId, teamName, teamMembers, teamId, dataUpload, codeUpload)

            if (!succeeded) messages.danger = messageSource.getMessage("project.creation.failed", null, Locale.getDefault())
            else messages.success = messageSource.getMessage("project.creation.succeeded", null, Locale.getDefault())
        }
        render([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def renderNewUploadScreen() {
        render(template: "newUploadScreen", model: [projectId: params.projectId, type: params.type, requireDescription: true])
    }

    @Secured('isAuthenticated()')
    def addBundleToProject() {
        boolean succeeded
        String filename
        String typeParam        = params.type.toString()
        String externalFileLink = params."${typeParam}UrlInput"
        String description      = params."${typeParam}UploadDescription"
        Long projectId          = Long.valueOf(params.projectId)
        FileUploadType type     = FileUploadType.valueOf(typeParam.toUpperCase())
        Part filePart

        if (params."${typeParam}File") {
            filename    = params."${typeParam}File"?.filename
            filePart    = params."${typeParam}File"?.part
        } else filename = params."${typeParam}ExternalFileName"

        succeeded = projectService.addBundleToProject(projectId, type, externalFileLink, filePart, filename, description)
        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def getBundles() {
        Long projectId  = Long.valueOf(params.projectId)
        String type     = params.bundleType
        Project project = Project.findById(projectId)
        Map model       = [:]

        if (project && type) {
            if (type.toLowerCase() == "data") model.bundles = project.datas
            else model.bundles = project.codes

            model.bundleType    = type
            model.project       = project
        }

        render(template: "bundleRows", model: model)
    }

    @Secured('isAuthenticated()')
    def removeTeam() {
        boolean succeeded
        Object principal        = springSecurityService.principal
        UserAccount user        = principal ? UserAccount.get(principal.id) : null
        Long teamId    = params.teamId      ? Long.valueOf(params.teamId)       : -1L
        Long projectId = params.projectId   ? Long.valueOf(params.projectId)    : -1L

        if (teamId && projectId && user) {
            succeeded = projectService.removeTeam(user, teamId, projectId)
        }

        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def removeBundleFromProject() {
        boolean succeeded
        Object principal    = springSecurityService.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null
        Long bundleId       = params.bundleId    ? Long.valueOf(params.bundleId)      : -1L
        Long projectId      = params.projectId   ? Long.valueOf(params.projectId)   : -1L
        FileUploadType type = FileUploadType.valueOf(params.type.toString().toUpperCase())

        if (type && bundleId && projectId && user) {
            succeeded = projectService.removeBundleFromProject(user, projectId, bundleId, type)
        }

        render([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def getFilteredProjects() {
        List<Project> projects
        int max     = params.max && params.max != "NaN"   ? Integer.valueOf(params.max)    : Constants.DEFAULT_MAX
        int offset  = params.offset && params.offset != "NaN" ? Integer.valueOf(params.offset) : Constants.DEFAULT_OFFSET
        int numberOfPages       = 1
        boolean isMyProjects    = Boolean.valueOf(params.myProjects)

        if (isMyProjects) {
            Object principal = springSecurityService.principal
            UserAccount user = principal ? UserAccount.get(principal.id) : null

            if (user) {
                projects        = projectService.retrieveFilteredProjectsFromDB(user, isMyProjects, params.filterTerm, (offset * max), max)
                numberOfPages   = projectService.countFilteredProjectsPages(user, isMyProjects, params.filterTerm, max)
            }
        } else {
            projects        = projectService.retrieveFilteredProjectsFromDB(null, isMyProjects, params.filterTerm, (offset * max), max)
            numberOfPages   = projectService.countFilteredProjectsPages(null, isMyProjects, params.filterTerm, max)
        }

        def newRowsHTML = projectService.renderTableRows([projects: projects,
                                                          usersProject: isMyProjects])
        render([html: newRowsHTML, pagesCount: numberOfPages] as JSON)
    }

    @Secured('isAuthenticated()')
    def downloadFile() {
        Long projectId          = params.id ? Long.valueOf(params.id) : -1L
        String fileName         = params.name
        String gitCommitHash    = params.hash

        response.setContentType("APPLICATION/OCTET-STREAM")
        response.setHeader("Content-Disposition",
                "Attachment;Filename=\"${fileName}\"")

        byte[] fileContents = cloudService.downloadFile(projectId, fileName, gitCommitHash)
        OutputStream outputStream = response.outputStream

        outputStream.bytes = fileContents
        outputStream.flush()
        outputStream.close()
    }

    @Secured('isAuthenticated()')
    def projectsList() {
        List<Project> projects
        int max     = params.max    ? Integer.valueOf(params.max)    : Constants.DEFAULT_MAX
        int offset  = params.offset ? Integer.valueOf(params.offset) : Constants.DEFAULT_OFFSET
        int pages
        boolean isMyProjects    = Boolean.valueOf(params.myProjects)
        String filterOn         = params.filterTerm ?: ""

        projects        = projectService.retrieveFilteredProjectsFromDB(null, false, filterOn,
                            (offset * max), max)
        pages           = projectService.countFilteredProjectsPages(null, false, filterOn, max)

        return [projects: projects, numberOfPages: pages, pageOffset: offset, isUserProjects: isMyProjects]
    }

    @Secured('permitAll')
    def getAccessRequestDialogContent() {
        render(template: "projectAccessRequest")
    }

    @Secured('isAuthenticated()')
    def requestAccessToProject() {
        boolean succeeded   = false
        Object principal    = springSecurityService.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null
        int permissionMask  = Integer.valueOf(params.mask)
        Long projectId      = Long.valueOf(params.id)
        Map messages = [:]

        if ( user ) {
            succeeded = projectService.requestAccessToProject(projectId, user, permissionMask)
        }

        if (!succeeded) messages.danger = messageSource.getMessage("project.permission.request.failed", null, Locale.getDefault())
        else messages.success = messageSource.getMessage("project.permission.request.succeeded", null, Locale.getDefault())

        render([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def grantAccessToProject() {
        boolean succeeded       = false
        Long accessRequestId    = Long.valueOf(params.id)
        Map messages            = [:]

        if ( accessRequestId ) {
            succeeded = projectService.grantPermissionToProject(accessRequestId)
        }

        if (!succeeded) messages.danger = messageSource.getMessage("project.permission.granting.failed", null, Locale.getDefault())
        else messages.success = messageSource.getMessage("project.permission.granting.succeeded", null, Locale.getDefault())

        render([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def denyAccessToProject() {
        boolean succeeded       = false
        Long accessRequestId    = Long.valueOf(params.id)
        Long userId             = springSecurityService.principal?.id ?: -1
        Map messages            = [:]

        if ( accessRequestId ) {
            succeeded = projectService.denyAccessRequest(accessRequestId, userId)
        }

        if (!succeeded) messages.danger = messageSource.getMessage("project.permission.deny.access.failed", null, Locale.getDefault())
        else messages.success = messageSource.getMessage("project.permission.deny.access.succeeded", null, Locale.getDefault())

        render([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def revokeAccessToProject() {
        boolean succeeded   = false
        Object principal    = springSecurityService.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null
        Long projectId      = Long.valueOf(params.projectId)
        Long userId         = Long.valueOf(params.userId)
        ArrayList<Integer> masks     = []
        Map messages        = [:]

        if (params."masks[]") {
            if (params."masks[]".class.simpleName == "String") masks.add(Integer.valueOf(params."masks[]"))
            else {
                params."masks[]".each { stringId ->
                    masks.add(Integer.valueOf(stringId))
                }
            }

            if (masks.size() > 0 && userId && projectId && user) {
                succeeded = projectService.revokeAccessToProject(projectId, userId, masks, user)
            }
        }

        if (!succeeded) messages.danger = messageSource.getMessage("project.permission.revoke.failed", null, Locale.getDefault())
        else messages.success = messageSource.getMessage("project.permission.revoke.succeeded", null, Locale.getDefault())

        render([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def acknowledgeAccessRequestResult() {
        boolean succeeded       = false
        Long accessRequestId    = Long.valueOf(params.id)
        Object principal    = springSecurityService.principal
        UserAccount user    = principal ? UserAccount.get(principal.id) : null
        Map messages        = [:]

        if (user) {
            succeeded = projectService.acknowledgeAccessRequestStatus(accessRequestId, user)
        }

        if (!succeeded) messages.danger = messageSource.getMessage("project.permission.acknowledge.failed", null, Locale.getDefault())
        else messages.success = messageSource.getMessage("project.permission.acknowledge.succeeded", null, Locale.getDefault())

        render([success: succeeded, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def renderIndividualUserAccess() {
        Long projectId  = Long.valueOf(params.projectId)
        Long userId     = Long.valueOf(params.userId)
        LinkedHashMap userObject =  projectService.getListOfUserAccess(projectId, userId)

        if (userObject) {
            render(template: "userAccessIndividual", model: [userObject: userObject, projectId: projectId])
        } else {
            render("")
        }
    }
}