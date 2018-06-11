package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class TeamIntegrationSpec extends Specification {

    void "test saving"() {
        int numberOfMemebers = 5
        Team team
        UserAccount user

        when: "no props"
            team = new Team()

        then: "fails to save"
            !team.save()

        when: "Adding a team name only"
            team = new Team(name: "Team1")

        then: "fails due to missing administrator"
            !team.save()

        when: 'Adding name and administrator'
            user = new UserAccount(username: "someuser", password: "somePassword")
            team = new Team(name: "Team1", administrator: user)

        then: "save is successful"
            team.save()
            assert team.members == []

        when: "adding members still saves"
            numberOfMemebers.times {
                UserAccount someUser = new UserAccount(username: "someuser" + it, password: "somePassword").save()
                team.addToMembers(someUser)
            }

        then: "save still is successful"
            team.save()
            assert team.members
            assert team.members.size() == 5
    }

    void "test toString"() {
        Team team
        UserAccount user

        given:
            user = new UserAccount(username: "someuser", password: "somePassword")
            team = new Team(name: "Team1", administrator: user).save()

        when: "retrieving to String"
            def returnVal = team.toString()

        then: "assert toString returns team name"
            assert returnVal == team.name
    }

    void "test listAllProjectsThatHaveThisTeamAssigned"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()
        SoftwareLicense   softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Project project2 = new Project(projectOwner: user, name: "Project2", license: softwareLicense,
                description: "some description").save()
        Project project3 = new Project(projectOwner: user, name: "Project3", license: softwareLicense,
                description: "some description").save()
        List<Project> projects

        when:
            projects = team.listAllProjectsThatHaveThisTeamAssigned()

        then:
            !projects
            projects.size() == 0

        when:
            project.addToTeams(team)
            projects = team.listAllProjectsThatHaveThisTeamAssigned()

        then:
            projects
            projects.contains(project)
            projects.size() == 1

        when:
            project3.addToTeams(team)
            projects = team.listAllProjectsThatHaveThisTeamAssigned()

        then:
            projects
            projects.contains(project)
            projects.contains(project3)
            projects.size() == 2
    }
}
