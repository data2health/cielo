package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class UserController {

    def userAccountService
    def springSecurityService
    def messageSource
    def projectService
    def teamService

    @Secured('isAuthenticated()')
    def view() {
        Object principal = springSecurityService?.principal
        UserAccount loggedInUser = principal ? UserAccount.get(principal.id) : null
        UserAccount user = params.id ? UserAccount.findById(Long.valueOf(params.id)) : null

        return [user: user,
                myTeams: teamService.getListOfTeamsUserOwns(user),
                contributingTeams: teamService.getListOfTeamsUserIsMemberOf(user),
                projects: userAccountService.getProjectsUserOwns(user),
                projectsUserContributesTo: projectService.getProjectsUserContributesTo(user),
                institutes: Institution.list(),
                annotations:  user?.profile?.annotations.collect { [name: it.term, id: it.id.toString()] } as JSON,
                loggedInUser: loggedInUser]
    }

    /**
     * Update user using post data from form
     *
     */
    @Secured('isAuthenticated()')
    def updateUser() {
        boolean userUpdated
        UserAccount user

        if (params.userId) user = UserAccount.findById(Long.valueOf(params.userId))

        if (user) {
            bindData(user, params, [exclude: ['password']])
            if (user.id) {
                bindData(user.profile, params)

                if (params."annotations-select") {
                    user.profile.annotations.clear()
                    List<Annotation> annotations = []
                    if (params."annotations-select".indexOf(',') != -1) {
                        (params."annotations-select".tokenize(',')).each { String annotationId ->
                            annotations.add(Annotation.findById(Long.valueOf(annotationId)))
                        }
                    } else {
                        annotations.add(Annotation.findById(Long.valueOf(params."annotations-select")))
                    }

                    user.profile.annotations.addAll(annotations)
                }

                if (params.profilePic) {
                    if (!user.profile.picture) user.profile.picture = new ProfilePic()
                    user.profile.picture.fileContents  = params.profilePic.bytes
                    user.profile.picture.fileExtension = params.profilePic.filename.tokenize('.')[1]
                }
            }

            userUpdated = userAccountService.updateUser(user)
        }

        if (!userUpdated) flash.danger = messageSource.getMessage("user.failed.update", null, Locale.getDefault())
        redirect(action: "view", id: params.userId)
    }

    /**
     * Follow a user
     *
     * @return [success: true/false, messages: messages] (where messages is a collection of flash messages) as JSON
     */
    @Secured('isAuthenticated()')
    def followUser() {
        Map messages = [:]
        boolean successful = false
        Long userToFollow

        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (params.id) userToFollow = Long.valueOf(params.id)

        if (userToFollow && user) {
            successful = userAccountService.addConnection(user, userToFollow)
        }

        if (!successful) messages.danger = messageSource.getMessage("user.failed.follow", null, Locale.getDefault())
        render([success: successful, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def unFollowUser() {
        Map messages = [:]
        boolean successful = false
        Long userToFollow

        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (params.id) userToFollow = Long.valueOf(params.id)

        if (userToFollow && user) {
            successful = userAccountService.removeConnection(user, userToFollow)
        }
        if (!successful) messages.danger = messageSource.getMessage("user.failed.un-follow", null, Locale.getDefault())
        render([success: successful, messages: messages] as JSON)
    }

    @Secured('isAuthenticated()')
    def getUsersIFollow() {
        TreeSet<UserAccount> users
        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (user) {
            users = user.connections
        }
        render(template: "/templates/addUsersDialogContent", model: [follow: users, users: UserAccount.list() - user])
    }

    @Secured('isAuthenticated()')
    def updateUsersIFollow() {
        Map messages = [:]
        boolean succeeded
        List<Long> userIds = []
        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (params."users[]") {
            if (params."users[]".class.simpleName == "String") userIds.add(Long.valueOf(params."users[]"))
            else {
                params."users[]".each { stringId ->
                    userIds.add(Long.valueOf(stringId))
                }
            }
        }

        if (user) {
            succeeded = userAccountService.updateConnections(user, userIds)
        }
        if (!succeeded) messages.danger = messageSource.getMessage("user.followed.update.failed", null, Locale.getDefault())
        render([success: succeeded, messages: messages] as JSON)
    }
}