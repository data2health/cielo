package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class RestServiceSpec extends Specification implements ServiceUnitTest<RestService>, DomainUnitTest<Project> {

    ProjectService projectService

    def setup() {
        projectService = new ProjectService()
    }

    void "test getNumberOfPages"() {

        projectService.metaClass.countFilteredProjectsPages = { UserAccount user, boolean filterOnShared, String filterTerm, int max ->
            if (max == 5) return 1
            else return 2
        }

        service.projectService = projectService

        when:
            int pages = service.getNumberOfPages(false, "", 5)

        then:
            pages == 1

        when:
            pages = service.getNumberOfPages(false, "", 4)

        then:
            pages == 2
    }

    void "test getProjectData"() {
        List<Object> projectData

        projectService.metaClass.retrieveFilteredProjectsFromDB = { UserAccount user, boolean filterOnSharedOnly, String filterTerm, int offset, int max ->
            projectData
        }

        service.projectService = projectService

        when:
            projectData = service.getProjectData(false, "", 0, 5)

        then:
            projectData.size() == 0

        when:
            projectData.add([name: "Some Project", identity: 1])

        then:
            projectData.size() == 1
    }

    void "test getTotalNumberOfProjects"() {
        List<Project> projects = []

        projectService.metaClass.countFilteredProjects = { boolean filterOnShared, String filterTerm ->
            projects.size()
        }

        service.projectService = projectService

        when:
            int projectCount = service.getTotalNumberOfProjects(false, "")


        then:
            projectCount == 0

        when:
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
            SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            projects.add(project)
            projectCount = service.getTotalNumberOfProjects(false, "")

        then:
            projectCount == 1
    }
}
