package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class UserAccountIntegrationSpec extends Specification {

    void "test saving account"() {
        UserAccount user

        when: "with no props"
            user = new UserAccount()

        then: "save fails"
            !user.save()

        when: "no password but with username"
            user = new UserAccount(username: "someuser")

        then: "save fails"
            !user.save()

        when: "no username but with password"
            user = new UserAccount(password: "somePassword")

        then: "save fails"
            !user.save()

        when: "user with required props"
            user = new UserAccount(username: "someuser", password: "somePassword")

        then: "save passes"
            user.save()
    }

    void "test getListOfTeamsIBelongTo"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()
        Team team2 = new Team(name: "Team2", administrator: user2).save()
        List<Team> teams

        when:
            teams = user.getListOfTeamsIBelongTo()

        then:
            !teams

        when:
            team2.addToMembers(user)
            team2.save()
            teams = user.getListOfTeamsIBelongTo()

        then:
            teams
            teams.size() == 1
    }

    void "test getProjectsIContributeTo"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        UserAccount user3 = new UserAccount(username: "someuser3", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user3).save()
        Team team2 = new Team(name: "Team2", administrator: user3).save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project2 = new Project(projectOwner: user2, name: "Project1", license: softwareLicense,
                description: "some description").save()
        List<Project> projects

        when:
            projects = user.getProjectsIContributeTo()

        then:
            !projects
            projects.size() == 0

        when:
            team.addToMembers(user)
            team.save()
            project.addToTeams(team)
            project.save()
            projects = user.getProjectsIContributeTo()

        then:
            projects
            projects.size() == 1

        when:
            team2.addToMembers(user)
            team2.save()
            project2.addToTeams(team2)
            project2.save()
            projects = user.getProjectsIContributeTo()

        then:
            projects
            projects.size() == 2
    }
}
