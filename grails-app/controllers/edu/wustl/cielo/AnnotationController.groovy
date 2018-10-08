package edu.wustl.cielo

import grails.plugin.springsecurity.annotation.Secured
import grails.converters.JSON

class AnnotationController {

    def annotationService

    @Secured('permitAll')
    def list() {
        String filterText   = params.search ?: ""
        int pageOffset      = params.page ? Integer.valueOf(params.page) : Constants.DEFAULT_OFFSET
        int numberOfItems   = annotationService.getFilteredAnnotationsCount(filterText)
        int numberOfPages   = annotationService.getNumberOfPagesForAnnotations(numberOfItems, annotationService.DEFAULT_ANNOTATIONS_MAX_COUNT)

        render([items: annotationService.retrieveFilteredAnnotationsFromDB(filterText, pageOffset),
                total_count: numberOfItems,
                pagination: [ more: numberOfPages > (pageOffset + 1) ]
                ] as JSON)
    }
}
