package edu.wustl.cielo

import edu.wustl.cielo.enums.AccessRequestStatusEnum
import grails.gsp.PageRenderer
import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class AccessRequestServiceSpec extends Specification implements ServiceUnitTest<AccessRequestService>, DomainUnitTest<AccessRequest>{

    PageRenderer groovyPageRenderer

    def setup() {
        groovyPageRenderer = new PageRenderer()
        service.groovyPageRenderer = groovyPageRenderer
        mockDomain(UserAccount)
    }

    void "test createRequest"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project

        when:
            boolean returnVal = service.createRequest(1L, null, 1)

        then:
            !returnVal

        when:
            returnVal = service.createRequest(1L, userAccount, 1)

        then:
            !returnVal

        when:
            project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description").save()
            returnVal = service.createRequest(project.id, userAccount, 1)

        then:
            returnVal
    }

    void "test deleteRequest" () {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project

        when:
            project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description").save()
            boolean returnVal = service.createRequest(project.id, userAccount, 1)

        then:
            returnVal

        when:
            AccessRequest accessRequest = AccessRequest.all.first()
            service.deleteRequest(accessRequest)

        then:
            AccessRequest.all.size() == 0
    }

    void "test approveAccessRequest"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project

        when:
            project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description").save()
            boolean returnVal = service.createRequest(project.id, userAccount, 1)

        then:
            returnVal

        when:
            returnVal = service.approveAccessRequest(AccessRequest.first())

        then:
            returnVal
            AccessRequest.first().status == AccessRequestStatusEnum.APPROVED
    }

    void "test denyAccess"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project

        when:
            project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            boolean returnVal = service.createRequest(project.id, userAccount, 1)

        then:
            returnVal

        when:
            returnVal = service.denyAccess(AccessRequest.first().id, userAccount.id)

        then:
            returnVal
            AccessRequest.first().status == AccessRequestStatusEnum.DENIED
    }

    void "test getAccessRequests"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project
        List<AccessRequest> accessRequestList

        when:
            accessRequestList = service.getAccessRequests(userAccount, 5, 0)

        then:
            accessRequestList.size() == 0

        when:
            project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            boolean returnVal = service.createRequest(project.id, userAccount, 1)

        then:
            returnVal

        when:
            accessRequestList = service.getAccessRequests(userAccount, 5 , 0)

        then:
            accessRequestList.size() == 1
    }

    void "test countAccessRequests"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project

        when:
            int count = service.countAccessRequests(userAccount)

        then:
            count == 0

        when:
            project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                    description: "some description").save()
            boolean returnVal = service.createRequest(project.id, userAccount, 1)

        then:
            returnVal

        when:
            count = service.countAccessRequests(userAccount)

        then:
            count == 1
    }

    void "test getPagesCount"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description").save()

        when:
            int pages = service.getPagesCount(userAccount, 5)

        then:
            pages == 1

        when:
            service.createRequest(project.id, userAccount, 1)
            pages = service.getPagesCount(userAccount, 1)

        then:
            pages == 1 //still one

        when:
            service.createRequest(project.id, userAccount, 1)
            pages = service.getPagesCount(userAccount, 1)

        then:
            pages == 2
    }

    void "test renderTableRows"() {
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: userAccount, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: userAccount, name: "Project1", license: softwareLicense,
                description: "some description").save()
        List<AccessRequest> accessRequestList


        groovyPageRenderer.metaClass.render = { Map args ->
            if (args.model.requests) return "Some Requests"
            else return null
        }

        service.groovyPageRenderer = groovyPageRenderer

        when:
            accessRequestList = service.getAccessRequests(userAccount, 5, 0)
            String returnVal = service.renderTableRows([requests: accessRequestList])

        then:
            !returnVal

        when:
            service.createRequest(project.id, userAccount, 1)
            accessRequestList = service.getAccessRequests(userAccount, 5, 0)
            returnVal = service.renderTableRows([requests: accessRequestList])

        then:
            returnVal
            returnVal == "Some Requests"
    }
}
