package edu.wustl.cielo

import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString
import edu.wustl.cielo.enums.ActivityTypeEnum

@GrailsCompileStatic
@ToString(includePackage = false, includeNames = true, includeFields = true)
class Activity implements Comparable {

    String  eventTitle
    String  eventText
    Date    dateCreated
    ActivityTypeEnum  eventType
    SortedSet<Comment> comments = new TreeSet<Comment>()
    String    activityInitiatorUserName

    static constraints = {
        eventType(nullable: false)
        eventTitle(nullable: false)
        eventText(nullable: false)
        activityInitiatorUserName(nullable: false)
    }

    static mapping = {
        eventText sqlType: 'text'
    }

    int compareTo(Object o) {
        if (dateCreated && ((Activity)o).dateCreated) dateCreated <=> ((Activity)o).dateCreated
    }


    UserAccount getUser() {
        UserAccount.findByUsername(activityInitiatorUserName)
    }
}
