package edu.wustl.cielo

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class ErrorsControllerSpec extends Specification implements ControllerUnitTest<ErrorsController> {

    def "test denied"() {

        when:
            controller.denied()

        then:
            response.status == 200
    }

    def "test notFound"() {

        when:
            controller.notFound()

        then:
            response.status == 200
    }

    def "test error"() {

        when:
            controller.error()

        then:
            response.status == 200
    }
}
