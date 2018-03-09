package edu.wustl.cielo

import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class SoftwareLicenseServiceIntegrationSpec extends Specification {

    @Autowired
    SoftwareLicenseService softwareLicenseService

    @Autowired
    SessionFactory sessionFactory

    def webRoot

    void setup() {
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
    }

    void cleanup() {
        SoftwareLicense.list().each {
            it.delete()
        }

        UserAccount.list().each {
            Comment.findByCommenter(it)?.delete()
            Profile.findByUser(it)?.delete()
            RegistrationCode.findByUserAccount(it)?.delete()
            UserAccountUserRole.findAllByUserAccount(it)?.each { it.delete() }
            it.delete()
        }

        UserRole.list().each {
            it.delete()
        }
    }

    void "test bootstrapLicenses"() {
        expect:"no licenses"
            SoftwareLicense.list() == []

        when: "calling bootstrapLicenses with incorrect params"
            softwareLicenseService.bootstrapLicenses(null)

        then: "still no licenses saved"
            SoftwareLicense.list() == []

        when: "calling with correct params"
            softwareLicenseService.bootstrapLicenses(new File(webRoot + "WEB-INF/startup/licenses.json"))

        then: "fails becuase we do not have a superadmin user"
            SoftwareLicense.list() == []

        when: "adding superadmin user"
            UserAccountService userAccountService = new UserAccountService()
            userAccountService.bootstrapUserRoles()
            UserAccount user = userAccountService.bootstrapCreateOrGetAdminAccount()
            userAccountService.bootstrapAddSuperUserRoleToUser(user)
            softwareLicenseService.bootstrapLicenses(new File(webRoot + "WEB-INF/startup/licenses.json"))
            sessionFactory.getCurrentSession().flush()

        then: "saves successfully"
            SoftwareLicense.list()
    }
}
