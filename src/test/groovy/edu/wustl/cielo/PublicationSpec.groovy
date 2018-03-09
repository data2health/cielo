package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PublicationSpec extends Specification implements DomainUnitTest<Publication> {

    void setup() {
        mockDomain(UserAccount)
    }

    void "test saving"() {
        Publication publication
        UserAccount user
        SoftwareLicense softwareLicense
        Project project

        when: "no props"
            publication = new Publication()

        then: "save fails"
            !publication.save()

        when: "label with project"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            publication = new Publication(label: "someLabel", project: project)

        then: "save is successful"
            publication.save()

        when: "label too short"
            publication = new Publication(label: "bel", project: project)

        then: "save fails"
            !publication.save()
    }

    void "test toString"() {
        Publication publication
        UserAccount user
        SoftwareLicense softwareLicense
        Project project

        given:
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            publication = new Publication(label: "someLabel", project: project).save()

        when: "when toString is called"
            def returnVal = publication.toString()

        then:
            assert returnVal == "${publication.label} (${publication.url})"
    }
}
