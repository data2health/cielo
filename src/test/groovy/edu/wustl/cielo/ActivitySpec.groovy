package edu.wustl.cielo

import edu.wustl.cielo.enums.ActivityTypeEnum
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ActivitySpec extends Specification implements DomainUnitTest<Activity> {

    void "test saving activity"() {
        Activity activity
        UserAccount user

        when: "activity with no user nor description"
            activity = new Activity(description: null, user: null)

        then: "the save fails"
            !activity.save()

        when: "description is not null but user is"
            activity = new Activity(description: "Something", user: null)

        then: "the save fails"
            !activity.save()

        when: "create a user and assign to activity but leave description null"
            user = new UserAccount(username: "someuser", password: "somePassword").save()
            activity = new Activity(description: null, user: user)

        then: "the save fails"
            !activity.save()

        when: "user and description not null"
            activity = new Activity(eventTitle: "Some description", activityInitiatorUserName: user.username,
                    eventType: ActivityTypeEnum.ACTIVITY_NEW_PROJECT,
                    eventText: new URL("https://cd2h.cielo.wustl.edu:8443/login/auth").toString())

        then: "the save is successful"
            activity.save()

        when: "liking an activity"
            activity.likedByUsers.add(user)
            activity.save()

        then:
            activity.likedByUsers.size() == 1
    }
}
