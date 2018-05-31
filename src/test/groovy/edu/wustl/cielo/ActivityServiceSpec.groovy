package edu.wustl.cielo

import edu.wustl.cielo.enums.ActivityTypeEnum
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import grails.web.mapping.LinkGenerator
import spock.lang.Specification

class ActivityServiceSpec extends Specification implements ServiceUnitTest<ActivityService>, DomainUnitTest<Activity> {

    LinkGenerator grailsLinkGenerator
    SpringSecurityService springSecurityService

    def setup() {
        grailsLinkGenerator = Mock()
        service.grailsLinkGenerator = grailsLinkGenerator
        mockDomains(Institution, Profile)

        messageSource.addMessage('ACTIVITY_NEW_USER', Locale.getDefault(), "hello")
        springSecurityService = new SpringSecurityService()
    }

    void "test areThereMoreActivitiesToRetrieve"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()

        when:"no activities"
            def results = service.areThereMoreActivitiesToRetrieve(0, 10)

        then:
            !results

        when: "add some activities"
            new Activity(eventType: ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_COMMENTS,
                    eventTitle: "title", eventText: "text", activityInitiatorUserName: user.username).save()
            new Activity(eventType: ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_COMMENTS,
                eventTitle: "title2", eventText: "text2", activityInitiatorUserName: user.username).save()
            results = service.areThereMoreActivitiesToRetrieve(0, 1)

        then:
            results
    }


    void "test getGenericActivityParams"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        Code code = new Code(name: "Some name", description: "Some description", repository: "repo", project: project)


        when: "get generic activity params for new code"
            def result = service.getGenericActivityParams(user, code.id, code.class.simpleName)

        then:
            result[0] == "someuser"
            result[1] == "/code/" + code.id

    }

    void "test getNewActivityTypeEnum"() {
        ActivityTypeEnum activityTypeEnum
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()

        when: "new Team"
            activityTypeEnum = service.getNewActivityTypeEnum("Team")

        then:
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_NEW_TEAM

        when: "new Project"
            activityTypeEnum = service.getNewActivityTypeEnum("Project")

        then:
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_NEW_PROJECT

        when: "new UserAccount"
            activityTypeEnum = service.getNewActivityTypeEnum("UserAccount")
        then:
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_NEW_USER

        when: "new Data"
            activityTypeEnum = service.getNewActivityTypeEnum("Data")
        then:
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_NEW_UPLOAD_DATA

        when: "new Code"
            activityTypeEnum = service.getNewActivityTypeEnum("Code")
        then:
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_NEW_UPLOAD_CODE
    }

    void "test getUpdateActivityType"() {
        ActivityTypeEnum activityTypeEnum
        Institution institution
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
        SoftwareLicense softwareLicense = new SoftwareLicense(creator: user, body: "Some text\nhere.", label: "RER License 1.0",
                url: "http://www.rerlicense.com").save()
        Project project = new Project(projectOwner: user, name: "Project1", license: softwareLicense,
                description: "some description").save()
        institution = new Institution(fullName: "Washington University of St. Louis", shortName: "WUSTL").save()
        Profile profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu",
                institution: institution, user: user).save()

        when: ""
            activityTypeEnum = service.getUpdateActivityType(project, "shared")

        then: ""
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_SHARED

        when: ""
            activityTypeEnum = service.getUpdateActivityType(project, "name")
        then: ""
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_NAME

        when: ""
            activityTypeEnum = service.getUpdateActivityType(project, "description")
        then: ""
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_DESCRIPTION

        when: ""
            activityTypeEnum = service.getUpdateActivityType(project, "metadatas")

        then: ""
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_METADATAS

        when: ""
            activityTypeEnum = service.getUpdateActivityType(project, "status")
        then: ""
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_STATUS

        when: ""
            activityTypeEnum = service.getUpdateActivityType(profile, "emailAddress")
        then: ""
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_UPDATE_USER_EMAIL_ADDRESS

        when: ""
            activityTypeEnum = service.getUpdateActivityType(profile, "picture")
        then: ""
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_UPDATE_USER_PICTURE

        when: ""
            activityTypeEnum = service.getUpdateActivityType(profile, "interests")
        then: ""
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_UPDATE_USER_INTERESTS

        when: ""
            activityTypeEnum = service.getUpdateActivityType(profile, "institution")
        then: ""
            activityTypeEnum == ActivityTypeEnum.ACTIVITY_UPDATE_USER_INSTITUTION
    }

    void "test saveActivityForEvent"() {
        UserAccount user = new UserAccount(username: "admin", password: "somePassword").save()

        given: "No activities"
            Activity.list().size() == 0

        when: "saving activity for an event"
            service.saveActivityForEvent(ActivityTypeEnum.ACTIVITY_NEW_USER, "Test")

        then:
            Activity.list().size() == 1

    }
    void "test saveComment"() {
        UserAccount user = new UserAccount(username: "admin", password: "somePassword").save()

        given: "No activities and no comments"
            Activity.list().size() == 0
            Comment.list().size() == 0

        when: "we save an activity"
            service.saveActivityForEvent(ActivityTypeEnum.ACTIVITY_NEW_USER, "Test")

        then:
            Activity.list().size() == 1

        when: "add comment to existing activity"
            service.saveComment(Activity.list()[0].id,
                    "comment", user)

        then:
            Comment.list().size() ==  1
            Activity.list()[0].comments.size() == 1
    }

    void "test likeActivity" () {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        Activity activity

        springSecurityService.metaClass.principal = [id: user.id]
        service.springSecurityService = springSecurityService

        when: "add some activities"
            activity = new Activity(eventType: ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_COMMENTS,
                eventTitle: "title", eventText: "text", activityInitiatorUserName: user.username).save()
            service.likeActivity(activity.id)

        then:
            activity.likedByUsers.size() > 0

    }

    void "test removeActivityLike"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        Activity activity

        springSecurityService.metaClass.principal = [id: user.id]
        service.springSecurityService = springSecurityService

        when: "add some activities"
            activity = new Activity(eventType: ActivityTypeEnum.ACTIVITY_UPDATE_PROJECT_COMMENTS,
                eventTitle: "title", eventText: "text", activityInitiatorUserName: user.username).save()
            service.likeActivity(activity.id)

        then:
            activity.likedByUsers.size() > 0

        when:
            service.removeActivityLike(activity.id)

        then:
            activity.likedByUsers.size() == 0
    }

    void "test saveActivityForManualPost"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        boolean returnVal

        when:
            returnVal = service.saveActivityForManualPost(user, null, null)

        then:
            !returnVal

        when:
            returnVal = service.saveActivityForManualPost(user, null, "Message")

        then:
            !returnVal

        when:
            returnVal = service.saveActivityForManualPost(user, "title", null)

        then:
            !returnVal

        when:
            returnVal = service.saveActivityForManualPost(user, "title", "Message")

        then:
            returnVal
    }
}
