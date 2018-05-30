package edu.wustl.cielo

import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class TeamControllerSpec extends Specification implements ControllerUnitTest<TeamController>, DomainUnitTest<UserAccount> {

    TeamService teamService
    SpringSecurityService springSecurityService
    UserAccount user

    def setup() {
        teamService = new TeamService()
        springSecurityService = new SpringSecurityService()

        user =  new UserAccount(username: "someuser", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService
    }

    void "test getTeamMembers"() {
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Project project2 = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()

        teamService.metaClass.getTeamMembers = { Long teamId ->
            [user2.id]
        }

        views["/templates/_addUsersDialogContent.gsp"] = "mock data"

        project.teams.add( new Team(name: "Avengers"))
        project.save()


        when:
            controller.getTeamMembers()

        then:
            response.text
    }

    void "test updateTeamMembers"() {
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Project project2 = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()

        teamService.metaClass.updateTeamMembers = {UserAccount userAccount, Long teamId, List<Long> userIds ->
            return true
        }

        controller.teamService = teamService

        Team team = new Team(name: "Team1", administrator: user).save()

        when:
            params."users[]" = [user2.id]
            params.id = team.id
            controller.updateTeamUsers()

        then:
            response.json.success
    }

    void "test teamMemberSnippet"() {
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Project project2 = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        views["/team/_team.gsp"] = "mock data"


        when:
            params.teamId = team.id
            params.projectId = project.id
            controller.teamMembersSnippet()

        then:
            response.text == "mock data"
    }

    void "test deleteTeam"() {
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()
        Team team = new Team(name: "Team1", administrator: user).save()

        teamService.metaClass.deleteTeam = { Long teamId, Long projectId -> return true }
        controller.teamService = teamService

        when:
            params.teamId       = team.id
            params.projectId    = project.id
            controller.deleteTeam()

        then:
            response.json.success
    }

    void "test newTeamForm"() {
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description", shared: true).save()

        views["/team/_newTeam.gsp"] = "mock data"

        when:
            controller.newTeamForm()

        then:
            response.text == "mock data"
    }

    void "test view"() {
        when:
            controller.view()

        then:
            response.status == 302
            response.reset()

        when:
            SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
            Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                    description: "some description", shared: true).save()
            Team team = new Team(name: "Team1", administrator: user).save()
            params.id = team.id
            controller.view()

        then:
            response.status == 302
    }
}
