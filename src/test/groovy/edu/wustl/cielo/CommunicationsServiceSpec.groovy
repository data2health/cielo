package edu.wustl.cielo

import grails.gsp.PageRenderer
import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import org.grails.gsp.GroovyPagesTemplateEngine

class CommunicationsServiceSpec extends Specification implements ServiceUnitTest<CommunicationsService>, DomainUnitTest<ContactUsEmail> {

    PageRenderer groovyPageRenderer

    def setup() {
        groovyPageRenderer = new PageRenderer(new GroovyPagesTemplateEngine())
    }

    void "test init"() {
        expect:"to_email is null"
           !service.TO_EMAIL

        when: "calling init"
            service.init()

        then: "to email has been initialized"
            service.TO_EMAIL
    }

    void "test deleteContactUsEmail"() {
        expect:"no contact emails"
            ContactUsEmail.all.size() == 0

        when: "Adding a new contact us email"
           def contactEmail = new ContactUsEmail(toAddresses: ["cd2h.cielo@wustl.edu"],
                    subject: "My email subject line",
                    plainMessage: "plain message",
                    htmlMessage: "<div> <p>My html message</p></div>").save()

        then: "we should have contact email saved"
            ContactUsEmail.all.size() > 0

        when: "Deleting the contact us email"
            contactEmail.delete()

        then: "no contact emails saved"
            ContactUsEmail.all.size() == 0

    }

    void "test scheduleContactUsEmail"() {
        def model = [name: "Ricky R.",
                     subject: "Email subject line",
                     email: "ricardo.rodriguez@wustl.edu",
                     phone: "(314) 273-DONT-CALL",
                     message: "Some message"]

        service.groovyPageRenderer = groovyPageRenderer

        service.groovyPageRenderer.metaClass.render = { Map param ->
            "mock text returned for render"
        }

        expect:"no contact emails"
            ContactUsEmail.all.size() == 0

        when: "scheduling contact us email"
            service.BASE_LINK = "localhost:8080"
            service.scheduleContactUsEmail(model)

        then: "email is saved to be retrieved later"
            ContactUsEmail.all.size() > 0
    }
}
