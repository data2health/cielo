package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class AnnotationIntegrationSpec extends Specification {

    void setup() {
        new UserAccount(username: "admin", password: "somepass").save(flush: true)
    }

    void cleanup() {
        Project.list().each {
            it.delete()
        }

        Annotation.list().each {
            it.delete()
        }
    }

    void "test saving annotation"() {
        def annotation

        when:"save with null label"
            annotation = new Annotation(term: null)

        then:
            !annotation.save()

        when: "annotation has blank label"
            annotation = new Annotation(term: "")

        then:
            !annotation.save()

        when: "annotation label length is 1"
            annotation = new Annotation(term: "A")

        then:
            !annotation.save()

        when: "save with proper label"
            annotation = new Annotation(term: "ID", code: null)

        then:
            !annotation.save()

        when: "save with proper label"
            annotation = new Annotation(term: "ID", code: "")

        then:
            !annotation.save()

        when: "save with proper label"
            annotation = new Annotation(term: "ID", code: "C10001")

        then:
            annotation.save()
    }
}
