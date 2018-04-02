package edu.wustl.cielo

import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.*

class ProjectControllerSpec extends Specification implements ControllerUnitTest<ProjectController>, DomainUnitTest<Project> {

    ProjectService projectService
    SpringSecurityService springSecurityService

    void setup() {
        projectService = Mock()
        controller.projectService = new ProjectService()
        springSecurityService = new SpringSecurityService()
        mockDomains(Profile, UserAccount, UserAccountUserRole)
    }

    void "test getMostPopularProjects"() {
        List bundles = []

        controller.projectService.metaClass.getMostViewedProjects = { int maxNumberOfProjects, boolean sharedOnly ->
            bundles
        }

        given:
            Project.list().size() == 0

        when:
            controller.getMostPopularProjects()

        then:
            !response.json.projects

        when:
            response.reset()
            params.publicOnly = true
            params.max = 1
            UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            bundles.add(project)
            controller.getMostPopularProjects()

        then:
            response.json.projects
     }

    void "test view"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        user = new UserAccount(username: "someuser", password: "somePassword").save()
        controller.springSecurityService = springSecurityService
        controller.springSecurityService.metaClass.principal = [id: user.id]
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()

        given:
            params.id = project.id

        when:
            controller.view()

        then:
            response.status == 200
    }
}