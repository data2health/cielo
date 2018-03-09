package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class InstitutionSpec extends Specification implements DomainUnitTest<Institution> {

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
