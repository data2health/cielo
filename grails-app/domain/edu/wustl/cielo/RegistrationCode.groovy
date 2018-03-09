package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RegistrationCode {

    String token = UUID.randomUUID().toString()
    UserAccount userAccount

    static belongsTo = [userAccount: UserAccount]
    static constraints = {
        token(nullable: false)
    }

    String toString() {
        return token
    }
}
