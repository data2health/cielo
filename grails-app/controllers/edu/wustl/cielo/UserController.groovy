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
     * @return [success: true/false] as JSON
     */
    @Secured('isAuthenticated()')
    def updateUser() {
        boolean userUpdated
        UserAccount user

        if (params.userId) user = UserAccount.findById(Long.valueOf(params.userId))

        if (user) {
            bindData(user, params)
            if (user.id) {
                bindData(user.profile, params)
            }

            userUpdated = userAccountService.updateUser(user)
        }

        if (!userUpdated) flash.danger = messageSource.getMessage("user.failed.update", null, Locale.getDefault())
        redirect(action: "view", id: params.userId)
    }

    /**
     * Follow a user
     *
     * @return [success: true/false] as json
     */
    @Secured('isAuthenticated()')
    def followUser() {
        boolean successful = false
        Long userToFollow

        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (params.id) userToFollow = Long.valueOf(params.id)

        if (userToFollow && user) {
            successful = userAccountService.addConnection(user, userToFollow)
        }

        if (!successful) flash.danger = messageSource.getMessage("user.failed.follow", null, Locale.getDefault())
        render([success: successful] as JSON)
    }

    @Secured('isAuthenticated()')
    def unFollowUser() {
        boolean successful = false
        Long userToFollow

        Object principal = springSecurityService?.principal
        UserAccount user = principal ? UserAccount.get(principal.id) : null

        if (params.id) userToFollow = Long.valueOf(params.id)

        if (userToFollow && user) {
            successful = userAccountService.removeConnection(user, userToFollow)
        }
        if (!successful) flash.danger = messageSource.getMessage("user.failed.un-follow", null, Locale.getDefault())
        render([success: successful] as JSON)
    }
}
