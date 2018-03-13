package edu.wustl.cielo

import grails.gsp.PageRenderer
import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import grails.plugin.springsecurity.SpringSecurityService
import edu.wustl.cielo.enums.ActivityTypeEnum

class ActivityControllerSpec extends Specification implements ControllerUnitTest<ActivityController>, DomainUnitTest<Activity> {
    PageRenderer groovyPageRenderer
    ActivityService activityService
    SpringSecurityService springSecurityService
    CieloTagLib cieloTagLib

    def setup() {
        groovyPageRenderer = Mock()
        activityService = new ActivityService()
        springSecurityService = Mock()
        cieloTagLib = mockTagLib(CieloTagLib)
    }

    void "test getActivities"() {
        Activity activity

        when:"no activities"
            params.max = 3
            params.offset = 0

            activityService.metaClass.getActivities = { int offset, int max ->
                if (activity){
                    [activity]
                }
            }

            activityService.metaClass.areThereMoreActivitiesToRetrieve = { int newOffset, int max ->
                false
            }

            controller.activityService = activityService
            controller.springSecurityService
            controller.getActivities()

        then:
            response.text.trim() == "<div id=\"nextPage\" style=\"display: flex;justify-content: center;\">\n" +
                    "    \n" +
                    "        <p class=\"text-secondary\">Hurray! You have read all activity posts. Now get back to work!</p>\n" +
                    "    \n" +
                    "</div>"

        when: "we create activities"
            new UserAccount(username: "someuser", password: "somePassword")
            activity = new Activity()
            activity.activityInitiatorUserName = "someuser"
            activity.eventType  = ActivityTypeEnum.ACTIVITY_NEW_PROJECT
            activity.eventTitle = "Some event"
            activity.eventText  = "Some event just occurred. Do something here"
            activity.save()

        and:
            params.max = 3
            params.offset = 0
            controller.getActivities()

        then: "we have results"
            response.text.contains("Some event just occurred. Do something here")
    }

    void "test getComments"() {
        Activity activity

        when: "no activities"
            params.id = -1
            controller.getComments()
        then:
            response.text.trim() == ""

        when: "there is an activity with comments"
            new UserAccount(username: "someuser", password: "somePassword")
                activity = new Activity()
                activity.activityInitiatorUserName = "someuser"
                activity.eventType  = ActivityTypeEnum.ACTIVITY_NEW_PROJECT
                activity.eventTitle = "Some event"
                activity.eventText  = "Some event just occurred. Do something here"
                activity.save()
                activity.comments.add(new Comment(commenter: new UserAccount(username: "commenter", password: "somePassword").save(),
                        text: "some comment").save())
                activity.save()
            params.id = activity.id
            controller.getComments()

        then:
            response.text.contains("some comment")

    }

    void "test saveComment"() {
        Activity activity

        activityService.metaClass.saveComment = { Long activityId, String commentText, UserAccount commenterUser ->
            if (activity){
               true
            }
        }

        controller.activityService = activityService

        when: "no activities"
            params.text = "Some comment here"
            params.activityId = -1
            controller.saveComment()

        then: "no comment saved"
            !response.json.success
            response.reset()

        when: "adding an activity"
            new UserAccount(username: "someuser", password: "somePassword")
                activity = new Activity()
                activity.activityInitiatorUserName = "someuser"
                activity.eventType  = ActivityTypeEnum.ACTIVITY_NEW_PROJECT
                activity.eventTitle = "Some event"
                activity.eventText  = "Some event just occurred. Do something here"
                activity.save()

            params.text = "Some comment here"
            params.activityId = activity.id
            controller.saveComment()

        then: "we were able to save the comment"
            response.json.success
    }
}
