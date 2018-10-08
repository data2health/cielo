package edu.wustl.cielo

import com.sun.org.apache.xpath.internal.operations.Bool
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class CieloTagLibSpec extends Specification implements TagLibUnitTest<CieloTagLib>, DomainUnitTest<UserAccount> {

    SpringSecurityService springSecurityService
    ProjectService projectService

    void setup() {
        mockDomain(Profile)
        springSecurityService = new SpringSecurityService()
        projectService = new ProjectService()
    }

    void "test rawOutput"() {
        when:"passing in an html string"
            def result = tagLib.rawOutput([text: "<div><span>some span</span></div>"], null)

        then: "the output should be same as the input"
            result == "<div><span>some span</span></div>"

    }

    void "test formatDateWithTimezone"() {
        when:"passing in an html string"
            Date date = new Date()
            def result = tagLib.formatDateWithTimezone([date: date], null)

        then:
            result == date.format("EEE MMM d yyyy @ hh:mm:ss z", TimeZone.getDefault())
    }

    void "test loggedInUserCanMakeChangesToUser"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        def result

        springSecurityService.metaClass.principal = [id: user.id]
        tagLib.springSecurityService = springSecurityService

        when:
          result = tagLib.loggedInUserCanMakeChangesToUser([user: user2], "<div>something to show</div>")

        then:
            !result

        when:
            result = tagLib.loggedInUserCanMakeChangesToUser([user: user], "<div>something to show</div>")

        then:
            result == "<div>something to show</div>"
    }

    void "test userCanMakeChangesToProject"() {
        def result
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description")

        springSecurityService.metaClass.principal = [id: user2.id]
        tagLib.springSecurityService = springSecurityService

        when:
            result = tagLib.userCanMakeChangesToProject([project: project])

        then:
            !result

        when:
            springSecurityService.metaClass.principal = [id: user.id]
            result = tagLib.userCanMakeChangesToProject([project: project], "<div>something to show</div>")

        then:
            result
    }

    void "test userOwnsProject"() {
        def result
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 =  new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description")

        springSecurityService.metaClass.principal = [id: user2.id]
        tagLib.springSecurityService = springSecurityService

        when:
            result = tagLib.userOwnsProject([project: project])

        then:
            !result

        when:
            springSecurityService.metaClass.principal = [id: user.id]
            result = tagLib.userOwnsProject([project: project], "<div>something to show</div>")

        then:
            result
    }

    void "test projectVisibility"() {
        def result
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description")

        springSecurityService.metaClass.principal = [id: user.id]
        tagLib.springSecurityService = springSecurityService

        when:
            result = tagLib.projectVisibility([value: project.shared])

        then:
            result == "Private"

        when:
            project.shared = true
            project.save()
            result = tagLib.projectVisibility([value: project.shared])

        then:
            result == "Public"

    }

    void "test booleanOut"() {
        def result

        when:
            result = tagLib.booleanOut([value: true])

        then:
            result == "Yes"

        when:
            result = tagLib.booleanOut([value: false])

        then:
            result == "No"
    }

    void "test getUserProfilePic"() {
        def result
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()

        springSecurityService.metaClass.principal = [id: user.id]
        tagLib.springSecurityService = springSecurityService

        when:
            result = tagLib.getUserProfilePic()

        then:
            result.contains("/assets/default_profile.png")
    }

    void "test getUserProfilePicNotRaw"() {
        def result
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()

        springSecurityService.metaClass.principal = [id: user.id]
        tagLib.springSecurityService = springSecurityService

        when:
        result = tagLib.getUserProfilePicNotRaw()

        then:
        result.contains("/assets/default_profile.png")
    }

    void "test getSoftwareLicenseOptions"() {
        UserAccount user

        given:
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                    url: "http://www.rerlicense.com").save()
        when:
            def result = tagLib.getSoftwareLicenseOptions()

        then:
            SoftwareLicense.list().each { license ->
                result.contains(license.label)
            }
    }

    void "test customTimeZoneSelect"() {

        when:
            def result = tagLib.customTimeZoneSelect()

        then:
            result.contains("UTC")
            result.contains("Asia/Dubai")
            result.contains("US/Eastern")

    }

    void "test userOwnsTeam"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()

        springSecurityService.metaClass.principal = [id: user.id]
        tagLib.springSecurityService = springSecurityService

        when:
            def result = tagLib.userOwnsTeam([team: null])

        then:
            !result

        when:
            Team team   = new Team(administrator: user, name: "The Avengers")
            result      = tagLib.userOwnsTeam([team: team])

        then:
            result != null
    }

    void "test userContributesToTeam"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        springSecurityService.metaClass.principal = [id: user2.id]
        tagLib.springSecurityService = springSecurityService

        when:
            def result = tagLib.userContributesToTeam([team: null]) { true }

        then:
            !result

        when:
            result = tagLib.userContributesToTeam([team: team]) { true }

        then:
            !result

        when:
            team.members.add(user2)
            team.save()
            result = tagLib.userContributesToTeam([team: team]) { true }

        then:
            result
    }

    void "test doesUserOwnOrContributeToTeam"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        Team team = new Team(name: "Team1", administrator: user).save()

        springSecurityService.metaClass.principal = [id: user2.id]
        tagLib.springSecurityService = springSecurityService

        when:
            def result = tagLib.doesUserOwnOrContributeToTeam([team: team])

        then:
            result

        when:
            team.members.add(user2)
            team.save()
            result = tagLib.doesUserOwnOrContributeToTeam([team: team])

        then:
            result

        when:
            springSecurityService.metaClass.principal = [id: user.id]
            tagLib.springSecurityService = springSecurityService
            result = tagLib.doesUserOwnOrContributeToTeam([team: team])

        then:
            result
    }

    void "test doesUserOwnProject"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Ricky's Project", license: softwareLicense,
                description: "some description")

        springSecurityService.metaClass.principal = [id: user2.id]
        tagLib.springSecurityService = springSecurityService

        when:
            def result = tagLib.doesUserOwnProject([project: null])

        then:
            !Boolean.valueOf(result.toString())

        when:
            result = tagLib.doesUserOwnProject([project: project])

        then:
            Boolean.valueOf(result.toString())
    }

    void "test getProjectsForTeam"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Ricky's 2nd Project", license: softwareLicense,
                description: "some description", shared: true)
        Project project2 = new Project(projectOwner: user, name: "Ricky's 3rd Project", license: softwareLicense,
                description: "some description")
        Team team   = new Team(administrator: user, name: "The Avengers").save()

        projectService.metaClass.getListOfProjectsTeamContributesTo = { Team projectTeam ->
            def listOfProjects = []
            if (project.teams.contains(projectTeam))  listOfProjects.add(project)
            if (project2.teams.contains(projectTeam)) listOfProjects.add(project2)

            return listOfProjects
        }

        tagLib.projectService = projectService

        when:
            def results = tagLib.getProjectsForTeam([team: team])

        then:
            results == "<em>None yet</em>"

        when:
            project.teams.add(team)
            project.save()
            results = tagLib.getProjectsForTeam([team: team])

        then:
            results.indexOf("<a class='btn btn-link' href='/project/${project.id}' style='padding: 0; margin:0'>${project.name}</a>") != -1

        when:
            project2.teams.add(team)
            project2.save()
            results = tagLib.getProjectsForTeam([team: team])

        then:
            results.indexOf("<a class='btn btn-link' href='/project/${project.id}' style='padding: 0; margin:0'>${project.name}</a>") != -1
    }
}
