package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class HomeControllerSpec extends Specification implements ControllerUnitTest<HomeController>, DomainUnitTest<UserAccount> {

    void "test index page"() {
        UserAccount userAccount

        when: "not logged in"
            controller.springSecurityService = [principal: null]
            controller.index()

        then: "redirected to logging page"
            response.redirectedUrl == "/login/auth"
            response.reset()

        when: "user is logged in then no redirect"
            userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
            controller.springSecurityService = [principal: userAccount]
            def responseToIndex = controller.index()

        then: "should return the user data"
            responseToIndex.userInstance == userAccount
            response.reset()
    }
}
