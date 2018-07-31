package edu.wustl.cielo

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AccessRequestSpec extends Specification implements DomainUnitTest<AccessRequest> {

    void setup() {
        mockDomain(UserAccount)
    }

    void "test save"() {
        AccessRequest accessRequest
        UserAccount userAccount

        when:
            accessRequest = new AccessRequest([mask: -1, projectId: -1L, projectOwnerId: -1L])
            accessRequest.save()

        then:
            !accessRequest.id

        when:
            userAccount = new UserAccount(username: "someuser", password: "somePassword")
            userAccount.save()
            accessRequest = new AccessRequest([mask: -1, user: userAccount, projectId: -1L, projectOwnerId: -1L])
            accessRequest.save()

        then:
            !accessRequest.id

        when:
            accessRequest = new AccessRequest([mask: 1, user: userAccount, projectId: -1L, projectOwnerId: -1L])
            accessRequest.save()

        then:
            !accessRequest.id

        when:
            accessRequest = new AccessRequest([mask: 1, user: userAccount, projectId: 1L, projectOwnerId: userAccount.id])
            accessRequest.save()

        then:
            accessRequest.id
    }
}
