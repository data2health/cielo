package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class InstitutionServiceSpec extends Specification implements ServiceUnitTest<InstitutionService>,
        DomainUnitTest<Institution> {

    def webRoot

    void setup() {
        mockDomain Annotation
        webRoot = new File(".").canonicalPath +  "/src/main/webapp/"
    }

    void "test setup mock institutions"() {
        expect:"fix me"
            Institution.list() == []

        when: "calling service to initialize annotations"
            service.setupMockInstitutions(null)

        then: "still empty"
            Institution.list() == []

        when: "calling service to initialize again with proper file"
            service.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))

        then: "now there are annotations saved"
            Institution.list() != []

    }
}
