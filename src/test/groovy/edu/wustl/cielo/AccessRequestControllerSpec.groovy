package edu.wustl.cielo

import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class AccessRequestControllerSpec extends Specification implements ControllerUnitTest<AccessRequestController>, DomainUnitTest<UserAccount> {

    AccessRequestService accessRequestService
    SpringSecurityService springSecurityService

    def setup() {
        mockDomain(AccessRequest)
        accessRequestService    = new AccessRequestService()
        springSecurityService   = new SpringSecurityService()
    }

    void "test list"(){
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        List<AccessRequest> accessRequestList = []

        accessRequestService.metaClass.getAccessRequests = { UserAccount user ->
            accessRequestList
        }

        springSecurityService.metaClass.principal = [id: userAccount.id]

        controller.accessRequestService     = accessRequestService
        controller.springSecurityService    = springSecurityService

        when:
            Map returnVal = controller.list()

        then:
            returnVal.messages == accessRequestList
            returnVal.messages.size() == 0

        when:
            AccessRequest accessRequest = new AccessRequest([mask: 1, user: userAccount, projectId: 1L, projectOwnerId: userAccount.id]).save()
            accessRequestList.add(accessRequest)
            returnVal = controller.list()

        then:
            returnVal.messages == accessRequestList
            returnVal.messages.size() == 1
            accessRequestList.size()  == 1
    }

    void "test getTableRows"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        List<AccessRequest> accessRequestList = []

        accessRequestService.metaClass.getAccessRequests = { UserAccount user ->
            accessRequestList
        }

        accessRequestService.metaClass.renderTableRows = { Map model ->
            "<div>hello</hello>"
        }

        springSecurityService.metaClass.principal = [id: userAccount.id]

        controller.accessRequestService     = accessRequestService
        controller.springSecurityService    = springSecurityService

        when:
            controller.getTableRows()

        then:
            response.json.html.contains("<div>hello</hello>")
            response.json.pagesCount == 1

        when:
            AccessRequest accessRequest = new AccessRequest([mask: 1, user: userAccount, projectId: 1L, projectOwnerId: userAccount.id]).save()
            accessRequestList.add(accessRequest)
            controller.getTableRows()

        then:
            response.json.html.contains("<div>hello</hello>")
            response.json.pagesCount == 1
    }
}
