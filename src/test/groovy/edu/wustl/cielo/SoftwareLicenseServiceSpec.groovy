package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class SoftwareLicenseServiceSpec extends Specification implements ServiceUnitTest<SoftwareLicenseService>,
        DomainUnitTest<SoftwareLicense> {

    def webRoot

    void setup() {
        mockDomains(Institution, Profile, UserRole, RegistrationCode, UserAccountUserRole)
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
    }

    void "test bootstrapLicenses"() {
        expect:"no licenses"
            SoftwareLicense.list() == []

        when: "calling bootstrapLicenses with incorrect params"
            service.bootstrapLicenses(null)

        then: "still no licenses saved"
            SoftwareLicense.list() == []

        when: "calling with correct params"
            service.bootstrapLicenses(new File(webRoot + "WEB-INF/startup/licenses.json"))

        then: "fails becuase we do not have a superadmin user"
            SoftwareLicense.list() == []

        when: "adding superadmin user"
            UserAccountService userAccountService = new UserAccountService()
            userAccountService.bootstrapUserRoles()
            userAccountService.bootstrapAddSuperUserRoleToUser(userAccountService.bootstrapCreateOrGetAdminAccount())
            service.bootstrapLicenses(new File(webRoot + "WEB-INF/startup/licenses.json"))

        then: "saves successfully"
            SoftwareLicense.list()
    }
}
