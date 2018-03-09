package edu.wustl.cielo.meta

import edu.wustl.cielo.Project
import edu.wustl.cielo.SoftwareLicense
import edu.wustl.cielo.UserAccount
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class MetadataSpec extends Specification implements DomainUnitTest<Metadata> {

    void setup() {
        mockDomain(UserAccount)
    }

    void "test saving"() {
        UserAccount user
        SoftwareLicense softwareLicense
        Project project
        Metadata metadata

        given:
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        when: "no props"
            metadata = new Metadata()

        then: "save fails"
            !metadata.save()

        when: "add key"
            metadata = new Metadata(key: "something")

        then: "still fails due to missing value prop"
            !metadata.save()

        when: "add key, value and project"
            metadata = new Metadata(key: "something", value: "something's value", project: project)

        then: "save successful"
            metadata.save()
    }

    void "test toString"() {
        UserAccount user
        SoftwareLicense softwareLicense
        Project project
        Metadata metadata

        given:
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            metadata = new Metadata(key: "something", value: "something's value", project: project).save()

        when: "toString is called"
            def returnVal = metadata.toString()

        then:
            assert returnVal == "${metadata.key}: ${metadata.value}"
    }
}
