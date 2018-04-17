package edu.wustl.cielo

import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class CieloTagLibSpec extends Specification implements TagLibUnitTest<CieloTagLib>, DomainUnitTest<UserAccount> {

    SpringSecurityService springSecurityService

    void setup() {
        mockDomain(Profile)
        springSecurityService = new SpringSecurityService()
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

    void "test dateDiff"() {
        Date date = new Date()
        def result

        when:
            result = tagLib.dateDiff([date: date])

        then:
            result.contains("today")

        when:
            result = tagLib.dateDiff([date: date - 1])

        then:
            result.contains("yesterday")

        when:
            result = tagLib.dateDiff([date: date - 7])

        then:
            result.contains("1 week ago")

        when:
            result = tagLib.dateDiff([date: date - 31])

        then:
            result.contains("1 month ago")

        when:
            result = tagLib.dateDiff([date: date - 730])

        then:
            result.contains("2 years ago")
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
}
