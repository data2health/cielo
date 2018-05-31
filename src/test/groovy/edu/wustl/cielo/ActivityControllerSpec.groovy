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
        mockDomain(Profile)
        groovyPageRenderer = Mock()
        activityService = new ActivityService()
        springSecurityService = new SpringSecurityService()
        cieloTagLib = mockTagLib(CieloTagLib)
        messageSource.addMessage('activity.post.failure', Locale.getDefault(), "Failed to add post")
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
            views['/activity/_singleActivity.gsp'] = "some text here"
            controller.getActivities()

        then:
            response.text.contains("Hurray! You have read all activity posts. Now get back to work!")

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
            views['/activity/_singleActivity.gsp'] = activity.eventText
            controller.getActivities()

        then: "we have results"
            response.text.contains("id=\"activity_post_${activity.id}")
    }

    void "test getComments"() {
        Activity activity

        when: "no activities"
            params.id = -1
            controller.getComments()
        then:
            response.text.trim() == ""

        when: "there is an activity with comments"
                UserAccount user = new UserAccount(username: "commenter", password: "somePassword").save()
                Institution institution = new Institution(fullName: "Washington University of St. Louis", shortName: "WUSTL").save()
                Profile profile = new Profile(firstName: "Ricky", lastName: "Rodriguez", emailAddress: "rrodriguez@wustl.edu",
                        institution: institution, user: user)
                profile.save()
                activity = new Activity()
                activity.activityInitiatorUserName = "someuser"
                activity.eventType  = ActivityTypeEnum.ACTIVITY_NEW_PROJECT
                activity.eventTitle = "Some event"
                activity.eventText  = "Some event just occurred. Do something here"
                activity.save()
                activity.comments.add(new Comment(commenter: user,
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

    void "test getActivity"() {
        Activity activity

        when: "adding an activity"
            new UserAccount(username: "someuser", password: "somePassword")
            activity = new Activity()
            activity.activityInitiatorUserName = "someuser"
            activity.eventType  = ActivityTypeEnum.ACTIVITY_NEW_PROJECT
            activity.eventTitle = "Some event"
            activity.eventText  = "Some event just occurred. Do something here"
            activity.save()

            views['/activity/_singleActivity.gsp'] = activity.eventText

            params.text = "Some comment here"
            params.id = activity.id
            controller.getActivity()

        then: "we were able to save the comment"
            response.text.contains(activity.eventText)

    }

    void "test likeActivity"() {
        Activity activity

        activityService.metaClass.likeActivity = { Long activityId ->
            true
        }
        controller.activityService = activityService

        when: "adding an activity"
            new UserAccount(username: "someuser", password: "somePassword")
            activity = new Activity()
            activity.activityInitiatorUserName = "someuser"
            activity.eventType  = ActivityTypeEnum.ACTIVITY_NEW_PROJECT
            activity.eventTitle = "Some event"
            activity.eventText  = "Some event just occurred. Do something here"
            activity.save()
            params.id = activity.id
            controller.likeActivity()

        then:
            response.json.success

    }

    void "test removeActivityLike"() {
        Activity activity

        activityService.metaClass.removeActivityLike = { Long activityId ->
            true
        }
        controller.activityService = activityService

        when: "adding an activity"
            new UserAccount(username: "someuser", password: "somePassword")
            activity = new Activity()
            activity.activityInitiatorUserName = "someuser"
            activity.eventType  = ActivityTypeEnum.ACTIVITY_NEW_PROJECT
            activity.eventTitle = "Some event"
            activity.eventText  = "Some event just occurred. Do something here"
            activity.save()
            params.id = activity.id
            controller.removeActivityLike()

        then:
            response.json.success

    }

    void "test getCommentLikeUsers"() {
        Activity activity
        UserAccount user

        activityService.metaClass.getUsersWhoLikedComment = { Long activityId ->
            [user]
        }
        controller.activityService = activityService

        views["/templates/_commentLikesUsers.gsp"] = "mock data"

        when: "adding an activity"
            user =  new UserAccount(username: "someuser", password: "somePassword")
            activity = new Activity()
            activity.activityInitiatorUserName = "someuser"
            activity.eventType  = ActivityTypeEnum.ACTIVITY_NEW_PROJECT
            activity.eventTitle = "Some event"
            activity.eventText  = "Some event just occurred. Do something here"
            activity.save()
            params.id = activity.id
            controller.getCommentLikeUsers()

        then:
            response.text == "mock data"
    }

    void "test saveNewActivity"() {
        UserAccount user =  new UserAccount(username: "someuser", password: "somePassword").save()
        springSecurityService.metaClass.principal = [id: user.id]

        activityService.metaClass.saveActivityForManualPost = { UserAccount userAccount, String eventTitle, String eventText ->
            return true
        }

        controller.springSecurityService = springSecurityService
        controller.activityService = activityService

        when:
            controller.saveNewActivity()

        then:
            !response.json.success
            response.json.messages.danger

        when:
            response.reset()
            params.title   = "My custom title"
            params.message = "Message here"
            controller.saveNewActivity()

        then:
            response.json.success
    }
}
