package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class UserControllerSpec extends Specification implements ControllerUnitTest<UserController>, DomainUnitTest<UserAccount> {

    void "test view"() {
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
}
