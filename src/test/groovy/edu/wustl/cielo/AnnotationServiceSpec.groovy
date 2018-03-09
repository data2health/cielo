package edu.wustl.cielo

import grails.testing.services.ServiceUnitTest
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AnnotationServiceSpec extends Specification implements ServiceUnitTest<AnnotationService>, DomainUnitTest<Annotation> {

    def webRoot

    void setup() {
        mockDomain Annotation
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
    }

    void "test initializeAnnotations"() {
        expect:"no annotations"
            Annotation.list() == []

        when: "calling service to initialize annotations"
            service.initializeAnnotations(null)

        then: "still empty"
            Annotation.list() == []

        when: "calling service to initialize again with proper file"
            service.initializeAnnotations(new File(webRoot + "WEB-INF/startup/shorter_mshd2014.txt"))

        then: "now there are annotations saved"
            Annotation.list() != []
    }
}
