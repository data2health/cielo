package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class SoftwareLicenseIntegrationSpec extends Specification {

    void cleanup() {
        Project.list().each {
            it.delete()
        }

        SoftwareLicense.list().each {
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
        SoftwareLicense softwareLicense
        UserAccount user

        when: "no props"
            softwareLicense = new SoftwareLicense()

        then:
            !softwareLicense.save()


        when: "adding just label"
            softwareLicense = new SoftwareLicense(label: "RER License 1.0")

        then: "fails due to missing props"
            !softwareLicense.save()

        when: "adding just label and body"
        softwareLicense = new SoftwareLicense(label: "RER License 1.0", body: "Some text\nhere.")

        then: "fails due to missing props"
            !softwareLicense.save()

        when: "all necessary props are set"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com")

        then: "save is successful"
            softwareLicense.save()

    }
}
