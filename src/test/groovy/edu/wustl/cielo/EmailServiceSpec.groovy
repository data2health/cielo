package edu.wustl.cielo

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class EmailServiceSpec extends Specification implements ServiceUnitTest<EmailService>{

    void "test sendEmail"() {
        //TODO: for now since we are using mailjet, do not test this code. At a later date need to see about mocking out
        //TODO: the service from the mailjet library
        expect:"passes"
            true == true
    }
}
