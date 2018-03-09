package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class SoftwareLicenseSpec extends Specification implements DomainUnitTest<SoftwareLicense> {

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
