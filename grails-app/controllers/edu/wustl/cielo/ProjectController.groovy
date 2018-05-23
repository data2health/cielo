package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import edu.wustl.cielo.enums.FileUploadType
import javax.servlet.http.Part

class ProjectController {

    ProjectService projectService
    def springSecurityService
    def messageSource

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
        Project project     = Project.findById(Long.valueOf(params.id))
        boolean showTeams   = params.teams ? Boolean.valueOf(params.teams) : false
        boolean showBundles = params.bundles ? Boolean.valueOf(params.bundles) : false

        projectService.incrementViewsCounter(project)

        return [userProfile: user.profile,
                annotations: Annotation.list(),
                project: project,
                showTeams: showTeams,
                showBundles: showBundles]
    }

    @Secured('isAuthenticated()')
    def saveProjectComment() {
        boolean succeeded
        Long projectId = Long.valueOf(params.id)
        String commentStr = params.comment
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (projectId && commentStr && user) {
            succeeded = projectService.saveProjectComment(user, projectId, commentStr)
        }

        render ([success: succeeded] as JSON)
    }

    @Secured('isAuthenticated()')
    def saveCommentReply () {
        boolean succeeded
        Long commentId = Long.valueOf(params.id)
        String commentStr = params.reply
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (commentId && commentStr && user) {
            succeeded = projectService.saveProjectCommentReply(user, commentId, commentStr)
        }

        render ([success: succeeded] as JSON)
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
            if (params.shared)      shared = Integer.valueOf(params.shared) ?: false

            succeeded = projectService.saveProjectBasicChanges(projectId, newName, description, tags,
                    softwareLicenseId, shared)
        }
        render([success: succeeded] as JSON)
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
            if (params.offset)  offset  = Integer.valueOf(params.offset) - 1

            myProjects = projectService.getMyProjects(user, max, offset)
        }

        return [userProfile: user?.profile, projects: myProjects, offset: offset <= 0 ? 1 : offset,
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
        if (params.offset)  offset  = Integer.valueOf(params.offset) - 1

        projects = projectService.getPublicProjects(offset, max)

        return [projects: projects, userProfile: user?.profile,
                offset: offset <= 0 ? 1 : offset,
                numberOfPages: projectService.getNumberOfPagesForPublicProjects(max),
                max: max]
    }

    @Secured('isAuthenticated()')
    def deleteProject() {
        boolean succeeded
        Long projectId = params.id ? Long.valueOf(params.id) : -1L
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (user) {
            succeeded = projectService.deleteProject(user, projectId)
        }

        if (!succeeded) flash.danger = messageSource.getMessage("project.deletion.failed", null, Locale.getDefault())
        else flash.success = messageSource.getMessage("project.deletion.succeeded", null, Locale.getDefault())
        render([success: succeeded] as JSON)
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

            if (!succeeded) flash.danger = messageSource.getMessage("project.creation.failed", null, Locale.getDefault())
            else flash.success = messageSource.getMessage("project.creation.succeeded", null, Locale.getDefault())
        }
        render([success: succeeded] as JSON)
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
}