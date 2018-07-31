package edu.wustl.cielo

import edu.wustl.cielo.enums.AccountStatusEnum
import grails.gsp.PageRenderer
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import grails.web.mapping.LinkGenerator
import org.grails.gsp.GroovyPagesTemplateEngine
import spock.lang.Specification
import grails.plugin.springsecurity.acl.AclSid

class RegistrationControllerSpec extends Specification implements ControllerUnitTest<RegistrationController>,
        DomainUnitTest<Institution> {

    LinkGenerator grailsLinkGenerator
    PageRenderer groovyPageRenderer
    UserAccountService userAccountService

    def setup() {
        mockDomains(RegistrationCode, AclSid)
        grailsLinkGenerator = Mock()
        groovyPageRenderer  = new PageRenderer(new GroovyPagesTemplateEngine())

        groovyPageRenderer.metaClass.render = { Map param ->
            "mock text returned for render"
        }

        mockDomains(UserAccount, Profile, RegistrationEmail)
        controller.institutionService   = new InstitutionService()
        userAccountService              = new UserAccountService()
        controller.userAccountService   = userAccountService
        controller.emailService         = new EmailService()
        controller.registrationService  = new RegistrationService()
        controller.groovyPageRenderer   = groovyPageRenderer
        controller.grailsLinkGenerator  = grailsLinkGenerator
        controller.registrationService.grailsLinkGenerator  = grailsLinkGenerator
        controller.registrationService.groovyPageRenderer   = groovyPageRenderer
    }

    void "test activateUser"() {
        RegistrationCode registrationCode = new RegistrationCode()
        registrationCode.save()
        UserAccount user

        when: "we first create a user"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            registrationCode.userAccount = user
            registrationCode.save()

        then:
            user.status == AccountStatusEnum.ACCOUNT_UNVERIFIED

        when:"registration key"
            params.ukey = user.registrationCode.token
            controller.activateUser()

        then:
            user.status == AccountStatusEnum.ACCOUNT_VERIFIED
    }

    void "test register"() {
        when:"show register page"
            def results = controller.register()

        then:
            results.institutes == []
            !results.usernames
    }

    void "test registered"() {
        UserAccount user
        Profile profile
        Institution institution = new Institution(fullName: "Washington University of St. Louis", shortName: "WUSTL").save()

        when:"user email passed in"
            params.userEmail = "ricardo.rodriguez@wustl.edu"
            def results = controller.registered()

        then: "No registration email is created"
            results.emailAddress
            RegistrationEmail.list().size() == 0

        when: "The user is properly saved"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "ricardo.rodriguez@wustl.edu",
                    institution: institution, user: user)
            profile.save()
            params.userEmail = "ricardo.rodriguez@wustl.edu"
            results = controller.registered()

        then:
            results.emailAddress
            RegistrationEmail.list().size() == 1
    }
}
