package edu.wustl.cielo

import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class InstitutionServiceIntegrationSpec extends Specification {

    @Autowired
    InstitutionService institutionService

    @Autowired
    SessionFactory sessionFactory

    String webRoot

    void setup() {
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
    }

    void cleanup() {
        Institution.list().each {
            it.delete()
        }
    }

    void "test setup mock institutions"() {
        expect:"fix me"
            Institution.list() == []

        when: "calling service to initialize annotations"
            institutionService.setupMockInstitutions(null)

        then: "still empty"
            Institution.list() == []

        when: "calling service to initialize again with proper file"
            institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
            sessionFactory.getCurrentSession().flush()

        then: "now there are annotations saved"
            Institution.list() != []

    }
}
