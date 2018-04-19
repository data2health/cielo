package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.validation.ObjectError

class ProjectController {

    ProjectService projectService
    def springSecurityService

    static allowedMethods = [save: "POST"]

    /**
     * Call to service that retrieves the most 'popular' projects
     *
     * @return a list of properties for most popular projects
     */
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

        projectService.incrementViewsCounter(project)

        return [userProfile: user.profile,
                annotations: Annotation.list(),
                project: project,
                showTeams: showTeams]
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
            List<Long> tags
            Long softwareLicenseId
            boolean shared

            if (params."tags[]")    tags = params."tags[]".collect { Long.valueOf(it) }
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
        render([success: succeeded] as JSON)
    }
}
