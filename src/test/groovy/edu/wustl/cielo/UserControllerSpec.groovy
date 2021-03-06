package edu.wustl.cielo

import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class UserControllerSpec extends Specification implements ControllerUnitTest<UserController>, DomainUnitTest<UserAccount> {

    UserAccountService userAccountService
    SpringSecurityService springSecurityService
    TeamService teamService
    ProjectService projectService

    void setup() {
        mockDomains(Institution, Profile)

        messageSource.addMessage('user.failed.update', Locale.getDefault(), "Failed to update user")
        messageSource.addMessage('user.failed.follow', Locale.getDefault(), "Failed to follow user")
        messageSource.addMessage('user.failed.un-follow', Locale.getDefault(), "Failed to un-follow user")

        userAccountService = new UserAccountService()
        springSecurityService = new SpringSecurityService()
        teamService = new TeamService()
        projectService = new ProjectService()
    }

    void "test view"() {
        teamService.metaClass.getListOfTeamsUserOwns = { UserAccount user ->
            []
        }
        
        projectService.metaClass.getProjectsUserContributesTo = { UserAccount user ->
            []
        }

        controller.projectService = projectService
        controller.teamService = teamService
        controller.userAccountService = userAccountService

        when:"attempting to get user with no user id param"
            def returnVal = controller.view()

        then: "no user is returned"
            !returnVal.user

        when: "user id is provided"
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
            params.id =  user.id
            returnVal = controller.view()

        then: "user is returned"
            returnVal.user
    }

    void "test updateUser"() {

        when: "user id is provided"
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
            Profile profile  = new Profile()
            profile.user = user
            profile.emailAddress = "ricardo.rodriguez@wustl.edu"
            profile.institution  = new Institution(fullName: "WAshington University in St. Louis", shortName: "WashU").save()
            profile.firstName    = "Some"
            profile.lastName     = "User"
            profile.save()

            assert user.timezoneId != "EST"
            params.userId =  user.id
            params.timezoneId = "EST"
            controller.userAccountService = userAccountService
            controller.updateUser()

        then: "user is returned"
            user.timezoneId == "EST"
    }

    void "test followUser"(){

        given:
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
            UserAccount toFollow = new UserAccount(username: "followMe", password: "somePassword").save()
            springSecurityService.metaClass.principal = [id: user.id]
            controller.userAccountService = userAccountService
            controller.springSecurityService = springSecurityService

        when:
            controller.followUser()

        then:
            !user.connections.contains(toFollow)

        when:
            params.id = toFollow.id
            controller.followUser()

        then:
            user.connections.contains(toFollow)
    }

    void "test unFollowUser"(){
        given:
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
            UserAccount toFollow = new UserAccount(username: "followMe", password: "somePassword").save()
            springSecurityService.metaClass.principal = [id: user.id]
            controller.userAccountService = userAccountService
            controller.springSecurityService = springSecurityService

        when:
            controller.followUser()

        then:
            !user.connections.contains(toFollow)

        when:
            params.id = toFollow.id
            controller.followUser()

        then:
            user.connections.contains(toFollow)

        //now unfollow and should be back to original state
        when:
            params.id = toFollow.id
            controller.unFollowUser()

        then:
            !user.connections.contains(toFollow)

    }

    void "test getUsersIFollow"() {
        given:
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
            UserAccount user2 = new UserAccount(username: "followMe", password: "somePassword").save()
            springSecurityService.metaClass.principal = [id: user.id]
            controller.userAccountService = userAccountService
            controller.springSecurityService = springSecurityService

            views["/templates/_addUsersDialogContent.gsp"] = "mock view"
        when:
            controller.getUsersIFollow()

        then:
            response.text == "mock view"
    }

    void "test updateUsersIFollow"() {
        given:
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
            UserAccount user2 = new UserAccount(username: "followMe", password: "somePassword").save()
            springSecurityService.metaClass.principal = [id: user.id]
            controller.userAccountService = userAccountService
            controller.springSecurityService = springSecurityService

        when:
            controller.updateUsersIFollow()

        then:
            response.json.success
            user.connections.size() == 0
            response.reset()

        when:
            params."users[]" = [user2.id.toString()]
            controller.updateUsersIFollow()

        then:
            response.json.success
            user.connections.size() == 1
            user.connections.contains(user2)
    }
}
