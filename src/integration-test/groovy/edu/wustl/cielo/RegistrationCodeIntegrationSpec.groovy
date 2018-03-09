package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class RegistrationCodeIntegrationSpec extends Specification {

    void cleanup() {
        RegistrationCode.list().each {
            it.delete()
        }

        Comment.list().each {
            it.delete()
        }

        UserAccount.list().each {
            Profile.findByUser(it)?.delete()
            RegistrationCode.findByUserAccount(it)?.delete()
            UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
            it.delete()
        }
    }

    void "test saving"() {
        RegistrationCode registrationCode
        UserAccount user

        when: "no props"
            registrationCode = new RegistrationCode()

        then: "save fails"
            !registrationCode.save()

        when: "a user is associated to the registration code"
            user = new UserAccount(username: "someuser", password: "somePassword")
            registrationCode = new RegistrationCode(userAccount: user)

        then: "save is successful"
            registrationCode.save()
    }

    void "test toString"() {
        RegistrationCode registrationCode
        UserAccount user

        given:
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            registrationCode = new RegistrationCode(userAccount: user).save()

        when: "toString is called"
            def returnVal = registrationCode.toString()

        then:
            assert returnVal == registrationCode.token
    }
}
