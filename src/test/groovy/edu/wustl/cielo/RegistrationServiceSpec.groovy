package edu.wustl.cielo

import grails.gsp.PageRenderer
import  grails.web.mapping.LinkGenerator
import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import org.grails.gsp.GroovyPagesTemplateEngine
import spock.lang.Specification

class RegistrationServiceSpec extends Specification implements ServiceUnitTest<RegistrationService>, DomainUnitTest<RegistrationEmail> {

    PageRenderer groovyPageRenderer
    LinkGenerator grailsLinkGenerator

    def setup() {
        groovyPageRenderer = groovyPageRenderer = new PageRenderer(new GroovyPagesTemplateEngine())
        grailsLinkGenerator = Mock()

        mockDomains(Institution, UserAccount, Profile)

        service.grailsLinkGenerator = grailsLinkGenerator
        service.groovyPageRenderer  = groovyPageRenderer

        service.groovyPageRenderer.metaClass.render = { Map param ->
            "mock text returned for render"
        }
    }

    void "test scheduleRegistrationEmail"() {
        UserAccount user
        Profile profile
        Institution institution
        RegistrationEmail registrationEmail

        given:
            RegistrationEmail.list().size() == 0
            institution = new Institution(fullName: "Washington University of St. Louis", shortName: "WUSTL").save()

        when:
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu",
                institution: institution, user: user)
            profile.save()
            registrationEmail = service.scheduleRegistrationEmail("rrodriguez@wustl.edu")

        then: "scheduling an email should work"
            registrationEmail
            RegistrationEmail.list().size() == 1
    }

    void "test deleteRegistrationEmail"() {

        given:
            RegistrationEmail.list().size() == 0

        when: "no registration email passed in"
            service.deleteRegistrationEmail(null)

        then:
            RegistrationEmail.list().size() == 0

        when: "we create one Registration email"
            RegistrationEmail registrationEmail = new RegistrationEmail(subject: "Subject line", plainMessage: "A Message",
            htmlMessage: "<div>Html <strong>encoded</strong> message", toAddresses: ["ricardo.rodriguez@wustl.edu"])
            registrationEmail.save()

        then:
            RegistrationEmail.list().size() == 1

        when: "we call for deletion"
            service.deleteRegistrationEmail(registrationEmail)

        then:
            RegistrationEmail.list().size() == 0

    }
}
