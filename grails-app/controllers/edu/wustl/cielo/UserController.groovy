package edu.wustl.cielo

import grails.plugin.springsecurity.annotation.Secured

class UserController {

    @Secured('isAuthenticated()')
    def view() {
        return [user: UserAccount.findById(Long.valueOf(params.id))]
    }
}
