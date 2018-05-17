package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class LicenseControllerSpec extends Specification implements ControllerUnitTest<LicenseController>, DomainUnitTest<UserAccount> {

    def setup() {
        mockDomain(SoftwareLicense)
    }

    void "test getLicenseBody"() {

        given:
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
            SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()

        when:"calling to retrieve the license body"
            params.id = softwareLicense.id
            controller.getLicenseBody()

        then:
            response.json.licenseText == "Some text\nhere."
    }

    void "test getTermsOfUse"() {
        views["/templates/_termsOfUse.gsp"] = "terms of use"
        when:
            controller.getTermsOfUse()

        then:
            response.text == "terms of use"
    }
}
