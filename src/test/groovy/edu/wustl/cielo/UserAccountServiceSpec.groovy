package edu.wustl.cielo

import edu.wustl.cielo.enums.AccountStatusEnum
import edu.wustl.cielo.enums.UserRolesEnum
import grails.testing.gorm.DomainUnitTest
import grails.testing.services.ServiceUnitTest
import org.springframework.core.io.ByteArrayResource
import spock.lang.Specification

class UserAccountServiceSpec extends Specification implements ServiceUnitTest<UserAccountService>, DomainUnitTest<UserAccount> {

    def webRoot
    def assetsRoot

    def setup() {
        mockDomains(Institution, Profile, UserRole, UserAccountUserRole, RegistrationCode)
        webRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/src/main/webapp/"
        assetsRoot = "/Users/rickyrodriguez/Documents/IdeaProjects/cielo/grails-app/assets"

        service.assetResourceLocator = [findAssetForURI: { String URI ->
            new ByteArrayResource(new File(assetsRoot + "/images/${URI}").bytes)
        }]

    }

    void "test handleOnAuthSuccess"() {
        given: "No users"
        UserAccount.list() == []

        when: "calling to handle auth has no effect"
        service.springSecurityService = [principal: null]
        service.handleOnAuthSuccess("")

        then: "nothing happens"
        true == true

        when: "adding a user and calling handle auth"
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somepass").save()
        service.springSecurityService = [principal: userAccount]
        service.handleOnAuthSuccess("someuser")

        then: "check the user object"
        userAccount.status != AccountStatusEnum.ACCOUNT_ACTIVE

        when: "user account has been verified"
        userAccount.status = AccountStatusEnum.ACCOUNT_VERIFIED
        service.handleOnAuthSuccess("someuser")

        then: "then user can be set to active"
        userAccount.status == AccountStatusEnum.ACCOUNT_ACTIVE
    }

    void "test handleBadCredentials"() {
        given: "No users"
        UserAccount.list() == []

        when: "calling handle bad credentials"
        service.handleBadCredentials("")

        then: "nothing happens"
        true == true

        when: "adding a user then calling it"
        UserAccount userAccount = new UserAccount(username: "someuser", password: "somepass").save()
        userAccount.failedAttempts == 0
        service.handleBadCredentials(userAccount.username)

        then: "the user account will have a failed login"
        userAccount.failedAttempts == 1

        when: "try it again"
        service.handleBadCredentials(userAccount.username)

        then: "one more failed attempt"
        userAccount.failedAttempts == 2
    }


    void "test findUser"() {
        UserAccount userAccount
        UserAccount foundUser

        when: "trying to find a user before saved"
        foundUser = service.findUser("someuser")

        then: "the user account is not found"
        foundUser == null

        when: "adding a user"
        userAccount = new UserAccount(username: "someuser", password: "somepass").save()
        foundUser = service.findUser("someuser")

        then: "found user is the one just created"
        foundUser == userAccount
    }

    void "test markUserFailedLogin"() {
        UserAccount userAccount

        when: "marking a null user"
        service.markUserFailedLogin(null)

        then: "Nothing happens"
        true == true

        when: "creating a user and marking failed login"
        userAccount = new UserAccount(username: "someuser", password: "somepass").save()
        userAccount.failedAttempts == 0
        service.markUserFailedLogin(userAccount)

        then: "someuser has a failed login"
        userAccount.failedAttempts == 1
    }

    void "test bootstrapUserRoles"() {
        given: "no Roles"
        UserRole.list() == []

        when: "bootstrapping roles"
        service.bootstrapUserRoles()

        then: "some roles are created"
        UserRole.list() != []
        UserRole.count() > 1

    }

    void "test bootstrapCreateOrGetAdminAccount and bootstrapAddSuperUserRoleToUser in one test"() {
        UserAccount admin

        given: "no users"
        UserAccount.list() == []

        when: "bootstrapping admin user"
        admin = service.bootstrapCreateOrGetAdminAccount()

        then: "user is created but no roles associated yet so not an admin"
        UserAccount.list() != [] && UserAccount.count() == 1
        !admin.getAuthorities().contains(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))

        when: "associating admin role"
        service.bootstrapUserRoles()
        service.bootstrapAddSuperUserRoleToUser(admin)

        then: "now an admin"
        UserAccount.list() != [] && UserAccount.count() == 1
        admin.getAuthorities().contains(UserRole.findByAuthority(UserRolesEnum.ROLE_SUPERUSER.name()))

    }

    void "test setupMockAppUsers"() {

        given: "no users"
        UserAccount.list() == []

        when: "setting up mock users"
        InstitutionService institutionService = new InstitutionService()
        institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
        AnnotationService annotationService = new AnnotationService()
        annotationService.initializeAnnotations(new File(webRoot + "WEB-INF/startup/shorter_mshd2014.txt"))
        service.bootstrapUserRoles()
        service.bootstrapAddSuperUserRoleToUser(service.bootstrapCreateOrGetAdminAccount())
        service.setupMockAppUsers(2, 0)

        then:
        UserAccount.count() == 3
        UserAccount.list().each { user -> user.profile.annotations == [] }

        when: "adding a few more with annotations"
        UserAccount.list().each { it.delete() }
        service.setupMockAppUsers(4, 3)

        then:
        UserAccount.count() == 4
        UserAccount.list().eachWithIndex { user, index ->
            assert user.profile.annotations.size() == 3
        }
    }

    void "test bootstrapFollowers"() {
        given: "no users"
        UserAccount.list() == []

        when: "setting up mock users"
        InstitutionService institutionService = new InstitutionService()
        institutionService.setupMockInstitutions(new File(webRoot + "WEB-INF/startup/intsitutions.json"))
        AnnotationService annotationService = new AnnotationService()
        annotationService.initializeAnnotations(new File(webRoot + "WEB-INF/startup/shorter_mshd2014.txt"))
        service.bootstrapUserRoles()
        service.bootstrapAddSuperUserRoleToUser(service.bootstrapCreateOrGetAdminAccount())
        service.setupMockAppUsers(2, 0)

        then:
        UserAccount.count() == 3 //2 + 1 admin
        UserAccount.list().each { user -> user.profile.annotations == [] }

        when: "adding a few more with annotations"
        UserAccount.list().each { it.delete() }
        service.setupMockAppUsers(4, 3)

        then:
        UserAccount.count() == 4
        UserAccount.list().eachWithIndex { user, index ->
            assert user.profile.annotations.size() == 3
        }

        when: "adding followers to the users"
        service.bootstrapFollowers(2)

        then: "each user should have two followers"
        UserAccount.list().eachWithIndex { user, index ->
            assert user.profile.annotations.size() == 3
            assert user.connections.size() == 2
        }
    }

    void "test getUsersFollowing"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword")
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword")

        when:
        def results = service.getUsersFollowing(user)

        then:
        results.size() == 0

        when:
        user.connections.add(user2)
        user.save()
        results = service.getUsersFollowing(user)

        then:
        results.size() == 1
        results.contains(user2)

    }

    void "test getUsersFollowingMe"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword")
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword")

        when:
        def results = service.getUsersFollowingMe(user)

        then:
        results.size() == 0

        when:
        user2.connections.add(user)
        user2.save()
        results = service.getUsersFollowingMe(user)

        then:
        results.size() == 1
        results.contains(user2)
    }

    void "test updateConnections"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword")
        UserAccount user2 = new UserAccount(username: "someuser2", password: "somePassword")

        when:
        def results = service.getUsersFollowing(user)

        then:
        results.size() == 0

        when:
        def results2 = service.updateConnections(user, [user2.id])

        then:
        results2
    }

    void "test registerUser"() {
        UserAccount user = new UserAccount(username: "someuser", password: "somePassword")
        Profile profile  = new Profile(user: user, institution: new Institution(), firstName: "Ricky", lastName: "Rodriguez",
                emailAddress: "ricardo.rodriguez@wustl.edu")

        when:
            boolean succeeded = service.registerUser(user, profile, "University of Kentucky",
                    "UK")

        then:
            succeeded
            user.id
            user.profile.institution.id
            user.profile.institution.fullName == "University of Kentucky"
    }
}
