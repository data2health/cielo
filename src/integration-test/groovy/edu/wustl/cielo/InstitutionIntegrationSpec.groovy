package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class InstitutionIntegrationSpec extends Specification {

    void cleanup() {
        Profile.list().each {
            it.delete()
        }

        Institution.list().each {
            it.delete()
        }
    }

    void "test saving"() {
        Institution institution

        when: "save with no props"
            institution = new Institution()

        then: "save fails"
            !institution.save()

        when: "save with fullname but no shortname"
            institution = new Institution(fullName: "Washington University of St. Louis")

        then: "save fails"
            !institution.save()

        when: "save with necessary props"
            institution = new Institution(fullName: "Washington University of St. Louis", shortName: "WUSTL")

        then: "save fails"
            institution.save()
    }
}
