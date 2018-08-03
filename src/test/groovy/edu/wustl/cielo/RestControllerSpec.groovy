package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class RestControllerSpec extends Specification implements ControllerUnitTest<RestController>, DomainUnitTest<UserAccount> {

    RestService restService

    def setup() {
        restService             = new RestService()
    }

    void "test appVersion"() {

        when:
            controller.appVersion()

        then:
            response.json.version == grailsApplication.config.getProperty("info.app.version")

    }

    void "test getListOfProjects"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project2 = new Project(projectOwner: user, name: "Project2", license: softwareLicense,
                description: "some description").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        restService.metaClass.getNumberOfPages = { boolean filterOnSharedOnly, String filterTerm, int max ->
            if (!max || max <= 0) return 1
            else {
               return Math.round( Math.ceil(2 / max) )
            }
        }

        restService.metaClass.getTotalNumberOfProjects = { boolean filterOnSharedOnly, String filterTerm ->
           return 2
        }

        restService.metaClass.getProjectData = { boolean filterOnSharedOnly, String filterTerm, int offset, int max ->
           return [[identity: project.id], [identity: project2.id]]
        }

        controller.restService  = restService

        when:
            controller.getListOfProjects()

        then:
            response.json.numberOfPages == 1
            response.json.totalCount    == 2

        when:
            response.reset()
            params.max = 1
            controller.getListOfProjects()

        then:
            response.json.numberOfPages == 2
            response.json.totalCount    == 2
    }
}
