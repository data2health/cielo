package edu.wustl.cielo

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class UserController {

    def userAccountService
    def springSecurityService
    def messageSource

    @Secured('isAuthenticated()')
    def view() {
        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        return [user: (params.id ? UserAccount.findById(Long.valueOf(params.id)) : null),
                institutes: Institution.list(),
                annotations: Annotation.list(),
                loggedInUser: user]
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
                if (user.profile) bindData(user.profile, params)
                if (params.profilePic) {
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