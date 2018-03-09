package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class UserAccountSpec extends Specification implements DomainUnitTest<UserAccount> {

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
}
