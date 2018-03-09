package edu.wustl.cielo

import grails.gsp.PageRenderer
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import org.grails.gsp.GroovyPagesTemplateEngine
import spock.lang.Specification

class CommunicationsControllerSpec extends Specification implements ControllerUnitTest<CommunicationsController>, DomainUnitTest<ContactUsEmail> {

    CommunicationsService communicationsService
    PageRenderer groovyPageRenderer

    def setup() {
        groovyPageRenderer = new PageRenderer(new GroovyPagesTemplateEngine())
        communicationsService = new CommunicationsService()
        groovyPageRenderer = Mock()
    }

    void "test contactUs"() {
        int callsToScheduleContactUsEmail = 0

        expect:"no contact us emails saved"
            ContactUsEmail.all.size() == 0

        when: "given params are given and call contactUs"
            communicationsService.metaClass.scheduleContactUsEmail = { Map params ->
                callsToScheduleContactUsEmail++
                true
            }

            controller.communicationsService = communicationsService

            params.name = "Ricky R."
            params.subject = "Email subject line"
            params.email = "ricardo.rodriguez@wustl.edu"
            params.phone = "(314) 273-DONT-CALL"
            params.message = "Some message"
            controller.contactUs()

        then: "Now you should have an email saved"
            flash.success //message for success
            callsToScheduleContactUsEmail == 1
    }
}
