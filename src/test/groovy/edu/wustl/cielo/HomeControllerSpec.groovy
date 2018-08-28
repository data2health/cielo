package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import grails.plugin.springsecurity.SpringSecurityService

class HomeControllerSpec extends Specification implements ControllerUnitTest<HomeController>, DomainUnitTest<UserAccount> {

    TeamService teamService
    ActivityService activityService
    ProjectService projectService
    SpringSecurityService springSecurityService
    UserAccountService userAccountService

    void setup() {
        mockDomains(Profile)
        projectService = Mock()
        teamService = Mock()
        activityService = Mock()
        userAccountService = Mock()
        springSecurityService = new SpringSecurityService()
    }

    void "test index page"() {
        int isLoggedInCalls = 0

        when: "not logged in"
            controller.projectService = projectService
            controller.springSecurityService = springSecurityService
            controller.springSecurityService.metaClass.isLoggedIn = { ->
                isLoggedInCalls++
                false
            }
            controller.index()

        then: "you see the landing page which is no secured"
            response.status == 200
            isLoggedInCalls == 1
            response.reset()

        when: "user is logged in then no redirect"
            controller.springSecurityService.metaClass.isLoggedIn = { ->
                isLoggedInCalls++
                true
            }
            controller.index()

        then: "should return the user data"
            response.redirectedUrl =="/home"
            isLoggedInCalls == 2
            response.reset()
    }

    void "test home"(){

        controller.activityService = activityService
        controller.userAccountService = userAccountService
        controller.teamService = teamService

        when: "not logged in"
            controller.projectService = projectService
            controller.springSecurityService = springSecurityService
            controller.springSecurityService.metaClass.principal = [id: -1]
            controller.home()

        then: "redirected to login page"
            response.redirectedUrl == "/login/auth"
            response.reset()

        when: "user is logged in - mock"
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword")
            user.save()
            controller.springSecurityService.principal = [id: user.id]
            controller.home()

        then:
            !response.redirectedUrl
            response.status == 200
            response.reset()

        when:
            userAccountService = new UserAccountService()
            userAccountService.metaClass.isUserApiUserOnly = { UserAccount userAccount ->
                true
            }
            controller.userAccountService = userAccountService
            controller.home()

        then:
            response.redirectedUrl
            response.status == 302

    }

    void "test sidebarLeft"() {
        controller.activityService = activityService
        controller.teamService = teamService
        controller.projectService = projectService
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword")
        user.save()

        springSecurityService.metaClass.principal = [id: user.id]
        controller.springSecurityService = springSecurityService
        controller.userAccountService = userAccountService

        when:
            controller.sidebarLeft()

        then:
            response.text.contains("Following")
    }

}
