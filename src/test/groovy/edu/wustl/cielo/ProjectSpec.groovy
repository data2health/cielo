package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ProjectSpec extends Specification implements DomainUnitTest<Project> {

    void "test something"() {
        Project project
        UserAccount user
        SoftwareLicense softwareLicense

        when: "save project with no props"
            project = new Project()

        then: "fails to save"
            !project.save()

        when: "create with not null description and name only"
            project = new Project(name: "Project1",description: "desc")

        then: "save fails"
            !project.save()

        when: "add user as well"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            project = new Project(name: "Project1",description: "desc", projectOwner: user)

        then: "save still fails because of missing props"
            !project.save()

        when: "save with all required props"
            softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
            project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
            description: "some description")

        then: "saves correctly"
            project.save()
    }

    void "test isTeamAssignedToProject"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        when:
            def result = project.isTeamAssignedToProject(team)

        then:
            !result

        when:
            project.teams.add(team)
            project.save()
            result = project.isTeamAssignedToProject(team)

        then:
            result
    }
}
