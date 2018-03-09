package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class UserAccountIntegrationSpec extends Specification {

    void cleanup() {
        UserAccount.list().each {
            Profile.findByUser(it)?.delete()
            RegistrationCode.findByUserAccount(it)?.delete()
            UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
            it.delete()
        }
    }

    void "test saving account"() {
        UserAccount user

        when: "with no props"
            user = new UserAccount()

        then: "save fails"
            !user.save()

        when: "no password but with username"
            user = new UserAccount(username: "someuser")

        then: "save fails"
            !user.save()

        when: "no username but with password"
            user = new UserAccount(password: "somePassword")

        then: "save fails"
            !user.save()

        when: "user with required props"
            user = new UserAccount(username: "someuser", password: "somePassword")

        then: "save passes"
            user.save()
    }
}
