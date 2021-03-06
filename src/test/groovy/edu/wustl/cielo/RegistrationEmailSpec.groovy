package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class RegistrationEmailSpec extends Specification implements DomainUnitTest<RegistrationEmail> {

    void "test save"() {
        RegistrationEmail registrationEmail

        when:"we attempt to save with no constraints fulfilled"
           registrationEmail = new RegistrationEmail()

        then: "save fails"
            !registrationEmail.save()

        when: "subject constraint fulfilled"
            registrationEmail = new RegistrationEmail(subject: "Some subject")

        then: "save fails"
            !registrationEmail.save()

        when: "subject, plainMessage constraints fulfilled"
            registrationEmail = new RegistrationEmail(subject: "Some subject", plainMessage: "My message")

        then: "save fails"
            !registrationEmail.save()

        when: "subject, plainMessage, htmlMessage constraints fulfilled"
            registrationEmail = new RegistrationEmail(subject: "Some subject", plainMessage: "My message",
                    htmlMessage: "<div>html message</div>")

        then: "save fails"
            !registrationEmail.save()

        when: "subject, plainMessage, htmlMessage, toAddresses constraints fulfilled"
            registrationEmail = new RegistrationEmail(subject: "Some subject", plainMessage: "My message",
                    htmlMessage: "<div>html message</div>", toAddresses: ["ricardo.rodriguez@wustl.edu"])

        then: "save is successful"
            registrationEmail.save()
    }
}
