package edu.wustl.cielo

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class ActivityIntegrationSpec extends Specification {

    void cleanup() {
        Activity.list().each {
            it.delete()
        }
    }

    void "test saving"() {

        expect: "no activities"
            Activity.count() == 0

        when: "attempting to save one"
            new Activity(description: "some text here").save()

        then:
            Activity.count() == 0

        when: "adding activity with the proper params"
            UserAccount user = new UserAccount(username: "someuser", password: "somePassword").save()
            Activity activity = new Activity(description: "Some description", user: user, linkToActivityItem: new URL("https://cd2h.cielo.wustl.edu:8443/login/auth"))

        then: "save is successful"
            activity.save()
    }
}
