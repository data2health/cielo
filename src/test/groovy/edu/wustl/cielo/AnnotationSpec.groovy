package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AnnotationSpec extends Specification implements DomainUnitTest<Annotation> {

    void "test saving annotation"() {
        def annotation

        when:"save with null label"
            annotation = new Annotation(label: null)

        then:
            !annotation.save()

        when: "annotation has blank label"
            annotation = new Annotation(label: "")

        then:
            !annotation.save()

        when: "annotation label length is 1"
            annotation = new Annotation(label: "A")

        then:
            !annotation.save()

        when: "save with proper label"
            annotation = new Annotation(label: "ID")

        then:
            annotation.save()
    }
}
