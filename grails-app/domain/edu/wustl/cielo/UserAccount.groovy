package edu.wustl.cielo

import edu.wustl.cielo.enums.AccountStatusEnum
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class UserAccount implements Serializable, Comparable {

    private static final long serialVersionUID = 1

    int failedAttempts = 0
    String username
    String password
    String timezoneId = TimeZone.default.getID()
    Date lastLogin
    Date dateCreated
    Date lastUpdated
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked = true
    boolean passwordExpired
    AccountStatusEnum status    = AccountStatusEnum.ACCOUNT_UNVERIFIED
    Set<UserAccount> connections    = []
    List<Project> projects          = []

    static hasMany = [connections: UserAccount, projects: Project]

    static constraints = {
        username(nullable: false, blank: false, unique: true)
        password(nullable: false, blank: false, password: true)
        lastLogin(nullable: true)
        timezoneId(nullable: false)
    }

    int compareTo(Object account) {
        ((UserAccount)account)?.dateCreated <=> dateCreated
    }

    Set<UserAccountUserRole> getAuthorities() {
        (UserAccountUserRole.findAllByUserAccount(this) as List<UserAccountUserRole>)*.userRole as Set<UserAccountUserRole>
    }

    //only non-null after creating user before validating via link in registration email
    RegistrationCode getRegistrationCode() {
        RegistrationCode.findByUserAccount(this)
    }

    Profile getProfile() {
        Profile.findByUser(this)
    }

    String getFullName() {
        Profile userProfile = Profile.findByUser(this)
        return "${userProfile.firstName} ${userProfile.lastName}"
    }

    Set<UserAccount> getFollowers() {

        Set<UserAccount> listOfFollowers = []

        list().each { UserAccount user ->
            if (user.connections.contains(this)) listOfFollowers.add(user)
        }

        return listOfFollowers
    }
}
