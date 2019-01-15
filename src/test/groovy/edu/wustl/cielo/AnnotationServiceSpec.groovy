package edu.wustl.cielo

import grails.testing.services.ServiceUnitTest
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AnnotationServiceSpec extends Specification implements ServiceUnitTest<AnnotationService>, DomainUnitTest<Annotation> {

    def webRoot
    UtilService utilService

    void setup() {
        mockDomain Annotation

        service.metaClass.saveNewAnnotation = { List<String> names, String code ->
            names.each { String name ->
                if (name) {
                    new Annotation(term: name, code: code).save()
                }
            }
        }

        utilService = new UtilService()
        utilService.metaClass.getDateDiff { Date fromDate, Date toDate = null ->
            "today"
        }
        service.utilService = utilService
        webRoot =  new File(".").canonicalPath + "/src/main/webapp/"
    }

    void "test initializeAnnotations"() {
        expect:"no annotations"
            Annotation.list() == []

        when: "calling service to initialize annotations"
            service.initializeAnnotations(null)

        then: "still empty"
            Annotation.list() == []

        when: "calling service to initialize again with proper file"
            service.initializeAnnotations([new File(webRoot + "WEB-INF/startup/NCI_Thesaurus_terms_shorter.txt")])

        then: "now there are annotations saved"
            Annotation.list() != []
    }


    void "test getFilteredAnnotationsCount"() {
        List<Annotation> annotations = []

        service.metaClass.getFilteredAnnotationsCount = { String filterText ->
            annotations.size()
        }

        when:
            int count = service.getFilteredAnnotationsCount("")

        then:
            count == 0

        when:
            annotations.add(new Annotation(term: "Something", code: "C10002").save())
            count = service.getFilteredAnnotationsCount("")

        then:
            count == 1
    }

    void "test getNumberOfPagesForAnnotations"() {
        List<Annotation> annotations = []

        service.metaClass.getFilteredAnnotationsCount = { String filterText ->
            annotations.size()
        }

        when:
            int totalCount = service.getFilteredAnnotationsCount("")
            int pageCount  = service.getNumberOfPagesForAnnotations(totalCount, 1)

        then:
            pageCount == 1 //empty page is 1 not zero

        when:
            5.times { int num ->
                annotations.add(new Annotation(term: "Something_${num}", code: "C10002").save())
            }

            totalCount = service.getFilteredAnnotationsCount("Something")
            pageCount  = service.getNumberOfPagesForAnnotations(totalCount, 2)

        then:
            pageCount == 3
    }

    void "test retrieveFilteredAnnotationsFromDB"() {
        List<Annotation> annotations = []

        service.metaClass.retrieveFilteredAnnotationsFromDB = { String filter, int offset ->
            if (!filter || offset != 0) []
            else {
                annotations.findAll { annotation -> annotation?.term?.toString().contains(filter) }
            }
        }

        when:
            List<Object> results = service.retrieveFilteredAnnotationsFromDB("", 0)

        then:
            results.size() == 0

        when:
            Annotation annotation = new Annotation(term: "Something", code: "C100023").save()
            annotations.add([term: annotation.term, id: annotation.id])
            results = service.retrieveFilteredAnnotationsFromDB("", 0)

        then:
            results.size() == 0

        when:
            results = service.retrieveFilteredAnnotationsFromDB("Something", 0)

        then:
            results.size() == 1

        when:
            results = service.retrieveFilteredAnnotationsFromDB("Something", 1)

        then:
            results.size() == 0
    }

    void "test saveNewAnnotation"() {

        when:
            service.saveNewAnnotation([], "C10002")

        then:
            Annotation.count == 0

        when:
            service.saveNewAnnotation(["one"], "C10002")

        then:
            Annotation.count == 1

        when:
            service.saveNewAnnotation(["two", "three"], "C10002")

        then:
            Annotation.count == 3

    }
}
