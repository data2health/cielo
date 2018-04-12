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
    SortedSet<Comment>      comments        = new TreeSet<Comment>()
    SortedSet<UserAccount>  likedByUsers    = new TreeSet<UserAccount>()
    String    activityInitiatorUserName

    static hasMany = [likedByUsers: UserAccount]

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

    void addTolikedByUsers(UserAccount user) {
        likedByUsers.add(user)
    }

    void removeFromlikedByUsers(UserAccount user) {
        likedByUsers.remove(user)
    }

    SortedSet<UserAccount> getMostRecentLikedByUsers(int count) {
        likedByUsers.take(count)
    }
}
