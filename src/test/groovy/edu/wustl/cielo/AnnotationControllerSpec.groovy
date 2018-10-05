package edu.wustl.cielo

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class AnnotationControllerSpec extends Specification implements ControllerUnitTest<AnnotationController> {

    AnnotationService annotationService

    def setup() {
        annotationService = new AnnotationService()

        annotationService.metaClass.getFilteredAnnotationsCount     = { String filterText ->
            0
        }

        annotationService.metaClass.getNumberOfPagesForAnnotation   = { int totalCount, int max ->
            0
        }

        annotationService.metaClass.retrieveFilteredAnnotationsFromDB = { String filterText, int offset ->
            []
        }

        controller.annotationService = annotationService
    }

    void "test list"() {
        when:
        controller.list()

        then:
        response.json.total_count == 0
    }
}