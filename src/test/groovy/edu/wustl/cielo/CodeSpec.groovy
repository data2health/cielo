package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class CodeSpec extends Specification implements DomainUnitTest<Code> {

    void setup() {
        mockDomain(UserAccount)
    }

    void "test saving code"() {
        UserAccount user
        Code code
        SoftwareLicense softwareLicense
        Project project

        when: "saving code with null name"
            code = new Code(name: null)

        then: "fails"
            !code.save()

        when: "saving code with name not null"
            code = new Code(name: "Some name")

        then: "fail"
            !code.save()

        when: "create a project and associate to a new code"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            code = new Code(name: "Some name", description: "Some description", repository: "repo", project: project)

        then:
            code.save()
    }

    void "test toString"() {
        UserAccount user
        Code code
        SoftwareLicense softwareLicense
        Project project

        given:
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            code = new Code(name: "Some name", description: "Some description", repository: "repo", project: project).save()

        when: "toString is called"
            def returnVal = code.toString()

        then: "returnval should be composed of 'name revision'"
            assert returnVal == code.name + " " + code.revision
    }
}
