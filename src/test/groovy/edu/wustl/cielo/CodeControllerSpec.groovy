package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class CodeControllerSpec extends Specification implements ControllerUnitTest<CodeController>, DomainUnitTest<Code> {

    void setup() {
        mockDomains(SoftwareLicense, Project)
        messageSource.addMessage('code.doesNotExist', Locale.getDefault(), "Code does not exist")
    }

    void "test view"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword")

        when:
            controller.view()

        then:
            response.status == 302
            flash.danger
            response.reset()

        when:
            flash.danger = null
            SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description", shared: true).save()
            Code code = new Code(name: "Some name", description: "Some description", repository: "repo", project: project).save()
            params.id = code.id
            controller.view()

        then:
            response.status == 302
            !flash.danger
    }
}
