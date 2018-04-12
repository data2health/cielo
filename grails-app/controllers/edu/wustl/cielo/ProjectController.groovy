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
        Object principal = springSecurityService.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null
        Project project =  Project.findById(Long.valueOf(params.id))

        projectService.incrementViewsCounter(project)

        return [userProfile: user.profile,
                annotations: Annotation.list(),
                project: project]
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
            String newName      = params.name
            List<Long> tags     = params."tags[]".collect { Long.valueOf(it) }
            String description  = params.desc

            succeeded = projectService.saveProjectBasicChanges(projectId, newName, description, tags)
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
}
