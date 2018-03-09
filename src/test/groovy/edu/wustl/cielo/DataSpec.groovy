package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class DataSpec extends Specification implements DomainUnitTest<Data> {

    void setup() {
        mockDomain(UserAccount)
    }

    void "test something"() {
        UserAccount user
        Data data
        SoftwareLicense softwareLicense
        Project project

        when: "saving data with null name"
            data = new Data(name: null)

        then: "fails"
            !data.save()

        when: "saving data with name not null"
            data = new Data(name: "Some name")

        then: "fail"
            !data.save()

        when: "save with name and not null description"
            data = new Data(name: "Some name", description: "Some description")

        then: "save still fails due to missing props that are required"
            !data.save()

        when: "create a project and associate to a new data"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            data = new Data(name: "Some name", description: "Some description", repository: "repo", project: project)

        then: "save is successful"
            data.save()
    }

    void "test toString"() {
        UserAccount user
        Data data
        SoftwareLicense softwareLicense
        Project project

        given:
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            data = new Data(name: "Some name", description: "Some description", repository: "repo", project: project).save()

        when: "toString is called"
            def returnVal = data.toString()

        then: "returnval should be composed of 'name revision'"
            assert returnVal == data.name + " " + data.revision
    }
}
